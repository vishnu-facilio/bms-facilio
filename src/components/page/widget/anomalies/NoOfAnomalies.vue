<template>
  <div class="p30 pT30 d-flex flex-direction-column metrics-card">
    <div
      class="f12 bold text-uppercase mB10 fc-black-13 text-left letter-spacing1"
    >
      NO OF OCCURRENCES
    </div>
    <div class="d-flex pT20">
      <div class="items-baseline width50">
        <div class="f18 bold">
          {{
            values && values.anomaliesThisMonth > 0
              ? values.anomaliesThisMonth
              : 0
          }}
        </div>
        <div class="secondary-text pT5">
          {{ $t('common.date_picker.this_month') }}
        </div>
      </div>
      <div class="f-el-divider"></div>
      <div class="items-baseline width50 pL40">
        <div class="f18 bold">
          {{
            values && values.anomaliesLastMonth > 0
              ? values.anomaliesLastMonth
              : 0
          }}
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
      values: null,
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
  methods: {
    loadData() {
      if (!this.isMetricsApiLoading) {
        this.values = {}
        let noOfAnomalies = this.$store.getters['anomalies/getNoOfAnomalies']
        this.values.anomaliesThisMonth = noOfAnomalies[this.currentMonth]
        this.values.anomaliesLastMonth = noOfAnomalies[this.lastMonth]
      }
      // this.loadAnomaliesCount([this.details.alarm.resource.id]).then(d => {
      //   if (d && d[this.details.alarm.resource.id]) {
      //     this.values = d[this.details.alarm.resource.id]
      //   }
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
