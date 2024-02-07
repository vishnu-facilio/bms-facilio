<template>
  <div>
    <div class="d-flex flex-direction-row flex-wrap">
      <div>
        <div class="fc-black-color f16 bold letter-spacing0_5 text-capitalize">
          {{ $t('common.wo_report.time_based_otp') }}
        </div>
        <div class="fc-grey-dark pT10 line-height20 f13 pT5">
          <div v-if="type == 1">
            {{ $t('common.wo_report.time_based_otp_button') }}
          </div>
          <div v-else-if="type == 2">
            {{ $t('common.wo_report.google_authenticator') }}
            <i
              class="el-icon-delete pointer mL25"
              @click="deleteConfiguration()"
            >
            </i>
          </div>
        </div>
        <el-button
          class="setup-el-btn2 mT40 bold"
          @click="TimeBasedOTP()"
          style="height: 32px !important; line-height: 0px"
        >
          <div v-if="type == 1">
            {{ $t('common.header.setup_now') }}
          </div>
          <div v-else-if="type == 2">
            {{ $t('common.profile.change_configuration') }}
          </div>
        </el-button>
      </div>
      <el-dialog
        :visible.sync="formVisibilityTotp"
        :append-to-body="true"
        :before-close="closeForm"
        :title="'TIME BASED OTP'"
        custom-class="fc-dialog-center-container f-webform-right-dialog f-webform-center-dialog"
      >
        <div class="height350">
          <el-form :label-position="'top'">
            <div class="label-txt-black break-word line-height20">
              {{ $t('common.wo_report.scan_qr_code_authenticator_app') }}
            </div>
            <div class="text-center fc-mfa-qrcode">
              <img v-bind:src="QRCode" />
            </div>
          </el-form>
        </div>
        <div class="modal-dialog-footer">
          <el-button
            class="modal-btn-cancel"
            @click="closeForm()"
            type="primary"
            >{{ $t('common._common.cancel') }}</el-button
          >
          <el-button
            :loading="isSaving"
            class="modal-btn-save"
            type="primary"
            @click="nextpage()"
            >{{ $t('common._common.next') }}</el-button
          >
        </div>
      </el-dialog>
    </div>
    <verifyCode
      v-if="formvisibility"
      :visibility.sync="formvisibility"
      :type="formtype"
      :isLoginSetup="isLoginSetup"
      :mfaConfigToken="mfaConfigToken"
      :lookUpType="lookUpType"
      @closeForm="closeForm"
      @totpconfigured="totpconfigured"
    >
    </verifyCode>
  </div>
</template>
<script>
import http from 'util/http'
import verifyCode from './verificationCode'
export default {
  components: {
    verifyCode,
  },
  props: ['isLoginSetup', 'mfaConfigToken', 'lookUpType'],
  data() {
    return {
      formvisibility: false,
      formtype: '1',
      QRCode: '',
      SecretKey: '',
      formVisibilityTotp: false,
      type: '1',
    }
  },
  created() {
    this.loadTotpStatus()
  },
  beforeUpdate() {
    this.loadTotpStatus()
  },
  methods: {
    totpconfigured() {
      this.$emit('totpconfigured')
    },
    closeForm() {
      this.formVisibilityTotp = false
      this.formvisibility = false
    },
    nextpage() {
      this.formVisibilityTotp = false
      this.formvisibility = true
    },
    async loadTotpStatus() {
      if (!this.isLoginSetup) {
        let resp = await this.$http.get('v2/settings/totpstatus')
        this.type = resp.data.result.totpstatus == false ? 1 : 2
      } else {
        this.type = 1
      }
    },
    TimeBasedOTP() {
      if (!this.isLoginSetup) {
        this.loadQRCode()
        this.formVisibilityTotp = true
      } else {
        this.loadQRCodeUsingToken()
        this.formVisibilityTotp = true
      }
    },
    async loadQRCodeUsingToken() {
      let url = `integ/getMfaSettingsUsingDigest?mfaConfigToken=${this.mfaConfigToken}`
      if (this.lookUpType) {
        url += `&lookUpType=${this.lookUpType}`
      }
      let resp = await http.get(url)
      this.QRCode = resp.data.jsonresponse.qrCode
      this.SecretKey = resp.data.jsonresponse.totpSecret
    },
    async loadQRCode() {
      if (this.type == '1') {
        let resp = await this.$http.get('v2/settings/totpVerification')
        this.QRCode = resp.data.result.totpData.qrCode
        this.SecretKey = resp.data.result.totpData.secret
      } else if (this.type == '2') {
        let resp = await this.$http.get('v2/settings/totpchange')
        this.QRCode = resp.data.result.totpData.qrCode
        this.SecretKey = resp.data.result.totpData.secret
      }
    },
    async deleteConfiguration() {
      await this.$http.post('v2/settings/totpexit', {})
      await this.loadTotpStatus()
      this.$message.success(this.$t('common.wo_report.totp_settings_removed'))
    },
    changeConfiguration() {
      this.formVisibilityTotp = true
    },
  },
}
</script>
