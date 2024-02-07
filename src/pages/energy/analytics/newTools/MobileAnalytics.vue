<template>
  <div class="newanalytics-generater">
    <f-new-analytic-report
      ref="newAnalyticReport"
      :config.sync="analyticsConfig"
      v-if="analyticsConfig.dataPoints.length"
      class="newanalytics-generater-container"
    ></f-new-analytic-report>
  </div>
</template>
<script>
import NewDataFormatHelper from 'pages/report/mixins/NewDataFormatHelper'
import FNewAnalyticReport from 'pages/energy/analytics/components/FNewAnalyticReport'
import AnalyticsMixin from 'pages/energy/analytics/mixins/AnalyticsMixin'
import newDateHelper from '@/mixins/NewDateHelper'
import OutApp from 'src/OuterAppUtil/OuterAppHelper'
export default {
  mixins: [NewDataFormatHelper, AnalyticsMixin, newDateHelper, OutApp],
  components: {
    FNewAnalyticReport,
  },
  data() {
    return {
      analyticsConfig: {
        name: '',
        api: '/v2/report/readingReport',
        period: 0,
        mode: 1,
        dateFilter: newDateHelper.getDatePickerObject(22),
        dataPoints: [],
        hidechart: false,
        hidetable: false,
        hidecharttypechanger: false,
      },
    }
  },
  mounted() {
    this.loadQData()
    this.loadQueryData()
    window.localStorage.setItem('mode', false)
  },
  methods: {
    loadQueryData() {
      let querryData = this.$route.query
      this.analyticsConfig.dataPoints = JSON.parse(querryData.dataPoints)
      if (querryData.period) {
        this.analyticsConfig.period = parseInt(querryData.period)
      }
      if (querryData.mode) {
        this.analyticsConfig.mode = parseInt(querryData.mode)
      }
      if (querryData.dateFilter) {
        let date = null
        date = JSON.parse(querryData.dateFilter)
        if (date.operatorId && date.value) {
          this.analyticsConfig.dateFilter = newDateHelper.getDatePickerObject(
            date.operatorId,
            date.value
          )
        } else if (date.operatorId) {
          this.analyticsConfig.dateFilter = newDateHelper.getDatePickerObject(
            date.operatorId,
            null
          )
        } else {
          this.analyticsConfig.dateFilter = newDateHelper.getDatePickerObject(
            22
          )
        }
      }
      if (querryData.api) {
        this.analyticsConfig.api = querryData.api
      }
      if (querryData.name) {
        this.analyticsConfig.name = querryData.name
      }
      if (querryData.chartType) {
        this.analyticsConfig.chartType = querryData.chartType
      }
      if (querryData.hidechart === 'true' || querryData.hidechart === true) {
        this.analyticsConfig.hidechart = true
      }
      if (
        querryData.hidetabular === 'true' ||
        querryData.hidetabular === true
      ) {
        this.analyticsConfig.hidetabular = true
      }
      if (
        querryData.hidecharttypechanger === 'true' ||
        querryData.hidecharttypechanger === true
      ) {
        this.analyticsConfig.hidecharttypechanger = true
      }
    },
  },
}
</script>
<style>
.q-toolbar.row.no-wrap.items-center.relative-position.q-toolbar-normal.fc-toolbar.toolbar,
.subheader-section.width100.new-analytics-subheader,
aside.fc-layout-aside,
.chart-icon {
  display: none !important;
}
.height100.pL50.pL60 {
  padding: 0;
}
.newanalytics-generater {
  overflow: scroll;
  height: 100vh;
  background: transparent;
  padding: 0;
}
.newanalytics-generater-container {
  background: transparent;
}
.newanalytics-generater .chart-icon-new,
.chart-icon-new {
  width: 30px !important;
  height: 15px !important;
}
.new-analytics-table {
  position: relative;
  margin-bottom: 150px;
  margin: 10px;
  background: #fff;
  border: 1px solid rgb(234, 232, 232);
  box-shadow: 0 7px 6px 0 rgba(233, 233, 233, 0.5);
}
.variance-item {
  width: 50%;
}
.variance-x-label {
  font-size: 10px !important;
}
.variance-value {
  font-size: 14px !important;
}
.widget-legends {
  padding: 10px !important;
  display: none !important;
}
.newanalytics-generater-container .mobile-chart table tr {
  display: inline-grid;
}
.mobile-chart {
  margin: 10px;
  padding: 15px;
  padding-right: 20px;
  background: #fff;
  border: 1px solid rgb(234, 232, 232);
  box-shadow: 0 7px 6px 0 rgba(233, 233, 233, 0.5);
}
.new-analytics-filter-section {
  display: none !important;
}
.bb-axis-x-label,
.bb-axis-y-label,
.bb-axis-y2-label {
  display: none;
}
div#q-app {
  background: #f6f7f8;
}

