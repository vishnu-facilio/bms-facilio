<template>
  <el-dialog
    :visible.sync="visibility"
    :fullscreen="true"
    custom-class="fc-dialog-right"
    class="trend-dialog headernew"
    :before-close="close"
  >
    <div id="emailReport">
      <div class="dialog-title title">
        {{ $t('common.wo_report.email_this_report') }}
      </div>
      <div class="heading pT15" style="float:right;">
        {{ $t('common.wo_report.format') }}
        <div>
          <el-select class="email-format" v-model="format">
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
      <div class="heading pT15 ">To</div>
      <div>
        <el-select
          v-model="mailJson.to"
          multiple
          filterable
          default-first-option
          style="width: 300px;overflow:scroll;"
          class="filter-form"
        >
          <el-option
            v-for="user in users"
            v-if="user.inviteAcceptStatus"
            :key="user.id"
            :label="user.name"
            :value="user.id"
            >{{ user.name + ' (' + user.email + ')' }}</el-option
          >
        </el-select>
      </div>
      <!-- <el-input type="text" v-model="mailJson.to" style="width: 300px;float:left;"></el-input> -->
      <div class="heading pT15" style="clear:both;">
        {{ $t('maintenance.wr_list.subject') }}
      </div>
      <div><el-input type="text" v-model="mailJson.subject"></el-input></div>
      <div class="heading pT15">
        {{ $t('setup.approvalprocess.description') }}
      </div>
      <div>
        <el-input
          type="textarea"
          :rows="3"
          v-model="mailJson.message"
        ></el-input>
      </div>
    </div>
    <div class="modal-dialog-footer">
      <button type="button" class="modal-btn-cancel" @click="close">
        <!----><!----><span>{{ $t('setup.users_management.cancel') }}</span>
      </button>
      <button type="button" class="modal-btn-save" @click="sendMail">
        <!----><!----><span>{{ $t('common.wo_report.send') }}</span>
      </button>
    </div>
  </el-dialog>
</template>
<script>
import AnalyticsMixin from 'pages/energy/analytics/mixins/AnalyticsMixin'
export default {
  props: ['visibility', 'report', 'analyticsConfig'],
  mixins: [AnalyticsMixin],
  data() {
    return {
      format: 1,
      mailJson: {
        to: [],
        subject: this.report.options.name + 'Report',
        message:
          'The following Report from ' +
          this.$getProperty(window.brandConfig, 'name', 'Facilio') +
          ' has been mailed to you by ' +
          this.$account.user.name +
          '\nReport name : ' +
          this.report.options.name +
          '\nDescription: ' +
          this.report.options.description +
          '\nDetailed report is available in the here. \n' +
          window.location.href +
          '\nRegards, \nTeam ' +
          this.$getProperty(window.brandConfig, 'name', 'Facilio'),
      },
    }
  },
  mounted() {
    if (this.report && this.report.options && this.report.options.name) {
      this.mailJson.subject = this.report.options.name + ' Report'
      this.mailJson.message =
        'Hi,\nThe report on ' +
        this.report.options.name +
        'has been sent to you from ' +
        this.$getProperty(window.brandConfig, 'name', 'Facilio') +
        '. Please have a look at ' +
        window.location.href
    }
  },
  computed: {
    users() {
      return this.$store.state.users
    },
  },
  methods: {
    close() {
      this.$emit('update:visibility', false)
    },
    sendMail(type) {
      let self = this

      if (!this.mailJson.to.length || !this.mailJson.subject) {
        return
      }

      let mailTemplate = this.$helpers.cloneObject(this.mailJson)
      this.$common.setUserMailWorkflow(this.mailJson.to, mailTemplate)
      let data = {
        type: this.format,
        emailTemplate: mailTemplate,
      }

      if (this.report) {
        data.reportId = this.report.options.id
      } else {
        this.$helpers.extend(data, this.getExportParam(this.analyticsConfig))
      }

      this.$message({ message: 'Sending email...', duration: 0 })
      this.close()

      this.$http
        .post('/dashboard/sendReportMail', data)
        .then(function(response) {
          self.$message.closeAll()
          if (typeof response.data === 'object') {
            self.$message({
              message: 'Email sent successfully!',
              type: 'success',
            })

            self.mailJson.to = []
            self.mailJson.subject = ''
            self.mailJson.message = ''
          } else {
            self.$message({
              message: 'Email sending failed!',
              type: 'error',
            })
          }
        })
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
.headernew .el-dialog__header {
  padding: 0px;
}
.headernew .el-dialog {
  width: 40%;
}

#emailReport .email-format .el-input .el-input__inner {
  height: 40px !important;
}
#emailReport .el-textarea .el-textarea__inner {
  min-height: 80px !important;
}
</style>
