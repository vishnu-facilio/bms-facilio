import { API } from '@facilio/api'
import axios from 'util/http'
import Vue from 'vue'
import Util from 'util/index'
import getProperty from 'dlv'
import { isNullOrUndefined, isEmpty } from '@facilio/utils/validation'
import Constants from 'util/constant'
import { mapState } from 'vuex'
import { initGoogleAnalytics } from 'src/track.js'

export default {
  data() {
    return {
      loading: true,
      account: null,
    }
  },
  mounted() {
    Vue.prototype.$servicePortal = true
    document.body.classList.add('fc-white-theme')
  },
  computed: {
    isValidPortal() {
      if (this.account && this.account.org && this.account.org.domain) {
        return true
      }
      return false
    },
    portalDomain() {
      if (this.account && this.account.org) {
        return this.account.org.domain
      }
      return document.domain
    },
    baseURL() {
      // Temporary hack till /service is completely removed
      return axios.defaults.baseURL
    },
    requiresAuth() {
      let requiresAuth = this.$route.matched.reduce((result, record) => {
        let required = record.meta.requiresAuth
        if (isNullOrUndefined(required)) {
          return result
        } else {
          return required
        }
      }, true)

      return requiresAuth
    },
    loginPath() {
      if (!this.$appDomain) {
        return ''
      }

      return { name: 'login' }
    },
  },
  methods: {
    loadPublicInfo() {
      let { baseURL } = this

      return API.get('/v2/v2fetchportalaccount', null, null, { baseURL }).then(
        ({ data, error }) => {
          if (error || !data.account) {
            this.$message.error(error.message || 'Error Occured')
          } else {
            Vue.prototype.$portalOrg = data.account.org
            Vue.prototype.$portalInfo = data.account.portalInfo
            Vue.prototype.$appDomain = data.account.appDomain
            Vue.prototype.$portaluser = null
            Vue.prototype.$isMFAEnabled = data.account.isMFAEnabled
            this.account = data.account

            let { org } = this.account
            let { language: orgLanguage } = org || {}
            let browserLanguage = window.navigator.language.split('-')[0]
            let haslanguage = Constants.languages
              .map(ln => ln.value)
              .includes(browserLanguage)

            if (!isEmpty(browserLanguage) && haslanguage) {
              this.$i18n.locale = browserLanguage
            } else if (!isEmpty(orgLanguage)) {
              this.$i18n.locale = orgLanguage
            } else {
              this.$i18n.locale = 'en'
            }

            return this.account
          }
        }
      )
    },

    loadAccount(force = false) {
      let { baseURL } = this

      return API.get('/v2/v2portalaccount', null, { force }, { baseURL }).then(
        ({ data, error }) => {
          if (error || !getProperty(data, 'account.user')) {
            // In this case we still need $account so we set the value from response
            // of v2fetchportalaccount
            let { account } = this || {}
            Vue.use(Util, {
              account: account || {},
            })

            this.handleAuthError()
          } else {
            let { account } = data
            this.$store.dispatch('setServicePortalAccount', account)

            Vue.prototype.$portaluser = account.user
            Vue.use(Util, {
              account,
            })

            let { user, org } = account
            let { language: orgLanguage } = org || {}
            let { language: userLanguage } = user || {}

            if (!isEmpty(userLanguage)) {
              this.$i18n.locale = userLanguage
            } else if (!isEmpty(orgLanguage)) {
              this.$i18n.locale = orgLanguage
            } else {
              this.$i18n.locale = 'en'
            }

            this.$store.commit('LOGIN_REQUIRED', false)
            this.account = account

            initGoogleAnalytics(account)

            return this.account
          }
        }
      )
    },

    async loadFeatures() {
      await this.$store.dispatch('getFeatureLicenses')
    },

    isPublicRequest() {
      let isSubmitReq = this.$route.path.includes('/submitrequest')
      let isPubicCreateAllowed = getProperty(
        this.account,
        'portalInfo.is_public_create_allowed'
      )
      return isSubmitReq && isPubicCreateAllowed
    },

    handleAuthError() {
      if (this.requiresAuth && !this.isPublicRequest()) {
        this.$router.replace({
          name: 'login',
        })
      }
    },

    logout() {
      if (
        window.identityServerURL &&
        window.identityServerURL.indexOf('http') >= 0
      ) {
        if (window.location.host == 'localhost') {
          let logoutURL = window.identityServerURL + '/identity/logout'
          logoutURL =
            logoutURL + '?redirect=' + encodeURIComponent(window.location.href)
          window.location.href = logoutURL
        } else {
          let logoutURL = '/identity/logout'
          logoutURL =
            logoutURL + '?redirect=' + encodeURIComponent(this.$route.path)
          window.location.href = logoutURL
        }
        return
      }
      API.get('/logout', null, { force: true }).then(({ error }) => {
        if (error) {
          console.error(error)
        } else {
          this.$store.commit('LOGIN_REQUIRED', true)
          this.$helpers.logout()
        }
      })
    },

    profiledp(profileview) {
      if (profileview === 'myprofile') {
        this.$router.push({
          path: '/service/myProfile',
        })
      } else if (profileview === 'logout') {
        this.logout()
      }
    },

    isLoggedIn() {
      return Vue.cookie.get('fc.loggedIn') === 'true'
    },
  },
}
