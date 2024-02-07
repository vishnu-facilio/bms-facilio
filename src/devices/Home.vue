<template>
  <!-- this acts as home screen for all devices -->
  <div v-if="loading">Loading...</div>
  <div class="passcode-page" v-else-if="!account">
    <table>
      <tr>
        <td>
          <img class="fc-rebrand-logo" :src="brandConfig.logo"/>
        </td>
      </tr>
      <tr>
        <td>
          <div class="code-header">
            Passcode
          </div>
        </td>
      </tr>
      <tr v-if="!refreshPageToGetCode">
        <td>
          <div class="code-subheader">
            Use this code to create a connection between Facilio and this
            Device.
          </div>
          <div class="code mT50">
            {{ passcode ? passcode : '---' }}
          </div>
        </td>
      </tr>
      <tr v-else>
        <td>
          <div class="error-box">
            Please refresh the page to get the passcode.
          </div>
        </td>
      </tr>
    </table>
  </div>

  <router-view v-else> </router-view>
</template>

<script>
import axios from 'axios'
import http from 'util/http'
import Util from '../util'
import Vue from 'vue'
import VueNativeSock from 'vue-native-websocket'
import { mapGetters } from 'vuex'
import WMSClient from '../websocket/wms/wms-client'
//import DashboardLoopRunner from './DashboardLoopRunner'

export default {
  components: {
    // DashboardLoopRunner
    // "DIGITAL_LOGBOOK":logBook,
  },
  data() {
    return {
      loading: true,
      validateCodeInterval: 3000,
      passcode: null,
      refreshPageToGetCode: false,
      account: null,
      device: null,
      space: null,
    }
  },
  mounted() {
    axios.defaults.headers.common['X-Remote-Screen'] = 'true'

    this.connectDevice()
  },

  beforeRouteLeave(to, from, next) {
    //when navigating to summary page stop it and navigate to your summary page : )
    //  if(to.name.includes('summary'))
    //  {
    //navigate to wo summary in devices , same component

    //just switch between list and summary component
    //accessing router props directly throws type error
    let cp = {}
    Object.assign(cp, to)
    if (cp.name && cp.name.includes('summary')) {
      next({ name: 'deviceWoSummary', params: { id: cp.params.id } })
    } else {
      next()
    }
  },
  computed: {
    ...mapGetters(['currentAccount']),
    refreshCount() {
      return this.$store.state.remotescreen.refreshCount
    },
    brandConfig()
    {
      return window.brandConfig
    },
  },
  watch: {
    refreshCount() {
      if (window.location.reload) {
        window.location.reload()
      } else {
        this.connectDevice()
      }
    },
  },
  methods: {
    jumpToDeviceRoute() {
      if (this.account.data.device.deviceTypeEnum == 'DIGITAL_LOGBOOK') {
        this.$router.replace({
          name: 'deviceWoList',
          params: { viewname: 'open' },
        })
      } else if (this.account.data.device.deviceTypeEnum == 'VISITOR_KIOSK') {
        //redirect to welcome screen , but ask device permissions before that and wait 30 seconds for user input , if no user input proceed to redirect
        //let timer=window.setTimeout(()=>{this.$router.replace({name:'welcome'})},30000)
        this.$router.replace({ name: 'welcome' })
      }
    },

    prePromptForCam() {
      navigator.mediaDevices
        .getUserMedia({ video: true })
        .then(vidStream => {
          vidStream.getVideoTracks().forEach(track => track.stop())
        })
        .catch(() => {
          alert('camera access required for taking photos')
        })
    },
    connectDevice() {
      let self = this
      self.loading = true
      self.account = null
      http
        .get('/v2/deviceclient/connect')
        .then(function(response) {
          self.loading = false
          if (response.data.result.account) {
            self.account = response.data.result.account

            self.loadAccount()
          } else {
            self.generateCode()
          }
        })
        .catch(() => {
          self.loading = false
          self.generateCode()
        })
    },
    loadAccount() {
      this.$store.dispatch('setCurrentAccount', this.account)

      Vue.use(Util, { account: this.account })

      if (this.account.config && this.account.config.new_ws_endpoint) {
        Vue.prototype.$wms = new WMSClient({
          endpoint: this.account.config.new_ws_endpoint,
          log: true,
        })

        this.device = this.account.data.device
        if (this.device.deviceTypeEnum == 'VISITOR_KIOSK') {
          this.prePromptForCam()
        }
        this.jumpToDeviceRoute()
      } else {
        console.warn(
          'WebSocket endpoint not returned by server, so live update and notifications will not work!!!'
        )
      }
    },
    generateCode() {
      let self = this
      if (!this.passcode) {
        http.get('v2/deviceclient/generatecode').then(function(response) {
          self.passcode = response.data.result.code
          self.validateCode()
        })
      }
    },
    validateCode() {
      let self = this
      http
        .get('v2/deviceclient/validatecode?code=' + self.passcode)
        .then(function(response) {
          if (response.data.responseCode === 1) {
            // error
            self.refreshPageToGetCode = true
          } else {
            if (response.data.result.status === 'connected') {
              self.connectDevice()
            } else {
              setTimeout(self.validateCode, self.validateCodeInterval)
            }
          }
        })
        .catch(() => {
          setTimeout(self.validateCode, self.validateCodeInterval)
        })
    },
  },
}
</script>

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
