<template>
  <div class="overflow-scroll-flex min-width0 min-height0 scatter-main-con">
    <div class="overflow-scroll-flex min-width0 min-height0">
      <div class="fc-overflow-y p10 fc-main-scatter-scroll">
        <div class="white-bg-block mB10 height550 width100">
          <div class="fc-report-widget-header">
            <div class="new-analytics-filter-section flex-middle">
              <div
                class="dashboard-timeline-filter"
                v-if="!config.hidedatepicker"
              >
                <new-date-picker
                  ref="newDatePicker"
                  :key="componentkey"
                  :zone="$timezone"
                  class="filter-field date-filter-comp inline"
                  v-bind:style="
                    config && config.analyticsType === 2
                      ? 'left:0;float:left;'
                      : ''
                  "
                  :dateObj.sync="config.dateFilter"
                  @date="setDateFilter"
                  :isDateFixed="config.applyReportDate"
                ></new-date-picker>
              </div>
              <div
                class="chart-icon pointer el-popover__reference fc-select-height35"
              >
                <el-select
                  class="period-select fc-input-full-border2 "
                  v-model="config.period"
                  placeholder="Period"
                  title="Time period"
                  data-arrow="true"
                  v-tippy
                  @change="onPeriodChange"
                >
                  <el-option
                    v-for="(period, index) in getAvailablePeriods"
                    :key="index"
                    :label="period.name"
                    :value="period.value"
                    :disabled="!period.enable"
                  ></el-option>
                </el-select>
              </div>
              <span
                v-if="
                  resultObj &&
                    resultObj.report &&
                    resultObj.report.dataPoints.length !== 0
                "
              >
                <!-- <i class="el-icon-circle-plus-outline pointer" @click="showRangeChartDialog = true" title="Add points for range chart" v-tippy></i> -->
              </span>
            </div>
            <div class="flex-middle">
              <div>
                <slot name="Algorithm"></slot>
              </div>
              <div
                v-if="config && config.period === 0"
                @click="openReportFilterDialog"
              >
                <InlineSvg
                  src="svgs/filter"
                  class="vertical-middle fc-icon-border pointer"
                  iconClass="icon icon-sm-md"
                >
                </InlineSvg>
              </div>
              <div @click="openChartOptions">
                <InlineSvg
                  src="svgs/chart-options"
                  class="vertical-middle fc-icon-border mL10 pointer"
                  iconClass="icon icon-sm-md"
                ></InlineSvg>
              </div>
              <div>
                <slot name="chartCustomization"></slot>
              </div>
            </div>
          </div>

          <spinner
            v-if="loading"
            :show="loading"
            size="80"
            class="analytics-spinner"
          ></spinner>
          <div
            v-else-if="failed"
            style="margin-top: 30px;opacity: 0.7;font-size: 13px;padding: 50px;line-height: 25px;text-align: center;"
          >
            <div>{{ $t('home.dashboard.data_loading_failed') }}</div>
          </div>
          <div
            v-else-if="!reportObj.data"
            style="margin-top: 30px;font-size: 13px;padding: 50px;line-height: 25px;text-align: center;"
          >
            <inline-svg
              src="svgs/emptystate/reportlist"
              iconClass="icon text-center icon-100"
            ></inline-svg>
            <div class="nowo-label f13">
              {{ $t('common.wo_report.empty_data') }}
              <span class="bold" v-if="resultObj && resultObj.dateRange">{{
                resultObj.dateRange.value | formatPeriod('MMM DD, YYYY')
              }}</span>
            </div>
          </div>
          <div v-else>
            <div v-if="!config.hidechart">
              <f-mobile-multi-chart
                ref="multiChart"
                v-if="
                  $route.meta &&
                    $route.meta.layout &&
                    $route.meta.layout === 'mobile' &&
                    reportObj.options.settings.chartMode === 'multi'
                "
                :isWidget="config ? true : false"
                :width="config && config.width ? config.width : null"
                :resultObj="resultObj"
                :height="config && config.height ? config.height : null"
                :data="reportObj.data"
                :options="reportObj.options"
                :alarms="reportObj.alarms"
                :dateRange="reportObj.dateRange"
                :hidecharttypechanger="config && config.widget"
                :booleanData="reportObj.booleanData"
              ></f-mobile-multi-chart>
              <div v-else>
                <f-mobile-chart
                  v-if="
                    $route.meta &&
                      $route.meta.layout &&
                      $route.meta.layout === 'mobile'
                  "
                  ref="newChart"
                  :data="reportObj.data"
                  :options="reportObj.options"
                  :dateRange="reportObj.dateRange"
                  :resultObj="resultObj"
                  class="mobile-chart"
                ></f-mobile-chart>
              </div>
              <f-grid-chart
                ref="gridChart"
                v-if="
                  scatterType === 3 &&
                    !(
                      $route.meta &&
                      $route.meta.layout &&
                      $route.meta.layout === 'mobile'
                    )
                "
                :reportObj="reportObj"
                :data="reportObj.data"
                :options="reportObj ? reportObj.options : null"
                :alarms="reportObj.alarms"
                :booleanData="reportObj.booleanData"
                :dateRange="reportObj.dateRange"
                :resultObj="resultObj"
                :hidecharttypechanger="config.hidecharttypechanger"
                :config="config"
              ></f-grid-chart>
              <f-new-chart
                v-if="scatterType !== 3"
                ref="newChart"
                :hidecharttypechanger="config.hidecharttypechanger"
                :resultObj="resultObj"
                :reportObj="reportObj"
                :options="reportObj ? reportObj.options : null"
                :fixedChartHeight="config.fixedChartHeight"
                @chartgenerate="chartgenerate"
                :config="config"
              ></f-new-chart>
            </div>
          </div>
        </div>

        <div class="white-bg-block mB10 width100">
          <div class="analytics-section mT20 fc-scatter-collapse">
            <el-collapse
              v-model="activeTrend"
              @change="renderTrendChart()"
              class="fc-pushing-data-collapse fc-collapse-arrow-bg fc-analytics-collapse fc-scatter-collapse"
            >
              <el-collapse-item title="Reading Trends" name="1">
                <div v-if="activeTrend.includes('1')">
                  <spinner
                    v-if="loading"
                    :show="loading"
                    size="80"
                    class="analytics-spinner"
                  ></spinner>
                  <div
                    v-else-if="failed"
                    style="margin-top: 30px;opacity: 0.7;font-size: 13px;padding: 50px;line-height: 25px;text-align: center;"
                  >
                    <div>{{ $t('home.dashboard.data_loading_failed') }}</div>
                  </div>
                  <div
                    v-else-if="!reportObj.data"
                    style="margin-top: 30px;font-size: 13px;padding: 50px;line-height: 25px;text-align: center;"
                  >
                    <inline-svg
                      src="svgs/emptystate/reportlist"
                      iconClass="icon text-center icon-100"
                    ></inline-svg>
                    <div class="nowo-label f13">
                      {{ $t('common.wo_report.empty_data') }}
                      <span
                        class="bold"
                        v-if="resultObj && resultObj.dateRange"
                        >{{
                          resultObj.dateRange.value
                            | formatPeriod('MMM DD, YYYY')
                        }}</span
                      >
                    </div>
                  </div>
                  <div v-else>
                    <div v-if="!config.hidechart">
                      <f-multi-chart
                        ref="trendChart"
                        v-if="trendreportObj && config.scatterConfig"
                        :ismulti="config.scatterConfig"
                        :reportObj="trendreportObj"
                        :data="trendreportObj.data"
                        :options="
                          trendreportObj ? trendreportObj.options : null
                        "
                        :alarms="trendreportObj.alarms"
                        :booleanData="trendreportObj.booleanData"
                        :dateRange="trendreportObj.dateRange"
                        :resultObj="trendresultObj"
                        :config="config"
                        :hidecharttypechanger="config.hidecharttypechanger"
                      ></f-multi-chart>
                    </div>
                  </div>
                </div>
              </el-collapse-item>
            </el-collapse>
          </div>
        </div>

        <div class="white-bg-block mB10 width100" v-if="!config.hidetabular">
          <div class="analytics-section mT20 fc-scatter-collapse">
            <el-collapse
              v-model="activeTable"
              class="fc-pushing-data-collapse fc-collapse-arrow-bg fc-analytics-collapse fc-scatter-collapse"
            >
              <el-collapse-item title="Undelying Data" name="1">
                <div v-if="activeTable.includes('1')">
                  <spinner
                    v-if="loading"
                    :show="loading"
                    size="80"
                    class="analytics-spinner"
                  ></spinner>
                  <div
                    v-else-if="failed"
                    style="margin-top: 30px;opacity: 0.7;font-size: 13px;padding: 50px;line-height: 25px;text-align: center;"
                  >
                    <div>{{ $t('home.dashboard.data_loading_failed') }}</div>
                  </div>
                  <div
                    v-else-if="!reportObj.data"
                    style="margin-top: 30px;font-size: 13px;padding: 50px;line-height: 25px;text-align: center;"
                  >
                    <inline-svg
                      src="svgs/emptystate/reportlist"
                      iconClass="icon text-center icon-100"
                    ></inline-svg>
                    <div class="nowo-label f13">
                      {{ $t('common.wo_report.empty_data') }}
                      <span
                        class="bold"
                        v-if="resultObj && resultObj.dateRange"
                        >{{
                          resultObj.dateRange.value
                            | formatPeriod('MMM DD, YYYY')
                        }}</span
                      >
                    </div>
                  </div>
                  <f-tabular-report
                    v-else-if="!config.hidetabular"
                    ref="newTable"
                    :reportObject="trendresultObj"
                    :reportConfig="trendreportObj"
                    :widget="true"
                    :showWidgetTable="true"
                    class="new-analytics-table fc-scatter-con-tabular"
                  ></f-tabular-report>
                </div>
              </el-collapse-item>
            </el-collapse>
          </div>
        </div>
      </div>
      <report-filter
        v-if="showReportFilter"
        :visibility.sync="showReportFilter"
        :config="config.reportFilter"
        :existingOptions="existingOptions"
        @reportFilter="setReportFilter"
      ></report-filter>
      <scatter-chart-options
        v-if="showScatterChartOptions"
        :visibility.sync="showScatterChartOptions"
        :report.sync="reportObj"
        :config.sync="config"
      ></scatter-chart-options>
    </div>
  </div>
