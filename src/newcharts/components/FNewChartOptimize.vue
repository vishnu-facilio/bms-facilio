<template>
  <div
    class="height100 f-singlechart mL20 mR20"
    :class="
      this.resultObj && this.resultObj.scatterConfig ? 'fc-scatter-chart' : ''
    "
    v-if="
      reportObj.options.settings.chartMode === 'single' ||
        (this.resultObj && this.resultObj.scatterConfig)
    "
  >
    <f-chart-type
      v-if="!hidecharttypechanger"
      :options="options"
      class="charttypechanger fc-new--chart-type-single"
    ></f-chart-type>
    <div
      v-if="chartContext && options.settings.chart !== false"
      style="text-align: center;"
    >
      <f-chart-legends
        v-if="options.legend.show && options.legend.position === 'top'"
        ref="chartLegendtop"
        :chart="c3.chart"
        :config="config"
        :resultObj="resultObj"
      ></f-chart-legends>
      <div :style="isWidget ? 'position: relative; top: 15px;' : ''">
        <div v-if="showAlarms" class="fc-alarms-chart-title">Alarms</div>
        <div ref="alarmsChartEle" class="fc-alarms-chart pdf-chart"></div>
      </div>
      <div
        class="fc-newchart-container"
        :class="{
          'scatter-full-opacity':
            c3.params &&
            c3.params.data &&
            c3.params.data.types &&
            !Object.values(c3.params.data.types).includes('bubble'),
        }"
      >
        <div
          ref="newChartEle"
          class="fc-new-chart pdf-chart"
          :class="{
            'hide-y2-axis':
              options && options.axis && options.axis.showy2axis === false,
          }"
        ></div>
        <f-chart-legends
          v-if="options.legend.show && options.legend.position === 'right'"
          :style="{ width: options.legend.width + 'px' }"
          ref="chartLegendright"
          :config="config"
          :resultObj="resultObj"
        ></f-chart-legends>
      </div>
      <f-chart-legends
        v-if="options.legend.show && options.legend.position === 'bottom'"
        ref="chartLegendbottom"
        :config="config"
        :resultObj="resultObj"
      ></f-chart-legends>
      <f-widget-legends
        ref="newWidget"
        class="widget-legends"
      ></f-widget-legends>
    </div>
  </div>
</template>

