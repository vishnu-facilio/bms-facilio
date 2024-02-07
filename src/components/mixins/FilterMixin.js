import { mapActions, mapState, mapGetters } from 'vuex'
import { getFieldOptions } from 'util/picklist'

export default {
  computed: {
    ...mapState({
      metaInfo: state => state.view.metaInfo,
    }),
    ...mapGetters(['getCurrentUser']),
  },
  created() {
    this.$store.dispatch('loadTicketStatus', 'workorder')
    this.$store.dispatch('loadTicketType')
    this.$store.dispatch('loadAssetCategory')
    this.$store.dispatch('loadAssetDepartment')
    this.$store.dispatch('loadAssetType')
    this.$store.dispatch('loadAlarmSeverity')
    this.$store.dispatch('loadRoles')
  },
  methods: {
    ...mapActions({
      saveNewView: 'view/saveNewView',
      editView: 'view/editView',
    }),
    apply() {
      this.$emit('hideSearch')
      let filters = {}
      for (let fieldName in this.config.data) {
        if (fieldName === 'rule') {
          this.config.data.rule.push({
            moduleName: 'alarmRaeding',
          })
        }
        let field = this.config.data[fieldName]
        let key = field.key || fieldName
        if (
          (field.operatorId === 1 || field.operatorId === 2 || field.value) &&
          (field.operatorId === 1 ||
            field.operatorId === 2 ||
            !Array.isArray(field.value) ||
            field.value.length)
        ) {
          if (field.customdate) {
            this.setCustomDateFilters(filters, fieldName)
          } else {
            let filterObj = {
              operatorId:
                field.operatorId ||
                (fieldName === 'space' ? 38 : fieldName === 'subject' ? 5 : 36),
            }
            if (field.operatorId === 1 || field.operatorId === 2) {
              if (fieldName === 'subject') {
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
              filterObj.value = !Array.isArray(field.value)
                ? [field.value]
                : field.value.map(val => val + '')
              if (fieldName === 'space' || fieldName === 'asset') {
                key = field.key || 'resource'
                if (!filters[key]) {
                  filters[key] = []
                }
                filters[key].push(filterObj)
              } else {
                filters[key] = filterObj
              }
            }
          }
        }
      }
      if (Object.keys(filters).length > '0') {
        let queryParam = {
          search: JSON.stringify(filters),
        }
        if (this.config.includeParentCriteria) {
          queryParam.includeParentFilter = true
        }
        this.$router.replace({
          query: queryParam,
        })
      }
      this.filterApplied = true
      this.$refs.select1.blur()
    },
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    reset(event, resetValueOnly) {
      this.filterApplied = false
      for (let field in this.config.data) {
        let data = this.config.data[field]
        data.value = data.single ? '' : []
      }
      // setting height back to normal for select tags
      let elem = document.getElementById('new-view-filter')
      if (elem) {
        elem.querySelectorAll('.el-input__inner').forEach(node => {
          node.style.removeProperty('height')
        })
      }
      if (!resetValueOnly && this.appliedFilters) {
        this.$router.replace({
          path: this.$route.path,
          query: {},
        })
      }
      // this.init()
    },
    addcolumn(element, index, allColumns) {
      this.selectedColumns.push(element)
      allColumns.splice(index, 1)
    },
    removeColumn(element, index, selectedColumns) {
      this.allColumns.push(element)
      selectedColumns.splice(index, 1)
    },
    saveAsNewView() {
      let self = this
      if (!this.newViewName) {
        return
      }
      if (
        !this.config.disableColumnCustomization &&
        !this.selectedColumns.length
      ) {
        return
      }
      this.applySharing()
      let fields = null
      if (!this.config.disableColumnCustomization) {
        let columns = [...this.fixedSelectedCol, ...this.selectedColumns]
        fields = columns.map(col => ({
          fieldId: col.id,
          columnDisplayName: col.label,
        }))
      }
      this.saveNewView({
        moduleName: this.config.moduleName,
        view: {
          displayName: this.newViewName,
          fields,
          includeParentCriteria: true,
          filtersJson: JSON.stringify(this.appliedFilters),
          viewSharing: this.viewSharing,
        },
        parentView: this.currentView,
      })
        .then(function(data) {
          self.showSaveDialog = false
          self.resetValues()
          self.$nextTick(() => {
            self.$router.replace({
              path: self.config.path + data.name,
              query: {},
            })
          })
          self.$message({
            message: 'View created successfully!',
            type: 'success',
          })
        })
        .catch(() => {
          self.$message({
            message: 'View creation failed!',
            type: 'error',
          })
        })
    },
    applySharing: function() {
      let self = this
      this.viewSharing = []
      if (self.shareTo === 1) {
        this.viewSharing.push({
          type: 1,
          userId: self.getCurrentUser().ouid,
        })
      } else if (self.shareTo === 3) {
        if (self.sharedUsers.length > 0) {
          this.viewSharing.push({
            type: 1,
            userId: self.getCurrentUser().ouid,
          })
          for (let i = 0; i < self.sharedUsers.length; i++) {
            if (self.sharedUsers[i] !== self.getCurrentUser().ouid) {
              this.viewSharing.push({
                type: 1,
                userId: self.sharedUsers[i],
              })
            }
          }
        }
        if (self.sharedRoles.length > 0) {
          for (let i = 0; i < self.sharedRoles.length; i++) {
            this.viewSharing.push({
              type: 2,
              roleId: self.sharedRoles[i],
            })
          }
        }
        if (self.sharedGroups.length > 0) {
          for (let i = 0; i < self.sharedGroups.length; i++) {
            this.viewSharing.push({
              type: 3,
              groupId: self.sharedGroups[i],
            })
          }
        }
      }
    },
    onFilterChanged(value) {
      this.apply()
      if (this.filterApplied) {
        this.filterApplied = false
      }
    },
    isDateOperatorWithValue(operatorId) {
      // TODO get value from date operator
      return [39, 40, 41, 50, 59, 60, 61].includes(operatorId)
    },
    async loadPickList(moduleName, field) {
      this.picklistOptions = {}

      let { error, options } = await getFieldOptions({
        field: { lookupModuleName: moduleName, skipDeserialize: true },
      })

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.$set(field, 'options', options)
      }
    },
    setCustomDateFilters(filters, fieldName) {
      let constructDateFilter = val => {
        val = val.split('_')
        let operatorId = val.length === 1 ? parseInt(val[0]) : parseInt(val[1])
        let filter = {
          operatorId: operatorId,
        }
        if (operatorId === 20) {
          let dateRange = this.config.data[fieldName].dateRange
          if (!dateRange || !dateRange.length) {
            return false
          }
          filter.value = this.config.data[fieldName].dateRange.map(
            date => this.$helpers.getTimeInOrg(date) + ''
          )
        } else if (this.isDateOperatorWithValue(operatorId)) {
          filter.value = [val[0]]
        }
        return filter
      }

      let filterVal = this.config.data[fieldName].value
      let key = this.config.data[fieldName].key || fieldName
      if (Array.isArray(filterVal)) {
        filters[key] = []
        filterVal.forEach(fval => {
          let filter = constructDateFilter(fval)
          if (filter) {
            filters[key].push(filter)
          }
        })
      } else {
        let filter = constructDateFilter(filterVal)
        if (filter) {
          filters[key] = filter
        }
      }
    },
    formatOptions(field, list) {
      let data = this.config.data
      if (data[field]) {
        list.forEach(val => {
          this.$set(data[field].options, val.id, val.name)
        })
      }
    },
    associateResource(resource, field) {
      this.$set(field, 'chooserVisibility', false)
      field.value = resource.resourceList
        ? resource.resourceList.map(res => res.id)
        : []
      this.onFilterChanged()
      this.apply()
    },
    setFieldValue(filterVal, field, fieldKey) {
      if (filterVal) {
        if (fieldKey === 'assignmentGroup' || fieldKey === 'assignedTo') {
          filterVal =
            fieldKey === 'assignmentGroup'
              ? filterVal.filter(id => id !== '-100').map(id => id + '_group')
              : filterVal
          if (field.value && !field.single) {
            field.value = [...field.value, ...filterVal]
          } else {
            field.value = filterVal
          }
        } else {
          field.value = filterVal
        }
      }
    },
    setOptions() {
      let data = this.config.data
      let userOptions = {}
      this.users.forEach(user => {
        userOptions[user.id] = user.name
      })
      if (data.assignedTo) {
        data.assignedTo.options = userOptions
        this.groups.forEach(group => {
          data.assignedTo.options[group.id + '_group'] = group.name
        })
      }

      if (data.status) {
        this.ticketstatus.forEach(status => {
          data.status.options[status.id] = status.displayName
        })
      }
      if (data.category) {
        this.loadPickList('ticketcategory', data.category)
      }
      if (data.priority) {
        this.loadPickList('ticketpriority', data.priority)
      }
      if (data.acknowledgedBy) {
        data.acknowledgedBy.options = userOptions
      }
      if (data.clearedBy) {
        data.clearedBy.options = userOptions
      }
      if (data.severity) {
        this.alarmseverity.forEach(severity => {
          data.severity.options[severity.id] = severity.severity
        })
      }
      if (data.ticketType) {
        data.ticketType.options = this.tickettype
      }
      if (data.frequency) {
        data.frequency.options = Object.assign(
          {},
          {
            0: 'Once',
          },
          this.$constants.FACILIO_FREQUENCY
        )
      }

      this.formatOptions('assetCategory', this.assetcategory)
      this.formatOptions('assetDepartment', this.assetdepartment)
      this.formatOptions('assetType', this.assettype)
    },
    editViews() {
      let data = {
        moduleName: this.config.moduleName,
        view: {
          name: this.viewDetail.name,
          id: this.viewDetail.id,
        },
      }
      if (this.appliedFilters) {
        data.view.filtersJson = JSON.stringify(this.appliedFilters)
      }

      this.editView(data)
        .then(data => {
          this.showSaveDialog = false
          this.resetValues()
          this.$nextTick(() => {
            this.$router.replace({
              path: this.config.path + data.view.name,
              query: {},
            })
          })
          this.$message.success('View Edited successfully!')
          this.subheaderMenu()
        })
        .catch(() => {
          this.$message.error('View creation failed!')
        })
    },
    queryHandler() {
      this.query = null
      this.reset()
    },
    resetValues() {
      this.filterApplied = false
      this.allColumns = this.$helpers.cloneObject(this.allFields)
      this.selectedColumns = []
      this.newViewName = ''
      this.sharedGroups = []
      this.sharedUsers = []
      this.sharedRoles = []
      this.viewSharing = []
    },
    getLookupValue(field, filter, fieldName) {
      // TODO needs to check more operators and support multiple lookups
      // TODO needs to get operatorid from metainfo
      let name = Object.keys(filter.value)[0]
      let lookupFilter = filter.value[name]

      let operatorId = lookupFilter.operatorId
      let valuesToCheck = lookupFilter.value // The values to check whether to insert or not based on operator
      let optionsToSelect = [] // Options that needs to be pre-selected. (The values which will be returned)
      let idsArr = Object.keys(this.config.data[fieldName].options) // All the option values for the field

      if (name === 'typeCode' && fieldName === 'status') {
        valuesToCheck = ['Closed']
        if (lookupFilter.value[0] === '1') {
          operatorId = 4
        }
      }

      idsArr.forEach(id => {
        // TODO check more operator ids
        if (valuesToCheck.includes(this.config.data[fieldName].options[id])) {
          if (operatorId !== 4) {
            optionsToSelect.push(id)
          }
        } else if (operatorId === 4) {
          optionsToSelect.push(id)
        }
      })
      return optionsToSelect
    },
    getValuesFromFilter(field, filter, fieldName) {
      let value
      // TODO temporary...needs to improve
      if (filter.operatorId === 35) {
        value = this.getLookupValue(field, filter, fieldName)
      } else if (filter.operatorId === 15 || field.operatorId === 15) {
        value = filter.value && filter.value[0] === 'true' ? 'true' : 'false'
      } else if (
        field.customdate &&
        (!filter.value || !filter.value.length) &&
        field.options[filter.operatorId]
      ) {
        value = field.single ? filter.operatorId + '' : [filter.operatorId + '']
      } else if (filter.value) {
        if (field.type === 'date' && filter.operatorId === 20) {
          value = '20'
          field.dateRange = filter.value
        } else if (this.isDateOperatorWithValue(filter.operatorId)) {
          value = filter.value[0] + '_' + filter.operatorId
          value = field.single ? value : [value]
        } else if (filter.operatorId === 10 || field.operatorId === 10) {
          let options = this.config.data[fieldName].options
          let idsArr = Object.keys(options)
          value = []
          idsArr.forEach(id => {
            if (!filter.value.includes(id)) {
              value.push(id)
            }
          })
        } else {
          value = field.single ? filter.value[0] : filter.value
        }
      }
      return value
    },
    init() {
      if (this.appliedFilters && this.includeParentFilter !== true) {
        this.filterApplied = true
        if (Object.keys(this.appliedFilters).length === 0) {
          return
        }
      } else if (this.includeParentFilter) {
        this.filterApplied = true
      }
      let filters = Object.assign({}, this.viewFilters, this.appliedFilters)
      let self = this

      let fieldKeyMap = {}
      for (let fieldName in this.config.data) {
        if (
          this.config.data.hasOwnProperty(fieldName) &&
          this.config.data[fieldName].key
        ) {
          fieldKeyMap[this.config.data[fieldName].key] = fieldName
        }
      }

      for (let fieldKey in filters) {
        if (
          filters.hasOwnProperty(fieldKey) ||
          fieldKey === 'assignmentGroup'
        ) {
          let filter = filters[fieldKey]
          let fieldName =
            fieldKey === 'assignmentGroup' ? 'assignedTo' : fieldKey
          let field
          let isResourceField = false
          if (fieldName === 'resource' || fieldName === 'resourceId') {
            field =
              this.config.data.space || this.config.data.asset
                ? {
                    parent: 'resource',
                  }
                : null
            isResourceField = true
          } else {
            field =
              this.config.data[fieldName] ||
              this.config.data[fieldKeyMap[fieldName]]
          }
          if (field) {
            let filterVal
            if (Array.isArray(filter)) {
              filterVal = []
              filter.forEach(fval => {
                if (isResourceField) {
                  filterVal = []
                  field =
                    fval.operatorId === 38
                      ? this.config.data.space
                      : this.config.data.asset
                }

                let val = self.getValuesFromFilter(field, fval, fieldName)
                if (Array.isArray(val)) {
                  filterVal = [...filterVal, ...val]
                } else {
                  field.operatorId === 15
                    ? (filterVal = val)
                    : filterVal.push(val)
                }

                if (isResourceField) {
                  self.setFieldValue(filterVal, field, fieldKey)
                }
              })
            } else {
              if (isResourceField) {
                field =
                  filter.operatorId === 38
                    ? this.config.data.space
                    : this.config.data.asset
              }
              filterVal = self.getValuesFromFilter(field, filter, fieldName)
            }

            if (!Array.isArray(filter) || !isResourceField) {
              self.setFieldValue(filterVal, field, fieldKey)
            }
          }
        }
      }
    },
  },
}
