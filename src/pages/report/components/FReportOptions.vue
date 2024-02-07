<template>
  <div>
    <el-button
      :title="$t('common._common.back_to_dashboard')"
      v-if="reportId && isFromDashboard"
      data-position="top"
      data-arrow="true"
      v-tippy
      class="freport-btn"
      style="border: none;"
      @click="$router.back()"
      >{{ $t('common.header.back') }}</el-button
    >
    <el-button
      :title="$t('common._common.back_to_report')"
      v-else-if="reportId"
      data-position="top"
      data-arrow="true"
      v-tippy
      class="freport-btn"
      style="border: none;"
      @click="showConfirmDialog = true"
      >{{ $t('common._common.cancel') }}</el-button
    >
    <UnsavedChangesErrorDialogBox
      v-if="showConfirmDialog"
      :visibility.sync="showConfirmDialog"
      @result="backToReport"
    >
    </UnsavedChangesErrorDialogBox>

    <f-save-as-report
      :template="getReportTemplate()"
      :config="config"
      :moduleName="moduleName"
      :moduleFromRoute="moduleFromRoute"
      :iscustomModule="iscustomModule ? iscustomModule : false"
      :report="reportObject"
      :savedReport="savedReport"
      v-if="
        (checkOption('save') && !isRegression) ||
          (checkOption('save') &&
            isRegression &&
            this.reportObject &&
            this.reportObject.params.regressionType === 'multiple')
      "
      @close="closeSaveDialog"
    >
      <el-button
        slot="reference"
        class="freport-btn newanalytics-save-as-report mL10"
        :disabled="isActionsDisabled"
        @click="visibility.saveAsDialog = true"
        >{{
          reportId
            ? $t('common.wo_report.update_report')
            : $t('common.wo_report.save_as_report')
        }}</el-button
      >
    </f-save-as-report>

    <el-button
      v-if="checkOption('saveExpression')"
      :disabled="
        isActionsDisabled ||
          (reportObject && typeof reportObject.data === 'undefined')
      "
      class="freport-btn newanalytics-save-as-report mL10"
      @click="initSaveExpression()"
      >{{ $t('common._common.create_enpi') }}</el-button
    >
    <FSaveAsRegression
      v-if="checkOption('saveExpression')"
      :reportObject="reportObject"
      :visibility.sync="visibility.saveRegressionExpr"
      @newEnpiConfig="openEnpiDialog"
    >
    </FSaveAsRegression>
    <div v-if="newflow" :style="'display:flex;margin:5px 35px 0 0;'">
      <div
        v-if="reportObject"
        style="height: 26px;width: 74px;margin-bottom: 4px;"
      >
        <el-button
          plain
          @click="editReport(resultObject.report.id)"
          class="edit-button"
          v-if="hasCreateEditPermission"
        >
          <InlineSvg
            src="svgs/pivot/edit"
            iconClass="icon-sm flex icon"
          ></InlineSvg>

          {{ $t('common.wo_report.edit') }}
        </el-button>
      </div>
      <div
        v-if="reportObject"
        class="more-button"
        style="margin-left:20px;margin-bottom:8px;margin-top:2px;transform: rotate(90deg);"
      >
        <el-dropdown
          trigger="click"
          placement="bottom"
          :hide-on-click="false"
          @command="command => handleCommand(command, isCloneToAnotherApp)"
        >
          <el-button icon="el-icon-more" size="medium" @click.stop="">
          </el-button>
          <el-dropdown-menu
            class="reportoptions"
            slot="dropdown"
            style="margin-left:155px;"
          >
            <el-dropdown-item command="delete" v-if="hasDeletePermission">
              <InlineSvg
                src="svgs/pivot/delete-icon"
                iconClass="icon-sm flex icon iconstyle"
              ></InlineSvg
              >{{
                $t('common.wo_report.delete_report_option')
              }}</el-dropdown-item
            >
            <el-dropdown-item
              v-if="
                resultObject.report.type != 1 &&
                  resultObject.report.type != 3 &&
                  hasCreateEditPermission
              "
              command="duplicate"
            >
              <InlineSvg
                src="svgs/pivot/duplicate"
                iconClass="icon-sm flex icon iconstyle"
              ></InlineSvg>
              {{
                $t('common.wo_report.duplicate_report_option')
              }}</el-dropdown-item
            >
            <el-dropdown-item command="move" v-if="hasCreateEditPermission">
              <el-popover
                placement="right"
                width="185"
                title="Folders"
                trigger="hover"
              >
                <div
                  v-for="folder in newReportTree"
                  :key="folder.id"
                  class="pivot-folder-option"
                  @click="handleMoveFolder(folder)"
                >
                  {{ folder.name }}
                </div>
                <button slot="reference" class="move-btn">
                  <InlineSvg
                    src="svgs/pivot/Move"
                    iconClass="icon-sm flex icon iconstyle"
                  ></InlineSvg>
                  {{ $t('common.wo_report.move_report_option') }}
                </button>
              </el-popover>
            </el-dropdown-item>
            <el-dropdown-item
              v-if="resultObject.report.type != 3 && hasExportPermission"
              command="export"
            >
              <el-popover placement="right" width="185" trigger="hover">
                <div
                  class="pivot-folder-option"
                  @click="() => triggerExport('1')"
                >
                  {{ $t('common.wo_report.export_csv_report_option') }}
                </div>
                <div
                  class="pivot-folder-option"
                  @click="() => triggerExport('2')"
                >
                  {{ $t('common.wo_report.export_excel_report_option') }}
                </div>
                <div
                  class="pivot-folder-option"
                  @click="() => triggerExport('3')"
                >
                  {{ $t('common.wo_report.export_pdf_report_option') }}
                </div>
                <div
                  class="pivot-folder-option"
                  @click="() => triggerExport('4')"
                >
                  {{ $t('common.wo_report.export_image_report_option') }}
                </div>
                <button slot="reference" class="move-btn">
                  <InlineSvg
                    src="svgs/pivot/Export"
                    iconClass="icon-xs flex icon iconstyle"
                  ></InlineSvg>
                  {{ $t('common.wo_report.export_report_option') }}
                </button>
              </el-popover>
            </el-dropdown-item>
            <el-dropdown-item
              v-if="resultObject.report.type != 3 && hasSchedulePermission"
              command="Schedule"
            >
              <img
                class="report-icon flex icon"
                src="~statics/report/calendar.svg"
                style="height: 14px;width: 14px;margin-left: 2px;margin-right: 8px;"
              />
              {{ $t('common.wo_report.schedule_report_option') }}
            </el-dropdown-item>
            <el-dropdown-item
              v-if="resultObject.report.type != 3"
              command="Mail"
            >
              <img
                class="report icon flex icon"
                src="~statics/report/email.svg"
                style="height: 14px;width: 14px;margin-left: 2px;margin-right: 8px;"
              />
              {{ $t('common.wo_report.sendmail_report_option') }}
            </el-dropdown-item>
            <el-dropdown-item
              v-if="resultObject.report.type != 3 && hasCreateEditPermission"
              command="CloneReport"
            >
              <i
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
    </div>
    <div v-else :class="optionClass">
      <el-dropdown
        :disabled="isActionsDisabled"
        @command="triggerExport($event)"
        :class="{ 'action-disabled': isActionsDisabled }"
        v-if="checkOption('export')"
      >
        <el-button size="small" type="text" class="fc-chart-btn">
          <img class="report-icon" src="~statics/report/export.svg" />
        </el-button>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item command="1" :disabled="isActionsDisabled">{{
            $t('common.wo_report.export_csv')
          }}</el-dropdown-item>
          <el-dropdown-item command="2" :disabled="isActionsDisabled">{{
            $t('common.wo_report.export_xcl')
          }}</el-dropdown-item>
          <template
            v-if="
              pdf &&
                ((params && params.reportId) || typeof config === 'undefined')
            "
          >
            <el-dropdown-item command="3" :disabled="isActionsDisabled">{{
              $t('common.wo_report.export_pdf')
            }}</el-dropdown-item>
            <el-dropdown-item
              v-if="
                (!optionSettings || optionSettings.chart !== false) &&
                  reportType !== 'pivot'
              "
              command="4"
              :disabled="isActionsDisabled"
              >{{ $t('common.wo_report.export_image') }}
            </el-dropdown-item>
          </template>
        </el-dropdown-menu>
      </el-dropdown>

      <el-button
        size="small"
        type="text"
        :title="$t('home.dashboard.schedule_report')"
        data-position="bottom"
        v-if="checkOption('schedule')"
        data-arrow="true"
        v-tippy
        class="fc-chart-btn"
        @click="visibility.showScheduleDialog = true"
      >
        <img class="report-icon" src="~statics/report/calendar.svg" />
      </el-button>

      <el-button
        size="small"
        :disabled="isActionsDisabled"
        type="text"
        :title="$t('common.wo_report.email_this_report')"
        data-position="bottom"
        data-arrow="true"
        v-tippy
        class="fc-chart-btn"
        @click="visibility.showEmailDialog = true"
        v-if="checkOption('email')"
      >
        <img class="report-icon" src="~statics/report/email.svg" />
      </el-button>
      <outside-click
        :visibility.sync="visibility.showCommentDialog"
        class="comment-dialog"
        style="top: 0;"
      >
        <div>
          <div class="comment-dialog-header">
            <h3
              class="comment-dialog-heading"
              style="text-transform: uppercase;"
            >
              {{ $t('maintenance.wr_list.comments') }}
            </h3>
            <div class="comment-close">
              <el-tooltip
                class="item"
                effect="dark"
                content="Close"
                placement="bottom"
              >
                <i
                  class="el-icon-close"
                  aria-hidden="true"
                  v-on:click="visibility.showCommentDialog = false"
                ></i>
              </el-tooltip>
            </div>
          </div>
          <div class="comment-dialog-body">
            <comments
              module="reportnotes"
              v-bind:notify="false"
              :record="reportCommentObject"
            ></comments>
          </div>
        </div>
      </outside-click>
      <NewEnpi
        v-if="visibility.saveAsEnpi"
        :isNew="true"
        :visibility.sync="visibility.saveAsEnpi"
        :enpi="loadedEnpi"
      ></NewEnpi>
    </div>
    <iframe
      v-if="exportDownloadUrl"
      :src="exportDownloadUrl"
      style="display: none;"
    ></iframe>
    <!-- print options dialog s-->
    <el-dialog
      :visible.sync="dialogVisible"
      width="20%"
      class="fc-dialog-form pdf-dialog-hide"
      :before-close="closeDialog"
    >
      <div class="new-header-container">
        <div class="new-header-text">
          <div class="fc-setup-modal-title">
            {{ $t('common.wo_report.pdf_options') }}
          </div>
        </div>
      </div>
      <div class="mL40 mT20">
        <div>
          <el-checkbox v-model="checked">{{
            $t('common.wo_report.include_legends')
          }}</el-checkbox>
        </div>
        <div class="mT10">
          <el-checkbox v-model="checked">{{
            $t('common.wo_report.include_table')
          }}</el-checkbox>
        </div>
      </div>
      <div class="modal-dialog-footer">
        <el-button @click="dialogVisible = false" class="modal-btn-cancel">{{
          $t('common._common.cancel')
        }}</el-button>
        <el-button
          type="primary"
          @click="save"
          :loading="saving"
          class="modal-btn-save"
          >{{
            saving ? $t('common._common._saving') : $t('common._common._save')
          }}</el-button
        >
      </div>
    </el-dialog>

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
        <el-button @click="exportDialog = false" class="modal-btn-cancel">{{
          $t('common._common.cancel')
        }}</el-button>
        <el-button
          type="primary"
          @click="newExportData"
          class="modal-btn-save"
          >{{ $t('common._common.export') }}</el-button
        >
      </div>
    </el-dialog>

    <!-- Print Image Options -->
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

    <MoveReportToDashBoard
      v-bind:enableMoveDialog.sync="sendToDashBoard"
      :reportObj="savedReportObj"
      moduleName="energyData"
    ></MoveReportToDashBoard>
    <column-selection-dialog
      :moduleName="moduleName"
      :fields="reportObject ? reportObject.underlyingFields : null"
      v-if="showColumnSelectionDialog"
      :visibility.sync="showColumnSelectionDialog"
      @save="onColumnSelection"
    ></column-selection-dialog>
    <email-report
      v-if="visibility.showEmailDialog"
      :moduleReportConfig="computedConfig"
      :moduleName="moduleName"
      :visibility.sync="visibility.showEmailDialog"
      :params="params"
      :report="reportObject ? reportObject.report : {}"
      :url="url"
    ></email-report>
    <schedule-report
      :moduleName="moduleName"
      v-if="visibility.showScheduleDialog"
      :visibility.sync="visibility.showScheduleDialog"
      :report="resultObject ? resultObject.report : {}"
      :appId="appId"
    ></schedule-report>
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
  </div>
