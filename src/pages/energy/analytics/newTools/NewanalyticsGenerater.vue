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
export default {
  mixins: [NewDataFormatHelper, AnalyticsMixin, newDateHelper],
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
</style>
