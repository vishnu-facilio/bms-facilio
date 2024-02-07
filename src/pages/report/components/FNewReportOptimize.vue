<template>
  <div
    class="height100 mobile-report"
    :class="{
      scrollable:
        config &&
        config.widget &&
        reportObj &&
        reportObj.options.settings.chart === false,
    }"
  >
    <div class="datefilter-hide-overlay"></div>
    <div style="text-align: center;">
      <new-date-picker
        v-if="dateFilter"
        :zone="$timezone"
        class="filter-field date-filter-comp-new-report inline"
        :dateObj="dateFilter"
        @date="setDateFilter"
      ></new-date-picker>
      <div
        v-if="
          reportObj &&
            reportObj.options.settings.timeperiod === true &&
            showTimePeriod
        "
        class="chart-icon pointer pB8 el-popover__reference"
        style="margin-left: 10px; margin-top: 5px; position: absolute;"
      >
        <el-select
          class="period-select width100 fwidget-report-period-select"
          size="mini"
          v-model="period"
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
      <f-chart-settings
        :showTimePeriod="showTimePeriod"
        :showSafelimit="showSafeLimit"
        :settings="reportObj.options.settings"
        v-if="!(config && config.widget) && !isRegression"
        @onSettingsChange="onSettingsChange"
      ></f-chart-settings>
    </div>
    <f-user-filters
      v-if="
        reportObj &&
          reportObj.options.common.filters &&
          reportObj.options.common.filters.filterState &&
          reportObj.options.common.filters.filterState.liveFilterField &&
          reportObj.options.common.filters.filterState.liveFilterField !==
            'none' &&
          !isRegression
      "
      :report="reportObj"
      @applyFilter="applyFilter"
    ></f-user-filters>
    <spinner v-if="loading" :show="loading" size="80"></spinner>
    <div
      v-else-if="failed"
      style="margin-top: 30px;opacity: 0.7;font-size: 13px;padding: 50px;line-height: 25px;text-align: center;"
    >
      <div>{{ $t('home.dashboard.data_loading_failed') }}</div>
    </div>
    <div
      v-else-if="!reportObj.data"
      style="font-size: 13px;padding: 80px 50px 50px 50px;line-height: 25px;text-align: center;"
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
    <div class="height100" v-else>
      <template v-if="!isRegression">
        <div
          :class="{ height100: config }"
          v-if="reportObj.options.settings.chartMode === 'multi'"
        >
          <f-multi-chart
            v-if="!$mobile"
            ref="multiChart"
            :isWidget="config ? true : false"
            :width="config && config.width ? config.width : null"
            :resultObj="resultObj"
            :height="config && config.height ? config.height : null"
            :data="reportObj.data"
            :options="reportObj.options"
            :alarms="reportObj.alarms"
            :dateRange="reportObj.dateRange"
            :hidecharttypechanger="(config && config.widget) || isRegression"
            :booleanData="reportObj.booleanData"
          ></f-multi-chart>
          <!-- <div v-else class="empty-multi-chart">Currently this chart not supported in the mobile</div> -->
          <f-mobile-multi-chart
            v-else
            ref="multiChart"
            :isWidget="config ? true : false"
            :width="config && config.width ? config.width : null"
            :resultObj="resultObj"
            :height="config && config.height ? config.height : null"
            :data="reportObj.data"
            :options="reportObj.options"
            :alarms="reportObj.alarms"
            :dateRange="reportObj.dateRange"
            :hidecharttypechanger="(config && config.widget) || isRegression"
            :booleanData="reportObj.booleanData"
          ></f-mobile-multi-chart>
        </div>
        <div
          :class="{
            height100: config && reportObj.options.settings.chart !== false,
          }"
          v-else
        >
          <f-mobile-chart
            v-if="
              $route.meta &&
                $route.meta.dashboardlayout &&
                $route.meta.dashboardlayout === 'mobile'
            "
            ref="newChart"
            :width="config && config.width ? config.width : null"
            :resultObj="resultObj"
            :height="config && config.height ? config.height : null"
            :data="reportObj.data"
            :options="reportObj.options"
            :dateRange="reportObj.dateRange"
          ></f-mobile-chart>
          <!-- <f-new-chart v-else ref="newChart" :isWidget="config ? true : false" :width="config && config.width ? config.width : null" :resultObj="resultObj" :height="config && config.height ? config.height : null" :data="reportObj.data" :options="reportObj.options" :alarms="reportObj.alarms" :dateRange="reportObj.dateRange" :hidecharttypechanger="config && config.widget ||  isRegression"></f-new-chart> -->
          <f-new-chart-optimize
            v-else
            ref="newChart"
            :isWidget="config ? true : false"
            :width="config && config.width ? config.width : null"
            :resultObj="resultObj"
            :height="config && config.height ? config.height : null"
            :reportObj="reportObj"
            :options="reportObj.options"
            :hidecharttypechanger="(config && config.widget) || isRegression"
            @chartgenerate="chartgenerate"
            :config="config"
          ></f-new-chart-optimize>
        </div>
        <f-tabular-report
          v-if="chartgenerated === true"
          ref="newTable"
          class="newTable"
          :reportObject="resultObj"
          :reportConfig="reportObj"
          :widget="config ? config.widget : null"
        ></f-tabular-report>
      </template>
      <div class="mT10" v-if="isRegression">
        <MultipleRegressionAnalysis
          :resultObj="resultObj"
        ></MultipleRegressionAnalysis>
      </div>
    </div>
  </div>