</template>
<script>
import { Message } from 'element-ui'
import EmailReport from 'pages/report/forms/EmailReportNew'
import FSaveAsReport from 'pages/energy/analytics/components/FSaveAsReport'
import ScheduleReport from 'pages/report/forms/ScheduleReportNew'
import MoveReportToDashBoard from 'src/pages/energy/analytics/newTools/MoveReportToDashBoard'
import OutsideClick from '@/OutsideClick'
import Comments from '@/relatedlist/Comments2'
import NewEnpi from 'src/pages/setup/new/NewEnPI'
import ReportHelper from 'pages/report/mixins/ReportHelper'
import AnalyticsModelHelper from 'src/pages/report/mixins/FNewAnalyticsModelHelper'
import FSaveAsRegression from 'src/pages/report/components/FSaveRegressionExpr'
import NewReportSummaryHelper from 'src/pages/report/mixins/NewReportSummaryHelper'
import ColumnSelectionDialog from 'src/pages/report/components/ModuleColumnSelection'
import dashboardLayoutHelper from 'pages/dashboard/helpers/newDashboardlayoutHelper'
import UnsavedChangesErrorDialogBox from 'pages/energy/pivot/Components/UnsavedChangesErrorDialogBox.vue'
import { API } from '@facilio/api'
import {
  getApp,
  isWebTabsEnabled,
  pageTypes,
  findRouteForReport,
} from '@facilio/router'
import { isEmpty } from '@facilio/utils/validation'
import FDialog from '@/FDialogNew'
import ReportTabPermissions from 'pages/report/mixins/ReportTabPermissions'

