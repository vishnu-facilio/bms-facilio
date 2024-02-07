<template>
  <div>
    <div
      v-if="chartContext && options.settings.chart !== false"
      style="text-align: center;"
    >
      <div>
        <div v-if="showAlarms && showAlarmTitle" class="fc-alarms-chart-title">
          Alarms
        </div>
        <div ref="alarmsChartEle" class="fc-alarms-chart pdf-chart"></div>
      </div>
      <f-mobile-wo-chart-legends
        ref="mobilechartLegend"
        v-if="
          c3.chart &&
            chartContext.options.legend.show &&
            modulename === 'workorder'
        "
        :chart="c3.chart"
        :options="options"
        @mobiledrilldown="mobiledrilldown"
        :reportVarianceData="resultObj ? resultObj.reportData.aggr : null"
        :chartContext="chartContext"
        :resultObj="resultObj"
        :xValueMode="chartContext.xValueMode"
        :xLabelMap="chartContext.xLabelMap"
      ></f-mobile-wo-chart-legends>
      <f-mobile-chart-legends
        ref="mobilechartLegend"
        v-else-if="c3.chart && chartContext.options.legend.show"
        :chart="c3.chart"
        :options="options"
        @mobiledrilldown="mobiledrilldown"
        :reportVarianceData="resultObj ? resultObj.reportData.aggr : null"
        :chartContext="chartContext"
        :resultObj="resultObj"
        :xValueMode="chartContext.xValueMode"
        :xLabelMap="chartContext.xLabelMap"
      ></f-mobile-chart-legends>
      <div class="fc-newchart-container">
        <div
          ref="newChartEle"
          class="fc-new-chart pdf-chart"
          :class="{
            'hide-y2-axis':
              options && options.axis && options.axis.showy2axis === false,
          }"
        ></div>
      </div>
    </div>
  </div>
</template>
<script>
import { bb } from 'billboard.js'
import basechart from 'newcharts/mixins/basechart'
import c3helper from '../helpers/c3-helper'
import FMobileChartLegends from './FMobileChartLegends'
import FMobileWoChartLegends from './FMobileWoChartLegends'
import JumpToHelper from '@/mixins/JumpToHelper'
import tooltip from '@/graph/mixins/tooltip'
import colorHelper from 'newcharts/helpers/color-helper'

