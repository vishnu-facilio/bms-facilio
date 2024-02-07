<template>
  <div class="fc-mfaSetup-login-page fc-login-con">
    <div class="fc-login-align">
      <div class="text-center pB40">
        <img src="~assets/facilio-blue-logo.svg" style="height:25px;" />
      </div>
      <div class="fc-login-block">
        <div class="fc-black3-20 fwBold text-center pB30">
          {{ $t('common._common.multi_factor_auth') }}
        </div>
        <TimeOTP
          @totpconfigured="totpconfigured"
          :isLoginSetup="true"
          :mfaConfigToken="$route.query.mfaConfigToken"
          :lookUpType="$route.query.lookUpType"
          class="fc-border4 p30"
        >
        </TimeOTP>

        <div class="fc-id f14 pT20 pB20 text-center">
          <a
            href="mailto:support@facilio.com?subject=Need assistance Signing in"
          >
            {{ $t('common._common.need_assistance') }}
          </a>
        </div>
      </div>
    </div>
    <div class="fc-grey7-12 f16 text-center pT20">
      Facilio Inc © 2022
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
</template>
<script>
import TimeOTP from 'pages/personalsettings/mfatimebasedotp'
import RebrandMixin from 'util/rebrandMixin'

export default {
  components: {
    TimeOTP,
  },
  data() {
    return {}
  },
  mixins: [RebrandMixin],
  methods: {
    totpconfigured() {
      this.handleLoginSuccess()
    },
    handleLoginSuccess() {
      this.$store.commit('LOGIN_REQUIRED', false)
      this.isMobileView = this.isWebView || this.isMobilePortal
      if (this.isMobileView || this.isMobilePortal) {
        this.$router.replace('/auth/loginsuccess')
      } else {
        const { href } =
          this.$router.resolve({
            path: this.$route.query.redirect || '/',
          }) || {}
        // Can't use route.push or repace here because we need the store to be cleared
        window.location.href = href
      }
    },
  },
}
</script>
<style lang="scss">
.fc-mfaSetup-login-page {
  .fc-login-block {
    width: 870px;
    max-width: 870px;
    padding-left: 115px;
    padding-right: 115px;
  }
}
</style>
