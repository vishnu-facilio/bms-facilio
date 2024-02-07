<template>
  <div>
    <div v-if="serverNotReachable">
      <server-not-reachable></server-not-reachable>
    </div>
    <div v-else-if="!hasMetaLoaded">
      <loader></loader>
    </div>
    <div v-else class="layout fc-main fc-white-theme">
      <div class="fc-agent-con">
        <agent-header>
          <template #logo>
            <img
              v-if="canShowAppLauncher"
              src="~assets/google-menu.svg"
              width="20"
              height="20"
              class="pointer vertical-middle launcher-icon"
              @click="showLauncher = true"
            />
            <img
              class="fc-rebrand-logo vertical-middle"
              :src="brandConfig.logoLight"
            />
          </template>
        </agent-header>
        <div class="fc-agent-main-con">
          <sidebar></sidebar>
          <div class="width100 fc-operations-main">
            <router-view></router-view>
          </div>
        </div>
      </div>
    </div>
    <popup-view ref="dashboardPopupView"></popup-view>
    <AppLauncher
      v-if="showLauncher"
      :close="() => (showLauncher = false)"
    ></AppLauncher>
  </div>
</template>
<script>
import Vue from 'vue'
import { mapState } from 'vuex'
import HomeMixin from 'pages/HomeMixin.js'
import Sidebar from './Sidebar'
import AgentHeader from 'src/agent/AgentHeader'
import AppLauncher from 'src/components/home/AppLauncher'
import 'src/agent/styles/agent.scss'

export default {
  mixins: [HomeMixin],
  components: {
    Sidebar,
    AgentHeader,
    AppLauncher,
  },
  data() {
    return { showLauncher: false }
  },
  async created() {
    this.$store.dispatch('loadOrgs')
    await this.$store.dispatch('getCurrentAccount')
    await this.$store.dispatch('getFeatureLicenses')
    await this.initializeMeta()
  },
  title() {
    return 'Ops Vision'
  },
  computed: {
    ...mapState({
      account: state => state.account,
    }),
    canShowAppLauncher() {
      const isDevMode = process.env.NODE_ENV === 'development'
      // ADD option to toggle this from app config
      return isDevMode
    },
    brandConfig()
    {
      return window.brandConfig
    },
  },
  watch: {
    account: function() {
      if (this.$refs['dashboardPopupView']) {
        Vue.prototype.$popupView = this.$refs['dashboardPopupView']
      } else {
        this.$nextTick(() => {
          Vue.prototype.$popupView = this.$refs['dashboardPopupView']
        })
      }
    },
  },
}
</script>
<style lang="scss" scoped>
.fc-agent-con {
  width: 100%;
  height: 100vh;
  overflow: hidden;
  position: relative;
  box-sizing: border-box;
  position: fixed;
  top: 0;
  right: 0;
  left: 0;
  z-index: 100;
  background: #f7f8f9;
  padding-left: 0 !important;
}
.fc-agent-main-con {
  height: calc(100vh - 60px);
  display: flex;
  flex-wrap: nowrap;
  flex-direction: row;
  flex: 0 0 100%;
  position: relative;
  overflow: hidden;
}
.fc-logo {
  width: 90px;
}
.launcher-icon {
  margin-left: -15px;
  margin-right: 15px;
}
</style>
