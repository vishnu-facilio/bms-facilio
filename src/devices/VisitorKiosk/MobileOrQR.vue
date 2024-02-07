<template>
  <div
    :class="[
      'kiosk',
      'auth page',
      'fc-kiosk-form',
      { 'kiosk-mobile-bg': !isInputOpen },
    ]"
  >
    <!-- kiosk auth   page mobile or qr -->
    <div class="fc-kiosk-form-con">
      <div class="fc-kiosk-form position-relative">
        <!-- SHOW HEADER ONLY WHEN ANY OF THE INPUT IS SELECTED -->
        <div
          :class="[
            'fc-kiosk-form-header',
            { 'kiosk-mobile-qr-header': !isInputOpen },
          ]"
        >
          <div class="fc-kiosk-form-header-icon" @click="handleBack">
            <img src="~assets/kiosk-back-arrow.svg" width="16" height="16" />
          </div>
          <div class="fc-kiosk-f18">{{ currentAccount.org.name }}</div>
        </div>

        <div class="fc-kiosk-in-out-con" v-if="!isInputOpen">
          <div
            class="fc-checkin-mode fc-kiosk-in-out-bg fc-animated slideInUp"
            @click="mobileClicked"
          >
            <img src="~assets/kiosk-mobile.svg" alt="" title="" />
            <div class="fc-kiosk-16 pT10 bold">MOBILE</div>
          </div>
          <div class="fc-checkin-mode qr-options">
            <div
              class="fc-kiosk-in-out-bg mL40 fc-animated slideInUp"
              @click="isQrInput = true"
            >
              <img src="~assets/kiosk-qr.svg" class="pT10" alt="" title="" />
              <div class="fc-kiosk-16 pT20 bold">SCAN QR</div>
            </div>
          </div>
          <div class="fc-checkin-mode qr-options" v-if="action !== 'checkout'">
            <div
              class="fc-kiosk-in-out-bg mL40 fc-animated slideInUp"
              @click="isPassCodeOpen = true"
            >
              <img
                src="~assets/pin-code.svg"
                style="height: 70px;"
                alt=""
                title=""
              />
              <div class="fc-kiosk-16 pT10 bold">INVITE CODE</div>
            </div>
          </div>
        </div>

        <!-- QR input selected -->
        <div
          class="kiosk-photo-capture-box d-flex flex-direction-column "
          v-if="isQrInput"
        >
          <div class="qr-cam-title mB20 bold">Scan QR</div>
          <p v-if="error" class="error">{{ error }}</p>

          <qrcode-stream :camera="camera" @decode="onDecode" @init="onInit" />
        </div>

        <!-- mobile input selected -->
        <div class="fc-kiosk-form-con" v-if="isMobileInput">
          <div class="position-relative fc-scale-in-center">
            <el-form @submit.native.prevent="mobileNumberEntered">
              <el-form-item>
                <div class="fc-kiosk-form-label">
                  Enter mobile number
                </div>

                <div class="fc-kiosk-form-input fc-kiosk-form-input-mobile">
                  <el-input
                    v-model="mobile"
                    class="fc-kiosk-input mT10"
                    inputmode="tel"
                    autofocus
                    ref="formInp"
                  >
                  </el-input>

                  <el-button
                    class="fc-kiosk-input-next"
                    @click="mobileNumberEntered"
                  >
                    <img
                      src="~assets/kiosk-forward.svg"
                      height="45"
                      width="45"
                    />
                  </el-button>
                </div>
              </el-form-item>
            </el-form>
          </div>
        </div>

        <div class="d-flex flex-col align-center" v-if="isPassCodeOpen">
          <div class="fc-check-in-f22 pT10 pB20">Enter Invite Code</div>
          <div class="position-relative fc-scale-in-center">
            <vie-otp-input
              inputClasses="otp-input"
              :numInputs="4"
              separator="-"
              :shouldAutoFocus="true"
              :is-input-num="true"
              @on-complete="passCodeEntered"
            />
          </div>
        </div>
      </div>
    </div>

    <portal to="kiosk-footer-logo">
      <img
        v-if="isInputOpen"
        src="~assets/facilio-logo-black.svg"
        style="width: 70px; height: 20px;"
      />
      <img
        v-else
        src="~assets/facilio-logo-white.svg"
        style="width: 70px; height: 20px;"
      />
    </portal>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import VieOtpInput from '@bachdgvn/vue-otp-input'
