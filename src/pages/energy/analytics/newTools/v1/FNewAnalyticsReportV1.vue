<template>
  <div class="analytics-section mT20 height100">
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
      <div
        v-if="
          this.reportObj &&
            this.reportObj.options &&
            this.reportObj.options.settings.timeperiod === true &&
            [1, 4].includes(config.mode)
        "
        class="chart-icon pointer flRight pB8 el-popover__reference"
        style="margin-right: 120px;"
      >
        <el-select
          class="period-select width20 fc-input-full-border2 "
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

      <div
        v-if="
          showTemplateFilter &&
            resultObj &&
            resultObj.report &&
            resultObj.report.currentTemplate &&
            resultObj.report.currentTemplate.show
        "
      >
        <el-select
          class="fc-input-full-border-h35 fc-report-label-position"
          @change="changeParentForTemplate"
          v-model="resultObj.report.currentTemplate.parentId"
          filterable
        >
          <el-option
            v-for="(asset, assetIdx) in resultObj.report.currentTemplate
              .chooseValues"
            :key="assetIdx"
            :value="asset.id"
            :label="asset.name"
          ></el-option>
        </el-select>
      </div>

      <f-chart-settings
        v-if="!config.hidechartoptions && reportObj && !isRegression === true"
        :dataPoints="reportObj.options.dataPoints"
        :settings="reportObj.options.settings"
        :showChart="false"
        :showTable="false"
        :showChartMode="showChartMode"
        :showTimePeriod="showTimePeriod"
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
        >{{ showTimePeriod }}</f-chart-settings
      >
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
    <div v-else class="height100">
      <div v-if="!config.hidechart" class="height100">
        <f-mobile-multi-chart
          ref="multiChart"
          v-if="
            $route.meta &&
              $route.meta.layout &&
              $route.meta.layout === 'mobile' &&
              reportObj &&
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
              reportObj &&
              reportObj.options.settings.chartMode === 'multi'
          "
          class="empty-multi-chart"
        >
          Currently this chart not supported in the mobile
        </div>
        <f-multi-chart
          v-else-if="
            reportObj && reportObj.options.settings.chartMode === 'multi'
          "
          ref="multiChart"
          :data="reportObj.data"
          :options="reportObj.options"
          :alarms="reportObj.alarms"
          :booleanData="reportObj.booleanData"
          :dateRange="reportObj.dateRange"
          :resultObj="resultObj"
          :hidecharttypechanger="config.hidecharttypechanger || isRegression"
        >
        </f-multi-chart>
        <div v-else class="height100">
          <f-mobile-chart
            v-if="
              $route.meta &&
                $route.meta.layout &&
                $route.meta.layout === 'mobile' &&
                !isHeatMap
            "
            ref="newChart"
            :data="reportObj.data"
            :options="reportObj.options"
            :dateRange="reportObj.dateRange"
            :resultObj="resultObj"
            class="mobile-chart"
          ></f-mobile-chart>
          <f-new-chart
            v-else-if="!isHeatMap"
            ref="newChart"
            :data="reportObj.data"
            :options="reportObj.options"
            :alarms="reportObj.alarms"
            :dateRange="reportObj.dateRange"
            :resultObj="resultObj"
            :hidecharttypechanger="config.hidecharttypechanger || isRegression"
            :fixedChartHeight="config.fixedChartHeight"
            :config="config"
            :showWidgetLegends="config ? config.showWidgetLegends : true"
            :chartTypeTarget="chartTypeTarget"
          >
            <template slot="relatedAlarmBar">
              <slot name="relatedAlarmBarLayout"></slot
            ></template>
          </f-new-chart>
          <FNewHeatMap
            v-if="isHeatMap"
            :config="config"
            :resultObject="resultObj"
            :reportObject="reportObj"
          ></FNewHeatMap>
        </div>
      </div>
      <f-tabular-report
        ref="newTable"
        :reportObject="resultObj"
        :reportConfig="reportObj"
        v-if="!config.hidetabular"
        class="new-analytics-table mL30 mR30"
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
import FNewChart from 'newcharts/components/FNewchartV1'
import FMobileChart from 'newcharts/components/FMobileChart'
import FMultiChart from 'newcharts/components/FMultiChart'
import NewDataFormatHelper from 'pages/report/mixins/NewDataFormatHelper'
import AnalyticsMixin from 'pages/energy/analytics/mixins/AnalyticsMixin'
import FTabularReport from 'pages/report/components/FTabularReportNew'
import FChartSettings from 'newcharts/components/FNewChartSettings'
import FMobileMultiChart from 'newcharts/components/FMobileMultiChart'
import PredictionTimingsBar from 'pages/energy/analytics/components/PredictionTimingsBar'
import NewDateHelper from '@/mixins/NewDateHelper'
import RangeChartDialog from 'src/pages/energy/analytics/components/RangeChartDialog'
import NewReportSummaryHelper from 'src/pages/report/mixins/NewReportSummaryHelper'
import FNewHeatMap from 'src/pages/energy/analytics/components/FNewHeatMap'
import deepmerge from 'util/deepmerge'
// import moment from 'moment-timezone'
export default {
  props: [
    'config',
    'baseLines',
    'readings',
    'showChartMode',
    'showTimePeriod',
    'chartTypeTarget',
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
    FChartSettings,
    FMobileMultiChart,
    PredictionTimingsBar,
    RangeChartDialog,
    FNewHeatMap,
  },
  data() {
    return {
      loading: true,
      predictive: true,
      failed: false,
      reportObj: null,
      resultObj: null,
      showRangeChartDialog: false,
      showTemplateFilter: false,
    }
  },
  mounted() {
    this.init()
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
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

      // avail.push({
      //   name:'Week of year', // W1, W2
      //   value: 16,
      //   enable: true
      // })

      // avail.push({
      //   name:'Month of year', // Jan, Feb , march
      //   value: 15,
      //   enable: true
      // })

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
  watch: {
    config: {
      handler: function(newData, oldData) {
        if (!this.config.applyReportDate || !this.config.period) {
          this.init(false)
        }
      },
      deep: true,
    },
    getAvailablePeriods: {
      handler(newData, oldData) {
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
      },
      immediate: true,
    },
  },
  methods: {
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
        for (let dp of this.config.dataPoints) {
          parentId.push(dp.parentId)
          fieldId = dp.yAxis.fieldId
          yAggr = dp.yAxis.aggr
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
          if (this.config.template) {
            fields.push({
              parentId: dp.parentId
                ? Array.isArray(dp.parentId)
                  ? dp.parentId
                  : [dp.parentId]
                : null,
              buildingId: dp.buildingId ? dp.buildingId : -1,
              metaData: metaData,
              yAxis: { fieldId: dp.yAxis.fieldId, aggr: dp.yAxis.aggr },
              aliases: dp.aliases,
              type: dp.type > 0 ? dp.type : 1,
              predictedTime: dp.predictedTime,
              rule_aggr_type: dp?.rule_aggr_type,
            })
          } else {
            fields.push({
              parentId: dp.parentId
                ? Array.isArray(dp.parentId)
                  ? dp.parentId
                  : [dp.parentId]
                : null,
              name: dp.name,
              buildingId: dp.buildingId ? dp.buildingId : -1,
              metaData: metaData,
              yAxis: dp.yAxis,
              aliases: dp.aliases,
              type: dp.type > 0 ? dp.type : 1,
              predictedTime: dp.predictedTime,
              moduleName: dp.reportModuleName,
            })
          }
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
      if (this.config.template) {
        if (this.config.template.parentId !== null) {
          fields.forEach(d => {
            d.parentId = [this.config.template.parentId]
          })
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
      if (window.fetchMarkedAlso) {
        params.shouldIncludeMarked = true
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
      self.loading = true
      self.failed = false

      let apiURL = this.config.api
        ? this.config.api
        : '/v2/report/readingReport'
      let params = null
      if (this.config.alarmId) {
        apiURL = '/v2/report/fetchReadingsFromAlarm'
        params = {
          alarmId: this.config.alarmId,
          mode: 1,
          xAggr: this.config.period || 0,
          startTime: this.config.dateFilter.value[0],
          endTime: this.config.dateFilter.value[1],
          showAlarms: true,
        }
      }
      if (this.config.readingRuleId) {
        params.readingRuleId = this.config.readingRuleId
      }
      if (this.config.isWithPrerequsite) {
        params.isWithPrerequsite = this.config.isWithPrerequsite
      }
      if (!params) {
        params = self.getReportParams()
      }

      if (this.config.template && this.config.template.show) {
        apiURL =
          apiURL +
          '?templateString=' +
          encodeURIComponent(JSON.stringify(this.config.template))
      }
      if (this.config.analyticsType) {
        params.analyticsType = this.config.analyticsType
      }
      params.newFormat = true

      self.$http
        .post(apiURL, params)
        .then(function(response) {
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
          if (self.reportObj && !self.config.alarmId) {
            result.report.chartState = self.reportObj.options
            result.report.tabularState = self.reportObj.params.tabularState
          } else if (self.config.savedReport) {
            result.report.chartState = self.config.savedReport.chartState
            result.report.tabularState = self.config.savedReport.tabularState
          }

          self.reportObj = self.prepareReport(result)
          if (self.config.customizeC3 && self.reportObj.options) {
            self.reportObj.options['customizeC3'] = self.config.customizeC3
          }
          if (self.config.customizeChartOptions && self.reportObj.options) {
            self.reportObj.options = deepmerge.objectAssignDeep(
              self.reportObj.options,
              self.config.customizeChartOptions
            )
          }
          if (self.config.template) {
            let currentTemplate = JSON.parse(
              JSON.stringify(self.config.template)
            )
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
            self.config.isFromAlarmSummary ||
            (self.$route.query &&
              self.$route.query.isFromAlarmSummary &&
              !self.resultObj)
          ) {
            if (self.reportObj.booleanData) {
              self.reportObj.options.dataPoints = self.groupNonBooleanPoints(
                self.reportObj.options.dataPoints
              )
            }
          }
          if (
            (self.$route.query && self.$route.query.alarmId) ||
            self.config.alarmId
          ) {
            self.reportObj.options.settings.alarm = true
          }

          if (
            self.config.alarmId &&
            typeof self.config.showAlarmBar != 'undefined'
          ) {
            self.reportObj.options.settings.alarm = self.config.showAlarmBar
          }
          self.resultObj = result
          self.reportObj.params = params

          self.reportObj.tableConfig = self.config.tableConfig
          self.reportObj.alarms = self.prepareBooleanReport(
            result.reportData.aggr
          )
          if (self.config.alarmId) {
            self.reportObj.alarms.barSize = 'medium'
          }
          if (self.config.regionConfig) {
            self.reportObj.options.regionConfig = self.config.regionConfig
          }
          if (self.config.hideDataPoints) {
            self.reportObj.options.hideDataPoints = self.config.hideDataPoints
          }
          if (self.config.hasOwnProperty('disbaleNiceTickMarks')) {
            self.reportObj.options.disbaleNiceTickMarks =
              self.config.disbaleNiceTickMarks
          }
          if (self.config.chartType) {
            self.reportObj.options.chartType = self.config.chartType
          }
          if (self.config.diffChartConfig) {
            self.reportObj.options.diffChartConfig = self.config.diffChartConfig
          }
          if (self.config.hideLegends && self.reportObj.options.legend) {
            self.reportObj.options.legend.show = false
          }
          if (self.config.paddingOptions) {
            self.reportObj.options.padding = self.config.paddingOptions
          }
          if (self.config.intersections) {
            self.reportObj.options.intersections = self.config.intersections
          }
          if (self.config.point) {
            if (self.config.pointShowRule) {
              self.reportObj.options.point = self.pointShowRule(
                self.reportObj.dateRange,
                self.config.pointShowRule
              )
            } else {
              self.reportObj.options.point = self.config.point
            }
          }
          if (self.config.zoom) {
            self.reportObj.options.zoom = self.config.zoom
          }
          if (
            self.config.size &&
            (self.config.size.width || self.config.size.height)
          ) {
            self.reportObj.options.size = self.config.size
          }
          if (self.config.axes) {
            self.reportObj.options.axes = self.config.axes
          }
          self.reportObj.options.xFormat = 'MM-DD-YYYY'
          if (self.config.xFormat) {
            self.reportObj.options.xFormat = self.config.xFormat
          }
          if (
            self.config.colors &&
            self.reportObj.options.dataPoints &&
            self.reportObj.options.dataPoints.length
          ) {
            self.reportObj.options.dataPoints.forEach(rt => {
              rt.color = self.config.colors[rt.key]
            })
          }
          if (self.config.axis) {
            self.reportObj.options.fixedAxis = self.config.axis
          }
          self.reportObj.options.predictionTimings =
            self.config.predictionTimings
          self.$emit('reportLoaded', self.reportObj, self.resultObj)
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