export default {
  mixins: [basechart, JumpToHelper],
  props: ['resultObj', 'config', 'moduleName', 'reportType'],
  components: {
    FMobileChartLegends,
    FMobileWoChartLegends,
  },
  data() {
    return {
      showAlarms: false,
      chartContext: null,
      chartId: Math.random()
        .toString(36)
        .substr(2, 9),
      c3: {
        params: null,
        chart: null,
      },
      mobile: {
        data: null,
      },
    }
  },
  mounted() {
    this.render()
  },
  beforeDestroy() {
    tooltip.hideTooltip()
    if (this.c3 && this.c3.chart) {
      this.c3.chart.destroy()
    }

    this.chartContext = null
    this.c3.params = null
    this.c3.chart = null
  },
  computed: {
    modulename() {
      if (this.reportType && this.reportType === 2) {
        return 'workorder'
      } else if (this.reportType && this.reportType === 1) {
        return 'energydata'
      } else if (
        this.chartContext &&
        this.chartContext.options &&
        this.chartContext.options.dataPoints
      ) {
        if (
          this.chartContext.options.dataPoints.length &&
          this.chartContext.options.dataPoints[0]
        ) {
          let modulename = this.chartContext.options.dataPoints[0].moduleName
          return modulename && modulename !== 'energydata'
            ? 'workorder'
            : 'energydata'
        }
      }
      return 'energydata'
    },
    showAlarmTitle() {
      if (this.chartContext && this.chartContext.options) {
        return !this.chartContext.options.hideAlarmTitle
      }
      return true
    },
  },
  methods: {
    mobiledrilldown(data) {
      this.$emit('mobiledrilldown', data)
    },
    render() {
      if (this.data) {
        this.chartContext = null

        this.$nextTick(() => {
          this.chartContext = {}
          this.chartContext.options = this.getOptions()
          this.chartContext.options.axis.x.culling = {
            max: 4,
          }
          this.chartContext.options.isMobile = true
          this.chartContext.dateRange = this.dateRange
          this.chartContext.data = this.data
          this.chartContext.xValueMode = this.isXValueMode(
            this.chartContext.options,
            this.data
          )
          this.chartContext.unitMap = this.getUnitMap(this.options.dataPoints)
          this.chartContext.enumMap = this.getEnumMap(this.options.dataPoints)
          this.showAlarms =
            this.chartContext.options.common.mode === 1 &&
            this.chartContext.options.settings.alarm
          if (this.showAlarms) {
            this.chartContext.alarms = this.alarms
            this.chartContext.options.size.height =
              this.chartContext.options.size.height - 100
          }
          this.chartContext.tooltipCallback = (d, config, title) => {
            if (this.$refs['mobilechartLegend']) {
              this.$refs['mobilechartLegend'].showTooltip(d, config, title)
            }
          }
          this.chartContext.tooltipHideCallback = () => {
            if (this.$refs['mobilechartLegend']) {
              this.$refs['mobilechartLegend'].hideTooltip()
            }
          }

          let c3Params = c3helper.prepare(this.chartContext)
          if (
            this.$route.meta.source &&
            this.$route.meta.source === 'analytics' &&
            document.getElementsByClassName('newanalytics-generater')[0]
          ) {
            c3Params.size.height =
              c3Params.data && c3Params.data.booleanChart
                ? document.getElementsByClassName('newanalytics-generater')[0]
                    .offsetHeight * 0.1
                : document.getElementsByClassName('newanalytics-generater')[0]
                    .offsetHeight * 0.6
          }
          if (
            this.$route.query &&
            this.$route.query.reportId &&
            document.getElementById('q-app')
          ) {
            c3Params.size.width =
              document.getElementById('q-app').offsetWidth - 30
            c3Params.size.height =
              c3Params.data && c3Params.data.booleanChart
                ? document.getElementById('q-app').offsetHeight *
                  (this.$route.query &&
                  this.$route.query &&
                  this.$route.query.chartHeight
                    ? Number(this.$route.query.chartHeight) / 100
                    : 0.1)
                : document.getElementById('q-app').offsetHeight *
                  (this.$route.query &&
                  this.$route.query &&
                  this.$route.query.chartHeight
                    ? Number(this.$route.query.chartHeight) / 100
                    : 0.5)
          }
          c3Params = c3helper.prepareMobile(c3Params)
          if (c3Params) {
            c3Params.data.onclick = d => {
              this.$emit('mobiledrilldown', d)
            }
          }
          let self = this
          this.$nextTick(() => {
            if (self.showAlarms) {
              let alarmsParams = this.showBooleanAlarms
                ? c3helper.prepareBooleanAlarmChart(
                    this.chartContext,
                    c3Params.data.json.x,
                    this.alarms
                  )
                : c3helper.prepareBooleanChart(
                    this.chartContext,
                    c3Params.data.json.x,
                    this.alarms
                  )

              if (self.$refs['alarmsChartEle']) {
                alarmsParams.bindto = self.$refs['alarmsChartEle']
                if (this.$route.query && document.getElementById('q-app')) {
                  alarmsParams.padding = {
                    top: 10,
                    left: 30,
                    right: 15,
                    bottom: -30,
                  }
                  alarmsParams.size = {
                    width: document.getElementById('q-app').offsetWidth - 20,
                    height: 47,
                  }
                }
                bb.generate(alarmsParams)
              }
            }
          })
          if (self.$refs['newChartEle']) {
            c3Params.bindto = self.$refs['newChartEle']
            self.c3.params = c3Params
            c3Params.axis.x.tick.count = 4
            self.c3.chart = bb.generate(c3Params)
          } else {
            this.$nextTick(function() {
              c3Params.bindto = self.$refs['newChartEle']
              self.c3.params = c3Params
              c3Params.axis.x.tick.count = 4
              self.c3.chart = bb.generate(c3Params)
            })
          }
        })
      }
    },
    isXValueMode(options, data) {
      if (options.type === 'pie' || options.type === 'donut') {
        if (options.xColorMap) {
          return true
        } else {
          if (
            data &&
            data.x &&
            Object.keys(data).length === 2 &&
            data.x.length <= 15
          ) {
            let colors = colorHelper.newColorPicker(data.x.length)
            let xColorMap = {}
            for (let i = 0; i < data.x.length; i++) {
              let xVal = data.x[i]
              xColorMap[xVal] =
                options.xColorMap && options.xColorMap[xVal]
                  ? options.xColorMap[xVal]
                  : colors[i]
            }
            options.xColorMap = xColorMap
            return true
          }
        }
      }
      return false
    },
    resize() {
      this.render()
      if (this.$refs['newWidget']) {
        this.$refs['newWidget'].resize()
      }
    },
    update() {
      this.render()
    },
  },
}
</script>

