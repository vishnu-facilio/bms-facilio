<template>
  <div class="height-100 report-layout-pivot flex-middle">
    <iframe
      v-if="exportDownloadUrl"
      :src="exportDownloadUrl"
      style="display: none;"
    ></iframe>

    <div
      v-if="!leftPaneVisibility"
      :class="`hamburger-menu`"
      @click="handleBackButton"
    >
      <!-- <img src="~statics/pivot/menu.svg" width="18px" height="18px" /> -->
      <i class="el-icon-back" />
    </div>
    <el-dialog
      :visible="loading"
      :show-close="false"
      :append-to-body="true"
      :close-on-click-modal="false"
      width="0"
    >
      <img src="~statics/pivot/circle-loading.svg" />
    </el-dialog>
    <ModuleSelectDialogBoxViewPage
      v-if="moduleSlectVisibility"
      :visibility.sync="moduleSlectVisibility"
      @dialogClosed="moduleSlectVisibility = false"
    ></ModuleSelectDialogBoxViewPage>
    <PivotReportSideBar
      v-show="leftPaneVisibility"
      class="report-summary"
      ref="pivotReportSidebar"
      moduleName="energydata"
      reportid="id"
      :updateReports="updateReports"
      @reportChanged="reportChanged"
      @folderClicked="folderReportView"
      @sidebarLoaded="defaultPageToggle(1)"
      @clearReport="report = null"
      @foldersEmpty="isFoldersEmpty = true"
      @foldersNotEmpty="isFoldersEmpty = false"
      v-bind:isPivot="true"
    ></PivotReportSideBar>
    <div class="header-bar">
      <div
        class="report-header-and-summary header-bar-background"
        v-if="isFolderClicked || isReportPrevied"
        :style="!leftPaneVisibility ? 'border-left:none' : ''"
      >
        <div
          class="report-name-section"
          :style="!leftPaneVisibility ? 'padding-left:50px;' : ''"
          v-if="report || reportId || isFolderClicked"
          :class="
            !leftPaneVisibility
              ? 'breadcrumb-view'
              : isReportPrevied || !leftPaneVisibility
              ? 'breadcrumb-view'
              : ''
          "
        >
          <el-breadcrumb separator="">
            <inline-svg
              v-if="isFolderClicked"
              src="svgs/pivot/folder"
              :style="
                !leftPaneVisibility
                  ? 'margin-right:10px;'
                  : 'margin-right:10px;paddinng-left:25px;'
              "
            />
            <el-breadcrumb-item
              v-if="isReportPrevied || isFolderClicked"
              class="pivot-crumb"
              style="font-weight:500 !important"
              v-model="selected_folder_Obj"
            >
              <div
                v-if="!leftPaneVisibility && reportLoaded"
                :style="
                  !leftPaneVisibility && !isReportPrevied
                    ? 'margin-left:25px;margin-top:20px;'
                    : ''
                "
                :class="isReportPrevied ? 'breadcrumb-text-folder' : ''"
              >
                {{
                  report.description != '--'
                    ? report.description
                    : 'No Description'
                }}
              </div>
              <div
                class="breadcrumb-text-folder"
                :style="'color:black;'"
                v-else
              >
                {{ selected_folder_Obj.folderName }}
              </div>
            </el-breadcrumb-item>

            <el-breadcrumb-item
              class="pivot-crumb"
              style="font-weight:500 !important;font-size:16px !important;"
              v-if="isReportPrevied"
            >
              {{ selected_report_name }}</el-breadcrumb-item
            >
          </el-breadcrumb>
          <div :style="'display:flex;margin:5px 35px 0 0;'">
            <div
              v-if="isReportPrevied && hasCreateEditPermission"
              style="height: 26px;width: 74px;margin-bottom: 4px;"
            >
              <el-button plain @click="editReport" class="edit-button">
                <InlineSvg
                  src="svgs/pivot/edit"
                  iconClass="icon-sm flex icon"
                ></InlineSvg>

                EDIT
              </el-button>
            </div>
            <div
              v-if="isReportPrevied"
              class="more-button"
              style="margin-left:20px;margin-bottom:8px;margin-top:2px;transform: rotate(90deg);"
            >
              <el-dropdown
                trigger="click"
                placement="bottom"
                :hide-on-click="false"
                @command="
                  command => handleCommand(command, isCloneToAnotherApp)
                "
              >
                <el-button icon="el-icon-more" size="medium" @click.stop="">
                </el-button>
                <el-dropdown-menu slot="dropdown" style="margin-left:155px;">
                  <el-dropdown-item command="delete" v-if="hasDeletePermission">
                    <InlineSvg
                      src="svgs/pivot/delete-icon"
                      iconClass="icon-sm flex icon"
                    ></InlineSvg
                    >Delete</el-dropdown-item
                  >
                  <el-dropdown-item
                    command="duplicate"
                    v-if="hasCreateEditPermission"
                  >
                    <InlineSvg
                      src="svgs/pivot/duplicate"
                      iconClass="icon-sm flex icon"
                    ></InlineSvg>
                    Duplicate</el-dropdown-item
                  >
                  <el-dropdown-item
                    command="move"
                    v-if="hasCreateEditPermission"
                  >
                    <el-popover
                      placement="right"
                      width="185"
                      title="Folders"
                      trigger="hover"
                    >
                      <div
                        v-for="folder in reportFolders"
                        :key="folder.id"
                        class="pivot-folder-option"
                        @click="handleMoveFolder(folder, report)"
                      >
                        {{ folder.name }}
                      </div>
                      <button slot="reference" class="move-btn">
                        <InlineSvg
                          src="svgs/pivot/Move"
                          iconClass="icon-sm flex icon"
                        ></InlineSvg>
                        Move
                      </button>
                    </el-popover>
                  </el-dropdown-item>
                  <el-dropdown-item command="export" v-if="hasExportPermission">
                    <el-popover placement="right" width="185" trigger="hover">
                      <div
                        class="pivot-folder-option"
                        @click="() => exportReport(1, this.report.id)"
                      >
                        Export as CSV
                      </div>
                      <div
                        class="pivot-folder-option"
                        @click="() => exportReport(2, this.report.id)"
                      >
                        Export as Excel
                      </div>
                      <button slot="reference" class="move-btn">
                        <InlineSvg
                          src="svgs/pivot/Export"
                          iconClass="icon-xs flex icon"
                        ></InlineSvg>
                        Export
                      </button>
                    </el-popover>
                  </el-dropdown-item>
                  <el-dropdown-item
                    command="Schedule"
                    v-if="hasSchedulePermission"
                  >
                    <img
                      class="report-icon flex icon"
                      src="~statics/report/calendar.svg"
                      style="height: 14px;width: 14px;margin-left: 2px;margin-right: 8px;"
                    />
                    Schedule Report
                  </el-dropdown-item>
                  <el-dropdown-item command="Mail">
                    <img
                      class="report icon flex icon"
                      src="~statics/report/email.svg"
                      style="height: 14px;width: 14px;margin-left: 2px;margin-right: 8px;"
                    />
                    Send Mail
                  </el-dropdown-item>
                  <el-dropdown-item
                    command="CloneReport"
                    v-if="hasCreateEditPermission"
                  >
                    <i
                      :class="['fa fa-clone']"
                      data-position="top"
                      data-arrow="true"
                      :title="$t('home.dashboard.duplicate_report')"
                      v-tippy
                    ></i>
                    Clone to another app
                  </el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
            </div>
          </div>
        </div>
        <div
          v-if="!isReportPrevied && !isEmptyFolderView"
          class="search-container-header pL10 pR23"
          style="margin-top:1px;"
        >
          <el-input
            placeholder="Search"
            v-model="searchText"
            type="search"
            :prefix-icon="'el-icon-search'"
            class="fc-input-full-border2 pL5"
          >
          </el-input>
        </div>
        <div
          class="new-pivot-btn header-btn"
          :style="isReportPrevied ? 'display:none' : ''"
          @click="moduleSlectVisibility = true"
        >
          <i class="el-icon-plus pR5 fwBold" style="transform: scale(0.9);"></i
          >New Pivot Table
        </div>
      </div>
      <div
        v-if="isReportPrevied"
        class="pivot-summary-section"
        :style="!leftPaneVisibility ? 'width:94.5vw;' : ''"
      >
        <PivotSummaryNew
          ref="PivotSummaryNew"
          :key="refreshPivotSummary"
          v-if="id"
          @reportDeleted="handleReportDelete"
          @summaryLoaded="defaultPageToggle(2)"
          :id="id"
        />
      </div>
      <div v-if="isFoldersEmpty" class="default-view-page">
        <div class="default-image-container">
          <img src="~statics/pivot/default-img.png" class="default-image" />
          <a>Learn more about Pivot Tables</a>
        </div>
        <div class="new-pivot-btn" @click="moduleSlectVisibility = true">
          <i class="el-icon-plus pR5 fwBold" style="transform: scale(0.9);"></i
          >New Pivot Table
        </div>
      </div>
      <div
        v-if="isFolderClicked"
        :class="
          !leftPaneVisibility
            ? 'report-view-table-fullscreen'
            : 'report-view-table'
        "
      >
        <!-- <a
          :href="exportDownloadUrl"
          ref="downloadExport"
          style="display:none;"
        /> -->
        <el-table
          :data="reportFoldersList"
          @row-click="openReportDetails"
          :cell-style="{ margin: '4px 0' }"
          row-class-name="report-row no-hover"
        >
          <el-table-column
            prop="name"
            label="Name"
            width="35%"
            class-name="pivot-table-name"
          >
          </el-table-column>
          <el-table-column prop="description" label="Description" width="25%">
          </el-table-column>
          <el-table-column prop="moduleName" label="Module" width="20%">
          </el-table-column>
          <el-table-column prop="created_by" label="Created by" width="20%">
            <template slot-scope="scope">
              <div class="pL25" v-if="scope.row.createdBy !== '--'">
                <user-avatar
                  size="sm"
                  :user="$store.getters.getUser(scope.row.createdBy)"
                  :name="false"
                  v-tippy
                  :title="$store.getters.getUser(scope.row.createdBy).name"
                ></user-avatar>
              </div>
              <div class="pL25" v-else>
                <span style="padding-left:6px;">
                  {{ scope.row.createdBy }}</span
                >
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="modified_time" label="Modified on" width="20%">
          </el-table-column>
          <el-table-column prop="modified_by" label="Modified by" width="20%">
            <template slot-scope="scope">
              <div class="pL25" v-if="scope.row.modified_by !== '--'">
                <user-avatar
                  size="sm"
                  :user="$store.getters.getUser(scope.row.modified_by)"
                  :name="false"
                  v-tippy
                  :title="$store.getters.getUser(scope.row.modified_by).name"
                ></user-avatar>
              </div>
              <div class="pL25" v-else>
                <span style="padding-left:6px;">{{
                  scope.row.modified_by
                }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column align="right" width="20%">
            <template slot-scope="scope">
              <div style="height:100%;width:100%;" class="table-btn-container">
                <el-button
                  v-if="hasCreateEditPermission"
                  size="medium"
                  class="edit-btn"
                  @click="editReport(scope.row)"
                  style="margin-right:10px"
                >
                  <InlineSvg
                    src="svgs/pivot/edit"
                    iconClass="icon-sm flex icon"
                  ></InlineSvg>
                </el-button>
                <el-dropdown
                  trigger="click"
                  class="more-dropdown"
                  :hide-on-click="false"
                  @command="
                    command =>
                      handleCommand(command, isCloneToAnotherApp, scope.row)
                  "
                >
                  <el-button
                    icon="el-icon-more"
                    class="more-btn"
                    size="medium"
                    @click.stop=""
                  >
                  </el-button>
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item
                      command="delete"
                      v-if="hasDeletePermission"
                    >
                      <InlineSvg
                        src="svgs/pivot/delete-icon"
                        iconClass="icon-sm flex icon"
                      ></InlineSvg
                      >Delete</el-dropdown-item
                    >
                    <el-dropdown-item
                      command="duplicate"
                      v-if="hasCreateEditPermission"
                    >
                      <InlineSvg
                        src="svgs/pivot/duplicate"
                        iconClass="icon-sm flex icon"
                      ></InlineSvg
                      >Duplicate</el-dropdown-item
                    >
                    <el-dropdown-item
                      command="move"
                      v-if="hasCreateEditPermission"
                    >
                      <el-popover
                        placement="left"
                        width="185"
                        title="Folders"
                        trigger="hover"
                      >
                        <div
                          v-for="folder in reportFolders"
                          :key="folder.id"
                          class="pivot-folder-option"
                          @click="handleMoveFolder(folder, scope.row)"
                        >
                          {{ folder.name }}
                        </div>
                        <button slot="reference" class="move-btn">
                          <InlineSvg
                            src="svgs/pivot/Move"
                            iconClass="icon-sm flex icon"
                          ></InlineSvg>
                          Move
                        </button>
                      </el-popover>
                    </el-dropdown-item>
                    <el-dropdown-item
                      command="export"
                      v-if="hasExportPermission"
                    >
                      <el-popover placement="left" width="185" trigger="hover">
                        <div
                          class="pivot-folder-option"
                          @click="exportReport(1, scope.row.id)"
                        >
                          Export as CSV
                        </div>
                        <div
                          class="pivot-folder-option"
                          @click="exportReport(2, scope.row.id)"
                        >
                          Export as Excel
                        </div>
                        <button slot="reference" class="move-btn">
                          <InlineSvg
                            src="svgs/pivot/Export"
                            iconClass="icon-sm flex icon"
                          ></InlineSvg>
                          Export
                        </button>
                      </el-popover>
                    </el-dropdown-item>
                    <el-dropdown-item
                      command="Schedule"
                      v-if="hasSchedulePermission"
                    >
                      <img
                        class="report-icon flex icon"
                        src="~statics/report/calendar.svg"
                        style="height: 14px;width: 14px;margin-left: 2px;margin-right: 8px;"
                      />
                      Schedule Report
                    </el-dropdown-item>
                    <el-dropdown-item command="Mail">
                      <img
                        class="report icon flex icon"
                        src="~statics/report/email.svg"
                        style="height: 14px;width: 14px;margin-left: 2px;margin-right: 8px;"
                      />
                      Send Mail
                    </el-dropdown-item>
                    <el-dropdown-item
                      command="CloneReport"
                      v-if="hasCreateEditPermission"
                    >
                      <i
                        style="margin-left: 2px;margin-right: 8px"
                        :class="['fa fa-clone']"
                        data-position="top"
                        data-arrow="true"
                        :title="$t('home.dashboard.duplicate_report')"
                        v-tippy
                      ></i>
                      Clone to another app
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
    <email-report
      v-if="visibility.showEmailDialog"
      :moduleReportConfig="computedConfig"
      :moduleName="moduleName"
      :visibility.sync="visibility.showEmailDialog"
      :params="params"
      :report="reportObject ? reportObject.report : {}"
      :name="name"
      :scheduledReportId="scheduledReportId"
      :reportname="reportname"
    ></email-report>
    <schedule-report
      :moduleName="modulename"
      v-if="visibility.showScheduleDialog"
      :visibility.sync="visibility.showScheduleDialog"
      :report="reportObject ? reportObject.report : {}"
      :scheduledReportId="scheduledReportId"
      :reportname="reportname"
      :isPivot="true"
      :name="name"
    ></schedule-report>
    <div class="dashboard-share dialog-box" v-if="isClone">
      <el-dialog
        title="Clone Report"
        :visible.sync="isClone"
        width="35%"
        class="fc-dialog-center-container pivot-share-dialog"
        :append-to-body="true"
      >
        <div>
          <div class="clonePopup">
            <el-form
              ref="cloneDashboardForm"
              class="clone_dialog"
              :rules="rules"
              :model="reportCloneObj"
            >
              <el-form-item
                label="Report Name"
                prop="name"
                :required="true"
                class="mB10"
              >
                <el-input
                  v-model="reportCloneObj.name"
                  class="width300px fc-input-full-border2 pR15"
                >
                </el-input>
              </el-form-item>
              <el-form-item
                v-if="isCloneToAnotherApp"
                label="Target Application"
                prop="name"
                class="mB10"
              >
                <el-select
                  v-model="reportCloneObj.selectedAppId"
                  filterable
                  class="fc-input-full-border2 width300px pR15"
                >
                  <el-option
                    v-for="app in apps"
                    :key="app.linkName"
                    :label="app.name"
                    :value="app.id"
                  >
                  </el-option>
                </el-select>
              </el-form-item>
            </el-form>
          </div>
        </div>
        <span class="dialog-footer row">
          <el-button
            @click="isClone = false"
            class="col-6 shareoverbtn shrbtn1"
          >
            {{ $t('common._common.cancel') }}
          </el-button>
          <el-button
            type="primary"
            class="modal-btn-save"
            :loading="dashboardSaving"
            @click="cloneReporttoAnotherApp(isCloneToAnotherApp)"
          >
            {{ $t('common._common._save') }}
          </el-button>
        </span>
      </el-dialog>
    </div>
  </div>
</template>
<script>
// import ReportSidebar from 'pages/report/ReportSidebar'
import PivotReportSideBar from './PivotReportSideBar'
import PivotSummaryNew from './PivotSummaryNew'
import EmailReport from 'pages/report/forms/EmailReportNew'
import ScheduleReport from 'pages/report/forms/ScheduleReportNew'
import { Message } from 'element-ui'
import { API } from '@facilio/api'
import dashboardLayoutHelper from 'pages/dashboard/helpers/newDashboardlayoutHelper'
import { getApp } from '@facilio/router'
import { isEmpty } from '@facilio/utils/validation'
import ModuleSelectDialogBoxViewPage from './Components/ModuleSelectDialogBoxViewPage.vue'
import UserAvatar from '@/avatar/User'
import { isWebTabsEnabled, findRouteForTab, pageTypes } from '@facilio/router'
import ReportTabPermissions from 'pages/report/mixins/ReportTabPermissions'

import './pivot.scss'

export default {
  mixins: [dashboardLayoutHelper, ReportTabPermissions],
  props: ['id'],
  data() {
    return {
      name: 'pivot',
      reportObject: null,
      visibility: {
        showScheduleDialog: false,
        showEmailDialog: false,
      },
      report_id: null,
      isClone: false,
      reportCloneObj: {
        folder: null,
        selectedAppId: null,
        dashboard: null,
        name: null,
      },
      rules: {
        folder: {
          required: true,
          message: this.$t('common.placeholders.enter_folder_name'),
        },
        name: {
          required: true,
          message: this.$t('common.placeholders.enter_report_name'),
          trigger: 'change',
        },
      },
      dashboardSaving: false,
      isCloneToAnotherApp: false,
      modulename: null,
      reportname: null,
      scheduledReportId: null,
      refreshPivotSummary: 0,
      report: null,
      loading: false,
      defaultPage: {
        summary: false,
        sidebar: false,
      },
      showButtons: false,
      moduleSlectVisibility: false,
      leftPaneVisibility: true,
      reportFolders: [],
      selected_folder_Obj: { folderName: '', folderId: null, folder: null },
      folderID: null,
      selected_report_name: '',
      reportList: [],
      isFolderClicked: false,
      isReportPrevied: false,
      isEmptyFolderView: false,
      isFoldersEmpty: false,
      searchText: '',
      updateReports: 0,
      reportFetchError: false,
      showMovePopOver: false,
      currentFolder: {},
      exportDownloadUrl: null,
      visible: true,
    }
  },
  computed: {
    reportLoaded() {
      if (!isEmpty(this.report)) {
        return true
      } else {
        return false
      }
    },
    reportFolderName() {
      if (!this.report || !this.reportFolders) return ''
      let reportFolder = this.reportFolders?.find(
        f => f.id === this.report.reportFolderId
      )
      return reportFolder.name
    },
    reportId() {
      if (isWebTabsEnabled()) {
        return this.isFolderClicked ? null : this.$route.params.reportId
      }
      return this.isFolderClicked ? null : this.$route.params.id
    },
    reportFoldersList() {
      if (this?.reportList && this.reportList.length > 0) {
        this.reportList.filter(report => {
          if (!report.description) {
            report.description = '--'
          }
          // report.created_time = !isNaN(report.createdTime) ? Date(report.createdTime): '--'
          report.created_by = !report.createdBy ? '--' : report.createdBy
          report.modified_time = !isNaN(report.modifiedTime)
            ? Date(report.modifiedTime)
            : '--'
          report.modified_by = !report.modifiedBy ? '--' : report.modifiedBy
        })
      }
      if (isEmpty(this.searchText)) {
        return this.reportList
      } else {
        let filteredReports = this.reportList.filter(report => {
          return report.name
            .toLowerCase()
            .includes(this.searchText.toLowerCase())
        })

        if (!isEmpty(filteredReports)) {
          return filteredReports
        }
        return []
      }
    },
  },
  title() {
    return this.$t('pivot.documentTitle')
  },
  async mounted() {
    this.availableApps()
    let self = this
    this.initDefaultValues()
    await this.loadReportFolders()
    if (this.$route.params.id) {
      this.loadReport()
    } else if (this.$route?.params?.reportId) {
      this.loadReport(this.$route?.params?.reportId)
    } else if (this.$route.query.folderId) {
      this.$refs['pivotReportSidebar'].collapseControl(
        this.$route.query.folderId
      )
    }
    if (self.$route.path.endsWith('/view')) self.openFirstFolder()
  },
  watch: {
    id() {
      this.refreshPivotSummary++
    },
    report: {
      handler(newState) {
        if (newState != null) {
          this.isFolderClicked = false
          this.leftPaneVisibility = false
        }
      },
      immediate: true,
    },
  },
  components: {
    PivotReportSideBar,
    PivotSummaryNew,
    EmailReport,
    ScheduleReport,
    ModuleSelectDialogBoxViewPage,
    UserAvatar,
  },
  created() {
    const { id } = this
    if (!isEmpty(id)) {
      this.leftPaneVisibility = false
    }
  },
  methods: {
    // loadPivotFolderManager(){
    //   this.$router.push('/app/em/pivot/foldermanager')
    // },
    async cloneReporttoAnotherApp(isCloneToAnotherPortal) {
      let reportObj = this.reportCloneObj
      let params = null
      if (
        reportObj?.name &&
        reportObj?.name.trim() !== '' &&
        reportObj?.selectedAppId > 0
      ) {
        params = {
          cloned_report_name: reportObj.name,
          target_app_id: reportObj.selectedAppId,
          cloned_app_id: this.cloned_app_id,
          report_id: this.report_id,
        }
      }
      if (params) {
        this.dashboardSaving = true
        let { data, error } = await API.post('v3/report/pivot/clone', {
          data: params,
        })
        this.isClone = false
        if (!error && data?.cloned_report_name) {
          this.$message.success('Report Cloned Successfully')
        } else {
          this.$message.error('Error while cloning report')
        }
      }
      this.dashboardSaving = false
    },
    availableApps() {
      API.get(`v2/application/list`).then(({ data, error }) => {
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.apps = data.application
          if (this?.apps?.length > 1) {
            this.isShowCloneToAnotherApp = true
          }
          this.appId = (getApp() || {}).id
          if (isEmpty(this.appId)) {
            let defaultApp =
              this.apps.find(app => app.linkName === 'newapp') || this.apps[0]
            this.appId = defaultApp.id
            this.reportCloneObj.selectedAppId = this.appId
          }
          this.reportCloneObj.selectedAppId = this.appId
          this.loadDashboards(true, this.appId)
        }
      })
    },
    openClonePopup(id, name, isCloneToAnotherApp) {
      this.isClone = true
      this.report_id = id
      this.reportCloneObj.name = name
      this.isCloneToAnotherApp = isCloneToAnotherApp
        ? isCloneToAnotherApp
        : false
      this.isCloneToAnotherApp = true
    },
    openFirstFolder() {
      this.$refs['pivotReportSidebar'].folderClicked(this.currentFolder)
    },
    handleBackButton() {
      if (!this.leftPaneVisibility) {
        this.isFolderClicked = true
        this.isReportPrevied = false
      }
      this.leftPaneVisibility = !this.leftPaneVisibility
      let route_url = `/app/em/pivot/view?folderId=${this.currentFolder.id}`
      if (isWebTabsEnabled()) {
        let { name } = findRouteForTab(pageTypes.PIVOT_VIEW)
        route_url = this.$router.resolve({
          name,
          query: { folderId: this.selected_folder_Obj.folderId },
        }).href
      }
      history.replaceState({}, '', window.location.origin + route_url)
    },
    openFolder() {
      this.isReportPrevied = false
      let url =
        '/app/em/pivot/view?folderId=' + this.selected_folder_Obj.folderId
      if (isWebTabsEnabled()) {
        let { name } = findRouteForTab(pageTypes.PIVOT_VIEW)
        url = this.$router.resolve({
          name,
          query: { folderId: this.selected_folder_Obj.folderId },
        }).href
      }
      this.$router.push(url)
      this.reportList = this.selected_folder_Obj.folder.reports
      this.isFolderClicked = true
    },
    initDefaultValues() {
      if (this.isReportPrevied === false && this.isFolderClicked === false) {
        this.isEmptyFolderView = true
      } else {
        this.isEmptyFolderView = false
      }
    },
    handleReportDelete(obj) {
      if (this.$refs['pivotReportSidebar']) {
        this.$refs['pivotReportSidebar'].updateReports(obj)
      }
    },
    view(row) {
      console.log('row :', row)
    },
    defaultPageToggle(val) {
      if (val == 1) {
        this.defaultPage.sidebar = true
      } else if (val == 2) {
        this.defaultPage.summary = true
      }
    },
    closePopover() {
      let popper = document.querySelectorAll('.el-popover')
      popper.forEach(pop => {
        pop.remove()
      })
    },
    Schedulereport(id, name, repname) {
      this.visibility.showScheduleDialog = true
      this.scheduledReportId = id
      this.modulename = name
      this.reportname = repname
    },
    sendmail(id, name, repname) {
      this.visibility.showEmailDialog = true
      this.scheduledReportId = id
      this.modulename = name
      this.reportname = repname
    },
    exportReport(type, id) {
      this.closePopover()
      this.$message({
        message: 'Exporting as ' + this.$constants.FILE_FORMAT[type],
        showClose: true,
        duration: 0,
      })
      let url = '/v2/report/exportPivotReport'
      let params = {
        reportId: id.toString(),
        fileFormat: `${type}`,
      }
      if (this.$refs['PivotSummaryNew']) {
        const dateFilterObject = this.$refs['PivotSummaryNew'].dateFilterObject
        if (!isEmpty(dateFilterObject) && !isEmpty(dateFilterObject.value)) {
          params['startTime'] = dateFilterObject.value[0]
          params['endTime'] = dateFilterObject.value[1]
        }
      }
      API.post(url, params).then(response => {
        Message.closeAll()
        if (response.data) {
          this.exportDownloadUrl = response.data.fileUrl
          // this.$refs['downloadExport'].click( )
        }
      })
    },
    handleMoveFolder(newFolder, report) {
      this.closePopover()
      this.$refs['pivotReportSidebar'].moveReport(
        this.currentFolder,
        newFolder,
        report
      )
      this.handleReportDelete(report)
      // this.isFolderClicked = true
    },
    reportChanged(report) {
      this.isFolderClicked = false
      this.isReportPrevied = true
      this.leftPaneVisibility = -false
      this.report = report
      let reportFolder = this.reportFolders?.find(
        f => f.id === this.report.reportFolderId
      )
      this.selected_report_name = report.name
      this.selected_folder_Obj.folderName = reportFolder.name
      this.selected_folder_Obj.folderId = reportFolder.id
      this.selected_folder_Obj.folder = reportFolder
    },
    async folderReportView(folder) {
      this.currentFolder = folder
      this.selected_folder_Obj.folderName = folder.name
      this.selected_folder_Obj.folderId = folder.id
      this.selected_folder_Obj.folder = folder
      this.reportList = folder.reports
      if (this.folderID != folder.id) {
        this.isEmptyFolderView = false
        this.isReportPrevied = false
        this.isFolderClicked = true
        this.folderID = folder.id
      } else {
        this.folderID = folder ? folder.id : null
        this.isFolderClicked = true
        this.isReportPrevied = false
        this.isEmptyFolderView = false
        this.report = null
      }
    },
    async loadReportFolders() {
      // no module name case is energy data reports, DON't send moduleName prop as energydata.;leave null for energreports
      let url = '/v3/report/folders?moduleName=energydata'
      let self = this
      //pivot
      url += '&isPivot=true'
      await API.get(url).then(response => {
        if (!response.error) {
          self.reportFolders = response.data.reportFolders
        }
      })
    },
    handleCommand(command, isCloneToAnotherApp, row) {
      let report = !row ? this.report : row
      if (command == 'delete') {
        this.deleteReport(report)
      } else if (command == 'duplicate') {
        this.duplicateReport(report)
      } else if (command == 'move') {
        this.showMovePopOver = true
      } else if (command == 'Mail') {
        this.sendmail(report.id, report.module.name, report.name)
      } else if (command == 'CloneReport') {
        this.openClonePopup(report.id, report.name, isCloneToAnotherApp)
      } else if (command == 'Schedule') {
        if (row) {
          this.Schedulereport(row.id, row.module.name, row.name)
        } else {
          this.Schedulereport(report.id, report.module.name, report.name)
        }
      }
    },
    async openReportDetails(row) {
      this.loading = true
      let route_url = `/app/em/pivot/view/${row.id}`
      if (isWebTabsEnabled()) {
        let { name } = findRouteForTab(pageTypes.PIVOT_VIEW)
        route_url = this.$router.resolve({ name, params: { reportId: row.id } })
          .href
      }
      this.$router.push(route_url)
      this.loadReport(row.id, true)
      this.leftPaneVisibility = false
    },
    async loadReport(rowId, control_folder) {
      let reportId = rowId ? rowId : this.$route.params.id
      this.isReportPrevied = reportId ? true : false
      this.isFolderClicked = false
      this.folderID = null
      this.initDefaultValues()
      let selected_report_folder = this.reportFolders.filter(report_folder => {
        if (
          report_folder &&
          report_folder.reports &&
          report_folder.reports.length
        ) {
          let selected_report = report_folder.reports.find(
            report => report.id === parseInt(reportId)
          )
          if (selected_report && selected_report.id) {
            this.selected_report_name = selected_report.name
            this.report = selected_report
            return report_folder
          }
        }
      })
      if (selected_report_folder && selected_report_folder.length) {
        this.selected_folder_Obj.folderName = selected_report_folder[0].name
        this.selected_folder_Obj.folderId = selected_report_folder[0].id
        this.selected_folder_Obj.folder = selected_report_folder[0]
        this.folderID = selected_report_folder[0].id
      }
      this.loading = false
      if (!control_folder) {
        try {
          this.$refs['pivotReportSidebar'].collapseControl(this.folderID)
        } catch {
          console.log('pivotReportSidebar not defined')
        }
      }
    },
    async deleteReport(row) {
      let { id } = row
      let promptObj = {
        title: 'Delete Report',
        message: 'Are you sure you want to delete this Report?',
        rbDanger: true,
        rbLabel: 'Delete',
      }
      let deleteConfirmation = await this.$dialog.confirm(promptObj)
      if (deleteConfirmation) {
        let { data, error } = await API.post('/v2/report/deleteReport', {
          reportId: id ? id : this.reportId,
        })

        if (error) {
          this.$message.error('Error deleting pivot report', error)
          // API error on delete
        } else {
          if (data && data.errorString) {
            // Cannot delete as it's present in dashboard,confirm again
            let confirmObj = {
              title: 'Delete Report',
              message:
                'This report is associated to a dashboard widget. Deleting this report will remove the widget from the dashboard. Are you sure you want to continue?',
              rbLabel: 'Yes, delete',
              lbLabel: 'No, cancel',
            }
            let widgetDeleteConfirm = await this.$dialog.confirm(confirmObj)

            if (widgetDeleteConfirm) {
              let resp = await API.post('/v2/report/deleteReport', {
                reportId: this.reportId,
                deleteWithWidget: true,
              })
              if (resp.error) {
                this.$message.error(
                  'Error deleting pivot report and widget',
                  error
                )
              } else {
                this.$emit('reportDeleted', {
                  type: 'new',
                  reportId: this.reportId,
                })
                this.$message.success('Report deleted successfully')
              }
            }
          } else {
            this.$emit('reportDeleted', {
              type: 'new',
              reportId: this.reportId,
            })
            this.$message.success('Report deleted successfully')
            this.report = null
            this.isFolderClicked = true
            this.isReportPrevied = false
            this.leftPaneVisibility = true
            this.handleReportDelete(row)
          }
        }
      }
    },
    duplicateReport(row) {
      let { id } = row
      let path = `/app/em/pivotbuilder/new?reportId=${
        id ? id : this.reportId
      }&duplicate=true`
      if (isWebTabsEnabled()) {
        let { name } = findRouteForTab(pageTypes.PIVOT_FORM)
        path = this.$router.resolve({
          name,
          query: { reportId: id ? id : this.reportId, duplicate: true },
        }).href
      }
      this.$router.push({ path: path })
    },
    editReport(row) {
      this.isFolderClicked = false
      let { id } = row
      let path = `/app/em/pivotbuilder/new?reportId=${id ? id : this.reportId}`
      if (isWebTabsEnabled()) {
        let { name } = findRouteForTab(pageTypes.PIVOT_FORM)
        path = this.$router.resolve({
          name,
          query: { reportId: id ? id : this.reportId },
        }).href
      }
      this.$router.push({ path: path })
    },
  },
}
</script>
<style lang="scss">
.report-layout-pivot {
  .pivot-report-summary-icon {
    width: fit-content;
  }

  .pivot-table-name {
    color: #0074d1 !important;
    &:hover {
      text-decoration: underline;
      cursor: pointer;
    }
  }

  .breadcrumb-text-folder {
    font-size: 13px;
    font-weight: normal;
    font-stretch: normal;
    font-style: normal;
    line-height: normal;
    letter-spacing: 0.31px;
    color: #a0acc0;
    cursor: pointer;
  }
  .breadcrumb-view {
    .el-breadcrumb {
      align-items: flex-start !important;
      flex-direction: column-reverse !important;
      height: 60px;
      justify-content: space-evenly;
    }
    .inline {
      // display: none;
    }
    .bc-separator {
      display: none;
    }
  }
  .report-header-section {
    height: 60px;
    background-color: #fff;
  }

  .report-header-and-summary {
    display: flex;
    justify-content: space-between;
    height: 60px;
    margin-left: -3px;
  }
  .is-right {
    .cell {
      display: flex;
      align-items: center;
      justify-content: center;
      height: 25px;
    }
  }

  .report-header-section {
    display: flex;
    align-items: center;
  }

  .current-folder {
    display: flex;
  }
  .current-folder-name {
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
    width: 200px;
  }

  .folder-name {
    &:hover {
      color: #ff3184;
      box-shadow: 0 3px 14px 0 #fec1d9;
    }
  }
  .report-name-section {
    padding-left: 14px;
    width: 100%;
    justify-content: space-between;
    display: flex;
    align-items: center;
    .el-breadcrumb,
    .el-breadcrumb__inner {
      display: flex;
      align-items: center;
    }
    // .el-button:hover {
    //   background: none !important;
    //   color: #38b2c2 !important;
    // }
  }

  .report-view-table {
    padding: 10px;
    .el-table__header {
      width: 100% !important;
    }
    .el-table__empty-block {
      width: auto !important;
    }
    .el-table__header-wrapper {
      border: solid 1px #cfd2da;
    }
    th {
      background-color: #f4f5f9;
    }
    .el-table__body {
      background-color: #f7f8f9;
      border-spacing: 0 1px;
      border-collapse: separate;
      width: 100% !important;
    }
    tr {
      background: white;
      border-radius: 2px;
    }
    th,
    td {
      text-align: left;
      padding-left: 20px;
    }
    .el-table th > .cell {
      text-transform: capitalize !important;
      font-size: 13px;
      font-weight: 500;
      font-stretch: normal;
      font-style: normal;
      line-height: normal;
      letter-spacing: 0.5px;
      color: #324056;
    }
    .el-table td > button {
      border: none;
    }
    .el-table__body-wrapper {
      overflow-y: scroll;
      max-height: calc(100vh - 200px);
    }
    tr.el-table__row.report-row > td {
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      padding-right: 25px;
    }
    .el-table__body tr:hover {
      border-radius: 3px;
      border: solid 1px #ebedf4;
      background-image: linear-gradient(to bottom, #fff -44%, #f5f5f5 108%);
      .table-btn-container {
        display: inline;
      }
    }
    .el-table--enable-row-transition .el-table__body td.el-table__cell {
      transition: background-color 15s linear !important;
    }
    .el-table thead {
      height: 46px;
    }
  }
  .report-view-table-fullscreen {
    padding: 30px 10px 0 10px;
    .el-table__header {
      width: 100% !important;
    }
    .el-table__empty-block {
      width: auto !important;
    }
    .el-table__header-wrapper {
      border: solid 1px #cfd2da;
    }
    th {
      background-color: #f4f5f9;
    }
    .el-table__body {
      background-color: #f7f8f9;
      border-spacing: 0 1px;
      border-collapse: separate;
      width: 100% !important;
    }
    tr {
      background: white;
      border-radius: 2px;
    }
    th,
    td {
      text-align: left;
      padding-left: 20px;
    }
    .el-table th > .cell {
      text-transform: capitalize !important;
      font-size: 14px;
      font-weight: 500;
      font-stretch: normal;
      font-style: normal;
      line-height: normal;
      letter-spacing: 0.5px;
      color: #324056;
    }
    .el-table td > button {
      border: none;
    }
    .el-table__body-wrapper {
      overflow-y: scroll;
      max-height: calc(100vh - 200px);
    }
    tr.el-table__row.report-row > td {
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      padding-right: 25px;
    }
    .el-table__body tr:hover {
      border-radius: 3px;
      border: solid 1px #ebedf4;
      .table-btn-container {
        display: inline;
      }
    }
    .el-table__body tr:active {
      background: linear-gradient(to bottom, red -44%, #f5f5f5 108%) !important;
    }
    .el-table thead {
      height: 46px;
    }
  }
  .el-button {
    padding: 5px 4px;
  }
  .edit-button {
    padding: 3px 6px;
    border-radius: 4px;
    border: solid 1px #38b2c2;
    font-family: Roboto, Arial, sans-serif;
    font-size: 14px;
    font-weight: 500;
    font-stretch: normal;
    font-style: normal;
    line-height: 1.43;
    letter-spacing: normal;
    text-align: center;
    color: #38b2c2;
    margin-left: 20px;
    span {
      display: flex;
      align-items: center;
      justify-content: space-evenly;
      width: 50px;
    }
    .inline {
      padding-right: 5px;
    }
    .report-row {
      border-radius: 3px;
      border: solid 1px #ebedf4;
    }
    .cls-2 {
      fill: #38b2c2 !important;
    }
    .cls-1 {
      fill: none;
    }
    &:hover {
      border: solid 1px #38b2c2;
      background-color: #38b2c2;
      color: #fff;

      .cls-2 {
        fill: #fff !important;
      }
    }
  }

  .report-summary {
    width: 320px;
  }
  .header-bar {
    width: 100%;
    align-self: flex-start;
  }
  .header-bar-background {
    background: #fff;
    border: solid 1px #ebedf4;
  }
}
.default-view-page {
  width: 100%;
  height: 100vh;
  background-color: #fff;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  position: relative;
}
.default-image-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  width: 400px;
  margin-bottom: 200px;
}
.default-image {
  width: 350px;
}
.new-pivot-btn {
  width: 143px;
  height: 36px;
  padding: 10px 10px;
  border-radius: 3px;
  font-size: 13px;
  font-weight: 500;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: normal;
  color: #fff;
  background-color: #ff3184;
  position: absolute;
  right: 23px;
  top: 12px;
  cursor: pointer;
  &:hover {
    box-shadow: 0 3px 14px 0 #fec1d9;
    background-color: #f31f74;
  }
}
.header-btn {
  top: 12px;
}
.search-container-header {
  position: absolute;
  right: 165px;
  top: 11px;
  width: 260px;
  height: 36px;

  .fc-input-full-border2 .el-input__inner {
    padding-left: 35px !important;
    margin-top: 1px;
    height: 36px !important;
    border-radius: 5px !important;
  }
  .el-icon-search {
    padding-left: 10px;
  }
}
.pR23 {
  padding-right: 23px !important;
}
.move-btn {
  border: none;
  background-color: inherit;
  cursor: pointer;
  display: inline-block;
  color: #324056;
  font-size: 14px;
  padding: 10px 68px 10px 0;
  display: flex;
  align-items: center;
}
.folder-option {
  height: 30px;
  margin: 10px 0;
  cursor: pointer;
  &:hover {
    background-color: #e5efff;
  }
}
.pivot-folder-option {
  max-height: 50px;
  padding: 8px;
  overflow: scroll;
  cursor: pointer;
  &:hover {
    background-color: #e5efff;
  }
}
.el-breadcrumb__inner {
  font-weight: 500 !important;
}
.hamburger-menu {
  position: absolute;
  z-index: 99;
  left: 70px;
  top: 23px;
  scale: 1.5;
  padding-top: 1px;
  height: 20px;
  width: 22px;
  justify-content: center;
  display: flex;
  &:hover {
    // background-color: #ebedf4 !important;
    // border-radius: 15%;
    cursor: pointer;
    // padding-top: 1px;
    // height: 20px;
    // width: 22px;
    // justify-content: center;
    // display: flex;
  }
}
.ham-menu-active {
  background-color: #ebedf4 !important;
  border-radius: 15%;
  height: 20px;
  width: 22px;
  display: flex;
  justify-content: center;
  margin-top: 10px;
  margin-left: 10px;
  //height:25.5px;
}

.header-bar {
  .el-breadcrumb__inner,
  .current-folder {
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
  }
  .pivot-summary-section {
    width: calc(100vw - 378px);
  }

  element.style {
    max-height: 566px;
  }
  .pivot-summary-section {
    .el-table__body-wrapper::-webkit-scrollbar {
      display: none !important;
    }
  }
}
.more-button {
  .el-button {
    border: none;
  }
  .el-button:hover {
    background-color: #ebedf4 !important;
    color: black !important;
    border-radius: 50%;
  }
  .el-button:focus {
    background-color: #ebedf4 !important;
    color: black !important;
    border-radius: 50%;
  }
}

.table-btn-container {
  display: none;
  margin-right: 20px;
  .el-button {
    background: transparent !important;
  }
}
.el-dropdown-menu__item:hover {
  background-color: #e5efff !important;
}
.pivot-crumb {
  text-overflow: ellipsis;
  white-space: nowrap;
  overflow: hidden;
  font-size: 14px;
  font-weight: 500 !important;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: normal;
  color: #324056;
  display: flex;
  align-items: center;
  flex-direction: column;
  cursor: pointer;
  margin-right: 10px;
}
.folderpath-crumb {
  span {
    color: #d0d1d3;
  }
  i {
    color: #d0d1d3;
  }
}
.edit-btn,
.more-btn {
  border: none;
  &:hover {
    color: black;
    background-color: #ebedf4 !important;
    border-radius: 50%;
  }
}

.edit-btn {
  .cls-1 {
    fill: none;
  }
}

.pivot-error-container {
  margin-top: 200px;
}
.pR24 {
  padding-right: 24px;
}
.el-popover__title {
  margin-left: 8px !important;
  font-size: 14px;
  letter-spacing: 0.5px;
}
</style>
<style lang="scss" scoped>
.el-dropdown-menu__item {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #324056;
  .inline {
    margin-right: 10px;
  }
}
.dialog-footer.row {
  position: unset !important;
  width: 100%;
  bottom: 0;
  left: 0px;
}
.clone_dialog {
  float: left;
  padding: 20px 30px 0 !important;
}
</style>
<style lang="scss">
// Unscoped css to override element-ui styles.
.pivot-share-dialog {
  .el-dialog .el-dialog__body {
    padding: 0px 0px !important;
  }
}
</style>
