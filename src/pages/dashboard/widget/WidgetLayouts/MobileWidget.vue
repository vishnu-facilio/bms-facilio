<template>
  <div
    :id="widget.id ? widget.id : -1"
    class="fc-widget f-mobile-widget"
    style="width: 100%; height: 100%"
    :class="widget.header.title"
    v-observe-visibility="
      isLazyDashboard ? handleViewportVisibilityChange : false
    "
    :style="
      widget.type &&
      widget.type === 'static' &&
      (widget.dataOptions.staticKey === 'profilecard' ||
        widget.dataOptions.staticKey === 'weathercard' ||
        widget.dataOptions.staticKey === 'energycard' ||
        widget.dataOptions.staticKey === 'energycostaltayer' ||
        widget.dataOptions.staticKey === 'weathercardaltayer' ||
        widget.dataOptions.staticKey === 'cumulativesavings' ||
        widget.dataOptions.staticKey === 'weathermini' ||
        widget.dataOptions.staticKey === 'profilemini' ||
        widget.dataOptions.staticKey === 'carbonmini' ||
        widget.dataOptions.staticKey === 'energycostmini' ||
        widget.dataOptions.staticKey === 'readingcard')
        ? 'border:0px'
        : 'border:solid 1px #eae8e8'
    "
  >
    <div
      class="row fc-widget-header"
      v-if="viewHeader(widget.header)"
      @click="sendDataToMobile()"
    >
      <div class="col-8" style="overflow: hidden">
        <el-input
          :autofocus="true"
          class="create-dashboard-input-title f18 fc-widget-label ellipsis"
          v-model="widget.header.title"
          placeholder=""
          @blur="blurevent()"
          v-if="mode && changeheader"
        ></el-input>
        <div
          class="f18 fc-widget-label"
          @dblclick="editheader('edit')"
          v-else-if="moduleName === 'workorder'"
        >
          {{ widget.header.title }}
        </div>
        <div class="f18 fc-widget-label" @dblclick="editheader('edit')" v-else>
          {{ widget.header.title }}
        </div>
        <div class="fc-widget-sublabel" v-if="widget.header.subtitle">
          {{ setSubTitle || emptyTitle }}
        </div>
        <div class="fc-widget-sublabel" v-else>{{ emptyTitle }}</div>
      </div>
      <div class="col-4" v-if="widget.type != 'static' && !isReadOnly">
        <div
          class="pull-right externalLink"
          style=""
          v-tippy
          :title="$t('home.dashboard.open_full_view')"
        >
          <img
            src="~statics/report/arrow-report.svg"
            class="chart-icon-report"
            @click="openReport(widget.dataOptions.reportId, widget)"
          />
        </div>
        <div
          class="pull-right chart-delete-icon"
          style=""
          v-tippy
          title="Delete widget"
        >
          <i
            class="el-icon-delete f18"
            @click="deleteChart(widget, dashboard)"
          ></i>
        </div>
        <div
          class="pull-right chart-icon-hide"
          style=""
          v-tippy
          title="Hide widget"
        >
          <i
            class="el-icon-view f18"
            @click="hideWidget(widget, dashboard)"
          ></i>
        </div>
      </div>
    </div>
    <!-- <i class="el-icon-delete f18" v-else @click="addwidgetHedaer()"></i> -->
    <div
      class="en-divider"
      :style="
        widget.type &&
        widget.type === 'static' &&
        (widget.dataOptions.staticKey === 'profilecard' ||
          widget.dataOptions.staticKey === 'weathercard' ||
          widget.dataOptions.staticKey === 'energycard' ||
          widget.dataOptions.staticKey === 'energycostaltayer' ||
          widget.dataOptions.staticKey === 'weathercardaltayer' ||
          widget.dataOptions.staticKey === 'cumulativesavings' ||
          widget.dataOptions.staticKey === 'weathermini' ||
          widget.dataOptions.staticKey === 'profilemini' ||
          widget.dataOptions.staticKey === 'carbonmini' ||
          widget.dataOptions.staticKey === 'energycostmini' ||
          widget.dataOptions.staticKey === 'readingcard' ||
          widget.dataOptions.staticKey === 'fahuStatusCard' ||
          widget.dataOptions.staticKey === 'fahuStatusCard1')
          ? 'display:none;'
          : 'display:block;'
      "
    ></div>
    <spinner :show="loading" size="80"></spinner>
    <div
      class="self-center"
      v-bind:class="{
        'fc-widget-body': widget.header.title !== null,
        'fc-widget-dragable-body': widget.header.title === null,
      }"
    >
      <div v-if="!loading && failed">
        {{ $t('common._common.load_data_failed') }}
      </div>
      <div
        class="pull-right chart-delete-icon"
        style=""
        v-tippy
        title="Delete widget"
      >
        <i
          class="el-icon-delete f18"
          @click="deleteChart(widget, dashboard)"
        ></i>
      </div>
      <div
        class="tooltip-data"
        v-for="(toolTip, index) in tooltipdata"
        :key="index"
      >
        {{ tooltipdata.value }}
      </div>
      <component
        ref="widgetComponent"
        :is="widgetType"
        @tooltipdata="getTooltipData"
        :config="{
          widget: widget,
          currentDashboard: currentDashboard,
          intervalUpdate: intervalUpdate,
          id: widget.dataOptions.reportId,
          width: Math.round(widgetSize.width),
          height: Math.round(widgetSize.height),
          isReportUpdateFromDashboard: true,
        }"
        :reportId="$getProperty(widget, 'dataOptions.newReportId')"
        :isMobileDashboard="true"
        @onload="reportLoaded"
        v-bind:class="{ 'fc-secondChart': multiChartMapping[widget.id] }"
        @dateFilterUpdated="dateFilterUpdated"
        @reportUtil="getReportutil"
        @reportLoaded="getNewReportObj"
        :mode1="mode"
        v-show="showTabularReport"
        :getReportUtil="sendUtil"
        :dbFilterJson="dbFilterJson"
        :dbTimelineFilter="dbTimelineFilter"
        :isLazyDashboard="isLazyDashboard"
        :isVisibleInViewport="isVisibleInViewport"
      >
      </component>
      <f-tabular-report
        :reportObject="reportObject"
        v-if="report && report.options.type === 'tabular'"
      ></f-tabular-report>
    </div>
  </div>
