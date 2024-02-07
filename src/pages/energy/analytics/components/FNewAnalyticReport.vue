<template>
  <div class="analytics-section">
    <div class="new-analytics-filter-section">
      <new-date-picker
        ref="newDatePicker"
        v-if="!config.hidedatepicker"
        :zone="$timezone"
        class="filter-field date-filter-comp inline"
        :dateObj="config.dateFilter"
        @date="setDateFilter"
        :isDateFixed="config.applyReportDate"
      ></new-date-picker>
      <f-chart-settings
        v-if="!config.hidechartoptions && reportObj && !isRegression === true"
        :dataPoints="reportObj.options.dataPoints"
        :settings="reportObj.options.settings"
        :showChart="false"
        :showTable="false"
        :showChartMode="
          reportObj.data && Object.keys(reportObj.data).length > 2
        "
        :showAlarm="
          reportObj.options &&
          reportObj.options.common &&
          reportObj.options.common.type === 1
            ? true
            : false
        "
        :showSafelimit="
          reportObj.options &&
          reportObj.options.common &&
          reportObj.options.common.type === 1 &&
          typeof resultObj.regression === 'undefined'
            ? true
            : false
        "
        :disableChartMode="reportObj.booleanData"
        @onSettingsChange="onSettingsChange"
      ></f-chart-settings>
      <span
        v-if="
          resultObj &&
            resultObj.report &&
            resultObj.report.dataPoints.length !== 0 &&
            !isRegression
        "
      >
      </span>
    </div>
    <prediction-timings-bar
      :reportObj="reportObj"
      :config="config"
      class="prediction-timing-bar"
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
      style="
        margin-top: 30px;
        opacity: 0.7;
        font-size: 13px;
        padding: 50px;
        line-height: 25px;
        text-align: center;
      "
    >
      <div>{{ $t('home.dashboard.data_loading_failed') }}</div>
    </div>
    <div
      v-else-if="!reportObj || !reportObj.data || noData"
      class="analytics-report-empty-state"
      style="
        margin-top: 30px;
        font-size: 13px;
        padding: 50px;
        line-height: 25px;
        text-align: center;
      "
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
        <f-multi-chart
          v-else-if="reportObj.options.settings.chartMode === 'multi'"
          ref="multiChart"
          :data="reportObj.data"
          :options="reportObj.options"
          :alarms="reportObj.alarms"
          :booleanData="reportObj.booleanData"
          :dateRange="reportObj.dateRange"
          :resultObj="resultObj"
          :relatedAlarms="reportObj.relatedAlarms"
          :hidecharttypechanger="config.hidecharttypechanger || isRegression"
        ></f-multi-chart>
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
            :alarms="reportObj.alarms"
            :dateRange="reportObj.dateRange"
            :resultObj="resultObj"
            class="mobile-chart"
          ></f-mobile-chart>
          <f-new-chart
            v-else
            ref="newChart"
            :data="reportObj.data"
            :options="reportObj.options"
            :alarms="reportObj.alarms"
            :dateRange="reportObj.dateRange"
            :resultObj="resultObj"
            :relatedAlarms="reportObj.relatedAlarms"
            :hidecharttypechanger="config.hidecharttypechanger || isRegression"
            :showWidgetLegends="config.showWidgetLegends"
            :fixedChartHeight="config.fixedChartHeight"
            :config="config"
            :moduleName="moduleName"
          ></f-new-chart>
        </div>
      </div>
      <f-tabular-report
        ref="newTable"
        :reportObject="resultObj"
        :reportConfig="reportObj"
        v-if="!config.hidetabular"
        class="new-analytics-table"
        :class="{ 'regression-table-fit': isRegression }"
      ></f-tabular-report>
    </div>
    <RangeChartDialog
      v-if="resultObj"
      :visibility.sync="showRangeChartDialog"
      :dataPoints="resultObj.report.dataPoints"
      @rangePoints="addRangePoints"
    ></RangeChartDialog>
  </div>
</template>

