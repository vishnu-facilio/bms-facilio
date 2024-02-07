<template>
  <div class="sfw-container">
    <spinner v-if="isLoading" :show="isLoading" size="80"></spinner>
    <div
      v-else-if="$validation.isEmpty(detailsLayout)"
      class="sfw-empty-state-container"
      ref="content-container"
    >
      <inline-svg
        class="self-center"
        src="svgs/custom-empty-state"
        iconClass="icon text-center icon-xxxxlg"
      />
      <div class="mT10 label-txt-black f14 self-center">
        {{ $t('common._common.error_summary_widget_field') }}
      </div>
    </div>
    <div v-else ref="content-container">
      <FieldDetails
        :detailsLayout="detailsLayout"
        :config="config"
        @fileDownload="downloadAttachment"
        @lookupRedirect="redirectToSummary"
      ></FieldDetails>
    </div>
    <div
      v-if="needsShowMore && !$validation.isEmpty(detailsLayout)"
      class="view-more"
    >
      <a
        @click="toggleVisibility"
        class=" fc-link-animation text-capitalize letter-spacing0_3 f13 margin-auto view-text"
        >{{ showMoreLinkText }}</a
      >
    </div>
    <iframe
      v-if="exportDownloadUrl"
      :src="exportDownloadUrl"
      style="display: none;"
    ></iframe>
    <PreviewFile
      :visibility.sync="showPreview"
      v-if="showPreview"
      :previewFile="previewFile"
      :files="[previewFile]"
    ></PreviewFile>
  </div>
