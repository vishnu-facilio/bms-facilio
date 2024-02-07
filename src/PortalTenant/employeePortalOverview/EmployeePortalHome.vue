<template>
  <div>
    <div v-if="acc && acc.user && acc.user.proxy">
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
    <div v-else class="width100 portal-home-layout">
      <el-container style="height: 100%;">
        <EmployeeSidebar></EmployeeSidebar>
        <el-container>
          <el-header class="p0" :height="'56px'">
            <EmployeePortalHeader></EmployeePortalHeader>
          </el-header>

          <el-main class="p0 employee-portal-homepage">
            <router-view />
          </el-main>
        </el-container>
      </el-container>

      <f-chatbot v-if="$helpers.isLicenseEnabled('CHATBOT')"> </f-chatbot>
    </div>
  </div>
</template>
<script>
import HomeMixin from 'src/PortalTenant/auth/portalHomeMixin'
import EmployeeSidebar from 'src/PortalTenant/employeePortalOverview/EmployeeSideBar'
import Spinner from '@/Spinner'
import getProperty from 'dlv'
import FChatbot from 'src/chatbot/FChatbotWrapper'
import websocketMixin from 'src/websocket/websocketMixin'
import WMSClient from 'src/websocket/wms/wms-client'
import Vue from 'vue'
import { initDataDog } from 'util/data-dog'
import { initPendo } from 'util/pendo'
import { findRouteForModule, findRouteForTab, pageTypes } from '@facilio/router'
import moment from 'moment-timezone'
import { mapState } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import { isRecordLocked, isRequestedState } from 'PortalTenant/util'
import EmployeePortalHeader from 'src/PortalTenant/employeePortalOverview/EmployeePortalHeader.vue'
const { LIST } = pageTypes

export default {
  // props: ['moduleName'],
  mixins: [HomeMixin, websocketMixin],
  components: {
    EmployeeSidebar,
    Spinner,
    FChatbot,
    EmployeePortalHeader,
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
    if (
      this.acc &&
      this.acc.sessionEndTime &&
      this.acc?.user?.proxy &&
      this.acc?.user?.userName
    ) {
      this.counterPrefix = `${this.acc.user.proxy} will be viewing as ${this.acc.user.userName} for `
      this.startTimer(this.acc.sessionEndTime)
    }
  },
  methods: {
    profileAction(command) {
      if (command === 'myprofile') {
        this.$router.push({
          path: `/${this.appName}/profile`,
        })
      } else if (command === 'logout') {
        this.logout()
      }
    },
    canShowActions(record) {
      let { moduleName } = this
      return !isRecordLocked(record, moduleName) && !isRequestedState(record)
    },
    getViewDetail() {
      if (this.currentView) {
        this.$store.dispatch('view/loadViewDetail', {
          viewName: this.currentView,
          moduleName: this.moduleName,
        })
      }
    },
    loadViews() {
      return this.$store
        .dispatch('view/loadGroupViews', {
          moduleName: this.moduleName,
        })
        .then(this.initViews)
    },
    initViews() {
      if (!this.currentView) {
        let { views } =
          this.groupViews.find(group => !isEmpty(group.views)) || {}

        if (!isEmpty(views)) {
          this.goToView(views[0])
        }
      }
    },
    goToView(view) {
      let route = this.getRoute(view)
      this.$router.replace(route)
    },
    getRoute(view) {
      if (isEmpty(view && view.name)) {
        return
      }

      let { id } = this.currentTab
      let { name = null } = findRouteForTab(id, { pageType: LIST }) || {}

      return name
        ? {
            name,
            params: { viewname: view.name },
          }
        : null
    },
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
    getCreationRoute() {
      return findRouteForModule(this.moduleName, pageTypes.CREATE)
    },
  },
  computed: {
    proxyBanner() {
      return this.counterPrefix + (this.counter || '---')
    },
    ...mapState('webtabs', {
      currentTab: state => state.selectedTab,
    }),
    moduleName() {
      return this.$attrs.moduleName
    },
  },
  watch: {
    moduleName: {
      async handler(newVal, oldVal) {
        if (newVal !== oldVal && !isEmpty(newVal)) {
          await this.$store.dispatch('view/clearViews')
          this.loadViews()
        }
      },
      immediate: true,
    },
    currentView(value) {
      // If current view is removed from URL, loadViews again and navigate to first group
      if (isEmpty(value)) this.loadViews()
    },
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

  .el-header {
    // background-color: #ffffff;
    color: #333;
    height: 56px;
    background-color: #fff;
    .header-details {
      display: flex;
      flex-direction: column;

      .center-title {
        height: 70px;
        justify-content: center;
      }
      .tab-header {
        width: 100%;
        height: 56px;
        background-color: #fff;
        display: flex;
        justify-content: space-between;

        .tab-name {
          margin: 20px 0px 20px 6px;
          font-size: 16px;
          font-weight: 500;
          font-stretch: normal;
          font-style: normal;
          line-height: normal;
          letter-spacing: normal;
          text-align: justify;
          color: #12324a;
        }
      }
      .header-section {
        display: flex;
        align-items: center;
      }
      .fc-avatar {
        margin: 6px 24px 13px 16px;
        padding: 1px;
      }
    }
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
.employee-portal-homepage {
  overflow: hidden;
}
.employee-portal-homepage .sr-email-thread-widget {
  border-radius: 0px;
}
.employee-portal-homepage .form-btn.secondary {
  margin-left: 0px;
  border-radius: 0;
}
</style>
