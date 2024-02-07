<template>
  <div>
    <el-dialog
      :visible.sync="visibility"
      :fullscreen="true"
      :append-to-body="true"
      custom-class="fc-dialog-form fc-dialog-right setup-dialog40 setup-dialog"
      :before-close="closeDialog"
      style="z-index: 999999"
    >
      <el-form
        :model="sso"
        :label-position="'top'"
        ref="ssoForm"
        :rules="rules"
      >
        <div class="new-header-container">
          <div class="new-header-text">
            <div class="fc-setup-modal-title">
              {{ $t('setup.setup.configure_saml') }}
            </div>
          </div>
        </div>
        <div class="new-body-modal">
          <!-- <el-form-item label="IdP Entity ID" prop="entityId">
            <el-input
              type="text"
              v-model="sso.entityId"
              class="fc-input-full-border2"
              placeholder="Enter entity ID provided by the IDP"
            ></el-input>
          </el-form-item> -->
          <el-form-item
            :label="$t('setup.setup_profile.login_url')"
            prop="loginUrl"
          >
            <el-input
              type="text"
              v-model="sso.loginUrl"
              class="fc-input-full-border2"
              :placeholder="$t('setup.setup_profile.enter_idp')"
            ></el-input>
          </el-form-item>
          <el-form-item
            :label="$t('setup.setup_profile.saml_certificate')"
            prop="certificate"
          >
            <el-input
              type="textarea"
              v-model="sso.certificate"
              class="fc-input-full-border2"
              :autosize="{ minRows: 2, maxRows: 4 }"
              :placeholder="$t('setup.setup_profile.saml_security_certificate')"
            ></el-input>
          </el-form-item>
          <el-form-item
            v-if="ssoType === 'domainSSO'"
            :label="$t('setup.setup_profile.show_sso_link')"
          >
            <el-switch v-model="sso.showSSOLink"></el-switch>
          </el-form-item>
        </div>
        <div class="modal-dialog-footer">
          <el-button @click="closeDialog()" class="modal-btn-cancel">{{
            $t('common._common.cancel')
          }}</el-button>
          <el-button
            type="primary"
            @click="submitForm('ssoForm')"
            :loading="saving"
            class="modal-btn-save"
          >
            {{ saving ? 'Saving...' : $t('common._common._save') }}</el-button
          >
        </div>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
export default {
  props: [
    'entityId',
    'loginUrl',
    'logoutUrl',
    'certificate',
    'visibility',
    'getSSOStatus',
    'ssoType',
    'updateUrl',
    'showSSOLink',
  ],
  data() {
    return {
      saving: false,
      sso: {
        entityId: this.entityId,
        loginUrl: this.loginUrl,
        logoutUrl: this.logoutUrl,
        certificate: this.certificate,
        showSSOLink: this.showSSOLink,
      },
      rules: {
        loginUrl: [
          {
            required: true,
            message: 'Please input IdP Login URL',
            trigger: 'blur',
          },
        ],
        certificate: [
          {
            required: true,
            message: 'Please input SAML Certificate',
            trigger: 'blur',
          },
        ],
      },
    }
  },
  mounted() {},
  methods: {
    closeDialog() {
      console.log('entering')
      this.$emit('update:visibility', false)
    },
    cancel() {
      this.$emit('canceled')
    },
    submitForm(ssoForm) {
      this.$refs[ssoForm].validate(valid => {
        if (valid) {
          this.saving = true
          let {
            certificate,
            entityId,
            loginUrl,
            logoutUrl,
            showSSOLink,
          } = this.sso
          let ssoData = {}
          ssoData[this.ssoType] = {
            name: 'SAML',
            ssoTypeEnum: 'SAML',
            config: {
              entityId: entityId,
              loginUrl: loginUrl,
              certificate: certificate,
              logoutUrl: logoutUrl,
            },
            showSSOLink: showSSOLink,
            isActive: true,
          }
          this.$http.post(this.updateUrl, ssoData).then(response => {
            console.log(response)
            let { status } = response
            let { errorMessage } = response.data
            if (errorMessage) {
              this.$message.error(errorMessage)
              this.saving = false
            } else if (status === 200 && !errorMessage) {
              this.getSSOStatus('refresh')
              console.log('calling')
              console.log('updated')
              this.$message({
                type: 'success',
                message: 'SAML Configuration saved successfully!',
              })
              this.saving = false
              this.resetForm()
              this.$emit('SSO configs saved')
              this.$emit('update:visibility', false)
            }
          })
        }
      })
    },
    resetForm() {
      this.$refs['ssoForm'].resetFields()
    },
  },
}
</script>
<style scoped></style>
