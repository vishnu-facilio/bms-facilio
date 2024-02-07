<script>
import {
  isObject,
  isEmpty,
  isBoolean,
  isNullOrUndefined,
  isFunction,
} from '@facilio/utils/validation'
import { getFieldValue } from 'util/picklist'
import { getDisplayValue } from 'util/field-utils'
import {
  isBooleanField,
  isSystemEnumField,
  isMultiEnumField,
  isEnumField,
  isDateField,
  isDateTimeField,
  isLookupSimple,
  isLookupPopup,
  isSiteField,
  isGeoLocationField,
  isSpecialEnumField,
} from '@facilio/utils/field'
import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'
import FetchViewsMixin from '@/base/FetchViewsMixin'

const lookupModulesRouterMap = {
  workorder: (id, viewname) => {
    return { name: 'wosummarynew', params: { id, viewname } }
  },
  ticket: (id, viewname) => {
    return { name: 'wosummarynew', params: { id, viewname } }
  },
  asset: (id, viewname) => {
    return { path: `/app/at/assets/${viewname}/${id}/overview` }
  },
  alarm: (id, viewname) => {
    return { path: `/app/fa/faults/${viewname}/newsummary/${id}` }
  },
  vendors: (id, viewname) => {
    return { path: `/app/vendor/vendors/${viewname}/summary/${id}` }
  },
  purchaseorder: (id, viewname) => {
    return { name: 'poSummary', params: { id, viewname } }
  },
  tenant: (id, viewname) => {
    return { name: 'tenantSummary', params: { id, viewname } }
  },
  tenantunit: (id, viewname) => {
    return { name: 'tenantUnitSummary', params: { id, viewname } }
  },

  custom: (id, viewname, moduleName) => {
    return {
      name: 'custommodules-summary',
      params: { moduleName, viewname, id },
    }
  },
}

const lookUpSpecialSupportModules = [
  'alarm',
  'workorder',
  'ticket',
  'asset',
  'vendors',
  'purchaseorder',
  'tenant',
  'tenantunit',
]