export default {
  props: [
    'isActionsDisabled',
    'resultObject',
    'reportObject',
    'params',
    'optionsToEnable',
    'optionClass',
    'savedReport',
    'moduleName',
    'iscustomModule',
    'config',
    'pdf',
    'optionSettings',
    'moduleFromRoute',
    'reportType',
    'newflow',
  ],
  mixins: [
    NewReportSummaryHelper,
    dashboardLayoutHelper,
    ReportHelper,
    ReportTabPermissions,
  ],
  components: {
    EmailReport,
    FSaveAsReport,
    ScheduleReport,
    MoveReportToDashBoard,
    OutsideClick,
    Comments,
    FSaveAsRegression,
    NewEnpi,
    ColumnSelectionDialog,
    FDialog,
    UnsavedChangesErrorDialogBox,
  },
  data() {
    return {
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
      apps: null,
      appId: null,
      url: null,
      Saving: false,
      folderState: {},
      isCloneToAnotherApp: false,
      showConfirmDialog: false,
      reportTemplate: undefined,
      loadedEnpi: null,
      exportDialog: false,
      checked: false,
      exportDownloadUrl: null,
      exportType: null,
      exportOption: 1,
      visibility: {
        saveAsEnpi: false,
        saveRegressionExpr: false,
        saveAsDialog: false,
        showEmailDialog: false,
        showScheduleDialog: false,
        showCommentDialog: false,
      },
      sendToDashBoard: false,
      savedReportObj: null,
      options: {
        export: 1,
        email: 2,
        print: 3,
        schedule: 4,
        save: 5,
        saveExpression: 6,
      },
      saving: false,
      dialogVisible: false,
      showColumnSelectionDialog: false,
      exportParams: null,
      showImageOptionsDialog: false,
      newReportTree: [],
    }
  },
  watch: {
    activateTemplateSave: {
      handler: function() {
        this.getReportTemplate()
      },
    },
  },
  async mounted() {
    await this.loadNewReportTree()
    this.availableApps()
  },
  computed: {
    activateTemplateSave() {
      let duplicateCase
      if (
        this.reportObject &&
        this.resultObject &&
        this.resultObject.report &&
        this.resultObject.report.type !== 2 &&
        this.resultObject.report.type !== 3
      ) {
        let currentLocation = new Set()

        for (let dataPoint of this.resultObject.report.dataPoints) {
          if (dataPoint.buildingId && dataPoint.buildingId !== -1) {
            currentLocation.add(dataPoint.buildingId)
          }
        }

        if (currentLocation.size === 1) {
          duplicateCase = this.getDuplicateCase(this.reportObject)
          if (
            !isEmpty(duplicateCase) &&
            (duplicateCase.case === 1 || duplicateCase.case === 2)
          ) {
            let isSpaceReadingPresent = false

            for (let dataPoint of this.resultObject.report.dataPoints) {
              let parentId = dataPoint?.metaData?.parentIds[0]
              if (currentLocation.has(parentId)) {
                isSpaceReadingPresent = true
                break
              }
            }

            if (isSpaceReadingPresent) {
              return false
            } else {
              return true
            }
          } else {
            return false
          }
        } else {
          return false
        }
      }
      return false
    },
    isRegression() {
      if (
        this.reportObject &&
        typeof this.reportObject !== 'undefined' &&
        this.reportObject.options &&
        this.reportObject.options.regressionConfig &&
        this.reportObject.options.regressionConfig.length !== 0
      ) {
        return true
      }
      return false
    },
    computedConfig() {
      let config = {}
      if (this.config) {
        ;(config['xField'] = this.config.xField),
          (config['yField'] = this.config.yField ? this.config.yField : null)
        config['dateField'] =
          this.config.dateField &&
          (this.config.isTime || this.config.isCustomDateField)
            ? this.config.dateField
            : null
        config['groupBy'] = this.config.groupBy ? this.config.groupBy : null
        config['criteria'] = this.config.criteria ? this.config.criteria : null
        config['sortFields'] = this.config.sortFields
          ? this.config.sortFields
          : null
        config['sortOrder'] = this.config.sortOrder
          ? this.config.sortOrder
          : null
        config['limit'] = this.config.limit ? this.config.limit : null
        config['moduleType'] = this.config.moduleType
          ? this.config.moduleType
          : null
        return config
      }
      return null
    },
    isFromDashboard() {
      if (this.$route.query && this.$route.query.fromDashboard) {
        return true
      }
      return false
    },
    reportId() {
      if (
        this.$route.query &&
        this.$route.query.reportId &&
        this.$route.query.duplicate
      ) {
        return null
      } else {
        if (typeof this.$route.query.reportId === 'number') {
          return this.$route.query.reportId
        } else {
          return parseInt(this.$route.query.reportId)
        }
      }
    },
    reportCommentObject() {
      let temp = {}
      temp['id'] = parseInt(this.$route.params.reportid)
      return temp
    },
  },
  methods: {
    getDateObj(dateRange) {
      if (!isEmpty(dateRange)) {
        let dateObj = {}
        dateObj.operatorId = dateRange.operatorId
        dateObj.startTime = dateRange.value[0]
        dateObj.endTime = dateRange.value[1]
        return dateObj
      }
    },
    constructUrl(id, type, filterObject, dateObject) {
      let path = 'pdf/report'
      if (type === 'reading') {
        path = 'pdf/readingReport'
      } else if (type === 'alarm') {
        path = 'pdf/alarmReport'
        dateObject = !isEmpty(dateObject)
          ? encodeURIComponent(JSON.stringify(dateObject))
          : null
        let url = `${window.location.protocol}//${window.location.host}/${
          getApp().linkName
        }/${path}?alarmId=${id}&daterange=${dateObject}&isWithPrerequsite=true`
        return url
      }
      let url = `${window.location.protocol}//${window.location.host}/${
        getApp().linkName
      }/${path}/${id}`
      if (!isEmpty(dateObject)) {
        dateObject = encodeURIComponent(JSON.stringify(dateObject))
        url = `${url}?daterange=${dateObject}`
      }
      if (!isEmpty(filterObject)) {
        filterObject = encodeURIComponent(JSON.stringify(filterObject))
        if (!isEmpty(dateObject)) {
          url = `${url}&filters=${filterObject}`
        } else {
          url = `${url}?filters=${filterObject}`
        }
      }
      return url
    },
    printPage() {
      const { report = {} } = this.resultObject ?? this.reportObject
      let url
      if (!isEmpty(report)) {
        if (this.params?.alarmId) {
          url = this.alarmReportUrl()
        } else if (
          report.typeEnum === 'READING_REPORT' ||
          report.typeEnum === 'TEMPLATE_REPORT'
        ) {
          url = this.readingReportUrl(report)
        } else {
          url = this.moduleReportUrl(report)
        }
        window.open(url)
      }
    },
    alarmReportUrl() {
      const id = this.params.alarmId || null
      const { dateRange } = this.resultObject
      const url = this.constructUrl(id, 'alarm', null, dateRange)
      return url
    },
    readingReportUrl(report) {
      const id = report?.id
      const { dateRange } = this.reportObject
      let dateObject = this.getDateObj(dateRange)
      let filterObject = {}
      let { parentId, spaceId } = this.params
      if (parentId) {
        filterObject.parentId = parentId
      }
      if (spaceId) {
        filterObject.spaceId = spaceId
      }
      if (!isEmpty(filterObject)) {
        filterObject.xCriteriaMode = this.params.xCriteriaMode
      }
      const url = this.constructUrl(id, 'reading', filterObject, dateObject)
      return url
    },
    moduleReportUrl(report) {
      const { dateRange } = this.resultObject
      const id = report?.id
      let dateObject = this.getDateObj(dateRange)
      let filterObject = {}
      let userFilter = report.userFilters ?? this.reportObject.userFilters
      if (!isEmpty(userFilter)) {
        filterObject = this.reportObject.filters?.underlyingDataFilters
        if (isEmpty(filterObject)) {
          filterObject = this.getFilters()
        }
        filterObject = Object.entries(filterObject).reduce(
          (validFilter, filter) => {
            if (Object.keys(filter[1]).includes('value')) {
              if (!isEmpty(filter[1].value[0])) {
                validFilter[filter[0]] = filter[1]
              }
            } else {
              validFilter[filter[0]] = filter[1]
            }
            return validFilter
          },
          {}
        )
      }
      let url = this.constructUrl(id, 'module', filterObject, dateObject)
      return url
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
        }
      })
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
    loadNewReportTree() {
      let self = this
      if (!this?.resultObject?.report?.module?.name) {
        return
      }
      let moduleName = this.resultObject.report.module.name
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
      API.get(url, {}, {}).then(resp => {
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
          for (let key in self.newReportTree) {
            let folder = self.newReportTree[key]
            let folderState = {}
            folderState['name'] = folder.name
            folderState['newName'] = folder.name
            folderState['setting'] = false
            folderState['editFolder'] = false
            self.folderState[folder.id] = folderState
          }
        }
      })
    },
    handleCommand(command, isCloneToAnotherApp) {
      let report = this.resultObject.report
      if (command == 'delete') {
        this.$emit('deleteReport', report.id)
      } else if (command == 'duplicate') {
        this.$emit('duplicateReport', report.id)
      } else if (command == 'move') {
        this.showMovePopOver = true
      } else if (command == 'Mail') {
        this.getRoute(report.id)
        this.visibility.showEmailDialog = true
      } else if (command == 'CloneReport') {
        this.openClonePopup(report.id, report.name, isCloneToAnotherApp)
      } else if (command == 'Schedule') {
        this.visibility.showScheduleDialog = true
      } else if (command == 'printPreview') {
        this.printPage()
      }
    },
    editReport(id) {
      this.$emit('editReport', id)
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
    handleMoveFolder(newFolder) {
      this.closePopover()
      let currentFolder = this.newReportTree.filter(
        folder => folder.id === this.reportObject.report.reportFolderId
      )
      this.moveReport(currentFolder, newFolder)
      // this.isFolderClicked = true
    },
    closePopover() {
      let popper = document.querySelectorAll('.el-popover')
      popper.forEach(pop => {
        pop.remove()
      })
    },
    moveReport(currentFolder, newFolder) {
      let curIndex
      let newIndex
      let reportIndex
      let report = this.reportObject.report
      // this.$set(this.folderState[currentFolder.id], 'setting', false)
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
    initSaveExpression() {
      if (this.resultObject) {
        if (
          this.resultObject.regressionConfig &&
          this.resultObject.regressionConfig.length === 1
        ) {
          this.loadEnpi()
        } else {
          this.$set(this.visibility, 'saveRegressionExpr', true)
        }
      }
    },
    loadEnpi() {
      if (
        this.resultObject.regressionType ===
        AnalyticsModelHelper.regressionTypes().SINGLE
      ) {
        let choosenAlias = [this.reportObject.options.dataPoints[0].key]
        this.loadedEnpi = AnalyticsModelHelper.prepareNewEnpiObject(
          this.reportObject,
          choosenAlias
        )
        this.$set(this.visibility, 'saveAsEnpi', true)
      } else {
        let choosenAlias = [this.resultObject.regressionConfig[0].groupAlias]
        this.loadedEnpi = AnalyticsModelHelper.prepareNewEnpiObject(
          this.resultObject,
          choosenAlias,
          this.reportObject.options.common,
          this.reportObject.dateRange.period
        )
        this.$set(this.visibility, 'saveAsEnpi', true)
      }
    },
    openEnpiDialog(enpiConfig) {
      this.loadedEnpi = enpiConfig
      this.$set(this.visibility, 'saveAsEnpi', true)
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
          this.reportObject.filters.underlyingDataFilters &&
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
    triggerExport(type) {
      this.exportType = type
      if (type === '4') {
        this.getRoute(this.resultObject.report.id)
        this.showImageOptionsDialog = true
      } else if (
        this.reportObject &&
        this.reportObject.report &&
        this.reportObject.report.type === 2
      ) {
        if (type === '3') {
          this.getRoute(this.resultObject.report.id)
          this.showColumnSelectionDialog = true
        } else {
          this.exportDialog = true
        }
      } else {
        if (this.resultObject?.report?.id) {
          this.getRoute(this.resultObject.report.id)
        }
        this.exportData(type)
      }
    },
    backToReport(status) {
      if (!status) {
        this.showConfirmDialog = false
      } else {
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
          let { reportId } = this
          let { name } = routeObj || {}
          if (name) {
            this.$router.push({
              name,
              params: { reportid: reportId },
            })
          }
        } else if (typeof this.moduleName === 'undefined') {
          if (this.$route.path && this.$route.path.includes('em/pivot')) {
            this.$router.push({ path: '/app/em/pivot/view/' + this.reportId })
          } else {
            this.$router.push({
              path: '/app/em/reports/newview/' + this.reportId,
            })
          }
        } else if (this.$helpers.isEtisalat()) {
          this.$router.push({
            path: '/app/em/modulereports/newview/' + this.reportId,
          })
        } else if (this.iscustomModule && this.iscustomModule === true) {
          this.$router.push({
            path: '/app/ca/reports/newview/' + this.reportId,
          })
        } else {
          let url = ''
          switch (this.moduleName) {
            case 'workorder':
            case 'workorderLabour':
            case 'workorderCost':
            case 'workorderItem':
            case 'workorderTools':
            case 'workorderService':
            case 'workorderTimeLog':
            case 'workorderHazard':
            case 'plannedmaintenance':
              url = '/app/wo/reports/newview/' + this.reportId
              break
            case 'purchaseorder':
            case 'poterms':
            case 'purchaseorderlineitems':
            case 'purchaserequest':
            case 'purchaserequestlineitems':
              url = '/app/purchase/reports/newview/' + this.reportId
              break
            case 'budget':
            case 'budgetamount':
              url = '/app/ac/reports/newview/' + this.reportId
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
              url = '/app/inventory/reports/newview/' + this.reportId
              break
            case 'visitor':
            case 'visitorlog':
            case 'invitevisitor':
            case 'watchlist':
              url = '/app/vi/reports/newview/' + this.reportId
              break
            case 'inspectionTemplate':
            case 'inspectionResponse':
              url = '/app/inspection/reports/newview/' + this.reportId
              break

            case 'tenant':
            case 'tenantunit':
            case 'tenantcontact':
            case 'quote':
            case 'contact':
            case 'tenantspaces':
            case 'quotelineitems':
            case 'quoteterms':
            case 'people':
            case 'newsandinformationsharing':
            case 'neighbourhoodsharing':
            case 'dealsandofferssharing':
            case 'contactdirectorysharing':
            case 'admindocumentsharing':
            case 'audienceSharing':
              url = '/app/tm/reports/newview/' + this.reportId
              break
            case 'contracts':
            case 'purchasecontracts':
            case 'purchasecontractlineitems':
            case 'labourcontracts':
            case 'labourcontractlineitems':
            case 'warrantycontracts':
            case 'warrantycontractlineitems':
            case 'rentalleasecontracts':
            case 'rentalleasecontractlineitems':
              url = '/app/ct/reports/newview/' + this.reportId
              break
            case 'alarm':
            case 'newreadingalarm':
            case 'readingalarmoccurrence':
            case 'bmsalarm':
            case 'mlAnomalyAlarm':
            case 'anomalyalarmoccurrence':
            case 'violationalarm':
            case 'violationalarmoccurrence':
            case 'operationalarm':
            case 'operationalarmoccurrence':
            case 'sensoralarm':
            case 'sensoralarmoccurrence':
            case 'sensorrollupalarm':
            case 'sensorrollupalarmoccurrence':
            case 'basealarm':
            case 'alarmoccurrence':
            case 'readingevent':
            case 'bmsevent':
            case 'mlAnomalyEvent':
            case 'violationevent':
            case 'operationevent':
            case 'sensorevent':
            case 'baseevent':
              url = '/app/fa/reports/newview/' + this.reportId
              break
            case 'serviceRequest':
              url = '/app/sr/reports/newview/' + this.reportId
              break
            default:
              url = '/app/at/reports/newview/' + this.reportId
              break
          }
          this.$router.push({ path: url })
        }
      }
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
      if (type === '3' || type === '4') {
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

      if (
        typeof this.moduleName !== 'undefined' &&
        this.reportType !== 'pivot'
      ) {
        let moduleReportExportParams = {}
        if (this.reportId || this.$route.params.reportid) {
          moduleReportExportParams['reportId'] = this.reportId
            ? this.reportId
            : this.$route.params.reportid
          if (this.resultObject && this.resultObject.dateRange) {
            moduleReportExportParams[
              'startTime'
            ] = this.resultObject.dateRange.value[0]
            moduleReportExportParams[
              'endTime'
            ] = this.resultObject.dateRange.value[1]
          }
        } else {
          moduleReportExportParams = { ...this.computedConfig }
          moduleReportExportParams['moduleName'] = this.moduleName
        }
        moduleReportExportParams['fileFormat'] = type
        if (this.exportParams) {
          moduleReportExportParams.exportParams = this.exportParams
        }
        if (type === '3' || type === '4') {
          moduleReportExportParams['url'] = this?.url ? this.url : ''
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
        this.$http
          .post('/v2/report/exportModuleReport', moduleReportExportParams)
          .then(response => {
            Message.closeAll()
            if (response.data && response.data.responseCode === 0) {
              this.exportDownloadUrl = response.data.result.fileUrl
            }
          })
      } else {
        let url = null
        if (this.reportType !== 'pivot') {
          url = '/v2/report/export'
        } else {
          url = '/v2/report/exportPivotReport'
        }
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
        if (this.exportParams) {
          params.exportParams = this.exportParams
        }
        if (type === '4' || type === '3') {
          params['url'] = this?.url ? this.url : ''
        }
        if (this.reportType == 'pivot') {
          params = {
            reportId: this.$route.params.id,
            fileFormat: type,
          }
        }
        this.$http.post(url, params).then(response => {
          Message.closeAll()
          if (response.data && response.data.responseCode === 0) {
            this.exportDownloadUrl = response.data.result.fileUrl
          }
        })
      }
    },

    printReport() {
      setTimeout(function() {
        window.print()
      }, 1000)
    },
    checkOption(key) {
      return (
        !this.optionsToEnable ||
        this.optionsToEnable.includes(this.options[key])
      )
    },
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    closeImageOptionsDialog() {
      this.showImageOptionsDialog = false
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

    closeSaveDialog(addToDashboard, savedReportObj) {
      this.savedReportObj = null
      if (addToDashboard === true) {
        this.savedReportObj = savedReportObj
        this.sendToDashBoard = true
      }
      this.$emit(
        'update:savedReport',
        savedReportObj ? savedReportObj.report : null
      )
    },
    getReportTemplate() {
      if (
        this.resultObject &&
        this.resultObject.report &&
        this.resultObject.report.currentTemplate
      ) {
        let template = JSON.parse(
          JSON.stringify(this.resultObject.report.currentTemplate)
        )
        template.chooseValues = []
        return template
      }
    },
    save() {},
  },
}
</script>
<style>
.hide-icon {
  display: none;
}
.analytics-page-options {
  margin: 10px;
  background-color: #ffffff;
  box-shadow: 0 2px 4px 0 rgba(230, 230, 230, 0.5);
  border: solid 1px #d9e0e7;
  border-radius: 5px;
  vertical-align: baseline;
}
.analytics-page-options:hover {
  border: solid 1px #6c6a91;
  color: #4d4b6d;
}
.analytics-page-options {
  display: inline-block;
  margin-left: 20px;
}
.analytics-page-options button:hover,
.analytics-page-options button:focus {
  border-left: 1px solid rgb(217, 224, 231);
}
.analytics-page-options .el-button + .el-button {
  margin-left: 0px;
}
.analytics-page-options button:first-child {
  border: none !important;
}
.analytics-page-options .report-icon {
  width: 17px;
  height: 17px;
}

.fc-chart-btn-border {
  border-left: 1px solid #d9e0e7 !important;
  border-top-left-radius: 0;
  border-bottom-left-radius: 0;
}
.analytics-page-options .fc-chart-btn {
  color: #333333;
  font-size: 17px;
  padding: 9px;
  padding-left: 15px;
  padding-right: 12px;
  margin-left: 0px;
}
/* .analytics-page-options .fc-chart-btn:hover{
   border-left: none !important;
} */
.analytics-page-options .el-dropdown {
  border-right: 1px solid #d9e0e7;
}
.analytics-page-options .fc-chart-btn:not(:last-child) {
  border-right: 1px solid #d9e0e7;
}

.analytics-page-options-building-analysis button:hover,
.analytics-page-options-building-analysis .analytics-page-options button:focus,
.analytics-page-options-building-analysis .analytics-page-options button {
  border-left: none;
}
.analytics-page-options-building-analysis .analytics-page-options button:hover {
  border-left: none;
}
.newanalytics-save-as-report {
  background-color: #39b2c2 !important;
  color: #fff !important;
  height: 42px;
  border: none;
}
.newanalytics-save-as-report:hover {
  border: none;
}
.newanalytics-save-as-report span {
  color: #fff !important;
  font-weight: 500 !important;
  font-size: 13px !important;
  text-transform: uppercase;
}
.pdf-dialog-hide .el-dialog__body {
  height: 300px;
  max-height: 300px;
}
.pdf-dialog-hide .el-dialog__header {
  display: none;
}
svg.icon.iconstyle {
  margin-right: 8px;
}
</style>

<style lang="scss" scoped>
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
.report-clone-dialog {
  .el-dialog .el-dialog__body {
    padding: 0px 0px !important;
  }
}
.reportoptions {
  .el-dropdown-menu__item {
    display: flex !important;
    align-items: center !important;
  }
}
</style>
