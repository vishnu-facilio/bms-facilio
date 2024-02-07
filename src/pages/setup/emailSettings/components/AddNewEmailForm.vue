<template>
  <div>
    <el-dialog
      :visible.sync="visibility"
      :fullscreen="true"
      :append-to-body="true"
      custom-class="fc-dialog-form fc-dialog-right setup-dialog40 setup-dialog"
      :before-close="() => closeDialog()"
      style="z-index: 999999"
    >
      <el-form
        :model="supportEmail"
        :rules="rules"
        :label-position="'top'"
        ref="emailForm"
      >
        <div class="new-header-container">
          <div class="new-header-text">
            <div class="fc-setup-modal-title">
              {{
                isNew ? $t('common._common._new') : $t('common._common.edit')
              }}
              {{ $t('setup.setup_profile.email') }}
            </div>
          </div>
        </div>
        <div class="new-body-modal">
          <el-form-item
            :label="$t('setup.approvalprocess.name')"
            prop="replyName"
          >
            <el-input
              type="text"
              v-model="supportEmail.replyName"
              class="fc-input-full-border2"
              :placeholder="$t('setup.emailSettings.enter_user_name')"
            ></el-input>
          </el-form-item>
          <el-form-item
            :label="$t('setup.setup_profile.email')"
            prop="actualEmail"
          >
            <el-input
              v-model="supportEmail.actualEmail"
              type="email"
              class="fc-input-full-border2"
              :placeholder="$t('setup.emailSettings.enter_email')"
              :disabled="!isNew"
            ></el-input>
          </el-form-item>
          <el-form-item
            :label="$t('setup.users_management.site')"
            prop="siteId"
          >
            <Lookup
              v-model="supportEmail.siteId"
              :field="fields.site"
              :hideLookupIcon="true"
              @recordSelected="setSelectedValue"
              @showLookupWizard="showLookupWizardSite"
            ></Lookup>
          </el-form-item>
          <el-form-item :label="$t('setup.emailSettings.custom')">
            <el-radio-group
              v-model="supportEmail.isCustomMail"
              @change="onCustomServerChange"
            >
              <el-radio :label="false" class="fc-radio-btn">{{
                $t('setup.emailSettings.default')
              }}</el-radio>
              <el-radio :label="true" class="fc-radio-btn">{{
                $t('setup.emailSettings.use_own_mail')
              }}</el-radio>
            </el-radio-group>
          </el-form-item>
          <div v-if="supportEmail.isCustomMail">
            <el-form-item
              :label="$t('setup.emailSettings.email_settings')"
              prop="emailSystem"
            >
              <el-radio-group
                v-model="supportEmail.emailSystem"
                @change="onCustomServerChange"
                size="small"
                class="fc-radio-custom-btn d-flex flex-direction-row"
              >
                <div class="mR40 position-relative">
                  <el-radio :label="'gmail'" border size="medium">
                    <div class="active-checkbox">
                      <i class="el-icon-check"></i>
                    </div>
                    <img
                      src="~assets/gmail.png"
                      style="width: 40px; height: 29px;"
                      class="mT5"
                    />
                  </el-radio>
                  <div class="label-txt-black text-center pT10">
                    {{ $t('setup.workordersettings.gmail') }}
                  </div>
                </div>
                <div class="mR40 position-relative">
                  <el-radio :label="'outLook'" border size="medium">
                    <div class="active-checkbox">
                      <i class="el-icon-check"></i>
                    </div>
                    <img
                      src="~assets/office-365.jpg"
                      style="width: 30px; height: 35px;"
                      class="mT5"
                    />
                  </el-radio>
                  <div class="label-txt-black text-center pT10 line-height20">
                    Microsoft
                    <br />Office 365
                  </div>
                </div>
                <div class="position-relative">
                  <el-radio :label="null" border size="medium">
                    <div class="active-checkbox">
                      <i class="el-icon-check"></i>
                    </div>
                    <img
                      src="~assets/other-email.svg"
                      width="28"
                      height="20"
                      class="mT10"
                    />
                  </el-radio>
                  <div class="label-txt-black text-center pT10">
                    {{ $t('setup.workordersettings.others') }}
                  </div>
                </div>
              </el-radio-group>
            </el-form-item>

            <div class="pT30">
              <div class="fc-text-pink12 text-uppercase bold">
                {{ $t('setup.workordersettings.incoming_email') }}
              </div>
            </div>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="Server Name" prop="mailServer">
                  <el-input
                    v-model="supportEmail.mailServer"
                    type="email"
                    class="fc-input-full-border2"
                    placeholder="Enter user email"
                  ></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="Port" prop="port">
                  <el-input
                    v-model="supportEmail.port"
                    type="email"
                    class="fc-input-full-border2"
                    placeholder="Enter user email"
                  ></el-input>
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item
              :label="$t('setup.emailSettings.authentication')"
              prop="mailServer"
            >
              <el-select
                v-model="supportEmail.authentication"
                class="fc-input-full-border-select2 width100"
                :placeholder="
                  $t('setup.emailSettings.select_authentication_type')
                "
              >
                <el-option label="Plain" :value="1"></el-option>
                <!-- <el-option label="MD5" :value="2"></el-option> -->
              </el-select>
            </el-form-item>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item :label="$t('setup.emailSettings.user_name')">
                  <el-input
                    v-model="supportEmail.userName"
                    class="fc-input-full-border2"
                    :placeholder="$t('setup.emailSettings.enter_email')"
                  ></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item
                  :label="$t('setup.emailSettings.password')"
                  v-if="!isNew && !editPassword"
                >
                  <el-input
                    v-model="dummyPassword"
                    :disabled="true"
                    class="fc-input-full-border2"
                    style="width:80%"
                    :placeholder="$t('setup.emailSettings.enter_password')"
                  ></el-input>
                  <el-tooltip
                    class="item"
                    effect="dark"
                    content="Edit Password"
                    placement="top"
                  >
                    <el-button
                      @click="editPassword = true"
                      type="text"
                      icon="el-icon-edit"
                      style="margin-left: 10px;"
                    ></el-button>
                  </el-tooltip>
                </el-form-item>
                <el-form-item
                  :label="$t('setup.emailSettings.password')"
                  v-else
                >
                  <el-input
                    v-model="supportEmail.password"
                    class="fc-input-full-border2"
                    show-password
                    :placeholder="$t('setup.emailSettings.enter_password')"
                  ></el-input>
                </el-form-item>
              </el-col>
            </el-row>
          </div>
          <div v-else>
            <el-form-item
              :label="$t('setup.emailSettings.forward_your_emails')"
              prop="fwdEmail"
            >
              <el-input
                v-model="supportEmail.fwdEmail"
                :disabled="true"
                :placeholder="$t('setup.emailSettings.support_forward_email')"
                class="fc-input-full-border-select2 width100"
              ></el-input>
            </el-form-item>
          </div>
        </div>
        <div class="modal-dialog-footer">
          <el-button @click="closeDialog()" class="modal-btn-cancel">{{
            $t('common._common.cancel')
          }}</el-button>
          <el-button
            type="primary"
            :loading="saving"
            class="modal-btn-save"
            @click="saveSupportEmail"
            >{{
              saving ? $t('common._common._saving') : $t('common._common._save')
            }}</el-button
          >
        </div>
      </el-form>
    </el-dialog>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import { Lookup } from '@facilio/ui/forms'
