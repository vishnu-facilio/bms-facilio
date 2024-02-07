<template>
  <div>
    <div class="white-bg-block mB10 width100 height550 fc-scatter-con">
      <div class="analytics-section mT20 mR20">
        <div class="fc-report-widget-header p0 pT10">
          <div
            :style="
              !config.scatterConfig
                ? 'text-align: center;'
                : 'height:50px;width: -webkit-fill-available;'
            "
            class="new-analytics-filter-section flex-middle"
          >
            <new-date-picker
              ref="newDatePicker"
              :key="componentkey"
              v-if="!config.hidedatepicker"
              :zone="$timezone"
              class="filter-field date-filter-comp inline fc-scatter-date-picker"
              v-bind:style="
                config && config.analyticsType === 2
                  ? config.scatterConfig
                    ? 'left:0;float:left;'
                    : 'margin-left:120px;'
                  : ''
              "
              :dateObj.sync="config.dateFilter"
              @date="setDateFilter"
              :isDateFixed="config.applyReportDate"
            ></new-date-picker>
            <div
              v-if="
                this.reportObj &&
                  this.reportObj.options &&
                  this.reportObj.options.settings.timeperiod === true &&
                  [1, 4].includes(config.mode)
              "
              class="chart-icon pointer flRight pB8 el-popover__reference"
              :style="
                !config.scatterConfig ? 'margin-right: 120px;' : 'float:left;'
              "
            >
              <el-select
                class="period-select fc-input-full-border2 "
                :class="{ width20: !config.scatterConfig }"
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
                  resultObj.report.dataPoints.length !== 0 &&
                  !isRegression
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
              ></InlineSvg>
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
        <prediction-timings-bar
          :reportObj="reportObj"
          :config="config"
          class=" prediction-timing-bar"
          v-if="
            !$mobile &&
              predictive &&
              config.predictionTimings &&
              config.predictionTimings.length
          "
        ></prediction-timings-bar>
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
            <div
              v-if="
                $route.meta &&
                  $route.meta.layout &&
                  $route.meta.layout === 'mobile' &&
                  reportObj.options.settings.chartMode === 'multi'
              "
              class="empty-multi-chart"
            >
              Currently this chart not supported in the mobile
            </div>
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
                class="mobile-chart fc-scatter-analytics-legends"
              ></f-mobile-chart>
            </div>
            <f-multi-chart
              ref="multiChart"
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
              :hidecharttypechanger="
                config.hidecharttypechanger || isRegression
              "
              :config="config"
            ></f-multi-chart>
            <f-new-chart
              ref="newChart"
              :hidecharttypechanger="
                config.hidecharttypechanger || isRegression
              "
              :resultObj="resultObj"
              :reportObj="reportObj"
              v-if="!isHeatMap && !isTreeMap && scatterType !== 3"
              :options="reportObj ? reportObj.options : null"
              :fixedChartHeight="config.fixedChartHeight"
              @chartgenerate="chartgenerate"
              :config="config"
            ></f-new-chart>
            <FNewHeatMap
              v-if="isHeatMap"
              ref="heatmap"
              :config="config"
              :width="
                chartDimensions && chartDimensions.width
                  ? chartDimensions.width
                  : null
              "
              :height="
                chartDimensions && chartDimensions.height
                  ? chartDimensions.height
                  : null
              "
              :resultObject="resultObj"
              :reportObject="reportObj"
            ></FNewHeatMap>

            <FTreeMap
              v-if="isTreeMap"
              ref="treemap"
              :config="config"
              :width="
                chartDimensions && chartDimensions.width
                  ? chartDimensions.width
                  : null
              "
              :height="
                chartDimensions && chartDimensions.height
                  ? chartDimensions.height
                  : null
              "
              :resultObject="resultObj"
              :reportObject="reportObj"
            ></FTreeMap>
          </div>
        </div>
      </div>
    </div>

    <div class="white-bg-block mL20 mR20 mB10 width100">
      <div class="analytics-section mT20 mR20 fc-scatter-collapse">
        <!-- <div class="fc-report-widget-header">
          <div class="fc-black-15 bold">
            Reading Trends
          </div>
        </div> -->
        <el-collapse
          class="fc-pushing-data-collapse fc-collapse-arrow-bg fc-analytics-collapse fc-scatter-collapse"
        >
          <el-collapse-item title="Reading Trends" name="1">
            <div>
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
                  <f-multi-chart
                    ref="multiChart"
                    v-if="trendreportObj && config.scatterConfig"
                    :reportObj="trendreportObj"
                    :data="trendreportObj.data"
                    :options="trendreportObj ? trendreportObj.options : null"
                    :alarms="trendreportObj.alarms"
                    :booleanData="trendreportObj.booleanData"
                    :dateRange="trendreportObj.dateRange"
                    :resultObj="trendresultObj"
                    :config="config"
                  ></f-multi-chart>
                </div>
              </div>
            </div>
          </el-collapse-item>
        </el-collapse>
      </div>
    </div>

    <div class="white-bg-block mL20 mR20 mB10 width100">
      <div class="analytics-section mT20 mR20">
        <!-- <div class="fc-report-widget-header">
          <div class="fc-black-15 bold">
            Underlying Data
          </div>
        </div> -->
        <el-collapse
          class="fc-pushing-data-collapse fc-collapse-arrow-bg fc-analytics-collapse fc-scatter-collapse"
        >
          <el-collapse-item title="Undelying Data" name="1">
            <div v-if="!loading">
              <f-tabular-report
                ref="newTable"
                :reportObject="resultObj"
                :reportConfig="reportObj"
                :widget="true"
                :showWidgetTable="true"
                v-if="!config.hidetabular"
                class="new-analytics-table mL30 mR30 fc-scatter-con-tabular"
                :class="{ 'regression-table-fit': isRegression }"
              ></f-tabular-report>
            </div>
          </el-collapse-item>
        </el-collapse>
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
    ></scatter-chart-options>
  </div>
