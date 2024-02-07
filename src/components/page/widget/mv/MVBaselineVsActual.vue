<template>
  <div class="cost-trend-graph mv-baselineVsActual new-chart-css">
    <div class="width100 widget-header-2 row">
      <div class="col-8 text-left">
        Adjusted Baseline Energy vs Actual Energy
      </div>
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
        hideDataPoints: ['B'],
        colors: {
          A: '#00a2f5',
          B: '#39c2b0',
          C: '#39c2b0',
        },
        dataPointsRange: {
          A: {
            range: [],
          },
        },
        point: {
          show: true,
        },
        axis: {
          y: {
            label: 'ENERGY USE',
          },
        },
        hidetabular: true,
        zoom: {
          enabled: false,
        },
        axes: {
          A: 'y',
          B: 'y2',
          c: 'y',
        },
        // point: {
        //   show: true
        // },
        hidecharttypechanger: true,
        showWidgetLegends: false,
        disbaleNiceTickMarks: true,
        type: 'reading',
        period: 12,
        mode: 4,
        baseLine: null,
        dateFilter: {},
        chartViewOption: 0,
        dataPoints: [],
        chartType: 'line',
        xFormat: 'MM-DD-YYYY',
        predictionTimings: [],
        transformWorkflow: null,
        regionConfig: [],
        diffChartConfig: {
          from: {
            point: 'A',
            color: 'red',
            disable: true,
          },
          to: {
            point: 'C',
          },
          context: {
            id: 'mvbaselinevsactual',
            class: 'baselinevsActual',
          },
        },
      },
    }
  },
  mounted() {
    this.loadPickList()
  },
  computed: {
    startTime() {
      if (this.details.baselines && this.details.baselines.length) {
        return this.$options.filters.formatDate(
          this.details.baselines[0].startTime,
          true,
          false
        )
      } else {
        return null
      }
    },
    endTime() {
      if (this.details.mvProject && this.details.mvProject.endTime) {
        return this.$options.filters.formatDate(
          this.details.mvProject.reportingPeriodEndTime,
          true,
          false
        )
      } else {
        return null
      }
    },
  },
  methods: {
    loadPickList(moduleName, fieldName) {
      let self = this
      this.loading = true
      self.$http.get('/asset/getreadings').then(function(response) {
        if (response.data) {
          self.fields = Object.values(response.data.fields).find(
            rt => rt.name === 'totalEnergyConsumptionDelta'
          )
          self.perepareConfig()
        }
      })
    },
    perepareConfig() {
      let actualValueStartTime = this.details.mvProject.reportingPeriodStartTime
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
      let startTime = moment(this.details.baselines[0].startTime)
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
        actualValueStartTime = moment(actualValueStartTime)
          .tz(this.$timezone)
          .startOf('month')
          .valueOf()
        this.analyticsConfig.dateFilter['value'] = [
          this.details.baselines[0].startTime,
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
          this.details.baselines[0].startTime,
          moment(time)
            .tz(this.$timezone)
            .endOf('day')
            .valueOf(),
        ]
      }
      this.analyticsConfig.transformWorkflow = {
        workflowString:
          '<workflow><parameter name="data" type=""/><parameter name="aggr" type=""/><iterator var="index,value:data"><expression name="time"><function>map.get(value, \'X\')</function></expression><expression name="paramC"><function>map.get(value, \'B\')</function></expression><conditions><if criteria="time &gt;=' +
          actualValueStartTime +
          '"><expression name="put"><function>map.put(value, \'C\', paramC)</function></expression></if></conditions></iterator></workflow>',
      }
      this.analyticsConfig.regionConfig = {
        axis: 'x',
        start:
          this.details.mvProject && this.details.mvProject.startTime
            ? this.details.mvProject.startTime
            : null,
        end:
          this.details.mvProject && this.details.mvProject.endTime
            ? this.details.mvProject.endTime
            : null,
        reportingPeriodStartTime:
          this.details.mvProject &&
          this.details.mvProject.reportingPeriodStartTime
            ? this.details.mvProject.reportingPeriodStartTime
            : null,
        class: 'mvregion',
      }
      this.analyticsConfig.dataPointsRange['A'] = {
        range: [
          this.details.mvProject && this.details.mvProject.startTime
            ? this.details.mvProject.startTime
            : null,
          this.details.mvProject && this.details.mvProject.endTime
            ? this.details.mvProject.endTime
            : null,
        ],
      }
      this.analyticsConfig.dataPoints = []
      let baselinedata = {
        parentId: this.details.mvProject.id,
        name:
          this.details.mvProject.meter && this.details.mvProject.meter.name
            ? this.details.mvProject.meter.name + ' (Adjusted Baseline)'
            : 'Adjusted Baseline',
        reportModuleName: 'mvproject',
        yAxis: {
          fieldId: this.details.submodules.mvbaselinewithadjustmentreading.fields.find(
            field => field.name === 'adjustedBaseline'
          ).fieldId,
          aggr: 3,
          label:
            this.details.mvProject.meter && this.details.mvProject.meter.name
              ? this.details.mvProject.meter.name + ' (Adjusted Baseline)'
              : 'Adjusted Baseline',
        },
        aliases: {
          actual: 'A',
        },
      }
      let actualdata = {
        parentId:
          this.details.mvProject && this.details.mvProject.meter
            ? this.details.mvProject.meter.id
            : null,
        name:
          this.details.mvProject.meter && this.details.mvProject.meter.name
            ? this.details.mvProject.meter.name + ' (Actual)'
            : 'Actual',
        yAxis: {
          fieldId: this.fields.id,
          aggr: 3,
          label:
            this.details.mvProject.meter && this.details.mvProject.meter.name
              ? this.details.mvProject.meter.name + ' (Actual)'
              : 'Actual',
        },
        aliases: {
          actual: 'B',
        },
      }
      let cummulative = {
        parentId: null,
        name:
          this.details.mvProject.meter && this.details.mvProject.meter.name
            ? this.details.mvProject.meter.name + ' (Actual)'
            : 'Actual',
        metaData: {},
        yAxis: {
          dataType: 3,
          label:
            this.details.mvProject.meter && this.details.mvProject.meter.name
              ? this.details.mvProject.meter.name + ' (Actual)'
              : 'Actual',
          unitStr: '',
        },
        aliases: {
          actual: 'C',
        },
        type: 2,
      }
      this.analyticsConfig.dataPoints.push(baselinedata)
      this.analyticsConfig.dataPoints.push(actualdata)
      this.analyticsConfig.dataPoints.push(cummulative)
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
