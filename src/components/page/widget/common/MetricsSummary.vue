<template>
  <div class="p30 pT30 d-flex flex-direction-column metrics-card">
    <div
      class="f12 bold text-uppercase mB10 fc-black-13 text-left letter-spacing1"
    >
      MTBF
    </div>
    <div class="d-flex items-baseline">
      <div class="featured">
        <span class="f26" v-html="stats.mtbf"></span>
        <inline-svg
          src="svgs/arrow"
          class="d-flex items-end"
          :iconClass="
            `${getTrendClasses(
              metrics.mtbf,
              metrics.mtbfTillLastMonth
            )} icon arrow mL10 mb5`
          "
        ></inline-svg>
      </div>
      <span class="f13 letter-spacing0_5">
        <span
          class="fwBold letter-spacing0_5"
          v-html="stats.mtbfTillLastMonth"
        ></span>
        till last month
      </span>
    </div>
    <div class="letter-spacing0_5 fc-blue-label f12 mT5 text-capitalize">
      This Year
    </div>
    <hr class="separator-line width100" />
    <div
      class="f13 bold text-uppercase mB10 text-left fc-black-13 letter-spacing1"
    >
      MTTR
    </div>
    <div class="d-flex items-baseline">
      <div class="featured">
        <span class="f26" v-html="stats.mttr"></span>
        <inline-svg
          src="svgs/arrow"
          class="d-flex items-end"
          :iconClass="
            `${getTrendClasses(
              metrics.mttr,
              metrics.mttrTillLastMonth
            )} icon arrow mL10 mb5`
          "
        ></inline-svg>
      </div>
      <span class="f13 letter-spacing0_5">
        <span
          class="fwBold letter-spacing0_5"
          v-html="stats.mttrTillLastMonth"
        ></span>
        till last month
      </span>
    </div>
    <div class="letter-spacing0_5 fc-blue-label f12 mT5 text-capitalize">
      This Year
    </div>
    <hr class="separator-line width100" />
    <div
      class="f12 bold text-uppercase mB10 fc-black-13 text-left letter-spacing1"
    >
      Downtime
    </div>
    <div class="d-flex items-baseline">
      <div class="featured">
        <span class="f26" v-html="stats.downtime"></span>
        <inline-svg
          src="svgs/arrow"
          class="d-flex items-end"
          :iconClass="
            `${getTrendClasses(
              metrics.downtime,
              metrics.downtimeTillLastMonth
            )} icon arrow mL10 mb5`
          "
        ></inline-svg>
      </div>
      <span class="f13 letter-spacing0_5">
        <span
          class="fwBold letter-spacing0_5"
          v-html="stats.downtimeTillLastMonth"
        ></span>
        till last month
      </span>
    </div>
    <div class="letter-spacing0_5 fc-blue-label f12 mT5 text-capitalize">
      This Year
    </div>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import moment from 'moment-timezone'
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
      metrics: {},
    }
  },
  mounted() {
    this.loadData()

    this.eventBus.$on('asset-downtime-reported', () => {
      this.loadData()
    })
  },
  computed: {
    stats() {
      if (isEmpty(this.metrics)) {
        return {
          downtime: '00:00 <span class="period">Hrs</span>',
          downtimeTillLastMonth: '00:00 <span class="period">Hrs</span>',
          mtbf: '00:00 <span class="period">Hrs</span>',
          mtbfTillLastMonth: '00:00 <span class="period">Hrs</span>',
          mttr: '00:00 <span class="period">Hrs</span>',
          mttrTillLastMonth: '00:00 <span class="period">Hrs</span>',
        }
      } else {
        return Object.entries(this.metrics).reduce((result, [key, value]) => {
          result[key] = this.getFormattedDuration(value, 'seconds')
          return result
        }, {})
      }
    },
  },
  methods: {
    loadData() {
      let url = `v2/assets/downtimemetrics?assetId=${this.details.id}`
      this.$http
        .get(url)
        .then(response => {
          if (response.data.responseCode === 0) {
            this.metrics = !isEmpty(response.data.result.metrics)
              ? response.data.result.metrics
              : {}
          } else {
            // TODO handle errors
            this.metrics = {}
          }
        })
        .catch(() => {
          // TODO handle errors
          this.metrics = {}
        })
    },
    getFormattedDuration(value, format) {
      if (!value) return '00:00 <span class="period">Hrs</span>'

      let duration = moment.duration(parseInt(value, 10), format)
      let days = parseInt(duration.asDays(), 10)
      let hours = duration.hours()
      let minutes = duration.minutes()
      let seconds = duration.seconds()

      const pad = val => String(val).padStart(2, '0')

      if (days > 0) {
        return hours
          ? `${pad(days)} <span class="period">Days</span> ${pad(
              hours
            )} <span class="period">Hrs</span>`
          : `${pad(days)} <span class="period">Days</span>`
      } else if (hours > 0) {
        return minutes
          ? `${pad(hours)} <span class="period">Hrs</span> ${pad(
              minutes
            )} <span class="period">Mins</span>`
          : `${pad(hours)} <span class="period">Hrs</span>`
      } else if (minutes > 0) {
        return `${pad(minutes)}:${pad(
          seconds
        )} <span class="period">Mins</span>`
      } else {
        return `${pad(seconds)} <span class="period">Secs</span>`
      }
    },
    getTrendClasses(durationA, durationB) {
      if (
        isEmpty(durationA) ||
        isEmpty(durationB) ||
        (durationA === 0 && durationB === 0)
      )
        return 'hide-v'
      return durationA - durationB ? 'fill-green' : 'fill-red rotate-bottom'
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
