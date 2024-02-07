<template>
  <div class="full-layout-white height100 scheduled-layout">
    <div class="header pB20">Views Schedule</div>
    <div class="row setting-Rlayout">
      <div class="col-lg-12 col-md-12">
        <table class="setting-list-view-table">
          <thead>
            <tr>
              <th class="setting-table-th setting-th-text">VIEW NAME</th>
              <th class="setting-table-th setting-th-text">FILE FORMAT</th>
              <th class="setting-table-th setting-th-text">FREQUENCY</th>
              <!-- <th class="setting-table-th setting-th-text">SCHEDULED COUNT</th> -->
              <th class="setting-table-th setting-th-text">NEXT EXECUTION</th>
              <th class="setting-table-th setting-th-text"></th>
              <th class="setting-table-th setting-th-text"></th>
            </tr>
          </thead>
          <tbody v-if="loading">
            <tr>
              <td colspan="100%" class="text-center">
                <spinner :show="loading" size="80"></spinner>
              </td>
            </tr>
          </tbody>
          <tbody v-else-if="reports.length === 0">
            <tr>
              <td colspan="100%" class="text-center">
                No View Schedules Available
              </td>
            </tr>
          </tbody>
          <tbody v-else>
            <tr class="tablerow" v-for="info in reports" :key="info.id">
              <!-- <td>{{info.name || info.reportContext.name || 'null'}}</td> -->
              <td>{{ getViewName(info.reportContext.data) }}</td>
              <td>{{ $constants.FILE_FORMAT[info.fileFormat] }}</td>
              <td>
                {{
                  info.job && info.job.schedule
                    ? frequencyType[info.job.schedule.frequencyType]
                    : '---'
                }}
              </td>
              <!-- <td>{{info.job.executionTime}}</td>  -->
              <td>
                {{
                  info.job && info.job.active && info.job.executionTime > -1
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
                  class="text-left actions"
                  style="margin-top:-3px;margin-right: 15px;text-align:center;"
                >
                  <i class="el-icon-edit pointer" @click="editView(info)"></i>
                  <i
                    class="el-icon-delete pointer pL5"
                    @click="deleteView(info)"
                  ></i>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
    <NewScheduleReport
      v-if="showEditDialog"
      :showScheduleDialog.sync="showEditDialog"
      isEdit="true"
      :model="selectedModel"
      @save=";(showEditDialog = false), initData()"
    ></NewScheduleReport>
  </div>
</template>
<script>
import ReportHelper from 'pages/report/mixins/ReportHelper'
import NewScheduleReport from 'pages/report/forms/NewScheduleView'
import UserAvatar from '@/avatar/User'

export default {
  mixins: [ReportHelper],
  props: ['modName'],
  components: { UserAvatar, NewScheduleReport },
  data() {
    return {
      loading: false,
      v2loading: false,
      moduleName: '',
      reports: [],
      woViews: [],
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
      return this.getCurrentModule() ? this.getCurrentModule().module : ''
    },
    users() {
      return this.$store.state.users
    },
  },
  mounted() {
    this.initData()
    this.loadViews()
  },
  methods: {
    initData() {
      this.loading = true
      let self = this
      let data = {
        moduleName: this.$route.meta.module,
      }
      let url
      url = '/view/woScheduleViewList'
      this.$http.post(url, data).then(function(response) {
        self.loading = false
        if (response.data && response.data.scheduledReports) {
          self.reports = response.data.scheduledReports
          self.setReportInfo(response.data.scheduledReports)
        }
      })
    },
    setReportInfo(reports) {
      reports.forEach(report => {
        report.userIds = []
        if (
          report.emailTemplate &&
          report.emailTemplate.workflow &&
          report.emailTemplate.workflow.expressions &&
          report.emailTemplate.workflow.expressions.length > 1
        ) {
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
    editView(reportinfo) {
      let info = this.$helpers.cloneObject(reportinfo)
      let view1 = []
      view1 = this.woViews.filter(
        view => view.id === info.reportContext.data.viewId
      )
      console.log('2222', view1)
      info.emailTemplate.originalTemplate.to = info.userIds
      let startTime =
        info.job.executionTime > 0 ? info.job.executionTime * 1000 : ''
      console.log('jwhjdhwdhwjhdjwhd', info.emailTemplate)
      this.selectedModel = {
        scheduleForm: {
          startTime: startTime || new Date(),
          schedule: info.job.schedule,
        },
        format: info.fileFormat,
        viewName: view1[0].name,
        mailJson: info.emailTemplate.originalTemplate,
        to: info.emailTemplate.originalTemplate.to,
        endTime:
          info.job.endExecutionTime > 0 ? info.job.endExecutionTime * 1000 : '',
        maxCount: info.job.maxExecution > 0 ? info.job.maxExecution : '',
        id: info.id,
      }
      this.showEditDialog = true
    },
    deleteView(info) {
      console.log('info', info)
      let url
      let params = {
        id: null,
      }
      this.$dialog
        .confirm({
          title: 'Delete Schedule',
          message: 'Are you sure you want to delete this schedule?',
          rbDanger: true,
          rbLabel: 'Delete',
        })
        .then(value => {
          if (value) {
            url = '/view/deleteScheduledView'
            params.id = info.id
            this.$http.post(url, params).then(response => {
              if (response.status === 200) {
                this.$message.success('Schedule deleted succesfully')
                this.initData()
              } else {
                this.$message.error('Schedule deletion failed')
              }
            })
          }
        })
    },
    loadViews() {
      let self = this
      this.$http
        .get('/view?moduleName=' + this.$route.meta.module)
        .then(function(response) {
          if (response.data && response.data.views) {
            self.woViews = response.data.views
          }
        })
    },
    getViewName(report) {
      if (report && report.viewId) {
        let view1 = []
        view1 = this.woViews.filter(view => view.id === report.viewId)
        if (view1 && view1[0] && view1[0].displayName) {
          let viewName = view1[0].displayName
          return viewName
        } else {
          return '---'
        }
      } else {
        return '---'
      }
    },
  },
}
</script>

<style>
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
</style>
