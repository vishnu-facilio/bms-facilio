import { mapState, mapGetters } from 'vuex'
import {
  isObject,
  isEmpty,
  isArray,
  isNullOrUndefined,
} from '@facilio/utils/validation'
import { isNumberField, isDecimalField } from '@facilio/utils/field'
import { getColumnConfig } from 'src/newapp/components/column-customization/columnConfigUtil.js'
import { getFormatedTime } from '@/mixins/TimeFormatMixin'

export default {
  computed: {
    ...mapState({
      metaInfo: state => state.view.metaInfo,
      sites: state => state.site,
      buildings: state => state.buildings,
    }),
    ...mapGetters([
      'getUser',
      'getTicketStatus',
      'getAssetDepartment',
      'getAssetType',
      'getAssetCategory',
      'getAlarmSeverity',
    ]),
    viewColumns() {
      let columns = []
      let viewObject = this.viewDetail
        ? this.viewDetail
        : this.$store.state.view.currentViewDetail

      if (viewObject && viewObject.fields) {
        columns = viewObject.fields.map(vf => {
          vf.name = (vf.field || {}).name || vf.fieldName
          vf.fieldId = (vf.field || {}).fieldId
          let defaultColumnName =
            viewObject.defaultModuleFields &&
            viewObject.defaultModuleFields[vf.name]
              ? viewObject.defaultModuleFields[vf.name].columnDisplayName
              : null
          vf.displayName =
            vf?.columnDisplayName || defaultColumnName || vf?.field?.displayName
          return vf
        })
      }
      return columns
    },
    includeParentFilter() {
      return (
        this.$route.query.includeParentFilter &&
        (this.$route.query.includeParentFilter === 'true' ||
          this.$route.query.includeParentFilter === true)
      )
    },
  },
  created() {
    this.$store.dispatch('loadSite')
    this.$store.dispatch('loadTicketStatus', 'workorder')
    this.$store.dispatch('loadAssetCategory')
    this.$store.dispatch('loadAssetDepartment')
    this.$store.dispatch('loadAssetType')
    this.$store.dispatch('loadAlarmSeverity')
    this.$store.dispatch('loadInventoryCategory')
    if (this.$account.org.orgId !== 418) this.$store.dispatch('loadBuildings')
  },
  methods: {
    getColumnDisplayValue(viewField, record) {
      let value
      let { isV3Api, $getProperty, moduleName } = this
      if (!isEmpty(viewField.field)) {
        let field = viewField.field
        let parentFieldName = viewField.parentField
          ? viewField.parentField.name
          : ''
        let isLookup =
          field.dataTypeEnum === 'LOOKUP' ||
          field.dataTypeEnum._name === 'LOOKUP'
        let isEnum =
          field.dataTypeEnum === 'ENUM' ||
          field.dataTypeEnum._name === 'ENUM' ||
          field.dataTypeEnum === 'SYSTEM_ENUM' ||
          field.dataTypeEnum._name === 'SYSTEM_ENUM'
        let isMulitEnum =
          field.dataTypeEnum === 'MULTI_ENUM' ||
          field.dataTypeEnum._name === 'MULTI_ENUM'
        let isBoolean =
          field.dataTypeEnum === 'BOOLEAN' ||
          field.dataTypeEnum._name === 'BOOLEAN'
        let isTimeField =
          (field.dataTypeEnum === 'NUMBER' ||
            field.dataTypeEnum._name === 'NUMBER') &&
          field.displayType === 'TIME'

        record =
          !isV3Api &&
          (!field.default ||
            (viewField.parentField && !viewField.parentField.default))
            ? record.data
            : record
        if (parentFieldName) {
          record =
            !isV3Api && !field.default && record[parentFieldName]
              ? record[parentFieldName].data
              : record[parentFieldName]
        }
        if (!record) {
          record = {}
        }
        if (field.name === 'assignedTo') {
          value = record.assignmentGroup ? record.assignmentGroup.name : '---'
          value += ' / ' + (record.assignedTo ? record.assignedTo.name : '---')
        } else if (field.name === 'contact') {
          value = record.contact ? record.contact.name : '---'
        } else if (field.name === 'space') {
          if (record.space || record.asset) {
            value =
              record.space && record.space.id && record.space.id !== -1
                ? record.space.name
                : record.asset.name
          }
        } else if (field.name === 'resource') {
          value = record.resource ? record.resource.name : ''
        } else if (field.name === 'tenant') {
          let primaryValue = $getProperty(record, 'tenant.primaryValue', '')
          let name = $getProperty(record, 'tenant.name', '')
          if (!isEmpty(name)) {
            value = name
          } else if (!isEmpty(primaryValue)) {
            value = primaryValue
          } else {
            value = '---'
          }
        } else if (
          field.name === 'severity' ||
          field.name === 'previousSeverity'
        ) {
          value = record[field.name]
            ? this.getAlarmSeverity(record[field.name].id).displayName
            : ''
        } else if (field.name === 'alarmType') {
          value = record.alarmTypeVal
        } else if (field.name === 'acknowledgedBy') {
          value = record.acknowledgedBy
            ? this.getUser(record.acknowledgedBy.id).name
            : ''
        } else if (['spaceType'].includes(field.name)) {
          value = record[field.name] > 0 ? record[`${field.name}Val`] : '---'
        } else if (isLookup && field.specialType) {
          if (field.specialType === 'users') {
            let { name: fieldName } = field
            let fieldValue = record[fieldName] || {}
            let userName = fieldValue.name
            if (isEmpty(fieldValue.name) && !isEmpty(fieldValue.id)) {
              userName = (this.getUser(fieldValue.id) || {}).name
            }
            value = userName || '---'
          } else {
            value = (record[field.name] || {}).id
              ? record[field.name].name
              : '---'
          }
        } else if (field.name === 'zone') {
          value = record.zone ? record.zone.name : ''
        } else if (
          isLookup &&
          (parentFieldName === 'asset' ||
            (field.module && field.module.name === 'asset'))
        ) {
          let id =
            parentFieldName === 'asset'
              ? record.asset && record.asset[field.name]
                ? record.asset[field.name].id
                : -1
              : record[field.name]
              ? record[field.name].id
              : -1
          if (id !== -1) {
            // TODO get based on lookupModuleName from account
            if (field.name === 'type') {
              value = this.getAssetType(id).name
            } else if (field.name === 'category') {
              value = this.getAssetCategory(id).displayName
            } else if (field.name === 'moduleState') {
              value = (this.getTicketStatus(id, 'asset') || {}).displayName
            } else if (field.name === 'department') {
              value = this.getAssetDepartment(id).name
            } else if (field.name === 'rotatingItem') {
              value = record[field.name]
                ? record[field.name].itemType.name
                : '---'
            } else if (field.name === 'rotatingTool') {
              value = record[field.name]
                ? record[field.name].toolType.name
                : '---'
            } else if (field.name === 'purchaseOrder') {
              value = record[field.name] ? '#' + record[field.name].id : '---'
            } else if (
              isLookup &&
              record[field.name] &&
              record[field.name].primaryValue
            ) {
              value = record[field.name].primaryValue
            }
          }
        } else if (isLookup && record[field.name] && record[field.name].name) {
          return record[field.name].name
        } else if (
          isLookup &&
          record[field.name] &&
          record[field.name].primaryValue
        ) {
          return record[field.name].primaryValue
        } else if (
          isLookup &&
          (parentFieldName === 'space' ||
            (field.module && field.module.name === 'basespace'))
        ) {
          if (record[field.name]) {
            // TODO get based on lookupModuleName from account
            if (
              field.name === 'site' &&
              record[field.name] &&
              record[field.name].id
            ) {
              if (this.sites) {
                let s = this.sites.find(i => i.id === record[field.name].id)
                if (!s) {
                  return '---'
                }
                return s.name
              }
              return '---'
            } else if (
              field.name === 'building' &&
              record[field.name] &&
              record[field.name].id
            ) {
              if (this.buildings) {
                let s = this.buildings.find(i => i.id === record[field.name].id)
                if (!s) {
                  return '---'
                }
                return s.name
              }
              return '---'
            } else if (
              field.name === 'floor' &&
              field.name === 'space1' &&
              field.name === 'space2' &&
              field.name === 'space3' &&
              field.name === 'space4'
            ) {
              return '---'
            }
          } else {
            value = '---'
          }
        } else if (
          isLookup &&
          record[field.name] &&
          (field.module || {}).name === 'tenant' &&
          (field.lookupModule || {}).name === 'building'
        ) {
          if (isArray(this.buildings)) {
            let s = this.buildings.find(i => i.id === record[field.name].id)
            if (!s) {
              return '---'
            }
            return s.name
          }
        } else if (
          isLookup &&
          field.module &&
          (field.module.name === 'itemTypes' ||
            field.module.name === 'toolTypes')
        ) {
          let id = record[field.name] ? record[field.name].id : -1
          if (id !== -1) {
            if (
              field.name === 'category' &&
              this.$store.state.inventoryCategory.find(ic => ic.id === id)
            ) {
              value = this.$store.state.inventoryCategory.find(
                ic => ic.id === id
              ).displayName
            }
          }
        } else if (
          isLookup &&
          field.module &&
          field.module.name === 'storeRoom' &&
          field.name === 'site'
        ) {
          if (record[field.name]) {
            if (record[field.name] && record[field.name].id) {
              if (this.sites) {
                let s = this.sites.find(i => i.id === record[field.name].id)
                if (!s) {
                  return '---'
                }
                return s.name
              }
              return '---'
            }
          }
        } else if (isLookup && field.name === 'moduleState' && !isV3Api) {
          let { displayName, id = -1, primaryValue } = record[field.name] || {}

          if (primaryValue || displayName) {
            value = primaryValue || displayName
          } else {
            value = (this.getTicketStatus(id, field.module.name) || {})
              .displayName
          }
        } else if (!parentFieldName && isLookup) {
          if (
            record[field.name] &&
            typeof record[field.name] === 'object' &&
            (record[field.name].displayName || record[field.name].name)
          ) {
            value = record[field.name].displayName
              ? record[field.name].displayName
              : record[field.name].name
          } else if (
            record[field.name] &&
            isObject(record[field.name]) &&
            record[field.name].primaryValue
          ) {
            // Custom modules lookup fields
            value = record[field.name].primaryValue
          } else if (record[field.name] && field.name === 'User') {
            if (record[field.name].id) {
              return this.getUser(record[field.name].id).name
                ? this.getUser(record[field.name].id).name
                : ''
            } else {
              return '---'
            }
          } else if (
            isObject(record[field.name]) &&
            (field.module || {}).name === 'workpermit' &&
            field.name === 'ticket'
          ) {
            return record[field.name] ? record[field.name].subject : '---'
          }
        } else if (isEnum && field.enumMap) {
          value = record[field.name] ? field.enumMap[record[field.name]] : value
        } else if (isMulitEnum) {
          value = this.getMultiEnumValue({ field, record })
        } else if (isBoolean) {
          value = record[field.name]
            ? field.trueVal || 'Yes'
            : field.falseVal || 'No'
        } else if (isTimeField) {
          value = getFormatedTime(record[field.name]) || '---'
        } else if ((field || {}).dataTypeEnum === 'MULTI_LOOKUP') {
          let { name } = field
          value = this.getMultiLookupValue(record[name])
        } else {
          if (field.name === 'siteId') {
            let site = this.$store.getters.getSite(record[field.name])
            value = site && site.name ? site.name : '---'
          } else if (isNumberField(field) || isDecimalField(field)) {
            // if values is -1, we wont render client, have to check with server dev
            let recordValue = record[field.name]
            let isFloorModule =
              field.name === 'floorlevel' && field?.module?.name === 'floor'
            if (isFloorModule) {
              value = !isNullOrUndefined(recordValue) ? `${recordValue}` : '---'
            } else {
              value = !isEmpty(recordValue) ? `${recordValue}` : '---'
            }
          } else if (record[field.name] === -1) {
            value = ''
          } else {
            // temporary fix to show weather station name
            if (moduleName === 'weatherstation' && field.name === 'name') {
              if (!isEmpty(record.data)) value = record?.data[field?.name]
            } else value = record[field.name]
          }
        }

        if (
          (field.dataTypeEnum === 'FILE' ||
            field.dataTypeEnum._name === 'FILE') &&
          !field.default
        ) {
          let filename = record[`${field.name}FileName`]
          if (!isEmpty(filename)) {
            return filename
          }
          return '---'
        }

        if (
          (field.dataTypeEnum === 'DATE_TIME' ||
            field.dataTypeEnum._name === 'DATE_TIME') &&
          !isNaN(value)
        ) {
          return !value || value === -1
            ? ''
            : this.$options.filters.formatDate(value)
        } else if (
          (field.dataTypeEnum === 'DATE' ||
            field.dataTypeEnum._name === 'DATE') &&
          !isNaN(value)
        ) {
          return !value || value === -1
            ? ''
            : this.$options.filters.formatDate(value, true)
        } else if (field.displayTypeInt === 23) {
          return !isEmpty(value) && value !== '---'
            ? this.$helpers.getFormattedDuration(
                value,
                !isEmpty(field.unit) ? field.unit : 's'
              )
            : '---'
        } else if (field.name === 'currentQuantity') {
          return value ? value : '0'
        } else if (this.$constants.currencyFieldsList.includes(field.name)) {
          let val =
            !isEmpty(value) && value !== '---'
              ? this.$d3.format(',.2f')(value)
              : '0'
          return this.$currency === '$' || this.$currency === '₹'
            ? this.$currency + val
            : val + ' ' + this.$currency
        } else if (field.name === 'score') {
          return value && value > 0 ? value + '%' : '---'
        } else if (field.module.name === 'sensorrollupalarm') {
          if (
            field.name === 'readingFieldId' &&
            $getProperty(record, 'readingField.displayName')
          ) {
            value = $getProperty(record, 'readingField.displayName')
          }
        }

        if (field.module.name === 'bmsalarm') {
          if (field.name === 'condition') {
            value = record.data ? record.data.condition : '---'
          } else if (field.name === 'source') {
            value = record.data ? record.data.source : '---'
          }
        }

        return value
      } else {
        if (
          viewField.name === 'sysModifiedBy' ||
          viewField.name === 'sysCreatedBy'
        ) {
          let userId = (record[viewField.name] || {}).id
          let userName = $getProperty(record[viewField.name], 'name')
          if (!isEmpty(userName)) {
            return userName
          } else if (userId) {
            let { name = '---' } = this.getUser(userId) || {}
            return name
          }
        } else if (
          viewField.name === 'sysCreatedTime' ||
          viewField.name === 'sysModifiedTime'
        ) {
          value = record[viewField.name]
          let timePerdiod =
            value > 0 ? this.$options.filters.formatDate(value) : '---'
          if (!isEmpty(timePerdiod)) {
            return timePerdiod
          }
        } else if (viewField.name === 'siteId') {
          let site = this.$store.getters.getSite(record[viewField.name])
          return (site || {}).name ? site.name : '---'
        }
      }
    },
    canShowColumn(...names) {
      return this.viewColumns.find(field => names.indexOf(field.name) !== -1)
    },
    getMultiLookupValue(value) {
      let lookupRecordNames = (value || []).map(
        currRecord =>
          currRecord.displayName || currRecord.name || currRecord.subject
      )
      if (lookupRecordNames.length > 2) {
        return `${lookupRecordNames.slice(0, 2).join(', ')} +${Math.abs(
          lookupRecordNames.length - 2
        )}`
      } else {
        return !isEmpty(lookupRecordNames)
          ? `${lookupRecordNames.join(', ')}`
          : '---'
      }
    },
    isFixedColumn(name) {
      let { moduleName, columnConfig } = this
      let columnConfigObj = !isEmpty(columnConfig)
        ? columnConfig
        : getColumnConfig(moduleName)
      let { fixedColumns, fixedSelectableColumns } = columnConfigObj

      return (fixedColumns || [])
        .concat(fixedSelectableColumns || [])
        .includes(name)
    },
    getColumnHeaderLabel(column) {
      let { displayName, field } = column
      if (!isEmpty(field) && !isEmpty(field.unit)) {
        let { unit } = field
        return `${displayName} (${unit})`
      }
      return displayName
    },
    getMultiEnumValue(props) {
      let { field, record } = props
      let { name, enumMap } = field
      let values = record[name] || []
      let valueStr = values.reduce((accStr, value) => {
        let str = enumMap[value] || ''
        return isEmpty(accStr) ? `${str}` : `${accStr}, ${str}`
      }, '')
      return isEmpty(valueStr) ? '---' : valueStr
    },
    isMultiCurrencyField(field) {
      let { displayType } = field || {}
      return displayType === 'MULTI_CURRENCY'
    },
  },
}
