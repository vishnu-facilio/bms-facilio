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
              <el-row class="mT10">
                <el-col :span="24">
                  <div class="label-txt-black pB5">
                    {{ $t('home.reports.dimension') }}
                  </div>
                  <el-select
                    class="period-select width100 fc-input-full-border2 "
                    v-model="analyticsConfig.mode"
                    @change="onModeChange"
                    :placeholder="$t('common._common.mode')"
                    :title="$t('home.reports.dimension')"
                    data-arrow="true"
                    v-tippy
                  >
                    <el-option
                      :label="$t('common.wo_report.time')"
                      :value="4"
                    ></el-option>
                    <el-option
                      :label="$t('common.products.site')"
                      :value="6"
                    ></el-option>
                    <el-option
                      :label="$t('common._common.building')"
                      :value="7"
                    ></el-option>
                    <el-option
                      :label="$t('common._common.asset')"
                      :value="8"
                    ></el-option>
                  </el-select>
                </el-col>
              </el-row>
              <el-row class="pT20">
                <div class="label-txt-black pB10">
                  {{ $t('common._common.size_based_on') }}
                </div>
                <el-col :span="sizeDataPoint ? 20 : 24">
                  <div
                    v-if="!sizeDataPoint"
                    class="fc-input-div-full-border-f14 position-relative"
                    @click="
                      ;(showSizeDataPointSelector = true),
                        (showDataPointSelector = true)
                    "
                  >
                    <span> {{ $t('common._common.select') }} </span>
                    <i
                      class=" pointer mT12 el-icon-arrow-right pull-right heatmap-right-icon"
                    ></i>
                  </div>
                  <template v-else>
                    <el-row class="visibility-visible-actions">
                      <el-col>
                        <div
                          class="fc-input-div-full-border-f14"
                          @click="
                            ;(showSizeDataPointSelector = true),
                              (showDataPointSelector = true)
                          "
                        >
                          <span> {{ sizeDataPoint.name }} </span>
                          <i
                            class="pointer mT12 el-icon-arrow-right pull-right"
                          ></i>
                        </div>
                      </el-col>
                    </el-row>
                  </template>
                </el-col>
                <el-col v-if="sizeDataPoint" :span="4" class="pL5">
                  <div class="fc-input-div-full-border pointer">
                    <el-popover
                      :disabled="!sizeDataPoint || isModuleField(sizeDataPoint)"
                      v-model="toggleSizeAggr"
                      placement="top-start"
                      width="90"
                      trigger="hover"
                      popper-class="metric-popover"
                    >
                      <div
                        :class="{
                          'selected-field-background':
                            metricAggr.value === sizeDataPoint.yAxis.aggr,
                        }"
                        class="pointer pT5 pB5 fc-label-hover label-txt-black f14 text-center"
                        @click="sizeDataPoint.yAxis.aggr = metricAggr.value"
                        v-for="(metricAggr, metricAggrIdx) in aggregations"
                        :value="metricAggr.value"
                        :key="metricAggrIdx"
                      >
                        {{ metricAggr.label }}
                      </div>
                      <img
                        :disabled="
                          !sizeDataPoint || isModuleField(sizeDataPoint)
                        "
                        src="~assets/summation_img.svg"
                        slot="reference"
                      />
                    </el-popover>
                  </div>
                </el-col>
              </el-row>
              <el-row class="pT20">
                <div class="label-txt-black pB10">
                  {{ $t('common.header.color_based_on') }}
                </div>
                <el-col :span="colorDataPoint ? 20 : 24">
                  <div
                    v-if="!colorDataPoint"
                    class="fc-input-div-full-border-f14 position-relative"
                    @click="
                      ;(showColorDataPointSelector = true),
                        (showDataPointSelector = true)
                    "
                  >
                    <span>{{ $t('common._common.select') }}</span>
                    <i
                      class="pointer mT12 el-icon-arrow-right pull-right heatmap-right-icon"
                    ></i>
                  </div>
                  <template v-else>
                    <el-row class="visibility-visible-actions">
                      <el-col>
                        <div
                          class="fc-input-div-full-border-f14"
                          @click="
                            ;(showColorDataPointSelector = true),
                              (showDataPointSelector = true)
                          "
                        >
                          <span>{{ colorDataPoint.name }}</span>
                          <i
                            class="pointer mT12 el-icon-arrow-right pull-right"
                          ></i>
                        </div>
                      </el-col>
                    </el-row>
                  </template>
                </el-col>
                <el-col v-if="colorDataPoint" :span="4" class="pL5">
                  <div class="fc-input-div-full-border pointer">
                    <el-popover
                      :disabled="
                        !colorDataPoint || isModuleField(colorDataPoint)
                      "
                      v-model="toggleColorAggr"
                      placement="top-start"
                      width="90"
                      trigger="hover"
                      popper-class="metric-popover"
                    >
                      <div
                        :class="{
                          'selected-field-background':
                            metricAggr.value === colorDataPoint.yAxis.aggr,
                        }"
                        class="pointer pT5 pB5 fc-label-hover label-txt-black f14 text-center"
                        @click="colorDataPoint.yAxis.aggr = metricAggr.value"
                        v-for="(metricAggr, metricAggrIdx) in aggregations"
                        :value="metricAggr.value"
                        :key="metricAggrIdx"
                      >
                        {{ metricAggr.label }}
                      </div>
                      <img
                        :disabled="
                          !colorDataPoint || isModuleField(colorDataPoint)
                        "
                        src="~assets/summation_img.svg"
                        slot="reference"
                      />
                    </el-popover>
                  </div>
                </el-col>
              </el-row>
              <el-row v-if="isTimeMode">
                <el-col :span="24" class="mT20">
                  <div class="label-txt-black pB5">
                    {{ $t('common.dashboard.time_period') }}
                  </div>
                  <el-select
                    class="period-select width100 fc-input-full-border2"
                    v-model="analyticsConfig.period"
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
                      :value="period.value"
                      :disabled="!period.enable"
                    ></el-option>
                  </el-select>
                </el-col>
              </el-row>
              <el-row v-if="!isTimeMode">
                <el-col :span="24" class="mT20">
                  <div class="fc-text-pink fw6">
                    {{ $t('home.reports.sorting') }}
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
                            resultObject.report.dataPoints.length > 1 &&
                            !isSameDataPoint
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
                      <el-row>
                        <el-col :span="24">
                          <el-form-item prop="orderByFunc">
                            <div class="label-txt-black pB5 label-line-height">
                              {{ $t('common._common.sort_order') }}
                            </div>
                            <el-select
                              class="period-select width100 fc-input-full-border2"
                              style="width: 90%"
                              v-model="analyticsConfig.sorting.orderByFunc"
                            >
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
                      <el-row>
                        <el-col :span="24">
                          <el-form-item prop="limit">
                            <div class="label-txt-black pB5 label-line-height">
                              {{ $t('common._common.limit') }}
                            </div>
                            <el-select
                              class="period-select width100 fc-input-full-border2"
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
              </el-row>
              <el-row class="mT20">
                <el-col :span="24">
                  <div class="label-txt-black pB10 pB5">
                    {{ $t('maintenance.pm_list.filters') }}
                  </div>
                  <div class="fL">
                    <div v-if="setfilter === 1">
                      <div class="fc-black2 f14 fw-bold pB5">
                        {{ $t('common.header.edit_filter') }}
                      </div>
                    </div>
                    <div v-else>
                      <div class="fc-black2 f14 fw-bold pB5">
                        {{ $t('common._common.add_filter') }}
                      </div>
                    </div>
                  </div>
                  <div class="fR">
                    <span
                      v-if="setfilter === 1"
                      @click="resetFilter"
                      class="pointer label-txt-blue2 pR10"
                      >{{ $t('common._common.reset') }}</span
                    >
                    <i
                      @click=";(filtersPopover = true), prepareFilterData()"
                      v-tippy
                      :title="$t('common.header.edit_filters')"
                      class="el-icon-edit report-edit-icon pointer"
                    ></i>
                  </div>
                </el-col>
              </el-row>
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
        <f-portfolio-data-point-selector
          ref="building-selector"
          :mode="analyticsConfig.mode"
          :visibility.sync="showDataPointSelector"
          :isSinglePointSelector="true"
          @selectBuildings="setSelectedBuildings"
          @updateDataPoints="updateDataPoints"
          disableMultipleAssetCategories="true"
        ></f-portfolio-data-point-selector>
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
    <el-dialog
      :visible.sync="filtersPopover"
      class="fc-dialog-center-container fc-dialog-center-container-body-p20"
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
          <el-row>
            <el-col :span="24">
              <el-form-item prop="assetFilter">
                <p class="grey-text2">
                  {{ $t('common._common.select_sites') }}
                </p>
                <FLookupFieldWrapper
                  v-model="reportFilterForm.siteFilter"
                  :field="{
                    lookupModule: { name: 'site' },
                    multiple: true,
                  }"
                  :hideWizard="true"
                  @recordSelected="updateFilterdata('site')"
                ></FLookupFieldWrapper>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24">
              <el-form-item prop="assetFilter">
                <p class="grey-text2">
                  {{ $t('common.wo_report.select_buildings') }}
                </p>
                <FLookupFieldWrapper
                  v-model="reportFilterForm.buildingFilter"
                  :field="{
                    lookupModule: { name: 'building' },
                    multiple: true,
                  }"
                  :hideWizard="true"
                  @recordSelected="updateFilterdata('building')"
                ></FLookupFieldWrapper>
              </el-form-item>
            </el-col>
          </el-row>
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
                  class="fc-input-full-border2 width100 "
                  :placeholder="$t('common.products._select_')"
                >
                  <el-option
                    value="none"
                    :label="$t('common.wo_report.none')"
                  ></el-option>
                  <el-option
                    value="site"
                    :label="$t('common.products.site')"
                  ></el-option>
                  <el-option
                    value="building"
                    :label="$t('common._common.building')"
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
                    :label="$t('common.header.single_select_box')"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>
      <div class="modal-dialog-footer">
        <el-button class="modal-btn-cancel" @click="filtersPopover = false">{{
          $t('setup.users_management.cancel')
        }}</el-button>
        <el-button
          class="modal-btn-save"
          type="primary"
          @click="applyFilter(null)"
          >{{ $t('maintenance.pm_list.apply') }}</el-button
        >
      </div>
    </el-dialog>
    <derivation
      v-if="showDerivation"
      :visibility.sync="showDerivation"
      :report="reportObject"
      :config="analyticsConfig"
    ></derivation>
  </div>
