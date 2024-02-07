<template>
  <div class="fc-login-con confirm-password-page">
    <loader v-if="loading"></loader>
    <div v-else class="fc-login-align" :class="{ mT0: isWebServicePortal }">
      <portal-header v-if="isWebServicePortal"></portal-header>
      <div class="fc-login-block" :class="{ mT50: isWebServicePortal }">
        <div
          v-if="!isWebServicePortal"
          class="fc-login-logo-block"
          aria-hidden="true"
        >
          <img
            v-if="brandName == 'BuildingsTalk'"
            src="~assets/buildingstalklogo.png"
            style="height:60px;"
            :alt="brandName"
          />
          <img
            v-else-if="brandName == 'Sutherland'"
            src="~assets/sutherland.png"
            style="height:60px;"
            :alt="brandName"
          />
          <img
            class="fc-custom-logo fc-moro-logo"
            v-else-if="brandName == 'Moro'"
            src="~assets/facilio-moro.png"
            :alt="brandName"
          />
          <img
            v-else-if="orgLogoUrl"
            :src="orgLogoUrl"
            style="height:60px;"
            alt="Facilio"
          />
          <img
            v-else
            src="~assets/facilio-blue-logo.svg"
            style="height:25px;"
            :alt="brandName"
          />
        </div>
        <div class="fc-login-txt pT15">
          Password Confirmation
        </div>
        <div class="alert alert-danger" v-if="error" role="alert">
          <div v-if="error" class="error text-center pT10 f14 nowrap">
            {{ error.message }}
          </div>
        </div>
        <div v-if="isPasswordExpired">
          <div class="pT10 f14 text-center" style="color: red;">
            Your password has expired!
          </div>
          <div class="pT5 f14 text-center" style="color: red;">
            Please reset the password to a new one.
          </div>
        </div>

        <form
          @submit.prevent="passwordResetConfirm"
          class="fc-form-slide-in-left fc-animate-login-form position-relative"
        >
          <div class="form-group position-relative pT20">
            <el-input
              class="fc-login-input"
              placeholder="Enter new password"
              aria-label="Enter new password"
              ref="newPassword"
              v-model="pass"
              required
              autocomplete="new-password"
              :type="showVisibilityIcon"
            />
            <div v-if="showVisibilityIcon == 'text'" @click="hidePassword">
              <InlineSvg
                src="svgs/eye-visible"
                class="fc-login-icons pointer"
                iconClass="icon icon-md fill-lite-grey"
              ></InlineSvg>
            </div>

            <div v-if="showVisibilityIcon == 'password'" @click="showPassword">
              <InlineSvg
                src="svgs/not-visible"
                class="fc-login-icons pointer"
                iconClass="icon icon-md fill-lite-grey"
              ></InlineSvg>
            </div>
          </div>

          <div class="position-relative pT20">
            <el-input
              :type="showVisibilityConfirmIcon"
              class="fc-login-input"
              placeholder="Confirm password"
              aria-label="Confirm password"
              v-model="confrimpass"
              required
              :autocomplete="'off'"
            />
            <div
              v-if="showVisibilityConfirmIcon == 'text'"
              @click="hideConfirmPassword"
            >
              <InlineSvg
                src="svgs/eye-visible"
                class="fc-login-icons pointer"
                iconClass="icon icon-md fill-lite-grey"
                style="top: 35px !important;"
              ></InlineSvg>
            </div>

            <div
              v-if="showVisibilityConfirmIcon == 'password'"
              @click="showConfirmPassword"
            >
              <InlineSvg
                src="svgs/not-visible"
                class="fc-login-icons pointer"
                iconClass="icon icon-md fill-lite-grey"
                style="top: 35px !important;"
              ></InlineSvg>
            </div>
            <div class="pT20 f12" style="color: red;" v-if="notSamePasswords">
              Password doesn't match
            </div>
          </div>
          <button
            class="btn fc-login-btn fc-login-btn-disabled mT20"
            v-if="!saving && pass === confrimpass"
          >
            Confirm Password Reset
          </button>
          <button
            class="btn fc-login-btn mT20 fc-login-btn-disabled"
            disabled
            v-else
          >
            Confirm Password Reset
          </button>
        </form>
      </div>
      <div v-if="!isCustomDomain" class="fc-copy-right pT30">
        {{ rebrandInfo.copyright.name }} © {{ rebrandInfo.copyright.year }}
      </div>
      <portal-footer
        v-if="isWebServicePortal && !isCustomDomain"
      ></portal-footer>
      <div class="fc-login-footer">
        <div class="fc-login-footer-img">
          <img
            src="~assets/svgs/fc-login-footer-bg.svg"
            class="fc-login-footer-img"
            aria-hidden="true"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import http from 'util/http'
