<template>
  <div class="analytics-section regression-analytics-section">
    <!-- <div v-if="!$mobile && config.predictionTimings && config.predictionTimings.length" class="predictive-header-section" v-bind:class="{active: predictive}">
      <img src="~assets/arrow-pointing-to-right.svg" height="14px" width="14px" class="predictive-arrow"> <div class="predictive-header" @click="predictive = !predictive">Predicted Timings</div>
    </div> -->
    <div style="text-align: center;" class="new-analytics-filter-section">
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
    <!-- <prediction-timings-bar :reportObj="reportObj" :config="config" class=" prediction-timing-bar" v-if="!$mobile && predictive && config.predictionTimings && config.predictionTimings.length"></prediction-timings-bar> -->
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
      v-else-if="resultObj && resultObj.reportData.data.length === 0"
      style="margin-top: 30px;opacity: 0.git 7;font-size: 13px;padding: 50px;line-height: 25px;text-align: center;"
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

    <div
      v-else-if="regressionModelList.length == 0"
      style="margin-top: 30px;opacity: 0.git 7;font-size: 13px;padding: 50px;line-height: 25px;text-align: center;"
    >
      <img
        class="no-chart-data"
        src="~statics/noData-light.png"
        style="width: 10%;"
      />
      <div class="mT15">{{ regressionErrorMessage }}</div>
    </div>

    <div
      v-else-if="config && config.regressionType === regressionTypes.SINGLE"
      class="height100"
    >
      <div
        v-if="Object.keys(regressionReportObjectList).length !== 0"
        class="fc-regression-scroll"
      >
        <f-new-chart
          ref="newChart"
          v-for="(report, reportIdx) in Object.keys(regressionReportObjectList)"
          :key="reportIdx"
          :data="regressionReportObjectList[report].data"
          :options="regressionReportObjectList[report].options"
          :alarms="regressionReportObjectList[report].alarms"
          :dateRange="regressionReportObjectList[report].dateRange"
          :resultObj="resultObj"
          :hidecharttypechanger="config.hidecharttypechanger || isRegression"
          :fixedChartHeight="270"
          :config="config"
          :showWidgetLegends="false"
        ></f-new-chart>

        <!-- <f-mobile-multi-chart ref="multiChart" v-if="($route.meta && $route.meta.layout && $route.meta.layout === 'mobile') && reportObj.options.settings.chartMode === 'multi'" :isWidget="config ? true : false" :width="config && config.width ? config.width : null" :resultObj="resultObj" :height="config && config.height ? config.height : null" :data="reportObj.data" :options="reportObj.options" :alarms="reportObj.alarms" :dateRange="reportObj.dateRange" :hidecharttypechanger="config && config.widget" :booleanData="reportObj.booleanData"></f-mobile-multi-chart> -->
        <!-- <div v-if="($route.meta && $route.meta.layout && $route.meta.layout === 'mobile') && reportObj.options.settings.chartMode === 'multi'" class="empty-multi-chart">Currently this chart not supported in the mobile</div> -->
        <!-- <f-multi-chart v-else-if="reportObj.options.settings.chartMode === 'multi'" ref="multiChart" :data="reportObj.data" :options="reportObj.options" :alarms="reportObj.alarms" :booleanData="reportObj.booleanData" :dateRange="reportObj.dateRange" :resultObj="resultObj" :hidecharttypechanger="config.hidecharttypechanger || isRegression"></f-multi-chart>
      <div v-else>
       <f-mobile-chart v-if="($route.meta && $route.meta.layout && $route.meta.layout === 'mobile')" ref="newChart" :data="reportObj.data" :options="reportObj.options" :dateRange="reportObj.dateRange" :resultObj="resultObj" class="mobile-chart"></f-mobile-chart>

      </div> -->
        <el-tabs
          v-if="regressionModelList.length !== 0"
          v-model="activeRegressionModel"
          class="single-regression-tab mT50"
        >
          <el-tab-pane
            :label="regressionInfo[0].dataPoints[0].name"
            :name="regressionInfo[0].alias"
            v-for="(regressionInfo, regressionInfoIdx) in regressionModelList"
            :key="regressionInfoIdx"
          >
            <MultipleRegressionAnalysis
              :regressionMetrics="regressionInfo"
              :analyticsConfig="config"
              class="regression-single-chart-scroll"
            ></MultipleRegressionAnalysis>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
    <div v-else>
      <MultipleRegressionAnalysis
        :resultObj="resultObj"
        :analyticsConfig="config"
      ></MultipleRegressionAnalysis>
    </div>
  </div>
