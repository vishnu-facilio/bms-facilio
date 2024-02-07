<script>
import ViewMixinHelper from '@/mixins/ViewMixin'
import Pagination from 'pageWidgets/utils/WidgetPagination'
import { isEmpty, isNullOrUndefined } from '@facilio/utils/validation'
import isEqual from 'lodash/isEqual'
import { getApp } from '@facilio/router'

const fetchListUrlAndResultMap = {
  users: {
    url: `v2/setup/userlist`,
    result: 'users',
  },
  alarm: {
    url: `v2/newAlarms/view/all`,
    result: 'alarms',
  },
  newreadingalarm: {
    url: `v2/newAlarms/view/active?alarmModule=newreadingalarm`,
    result: 'alarms',
  },
  workorder: {
    url: `/workorder/all`,
  },
  safetyPlanHazard: {
    url: `v2/safetyPlanHazard/list`,
    result: 'safetyPlanHazardList',
  },
  hazardPrecaution: {
    url: `v2/hazardPrecaution/list`,
    result: 'hazardPrecautionList',
  },
  workorderHazard: {
    url: `v2/workorderHazard/list`,
    result: 'workorderHazards',
  },
  assetHazard: {
    url: `v2/assetHazard/list`,
    result: 'assetHazards',
  },
  tenant: {
    url: `v2/tenant/all`,
    countUrl: `v2/tenant/all?count=true`,
    result: 'tenants',
  },
  asset: {
    url: 'v2/assets/view/all',
    countUrl: `v2/assets/view/all?count=true`,
    result: 'assets',
  },
}
const skipSiteIdModules = [
  'vendors',
  'safetyPlan',
  'people',
  'vendorcontact',
  'service',
  'client',
]

