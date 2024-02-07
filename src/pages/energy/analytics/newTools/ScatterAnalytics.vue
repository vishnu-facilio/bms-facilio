<template>
  <div :key="$route.path" class="fc-energy-building-analysis">
    <div class="report-scatter-main-con">
      <div class="">
        <f-report-options
          :isActionsDisabled="isActionsDisabled"
          optionClass="analytics-page-options"
          :optionsToEnable="[5, 1, 2]"
          :reportObject="reportObject"
          :resultObject="resultObject"
          :params="reportObject ? reportObject.params : null"
          class="analytics-page-options-building-analysis fc-v1-portfolio-anlaysis"
          :savedReport.sync="analyticsConfig.savedReport"
          :pdf="false"
        ></f-report-options>
      </div>
      <div class="report-sidebar-con">
        <div
          v-if="!hideSideBar && !showChartCustomization"
          :class="{
            'new-report-sidebar fc-new-report-sidebar col-3 pT0':
              !hideSideBar && !showChartCustomization,
          }"
        >
          <div class="report-sidebar-inner">
            <div class="analytics-body p20 clearboth">
              <!-- <div class="fc-text-pink">
                {{ $t('home.dashboard.generate_chart') }}
              </div> -->
              <el-row class="pT20">
                <div class="label-txt-grey pB5">
                  {{ $t('common._common.mode') }}
                </div>
                <el-select
                  v-model="scatterType"
                  @change="onTypeChange"
                  class="fc-input-full-border2 width100"
                >
                  <el-option
                    :key="1"
                    :label="'Category'"
                    :value="1"
                  ></el-option>
                  <el-option :key="2" :label="'Single'" :value="2"></el-option>
                  <el-option
                    :key="3"
                    :label="'Multiple'"
                    :value="3"
                  ></el-option>
                </el-select>
              </el-row>
              <div v-show="scatterType === 1">
                <el-row class="pT20">
                  <div class="label-txt-grey pB5">
                    {{ $t('common._common.category') }}
                  </div>
                  <el-select
                    v-model="selectedAssetCategory"
                    filterable
                    @change="flushAssetDetails(), loadReadings()"
                    class="fc-input-full-border2 width100"
                    :loading="categoriesLoading"
                  >
                    <el-option
                      v-for="(category, idx) in assetCategoryList"
                      :key="idx"
                      :label="category.displayName"
                      :value="category.id"
                    ></el-option>
                  </el-select>
                </el-row>
                <el-row class="pT20">
                  <div class="label-txt-grey pB5">
                    {{ $t('common._common.x_axis') }}
                  </div>
                  <el-select
                    v-model="selectedxField"
                    filterable
                    class="fc-input-full-border2 width100"
                    @change="populateDataPoints()"
                    :loading="readingsLoading"
                  >
                    <el-option
                      v-for="(reading, idx) in readings"
                      :key="idx"
                      :label="reading.displayName"
                      :value="reading.fieldId"
                    ></el-option>
                  </el-select>
                </el-row>
                <el-row class="pT20">
                  <div class="label-txt-grey pB5">
                    {{ $t('common._common.y_axis') }}
                  </div>
                  <el-select
                    v-model="selectedyField"
                    filterable
                    class="fc-input-full-border2 width100"
                    @change="populateDataPoints()"
                    :loading="readingsLoading"
                  >
                    <el-option
                      v-for="(reading, idx) in readings"
                      :key="idx"
                      :label="reading.displayName"
                      :value="reading.fieldId"
                    ></el-option>
                  </el-select>
                </el-row>
                <el-row class="pT20">
                  <div class="label-txt-grey pB5">
                    {{ $t('common.products.assets') }}
                  </div>
                  <el-select
                    :disabled="!(selectedxField && selectedyField)"
                    v-model="selectedAsset"
                    @change="populateDataPoints()"
                    filterable
                    multiple
                    class="fc-input-full-border-select2 width100 fc-tag"
                    :loading="assetsLoading"
                  >
                    <el-option
                      v-for="(asset, idx) in selectedAssets"
                      :key="idx"
                      :label="asset.name"
                      :value="asset.id"
                    ></el-option>
                  </el-select>
                </el-row>
              </div>
              <div v-show="scatterType === 2"></div>
              <div v-show="scatterType === 3">
                <FScatterPointSelector
                  @resetState="resetState"
                  visibility="true"
                  :enableFloatimngIcon.sync="enableFloatimngIcon"
                  @updateDataPoints="applyscatter"
                  :analyticsConfig.sync="analyticsConfig"
                ></FScatterPointSelector>
              </div>
            </div>
            <!-- <div
                v-if="
                  reportObject
                "
              >
                <el-row>
                  <el-col :span="24" class="mT20">
                    <div class="fL">
                      <div class="label-txt-black f14 pB5">Trendline</div>
                    </div>
                  </el-col>
                </el-row>
                <el-row class="mT10">
                  <el-col :span="24">
                    <div class="flex-middle justify-content-space">
                      <div class="flex-middle">
                        <el-checkbox
                          v-model="reportObject.options.trendLine.enable"
                          class="mR10"
                          @change="openTrendLineDialog()"
                        ></el-checkbox>
                        <div class="label-txt-black fw4 pR5">Enable</div>
                      </div>
                      <div
                        class="pointer"
                        v-if="reportObject.options.trendLine.enable"
                      >
                        <div
                          class="fc-dark-blue4-12 pointer"
                          title="Edit"
                          v-tippy="{
                            placement: 'top',
                            arrow: true,
                            animation: 'shift-away',
                          }"
                          @click="openTrendLineDialog()"
                        >
                          Edit
                        </div>
                      </div>
                    </div>
                  </el-col>
                </el-row>
              </div> -->
          </div>
        </div>
        <div
          v-if="!hideSideBar && !showChartCustomization"
          class="scatter-options-block"
        >
          <div class="">
            <div
              @click="toggleSideBar()"
              class="text-center scatter-report-icon-bg"
            >
              <InlineSvg
                src="arrow-pointing-to-left2"
                iconClass="icon icon-sm vertical-middle"
              ></InlineSvg>
            </div>
          </div>
        </div>
        <div
          v-if="hideSideBar && !showChartCustomization"
          :style="{ left: cssLeft }"
          class="scatter-options-block"
        >
          <div class="">
            <div
              @click="toggleSideBar()"
              class="text-center scatter-report-icon-bg"
            >
              <InlineSvg
                src="arrow-pointing-to-left2"
                style="transform:rotate(180deg);top: 2px;"
                iconClass="icon icon-sm vertical-middle"
              ></InlineSvg>
            </div>
          </div>
        </div>
        <div
          class="report-graph-con position-relative"
          :class="{
            'new-report-graph-con': !hideSideBar || showChartCustomization,
            'new-report-graph-con-stretch':
              hideSideBar && !showChartCustomization,
            'scatter-graph-con': !hideSideBar || showChartCustomization,
            'scatter-graph-con-stretch': hideSideBar && !showChartCustomization,
          }"
          style="height: calc(100vh - 100px); overflow-y: scroll; padding-bottom: 100px;"
        >
          <div
            ref="chartSection"
            :class="[
              'self-center fchart-section building-graph-container',
              {
                'prediction-timings-container':
                  analyticsConfig.predictionTimings &&
                  analyticsConfig.predictionTimings.length > 1,
              },
            ]"
          >
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
                    iconClass="icon text-center icon-xxxxlg"
                  ></inline-svg>
                </div>
                <div class="nowo-label">
                  {{ $t('common._common.please_select_data_points_analyze') }}
                </div>
              </div>
            </div>
            <div v-else>
              <scatter-analytics-report
                ref="newAnalyticReport"
                :config.sync="analyticsConfig"
                :scatterType="scatterType"
                :showFilterBar="false"
                :baseLines="baseLineList"
                @reportLoaded="reportLoaded"
                :showTimePeriod="analyticsConfig.mode === 1 ? true : false"
                :showChartMode="true"
              >
                <template #chartCustomization>
                  <div
                    @click="showChartCustomization = !showChartCustomization"
                  >
                    <InlineSvg
                      src="svgs/chart-options"
                      class="vertical-middle fc-icon-border mL10 mR10 pointer"
                      iconClass="icon icon-sm-md"
                    ></InlineSvg>
                  </div>
                </template>
                <template #Algorithm>
                  <el-button
                    class="fc-btn-grey-border mR10 f12"
                    @click="enableTrendline()"
                  >
                    {{ $t('common.products.algorithm') }}
                  </el-button>
                </template>
              </scatter-analytics-report>
            </div>
          </div>
        </div>

        <div
          v-show="showChartCustomization"
          :class="{
            'new-chart-custom fc-new-portfolio-chart-custom': showChartCustomization,
          }"
        >
          <div
            class="scatter-customize-container building-customize-container col-3 fc-new-portfolio-cus"
            style="top:0px;"
            v-if="reportObject"
            v-show="showChartCustomization"
          >
            <div class="customize-body fc-customize-body">
              <f-chart-customization
                v-if="reportObject"
                v-show="showChartCustomization"
                :report="reportObject"
                :resultDataPoints="
                  resultObject ? resultObject.report.dataPoints : []
                "
                :config="analyticsConfig"
                :optionsToEnable="[2, 3, 4, 6]"
                :hideFooter="true"
              ></f-chart-customization>
            </div>
          </div>
        </div>
      </div>
    </div>
    <trend-line
      v-if="showTrendLine"
      :visibility.sync="showTrendLine"
      :report="reportObject"
      @onTrendLineChange="onTrendLineChange"
    ></trend-line>
  </div>
