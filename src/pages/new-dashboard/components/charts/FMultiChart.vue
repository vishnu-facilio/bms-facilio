<template>
  <div
    class="height100"
    v-if="options.settings.chartMode === 'multi' || ismulti"
  >
    <div
      v-if="c3ParamsList && options.settings.chart !== false"
      class="f-multichart"
    >
      <div
        v-if="showAlarms && showAlarmTitle"
        class="fc-alarms-chart-title"
        :style="
          moduleName === 'sensorrollupalarm'
            ? 'color: #324056 !important;font-weight: bold;'
            : ''
        "
      >
        {{ !$validation.isEmpty(alarms.barTitle) ? alarms.barTitle : 'Alarms' }}
      </div>
      <div
        ref="alarmsChartEle"
        v-show="showAlarms"
        class="fc-alarms-chart pdf-chart"
        @click="alarmDrilldown"
      ></div>
      <div v-if="relatedAlarms">
        <template v-for="(relAlarm, idx) in relatedAlarms">
          <div
            :style="isWidget ? 'position: relative; top: 15px;' : ''"
            :key="idx"
          >
            <div
              class="fc-alarms-chart-title"
              style="text-transform: capitalize; color: #324056 !important"
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
      <div
        v-for="(c3Param, index) in c3ParamsList"
        :key="index"
        :class="['f-multichart-print']"
      >
        <f-chart-type
          class="pT10"
          v-if="
            !hidecharttypechanger &&
              !c3Param.data.booleanChart &&
              !c3Param.data.enumChart
          "
          :options="options"
          :multichartkey="c3Param.key"
        ></f-chart-type>
        <div v-if="chartContext" style="text-align: center">
          <div
            class="boolean-chart-title"
            :style="{
              color: '#000',
            }"
            v-if="
              chartList &&
                chartList[index] &&
                chartList[index].chart &&
                !options.legend.show
            "
          >
            {{ c3Param.label }}
          </div>
          <f-chart-legends
            ref="chartLegend"
            :class="{
              'boolean-chart':
                c3Param.data.booleanChart || c3Param.data.enumChart,
            }"
            v-if="options.legend.show"
            :chart="
              chartList && chartList[index] && chartList[index].chart
                ? chartList[index].chart
                : null
            "
            :options="options"
            :multichart="c3Param.key"
            :config="config"
          ></f-chart-legends>
          <div class="fc-newchart-container">
            <div
              ref="newChartEle"
              class="fc-new-chart"
              :class="{
                'fc-boolean-chart':
                  c3Param.data.booleanChart || c3Param.data.enumChart,
                'hide-y2-axis':
                  options &&
                  options.multichart &&
                  options.multichart[c3Param.key] &&
                  options.multichart[c3Param.key].axis &&
                  options.multichart[c3Param.key].axis.showy2axis === false,
              }"
            ></div>
          </div>
        </div>
      </div>
      <f-widget-legends
        ref="newWidget"
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
import basechart from 'src/pages/new-dashboard/components/charts/mixins/basechart.js'
import c3helper from 'src/pages/new-dashboard/components/charts/helpers/c3-helper.js'
import FChartLegends from './FChartLegends'
import FWidgetLegends from 'newcharts/components/FWidgetLegends'
import JumpToHelper from '@/mixins/JumpToHelper'
import FChartType from 'newcharts/components/FChartType'
import tooltip from '@/graph/mixins/tooltip'
import { isEmpty } from '@facilio/utils/validation'

