import moment from 'moment-timezone'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import { isEmpty } from '@facilio/utils/validation'

export default {
  methods: {
    getModularReportEditURL(module) {
      let url = null
      switch (module) {
        case 'workorder':
        case 'workorderLabour':
        case 'workorderCost':
        case 'workorderItem':
        case 'workorderTools':
        case 'workorderService':
        case 'workorderTimeLog':
        case 'workorderHazard':
        case 'plannedmaintenance':
          url = '/app/wo/reports/new'
          break
        case 'inspectionTemplate':
        case 'inspectionResponse':
          url = '/app/inspection/reports/new'
          break
        case 'purchaseorder':
        case 'poterms':
        case 'purchaseorderlineitems':
        case 'purchaserequest':
        case 'purchaserequestlineitems':
          url = '/app/purchase/reports/new'
          break
        case 'budget':
        case 'budgetamount':
          url = '/app/ac/reports/new'
          break
        case 'item':
        case 'tool':
        case 'itemTransactions':
        case 'tootTransactions':
        case 'itemTypes':
        case 'toolTypes':
        case 'storeRoom':
        case 'shipment':
        case 'transferrequest':
        case 'transferrequestshipmentreceivables':
        case 'transferrequestpurchaseditems':
          if (this.$route.path && this.$route.path.includes('/at/reports/')) {
            url = '/app/at/reports/new'
          } else {
            url = '/app/inventory/reports/new'
          }
          break
        case 'alarm':
        case 'newreadingalarm':
        case 'readingalarmoccurrence':
        case 'bmsalarm':
        case 'mlAnomalyAlarm':
        case 'anomalyalarmoccurrence':
        case 'violationalarm':
        case 'violationalarmoccurrence':
        case 'operationalarm':
        case 'operationalarmoccurrence':
        case 'sensoralarm':
        case 'sensoralarmoccurrence':
        case 'sensorrollupalarm':
        case 'sensorrollupalarmoccurrence':
        case 'basealarm':
        case 'alarmoccurrence':
        case 'readingevent':
        case 'bmsevent':
        case 'mlAnomalyEvent':
        case 'violationevent':
        case 'operationevent':
        case 'sensorevent':
        case 'baseevent':
          url = '/app/fa/reports/new'
          break
        case 'serviceRequest':
          url = '/app/sr/reports/new'
          break
        case 'asset':
          url = '/app/at/reports/new'
          break
        case 'visitor':
        case 'visitorlog':
        case 'invitevisitor':
        case 'watchlist':
          url = '/app/vi/reports/new'
          break
        case 'tenant':
        case 'tenantunit':
        case 'tenantcontact':
        case 'quote':
        case 'contact':
        case 'tenantspaces':
        case 'quotelineitems':
        case 'quoteterms':
        case 'people':
        case 'newsandinformationsharing':
        case 'neighbourhoodsharing':
        case 'dealsandofferssharing':
        case 'contactdirectorysharing':
        case 'admindocumentsharing':
        case 'audienceSharing':
          url = '/app/tm/reports/new'
          break
        case 'contracts':
        case 'purchasecontracts':
        case 'purchasecontractlineitems':
        case 'labourcontracts':
        case 'labourcontractlineitems':
        case 'warrantycontracts':
        case 'warrantycontractlineitems':
        case 'rentalleasecontracts':
        case 'rentalleasecontractlineitems':
          url = '/app/ct/reports/new'
          break
        case 'custom':
          url = '/app/ca/reports/new'
          break
        default:
          url = '/app/at/reports/new'
          break
      }
      return url
    },
    getMillis(timeString, momentFormat, period) {
      let time = []
      let periodString = this.getMomentPeriod(period)
      time.push(
        moment
          .tz(timeString, momentFormat, this.$timezone)
          .startOf(periodString)
          .valueOf()
      )
      time.push(
        moment
          .tz(timeString, momentFormat, this.$timezone)
          .endOf(periodString)
          .valueOf()
      )
      return time
    },
    getModularReportURL(module) {
      let url = null
      switch (module) {
        case 'workorder':
        case 'workorderLabour':
        case 'workorderCost':
        case 'workorderItem':
        case 'workorderTools':
        case 'workorderService':
        case 'workorderTimeLog':
        case 'workorderHazard':
        case 'plannedmaintenance':
          url = '/app/wo/reports'
          break
        case 'inspectionTemplate':
        case 'inspectionResponse':
          url = '/app/inspection/reports'
          break
        case 'purchaseorder':
        case 'poterms':
        case 'purchaseorderlineitems':
        case 'purchaserequest':
        case 'purchaserequestlineitems':
          url = '/app/purchase/reports'
          break
        case 'budget':
        case 'budgetamount':
          url = '/app/ac/reports'
          break
        case 'item':
        case 'tool':
        case 'itemTransactions':
        case 'tootTransactions':
        case 'itemTypes':
        case 'toolTypes':
        case 'storeRoom':
        case 'shipment':
        case 'transferrequest':
        case 'transferrequestshipmentreceivables':
        case 'transferrequestpurchaseditems':
          if (this.$route.path && this.$route.path.includes('/at/reports/')) {
            url = '/app/at/reports'
          } else {
            url = '/app/inventory/reports'
          }
          break
        case 'alarm':
        case 'newreadingalarm':
        case 'readingalarmoccurrence':
        case 'bmsalarm':
        case 'mlAnomalyAlarm':
        case 'anomalyalarmoccurrence':
        case 'violationalarm':
        case 'violationalarmoccurrence':
        case 'operationalarm':
        case 'operationalarmoccurrence':
        case 'sensoralarm':
        case 'sensoralarmoccurrence':
        case 'sensorrollupalarm':
        case 'sensorrollupalarmoccurrence':
        case 'basealarm':
        case 'alarmoccurrence':
        case 'readingevent':
        case 'bmsevent':
        case 'mlAnomalyEvent':
        case 'violationevent':
        case 'operationevent':
        case 'sensorevent':
        case 'baseevent':
          url = '/app/fa/reports'
          break
        case 'serviceRequest':
          url = '/app/sr/reports'
          break
        case 'asset':
          url = '/app/at/reports'
          break
        case 'visitor':
        case 'visitorlog':
        case 'invitevisitor':
        case 'watchlist':
          url = '/app/vi/reports'
          break
        case 'tenant':
        case 'tenantunit':
        case 'tenantcontact':
        case 'quote':
        case 'contact':
        case 'tenantspaces':
        case 'quotelineitems':
        case 'quoteterms':
        case 'people':
        case 'newsandinformationsharing':
        case 'neighbourhoodsharing':
        case 'dealsandofferssharing':
        case 'contactdirectorysharing':
        case 'admindocumentsharing':
        case 'audienceSharing':
          url = '/app/tm/reports'
          break
        case 'contracts':
        case 'purchasecontracts':
        case 'purchasecontractlineitems':
        case 'labourcontracts':
        case 'labourcontractlineitems':
        case 'warrantycontracts':
        case 'warrantycontractlineitems':
        case 'rentalleasecontracts':
        case 'rentalleasecontractlineitems':
          url = '/app/ct/reports'
          break
        case 'custom':
          url = '/app/ca/reports'
          break
        case 'energydata':
          url = '/app/em/reports'
          break
        default:
          url = '/app/at/reports'
          break
      }
      return url
    },
    getFormattedTime(timemillis, momentFormat) {
      return moment(parseInt(timemillis))
        .tz(this.$timezone)
        .format(momentFormat)
    },
    getMomentPeriod(period) {
      if (period === 'hourly') {
        return 'hour'
      } else if (period === 'daily') {
        return 'day'
      } else if (period === 'weekly') {
        return 'week'
      } else if (period === 'monthly') {
        return 'month'
      } else if (period === 'quarterly') {
        return 'quarter'
      }
    },
    getDefaultFilters(resultObject, val) {
      let defaultFilter = {}
      if (
        (resultObject.report.dataPoints[0].xAxis.dataType === 5 ||
          resultObject.report.dataPoints[0].xAxis.dataType === 6 ||
          resultObject.report.dateOperator !== -1) &&
        ![
          'readingalarmoccurrence',
          'anomalyalarmoccurrence',
          'violationalarmoccurrence',
          'operationalarmoccurrence',
          'sensoralarmoccurrence',
          'sensorrollupalarmoccurrence',
          'alarmoccurrence',
        ].includes(resultObject.report.dataPoints[0].xAxis.moduleName)
      ) {
        // x axis has time
        let fieldName =
          resultObject.report.dataPoints[0].xAxis.dataType === 5 ||
          resultObject.report.dataPoints[0].xAxis.dataType === 6
            ? resultObject.report.dataPoints[0].xAxis.fieldName
            : resultObject.report.dataPoints[0].dateField.fieldName
        let dateOperatorId = 20
        let dateValues = resultObject.dateRange.value
        if (
          resultObject?.report?.baseLines?.length &&
          !isEmpty(resultObject?.report?.baseLines[0].diff) &&
          resultObject?.report?.chartState?.dataPoints?.length &&
          !isEmpty(val)
        ) {
          resultObject.report.chartState.dataPoints.forEach(dp => {
            if (dp.isBaseLine && dp.alias === val.id) {
              let diff = resultObject.report.baseLines[0].diff
              dateValues = [dateValues[0] - diff, dateValues[1] - diff]
            }
          })
        }
        let temp = {}
        temp['operatorId'] = dateOperatorId
        temp['value'] = dateValues.map(element => element + '')
        defaultFilter[fieldName] = temp
      }
      // if crietria is enabled
      if (
        resultObject.report.dataPoints[0].hasOwnProperty('criteria') &&
        resultObject.report.dataPoints[0].criteria !== null
      ) {
        for (let conditionIdx in resultObject.report.dataPoints[0].criteria
          .conditions) {
          let condition =
            resultObject.report.dataPoints[0].criteria.conditions[conditionIdx]
          let fieldName = condition.fieldName
          let value = null
          if (condition.value) {
            value = condition.value.split(',')
          } else {
            value = []
          }
          let temp = {}
          temp['operatorId'] = condition.operatorId
          temp['value'] = value
          defaultFilter[fieldName] = temp
        }
      }

      // applying user filters
      if (resultObject.report.userFilters) {
        // user filters are only lookupField
        for (let userFilterConfig of resultObject.report.userFilters) {
          if (parseInt(userFilterConfig.chooseValue.type) === 1) {
            let isAllValue = userFilterConfig.values
              ? userFilterConfig.values.includes('all')
              : userFilterConfig.defaultValues.length === 0
            if (isAllValue) {
              continue
            }
          }
          if (userFilterConfig.criteria) {
            let conditions = userFilterConfig.criteria.conditions
            for (let condition of Object.keys(conditions)) {
              let temp = {}
              temp['operatorId'] = conditions[condition].operatorId
              if (conditions[condition].value.split(',').length > 1) {
                temp['value'] = conditions[condition].value.split(',')
              } else {
                temp['value'] = [conditions[condition].value]
              }
              if (conditions[condition].fieldName === 'space') {
                temp = [temp]
              }
              defaultFilter[conditions[condition].fieldName] = temp
            }
          }
        }
      }
      //dBFilters
      if (resultObject.report.criteria) {
        let conditions = resultObject.report.criteria.conditions
        for (let condition of Object.keys(conditions)) {
          let temp = {}
          temp['operatorId'] = conditions[condition].operatorId
          if (conditions[condition].value.split(',').length > 1) {
            temp['value'] = conditions[condition].value.split(',')
          } else {
            temp['value'] = [conditions[condition].value]
          }
          if (conditions[condition].fieldName === 'space') {
            temp = [temp]
          }
          defaultFilter[conditions[condition].fieldName] = temp
        }
      }
      return defaultFilter
    },
    handleBasicDrillDown(
      val,
      resultObject,
      reportObject,
      dashboardUserFilters,
      popup
    ) {
      if (this.$mobile) {
        let filters = this.getFiltersForDrillDown(
          val,
          resultObject,
          reportObject
        )
        if (this.moduleName) {
          let drillDowndata = {
            moduleName: this.moduleName,
            viewName: 'all',
            viewDisplayName: 'All',
            type: 'view',
            query: {
              filter: this.$helpers.formatFilter(filters),
            },
          }
          this.$helpers.sendToMobile(drillDowndata)
        }
      } else {
        let filters = this.getFiltersForDrillDown(
          val,
          resultObject,
          reportObject
        )
        // hack
        if (dashboardUserFilters) {
          let dashboardFilterJSON = JSON.parse(dashboardUserFilters)
          for (let field of Object.keys(dashboardFilterJSON)) {
            if (!Object.keys(filters).includes(field)) {
              filters[field] = dashboardFilterJSON[field]
            }
          }
        }
        if (!popup) {
          let url = this.getListUrl(resultObject)
          if (url === 'default') {
            console.log('drilldown yet to be supported')
          } else {
            let viewMap = {
              tenantcontact: 'all-contacts',
              bmsalarm: 'bmsAlarm',
              sensorrollupalarm: 'sensorAlarm',
            }
            let currentView = viewMap[this.moduleName]
              ? viewMap[this.moduleName]
              : 'all'
            window.open(
              `${url}/${currentView}?search=${encodeURIComponent(
                JSON.stringify(filters)
              )}&includeParentFilter=true`,
              '_blank'
            )
          }
        } else {
          return filters
        }
      }
    },

    getFiltersForFormulaField(val, resultObject) {
      // function constructs filters for formula fields. Note: X is unique even for multi metric cases
      let filters = {}
      let xFieldName = resultObject.reportData.data[val.index].X
      if (
        xFieldName &&
        !resultObject.report.dataPoints[0].xAxis.field.conditions[xFieldName]
      ) {
        xFieldName = xFieldName === 'Ontime' ? 'On Schedule' : xFieldName
      }
      let condition =
        resultObject.report.dataPoints[0].xAxis.field.conditions[xFieldName]
      let temp = {}
      temp['operatorId'] = condition.operatorId
      if (condition.value.split(',').length > 1) {
        temp['value'] = condition.value.split(',')
      } else {
        temp['value'] = [condition.value]
      }

      filters[condition.fieldName] = temp
      return filters
    },
    getFiltersForGroupByFormulaFields(val, resultObject, filters) {
      // groupByFields are going to be uniform in all dataPoints
      let groupByFields =
        resultObject.report.dataPoints[0].groupByFields &&
        resultObject.report.dataPoints[0].hasOwnProperty('groupByFields')
          ? resultObject.report.dataPoints[0].groupByFields
          : []
      for (let group of groupByFields) {
        if (typeof val === 'undefined' || val === null) {
          for (let requiredCondition of Object.keys(group.field.conditions)) {
            let temp = {}
            temp['operatorId'] =
              group.field.conditions[requiredCondition].operatorId
            if (
              group.field.conditions[requiredCondition].value.split(',')
                .length > 1
            ) {
              temp['value'] = group.field.conditions[
                requiredCondition
              ].value.split(',')
            } else {
              temp['value'] = [group.field.conditions[requiredCondition].value]
            }

            filters[group.field.conditions[requiredCondition].fieldName] = temp
          }
        } else if (group.field.conditions.hasOwnProperty(val.id)) {
          let requiredCondition = group.field.conditions[val.id]
          let temp = {}
          temp['operatorId'] = requiredCondition.operatorId
          if (requiredCondition.value.split(',').length > 1) {
            temp['value'] = requiredCondition.value.split(',')
          } else {
            temp['value'] = [requiredCondition.value]
          }

          filters[requiredCondition.fieldName] = temp
          return filters
        } else {
          continue
        }
      }
      return filters
    },
    getIndexFromReportObject(value, reportObj) {
      let data = reportObj.data.x
      let index = data.findIndex(dt => dt == value.name)
      return index
    },
    getFiltersForDrillDown(val, resultObject, reportObject) {
      let filters = this.getDefaultFilters(resultObject, val)
      let xFieldName = resultObject.report.dataPoints[0].xAxis.fieldName
      let temp = {}
      if (
        resultObject.report.dataPoints[0].xAxis.fieldId === -1 &&
        ![
          'sysCreatedBy',
          'sysModifiedBy',
          'sysCreatedTime',
          'sysModifiedTime',
          'siteId',
        ].includes(xFieldName)
      ) {
        Object.assign(
          filters,
          this.getFiltersForFormulaField(val, resultObject)
        )
      } else if (
        resultObject.report.dataPoints[0].xAxis.field.lookupModule &&
        ((resultObject.report.dataPoints[0].xAxis.field.lookupModule.name ===
          'resource' &&
          [21, 22, 23, 26].includes(resultObject.report.xAggr)) ||
          resultObject.report.dataPoints[0].xAxis.field.lookupModule.name ===
            'basespace')
      ) {
        let key = resultObject.reportData.data[val.index].X
        filters[xFieldName] = {
          operatorId: 38,
          value: [key + ''],
        }
      } else if (
        resultObject.report.dataPoints[0].xAxis.dataTypeEnum.toLowerCase() ===
        'lookup'
      ) {
        let key = resultObject.reportData.data[val.index].X
        temp['operatorId'] = 36
        temp['value'] = [key + '']
        temp[
          'selectedLabel'
        ] = `${resultObject.report.dataPoints[0].xAxis.lookupMap[key]}`
        filters[xFieldName] = temp
      } else if (
        resultObject.report.dataPoints[0].xAxis.dataTypeEnum.toLowerCase() ===
        'boolean'
      ) {
        let key = resultObject.reportData.data[val.index].X
        temp['operatorId'] = 15
        temp['value'] = [
          resultObject.report.dataPoints[0].xAxis.enumMap[key].toLowerCase(),
        ]
        filters[xFieldName] = temp
      } else if (
        ['enum', 'system_enum'].includes(
          resultObject.report.dataPoints[0].xAxis.dataTypeEnum.toLowerCase()
        )
      ) {
        temp['operatorId'] = 54
        temp['value'] = [resultObject.reportData.data[val.index].X + '']
        filters[xFieldName] = temp
      } else if (
        ['multi_enum'].includes(
          resultObject.report.dataPoints[0].xAxis.dataTypeEnum.toLowerCase()
        )
      ) {
        temp['operatorId'] = 90
        temp['value'] = [resultObject.reportData.data[val.index].X + '']
        filters[xFieldName] = temp
      } else {
        temp['operatorId'] =
          resultObject.report.dataPoints[0].xAxis.dataType === 5 ||
          resultObject.report.dataPoints[0].xAxis.dataType === 6
            ? 20
            : resultObject.report.dataPoints[0].xAxis.dataType === 1
            ? 3
            : 36
        let values = null
        if (
          resultObject.report.dataPoints[0].xAxis.dataType === 5 ||
          resultObject.report.dataPoints[0].xAxis.dataType == 6
        ) {
          // let xMilliseconds = resultObject.reportData.data[val.index].X
          let domain = reportObject.dateRange.range.domain[val.index]
          if (!domain) {
            domain = reportObject.dateRange.range.domain[0]
          }
          let momentFormat = this.getDateFormat(reportObject.dateRange.period)
            .format
          let xMilliseconds = this.getMillis(
            domain,
            momentFormat,
            reportObject.dateRange.period
          )
          values = [xMilliseconds[0] + '', xMilliseconds[1] + '']
          if (
            resultObject?.report?.baseLines?.length &&
            !isEmpty(resultObject?.report?.baseLines[0].diff) &&
            resultObject?.report?.chartState?.dataPoints?.length &&
            !isEmpty(val)
          ) {
            resultObject.report.chartState.dataPoints.forEach(dp => {
              if (dp.isBaseLine && dp.alias === val.id) {
                let diff = resultObject.report.baseLines[0].diff
                values = [
                  xMilliseconds[0] - diff + '',
                  xMilliseconds[1] - diff + '',
                ]
              }
            })
          }
        } else {
          if (xFieldName && xFieldName === 'siteId') {
            let xLabelMap = resultObject.report.dataPoints[0].xAxis.lookupMap
            let result_data = resultObject.reportData.data.filter(data_obj => {
              if (xLabelMap && xLabelMap[data_obj.X]) {
                return data_obj
              }
            })
            values = [result_data[val.index].X + '']
          } else {
            values = [resultObject.reportData.data[val.index].X + '']
          }
        }
        temp['value'] = values
        filters[xFieldName] = temp
      }
      // groupByFilters
      if (
        resultObject.report.dataPoints[0].hasOwnProperty('groupByFields') &&
        resultObject.report.dataPoints[0].groupByFields !== null
      ) {
        // group by does not have time
        if (val.id === null) {
          let group =
            resultObject.report.dataPoints[0].hasOwnProperty('groupByFields') &&
            resultObject.report.dataPoints[0].groupByFields[0].field
              ? resultObject.report.dataPoints[0].groupByFields[0].field
              : null
          let groupName = group.name
          let keys = []
          if (this.computeGroupByHeadings) {
            let requiredHeadings = this.computeGroupByHeadings.slice(2)
            if (group.fieldId === -1 && group.name !== 'siteId') {
              this.getFiltersForGroupByFormulaFields(
                null,
                resultObject,
                filters
              )
            } else if (
              group.dataTypeEnum.toLowerCase() === 'lookup' ||
              group.name === 'siteId'
            ) {
              for (let val of requiredHeadings) {
                keys.push(val.id + '')
              }
              let temp = {}
              temp['operatorId'] =
                group.lookupModule &&
                (group.lookupModule.name === 'resource' ||
                  group.lookupModule.name === 'basespace') &&
                group.name !== 'siteId'
                  ? 38
                  : 36
              temp['value'] = keys
              filters[groupName] = temp
            } else if (
              ['enum', 'system_enum'].includes(group.dataTypeEnum.toLowerCase())
            ) {
              for (let val of requiredHeadings) {
                keys.push(val.id + '')
              }
              let temp = {}
              temp['operatorId'] = 54
              temp['value'] = keys
              filters[groupName] = temp
            } else if (group.dataTypeEnum.toLowerCase() === 'boolean') {
              for (let val of requiredHeadings) {
                keys.push(val.id + '')
              }
              let temp = {}
              temp['operatorId'] = 15
              temp['value'] = keys
              filters[groupName] = temp
            } else {
              for (let val of requiredHeadings) {
                keys.push(val.label + '')
              }
              let temp = {}
              temp['operatorId'] = 36
              temp['value'] = keys
              filters[groupName] = temp
            }
          }
        } else {
          // let groupName = resultObject.report.dataPoints[0].groupByFields[0].field.name
          let groupObject = this.getGroupIdFromName(val.id, resultObject)
          let group = resultObject.report.dataPoints[0].groupByFields.filter(
            groupField => groupField.fieldId === groupObject.group
          )[0]
          if (
            group.field.lookupModule &&
            (group.field.lookupModule.name === 'resource' ||
              group.field.lookupModule.name === 'basespace')
          ) {
            let groupName = group.fieldName
            let temp = {}
            temp['operatorId'] = 38
            temp['value'] = [groupObject.subGroup]
            if (group.field.lookupModule.name === 'resource') {
              filters[groupName] = [temp]
            } else {
              filters[groupName] = temp
            }
          } else if (group && group.fieldId !== -1) {
            let groupName = group.fieldName
            let temp = {}
            temp['operatorId'] = 36
            temp['value'] = [`${groupObject.subGroup}`]
            if (group.dataTypeEnum.toLowerCase() === 'lookup') {
              temp['selectedLabel'] = `${group.lookupMap[groupObject.subGroup]}`
            } else if (group.dataTypeEnum.toLowerCase() === 'boolean') {
              temp['operatorId'] = 15
              temp['value'] = [
                group.enumMap[groupObject.subGroup].toLowerCase(),
              ]
            } else if (
              ['enum', 'system_enum'].includes(group.dataTypeEnum.toLowerCase())
            ) {
              temp['operatorId'] = 54
            } else if (group.dataTypeEnum.toLowerCase() === 'multi_enum') {
              temp['operatorId'] = 90
            }
            filters[groupName] = temp
          } else {
            filters = this.getFiltersForGroupByFormulaFields(
              val,
              resultObject,
              filters
            )
          }
        }
      }
      return filters
    },

    getGroupIdFromName(groupName, resultObject) {
      if (resultObject.report.dataPoints[0].hasOwnProperty('groupByFields')) {
        for (let group of resultObject.report.dataPoints[0].groupByFields) {
          if (
            group.dataTypeEnum.toLowerCase() === 'lookup' ||
            group.dataTypeEnum.toLowerCase() === 'multi_lookup'
          ) {
            let groupMap = group.lookupMap
            for (let id in groupMap) {
              if (groupMap[id] === groupName) {
                return {
                  group: group.fieldId,
                  subGroup: id,
                }
              }
            }
          } else if (
            group.dataTypeEnum.toLowerCase() === 'enum' ||
            group.dataTypeEnum.toLowerCase() === 'system_enum' ||
            group.dataTypeEnum.toLowerCase() === 'boolean' ||
            group.dataTypeEnum.toLowerCase() === 'multi_enum'
          ) {
            let groupMap = group.enumMap
            for (let id in groupMap) {
              if (groupMap[id] === groupName) {
                return {
                  group: group.fieldId,
                  subGroup: id,
                }
              }
            }
          } else {
            return {
              group: group.fieldId,
              subGroup: groupName,
            }
          }
        }
      }
    },
    getListUrl(resultObject) {
      let url
      if (isWebTabsEnabled() && this.moduleName) {
        let { name } = findRouteForModule(this.moduleName, pageTypes.LIST) || {}

        if (name) {
          url = this.$router.resolve({ name }).href
        }

        return url || 'default'
      }
      if (typeof this.moduleName === 'undefined') {
        url = 'default'
      } else if (resultObject.module && resultObject.module.custom) {
        url = '/app/ca/modules/' + this.moduleName
      } else {
        switch (this.moduleName) {
          case 'workorder':
            url = '/app/wo/orders'
            break
          case 'plannedmaintenance':
            url = '/app/wo/pm'
            break
          case 'alarm':
          case 'newreadingalarm':
          case 'bmsalarm':
            url = '/app/fa/faults'
            break
          case 'operationalarm':
            url = '/app/fa/outofschedule'
            break
          case 'sensorrollupalarm':
            url = '/app/fa/sensoralarms'
            break
          case 'mlAnomalyAlarm':
            url = '/app/fa/anomalies'
            break
          case 'asset':
            url = '/app/at/assets'
            break
          case 'tenant':
            url = '/app/tm/tenants'
            break
          case 'tenantcontact':
            url = '/app/tm/tenantcontact'
            break
          case 'tenantunit':
            url = '/app/tm/tenantunit/list'
            break
          case 'quote':
            url = '/app/tm/quotation'
            break
          case 'vendors':
            url = '/app/vendor/vendors'
            break
          case 'visitor':
            url = '/app/vi/visitor'
            break
          case 'visitorlog':
            url = '/app/vi/visits'
            break
          case 'invitevisitor':
            url = '/app/vi/invites'
            break
          case 'watchlist':
            url = '/app/vi/watchlist'
            break
          case 'contact':
            url = '/app/home/contact'
            break
          case 'item':
            url = '/app/inventory/items'
            break
          case 'itemTypes':
            url = '/app/inventory/itemtypes'
            break
          case 'storeRoom':
            url = '/app/inventory/storerooms'
            break
          case 'shipment':
            url = '/app/inventory/shipment'
            break
          case 'tool':
            url = '/app/inventory/tools'
            break
          case 'toolTypes':
            url = '/app/inventory/tooltypes'
            break
          case 'rentalleasecontracts':
            url = '/app/ct/rentalleasecontracts'
            break
          case 'warrantycontracts':
            url = '/app/at/warrantycontracts'
            break
          case 'labourcontracts':
            url = '/app/ct/labourcontracts'
            break
          case 'purchasecontracts':
            url = '/app/ct/purchasecontracts'
            break
          case 'serviceRequest':
            url = '/app/sr/serviceRequest'
            break
          case 'inspectionTemplate':
            url = '/app/inspection/template'
            break
          case 'inspectionResponse':
            url = '/app/inspection/individual'
            break
          default:
            url = 'default'
            break
        }
      }
      return url
    },
    getUrlBasedOnModule() {
      let url = ''

      if (typeof this.moduleName === 'undefined') {
        url = '/workorder/'
      } else {
        switch (this.moduleName) {
          case 'workorder':
            url = '/workorder/'
            break
          case 'alarm':
            url = '/alarm'
            break
          default:
            url = '/workorder/'
            break
        }
      }
      return url
    },
  },
  computed: {
    printQuery() {
      if (this.$route.query.printQuery) {
        if (typeof this.$route.query.printQuery === 'string') {
          let json = JSON.parse(this.$route.query.printQuery)
          return json
        } else {
          return this.$route.query.printQuery
        }
      }
      return {}
    },
  },
}
