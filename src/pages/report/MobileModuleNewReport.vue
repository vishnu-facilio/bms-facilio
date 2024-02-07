<template>
  <div class="height100 mobile-report">
    <div class="datefilter-hide-overlay"></div>

    <div v-if="showDatePicker" style="text-align: center;">
      <new-date-picker
        :zone="$timezone"
        class="filter-field date-filter-comp-new-report"
        :dateObj="dateFilter"
        @date="setDateFilter"
      ></new-date-picker>
    </div>
    <spinner v-if="loading" :show="loading" size="80"></spinner>

    <div
      v-else-if="failed"
      style="margin-top: 30px;opacity: 0.7;font-size: 13px;padding: 50px;line-height: 25px;text-align: center;"
    >
      <div>{{ $t('home.dashboard.data_loading_failed') }}</div>
    </div>

    <!-- if no data is available -->
    <div
      v-else-if="noData"
      style="font-size: 13px;padding: 80px 50px 50px 50px;line-height: 25px;text-align: center;"
    >
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
    <!-- -->

    <!-- chart generation start -->
    <div class="height100" v-else>
      <!-- <f-chart-settings v-if="false" :settings="reportObj.options.settings"  :showChartMode="(reportObj.data && Object.keys(reportObj.data).length > 2)" :showAlarm="false" :showSafelimit="false"></f-chart-settings> -->
      <div :class="{ height100: config }">
        <f-mobile-chart
          :showWidgetLegends="false"
          ref="newChart"
          @mobiledrilldown="drilldown"
          :width="config && config.width ? config.width : null"
          :resultObj="resultObj"
          :height="config && config.height ? config.height : null"
          :data="reportObj.data"
          :options="reportObj.options"
          :dateRange="reportObj.dateRange"
          :reportType="reportObj.reportType"
          :isWidget="config ? true : false"
          :hidecharttypechanger="config && config.widget"
        ></f-mobile-chart>
        <!-- <f-new-chart @drilldown="drilldown"   :showWidgetLegends="false" ref="newChart" :isWidget="config ? true : false" :width="config && config.width ? config.width : null" :resultObj="resultObj" :height="config && config.height ? config.height : null" :data="reportObj.data" :options="reportObj.options" :alarms="reportObj.alarms" :dateRange="reportObj.dateRange" :hidecharttypechanger="config && config.widget"></f-new-chart> -->
      </div>
      <div v-if="reportObj">
        <modular-new-tabular-report
          v-if="
            failed === false &&
              loading === false &&
              reportObj.options.settings.chart === false
          "
          ref="newTable"
          :reportObject="resultObj"
          :reportConfig="reportObj"
          class="new-analytics-table"
        ></modular-new-tabular-report>
        <!-- <el-tabs v-if="failed === false && loading === false && reportObj.options.settings.chart === true" v-model="activeReportTab" class="report-tab">
      <el-tab-pane label="Tabular Report" name="tabular">
        <modular-new-tabular-report ref="newTable" :reportObject="resultObj" :reportConfig="reportObj" class="new-analytics-table"></modular-new-tabular-report>
      </el-tab-pane>
      <el-tab-pane :label="'Underlying ' + getStringWithCaps(moduleName) +'s'"  name="underlying">
        <underlying-workorders :moduleName="moduleName" :reportObject="reportObj" :resultObject="resultObj" ></underlying-workorders>
      </el-tab-pane>
    </el-tabs> -->
      </div>
    </div>
    <!-- chart generation end -->
  </div>
</template>
<script>
import NewDatePicker from '@/NewDatePicker'
import FMobileChart from 'newcharts/components/FMobileChart'
import NewDataFormatHelper from 'src/pages/report/mixins/NewDataFormatHelper'
import ModularNewTabularReport from 'pages/report/components/ModularNewTabularReport'
import NewDateHelper from '@/mixins/NewDateHelper'
import ModularAnalyticmixin from 'src/pages/energy/analytics/mixins/ModularAnalyticmixin'
import { isEqual } from 'lodash'

