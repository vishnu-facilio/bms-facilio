<script>
import { isEmpty, isFunction, isObject } from '@facilio/utils/validation'
import { mapGetters, mapState } from 'vuex'
import SignatureField from '@/list/SignatureColumn'
import FilePreviewColumn from '@/list/FilePreviewColumn'
import { formatDate } from 'src/util/filters'
import { POSITION_TYPE } from 'pages/setup/custombutton/CustomButtonUtil'
import CustomButton from '@/custombutton/CustomButton'
import { getFieldOptions } from 'util/picklist'
import ConnectedAppViewWidget from 'pages/connectedapps/ConnectedAppViewWidget'
import $helpers from 'util/helpers'
import { API } from '@facilio/api'
import { getColumnConfig } from 'src/newapp/components/column-customization/columnConfigUtil'
import GlimpseLookupWrapper from 'src/newapp/components/GlimpseLookupWrapper.vue'
import { getFormatedTime } from '@/mixins/TimeFormatMixin.js'
import CurrencyPopOver from 'src/pages/setup/organizationSetting/currency/CurrencyPopOver.vue'

const DATA_TYPE_VALUE_HASH = {
  LOOKUP(field, data) {
    let { name: fieldName } = field

    let { primaryValue, name, displayName, subject } = data[fieldName] || {}
    let value = displayName || name || subject
    return !isEmpty(primaryValue) ? primaryValue : value
  },
  FILE(field, data) {
    let filename = data[`${field.name}FileName`]
    return filename
  },
  DATE(field, data) {
    let { name } = field || {}
    let value = data[name]

    return !value || value === -1 ? '' : formatDate(value, true)
  },
  DATE_TIME(field, data) {
    let { name } = field || {}
    let value = data[name]

    return !value || value === -1 ? '' : formatDate(value)
  },
  ENUM(field, data) {
    let { name } = field || {}
    let value = data[name]
    let fieldObj = field.field

    value = value ? fieldObj.enumMap[data[fieldObj.name]] : value
    return value
  },
  SYSTEM_ENUM(field, data) {
    let { name } = field || {}
    let value = data[name]
    let fieldObj = field.field

    value = value ? fieldObj.enumMap[data[fieldObj.name]] : value
    return value
  },
  MULTI_ENUM(field, data) {
    let { name, enumMap } = field.field || {}
    let values = data[name] || []
    let valueStr = values.reduce((accStr, value) => {
      let str = enumMap[value] || ''
      return isEmpty(accStr) ? `${str}` : `${accStr}, ${str}`
    }, '')
    return isEmpty(valueStr) ? '---' : valueStr
  },
  BOOLEAN(field, data) {
    let { name } = field || {}
    let { field: fieldObj } = field || {}
    let trueValue = 'Yes',
      falseValue = 'No'
    let { trueVal, falseVal } = fieldObj
    if (!isEmpty(trueVal) && !isEmpty(falseVal)) {
      trueValue = trueVal
      falseValue = falseVal
    }
    let value = data[name] ? trueValue : falseValue
    return value
  },
  MULTI_LOOKUP(field, data) {
    let { name } = field
    let value = data[name] || []
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
  DURATION(field, record) {
    let { name } = field
    let value = record[name] || []

    if (!isEmpty(value) && value !== '---') {
      let { unit } = field || {}
      unit = !isEmpty(unit) ? unit : 's'
      return $helpers.getFormattedDuration(value, unit)
    } else {
      return '---'
    }
  },
  OTHERS(field, data) {
    let { name } = field || {}
    if (isObject(data[name])) {
      let { name } = data[name] || {}

      if (!isEmpty(name)) return name
      else data[name]
    } else {
      return data[name]
    }
  },
}

const DISPLAY_TYPE_HASH = {
  LOOKUP_SIMPLE(field, record) {
    return (
      <GlimpseLookupWrapper
        field={field}
        record={record}
        hideGlimpse={this.hideGlimpse}
        recordModuleName={this.moduleName}
        updateSite={this.updateSite}
        siteList={this.sites}
      ></GlimpseLookupWrapper>
    )
  },
  SIGNATURE(field, record) {
    return (
      <SignatureField
        field={(field || {}).field}
        record={record}
        isV3Api={true}
      />
    )
  },
  FILE(field, record) {
    return <FilePreviewColumn field={field} record={record} isV2={false} />
  },
  DURATION(field, record) {
    let { field: fieldObj } = field || {}
    let modifiedField = {
      ...field,
      field: { ...fieldObj, dataTypeEnum: 'DURATION' },
    }
    return <div>{this.getColumnDisplayValue(modifiedField, record)}</div>
  },
  URL_FIELD(field, record) {
    let { name } = field || {}
    if (!isEmpty(record[name])) {
      let { name: displayName, href, target } = record[name] || {}
      let value = !isEmpty(displayName) ? displayName : href
      return (
        <el-tooltip effect="dark" content={value} placement="bottom-start">
          <a
            rel="nofollow"
            class="url-field-display truncate-text"
            referrerpolicy="no-referrer"
            href={href}
            target={target}
          >
            <span class="textoverflow-ellipsis">{value}</span>
          </a>
        </el-tooltip>
      )
    } else {
      return '---'
    }
  },
  CURRENCY(field, record) {
    let { name } = field || {}
    let {
      displaySymbol: baseSymbol,
      currencyCode: baseCode,
      multiCurrencyEnabled,
    } = this.multiCurrency
    if (!isEmpty(record[name])) {
      let {
        displaySymbol,
        currencyValue,
        baseCurrencyValue,
        exchangeRate,
        currencyCode,
      } = record[name] || {}
      let content = `${baseSymbol} 1 = ${displaySymbol} ${exchangeRate}`
      let baseValue = ''
      if (multiCurrencyEnabled && baseCode !== currencyCode)
        baseValue = `( ${baseSymbol} ${baseCurrencyValue} )`
      return (
        <div class="d-flex">
          <el-popover
            placement="right"
            title={this.$t('setup.currency.rate')}
            width="230"
            trigger="hover"
            content={content}
            open-delay={5}
            disabled={isEmpty(baseValue)}
          >
            <span slot="reference" class="pointer">
              {`${displaySymbol} ${currencyValue}`} {baseValue}
            </span>
          </el-popover>
        </div>
      )
    } else {
      return '---'
    }
  },
  MULTI_CURRENCY(field, record) {
    return (
      <CurrencyPopOver
        key={`${field.id}-${field.name}`}
        field={field}
        details={record}
      />
    )
  },
  TIME(field, record) {
    let { name } = field || {}

    if (!isEmpty(record[name])) {
      return getFormatedTime(record[name])
    } else {
      return '---'
    }
  },
  OTHERS(field, record) {
    return (
      <div
        class="truncate-text"
        vOn:click={() => this.openSummary(field, record)}
      >
        {this.getColumnDisplayValue(field, record)}
      </div>
    )
  },
}

const COLUMN_STYLE_HASH = {
  columnWidth(value) {
    return { width: `${value}px` }
  },
  'text-wrap'(value) {
    if (value === 'wrap') {
      return { 'word-wrap': 'break-word', 'white-space': 'normal' }
    }
    return { 'text-overflow': 'ellipsis' }
  },
}

export default {
  props: [
    'viewDetail',
    'records',
    'moduleName',
    'redirectToOverview',
    'slotList',
    'hideListSelect',
    'canShowCustomButton',
    'refreshList',
    'modelDataClass',
    'isTableFreeze',
    'hideGlimpse',
    'specialFieldDisplayValue',
  ],
  data() {
    return {
      sites: [],

      backgroundWidgets: [],
      handlers: {},
      customColumnValueHash: {},
      isCustomBtnLoading: false,
    }
  },
  components: {
    SignatureField,
    FilePreviewColumn,
    CustomButton,
    GlimpseLookupWrapper,
  },

  computed: {
    ...mapGetters(['getTicketStatus', 'currentAccount']),
    ...mapState({
      metaInfo: state => state.view.metaInfo,
      account: state => state.account,
      currencyList: state => state.activeCurrencies,
    }),
    moduleDisplayName() {
      return this.metaInfo?.displayName
    },
    columnConfig() {
      let { columnConfig } = this.$attrs || {}
      return !isEmpty(columnConfig)
        ? columnConfig
        : getColumnConfig(this.moduleName)
    },
    viewColumns() {
      let columns = []
      let { viewDetail } = this
      let { fields: viewDetailFields = [] } = viewDetail

      if (!isEmpty(viewDetail) && !isEmpty(viewDetailFields)) {
        columns = viewDetailFields.map(viewField => {
          let { field, fieldName } = viewField || {}
          field = field || {}
          let name = field.name || fieldName
          let fieldId = field.fieldId
          let defaultColumnName =
            viewDetail.defaultModuleFields &&
            viewDetail.defaultModuleFields[viewField.name]
              ? viewDetail.defaultModuleFields[viewField.name].columnDisplayName
              : null
          let displayName =
            viewField.columnDisplayName ||
            defaultColumnName ||
            field.displayName

          viewField = { ...viewField, name, fieldId, displayName }

          let columnStyle = this.getColumnStyle(viewField)
          viewField.columnAttrs = { ...viewField.columnAttrs, ...columnStyle }

          return viewField
        })
      }

      let { columnConfig } = this
      let { fixedSelectableColumns } = columnConfig || {}
      if (!isEmpty(columns)) {
        return columns.filter(column => {
          return !(fixedSelectableColumns || []).includes(column.name)
        })
      }
      return []
    },
    customFieldValueSlots() {
      let { slotList } = this
      return !isEmpty(slotList)
        ? slotList.filter(slotObj => !slotObj.isActionColumn)
        : []
    },
    actionColumnSlots() {
      let { slotList } = this
      return !isEmpty(slotList)
        ? slotList.filter(slotObj => slotObj.isActionColumn)
        : []
    },
    hardCodedSlots() {
      let { slotList } = this
      return !isEmpty(slotList)
        ? slotList.filter(slotObj => slotObj.isHardcodedColumn)
        : []
    },
    tableLoading() {
      return this.isTableFreeze || this.isCustomBtnLoading
    },
    multiCurrency() {
      let { displaySymbol, currencyCode, multiCurrencyEnabled } =
        this.account.data.currencyInfo || {}
      return { displaySymbol, currencyCode, multiCurrencyEnabled }
    },
  },
  created() {
    this.getSiteList()
    this.$store.dispatch('getActiveCurrencyList')
  },
  mounted() {
    this.initWidgetHandlers()
    this.getRecordWidgets()
  },
  methods: {
    getScopedSlots(slotObj, data) {
      let { name } = slotObj || {}
      if (!isEmpty(this.$scopedSlots) && !isEmpty(this.$scopedSlots[name])) {
        return this.$scopedSlots[slotObj.name]({
          record: data.row,
        })
      }
      return
    },
    initWidgetHandlers() {
      const reloadList = () => {
        this.reloadList()
      }
      const setColumnValue = options => {
        let { id, fieldName, value } = options || {}
        this.setColumnValue(id, fieldName, value)
      }
      const getRecords = () => {
        return this.records
      }
      const resetColumnValue = options => {
        let { id, fieldName } = options || {}
        this.resetColumnValue(id, fieldName)
      }

      // live data subscribe
      let subscribe = ({ topic }) => {
        if (topic) {
          this.$wms.subscribe(topic, this.onLiveData)
        }
      }
      let unsubscribe = ({ topic }) => {
        if (topic) {
          this.$wms.unsubscribe(topic, this.onLiveData)
        }
      }
      this.handlers = {
        reloadList,
        setColumnValue,
        getRecords,
        resetColumnValue,
        subscribe,
        unsubscribe,
        // setColumnStyle,
      }
    },
    onLiveData(eventData) {
      this.sendEvent('live.data.received', eventData)
    },
    sendEvent(eventName, eventData) {
      let { backgroundWidgets, $refs } = this
      if (!isEmpty(backgroundWidgets)) {
        for (let widget of backgroundWidgets) {
          let { id } = widget
          let refElement = $refs[`ref-${id}`]
          if (!isEmpty(refElement)) {
            refElement.sendEvent(eventName, eventData)
          }
        }
      }
    },
    setColumnValue(id, fieldName, value) {
      let { customColumnValueHash } = this || {}
      let selectedRecordId = this.$getProperty(
        customColumnValueHash,
        `${id}`,
        null
      )
      if (!isEmpty(selectedRecordId)) {
        let setFieldValue = { [fieldName]: value }

        let customValueList = {
          [id]: { ...selectedRecordId, ...setFieldValue },
        }
        this.customColumnValueHash = {
          ...this.customColumnValueHash,
          ...customValueList,
        }
      } else {
        let customValueList = { [id]: { [fieldName]: value } }
        this.customColumnValueHash = {
          ...this.customColumnValueHash,
          ...customValueList,
        }
      }
    },
    resetColumnValue(id, fieldName) {
      let { customColumnValueHash } = this || {}
      let selectedRecordId = this.$getProperty(
        customColumnValueHash,
        `${id}`,
        null
      )
      if (!isEmpty(selectedRecordId)) {
        let setFieldValue = { [fieldName]: {} }
        let customValueList = {
          [id]: { ...selectedRecordId, ...setFieldValue },
        }
        this.customColumnValueHash = {
          ...this.customColumnValueHash,
          ...customValueList,
        }
      }
    },
    async getRecordWidgets() {
      let { viewDetail } = this
      let { id } = viewDetail || {}

      let filters = {
        entityType: {
          operatorId: 9,
          value: ['11'],
        },
      }
      if (!isEmpty(id) && id !== -1) {
        filters = {
          ...filters,
          entityId: {
            operatorId: 9,
            value: [`${id}`],
          },
        }
      }

      let { error, data } = await this.loadWidgets(filters)
      if (error) {
        let { message = 'Error occured' } = error
        this.$message.error(message)
      } else {
        let { connectedAppWidgets } = data || {}

        this.backgroundWidgets = connectedAppWidgets
      }
    },
    async loadWidgets(filters) {
      let encodedFilters = encodeURIComponent(JSON.stringify(filters))
      let url = `/v2/connectedApps/widgetList?filters=${encodedFilters}`
      let response = await API.get(url)
      return response
    },

    // field type checks
    isDecimalField(field) {
      let { dataTypeEnum } = field.field || {}
      return field.field && dataTypeEnum === 'DECIMAL'
    },
    isFixedColumn(field) {
      let { name } = field || {}
      let { fixedColumns } = this.columnConfig || {}
      return (fixedColumns || []).includes(name) || this.isMainField(field)
    },
    isMainField(fieldObj) {
      let { field } = fieldObj || {}
      let { mainField } = field || {}
      return mainField
    },
    // column display related methods
    getColumnHeaderLabel(column) {
      let { displayName, field } = column

      if (!isEmpty(field) && !isEmpty(field.unit)) {
        let { unit } = field
        return `${displayName} (${unit})`
      }
      return displayName
    },
    getColumnDisplayValue(field, record) {
      let { parentField } = field
      let fieldObj = field.field || field
      let { dataTypeEnum } = fieldObj || {}

      let value
      // first level lookup value, related handling
      if (!isEmpty(parentField)) {
        let { name: lookupModuleName } = parentField || {}
        if (!isEmpty(lookupModuleName) && !isEmpty(record[lookupModuleName]))
          record = record[lookupModuleName]
      }
      // Column display value is categorized based on the data type property, except for siteId
      // which is treated as a special field for all the modules.
      if (field.name === 'siteId') {
        value = this.getSiteName(record[field.name])
      } else if (!isEmpty(DATA_TYPE_VALUE_HASH[dataTypeEnum])) {
        dataTypeEnum = dataTypeEnum || dataTypeEnum._name
        value = DATA_TYPE_VALUE_HASH[dataTypeEnum](field, record, this)
      } else {
        value = DATA_TYPE_VALUE_HASH['OTHERS'](field, record, this)
      }

      let { id } = record || {}
      let { name } = field || {}
      let customValue = this.$getProperty(
        this.customColumnValueHash,
        `${id}.${name}`,
        null
      )

      if (!isEmpty(customValue)) {
        value = customValue
      }

      if (isFunction(this.specialFieldDisplayValue)) {
        value = this.specialFieldDisplayValue({ field, value })
      }

      return !isEmpty(value) ? value : '---'
    },
    getColumnDisplayComponent(field, record) {
      let fieldObj = field.field || field
      let { displayType } = fieldObj || {}

      let { criteriaCheckForSlot } = this || {}

      let { canRenderSlot, slotName } = criteriaCheckForSlot(field)

      if (canRenderSlot && !isEmpty(this.$scopedSlots[slotName])) {
        return this.$scopedSlots[slotName]({ record })
      }

      if (!isEmpty(this[displayType])) {
        return this[displayType](field, record, this.getColumnDisplayValue)
      } else {
        return this['OTHERS'](field, record, this.getColumnDisplayValue)
      }
    },
    getColumnStyle(field) {
      let { customization } = field || {}
      let style = {}
      if (!isEmpty(customization)) {
        customization = JSON.parse(customization) || {}
        Object.entries(customization).forEach(([key, value]) => {
          if (!isEmpty(COLUMN_STYLE_HASH[key])) {
            style = { ...style, ...COLUMN_STYLE_HASH[key](value) }
          }
        })
      }
      return style
    },
    criteriaCheckForSlot(field) {
      let { customFieldValueSlots } = this
      let canRenderSlot = true
      let slotName = ''
      let fieldObj = field.field || field
      if (!isEmpty(customFieldValueSlots)) {
        for (let currSlot of customFieldValueSlots) {
          let { criteria } = currSlot || {}
          if (!isEmpty(criteria)) {
            let slotCriteria = JSON.parse(criteria)
            if (isObject(slotCriteria)) {
              Object.keys(slotCriteria).forEach(fieldName => {
                if (fieldObj[fieldName] !== slotCriteria[fieldName]) {
                  canRenderSlot = false
                } else {
                  canRenderSlot = true
                }
              })
            }
            if (canRenderSlot) {
              slotName = criteria
              break
            }
          }
        }
      }
      return { canRenderSlot, slotName }
    },
    // redirects and event emiters
    openSummary(field, record) {
      let { id } = record || {}
      let routerObj = isFunction(this.redirectToOverview)
        ? this.redirectToOverview(id)
        : null

      if (this.isFixedColumn(field)) {
        !isEmpty(routerObj) && this.$router.push(routerObj)
        this.$emit('mainField', record)
      }
    },
    selectionChange(selected) {
      this.$emit('selection-change', selected)
    },
    select(selected) {
      this.$emit('select', selected)
    },
    selectAll(selected) {
      this.$emit('selectAll', selected)
    },
    async getSiteList() {
      let { records } = this
      let defaultIds = []
      records.forEach(record => {
        if (record.siteId) {
          defaultIds.push(record.siteId)
        }
      })

      let { error, options } = await getFieldOptions({
        field: {
          lookupModuleName: 'site',
          additionalParams: {
            isToFetchDecommissionedResource: true,
          },
        },
        defaultIds,
      })
      if (!error) {
        this.sites = options
      }
    },
    getSiteName(id) {
      let { sites } = this
      let site = (sites || []).find(site => site.value === id)
      if (!isEmpty(site)) {
        let value = site.label ? site.label : '---'
        return value
      }
    },
    reloadList() {
      this.refreshList()
    },
    toggleVisibility(id, val) {
      let { $el } = this.$refs[`custom-btn-${id}`] || {}
      let { classList } = $el || {}
      if (val) {
        classList.remove('visibility-hide-actions')
      } else {
        classList.add('visibility-hide-actions')
      }
    },
    handleRecordFreeze(loading) {
      this.isCustomBtnLoading = loading
    },
    tableRowClick() {
      if (isFunction(this.$attrs.tableRowClick)) {
        this.$attrs.tableRowClick(...arguments)
      }
    },
    checkSelection() {
      if (isFunction(this.$attrs.checkSelection)) {
        return this.$attrs.checkSelection(...arguments)
      }
      return true
    },
    updateSite(siteObj) {
      this.sites.push(siteObj)
    },
    // display type hash
    ...DISPLAY_TYPE_HASH,
  },
  render() {
    return (
      <div class="common-list-container">
        <div class="hide">
          {this.backgroundWidgets &&
            this.backgroundWidgets.map(widget => {
              return (
                <ConnectedAppViewWidget
                  ref={`ref-${widget.id}`}
                  key={widget.id}
                  widgetId={widget.id}
                  context={this.viewDetail}
                  handlers={this.handlers}
                ></ConnectedAppViewWidget>
              )
            })}
        </div>
        <el-table
          v-loading={this.tableLoading}
          ref={`common-list-${this.moduleName}`}
          data={this.records}
          style="width: 100%;"
          height="100%"
          fit={true}
          row-class-name={'no-hover'}
          {...{
            on: {
              'selection-change': this.selectionChange,
              select: this.select,
              'select-all': this.selectAll,
              'row-click': this.tableRowClick,
            },
          }}
        >
          {!this.hideListSelect && (
            <el-table-column
              fixed
              align="left"
              type="selection"
              width="60"
              selectable={this.checkSelection}
            ></el-table-column>
          )}
          {this.hardCodedSlots.map((slotObj, index) => {
            return (
              <el-table-column
                key={`${slotObj.name}-${index}`}
                {...{
                  scopedSlots: {
                    default: data => {
                      return this.getScopedSlots(slotObj, data)
                    },
                  },
                }}
                {...{ attrs: slotObj.columnAttrs || {} }}
              ></el-table-column>
            )
          })}
          {this.viewColumns.map((field, index) => {
            return (
              <el-table-column
                fixed={this.isFixedColumn(field)}
                key={index}
                prop={field.name}
                label={this.getColumnHeaderLabel(field)}
                align={this.isDecimalField(field) ? 'right' : 'left'}
                min-width="230"
                class-name={this.isMainField(field) ? 'main-field-column' : ''}
                {...{
                  scopedSlots: {
                    default: data => {
                      return this.getColumnDisplayComponent(field, data.row)
                    },
                  },
                }}
                {...{ attrs: field.columnAttrs || {} }}
              ></el-table-column>
            )
          })}
          {this.actionColumnSlots.map((slotObj, index) => {
            return (
              <el-table-column
                key={`${slotObj.name}-${index}`}
                {...{
                  scopedSlots: {
                    default: data => {
                      return this.getScopedSlots(slotObj, data)
                    },
                  },
                }}
                {...{ attrs: slotObj.columnAttrs || {} }}
              ></el-table-column>
            )
          })}
          {this.canShowCustomButton && (
            <el-table-column
              width="70"
              align="center"
              fixed="right"
              class="visibility-visible-actions"
              {...{
                scopedSlots: {
                  default: data => {
                    return (
                      <CustomButton
                        key={`${this.moduleName}_${data.row.id}_${POSITION_TYPE.LIST_ITEM}`}
                        moduleName={this.moduleName}
                        position={POSITION_TYPE.LIST_ITEM}
                        record={data.row}
                        modelDataClass={this.modelDataClass}
                        class="custom-button visibility-hide-actions"
                        vOn:onSuccess={() => this.refreshList()}
                        ref={`custom-btn-${data.row.id}`}
                        toggleVisibility={this.toggleVisibility}
                        v-on:freezeRecord={this.handleRecordFreeze}
                      ></CustomButton>
                    )
                  },
                },
              }}
            ></el-table-column>
          )}
        </el-table>
      </div>
    )
  },
}
</script>

<style scoped lang="scss">
.url-field-display {
  color: #46a2bf;
  &:hover {
    text-decoration: underline;
    text-underline-offset: 3px;
    color: #46a2bf;
  }
}
.common-list-container {
  height: 100%;
}
</style>
<style lang="scss">
.common-list-container {
  .el-table .el-table__row {
    min-height: 50px;
    height: 50px;
  }
}
</style>