<script>
import NewDatePicker from '@/NewDatePicker'
import FNewChart from 'newcharts/components/FNewChart'
import FMobileChart from 'newcharts/components/FMobileChart'
import FMultiChart from 'newcharts/components/FMultiChart'
import NewDataFormatHelper from 'pages/report/mixins/NewDataFormatHelper'
import AnalyticsMixin from 'pages/energy/analytics/mixins/AnalyticsMixin'
import FTabularReport from 'pages/report/components/FTabularReportNew'
import FChartSettings from 'newcharts/components/FChartSettings'
import FMobileMultiChart from 'newcharts/components/FMobileMultiChart'
import PredictionTimingsBar from 'pages/energy/analytics/components/PredictionTimingsBar'
import NewDateHelper from '@/mixins/NewDateHelper'
import RangeChartDialog from 'src/pages/energy/analytics/components/RangeChartDialog'
import deepmerge from 'util/deepmerge'
import { isEmpty } from '@facilio/utils/validation'
// import moment from 'moment-timezone'
export default {
  props: ['config', 'baseLines', 'readings', 'moduleName'],
  mixins: [NewDataFormatHelper, AnalyticsMixin, NewDateHelper],
  components: {
    NewDatePicker,
    FNewChart,
    FMultiChart,
    FTabularReport,
    FMobileChart,
    FChartSettings,
    FMobileMultiChart,
    PredictionTimingsBar,
    RangeChartDialog,
  },
  data() {
    return {
      loading: true,
      predictive: true,
      failed: false,
      noData: false,
      reportObj: null,
      resultObj: null,
      showRangeChartDialog: false,
    }
  },
  mounted() {
    this.init()
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
  },
  computed: {
    isRegression() {
      if (this.$route.path.includes('regression')) {
        return true
      } else {
        return false
      }
    },
  },
  watch: {
    config: {
      handler: function(newData, oldData) {
        if (!this.config.applyReportDate) {
          this.init(false)
        }
      },
      deep: true,
    },
  },
  methods: {
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
      this.$set(this.config, 'dateFilter', dateFilter)
      // this.$emit('update:config', this.config)
    },
    init() {
      this.initChart()
    },
    resize() {
      if (this.$refs['newChart']) {
        this.$refs['newChart'].resize()
      }
      if (this.$refs['multiChart']) {
        this.$refs['multiChart'].resize()
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
        let dynamicKpi = -1
        for (let dp of this.config.dataPoints) {
          parentId.push(dp.parentId)
          fieldId = dp.yAxis.fieldId
          yAggr = dp.yAxis.aggr
          let { kpiType = '' } = dp
          if (kpiType === 'DYNAMIC') {
            dynamicKpi = dp.yAxis.dynamicKpi
          }
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
        if (!isEmpty(dynamicKpi)) {
          let field = fields[0] || {}
          fields.splice(0, 1, {
            ...field,
            yAxis: {
              ...field.yAxis,
              dynamicKpi: dynamicKpi,
            },
            kpiType: 'DYNAMIC',
          })
        }
        this.setAlias(fields, baselineNames)
      } else {
        this.setAlias(this.config.dataPoints, baselineNames)

        for (let dp of this.config.dataPoints) {
          let metaData = {}
          if (dp.categoryId) {
            metaData.categoryId = dp.categoryId
          }

          fields.push({
            parentId: dp.parentId
              ? Array.isArray(dp.parentId)
                ? dp.parentId
                : [dp.parentId]
              : null,
            name: dp.name,
            metaData: metaData,
            yAxis: dp.yAxis,
            aliases: dp.aliases,
            type: dp.type > 0 ? dp.type : 1,
            predictedTime: dp.predictedTime,
            rule_aggr_type: dp?.rule_aggr_type,
            kpiType: dp.kpiType,
          })
        }
      }

      if (
        this.config.sorting &&
        (this.config.mode === 6 ||
          this.config.mode === 7 ||
          this.config.mode === 8)
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

          let field = fields.find(f => f.yAxis.fieldId === sortByField)
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
      let params = {
        mode: this.config.mode,
        startTime: this.config.dateFilter.value[0],
        endTime: this.config.dateFilter.value[1],
        xAggr:
          this.config.mode === 1 ||
          this.config.mode === 3 ||
          this.config.mode === 4 ||
          this.config.mode === 5
            ? this.config.period
            : 0,
        fields: JSON.stringify(fields),
        transformWorkflow: this.config.transformWorkflow,
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
      if (this.config.filters) {
        if (this.config.filters.xCriteriaMode) {
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
                if (categoryId.indexOf(dp.categoryId) === -1) {
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
      let { moduleName } = self
      this.loading = true
      this.failed = false
      this.noData = false

      let apiURL = this.config.api
        ? this.config.api
        : '/v2/report/readingReport'
      let params = null
      if (this.config.alarmId && this.$mobile) {
        apiURL = '/v2/report/fetchReadingsFromAlarm'
        params = {
          alarmId: this.config.alarmId,
          mode: 1,
          xAggr:
            this.$constants.OPERATOR_TO_XAGG[
              this.config.dateFilter.operatorId
            ] || 0,
          startTime: this.config.dateFilter.value[0],
          endTime: this.config.dateFilter.value[1],
          showAlarms: true,
        }
      } else if (this.config.alarmId) {
        apiURL = '/v2/report/fetchReadingsFromAlarm'
        params = {
          alarmId: this.config.alarmId,
          mode: 1,
          xAggr: 0,
          startTime: this.config.dateFilter.value[0],
          endTime: this.config.dateFilter.value[1],
          showAlarms: true,
        }
      }
      if (this.config.readingRuleId) {
        if (params) {
          params.readingRuleId = this.config.readingRuleId
        } else {
          params = {}
          params.readingRuleId = this.config.readingRuleId
        }
      }
      if (this.config.isFromSiteSummary) {
        if (this.config.params) {
          params = this.config.params
        }
      }
      if (this.config.isFromFaultInsight) {
        if (!isEmpty(this.config.params)) {
          let { config } = this
          let { dateFilter, params: configParams } = config || {}
          let { value } = dateFilter || {}
          let startTime = value[0]
          let endTime = value[1]
          params = { ...configParams, startTime, endTime }
        }
      }
      if (this.config.isWithPrerequsite) {
        params.isWithPrerequsite = this.config.isWithPrerequsite
      }
      if (!params) {
        params = self.getReportParams()
      }
      if (this.config.analyticsType) {
        params.analyticsType = this.config.analyticsType
      }
      if (this.moduleName === 'sensorrollupalarm') {
        params.shouldIncludeMarked = true
      }
      params.newFormat = true

      self.$http
        .post(apiURL, params)
        .then(function(response) {
          if (response.data && response.data.responseCode === 0) {
            let result = response.data.result
            result.xAggr = params.xAggr
            result.mode = params.mode
            if (self.config.analyticsType) {
              result.report.analyticsType = self.config.analyticsType
            }
            if (self.config.filters) {
              result.filters = self.config.filters
            }
            if (self.config.sorting) {
              result.sorting = self.config.sorting
            }
            if (self.config.applyReportDate) {
              self.config.dateFilter = result.dateRange = NewDateHelper.getDatePickerObject(
                self.config.dateFilter.operatorId,
                result.report.dateValue
              )
              self.$nextTick(() => {
                self.$refs.newDatePicker.loadFromObject()
              })
            } else {
              result.dateRange = self.config.dateFilter
            }
            if (self.reportObj) {
              result.report.chartState = self.reportObj.options
              result.report.tabularState = self.reportObj.params.tabularState
            } else if (self.config.savedReport) {
              result.report.chartState = self.config.savedReport.chartState
              result.report.tabularState = self.config.savedReport.tabularState
            }
            if (self.config.groupByMetrics) {
              result.groupByMetrics = true
            }

            self.reportObj = self.prepareReport(result)
            if (
              self.config.customizeChartOptions &&
              self.reportObj &&
              self.reportObj.options
            ) {
              self.reportObj.options = deepmerge.objectAssignDeep(
                self.reportObj.options,
                self.config.customizeChartOptions
              )
            }
            if (
              (self.$route.query && self.$route.query.alarmId) ||
              self.config.alarmId
            ) {
              self.reportObj.options.settings.alarm = true
            }
            self.resultObj = result
            self.reportObj.params = params

            self.reportObj.alarms = self.prepareBooleanReport(
              result.reportData.aggr
            )
            if (!isEmpty(self.reportObj.alarms)) {
              if (!isEmpty(moduleName)) {
                let displayName =
                  moduleName === 'newreadingalarm' ? 'Faults' : null
                self.$set(self.reportObj.alarms, 'barTitle', displayName)
              }
            }
            if (self.config.hideAlarmSubject) {
              self.reportObj.alarms.showTimeOnly = true
            }
            if (
              result.reportData.aggr &&
              result.reportData.aggr.relatedAlarms
            ) {
              self.reportObj.relatedAlarms = self.prepareRelatedAlarms(
                result.reportData.aggr.relatedAlarms
              )
            }
            if (self.config.alarmId) {
              self.reportObj.alarms.barSize = 'medium'
            }
            self.reportObj.options.predictionTimings =
              self.config.predictionTimings
            if (self.config.regionConfig) {
              self.reportObj.options.regionConfig = self.config.regionConfig
            }
            self.$emit('reportLoaded', self.reportObj, self.resultObj)
            self.loading = false
          } else if (
            response.data.message ===
            'In sufficient params for Reading Analysis'
          ) {
            self.loading = false
            self.noData = true
          } else {
            self.loading = false
            self.failed = true
          }
        })
        .catch(function(error) {
          console.log('error ', error)
          if (error === 'In sufficient params for Reading Analysis') {
            self.loading = false
            self.noData = true
          } else if (error) {
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
      }
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
  /* padding-bottom: 30px; */
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
.prediction-timing-bar {
  /* display: block; */
  /* overflow: auto;
  width: 100%; */
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
</style>
<style scoped>
.new-analytics-filter-section {
  text-align: center;
}
</style>
