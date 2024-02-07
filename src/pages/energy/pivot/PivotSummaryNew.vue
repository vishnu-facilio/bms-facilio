<template>
  <div
    class="pivot-report-summary  d-flex flex-direction-column white-background "
  >
    <iframe
      v-if="exportDownloadUrl"
      :src="exportDownloadUrl"
      style="display: none;"
    ></iframe>
    <div class="flex-grow pivot-wrapper">
      <PivotTableWrapper
        :reportId="id"
        @pivotReportLoaded="reportLoaded"
      ></PivotTableWrapper>
    </div>
  </div>
</template>

<script>
import PivotTableWrapper from './PivotTableWrapper'
import { API } from '@facilio/api'

export default {
  mixins: [],
  props: ['id'],
  components: {
    PivotTableWrapper,
  },
  data() {
    return {
      dateFilterObject: null,
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
    reportLoaded(reportContext, dateFilterObject = null) {
      this.dateFilterObject = dateFilterObject

      this.reportContext = reportContext
      this.$emit('summaryLoaded', dateFilterObject)
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
  background: #f7f8f9;
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
