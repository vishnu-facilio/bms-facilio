<template>
  <div class="height100">
    <div
      v-if="c3ParamsList && options.settings.chart !== false"
      class="f-multichart"
    >
      <div :class="['mchart-child-title', 'mchart-boolean']" v-if="showAlarms">
        Alarms
      </div>
      <div
        ref="alarmsChartEle"
        v-show="showAlarms"
        class="fc-alarms-chart pdf-chart"
      ></div>
      <div
        v-for="(c3Param, index) in c3ParamsList"
        :key="index"
        class="f-multichart-print"
      >
        <div v-if="chartContext" style="text-align: center;">
          <div
            class="boolean-text-style"
            :class="[
              'mchart-child-title',
              c3Param.data.booleanChart
                ? 'mchart-boolean'
                : 'mchart-non-boolean',
            ]"
            :style="{
              color: c3Param.data.booleanChart
                ? c3Param.data.booleanChart.color
                : '',
            }"
            v-if="
              chartList &&
                chartList[index] &&
                chartList[index].chart &&
                (!chartContext.options.legend.show || c3Param.data.booleanChart)
            "
          >
            {{ c3Param.label }}
          </div>
          <f-mobile-chart-legends
            :key="index"
            :id="'mobilechartLegend_' + index"
            :ref="'mobilechartLegend' + [index]"
            v-if="
              chartContext.options.legend.show &&
                !(c3Param.data && c3Param.data.booleanChart)
            "
            :chart="
              chartList && chartList[index] && chartList[index].chart
                ? chartList[index].chart
                : null
            "
            :options="chartContext.options"
            :multichart="c3Param.key"
            :reportVarianceData="
              resultObj ? resultObj.reportVarianceData : null
            "
            :chartContext="chartContext"
            :resultObj="resultObj"
          ></f-mobile-chart-legends>
          <div class="fc-newchart-container f-mobile-multi-chart pT10">
            <div
              ref="newChartEle"
              class="fc-new-chart"
              :class="{
                'fc-boolean-chart': c3Param.data.booleanChart,
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
    </div>
  </div>
</template>

<script>
import { bb } from 'billboard.js'
import basechart from 'newcharts/mixins/basechart'
import c3helper from '../helpers/c3-helper'
import FMobileChartLegends from './FMobileChartLegends'
import tooltip from '@/graph/mixins/tooltip'