</template>
<script>
import FReport from 'pages/report/components/FReport'
import FNewReport from 'pages/report/components/FNewReport'
import FListWidget from 'pages/dashboard/widget/FListWidget'
import FCountWidget from 'pages/dashboard/widget/FCountWidget'
import FMapWidget from 'pages/dashboard/widget/FMapWidget'
import FStaticWidget from 'pages/dashboard/widget/FStaticWidget'
import FcardsWidget from 'pages/dashboard/widget/Fcards'
import FWebWidget from 'pages/dashboard/widget/FWebWidget'
import FTabularReport from 'pages/report/components/FTabularReport'
import moment from 'moment'
import ModuleNewReport from 'src/pages/report/MobileModuleNewReport'
import FGraphicsWidget from 'pages/dashboard/widget/FGraphicsWidget'
import NewDateHelper from '@/mixins/NewDateHelper'
import PivotTableWrapper from 'src/pages/energy/pivot/PivotTableWrapper'

const types = [
  'chart',
  'newchart',
  'view',
  'map',
  'count',
  'static',
  'web',
  'cards',
  'moduleName',
]

export default {
  props: {
    isLazyDashboard: {
      type: Boolean,
      default: false,
    },
    type: {
      type: String,
      required: true,
      validator: val => types.includes(val),
    },
    widget: {
      type: Object,
      required: true,
      default: () => ({}),
    },
    currentDashboard: {
      type: Object,
    },
    demoData: {
      type: Object,
    },
    grid: {
      type: Object,
    },
    rowHeight: {
      type: Number,
    },
    dashboard: {
      type: Number,
    },
    mode: {
      type: Boolean,
    },
    moduleName: {
      type: String,
    },
    dbFilterJson: {
      type: Object,
    },
    dbTimelineFilter: {
      type: Object,
    },
  },
  components: {
    'f-chart-widget': FReport,
    'f-newchart-widget': FNewReport,
    'f-view-widget': FListWidget,
    'f-count-widget': FCountWidget,
    'f-map-widget': FMapWidget,
    'f-static-widget': FStaticWidget,
    'f-cards-widget': FcardsWidget,
    'f-web-widget': FWebWidget,
    'f-tabular-report': FTabularReport,
    'f-modular-report-widget': ModuleNewReport,
    'f-graphics-widget': FGraphicsWidget,
    'f-pivot-table-wrapper-widget': PivotTableWrapper,
  },
  data() {
    return {
      loading: false,
      failed: false,
      changeheader: false,
      tooltipdata: null,
      newReportObj: null,
      sendUtil: null,
      data: null,
      report: null,
      reportObject: null,
      selectedPeriod: null,
      refreshInterval: null,
      setSubTitle: '',
      emptyTitle: 'Today',
      ChartType: null,
      multiChartMapping: {},
      isVisibleInViewport: false,
    }
  },
  mounted() {},
  destroyed() {
    this.cleanupRefreshInterval()
  },
  computed: {
    isReadOnly() {
      if (this.currentDashboard && this.currentDashboard.readOnly) {
        return true
      }
      return false
    },
    widgetSize() {
      let w = null
      let h = null
      if (this.grid) {
        w = this.grid.w
        h = this.grid.h
      } else {
        w = this.widget.layout.width
        h = this.widget.layout.height
      }
      if (this.$el) {
        if (this.newReportObj) {
          return {
            width: w * (this.rowHeight ? this.rowHeight : 100),
            height:
              this.$el.offsetHeight -
              (this.newReportObj &&
              this.newReportObj.options &&
              this.newReportObj.options.widgetLegend &&
              this.newReportObj.options &&
              this.newReportObj.options.widgetLegend.show
                ? 160
                : 60),
          }
        } else {
          return {
            width: w * (this.rowHeight ? this.rowHeight : 100),
            height:
              this.$el.offsetHeight -
              (this.report && this.report.options.showWidgetLegends ? 120 : 60),
          }
        }
      } else {
        return {
          width: w * (this.rowHeight ? this.rowHeight : 100),
          height: h * (this.rowHeight ? this.rowHeight : 100) + 120,
        }
      }
    },
    widgetType() {
      if (this.widget.dataOptions.newReportId) {
        if (
          (this.widget.dataOptions.newReport &&
          this.widget.dataOptions.newReport.type === 2) || this.widget.dataOptions.reportType === 2
        ) {
          return 'f-modular-report-widget'
        } else if (this.widget?.dataOptions?.reportType === 5) {
          return 'f-pivot-table-wrapper-widget'
        } else {
          return 'f-newchart-widget'
        }
      }
      return 'f-' + this.widget.type + '-widget'
    },
    classObject() {
      if (this.widget.layout.width) {
        let className = 'col-' + this.widget.layout.width * 3
        return className
      } else {
        return 'col-1'
      }
    },
    showTabularReport() {
      return (
        !this.report ||
        (this.report &&
          this.report.options.type !== 'matrix' &&
          this.report.options.type !== 'tabular')
      )
    },
  },
  methods: {
    handleViewportVisibilityChange(isVisible, entry) {
      //one set state from false to true, DONT reset it to false when widget goes out of view again , no need to trigger refresh again. see watcher for isVisibleInViewport inside report comp
      if (isVisible) {
        this.isVisibleInViewport = isVisible
      }
    },
    getNewReportObj(report, result) {
      if (report && result) {
        this.newReportObj = report
        this.$emit('reportLoaded', report, result)
        // console.log('**************** new report', report, result)
      }
    },
    getWidget(widget) {
      if (widget) {
        // if (widget.dataOptions && widget.dataOptions.chartType) {
        //   widget.dataOptions.chartType = 'boolean'
        // }
        return widget
      }
    },
    cleanupRefreshInterval() {
      if (this.refreshInterval) {
        clearInterval(this.refreshInterval)
        this.refreshInterval = null
      }
    },
    initData() {
      let self = this
      console.log('***** rowHeight', this.rowHeight, this.widget.layout)
      self.loading = true
      if (
        !this.selectedPeriod &&
        this.widget.header &&
        this.widget.header.periods
      ) {
        this.selectedPeriod = this.widget.header.periods[0].value
        this.widget.header.subtitle = this.selectedPeriod
      }
      let url = this.widget.dataOptions.dataurl
      if (this.selectedPeriod) {
        url =
          url.indexOf('?') > 0
            ? url + '&period=' + this.selectedPeriod
            : url + '?period=' + this.selectedPeriod
      }
      self.$http
        .get(url)
        .then(function(response) {
          self.loading = false
          console.log('response', response)
          if (response.data.reportData) {
            self.data = {
              xAxis: {},
              yAxis: {},
              name: self.widget.dataOptions.name,
            }
            let reportData = response.data.reportData.filter(function(item) {
              if (item.label) {
                return true
              } else if (item.value && !item.label) {
                item.label = 'unknown'
                return true
              }
              return false
            })
            self.data = reportData
            console.log(self.data)
          } else {
            if (self.widget.dataKey && response.data[self.widget.dataKey]) {
              self.data = response.data[self.widget.dataKey]
            } else if (self.widget.dataKey && response.data.length) {
              self.data = response.data
            } else if (self.demoData) {
              self.data = self.demoData
            } else {
              self.failed = true
            }
          }
        })
        .catch(function(error) {
          if (error) {
            self.loading = false
            self.failed = true
          }
        })
    },
    intervalUpdate(interval) {
      this.widget.dataOptions.refresh_interval = interval
      this.cleanupRefreshInterval()
    },
    reportLoaded(report, reportObject) {
      let self = this
      this.report = report
      this.reportObject = reportObject
      if (self.report !== null) {
        if (self.report.options.timeObject.time !== null) {
          // console.log('********** reportLoaded loop', moment(self.report.options.timeObject.time[0]).calendar(), moment(self.report.options.timeObject.time[1]).calendar())
          // self.setSubTitle = moment(self.report.options.timeObject.time[0]).calendar().split(' at')[0]
          self.setSubTitle = self.timeForm(
            self.report.options.timeObject.time,
            self.report.options.timeObject.field
          )
        } else {
          // console.log('********** reportLoaded loop', self.report.options.datefilter.period)
          // self.setSubTitle = self.getThisperiod(self.report.options.datefilter.period)
        }
      }
    },
    dateFilterUpdated(fulldate) {
      if (this.$refs['widgetChildComponent']) {
        this.$refs['widgetChildComponent'].setDateFilter(fulldate)
      }
    },
    timeForm(time, option) {
      // console.log('********* option', option)
      if (option === 'D') {
        return moment(time[0])
          .calendar()
          .split(' at')[0]
      } else if (option === 'W') {
        // let week = moment(time[0]).calendar().split(' at')[0]
        return (
          moment(time[0])
            .tz(this.$timezone)
            .format('MMM DD, YYYY') +
          ' - ' +
          moment(time[1])
            .tz(this.$timezone)
            .format('MMM DD, YYYY')
        )
      } else if (option === 'M') {
        return (
          moment(time[0])
            .tz(this.$timezone)
            .format('MMM DD, YYYY') +
          ' - ' +
          moment(time[1])
            .tz(this.$timezone)
            .format('MMM DD, YYYY')
        )
      } else if (option === 'Y') {
        return (
          moment(time[0])
            .tz(this.$timezone)
            .format('MMM DD, YYYY') +
          ' - ' +
          moment(time[1])
            .tz(this.$timezone)
            .format('MMM DD, YYYY')
        )
      } else {
        return ''
      }
    },
    getThisperiod(option) {
      if (option === 'D') {
        return 'Today'
      } else if (option === 'W') {
        return 'This Week'
      } else if (option === 'M') {
        return 'This Month'
      } else if (option === 'Y') {
        return 'This Year'
      } else {
        return ''
      }
    },
    selectPeriod(period) {
      this.selectedPeriod = period.value
      this.widget.header.subtitle = period.value
      // this.initData()
    },
    getPeriod(period) {
      console.log('selected' + period)
      period = period.toLowerCase()
      switch (period) {
        case 'current week': {
          let firstDay = moment().startOf('week')
          let endDay = moment().endOf('week')
          return (
            moment(firstDay).format('DD') +
            ' - ' +
            moment(endDay).format('DD MMM YYYY')
          )
        }
        case 'last week': {
          let lastWeekStart = moment()
            .subtract(1, 'weeks')
            .startOf('week')
          let lastWeekEnd = moment()
            .subtract(1, 'weeks')
            .endOf('week')
          return (
            moment(lastWeekStart).format('DD') +
            ' - ' +
            moment(lastWeekEnd).format('DD MMM YYYY')
          )
        }
        case 'today':
          return moment(new Date()).format('DD MMM YYYY')
      }
      return period
    },
    openReport(id, widget) {
      let currentModule = this.getCurrentModule()
      if (widget.type === 'view') {
        let modulePath = ''
        if (widget.dataOptions.moduleName === 'workorder') {
          modulePath = '/app/wo/orders/' + widget.dataOptions.viewName
        } else if (widget.dataOptions.moduleName === 'alarm') {
          modulePath = '/app/fa/faults/' + widget.dataOptions.viewName
        }
        this.$router.push(modulePath)
        console.log('model path', modulePath)
      } else {
        let modulePath = '/app/wo'
        if (currentModule.module === 'alarm') {
          modulePath = '/app/fa'
        } else if (currentModule.module === 'energydata') {
          modulePath = '/app/em'
        } else {
          modulePath = '/app/wo'
        }
        if (widget.dataOptions.newReportId) {
          let url =
            modulePath + '/reports/newview/' + widget.dataOptions.newReportId
          this.$router.push(url)
        } else if (id) {
          let url = modulePath + '/reports/view/' + id
          console.log('model path', modulePath, widget)
          if (
            this.$route.params &&
            this.$route.params.dashboardlink === 'buildingdashboard' &&
            this.$route.params.buildingid
          ) {
            let data = { buildingid: parseInt(this.$route.params.buildingid) }
            this.$router.push({
              path: url,
              query: { reportSpaceFilterContext: JSON.stringify(data) },
            })
          } else if (
            this.$route.params &&
            (this.$route.params.dashboardlink === 'chillerplant' ||
              this.$route.params.dashboardlink === 'chillers') &&
            this.$route.params.buildingid
          ) {
            let data = { chillerId: parseInt(this.$route.params.buildingid) }
            this.$router.push({
              path: url,
              query: { reportSpaceFilterContext: JSON.stringify(data) },
            })
          } else {
            this.$router.push(url)
          }
        }
        console.log('model path', modulePath)
      }
    },
    getCurrentModule() {
      let routeObj = this.$route
      let module = null
      let rootPath = null
      if (routeObj.meta.module) {
        module = routeObj.meta.module
        rootPath = routeObj.path
      } else {
        if (routeObj.matched) {
          for (let matchedRoute of routeObj.matched) {
            if (matchedRoute.meta.module) {
              module = matchedRoute.meta.module
              rootPath = matchedRoute.path
              break
            }
          }
        }
      }
      return {
        module: module,
        rootPath: rootPath,
      }
    },
    deleteChart(widget, dashboard) {
      let data = { widgetId: widget.id, dashboardId: dashboard, widget }
      this.$emit('deletechart', data)
    },
    hideWidget(widget, dashboard) {
      let data = { widgetId: widget.id, dashboardId: dashboard, widget }
      this.$emit('hidewidget', data)
    },
    editheader() {
      console.log('duble clicked', this.mode)
      this.changeheader = true
    },
    blurevent() {
      if (this.widget.header.title === '') {
        this.changeheader = true
      } else {
        this.changeheader = false
      }
      console.log('******* blurevent trigged')
      this.$emit('widget', this.widget)
    },
    addwidgetHedaer() {
      console.log('***** changeheader clicked')
      this.changeheader = true
    },
    viewHeader(header) {
      if (header && header.title) {
        return true
      } else if (this.changeheader) {
        return true
      } else {
        return false
      }
    },
    getReportutil(data) {
      console.log('********* data', data)
      this.ChartType = data.ChartType ? data.ChartType : 'line'
    },
    sendReportUtil(data) {
      console.log('****** send chart type', data)
      this.sendUtil = data
    },
    getTooltipData(data) {
      this.tooltipdata = data
    },
    getdateFilter(report) {
      let dateFilter = {}
      if (report.dateOperator) {
        this.$set(dateFilter, 'operatorId', report.dateOperator)
      }
      if (report.dateRange) {
        this.$set(dateFilter, 'value', report.dateRange)
      }
      return dateFilter
    },
    sendDataToMobile() {
      if (
        !this.widget.dataOptions.dateFilter &&
        this.widget.dataOptions.newReport
      ) {
        this.widget.dataOptions.dateFilter = this.getdateFilter(
          this.widget.dataOptions.newReport
        )
      }
      this.getMobileRedirectUrl(this.widget)
      if (
        window.JSReceiver &&
        this.widget &&
        this.widget.dataOptions &&
        this.widget.dataOptions.newReportId
      ) {
        if (
          !this.widget.dataOptions.dateFilter &&
          this.widget.dataOptions.newReport
        ) {
          this.widget.dataOptions.dateFilter = this.getdateFilter(
            this.widget.dataOptions.newReport
          )
        }
        window.JSReceiver.sendData(
          JSON.stringify(this.getMobileRedirectUrl(this.widget)),
          JSON.stringify(this.widget)
        )
      } else if (
        this.$route.query &&
        this.$route.query.media &&
        this.$route.query.media === 'ios'
      ) {
        if (
          this.widget &&
          this.widget.dataOptions &&
          this.widget.dataOptions.newReportId
        ) {
          if (
            !this.widget.dataOptions.dateFilter &&
            this.widget.dataOptions.newReport
          ) {
            this.widget.dataOptions.dateFilter = this.getdateFilter(
              this.widget.dataOptions.newReport
            )
          }
          let data = {
            url: this.getMobileRedirectUrl(this.widget),
          }
          document.location.href = 'dashboard_reports://' + JSON.stringify(data)
        }
      }
    },
    getAsPositiveNumber(value) {
      if (parseInt(value) > 0) {
        return parseInt(value)
      } else if (parseInt(value) < 0) {
        return parseInt(value) * -1
      } else {
        return null
      }
    },
    getMobileRedirectUrl(widget) {
      if (
        widget &&
        widget.dataOptions &&
        widget.dataOptions.newReportId &&
        this.newReportObj
      ) {
        let url = {}
        if (this.$timezone) {
          url.timezone = this.$timezone
        }
        if (this.$currency) {
          url.currency = this.$currency
        }
        if (this.newReportObj && this.newReportObj.dateRange) {
          let dateFilter = {
            operatorId: this.newReportObj.dateRange.operatorId,
            value:
              this.getAsPositiveNumber(this.newReportObj.dateRange.value) > 0
                ? this.getAsPositiveNumber(this.newReportObj.dateRange.value) +
                  ''
                : this.newReportObj.dateRange.time,
          }
          if (this.newReportObj.dateRange.operatorId === 62) {
            dateFilter.value = this.newReportObj.dateRange.time
          } else if (this.newReportObj.dateRange.operatorId === 63) {
            dateFilter.value = this.newReportObj.dateRange.time
          } else if (this.newReportObj.dateRange.operatorId === 64) {
            dateFilter.value = this.newReportObj.dateRange.time
          } else if (this.newReportObj.dateRange.operatorId === 65) {
            dateFilter.value = this.newReportObj.dateRange.time
          } else if (this.newReportObj.dateRange.operatorId === 66) {
            dateFilter.value = this.newReportObj.dateRange.time
          } else if (this.newReportObj.dateRange.operatorId === 67) {
            dateFilter.value = this.newReportObj.dateRange.time
          } else if (this.newReportObj.dateRange.operatorId === 20) {
            dateFilter.value = this.newReportObj.dateRange.time
          }
          url.dateFilter = dateFilter
        } else if (
          this.widget &&
          this.widget.dataOptions &&
          this.widget.dataOptions.dateFilter
        ) {
          let { dateFilter } = this.widget.dataOptions
          url.dateFilter = NewDateHelper.getDatePickerObject(
            dateFilter.operatorId
          )
        }
        if (this.newReportObj && this.newReportObj.options) {
          if (
            this.newReportObj.options.dataPoints &&
            this.newReportObj.options.dataPoints.length
          ) {
            url.period = this.newReportObj.options.dataPoints[0].aggr
          } else {
            url.period = 0
          }
        }
        if (
          this.widget &&
          this.widget.dataOptions &&
          this.widget.dataOptions.newReportId
        ) {
          url.reportId = this.widget.dataOptions.newReportId
        }
        url.title = ''
        if (this.widget && this.widget.header && this.widget.header.title) {
          url.title = this.widget.header.title
        }
        console.log(
          'WEBAPP[SEND]:',
          '[',
          new Date(),
          ']',
          '[' + JSON.stringify(url),
          ']'
        )
        return url
      }
    },
  },
}
</script>
<style>
/* .dashboard-container .fc-report .fc-report-filter  {
  position: absolute;
  width: 100%;
  z-index: 100;
  display: block;
  width: 100%;
  padding-right: 0px;
  margin-left: -10px;
  top: 0px;
  right: 30px;
  width: 40px;
  padding-left: 0px ;
  padding-right: 0px;
} */
/* .dashboard-f-widget:hover .fc-report .fc-report-filter  {
  display: block !important;
} */
.dashboard-f-widget .fc-widget {
  position: relative;
}
.externalLink,
.chart-delete-icon {
  color: #868686;
  padding-top: 3px;
}
.fc-widget-body {
  text-align: center;
  overflow: hidden;
  height: 100%;
}
.f-mobile-widget .fc-widget-body {
  padding-top: 50px;
}
.fc-widget-dragable-body {
  text-align: center;
  overflow: hidden;
  height: 100%;
}
.fc-widget.Categories .fc-widget-body {
  overflow: hidden;
}
.dashboard-container .change-chart-select {
  display: none;
}
.fc-widget:hover .change-chart-select {
  display: block !important;
}
/* .dashboard-f-widget:hover .change-chart-select {
  display: block !important;
} */

