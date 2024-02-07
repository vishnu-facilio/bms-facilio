<template>
  <div class="height100 f-singlechart">
    <f-chart-type
      v-if="!hidecharttypechanger"
      :options="options"
      @getOptions="getOptionsFromTypeChart"
      class="charttypechanger fc-new-chart-type-single"
    ></f-chart-type>
    <div
      v-if="chartContext && options.settings.chart !== false"
      style="text-align: center;"
    >
      <div :style="isWidget ? 'position: relative; top: 15px;' : ''">
        <div
          v-if="showAlarms && showAlarmTitle"
          class="fc-alarms-chart-title"
          :style="
            moduleName === 'sensorrollupalarm'
              ? 'color: #324056 !important;font-weight: bold;'
              : ''
          "
        >
          Alarms
        </div>
        <div ref="alarmsChartEle" class="fc-alarms-chart pdf-chart"></div>
      </div>
      <div v-if="relatedAlarms">
        <template v-for="(relAlarm, idx) in relatedAlarms">
          <div
            :style="isWidget ? 'position: relative; top: 15px;' : ''"
            :key="idx"
          >
            <div
              class="fc-alarms-chart-title"
              style="text-transform: capitalize;color: #324056 !important;"
            >
              {{ relAlarm ? relAlarm.alarmTitle : '' }}
            </div>
            <div
              ref="relatedAlarmsChartEle"
              class="fc-alarms-chart pdf-chart"
            ></div>
          </div>
        </template>
      </div>
      <f-chart-legends
        ref="chartLegend"
        v-if="
          chartContext.options.legend.show &&
            chartContext.options.legend.position === 'top'
        "
        class="f-chart-ledgend"
        :chart="c3.chart"
        @ontoggle="ontoggle"
        :options="options"
        :resultObj="resultObj"
        :xValueMode="chartContext.xValueMode"
        :xLabelMap="chartContext.xLabelMap"
        :config="config"
        :isScatterPlot="chartContext && chartContext.scatterConfig"
      ></f-chart-legends>
      <f-chart-title
        v-if="chartContext.options.regionConfig"
        ref="chartTitle"
        :chart="c3.chart"
        @ontoggle="ontoggle"
        :options="options"
        :xValueMode="chartContext.xValueMode"
        :xLabelMap="chartContext.xLabelMap"
        :config="config"
        :chartContext="chartContext"
      ></f-chart-title>
      <drilldown-breadcrumb
        v-if="drilldownParams"
        :drilldownParams="drilldownParams"
        @crumbClick="$emit('crumbClick', $event)"
      >
      </drilldown-breadcrumb>
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
          :style="{ width: chartContext.options.legend.width + 'px' }"
          v-if="
            chartContext.options.legend.show &&
              chartContext.options.legend.position === 'right'
          "
          :chart="c3.chart"
          @ontoggle="ontoggle"
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
        @ontoggle="ontoggle"
        :options="options"
        :xValueMode="chartContext.xValueMode"
        :xLabelMap="chartContext.xLabelMap"
      ></f-chart-legends>
      <f-widget-legends
        v-if="showWidget"
        ref="newWidget"
        :chart="c3.chart"
        @ontoggle="ontoggle"
        :reportVarianceData="resultObj ? resultObj.reportData.aggr : null"
        :data="data"
        :options="options"
        class="widget-legends"
      ></f-widget-legends>
    </div>
  </div>
</template>