</template>

<script>
import NewDatePicker from '@/NewDatePicker'
import FNewChart from 'newcharts/components/FNewChart'
// import FMobileChart from 'newcharts/components/FMobileChart'
// import FMultiChart from 'newcharts/components/FMultiChart'
import NewDataFormatHelper from 'pages/report/mixins/NewDataFormatHelper'
import AnalyticsMixin from 'pages/energy/analytics/mixins/AnalyticsMixin'
import FChartSettings from 'newcharts/components/FChartSettings'
// import FMobileMultiChart from 'newcharts/components/FMobileMultiChart'
import NewDateHelper from '@/mixins/NewDateHelper'
import FNewAnalyticModelHelper from 'src/pages/report/mixins/FNewAnalyticsModelHelper'
import MultipleRegressionAnalysis from 'src/pages/energy/analytics/newTools/v1/MultipleRegressionAnalysis'
import { API } from '@facilio/api'

export default {
  props: ['config', 'baseLines', 'readings'],
  mixins: [NewDataFormatHelper, AnalyticsMixin, NewDateHelper],
  components: {
    NewDatePicker,
    FNewChart,
    // FMultiChart,
    // FMobileChart,
    FChartSettings,
    // FMobileMultiChart,
    MultipleRegressionAnalysis,
  },
  data() {
    return {
      loading: true,
      predictive: true,
      failed: false,
      reportObj: null,
      reportList: {},
      resultObj: null,
      regressionTypes: null,
      activeRegressionModel: null,
    }
  },
  created() {
    this.regressionTypes = FNewAnalyticModelHelper.regressionTypes()
    this.$store.dispatch('loadAssetCategory')
  },
  mounted() {
    this.init()
  },
  computed: {
    regressionErrorMessage() {
      let errorMessage = ''
      if (this.config.regressionType === 'single') {
        errorMessage =
          'Data is not enough to construct a model using regression.'
      } else {
        if (
          this.resultObj &&
          this.resultObj.regressionConfig &&
          this.resultObj.regressionConfig.length != 0
        ) {
          errorMessage =
            this.resultObj.regressionConfig[0].errorState === 2
              ? 'Data available is not enough to construct a model using regression'
              : 'A Model cannot be constructed with the available data using regression.'
        }
      }
      return errorMessage
    },
    regressionModelList() {
      let regressionMetrics = []
      let regressionMetricKeys = FNewAnalyticModelHelper.regressionMetrics()

      if (this.resultObj && this.resultObj.reportData.regressionResult) {
        for (let config of this.resultObj.regressionConfig) {
          let key = FNewAnalyticModelHelper.computeRegressionAlias(config)
          let temp = {}
          temp['alias'] = key
          let result = this.resultObj.reportData.regressionResult[key]
          if (result) {
            let regressionDataPoint = this.resultObj.report.dataPoints.filter(
              dp => dp.aliases.actual === key
            )
            if (regressionDataPoint.length) {
              temp['expressionString'] = regressionDataPoint[0].expressionString
            }

            let dataPoints = []
            for (let alias in result.coefficientMap) {
              if (alias !== 'constant') {
                let dataPoint = this.resultObj.report.dataPoints.find(
                  dp => dp.aliases.actual === alias
                )
                if (dataPoint) {
                  dataPoints.push(dataPoint)
                }
              }
            }

            let yPoint = this.resultObj.report.dataPoints.find(
              dp => dp.aliases.actual === config.yAxis.alias
            )
            if (yPoint) {
              temp['yPoint'] = yPoint
            }

            temp['dataPoints'] = dataPoints

            for (let heading of regressionMetricKeys) {
              temp[heading.name] = result[heading.name]
            }

            temp['anovaMetrics'] = result['anovaMetrics']

            for (let variableName in result.regressionMetrics) {
              let row = result.regressionMetrics[variableName]
              row['name'] = variableName
            }

            temp['regressionMetrics'] = result.regressionMetrics

            regressionMetrics.push([temp])
          }
        }
      }
      if (
        this.config.regressionType ===
          FNewAnalyticModelHelper.regressionTypes().SINGLE &&
        regressionMetrics.length !== this.config.regressionConfig.length
      ) {
        return []
      }
      return regressionMetrics
    },
    regressionReportObjectList() {
      let reportList = []
      if (this.resultObj && this.resultObj.regressionConfig) {
        for (let reportKey in this.reportList) {
          let reportObject = this.reportList[reportKey]
          if (reportObject.data && reportObject.data.length != 0) {
            reportList.push(reportObject)
          }
        }
      }

      if (this.config.regressionConfig.length !== reportList.length) {
        return []
      }
      return reportList
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
        if (!this.config.applyReportDate) {
          this.init(false)
        }
      },
      deep: true,
    },
  },
  methods: {
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
      if (
        this.config.regressionConfig &&
        this.config.regressionConfig.length !== 0
      ) {
        this.setRegressionConfigAlias()
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

    setRegressionConfigAlias() {
      for (let conf of this.config.regressionConfig) {
        let yDataPoint = this.config.dataPoints.filter(
          dp =>
            dp.parentId === conf.yAxis.parentId &&
            dp.yAxis.fieldId === conf.yAxis.readingId
        )

        if (yDataPoint.length !== 0) {
          conf.yAxis['alias'] = yDataPoint[0].aliases.actual
        }

        for (let xPoint of conf.xAxis) {
          let xDataPoint = this.config.dataPoints.filter(
            dp =>
              dp.parentId === xPoint.parentId &&
              dp.yAxis.fieldId === xPoint.readingId
          )
          if (xDataPoint.length !== 0) {
            xPoint.alias = xDataPoint[0].aliases.actual
          }
        }
        // if(this.config.regressionType === this.regressionTypes.MULTIPLE){

        // }
        // else{
        //   let xDataPoint = this.config.dataPoints.filter((dp) => dp.parentId === conf.xAxis.parentId && dp.yAxis.fieldId === conf.xAxis.readingId)
        //   if(xDataPoint.length !== 0){
        //     conf.xAxis['alias'] = xDataPoint[0].aliases.actual
        //   }
        // }
      }
    },
    initChart() {
      let self = this
      self.loading = true
      self.failed = false

      // let apiURL = this.config.api ? this.config.api : (this.config.regressionConfig && this.config.regressionType === this.regressionTypes.SINGLE )? '/v2/report/readingReport' : '/v2/report/regressionreport'
      let apiURL = '/v3/report/reading/regression'
      let params = null
      if (this.config.alarmId) {
        apiURL = '/v2/report/fetchReadingsFromAlarm'
        params = {
          alarmId: this.config.alarmId,
          mode: 1,
          xAggr: 0,
          startTime: this.config.dateFilter.value[0],
          endTime: this.config.dateFilter.value[1],
          showAlarms: true,
          regressionConfig: this.config.regressionConfig,
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
      params.newFormat = true

      // if(this.config.regressionConfig && this.config.regressionConfig.length != 0 && this.config.regressionType === this.regressionTypes.MULTIPLE){
      //   params['regressionConfig'] = this.config.regressionConfig
      // }

      if (this.config.regressionConfig) {
        params['regressionConfig'] = this.config.regressionConfig
        params['regressionType'] = this.config.regressionType
      }
      API.post(apiURL, params)
        .then(response => {
          if (!response.error) {
            let result = response.data.result
              ? response.data.result
              : response.data
            if (!result) {
              self.loading = false
              self.failed = true
              return
            }
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

            let regressionConfig = result.report.reportState.regressionConfig
            if (
              regressionConfig &&
              regressionConfig.length > 0 &&
              self.config.regressionType ===
                FNewAnalyticModelHelper.regressionTypes().SINGLE
            ) {
              self.reportList = {}
              for (let config of regressionConfig) {
                let tempResult = JSON.parse(JSON.stringify(result))
                tempResult['regression'] = true
                tempResult['regressionConfig'] = [config]
                tempResult['regressionType'] = self.config.regressionType
                tempResult['filterRegression'] = true

                if (config.groupAlias && self.reportList[config.groupAlias]) {
                  let chartState = self.reportList[config.groupAlias].options
                  tempResult.report.chartState = chartState
                }

                let reportObject = self.prepareReport(tempResult)

                let groupAlias = config.groupAlias
                if (groupAlias) {
                  self.reportList[groupAlias] = reportObject
                }
              }
            }

            if (
              self.config.regressionType ===
              FNewAnalyticModelHelper.regressionTypes().SINGLE
            ) {
              result['regression'] = true
              result[
                'regressionType'
              ] = FNewAnalyticModelHelper.regressionTypes().SINGLE
              result['regressionConfig'] =
                result.report.reportState.regressionConfig

              let firstKey = FNewAnalyticModelHelper.computeRegressionAlias(
                self.config.regressionConfig[0]
              )
              self.activeRegressionModel = firstKey
            }
            self.reportObj = self.prepareReport(result)

            if (
              self.config.regressionType ===
              FNewAnalyticModelHelper.regressionTypes().MULTIPLE
            ) {
              result['regression'] = true
              result['regressionType'] = self.config.regressionType
              result['regressionConfig'] =
                result.report.reportState.regressionConfig

              for (let conf of self.config.regressionConfig) {
                let groupAlias = FNewAnalyticModelHelper.computeRegressionAlias(
                  conf
                )
                conf['groupAlias'] = groupAlias
              }
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
            self.resultObj = result
            self.reportObj.params = params

            self.reportObj.alarms = self.prepareBooleanReport(
              result.reportData.aggr
            )
            if (self.config.alarmId) {
              self.reportObj.alarms.barSize = 'medium'
            }
            self.reportObj.options.predictionTimings =
              self.config.predictionTimings
            self.$emit('reportLoaded', self.reportObj, self.resultObj)
            self.loading = false
          }
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

<style lang="scss">
.regression-analytics-section {
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
  .fc-regression-scroll {
    height: 100%;
    overflow-y: scroll;
    overflow-x: hidden;
    padding-bottom: 100px;
  }
  .regression-single-chart-scroll {
    .fc__layout__asset_main {
      position: inherit;
      padding-top: 0;
      padding-bottom: 0;
      //  .fc__layout__flexes{
      //    padding-left: 0px;
      //    padding-right: 0px;
      //    padding-top: 0;
      //  }
    }
    .fc__asset__main__scroll {
      height: inherit;
    }
  }
  .single-regression-tab {
    .el-tabs__nav {
      margin-left: 40px;
    }
    .el-tabs__active-bar {
      width: 40px !important;
    }
  }
}
.regression-analysis-chart .f-singlechart {
  box-shadow: 0 3px 7px 0 rgba(233, 233, 226, 0.5);
  background-color: #ffffff;
  margin: 14px;
  padding: 12px 0;
}
</style>
