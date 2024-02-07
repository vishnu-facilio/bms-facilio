<script>
import Search from 'newapp/components/Search'
import { isEmpty, isArray } from '@facilio/utils/validation'
import { isLookupDropDownField } from 'util/field-utils'
import { mapState, mapGetters } from 'vuex'
import cloneDeep from 'lodash/cloneDeep'
import { getFieldOptions } from 'util/picklist'

export default {
  extends: Search,
  props: ['clearFilters', 'singleFieldSearch', 'searchMetaInfo'],
  data() {
    return {
      singleFieldFilterObj: {},
      canShow: false,
    }
  },
  created() {
    this.$store.dispatch('loadTicketStatus', this.moduleName)
    this.setFilterObject()
  },
  computed: {
    ...mapState({
      users: state => state.users,
      ticketStatus: state => state.ticketStatus,
      ticketCategory: state => state.ticketCategory,
      readingAlarmCategory: state => state.readingAlarmCategory,
      assetCategory: state => state.assetCategory,
      assetDepartment: state => state.assetDepartment,
      assetType: state => state.assetType,
      inventoryCategory: state => state.inventoryCategory,
      sites: state => state.sites,
      ticketPriority: state => state.ticketPriority,
      ticketType: state => state.ticketType,
      groups: state => state.groups,
      alarmSeverity: state => state.alarmSeverity,
    }),
    ...mapGetters(['getTicketStatusPickList']),
    showSearch: {
      get() {
        return this.canShow
      },
      set(value) {
        this.canShow = value
      },
    },
    filterObjectsData: {
      get() {
        return this.singleFieldFilterObj
      },
      set(value) {
        this.singleFieldFilterObj = {
          ...this.singleFieldFilterObj,
          ...value,
        }
      },
    },
    metaInfo() {
      return this.searchMetaInfo || this.viewMetaInfo
    },
    hideLookupWizard() {
      let { currentFilterProxy, fieldName } = this
      let { name } = this.getFieldObj(currentFilterProxy, fieldName) || {}

      return name !== 'resource'
    },
  },
  watch: {
    clearFilters(value) {
      if (!isEmpty(value) && value) {
        this.resetFilterData()
        this.close(true)
      }
    },
  },
  methods: {
    async setFilterObject() {
      await Promise.all([
        this.$store.dispatch('loadGroups'),
        this.$store.dispatch('loadUsers'),
      ])

      let config = await this.setFilterConfig()

      this.singleFieldFilterObj = (config || {}).data || {}
      this.fieldName = this.setDefaultFieldName()
    },

    async setFilterConfig() {
      let { moduleName, metaInfo } = this
      let { fields } = metaInfo || []

      let configObj = {
        includeParentCriteria: true,
        data: {},
        availableColumns: [],
        saveView: false,
        moduleName,
        path: ``,
      }
      await Promise.all(
        fields.map(async field => {
          let {
            name,
            dataTypeEnum,
            trueVal,
            falseVal,
            specialType,
            enumMap,
            displayName,
            lookupModule,
          } = field
          let values
          if (
            dataTypeEnum._name == 'DATE' ||
            dataTypeEnum._name == 'DATE_TIME'
          ) {
            values = {
              label: '',
              displayType: 'select',
              dateRange: [],
              type: 'date',
              customdate: true,
              value: '',
              options: {
                '22': 'Today',
                '26': 'Till Yesterday',
                '31': 'This Week',
                '30': 'Last Week',
                '2_50': 'Last 2 Weeks',
                '32': 'Next Week',
                '2_61': 'Next 2 Days',
                '7_61': 'Next 7 Days',
                '28': 'This Month',
                '27': 'Last Month',
              },
            }
          } else {
            values = {
              label: '',
              displayType: 'string',
              value: [],
            }

            if (dataTypeEnum._name == 'STRING') {
              values.displayType = 'string'
              values.operatorId = 5
            } else if (dataTypeEnum._name == 'BOOLEAN') {
              values.displayType = 'select'
              values.type = 'Boolean'
              values.options = {
                true: trueVal || 'Yes',
                false: falseVal || 'No',
              }
              values.value = []
            } else if (dataTypeEnum._name == 'NUMBER') {
              if (field.name === 'siteId') {
                values.displayType = 'select'
                values.options = {}
              } else if (name === 'frequency') {
                let options = cloneDeep(this.$constants.FACILIO_FREQUENCY)
                options['0'] = 'Once'

                values.options = options
                values.displayType = 'select'
              } else {
                values.displayType = 'number'
                values.operatorId = 9
              }
            } else if (dataTypeEnum._name == 'LOOKUP') {
              let error, options

              let lookupModuleName = this.$getProperty(lookupModule, 'name')
              if (lookupModuleName === 'ticketstatus') {
                options = this.getTicketStatusPickList(lookupModuleName)
              } else {
                let response = await getFieldOptions({
                  field: { lookupModule, skipDeserialize: true },
                  page: 1,
                  perPage: 50,
                })
                let {
                  error: responseError,
                  options: responseOptions,
                } = response
                error = responseError
                options = responseOptions
              }

              if (isLookupDropDownField(field)) {
                values.displayType = 'lookup'
              } else {
                values.displayType = 'select'
              }

              if (specialType === 'users') {
                let userOptions = {}

                this.users.forEach(user => {
                  userOptions[user.id] = user.name
                })

                if (
                  ['assignedTo', 'executedBy'].includes(name) &&
                  !isEmpty(this.groups)
                ) {
                  this.groups.forEach(group => {
                    userOptions[group.id + '_group'] = group.name
                  })
                }
                values.specialType === true
                values.displayType = 'select'
                values.options = userOptions
              } else {
                values.options = error ? {} : options
              }
            } else if (dataTypeEnum._name == 'ENUM') {
              values.displayType = 'select'
              values.type = 'enum'
              values.value = []
              values.options = enumMap
            }
          }

          if (
            ['DATE', 'DATE_TIME', 'BOOLEAN', 'ENUM'].includes(
              dataTypeEnum._name
            )
          ) {
            values['key'] = name
          }

          values.label = displayName
          values.default = field.default
          values.operator = dataTypeEnum._name

          if (
            [
              'LOOKUP',
              'NUMBER',
              'ENUM',
              'BOOLEAN',
              'STRING',
              'DATE',
              'DATE_TIME',
            ].includes(dataTypeEnum._name)
          ) {
            // Set only allowed field types
            configObj.data[name] = values
          }
        })
      )
      return configObj
    },

    setDefaultFieldName() {
      let { moduleName, singleFieldFilterObj } = this

      if (moduleName === 'workorder') {
        return 'subject'
      } else {
        let hasNameFilter = singleFieldFilterObj.hasOwnProperty('name')

        return hasNameFilter
          ? 'name'
          : (Object.keys(singleFieldFilterObj) || [])[0]
      }
    },

    setFilterApplied() {},

    resetFilterData() {
      Object.entries(this.filterObjectsData || {}).forEach(
        ([fieldName, filterData]) => {
          filterData.value = isArray(filterData.value) ? [] : ''
          delete filterData.operatorId
          delete filterData.selectedLabel

          this.filterObjectsData = {
            ...this.filterObjectsData,
            [fieldName]: filterData,
          }
        }
      )
      this.$emit('clearedFilters', true)
    },

    search() {
      this.showSearch = true
      this.$nextTick(() => {
        let element = this.$refs['f-search']
        if (!isEmpty(element)) {
          element.focus()
        }
      })
    },

    close(forceClose) {
      if (!this.showingLookupWizard || forceClose) {
        this.showSearch = false
      }
    },

    applyFilter(filterData) {
      if (isEmpty(filterData)) filterData = this.filterObjectsData
      else this.filterObjectsData = filterData

      let filters = this.formatDataForFilter(filterData)

      if (isEmpty(filters)) {
        this.resetFilterData()
      } else if (this.singleFieldSearch) {
        if (!isEmpty(this.$refs['selectDropdown'])) {
          this.$refs.selectDropdown.blur()
        }
        this.$emit('onChange', filters)
      }
    },
  },
}
</script>
