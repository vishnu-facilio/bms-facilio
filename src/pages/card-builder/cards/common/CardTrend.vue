<template>
  <div>
    <sparkline
      v-if="chartrender"
      :height="option.height"
      :width="option.width"
      :tooltipProps="trend.tooltipProps"
    >
      <sparklineCurve
        :data="trend.data"
        :limit="trend.data.length"
        :styles="trend.style"
        :textStyles="trend.label"
      />
    </sparkline>
  </div>
</template>
<script>
import Sparkline from 'newcharts/sparklines/Sparkline'
import formatter from 'charts/helpers/formatter'
import {
  dateOperators,
  aggregateFunctions,
} from 'pages/card-builder/card-constants'
export default {
  props: [
    'trendOption',
    'widget',
    'widgetMeta',
    'cardData',
    'cardParams',
    'trendColor',
  ],
  components: { Sparkline },
  data() {
    return {
      aggregateFunctions,
      chartrender: false,
      trend: {
        data: [],
        style: {
          stroke: '#54a5ff',
          fill: '#54a5ff',
        },
        tooltipProps: {
          formatter(val) {
            return `<label style="color:#fff;font-weight:bold;">${val.value}</label>`
          },
        },
      },
      dateOperators,
    }
  },

  mounted() {
    this.prepareTrend()
    this.chartrender = true
  },
  computed: {
    option() {
      if (this.widgetMeta) {
        return this.widgetMeta
      } else {
        return {
          width: 100,
          height: 70,
        }
      }
    },
    report() {
      if (this.cardData && this.cardData.trend) {
        return this.cardData.trend
      }
      return null
    },
  },
  methods: {
    prepareTrend() {
      this.trend.data = this.prepareTrendData(this.report)
      this.trend.labelArray = this.prepareTrendX(this.report)
      this.trend.tooltipProps = {
        formatter: val => {
          let { trend } = this
          let { cardParams } = this
          let { cardData } = this
          let { dateOperators } = this
          let data = `<div style="padding:3px;"><div><label>${formatter.formatCardTime(
            trend.labelArray[val.index],
            cardParams.trend.xAggr,
            dateOperators.find(rt => rt.value === cardParams.dateRange)
              .enumValue
          )}</label></div>`
          data += `<div>`
          data += `<label style="color:#fff;font-weight:bold;">${val.value}</label>`
          if (cardData.value !== null && cardData.value.unit) {
            data += `<label> ${cardData.value.unit}</label>`
          }
          data += `</div></div>`
          return data
        },
      }
      this.applyStyle()
    },
    prepareTrendData(report) {
      if (report && report.reportData && report.reportData.data) {
        return report.reportData.data.map(rt => Number(rt['A']))
      }
      return []
    },
    prepareTrendX(report) {
      if (report && report.reportData && report.reportData.data) {
        return report.reportData.data.map(rt => Number(rt['X']))
      }
      return []
    },
    applyStyle() {
      this.trend.style.stroke = this.trendColor || '#54a5ff'
      this.trend.style.fill = this.trendColor || '#54a5ff'
    },
  },
}
</script>
