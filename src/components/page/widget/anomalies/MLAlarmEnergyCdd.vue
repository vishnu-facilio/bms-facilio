<template>
  <div class="p30 pT30 d-flex flex-direction-column metrics-card">
    <spinner v-if="loading" :show="loading"></spinner>
    <template v-else>
      <div
        class="f12 bold text-uppercase mB10 fc-black-13 text-left letter-spacing1"
      >
        ENERGY/CDD
      </div>
      <div class="d-flex pT20">
        <div class="items-baseline width50 pR8">
          <div class="f18 bold">
            {{ thisMonthData || 0 }}
            <span class="pL5 f12 fw4">kWh/CDD</span>
          </div>
          <div class="secondary-text pT5">This Month</div>
        </div>

        <div class="items-baseline width50 pL10 border-left2">
          <div class="f18 bold">
            {{ lastMonthData || 0 }}
            <span class="pL5 f12 fw4">kWh/CDD</span>
          </div>
          <div class="letter-spacing0_5 fc-blue-label f12 text-capitalize pT5">
            Last Month
          </div>
        </div>
      </div>
    </template>
  </div>
</template>
<script>
import AnomalyMixin from '@/mixins/AnomalyMixin'

export default {
  props: [
    'details',
    'layoutParams',
    'hideTitleSection',
    'activeTab',
    'widget',
    'resizeWidget',
    'eventBus',
  ],
  data() {
    return {
      thisMonthData: 0,
      lastMonthData: 0,
      loading: false,
    }
  },
  mounted() {
    this.loadData()
  },
  mixins: [AnomalyMixin],
  computed: {},
  watch: {
    isMetricsApiLoading: function() {
      this.loading = this.isMetricsApiLoading
      this.loadData()
    },
  },
  methods: {
    loadData() {
      // this.loading = true
      // if (this.details && this.details.alarm) {
      //   let resourceId = this.details.alarm.resource.id
      //   this.$util
      //     .getWorkFlowResult('49', [
      //       [resourceId],
      //       this.details.alarm.resource.siteId,
      //     ])
      //     .then(response => {
      //       this.loading = false
      // if (response) {
      if (!this.isMetricsApiLoading) {
        let energyCdd = this.$store.getters['anomalies/getEnergyByCdd']
        this.thisMonthData = this.$getProperty(
          energyCdd[this.currentMonth],
          'value'
        )
        if (this.thisMonthData > 0) {
          this.thisMonthData = this.thisMonthData.toFixed(2)
        }
        this.lastMonthData = this.$getProperty(
          energyCdd[this.lastMonth],
          'value'
        )
        if (this.lastMonthData > 0) {
          this.lastMonthData = this.lastMonthData.toFixed(2)
        }
      }
      // }
      // })
      // .catch(() => {
      //   this.loading = false
      // })
      // }
    },
  },
}
</script>
<style lang="scss">
.metrics-card .period {
  font-size: 12px;
  font-weight: normal;
}
.metrics-card .featured {
  min-width: 170px;
  display: flex;
}
</style>