export default {
  mixins: [basechart],
  props: ['resultObj', 'isWidget', 'booleanData'],
  components: {
    FMobileChartLegends,
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
      updateTimeout: null,
    }
  },
  mounted() {
    this.render()
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
        this.chartContext = {}
        this.chartContext.multichartUniqueKey = Math.random()
          .toString(36)
          .substr(2, 9)
        this.chartContext.options = this.getOptions()
        this.chartContext.options.isMobile = true
        this.chartContext.options.isMobileMulti = true
        this.chartContext.dateRange = this.dateRange
        this.chartContext.options.axis.x.culling = {
          max: 4,
        }
        this.chartContext.data = this.data
        this.chartContext.booleanData = this.booleanData
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
        let self = this
        this.chartContext.tooltipCallback = (d, config, title) => {
          for (let i = 0; i < config.data.length; i++) {
            if (self.$refs['mobilechartLegend' + [i]]) {
              self.$refs['mobilechartLegend' + [i]][0].showTooltip(
                d,
                config,
                title
              )
            }
          }
        }
        this.chartContext.tooltipHideCallback = (d, config) => {
          let points = []
          config.data.forEach(dp => {
            if (dp.type === 'group') {
              points.push(...dp.children)
            } else {
              points.push(dp)
            }
          })
          for (let i = 0; i < points.length; i++) {
            if (self.$refs['mobilechartLegend' + [i]]) {
              self.$refs['mobilechartLegend' + [i]][0].hideTooltip()
            }
          }
        }
        if (this.isWidget) {
          if (this.chartContext.options.legend.show) {
            this.chartContext.options.size.height =
              this.chartContext.options.size.height - 50
          } else {
            this.chartContext.options.size.height =
              this.chartContext.options.size.height - 20
          }
          if (this.options.widgetLegend.show) {
            this.chartContext.options.size.height =
              this.chartContext.options.size.height - 100
          }
        }
        this.c3ParamsList = c3helper.prepareMultichart(this.chartContext)

        this.chartList = []
        this.$nextTick(function() {
          if (self.showAlarms) {
            let alarmsParams = this.showBooleanAlarms
              ? c3helper.prepareBooleanAlarmChart(
                  this.chartContext,
                  self.c3ParamsList[0].data.json.x,
                  this.alarms
                )
              : c3helper.prepareBooleanChart(
                  this.chartContext,
                  self.c3ParamsList[0].data.json.x,
                  this.alarms
                )

            if (self.$refs['alarmsChartEle']) {
              alarmsParams.bindto = self.$refs['alarmsChartEle']
              if (this.$route.query && document.getElementById('q-app')) {
                alarmsParams.padding = {
                  top: 10,
                  left: 45,
                  right: 15,
                  bottom: -30,
                }
                alarmsParams.size = {
                  width: document.getElementById('q-app').offsetWidth,
                  height: 47,
                }
              }
              bb.generate(alarmsParams)
            }
          }
          for (let i = 0; i < self.c3ParamsList.length; i++) {
            let c3Param = self.c3ParamsList[i]
            if (self.$refs['newChartEle'][i]) {
              c3Param.bindto = self.$refs['newChartEle'][i]
              if (
                this.$route.meta.source &&
                this.$route.meta.source === 'dashboard'
              ) {
                c3Param.size.width =
                  document.getElementById('q-app').offsetWidth - 30
                // c3Param.size.height =
                //   c3Param.data && c3Param.data.booleanChart
                //     ? document.getElementById('q-app').offsetHeight * 0.05
                //     : document.getElementById('q-app').offsetHeight * 0.3
              }
              // if (
              //   this.$route.meta.source &&
              //   this.$route.meta.source === 'analytics' &&
              //   document.getElementsByClassName('newanalytics-generater')[0]
              // ) {
              //   c3Param.size.height =
              //     c3Param.data && c3Param.data.booleanChart
              //       ? document.getElementById('q-app').offsetHeight * 0.05
              //       : document.getElementById('q-app').offsetHeight * 0.3
              // }
              if (
                (!c3Param.data.booleanChart &&
                  this.$route.query &&
                  this.$route.query.reportId &&
                  document.getElementById('q-app')) ||
                (this.$route.query &&
                  this.$route.query.analyticsConfig &&
                  document.getElementById('q-app'))
              ) {
                c3Param.size.width =
                  document.getElementById('q-app').offsetWidth - 30
                c3Param.size.height =
                  c3Param.data && c3Param.data.booleanChart
                    ? document.getElementById('q-app').offsetHeight *
                      (this.$route.query &&
                      this.$route.query &&
                      this.$route.query.chartHeight
                        ? Number(this.$route.query.chartHeight) / 100
                        : 0.05)
                    : document.getElementById('q-app').offsetHeight *
                      (this.$route.query &&
                      this.$route.query &&
                      this.$route.query.chartHeight
                        ? Number(this.$route.query.chartHeight) / 100
                        : 0.3)
              }
              c3Param = c3helper.prepareMobile(c3Param, true)
              c3Param.axis.x.tick.count = 4
              self.chartList.push({
                params: c3Param,
                chart: bb.generate(c3Param),
              })
            }
          }
        })
      }
    },
    getIndividualChartHeight() {
      let height = 200
      if (this.size.height) {
        let calcHeight = this.size.height / this.chartList.length
        if (calcHeight >= 200) {
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
  },
}
</script>

<style>
.widget-legends {
  background: #fff;
  padding: 20px 0 50px 50px;
  position: relative;
}

.mchart-child-title {
  text-align: left;
  text-transform: uppercase;
  font-size: 12px;
  position: relative;
  z-index: 100;
  top: 15px;
}

.mchart-boolean {
  padding-left: 45px;
}

.mchart-non-boolean {
  padding-left: 75px;
  color: #adb0b6 !important;
}
.boolean-text-style {
  background: white;
  padding-top: 10px;
  padding-bottom: 10px;
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

  .f-multichart-print {
    page-break-inside: avoid;
  }
}
</style>
