<template>
  <el-dialog
    :visible.sync="visibility"
    width="50%"
    custom-class="fc-dialog-form fc-dialog-right setup-dialog40 setup-dialog scheduler-form"
    @close="close"
  >
    <el-form
      :model="scheduleForm"
      :label-position="'top'"
      ref="schedule-form"
      :rules="rules"
    >
      <div class="new-header-container">
        <div class="new-header-text">
          <div class="fc-setup-modal-title">
            {{ $t('home.dashboard.schedule_report') }}
          </div>
        </div>
      </div>

      <div class="new-body-modal enpi-body-modal">
        <div class="body-scroll">
          <div class="fc-input-label-txt pT20" style="clear:both;">
            {{ $t('Name') }}
          </div>
          <el-row>
            <el-col :span="18" class="fc-input-full-border2 width100">
              <el-input
                class="fc-input-full-border2 width100"
                type="text"
                v-model="scheduled_report_name"
              ></el-input>
            </el-col>
          </el-row>
          <el-row class="mT20">
            <div class="fc-input-label-txt">
              {{ $t('Select Reports') }}
            </div>
            <el-col :span="24">
              <el-select
                filterable
                multiple
                collapse-tags
                v-model="selected_reportIds"
                class="fc-input-full-border2 width100"
                remote
                :remote-method="query => searchContent({ searchText: query })"
                @change="setSelectedOption"
              >
                <el-option
                  v-for="report in getReportsOption"
                  :key="report.id"
                  :label="report.name"
                  :value="report.id"
                  clearable
                >
                </el-option>
              </el-select>
            </el-col>
          </el-row>
          <FrequencySelector
            v-model="schedule"
            :isNew="isNew"
            ref="reportFrequencySelector"
          >
          </FrequencySelector>

          <div
            class="pT30 subheader colorPink"
            style="padding-bottom: 15px;font-weight: 600;text-transform: uppercase;color: #333333;"
          >
            {{ $t('common.wo_report.email_this_report') }}
          </div>
          <div>
            <el-form-item :label="$t('asset.performance.from')">
              <el-select
                v-model="mailJson.fromID"
                filterable
                default-first-option
                class="fc-input-full-border-select2 width100"
              >
                <el-option
                  v-for="email_address in senderEmailList"
                  :key="email_address.id"
                  :label="email_address.email"
                  :value="email_address.id"
                  class="subject"
                ></el-option>
              </el-select>
            </el-form-item>
          </div>
          <div class="fc-input-label-txt" style="float:right;">
            {{ $t('common.wo_report.format') }}
            <div>
              <el-select
                v-model="format"
                class="fc-input-full-border-select2"
                style="padding-top: 8px;"
              >
                <el-option
                  v-for="(label, value) in $constants.FILE_FORMAT"
                  v-if="!((label == 'Image' || label == 'PDF') && isPivot)"
                  :key="value"
                  :label="label"
                  :value="parseInt(value)"
                  collapse-tags
                ></el-option>
              </el-select>
            </div>
          </div>
          <div class="fc-input-label-txt">{{ $t('common.products.to') }}</div>
          <el-select
            v-model="mailJson.to"
            multiple
            collapse-tags
            filterable
            default-first-option
            style="width: 280px;overflow:scroll;"
            class="fc-input-full-border-select2"
          >
            <el-option
              v-for="user in users"
              :key="user.id"
              :label="user.name"
              :value="parseInt(user.id)"
              >{{ user.name + ' (' + user.email + ')' }}</el-option
            >
          </el-select>
          <div class="fc-input-label-txt pT20" style="clear:both;">
            {{ $t('maintenance.wr_list.subject') }}
          </div>
          <el-row>
            <el-col :span="18">
              <el-input
                type="text"
                v-model="mailJson.subject"
                @change="messageChanged = true"
              ></el-input>
            </el-col>
          </el-row>

          <div class="fc-input-label-txt pT20">
            {{ $t('setup.approvalprocess.description') }}
          </div>
          <el-input
            type="textarea"
            v-model="mailJson.message"
            @change="messageChanged = true"
            :autosize="{ minRows: 6, maxRows: 6 }"
            resize="none"
            class="fc-input-txt fc-desc-input"
          ></el-input>
        </div>
      </div>
      <div class="modal-dialog-footer">
        <el-button @click="close" class="modal-btn-cancel button-padding">{{
          $t('setup.users_management.cancel')
        }}</el-button>
        <el-button
          type="primary"
          @click="save"
          :loading="saving"
          class="modal-btn-save button-padding"
        >
          {{
            !saving
              ? $t('common._common.schedule')
              : $t('common.header.scheduling')
          }}
        </el-button>
      </div>
    </el-form>
  </el-dialog>
