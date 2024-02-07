<template>
  <spinner v-if="loading" :show="loading"></spinner>
  <div v-else class="p30 pT30 d-flex flex-direction-column ml-deviation-card">
    <div
      class="f12 bold text-uppercase mB10 fc-black-13 text-left letter-spacing1"
    >
      DEVIATION
    </div>
    <div class="d-flex pT20">
      <div class="items-baseline width40">
        <div class="f18 bold">
          {{ deviation > 0 ? deviation.toFixed(2) : 0 }}
          <span class="pL5 f14 fw4">%</span>
        </div>
        <div class="secondary-text pT5">
          {{ $t('common.date_picker.this_month') }}
        </div>
      </div>
      <div class="f-el-divider"></div>
      <div class="items-baseline width50 pL10">
        <div class="f18 bold">
          {{ deviationLastMonth > 0 ? deviationLastMonth.toFixed(2) : 0 }}
          <span class="pL5 f14 fw4">%</span>
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
  mixins: [AnomalyMixin],
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
      deviation: 0,
      deviationLastMonth: 0,
      loading: false,
    }
  },
  mounted() {
    this.loadData()
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
        let deviationValues = this.$store.getters['anomalies/getDeviation']
        this.deviation = deviationValues[this.currentMonth]
        this.deviationLastMonth = deviationValues[this.lastMonth]
      }
    },
  },
}
</script>
<style lang="scss">
.ml-deviation-card .period {
  font-size: 12px;
  font-weight: normal;
}
.ml-deviation-card .featured {
  min-width: 170px;
  display: flex;
}
</style>
