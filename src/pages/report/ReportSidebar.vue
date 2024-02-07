<template>
  <div>
    <div class="reports-sidebar">
      <div class="reports-sidebar-header">
        <div
          size="mini"
          split-button
          type="text"
          class="fc-black-13"
          @command="handleNewCommand"
        >
          {{ $t('common.wo_report.all_reports') }}
        </div>
        <div class="pull-right mR10" v-if="createPermission">
          <button
            class="sh-button button-add  shadow-1"
            style="padding: 6px 9px;"
            @click="createNewReport"
            v-if="(isModuleReport || isPivot) && !$helpers.isPortalUser()"
          >
            <i
              class="el-icon-plus"
              style="font-weight: 700;font-size: 12px;"
            ></i>
          </button>
        </div>
        <div
          class="pull-right hide"
          style="margin-right: 10px;margin-top: 7px;"
        >
          <el-dropdown @command="handleNewCommand">
            <button
              class="sh-button button-add  shadow-1"
              style="padding: 6px 9px;"
            >
              <i
                class="el-icon-plus"
                style="font-weight: 700;font-size: 12px;"
              ></i>
            </button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="report">{{
                $t('common.wo_report.new_report')
              }}</el-dropdown-item>
              <el-dropdown-item command="matrix">{{
                $t('common.wo_report.new_matrix_report')
              }}</el-dropdown-item>
              <el-dropdown-item command="reportfolder">{{
                $t('common.wo_report.new_report_folder')
              }}</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
      <div class="rtree">
        <div class="report-sidebar-scroll">
          <spinner v-if="loading" :show="loading" />
          <div v-else>
            <div
              class="p15 flex-middle height80vh justify-content-center flex-direction-column"
              v-if="!newReportTree.length && !reportTree.length"
            >
              <div>
                <inline-svg
                  src="svgs/emptystate/reportlist"
                  iconClass="icon text-center icon-xxxlg"
                ></inline-svg>
              </div>
              <div class="nowo-label">
                {{ $t('common.wo_report.no_reports') }}
              </div>
            </div>
            <div
              v-if="newReportTree"
              v-for="(folder, index2) in newReportTree"
              :key="'new_' + index2"
              class="folder-container"
            >
              <div
                class="rfolder-name text-capitalize report-new-folder newreport-folder-name"
                @click="expand(folder)"
              >
                <div class="rfolder-icon fL">
                  <i class="el-icon-arrow-down" v-if="folder.expand"></i>
                  <i class="el-icon-arrow-right" v-else></i>
                </div>
                <div>
                  <div
                    v-if="folder.id !== editFolderId"
                    class="mL5 label-txt-black bold"
                  >
                    {{ folder.name }}
                  </div>
                  <input
                    v-else
                    ref="FolderName"
                    @keyup.enter="editFolderName(folder)"
                    @blur="editFolderName(folder)"
                    v-model="folderState[folder.id].newName"
                  />
                </div>
              </div>
              <div class="edit-icon-set">
                <i
                  @click="toggleEditFolder(folder)"
                  class="el-icon-edit icon-display pointer"
                ></i>
                <i
                  class="el-icon-delete icon-display pointer"
                  @click="deleteReportFolder(folder)"
                ></i>
                <i
                  class="el-icon-share icon-display pointer"
                  @click="showPermissionDialig(folder)"
                ></i>
              </div>
              <draggable
                v-model="folder.reports"
                class="rfolder-children"
                v-show="folder.expand"
                :options="{ group: 'report' }"
              >
                <div class="rempty" v-if="!folder.reports.length">
                  -- {{ $t('common.wo_report.no_reports') }} --
                </div>
                <div
                  v-for="(report, ridx1) in folder.reports"
                  :key="'new_' + ridx1"
                  class="folder-list-txt"
                >
                  <div style="width: 100%" @click="loadReport(report.id, true)">
                    <a class="label-txt-black pT15 pB15 pL50 pR10">{{
                      report.name
                    }}</a>
                    <el-popover
                      trigger="click"
                      placement="right"
                      v-model="folderState[folder.id].setting"
                      popper-class="folder-popover"
                      width="250"
                    >
                      <ul class="ul-folder-list">
                        <div
                          class="label-txt-black border-bottom1px pT10 pB10 pL10 bold"
                        >
                          {{ $t('common.wo_report.move_to') }}
                        </div>
                        <li
                          @click="moveReport(folder, folder1, report)"
                          v-for="(folder1, index1) in newReportTree.filter(
                            rm => rm.name !== folder.name
                          )"
                          :key="index1"
                          class="pointer"
                        >
                          {{ folder1.name }}
                        </li>
                      </ul>
                      <i
                        slot="reference"
                        @click="folderState[folder.id].setting = true"
                        class="q-icon material-icons cursor-pointer"
                        style="float:right; font-size: 20px; color: rgb(216, 216, 216);padding-top: 3px;"
                        >more_vert</i
                      >
                    </el-popover>
                  </div>
                </div>
              </draggable>
            </div>
            <div v-for="(folder, index) in reportTree" :key="index">
              <div class="rfolder-name uppercase" @click="expand(folder)">
                <div class="rfolder-icon">
                  <i class="el-icon-arrow-down" v-if="folder.expand"></i>
                  <i class="el-icon-arrow-right" v-else></i>
                </div>
                {{ folder.label }}
              </div>
              <draggable
                v-model="folder.children"
                class="rfolder-children"
                v-show="folder.expand"
                :options="{ group: 'report' }"
              >
                <div class="rempty" v-if="!folder.children.length">
                  -- {{ $t('common.wo_report.no_reports') }} --
                </div>
                <template v-for="(report, ridx) in folder.children">
                  <div
                    :key="ridx"
                    class="label-txt-black pT15 pB15 pL50 pR10"
                    @click="loadReport(report.widget.dataOptions.reportId)"
                  >
                    {{ report.label }}
                  </div>
                </template>
              </draggable>
            </div>
            <div v-if="!$helpers.isPortalUser()" class="scheduled-viewall">
              <div @click="openScheduledReports()">
                {{ $t('common.wo_report.view_all_scheduled_reports') }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div v-if="showDialog">
      <f-dialog
        v-if="showDialog"
        :visible.sync="showDialog"
        width="30%"
        @save="addFolderPermission()"
        @close="showDialog = false"
        :confirmTitle="$t('common._common._save')"
        :stayOnSave="true"
        customClass="qr-dialog"
      >
        <div class="fc-setup-modal-title">
          {{ $t('common.wo_report.sharing_permission') }}
        </div>
        <div class="mT10">
          <div>
            <el-radio
              v-model="shareTo"
              :label="1"
              class="fc-radio-btn pB10 pT10"
              >{{ $t('common.wo_report.only_me') }}</el-radio
            >
          </div>
          <div>
            <el-radio v-model="shareTo" :label="2" class="fc-radio-btn pB10">{{
              $t('common.wo_report.everyone')
            }}</el-radio>
          </div>
          <div>
            <el-radio v-model="shareTo" :label="3" class="fc-radio-btn pB10">{{
              $t('common.wo_report.specific')
            }}</el-radio>
          </div>
          <el-row v-if="shareTo === 3" class="mT20 el-select-block">
            <el-col :span="24">
              <div class="label-txt-black pB5">
                {{ $t('common.wo_report.team') }}
              </div>
              <el-select
                filterable
                v-model="sharedGroups"
                multiple
                collapse-tags
                class="width100 fc-full-border-select-multiple2"
                :placeholder="$t('common.wo_report.choose_users')"
              >
                <el-option
                  v-for="group in groups"
                  :key="group.id"
                  :label="group.name"
                  :value="group.id"
                >
                </el-option>
              </el-select>
            </el-col>
          </el-row>

          <el-row v-if="shareTo === 3" class="mT20 el-select-block">
            <el-col :span="24">
              <div class="label-txt-black pB5">
                {{ $t('common.wo_report.role') }}
              </div>
              <el-select
                filterable
                v-model="sharedRoles"
                multiple
                collapse-tags
                class="width100 fc-full-border-select-multiple2"
                :placeholder="$t('common.wo_report.choose_roles')"
              >
                <el-option
                  v-for="role in roles"
                  :key="role.id"
                  :label="role.name"
                  :value="role.id"
                >
                </el-option>
              </el-select>
            </el-col>
          </el-row>

          <el-row v-if="shareTo === 3" class="mT20 el-select-block">
            <el-col :span="24">
              <div class="label-txt-black pB5">
                {{ $t('common.wo_report.staff') }}
              </div>
              <el-select
                filterable
                v-model="sharedUsers"
                multiple
                collapse-tags
                class="width100 fc-full-border-select-multiple2"
                :placeholder="$t('common.wo_report.choose_teams')"
              >
                <el-option
                  v-for="user in users"
                  :key="user.id"
                  :label="user.name"
                  :value="user.id"
                >
                </el-option>
              </el-select>
            </el-col>
          </el-row>
        </div>
        <div style="padding: 25px 0px;"></div>
      </f-dialog>
    </div>
  </div>
</template>
<script>
import draggable from 'vuedraggable'
import ReportHelper from 'pages/report/mixins/ReportHelper'
import FDialog from '@/FDialogNew'
import { mapState, mapGetters } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import {
  isWebTabsEnabled,
  findRouteForTab,
  findRouteForReport,
  pageTypes,
} from '@facilio/router'

export default {
  props: ['isPivot', 'webTabId'],
  mixins: [ReportHelper],
  components: {
    draggable,
    FDialog,
  },
  data() {
    return {
      loading: true,
      shareTo: 2,
      reportSharing: [],
      sharedGroups: [],
      sharedUsers: [],
      sharedRoles: [],
      reportTree: [],
      showDialog: false,
      newReportTree: [],
      newFolderName: '',
      folderState: {},
      editFolderId: null,
      reportFolderId: null,
      ids: [],
    }
  },
  created() {
    this.$store.dispatch('loadRoles')
    this.$store.dispatch('loadGroups')
    this.$store.dispatch('loadUsers')
  },
  watch: {
    moduleName: {
      handler() {
        this.loadNewReportTree()
        this.loadReportTree()
      },
      immediate: true,
    },
  },
  computed: {
    ...mapState({
      groups: state => state.groups,
      users: state => state.users,
      roles: state => state.roles,
    }),

    ...mapGetters(['getCurrentUser']),
    moduleName() {
      let { module: moduleName } = this.$route.meta || {}

      if (isEmpty(moduleName)) moduleName = this.$attrs.moduleName

      return moduleName
    },

    createPermission() {
      let { moduleName } = this

      if (!isWebTabsEnabled() || !moduleName) {
        moduleName = (this.getCurrentModule() || {}).module
      }
      if (moduleName === 'energydata') moduleName = 'energy'

      if (isWebTabsEnabled() && moduleName) {
        return this.$hasPermission(moduleName + ':CREATE_EDIT')
      }
      if (['workorder', 'alarm', 'energy'].includes(moduleName)) {
        return this.$hasPermission(moduleName + ':CREATE_EDIT_REPORTS')
      }
      return true
    },

    isModuleReport() {
      let { moduleName } = this
      if (!isWebTabsEnabled() || !moduleName) {
        moduleName = (this.getCurrentModule() || {}).module
      }
      return moduleName && moduleName !== 'energydata'
    },

    reportId() {
      let reportId = this.$attrs.reportid || this.$route.params.reportid

      return parseInt(reportId)
    },
  },
  methods: {
    showPermissionDialig(folder) {
      this.sharedGroups = []
      this.sharedUsers = []
      this.sharedRoles = []
      if (folder && folder.reportSharing && folder.reportSharing.length > 0) {
        if (folder.reportSharing.length > 1) {
          this.shareTo = 3
          for (let sharing of folder.reportSharing) {
            if (sharing.type === 1) {
              this.sharedUsers.push(sharing.userId)
            } else if (sharing.type === 2) {
              this.sharedRoles.push(sharing.roleId)
            } else if (sharing.type === 3) {
              this.sharedGroups.push(sharing.groupId)
            }
          }
        } else if (
          folder.reportSharing.length === 1 &&
          folder.reportSharing[0].userId === this.getCurrentUser().ouid
        ) {
          this.shareTo = 1
        }
        for (let i = 0; i < folder.reportSharing.length; i++) {
          this.ids.push(folder.reportSharing[i].id)
        }
      } else {
        this.shareTo = 2
      }
      this.reportFolderId = folder.id
      this.showDialog = true
    },
    addFolderPermission() {
      let self = this
      let moduleName = this.getCurrentModule().module
      this.applySharing()
      let apiUrl = null
      if (typeof moduleName !== 'undefined' && moduleName) {
        apiUrl = '/v3/report/folder/permission/update?moduleName=' + moduleName
      } else {
        apiUrl = '/v3/report/folder/permission/update?moduleName=energydata'
      }
      return new Promise((resolve, reject) => {
        let a = {
          reportFolder: {
            reportSharing: this.reportSharing,
            ids: this.ids,
          },
          folderId: this.reportFolderId,
        }
        API.put(apiUrl, a)
          .then(response => {
            if (!response.error) {
              self.loadNewReportTree()
              self.showDialog = false
            }
          })
          .catch(function(error) {
            if (error) {
              reject(error)
            }
          })
      })
    },
    applySharing: function() {
      let self = this
      this.reportSharing = []
      if (self.shareTo === 1) {
        this.reportSharing.push({
          type: 1,
          userId: self.getCurrentUser().ouid,
        })
      } else if (self.shareTo === 3) {
        if (self.sharedUsers.length > 0) {
          this.reportSharing.push({
            type: 1,
            userId: self.getCurrentUser().ouid,
          })
          for (let i = 0; i < self.sharedUsers.length; i++) {
            if (self.sharedUsers[i] !== self.getCurrentUser().ouid) {
              this.reportSharing.push({
                type: 1,
                userId: self.sharedUsers[i],
              })
            }
          }
        }
        if (self.sharedRoles.length > 0) {
          for (let i = 0; i < self.sharedRoles.length; i++) {
            this.reportSharing.push({
              type: 2,
              roleId: self.sharedRoles[i],
            })
          }
        }
        if (self.sharedGroups.length > 0) {
          for (let i = 0; i < self.sharedGroups.length; i++) {
            this.reportSharing.push({
              type: 3,
              groupId: self.sharedGroups[i],
            })
          }
        }
      }
    },
    toggleEditFolder(folder) {
      this.editFolderId = folder.id
      this.$nextTick(() => {
        if (Array.isArray(this.$refs['FolderName'])) {
          this.$refs['FolderName'][0].focus()
        } else {
          this.$refs['FolderName'].focus()
        }
      })
    },

    editFolderName(folder) {
      let newName = this.folderState[folder.id].newName
      if (newName !== '' && newName !== null) {
        let reportFolder = {}
        reportFolder['name'] = newName
        reportFolder['id'] = folder.id
        this.editFolderId = null
        API.put('/v3/report/folder/update', { reportFolder: reportFolder })
          .then(response => {
            if (response.error) {
              this.$message({
                message: this.$t('common.wo_report.cannot_rename_folder'),
                type: 'error',
              })
              folder.name = this.folderState[folder.id].name
            } else {
              folder.name = response.data.reportFolder.name
              this.folderState[folder.id].name = folder.name
              this.folderState[folder.id].newName = folder.name
            }
          })
          .catch(() => {
            this.$message({
              message: this.$t('common.wo_report.cannot_rename_folder'),
              type: 'error',
            })
            folder.name = this.folderState[folder.id].name
          })
      } else {
        this.$message({
          message: this.$t('common.wo_report.foldername_cannot_empty'),
          type: 'error',
        })
      }
      this.$forceUpdate()
    },
    addNewFolder() {
      let newFolder = {}
      if (this.newFolderName === '' || this.newFolderName === null) {
        this.newFolderError = true
      } else {
        newFolder['name'] = this.newFolderName
        let moduleName = this.getCurrentModule().module
        API.post('/v3/report/folder/create', {
          reportFolder: newFolder,
          moduleName: moduleName,
        }).then(response => {
          if (response.error) {
            this.$message.error('Error while creating report folder')
          } else {
            this.newFolderName = ''
            let newFolder = response.data.reportFolder
            newFolder.reports = []
            this.newReportTree.push(newFolder)
            let folderState = {}
            folderState['name'] = newFolder.name
            folderState['newName'] = newFolder.name
            folderState['setting'] = false
            this.folderState[newFolder.id] = folderState
          }
        })
      }
    },
    moveReport(currentFolder, newFolder, report) {
      let curIndex
      let newIndex
      let reportIndex
      this.$set(this.folderState[currentFolder.id], 'setting', false)
      for (let key in this.newReportTree) {
        if (this.newReportTree[key].name === currentFolder.name) {
          curIndex = key
          reportIndex = this.newReportTree[key].reports.indexOf(report)
        }
        if (this.newReportTree[key].name === newFolder.name) {
          newIndex = key
        }
      }

      API.put('/v3/report/moveto', {
        reportId: report.id,
        folderId: newFolder.id,
      })
        .then(response => {
          this.newReportTree[curIndex].reports.splice(reportIndex, 1)
          this.newReportTree[newIndex].reports.push(report)
        })
        .catch(() => {
          this.$message({
            message: this.$t('common.wo_report.cannot_move_report'),
            type: 'error',
          })
        })
    },
    loadReportTree() {
      let self = this
      let moduleName = this.$route.query.module
        ? this.$route.query.module
        : this.getCurrentModule().module
      if (moduleName === 'energy') {
        moduleName = 'energyData'
      }
      if (moduleName !== 'custom') {
        self.$http
          .get(
            '/report/workorder/getAllWorkOrderReports?moduleName=' + moduleName
          )
          .then(function(response) {
            let data = response.data.allWorkOrderJsonReports
            let treeData = data.map(function(d) {
              d.expand = false
              if (self.reportId) {
                let report = d.children.find(
                  rt => rt.widget.dataOptions.reportId === self.reportId
                )
                if (report) {
                  d.expand = true
                }
              }
              return d
            })
            self.reportTree = treeData.filter(
              row => row.label !== 'Default' && row.label !== 'Old Reports'
            )
          })
      }
    },
    loadNewReportTree() {
      let self = this
      let moduleName = this.getCurrentModule().module
      if (this.$route.query.module) {
        moduleName = this.$route.query.module
      }
      if (moduleName === 'energy') {
        moduleName = 'energyData'
      }
      let url = '/v3/report/folders?moduleName=' + moduleName
      if (moduleName === 'custom') {
        url = '/v3/report/folders?moduleName=custommodule'
      }
      if (this.isPivot) {
        url += '&isPivot=true'
      }
      if (!isEmpty(this.webTabId) && this.webTabId > 0) {
        url += '&webTabId=' + this.webTabId
      }
      API.get(url, {}, { force: true }).then(resp => {
        if (!resp.error) {
          let data = resp.data.reportFolders
          let treeData = data.map(function(d) {
            d.expand = false
            if (self.reportId) {
              let report = d.reports.find(rt => rt.id === self.reportId)
              if (report) {
                d.expand = true
              }
            }
            return d
          })
          self.newReportTree = treeData
          self.loading = false
          // self.openFirstReport()
          for (let key in self.newReportTree) {
            let folder = self.newReportTree[key]
            let folderState = {}
            folderState['name'] = folder.name
            folderState['newName'] = folder.name
            folderState['setting'] = false
            folderState['editFolder'] = false
            self.folderState[folder.id] = folderState
          }
        } else {
          self.loading = false
        }
      })
    },
    openFirstReport() {
      let reportLink
      if (
        !this.loading &&
        !this.reportId &&
        !this.$route.path.includes('scheduled')
      ) {
        if (this.newReportTree.length && this.newReportTree[0].reports.length) {
          this.newReportTree[0].expand = true
          reportLink = this.getNewReportLink(
            this.newReportTree[0].reports[0].id
          )
        } else if (
          this.reportTree.length &&
          this.reportTree[0].children.length
        ) {
          this.reportTree[0].expand = true
          reportLink = this.getReportLink(
            this.reportTree[0].children[0].widget.dataOptions.reportId
          )
        }
        if (!isEmpty(reportLink)) this.$router.replace({ path: reportLink })
      }
    },
    loadReport(reportId, isNewReport) {
      let reportLink = isNewReport
        ? this.getNewReportLink(reportId)
        : this.getReportLink(reportId)
      this.$router.replace({ path: reportLink })
    },
    removeReport(obj) {
      let reportsType = obj.type
      let reportId = obj.reportId
      if (reportsType === 'new') {
        if (this.newReportTree) {
          for (let folder of this.newReportTree) {
            let rep = folder.reports.find(rt => rt.id === parseInt(reportId))
            if (rep) {
              let reportIndex = folder.reports.indexOf(rep)
              if (reportIndex >= 0) {
                folder.reports.splice(reportIndex, 1)

                if (reportIndex < folder.reports.length) {
                  let reportLink = this.getNewReportLink(
                    folder.reports[reportIndex].id
                  )
                  this.$router.replace({ path: reportLink })
                  return
                } else if (reportIndex - 1 < folder.reports.length) {
                  let reportLink = this.getNewReportLink(
                    folder.reports[reportIndex - 1].id
                  )
                  this.$router.replace({ path: reportLink })
                  return
                }
              }
            }
          }
        }
      } else {
        for (let folder of this.reportTree) {
          let rep = folder.children.find(
            rt => rt.widget.dataOptions.reportId === parseInt(reportId)
          )
          if (rep) {
            let reportIndex = folder.children.indexOf(rep)
            if (reportIndex >= 0) {
              folder.children.splice(reportIndex, 1)

              if (reportIndex < folder.children.length) {
                let reportLink = this.getReportLink(
                  folder.children[reportIndex].widget.dataOptions.reportId
                )
                this.$router.replace({ path: reportLink })
                return
              } else if (reportIndex - 1 < folder.children.length) {
                let reportLink = this.getReportLink(
                  folder.children[reportIndex - 1].widget.dataOptions.reportId
                )
                this.$router.replace({ path: reportLink })
                return
              }
            }
          }
        }
      }
      this.$router.push({ path: this.getCurrentModule(true).rootPath })
    },
    deleteReportFolder(folder) {
      let self = this
      let promptObj = {
        title: self.$t('common.wo_report.delete_folder'),
        message: self.$t('common.wo_report.delete_folder_msg'),
        rbDanger: true,
        rbLabel: self.$t('common._common.delete'),
      }
      self.$dialog.confirm(promptObj).then(function(value) {
        if (value) {
          API.post('/v3/report/folder/delete', { reportFolder: folder })
            .then(response => {
              if (response.error) {
                self.$message({
                  message: self.$t('common.wo_report.cannot_delete_msg'),
                  type: 'error',
                })
              } else {
                let index = self.newReportTree.indexOf(folder)
                self.newReportTree.splice(index, 1)
                self.$message({
                  message: self.$t('common.wo_report.folder_delete_success'),
                  type: 'success',
                })
              }
            })
            .catch(function(error) {
              self.$message({
                message: self.$t('common.wo_report.cannot_delete_msg'),
                type: 'error',
              })
            })
        }
      })
    },
    expand(folder) {
      folder.expand = !folder.expand
    },
    getReportLink(reportId) {
      let rootPath = this.getCurrentModule(true).rootPath
      return rootPath + '/view/' + reportId
    },
    getNewReportLink(reportId) {
      if (isWebTabsEnabled()) {
        let routeObj
        if (!this.moduleName) {
          routeObj = findRouteForReport(
            'analytics_reports',
            pageTypes.REPORT_VIEW
          )
        } else if (this.moduleName === 'energydata') {
          routeObj = findRouteForReport(
            'analytics_reports',
            pageTypes.REPORT_VIEW,
            { moduleName: this.moduleName }
          )
        } else {
          routeObj = findRouteForReport(
            'module_reports',
            pageTypes.REPORT_VIEW,
            { moduleName: this.moduleName }
          )
        }

        let { name } = routeObj || {}
        let path

        if (name) {
          path = this.$router.resolve({ name, params: { reportid: reportId } })
            .href
        }

        return path
      } else {
        let completeUrl = null
        if (
          this.$helpers.isEtisalat() &&
          this.getCurrentModule().module !== 'energydata'
        ) {
          completeUrl = '/app/em/modulereports/newview/' + reportId
          if (this.$route.query.module) {
            completeUrl = completeUrl + '?module=' + this.$route.query.module
          }
          return completeUrl
        }
        if (this.isPivot) {
          return `/app/em/pivot/view/${reportId}`
        }

        switch (this.getCurrentModule().module) {
          case 'workorder': {
            completeUrl = '/app/wo/reports/newview/' + reportId
            break
          }
          default: {
            completeUrl =
              this.getCurrentModule(true).rootPath + '/newview/' + reportId
            if (this.$route.query.module) {
              completeUrl = completeUrl + '?module=' + this.$route.query.module
            }
            break
          }
        }
        return completeUrl
      }
    },
    openScheduledReports() {
      let path
      if (isWebTabsEnabled()) {
        let routeObj
        if (!this.moduleName) {
          routeObj = findRouteForReport(
            'analytics_reports',
            pageTypes.REPORT_SCHEDULED
          )
        } else if (this.moduleName === 'energydata') {
          routeObj = findRouteForReport(
            'analytics_reports',
            pageTypes.REPORT_SCHEDULED,
            { moduleName: this.moduleName }
          )
        } else {
          routeObj = findRouteForReport(
            'module_reports',
            pageTypes.REPORT_SCHEDULED,
            { moduleName: this.moduleName }
          )
        }
        let { name } = routeObj || {}

        if (name) {
          path = this.$router.resolve({ name }).href
        }
      } else {
        path = `${this.getCurrentModule(true).rootPath}/scheduled`
      }
      if (path) this.$router.push({ path })
    },
    handleReportCommond(cmd) {
      if (cmd === 'delete') {
        let self = this
        let promptObj = {
          title: self.$t('common.wo_report.delete_report'),
          rbLabel: 'Delete',
        }
        self.$dialog.prompt(promptObj)
      } else if (cmd === 'edit') {
        this.$router.replace({
          path: this.getCurrentModule(true).rootPath + '/edit',
        })
      }
    },
    createNewReport() {
      if (this.isPivot) {
        this.$router.push({ name: 'pivotcreate' })
      } else if (this.isModuleReport) this.newModularReport()
    },
    newModularReport() {
      let moduleName = this.getCurrentModule().module

      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForReport('module_reports', pageTypes.REPORT_FORM, {
            moduleName,
          }) || {}

        if (name) {
          this.$router.push({
            name,
          })
        }
        return
      }

      if (this.$helpers.isEtisalat()) {
        this.$router.push({ path: '/app/em/modulereports/new' })
        return
      }
      switch (moduleName) {
        case 'workorder':
        case 'plannedmaintenance':
          this.$router.push({ path: '/app/wo/reports/new' })
          break
        case 'asset':
          this.$router.push({ path: '/app/at/reports/new' })
          break
        case 'inspectionTemplate':
          this.$router.push({ path: '/app/inspection/reports/new' })
          break
        case 'alarm':
          this.$router.push({ path: '/app/fa/reports/new' })
          break
        case 'custom':
          this.$router.push({ path: '/app/ca/reports/new' })
          break
        case 'tenant':
          this.$router.push({ path: '/app/tm/reports/new' })
          break
        case 'contracts':
          this.$router.push({ path: '/app/ct/reports/new' })
          break
        case 'visitorlog':
          this.$router.push({ path: '/app/vi/reports/new' })
          break
        case 'serviceRequest':
          this.$router.push({ path: '/app/sr/reports/new' })
          break
        case 'purchaseorder':
          this.$router.push({ path: '/app/purchase/reports/new' })
          break
        case 'budget':
        case 'budgetamount':
          this.$router.push({ path: '/app/ac/reports/new' })
          break
        case 'item':
        case 'tool':
        case 'itemTransactions':
        case 'tootTransactions':
        case 'itemTypes':
        case 'toolTypes':
        case 'storeRoom':
        case 'shipment':
        case 'transferrequest':
        case 'transferrequestshipmentreceivables':
        case 'transferrequestpurchaseditems':
          this.$router.push({ path: '/app/inventory/reports/new' })
          break
        default:
          this.$router.push({ path: '/app/at/reports/new' })
          break
      }
    },
    handleNewCommand(cmd) {
      if (cmd === 'reportfolder') {
        let self = this
        let promptObj = {
          title: self.$t('common.wo_report.new_report_folder'),
          promptPlaceholder: self.$t('common.wo_report.folder_name'),
          rbLabel: 'Save',
        }
        self.$dialog.prompt(promptObj).then(function(value) {
          if (value !== null) {
            let reportFolderObj = {
              name: value,
            }

            self.$http
              .post(
                'dashboard/addReportFolder?moduleName=' +
                  self.getCurrentModule().module,
                { reportFolderContext: reportFolderObj }
              )
              .then(function(response) {
                if (response.data.reportFolderContext) {
                  self.$message({
                    message: self.$t('common.wo_report.folder_created_success'),
                    type: 'success',
                  })
                  self.reportTree.push(response.data.reportFolderContext)
                } else {
                  self.$message({
                    message: self.$t('common.wo_report.folder_creation_failed'),
                    type: 'warning',
                  })
                }
              })
          }
        })
      } else if (cmd === 'tabular') {
        this.$router.replace({
          path: this.getCurrentModule(true).rootPath + '/newtabular',
        })
      } else if (cmd === 'matrix') {
        this.$router.replace({
          path: this.getCurrentModule(true).rootPath + '/newmatrix',
        })
      } else {
        this.$router.replace({
          path: this.getCurrentModule(true).rootPath + '/new',
        })
      }
    },
  },
}
</script>
<style>
.edit-icon-set {
  position: absolute;
  right: 10px;
  top: 12px;
  z-index: 3;
}
.row.reports-layout {
  height: 100%;
}