</template>
<script>
import NewDatePicker from '@/NewDatePicker'
import FNewChartOptimize from 'newcharts/components/FNewChartOptimize'
import FMobileChart from 'newcharts/components/FMobileChart'
import FMobileMultiChart from 'newcharts/components/FMobileMultiChart'
import FMultiChart from 'newcharts/components/FMultiChart'
import NewDataFormatHelper from 'pages/report/mixins/NewDataFormatHelper'
import FTabularReport from 'pages/report/components/FTabularReportNew'
import NewDateHelper from '@/mixins/NewDateHelper'
import FChartSettings from 'newcharts/components/FChartSettings'
import FUserFilters from './FUserFilters'
import MultipleRegressionAnalysis from 'src/pages/energy/analytics/newTools/v1/MultipleRegressionAnalysis'
import { API } from '@facilio/api'
export default {
  props: ['id', 'config', 'tabular', 'showTimePeriod'],
  mixins: [NewDataFormatHelper],
  components: {
    NewDatePicker,
    FNewChartOptimize,
    FMultiChart,
    FTabularReport,
    FMobileChart,
    FChartSettings,
    FUserFilters,
    FMobileMultiChart,
    MultipleRegressionAnalysis,
  },
  data() {
    return {
      loading: true,
      failed: false,
      reportObj: null,
      resultObj: null,
      showReportOptions: false,
      dateFilter: null,
      userFilterApplied: null,
      chartgenerated: false,
      period: null,
    }
  },
  mounted() {
    this.init()
  },
  computed: {
    getAvailablePeriods() {
      if (this.reportObj && this.reportObj.dateRange) {
        let operationOnId = this.reportObj.dateRange.operatorId
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
      }
      return []
    },
    isRegression() {
      if (
        this.resultObj &&
        this.resultObj.report &&
        this.resultObj.report.reportState &&
        this.resultObj.report.reportState.regressionConfig &&
        this.resultObj.report.reportState.regressionConfig.length != 0
      ) {
        return true
      }
      return false
    },
    showSafeLimit() {
      if (
        this.resultObj &&
        this.resultObj.report &&
        this.resultObj.report.type === 3
      ) {
        return false
      }
      return true
    },
    reportId() {
      if (this.id) {
        return this.id
      } else {
        return this.config ? this.config.widget.dataOptions.newReportId : null
      }
    },
    appliedDateRange() {
      if (this.$route.query.daterange) {
        return JSON.parse(this.$route.query.daterange)
      }
    },
    appliedChartType() {
      // Temp...will be changed for pdf once temp report support is implemented
      return this.$route.query.charttype
    },
  },
  watch: {
    reportId: {
      handler(newData, oldData) {
        this.dateFilter = false
        this.init(false)
      },
    },
    config: {
      handler(newData, oldData) {
        if (newData && oldData) {
          if (newData.id !== oldData.id) {
            this.init(false)
          } else {
            this.$emit('reportLoaded', this.reportObj, this.resultObj)
          }
        } else {
          this.init(false)
        }
      },
      deep: true,
    },
  },
  methods: {
    onPeriodChange() {
      this.initChart()
    },
    chartgenerate(value) {
      setTimeout(() => (this.chartgenerated = value), 10)
    },
    setDateFilter(dateFilter) {
      this.dateFilter = dateFilter
      if (this.userFilterApplied) {
        this.applyFilter(this.userFilterApplied)
      } else {
        this.initChart()
      }
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

    initChart() {
      let self = this
      self.loading = true
      self.failed = false

      let params = { newFormat: true }
      if (this.reportObj) {
        params.showAlarms = this.reportObj.options.settings.alarm
        params.showSafeLimit = this.reportObj.options.settings.safelimit
      }
      this.reportObj = null
      if (this.appliedDateRange) {
        params.startTime = this.appliedDateRange.startTime
        params.endTime = this.appliedDateRange.endTime
      } else if (self.dateFilter && self.dateFilter.value) {
        params.startTime = self.dateFilter.value[0]
        params.endTime = self.dateFilter.value[1]
      }

      if (self.period !== null) {
        params.xAggr = self.period
      }
      API.get('/v3/report/reading/view?reportId=' + this.reportId, params)
        .then(response => {
          if (response.error) {
            self.$message.error('Error while fetching report data')
            self.loading = false
            self.failed = true
          } else {
            let result = response.data
            if (!self.dateFilter) {
              if (self.appliedDateRange) {
                if (
                  self.appliedDateRange.startTime &&
                  self.appliedDateRange.endTime
                ) {
                  self.dateFilter = NewDateHelper.getDatePickerObject(
                    self.appliedDateRange.operatorId,
                    '' +
                      self.appliedDateRange.startTime +
                      ',' +
                      self.appliedDateRange.endTime +
                      ''
                  )
                } else {
                  self.dateFilter = NewDateHelper.getDatePickerObject(
                    self.appliedDateRange.operatorId,
                    self.appliedDateRange.value
                  )
                }
              } else {
                self.dateFilter = NewDateHelper.getDatePickerObject(
                  result.report.dateOperator,
                  result.report.dateValue
                )
              }
            }
            result.xAggr = response.data.report.xAggr
            result.dateRange = self.dateFilter
            self.period = response.data.report.xAggr

            if (
              result.report.reportState &&
              result.report.reportState.regressionConfig !== null
            ) {
              if (
                result.report.reportState.regressionType &&
                result.report.reportState.regressionConfig &&
                result.report.reportState.regressionType !== 'multiple'
              ) {
                result.regression = true
                result.regressionType = result.report.reportState.regressionType
                result.regressionConfig =
                  result.report.reportState.regressionConfig
              }
            }

            let reportObj = self.prepareReport(result, params)
            reportObj.alarms = self.prepareBooleanReport(result.reportData.aggr)

            if (
              result.report.reportState &&
              result.report.reportState.regressionConfig !== null
            ) {
              if (
                result.report.reportState.regressionType &&
                result.report.reportState.regressionConfig &&
                result.report.reportState.regressionType === 'multiple'
              ) {
                result.regression = true
                result.regressionType = result.report.reportState.regressionType
                result.regressionConfig =
                  result.report.reportState.regressionConfig
              }
            }

            if (reportObj.options.mode) {
              result.mode = reportObj.options.mode
            }
            self.resultObj = result
            if (self.appliedChartType) {
              // TODO remove
              reportObj.options.type = self.appliedChartType
            }
            self.reportObj = reportObj
            self.$nextTick(() => {
              if (self.$refs['newChart']) {
                self.$refs['newChart'].render(reportObj)
              }
            })
            self.$emit('reportLoaded', self.reportObj, self.resultObj)
            self.loading = false
          }
        })
        .catch(function(error) {
          console.log('####### error ', error)
          if (error) {
            self.loading = false
            self.failed = true
          }
        })
    },

    applyFilter(filters) {
      let self = this
      self.loading = true
      self.failed = false

      let reportContext = null

      if (
        self.resultObj &&
        self.resultObj.report &&
        self.resultObj.report.id &&
        self.resultObj.report.id != -1
      ) {
        reportContext = self.resultObj.report
      }

      let params = { newFormat: true }
      params.mode = this.resultObj.mode
      let chartState = self.resultObj.report.chartState
      let tabularState = self.resultObj.report.tabularState

      let chartStateObject
      let sortField
      let limit
      let orderByFunc
      if (
        self.reportObj &&
        self.reportObj.options &&
        self.reportObj.options.common &&
        self.reportObj.options.common.sorting
      ) {
        if (self.reportObj.options.common.sorting.sortByField) {
          sortField = self.reportObj.options.common.sorting.sortByField
        }
        if (self.reportObj.options.common.sorting.limit) {
          limit = self.reportObj.options.common.sorting.limit
          orderByFunc = self.reportObj.options.common.sorting.orderByFunc
        }
      } else if (chartState) {
        chartStateObject = JSON.parse(chartState)
        if (
          chartStateObject.options &&
          chartStateObject.options.common &&
          chartStateObject.options.common.sorting
        ) {
          if (chartStateObject.options.common.sorting.sortByField) {
            sortField = chartStateObject.options.common.sorting.sortByField
          }
          if (chartStateObject.options.common.sorting.limit) {
            limit = chartStateObject.options.common.sorting.limit
            orderByFunc = chartStateObject.options.common.sorting.orderByFunc
          }
        }
      }

      if ([1, 2, 3].includes(params.mode)) {
        params.xAggr = this.resultObj.report.xAggr
      }

      if (this.appliedDateRange) {
        params.startTime = this.appliedDateRange.startTime
        params.endTime = this.appliedDateRange.endTime
      } else if (self.dateFilter && self.dateFilter.value) {
        params.startTime = self.dateFilter.value[0]
        params.endTime = self.dateFilter.value[1]
      }

      let fields = []
      let categoryId = []
      for (let dp of this.resultObj.report.dataPoints) {
        if (
          dp.metaData &&
          dp.metaData.categoryId &&
          categoryId.indexOf(dp.metaData.categoryId) === -1
        ) {
          categoryId.push(dp.metaData.categoryId)
        }

        let dataField = {
          parentId: null,
          name: dp.name,
          metaData: dp.metaData,
          yAxis: {
            fieldId: dp.yAxis.fieldId,
            aggr: dp.yAxis.aggr,
            label: dp.name,
            dataType: dp.yAxis.dataType,
            unitStr: dp.yAxis.unitStr,
          },
          aliases: dp.aliases,
          type: dp.type > 0 ? dp.type : 1,
          transformWorkflow: dp.transformWorkflow,
          rule_aggr_type:dp?.rule_aggr_type
        }
        if (sortField && sortField === dp.yAxis.fieldId) {
          dataField.orderByFunc = orderByFunc
          dataField.limit = limit
        }

        fields.push(dataField)
      }
      params.fields = JSON.stringify(fields)

      if (
        this.resultObj.report.baseLines &&
        this.resultObj.report.baseLines.length
      ) {
        let baseLines = []
        for (let bl of this.resultObj.report.baseLines) {
          baseLines.push({
            baseLineId: bl.baseLineId,
            adjustType: bl.adjustType,
          })
        }
        params.baseLines = JSON.stringify(baseLines)
      }

      if (filters) {
        if (
          !filters.siteFilter.length &&
          !filters.buildingFilter.length &&
          !filters.categoryFilter.length &&
          !filters.assetFilter.length
        ) {
          params.xCriteriaMode = 2
        }
        if (filters.assetFilter.length) {
          params.xCriteriaMode = 3
          params.parentId = filters.assetFilter
        } else {
          let spaceFilter = filters.buildingFilter.length
            ? filters.buildingFilter
            : filters.siteFilter
          params.xCriteriaMode = 4
          params.spaceId = spaceFilter
          params.assetCategory = filters.categoryFilter.length
            ? filters.categoryFilter
            : categoryId
          if (!params.spaceId || !params.spaceId.length) {
            params.xCriteriaMode = 2
          }
        }
      }

      self.$http
        .post('/v2/report/readingReport', params)
        .then(function(response) {
          let result = response.data.result
          result.xAggr = params.xAggr
          result.mode = params.mode
          result.report.analyticsType = 1

          result.dateRange = self.dateFilter

          result.report.chartState = chartState
          result.report.tabularState = tabularState

          self.reportObj = self.prepareReport(result)
          if (reportContext) {
            result.report.id = reportContext.id
            result.report.name = reportContext.name
          }
          self.resultObj = result
          self.reportObj.params = params
          self.$emit('reportLoaded', self.reportObj, self.resultObj)
          self.loading = false
          self.userFilterApplied = filters
        })
        .catch(function(error) {
          console.log('####### error ', error)
          self.userFilterApplied = filters
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
          this.initChart()
        }
      } else if (changedOption === 'safelimit') {
        if (
          !this.reportObj.options.safeLimit.length &&
          this.reportObj.options.settings.safelimit
        ) {
          this.initChart()
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

.reports-summary.new-reports-summary .fc-new-chart.bb {
  padding-bottom: 30px;
}

.reports-summary.new-reports-summary .f-multichart .fc-new-chart.bb {
  padding-bottom: 0px;
}
</style>