import { QrcodeStream } from 'vue-qrcode-reader'

export default {
  props: ['action'],
  data() {
    return {
      isMobileInput: false,
      isQrInput: false,
      result: '', //qr code
      mobile: null,
      error: '',
      camera: 'front',
      isPassCodeOpen: false,
    }
  },
  components: {
    'qrcode-stream': QrcodeStream,
    VieOtpInput,
  },
  computed: {
    ...mapGetters(['currentAccount']),
    isInputOpen() {
      return this.isMobileInput || this.isQrInput || this.isPassCodeOpen
    },
  },
  methods: {
    switchCamera() {
      if (this.camera == 'front') {
        this.camera = 'back'
      } else if (this.camera == 'back') {
        this.camera = 'front'
      }
    },
    passCodeEntered(e) {
      this.$emit('authDone', { type: 'qr', qr: 'passcode_' + e })
    },

    handleBack() {
      if (this.isInputOpen) {
        this.isMobileInput = false
        this.isQrInput = false
        this.isPassCodeOpen = false
      } else {
        this.$router.replace({ name: 'welcome' })
      }
    },
    mobileClicked() {
      this.isMobileInput = true
      this.$nextTick(() => {
        this.$refs['formInp'].focus()
      })
    },
    mobileNumberEntered() {
      this.$emit('authDone', { type: 'mobile', mobile: this.mobile })
    },
    onDecode(result) {
      if (result && result.trim()) {
        this.result = result
        this.$emit('authDone', { type: 'qr', qr: this.result })
      } else {
        this.$message({
          message: 'Please position QR code in frame correctly',
          type: 'info',
        })
      }
    },
    async onInit(promise) {
      try {
        await promise
      } catch (error) {
        if (error.name === 'NotAllowedError') {
          this.error = 'ERROR: you need to grant camera access permisson'
        } else if (error.name === 'NotFoundError') {
          this.error = 'ERROR: no camera on this device'
        } else if (error.name === 'NotSupportedError') {
          this.error = 'ERROR: secure context required (HTTPS, localhost)'
        } else if (error.name === 'NotReadableError') {
          this.error = 'ERROR: is the camera already in use?'
        } else if (error.name === 'OverconstrainedError') {
          this.camera = 'auto'
        } else if (error.name === 'StreamApiNotSupportedError') {
          this.error = 'ERROR: Stream API is not supported in this browser'
        }
      }
    },
  },
}
</script>

<style lang="scss">
.error {
  font-weight: bold;
  color: red;
}
.mobile-or-qr-option {
  margin-top: 20px;
}
.kiosk-mobile-bg {
  background: #46377d;
}
.kiosk-mobile-qr-header {
  background: #3e2f73;
  border: 1px solid #3e2f73;
  .fc-kiosk-f18 {
    color: #ffffff;
  }
}
.kiosk-passcode {
  margin: auto;
  margin-left: 80px;
  margin-bottom: 10px;
  color: white;
  font-size: 16px;
  text-decoration: underline;
}
.otp-input {
  width: 50px;
  height: 50px;
  padding: 5px;
  margin: 0 10px;
  font-size: 20px !important;
  border-radius: 4px;
  border: 1px solid rgba(0, 0, 0, 0.3);
  text-align: center;
}
.qr-cam-title {
  font-size: 22px;
  color: black;
}
</style>
