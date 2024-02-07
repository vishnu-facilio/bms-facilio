<template>
  <div class="height100 pL50">
    <router-view></router-view>
  </div>
</template>
<script>
export default {
  data() {
    return {
      product: {
        code: 'em',
        label: 'Energy Management',
        path: '/app/em',
        modules: [
          {
            label: this.$t('common.header.dashboard'),
            path: {
              path: this.isNewDashboardEnabled()
                ? '/app/em/newdashboard'
                : '/app/em/dashboard',
            },
            icon: 'fa fa-tachometer',
          },
          {
            label: this.$t('common.header.analytics'),
            path: { path: '/app/em/analytics' },
            icon: 'fa fa-bar-chart-o',
            permission: 'energy:VIEW_REPORTS',
          },
          {
            label: this.$t('common.header.m&v'),
            path: { path: '/app/em/mv' },
            icon: 'fa fa-bar-chart-o',
            license: 'M_AND_V',
            hide_if_license: 'ENERGY_STAR_INTEG',
          },
          {
            label: this.$t('common.header.graphics'),
            path: { path: '/app/em/graphics' },
            icon: 'fa fa-bar-chart-o',
            license: 'GRAPHICS',
          },
          {
            label: this.$t('common.header.reports'),
            path: { path: '/app/em/reports' },
            icon: 'fa fa-bar-chart-o',
            permission: 'energy:VIEW_REPORTS',
          },
          // {
          //   label: 'Leed',
          //   path: {path: '/app/em/leeds'},
          //   svgicon: 'leed',
          //   permission: 'WORKORDER_ACCESS_READ_ACCESSIBLE_SPACES,WORKORDER_ACCESS_READ_ANY'
          // }
          // ,
          // {
          //   label: 'Reports',
          //   path: {path: '/app/em/reports'},
          //   icon: 'fa fa-bar-chart-o',
          //   permission: 'WORKORDER_ACCESS_READ_ACCESSIBLE_SPACES,WORKORDER_ACCESS_READ_ANY'
          // }
        ],
      },
      etisalat: {
        code: 'em',
        label: 'Energy Management',
        path: '/app/em',
        modules: [
          {
            label: this.$t('common.header.analytics'),
            path: { path: '/app/em/analytics' },
            icon: 'fa fa-bar-chart-o',
            permission: 'energy:VIEW_REPORTS',
          },
          {
            label: 'READING REPORTS',
            path: { path: '/app/em/reports' },
            icon: 'fa fa-bar-chart-o',
            permission: 'energy:VIEW_REPORTS',
          },
          {
            label: 'MODULE REPORTS',
            path: { path: '/app/em/modulereports' },
            icon: 'fa fa-bar-chart-o',
            permission: 'energy:VIEW_REPORTS',
          },
        ],
      },
      newproduct: {
        code: 'em',
        label: 'Energy Management',
        path: '/app/em',
        modules: [
          // {
          //   label: this.$t('common.header.dashboard'),
          //   path: {path: this.isNewDashboardEnabled() ? '/app/em/newdashboard' : '/app/em/dashboard'},
          //   icon: 'fa fa-tachometer'
          // },
          {
            label: this.$t('common.header.analytics'),
            path: { path: '/app/em/analytics' },
            icon: 'fa fa-bar-chart-o',
            permission: 'energy:VIEW_REPORTS',
          },
          ...(this.$helpers.isLicenseEnabled('KPI')
            ? [
                {
                  label: this.$t('common.header.kpi'),
                  path: { path: '/app/em/kpi' },
                  icon: 'fa fa-bar-chart-o',
                  permission: 'energy:VIEW_REPORTS',
                },
              ]
            : []),
          ...(this.$helpers.isLicenseEnabled('NEW_KPI')
            ? [
                {
                  label: this.$t('common.header.new_kpi'),
                  path: { path: '/app/em/readingKpi' },
                  icon: 'fa fa-bar-chart-o',
                  permission: 'energy:VIEW_REPORTS',
                },
              ]
            : []),
          {
            label: this.$t('common.header.pivot'),
            path: { path: '/app/em/pivot/view' },
            icon: 'fa fa-bar-chart-o',
            permission: 'energy:VIEW_REPORTS',
          },
          {
            label: this.$t('common.header.m&v'),
            path: { path: '/app/em/mv' },
            icon: 'fa fa-bar-chart-o',
            license: 'M_AND_V',
            hide_if_license: 'ENERGY_STAR_INTEG',
          },
          {
            label: this.$t('common.header.reports'),
            path: { path: '/app/em/reports' },
            icon: 'fa fa-bar-chart-o',
            permission: 'energy:VIEW_REPORTS',
          },
        ],
      },
    }
  },
  mounted() {
    this.initProduct()
  },
  watch: {
    $route(from, to) {
      this.initProduct()
    },
  },
  methods: {
    isNewDashboardEnabled() {
      if (
        this.$account.data.orgInfo &&
        this.$account.data.orgInfo.newdashboard &&
        this.$account.data.orgInfo.newdashboard.split(',').indexOf('em') >= 0
      ) {
        return true
      }
      return false
    },
    initProduct() {
      let product = []
      if (this.$helpers.isEtisalat()) {
        product = this.etisalat
      } else if (this.$helpers.isLicenseEnabled('NEW_LAYOUT')) {
        product = this.newproduct
      } else {
        product = this.product
      }
      this.$store.dispatch('switchProduct', product)
      if (this.$route.path === '/app/em') {
        if (this.$helpers.isLicenseEnabled('NEW_LAYOUT')) {
          this.$router.push({
            path: this.isNewDashboardEnabled()
              ? '/app/em/analytics'
              : '/app/em/analytics',
          })
        } else {
          this.$router.push({
            path: this.isNewDashboardEnabled()
              ? '/app/em/newdashboard'
              : '/app/em/dashboard',
          })
        }
      }
    },
  },
}
</script>
<style></style>
