<template>
  <div class="height100">
    <router-view></router-view>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
export default {
  data() {
    return {
      product: {
        code: 'fa',
        label: 'Fire Alarm System',
        path: '/app/fa',
        modules: [
          {
            label: this.$t('common.header.faults'),
            path: { path: '/app/fa/faults' },
            svgicon: 'fire',
            permission: 'alarm:READ',
          },
          // {
          //   label: this.$t('common.header.alarms'),
          //   path: { path: '/app/fa/faults' },
          //   svgicon: 'fire',
          //   permission: 'alarm:READ',
          // },
          {
            label: this.$t('common.header.outOfSchedules'),
            path: { path: '/app/fa/outofschedule' },
            permission: 'alarm:READ',
            license: 'OPERATIONAL_ALARM',
          },
          {
            label: this.$t('common.header.anomalies'),
            path: { path: '/app/fa/anomalies' },
            permission: 'alarm:READ',
            license: 'ANOMALY',
          },
          {
            label: this.$t('common.header.sensorFault'),
            path: { path: '/app/fa/sensoralarms' },
            svgicon: 'fire',
            permission: 'alarm:READ',
            license: 'SENSOR_RULE',
          },
          {
            label: this.$t('common.header.bmsAlarms'),
            path: { path: '/app/fa/bmsalarms' },
            svgicon: 'fire',
            permission: 'alarm:READ',
          },
          {
            label: this.$t('common.header.rules'),
            path: { path: '/app/fa/rules' },
            permission: 'alarmrules:READ',
            hide_if_license: 'NEW_READING_RULE',
          },
          {
            label: this.$t('common.header.rules'),
            path: { path: '/app/fa/newrules' },
            permission: 'alarmrules:READ',
            license: 'NEW_READING_RULE',
          },
          {
            label: this.$t('common.header.reports'),
            path: { path: '/app/fa/reports' },
            icon: 'fa fa-bar-chart-o',
            permission: 'alarm:VIEW_REPORTS',
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
  computed: {
    modules() {
      let self = this
      let { currentProduct = {} } = this.$store.state
      let { modules } = currentProduct || {}

      if (!isEmpty(modules)) {
        let accessibleModules = modules.filter(function(m) {
          if (typeof m.permission === 'undefined') {
            return m
          } else if (self.$hasPermission(m.permission)) {
            return m
          }
        })
        return accessibleModules
      }
      return []
    },
  },
  methods: {
    initProduct() {
      let { product } = this
      // temp
      if (this.$account.user.email === 'jasonsmith@sutherland.com') {
        product.modules = product.modules.filter(
          m => m.path.path !== '/app/fa/anomalies'
        )
      }
      // will be removed once webtab is enabled
      else if (this.$org.id === 339) {
        product.modules = product.modules.filter(
          m => m.path.path !== '/app/fa/bmsalarms'
        )
      }
      this.$store.dispatch('switchProduct', product)
      if (this.$route.path === '/app/fa') {
        this.$router.push(this.modules[0].path)
      }
    },
  },
}
</script>