</template>
<script>
import { NewFieldDetails as FieldDetails } from '@facilio/ui/app'
import { API } from '@facilio/api'
import { getApp } from '@facilio/router'
import { isEmpty } from '@facilio/utils/validation'
import { getFieldOptions } from 'src/util/picklist'
import FetchViewsMixin from '@/base/FetchViewsMixin'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import PreviewFile from '@/PreviewFile'
import getProperty from 'dlv'
import { mapState } from 'vuex'

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
  custom: (id, viewname, moduleName) => {
    return {
      name: 'custommodules-summary',
      params: { moduleName, viewname, id },
    }
  },
}
const dataConstructHash = {
  DEFAULT(details, mainField) {
    let { name } = mainField
    let value = details[name] || null

    return { name, value }
  },
  ENUM_HASH(details, mainField) {
    let { name, field } = mainField
    let value = details[name] || null
    let { values } = field
    let options = values.reduce((acc, value) => {
      let { index, value: choice } = value
      acc[index] = choice
      return acc
    }, {})

    return { value, options, name }
  },
  FILE(details, mainField) {
    let { name } = mainField
    let value = details[`${name}FileName`] || null
    let url = details[`${name}DownloadUrl`] || null
    let contentType = details[`${name}ContentType`] || null
    let previewUrl = details[`${name}Url`] || null
    let isClickable = !isEmpty(value) ? true : false

    return { value, url, contentType, previewUrl, isClickable, name }
  },
  DIGIT_HASH(details, mainField) {
    let { name } = mainField || {}
    let unit = getProperty(mainField, 'field.unit', null)
    let value = details[name] || null
    let returnValue = { name, value }

    if (unit) returnValue.options = { unit }
    return returnValue
  },
  BOOLEAN(details, mainField) {
    let { field: fieldObj, name } = mainField || {}
    let { trueVal, falseVal } = fieldObj
    let options = { trueVal, falseVal }
    let value = details[name] || null

    return { options, name, value }
  },
  CURRENCY_FIELD(details, mainField) {
    let { name } = mainField || {}
    return { value: details[name] || {}, name }
  },
  MULTI_CURRENCY_FIELD(details, mainField) {
    let { name } = mainField || {}
    let { currencyCode, exchangeRate } = details || {}
    return { value: details[name] || null, name, currencyCode, exchangeRate }
  },
  async LOOKUP(details, mainField) {
    let { field, name: fieldName, parentLookupField } = mainField
    let { lookupModule } = field || {}
    let { name: moduleName } = lookupModule
    let lookupModuleName = parentLookupField?.name || ''
    let name = lookupModuleName
      ? `${lookupModuleName}.${fieldName}`
      : `${fieldName}`
    let getFieldValue = fieldName => {
      let fieldValue = details[fieldName]

      return !isEmpty(fieldValue) ? [fieldValue] : []
    }
    let id = getProperty(details, `${name}.id`, null)
    let idArr = !isEmpty(id) ? [id] : []
    let { isClickable, value } = await fetchSecondLevelValues(
      mainField,
      getFieldValue,
      idArr
    )

    value = value[0] || null
    return { id, isClickable, moduleName, name, value }
  },
  async MULTI_LOOKUP(details, mainField) {
    let { field, name: fieldName, parentLookupField } = mainField
    let { lookupModule } = field || {}
    let { name: moduleName } = lookupModule || {}
    let lookupModuleName = parentLookupField?.name || ''
    let name = lookupModuleName
      ? `${lookupModuleName}.${fieldName}`
      : `${fieldName}`
    let ids = getProperty(details, `${name}`, null) || []
    let id = ids.map(lookupObj => lookupObj.id)
    let getFieldValue = fieldName => details[fieldName] || []
    let { isClickable, value } = await fetchSecondLevelValues(
      mainField,
      getFieldValue,
      id
    )

    value = value.map(ml => {
      ml.moduleName = moduleName
      return ml
    })

    return { value, isClickable, name }
  },
}
const fetchSecondLevelValues = async function(mainField, getFieldValue, id) {
  let { field, secondLevelLookup, name } = mainField
  let { lookupModule } = field || {}
  let { name: moduleName, typeEnum, custom } = lookupModule || {}
  let value
  let isClickable = typeEnum === 'BASE_ENTITY' && (isWebTabsEnabled() || custom)

  if (secondLevelLookup) {
    value = (await fetchLookupName(moduleName, id)) || []
  } else {
    value = getFieldValue(name) || []
  }
  isClickable = !isEmpty(value) && isClickable
  return { isClickable, value }
}
const fetchLookupName = async function(moduleName, id) {
  let value = []
  if (!isEmpty(id)) {
    let perPage = id.length
    let { error, options } = await getFieldOptions({
      perPage,
      field: {
        lookupModuleName: moduleName,
      },
      defaultIds: id,
    })
    if (isEmpty(error)) {
      let filterOptions = (options || []).filter(item =>
        id.includes(item.value)
      )
      value = filterOptions.map(item => {
        return { name: item.label }
      })
    }
  }
  return value
}
export default {
  props: [
    'layoutParams',
    'details',
    'moduleName',
    'primaryFields',
    'widget',
    'isV3Api',
    'disableAutoResize',
    'detailsLayoutProp',
    'siteList',
  ],
  components: { FieldDetails, PreviewFile },
  mixins: [FetchViewsMixin],
  data() {
    return {
      appId: null,
      detailsLayout: null,
      sites: {},
      needsShowMore: false,
      isAllVisible: false,
      defaultWidgetHeight: (this.layoutParams || {}).h,
      config: null,
      isLoading: false,
      exportDownloadUrl: null,
      showPreview: false,
      previewFile: null,
    }
  },
  async created() {
    this.init()
  },
  computed: {
    ...mapState({
      account: state => state.account,
      currencyList: state => state.activeCurrencies,
    }),
    showMoreLinkText() {
      return this.isAllVisible
        ? this.$t('common._common.view_less')
        : this.$t('common._common.view_more')
    },
    widgetName() {
      return this.$attrs.widgetName || this.widget?.name
    },
    multiCurrency() {
      let { displaySymbol, currencyCode, multiCurrencyEnabled } =
        this.account.data.currencyInfo || {}
      return { displaySymbol, currencyCode, multiCurrencyEnabled }
    },
  },
  methods: {
    async init() {
      this.isLoading = true
      let { multiCurrency } = this
      await this.$store.dispatch('getActiveCurrencyList')

      this.config = {
        dateformat: this.$dateformat,
        timezone: this.$timezone,
        timeformat: this.$timeformat,
        org: this.$account.org,
        multiCurrency,
        currencies: this.currencyList,
      }
      this.appId = (getApp() || {}).id

      await this.fetchSites()
      await this.fetchDetails()

      this.isLoading = false
      this.$nextTick(() => {
        if (isEmpty(this.detailsLayout)) this.resizeWidget({ height: 250 })
        else if (!this.disableAutoResize) this.autoResize()
        else this.$emit('autoResize', this.$refs['content-container'])
      })
    },
    openAttachment(fileObj) {
      let { url, contentType, previewUrl, value } = fileObj

      this.previewFile = {
        fileName: value,
        contentType,
        downloadUrl: url,
        previewUrl,
      }
      this.showPreview = true
    },
    async fetchDetails() {
      let detailsLayout = null

      if (isEmpty(this.detailsLayoutProp)) {
        let { moduleName, appId, widgetName } = this
        let { data, error } = await API.get(
          'v2/customPage/summaryFieldWidget',
          {
            moduleName,
            appId,
            widgetName,
          }
        )

        if (!error) {
          detailsLayout = data
        }
      } else {
        detailsLayout = this.detailsLayoutProp
      }
      await this.serializeDetailsLayout(detailsLayout)
    },

    async serializeDetailsLayout(data) {
      let { summaryFieldWidget } = data || {}
      let { groups } = summaryFieldWidget || {}
      let filteredGroup = (groups || []).filter(group => !isEmpty(group.fields))
      let detailsLayout = (filteredGroup || []).map(async group => {
        let { fields, colorCode, displayName, columns } = group
        let serializedFields = await this.constructFieldValues(fields)

        return {
          name: displayName,
          fields: serializedFields,
          sectionConfig: {
            columns,
            colorCode,
          },
        }
      })
      this.detailsLayout = await Promise.all(detailsLayout)
    },

    async constructFieldValues(fields) {
      let { details } = this
      let groupObj = fields.map(async mainField => {
        let {
          field,
          name,
          secondLevelLookup,
          parentLookupField,
          displayName,
          colSpan,
          colIndex,
          rowIndex,
          displayType: diplayTypeMain,
        } = mainField || {}
        let { dataTypeEnum, displayType } = field || {}
        let recordHolder = details || {}

        if (!isEmpty(parentLookupField) && !secondLevelLookup) {
          let { name: lookupModuleName } = parentLookupField || {}
          recordHolder = details[lookupModuleName] || {}
        }
        let extraValues
        if (name === 'siteId') {
          let siteId = details[name] || ''
          let siteLabel = this.sites[siteId] || ''
          let value = siteLabel || null
          extraValues = { name, value }
          dataTypeEnum = 'OTHERS'
        } else {
          extraValues = await this.getHash(dataTypeEnum)(
            recordHolder,
            mainField
          )
        }
        return {
          displayName,
          ...extraValues,
          dataTypeEnum,
          displayType: displayType || diplayTypeMain || null,
          fieldConfig: {
            rowIndex,
            colIndex,
            colSpan,
          },
        }
      })
      return Promise.all(groupObj)
    },
    getHash(dataTypeEnum) {
      let currentDataType = dataTypeEnum

      if (['ENUM', 'SYSTEM_ENUM', 'MULTI_ENUM'].includes(dataTypeEnum)) {
        currentDataType = 'ENUM_HASH'
      } else if (['NUMBER', 'DECIMAL'].includes(dataTypeEnum)) {
        currentDataType = 'DIGIT_HASH'
      }

      return dataConstructHash[currentDataType] || dataConstructHash['DEFAULT']
    },
    downloadAttachment(field) {
      let { name, url, contentType } = field
      let contentTypeArr = contentType.split('/')
      if (contentTypeArr.includes('image')) {
        this.openAttachment(field)
      } else if (!isEmpty(name)) {
        url = field.url
        if (!isEmpty(url)) {
          if (this.exportDownloadUrl) {
            this.exportDownloadUrl = null
          }

          this.$nextTick(() => {
            this.exportDownloadUrl = url
          })
        }
      }
    },
    toggleVisibility() {
      this.isAllVisible = !this.isAllVisible

      if (this.isAllVisible) {
        this.$nextTick(() => {
          let height = this.$refs['content-container'].scrollHeight + 40
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

        let height = this.$refs['content-container'].scrollHeight
        let width = this.$refs['content-container'].scrollWidth

        let dimensions = this.calculateDimensions({ height, width })

        if (isEmpty(dimensions)) return
        let { h } = dimensions || {}
        let params = {}
        if (h <= this.defaultWidgetHeight) {
          this.needsShowMore = false
          params = { height, width }
        } else {
          let { defaultWidgetHeight = 8 } = this
          this.needsShowMore = h > defaultWidgetHeight ? true : false
          this.defaultWidgetHeight = defaultWidgetHeight
          this.needsShowMore
            ? (params = { h: defaultWidgetHeight })
            : (params = { h: h })
        }
        this.resizeWidget(params)
      })
    },

    async redirectToSummary(field) {
      const freezeWidget = this.$loading({
        target: '.sfw-container',
      })
      let { moduleName, id, lookupModule } = field || {}
      let { custom } = lookupModule || {}
      let viewname = await this.fetchView(moduleName)
      let routerPath = null
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}
        if (name) {
          routerPath = this.$router.resolve({ name, params: { viewname, id } })
        }
      } else {
        if (custom) {
          routerPath = this.$router.resolve(
            lookupModulesRouterMap['custom'](id, viewname, moduleName)
          )
        } else {
          let getRouterObj = lookupModulesRouterMap[moduleName]

          if (getRouterObj) {
            routerPath = this.$router.resolve(getRouterObj(id, viewname))
          }
        }
      }
      !!routerPath && window.open(routerPath?.href, '_blank')
      freezeWidget.close()
    },
    async fetchSites() {
      let { details } = this
      let siteId = details.siteId || null

      if (isEmpty(siteId)) return
      if (isEmpty(this.siteList) && !isEmpty(siteId)) {
        let { error, options } = await getFieldOptions({
          field: { lookupModuleName: 'site', skipDeserialize: true },
          defaultIds: [siteId],
          perPage: 1,
        })
        if (!error && !isEmpty(options[siteId])) {
          this.sites = options
        }
      } else this.sites = this.siteList
    },
    resizeWidget(params) {
      let { $attrs } = this
      return $attrs.resizeWidget(params)
    },
    calculateDimensions(params) {
      let { $attrs } = this
      return $attrs?.calculateDimensions(params)
    },
  },
}
</script>
<style lang="scss" scoped>
.sfw-container {
  position: relative;
  height: 100%;
  overflow: hidden;
  .sfw-empty-state-container {
    background-color: #fff;
    flex-grow: 1;
    display: flex;
    flex-direction: column;
    justify-content: center;
    margin: 10px;
  }
  .view-more {
    background-color: #fff;
    position: absolute;
    width: 100%;
    top: calc(100% - 40px);
    padding: 10px 0px 15px;
    display: flex;
  }
}
</style>
