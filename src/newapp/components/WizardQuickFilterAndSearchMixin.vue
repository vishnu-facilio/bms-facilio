<script>
import cloneDeep from 'lodash/cloneDeep'
import { isEmpty } from '@facilio/utils/validation'
import { getFieldOptions, getFieldValue } from 'util/picklist'
const RESOURCE_MODULES = [
  'site',
  'building',
  'floor',
  'space',
  'asset',
  'basespace',
]
export default {
  data() {
    return {
      showSearch: false,
      moduleMetaInfo: {},
      clearAllFilters: false,
      wizardFilterValue: {},
      resourceSubModuleName: null,
      resourceFilterObj: {},
      quickFilters: null,
    }
  },

  computed: {
    resourceModule() {
      let { selectedLookupField } = this
      let { config, field, lookupModule } = selectedLookupField
      let { isFiltersEnabled } = config || {}
      if (isFiltersEnabled) {
        return false
      }

      let fieldName = ''

      if (!isEmpty(lookupModule)) {
        fieldName = lookupModule.name
      } else {
        fieldName = this.$getProperty(field, 'lookupModule.name', '')
      }

      return fieldName === 'resource'
    },
    specialFilters() {
      let {
        specialFilterValue,
        wizardFilterValue,
        moduleName,
        skipDecommission,
      } = this

      if (RESOURCE_MODULES.includes(moduleName) && !skipDecommission) {
        let resourceFilter = {
          decommission: {
            operatorId: 15,
            value: ['false'],
          },
        }

        wizardFilterValue = { ...wizardFilterValue, ...resourceFilter }
      }
      if (!isEmpty(specialFilterValue) && !isEmpty(wizardFilterValue)) {
        return Object.assign(specialFilterValue, wizardFilterValue)
      } else if (!isEmpty(wizardFilterValue)) {
        return wizardFilterValue
      } else {
        return !isEmpty(specialFilterValue) ? specialFilterValue : {}
      }
    },
  },

  methods: {
    async getResourceDetails(selectedIds) {
      let { moduleName, resourceModule } = this
      let lookupModuleName = moduleName

      if (resourceModule) lookupModuleName = 'resource'

      return getFieldValue({
        lookupModuleName,
        selectedOptionId: selectedIds,
      }).then(({ error, data }) => {
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.selectedItem = data
          if (this.$getProperty(data, '0.subModule') === 'basespace') {
            this.selectedItem.forEach(item => (item.subModule = 'space'))
          }
          this.setResourceSubModuleName(this.selectedItem)
        }
      })
    },
    setResourceSubModuleName(selectedItems) {
      let { subModule } = selectedItems[0]
      this.resourceSubModuleName = subModule
    },
    onTabSwitch(tabName) {
      let { disableTabSwitch } = this
      if (!disableTabSwitch) {
        this.showSearch = false
        this.resourceSubModuleName = tabName
        this.fetchModuleMetaInfo()
        this.constructQuickFilters()
        this.loadInitialData()
      }
    },
    fetchModuleMetaInfo() {
      let { moduleName } = this
      if (!isEmpty(moduleName)) {
        this.$http
          .get('/module/metafields?moduleName=' + moduleName)
          .then(response => {
            if (!isEmpty(response.data.meta))
              this.moduleMetaInfo = response.data.meta
            this.showSearch = true
          })
          .catch(() => {})
      }
    },
    constructQuickFilters() {
      this.quickFilters = cloneDeep(this.$constants.QUICK_FILTERS)
      this.resourceFilterObj = {}

      let {
        moduleName,
        quickFilters,
        siteId,
        categoryId,
        selectedLookupField,
      } = this
      let { config } = selectedLookupField
      let { skipSiteFilter } = config || {}

      quickFilters.forEach(filter => {
        filter.value = null

        if (filter.key === 'category') {
          if (moduleName === 'space' || moduleName === 'basespace') {
            filter.lookupModule.name = 'spacecategory'
          } else if (moduleName === 'asset') {
            filter.lookupModule.name = 'assetcategory'
          }
          if (!isEmpty(categoryId) && !skipSiteFilter) {
            filter.value = categoryId
            filter.disabled = true
          }
        } else if (filter.key === 'site' && siteId && !skipSiteFilter) {
          filter.value = siteId
          filter.disabled = true
        }
        this.resourceFilterConstruction(filter)
      })
    },
    resourceFilterConstruction(field) {
      if (isEmpty(field)) {
        let { moduleName, resourceFilterObj } = this

        if (moduleName === 'asset') {
          let filterObj = {}
          let spaceFilter = {
            operator: 'building_is',
            value: null,
          }
          let { space, floor, building, siteId, category } = resourceFilterObj

          if (category) {
            filterObj.category = { ...category }
          }

          if (space) {
            this.$set(spaceFilter, 'value', space.value)
          } else if (floor) {
            this.$set(spaceFilter, 'value', floor.value)
          } else if (building) {
            this.$set(spaceFilter, 'value', building.value)
          } else if (siteId) {
            this.$set(spaceFilter, 'value', siteId.value)
          }

          if (spaceFilter.value) filterObj.space = { ...spaceFilter }

          let parentModuleName = this.$getProperty(
            this.selectedLookupField,
            'field.module.name'
          )
          if (parentModuleName === 'inspectionTemplate') {
            filterObj.storeRoom = {
              operatorId: 1,
            }
          }

          return filterObj
        } else return this.resourceFilterObj
      }

      let {
        lookupModule: { name },
        value,
      } = field
      let filterHash = {
        spacecategory: 'spaceCategory',
        assetcategory: 'category',
        site: 'siteId',
        building: 'building',
        floor: 'floor',
        basespace: 'space',
      }

      if (!isEmpty(value)) {
        let filter = {}
        if (field.key === 'category') {
          filter = {
            operator: 'is',
            value: [`${value}`],
          }
        } else {
          filter = {
            operatorId: 36,
            value: [`${value}`],
          }
        }

        this.$set(this.resourceFilterObj, filterHash[name], filter)
      } else {
        this.$delete(this.resourceFilterObj, filterHash[name])
      }

      let filters = {}

      if (name === 'basespace') {
        Object.entries(this.resourceFilterObj).forEach(([key, value]) => {
          if (['siteId', 'building', 'floor'].includes(key)) {
            filters[key] = value
          }
        })
      } else if (name === 'building') {
        Object.entries(this.resourceFilterObj).forEach(([key, value]) => {
          if (key === 'siteId') {
            filters[key] = value
          }
        })
      } else if (name === 'floor') {
        Object.entries(this.resourceFilterObj).forEach(([key, value]) => {
          if (['siteId', 'building'].includes(key)) {
            filters[key] = value
          }
        })
      }
      return filters
    },
    setLookUpFilter(field, valueObj) {
      let { spaceType } = field
      let { value } = valueObj || {}

      this.quickFilters.forEach(fld => {
        if (fld.key === field.key) {
          fld.value = !isEmpty(value) ? value : null
          this.resourceFilterConstruction(fld)
        }
      })

      this.quickFilters.forEach(fld => {
        let resetOptions = spaceType && spaceType < fld.spaceType

        fld.filters = this.resourceFilterConstruction(fld)
        if (resetOptions) {
          if (fld.key !== 'building') fld.disabled = isEmpty(value)
          fld.value = null
          this.resourceFilterConstruction(fld)
          value = null

          let params = {
            page: 1,
            perPage: 50,
          }
          getFieldOptions({ field: fld, ...params }).then(
            ({ error, options }) => {
              if (error) {
                this.$message.error(error.message || 'Error Occured')
              } else {
                fld.options = options
              }
            }
          )
        }
      })
    },
    applyFilter(filter) {
      if (!isEmpty(filter)) {
        this.wizardFilterValue = filter
      }
    },
    clearFilter() {
      this.wizardFilterValue = null
      this.$set(this, 'clearAllFilters', true)
    },
  },
}
</script>