export default {
  mixins: [ViewMixinHelper],
  components: { Pagination },
  computed: {
    mainField() {
      let { viewDetail } = this
      let mainField = null
      if (!isEmpty(viewDetail)) {
        let { fields } = viewDetail
        mainField = (fields || []).find(field => {
          let { field: fieldObj } = field
          return (fieldObj || {}).mainField
        })
      } else {
        mainField = {
          name: 'name',
          displayName: 'Name',
          field: {
            name: 'name',
            dataTypeEnum: 'STRING',
          },
        }
      }
      return mainField
    },
    mainFieldColumn() {
      let { viewColumns, mainField } = this
      let mainFieldName =
        this.$getProperty(mainField, 'field.name', '') || 'name'
      if (!isEmpty(viewColumns)) {
        return viewColumns.find(column => column.name === mainFieldName)
      }
      return {}
    },
    hideColumns() {
      let { defaultHideColumns = [], mainField } = this
      let mainFieldName =
        this.$getProperty(mainField, 'field.name', '') || 'name'
      return defaultHideColumns.concat(mainFieldName)
    },
    hidePaginationSearch() {
      let { moduleName } = this
      return ['users'].includes(moduleName)
    },
    filteredViewColumns() {
      let { viewColumns, hideColumns } = this
      if (!isEmpty(viewColumns)) {
        return viewColumns.filter(column => {
          return !hideColumns.includes(column.name)
        })
      }
      return []
    },
    filters() {
      let {
        $route,
        widget,
        siteId,
        moduleName,
        specialFilters,
        resourceModule,
        showFilters,
        categoryId,
        skipSiteFilter = false,
        selectedLookupField,
      } = this
      let { params, name } = $route
      let { id, assetid } = params
      let { relatedList } = widget || {}
      let { field } = relatedList || {}
      let fieldName = (field || {}).name
      let { filters } = selectedLookupField || {}
      let filterObj = {}

      if (name === 'assetsummary') {
        id = assetid
      }
      if (resourceModule || showFilters) {
        filterObj = { ...filterObj, ...this.resourceFilterConstruction() }
      }
      //temp should be changed as prop
      if (
        ([
          'wosummarynew',
          'portalVendorsummary',
          'vendorsummary',
          'vendorWorkorderSummary',
          'hazardSummary',
          'safetyPlanSummary',
          'precautionSummary',
          'tenantSummary',
          'assetsummary',
          'serviceRequestSummary',
          'custommodules-summary',
          'clientSummary',
          'spaceOverview',
          'tenantUnitSummary',
          'et-custommodules-summary',
          'newapprovalsummary',
          'quotationSummary',
          'prSummary',
          'poSummary',
          'tenant',
          // DONT ADD ANYTHING HERE
        ].includes(name) ||
          (this.$account.org.id === 406 &&
            getApp().linkName === 'service' &&
            [
              'custom_supplierquotation',
              'custom_supplierselectionform',
            ].includes(moduleName))) &&
        !specialFilters &&
        !isEmpty(fieldName)
      ) {
        filterObj[fieldName] = {
          operatorId: 36,
          value: [`${id}`],
        }
      }

      if (!isEmpty(categoryId)) {
        filterObj.category = {
          operator: 'is',
          value: [`${categoryId}`],
        }
      }

      if (
        !isEmpty(siteId) &&
        !skipSiteIdModules.includes(moduleName) &&
        !skipSiteFilter
      ) {
        filterObj.siteId = {
          operatorId: 36,
          value: [`${siteId}`],
        }
      }
      if (!isEmpty(specialFilters)) {
        filterObj = Object.assign(filterObj, specialFilters)
      }
      if (
        moduleName === 'workorder' &&
        this.$getProperty(this, 'selectedLookupField.field.module.name', '') ===
          'quote'
      ) {
        filterObj['isQuotationNeeded'] = {
          operatorId: 15,
          value: [String(true)],
        }
      }
      if (!isEmpty(filters)) {
        filterObj = {
          ...filterObj,
          ...filters,
        }
      }
      return filterObj
    },
    routeName() {
      let { $route } = this
      let { name } = $route || {}
      return name || ''
    },
  },
  data() {
    return {
      isLoading: false,
      uniqueListFetchModules: ['alarm'],
      viewDetail: null,
      modulesList: [],
      page: 1,
      perPage: 10,
      totalCount: null,
      isSearchDataLoading: false,
      debounceDelay: 2000,
    }
  },
  watch: {
    filters(newVal, oldVal) {
      if (!isEqual(newVal, oldVal) && !isEmpty(newVal)) {
        this.$set(this, 'isSearchDataLoading', true)
        this.debounceMainFieldSearch()
      }
    },
    page(newVal, oldVal) {
      if (!isEqual(newVal, oldVal)) {
        let { isSearchDataLoading } = this
        if (!isSearchDataLoading) {
          this.isLoading = true
          this.fetchModulesData(true).finally(() => {
            this.isLoading = false
            this.onDataLoad && this.onDataLoad()
          })
          this.loadDataCount(true)
        }
      }
    },
  },
  methods: {
    init() {
      this.fetchListUrlAndResultMap = this.$helpers.cloneObject(
        fetchListUrlAndResultMap
      )
      this.debounceMainFieldSearch = this.$helpers.debounce(() => {
        this.fetchModulesData().finally(() => {
          this.onDataLoad && this.onDataLoad()
        })
        this.loadDataCount()
      }, this.debounceDelay)
      this.loadData()
      this.loadDataCount()
    },
    loadData() {
      let promise = []

      if (this.moduleName) {
        promise.push(this.fetchViewDetail())
        promise.push(this.fetchModulesData())

        this.isLoading = true
        Promise.all(promise).finally(() => {
          this.isLoading = false
          this.onDataLoad && this.onDataLoad()
        })
      }
    },
    loadDataCount() {
      let {
        moduleName,
        filters,
        hidePaginationSearch,
        selectedLookupField,
        withReadings,
      } = this
      let { clientCriteria } = selectedLookupField || {}
      if (!hidePaginationSearch && moduleName) {
        let url =
          (fetchListUrlAndResultMap[moduleName] || {}).countUrl ||
          `v2/module/data/list?moduleName=${moduleName}&fetchCount=true`
        if (!isEmpty(filters)) {
          let encodedFilters = encodeURIComponent(JSON.stringify(filters))
          url = `${url}&filters=${encodedFilters}`
        }
        if (moduleName === 'workorder') {
          url += `&viewName=all`
          if (!isEmpty(filters)) {
            url += `&includeParentFilter=true`
          }
        }

        if (
          moduleName === 'tenant' &&
          !isEmpty(selectedLookupField) &&
          !isEmpty(selectedLookupField.spaceId) &&
          this.$org.id !== 320
        ) {
          url = `${url}&spaceId=${selectedLookupField.spaceId}`
        }
        if (withReadings) {
          url = `${url}&withReadings=true`
        }
        if (!isEmpty(clientCriteria)) {
          let criteria = encodeURIComponent(JSON.stringify(clientCriteria))
          url = `${url}&clientCriteria=${criteria}`
        }
        return this.$http
          .get(url)
          .then(({ data: { message, responseCode, result = {} } }) => {
            if (responseCode === 0) {
              let { count } = result
              this.$set(this, 'totalCount', count)
            } else {
              throw new Error(message)
            }
          })
          .catch(({ message }) => {
            this.$message.error(`${message} while fetching count`)
          })
      }
    },
    isSpecialHandlingField(field) {
      let { name } = field
      let { moduleName, listComponentsMap = {} } = this
      let specialFields = (listComponentsMap[moduleName] || {})
        .specialHandlingFields
      return (specialFields || []).includes(name)
    },
    fetchViewDetail() {
      let { moduleName } = this
      let viewDetailUrl = `v2/views/hidden-all?moduleName=${moduleName}`

      return this.$http.get(viewDetailUrl).then(viewDetailsData => {
        if (!isEmpty(viewDetailsData)) {
          let {
            data: { message, responseCode, result = {} },
          } = viewDetailsData

          if (responseCode === 0) {
            let { viewDetail } = result
            if (!isEmpty(viewDetail)) {
              this.$set(this, 'viewDetail', viewDetail)
            }
          } else {
            throw new Error(message)
          }
        }
      })
    },
    fetchModulesData(skipResetPage = false) {
      let {
        moduleName,
        filters,
        perPage,
        hidePaginationSearch,
        fetchListUrlAndResultMap,
        selectedLookupField,
        withReadings,
      } = this
      let { clientCriteria, additionalParams } = selectedLookupField || {}
      let modulesListDataUrl
      modulesListDataUrl =
        (fetchListUrlAndResultMap[moduleName] || {}).url ||
        `v2/module/data/list?moduleName=${moduleName}`
      // Reset page to 1 on search
      if (!isEmpty(filters) && !skipResetPage) {
        this.$set(this, 'page', 1)
      }
      if (modulesListDataUrl.includes('?')) {
        modulesListDataUrl += '&'
      } else {
        modulesListDataUrl += '?'
      }
      if (!hidePaginationSearch) {
        modulesListDataUrl += `page=${this.page}&perPage=${perPage}`
      }
      if (moduleName !== 'workorder') {
        modulesListDataUrl += `&viewName=hidden-all`
      }
      if (!isEmpty(filters)) {
        let encodedFilters = encodeURIComponent(JSON.stringify(filters))
        modulesListDataUrl = `${modulesListDataUrl}&filters=${encodedFilters}`
      }
      if (!isEmpty(filters) && moduleName === 'workorder') {
        modulesListDataUrl += `&includeParentFilter=true`
      }
      if (
        moduleName === 'tenant' &&
        !isEmpty(selectedLookupField) &&
        !isEmpty(selectedLookupField.spaceId) &&
        this.$org.id !== 320
      ) {
        modulesListDataUrl = `${modulesListDataUrl}&spaceId=${this.selectedLookupField.spaceId}`
      }
      if (withReadings) {
        modulesListDataUrl = `${modulesListDataUrl}&withReadings=true`
      }
      if (!isEmpty(clientCriteria)) {
        let criteria = encodeURIComponent(JSON.stringify(clientCriteria))
        modulesListDataUrl = `${modulesListDataUrl}&clientCriteria=${criteria}`
      }
      if (!isEmpty(additionalParams)) {
        Object.entries(additionalParams).forEach(([key, value]) => {
          modulesListDataUrl = `${modulesListDataUrl}&${key}=${value}`
        })
      }
      return this.$http
        .get(modulesListDataUrl)
        .then(modulesListData => {
          if (!isEmpty(modulesListData)) {
            if (moduleName === 'workorder') {
              let {
                data: { workOrders = {} },
              } = modulesListData
              if (!isNullOrUndefined(workOrders)) {
                this.modulesList = workOrders
              }
            } else {
              let {
                data: { message, responseCode, result = {} },
              } = modulesListData
              if (responseCode === 0) {
                let moduleDatas =
                  fetchListUrlAndResultMap[moduleName] &&
                  result[fetchListUrlAndResultMap[moduleName].result]
                    ? result[fetchListUrlAndResultMap[moduleName].result]
                    : result.moduleDatas
                if (!isNullOrUndefined(moduleDatas)) {
                  this.$set(this, 'modulesList', moduleDatas)
                }
              } else {
                throw new Error(message)
              }
            }
          }
        })
        .catch(({ message }) => {
          this.$message.error(message)
        })
        .finally(() => {
          this.isSearchDataLoading = false
        })
    },
    refreshRelatedList() {
      this.loadData()
      this.loadDataCount()
    },
  },
}
</script>
