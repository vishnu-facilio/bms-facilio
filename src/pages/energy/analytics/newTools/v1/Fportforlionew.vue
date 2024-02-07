<template>
  <div class="fc-energy-portfolio-analysis">
    <div class="report-main-con">
      <div class="">
        <f-report-options
          :isActionsDisabled="isActionsDisabled"
          optionClass="analytics-page-options"
          :optionsToEnable="[1, 2, 5]"
          :reportObject="reportObject"
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
                    class="setup-el-btn add-points-btn-first-child"
                    @click="showDataPointSelector = true"
                  >
                    <i
                      class="el-icon-circle-plus-outline f20 bold pR5 vertical-sub"
                    ></i
                    >{{ $t('home.dashboard.add_points') }}</el-button
                  >
                  <el-tooltip
                    effect="dark"
                    v-if="reportObject && analyticsConfig.mode === 4"
                    content="Add derivation"
                    placement="bottom"
                  >
                    <el-button
                      type="primary"
                      class="add-points-btn mL20 pL12 pR12"
                      v-if="reportObject && analyticsConfig.mode === 4"
                      @click="showDerivation = true"
                      ><img
                        src="~statics/formula/formula-grey.svg"
                        width="15px"
                        height="15px"
                    /></el-button>
                  </el-tooltip>
                  <el-tooltip
                    effect="dark"
                    v-else-if="analyticsConfig.mode !== 4"
                    content="Set Dimension as time to create a derivation"
                    placement="bottom"
                  >
                    <span
                      ><el-button
                        type="primary"
                        class="add-points-btn mL20 pL12 pR12"
                        :disabled="!reportObject && analyticsConfig.mode !== 4"
                        @click="onclick_fx"
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
                        :disabled="!reportObject && analyticsConfig.mode !== 4"
                        @click="onclick_fx"
                        ><img
                          src="~statics/formula/formula-grey.svg"
                          width="15px"
                          height="15px"/></el-button
                    ></span>
                  </el-tooltip>
                </el-button-group>
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
                    v-model="analyticsConfig.mode"
                    @change="onModeChange"
                    :placeholder="$t('common._common.mode')"
                    :title="$t('common.header.dimension')"
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
                    <el-option
                      :label="$t('common.header.m&v')"
                      :value="'mv'"
                    ></el-option>
                  </el-select>
                </el-col>
              </el-row>
              <el-row v-if="isTimeMode">
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
              <el-row v-if="analyticsConfig.mode !== 4">
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
              </el-row>
              <el-row class="mT20" v-if="analyticsConfig.mode !== 'mv'">
                <el-col :span="24">
                  <div class="pB10 pB5">
                    {{ $t('common._common.filters') }}
                  </div>
                  <div class="fL">
                    <div v-if="setfilter === 1">
                      <div class="fc-black2 f14 fw-bold pB5">
                        {{ $t('common.header.edit_filter') }}
                      </div>
                    </div>
                    <div v-else>
                      <div class="fc-black2 f14 fw-bold pB5">
                        {{ $t('common._common.add_filters') }}
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
                      :title="$t('common.header.edit_filter')"
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
            class="customize-container building-customize-container col-3 fc-new-portfolio-cus"
            style="top:0px;"
            v-if="reportObject"
            v-show="showChartCustomization"
          >
            <div class="customize-header">
              <div class="customize-H">
                {{ $t('home.reports.chart_customization') }}
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
        <f-portfolio-data-point-selector
          ref="building-selector"
          :mode="analyticsConfig.mode"
          :visibility.sync="showDataPointSelector"
          @selectBuildings="setSelectedBuildings"
          @updateDataPoints="updateDataPoints"
          :zones="zones"
          :isSiteAnalysis="isSiteAnalysis"
          :disableMultipleAssetCategories="analyticsConfig.mode === 8"
        ></f-portfolio-data-point-selector>
        <div v-if="!hideSideBar" class="analytics-options-block">
          <div class="">
            <div
              class="text-center analytics-report-icon-bg line-height50 height50"
              :class="{ 'analytics-report-icon-bg-active': showgeneratedata }"
              @click="showgeneratedataDialog()"
              style="border-bottom: 1px solid #eceded;"
            >
              <InlineSvg
                src="statistics"
                iconClass="icon icon-sm-md vertical-middle"
              ></InlineSvg>
            </div>
            <div
              class="text-center analytics-report-icon-bg line-height50 height50"
              :class="{
                'analytics-report-icon-bg-active': showChartCustomization,
              }"
              @click="reportObject ? showChartCustomizationDialog() : null"
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
              :class="{
                'analytics-report-icon-bg-active': showChartCustomization,
              }"
              @click="reportObject ? showChartCustomizationDialog() : null"
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
            <f-new-analytic-report-optimize
              ref="newAnalyticReport"
              :config.sync="analyticsConfig"
              :baseLines="baseLineList"
              @reportLoaded="reportLoaded"
              :showTimePeriod="analyticsConfig.mode === 4 ? true : false"
              :showChartMode="true"
              v-else
            ></f-new-analytic-report-optimize>
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
                  class="fc-input-full-border2 width100"
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
                    :label="$t('common.header.multi_select_box')"
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
import FNewAnalyticReportOptimize from 'pages/energy/analytics/newTools/v1/FNAROptimize'
import FPortfolioDataPointSelector from 'pages/energy/analytics/components/FPortfolioDataPointSelector'
import Derivation from 'pages/energy/analytics/components/FDerivation'
import FChartCustomization from 'newcharts/components/NewFChartCustomization'
import FReportOptions from 'pages/report/components/FReportOptions'
import NewDateHelper from '@/mixins/NewDateHelper'
import InlineSvg from '@/InlineSvg'
import { isEmpty } from '@facilio/utils/validation'
import cloneDeep from 'lodash/cloneDeep'
import { mapState } from 'vuex'
import { API } from '@facilio/api'
import FLookupFieldWrapper from '@/FLookupFieldWrapper'

