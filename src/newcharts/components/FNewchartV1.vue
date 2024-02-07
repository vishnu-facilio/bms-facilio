<template>
  <div class="height100 f-singlechart mL20 mR20">
    <portal
      v-if="chartTypeTarget && !hidecharttypechanger"
      :to="chartTypeTarget"
      slim
    >
      <f-chart-type :options="options"></f-chart-type
    ></portal>
    <f-chart-type
      v-else-if="!hidecharttypechanger"
      :options="options"
      class="charttypechanger fc-new--chart-type-single"
    ></f-chart-type>
    <div
      v-if="chartContext && options.settings.chart !== false"
      style="text-align: center;"
    >
      <f-chart-legends
        ref="chartLegend"
        v-if="
          chartContext.options.legend.show &&
            chartContext.options.legend.position === 'top'
        "
        :chart="c3.chart"
        :options="options"
        :xValueMode="chartContext.xValueMode"
        :xLabelMap="chartContext.xLabelMap"
        :config="config"
      ></f-chart-legends>
      <slot name="relatedAlarmBar" class="fc-alarms-chart pdf-chart"></slot>
      <f-chart-title
        v-if="chartContext.options.regionConfig"
        ref="chartTitle"
        :chart="c3.chart"
        :options="options"
        :xValueMode="chartContext.xValueMode"
        :xLabelMap="chartContext.xLabelMap"
        :config="config"
        :chartContext="chartContext"
      ></f-chart-title>
      <div :style="isWidget ? 'position: relative; top: 15px;' : ''">
        <div v-if="showAlarms && showAlarmTitle" class="fc-alarms-chart-title">
          Alarms
        </div>
        <div ref="alarmsChartEle" class="fc-alarms-chart pdf-chart"></div>
      </div>
      <div class="fc-newchart-container">
        <div
          ref="newChartEle"
          class="fc-new-chart pdf-chart"
          :class="{
            'hide-y2-axis':
              options && options.axis && options.axis.showy2axis === false,
          }"
        ></div>
        <f-chart-legends
          :style="{ width: chartContext.options.legend.width + 'px' }"
          v-if="
            chartContext.options.legend.show &&
              chartContext.options.legend.position === 'right'
          "
          :chart="c3.chart"
          :options="options"
          :xValueMode="chartContext.xValueMode"
          :xLabelMap="chartContext.xLabelMap"
          :config="config"
        ></f-chart-legends>
      </div>
      <f-chart-legends
        ref="chartLegend"
        v-if="
          chartContext.options.legend.show &&
            chartContext.options.legend.position === 'bottom'
        "
        :chart="c3.chart"
        :options="options"
        :xValueMode="chartContext.xValueMode"
        :xLabelMap="chartContext.xLabelMap"
      ></f-chart-legends>
      <f-widget-legends
        v-if="showWidget"
        ref="newWidget"
        :chart="c3.chart"
        :reportVarianceData="resultObj ? resultObj.reportData.aggr : null"
        :data="data"
        :options="options"
        class="widget-legends"
      ></f-widget-legends>
    </div>
  </div>
</template>

<script>
import { bb } from 'billboard.js'
import basechart from 'newcharts/mixins/basechart'
import c3helper from '../helpers/c3-helper'
import FChartLegends from './FChartLegends'
import FChartTitle from './FChartTitle'
import FWidgetLegends from 'newcharts/components/FWidgetLegends'
import FChartType from 'newcharts/components/FNewChartType'
import JumpToHelper from '@/mixins/JumpToHelper'
import tooltip from '@/graph/mixins/tooltip'
import colorHelper from 'newcharts/helpers/color-helper'
import deepmerge from 'util/deepmerge'

