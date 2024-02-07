<template>
  <div>
    <div class="overflow-scroll-flex min-width0 min-height0">
      <el-row>
        <el-col
          v-show="!hideSideBar && !showChartCustomization"
          :span="hideSideBar || showChartCustomization ? 0 : 6"
        >
          <ScatterLeftSide
            ref="leftpanel"
            @updatecategoryDataPoints="updatecategoryDataPoints"
            @flushChartState="flushChartState"
            :config.sync="analyticsConfig"
            @updateMultiModeDataPoints="updateMultiModeDataPoints"
          >
            <template #sideBarHide>
              <div class="fc-scatter-left-show-hide" @click="toggleSideBar()">
                <i class="el-icon-back"></i>
              </div>
            </template>
          </ScatterLeftSide>
        </el-col>
        <div
          v-show="hideSideBar && !showChartCustomization"
          class="fc-scatter-left-toggle"
          @click="toggleSideBar()"
        >
          <i class="el-icon-back"></i>
        </div>
        <el-col :span="hideSideBar && !showChartCustomization ? 24 : 18">
          <div v-if="!analyticsConfig.dataPoints.length" class="text-center">
            <div
              class="p15"
              v-if="(reportId || alarmId || cardId) && spinnerToggle"
            >
              <spinner :show="spinnerToggle" />
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
          <ScatterMain
            v-else
            ref="newAnalyticReport"
            :config.sync="analyticsConfig"
            :noChartState="noChartState"
            :scatterType.sync="scatterType"
            @reportLoaded="reportLoaded"
          >
            <template #chartCustomization>
              <div @click="toggleCustomization">
                <InlineSvg
                  src="svgs/chart-customisation"
                  class="vertical-middle fc-icon-border mL10 mR10 pointer"
                  :class="{
                    'customization-btn-active': showChartCustomization,
                  }"
                  iconClass="icon icon-md"
                ></InlineSvg>
              </div>
            </template>
            <template #Algorithm>
              <el-dropdown @command="switchAlgorithm">
                <el-button class="fc-btn-grey-border mR10 f12 text-uppercase">
                  {{ $t('common.products.algorithm') }}
                  <i class="el-icon-arrow-down el-icon--right"></i>
                </el-button>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item command="1">
                    <InlineSvg
                      src="svgs/chart/linear"
                      class="mR10 pointer"
                      iconClass="icon icon-md vertical-sub "
                    ></InlineSvg>
                    {{ $t('common.header.linear') }}</el-dropdown-item
                  >
                  <el-dropdown-item command="2">
                    <InlineSvg
                      src="svgs/chart/polynomial"
                      class="mR10 pointer"
                      iconClass="icon icon-md vertical-sub "
                    ></InlineSvg>
                    {{ $t('common.header.polynomial') }}</el-dropdown-item
                  >
                </el-dropdown-menu>
              </el-dropdown>
            </template>
          </ScatterMain>
        </el-col>
        <el-col
          v-show="showChartCustomization"
          :span="showChartCustomization ? 6 : 0"
        >
          <ScatterRightSide
            v-if="reportObject"
            :scatterType="scatterType"
            :report="reportObject"
          ></ScatterRightSide>
        </el-col>
      </el-row>
    </div>
    <trend-line
      v-if="showTrendLine"
      :visibility.sync="showTrendLine"
      :report="reportObject"
      @onTrendLineChange="onTrendLineChange"
      :algorithmType="algorithmType"
    ></trend-line>
    <f-report-options
      :isActionsDisabled="isActionsDisabled"
      optionClass="analytics-page-options"
      :optionsToEnable="[5, 1, 2]"
      :reportObject="reportObject"
      :resultObject="resultObject"
      :params="reportObject ? reportObject.params : null"
      class="analytics-page-options-building-analysis save-button"
      :savedReport.sync="analyticsConfig.savedReport"
      :pdf="false"
    ></f-report-options>
  </div>
