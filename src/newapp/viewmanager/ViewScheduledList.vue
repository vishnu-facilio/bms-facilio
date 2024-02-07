<template>
  <div class="position-relative">
    <div
      v-if="loading"
      class="flex-middle scheduled-view-empty-white asset-empty-data-con m10"
    >
      <spinner :show="true" size="80"></spinner>
    </div>
    <div v-else>
      <div
        v-if="$validation.isEmpty(scheduledReports)"
        class="scheduled-view-empty-white flex-middle justify-content-center flex-direction-column"
      >
        <inline-svg
          src="svgs/emptystate/scheduled-views"
          iconClass="icon text-center icon-xxxxlg"
        ></inline-svg>
        <div class="fc-black-dark f18 bold">
          {{ $t('viewsmanager.schedules.no_view_schedules_available') }}
        </div>
      </div>
      <div v-else class="height100 user-layout">
        <div class="container-scroll">
          <div class="row">
            <div class="col-lg-12 col-md-12">
              <table class="setting-list-view-table">
                <thead>
                  <tr>
                    <th class="setting-table-th setting-th-text width250px">
                      VIEW NAME
                    </th>
                    <th class="setting-table-th setting-th-text width250px">
                      FILE FORMAT
                    </th>
                    <th class="setting-table-th setting-th-text width250px">
                      FREQUENCY
                    </th>
                    <th class="setting-table-th setting-th-text width250px">
                      NEXT EXECUTION
                    </th>
                    <th
                      class="setting-table-th setting-th-text width200px"
                    ></th>
                    <th
                      class="setting-table-th setting-th-text width200px"
                    ></th>
                  </tr>
                </thead>
                <tbody>
                  <tr
                    class="tablerow"
                    v-for="scheduledReport in scheduledReports"
                    :key="scheduledReport.id"
                  >
                    <td>{{ getViewName(scheduledReport) }}</td>

                    <td>
                      {{ fileFormat[scheduledReport.fileFormat] }}
                    </td>

                    <td>
                      {{
                        !$validation.isEmpty(scheduledReport.job) &&
                        !$validation.isEmpty(scheduledReport.job.schedule)
                          ? frequencyType[
                              scheduledReport.job.schedule.frequencyType
                            ]
                          : '---'
                      }}
                    </td>

                    <td>
                      {{ checkForActiveJob(scheduledReport) }}
                    </td>

                    <td>
                      <template
                        v-if="!$validation.isEmpty(scheduledReport.userIds)"
                      >
                        <span
                          v-for="(id, index) in scheduledReport.userIds"
                          :key="index"
                        >
                          <user-avatar
                            size="sm"
                            :user="$store.getters.getUser(id)"
                            :name="false"
                            v-tippy
                            :title="$store.getters.getUser(id).name"
                          ></user-avatar>
                        </span>
                      </template>
                    </td>

                    <td>
                      <div
                        class="text-left actions"
                        style="margin-top:-3px;margin-right: 15px;text-align:center;"
                      >
                        <i
                          class="el-icon-edit pointer"
                          @click="editScheduledReport(scheduledReport)"
                        ></i>
                        <i
                          class="el-icon-delete pointer pL5"
                          @click="deleteScheduledReport(scheduledReport)"
                        ></i>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>

    <portal to="view-manager-actions">
      <div class="btn-container">
        <el-button
          type="primary"
          class="manager-secondary-btn ml-add-btn-label"
          @click="createView()"
        >
          <span class="btn-label">
            {{ $t('viewsmanager.schedules.schedule_views') }}
          </span>
        </el-button>
      </div>
    </portal>

    <NewScheduleReport
      v-if="showScheduleViewCreation"
      :moduleName="moduleName"
      :isEdit="!$validation.isEmpty(selectedReport)"
      :showViews="$validation.isEmpty(selectedReport)"
      :model="selectedReport"
      :viewName="(selectedReport || {}).viewName"
      :viewDisplayName="(selectedReport || {}).viewDisplayName"
      @save="initData"
      @close="showScheduleViewCreation = false"
    ></NewScheduleReport>
  </div>
</template>

<script>
import Spinner from '@/Spinner'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import NewScheduleReport from 'pages/report/forms/NewScheduleView'
import UserAvatar from '@/avatar/User'
import { deepCloneObject } from 'util/utility-methods'
import Constants from 'util/constant'

const frequency = {
  0: 'Once',
  ...Constants.FACILIO_FREQUENCY,
}