<style>
.widget-legends {
  background: #fff;
  padding: 20px 0 50px 50px;
  position: relative;
  clear: both;
}
.fchart-legend-left-container {
  width: 100%;
  max-width: 1000px;
}
.fchart-legend-right-container {
  width: 100%;
  max-width: 300px;
  max-height: 500px;
  overflow-x: hidden;
  overflow-y: scroll;
  text-align: right;
  float: right;
  padding-bottom: 30px;
}
.fchart-legend-right-container .fLegendContainer-new {
  justify-content: left;
  position: relative;
  top: 60px;
}
@media print {
  html,
  body {
    height: 100%;
    width: 100%;
    margin: 0;
    padding: 0;
  }
  @page {
    size: A4 portrait;
    max-height: 100%;
    max-width: 100%;
  }
  .fLegendContainer-new {
    margin-bottom: 0px;
    margin-top: 0px;
  }
  .add-points-block {
    display: none;
  }
  .fc-mobile-chart,
  .widget-legends {
    width: 100%;
  }
  .fLegendContainer-new,
  .widget-legends {
    width: 100%;
    max-width: 100%;
  }
  .widget-legends {
    padding-top: 0;
    padding-bottom: 30px;
    padding-right: 30px;
  }
  .variance-item {
    padding-right: 0;
  }
  .legend-container {
    display: flex !important;
    flex-wrap: wrap !important;
  }
  .scrollable {
    overflow: visible;
    margin-top: 0 !important;
    padding-top: 0px !important;
  }
  .legendBoxNew-fchart .textoverflow-ellipsis {
    font-size: 16px;
  }
  .filter-field .Reading-Analysis {
    display: none !important;
  }
  .chart-icon {
    display: none !important;
  }
  .pdfDateView,
  .reports-header .description {
    margin-top: 0 !important;
  }
  .fc-mobile-chart svg {
    zoom: 80%;
    margin-left: -60px;
  }
  .reports-header .description {
    display: none;
  }
  .fLegendContainer-new {
    margin-top: 0 !important;
  }
  .report-title,
  .description {
    padding-bottom: 0;
  }
  .report-title .title {
    padding-top: 0;
    margin-top: 0 !important;
  }
  .building-graph-container {
    padding-top: 0;
  }
  .newanalytics .fc-mobile-chart.bb {
    padding-bottom: 0 !important;
    margin-left: -55px;
  }
  .building-graph-container,
  .newanalytics .fc-mobile-chart.bb,
  .widget-legends {
    padding-left: 0;
    padding-right: 0;
    width: 100%;
  }
}
.mobile-edit-repor .mobile-chart-legend {
  max-height: 200px;
}
.mobile-alarmsummary-chart .fc-alarms-chart-title {
  margin-left: 30px;
  padding-bottom: 10px;
}
.drill-down-arrow {
  font-size: 16px;
  padding-left: 20px;
}
</style>
