<template>
  <div class="mobile-report-outline">
    <!-- <div class="datefilter-hide-overlay"></div> -->
    <mobile-report
      ref="newAnalyticReport"
      class="newanalytics-generater-container mobile-edit-report"
      v-if="!localloading && !analyticsLoading"
      :mobileConfig="mobileConfig"
    ></mobile-report>
  </div>
</template>

<script>
import NewDataFormatHelper from 'pages/report/mixins/NewDataFormatHelper'
// import MobileReport from 'pages/energy/analytics/newTools/MobileReport'
import MobileReport from 'pages/report/FNewMobileReport'
import AnalyticsMixin from 'pages/energy/analytics/mixins/AnalyticsMixin'
import newDateHelper from '@/mixins/NewDateHelper'
import OutApp from 'src/OuterAppUtil/OuterAppHelper'
import MobileAnalyticsMixin from 'pages/energy/analytics/mixins/MobileAnalyticsMixin'
export default {
  mixins: [
    NewDataFormatHelper,
    AnalyticsMixin,
    newDateHelper,
    OutApp,
    MobileAnalyticsMixin,
  ],
  components: {
    MobileReport,
  },
  data() {
    return {
      analyticsConfig: {
        dateFilter: null,
      },
    }
  },
  mounted() {
    this.loadQData()
  },
  computed: {
    mobileConfig() {
      if (this.analyticsConfig && this.analyticsConfig.dateFilter) {
        return this.analyticsConfig
      } else if (this.analyticsConfig) {
        // this.analyticsConfig.dateFilter = newDateHelper.getDatePickerObject(22)
        return this.analyticsConfig
      } else {
        return null
      }
    },
  },
}
</script>
<style>
@import '../../assets/styles/mobilereport.css';
</style>
<style>
@import '../../assets/styles/c3.css';
</style>
<style>
@import '../../assets/styles/mobile-app.scss';
</style>
<style>
.mT50.new-analytics-table {
  display: none;
}
.analytic-summary.new-analytic-summary.scrollable.col-12,
.mleft0 {
  max-width: 100% !important;
  width: 100%;
}
.newanalytics-page-header {
  display: none;
}
.add-points-block {
  display: none;
}
.self-center.fchart-section.building-graph-container {
  margin: 0;
  padding: 0;
  height: 100%;
  position: relative;
}
.analytics-section-new.row.newanalytics-generater-container.mobile-edit-report {
  background: #fff;
}
.bb-axis-y-label {
  display: none;
}
body.mat.mobile.touch.platform-ios.tippy-touch,
.mobile-report-outline {
  overflow-x: hidden;
}
.filter-field.date-filter-comp {
  display: none;
}
.mobile-chart-legend {
  padding: 10px;
}

span.mobile-tootltip-value.f20 {
  padding-left: 10px;
  border-left: 1px solid gray;
  border-left: 1px solid #f3f3f3;
  height: 20px;
}
svg.spinner.analytics-spinner.show {
  height: 50vh;
  margin: auto;
  position: relative;
  left: 35%;
}
</style>
