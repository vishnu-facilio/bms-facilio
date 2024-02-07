<template>
  <div>
    <el-dialog
      :visible.sync="showSaveDialog"
      :append-to-body="true"
      :fullscreen="true"
      custom-class="fc-dialog-form fc-dialog-right setup-dialog55 setup-dialog save-dialog"
      :before-close="closeDialog"
      style="z-index: 999999;overflow-y:scroll !important;"
    >
      <div class="wo-save-dialog">
        <p class="setup-modal-title" style="margin:0;">Edit View</p>
        <el-row>
          <el-col :span="13" v-if="!viewDetail.isDefault">
            <el-input v-model="newViewNames" placeholder="Name"></el-input>
          </el-col>
          <el-col :span="13" v-else>
            <el-input
              v-bind:disabled="newViewNames"
              v-model="newViewNames"
              placeholder="Name"
            ></el-input>
          </el-col>
        </el-row>
        <div v-if="!config.disableColumnCustomization">
          <div class="wo-save-subH">CUSTOMIZE COLUMNS</div>
          <div class="wo-save-greytxt">
            Drag and drop columns that you wish to save in this view
          </div>
          <div class="wo-rearrage-container">
            <div class="wo-rearrage-left wo-rearrange-box mR40">
              <p class="rearrange-box-header">
                <span class="rearrange-box-H">AVAILABLE COLUMNS</span>
                <!-- <span class="rearrange-box-sub">Add All</span> -->
              </p>

              <div class="rearrange-box-body">
                <div v-if="moduleMeta">
                  <draggable
                    v-model="allColumns"
                    class="dragArea"
                    :options="{ group: 'people' }"
                  >
                    <div
                      class="rearrange-el-block"
                      v-for="(element, index) in availableColumns"
                      :key="index"
                    >
                      <span
                        class="rearrange-plus-icon"
                        v-on:click="addcolumn(element, index, availableColumns)"
                        style="cursor: pointer;"
                        >+</span
                      >
                      <span class="rearrange-txt">{{ element.label }}</span>
                      <span
                        style="text-align:right;float:right;margin-right: 4px;margin-top: 4px;"
                      >
                        <img
                          src="~assets/move.svg"
                          class="icon-right"
                          style="height: 16px;"
                        />
                      </span>
                    </div>
                  </draggable>
                </div>
              </div>
            </div>
            <div class="wo-rearrage-right wo-rearrange-box">
              <p class="rearrange-box-header">
                <span class="rearrange-box-H">SELECTED COLUMNS</span>
                <!-- <span class="rearrange-box-sub">Reset</span> -->
              </p>
              <div class="rearrange-box-body">
                <div v-if="moduleMeta">
                  <div
                    class="rearrange-el-block"
                    style="border: solid 1px #f0f0f0;"
                    v-for="(col, index) in fixedSelectedCol"
                    :key="index"
                  >
                    <span class="rearrange-txt2" style="opacity: 0.5;">{{
                      col.label
                    }}</span>
                  </div>
                  <!-- <span><i class="el-icon-close rearrange-plus-icon icon-right" style="font-size: 16px;font-weight: 600;"></i></span> -->
                  <draggable
                    v-model="selectedColumns"
                    class="dragArea"
                    :options="{ group: 'people' }"
                  >
                    <div
                      class="rearrange-el-block"
                      v-for="(element, index) in selectedColumns"
                      :key="index"
                    >
                      <span>
                        <i
                          class="el-icon-close rearrange-plus-icon3"
                          style="font-size: 16px;font-weight: 600;cursor: pointer;"
                          v-on:click="
                            removeColumn(element, index, selectedColumns)
                          "
                        ></i>
                      </span>
                      <span class="rearrange-txt2">{{ element.label }}</span>
                      <span
                        style="text-align:right;float:right;margin-right: 4px;margin-top: 4px;"
                      >
                        <img
                          src="~assets/move.svg"
                          class="icon-right"
                          style="height: 16px;"
                        />
                      </span>
                    </div>
                  </draggable>
                </div>
                <!-- <div class="rearrange-el-block3">
 <span><i class="el-icon-close rearrange-plus-icon3" style="font-size: 16px;font-weight: 600;"></i></span>
 <span class="rearrange-txt3">Assigned to</span>
                </div>-->
              </div>
            </div>
          </div>
        </div>
        <div
          v-if="Object.keys(filtersApplies).length > '0'"
          class="wo-save-subH"
        >
          FILTERS
        </div>
        <div
          v-if="Object.keys(filtersApplies).length > '0'"
          class="fc-grey-text-input-label pB10"
        >
          Existing Filters
        </div>
        <div v-if="Object.keys(filtersApplies).length > '0'">
          <new-tag
            :config="configsData"
            :filters="filtersApplies"
            :showCloseIcon="false"
            class="saveas-newtag"
          ></new-tag>
        </div>
        <div v-if="viewDetail.isDefault"></div>
        <div v-else>
          <div class="fc-grey-text-input-label pB10 pT10">New Filters</div>
          <div v-if="showAddFilter || Object.keys(appliedFilters).length > '0'">
            <new-tag
              :config="config"
              :filters="appliedFilters"
              :showFilterAdd="showAddFilter"
              :showCloseIcon="true"
              class="saveas-newtag"
            ></new-tag>
          </div>
        </div>
        <!-- user -->
        <div class="wo-save-subH">SHARING PERMISSIONS</div>
        <!-- radio user -->
        <el-row align="middle" style="margin:0px;" :gutter="20">
          <el-col :span="24" style="padding-right: 35px;padding-left:0px;">
            <div class="add">
              <el-radio v-model="shareTo" :label="1" class="newradio"
                >Only Me</el-radio
              >
              <el-radio v-model="shareTo" :label="2" class="newradio"
                >Everyone</el-radio
              >
              <el-radio v-model="shareTo" :label="3" class="newradio"
                >Specific</el-radio
              >
            </div>
          </el-col>
        </el-row>
        <!-- radio user -->
        <!-- Team -->
        <el-row v-if="shareTo === 3" class="mT30 el-select-block">
          <el-col :span="24">
            <label class="sharing-label">Team</label>
            <el-select
              v-model="sharedGroups"
              multiple
              collapse-tags
              class="width60"
              :placeholder="$t('common.wo_report.choose_users')"
            >
              <el-option
                v-for="group in groups"
                :key="group.id"
                :label="group.name"
                :value="group.id"
              ></el-option>
            </el-select>
          </el-col>
        </el-row>

        <el-row v-if="shareTo === 3" class="mT30 el-select-block">
          <el-col :span="24">
            <label class="sharing-label">Role</label>
            <el-select
              v-model="sharedRoles"
              multiple
              collapse-tags
              class="width60"
              :placeholder="$t('common.wo_report.choose_roles')"
            >
              <el-option
                v-for="role in roles"
                :key="role.id"
                :label="role.name"
                :value="role.id"
              ></el-option>
            </el-select>
          </el-col>
        </el-row>

        <el-row v-if="shareTo === 3" class="mT30 el-select-block">
          <el-col :span="24">
            <label class="sharing-label">Staff</label>
            <el-select
              v-model="sharedUsers"
              multiple
              collapse-tags
              class="width60"
              :placeholder="$t('common.wo_report.choose_teams')"
            >
              <el-option
                v-for="user in users"
                :key="user.id"
                :label="user.name"
                :value="user.id"
              ></el-option>
            </el-select>
          </el-col>
        </el-row>
      </div>
      <span slot="footer" class="dialog-footer modal-footer-fixed">
        <el-button @click="showSaveDialog = false" class="modal-btn-cancel"
          >Cancel</el-button
        >
        <el-button
          type="primary"
          @click="saveAsNewView()"
          class="modal-btn-save"
          >Save</el-button
        >
      </span>
    </el-dialog>
  </div>
