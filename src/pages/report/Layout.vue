<template>
  <div
    :key="currentModuleName"
    class="height-100 report-layout-pivot flex-middle"
  >
    <iframe
      v-if="exportDownloadUrl"
      :src="exportDownloadUrl"
      style="display: none;"
    ></iframe>
    <el-dialog
      :visible="loading"
      :show-close="false"
      :append-to-body="true"
      :close-on-click-modal="false"
      width="0"
    >
      <img src="~statics/pivot/circle-loading.svg" />
    </el-dialog>
    <div
      v-if="!leftPaneVisibility && folderView"
      :class="`hamburger-menu reports`"
      @click="handleBackButton"
    >
      <!-- <img src="~statics/pivot/menu.svg" width="18px" height="18px" /> -->
      <i class="el-icon-back" />
    </div>
    <div class="row reports-layout">
      <div class="col-3" id="sideBar">
        <report-sidebar
          v-show="leftPaneVisibility"
          class="report-summary"
          ref="reportSidebar"
          :selectedFolderObj="selectedFolderObj"
          :moduleName="$attrs.moduleName"
          :appId="appId"
          :newReportTree="newReportTree"
          :tabId="$attrs.tabId"
          :perPage="perPage"
          @foldermanager="foldermanager"
          @folderchanged="loadfolder"
          @reportsList="reportsList"
          @paginationDetails="paginationDetails"
        ></report-sidebar>
      </div>
    </div>
    <div v-if="leftPaneVisibility && newReportTree.length" class="header-bar">
      <div
        class="report-header-and-summary header-bar-background"
        v-if="isFolderClicked || isReportPrevied"
        :style="!leftPaneVisibility ? 'border-left:none' : ''"
      >
        <div
          class="report-name-section"
          :style="!leftPaneVisibility ? 'padding-left:50px;' : ''"
          v-if="isFolderClicked"
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
              v-model="selectedFolderObj"
            >
              <div class="breadcrumb-text-folder" :style="'color:black;'">
                {{ selectedFolderObj.name }}
              </div>
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div
          class="flex-center-row-space fc-black-small-txt-12"
          :class="[currentModuleName === 'energydata' ? 'noicon' : '']"
        >
          <el-tooltip
            effect="dark"
            :content="$t('common._common.search_report')"
            placement="left"
          >
            <div v-if="showSearchIcon" @click.stop="showSearchIcon = false">
              <slot name="icon">
                <InlineSvg
                  src="svgs/search"
                  class="d-flex mT3 cursor-pointer"
                  iconClass="icon icon-sm self-center mR5"
                ></InlineSvg>
              </slot>
            </div>
            <OutsideClick
              v-else
              :visibility="true"
              @onOutsideClick="showSearchIcon = true"
            >
              <div class="search-container-header pL33">
                <el-input
                  placeholder="Search Report"
                  v-model="searchText"
                  type="search"
                  :prefix-icon="'el-icon-search'"
                  class="fc-input-full-border2 pL5"
                  @input="searchTriggered(searchText)"
                >
                </el-input>
              </div>
            </OutsideClick>
          </el-tooltip>
          <span class="separator">|</span>
          <el-popover
            placement="bottom"
            trigger="click"
            :disabled="disablePopup"
          >
            <div slot="reference" class="p8" :class="['button-hover pointer']">
              <div class="flex-middle justify-between lp-page">
                <span v-if="from !== to">{{ from }} - </span>
                <span>{{ to }}</span
                ><span class="d-flex">
                  <span>{{ ' of ' }}</span>
                  <span
                    v-if="shimmerLoading"
                    class="count-shimmer-line loading-shimmer mT1 "
                  ></span>
                  <span v-else>{{ total }}</span>
                </span>
              </div>
            </div>
          </el-popover>
          <span>
            <span
              class="el-icon-arrow-left pagination-arrow f16 pointer p5 fw-bold"
              @click="from > 1 && prev()"
              :class="{ disable: from <= 1 }"
            ></span>
            <span
              class="el-icon-arrow-right pagination-arrow  f16 pointer p5 fw-bold"
              @click="canGoNextPage && next()"
              :class="{ disable: !canGoNextPage }"
            ></span>
          </span>
          <span class="separator">|</span>
          <el-tooltip
            effect="dark"
            :content="$t('common._common.sort')"
            placement="right"
          >
            <el-popover
              v-model="visible"
              placement="bottom"
              trigger="click"
              popper-class="portal-sort-container"
              :visible-arrow="true"
            >
              <InlineSvg
                slot="reference"
                src="new-sortby"
                class="d-flex pointer user-select-none pR0"
                iconClass="icon self-center mR5"
              ></InlineSvg>
              <div class="sort-list-container">
                <div
                  v-for="(type, name) in orderTypeList"
                  :key="name"
                  class="list-item"
                  :class="['list-item', orderType === name && 'active']"
                  @click="updateOrder(name)"
                >
                  {{ type }}
                </div>
              </div>
            </el-popover>
          </el-tooltip>
          <span class="separator">|</span>
        </div>
        <div
          v-if="
            currentModuleName &&
              currentModuleName != 'energydata' &&
              !$helpers.isPortalUser() &&
              hasCreateEditPermission
          "
          class="new-pivot-btn header-btn"
          :style="isReportPrevied ? 'display:none' : ''"
          @click="createNewReport"
        >
          <i class="el-icon-plus pR5 fwBold" style="transform: scale(0.9);"></i
          >{{ $t('common.wo_report.new_report') }}
        </div>
      </div>
      <div v-if="leftPaneVisibility" class="report-view-table">
        <el-table
          :data="reportlist"
          @row-click="loadReport"
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
                  @click.stop="editReport(scope.row.id, scope.row)"
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
                      >{{
                        $t('common.wo_report.delete_report_option')
                      }}</el-dropdown-item
                    >
                    <el-dropdown-item
                      v-if="
                        scope.row.type != 3 &&
                          scope.row.type != 1 &&
                          hasCreateEditPermission
                      "
                      command="duplicate"
                    >
                      <InlineSvg
                        src="svgs/pivot/duplicate"
                        iconClass="icon-sm flex icon"
                      ></InlineSvg>
                      {{
                        $t('common.wo_report.duplicate_report_option')
                      }}</el-dropdown-item
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
                          v-for="folder in newReportTree.filter(
                            folders => folders.id != selectedFolderObj.id
                          )"
                          :key="folder.id"
                          class="pivot-folder-option"
                          @click="
                            moveReport(selectedFolderObj, folder, scope.row)
                          "
                        >
                          {{ folder.name }}
                        </div>
                        <button slot="reference" class="move-btn">
                          <InlineSvg
                            src="svgs/pivot/Move"
                            iconClass="icon-sm flex icon"
                          ></InlineSvg>
                          {{ $t('common.wo_report.move_report_option') }}
                        </button>
                      </el-popover>
                    </el-dropdown-item>
                    <el-dropdown-item
                      v-if="scope.row.type != 3 && hasExportPermission"
                      command="export"
                    >
                      <el-popover placement="left" width="185" trigger="hover">
                        <div
                          class="pivot-folder-option"
                          @click="triggerExport(1, scope.row)"
                        >
                          {{ $t('common.wo_report.export_csv_report_option') }}
                        </div>
                        <div
                          class="pivot-folder-option"
                          @click="triggerExport(2, scope.row)"
                        >
                          {{
                            $t('common.wo_report.export_excel_report_option')
                          }}
                        </div>
                        <div
                          class="pivot-folder-option"
                          @click="triggerExport(3, scope.row)"
                        >
                          {{ $t('common.wo_report.export_pdf_report_option') }}
                        </div>
                        <div
                          class="pivot-folder-option"
                          @click="triggerExport(4, scope.row)"
                        >
                          {{
                            $t('common.wo_report.export_image_report_option')
                          }}
                        </div>
                        <button slot="reference" class="move-btn">
                          <InlineSvg
                            src="svgs/pivot/Export"
                            iconClass="icon-sm flex icon"
                          ></InlineSvg>
                          {{ $t('common.wo_report.export_report_option') }}
                        </button>
                      </el-popover>
                    </el-dropdown-item>
                    <el-dropdown-item
                      v-if="scope.row.type != 3 && hasSchedulePermission"
                      command="Schedule"
                    >
                      <img
                        class="report-icon flex icon"
                        src="~statics/report/calendar.svg"
                        style="height: 14px;width: 14px;margin-left: 2px;margin-right: 8px;"
                      />
                      {{ $t('common.wo_report.schedule_report_option') }}
                    </el-dropdown-item>
                    <el-dropdown-item v-if="scope.row.type != 3" command="Mail">
                      <img
                        class="report icon flex icon"
                        src="~statics/report/email.svg"
                        style="height: 14px;width: 14px;margin-left: 2px;margin-right: 8px;"
                      />
                      {{ $t('common.wo_report.sendmail_report_option') }}
                    </el-dropdown-item>
                    <el-dropdown-item
                      v-if="scope.row.type != 3 && hasCreateEditPermission"
                      command="CloneReport"
                    >
                      <i
                        style="margin-left: 2px;margin-right: 8px"
                        :class="['fa fa-clone']"
                        data-position="top"
                        data-arrow="true"
                        :title="$t('home.dashboard.duplicate_report')"
                        v-tippy
                      ></i>
                      {{ $t('common.wo_report.clone_report_option') }}
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
              </div>
              <column-selection-dialog
                :moduleName="moduleName"
                :fields="reportObject ? reportObject.underlyingFields : null"
                v-if="
                  showColumnSelectionDialog && showDialogId === scope.row.id
                "
                :visibility.sync="showColumnSelectionDialog"
                @save="onColumnSelection"
              ></column-selection-dialog>
              <div class="dashboard-share dialog-box" v-if="isClone">
                <el-dialog
                  title="Clone Report"
                  :visible.sync="isClone"
                  width="35%"
                  class="fc-dialog-center-container report-clone-dialog"
                  :append-to-body="true"
                >
                  <div>
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
                        :loading="Saving"
                        @click="cloneReporttoAnotherApp(isCloneToAnotherApp)"
                      >
                        {{ $t('common._common._save') }}
                      </el-button>
                    </span>
                  </div>
                </el-dialog>
              </div>
              <el-dialog
                :title="$t('common._common.export')"
                :visible.sync="exportDialog"
                width="30%"
                :append-to-body="true"
                class="fc-dialog-center-container"
              >
                <div class="height100px">
                  <el-radio-group v-model="exportOption">
                    <el-radio :label="1" class="fc-radio-btn">{{
                      $t('common._common.tabular_report')
                    }}</el-radio>
                    <el-radio :label="2" class="fc-radio-btn">{{
                      $t('common._common.underlying_data')
                    }}</el-radio>
                  </el-radio-group>
                </div>
                <div class="modal-dialog-footer">
                  <el-button
                    @click="exportDialog = false"
                    class="modal-btn-cancel"
                    >{{ $t('common._common.cancel') }}</el-button
                  >
                  <el-button
                    type="primary"
                    @click="newExportData"
                    class="modal-btn-save"
                    >{{ $t('common._common.export') }}</el-button
                  >
                </div>
              </el-dialog>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
    <div
      v-if="!newReportTree.length && !changeComponent && reportNumber"
      class="default-view-page"
    >
      <div class="default-image-container">
        <img src="~statics/pivot/default-img.png" class="default-image" />
        <a>{{ $t('common.wo_report.learn_more_about_reports') }}</a>
      </div>
      <div
        v-if="
          currentModuleName &&
            currentModuleName != 'energydata' &&
            !$helpers.isPortalUser() &&
            hasCreateEditPermission
        "
        class="new-pivot-btn"
        @click="createNewReport"
      >
        <i class="el-icon-plus pR5 fwBold" style="transform: scale(0.9);"></i
        >{{ $t('common.wo_report.new_report') }}
      </div>
    </div>
    <f-dialog
      :title="$constants.FILE_FORMAT[exportType] + ' Settings'"
      v-if="showImageOptionsDialog"
      :visible="showImageOptionsDialog"
      width="30%"
      @save="
        showImageOptionsDialog = false
        exportData(exportType)
      "
      @close="closeImageOptionsDialog"
      class="print-image-options"
      confirmTitle="Export"
    >
      <div class="mB20 mT20">
        <div>
          <el-checkbox v-model="showHeader">{{
            $t('common.wo_report.include_title')
          }}</el-checkbox>
        </div>
        <div class="mT10">
          <el-checkbox v-model="showPrintDetails">{{
            $t('common.wo_report.include_description')
          }}</el-checkbox>
        </div>
      </div>
    </f-dialog>
    <email-report
      v-if="visibility.showEmailDialog"
      :moduleName="moduleName"
      :visibility.sync="visibility.showEmailDialog"
      :params="params"
      :mailParams="mailParams"
      :url="url"
      :report="report ? report : {}"
    ></email-report>
    <schedule-report
      :moduleName="moduleName"
      v-if="visibility.showScheduleDialog"
      :visibility.sync="visibility.showScheduleDialog"
      :report="report ? report : {}"
      :reportsList="list"
      :webtabId="$attrs.tabId"
      :appId="appId"
    ></schedule-report>
    <router-view v-if="changeComponent" @goBack="goBack"></router-view>
  </div>
