<template>
  <div class="mobile-report">
    <div class="datefilter-hide-overlay"></div>
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
          <img :src="$org.logoUrl" style="width: 100px;height:50px" />
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
          {{ reportPdfData.report.name || 'Report' }}
        </div>
      </div>
      <!-- print  -->
      <div
        class="fc-report-author-sec pdf-margin-top pdf-text-left"
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
          <div
            class="fc-report-author-txt flex-middle"
            v-if="getDateRangeString.dateFieldLabel || getDateRangeString.label"
          >
            {{ `${$t('common.date_picker.date_range')}:` }}
            <div class="f9 fw6 pdf-left-margin">
              <span class="fc-report-author-txt">{{
                getDateRangeString.dateFieldLabel !== ''
                  ? getDateRangeString.dateFieldLabel.concat(':')
                  : ''
              }}</span>
              {{ getDateRangeString.label }}
            </div>
          </div>
          <div
            class="fc-report-author-txt"
            v-for="(value, key, index) in dataObj"
            :key="index"
          >
            {{ `${key}:` }}
            <span class="fw6 pdf-left-margin">
              {{ ` ${value}` }}
            </span>
          </div>
        </div>
      </div>
    </div>
    <portal
      :to="'widget-datepicker' + config.widget.id"
      slim
      class="tabulardata-datepicker"
      v-if="config && widgetDatePicker"
    >
      <new-date-picker
        :key="datePickerCompKey"
        v-if="!hideTimelineFilterInsideWidget && dateFilter"
        :zone="$timezone"
        class="filter-field date-filter-comp-new-report inline"
        :dateObj="dateFilter"
        @date="setDateFilter"
      ></new-date-picker>
    </portal>
    <template v-else>
      <div v-if="!hideTimelineFilterInsideWidget && showDatePicker">
        <new-date-picker
          :disabled="drilldownDatePickerObj ? true : false"
          :key="datePickerCompKey"
          :zone="$timezone"
          class="filter-field date-filter-comp-new-report"
          :dateObj="dateFilter"
          @date="setDateFilter"
        ></new-date-picker>
      </div>
    </template>

    <div
      class="mT5 modular-user-filter"
      v-if="resultObj && resultObj.report.userFilters && userFilters !== null"
    >
      <ModularUserFilterRender
        :widgetConfig="config"
        @emitChangedValue="setUserFilter"
        :filterConfiguration="userFilters"
      ></ModularUserFilterRender>
    </div>

    <Spinner v-if="loading" :show="loading" size="80"></Spinner>

    <div
      v-else-if="failed"
      style="margin-top: 30px;opacity: 0.7;font-size: 13px;padding: 50px;line-height: 25px;text-align: center;"
    >
      <div>{{ $t('home.dashboard.data_loading_failed') }}</div>
    </div>

    <!-- if no data is available -->
    <div v-else-if="noData" class="report-no-data-cotainer-wrapper">
      <!-- FOR NO DATA Fnewchart isn't rendered so breadcrumb inside it is not shown,Cannot drill back -->
      <!-- SO Display Breadcrumb along with no data here -->
      <drilldown-breadcrumb
        v-if="drilldownParams"
        :drilldownParams="drilldownParams"
        @crumbClick="handleCrumbClick"
      >
      </drilldown-breadcrumb>
      <div class="report-no-data-container">
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
    </div>
    <!-- -->

    <!-- chart generation start -->
    <div
      :class="{
        scrollable:
          reportObj &&
          reportObj.options &&
          reportObj.options.settings.chart === false,
      }"
      v-else
    >
      <f-chart-settings
        v-if="false"
        :settings="reportObj.options.settings"
        :showChartMode="
          reportObj.data && Object.keys(reportObj.data).length > 2
        "
        :showAlarm="false"
        :showSafelimit="false"
      ></f-chart-settings>
      <div
        v-if="reportObj.options.settings.chart === true"
        class="fc-single-chart-pdf"
      >
        <!-- TODO :change event to @clickAction event -->
        <f-new-chart
          v-if="!isHeatMap && !isTreeMap"
          @drilldown="handleClickAction"
          :showWidgetLegends="
            reportObj &&
              reportObj.options &&
              reportObj.options.widgetLegend.show
          "
          ref="newChart"
          :isWidget="config || isWidget"
          :width="config && config.width ? config.width : null"
          :resultObj="resultObj"
          :config="config"
          :height="config && config.height ? config.height : height"
          :data="reportObj.data"
          :options="reportObj.options"
          :alarms="reportObj.alarms"
          :dateRange="reportObj.dateRange"
          :hidecharttypechanger="$mobile || isWidget"
          :drilldownParams="drilldownParams"
          :widgetBodyDimension="widgetBodyDimension"
          :updateWidget="updateWidget"
          :item="item"
          @crumbClick="handleCrumbClick"
        ></f-new-chart>
        <FNewHeatMap
          v-if="isHeatMap && !isTreeMap"
          ref="heatmap"
          :config="config"
          :width.sync="widgetWidth"
          :height.sync="widgetHeight"
          :resultObject="resultObj"
          :reportObject="reportObj"
        ></FNewHeatMap>
        <FTreeMap
          v-if="!isHeatMap && isTreeMap"
          ref="treemap"
          :config="config"
          :resultObject="resultObj"
          :width.sync="widgetWidth"
          :height.sync="widgetHeight"
          :reportObject="reportObj"
        ></FTreeMap>
      </div>
      <div
        v-if="
          reportObj &&
            (typeof config === 'undefined' ||
              reportObj.options.settings.chart === false) &&
            (typeof hideTabs === 'undefined' || !hideTabs) &&
            !showOnlyImage &&
            !excludeParamsObj.table &&
            !isWidget
        "
      >
        <portal
          :to="'widget-period' + config.widget.id"
          slim
          class="tabulardata-datepicker"
          v-if="config"
        >
          <div class="widget-tabular-date">
            <div class="f14 fw6">
              <span>{{
                getDateRangeString.dateFieldLabel !== ''
                  ? getDateRangeString.dateFieldLabel.concat(':')
                  : ''
              }}</span>
              {{ getDateRangeString.label }}
            </div>
          </div>
        </portal>
        <modular-new-tabular-report
          v-if="
            failed === false &&
              loading === false &&
              reportObj.options.settings.chart === false &&
              !isPrinting
          "
          ref="newTable"
          :reportObject="resultObj"
          :reportConfig="reportObj"
          class="new-analytics-table"
        ></modular-new-tabular-report>
        <template v-if="isPrinting">
          <criteria-table
            v-if="criteriaObj"
            :criteriaObj="criteriaObj"
          ></criteria-table>
          <div class="tabular-print-heading">
            {{ $t('home.dashboard.report_summary') }}
          </div>
          <modular-new-tabular-report
            ref="newTable"
            :printReverse="true"
            :reportObject="resultObj"
            :reportConfig="reportObj"
            class="new-analytics-table fc-report-pdf-tabular"
          ></modular-new-tabular-report>

          <div class="underlying-data-cut-page">
            <div class="tabular-print-heading mL0 mT0 mB20">
              {{ resultObj.report.name }}
            </div>
            <underlying-workorders
              :module="computedModule"
              :reportObject="reportObj"
              :resultObject="resultObj"
              class="underlying-report-print"
            ></underlying-workorders>
          </div>
        </template>
        <template v-else>
          <el-tabs
            v-if="
              failed === false &&
                loading === false &&
                reportObj.options.settings.chart === true
            "
            v-model="activeReportTab"
            class="report-tab"
          >
            <el-tab-pane
              :label="$t('home.dashboard.tabular_report')"
              name="tabular"
            >
              <modular-new-tabular-report
                ref="newTable"
                :reportObject="resultObj"
                :reportConfig="reportObj"
                class="new-analytics-table"
              ></modular-new-tabular-report>
            </el-tab-pane>
            <el-tab-pane
              :label="$t('home.dashboard.underlying') + ' ' + getModuleString"
              name="underlying"
            >
              <underlying-workorders
                ref="underlyingWorkOrders"
                :module="computedModule"
                :reportObject="reportObj"
                :resultObject="resultObj"
              ></underlying-workorders>
            </el-tab-pane>
          </el-tabs>
        </template>
        <!--  -->
      </div>
    </div>
    <!-- chart generation end -->
    <el-dialog
      :title="listView.title || 'VIEW'"
      v-if="showListView && listView"
      :visible.sync="showListView"
      :append-to-body="true"
      custom-class="f-popup-view fc-dialog-center-body-p0 fc-dialog-center-body-m0 fc-card-popup-list-view"
      top="0%"
      :before-close="closeListView"
    >
      <template slot="title">
        <div class="flex-middle justify-content-space">
          <div class="label-txt-black fwBold">
            {{ listView.title || 'VIEW' }}
          </div>
        </div>
      </template>
      <CustomTable
        class="list-view-widget custom-view-table"
        :moduleName="listView.moduleName"
        :viewname="'all'"
        :viewMode="true"
        :filterJSON="listView.filters"
      >
      </CustomTable>
    </el-dialog>
    <email-report
      v-if="emailReportVisibility"
      :visibility.sync="emailReportVisibility"
      :report="report"
    />
  </div>
