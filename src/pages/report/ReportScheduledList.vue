<template>
  <div
    class="full-layout-white height100 scheduled-layout"
    style="height: calc(100vh - 160px)"
  >
    <div class="header pB20">{{ $t('pivot.active_scheduled_reports') }}</div>
    <div
      class="row setting-Rlayout pT20"
      style="max-height:calc(100vh - 280px);"
    >
      <div class="col-lg-12 col-md-12">
        <table class="setting-list-view-table">
          <thead>
            <tr>
              <th class="setting-table-th setting-th-text">NAME</th>
              <th class="setting-table-th setting-th-text">FILE FORMAT</th>
              <th class="setting-table-th setting-th-text">FREQUENCY</th>
              <th class="setting-table-th setting-th-text">NEXT EXECUTION</th>
              <th class="setting-table-th setting-th-text"></th>
              <th class="setting-table-th setting-th-text"></th>
            </tr>
          </thead>
          <tbody v-if="loading || v2loading">
            <tr>
              <td colspan="100%" class="text-center">
                <spinner :show="loading || v2loading" size="80"></spinner>
              </td>
            </tr>
          </tbody>
          <tbody v-else>
            <tr class="tablerow" v-for="info in reports" :key="info.id">
              <td>{{ info.name || info.reportContext.name }}</td>
              <td>{{ $constants.FILE_FORMAT[info.fileFormat] }}</td>
              <td>{{ frequencyType[info.job.schedule.frequencyType] }}</td>
              <td>
                {{
                  info.job.active && info.job.executionTime > -1
                    ? $options.filters.formatDate(
                        info.job.executionTime * 1000,
                        'DD-MM-YYYY'
                      )
                    : '---'
                }}
              </td>
              <td>
                <template v-if="info.userIds.length"
                  ><user-avatar
                    size="sm"
                    :user="$store.getters.getUser(id)"
                    v-for="(id, index) in info.userIds"
                    :name="false"
                    :key="index"
                    v-tippy
                    :title="$store.getters.getUser(id).name"
                  ></user-avatar
                ></template>
              </td>
              <td>
                <div
                  v-if="hasSchedulePermission"
                  class="text-left actions"
                  style="margin-top:-3px;margin-right: 15px;text-align:center;"
                >
                  <i class="el-icon-edit pointer" @click="editReport(info)"></i>
                  <i
                    class="el-icon-delete pointer pL5"
                    @click="deleteReport(info.id, info.isV2)"
                  ></i>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
    <schedule-report-v2
      v-if="showEditDialog"
      :visibility.sync="showEditDialog"
      isEdit="true"
      :list="list"
      :model="selectedModel"
      :moduleName="currentModule"
      :webtabId="webTabId"
      :isPivot="isPivot"
      @myEvent="initV2Reports"
      @save="onReportUpdate"
    ></schedule-report-v2>
  </div>
