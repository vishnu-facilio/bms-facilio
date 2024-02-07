import Vue from 'vue'
import chartModel from 'newcharts/model/chart-model'
import moment from 'moment-timezone'
import deepmerge from 'util/deepmerge'
import colorHelper from 'newcharts/helpers/color-helper'
import NewDateHelper from '@/mixins/NewDateHelper'
import analyticsModels from 'src/pages/report/mixins/FNewAnalyticsModelHelper'
import ChartTypes from 'pages/report/mixins/NewChartTypes'
import { isEmpty } from '@facilio/utils/validation'
import { getFieldOptions } from 'util/picklist'
import helpers from 'src/util/helpers.js'

const MomentRange = require('moment-range')
const contextNames = analyticsModels.contextNames()
const dataTypes = analyticsModels.dataTypes()

export default {
  mixins: [ChartTypes],
  data() {
    return {
      assets: [],
    }
  },
  created() {
    this.loadAssetPickListData()
  },
  methods: {
    noneSelected(reportFilter) {
      const nonEmptyFilters = Object.keys(reportFilter).filter(key => {
        const filter = reportFilter[key]
        if (
          (filter?.length > 0 &&
            filter != null &&
            Object.keys(filter)?.length > 0) ||
          Number(filter) > 0
        ) {
          return key
        }
      })
      if (nonEmptyFilters.length > 0) {
        return false
      } else {
        return true
      }
    },
    prepareUserFilterProps(reportFilter, report, isFromUserFilter) {
      let {
        siteFilter,
        buildingFilter,
        categoryFilter,
        assetFilter,
        ttimeFilter,
      } = reportFilter
      if (this.noneSelected(reportFilter)) {
        return {}
      }
      let { filterState } = report.options.common.filters
      if (filterState && filterState.liveFilterField !== 'none') {
        if (reportFilter?.siteId) {
          siteFilter = [reportFilter.siteId]
        } else if (siteFilter?.length <= 0) {
          siteFilter = []
        }
        if (reportFilter?.buildingId) {
          buildingFilter = [reportFilter.buildingId]
        } else if (buildingFilter?.length <= 0) {
          buildingFilter = []
        }
        if (reportFilter.assetId) {
          assetFilter = [reportFilter.assetId]
        } else if (assetFilter?.length <= 0) {
          assetFilter = []
        }
      }

      return {
        siteFilter: siteFilter.length
          ? siteFilter
          : filterState
          ? isEmpty(isFromUserFilter)
            ? filterState.siteFilter
            : []
          : [],
        buildingFilter: buildingFilter.length
          ? buildingFilter
          : filterState
          ? isEmpty(isFromUserFilter)
            ? filterState.buildingFilter
            : []
          : [],
        categoryFilter: categoryFilter.length
          ? categoryFilter
          : filterState
          ? isEmpty(isFromUserFilter)
            ? filterState.categoryFilter
            : []
          : [],
        assetFilter: assetFilter.length
          ? assetFilter
          : filterState
          ? isEmpty(isFromUserFilter)
            ? filterState.assetFilter
            : []
          : [],
        ttimeFilter,
      }
    },
    async loadAssetPickListData() {
      let { error, options } = await getFieldOptions({
        field: { lookupModuleName: 'asset', skipDeserialize: true },
      })

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.assets = options
      }
    },
    prepareReport(reportObj, params) {
      if (
        reportObj.report.chartState &&
        !['null', null].includes(reportObj.report.chartState) &&
        typeof reportObj.report.chartState === 'string'
      ) {
        reportObj.report.chartState = JSON.parse(reportObj.report.chartState)
        reportObj.report.chartState = this.applyWidgetChartState(
          reportObj.report.chartState
        )
        if (
          reportObj.report.chartState.regressionConfig &&
          !reportObj.regression
        ) {
          reportObj.regression = true
          reportObj.regressionConfig =
            reportObj.report.chartState.regressionConfig
        }
        if (
          reportObj.report.chartState.common &&
          reportObj.report.chartState.common.mode
        ) {
          reportObj.mode = reportObj.report.chartState.common.mode
          reportObj.filters = reportObj.report.chartState.common.filters
          reportObj.sorting = reportObj.report.chartState.common.sorting
        }
      }
      this.isRangeMode(reportObj)
      if (reportObj?.appliedAssetFilter) {
        if (reportObj?.report?.chartState?.dataPoints?.length > 0) {
          for (let dataPointIndex in reportObj.report.chartState.dataPoints) {
            let dataPoint =
              reportObj.report.chartState.dataPoints[dataPointIndex]
            if (
              reportObj?.appliedAssetFilter?.name &&
              reportObj?.appliedAssetFilter?.name !== ''
            ) {
              let label =
                reportObj.appliedAssetFilter.name +
                ' ( ' +
                dataPoint.name +
                ' )'
              if (label !== dataPoint.label) {
                dataPoint.label = label
              }
            }
          }
        }
      }
      let report = {}
      report['options'] = this.prepareReportOptions(reportObj, params)
      report['data'] = this.prepareReportData(reportObj, report.options)
      report['booleanData'] = this.prepareBooleanData(reportObj)
      report['reportType'] = reportObj.report.type
      if (reportObj.report.defaultDurationUnit) {
        report['defaultDurationUnit'] = reportObj.report.defaultDurationUnit
      }
      if (reportObj.Readingdp) {
        report['Readingdp'] = reportObj.Readingdp
      }
      if (reportObj.scatterConfig) {
        report['scatterConfig'] = reportObj.scatterConfig
      }
      if (report.scatterConfig) {
        let xMap = {}
        for (let config of report.scatterConfig) {
          let xAxis = 'x'
          if (config.yAxis) {
            if (config.yAxis.aliases.actual) {
              xAxis = config.yAxis.aliases.actual
            } else {
              let match = report.options.dataPoints.find(
                dp =>
                  parseInt(dp.parentId) === parseInt(config.yAxis.parentId) &&
                  parseInt(dp.fieldId) === parseInt(config.yAxis.yAxis.fieldId)
              )
              if (match) {
                xAxis = match.key
              }
            }
          }
          if (config.xAxis && config.xAxis.length) {
            for (let xObject of config.xAxis) {
              if (xObject.aliases.actual) {
                xMap[xObject.aliases.actual] = xAxis
              } else {
                let match = report.options.dataPoints.find(
                  dp =>
                    parseInt(dp.parentId) === parseInt(xObject.parentId) &&
                    parseInt(dp.fieldId) === parseInt(xObject.yAxis.fieldId)
                )
                if (match) {
                  xMap[match.key] = xAxis
                }
              }
            }
          }
        }
        report['xMap'] = xMap
        reportObj['xMap'] = xMap
      }
      if (
        reportObj.report.reportState &&
        reportObj.report.reportState.groupByTimeAggr > 0
      ) {
        report.options['isGroupedByTime'] = true
      } else {
        report.options['isGroupedByTime'] = false
      }

      if (
        reportObj.report.type === 1 ||
        reportObj.report.type === 3 ||
        report.options.axis.x.datatype === 'date_time' ||
        report.options.axis.x.datatype === 'date' ||
        (report.Readingdp && reportObj.report.type === 4)
      ) {
        report['dateRange'] = this.prepareDateRange(reportObj)
      }

      this.getXColorMap(report, reportObj.report.chartState)
      this.applyDefaultOptionsForModuleReport(
        report,
        reportObj.report.chartState
      )

      // reading as x axis
      let xDataPoint = !reportObj.scatterConfig
        ? report.options.dataPoints.find(dp => dp.axes === 'x')
        : null
      if (xDataPoint && report.data) {
        let reportDataPoint = reportObj.report.dataPoints.find(
          rdp =>
            rdp.name === xDataPoint.key ||
            (rdp.aliases && rdp.aliases.actual === xDataPoint.key)
        )
        if (reportDataPoint) {
          report.options.axis.x.datatype = reportDataPoint.yAxis.dataTypeEnum.toLowerCase()
          report.data.x = report.data[xDataPoint.key]
          xDataPoint.visible = false
          if (
            reportDataPoint.xDataPoint &&
            reportDataPoint.xDataPoint === true
          ) {
            if (
              report.options.axis.x.label.text === reportDataPoint.xAxis.label
            ) {
              report.options.axis.x.label.text = reportDataPoint.name
            }
          }
          if (
            report.options.axis.x.label.text === reportDataPoint.xAxis.label
          ) {
            report.options.axis.x.label.text = reportDataPoint.yAxis.label
          }
        }
      } else if (!reportObj.scatterConfig) {
        let xDataPoint = reportObj.report.dataPoints.find(
          dp => dp.xDataPoint === true
        )
        if (xDataPoint && report.data) {
          report.options.axis.x.datatype = xDataPoint.yAxis.dataTypeEnum.toLowerCase()
          // report.data.x = report.data[xDataPoint.aliases.actual]
          if (
            report.options.axis.x.label.text === xDataPoint.xAxis.label &&
            !reportObj.report.chartState
          ) {
            report.options.axis.x.label.text = xDataPoint.yAxis.label
          }
        }
      }
      if (report.booleanData != null) {
        let filterDPS = []
        report.options.dataPoints.forEach(dp => {
          if (dp.pointType === 5) {
            filterDPS.push(dp.key)
          }
        })

        let filterDp
        if (report.booleanData.hasOwnProperty('Filter')) {
          filterDp = report.booleanData['Filter']
        } else if (report.booleanData.hasOwnProperty('TimeFilter')) {
          filterDp = report.booleanData['TimeFilter']
        } else if (report.booleanData.hasOwnProperty('Cri_A')) {
          filterDp = report.booleanData['Cri_A']
        }
        if (filterDp != null && !reportObj.scatterConfig) {
          if (report.options && report.options.line) {
            report.options.line.connectNull = false
          }
          let intervals = []
          let filterRegion = filterDp.filter(dp => dp.value == 1)
          filterRegion.forEach(interval => {
            intervals.push(interval.start)
            intervals.push(interval.end)
            // intervals.push(moment(new Date(new Date(interval.start).valueOf()-1))
            // .tz(Vue.prototype.$timezone)
            // .format('MM-DD-YYYY HH:mm'))
            // intervals.push(moment(new Date(new Date(interval.end).valueOf()-1))
            //           .tz(Vue.prototype.$timezone)
            //           .format('MM-DD-YYYY HH:mm'))
          })
          report.data.x = report.data.x.concat(intervals).sort()
          for (let key in report.data) {
            if (!(filterDPS.includes(key) || 'x' === key)) {
              let dp = report.data[key]
              intervals.forEach(interval => {
                let first = report.data.x.indexOf(interval)
                let last = report.data.x.lastIndexOf(interval)
                let isAdded = true
                if (first != -1 && isAdded) {
                  dp.splice(first, 0, null)
                  isAdded = false
                }
                if (last != -1 && isAdded) {
                  dp.splice(last, 0, null)
                }
              })
            }
          }
        }
        if (
          (!report.options.settings.filterBar && filterDp != null) ||
          reportObj.scatterConfig
        ) {
          report.options.dataPoints = report.options.dataPoints.filter(
            dp => dp.pointType != 5
          )
        }
      } else {
        report.options.line.connectNull = true
      }
      report.data = Object.freeze(report.data)
      report.booleanData = Object.freeze(report.booleanData)
      return report
    },
    applyWidgetChartState(chartState) {
      if (
        this.config &&
        this.config.widget &&
        this.config.widget.dataOptions &&
        this.config.widget.dataOptions.hasOwnProperty('chartType') &&
        this.config.widget.dataOptions.hasOwnProperty('chartTypeInt') &&
        this.config.widget.dataOptions.chartTypeInt
      ) {
        chartState.type = this.getFullChartList().find(
          rt => rt.chartTypeInt === this.config.widget.dataOptions.chartTypeInt
        ).value
      }
      return chartState
    },
    prepareDateRange(reportObj) {
      let period = this.getTimePeriod(reportObj)
      return {
        operatorId: reportObj.dateRange.operatorId,
        operationOn: reportObj.dateRange
          ? reportObj.dateRange.operationOn
          : null,
        value: reportObj.dateRange.offset,
        offset: -reportObj.dateRange.offset,
        time: reportObj.dateRange.value,
        rangeType:
          NewDateHelper.DateType.properties[reportObj.dateRange.operationOnId]
            .code,
        period: period,
        range: this.isSeriesMode(reportObj.mode)
          ? null
          : this.getDateRange(
              reportObj.dateRange.value,
              period,
              reportObj.dateRange.operationOn
            ),
      }
    },
    prepareHighResDateRange(reportObj) {
      let period = this.getTimePeriod(reportObj)
      return {
        operatorId: reportObj.dateRange.operatorId,
        value: reportObj.dateRange.offset,
        offset: -reportObj.dateRange.offset,
        time: reportObj.dateRange.value,
        rangeType:
          NewDateHelper.DateType.properties[reportObj.dateRange.operationOnId]
            .code,
        period: period,
        range: this.isSeriesMode(reportObj.mode)
          ? null
          : this.getDateRange(
              reportObj.dateRange.value,
              period,
              reportObj.dateRange.operationOn
            ),
      }
    },

    prepareReportOptions(reportObj, params) {
      let reportOptions = this.getReportOptions(reportObj)
      if (reportObj.mode) {
        if (!reportOptions.common) {
          reportOptions.common = {}
        }
        reportOptions.common.mode = reportObj.mode
        reportOptions.common.type = reportObj.report.type
        if (reportObj.filters) {
          reportOptions.common.filters = reportObj.filters
          reportOptions.common.sorting = reportObj.sorting
        }
        /* reportOptions.common = {
          mode: reportObj.mode,
          buildingIds: reportObj.buildingIds
        } */
      }
      if (!reportObj.report.chartState && reportObj.scatterConfig) {
        chartModel.options.general.grid.x = true
      }
      let defaultOptions = {}

      let mergedOptions = deepmerge.objectAssignDeep(
        defaultOptions,
        chartModel.options,
        {
          style: chartModel.style,
        },
        reportOptions
      )

      if (reportObj.report.template) {
        mergedOptions.reportTemplate = JSON.parse(reportObj.report.template)
      }
      if (
        reportObj.hasOwnProperty(contextNames.REGRESSION) &&
        reportObj.regression === true
      ) {
        mergedOptions.settings.safelimit = false
        if (!reportObj.report.chartState) {
          mergedOptions.general.point = {
            show: true,
            pattern: [],
          }
        } else {
          mergedOptions.general.point =
            reportObj.report.chartState.general.point
        }
      }

      if (reportObj.Readingdp || reportObj.scatterConfig) {
        mergedOptions.settings.safelimit = false
        if (!reportObj.report.chartState) {
          mergedOptions.general.point = {
            show: true,
            pattern: [],
          }
          mergedOptions.type = 'scatter'
        } else {
          mergedOptions.general.point =
            reportObj.report.chartState.general.point
        }
      }
      if (reportObj.showReportFilter) {
        mergedOptions.settings.filterBar = true
      }

      if (reportObj.report.chartState) {
        if (reportObj.report.chartState.settings) {
          if (params && params.showAlarms != null) {
            mergedOptions.settings.alarm = params.showAlarms
            mergedOptions.settings.safelimit = params.showSafeLimit
          }
          if (reportObj.report.chartState.settings.alarm) {
            mergedOptions.settings.alarm =
              reportObj.report.chartState.settings.alarm
          }
          if (!reportObj.report.chartState.settings.enableAlarm) {
            mergedOptions.settings.alarm = false
          }
          mergedOptions.settings.chart =
            reportObj.report.chartState.settings.chart
        }
        if (reportObj.report.chartState && reportObj.report.chartState.legend) {
          mergedOptions.legend = reportObj.report.chartState.legend
        }
      }

      if (
        reportObj.report.dataPoints.find(
          rd =>
            rd.yAxis.dataTypeEnum.toLowerCase() === 'boolean' ||
            rd.yAxis.dataTypeEnum.toLowerCase() === 'enum'
        )
      ) {
        mergedOptions.settings.chartMode = 'multi'
      }
      if (
        reportObj.report.reportState &&
        reportObj.report.reportState.groupByTimeAggr > 0 &&
        reportObj.report.dataPoints.length > 1
      ) {
        mergedOptions.settings.chartMode = 'multi'
      } else if (
        reportObj.report.reportState &&
        reportObj.report.reportState.groupByTimeAggr > 0
      ) {
        mergedOptions.settings.chartMode = 'single'
      }
      if (
        reportObj.report.chartState &&
        reportObj.report.chartState.trendLine
      ) {
        mergedOptions.trendLine = reportObj.report.chartState.trendLine
      }
      if (reportObj.report.defaultDurationUnit) {
        mergedOptions.defaultDurationUnit = reportObj.report.defaultDurationUnit
      }

      return mergedOptions
    },

    prepareBooleanReport(aggr) {
      let alarmData = {
        x: [],
        alarms: [],
      }
      let alarms = aggr.alarms
      let alarmRegions = []
      if (alarms && alarms.length) {
        if (alarms) {
          for (let index = 0; index < alarms.length; index++) {
            let d = alarms[index]
            if (d.alarm && d.alarm.length) {
              let startTime = this.$options.filters.toDateFormat(
                d.time,
                'MM-DD-YYYY HH:mm'
              )
              alarmData.x.push(startTime, null)
              alarmData.alarms.push(index)
              let now = this.$options.filters.now()
              let end = alarms[index + 1].time
              if (end > now) {
                end = now
              }
              alarmRegions.push({
                start: startTime,
                end: this.$options.filters.toDateFormat(
                  end,
                  'MM-DD-YYYY HH:mm'
                ),
                value: index,
                count: d.alarm.length,
                alarms: d.alarm
                  .filter(alarmId => aggr.alarmInfo[alarmId])
                  .map(alarmId => aggr.alarmInfo[alarmId]),
              })
            }
          }
        }
      }
      return {
        data: alarmData,
        regions: alarmRegions,
      }
    },

    prepareRelatedAlarms(alarmsList) {
      let result = []
      for (let alarm of alarmsList) {
        let alarmObj = this.prepareBooleanReport(alarm)
        alarmObj.barSize = 'medium'
        alarmObj.alarmTitle = alarm.alarmTitle
        result.push(alarmObj)
      }
      return result
    },

    getReportOptions(reportObj) {
      let yAxisOptions = {}
      let y2AxisOptions = {}
      let unitVsAxes = {}
      if (reportObj.report.chartState && !reportObj.regression) {
        yAxisOptions = reportObj.report.chartState.axis.y
        y2AxisOptions = reportObj.report.chartState.axis.y2
        // TODO migrate
        if (!yAxisOptions.label.type) {
          yAxisOptions.label.type = 'custom'
          y2AxisOptions.label.type = 'custom'
        }

        if (y2AxisOptions.unit && y2AxisOptions.show !== false) {
          unitVsAxes[y2AxisOptions.unit] = 'y2'
        }
      } else {
        let yAxis = null
        let y2Axis = null

        if (
          reportObj.hasOwnProperty(contextNames.REGRESSION) &&
          reportObj.regression === true
        ) {
          // regression graphs cannot have multi charts
          let yDataPoint = reportObj.report.dataPoints.find(
            dP =>
              dP.yAxis.fieldId ===
                reportObj.regressionConfig[0].yAxis.readingId &&
              dP.metaData.parentIds[0] ===
                reportObj.regressionConfig[0].yAxis.parentId
          )
          if (yDataPoint) {
            yAxis = yDataPoint.yAxis
            unitVsAxes[yAxis.unitStr] = 'y'
          }
        } else {
          for (let dataPoint of reportObj.report.dataPoints) {
            if (reportObj.Readingdp && dataPoint.xDataPoint === true) {
              reportObj.Readingdp = dataPoint
            } else if (
              (dataPoint.yAxis.dataType !== 4 || !yAxis) &&
              !unitVsAxes[dataPoint.yAxis.unitStr]
            ) {
              if (!yAxis) {
                yAxis = dataPoint.yAxis
                if (yAxis.unitStr || reportObj.report.type !== 2) {
                  unitVsAxes[yAxis.unitStr] = 'y'
                }
              } else if (!y2Axis) {
                y2Axis = dataPoint.yAxis
                if (yAxis.unitStr || reportObj.report.type !== 2) {
                  unitVsAxes[y2Axis.unitStr] = 'y2'
                }
              }
            }
          }
        }

        yAxisOptions = {
          label: {
            text: yAxis.label?.toUpperCase(),
            type: 'auto',
          },
          datatype: yAxis.dataTypeEnum.toLowerCase(),
          unit: yAxis.unitStr,
        }

        if (y2Axis) {
          y2AxisOptions = {
            show: true,
            label: {
              text: y2Axis.label.toUpperCase(),
              type: 'auto',
            },
            datatype: y2Axis.dataTypeEnum.toLowerCase(),
            unit: y2Axis.unitStr,
          }
        }
      }

      let axisOptions = {}
      let xAxis = reportObj.report.dataPoints[0].xAxis
      if (reportObj.report.analyticsType === 7) {
        xAxis = reportObj.report.dataPoints[0].yAxis
      }
      let xDataPoint = null
      if (
        reportObj.report.chartState &&
        reportObj.report.type === 1 &&
        !reportObj.regression &&
        !reportObj.scatterConfig
      ) {
        axisOptions.x = reportObj.report.chartState.axis.x
        axisOptions.rotated = reportObj.report.chartState.axis.rotated
        axisOptions.showy2axis = reportObj.report.chartState.axis.showy2axis
      } else {
        let text
        if (xAxis.fieldName === 'resource' && reportObj.report.xAggrEnum) {
          // for space aggregation -> module analytics
          text = reportObj.report.xAggrEnum
        } else if (
          reportObj.hasOwnProperty(contextNames.REGRESSION) &&
          reportObj.regression === true
        ) {
          xDataPoint = reportObj.report.dataPoints.filter(
            dP =>
              dP.yAxis.fieldId ===
                reportObj.regressionConfig[0].xAxis[0].readingId &&
              dP.metaData.parentIds[0] ===
                reportObj.regressionConfig[0].xAxis[0].parentId
          )
          xDataPoint = xDataPoint[0]
          if (xDataPoint) {
            text = xDataPoint.yAxis.label
            xAxis = xDataPoint.xAxis
          }
        } else if (reportObj.scatterConfig) {
          xDataPoint = reportObj.report.dataPoints.filter(dP => dP.xDataPoint)
          text = ''
          for (let point of xDataPoint) {
            if (point) {
              let labels = text.split(' & ')
              if (
                text != '' &&
                !labels.includes(point.name.match(/\(([^)]+)\)/)[1])
              ) {
                text = text + ' & ' + point.name.match(/\(([^)]+)\)/)[1]
              } else {
                text = point.name.match(/\(([^)]+)\)/)[1]
              }
              xAxis = point.xAxis
            }
          }
        } else if (reportObj.Readingdp) {
          xDataPoint = reportObj.Readingdp
          if (xDataPoint) {
            text = xDataPoint.name
            xAxis = xDataPoint.xAxis
          }
        } else if (
          reportObj.report &&
          reportObj.report.reportState &&
          reportObj.report.reportState.reportResourceAliases &&
          reportObj.report.reportState.reportResourceAliases.X === 'mvproject'
        ) {
          text = 'M&Vs'
        } else {
          if (reportObj.mode === 6) {
            text = 'Site'
          } else if (reportObj.mode === 7) {
            text = 'Building'
          } else if (reportObj.mode === 8) {
            text = 'Asset'
          } else if (reportObj.mode === 9) {
            text = 'Floor'
          } else if (reportObj.mode === 10) {
            text = 'Space'
          } else if (reportObj.mode === 2) {
            text = 'Asset'
          } else {
            text = xAxis.label
          }
        }
        axisOptions.x = {
          label: {
            text: text,
          },
        }
      }

      axisOptions.x.datatype =
        xAxis.fieldName === 'siteId'
          ? 'string'
          : xAxis.dataTypeEnum.toLowerCase()
      if (
        reportObj.report.chartState &&
        ['number', 'decimal'].includes(axisOptions.x.datatype)
      ) {
        axisOptions.x.range = {}
        if (reportObj.report.chartState.axis.x.range.min !== null) {
          axisOptions.x.range.min = parseFloat(
            reportObj.report.chartState.axis.x.range.min
          )
        }
        if (reportObj.report.chartState.axis.x.range.max !== null) {
          axisOptions.x.range.max = parseFloat(
            reportObj.report.chartState.axis.x.range.max
          )
        }
      }
      if (reportObj.regression) {
        axisOptions.x.tick = {
          fit: false,
        }
      }

      if (reportObj.Readingdp || reportObj.scatterConfig) {
        xDataPoint = reportObj.Readingdp
        if (!axisOptions.x.tick) {
          axisOptions.x.tick = {}
        }
        axisOptions.x.tick.fit = false
        switch (xDataPoint.yAxis.dataType) {
          case 2: {
            axisOptions.x.datatype = 'number'
            break
          }
          case 3: {
            axisOptions.x.datatype = 'decimal'
            break
          }
          default: {
            axisOptions.x.datatype = 'string'
            break
          }
        }
        let period = this.getTimePeriod(reportObj)
        axisOptions.x.time = {
          period: period,
          format: this.getDateFormat(period),
        }
        if (reportObj.report.chartState) {
          axisOptions.x.range = {}
          if (reportObj.report.chartState.axis.x.range.min !== null) {
            axisOptions.x.range.min =
              reportObj.report.chartState.axis.x.range.min
          }
          if (reportObj.report.chartState.axis.x.range.max !== null) {
            axisOptions.x.range.max =
              reportObj.report.chartState.axis.x.range.max
          }
        }
      }

      if (
        this.isSeriesMode(reportObj.mode) ||
        (reportObj.hasOwnProperty(contextNames.REGRESSION) &&
          reportObj.regression === true &&
          !reportObj.report.chartState)
      ) {
        if (reportObj.regression) {
          if (!axisOptions.x.tick) {
            axisOptions.x.tick = {}
          }
          axisOptions.x.tick.fit = false

          switch (xDataPoint.yAxis.dataType) {
            case 2: {
              axisOptions.x.datatype = 'number'
              break
            }
            case 3: {
              axisOptions.x.datatype = 'decimal'
              break
            }
            default: {
              axisOptions.x.datatype = 'string'
              break
            }
          }
        } else {
          axisOptions.x.datatype = 'string'
        }
      }

      if (axisOptions.x.datatype.indexOf('date') !== -1) {
        let period = this.getTimePeriod(reportObj)
        axisOptions.x.time = {
          period: period,
          format: this.getDateFormat(period),
        }
      }

      axisOptions.y = yAxisOptions
      axisOptions.y2 = y2AxisOptions

      let dataPoints = this.prepareDataPoints(reportObj, unitVsAxes)
      if (!this.isSeriesMode(reportObj.mode)) {
        this.prepareYAxes(axisOptions, dataPoints, reportObj.report.dataPoints)
      }
      // else if(reportObj.mode === 8){
      //   this.prepareYAxes(axisOptions, dataPoints, reportObj.report.dataPoints)
      // }

      let multichartOptions = {}
      if (!this.isSeriesMode(reportObj.mode)) {
        multichartOptions = this.getMultichartOptions(
          axisOptions,
          dataPoints,
          reportObj.report.dataPoints,
          reportObj.report.chartState,
          reportObj
        )
      }

      let widgetLegend = this.prepareWidgetLegends(reportObj, dataPoints)
      let safeLimit = this.prepareSafelimit(reportObj)
      let points = []
      dataPoints.forEach(dp => {
        if (dp.type === 'group') {
          points.push(...dp.children)
        } else {
          points.push(dp)
        }
      })
      let benchmark =
        reportObj.report.chartState && reportObj.report.chartState.benchmark
          ? reportObj.report.chartState.benchmark
          : {
              show: false,
              label: 'Benchmark',
              variances: {},
            }
      points.forEach(dp => {
        if (!benchmark.variances[dp.alias]) {
          benchmark.variances[dp.alias] = []
        }
      })
      let isRange =
        reportObj.report.chartState && reportObj.report.chartState.combo
          ? reportObj.report.chartState.combo
          : reportObj.report.rangeModeEnabled &&
            reportObj.report.rangeModeEnabled === true
          ? true
          : false
      let regressionConfig = reportObj.regression
        ? reportObj.regressionConfig
        : null

      let defaultScatter =
        reportObj.report.chartState && reportObj.report.chartState.scatter
          ? reportObj.report.chartState.scatter
          : {
              shape: 'circle',
              color: {
                mode: null,
                pallete: 1,
                colors: ['#1d7f01', '#6cb302', '#e9f501', '#fda504', '#fb5905'],
                reading: null,
              },
              size: {
                mode: null,
                reading: null,
              },
            }
      if (defaultScatter.color && defaultScatter.color.mode) {
        let points = dataPoints.filter(
          dp =>
            dp.axes !== 'x' &&
            !dp.isBaseLine &&
            dp.pointCustomization &&
            dp.visible
        )
        if (points.length > 1) {
          defaultScatter.color.mode = null
        }
      }

      if (
        reportObj.report.chartState &&
        (reportObj.report.type === 1 ||
          reportObj.report.type === 3 ||
          reportObj.report.type === 4)
      ) {
        reportObj.report.chartState.combo = isRange
        reportObj.report.chartState.axis = axisOptions
        reportObj.report.chartState.dataPoints = dataPoints
        reportObj.report.chartState.multichart = multichartOptions
        reportObj.report.chartState.widgetLegend = widgetLegend
        reportObj.report.chartState.benchmark = benchmark
        reportObj.report.chartState.safeLimit = safeLimit
        reportObj.report.chartState.scatter = defaultScatter
        reportObj.report.chartState.regressionConfig = regressionConfig

        return reportObj.report.chartState
      }

      let systemGroupPoints = dataPoints.filter(
        element => element.type === 'systemgroup'
      )
      let isSystemGroup = systemGroupPoints.length > 0
      let defaultChartType =
        this.isSeriesMode(reportObj.mode) || reportObj.xAggr > 0
          ? 'bar'
          : 'area'
      defaultChartType =
        isSystemGroup === true
          ? 'stackedbar'
          : reportObj.Readingdp || reportObj.scatterConfig
          ? 'scatter'
          : defaultChartType
      return {
        type: reportObj.report.chartState
          ? reportObj.report.chartState.type
          : defaultChartType,
        axis: axisOptions,
        regressionConfig: regressionConfig,
        combo: isRange,
        multichart: multichartOptions,
        isSystemGroup: isSystemGroup,
        dataPoints: dataPoints,
        widgetLegend,
        benchmark,
        safeLimit,
        scatter: defaultScatter,
      }
    },
    handleAxisChartState(rObj, stateResultObject, newResultObject) {
      if (stateResultObject) {
        if (newResultObject.report && newResultObject.report.chartState) {
          rObj.options.axis.rotated =
            newResultObject.report.chartState.axis.rotated
          rObj.options.axis.showy2axis =
            newResultObject.report.chartState.axis.showy2axis
        }
        if (
          stateResultObject.report.dataPoints[0].xAxis.fieldId ===
          newResultObject.report.dataPoints[0].xAxis.fieldId
        ) {
          rObj.options.axis.x.label =
            newResultObject.report.chartState.axis.x.label
          rObj.options.axis.x.tick =
            newResultObject.report.chartState.axis.x.tick
          rObj.options.axis.x.show =
            newResultObject.report.chartState.axis.x.show
          rObj.options.axis.x.tick.direction =
            newResultObject.report.chartState.axis.x.tick.direction
        }
      } else {
        if (newResultObject.report && newResultObject.report.chartState) {
          rObj.options.axis.rotated =
            newResultObject.report.chartState.axis.rotated
          rObj.options.axis.showy2axis =
            newResultObject.report.chartState.axis.showy2axis
          rObj.options.axis.x.label =
            newResultObject.report.chartState.axis.x.label
          rObj.options.axis.x.tick =
            newResultObject.report.chartState.axis.x.tick
          rObj.options.axis.x.show =
            newResultObject.report.chartState.axis.x.show
          rObj.options.axis.x.tick.direction =
            newResultObject.report.chartState.axis.x.tick.direction
        }
      }
    },
    getXColorMap(report, chartState) {
      if (
        report.data &&
        report.data.x &&
        Object.keys(report.data).length === 2 &&
        report.data.x.length <= 15
      ) {
        let colors = colorHelper.newColorPicker(report.data.x.length)
        let xColorMap = {}
        for (let i = 0; i < report.data.x.length; i++) {
          let xVal = report.data.x[i]
          xColorMap[xVal] =
            chartState && chartState.xColorMap && chartState.xColorMap[xVal]
              ? chartState.xColorMap[xVal]
              : colors[i]
        }
        report.options.xColorMap = xColorMap
      }
    },

    applyDefaultOptionsForModuleReport(report, chartState) {
      if (report.options.common.type === 2) {
        report.options.general.points =
          chartState && chartState.general.points
            ? chartState.general.points
            : {
                show: false,
              }
        report.options.general.dataOrder =
          chartState && chartState.general.dataOrder
            ? chartState.general.dataOrder
            : 'asc'
        report.options.general.hideZeroes =
          chartState && typeof chartState.general.hideZeroes !== 'undefined'
            ? chartState.general.hideZeroes
            : false
        report.options.general.grid =
          chartState && chartState.general.grid
            ? chartState.general.grid
            : {
                x: false,
                y: true,
              }
        report.options.general.labels = chartState
          ? chartState.general.labels
          : report.options.type !== 'stackedbar'
        report.options.donut.labelType =
          chartState && chartState.donut && chartState.donut.labelType
            ? chartState.donut.labelType
            : 'actual'
        report.options.donut.centerText =
          chartState &&
          chartState.donut &&
          chartState.donut.centerText.primaryText
            ? chartState.donut.centerText
            : {
                primaryText: '_sum',
                primaryUnit: null,
                primaryRoundOff: false,
                secondaryText: report.options.dataPoints[0].label,
                secondaryUnit: null,
                secondaryRoundOff: false,
              }
      }
      if (chartState && chartState.general.normalizeStack) {
        report.options.general.normalizeStack =
          chartState.general.normalizeStack
      }

      if (chartState && chartState.bar && chartState.bar.showGroupTotal) {
        report.options.bar.showGroupTotal = chartState.bar.showGroupTotal
      }
      if (chartState && chartState.bar && chartState.bar.groupTotalLabel) {
        report.options.bar.groupTotalLabel = chartState.bar.groupTotalLabel
      }
    },

    getMultichartOptions(
      mainAxisOptions,
      dataPoints,
      reportDataPoints,
      chartState,
      reportObj
    ) {
      let multichartOptions = {}

      for (let dp of dataPoints) {
        let dpList = []
        if (dp.type === 'datapoint') {
          // dp.axes = 'y'
          dpList.push(dp)
        } else if (dp.type === 'systemgroup') {
          dpList.push(dp)
        } else if (dp.type === 'rangeGroup') {
          dpList.push(dp)
        } else if (
          dp.type === dataTypes.GROUP &&
          dp.pointType === dataTypes.REGRESSION
        ) {
          dpList.push(dp)
        } else {
          let yAxis = null
          let y2Axis = null
          let unitVsAxes = {}
          for (let c of dp.children) {
            if (c.unit) {
              if (!unitVsAxes[c.unit]) {
                if (!yAxis) {
                  unitVsAxes[c.unit] = 'y'
                  yAxis = c.unit
                } else if (!y2Axis) {
                  unitVsAxes[c.unit] = 'y2'
                  y2Axis = c.unit
                }
              }

              c.axes = unitVsAxes[c.unit]
            }
          }
          dpList.push(...dp.children)
        }

        let axisOptions = {
          y: {
            label: {
              type: 'auto',
            },
          },
          y2: {
            label: {
              type: 'auto',
            },
          },
        }
        this.prepareYAxes(axisOptions, dpList, reportDataPoints)

        let multichartYTickcount = {
          y: {
            ticks: {
              count: 3,
            },
          },
          y2: {
            ticks: {
              count: 3,
            },
          },
        }

        let newAxisOptions = {}
        deepmerge.objectAssignDeep(
          newAxisOptions,
          chartModel.options.axis,
          multichartYTickcount,
          mainAxisOptions,
          axisOptions
        )

        let mKey = dp.type + '_' + (dp.key ? dp.key : dp.label)
        if (
          chartState &&
          chartState.multichart &&
          chartState.multichart[mKey]
        ) {
          multichartOptions[mKey] = {
            axis: chartState.multichart[mKey].axis,
          }
          if (!multichartOptions[mKey].axis.y.label.type) {
            multichartOptions[mKey].axis.y.label.type = 'custom'
            multichartOptions[mKey].axis.y2.label.type = 'custom'
          }
        } else {
          multichartOptions[mKey] = {
            axis: newAxisOptions,
          }
        }
      }
      return multichartOptions
    },
    countUnits(dataPoints) {
      let initialUnitCount = {}
      for (let index in dataPoints) {
        if (dataPoints[index].type === 'group') {
          for (let childIndex in dataPoints[index].children) {
            if (initialUnitCount.hasOwnProperty(dataPoints[index].unitStr)) {
              initialUnitCount[dataPoints[index].children[childIndex].unitStr] =
                initialUnitCount[
                  dataPoints[index].children[childIndex].unitStr
                ] + 1
            } else {
              initialUnitCount[
                dataPoints[index].children[childIndex].unitStr
              ] = 1
            }
          }
        } else {
          if (initialUnitCount.hasOwnProperty(dataPoints[index].unitStr)) {
            initialUnitCount[dataPoints[index].unitStr] =
              initialUnitCount[dataPoints[index].unitStr] + 1
          } else {
            initialUnitCount[dataPoints[index].unitStr] = 1
          }
        }
      }
      return initialUnitCount
    },
    countMetrics(dataPoints) {
      let initialMetricCount = {}
      for (let index in dataPoints) {
        if (dataPoints[index].type === 'group') {
          for (let childIndex in dataPoints[index].children) {
            if (
              initialMetricCount.hasOwnProperty(dataPoints[index].metricEnum)
            ) {
              initialMetricCount[
                dataPoints[index].children[childIndex].metricEnum
              ] =
                initialMetricCount[
                  dataPoints[index].children[childIndex].metricEnum
                ] + 1
            } else {
              initialMetricCount[
                dataPoints[index].children[childIndex].metricEnum
              ] = 1
            }
          }
        } else {
          if (initialMetricCount.hasOwnProperty(dataPoints[index].metricEnum)) {
            initialMetricCount[dataPoints[index].metricEnum] =
              initialMetricCount[dataPoints[index].metricEnum] + 1
          } else {
            initialMetricCount[dataPoints[index].metricEnum] = 1
          }
        }
      }
      return initialMetricCount
    },
    getLastGroupKey(dataPoints) {
      let largestGroupKey = 0
      let groups = dataPoints.filter(db => {
        return db.type === 'group'
      })
      if (groups.length === 0) {
        return largestGroupKey
      } else {
        for (let index in dataPoints) {
          if (dataPoints[index].type === 'group') {
            if (dataPoints[index].groupId > largestGroupKey) {
              largestGroupKey = dataPoints[index].groupId
            } else {
              continue
            }
          }
        }
      }
      return largestGroupKey
    },
    groupPoints(unitCount, dataPoints) {
      let lastGroupKey = this.getLastGroupKey(dataPoints)
      // grouping
      for (
        let index = Object.keys(dataPoints).length - 1;
        index >= 0;
        index--
      ) {
        if (
          dataPoints[index].type === 'group' &&
          (dataPoints[index].unit === '' || dataPoints[index].unit === null)
        ) {
          continue
        } else {
          if (
            unitCount[dataPoints[index].unitStr] > 1 &&
            dataPoints[index].unitStr !== null
          ) {
            let group = dataPoints.filter(db => {
              return (
                db.type === 'group' &&
                db.unit === dataPoints[index].unitStr &&
                db.unit !== ''
              )
            })
            if (group.length !== 0) {
              // add to already existing group
              let removedDataPoint = dataPoints.splice(index, 1)
              let groupIndex = dataPoints.indexOf(group[0])
              dataPoints[groupIndex].children.push(removedDataPoint[0])
            } else {
              if (dataPoints[index].type !== 'group') {
                // add a new group
                let dataPoint = dataPoints.splice(index, 1)
                // new group temp object
                let temp = {
                  label: dataPoint[0].unitStr,
                  children: [],
                  type: 'group',
                  chartType: '',
                  dataType: dataPoint[0].dataType,
                  groupKey: (lastGroupKey += 1),
                  unit: dataPoint[0].unitStr, // auto group key
                }
                temp.children.push(dataPoint[0])
                dataPoints.push(temp)
              }
            }
          } else if (
            unitCount[dataPoints[index].unitStr] === 1 &&
            dataPoints[index].unitStr !== null
          ) {
            continue
          } else {
            // dataType check if unitStr is null and dataType is boolean
            if (dataPoints[index].dataType.trim().toLowerCase() === 'boolean') {
              // create new group for boolean

              let group = dataPoints.filter(db => {
                return (
                  db.type === 'group' &&
                  db.unit === null &&
                  db.dataType.trim().toLowerCase() === 'boolean'
                )
              })
              if (group.length !== 0) {
                let dataPoint = dataPoints.splice(index, 1)
                let groupIndex = dataPoints.indexOf(group[0])
                dataPoints[groupIndex].children.push(dataPoint[0])
              } else {
                let dataPoint = dataPoints.splice(index, 1)
                let temp = {
                  label: 'Status',
                  children: [],
                  type: 'group',
                  chartType: '',
                  groupKey: (lastGroupKey += 1),
                  unit: null,
                  dataType: dataPoint[0].dataType,
                }
                temp.children.push(dataPoint[0])
                dataPoints.push(temp)
              }
            } else if (
              dataPoints[index].dataType.trim().toLowerCase() === 'enum'
            ) {
              let group = dataPoints.filter(db => {
                return (
                  db.type === 'group' &&
                  db.unit === null &&
                  db.dataType.trim().toLowerCase() === 'enum'
                )
              })
              if (group.length !== 0) {
                let dataPoint = dataPoints.splice(index, 1)
                let groupIndex = dataPoints.indexOf(group[0])
                dataPoints[groupIndex].children.push(dataPoint[0])
              } else {
                let dataPoint = dataPoints.splice(index, 1)
                let temp = {
                  label: 'Status',
                  children: [],
                  type: 'group',
                  chartType: '',
                  groupKey: (lastGroupKey += 1),
                  unit: null,
                  dataType: dataPoint[0].dataType,
                }
                temp.children.push(dataPoint[0])
                dataPoints.push(temp)
              }
            } else {
              continue
            }
          }
        }
      }
      return dataPoints
    },
    groupPointsByMetric(metricCount, dataPoints) {
      let lastGroupKey = this.getLastGroupKey(dataPoints)
      let finalDataPoints = []
      let nonBooleanPoints = []
      let allPoints = []
      dataPoints.forEach(dp => {
        if (dp.type === 'group') {
          allPoints.push(...dp.children)
        } else {
          allPoints.push(dp)
        }
      })
      allPoints.forEach(dp => {
        if (dp.dataTypeId === 4 || dp.dataTypeId === 8) {
          finalDataPoints.push(dp)
        } else {
          nonBooleanPoints.push(dp)
        }
      })
      // grouping
      for (
        let index = Object.keys(nonBooleanPoints).length - 1;
        index >= 0;
        index--
      ) {
        if (nonBooleanPoints[index].type === 'group') {
          finalDataPoints.push(nonBooleanPoints[index])
        } else {
          if (
            metricCount[nonBooleanPoints[index].metricEnum] > 0 &&
            nonBooleanPoints[index].metricEnum !== null
          ) {
            let group = finalDataPoints.filter(db => {
              return (
                db.type === 'group' &&
                db.metricEnum === nonBooleanPoints[index].metricEnum &&
                db.metricEnum !== ''
              )
            })
            if (group.length !== 0) {
              // add to already existing group
              let removedDataPoint = nonBooleanPoints.splice(index, 1)
              let groupIndex = finalDataPoints.indexOf(group[0])
              finalDataPoints[groupIndex].children.push(removedDataPoint[0])
            } else {
              if (nonBooleanPoints[index].type !== 'group') {
                // add a new group
                let dataPoint = nonBooleanPoints.splice(index, 1)
                // new group temp object
                let temp = {
                  label: this.$options.filters.pascalCase(
                    dataPoint[0].metricEnum
                  ),
                  children: [],
                  type: 'group',
                  chartType: '',
                  dataType: dataPoint[0].dataType,
                  groupKey: (lastGroupKey += 1),
                  unit: dataPoint[0].unitStr,
                  metricEnum: dataPoint[0].metricEnum, // auto group key
                }
                temp.children.push(dataPoint[0])
                finalDataPoints.push(temp)
              }
            }
          } else {
            finalDataPoints.push(nonBooleanPoints[index])
          }
        }
      }
      return finalDataPoints
    },
    hasDiffUnit(newDataPoints, finalDataPoints) {
      let newDataPointsUnitKeys = Object.keys(this.countUnits(newDataPoints))
      let finalDataPointsUnitKeys = Object.keys(
        this.countUnits(finalDataPoints)
      )
      for (let newUnit in newDataPointsUnitKeys) {
        if (!finalDataPointsUnitKeys.includes(newDataPointsUnitKeys[newUnit])) {
          return true
        }
      }
      return false
    },
    hasDiffMetrics(newDataPoints, finalDataPoints) {
      let newDataPointsUnitKeys = Object.keys(this.countMetrics(newDataPoints))
      let finalDataPointsUnitKeys = Object.keys(
        this.countMetrics(finalDataPoints)
      )
      for (let newUnit in newDataPointsUnitKeys) {
        if (!finalDataPointsUnitKeys.includes(newDataPointsUnitKeys[newUnit])) {
          return true
        }
      }
      return false
    },
    groupNonBooleanPoints(dataPoints) {
      if (!dataPoints || !dataPoints.length) {
        return dataPoints
      }
      // Assuming no group will be present
      let booleanPoints = []
      let nonBooleanPoints = []
      let allPoints = []
      dataPoints.forEach(dp => {
        if (dp.type === 'group') {
          allPoints.push(...dp.children)
        } else {
          allPoints.push(dp)
        }
      })
      allPoints.forEach(dp => {
        if (dp.dataTypeId === 4 || dp.dataTypeId === 8) {
          booleanPoints.push(dp)
        } else {
          nonBooleanPoints.push(dp)
        }
      })
      let newPoints = []
      if (booleanPoints.length) {
        newPoints.push({
          label: 'Status',
          children: booleanPoints,
          type: 'group',
          chartType: '',
          groupKey: 1,
          unit: null,
          dataType: booleanPoints[0].dataType,
        })
      }
      if (nonBooleanPoints.length) {
        newPoints.push({
          label: 'Timeseries',
          children: nonBooleanPoints,
          type: 'group',
          chartType: '',
          dataType: nonBooleanPoints[0].dataType,
          groupKey: 2,
          unit: nonBooleanPoints[0].unitStr, // auto group key
        })
      }
      return newPoints
    },
    setColorForNonExisting(finalDataPoints, colors) {
      let colorIndex = 0
      for (let dataPoint of finalDataPoints) {
        if (dataPoint.type === 'datapoint') {
          if (!dataPoint.color) {
            dataPoint.color = colors[colorIndex]
            colorIndex++
          }
        } else if (dataPoint.type === 'rangeGroup') {
          if (!dataPoint.color) {
            dataPoint.color = colors[colorIndex]
            colorIndex++
          }
        } else {
          for (let dp of dataPoint.children) {
            if (!dp.color) {
              dp.color = colors[colorIndex]
              colorIndex++
            }
          }
        }
      }
      return finalDataPoints
    },
    getLabelWithCaps(name) {
      let firstChar = name.charAt(0)
      firstChar = firstChar.toUpperCase()
      name = name.toLowerCase()
      name = name.slice(1)
      name = firstChar + name
      return name
    },
    getLabelsForSpecialCases(dataPoint) {
      if (
        dataPoint.name === 'resource' &&
        (dataPoint.xAggr === 21 ||
          dataPoint.xAggr === 22 ||
          dataPoint.xAggr === 23)
      ) {
        return this.getLabelWithCaps(dataPoint.xAggrEnum)
      } else if (dataPoint.yAxis.aggr === 1) {
        return 'Number of ' + dataPoint.yAxis.moduleName + 's'
      } else {
        return dataPoint.name
      }
    },
    mergeDataPointsChnages(resultdataPoints, stateDataPoints) {
      // to check the state dataPoints name changes with result datapoints
      if (
        stateDataPoints &&
        stateDataPoints.length &&
        resultdataPoints &&
        resultdataPoints.length
      ) {
        stateDataPoints.forEach(rt => {
          rt.name = resultdataPoints.find(rl => {
            if (
              rl.aliases &&
              rl.aliases.actual &&
              rl.aliases.actual === rt.alias &&
              (rl.type === 3 || rl.type === 5)
            ) {
              rt.label = rl.name
            }
          })
        })
      }
      return stateDataPoints
    },
    setDataPointAxis(
      reportObj,
      dataPoint,
      statePoint,
      unitVsAxes,
      dataPointNumber
    ) {
      let axis = null
      if (
        reportObj.hasOwnProperty(contextNames.REGRESSION) &&
        reportObj.regression === true
      ) {
        if (!dataPoint.yAxis.field) {
          return 'y'
        }

        for (let config of reportObj.regressionConfig) {
          for (let key in config) {
            if (key === 'xAxis' || key === 'yAxis') {
              if (
                key === 'yAxis' &&
                config[key].readingId === dataPoint.yAxis.fieldId &&
                config[key].parentId === dataPoint.metaData.parentIds[0]
              ) {
                axis = config[key].axis
                return axis
              } else if (
                key === 'xAxis' &&
                config[key][0].readingId === dataPoint.yAxis.fieldId &&
                config[key][0].parentId === dataPoint.metaData.parentIds[0]
              ) {
                axis = config[key][0].axis
                return axis
              }
            }
          }
        }
      } else if (reportObj.Readingdp || reportObj.scatterConfig) {
        if (dataPoint.xDataPoint === true) {
          return 'x'
        } else {
          axis = unitVsAxes[dataPoint.yAxis.unitStr]
            ? unitVsAxes[dataPoint.yAxis.unitStr]
            : reportObj.report.type === 2
            ? dataPointNumber > 0
              ? 'y2'
              : 'y'
            : 'y'
          return axis
        }
      } else {
        if (reportObj.groupByMetrics) {
          return 'y'
        }
        axis = unitVsAxes[dataPoint?.yAxis?.unitStr]
          ? unitVsAxes[dataPoint.yAxis.unitStr]
          : reportObj.report.type === 2
          ? statePoint && statePoint !== null && statePoint.axes
            ? statePoint.axes
            : dataPointNumber > 0
            ? 'y2'
            : 'y'
          : 'y'
        return axis
      }
    },
    prepareDataPoints(reportObj, unitVsAxes) {
      let existingColors = []
      let newDataPoints = []
      let dataPointLabelMap = {}
      let shapeArr = ['circle', 'rectangle', 'triangle', 'diamond']
      let noOfDataPoints = reportObj.report.dataPoints.length
      if (reportObj.report.baseLines) {
        noOfDataPoints *= 1 + reportObj.report.baseLines.length
      }
      // let colors = colorHelper.colorPicker(noOfDataPoints)
      // let colorIndex = 0

      for (let i = 0; i < reportObj.report.dataPoints.length; i++) {
        let dataPoint = reportObj.report.dataPoints[i]
        if (dataPoint.aliases.actual) {
          dataPointLabelMap[dataPoint.aliases.actual] = dataPoint.name
        }
        let statePoint = null
        if (
          reportObj.report.chartState !== null &&
          reportObj.report.chartState
        ) {
          statePoint = reportObj.report.chartState.dataPoints.find(
            sdp => sdp.key === dataPoint.aliases.actual
          )
        }
        if (
          dataPoint.xDataPoint !== true ||
          !reportObj.Readingdp ||
          reportObj.scatterConfig
        ) {
          let dpgroupBy = []
          let dpgroupByLabelValues = {}
          let dpChildren = []
          let point = {
            buildingId: dataPoint.buildingId ? dataPoint.buildingId : -1,
            visible:
              dataPoint.type === 6 && reportObj.scatterConfig ? false : true,
            label:
              reportObj.scatterConfig &&
              dataPoint.name &&
              dataPoint.name.split('(').length
                ? dataPoint.name.split('(')[0]
                : dataPoint.name,
            name:
              dataPoint.name && dataPoint.name.split('(').length > 1
                ? dataPoint.name.split('(')[1].split(')')[0]
                : dataPoint.name,
            key: dataPoint.aliases.actual,
            alias: dataPoint.aliases.actual,
            type: 'datapoint',
            chartType: '',
            // color: colors[colorIndex],
            color: null,
            // axes: unitVsAxes[dataPoint.yAxis.unitStr] ? unitVsAxes[dataPoint.yAxis.unitStr] : reportObj.report.type === 2 ? ((i >0) ? 'y2' : 'y') : 'y',
            axes: this.setDataPointAxis(
              reportObj,
              dataPoint,
              statePoint,
              unitVsAxes,
              i
            ),
            safelimit: false,
            aggr: dataPoint.yAxis.aggr,
            parentId:
              (reportObj.report.type === 1 ||
                reportObj.report.type === 3 ||
                reportObj.report.type === 4) &&
              dataPoint.criteria
                ? dataPoint.criteria.conditions['1'].value
                  ? parseInt(dataPoint.criteria.conditions['1'].value.trim())
                  : null
                : null, // TODO remove
            fieldId: dataPoint.yAxis.fieldId, // TODO remove
            fieldName: dataPoint.yAxis.fieldName, // TODO remove
            metric: dataPoint.yAxis.metricEnum,
            moduleName:
              dataPoint.yAxis && dataPoint.yAxis.module
                ? dataPoint.yAxis.module.name
                : null,
            moduleId:
              dataPoint.yAxis && dataPoint.yAxis.moduleId
                ? dataPoint.yAxis.moduleId
                : null,
            pointType: dataPoint.type,
            unitStr: dataPoint.yAxis.unitStr,
            convertTounit: null,
            metricEnum: dataPoint.yAxis.metricEnum,
            dataType: dataPoint.yAxis.dataTypeEnum, // TODO remove
            dataTypeId: dataPoint.yAxis.dataType, // TODO use this everywhere as dataType
            enumMap: dataPoint.yAxis.enumMap,
            groupBy: [],
            groupByLabelValues: {},
            xDataPoint: dataPoint.xDataPoint,
            duplicateDataPoint: dataPoint.duplicateDataPoint,
            rule_aggr_type: dataPoint?.rule_aggr_type,
            rightInclusive: dataPoint?.rightInclusive,
          }
          if (dataPoint.kpiType === 'DYNAMIC') {
            point['kpiType'] = dataPoint.kpiType
          }
          if (point.dataType === 'ENUM' || point.dataType === 'SYSTEM_ENUM') {
            point.enumColorMap = {}
            for (let idx of Object.keys(point.enumMap)) {
              point.enumColorMap[idx] = colorHelper.newColorPicker(1)[0]
            }
          }
          //scatter customization json added
          if (reportObj.scatterConfig) {
            point['pointCustomization'] = {
              shape:
                reportObj.report.chartState !== null &&
                reportObj.report.chartState &&
                reportObj.report.chartState.scatter.shape
                  ? reportObj.report.chartState.scatter.shape === 'predefined'
                    ? shapeArr[i % 4]
                    : reportObj.report.chartState.scatter.shape
                  : 'circle',
              color: {
                enable: false,
                basedOn: point.key,
              },
              size: {
                enable: false,
                basedOn: point.key,
              },
            }
          }

          if (dataPoint.groupByFields && dataPoint.groupByFields.length === 1) {
            for (let group of dataPoint.groupByFields) {
              point.groupBy.push({
                name: group.fieldName,
                id: group.fieldId,
              })
              let groupMap = reportObj.report.dataPoints[0].groupByFields.filter(
                element => element.fieldId === group.fieldId
              )[0].lookupMap
              point.groupByLabelValues[group.fieldId] = groupMap
              dpgroupBy = point.groupBy
              dpgroupByLabelValues = point.groupByLabelValues
            }
          }
          if (dataPoint.groupByFields && dataPoint.groupByFields.length === 1) {
            let colorMap = {}
            let numberOfGroups = Object.keys(
              this.requiredGroupValues(reportObj)
            ).length
            let colors = colorHelper.newColorPicker(numberOfGroups)
            colorMap = {}

            // Only for first level group by
            if (
              reportObj.report.dataPoints[0].groupByFields[0].dataTypeEnum.toLowerCase() ===
                'lookup' ||
              reportObj.report.dataPoints[0].groupByFields[0].fieldName ===
                'siteId'
            ) {
              let groupId =
                reportObj.report.dataPoints[0].groupByFields[0].fieldId

              let groupMap = this.requiredGroupValues(reportObj)
              let groupKeys = Object.keys(groupMap)
              for (let j = 0; j < groupKeys.length; j++) {
                let key = groupKeys[j]
                let color = colors[j]
                let label = groupMap[key]
                colorMap[label] = color
              }
            } else {
              let groups = this.requiredGroupValues(reportObj)
              for (let j = 0; j < Object.keys(groups).length; j++) {
                let color = colors[j]
                let key = Object.keys(groups)[j]
                colorMap[groups[key]] = color
              }
            }
            point['children'] = []
            let requiredGroups = this.requiredGroupValues(reportObj)
            for (let childGroupId in requiredGroups) {
              let childDataPoint = {
                visible:
                  dataPoint.type === 6 && reportObj.scatterConfig
                    ? false
                    : true,
                label: requiredGroups[childGroupId],
                key: requiredGroups[childGroupId],
                alias: requiredGroups[childGroupId],
                type: 'datapoint',
                chartType: '',
                color: requiredGroups[childGroupId]
                  ? colorMap[requiredGroups[childGroupId]]
                  : null,
                axes: unitVsAxes[dataPoint.yAxis.unitStr]
                  ? unitVsAxes[dataPoint.yAxis.unitStr]
                  : 'y',
                fieldId: parseInt(childGroupId), // TODO remove
                fieldName: requiredGroups[childGroupId], // TODO remove
                pointType: null,
                unitStr: dataPoint.yAxis.unitStr,
                convertTounit: null,
                metricEnum: dataPoint.yAxis.metricEnum,
                dataType: null, // TODO remove
                dataTypeId: null, // TODO use this everywhere as dataType
                enumMap: null,
              }
              point.children.push(childDataPoint)
            }
            dpChildren = point.children
            point.type = 'systemgroup'
            newDataPoints.push(point)
          } else if (
            reportObj.report.rangeModeEnabled &&
            reportObj.report.rangeModeEnabled === true
          ) {
            let aliasSplit = dataPoint.aliases.actual.split('.')
            let rangeAliases = reportObj.report.rangeAliases.map(a => {
              if (a.alias && a.rangeMode === true) {
                return a.alias
              }
              return
            })
            if (rangeAliases.includes(aliasSplit[0])) {
              let dpGroup = newDataPoints.filter(
                dp => dp.alias === aliasSplit[0] && dp.type === 'rangeGroup'
              )
              if (dpGroup.length !== 0) {
                let dPoint = dpGroup[0]
                point.label =
                  point.label + ' ' + dataPoint.yAxis.aggrEnum.toLowerCase()
                dPoint.children.push(point)
              } else {
                let rangeGroup = {
                  visible: true,
                  key: aliasSplit[0],
                  alias: aliasSplit[0],
                  label: dataPoint.name + '(Range)',
                  color: null,
                  children: [],
                  axes: 'y',
                  type: 'rangeGroup',
                  chartType: 'area-line-range',
                }
                point.label =
                  point.label + ' ' + dataPoint.yAxis.aggrEnum.toLowerCase()
                rangeGroup.children.push(point)
                newDataPoints.push(rangeGroup)
              }
            } else {
              newDataPoints.push(point)
            }
          } else {
            newDataPoints.push(point)
          }

          // colorIndex++

          if (
            reportObj.report.baseLines &&
            !this.isDimension(dataPoint, reportObj)
          ) {
            let aliasSplit = dataPoint.aliases.actual.split('.')
            if (!reportObj.report.rangeModeEnabled) {
              for (let bl of reportObj.report.baseLines) {
                if (dataPoint.type === 2) {
                  continue
                }
                let blKey = dataPoint.name + ' - ' + bl.baseLine.name
                let point = {
                  visible:
                    dataPoint.type === 6 && reportObj.scatterConfig
                      ? false
                      : true,
                  label: blKey,
                  key:
                    reportObj.report.type === 2
                      ? `${dataPoint.aliases.actual}-${bl.baseLine.name}`
                      : dataPoint.aliases[bl.baseLine.name],
                  alias:
                    reportObj.report.type === 2
                      ? `${dataPoint.aliases.actual}-${bl.baseLine.name}`
                      : dataPoint.aliases[bl.baseLine.name],
                  dpKey: dataPoint.name,
                  dpAlias: dataPoint.aliases.actual,
                  type: 'datapoint',
                  chartType: '',
                  // color: colors[colorIndex],
                  color: null,
                  axes: unitVsAxes[dataPoint.yAxis.unitStr]
                    ? unitVsAxes[dataPoint.yAxis.unitStr]
                    : 'y',
                  isBaseLine: true,
                  baseLineName: bl.baseLine.name,
                  parentId:
                    (reportObj.report.type === 1 ||
                      reportObj.report.type === 3 ||
                      reportObj.report.type === 4) &&
                    dataPoint.criteria
                      ? dataPoint.criteria.conditions['1'].value
                        ? parseInt(
                            dataPoint.criteria.conditions['1'].value.trim()
                          )
                        : null
                      : null, // TODO remove
                  fieldId: dataPoint.yAxis.fieldId,
                  fieldName: dataPoint.yAxis.fieldName,
                  metric: dataPoint.yAxis.metricEnum,
                  pointType: dataPoint.type,
                  unitStr: dataPoint.yAxis.unitStr,
                  convertTounit: null,
                  metricEnum: dataPoint.yAxis.unitStr,
                  dataType: dataPoint.yAxis.dataTypeEnum,
                  dataTypeId: dataPoint.yAxis.dataType,
                  enumMap: dataPoint.yAxis.enumMap,
                  xDataPoint: dataPoint.xDataPoint,
                  duplicateDataPoint: dataPoint.duplicateDataPoint,
                  rightInclusive: dataPoint?.rightInclusive,
                }
                if (dpgroupBy.length && !isEmpty(dpgroupByLabelValues)) {
                  point['groupBy'] = dpgroupBy
                  point['groupByLabelValues'] = dpgroupByLabelValues
                  point['children'] = dpChildren
                  point.type = 'systemgroup'
                }
                if (
                  point.dataType === 'ENUM' ||
                  point.dataType === 'SYSTEM_ENUM'
                ) {
                  point.enumColorMap = {}
                  for (let idx of Object.keys(point.enumMap)) {
                    point.enumColorMap[idx] = colorHelper.newColorPicker(1)[0]
                  }
                }
                if (point.dataTypeId === 4) {
                  if (point.enumMap[0] === 0) {
                    point.enumMap[0] = 'False'
                  }
                  if (point.enumMap[1] === 1) {
                    point.enumMap[1] = 'True'
                  }
                }
                newDataPoints.push(point)
                // colorIndex++
              }
            } else {
              let isRangePoint = reportObj.report.rangeAliases.filter(
                r => r.alias === aliasSplit[0] && r.rangeMode === true
              )
              if (isRangePoint.length === 0) {
                for (let bl of reportObj.report.baseLines) {
                  if (dataPoint.type === 2) {
                    continue
                  }
                  let blKey = dataPoint.name + ' - ' + bl.baseLine.name
                  let point = {
                    visible: true,
                    label: blKey,
                    key: dataPoint.aliases[bl.baseLine.name],
                    alias: dataPoint.aliases[bl.baseLine.name],
                    dpKey: dataPoint.name,
                    dpAlias: dataPoint.aliases.actual,
                    type: 'datapoint',
                    chartType: '',
                    // color: colors[colorIndex],
                    color: null,
                    axes: unitVsAxes[dataPoint.yAxis.unitStr]
                      ? unitVsAxes[dataPoint.yAxis.unitStr]
                      : 'y',
                    isBaseLine: true,
                    baseLineName: bl.baseLine.name,
                    parentId:
                      (reportObj.report.type === 1 ||
                        reportObj.report.type === 3 ||
                        reportObj.report.type === 4) &&
                      dataPoint.criteria
                        ? dataPoint.criteria.conditions['1'].value
                          ? parseInt(
                              dataPoint.criteria.conditions['1'].value.trim()
                            )
                          : null
                        : null, // TODO remove
                    fieldId: dataPoint.yAxis.fieldId,
                    fieldName: dataPoint.yAxis.fieldName,
                    metric: dataPoint.yAxis.metricEnum,
                    pointType: dataPoint.type,
                    unitStr: dataPoint.yAxis.unitStr,
                    convertTounit: null,
                    metricEnum: dataPoint.yAxis.metricEnum,
                    dataType: dataPoint.yAxis.dataTypeEnum,
                    dataTypeId: dataPoint.yAxis.dataType,
                    enumMap: dataPoint.yAxis.enumMap,
                    xDataPoint: dataPoint.xDataPoint,
                    duplicateDataPoint: dataPoint.duplicateDataPoint,
                  }
                  if (
                    point.dataType === 'ENUM' ||
                    point.dataType === 'SYSTEM_ENUM'
                  ) {
                    point.enumColorMap = {}
                    for (let idx of Object.keys(point.enumMap)) {
                      point.enumColorMap[idx] = colorHelper.newColorPicker(1)[0]
                    }
                  }
                  if (point.dataTypeId === 4) {
                    if (point.enumMap[0] === 0) {
                      point.enumMap[0] = 'False'
                    }
                    if (point.enumMap[1] === 1) {
                      point.enumMap[1] = 'True'
                    }
                  }
                  newDataPoints.push(point)
                }
              }
            }
          }
        }
      }
      if (
        reportObj.hasOwnProperty(contextNames.REGRESSION) &&
        reportObj.regression === true
      ) {
        this.addRegressionDataPoints(reportObj, newDataPoints)
      }

      // handle for regression
      let finalDataPoints = []
      if (reportObj.report.chartState && !reportObj.regression) {
        let newDataPointMap = {}
        for (let newDp of newDataPoints) {
          if (newDp.children && newDp.children.length !== 0) {
            newDataPointMap[newDp.key] = newDp
            for (let child of newDp.children) {
              newDataPointMap[child.key] = child
            }
          } else {
            newDataPointMap[newDp.key] = newDp
          }
        }
        let stateDataPoints = this.mergeDataPointsChnages(
          reportObj.report.dataPoints,
          reportObj.report.chartState.dataPoints
        )
        let stateDataPointMap = {}
        for (let i = 0; i < stateDataPoints.length; i++) {
          let dp = stateDataPoints[i]
          if (
            dp.type === 'datapoint' &&
            newDataPointMap[dp.alias] &&
            newDataPointMap[dp.alias].type === 'datapoint' &&
            newDataPointMap[dp.alias].fieldId === dp.fieldId
          ) {
            if (dp.key !== dp.alias) {
              // For older report
              dp.key = dp.alias
            }
            if (dp.parentId && newDataPointMap[dp.alias].parentId) {
              if (dp.parentId !== newDataPointMap[dp.alias].parentId) {
                continue
              }
            }
            stateDataPointMap[dp.key] = true
            let newDp = newDataPointMap[dp.key]
            if (newDp) {
              newDp.label = dp.label
              // reportObj.report.reportTemplate !== null &&
              // reportObj.report.type === 4 &&
              // newDp.type !== 2 &&
              // dataPointLabelMap[dp.key]
              //   ? dataPointLabelMap[dp.key]
              //   : dp.label
              newDp.visible = dp.visible
              newDp.safelimit = dp.safelimit
              newDp.chartType = dp.chartType
              newDp.color = dp.color
              newDp.convertTounit = dp.convertTounit
              if (dp.colorCriteria) {
                newDp.colorCriteria = dp.colorCriteria
              }
              if (newDp.dataType === 'ENUM' && dp.dataType === 'ENUM') {
                for (let idx of Object.keys(dp.enumColorMap)) {
                  if (newDp.enumColorMap[idx])
                    newDp.enumColorMap[idx] = dp.enumColorMap[idx]
                }
              }
              if (reportObj.report.type !== 2 && !reportObj.Readingdp) {
                newDp.axes = dp.axes
              }
              newDp.groupBy = dp.groupBy
              newDp.groupByLabelValues = dp.groupByLabelValues
              //scatter customization json added for datapoint type
              if (dp.pointCustomization) {
                newDp['pointCustomization'] = {
                  shape: dp.pointCustomization.shape,
                  color: {
                    enable: dp.pointCustomization.color.enable,
                    basedOn: dp.pointCustomization.color.basedOn,
                  },
                  size: {
                    enable: dp.pointCustomization.size.enable,
                    basedOn: dp.pointCustomization.color.basedOn,
                  },
                }
              }
              existingColors.push(dp.color)
              finalDataPoints.push(newDp)
            }
          } else if (
            dp.type === 'systemgroup' &&
            newDataPointMap[dp.key] &&
            newDataPointMap[dp.key].type === 'systemgroup'
          ) {
            if (this.checkForGroupConsistency(newDataPointMap[dp.key], dp)) {
              stateDataPointMap[dp.key] = true
              let children = []
              let stateKeys = dp.children.map(element => element.key)
              let requiredGroups = this.requiredGroupValues(reportObj)
              for (let groupChild of Object.keys(requiredGroups)) {
                let child = requiredGroups[groupChild]
                if (stateKeys.includes(child)) {
                  let newDp = newDataPointMap[child]
                  let childDp = dp.children.filter(
                    element => element.key === child
                  )[0]
                  newDp.color = childDp.color
                  newDp.convertTounit = childDp.convertTounit
                  if (
                    newDp.dataType === 'ENUM' &&
                    childDp.dataType === 'ENUM'
                  ) {
                    for (let idx of Object.keys(childDp.enumColorMap)) {
                      if (newDp.enumColorMap[idx])
                        newDp.enumColorMap[idx] = childDp.enumColorMap[idx]
                    }
                  }
                  children.push(newDp)
                } else {
                  let newDp = newDataPointMap[child]
                  if (newDp) {
                    children.push(newDp)
                  }
                }
              }
              finalDataPoints.push({
                label: dp.label,
                type: 'systemgroup',
                key: dp.key,
                axes: dp.axes,
                chartType: newDataPointMap[dp.key].chartType
                  ? newDataPointMap[dp.key].chartType
                  : null,
                unit: newDataPointMap[dp.key].unitStr,
                unitStr: newDataPointMap[dp.key].unitStr,
                convertTounit: newDataPointMap[dp.key].convertTounit,
                metricEnum: newDataPointMap[dp.key].metricEnum,
                children: children,
                dataType: newDataPointMap[dp.key].dataType,
                dataTypeId: newDataPointMap[dp.key].dataTypeId,
                groupBy: dp.groupBy,
                groupByLabelValues: dp.groupByLabelValues,
              })
            } else {
              stateDataPointMap[dp.key] = true
              let newDp = newDataPointMap[dp.key]
              for (let child of newDp.children) {
                existingColors.push(child.color)
              }
              finalDataPoints.push(newDp)
            }
          } else if (
            dp.type === 'rangeGroup' &&
            reportObj.report.rangeModeEnabled === true
          ) {
            let children = dp.children
            dp.children = []
            stateDataPointMap[dp.alias] = true
            existingColors.push(dp.color)
            for (let rChild of children) {
              stateDataPointMap[rChild.key] = true
              if (newDataPointMap[rChild.alias]) {
                let newDp = newDataPointMap[rChild.alias]
                if (newDp.fieldId === rChild.fieldId) {
                  existingColors.push(rChild.color)
                  newDp.color = rChild.color
                  newDp.convertTounit = rChild.convertTounit
                  if (newDp.dataType === 'ENUM' && rChild.dataType === 'ENUM') {
                    for (let idx of Object.keys(rChild.enumColorMap)) {
                      if (newDp.enumColorMap[idx])
                        newDp.enumColorMap[idx] = rChild.enumColorMap[idx]
                    }
                  }
                }
                dp.children.push(newDp)
              }
            }
            if (
              reportObj.report.rangeAliases &&
              reportObj.report.rangeAliases.length !== 0
            ) {
              // check if range mode has been enabled on a dataPoint level
              if (
                reportObj.report.rangeAliases.filter(
                  rAlias =>
                    rAlias.alias === dp.alias && rAlias.rangeMode === true
                ).length !== 0
              ) {
                finalDataPoints.push(dp)
              }
            }
          } else if (dp.type === 'group') {
            let childList = []
            for (let j = 0; j < dp.children.length; j++) {
              let subDp = dp.children[j]
              if (subDp.key !== subDp.alias) {
                // For older report
                subDp.key = subDp.alias
              }
              stateDataPointMap[subDp.key] = true

              let newDp = newDataPointMap[subDp.key]
              if (newDp) {
                newDp.label = subDp.label
                newDp.visible = subDp.visible
                newDp.safelimit = subDp.safelimit
                newDp.chartType = subDp.chartType
                newDp.color = subDp.color
                newDp.convertTounit = subDp.convertTounit
                if (newDp.dataType === 'ENUM' && subDp.dataType === 'ENUM') {
                  for (let idx of Object.keys(subDp.enumColorMap)) {
                    if (newDp.enumColorMap[idx])
                      newDp.enumColorMap[idx] = subDp.enumColorMap[idx]
                  }
                }
                newDp.axes = subDp.axes
                //scatter customization json added for group type
                if (subDp.pointCustomization) {
                  newDp['pointCustomization'] = {
                    shape: subDp.pointCustomization.shape,
                    color: {
                      enable: subDp.pointCustomization.color.enable,
                      basedOn: subDp.pointCustomization.color.basedOn,
                    },
                    size: {
                      enable: subDp.pointCustomization.size.enable,
                      basedOn: subDp.pointCustomization.color.basedOn,
                    },
                  }
                }
                existingColors.push(subDp.color)
                childList.push(newDp)
              }
            }

            finalDataPoints.push({
              label: dp.label,
              type: 'group',
              key: dp.key,
              chartType: dp.chartType ? dp.chartType : '',
              pointType: dp.pointType ? dp.pointType : '',
              children: childList,
              unit: dp.unit,
              convertTounit: dp.convertTounit,
              dataType: dp.dataType === '' ? '' : dp.dataType,
              groupBy: dp.groupBy,
              groupByLabelValues: dp.groupByLabelValues,
            })
          }
        }
        for (let newDp of newDataPoints) {
          if (!stateDataPointMap[newDp.key]) {
            finalDataPoints.push(newDp)
          }
        }
        if (reportObj.report.chartState.settings && !reportObj.groupByMetrics) {
          if (
            reportObj.report.chartState.settings.chartMode === 'multi' &&
            this.hasDiffUnit(newDataPoints, finalDataPoints) &&
            reportObj.report.chartState.settings.hasOwnProperty('autoGroup') &&
            reportObj.report.chartState.settings.autoGroup === false &&
            !reportObj.hasOwnProperty(contextNames.REGRESSION)
          ) {
            reportObj.report.chartState.settings.autoGroup = true
            finalDataPoints = this.groupPoints(
              this.countUnits(finalDataPoints),
              finalDataPoints
            )
          } else if (
            reportObj.report.chartState.settings.hasOwnProperty('autoGroup') &&
            reportObj.report.chartState.settings.autoGroup === true
          ) {
            finalDataPoints = this.groupPoints(
              this.countUnits(finalDataPoints),
              finalDataPoints
            )
          }
        }
      } else {
        finalDataPoints = newDataPoints
        if (reportObj.groupByMetrics) {
          finalDataPoints = this.groupPointsByMetric(
            this.countMetrics(finalDataPoints),
            finalDataPoints
          )
        }
      }

      if (
        finalDataPoints.filter(
          element =>
            element.type === 'datapoint' || element.type === 'rangeGroup'
        ).length !== 0 ||
        reportObj.groupByMetrics
      ) {
        let colors = colorHelper.newColorPicker(noOfDataPoints, existingColors)
        finalDataPoints = this.setColorForNonExisting(finalDataPoints, colors)
      }

      if (
        reportObj.hasOwnProperty(contextNames.REGRESSION) &&
        reportObj.regression === true
      ) {
        finalDataPoints = this.groupForRegression(reportObj, finalDataPoints)
      }

      if (
        reportObj.report.chartState &&
        reportObj.report.chartState.settings &&
        reportObj.report.chartState.settings.hasOwnProperty('filterBar') &&
        reportObj.report.chartState.settings.filterBar
      ) {
        let filterDataPoints =
          finalDataPoints.filter(element => element.type !== 'datapoint') || []
        for (let i = 0; i < reportObj.report.dataPoints.length; i++) {
          let dataPoint = reportObj.report.dataPoints[i]
          let statePoint = null
          if (reportObj.report.chartState !== null) {
            statePoint = finalDataPoints.find(
              sdp => sdp.key === dataPoint.aliases.actual
            )
          }
          if (statePoint) filterDataPoints.push(statePoint)
        }
        finalDataPoints = filterDataPoints
      }
      if (reportObj.scatterType === 'multi') {
        let lastGroupKey = 0
        let dataPoints = []
        for (let config of reportObj.scatterConfig) {
          let temp = {
            label: lastGroupKey + 1,
            children: [],
            type: 'group',
            chartType: '',
            dataType: 'decimal',
            groupKey: (lastGroupKey += 1),
            unit: '',
          }
          if (config.yAxis) {
            let yAxis = finalDataPoints.find(
              dp => dp.key === config.yAxis.aliases.actual
            )
            if (yAxis) {
              temp.children.push(yAxis)
            }
          }
          if (config.xAxis && config.xAxis.length) {
            for (let xObject of config.xAxis) {
              let xAxis = finalDataPoints.find(
                dp => dp.key === xObject.aliases.actual
              )
              if (xAxis) {
                temp.children.push(xAxis)
              }
            }
          }
          if (temp.children.length) {
            dataPoints.push(temp)
          }
          let prevPoints = finalDataPoints.filter(dp => dp.type === 'group')
          if (prevPoints.length) {
            dataPoints = dataPoints.concat(prevPoints)
          }
        }
        finalDataPoints = dataPoints
      }

      return finalDataPoints
    },
    isDimension(dp, reportObj) {
      if (reportObj.Readingdp || reportObj.scatterConfig) {
        if (dp.xDataPoint === true) {
          return true
        } else {
          return false
        }
      } else {
        return false
      }
    },
    addRegressionDataPoints(reportObj, newDataPoints) {
      for (let rConfig of reportObj.regressionConfig) {
        let xDataPoint = reportObj.report.dataPoints.filter(
          dP =>
            dP.yAxis.fieldId === rConfig.xAxis[0].readingId &&
            dP.metaData.parentIds[0] === rConfig.xAxis[0].parentId
        )
        let yDataPoint = reportObj.report.dataPoints.filter(
          dP =>
            dP.yAxis.fieldId === rConfig.yAxis.readingId &&
            dP.metaData.parentIds[0] === rConfig.yAxis.parentId
        )
        let dataPoint = null
        if (xDataPoint.length !== 0 && yDataPoint.length !== 0) {
          if (
            newDataPoints.filter(
              dp =>
                dp.alias ===
                xDataPoint[0].aliases.actual +
                  '_' +
                  yDataPoint[0].aliases.actual +
                  'regr'
            ).length
          ) {
            let regressionPoint = reportObj.report.dataPoints.filter(
              dp =>
                dp.aliases.actual ===
                xDataPoint[0].aliases.actual +
                  '_' +
                  yDataPoint[0].aliases.actual +
                  'regr'
            )

            dataPoint = newDataPoints.filter(
              dp =>
                dp.alias ===
                xDataPoint[0].aliases.actual +
                  '_' +
                  yDataPoint[0].aliases.actual +
                  'regr'
            )

            dataPoint = dataPoint[0]
            if (regressionPoint.length) {
              dataPoint.label = regressionPoint[0].expressionString
                ? regressionPoint[0].expressionString
                : ''
            }

            dataPoint.xAxis = rConfig['xAxis']
            dataPoint.yAxis = rConfig['yAxis']
            dataPoint['coefficients'] =
              reportObj.reportData.regressionResult[
                xDataPoint[0].aliases.actual +
                  '_' +
                  yDataPoint[0].aliases.actual +
                  'regr'
              ].coefficientMap
            dataPoint['pattern'] = null
            dataPoint['pointType'] = dataTypes.REGRESSION
            dataPoint['regressionTypes'] = contextNames.LINEAR
            dataPoint['chartType'] = 'line'
          } else {
            dataPoint = {
              visible: true,
              label: 'Model line',
              key:
                xDataPoint[0].aliases.actual +
                '_' +
                yDataPoint[0].aliases.actual +
                'regr',
              xAxis: rConfig['xAxis'],
              yAxis: rConfig['yAxis'],
              alias:
                xDataPoint[0].aliases.actual +
                '_' +
                yDataPoint[0].aliases.actual +
                'regr',
              type: 'datapoint',
              chartType: 'line',
              color: null,
              axes: 'y',
              evaluator: null,
              pattern: null,
              pointType: dataTypes.REGRESSION,
              regressionTypes: contextNames.linear,
              groupBy: [],
              groupByLabelValues: {},
            }

            newDataPoints.push(dataPoint)
          }
        }
      }
    },
    populateRegressionStateholder(reportObj, regressionPoints) {
      if (
        reportObj.hasOwnProperty(contextNames.REGRESSION) &&
        reportObj.regression === true
      ) {
        for (let config of reportObj.regressionConfig) {
          let xDataPoint = reportObj.report.dataPoints.filter(
            xDp =>
              xDp.yAxis.fieldId === config['xAxis'][0].readingId &&
              xDp.metaData.parentIds[0] === config['xAxis'][0].parentId
          )
          let yDataPoint = reportObj.report.dataPoints.filter(
            xDp =>
              xDp.yAxis.fieldId === config['yAxis'].readingId &&
              xDp.metaData.parentIds[0] === config['yAxis'].parentId
          )

          if (xDataPoint.length !== 0 && yDataPoint.length !== 0) {
            let key =
              xDataPoint[0].aliases.actual +
              '_' +
              yDataPoint[0].aliases.actual +
              'regr'
            config.groupAlias = key
            if (
              regressionPoints[
                xDataPoint[0].yAxis.fieldId +
                  '_' +
                  xDataPoint[0].metaData.parentIds
              ]
            ) {
              regressionPoints[
                xDataPoint[0].yAxis.fieldId +
                  '_' +
                  xDataPoint[0].metaData.parentIds
              ].push(key)
            } else {
              regressionPoints[
                xDataPoint[0].yAxis.fieldId +
                  '_' +
                  xDataPoint[0].metaData.parentIds
              ] = []
              regressionPoints[
                xDataPoint[0].yAxis.fieldId +
                  '_' +
                  xDataPoint[0].metaData.parentIds
              ].push(key)
            }

            if (
              regressionPoints[
                yDataPoint[0].yAxis.fieldId +
                  '_' +
                  yDataPoint[0].metaData.parentIds
              ]
            ) {
              regressionPoints[
                yDataPoint[0].yAxis.fieldId +
                  '_' +
                  yDataPoint[0].metaData.parentIds
              ].push(key)
            } else {
              regressionPoints[
                yDataPoint[0].yAxis.fieldId +
                  '_' +
                  yDataPoint[0].metaData.parentIds
              ] = []
              regressionPoints[
                yDataPoint[0].yAxis.fieldId +
                  '_' +
                  yDataPoint[0].metaData.parentIds
              ].push(key)
            }
          }
        }
      }
    },
    groupForRegression(reportObj, dataPoints) {
      let finalDataPoints = []
      let regGroup = 1
      let options = {
        dataPoints: dataPoints,
      }
      let dps = JSON.parse(
        JSON.stringify(analyticsModels.getAllDataPoints(options))
      )

      let regressionPoints = {}
      this.populateRegressionStateholder(reportObj, regressionPoints)
      let regressionConfig = reportObj.regressionConfig

      for (let rConfig of regressionConfig) {
        let xDataPoint = dps.filter(
          dP =>
            dP.fieldId === rConfig.xAxis[0].readingId &&
            dP.parentId === rConfig.xAxis[0].parentId
        )

        if (xDataPoint.length !== 0) {
          let index = dps.indexOf(xDataPoint[0])
          xDataPoint = xDataPoint[0]
        }

        for (let dataPoint of dps) {
          if (
            dataPoint.type === dataTypes.DATAPOINT &&
            dataPoint.axes !== 'x'
          ) {
            if (
              dataPoint.pointType === dataTypes.REGRESSION &&
              dataPoint.alias === rConfig.groupAlias
            ) {
              let group = finalDataPoints.filter(
                dP => dP.key === dataPoint.alias && dP.type === dataTypes.GROUP
              )
              if (group.length !== 0) {
                group[0].children.push(dataPoint)
              } else {
                let childList = []
                childList.push(xDataPoint)
                childList.push(dataPoint)
                let groupLabel = 'Regression' + regGroup
                let group = {
                  label: groupLabel,
                  type: 'group',
                  pointType: contextNames.REGRESSION,
                  key: rConfig.groupAlias,
                  chartType: dataPoint.chartType,
                  children: childList,
                  unit: null,
                }
                regGroup++
                finalDataPoints.push(group)
              }
            } else {
              if (
                this.isPartOfRegression(
                  dataPoint,
                  regressionPoints,
                  rConfig.groupAlias
                )
              ) {
                // find the group and add else create a new group
                if (dataPoint.axes === 'y') {
                  dataPoint.chartType = 'scatter'
                }
                let rGroup = finalDataPoints.filter(
                  dp =>
                    dp.type === dataTypes.GROUP &&
                    dp.key ===
                      regressionPoints[
                        dataPoint.fieldId + '_' + dataPoint.parentId
                      ]
                )
                if (rGroup.length !== 0) {
                  rGroup[0].children.push(dataPoint)
                } else {
                  let childList = []
                  childList.push(xDataPoint)
                  childList.push(dataPoint)
                  let groupLabel = 'Regression' + regGroup
                  let group = {
                    label: groupLabel,
                    type: 'group',
                    pointType: contextNames.REGRESSION,
                    key: rConfig.groupAlias,
                    chartType: 'line',
                    children: childList,
                    unit: null,
                  }
                  regGroup++
                  finalDataPoints.push(group)
                }
              }
            }
          }
        }
      }
      return finalDataPoints
    },
    sortBasedOnRSqr(finalDataPoints) {
      let dataPoints = []
      let rvsIndex = {}
      for (let dataPoint of finalDataPoints) {
        let index = finalDataPoints.indexOf(dataPoint)
        if (dataPoint.type === 'group') {
          let regressionPoint = dataPoint.children.filter(
            dp => dp.alias === dataPoint.key
          )
          if (regressionPoint.length) {
            rvsIndex[regressionPoint[0].rSquared] = index
          }
        }
      }
      let orderedX = Object.keys(rvsIndex).sort((a, b) => a - b)
      for (let key of orderedX) {
        let group = finalDataPoints[rvsIndex[key]]
        dataPoints.push(group)
      }
      return dataPoints
    },
    isPartOfRegression(dataPoint, regressionPoints, groupAlias) {
      if (
        Object.keys(regressionPoints).includes(
          dataPoint.fieldId + '_' + dataPoint.parentId
        ) &&
        regressionPoints[dataPoint.fieldId + '_' + dataPoint.parentId].includes(
          groupAlias
        )
      ) {
        return true
      }
      return false
    },
    requiredGroupValues(reportObject) {
      let firstLevelGroupId =
        reportObject.report.dataPoints[0].groupByFields[0].fieldId
      let firstLevelGroupName =
        reportObject.report.dataPoints[0].groupByFields[0].fieldName
      let groupField = reportObject.report.dataPoints[0].groupByFields.filter(
        element => element.fieldId === firstLevelGroupId
      )[0]
      let requiredGroups = {}
      if (
        [
          'lookup',
          'boolean',
          'enum',
          'system_enum',
          'multi_enum',
          'multi_lookup',
        ].includes(
          reportObject.report.dataPoints[0].groupByFields[0].dataTypeEnum.toLowerCase()
        ) ||
        firstLevelGroupName === 'siteId'
      ) {
        let firstLevelGroupMap =
          groupField.dataTypeEnum.toLowerCase() === 'lookup' ||
          groupField.dataTypeEnum.toLowerCase() === 'multi_lookup' ||
          groupField.fieldName === 'siteId'
            ? groupField.lookupMap
            : groupField.enumMap
        for (let record of reportObject.reportData.data) {
          let yValue = record[firstLevelGroupName]
          for (let val of yValue) {
            if (
              (val[firstLevelGroupName] &&
                firstLevelGroupMap[val[firstLevelGroupName]]) ||
              (val[firstLevelGroupName] === 0 &&
                groupField.dataTypeEnum === 'BOOLEAN')
            ) {
              requiredGroups[val[firstLevelGroupName]] =
                firstLevelGroupMap[val[firstLevelGroupName]]
            }
          }
        }
      } else {
        let groups = []
        for (let record of reportObject.reportData.data) {
          let yValue = record[firstLevelGroupName]
          for (let val of yValue) {
            if (
              val[firstLevelGroupName] &&
              val[firstLevelGroupName] !== '' &&
              !groups.includes(val[firstLevelGroupName])
            ) {
              groups.push(val[firstLevelGroupName])
            }
          }
        }
        requiredGroups = {
          ...groups,
        }
      }

      return requiredGroups
    },
    checkForGroupConsistency(oldDp, newDp) {
      if (newDp.groupBy[0].id === oldDp.groupBy[0].id) {
        return true
      }
      return false
    },
    prepareYAxes(axisOptions, dataPoints, reportDataPoints) {
      let axes = ['y', 'y2']
      let allPoints = []
      for (let dp of dataPoints) {
        if (dp.type !== 'group' && dp.type !== 'rangeGroup') {
          if (dp.axes !== 'x' && dp.visible !== false) {
            allPoints.push(dp)
          }
        } else {
          let isRegression = dp.children.filter(
            dataPoint => dataPoint.key === dp.key
          )
          if (isRegression.length !== 0) {
            let yPoint = dp.children.find(
              inDp =>
                inDp.fieldId === isRegression[0].yAxis?.readingId &&
                inDp.parentId === isRegression[0].yAxis.parentId
            )
            if (yPoint) {
              allPoints.push(yPoint)
            }
          } else {
            allPoints.push(
              ...dp.children.filter(
                child => child.axes !== 'x' && child.visible !== false
              )
            )
          }
        }
      }
      axes.forEach(axis => {
        let yAxis = axisOptions[axis]

        let yPoints = allPoints.filter(dp => dp.axes === axis)
        if (!Object.keys(yAxis).length) {
          axisOptions[axis] = {
            label: {
              type: 'auto',
            },
          }
        }
        if (!yPoints.length) {
          yAxis.show = false
          return
        }
        yAxis.show = true

        let rdPoints = []
        let rdLabels = new Set()
        for (let point of yPoints) {
          let dp = reportDataPoints.find(
            rdp =>
              point.dpKey === rdp.name ||
              point.key === rdp.name ||
              point.key === rdp.aliases.actual
          )
          if (dp && dp.yAxis.unitStr) {
            rdPoints.push(dp)
          }
          if (dp && dp.yAxis.label) {
            rdLabels.add(dp.yAxis.label.toUpperCase())
          }
        }
        let units = [...new Set(rdPoints.map(rp => rp.yAxis.unitStr))]
        if (units.length === 1) {
          yAxis.unit = units[0]
          yAxis.datatype = rdPoints[0].yAxis.dataTypeEnum.toLowerCase()
        } else {
          yAxis.unit = null
          yAxis.datatype = null
        }
        if (yAxis.label.type === 'auto') {
          yAxis.label.text = [...rdLabels].join(' & ')
        }
      })
    },
    prepareSafelimit(reportObj) {
      let safeLimits = []
      for (let dp of reportObj.report.dataPoints) {
        let aliasKey = dp.aliases['actual']
        let dpname = dp.name
        let limit = reportObj.reportData.aggr[aliasKey + '.safeLimit']
        if (limit) {
          if (limit.min !== null) {
            safeLimits.push({
              value: limit.min,
              label: 'Min',
              key: 'min',
              datapoint: aliasKey,
              name: dpname,
              show: false,
            })
          }
          if (limit.max !== null) {
            safeLimits.push({
              value: limit.max,
              label: 'Max',
              key: 'max',
              datapoint: aliasKey,
              name: dpname,
              show: false,
            })
          }
        }
        if (
          dp.yAxis.fieldName === 'energybenchmark' &&
          reportObj.reportData.aggr[aliasKey + '.lastValue']
        ) {
          let value = reportObj.reportData.aggr[aliasKey + '.lastValue']
          safeLimits.push({
            value: value,
            label: 'Benchmark',
            key: 'benchmark',
            datapoint: aliasKey,
            name: dpname,
            show: false,
          })
        }
      }
      return safeLimits
    },
    prepareWidgetLegends(reportObj, dataPoints) {
      let stateLegend = reportObj.report.chartState
        ? reportObj.report.chartState.widgetLegend
        : null
      let widgetLegend = {
        show: stateLegend
          ? stateLegend.show
          : reportObj.report.analyticsType === 2 &&
            !this.isSeriesMode(reportObj.mode) &&
            !reportObj.scatterConfig,
        variances: {},
      }
      let sumDefaultUnits = ['AED', 'kWh', 'co2', 'kg']
      let sumDefaultFields = ['cdd', 'hdd', 'wdd', 'cost']
      let sumMetricFields = ['ENERGY', 'LENGTH', 'MASS', 'CURRENCY']
      let points = []
      dataPoints.forEach(dp => {
        if (dp.type === 'group') {
          points.push(...dp.children)
        } else {
          points.push(dp)
        }
      })

      let setEnumLegends = dp => {
        if (dp.dataTypeId === 4) {
          widgetLegend.variances[dp.alias || dp.key] = ['1']
        } else if (dp.dataTypeId === 8 || dp.dataTypeId === 12) {
          widgetLegend.variances[dp.alias || dp.key] = []
          Object.keys(dp.enumMap).forEach(key => {
            widgetLegend.variances[dp.alias || dp.key].push(key + '')
          })
        }
      }

      if (points.length === 1) {
        if ([4, 8].includes(points[0].dataTypeId)) {
          setEnumLegends(points[0])
        } else {
          widgetLegend.variances[points[0].alias || points[0].key] = [
            'min',
            'max',
            'sum',
            'avg',
            'lastValue',
          ]
        }
      } else {
        points.forEach(dp => {
          let showSumAggr =
            sumDefaultUnits.includes(dp.unitStr) ||
            sumDefaultFields.includes(dp.fieldName) ||
            sumMetricFields.includes(dp.metric)
          if ([4, 8].includes(dp.dataTypeId)) {
            setEnumLegends(dp)
          } else {
            widgetLegend.variances[dp.alias || dp.key] =
              dp.pointType !== 2 ? [showSumAggr ? 'sum' : 'avg'] : []
          }
        })
      }

      if (stateLegend) {
        helpers.copy(widgetLegend.variances, stateLegend.variances)
      }
      return widgetLegend
    },

    getTimePeriod(reportObj) {
      let period = null
      if (reportObj.xAggr || reportObj.report.xAggr) {
        let xAggr = reportObj.xAggr ? reportObj.xAggr : reportObj.report.xAggr
        if (xAggr === 20) {
          period = 'hourly'
        } else if (xAggr === 12) {
          period = 'daily'
        } else if (xAggr === 11) {
          period = 'weekly'
        } else if (xAggr === 10) {
          period = 'monthly'
        } else if (xAggr === 8) {
          period = 'yearly'
        } else if (xAggr === 25) {
          period = 'quarterly'
        } else if (xAggr === 0) {
          period = 'high-res'
        } else if (xAggr === 19) {
          period = 'hourofday'
        } else if (xAggr === 17) {
          period = 'dayofweek'
        } else if (xAggr === 18) {
          period = 'dayofmonth'
        } else if (xAggr === 16) {
          period = 'weekofyear'
        } else if (xAggr === 15) {
          period = 'monthofyear'
        }
      }
      return period
    },
    getPeriodFromAggr(aggr) {
      switch (aggr) {
        case 20:
          return 'hourly'
        case 12:
          return 'daily'
        case 11:
          return 'weekly'
        case 10:
          return 'monthly'
        case 8:
          return 'yearly'
        case 25:
          return 'quarterly'
        case 19:
          return 'hourofday'
        case 17:
          return 'dayofweek'
        case 18:
          return 'dayofmonth'
        case 16:
          return 'weekofyear'
        case 15:
          return 'monthofyear'
        case 0:
          return 'high-res'
      }
    },

    isEmptyData(reportObj) {
      for (let key in reportObj.reportData) {
        let dp = reportObj.reportData[key]
        let keys = Object.keys(dp)
        if (keys.length) {
          return false
        }
      }
      return true
    },

    isSeriesMode(mode) {
      if ([2, 6, 7, 8, 9, 10].includes(mode)) {
        return true
      }
      return false
    },
    reverseIterate(timeString, periodObject) {
      return moment(timeString, periodObject.format).valueOf()
    },
    prepareReportDataForSpecialCases(reportObj) {
      // for handling X
      let data = {}
      let xData = []
      let xLabelMap = null
      let columns = []
      let groupIds = []
      let groupLabelValues = {}
      let levelOfGroupBy = 0
      let XAlias = reportObj.report.xAlias ? reportObj.report.xAlias : 'X'
      let isTimeSeries =
        reportObj.report.dataPoints[0].xAxis.dataType === 5 ||
        reportObj.report.dataPoints[0].xAxis.dataType === 6
      let period = this.getTimePeriod(reportObj)
      if (
        (reportObj.report.dataPoints[0].xAxis.field &&
          reportObj.report.dataPoints[0].xAxis.field.dataTypeEnum.toLowerCase() ===
            'lookup') ||
        reportObj.report.dataPoints[0].xAxis.dataTypeEnum.toLowerCase() ===
          'lookup' ||
        reportObj.report.dataPoints[0].xAxis.dataTypeEnum.toLowerCase() ===
          'multi_lookup' ||
        reportObj.report.dataPoints[0].xAxis.fieldName === 'siteId'
        // ) && reportObj.reportData.labelMap) {
      ) {
        xLabelMap = reportObj.report.dataPoints[0].xAxis.lookupMap
      }
      // populating aliases
      for (let dataPoint of reportObj.report.dataPoints) {
        let temp = {}
        temp['name'] = dataPoint.name
        temp['alias'] = dataPoint.aliases.actual
        columns.push(temp)

        if (reportObj.report.baseLines) {
          for (let bl of reportObj.report.baseLines) {
            columns.push({
              alias: dataPoint.aliases[bl.baseLine.name],
              label: dataPoint.name + ' - ' + bl.baseLine.name,
            })
          }
        }
      }

      // initalize groupFields and groupBuckets
      if (
        reportObj.report.dataPoints[0].groupByFields &&
        reportObj.report.dataPoints[0].groupByFields.length !== 0
      ) {
        // group by is unique. so take one datapoint
        for (let group of reportObj.report.dataPoints[0].groupByFields) {
          let temp = {}
          temp['name'] = group.fieldName
          temp['id'] = group.fieldId
          groupIds.push(temp)

          if (group.dataTypeEnum.toLowerCase() === 'lookup') {
            groupLabelValues[group.fieldId] = group.lookupMap
          } else {
            groupLabelValues[group.fieldId] = this.requiredGroupValues(
              reportObj
            )
          }

          levelOfGroupBy = levelOfGroupBy + 1
        }
      }

      // X data population
      if (
        isTimeSeries &&
        reportObj.report.dataPoints[0].groupByFields !== null
      ) {
        let dateRange = this.getDateRange(
          reportObj.dateRange.value,
          period,
          reportObj.dateRange.operationOn
        )
        let requiredGroups = this.requiredGroupValues(reportObj)
        for (let domain of dateRange.domain) {
          let yData = reportObj.reportData.data.filter(
            element =>
              this.formatDate(new Date(element[XAlias]), period) === domain
          )
          if (yData.length === 0) {
            xData.push(domain)
            let groupMap = requiredGroups
            for (let key in groupMap) {
              let label = groupMap[key]
              if (!data[label]) {
                data[label] = []
                data[label].push(0)
              } else {
                data[label].push(0)
              }
            }
          } else {
            xData.push(domain)
            let firstLevelGroupLabel = groupIds[0].name

            let yValues = yData[0][firstLevelGroupLabel]

            for (let group of Object.keys(requiredGroups)) {
              let filter = yValues.filter(
                element =>
                  (typeof element[firstLevelGroupLabel] !== 'string' &&
                    parseInt(element[firstLevelGroupLabel]) ===
                      parseInt(group)) ||
                  element[firstLevelGroupLabel] === requiredGroups[group]
              )
              let groupLabel = requiredGroups[group]
              if (filter.length === 0) {
                for (
                  let columnIdx = 0;
                  columnIdx < columns.length;
                  columnIdx++
                ) {
                  if (!data[groupLabel]) {
                    data[groupLabel] = []
                    data[groupLabel].push(0)
                  } else {
                    data[groupLabel].push(0)
                  }
                }
              } else {
                for (let column of columns) {
                  let val = filter[0][column.alias]
                  if (!data[groupLabel]) {
                    data[groupLabel] = []
                    data[groupLabel].push(val)
                  } else {
                    data[groupLabel].push(val)
                  }
                }
              }
            }
          }
        }
        data.x = xData
      } else {
        for (let dataEntry of reportObj.reportData.data) {
          if (
            reportObj.report.dataPoints[0].xAxis.dataTypeEnum.toLowerCase() ===
              'lookup' ||
            reportObj.report.dataPoints[0].xAxis.dataTypeEnum.toLowerCase() ===
              'multi_lookup' ||
            reportObj.report.dataPoints[0].xAxis.fieldName === 'siteId'
          ) {
            let xLabel = xLabelMap[dataEntry[XAlias]]
            if (xLabel) {
              xData.push(xLabel)
            } else {
              continue
            }
          } else if (
            (reportObj.report.dataPoints[0].xAxis.dataTypeEnum.toLowerCase() ===
              'enum' ||
              reportObj.report.dataPoints[0].xAxis.dataTypeEnum.toLowerCase() ===
                'multi_enum' ||
              reportObj.report.dataPoints[0].xAxis.dataTypeEnum.toLowerCase() ===
                'system_enum' ||
              reportObj.report.dataPoints[0].xAxis.dataTypeEnum.toLowerCase() ===
                'boolean') &&
            reportObj.report.dataPoints[0].xAxis.enumMap
          ) {
            let enumMap = reportObj.report.dataPoints[0].xAxis.enumMap
            let xLabel = enumMap[dataEntry[XAlias]]
            if (xLabel) {
              xData.push(xLabel)
            } else {
              continue
            }
          } else {
            if (isTimeSeries) {
              let dateRange = this.getDateRange(
                reportObj.dateRange.value,
                period,
                reportObj.dateRange.operationOn
              )
              if (
                dateRange.domain.includes(
                  this.formatDate(new Date(dataEntry[XAlias]), period)
                )
              ) {
                xData.push(this.formatDate(new Date(dataEntry[XAlias]), period))
              }
            } else {
              xData.push(dataEntry[XAlias])
            }
          }

          if (
            reportObj.report.dataPoints[0].groupByFields &&
            reportObj.report.dataPoints[0].groupByFields.length !== 0
          ) {
            // if group by was applied
            if (groupIds.length === 1) {
              // TODO first level groupBy for Y2 Axis ??
              let firstLevelGroupLabel = groupIds[0].name

              let yValues = dataEntry[firstLevelGroupLabel]
              let requiredGroups = this.requiredGroupValues(reportObj)
              for (let group of Object.keys(requiredGroups)) {
                let filter = yValues.filter(
                  element =>
                    parseInt(element[firstLevelGroupLabel]) ===
                      parseInt(group) ||
                    element[firstLevelGroupLabel] === requiredGroups[group]
                )

                let groupLabel = requiredGroups[group]
                if (filter.length === 0) {
                  for (
                    let columnIdx = 0;
                    columnIdx < columns.length;
                    columnIdx++
                  ) {
                    if (!data[groupLabel]) {
                      data[groupLabel] = []
                      data[groupLabel].push(0)
                    } else {
                      data[groupLabel].push(0)
                    }
                  }
                } else {
                  for (let column of columns) {
                    let val = filter[0][column.alias]
                    if (!data[groupLabel]) {
                      data[groupLabel] = []
                      data[groupLabel].push(val)
                    } else {
                      data[groupLabel].push(val)
                    }
                  }
                }
              }
            } else {
              // more than one group by
            }
          } else {
            // no groupby applied
            for (let column of columns) {
              if (!data[column.alias]) {
                data[column.alias] = []
                data[column.alias].push(
                  dataEntry[column.alias] == undefined
                    ? null
                    : dataEntry[column.alias]
                )
              } else {
                data[column.alias].push(
                  dataEntry[column.alias] ? dataEntry[column.alias] : null
                )
              }
            }
          }
        }
        data.x = xData
      }

      reportObj['groupIds'] = groupIds
      reportObj['groupLabelValues'] = groupLabelValues
      return data
    },
    prepareReportData(reportObj, options) {
      let xDataPoint = !reportObj.scatterConfig
        ? analyticsModels.getAllDataPoints(options).find(dp => dp.axes === 'x')
        : null
      if (!xDataPoint && reportObj.Readingdp && !reportObj.scatterConfig) {
        xDataPoint = reportObj.report.dataPoints.find(
          dp => dp.xDataPoint === true
        )
      }
      let xAlias =
        xDataPoint && xDataPoint.alias
          ? xDataPoint.alias
          : xDataPoint && xDataPoint.aliases && xDataPoint.aliases.actual
          ? xDataPoint.aliases.actual
          : reportObj.report.xAlias
          ? reportObj.report.xAlias
          : 'X'
      let columns = []
      let groupedColumns = []
      let timeValues = []
      if (
        reportObj.report.dataPoints[0].xAxis.fieldName === 'siteId' ||
        (reportObj.report.dataPoints[0].xAxis.field &&
          reportObj.report.dataPoints[0].xAxis.field.dataTypeEnum.toLowerCase() ===
            'lookup') ||
        reportObj.report.dataPoints[0].xAxis.dataTypeEnum.toLowerCase() ===
          'lookup' ||
        reportObj.report.dataPoints[0].xAxis.dataTypeEnum.toLowerCase() ===
          'multi_enum' ||
        reportObj.report.dataPoints[0].xAxis.dataTypeEnum.toLowerCase() ===
          'system_enum' ||
        reportObj.report.dataPoints[0].xAxis.dataTypeEnum.toLowerCase() ===
          'multi_lookup' ||
        (reportObj.report.dataPoints[0].groupByFields &&
          reportObj.report.dataPoints[0].groupByFields.length)
      ) {
        let data = this.prepareReportDataForSpecialCases(reportObj)
        return data
      } else if (reportObj.report.analyticsType === 7) {
        let data = this.prepareDataForSpecialScatter(reportObj)
        return data
      } else {
        if (!reportObj.reportData.data || !reportObj.reportData.data.length) {
          return
        }

        if (
          reportObj.report.rangeModeEnabled &&
          reportObj.report.rangeModeEnabled === true
        ) {
          for (let rangeAlias of reportObj.report.rangeAliases) {
            columns.push({
              alias: rangeAlias.alias,
              label: rangeAlias.dataPointName,
              rangeMode: rangeAlias.rangeMode,
            })
          }
          // TODO: add baseline support
        } else {
          for (let dp of reportObj.report.dataPoints) {
            if (
              (xDataPoint && xDataPoint.alias === dp.aliases.actual) ||
              (xDataPoint &&
                xDataPoint.aliases &&
                xDataPoint.aliases.actual === dp.aliases.actual)
            ) {
              continue
            } else if (reportObj.scatterConfig && dp.type === 5) {
              continue
            }

            columns.push({
              alias: dp.aliases['actual'],
              label: dp.name,
            })

            if (reportObj.report.baseLines) {
              for (let bl of reportObj.report.baseLines) {
                columns.push({
                  alias: dp.aliases[bl.baseLine.name],
                  label: dp.name + ' - ' + bl.baseLine.name,
                })
              }
            }
          }
        }

        let isTimeseries =
          xDataPoint && xDataPoint.dataTypeId
            ? [5, 6].includes(xDataPoint.dataTypeId)
            : xDataPoint && xDataPoint.yAxis && xDataPoint.yAxis.dataType
            ? [5, 6].includes(xDataPoint.yAxis.dataType)
            : reportObj.Readingdp || reportObj.scatterConfig
            ? false
            : [5, 6].includes(reportObj.report.dataPoints[0].xAxis.dataType)
        let period = this.getTimePeriod(reportObj)

        let x = []
        let data = {}
        if (isTimeseries && reportObj.xAggr > 0) {
          let timeMap = {}
          for (let record of reportObj.reportData.data) {
            let formattedString = this.formatDate(
              new Date(record[xAlias]),
              period
            )
            if (
              reportObj.report.reportState &&
              reportObj.report.reportState.groupByTimeAggr > 0
            ) {
              if (!timeMap[formattedString]) {
                timeMap[formattedString] = {}
              }
              let groupMap = {}
              for (let groupedRecord of record.group) {
                let period = this.getPeriodFromAggr(
                  parseInt(reportObj.report.reportState.groupByTimeAggr)
                )
                let formattedTime = this.formatDate(
                  new Date(groupedRecord[xAlias]),
                  period
                )
                if (['hourofday', 'dayofmonth'].includes(period)) {
                  formattedTime = this.convertToTooltipFormat(
                    new Date(groupedRecord[xAlias]),
                    period
                  )
                }
                for (let column of columns) {
                  let val = null
                  val = groupedRecord[column.alias]
                    ? groupedRecord[column.alias]
                    : null
                  timeMap[formattedString][
                    column.alias + '_' + formattedTime
                  ] = val
                  if (
                    !groupedColumns.includes(column.alias + '_' + formattedTime)
                  ) {
                    groupedColumns.push(column.alias + '_' + formattedTime)
                  }
                }
              }
            } else {
              timeMap[formattedString] = {}
              for (let column of columns) {
                let val = null
                if (
                  reportObj.report.rangeModeEnabled &&
                  reportObj.report.rangeModeEnabled === true &&
                  column.rangeMode &&
                  column.rangeMode === true
                ) {
                  let range = ['max', 'avg', 'min']
                  val = []
                  range.forEach(r =>
                    val.push(
                      isNaN(parseInt(record[column.alias + '.' + r]))
                        ? null
                        : parseInt(record[column.alias + '.' + r])
                    )
                  )
                } else {
                  val = !isEmpty(record[column.alias])
                    ? record[column.alias]
                    : null
                }

                timeMap[formattedString][column.alias] = val
              }
            }
          }
          if (groupedColumns.length > 0) {
            if (parseInt(reportObj.report.reportState.groupByTimeAggr) === 12) {
              groupedColumns = groupedColumns
                .map(date => {
                  let parts = date.split('-')
                  return `${parts[2]}-${parts[0]}-${parts[1]}`
                })
                .sort()
                .map(date => {
                  let parts = date.split('-')
                  return `${parts[1]}-${parts[2]}-${parts[0]}`
                })
            } else if (
              parseInt(reportObj.report.reportState.groupByTimeAggr) === 10
            ) {
              groupedColumns = groupedColumns
                .map(date => {
                  let parts = date.split('-')
                  return `${parts[1]}-${parts[0]}`
                })
                .sort()
                .map(date => {
                  let parts = date.split('-')
                  return `${parts[1]}-${parts[0]}`
                })
            } else if (
              parseInt(reportObj.report.reportState.groupByTimeAggr) === 17
            ) {
              let days = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
              let temp = []
              for (let day of days) {
                for (let column of groupedColumns) {
                  if (column.split('_')[1] === day) {
                    temp.push(column)
                  }
                }
              }
              groupedColumns = temp
            } else if (
              parseInt(reportObj.report.reportState.groupByTimeAggr) === 18
            ) {
              let days = [
                '1st',
                '2nd',
                '3rd',
                '4th',
                '5th',
                '6th',
                '7th',
                '8th',
                '9th',
                '10th',
                '11th',
                '12th',
                '13th',
                '14th',
                '15th',
                '16th',
                '17th',
                '18th',
                '19th',
                '20th',
                '21st',
                '22nd',
                '23rd',
                '24th',
                '25th',
                '26th',
                '27th',
                '28th',
                '29th',
                '30th',
                '31st',
              ]
              let temp = []
              for (let day of days) {
                for (let column of groupedColumns) {
                  if (column.split('_')[1] === day) {
                    temp.push(column)
                  }
                }
              }
              groupedColumns = temp
            } else if (
              parseInt(reportObj.report.reportState.groupByTimeAggr) === 19
            ) {
              let days = [
                '12 am',
                '1 am',
                '2 am',
                '3 am',
                '4 am',
                '5 am',
                '6 am',
                '7 am',
                '8 am',
                '9 am',
                '10 am',
                '11 am',
                '12 pm',
                '1 pm',
                '2 pm',
                '3 pm',
                '4 pm',
                '5 pm',
                '6 pm',
                '7 pm',
                '8 pm',
                '9 pm',
                '10 pm',
                '11 pm',
              ]
              let temp = []
              for (let day of days) {
                for (let column of groupedColumns) {
                  if (column.split('_')[1] === day) {
                    temp.push(column)
                  }
                }
              }
              groupedColumns = temp
            }
          }
          let dateRange = this.getDateRange(
            reportObj.dateRange.value,
            period,
            reportObj.dateRange.operationOn
          )
          for (let range of dateRange.domain) {
            x.push(range)
            if (
              reportObj.report.reportState &&
              reportObj.report.reportState.groupByTimeAggr > 0
            ) {
              if (!timeMap.hasOwnProperty(range)) {
                for (let column of groupedColumns) {
                  if (!data[column]) {
                    data[column] = []
                  }
                  data[column].push(null)
                }
              } else {
                for (let column of groupedColumns) {
                  if (!data[column]) {
                    data[column] = []
                  }
                  if (typeof timeMap[range][column] !== 'undefined') {
                    data[column].push(timeMap[range][column])
                  } else {
                    data[column].push(null)
                  }
                }
              }
            } else {
              if (!timeMap.hasOwnProperty(range)) {
                for (let column of columns) {
                  if (!data[column.alias]) {
                    data[column.alias] = []
                  }
                  if (
                    reportObj.report.rangeModeEnabled &&
                    reportObj.report.rangeModeEnabled === true &&
                    column.rangeMode &&
                    column.rangeMode === true
                  ) {
                    let range = ['max', 'avg', 'min']
                    let subData = []
                    range.forEach(r => subData.push(null))
                    data[column.alias].push(subData)
                  } else {
                    data[column.alias].push(null)
                  }
                }
              } else {
                for (let column of columns) {
                  if (!data[column.alias]) {
                    data[column.alias] = []
                    data[column.alias].push(timeMap[range][column.alias])
                  } else {
                    data[column.alias].push(timeMap[range][column.alias])
                  }
                }
              }
            }
          }
        } else {
          for (let row of reportObj.reportData.data) {
            if (isTimeseries) {
              if (row[xAlias] !== null) {
                x.push(this.formatDate(new Date(row[xAlias]), period))
              } else {
                continue
              }
            } else if (
              ['enum', 'boolean', 'system_enum'].includes(
                reportObj.report.dataPoints[0].xAxis.dataTypeEnum.toLowerCase()
              )
            ) {
              let enumMap = reportObj.report.dataPoints[0].xAxis.enumMap
              if (enumMap[row[xAlias]] !== null) {
                x.push(enumMap[row[xAlias]])
              } else {
                continue
              }
            } else {
              if (row[xAlias] !== null) {
                if (reportObj.mode === 2) {
                  let asset = []
                  asset = this.assets
                  x.push(asset[row[xAlias]])
                } else {
                  x.push(row[xAlias])
                }
              } else {
                continue
              }
            }

            if (xDataPoint) {
              if (
                reportObj.report.dataPoints[0].xAxis.dataType === 5 ||
                reportObj.report.dataPoints[0].xAxis.dataType === 6 ||
                reportObj.Readingdp
              ) {
                timeValues.push(row['X'])
              }
            }

            for (let column of columns) {
              // for pushing y data
              if (!data[column.alias]) {
                data[column.alias] = []
              }

              if (
                reportObj.report.rangeModeEnabled &&
                reportObj.report.rangeModeEnabled === true &&
                column.rangeMode &&
                column.rangeMode === true
              ) {
                let subAliases = [
                  column.alias + '.max',
                  column.alias + '.avg',
                  column.alias + '.min',
                ]
                let subData = []
                subAliases.forEach(subAlias => {
                  subData.push(
                    isNaN(parseInt(row[subAlias]))
                      ? null
                      : parseInt(row[subAlias])
                  )
                })
                data[column.alias].push(subData)
              } else {
                data[column.alias].push(
                  !isEmpty(row[column.alias])
                    ? parseFloat(row[column.alias])
                    : null
                )
              }
            }
          }
        }
        if (groupedColumns.length > 0) {
          this.prepareTimeGroupedDataOptions(groupedColumns, reportObj, options)
        }

        if (x.length !== 0) {
          if (xDataPoint && !reportObj.Readingdp && !reportObj.scatterConfig) {
            data[xDataPoint.alias] = x
          } else if (!reportObj.scatterConfig) {
            data.x = x
          } else if (reportObj.scatterConfig) {
            options['timeValues'] = x
          }
        }

        if (reportObj.Readingdp) {
          if (timeValues.length !== 0) {
            options['timeValues'] = timeValues
          }
          if (options.axis.y.range.min === null) {
            options.axis.y.range.min = 0
          }
          if (options.axis.y2.range.min === null) {
            options.axis.y2.range.min = 0
          }
        } else if (
          !reportObj.report.chartState &&
          groupedColumns.length === 0
        ) {
          let ymin = 0
          let y2min = 0
          if (columns.length > 0) {
            for (let column of columns) {
              let minOfColumn = Math.min(...data[column.alias])
              let point = options.dataPoints.find(
                dp => dp.alias === column.alias
              )
              if (point && minOfColumn) {
                if (point.axes === 'y' && minOfColumn < ymin) {
                  ymin = minOfColumn
                } else if (point.axes === 'y2' && minOfColumn < y2min) {
                  y2min = minOfColumn
                }
              }
            }
          }
          options.axis.y.range.min = ymin
          options.axis.y2.range.min = y2min
        }

        if (
          reportObj.hasOwnProperty(contextNames.REGRESSION) &&
          reportObj.regression === true
        ) {
          if (timeValues.length !== 0) {
            options['timeValues'] = timeValues
          }

          for (let conf of options.regressionConfig) {
            if (conf.errorState && conf.errorState !== 2) {
              let yDataPoint = analyticsModels.getDataPoint(
                conf.yAxis.readingId,
                conf.yAxis.parentId,
                options
              )
              let xDataPoint = analyticsModels.getDataPoint(
                conf.xAxis[0].readingId,
                conf.xAxis[0].parentId,
                options
              )

              if (yDataPoint && data[yDataPoint.alias]) {
                let yMin = Math.min(...data[yDataPoint.alias])
                let yMax = Math.max(...data[yDataPoint.alias])
                options.axis.y.range.min = yMin
                options.axis.y.range.max = yMax
                options.axis.y.format.decimals = 2

                if (options.multichart) {
                  let multiChartOptions =
                    options.multichart['group_' + conf.groupAlias]
                  if (multiChartOptions) {
                    multiChartOptions.axis.y.range.min = yMin
                    multiChartOptions.axis.y.range.max = yMax
                    multiChartOptions.axis.y.format.decimals = 2
                  }
                }
              }
              if (data[xDataPoint.alias] && data[yDataPoint.alias]) {
                data = this.rearrangeData(reportObj, options, data)
                this.populatePointsInfo(options, data)
              }
            } else {
              data = {}
            }
            // data = this.computeRegressionValues(reportObj,options,data)
          }
        }
        return data
        // }
      }
    },
    rearrangeData(reportObj, options, data) {
      let finalData = {}
      for (let rConfig of reportObj.regressionConfig) {
        let xDataPoint = rConfig.xAxis[0]
        let yDataPoint = rConfig.yAxis
        finalData['x'] = data[xDataPoint.alias]
        finalData[yDataPoint.alias] = data[yDataPoint.alias]
        finalData[xDataPoint.alias + '_' + yDataPoint.alias + 'regr'] =
          data[xDataPoint.alias + '_' + yDataPoint.alias + 'regr']
      }
      return finalData
    },
    populatePointsInfo(options, data) {
      let dataPoints = analyticsModels.getAllDataPoints(options)
      options.general.point.pattern = []
      for (let key in data) {
        let dp = dataPoints.filter(d => d.alias === key)
        if (dp.length) {
          if (dp[0].pointType === contextNames.REGRESSION) {
            options.general.point.pattern.push('<polygon></polygon>')
          } else {
            options.general.point.pattern.push('circle')
          }
        }
      }
    },
    prepareBooleanData(reportObj) {
      let booleanData = {}
      for (let dp of reportObj.report.dataPoints) {
        if (
          dp.yAxis.dataTypeEnum.toLowerCase() === 'boolean' ||
          dp.yAxis.dataTypeEnum.toLowerCase() === 'enum'
        ) {
          let aliasKey = dp.aliases['actual']

          let aggregatedTimeline =
            reportObj.reportData.aggr[aliasKey + '.timeline']
          if (aggregatedTimeline) {
            let prevVal = null
            let start = null
            let end = null
            let rangeList = []
            aggregatedTimeline.sort((a, b) => a.key - b.key) // Temp
            for (let b of aggregatedTimeline) {
              if (prevVal === null) {
                start = b.key
                prevVal = b.value
                if (aggregatedTimeline.length === 1) {
                  end = this.$options.filters.now()
                }
              } else {
                let now = this.$options.filters.now()
                end =
                  reportObj.dateRange &&
                  reportObj.dateRange.value[1] &&
                  b.key > reportObj.dateRange.value[1]
                    ? reportObj.dateRange.value[1]
                    : b.key > now
                    ? now
                    : b.key
                if (prevVal !== b.value) {
                  rangeList.push({
                    start: moment(new Date(start))
                      .tz(Vue.prototype.$timezone)
                      .format('MM-DD-YYYY HH:mm'),
                    end: moment(new Date(end))
                      .tz(Vue.prototype.$timezone)
                      .format('MM-DD-YYYY HH:mm'),
                    value: prevVal,
                  })

                  prevVal = b.value
                  start = b.key
                }
              }
            }
            rangeList.push({
              start: moment(new Date(start))
                .tz(Vue.prototype.$timezone)
                .format('MM-DD-YYYY HH:mm'),
              end: moment(new Date(end))
                .tz(Vue.prototype.$timezone)
                .format('MM-DD-YYYY HH:mm'),
              value: prevVal,
            })

            booleanData[aliasKey] = rangeList
          }
        }
      }
      if (!Object.keys(booleanData).length) {
        return null
      }
      return booleanData
    },

    isRangeMode(reportObj) {
      let uniqueAlias = new Set()
      let uniqueNames = {}
      let aliasList = []
      let rangeAlias = []
      for (let dataPoint of reportObj.report.dataPoints) {
        let alias = dataPoint.aliases.actual.split('.')
        uniqueNames[alias[0]] = dataPoint.name
        uniqueAlias.add(alias[0])
        aliasList.push(alias[0])
      }

      for (let alias of uniqueAlias.values()) {
        if (aliasList.filter(a => a === alias).length === 3) {
          let temp = {}
          temp['alias'] = alias
          temp['dataPointName'] = uniqueNames[alias]
          temp['rangeMode'] = true
          rangeAlias.push(temp)
        } else {
          let temp = {}
          temp['alias'] = alias
          temp['dataPointName'] = uniqueNames[alias]
          temp['rangeMode'] = false
          rangeAlias.push(temp)
        }
      }
      if (rangeAlias.filter(range => range.rangeMode === true).length !== 0) {
        reportObj.report['rangeModeEnabled'] = true
        reportObj.report['rangeAliases'] = rangeAlias
      } else {
        reportObj.report['rangeModeEnabled'] = false
      }
    },
    getDateRange(dateRange, period, operationOn) {
      const currentTimezone = Vue.prototype.$timezone
      const momentRange = MomentRange.extendMoment(moment)
      let start = moment(dateRange[0]).tz(currentTimezone)
      let end = moment(dateRange[1]).tz(currentTimezone)
      let range = momentRange.range(start, end)

      let dateFormat = this.getDateFormat(period)

      let dateAxis = {}

      dateAxis.min = this.formatDate(new Date(dateRange[0]), period)
      dateAxis.max = this.formatDate(new Date(dateRange[1]), period)
      dateAxis.highResMin = this.formatDate(new Date(dateRange[0]), null)
      dateAxis.highResMax = this.formatDate(new Date(dateRange[1]), null)

      if (period === 'dayofweek') {
        dateAxis.min = 'Mon'
        dateAxis.max = 'Sun'
      } else if (period === 'weekofyear' && operationOn === 'year') {
        dateAxis.min = '01'
        dateAxis.max = '52'
      } else if (period === 'dayofmonth') {
        dateAxis.min = '01'
        dateAxis.max = '31'
      } else if (period === 'hourofday') {
        dateAxis.min = '00'
        dateAxis.max = '23'
      }

      if (dateFormat.interval) {
        let domain = null
        if (period !== null) {
          if (period === 'weekly') {
            if (this?.$org?.country === 'GB') {
              start = start.locale('fr')
              end = end.locale('fr')
              range = momentRange.range(start, end)
            }
            domain = Array.from(range.by(dateFormat.interval)).map(timestamp =>
              timestamp.startOf('week').format(dateFormat.format)
            )
          } else if (period === 'monthly') {
            domain = Array.from(range.by(dateFormat.interval)).map(timestamp =>
              timestamp.startOf('month').format(dateFormat.format)
            )
          } else if (period === 'quarterly') {
            domain = Array.from(range.by(dateFormat.interval)).map(timestamp =>
              timestamp.startOf('quarter').format(dateFormat.format)
            )
          } else if (period === 'dayofweek') {
            domain = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
          } else if (period === 'dayofmonth') {
            domain = [
              '01',
              '02',
              '03',
              '04',
              '05',
              '06',
              '07',
              '08',
              '09',
              '10',
              '11',
              '12',
              '13',
              '14',
              '15',
              '16',
              '17',
              '18',
              '19',
              '20',
              '21',
              '22',
              '23',
              '24',
              '25',
              '26',
              '27',
              '28',
              '29',
              '30',
              '31',
            ]
          } else if (period === 'hourofday') {
            domain = [
              '00',
              '01',
              '02',
              '03',
              '04',
              '05',
              '06',
              '07',
              '08',
              '09',
              '10',
              '11',
              '12',
              '13',
              '14',
              '15',
              '16',
              '17',
              '18',
              '19',
              '20',
              '21',
              '22',
              '23',
            ]
          } else {
            domain = Array.from(range.by(dateFormat.interval)).map(timestamp =>
              timestamp.format(dateFormat.format)
            )
          }
        }
        if (domain && domain.length === 2 && domain[0] === domain[1]) {
          domain = [domain[0]]
        }
        dateAxis.domain = domain
      }
      if (period === 'weekly') {
        const days =
          start.isValid() && end.isValid() ? end.diff(start, 'days') + 1 : null
        let { domain } = dateAxis
        domain = domain && domain.length === 1 ? domain[0] : null
        if (days && days <= 7 && !isEmpty(domain)) {
          dateAxis.min = domain
          dateAxis.max = domain
        }
      }
      return dateAxis
    },

    formatDate(date, period) {
      let dateFormat = this.getDateFormat(period)
      return moment(date)
        .tz(Vue.prototype.$timezone)
        .format(dateFormat.format)
    },
    convertToTooltipFormat(date, period) {
      let dateFormat = this.getDateFormat(period)
      return moment(date)
        .tz(Vue.prototype.$timezone)
        .format(dateFormat.tooltip)
    },

    getDateFormat(period) {
      let formatConfig = null

      if (period === 'hourly') {
        formatConfig = {
          period: 'hour',
          interval: 'hours',
          format: 'MM-DD-YYYY HH',
          d3Format: '%m-%d-%Y %H',
          tooltip: 'dddd, MMM D, YYYY h a',
        }
      } else if (period === 'daily') {
        formatConfig = {
          period: 'day',
          interval: 'days',
          format: 'MM-DD-YYYY',
          d3Format: '%m-%d-%Y',
          tooltip: 'dddd, MMM D, YYYY',
        }
      } else if (period === 'weekly') {
        formatConfig = {
          period: 'week',
          interval: 'weeks',
          format: 'MM-DD-YYYY',
          d3Format: '%m-%d-%Y',
          tooltip: '[W]ww gggg',
        }
      } else if (period === 'monthly') {
        formatConfig = {
          period: 'month',
          interval: 'months',
          format: 'MM-YYYY',
          d3Format: '%m-%Y',
          tooltip: 'MMMM YYYY',
        }
      } else if (period === 'quarterly') {
        formatConfig = {
          period: 'quarter',
          interval: 'quarters',
          format: 'MM-YYYY',
          d3Format: '%m-%Y',
          tooltip: '[Q]Q YYYY',
        }
      } else if (period === 'yearly') {
        formatConfig = {
          period: 'year',
          interval: 'years',
          format: 'YYYY',
          d3Format: '%Y',
          tooltip: 'YYYY',
        }
      } else if (period === 'hourofday') {
        formatConfig = {
          period: 'hourofday',
          interval: 'hours',
          format: 'HH',
          d3Format: '%H',
          tooltip: 'h a',
        }
      } else if (period === 'dayofweek') {
        formatConfig = {
          period: 'dayofweek',
          interval: 'days',
          format: 'ddd',
          d3Format: '%a',
          // format: 'MM-DD-YYYY',
          // d3Format: '%m-%d-%Y',
          tooltip: 'ddd',
        }
      } else if (period === 'dayofmonth') {
        formatConfig = {
          period: 'dayofmonth',
          interval: 'days',
          format: 'DD',
          d3Format: '%d',
          tooltip: 'Do',
        }
      } else if (period === 'weekofyear') {
        formatConfig = {
          period: 'weekofyear',
          interval: 'weeks',
          format: 'WW',
          d3Format: '%V',
          tooltip: '[W]WW',
        }
      } else if (period === 'monthofyear') {
        formatConfig = {
          period: 'monthofyear',
          interval: 'months',
          format: 'MMM',
          d3Format: '%b',
          tooltip: 'MMM',
        }
      }

      if (!formatConfig) {
        formatConfig = {
          period: 'minute',
          interval: 'minutes',
          format: 'MM-DD-YYYY HH:mm',
          d3Format: '%m-%d-%Y %H:%M',
          tooltip: 'LLLL',
        }
      }
      return formatConfig
    },
    getStringWithCaps(string) {
      let firstChar = string.charAt(0)
      let remaining = string.slice(1)
      return firstChar.toUpperCase() + remaining.toLowerCase()
    },
    getLowestYValue(result, dataPoints) {
      for (let dataPoint of dataPoints) {
        if (
          (dataPoint.yAxis.dataType === 2 || dataPoint.yAxis.dataType === 3) &&
          this.isDataEmpty(result) === false
        ) {
          let yAlias = dataPoint.aliases.actual
          let min = 0
          let isGroupBy =
            result.report.dataPoints[0].groupByFields &&
            result.report.dataPoints[0].groupByFields.length
          if (isGroupBy) {
            let firstLevelGroupName =
              result.report.dataPoints[0].groupByFields[0].alias
            min = result.reportData.data[0][firstLevelGroupName][0][yAlias]
          } else {
            min = result.reportData.data[0][yAlias]
          }
          if (isGroupBy) {
            let firstLevelGroupName =
              result.report.dataPoints[0].groupByFields[0].alias
            for (let data of result.reportData.data) {
              let groupData = data[firstLevelGroupName]
              for (let group of groupData) {
                if (Number.isInteger(group[yAlias])) {
                  if (group[yAlias] < min) {
                    min = group[yAlias]
                  }
                }
              }
            }
          } else {
            for (let data of result.reportData.data) {
              if (data[yAlias] < min) {
                min = data[yAlias]
              }
            }
          }
          return min
        }
      }
    },
    specialTimeFormatter(valueInSeconds, unitEnum, convertTo, milliseconds) {
      let conversionEnum = {
        day: 4,
        hour: 3,
        minute: 2,
        second: 1,
      }

      let multiplier = milliseconds ? 1000 : 1
      if (unitEnum === 'HOUR') {
        valueInSeconds = valueInSeconds * 60 * 60 // hour into seconds
      } else if (unitEnum === 'MIN') {
        valueInSeconds = valueInSeconds * 60 // minute into seconds
      }
      if (conversionEnum[convertTo] === 4) {
        return parseFloat(valueInSeconds / (86400 * multiplier + '')).toFixed(9)
      } else if (conversionEnum[convertTo] === 3) {
        return parseFloat(valueInSeconds / (3600 * multiplier + '')).toFixed(9)
      } else if (conversionEnum[convertTo] === 2) {
        return parseFloat(valueInSeconds / (60 * multiplier + '')).toFixed(9)
        // return Math.floor(valueInSeconds / (60 * multiplier))
      } else {
        return Math.floor(valueInSeconds)
      }
    },
    isDataEmpty(result) {
      if (result.reportData.data.length === 0) {
        return true
      }
      return false
    },
    specialHandler(result, chartData) {
      let convertTo = null
      let chartStateObj = null
      let dataPointName_convertUnit = {}
      let report_chart_data = chartData
        ? chartData
        : result?.report?.chartState
        ? result.report.chartState
        : null
      if (report_chart_data) {
        try {
          chartStateObj = JSON.parse(report_chart_data)
        } catch (e) {
          console.log('chart data parsing error ')
        }
        if (chartStateObj?.dataPoints?.length > 0) {
          dataPointName_convertUnit = {}
          chartStateObj.dataPoints.forEach(element => {
            if (element?.convertTounit) {
              if (element?.aliases?.actual) {
                dataPointName_convertUnit[element.aliases.actual] =
                  element.convertTounit
              } else if (element?.alias) {
                dataPointName_convertUnit[element.alias] = element.convertTounit
              }
            }
          })
        }
      }
      let lowestYValue = this.getLowestYValue(result, result.report.dataPoints)
      for (let dataPoint of result.report.dataPoints) {
        let durationFields = [
          'duration',
          'actualWorkDuration',
          'estimatedduration',
          'firstresponsetime',
        ]
        if (
          (dataPoint.yAxis.metricEnum === 'DURATION' ||
            dataPoint.yAxis.unitStr === 'duration' ||
            (dataPoint.yAxis.field &&
              dataPoint.yAxis.field.displayType === 'DURATION') ||
            (dataPoint.yAxis.fieldName &&
              durationFields.indexOf(dataPoint.yAxis.fieldName) >= 0) ||
            (dataPoint.yAxis.field &&
              dataPoint.yAxis.field.metricEnum === 'DURATION')) &&
          this.isDataEmpty(result) === false
        ) {
          let yAlias = dataPoint.aliases.actual
          let isMilli = false
          let unitEnum =
            dataPoint.yAxis.unitEnum || dataPoint.yAxis.field.unitEnum

          if (!unitEnum) {
            if (dataPoint.yAxis.fieldId === -1) {
              unitEnum = 'MILLIS'
            } else {
              unitEnum = 'SEC'
            }
          }

          let unitStr = ''
          if (unitEnum == 'YEAR') {
            convertTo = null
            unitStr = 'years'
          } else if (unitEnum == 'WEEK') {
            convertTo = null
            unitStr = 'weeks'
          } else if (unitEnum == 'DAY') {
            convertTo = null
            unitStr = 'days'
          } else if (unitEnum == 'HOUR') {
            if (lowestYValue > 24) {
              unitStr = 'days'
              convertTo = 'day'
            } else {
              unitStr = 'hours'
              convertTo = null
            }
          } else if (unitEnum == 'MIN') {
            if (lowestYValue > 1440) {
              unitStr = 'days'
              convertTo = 'day'
            } else if (lowestYValue > 60) {
              unitStr = 'hours'
              convertTo = 'hour'
            } else {
              unitStr = 'minutes'
              convertTo = null
            }
          } else if (unitEnum == 'SEC') {
            if (lowestYValue > 86400) {
              unitStr = 'days'
              convertTo = 'day'
            } else if (lowestYValue > 3600) {
              unitStr = 'hours'
              convertTo = 'hour'
            } else {
              unitStr = 'minutes'
              convertTo = 'minute'
            }
          } else if (unitEnum == 'MILLIS') {
            isMilli = true
            if (lowestYValue > 86400 * 1000) {
              unitStr = 'days'
              convertTo = 'day'
            } else if (lowestYValue > 3600 * 1000) {
              unitStr = 'hours'
              convertTo = 'hour'
            } else {
              unitStr = 'minutes'
              convertTo = 'minute'
            }
          } else {
            // default seconds
            if (lowestYValue > 86400) {
              unitStr = 'days'
              convertTo = 'day'
            } else if (lowestYValue > 3600) {
              unitStr = 'hours'
              convertTo = 'hour'
            } else {
              unitStr = 'minutes'
              convertTo = 'minute'
            }
          }

          if (result.convertTo) {
            convertTo = result.convertTo
            unitStr = convertTo + 's'
          }
          if (
            dataPointName_convertUnit &&
            yAlias in dataPointName_convertUnit
          ) {
            let convertUnit = dataPointName_convertUnit[yAlias].toLowerCase()
            unitStr = convertUnit
            convertTo = convertUnit.substring(0, convertUnit.length - 1)
          }
          if (convertTo) {
            let isGroupBy =
              result.report.dataPoints[0].groupByFields &&
              result.report.dataPoints[0].groupByFields.length
            if (isGroupBy) {
              let firstLevelGroupName =
                result.report.dataPoints[0].groupByFields[0].alias
              for (let data of result.reportData.data) {
                let groupData = data[firstLevelGroupName]
                for (let group of groupData) {
                  let formattedValue = this.specialTimeFormatter(
                    group[yAlias],
                    unitEnum,
                    convertTo,
                    isMilli
                  )
                  group[yAlias] = formattedValue
                }
              }
            } else {
              let tempData = []
              let isSecond = false
              for (let data of result.reportData.data) {
                let formattedValue = this.specialTimeFormatter(
                  data[yAlias],
                  unitEnum,
                  convertTo,
                  isMilli
                )
                tempData.push(formattedValue)
              }
              if (convertTo === 'minute') {
                isSecond = tempData.filter(d => d > 0).length === 0
              }
              if (isSecond) {
                convertTo = 'second'
                for (let data of result.reportData.data) {
                  let formattedValue = this.specialTimeFormatter(
                    data[yAlias],
                    unitEnum,
                    convertTo,
                    isMilli
                  )
                  data[yAlias] = formattedValue
                }
              } else {
                for (let data of result.reportData.data) {
                  let index = result.reportData.data.indexOf(data)
                  if (index !== -1) {
                    data[yAlias] = tempData[index]
                  }
                }
              }
            }
          }
          if (!(dataPoint.yAxis.unitEnum || dataPoint.yAxis.field.unitEnum)) {
            result.report.defaultDurationUnit = -1
          }
          dataPoint.yAxis.unitStr = unitStr
        }
        if (dataPoint.yAxis.field && dataPoint.yAxis.field.type === 13) {
          dataPoint.yAxis.unitStr = '%'
        }
      }

      for (let dataPoint of result.report.dataPoints) {
        if (dataPoint.xAxis.fieldName === 'resource') {
          dataPoint.xAxis.label = this.getStringWithCaps(
            result.report.xAggrEnum
          )
        } else if (
          dataPoint.yAxis.fieldName &&
          dataPoint.yAxis.fieldName.toLowerCase() === 'actualworkduration'
        ) {
          dataPoint.yAxis.label = 'Work Duration'
        } else if (
          dataPoint.xAxis.fieldName &&
          dataPoint.xAxis.fieldName.toLowerCase() === 'actualworkstart'
        ) {
          dataPoint.xAxis.label = 'Start Time'
        } else if (dataPoint.xAxis.fieldName.toLowerCase() === 'siteid') {
          dataPoint.xAxis.label = 'Site'
        }
        if (dataPoint.yAxis.fieldName === 'id') {
          if (
            result.moduleTypes &&
            result.moduleTypes.length !== 0 &&
            result.report.moduleType !== -1
          ) {
            dataPoint.yAxis.label = result.moduleTypes.filter(
              type => type.type === result.report.moduleType
            )[0].displayName
          } else {
            if (result.moduleDisplayName) {
              dataPoint.yAxis.label = 'Number of ' + result.moduleDisplayName
            } else if (
              result.report &&
              result.report.chartState &&
              result.report.chartState !== null &&
              result.report.chartState !== 'null'
            ) {
              let stateDataPoint = JSON.parse(
                result.report.chartState
              ).dataPoints.find(dp => dp.key === dataPoint.aliases.actual)
              if (
                typeof stateDataPoint !== 'undefined' &&
                stateDataPoint.label
              ) {
                dataPoint.yAxis.label = stateDataPoint.label
              } else {
                dataPoint.yAxis.label = 'Number of ' + result.module.name + 's'
              }
            } else {
              dataPoint.yAxis.label = 'Number of ' + result.module.name + 's'
            }
          }
        }
        dataPoint.name = dataPoint.yAxis.label
      }
      return result
    },
    prepareTimeGroupedDataOptions(columns, reportObj, options) {
      options['timeGroupOptions'] = {}
      for (let column of columns) {
        options['timeGroupOptions'][column] = {}
        if (
          reportObj.report.chartState &&
          reportObj.report.chartState.timeGroupOptions &&
          reportObj.report.chartState.timeGroupOptions[column] != null
        ) {
          options['timeGroupOptions'][column].color =
            reportObj.report.chartState.timeGroupOptions[column].color
          if (
            typeof reportObj.report.chartState.timeGroupOptions[column]
              .visible !== undefined
          ) {
            options['timeGroupOptions'][column].visible =
              reportObj.report.chartState.timeGroupOptions[column].visible
          } else {
            options['timeGroupOptions'][column].visible = true
          }
        } else {
          options['timeGroupOptions'][
            column
          ].color = colorHelper.newColorPicker(1)[0]
          options['timeGroupOptions'][column].visible = true
        }
        let namesplit = column.split('_')
        options['timeGroupOptions'][column].name = namesplit[1]
      }
    },
    prepareDataForSpecialScatter(reportObj) {
      let data = {}
      let xValues = []
      if (reportObj && reportObj.reportData.data) {
        let xPoint = reportObj.report.dataPoints[0]
        let yPoint = reportObj.report.dataPoints[0]
        if (reportObj.report.dataPoints.length > 1) {
          yPoint = reportObj.report.dataPoints[1]
        }
        let xAlias = xPoint.aliases.actual
        let yAlias = yPoint.aliases.actual
        for (let rkey in reportObj.reportData.data) {
          let record = reportObj.reportData.data[rkey]
          let temp = reportObj.reportData.data.map(d => {
            return null
          })
          temp[rkey] = record[yAlias] ? record[yAlias] : null
          data[record['X']] = temp
          xValues.push(record[xAlias] ? record[xAlias] : null)
        }
        data['x'] = xValues
      }
      return data
    },
  },
}
