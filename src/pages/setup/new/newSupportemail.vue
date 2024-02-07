<template>
  <el-dialog
    :visible.sync="visibility"
    :fullscreen="true"
    :append-to-body="true"
    custom-class="fc-dialog-form fc-support-dialog fc-dialog-right setup-dialog45"
    @close="close"
    :before-close="handleclose"
    class="support-container"
  >
    <error-banner
      :error.sync="error"
      :errorMessage.sync="errorText"
    ></error-banner>
    <!-- header -->
    <div class="new-header-container">
      <div class="new-header-text">
        <div class="fc-setup-modal-title">
          {{ isEditMode ? 'Edit Support email' : 'New Support email' }}
        </div>
      </div>
    </div>
    <!-- body -->
    <div class="new-body-modal">
      <el-form
        :model="supportEmail"
        ref="newSupportEmailForm"
        @submit.prevent="save"
        :label-position="'top'"
      >
        <el-row>
          <p class="fc-input-label-txt">Name</p>
          <el-form-item prop="replyName">
            <el-input
              v-model="supportEmail.replyName"
              :autofocus="true"
              placeholder="Support Name"
              class="fc-input-full-border2 width100"
            ></el-input>
          </el-form-item>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <p class="fc-input-label-txt">Your support email</p>
            <el-form-item prop="actualEmail">
              <el-input
                v-model="supportEmail.actualEmail"
                placeholder="Eg:support@domain.facilio.com"
                class="fc-input-full-border-select2 width100"
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <div class="setup-input-block">
              <p class="fc-input-label-txt">Forward your emails to</p>
              <el-form-item prop="fwdEmail">
                <el-input
                  v-model="supportEmail.fwdEmail"
                  :disable="true"
                  placeholder="Support Forward Email"
                  class="fc-input-full-border-select2 width100"
                ></el-input>
              </el-form-item>
            </div>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <div class="setup-input-block">
              <p class="fc-input-label-txt">Sites</p>
              <el-select
                v-model="supportEmail.siteId"
                class="fc-input-full-border-select2 width100"
                placeholder="Select Site"
              >
                <el-option
                  v-for="site in sites"
                  :key="site.id"
                  :label="site.name"
                  :value="site.id"
                ></el-option>
              </el-select>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="setup-input-block">
              <p class="fc-input-label-txt">Assign to Team</p>
              <el-form-item prop="autoAssignGroupId">
                <el-select
                  v-model="supportEmail.autoAssignGroupId"
                  class="fc-input-full-border-select2 width100"
                  float-label="Assign to Team"
                  placeholder="Assign Team"
                >
                  <el-option
                    v-for="group in groups"
                    :key="group.id"
                    :label="group.name"
                    :value="group.id"
                  ></el-option>
                </el-select>
              </el-form-item>
            </div>
          </el-col>
        </el-row>
        <el-row :gutter="20" v-if="isCustomMail">
          <el-col :span="12">
            <div class="setup-input-block">
              <!-- <p class="fc-input-label-txt">Sites</p> -->
              <el-checkbox v-model="supportEmail.customMail">
                Use your own mail server
              </el-checkbox>
            </div>
          </el-col>
        </el-row>
        <el-row v-if="supportEmail.customMail">
          <div class="pT10 pB30">
            <div
              class="rule-border-blue p20 mB15 position-relative"
              style="border-left: 1px solid rgb(228, 235, 241);"
            >
              <div
                id="ownMailServer-header"
                class="fc-input-label-txt mL10 pB0 text-fc-pink"
              >
                Incoming Mail Settings
              </div>

              <el-row :gutter="20" class="pT20">
                <el-col :span="12">
                  <p class="fc-input-label-txt">Incoming Mail Server</p>
                  <el-form-item prop="mailServer">
                    <el-input
                      v-model="supportEmail.mailServer"
                      placeholder=""
                      class="fc-input-full-border-select2 width100"
                    ></el-input>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <div class="setup-input-block">
                    <p class="fc-input-label-txt">Port</p>
                    <el-form-item prop="port">
                      <el-input
                        v-model="supportEmail.port"
                        :disable="true"
                        class="fc-input-full-border-select2 width100"
                      ></el-input>
                    </el-form-item>
                  </div>
                </el-col>
              </el-row>
              <el-row :gutter="20" class="pT20">
                <el-col :span="24">
                  <p class="fc-input-label-txt">Authentication</p>
                  <el-form-item prop="mailServer">
                    <el-select
                      v-model="supportEmail.authentication"
                      class="fc-input-full-border-select2 width100"
                      placeholder="Select Site"
                    >
                      <el-option label="Plain" value="1"></el-option>
                      <el-option label="MD5" value="2"></el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="20">
                <el-col :span="12">
                  <p class="fc-input-label-txt">User Name</p>
                  <el-form-item prop="userName">
                    <el-input
                      v-model="supportEmail.userName"
                      placeholder=""
                      class="fc-input-full-border-select2 width100"
                    ></el-input>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <div class="setup-input-block">
                    <p class="fc-input-label-txt">Password</p>
                    <el-form-item prop="password">
                      <el-input
                        v-model="supportEmail.password"
                        :disable="true"
                        placeholder=""
                        class="fc-input-full-border-select2 width100"
                      ></el-input>
                    </el-form-item>
                  </div>
                </el-col>
              </el-row>
            </div>
          </div>
        </el-row>
      </el-form>
    </div>
    <div class="modal-dialog-footer">
      <el-button @click="handleclose" class="modal-btn-cancel"
        >Cancel</el-button
      >
      <el-button
        type="primary"
        @click="saveSupportEmail('newSupportEmailForm')"
        class="modal-btn-save"
        >{{ saving ? 'saving...' : 'Save' }}</el-button
      >
    </div>
  </el-dialog>
