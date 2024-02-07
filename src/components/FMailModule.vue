<template>
  <el-dialog
    :visible.sync="visibility"
    :fullscreen="true"
    custom-class="fc-dialog-form fc-dialog-right setup-dialog fc-dialog-right-hide setup-dialog45"
    :before-close="close"
    :append-to-body="true"
  >
    <error-banner
      :error.sync="error"
      :errorMessage="errorMessage"
    ></error-banner>
    <div class="new-header-container">
      <div class="new-header-text">
        <div class="fc-setup-modal-title">{{ 'Email ' + module }}</div>
      </div>
    </div>
    <div class="new-body-modal">
      <el-row>
        <el-col :span="12">
          <div class="fc-input-label-txt">To</div>
          <el-select
            v-model="mailJson.to"
            multiple
            filterable
            default-first-option
            collapse-tags
            class="width240px fc-tag"
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
        </el-col>
        <el-col :span="12">
          <div class="fc-input-label-txt">Format</div>
          <el-select
            class="email-format width240px"
            v-model="format"
            collapse-tags
          >
            <el-option
              v-for="(label, value) in $constants.FILE_FORMAT"
              v-if="label !== 'Image' && label !== 'PDF'"
              :key="value"
              :label="label"
              :value="parseInt(value)"
            ></el-option>
          </el-select>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="22" class="mT20">
          <div class="fc-input-label-txt pT15">Subject</div>
          <el-input
            class="text-capitalize"
            type="text"
            v-model="mailJson.subject"
          ></el-input>
        </el-col>
      </el-row>
      <el-row class="mT20">
        <el-col :span="22">
          <div class="fc-input-label-txt">Description</div>
          <el-input
            type="textarea"
            v-model="mailJson.message"
            :placeholder="$t('common._common.enter_desc')"
            autoComplete="off"
            :autosize="{ minRows: 6, maxRows: 6 }"
            class="fc-input-txt fc-desc-input"
          ></el-input>
        </el-col>
      </el-row>
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
import ErrorBanner from '@/ErrorBanner'
export default {
  props: ['visibility', 'module', 'viewName', 'viewDetail', 'filters'],
  data() {
    return {
      format: 1,
      error: false,
      errorMessage: null,
      mailJson: {
        to: [],
        subject: '',
        message: '',
      },
    }
  },
  components: {
    ErrorBanner,
  },
  computed: {
    users() {
      return this.$store.state.users
    },
  },
  mounted() {
    this.mailJson.subject = this.viewDetail.displayName + ' ' + this.module
    this.mailJson.message =
      'Hi,\n List of ' +
      this.viewDetail.displayName +
      ' ' +
      this.module +
      ' has been sent to you from ' +
      this.$getProperty(window.brandConfig, 'name', 'Facilio') +
      '. Please have a look'
  },
  methods: {
    close() {
      this.$emit('update:visibility', false)
    },
    sendMail(type) {
      let self = this
      this.error = false
      this.errorMessage = null
      if (!this.mailJson.to.length || !this.mailJson.subject) {
        this.errorMessage = 'Please specify at least one recipient to send mail'
        this.error = true
        return
      }
      let mailTemplate = this.$helpers.cloneObject(this.mailJson)
      this.$common.setUserMailWorkflow(this.mailJson.to, mailTemplate)
      let data = {
        type: this.format,
        emailTemplate: mailTemplate,
        moduleName: this.module,
        viewName: this.viewName,
      }
      if (this.filters) {
        data.filters = JSON.stringify(this.filters)
      }
      this.$message({
        message: 'Sending email...',
        duration: 0,
      })
      this.close()
      let url = '/v2/sendMail'
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
