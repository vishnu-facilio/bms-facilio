<template>
  <el-dialog
    :visible="true"
    :before-close="closeForm"
    title="Enter Invite Code"
    width="31%"
    :append-to-body="true"
    custom-class="fc-dialog-center-container scale-up-center"
  >
    <div class="height180">
      <template v-if="loading">
        <spinner :show="loading" size="80"></spinner>
        <div class="c-grey2-text12 line-height20 text-center ">
          Verifying the invite code
        </div>
      </template>
      <template v-else>
        <div class="c-grey2-text12 line-height20 text-center mB20">
          Enter the Invite Code sent with your visit invitation.
        </div>
        <OtpInput
          class="justify-content-center"
          inputClasses="otp-input-box"
          :numInputs="4"
          separator=" "
          :shouldAutoFocus="true"
          :is-input-num="true"
          @on-complete="submitOtp"
        ></OtpInput>
      </template>
    </div>
  </el-dialog>
</template>
<script>
import OtpInput from '@bachdgvn/vue-otp-input'
import { API } from '@facilio/api'

export default {
  components: { OtpInput },
  data() {
    return {
      loading: false,
    }
  },
  methods: {
    submitOtp(otp) {
      let params = {
        passCode: otp,
        moduleName: 'invitevisitor',
      }

      this.loading = true
      API.get('v3/modules/data/summary', params).then(({ data, error }) => {
        if (error) {
          this.$message.error(error.message || 'Error Occured')
          this.closeForm()
        } else {
          let { invitevisitor } = data
          this.$emit('submitOtp', invitevisitor)
        }
      })
    },
    closeForm() {
      this.$emit('onClose')
    },
  },
}
</script>
