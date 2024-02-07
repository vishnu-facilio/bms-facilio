<template>
  <div class="analytics-section">
    <div class="report-graph-header" v-if="!hideHeader">
      <div
        v-if="showDatePicker"
        style="text-align: center;"
        class="new-analytics-filter-section"
      >
        <new-date-picker
          ref="datePicker"
          :zone="$timezone"
          class="filter-field date-filter-comp"
          :dateObj="dateObj ? dateObj : serverConfig.dateFilter"
          @date="setDateFilter"
        ></new-date-picker>
      </div>
      <div
        v-if="showDatePicker && serverConfig.isCustomDateField !== true"
        class=""
      >
        <el-select
          v-model="serverConfig.xField.aggr"
          :placeholder="$t('common.products.select_aggregation')"
          class="fc-input-full-border-select2 fR width140px mR50"
        >
          <el-option
            v-for="(item, itemIdx) in timeAggregators"
            :key="itemIdx"
            :label="item.displayName"
            :value="item.value"
          ></el-option>
        </el-select>
      </div>
    </div>
    <div
      class="chart-icon pointer pB8 el-popover__reference"
      style="margin-left: 10px; position: absolute;"
      v-else-if="showPeriodSelect && !noData"
    >
      <div
        v-if="showPeriodSelect && serverConfig.isCustomDateField !== true"
        class=""
      >
        <el-select
          v-model="serverConfig.xField.aggr"
          :placeholder="$t('common.products.select_aggregation')"
          class="period-select width100 fwidget-report-period-select"
        >
          <el-option
            v-for="(item, itemIdx) in timeAggregators"
            :key="itemIdx"
            :label="item.displayName"
            :value="item.value"
          ></el-option>
        </el-select>
      </div>
    </div>
    <!-- Modular user filters-->
    <div v-if="showUserFilter">
      <ModularUserFilterRender
        @emitChangedValue="setUserFilter"
        :filterConfiguration="cloneArray(serverConfig.userFilters)"
      ></ModularUserFilterRender>
    </div>

    <spinner
      v-if="loading"
      :show="loading"
      size="80"
      class="analytics-spinner"
    ></spinner>

    <div
      v-else-if="failed"
      style="margin-top: 30px;opacity: 0.7;font-size: 13px;padding: 50px;line-height: 25px;text-align: center;"
    >
      <div>{{ $t('home.dashboard.data_loading_failed') }}</div>
    </div>

    <div
      v-else-if="noData"
      style="margin-top: 30px;font-size: 13px;padding: 50px;line-height: 25px;text-align: center;"
    >
      <inline-svg
        src="svgs/emptystate/reportlist"
        iconClass="icon text-center icon-100"
      ></inline-svg>
      <div class="nowo-label f13">
        {{ $t('common.wo_report.empty_data') }}
        <span class="bold" v-if="resultObject && resultObject.dateRange">{{
          resultObject.dateRange.value | formatPeriod('MMM DD, YYYY')
        }}</span>
      </div>
    </div>

    <div v-else>
      <div v-if="!serverConfig.hideChart && reportObject">
        <div>
          <FNewHeatMap
            v-if="isHeatMap"
            ref="heatmap"
            :config="serverConfig"
            :width="
              chartDimensions && chartDimensions.width
                ? chartDimensions.width
                : null
            "
            :height="
              chartDimensions && chartDimensions.height
                ? chartDimensions.height
                : null
            "
            :resultObject="resultObject"
            :reportObject="reportObject"
            :hidecharttypechanger="hidecharttypechanger"
          ></FNewHeatMap>
          <FWorkPlaceTreeMap
            v-else-if="isWorkPlaceTreeMap"
            ref="workplacetreemap"
            :config="serverConfig"
            :width="
              chartDimensions && chartDimensions.width
                ? chartDimensions.width
                : $el.clientWidth
                ? $el.clientWidth
                : null
            "
            :height="
              chartDimensions && chartDimensions.height
                ? chartDimensions.height
                : null
            "
            :resultObject="resultObject"
            :reportObject="reportObject"
          ></FWorkPlaceTreeMap>
          <f-mobile-chart
            v-else-if="$mobile"
            ref="newChart"
            :alarms="null"
            :resultObj="resultObject"
            :height="height || null"
            :width="width || null"
            :data="reportObject.data"
            :options="reportObject.options"
            :dateRange="reportObject.dateRange"
            :mergeOption="mergeOption"
            :hidecharttypechanger="hidecharttypechanger"
          ></f-mobile-chart>
          <f-new-chart
            v-if="!isHeatMap && !isWorkPlaceTreeMap"
            @drilldown="drilldown"
            :showWidgetLegends="
              reportObject && reportObject.options
                ? reportObject.options.widgetLegend.show
                : false
            "
            ref="newChart"
            :data="reportObject.data"
            :options="reportObject.options"
            :alarms="null"
            :dateRange="reportObject.dateRange"
            :resultObj="resultObject"
            :height="height || null"
            :width="width || null"
            :mergeOption="mergeOption"
            :hidecharttypechanger="hidecharttypechanger"
            :isWidget="isWidget"
          ></f-new-chart>

          <div
            v-if="
              reportObject &&
                (typeof hideTabs === 'undefined' || hideTabs === false)
            "
          >
            <modular-new-tabular-report
              v-if="
                reportObject.options.settings.chart === false &&
                  loading === false &&
                  failed === false
              "
              ref="newTable"
              :reportObject="resultObject"
              :reportConfig="reportObject"
              class="new-analytics-table"
            ></modular-new-tabular-report>
            <el-tabs
              v-if="
                reportObject.options.settings.chart === true &&
                  loading === false &&
                  failed === false
              "
              v-model="activeReportTab"
              class="report-tab"
            >
              <el-tab-pane label="Tabular Report" name="tabular">
                <modular-new-tabular-report
                  :moduleName="module.moduleName"
                  ref="newTable"
                  :reportObject="resultObject"
                  :reportConfig="reportObject"
                  v-if="!serverConfig.hidetabular"
                  class="new-analytics-table"
                ></modular-new-tabular-report>
              </el-tab-pane>
              <el-tab-pane
                :label="$t('home.dashboard.underlying') + ' ' + getModuleString"
                name="underlying"
              >
                <underlying-workorders
                  ref="underlyingWorkOrders"
                  :module="module"
                  :reportObject="reportObject"
                  :resultObject="resultObject"
                ></underlying-workorders>
              </el-tab-pane>
            </el-tabs>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import NewDatePicker from '@/NewDatePicker'
