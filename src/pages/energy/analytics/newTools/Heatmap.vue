<template>
  <div :key="$route.path" class="fc-energy-building-analysis">
    <div class="report-main-con">
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
          v-if="!hideSideBar"
          :class="{
            'new-report-sidebar fc-new-report-sidebar col-3 pT0': !hideSideBar,
          }"
        >
          <div class="report-sidebar-inner" v-show="showgeneratedata">
            <div class="analytics-body p20 clearboth">
              <div class="fc-text-pink">
                {{ $t('home.dashboard.generate_chart') }}
              </div>
              <el-row class="pT20">
                <el-col :span="24">
                  <div class="label-txt-black pB10">
                    {{ $t('maintenance._workorder.reading') }}
                  </div>

                  <div
                    v-if="!analyticsConfig.dataPoints.length"
                    class="fc-input-div-full-border-f14 position-relative"
                    @click="showDataPointSelector = true"
                  >
                    <span> {{ $t('common._common.select') }} </span>
                    <i
                      class=" pointer mT12 el-icon-arrow-right pull-right heatmap-right-icon"
                    ></i>
                  </div>
                  <template
                    v-for="(data, index) in analyticsConfig.dataPoints"
                    v-else
                  >
                    <el-row :key="index" class="visibility-visible-actions">
                      <el-col>
                        <div
                          class="fc-input-div-full-border-f14"
                          @click="showDataPointSelector = true"
                        >
                          <span> {{ data.name }} </span>
                          <i
                            class=" pointer mT12 el-icon-arrow-right pull-right "
                          ></i>
                        </div>
                      </el-col>
                    </el-row>
                  </template>
                </el-col>
              </el-row>
              <el-row v-if="analyticsConfig.dataPoints.length">
                <el-col :span="24" class="mT20">
                  <div class="label-txt-black pB5">
                    {{ $t('common._common.aggregation') }}
                  </div>
                  <el-select
                    class="period-select width100 fc-input-full-border2 "
                    v-model="analyticsConfig.dataPoints[0].yAxis.aggr"
                    :placeholder="$t('common._common.aggregation')"
                    :title="$t('common.header.datapoint_aggregation')"
                    data-arrow="true"
                    v-tippy
                  >
                    <el-option
                      v-for="(aggr, index) in aggregations"
                      :key="index"
                      :label="aggr.label"
                      :value="aggr.value"
                    ></el-option>
                  </el-select>
                </el-col>
              </el-row>
              <el-row v-if="analyticsConfig.mode !== 2">
                <el-col :span="24" class="mT20">
                  <div class="label-txt-black pB5">
                    {{ $t('common.dashboard.time_period') }}
                  </div>
                  <el-select
                    class="period-select width100 fc-input-full-border2 "
                    v-model="analyticsConfig.format"
                    :placeholder="$t('common.wo_report.period')"
                    :title="$t('common.dashboard.time_period')"
                    data-arrow="true"
                    v-tippy
                    @change="onPeriodChange"
                  >
                    <el-option
                      v-for="(period, index) in availablePeriods"
                      :key="index"
                      :label="period.name"
                      :value="period.format"
                      :disabled="!period.enable"
                    ></el-option>
                  </el-select>
                </el-col>
              </el-row>
              <div class="border-top5">
                <div class="pR5">
                  <el-row v-show="isFilterActive" class="clearboth pT20">
                    <el-col :span="24">
                      <div class="fL">
                        <div class="label-txt-black f14 pB5">
                          {{ $t('common.products.user_filters') }}
                        </div>
                      </div>
                    </el-col>
                  </el-row>
                  <div>
                    <el-row v-show="isFilterActive" class="mT10">
                      <el-col :span="24">
                        <div class="flex-middle justify-content-space">
                          <div class="flex-middle">
                            <el-checkbox
                              :disabled="!isFilterActive"
                              v-model="filterToggle"
                              class="mR10"
                            ></el-checkbox>
                            <div class="label-txt-black fw4 pR5">
                              {{ filterTitle }}
                            </div>
                          </div>
                          <div class="pointer">
                            <div
                              class="fc-dark-blue4-12 pointer"
                              :title="$t('common._common.edit')"
                              v-tippy="{
                                placement: 'top',
                                arrow: true,
                                animation: 'shift-away',
                              }"
                              @click="openFilter()"
                            >
                              {{
                                analyticsConfig.template === null ||
                                typeof analyticsConfig.template === 'undefined'
                                  ? 'Add'
                                  : 'Edit'
                              }}
                            </div>
                          </div>
                        </div>
                      </el-col>
                    </el-row>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <!-- CHART CUSTOMIZATION start -->
        <div
          v-if="!hideSideBar"
          :class="{
            'new-chart-custom fc-new-portfolio-chart-custom': !hideSideBar,
          }"
        >
          <div
            class="customize-container building-customize-container col-3 fc-new-portfolio-cus fc-heatmap-cus"
            style="top:0px;"
            v-if="reportObject"
            v-show="showCustomization"
          >
            <div class="customize-header pB10">
              <div class="fc-text-pink">
                {{ $t('home.reports.chart_customization') }}
                <!-- <i class="el-icon-close" @click="hideChartCustomizationDialog"></i> -->
              </div>
            </div>
            <div class="customize-body fc-customize-body pT0">
              <FHeatMapCustomization
                v-if="reportObject"
                v-show="showCustomization"
                @close=";(showCustomization = false), (showgeneratedata = true)"
                :report="reportObject"
                :heatMapOptions="heatMapOptions"
              ></FHeatMapCustomization>
            </div>
          </div>
        </div>
        <!-- CHART CUSTOMIZATION end -->
        <f-heat-map-point-selector
          ref="building-selector"
          :visibility.sync="showDataPointSelector"
          :enableFloatimngIcon.sync="enableFloatimngIcon"
          @updateDataPoints="updateDataPoints"
        ></f-heat-map-point-selector>
        <div v-if="!hideSideBar" class="analytics-options-block">
          <div class="">
            <div
              class="text-center analytics-report-icon-bg line-height50 height50"
              :class="{ 'analytics-report-icon-bg-active': showgeneratedata }"
              @click="showgeneratedataDialog()"
            >
              <InlineSvg
                src="statistics"
                iconClass="icon icon-sm-md  vertical-middle"
              ></InlineSvg>
            </div>
            <div
              class="text-center analytics-report-icon-bg line-height50 height50"
              :class="{ 'analytics-report-icon-bg-active': showCustomization }"
              @click="reportObject ? showCustomizationDialog() : null"
              :disabled="!reportObject"
            >
              <InlineSvg
                src="settings-grey"
                iconClass="icon icon-sm-md vertical-middle"
              ></InlineSvg>
            </div>
            <div
              @click="toggleSideBar()"
              class="text-center analytics-report-icon-bg line-height50 height50"
            >
              <InlineSvg
                src="arrow-pointing-to-left2"
                iconClass="icon icon-sm-md vertical-middle"
              ></InlineSvg>
            </div>
          </div>
        </div>
        <div
          v-if="hideSideBar"
          :style="{ left: cssLeft }"
          class="analytics-options-block"
        >
          <div class="">
            <div
              class="text-center analytics-report-icon-bg line-height50 height50"
              :class="{ 'analytics-report-icon-bg-active': showgeneratedata }"
              @click="showgeneratedataDialog()"
            >
              <InlineSvg
                src="statistics"
                iconClass="icon icon-sm-md  vertical-middle"
              ></InlineSvg>
            </div>
            <div
              class="text-center analytics-report-icon-bg line-height50 height50"
              :class="{ 'analytics-report-icon-bg-active': showCustomization }"
              @click="reportObject ? showCustomizationDialog() : null"
              :disabled="!reportObject"
            >
              <InlineSvg
                src="settings-grey"
                iconClass="icon icon-sm-md vertical-middle"
              ></InlineSvg>
            </div>
            <div
              @click="toggleSideBar()"
              class="text-center analytics-report-icon-bg line-height50 height50"
            >
              <InlineSvg
                src="arrow-pointing-to-left2"
                style="transform:rotate(180deg);"
                iconClass="icon icon-sm-md vertical-middle"
              ></InlineSvg>
            </div>
          </div>
        </div>
        <div
          class="report-graph-con position-relative"
          :class="{
            'new-report-graph-con-heatmap': !hideSideBar,
            'new-report-graph-con-stretch': hideSideBar,
          }"
          style="height: calc(100vh - 100px); overflow-y: scroll; padding-bottom: 100px;"
        >
          <div
            ref="chartSection"
            :class="[
              'self-center fchart-section building-graph-container heatmap-graph-container',
              {
                'prediction-timings-container':
                  analyticsConfig.predictionTimings &&
                  analyticsConfig.predictionTimings.length > 1,
              },
            ]"
          >
            <div v-if="!analyticsConfig.dataPoints.length" class="text-center">
              <div class="p15" v-if="reportId || alarmId || cardId">
                <spinner :show="true" />
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
            <div v-else>
              <f-new-analytic-report-optimize
                class="text-center"
                ref="newAnalyticReport"
                :config.sync="analyticsConfig"
                :baseLines="baseLineList"
                @reportLoaded="reportLoaded"
                :showTimePeriod="analyticsConfig.mode === 1 ? true : false"
                :showChartMode="true"
                :heatMapOptions="heatMapOptions"
                v-if="isOptimize === false"
                :chartDimensions="chartDimensions"
              ></f-new-analytic-report-optimize>
              <f-new-analytic-report
                ref="newAnalyticReport"
                :config.sync="analyticsConfig"
                :baseLines="baseLineList"
                @reportLoaded="reportLoaded"
                :showTimePeriod="analyticsConfig.mode === 1 ? true : false"
                :showChartMode="true"
                v-if="isOptimize === true"
              ></f-new-analytic-report>
            </div>
          </div>
        </div>
      </div>
    </div>
    <derivation
      v-if="showDerivation"
      :visibility.sync="showDerivation"
      :report="reportObject"
      :config="analyticsConfig"
    ></derivation>
    <!-- filter configuration start -->
    <el-dialog
      :visible.sync="showFilter"
      class="fc-dialog-center-container"
      width="50%"
      :title="$t('common.wo_report.configure_asset_filter')"
      :lock-scroll="false"
    >
      <div class="height400">
        <new-criteria-builder
          v-if="showFilter"
          class="report-criteria-buuilder"
          :exrule="
            analyticsConfig.template ? analyticsConfig.template.criteria : null
          "
          @condition="setTemplateFilterCriteria"
          :showSiteField="true"
          module="asset"
          :title="
            $t('setup.users_management.specify_rules_for_assigment_rules')
          "
        ></new-criteria-builder>
      </div>
      <div class="modal-dialog-footer">
        <el-button class="modal-btn-cancel" @click="showFilter = false">{{
          $t('common._common.close')
        }}</el-button>
        <el-button
          type="primary"
          class="modal-btn-save"
          @click="addFilterCriteria"
          >{{ $t('home.reports.add_criteria') }}</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>