<script>
import { cloneDeep } from 'lodash'
import { bb } from 'billboard.js'
import basechart from 'src/pages/new-dashboard/components/charts/mixins/basechart.js'
import ReportDataUtil from 'src/pages/report/mixins/ReportDataUtil'
import c3helper from './helpers/c3-helper'
import FChartLegends from './FChartLegends'
import DrilldownBreadcrumb from 'src/pages/report/components/DrilldownBreadcrumb'
import FChartTitle from './FChartTitle'
import FWidgetLegends from 'src/pages/new-dashboard/components/charts/FWidgetLegends.vue'
import FChartType from 'src/pages/new-dashboard/components/charts/FChartType.vue'
import JumpToHelper from '@/mixins/JumpToHelper'
import tooltip from '@/graph/mixins/tooltip'
import colorHelper from 'newcharts/helpers/color-helper'
import deepmerge from 'util/deepmerge'
import * as d3 from 'd3'
import { formatCurrency } from 'charts/helpers/formatter'
import { isEmpty } from '@facilio/utils/validation'
export default {
  mixins: [basechart, JumpToHelper, ReportDataUtil],
  props: {
    updateWidget: { type: Function },
    resultObj: {},
    isWidget: {},
    showWidgetLegends: {},
    config: {},
    specialScatter: {},
    relatedAlarms: {},
    moduleName: {},
    drilldownParams: {},
    item: {
      Type: Object,
      default: () => ({}),
    },
  },
  components: {
    FChartLegends,
    FWidgetLegends,
    FChartType,
    FChartTitle,
    DrilldownBreadcrumb,
  },
  computed: {
    showWidget() {
      if (
        typeof this.showWidgetLegends === 'undefined' ||
        this.showWidgetLegends
      ) {
        return true
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
          if (
            this.chartContext.options.general &&
            this.chartContext.options.general.hideZeroes
          ) {
            this.chartContext.data = this.formatZeroesInData(this.data)
          } else {
            this.chartContext.data = this.data
          }
          if (
            this.chartContext.options.benchmark &&
            this.chartContext.options.benchmark.show
          ) {
            this.chartContext.reportVarianceData = this.resultObj
              ? this.resultObj.reportData.aggr
              : null
          }
          this.chartContext.xValueMode = this.isXValueMode(
            this.chartContext.options,
            this.data
          )
          if (this.options.defaultDurationUnit) {
            this.chartContext.defaultDurationUnit = this.options.defaultDurationUnit
          }
          this.chartContext.specialScatter = this.specialScatter ? true : false
          this.chartContext.unitMap = this.getUnitMap(this.options.dataPoints)
          this.chartContext.enumMap = this.getEnumMap(this.options.dataPoints)
          if (this.resultObj && this.resultObj.baselineData) {
            this.chartContext.baselineColors = this.resultObj.baselineDataColors
            this.chartContext.baselineData = this.resultObj.baselineData
          }
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

          if (this.resultObj && this.resultObj.Readingdp) {
            this.chartContext['Readingdp'] = this.resultObj.Readingdp
          }
          if (this.resultObj && this.resultObj.scatterConfig) {
            this.chartContext['scatterConfig'] = this.resultObj.scatterConfig
            this.chartContext['xMap'] = this.resultObj.xMap
          }
          let c3Params = c3helper.prepare(this.chartContext)
          if (c3Params) {
            const x = cloneDeep(c3Params?.data?.json?.x ?? {})
            c3Params.data.onclick = d => {
              d['dataArray'] = x
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
              c3Params.size.height = c3Params.size.height - 100
            }

            if (this.resultObj && this.resultObj.report.userFilters) {
              c3Params.size.height = c3Params.size.height - 65
            }
          }
          if (this.drilldownParams) {
            c3Params.size.height = c3Params.size.height - 25
            if (this.isWidget) {
              c3Params.size.height = c3Params.size.height - 25 //when chart is inside widget.chart-legend has top:25 . applying margin to  breadcrumb to align with that
            }
          }
          if (
            this.chartContext.options.type === 'gauge' &&
            c3Params.size.height > 350
          ) {
            c3Params.size.height = 350
          }
          let self = this
          const {
            common: { mode },
            settings: { alarm },
          } = this.chartContext.options
          self.showAlarms = mode === 1 && alarm
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

          if (self.relatedAlarms && self.relatedAlarms.length) {
            for (let idx in self.relatedAlarms) {
              if (
                self.$refs['relatedAlarmsChartEle'] &&
                self.$refs['relatedAlarmsChartEle'][idx]
              ) {
                let relalarmsParams = c3helper.prepareBooleanChart(
                  this.chartContext,
                  c3Params.data.json.x,
                  self.relatedAlarms[idx]
                )
                relalarmsParams.bindto =
                  self.$refs['relatedAlarmsChartEle'][idx]
                bb.generate(relalarmsParams)
              }
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
          let print = JSON.parse(
            this.$getProperty(this, '$route.query.printing') || null
          )
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
            let width = self.$refs['newChartEle'].clientWidth
            if (print && !isEmpty(width)) {
              c3Params.size.width = self.$refs['newChartEle'].clientWidth
            }
            self.c3.params = c3Params
            self.c3.chart = bb.generate(this.byPassSetting(c3Params))
          } else {
            this.$nextTick(function() {
              if (self.showAlarms) {
                if (self.$refs['alarmsChartEle']) {
                  alarmsParams.bindto = self.$refs['alarmsChartEle']
                  bb.generate(alarmsParams)
                }
              }
              if (self.relatedAlarms && self.relatedAlarms.length) {
                for (let idx in self.relatedAlarms) {
                  if (
                    self.$refs['relatedAlarmsChartEle'] &&
                    self.$refs['relatedAlarmsChartEle'][idx]
                  ) {
                    let relalarmsParams = c3helper.prepareBooleanChart(
                      this.chartContext,
                      c3Params.data.json.x,
                      self.relatedAlarms[idx]
                    )
                    relalarmsParams.bindto =
                      self.$refs['relatedAlarmsChartEle'][idx]
                    bb.generate(relalarmsParams)
                  }
                }
              }

              c3Params.bindto = self.$refs['newChartEle']
              let width = self.$refs['newChartEle'].clientWidth
              if (print && !isEmpty(width)) {
                c3Params.size.width = self.$refs['newChartEle'].clientWidth
              }
              if (self.isWidget && self.$refs['chartLegend']) {
                c3Params.size.height =
                  c3Params.size.height -
                  (self.$refs['chartLegend'].$el.clientHeight - 20)
              }

              if (self.fixedChartHeight) {
                c3Params.size.height = self.fixedChartHeight
              }
              self.c3.params = c3Params
              self.c3.chart = bb.generate(this.byPassSetting(c3Params))
            })
          }
        })
      }
    },
    ontoggle(key, hiddenLegends) {
      if (
        this.c3.params.ontoggle &&
        typeof this.c3.params.ontoggle === 'function'
      ) {
        let callback = this.c3.params.ontoggle.bind(this.c3.chart.internal, key)
        callback()
      }

      if (
        this.chartContext.options.type === 'bar' &&
        this.chartContext.options.bar.showGroupTotal
      ) {
        let groupTotalData = c3helper.getGroupTotalDataPoint(
          this.c3.params,
          hiddenLegends
        )
        if (groupTotalData) {
          this.c3.chart.load({
            json: {
              group_total: groupTotalData,
            },
          })
        }
      }
    },
    byPassSetting(params) {
      if (
        params &&
        params.data &&
        params.data.hasOwnProperty('labels') &&
        params.data.labels === true
      ) {
        let formattedLabel = {
          centered: false,
          format: function(v) {
            if (Number.isInteger(v)) {
              return d3.format(',')(v)
            } else {
              return v ? formatCurrency(v) : null
            }
          },
        }
        this.$set(params.data, 'labels', formattedLabel)
      }
      return params
    },
    getOptionsFromTypeChart({ chartTypeInt }) {
      const { item, updateWidget } = this
      const itemClone = cloneDeep(item)
      const {
        widget: { dataOptions },
      } = itemClone
      dataOptions['charType'] = chartTypeInt
      dataOptions['chartTypeInt'] = chartTypeInt
      updateWidget(itemClone)
    },
    isXValueMode(options, data) {
      if (options.type === 'pie' || options.type === 'donut') {
        if (data && data.x && Object.keys(data).length === 2) {
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
<style lang="scss" scoped>
.f-chart-ledgend {
  margin-top: 30px;
  display: flex;
}
</style>
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
.f-singlechart .fc-new-chart-type-single {
  top: -42px;
  right: 70px !important;
}
.f-singlechart .chart-icon {
  position: absolute;
  top: 62px;
  right: 17px;
}
@media print {
  .bb {
    display: none;
  }
  .fc-newchart-container .bb {
    display: block;
  }
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

.mvregion rect {
  fill: #037cff;
}
.fc-widget-body .charttypechanger {
  top: 10px;
  left: 10px !important;
  width: 30px;
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
<style scoped lang="scss">
.widget-legends {
  padding: 20px;
  padding-left: 40px;
  padding-right: 40px;
  position: absolute;
  bottom: 0;
  width: 100%;
  display: -webkit-box;
  display: -ms-box;
}
.height100 {
  height: 100%;
  box-sizing: border-box;
  position: static;
}
</style>