export default {
  mixins: [basechart, JumpToHelper],
  props: [
    'resultObj',
    'isWidget',
    'booleanData',
    'booleanAlarmsData',
    'config',
    'ismulti',
    'relatedAlarms',
    'alarms',
    'moduleName',
  ],
  components: {
    FChartLegends,
    FWidgetLegends,
    FChartType,
  },
  data() {
    return {
      chartContext: null,
      c3: {
        params: null,
        chart: null,
      },
      c3ParamsList: null,
      chartList: [],
      showAlarms: false,
      showBooleanAlarms: false,
      updateTimeout: null,
    }
  },
  mounted() {
    this.render()
  },
  computed: {
    showAlarmTitle() {
      if (this.chartContext && this.chartContext.options) {
        return !this.chartContext.options.hideAlarmTitle
      }
      return true
    },
  },
  beforeDestroy() {
    tooltip.hideTooltip()
    if (this.updateTimeout) {
      clearTimeout(this.updateTimeout)
    }

    if (this.chartList) {
      for (let chart of this.chartList) {
        if (chart.chart) {
          chart.chart.destroy()
        }
      }
    }
    this.chartContext = null
    this.c3ParamsList = null
    this.chartList = null
  },
  methods: {
    render() {
      if (this.data) {
        let chartContext = {}
        chartContext.multichartUniqueKey = Math.random()
          .toString(36)
          .substr(2, 9)
        chartContext.options = this.getOptions()
        chartContext.dateRange = this.dateRange
        chartContext.data = Object.freeze(this.data)
        if (this.resultObj && this.resultObj.Readingdp) {
          chartContext.Readingdp = this.resultObj.Readingdp
        }
        if (this.resultObj) {
          if (this.resultObj.scatterConfig) {
            chartContext.scatterConfig = this.resultObj.scatterConfig
            chartContext.xMap = this.resultObj.xMap
          }
          if (this.resultObj.groupByMetrics) {
            chartContext.groupByMetrics = true
          }
        }
        chartContext.openAlarm = alarmList => {
          if (alarmList) {
            let alarmId = []
            for (let a of alarmList) {
              alarmId.push(a.id)
            }
            if (!this.$mobile) {
              this.jumpToAlarmsNew(alarmId)
            }
          }
        }
        chartContext.booleanData = Object.freeze(this.booleanData)
        chartContext.unitMap = this.getUnitMap(this.options.dataPoints)
        chartContext.enumMap = this.getEnumMap(this.options.dataPoints)

        this.showAlarms =
          chartContext.options.common.mode === 1 &&
          chartContext.options.settings.alarm
        if (this.showAlarms) {
          chartContext.alarms = this.alarms
          chartContext.options.size.height =
            chartContext.options.size.height - 100
        }

        this.showBooleanAlarms =
          this.booleanAlarmsData &&
          this.booleanAlarmsData.regions &&
          this.booleanAlarmsData.regions.length &&
          chartContext.options.common.mode === 1 &&
          chartContext.options.settings.booleanAlarms
        if (this.showBooleanAlarms) {
          chartContext.booleanAlarmsData = this.booleanAlarmsData
          chartContext.options.size.height =
            chartContext.options.size.height - 100
        }
        if (this.isWidget) {
          if (chartContext.options.legend.show) {
            chartContext.options.size.height =
              chartContext.options.size.height - 50
          } else {
            chartContext.options.size.height =
              chartContext.options.size.height - 20
          }
          if (this.options.widgetLegend.show) {
            chartContext.options.size.height =
              chartContext.options.size.height - 100
          }
        }

        this.c3ParamsList = c3helper.prepareMultichart(chartContext)
        if (
          this.resultObj &&
          (this.resultObj.Readingdp || this.resultObj.scatterConfig)
        ) {
          this.c3ParamsList = this.c3ParamsList.filter(
            params => !isEmpty(params.data.axes)
          )
        }

        this.chartList = []
        this.chartContext = chartContext
        let self = this
        this.$nextTick(function() {
          if (self.showAlarms) {
            let alarmsParams = this.showBooleanAlarms
              ? c3helper.prepareBooleanAlarmChart(
                  chartContext,
                  self.c3ParamsList[0].data.json.x,
                  this.alarms
                )
              : c3helper.prepareBooleanChart(
                  chartContext,
                  self.c3ParamsList[0].data.json.x,
                  this.alarms
                )

            if (self.$refs['alarmsChartEle']) {
              alarmsParams.bindto = self.$refs['alarmsChartEle']
              bb.generate(Object.freeze(alarmsParams))
            }
            if (self.relatedAlarms && self.relatedAlarms.length) {
              for (let idx in self.relatedAlarms) {
                if (
                  self.$refs['relatedAlarmsChartEle'] &&
                  self.$refs['relatedAlarmsChartEle'][idx]
                ) {
                  let relalarmsParams = c3helper.prepareBooleanChart(
                    this.chartContext,
                    self.c3ParamsList[0].data.json.x,
                    self.relatedAlarms[idx]
                  )
                  relalarmsParams.bindto =
                    self.$refs['relatedAlarmsChartEle'][idx]
                  bb.generate(relalarmsParams)
                }
              }
            }
          }
          self.$nextTick(() => {
            let j = 0
            for (let i = 0; i < self.c3ParamsList.length; i++) {
              let c3Param = self.c3ParamsList[i]
              if (self.$refs['newChartEle'] && self.$refs['newChartEle'][i]) {
                c3Param.bindto = self.$refs['newChartEle'][i]
                if (
                  !c3Param.data.booleanChart &&
                  !c3Param.data.enumChart &&
                  self.$refs['chartLegend'] &&
                  self.$refs['chartLegend'][j]
                ) {
                  c3Param.size.height =
                    c3Param.size.height -
                    self.$refs['chartLegend'][j++].$el.clientHeight
                  if (
                    self.isWidget &&
                    self.options &&
                    self.options.widgetLegend.show
                  ) {
                    c3Param.size.height = c3Param.size.height + 20
                  }
                }
                self.chartList.push({
                  params: c3Param,
                  chart: bb.generate(Object.freeze(c3Param)),
                })
              }
            }
          })
        })
      }
    },
    getIndividualChartHeight() {
      let height = 100
      if (this.size.height) {
        let calcHeight = this.size.height / this.chartList.length
        if (calcHeight >= 100) {
          height = calcHeight
        }
      }
      return height
    },
    reRender() {
      if (this.updateTimeout) {
        clearTimeout(this.updateTimeout)
      }
      this.updateTimeout = setTimeout(() => this.render(), 1000)
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
    alarmDrilldown() {
      this.$emit('alarmDrilldown')
    },
  },
}
</script>

<style>
.widget-legends {
  background: #fff;
  padding: 20px 0 50px 50px;
  position: relative;
}
.f-multichart .bb-zoom-rect {
  fill: #fff;
  opacity: 1 !important;
  stroke: #e4e7ed;
  stroke-width: 1px;
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
  .f-multichart-print {
    page-break-inside: avoid;
  }
}
</style>
