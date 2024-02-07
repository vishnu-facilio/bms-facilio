<template>
  <div class="text-center">
    <div class="row">
      <div class="col-12">
        <date-picker
          class="filter-field date-filter-comp"
          :mills="
            config.dateFilter.actual_time
              ? config.dateFilter.actual_time
              : config.dateFilter.time
          "
          :filter="config.dateFilter.filter"
          @data="setDateFilter"
        ></date-picker>
        <div
          style="float: right; margin-top: -28px; margin-right: 30px;"
          v-if="
            (this.config.chartType && this.config.chartType !== 'heatMap') ||
              currentChartType
          "
        >
          <span
            title="Toggle Alarms"
            v-tippy
            data-arrow="true"
            class="fanalytics-alarm-toggle"
            v-bind:class="{ 'fchart-alarm-toggle-on': showAlarm === false }"
            v-if="
              report &&
                report.data &&
                report.data[0].alarms &&
                report.data[0].alarms.length &&
                this.config.chartType !== 'heatMap'
            "
            @click="changeRenderAlarm"
            ><i class="material-icons alarm-icon">alarm</i></span
          >
          <span
            v-if="
              typeof config.chartTypeChangable === 'undefined' ||
                config.chartTypeChangable
            "
          >
            <span style="display: inline-block;" class="chart-icon"
              ><chart-icon
                :icon="currentChartType ? currentChartType.ct : null"
              ></chart-icon
            ></span>
            <q-popover ref="charttypepopover">
              <q-list link class="scroll" style="min-width: 150px">
                <q-item
                  v-for="(ctype, index) in chartType.availableChartTypes"
                  :key="index"
                  @click="
                    changeChartType(ctype), $refs.charttypepopover.close()
                  "
                  v-if="!ctype.disabled"
                >
                  <span class="chart-icon" style="margin-right: 5px;">
                    <chart-icon :icon="ctype.ct"></chart-icon>
                  </span>
                  <span class="chart-label">{{ ctype.label }}</span>
                </q-item>
              </q-list>
            </q-popover>
          </span>
          <span
            title="Settings"
            v-if="report && report.options.is_combo"
            v-tippy
            data-arrow="true"
            style="display: inline-block;color: rgb(239, 80, 143);cursor: pointer;position: relative;top: -3px; margin-left: 14px;"
          >
            <el-popover
              popper-class="report-settings-popover"
              ref="popover2"
              placement="bottom"
              title="Settings"
              v-model="reportSettingsPopover"
              width="400"
              trigger="click"
            >
              <div style="padding: 12px;">
                <f-report-settings
                  @close="closeSettingsPopover()"
                  @save="saveSettingsPopover()"
                  :report="report"
                ></f-report-settings>
              </div>
            </el-popover>
            <i v-popover:popover2 class="fa fa-cog"></i>
          </span>
          <span
            title="Options"
            v-if="config.regression"
            v-tippy
            data-arrow="true"
            style="display: inline-block;color: rgb(239, 80, 143);cursor: pointer;position: relative;top: -3px; margin-left: 14px;"
          >
            <el-popover
              class="report-options-popover"
              ref="popover2"
              placement="bottom"
              title="Options"
              width="200"
              trigger="click"
            >
              <div style="padding: 12px;">
                <el-checkbox
                  @change="onExcludeWeekendChange"
                  v-model="excludeWeekends"
                  >Exclude Weekends</el-checkbox
                >
              </div>
            </el-popover>
            <i v-popover:popover2 class="fa fa-cog"></i>
          </span>
        </div>
      </div>
    </div>
    <spinner v-if="loading" :show="loading" size="80"></spinner>
    <div v-else-if="error" class="mT30">
      {{ $t('common._common.load_data_failed') }}
    </div>
    <div v-else-if="!report || !report.data" class="mT30">
      {{ $t('common._common.no_data_available') }}
    </div>
    <div v-else>
      <div class="fchart-overlay" v-if="chartUpdating"></div>
      <div v-if="config.type === 'peak'">
        <f-peak
          :width="600"
          :height="450"
          :data="report.data"
          :alarms="report.data[0].alarms"
          :type="chartType.chartType.ct"
        ></f-peak>
      </div>
      <div
        v-else
        v-bind:class="{ 'analytics-alarm-toggle-on': showAlarm === false }"
      >
        <f-timeseries
          ref="fTimeseriesChart"
          :width="600"
          :height="450"
          :data="report.data"
          :alarms="report.data[0].alarms"
          :type="report.options.type"
          v-if="report.options.type === 'timeseries'"
        ></f-timeseries>
        <f-chart
          ref="fchart en-fchart"
          :type="report.options.type"
          :data="report.data"
          :options="report.options"
          :alarms="report.data[0].alarms"
          :width="600"
          :height="getChartHeight(report.options.type)"
          @drilldown="handleDrillDown"
          v-else
        ></f-chart>
      </div>
      <div class="reports-underlyingdata mT30 mB100" v-if="showTabularData">
        <f-tabular-data
          :data="actualReport ? actualReport.data : report.data"
          :options="actualReport ? actualReport.options : report.options"
          :multi="
            actualReport ||
              report.options.type === 'timeseries' ||
              report.options.is_combo
          "
          ref="tabulardata"
        ></f-tabular-data>
      </div>
    </div>
  </div>