export default {
  props: ['mobileConfig'],
  mixins: [AnalyticsMixin],
  components: {
    Derivation,
    FNewAnalyticReportOptimize,
    FChartCustomization,
    FPortfolioDataPointSelector,
    FReportOptions,
    InlineSvg,
    FLookupFieldWrapper,
  },
  data() {
    return {
      hideSideBar: false,
      showDataPointSelector: false,
      showChartCustomization: false,
      showgeneratedata: true,
      assetsMap: null,
      assetCategoryMap: null,
      filtersPopover: false,
      sortPopover: false,
      showDerivation: false,
      chartCustomizationActiveTab: 'datapoints',
      selectedBaseLineId: '',
      baseLineList: null,
      baseLineCasecaderOptions: [],
      oldBaseLine: null,
      analyticsConfig: {
        name: 'Portfolio Analysis',
        key: 'PORTFOLIO_ANALYSIS',
        analyticsType: 1,
        type: 'reading',
        period: 12,
        mode: 4,
        baseLine: [-1],
        dateFilter: NewDateHelper.getDatePickerObject(28),
        chartViewOption: 0,
        dataPoints: [],
        mvPoints: [],
        filters: {
          xCriteriaMode: 5,
        },
        sorting: {
          sortByField: null,
          orderByFunc: 3,
          limit: 10,
        },
      },
      setfilter: 0,
      reportObject: null,
      resultObject: null,
      selectedBuildings: [],
      selectedBuildingTitle: '',
      zones: null,
      buildingList: null,
      reportFilterForm: {
        siteFilter: [],
        buildingFilter: [],
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
        allBuildings: null,
      },
      compare_indicator: 2,
      isShowCompareIndicator: false,
    }
  },
  watch: {
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
    this.$store.dispatch('loadAlarmSeverity')
    this.$store.dispatch('loadAssetCategory')
    this.getBuildingList()
  },
  mounted() {
    if (this.$route.query.filters) {
      let filters = this.$route.query.filters
      let filtersJSON = JSON.parse(filters)
      this.analyticsConfig = filtersJSON
    }
    if (this.reportId || this.alarmId || this.cardId) {
      this.showDataPointSelector = false
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
    cssLeft() {
      return 0 + 'px !important'
    },
    isSiteAnalysis() {
      return true
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
    isTimeMode() {
      return this.analyticsConfig.mode === 4 || this.analyticsConfig.mode === 5
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
    async getBuildingList() {
      let { data, error } = await API.get('/v3/picklist/building')
      if (!error) {
        this.buildingList = cloneDeep(data.pickList)
        this.selectedCategory.allBuildings = cloneDeep(data.pickList)
      }
    },
    onclick_fx() {
      if (this.analyticsConfig.mode !== 4 && !this.reportObject) {
        this.showDerivation = false
      }
    },

    toggleSideBar() {
      if (this.hideSideBar) {
        this.hideSideBar = false
        if (this.$refs['newAnalyticReport']) {
          console.log('yes')
          this.$refs['newAnalyticReport'].resize()
        }
      } else {
        this.hideSideBar = true
        if (this.$refs['newAnalyticReport']) {
          this.$refs['newAnalyticReport'].resize()
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
      } else if (buildingList) {
        self.loading = true
        self.$http
          .post('/asset/getAssetCategoryWithReadings', {
            buildingIds:
              buildingList ||
              self.selectedCategory.allBuildings.map(rt => rt.value),
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
                self.selectedCategory.allBuildings.map(rt => rt.value)
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
            this.loadAssetCategories(this.buildingList.map(rt => rt.value))
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
            this.loadAssetCategories(this.buildingList.map(rt => rt.value))
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
          this.loadAssetCategories(this.buildingList.map(rt => rt.value))
        }
      } else if (root === 'category') {
        this.loadReadings(this.reportFilterForm.categoryFilter)
      }
    },
    reportLoaded(report, result) {
      this.reportObject = report
      this.resultObject = result
      if (report.options.common && report.options.common.buildingIds) {
        if (
          !report.options.common.buildingIds.length &&
          this.selectedBuildings
        ) {
          this.reportObject.options.common.buildingIds = this.selectedBuildings
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
      this.analyticsConfig.mvPoints = this.analyticsConfig.dataPoints.filter(
        point => point.moduleName === 'mvproject'
      )
      this.addPredictionFields(this.analyticsConfig)
      this.showDataPointSelector = false
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
      if (this.analyticsConfig.mode === 'mv') {
        this.analyticsConfig.dataPoints = []
        this.resetFilter()
        this.$refs['building-selector'].setInitialValues(
          this.analyticsConfig.dataPoints,
          this.selectedBuildings
        )
      } else if (
        this.analyticsConfig.mode === 8 &&
        this.isSameCategoryDataPoint
      ) {
        this.$message({
          message: this.$t(
            'common.header.please_choose_single_asset_category_point'
          ),
          type: 'warning',
        })
        this.analyticsConfig.dataPoints = []
        this.resetFilter()
        this.$refs['building-selector'].setInitialValues(
          this.analyticsConfig.dataPoints,
          this.selectedBuildings
        )
      } else {
        this.analyticsConfig.dataPoints = this.analyticsConfig.dataPoints.filter(
          dp => ![2, 4].includes(dp.type) && dp.moduleName !== 'mvproject'
        )
        this.analyticsConfig.mvPoints = []
        if (isEmpty(this.analyticsConfig.dataPoints)) {
          this.$refs['building-selector'].setInitialValues(
            this.analyticsConfig.dataPoints,
            this.selectedBuildings
          )
        }
      }
      if (this.analyticsConfig.mode !== 4) {
        this.analyticsConfig.dataPoints = this.analyticsConfig.dataPoints.filter(
          dp => ![2, 4].includes(dp.type)
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
}
</script>
<style lang="scss">
.fc-energy-portfolio-analysis {
  .add-points-block .el-button--primary.is-disabled {
    background: #ffffff;
  }
}
.label-line-height {
  line-height: 21px;
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
