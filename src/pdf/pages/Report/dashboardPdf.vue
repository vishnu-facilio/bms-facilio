<template>
  <div class="dashboard-pdf-page" v-if="appName">
    <DashboardPicker type="viewer" v-if="mountDashboard" />
  </div>
</template>
<script>
import Helpers from 'util/helpers.js'
import Util from 'src/util/index.js'
import DashboardPicker from 'src/pages/new-dashboard/components/dashboard/DashboardPicker'
import { mapGetters } from 'vuex'
import Vue from 'vue'
import { getApp } from '@facilio/router'
Vue.prototype.$helpers = Helpers

export default {
  data() {
    return {
      mountDashboard: false,
      appName: getApp().linkName,
    }
  },
  components: {
    DashboardPicker,
  },
  computed: {
    ...mapGetters(['currentAccount']),
    linkName() {
      return this.$route?.params?.linkName === this.appName
        ? this.$route?.params?.linkName
        : ''
    },
  },
  async created() {
    const self = this
    await self.$store.dispatch('getFeatureLicenses')
    await self.$store.dispatch('getCurrentAccount')
    Vue.use(Util, {
      account: self.currentAccount,
    })
    self.mountDashboard = true
  },
}
</script>

<style lang="scss">
.dashboard-pdf-page {
  .subheader-section {
    display: none;
  }
  .pm-pdf-heading {
    font-size: 14px !important;
  }
}
</style>