export default {
  props: ['id', 'config', 'tabular', 'dbFilterJson', 'dbTimelineFilter'],
  mixins: [NewDataFormatHelper, ModularAnalyticmixin],
  components: {
    NewDatePicker,
    ModularNewTabularReport,
    FMobileChart,
    // FUserFilters
  },
  data() {
    return {
      activeReportTab: 'tabular',
      loading: true,
      failed: false,
      noData: false,
      reportObj: null,
      resultObj: null,
      showReportOptions: false,
      dateFilter: null,
      userFilterApplied: null,
      moduleName: null,
      showDatePicker: false,
    }
  },
  created() {
    //on report load , db date filter overrides report date filter . on further change of either ,the change is applied
    if (this.dbTimelineFilter) {
      let pickerObj = NewDateHelper.getDatePickerObject(
        this.dbTimelineFilter.operatorId,
        this.dbTimelineFilter.dateValueString
      )
      this.setDateFilter(pickerObj)
      console.log('date picker init from dashboard', pickerObj)
    }
  },
  mounted() {
    this.init()
  },
  computed: {
    reportId() {
      if (this.id) {
        return this.id
      } else {
        return this.config ? this.config.widget.dataOptions.newReportId : null
      }
    },
    appliedDateRange() {
      if (this.$route.query.daterange) {
        return JSON.parse(this.$route.query.daterange)
      }
    },
    appliedChartType() {
      // Temp...will be changed for pdf once temp report support is implemented
      return this.$route.query.charttype
    },
  },
  watch: {
    reportId: {
      handler(newData, oldData) {
        this.dateFilter = null
        this.init(false)
      },
    },
    dbFilterJson(newValue, oldValue) {
      if (!isEqual(newValue, oldValue)) {
        this.init()
      }
    },
    dbTimelineFilter(newValue, oldValue) {
      if (!isEqual(newValue, oldValue)) {
        let pickerObj = NewDateHelper.getDatePickerObject(
          newValue.operatorId,
          newValue.dateValueString
        )
        console.log('date picker change from dashboard', pickerObj)
        this.setDateFilter(pickerObj)
        this.datePickerCompKey++
      }
    },
    config: {
      handler(newData, oldData) {
        if (newData && oldData) {
          if (newData.id !== oldData.id) {
            this.init(false)
          } else {
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
    drilldown(val) {
      if (this.isXFieldCrossModule || this.isGroupByCrossModule) {
        console.log('Drill down not enabled for cross module')
      } else {
        this.handleBasicDrillDown(
          val,
          this.resultObj,
          this.reportObj,
          this.filters
        )
      }
    },
    setDateFilter(dateFilter) {
      this.dateFilter = dateFilter
      if (this.userFilterApplied) {
        this.applyFilter(this.userFilterApplied)
      } else {
        this.initchart()
      }
    },
    init() {
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

    initchart() {
      this.loading = true
      this.failed = false
      this.noData = false
      // this.reportObj = null
      // let query = {}
      // query['reportId'] = this.reportId
      // if (this.showDatePicker && this.dateFilter !== null) {
      //   query['startTime'] = this.dateFilter.value[0]
      //   query['endTime'] = this.dateFilter.value[1]
      // }
      let params = { reportId: this.reportId }
      if (this.dateFilter) {
        params.startTime = this.dateFilter.value[0]
        params.endTime = this.dateFilter.value[1]
      }
      if (this.filters) {
        params['filters'] = this.filters
      }
      if (this.dbFilterJson) {
        params['filters'] = JSON.stringify(this.dbFilterJson)
      }
      this.$http.post('/v2/report/executeReport', params).then(response => {
        console.log('This is for workorder reports')
        let result = this.specialHandler(response.data.result)
        let self = this
        if (result.reportData.data.length === 0) {
          self.noData = true
          self.loading = false
          self.moduleName = result.module.name
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
            }
          }
          result.xAggr = result.report.xAggr
          // this.reportObj = this.prepareReport(result)
          self.resultObj = result
          self.$emit('reportLoaded', null, response.data.result)
        } else {
          this.moduleName = result.module.name
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
            }
          } else {
            result['dateRange'] = self.dateFilter
          }
          result.xAggr = result.report.xAggr
          self.reportObj = this.prepareReport(result)
          //do not persist any options from old resultObj or from chartState while drilling down
          self.handleAxisChartState(self.reportObj, self.resultObj, result)
          self.resultObj = result
          if (self.appliedChartType) {
            self.reportObj.options.data.type = this.appliedChartType
          }
          console.log('before emit ')
          self.$emit('reportLoaded', self.reportObj, self.resultObj)
          self.loading = false
        }
      })
    },
  },
}
</script>

<style>
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
</style>
