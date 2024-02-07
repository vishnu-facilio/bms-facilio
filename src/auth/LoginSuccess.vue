<template>
  <div class="saml-logout-page">
    <div class="fc-login-con">
      <div class="fc-login-align">
        <div class="fc-login-block">
          <div class="fc-saml-white-bg">
            <div class="text-center">
              <div>
                <img
                  v-if="!isCustomDomain"
                  src="~assets/facilio-blue-logo.svg"
                  style="height:25px;"
                />
                <img v-else :src="orgLogoUrl" style="height:25px;" />
              </div>
              <div class="fc-login-black20 pT10 pB5">
                Redirecting to Application…
              </div>
              <div class="fc-login-black18">
                Please click Continue if you’re not automatically redirected
              </div>
              <div
                class="fc-login-blue18 underline pT20 pointer"
                @click="continueLogin"
              >
                Continue
              </div>
            </div>
          </div>
        </div>
        <div v-if="!isCustomDomain" class="fc-copy-right pT30">
          Facilio Inc © 2022
        </div>
      </div>
      <div class="fc-login-footer">
        <div class="fc-login-footer-img">
          <img
            src="~assets/svgs/fc-login-footer-bg.svg"
            class="fc-login-footer-img"
          />
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import RebrandMixin from 'util/rebrandMixin'
export default {
  mounted() {
    let token = this.$cookie.get('fc.mobile.idToken.facilio')
    let scheme = this.$cookie.get('fc.mobile.scheme')
    this.loginUrl = `${scheme}://login?token=${token}`
    this.isTokenPresent = token
    if (token) {
      setTimeout(() => {
        window.location.href = this.loginUrl
        return false
      }, 50)
    }
  },
  mixins: [RebrandMixin],
  methods: {
    continueLogin() {
      if (this.isTokenPresent) {
        window.location.href = this.loginUrl
      }
    },
  },
  data() {
    return {
      isTokenPresent: false,
      loginUrl: '',
    }
  },
}
</script>