</template>
<script>
import AnalyticsMixin from 'pages/energy/analytics/mixins/AnalyticsMixin'
import ScatterAnalyticsReport from 'pages/energy/analytics/newTools/v1/ScatterAnalyticsReport'
import FScatterPointSelector from 'pages/report/components/FScatterPointSelector'
import FChartCustomization from 'newcharts/components/NewFChartCustomization'
import FReportOptions from 'pages/report/components/FReportOptions'
import NewDateHelper from '@/mixins/NewDateHelper'
import InlineSvg from '@/InlineSvg'
import NewReportSummaryHelper from 'src/pages/report/mixins/NewReportSummaryHelper'
import cloneDeep from 'lodash/cloneDeep'
import { mapState } from 'vuex'
import deepmerge from 'util/deepmerge'
import TrendLine from 'pages/report/components/TrendLine'
import { API } from '@facilio/api'

export default {
  props: ['mobileConfig'],
  mixins: [AnalyticsMixin, NewReportSummaryHelper],
  components: {
    FChartCustomization,
    FScatterPointSelector,
    FReportOptions,
    InlineSvg,
    TrendLine,
    ScatterAnalyticsReport,
  },
  data() {
    return {
      scatterType: 1,
      selectedAssetCategory: null,
      selectedxField: null,
      selectedyField: null,
      selectedAssets: [],
      selectedAsset: [],
      enableFloatimngIcon: true,
      templateCriteria: null,
      showFilter: false,
      hideSideBar: false,
      showDataPointSelector: false,
      showBuildingPointSelector: false,
      showHeatMapPointSelector: false,
      showChartCustomization: false,
      showDerivation: false,
      chartCustomizationActiveTab: 'datapoints',
      selectedBaseLineId: '',
      baseLineList: null,
      baseLineCasecaderOptions: [],
      oldBaseLine: null,
      spinnerToggle: true,
      assetCategoryMap: null,
      spacefiltersPopover: false,
      analyticsConfig: {
        name: 'Building Analysis',
        key: 'BUILDING_ANALYSIS',
        analyticsType: this.isSiteAnalysis ? 6 : 2,
        type: 'reading',
        period: 0,
        groupByTimeAggr: null,
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
        filters: {
          xCriteriaMode: 2,
        },
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
      },
      reportFilterForm: {
        floorFilter: [],
        SpaceFilter: [],
        categoryFilter: [],
        assetFilter: [],
        liveFilterField: 'none',
        liveFilterType: 'single',
      },
      selectedCategory: {
        categoryId: [],
        assetList: null,
        allAsset: null,
        categoryList: null,
        FloorList: [],
        spaceList: [],
        allBuildings: this.$store.state.buildings
          ? cloneDeep(this.$store.state.buildings)
          : null,
      },
      assetsMap: null,
      analyticsMode: 1,
      setfilter: 0,
      noChartState: false,
      reportObject: null,
      resultObject: null,
      selectedBuildings: [],
      selectedBuildingTitle: '',
      zones: null,
      dataPointSummaryList: null,
      dPSummaryList: null,
      filterToggle: false,
      showTrendLine: false,
      assetCategoryName: '',
      filterTitle: 'Assets',
      enableReadingSelection: false,
      savedPoints: [],
      reportFilterEnable: false,
      showReportFilter: false,
      readings: null,
      assetCategoryList: [],
      categoriesLoading: false,
      assetsLoading: false,
      readingsLoading: false,
    }
  },
  watch: {
    reportId: function() {
      if (this.reportId) {
        this.loadReport()
      }
    },
    filterToggle: {
      handler(newData, oldData) {
        this.toggleFilter(newData)
      },
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
  },
  created() {
    this.$store.dispatch('loadBuildings')
    this.$store.dispatch('loadAlarmSeverity')
    this.$store
      .dispatch('loadAssetCategory')
      .then(() => this.loadAssetCategories())
  },
  mounted() {
    if (this.isSiteAnalysis) {
      this.analyticsConfig.analyticsType = 6
    }
    if (this.$route.query.filters) {
      let filters = this.$route.query.filters
      let filtersJSON = JSON.parse(filters)
      this.analyticsConfig = deepmerge.objectAssignDeep(
        this.analyticsConfig,
        filtersJSON
      )
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
    ...mapState({
      assetCategory: state => state.assetCategory,
    }),

    isFilterActive() {
      let self = this
      if (this.reportObject) {
        let isFilterActive = self.checkFilterFeasibility(self.reportObject)
        if (isFilterActive) {
          self.filterTitle =
            self.analyticsConfig.template &&
            self.analyticsConfig.template.criteria
              ? 'Specific ' + self.assetCategoryName + ' Selected'
              : 'All ' + self.assetCategoryName
        }
        return isFilterActive
      }
      return false
    },
    cssLeft() {
      return 0 + 'px !important'
    },
    isOptimize() {
      return true
    },
    show() {
      if (this.resultObject && this.resultObject.report.dataPoints.length > 1) {
        if (this.analyticsConfig.sorting.sortByField) {
          return true
        } else {
          return false
        }
      } else {
        return true
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
    isSameCategoryDataPoint() {
      if (this.analyticsConfig && this.analyticsConfig.dataPoints.length > 1) {
        let dp = {}
        for (let d of this.analyticsConfig.dataPoints) {
          dp[d.categoryId] = true
        }
        if (Object.keys(dp).length === 1) {
          return false
        }
      }
      return true
    },
    getAvailableGroupAggr() {
      let avail = []
      avail.push({
        name: this.$t('common.wo_report.none'),
        value: null,
        enable: true,
      })
      avail.push({
        name: this.$t('common._common.daily'),
        value: 12,
        enable: this.analyticsConfig.period === 19,
      })
      avail.push({
        name: this.$t('common._common.monthly'),
        value: 10,
        enable: [17, 18, 19].includes(this.analyticsConfig.period),
      })
      avail.push({
        name: this.$t('common.header.hour_of_day'), // 12, 1, 2, 3
        value: 19,
        enable: [12, 11, 10, 8, 25, 17, 18].includes(
          this.analyticsConfig.period
        ),
      })

      avail.push({
        name: this.$t('common.header.day_of_week'), // sun, mon
        value: 17,
        enable: [11, 10, 8, 25].includes(this.analyticsConfig.period),
      })

      avail.push({
        name: this.$t('common.header.day_of_month'), // 1,2,3
        value: 18,
        enable: [10, 8, 25].includes(this.analyticsConfig.period),
      })
      return avail
    },

    getAvailablePeriods() {
      let operationOnId = this.analyticsConfig.dateFilter.operationOnId
      let avail = []

      avail.push({
        name: this.$t('common.wo_report.high_res'),
        value: 0,
        enable: operationOnId !== 6 ? operationOnId !== 4 : true,
      })

      avail.push({
        name: this.$t('common.wo_report.hourly'),
        value: 20,
        enable: operationOnId !== 6 ? operationOnId !== 4 : true,
      })

      avail.push({
        name: this.$t('common._common.daily'),
        value: 12,
        enable: true,
      })

      avail.push({
        name: this.$t('common._common.weekly'),
        value: 11,
        enable: true,
      })

      avail.push({
        name: this.$t('common._common.monthly'),
        value: 10,
        enable: true,
      })

      avail.push({
        name: this.$t('common.products.quarterly'),
        value: 25,
        enable: operationOnId === 4 || operationOnId === 5,
      })

      avail.push({
        name: this.$t('common._common.yearly'),
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
    hasBooleanEnumPoints() {
      if (this.analyticsConfig.dataPoints.length) {
        let highResPoints = this.analyticsConfig.dataPoints.filter(dp =>
          [4, 8].includes(dp.yAxis.dataType)
        )
        if (highResPoints.length) {
          return true
        }
      }
      return false
    },
  },
  methods: {
    resetState() {
      Object.assign(this.$data, this.$options.data.apply(this))
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
    applyscatter(dataPoints) {
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
          this.reportObj = null
          this.resultObj = null
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
    toggleFilter(val) {
      if (this.analyticsConfig && this.analyticsConfig.template) {
        this.$set(this.analyticsConfig.template, 'show', val)
      } else {
        // create a new template
        let config = JSON.parse(JSON.stringify(this.analyticsConfig))
        let duplicateCase = this.getDuplicateCase(this.reportObject)
        this.prepareReportTemplate(this.resultObject, duplicateCase.case).then(
          newTemplate => {
            newTemplate.show = val
            config.template = newTemplate
            this.analyticsConfig = config
          }
        )
      }
    },
    toggleTemplateFilter(val) {
      if (this.analyticsConfig && this.analyticsConfig.template) {
        this.$nextTick(() => {
          this.analyticsConfig.template.show = val
        })
      }
    },
    setTemplateFilterCriteria(val) {
      this.templateCriteria = val
    },
    PointSelector() {
      if (
        this.analyticsConfig.mode === 1 ||
        this.analyticsConfig.mode === 11 ||
        this.analyticsConfig.mode === 'reading'
      ) {
        this.showDataPointSelector = true
      } else {
        this.showBuildingPointSelector = true
      }
    },
    addFilterCriteria() {
      let config = JSON.parse(JSON.stringify(this.analyticsConfig))
      this.showFilter = false
      config.parentId = null
      if (config.template) {
        config.template.criteria = this.templateCriteria
        this.analyticsConfig = config
      } else {
        let duplicateCase = this.getDuplicateCase(this.reportObject)
        this.prepareReportTemplate(this.resultObject, duplicateCase.case).then(
          newTemplate => {
            newTemplate.criteria = this.templateCriteria
            config.template = newTemplate
            this.analyticsConfig = config
          }
        )
      }
    },
    openFilter() {
      if (this.isFilterActive) {
        this.showFilter = true
      } else {
        this.showFilter = false
      }
    },
    toggleSideBar() {
      if (this.hideSideBar) {
        this.hideSideBar = false
        if (this.$refs['newAnalyticReport']) {
          this.$refs['newAnalyticReport'].resize()
        }
      } else {
        this.hideSideBar = true
        if (this.$refs['newAnalyticReport']) {
          this.$refs['newAnalyticReport'].resize()
        }
      }
    },
    loadSpaceList() {
      let self = this
      if (self.selectedBuildings) {
        self.selectedCategory.FloorList = []
        self.selectedCategory.spaceList = []
        self.loading = true
        for (let buildingId in self.selectedBuildings) {
          self.$http
            .get('/floor?buildingId=' + self.selectedBuildings[buildingId])
            .then(function(response) {
              self.selectedCategory.FloorList = response.data.records
            })
          self.$util
            .loadSpacesContext(4, null, [
              {
                key: 'building',
                operator: 'is',
                value: self.selectedBuildings[buildingId],
              },
              { key: 'floor', operator: 'is empty' },
            ])
            .then(response => {
              console.log(
                'spacelist',
                response,
                self.selectedCategory.spaceList
              )
              self.selectedCategory.spaceList = response.records
            })
        }
      }
    },
    loadReadings() {
      this.readingsLoading = true
      this.$util
        .loadAssetReadingFields(null, this.selectedAssetCategory)
        .then(response => {
          this.readings = response
          this.readingsLoading = false
        })
      this.loadAssets()
    },
    loadAssets() {
      this.assetsLoading = true
      this.$util
        .loadAsset({
          categoryId: this.selectedAssetCategory,
          selectFields: ['name'],
        })
        .then(response => {
          this.selectedAssets = (response.assets || []).map(asset => ({
            name: asset.name,
            id: asset.id,
          }))
          this.assetsLoading = false
        })
    },
    populateDataPoints() {
      if (
        this.selectedAsset.length &&
        this.selectedxField &&
        this.selectedyField
      ) {
        let changedDataPoints = []
        let tempConfig = []
        for (let assetId of this.selectedAsset) {
          let assetDetails = this.selectedAssets.find(
            asset => assetId === asset.id
          )
          let yPoint = {
            parentId: assetId,
            prediction: false,
            aliases: {},
            yAxis: {
              fieldId: this.selectedyField,
              aggr: 3,
            },
          }
          if (assetDetails) {
            yPoint['name'] = assetDetails.name
          }
          changedDataPoints.push(yPoint)
          let xPoint = {
            parentId: assetId,
            prediction: false,
            aliases: {},
            yAxis: {
              fieldId: this.selectedxField,
              aggr: 3,
            },
            xDataPoint: true,
          }
          changedDataPoints.push(xPoint)
          tempConfig.push({
            xAxis: [yPoint],
            yAxis: xPoint,
          })
        }
        this.analyticsConfig.dataPoints = changedDataPoints
        this.analyticsConfig['scatterConfig'] = tempConfig
      }
    },
    loadAssetCategories() {
      this.categoriesLoading = true
      API.get('/asset/getAssetCategoryWithReadings').then(({ error, data }) => {
        if (!error) {
          let categoryIds = data ? Object.keys(data) : [-1]
          this.assetCategoryList = this.assetCategory.filter(ele => {
            for (let index = 0; index < categoryIds.length; index++) {
              const element = categoryIds[index]
              if (parseInt(element) === ele.id) {
                ele.toggle = false
                return ele
              }
            }
          })
          this.categoriesLoading = false
        } else {
          let { message } = error
          this.$message.error(message)
          this.categoriesLoading = false
        }
      })
    },
    updateFilterdata(root) {
      if (root === 'category') {
        this.selectedCategory.assetList = []
        for (let i = 0; i < this.reportFilterForm.categoryFilter.length; i++) {
          this.selectedCategory.assetList = [
            ...this.selectedCategory.assetList,
            ...Object.keys(
              this.assetCategoryMap[this.reportFilterForm.categoryFilter[i]]
            ),
          ]
        }
      }
    },
    filterAssetsFromCategory(readings) {
      let self = this
      self.assetCategoryMap = readings.categoryWithAssets
      this.selectedCategory.assetList = []
      for (let i = 0; i < this.reportFilterForm.categoryFilter.length; i++) {
        this.selectedCategory.assetList = [
          ...this.selectedCategory.assetList,
          ...Object.keys(
            this.assetCategoryMap[this.reportFilterForm.categoryFilter[i]]
          ),
        ]
      }
      this.selectedCategory.allAsset = self.selectedCategory.assetList
      self.assetsMap = readings.assets
    },
    resetspaceFilter() {
      this.reportFilterForm = {
        floorFilter: [],
        SpaceFilter: [],
        categoryFilter: [],
        assetFilter: [],
        liveFilterField: 'none',
        liveFilterType: 'single',
      }
      this.applyFilter(null)
    },
    applyFilter() {
      if (
        !this.reportFilterForm.categoryFilter.length &&
        !this.reportFilterForm.assetFilter.length &&
        this.reportFilterForm.liveFilterField === 'none' &&
        this.reportFilterForm.liveFilterType === 'single'
      ) {
        this.setfilter = 0
      } else {
        this.setfilter = 1
        if (
          !this.reportFilterForm.categoryFilter.length &&
          !this.reportFilterForm.assetFilter.length
        ) {
          this.analyticsConfig.filters = {
            xCriteriaMode: 2,
            filterState: this.$helpers.cloneObject(this.reportFilterForm),
          }
        } else if (this.reportFilterForm.assetFilter.length) {
          this.analyticsConfig.filters = {
            xCriteriaMode: 3,
            parentId: this.reportFilterForm.assetFilter,
            filterState: this.$helpers.cloneObject(this.reportFilterForm),
          }
        } else {
          let spaceFilter = this.selectedBuildings
          this.analyticsConfig.filters = {
            xCriteriaMode: 4,
            assetCategory: this.reportFilterForm.categoryFilter,
            spaceId: spaceFilter,
            filterState: this.$helpers.cloneObject(this.reportFilterForm),
          }
        }
      }
      this.spacefiltersPopover = false
    },
    prepareDataPointSummary() {
      if (this.resultObject) {
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
          if (
            dataPoint.type !== 2 &&
            !dataPoint.duplicateDataPoint &&
            dataPoint.type !== 5
          ) {
            if (
              Object.keys(weatherModules).includes(dataPoint.yAxis.module.name)
            ) {
              this.addToGroup(
                dataPoint,
                weatherModules[dataPoint.yAxis.module.name],
                summaryList
              )
            } else if (dataPoint.yAxis.module.tableName === 'MV_Readings') {
              this.addToGroup(dataPoint, 'M&Vs', summaryList)
            } else {
              let self = this
              self.$http
                .post('v2/resource/getResourcesDetails', {
                  resourceId: dataPoint.metaData.parentIds,
                })
                .then(response => {
                  if (
                    response.data.result.resource[0] &&
                    response.data.result.resource[0].resourceTypeEnum ===
                      'SPACE'
                  ) {
                    if (
                      dataPoint.yAxis.module.typeEnum.toLowerCase() ===
                      'scheduled_formula'
                    ) {
                      this.addToGroup(dataPoint, 'Enpi', summaryList)
                    } else {
                      this.addToGroup(dataPoint, 'Space', summaryList)
                    }
                  } else {
                    this.addToGroup(dataPoint, 'Asset', summaryList)
                  }
                })
            }
          }
        }
        return summaryList
      }
    },
    addToGroup(dataPoint, label, summaryList) {
      let reading = {}
      reading['readingId'] = dataPoint.yAxis.fieldId
      reading['readingLabel'] = dataPoint.yAxis.field.displayName
      reading['resourceId'] = parseInt(dataPoint.metaData.parentIds[0])
      reading['resourceLabel'] = dataPoint.resourceName
      reading['marked'] = null
      reading['location'] =
        label === 'Space' || label === 'Enpi'
          ? dataPoint.resourceName
          : this.selectedBuildingTitle
      reading['categoryId'] = dataPoint.assetCategoryId
      reading['buildingId'] =
        label === 'Space' || label === 'Enpi'
          ? dataPoint.metaData.parentIds[0]
          : dataPoint.buildingId

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
      if (this.noChartState === true) {
        this.noChartState = false
      }
      this.reportObject = report
      if (this.$route.query.reportId) {
        this.filterToggle = report.reportType === 4 ? true : false
      }
      this.resultObject = result
      if (
        this.analyticsConfig.savedReport ||
        this.$route.query.filters ||
        this.analyticsConfig.template
      ) {
        if (![8, 9, 10, 'mv'].includes(this.analyticsConfig.mode)) {
          this.dataPointSummaryList = this.prepareDataPointSummary()
        }
      }
      if (
        this.reportObject.reportType === 4 &&
        this.reportObject.options.reportTemplate.buildingId
      ) {
        report.options.common.buildingIds.push(
          this.reportObject.options.reportTemplate.buildingId
        )
        this.resetDataPointSelector(report.options.common.buildingIds)
      } else {
        if (report.options.common && report.options.common.buildingIds) {
          if (
            !report.options.common.buildingIds.length &&
            this.selectedBuildings
          ) {
            if (
              this.$route.query.filters ||
              this.$route.query.alarmId ||
              this.$route.query.cardId
            ) {
              this.analyticsConfig.buildingId
                ? this.reportObject.options.common.buildingIds.push(
                    this.analyticsConfig.buildingId
                  )
                : this.$route.query.buildingId
                ? this.reportObject.options.common.buildingIds.push(
                    this.$route.query.buildingId
                  )
                : (this.reportObject.options.common.buildingIds = this.selectedBuildings)
              this.resetDataPointSelector(
                this.reportObject.options.common.buildingIds
              )
            } else {
              this.reportObject.options.common.buildingIds = this.selectedBuildings
            }
          } else {
            // this.selectedBuildings = report.options.common.buildingIds
            this.resetDataPointSelector(report.options.common.buildingIds)
          }
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
      if (
        this.analyticsConfig.mode === 1 ||
        this.analyticsConfig.mode === 'reading' ||
        this.analyticsConfig.mode === 11
      ) {
        if (this.analyticsMode === 'reading') {
          let filteredPoints = this.analyticsConfig.dataPoints.filter(
            dp => !(dp.xDataPoint && dp.xDataPoint === true)
          )
          this.$refs['building-selector'].setInitialValues(
            filteredPoints,
            buildingIds
          )
        } else {
          this.$refs['building-selector'].setInitialValues(
            this.analyticsConfig.dataPoints,
            buildingIds
          )
        }
      } else if ([8, 9, 10, 'mv'].includes(this.analyticsConfig.mode)) {
        this.$refs['portfolio-building-selector'].setInitialValues(
          this.analyticsConfig.dataPoints,
          buildingIds
        )
      }
    },
    updateDataPoints(changedDataPoints) {
      if ([8, 9, 10].includes(this.analyticsConfig.mode)) {
        this.analyticsConfig.filters = {
          xCriteriaMode: 4,
          spaceId: this.selectedBuildings,
        }
      } else if (this.analyticsConfig.mode === 'mv') {
        let siteId = this.$store.state.buildings.filter(
          building => building.id === parseInt(this.selectedBuildings[0])
        )[0].siteId
        this.analyticsConfig.filters = {
          xCriteriaMode: 4,
          spaceId: [siteId + ''],
        }
      } else {
        this.analyticsConfig.filters = {
          xCriteriaMode: 2,
        }
      }
      let pointToAdd = null
      if (this.analyticsConfig.dataPoints.length) {
        let configIndexes = []
        let alreadyAddedIndexes = []
        let pointsToRemove = []

        this.enableReadingSelection =
          changedDataPoints.length > 1 ? true : false

        if (
          this.analyticsMode === 'reading' &&
          this.analyticsConfig.dimensionDataPoint !== null
        ) {
          pointToAdd = this.analyticsConfig.dataPoints[
            this.analyticsConfig.dimensionDataPoint
          ]
        }

        let optionPoints = []
        this.reportObject.options.dataPoints.forEach(dp => {
          if (dp.type === 'group') {
            optionPoints.push(...dp.children)
          } else {
            optionPoints.push(dp)
          }
        })

        let isValid = optionPoints
          .filter(dp => dp.pointType !== 2 && !dp.duplicateDataPoint)
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
                    this.$t(
                      'common.header.has_been_used_derivation_removing_point'
                    )
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
      // if(this.analyticsMode === 11 && this.analyticsConfig.dimensionDataPoint >= this.analyticsConfig.dataPoints.length){
      //   this.analyticsConfig.dimensionDataPoint = 0
      //   this.$refs['single-point-selector'].setInitialValues([],this.selectedBuildings)
      // }
      if (pointToAdd !== null) {
        let idx = this.analyticsConfig.dataPoints.findIndex(
          adp =>
            adp.parentId === pointToAdd.parentId &&
            adp.yAxis.fieldId === pointToAdd.yAxis.fieldId
        )
        if (idx === -1) {
          this.analyticsConfig.dataPoints.push(pointToAdd)
          this.analyticsConfig.dimensionDataPoint =
            this.analyticsConfig.dataPoints.length - 1
        }
      }
      if (this.hasBooleanEnumPoints && this.analyticsConfig.groupByTimeAggr) {
        this.analyticsConfig.groupByTimeAggr = null
      }
      this.addPredictionFields(this.analyticsConfig)
      this.showDataPointSelector = false
      this.showBuildingPointSelector = false
    },
    updateDimensionDataPoints(changedDataPoints) {
      // if (this.analyticsConfig.dimensionDataPoint!==null) {

      let index = this.analyticsConfig.dataPoints.findIndex(
        dp =>
          dp.parentId === changedDataPoints[0].parentId &&
          dp.yAxis.fieldId === changedDataPoints[0].yAxis.fieldId
      )
      if (index === -1) {
        let idx = this.analyticsConfig.dataPoints.findIndex(
          dp => dp.xDataPoint === true
        )
        changedDataPoints[0]['xDataPoint'] = true
        if (idx === -1) {
          idx = 0
          this.analyticsConfig.dataPoints.push(changedDataPoints[0])
          this.analyticsConfig.dimensionDataPoint =
            this.analyticsConfig.dataPoints.length - 1
        } else {
          this.analyticsConfig.dataPoints[idx] = changedDataPoints[0]
          this.analyticsConfig.dimensionDataPoint = idx
        }
      } else {
        this.analyticsConfig.dimensionDataPoint = index
      }
      // }
      // else {
      //   changedDataPoints[0]['xDataPoint']=true
      //   this.analyticsConfig.dataPoints.push(changedDataPoints[0])
      //   this.analyticsConfig.dimensionDataPoint=this.analyticsConfig.dataPoints.length-1
      // }
      this.addPredictionFields(this.analyticsConfig)
      this.showHeatMapPointSelector = false
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
      if (
        ![8, 10, 11, 12, 17, 18, 19, 25].includes(this.analyticsConfig.period)
      ) {
        this.analyticsConfig.groupByTimeAggr = null
      }
      if (this.analyticsConfig.groupByTimeAggr) {
        if (
          [17, 18].includes(this.analyticsConfig.period) &&
          ![10, 19].includes(this.analyticsConfig.groupByTimeAggr)
        ) {
          this.analyticsConfig.groupByTimeAggr = 19
        } else if (
          this.analyticsConfig.period === 12 &&
          this.analyticsConfig.groupByTimeAggr !== 19
        ) {
          this.analyticsConfig.groupByTimeAggr = 19
        } else if (
          this.analyticsConfig.period === 11 &&
          ![19, 17].includes(this.analyticsConfig.groupByTimeAggr)
        ) {
          this.analyticsConfig.groupByTimeAggr = 17
        } else if (
          this.analyticsConfig.period === 19 &&
          ![12, 10].includes(this.analyticsConfig.groupByTimeAggr)
        ) {
          this.analyticsConfig.groupByTimeAggr = 12
        }
      }
      if (!this.analyticsConfig.dataPoints.length) {
        let filter = 22
        if (this.analyticsConfig.period === 20) {
          filter = 31
        } else if (this.analyticsConfig.period === 12) {
          filter = 28
        } else if (this.analyticsConfig.period === 10) {
          filter = 44
        }
        this.analyticsConfig.dateFilter = NewDateHelper.getDatePickerObject(
          filter
        )
      }
    },

    onTypeChange() {
      this.selectedAssetCategory = null
      this.flushAssetDetails()
    },
    flushAssetDetails() {
      this.noChartState = true
      this.reportObj = null
      this.resultObj = null
      this.analyticsConfig.dataPoints = []
      this.analyticsConfig.scatterConfig = []
      this.selectedxField = null
      this.selectedyField = null
      this.selectedAsset = []
      this.assetsLoading = false
      this.readingsLoading = false
    },

    onModeChange() {
      if (this.reportId || this.alarmId || this.cardId) {
        this.spinnerToggle = false
      }
      this.noChartState = true
      if (
        (![8, 9, 10].includes(this.analyticsConfig.mode) &&
          [8, 9, 10].includes(this.analyticsMode)) ||
        this.analyticsMode === 'mv'
      ) {
        //Asset,Floor,Space,M&V
        this.analyticsConfig.dataPoints = []
        this.resetDataPointSelector(this.selectedBuildings)
        this.analyticsConfig.dimensionDataPoint = null
      } else if (
        this.analyticsConfig.mode !== 1 &&
        this.analyticsConfig.mode !== 'reading' &&
        ![8, 9, 10].includes(this.analyticsMode)
      ) {
        //Reading,Time,TimeDuration
        this.analyticsConfig.dataPoints = []
        this.resetDataPointSelector(this.selectedBuildings)
      } else if (this.analyticsMode === 8 && this.isSameCategoryDataPoint) {
        this.$message({
          message: this.$t(
            'common.header.please_choose_single_asset_category_point'
          ),
          type: 'warning',
        })
        this.analyticsConfig.dataPoints = []
        this.resetDataPointSelector(this.selectedBuildings)
        this.analyticsConfig.dimensionDataPoint = null
      }
      if (this.analyticsMode !== 'reading') {
        this.analyticsConfig.dimensionDataPoint = null
      }
      this.analyticsConfig.mode = this.analyticsMode
      this.enableReadingSelection =
        this.analyticsConfig.dataPoints.length > 1 ? true : false
    },

    OnXdatapointChange() {
      if (
        this.analyticsConfig.mode === 'reading' &&
        this.analyticsConfig.dimensionDataPoint !== null
      ) {
        let point = this.analyticsConfig.dataPoints[
          this.analyticsConfig.dimensionDataPoint
        ]
        if (!(point.xDataPoint && point.xDataPoint === true)) {
          this.analyticsConfig.dataPoints = this.analyticsConfig.dataPoints.filter(
            dp => !(dp.xDataPoint && dp.xDataPoint === true)
          )
        }
      }
      if (
        this.analyticsConfig.mode === 11 &&
        this.analyticsConfig.dimensionDataPoint !== null
      ) {
        this.analyticsConfig.dataPoints = [
          this.analyticsConfig.dataPoints[
            this.analyticsConfig.dimensionDataPoint
          ],
        ]
      }
      this.$refs['single-point-selector'].setInitialValues(
        [],
        this.selectedBuildings
      )
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
              this.$t(
                'common.header.has_been_derivation_please_delete_derivation'
              )
          )
          this.$set(this.analyticsConfig, 'baseLine', this.oldBaseLine)
          return
        }
      }
      this.oldBaseLine = [...newBaseLine]
      this.$set(this.analyticsConfig, 'baseLine', newBaseLine)
    },
    onSettingsChange(changedOption) {
      if (changedOption === 'alarm') {
        if (
          (!this.reportObj.alarms || !this.reportObj.alarms.regions.length) &&
          this.reportObj.options.settings.alarm
        ) {
          this.initChart()
        }
      }
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
  },
}
</script>
<style lang="scss">
.fc-energy-building-analysis {
  .add-points-block .el-button--primary.is-disabled {
    background: #ffffff;
  }
}
.label-line-height {
  line-height: 21px;
}
.report-scatter-main-con {
  width: 100%;
  position: relative;
  display: block;
  height: 100vh;
}
.report-scatter-main-con .new-report-sidebar {
  width: 350px;
  max-width: 350px;
  padding-top: 20px;
  height: calc(100vh - 100px);
  overflow-y: scroll;
  z-index: 1;
  border-right: 1px solid #e6e6e6;
  border-top: 1px solid transparent;
  float: left;
  padding-bottom: 210px;
  transition: margin-left 4s ease-in-out 0.9s;
  position: relative;
  background: #fff;
  padding: 20px 0;
  padding-bottom: 150px;
}
.scatter-options-block {
  border: 1px solid #eceded;
  border-radius: 4px;
  background: #fff;
  height: 20px;
  width: 20px;
  position: absolute;
  left: 330px;
  top: 10px;
  z-index: 200;
}
.scatter-report-icon-bg:hover {
  transition: 0.2s all;
  -webkit-transition: 0.2s all;
  -moz-transition: 0.2s all;
  cursor: pointer;
}
.scatter-report-icon-bg .icon #setting-2,
.scatter-report-icon-bg .icon #Shape {
  fill: #6c6a91;
}
.label-txt-grey {
  font-size: 14px;
  letter-spacing: 0.5px;
  color: #6b7e91;
}
.scatter-graph-con {
  width: calc(100% - 370px) !important;
}
.scatter-graph-con-stretch {
  width: calc(100% - 20px) !important;
}
// .report-scatter-main-con g.bb-chart {
//   shape-rendering: crispEdges;
//   stroke: #d2d6df !important;
//   stroke-width: 1 !important;
// }
.scatter-customize-container {
  width: 350px;
  max-width: 350px;
  box-shadow: 5px 3px 10px 0 rgba(181, 181, 181, 0.15);
  background-color: #ffffff;
  border-bottom: 1px solid transparent;
  border: 1px solid #e6e6e6;
  z-index: 1;
  transition: all ease-in-out 0.5s;
  position: absolute;
  bottom: 0;
  right: 0px;
  z-index: 1234;
  height: 100vh;
}
</style>
