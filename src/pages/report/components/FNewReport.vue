<template>
  <div
    class="height100 mobile-report fnew-report-page"
    :class="{
      scrollable:
        config &&
        config.widget &&
        reportObj &&
        reportObj.options &&
        reportObj.options.settings.chart === false,
      'scatter-main-con': isScatter,
    }"
    style="overflow-y: scroll;"
  >
    <div
      v-if="showHeadingOnlyForPdf && pdfloading"
      class="text-center label-txt-black4 bold pB20"
    >
      <div
        style="overflow:hidden;"
        class="report-pdf-header"
        v-if="!(excludeParamsObj.title && excludeParamsObj.logo)"
      >
        <div
          v-if="$org.logoUrl"
          class="fc-report-cus-logo"
          :class="{ 'logo-visible': excludeParamsObj.logo }"
        >
          <img :src="$org.logoUrl" style="width: 100px; height:50px" />
        </div>
        <div
          v-else
          class="fc-logo-report"
          :class="{ 'logo-visible': excludeParamsObj.logo }"
        >
          <img v-if="showLogo" :src="getFacilioLogoURL" />
        </div>
        <div
          class="f18 fc-widget-label ellipsis max-width350px"
          v-if="!excludeParamsObj.title"
        >
          {{ resultObj.report.name || 'AnalyticalReport' }}
        </div>
      </div>
      <!-- print  -->
      <div
        class="fc-report-author-sec pdf-text-left pdf-margin-top"
        v-if="showPrintDetails || printView"
      >
        <div v-if="!excludeParamsObj.description">
          <div class="fc-report-author-txt">
            {{ $t('common.wo_report.generated_on') }}
            <span class="fw6 pdf-left-margin"> {{ formatDate() }} </span>
          </div>
          <div class="fc-report-author-txt">
            {{ $t('common.wo_report.generated_by') }}
            <span class="fw6 pdf-left-margin"> {{ $account.user.name }} </span>
          </div>
        </div>

        <div v-if="!excludeParamsObj.filter">
          <div class="fc-report-author-txt flex-middle">
            {{ `${$t('common.date_picker.date_range')}:` }}
            <div class="f9 fw6 pdf-left-margin">
              {{ getDateRangeString }}
            </div>
          </div>
          <div v-if="isFilterApplied" class="fc-report-author-txt">
            {{ `${pdfFilter}  :` }}
            <span class="fw6 pdf-left-margin">
              {{ ` ${displayName}` }}
            </span>
          </div>
        </div>
      </div>
    </div>
    <div class="datefilter-hide-overlay"></div>
    <div style="text-align: center;" v-if="!loading">
      <portal
        :to="'widget-datepicker' + config.widget.id"
        slim
        class="tabulardata-datepicker"
        v-if="typeof config !== 'undefined' && widgetDatePicker"
      >
        <new-date-picker
          v-if="dateFilter"
          :zone="$timezone"
          class="filter-field date-filter-comp-new-report inline"
          :dateObj="dateFilter"
          @date="setDateFilter"
          :key="datePickerCompKey"
        ></new-date-picker>
      </portal>
      <portal
        :to="'widget-datepicker' + datePickerTarget"
        slim
        class="tabulardata-datepicker"
        v-else-if="datePickerTarget"
      >
        <new-date-picker
          v-if="!hideTimelineFilterInsideWidget && dateFilter"
          :zone="$timezone"
          class="filter-field date-filter-comp-asset-summary inline"
          :dateObj="dateFilter"
          @date="setDateFilter"
          :key="datePickerCompKey"
        ></new-date-picker>
      </portal>
      <template v-else>
        <new-date-picker
          v-if="!hideTimelineFilterInsideWidget && dateFilter"
          :zone="$timezone"
          class="filter-field date-filter-comp-new-report inline"
          :dateObj="dateFilter"
          @date="setDateFilter"
          :key="datePickerCompKey"
        ></new-date-picker>
      </template>
      <div
        v-if="
          reportObj &&
            reportObj.options.settings.timeperiod === true &&
            showTimePeriod &&
            !isScatter
        "
        class="chart-icon pointer pB8 el-popover__reference"
        style="padding-left: 210px; top: 10px;height: 50px;position: absolute;"
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
        :settings="
          reportObj && reportObj.options ? reportObj.options.settings : null
        "
        v-if="
          !hideChartSettings &&
            reportObj &&
            reportObj.options &&
            reportObj.options.settings &&
            !(config && config.widget) &&
            !isRegression &&
            !isHeatMap &&
            !isTreeMap &&
            !isScatter
        "
        @onSettingsChange="onSettingsChange"
        class="freport-page-settings"
      ></f-chart-settings>
    </div>
    <FDateTimeFilter
      v-if="
        !(
          reportObj &&
          reportObj.options.common.filters &&
          reportObj.options.common.filters.filterState &&
          reportObj.options.common.filters.filterState.liveFilterField &&
          reportObj.options.common.filters.filterState.liveFilterField !==
            'none' &&
          !isRegression &&
          loading === false &&
          resultObj &&
          reportObj.reportType !== 4
        )
      "
      :filters="dbFilterJson"
      :report="reportObj"
      @applyTTimeFilter="applyTTimeFilter"
    >
    </FDateTimeFilter>
    <f-user-filters
      v-if="
        reportObj &&
          reportObj.options.common.filters &&
          reportObj.options.common.filters.filterState &&
          reportObj.options.common.filters.filterState.liveFilterField &&
          reportObj.options.common.filters.filterState.liveFilterField !==
            'none' &&
          !isRegression &&
          loading === false
      "
      :ref="
        `userFilter-${
          config && config.widget && config.widget.id
            ? config.widget.id
            : reportId
        }`
      "
      :report="reportObj"
      :filters="filter"
      @filterData="value => liveFilterValue(value)"
      @applyFilter="applyFilter"
      @userFilterLoaded="userFilterLoaded"
    ></f-user-filters>
    <!-- <el-button type="primary" @click="showSpaceAssetChooser = true">Click me</el-button> -->
    <div
      v-if="reportTemplateAssetChooser"
      class="chart-icon pointer pB8 el-popover__reference mL10 mT10"
    >
      <!-- <div class="fc-text-pink11 f10 text-uppercase">Choose Asset</div>
      <div v-if="reportTemplate" class="flex-middle fc-black-12 pT5 pointer" @click="showSpaceAssetChooser = true">
        <img src="~assets/one-box-light.svg" width="14" height="14" class="mR5"/>
      {{reportTemplate.name}}</div>-->
      <el-row>
        <el-col :span="5" class="text-left position-relative">
          <el-select
            class="period-select fwidget-report-period-select width100 mB30"
            :class="{ 'position-absolute': isWidget }"
            size="mini"
            v-model="reportTemplate.parentId"
            filterable
            :disabled="disbaleRPassetChooser"
            @change="applyTemplate"
          >
            <el-option
              v-for="(val, valIdx) in reportTemplate.chooseValues"
              :key="valIdx"
              :label="val.name"
              :value="val.id"
            ></el-option>
          </el-select>
        </el-col>
      </el-row>
    </div>
    <!-- <SpaceAssetChooser picktype="asset" :disableAssetCategory="true" :showAsset="true" @associate="applyTemplate" :visibility.sync="showSpaceAssetChooser" :initialValues="computeSpaceAssetInitValue"></SpaceAssetChooser> -->

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
        <template v-if="isHeatMap">
          <FNewHeatMap
            ref="heatmap"
            :config="config"
            :resultObject="resultObj"
            :width.sync="this.widgetWidth"
            :height.sync="this.widgetHeight"
            :reportObject="reportObj"
          ></FNewHeatMap>
          <f-tabular-report
            v-if="!hideTable"
            ref="newTable"
            class="newTable"
            :reportObject="resultObj"
            :reportConfig="reportObj"
            :hideColumn="hideColumn"
            :widget="config ? config.widget : null"
          ></f-tabular-report>
        </template>
        <template v-else-if="isTreeMap">
          <FTreeMap
            ref="treemap"
            :config="config"
            :resultObject="resultObj"
            :width.sync="this.widgetWidth"
            :height.sync="this.widgetHeight"
            :reportObject="reportObj"
          ></FTreeMap>
          <f-tabular-report
            v-if="!hideTable"
            ref="newTable"
            class="newTable"
            :reportObject="resultObj"
            :reportConfig="reportObj"
            :hideColumn="hideColumn"
            :widget="config ? config.widget : null"
          ></f-tabular-report>
        </template>
        <template v-else>
          <div
            :class="{ height100: config }"
            v-if="
              reportObj &&
                reportObj.options &&
                reportObj.options.settings &&
                reportObj.options.settings.chartMode === 'multi' &&
                !isScatter &&
                reportObj.options.settings.chart !== false &&
                !showChart
            "
          >
            <f-multi-chart
              v-if="!$mobile"
              ref="multiChart"
              :isWidget="config ? true : false"
              :width="config && config.width ? config.width : null"
              :resultObj="resultObj"
              :height="config && config.height ? config.height : null"
              :data="reportObj.data"
              :options="reportObj ? reportObj.options : null"
              :alarms="reportObj.alarms"
              :dateRange="reportObj.dateRange"
              :hidecharttypechanger="isRegression"
              :booleanData="reportObj.booleanData"
            ></f-multi-chart>
            <!-- <div v-else class="empty-multi-chart">Currently this chart not supported in the mobile</div> -->
            <f-mobile-multi-chart
              v-else
              ref="multiChart"
              :isWidget="config ? true : false"
              :config="config"
              :width="config && config.width ? config.width : null"
              :resultObj="resultObj"
              :height="config && config.height ? config.height : null"
              :data="reportObj.data"
              :options="reportObj ? reportObj.options : null"
              :alarms="reportObj.alarms"
              :dateRange="reportObj.dateRange"
              :hidecharttypechanger="isRegression"
              :booleanData="reportObj.booleanData"
            ></f-mobile-multi-chart>
          </div>
          <div
            :class="{
              height100:
                config &&
                reportObj &&
                reportObj.options.settings.chart !== false,
            }"
            v-else-if="!showChart"
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
              :options="reportObj ? reportObj.options : null"
              :dateRange="reportObj.dateRange"
              :reportType="reportObj.reportType"
            ></f-mobile-chart>
            <f-new-chart
              v-else
              :config="config"
              ref="newChart"
              :isWidget="config ? true : false"
              :width="config && config.width ? config.width : null"
              :resultObj="resultObj"
              :height="config && config.height ? config.height : null"
              :data="reportObj.data"
              :options="reportObj ? reportObj.options : null"
              :alarms="reportObj.alarms"
              :dateRange="reportObj.dateRange"
              :hidecharttypechanger="isRegression || isScatter"
              :reportType="reportObj.reportType"
              class="fc-single-chart-pdf"
              :class="{ 'fc-scatter-chart': isScatter }"
            ></f-new-chart>
          </div>
          <f-tabular-report
            v-if="!hideTable && !isScatter"
            ref="newTable"
            class="newTable tabular-table-widget"
            :reportObject="resultObj"
            :reportConfig="reportObj"
            :hideColumn="hideColumn"
            :widget="config ? config.widget : null"
          ></f-tabular-report>
        </template>
        <template v-if="isScatter && !hideTable && !$mobile">
          <template v-if="!isPrinting">
            <!-- <div class="white-bg-block mB10 width100">
            <div class="analytics-section mT20">
              <el-collapse
                v-model="activeTrend"
                @change="renderTrendChart()"
                class="fc-pushing-data-collapse fc-collapse-arrow-bg fc-analytics-collapse fc-scatter-collapse"
              >
                <el-collapse-item title="Reading Trends" name="1">
                  <div v-if="activeTrend.includes('1')">
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
                        <span
                          class="bold"
                          v-if="resultObj && resultObj.dateRange"
                          >{{
                            resultObj.dateRange.value
                              | formatPeriod('MMM DD, YYYY')
                          }}</span
                        >
                      </div>
                    </div>
                    <div v-else>
                      <div>
                        <f-multi-chart
                          ref="trendChart"
                          v-if="trendreportObj && isScatter"
                          :reportObj="trendreportObj"
                          :data="trendreportObj.data"
                          :options="
                            trendreportObj ? trendreportObj.options : null
                          "
                          :alarms="trendreportObj.alarms"
                          :booleanData="trendreportObj.booleanData"
                          :dateRange="trendreportObj.dateRange"
                          :resultObj="trendresultObj"
                          :ismulti="isScatter"
                          hidecharttypechanger="true"
                        ></f-multi-chart>
                      </div>
                    </div>
                  </div>
                </el-collapse-item>
              </el-collapse>
            </div>
          </div> -->

            <div class="white-bg-block mB10 width100">
              <div class="analytics-section mT20">
                <el-collapse
                  v-model="activeTable"
                  class="fc-pushing-data-collapse fc-collapse-arrow-bg fc-analytics-collapse fc-scatter-collapse"
                >
                  <el-collapse-item title="Undelying Data" name="1">
                    <div v-if="activeTable.includes('1')">
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
                        <div>
                          {{ $t('home.dashboard.data_loading_failed') }}
                        </div>
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
                          <span
                            class="bold"
                            v-if="resultObj && resultObj.dateRange"
                            >{{
                              resultObj.dateRange.value
                                | formatPeriod('MMM DD, YYYY')
                            }}</span
                          >
                        </div>
                      </div>
                      <f-tabular-report
                        ref="newTable"
                        :reportObject="trendresultObj"
                        :reportConfig="trendreportObj"
                        :hideColumn="hideColumn"
                        :widget="true"
                        :showWidgetTable="true"
                        class="new-analytics-table"
                      ></f-tabular-report>
                    </div>
                  </el-collapse-item>
                </el-collapse>
              </div>
            </div>
          </template>
          <f-tabular-report
            v-else
            ref="newTable"
            class="fc-tabular new-analytics-table"
            :reportObject="trendresultObj"
            :reportConfig="trendreportObj"
            :hideColumn="hideColumn"
          ></f-tabular-report>
        </template>
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
import { API } from '@facilio/api'
import NewDatePicker from '@/NewDatePicker'
import FNewChart from 'newcharts/components/FNewChart'
import FMobileChart from 'newcharts/components/FMobileChart'
import FMobileMultiChart from 'newcharts/components/FMobileMultiChart'
import FMultiChart from 'newcharts/components/FMultiChart'
import NewDataFormatHelper from 'pages/report/mixins/NewDataFormatHelper'
import FTabularReport from 'pages/report/components/FTabularReportNew'
import NewDateHelper from '@/mixins/NewDateHelper'
import FChartSettings from 'src/newcharts/components/FNewChartSettings'
import FUserFilters from './FUserFilters'
import NewReportSummaryHelper from 'src/pages/report/mixins/NewReportSummaryHelper'
import MultipleRegressionAnalysis from 'src/pages/energy/analytics/newTools/v1/MultipleRegressionAnalysis'
import InlineSvg from '@/InlineSvg'
import FNewHeatMap from 'pages/energy/analytics/components/FNewHeatMap'
import FTreeMap from 'pages/energy/analytics/components/FTreeMap'
import { isEqual } from 'lodash'
import deepmerge from 'util/deepmerge'
import { isEmpty, isUndefined } from '@facilio/utils/validation'
import FDateTimeFilter from './FDateTimeFilter'
import moment from 'moment-timezone'
import Vue from 'vue'

