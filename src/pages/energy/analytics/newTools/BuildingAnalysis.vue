<template>
  <div class="building-analysis-page">
    <div
      class="regression-analysis-page-options-con flex-middle"
      v-if="isRegression"
    >
      <f-report-options
        :isActionsDisabled="isActionsDisabled"
        optionClass="analytics-page-options"
        :optionsToEnable="computeReportOptions"
        :reportObject="reportObject"
        :resultObject="resultObject"
        :params="reportObject ? reportObject.params : null"
        class="analytics-page-options-building-analysis "
        :savedReport.sync="analyticsConfig.savedReport"
      ></f-report-options>
    </div>
    <div class="analytics-section-new row" :key="$route.path">
      <!-- analytics page header start -->
      <div v-if="!isRegression" class="newanalytics-page-header">
        <div class="row">
          <div class="col-8">
            <div class="analytics-page-header-filters">
              <div v-if="!isRegression" class="col-2">
                <div class="building-avatar-block fL">
                  <div class="building-avatar">
                    <img
                      src="~statics/space/building.svg"
                      height="40px"
                      width="40px"
                    />
                  </div>
                </div>
              </div>
              <div class="col-2">
                <div class="fL">
                  <div class="selected-buildings-analytics">
                    {{
                      isSiteAnalysis
                        ? $t('common.products.site_analysis')
                        : $t('common.products.building_analysis')
                    }}
                  </div>
                </div>
              </div>
              <div class="col-4">
                <div class="mL20">
                  <el-select
                    v-if="!isRegression"
                    class="period-select"
                    v-model="analyticsConfig.mode"
                    @change="onModeChange"
                    :placeholder="$t('common._common.mode')"
                    :title="$t('common._common.mode')"
                    data-arrow="true"
                    v-tippy
                  >
                    <el-option
                      :label="$t('common.wo_report.time')"
                      :value="1"
                    ></el-option>
                    <el-option
                      :label="$t('common.products.series')"
                      :value="2"
                    ></el-option>
                    <el-option
                      :label="$t('common.products.conslidated')"
                      :value="3"
                      v-if="isSameDataPoint"
                    ></el-option>
                  </el-select>
                  <el-cascader
                    class="period-select"
                    :placeholder="$t('common._wo_report.compare_to')"
                    :options="baseLineCasecaderOptions"
                    :value="analyticsConfig.baseLine"
                    v-if="baseLineList && !isActionsDisabled && !isRegression"
                    @change="onBaseLineChange"
                    :title="$t('common.wo_report.compare_with_baseline')"
                    data-arrow="true"
                    v-tippy
                  >
                  </el-cascader>
                  <el-select
                    class="period-select"
                    v-model="analyticsConfig.period"
                    :placeholder="$t('common.wo_report.period')"
                    v-if="analyticsConfig.mode !== 2 && !isRegression"
                    :title="$t('common.dashboard.time_period')"
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
              </div>
            </div>
          </div>
          <div class="col-4" style="text-align: right;">
            <f-report-options
              :isActionsDisabled="isActionsDisabled"
              optionClass="analytics-page-options"
              :optionsToEnable="computeReportOptions"
              :reportObject="reportObject"
              :resultObject="resultObject"
              :params="reportObject ? reportObject.params : null"
              class="analytics-page-options-building-analysis"
              :savedReport.sync="analyticsConfig.savedReport"
            ></f-report-options>
          </div>
        </div>
      </div>

      <f-data-point-selector
        v-if="!isRegression"
        ref="building-selector"
        :reportObject="reportObject ? reportObject : null"
        :savedPoints="
          analyticsConfig.savedReport && resultObject
            ? dataPointSummaryList
            : null
        "
        :visibility.sync="showDataPointSelector"
        @selectBuildings="setSelectedBuildings"
        @updateDataPoints="updateDataPoints"
        :zones="zones"
        :isSiteAnalysis="isSiteAnalysis"
      ></f-data-point-selector>

      <FRegressionPointSelector
        v-else
        @resetState="resetState"
        :visibility.sync="showRegressionPointSelector"
        :enableFloatimngIcon.sync="enableFloatimngIcon"
        @updateDataPoints="applyregression"
        :analyticsConfig.sync="analyticsConfig"
      ></FRegressionPointSelector>
      <!-- <div v-if="!isRegression" class="report-settings-group1 report-settings-group" v-bind:class="{'settings-group-edge1': !showSidebar}">
        <el-button-group>
        <el-button type="primary" :class="{'report-button-active':showInnerPanel}" @click="showSidebar = false, showInnerPanel = true, showChartCustomization = false" class="report-settings-btn" style="padding: 12.3px 10px;"><img src="~assets/statistics.svg" class="report-option-img report-rotate-icons" width="25" height="25"></el-button>
        <el-button type="primary" :class="{'report-button-active':showChartCustomization}" :disabled ="reportObject === null || typeof(reportObject) === 'undefined'" @click="showSidebar = false, showChartCustomization = true, showInnerPanel = false" class="report-settings-btn report-settings-btn2"><img src="~assets/settings-grey2.svg" width="18" height="18" class="report-option-img"></el-button>
        <el-button type="primary" @click="toggleSideBar" style="transform:rotate(180deg);" class="report-settings-btn report-settings-btn3"><img src="~assets/arrow-pointing-to-left2.svg" width="18" height="18" class="report-option-img report-rotate-icons" :style="{transform: (showSidebar ? 'rotate(90deg)' : 'rotate(270deg)')}"></el-button>
      </el-button-group>
    </div> -->
      <!-- CHART CUSTOMIZATION start -->
      <div
        class="customize-container building-customize-container col-4"
        v-if="reportObject && !isRegression"
        v-show="showChartCustomization"
      >
        <div class="customize-header">
          <div class="customize-H">
            {{ $t('home.reports.chart_customization') }}
            <i class="el-icon-close" @click="hideChartCustomizationDialog"></i>
          </div>
        </div>
        <div class="customize-body">
          <f-chart-customization
            v-if="reportObject"
            v-show="showChartCustomization"
            @close="showChartCustomization = false"
            :report="reportObject"
            :resultDataPoints="
              resultObject ? resultObject.report.dataPoints : []
            "
            :config="analyticsConfig"
            @resetPoints="resetDataPointSelector"
          ></f-chart-customization>
        </div>
      </div>
      <!-- CHART CUSTOMIZATION end -->

      <!-- Graph section start -->
      <div
        class="analytic-summary new-analytic-summary scrollable"
        :style="
          showChartCustomization
            ? 'max-width: calc(100% - 450px) !important;'
            : ''
        "
        :class="{
          'col-12, mleft0': !showChartCustomization,
          'col-8, mleft447': showChartCustomization,
          'regression-bg':
            isRegression &&
            analyticsConfig.regressionType &&
            analyticsConfig.regressionType === 'multiple',
        }"
      >
        <div class="add-points-block" v-show="!showChartCustomization">
          <el-button-group v-if="!isRegression">
            <el-button
              type="primary"
              class="add-points-btn"
              @click="showDataPointSelector = true"
            >
              <i class="el-icon-plus"></i
              >{{ $t('home.dashboard.add_points') }}</el-button
            >
            <el-button
              type="primary"
              class="add-points-btn"
              v-if="reportObject && analyticsConfig.mode === 1"
              @click="showDerivation = true"
              ><img
                src="~statics/formula/formula-grey.svg"
                width="17px"
                height="17px"
            /></el-button>
          </el-button-group>
          <el-button
            type="primary"
            class="add-points-btn add-points-btn2"
            v-if="reportObject"
            @click="showChartCustomizationDialog()"
            ><img src="~assets/settings-grey.svg" width="17px" height="17px"
          /></el-button>
          <!-- <prediction-timings-bar :config="analyticsConfig" class="inline" v-if="reportObject && analyticsConfig.predictionTimings && analyticsConfig.predictionTimings.length"></prediction-timings-bar> -->
        </div>
        <div
          ref="chartSection"
          :class="[
            'self-center fchart-section building-graph-container',
            {
              'prediction-timings-container':
                analyticsConfig.predictionTimings &&
                analyticsConfig.predictionTimings.length > 1,
              'regression-analysis-chart': isRegression,
            },
          ]"
        >
          <div v-if="!analyticsConfig.dataPoints.length" class="text-center">
            <div class="p15" v-if="reportId || alarmId || cardId">
              <spinner :show="true" />
            </div>
            <div class="p15" v-else-if="repid">
              <f-new-report
                v-if="repid"
                :id="repid"
                @reportLoaded="reportLoaded"
              ></f-new-report>
            </div>
            <div
              class="p15 flex-middle height80vh justify-content-center flex-direction-column"
              v-else
            >
              <div>
                <inline-svg
                  src="svgs/emptystate/reportlist"
                  iconClass="icon text-center icon-xxxlg"
                ></inline-svg>
              </div>
              <div class="nowo-label">
                {{ $t('common._common.please_select_data_points_analyze') }}
              </div>
            </div>
          </div>

          <f-new-analytic-report
            v-else-if="!isRegression"
            ref="newAnalyticReport"
            :config.sync="analyticsConfig"
            :baseLines="baseLineList"
            @reportLoaded="reportLoaded"
          ></f-new-analytic-report>
          <FNewAnalyticRegression
            class="regression-single"
            @reportLoaded="reportLoaded"
            v-if="
              isRegression &&
                analyticsConfig &&
                analyticsConfig.regressionConfig &&
                analyticsConfig.regressionConfig.length != 0
            "
            ref="newAnalyticRegressionReport"
            :config.sync="analyticsConfig"
            :baseLines="baseLineList"
          ></FNewAnalyticRegression>
        </div>
      </div>
      <!-- Graph section end -->

      <derivation
        v-if="showDerivation"
        :visibility.sync="showDerivation"
        :report="reportObject"
        :config="analyticsConfig"
      ></derivation>
    </div>
  </div>
