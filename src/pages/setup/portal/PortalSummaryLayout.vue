<template>
  <div class="height-100">
    <SetupLoader
      v-if="loading"
      class="m10 height100vh fc-portal-summary-loader"
    >
      <template #setupLoading>
        <spinner :show="loading" size="80"></spinner>
      </template>
    </SetupLoader>

    <template v-else>
      <component
        :is="appSummary"
        :application="application"
        @reload="loadApp"
        class="portal-summary-layout"
      ></component>
    </template>
  </div>
</template>
<script>
import { loadLayouts } from 'util/webtabUtil'
import SetupLoader from 'pages/setup/components/SetupLoader'

export default {
  data() {
    return {
      application: {},
      loading: false,
    }
  },
  created() {
    this.loadApp()
  },
  components: {
    SetupLoader,
  },
  computed: {
    appId() {
      let { params } = this.$route
      let { id } = params || {}
      return id ? parseInt(id) : null
    },
    isWorkCenterApp() {
      let {
        appCategory: { WORK_CENTERS },
      } = this.$constants
      let { appCategory } = this.application || {}

      return appCategory === WORK_CENTERS
    },
    isDataLoaderApp() {
      let { linkName } = this.application || {}

      return linkName === 'dataloader'
    },
    appLinkName() {
      let { linkName } = this.application || {}
      return linkName
    },
    appSummary() {
      let { isWorkCenterApp, appLinkName, isDataLoaderApp } = this

      if (isWorkCenterApp) {
        return () => import('./v1/ApplicationSummary')
      } else if (isDataLoaderApp) {
        return () => import('./v1/DataLoaderSummaryPage')
      } else if (appLinkName === 'vendor') {
        return () => import('./v1/VendorPortalSummary')
      } else if (appLinkName === 'tenant') {
        return () => import('./v1/TenantPortalSummary')
      } else if (appLinkName === 'service') {
        return () => import('./v1/OccupantPortalSummary')
      } else if (appLinkName === 'employee') {
        return () => import('./v1/EmployeePortalAppSummary')
      } else if (appLinkName === 'client') {
        return () => import('./v1/ClientPortalAppSummary')
      } else {
        return null
      }
    },
  },
  methods: {
    async loadApp() {
      this.loading = true
      let { error, data } = await loadLayouts(this.appId)

      if (!error) {
        this.application = data
      }
      this.loading = false
    },
  },
}
</script>
<style lang="scss">
.portal-summary-layout {
  .portal-summary {
    &.layout-tabs {
      .layout-icons {
        top: 15px;
      }
      .el-tabs {
        margin-top: 0px;
      }
    }
    .add-group-btn,
    .add-app-btn {
      border-radius: 3px;
      border-color: transparent;
      background-color: #ee518f;
      padding: 10px;
      width: 200px;
      color: #fff;
      font-weight: 500;
      text-transform: uppercase;
      cursor: pointer;
    }
    .add-group-btn:hover,
    .add-app-btn:hover {
      font-weight: bold;
    }
  }
  .fc-setup-tab {
    .el-tabs__content {
      height: calc(100vh - 170px);
      padding-bottom: 0px;
    }
    .layout-tabs {
      .el-tabs .el-tabs__content {
        height: calc(100vh - 270px);
        padding-bottom: 0px;
      }
      .layout-container {
        height: 100%;
      }
    }
  }
}
.fc-portal-summary-loader {
  width: 98.6% !important;
}
</style>
