<template>
  <div class="reports-summary">
    <iframe
      v-if="exportDownloadUrl"
      :src="exportDownloadUrl"
      style="display: none;"
    ></iframe>
    <div class="reports-header" v-if="report">
      <div class="report-title pull-left" style="width: 50%;">
        <div class="title row">
          <div class="" style="overflow:hidden;">
            <el-input
              :autofocus="true"
              class="create-dashboard-input-title f18 fc-widget-label ellipsis"
              v-model="title"
              placeholder=""
              @blur="blurevent()"
              v-if="titleEdit"
            ></el-input>
            <div
              class="f18 fc-widget-label ellipsis"
              @dblclick="editheader('edit')"
              v-else
            >
              {{ report.options.name }}
            </div>
          </div>
          <div
            v-if="
              $hasPermission(
                this.getCurrentModule().module + ':CREATE_EDIT_REPORTS'
              )
            "
            class="title-actions"
          >
            <i
              class="el-icon-edit default"
              data-position="bottom"
              data-arrow="true"
              title="Edit name"
              v-tippy
              @click="editheader()"
            ></i>
            <i
              class="el-icon-edit default hide"
              data-position="bottom"
              data-arrow="true"
              title="Edit"
              v-tippy
              @click="editReport()"
            ></i>
            <i
              class="el-icon-delete default"
              data-position="bottom"
              data-arrow="true"
              title="Delete"
              v-tippy
              @click="deleteReport()"
            ></i>
          </div>
        </div>
        <div class="description">
          {{ report.options.description ? report.options.description : '---' }}
        </div>
      </div>
      <div
        v-if="
          $hasPermission(this.getCurrentModule().module + ':EXPORT_REPORTS')
        "
        class="pull-right"
        style="padding-right: 5px;display: inline-flex;padding-top: 10px;"
      >
        <div class="report-options">
          <el-dropdown @command="exportData">
            <el-button size="small" type="text" class="fc-chart-btn">
              <img class="report-icon" src="~statics/report/export.svg" />
            </el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="1" name="CSV">As CSV</el-dropdown-item>
              <el-dropdown-item command="2" name="Excel"
                >As Excel</el-dropdown-item
              >
              <el-dropdown-item command="3" name="PDF">As PDF</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
          <email-report
            :visibility.sync="email"
            :report="report"
          ></email-report>
          <el-button
            size="small"
            type="text"
            :title="$t('home.dashboard.schedule_report')"
            data-position="bottom"
            data-arrow="true"
            v-tippy
            class="fc-chart-btn"
            @click="scheduleexecution()"
          >
            <img class="report-icon" src="~statics/report/calendar.svg"
          /></el-button>
          <el-button
            size="small"
            type="text"
            :title="$t('common.wo_report.email_this_report')"
            data-position="bottom"
            data-arrow="true"
            v-tippy
            class="fc-chart-btn"
            @click="emailexecution()"
          >
            <img class="report-icon" src="~statics/report/email.svg"
          /></el-button>
          <el-button
            size="small"
            type="text"
            @click="printReport"
            title="Print"
            data-position="bottom"
            data-arrow="true"
            v-tippy
            class="fc-chart-btn"
          >
            <img class="report-icon" src="~statics/report/printer.svg" />
          </el-button>
        </div>
      </div>
      <div style="clear:both"></div>
    </div>
    <div
      class="height100 scrollable chart-table-layout"
      style="padding-bottom:0px;"
    >
      <date
        class="filter-field date-filter-comp text-center pT5"
        @data="setdate"
        v-if="report && report.options.type === 'tabular'"
        :mills="getMills(report.datefilter).time"
        :filter="getMills(report.datefilter).filter"
        :dateObj="report.datefilter"
      ></date>
      <div
        class="reports-chart"
        v-show="
          !report ||
            (report &&
              report.options.type !== 'matrix' &&
              report.options.type !== 'tabular')
        "
      >
        <f-report
          ref="freport"
          :config="{ id: reportId }"
          :currentDateFilter.sync="currentDateFilter"
          :currentEMFilter.sync="energyMeterFilter"
          @onload="reportLoaded"
          :print="isPrint"
        ></f-report>
      </div>
      <div
        class="reports-underlyingdata"
        v-if="report && report.options.type === 'tabular'"
      >
        <spinner
          v-if="loading"
          class="mT15"
          style="margin: 0 auto;"
          :show="loading"
          size="80"
        ></spinner>
        <f-tabular-report
          :reportObject="reportObject"
          v-else
        ></f-tabular-report>
      </div>
      <div
        class="reports-underlyingdata"
        style="padding-bottom:150px;"
        v-show="
          report &&
            report.options.type !== 'matrix' &&
            report.options.type !== 'tabular'
        "
      >
        <f-tabular-data ref="tabulardata"></f-tabular-data>
      </div>
    </div>
  </div>
</template>
<script>
import tooltip from '@/graph/mixins/tooltip'
import FReport from './components/FReport'
import FTabularReport from './components/FTabularReport'
import FTabularData from './components/FTabularData'
import EmailReport from './forms/EmailReport'
import ScheduleReport from './forms/ScheduleReport'
import ReportHelper from 'pages/report/mixins/ReportHelper'
import date from '@/DatePicker'
import {
  isWebTabsEnabled,
  findRouteForReport,
  pageTypes,
} from '@facilio/router'