export default {
  mixins: [basechart, JumpToHelper],
  props: [
    'resultObj',
    'isWidget',
    'showWidgetLegends',
    'config',
    'chartTypeTarget',
  ],
  components: {
    FChartLegends,
    FWidgetLegends,
    FChartType,
    FChartTitle,
  },
  computed: {
    showWidget() {
      if (typeof this.showWidgetLegends === 'undefined') {
        return true
      } else if (this.config.hasOwnProperty('showWidgetLegends')) {
        return this.config.showWidgetLegends
      } else {
        return false
      }
    },
    isRegression() {
      if (
        this.$route.path.includes('regression') ||
        (this.resultObj &&
          this.resultObj.regressionConfig &&
          this.resultObj.regressionConfig.length !== 0 &&
          this.resultObj.regression === true)
      ) {
        return true
      }
      return false
    },
    showAlarmTitle() {
      if (this.chartContext && this.chartContext.options) {
        return !this.chartContext.options.hideAlarmTitle
      }
      return true
    },
  },
  data() {
    return {
      chartContext: null,
      chartId: Math.random()
        .toString(36)
        .substr(2, 9),
      c3: {
        params: null,
        chart: null,
      },
      showAlarms: false,
      updateTimeout: null,
    }
  },
  mounted() {
    if (this.config.hidecharttypechanger) {
      this.hidecharttypechanger = true
    }
    if (this.isRegression) {
      this.hidecharttypechanger = true
    }
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
  methods: {
    render() {
      if (this.data) {
        this.chartContext = null

        this.$nextTick(() => {
          this.chartContext = {}
          this.chartContext.options = this.getOptions()
          this.chartContext.dateRange = this.dateRange
          this.chartContext.data = this.data
          this.chartContext.xValueMode = this.isXValueMode(
            this.chartContext.options,
            this.data
          )
          this.chartContext.unitMap = this.getUnitMap(this.options.dataPoints)
          this.chartContext.enumMap = this.getEnumMap(this.options.dataPoints)
          this.chartContext.openAlarm = alarmList => {
            if (alarmList) {
              let alarmId = []
              for (let a of alarmList) {
                alarmId.push(a.id)
              }

              this.jumpToAlarms(alarmId)
            }
          }

          if (
            this.chartContext.options.type === 'pie' ||
            this.chartContext.options.type === 'donut'
          ) {
            this.chartContext.options.padding.top = 20
          }

          let c3Params = c3helper.prepare(this.chartContext)

          if (c3Params) {
            c3Params.data.onclick = d => {
              this.$emit('drilldown', d)
            }
          }
          if (this.mergeOption) {
            this.chartContext.options.padding.left = this.mergeOption.padding.left
            this.chartContext.options.padding.right = this.mergeOption.padding.right
            this.chartContext.options.axis.x.label.text = 'Month'
            c3Params.axis.y.label.text = 'No of Work Request'
          }
          if (this.isWidget) {
            if (this.chartContext.options.legend.show) {
              c3Params.size.height = c3Params.size.height - 50
            } else {
              c3Params.size.height = c3Params.size.height - 20
            }
            if (this.options.widgetLegend.show) {
              c3Params.size.height = c3Params.size.height - 75
            }

            if (this.resultObj && this.resultObj.report.userFilters) {
              c3Params.size.height = c3Params.size.height - 65
            }
          }

          let self = this

          self.showAlarms =
            this.getOptions().common.mode === 1 &&
            this.getOptions().settings.alarm
          if (this.config && this.config.hasOwnProperty('showAlarms')) {
            self.showAlarms = this.config.showAlarms
          }
          if (self.showAlarms) {
            this.chartContext.alarms = this.alarms
            c3Params.size.height = c3Params.size.height - 100
          }

          let alarmsParams = null
          if (self.showAlarms) {
            alarmsParams = c3helper.prepareBooleanChart(
              this.chartContext,
              c3Params.data.json.x,
              this.alarms
            )

            if (self.$refs['alarmsChartEle']) {
              alarmsParams.bindto = self.$refs['alarmsChartEle']
              bb.generate(alarmsParams)
            }
          }

          if (this.chartContext.options.customizeC3) {
            let defaultOptions = {}
            let mergedOptions = deepmerge.objectAssignDeep(
              defaultOptions,
              c3Params,
              this.chartContext.options.customizeC3
            )
            c3Params = mergedOptions
          }

          if (self.$refs['newChartEle']) {
            c3Params.bindto = self.$refs['newChartEle']

            if (self.isWidget && self.$refs['chartLegend']) {
              c3Params.size.height =
                c3Params.size.height -
                (self.$refs['chartLegend'].$el.clientHeight - 20)
            }

            if (self.fixedChartHeight) {
              c3Params.size.height = self.fixedChartHeight
            }
            self.c3.params = c3Params
            self.c3.chart = bb.generate(c3Params)
          } else {
            this.$nextTick(function() {
              if (self.showAlarms) {
                if (self.$refs['alarmsChartEle']) {
                  alarmsParams.bindto = self.$refs['alarmsChartEle']
                  bb.generate(alarmsParams)
                }
              }

              c3Params.bindto = self.$refs['newChartEle']

              if (self.isWidget && self.$refs['chartLegend']) {
                c3Params.size.height =
                  c3Params.size.height -
                  (self.$refs['chartLegend'].$el.clientHeight - 20)
              }

              if (self.fixedChartHeight) {
                c3Params.size.height = self.fixedChartHeight
              }
              self.c3.params = c3Params
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

    reRender() {
      if (this.updateTimeout) {
        clearTimeout(this.updateTimeout)
      }
      this.updateTimeout = setTimeout(() => this.render(), 500)
    },
    resize() {
      this.reRender()
      if (this.$refs['newWidget']) {
        this.$refs['newWidget'].resize()
      }
    },
    update() {
      this.reRender()
    },
  },
}
</script>

<style>
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
.f-singlechart .fc-new--chart-type-single {
  top: -37px !important;
  right: -57px !important;
  position: absolute !important;
  border-right: 1px solid #e5e4e4;
  padding-right: 10px;
  margin-right: 135px !important;
  line-height: 10px;
}
@media print {
  @page {
    size: A4;
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
  .fc-new-chart,
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
    overflow: scroll;
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
  .building-graph-container,
  .newanalytics .fc-new-chart.bb,
  .widget-legends {
    padding-left: 0;
    padding-right: 0;
    width: 100%;
  }
  .fc-new-chart.bb svg {
    display: initial !important;
    margin-left: -30px;
  }
  .variance-x-label {
    font-size: 14px;
  }
  .reports-header .max-width350px {
    max-width: 100% !important;
    text-align: center;
  }
}
</style>