</template>
<script>
import ReportHelper from 'pages/report/mixins/ReportHelper'
import ScheduleReportV2 from './forms/ScheduleReportNew'
import UserAvatar from '@/avatar/User'
import { isWebTabsEnabled, findRouteForTab, pageTypes } from '@facilio/router'
import ReportTabPermissions from 'pages/report/mixins/ReportTabPermissions'
export default {
  props: ['Name', 'list', 'webTabId', 'isPivot'],
  mixins: [ReportHelper, ReportTabPermissions],
  components: { ScheduleReportV2, UserAvatar },
  data() {
    return {
      isCreateScheduleReport: false,
      loading: false,
      v2loading: false,
      reports: [],
      frequencyType: {
        0: 'Once',
        1: 'Daily',
        2: 'Weekly',
        3: 'Monthly',
        4: 'Monthly',
        5: 'Yearly',
      },
      selectedModel: null,
      showEditDialog: false,
    }
  },
  computed: {
    currentModule() {
      if (isWebTabsEnabled()) {
        return this.$attrs.moduleName ? this.$attrs.moduleName : ''
      }
      return this.getCurrentModule() ? this.getCurrentModule().module : ''
    },
    users() {
      return this.$store.state.users
    },
  },
  mounted() {
    if (this.Name !== 'pivot') {
      this.initData()
    }
    this.initV2Reports()
  },
  methods: {
    initData() {
      this.loading = true
      this.reports = this.reports.filter(report => report.isV2)
      this.$http
        .post('/v2/report/scheduled/scheduledList', {
          moduleName: this.currentModule,
        })
        .then(response => {
          this.loading = false
          if (response.data && response.data.scheduledReports) {
            this.reports.push(...response.data.scheduledReports)
            this.setReportInfo(response.data.scheduledReports)
          }
        })
    },
    initV2Reports() {
      this.v2loading = true
      this.reports = this.reports.filter(report => !report.isV2)
      this.$http
        .get(
          '/v2/report/scheduled/' +
            (this.Name === 'pivot' ? 'pivot' : this.currentModule)
        )
        .then(response => {
          this.v2loading = false
          if (response.data && response.data.responseCode === 0) {
            this.reports.push(
              ...response.data.result.scheduledReports.map(report => {
                report.isV2 = true
                return report
              })
            )
            this.setReportInfo(response.data.result.scheduledReports)
          }
        })
    },
    setReportInfo(reports) {
      reports.forEach(report => {
        report.userIds = []
        if (report.emailTemplate.workflow.expressions.length > 1) {
          for (
            let i = 1;
            i < report.emailTemplate.workflow.expressions.length;
            i++
          ) {
            let exp = report.emailTemplate.workflow.expressions[i]
            let userId = exp.criteria.conditions[1].value
            report.userIds.push(parseInt(userId))
          }
        } else {
          let userMails = report.emailTemplate.originalTemplate.to.split()
          userMails.forEach(mail => {
            let user = this.users.find(user => user.email === mail)
            if (user) {
              report.userIds.push(parseInt(user.id))
            }
          })
        }
      })
    },
    editReport(reportinfo) {
      let info = this.$helpers.cloneObject(reportinfo)
      info.emailTemplate.originalTemplate.to = info.userIds
      let startTime =
        info.job.executionTime > 0 ? info.job.executionTime * 1000 : ''
      this.selectedModel = {
        schedule: info.job.schedule,
        startTime: startTime || new Date(),
        format: info.fileFormat,
        mailJson: info.emailTemplate.originalTemplate,
        endTime:
          info.job.endExecutionTime > 0 ? info.job.endExecutionTime * 1000 : '',
        maxCount: info.job.maxExecution > 0 ? info.job.maxExecution : '',
        reportId: info.reportId,
        id: info.id,
        isV2: reportinfo.isV2,
        scheduled_report_name: info.scheduled_report_name,
        selected_reportIds: info.selected_reportIds,
        reportName: info.name,
      }
      if(info.emailTemplate.fromID && info.emailTemplate.fromID !=''){
        this.selectedModel.mailJson['fromID'] = info.emailTemplate.fromID
      }
      this.showEditDialog = true
    },
    onReportUpdate(info) {
      this.showEditDialog = false
      this.initV2Reports()
    },
    deleteReport(id, isV2) {
      this.$dialog
        .confirm({
          title: 'Delete Schedule',
          message: 'Are you sure you want to delete this schedule?',
          rbDanger: true,
          rbLabel: 'Delete',
        })
        .then(value => {
          if (value) {
            let url
            let params
            if (isV2) {
              url = '/v2/report/scheduled/delete'
              params = { ids: [id] }
            } else {
              url = '/dashboard/deleteScheduledReport'
              params = { id: [id] }
            }
            this.$http.post(url, params).then(response => {
              if (
                response.status === 200 &&
                typeof response.data === 'object'
              ) {
                this.$message.success('Schedule deleted succesfully')
                this.reports.splice(
                  this.reports.findIndex(report => report.id),
                  1
                )
              } else {
                this.$message.error('Schedule deletion failed')
              }
            })
          }
        })
    },
    openCreatePopupForScheduleReport() {
      this.isCreateScheduleReport = true
    },
  },
}
</script>

<style scoped>
.scheduled-layout {
  padding: 40px;
}
.scheduled-layout .header {
  font-size: 20px;
  letter-spacing: 0.7px;
  color: rgba(0, 0, 0, 0.7);
}

.scheduled-layout .setting-Rlayout {
  padding: 0px !important;
}
th {
  top: 0;
  position: sticky;
  z-index: 999;
}
.setting-list-view-table tr {
  cursor: default !important;
}
.setting-list-view-table tbody tr:not(.nohover):hover {
  border: none !important;
}
.setting-list-view-table tbody tr:not(.nohover):hover td {
  border: none !important;
}
</style>
