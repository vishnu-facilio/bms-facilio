<template>
  <el-dialog
    id="schedule-form"
    :visible="true"
    :fullscreen="true"
    custom-class="fc-dialog-form fc-dialog-right setup-dialog40 setup-dialog scheduled-view-container"
    width="40%"
    @close="close"
    :append-to-body="true"
  >
    <div class="new-header-container">
      <div class="new-header-text">
        <div class="fc-setup-modal-title">
          {{ isEdit ? 'Edit Schedule View' : 'Schedule This View' }}
        </div>
      </div>
    </div>
    <div class="new-body-modal">
      <div class="fc-modal-sub-title" v-if="showViews">
        {{ $t('viewsmanager.schedules.view') }}
      </div>
      <el-form :rules="viewRules" ref="view-form" :model="selectedView">
        <el-form-item prop="id">
          <el-select
            v-if="showViews"
            v-model="selectedView.id"
            @change="setSelectedView"
            filterable
            default-first-option
            style="width: 280px;overflow:scroll;"
            class="filter-form fc-tag pT15"
            :loading="viewLoading"
          >
            <el-option
              v-for="view in views"
              :key="view.id"
              :label="view.displayName"
              :value="view.id"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div class="fc-modal-sub-title pT15">
        {{ $t('common.wo_report.schedule') }}
      </div>
      <el-date-picker
        v-model="scheduleForm.startTime"
        class="form-item pT15 fc-input-full-border2 fc-date-picker-icon-align"
        float-label="Start Date"
        type="datetime"
        style="width: 56%;"
      />
      <f-schedule
        :from="scheduleForm.startTime"
        v-model="scheduleForm.schedule"
        :initialSchedule="initialSchedule"
        @input2="getfreq"
        class="pT15"
        style="width: 71%;margin-bottom: 20px;"
        simple="true"
      ></f-schedule>
      <div class="fc-input-label-txt">{{ $t('common.wo_report.ends') }}</div>
      <div>
        <div>
          <el-radio
            class="fc-radio-btn pT15"
            v-model="endType"
            color="secondary"
            label="never"
            >{{ $t('common.wo_report.never') }}</el-radio
          >
        </div>
        <div class="row">
          <el-radio
            class="fc-radio-btn pT15"
            v-model="endType"
            color="secondary"
            label="after"
            >{{ $t('common.wo_report.after') }}</el-radio
          >
          <div v-show="endType === 'after'">
            <el-input
              placeholder=""
              type="number"
              v-model="maxCount"
              min="1"
              style="width: 180px;margin-left: 20px;"
            ></el-input>
            <span style="margin-left: 10px;">{{
              $t('common.wo_report.times')
            }}</span>
          </div>
        </div>
        <div class="row">
          <el-radio
            class="fc-radio-btn pT15 "
            v-model="endType"
            color="secondary"
            label="on"
            >{{ $t('common.wo_report.on') }}</el-radio
          >
          <div class="row" v-show="endType === 'on'">
            <el-date-picker
              v-model="endTime"
              type="datetime"
              placeholder="dd/mm/yy"
              style="width:180px;margin-left: 30px;vertical-align: text-bottom;"
            ></el-date-picker>
          </div>
        </div>
      </div>
      <div class="pT20 fc-modal-sub-title" style=" padding-bottom:15px;">
        {{ $t('common.wo_report.email_this_view') }}
      </div>
      <el-form :rules="mailJsonRule" ref="schedule-form" :model="mailJson">
        <el-form-item :label="$t('common.wo_report.to')" prop="to">
          <el-select
            v-model="mailJson.to"
            multiple
            filterable
            default-first-option
            style="overflow:scroll;"
            class="filter-form fc-input-full-border-select2 width100 fc-tag widht100"
          >
            <el-option
              v-for="user in users"
              :key="user.id"
              :label="user.name"
              :value="parseInt(user.id)"
              >{{ user.name + ' (' + user.email + ')' }}</el-option
            >
          </el-select>
        </el-form-item>
        <el-form-item
          :label="$t('common.wo_report.report_subject')"
          prop="subject"
        >
          <template>
            <el-input
              type="text"
              v-model="mailJson.subject"
              @change="messageChanged = true"
            ></el-input>
          </template>
        </el-form-item>
        <el-form-item :label="$t('common.wo_report.report_description')">
          <template>
            <el-input
              type="textarea"
              :autosize="{ minRows: 6, maxRows: 6 }"
              resize="none"
              class="fc-input-full-border-select2 width100"
              v-model="mailJson.message"
              @change="messageChanged = true"
            ></el-input>
          </template>
        </el-form-item>
      </el-form>
    </div>
    <div class="modal-dialog-footer">
      <button type="button" class="modal-btn-cancel" @click="close">
        <span>{{ $t('common._common.cancel') }}</span>
      </button>
      <el-button
        type="button"
        class="modal-btn-save"
        :disabled="disableSave"
        :loading="disableSave"
        @click="save"
      >
        <span>{{ $t('common.wo_report.schedule') }}</span>
      </el-button>
    </div>
  </el-dialog>
