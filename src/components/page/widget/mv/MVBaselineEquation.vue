<template>
  <div class="mv-energ-cost-comparison new-chart-css">
    <div class="width100 widget-header-2 row">
      <div class="col-6 pT10">{{ $t('mv.summary.consumption_vs_cost') }}</div>
      <div class="col-6 f12 text-right" v-if="!hideFilter">
        <el-select
          v-if="frequency === 1"
          v-model="selectedPeriod"
          placeholder="Filter"
          @change="changePeriod()"
          class="fc-input-full-border-h35 pR15"
        >
          <el-option
            v-for="item in period"
            :key="item.key"
            :label="item.label"
            :value="item.label"
          ></el-option>
        </el-select>
        <el-select
          v-model="selectedMonth"
          @change="getData('month')"
          placeholder="Month"
          class="fc-input-full-border-h35 pR15"
          v-if="analyticsConfig.period !== 10"
        >
          <el-option
            v-for="item in months"
            :key="item.key"
            :label="item.label"
            :value="item.key"
          ></el-option>
        </el-select>
        <el-select
          v-model="selectedYear"
          @change="getData('year')"
          placeholder="Year"
          class="fc-input-full-border-h35"
        >
          <el-option
            v-for="item in years"
            :key="item.key"
            :label="item.label"
            :value="item.key"
          ></el-option>
        </el-select>
      </div>
    </div>
    <f-new-analytic-report
      v-if="!loading"
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
  props: ['details', 'hideFilter'],
  mixins: [AnalyticsMixin],
  components: {
    FNewAnalyticReport,
  },
  data() {
    return {
      loading: true,
      baseLineList: null,
      fields: null,
      selectedPeriod: '',
      fullMonth: [],
      months: [],
      years: [],
      period: [
        {
          label: 'Daily',
          value: 12,
          frequency: 1,
          period: 'month',
        },
        {
          label: 'Monthly',
          value: 10,
          frequency: 3,
          period: 'year',
        },
      ],
      selectedMonth: null,
      selectedYear: null,
      analyticsConfig: {
        name: 'Building Analysis',
        key: 'BUILDING_ANALYSIS',
        analyticsType: this.isSiteAnalysis ? 6 : 2,
        hidedatepicker: true,
        hidechartoptions: true,
        point: {
          show: true,
        },
        hidetabular: false,
        hidechart: true,
        hidecharttypechanger: true,
        showWidgetLegends: false,
        axis: {
          y: {
            label: 'SAVINGS',
          },
        },
        type: 'reading',
        period: 12,
        mode: 4,
        baseLine: null,
        dateFilter: {},
        chartViewOption: 0,
        dataPoints: [],
        chartType: 'line',
        predictionTimings: [],
        tableConfig: {
          hideIndex: true,
          removeNegativeValue: true,
        },
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
          expressions: [
            {
              workflowExpressionType: 2,
              iteratableVariable: 'data',
              loopVariableIndexName: 'index',
              loopVariableValueName: 'value',
              expressions: [],
            },
          ],
        },
      },
    }
  },
  mounted() {
    this.setDefaultValues()
    this.loadPickList()
  },
  computed: {
    frequency() {
      if (this.details.mvProject && this.details.mvProject.frequency) {
        return this.details.mvProject.frequency
      } else {
        return 1
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
    setDefaultValues() {
      if (!this.hideFilter) {
        this.prepareFilter()
        if (this.details.mvProject.reportingPeriodEndTime) {
          this.selectedYear =
            Number(
              moment(this.details.mvProject.reportingPeriodEndTime).format(
                'YYYY'
              )
            ) > Number(moment().format('YYYY'))
              ? moment().format('YYYY')
              : moment(this.details.mvProject.reportingPeriodEndTime).format(
                  'YYYY'
                )
          this.selectedMonth =
            Number(
              moment(this.details.mvProject.reportingPeriodEndTime).format(
                'YYYYMM'
              )
            ) > Number(moment().format('YYYYMM'))
              ? moment().format('MM-YYYY')
              : moment(this.details.mvProject.reportingPeriodEndTime).format(
                  'MM-YYYY'
                )
          this.months = this.fullMonth.filter(rt =>
            rt.key.includes(this.selectedYear)
          )
          if (this.frequency === 1) {
            let value = this.months.find(rt => rt.key === this.selectedMonth)
              .value
            this.setdateFilter(value)
          } else {
            let value = this.years.find(rt => rt.key === this.selectedYear)
              .value
            this.setdateFilter(value)
          }
          if (this.frequency !== 1) {
            this.analyticsConfig.period = 10
          }
          this.selectedPeriod = this.period.find(
            rt => rt.value === this.analyticsConfig.period
          ).label
        }
      } else {
        this.analyticsConfig.period = 10
        this.setdateFilter([
          this.details.mvProject.reportingPeriodStartTime,
          this.details.mvProject.reportingPeriodEndTime,
        ])
      }
    },
    prepareFilter() {
      this.months = []
      this.years = []
      this.fullMonth = []
      let actualStartTime = this.details.mvProject.reportingPeriodStartTime
      let actualEndTime = this.details.mvProject.reportingPeriodEndTime
      let dateStart = moment(actualStartTime)
      let dateEnd = moment(actualEndTime)
      let yearStart = moment(actualStartTime)
      let yearEnd = moment(actualEndTime)
      while (
        dateEnd > dateStart ||
        dateStart.format('M') === dateEnd.format('M')
      ) {
        this.months.push({
          label: dateStart.format('MMM'),
          value: [
            dateStart.format('MM-YYYY') ===
              moment(this.details.mvProject.reportingPeriodStartTime).format(
                'MM-YYYY'
              ) &&
            dateStart
              .tz(this.$timezone)
              .startOf('month')
              .valueOf() > this.details.mvProject.reportingPeriodStartTime
              ? this.details.mvProject.reportingPeriodStartTime
              : dateStart
                  .tz(this.$timezone)
                  .startOf('month')
                  .valueOf(),
            dateStart.format('MM-YYYY') ===
              moment(this.details.mvProject.reportingPeriodEndTime).format(
                'MM-YYYY'
              ) &&
            dateStart
              .tz(this.$timezone)
              .endOf('month')
              .valueOf() > this.details.mvProject.reportingPeriodStartTime
              ? this.details.mvProject.reportingPeriodStartTime
              : dateStart
                  .tz(this.$timezone)
                  .endOf('month')
                  .valueOf(),
          ],
          key: dateStart.format('MM-YYYY'),
          year: dateStart.format('YYYY'),
        })
        dateStart.add(1, 'month')
      }
      this.fullMonth = this.$helpers.cloneObject(this.months)
      while (
        yearEnd > yearStart ||
        yearStart.format('YYYY') === yearEnd.format('YYYY')
      ) {
        this.years.push({
          label: yearStart.format('YYYY'),
          value: [
            yearStart.format('YYYY') ===
              moment(this.details.mvProject.reportingPeriodStartTime).format(
                'YYYY'
              ) &&
            yearStart
              .tz(this.$timezone)
              .startOf('year')
              .valueOf() > this.details.mvProject.reportingPeriodStartTime
              ? this.details.mvProject.reportingPeriodStartTime
              : yearStart
                  .tz(this.$timezone)
                  .startOf('year')
                  .valueOf(),
            yearStart.format('YYYY') ===
              moment(this.details.mvProject.reportingPeriodEndTime).format(
                'YYYY'
              ) &&
            yearStart
              .tz(this.$timezone)
              .endOf('year')
              .valueOf() > this.details.mvProject.reportingPeriodEndTime
              ? this.details.mvProject.reportingPeriodEndTime
              : yearStart
                  .tz(this.$timezone)
                  .endOf('year')
                  .valueOf(),
          ],
          key: yearStart.format('YYYY'),
        })
        yearStart.add(1, 'year')
      }
    },
    changePeriod() {
      this.analyticsConfig.period = this.period.find(
        rt => rt.label === this.selectedPeriod
      ).value
      this.getData(
        this.period.find(rt => rt.label === this.selectedPeriod).period
      )
    },
    getData(period) {
      if (this.details.mvProject) {
        let value = []
        if (period === 'year') {
          this.analyticsConfig.dateFilter = NewDateHelper.getDatePickerObject(
            64,
            this.years.find(rt => rt.key === this.selectedYear).value
          )
          this.months = this.fullMonth.filter(
            rt => rt.year === this.selectedYear
          )
          this.selectedMonth = this.months.find(
            rt =>
              rt.label ===
              (this.fullMonth.find(r => r.key === this.selectedMonth)
                ? this.fullMonth.find(r => r.key === this.selectedMonth).label
                : this.months[this.months.length - 1].label)
          ).key
          if (this.analyticsConfig.period === 10) {
            value = this.years.find(rt => rt.key === this.selectedYear)
              ? this.years.find(rt => rt.key === this.selectedYear).value
              : this.years[this.years.length - 1].value
            this.setdateFilter(value)
          } else {
            value = this.months.find(rt => rt.key === this.selectedMonth)
              ? this.months.find(rt => rt.key === this.selectedMonth).value
              : this.months[this.months.length - 1].value
            this.setdateFilter(value)
          }
        } else {
          this.analyticsConfig.dateFilter = NewDateHelper.getDatePickerObject(
            64,
            this.months.find(rt => rt.key === this.selectedMonth).value
          )
          value = this.months.find(rt => rt.key === this.selectedMonth).value
          this.setdateFilter(value)
        }
      }
    },
    setdateFilter(value) {
      if (this.details.mvProject) {
        this.analyticsConfig.dateFilter = NewDateHelper.getDatePickerObject(
          64,
          value
        )
        this.analyticsConfig.dateFilter['value'] = value
      }
    },
    perepareConfig() {
      this.analyticsConfig.dataPoints = []
      let paramAExp = {
        name: 'paramA',
        defaultFunctionContext: {
          nameSpace: 'map',
          functionName: 'get',
          params: "value, 'A'",
        },
      }
      let paramBExp = {
        name: 'paramB',
        defaultFunctionContext: {
          nameSpace: 'map',
          functionName: 'get',
          params: "value, 'B'",
        },
      }
      let energySavingsExp = [
        {
          name: 'energySavings',
          expr: 'paramB - paramA',
        },
        {
          name: 'savingsMap',
          defaultFunctionContext: {
            nameSpace: 'map',
            functionName: 'put',
            params: "value, 'C', energySavings",
          },
        },
      ]
      let actualCostExp = [
        {
          name: 'actualCost',
          expr: 'paramA * 0.44',
        },
        {
          name: 'actualCostMap',
          defaultFunctionContext: {
            nameSpace: 'map',
            functionName: 'put',
            params: "value, 'D', actualCost",
          },
        },
      ]
      let baselineCostExp = [
        {
          name: 'baselineCost',
          expr: 'paramB * 0.44',
        },
        {
          name: 'baselineCostMap',
          defaultFunctionContext: {
            nameSpace: 'map',
            functionName: 'put',
            params: "value, 'E', baselineCost",
          },
        },
      ]
      let costSavingsExp = [
        {
          name: 'paramD',
          defaultFunctionContext: {
            nameSpace: 'map',
            functionName: 'get',
            params: "value, 'D'",
          },
        },
        {
          name: 'paramE',
          defaultFunctionContext: {
            nameSpace: 'map',
            functionName: 'get',
            params: "value, 'E'",
          },
        },
        {
          name: 'costSavings',
          expr: 'paramE - paramD',
        },
        {
          name: 'costSavingsMap',
          defaultFunctionContext: {
            nameSpace: 'map',
            functionName: 'put',
            params: "value, 'F', costSavings",
          },
        },
      ]

      this.analyticsConfig.transformWorkflow.expressions[0].expressions = [
        paramAExp,
        paramBExp,
        ...energySavingsExp,
        ...actualCostExp,
        ...baselineCostExp,
        ...costSavingsExp,
      ]

      let baselinedata = {
        visible: false,
        name: 'ADJUSTED BASELINE ENERGY',
        parentId: this.details.mvProject.id,
        reportModuleName: 'mvproject',
        yAxis: {
          fieldId:
            this.details.baselines && this.details.baselines.length
              ? this.details.submodules.mvbaselinewithadjustmentreading.fields.find(
                  field => field.name === 'adjustedBaseline'
                ).fieldId
              : null,
          aggr: 3,
          label: 'ADJUSTED BASELINE ENERGY',
          unitStr: 'kWh',
        },
        aliases: {
          actual: 'B',
        },
      }
      let actualdata = {
        visible: false,
        parentId:
          this.details.mvProject && this.details.mvProject.meter
            ? this.details.mvProject.meter.id
            : null,
        name: 'ACTUAL ENERGY',
        aliases: {
          actual: 'A',
        },
        yAxis: {
          fieldId: this.fields.id,
          aggr: 3,
        },
      }
      let savedEnergy = {
        parentId: null,
        name: 'ENERGY SAVINGS',
        metaData: {},
        yAxis: {
          dataType: 3,
          label: 'ENERGY SAVINGS',
          unitStr: 'kWh',
        },
        aliases: {
          actual: 'C',
        },
        type: 2,
      }
      let actualCost = {
        parentId: null,
        name: 'ACTUAL COST',
        metaData: {},
        yAxis: {
          dataType: 3,
          label: 'ACTUAL COST',
          unitStr: this.$currency,
        },
        aliases: {
          actual: 'D',
        },
        type: 2,
      }
      let baselineCost = {
        parentId: null,
        name: 'ADJUSTED BASELINE COST',
        metaData: {},
        yAxis: {
          dataType: 3,
          label: 'ADJUSTED BASELINE COST',
          unitStr: this.$currency,
        },
        aliases: {
          actual: 'E',
        },
        type: 2,
      }
      let savedCost = {
        parentId: null,
        name: 'SAVED COST',
        metaData: {},
        yAxis: {
          dataType: 3,
          label: 'SAVED COST',
          unitStr: this.$currency,
        },
        aliases: {
          actual: 'F',
        },
        type: 2,
      }
      this.analyticsConfig.dataPoints = [
        baselinedata,
        actualdata,
        savedEnergy,
        baselineCost,
        actualCost,
        savedCost,
      ]
      this.loading = false
    },
    onReportLoaded() {
      this.$emit('reportLoaded')
    },
  },
}
</script>

<style lang="scss">
.mv-energ-cost-comparison {
  .widget-header-2 {
    font-size: 16px;
    font-weight: 500;
    font-style: normal;
    font-stretch: normal;
    line-height: normal;
    letter-spacing: 0.5px;
    color: #333333;
    padding: 30px 20px;
    border-bottom: solid 1px #eceef1;
    padding-bottom: 15px;
    padding-top: 15px;
  }
  .new-analytics-table .tabular-report-table th {
    text-align: right !important;
    padding-left: 20px !important;
    padding-right: 20px !important;
    font-size: 11px;
    font-weight: 500;
    line-height: normal;
    letter-spacing: 1px;
    color: #324056;
    border: 1px solid #f2f5f6 !important;
    border-top: none !important;
    font-weight: 600 !important;
    white-space: nowrap !important;
  }
  .tabular-report-table {
    border-top: none;
    border-left: none;
    border-right: none;
    padding-bottom: 280px;
    margin-bottom: 280px;
  }
  .tabular-report-table .tabular-data-td {
    border: 1px solid #f2f5f6 !important;
  }
  .new-analytics-table .tabular-report-table th:first-child {
    text-align: left !important;
  }
  .new-analytics-table .tabular-report-table th:first-child,
  .new-analytics-table .tabular-report-table td:first-child {
    border-left: none !important;
    width: 150px !important;
    min-width: 150px !important;
  }
  .new-analytics-table .tabular-report-table th:last-child,
  .new-analytics-table .tabular-report-table td:last-child {
    border-right: none !important;
  }
  .tabular-report-table .tabular-data-td {
    width: 186px !important;
    min-width: 186px !important;
    padding-left: 20px !important;
    padding-right: 20px !important;
    font-size: 12.5px;
    font-weight: normal;
    line-height: 22px;
    letter-spacing: 0.5px;
    color: #324056;
    white-space: nowrap !important;
  }
  .new-analytics-table .tabular-report-table th .inline {
    padding-left: 20px;
    padding-right: 20px;
  }
  .tablular-container {
    padding-bottom: 170px;
  }
  .tabular-report-table .scrollable {
    height: inherit !important;
  }
}

.mv-energ-cost-comparison .bb-axis.bb-axis-y2 {
  display: none;
}
.mv-energ-cost-comparison .analytics-section,
.mv-energ-cost-comparison {
  margin: 0px;
}
.mv-energ-cost-comparison .new-analytics-table {
  margin: 0px;
  height: 100vh;
  overflow-y: scroll;
}
.mv-energ-cost-comparison .new-analytics-table .tabular-report-table th {
  height: 50px;
  color: #324056 !important;
  font-size: 10px !important;
}
</style>