</template>
<script>
import { mapActions, mapGetters, mapState } from 'vuex'
import ViewMixinHelper from '@/mixins/ViewMixin'
import { getFieldOptions } from 'util/picklist'
import FilterMixinHelper from '@/mixins/FilterMixin'
import { isEmpty } from '@facilio/utils/validation'
import draggable from 'vuedraggable'
import NewTag from '@/NewTag'

export default {
  mixins: [FilterMixinHelper, ViewMixinHelper],
  props: [
    'newViewName',
    'share',
    'selectedColumn',
    'config',
    'showSaveDialog',
    'modName',
  ],
  data() {
    return {
      allColumns: [],
      selectedColumns: [],
      nonSelectableColumns: [
        'assignmentGroup',
        'asset',
        'week',
        'hour',
        'month',
        'day',
        'localId',
        'photoId',
        'space',
        'hideToCustomer',
        'parentAssetId',
        'moduleState',
        'stateFlowId',
        'id',
      ],
      configsData: {},
      availableColumns: [],
      loading: false,
      showAddFilter: true,
      filterApplied: false,
      newViewNames: this.newViewName,
      // showSaveDialog: false,
      selectedResourceName: null,
      chooserVisibility: false,
      shareTo: this.share,
      filtersApplies: {},
      sharedGroups: [],
      sharedUsers: [],
      sharedRoles: [],
      viewSharing: [],
    }
  },
  watch: {
    allFields: {
      handler(val) {
        this.allColumns = this.$helpers.cloneObject(val)
        this.checkColumns()
        this.setOptions()
      },
      deep: true,
    },
    appliedFilters(val) {
      if (val) {
        this.filterApplied = true
      } else {
        if (this.filterApplied) {
          this.reset(null, true)
        }
      }
    },
    share(val, oldVal) {
      this.shareTo = this.share
      this.$emit('update:newViewName', val)
    },
    newViewName(val, oldVal) {
      this.init()
      this.newViewNames = this.newViewName
      this.$emit('update:newViewName', val)
    },
    selectedColumn(val, oldVal) {
      this.init()
      this.$emit('update:selectedColumn', val)
    },
    viewColumns: {
      handler() {
        this.availableColumns = []
        this.selectedColumns = []
        this.checkColumns()
      },
    },
    shareTo(val, oldVal) {
      if (val === 3) {
        this.sharedUsers.push(this.userDetails.id)
      }
    },
    showSaveDialog(val, oldVal) {
      this.$emit('update:showSaveDialog', val)
    },
    viewFilters() {
      this.init()
      this.reset(null, true)
    },
  },
  mounted() {
    this.allColumns = this.$helpers.cloneObject(this.allFields)
    this.init()
    this.checkColumns()
  },
  created() {
    Promise.all([
      this.$store.dispatch('loadTicketCategory'),
      this.$store.dispatch('loadTicketStatus', 'workorder'),
      this.$store.dispatch('loadTicketType'),
      this.$store.dispatch('loadTicketPriority'),
      this.$store.dispatch('loadAssetCategory'),
      this.$store.dispatch('loadAssetDepartment'),
      this.$store.dispatch('loadAssetType'),
      this.$store.dispatch('loadAlarmSeverity'),
      this.$store.dispatch('loadRoles'),
      this.$store.dispatch('loadGroups'),
    ]).then(() => this.setOptions())
  },
  computed: {
    ...mapState({
      users: state => state.users,
      groups: state => state.groups,
      ticketstatus: state => state.ticketStatus.workorder,
      ticketpriority: state => state.ticketPriority,
      ticketcategory: state => state.ticketCategory,
      alarmseverity: state => state.alarmSeverity,
      assetcategory: state => state.assetCategory,
      assetdepartment: state => state.assetDepartment,
      assettype: state => state.assetType,
      moduleMeta: state => state.view.metaInfo,
      viewDetail: state => state.view.currentViewDetail,
      userDetails: state => state.account.user,
      roles: state => state.roles,
    }),
    ...mapActions({
      loadModuleMeta: 'view/loadModuleMeta',
      loadViews: 'view/loadViews',
      loadGroupViews: 'view/loadGroupViews',
      loadViewDetail: 'view/loadViewDetail',
      savesorting: 'view/savesorting',
    }),
    ...mapGetters(['getTicketTypePickList', 'getCurrentUser']),
    tickettype() {
      return this.getTicketTypePickList()
    },
    appliedFilters() {
      if (this.$route.query.search) {
        return JSON.parse(this.$route.query.search)
      }
      return null
    },
    allFields() {
      let obj = []
      if (this.moduleMeta && this.moduleMeta.fields) {
        obj = this.moduleMeta.fields
          .filter(field => {
            return (
              (this.config.availableColumns ||
                this.config.availableColumns.indexOf(field.name) !== -1 ||
                !field.default) &&
              (!this.config.fixedCols ||
                !this.config.fixedCols.includes(field.name))
            )
          })
          .map((field, idx) => {
            return {
              id: field.id,
              label: field.columnDisplayName || field.displayName,
              key: field.name || '',
            }
          })
      }
      return obj
    },
    fixedSelectedCol() {
      return this.config.fixedCols && this.moduleMeta.fields
        ? this.moduleMeta.fields
            .filter(field => this.config.fixedCols.includes(field.name))
            .map(field => ({
              id: field.id,
              label: field.columnDisplayName || field.displayName,
            }))
        : []
    },
    includeParentFilter() {
      return (
        this.$route.query.includeParentFilter &&
        (this.$route.query.includeParentFilter === 'true' ||
          this.$route.query.includeParentFilter === true)
      )
    },
    currentView() {
      return this.$route.params.viewname || null
    },
    viewFilters() {
      return this.viewDetail ? this.viewDetail.filters : null
    },
  },
  components: {
    draggable,
    NewTag,
  },
  methods: {
    ...mapActions({
      editView: 'view/editView',
    }),
    newView() {
      if (this.viewDetail.isDefault === false) {
        this.saveAsNewView()
      } else {
        this.saveAsNewView()
      }
    },
    editOldView() {
      this.apply()
      this.editViews()
    },
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
            this.setCustomDateFilters(filters, fieldName, this.config)
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
      this.showSaveDialog = false
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
      this.availableColumns.push(element)
      selectedColumns.splice(index, 1)
    },
    saveAsNewView() {
      let self = this
      if (!this.newViewNames) {
        return
      }
      if (!this.selectedColumns.length) {
        return
      }
      this.applySharing()
      let fields = null
      if (!this.config.disableColumnCustomization) {
        let columns = [...this.fixedSelectedCol, ...this.selectedColumns]
        fields = columns.map(col => ({
          fieldId: col.id,
          columnDisplayName: col.label,
          fieldName: col.key || '',
        }))
      }
      let a1
      if (this.modName) {
        a1 = this.modName
      } else {
        a1 = this.config.moduleName
      }
      let data = {
        moduleName: a1,
        view: {
          displayName: this.newViewNames,
          fields,
          id: this.viewDetail.id,
          includeParentCriteria: true,
          name: this.viewDetail.name,
          viewSharing: this.viewSharing,
        },
        parentView: this.currentView,
      }

      let finalFilteredJsonWithParentFilters = {}
      if (this.filtersApplies) {
        finalFilteredJsonWithParentFilters = {
          ...this.filtersApplies,
          ...this.appliedFilters,
        }
      } else {
        finalFilteredJsonWithParentFilters = this.appliedFilters
      }
      data.view.filtersJson = JSON.stringify(finalFilteredJsonWithParentFilters)

      this.editView(data)
        .then(data => {
          this.resetValues()
          // this.$router.replace({
          //   path: this.config.path + data.view.name,
          //   query: {},
          // })
          this.showSaveDialog = false
          this.$message.success('View Edited successfully!')
        })
        .catch(error => {
          this.$message.error(error.message)
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
    setCustomDateFilters(filters, fieldName, config) {
      let constructDateFilter = val => {
        val = val.split('_')
        let operatorId = val.length === 1 ? parseInt(val[0]) : parseInt(val[1])
        let filter = {
          operatorId: operatorId,
        }
        if (operatorId === 20) {
          let dateRange = config.data[fieldName].dateRange
          if (!dateRange || !dateRange.length) {
            return false
          }
          filter.value = config.data[fieldName].dateRange.map(
            date => this.$helpers.getTimeInOrg(date) + ''
          )
        } else if (this.isDateOperatorWithValue(operatorId)) {
          filter.value = [val[0]]
        }
        return filter
      }

      let filterVal = config.data[fieldName].value
      let key = config.data[fieldName].key || fieldName
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
      if (data.issuedTo) {
        data.issuedTo.options = userOptions
      }
      if (data.issuedBy) {
        data.issuedBy.options = userOptions
      }
      if (data.requestedBy) {
        data.requestedBy.options = userOptions
      }
      if (data.requestedFor) {
        data.requestedFor.options = userOptions
      }
      if (data.status) {
        if (!isEmpty(this.ticketstatus)) {
          this.ticketstatus.forEach(status => {
            data.status.options[status.id] = status.displayName
          })
        }
      }
      if (data.category) {
        this.loadPickList('ticketcategory', data.category)
      }
      if (data.tenant) {
        this.loadPickList('tenant', data.tenant)
      }
      if (data.itemType) {
        this.loadPickList('itemTypes', data.itemType)
      }
      if (data.toolType) {
        this.loadPickList('toolTypes', data.toolType)
      }
      if (data.storeRoom) {
        this.loadPickList('storeRoom', data.storeRoom)
      }
      if (data.fromStore) {
        this.loadPickList('storeRoom', data.fromStore)
      }
      if (data.toStore) {
        this.loadPickList('storeRoom', data.toStore)
      }
      if (data.vendor) {
        this.loadPickList('vendors', data.vendor)
      }
      if (data.priority) {
        if (!isEmpty(this.ticketpriority)) {
          this.ticketpriority.forEach(prior => {
            data.priority.options[prior.id] = prior.displayName
          })
        }
      }
      if (data.acknowledgedBy) {
        data.acknowledgedBy.options = userOptions
      }
      if (data.clearedBy) {
        data.clearedBy.options = userOptions
      }
      if (data.severity) {
        if (!isEmpty(this.alarmseverity)) {
          this.alarmseverity.forEach(severity => {
            data.severity.options[severity.id] = severity.severity
            if (data.previousSeverity) {
              data.previousSeverity.options[severity.id] = severity.severity
            }
          })
        }
      }
      if (data.severity) {
        if (!isEmpty(this.alarmseverity)) {
          this.alarmseverity.forEach(severity => {
            data.severity.options[severity.id] = severity.severity
          })
        }
      }
      if (data.ticketType) {
        if (!isEmpty(this.tickettype)) {
          data.ticketType.options = this.tickettype
        }
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
          this.resetValues()
          // this.$router.replace({
          //   path: this.config.path + data.view.name,
          //   query: {},
          // })
          this.showSaveDialog = false
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
      this.newViewNames = ''
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
    applyFilter() {
      let filters = {}
      this.filtersApplies = {}
      for (let fieldName in this.configsData.data) {
        if (fieldName === 'rule') {
          this.configsData.data.rule.push({
            moduleName: 'alarmRaeding',
          })
        }
        let field = this.configsData.data[fieldName]
        let key = field.key || fieldName
        if (
          (field.operatorId === 1 || field.operatorId === 2 || field.value) &&
          (field.operatorId === 1 ||
            field.operatorId === 2 ||
            !Array.isArray(field.value) ||
            field.value.length)
        ) {
          if (field.customdate) {
            this.setCustomDateFilters(filters, fieldName, this.configsData)
          } else {
            let filterObj = {
              operatorId:
                field.operatorId ||
                (fieldName === 'space'
                  ? 38
                  : fieldName === 'subject' ||
                    fieldName === 'name' ||
                    fieldName === 'title'
                  ? 5
                  : 36),
            }
            if (field.operatorId === 1 || field.operatorId === 2) {
              if (
                fieldName === 'subject' ||
                fieldName === 'name' ||
                fieldName === 'title'
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
      if (Object.keys(filters).length) {
        this.filtersApplies = null
        this.filtersApplies = filters
        if (this.filtersApplies) {
          this.appliedFilters += this.filtersApplies
        }
      }
    },
    init() {
      let filters = Object.assign({}, this.viewFilters)
      this.filtersApplies = {}
      this.configsData = this.$helpers.cloneObject(this.config)
      let self = this
      let fieldKeyMap = {}
      for (let fieldName in this.configsData.data) {
        if (
          this.configsData.data.hasOwnProperty(fieldName) &&
          this.configsData.data[fieldName].key
        ) {
          fieldKeyMap[this.configsData.data[fieldName].key] = fieldName
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
              this.configsData.data.space || this.configsData.data.asset
                ? {
                    parent: 'resource',
                  }
                : null
            isResourceField = true
          } else {
            field =
              this.configsData.data[fieldName] ||
              this.configsData.data[fieldKeyMap[fieldName]]
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
                      ? this.configsData.data.space
                      : this.configsData.data.asset
                }

                let val = self.getValuesFromFilter(
                  field,
                  fval,
                  fieldKeyMap[fieldName] || fieldName
                )
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
                    ? this.configsData.data.space
                    : this.configsData.data.asset
              }
              filterVal = self.getValuesFromFilter(
                field,
                filter,
                fieldKeyMap[fieldName] || fieldName
              )
            }

            if (!Array.isArray(filter) || !isResourceField) {
              self.setFieldValue(filterVal, field, fieldKey)
            }
          }
        }
      }
      let { viewDetail } = this
      if (!isEmpty(viewDetail)) {
        let { viewSharing } = viewDetail
        if (this.shareTo === 3) {
          for (let i = 0; i < viewSharing.length; i++) {
            if (!isEmpty(viewSharing[i].typeEnum)) {
              if (viewSharing[i].typeEnum === 'USER') {
                this.sharedUsers.push(viewSharing[i].userId)
              } else if (viewSharing[i].typeEnum === 'ROLE') {
                this.sharedRoles.push(viewSharing[i].roleId)
              } else if (viewSharing[i].typeEnum === 'GROUP') {
                this.sharedGroups.push(viewSharing[i].groupId)
              }
            }
          }
        }
      }
      this.applyFilter()
    },
    checkColumns() {
      this.selectedColumns = []
      for (let j in this.allColumns) {
        let k = false
        for (let i in this.viewColumns) {
          if (this.allColumns[j].label === this.viewColumns[i].displayName) {
            this.selectedColumns.push(this.allColumns[j])
            k = true
          }
        }
        if (!k && !this.nonSelectableColumns.includes(this.allColumns[j].key)) {
          this.availableColumns.push(this.allColumns[j])
        }
      }
    },
  },
}
</script>
<style>
.fc-subheader-container {
  width: 100%;
  height: 60px;
  background: #ffffff;
  -moz-box-sizing: border-box;
  box-sizing: border-box;
  -webkit-box-sizing: border-box;
  padding: 20px 30px;
}

.fc-subheader-left {
  display: flex;
  flex-direction: row;
  position: relative;
}

.fc-subheader-right {
  display: flex;
  flex-direction: row;
  height: 0;
  margin-right: 12px;
}

.fc-subheader-right-search {
  position: relative;
  bottom: 11px;
  right: -2px;
}

.create-btn {
  margin-top: -10px;
}

.subheader-tab {
  background: #fff;
  height: 2px;
}

.subheader-tab.active {
  background: #ef508f;
  margin-top: 8px;
  width: 20px;
}

.wo-three-line {
  position: relative;
  top: 2px;
}

/* .el-dialog__body.list-dialog-form {
 padding: 250px 20px;
 color: #606266;
 font-size: 14px;
} */
/* .el-dialog__body {
 padding: 30px 20px;
 color: #606266;
 font-size: 14px;
} */
.individual-view {
  position: relative;
  text-transform: capitalize;
  padding-left: 10px;
  padding-right: 10px;
  font-size: 14px;
  color: #333333;
  height: 40px;
}

.individual-view:hover {
  background-color: #f1f8fa;
  border-radius: 3px;
}

.individual-view .el-icon-edit {
  position: absolute;
  right: 8px;
  top: 10px;
}

.individual-view .customEdit {
  position: absolute;
  right: 30px;
  top: 10px;
}

.wo-list-block {
  margin-bottom: 20px;
}

.view-dialog-body .el-dialog__body {
  padding-top: 10px;
}

.view-dialog-body .el-dialog__title {
  padding-left: 10px;
}

.border-right-remove:last-child {
  border-right: none;
}

.fc-dropdown-menu {
  font-weight: 500;
  color: #2d2d52;
}

.comment-close {
  position: relative;
  top: 0;
  right: 10px;
  opacity: 0.3;
  color: #000000;
}

.el-icon-edit.pointer.edit-icon.visibility-hide-actions.fR.editview {
  position: absolute;
  right: 38px;
  top: 10px;
}

.save-btn-separator {
  font-size: 0;
  color: TRANSPARENT;
  padding-right: 0;
  padding-left: 0;
  border-left: 1px SOLID #efefef;
  margin-left: 10px;
  margin-right: 10px;
}

.subheader-saveas-btn:hover {
  background-color: rgba(125, 103, 184, 0.1);
  color: #372668;
}

/* .sort-icon {
 width: 17px;
 background-color: #ffffff;
 margin-top: 6px;
 }
 .sort {
 margin-top: -16px;
 } */
.sort-icon {
  margin-top: -7px;
}

.pagination {
  padding-top: 13px;
}

.delete-icon {
  position: absolute;
  right: 6px;
  top: 10px;
}

.sh-selection-bar {
  border: 1px solid #ee518f;
  width: 25px;
  margin-top: 5px;
  position: absolute;
}

.filter-search-close {
  font-size: 18px;
  position: absolute;
  right: 85px;
  top: 10px;
  color: #8ca1ad;
}

.sort-icon-hover:hover {
  fill: #e7328a;
  transition: fill 0.5s ease;
}

.save-btn-section {
  width: 17%;
  position: absolute;
  top: 29px;
  right: 22px;
  text-align: right;
}

.subheader-saveas-btn {
  background: #39b2c2;
  border-radius: 3px;
  font-size: 10px;
  font-weight: 500;
  letter-spacing: 0.3px;
  color: #ffffff;
  padding: 5px;
}

.subheader-saveas-btn:hover {
  background: #33a6b5;
  color: #fff;
}

.clear-filter {
  font-size: 13px;
  letter-spacing: 0.5px;
  color: #748893;
  float: right;
  margin-left: 10px;
  margin-top: 5px;
  cursor: pointer;
}

.dp-saveas-btn {
  background-color: #39b2c2;
  border-color: #39b2c2;
  padding: 6px 4px;
  font-size: 10px;
  font-weight: 500;
  border-radius: 4px;
  letter-spacing: 0.3px;
  color: #ffffff;
}

.dp-saveas-btn:hover {
  background: #33a6b5;
  color: #fff;
}

.search-icon-set {
  height: 80%;
}

.filter-search-clear {
  position: absolute;
  right: 63px;
  color: #8ca1ad;
  font-size: 15px;
  font-weight: 500;
  padding: 2px;
  top: 10px;
  cursor: pointer;
}

.filter-search-clear:hover {
  background: #d0d9e2;
  color: #8ca1ad !important;
  transition: 0.2s all;
  padding: 2px;
  border-radius: 50px;
  -webkit-transition: 0.2s all;
}

.rearrange-box-body .dragArea {
  min-height: 100px;
}

.new-search-date-filter {
  border-radius: 0;
  margin-top: 0;
}

.f-search-popover .el-select-dropdown__list {
  padding-bottom: 70px;
}
</style>