import FNewChart from 'newcharts/components/FNewChart'
import FMobileChart from 'newcharts/components/FMobileChart'
import FNewHeatMap from 'src/pages/energy/analytics/components/FNewHeatMap'
// import FMobileChart from 'newcharts/components/FMobileChart'
// import FMultiChart from 'newcharts/components/FMultiChart'
import NewDataFormatHelper from 'pages/report/mixins/NewDataFormatHelper'
import ModularNewTabularReport from 'pages/report/components/ModularNewTabularReport'
// import FChartSettings from 'newcharts/components/FChartSettings'
import NewDateHelper from 'src/components/mixins/NewDateHelper'
import UnderlyingWorkorders from 'src/pages/report/UnderlyingWorkorders'
import ModularAnalyticmixin from 'src/pages/energy/analytics/mixins/ModularAnalyticmixin'
import ModularUserFilterMixin from 'src/pages/report/mixins/modularUserFilter'
import ModularUserFilterRender from 'src/pages/energy/analytics/components/ModularUserFilterRender'
import FWorkPlaceTreeMap from 'src/pages/workplaceAnalytics/FWorkPlaceTreeMap.vue'
import { isEmpty } from '@facilio/utils/validation'
import deepmerge from 'util/deepmerge'
import { API } from '@facilio/api'

