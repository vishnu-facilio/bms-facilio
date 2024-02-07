<template>
  <component :is="specificComponent"></component>
</template>

<script>
import { mapState } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'

export default {
  components: {
    AllDashboard: () => import('src/PortalTenant/dashboard/DashboardLayout'),
    SpecifiedDashboard: () =>
      import('src/OperationalVisibility/DashboardLayout'),
  },
  computed: {
    ...mapState('webtabs', {
      currentTab: state => state.selectedTab,
    }),
    specificComponent() {
      let { config } = this.currentTab || {}

      if (isEmpty(config)) return 'AllDashboard'
      else return 'SpecifiedDashboard'
    },
  },
}
</script>