const imapService = {
  DEFAULT: 'DEFAULT',
  GMAIL: 'GMAIL',
  MICROSOFT_OFFICE_365: 'MICROSOFT_OFFICE_365',
  OTHERS: 'OTHERS',
}
const fields = {
  site: {
    isDataLoading: false,
    options: [],
    lookupModuleName: 'site',
    field: {
      lookupModule: {
        name: 'site',
        displayName: 'Sites',
      },
    },

    multiple: false,
    additionalParams: {
      orderBy: 'spaceType',
      orderType: 'asc',
    },
  },
}

export default {
  props: ['isNew', 'visibility', 'supportEmailObj'],
  data() {
    return {
      saving: false,
      fields,
      rules: {
        replyName: [
          {
            required: true,
            message: 'Please input name',
            trigger: 'blur',
          },
        ],
        actualEmail: [
          { required: true, message: 'Please input email address' },
          { type: 'email', message: 'Please input correct email address' },
        ],
        siteId: [
          { required: true, message: 'Please input site', trigger: 'blur' },
        ],
      },
      supportEmail: {
        replyName: '',
        actualEmail: '',
        fwdEmail: '',
        authentication: 1,
        isCustomMail: false,
        userName: null,
        password: null,
        mailServer: null,
        port: null,
        emailSystem: 'gmail',
        imapServiceProviderType: imapService.DEFAULT,
      },
      dummyPassword: '**********',
      editPassword: false,
      gmail: {
        mailServer: 'imap.gmail.com',
        port: '993',
        type: imapService.GMAIL,
      },
      outLook: {
        mailServer: 'outlook.office365.com',
        port: '993',
        type: imapService.MICROSOFT_OFFICE_365,
      },
    }
  },
  components: {
    Lookup,
  },
  mounted() {},
  watch: {
    'supportEmail.actualEmail': function(newVal) {
      this.fwdMail(newVal)
    },
    supportEmailObj: {
      handler(value, oldValue) {
        if (!isEmpty(value)) {
          let { id } = value
          let { id: oldId = null } = oldValue || {}
          if (id !== oldId) {
            this.supportEmail = {
              ...this.supportEmail,
              ...this.supportEmailObj,
            }
            this.editPassword = false
            this.onCustomServerChange()
          }
        }
      },
      immediate: true,
    },
  },
  methods: {
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    onCustomServerChange() {
      if (this.supportEmail.isCustomMail) {
        if (!isEmpty(this.supportEmail.emailSystem)) {
          this.supportEmail.mailServer = this[
            this.supportEmail.emailSystem
          ].mailServer
          this.supportEmail.port = this[this.supportEmail.emailSystem].port
          this.supportEmail.authentication = 1
          let { supportEmail } = this || {}
          let { emailSystem } = supportEmail || {}
          this.supportEmail.imapServiceProviderType = this[emailSystem].type
        } else {
          let { supportEmail } = this || {}
          supportEmail = {
            ...supportEmail,
            mailServer: null,
            port: null,
            imapServiceProviderType: imapService.OTHERS,
          }
          this.supportEmail = supportEmail
        }
      } else {
        let { supportEmail } = this || {}
        supportEmail = {
          ...supportEmail,
          isCustomMail: false,
          imapServiceProviderType: imapService.DEFAULT,
          mailServer: null,
          userName: null,
        }
        this.supportEmail = supportEmail
      }
    },
    fwdMail(actualEmail) {
      let orgEmailDomain =
        '@' + this.$account.org.domain + '.' + this.$account.config.mailDomain
      if (actualEmail && actualEmail.toLowerCase().endsWith(orgEmailDomain)) {
        this.supportEmail.fwdEmail = actualEmail
      } else {
        let emailSplit = actualEmail && actualEmail.toLowerCase().split('@')
        if (emailSplit) {
          let domain = emailSplit[1] || ''
          this.supportEmail.fwdEmail =
            domain.replace(/\./g, '') + emailSplit[0] + orgEmailDomain
        }
      }
    },
    showLookupWizardSite(field, canShow) {
      canShow = false
      this.$set(this, 'selectedLookupField', field)
      this.$set(this, 'canShowLookupWizardSite', canShow)
    },
    setSelectedValue(selectedValues, field) {
      selectedValues
      field
    },
    saveSupportEmail() {
      this.$refs['emailForm'].validate(valid => {
        if (valid) {
          this.saveMail()
        }
      })
    },
    saveMail() {
      let url
      let data
      if (!this.isNew) {
        this.saving = true
        url = '/setup/updateemailsettings'
        data = this.supportEmail
        data.id = this.supportEmail.id
      } else {
        this.saving = true
        url = '/setup/addemailsettings'
        data = this.$helpers.cloneObject(this.supportEmail)
      }

      API.post(url, { supportEmail: data }).then(({ error }) => {
        if (error) {
          this.$message.error(
            error.message || this.$t('common._common.error_occured')
          )
          this.saving = false
        } else {
          this.saving = false

          this.$emit('onsave')
          this.closeDialog()
          this.$dialog.notify(
            this.isNew
              ? this.$t('setup.emailSettings.email_added_msg')
              : this.$t('setup.emailSettings.email_updated_msg')
          )
        }
      })
    },
    resetForm() {
      if (this.$refs['emailForm']) {
        this.$refs['emailForm'].resetFields()
      }
    },
  },
}
</script>