import RebrandMixin from 'util/rebrandMixin.js'
import PortalHeader from 'src/PortalTenant/auth/PortalHeader'
import PortalFooter from 'src/PortalTenant/auth/PortalFooter'
import Loader from '@/Loader'

export default {
  components: { PortalHeader, PortalFooter, Loader },
  mixins: [RebrandMixin],
  data() {
    return {
      loading: false,
      saving: false,
      code: '',
      pass: null,
      confrimpass: null,
      error: null,
      brandName: '',
      isMobileView: false,
      showVisibilityIcon: 'password',
      showVisibilityConfirmIcon: 'password',
      isPasswordExpired: '',
    }
  },
  created() {
    let { location } = window

    this.isMobileView = this.isWebView
  },
  computed: {
    notSamePasswords() {
      return this.confrimpass && this.pass !== this.confrimpass
    },
  },
  mounted() {
    if (
      window.rebrandInfo &&
      window.rebrandInfo.brandName === 'BuildingsTalk'
    ) {
      // alert(this.brandName)
      this.brandName = window.rebrandInfo.brandName
      // alert(this.brandName)
    } else {
      this.brandName = window.rebrandInfo.brandName
    }
    this.isPasswordExpired = this.$route.query.isPasswordExpired

    this.setAutofocus('newPassword')
  },
  methods: {
    handleLoginSuccess() {
      this.$store.commit('LOGIN_REQUIRED', false)
      if (this.isMobileView || this.isMobilePortal) {
        this.$router.replace('/auth/loginsuccess')
      } else {
        const { href } =
          this.$router.resolve({ path: this.$route.query.redirect || '/' }) ||
          {}
        // Can't use route.push or repace here because we need the store to be cleared
        window.location.href = href
      }
    },
    routeToLogin() {
      this.$router.replace({
        name: 'login',
      })
    },
    showPassword() {
      this.showVisibilityIcon = 'text'
    },
    hidePassword() {
      this.showVisibilityIcon = 'password'
    },
    showConfirmPassword() {
      this.showVisibilityConfirmIcon = 'text'
    },
    hideConfirmPassword() {
      this.showVisibilityConfirmIcon = 'password'
    },
    passwordResetConfirm() {
      this.saving = true
      let formData

      if (this.isPasswordExpired) {
        formData = {
          digest: this.$route.params.invitetoken,
          rawPassword: this.pass,
        }
      } else {
        formData = {
          inviteToken: this.$route.params.invitetoken,
          rawPassword: this.pass,
        }
      }

      let url = 'fresetPassword'
      if (this.isPasswordExpired) {
        url = 'resetExpiredPassword'
      }

      if (this.$route.query.lookUpType) {
        formData.lookUpType = this.$route.query.lookUpType
      } else if (this.isMobileServicePortal) {
        formData.lookUpType = 'service'
      } else if (this.isMobileTenantPortal) {
        formData.lookUpType = 'tenant'
      } else if (this.isMobileVendorPortal) {
        formData.lookUpType = 'vendor'
      } else if (this.isMobileWorkQ) {
        formData.lookUpType = 'workQ'
      }

      let options = {
        headers: {
          'X-Device-Type': 'web',
        },
      }

      if (this.isMobileView) {
        options.headers['X-Device-Type'] = 'webview'
      }

      http
        .post(url, formData, options)
        .then(resp => {
          if (
            resp.data &&
            resp.data.jsonresponse &&
            resp.data.jsonresponse.status === 'failure'
          ) {
            this.$message.error(resp.data.jsonresponse.message)
            this.saving = false
          } else {
            if (this.isPasswordExpired) {
              this.handleLoginSuccess()
            } else {
              this.routeToLogin()
            }
          }
        })
        .catch(error => {
          this.$message.error(error.data.message)
          this.saving = false
        })
    },
  },
}
</script>
<style lang="scss">
body {
  background-color: #f8f8fb;
}
.fc-login-input-confirm {
  width: 100%;
  height: 45px;
  padding-left: 16px !important;
  padding-right: 16px !important;
  border-radius: 5px !important;
  box-shadow: 0 0 4px 0 #e1d6ff !important;
  border: solid 1px #b4d0ff;
  background-color: #ffffff !important;
  box-shadow: 0 0 4px 0 #e1d6ff !important;
  transition: border-color ease-in-out 0.15s, box-shadow ease-in-out 0.15s;
  color: #19182e;
  font-size: 13px;
  letter-spacing: 0.5px;
}
.confirm-password-page {
  .fc-login-icons {
    top: 42px !important;
  }
  .fc-login-btn-disabled:disabled {
    opacity: 1 !important;
  }
}
</style>
