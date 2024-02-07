<template>
  <component :is="component" :printMode="printMode"></component>
</template>

<script>
export default {
  name: 'DashboardPicker',
  components: {
    NewDashboardEditer: () =>
      import(
        'src/pages/new-dashboard/components/dashboard/DashboardEditer.vue'
      ),
    OldDashboardEditer: () => import('src/pages/dashboard/DashboardEditer.vue'),
    OldDashboardViewer: () =>
      import('src/pages/dashboard/NewDashboardLayout.vue'),
    NewDashboardViewer: () =>
      import(
        'src/pages/new-dashboard/components/dashboard/DashboardLayout.vue'
      ),
  },
  props: {
    type: {
      type: String,
      required: true,
    },
  },
  computed: {
    printMode() {
      return this?.$route?.query?.printing == 'true'
    },
    component() {
      const isNewDashboardEnabled = this.$helpers.isLicenseEnabled('NEW_DASHBOARD_FLOW')
      if (this.type == 'editer') {
        return isNewDashboardEnabled
          ? 'NewDashboardEditer'
          : 'OldDashboardEditer'
      } else if (this.type == 'viewer') {
        return isNewDashboardEnabled
          ? 'NewDashboardViewer'
          : 'OldDashboardViewer'
      } else {
        return ''
      }
    },
  },
}
</script>

<style lang="scss" scoped></style>
