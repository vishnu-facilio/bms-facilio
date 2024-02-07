<script>
import { mapState, mapGetters } from 'vuex'
import {
  isEmpty,
  isArray,
  isObject,
  isUndefined,
} from '@facilio/utils/validation'
import clone from 'lodash/clone'
import cloneDeep from 'lodash/cloneDeep'

const SPACE_FIELDS = ['space', 'visitedSpace', 'resourceIdSpace']
const OPERATOR_CHECK = {
  isEmpty: 1,
  isNotEmpty: 2,
  related: 88,
}

export default {
  created() {
    let { moduleName, singleFieldFilterObj } = this
    if (isUndefined(singleFieldFilterObj)) {
      if (moduleName) {
        let promises = [
          this.$store.dispatch('loadTicketType'),
          this.$store.dispatch('loadAssetType'),
          this.$store.dispatch('loadRoles'),
          this.$store.dispatch('loadAssetDepartment'),
          this.$store.dispatch('loadSites'),
          this.$store.dispatch('loadInventoryCategory'),
          this.$store.dispatch('loadAssetCategory'),
          this.$store.dispatch('loadAlarmSeverity'),
          this.$store.dispatch('loadReadingAlarmCategory'),
          this.$store.dispatch('loadTicketCategory'),
          this.$store.dispatch('loadTicketPriority'),
          this.$store.dispatch('loadTicketStatus', moduleName),
          this.$store.dispatch('loadGroups'),
          this.$store.dispatch('loadUsers'),
        ]

        if (!this.searchMetaInfo) {
          promises.push(this.$store.dispatch('view/loadModuleMeta', moduleName))
        }

        Promise.all(promises)
          .catch(() => {})
          .finally(this.initSearchFields)
      }
    }
  },

  data() {
    return {
      excludeCustomfieldsForSpecificModules: [
        'readingrule',
        'preventivemaintenance',
      ],
      dateTimeFields: [
        'createdTime',
        'dueDate',
        'responseDueDate',
        'actualWorkEnd',
        'acknowledgedTime',
        'warrantyExpiryDate',
        'clearedTime',
        'modifiedTime',
      ],
    }
  },

  computed: {
    ...mapState({
      showSearch: state => state.search.active,
      users: state => state.users,
      groups: state => state.groups,
      viewMetaInfo: state => state.view.metaInfo,
    }),

    ...mapGetters({
      getFilterConfig: 'search/getFilterConfig',
    }),

    metaInfo() {
      return this.viewMetaInfo
    },
    appliedFilters() {
      let { $route } = this
      let {
        query: { search },
      } = $route || {}

      if (!isEmpty(search)) {
        return JSON.parse(this.$route.query.search)
      }
      return null
    },

    filterObjectsData: {
      get() {
        let config = this.getFilterConfig
        return this.$getProperty(config, 'data', null)
      },
      set(value) {
        let config = this.getFilterConfig || {}
        this.$store.dispatch('search/updateFilterConfig', {
          ...config,
          data: value,
        })
      },
    },
  },

  watch: {
    moduleName(newVal, oldVal) {
      if (
        newVal &&
        newVal !== oldVal &&
        isUndefined(this.singleFieldFilterObj)
      ) {
        let { moduleName, excludeCustomfieldsForSpecificModules } = this
        let canAddCustomFields =
          moduleName &&
          !excludeCustomfieldsForSpecificModules.includes(moduleName)

        let payload = {
          moduleName,
          canAddCustomFields,
          includeAllMetaFields: this.includeAllMetaFields,
        }

        if (!this.searchMetaInfo) {
          this.$store.dispatch('view/loadModuleMeta', newVal)
        }
        this.$store.dispatch('loadTicketStatus', newVal)
        this.$store.dispatch('search/init', payload)
      }
    },
  },

  methods: {
    initSearchFields() {
      let { singleFieldFilterObj } = this
      if (isUndefined(singleFieldFilterObj)) {
        if (this.config) {
          let config = cloneDeep(this.config)
          this.$store.dispatch('search/updateFilterConfig', config)
        }

        let {
          excludeCustomfieldsForSpecificModules,
          moduleName,
          metaInfo,
          includeAllMetaFields,
        } = this

        let canAddCustomFields =
          moduleName &&
          !excludeCustomfieldsForSpecificModules.includes(moduleName)

        this.$store
          .dispatch('search/init', {
            moduleName,
            canAddCustomFields,
            includeAllMetaFields,
            metaInfo,
          })
          .finally(() => this.setFilterApplied())
      }
    },

    getFieldObj(currentFilterProxy, fieldName) {
      let { metaInfo } = this

      if (currentFilterProxy.operatorId !== 88) {
        let searchFieldName = fieldName
        if (['resourceIdSpace', 'resourceIdAsset'].includes(fieldName)) {
          searchFieldName = 'space'
        }

        let fieldObj = metaInfo.fields.find(
          field => field.name === searchFieldName
        )

        if (currentFilterProxy.displayType === 'resourceType') {
          this.$set(fieldObj, 'multiple', true)
        }
        if (fieldName === 'siteId') {
          this.$set(fieldObj, 'lookupModule', {
            name: 'site',
            displayName: 'Site',
          })
          this.$set(fieldObj, 'multiple', true)
        } else if (fieldName === 'resourceIdSpace') {
          this.$set(fieldObj, 'lookupModule', {
            name: 'basespace',
            displayName: 'Space',
          })
          this.$set(fieldObj, 'multiple', true)
        } else if (fieldName === 'resourceIdAsset') {
          this.$set(fieldObj, 'lookupModule', {
            name: 'asset',
            displayName: 'Asset',
          })
          this.$set(fieldObj, 'multiple', true)
        }
        return fieldObj
      } else {
        if (fieldName === 'siteId') {
          return { lookupModule: { name: 'site' } }
        }
        return { lookupModule: { name: fieldName } }
      }
    },

    setFilterApplied() {
      let appliedFilters = !isEmpty(this.appliedFilters)
        ? this.appliedFilters
        : {}
      let filters = Object.keys(appliedFilters || {})
      let filterObjectsData = clone(this.filterObjectsData || {})

      Object.entries(filterObjectsData).forEach(([fieldName, filterData]) => {
        let key = filterData.key ? filterData.key : fieldName

        if (filters.includes(key)) {
          let filterObj = this.$getProperty(appliedFilters, key, {})

          if (isArray(filterObj)) filterObj = filterObj[0]

          let { operatorId, value, selectedLabel } = filterObj
          let { multiple = false } = this.getFieldObj({}, 'siteId') || {}

          if (['string', 'number'].includes(filterData.displayType)) {
            filterData.value = !isEmpty(value) ? value[0] : ''
          } else if (filterData.displayType === 'lookup') {
            //filterData.value = !isEmpty(value) ? value[0] : '' // replacement for this is given in below if-else condition
            if (!isEmpty(value) && isArray(value) && value.length > 0) {
              if (!multiple && value.length == 1) {
                filterData.value = value[0]
              } else {
                filterData.value = value
              }
            } else {
              filterData.value = ''
            }
            filterData.selectedLabel = selectedLabel
          } else if (filterData.customdate) {
            if (!isEmpty(value)) {
              let dateRange = value.map(date => parseInt(date))
              filterData.dateRange = dateRange
            }
            filterData.value = operatorId + ''
          } else filterData.value = value

          filterData.operatorId = operatorId
        }
      })

      this.filterObjectsData = {
        ...this.filterObjectsData,
        ...filterObjectsData,
      }
    },

    spaceAssetProps(filterData) {
      this.chooserVisibility = true

      let selectedResources = this.$getProperty(filterData, 'value', []).map(
        id => ({
          id: parseInt(id),
        })
      )

      this.initialValues = {
        isIncludeResource: true,
        selectedResources,
      }
    },

    operatorIdForFields(filterData, fieldName) {
      let operatorId

      if (!isEmpty(filterData.operatorId)) operatorId = filterData.operatorId
      else {
        if (fieldName === 'resourceIdSpace' || fieldName === 'resourceIdAsset')
          operatorId = 15
        else if (SPACE_FIELDS.includes(fieldName)) operatorId = 38
        else if (
          ['subject', 'name', 'title'].includes(fieldName) ||
          this.$getProperty(filterData, 'operator', '') === 'STRING'
        )
          operatorId = 5
        else if (
          ['sourceType', 'alarmType'].includes(fieldName) ||
          ['NUMBER', 'DECIMAL'].includes(
            this.$getProperty(filterData, 'operator', '')
          )
        )
          operatorId = 9
        else if (
          fieldName === 'isAcknowledged' ||
          this.$getProperty(filterData, 'operator', '') === 'BOOLEAN'
        )
          operatorId = 15
        else if (this.$getProperty(filterData, 'operator', '') === 'ENUM')
          operatorId = 54
        else operatorId = 36
      }

      return operatorId
    },

    setOperatorId(fieldData, fieldName, returnId = false) {
      if (isEmpty(fieldName)) {
        fieldName = this.fieldName
      }
      if (isEmpty(fieldData)) {
        fieldData = this.$getProperty(this.filterObjectsData, fieldName, null)
      }

      let operatorId = this.operatorIdForFields(fieldData, fieldName)

      if (returnId) {
        return operatorId
      } else {
        this.$setProperty(fieldData, 'operatorId', operatorId)
      }
    },

    constructDateFilter(filterValue, fieldName) {
      filterValue = filterValue.split('_')

      let operatorId =
        filterValue.length === 1
          ? parseInt(filterValue[0])
          : parseInt(filterValue[1])
      let filter = {
        operatorId,
      }

      if (operatorId === 20) {
        let filterData = this.$getProperty(
          this.filterObjectsData,
          fieldName,
          null
        )
        let dateRange = this.$getProperty(filterData, 'dateRange', null)

        if (isEmpty(dateRange)) return false

        filter.value = dateRange.map(date => date + '')
      } else if ([39, 40, 41, 50, 59, 60, 61].includes(operatorId)) {
        filter.value = [filterValue[0]]
      }

      return filter
    },

    setCustomDateFilters(filters, fieldName) {
      let filterData = this.$getProperty(this.filterObjectsData, fieldName)
      let filterVal = filterData.value
      let key = filterData.key || fieldName
      let filter

      if (isArray(filterVal)) {
        filters[key] = []

        filterVal.forEach(fval => {
          filter = this.constructDateFilter(fval, fieldName)

          if (filter) {
            filters[key].push(filter)
          }
        })
      } else {
        filter = this.constructDateFilter(filterVal, fieldName)

        if (filter) {
          filters[key] = filter
        }
      }
    },

    applyFilter(filterData) {
      if (isEmpty(filterData)) filterData = this.filterObjectsData
      else this.filterObjectsData = filterData

      let filters = this.formatDataForFilter(filterData)

      if (isEmpty(filters)) {
        this.$store.dispatch('search/resetFilters')
      } else {
        this.$store.dispatch('search/applyFilters', filters)
      }

      this.showFilterCondition = false
      this.close()
    },

    formatDataForFilter(filterData) {
      let filters = {}
      for (let fieldName in filterData) {
        let field = filterData[fieldName]
        let key = field.key ? field.key : fieldName

        if (
          !isEmpty(field.value) ||
          [OPERATOR_CHECK.isEmpty, OPERATOR_CHECK.isNotEmpty].includes(
            field.operatorId
          )
        ) {
          if (field.customdate) {
            this.setCustomDateFilters(filters, fieldName)
          } else {
            let filterObj = {
              operatorId: this.setOperatorId(field, fieldName, true),
            }

            if (
              [OPERATOR_CHECK.isEmpty, OPERATOR_CHECK.isNotEmpty].includes(
                field.operatorId
              )
            ) {
              let { operator } =
                this.$getProperty(filterData, fieldName, null) || {}

              if (
                ['subject', 'name', 'title'].includes(fieldName) ||
                (!isEmpty(operator) && operator === 'STRING')
              ) {
                field.value = ''
              } else {
                field.value = []
              }
            }

            if (fieldName === 'assignedTo') {
              let users = [],
                groups = []

              field.value.forEach(user => {
                if (user.indexOf('_group') !== -1) {
                  groups.push(user.substring(0, user.indexOf('_group')))
                } else {
                  users.push(user)
                }
              })

              if (users.length) {
                filterObj.value = users
              }

              if (groups.length) {
                filterObj.value = groups
                key =
                  key === 'assignedToId'
                    ? 'assignmentGroupId'
                    : 'assignmentGroup'
              }

              filters[key] = filterObj
            } else {
              let value

              if (!isArray(field.value)) {
                value = [field.value]
              } else {
                if (isObject(field.value[0])) {
                  field.value[0].value =
                    this.$getProperty(field.value[0], 'value') + ''
                  value = field.value
                } else {
                  value = field.value.map(val => val + '')
                }
              }
              filterObj.value = value

              if (
                SPACE_FIELDS.includes(fieldName) ||
                fieldName === 'asset' ||
                fieldName == 'resourceIdAsset'
              ) {
                key = field.key || fieldName
                if (!filters[key]) {
                  filters[key] = []
                }

                if (isObject(filters[key])) {
                  if (!isEmpty(filters[key].value))
                    filters[key] = {
                      ...filterObj,
                    }
                } else {
                  filters[key].push(filterObj)
                }
              } else {
                filters[key] = filterObj
              }

              if (field.displayType === 'lookup') {
                //Since fieldValue is array of only one object
                if (isArray(filters[key])) filters[key] = filters[key][0]

                let fieldValue = filters[key].value

                let { multiple = false } =
                  this.getFieldObj(field, fieldName) || {}

                if (isArray(fieldValue) && !multiple) fieldValue = fieldValue[0]

                filters[key]['selectedLabel'] = field.selectedLabel
                // filters[key].value = !isEmpty(fieldValue) ? [fieldValue.toString()]: []

                if (isEmpty(fieldValue)) filters[key].value = []
                else if (!multiple) filters[key].value = [fieldValue.toString()]
                else filters[key].value = fieldValue

                if (OPERATOR_CHECK.related === field.operatorId) {
                  filters[key].relatedFieldName = field.relatedFieldName
                }
              }
            }
          }
        }
      }

      return filters
    },

    setLookUpFilter(filter) {
      if (isArray(filter)) {
        let valueArr = []
        filter.forEach(item => {
          let key = item.hasOwnProperty('id') ? 'id' : 'value'
          valueArr.push(item[key])
        })
        this.currentFilterProxy = {
          ...this.currentFilterProxy,
          value: valueArr,
        }
      } else {
        let { value, label: selectedLabel } = filter
        this.currentFilterProxy = {
          ...this.currentFilterProxy,
          value,
          selectedLabel,
        }
      }
      this.showingLookupWizard = false
      this.applyFilter()
    },

    resetFilterData() {
      let appliedFilters = !isEmpty(this.appliedFilters)
        ? this.appliedFilters
        : {}
      let filters = Object.keys(appliedFilters || {})

      Object.entries(this.filterObjectsData || {}).forEach(
        ([fieldName, filterData]) => {
          let key = filterData.key ? filterData.key : fieldName

          if (!filters.includes(key)) {
            filterData.value = isArray(filterData.value) ? [] : ''
            delete filterData.operatorId

            this.filterObjectsData = {
              ...this.filterObjectsData,
              [fieldName]: filterData,
            }
          }
        }
      )
    },
  },
}
</script>
