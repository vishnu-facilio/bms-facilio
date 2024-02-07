<template>
  <el-dialog
    :visible.sync="visibility"
    custom-class="fc-dialog-center-container fc-dashboard-email-dialog"
    :before-close="close"
    :append-to-body="true"
  >
    <div>
      <div class="new-header-container">
        <div class="new-header-text">
          <div class="fc-setup-modal-title">
            {{ $t('common.wo_report.email_this_report') }}
          </div>
        </div>
      </div>
      <div class="height500 p30">
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="fc-input-label-txt">To</div>
            <div>
              <el-select
                v-model="mailJson.to"
                multiple
                filterable
                default-first-option
                class="fc-input-full-border2 width100 fc-tag"
                collapse-tags
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
          </el-col>
          <el-col :span="12">
            <div class="fc-input-label-txt">
              Format
              <div>
                <el-select
                  class="fc-input-full-border2 width100 mT10"
                  v-model="format"
                  collapse-tags
                >
                  <el-option
                    v-for="(label, value) in fileFormats"
                    :key="value"
                    :label="label"
                    :value="parseInt(value)"
                  >
                  </el-option>
                </el-select>
              </div>
            </div>
          </el-col>
        </el-row>
        <!-- <el-input type="text" v-model="mailJson.to" style="width: 300px;float:left;"></el-input> -->
        <!-- <el-row class="mT20">
            <el-col :span="18">
            <div class="fc-input-label-txt mB20">PDF Options</div>
            <div><el-checkbox v-model="checked">Include Legends</el-checkbox></div>
            <div class="mT10"> <el-checkbox v-model="checked">Include Table</el-checkbox></div>
            </el-col>
          </el-row> -->
        <div class="fc-input-label-txt pT15" style="clear:both;">Subject</div>
        <div>
          <el-input
            type="text"
            class="fc-input-full-border-select2"
            v-model="mailJson.subject"
          ></el-input>
        </div>
        <div class="fc-input-label-txt pT20">Description</div>
        <div>
          <el-input
            type="textarea"
            v-model="mailJson.message"
            :placeholder="$t('common._common.enter_desc')"
            autoComplete="off"
            :autosize="{ minRows: 4, maxRows: 4 }"
            resize="none"
            class="fc-input-full-border-select2"
          ></el-input>
        </div>
      </div>
    </div>
    <div class="modal-dialog-footer">
      <el-button @click="close" class="modal-btn-cancel">CANCEL</el-button>
      <el-button type="primary" @click="sendMail" class="modal-btn-save"
        >SEND</el-button
      >
    </div>
  </el-dialog>
</template>
<script>
import Vue from 'vue'
export default {
  props: ['visibility', 'report', 'moduleReportConfig'],
  data() {
    return {
      format: 1,
      mailJson: {
        to: [],
        subject: '',
        message: '',
      },
    }
  },
  computed: {
    type() {
      let { type } = this.report
      if (type) {
        return type
      }
      return -1
    },
    moduleName() {
      let { module } = this.report
      if (module && module.name) {
        return module.name
      }
      return null
    },
    users() {
      return this.$store.state.users
    },
    params() {
      let { report, type } = this
      if (report && type === 1) {
        let dateOperator = report.dateRange.operatorId
        let dateOperatorValue =
          report.dateRange.operatorId === 20
            ? report.dateRange?.time?.join(',')
            : report.dateRange.value
        if (!dateOperator && report?.dateOperator)
          dateOperator = report.dateOperator
        if (!dateOperatorValue && report?.dateValue)
          dateOperatorValue = report.dateValue
        let params = {
          mode: this.type,
          startTime: report.dateRange.startTime,
          endTime: report.dateRange.endTime,
          dateOperator: dateOperator,
          dateOperatorValue: dateOperatorValue,
          chartType: report.chartState ? report.chartState.type : null,
        }
        return params
      }
      return {}
    },
    fileFormats() {
      let fileFormats = {}
      let formats = Vue.prototype.$constants.FILE_FORMAT
      if (
        (this.report && this.report.id && this.report.id != -1) ||
        (this.params && this.params.reportId)
      ) {
        for (let format in formats) {
          if (formats[format] !== 'Image') {
            fileFormats[format] = formats[format]
          }
        }
      } else {
        for (let format in formats) {
          if (formats[format] !== 'Image' && formats[format] !== 'PDF') {
            fileFormats[format] = formats[format]
          }
        }
      }
      return fileFormats
    },
  },
  mounted() {
    if (this.report) {
      this.mailJson.subject =
        this.report.name +
        (!this.report.name.endsWith('report') &&
        !this.report.name.endsWith('Report')
          ? ' report'
          : '')
      this.mailJson.message =
        'Hi,\nThe report on ' +
        this.report.name +
        ' has been sent to you from ' +
        this.$getProperty(window.brandConfig, 'name', 'Facilio') +
        '.'
    } else {
      this.mailJson.message = 'Hi,\nThis report has been sent to you.'
    }
  },
  methods: {
    close() {
      this.$emit('update:visibility', false)
    },
    sendMail() {
      let self = this
      let { type } = this
      let url = null
      if (!this.mailJson.to.length || !this.mailJson.subject) {
        return
      }

      let mailTemplate = this.$helpers.cloneObject(this.mailJson)
      this.$common.setUserMailWorkflow(this.mailJson.to, mailTemplate)
      let data = {
        fileFormat: this.format,
        emailTemplate: mailTemplate,
      }
      if (type === 2) {
        if (this.report.id && this.report.id !== -1) {
          data['reportId'] = this.report.id
        } else {
          this.$helpers.extend(data, this.moduleReportConfig)
          data['moduleName'] = this.moduleName
        }
        this.$message({
          message: 'Sending email...',
          duration: 0,
        })
        this.close()
        url = '/v2/report/sendModuleReportMail'
      } else {
        this.$helpers.extend(data, this.params)
        if (this.report.id && this.report.id !== -1) {
          data['reportId'] = this.report.id
        }
        this.$message({
          message: 'Sending email...',
          duration: 0,
        })
        this.close()
        url = '/v2/report/sendMail'
        if (
          this.report &&
          this.report.type === 4 &&
          this.report.currentTemplate
        ) {
          url =
            url +
            '?templateString=' +
            encodeURIComponent(JSON.stringify(this.report.currentTemplate))
        }
      }

      this.$http.post(url, data).then(function(response) {
        self.$message.closeAll()
        if (response.data && response.data.responseCode === 0) {
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
<style lang="scss">
.fc-dashboard-email-dialog {
  .el-dialog {
    margin-top: 15vh;
  }

  .el-dialog__header {
    border-bottom: none;
    padding: 0;

    .el-dialog__headerbtn {
      z-index: 4;
    }
  }

  .el-dialog__body {
    padding: 0;
  }
}
</style>
