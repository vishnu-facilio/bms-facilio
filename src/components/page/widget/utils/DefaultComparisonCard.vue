<template>
  <div class="p30 pT30 d-flex flex-direction-column ml-mean-metric-card">
    <spinner v-if="loading" :show="loading"></spinner>
    <template v-else>
      <div
        class="f12 bold text-uppercase mB10 fc-black-13 text-left letter-spacing1"
      >
        {{ widget.title }}
      </div>
      <div
        v-if="widget.widgetParams.dataType === 'duration'"
        class="d-flex pT20"
      >
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
            {{ widget.widgetParams ? widget.widgetParams.primaryTitle : '' }}
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
            {{ widget.widgetParams ? widget.widgetParams.secondaryTitle : '' }}
          </div>
        </div>
      </div>
      <div
        v-else-if="widget.widgetParams.dataType === 'rank'"
        class="d-flex pT20"
      >
        <div class="items-baseline width50 pR8">
          <div class="flex-middle">
            <template>
              <div v-if="resultValue.current > 0" class="f18 bold pL8">
                {{ resultValue.current }} /
                {{ resultValue.outOfCurrent }}
              </div>
              <div class="f18 bold pL8" v-else>
                --
              </div>
            </template>
          </div>
          <div class="secondary-text pT5">
            {{ widget.widgetParams ? widget.widgetParams.primaryTitle : '' }}
          </div>
        </div>

        <div class="items-baseline width50 pL15 border-left2">
          <div class="flex-middle">
            <template>
              <div v-if="resultValue.previous > 0" class="f18 bold pL8">
                {{ resultValue.previous }} /
                {{ resultValue.outOfPrevious }}
              </div>
              <div class="f18 bold pL8" v-else>
                --
              </div>
            </template>
          </div>
          <div class="letter-spacing0_5 fc-blue-label f12 text-capitalize pT5">
            {{ widget.widgetParams ? widget.widgetParams.secondaryTitle : '' }}
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
  props: ['isMttc', 'title', 'details', 'moduleName', 'cardId', 'widget'],
  mixins: [AnomalyMixin],
  data() {
    return {
      metrics: {},
      loading: false,
      resultValue: {},
    }
  },
  mounted() {
    if (this.widget.widgetParams.workflowId) {
      this.executeworkflow()
      return
    }
    this.loadData()
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
              this.metrics = {
                thisMonthData: response[this.widget.widgetParams.primaryKey],
                lastMonthData: response[this.widget.widgetParams.secondaryKey],
              }
            }
            this.loading = false
          })
          .catch(() => {
            this.loading = false
          })
      }
    },
    executeworkflow() {
      if (this.details && this.details.alarm) {
        let widgetDetail = this.widget.widgetParams
        this.$util
          .getWorkFlowResult(this.widget.widgetParams.workflowId, [
            this.details.alarm.id,
          ])
          .then(data => {
            if (widgetDetail.dataType === 'duration') {
              this.metrics = {
                thisMonthData: data[widgetDetail.primaryKey],
                lastMonthData: data[widgetDetail.secondaryKey],
              }
            } else if (widgetDetail.dataType === 'rank') {
              this.$set(
                this.resultValue,
                'current',
                data[widgetDetail.primaryKey]
              )
              this.$set(
                this.resultValue,
                'outOfCurrent',
                data[widgetDetail.primaryKeyOutOf]
              )
              this.$set(
                this.resultValue,
                'previous',
                data[widgetDetail.secondaryKey]
              )
              this.$set(
                this.resultValue,
                'outOfPrevious',
                data[widgetDetail.secondaryKeyOutOf]
              )
            }
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