.reports-sidebar {
  background: white;
  height: 100%;
  overflow-y: hidden;
  border-right: 1px solid #6666662f;
  position: relative;
}

.rtree {
  position: relative;
  padding-bottom: 70px;
}

.rfolder-name {
  padding: 14px 10px 14px 20px;
  cursor: pointer;
  font-size: 12px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.6px;
  text-align: left;
  color: #000000;
}

.folder-container {
  position: relative;
}

.folder-container:hover .icon-display {
  visibility: visible;
}
.icon-display {
  visibility: hidden;
  padding: 5px;
}
.rfolder-name i {
  margin-right: 6px;
  font-size: 16px;
  color: #66666696;
}

.rfolder-children div {
  /* padding: 10px 10px 10px 25px; */
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.rfolder-children div:not(.rempty) {
  cursor: pointer;
  font-size: 14px;
  font-weight: 400;
  font-style: normal;
  font-stretch: normal;
  letter-spacing: 0.4px;
  text-align: left;
  color: #324056;
}

.rfolder-name:hover,
.rfolder-children div:not(.rempty):hover,
.rfolder-children div.active,
.scheduled-viewall:hover,
.scheduled-viewall div.active {
  background: #f0f7f8;
}

.rfolder-children div a:hover {
  color: #324056;
}
.rfolder-icon {
  display: inline-block;
}

.rempty {
  font-size: 13px;
  color: rgba(102, 102, 102, 0.57);
}
.fc-chart-side-btn {
  padding: 0;
  font-size: 13px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 1px;
  text-align: left;
  float: left;
  padding-top: 17px;
  /* color: #717b85; */
}
.fc-chart-side-btn .el-button-group button.el-button {
  font-size: 13px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.9px;
  text-align: left;
  /* color: #000000; */
}
.m20 {
  margin: 20px;
}
.pR10 {
  padding-right: 10px;
}
.r-sidebar-btn {
  padding-left: 10px;
  color: #615f89;
}
.r-sidebar-btn .el-button {
  color: #615f89;
  border-left: none;
}
.r-sidebar-btn .el-button-group .el-button:last-child {
  border-left: none;
}

.scheduled-viewall {
  width: 100%;
  left: 0;
  right: 0;
  border-top: solid 1px #f4f4f4;
  position: absolute;
  bottom: 30px;
  background-color: #ffffff;
  font-size: 14px;
  line-height: 25px;
  letter-spacing: 0.4px;
  color: #46a2bf;
  cursor: pointer;
  padding-left: 30px;
  text-align: left;
}

.scheduled-viewall div {
  padding: 13px 20px;
}

.new-folder-label {
  text-align: center;
  color: #303133;
  font-size: 14px;
  line-height: 1;
  margin-bottom: 12px;
  margin-top: 3px;
}
.new-folder-button {
  border: 0px;
  width: 100%;
}
.report-new-folder:hover .show-folder-delete {
  display: inline;
  float: right;
}
.ul-folder-list {
  list-style-type: none;
  padding-left: 0;
  margin-top: 0;
}

.ul-folder-list li {
  padding: 10px;
  cursor: pointer;
  line-height: 24px;
}
.ul-folder-list li:hover {
  background: #f0f7f8;
}

.folder-popover {
  height: 200px;
  max-width: 200px;
  overflow-y: scroll;
  overflow-x: hidden;
}
.show-folder-delete {
  display: none;
  margin-right: 0 !important;
}
.folder-list-txt span {
  padding-right: 10px;
}
.folder-list-txt.active {
  background: #f0f7f8;
}
.report-sidebar-scroll {
  height: calc(100vh - 150px);
  overflow-y: scroll;
}
</style>
