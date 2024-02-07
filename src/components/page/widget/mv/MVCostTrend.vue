<template>
  <div class="cost-trend-graph new-chart-css">
    <div class="width100 widget-header-2 row">
      <div class="col-8 text-left">Saving Trend</div>
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
      class="pT10"
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
        hidechartoptions: true,
        point: {
          show: true,
        },
        colors: {
          C: '#00a2f5',
          D: '#39c2b0',
        },
        zoom: {
          enabled: false,
        },
        hidetabular: true,
        hidecharttypechanger: true,
        showWidgetLegends: false,
        hideDataPoints: ['A', 'B'],
        axis: {
          y: {
            label: 'SAVINGS',
          },
        },
        axes: {
          C: 'y',
          D: 'y',
          A: 'y2',
          B: 'y2',
        },
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
        transformWorkflow: {
          parameters: [
            {
              name: 'data',
              typeString: 'list',
            },
            {
              name: 'aggr',
              typeString: 'map',
            },
          ],
          expressions: [],
        },
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
    targetSavingFieldId() {
      return this.details.submodules.mvtargetsaving.fields.find(
        field => field.name === 'targetsaving'
      ).fieldId
    },
    actualSavingFieldId() {
      return this.details.submodules.mvactualsaving.fields.find(
        field => field.name === 'actualsaving'
      ).fieldId
    },
  },
  mounted() {
    this.perepareConfig()
  },
  methods: {
    perepareConfig() {
      let parentId = this.details.mvProject.id
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
      let expressions = []
      let paramACriteria1 = {
        name: 'paramA',
        defaultFunctionContext: {
          nameSpace: 'map',
          functionName: 'get',
          params: "value, 'A'",
        },
      }
      let paramACriteria2 = {
        name: 'result',
        expr: 'paramA * 0.44',
      }
      let paramACriteria3 = {
        name: 'put',
        defaultFunctionContext: {
          nameSpace: 'map',
          functionName: 'put',
          params: "value, 'C', result",
        },
      }
      let paramBCriteria1 = {
        name: 'paramB',
        defaultFunctionContext: {
          nameSpace: 'map',
          functionName: 'get',
          params: "value, 'B'",
        },
      }
      let paramBCriteria2 = {
        name: 'result',
        expr: 'paramB * 0.44',
      }
      let paramBCriteria3 = {
        name: 'put',
        defaultFunctionContext: {
          nameSpace: 'map',
          functionName: 'put',
          params: "value, 'D', result",
        },
      }
      expressions = [
        paramACriteria1,
        paramBCriteria1,
        paramACriteria2,
        paramACriteria3,
        paramBCriteria2,
        paramBCriteria3,
      ]
      this.analyticsConfig.dataPoints = []
      this.analyticsConfig.transformWorkflow = {
        parameters: [
          {
            name: 'data',
            typeString: 'list',
          },
          {
            name: 'aggr',
            typeString: 'map',
          },
        ],
        expressions: [
          {
            workflowExpressionType: 2,
            iteratableVariable: 'data',
            loopVariableIndexName: 'index',
            loopVariableValueName: 'value',
            expressions: expressions,
          },
        ],
      }

      let targetSavings = {
        visible: false,
        parentId: parentId,
        name: 'Target Savings',
        reportModuleName: 'mvproject',
        aliases: {
          actual: 'A',
        },
        yAxis: {
          fieldId: this.targetSavingFieldId,
          aggr: 3,
        },
      }
      let actualSavings = {
        visible: false,
        parentId: parentId,
        name: 'Actual Savings',
        reportModuleName: 'mvproject',
        aliases: {
          actual: 'B',
        },
        yAxis: {
          fieldId: this.actualSavingFieldId,
          aggr: 3,
        },
      }
      let targetCost = {
        parentId: null,
        name: 'Targeted Saving',
        metaData: {},
        yAxis: {
          dataType: 3,
          label: 'Targeted Saving',
          unitStr: this.$currency,
        },
        aliases: {
          actual: 'C',
        },
        type: 2,
      }
      let savedCost = {
        parentId: null,
        name: 'Actual Saving',
        metaData: {},
        yAxis: {
          dataType: 3,
          label: 'Actual Saving',
          unitStr: this.$currency,
        },
        aliases: {
          actual: 'D',
        },
        type: 2,
      }
      this.analyticsConfig.dataPoints = [
        targetSavings,
        actualSavings,
        targetCost,
        savedCost,
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
.cost-trend-graph .bb-axis.bb-axis-y2,
.cost-trend-graph g.bb-ygrid-line.x-axis-zero-line {
  display: none;
}
/* .new-chart-css .fLegendContainer.fLegendContainer-new.fLegendContainer-right {
  justify-content: left;
  position: relative;
  right: 70px;
  top:10px;
}
.new-chart-css span.datapoint-leabel {
    font-size: 14px;
    font-weight: normal;
    font-style: normal;
    font-stretch: normal;
    line-height: normal;
    letter-spacing: 0.98px;
    color: #324056;
    padding-left: 10px;
}
.new-chart-css .fLegendContainer .el-color-picker__color-inner {
  height: 18px !important;
  width: 18px !important;
  top: -5px;
}
.new-chart-css .bb .bb-axis-x .tick text, .new-chart-css  .bb .bb-axis-y .tick text,.new-chart-css .bb .bb-axis-y2 .tick text {
  font-size: 10px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.04px;
  text-align: right;
  fill: #324056;
}
.new-chart-css  text.bb-axis-y-label, text.bb-axis-x-label {
  font-size: 11px;
    font-weight: normal;
    font-style: normal;
    font-stretch: normal;
    line-height: normal;
    letter-spacing: 0.98px;
    fill: #111213 !important;
} */
</style>