export default {
  mixins: [ReportHelper],
  components: {
    FReport,
    FTabularReport,
    FTabularData,
    EmailReport,
    ScheduleReport,
    date,
  },
  data() {
    return {
      exportDownloadUrl: null,
      titleEdit: false,
      title: null,
      report: null,
      reportObject: null,
      email: false,
      schedule: false,
      currentDateFilter: null,
      isPrint: false,
      energyMeterFilter: null,
      dateObj: null,
      loading: false,
    }
  },
  destroyed() {
    tooltip.hideTooltip()
  },
  computed: {
    reportId() {
      let reportId = this.$attrs.reportid || this.$route.params.reportid

      return parseInt(reportId)
    },
  },
  watch: {
    $route: {
      handler(newData, oldData) {
        this.titleEdit = false
      },
    },
  },
  methods: {
    reportLoaded(report, reportObject) {
      this.report = report
      this.reportObject = reportObject
      if (this.report && this.report.options) {
        this.tilte = this.report.options.name ? this.report.options.name : null
      }
      if (this.$refs['tabulardata']) {
        this.$refs['tabulardata'].initData(this.report)
      }
    },
    editheader() {
      this.titleEdit = true
      if (this.report && this.report.options) {
        this.title = this.report.options.name ? this.report.options.name : null
      }
    },
    blurevent() {
      if (this.title.replace(/\s/g, '').length > 0 && this.titleEdit) {
        this.titleEdit = false
        this.report.options.name = this.title
        this.updateReport()
      } else {
        this.titleEdit = true
      }
    },
    updateReport() {
      let params = {
        reportContext: {
          name: this.report.options.name,
          id: this.report.options.id,
        },
      }
      this.$http.post('/dashboard/updateReport', params).then(response => {})
    },
    editReport() {
      let { reportId } = this

      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForReport('module_reports', pageTypes.REPORT_FORM) || {}

        if (name) {
          this.$router.push({
            name,
            query: { reportId },
          })
        }
      } else {
        this.$router.replace({
          path: `${this.getCurrentModule().rootPath}/edit/${reportId}`,
        })
      }
    },
    deleteReport() {
      let self = this
      let promptObj = {
        title: 'Delete Report',
        message: 'Are you sure you want to delete this Report?',
        rbDanger: true,
        rbLabel: 'Delete',
      }
      self.$dialog.confirm(promptObj).then(function(value) {
        if (value) {
          self.$http
            .post('dashboard/deleteReport', {
              reportId: self.reportId,
            })
            .then(function(response) {
              console.log('response', response)
              self.$emit('reportDeleted', {
                type: 'old',
                reportId: self.reportId,
              })
            })
            .catch(function(error) {
              self.$message({
                message: error.response.data.errorString,
                type: 'error',
              })
            })
        }
      })
    },
    exportData(cmd, name) {
      let self = this
      console.log('REpoting', cmd, name.$vnode.data.attrs.name)
      let reportId = self.report.tabledata
        ? self.report.tabledata[0].options.id
        : self.report.options.id
      let param = { type: cmd, reportId: reportId }
      if (this.currentDateFilter) {
        param.dateFilter = this.currentDateFilter
      }
      if (this.energyMeterFilter) {
        param.energyMeterFilter = this.energyMeterFilter
        if (this.energyMeterFilter.buildingId) {
          param.reportSpaceFilterContext = {
            buildingId: parseInt(this.energyMeterFilter.buildingId),
          }
        }
      }
      if (this.$attrs.buildingid) {
        param.reportSpaceFilterContext = {
          buildingId: parseInt(this.$attrs.buildingid),
        }
      }

      this.$message('Exporting as ' + name.$vnode.data.attrs.name)
      this.$http.post('/dashboard/export', param).then(response => {
        this.$message.close()
        this.exportDownloadUrl = response.data.fileUrl
      })
    },
    reload: function(d) {
      location.reload()
    },
    emailexecution() {
      this.email = true
    },
    scheduleexecution() {
      this.schedule = true
    },
    printReport() {
      this.isPrint = !this.isPrint
    },
    setdate(data) {
      console.log('**************', data)
      let self = this
      let url = '/dashboard/getData'
      let params = {}
      params.reportId = self.reportId
      if (data) {
        params.dateFilter = data.time
      }
      this.loading = true
      self.$http.post(url, params).then(function(response) {
        console.log('*********', response)
        self.reportObject = response.data
        self.loading = false
      })
    },
  },
}
</script>
<style>
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

.report-title .title-actions i:not(.default) {
  display: none;
}

.report-title:hover .title-actions i:not(.default) {
  display: inline-block;
}

.report-title .title-actions i {
  padding: 0 5px;
  cursor: pointer;
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
  padding-bottom: 150px;
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
.reports-header:hover .title-actions {
  padding-top: 10px;
}
.title-actions {
  padding-left: 10px;
}
</style>