</template>
<script>
import FSchedule from '@/FSchedule2'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import { mapState } from 'vuex'
export default {
  props: [
    'viewName',
    'viewId',
    'report',
    'isEdit',
    'model',
    'visibility',
    'viewDisplayName',
    'moduleName',
    'showViews',
    'exportFromView',
  ],
  data() {
    return {
      scheduleForm: {
        startTime: new Date(),
        schedule: null,
      },
      initialSchedule: null,
      format: 2,
      frequencyObj: null,
      mailJson: {
        to: [],
        subject: '',
        message: '',
      },
      endType: 'never',
      endTime: null,
      maxCount: null,
      messageChanged: false,
      viewIds: '',
      viewLoading: false,
      views: [],
      selectedView: {
        name: '',
        displayName: '',
        id: null,
      },
      disableSave: false,
      mailJsonRule: {
        to: [
          {
            required: true,
            message: this.$t('viewsmanager.schedules.select_user'),
            trigger: 'change',
          },
        ],
        subject: [
          {
            required: true,
            message: this.$t('viewsmanager.schedules.subject_empty'),
            trigger: 'blur',
          },
        ],
      },
      viewRules: {
        id: [
          {
            required: true,
            message: this.$t('viewsmanager.schedules.select_view'),
            trigger: 'change',
          },
        ],
      },
    }
  },
  components: {
    FSchedule,
  },
  created() {
    if (this.isEdit || this.exportFromView)
      this.selectedView = {
        name: this.viewName,
        displayName: this.viewDisplayName,
        id: this.viewId,
      }
  },
  mounted() {
    let { showViews } = this
    if (!isEmpty(showViews) && showViews) {
      this.loadViews()
    } else {
      this.init()
      this.checkList()
    }
  },
  computed: {
    ...mapState({
      users: state => state.users,
    }),
    currentModuleName() {
      let { moduleName } = this
      return !isEmpty(moduleName) ? moduleName : this.route.meta.module
    },
  },
  watch: {
    viewId(val) {
      this.viewIds = val
    },
  },
  methods: {
    setSelectedView(viewId) {
      let { views } = this
      let viewObj = views.find(view => view.id === viewId)

      if (!isEmpty(viewObj)) {
        this.selectedView = { ...viewObj }
        this.setInitialMessage(this.frequencyObj, true)
      }
    },
    loadViews() {
      this.viewLoading = true
      let { currentModuleName } = this
      API.get('/view?moduleName=' + currentModuleName)
        .then(({ data, error }) => {
          if (error) {
            this.$message.error(error.message || 'Error occured')
          } else {
            if (!isEmpty(data)) {
              let { views } = data || {}
              this.views = views
              this.init()
              this.checkList()
            }
          }
        })
        .finally(() => {
          this.viewLoading = false
        })
    },
    init() {
      if (this.isEdit && this.model) {
        this.model.scheduleForm.schedule.timeObjects = null
        this.scheduleForm.startTime = this.model.scheduleForm.startTime
        this.scheduleForm.schedule = this.model.scheduleForm.schedule
        this.initialSchedule = this.model.scheduleForm.schedule
        this.format = this.model.format
        this.mailJson = this.model.mailJson
        this.mailJson.to = this.model.mailJson.to
        if (this.model.endTime) {
          this.endTime = this.model.endTime
        }
        if (this.model.maxCount) {
          this.maxCount = this.model.maxCount
        }
        if (this.model.viewName) {
          this.selectedView.name = this.model.viewName
        }
        if (this.maxCount > 0) {
          this.endType = 'after'
        } else {
          this.endType = this.endTime > 0 ? 'on' : 'never'
        }
      } else {
        this.scheduleForm.startTime = new Date()
        this.setInitialMessage(this.frequencyObj, false)
      }
    },
    checkList() {
      let { currentModuleName } = this
      let url

      url = '/view/woScheduleViewList'
      this.$http.post(url, { moduleName: currentModuleName })
    },
    setInitialMessage(data, messageChange) {
      let { selectedView } = this || []
      let { displayName } = selectedView || {}

      if ((!this.messageChanged && this.report) || messageChange) {
        this.mailJson.message =
          'Here are the ' +
          displayName +
          (this.currentModuleName === 'workorder'
            ? ' tasks and work orders scheduled for the day by '
            : 'assets scheduled for the day by ') +
          this.$account.user.name +
          '\nView name : ' +
          displayName +
          '\nFrequency: ' +
          (data ? data.label : '') +
          '\n\nYou could view the details here \n' +
          window.location.href +
          '\n\nRegards, \nTeam ' +
          this.$getProperty(window.brandConfig, 'name', 'Facilio')
        this.mailJson.subject =
          displayName +
          this.$t('common.wo_report.view_schedule_at') +
          ' ' +
          this.$options.filters.formatDate(
            this.$helpers.getTimeInOrg(this.scheduleForm.startTime)
          )
      }
    },
    getfreq(data) {
      let { selectedView } = this || []
      let { displayName, name } = selectedView || {}

      this.frequencyObj = data
      this.mailJson.message =
        'Here are the ' +
        name +
        (this.currentModuleName === 'workorder'
          ? ' tasks and work orders scheduled for the day by '
          : ' assets scheduled for the day by ') +
        this.$account.user.name +
        '\nView name : ' +
        displayName +
        '\nFrequency: ' +
        (data ? data.label : '') +
        '\nYou could view the details here \n' +
        window.location.href +
        '\nRegards, \nTeam ' +
        this.$getProperty(window.brandConfig, 'name', 'Facilio')
      this.mailJson.subject =
        displayName +
        this.$t('common.wo_report.view_schedule_at') +
        this.scheduleForm.startTime
      return data
    },
    async validate() {
      let { showViews } = this
      let viewValidation = true,
        scheduleValidation = true
      try {
        scheduleValidation = await this.$refs['schedule-form'].validate()
      } catch {
        scheduleValidation = false
      }
      try {
        if (!isEmpty(showViews) && showViews) {
          viewValidation = await this.$refs['view-form'].validate()
        }
      } catch {
        viewValidation = false
      }
      return scheduleValidation && viewValidation
    },
    async save() {
      // this.$emit('addViewInDb')
      let valid = await this.validate()
      if (!valid) return
      this.disableSave = true
      let { selectedView } = this
      let mailTemplate = this.$helpers.cloneObject(this.mailJson)
      this.$common.setUserMailWorkflow(this.mailJson.to, mailTemplate)

      let data = {
        type: this.format,
        viewName: selectedView.name,
        moduleName: this.currentModuleName,
        viewId: selectedView.id,
        id: this.isEdit ? this.model.id : -1,
        emailTemplate: mailTemplate,
        scheduleInfo: this.scheduleForm.schedule,
        startTime: this.$helpers.getTimeInOrg(this.scheduleForm.startTime),
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
              message: this.$t('common.wo_report.end_time_schedule'),
              type: 'error',
            })
            return
          }
        } else {
          return
        }
      }
      let url
      if (this.isEdit) {
        url = '/view/editWoScheduledView'
        data.id = this.model.id
        data.moduleName = this.currentModuleName
      } else {
        url = '/view/woScheduleView'
      }

      API.post(url, data)
        .then(({ error }) => {
          if (error) {
            this.$message.error(
              error.message || this.$t('common.wo_report.view_schedule_failed')
            )
          } else {
            this.$message({
              message: this.$t('common.wo_report.view_schedule_success'),
              type: 'success',
            })
            this.$emit('save')
          }
          this.close()
        })
        .finally(() => {
          this.disableSave = false
        })
    },
    reset() {
      this.mailJson.to = []
      this.mailJson.subject = ''
      this.mailJson.message = ''
      this.endTime = null
      this.maxCount = null
      this.endType = 'never'
    },
    close() {
      this.$emit('close')
    },
  },
}
</script>
<style lang="scss">
.scheduled-view-container {
  .el-form-item__content {
    line-height: unset;
  }
}
</style>
