<template>
  <el-dialog
    :visible.sync="visibility"
    :fullscreen="true"
    custom-class="fc-dialog-form fc-dialog-right setup-dialog40 fc-dialog-right-hide"
    class="trend-dialog headernew"
    :before-close="close"
    :append-to-body="true"
  >
    <el-form>
      <div>
        <div class="new-header-container">
          <div class="new-header-text">
            <div class="fc-setup-modal-title">
              {{ $t('common.wo_report.email_this_report') }}
            </div>
          </div>
        </div>
        <div class="new-body-modal">
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
          <div style="float:right;">
            <div class="fc-input-label-txt">
              {{ $t('common.wo_report.format') }}
            </div>
            <el-select class="email-format" v-model="format" collapse-tags>
              <el-option
                v-for="(label, value) in fileFormats"
                :key="value"
                :label="label"
                :value="parseInt(value)"
              ></el-option>
            </el-select>
          </div>
          <div>
            <div class="fc-input-label-txt">{{ $t('common.products.to') }}</div>
            <el-select
              v-model="mailJson.to"
              multiple
              filterable
              default-first-option
              style="width: 310px;overflow:scroll;"
              class="fc-input-full-border-select2"
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
          <div class="fc-input-label-txt pT15" style="clear:both;">
            {{ $t('maintenance.wr_list.subject') }}
          </div>
          <div>
            <el-input type="text" v-model="mailJson.subject"></el-input>
          </div>
          <div class="fc-input-label-txt pT20">
            {{ $t('setup.approvalprocess.description') }}
          </div>
          <div>
            <el-input
              type="textarea"
              v-model="mailJson.message"
              :placeholder="$t('common._common.enter_desc')"
              autoComplete="off"
              :autosize="{ minRows: 6, maxRows: 6 }"
              class="fc-input-txt fc-desc-input"
            ></el-input>
          </div>
        </div>
      </div>
    </el-form>
    <div class="modal-dialog-footer">
      <el-button @click="close" class="modal-btn-cancel">{{
        $t('setup.users_management.cancel')
      }}</el-button>
      <el-button type="primary" @click="sendMail" class="modal-btn-save">{{
        $t('common.wo_report.send')
      }}</el-button>
    </div>
  </el-dialog>
</template>
<script>
import Vue from 'vue'
import { EmailModel } from 'src/newapp/setupActions/models/EmailModel.js'
import { isEmpty } from '@facilio/utils/validation'
export default {
  components: {
    EmailModel,
  },
  props: [
    'visibility',
    'params',
    'report',
    'moduleName',
    'moduleReportConfig',
    'name',
    'scheduledReportId',
    'reportname',
    'mailParams',
    'url',
  ],
  data() {
    return {
      format: 1,
      mailJson: {
        fromID: null,
        to: [],
        subject: '',
        message: '',
      },
      senderEmailList: [],
    }
  },
  created() {
    this.getFromAddressList()
  },
  computed: {
    users() {
      return this.$store.state.users
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
        (this.name ? this.reportname : this.report.name) +
        (!this.name &&
        !this.report.name.endsWith('report') &&
        !this.report.name.endsWith('Report')
          ? ' report'
          : '')
      this.mailJson.message =
        'Hi,\n\nThe report on ' +
        (this.name ? this.reportname : this.report.name) +
        ' has been sent to you from ' +
        this.$getProperty(window.brandConfig, 'name', 'Facilio') +
        '.\n\nPlease have a look at ' +
        window.location.href
    } else {
      this.mailJson.message =
        'Hi,\nThis report has been sent to you from ' +
        this.$getProperty(window.brandConfig, 'name', 'Facilio') +
        '. Please have a look'
    }
  },
  methods: {
    async getFromAddressList() {
      this.senderEmailList = await EmailModel.getSenderList()
      if(!isEmpty(this.senderEmailList)){
        this.mailJson.fromID = this.senderEmailList[0].id
      }
    },
    close() {
      this.$emit('update:visibility', false)
    },
    sendMail(type) {
      let self = this
      let url = null
      if (!this.mailJson.to.length || !this.mailJson.subject) {
        return
      }

      let mailTemplate = this.$helpers.cloneObject(this.mailJson)
      this.$common.setUserMailWorkflow(this.mailJson.to, mailTemplate)
      let data = {
        fileFormat: this.format,
        emailTemplate: mailTemplate,
        url: this.url ? this.url : '',
      }
      if (
        typeof this.moduleName !== 'undefined' &&
        this.moduleName != 'energydata'
      ) {
        if (this.report.id && this.report.id !== -1) {
          data['reportId'] = this.report.id
        } else {
          this.$helpers.extend(data, this.moduleReportConfig)
          data['moduleName'] = this.moduleName
        }
        this.$message({ message: 'Sending email...', duration: 0 })
        this.close()
        url = '/v2/report/sendModuleReportMail'
      } else {
        this.$helpers.extend(data, this.params)
        if (typeof this.mailParams !== 'undefined') {
          this.$helpers.extend(data, this.mailParams)
        }
        this.$message({ message: 'Sending email...', duration: 0 })
        this.close()
        if (this.name) {
          url = '/v3/report/pivot/sendMail'
          data['reportId'] = this.scheduledReportId
          data['moduleName'] = this.reportname
        } else {
          url = '/v2/report/sendMail'
          data.fileFormat = this.format
        }
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
        if (
          (response.data && response.data.code === 0) ||
          (response.data && response.data.responseCode === 0)
        ) {
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
