<template>
  <el-dialog
    id="schedule-form"
    :visible.sync="visibility"
    :fullscreen="true"
    custom-class="fc-dialog-right"
    class="trend-dialog headernew"
    @close="close"
  >
    <div>
      <div class="title">{{ $t('common.wo_report.schedule_report') }}</div>
      <div class=" pT20">
        <div class="subheader colorPink">
          {{ $t('common.wo_report.schedule') }}
        </div>
        <el-date-picker
          v-model="scheduleForm.startTime"
          class="form-item pT15"
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
      </div>
      <div class="heading">{{ $t('common.wo_report.ends') }}</div>
      <div>
        <div>
          <el-radio
            class="newradio pT15 labelText"
            v-model="endType"
            color="secondary"
            label="never"
            >{{ $t('common.wo_report.never') }}</el-radio
          >
        </div>
        <div class="row">
          <el-radio
            class="newradio pT15 labelText"
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
            class="newradio pT15 labelText"
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
      <div class="pT20 subheader colorPink" style=" padding-bottom:15px;">
        {{ $t('common.wo_report.email_this_report') }}
      </div>
      <div class="heading" style="float:right;">
        {{ $t('common.wo_report.format') }}
        <div>
          <el-select v-model="format">
            <el-option
              v-for="(label, value) in $constants.FILE_FORMAT"
              v-if="label !== 'Image'"
              :key="value"
              :label="label"
              :value="parseInt(value)"
            ></el-option>
          </el-select>
        </div>
      </div>
      <div class="heading">{{ $t('common.wo_report.to') }}</div>
      <el-select
        v-model="mailJson.to"
        multiple
        filterable
        default-first-option
        style="width: 280px;overflow:scroll;"
        class="filter-form"
      >
        <el-option
          v-for="user in users"
          :key="user.id"
          :label="user.name"
          :value="parseInt(user.id)"
          >{{ user.name + ' (' + user.email + ')' }}</el-option
        >
      </el-select>
      <div class="heading pT15" style="clear:both;">
        {{ $t('common.wo_report.report_subject') }}
      </div>
      <div>
        <el-input
          type="text"
          v-model="mailJson.subject"
          @change="messageChanged = true"
        ></el-input>
      </div>
      <div class="heading pT15">
        {{ $t('common.wo_report.report_description') }}
      </div>
      <div>
        <el-input
          type="textarea"
          :autosize="{ minRows: 6, maxRows: 6 }"
          resize="none"
          class="fc-input-txt fc-desc-input"
          v-model="mailJson.message"
          @change="messageChanged = true"
        ></el-input>
      </div>
    </div>

    <div class="modal-dialog-footer">
      <button type="button" class="modal-btn-cancel" @click="close">
        <span>{{ $t('common._common.cancel') }}</span>
      </button>
      <button type="button" class="modal-btn-save" @click="save">
        <span>{{ $t('common.wo_report.schedule') }}</span>
      </button>
    </div>
  </el-dialog>
</template>
<script>
import FSchedule from '@/FSchedule2'
export default {
  props: ['visibility', 'report', 'isEdit', 'model'],
  data() {
    return {
      scheduleForm: {
        startTime: new Date(),
        schedule: null,
      },
      initialSchedule: null,
      format: 1,
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
    }
  },
  components: {
    FSchedule,
  },
  computed: {
    users() {
      return this.$store.state.users
    },
  },
  watch: {
    visibility(val) {
      if (val) {
        if (this.isEdit) {
          this.init()
        } else {
          this.scheduleForm.startTime = new Date()
          this.setInitialMessage(this.frequencyObj)
        }
      }
    },
  },
  mounted() {
    this.init()
  },
  methods: {
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
        if (this.maxCount > 0) {
          this.endType = 'after'
        } else {
          this.endType = this.endTime > 0 ? 'on' : 'never'
        }
      }
    },
    setInitialMessage(data) {
      if (!this.messageChanged && this.report) {
        this.mailJson.message =
          this.$t('common.wo_report.mail_reported_from_facilio', [
            this.$getProperty(window.brandConfig, 'name', 'Facilio'),
          ]) +
          this.$account.user.name +
          '\n\nReport name : ' +
          this.report.name +
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
          this.report.name +
          this.$t('common.wo_report.report_schedule_at') +
          this.$options.filters.formatDate(
            this.$helpers.getTimeInOrg(this.scheduleForm.startTime)
          )
      }
    },
    close() {
      this.$emit('update:visibility', false)
    },
    getfreq(data) {
      this.frequencyObj = data
      this.mailJson.message =
        this.$t('common.wo_report.mail_reported_from_facilio', [
          this.$getProperty(window.brandConfig, 'name', 'Facilio'),
        ]) +
        this.$account.user.name +
        '\nReport name : ' +
        this.report.options.name +
        '\nDescription: ' +
        this.report.options.description +
        '\nFrequency: ' +
        (data ? data.label : '') +
        '\nDetailed report is available in the here. \n' +
        window.location.href +
        '\nRegards, \nTeam ' +
        this.$getProperty(window.brandConfig, 'name', 'Facilio')
      this.mailJson.subject =
        this.report.options.name +
        this.$t('common.wo_report.report_schedule_at') +
        this.scheduleForm.startTime
      return data
    },
    save() {
      if (!this.mailJson.to.length || !this.mailJson.subject) {
        return
      }

      let self = this

      let mailTemplate = this.$helpers.cloneObject(this.mailJson)
      this.$common.setUserMailWorkflow(this.mailJson.to, mailTemplate)
      let data = {
        type: this.format,
        reportId: this.isEdit ? this.model.reportId : this.report.options.id,
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
        url = '/dashboard/editScheduledReport'
        data.id = [this.model.id]
      } else {
        url = '/dashboard/scheduleReport'
      }

      this.$http.post(url, data).then(function(response) {
        if (typeof response.data === 'object') {
          self.$message({
            message: self.$t('common.wo_report.schedule_success'),
            type: 'success',
          })
          self.reset()
          if (!self.isEdit) {
            self.close()
          } else {
            self.$emit('save')
          }
        } else {
          self.$message({
            message: self.$t('common.wo_report.schedule_failed'),
            type: 'error',
          })
        }
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
  },
}
</script>
<style>
.titlefont {
  font-size: 11px;
}
.item__label::before {
  content: '*';
  color: #fa5555;
  margin-right: 4px;
}
.heading {
  font-size: 13px;
  font-weight: normal;
  letter-spacing: 0.4px;
  text-align: left;
  color: #6b7e91;
}
.headernew .el-dialog__header {
  padding: 0px;
}
.headernew .el-dialog {
  width: 40%;
}
.subheader {
  font-size: 12px;
  font-weight: 500;
  text-transform: uppercase;
  color: #fa5555;
}
.colorPink {
  color: #ef4f8f;
  letter-spacing: 1.6px;
}
.labelText {
  font-weight: 400;
}
.el-dialog__body {
  height: 100%;
  overflow-y: scroll;
}
.el-select .el-tag {
  border-radius: 100px;
  background-color: #f8ffff;
  border: solid 1px #39b1c1;
  font-size: 12px;
  letter-spacing: 0.4px;
  color: #2c9baa;
  margin: 3px 6px 3px 0px;
}
/* .el-select .el-tag__close.el-icon-close, .el-select .el-tag__close.el-icon-close:hover{
    background: #2c9baa;
    color: #fff;
} */
.form-item .q-if-label-above {
  color: #6b7e91 !important;
}
</style>