<script>
import { bb } from 'billboard.js'
import basechart from 'newcharts/mixins/basechart'
import c3helper from '../helpers/c3-helper'
import FChartLegends from './FChartLegendsOptimize'
import FWidgetLegends from 'newcharts/components/FWidgetLegendsOptimize'
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
    'reportObj',
    'options',
  ],
  components: {
    FChartLegends,
    FWidgetLegends,
    FChartType,
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
    render(reportObj) {
      let data = reportObj.data
      let dateRange = reportObj.dateRange
      let alarms = reportObj.alarms
      let options = reportObj.options
      if (
        data &&
        (options.settings.chartMode === 'single' ||
          (this.resultObj && this.resultObj.scatterConfig)) &&
        reportObj.params.analyticsType !== 3
      ) {
        this.chartContext = null

        let ChartContext = null

        this.$nextTick(() => {
          ChartContext = {}
          ChartContext.options = this.getOptions()
          ChartContext.dateRange = dateRange
          ChartContext.data = data
          ChartContext.xValueMode = this.isXValueMode(
            ChartContext.options,
            data
          )
          ChartContext.unitMap = this.getUnitMap(options.dataPoints)
          ChartContext.enumMap = this.getEnumMap(options.dataPoints)
          if (this.resultObj && this.resultObj.baselineData) {
            ChartContext.baselineColors = this.resultObj.baselineDataColors
            ChartContext.baselineData = this.resultObj.baselineData
          }
          if (this.resultObj && this.resultObj.Readingdp) {
            ChartContext['Readingdp'] = this.resultObj.Readingdp
          }
          if (this.resultObj && this.resultObj.scatterConfig) {
            ChartContext['scatterConfig'] = this.resultObj.scatterConfig
            ChartContext['xMap'] = this.resultObj.xMap
          }

          ChartContext.openAlarm = alarmList => {
            if (alarmList) {
              let alarmId = []
              for (let a of alarmList) {
                alarmId.push(a.id)
              }

              this.jumpToAlarms(alarmId)
            }
          }

          if (
            ChartContext.options.type === 'pie' ||
            ChartContext.options.type === 'donut'
          ) {
            ChartContext.options.padding.top = 20
          }
          let c3Params = c3helper.prepare(ChartContext)

          if (c3Params) {
            c3Params.data.onclick = d => {
              this.$emit('drilldown', d)
            }
          }
          if (this.mergeOption) {
            ChartContext.options.padding.left = this.mergeOption.padding.left
            ChartContext.options.padding.right = this.mergeOption.padding.right
            ChartContext.options.axis.x.label.text = 'Month'
            c3Params.axis.y.label.text = 'No of Work Request'
          }
          if (this.isWidget) {
            if (ChartContext.options.legend.show) {
              c3Params.size.height = c3Params.size.height - 50
            } else {
              c3Params.size.height = c3Params.size.height - 20
            }
            if (options.widgetLegend.show) {
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
            ChartContext.alarms = alarms
            c3Params.size.height = c3Params.size.height - 100
          }

          let alarmsParams = null
          if (self.showAlarms) {
            alarmsParams = c3helper.prepareBooleanChart(
              ChartContext,
              c3Params.data.json.x,
              alarms
            )

            if (self.$refs['alarmsChartEle']) {
              alarmsParams.bindto = self.$refs['alarmsChartEle']
              bb.generate(alarmsParams)
            }
          }

          if (ChartContext.options.customizeC3) {
            let defaultOptions = {}
            let mergedOptions = deepmerge.objectAssignDeep(
              defaultOptions,
              c3Params,
              ChartContext.options.customizeC3
            )
            c3Params = mergedOptions
          }
          self.chartContext = ChartContext
          if (self.$refs['newChartEle']) {
            c3Params.bindto = self.$refs['newChartEle']
            if (self.$refs['chartLegendtop']) {
              self.$refs['chartLegendtop'].values(
                options,
                ChartContext.xValueMode,
                ChartContext.xLabelMap
              )
            } else if (self.$refs['chartLegendbottom']) {
              self.$refs['chartLegendbottom'].values(
                options,
                ChartContext.xValueMode,
                ChartContext.xLabelMap
              )
            } else if (self.$refs['chartLegendright']) {
              self.$refs['chartLegendright'].values(
                options,
                ChartContext.xValueMode,
                ChartContext.xLabelMap
              )
            }
            this.$nextTick(function() {
              if (self.isWidget && self.$refs['chartLegendtop']) {
                c3Params.size.height =
                  c3Params.size.height -
                  (self.$refs['chartLegendtop'].$el.clientHeight - 20)
              } else if (self.isWidget && self.$refs['chartLegendbottom']) {
                c3Params.size.height =
                  c3Params.size.height -
                  (self.$refs['chartLegendbottom'].$el.clientHeight - 20)
              }

              if (self.fixedChartHeight) {
                c3Params.size.height = self.fixedChartHeight
              }
              if (self.c3.chart) {
                self.c3.chart.destroy()
              }
              self.c3.chart = bb.generate(c3Params)
              self.c3.params = c3Params
              if (this.showWidget && self.$refs['newWidget']) {
                self.$refs['newWidget'].value(
                  self.c3.chart,
                  data,
                  options,
                  self.resultObj ? self.resultObj.reportData.aggr : null
                )
              }
              this.chartgenerate()
            })
          } else {
            this.$nextTick(function() {
              if (self.showAlarms) {
                if (self.$refs['alarmsChartEle']) {
                  alarmsParams.bindto = self.$refs['alarmsChartEle']
                  bb.generate(alarmsParams)
                }
              }
              c3Params.bindto = self.$refs['newChartEle']
              if (self.$refs['chartLegendtop']) {
                self.$refs['chartLegendtop'].values(
                  options,
                  ChartContext.xValueMode,
                  ChartContext.xLabelMap
                )
              } else if (self.$refs['chartLegendbottom']) {
                self.$refs['chartLegendbottom'].values(
                  options,
                  ChartContext.xValueMode,
                  ChartContext.xLabelMap
                )
              } else if (self.$refs['chartLegendright']) {
                self.$refs['chartLegendright'].values(
                  options,
                  ChartContext.xValueMode,
                  ChartContext.xLabelMap
                )
              }
              this.$nextTick(function() {
                if (self.isWidget && self.$refs['chartLegendtop']) {
                  c3Params.size.height =
                    c3Params.size.height -
                    (self.$refs['chartLegendtop'].$el.clientHeight - 20)
                } else if (self.isWidget && self.$refs['chartLegendbottom']) {
                  c3Params.size.height =
                    c3Params.size.height -
                    (self.$refs['chartLegendbottom'].$el.clientHeight - 20)
                }

                if (self.fixedChartHeight) {
                  c3Params.size.height = self.fixedChartHeight
                }
                if (self.c3.chart) {
                  self.c3.chart.destroy()
                }
                self.c3.chart = bb.generate(c3Params)
                self.c3.params = c3Params
                if (this.showWidget && self.$refs['newWidget']) {
                  self.$refs['newWidget'].value(
                    self.c3.chart,
                    data,
                    options,
                    self.resultObj ? self.resultObj.reportData.aggr : null
                  )
                }
                this.chartgenerate()
              })
            })
          }
        })
      } else {
        if (this.c3.chart) {
          this.c3.chart.destroy()
        }
      }
    },
    chartgenerate() {
      this.$emit('chartgenerate', true)
    },
    isXValueMode(options, data) {
      if (options.type === 'pie' || options.type === 'donut') {
        if (
          data &&
          data.x &&
          Object.keys(data).length === 2 &&
          data.x.length <= 15
        ) {
          if (options.xColorMap) {
            return true
          } else {
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
      options.xColorMap = null
      return false
    },

    reRender() {
      let reportObj = Object.assign({}, this.reportObj)
      if (this.updateTimeout) {
        clearTimeout(this.updateTimeout)
      }
      this.updateTimeout = setTimeout(() => this.render(reportObj), 500)
    },
    resize() {
      this.reRender()
      if (this.$refs['newWidget'] && this.$refs) {
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
  top: -36px !important;
  right: -30px !important;
  position: absolute !important;
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
.fc-scatter-chart g.bb-chart .bb-event-rects rect.bb-event-rect.bb-event-rect {
  shape-rendering: crispEdges;
  stroke: #e8ebf1 !important;
  stroke-width: 2px !important;
}
.fc-scatter-chart .bb .bb-axis-x path.domain {
  opacity: 0 !important;
}
.fc-scatter-chart .scatter-full-opacity .bb-circles circle {
  opacity: 1 !important;
}
.fc-scatter-chart .scatter-full-opacity .bb-circles rect {
  opacity: 1 !important;
}
.fc-scatter-chart .scatter-full-opacity .bb-circles use {
  opacity: 1 !important;
}
</style>
