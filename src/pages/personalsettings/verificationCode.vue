<template>
  <div>
    <el-dialog
      :visible.sync="visibility"
      :append-to-body="true"
      :before-close="closeForm"
      :title="type == 1 ? 'VERIFY YOUR DEVICE' : 'USE MOBILE NUMBER'"
      width="35%"
      custom-class="fc-dialog-center-container f-webform-right-dialog f-webform-center-dialog"
    >
      <div v-if="type == 1" class="height200">
        <div class="mL10 mT10">
          <div class="profile-input-group">
            <p class="fc-grey7-12 f14 text-left">
              Enter Verification Code
            </p>
            <el-input
              v-model="value"
              type="password"
              class="fc-input-full-border2"
              autocomplete="new-password"
            />
          </div>
        </div>
      </div>
      <div v-if="type == 2" class="height200">
        <div class="mL10 mT10">
          <div class="profile-input-group">
            <p class="input-label-text">
              USE MOBILE NUMBER
            </p>
            <el-input v-model="value" />
          </div>
        </div>
      </div>
      <div class="modal-dialog-footer">
        <el-button class="modal-btn-cancel" @click="closeForm()" type="primary"
          >CANCEL</el-button
        >
        <el-button
          :loading="isSaving"
          class="modal-btn-save"
          type="primary"
          @click="verify()"
          >VERIFY</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>
<script>
import http from 'util/http'
import RebrandMixin from 'util/rebrandMixin'
export default {
  props: [
    'visibility',
    'type',
    'isLoginSetup',
    'mfaConfigToken',
    'totp',
    'lookUpType',
  ],
  mixins: [RebrandMixin],
  data() {
    return {
      value: '',
    }
  },
  methods: {
    closeForm() {
      this.$emit('closeForm')
    },
    async verify() {
      if (this.isLoginSetup) {
        let data = {
          mfaConfigToken: this.mfaConfigToken,
          totp: this.value,
          lookUpType: this.lookUpType,
        }
        let options = {
          headers: {
            'X-Device-Type': 'web',
          },
        }

        if (this.isWebView) {
          options.headers['X-Device-Type'] = 'webview'
        }
        let resp = await http.post(
          `integ/configureMFAUsingDigest`,
          data,
          options
        )
        if (resp.data.jsonresponse.pwdPolicyResetToken) {
          this.$router.push({
            name: 'confirmpassword',
            params: { invitetoken: resp.data.jsonresponse.pwdPolicyResetToken },
            query: {
              isPasswordExpired: true,
              lookUpType: this.$route.query.lookUpType,
            },
          })
        } else if (resp.data.jsonresponse.username) {
          this.closeForm()
          this.$message.success('TOTP Successfully enabled.')
          this.$emit('totpconfigured')
        } else {
          this.$message.error('Invalid verification code.')
        }
      } else {
        let resp = await this.$http.post('v2/settings/totpsetup', {
          verificationCode: this.value,
        })
        if (resp.data.responseCode === 0) {
          this.closeForm()
          this.$message.success('MFA Successfully enabled')
        } else {
          this.$message.error('Error setting up TOTP')
        }
      }
    },
  },
}
</script>
