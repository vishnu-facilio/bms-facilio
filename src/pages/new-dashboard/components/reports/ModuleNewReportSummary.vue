<template>
  <div class="reports-summary new-reports-summary workorder-report-page">
    <iframe
      v-if="exportDownloadUrl"
      :src="exportDownloadUrl"
      style="display: none;"
    ></iframe>
    <div class="reports-header" v-if="resultObj">
      <div class="report-title pull-left" style="width: 50%;">
        <div class="title row">
          <div
            style="overflow:hidden;"
            class="report-pdf-header"
            v-if="showHeader"
          >
            <div v-show="$org.logoUrl" class="fc-report-cus-logo">
              <img :src="$org.logoUrl" style="width: 100px;" />
            </div>
            <div
              class="f18 fc-widget-label ellipsis max-width350px"
              @click="renameDialogVisibility = !renameDialogVisibility"
            >
              {{ resultObj.report.name }}
            </div>
            <div class="fc-logo-report">
              <img v-if="showLogo" :src="getFacilioLogoURL" />
            </div>
            <div class="fc-text-pink13 report-created-time">
              <span class="fc-black-13">{{
                getDateRangeString.dateFieldLabel !== ''
                  ? getDateRangeString.dateFieldLabel.concat(':')
                  : ''
              }}</span>
              {{ getDateRangeString.label }}
            </div>
          </div>
          <div
            class="report-header-title title-actions"
            v-if="moduleRolePermission()"
          >
            <i
              class="el-icon-edit"
              @click="editReport(resultObj.report.id)"
              data-position="top"
              data-arrow="true"
              :title="$t('home.dashboard.edit_report')"
              v-tippy
            ></i>
            <i
              class="el-icon-delete"
              @click="deleteReport(resultObj.report.id)"
              data-position="top"
              data-arrow="true"
              :title="$t('common.wo_report.report_delete')"
              v-tippy
            ></i>
            <i
              :class="['fa fa-clone']"
              @click="duplicateReport(resultObj.report.id)"
              data-position="top"
              data-arrow="true"
              :title="$t('home.dashboard.duplicate_report')"
              v-tippy
            ></i>
            <!-- <img
              class="pointer"
              src="~assets/dashboard.svg"
              @click="sendToDashBoard = true"
              data-position="top"
              data-arrow="true"
              :title="$t('home.dashboard.dashboard_add')"
              v-tippy
            /> -->
          </div>
        </div>
        <div class="description">
          {{
            resultObj.report.description ? resultObj.report.description : '---'
          }}
        </div>
      </div>
      <!-- print  -->
      <div class="fc-report-author-sec" v-if="showPrintDetails">
        <div class="fc-report-author-txt">
          {{ $t('common.wo_report.generated_on') }}
          <span class="fw6"> {{ '' | now | formatDate() }} </span>
        </div>
        <div class="fc-report-author-txt">
          {{ $t('common.wo_report.generated_by') }}
          <span class="fw6"> {{ $account.user.name }} </span>
        </div>
        <div class="fc-report-author-txt flex-middle">
          <div class="f9 fw6">
            <span class="fc-report-author-txt">{{
              getDateRangeString.dateFieldLabel !== ''
                ? getDateRangeString.dateFieldLabel.concat(':')
                : ''
            }}</span>
            {{ getDateRangeString.label }}
          </div>
        </div>
      </div>
      <div
        class="pull-right"
        style="padding-right: 5px;display: inline-flex;padding-top: 10px;"
      >
        <f-report-options
          :moduleName="moduleName"
          :pdf="true"
          optionClass="analytics-page-options"
          :optionsToEnable="[1, 2, 4]"
          :reportObject="reportObj"
          :resultObject="resultObj"
          :params="reportObj ? reportObj.params : null"
          :optionSettings="
            reportObj && reportObj.options ? reportObj.options.settings : null
          "
          class="pull-right analytics-page-options-building-analysis newreport-page-options"
        ></f-report-options>
      </div>
      <div style="clear:both"></div>
    </div>

    <div
      class="scrollable height100 chart-table-layout p20"
      style="max-height:calc(100vh - 100px);"
    >
      <module-new-report
        :id="reportId"
        @reportLoaded="reportLoaded"
        v-if="reportId"
        :loadImmediately="true"
        class="fc-report-pdf-chart"
        :class="showOnlyImage ? 'fc-report-image-chart' : ''"
      ></module-new-report>
    </div>

    <!-- move reports to dashboard start -->
    <MoveReportToDashBoard
      :moduleName="moduleName"
      v-if="resultObj"
      v-bind:enableMoveDialog.sync="sendToDashBoard"
      :reportObj="resultObj"
    ></MoveReportToDashBoard>
    <!-- move reports to dashboard end -->
  </div>
</template>

