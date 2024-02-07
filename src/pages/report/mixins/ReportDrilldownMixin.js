import getProperty from 'dlv'
import { isEmpty } from '@facilio/utils/validation'

import NewDateHelper from 'src/components/mixins/NewDateHelper'
// Requires ModularAnalyticsMixin and NewDataformat helper
export default {
  data() {
    return {
      drilldownParams: null,
      drilldownDatePickerObj: false, //date picker range used to generate X axis range(domain),so generate for each step of date drilldown
      // drilldownParams: {
      //   criteria: [],
      //   xField: {},
      //   xAggr: null,
      // },

      drilldownStepCount: 0,
      drilldownactionslist: [],
    }
  },
  methods: {
    resetDrillDown() {
      this.drilldownDatePickerObj = null
      this.drilldownStepCount = 0
      this.drilldownParams = null
      this.drilldownactionslist = []
    },
    drillFromCrumb(crumbIndex) {
      if (crumbIndex == -1) {
        this.resetDrillDown()
      } else {
        this.drilldownStepCount = crumbIndex + 1 //crumb index starts from  zero, drilldown steps start from 1
        this.drilldownParams.drilldownCriteria = this.drilldownParams.drilldownCriteria.slice(
          0,
          this.drilldownStepCount
        )
        let drillStepToRevert = this.drilldownPath[crumbIndex]
        this.drilldownParams.xAggr = drillStepToRevert.xAggr
        this.drilldownParams.xField = drillStepToRevert.xField
      }
    },

    drillReport(clickData, isShowListAtDrillEnd, resultObj, reportObj) {
      if (
        this.drilldownPath &&
        this.drilldownStepCount < this.drilldownPath.length
      ) {
        if (!this.drilldownParams) {
          //first drilldown step
          this.drilldownParams = {
            drilldownCriteria: [],
            xField: null,
            xAggr: null,
            seriesAlias: null,
          }
        }

        let nextDrillStep = this.drilldownPath[this.drilldownStepCount]
        let xAxis = resultObj.report.dataPoints[0].xAxis
        let { xAggr: dimensionXAggr } = resultObj.report

        let clickedXVal = null
        let dimensionValueLabel = null

        let formulaFieldName = null
        let formulaModuleName = null
        let formulaFieldOperator = null
        let drilldownhistory = {}
        //generate criteria and breadcrumb label based on xAxis type
        if (
          xAxis.fieldId == -1 &&
          ![
            'sysCreatedBy',
            'sysModifiedBy',
            'sysCreatedTime',
            'sysModifiedTime',
            'siteId',
          ].includes(xAxis.fieldName)
        ) {
          //formula field
          let forumalaName = resultObj.reportData.data[clickData.index].X
          dimensionValueLabel = forumalaName
          let condition =
            resultObj.report.dataPoints[0].xAxis.field.conditions[forumalaName]
          formulaFieldName = condition.fieldName
          formulaModuleName = xAxis.field.module.name
          formulaFieldOperator = condition.operatorId
          clickedXVal = condition?.value ? condition.value.toString() : ''
          let values = [[clickedXVal].toString()]
          let drilldowndata = {}
          let val = values[0].split(',')
          drilldowndata.value = []
          drilldowndata.operatorId = condition.operatorId
          for (let id in val) {
            drilldowndata.value.push(val[id])
          }
          drilldownhistory[condition.fieldName] = drilldowndata
          this.drilldownactionslist.push(drilldownhistory)
        } else if (xAxis.dataType === 5 || xAxis.dataType == 6) {
          //DATE FIELD DRILL DOWN
          // neet to convert X date point like Nov 4 to "startTime,endTime" format
          let clickedDateValue =
            reportObj.dateRange.range.domain[clickData.index]
          let dateRangePeriod = reportObj.dateRange.period
          let xAxisDateFormatConfig = this.getDateFormat(dateRangePeriod)

          let xMilliseconds = this.getMillis(
            clickedDateValue,
            xAxisDateFormatConfig.format,
            dateRangePeriod
          )
          clickedXVal = xMilliseconds[0] + ',' + xMilliseconds[1]
          dimensionValueLabel = this.convertToTooltipFormat(
            xMilliseconds[0],
            dateRangePeriod
          )
          this.drilldownDatePickerObj = NewDateHelper.getDatePickerObjectFromDateAggregation(
            dimensionXAggr,
            clickedXVal
          )
        } else if (xAxis.dataType === 4) {
          clickedXVal = resultObj.reportData.data[clickData.index].X
          dimensionValueLabel = this.getLabelFromReportField(clickedXVal, xAxis)

          if (clickedXVal === 1) {
            clickedXVal = true
          } else if (clickedXVal === 0) {
            clickedXVal = false
          }
          let drilldowndata = {}
          drilldowndata.operatorId = 36
          drilldowndata.value = [[clickedXVal].toString()]
          drilldownhistory[xAxis.fieldName] = drilldowndata
          this.drilldownactionslist.push(drilldownhistory)
        } else if (xAxis.dataType === 14) {
          clickedXVal = resultObj.reportData.data[clickData.index].X
          dimensionValueLabel = this.getLabelFromReportField(clickedXVal, xAxis)
          let drilldowndata = {}
          drilldowndata.operatorId = 90
          drilldowndata.value = [[clickedXVal].toString()]
          drilldownhistory[xAxis.fieldName] = drilldowndata
          this.drilldownactionslist.push(drilldownhistory)
        } else {
          clickedXVal = resultObj.reportData.data[clickData.index].X
          dimensionValueLabel = this.getLabelFromReportField(clickedXVal, xAxis)
          let drilldowndata = {}
          drilldowndata.operatorId =
            xAxis.field.tableName == 'Resources' ||
            xAxis.fieldName == 'resource'
              ? 38
              : 36
          drilldowndata.value = [[clickedXVal].toString()]
          drilldownhistory[xAxis.fieldName] = drilldowndata
          this.drilldownactionslist.push(drilldownhistory)
        }

        let dimensionValues = [clickedXVal].toString()

        let breadcrumbLabel = dimensionValueLabel
        let groupByValueLabel = null
        let { fieldId: xFieldId, moduleId: xModuleId } = xAxis

        let groupByValues = null
        let groupByFieldId = null
        let groupByModuleId = null
        let groupByXAgg = null
        let groupByFieldCol = null
        this.drilldownParams.xField = nextDrillStep.xField
        this.drilldownParams.xAggr = nextDrillStep.xAggr

        let isGroupByPresent = this.isGroupByFieldPresent(resultObj)

        if (
          ['pie', 'donut', 'gauge'].includes(reportObj.report.chartState.type)
        ) {
          this.drilldownParams.seriesAlias =
            resultObj.report.dataPoints[0].aliases.actual
        } else if (isGroupByPresent) {
          let groupByMeta = this.getGroupFromName(clickData.id, resultObj)
          let {
            group: groupByReportField,
            group: { fieldId, moduleId, aggr },
            value,
          } = groupByMeta
          groupByFieldId = fieldId
          groupByFieldCol = groupByMeta?.group?.fieldName
            ? groupByMeta.group.fieldName
            : ''
          groupByModuleId = moduleId
          groupByXAgg = aggr == -1 ? 0 : aggr

          groupByValues = [value].toString()
          groupByValueLabel = this.getLabelFromReportField(
            value,
            groupByReportField
          )
          if (
            fieldId == -1 &&
            (groupByFieldCol.includes('overdue') ||
              groupByFieldCol == 'openvsclose') &&
            groupByValues !== 'group_total'
          ) {
            let drill = {}
            let drilldownObj =
              resultObj.report.dataPoints[0].groupByFields[0].field.conditions[
                clickData.id
              ]
            let drilldowndata = {}
            let val = drilldownObj.value.split(',')
            drilldowndata.value = []
            drilldowndata.operatorId = drilldownObj.operatorId
            for (let id in val) {
              drilldowndata.value.push(val[id])
            }
            drill[drilldownObj.fieldName] = drilldowndata
            this.drilldownactionslist.push(drill)
            breadcrumbLabel += ' & ' + groupByValues
          } else {
            let drilldowndata = {}
            let drill = {}
            let groubyData = reportObj.report.dataPoints[0].groupByFields[0]
            if (
              groubyData.field.dataTypeEnum === 'LOOKUP' ||
              groubyData.field.dataTypeEnum === 'NUMBER'
            ) {
              drilldowndata.value = [groupByValues]
            } else if (groubyData.field.dataTypeEnum === 'SYSTEM_ENUM') {
              for (let val in groubyData.enumMap) {
                if (groubyData.enumMap[val] === groupByValues) {
                  drilldowndata.value = [val]
                }
              }
            } else if (
              groubyData.field.dataTypeEnum === 'BOOLEAN' ||
              groubyData.field.dataTypeEnum === 'ENUM' ||
              groubyData.field.dataTypeEnum === 'MULTI_ENUM'
            ) {
              for (let val in groubyData.enumMap) {
                if (groubyData.enumMap[val] === groupByValueLabel) {
                  drilldowndata.value =
                    groubyData.field.dataTypeEnum === 'BOOLEAN'
                      ? [groupByValueLabel.toLowerCase()]
                      : [val]
                }
              }
            } else {
              drilldowndata.value = [groupByValues]
            }
            drilldowndata.operatorId = 36
            drill[groupByFieldCol] = drilldowndata
            this.drilldownactionslist.push(drill)
            breadcrumbLabel += ' & ' + groupByValueLabel
          }
          //only one series when GROUP BY  present ,so always first series only clicked
          this.drilldownParams.seriesAlias =
            resultObj.report.dataPoints[0].aliases.actual
        } else {
          //NO group by , can have multiple series(multiple metric),drill down and show only clicked series in corresponding(child) report
          this.drilldownParams.seriesAlias = clickData.id
        }
        if (!isEmpty(reportObj) && !isEmpty(reportObj.report)) {
          const isPresent = reportObj.report.dataPoints.filter(dp => {
            return dp.aliases.actual == this.drilldownParams.seriesAlias
          })
          if (isEmpty(isPresent)) {
            return
          }
        }
        let criteria = {
          xField: {
            field_id: xFieldId,
            module_id: xModuleId,
            xAggr: dimensionXAggr,
          },
          breadcrumbLabel,
          dimensionValues,
          formulaFieldOperator, //formula type fields
          formulaFieldName,
          formulaModuleName,
        }

        if (
          isGroupByPresent &&
          !['pie', 'donut', 'gauge'].includes(reportObj.report.chartState.type)
        ) {
          criteria.groupBy = {
            field_id: groupByFieldId,
            module_id: groupByModuleId,
            xAggr: groupByXAgg,
            colName: groupByFieldCol,
          }
          criteria.groupByValues = groupByValues
        }
        this.drilldownParams.drilldownCriteria.push(criteria)

        this.drilldownStepCount++
        return true
      } else {
        return false
      }
    },

    //ReportXAxis and ReportGroupBy->Extend ReportField
    getLabelFromReportField(value, reportField) {
      if (reportField.dataTypeEnum == 'LOOKUP') {
        return reportField.lookupMap[value]
      } else if (
        reportField.dataTypeEnum == 'ENUM' ||
        reportField.dataTypeEnum == 'BOOLEAN' ||
        reportField.dataTypeEnum == 'MULTI_ENUM'
      ) {
        return reportField.enumMap[value]
      } else {
        return value
      }
    },
    isGroupByFieldPresent(resultObj) {
      return !isEmpty(resultObj.report.dataPoints[0].groupByFields)
    },
    getGroupFromName(groupName, resultObject) {
      if (resultObject.report.dataPoints[0].hasOwnProperty('groupByFields')) {
        for (let group of resultObject.report.dataPoints[0].groupByFields) {
          if (group.dataTypeEnum.toLowerCase() === 'lookup') {
            let groupMap = group.lookupMap
            for (let id in groupMap) {
              if (groupMap[id] === groupName) {
                return {
                  group: group,
                  value: id,
                }
              }
            }
          } else if (
            group.dataTypeEnum.toLowerCase() === 'enum' ||
            group.dataTypeEnum.toLowerCase() === 'boolean'
          ) {
            let groupMap = group.enumMap
            for (let id in groupMap) {
              if (groupMap[id] === groupName) {
                return {
                  group: group,
                  value: id,
                }
              }
            }
          } else {
            return {
              group: group,
              value: groupName,
            }
          }
        }
      }
    },
  },

  computed: {
    drilldownPath() {
      let drillPath = getProperty(this, 'resultObj.report.reportDrilldownPath')
      return !isEmpty(drillPath) ? drillPath : null
    },
  },
}