export default {
  components: {
    Spinner,
    NewScheduleReport,
    UserAvatar,
  },
  props: ['moduleName'],
  data() {
    return {
      loading: false,
      scheduledReports: [],
      selectedReport: null,
      showScheduleViewCreation: false,
      views: [],
    }
  },
  computed: {
    users() {
      return this.$store.state.users
    },
    frequencyType() {
      let types = deepCloneObject(frequency)
      return types
    },
    fileFormat() {
      let formats = deepCloneObject(Constants.FILE_FORMAT)
      return formats
    },
  },
  mounted() {
    this.initData()
    this.loadViews()
  },
  methods: {
    createView() {
      this.selectedReport = null
      this.showScheduleViewCreation = true
    },
    checkForActiveJob(report) {
      let { job } = report
      if (!isEmpty(job)) {
        let { active, executionTime } = job
        if (active && executionTime > -1) {
          return this.$options.filters.formatDate(
            executionTime * 1000,
            'DD-MM-YYYY'
          )
        }
      }
      return '---'
    },
    initData() {
      this.loading = true
      this.scheduledReports = []
      let { moduleName } = this
      let data = {
        moduleName,
      }
      let url = '/view/woScheduleViewList'
      API.post(url, data).then(({ data, error }) => {
        if (error) {
          this.$message.error(error.message || 'Error occured')
        } else {
          let { scheduledReports } = data || []
          this.scheduledReports = this.deserializeReportsInfo(scheduledReports)
        }
        this.loading = false
      })
    },
    deserializeReportsInfo(scheduledReports) {
      let reports = scheduledReports
      if (!isEmpty(reports)) {
        reports.forEach(report => {
          let { emailTemplate } = report || {}
          let { workflow } = emailTemplate || {}
          let { expressions } = workflow || {}
          let userIds = []
          if (!isEmpty(expressions)) {
            for (let i = 0; i < expressions.length; i++) {
              let expression = expressions[i]
              let { criteria } = expression || {}
              if (!isEmpty(criteria)) {
                let userId = expression.criteria.conditions[1].value
                userIds.push(parseInt(userId))
              }
            }
          } else {
            let userMails = emailTemplate.originalTemplate.to.split()
            userMails.forEach(mail => {
              let user = this.users.find(user => user.email === mail)
              if (!isEmpty(user)) {
                userIds.push(parseInt(user.id))
              }
            })
          }
          this.$set(report, 'userIds', userIds)
        })
      }
      return reports
    },
    loadViews() {
      let { moduleName } = this
      let url = `/view?moduleName=${moduleName}`
      API.get(url).then(({ data, error }) => {
        if (error) {
          this.$message.error(error.message || 'Error occured')
        } else {
          if (!isEmpty(data)) {
            let { views } = data || {}
            this.views = views
          }
        }
      })
    },
    getViewName(report) {
      let { reportContext } = report || {}
      let { data } = reportContext || {}
      let { views } = this
      if (!isEmpty(data)) {
        let viewArr = views.filter(view => view.id === data.viewId)
        if (!isEmpty(viewArr)) {
          let viewName = viewArr[0].displayName || viewArr[0].name
          return viewName
        }
      }
      return '---'
    },
    editScheduledReport(scheduledReport) {
      let { views } = this
      this.selectedReport = {}
      let scheduledReportInfo = this.$helpers.cloneObject(scheduledReport)
      let {
        reportContext,
        job,
        id,
        fileFormat,
        emailTemplate,
        userIds,
      } = scheduledReportInfo
      let { data } = reportContext
      let { viewId } = data
      let viewObj = views.filter(view => view.id === viewId)

      emailTemplate.originalTemplate.to = userIds
      let startTime = job.executionTime > 0 ? job.executionTime * 1000 : ''
      this.selectedReport = {
        scheduleForm: {
          startTime: startTime || new Date(),
          schedule: job.schedule,
        },
        format: fileFormat,
        viewName: viewObj[0].name,
        viewDisplayName: viewObj[0].displayName || viewObj[0].name,
        mailJson: emailTemplate.originalTemplate,
        to: emailTemplate.originalTemplate.to,
        endTime: job.endExecutionTime > 0 ? job.endExecutionTime * 1000 : '',
        maxCount: job.maxExecution > 0 ? job.maxExecution : '',
        id: id,
      }
      this.showScheduleViewCreation = true
    },
    deleteScheduledReport(scheduledReport) {
      this.$dialog
        .confirm({
          title: 'Delete Schedule',
          message: 'Are you sure you want to delete this schedule?',
          rbDanger: true,
          rbLabel: 'Delete',
        })
        .then(value => {
          if (value) {
            let url = '/view/deleteScheduledView'
            let dataObj = {
              id: scheduledReport.id,
            }
            API.post(url, dataObj).then(({ data, error }) => {
              if (error) {
                this.$message.error(
                  error.message ||
                    this.$t(`viewsmanager.schedules.schedule_deletion_failed`)
                )
              } else {
                this.$message.success(
                  this.$t(
                    `viewsmanager.schedules.schedule_deletion_succesfully`
                  )
                )
                this.initData()
              }
            })
          }
        })
    },
  },
}
</script>

<style scoped>
.scheduled-view-empty-white {
  width: 100%;
  height: 73vh;
  background: #ffffff;
}
.ml-add-btn-label {
  background-color: #39b2c2 !important;
}
</style>