<script>
import moment from 'moment-timezone'
import ModuleNewReport from './ModuleNewReport'
import FReportOptions from 'pages/report/components/FReportOptions'
import ReportHelper from 'pages/report/mixins/ReportHelper'
import NewReportSummaryHelper from 'src/pages/report/mixins/NewReportSummaryHelper'
import NewDataFormatHelper from 'pages/report/mixins/NewDataFormatHelper'
import MoveReportToDashBoard from 'src/pages/energy/analytics/newTools/MoveReportToDashBoard'
import ModularAnalyticmixin from 'src/pages/energy/analytics/mixins/ModularAnalyticmixin'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import {
  isWebTabsEnabled,
  findRouteForReport,
  pageTypes,
} from '@facilio/router'
import Vue from 'vue'

export default {
  mixins: [
    ReportHelper,
    NewReportSummaryHelper,
    NewDataFormatHelper,
    ModularAnalyticmixin,
  ],
  components: {
    FReportOptions,
    MoveReportToDashBoard,
    ModuleNewReport,
  },
  data() {
    return {
      spinnerToggle: false,
      reportObj: null,
      resultObj: null,
      exportDownloadUrl: null,
      schedule: false,
      email: false,
      duplicateReportToggle: false,
      currentDateFilter: null,
      renameDialogVisibility: false,
      sendToDashBoard: false,
      moduleName: null,
      isCustomModule: false,
      underlyingFields: [],
    }
  },
  computed: {
    getFacilioLogoURL() {
      let { brandConfig } = this
      let logo = this.$getProperty(brandConfig, 'logo', null)
      return logo
    },
    showLogo() {
      let { brandConfig } = this
      let showLogoInPDF = this.$getProperty(brandConfig, 'showLogoInPDF', true)
      return showLogoInPDF
    },
    brandConfig() {
      return window.brandConfig
    },
    reportId() {
      let reportId = this.$attrs.reportid || this.$route.params.reportid

      return parseInt(reportId)
    },
    reportParams() {
      let params = { reportId: this.reportId }
      if (this.resultObj) {
        params.mode = this.resultObj.mode
        if (this.reportObj.dateRange) {
          params.startTime = this.reportObj.dateRange.time[0]
          params.endTime = this.reportObj.dateRange.time[1]
          params.dateOperator = this.reportObj.dateRange.operatorId
          params.dateOperatorValue = this.reportObj.dateRange.value
        }
        params.chartType = this.reportObj.options.type
      }
      return params
    },
    getDateRangeString() {
      let temp = {
        dateFieldLabel: '',
        label: '',
      }
      if (this.resultObj) {
        let dateField = this.resultObj.report.dataPoints[0].dateField
        let xField = this.resultObj.report.dataPoints[0].xAxis
        let dateFieldString = ''
        if (xField) {
          if (xField && dateField && xField.fieldId !== dateField.fieldId) {
            temp['dateFieldLabel'] = dateField.label
          }
          if (dateField) {
            let a = moment(this.resultObj.report.dateRange.startTime).tz(
              Vue.prototype.$timezone
            )
            dateFieldString = a.format('DD/MM/YYYY') + ' ' + 'to '
            a = moment(this.resultObj.report.dateRange.endTime).tz(
              Vue.prototype.$timezone
            )
            dateFieldString = dateFieldString + a.format('DD/MM/YYYY') + ' '
            temp['label'] = dateFieldString
            return temp
          }
        }
        return temp
      }
      return temp
    },
  },
  methods: {
    duplicateReport(reportId) {
      this.duplicateReportToggle = true
      this.editReport(reportId)
    },
    openReportInView(reportId) {
      if (reportId) {
        window.location.href = this.getNewReportLink(reportId)
      } else {
        this.duplicateDialog = false
        window.location.href = this.getNewReportLink(this.duplicatedReport.id)
      }
    },
    moduleRolePermission() {
      let currentModule = this.getCurrentModule()
      let moduleName = currentModule.module
      if (currentModule.module === 'energydata') {
        moduleName = 'energy'
      }
      if (['workorder', 'alarm', 'energy'].includes(moduleName)) {
        return this.$hasPermission(moduleName + ':CREATE_EDIT_REPORTS')
      }
      return true
    },
    reportLoaded(reportObj, resultObj) {
      this.moduleName = resultObj.module.name
      this.isCustomModule = resultObj.module.custom
      this.reportObj = reportObj
      this.resultObj = resultObj
    },
    getNewReportLink(reportId) {
      return this.getCurrentModule().rootPath + '/newview/' + reportId
    },
    editReport(id) {
      let path = this.getModularReportEditURL(this.moduleName)
      if (this.$helpers.isEtisalat()) {
        path = '/app/em/modulereports/new'
      } else if (this.isCustomModule) {
        path = '/app/ca/reports/new'
      }
      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForReport('module_reports', pageTypes.REPORT_FORM, {
            moduleName: this.moduleName,
          }) || {}
        let query = {
          reportId: id,
          module: this.moduleName ? this.moduleName : null,
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
              module: this.moduleName ? this.moduleName : null,
            },
          })
        } else {
          this.$router.push({
            path: path,
            query: {
              reportId: id,
              module: this.moduleName ? this.moduleName : null,
            },
          })
        }
      }
    },

    async deleteReport(id) {
      let promptObj = {
        title: 'Delete Report',
        message: 'Are you sure you want to delete this Report?',
        rbDanger: true,
        rbLabel: 'Delete',
      }
      let deleteConfirmation = await this.$dialog.confirm(promptObj)
      if (deleteConfirmation) {
        let { data, error } = await API.delete(`/v3/report/delete`, {
          reportId: id,
        })
        if (error) {
          this.$message.error(
            'Failed to delete report, please try again.',
            error
          )
        } else {
          if (data && data.errorString) {
            let confirmObj = {
              title: 'Delete Report',
              message:
                'This report is associated to a dashboard widget. Deleting this report will remove the widget from the dashboard. Are you sure you want to continue?',
              rbLabel: 'Yes, delete',
              lbLabel: 'No, cancel',
            }
            let widgetDeleteConfirm = await this.$dialog.confirm(confirmObj)
            if (widgetDeleteConfirm) {
              let resp = await API.delete('/v3/report/delete', {
                reportId: id,
                deleteWithWidget: true,
              })
              if (resp.error) {
                this.$message.error(
                  'Failed to delete report, please try again.',
                  error
                )
              } else {
                this.$emit('reportDeleted', { type: 'new', reportId: id })
                this.$message.success('Report deleted successfully')
              }
            }
          } else {
            this.$emit('reportDeleted', { type: 'new', reportId: id })
            this.$message.success('Report deleted successfully')
          }
        }
      }
    },
  },
}
</script>

