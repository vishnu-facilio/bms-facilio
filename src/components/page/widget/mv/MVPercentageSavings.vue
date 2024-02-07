<template>
  <div class="cost-trend-graph new-chart-css">
    <div class="width100 widget-header-2 row">
      <div class="col-8 text-left">Saving Percentage</div>
      <div class="col-4 text-right f12 font-normal mv-chart-date-picker">
        {{ startTime }} - {{ endTime }}
      </div>
    </div>
    <spinner :show="chartloading" size="80"></spinner>
    <f-new-analytic-report
      v-if="!loading"
      v-show="!chartloading"
      ref="newAnalyticReport"
      :config.sync="analyticsConfig"
      :baseLines="baseLineList"
      @reportLoaded="onReportLoaded"
    ></f-new-analytic-report>
  </div>
</template>

<script>
import moment from 'moment-timezone'
import FNewAnalyticReport from 'pages/energy/analytics/newTools/v1/FNewAnalyticsReportV1'
import NewDateHelper from '@/mixins/NewDateHelper'
import AnalyticsMixin from 'pages/energy/analytics/mixins/AnalyticsMixin'
export default {
  props: ['details'],
  mixins: [AnalyticsMixin],
  components: {
    FNewAnalyticReport,
  },
  data() {
    return {
      loading: true,
      chartloading: true,
      baseLineList: null,
      fields: null,
      analyticsConfig: {
        name: 'Building Analysis',
        key: 'BUILDING_ANALYSIS',
        analyticsType: this.isSiteAnalysis ? 6 : 2,
        hidedatepicker: true,
        hidechartoptions: true,
        colors: {
          A: '#6BC04D',
        },
        point: {
          show: true,
        },
        axis: {
          y: {
            label: 'SAVING (%)',
            min: 0,
            max: 100,
          },
        },
        hidetabular: true,
        zoom: {
          enabled: false,
        },
        hidecharttypechanger: true,
        showWidgetLegends: false,
        type: 'reading',
        period: 12,
        mode: 4,
        baseLine: null,
        dateFilter: {},
        chartViewOption: 0,
        dataPoints: [],
        chartType: 'bar',
        xFormat: 'MM-DD-YYYY',
        predictionTimings: [],
        regionConfig: [],
      },
    }
  },
  mounted() {
    this.perepareConfig()
  },
  computed: {
    startTime() {
      if (this.details.baselines && this.details.baselines.length) {
        return this.$options.filters.formatDate(
          this.details.mvProject.reportingPeriodStartTime,
          true,
          false
        )
      } else {
        return null
      }
    },
    endTime() {
      if (
        this.details.mvProject &&
        this.details.mvProject.reportingPeriodEndTime
      ) {
        return this.$options.filters.formatDate(
          this.details.mvProject.reportingPeriodEndTime,
          true,
          false
        )
      } else {
        return null
      }
    },
    fieldId() {
      return this.details.submodules.mvsavingpercentage.fields.find(
        field => field.name === 'savingpercentage'
      ).fieldId
    },
  },
  methods: {
    perepareConfig() {
      if (this.details.baselines && this.details.baselines.length) {
        this.analyticsConfig.dateFilter = NewDateHelper.getDatePickerObject(
          64,
          [
            this.details.baselines[0].startTime,
            this.details.baselines[0].endTime,
          ]
        )
        this.analyticsConfig.dateFilter['value'] = [
          this.details.baselines[0].startTime,
          this.details.mvProject.reportingPeriodEndTime,
        ]
      }
      let startTime =
        this.details.baselines && this.details.baselines.length
          ? moment(this.details.baselines[0].startTime)
          : moment(this.details.mvProject.reportingPeriodStartTime)
      let endTime = moment(this.details.mvProject.reportingPeriodEndTime)
      let start = moment(startTime)
      let end = moment(endTime)
      let days = end.diff(start, 'days')
      if (days > 90) {
        this.analyticsConfig.period = 10
        this.analyticsConfig.xFormat = 'MM-YYYY'
      }
      if (this.analyticsConfig.period === 10) {
        let time =
          new Date().valueOf() > this.details.mvProject.reportingPeriodEndTime
            ? this.details.mvProject.reportingPeriodEndTime
            : new Date().valueOf()
        this.analyticsConfig.dateFilter['value'] = [
          this.details.mvProject.reportingPeriodStartTime,
          moment(time)
            .tz(this.$timezone)
            .endOf('month')
            .valueOf(),
        ]
      } else if (this.analyticsConfig.period === 12) {
        let time =
          new Date().valueOf() > this.details.mvProject.reportingPeriodEndTime
            ? this.details.mvProject.reportingPeriodEndTime
            : new Date().valueOf()
        this.analyticsConfig.dateFilter['value'] = [
          this.details.mvProject.reportingPeriodStartTime,
          moment(time)
            .tz(this.$timezone)
            .endOf('day')
            .valueOf(),
        ]
      }
      this.analyticsConfig.dataPoints = [
        {
          parentId: this.details.mvProject.id,
          name: 'Saving Percentage',
          reportModuleName: 'mvproject',
          yAxis: {
            fieldId: this.fieldId,
            aggr: 3,
          },
          aliases: {
            actual: 'A',
          },
        },
      ]
      this.loading = false
    },
    onReportLoaded() {
      this.$emit('reportLoaded')
      this.chartloading = false
    },
  },
}
</script>

<style>
.widget-header-2 {
  font-size: 16px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.5px;
  color: #333333;
  padding: 30px;
  border-bottom: solid 1px #eceef1;
  padding-bottom: 20px;
  padding-top: 20px;
}
.mv-baselineVsActual .fLegendContainer {
  top: 20px;
  position: relative;
}
</style>
