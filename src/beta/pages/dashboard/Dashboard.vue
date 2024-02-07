<template>
  <div style="width:100%;height:100%">
    <MicroEmbed
      style="z-index:1;position:relative"
      ref="dashboard"
      name="dashboard"
      :url="dashboardUrl"
      :handlers="handlers"
      @loaded="sendAppId"
      @routeChange="setRoute"
      :style="
        fullscreen &&
          'position:absolute;top:0;left:0;bottom:0;right:0;height:100%;width:100%;z-index:100000'
      "
    />
    <div
      v-if="overlay"
      style="position:absolute;top:0;left:0;background:rgba(0,0,0,0.45);height:100%;width:100%"
    ></div>
  </div>
</template>

<script>
import MicroEmbed from './MicroEmbed.vue'
import {
  getApp,
  findRouteForModule,
  findRouteForReport,
  pageTypes,
} from '@facilio/router'
import { getBaseURL } from 'util/baseUrl'
import { mapGetters, mapState } from 'vuex'
import Vue from 'vue'
export default {
  components: { MicroEmbed },
  computed: {
    ...mapState({
      currentTab: state => state.webtabs.selectedTab,
    }),
    ...mapGetters('webtabs', ['tabHasPermission']),
    // dashboardUrl() {
    //   let url = ''
    //   if (
    //     window.location.origin == 'http://localhost:9090' ||
    //     window.location.origin == 'http://localhost:9091'
    //   ) {
    //     url = 'http://localhost:8080/dashboard/maintenance/list'
    //   } else {
    //     url = `${window.location.origin}/dashboard/maintenance/list`
    //   }
    //   return url
    // },
    appId() {
      return getApp().id
    },
  },
  data() {
    return {
      handlers: {},
      overlay: false,
      fullscreen: false,
      dashboardUrl: '',
    }
  },

  mounted() {
    this.handlers = {
      getAppId: this.getAppId,
      baseUrl: this.baseUrl,
      getModuleRoute: this.getModuleRoute,
      getReportRoute: this.getReportRoute,
      fetchAccountDetails: this.accountDetails,
      hasViewPermission: this.hasViewPermission,
      hasCreatePermission: this.hasCreatePermission,
      hasDeletePermission: this.hasDeletePermission,
      hasDashboardEditPermission: this.hasDashboardEditPermission,
      hasSharePermission: this.hasSharePermission,
      parentDimension: this.parentDimension,
      showOverlay: this.showOverlay,
      hideOverlay: this.hideOverlay,
      disableFullScreen: this.disableFullScreen,
      enableFullScreen: this.enableFullScreen,
    }
    this.setIframeUrl()
  },
  methods: {
    setIframeUrl() {
      let url = ''
      let urlPath = this.$route.path
        .split('/')
        .filter(Boolean)
        .slice(3)
        .join('/')
      let path = 'dashboard/maintenance/'
      path += urlPath || 'list'

      if (window.location.origin.startsWith('http://localhost')) {
        url = `http://localhost:8080/${path}`
      } else {
        url = `${window.location.origin}/${path}`
      }
      this.dashboardUrl = url
    },
    disableFullScreen() {
      this.fullscreen = false
    },
    enableFullScreen() {
      this.fullscreen = true
    },
    showOverlay() {
      this.overlay = true
    },
    hideOverlay() {
      this.overlay = false
    },
    baseUrl() {
      return getBaseURL()
    },
    accountDetails() {
      return Vue.prototype.$account
    },

    sendAppId() {
      this.$refs['dashboard'].sendEvent('appId', this.appId)
    },
    getAppId() {
      return getApp().id
    },
    getModuleRoute(moduleObj) {
      let { moduleName } = moduleObj
      return findRouteForModule(moduleName, pageTypes.LIST)
    },
    getReportRoute(data) {
      let { moduleType, moduleName } = data
      if (moduleName) {
        return findRouteForReport(moduleType, pageTypes.REPORT_VIEW, {
          moduleName: moduleName,
        })
      } else {
        return findRouteForReport(moduleType, pageTypes.REPORT_VIEW)
      }
    },
    setRoute(path) {
      let iframePath = path.split(/\/dashboard\/maintenance\//)[1]

      let parentPath = window.location.pathname
      let parentPathArr = parentPath.split('/').slice(0, 4)
      let updatedPath = parentPathArr.join('/') + '/' + iframePath
      if (parentPath !== updatedPath) {
        this.$router.push({ path: updatedPath })
      }
    },
    hasViewPermission() {
      let { currentTab, tabHasPermission } = this
      return tabHasPermission('VIEW', currentTab)
    },
    hasCreatePermission() {
      let { currentTab, tabHasPermission } = this
      return tabHasPermission('CREATE', currentTab)
    },
    hasDeletePermission() {
      let { currentTab, tabHasPermission } = this
      return tabHasPermission('DELETE', currentTab)
    },
    hasDashboardEditPermission() {
      let { currentTab, tabHasPermission } = this
      return tabHasPermission('EDIT', currentTab)
    },
    hasSharePermission() {
      let { currentTab, tabHasPermission } = this
      return tabHasPermission('SHARE', currentTab)
    },
    parentDimension() {
      return { height: window.innerHeight, width: window.innerWidth }
    },
  },
}
</script>

<style lang="scss" scoped></style>