</template>
<script>
import AnalyticsMixin from 'pages/energy/analytics/mixins/AnalyticsMixin'
import ScatterLeftSide from 'pages/energy/analytics/scatterAnalytics/ScatterLeftSide'
import ScatterRightSide from 'pages/energy/analytics/scatterAnalytics/ScatterRightSide'
import ScatterMain from 'pages/energy/analytics/scatterAnalytics/ScatterMain'
import NewDateHelper from '@/mixins/NewDateHelper'
import NewReportSummaryHelper from 'src/pages/report/mixins/NewReportSummaryHelper'
import TrendLine from 'pages/report/components/TrendLine'
import FReportOptions from 'pages/report/components/FReportOptions'

export default {
  data() {
    return {
      scatterType: 1,
      analyticsConfig: {
        name: 'Scatter Analysis',
        key: 'SCATTER_ANALYSIS',
        analyticsType: 8,
        type: 'reading',
        period: 0,
        mode: 1,
        baseLine: null,
        dateFilter: NewDateHelper.getDatePickerObject(22),
        chartViewOption: 0,
        dataPoints: [],
        dimensionDataPoint: null,
        defaultDate: false,
        predictionTimings: [],
        transformWorkflow: null,
        trendLineChange: 0,
        sorting: {
          sortByField: null,
          orderByFunc: 3,
          limit: 10,
        },
        reportFilter: {
          timeFilter: {
            conditions: {},
          },
          dataFilter: {
            conditions: {},
          },
        },
        hidecharttypechanger: true,
        customizeChartOptions: {
          widgetLegend: {
            show: false,
          },
        },
        customPadding: {
          top: 20,
          right: 20,
          bottom: 20,
          left: 50,
        },
        scatter: null,
      },
      spinnerToggle: true,
      reportObject: null,
      resultObject: null,
      showChartCustomization: false,
      showTrendLine: false,
      hideSideBar: false,
      noChartState: false,
      algorithmType: null,
    }
  },
  components: {
    ScatterLeftSide,
    ScatterRightSide,
    ScatterMain,
    TrendLine,
    FReportOptions,
  },
  watch: {
    reportId: function() {
      if (this.reportId) {
        this.loadReport()
      }
    },
  },
  mixins: [AnalyticsMixin, NewReportSummaryHelper],
  mounted() {
    if (this.reportId || this.alarmId || this.cardId) {
      if (this.$mobile) {
        this.loadReport(this.mobileConfig)
      } else {
        this.loadReport()
      }
    }
  },
  methods: {
    updateDataPoints(changedDataPoints) {
      if (this.analyticsConfig.dataPoints.length) {
        let configIndexes = []
        let alreadyAddedIndexes = []
        let pointsToRemove = []

        this.enableReadingSelection =
          changedDataPoints.length > 1 ? true : false

        let optionPoints = []
        if (this.reportObject) {
          this.reportObject.options.dataPoints.forEach(dp => {
            if (dp.type === 'group') {
              optionPoints.push(...dp.children)
            } else {
              optionPoints.push(dp)
            }
          })
        }

        let isValid = optionPoints
          .filter(dp => dp.pointType !== 2 && !dp.duplicateDataPoint)
          .every(dp => {
            let idx = changedDataPoints.findIndex(
              cdp =>
                parseInt(cdp.parentId) === parseInt(dp.parentId) &&
                parseInt(cdp.yAxis.fieldId) === parseInt(dp.fieldId)
            )
            if (idx === -1) {
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
          if (pointsToRemove.length) {
            this.removeOptionPoints(this.reportObject.options, pointsToRemove)
          }
          this.analyticsConfig.dataPoints = [
            ...this.analyticsConfig.dataPoints.filter(
              (dp, idx) =>
                dp.type === 2 ||
                dp.duplicateDataPoint ||
                configIndexes.includes(idx)
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
    },
    reportLoaded(report, result) {
      this.noChartState = false
      this.reportObject = report
      this.resultObject = result
    },
    switchAlgorithm(command) {
      this.algorithmType = command
      this.enableTrendline()
    },
    enableTrendline() {
      if (this.reportObject) {
        this.reportObject.options.trendLine.enable = true
      }
      this.openTrendLineDialog()
    },
    openTrendLineDialog() {
      if (this.reportObject.options.trendLine.enable) {
        this.showTrendLine = true
      } else {
        this.analyticsConfig.trendLineChange++
      }
    },
    onTrendLineChange(trendLineObj) {
      this.reportObject.options.trendLine = trendLineObj
      this.analyticsConfig.trendLineChange++
    },
    toggleSideBar() {
      this.hideSideBar = !this.hideSideBar
      if (this.$refs['newAnalyticReport']) {
        this.$refs['newAnalyticReport'].resize()
      }
    },
    flushChartState() {
      this.noChartState = true
      this.analyticsConfig.dataPoints = []
      this.analyticsConfig.scatterConfig = []
    },
    updatecategoryDataPoints(changedDataPoints, scatterConfig, properties) {
      this.scatterType = 1
      this.updateDataPoints(changedDataPoints)
      this.analyticsConfig.scatterConfig = scatterConfig
      this.analyticsConfig.scatter = properties
    },
    updateMultiModeDataPoints(dataPoints) {
      this.scatterType = 3
      if (this.checkPointStructure(dataPoints)) {
        let scatterPoints = dataPoints.scatterPoints
        console.log('scatter Points')
        console.log(dataPoints.scatterPoints)
        let config = JSON.parse(JSON.stringify(this.analyticsConfig))
        if (dataPoints.result.meta) {
          this.selectedBuildings = dataPoints.result.meta.selectedBuildingIds
          this.selectedBuildingTitle =
            dataPoints.result.meta.selectedBuildingTitle
        }
        if (dataPoints.scatterPoints.length === 0) {
          this.reportObject = null
          this.resultObject = null
          this.analyticsConfig.scatterConfig = []
          this.analyticsConfig.dataPoints = []
        } else {
          config['dataPoints'] = this.prepareOptionDataPoints(scatterPoints)
          config['scatterConfig'] = dataPoints.scatterPoints
          // config['hidechartoptions'] = true
          config['hidecharttypechanger'] = true
          this.analyticsConfig = config
        }
      }
    },
    checkPointStructure(dataPoints) {
      let scatterPoints = dataPoints.scatterPoints
      for (let rPoint of scatterPoints) {
        if (!rPoint.xAxis || !rPoint.yAxis) {
          return false
        }
      }
      return true
    },
    prepareOptionDataPoints(rConfig) {
      console.log('option dataPoints')
      console.log(rConfig)
      let dataPoints = []
      if (rConfig.length !== 0) {
        for (let point of rConfig) {
          let yAxis = false
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
                  temp['aliases'] = dp.aliases
                  temp['yAxis'] = {}
                  if (dp.name) {
                    temp['name'] = dp.name
                  }
                  temp.yAxis['fieldId'] = dp.readingId
                  temp.yAxis['aggr'] = dp.aggr
                  dataPoints.push(temp)
                }
              } else {
                let temp = {}
                temp['parentId'] =
                  typeof point[key].parentId === 'string'
                    ? parseInt(point[key].parentId)
                    : point[key].parentId
                temp['prediction'] = false
                temp['aliases'] = point[key].aliases
                temp['yAxis'] = {}
                if (point[key].name) {
                  temp['name'] = point[key].name
                }
                temp.yAxis['fieldId'] = point[key].readingId
                temp.yAxis['aggr'] = point[key].aggr
                temp['xDataPoint'] = true
                dataPoints.push(temp)
              }
            }
          }
        }
      }
      return dataPoints
    },
    toggleCustomization() {
      if (this.scatterType === 1) {
        this.showChartCustomization = !this.showChartCustomization
      }
    },
  },
}
</script>
<style lang="scss">
.customization-btn-active svg path {
  stroke: #ff3184;
}
.save-button {
  position: absolute;
  z-index: 400;
  top: 4px;
  right: 20px;
}
</style>