</template>
<script>
import NewDatePicker from '@/NewDatePicker'
import FNewChart from 'newcharts/components/FNewChartOptimize'
import FMobileChart from 'newcharts/components/FMobileChart'
import FMultiChart from 'newcharts/components/FMultiChart'
import FGridChart from 'newcharts/components/FGridChart'
import NewDataFormatHelper from 'pages/report/mixins/NewDataFormatHelper'
import AnalyticsMixin from 'pages/energy/analytics/mixins/AnalyticsMixin'
import FTabularReport from 'pages/report/components/FTabularReportNew'
import FMobileMultiChart from 'newcharts/components/FMobileMultiChart'
import NewDateHelper from '@/mixins/NewDateHelper'
import NewReportSummaryHelper from 'src/pages/report/mixins/NewReportSummaryHelper'
import ReportFilter from 'pages/energy/analytics/filter/ReportFilter'
import deepmerge from 'util/deepmerge'
import ScatterChartOptions from 'pages/energy/analytics/scatterAnalytics/ScatterChartOptions'
import { API } from '@facilio/api'
export default {
  props: ['config', 'noChartState', 'scatterType'],
  mixins: [
    NewDataFormatHelper,
    AnalyticsMixin,
    NewDateHelper,
    NewReportSummaryHelper,
  ],
  components: {
    NewDatePicker,
    FNewChart,
    FMultiChart,
    FTabularReport,
    FMobileChart,
    FMobileMultiChart,
    ReportFilter,
    ScatterChartOptions,
    FGridChart,
  },
  data() {
    return {
      loading: true,
      failed: false,
      reportObj: null,
      resultObj: null,
      chartgenerated: false,
      showTemplateFilter: false,
      componentkey: 0,
      FirstDateFromPicker: false,
      appliedDateFilter: null,
      trendLineDataPoints: null,
      showReportFilter: false,
      hasReportFilter: false,
      existingOptions: [],
      trendresultObj: null,
      trendreportObj: null,
      showScatterChartOptions: false,
      activeTable: [],
      activeTrend: [],
    }
  },
  mounted() {
    this.init()
  },
  watch: {
    config: {
      handler: function(newData, oldData) {
        if (!this.config.applyReportDate) {
          this.init()
        }
      },
      deep: true,
    },
  },
  computed: {
    getAvailablePeriods() {
      let operationOnId = this.config.dateFilter.operationOnId
      let avail = []

      avail.push({
        name: 'High-res',
        value: 0,
        enable: operationOnId !== 6 ? operationOnId !== 4 : true,
      })

      avail.push({
        name: 'Hourly',
        value: 20,
        enable: operationOnId !== 6 ? operationOnId !== 4 : true,
      })

      avail.push({
        name: 'Daily',
        value: 12,
        enable: true,
      })

      avail.push({
        name: 'Weekly',
        value: 11,
        enable: true,
      })

      avail.push({
        name: 'Monthly',
        value: 10,
        enable: true,
      })

      avail.push({
        name: 'Quarterly',
        value: 25,
        enable: operationOnId === 4 || operationOnId === 5,
      })

      avail.push({
        name: 'Yearly',
        value: 8,
        enable: operationOnId === 4,
      })

      avail.push({
        name: 'Hour of day', // 12, 1, 2, 3
        value: 19,
        enable: true,
      })

      avail.push({
        name: 'Day of week', // sun, mon
        value: 17,
        enable: true,
      })

      avail.push({
        name: 'Day of month', // 1,2,3
        value: 18,
        enable: true,
      })

      return avail
    },
  },
  methods: {
    init() {
      this.$store.dispatch('loadAlarmSeverity')
      if (this.config.dataPoints.length > 0) {
        this.initChart()
      }
    },
    initChart() {
      this.loading = true
      this.failed = false
      let Config = Object.freeze(Object.assign({}, this.config))
      let apiURL = Config.api ? Config.api : '/v2/report/readingReport'
      let params = null
      this.chartgenerated = false
      if (Config.alarmId) {
        apiURL = '/v2/report/fetchReadingsFromAlarm'
        params = {
          alarmId: Config.alarmId,
          mode: 1,
          xAggr: 0,
          startTime: Config.dateFilter.value[0],
          endTime: Config.dateFilter.value[1],
          showAlarms: true,
        }
      }
      if (!params) {
        params = this.getReportParams()
      }
      if (Config.analyticsType) {
        params.analyticsType = Config.analyticsType
      } else {
        params.analyticsType = -1
      }
      if (Config.scatterConfig) {
        params.scatterConfig = JSON.stringify({
          properties: Config.scatter,
          datapoints: Config.scatterConfig,
        })
      }
      params.newFormat = true

      if (
        this.reportObj &&
        this.reportObj.options &&
        this.reportObj.options.trendLine.enable &&
        this.reportObj.options.trendLine.selectedPoints.length
      ) {
        params.trendLine = JSON.stringify(this.reportObj.options.trendLine)
      }
      API.post(apiURL, params).then(({ error, data }) => {
        if (error) {
          console.log('error', error)
          this.loading = false
          this.failed = true
        } else {
          console.log('data', data)
        }
        let result = data
        // To remove object observer from reportObj
        let reportObj = JSON.parse(JSON.stringify(this.reportObj))
        result.xAggr = params.xAggr ? params.xAggr : null
        result.mode = params.mode
        if (Config.analyticsType) {
          result.report.analyticsType = Config.analyticsType
        }
        if (Config.filters) {
          result.filters = Config.filters
        }
        if (Config.sorting) {
          result.sorting = Config.sorting
        }
        if (Config.applyReportDate) {
          Config.dateFilter = result.dateRange = NewDateHelper.getDatePickerObject(
            Config.dateFilter.operatorId,
            result.report.dateValue
          )
          this.$nextTick(() => {
            this.$refs.newDatePicker.loadFromObject()
          })
        } else {
          result.dateRange = Config.dateFilter
        }
        this.trendresultObj = deepmerge.objectAssignDeep({}, result)
        if (reportObj && !Config.alarmId) {
          if (typeof this.noChartState !== 'undefined') {
            if (this.noChartState === false) {
              result.report.chartState = reportObj.options
            }
          } else {
            result.report.chartState = reportObj.options
          }
          result.report.tabularState = reportObj.params.tabularState
        } else if (Config.savedReport) {
          if (typeof this.noChartState !== 'undefined') {
            if (this.noChartState === false) {
              result.report.chartState = Config.savedReport.chartState
            }
          } else {
            result.report.chartState = Config.savedReport.chartState
          }
          result.report.tabularState = Config.savedReport.tabularState
        }

        this.existingOptions = []
        for (let dp of result.report.dataPoints) {
          if (dp.type != 5 && Config.analyticsType == 2) {
            let existingOptionIds = this.existingOptions.map(
              option => option.value
            )
            let assetId =
              dp.metaData && dp.metaData.parentIds
                ? dp.metaData.parentIds[0]
                : null
            if (assetId != null && !existingOptionIds.includes(assetId)) {
              this.existingOptions.push({
                value: assetId,
                label: this.$parent.filterToggle
                  ? 'Current Asset'
                  : dp.name.split(' (')[0],
              })
            }
          }
          if (dp.xDataPoint === true) {
            result['Readingdp'] = dp
            break
          }
        }
        if (result.report.trendLineDataPoints) {
          this.getTrendLineEquation(result)
          result.report.dataPoints = result.report.dataPoints.concat(
            result.report.trendLineDataPoints
          )
        }
        if (params.mode === 11) {
          let x = result.report.dataPoints[0].yAxis
          let y = result.report.dataPoints[0].xAxis
          result.report.dataPoints[0].yAxis = y
          result.report.dataPoints[0].xAxis = x
        }
        if (Config.scatterConfig) {
          result.scatterConfig = Config.scatterConfig
          this.trendresultObj.report.chartState = null
          this.prepareTrendReportObj(this.trendresultObj, params)
        }
        if (this.scatterType === 3) {
          result.scatterType = 'multi'
        }
        reportObj = this.prepareReport(result)
        if (Config.customizeC3 && reportObj.options) {
          reportObj.options['customizeC3'] = Config.customizeC3
        }
        if (Config.customPadding && reportObj.options) {
          reportObj.options['customPadding'] = Config.customPadding
        }
        if (Config.customizeChartOptions && reportObj.options) {
          reportObj.options = deepmerge.objectAssignDeep(
            reportObj.options,
            Config.customizeChartOptions
          )
        }
        if (Config.mode === 1 && Config.period === 0) {
          this.hasReportFilter =
            Object.keys(this.config.reportFilter.timeFilter.conditions).length >
            0
          this.hasReportFilter =
            Object.keys(this.config.reportFilter.dataFilter.conditions).length >
            0
              ? true
              : this.hasReportFilter
        }
        if (Config.scatterConfig && reportObj && reportObj.options) {
          reportObj.options.settings.timeperiod = true
        }
        if (Config.template) {
          let currentTemplate = JSON.parse(JSON.stringify(Config.template))
          let filters = this.getFiltersFromCriteria(currentTemplate)
          this.loadReportTemplateValues(currentTemplate, filters).then(() => {
            currentTemplate.parentId =
              currentTemplate.parentId === null
                ? currentTemplate.defaultValue
                : currentTemplate.parentId
            this.$set(this.resultObj.report, 'currentTemplate', currentTemplate)
            this.showTemplateFilter = true
          })
        }
        if (
          Config.isFromAlarmSummary ||
          (this.$route.query &&
            this.$route.query.isFromAlarmSummary &&
            !this.resultObj)
        ) {
          if (reportObj.booleanData) {
            reportObj.options.dataPoints = this.groupNonBooleanPoints(
              reportObj.options.dataPoints
            )
          }
        }
        if (
          (this.$route.query && this.$route.query.alarmId) ||
          Config.alarmId
        ) {
          reportObj.options.settings.alarm = true
        }
        this.resultObj = result
        reportObj.params = params
        reportObj.alarms = this.prepareBooleanReport(result.reportData.aggr)
        if (Config.alarmId || this.alarmId) {
          reportObj.alarms.barSize = 'medium'
        }
        if (Config.regionConfig) {
          reportObj.options.regionConfig = Config.regionConfig
        }
        if (Config.hideDataPoints) {
          reportObj.options.hideDataPoints = Config.hideDataPoints
        }
        if (Config.chartType) {
          reportObj.options.chartType = Config.chartType
        }
        if (Config.diffChartConfig) {
          reportObj.options.diffChartConfig = Config.diffChartConfig
        }
        if (Config.point) {
          reportObj.options.point = Config.point
        }
        if (Config.zoom) {
          reportObj.options.zoom = Config.zoom
        }
        if (Config.axes) {
          reportObj.options.axes = Config.axes
        }
        reportObj.options.xFormat = 'MM-DD-YYYY'
        if (Config.xFormat) {
          reportObj.options.xFormat = Config.xFormat
        }
        if (Config.axis) {
          Object.keys(Config.axis).forEach(rt => {
            if (reportObj.options.axis[rt].label) {
              reportObj.options.axis[rt].label.text = Config.axis[rt].label
            }
          })
        }
        reportObj.options.predictionTimings = Config.predictionTimings
        if (
          this.scatterType !== 3 &&
          !(
            this.$route.meta &&
            this.$route.meta.layout &&
            this.$route.meta.layout === 'mobile'
          )
        ) {
          this.$nextTick(() => {
            if (this.$refs['newChart']) {
              this.$refs['newChart'].render(reportObj)
            }
          })
        }
        this.reportObj = reportObj
        this.$emit('reportLoaded', reportObj, result)
        this.loading = false
      })
    },
    getReportParams() {
      let baselines
      if (
        this.config.baseLine &&
        Array.isArray(this.config.baseLine) &&
        this.config.baseLine.length
      ) {
        if (this.config.baseLine[0] > 0) {
          let bl = {}
          bl.baseLineId = this.config.baseLine[0]
          if (this.config.baseLine.length > 1) {
            bl.adjustType = this.config.baseLine[1]
          }

          let blList = []
          blList.push(bl)

          baselines = blList
        }
      } else if (this.config.baseLine && this.config.baseLine > 0) {
        baselines = [
          {
            baseLineId: this.config.baseLine,
          },
        ]
      }

      let baselineNames
      if (baselines && this.baseLines) {
        baselineNames = []
        baselines.forEach(baseline => {
          let baseLineName = this.baseLines.find(
            bl => bl.id === baseline.baseLineId
          ).name
          baselineNames.push(baseLineName)
        })
      }

      let fields = []
      if (this.config.mode === 3) {
        let parentId = []
        let fieldId = null
        let yAggr = 3
        for (let dp of this.config.dataPoints) {
          parentId.push(dp.parentId)
          fieldId = dp.yAxis.fieldId
          yAggr = this.getYAggr(dp)
        }

        fields.push({
          parentId: parentId,
          yAxis: {
            fieldId: fieldId,
            aggr: this.config.period !== 0 && yAggr === 0 ? 3 : yAggr,
          },
          type: 1,
          aliases: {
            actual: 'A',
          },
        })
        this.setAlias(fields, baselineNames)
      } else {
        this.setAlias(this.config.dataPoints, baselineNames)

        for (let dp of this.config.dataPoints) {
          let metaData = {}
          if (dp.categoryId) {
            metaData.categoryId = dp.categoryId
          }

          dp.yAxis.aggr = this.getYAggr(dp)
          let params = {}
          if (this.config.template) {
            params = {
              parentId: dp.parentId
                ? Array.isArray(dp.parentId)
                  ? dp.parentId
                  : [dp.parentId]
                : null,
              buildingId: dp.buildingId ? dp.buildingId : -1,
              moduleName: dp.moduleName,
              metaData: metaData,
              yAxis: { fieldId: dp.yAxis.fieldId, aggr: dp.yAxis.aggr },
              aliases: dp.aliases,
              type: dp.type > 0 ? dp.type : 1,
              predictedTime: dp.predictedTime,
              duplicateDataPoint: dp.duplicateDataPoint,
            }
          } else {
            params = {
              parentId: dp.parentId
                ? Array.isArray(dp.parentId)
                  ? dp.parentId
                  : [dp.parentId]
                : null,
              name: dp.name,
              buildingId: dp.buildingId ? dp.buildingId : -1,
              moduleName: dp.moduleName,
              metaData: metaData,
              yAxis: dp.yAxis,
              aliases: dp.aliases,
              type: dp.type > 0 ? dp.type : 1,
              predictedTime: dp.predictedTime,
              duplicateDataPoint: dp.duplicateDataPoint,
            }
          }
          if (
            this.config.dimensionDataPoint !== null &&
            this.config.mode !== 11
          ) {
            let xdp = this.config.dataPoints[this.config.dimensionDataPoint]
            if (xdp && xdp.yAxis.fieldId && xdp.parentId) {
              if (
                dp.yAxis.fieldId === xdp.yAxis.fieldId &&
                xdp.parentId === dp.parentId &&
                xdp.aliases.actual === dp.aliases.actual
              ) {
                params['xDataPoint'] = true
              }
            } else if (xdp && xdp.type === 2) {
              if (xdp.aliases.actual === dp.aliases.actual) {
                params['xDataPoint'] = true
              }
            }
          }
          if (dp.xDataPoint) {
            params['xDataPoint'] = true
          }

          fields.push(params)
        }
      }

      if (
        this.config.sorting &&
        [6, 7, 8, 9, 10, 'mv'].includes(this.config.mode)
      ) {
        if (this.config.sorting.orderByFunc || this.config.sorting.limit) {
          let sortByField = this.config.sorting.sortByField
          if (sortByField) {
            let field = fields.find(f => f.yAxis.fieldId === sortByField)
            if (!field) {
              sortByField = null
            }
          }
          if (!sortByField) {
            sortByField = fields[0].yAxis.fieldId
            this.config.sorting.sortByField = fields[0].yAxis.fieldId
          }
          let field = null
          if (sortByField) {
            field = fields.find(f => f.yAxis.fieldId === sortByField)
          } else {
            if (this.config.dataPoints.length <= 1) {
              field = fields[0]
            }
          }

          if (field) {
            field.defaultSortPoint = true

            if (this.config.sorting.orderByFunc) {
              field.orderByFunc = this.config.sorting.orderByFunc
            }
            if (this.config.sorting.limit) {
              field.limit = this.config.sorting.limit
            }
          }
        }
      }
      let trendLine = null
      if (this.config.savedReport && this.config.savedReport.chartState) {
        trendLine = JSON.stringify(
          JSON.parse(this.config.savedReport.chartState).trendLine
        )
      }
      let params = {
        mode:
          this.config.mode === 'reading'
            ? 1
            : ['mv'].includes(this.config.mode)
            ? 8
            : this.config.mode,
        startTime: this.config.dateFilter.value[0],
        endTime: this.config.dateFilter.value[1],
        groupByTimeAggr: this.config.groupByTimeAggr,
        fields: JSON.stringify(fields),
        transformWorkflow: this.config.transformWorkflow,
        trendLine: trendLine,
      }
      if (this.config.mode == 1 && this.config.period == 0) {
        if (
          typeof this.config.reportFilter !== 'undefined' &&
          this.config.reportFilter.hasOwnProperty('timeFilter')
        ) {
          params.timeFilter = JSON.stringify(
            this.config.reportFilter.timeFilter
          )
        }
        if (
          typeof this.config.reportFilter !== 'undefined' &&
          this.config.reportFilter.hasOwnProperty('dataFilter')
        ) {
          let dConfig = this.config.reportFilter.dataFilter
          for (let key in dConfig.conditions) {
            let condition = dConfig.conditions[key]
            condition.currentAsset =
              this.$parent.filterToggle &&
              condition.parentId == this.config.dataPoints[0]['parentId']
          }
          params.dataFilter = JSON.stringify(
            this.config.reportFilter.dataFilter
          )
        }
      }

      if (![6, 7, 9, 10].includes(this.config.mode)) {
        params.xAggr =
          this.config.mode === 1 ||
          this.config.mode === 3 ||
          this.config.mode === 4 ||
          this.config.mode === 5
            ? this.config.period
            : 0
      }
      if (
        this.reportObj &&
        this.reportObj.options &&
        this.reportObj.options.settings
      ) {
        params.showAlarms = this.reportObj.options.settings.alarm
        params.showSafeLimit = this.reportObj.options.settings.safelimit
      } else if (this.config.savedReport) {
        params.showAlarms = this.config.savedReport.showAlarms
        params.showSafeLimit = this.config.savedReport.showSafeLimit
      } else {
        params.showSafeLimit = true
      }
      if (baselines) {
        params.baseLines = JSON.stringify(baselines)
      }
      if (
        this.config.filters &&
        ![1, 11, 'reading'].includes(this.config.mode)
      ) {
        if (
          this.config.filters.xCriteriaMode &&
          (['mv'].includes(this.config.mode) ||
            (this.config.mvPoints && this.config.mvPoints.length > 0))
        ) {
          params.xCriteriaMode = 5
        } else if (this.config.filters.xCriteriaMode) {
          params.xCriteriaMode = this.config.filters.xCriteriaMode

          if (
            this.config.filters.parentId &&
            this.config.filters.parentId.length
          ) {
            params.parentId = this.config.filters.parentId
          } else {
            let categoryId = this.config.filters.assetCategory
            if (!categoryId || !categoryId.length) {
              categoryId = []
              for (let dp of this.config.dataPoints) {
                if (
                  categoryId.indexOf(dp.categoryId) === -1 &&
                  dp.categoryId !== null
                ) {
                  categoryId.push(dp.categoryId)
                }
              }
            }

            if (categoryId.length) {
              params.assetCategory = categoryId
            }

            if (
              this.config.filters.spaceId &&
              this.config.filters.spaceId.length
            ) {
              params.spaceId = this.config.filters.spaceId
            }
          }
        }
      }

      if (this.$route.query && this.$route.query.alarmId) {
        params.alarmId = parseInt(this.$route.query.alarmId)
        params.showAlarms = true
      }

      return params
    },
    getYAggr(dp) {
      if (dp.yAxis && dp.yAxis.aggr && [2, 3, 4, 5].includes(dp.yAxis.aggr)) {
        return dp.yAxis.aggr === 6 ? 3 : dp.yAxis.aggr
      } else {
        return dp.yAxis &&
          dp.yAxis.unitStr &&
          ['currency', 'kwh', 'co2', 'kg', 'mwh'].includes(
            dp.yAxis.unitStr.trim().toLowerCase()
          )
          ? 3
          : 2
      }
    },
    prepareTrendReportObj(result, params) {
      let Config = JSON.parse(JSON.stringify(this.config))
      if (Config.mode === 1 && Config.period === 0) {
        this.hasReportFilter =
          Object.keys(this.config.reportFilter.timeFilter.conditions).length > 0
        this.hasReportFilter =
          Object.keys(this.config.reportFilter.dataFilter.conditions).length > 0
            ? true
            : this.hasReportFilter
      }
      result.showReportFilter = this.hasReportFilter
      let reportObj = this.prepareReport(result)
      if (Config.template) {
        let currentTemplate = JSON.parse(JSON.stringify(Config.template))
        let filters = this.getFiltersFromCriteria(currentTemplate)
        this.loadReportTemplateValues(currentTemplate, filters).then(() => {
          currentTemplate.parentId =
            currentTemplate.parentId === null
              ? currentTemplate.defaultValue
              : currentTemplate.parentId
          this.$set(
            this.trendresultObj.report,
            'currentTemplate',
            currentTemplate
          )
          this.showTemplateFilter = true
        })
      }
      if (
        Config.isFromAlarmSummary ||
        (this.$route.query &&
          this.$route.query.isFromAlarmSummary &&
          !this.trendresultObj)
      ) {
        if (reportObj.booleanData) {
          reportObj.options.dataPoints = this.groupNonBooleanPoints(
            reportObj.options.dataPoints
          )
        }
      }
      if ((this.$route.query && this.$route.query.alarmId) || Config.alarmId) {
        reportObj.options.settings.alarm = true
      }
      reportObj.params = params

      reportObj.alarms = this.prepareBooleanReport(result.reportData.aggr)
      if (Config.alarmId || this.alarmId) {
        reportObj.alarms.barSize = 'medium'
      }
      if (Config.regionConfig) {
        reportObj.options.regionConfig = Config.regionConfig
      }
      if (Config.hideDataPoints) {
        reportObj.options.hideDataPoints = Config.hideDataPoints
      }
      if (Config.chartType) {
        reportObj.options.chartType = Config.chartType
      }
      if (Config.diffChartConfig) {
        reportObj.options.diffChartConfig = Config.diffChartConfig
      }
      if (Config.point) {
        reportObj.options.point = Config.point
      }
      if (Config.zoom) {
        reportObj.options.zoom = Config.zoom
      }
      if (Config.axes) {
        reportObj.options.axes = Config.axes
      }
      reportObj.options.xFormat = 'MM-DD-YYYY'
      if (Config.xFormat) {
        reportObj.options.xFormat = Config.xFormat
      }
      if (Config.axis) {
        Object.keys(Config.axis).forEach(rt => {
          if (reportObj.options.axis[rt].label) {
            reportObj.options.axis[rt].label.text = Config.axis[rt].label
          }
        })
      }
      reportObj.options.predictionTimings = Config.predictionTimings
      this.trendreportObj = reportObj
      if (Config.customizeC3 && this.trendreportObj.options) {
        this.trendreportObj.options['customizeC3'] = Config.customizeC3
      }
      if (Config.customizeChartOptions && this.trendreportObj.options) {
        this.trendreportObj.options = deepmerge.objectAssignDeep(
          this.trendreportObj.options,
          Config.customizeChartOptions
        )
      }
    },
    onPeriodChange() {
      if (!this.config.dataPoints.length) {
        let filter = 'D'
        if (this.config.period === 20) {
          filter = 'W'
        } else if (this.config.period === 12) {
          filter = 'M'
        } else if (this.config.period === 10) {
          filter = 'Y'
        }
        this.config.dateFilter = this.getDefaultDateFilter(filter)
      }
    },
    setReportFilter(reportFilter) {
      this.hasReportFilter =
        Object.keys(reportFilter.timeFilter.conditions).length > 0
      this.hasReportFilter =
        Object.keys(reportFilter.dataFilter.conditions).length > 0
          ? true
          : this.hasReportFilter
      this.trendreportObj.options.settings.filterBar = this.hasReportFilter
      this.$set(this.config, 'reportFilter', reportFilter)
    },
    getTrendLineEquation(result) {
      let self = this
      let trendLineProp = result.reportData.trendLineProp
      let trendLineObj = {}
      if (this.config.savedReport && this.config.savedReport.chartState) {
        trendLineObj = JSON.parse(this.config.savedReport.chartState).trendLine
      } else {
        trendLineObj = self.reportObj.options.trendLine
      }

      let decimalCount = trendLineObj.decimal
      for (let alias in trendLineProp) {
        let coef = trendLineProp[alias].coef
        coef.forEach(function(data, index) {
          coef[index] =
            self.showDecimal(data, decimalCount) +
            (index !== 0 ? 'x^' + index : '')
        })
        coef.reverse()
        coef = coef.join(' + ')
        let dataPoint = result.report.trendLineDataPoints.filter(
          dataPoint => dataPoint.aliases.actual === alias
        )
        dataPoint[0].name = coef
        if (trendLineObj.showr2) {
          dataPoint[0].name =
            'R^2 = ' +
            self.showDecimal(
              trendLineProp[dataPoint[0].aliases.actual].rSquare,
              decimalCount
            ) +
            '; ' +
            dataPoint[0].name
        }
      }
    },
    showDecimal(data, decimalCount) {
      let splitData = data.split('.')
      if (splitData[1]) {
        return splitData[0] + '.' + splitData[1].slice(0, decimalCount)
      }
      return data
    },
    openReportFilterDialog() {
      this.showReportFilter = true
    },
    openChartOptions() {
      this.showScatterChartOptions = true
    },
    forceRerender() {
      if (this.$refs['newDatePicker'] && !this.FirstDateFromPicker) {
        this.componentkey = this.componentkey + 1
      } else {
        this.setDateFilter(this.appliedDateFilter)
      }
    },
    chartgenerate(value) {
      setTimeout(() => (this.chartgenerated = value), 10)
    },
    setDateFilter(dateFilter) {
      this.FirstDateFromPicker = true
      this.appliedDateFilter = dateFilter
      this.$set(this.config, 'dateFilter', dateFilter)
    },
    resize() {
      if (this.$refs['newChart'] && this.scatterType !== 3) {
        this.$refs['newChart'].resize()
      }
      if (this.$refs['multiChart']) {
        this.$refs['multiChart'].resize()
      }
      if (this.$refs['gridChart']) {
        this.$refs['gridChart'].resize()
      }
      if (this.$refs['heatmap']) {
        this.$refs['heatmap'].resize()
      }
      if (this.$refs['treemap']) {
        this.$refs['treemap'].resize()
      }
      if (this.$refs['newTable']) {
        this.$refs['newTable'].resize()
      }
      if (this.$refs['trendChart']) {
        this.$refs['trendChart'].resize()
      }
    },
    renderTrendChart() {
      if (this.$refs['trendChart']) {
        this.$refs['trendChart'].resize()
      }
    },
  },
}
</script>
