<template>
  <div v-if="!localloading">
    <default-portal-dashboard
      v-if="dashboardLink === 'home'"
    ></default-portal-dashboard>
    <mobiledashboard class="mobiledashboard" v-else></mobiledashboard>
  </div>
</template>
<script>
import mobiledashboard from './MobileDashboardViewer'
import DefaultPortalDashboard from 'pages/dashboard/DefaultMobilePortalDashboard'
import OutApp from 'src/OuterAppUtil/OuterAppHelper'
import '../../assets/styles/mobiledashboard.scss'
export default {
  mixins: [OutApp],
  props: ['isportal', 'currentDashboard'],
  components: {
    mobiledashboard,
    DefaultPortalDashboard,
  },
  mounted() {
    this.loadQData()
  },
  computed: {
    dashboardLink() {
      if (this.currentDashboard) {
        return this.currentDashboard.linkName
      } else {
        if (
          this.$route.params.dashboardlink === 'residentialbuildingdashboard' ||
          this.$route.params.dashboardlink === 'commercialbuildingdashboard'
        ) {
          return 'buildingdashboard'
        } else {
          return this.$route.params.dashboardlink
        }
      }
    },
  },
}
</script>

<style>
@import '../../assets/styles/c3.css';
</style>
<style>
@import '../../assets/styles/mobile-app.scss';
</style>
