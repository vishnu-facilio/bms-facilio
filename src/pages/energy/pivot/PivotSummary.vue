<template>
  <div class="pivot-report-summary  d-flex flex-direction-column">
    <iframe
      v-if="exportDownloadUrl"
      :src="exportDownloadUrl"
      style="display: none;"
    ></iframe>
    <!-- header float layout copied from NewReportSummary ,TO DO clean up after checking print requirments-->
    <header class="reports-header flex-shrink-0" v-if="reportContext">
      <div class="report-title pull-left" style="width: 50%;">
        <div class="title row">
          <div style="overflow:hidden;" class="report-pdf-header">
            <div v-show="$org.logoUrl" class="fc-report-cus-logo">
              <img :src="$org.logoUrl" style="width: 100px;" />
            </div>
            <div
              class="f18 fc-widget-label ellipsis max-width350px"
              @click="renameDialogVisibility = !renameDialogVisibility"
            >
              {{ reportContext.name }}
            </div>
            <div class="fc-logo-report">
              <img src="~assets/facilio-logo-black.svg" />
            </div>
          </div>
          <div
            class="report-header-title title-actions"
            v-if="$hasPermission('energy:CREATE_EDIT_REPORTS')"
          >
            <i
              class="el-icon-edit"
              data-position="top"
              data-arrow="true"
              @click="editReport(reportContext.id)"
              :title="$t('home.dashboard.edit_report')"
              v-tippy
            ></i>
            <i
              @click="deleteReport(reportContext.id)"
              class="el-icon-delete"
              data-position="top"
              data-arrow="true"
              :title="$t('common.wo_report.report_delete')"
              v-tippy
            ></i>
            <i
              :class="['fa fa-clone']"
              data-position="top"
              data-arrow="true"
              @click="duplicateReport(reportContext.id)"
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
          {{ reportContext.description ? reportContext.description : '---' }}
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
          :pdf="false"
          reportType="pivot"
          optionClass="analytics-page-options"
          :optionsToEnable="[1]"
          :reportObject="reportContext"
          :reportContextect="reportContext"
          :params="reportContext ? reportContext.params : null"
          :optionSettings="
            reportContext && reportContext.options
              ? reportContext.options.settings
              : null
          "
          class="pull-right analytics-page-options-building-analysis newreport-page-options"
        >
        </f-report-options>
      </div>
      <div style="clear:both"></div>
    </header>

    <div class="flex-grow pivot-wrapper overflow-hidden">
      <PivotTableWrapper
        :reportId="id"
        @pivotReportLoaded="reportLoaded"
      ></PivotTableWrapper>
    </div>

    <!-- move reports to dashboard start -->
    <MoveReportToDashBoard
      :moduleName="moduleName"
      v-if="reportContext"
      v-bind:enableMoveDialog.sync="sendToDashBoard"
      :reportObj="{ report: reportContext }"
    ></MoveReportToDashBoard>
    <!-- move reports to dashboard end -->
  </div>
</template>

<script>
import PivotTableWrapper from './PivotTableWrapper'
import { API } from '@facilio/api'
import FReportOptions from 'pages/report/components/FReportOptions'
import MoveReportToDashBoard from 'src/pages/energy/analytics/newTools/MoveReportToDashBoard'
// import // isWebTabsEnabled,
// // findRouteForTab,
// // findRouteForReport,
// // pageTypes,
// '@facilio/router'

export default {
  mixins: [],
  props: ['id'],
  components: {
    FReportOptions,
    MoveReportToDashBoard,
    PivotTableWrapper,
  },
  data() {
    return {
      showPrintDetails: false,

      reportContext: null,
      exportDownloadUrl: null,
      schedule: false,
      email: false,
      duplicateReportToggle: false,
      renameDialogVisibility: false,
      sendToDashBoard: false,
    }
  },
  computed: {
    moduleName() {
      return this.reportContext.module.name
    },
  },
  methods: {
    async deleteReport(id) {
      let promptObj = {
        title: 'Delete Report',
        message: 'Are you sure you want to delete this Report?',
        rbDanger: true,
        rbLabel: 'Delete',
      }
      let deleteConfirmation = await this.$dialog.confirm(promptObj)
      if (deleteConfirmation) {
        let { data, error } = API.post('/v2/report/deleteReport', {
          reportId: id,
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
                reportId: id,
                deleteWithWidget: true,
              })
              if (resp.error) {
                this.$message.error(
                  'Error deleting pivot report and widget',
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
    duplicateReport(id) {
      let path = `/app/em/pivot/new?reportId=${id}&duplicate=true`
      this.$router.push({ path: path })
    },
    editReport(id) {
      let path = `/app/em/pivot/new?reportId=${id}`
      this.$router.push({ path: path })
    },
    reportLoaded(reportContext) {
      this.reportContext = reportContext
    },
  },
}
</script>

<style scoped>
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

.pivot-wrapper {
  padding: 20px 0px 20px 20px;
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
/* OLD CSS FROM ReportSummary ends */
</style>
