import { isEmpty } from '@facilio/utils/validation'
export default {
  computed: {
    showOnlyImage() {
      return (
        this.$route.query.showOnlyImage === 'true' ||
        this.$route.query.showOnlyImage === true
      )
    },
    isPrinting() {
      return (
        this.$route.query.print === 'true' || this.$route.query.print === true
      )
    },
    showHeader() {
      return this.$route.query.showHeader !== 'false'
    },
    showPrintDetails() {
      return this.$route.query.showPrintDetails !== 'false'
    },
  },
  methods: {
    getDuplicateCases() {
      return {
        SASP: 1, // Single Point Single Asset
        SAMP: 2, // Single Point Multiple asset
        MASP: 3, // Multiple Asset Single Point
        MPMA: 4, // Multiple Point Multiple Asset
      }
    },
    getFields(id) {
      let fields = []
      for (let dataPointIndex in this.resultObj.report.dataPoints) {
        fields.push(
          this.getField(id, this.resultObj.report.dataPoints[dataPointIndex])
        )
      }
      return fields
    },
    groupDataPoints(oldDataPoints, newDataPoints) {
      let result = []
      if (this.duplicateCase.case === 1 || this.duplicateCase.case === 2) {
        for (let dataPointIndex in oldDataPoints) {
          if (
            oldDataPoints[dataPointIndex].type.trim().toLowerCase() ===
            'datapoint'
          ) {
            let match = newDataPoints.filter(element => {
              return oldDataPoints[dataPointIndex].fieldId === element.fieldId
            })
            result.push(match[0])
          } else {
            // loop through group
            let groupChildren = []
            for (let childIndex in oldDataPoints[dataPointIndex].children) {
              let match = newDataPoints.filter(element => {
                return (
                  oldDataPoints[dataPointIndex].children[childIndex].fieldId ===
                  element.fieldId
                )
              })
              groupChildren.push(match[0])
            }
            let tempGroup = oldDataPoints[dataPointIndex]
            tempGroup.children = groupChildren
            result.push(tempGroup)
          }
        }
        return result
      } else if (this.duplicateCase.case === 3) {
        for (let dataPointIndex in oldDataPoints) {
          if (
            oldDataPoints[dataPointIndex].type.trim().toLowerCase() ===
            'dataPoint'
          ) {
            let match = newDataPoints.filter(element => {
              return element.parentId === oldDataPoints[dataPointIndex].parentId
            })
            result.push(match[0])
          } else {
            // loop through group
            let groupChildren = []
            for (let childIndex in oldDataPoints[dataPointIndex].children) {
              let match = newDataPoints.filter(element => {
                return (
                  element.parentId ===
                  oldDataPoints[dataPointIndex].children[childIndex].parentId
                )
              })
              groupChildren.push(match[0])
            }
            let tempGroup = oldDataPoints[dataPointIndex]
            tempGroup.children = groupChildren
            result.push(tempGroup)
          }
        }
        return result
      }
    },
    getFieldStats(id) {
      for (let readings of this.loadedFields) {
        if (Object.keys(readings.readings).includes(id.toString())) {
          return readings.readings[id]
        }
      }
    },
    getParentFromCriteria(dataPoint) {
      for (let condition of dataPoint.criteria.conditions) {
        if (condition.fieldName === 'parentId') {
          return condition.value
        }
      }
      return null
    },
    getField(id, dataPoint) {
      let field = {}
      field['parentId'] = [
        this.duplicateCase.case === 1 || this.duplicateCase.case === 2
          ? id
          : dataPoint.metaData
          ? dataPoint.metaData.parentIds[0]
          : this.getParentFromCriteria(dataPoint),
      ]
      let yAxis = {}
      yAxis['fieldId'] =
        this.duplicateCase.case === 1 || this.duplicateCase.case === 2
          ? dataPoint.yAxis.fieldId
          : id
      yAxis['aggr'] =
        this.duplicateCase.case === 1 || this.duplicateCase.case === 2
          ? dataPoint.yAxis.aggr
          : this.resultObj.report.dataPoints[0].yAxis.aggr
      yAxis['unitStr'] =
        this.duplicateCase.case === 1 || this.duplicateCase.case === 2
          ? dataPoint.yAxis.unitStr
          : this.getFieldStats(id).unitStr
      yAxis['dataType'] =
        this.duplicateCase.case === 1 || this.duplicateCase.case === 2
          ? dataPoint.yAxis.dataType
          : this.getFieldStats(id).dataType
      field['yAxis'] = yAxis
      field['aliases'] = { actual: dataPoint.aliases.actual }
      field['transformWorkflow'] =
        this.duplicateCase.case === 1 || this.duplicateCase.case === 2
          ? dataPoint.transformWorkflow
          : null
      field['type'] =
        this.duplicateCase.case === 1 || this.duplicateCase.case === 2
          ? dataPoint.type
          : this.resultObj.report.dataPoints[0].type
      return field
    },
    getNewAssetName(listOfAssets, id) {
      for (let index in listOfAssets) {
        if (listOfAssets[index].id === id) {
          return listOfAssets[index].name
        }
      }
    },
    getFieldName(listOfFields, id) {
      for (let index in listOfFields) {
        if (Object.keys(listOfFields[index].readings).includes(id.toString())) {
          return listOfFields[index].readings[id].displayName
        }
      }
      return null
    },
    getDuplicateCase(reportObj) {
      let parentIds = []
      let fieldIds = []
      let result = {}
      let duplicateCases = this.getDuplicateCases()
      if (reportObj && reportObj.options) {
        for (let index in reportObj.options.dataPoints) {
          let dataPoint = reportObj.options.dataPoints[index]
          if (dataPoint.type.trim().toLowerCase() === 'datapoint') {
            if (
              !parentIds.includes(dataPoint.parentId) &&
              dataPoint.parentId !== null
            ) {
              parentIds.push(dataPoint.parentId)
            }
            if (
              !fieldIds.includes(dataPoint.fieldId) &&
              dataPoint.fieldId !== null
            ) {
              fieldIds.push(dataPoint.fieldId)
            }
          } else {
            if (dataPoint.children.length > 0) {
              for (let childIndex in dataPoint.children) {
                if (
                  !parentIds.includes(dataPoint.children[childIndex].parentId)
                ) {
                  parentIds.push(dataPoint.children[childIndex].parentId)
                }
                if (
                  !fieldIds.includes(dataPoint.children[childIndex].fieldId)
                ) {
                  fieldIds.push(dataPoint.children[childIndex].fieldId)
                }
              }
            }
          }
        }
      }
      result['parentIds'] = parentIds
      result['fieldIds'] = fieldIds
      if (parentIds.length === 0) {
        return null
      }
      if (parentIds.length === 1 && fieldIds.length === 1) {
        result['case'] = duplicateCases.SASP
      } else if (parentIds.length === 1 && fieldIds.length > 1) {
        result['case'] = duplicateCases.SAMP
      } else if (parentIds.length > 1 && fieldIds.length === 1) {
        result['case'] = duplicateCases.MASP
      } else {
        result['case'] = duplicateCases.MPMA
      }
      return result
    },
    getDefaultReportParams(resultObj) {
      let reportParams = {}
      let reportContext = {}
      reportContext['name'] = ''
      reportContext['description'] =
        'Auto created report with ' + resultObj.report.name + ' as reference'
      reportContext['reportFolderId'] = resultObj.report.reportFolderId
      reportContext['analyticsType'] = resultObj.report.analyticsType
      reportParams['reportContext'] = reportContext
      if (resultObj.dateRange.operatorId) {
        reportParams['dateOperator'] = resultObj.dateRange.operatorId
        if (
          resultObj.dateRange.operatorId === 42 ||
          resultObj.dateRange.operatorId === 49 ||
          resultObj.dateRange.operatorId === 50 ||
          resultObj.dateRange.operatorId === 51
        ) {
          // last N days/weeks/months/quarters/years
          reportParams['dateOperatorValue'] = resultObj.report.dateRange.offset
        } else if (
          resultObj.dateRange.operatorId === 62 ||
          resultObj.dateRange.operatorId === 63 ||
          resultObj.dateRange.operatorId === 64 ||
          resultObj.dateRange.operatorId === 65
        ) {
          // custom date/week/month/quarter/year
          reportParams['dateOperatorValue'] = resultObj.report.dateRange
            .startTime
            ? resultObj.report.dateRange.startTime
            : resultObj.report.dateRange.time[0]
        } else if (resultObj.dateRange.rangeType === 'R') {
          reportParams[
            'dateOperatorValue'
          ] = resultObj.report.dateRange.time.join(',')
        }
      }
      reportParams['mode'] = resultObj.mode
      reportParams['xAggr'] = resultObj.xAggr
      return reportParams
    },
    getReadingReportDefaultParams(resultObj) {
      let reportParams = { newFormat: true }
      reportParams['startTime'] = resultObj.report.dateRange.startTime
      reportParams['endTime'] = resultObj.report.dateRange.endTime
      reportParams['mode'] = resultObj.mode
      reportParams['xAggr'] = resultObj.report.xAggr
      return reportParams
    },
    copyNamesToyAxis(fields, newReport) {
      for (let index in fields) {
        let field = fields[index]
        field['name'] = newReport[index].name
      }
      return fields
    },
    async prepareReportTemplate(resultObject, duplicateCase) {
      let temp = {}
      if (
        resultObject &&
        resultObject.report.dataPoints &&
        resultObject.report.dataPoints.length != 0
      ) {
        if (duplicateCase === 1 || duplicateCase === 2) {
          let categoryName = resultObject.report.dataPoints[0].yAxis.module.name

          let parentId = resultObject.report.dataPoints[0].metaData.parentIds[0]
          let response = await this.$http.get('/asset/summary/' + parentId)

          temp['show'] = true
          temp['categoryId'] = response.data.asset.category.id
          temp['defaultValue'] =
            resultObject.report.dataPoints[0].metaData.parentIds[0]
          temp['parentId'] = null
          temp['siteId'] = null
          temp['chooseValues'] = ['all']
          temp['buildingId'] =
            resultObject.report.dataPoints[0].buildingId &&
            resultObject.report.dataPoints[0].buildingId !== -1
              ? resultObject.report.dataPoints[0].buildingId
              : -1
        }
      }
      return temp
    },
    async loadReportTemplateValues(reportTemplate, fieldVsValueList) {
      if (reportTemplate) {
        let categoryId = reportTemplate.categoryId
        let request = {}
        /* request['category'] = {
         operatorId:36,
         value:[categoryId + ''],
         orFilters: [{field: 'id', operatorId: 9, value: [reportTemplate.defaultValue + '']}]
        } */

        if (reportTemplate.buildingId && reportTemplate.buildingId !== -1) {
          request['space'] = {
            operatorId: 38,
            value: [reportTemplate.buildingId + ''],
          }
        } else if (reportTemplate.siteId && reportTemplate.siteId !== -1) {
          request['space'] = {
            operatorId: 36,
            value: [reportTemplate.siteId + ''],
          }
        }

        if (reportTemplate.defaultValue) {
          request['id'] = {
            operatorId: 9,
            value: [reportTemplate.defaultValue + ''],
            orFilters: [
              { field: 'category', operatorId: 36, value: [categoryId + ''] },
            ],
          }
        }

        if (fieldVsValueList && fieldVsValueList.length !== 0) {
          for (let key of fieldVsValueList) {
            let values = []
            if (key.values) {
              if (typeof key.values === 'string') {
                if (key.operatorId === 5) {
                  values = key.values.split(',')
                } else {
                  values = [key.values]
                }
              } else {
                for (let val of key.values) {
                  values.push(val + '')
                }
              }
            } else {
              values = null
            }

            request[key.fieldName] = {
              operatorId: key.operatorId,
              value: values,
            }
          }
        }

        let url =
          '/asset/all?filters=' +
          encodeURIComponent(JSON.stringify(request)) +
          '&orderType=desc&overrideViewOrderBy=true'

        let response = await this.$http.get(url)

        if (response && response.data) {
          if (reportTemplate?.chooseValues?.length == 1) {
            reportTemplate.chooseValues = [reportTemplate.chooseValues[0]]
            Array.prototype.push.apply(
              reportTemplate.chooseValues,
              response.data.assets
            )
          } else {
            reportTemplate.chooseValues = response.data.assets
          }
        }
      }
    },
    getFiltersFromCriteria(reportTemplate) {
      if (reportTemplate.criteria) {
        let filters = []
        for (let key in reportTemplate.criteria.conditions) {
          let condition = reportTemplate.criteria.conditions[key]
          let temp = {}
          temp = {
            fieldName: condition.fieldName,
            operatorId: condition.operatorId,
            values: condition.value
              ? condition.operatorId === 5
                ? condition.value.split(',')
                : [condition.value]
              : null,
          }
          filters.push(temp)
        }
        return filters
      } else {
        return null
      }
    },
    checkFilterFeasibility(reportObject) {
      let duplicateCase
      if (
        reportObject &&
        reportObject.reportType &&
        reportObject.reportType !== 2 &&
        reportObject.reportType !== 3
      ) {
        let currentLocation = new Set()

        for (let dataPoint of reportObject.options.dataPoints) {
          if (dataPoint.buildingId && dataPoint.buildingId !== -1) {
            currentLocation.add(dataPoint.buildingId)
          }
        }

        if (currentLocation.size === 1) {
          duplicateCase = this.getDuplicateCase(reportObject)
          if (duplicateCase?.case === 1 || duplicateCase?.case === 2) {
            let isSpaceReadingPresent = false

            for (let dataPoint of reportObject.options.dataPoints) {
              let parentId = dataPoint.parentId
              if (currentLocation.has(parentId)) {
                isSpaceReadingPresent = true
                break
              }
            }

            if (isSpaceReadingPresent) {
              return false
            } else {
              return true
            }
          } else {
            return false
          }
        } else {
          return false
        }
      }
      return false
    },
  },
}