export default {
  data() {
    return {
      isLookupSimple,
      exportDownloadUrl: {},
      lookUpSpecialSupportModules,
    }
  },
  mixins: [FetchViewsMixin],
  methods: {
    isLookUpRedirectApplicable(field) {
      let { displayTypeEnum, lookupModuleName, field: fieldObj } = field || {}
      let { lookupModule } = fieldObj || {}
      let { custom, typeEnum } = lookupModule || {}

      return (
        displayTypeEnum === 'LOOKUP_SIMPLE' &&
        lookupModuleName &&
        typeEnum === 'BASE_ENTITY' &&
        (this.lookUpSpecialSupportModules.includes(lookupModuleName) ||
          custom ||
          isWebTabsEnabled())
      )
    },
    async redirectToLookUpFieldSummary(lookUpField) {
      let { lookupModuleName, name, field } = lookUpField || {}
      let { details, isV3Api } = this
      let { data } = details || {}
      let { lookupModule } = field || {}
      let { custom } = lookupModule || {}
      let viewname = await this.fetchView(lookupModuleName)
      let { id } =
        !isV3Api && !(field || {}).default
          ? (data || {})[name]
          : (details || {})[name]

      if (isEmpty(id)) return

      if (isWebTabsEnabled()) {
        let { name: routeName } =
          findRouteForModule(lookupModuleName, pageTypes.OVERVIEW) || {}

        if (routeName) {
          this.$router.push({
            name: routeName,
            params: { viewname, id },
          })
        }
      } else {
        if (custom) {
          this.$router.push(
            lookupModulesRouterMap['custom'](id, viewname, lookupModuleName)
          )
        } else {
          this.$router.push(
            lookupModulesRouterMap[lookupModuleName](id, viewname)
          )
        }
      }
    },
    isFileField(field) {
      return field.displayTypeEnum === 'FILE'
    },
    isCustomField(field) {
      return !this.$getProperty(field, 'field.default', false)
    },
    getFileName(field) {
      let { details, isV3Api } = this
      let record
      if (isV3Api) {
        record = details
      } else {
        let { data } = details || {}
        record = this.isCustomField(field) ? data : details
      }
      if (!isEmpty(record)) {
        let filename = record[`${field.name}FileName`]
        if (!isEmpty(filename)) {
          return filename
        }
      }
      return '---'
    },
    downloadAttachment(field) {
      let { details, isV3Api } = this
      let url
      let record
      if (isV3Api) {
        record = details
      } else {
        let { data } = details || {}
        record = this.isCustomField(field) ? data : details
      }
      if (!isEmpty(record)) {
        let filename = record[`${field.name}FileName`]
        if (!isEmpty(filename)) {
          url = record[`${field.name}DownloadUrl`]
          if (!isEmpty(this.exportDownloadUrl[field.name])) {
            this.exportDownloadUrl = { [field.name]: null }
            this.$nextTick(() => {
              this.exportDownloadUrl = { [field.name]: url }
            })
          } else {
            this.exportDownloadUrl = { [field.name]: url }
          }
        }
      }
    },
    getFormattedDisplayName(fieldObj = {}) {
      let { field, displayName: formFieldDisplayName } = fieldObj
      let fieldDisplayName = this.$getProperty(field, 'displayName')

      if (this.$org.id === 1) return formFieldDisplayName || fieldDisplayName //ICD wants its fieldLabel to be formFieldDisplaName
      return fieldDisplayName || formFieldDisplayName
    },
    getFormattedValue(fieldObj, lookupObj, sites) {
      return new Promise(resolve => {
        if (!lookupObj) lookupObj = {}

        let { field, lookupModuleName } = fieldObj
        let { lookupModule = {} } = field || {}

        let value
        if (lookupObj && field) {
          value = !isEmpty(lookupObj[field.name]) ? lookupObj[field.name] : null
        } else if (lookupObj) {
          value = !isEmpty(lookupObj[fieldObj.name])
            ? lookupObj[fieldObj.name]
            : null
        }

        if ((field && isSiteField(field)) || isSiteField(fieldObj)) {
          let site = sites[value]
          value = site && site.label ? site.label : '---'
          resolve(value)
        } else if (
          !isEmpty(field) &&
          (isDateField(field) || isDateTimeField(field))
        ) {
          resolve(value > 0 ? getDisplayValue(field, value) : '---')
        } else if (!isEmpty(field) && isSpecialEnumField(field)) {
          resolve(value ? getDisplayValue(field, value) : '---')
        } else if (
          !isEmpty(field) &&
          (isEnumField(field) || isSystemEnumField(field))
        ) {
          resolve(value ? getDisplayValue(field, value) : '---')
        } else if (!isEmpty(field) && isMultiEnumField(field)) {
          resolve(this.getMultiEnumFieldValues(field, value))
        } else if (!isEmpty(field) && isBooleanField(field)) {
          resolve(isBoolean(value) ? getDisplayValue(field, value) : '---')
        } else if (!isEmpty(field) && isGeoLocationField(fieldObj)) {
          resolve(value ? getDisplayValue(fieldObj, value) : '')
        } else if (
          (field && field.displayType === 'DURATION') ||
          fieldObj.displayTypeEnum === 'DURATION'
        ) {
          resolve(
            this.$helpers.getFormattedDuration(
              value,
              !isEmpty(field.unit) ? field.unit : 's'
            )
          )
        } else if (!isEmpty(field) && [2, 3].includes(field.dataType)) {
          let { field } = fieldObj
          let { unit } = field || {}

          if (this.isV3Api ? !isNullOrUndefined(value) : !isEmpty(value)) {
            if (!isEmpty(unit)) {
              if (['$', '₹'].includes(unit)) {
                value = ` ${unit} ${value}`
              } else {
                value = `${value} ${unit}`
              }
            } else {
              value = `${value}`
            }
          } else {
            value = '---'
          }

          resolve(value)
        } else if ((field || {}).dataTypeEnum === 'MULTI_LOOKUP') {
          resolve(this.getMultiLookupValue(value))
        } else if ((field || {}).dataTypeEnum === 'STRING_SYSTEM_ENUM') {
          resolve(value ? getDisplayValue(field, value) : '---')
        } else if (
          value &&
          ((lookupModule && lookupModule.name === 'basespace') ||
            lookupModuleName === 'basespace')
        ) {
          let spaceName = this.$getProperty(value, 'name')
          if (isEmpty(spaceName)) {
            spaceName = this.space[value.id] ? this.space[value.id] : '---'
          }
          resolve(spaceName)
        } else if (
          (lookupModule &&
            ['users', 'requester'].includes(lookupModule.name)) ||
          ['users', 'requester'].includes(lookupModuleName)
        ) {
          let userId = (value || {}).id
          let userName = this.$getProperty(value, 'name')
          if (!isEmpty(userName)) {
            resolve(userName)
          } else if (userId) {
            let { name = '---' } = this.$store.getters.getUser(userId) || {}
            resolve(name)
          } else {
            resolve('---')
          }
        } else if (
          field &&
          lookupModule.name === 'ticket' &&
          this.$getProperty(field.module, 'name', '') === 'workpermit'
        ) {
          resolve(
            !isEmpty((value || {}).serialNumber)
              ? `#${value.serialNumber}`
              : '---'
          )
        } else if (
          ((isLookupSimple(fieldObj) || isLookupPopup(fieldObj)) &&
            this.$getProperty(lookupObj, fieldObj.name, null) &&
            isObject(lookupObj[fieldObj.name]) &&
            this.$getProperty(
              lookupObj,
              `${fieldObj.name}.primaryValue`,
              null
            )) ||
          (field &&
            ['LOOKUP_SIMPLE', 'LOOKUP_POPUP'].includes(field.displayType) &&
            this.$getProperty(lookupObj, field.name, null) &&
            isObject(lookupObj[field.name]) &&
            this.$getProperty(lookupObj, `${field.name}.primaryValue`, null))
        ) {
          // Custom modules lookup fields
          let name = (field && field.name) || fieldObj.name

          resolve(
            lookupObj[name].primaryValue ? lookupObj[name].primaryValue : '---'
          )
        } else if (
          value &&
          (((isLookupSimple(fieldObj) || isLookupPopup(fieldObj)) &&
            fieldObj.lookupModuleName) ||
            (field &&
              ['LOOKUP_SIMPLE', 'LOOKUP_POPUP'].includes(field.displayType) &&
              this.$getProperty(field.lookupModule, 'name', '')))
        ) {
          let isRotating = ['rotatingItem', 'rotatingTool'].includes(field.name)
          if (!isRotating) {
            let lookupModuleName =
              (field && this.$getProperty(field.lookupModule, 'name', '')) ||
              fieldObj.lookupModuleName
            getFieldValue({
              lookupModuleName,
              selectedOptionId: [value.id],
            }).then(({ error, data }) => {
              let value
              if (!error) {
                value = this.$getProperty(data, '0.label')
              }
              resolve(value ? value : '---')
            })
          } else resolve(value || '---')
        } else if ((field || {}).name === 'resource') {
          resolve((value || {}).name ? value.name : '---')
        } else if (
          this.isFileField(fieldObj) ||
          this.$getProperty(fieldObj, 'field.displayType') === 'SIGNATURE'
        ) {
          resolve(this.getFileName(fieldObj))
        } else {
          resolve(value || '---')
        }
      })
    },
    getMultiLookupValue(value) {
      let lookupRecordNames = (value || []).map(
        currRecord =>
          currRecord.displayName || currRecord.name || currRecord.subject
      )
      if (lookupRecordNames.length > 5) {
        return `${lookupRecordNames.slice(0, 5).join(', ')} +${Math.abs(
          lookupRecordNames.length - 5
        )}`
      } else {
        return !isEmpty(lookupRecordNames)
          ? `${lookupRecordNames.join(', ')}`
          : '---'
      }
    },
    getSysFieldValue(field) {
      let value
      let { details } = this
      if (
        field.name === 'sysModifiedBy' ||
        field.name === 'sysCreatedBy' ||
        field.name === 'lastIssuedToUser'
      ) {
        let userId = (details[field.name] || {}).id
        let userName = this.$getProperty(details[field.name], 'name')
        if (!isEmpty(userName)) {
          return userName
        } else if (userId) {
          let { name = '---' } = this.$store.getters.getUser(userId) || {}
          return name
        }
      } else if (
        field.name === 'sysCreatedTime' ||
        field.name === 'sysModifiedTime' ||
        field.name === 'lastIssuedTime'
      ) {
        value = details[field.name]
        if (isEmpty(value)) {
          return '---'
        }
        let timePerdiod = this.$options.filters.formatDate(value)
        if (!isEmpty(timePerdiod)) {
          return timePerdiod
        }
      } else {
        value = details[field.name]
        return value || '---'
      }
      return '---'
    },
    getMultiEnumFieldValues(field, values = []) {
      let { enumMap } = field

      let valueStr = (values || []).reduce((accStr, value) => {
        let str = enumMap[value] || ''
        return isEmpty(accStr) ? `${str}` : `${accStr}, ${str}`
      }, '')

      return isEmpty(valueStr) ? '---' : valueStr
    },
    toggleVisibility(offsetHeight) {
      this.isAllVisible = !this.isAllVisible

      if (this.isAllVisible) {
        this.$nextTick(() => {
          let height =
            this.$refs['content-container'].scrollHeight + (offsetHeight || 90)
          let width = this.$refs['content-container'].scrollWidth
          this.resizeWidget({ height, width })
        })
      } else {
        this.$nextTick(() => this.resizeWidget({ h: this.defaultWidgetHeight }))
      }
    },
    autoResize() {
      this.$nextTick(() => {
        let container = this.$refs['content-container']
        if (!container) return

        let height = this.$refs['content-container'].scrollHeight + 60
        let width = this.$refs['content-container'].scrollWidth
        let dimensions
        if (isFunction(this.calculateDimensions)) {
          dimensions = this.calculateDimensions({ height, width })
        }

        if (isEmpty(dimensions)) return
        let { h } = dimensions || {}
        let params = {}
        if (h <= this.defaultWidgetHeight) {
          this.needsShowMore = false
          params = { height, width }
        } else {
          let { defaultWidgetHeight = 7 } = this
          this.needsShowMore = h > defaultWidgetHeight ? true : false
          this.defaultWidgetHeight = defaultWidgetHeight
          this.needsShowMore
            ? (params = { h: defaultWidgetHeight })
            : (params = { h: h })
        }
        this.resizeWidget(params)
      })
    },
  },
}
</script>