</template>
<script>
import FrequencySelector from 'src/pages/setup/scheduler/FrequencySelector.vue'
import { frequencyTypes } from 'src/pages/setup/scheduler/schedulerFrequencyUtil'
import cloneDeep from 'lodash/cloneDeep'
import debounce from 'lodash/debounce'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import { getApp } from '@facilio/router'
import { mapState } from 'vuex'
import { EmailModel } from 'src/newapp/setupActions/models/EmailModel.js'

export default {
  props: [
    'visibility',
    'report',
    'isEdit',
    'model',
    'moduleName',
    'scheduledReportId',
    'reportname',
    'name',
    'list',
    'webtabId',
    'appId',
    'isPivot',
  ],
  components: {
    FrequencySelector,
    EmailModel,
  },
  data() {
    return {
      scheduled_report_name: 'Schedule Report',
      selected_reportIds: [],
      reports_List: [],
      checked: false,
      params: null,
      scheduleForm: {
        startTime: new Date(),
        startHour: ['00:00'],
      },
      schedule: null,
      dateOptions: {
        disabledDate(time) {
          let today = new Date()
          let fiveYear = new Date()
          let startData = time.getTime()

          today.setHours(0, 0, 0, 0)
          fiveYear.setFullYear(today.getFullYear() + 5)

          return startData < today.getTime() || startData > fiveYear.getTime()
        },
      },
      rules: {},
      format: 1,
      frequencyObj: null,
      mailJson: {
        fromID: null,
        to: [],
        subject: '',
        message: '',
      },
      endType: 'never',
      endTime: null,
      maxCount: null,
      messageChanged: false,
      saving: false,
      search_report_list: [],
      senderEmailList: [],
    }
  },
  computed: {
    ...mapState('webtabs', {
      currentTab: state => state.selectedTab,
    }),
    users() {
      return this.$store.state.users
    },
    isNew() {
      return !(this.isEdit && this.model)
    },
    getReportsOption() {
      if (this.search_report_list.length) {
        return this.search_report_list
      }
      return this.reports_List
    },
  },
  watch: {
    visibility(val) {
      if (val) {
        if (this.isEdit) {
          this.init()
        } else {
          this.scheduleForm.startTime = new Date()
        }
      }
    },
  },
  created() {
    this.initReportOptions()
    this.init()
    this.getFromAddressList()
  },
  methods: {
    async getFromAddressList() {
      this.senderEmailList = await EmailModel.getSenderList()
      if (!isEmpty(this.senderEmailList)) {
        this.mailJson.fromID = this.senderEmailList[0].id
      }
    },
    getPaginatedParams() {
      return {
        appId: !isEmpty(this.appId) ? this.appId : (getApp() || {}).id,
        isPivot: !isEmpty(this.isPivot) ? this.isPivot : false,
        webTabId: !isEmpty(this.webtabId)
          ? this.webtabId
          : !isEmpty(this.currentTab)
          ? this.currentTab.id
          : null,
        moduleName: !isEmpty(this.moduleName) ? this.moduleName : 'energydata',
      }
    },
    async initReportOptions() {
      let apiUrl = '/v3/report/list'
      let params = this.getPaginatedParams()
      let { data, error } = await API.get(apiUrl, params)
      if (!error) {
        this.reports_List = data.reports
      }
    },
    setSelectedOption() {
      this.search_report_list = []
    },
    init() {
      if (this.isEdit && this.model) {
        const selectedSchedule = cloneDeep(this.model)
        let { startTime, schedule } = selectedSchedule || {}
        delete schedule.rolledBackDatesAsList
        delete schedule.timeObjects
        this.schedule = this.deserialize(schedule, startTime)
        delete this.schedule.rolledBackDatesAsList
        delete this.schedule.timeObjects
        this.format = this.model.format
        this.mailJson = this.model.mailJson
        this.mailJson.to = this.model.mailJson.to
        if (this.model.endTime) {
          this.endTime = this.model.endTime
        }
        if (this.model.maxCount) {
          this.maxCount = this.model.maxCount
        }
        if (this.maxCount > 0) {
          this.endType = 'after'
        } else {
          this.endType = this.endTime > 0 ? 'on' : 'never'
        }
        if (isEmpty(this.model.selected_reportIds)) {
          this.selected_reportIds.push(this.model.reportId)
        } else {
          this.selected_reportIds = this.model.selected_reportIds
        }
        this.scheduled_report_name = !isEmpty(this.model.scheduled_report_name)
          ? this.model.scheduled_report_name
          : this.model.reportName
      } else {
        if (!isEmpty(this.scheduledReportId)) {
          this.selected_reportIds.push(this.scheduledReportId)
        }
        if (!isEmpty(this?.report.id)) {
          this.selected_reportIds.push(this.report.id)
        }
        this.setInitialMessage(this.frequencyObj)
      }
    },
    searchContent: debounce(async function(props = {}) {
      let { searchText } = props
      let apiUrl = '/v3/report/list'
      let params = this.getPaginatedParams()
      if (!isEmpty(searchText)) {
        params['searchText'] = searchText
        await API.get(apiUrl, params).then(response => {
          if (!response.error) {
            this.search_report_list = response.data.reports
          }
        })
      }
    }, 500),
    deserialize(schedule, startTime) {
      let {
        DO_NOT_REPEAT,
        DAILY,
        WEEKLY,
        MONTHLY,
        QUARTERLY,
        HALF_YEARLY,
        ANNUALLY,
      } = frequencyTypes
      let {
        frequencyType,
        yearlyDayValue,
        yearlyDayOfWeekValues,
        weekFrequency,
      } = schedule || {}
      schedule.basedOn = weekFrequency === -1 ? 'Date' : 'Week'
      schedule.startTime = startTime || null
      if (![DO_NOT_REPEAT, DAILY, WEEKLY].includes(frequencyType)) {
        let Date = { 3: MONTHLY, 5: ANNUALLY, 7: QUARTERLY, 9: HALF_YEARLY }
        let Week = { 4: MONTHLY, 6: ANNUALLY, 8: QUARTERLY, 10: HALF_YEARLY }

        schedule.frequencyType =
          schedule.basedOn === 'Date'
            ? Date[frequencyType]
            : Week[frequencyType]
      }
      if (ANNUALLY === frequencyType) {
        schedule.months = schedule.values
        schedule.values =
          yearlyDayValue !== -1 ? [yearlyDayValue] : yearlyDayOfWeekValues
      }
      return schedule
    },
    serialize(schedule) {
      let scheduleObj = cloneDeep(schedule)
      let { frequencyType, basedOn } = scheduleObj || {}
      let { MONTHLY, QUARTERLY, HALF_YEARLY, ANNUALLY } = frequencyTypes
      let Date = {
        [MONTHLY]: 3,
        [ANNUALLY]: 5,
        [QUARTERLY]: 7,
        [HALF_YEARLY]: 9,
      }
      let Week = {
        [MONTHLY]: 4,
        [ANNUALLY]: 6,
        [QUARTERLY]: 8,
        [HALF_YEARLY]: 10,
      }
      if ([MONTHLY, QUARTERLY, HALF_YEARLY, ANNUALLY].includes(frequencyType))
        scheduleObj.frequencyType =
          basedOn === 'Date' ? Date[frequencyType] : Week[frequencyType]
      if (ANNUALLY === frequencyType) {
        if (basedOn === 'Date') scheduleObj.yearlyDayValue = scheduleObj.values
        else scheduleObj.yearlyDayOfWeekValues = scheduleObj.values
        scheduleObj.values = scheduleObj.months
        delete scheduleObj.months
      }
      delete scheduleObj.startTime
      delete scheduleObj.basedOn

      return scheduleObj
    },
    setInitialMessage(data) {
      if (!this.messageChanged && this.report) {
        this.mailJson.message =
          'The following scheduled report from ' +
          this.$getProperty(window.brandConfig, 'name', 'Facilio') +
          ' has been mailed to you by ' +
          this.$account.user.name +
          '\n\nReport name : ' +
          (this.name ? this.reportname : this.report.name) +
          (this.report.description
            ? '\nDescription: ' + this.report.description
            : '') +
          '\nFrequency: ' +
          (data ? data.label : '') +
          '\n\nDetailed report is available at \n' +
          window.location.href +
          '\n\nRegards, \nTeam ' +
          this.$getProperty(window.brandConfig, 'name', 'Facilio')
        this.mailJson.subject =
          (this.name ? this.reportname : this.report.name) +
          ' report scheduled at ' +
          this.$options.filters.formatDate(
            this.$helpers.getTimeInOrg(this.scheduleForm.startTime)
          )
      }
    },
    close() {
      this.$emit('update:visibility', false)
    },
    save() {
      if (!this.mailJson.to.length || !this.mailJson.subject) {
        return
      }

      let self = this

      let mailTemplate = this.$helpers.cloneObject(this.mailJson)
      this.$common.setUserMailWorkflow(this.mailJson.to, mailTemplate)
      let startDate = new Date()
      startDate.setHours(0, 0, 0, 0)
      let data = {
        fileFormat: this.format,
        reportId: this.isEdit ? this.model.reportId : this.report.id,
        emailTemplate: mailTemplate,
        scheduleInfo: this.serialize(this.schedule),
        startTime: this.$helpers.getTimeInOrg(this.schedule.startTime),
        startDate: startDate,
        moduleName:
          typeof this.moduleName != 'undefined' && this.moduleName != null
            ? this.moduleName
            : 'energydata', // TODO needs to get generic way
        selected_reportIds: this.selected_reportIds,
        scheduled_report_name: this.scheduled_report_name,
      }
      if (this.name) {
        data.reportId = this.scheduledReportId
      }
      if (
        isEmpty(data.reportId) ||
        (data.reportId == -1 && this?.selected_reportIds?.length)
      ) {
        data.reportId = this.selected_reportIds[0]
      }

      if (this.endType === 'after') {
        if (this.maxCount > 0) {
          data.maxCount = this.maxCount
        } else {
          return
        }
      } else if (this.endType === 'on') {
        if (this.endTime) {
          data.endTime = this.$helpers.getTimeInOrg(this.endTime)
          if (data.endTime < data.startTime) {
            this.$message({
              message: this.$t(
                'common.wo_report.end_time_should_greater_start_time'
              ),
              type: 'error',
            })
            return
          }
        } else {
          return
        }
      }

      let url
      let params
      if (this.isEdit) {
        url = '/v3/report/scheduled/update'
        params = { reportInfo: data, ids: [this.model.id] }
      } else {
        url = '/v3/report/scheduled/create'
        params = { reportInfo: data }
      }
      this.saving = true
      API.post(url, params)
        .then(response => {
          self.saving = false
          if (!response.error) {
            self.$message({
              message: this.$t('common.wo_report.schedule_success'),
              type: 'success',
            })
            self.reset()
            if (!self.isEdit) {
              self.close()
            } else {
              if (response.data.id) {
                self.$emit('save', response.data.result.id)
              }
            }
          } else {
            self.$message({
              message: this.$t('common.wo_report.schedule_failed'),
              type: 'error',
            })
          }
        })
        .catch(() => {
          self.saving = false
        })
        .finally(() => {
          self.close()
        })
      this.$emit('myEvent')
    },
    reset() {
      this.mailJson.to = []
      this.mailJson.subject = ''
      this.mailJson.message = ''
      this.endTime = null
      this.maxCount = null
      this.endType = 'never'
    },
  },
}
</script>
<style lang="scss">
.scheduler-form {
  .button-padding {
    padding-top: 18px !important;
    padding-bottom: 18px !important;
  }
  .fc-modal-sub-title {
    color: #324056;
  }
  .details-Heading {
    font-weight: normal;
    color: #324056;
  }
  .action-row {
    padding: 20px 10px;
  }
  .action-row:hover {
    background-color: #f1f8fa;
  }
  .action-edit-section {
    padding-left: 20px;
    border-left: 1px solid #d9e0e1;
  }
  .configured-green {
    color: #5bc293;
  }
  .reset-txt {
    font-size: 12px;
    letter-spacing: 0.5px;
    color: #6171db;
  }
  .custom-frequency .fc-input-label-txt {
    margin-top: 22px;
  }
}
</style>