/*-- Chart --*/
.c3 svg {
  font: 10px sans-serif;
  -webkit-tap-highlight-color: rgba(0, 0, 0, 0);
}

.c3 path,
.c3 line {
  fill: none;
  stroke: #000;
}

.c3 text {
  -webkit-user-select: none;
  -moz-user-select: none;
  user-select: none;
  letter-spacing: 0.3px;
  fill: #2f2e49;
}

.c3-legend-item-tile,
.c3-xgrid-focus,
.c3-ygrid,
.c3-event-rect,
.c3-bars path {
  shape-rendering: crispEdges;
}

.c3-chart-arc path {
  stroke: #fff;
}

.c3-chart-arc rect {
  stroke: white;
  stroke-width: 1;
}

.c3-chart-arc text {
  fill: #fff;
  font-size: 13px;
}

/*-- Axis --*/
/*-- Grid --*/
.c3-grid line {
  stroke: #aaa;
}

.c3-grid text {
  fill: #aaa;
}

.c3-xgrid,
.c3-ygrid {
  stroke-dasharray: 3 3;
}

/*-- Text on Chart --*/
.c3-text.c3-empty {
  fill: #808080;
  font-size: 2em;
}

/*-- Line --*/
.c3-line {
  stroke-width: 1px;
}

/*-- Point --*/
.c3-circle._expanded_ {
  stroke-width: 1px;
  stroke: white;
}

.c3-selected-circle {
  fill: white;
  stroke-width: 2px;
}

/*-- Bar --*/
.c3-bar {
  stroke-width: 0;
}

.c3-bar._expanded_ {
  fill-opacity: 1;
  fill-opacity: 0.75;
}

/*-- Focus --*/
.c3-target.c3-focused {
  opacity: 1;
}

.c3-target.c3-focused path.c3-line,
.c3-target.c3-focused path.c3-step {
  stroke-width: 2px;
}

.c3-target.c3-defocused {
  opacity: 0.3 !important;
}

/*-- Region --*/
.c3-region {
  fill: steelblue;
  fill-opacity: 0.1;
}

/*-- Brush --*/
.c3-brush .extent {
  fill-opacity: 0.1;
}

/*-- Select - Drag --*/
/*-- Legend --*/
.c3-legend-item {
  font-size: 12px;
}

.c3-legend-item-hidden {
  opacity: 0.15;
}

.c3-legend-background {
  opacity: 0.75;
  fill: white;
  stroke: lightgray;
  stroke-width: 1;
}

/*-- Title --*/
.c3-title {
  font: 14px sans-serif;
}

/*-- Tooltip --*/
.c3-tooltip-container {
  z-index: 10;
}

.c3-tooltip {
  border-collapse: collapse;
  border-spacing: 0;
  background-color: #fff;
  empty-cells: show;
  -webkit-box-shadow: 7px 7px 12px -9px #777777;
  -moz-box-shadow: 7px 7px 12px -9px #777777;
  box-shadow: 7px 7px 12px -9px #777777;
  opacity: 0.9;
}

.c3-tooltip tr {
  border: 1px solid #ccc;
}

.c3-tooltip th {
  background-color: #aaa;
  font-size: 14px;
  padding: 2px 5px;
  text-align: left;
  color: #fff;
}

.c3-tooltip td {
  font-size: 13px;
  padding: 3px 6px;
  background-color: #fff;
  border-left: 1px dotted #999;
}

.c3-tooltip td > span {
  display: inline-block;
  width: 10px;
  height: 10px;
  margin-right: 6px;
}

.c3-tooltip td.value {
  text-align: right;
}

/*-- Area --*/
.c3-area {
  stroke-width: 0;
  opacity: 0.2;
}

/*-- Arc --*/
.c3-chart-arcs-title {
  dominant-baseline: middle;
  font-size: 1.3em;
}

.c3-chart-arcs .c3-chart-arcs-background {
  fill: #e0e0e0;
  stroke: #fff;
}