</template>
<script>
import { Message } from 'element-ui'
import ReportSidebar from './ReportSidebarNew'
import ReportHelper from 'pages/report/mixins/ReportHelper'
import ReportUtil from 'pages/report/mixins/ReportUtil'
import UserAvatar from '@/avatar/User'
import NewDateHelper from '@/mixins/NewDateHelper'
import ModularAnalyticmixin from 'src/pages/energy/analytics/mixins/ModularAnalyticmixin'
import ScheduleReport from 'pages/report/forms/ScheduleReportNew'
import EmailReport from 'pages/report/forms/EmailReportNew'
import NewDataFormatHelper from './mixins/NewDataFormatHelper'
import ColumnSelectionDialog from 'src/pages/report/components/ModuleColumnSelection'
import OutsideClick from 'src/components/OutsideClick'
import FDialog from '@/FDialogNew'
import debounce from 'lodash/debounce'
import { getApp } from '@facilio/router'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import ReportTabPermissions from 'pages/report/mixins/ReportTabPermissions'
import {
  isWebTabsEnabled,
  findRouteForReport,
  findRouteForTab,
  pageTypes,
} from '@facilio/router'
export default {
  mixins: [
    ReportHelper,
    ModularAnalyticmixin,
    ReportUtil,
    NewDataFormatHelper,
    NewDateHelper,
    ReportTabPermissions,
  ],
  data() {
    return {
      visible: false,
      isFolderClicked: true,
      isReportPrevied: false,
      isCloneToAnotherApp: false,
      visibility: {
        showEmailDialog: false,
        showScheduleDialog: false,
      },
      selectedFolderObj: null,
      leftPaneVisibility: true,
      duplicateReportToggle: false,
      isClone: false,
      mailParams: null,
      apps: null,
      appId: null,
      Saving: false,
      list: null,
      reportNumber: false,
      folderView: true,
      exportOption: 1,
      folderState: {},
      searchText: null,
      newReportTree: [],
      reportCloneObj: {
        folder: null,
        selectedAppId: null,
        name: null,
      },
      params: null,
      url: null,
      showSearchIcon: true,
      from: null,
      to: null,
      page: 1,
      perPage: 15,
      total: null,
      shimmerLoading: false,
      showColumnSelectionDialog: false,
      showDialogId: null,
      showImageOptionsDialog: false,
      exportDialog: false,
      exportType: null,
      scheduledReportId: null,
      reportObject: null,
      loading: true,
      moduleName: null,
      showHeader: true,
      showPrintDetails: true,
      reportObj: null,
      moduleId: null,
      reportName: null,
      report: null,
      changeComponent: false,
      exportDownloadUrl: null,
      orderTypeName: 'desc',
      orderTypeList: { asc: 'Ascending', desc: 'Descending' },
    }
  },
  title() {
    return 'Reports'
  },
  components: {
    ReportSidebar,
    UserAvatar,
    ScheduleReport,
    EmailReport,
    ColumnSelectionDialog,
    OutsideClick,
    FDialog,
  },
  created() {
    if (
      (this.$route.path.includes('newview') && this.$route.params?.reportid) ||
      this.$route.path.includes('foldermanager')
    ) {
      this.loading = false
      this.changeComponent = true
      this.leftPaneVisibility = false
      if (this.$route.path.includes('foldermanager')) {
        this.folderView = false
      }
    }
  },
  watch: {
    async currentModuleName() {
      await this.init()
    },
    orderTypeName: {
      handler() {
        this.$refs['reportSidebar'].listCall(
          this.selectedFolderObj,
          this.page,
          null,
          this.orderType
        )
      },
    },
  },
  async mounted() {
    await this.init()
  },

  computed: {
    currentModuleName() {
      let { $attrs } = this
      let moduleName
      if (isWebTabsEnabled()) {
        moduleName = $attrs.moduleName
      } else {
        moduleName = this.getCurrentModule().module
      }
      return moduleName
    },
    reportlist() {
      let list = this.list
      if (list && list.length > 0) {
        list.map(report => {
          if (!report.description) {
            report.description = '--'
          }
          report.created_by = !report.createdBy ? '--' : report.createdBy
          report.modified_time = !isNaN(report.modifiedTime)
            ? Date(report.modifiedTime)
            : '--'
          report.modified_by = !report.modifiedBy ? '--' : report.modifiedBy
          return report
        })
      }
      return list
    },
    disablePopup() {
      return !(this.total / this.perPage > 1)
    },
    canGoNextPage() {
      return this.total > this.to
    },
    orderType() {
      return this.orderTypeName ? this.orderTypeName : 'desc'
    },
    analyParams() {
      let params = {}
      if (this.reportObject && this.reportObject.report) {
        let type = typeof this.reportObject.report.chartState
        let chartState =
          type === 'string'
            ? JSON.parse(this.reportObject.report.chartState)
            : this.reportObject.report.chartState
        params['reportId'] = this.reportObject.report.id
        params['startTime'] = this.reportObject.report.dateRange['startTime']
        params['endTime'] = this.reportObject.report.dateRange['endTime']
        params['dateOperator'] = this.reportObject.report.dateOperator
        params['dateOperatorValue'] =
          this.reportObject.dateRange.operatorId === 20
            ? this.reportObject.dateRange.value.join(',')
            : this.reportObject.dateRange.offset
        params['fileFormat'] = this.exportType
        params['mode'] = chartState.common.mode
        params['newFormat'] = true
        params['chartType'] = chartState.type
        params['url'] = this?.url ? this.url : ''
      }
      return params
    },
  },
  methods: {
    async init() {
      this.loading = this.changeComponent ? false : true
      await this.availableApps()
      await this.loadNewReportTree()
    },
    searchTriggered: debounce(function(searchText) {
      if (searchText != null) {
        this.$refs['reportSidebar'].expand(
          this.selectedFolderObj,
          1,
          searchText
        )
      }
    }, 1000),
    next() {
      let searchText =
        this.searchText != '' && this.searchText != null
          ? this.searchText
          : null
      this.from = this.to + 1
      if (!isEmpty(this.total)) {
        let to = this.from + this.perPage - 1

        if (to > this.total) to = this.total
        this.to = to
      }
      this.page++
      this.orderTypeName = 'desc'
      this.$refs['reportSidebar'].listCall(
        this.selectedFolderObj,
        this.page,
        searchText
      )
    },
    prev() {
      let searchText =
        this.searchText != '' && this.searchText != null
          ? this.searchText
          : null
      this.to = this.from - 1
      this.from -= this.perPage
      if (this.from <= 1) {
        this.from = 1
        this.page = 1
      } else {
        this.page--
      }
      this.orderTypeName = 'desc'
      this.$refs['reportSidebar'].listCall(
        this.selectedFolderObj,
        this.page,
        searchText
      )
    },
    updateOrder(name) {
      this.orderTypeName = name
    },
    paginationDetails(from, to, count) {
      this.from = from
      this.to = to
      this.total = count
    },
    async openFirstFolder(folder) {
      if (this.$refs['reportSidebar']) {
        await this.$refs['reportSidebar'].expand(folder)
        this.$refs['reportSidebar'].getPaginationDetails()
      }
    },
    reportsList(reportsList) {
      this.list = reportsList
      this.loading = false
    },
    loadfolder(folder) {
      if (!isWebTabsEnabled()) {
        this.moduleName = this.getCurrentModule().module
      }
      this.selectedFolderObj = folder
      let url = null
      if (this.$route.meta.module === 'custom') {
        url = `/app/ca/reports?folderId=` + this.selectedFolderObj.id
      } else {
        url = this.getModularReportURL(this.moduleName)
        url += '?folderId=' + this.selectedFolderObj.id
      }
      if (isWebTabsEnabled()) {
        let { name } = findRouteForTab(this.$attrs.tabId, pageTypes.REPORT_VIEW)
        url = this.$router.resolve({
          name,
          query: { folderId: this.selectedFolderObj.id },
        }).href
      }
      if (this.leftPaneVisibility) {
        this.$router.replace({ path: url })
        history.replaceState({}, '', window.location.origin + url)
      }
    },
    goBack() {
      this.leftPaneVisibility = true
      this.changeComponent = false
    },
    async sendmail(id, name, report) {
      await this.getReportDetails(id)
      this.getRoute(id)
      if (this.reportObject.module.name === 'energydata' && this.analyParams) {
        this.mailParams = this.analyParams
      }
      this.visibility.showEmailDialog = true
      this.scheduledReportId = id
      this.moduleName = name
      this.reportName = report.name
      this.report = report
    },
    Schedulereport(id, name, report) {
      this.visibility.showScheduleDialog = true
      this.scheduledReportId = id
      this.moduleName = name
      this.reportName = report.name
      this.report = report
    },
    duplicateReport(reportId, report) {
      this.duplicateReportToggle = true
      this.editReport(reportId, report)
    },
    async availableApps() {
      await API.get(`v2/application/list`).then(({ data, error }) => {
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.apps = data.application
          this.appId = (getApp() || {}).id
          if (isEmpty(this.appId)) {
            let defaultApp =
              this.apps.find(app => app.linkName === 'newapp') || this.apps[0]
            this.appId = defaultApp.id
            this.reportCloneObj.selectedAppId = this.appId
          }
          this.reportCloneObj.selectedAppId = this.appId
        }
      })
    },
    newExportData() {
      this.exportDialog = false
      if (this.exportOption === 1) {
        this.exportData(this.exportType)
      } else {
        // export underlying data
        this.$message({
          message:
            'Exporting as ' + this.$constants.FILE_FORMAT[this.exportType],
          showClose: true,
        })
        let url =
          '/exportModule?type=' +
          this.exportType +
          '&moduleName=' +
          this.moduleName +
          '&viewName=all'
        let params = null
        if (
          this.reportObject?.filters?.underlyingDataFilters &&
          Object.keys(this.reportObject.filters.underlyingDataFilters)
            .length !== 0
        ) {
          params =
            '&filters=' +
            JSON.stringify(this.reportObject.filters.underlyingDataFilters)
        }

        this.$http
          .post(url, params)
          .then(response => {
            this.$message.close()
            this.exportDownloadUrl = response.data.fileUrl
          })
          .catch(() => {
            this.$error.message(this.$t('common.wo_report.export_failed'))
          })
      }
    },
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
          cloned_app_id: this.appId,
          report_id: this.report_id,
        }
      }
      if (params) {
        this.Saving = true
        let { data, error } = await API.post('v3/report/clone', {
          data: params,
        })
        this.isClone = false
        if (!error && data?.cloned_report_name) {
          this.$message.success('Report Cloned Successfully')
        } else {
          this.$message.error('Error while cloning report')
        }
      }
      this.Saving = false
    },
    moveReport(currentFolder, newFolder, report) {
      let reportIndex
      // this.$set(this.folderState[currentFolder.id], 'setting', false)
      for (let key in this.newReportTree) {
        if (this.newReportTree[key].id === currentFolder.id) {
          this.list.forEach((reportt, index) => {
            if (reportt.id === report.id) {
              reportIndex = index
            }
          })
        }
      }

      API.put('/v3/report/moveto', {
        reportId: report.id,
        folderId: newFolder.id,
      })
        .then(response => {
          this.list.splice(reportIndex, 1)
        })
        .catch(() => {
          this.$message({
            message: this.$t('common.wo_report.cannot_move_report'),
            type: 'error',
          })
        })
    },
    createNewReport() {
      this.newModularReport()
    },
    newModularReport() {
      let moduleName = this.$attrs.moduleName
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
      } else {
        moduleName = this.getCurrentModule().module
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
    loadReport(row) {
      let reportId = row.id
      this.moduleName = this.$attrs.moduleName
      let reportlink = this.getNewReportLink(reportId)
      this.$router.push({ path: reportlink })
      this.leftPaneVisibility = false
      this.changeComponent = true
      this.loading = false
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
    async editReport(id, report) {
      let moduleName = null
      moduleName = !isWebTabsEnabled() ? this.getCurrentModule().module : ''
      let path = this.getModularReportEditURL(moduleName)
      if (moduleName === 'custom') {
        moduleName = report.module.name
      }
      if (
        moduleName === 'energydata' ||
        (isWebTabsEnabled() && this.$attrs.moduleName === 'energydata')
      ) {
        let resultObj
        let routePath
        let {
          ANALYTIC_PORTFOLIO,
          ANALYTIC_BUILDING,
          HEAT_MAP,
          ANALYTIC_SITE,
          TREE_MAP,
          REGRESSION,
          SCATTER,
        } = pageTypes
        let url = '/v3/report/reading/view?reportId=' + id
        let { error, data } = await API.get(url)
        if (!error) {
          resultObj = data
        }

        switch (resultObj.report.analyticsType) {
          case 1:
            if (resultObj.filters && resultObj.filters.xCriteriaMode) {
              routePath = { path: 'portfolio', pageType: ANALYTIC_PORTFOLIO }
            } else {
              routePath = { path: 'building', pageType: ANALYTIC_BUILDING }
            }
            break
          case 3:
            routePath = { path: 'heatmap', pageType: HEAT_MAP }
            break
          case 6:
            routePath = { path: 'site', pageType: ANALYTIC_SITE }
            break
          case 7:
            routePath = { path: 'treemap', pageType: TREE_MAP }
            break
          case 8:
            routePath = { path: 'scatter', pageType: SCATTER }
            break
          default:
            routePath = { path: 'building', pageType: ANALYTIC_BUILDING }
        }

        if (this.isRegressionReport) {
          routePath = { path: 'regression', pageType: REGRESSION }
        }
        if (isWebTabsEnabled()) {
          let { name } = findRouteForTab(routePath.pageType) || {}
          let query = { reportId: id }

          if (this.duplicateReportToggle === true) {
            query.duplicate = true
          }
          name &&
            this.$router.push({
              name,
              query,
            })
        } else {
          let path = '/app/em/analytics/' + routePath.path
          if (this.duplicateReportToggle === true) {
            this.$router.push({
              path: path,
              query: { reportId: id, duplicate: true },
            })
          } else {
            this.$router.push({ path: path, query: { reportId: id } })
          }
        }
      } else {
        if (this.$helpers.isEtisalat()) {
          path = '/app/em/modulereports/new'
        } else if (this.isCustomModule) {
          path = '/app/ca/reports/new'
        }
        if (isWebTabsEnabled()) {
          moduleName = this.$attrs.moduleName
          if (moduleName === 'inspectionResponse') {
            moduleName = 'inspectionTemplate'
          }
          let { name } =
            findRouteForReport('module_reports', pageTypes.REPORT_FORM, {
              moduleName: moduleName,
            }) || {}
          let query = {
            reportId: id,
            module: moduleName ? moduleName : null,
          }

          if (this.duplicateReportToggle === true) {
            query.duplicate = true
          }
          if (name) {
            this.$router.push({
              name,
              query,
            })
          } else if (isEmpty(name)) {
            let parentModule = ReportHelper.findModuleFromSubmodule(
              this.moduleName
            )
            let { name } =
              findRouteForReport('module_reports', pageTypes.REPORT_FORM, {
                moduleName: parentModule,
              }) || {}
            if (name) {
              this.$router.push({
                name,
                query,
              })
            }
          }
        } else {
          if (this.duplicateReportToggle) {
            this.$router.push({
              path: path,
              query: {
                reportId: id,
                duplicate: true,
                module: moduleName ? moduleName : null,
              },
            })
          } else {
            this.$router.push({
              path: path,
              query: {
                reportId: id,
                module: moduleName ? moduleName : null,
              },
            })
          }
        }
      }
    },
    handleCommand(command, isCloneToAnotherApp, row) {
      let report = !row ? this.report : row
      if (command == 'delete') {
        this.deleteReport(row.id)
      } else if (command == 'move') {
        this.showMovePopOver = true
      } else if (command == 'duplicate') {
        this.duplicateReport(row.id, row)
      } else if (command == 'Mail') {
        this.sendmail(report.id, report.module.name, report)
      } else if (command == 'CloneReport') {
        this.openClonePopup(report.id, report.name, isCloneToAnotherApp)
      } else if (command == 'Schedule') {
        this.Schedulereport(row.id, row.module.name, row)
      }
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
    foldermanager() {
      this.leftPaneVisibility = false
      this.changeComponent = true
      this.folderView = false
    },
    async loadNewReportTree() {
      let self = this
      self.loading = this.changeComponent ? false : true
      let moduleName = null
      let params = {
        appId: this.appId,
      }
      if (isWebTabsEnabled()) {
        moduleName = this.$attrs.moduleName
        moduleName += '&webTabId=' + this.$attrs.tabId
      } else {
        moduleName = this.getCurrentModule().module
      }
      if (this.$route.query.module) {
        moduleName = this.$route.query.module
      }
      if (moduleName === 'energy') {
        moduleName = 'energyData'
      }
      let url = '/v3/report/foldersNew?moduleName=' + moduleName
      if (moduleName === 'custom') {
        url = '/v3/report/foldersNew?moduleName=custommodule'
      }
      if (
        isWebTabsEnabled() &&
        (this.$attrs.moduleName == 'newreadingalarm' ||
          this.$attrs.moduleName == 'bmsalarm')
      ) {
        url =
          '/v3/report/foldersNew?moduleName=alarm&webTabId=' + this.$attrs.tabId
      }
      API.get(url, params).then(resp => {
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
          if (this.$route.query.folderId) {
            // this.expandFolder.push(parseInt(this.$route.query.folderId))
            let folder = this.newReportTree.find(
              f => f.id == parseInt(this.$route.query.folderId)
            )
            this.selectedFolderObj = folder
          } else if (self.newReportTree.length > 0) {
            this.selectedFolderObj = treeData[0]
            if (this.leftPaneVisibility) {
              this.$router.push({
                path: '?folderId=' + this.selectedFolderObj.id,
              })
            }
          }
          if (self.selectedFolderObj) {
            this.openFirstFolder(self.selectedFolderObj)
          } else {
            self.loading = false
          }
          if (self.newReportTree.length > 0) {
            this.reportNumber = false
          } else {
            this.reportNumber = true
          }
          for (let key in self.newReportTree) {
            let folder = self.newReportTree[key]
            let folderState = {}
            folderState['name'] = folder.name
            folderState['newName'] = folder.name
            folderState['setting'] = false
            folderState['editFolder'] = false
            // self.folderState[folder.id] = folderState
          }
        } else {
          self.loading = false
        }
      })
    },
    async triggerExport(type, row) {
      this.exportType = type
      await this.getReportDetails(row.id)
      if (type === 4) {
        this.getRoute(row.id)
        this.showImageOptionsDialog = true
      } else if (
        this.reportObject &&
        this.reportObject.report &&
        this.reportObject.report.type === 2
      ) {
        if (type === 3) {
          this.getRoute(row.id)
          this.showDialogId = row.id
          this.showColumnSelectionDialog = true
        } else {
          this.exportDialog = true
        }
      } else {
        this.getRoute(row.id)
        this.exportData(type)
      }
    },
    async getReportDetails(id) {
      let params = {}
      params['reportId'] = id
      await API.put(`/v3/report/execute`, params).then(({ data, error }) => {
        if (error) {
          console.log('---Error---', error)
          this.loading = false
          this.failed = true
        } else {
          console.log('This is for workorder reports')
          if (data.criteriaData) {
            this.criteriaObj = data.criteriaData
          }
          let result = data
          this.reportObject = data
          result = this.specialHandler(result)
          this.moduleName = result.module.name
          this.moduleId = result.module.moduleId
          let self = this
          if (result.reportData.data.length === 0) {
            self.loading = false

            if (result.report.dateOperator !== -1) {
              let datePickerObject = NewDateHelper.getDatePickerObject(
                result.report.dateOperator,
                result.report.dateValue
              )
              result['dateRange'] = datePickerObject
              self.dateFilter = datePickerObject
              // self.showDatePicker = true
            }
            result.xAggr = result.report.xAggr
            self.resultObj = result
          } else {
            if (result.report.dateOperator !== -1) {
              let datePickerObject = NewDateHelper.getDatePickerObject(
                result.report.dateOperator,
                result.report.dateValue
              )
              result['dateRange'] = datePickerObject
              self.dateFilter = datePickerObject
            } else {
              result['dateRange'] = self.dateFilter
            }
            result.xAggr = result.report.xAggr
            self.reportObj = this.prepareReport(result)
            self.reportObj['report'] = result.report
            //self.resultObj is old result state here , from previous report load
            self.resultObj = result //setting resultobj to current response result state
            console.log('before emit ')
            // self.$emit('reportLoaded', self.reportObj, self.resultObj)
            self.loading = false
          }
        }
      })
    },
    onColumnSelection(fields) {
      if (!this.exportParams) {
        this.exportParams = {}
      }
      if (fields && fields.length) {
        this.exportParams.fields = fields.join(',')
      }
      this.exportParams.pageLimit = 'all'
      this.showColumnSelectionDialog = false
      this.exportData(this.exportType)
    },
    exportData(type) {
      let params = this.params
      if (params) {
        params.fileFormat = type
        /* params.exportInfo = {
        fileFormat: type
      } */
        params.newFormat = true
      }
      if (type === 3 || type === 4) {
        if (this.exportParams == null) {
          this.exportParams = {}
        }
        this.exportParams.showHeader = this.showHeader
        this.exportParams.showPrintDetails = this.showPrintDetails
      }
      this.$message({
        message: 'Exporting as ' + this.$constants.FILE_FORMAT[type],
        showClose: true,
        duration: 0,
      })

      if (this.moduleName !== 'energydata') {
        let moduleReportExportParams = {}
        if (this.reportObject?.report?.id) {
          moduleReportExportParams['reportId'] = this.reportObject.report.id
            ? this.reportObject.report.id
            : this.$route.params.reportid
          if (this.reportObject && this.reportObject.dateRange) {
            moduleReportExportParams[
              'startTime'
            ] = this.reportObject.dateRange.value[0]
            moduleReportExportParams[
              'endTime'
            ] = this.reportObject.dateRange.value[1]
          }
        } else {
          moduleReportExportParams = { ...this.computedConfig }
          moduleReportExportParams['moduleName'] = this.moduleName
        }
        moduleReportExportParams['fileFormat'] = type
        if (this.exportParams) {
          moduleReportExportParams.exportParams = this.exportParams
        }
        if ((type === 3 || type === 4) && this.url) {
          moduleReportExportParams.url = this.url
        }
        if (
          this?.reportObject?.filters?.underlyingDataFilters &&
          Object.keys(this.reportObject.filters.underlyingDataFilters)
            .length !== 0
        ) {
          moduleReportExportParams['filters'] = JSON.stringify(
            this.reportObject.filters.underlyingDataFilters
          )
        }
        let self = this
        this.$http
          .post('/v2/report/exportModuleReport', moduleReportExportParams)
          .then(response => {
            Message.closeAll()
            if (response.data && response.data.responseCode === 0) {
              self.exportDownloadUrl = response.data.result.fileUrl
            }
          })
      } else {
        let url = '/v2/report/export'
        if (
          this.reportObject &&
          this.reportObject.report &&
          this.reportObject.report.type === 4 &&
          this.reportObject.report.currentTemplate
        ) {
          url =
            url +
            '?templateString=' +
            encodeURIComponent(
              JSON.stringify(this.reportObject.report.currentTemplate)
            )
        }
        if (!params) {
          params = {}
        }
        if (this.analyParams) {
          params = this.analyParams
        }
        if (type === 3 || type === 4) {
          params.exportParams = this.exportParams
        }
        this.$http.post(url, params).then(response => {
          Message.closeAll()
          if (response.data && response.data.responseCode === 0) {
            this.exportDownloadUrl = response.data.result.fileUrl
          }
        })
      }
    },
    closeImageOptionsDialog() {
      this.showImageOptionsDialog = false
    },
    handleReportDelete(obj) {
      if (this.$refs['reportSidebar']) {
        this.$refs['reportSidebar'].removeReport(obj)
      }
    },
    getRoute(id) {
      let moduleName = this.moduleName
      if (isWebTabsEnabled()) {
        let routeObj
        if (!moduleName) {
          routeObj = findRouteForReport(
            'analytics_reports',
            pageTypes.REPORT_VIEW
          )
        } else if (moduleName === 'energydata') {
          routeObj = findRouteForReport(
            'analytics_reports',
            pageTypes.REPORT_VIEW,
            { moduleName }
          )
        } else {
          routeObj = findRouteForReport(
            'module_reports',
            pageTypes.REPORT_VIEW,
            { moduleName }
          )
        }
        let { name } = routeObj || {}
        let url = this.$router.resolve({ name }).href + '/' + id
        this.url = url
      }
    },
    handleBackButton() {
      this.$route.params.reportid = null
      let module = null
      if (!this.leftPaneVisibility) {
        this.isFolderClicked = true
        this.isReportPrevied = false
      }
      let route_url = null
      if (!this.selectedFolderObj) {
        this.leftPaneVisibility = true
        if (!isWebTabsEnabled()) {
          module = this.getCurrentModule().module
          route_url = this.getModularReportURL(module)
        }
      } else {
        this.leftPaneVisibility = !this.leftPaneVisibility
        if (!isWebTabsEnabled()) {
          if (this.$route.name === 'custommodulenewreport') {
            route_url = `/app/ca/reports?folderId=` + this.selectedFolderObj.id
          } else {
            module = this.getCurrentModule().module
            route_url =
              this.getModularReportURL(module) +
              '?folderId=' +
              this.selectedFolderObj.id
          }
        }
      }
      if (isWebTabsEnabled()) {
        let { name } = findRouteForTab(this.$attrs.tabId, pageTypes.REPORT_VIEW)
        route_url = this.$router.resolve({
          name,
          query: { folderId: this.selectedFolderObj.id },
        }).href
      }
      this.$router.replace({ path: route_url })
      history.replaceState({}, '', window.location.origin + route_url)
      this.changeComponent = false
    },
  },
}
</script>
<style>
@media print {
  #printable-area {
    visibility: visible !important;
    width: 100% !important;
    max-width: 100% !important;
    min-width: 100% !important;
    padding: 0px !important;
  }
  #printable-area .view-column-chooser {
    display: none;
  }
  .fc-underlyingdata .fc-chart-table {
    overflow: hidden !important;
    table-layout: fixed;
  }
  .reports-underlyingdata {
    /* doubt */
    padding: 0px;
  }
  .reports-header {
    box-shadow: none !important;
  }
  #sideBar,
  .fc-layout-aside,
  .layout-header,
  .report-options,
  .title-actions,
  .charttype-options,
  .emptyLegends {
    display: none;
  }
  .height100.pL50.pL60 {
    padding: 0px !important;
  }
  .legend.legendsAll {
    max-width: 800px !important;
    margin: auto;
  } /* doubt */
  .printedTime {
    display: block !important;
  }
  .layout-page-container {
    padding-top: 0px !important;
  }
  #printable-area {
    page-break-after: auto;
  }
  table {
    page-break-after: auto;
  }
  tr {
    page-break-inside: avoid;
    page-break-after: auto;
  }
  td {
    page-break-inside: avoid;
    page-break-after: auto;
  }
  thead {
    display: table-header-group;
  }
  tfoot {
    display: table-footer-group;
  }
  main.layout-page {
    min-height: 100% !important;
    height: 100% !important;
  }
  .self-center.fchart-section {
    margin-right: 100px !important;
    width: 100% !important;
  }
  svg.fchart {
    zoom: 80%;
  }
  .fc-report-filter,
  .fc-underlin,
  .table-header {
    display: none !important;
  }
  .chart-table-layout {
    padding-bottom: 0px !important;
  }
  .reports-summary .fc-report-section,
  .fc-underlyingdata {
    box-shadow: none !important;
  }
  .reports-summary .fc-report-section {
    box-shadow: none !important;
    border: solid 0px #e6ebf0;
  }
  .reports-summary .reports-chart {
    padding-left: 0px;
    padding-right: 0px;
    padding-top: 0px;
    border: none;
  }
  .reports-summary .fc-underlyingdata {
    border: solid 1px #e6ebf0;
    box-shadow: none;
  }
  .row,
  .filter-field .pdfDateView,
  .filter-field .Reading-Analysis {
    display: block;
  }
  .scrollable {
    overflow: visible;
  }
  .scrollable {
    padding-top: 0px !important;
  }
  .report-title {
    width: 100% !important;
  }
  .report-title .title,
  .report-title .description {
    text-align: center !important;
  }
  .reports-header {
    border-bottom: none !important;
    height: 100px !important;
  }
  .chart-option {
    display: inline-flex !important;
  }
  body {
    -webkit-print-color-adjust: exact;
    color-adjust: exact;
  }
  .chart-tooltip {
    opacity: 0 !important;
  }
  .reports-summary .fc-underlyingdata,
  .reports-summary .fc-report-section {
    box-shadow: none;
  }
  .reports-summary .reports-chart,
  .reports-underlyingdata {
    background: #fff;
  }
  .hamburger-menu.reports {
    display: none !important;
  }
  .height-100.report-layout-pivot.flex-middle.pL60 {
    padding-left: 0px !important;
  }
}
</style>
<style lang="scss">
.pagination-arrow.disable {
  color: #50506c;
  font-weight: bold;
  opacity: 0.4;
  background: none;
}
.lp-page span {
  padding: 2px;
}
.list-item {
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 10px;

  &.active {
    font-weight: bold;
    color: #39b2c2;
  }
}
.flex-center-row-space.fc-black-small-txt-12.noicon {
  right: 1px;
}
.report-layout-pivot {
  .pivot-report-summary-icon {
    width: fit-content;
  }
  .flex-center-row-space {
    position: absolute !important;
    right: 140px;
    top: 12px;
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
    cursor: default;
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
    align-self: flex-start;
  }
  .header-bar-background {
    background: #fff;
    border: solid 1px #ebedf4;
    box-shadow: 0 3px 4px 0 rgba(218, 218, 218, 0.32);
  }
}
.pL33 {
  padding-left: 33px;
}
.default-view-page {
  width: 100%;
  height: 100%;
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
  right: 245px;
  width: 260px;
  height: 36px;
  top: 1px;

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
