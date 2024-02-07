import Vue from 'vue'
import chartModel from 'newcharts/model/chart-model'
import moment from 'moment-timezone'
import deepmerge from 'util/deepmerge'
import colorHelper from 'newcharts/helpers/color-helper'
import NewDateHelper from '@/mixins/NewDateHelper'
// import defaults from 'src/components/mixins/NumberFormatHelper'

const MomentRange = require('moment-range')

export default {
  methods: {
    prepareReport(reportObj) {
      if (
        reportObj.report.chartState &&
        typeof reportObj.report.chartState === 'string'
      ) {
        reportObj.report.chartState = JSON.parse(reportObj.report.chartState)
        if (
          reportObj.report.chartState.common &&
          reportObj.report.chartState.common.mode
        ) {
          reportObj.mode = reportObj.report.chartState.common.mode
          reportObj.filters = reportObj.report.chartState.common.filters
          reportObj.sorting = reportObj.report.chartState.common.sorting
        }
      }
      let report = {
        options: this.prepareReportOptions(reportObj),
        data: this.prepareReportData(reportObj),
        dateRange: this.prepareDateRange(reportObj),
        analyticsType: reportObj.report.analyticsType,
      }

      // reading as x axis
      let xDataPoint = report.options.dataPoints.find(dp => dp.axes === 'x')
      if (xDataPoint && report.data) {
        let reportDataPoint = reportObj.report.dataPoints.find(
          rdp => rdp.name === xDataPoint.key
        )
        if (reportDataPoint) {
          report.options.axis.x.datatype = reportDataPoint.yAxis.dataTypeEnum.toLowerCase()
          report.data.x = report.data[xDataPoint.key]
          xDataPoint.visible = false

          if (
            report.options.axis.x.label.text === reportDataPoint.xAxis.label
          ) {
            report.options.axis.x.label.text = reportDataPoint.yAxis.label
          }
        }
      }
      return report
    },

    prepareDateRange(reportObj) {
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
          : this.getDateRange(reportObj.dateRange.value, period),
      }
    },

    prepareReportOptions(reportObj) {
      let reportOptions = this.getReportOptions(reportObj)
      if (reportObj.mode) {
        if (!reportOptions.common) {
          reportOptions.common = {}
        }
        reportOptions.common.mode = reportObj.mode
        if (reportObj.filters) {
          reportOptions.common.filters = reportObj.filters
          reportOptions.common.sorting = reportObj.sorting
        }
        /* reportOptions.common = {
          mode: reportObj.mode,
          buildingIds: reportObj.buildingIds
        } */
      }

      let defaultOptions = {}

      let mergedOptions = deepmerge.objectAssignDeep(
        defaultOptions,
        chartModel.options,
        { style: chartModel.style },
        reportOptions
      )

      if (reportObj.report.chartState) {
        if (
          reportObj.report.chartState.settings &&
          !reportObj.report.chartState.settings.enableAlarm
        ) {
          mergedOptions.settings.alarm = false
        }
      }
      return mergedOptions
    },

    prepareBooleanReport(alarmContext) {
      let alarmData = {
        x: [],
        alarms: [],
      }
      let alarmRegions = []
      if (alarmContext) {
        let index = 0
        for (let d of alarmContext) {
          let endTime = d.endTime ? d.endTime : d.startTime
          endTime = d.startTime === endTime ? endTime + 300000 : endTime

          alarmData.x.push(this.formatDate(new Date(d.startTime), null))
          alarmData.alarms.push(index)

          alarmRegions.push({
            start: this.formatDate(new Date(d.startTime), null),
            end: this.formatDate(new Date(endTime), null),
            value: d.order,
            alarms: d.alarmContexts,
          })
          index++
        }
      }
      return {
        data: alarmData,
        regions: alarmRegions,
      }
    },

    getReportOptions(reportObj) {
      let yAxisOptions = {}
      let y2AxisOptions = {}
      let unitVsAxes = {}
      if (reportObj.report.chartState) {
        yAxisOptions = reportObj.report.chartState.axis.y
        y2AxisOptions = reportObj.report.chartState.axis.y2
        // TODO migrate
        if (!yAxisOptions.label.type) {
          yAxisOptions.label.type = 'custom'
          y2AxisOptions.label.type = 'custom'
        }

        if (reportObj.report.chartState.axis.y2.unit) {
          unitVsAxes[reportObj.report.chartState.axis.y2.unit] = 'y2'
        }
      } else {
        let yAxis = null
        let y2Axis = null
        for (let dataPoint of reportObj.report.dataPoints) {
          if (!unitVsAxes[dataPoint.yAxis.unitStr]) {
            if (!yAxis) {
              yAxis = dataPoint.yAxis
              unitVsAxes[yAxis.unitStr] = 'y'
            } else if (!y2Axis) {
              y2Axis = dataPoint.yAxis
              unitVsAxes[y2Axis.unitStr] = 'y2'
            }
          }
        }

        yAxisOptions = {
          label: {
            text: yAxis.label.toUpperCase(),
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
      if (reportObj.report.chartState) {
        axisOptions.x = reportObj.report.chartState.axis.x
      } else {
        axisOptions.x = {
          label: {
            text: xAxis.label,
          },
        }
      }
      axisOptions.x.datatype = xAxis.dataTypeEnum.toLowerCase()
      if (this.isSeriesMode(reportObj.mode)) {
        axisOptions.x.datatype = 'string'
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

      let multichartOptions = {}
      if (!this.isSeriesMode(reportObj.mode)) {
        multichartOptions = this.getMultichartOptions(
          axisOptions,
          dataPoints,
          reportObj.report.dataPoints,
          reportObj.report.chartState
        )
      }

      let widgetLegend = this.prepareWidgetLegends(reportObj, dataPoints)
      let safeLimit = this.prepareSafelimit(reportObj)

      if (reportObj.report.chartState) {
        reportObj.report.chartState.axis = axisOptions
        reportObj.report.chartState.dataPoints = dataPoints
        reportObj.report.chartState.multichart = multichartOptions
        reportObj.report.chartState.widgetLegend = widgetLegend
        reportObj.report.chartState.safeLimit = safeLimit

        return reportObj.report.chartState
      }
      let defaultChartType =
        this.isSeriesMode(reportObj.mode) || reportObj.xAggr > 0
          ? 'bar'
          : 'area'
      return {
        type: reportObj.report.chartState
          ? reportObj.report.chartState.type
          : defaultChartType,
        axis: axisOptions,
        multichart: multichartOptions,
        dataPoints: dataPoints,
        widgetLegend,
        safeLimit,
      }
    },

    getMultichartOptions(
      mainAxisOptions,
      dataPoints,
      reportDataPoints,
      chartState
    ) {
      let multichartOptions = {}

      for (let dp of dataPoints) {
        let dpList = []
        if (dp.type === 'datapoint') {
          // dp.axes = 'y'
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
            label: { type: 'auto' },
          },
          y2: {
            label: { type: 'auto' },
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
                  label: 'status',
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
    setColorForNonExisting(finalDataPoints, colors) {
      let colorIndex = 0
      for (let dataPoint of finalDataPoints) {
        if (dataPoint.type === 'datapoint') {
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
      console.log('set Color')
      console.log(finalDataPoints)
      return finalDataPoints
    },
    prepareDataPoints(reportObj, unitVsAxes) {
      console.log('prepareDataPoints new ')
      console.log(reportObj)
      let existingColors = []
      if (
        reportObj.mode === 1 ||
        reportObj.mode === 4 ||
        reportObj.mode === 5 ||
        reportObj.mode === 6 ||
        reportObj.mode === 7 ||
        reportObj.mode === 8
      ) {
        let newDataPoints = []
        let noOfDataPoints = reportObj.report.dataPoints.length
        if (reportObj.report.baseLines) {
          noOfDataPoints += reportObj.report.baseLines.length
        }
        // let colorIndex = 0

        for (let i = 0; i < reportObj.report.dataPoints.length; i++) {
          let dataPoint = reportObj.report.dataPoints[i]
          let point = {
            visible: true,
            label: dataPoint.name,
            key: dataPoint.name,
            alias: dataPoint.aliases ? dataPoint.aliases.actual : '',
            type: 'datapoint',
            chartType: '',
            // color: colors[colorIndex]
            color: null,
            axes: unitVsAxes[dataPoint.yAxis.unitStr]
              ? unitVsAxes[dataPoint.yAxis.unitStr]
              : 'y',
            aggr: dataPoint.yAxis.aggr,
            parentId: dataPoint.criteria
              ? parseInt(dataPoint.criteria.conditions['1'].value.trim())
              : null, // TODO remove
            fieldId: dataPoint.yAxis.fieldId, // TODO remove
            fieldName: dataPoint.yAxis.fieldName, // TODO remove
            metric: dataPoint.yAxis.metricEnum,
            pointType: dataPoint.type,
            unitStr: dataPoint.yAxis.unitStr,
            unitObj: dataPoint.yAxis.unitObj,
            dataType: dataPoint.yAxis.dataTypeEnum,
            enumMap: dataPoint.yAxis.enumMap,
          }
          // if (point.unitObj) {
          //   point['customFormat'] = defaults.buildFormatObject(point)
          // }
          // else {
          //   if (defaults.fetchDefaultFormat(point.dataType.trim().toLowerCase()) !== null) {
          //     point['customFormat'] = defaults.fetchDefaultFormat(point.dataType.trim().toLowerCase())
          //   }
          // }
          newDataPoints.push(point)
          // colorIndex++

          if (reportObj.report.baseLines) {
            for (let bl of reportObj.report.baseLines) {
              if (dataPoint.type === 2) {
                continue
              }
              let blKey = dataPoint.name + ' - ' + bl.baseLine.name
              let point = {
                visible: true,
                label: blKey,
                key: blKey,
                alias: dataPoint.aliases
                  ? dataPoint.aliases[bl.baseLine.name]
                  : '',
                dpKey: dataPoint.name,
                dpAlias: dataPoint.aliases ? dataPoint.aliases.actual : '',
                type: 'datapoint',
                chartType: '',
                // color: colors[colorIndex],
                color: null,
                axes: unitVsAxes[dataPoint.yAxis.unitStr]
                  ? unitVsAxes[dataPoint.yAxis.unitStr]
                  : 'y',
                isBaseLine: true,
                baseLineName: bl.baseLine.name,
                parentId: dataPoint.criteria
                  ? parseInt(dataPoint.criteria.conditions['1'].value.trim())
                  : null,
                fieldId: dataPoint.yAxis.fieldId,
                fieldName: dataPoint.yAxis.fieldName,
                metric: dataPoint.yAxis.metricEnum,
                pointType: dataPoint.type,
                unitStr: dataPoint.yAxis.unitStr,
                unitObj: dataPoint.yAxis.unitObj,
                dataType: dataPoint.yAxis.dataTypeEnum,
                enumMap: dataPoint.yAxis.enumMap,
              }
              // if (point.unitObj) {
              //   point['customFormat'] = defaults.buildFormatObject(point)
              // }
              // else {
              //   if (defaults.fetchDefaultFormat(point.dataType.trim().toLowerCase()) !== null) {
              //     point['customFormat'] = defaults.fetchDefaultFormat(point.dataType.trim().toLowerCase())
              //   }
              // }
              newDataPoints.push(point)
              // colorIndex++
            }
          }
        }

        let finalDataPoints = []
        if (reportObj.report.chartState) {
          let newDataPointMap = {}
          for (let newDp of newDataPoints) {
            newDataPointMap[newDp.key] = newDp
          }

          let stateDataPointMap = {}
          let stateDataPoints = reportObj.report.chartState.dataPoints
          for (let i = 0; i < stateDataPoints.length; i++) {
            let dp = stateDataPoints[i]
            if (dp.type === 'datapoint') {
              stateDataPointMap[dp.key] = true
              let newDp = newDataPointMap[dp.key]
              if (newDp) {
                newDp.label = dp.label
                newDp.visible = dp.visible
                newDp.chartType = dp.chartType
                newDp.color = dp.color
                newDp.axes = dp.axes
                existingColors.push(dp.color)
                // if (dp.customFormat) {
                //   newDp.customFormat = dp.customFormat
                // }
                finalDataPoints.push(newDp)
              }
            } else {
              let childList = []
              for (let j = 0; j < dp.children.length; j++) {
                let subDp = dp.children[j]
                stateDataPointMap[subDp.key] = true

                let newDp = newDataPointMap[subDp.key]
                if (newDp) {
                  newDp.label = subDp.label
                  newDp.visible = subDp.visible
                  newDp.chartType = subDp.chartType
                  newDp.color = subDp.color
                  existingColors.push(subDp.color)
                  newDp.axes = subDp.axes
                  // if (dp.customFormat) {
                  //   newDp.customFormat = dp.customFormat
                  // }
                  childList.push(newDp)
                }
              }

              finalDataPoints.push({
                label: dp.label,
                type: 'group',
                groupKey: dp.groupKey,
                chartType: '',
                children: childList,
                unit: dp.unit,
                dataType: dp.dataType === '' ? '' : dp.dataType,
              })
            }
          }
          if (
            reportObj.report.chartState.settings &&
            reportObj.report.chartState.settings.chartMode &&
            reportObj.report.chartState.settings.chartMode === 'multi' &&
            this.hasDiffUnit(newDataPoints, finalDataPoints) &&
            reportObj.report.chartState.settings.hasOwnProperty('autoGroup') &&
            reportObj.report.chartState.settings.autoGroup === false
          ) {
            console.log('Auto group enabled')
            reportObj.report.chartState.settings.autoGroup = true
            for (let newDp of newDataPoints) {
              if (!stateDataPointMap[newDp.key]) {
                finalDataPoints.push(newDp)
              }
            }
            finalDataPoints = this.groupPoints(
              this.countUnits(finalDataPoints),
              finalDataPoints
            )
          } else if (
            reportObj.report.chartState.settings &&
            reportObj.report.chartState.settings.hasOwnProperty('autoGroup') &&
            reportObj.report.chartState.settings.autoGroup === true
          ) {
            let onlyNewDataPoints = []
            for (let newDp of newDataPoints) {
              if (!stateDataPointMap[newDp.key]) {
                onlyNewDataPoints.push(newDp)
              }
            }
            if (onlyNewDataPoints.length !== 0) {
              for (let dataPoint of onlyNewDataPoints) {
                finalDataPoints.push(dataPoint)
              }
              finalDataPoints = this.groupPoints(
                this.countUnits(finalDataPoints),
                finalDataPoints
              )
            }
          } else {
            console.log('## Single unit')
            for (let newDp of newDataPoints) {
              if (!stateDataPointMap[newDp.key]) {
                finalDataPoints.push(newDp)
              }
            }
          }
        } else {
          finalDataPoints = newDataPoints
        }
        let colors = colorHelper.newColorPicker(noOfDataPoints, existingColors)
        finalDataPoints = this.setColorForNonExisting(finalDataPoints, colors)
        return finalDataPoints
      } else {
        let newDataPoints = []
        let keys = Object.keys(reportObj.reportData)
        let noOfColors = reportObj.report.baseLines
          ? reportObj.report.baseLines.length
          : 0
        noOfColors += 1
        if (keys.length) {
          let dataPoint = reportObj.report.dataPoints[0]

          let point = {
            visible: true,
            label: dataPoint.yAxis.label,
            alias: dataPoint.aliases ? dataPoint.aliases.actual : '',
            key: this.isSeriesMode(reportObj.mode)
              ? 'actual'
              : dataPoint.yAxis.label,
            type: 'datapoint',
            chartType: '',
            // color: colors[colorIndex],
            color: null,
            axes: 'y',
            aggr: dataPoint.yAxis.aggr,
            parentId: dataPoint.criteria
              ? parseInt(dataPoint.criteria.conditions['1'].value.trim())
              : null,
            fieldId: dataPoint.yAxis.fieldId,
            fieldName: dataPoint.yAxis.fieldName,
            metric: dataPoint.yAxis.metricEnum,
            unitStr: dataPoint.yAxis.unitStr,
            unitObj: dataPoint.yAxis.dataObj,
            dataType: dataPoint.yAxis.dataTypeEnum,
            enumMap: dataPoint.yAxis.enumMap,
          }
          // if (point.unitObj) {
          //   point['customFormat'] = defaults.buildFormatObject(point)
          // }
          // else {
          //   if (defaults.fetchDefaultFormat(point.dataType.trim().toLowerCase()) !== null) {
          //     point['customFormat'] = defaults.fetchDefaultFormat(point.dataType.trim().toLowerCase())
          //   }
          // }
          newDataPoints.push(point)

          if (reportObj.report.baseLines) {
            for (let bl of reportObj.report.baseLines) {
              let point = {
                visible: true,
                label: bl.baseLine.name,
                alias: dataPoint.aliases
                  ? dataPoint.aliases[bl.baseLine.name]
                  : '',
                key: this.isSeriesMode(reportObj.mode)
                  ? bl.baseLine.name
                  : dataPoint.name + ' - ' + bl.baseLine.name,
                dpKey: dataPoint.name,
                dpAlias: dataPoint.aliases ? dataPoint.aliases.actual : '',
                type: 'datapoint',
                chartType: '',
                // color: colors[colorIndex]
                color: null,
                axes: 'y',
                isBaseLine: true,
                baseLineName: bl.baseLine.name,
                parentId: dataPoint.criteria
                  ? parseInt(dataPoint.criteria.conditions['1'].value.trim())
                  : null,
                fieldId: dataPoint.yAxis.fieldId,
                fieldName: dataPoint.yAxis.fieldName,
                metric: dataPoint.yAxis.metricEnum,
                unitStr: dataPoint.yAxis.unitStr,
                unitObj: dataPoint.yAxis.unitObj,
                dataType: dataPoint.yAxis.dataTypeEnum,
                enumMap: dataPoint.yAxis.enumMap,
              }
              // if (point.unitObj) {
              //   point['customFormat'] = defaults.buildFormatObject(point)
              // }
              // else {
              //   if (defaults.fetchDefaultFormat(point.dataType.trim().toLowerCase()) !== null) {
              //     point['customFormat'] = defaults.fetchDefaultFormat(point.dataType.trim().toLowerCase())
              //   }
              // }
              newDataPoints.push(point)
            }
          }
        }

        let stateDataPointMap = {}
        let stateDataPointGroupMap = {}
        if (reportObj.report.chartState) {
          let stateDataPoints = reportObj.report.chartState.dataPoints
          for (let i = 0; i < stateDataPoints.length; i++) {
            let dp = stateDataPoints[i]
            if (dp.type === 'datapoint') {
              stateDataPointMap[dp.key] = dp
            } else {
              for (let j = 0; j < dp.children.length; j++) {
                let subDp = dp.children[j]
                stateDataPointMap[subDp.key] = subDp
                stateDataPointGroupMap[subDp.key] = dp.label
              }
            }
          }
        }

        let finalDataPoints = []
        let groupVsList = {}
        for (let newDp of newDataPoints) {
          if (stateDataPointMap[newDp.key]) {
            let stateDp = stateDataPointMap[newDp.key]

            newDp.label = stateDp.label
            newDp.visible = stateDp.visible
            newDp.chartType = stateDp.chartType
            newDp.color = stateDp.color
            existingColors.push(stateDp.color)
            newDp.axes = stateDp.axes
          } else if (stateDataPointGroupMap[newDp.key]) {
            let groupLabel = stateDataPointGroupMap[newDp.key]
            if (!groupVsList[groupLabel]) {
              groupVsList[groupLabel] = {
                label: groupLabel,
                type: 'group',
                chartType: '',
                children: [],
              }
            }
            groupVsList[groupLabel].children.push(newDp)
          } else {
            finalDataPoints.push(newDp)
          }
        }
        let colors = colorHelper.newColorPicker(noOfColors, existingColors)
        finalDataPoints = this.setColorForNonExisting(finalDataPoints, colors)
        let groupKeys = Object.keys(groupVsList)
        for (let gk of groupKeys) {
          if (groupVsList[gk].children.length) {
            finalDataPoints.push(groupVsList[gk])
          }
        }
        return finalDataPoints
      }
    },
    prepareYAxes(axisOptions, dataPoints, reportDataPoints) {
      let axes = ['y', 'y2']
      let allPoints = []
      for (let dp of dataPoints) {
        if (dp.type !== 'group') {
          if (dp.axes !== 'x' && dp.visible !== false) {
            allPoints.push(dp)
          }
        } else {
          allPoints.push(
            ...dp.children.filter(
              child => child.axes !== 'x' && child.visible !== false
            )
          )
        }
      }
      axes.forEach(axis => {
        let yAxis = axisOptions[axis]

        let yPoints = allPoints.filter(dp => dp.axes === axis)
        if (!yPoints.length) {
          yAxis.show = false
          return
        }
        yAxis.show = true

        let rdPoints = []
        let rdLabels = new Set()
        for (let point of yPoints) {
          let dp = reportDataPoints.find(
            rdp => point.dpKey === rdp.name || point.key === rdp.name
          )
          if (dp.yAxis.unitStr) {
            rdPoints.push(dp)
          }
          if (dp.yAxis.label) {
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
      if (reportObj && reportObj.safeLimits) {
        for (let key in reportObj.safeLimits) {
          let range = reportObj.safeLimits[key]
          if (range) {
            if (range[0]) {
              safeLimits.push({
                value: range[0],
                label: 'Min',
                key: 'min',
                datapoint: key,
              })
            }

            if (range[1]) {
              safeLimits.push({
                value: range[1],
                label: 'Max',
                key: 'max',
                datapoint: key,
              })
            }
          }
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
            !this.isSeriesMode(reportObj.mode),
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

      if (points.length === 1) {
        widgetLegend.variances[dataPoints[0].alias] = [
          'min',
          'max',
          'sum',
          'avg',
        ]
      } else {
        points.forEach(dp => {
          let showSumAggr =
            sumDefaultUnits.includes(dp.unitStr) ||
            sumDefaultFields.includes(dp.fieldName) ||
            sumMetricFields.includes(dp.metric)
          widgetLegend.variances[dp.alias] =
            dp.pointType !== 2 ? [showSumAggr ? 'sum' : 'avg'] : []
        })
      }

      if (stateLegend) {
        this.$helpers.copy(widgetLegend.variances, stateLegend.variances)
      }
      return widgetLegend
    },

    getTimePeriod(reportObj) {
      let period = null
      if (reportObj.xAggr) {
        if (reportObj.xAggr === 20) {
          period = 'hourly'
        } else if (reportObj.xAggr === 12) {
          period = 'daily'
        } else if (reportObj.xAggr === 11) {
          period = 'weekly'
        } else if (reportObj.xAggr === 10) {
          period = 'monthly'
        } else if (reportObj.xAggr === 8) {
          period = 'yearly'
        } else if (reportObj.xAggr === 25) {
          period = 'quarterly'
        }
      }
      return period
    },

    isEmptyData(reportObj) {
      for (let key in reportObj.reportData) {
        let dp = reportObj.reportData[key]
        let keys = Object.keys(dp)
        if (keys.length) {
          for (let k of keys) {
            let d = reportObj.reportData[key][k]
            if (typeof d === 'object') {
              if (Object.keys(d).length) {
                return false
              }
            } else {
              return false
            }
          }
        }
      }
      return true
    },

    isSeriesMode(mode) {
      if (mode === 2 || mode === 6 || mode === 7 || mode === 8) {
        return true
      }
      return false
    },

    prepareReportData(reportObj) {
      if (this.isEmptyData(reportObj)) {
        return null
      }
      let x = []
      let data = {}

      if (this.isSeriesMode(reportObj.mode)) {
        let dataList = []
        let xMap = {}
        if (reportObj.report.baseLines) {
          if (reportObj.filters) {
            for (let bl of reportObj.report.baseLines) {
              let baseLineData = {}
              for (let key in reportObj.reportData) {
                if (reportObj.reportData[key][bl.baseLine.name]) {
                  let blData = reportObj.reportData[key][bl.baseLine.name]
                  for (let d of Object.keys(blData)) {
                    xMap[d] = key
                    baseLineData[d] = blData[d]
                  }
                }
                dataList.push({
                  name: key + ' - ' + bl.baseLine.name,
                  data: baseLineData,
                })
              }
            }
          } else {
            for (let bl of reportObj.report.baseLines) {
              let baseLineData = {}
              for (let key in reportObj.reportData) {
                if (reportObj.reportData[key][bl.baseLine.name]) {
                  let blData = reportObj.reportData[key][bl.baseLine.name]
                  for (let d of Object.keys(blData)) {
                    xMap[d] = key
                    baseLineData[d] = blData[d]
                  }
                }
              }

              dataList.push({
                name: bl.baseLine.name,
                data: baseLineData,
              })
            }
          }
        }

        if (reportObj.filters) {
          for (let key in reportObj.reportData) {
            let actualData = {}
            if (reportObj.reportData[key]['actual']) {
              let actualDataObj = reportObj.reportData[key]['actual']
              for (let d of Object.keys(actualDataObj)) {
                xMap[d] = key
                actualData[d] = actualDataObj[d]
              }
            }
            dataList.push({
              name: key,
              data: actualData,
            })
          }
        } else {
          let actualData = {}
          for (let key in reportObj.reportData) {
            if (reportObj.reportData[key]['actual']) {
              let actualDataObj = reportObj.reportData[key]['actual']
              for (let d of Object.keys(actualDataObj)) {
                xMap[d] = key
                actualData[d] = actualDataObj[d]
              }
            }
          }
          dataList.push({
            name: 'actual',
            data: actualData,
          })
        }

        for (let xVal of reportObj.reportXValues) {
          if (reportObj.filters) {
            x.push(xVal)
          } else {
            x.push(xMap[xVal])
          }

          for (let dataObj of dataList) {
            if (!data[dataObj.name]) {
              data[dataObj.name] = []
            }
            if (dataObj.data[xVal]) {
              data[dataObj.name].push(dataObj.data[xVal])
            } else {
              data[dataObj.name].push(null)
            }
          }
        }
      } else {
        let period = this.getTimePeriod(reportObj)

        if (!period) {
          let xValues = []

          for (let time of reportObj.reportXValues) {
            xValues.push(time)
          }

          if (reportObj.report.baseLines) {
            for (let time of xValues) {
              for (let bl of reportObj.report.baseLines) {
                for (let key in reportObj.reportData) {
                  let dp = reportObj.report.dataPoints.find(
                    dp => dp.name === key
                  )
                  if (dp.type !== 2) {
                    let blKey = key + ' - ' + bl.baseLine.name
                    if (!data[blKey]) {
                      data[blKey] = []
                    }

                    if (
                      reportObj.reportData[key][bl.baseLine.name] &&
                      reportObj.reportData[key][bl.baseLine.name][time]
                    ) {
                      data[blKey].push(
                        reportObj.reportData[key][bl.baseLine.name][time]
                      )
                    } else {
                      data[blKey].push(null)
                    }
                  }
                }
              }
            }
          }

          for (let time of xValues) {
            x.push(this.formatDate(new Date(time), period))

            for (let key in reportObj.reportData) {
              if (!data[key]) {
                data[key] = []
              }
              if (
                reportObj.reportData[key]['actual'] &&
                reportObj.reportData[key]['actual'][time]
              ) {
                data[key].push(reportObj.reportData[key]['actual'][time])
              } else {
                data[key].push(null)
              }
            }
          }
        } else {
          let dataPointKeys = []
          let tmpData = {}

          if (reportObj.report.baseLines) {
            for (let bl of reportObj.report.baseLines) {
              for (let key in reportObj.reportData) {
                let dp = reportObj.report.dataPoints.find(dp => dp.name === key)
                if (dp.type !== 2) {
                  let blKey = key + ' - ' + bl.baseLine.name
                  dataPointKeys.push(blKey)
                  tmpData[blKey] = {}

                  let blData = reportObj.reportData[key][bl.baseLine.name]
                  if (blData) {
                    for (let d of Object.keys(blData)) {
                      let cd = parseInt(d)

                      let dateStr = this.formatDate(new Date(cd), period)

                      tmpData[blKey][dateStr] =
                        reportObj.reportData[key][bl.baseLine.name][d]
                    }
                  }
                }
              }
            }
          }

          for (let time of reportObj.reportXValues) {
            let dateStr = this.formatDate(new Date(time), period)

            for (let key in reportObj.reportData) {
              if (dataPointKeys.indexOf(key) === -1) {
                dataPointKeys.push(key)
              }
              if (!tmpData[key]) {
                tmpData[key] = {}
              }
              if (
                reportObj.reportData[key]['actual'] &&
                reportObj.reportData[key]['actual'][time]
              ) {
                tmpData[key][dateStr] =
                  reportObj.reportData[key]['actual'][time]
              }
            }
          }

          let range = this.getDateRange(reportObj.dateRange.value, period)
          x = range.domain
          for (let val of x) {
            for (let key of dataPointKeys) {
              if (!data[key]) {
                data[key] = []
              }
              if (tmpData[key] && tmpData[key][val]) {
                data[key].push(tmpData[key][val])
              } else {
                data[key].push(null)
              }
            }
          }
        }
      }

      if (x.length) {
        data.x = x
        return data
      }
      return null
    },

    getDateRange(dateRange, period) {
      const momentRange = MomentRange.extendMoment(moment)
      let range = momentRange.range(
        new Date(dateRange[0]),
        new Date(dateRange[1])
      )

      let dateFormat = this.getDateFormat(period)

      let dateAxis = {}
      dateAxis.min = this.formatDate(new Date(dateRange[0]), period)
      dateAxis.max = this.formatDate(new Date(dateRange[1]), period)

      if (dateFormat.interval) {
        let domain = null
        if (period === 'weekly') {
          domain = Array.from(range.by(dateFormat.interval)).map(r =>
            moment(r.valueOf())
              .tz(Vue.prototype.$timezone)
              .startOf('week')
              .format(dateFormat.format)
          )
        } else if (period === 'monthly') {
          domain = Array.from(range.by(dateFormat.interval)).map(r =>
            moment(r.valueOf())
              .tz(Vue.prototype.$timezone)
              .startOf('month')
              .format(dateFormat.format)
          )
        } else if (period === 'quarterly') {
          domain = Array.from(range.by(dateFormat.interval)).map(r =>
            moment(r.valueOf())
              .tz(Vue.prototype.$timezone)
              .startOf('quarter')
              .format(dateFormat.format)
          )
        } else {
          domain = Array.from(range.by(dateFormat.interval)).map(r =>
            moment(r.valueOf())
              .tz(Vue.prototype.$timezone)
              .format(dateFormat.format)
          )
        }
        if (domain.length === 2 && domain[0] === domain[1]) {
          domain = [domain[0]]
        }
        dateAxis.domain = domain
      }
      return dateAxis
    },

    formatDate(date, period) {
      let dateFormat = this.getDateFormat(period)
      return moment(date)
        .tz(Vue.prototype.$timezone)
        .format(dateFormat.format)
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
          tooltip: 'ww gggg',
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
      }

      if (!formatConfig) {
        formatConfig = {
          period: 'minute',
          interval: 'minutes',
          format: 'MM-DD-YYYY HH:mm',
          d3Format: '%m-%d-%Y %H:%M',
          tooltip: 'LLL',
        }
      }
      return formatConfig
    },
  },
}