.c3-chart-arcs .c3-chart-arcs-gauge-unit {
  fill: #000;
  font-size: 16px;
}

.c3-chart-arcs .c3-chart-arcs-gauge-max {
  fill: #777;
}

.c3-chart-arcs .c3-chart-arcs-gauge-min {
  fill: #777;
}

.c3-chart-arc .c3-gauge-value {
  fill: #000;
  /*  font-size: 28px !important;*/
}

.c3-chart-arc.c3-target g path {
  opacity: 1;
}

.c3-chart-arc.c3-target.c3-focused g path {
  opacity: 1;
}

/* c3 custom starts here */
/* .fc-new-chart {
  margin: 40px;
} */

.fc-new-chart svg {
  background: white;
}

.c3 .c3-axis-x path.domain {
  fill: none;
  shape-rendering: crispEdges;
  stroke: #d2d6df !important;
  stroke-width: 1 !important;
}

.c3-ygrid {
  stroke: #eff2f5 !important;
}

.c3 .c3-axis-x .tick line {
  opacity: 0;
}

.c3 .c3-axis-x .tick text,
.c3 .c3-axis-y .tick text,
.c3 .c3-axis-y2 .tick text {
  fill: #868686;
  font-size: 10px;
}

.c3 .c3-axis-y path.domain {
  opacity: 0;
}

.c3 .c3-axis-y .tick line {
  opacity: 0;
}

.c3 .c3-axis-y2 path.domain {
  opacity: 0;
}

.c3 .c3-axis-y2 .tick line {
  opacity: 0;
}

.bb-axis-x-label,
.bb-axis-y-label,
.bb-axis-y2-label {
  font-size: 0.7rem;
  fill: #adb0b6 !important;
  letter-spacing: 1px !important;
  /* text-transform: uppercase; */
}

.bb-axis-x-label {
  text-transform: uppercase;
}

.c3-xgrid-focus {
  stroke: #d2d6df !important;
}

.c3-tooltip-container .bb-chart-tooltip {
  position: relative !important;
}

/*-- Chart --*/
.bb svg {
  font: 10px sans-serif;
  -webkit-tap-highlight-color: rgba(0, 0, 0, 0);
}

.bb path,
.bb line {
  fill: none;
  stroke: #000;
}

.bb text {
  -webkit-user-select: none;
  -moz-user-select: none;
  user-select: none;
  letter-spacing: 0.3px;
  fill: #2f2e49;
}

.bb-legend-item-tile,
.bb-xgrid-focus,
.bb-ygrid,
.bb-event-rect,
.bb-bars path {
  shape-rendering: crispEdges;
}

.bb-chart-arc path {
  stroke: #fff;
}

.bb-chart-arc rect {
  stroke: white;
  stroke-width: 1;
}

.bb-chart-arc text {
  fill: #fff;
  font-size: 13px;
}

/*-- Axis --*/
/*-- Grid --*/
.bb-grid line {
  stroke: #aaa;
}

.bb-grid text {
  fill: #aaa;
}

.bb-xgrid,
.bb-ygrid {
  stroke-dasharray: 3 3;
}

/*-- Text on Chart --*/
.bb-text.bb-empty {
  fill: #808080;
  font-size: 2em;
}

/*-- Line --*/
.bb-line {
  stroke-width: 1px;
}

/*-- Point --*/
.bb-circle._expanded_ {
  stroke-width: 1px;
  stroke: white;
}

.bb-selected-circle {
  fill: white;
  stroke-width: 2px;
}

/*-- Bar --*/
.bb-bar {
  stroke-width: 0;
}

.bb-bar._expanded_ {
  fill-opacity: 1;
  fill-opacity: 0.75;
}

/*-- Focus --*/
.bb-target.bb-focused {
  opacity: 1;
}

.bb-target.bb-focused path.bb-line,
.bb-target.bb-focused path.bb-step {
  stroke-width: 2px;
}

.bb-target.bb-defocused {
  opacity: 0.3 !important;
}

/*-- Region --*/
.bb-region {
  fill: steelblue;
  fill-opacity: 0.1;
}

/*-- Brush --*/
.bb-brush .extent {
  fill-opacity: 0.1;
}

/*-- Select - Drag --*/
/*-- Legend --*/
.bb-legend-item {
  font-size: 12px;
}

.bb-legend-item-hidden {
  opacity: 0.15;
}

