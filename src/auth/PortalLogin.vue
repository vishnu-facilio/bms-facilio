<script>
import Vue from 'vue'
import FacilioLogin from './FacilioLogin'
import { API } from '@facilio/api'
import homeMixin from 'src/PortalTenant/auth/portalHomeMixin'

export default {
  mixins: [homeMixin],
  extends: FacilioLogin,
  created() {
    this.ssoLoading = true
    this.loadPublicInfo().finally(() => (this.ssoLoading = false))
    this.validateLogin()
  },
  methods: {
    validateLogin() {
      let isLoggedIn = Vue.cookie.get('fc.loggedIn') === 'true'
      if (isLoggedIn) {
        this.onLogin()
      }
    },
    async loginUserNamePwd() {
      let logindata = {
        username: this.username,
        password: this.pass,
      }

      let options = {
        headers: {
          'X-Device-Type': 'web',
        },
      }

      if (this.isMobileView) {
        options.headers['X-Device-Type'] = 'webview'
      }

      this.logging_in = true

      await this.appModelLogin(logindata)

      this.logging_in = false
    },
    async appModelLogin(credentials) {
      let { error, data } = await API.post('integ/faciliosubmit', credentials)
      if (
        !error &&
        (data.jsonresponse.username || data.jsonresponse.totpToken)
      ) {
        if (data.jsonresponse.isOrgTotpEnabled) {
          if (data.jsonresponse.isMFASetupRequired) {
            this.$router.push({
              path: '/auth/mfasetup',
              query: {
                mfaConfigToken: data.jsonresponse.mfaConfigToken,
              },
            })
          } else {
            this.totpToken = data.jsonresponse.totpToken
          }
        } else if (data.jsonresponse.pwdPolicyResetToken) {
          let pwdPolicyResetToken =
            response.data.jsonresponse.pwdPolicyResetToken
          this.$router.push({
            name: 'confirmpassword',
            params: { invitetoken: pwdPolicyResetToken },
            query: { isPasswordExpired: true },
          })
        } else {
          await this.onLogin()
        }
      } else {
        this.error = {
          message: this.$t('common._common.invalid_username_password'),
        }
      }
    },
    async onLogin() {
      this.$store.commit('LOGIN_REQUIRED', false)
      const { href } =
        this.$router.resolve({ path: this.$route.query.redirect || '/' }) || {}
      // Can't use route.push or repace here because we need the store to be cleared
      window.location.href = href
    },
  },
}
</script>
