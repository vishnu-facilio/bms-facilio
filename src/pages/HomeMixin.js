import Vue from 'vue'
import Util from '../util'
import FooterBar from '@/Footer'
import Loader from '@/Loader'
import Avatar from '@/Avatar'
import UserAvatar from '@/avatar/User'
import OfflineMixin from '@/mixins/OfflineMixin'
import ServerNotReachable from '@/ServerNotReachable'
import VueNativeSock from 'vue-native-websocket'
import PopupView from '@/PopupView'
import QuickAddRecord from '@/QuickAddRecord'
import { mapState } from 'vuex'
import { QLayout, QToolbar, QItemMain, QPopover, QList, QItem } from 'quasar'
import { isEmpty } from '@facilio/utils/validation'
import WMSClient from '../websocket/wms/wms-client'
import { initDataDog } from 'util/data-dog'
import { initPendo } from 'util/pendo'
import { API } from '@facilio/api'
import { initGoogleAnalytics } from 'src/track.js'

export default {
  mixins: [OfflineMixin],
  components: {
    QLayout,
    QToolbar,
    QItemMain,
    QPopover,
    QList,
    QItem,
    FooterBar,
    Loader,
    Avatar,
    UserAvatar,
    ServerNotReachable,
    PopupView,
    QuickAddRecord,
  },
  data() {
    return {
      loading: true,
      isHelpersIntialized: false,
      windowWidth: document.getElementById('q-app').clientWidth
        ? document.getElementById('q-app').clientWidth
        : 1280,
      windowHeight: document.getElementById('q-app').clientHeight
        ? document.getElementById('q-app').clientHeight
        : 700,
      isTvMode: false,
      isTablet: false,
      screenTheame: 'white',
      newlayout: true,
      txt: '',
      profileContainer: false,
      onlineState: navigator,
      products: [],
      wsPingPongInterval: null,
      themeClassNew: '',
      siteList: [],
      dialogVisible: false,
      dialogFormVisible: false,
      tvMode: 'normal',
      rogerOrgs: [
        { domain: 'dsoa', name: 'FM Energy Manager' },
        { domain: 'springfield', name: 'FM Supervisor' },
        { domain: 'hendry', name: 'FM Admin' },
        { domain: 'rmzbangalore', name: 'Executive' },
        { domain: 'damac', name: 'Damac Heights' },
      ],
      edenOrgs: [
        { domain: 'springfield', name: 'Supervisor' },
        { domain: 'rmzbangalore', name: 'Executive' },
      ],
      bannerListData: {},
    }
  },
  created() {
    this.$store.dispatch('loadSite')
    this.$on('online', function() {
      this.onlineState = null
    })
    this.$on('offline', function() {
      this.onlineState = null
    })
    this.getBannerListData()
  },
  mounted() {
    let istablet = /ipad|android|android 3.0|xoom|sch-i800|playbook|tablet|kindle/i.test(
      navigator.userAgent.toLowerCase()
    )
    this.getScreenWidth()
    if (istablet && !this.isTablet) {
      window.localStorage.setItem('mode', false)
    }
    this.screenTheame = window.localStorage.getItem('theme')
    this.loadScreenMode()
    Vue.prototype.$servicePortal = false
    Vue.prototype.$newlayout = true
  },
  destroyed() {
    if (this.wsPingPongInterval !== null) {
      clearInterval(this.wsPingPongInterval)
      this.wsPingPongInterval = null
    }
    window.removeEventListener('message')
  },
  computed: {
    ...mapState({
      serverNotReachable: state => state.serverNotReachable,
      loginRequired: state => state.loginRequired,
      account: state => state.account,
      app: state => state.app,
      site: state => state.site,
      features: state => state.features,
    }),
    currentSite() {
      let currentSiteId = Number(this.$cookie.get('fc.currentSite')) || -1
      let { site = [] } = this
      let curr = (site || []).find(i => i.id === currentSiteId)

      return (
        curr || {
          id: -1,
          name: 'All',
        }
      )
    },
    hasMetaLoaded() {
      let {
        isHelpersIntialized,
        account: { user = null } = {},
        features,
      } = this
      let hasMeta = !isEmpty(user) && !isEmpty(features)

      return hasMeta && isHelpersIntialized
    },
    modules() {
      let self = this
      let { currentProduct = {} } = this.$store.state
      let { modules } = currentProduct || {}

      if (!isEmpty(modules)) {
        let accessibleModules = modules.filter(function(m) {
          return (
            (typeof m.permission === 'undefined' ||
              self.$hasPermission(m.permission)) &&
            (!m.license || self.$helpers.isLicenseEnabled(m.license)) &&
            (!m.hide_if_license ||
              !self.$helpers.isLicenseEnabled(m.hide_if_license))
          )
        })
        return accessibleModules
      }
      return []
    },
    themeClass() {
      let theme = 'white'
      let storedTheme = window.localStorage.getItem('theme')
      let paramTheme =
        this.$route.query.theme && this.$route.query.theme !== ''
          ? this.$route.query.theme
          : null
      if (paramTheme) {
        theme = paramTheme
      } else if (storedTheme) {
        theme = storedTheme
      }
      window.localStorage.setItem('theme', theme)
      document.body.classList.remove('fc-white-theme')
      document.body.classList.remove('fc-black-theme')
      document.body.classList.add('fc-' + theme + '-theme')
      return 'fc-' + theme + '-theme'
    },
    notifications() {
      return this.$store.state.notification.notification
    },
    newAlarms() {
      return this.$store.state.alarm.unseen || this.$store.state.newAlarm.unseen
    },
    newNotification() {
      return this.$store.state.notification.newNotification
    },
    isSiteDecommissioned() {
      let { site } = this
      if (!isEmpty(site))
        return site.length > 1
          ? false
          : this.$getProperty(site, '0.decommission', false)
      else return false
    },
    isBuildingsTalk() {
      let { account } = this
      if (
        !isEmpty(account) &&
        account.user.email === 'machinestalk@facilio.com' &&
        account.org.logoUrl
      ) {
        return true
      }
      if (
        window.rebrandInfo &&
        window.rebrandInfo.brandName === 'BuildingsTalk'
      ) {
        return true
      }
      return false
    },
    isMoro() {
      return window.rebrandInfo && window.rebrandInfo.brandName === 'Moro'
    },
    isHoneywell() {
      let { account } = this
      if (
        !isEmpty(account) &&
        (account.org.id === 143 || account.user.email === 'demo@honeywell.com')
      ) {
        return true
      }
      return false
    },
    cssVariables() {
      if (
        window.rebrandInfo &&
        window.rebrandInfo.brandName === 'BuildingsTalk'
      ) {
        return {
          '--fc-toolbar-bg': '#2f2e49',
          '--fc-theme-color': '#2e398e',
          '--fc-theme-filter':
            'invert(11%) sepia(95%) saturate(6755%) hue-rotate(244deg) brightness(53%) contrast(131%)',
        }
      } else {
        return {
          '--fc-toolbar-bg': '#2f2e49',
          '--fc-theme-color': '#ff3184',
          '--fc-white-color': '#ffffff',
        }
      }
    },
    orgs() {
      if (this.$store.state.orgs && this.$store.state.orgs.length > 1) {
        let { user = {} } = this.account || {}
        let demoOrgs = []
        if (user.email === 'roger@facilio.com') {
          demoOrgs = this.rogerOrgs
        } else if (user.email === 'eden@facilio.com') {
          demoOrgs = this.edenOrgs
        }
        if (demoOrgs.length) {
          let orgMap = {}
          this.$store.state.orgs.forEach(org => {
            orgMap[org.domain] = org
          })
          let orgs = []
          demoOrgs.forEach(demoOrg => {
            let org = orgMap[demoOrg.domain]
            if (org) {
              let newOrg = this.$helpers.cloneObject(org)
              newOrg.name = demoOrg.name
              orgs.push(newOrg)
            }
          })
          return orgs
        } else {
          return this.$store.state.orgs
        }
      }
      return null
    },
  },
  watch: {
    newNotification(newVal) {
      if (newVal != null) {
        this.$sound.play('stuffed')
        if (Notification.permission === 'granted') {
          let notification = new Notification('Workorder assigned', {
            icon: 'https://fazilio.com/statics/favicon.png',
            body: 'Workorder assigned to you',
          })
          notification.onclick = function() {}
        }
      }
    },
    loginRequired() {
      if (this.loginRequired) {
        this.$router.push({
          name: 'login',
          query: {
            redirect: this.$route.path,
          },
        })
      }
    },
    newAlarms(newVal) {
      if (newVal > 0) {
        this.$sound.play('alarm')
      }
    },
    isBuildingsTalk(value) {
      window.isBuildingsTalk = value
    },
  },
  methods: {
    initializeMeta() {
      this.isHelpersIntialized = false

      let { account = {}, app = {} } = this
      account.app = app
      Vue.use(Util, {
        account,
      })

      let { language: userLanguage } = account.user || {}
      let { language: orgLanguage } = account.org || {}

      if (!isEmpty(userLanguage)) {
        this.$i18n.locale = userLanguage
      } else if (!isEmpty(orgLanguage)) {
        this.$i18n.locale = orgLanguage
      } else {
        this.$i18n.locale = 'en'
      }

      this.initializeWMS()
      initGoogleAnalytics(account)
      this.setSandboxMode()
      const isProd = process.env.NODE_ENV === 'production'
      if (isProd) initDataDog(this.$account)
      initPendo(this.$account, this)
      if (this.$account.org.orgId !== 418) this.$store.dispatch('loadSpaces')
      if (this.$account.org.orgId !== 418)
        this.$store.dispatch('notification/fetchNotifications')

      this.isHelpersIntialized = true
    },
    initializeWMS() {
      let { account: { config = null } = {} } = this

      if (config && config.new_ws_endpoint) {
        Vue.prototype.$wms = new WMSClient({
          endpoint: config.new_ws_endpoint,
          log: true,
        })
      } else {
        console.warn(
          'WebSocket endpoint not returned by server, so live update and notifications will not work!!!'
        )
      }
    },
    setSandboxMode() {
      let { account: { user } = {} } = this

      window.demoMode = false
      if (
        user.email === 'machinestalk@facilio.com' ||
        user.email === 'demo@facilio.com'
      ) {
        window.demoMode = true
      }

      if (this.$route && this.$route.query && this.$route.query.sandbox) {
        if (this.$route.query.sandbox === 'true') {
          this.$cookie.set('fc.sandbox', 'true', {
            expires: '10Y',
            path: '/',
          })
        } else if (this.$route.query.sandbox === 'false') {
          this.$cookie.set('fc.sandbox', 'false', {
            expires: '10Y',
            path: '/',
          })
        }
      }
    },
    setCurrentSite(space) {
      this.$cookie.set('fc.currentSite', space.id, {
        expires: '10Y',
        path: '/',
      })
      this.$router.go()
    },
    resetAlarmUnseen() {
      this.$store.commit('alarm/RESET_UNSEEN')
      this.$store.commit('newAlarm/RESET_UNSEEN')
      this.$sound.stop('alarm')
    },
    isSetup() {
      return this.$route.path.startsWith('/app/setup')
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
      this.$router.push({
        name: 'logout',
        query: {
          redirect: this.$route.path,
        },
      })
    },
    initChat() {
      // let crypto = require('crypto')
      let currentUserId = this.account.user.email
      let currentUserHash = crypto
        .createHmac(
          'sha256',
          '5b373f853aad52a534fd991cebd1f89e7e0b805f7509c723c272d5b9c09df556984f9152dcf0ffa31d53fa1950f666f1'
        )
        .update(currentUserId)
        .digest('hex')
      window.MP = {
        data: {
          appId: '5a18165bfe4b3c002816e3f8',
          user: {
            username: this.account.user.email,
            firstName: this.account.user.name,
            lastName: 'User',
            email: this.account.user.email,
            userId: currentUserId,
            groupId: this.account.org.orgDomain,
          },
          userHash: currentUserHash,
        },
        settings: {
          styles: {
            headerColor: '#2f2e49', // a primary chat color
            scrollColor: '#01c1b4', // a color of the chat scroll
          },
        },
      }
    },
    markNotificationAsRead(notification) {
      this.$store.dispatch('notification/markAsRead', [notification.id])
    },
    markNotificationsAsSeen() {
      this.$store.dispatch('notification/markAllAsSeen')
    },
    getScreenWidth() {
      let self = this
      self.$nextTick(function() {
        window.addEventListener('resize', function() {
          self.windowWidth = window.innerWidth
          self.windowHeight = window.innerHeight
        })
        window.addEventListener(
          'webkitfullscreenchange mozfullscreenchange fullscreenchange',
          function() {}
        )
      })
    },
    getScreenHeight() {
      let self = this
      if (self.isTvMode) {
        window.appConfig = {
          isTvMode: this.isTvMode,
          height:
            document.getElementById('q-app').clientHeight / self.getScale() -
            50,
        }
        return (
          'height:' +
          (document.getElementById('q-app').clientHeight / self.getScale() -
            50) +
          'px;'
        )
      } else {
        return ''
      }
    },
    switchTheme() {
      let storedTheme = window.localStorage.getItem('theme')
      if (storedTheme === '' || storedTheme === 'white') {
        document.body.classList.remove('fc-white-theme')
        document.body.classList.remove('fc-black-theme')
        window.localStorage.setItem('theme', 'black')
        document.body.classList.add('fc-black-theme')
        this.themeClassNew = 'fc-black-theme'
      } else if (storedTheme === 'black') {
        document.body.classList.remove('fc-white-theme')
        document.body.classList.remove('fc-black-theme')
        window.localStorage.setItem('theme', 'white')
        document.body.classList.add('fc-white-theme')
        this.themeClassNew = 'fc-white-theme'
      }
      location.reload()
    },
    switchScreen() {
      let stage = !this.isTvMode
      this.isTvMode = stage
      window.localStorage.setItem('mode', stage)
      location.reload()
    },
    loadScreenMode() {
      let stage = window.localStorage.getItem('mode')
      if (stage !== null) {
        if (stage === 'true') {
          this.isTvMode = true
        } else {
          this.isTvMode = false
        }
      } else {
        if (this.isTablet) {
          window.localStorage.setItem('mode', true)
          this.isTvMode = true
        } else {
          this.isTvMode = false
        }
      }
    },
    switchAccount(org) {
      this.$http
        .get('switchaccount?switchOrgDomain=' + org.domain)
        .then(function() {
          Vue.cookie.delete('lastvisit')
          window.location.href =
            window.location.protocol + '//' + window.location.host
        })
        .catch(function() {})
    },
    getScale() {
      if (this.isTvMode) {
        return this.windowWidth / 1280
      } else {
        return 1
      }
    },
    getStyle() {
      let style = Object.assign({}, this.cssVariables)
      if (this.isTvMode) {
        style.transform = 'scale(' + this.getScale() + ') translateX(-50%)'
        style.width = '1280px !important'
        style['transform-origin'] = '0% 0% 0px'
        style['margin-left'] = '50%'
      }
      return style
    },
    async getBannerListData() {
      this.loading = true
      let { error, data } = await API.get(`v2/banner/list`)
      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.bannerListData = data.facilioBanners[0] || {}
      }
    },
  },
}