</template>
<script>
import ErrorBanner from '@/ErrorBanner'
export default {
  props: ['supportEmailContext', 'visibility'],
  data() {
    return {
      saving: false,
      errorText: '',
      error: false,
      customMail: false,
      supportEmail: {
        replyName: '',
        actualEmail: '',
        autoAssignGroupId: '',
        fwdEmail: '',
        siteId: '',
        mailServer: 'imap.gmail.com',
        port: '993',
        authentication: '1',
        userName: null,
        password: null,
      },
      sites: [],
    }
  },
  created() {
    this.$store.dispatch('loadGroups')
  },
  mounted() {
    this.list = this.groups.map(group => {
      return { group }
    })
    this.initEditSupportEmail()
    this.$util.loadSpace(1).then(response => {
      this.sites = response.basespaces
    })
  },
  watch: {
    'supportEmail.actualEmail': function(newVal) {
      this.fwdMail(newVal)
    },
  },
  components: {
    ErrorBanner,
  },
  computed: {
    groups() {
      return this.$store.state.groups
    },
    isEditMode() {
      return this.supportEmailContext
    },
    isCustomMail() {
      return this.$route.query.showCustom
    },
  },
  methods: {
    update() {
      let self = this
      self.updating = true
    },
    handleclose() {
      this.$emit('update:visibility', false)
      this.reset()
    },
    reset() {
      this.supportEmail = {
        replyName: '',
        actualEmail: '',
        autoAssignGroupId: '',
        fwdEmail: '',
      }
    },
    close() {
      this.handleclose()
      this.reset()
    },

    initEditSupportEmail() {
      if (this.isEditMode) {
        this.$helpers.copy(this.supportEmail, this.supportEmailContext)
        this.replyName = this.supportEmailContext.replyName
        this.actualEmail = this.supportEmailContext.actualEmail
        this.autoAssignGroupId = this.supportEmailContext.autoAssignGroupId
        this.fwdEmail = this.supportEmailContext.fwdEmail
      }
    },

    fwdMail(actualEmail) {
      let orgEmailDomain = '@' + this.$account.org.domain + '.facilio.com'
      if (actualEmail.toLowerCase().endsWith(orgEmailDomain)) {
        this.supportEmail.fwdEmail = actualEmail
      } else {
        let emailSplit = actualEmail.toLowerCase().split('@')
        let domain = emailSplit[1] || ''
        this.supportEmail.fwdEmail =
          domain.replace(/\./g, '') + emailSplit[0] + orgEmailDomain
      }
    },
    validation(rule) {
      this.error = false
      this.errorText = ''
      if (!rule.replyName) {
        this.errorText = 'Please enter the Name'
        this.error = true
      } else if (!rule.siteId || rule.siteId <= 0) {
        this.errorText = 'Please select a site'
        this.error = true
      } else {
        this.errorText = ''
        this.error = false
      }
    },
    saveSupportEmail(newSupportEmailForm) {
      console.log('ruleForm')
      let self = this
      this.validation(self.supportEmail)
      if (this.error) {
        return
      }
      let url
      let data
      if (this.isEditMode) {
        url = '/setup/updateemailsettings'
        data = this.$helpers.compareObject(
          this.supportEmail,
          this.supportEmailContext
        )
        data.id = this.supportEmailContext.id
      } else {
        url = '/setup/addemailsettings'
        data = this.$helpers.cloneObject(this.supportEmail)
      }
      if (!data.autoAssignGroupId) {
        delete data.autoAssignGroupId
      }
      if (!data.siteId) {
        delete data.siteId
      }
      this.$http.post(url, { supportEmail: data }).then(response => {
        if (typeof response.data === 'object') {
          this.$emit('onsave')
          this.$message.success(
            this.isEditMode
              ? 'Support Email updated successfully!'
              : 'Email created successfully!'
          )
          this.handleclose()
        } else {
          this.$message.error(
            'Support Email ' +
              (this.isEditMode ? 'updation' : 'creation') +
              ' failed'
          )
        }
      })
    },
  },
}
</script>
<style>
.fc-support-dialog .el-dialog__header {
  display: none;
}
</style>
