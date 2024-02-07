<template>
  <div v-if="loading">{{ $t('panel.loading_load') }}</div>
  <div class="passcode-page" v-else-if="!account">
    <table>
      <tr>
        <td>
          <img class="fc-logo" src="~assets/facilio-logo-black.svg" />
        </td>
      </tr>
      <tr>
        <td>
          <div class="code-header">
            {{ $t('panel.pass') }}
          </div>
        </td>
      </tr>
      <tr v-if="!refreshPageToGetCode">
        <td>
          <div class="code-subheader">
            {{ $t('panel.connect_facilio_tv') }}
          </div>
          <div class="code mT50">
            {{ passcode ? passcode : '---' }}
          </div>
        </td>
      </tr>
      <tr v-else>
        <td>
          <div class="error-box">
            {{ $t('panel.refresh') }}
          </div>
        </td>
      </tr>
    </table>
  </div>
  <div
    class="layout-page-container"
    v-else
    :class="{
      'fc-white-bg':
        $route.path === '/app/pdf/workpermit' &&
        $route.path === '/app/pdf/quotationpdf',
    }"
  >
    <div class="layout-page scrollable" :class="themeClass">
      <dashboard-loop-runner
        :remoteScreen="account.data.connectedScreen"
      ></dashboard-loop-runner>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import http from 'util/http'
import Util from '../util'
import Vue from 'vue'
import VueNativeSock from 'vue-native-websocket'
import DashboardLoopRunner from './DashboardLoopRunner'
import WMSClient from '../websocket/wms/wms-client'

export default {
  components: {
    DashboardLoopRunner,
  },
  data() {
    return {
      loading: true,
      validateCodeInterval: 3000,
      passcode: null,
      refreshPageToGetCode: false,
      account: null,
    }
  },
  mounted() {
    axios.defaults.headers.common['X-Remote-Screen'] = 'true'

    this.connectScreen()
  },
  computed: {
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
    refreshCount() {
      return this.$store.state.remotescreen.refreshCount
    },
    buildUpdate() {
      return this.$store.state.system.reload
    },
  },
  watch: {
    refreshCount: function(newVal) {
      if (window.location.reload) {
        window.location.reload()
      } else {
        this.connectScreen()
      }
    },
    buildUpdate: function() {
      if (this.buildUpdate) {
        if (window.location.reload) {
          window.location.reload()
        } else {
          this.connectScreen()
        }
      }
    },
  },
  methods: {
    connectScreen() {
      let self = this
      self.loading = true
      self.account = null
      http
        .get('/tv/connect')
        .then(function(response) {
          self.loading = false
          if (response.data.account) {
            self.account = response.data.account
            self.loadAccount()
          } else {
            self.generateCode()
          }
        })
        .catch(function() {
          self.loading = false
          self.generateCode()
        })
    },
    loadAccount() {
      this.$store.dispatch('setCurrentAccount', this.account)

      Vue.use(Util, { account: this.account })

      if (this.account.config && this.account.config.ws_endpoint) {
        Vue.use(VueNativeSock, this.account.config.ws_endpoint, {
          store: this.$store,
          format: 'json',
          reconnection: true, // (Boolean) whether to reconnect automatically (false)
          reconnectionDelay: 10000, // (Number) how long to initially wait before attempting a new (1000)
        })

        if (this.wsPingPongInterval === null) {
          let self = this
          this.wsPingPongInterval = setInterval(function() {
            if (self.$socket) {
              let obj = { from: 0, to: 0, content: { ping: 'check' } }
              self.$socket.sendObj(obj)
            }
          }, 60000)
        }
      } else {
        console.warn(
          'WebSocket endpoint not returned by server, so live update and notifications will not work!!!'
        )
      }
      try {
        if (this.account.config.new_ws_endpoint) {
          Vue.prototype.$wms = new WMSClient({
            endpoint: this.account.config.new_ws_endpoint,
            log: true,
          })
        }
      } catch (err) {
        console.log(err)
      }
    },
    generateCode() {
      let self = this
      if (!this.passcode) {
        http.get('/tv/generatecode').then(function(response) {
          self.passcode = response.data.result.code
          self.validateCode()
        })
      }
    },
    validateCode() {
      let self = this
      http
        .get('/tv/validatecode?code=' + self.passcode)
        .then(function(response) {
          if (response.data.responseCode === 1) {
            // error
            self.refreshPageToGetCode = true
          } else {
            if (response.data.result.status === 'connected') {
              self.connectScreen()
            } else {
              setTimeout(self.validateCode, self.validateCodeInterval)
            }
          }
        })
        .catch(function(error) {
          console.log(error)
          setTimeout(self.validateCode, self.validateCodeInterval)
        })
    },
  },
}
</script>

<style>
@import './../assets/styles/common.css';
</style>
<style>
@import './../assets/styles/white-theme.css';
</style>
<style>
@import './../assets/styles/black-theme.scss';
</style>
<style>
@import './../assets/styles/form.css';
</style>
<style>
@import './../assets/styles/colors.css';
</style>
<style>
@import './../assets/styles/avatar.css';
</style>
<style>
@import './../assets/styles/leed.css';
</style>
<style>
@import './../assets/styles/firealarm.css';
</style>
<style>
@import './../assets/styles/energy.css';
</style>
<style>
@import './../assets/styles/space.css';
</style>
<style>
@import './../assets/styles/element.css';
</style>
<style>
@import './../assets/styles/summary.css';
</style>
<style>
@import './../assets/styles/filter.css';
</style>
<style>
@import './../assets/styles/all-page.scss';
</style>
<style>
@import './../assets/styles/components.scss';
</style>
<style>
@import './../assets/styles/helper.scss';
</style>
<style>
@import './../charts/styles/chart.css';
</style>
<style>
@import './../assets/styles/analytics.scss';
</style>
<style>
@import './../assets/styles/dashboard.css';
</style>
<style>
@import './../assets/styles/c3.css';
</style>
<style>
@import './../assets/styles/markdown-style.css';
</style>

<style>
.passcode-page table {
  text-align: center;
  margin: auto;
  margin-top: 60px;
}

.passcode-page table td {
  padding: 18px;
}

.passcode-page .code-header {
  font-size: 40px;
  font-weight: 500;
}

.passcode-page .code-subheader {
  font-size: 25px;
}

.code {
  border: 1px solid #2f2e49;
  font-size: 80px;
  letter-spacing: 30px;
  text-align: center;
  padding: 20px;
  border-radius: 4px;
  color: #ffffff;
  background: #2f2e49;
}

.error-box {
  border: 1px solid #e3524f;
  font-size: 35px;
  text-align: center;
  padding: 30px;
  border-radius: 4px;
  color: #ffffff;
  background: #e3524f;
}

.mT50 {
  margin-top: 50px;
}
</style>
