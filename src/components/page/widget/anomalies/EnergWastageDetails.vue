<template>
  <spinner v-if="loading" :show="loading"></spinner>
  <div v-else class="p30 pT30 d-flex flex-direction-column metrics-card">
    <div
      class="f12 bold text-uppercase mB10 fc-black-13 text-left letter-spacing1"
    >
      EXCEEDED ENERGY
    </div>
    <div class="d-flex pT20">
      <div class="items-baseline width40">
        <div class="f18 bold">
          {{ currentMonthWastage > 0 ? currentMonthWastage.toFixed(2) : 0 }}
          <span class="pL5 f14 fw4">{{ unit }}</span>
        </div>
        <div class="secondary-text pT5">
          {{ $t('common.date_picker.this_month') }}
        </div>
      </div>
      <div class="f-el-divider"></div>
      <div class="items-baseline width50 pL10">
        <div class="f18 bold">
          {{ lastMonthWastage > 0 ? lastMonthWastage.toFixed(2) : 0 }}
          <span class="pL5 f14 fw4">{{ lastMonthUnit }}</span>
        </div>
        <div class="letter-spacing0_5 fc-blue-label f12 text-capitalize pT5">
          Last Month
        </div>
      </div>
    </div>
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
  mixins: [AnomalyMixin],
  data() {
    return {
      metrics: {},
      currentMonthWastage: 0,
      unit: 'kWh',
      lastMonthWastage: 0,
      lastMonthUnit: 'kWh',
      loading: false,
    }
  },
  mounted() {
    this.loadData()

    this.eventBus.$on('asset-downtime-reported', () => {
      this.loadData()
    })
  },
  watch: {
    isMetricsApiLoading: function() {
      this.loading = this.isMetricsApiLoading
      this.loadData()
    },
  },
  computed: {},
  methods: {
    loadData() {
      // this.loading = true
      // this.$util
      //   .getWorkFlowResult(99, [
      //     [this.details.alarm.resource.id],
      //     this.details.alarm.id,
      //   ])
      //   .then(d => {
      if (!this.isMetricsApiLoading) {
        let exceedEnegy = this.$store.getters['anomalies/getEnergyWastage']
        this.currentMonthWastage = exceedEnegy[this.currentMonth]

        if (this.currentMonthWastage > 10000) {
          this.currentMonthWastage = this.currentMonthWastage / 1000
          this.unit = 'MWh'
        }
        this.lastMonthWastage = exceedEnegy[this.lastMonth]
        if (this.lastMonthWastage > 10000) {
          this.lastMonthWastage = this.lastMonthWastage / 1000
          this.lastMonthUnit = 'MWh'
        }
      }
      //   this.loading = false
      // })
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
