<template>
  <div class="saml-logout-page">
    <div class="fc-login-con">
      <div class="fc-login-align">
        <div class="fc-login-block">
          <div class="fc-saml-white-bg">
            <div class="text-center">
              <div>
                <img src="~assets/facilio-blue-logo.svg" style="height:25px;" />
              </div>
              <div class="fc-login-black20 pT10 pB5">
                You’ve logged out successfully
              </div>
              <div class="fc-login-black18">
                However make sure you’ve logged out from your IDP too.
              </div>
              <div
                class="fc-login-blue18 underline pT20 pointer"
                @click="redirectToSSO"
              >
                Sign in again
              </div>
            </div>
          </div>
        </div>
        <div class="fc-copy-right pT30">
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
export default {
  data() {
    return {
      isMobile: false,
    }
  },
  mounted() {
    this.isMobileView = window.location.pathname.includes('/mobile/entry')
    if (this.isMobileView) {
      this.$cookie.delete('fc.mobile.idToken.facilio')
      let date = new Date()
      date.setTime(date.getTime() + 2 * 3600 * 1000)
      this.$cookie.set('fc.isWebView', 'true', {
        expires: date,
        path: '/',
      })
    }
  },
  methods: {
    appendRelayToSSOEndPoint(ssoEndPoint) {
      if (this.isMobileView) {
        let url = new URL(ssoEndPoint)
        url.searchParams.append('relay', 'mobile')
        ssoEndPoint = url.href
      }
      return ssoEndPoint
    },
    redirectToSSO() {
      if (window.domainInfo && window.domainInfo.isSSOEnabled) {
        window.location.href = this.appendRelayToSSOEndPoint(
          window.domainInfo.ssoEndPoint
        )
      }
    },
  },
}
</script>