</template>
<script>
import AnalyticsMixin from 'pages/energy/analytics/mixins/AnalyticsMixin'
import FNewAnalyticReport from 'pages/energy/analytics/newTools/v1/FNewAnalyticsReportV1'
import FNewAnalyticReportOptimize from 'pages/energy/analytics/newTools/v1/FNAROptimize'
import FPortfolioDataPointSelector from 'pages/energy/analytics/components/FPortfolioDataPointSelector'
import Derivation from 'pages/energy/analytics/components/FDerivation'
import FHeatMapCustomization from 'newcharts/components/FHeatMapCustomization'
import FReportOptions from 'pages/report/components/FReportOptions'
import NewDateHelper from '@/mixins/NewDateHelper'
import InlineSvg from '@/InlineSvg'
import cloneDeep from 'lodash/cloneDeep'
import { mapState } from 'vuex'
import FLookupFieldWrapper from '@/FLookupFieldWrapper'
import { API } from '@facilio/api'

export default {
  props: ['mobileConfig'],
  mixins: [AnalyticsMixin],
  components: {
    Derivation,
    FNewAnalyticReport,
    FNewAnalyticReportOptimize,
    FHeatMapCustomization,
    FPortfolioDataPointSelector,
    FReportOptions,
    InlineSvg,
    FLookupFieldWrapper,
  },
  data() {
    return {
      hideSideBar: false,
      enableFloatimngIcon: true,
      showDataPointSelector: false,
      showColorDataPointSelector: false,
      showSizeDataPointSelector: false,
      showgeneratedata: true,
      showCustomization: false,
      showDerivation: false,
      selectedBaseLineId: '',
      baseLineList: null,
      baseLineCasecaderOptions: [],
      oldBaseLine: null,
      toggleSizeAggr: false,
      toggleColorAggr: false,
      analyticsConfig: {
        name: 'Tree Map Analysis',
        key: 'TREE_MAP_ANALYSIS',
        analyticsType: 7,
        hidechartoptions: true,
        hidecharttypechanger: true,
        type: 'reading',
        period: 12,
        mode: 7,
        baseLine: null,
        dateFilter: NewDateHelper.getDatePickerObject(49, 30),
        chartViewOption: 0,
        dataPoints: [],
        predictionTimings: [],
        transformWorkflow: null,
        filters: {
          xCriteriaMode: 5,
        },
        sorting: {
          sortByField: null,
          orderByFunc: 3,
          limit: 10,
        },
      },
      chartDimensions: {
        width: 980,
        height: 600,
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
      sizeDataPoint: null,
      colorDataPoint: null,
      heatMapOptions: {
        Colors: {
          Default: ['#5EC343', '#90E214', '#E9F600', '#FDA504', '#F47533'],
          'Palette 1': ['#40AF22', '#77C700', '#FFA000', '#F76E0C', '#E54619'],
          'Palette 2': ['#16AC99', '#037E80', '#004852', '#D25A38', '#E8721E'],
          'Palette 3': ['#70B804', '#2DA70C', '#006A6D', '#002E5B', '#2E004D'],
          'Palette 4': ['#00AAB3', '#009BC2', '#2872C7', '#623CB7', '#C84D99'],
          'Palette 5': ['#3A688A', '#27916F', '#44AE7A', '#A4B83B', '#839A08'],
          'Palette 6': ['#4BB82D', '#2DA70C', '#44AE7A', '#18898B', '#076163'],
          'Palette 7': ['#AFE7FF', '#87DBFF', '#69D2FF', '#64C2FF', '#53B4F2'],
          'Palette 8': ['#C5E3FF', '#FFB5B4', '#B0F7CF', '#C1A4FF', '#FDCEE3'],
          'Palette 9': ['#32AEF9', '#1F98E3', '#177ABD', '#1869A0', '#135582'],
          'Palette 10': ['#FFA000', '#FF7E30', '#E02564', '#500083', '#0B0310'],
          'Palette 11': ['#1CACEE', '#009BE2', '#3674FF', '#0150FF', '#1700FF'],
          'Palette 12': ['#E8E4FF', '#D8D1FD', '#BDABF6', '#AA90FB', '#987AF4'],
          'Palette 13': ['#00BBA4', '#009BC2', '#4D60A6', '#4E177D', '#4A0052'],
          'Palette 14': ['#FFF2B5', '#F7E383', '#FFA085', '#F4797B', '#DF6A6B'],
          'Palette 15': ['#F7E383', '#F4D814', '#FFC05C', '#FFAA82', '#FF9665'],
          'Palette 16': ['#DC687F', '#24C4BC', '#366CBA', '#30B089', '#0297CC'],
          'Palette 17': ['#135582', '#1869A0', '#177ABD', '#1F98E3', '#32AEF9'],
          // 'Palette 11': ['#ffed95', '#f0b277', '#e0775a', '#d13b3c', '#c1001e'],
          // 'Palette 12': ['#eaf9a8', '#b1c5a0', '#779299', '#3d5e91', '#042a89'],
          // 'Palette 13': ['#ffc300', '#eead09', '#de9812', '#cd821b', '#bc6c24'],
          // 'Palette 14': ['#58edf9', '#4bd4f5', '#3fbcf1', '#32a3ec', '#258be8'],
          // 'Palette 15': ['#E81A50', '#C44086', '#AE6CD8', '#5E7AD3', '#2B97F9'],
        },
        reversePallete: false,
        chosenColors: 'Default',
        minValue: null,
        maxValue: null,
        showGrandParent: true,
        showColorScale: true,
        showWidgetLegends: false,
        treemapTextMode: 1,
      },
      reportObject: null,
      resultObject: null,
      selectedBuildings: [],
      selectedBuildingTitle: '',
      zones: null,
      dataPointSummaryList: null,
      reportFilterForm: {
        siteFilter: [],
        buildingFilter: [],
        categoryFilter: [],
        assetFilter: [],
        liveFilterField: 'none',
        liveFilterType: 'single',
      },
      buildingList: null,
      setfilter: 0,
      filtersPopover: false,
      selectedCategory: {
        categoryId: [],
        assetList: null,
        allAsset: null,
        categoryList: null,
        allBuildings: null,
      },
    }
  },
  created() {
    this.$store.dispatch('loadAlarmSeverity')
    this.$store.dispatch('loadAssetCategory')
    this.getBuildingList()
  },
  watch: {
    availablePeriods: {
      handler(newData, oldData) {
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
    ...mapState({
      assetCategory: state => state.assetCategory,
    }),

    siteList() {
      return []
    },
    isTimeMode() {
      return this.analyticsConfig.mode === 4
    },
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
        name: this.$t('common._common.daily'),
        value: 12,
        enable: true,
      })

      avail.push({
        name: this.$t('common._common.weekly'),
        value: 11,
        enable:
          operationOnId !== 6
            ? operationOnId === 3 || operationOnId === 4
            : true,
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

      return avail
    },
  },
  methods: {
    async getBuildingList() {
      let { data, error } = await API.get('/v3/picklist/building')
      if (!error) {
        this.buildingList = cloneDeep(data.pickList)
        this.selectedCategory.allBuildings = cloneDeep(data.pickList)
      }
    },
    onModeChange() {
      this.analyticsConfig.dataPoints = []
      this.sizeDataPoint = this.colorDataPoint = null
    },
    isModuleField(dp) {
      if (dp && ['building', 'site', 'asset'].includes(dp.moduleName)) {
        return true
      }
      return false
    },
    loadReadings(categoryList, spaceList) {
      let self = this
      if (this.reportFilterForm.categoryFilter.length > 0) {
        self.$http
          .post('/asset/getReadingsForSpecificAssetCategory', {
            buildingIds: spaceList,
            categoryIds: categoryList,
          })
          .then(function(response) {
            self.filterAssetsFromCategory(response.data)
          })
      }
    },
    loadAssetCategories(buildingList, siteList) {
      let self = this
      if (siteList) {
        self.loading = true
        self.$http
          .post('/asset/getAssetCategoryWithReadings', {
            buildingIds: siteList || self.reportFilterForm.siteFilter,
          })
          .then(function(response) {
            let categoryIds = response.data ? Object.keys(response.data) : [-1]
            self.selectedCategory.categoryList = self.assetCategory.filter(
              ele => {
                for (let index = 0; index < categoryIds.length; index++) {
                  const element = categoryIds[index]
                  if (parseInt(element) === ele.id) {
                    ele.toggle = false
                    return ele
                  }
                }
              }
            )
            self.loadReadings(
              categoryIds,
              siteList || self.reportFilterForm.siteFilter
            )
          })
      } else if (self.buildingList) {
        self.loading = true
        self.$http
          .post('/asset/getAssetCategoryWithReadings', {
            buildingIds:
              buildingList ||
              self.selectedCategory.allBuildings.map(rt => rt.id),
          })
          .then(function(response) {
            let categoryIds = response.data ? Object.keys(response.data) : [-1]
            self.selectedCategory.categoryList = self.assetCategory.filter(
              ele => {
                for (let index = 0; index < categoryIds.length; index++) {
                  const element = categoryIds[index]
                  if (parseInt(element) === ele.id) {
                    ele.toggle = false
                    return ele
                  }
                }
              }
            )
            self.loadReadings(
              categoryIds,
              buildingList ||
                self.selectedCategory.allBuildings.map(rt => rt.id)
            )
          })
      } else {
        self.loading = true
        self.$http
          .get('/asset/getAssetCategoryWithReadings')
          .then(function(response) {
            let categoryIds = response.data ? Object.keys(response.data) : [-1]
            self.selectedCategory.categoryList = self.assetCategory.filter(
              ele => {
                for (let index = 0; index < categoryIds.length; index++) {
                  const element = categoryIds[index]
                  if (parseInt(element) === ele.id) {
                    ele.toggle = false
                    return ele
                  }
                }
              }
            )
            self.loadReadings(categoryIds)
          })
      }
    },
    async updateFilterdata(root) {
      if (root === 'site') {
        this.reportFilterForm.assetFilter = []
        this.reportFilterForm.buildingFilter = []
        if (
          this.reportFilterForm.siteFilter &&
          this.reportFilterForm.siteFilter.length
        ) {
          let site_ids = []
          this.reportFilterForm.siteFilter.filter(siteId => {
            if (siteId) {
              site_ids.push(siteId.toString())
            }
          })
          this.buildingList = null
          let filters = JSON.stringify({
            siteId: { operatorId: 36, value: site_ids },
          })
          let { error, data } = await API.get('/v3/picklist/building', {
            filters,
          })
          if (!error && data?.picklist) {
            this.buildingList = data.picklist
          }
          // this.buildingList =
          //   this.selectedCategory.allBuildings &&
          //   this.selectedCategory.allBuildings.length
          //     ? this.selectedCategory.allBuildings.filter(f =>
          //         this.reportFilterForm.siteFilter.includes(f.siteId)
          //       )
          //     : null
          if (this.buildingList && this.buildingList.length) {
            this.loadAssetCategories(this.buildingList.map(rt => rt.id))
          } else if (
            this.reportFilterForm.siteFilter &&
            this.reportFilterForm.siteFilter.length
          ) {
            this.loadAssetCategories(false, this.reportFilterForm.siteFilter)
          } else {
            this.loadAssetCategories()
          }
        } else {
          this.selectedCategory.assetList = this.selectedCategory.allAsset
          this.buildingList = this.selectedCategory.allBuildings
          if (this.buildingList && this.buildingList.length) {
            this.loadAssetCategories(this.buildingList.map(rt => rt.id))
          } else {
            this.loadAssetCategories()
          }
        }
      } else if (root === 'building') {
        this.reportFilterForm.assetFilter = []
        this.selectedCategory.assetList = null
        if (
          this.reportFilterForm.buildingFilter &&
          this.reportFilterForm.buildingFilter.length
        ) {
          this.loadAssetCategories(this.reportFilterForm.buildingFilter)
        } else {
          this.selectedCategory.assetList = this.selectedCategory.allAsset
          this.loadAssetCategories(this.buildingList.map(rt => rt.id))
        }
      } else if (root === 'category') {
        this.loadReadings(this.reportFilterForm.categoryFilter)
      }
    },
    filterAssetsFromCategory(readings) {
      let self = this
      self.assetCategoryMap = readings.categoryWithAssets
      this.selectedCategory.assetList = []
      for (let i = 0; i < this.reportFilterForm.categoryFilter.length; i++) {
        if (self.assetCategoryMap[this.reportFilterForm.categoryFilter[i]]) {
          this.selectedCategory.assetList = [
            ...this.selectedCategory.assetList,
            ...Object.keys(
              this.assetCategoryMap[this.reportFilterForm.categoryFilter[i]]
            ),
          ]
        }
      }
      this.selectedCategory.allAsset = self.selectedCategory.assetList
      self.assetsMap = readings.assets
    },
    prepareFilterData() {
      if (!this.selectedCategory.categoryList) {
        this.loadAssetCategories()
      }
    },
    resetFilter() {
      this.reportFilterForm = {
        siteFilter: [],
        buildingFilter: [],
        categoryFilter: [],
        assetFilter: [],
        liveFilterField: 'none',
        liveFilterType: 'single',
      }
      this.applyFilter(null)
    },
    applyFilter() {
      if (
        !this.reportFilterForm.siteFilter.length &&
        !this.reportFilterForm.buildingFilter.length &&
        !this.reportFilterForm.categoryFilter.length &&
        !this.reportFilterForm.assetFilter.length &&
        this.reportFilterForm.liveFilterField === 'none' &&
        this.reportFilterForm.liveFilterType === 'single'
      ) {
        this.setfilter = 0
        this.analyticsConfig.filters = {
          xCriteriaMode: 5,
          filterState: this.$helpers.cloneObject(this.reportFilterForm),
        }
      } else {
        this.setfilter = 1
        if (
          !this.reportFilterForm.siteFilter.length &&
          !this.reportFilterForm.buildingFilter.length &&
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
          let spaceFilter = this.reportFilterForm.buildingFilter.length
            ? this.reportFilterForm.buildingFilter
            : this.reportFilterForm.siteFilter
          this.analyticsConfig.filters = {
            xCriteriaMode: 4,
            assetCategory: this.reportFilterForm.categoryFilter,
            spaceId: spaceFilter,
            filterState: this.$helpers.cloneObject(this.reportFilterForm),
          }
        }
      }
      this.filtersPopover = false
    },
    toggleSideBar() {
      if (this.hideSideBar) {
        this.hideSideBar = false
        this.chartDimensions.width = 980
        this.chartDimensions.height = 600
      } else {
        this.hideSideBar = true
        this.chartDimensions.width = 1340
        this.chartDimensions.height = 600
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
              dataPoint.metaData.parentIds &&
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
                    response.data.result.resource &&
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
      reading['readingLabel'] = label === 'Asset' ? '' : dataPoint.name
      reading['resourceId'] =
        label === 'Asset'
          ? parseInt(dataPoint.metaData.parentIds[0])
          : label === 'Space' || label === 'Enpi'
          ? parseInt(this.selectedBuildings[0])
          : this.$store.state.buildings.filter(
              building => building.id === parseInt(this.selectedBuildings[0])
            )[0].siteId
      reading['resourceLabel'] = null
      ;(reading['marked'] = null),
        (reading['location'] = this.selectedBuildingTitle)

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
      this.resultObject = result
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
      if (this.showSizeDataPointSelector) {
        this.sizeDataPoint = changedDataPoints[0]
        if (
          this.colorDataPoint === null ||
          (this.colorDataPoint &&
            changedDataPoints[0].categoryId &&
            this.colorDataPoint.categoryId &&
            changedDataPoints[0].categoryId !== this.colorDataPoint.categoryId)
        ) {
          this.colorDataPoint = Object.assign({}, changedDataPoints[0])
        }
        this.showSizeDataPointSelector = false
      } else if (this.showColorDataPointSelector) {
        this.colorDataPoint = changedDataPoints[0]
        if (
          this.sizeDataPoint === null ||
          (this.sizeDataPoint &&
            changedDataPoints[0].categoryId &&
            this.sizeDataPoint.categoryId &&
            changedDataPoints[0].categoryId !== this.sizeDataPoint.categoryId)
        ) {
          this.sizeDataPoint = Object.assign({}, changedDataPoints[0])
        }
        this.showColorDataPointSelector = false
      }
      this.analyticsConfig.dataPoints = [
        this.sizeDataPoint,
        this.colorDataPoint,
      ]
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
.fc-heatmap-report-sidebar {
  width: 24% !important;
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
.label-line-height {
  line-height: 21px;
}
</style>