.fc-widget #tabular-report-hot {
  border: none;
  /* height: 100%; */
}
.chart-icon-report {
  width: 1.4vw;
  height: 1.1vw;
}
/* #1334 .fc-report:second-child {

} */
/* .fc-widget .second-class .boolean-conatiner g.y.y-axis-group.axis {
    display: none;
} */
.fc-secondChart .fc-report-filter.row.header {
  padding: 0px;
}
.fc-secondChart .f-legends {
  position: absolute;
  top: 100px;
  left: 0;
  width: 100%;
}
.dualchart g.x.axis,
.dualchart .month-axis {
  display: none;
}
.dashboard-container .widget-legends {
  padding: 20px;
  padding-left: 40px;
  padding-right: 40px;
  position: absolute;
  bottom: 0;
  width: 100%;
  display: -webkit-box;
  display: -ms-box;
}
.fc-widget .fc-new-chart.bb {
  position: relative;
  top: 10px;
}
.f-mobile-widget .bb-axis-y-label {
  display: none;
}
.f-mobile-widget span.chart-icon.pointer.el-popover__reference {
  display: none !important;
}
.f18.fc-widget-label.loading-dull,
.loading-dull button.el-button.el-button--default.el-popover__reference {
  color: #2f2e49 !important;
  opacity: 0.3;
}
.mobile-new-date-filter {
  position: absolute;
  right: 10px;
  bottom: 33px;
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  font-weight: 500;
  opacity: 0.8;
  white-space: nowrap;
}
</style>