</template>

<script>
import tooltip from '@/graph/mixins/tooltip'
import FChart from 'charts/FChart'
import FTimeseries from 'charts/FTimeseries'
import FPeak from 'charts/FPeak'
import FTabularData from 'pages/report/components/FTabularData'
// import formatter from 'charts/helpers/formatter'
import colors from 'charts/helpers/colors'
import DatePicker from '@/DatePicker'
import ChartIcon from 'charts/components/chartIcon'
import ReportUtil from 'pages/report/mixins/ReportUtil'
import ChartType from 'pages/report/mixins/ChartType'
import FReportSettings from 'pages/report/components/FReportSettings'
import JumpToHelper from '@/mixins/JumpToHelper'
import { QPopover, QList, QItem } from 'quasar'
export default {
  mixins: [ReportUtil, ChartType, JumpToHelper],
  props: ['config'],
  components: {
    FChart,
    FPeak,
    FTimeseries,
    FTabularData,
    DatePicker,
    QPopover,
    QList,
    QItem,
    ChartIcon,
    FReportSettings,
  },
  data() {
    return {
      loading: true,
      chartUpdating: false,
      error: false,
      showAlarm: true,
      report: null,
      actualReport: null,
      consolidateDataPoints: null,
      chartType: null,
      excludeWeekends: false,
      reportSettingsPopover: false,
    }
  },
  computed: {
    currentChartType() {
      if (this.chartType) {
        return this.chartType.chartType
      }
      return null
    },
    showTabularData() {
      if (
        typeof this.config.tabulardata === 'undefined' ||
        this.config.tabulardata
      ) {
        return true
      }
      return false
    },
  },
  watch: {
    config: {
      handler(newData, oldData) {
        this.init(false)
      },
      deep: true,
    },
    report: {
      handler(newData, oldData) {
        this.$emit('report', this.report)
      },
      deep: true,
    },
  },
  mounted() {
    this.init(true)
  },
  destroyed() {
    tooltip.hideTooltip()
  },
  methods: {
    init(firstTime) {
      this.cType = this.currentChartType
      this.$emit('change', this.config)
      let self = this
      if (
        this.config &&
        this.config.dataPoints &&
        this.config.dataPoints.length
      ) {
        if (firstTime) {
          self.loading = true
          self.chartUpdating = false
        } else {
          self.chartUpdating = true
        }
        let index = 0
        let dataList = []
        let colorMap = {}

        let dataPointsWithBaseLine = []
        let uniqueId = 1
        for (let dataPoint of this.config.dataPoints) {
          dataPoint.id = dataPoint.id || uniqueId++
          dataPointsWithBaseLine.push(dataPoint)

          if (this.config.baseLine) {
            let dataPointClone = self.$helpers.cloneObject(dataPoint)
            dataPointClone.baseLine = this.config.baseLine
            dataPointsWithBaseLine.push(dataPointClone)
          }
        }
        if (this.config.chartViewOption && this.config.chartViewOption === 1) {
          let consolidateDataPoints = {}
          for (let data = 0; data < this.config.dataPoints.length; data++) {
            consolidateDataPoints[
              this.config.dataPoints[data].parentId
            ] = this.config.dataPoints[data].readingFieldId
          }
          this.loadConsolidationReadingData(
            dataPointsWithBaseLine,
            this.config.dateFilter,
            dataList,
            index,
            colorMap,
            consolidateDataPoints
          ).then(function(response) {
            let nodata = true
            for (let d of dataList) {
              if (d.data.length) {
                nodata = false
                break
              }
            }
            let reportObj = {
              data: nodata ? null : dataList,
              options: dataList[0].options,
              multi: true,
            }
            if (self.config.regression) {
              self.actualReport = reportObj
            } else {
              self.actualReport = null
            }
            if (reportObj.options.is_highres_data) {
              self.config.chartType = null
            }
            reportObj.options.type = self.config.chartType
              ? self.config.chartType
              : self.chartType
              ? self.chartType.chartType.ct
              : 'timeseries'
            reportObj.options.is_combo = self.config.chartType === 'combo'
            self.report = self.combineReportData(reportObj)
            self.chartType = self.getChartType(self.report)
            if (firstTime) {
              self.loading = false
            } else {
              self.chartUpdating = false
            }
            self.setPointSettings()
          })
        } else {
          this.loadReadingData(
            dataPointsWithBaseLine,
            this.config.dateFilter,
            dataList,
            index,
            colorMap
          ).then(function(response) {
            let nodata = true
            for (let d of dataList) {
              if (d.data.length) {
                nodata = false
                break
              }
            }
            let reportObj = {
              data: nodata ? null : dataList,
              options: dataList[0].options,
              multi: true,
            }
            if (self.config.regression) {
              self.actualReport = reportObj
            } else {
              self.actualReport = null
            }
            if (reportObj.options.is_highres_data) {
              self.config.chartType = null
            }
            reportObj.options.type = self.config.chartType
              ? self.config.chartType
              : self.chartType
              ? self.chartType.chartType.ct
              : 'timeseries'
            reportObj.options.is_combo = self.config.chartType === 'combo'
            self.report = self.combineReportData(reportObj)
            self.chartType = self.getChartType(self.report)
            if (firstTime) {
              self.loading = false
            } else {
              self.chartUpdating = false
            }
            self.setPointSettings()
          })
        }
      }
    },
    changeRenderAlarm() {
      this.showAlarm = !this.showAlarm
    },
    changeChartType(type) {
      let oldType = this.report.options.type
      this.chartType.chartType = type
      this.report.options.type = type.ct

      this.config.chartType = type.ct
      if (
        oldType === 'timeseries' ||
        type.ct === 'timeseries' ||
        this.report.options.is_combo ||
        type.ct === 'combo'
      ) {
        this.init()
      }
    },
    setDateFilter(dateFilter) {
      this.config.dateFilter = dateFilter
      console.log('########### MAGESH: ', dateFilter)
      this.$emit('update:config', this.config)
      this.init()
    },
    onExcludeWeekendChange() {
      this.config.excludeWeekends = this.excludeWeekends
      this.$emit('update:config', this.config)
      this.init()
    },
    handleDrillDown(data) {
      this.jumpReadingToAnalytics(
        this.config.dataPoints[0].parentId,
        this.config.dataPoints[0].readingFieldId,
        { operatorId: 62, value: data.chartData.orgLabel + '' },
        20,
        3
      )
    },
    getChartHeight(type) {
      let self = this
      if (type && type === 'heatMap') {
        if (self.config.dateFilter.filterName === 'Y') {
          return 2000
        } else if (self.config.dateFilter.filterName === 'M') {
          return 400
        } else if (self.config.dateFilter.filterName === 'R') {
          return 1000
        } else if (self.config.dateFilter.filterName === 'C') {
          return 600
        } else if (self.config.dateFilter.filterName === 'W') {
          return 400
        } else if (self.config.dateFilter.filterName === 'D') {
          return 200
        } else {
          return 1000
        }
      } else {
        return 450
      }
    },
    loadConsolidationReadingData(
      dataPoints,
      dateFilter,
      dataList,
      index,
      colorMap,
      consolidateDataPoints
    ) {
      let self = this
      let dataPoint = dataPoints[index]
      return new Promise((resolve, reject) => {
        if (this.config.period !== 0 && dataPoint.yAggr === 0) {
          // setting sum
          dataPoint.yAggr = 3
        } else if (this.config.period === 0 && dataPoint.yAggr !== 0) {
          // setting actual
          dataPoint.yAggr = 0
        }
        let yAggr = dataPoint.yAggr
        let params = {
          parentId: dataPoint.parentId,
          readingFieldId: dataPoint.readingFieldId,
          xAggr: this.config.period,
          yAggr: yAggr,
        }
        if (dataPoint.workflowId > 0) {
          params.derivation = dataPoint
        }
        if (dataPoint.baseLine) {
          params.baseLineId = dataPoint.baseLine.id
        }
        if (dateFilter) {
          params.dateFilter = dateFilter.time
        }
        if (self.excludeWeekends) {
          params.excludeWeekends = self.excludeWeekends
        }
        let url = '/dashboard/getReadingReportData'
        if (dataPoint.type && dataPoint.type === 'cost') {
          url = url + '?cost=true'
        }
        if (consolidateDataPoints) {
          params.consolidateDataPoints = consolidateDataPoints
        }
        self.$http
          .post(url, params)
          .then(function(response) {
            let options = self.prepareReportOptions(
              response.data,
              self.config.dateFilter.time
            )
            let alarms = self.prepareRelatedAlarms(
              response.data.readingAlarms,
              response.data.reportContext,
              options
            )
            let data = []
            let data2 = []
            if (
              response.data &&
              response.data.booleanResultGrouping &&
              response.data.booleanResultGrouping.length
            ) {
              data = self.prepareReportData(
                response.data.reportData,
                response.data.reportContext,
                options,
                'y1axis',
                response.data.baseLineComparisionDiff
              )
              options.booleanchart = true
              options.booleanKey = self.prepareBooleanKey(
                response.data.booleanResultOptions,
                response.data.reportContext,
                options,
                'y1axis',
                response.data.baseLineComparisionDiff
              )
              options.booleanKey1 = response.data.booleanResultOptions
              data2 = self.prepareBooleanReportData(
                response.data.booleanResultGrouping,
                response.data.reportContext,
                options,
                'y1axis',
                response.data.baseLineComparisionDiff
              )
            } else {
              data = self.prepareReportData(
                response.data.reportData,
                response.data.reportContext,
                options,
                'y1axis',
                response.data.baseLineComparisionDiff
              )
            }
            options.color = colors.default[index + 1]
            let title = dataPoint.name
            if (dataPoint.baseLine && dataPoint.baseLine.name) {
              title += ' (' + dataPoint.baseLine.name + ')'
            }
            dataList.push({
              title: title,
              type: self.config.baseLine
                ? params.baseLineId
                  ? 'area'
                  : 'line'
                : 'area',
              baseLineId: params.baseLineId,
              options: options,
              data: data,
              alarms: alarms,
              data2: data2,
              dataPointId: dataPoint.id,
            })
            resolve(response)
          })
          .catch(function(error) {
            reject(error)
          })
      })
    },
    loadReadingData(dataPoints, dateFilter, dataList, index, colorMap) {
      let self = this
      let dataPoint = dataPoints[index]
      return new Promise((resolve, reject) => {
        if (this.config.period !== 0 && dataPoint.yAggr === 0) {
          if (
            dataPoint.readingField.unit === 'kWh' ||
            dataPoint.readingField.name === 'totalEnergyConsumptionDelta'
          ) {
            // setting sum
            dataPoint.yAggr = 3
          } else {
            // setting avg
            dataPoint.yAggr = 2
          }
        } else if (this.config.period === 0 && dataPoint.yAggr !== 0) {
          // setting actual
          dataPoint.yAggr = 0
        }
        let yAggr = dataPoint.yAggr
        let params = {
          parentId: dataPoint.parentId,
          readingFieldId: dataPoint.readingFieldId,
          xAggr: this.config.period,
          yAggr: yAggr,
        }
        if (dataPoint.workflowId > 0) {
          params.derivation = dataPoint
        }
        if (dataPoint.baseLine) {
          params.baseLineId = dataPoint.baseLine.id
        }
        if (dateFilter) {
          params.dateFilter = dateFilter.time
        }
        if (self.excludeWeekends) {
          params.excludeWeekends = self.excludeWeekends
        }
        let url = '/dashboard/getReadingReportData'
        if (dataPoint.type && dataPoint.type === 'cost') {
          url = url + '?cost=true'
        }
        if (this.config.chartType === 'heatMap') {
          params.isHeatMap = true
        }
        // if (this.consolidateDataPoints !== null) {
        //   params = null
        //   params.consolidateDataPoints = this.consolidateDataPoints
        // }
        self.$http
          .post(url, params)
          .then(function(response) {
            let options = self.prepareReportOptions(
              response.data,
              self.config.dateFilter.time
            )
            let alarms = self.prepareRelatedAlarms(
              response.data.readingAlarms,
              response.data.reportContext,
              options
            )
            let data = []
            let data2 = []
            if (
              response.data &&
              response.data.booleanResultGrouping &&
              response.data.booleanResultGrouping.length
            ) {
              data = self.prepareReportData(
                response.data.reportData,
                response.data.reportContext,
                options,
                'y1axis',
                response.data.baseLineComparisionDiff
              )
              options.booleanchart = true
              options.booleanKey = self.prepareBooleanKey(
                response.data.booleanResultOptions,
                response.data.reportContext,
                options,
                'y1axis',
                response.data.baseLineComparisionDiff
              )
              options.booleanKey1 = response.data.booleanResultOptions
              data2 = self.prepareBooleanReportData(
                response.data.booleanResultGrouping,
                response.data.reportContext,
                options,
                'y1axis',
                response.data.baseLineComparisionDiff
              )
            } else {
              data = self.prepareReportData(
                response.data.reportData,
                response.data.reportContext,
                options,
                'y1axis',
                response.data.baseLineComparisionDiff
              )
            }
            // if (params.baseLineId) {
            //   options.baseLine = true
            // }
            // if (!colorMap[response.data.reportContext.id]) {
            //   colorMap[response.data.reportContext.id] = colors.default[index + 1]
            // }
            // if (!colorMap[dataPoint.id]) {
            //   colorMap[dataPoint.id] = colors.default[index + 1]
            // }
            // options.color = colorMap[dataPoint.id]
            options.color = colors.default[index + 1]
            let title = dataPoint.name
            if (dataPoint.baseLine && dataPoint.baseLine.name) {
              title += ' (' + dataPoint.baseLine.name + ')'
            }
            dataList.push({
              title: title,
              type: self.config.baseLine
                ? params.baseLineId
                  ? 'area'
                  : 'line'
                : 'area',
              baseLineId: params.baseLineId,
              options: options,
              data: data,
              data2: data2,
              alarms: alarms,
              dataPointId: dataPoint.id,
            })

            if (index + 1 < dataPoints.length) {
              self
                .loadReadingData(
                  dataPoints,
                  dateFilter,
                  dataList,
                  index + 1,
                  colorMap
                )
                .then(function(response) {
                  resolve(response)
                })
            } else {
              resolve(response)
            }
          })
          .catch(function(error) {
            reject(error)
          })
      })
    },
    getFreportOptions(option) {
      option.chartname = this.config.dataPoints[0].name
      return option
    },
    closeSettingsPopover() {
      this.reportSettingsPopover = false
    },
    saveSettingsPopover() {
      if (this.$refs['fchart en-fchart']) {
        this.$refs['fchart en-fchart'].rerender()
      }
      this.reportSettingsPopover = false
      this.setPointSettings()
    },
    setPointSettings() {
      if (this.report && this.report.data) {
        if (this.report.options.is_combo) {
          for (let d of this.report.data) {
            let conf = this.config.dataPoints.find(
              conf => conf.id === d.dataPointId
            )
            conf.settings = {
              reportColor: d.options.color,
              chartType: d.options.type,
            }
          }
        }
      }
    },
  },
}
</script>

<style>
.analytic-summary .fc-list-view-table {
  background: white !important;
}
.analytic-summary .fc-underlyingdata {
  box-shadow: 0 4px 10px 0 rgba(178, 178, 178, 0.18);
}
.analytic-summary .reports-underlyingdata .table-header {
  font-size: 16px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.6px;
  text-align: left;
  color: #000000;
  padding-bottom: 35px;
}
.analytic-summary .fc-list-view-table thead th {
  background: #fff;
  padding: 25px 20px;
  font-size: 12px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 1px;
  text-align: left;
  color: #879eb5;
}
.analytic-summary .fc-list-view-table td {
  padding-left: 20px;
}
.analytic-summary .date-arrow {
  font-weight: bold;
}
.analytic-summary .mB100 {
  margin-bottom: 100px;
}
.analytic-summary .button-row .el-button {
  font-size: 14px !important;
  padding: 3px !important;
  letter-spacing: 0.9px;
  font-weight: normal;
}
.report-options-popover .el-popover__title {
  font-size: 14px;
}
</style>
