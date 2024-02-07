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
            <div class="analytics-header">
              <div class="add-points-block" v-show="!showChartCustomization">
                <el-button-group class="mL0">
                  <el-button
                    type="primary"
                    class="report_building_add_point_text report_building_add_point_layout setup-el-btn add-points-btn-first-child pR20 pL20"
                    @click="PointSelector"
                  >
                    <i
                      class="el-icon-circle-plus-outline f20 bold pR5 vertical-sub"
                    ></i
                    >{{ $t('common.reports.add_points') }}</el-button
                  >
                  <el-tooltip
                    effect="dark"
                    v-if="
                      reportObject &&
                        [1, 'reading', 11].includes(analyticsMode) &&
                        analyticsConfig.dataPoints.length > 0
                    "
                    :content="$t('common.products.add_derivation')"
                    placement="bottom"
                  >
                    <span
                      ><el-button
                        type="primary"
                        class="add-points-btn mL20 pL12 pR12"
                        v-if="
                          reportObject &&
                            [1, 'reading', 11].includes(analyticsMode) &&
                            analyticsConfig.dataPoints.length > 0
                        "
                        @click="showDerivation = true"
                        ><img
                          src="~statics/formula/formula-grey.svg"
                          width="15px"
                          height="15px"/></el-button
                    ></span>
                  </el-tooltip>
                  <el-tooltip
                    effect="dark"
                    v-else-if="![1, 'reading', 11].includes(analyticsMode)"
                    :content="
                      $t(
                        'common.dialog.set_dimension_as_time_to_create_a_derivation'
                      )
                    "
                    placement="bottom"
                  >
                    <span
                      ><el-button
                        type="primary"
                        class="add-points-btn mL20 pL12 pR12"
                        :disabled="![1, 'reading', 11].includes(analyticsMode)"
                        @click="showDerivation = true"
                        ><img
                          src="~statics/formula/formula-grey.svg"
                          width="15px"
                          height="15px"/></el-button
                    ></span>
                  </el-tooltip>
                  <el-tooltip
                    effect="dark"
                    v-else
                    :content="
                      $t(
                        'common.dialog.add_at_least_one_datapoint_to_create_derivation'
                      )
                    "
                    placement="bottom"
                  >
                    <span
                      ><el-button
                        type="primary"
                        class="add-points-btn mL20 pL12 pR12"
                        :disabled="true"
                        @click="showDerivation = true"
                        ><img
                          src="~statics/formula/formula-grey.svg"
                          width="15px"
                          height="15px"/></el-button
                    ></span>
                  </el-tooltip>
                </el-button-group>
                <!-- <prediction-timings-bar :config="analyticsConfig" class="inline" v-if="reportObject && analyticsConfig.predictionTimings && analyticsConfig.predictionTimings.length"></prediction-timings-bar> -->
              </div>
            </div>
            <div class="analytics-body p20 clearboth">
              <div class="report_building_inputbox">
                <span style="font-weight:500;">{{
                  $t('common.reports.general_settings')
                }}</span>
              </div>
              <el-row class="mT20">
                <el-col :span="24" class="report_building_inputbox">
                  <div class="pB5">
                    {{ $t('home.reports.dimension') }}
                  </div>
                  <el-select
                    class="period-select width100 fc-input-full-border2 "
                    v-model="analyticsMode"
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
                      :label="$t('common.tabs.reading')"
                      :value="'reading'"
                    ></el-option>
                    <el-option
                      :label="$t('common._common.asset')"
                      :value="8"
                    ></el-option>
                    <el-option
                      :label="$t('common._common.floor')"
                      :value="9"
                    ></el-option>
                    <el-option
                      :label="$t('common.space_asset_chooser.space')"
                      :value="10"
                    ></el-option>
                    <el-option
                      :label="$t('common.dashboard.time_duration')"
                      :value="11"
                    ></el-option>
                    <el-option
                      :label="$t('common.header.m&v')"
                      :value="'mv'"
                    ></el-option>
                    <!-- <el-option label="Series" :value="2"></el-option>
                    <el-option label="Consolidated" :value="3" v-if="isSameDataPoint"></el-option> -->
                  </el-select>
                </el-col>
              </el-row>
              <el-row v-if="analyticsMode === 'reading'" class="mT20">
                <el-col :span="22" class="report_building_inputbox">
                  <div class="pB5">
                    {{ $t('common.tabs.reading') }}
                  </div>
                  <el-select
                    class="period-select width100 fc-input-full-border2 "
                    :disabled="
                      analyticsConfig.dataPoints.length <= 1 &&
                        enableReadingSelection
                    "
                    v-model="analyticsConfig.dimensionDataPoint"
                    :placeholder="$t('common.tabs.reading')"
                    @change="OnXdatapointChange"
                    :title="$t('common.tabs.reading')"
                    data-arrow="true"
                    v-tippy
                  >
                    <el-option
                      v-for="(datapoint, index) in analyticsConfig.dataPoints"
                      :key="index"
                      :label="datapoint.name"
                      :value="index"
                    ></el-option>
                  </el-select>
                </el-col>
                <el-col :span="2">
                  <div
                    @click="showHeatMapPointSelector = true"
                    class="pointer z-20"
                  >
                    <inline-svg
                      src="svgs/add-circled"
                      class="vertical-middle mT30 mL10"
                      iconClass="icon icon-md icon-add"
                    ></inline-svg>
                  </div>
                </el-col>
              </el-row>
              <el-row v-if="analyticsMode === 11">
                <el-col :span="24" class="mT20 report_building_inputbox">
                  <div class="pB5">
                    {{ $t('common.tabs.reading') }}
                  </div>
                  <el-select
                    class="period-select width100 fc-input-full-border2 "
                    v-model="analyticsConfig.dimensionDataPoint"
                    :placeholder="$t('common.tabs.reading')"
                    @change="OnXdatapointChange"
                    :title="$t('common.tabs.reading')"
                    data-arrow="true"
                    v-tippy
                  >
                    <el-option
                      v-for="(datapoint,
                      index) in analyticsConfig.dataPoints.filter(
                        dp => dp.yAxis.dataType === 4 || dp.yAxis.dataType === 8
                      )"
                      :key="index"
                      :label="datapoint.name"
                      :value="index"
                    ></el-option>
                  </el-select>
                </el-col>
              </el-row>
              <el-row v-if="![1, 'reading', 11].includes(analyticsConfig.mode)">
                <el-col :span="24" class="mT20">
                  <div class="fc-text-pink fw6">
                    {{ $t('common._common.sorting') }}
                  </div>
                  <div class="mT15">
                    <el-form
                      :model="analyticsConfig.sorting"
                      ref="reportSortingForm"
                      :label-position="'top'"
                    >
                      <el-row
                        v-if="
                          resultObject &&
                            resultObject.report.dataPoints.length > 1
                        "
                      >
                        <el-col :span="24">
                          <el-form-item prop="sortByField">
                            <div class="label-txt-black pB5 label-line-height">
                              {{ $t('common._common.sort_by') }}
                            </div>
                            <el-select
                              class="period-select width100 fc-input-full-border2 "
                              style="width: 90%"
                              v-model="analyticsConfig.sorting.sortByField"
                            >
                              <el-option
                                v-for="(dp, index) in resultObject.report
                                  .dataPoints"
                                :key="index"
                                :label="dp.name"
                                :value="dp.yAxis.fieldId"
                              ></el-option>
                            </el-select>
                          </el-form-item>
                        </el-col>
                      </el-row>
                      <el-row v-if="show">
                        <el-col :span="24">
                          <el-form-item prop="orderByFunc">
                            <div class="label-txt-black pB5 label-line-height">
                              {{ $t('common._common.sort_order') }}
                            </div>
                            <el-select
                              class="period-select width100 fc-input-full-border2 "
                              style="width: 90%"
                              v-model="analyticsConfig.sorting.orderByFunc"
                            >
                              <!-- <el-option label="None" :value="1"></el-option> -->
                              <el-option
                                :label="$t('common._common.ascending')"
                                :value="2"
                              ></el-option>
                              <el-option
                                :label="$t('common._common.descending')"
                                :value="3"
                              ></el-option>
                            </el-select>
                          </el-form-item>
                        </el-col>
                      </el-row>
                      <el-row v-if="show">
                        <el-col :span="24">
                          <el-form-item prop="limit">
                            <div class="label-txt-black pB5 label-line-height">
                              {{ $t('common._common.limit') }}
                            </div>
                            <el-select
                              class="period-select width100 fc-input-full-border2 "
                              style="width: 90%"
                              v-model="analyticsConfig.sorting.limit"
                            >
                              <el-option
                                :label="i"
                                :value="i"
                                v-for="i in 20"
                                :key="i"
                                v-if="i >= 3"
                              ></el-option>
                            </el-select>
                          </el-form-item>
                        </el-col>
                      </el-row>
                    </el-form>
                  </div>
                </el-col>
                <!-- <el-button slot="reference" style="font-size: 1rem;position: relative;top: 2px;left: 18%;margin-left: 10px;margin-right: 10px;float: right;" plain icon="el-icon-sort" title="Sorting" v-tippy data-arrow="true"></el-button> -->
              </el-row>
              <!-- <el-row class="mT20" v-if="[8,9,10].includes(analyticsConfig.mode)">
                  <el-col :span="24">
                    <div class="label-txt-black pB10 pB5">
                    Filters
                    </div>
                    <div class="fL">
                    <div v-if="setfilter === 1">
                    <div class="fc-black2 f14 fw-bold pB5">
                     Edit Filter
                    </div>
                    </div>
                    <div v-else>
                    <div class="fc-black2 f14 fw-bold pB5">
                      Add Filter
                    </div>
                    </div>

                  </div>
                      <div class="fR">
                        <span v-if="setfilter === 1" @click="resetspaceFilter" class="pointer label-txt-blue2 pR10">Reset</span>
                        <i @click="spacefiltersPopover = true" v-tippy title="Edit Filters" class="el-icon-edit report-edit-icon pointer"></i>
                      </div>
                  </el-col>
                </el-row> -->
              <el-row
                v-if="
                  analyticsConfig.mode === 1 ||
                    analyticsConfig.mode === 'reading'
                "
              >
                <el-col :span="24" class="mT20 report_building_inputbox">
                  <div class="pB5">
                    {{ $t('common.dashboard.time_period') }}
                  </div>
                  <el-select
                    class="period-select width100 fc-input-full-border2 "
                    v-model="analyticsConfig.period"
                    :placeholder="$t('common.wo_report.period')"
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
                </el-col>
              </el-row>
              <div class="mT25 report_building_inputbox_divider"></div>
              <div v-if="baseLineList && !isActionsDisabled" class="mT20">
                <div class="report_building_inputbox">
                  <span style="font-weight:500;">
                    {{ $t('common.reports.comparsion_indicator') }}</span
                  >
                </div>
                <el-row class="mT10" v-if="baseLineList && !isActionsDisabled">
                  <el-col :span="24" class="mT10 report_building_inputbox">
                    <div class="pB5">
                      {{ $t('common.reports.compare_with') }}
                    </div>
                    <el-cascader
                      class="period-select fc-input-full-border2 width100"
                      :placeholder="$t('common.wo_report.compare_to')"
                      :options="baseLineCasecaderOptions"
                      :value="analyticsConfig.baseLine"
                      @change="onBaseLineChange"
                      :title="$t('common.wo_report.compare_with_baseline')"
                      data-arrow="true"
                      v-tippy
                    ></el-cascader>
                  </el-col>
                </el-row>
                <el-row
                  class="mT10"
                  v-if="
                    isShowCompareIndicator &&
                      reportObject &&
                      reportObject.options &&
                      !['pie', 'donut', 'gauge'].includes(
                        reportObject.options.type
                      )
                  "
                >
                  <el-col :span="24" class="mT10 report_building_inputbox">
                    <div class="pB5">
                      {{ $t('common.reports.compare_indicator_on_value') }}
                    </div>
                    <div class="mT10">
                      <el-radio
                        @change="compareIndicatorChange"
                        v-model="compare_indicator"
                        :label="1"
                        >{{ $t('common.reports.positive') }}</el-radio
                      >
                      <el-radio
                        @change="compareIndicatorChange"
                        v-model="compare_indicator"
                        :label="2"
                        >{{ $t('common.reports.negative') }}</el-radio
                      >
                    </div>
                  </el-col>
                </el-row>
              </div>
              <el-row
                v-if="
                  [8, 10, 11, 12, 17, 18, 19, 25].includes(
                    analyticsConfig.period
                  ) &&
                    !hasBooleanEnumPoints &&
                    analyticsMode === 1
                "
              >
                <el-col :span="24" class="mT20 report_building_inputbox">
                  <div class="pB5">
                    {{ $t('common.header.group_by') }}
                  </div>
                  <el-select
                    class="period-select width100 fc-input-full-border2 "
                    v-model="analyticsConfig.groupByTimeAggr"
                    :placeholder="$t('common.wo_report.period')"
                    :title="$t('common.dashboard.time_period')"
                    data-arrow="true"
                    v-tippy
                  >
                    <el-option
                      v-for="(period, index) in getAvailableGroupAggr"
                      :key="index"
                      :label="period.name"
                      :value="period.value"
                      :disabled="!period.enable"
                    ></el-option>
                  </el-select>
                </el-col>
              </el-row>
              <!-- <el-row class="mT20">
                  <el-col :span="24">
                    <div class="label-txt-black pB10 pB5">Criteria</div>
                    <div class="fL">
                     <div class="fc-black2 f14 bold pB5">
                      Default
                      </div>
                      </div>
                      <div class="fR">
                         <i v-tippy title="Edit criteria" class="el-icon-edit report-edit-icon pointer"></i>
                      </div>
                  </el-col>
                </el-row> -->
              <div
                v-if="
                  (analyticsConfig.mode === 1 ||
                    analyticsConfig.mode === 'reading') &&
                    !hideFilterDynamic &&
                    isFilterActive
                "
              >
                <div class="mT25 report_building_inputbox_divider"></div>
                <div class="pR5">
                  <el-row v-show="isFilterActive" class="clearboth pT20">
                    <el-col :span="24">
                      <div class="fL">
                        <div class="label-txt-black f14 pB5">
                          {{ $t('common.products.user_filters') }}
                        </div>
                      </div>
                      <!-- <div class="fR">

                  </div> -->
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
                              title="Edit"
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
                  <!-- <div v-if="analyticsConfig && analyticsConfig.template">
              <el-row class="mT10">
              <el-col :span="24">
                    <div >
                        <div class="fL">
                          <div class="label-txt-black fw4">Enable / Disable</div>
                        </div>
                        <div class="fR border-left1 pL15 pointer">
                          <img src="~assets/report-configure.svg" width="14" height="14" />
                          <el-switch @change="toggleTemplateFilter" v-model="analyticsConfig.template.show"></el-switch>
                        </div>
                    </div>
              </el-col>
            </el-row>
            </div> -->
                </div>
              </div>
              <div
                v-if="
                  reportObject &&
                    (analyticsConfig.mode === 1 ||
                      analyticsConfig.mode === 'reading')
                "
              >
                <el-row>
                  <el-col :span="24" class="mT20">
                    <div class="fL">
                      <div class="label-txt-black f14 pB5">
                        {{ $t('common.products.trendline') }}
                      </div>
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
                        <div class="label-txt-black fw4 pR5">
                          {{ $t('common._common.enable') }}
                        </div>
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
                          {{ $t('common._common.edit') }}
                        </div>
                      </div>
                    </div>
                  </el-col>
                </el-row>
              </div>
              <!-- Template report filter  end -->
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
            class="customize-container building-customize-container col-3 fc-new-portfolio-cus"
            style="top:0px;"
            v-if="reportObject"
            v-show="showChartCustomization"
          >
            <div class="customize-header">
              <div class="customize-H">
                {{ $t('home.reports.chart_customization') }}
                <!-- <i class="el-icon-close" @click="hideChartCustomizationDialog"></i> -->
              </div>
            </div>
            <div class="customize-body fc-customize-body">
              <f-chart-customization
                v-if="reportObject"
                v-show="showChartCustomization"
                @close="
                  ;(showChartCustomization = false), (showgeneratedata = true)
                "
                :report="reportObject"
                :resultDataPoints="
                  resultObject ? resultObject.report.dataPoints : []
                "
                :config="analyticsConfig"
                @resetPoints="resetDataPointSelector"
              ></f-chart-customization>
            </div>
          </div>
        </div>
        <!-- CHART CUSTOMIZATION end -->
        <f-data-point-selector
          v-if="[1, 'reading', 11].includes(analyticsMode)"
          ref="building-selector"
          :savedPoints="
            (analyticsConfig.savedReport ||
              this.$route.query.filters ||
              this.analyticsConfig.template) &&
            resultObject
              ? dataPointSummaryList
              : null
          "
          :visibility.sync="showDataPointSelector"
          @selectBuildings="setSelectedBuildings"
          @hideFilter="hideFilter"
          @updateDataPoints="updateDataPoints"
          :zones="zones"
          :isSiteAnalysis="isSiteAnalysis"
          :reportObject="reportObject"
          :templateFilterToggle.sync="filterToggle"
        ></f-data-point-selector>
        <f-building-point-selector
          v-if="[8, 9, 10, 'mv'].includes(analyticsMode)"
          ref="portfolio-building-selector"
          :visibility.sync="showBuildingPointSelector"
          @selectBuildings="setSelectedBuildings"
          @updateDataPoints="updateDataPoints"
          :zones="zones"
          :isSiteAnalysis="isSiteAnalysis"
          :mode="analyticsMode"
          :disableMultipleAssetCategories="analyticsMode === 8"
        ></f-building-point-selector>
        <f-heat-map-point-selector
          v-if="analyticsMode === 'reading'"
          ref="single-point-selector"
          :visibility.sync="showHeatMapPointSelector"
          @updateDataPoints="updateDimensionDataPoints"
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
              :class="{
                'analytics-report-icon-bg-active': showChartCustomization,
              }"
              @click="reportObject ? showChartCustomizationDialog() : null"
              :disabled="
                !reportObject || analyticsConfig.dataPoints.length === 0
              "
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
              :class="{
                'analytics-report-icon-bg-active': showChartCustomization,
              }"
              @click="reportObject ? showChartCustomizationDialog() : null"
              :disabled="
                !reportObject || analyticsConfig.dataPoints.length === 0
              "
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
            'new-report-graph-con': !hideSideBar,
            'new-report-graph-con-stretch': hideSideBar,
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
                    iconClass="icon text-center icon-xxxlg"
                  ></inline-svg>
                </div>
                <div class="nowo-label">
                  {{ $t('common._common.please_select_data_points_analyze') }}
                </div>
              </div>
            </div>
            <div
              v-else-if="
                analyticsMode === 'reading' &&
                  analyticsConfig.dimensionDataPoint === null
              "
              class="text-center"
            >
              <div class="p15">
                {{ $t('common._common.please_select_reading_analyze') }}
              </div>
            </div>
            <div v-else>
              <f-new-analytic-report-optimize
                ref="newAnalyticReport"
                :config.sync="analyticsConfig"
                :baseLines="baseLineList"
                @reportLoaded="reportLoaded"
                @removeFilters="removeFilters"
                :showTimePeriod="analyticsConfig.mode === 1 ? true : false"
                :showFilterBar="true"
                :showChartMode="true"
                :noChartState="noChartState"
                v-if="isOptimize === false"
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
    <el-dialog
      :visible.sync="spacefiltersPopover"
      class="fc-dialog-center-container fc-dialog-center-container-body-p20 "
      width="30%"
      :title="$t('common._common.add_filters')"
      :lock-scroll="false"
    >
      <div class="height100 pB50">
        <el-form
          :model="reportFilterForm"
          ref="reportFilterForm"
          :label-position="'top'"
        >
          <!-- <el-row>
            <el-col :span="24">
              <el-form-item prop="assetFilter">
                <p class="grey-text2">Select Floors</p>
                <el-select filterable multiple collapse-tags v-model="reportFilterForm.floorFilter" @change="updateFilterdata('floor')" class="fc-input-full-border2 width100">
                  <el-option v-if="selectedCategory.FloorList && selectedCategory.FloorList.length" v-for="(floor, index) in selectedCategory.FloorList" :key="index" :label="floor.name" :value="floor.id"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24">
              <el-form-item prop="assetFilter">
                <p class="grey-text2">Select Spaces</p>
                <el-select filterable multiple collapse-tags v-model="reportFilterForm.spaceFilter" @change="updateFilterdata('space')" class="fc-input-full-border2 width100">
                  <el-option v-if="selectedCategory.spaceList && selectedCategory.spaceList.length" v-for="(space, index) in selectedCategory.spaceList" :key="index" :label="space.name" :value="space.id"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row> -->
          <el-row>
            <el-col :span="24">
              <el-form-item prop="assetFilter">
                <p class="grey-text2">
                  {{ $t('common._common.select_asset_category') }}
                </p>
                <el-select
                  class="fc-input-full-border2 width100 fc-tag"
                  filterable
                  multiple
                  collapse-tags
                  v-model="reportFilterForm.categoryFilter"
                  @change="updateFilterdata('category')"
                >
                  <el-option
                    v-if="
                      selectedCategory.categoryList &&
                        selectedCategory.categoryList.length
                    "
                    v-for="(category, index) in selectedCategory.categoryList"
                    :key="index"
                    :label="category.name"
                    :value="category.id"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24">
              <el-form-item prop="assetFilter">
                <p class="grey-text2">
                  {{ $t('common._common.select_assets') }}
                </p>
                <el-select
                  class="fc-input-full-border2 width100 fc-tag"
                  filterable
                  multiple
                  collapse-tags
                  v-model="reportFilterForm.assetFilter"
                >
                  <el-option
                    v-if="
                      selectedCategory.assetList &&
                        selectedCategory.assetList.length
                    "
                    v-for="(asset, index) in selectedCategory.assetList"
                    :key="index"
                    :label="assetsMap[Number(asset)]"
                    :value="Number(asset)"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24">
              <el-form-item prop="field">
                <p class="grey-text2">{{ $t('common._common.live_filter') }}</p>
                <el-select
                  v-model="reportFilterForm.liveFilterField"
                  class="fc-input-full-border2 width100"
                  :placeholder="$t('common.products._select_')"
                >
                  <el-option
                    value="none"
                    :label="$t('common.wo_report.none')"
                  ></el-option>
                  <el-option
                    value="asset"
                    :label="$t('common._common.asset')"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col
              :span="24"
              v-if="
                reportFilterForm.liveFilterField &&
                  reportFilterForm.liveFilterField !== 'none'
              "
            >
              <el-form-item prop="selectBoxType">
                <p class="grey-text2">{{ $t('common._common.type') }}</p>
                <el-select
                  v-model="reportFilterForm.liveFilterType"
                  class="fc-input-full-border2 width100"
                >
                  <el-option
                    value="single"
                    :label="$t('common.header.single_select_box')"
                  ></el-option>
                  <el-option
                    value="multi"
                    :label="$t('common.header.multi_select_box')"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>
      <div class="modal-dialog-footer">
        <el-button
          class="modal-btn-cancel"
          @click="spacefiltersPopover = false"
          >{{ $t('setup.users_management.cancel') }}</el-button
        >
        <el-button
          class="modal-btn-save"
          type="primary"
          @click="applyFilter(null)"
          >{{ $t('maintenance.pm_list.apply') }}</el-button
        >
      </div>
    </el-dialog>
    <!-- filter configuration end -->
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
import FNewAnalyticReport from 'pages/energy/analytics/newTools/v1/FNewAnalyticsReportV1'
import FNewAnalyticReportOptimize from 'pages/energy/analytics/newTools/v1/FNAROptimize'
import FDataPointSelector from 'pages/energy/analytics/components/FDataPointSelector'
import FBuildingPointSelector from 'pages/energy/analytics/components/FBuildingPointSelector'
import FHeatMapPointSelector from 'pages/report/components/FHeatMapPointSelector'
import Derivation from 'pages/energy/analytics/components/FDerivation'
import FChartCustomization from 'newcharts/components/NewFChartCustomization'
import FReportOptions from 'pages/report/components/FReportOptions'
import NewDateHelper from '@/mixins/NewDateHelper'
import InlineSvg from '@/InlineSvg'
import NewReportSummaryHelper from 'src/pages/report/mixins/NewReportSummaryHelper'
import NewCriteriaBuilder from 'src/components/NewCriteriaBuilder'
import TrendLine from 'pages/report/components/TrendLine'
import cloneDeep from 'lodash/cloneDeep'
import { isEmpty } from '@facilio/utils/validation'
import { mapState } from 'vuex'
import deepmerge from 'util/deepmerge'
import { API } from '@facilio/api'