.bb-legend-background {
  opacity: 0.75;
  fill: white;
  stroke: lightgray;
  stroke-width: 1;
}

/*-- Title --*/
.bb-title {
  font: 14px sans-serif;
}

/*-- Tooltip --*/
.bb-tooltip-container {
  z-index: 10;
}

.bb-tooltip {
  border-collapse: collapse;
  border-spacing: 0;
  background-color: #fff;
  empty-cells: show;
  -webkit-box-shadow: 7px 7px 12px -9px #777777;
  -moz-box-shadow: 7px 7px 12px -9px #777777;
  box-shadow: 7px 7px 12px -9px #777777;
  opacity: 0.9;
}

.bb-tooltip tr {
  border: 1px solid #ccc;
}

.bb-tooltip th {
  background-color: #aaa;
  font-size: 14px;
  padding: 2px 5px;
  text-align: left;
  color: #fff;
}

.bb-tooltip td {
  font-size: 13px;
  padding: 3px 6px;
  background-color: #fff;
  border-left: 1px dotted #999;
}

.bb-tooltip td > span {
  display: inline-block;
  width: 10px;
  height: 10px;
  margin-right: 6px;
}

.bb-tooltip td.value {
  text-align: right;
}

/*-- Area --*/
.bb-area {
  stroke-width: 0;
  opacity: 0.2;
}

/*-- Arc --*/
.bb-chart-arcs-title {
  dominant-baseline: middle;
  font-size: 1.3em;
}

.bb-chart-arcs .bb-chart-arcs-background {
  fill: #e0e0e0;
  stroke: #fff;
}

.bb-chart-arcs .bb-chart-arcs-gauge-unit {
  fill: #000;
  font-size: 16px;
}

.bb-chart-arcs .bb-chart-arcs-gauge-max {
  fill: #777;
}

.bb-chart-arcs .bb-chart-arcs-gauge-min {
  fill: #777;
}

.bb-chart-arc .bb-gauge-value {
  fill: #000;
  /*  font-size: 28px !important;*/
}

.bb-chart-arc.bb-target g path {
  opacity: 1;
}

.bb-chart-arc.bb-target.bb-focused g path {
  opacity: 1;
}

/* bb custom starts here */
/* .fc-new-chart {
  margin: 40px;
} */

.fc-new-chart svg {
  background: white;
}

.bb .bb-axis-x path.domain {
  fill: none;
  shape-rendering: crispEdges;
  stroke: #d2d6df !important;
  stroke-width: 1 !important;
}

.bb-xgrid,
.bb-ygrid {
  stroke: #eff2f5 !important;
}

.bb-ygrid-line.x-axis-zero-line line {
  stroke: #d2d6df !important;
}

.bb .bb-axis-x .tick line {
  opacity: 0;
}

.bb .bb-axis-x .tick text,
.bb .bb-axis-y .tick text,
.bb .bb-axis-y2 .tick text {
  fill: #202024;
  font-size: 10px;
}

.bb .bb-axis-y path.domain {
  opacity: 0;
}

.bb .bb-axis-y .tick line {
  opacity: 0;
}

.bb .bb-axis-y2 path.domain {
  opacity: 0;
}

.bb .bb-axis-y2 .tick line {
  opacity: 0;
}

.bb-xgrid-focus {
  stroke: #d2d6df !important;
}

.bb-tooltip-container .bb-chart-tooltip {
  position: relative !important;
}

/*-- Zoom region --*/
.bb-zoom-brush {
  fill-opacity: 0.1;
}

/*-- Brush --*/
.bb-brush .extent {
  fill-opacity: 0.1;
}

/*-- Radar --*/
.bb-chart-radars .bb-levels polygon {
  fill: none;
  stroke: #848282;
  stroke-width: 0.5px;
}

.bb-chart-radars .bb-levels text {
  fill: #848282;
}

.bb-chart-radars .bb-axis line {
  stroke: #848282;
  stroke-width: 0.5px;
}

.bb-chart-radars .bb-axis text {
  font-size: 1.15em;
  cursor: default;
}

.bb-chart-radars .bb-shapes polygon {
  fill-opacity: 0.2;
  stroke-width: 1px;
}

/*-- Button --*/
.bb-button {
  position: absolute;
  top: 10px;
  right: 10px;
}
.bb-button .bb-zoom-reset {
  font-size: 11px;
  border: solid 1px #ccc;
  background-color: #fff;
  padding: 5px;
  border-radius: 5px;
  cursor: pointer;
}

