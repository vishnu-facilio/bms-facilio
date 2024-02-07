<template>
  <div class="p30 pT30 d-flex flex-direction-column ml-mean-metric-card">
    <spinner v-if="loading" :show="loading"></spinner>
    <template v-else>
      <div
        class="f12 bold text-uppercase mB10 fc-black-13 text-left letter-spacing1"
      >
        No of Occurrences
      </div>
      <div class="d-flex pT20">
        <div class="items-baseline width50 pR8">
          <div class="flex-middle">
            <div class="f18 bold">
              {{ metrics.thisMonthData }}
            </div>
          </div>
          <div class="secondary-text pT5">
            {{ $t('common.date_picker.this_month') }}
          </div>
        </div>

        <div class="items-baseline width50 pL15 border-left2">
          <div class="flex-middle">
            <div class="f18 bold">
              {{ metrics.lastMonthData }}
            </div>
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
  mixins: [AnomalyMixin],
  props: [
    'details',
    'layoutParams',
    'moduleName',
    'hideTitleSection',
    'activeTab',
    'widget',
    'resizeWidget',
    'eventBus',
  ],
  data() {
    return {
      cardId: '1',
      loading: false,
      metrics: { lastMonthData: 0, thisMonthData: 0 },
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    loadData() {
      if (this.details && this.details.alarm) {
        this.loading = true
        this.loadMeanMetrics()
          .then(response => {
            if (response) {
              ;(this.metrics.thisMonthData = response.noOfOcc),
                (this.metrics.lastMonthData = response.noOfOccLastMonth)
            }
            this.loading = false
          })
          .catch(() => {
            this.loading = false
          })
      }
    },
  },
}
</script>
