<template>
  <div>
    <div v-if="hasProxy">
      <el-alert
        :title="proxyBanner"
        type="error"
        :closable="false"
        effect="dark"
        class="fc-portal-login-alert"
      >
      </el-alert>
    </div>
    <div
      v-if="loading"
      class="flex-middle justify-content-center height100vh width100"
    >
      <spinner :show="loading" size="80"></spinner>
    </div>
    <div v-else class="portal-home-layout">
      <tenant-sidebar></tenant-sidebar>
      <ConnectedAppDialer></ConnectedAppDialer>
      <router-view class="flex-grow dashboard-print" />
      <f-chatbot v-if="$helpers.isLicenseEnabled('CHATBOT')"> </f-chatbot>
    </div>
  </div>
</template>
<script>
import HomeMixin from 'src/PortalTenant/auth/portalHomeMixin'
import tenantSidebar from 'src/PortalTenant/components/TenantSidebar'
import Spinner from '@/Spinner'
import getProperty from 'dlv'
import FChatbot from 'src/chatbot/FChatbotWrapper'
import websocketMixin from 'src/websocket/websocketMixin'
import WMSClient from '../websocket/wms/wms-client'
import Vue from 'vue'
import { initDataDog } from 'util/data-dog'
import { initPendo } from 'util/pendo'
import moment from 'moment-timezone'
import { isEmpty } from '@facilio/utils/validation'
import ConnectedAppDialer from '@/home/ConnectedAppDialer'

export default {
  mixins: [HomeMixin, websocketMixin],
  components: {
    tenantSidebar,
    Spinner,
    FChatbot,
    ConnectedAppDialer,
  },
  data() {
    return {
      loading: true,
      displayProxyBanner: true,
      counterPrefix: null,
      counter: null,
      acc: null,
    }
  },
  async created() {
    this.acc = await this.loadAccount(true)

    let { sessionEndTime, user } = this.acc || {}
    let { proxy, userName } = user || {}

    if (sessionEndTime) {
      this.counterPrefix = `${proxy} will be viewing as ${userName} for `
      this.startTimer(sessionEndTime)
    }
  },
  beforeRouteEnter(to, from, next) {
    next(async vm => {
      // Public info is called first because we set store data
      // from both publicInfo and loadAccount so have to avoid edge cases
      await vm.loadPublicInfo()

      let [account] = await Promise.all([vm.loadAccount(), vm.loadFeatures()])

      if (account && getProperty(account, 'config.new_ws_endpoint')) {
        Vue.prototype.$wms = new WMSClient({
          endpoint: getProperty(account, 'config.new_ws_endpoint'),
          log: true,
        })
      }
      const isProd = process.env.NODE_ENV === 'production'
      if (isProd) initDataDog(account)
      initPendo(account, vm)
      vm.loading = false
    })
  },
  destroyed() {
    window.removeEventListener('message')
  },
  computed: {
    proxyBanner() {
      return this.counterPrefix + (this.counter || '---')
    },
    hasProxy() {
      let { user } = this.acc || {}
      let { proxy } = user || {}
      return !isEmpty(proxy)
    },
  },
  methods: {
    startTimer(endTime) {
      let startTime = Date.now()
      if (startTime > endTime) {
        return
      }
      let duration = endTime - startTime
      let interval = 1000
      setInterval(() => {
        duration = moment.duration(duration - interval, 'milliseconds')
        this.counter = moment
          .utc(duration.as('milliseconds'))
          .format('HH:mm:ss')
      }, interval)
    },
  },
}
</script>
<style lang="scss">
.portal-home-layout {
  width: 100%;
  height: 100vh;
  display: flex;
  position: relative;
  flex-direction: row;
  background: #f5f6fa;

  .portal-layout-header .fc-create-btn {
    padding: 14px 14px;
    letter-spacing: 0.7px;
    background-color: #ef508f !important;
  }

  .fc-v1-portal-main {
    position: relative;
    display: block;
    width: 100%;
    height: 100vh;
    overflow: hidden;
    box-sizing: border-box;

    .fc-widget-header {
      padding: 15px !important;
    }
    .fc__asset__main__header {
      border-top: 1px solid #ebf0f5;
    }
    .service-portal-main {
      height: 100%;
      overflow: initial;
      padding: 0;
      .floor-filter-con {
        width: 100%;
        height: 100vh;
        z-index: 900;
        position: absolute;
        top: 0;
        left: 1px;
      }
      .time-picker-popover {
        width: 350px;
        position: absolute;
        left: -120px;
        background-color: #ffffff;
        top: 55px;
        display: -webkit-box;
        display: -ms-flexbox;
        display: flex;
        border: 1px solid #e4eaed;
        border-radius: 6px;
      }
    }
    .h100 {
      height: 100%;
    }
    .catalog-container {
      margin: 15px !important;
    }

    .fc-v1-portal-table td {
      padding: 15px 20px !important;
    }

    .fc-v1-portal-table .el-table,
    .portal-table-scroll-pad .el-table {
      height: calc(100vh - 120px) !important;
    }

    .fc-v1-portal-table .el-table td {
      padding: 15px 20px !important;
    }
    .fc-workpermit-table {
      .el-table th > .cell {
        padding-left: 0 !important;
      }
    }

    .form-data-creation {
      margin-left: 20px;

      .header {
        margin-top: 0;
        margin-left: -20px;
        width: 100%;
        padding: 20px 30px 20px 20px;
        background: #ffffff;
        min-height: 80px;
        align-items: center;
        position: absolute;
        top: 0;

        .title {
          margin-top: 0 !important;
          font-weight: 400;
        }
      }
      .f-webform-container,
      .loading-container {
        margin-top: 100px;
        height: calc(100vh - 110px);
      }
    }
  }
  .floor-filter-con {
    left: 1px !important;
  }
}
.fc-portal-login-alert {
  width: 100%;
  height: 25px;
  font-size: 12px;
  z-index: 500;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 500;
  border-radius: 0;
}
</style>