</template>
<script>
import EmailReport from 'pages/report/forms/DashboardEmailReport'
import { isEmpty } from '@facilio/utils/validation'
import NewDatePicker from 'src/pages/new-dashboard/components/date-picker/NewDatePicker.vue'
import cloneDeep from 'lodash/cloneDeep'
import FNewChart from 'src/pages/new-dashboard/components/charts/FNewChart.vue'
import FNewHeatMap from 'src/pages/energy/analytics/components/FNewHeatMap'
import FTreeMap from 'pages/energy/analytics/components/FTreeMap'
import NewDataFormatHelper from 'src/pages/report/mixins/NewDataFormatHelper'
import ModularNewTabularReport from 'pages/report/components/ModularNewTabularReport'
import NewDateHelper from 'src/pages/new-dashboard/components/date-picker/NewDateHelper.js'
import UnderlyingWorkorders from 'src/pages/report/UnderlyingWorkorders'
import FChartSettings from 'newcharts/components/FChartSettings'
import ModularAnalyticmixin from 'src/pages/energy/analytics/mixins/ModularAnalyticmixin'
import ModularUserFilterMixin from 'src/pages/report/mixins/modularUserFilter'
import ModularUserFilterRender from 'src/pages/energy/analytics/components/ModularUserFilterRender'
import CriteriaTable from 'src/pages/report/CriteriaTable'
import moment from 'moment-timezone'
import NewReportSummaryHelper from 'src/pages/report/mixins/NewReportSummaryHelper'
import { isEqual } from 'lodash'
import CustomTable from 'src/components/CustomViewTableComponent'
import ReportDrilldownMixin from 'src/pages/report/mixins/ReportDrilldownMixin'
import DrilldownBreadcrumb from 'src/pages/report/components/DrilldownBreadcrumb'
import { API } from '@facilio/api'
import Spinner from '@/Spinner'
export default {
  props: {
    showHeadingOnlyForPdf: {
      type: Boolean,
      default: false,
    },
    updateWidget: {
      type: Function,
    },
    widgetBodyDimension: {
      type: Object,
    },
    loadImmediately: {
      type: Boolean,
      default: true,
    },
    hideTimelineFilterInsideWidget: {},
    id: {},
    item: {},
    config: {},
    tabular: {},
    hideTabs: {},
    dbFilterJson: {},
    dbTimelineFilter: {},
    isWidget: {},
    qs: {},
    ruleInfo: {},
    componentVisibleInViewPort: {
      type: Boolean,
      default: true,
    },
  },
  mixins: [
    NewDataFormatHelper,
    ModularAnalyticmixin,
    ModularUserFilterMixin,
    NewReportSummaryHelper,
    ReportDrilldownMixin,
  ],
  components: {
    EmailReport,
    NewDatePicker,
    ModularNewTabularReport,
    UnderlyingWorkorders,
    FNewChart,
    ModularUserFilterRender,
    FChartSettings,
    CriteriaTable,
    FNewHeatMap,
    FTreeMap,
    CustomTable,
    DrilldownBreadcrumb,
    Spinner,
  },
  data() {
    return {
      reportPdfData: [],
      metaField: [],
      displayObject: {},
      isFilterApplied: false,
      pdfloading: false,
      isReportLoadedOnes: false,
      emailReportVisibility: false,
      activeReportTab: 'tabular',
      loading: true,
      failed: false,
      noData: false,
      reportObj: null,
      resultObj: null,
      showReportOptions: false,
      dateFilter: null,
      userFilterApplied: null,
      userFilters: null,
      moduleName: null,
      moduleId: null,
      moduleResourceField: null,
      showDatePicker: false,
      firstLoad: true,
      datePickerCompKey: 1, //new refresh impl, remove syncPicker soon
      criteriaObj: null,
      height: null,
      widgetWidth: null,
      widgetHeight: null,
      showListView: false,
      listView: null,
      // chartDimensions:{
      //   'width': 980,
      //   'height': 800
      // },
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
        },
        chosenColors: 'Default',
        minValue: null,
        maxValue: null,
        showGrandParent: true,
        showColorScale: true,
        showWidgetLegends: false,
        treemapTextMode: 2,
      },
      excludeParamsObj: {},
    }
  },
  created() {
    //on report load , db date filter overrides report date filter . on further change of either ,the last change is applied
    if (!isEmpty(this.dbTimelineFilter)) {
      let pickerObj = NewDateHelper.getDatePickerObject(
        this.dbTimelineFilter.operatorId,
        this.dbTimelineFilter.dateValueString
      )
      this.dateFilter = pickerObj //don't init api call here, just set filter state, report intial load will take filter param from this
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
  },
  mounted() {
    let { excludeParams = {} } = this.$route.query
    this.excludeParamsObj = !isEmpty(excludeParams)
      ? JSON.parse(excludeParams) ?? {}
      : {}

    const { loadImmediately } = this ?? {}
    if (loadImmediately == true) {
      // Two cases,
      // 1) Dashboard, load after asking the rules/execute API.
      // 2) Reports, load the data immediately.
      this.init()
    }
    if (this.isPrinting) {
      this.height = 300
    }
  },
  computed: {
    dataObj() {
      let { displayObject = {} } = this || {}
      return displayObject
    },
    widget() {
      const { item = {} } = this ?? {}
      const { widget = {} } = item
      return widget
    },
    report() {
      const {
        resultObj: { report },
      } = this
      return report ?? {}
    },
    allFilters() {
      return {
        dbFilterJson: this.dbFilterJson,
        dbTimelineFilter: this.dbTimelineFilter,
        ruleInfo: this.ruleInfo,
      }
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
    isHeatMap() {
      if (
        this.reportObj &&
        this.reportObj.options &&
        this.reportObj.options.type === 'heatmap'
      ) {
        this.widgetWidth = this.$el.clientWidth
        this.widgetHeight = this.$mobile
          ? this.config.height
          : this.$el.clientHeight - 30
        return true
      } else {
        return false
      }
    },
    isTreeMap() {
      if (
        this.reportObj &&
        this.reportObj.report &&
        this.reportObj.report.analyticsType === 7
      ) {
        this.widgetWidth = this.$el.clientWidth
        this.widgetHeight = this.$mobile
          ? this.config.height
          : this.$el.clientHeight - 30
        return true
      } else {
        return false
      }
    },
    filters() {
      if (this.$route.query.filters) {
        return this.$route.query.filters
      }
      return null
    },
    computedModule() {
      let module = {}
      module['moduleName'] = this.moduleName
      module['moduleId'] = this.moduleId
      if (this.moduleResourceField) {
        module['resourceField'] = this.moduleResourceField
      }
      return module
    },
    isPrinting() {
      if (!this.showHeadingOnlyForPdf) {
        return this.$route.query.print || this.$route.query.daterange // daterange check temp
      }
      return null
    },
    isGroupByCrossModule() {
      if (
        this.resultObj.report.dataPoints[0].hasOwnProperty('groupByFields') &&
        this.resultObj.report.dataPoints[0].groupByFields !== null
      ) {
        let groupField = this.resultObj.report.dataPoints[0].groupByFields[0]
        if (
          groupField.field.module.name === this.resultObj.report.module.name ||
          (this.resultObj.report.module.extendModule &&
            groupField.field.module.name ===
              this.resultObj.report.module.extendModule.name)
        ) {
          return false
        }
        return true
      }
      return false
    },
    isXFieldCrossModule() {
      let xAxis = this.resultObj.report.dataPoints[0].xAxis
      if (
        xAxis.field.module.name === this.resultObj.report.module.name ||
        (this.resultObj.report.module.extendModule &&
          xAxis.field.module.name ===
            this.resultObj.report.module.extendModule.name)
      ) {
        return false
      }
      return true
    },
    reportId() {
      const { widget } = this ?? {}
      if (!isEmpty(widget)) {
        const {
          dataOptions: { newReportId },
        } = widget
        return newReportId
      } else if (id) {
        return id
      } else {
        return null
      }
    },
    appliedDateRange() {
      if (this.$route.query.daterange) {
        return JSON.parse(this.$route.query.daterange)
      }
      return ''
    },
    getModuleString() {
      if (
        this.resultObj.report.moduleType !== -1 &&
        this.resultObj.moduleTypes &&
        this.resultObj.moduleTypes.length !== 0
      ) {
        let moduleName = this.resultObj.moduleTypes.filter(
          type => type.type === this.resultObj.report.moduleType
        )[0].displayName
        return moduleName
      } else if (
        this.resultObj.report.module &&
        this.resultObj.report.module.custom
      ) {
        return this.resultObj.report.module.displayName
      } else {
        return this.resultObj.report.module.name + 's'
      }
    },
    appliedChartType() {
      // Temp...will be changed for pdf once temp report support is implemented
      return this.$route.query.charttype
    },
    getDateRangeString() {
      let temp = {
        dateFieldLabel: '',
        label: '',
      }
      if (this.resultObj) {
        let dateField = this.resultObj.report.dataPoints[0].dateField
        let xField = this.resultObj.report.dataPoints[0].xAxis
        let dateFieldString = ''
        if (xField) {
          if (xField && dateField && xField.fieldId !== dateField.fieldId) {
            temp['dateFieldLabel'] = dateField.label
          }
          if (dateField) {
            let a = moment(this.resultObj.report.dateRange.startTime)
            dateFieldString = a.format('DD/MM/YYYY') + ' ' + 'to '
            a = moment(this.resultObj.report.dateRange.endTime)
            dateFieldString = dateFieldString + a.format('DD/MM/YYYY') + ' '
            temp['label'] = dateFieldString
            return temp
          }
        }
        return temp
      }
      return temp
    },
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
  },
  watch: {
    userFilters(newValue) {
      const { filters } = this.$route.query
      if (
        !isEmpty(newValue) &&
        this.showHeadingOnlyForPdf &&
        !isEmpty(filters)
      ) {
        this.pdfUserFilter(JSON.parse(filters))
      }
    },
    //when widget at bottom of dashboard is scrolled up , this prop changes from false to true,
    componentVisibleInViewPort(componentVisibleInViewPort) {
      if (
        componentVisibleInViewPort &&
        (this.isReportLoadedOnes || this.loadImmediately)
      ) {
        this.init()
      }
    },
    allFilters(newVal, oldVal) {
      const { loading } = this
      if (this.isReportLoadedOnes == false) {
        this.isReportLoadedOnes = true
      }
      if (!isEqual(newVal, oldVal) || loading) {
        const { dbTimelineFilter: timelineFilter } = newVal
        const { startTime, endTime, dateOperator } = timelineFilter
        if (
          !isEmpty(startTime) &&
          !isEmpty(endTime) &&
          !isEmpty(dateOperator)
        ) {
          this.dateFilter = NewDateHelper.getDatePickerObject(dateOperator, [
            startTime,
            endTime,
          ])
          this.datePickerCompKey++
        }
        this.init()
      }
    },
    reportId: {
      handler(newData, oldData) {
        this.dateFilter = null
        this.showDatePicker = false
        this.userFilters = null
        this.resetDrillDown()
        this.init(false)
      },
    },
    filters: {
      handler(newData, oldData) {
        if (newData !== oldData) {
          this.init(false)
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
  },
  methods: {
    getFields(fieldId) {
      const fieldName = (this.metaField || []).reduce(
        (fieldNameList, field) => {
          return fieldId === field.id
            ? [...fieldNameList, field.name]
            : fieldNameList
        },
        []
      )
      return fieldName[0]
    },

    async pdfUserFilter(filter) {
      this.isFilterApplied = true
      const validFilterObj = (Object.keys(filter) || []).reduce(
        (filterObj, userFilter) => {
          let temp = {}
          const selectedVal = filter[userFilter].value
          if (!isEmpty(selectedVal)) {
            temp[userFilter] = selectedVal
          }
          return { ...filterObj, ...temp }
        },
        {}
      )

      for (let i = 0; i < this.userFilters?.length; i++) {
        let currentEle = this.userFilters[i]

        let fieldName = !isEmpty(currentEle.criteria)
          ? this.$getProperty(currentEle, 'criteria.conditions.1.fieldName') ||
            ''
          : this.getFields(currentEle.fieldId)

        const selectedValues = validFilterObj[fieldName]
        let displayVal = []

        if (!isEmpty(selectedValues)) {
          displayVal = currentEle.allValues
            .filter(value => {
              if (selectedValues.includes(Object.keys(value)[0])) {
                return true
              }
              return false
            })
            .map(value => {
              return value[Object.keys(value)[0]]
            })
          if (isEmpty(displayVal)) {
            const filters = JSON.stringify({
              [fieldName]: filter[fieldName],
            })
            let { data, error } = await API.get(
              `/v3/picklist/${currentEle.lookupModuleName}`,
              {
                filters: filters,
              }
            )
            if (!error) {
              displayVal = data.pickList.map(ele => {
                return ele.label
              })
            }
          }
          if (!isEmpty(displayVal)) {
            this.displayObject = {
              ...this.displayObject,
              [currentEle.name]: displayVal,
            }
          }
        }
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

    setUserFilter(val) {
      console.log('SETTING USER FILTER VALUE')
      this.userFilters = val
      this.$forceUpdate()
      this.initchart()
    },
    handleClickAction(val) {
      let {
        clickActionEnum,
        isShowListAtDrillEnd,
      } = this.resultObj.report.reportSettings
      if (clickActionEnum == 'DRILLDOWN') {
        this.handleReportDrillDown(val, isShowListAtDrillEnd)
      } else if (clickActionEnum == 'LIST') {
        this.drilltoList(val)
      } else if (clickActionEnum == 'DASHBOARD_ACTION') {
        const { id, name, value, index: dimensionIndex } = val ?? {}
        const reportObj = cloneDeep(this.reportObj)
        const resultObj = cloneDeep(this.resultObj)
        let {
          report: {
            dataPoints: [dataPoint],
          },
        } = reportObj
        const {
          reportData: { data: dimensionArray },
        } = resultObj
        const {
          item: { id: widgetId },
          widget: { link_name: linkName },
        } = this ?? {}

        let {
          xAxis: { field, fieldName, moduleName },
          allCriteria: criteria,
          groupByFields,
        } = dataPoint ?? {}

        let groupBy, groupByFieldName, groupByModuleName
        let dataTypeEnum
        if (!isEmpty(field)) {
          ;({ dataTypeEnum } = field)
          if (dataTypeEnum == 'LOOKUP' || dataTypeEnum == 'LOOKUP_SIMPLE') {
            const {
              name: lookupField,
              lookupModule: { name: lookupModule },
            } = field
            moduleName = lookupModule
            fieldName = lookupField
          }
        }
        if (!isEmpty(groupByFields)) {
          const [{ dataTypeEnum, lookupMap, field }] = groupByFields

          groupByFieldName = field?.name
          groupByModuleName = field?.lookupModule?.name

          if (dataTypeEnum == 'LOOKUP' || dataTypeEnum == 'LOOKUP_SIMPLE') {
            groupBy = Object.keys(lookupMap).find(k => {
              return lookupMap[k] == id
            })
          }
          // else if (dataTypeEnum == 'ENUM' || dataTypeEnum == 'MULTI_ENUM') {
          //   //"reportObj.report.dataPoints.0.groupByFields.0.enumMap"
          //   // groupBy = Object.keys(enumMap).find(k => {
          //   //   lookupMap[k] == id
          //   // })
          // }
        }
        const getDimension = () => {
          const { dataArray } = val
          if (dataTypeEnum == 'DATE_TIME') {
            const x = moment(dataArray[dimensionIndex], 'DD-MM-YYYY')
              .tz(this.$timezone)
              .valueOf()
            return x ?? ''
          } else {
            const { X: x } = dimensionArray[dimensionIndex]
            return x
          }
        }
        const action = {
          widgetLinkName: linkName,
          criteria: criteria,
          dimension: getDimension(),
          fieldName: fieldName,
          moduleName: moduleName,
          id: widgetId,
          groupBy: groupBy,
          groupByModuleName,
          groupByFieldName,
        }
        this.$emit('action', action)
      }
    },
    handleReportDrillDown(val, isShowListAtDrillEnd) {
      let reloadReport = this.drillReport(
        val,
        isShowListAtDrillEnd,
        this.resultObj,
        this.reportObj
      )
      if (reloadReport) {
        this.init()
      }
      if (!reloadReport) {
        this.drilltoList(val, true)
      }
    },
    drilltoList(val, isShowListAtEnd) {
      if (this.isXFieldCrossModule || this.isGroupByCrossModule) {
        console.log('Drill down not enabled for cross module')
      } else {
        let appNameFromUrl = window.location.pathname.slice(1).split('/')[0]
        if (appNameFromUrl === 'operations') {
          let filters = this.handleBasicDrillDown(
            val,
            this.resultObj,
            this.reportObj,
            this.filters,
            true
          )
          this.listView = {}
          this.listView['filters'] = filters
          this.listView['moduleName'] = this.moduleName
          this.listView['title'] = ''
          this.showListView = true
          return
        }

        if (isShowListAtEnd) {
          let drilldown_filter = {}
          for (let filter_val in this.drilldownactionslist) {
            let drill_down_obj = this.drilldownactionslist[filter_val]
            let key_name = Object.keys(drill_down_obj)[0]
            drilldown_filter[key_name] = { ...drill_down_obj[key_name] }
          }
          this.handleBasicDrillDown(
            val,
            this.resultObj,
            this.reportObj,
            JSON.stringify(drilldown_filter)
          )
        } else {
          this.handleBasicDrillDown(
            val,
            this.resultObj,
            this.reportObj,
            this.filters
          )
        }
      }
    },
    handleCrumbClick(crumbIndex) {
      this.drillFromCrumb(crumbIndex)
      this.init()
    },
    closeListView() {
      this.listView = null
      this.showListView = false
    },
    setDateFilter(dateFilter) {
      this.dateFilter = dateFilter
      this.$emit('timelineFilterChange', dateFilter)
      if (this.userFilterApplied) {
        this.applyFilter(this.userFilterApplied)
      } else {
        this.initchart()
      }
    },
    init() {
      //when dashboard in lazy mode and widget ain't visible dont load
      if (!this.componentVisibleInViewPort) {
        return
      }

      this.initchart()
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

    async initchart() {
      if (this.printQuery) {
        if (
          this.printQuery.underlyingData &&
          this.printQuery.underlyingData === true
        ) {
          this.activeReportTab = 'underlying'
        }
      }
      this.loading = true
      this.failed = false
      this.noData = false
      if (this.$refs['underlyingWorkOrders']) {
        this.$refs['underlyingWorkOrders'].rerender()
      }
      let params = { reportId: this.reportId }
      if (
        this.firstLoad &&
        this.appliedDateRange.startTime &&
        this.appliedDateRange.endTime
      ) {
        this.firstLoad = false
        this.dateFilter = NewDateHelper.getDatePickerObject(
          this.appliedDateRange.operatorId,
          '' +
            this.appliedDateRange.startTime +
            ',' +
            this.appliedDateRange.endTime +
            ''
        )
      }
      if (!isEmpty(this.dateFilter)) {
        params.startTime = this.dateFilter.value[0]
        params.endTime = this.dateFilter.value[1]
      }
      if (this.filters) {
        params['filters'] = this.filters
      }
      if (!isEmpty(this.dbFilterJson)) {
        params['filters'] = JSON.stringify(this.dbFilterJson)
      }
      if (this.userFilters) {
        params.userFilters = this.userFilters
      }
      const { ruleInfo } = this ?? {}
      if (!isEmpty(ruleInfo)) {
        params['ruleInfo'] = ruleInfo
      }

      if (this.drilldownParams) {
        params.drilldownParams = this.drilldownParams
      }

      await API.put(`/v3/report/execute`, params).then(({ data, error }) => {
        if (error) {
          console.log('---Error---', error)
          this.loading = false
          this.failed = true
        } else {
          console.log('This is for workorder reports')
          if (data.criteriaData) {
            this.criteriaObj = data.criteriaData
          }
          let result = data
          this.reportPdfData = data
          this.pdfloading = true
          if (
            this.config &&
            this.config.dashboardObj &&
            this.config.dashboardObj.id === 1965
          ) {
            result.convertTo = 'hour'
          }
          result = this.specialHandler(result)
          this.moduleName = result.module.name
          this.moduleId = result.module.moduleId
          if (result.report.userFilters && this.userFilters === null) {
            let conf = {}
            let userFilters = null
            userFilters = result.report.userFilters
            conf['userFilters'] = userFilters
            this.loadAllValues(conf)
          }
          let self = this
          if (result.reportData.data.length === 0) {
            self.noData = true
            self.loading = false

            if (result.report.dateOperator !== -1) {
              if (isEmpty(self.dateFilter)) {
                const getTimelineFilter = () => {
                  if (!isEmpty(self.dbTimelineFilter)) {
                    // Using the date of global timeline filter.
                    const { dateOperator } = self.dbTimelineFilter
                    return NewDateHelper.getDatePickerObject(dateOperator)
                  } else {
                    // Using the date attached to the report.
                    return NewDateHelper.getDatePickerObject(
                      result.report.dateOperator,
                      result.report.dateValue
                    )
                  }
                }
                const datePickerObject = getTimelineFilter()
                result['dateRange'] = datePickerObject
                self.dateFilter = datePickerObject
              } else {
                result['dateRange'] = self.dateFilter
                if (this.drilldownDatePickerObj) {
                  result['dateRange'] = self.drilldownDatePickerObj
                }
              }
              self.showDatePicker = true
            }
            result.xAggr = result.report.xAggr
            self.resultObj = result
            // TO DO . FWidget should not depend on report obj emitted by this component
            self.$emit('reportLoaded', result.report, result)
          } else {
            if (result.report.dateOperator !== -1) {
              if (this.dateFilter === null) {
                let datePickerObject = NewDateHelper.getDatePickerObject(
                  result.report.dateOperator,
                  result.report.dateValue
                )
                result['dateRange'] = datePickerObject
                self.dateFilter = datePickerObject
                self.showDatePicker = true
              } else {
                result['dateRange'] = self.dateFilter
                if (this.drilldownDatePickerObj) {
                  result['dateRange'] = this.drilldownDatePickerObj
                  //on initial load cases . drill down . won't exist. only on subsequent reloads like this.
                  //if drilldown present take dateRange from last drilled->point .not UI date picker state
                }
              }
              this.showDatePicker = true
            } else {
              result['dateRange'] = self.dateFilter
            }
            result.xAggr = result.report.xAggr
            self.reportObj = this.prepareReport(result)
            self.reportObj['report'] = result.report
            //self.resultObj is old result state here , from previous report load
            //check why this is needed in execute report

            if (!this.drilldownParams) {
              //do not persist any options from old resultObj or from chartState while drilling down
              this.handleAxisChartState(self.reportObj, self.resultObj, result)
            }
            self.resultObj = result //setting resultobj to current response result state

            if (self.appliedChartType) {
              self.reportObj.options.data.type = this.appliedChartType
            }
            if (self.isPrinting) {
              if (!self.reportObj.options.size) {
                self.reportObj.options.size = {}
              }
              self.reportObj.options.size.height = 300
            }
            if (
              self.reportObj &&
              self.reportObj.report &&
              self.reportObj.report.analyticsType === 7
            ) {
              self.reportObj.options.heatMapOptions = self.heatMapOptions
            }
            console.log('before emit ')
            self.$emit('reportLoaded', self.reportObj, self.resultObj)
            self.loading = false
          }
        }
      })
    },
  },
}
</script>
<style lang="scss" scoped>
.pdf-margin-top {
  margin-top: 5px;
}
.pdf-left-margin {
  margin-left: 5px;
}
.pdf-text-left {
  text-align: left;
}
.date-filter-comp-new-report {
  position: absolute;
  top: 50px;
  right: 0px;
}
</style>
<style lang="scss">
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

.report-no-data-container {
  font-size: 13px;
  padding: 80px 50px 50px 50px;
  line-height: 25px;
  text-align: center;
}
</style>