export default {
  props: [
    'id',
    'config',
    'tabular',
    'showTimePeriod',
    'templateJson',
    'hideChartSettings',
    'hideTabularReport',
    'datePickerTarget',
    'dbTimelineFilter',
    'dbFilterJson',
    'qs',
    'isLazyDashboard',
    'isVisibleInViewport',
    'showHeadingOnlyForPdf',
    'hideColumn',
    'showChart',
    'printView',
  ],
  mixins: [NewDataFormatHelper, NewReportSummaryHelper],
  components: {
    NewDatePicker,
    FNewChart,
    FMultiChart,
    FTabularReport,
    FMobileChart,
    FChartSettings,
    FUserFilters,
    FMobileMultiChart,
    MultipleRegressionAnalysis,
    InlineSvg,
    FNewHeatMap,
    FTreeMap,
    FDateTimeFilter,
  },
  data() {
    return {
      pdfloading: false,
      applyFilterCallbackHook: true,
      disbaleRPassetChooser: false,
      loading: true,
      failed: false,
      reportObj: null,
      resultObj: null,
      showReportOptions: false,
      dateFilter: null,
      userFilterApplied: null,
      period: null,
      reportTemplate: null,
      widgetWidth: null,
      widgetHeight: null,
      datePickerCompKey: 1,
      activeTable: [],
      activeTrend: [],
      trendresultObj: null,
      trendreportObj: null,
      filter: {},
      displayName: [],
      isFilterApplied: false,
      excludeParamsObj: {},
    }
  },
  mounted() {
    if (this.$mobile) {
      this.widgetWidth = this.$el.clientWidth
      this.widgetHeight = this.$mobile
        ? this.config.height
        : this.$el.clientHeight - 30
    } else if (this.isPrinting) {
      this.widgetWidth = this.$el.clientWidth
    }
    let { excludeParams = {} } = this.$route.query
    this.excludeParamsObj = !isEmpty(excludeParams)
      ? JSON.parse(excludeParams) ?? {}
      : {}
    this.init()
  },
  computed: {
    getFacilioLogoURL() {
      let { brandConfig } = this
      let logo = this.$getProperty(brandConfig, 'logo', null)
      return logo
    },
    showLogo() {
      let { brandConfig } = this
      let showLogoInPDF = this.$getProperty(brandConfig, 'showLogoInPDF', true)
      return showLogoInPDF
    },
    brandConfig() {
      return window.brandConfig
    },
    getDateRangeString() {
      const dateRange = this.$getProperty(this.resultObj, 'dateRange.value', {})
      const startTime = moment(dateRange[0])
        .tz(Vue.prototype.$timezone)
        .format('DD/MM/YYYY')
      const endTime = moment(dateRange[1])
        .tz(Vue.prototype.$timezone)
        .format('DD/MM/YYYY')
      return `${startTime} to ${endTime}`
    },
    pdfFilter() {
      let filterName = this.$getProperty(
        this.reportObj,
        'options.common.filters.filterState.liveFilterField',
        'Filter'
      )
      return `${filterName.charAt(0).toUpperCase()}${filterName.slice(1)}`
    },
    reportTemplateAssetChooser() {
      let { resultObj, reportTemplate } = this
      if (
        resultObj &&
        resultObj.report &&
        resultObj.report.type === 4 &&
        reportTemplate &&
        reportTemplate.chooseValues.length !== 0
      ) {
        return true
      }
      return false
    },
    hideTimelineFilterInsideWidget() {
      if (this.config?.dashboardObj?.dashboardFilter?.hideFilterInsideWidgets) {
        return this.config.dashboardObj.dashboardFilter.hideFilterInsideWidgets
      }
      return false
    },
    widgetDatePicker() {
      if (
        this.reportObj &&
        this.reportObj.options &&
        this.reportObj.options.settings &&
        !this.reportObj.options.settings.chart &&
        this.reportObj.options.settings.table
      ) {
        return true
      } else {
        return false
      }
    },
    isWidget() {
      if (this.config) {
        return true
      }
      return false
    },
    isPrinting() {
      return (
        this.$route.query.print ||
        this.$route.query.daterange ||
        this.showHeadingOnlyForPdf
      ) // daterange check temp
    },
    computeSpaceAssetInitValue() {
      if (
        this.resultObj &&
        this.resultObj.report &&
        this.resultObj.report.reportTemplate
      ) {
        let temp = {}
        temp['assetCategory'] =
          this.resultObj.report.reportTemplate.categoryId + ''
        temp['isIncludeResource'] = false
        if (this.reportTemplate) {
          temp['selectedResource'] = []
          temp.selectedResource.push(this.reportTemplate.parentId)
        } else {
          temp['selectedResource'] = []
        }
        temp['reportTemplate'] = true
        return temp
      }
      return {}
    },
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
    isScatter() {
      if (
        this.resultObj &&
        this.resultObj.report &&
        this.resultObj.report.analyticsType === 8
      ) {
        return true
      }
      return false
    },
    isHeatMap() {
      if (
        this.resultObj &&
        this.resultObj.report &&
        this.resultObj.report.analyticsType === 3
      ) {
        return true
      }
      return false
    },
    isTreeMap() {
      if (
        this.resultObj &&
        this.resultObj.report &&
        this.resultObj.report.analyticsType === 7
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
    isSiteFilterApplied_toExport() {
      if (this.$route.query.spaceId_forExport) {
        return this.$route.query.spaceId_forExport
      }
      return null
    },
    isSiteFilterMode_toExport() {
      if (this.$route.query.filterModeValue_toExport) {
        return this.$route.query.filterModeValue_toExport
      }
      return null
    },
    appliedChartType() {
      // Temp...will be changed for pdf once temp report support is implemented
      return this.$route.query.charttype
    },
    hideTable() {
      if (this.excludeParamsObj.table) {
        return true
      }
      return this.showOnlyImage || this.hideTabularReport
    },
    appliedReportTemplate() {
      return this.$route.query.template
    },
    reportTemplteParams() {
      return this.$route.query.templateParams
    },
  },
  created() {
    if (this.dbTimelineFilter) {
      let pickerObj = NewDateHelper.getDatePickerObject(
        this.dbTimelineFilter.operatorId,
        this.dbTimelineFilter.dateValueString
      )
      this.dateFilter = pickerObj
    }

    if (this.qs) {
      let qsParams = this.getQueryStringParams(this.qs)
      if (qsParams.startTime && qsParams.endTime) {
        let pickerObj = NewDateHelper.getDatePickerObject(
          20,
          qsParams.startTime + ',' + qsParams.endTime
        )
        this.dateFilter = pickerObj
      }
    }
    this.$store.dispatch('loadAssetCategory')
  },
  watch: {
    //when widget at bottom of dashboard is scrolled up , this prop changes from false to true,
    isVisibleInViewport(newValue, oldValue) {
      if (newValue) {
        this.init()
      }
    },
    reportId: {
      handler(newData, oldData) {
        this.period = null
        this.dateFilter = false
        this.reportTemplate = null
        this.init(false)
      },
    },
    reportTemplteParams: {
      handler(newData, olddata) {
        if (newData !== olddata && this.reportTemplate) {
          this.setParamsToTemplate()
        }
      },
      deep: true,
    },
    config: {
      handler(newData, oldData) {
        if (newData && oldData) {
          if (newData.id !== oldData.id) {
            this.init(false)
          } else {
            if (this.isHeatMap || this.isTreeMap) {
              this.widgetWidth = this.$el.clientWidth
              this.widgetHeight = this.$mobile
                ? this.config.height
                : this.$el.clientHeight - 30
              if (this.$refs['heatmap']) {
                this.$refs['heatmap'].resize()
              } else if (this.$refs['treemap']) {
                this.$refs['treemap'].resize()
              }
            }
            this.$emit('reportLoaded', this.reportObj, this.resultObj)
          }
        } else {
          this.init(false)
        }
      },
      deep: true,
    },

    dbTimelineFilter(newValue, oldValue) {
      if (!isEqual(newValue, oldValue)) {
        let pickerObj = NewDateHelper.getDatePickerObject(
          newValue.operatorId,
          newValue.dateValueString
        )

        this.dateFilter = pickerObj
        this.init()
        this.datePickerCompKey++
      }
    },
    dbFilterJson(newValue, oldValue) {
      if (!isEqual(newValue, oldValue)) {
        this.applyFilterJson(this.configdbUserFilterJson())
      }
    },
    // 'getAvailablePeriods': {
    //   handler (newData, oldData) {
    //     if (this.reportObj) {
    //       let avail = this.getAvailablePeriods
    //       let selected = avail.find(a => a.value === this.reportObj.dateRange.period && a.enable)
    //       if (!selected) {
    //         let filterName = this.reportObj ? this.reportObj.dateRange.rangeType : ''

    //         let filtered = avail.filter(a => a.enable)
    //         let defaultPeriod =  (filtered && filtered.length) ? filtered[0].value : this.reportObj.dateRange.period
    //         if (filterName === 'week' || filterName === 'W') {
    //           defaultPeriod = 12
    //         }
    //         else if (filterName === 'month' || filterName === 'M') {
    //           defaultPeriod = 12
    //         }
    //         else if (filterName === 'year' || filterName === 'Y') {
    //           defaultPeriod = 10
    //         }
    //         this.reportObj.dateRange.period = defaultPeriod
    //       }
    //     }
    //   },
    //   immediate: true
    // }
  },
  methods: {
    liveFilterValue(value) {
      let filterIds = []
      const { params = {} } = this.reportObj
      let key1 = 'id',
        key2 = 'name'
      if (!isEmpty(params.parentId)) {
        filterIds = params.parentId
      } else if (!isEmpty(params.spaceId)) {
        filterIds = params.spaceId
        if (this.pdfFilter === 'Site') {
          key1 = 'value'
          key2 = 'label'
        }
      }
      this.displayName = (value || []).reduce((acc, cur) => {
        if (filterIds.includes(cur[key1])) {
          return [...acc, cur[key2]]
        }
        return acc
      }, [])
      this.displayName = this.displayName.join(', ')
    },
    initialReportCalled() {
      if (this.dbFilterJson) {
        if (this.resultObj?.report?.type === 4) {
          this.initReportTemplateAssetFilter()
        } else {
          this.initDbuserFilterJson()
        }
      }
    },
    async loadAsset(assetId) {
      let { asset } = await API.fetchRecord('asset', {
        id: assetId,
      })

      if (asset) {
        this.reportTemplate.chooseValues.push({
          id: asset.id,
          name: asset.name,
        })
      }
    },
    initReportTemplateAssetFilter() {
      // this method helps to load template report while mount
      let filter = this.getReportTemplateJson(this.dbFilterJson)
      if (this.reportTemplate?.parentId && filter?.assetId) {
        this.reportTemplate.parentId = filter.assetId
        this.applyFilterCallbackHook = false
        this.prefillAsset(filter)
        this.applyTemplate()
      }
    },
    prefillAsset(filter) {
      // this method will add missing assets
      if (this.reportTemplate?.buildingId) {
        // removing the building filter becouse we load assets across the portfolio.
        this.reportTemplate.buildingId = null
      }
      if (this.reportTemplate.chooseValues !== null) {
        let asset = this.reportTemplate.chooseValues.find(
          rt => rt.id === filter.assetId
        )
        if (asset) {
          this.disbaleRPassetChooser = false
        } else {
          this.disbaleRPassetChooser = true
          this.loadAsset(filter.assetId)
        }
      } else {
        this.disbaleRPassetChooser = true
        this.reportTemplate.chooseValues = []
        this.loadAsset(filter.assetId)
      }
    },
    userFilterLoaded() {
      // user filter after load hook
    },
    initDbuserFilterJson() {
      let filter = this.prepareUserFilterProps(
        this.configdbUserFilterJson(),
        this.reportObj
      )
      this.applyFilter(filter)
    },
    formatedValue(value) {
      let values = []
      value.forEach(val => {
        values.push(Number(val))
      })
      return values
    },
    getReportTemplateJson(data) {
      let assetId = null
      if (!isEmpty(data) && !isUndefined(data)) {
        Object.keys(data).forEach(key => {
          if (key === 'asset') {
            let values = data[key].value
            if (values.length) {
              assetId = Number(values[0])
            }
          }
        })
      }
      return { assetId: assetId }
    },
    configdbUserFilterJson() {
      let { dbFilterJson, reportTemplateAssetChooser } = this

      if (reportTemplateAssetChooser) {
        return this.getReportTemplateJson(dbFilterJson)
      } else {
        let filter = {
          siteFilter: [],
          siteId: null,
          buildingFilter: [],
          buildingId: null,
          categoryFilter: [],
          assetFilter: [],
          assetId: null,
          ttimeFilter: {},
        }
        if (!isEmpty(dbFilterJson) && !isUndefined(dbFilterJson)) {
          Object.keys(dbFilterJson).forEach(key => {
            if (key === 'site') {
              let values = dbFilterJson[key].value
              if (values.length) {
                filter.siteId = Number(values[0])
              } else {
                filter.siteId = null
              }
              filter.siteFilter = this.formatedValue(values)
            } else if (key === 'asset') {
              let values = dbFilterJson[key].value
              if (values.length && values.length === 1) {
                filter.assetId = Number(values[0])
              } else if (values.length && values.length > 1) {
                filter.assetFilter = this.formatedValue(values)
              }
            } else if (key === 'building') {
              let values = dbFilterJson[key].value
              if (values.length && values.length === 1) {
                filter.buildingId = Number(values[0])
              } else if (values.length && values.length > 1) {
                filter.buildingFilter = this.formatedValue(values)
              }
            } else if (key && key.startsWith('ttime_')) {
              filter.ttimeFilter[key] = dbFilterJson[key]
            }
          })
        }
        return filter
      }
    },
    applyFilterJson(filter) {
      let userFilter = this.$refs[`userFilter-${this.config.widget.id}`]
      if (this.reportTemplateAssetChooser) {
        // this method helps to load template report while dashboard filter change

        if (filter.assetId) {
          this.reportTemplate.parentId = filter.assetId
          this.applyTemplate()
          this.prefillAsset(filter)
        }
        //
      } else if (userFilter) {
        userFilter.reportFilter = filter
        // userFilter.initProps()
        this.$nextTick(() => {
          userFilter.applyFilter()
        })
      }
    },
    getQueryStringParams(qs) {
      let params = {}
      if (qs) {
        let qsList = qs.split('&')
        for (let qsEntry of qsList) {
          let key = qsEntry.split('=')[0]
          let value = qsEntry.split('=')[1]
          params[key] = value
        }
      }
      return params
    },
    setParamsToTemplate() {
      let self = this
      let reportTemplteParam = JSON.parse(self.reportTemplteParams)
      if (reportTemplteParam.type && reportTemplteParam.type === 2) {
        let querryCategoryTemplate = reportTemplteParam.categoryTemplate
        if (
          querryCategoryTemplate &&
          querryCategoryTemplate.fahu &&
          querryCategoryTemplate['fahu']['parentId']
        ) {
          Object.values(self.reportTemplate.categoryTemplate)[0].parentId =
            querryCategoryTemplate['fahu']['parentId']
        }
        if (
          querryCategoryTemplate &&
          querryCategoryTemplate.energymeter &&
          querryCategoryTemplate['energymeter']['parentId']
        ) {
          Object.values(self.reportTemplate.categoryTemplate)[1].parentId =
            querryCategoryTemplate['energymeter']['parentId']
        }
      } else {
        self.reportTemplate = {}
        Object.keys(JSON.parse(this.reportTemplteParams)).forEach(rt => {
          self.$set(
            self.reportTemplate,
            rt,
            Number(JSON.parse(self.reportTemplteParams)[rt])
          )
        })
      }

      this.initChart(self.reportTemplate)
    },

    applyTemplate(val) {
      if (
        this.config &&
        this.config.widget &&
        this.config.widget.dataOptions &&
        this.config.widget.dataOptions.reportTemplate
      ) {
        this.config.widget.dataOptions.reportTemplate = null
      }
      if (
        this.config &&
        this.config.widget &&
        this.config.widget.dataOptions &&
        this.config.widget.dataOptions.newReport &&
        this.config.widget.dataOptions.newReport.type === 4 &&
        this.config.widget.dataOptions.newReport.reportTemplate
      ) {
        this.config.widget.dataOptions.newReport.reportTemplate = null
      }
      this.init(false)
      // this.showSpaceAssetChooser = false
      // this.$nextTick(() => {
      //   this.reportTemplate.parentId = val.id
      //   this.reportTemplate['name'] = val.name
      //   this.initChart()
      // })
    },
    onPeriodChange() {
      this.initChart()
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
      this.$store.dispatch('loadAlarmSeverity')
      if (this.isLazyDashboard && !this.isVisibleInViewport) {
        return
      }
      this.initChart()
    },
    resize() {
      if (this.$refs['newChart']) {
        this.$refs['newChart'].resize()
      }
      if (this.$refs['heatmap']) {
        this.$refs['heatmap'].resize()
      }
      if (this.$refs['treemap']) {
        this.$refs['treemap'].resize()
      }
      if (this.$refs['multiChart']) {
        this.$refs['multiChart'].resize()
      }
      if (this.$refs['newTable']) {
        this.$refs['newTable'].resize()
      }
    },
    renderTrendChart() {
      if (this.$refs['trendChart']) {
        this.$refs['trendChart'].resize()
      }
    },
    setAppliedFilterInParams(filters, params, categoryId) {
      if (
        filters &&
        (filters?.siteFilter ||
          filters?.buildingFilter ||
          filters?.categoryFilter ||
          filters?.assetFilter)
      ) {
        if (
          !filters?.siteFilter?.length &&
          !filters?.buildingFilter?.length &&
          !filters?.categoryFilter?.length &&
          !filters?.assetFilter?.length
        ) {
          params.xCriteriaMode = 2
        }
        if (filters?.assetFilter?.length) {
          params.xCriteriaMode = 3
          params.parentId = filters.assetFilter
        } else {
          let spaceFilter = filters?.buildingFilter?.length
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
      if (filters?.ttimeFilter) {
        params.ttimeFilter = JSON.stringify(filters.ttimeFilter)
      }
    },
    formTTimeFilter(dbFilterJson) {
      let formTTimeFilerObj = {}
      if (dbFilterJson) {
        const keys = Object.keys(dbFilterJson)
        keys.forEach((key, index) => {
          if (key && key.startsWith('ttime_')) {
            formTTimeFilerObj[key] = dbFilterJson[key]
          }
        })
        if (!isEmpty(formTTimeFilerObj)) {
          return formTTimeFilerObj
        }
      }
      return null
    },
    initChart(reportTempl) {
      let self = this
      let { templateJson } = this
      self.loading = true
      self.failed = false
      this.$nextTick(() => {
        let params = { newFormat: true }

        if (
          this.config &&
          this.config.widget &&
          this.config.widget.dataOptions &&
          this.config.widget.dataOptions.reportTemplate
        ) {
          let reportTemplate = null
          if (
            typeof this.config.widget.dataOptions.reportTemplate === 'string'
          ) {
            try {
              reportTemplate = JSON.parse(
                this.config.widget.dataOptions.reportTemplate
              )
            } catch (e) {
              console.log('Cannot parse saved report template ' + e)
            }
          } else {
            reportTemplate = JSON.parse(
              JSON.stringify(this.config.widget.dataOptions.reportTemplate)
            )
          }

          if (
            reportTemplate &&
            reportTemplate.parentId &&
            reportTemplate.chooseValues &&
            reportTemplate.chooseValues.length === 0
          ) {
            self.reportTemplate = JSON.parse(JSON.stringify(reportTemplate))
          }
        } else if (
          this.config &&
          this.config.widget &&
          this.config.widget.dataOptions &&
          this.config.widget.dataOptions.newReport &&
          this.config.widget.dataOptions.newReport.type === 4 &&
          this.config.widget.dataOptions.newReport.reportTemplate
        ) {
          let reportTemplate = JSON.parse(
            JSON.stringify(
              this.config.widget.dataOptions.newReport.reportTemplate
            )
          )
          if (
            reportTemplate.parentId &&
            reportTemplate.chooseValues &&
            reportTemplate.chooseValues.length === 0
          ) {
            self.reportTemplate = reportTemplate
          }
        }

        if (this.isWidget && this.reportObj) {
          params.showAlarms = this.reportObj.options.settings.alarm
          params.showSafeLimit = this.reportObj.options.settings.safelimit
        }

        this.reportObj = null
        if (this.appliedDateRange) {
          params.startTime = this.appliedDateRange?.startTime
          params.endTime = this.appliedDateRange?.endTime
        } else if (self.dateFilter && self.dateFilter.value) {
          params.startTime = self.dateFilter.value[0]
          params.endTime = self.dateFilter.value[1]
        }
        if (this.isSiteFilterApplied_toExport) {
          params.spaceId_forExport = this.isSiteFilterApplied_toExport
          params.filterModeValue_toExport = this.isSiteFilterMode_toExport
        }
        if (self.period !== null) {
          params.xAggr = self.period
        }
        let appliedAssetFilter = {}
        if (this.filter && this.filter.siteFilter) {
          let categoryId = []
          self.setAppliedFilterInParams(this.filter, params, categoryId)
          if (params?.parentId) {
            appliedAssetFilter.parentId = params.parentId
          }
        }
        if (this?.dbFilterJson && Object.keys(this.dbFilterJson).length > 0) {
          let tTimeFilterVal = this.formTTimeFilter(this.dbFilterJson)
          if (tTimeFilterVal) {
            params.ttimeFilter = JSON.stringify(tTimeFilterVal)
          }
        }
        let url = '/v3/report/reading/view?reportId=' + this.reportId
        if (templateJson) {
          url =
            url +
            '&templateString=' +
            encodeURIComponent(JSON.stringify(templateJson))
          if (templateJson?.parentId) {
            appliedAssetFilter.parentId = templateJson.parentId
          }
        } else if (reportTempl) {
          reportTempl.chooseValues = []
          url =
            url +
            '&templateString=' +
            encodeURIComponent(JSON.stringify(reportTempl))
          if (reportTempl?.parentId) {
            appliedAssetFilter.parentId = reportTempl.parentId
          }
        } else if (self.reportTemplate) {
          if (self.reportTemplate.templateType !== 2) {
            let template = JSON.parse(JSON.stringify(self.reportTemplate))
            template.chooseValues = []
            url =
              url +
              '&templateString=' +
              encodeURIComponent(JSON.stringify(template))
            if (template?.parentId) {
              appliedAssetFilter.parentId = template.parentId
            }
          }
        } else if (this.appliedReportTemplate) {
          let template = JSON.parse(this.appliedReportTemplate)
          template.chooseValues = []
          url =
            url +
            '&templateString=' +
            encodeURIComponent(JSON.stringify(template))
          if (template?.parentId) {
            appliedAssetFilter.parentId = template.parentId
          }
        }
        const { filters = {} } = this.$route.query
        let filterObj = !isEmpty(filters) ? JSON.parse(filters) ?? {} : {}
        if (!isEmpty(filterObj)) {
          this.isFilterApplied = true
          params.xCriteriaMode = filterObj.xCriteriaMode
          if (!isEmpty(filterObj.spaceId)) {
            params.spaceId = filterObj.spaceId
          }
          if (!isEmpty(filterObj.parentId)) {
            params.parentId = filterObj.parentId
          }
        }
        API.put(url, params)
          .then(response => {
            if (response.error) {
              self.$message.error('Error while fetching report data')
              self.loading = false
              self.failed = true
            } else {
              let result = response.data
              if (!self.dateFilter) {
                if (self.appliedDateRange) {
                  console.log('setting picker from query')
                  if (
                    self.appliedDateRange?.startTime &&
                    self.appliedDateRange?.endTime
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
                  console.log('setting picker')
                  self.dateFilter = NewDateHelper.getDatePickerObject(
                    result.report.dateOperator,
                    result.report.dateValue
                  )
                }
              }
              result.xAggr = response.data.report.xAggr
              if (
                result &&
                result.report &&
                result.report.reportState &&
                result.report.reportState.hmAggr
              ) {
                result.report.hmAggr = result.report.reportState.hmAggr
              }
              result.dateRange = self.dateFilter
              self.period = response.data.report.xAggr

              if (
                result &&
                result.report &&
                result.report.analyticsType === 8 &&
                result.report.reportState &&
                result.report.reportState.scatterConfig
              ) {
                self.trendresultObj = deepmerge.objectAssignDeep({}, result)
                self.trendresultObj.report.chartState = null
                self.prepareTrendReportObj(self.trendresultObj, params)
                let savedconfig = JSON.parse(
                  result.report.reportState.scatterConfig
                )
                result.scatterConfig = savedconfig.datapoints
              }

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
                  result.regressionType =
                    result.report.reportState.regressionType
                  result.regressionConfig =
                    result.report.reportState.regressionConfig
                }
              }
              for (let dp of result.report.dataPoints) {
                if (dp.xDataPoint === true) {
                  result['Readingdp'] = dp
                  break
                }
              }

              if (result.report.trendLineDataPoints) {
                self.getTrendLineEquation(result)
                result.report.dataPoints = result.report.dataPoints.concat(
                  result.report.trendLineDataPoints
                )
              }
              if (result.report.chartState) {
                let chartStateObject = JSON.parse(result.report.chartState)
                if (chartStateObject.common.mode === 11) {
                  let x = result.report.dataPoints[0].yAxis
                  let y = result.report.dataPoints[0].xAxis
                  result.report.dataPoints[0].yAxis = y
                  result.report.dataPoints[0].xAxis = x
                }
              }
              if (Object.keys(appliedAssetFilter)?.length > 0) {
                let assets = this.reportTemplate?.chooseValues
                for (let aId in assets) {
                  if (assets[aId].id === appliedAssetFilter.parentId) {
                    appliedAssetFilter.name = assets[aId].name
                  }
                }
                result.appliedAssetFilter = appliedAssetFilter
              }
              self.reportObj = self.prepareReport(result, params)
              self.reportObj.alarms = self.prepareBooleanReport(
                result.reportData.aggr
              )
              if (self.reportObj?.options?.type === 'table') {
                this.$emit('hideChart')
              }
              if (result.report.type === 4) {
                if (self.reportTemplate === null) {
                  self.reportTemplate = JSON.parse(
                    JSON.stringify(result.report.reportTemplate)
                  )
                }
                if (
                  self.reportTemplate.parentId === null &&
                  self.reportTemplate.defaultValue
                ) {
                  self.reportTemplate.parentId =
                    self.reportTemplate.defaultValue
                }
                if (
                  self.reportTemplate.chooseValues.length === 0 &&
                  (self.reportTemplate.isVisibleInDashBoard === true ||
                    self.reportTemplate.isVisibleInDashBoard === null)
                ) {
                  let filters = self.getFiltersFromCriteria(self.reportTemplate)
                  self.loadReportTemplateValues(self.reportTemplate, filters)
                }
              }

              if (
                self.reportTemplate &&
                result &&
                result.report &&
                result.report.type === 4
              ) {
                let template = JSON.parse(JSON.stringify(self.reportTemplate))
                template.chooseValues = []
                result.report.currentTemplate = template
              }

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
                  result.regressionType =
                    result.report.reportState.regressionType
                  result.regressionConfig =
                    result.report.reportState.regressionConfig
                }
              }

              if (self.reportObj && self.reportObj.options.mode) {
                result.mode = self.reportObj.options.mode
              }
              self.resultObj = result
              this.pdfloading = true
              if (self.appliedChartType && self.reportObj) {
                // TODO remove
                self.reportObj.options.type = self.appliedChartType
              }
              self.reportObj.params = params
              self.$emit('reportLoaded', self.reportObj, self.resultObj)
              if (params && params.spaceId_forExport) {
                self.filter = self.prepareUserFilterProps(
                  self.configdbUserFilterJson(),
                  self.reportObj
                )
                self.filter.siteFilter = [Number(params.spaceId_forExport)]
              }
              self.$nextTick(() => {
                if (self.applyFilterCallbackHook) {
                  if (
                    self.dbFilterJson &&
                    this.isOnlyTTimeFilter(self.dbFilterJson)
                  ) {
                    self.initialReportCalled()
                  }
                }
              })
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
      })
    },
    async loadInitialAssetName(reportTemplate) {
      let response = await this.$http.get(
        '/asset/summary/' + reportTemplate.parentId
      )

      if (response) {
        this.$set(reportTemplate, 'name', response.data.asset.name)
      }
    },
    isOnlyTTimeFilter(dbFilterJson) {
      if (dbFilterJson) {
        let flag = false
        const keys = Object.keys(dbFilterJson)
        keys.forEach((key, index) => {
          if (key && !key.startsWith('ttime_')) {
            flag = true
          }
        })
        return flag
      }
    },
    applyTTimeFilter(filterObj) {
      if (filterObj?.isTTimeFilter) {
        this.applyFilter(filterObj.filters, filterObj.isTTimeFilter)
      }
    },
    applyFilter(filters, isTTimeFilter) {
      let params = { newFormat: true }
      if (isTTimeFilter) {
        let tTimeFilterVal = this.formTTimeFilter(filters)
        if (tTimeFilterVal) {
          params.ttimeFilter = JSON.stringify(tTimeFilterVal)
          filters.ttimeFilter = tTimeFilterVal
        }
      }
      this.filter = filters
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
      params.mode = this.resultObj.mode
      if (self.resultObj?.report?.analyticsType) {
        params.analyticsType = self.resultObj?.report?.analyticsType
      }
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
          chartStateObject &&
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

      if ([1, 2, 3, 4].includes(params.mode)) {
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
          rule_aggr_type: dp?.rule_aggr_type,
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

      if (filters && !isTTimeFilter) {
        self.setAppliedFilterInParams(filters, params, categoryId)
      }
      if (
        isTTimeFilter ||
        !(
          filters &&
          (filters?.siteFilter?.length ||
            filters?.assetFilter?.length ||
            filters?.categoryFilter?.length ||
            filters?.buildingFilter?.length)
        )
      ) {
        params.xCriteriaMode = 6
      }
      self.$http
        .post('/v2/report/readingReport', params)
        .then(function(response) {
          let result = response.data.result
          result.xAggr = params.xAggr
          result.mode = params.mode
          result.report.analyticsType =
            result.report?.analyticsType > 0 ? result.report.analyticsType : 1
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
          this.reportObj &&
          this.reportObj.options.settings.alarm
        ) {
          this.initChart()
        }
      } else if (changedOption === 'safelimit') {
        if (
          this.reportObj &&
          !this.reportObj.options.safeLimit.length &&
          this.reportObj.options.settings.safelimit
        ) {
          this.initChart()
        }
      }
    },
    getTrendLineEquation(result) {
      let self = this
      let trendLineProp = result.reportData.trendLineProp
      let chartStateObject = JSON.parse(result.report.chartState)
      let trendlineObj = chartStateObject.trendLine
      let decimalCount = trendlineObj.decimal
      for (let alias in trendLineProp) {
        let coef = trendLineProp[alias].coef
        coef.forEach(function(data, index) {
          coef[index] =
            self.showDecimal(data, decimalCount) +
            (index !== 0 ? 'x^' + index : '')
        })
        coef.reverse()
        coef = coef.join(' + ')
        let dataPoint = result.report.trendLineDataPoints.filter(
          dataPoint => dataPoint.aliases.actual === alias
        )
        dataPoint[0].name = coef
        if (trendlineObj && trendlineObj.showr2) {
          dataPoint[0].name =
            'R^2 = ' +
            self.showDecimal(
              trendLineProp[dataPoint[0].aliases.actual].rSquare,
              decimalCount
            ) +
            '; ' +
            dataPoint[0].name
        }
      }
    },
    showDecimal(data, decimalCount) {
      let splitData = data.split('.')
      if (splitData[1]) {
        return splitData[0] + '.' + splitData[1].slice(0, decimalCount)
      }
      return data
    },
    prepareTrendReportObj(result, params) {
      let reportObj = this.prepareReport(result, params)
      reportObj.alarms = this.prepareBooleanReport(result.reportData.aggr)

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
          result.regressionConfig = result.report.reportState.regressionConfig
        }
      }

      if (this.reportObj && this.reportObj.options.mode) {
        result.mode = this.reportObj.options.mode
      }
      this.trendresultObj = result
      this.trendreportObj = reportObj
    },
  },
}
</script>

<style>
.logo-visible {
  visibility: hidden;
}
.pdf-margin-top {
  margin-top: 5px;
}
.pdf-left-margin {
  margin-left: 5px;
}
.pdf-text-left {
  text-align: left;
}
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

.fwidget-report-period-select {
  z-index: 100;
}
.fwidget-report-period-select input {
  border-radius: 3px !important;
  background-color: #ffffff !important;
  border: solid 1px #d0d9e2 !important;
  padding-left: 10px !important;
  letter-spacing: 0.4px !important;
  color: #324056 !important;
}
.asset-selection-report {
  position: absolute;
  top: 0px;
  z-index: 1;
}
.fnew-report-page .freport-page-settings .portfolio-equiv-icon {
  top: 0;
}
.vue-portal-target .date-filter-comp-new-report {
  right: 65px;
  top: 8px !important;
}
.vue-portal-target .date-filter-comp-asset-summary {
  top: 8px !important;
}
/* .tabular-table-widget {
  margin-top: 0 !important;
} */
@media print {
  .tabular-table-widget {
    margin-top: 20px !important;
  }
}
</style>