<style>
.auto-created-report {
  padding-top: 10px;
  padding-bottom: 10px;
  letter-spacing: 0.3px;
  color: #333333;
  padding-left: 10px;
  padding-right: 10px;
}
.auto-created-report:hover {
  background: #f1f8fa;
  color: #39b2c2;
}
.duplicate-report-option {
  padding-top: 10px;
  padding-left: 5px;
  padding-right: 5px;
}
.duplicate-report-option:hover {
  background: #f1f8fa;
}
.reports-summary {
  background: white;
  height: 100%;
}

.reports-header .title {
  font-size: 18px;
  letter-spacing: 0.3px;
  color: #333333;
}

.reports-header .description {
  font-size: 13px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.5px;
  text-align: left;
  color: #898989;
}

.reports-header {
  background: white;
  padding: 5px;
  border-bottom: solid 1px #eae9e9;
  box-shadow: 0 3px 4px 0 rgba(218, 218, 218, 0.32);
}

.report-title {
  padding: 5px 18px;
}

.report-title div {
  padding: 5px 0;
}

.report-title .title-actions {
  font-size: 16px;
}
.report-title .title-actions-fixed {
  font-size: 16px;
}
.report-title .title-actions i:not(.default) {
  display: none;
}
.report-title .title-actions img {
  display: none;
}
.report-title:hover .title-actions img {
  display: inline-block;
}

.report-title:hover .title-actions i:not(.default) {
  display: inline-block;
}

.title-actions-fixed i:not(.default) {
  display: inline-block;
}

/* .duplicate-points-popover{
  margin:50px;
} */

.report-title .title-actions i {
  padding: 0 5px;
  cursor: pointer;
}

.report-title .title-actions img {
  padding: 0 5px;
  cursor: pointer;
}

.report-title .title-actions img:hover {
  opacity: 1;
}

.report-title .title-actions i:hover {
  opacity: 1;
}

.report-options {
  margin: 10px;
  background-color: #ffffff;
  box-shadow: 0 2px 4px 0 rgba(230, 230, 230, 0.5);
  border: solid 1px #d9e0e7;
  border-radius: 5px;
}
/*
.report-options .el-button {
    margin: 2px;
} */

.reports-chart {
  text-align: center;
  min-height: 300px;
}