</template>
<script>
import FNewReport from 'pages/report/components/FNewReport'
import AnalyticsMixin from 'pages/energy/analytics/mixins/AnalyticsMixin'
import FNewAnalyticReport from 'pages/energy/analytics/components/FNewAnalyticReport'
import FDataPointSelector from 'pages/energy/analytics/components/FDataPointSelector'
import Derivation from 'pages/energy/analytics/components/FDerivation'
import FChartCustomization from 'newcharts/components/FChartCustomization'
import FReportOptions from 'pages/report/components/FReportOptions'
import NewDateHelper from '@/mixins/NewDateHelper'
import FRegressionPointSelector from 'pages/report/components/FRegressionPointSelector'
import FNewAnalyticRegression from 'src/pages/report/components/FNewAnalyticRegression'

export default {
  props: ['mobileConfig'],
  mixins: [AnalyticsMixin],
  components: {
    FRegressionPointSelector,
    Derivation,
    FNewAnalyticReport,
    FChartCustomization,
    FDataPointSelector,
    FReportOptions,
    FNewAnalyticRegression,
    FNewReport,
  },
  data() {
    return {
      showRegressionPointSelector: true,
      repid: null,
      enableFloatimngIcon: true,
      showDataPointSelector: false,
      showChartCustomization: false,
      showDerivation: false,
      chartCustomizationActiveTab: 'datapoints',
      selectedBaseLineId: '',
      baseLineList: null,
      baseLineCasecaderOptions: [],
      oldBaseLine: null,
      analyticsConfig: {
        name: 'Building Analysis',
        key: 'BUILDING_ANALYSIS',
        analyticsType: this.isSiteAnalysis ? 6 : 2,
        type: 'reading',
        period: 0,
        mode: 1,
        baseLine: null,
        dateFilter: NewDateHelper.getDatePickerObject(22),
        chartViewOption: 0,
        dataPoints: [],
        defaultDate: false,
        predictionTimings: [],
        transformWorkflow: null,
      },
      reportObject: null,
      resultObject: null,
      selectedBuildings: [],
      selectedBuildingTitle: '',
      zones: null,
      dataPointSummaryList: null,
    }
  },
  watch: {
    reportId: function() {
      if (this.reportId) {
        this.loadReport()
      }
    },
    getAvailablePeriods: {
      handler(newData, oldData) {
        let avail = this.getAvailablePeriods
        let selected = avail.find(
          a => a.value === this.analyticsConfig.period && a.enable
        )
        if (!selected) {
          let filterName = this.analyticsConfig.dateFilter.operationOn

          let defaultPeriod = avail.filter(a => a.enable)[0].value
          if (filterName === 'week') {
            defaultPeriod = 12
          } else if (filterName === 'month') {
            defaultPeriod = 12
          } else if (filterName === 'year') {
            defaultPeriod = 10
          }
          this.analyticsConfig.period = defaultPeriod
        }
      },
      immediate: true,
    },
    showChartCustomization: function(newVal) {
      let self = this
      self.$nextTick(function() {
        if (
          self.$refs['newAnalyticReport'] &&
          self.$refs['newAnalyticReport'].resize
        ) {
          self.$refs['newAnalyticReport'].resize()
        }
      })
    },
  },
  created() {
    this.$store.dispatch('loadBuildings')
  },
  mounted() {
    if (this.isSiteAnalysis) {
      this.analyticsConfig.analyticsType = 6
    }
    if (this.isRegression) {
      this.analyticsConfig.dateFilter = NewDateHelper.getDatePickerObject(28)
    }
    if (this.$route.query.filters) {
      let filters = this.$route.query.filters
      let filtersJSON = JSON.parse(filters)
      this.analyticsConfig = filtersJSON
    }
    if (this.reportId || this.alarmId || this.cardId) {
      if (this.$mobile) {
        this.loadReport(this.mobileConfig)
      } else {
        this.loadReport()
      }
    }

    let self = this
    self.$http.get('/baseline/all').then(function(response) {
      if (response.status === 200) {
        self.baseLineList = response.data ? response.data : []

        self.baseLineCasecaderOptions = []
        self.baseLineCasecaderOptions.push({
          label: 'None',
          value: -1,
        })

        for (let b of self.baseLineList) {
          let children = null
          if (
            b.rangeTypeEnum === 'PREVIOUS_MONTH' ||
            b.rangeTypeEnum === 'ANY_MONTH'
          ) {
            children = [
              {
                label: 'Same date',
                value: 4,
              },
              {
                label: 'Same week',
                value: 1,
              },
            ]
          } else if (
            b.rangeTypeEnum === 'PREVIOUS_YEAR' ||
            b.rangeTypeEnum === 'ANY_YEAR'
          ) {
            children = [
              {
                label: 'Same date',
                value: 3,
              },
              {
                label: 'Same week',
                value: 1,
              },
            ]
          }

          if (b.rangeTypeEnum !== 'PREVIOUS') {
            self.baseLineCasecaderOptions.push({
              label: b.name,
              value: b.id,
              children: children,
            })
          }
        }
      }
    })

    self.loadZones()
  },
  computed: {
    computeReportOptions() {
      if (this.analyticsConfig && this.isRegression) {
        return [6, 5]
      } else {
        return [1, 2, 5]
      }
    },
    isRegression() {
      if (
        this.$route.path.includes('regression') ||
        (this.analyticsConfig.regressionConfig &&
          this.analyticsConfig.regressionConfig.length !== 0)
      ) {
        return true
      } else {
        return false
      }
    },
    isSiteAnalysis() {
      return this.$route.meta && this.$route.meta.analyticsType === 'site'
    },
    isSameDataPoint() {
      if (this.analyticsConfig && this.analyticsConfig.dataPoints.length) {
        let dp = {}
        for (let d of this.analyticsConfig.dataPoints) {
          dp[d.yAxis.fieldId] = true
        }
        if (Object.keys(dp).length === 1) {
          return true
        }
      }
      return false
    },
    getAvailablePeriods() {
      let operationOnId = this.analyticsConfig.dateFilter.operationOnId
      let avail = []
      let isRangeEnabled = this.enableForRangeMode()

      avail.push({
        name: 'High-res',
        value: 0,
        enable:
          operationOnId !== 6
            ? operationOnId !== 4 && isRangeEnabled
            : true && isRangeEnabled,
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
        name: this.$t('common.header.hour_of_day'), // 12, 1, 2, 3
        value: 19,
        enable: true,
      })

      avail.push({
        name: this.$t('common.header.day_of_week'), // sun, mon
        value: 17,
        enable: true,
      })

      avail.push({
        name: this.$t('common.header.day_of_month'), // 1,2,3
        value: 18,
        enable: true,
      })

      return avail
    },
  },
  methods: {
    resetState() {
      Object.assign(this.$data, this.$options.data.apply(this))
    },
    prepareOptionDataPoints(rConfig, regressionType) {
      console.log('option dataPoints')
      console.log(rConfig)
      let yAxis = false
      let dataPoints = []
      if (rConfig.length !== 0) {
        for (let point of rConfig) {
          for (let key in point) {
            if ((key === 'yAxis' && yAxis === false) || key === 'xAxis') {
              if (key === 'yAxis') {
                yAxis = true
              }
              if (key === 'xAxis') {
                for (let dp of point[key]) {
                  let temp = {}
                  temp['parentId'] =
                    typeof dp.parentId === 'string'
                      ? parseInt(dp.parentId)
                      : dp.parentId
                  temp['prediction'] = false
                  temp['aliases'] = {}
                  temp['yAxis'] = {}
                  temp.yAxis['fieldId'] = dp.readingId
                  temp.yAxis['aggr'] = dp.aggr
                  temp['isRegression'] = true
                  dataPoints.push(temp)
                }
              } else {
                let temp = {}
                temp['parentId'] =
                  typeof point[key].parentId === 'string'
                    ? parseInt(point[key].parentId)
                    : point[key].parentId
                temp['prediction'] = false
                temp['aliases'] = {}
                temp['yAxis'] = {}
                temp.yAxis['fieldId'] = point[key].readingId
                temp.yAxis['aggr'] = point[key].aggr
                temp['isRegression'] = true
                dataPoints.push(temp)
              }
            }
          }
        }
      }
      return dataPoints
    },
    applyregression(dataPoints) {
      if (this.checkRegressionPointStructure(dataPoints)) {
        let regressionPoints = dataPoints.regressionPoints
        console.log('Regression Points')
        console.log(dataPoints.regressionPoints)
        let config = JSON.parse(JSON.stringify(this.analyticsConfig))
        if (dataPoints.result.meta) {
          this.selectedBuildings = dataPoints.result.meta.selectedBuildingIds
          this.selectedBuildingTitle =
            dataPoints.result.meta.selectedBuildingTitle
        }
        if (dataPoints.regressionPoints.length === 0) {
          this.reportObj = null
          this.resultObj = null
          this.analyticsConfig.regressionConfig = []
          this.analyticsConfig.regressionType = ''
          this.analyticsConfig.dataPoints = []
        } else {
          config['regressionType'] = dataPoints.regressionType
          config['dataPoints'] = this.prepareOptionDataPoints(
            regressionPoints,
            dataPoints.regressionType
          )
          config['regressionConfig'] = dataPoints.regressionPoints
          config['hidechartoptions'] = true
          this.analyticsConfig = config
        }
      }
    },
    checkRegressionPointStructure(dataPoints) {
      let regressionPoints = dataPoints.regressionPoints
      for (let rPoint of regressionPoints) {
        if (!rPoint.xAxis || !rPoint.yAxis) {
          return false
        }
      }
      return true
    },
    prepareDataPointSummary() {
      if (this.reportId && this.resultObject) {
        let summaryList = []
        let weatherModules = {
          weather: 'Live Weather',
          weatherDaily: 'Daily Weather',
          psychrometric: 'Psychrometric',
          cdd: 'Degree Days',
          wdd: 'Degree Days',
          hdd: 'Degree Days',
        }

        for (let dataPoint of this.resultObject.report.dataPoints) {
          if (dataPoint.yAxis && dataPoint.yAxis.module) {
            if (
              Object.keys(weatherModules).includes(dataPoint.yAxis.module.name)
            ) {
              this.addToGroup(
                dataPoint,
                weatherModules[dataPoint.yAxis.module.name],
                summaryList
              )
            } else if (
              dataPoint.metaData.parentIds[0] ===
                parseInt(this.selectedBuildings[0]) &&
              dataPoint.yAxis.module.typeEnum.toLowerCase() ===
                'scheduled_formula'
            ) {
              this.addToGroup(dataPoint, 'Enpi', summaryList)
            } else if (
              dataPoint.metaData.parentIds[0] ===
              parseInt(this.selectedBuildings[0])
            ) {
              this.addToGroup(dataPoint, 'Space', summaryList)
            } else {
              this.addToGroup(dataPoint, 'Asset', summaryList)
            }
          }
        }
        console.log('Summary list')
        console.log(summaryList)
        return summaryList
      }
    },
    addToGroup(dataPoint, label, summaryList) {
      let reading = {}
      reading['readingId'] = dataPoint.yAxis.fieldId
      reading['readingLabel'] = dataPoint.yAxis.field.displayName
      reading['resourceId'] =
        label === 'Asset'
          ? parseInt(dataPoint.metaData.parentIds[0])
          : label === 'Space' || label === 'Enpi'
          ? parseInt(this.selectedBuildings[0])
          : this.$store.state.buildings.filter(
              building => building.id === parseInt(this.selectedBuildings[0])
            )[0].siteId
      reading['resourceLabel'] = dataPoint.resourceName
      ;(reading['marked'] = null),
        (reading['location'] = this.selectedBuildingTitle)
      reading['categoryId'] = dataPoint.assetCategoryId
      reading['buildingId'] = dataPoint.buildingId

      if (summaryList && summaryList.length === 0) {
        let temp = {}
        temp['label'] = label
        temp['readings'] = []
        temp.readings.push(reading)
        summaryList.push(temp)
      } else if (summaryList && summaryList.length > 0) {
        let readingGroup = summaryList.filter(group => group.label === label)
        if (readingGroup.length !== 0) {
          readingGroup[0].readings.push(reading)
        } else {
          let temp = {}
          temp['label'] = label
          temp['readings'] = []
          temp.readings.push(reading)
          summaryList.push(temp)
        }
      }
    },
    loadAlarmMeta() {
      this.$http
        .post('/dashboard/getReportMetaForAlarms', {
          alarmId: this.$route.query.alarmId,
        })
        .then(response => {
          if (response.data && response.data.reportMeta) {
            this.analyticsConfig.dataPoints =
              response.data.reportMeta.dataPoints
          }
        })
    },
    reportLoaded(report, result) {
      this.reportObject = report
      this.resultObject = result
      if (this.analyticsConfig.savedReport) {
        if (
          this.selectedBuildings.length === 0 &&
          report.options.common.buildingIds
        ) {
          this.selectedBuildings = report.options.common.buildingIds
          this.selectedBuildingTitle = report.options.common.buildingName
        }
        this.dataPointSummaryList = this.prepareDataPointSummary()
      }
      if (report.options.common && report.options.common.buildingIds) {
        if (
          !report.options.common.buildingIds.length &&
          this.selectedBuildings
        ) {
          this.reportObject.options.common.buildingIds = this.selectedBuildings
          this.reportObject.options.common.buildingName = this.selectedBuildingTitle
        } else {
          this.resetDataPointSelector(report.options.common.buildingIds)
        }
      }
    },
    loadZones() {
      let self = this
      self.$http.get('/zone').then(function(response) {
        if (response.data && response.data.records) {
          self.zones = response.data.records
        }
      })
    },
    resetDataPointSelector(buildingIds) {
      console.log('reset DataPoint selector')
      if (!this.analyticsConfig.regressionConfig) {
        this.$refs['building-selector'].setInitialValues(
          this.analyticsConfig.dataPoints,
          buildingIds
        )
      }
    },
    updateDataPoints(changedDataPoints) {
      if (this.analyticsConfig.dataPoints.length) {
        let configIndexes = []
        let alreadyAddedIndexes = []
        let pointsToRemove = []

        let optionPoints = []
        this.reportObject.options.dataPoints.forEach(dp => {
          if (dp.type === 'group' || dp.type === 'rangeGroup') {
            optionPoints.push(...dp.children)
          } else {
            optionPoints.push(dp)
          }
        })

        let isValid = optionPoints
          .filter(dp => dp.pointType !== 2)
          .every(dp => {
            let idx = changedDataPoints.findIndex(
              cdp =>
                cdp.parentId === dp.parentId && cdp.yAxis.fieldId === dp.fieldId
            )
            if (idx === -1) {
              if (
                !this.checkAndDeletePointsRel(
                  this.reportObject.options,
                  dp.alias
                )
              ) {
                this.$message.error(
                  dp.label +
                    ' has been used in derivation(s). Please delete the derivations before removing this point'
                )
                this.resetDataPointSelector()
                return false
              }
              pointsToRemove.push(dp.alias)
            }
            if (idx !== -1) {
              let configIdx = this.getConfigDataPointFromOptionDP(
                this.analyticsConfig,
                dp,
                true
              )
              configIndexes.push(configIdx)
              alreadyAddedIndexes.push(idx)
            }
            return true
          })
        if (isValid) {
          this.removeOptionPoints(this.reportObject.options, pointsToRemove)
          this.analyticsConfig.dataPoints = [
            ...this.analyticsConfig.dataPoints.filter(
              (dp, idx) => dp.type === 2 || configIndexes.includes(idx)
            ),
            ...changedDataPoints.filter(
              (cdp, idx) => !alreadyAddedIndexes.includes(idx)
            ),
          ]
        }
      } else {
        this.analyticsConfig.dataPoints = changedDataPoints
      }
      this.addPredictionFields(this.analyticsConfig)
      this.showDataPointSelector = false
    },
    showChartCustomizationDialog(defaultTab) {
      this.showChartCustomization = true
      if (defaultTab) {
        this.chartCustomizationActiveTab = defaultTab
      }
    },
    hideChartCustomizationDialog() {
      this.showChartCustomization = false
    },
    setSelectedBuildings({ name, ids }) {
      this.selectedBuildingTitle = name
      this.selectedBuildings = ids
      if (
        this.reportObject &&
        this.reportObject.options &&
        this.reportObject.options.common
      ) {
        this.reportObject.options.common.buildingIds = this.selectedBuildings
      }
    },
    onPeriodChange() {
      if (!this.analyticsConfig.dataPoints.length) {
        if (this.analyticsConfig.period === 20) {
          this.analyticsConfig.dateFilter = NewDateHelper.getDatePickerObject(
            31
          )
        } else if (this.analyticsConfig.period === 12) {
          this.analyticsConfig.dateFilter = NewDateHelper.getDatePickerObject(
            28
          )
        } else if (this.analyticsConfig.period === 10) {
          this.analyticsConfig.dateFilter = NewDateHelper.getDatePickerObject(
            44
          )
        } else {
          this.analyticsConfig.dateFilter = NewDateHelper.getDatePickerObject(
            22
          )
        }
      }
    },

    onModeChange() {
      if (this.analyticsConfig.mode !== 1) {
        this.analyticsConfig.dataPoints = this.analyticsConfig.dataPoints.filter(
          dp => dp.type !== 2
        )
      }
    },

    onBaseLineChange(newBaseLine) {
      if (this.oldBaseLine && this.oldBaseLine[0] === newBaseLine[0]) {
        return
      }
      if (this.oldBaseLine && this.oldBaseLine[0] !== -1) {
        let oldBlName = this.baseLineList.find(
          bl => bl.id === this.oldBaseLine[0]
        ).name
        let dp = this.reportObject.options.dataPoints.find(
          dp => dp.baseLineName === oldBlName
        )
        if (
          !this.checkAndDeletePointsRel(this.reportObject.options, dp.alias)
        ) {
          this.$message.error(
            dp.baseLineName +
              ' has been used in derivation(s). Please delete the derivations before removing this baseline'
          )
          this.$set(this.analyticsConfig, 'baseLine', this.oldBaseLine)
          return
        }
      }
      this.oldBaseLine = [...newBaseLine]
      this.$set(this.analyticsConfig, 'baseLine', newBaseLine)
    },
  },
}
</script>
<style lang="scss">
.building-analysis-page {
  .charttype-options {
    border-bottom: 1px solid #6666660d;
    padding: 8px 13px;
  }
  .charttype-options ul.fchart-icon li {
    float: left;
    cursor: pointer;
    width: 45px;
    height: 40px;
    padding: 20px 10px 10px 10px;
  }

  .charttype-options ul li svg {
    width: 18px;
    height: 18px;
    opacity: 0.3;
  }

  .charttype-options ul li svg:hover,
  .charttype-options ul li.active svg {
    opacity: 1;
  }

  .charttype-options-select {
    padding-top: 0px;
    padding-left: 10px;
    padding-right: 10px;
  }
  .chart-category-dropdown {
    font-size: 12px;
  }
  .datefilter-name {
    padding-right: 8px;
    padding-top: 7px !important;
  }
  .chart-created-info {
    text-align: left;
    line-height: 1.5;
    color: #333333;
    font-size: 12px;
    justify-content: center;
  }
  .chart-category-dropdown input.el-input__inner {
    font-size: 12px;
  }
  .datefilter-name {
    white-space: nowrap;
    align-items: center;
    justify-content: center;
    padding-top: 10px;
    padding-right: 10px;
  }
  .charttype-options-select {
    font-size: 12px;
  }
  .building-filter {
    text-align: left;
  }
  .building-filter .filter-entry {
    display: inline-block;
    font-size: 12px;
    color: #666;
    padding-left: 10px;
  }

  .building-filter .filter-entry .q-select {
    font-size: 12px;
    margin-top: 0px;
    padding-bottom: 0px;
    margin-right: 10px;
  }

  .building-filter .filter-entry .q-select i {
    font-size: 12px;
    opacity: 0.5;
    padding-right: 4px;
  }

  .building-filter .filter-entry .q-select:before {
    height: 0px;
  }

  .building-filter .filter-entry .q-select .q-if-control[slot='after'] {
    display: none;
  }

  .fc-analysis-filter {
    padding: 20px;
  }

  .fc-analysis-filter .pull-left {
    padding-top: 4px;
  }

  .fc-analysis-filter .filter-field {
    font-size: 12px;
    margin-top: 0px;
    padding-bottom: 0px;
    margin-right: 15px;
    display: inline-block;
  }

  .fc-analysis-filter .filter-field i {
    opacity: 0.4;
    padding-right: 4px;
  }

  .fc-analysis-filter .filter-field .plholder {
    opacity: 0.5;
    font-size: 11px;
  }

  .chart-icon svg {
    width: 18px;
    height: 18px;
  }
  .chart-label {
    margin-top: -4px;
    margin-left: 6px;
  }
  .fchart-overlay {
    position: absolute;
    width: 100%;
    height: 100%;
    background: #ffffff91;
    cursor: progress;
  }
  .report-col {
    padding: 5px;
  }
  .report-col.active {
    color: red !important;
  }
  .date-icon-right,
  .date-icon-left {
    font-size: 15px;
    position: relative;
    top: 2px;
  }
  .fchart-overlay {
    position: absolute;
    width: 100%;
    height: 100%;
    background: #ffffff91;
    cursor: progress;
  }
  .fc-analysis-filter {
    box-shadow: 0px 5px 10px rgba(0, 0, 0, 0.03);
    border-bottom: 1px solid #cccccc61;
  }
  .en-dropDown {
    padding: 4px 10px;
    border: 1px solid #ccccccd6;
    border-radius: 5px;
    margin-right: 15px;
  }
  .en-dropDown .el-input .el-input__inner {
    border: none !important;
  }
  .en-dropDown .el-input__inner {
    max-width: 75%;
  }
  .en-dropDown input {
    font-size: 13px;
    color: #333;
  }
  .en-icon {
    position: relative;
    left: 30px;
    top: 5px;
  }
  .en-icon svg,
  .en-icon img {
    width: 20px;
  }
  .fc-analysis-filter .el-select-dropdown.el-popper {
    margin-left: -20px;
  }
  .an-sidebar.header {
    box-shadow: 0px 5px 10px rgba(0, 0, 0, 0.03);
  }
  .en-fchart {
    padding-left: 10px;
    padding-right: 50px;
  }
  .reading-analysi .fchart-section {
    height: calc(100vh - 150px);
    overflow: auto;
  }
  .an-spin {
    align-items: center;
    width: 80px;
    margin: 0 auto;
    height: 80px;
    margin-top: 10%;
  }
  .en-dropDown.el-cascader,
  .en-dropDown.el-cascader .el-input__icon {
    line-height: 0px;
  }
  .reading-fileds-header .en-dropDown {
    margin-right: 5px;
  }
  .reading-analysis .fc-analysis-filter {
    display: inline-flex;
    width: 100%;
  }
  .reading-analysis
    .fc-analysis-filter
    .pull-right
    input.el-input
    .el-input__inner {
    border-bottom: 0px solid #d8dce5;
    margin-left: 40px;
  }
  .date-filter-day input.el-input__inner {
    border: 0px;
    margin-left: 30px;
  }
  .fc-el-report-pop {
    padding: 0px !important;
  }
  .fc-el-btn {
    text-align: center;
    padding: 10px;
    font-size: 12px;
    align-items: center;
    text-transform: uppercase;
    padding-bottom: 15px;
    cursor: pointer;
    font-weight: 500;
    padding-top: 15px;
  }
  .el-report-cancel-btn {
    background-color: #f4f4f4;
    color: #5f5f5f;
  }
  .el-report-save-btn {
    color: white;
    background-color: #39b2c2;
  }
  .analytics-page-header-title {
    letter-spacing: 0.6px;
    color: #000000;
    font-weight: 500;
    font-size: 18px;
  }
  .analytics-page-header-filters {
    padding-top: 12px;
  }
  .analytics-page-header-filters .el-select {
    margin-right: 10px;
    margin-left: 10px;
  }
  .analytics-page-header-filters .el-select input,
  .analytics-page-header-filters .period-select input {
    font-size: 14px;
    letter-spacing: 0.5px;
    color: #333333;
    padding: 8px;
    height: 42px;
    border-radius: 3px;
    background-color: #ffffff;
    border: solid 1px #d0d9e2;
    padding-left: 13px;
    cursor: pointer;
  }
  .analytics-section-new .analytics-page-header-filters .period-select {
    width: 140px;
  }
  .analytics-sidebar-menu .inner-child {
    padding-left: 20px;
    padding-top: 12px;
    padding-bottom: 12px;
  }
  .analytics-sidebar-menu .inner-child:hover {
    background-color: #f0f7f8;
  }
  .double-inner-child {
    padding: 15px 0 15px 10px;
    font-size: 13px;
    letter-spacing: 0.6px;
    cursor: pointer;
  }
  .double-inner-child:hover {
    padding: 15px 0 15px 10px;
    background-color: #f0f7f8;
  }
  .building-na-child-content:hover {
    display: block;
  }
  .double-inner-child:hover,
  .asset-img-hov:hover {
    display: block;
    background-color: #f0f7f8;
    cursor: pointer;
  }
  .double-inner-child .field-txt {
    width: 170px;
    text-overflow: ellipsis;
    overflow: hidden;
    white-space: nowrap;
  }
  .building-na-child-content {
    cursor: pointer;
    font-size: 14px;
  }
  .building-dialog .el-dialog__header {
    padding: 0;
  }
  .title-building {
    letter-spacing: 1.1px;
    color: rgb(0, 0, 0);
    font-weight: 500;
    font-size: 14px;
    text-transform: uppercase;
    padding-left: 30px;
  }
  .bulidng-search-block {
    margin-top: 10px;
  }
  .bulidng-search-block .search-element {
    border: 0px solid #e6ecf3 !important;
    border-bottom: 1px solid #e6ecf3 !important;
    padding: 0 30px 0 !important;
    border-radius: 0px !important;
    height: 28px;
    width: 100%;
    border-top: none !important;
    border-left: none !important;
    border-right: none !important;
    font-size: 14px;
    margin: 0;
  }
  .building-dialog .el-dialog__body {
    padding: 0;
  }
  .building-dialog-body {
    height: 60vh;
    padding: 0 0 60px;
    overflow-y: scroll;
    overflow-x: hidden;
  }
  .building-dialog-footer-btn {
    width: 50%;
  }
  .building-dialog-footer {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
  }
  .building-header-dialog {
    position: sticky;
    padding: 25px 0 0;
  }
  .bulidng-search-block .el-icon-search {
    position: absolute;
    top: 64px;
    right: 29px;
    z-index: 1;
  }
  .building-graph-container {
    padding: 40px 20px 20px 20px;
    margin-bottom: 50px;
  }
  .selectedBuildingPopup {
    position: relative;
    cursor: pointer;
    display: inline-block;
  }
  .data-length-btn {
    border-radius: 3px;
    border: solid 1px #8fd2db;
    font-size: 13px;
    letter-spacing: 0.5px;
    color: #31a4b4;
    padding: 7px;
    position: relative;
    top: 1px;
  }
  .asset-deselect-dialog-body {
    padding: 15px 0 10px;
    border: solid 1px #e8e8e8;
  }
  .asset-result-txt {
    width: 100%;
    padding: 15px 30px;
    display: flex;
    justify-content: space-between;
  }
  .asset-result-txt:nth-child(odd) {
    background-color: #fbfbfb;
  }
  .asset-result-txt:nth-child(even) {
    background-color: #fff;
  }
  .building-dialog .el-dialog__header .el-dialog__headerbtn {
    display: none;
  }
  .analysis-search-input {
    width: 100%;
    height: 37px;
    border-right: none;
    border-left: none;
    border-top: 1px solid transparent;
    border-bottom: 1px solid #e8e8e8;
    padding-left: 22px;
    font-size: 16px;
    padding-right: 20px;
  }
  .blue-txt-10 {
    font-size: 10px;
    font-weight: 500;
    letter-spacing: 0.7px;
    text-align: right;
    color: #38b3c2;
    visibility: hidden;
    text-transform: uppercase;
  }
  .building-hover-actions {
    cursor: pointer;
  }
  .building-hover-actions:hover .blue-txt-10 {
    visibility: visible;
    padding-right: 20px;
  }
  .asset-grey-txt {
    font-size: 10px;
    font-weight: bold;
    letter-spacing: 1.1px;
    color: #a5acb4;
    padding-top: 20px;
    padding-left: 35px;
  }
  .assets-list-block {
    margin-top: 10px;
    cursor: pointer;
  }
  .assets-list-txt {
    font-size: 14px;
    letter-spacing: 0.5px;
    color: #333333;
    margin: 0;
    padding-left: 35px;
    padding-top: 12px;
    padding-bottom: 12px;
  }
  .assets-list-txt:hover {
    background-color: #f1f8fa;
  }
  /* .building-anlysis-active-block{
  margin-top: 30px;
} */
  .building-search-block {
    position: relative;
  }
  .building-search-block .el-icon-search {
    position: absolute;
    right: 18px;
    top: 9px;
    color: #bec9d6;
    font-size: 18px;
    vertical-align: middle;
  }
  .customize-container {
    width: 450px;
    max-width: 450px;
    box-shadow: 5px 3px 10px 0 rgba(181, 181, 181, 0.15);
    background-color: #ffffff;
    border-bottom: 1px solid transparent;
    border: 1px solid #e6e6e6;
    z-index: 1;
    transition: all ease-in-out 0.5s;
    -webkit-transition: all ease-in-out 0.5s;
    -moz-transition: all ease-in-out 0.5s;
    position: absolute;
    bottom: 0;
    left: 0px;
    z-index: 1234;
    height: 100vh;
  }
  .building-customize-container {
    top: 72px !important;
  }
  .customize-header {
    padding: 20px 20px 20px 26px;
  }
  .customize-header .customize-H {
    font-size: 12px;
    font-weight: bold;
    letter-spacing: 0.9px;
    color: #25243e;
  }
  .customize-H .el-icon-close {
    color: 000000;
    font-size: 20px;
    font-weight: 500;
    float: right;
    position: relative;
    cursor: pointer;
  }
  .customize-body .el-tabs__nav-scroll {
    padding-left: 27px;
  }
  .customize-body .el-tabs__active-bar {
    background-color: #ef508f;
    width: 21px !important;
  }
  .customize-body .el-tabs__item.is-active {
    font-size: 13px;
    font-weight: 500;
    letter-spacing: 0.4px;
    color: #25243e;
  }
  .customize-body .el-tabs__item {
    font-size: 13px;
    font-weight: 400;
    letter-spacing: 0.4px;
    color: #50506c;
  }
  .customize-body .el-tabs__nav-wrap::after {
    height: 1px;
    background-color: 1px solid #e0e0e0;
  }
  .customize-tab-body-axis {
    padding-top: 14px;
    padding-bottom: 14px;
  }
  .customize-body .el-tabs__header {
    margin: 0;
  }
  .customize-input-block .customize-label {
    font-size: 13px;
    font-weight: normal;
    letter-spacing: 0.4px;
    color: #6b7e91;
    margin: 0;
  }
  .customize-input-block .el-input .el-input__inner {
    font-size: 14px;
    letter-spacing: 0.5px;
    color: #333333;
  }
  .customize-radio-block .el-radio-button__inner {
    padding: 11px 21px;
    font-size: 14px;
    border-radius: 0;
    font-size: 14px;
    letter-spacing: 0.5px;
    color: #333333;
    font-weight: 400;
  }
  .customize-radio-block .el-radio-button:first-child .el-radio-button__inner {
    box-shadow: 0 2px 4px 0 rgba(232, 229, 229, 0.5) !important;
    border: solid 1px #e2e8ee;
  }
  .customize-radio-block
    .el-radio-button__orig-radio:checked
    + .el-radio-button__inner {
    background-color: #f1fdff;
    font-size: 14px;
    font-weight: 500;
    letter-spacing: 0.5px;
    text-align: center;
    color: #30a0af;
    box-shadow: -1px 0 0 0 #39b2c2 !important;
    border-color: #39b2c2 !important;
  }
  .customize-radio-block .el-radio-button__inner:hover {
    color: #39b2c2 !important;
  }
  .customize-radio-block
    .el-radio-button__orig-radio:checked
    + .el-radio-button__inner {
    border-left: 1px solid transparent !important;
  }
  .y-axis-container {
    margin-top: 30px;
  }
  .y-axis-container .el-tabs__nav-scroll {
    padding-left: 0;
  }
  .y-axis-container .el-tabs--card > .el-tabs__header .el-tabs__item {
    border: none;
  }
  .y-axis-container .el-tabs--card > .el-tabs__header .el-tabs__item.is-active {
    border-color: transparent;
    background-color: #ef508f;
    border-radius: 50px;
    color: #fff;
    font-weight: 600;
  }
  .y-axis-container .el-tabs--card > .el-tabs__header .el-tabs__nav,
  .y-axis-container .el-tabs--card > .el-tabs__header {
    border: none;
  }
  .y-axis-container .el-tabs__item {
    height: 35px;
    line-height: 35px;
  }
  .pl27 {
    padding-left: 27px;
  }
  .yaxis-tab-block .el-tabs__header {
    background-color: #fbfbfb;
    padding-top: 10px;
    padding-bottom: 10px;
    border-top: 1px solid #e6e6e6 !important;
    border-bottom: 1px solid #e6e6e6 !important;
    padding-left: 27px;
  }
  .customize-scroll {
    overflow-x: hidden;
    overflow-y: scroll;
    height: calc(100vh - 310px);
    padding-bottom: 50px;
  }
  .customize-scroll-hidden {
    overflow: hidden;
  }
  .customize-select .el-input--suffix {
    width: 250px;
    min-width: 250px;
  }
  .customize-select2 .el-input__inner {
    width: 180px;
    min-width: 180px;
  }
  .width68 {
    width: 68%;
    flex: 0 0 68%;
  }
  .width80 {
    width: 80%;
    flex: 0 0 80%;
  }
  .quick-search-input3 {
    transition: 0.2s linear;
    padding: 10px 40px 8px 20px !important;
    line-height: 1.8;
    width: 100%;
    margin-bottom: 5px;
    border: none !important;
    outline: none;
    background: transparent;
    border-bottom: 1px solid #6f7c87 !important;
  }
  .search-icon3 {
    width: 15px;
    fill: #6f7c87;
    height: 20px;
    top: 12px;
    left: 0;
    position: absolute;
  }
  .close-icon3 {
    width: 15px;
    fill: #6f7c87;
    height: 20px;
    position: absolute;
    right: 5px;
    top: 13px;
    cursor: pointer;
  }
  .input-search-analysis {
    width: 100%;
    margin-top: -31px;
    background: #fff;
  }
  .fc-list-search-wrapper {
    background: #fff;
  }
  .rule-list-txt {
    color: #606266;
    line-height: 2.4;
    text-align: justify;
    font-size: 14px;
    padding-left: 20px;
  }
  .rule-list-txt:hover {
    background: #fbfbfb;
  }
  .all-rule-btn {
    background: none;
    border: none;
    border-bottom: none;
    font-size: 14px;
    font-weight: 500;
    letter-spacing: 0.5px;
    color: #333333;
    cursor: pointer;
    padding: 0;
  }
  .all-rule-btn:hover,
  .all-rule-btn:focus {
    color: #333333;
    border-color: transparent;
    background-color: transparent;
  }
  .building-customize-container {
    top: 72px !important;
  }
  .all-rule-btn .el-icon-arrow-down {
    font-size: 16px;
    color: #333333;
    font-weight: bold;
    padding-left: 7px;
    position: relative;
    top: 2px;
  }
  .el-popover {
    cursor: pointer;
    padding-left: 0;
    padding-right: 0;
    padding-top: 0;
    padding-bottom: 0;
  }
  .chart-color-selection {
    width: 20px;
    height: 20px;
    background-color: #aa70ae;
  }
  .mleft0 {
    margin-left: 0;
  }
  .mleft447 {
    margin-left: 447px;
  }
  .regression-analytics-page-options {
    position: absolute;
    top: 14px;
    right: 20px;
    z-index: 100;
  }
  .selected-buildings-analytics {
    font-size: 16px;
    font-weight: 500;
    letter-spacing: 0.4px;
    color: #333333;
    padding-top: 9px;
    padding-left: 10px;
    padding-right: 10px;
  }
  .analytics-page-options-building-analysis button:hover,
  .analytics-page-options-building-analysis
    .analytics-page-options
    button:focus,
  .analytics-page-options-building-analysis .analytics-page-options button {
    border-left: none;
  }
  .analytics-page-options-building-analysis
    .analytics-page-options
    button:hover {
    border-left: none;
  }
}
.regression-analysis-chart {
  width: calc(100% - 350px);
  float: right;
  padding: 10px 20px 20px 20px !important;
  margin-bottom: 0;
}
.regression-analytics-header {
  padding: 15px 20px !important;
}
.regression-analytics-header .selected-buildings-analytics {
  padding-top: 0px !important;
}
.regression-analytics-header .selected-buildings-analytics {
  padding-left: 0px !important;
}
.regression-analysis-page-options-con
  .analytics-page-options-building-analysis {
  position: absolute;
  top: 14px;
  right: 0px;
  z-index: 100;
}
.regression-bg {
  background-color: #f7f8f9 !important;
}
.fc-btn-ico-lg-hover:hover {
  background: #39b2c2;
  border-color: #39b2c2;
  color: #fff;
}
.fc-btn-ico-lg-hover {
  font-size: 16px;
}
.fc-btn-ico-lg-hover:hover .material-icons {
  color: #fff;
}
@media screen and (min-width: 1600px) {
  .fc-energy-regression-page .fc__layout__asset_main {
    left: 0;
  }
}
</style>