.bb .tooltip-title {
  text-align: left;
}

.bb-chart-tooltip {
  position: absolute;
  padding: 12px;
  background-color: #fff;
  color: rgba(0, 0, 0, 0.7);
  box-shadow: 0 2px 8px 0 hsla(0, 1%, 76%, 0.5);
  border-radius: 4px;
  border: 1px solid rgb(215, 215, 215);
  transition: visibility 0.2s, opacity 0.3s linear;
  z-index: 10000;
  max-width: 500px;
}

.bb-chart-tooltip .axis-row {
  padding: 5px;
  padding-left: 0px;
}

.bb-chart-tooltip .tooltip-title {
  font-weight: 500;
  font-size: 13px;
  padding: 5px 0;
}
.tooltip-type-title {
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 1px;
  text-align: left;
  color: #879eb5;
  padding: 5px;
  padding-left: 0px;
  text-transform: uppercase;
  font-size: 11px;
}
.bb-chart-tooltip .axis-color {
  margin: 2px 5px;
}

.bb-chart-tooltip .axis-group,
.chart-tooltip .axis-group {
  font-size: 13px;
  opacity: 0.7;
  margin-top: 8px;
}

.bb-chart-tooltip .axis-tip {
  font-size: 12px;
  color: #333333bd;
  padding-top: 5px;
}

.bb-chart-tooltip .axis-label {
  font-weight: 400;
  padding-right: 8px;
  color: #979797;
  font-size: 13px;
}
.bb-chart-tooltip .icon {
  height: 20px;
  width: 20px;
  margin-right: 4px;
  margin-top: 4px;
}
.bb-chart-tooltip .imgcls {
  display: flex;
  cursor: pointer;
  /* align-items: center; */
  flex-direction: row;
}
.bb-chart-tooltip .axis-value {
  font-weight: 500;
}

.bb-chart-tooltip .axis-unit {
  font-weight: 400;
  font-size: 13px;
  margin-left: 2px;
  color: #979797;
}

.bb-chart-tooltip table td div {
  display: inline-block;
  vertical-align: middle;
  padding: 4px 0px;
}

.bb-chart-tooltip table tr > td:last-child {
  text-align: right;
  padding-left: 15px;
}

/* mobile app*/
@media only screen and (max-width: 1024px) {
  .dashboard-container .fc-widget-header {
    padding-top: 15px !important;
    padding-bottom: 15px !important;
    padding-left: 15px !important;
    padding-right: 15px !important;
    display: flex !important;
    justify-content: center !important;
    align-items: center !important;
  }
  .fc-white-theme .en-divider {
    display: none !important;
  }
  .fc-white-theme .axis text,
  .month-axis .tick text,
  .fchart text.Xaxis-label,
  text.Yaxis-label {
    fill: #000000 !important;
  }
  .dashboard-container .fc-widget-label {
    font-size: 18px !important;
  }
  .dashboard-container .date-filter-comp button {
    font-size: 12px !important;
  }
  .button-row {
    margin-top: -25px;
  }
  .datepicker-header {
    padding-left: 6%;
  }
  .calender-range-section {
    width: 100%;
    overflow-x: scroll;
  }
  .calender-popup {
    left: -10px !important;
    width: 100% !important;
  }
  .to-table {
    /* padding-left: 0 !important; */
  }
  .ftimeseries-alarm-toggle-container {
    margin-top: -20px !important;
  }
  .year-section .year {
    padding-left: 15px !important;
  }
  .month-container {
    margin-right: 0 !important;
  }
  .year-section {
    margin-right: 0 !important;
  }
  .days-section {
    margin-left: 0 !important;
  }
  .range-selecter {
    padding-left: 10px !important;
    padding-right: 10px !important;
  }
  .days-section .month-row {
    margin-left: 4% !important;
  }
  .externalLink {
    display: none !important;
  }
  .fLegendContainer .el-color-picker__color-inner {
    height: 3px !important;
    width: 14px !important;
    border-radius: 0% !important;
    top: 6px;
  }
  .fLegendContainer-new {
    justify-content: left !important;
  }
  img.no-chart-data {
    width: 40% !important;
  }
}
.newanalytics-generater .spinner {
  width: 100%;
}
.mobile-chart {
  padding: 0 !important;
}
</style>