export default {
  props: ['mobileConfig'],
  mixins: [AnalyticsMixin, NewReportSummaryHelper],
  components: {
    Derivation,
    FNewAnalyticReport,
    FNewAnalyticReportOptimize,
    FChartCustomization,
    FDataPointSelector,
    FBuildingPointSelector,
    FHeatMapPointSelector,
    FReportOptions,
    InlineSvg,
    NewCriteriaBuilder,
    TrendLine,
  },
  data() {
    return {
      templateCriteria: null,
      showFilter: false,
      hideSideBar: false,
      showDataPointSelector: false,
      showBuildingPointSelector: false,
      showHeatMapPointSelector: false,
      showChartCustomization: false,
      showgeneratedata: true,
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
        baseLine: [-1],
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
        allBuildings: null,
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
      alarmPdf: false,
      hideFilterDynamic: false,
      compare_indicator: 2,
      isShowCompareIndicator: false,
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
    compare_indicator: function() {
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
    this.$store.dispatch('loadAlarmSeverity')
    this.$store.dispatch('loadAssetCategory')
  },
  mounted() {
    if (!this.alarmPdf) {
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
    }
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
      return 3 + 'px !important'
    },
    isOptimize() {
      if (this.$route.query.newchart) {
        return true
      } else {
        return false
      }
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
    compareIndicatorChange() {
      if (this?.reportObject?.options?.tooltip) {
        this.$set(
          this.reportObject.options.tooltip,
          'compare_indicator',
          this.compare_indicator
        )
      }
    },
    hideFilter(val) {
      this.hideFilterDynamic = val
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
    loadAssetCategories(readings) {
      let self = this
      if (readings) {
        let categoryIds = Object.keys(readings.categoryWithFields)
        self.selectedCategory.categoryList = self.assetCategory.filter(ele => {
          for (let index = 0; index < categoryIds.length; index++) {
            const element = categoryIds[index]
            if (parseInt(element) === ele.id) {
              ele.toggle = false
              return ele
            }
          }
        })
        self.filterAssetsFromCategory(readings)
      }
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
    async prepareDataPointSummary() {
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
              Object.keys(weatherModules).includes(dataPoint.yAxis.module?.name)
            ) {
              this.addToGroup(
                dataPoint,
                weatherModules[dataPoint.yAxis.module.name],
                summaryList
              )
            } else if (dataPoint.yAxis.module?.tableName === 'MV_Readings') {
              this.addToGroup(dataPoint, 'M&Vs', summaryList)
            } else if (
              dataPoint.metaData.parentIds[0] ===
                parseInt(this.selectedBuildings[0]) &&
              dataPoint.yAxis.module.typeEnum.toLowerCase() ===
                'scheduled_formula'
            ) {
              this.addToGroup(dataPoint, 'Enpi', summaryList)
            } else {
              let { error, data } = await API.post(
                'v2/resource/getResourcesDetails',
                {
                  resourceId: dataPoint.metaData.parentIds,
                }
              )
              let kpiTypeEnum =
                this.$getProperty(dataPoint, 'yAxis.module.typeEnum') || null
              if (!error) {
                if (data.resource[0].resourceTypeEnum === 'SPACE') {
                  this.addToGroup(dataPoint, 'Space', summaryList)
                } else if (!isEmpty(dataPoint.kpiType)) {
                  this.addToGroup(dataPoint, 'kpi', summaryList, kpiTypeEnum)
                } else {
                  this.addToGroup(dataPoint, 'Asset', summaryList)
                }
              }
            }
          }
        }
        return summaryList
      }
    },
    async addToGroup(dataPoint, label, summaryList) {
      let reading = {}
      let dynamicKpi =
        this.$getProperty(dataPoint, 'dynamicKpi.dynamicKpi') || null
      if (!isEmpty(dynamicKpi)) {
        reading['kpiType'] = 'DYNAMIC'
        reading['readingId'] = parseInt(dynamicKpi)
      } else {
        reading['readingId'] = dataPoint.yAxis.fieldId
      }
      reading['readingLabel'] = dataPoint.yAxis.field?.displayName
      let selectedBuidlingSiteId = null
      if (
        label !== 'Asset' &&
        label !== 'Space' &&
        label !== 'Enpi' &&
        label !== 'kpi' &&
        this.selectedBuildings[0]
      ) {
        let { error, data } = await API.get('/v2/pages/building', {
          id: parseInt(this.selectedBuildings[0]),
        })
        if (!error && data?.record) {
          selectedBuidlingSiteId = data.record.siteId
        }
      }
      reading['resourceId'] =
        label === 'Asset' || label === 'kpi'
          ? parseInt(dataPoint.metaData.parentIds[0])
          : label === 'Space' || label === 'Enpi'
          ? parseInt(this.selectedBuildings[0])
          : selectedBuidlingSiteId
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
          let groupIndex = summaryList.findIndex(
            group => group.label === readingGroup[0].label
          )
          if (groupIndex != -1) {
            summaryList.splice(groupIndex, 1)
            summaryList.push(readingGroup[0])
          }
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
    async reportLoaded(report, result) {
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
          this.dataPointSummaryList = await this.prepareDataPointSummary()
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
            this.resetDataPointSelector(report.options.common.buildingIds)
          }
        }
      }
    },
    removeFilters(parentKey) {
      let filters = this.analyticsConfig?.reportFilter
      if (!isEmpty(filters.timeFilter) && parentKey === 'TimeFilter') {
        filters.timeFilter.conditions = {}
      } else if (!isEmpty(filters.dataFilter && parentKey.includes('Cri'))) {
        let criteriaId = parentKey.split('_')[1]
        let length = Object.keys(filters.dataFilter.conditions).length
        if (length > 1) {
          delete filters.dataFilter.conditions[criteriaId]
          this.$set(this.analyticsConfig.reportFilter, filters)
        } else {
          filters.dataFilter.conditions = {}
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
          this.$refs['building-selector']?.setInitialValues(
            filteredPoints,
            buildingIds
          )
        } else {
          this.$refs['building-selector']?.setInitialValues(
            this.analyticsConfig.dataPoints,
            buildingIds
          )
        }
      } else if ([8, 9, 10, 'mv'].includes(this.analyticsConfig.mode)) {
        this.$refs['portfolio-building-selector']?.setInitialValues(
          this.analyticsConfig.dataPoints,
          buildingIds
        )
      }
    },
    async updateDataPoints(changedDataPoints) {
      if ([8, 9, 10].includes(this.analyticsConfig.mode)) {
        this.analyticsConfig.filters = {
          xCriteriaMode: 4,
          spaceId: this.selectedBuildings,
        }
      } else if (this.analyticsConfig.mode === 'mv') {
        let selectedBuidlingSiteId = null
        if (
          label !== 'Asset' &&
          label !== 'Space' &&
          label !== 'Enpi' &&
          this.selectedBuildings[0]
        ) {
          let { error, data } = await API.get('/v2/pages/building', {
            id: parseInt(this.selectedBuildings[0]),
          })
          if (!error && data?.record) {
            selectedBuidlingSiteId = data.record.siteId
          }
        }

        let siteId = selectedBuidlingSiteId
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
      this.addPredictionFields(this.analyticsConfig)
      this.showHeatMapPointSelector = false
    },
    showChartCustomizationDialog(defaultTab) {
      this.hideSideBar = false
      this.showgeneratedata = false
      this.showChartCustomization = true
      if (defaultTab) {
        this.chartCustomizationActiveTab = defaultTab
      }
    },
    hideChartCustomizationDialog() {
      this.showChartCustomization = false
    },
    showgeneratedataDialog() {
      this.hideSideBar = false
      this.showChartCustomization = false
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
          message:
            'Please choose only a single asset category point to analyse across ‘asset’ as dimension.',
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
      this.$refs['single-point-selector']?.setInitialValues(
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
      if (
        !isEmpty(this.oldBaseLine) &&
        this.oldBaseLine.length > 0 &&
        this.oldBaseLine[0] !== -1
      ) {
        this.isShowCompareIndicator = true
      } else {
        this.isShowCompareIndicator = false
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
    padding: 0px 20px;
  }
}
.label-line-height {
  line-height: 21px;
}
.report_building_add_point_layout {
  display: inline-flex;
  height: 32px;
  padding: var(--spacing-container-medium, 4px)
    var(--spacing-container-large, 8px);
  justify-content: center;
  align-items: center;
  gap: var(--spacing-container-none, 0px);
  border-radius: var(--border-medium, 4px);
  // background: var(--colors-background-primary-default, #0059D6);
}
.report_building_add_point_text {
  color: var(--colors-text-inverse-default, #edf5ff);
  text-align: center;
  /* text-heading/med-14 */
  font-family: sans-serif;
  font-size: 12px;
  font-style: normal;
  font-weight: 600;
  line-height: 130%;
}
.report_building_add_point_headertext {
  color: var(--colors-icon-accent-pink, #ed2780);
  font-family: sans-serif;
  font-size: 12px;
  font-style: normal;
  font-weight: 600;
  line-height: 130%;
  text-transform: uppercase;
}
.report_building_inputbox {
  display: inline-flex;
  padding: var(--spacing-container-none, 0px);
  flex-direction: column;
  align-items: flex-start;
  gap: var(--spacing-container-medium, 4px);
}
.report_building_inputbox_divider {
  width: 350px;
  margin-left: -20px;
  height: 1px;
  background: var(--colors-border-neutral-base-subtle, #dbdbdb);
}
</style>