export default {
  props: [
    'serverConfig',
    'baseLines',
    'module',
    'reportid',
    'savedReport',
    'height',
    'width',
    'mergeOption',
    'hideTabs',
    'defaultChartType',
    'hideHeader',
    'hidecharttypechanger',
    'config',
    'isWidget',
    'showPeriodSelect',
    'hideUserFilter',
  ],
  mixins: [NewDataFormatHelper, ModularAnalyticmixin, ModularUserFilterMixin],
  components: {
    NewDatePicker,
    FNewChart,
    // FMultiChart,
    ModularNewTabularReport,
    FMobileChart,
    // FChartSettings,
    ModularUserFilterRender,
    UnderlyingWorkorders,
    FNewHeatMap,
    FWorkPlaceTreeMap,
  },
  data() {
    return {
      moduleName: null,
      moduleDisplayName: null,
      moduleId: null,
      moduleResourceField: null,
      dateObj: null,
      showDatePicker: false,
      showTabularReport: true,
      loading: true,
      // dateField: {},
      failed: false,
      reportObject: null,
      noData: false,
      resultObject: null,
      activeReportTab: 'tabular',
      chartDimensions: {
        width: 980,
        height: 800,
      },
      timeAggregators: [
        {
          displayName: this.$t('common.wo_report.high_res'),
          label: 'highres',
          value: 0,
          selected: false,
          field_Id: 0,
        },
        {
          displayName: this.$t('common.wo_report.hourly'),
          label: 'hourly',
          value: 20,
          selected: false,
          field_Id: 1,
        },
        {
          displayName: this.$t('common._common.daily'),
          label: 'daily',
          value: 12,
          selected: false,
          field_Id: 2,
        },
        {
          displayName: this.$t('common._common.weekly'),
          label: 'weekly',
          value: 11,
          selected: false,
          field_Id: 3,
        },
        {
          displayName: this.$t('common._common.monthly'),
          label: 'monthly',
          value: 10,
          selected: false,
          field_Id: 4,
        },
        {
          displayName: this.$t('common.products.quarterly'),
          label: 'quarterly',
          value: 25,
          selected: false,
          field_Id: 5,
        },
        {
          displayName: this.$t('common.header.hour_of_day'),
          label: 'hour_of_day',
          value: 19,
          selected: false,
          field_Id: 7,
        },
        {
          displayName: this.$t('common.header.day_of_week'),
          label: 'day_of_week',
          value: 17,
          selected: false,
          field_Id: 8,
        },
        {
          displayName: this.$t('common.header.day_of_month'),
          label: 'day_of_month',
          value: 18,
          selected: false,
          field_Id: 6,
        },
      ],
    }
  },
  created() {
    if (this.module) {
      this.moduleName = this.module['moduleName']
      this.moduleDisplayName = this.module['moduleDisplayName']
      this.moduleId = this.module['moduleId']
      this.moduleResourceField = this.module['resourceField']
    }
  },
  mounted() {
    if (typeof this.reportid !== 'undefined' && this.reportid !== null) {
      this.loadFromReportId()
    } else {
      this.init()
    }
  },
  watch: {
    serverConfig: {
      handler: function(newData, oldData) {
        this.initApiCall()
      },
      deep: true,
    },
    isHeatMap: {
      handler: function(newData, oldData) {
        if (newData !== oldData) {
          this.initApiCall()
        }
      },
      immediate: true,
    },
  },
  computed: {
    showUserFilter() {
      let { serverConfig, hideUserFilter } = this
      if (hideUserFilter) {
        return false
      }
      if (serverConfig?.userFilters && serverConfig.userFilters.length !== 0) {
        return true
      }
      return false
    },
    isHeatMap() {
      if (
        this.reportObject &&
        this.reportObject.options &&
        this.reportObject.options.type == 'heatmap'
      ) {
        return true
      } else {
        return false
      }
    },
    isWorkPlaceTreeMap() {
      if (this.defaultChartType && this.defaultChartType === 'treemap') {
        return true
      }
      if (
        this.reportObject?.options?.type &&
        this.reportObject.options.type == 'treemap'
      ) {
        return true
      }
      return false
    },
    isGroupByCrossModule() {
      if (this.resultObject.report.dataPoints[0].groupByFields !== null) {
        let groupField = this.resultObject.report.dataPoints[0].groupByFields[0]
        if (
          groupField.field.module.name ===
            this.resultObject.report.module.name ||
          (this.resultObject.report.module.extendModule &&
            groupField.field.module.name ===
              this.resultObject.report.module.extendModule.name)
        ) {
          return false
        }
        return true
      }
      return false
    },
    isXFieldCrossModule() {
      let xAxis = this.resultObject.report.dataPoints[0].xAxis
      if (
        xAxis.field.module.name === this.resultObject.report.module.name ||
        (this.resultObject.report.module.extendModule &&
          xAxis.field.module.name ===
            this.resultObject.report.module.extendModule.name)
      ) {
        return false
      }
      return true
    },
    getModuleString() {
      if (
        this.resultObject.report.module &&
        this.resultObject.report.module.name &&
        this.resultObject.report.module.name === 'newreadingalarm'
      ) {
        return 'Alarms'
      }
      if (
        this.resultObject.report.moduleType !== -1 &&
        this.resultObject.moduleTypes &&
        this.resultObject.moduleTypes.length !== 0
      ) {
        let moduleName = this.resultObject.moduleTypes.filter(
          type => type.type === this.resultObject.report.moduleType
        )[0].displayName
        return moduleName
      } else if (this.moduleDisplayName !== null) {
        return this.moduleDisplayName
      } else {
        return this.resultObject.report.module.name + 's'
      }
    },
  },
  methods: {
    cloneArray(arrayToClone) {
      if (Array.isArray(arrayToClone)) {
        return JSON.parse(JSON.stringify(arrayToClone))
      } else {
        return []
      }
    },
    setUserFilter(val) {
      this.$set(this.serverConfig, 'userFilters', val)
    },
    drilldown(val) {
      if (!this.$servicePortal) {
        if (this.isXFieldCrossModule || this.isGroupByCrossModule) {
          console.log('Drill down not enabled for cross module')
        } else {
          this.handleBasicDrillDown(val, this.resultObject, this.reportObject)
        }
      }
    },
    setDateFilter(dateFilter) {
      if (
        this.serverConfig.xField &&
        typeof this.showDatePicker !== 'undefined' &&
        this.showDatePicker === true
      ) {
        this.dateObj = dateFilter
        let dateField = {}
        dateField['operator'] = dateFilter.operatorId
        dateField['field_id'] =
          this.serverConfig.isTime === true
            ? this.serverConfig.xField.field_id
            : this.serverConfig.dateField.field_id
        if (
          this.serverConfig.isTime === true &&
          this.serverConfig.xField.nullFieldId === true
        ) {
          dateField['fieldName'] = dateField.field_id
          dateField['field_id'] = -1
        } else if (
          this.serverConfig.isTime === false &&
          dateField.field_id === -1
        ) {
          dateField['fieldName'] = this.serverConfig.dateField.fieldName
        }
        dateField['module_id'] =
          this.serverConfig.isCustomDateField === true
            ? this.module.meta.fieldMeta.customDateField
            : this.module.meta.fieldMeta.xField
        if (
          dateFilter.operatorId === 49 ||
          dateFilter.operatorId === 50 ||
          dateFilter.operatorId === 51
        ) {
          dateField['date_value'] = Math.abs(dateFilter.offset) + ''
        } else if (dateFilter.operatorId === 20) {
          dateField['date_value'] = dateFilter.value.join(',')
        } else {
          dateField['date_value'] = dateFilter.value[0] + ''
        }
        this.$set(this.serverConfig, 'dateField', dateField)
        // this.dateField = dateField
      } else {
        this.$set(this.serverConfig, 'dateField', null)
      }
    },
    init() {
      this.initApiCall()
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
    loadFromReportId() {
      this.loading = true
      this.failed = false
      this.noData = false
      if (this.reportid) {
        API.put('/v3/report/execute', {
          reportId: parseInt(this.reportid),
          moduleName: this.moduleName,
        }).then(({ data, error }) => {
          // if (response.data.result.reportData.data.length === 0) {
          //   this.noData = true
          //   this.loading = false
          // } else {
          if (data.error) {
            console.log(error)
            this.loading = false
            this.failed = true
          } else {
            let result = this.specialHandler(data)
            // time handling for report (special handling)
            let dateOperator = result.report.dateOperator
            let datePickerObject = null
            if (dateOperator !== -1) {
              datePickerObject = NewDateHelper.getDatePickerObject(
                dateOperator,
                result.report.dateValue
              )
              // this.showDatePicker = true
            }
            if (datePickerObject) {
              result.dateRange = datePickerObject
            }
            let report = this.prepareReport(result)
            report['report'] = result.report // added for emailReportDependency
            if (!report.options.type) {
              report.options.type = 'bar'
            }
            // this.showDatePicker = true
            this.loading = false
            this.$emit('reportLoaded', null, result)
            // }
          }
        })
      }
    },
    initApiCall() {
      console.log('Init api call')
      this.loading = true
      this.failed = false
      let chartType = null
      if (this.$refs['underlyingWorkOrders']) {
        this.$refs['underlyingWorkOrders'].rerender()
      }
      if (this.reportObject && this.reportObject.options.type === 'heatmap') {
        if (
          this.serverConfig.isTime === false ||
          (this.serverConfig.groupBy && !isEmpty(this.serverConfig.groupBy))
        ) {
          this.reportObject.options.type = 'bar'
        }
      }
      if (this.reportObject) {
        chartType = this.reportObject.options.type
      } else if (!isEmpty(this.defaultChartType)) {
        chartType = this.defaultChartType
      }
      if (
        this.serverConfig.isCustomDateField === true ||
        this.serverConfig.isTime === true
      ) {
        this.showDatePicker = true
      } else {
        this.showDatePicker = false
      }
      let woparams = {
        xField: this.serverConfig.xField,
        yField: this.serverConfig.yField ? this.serverConfig.yField : null,
        dateField:
          this.serverConfig.dateField &&
          (this.serverConfig.isTime || this.serverConfig.isCustomDateField)
            ? this.serverConfig.dateField
            : null,
        groupBy: this.serverConfig.groupBy ? this.serverConfig.groupBy : null,
        criteria: this.serverConfig.criteria
          ? this.serverConfig.criteria
          : null,
        having: this.serverConfig.having ? this.serverConfig.having : null,
        sortFields: this.serverConfig.sortFields
          ? this.serverConfig.sortFields
          : null,
        sortOrder: this.serverConfig.sortOrder
          ? this.serverConfig.sortOrder
          : null,
        limit: this.serverConfig.limit ? this.serverConfig.limit : null,
        userFilters: this.serverConfig.userFilters
          ? this.serverConfig.userFilters
          : null,
        moduleType: this.serverConfig.moduleType
          ? this.serverConfig.moduleType
          : null,
        moduleName:
          this.$helpers.isLicenseEnabled('NEW_ALARMS') &&
          this.moduleName === 'alarm'
            ? 'alarmoccurrence'
            : this.moduleName,
      }
      let baselines
      if (
        this.serverConfig.baseLine &&
        Array.isArray(this.serverConfig.baseLine) &&
        this.serverConfig.baseLine.length
      ) {
        if (this.serverConfig.baseLine[0] > 0) {
          let bl = {}
          bl.baseLineId = this.serverConfig.baseLine[0]
          if (this.serverConfig.baseLine.length > 1) {
            bl.adjustType = this.serverConfig.baseLine[1]
          }

          let blList = []
          blList.push(bl)

          baselines = blList
        }
      } else if (this.serverConfig.baseLine && this.serverConfig.baseLine > 0) {
        baselines = [
          {
            baseLineId: this.serverConfig.baseLine,
          },
        ]
      }
      if (baselines) {
        woparams.baseLines = JSON.stringify(baselines)
      }

      if (this.$org.id === 210 && woparams.dateField) {
        if ([541010, 541011].includes(woparams.dateField.field_id)) {
          woparams.dateField['allowFutureData'] = true
        }
      }
      if (this.reportObject && this.reportObject.options) {
        woparams.chartState = JSON.stringify(this.reportObject.options)
      } else if (
        this.savedReport &&
        this.savedReport.chartState &&
        this.savedReport.chartState.type === 'heatmap'
      ) {
        woparams.chartState = JSON.stringify(this.savedReport.chartState)
      }
      if (woparams.moduleName) {
        API.post('/v3/report/fetch_data', woparams, { force: true }).then(
          (resp, error) => {
            if (this.moduleDisplayName !== null && resp && resp.data) {
              resp.data.moduleDisplayName = this.moduleDisplayName
            }
            let report_chart_state = null
            if (this?.reportObject?.options) {
              report_chart_state = JSON.stringify(this.reportObject.options)
            }
            if (this?.savedReport?.chartState) {
              report_chart_state = JSON.stringify(this.savedReport.chartState)
            }
            let result = this.specialHandler(resp.data, report_chart_state)

            if (result.reportData.data.length === 0) {
              this.noData = true
            } else {
              this.noData = false
            }
            result.mode = this.serverConfig.mode
            result.dateRange = this.dateObj
              ? this.dateObj
              : this.serverConfig['dateFilter']
            result.xAggr = result.report.xAggr
            let localChartType = null
            if (result.report && result.report.chartState) {
              let state = JSON.parse(result.report.chartState)
              localChartType = state.type
            }
            if (this.reportObject) {
              result.report.chartState = this.reportObject.options
            }
            if (localChartType === 'heatmap') {
              if (result.report.chartState.type) {
                result.report.chartState.type = 'heatmap'
              } else {
                result.report.chartState = JSON.parse(result.report.chartState)
                result.report.chartState.type = localChartType
              }
            }
            if (
              typeof this.savedReport !== 'undefined' &&
              this.savedReport !== null &&
              this.reportObject === null
            ) {
              result.report.chartState = this.savedReport.chartState
            }
            let robj = this.prepareReport(result)
            robj['report'] = result.report // added for emailReportDependency
            this.handleAxisChartState(robj, this.resultObject, result)
            if (localChartType === 'heatmap') {
              robj.options.type = localChartType
            } else {
              if (
                chartType &&
                (!Object.hasOwnProperty('isSystemGroup') ||
                  robj.options.isSystemGroup === false)
              ) {
                robj.options.type = chartType
              } else if (robj.options.type !== 'stackedbar') {
                robj.options.type = 'bar'
              }
            }

            if (
              !this.reportObject &&
              this.savedReport &&
              this.savedReport.chartState
            ) {
              if (typeof this.savedReport.chartState === 'object') {
                robj.options.type = this.savedReport.chartState.type
              } else {
                let chartState = JSON.parse(this.savedReport.chartState)
                robj.options.type = chartState.type
              }
            }
            robj.options['isTime'] = this.serverConfig.isTime
            if (this.serverConfig.customizeC3 && robj.options) {
              robj.options['customizeC3'] = this.serverConfig.customizeC3
            }
            if (this.serverConfig.customizeChartOptions && robj.options) {
              robj.options = deepmerge.objectAssignDeep(
                robj.options,
                this.serverConfig.customizeChartOptions
              )
            }
            this.reportObject = robj
            this.resultObject = result
            console.log('report object workorder')
            console.log(this.reportObject)
            this.loading = false
            this.$emit('reportLoaded', this.reportObject, this.resultObject)
          }
        )
      }
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

.dp-axis-selector ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.dp-axis-selector ul li {
  padding: 8px 20px;
  vertical-align: middle;
  cursor: pointer;
}

.dp-axis-selector ul li {
  padding: 10px 20px;
}

.dp-axis-selector .dp-axis-label {
  top: 0;
}

.newanalytics .fc-new-chart.bb {
  /* padding-bottom: 30px; */
  background: #fff;
}
.empty-multi-chart {
  height: 80vh;
  text-align: center;
  align-items: center;
  justify-content: center;
  margin: auto;
  width: 50vh;
  margin-top: 30px;
  white-space: nowrap;
}
.fwidget-report-period-select {
  z-index: 100;
}
.fwidget-report-period-select input {
  border-radius: 3px !important;
  background-color: #ffffff !important;
  border: solid 1px #d0d9e2 !important;
  padding-left: 10px !important;
  letter-spacing: 0.4px !important;
  color: #324056 !important;
}
</style>
