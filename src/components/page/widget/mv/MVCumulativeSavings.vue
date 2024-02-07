<template>
  <div class="cost-trend-graph  new-chart-css">
    <div class="width100 widget-header-2 row">
      <div class="col-8 text-left">Cummulative Savings</div>
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
import FNewAnalyticReport from 'pages/energy/analytics/newTools/v1/FNewAnalyticsReportV1'
import NewDateHelper from '@/mixins/NewDateHelper'
import AnalyticsMixin from 'pages/energy/analytics/mixins/AnalyticsMixin'
import moment from 'moment-timezone'
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
        hideDataPoints: ['A'],
        xFormat: 'MM-DD-YYYY',
        hidechartoptions: true,
        point: {
          show: true,
        },
        hidetabular: true,
        hidecharttypechanger: true,
        showWidgetLegends: false,
        axis: {
          y: {
            label: `CUMMULATIVE ${this.$currency} SAVINGS`,
          },
        },
        axes: {
          A: 'y2',
          B: 'y',
        },
        colors: {
          B: '#C06A36',
        },
        type: 'reading',
        period: 12,
        mode: 4,
        baseLine: null,
        dateFilter: {},
        chartViewOption: 0,
        dataPoints: [],
        chartType: 'area',
        predictionTimings: [],
      },
    }
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
    field() {
      return this.details.submodules.mvcumulativesaving.fields.find(
        field => field.name === 'cumulativesaving'
      )
    },
  },
  mounted() {
    this.perepareConfig()
  },
  methods: {
    perepareConfig() {
      if (this.details.baselines && this.details.baselines.length) {
        this.analyticsConfig.dateFilter = NewDateHelper.getDatePickerObject(
          64,
          [
            this.details.mvProject.reportingPeriodStartTime,
            this.details.baselines[0].endTime,
          ]
        )
        this.analyticsConfig.dateFilter['value'] = [
          this.details.mvProject.reportingPeriodStartTime,
          this.details.mvProject.reportingPeriodEndTime,
        ]
      }
      let startTime = moment(this.details.mvProject.reportingPeriodStartTime)
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
      this.analyticsConfig.transformWorkflow = {
        workflowString:
          '<workflow> <parameter name="data" type="" /> <parameter name="aggr" type="" /> <iterator var="index,value:data"> <expression name="paramA"> <function>map.get(value, \'A\')</function> </expression> <expression name="result"> <expr>paramA * 0.44</expr> </expression> <expression name="put"> <function>map.put(value, \'B\', result)</function> </expression> </iterator> </workflow>',
      }
      this.analyticsConfig.dataPoints = []

      let actualdata = {
        visible: false,
        name: this.field.name,
        reportModuleName: 'mvproject',
        parentId: this.details.mvProject.id,
        aliases: {
          actual: 'A',
        },
        yAxis: {
          fieldId: this.field.fieldId,
          aggr: 5,
        },
      }
      let cummulative = {
        parentId: null,
        name: 'Cumulative Savings',
        metaData: {},
        yAxis: {
          dataType: 3,
          label: 'Cumulative Savings',
          unitStr: this.$currency,
        },
        aliases: {
          actual: 'B',
        },
        type: 2,
      }
      this.analyticsConfig.dataPoints = [actualdata, cummulative]
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
</style>
