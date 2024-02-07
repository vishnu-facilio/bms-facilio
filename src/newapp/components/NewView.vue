<template>
  <div>
    <el-dialog
      :visible="showSaveDialog"
      :append-to-body="true"
      :fullscreen="true"
      custom-class="fc-dialog-form fc-dialog-right setup-dialog55 setup-dialog save-dialog"
      :before-close="closeDialog"
      style="z-index: 999999;overflow-y:scroll !important;"
    >
      <div class="wo-save-dialog">
        <p v-if="isNewView" class="setup-modal-title" style="margin:0;">
          Save as New View
        </p>
        <p v-else class="setup-modal-title" style="margin:0;">Edit View</p>
        <el-row>
          <el-col
            :span="13"
            v-if="(viewDetail && !viewDetail.isDefault) || isNewView"
          >
            <el-input v-model="newViewNames" placeholder="Name"></el-input>
          </el-col>
          <el-col :span="13" v-else>
            <el-input
              v-bind:disabled="!$validation.isEmpty(newViewNames)"
              v-model="newViewNames"
              placeholder="Name"
            ></el-input>
          </el-col>
        </el-row>
        <el-row>
          <div class="wo-save-subH">FOLDER NAME</div>
          <el-col :span="13">
            <el-select
              v-model="groupId"
              placeholder="Select the group"
              class="fc-input-full-border2 width100"
              filterable
            >
              <el-option
                v-for="(group, index) in groupViewsList"
                :key="index"
                :label="group.displayName"
                :value="group.id"
              ></el-option>
            </el-select>
          </el-col>
        </el-row>
        <div v-if="!configData.disableColumnCustomization">
          <div class="wo-save-subH">CUSTOMIZE COLUMNS</div>
          <div class="wo-save-greytxt">
            Drag and drop columns that you wish to save in this view
          </div>
          <div class="wo-rearrage-container">
            <div class="wo-rearrage-left wo-rearrange-box mR40">
              <p class="rearrange-box-header">
                <span class="rearrange-box-H">AVAILABLE COLUMNS</span>
              </p>

              <div class="rearrange-box-body">
                <div v-if="metaInfo">
                  <draggable
                    v-model="allColumns"
                    class="dragArea"
                    :group="'people'"
                  >
                    <div
                      class="rearrange-el-block"
                      v-for="(element, index) in availableColumns"
                      :key="index"
                    >
                      <span
                        class="rearrange-plus-icon"
                        v-on:click="addcolumn(element, index)"
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
              </p>
              <div class="rearrange-box-body">
                <div v-if="metaInfo">
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
                  <draggable
                    v-model="selectedColumns"
                    class="dragArea"
                    :group="'people'"
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
                          v-on:click="removeColumn(element, index)"
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
              </div>
            </div>
          </div>
        </div>
        <div
          v-if="!$validation.isEmpty(viewFilters) || appliedFilters !== null"
          class="wo-save-subH"
        >
          FILTERS
        </div>
        <div v-if="!$validation.isEmpty(viewFilters)">
          <div class="fc-grey-text-input-label pB10">
            Existing Filters
          </div>
          <tag
            :filter="viewFilters"
            :disableFilterAdd="true"
            :disableCloseIcon="true"
            :disableActions="true"
            class="saveas-newtag"
          ></tag>
        </div>
        <div v-if="viewDetail && viewDetail.isDefault && !isNewView"></div>
        <div v-else>
          <div class="fc-grey-text-input-label pB10 pT10">New Filters</div>
          <div>
            <tag
              :disableActions="true"
              :showOptions="true"
              class="saveas-newtag"
            ></tag>
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
              filterable
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
              filterable
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
              filterable
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
        <el-button @click="closeDialog()" class="modal-btn-cancel"
          >Cancel</el-button
        >
        <el-button type="primary" @click="saveView()" class="modal-btn-save"
          >Save</el-button
        >
      </span>
    </el-dialog>
  </div>