<script>
import AnalyticsMixin from 'pages/energy/analytics/mixins/AnalyticsMixin'
import FNewAnalyticReport from 'pages/energy/analytics/newTools/v1/FNewAnalyticsReportV1'
import FNewAnalyticReportOptimize from 'pages/energy/analytics/newTools/v1/FNAROptimize'
import FHeatMapPointSelector from 'pages/report/components/FHeatMapPointSelector'
import Derivation from 'pages/energy/analytics/components/FDerivation'
import FHeatMapCustomization from 'newcharts/components/FHeatMapCustomization'
import FReportOptions from 'pages/report/components/FReportOptions'
import NewDateHelper from '@/mixins/NewDateHelper'
import InlineSvg from '@/InlineSvg'
import NewReportSummaryHelper from 'src/pages/report/mixins/NewReportSummaryHelper'
import NewCriteriaBuilder from 'src/components/NewCriteriaBuilder'

export default {
  props: ['mobileConfig'],
  mixins: [AnalyticsMixin, NewReportSummaryHelper],
  components: {
    Derivation,
    FNewAnalyticReport,
    FNewAnalyticReportOptimize,
    FHeatMapCustomization,
    FHeatMapPointSelector,
    FReportOptions,
    InlineSvg,
    NewCriteriaBuilder,
  },
  data() {
    return {
      hideSideBar: false,
      showFilter: false,
      enableFloatimngIcon: true,
      showDataPointSelector: false,
      showgeneratedata: true,
      showCustomization: false,
      showDerivation: false,
      selectedBaseLineId: '',
      baseLineList: null,
      baseLineCasecaderOptions: [],
      oldBaseLine: null,
      analyticsConfig: {
        name: 'Heat Map Analysis',
        key: 'HEAT_MAP_ANALYSIS',
        analyticsType: 3,
        hidechartoptions: true,
        hidecharttypechanger: true,
        type: 'reading',
        format: 'hours',
        period: 20,
        mode: 1,
        baseLine: null,
        dateFilter: NewDateHelper.getDatePickerObject(49, 30),
        chartViewOption: 0,
        dataPoints: [],
        predictionTimings: [],
        transformWorkflow: null,
      },
      chartDimensions: {
        width: 980,
        height: 800,
      },
      aggregations: [
        {
          label: this.$t('common._common.sum'),
          value: 3,
          name: this.$t('common._common.sum'),
        },
        {
          label: this.$t('common.header.avg'),
          value: 2,
          name: this.$t('common.header.avg'),
        },
        {
          label: this.$t('common._common.min'),
          value: 4,
          name: this.$t('common._common.min'),
        },
        {
          label: this.$t('common._common.max'),
          value: 5,
          name: this.$t('common._common.max'),
        },
      ],
      heatMapOptions: {
        Colors: {
          Default: ['#1d7f01', '#6cb302', '#e9f501', '#fda504', '#fb5905'],
          'Palette 1': ['#93d5ed', '#45a5f5', '#4285f4', '#2f5ec4', '#073a93'],
          'Palette 2': ['#04b9bd', '#007e80', '#004852', '#f63700', '#fb6900'],
          'Palette 3': ['#ffaf00', '#ff7300', '#ff0057', '#500083', '#0b0411'],
          'Palette 4': ['#b0f400', '#74c700', '#00696d', '#002e5b', '#2e004d'],
          'Palette 5': ['#00fff1', '#00c0d0', '#4c60a6', '#4d177c', '#4a0052'],
          'Palette 6': ['#00effc', '#00b9e7', '#357ac9', '#793fad', '#980084'],
          'Palette 7': ['#c5ff00', '#fef800', '#00daff', '#0050ff', '#1700ff'],
          'Palette 8': ['#39678a', '#27916e', '#becd70', '#d7e1b2', '#9db4a4'],
          'Palette 9': ['#eeebff', '#c0b8dd', '#9184ba', '#635198', '#341e75'],
          'Palette 10': ['#e1f6de', '#a9d0b2', '#71a986', '#38835a', '#005c2e'],
          'Palette 11': ['#ffed95', '#f0b277', '#e0775a', '#d13b3c', '#c1001e'],
          'Palette 12': ['#eaf9a8', '#b1c5a0', '#779299', '#3d5e91', '#042a89'],
          'Palette 13': ['#ffc300', '#eead09', '#de9812', '#cd821b', '#bc6c24'],
          'Palette 14': ['#58edf9', '#4bd4f5', '#3fbcf1', '#32a3ec', '#258be8'],
          'Palette 15': ['#E81A50', '#C44086', '#AE6CD8', '#5E7AD3', '#2B97F9'],
        },
        chosenColors: 'Default',
        minValue: null,
        maxValue: null,
      },
      reportObject: null,
      resultObject: null,
      selectedBuildings: [],
      selectedBuildingTitle: '',
      zones: null,
      dataPointSummaryList: null,
      filterTitle: 'Assets',
      filterToggle: false,
      templateCriteria: null,
      assetCategoryName: '',
      isFilterActive: false,
    }
  },
  watch: {
    availablePeriods: {
      handler() {
        let avail = this.availablePeriods
        let selected = avail.find(
          a => a.value === this.analyticsConfig.period && a.enable
        )
        if (!selected) {
          let filterName = this.analyticsConfig.dateFilter.operationOn

          let defaultPeriod = avail.filter(a => a.enable)[0].value
          if (filterName === 'week') {
            defaultPeriod = 20
          } else if (filterName === 'month') {
            defaultPeriod = 20
          } else if (filterName === 'year') {
            defaultPeriod = 20
          }
          this.analyticsConfig.period = defaultPeriod
        }
      },
      immediate: true,
    },
    filterToggle: {
      handler(newData) {
        this.toggleFilter(newData)
      },
    },
    'analyticsConfig.period': {
      handler(newData, oldData) {
        if (newData === 20) {
          let self = this
          this.$set(
            this.analyticsConfig,
            'dateFilter',
            NewDateHelper.getDatePickerObject(49, 30)
          )
          if (this.$refs['newAnalyticReport']) {
            this.$refs['newAnalyticReport'].forceRerender()
          }
        } else if (newData === 12) {
          let self = this
          this.$set(
            this.analyticsConfig,
            'dateFilter',
            NewDateHelper.getDatePickerObject(44)
          )
          if (this.$refs['newAnalyticReport']) {
            this.$refs['newAnalyticReport'].forceRerender()
          }
        }
      },
      immediate: true,
    },
  },
  mounted() {
    if (this.isSiteAnalysis) {
      this.analyticsConfig.analyticsType = 6
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
    cssLeft() {
      return 0 + 'px !important'
    },
    isOptimize() {
      if (this.$route.query.newchart) {
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
    availablePeriods() {
      let operationOnId = this.analyticsConfig.dateFilter.operationOnId
      let avail = []

      avail.push({
        name: this.$t('common.header.hour_of_day'),
        value: 20,
        format: 'hours',
        enable: true,
      })

      avail.push({
        name: this.$t('common.header.day_of_month'),
        value: 12,
        format: 'days',
        enable: true,
      })

      avail.push({
        name: this.$t('common.header.day_of_week'),
        value: 12,
        format: 'weeks',
        enable: true,
      })

      return avail
    },
  },
  methods: {
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
    setTemplateFilterCriteria(val) {
      this.templateCriteria = val
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
    checkFilterActive() {
      if (this.reportObject) {
        let FilterFeasibility = this.checkFilterFeasibility(this.reportObject)
        if (FilterFeasibility) {
          this.filterTitle =
            this.analyticsConfig.template &&
            this.analyticsConfig.template.criteria
              ? 'Specific ' + this.assetCategoryName + ' Selected'
              : 'All ' + this.assetCategoryName
        }
        this.isFilterActive = FilterFeasibility
      } else {
        this.isFilterActive = false
      }
    },
    toggleSideBar() {
      if (this.hideSideBar) {
        this.hideSideBar = false
        this.chartDimensions.width = 980
        this.chartDimensions.height = 800
      } else {
        this.hideSideBar = true
        this.chartDimensions.width = 1340
        this.chartDimensions.height = 800
      }
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
            } else {
              let self = this
              self.$http
                .post('v2/resource/getResourcesDetails', {
                  resourceId: dataPoint.metaData.parentIds,
                })
                .then(response => {
                  if (
                    response.data.result.resource[0].resourceTypeEnum ===
                    'SPACE'
                  ) {
                    this.addToGroup(dataPoint, 'Space', summaryList)
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
      console.log('REPORTLOADED', this.analyticsConfig)
      this.reportObject = report
      this.checkFilterActive()
      if (this.$route.query.reportId) {
        this.filterToggle = report.reportType === 4 ? true : false
      }
      this.resultObject = result
      if (this.analyticsConfig.savedReport || this.$route.query.filters) {
        this.dataPointSummaryList = this.prepareDataPointSummary()
      }
      if (
        this.reportObject.reportType === 4 &&
        this.reportObject.options.reportTemplate.buildingId
      ) {
        report.options.common.buildingIds.push(
          this.reportObject.options.reportTemplate.buildingId
        )
        this.resetDataPointSelector(report.options.common.buildingIds)
      } else if (report.options.common && report.options.common.buildingIds) {
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
      this.$refs['building-selector'].setInitialValues(
        this.analyticsConfig.dataPoints,
        buildingIds
      )
    },
    updateDataPoints(changedDataPoints) {
      if (this.analyticsConfig.dataPoints.length) {
        let configIndexes = []
        let alreadyAddedIndexes = []
        let pointsToRemove = []

        let optionPoints = []
        this.reportObject.options.dataPoints.forEach(dp => {
          if (dp.type === 'group') {
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
      console.log('analyticsconfig', this.analyticsConfig)

      this.showDataPointSelector = false
    },
    showCustomizationDialog() {
      this.hideSideBar = false
      this.showgeneratedata = false
      this.showCustomization = true
    },
    hideChartCustomizationDialog() {
      this.showCustomization = false
    },
    showgeneratedataDialog() {
      this.hideSideBar = false
      this.showCustomization = false
      this.showgeneratedata = true
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
      if (this.analyticsConfig.format === 'hours') {
        this.analyticsConfig.period = 20
      } else {
        this.analyticsConfig.period = 12
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
  },
}
</script>
<style lang="scss">
.fc-energy-building-analysis {
  .add-points-block .el-button--primary.is-disabled {
    background: #ffffff;
  }
}
.heatmap-graph-container {
  margin-left: 60px;
}
.new-report-graph-con-heatmap {
  width: calc(100% - 359px) !important;
  float: left !important;
}
.fc-heatmap-cus {
  width: 331px !important;
  max-width: 350px;
}
</style>