</template>

<script>
import NewDatePicker from '@/NewDatePicker'
import FNewChart from 'newcharts/components/FNewChartOptimize'
import FMobileChart from 'newcharts/components/FMobileChart'
import FMultiChart from 'newcharts/components/FMultiChart'
import NewDataFormatHelper from 'pages/report/mixins/NewDataFormatHelper'
import AnalyticsMixin from 'pages/energy/analytics/mixins/AnalyticsMixin'
import FTabularReport from 'pages/report/components/FTabularReportNew'
import FMobileMultiChart from 'newcharts/components/FMobileMultiChart'
import PredictionTimingsBar from 'pages/energy/analytics/components/PredictionTimingsBar'
import NewDateHelper from '@/mixins/NewDateHelper'
import FNewHeatMap from 'src/pages/energy/analytics/components/FNewHeatMap'
import FTreeMap from 'src/pages/energy/analytics/components/FTreeMap'
import NewReportSummaryHelper from 'src/pages/report/mixins/NewReportSummaryHelper'
import ReportFilter from 'pages/energy/analytics/filter/ReportFilter'
import deepmerge from 'util/deepmerge'
import ScatterChartOptions from 'pages/energy/analytics/scatterAnalytics/ScatterChartOptions'

export default {
  props: [
    'config',
    'baseLines',
    'readings',
    'showChartMode',
    'showTimePeriod',
    'showFilterBar',
    'noChartState',
    'heatMapOptions',
    'chartDimensions',
    'scatterType',
  ],
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
    PredictionTimingsBar,
    FNewHeatMap,
    FTreeMap,
    ReportFilter,
    ScatterChartOptions,
  },
  data() {
    return {
      loading: true,
      predictive: true,
      failed: false,
      reportObj: null,
      resultObj: null,
      showRangeChartDialog: false,
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
    }
  },
  mounted() {
    this.init()
  },
  computed: {
    isHeatMap() {
      if (
        this.$route &&
        this.$route.meta &&
        this.$route.meta.analyticsType === 'heatmap'
      ) {
        return true
      }
      return false
    },
    isTreeMap() {
      if (
        this.$route &&
        this.$route.meta &&
        this.$route.meta.analyticsType === 'treemap'
      ) {
        return true
      }
      return false
    },
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
    isRegression() {
      if (this.$route.path.includes('regression')) {
        return true
      } else {
        return false
      }
    },
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
    this.$store.dispatch('loadAlarmSeverity')
  },
  watch: {
    config: {
      handler: function(newData, oldData) {
        if (!this.config.applyReportDate) {
          if (
            this.config.mode &&
            (this.config.mode === 'reading' || this.config.mode === 11)
          ) {
            if (this.config.dimensionDataPoint !== null) {
              this.init(false)
            }
          } else {
            this.init(false)
          }
        }
      },
      deep: true,
    },
    heatMapOptions: {
      handler: function(newData, oldData) {
        if (this.$refs['heatmap']) {
          this.$refs['heatmap'].rerender()
        }
        if (this.$refs['treemap']) {
          this.$refs['treemap'].rerender()
        }
      },
      deep: true,
    },
    chartDimensions: {
      handler: function(newData, oldData) {
        if (this.$refs['heatmap']) {
          this.$refs['heatmap'].resize()
        }
        if (this.$refs['treemap']) {
          this.$refs['treemap'].resize()
        }
      },
      deep: true,
    },
    getAvailablePeriods: {
      handler(newData, oldData) {
        if (this.config.analyticsType !== 3) {
          let avail = this.getAvailablePeriods
          let selected = avail.find(
            a => a.value === this.config.period && a.enable
          )
          if (!selected) {
            let filterName = this.config.dateFilter.operationOn

            let defaultPeriod = avail.filter(a => a.enable)[0].value
            if (filterName === 'week') {
              defaultPeriod = 12
            } else if (filterName === 'month') {
              defaultPeriod = 12
            } else if (filterName === 'year') {
              defaultPeriod = 10
            }
            this.config.period = defaultPeriod
          }
        }
      },
      immediate: true,
    },
  },
  methods: {
    forceRerender() {
      if (this.$refs['newDatePicker'] && !this.FirstDateFromPicker) {
        this.componentkey = this.componentkey + 1
      } else {
        this.setDateFilter(this.appliedDateFilter)
      }
    },
    changeParentForTemplate() {
      if (this.config.template) {
        this.config.template.parentId = this.resultObj.report.currentTemplate.parentId
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
    chartgenerate(value) {
      setTimeout(() => (this.chartgenerated = value), 10)
    },
    addRangePoints(val, remainingDataPoints) {
      console.log('addRangePoints')
      console.log(val)
      let ranges = ['min', 'max', 'avg']
      let dataPoints = []
      for (let rangeConfig of val) {
        for (let key in rangeConfig) {
          let config = {}
          if (ranges.includes(key)) {
            config['parentId'] = rangeConfig[key].parentId
            config['prediction'] = false
            config['aliases'] = {
              actual: rangeConfig[key].alias,
            }
            config['yAxis'] = {
              aggr: rangeConfig[key].aggr,
              fieldId: rangeConfig[key].fieldId,
            }
            dataPoints.push(config)
          }
        }
      }

      for (let dataPoint of remainingDataPoints) {
        let config = {}
        config['parentId'] = dataPoint.metaData.parentIds[0]
        config['prediction'] = false
        config['aliases'] = {
          actual: dataPoint.aliases.actual,
        }
        config['yAxis'] = {
          aggr: dataPoint.yAxis.aggr,
          fieldId: dataPoint.yAxis.fieldId,
        }
        dataPoints.push(config)
      }
      console.log(dataPoints)
      let analyticConfig = this.$helpers.cloneObject(this.config)
      analyticConfig.dataPoints = dataPoints
      analyticConfig.period =
        typeof this.config.period !== 'undefined' && this.config.period !== null
          ? this.config.period === 0
            ? this.getXAggr()
            : this.config.period
          : 0
      this.$emit('update:config', analyticConfig)
    },
    setDateFilter(dateFilter) {
      this.FirstDateFromPicker = true
      this.appliedDateFilter = dateFilter
      this.$set(this.config, 'dateFilter', dateFilter)
      // this.$emit('update:config', this.config)
    },
    init() {
      this.$store.dispatch('loadAlarmSeverity')
      if (this.config.dataPoints.length > 0) {
        this.initChart()
      }
    },
    resize() {
      if (this.$refs['newChart'] && this.scatterType !== 3) {
        this.$refs['newChart'].resize()
      }
      if (this.$refs['multiChart']) {
        this.$refs['multiChart'].resize()
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

    initChart() {
      let self = this
      self.loading = true
      self.failed = false
      let Config = Object.assign({}, this.config)
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
      if (Config.readingRuleId) {
        params.readingRuleId = Config.readingRuleId
      }
      if (Config.isWithPrerequsite) {
        params.isWithPrerequsite = Config.isWithPrerequsite
      }
      if (!params) {
        params = self.getReportParams()
      }
      if (Config.template && Config.template.show) {
        Config.template.parentId =
          Config.template.parentId === null
            ? Config.dataPoints[0].parentId
            : Config.template.parentId
        params.template = Config.template
      }
      if (Config.analyticsType) {
        params.analyticsType = Config.analyticsType
      } else {
        params.analyticsType = -1
      }
      if (Config && Config.format) {
        params.hmAggr = Config.format
      }
      params.newFormat = true
      params.defaultDate = Config.defaultDate

      if (
        this.reportObj &&
        this.reportObj.options &&
        this.reportObj.options.trendLine.enable &&
        this.reportObject.options.trendLine.selectedPoints.length
      ) {
        params.trendLine = JSON.stringify(this.reportObj.options.trendLine)
      }
      self.$http
        .post(apiURL, params)
        .then(function(response) {
          let result = response.data.result
          // To remove object observer from reportObj
          let reportObj = JSON.parse(JSON.stringify(self.reportObj))
          result.xAggr = params.xAggr ? params.xAggr : null
          result.mode = params.mode
          if (self.config.defaultDate) {
            self.config.defaultDate = false
            Config.dateFilter.operatorId = result.report.dateOperator
            self.config.dateFilter = result.dateRange = NewDateHelper.getDatePickerObject(
              Config.dateFilter.operatorId,
              result.report.dateValue
            )
            self.$nextTick(() => {
              self.$refs.newDatePicker.loadFromObject()
            })
          }
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
            self.$nextTick(() => {
              self.$refs.newDatePicker.loadFromObject()
            })
          } else {
            result.dateRange = Config.dateFilter
          }
          self.trendresultObj = deepmerge.objectAssignDeep({}, result)
          if (reportObj && !Config.alarmId) {
            if (typeof self.noChartState !== 'undefined') {
              if (self.noChartState === false) {
                result.report.chartState = reportObj.options
              }
            } else {
              result.report.chartState = reportObj.options
            }
            result.report.tabularState = reportObj.params.tabularState
          } else if (Config.savedReport) {
            if (typeof self.noChartState !== 'undefined') {
              if (self.noChartState === false) {
                result.report.chartState = Config.savedReport.chartState
              }
            } else {
              result.report.chartState = Config.savedReport.chartState
            }
            result.report.tabularState = Config.savedReport.tabularState
          }

          self.existingOptions = []
          for (let dp of result.report.dataPoints) {
            if (dp.type != 5 && Config.analyticsType == 2) {
              let existingOptionIds = self.existingOptions.map(
                option => option.value
              )
              let assetId =
                dp.metaData && dp.metaData.parentIds
                  ? dp.metaData.parentIds[0]
                  : null
              if (assetId != null && !existingOptionIds.includes(assetId)) {
                self.existingOptions.push({
                  value: assetId,
                  label: self.$parent.filterToggle
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
            self.getTrendLineEquation(result)
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
            self.prepareTrendReportObj(self.trendresultObj, params)
          }
          if (self.scatterType === 3) {
            result.scatterType = 'multi'
          }
          reportObj = self.prepareReport(result)
          if (
            Config.analyticsType === 2 &&
            Config.mode === 1 &&
            Config.period === 0
          ) {
            self.hasReportFilter =
              Object.keys(self.config.reportFilter.timeFilter.conditions)
                .length > 0
            self.hasReportFilter =
              Object.keys(self.config.reportFilter.dataFilter.conditions)
                .length > 0
                ? true
                : self.hasReportFilter
          }
          if (Config.scatterConfig && reportObj && reportObj.options) {
            reportObj.options.settings.timeperiod = true
          }
          if (self.isHeatMap || self.isTreeMap) {
            reportObj.options.heatMapOptions = self.heatMapOptions
          }
          if (Config.template) {
            let currentTemplate = JSON.parse(JSON.stringify(Config.template))
            let filters = self.getFiltersFromCriteria(currentTemplate)
            self.loadReportTemplateValues(currentTemplate, filters).then(() => {
              currentTemplate.parentId =
                currentTemplate.parentId === null
                  ? currentTemplate.defaultValue
                  : currentTemplate.parentId
              self.$set(
                self.resultObj.report,
                'currentTemplate',
                currentTemplate
              )
              self.showTemplateFilter = true
            })
          }
          if (
            Config.isFromAlarmSummary ||
            (self.$route.query &&
              self.$route.query.isFromAlarmSummary &&
              !self.resultObj)
          ) {
            if (reportObj.booleanData) {
              reportObj.options.dataPoints = self.groupNonBooleanPoints(
                reportObj.options.dataPoints
              )
            }
          }
          if (
            (self.$route.query && self.$route.query.alarmId) ||
            Config.alarmId
          ) {
            reportObj.options.settings.alarm = true
          }
          self.resultObj = result
          reportObj.params = params

          reportObj.alarms = self.prepareBooleanReport(result.reportData.aggr)
          if (Config.alarmId || self.alarmId) {
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
            reportObj.options.settings.chartMode === 'single' &&
            self.scatterType !== 3 &&
            !self.isHeatMap &&
            !self.isTreeMap &&
            !(
              self.$route.meta &&
              self.$route.meta.layout &&
              self.$route.meta.layout === 'mobile'
            )
          ) {
            self.$nextTick(() => {
              if (self.$refs['newChart']) {
                self.$refs['newChart'].render(reportObj)
              }
            })
          }
          self.reportObj = reportObj
          self.$emit('reportLoaded', reportObj, result)
          self.loading = false
        })
        .catch(function(error) {
          console.log('error ', error)
          if (error) {
            self.loading = false
            self.failed = true
          }
        })
    },
    onSettingsChange(changedOption) {
      if (changedOption === 'alarm') {
        if (
          (!this.reportObj.alarms || !this.reportObj.alarms.regions.length) &&
          this.reportObj.options.settings.alarm
        ) {
          this.init()
        }
      } else if (changedOption === 'safelimit') {
        if (
          !this.reportObj.options.safeLimit.length &&
          this.reportObj.options.settings.safelimit
        ) {
          this.init()
        }
      } else if (changedOption === 'filter') {
        if (this.reportObj.options.settings.hasOwnProperty('filterBar')) {
          this.init()
        }
      }
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
    showRSquareValue() {
      let trendPoints = this.$helpers.getDataPoints(
        this.reportObj.options.dataPoints,
        [3],
        false
      )
      let trendLineProp = this.resultObj.reportData.trendLineProp
      if (this.reportObj.options.trendLine.showr2) {
        trendPoints.filter(function(point) {
          point.label =
            'R^2 = ' + trendLineProp[point.alias].rSquare + ';' + point.label
        })
      } else {
        trendPoints.filter(function(point) {
          point.label = point.label.split('; ').splice(1)[0]
        })
      }
      self.$refs['newChart'].render(this.reportObj)
    },
    openReportFilterDialog() {
      this.showReportFilter = true
    },
    openChartOptions() {
      this.showScatterChartOptions = true
    },
    setReportFilter(reportFilter) {
      this.hasReportFilter =
        Object.keys(reportFilter.timeFilter.conditions).length > 0
      this.hasReportFilter =
        Object.keys(reportFilter.dataFilter.conditions).length > 0
          ? true
          : this.hasReportFilter
      if (this.reportObj.options.settings.hasOwnProperty('filterBar')) {
        this.reportObj.options.settings.filterBar =
          this.hasReportFilter && !this.config.scatterConfig
      }
      this.$set(this.config, 'reportFilter', reportFilter)
    },
    prepareTrendReportObj(result, params) {
      let self = this
      let Config = JSON.parse(JSON.stringify(this.config))
      let reportObj = self.prepareReport(result)
      if (
        Config.analyticsType === 2 &&
        Config.mode === 1 &&
        Config.period === 0
      ) {
        self.hasReportFilter =
          Object.keys(self.config.reportFilter.timeFilter.conditions).length > 0
        self.hasReportFilter =
          Object.keys(self.config.reportFilter.dataFilter.conditions).length > 0
            ? true
            : self.hasReportFilter
      }
      if (self.isHeatMap || self.isTreeMap) {
        reportObj.options.heatMapOptions = self.heatMapOptions
      }
      if (Config.template) {
        let currentTemplate = JSON.parse(JSON.stringify(Config.template))
        let filters = self.getFiltersFromCriteria(currentTemplate)
        self.loadReportTemplateValues(currentTemplate, filters).then(() => {
          currentTemplate.parentId =
            currentTemplate.parentId === null
              ? currentTemplate.defaultValue
              : currentTemplate.parentId
          self.$set(
            self.trendresultObj.report,
            'currentTemplate',
            currentTemplate
          )
          self.showTemplateFilter = true
        })
      }
      if (
        Config.isFromAlarmSummary ||
        (self.$route.query &&
          self.$route.query.isFromAlarmSummary &&
          !self.trendresultObj)
      ) {
        if (reportObj.booleanData) {
          reportObj.options.dataPoints = self.groupNonBooleanPoints(
            reportObj.options.dataPoints
          )
        }
      }
      if ((self.$route.query && self.$route.query.alarmId) || Config.alarmId) {
        reportObj.options.settings.alarm = true
      }
      reportObj.params = params

      reportObj.alarms = self.prepareBooleanReport(result.reportData.aggr)
      if (Config.alarmId || self.alarmId) {
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
      self.trendreportObj = reportObj
    },
  },
}
</script>

<style>
.customize-chart-icon {
  float: right;
  margin-right: 60px;
  padding: 5px;
  font-size: 15px;
  opacity: 0.7;
  cursor: pointer;
  margin-top: -35px;
  display: none;
}

.fchart-section:hover .customize-chart-icon {
  display: block;
}

.customize-chart-icon:hover {
  opacity: 1;
}

.dp-axis-selector ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.dp-axis-selector ul li {
  padding: 8px 20px;
  vertical-align: middle;
  cursor: pointer;
}

.dp-axis-selector ul li {
  padding: 10px 20px;
}

.dp-axis-selector .dp-axis-label {
  top: 0;
}

.newanalytics .fc-new-chart.bb {
  background: #fff;
}
.empty-multi-chart {
  height: 80vh;
  text-align: center;
  align-items: center;
  justify-content: center;
  margin: auto;
  width: 50vh;
  margin-top: 30px;
  white-space: nowrap;
}

img.predictive-arrow {
  position: relative;
  right: 13px;
}
.predictive-header-section {
  position: relative;
  right: 30px;
  float: right;
}
.predictive-header-section.active .predictive-arrow {
  transform: rotate(90deg);
}
.regression-table-fit {
  float: right;
  width: 97%;
}
.coltrue {
  fill: #ecf2f7;
}
.filter-indicator {
  border: 1px solid #39b2c2 !important;
  width: 5px;
  height: 5px;
  border-radius: 50%;
  position: relative;
  background-color: #39b2c2 !important;
  left: 10px;
  box-shadow: 0px 0px 5px 5px #39b2c0 !important;
}
.button-icon {
  border: 1px solid #eceded;
  border-radius: 4px;
  background: #fff;
  height: 20px;
  width: 28px;
}
</style>