</template>
<script>
import { mapActions, mapState, mapGetters } from 'vuex'
import ViewMixinHelper from '@/mixins/ViewMixin'
import { isEmpty, isArray } from '@facilio/utils/validation'
import draggable from 'vuedraggable'
import Tag from 'newapp/components/Tags'
import SearchTagMixin from 'newapp/components/SearchTagMixin'
import cloneDeep from 'lodash/cloneDeep'
import { deepCloneObject } from 'util/utility-methods'

export default {
  name: 'SaveAsNewView',
  mixins: [ViewMixinHelper, SearchTagMixin],
  components: {
    draggable,
    Tag,
  },

  data() {
    return {
      allColumns: [],
      selectedColumns: [],
      configData: {},
      availableColumns: [],
      newViewNames: '',
      shareTo: 2,
      sharedGroups: [],
      sharedUsers: [],
      sharedRoles: [],
      viewSharing: [],
      groupId: null,
    }
  },

  created() {
    this.configData = this.getFilterConfig || {}

    Promise.all([
      this.$store.dispatch('loadRoles'),
      this.$store.dispatch('loadGroups'),
    ])
    this.init()
  },
  computed: {
    ...mapState({
      users: state => state.users,
      groups: state => state.groups,
      viewDetail: state => state.view.currentViewDetail,
      userDetails: state => state.account.user,
      roles: state => state.roles,
      showSaveDialog: state => state.search.openViewDialog,
      view: state => state.search.viewToEdit,
      isNewView: state => state.search.newView,
      saveWithExistingViewFilter: state =>
        state.search.saveWithExistingViewFilter,
    }),

    ...mapGetters('search', ['getFilterConfig']),
    ...mapGetters('view', ['getViewsFolderList']),
    groupViewsList() {
      let { getViewsFolderList } = this
      return deepCloneObject(getViewsFolderList())
    },

    allFields() {
      let list = []
      let { configData = {}, metaInfo = {}, $getProperty } = this

      if ($getProperty(metaInfo, 'fields') && !isEmpty(configData)) {
        list = (metaInfo.fields || [])
          .filter(field => {
            let { availableColumns = [], fixedCols = [] } = configData || {}

            return (
              (availableColumns ||
                availableColumns.includes(field.name) ||
                !field.default) &&
              (!fixedCols || !fixedCols.includes(field.name))
            )
          })
          .map(field => {
            return {
              id: field.id,
              label: field.columnDisplayName || field.displayName,
              key: field.name || '',
            }
          })
      }
      return list
    },

    fixedSelectedCol() {
      return this.configData.fixedCols && this.metaInfo.fields
        ? this.metaInfo.fields
            .filter(field => this.configData.fixedCols.includes(field.name))
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
      if (this.$route.params.viewname) {
        return this.$route.params.viewname
      }
      return null
    },

    viewFilters() {
      return this.viewDetail ? this.viewDetail.filters : null
    },
  },

  watch: {
    allFields: {
      handler(val) {
        this.allColumns = val ? cloneDeep(val) : []
        this.checkColumns()
      },
      deep: true,
    },

    viewColumns() {
      this.availableColumns = []
      this.selectedColumns = []
      this.checkColumns()
    },

    shareTo(val) {
      if (val === 3) {
        this.sharedUsers.push(this.userDetails.id)
      }
    },

    view: {
      handler(value) {
        if (!isEmpty(value)) this.edit(value)
      },
      immediate: true,
    },

    viewFilters() {
      this.resetFilterData()
      this.init()
    },

    saveWithExistingViewFilter: {
      handler(value) {
        if (value) this.confirmationEditViews()
      },
      immediate: true,
    },

    showSaveDialog() {
      this.resetValues()
      this.configData = this.getFilterConfig
      this.init()
    },
  },

  methods: {
    ...mapActions({
      saveNewView: 'view/saveNewView',
      editView: 'view/editView',
    }),

    edit(view) {
      if (
        isEmpty(view.moduleName) ||
        ['newreadingalarm'].includes(view.moduleName)
      )
        view.moduleName = this.configData.moduleName

      let moduleName = view.moduleName

      this.$store
        .dispatch('view/loadModuleMeta', moduleName)
        .then(() => {
          this.$router.push(view.name)
          this.selectedColumns = []

          let url = '/view/' + view.name + '?moduleName=' + moduleName

          this.$http.get(url).then(({ data }) => {
            this.newViewNames = data.displayName
            let { viewSharing } = data

            if (!isEmpty(viewSharing)) {
              viewSharing = viewSharing.filter(
                viewSharingContext => viewSharingContext.typeEnum != 'APP'
              )
              let sharingTypeLength = viewSharing.length
              if (sharingTypeLength > 1) {
                this.shareTo = 3
              } else if (
                sharingTypeLength === 1 &&
                viewSharing[0].userId === this.$account.user.ouid
              ) {
                this.shareTo = 1
              } else {
                this.shareTo = 2
              }
            } else {
              this.shareTo = 2
            }
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

            if (data.fields) {
              data.fields.forEach(field => {
                let fieldObj = {
                  id: this.$getProperty(field.field, 'fieldId', field.fieldId),
                  label: this.$getProperty(
                    field.field,
                    'displayName',
                    field.columnDisplayName
                  ),
                }

                if (isEmpty(field.field)) {
                  fieldObj.fieldName = field.fieldName
                }

                this.selectedColumns.push(fieldObj)
              })
            }

            this.$router.push(data.name)
            this.$store.dispatch('search/updateViewDialogState', true)
          })
        })
        .catch(() => {})
      this.$store.dispatch('search/updateViewToEdit', null)
    },

    closeDialog() {
      this.$store.dispatch('search/updateViewDialogState', false)
    },

    addcolumn(element, index) {
      this.selectedColumns.push(element)
      this.availableColumns.splice(index, 1)
    },

    removeColumn(element, index) {
      this.availableColumns.push(element)
      this.selectedColumns.splice(index, 1)
    },

    saveView() {
      if (this.isNewView) this.saveAsNewView()
      else this.editViews()
    },

    saveAsNewView() {
      let { groupId } = this
      if (!this.validateViewData()) {
        return
      }

      this.applySharing()

      let data = this.constructViewData()

      data.view = Object.assign(data.view, {
        includeParentCriteria: true,
        filtersJson: JSON.stringify(this.appliedFilters),
        groupId,
      })

      this.saveNewView(data)
        .then(data => {
          this.closeDialog()

          this.resetValues()
          this.$router.push({
            path: this.configData.path + data.name,
            query: {},
          })
          this.$store.dispatch('view/loadGroupViews', {
            moduleName: this.configData.moduleName,
          })
          this.$message({
            message: 'View created successfully!',
            type: 'success',
          })
        })
        .catch(() => {
          this.$message({
            message: 'View creation failed!',
            type: 'error',
          })
        })
    },

    editViews() {
      let { groupId } = this
      if (!this.validateViewData()) {
        return
      }

      this.applySharing()

      let data = this.constructViewData()

      data.view = Object.assign(data.view, {
        id: this.viewDetail.id,
        name: this.viewDetail.name,
        groupId,
      })

      this.saveFiltersInExistingView(data)
    },

    validateViewData() {
      let { groupId } = this
      if (!this.newViewNames) {
        this.$message({
          message: 'Please name this view',
          type: 'error',
        })
        return false
      }
      if (isEmpty(groupId)) {
        this.$message({
          message: 'Please select a folder',
          type: 'error',
        })
        return false
      }

      let { isNewView, configData, selectedColumns } = this

      if (isNewView) {
        if (
          !configData.disableColumnCustomization &&
          isEmpty(selectedColumns)
        ) {
          this.$message({
            message: 'Please select atleast one customize column',
            type: 'error',
          })
          return false
        }
      } else if (isEmpty(selectedColumns)) {
        this.$message({
          message: 'Please select atleast one customize column',
          type: 'error',
        })
        return false
      }

      return true
    },

    constructViewData() {
      let fields = null

      if (!this.configData.disableColumnCustomization) {
        let columns = [...this.fixedSelectedCol, ...this.selectedColumns]

        fields = columns.map(col => ({
          fieldId: col.id,
          columnDisplayName: col.label,
          fieldName: col.key || '',
        }))
      }
      let data = {
        moduleName: this.configData.moduleName,
        view: {
          displayName: this.newViewNames,
          fields,
          viewSharing: this.viewSharing,
        },
        parentView: this.currentView,
      }

      return data
    },

    applySharing() {
      let currentUser = this.$account.user.ouid

      this.viewSharing = []
      if (this.shareTo === 1) {
        this.viewSharing.push({
          type: 1,
          userId: currentUser,
        })
      } else if (this.shareTo === 3) {
        if (!isEmpty(this.sharedUsers)) {
          this.viewSharing.push({
            type: 1,
            userId: currentUser,
          })

          this.sharedUsers.forEach(user => {
            if (user !== currentUser) {
              this.viewSharing.push({
                type: 1,
                userId: user,
              })
            }
          })
        }
        if (!isEmpty(this.sharedRoles)) {
          this.sharedRoles.forEach(role => {
            this.viewSharing.push({
              type: 2,
              roleId: role,
            })
          })
        }
        if (!isEmpty(this.sharedGroups)) {
          this.sharedGroups.forEach(group => {
            this.viewSharing.push({
              type: 3,
              groupId: group,
            })
          })
        }
      }
    },

    setFieldValue(filterVal, field, fieldKey) {
      if (filterVal) {
        if (['assignmentGroup', 'assignedTo'].includes(fieldKey)) {
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

    confirmationEditViews() {
      this.$dialog
        .confirm({
          title: 'Edit View',
          message: 'Are you sure you want to EDIT the view?',
          rbDanger: true,
          rbLabel: 'Edit',
        })
        .then(value => {
          if (value) this.saveFiltersInExistingView()
          this.$store.dispatch('search/updateSaveExistingView', false)
        })
    },

    saveFiltersInExistingView(data) {
      if (isEmpty(data)) {
        data = {
          moduleName: this.configData.moduleName,
          view: {
            name: this.viewDetail.name,
            id: this.viewDetail.id,
          },
        }
      }

      let finalFilteredJsonWithParentFilters = {}

      if (this.viewFilters) {
        finalFilteredJsonWithParentFilters = {
          ...this.viewFilters,
          ...this.appliedFilters,
        }
      } else {
        finalFilteredJsonWithParentFilters = this.appliedFilters
      }

      data.view.filtersJson = JSON.stringify(finalFilteredJsonWithParentFilters)

      this.editView(data)
        .then(data => {
          this.closeDialog()
          this.resetValues()
          this.$router.push({
            path: this.configData.path + data.view.name,
            query: {},
          })
          this.$message.success('View Edited successfully!')
        })
        .catch(error => {
          this.$message.error(error.message)
        })
    },

    resetValues() {
      this.allColumns = cloneDeep(this.allFields)
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
      let filterObj = this.$getProperty(this.filterObjectsData, fieldName, {})
      let idsArr = Object.keys(this.$getProperty(filterObj, 'options', {})) // All the option values for the field

      if (name === 'typeCode' && fieldName === 'status') {
        valuesToCheck = ['Closed']
        if (lookupFilter.value[0] === '1') {
          operatorId = 4
        }
      }

      idsArr.forEach(id => {
        // TODO check more operator ids
        if (
          valuesToCheck.includes(this.filterObjectsData[fieldName].options[id])
        ) {
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
        isEmpty(filter.value) &&
        field.options[filter.operatorId]
      ) {
        value = field.single ? filter.operatorId + '' : [filter.operatorId + '']
      } else if (filter.value) {
        if (field.type === 'date' && filter.operatorId === 20) {
          value = '20'
          field.dateRange = filter.value
        } else if ([39, 40, 41, 50, 59, 60, 61].includes(filter.operatorId)) {
          value = filter.value[0] + '_' + filter.operatorId
          value = field.single ? value : [value]
        } else if (filter.operatorId === 10 || field.operatorId === 10) {
          let options = this.filterObjectsData[fieldName].options
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
      let filters = cloneDeep(this.viewFilters) || {}
      let fieldKeyMap = {}

      for (let fieldName in this.filterObjectsData) {
        let key = this.$getProperty(
          this.filterObjectsData[fieldName],
          'key',
          null
        )

        if (key) {
          fieldKeyMap[fieldName] = key
        }
      }

      for (let fieldKey in filters) {
        let filter = filters[fieldKey]
        let fieldName = fieldKey === 'assignmentGroup' ? 'assignedTo' : fieldKey
        let field
        let isResourceField = false

        if (['resource', 'resourceId'].includes(fieldName)) {
          field =
            this.filterObjectsData.space || this.filterObjectsData.asset
              ? {
                  parent: 'resource',
                }
              : null
          isResourceField = true
        } else {
          let { filterObjectsData = {}, $getProperty } = this

          field = $getProperty(filterObjectsData, fieldName)
          if (!field) {
            field = $getProperty(fieldKeyMap, fieldName)
              ? $getProperty(
                  filterObjectsData,
                  $getProperty(fieldKeyMap, fieldName)
                )
              : null
          }
        }

        if (field) {
          let filterVal

          if (isArray(filter)) {
            filterVal = []

            filter.forEach(fval => {
              if (isResourceField) {
                filterVal = []
                field =
                  fval.operatorId === 38
                    ? this.filterObjectsData.space
                    : this.filterObjectsData.asset
              }

              let val = this.getValuesFromFilter(
                field,
                fval,
                fieldKeyMap[fieldName] || fieldName
              )

              if (isArray(val)) {
                filterVal = [...filterVal, ...val]
              } else {
                field.operatorId === 15
                  ? (filterVal = val)
                  : filterVal.push(val)
              }

              if (isResourceField) {
                this.setFieldValue(filterVal, field, fieldKey)
              }
            })
          } else {
            if (isResourceField) {
              field =
                filter.operatorId === 38
                  ? this.filterObjectsData.space
                  : this.filterObjectsData.asset
            }
            filterVal = this.getValuesFromFilter(
              field,
              filter,
              fieldKeyMap[fieldName] || fieldName
            )
          }

          if (!isArray(filter) || !isResourceField) {
            this.setFieldValue(filterVal, field, fieldKey)
          }
        }
      }
    },
    checkColumns() {
      let nonSelectableColumns = [
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
      ]

      this.selectedColumns = []

      this.allColumns.forEach(column => {
        let isColumnSelected = false

        this.viewColumns.forEach(viewColumn => {
          if (column.label === viewColumn.displayName) {
            this.selectedColumns.push(column)
            isColumnSelected = true
          }
        })

        if (!isColumnSelected && !nonSelectableColumns.includes(column.key)) {
          this.availableColumns.push(column)
        }
      })
    },
  },
}
</script>
<style>
.view-dialog-body .el-dialog__body {
  padding-top: 10px;
}

.view-dialog-body .el-dialog__title {
  padding-left: 10px;
}

.save-btn-section {
  width: 17%;
  position: absolute;
  top: 29px;
  right: 22px;
  text-align: right;
}

.search-icon-set {
  height: 80%;
}

.rearrange-box-body .dragArea {
  min-height: 100px;
}

.saveas-newtag .layout-new-tag {
  margin-top: 0px;
}
</style>
