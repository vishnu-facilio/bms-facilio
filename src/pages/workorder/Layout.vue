<template>
  <div
    id="padleft"
    class="height100 pL50 overflow-scroll-flex min-width0 min-height0"
  >
    <router-view></router-view>
  </div>
</template>
<script>
import { mapState } from 'vuex'
import { isEmpty } from 'util/validation'
export default {
  data() {
    return {
      etisalat: {
        code: 'wo',
        label: 'Work Order Management',
        path: '/app/wo',
        modules: [
          {
            label: this.$t('common.header.workorders'),
            path: { path: '/app/wo/orders' },
            icon: 'fa fa-clock-o',
            permission: 'workorder:READ,READ_TEAM,READ_OWN',
          },
          {
            label: this.$t('common.header.calender'),
            path: { path: '/app/wo/calendar' },
            icon: 'fa fa-calendar',
            permission: 'workorder:CALENDAR',
          },
        ],
      },
    }
  },
  mounted() {
    this.initProduct()
  },
  computed: {
    ...mapState({ account: state => state.account }),
    showPmV1() {
      let { $account } = this

      let ppmMigration = this.$getProperty(
        $account,
        'data.orgInfo.ppmMigration',
        {}
      )
      return !isEmpty(ppmMigration)
    },
    product() {
      return {
        code: 'wo',
        label: 'Work Order Management',
        path: '/app/wo',
        modules: [
          {
            label: this.$t('common.header.workorders'),
            path: { path: '/app/wo/orders' },
            icon: 'fa fa-clock-o',
            permission: 'workorder:READ,READ_TEAM,READ_OWN',
          },
          {
            label: this.$t('common.header.workpermit'),
            path: {
              path: '/app/wo/workpermit',
            },
            icon: 'fa fa-clock-o',
            license: 'WORK_PERMIT',
          },
          {
            label: this.$t('common.header.approvals'),
            path: {
              path: this.$helpers.isLicenseEnabled('NEW_APPROVALS')
                ? '/app/wo/newapprovals'
                : '/app/wo/approvals',
            },
            icon: 'fa fa-clock-o',
            permission: 'workorder:VIEW_APPROVAL',
          },
          {
            label: 'Surveys',
            path: {
              path: '/app/wo/surveys',
            },
            icon: 'fa fa-clock-o',
            license: 'SURVEY',
          },
          {
            label: this.$t('common.header.planned_maintenance'),
            path: {
              path: this.$helpers.isLicenseEnabled('PM_PLANNER')
                ? !this.showPmV1
                  ? '/app/wo/pm'
                  : '/app/wo/planned'
                : '/app/wo/planned',
            },
            icon: 'fa fa-clock-o',
            permission: 'planned:READ,READ_TEAM,READ_OWN',
          },
          {
            label: this.$t('common.header.jobplan'),
            path: { path: '/app/wo/jobplan' },
            permission: 'planned:READ,READ_TEAM,READ_OWN',
            license: 'PM_PLANNER',
          },
          {
            label: this.$t('common.header.calender'),
            path: { path: '/app/wo/calendar' },
            icon: 'fa fa-calendar',
            permission: 'workorder:CALENDAR',
          },
          {
            label: this.$t('common.header.resource_scheduler'),
            path: { path: '/app/wo/timeline' },
            license: 'RESOURCE_SCHEDULER',
          },
          {
            label: this.$t('common.header.reports'),
            path: { path: '/app/wo/reports' },
            icon: 'fa fa-bar-chart-o',
            permission: 'workorder:VIEW_REPORTS',
          },
        ],
      }
    },
  },
  watch: {
    $route(from, to) {
      this.initProduct()
    },
  },
  methods: {
    initProduct() {
      let { product, $store, $route, $router } = this
      let products = []
      if (this.$helpers.isEtisalat()) {
        products = this.etisalat
      } else {
        products = product
      }
      $store.dispatch('switchProduct', products)
      if ($route.path === '/app/wo') {
        $router.push(products.modules[0].path)
      }
    },
  },
}
</script>
