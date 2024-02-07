<template>
  <div class="p30 pT30 d-flex flex-direction-column ml-mean-metric-card">
    <spinner v-if="loading" :show="loading"></spinner>
    <template v-else>
      <div
        class="f12 bold text-uppercase mB10 fc-black-13 text-left letter-spacing1"
      >
        {{ title }}
      </div>
      <div class="d-flex pT20">
        <div class="items-baseline width50 pR8">
          <div class="flex-middle">
            <template v-for="(value, key, index) in stats.thisMonthData">
              <div class="f18 bold" :class="{ pL8: index > 0 }" :key="key">
                {{ value }}
                <span class="pL2 f12 fw4">{{ key }}</span>
              </div>
            </template>
          </div>
          <div class="secondary-text pT5">
            {{ $t('common.date_picker.this_month') }}
          </div>
        </div>

        <div class="items-baseline width50 pL15 border-left2">
          <div class="flex-middle">
            <template v-for="(value, key, index) in stats.lastMonthData">
              <div class="f18 bold" :class="{ pL8: index > 0 }" :key="key">
                {{ value }}
                <span class="pL2 f12 fw4">{{ key }}</span>
              </div>
            </template>
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

import { isEmpty } from '@facilio/utils/validation'
export default {
  props: ['isMttc', 'title', 'details', 'moduleName', 'cardId'],
  mixins: [AnomalyMixin],
  data() {
    return {
      metrics: {},
      loading: false,
    }
  },
  watch: {
    details: {
      handler(value, oldValue) {
        let { id } = value
        let { id: oldId = null } = oldValue || {}
        if (id !== oldId) this.loadData()
      },
      immediate: true,
    },
  },
  mounted() {
    // this.loadData()
  },
  computed: {
    stats() {
      if (isEmpty(this.metrics)) {
        return {
          thisMonthData: { Hrs: 0 },
          lastMonthData: { Hrs: 0 },
        }
      } else {
        return Object.entries(this.metrics).reduce((result, [key, value]) => {
          result[key] = this.$helpers.getDuration(value, 'seconds')
          return result
        }, {})
      }
    },
  },
  methods: {
    loadData() {
      if (this.details && this.details.alarm) {
        this.loading = true
        this.loadMeanMetrics()
          .then(response => {
            if (response) {
              if (this.isMttc) {
                this.metrics = {
                  thisMonthData: response.mttc,
                  lastMonthData: response.mttcLastMonth,
                }
              } else {
                this.metrics = {
                  thisMonthData: response.mtba,
                  lastMonthData: response.mtbaLastMonth,
                }
              }
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
<style lang="scss">
.ml-mean-metric-card .period {
  font-size: 12px;
  font-weight: 400;
  padding-left: 5px;
}
.ml-mean-metric-card.featured {
  min-width: 170px;
  display: flex;
}
</style>