.reports-underlyingdata {
  padding: 24px;
  padding-top: 10px;
}
.fc-chart-btn {
  color: #333333;
  font-size: 17px;
  padding: 7px;
  padding-left: 15px;
  padding-right: 10px;
}
.title-actions .el-icon-delete {
  opacity: 0 !important;
}
.reports-header:hover .title-actions .el-icon-delete {
  opacity: 1 !important;
}
.title-actions .el-icon-edit {
  opacity: 0 !important;
}
.reports-header:hover .title-actions .el-icon-edit {
  opacity: 1 !important;
}
.chart-table-layout {
  padding-bottom: 150px;
}
.reports-summary .fc-report {
  background: #f7f8f9 !important;
}
.reports-summary .fc-report-section {
  background: #fff !important;
  box-shadow: 0 4px 10px 0 rgba(178, 178, 178, 0.18);
  min-height: 500px;
  border: solid 1px #e6ebf0;
}
.reports-summary .reports-chart {
  padding-left: 25px;
  padding-right: 25px;
  padding-top: 15px;
  padding-bottom: 40px;
}
.chart-table-layout .fc-report-section {
  position: relative;
}
.reports-summary {
  background: transparent !important;
}
.new-reports-summary {
  background: white !important;
}
.reports-summary .fc-list-view-table {
  background: white !important;
}
.reports-summary .fc-underlyingdata {
  box-shadow: 0 4px 10px 0 rgba(178, 178, 178, 0.18);
}
.reports-summary .reports-underlyingdata .table-header {
  font-size: 16px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.6px;
  text-align: left;
  color: #000000;
  padding-bottom: 35px;
}
.reports-summary .fc-list-view-table thead th {
  background: #fff;
  padding: 25px 20px;
  font-size: 12px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 1px;
  text-align: left;
  color: #879eb5;
}
.reports-summary .fc-list-view-table td {
  padding-left: 20px;
}
.chart-table-layout .fc-report-section .header .chart-select {
  position: absolute;
  right: 15px;
  margin-top: -14px;
  display: inline-flex;
}

/* .chart-table-layout .fc-report-section .header .chart-select.noreportdata {
  top: 10px;
} */
.chart-table-layout .fc-report-section .header {
  padding-top: 25px;
  padding-bottom: 25px;
  font-size: 18px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.5px;
  text-align: center;
  color: #000000;
}
.reports-summary .chart-table-layout .fc-report-section .header {
  height: 70px;
}
.fc-report-section .header .header-content {
  margin: auto;
  width: 50%;
  overflow: hidden;
  white-space: nowrap;
}
.fc-report-filter .filter-field {
  margin-right: 5px;
}
.report-title .title {
  font-size: 18px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.6px;
  text-align: left;
  padding-bottom: 0px;
  color: #000000;
}
.report-title .title .pin {
  font-size: 24px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 1.5px;
  text-align: left;
  color: #e65b5b;
}
.report-options .el-button + .el-button {
  margin: 0px;
}
.report-options button:first-child {
  border: none !important;
}
.report-options button {
  border-left: 1px solid rgb(217, 224, 231);
}
.report-options button:hover {
  border-left: 1px solid rgb(217, 224, 231);
}
.report-options .fc-cmp-btn {
  font-size: 12px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 1.2px;
  text-align: left;
  color: #615f89;
  padding: 10px;
}
.report-options .i.el-icon-date.el-icon--right {
  font-size: 15px;
}
.report-icon {
  width: 17px;
  height: 17px;
}
.nounderlinedata {
  height: 500px;
  background: #fff;
  text-align: center;
}
.nounderlinedata .content {
  margin: auto;
  padding-top: 200px;
}
.reports-summary .fc-list-view-table td {
  font-size: 14px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: 1.57;
  letter-spacing: 0.3px;
  text-align: left;
  color: #333333;
}
.reports-summary table.fc-list-view-table.fc-chart-table {
  border: 0 !important;
}
/* .reports-header:hover .title-actions  {
  padding-top: 12px;
  padding-left: 10px;
} */
.title-actions {
  padding-left: 10px;
}
.cursor-load {
  cursor: progress;
}
.report-diplicate-dialog .el-dialog__header {
  border-bottom: 1px solid #e4eaf0;
}
.report-diplicate-dialog .el-dialog {
  width: 20%;
}
.report-header-title {
  width: 100px;
  margin-left: 20px;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
}
.report-created-time {
  display: none;
}
.workorder-report-page .f-singlechart .fc-new-chart-type-single {
  top: 10px !important;
}
@media print {
  .fLegendContainer .el-color-picker__color-inner {
    width: 7px !important;
    height: 7px !important;
  }
  .report-created-time {
    display: block;
    text-align: center !important;
    padding-top: 10px !important;
  }
  .report-tab .el-tabs__header .is-top {
    display: none;
  }
  .reports-header .analytics-page-options-building-analysis {
    display: none;
  }
  .modular-user-filter {
    display: none;
  }
  .underlying-report-filters {
    display: none;
  }
  .chart-icon-report {
    display: none;
  }
  .title-actions {
    display: none;
  }
  .fc-report-pdf-chart {
    background: #fff;
    min-height: 500px;
  }
  .fc-report-pdf-chart.fc-report-image-chart {
    min-height: auto !important;
  }
  .fc-report-image-chart .fc-new-chart.bb svg {
    margin-left: 0px !important;
  }
}
</style>
