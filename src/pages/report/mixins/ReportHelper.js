import moment from 'moment-timezone'
export default {
  PARENT_MODULE_VS_DEPENDENT_MODULE_FOR_REPORT: {
    workorder: 'workorder',
    workorderLabour: 'workorder',
    workorderCost: 'workorder',
    workorderItem: 'workorder',
    workorderTools: 'workorder',
    workorderService: 'workorder',
    workorderTimeLog: 'workorder',
    workorderHazard: 'workorder',
    workOrderPlannedItems: 'workorder',
    workOrderPlannedServices: 'workorder',
    workorderFailureClassRelationship: 'workorder',
    workOrderPlannedTools: 'workorder',
    workorderLabourPlan: 'workorder',
    purchaseorder: 'purchaseorder',
    poterms: 'purchaseorder',
    purchaseorderlineitems: 'purchaseorder',
    purchaserequest: 'purchaseorder',
    purchaserequestlineitems: 'purchaseorder',
    inspectionTemplate: 'inspectionTemplate',
    inspectionResponse: 'inspectionTemplate',
    budget: 'budget',
    budgetamount: 'budget',
    item: 'item',
    tool: 'item',
    itemTransactions: 'item',
    tootTransactions: 'item',
    itemTypes: 'item',
    toolTypes: 'item',
    storeRoom: 'item',
    shipment: 'item',
    transferrequest: 'item',
    transferrequestpurchaseditems: 'item',
    transferrequestshipmentreceivables: 'item',
    alarm: 'alarm',
    newreadingalarm: 'alarm',
    readingalarmoccurrence: 'alarm',
    bmsalarm: 'alarm',
    mlAnomalyAlarm: 'alarm',
    anomalyalarmoccurrence: 'alarm',
    violationalarm: 'alarm',
    violationalarmoccurrence: 'alarm',
    operationalarm: 'alarm',
    operationalarmoccurrence: 'alarm',
    sensoralarm: 'alarm',
    sensoralarmoccurrence: 'alarm',
    sensorrollupalarm: 'alarm',
    sensorrollupalarmoccurrence: 'alarm',
    basealarm: 'alarm',
    alarmoccurrence: 'alarm',
    readingevent: 'alarm',
    bmsevent: 'alarm',
    mlAnomalyEvent: 'alarm',
    violationevent: 'alarm',
    operationevent: 'alarm',
    sensorevent: 'alarm',
    baseevent: 'alarm',
    serviceRequest: 'serviceRequest',
    asset: 'asset',
    assetbreakdown: 'asset',
    controlGroupAsset: 'asset',
    assetmovement: 'asset',
    budgetmonthlyamount: 'asset',
    inspectionTriggerResourceInclExcl: 'asset',
    faultImpactFields: 'asset',
    inductionTriggerResourceInclExcl: 'asset',
    assetHazard: 'asset',
    surveyTriggerResourceInclExcl: 'asset',
    assetdepreciationCalculation: 'asset',
    vendors: 'asset',
    visitor: 'visitor',
    visitorlog: 'visitor',
    invitevisitor: 'visitor',
    watchlist: 'visitor',
    tenant: 'tenant',
    tenantunit: 'tenant',
    tenantcontact: 'tenant',
    quote: 'tenant',
    contact: 'tenant',
    tenantspaces: 'tenant',
    quotelineitems: 'tenant',
    quoteterms: 'tenant',
    people: 'tenant',
    newsandinformationsharing: 'tenant',
    neighbourhoodsharing: 'tenant',
    dealsandofferssharing: 'tenant',
    contactdirectorysharing: 'tenant',
    admindocumentsharing: 'tenant',
    audienceSharing: 'tenant',
    contracts: 'contracts',
    purchasecontracts: 'contracts',
    purchasecontractlineitems: 'contracts',
    labourcontracts: 'contracts',
    labourcontractlineitems: 'contracts',
    warrantycontracts: 'contracts',
    warrantycontractlineitems: 'contracts',
    rentalleasecontracts: 'contracts',
    rentalleasecontractlineitems: 'contracts',
  },
  methods: {
    getCurrentModule(fromRouteObj) {
      if (
        this.$helpers.isLicenseEnabled('NEW_LAYOUT') &&
        this.moduleName &&
        !fromRouteObj
      ) {
        return {
          module: this.moduleName,
          rootPath: '',
        }
      }
      if (this.currentDashboard) {
        return {
          module: this.currentDashboard.moduleName,
          rootPath: '',
        }
      }

      let routeObj = this.$route
      if (routeObj?.path.includes('/em/pivot')) {
        return 'pivot'
      }
      let module = null
      let rootPath = null
      if (this.$helpers.isLicenseEnabled('NEW_LAYOUT')) {
        if (routeObj.params.moduleName) {
          module = routeObj.params.moduleName
          rootPath = routeObj.path
        } else {
          if (routeObj.matched) {
            for (let matchedRoute of routeObj.matched) {
              if (matchedRoute.meta.module) {
                module = matchedRoute.meta.module
                rootPath = matchedRoute.path
                break
              }
            }
          }
        }
      } else {
        if (routeObj.meta.module) {
          module = routeObj.meta.module
          rootPath = routeObj.path
        } else {
          if (routeObj.matched) {
            for (let matchedRoute of routeObj.matched) {
              if (matchedRoute.meta.module) {
                module = matchedRoute.meta.module
                rootPath = matchedRoute.path
                break
              }
            }
          }
        }
      }
      rootPath = rootPath.replace('/new', '')
      rootPath = rootPath.replace('/newmatrix', '')
      rootPath = rootPath.replace('/newtabular', '')
      rootPath = rootPath.replace('/scheduled', '')
      return {
        module: module,
        rootPath: rootPath,
      }
    },
    getOperator() {
      let operator = [
        {
          index: 0,
          label: 'Today',
          trimLabel: 'D',
          value: 22,
          uptoNowValue: 43,
          timestamp: [
            moment()
              .startOf('day')
              .valueOf(),
            moment()
              .endOf('day')
              .valueOf(),
          ],
        },
        {
          index: 1,
          label: 'This Week',
          trimLabel: 'W',
          value: 31,
          uptoNowValue: 47,
          timestamp: [
            moment()
              .startOf('week')
              .valueOf(),
            moment()
              .endOf('week')
              .valueOf(),
          ],
        },
        {
          index: 2,
          label: 'This Month',
          trimLabel: 'M',
          value: 28,
          uptoNowValue: 48,
          timestamp: [
            moment()
              .startOf('month')
              .valueOf(),
            moment()
              .endOf('month')
              .valueOf(),
          ],
        },
        {
          index: 3,
          label: 'Last Month',
          trimLabel: 'M',
          value: 27,
          uptoNowValue: 48,
          timestamp: [
            moment()
              .subtract(1, 'month')
              .startOf('month')
              .valueOf(),
            moment()
              .subtract(1, 'month')
              .endOf('month')
              .valueOf(),
          ],
        },
        {
          index: 3,
          label: 'This Year',
          trimLabel: 'Y',
          value: 44,
          uptoNowValue: 46,
          timestamp: [
            moment()
              .startOf('year')
              .valueOf(),
            moment()
              .endOf('year')
              .valueOf(),
          ],
        },
        {
          index: 4,
          label: 'Last N displayUnkownValueAs',
          trimLabel: 'C',
          value: 49,
          uptoNowValue: 49,
          timestamp: [
            moment()
              .startOf('day')
              .valueOf(),
            moment()
              .endOf('day')
              .valueOf(),
          ],
        },
        {
          index: 5,
          label: 'Last N displayUnkownValueAs',
          trimLabel: 'C',
          value: 42,
          uptoNowValue: 42,
          timestamp: [
            moment()
              .startOf('day')
              .valueOf(),
            moment()
              .endOf('day')
              .valueOf(),
          ],
        },
        {
          index: 6,
          label: 'Last N displayUnkownValueAs',
          trimLabel: 'C',
          value: 50,
          uptoNowValue: 50,
          timestamp: [
            moment()
              .startOf('day')
              .valueOf(),
            moment()
              .endOf('day')
              .valueOf(),
          ],
        },
        {
          index: 7,
          label: 'Last N displayUnkownValueAs',
          trimLabel: 'C',
          value: 51,
          uptoNowValue: 51,
          timestamp: [
            moment()
              .startOf('day')
              .valueOf(),
            moment()
              .endOf('day')
              .valueOf(),
          ],
        },
        {
          index: 8,
          label: 'Range',
          trimLabel: 'R',
          value: 100,
          uptoNowValue: 100,
          timestamp: [
            moment()
              .startOf('year')
              .valueOf(),
            moment()
              .endOf('year')
              .valueOf(),
          ],
        },
        {
          index: 9,
          label: 'Yesterday',
          option: 'D',
          value: 25,
          days: '30',
          uptoNowValue: 25,
          timestamp: [
            moment()
              .subtract(1, 'day')
              .startOf('day')
              .valueOf(),
            moment()
              .subtract(1, 'day')
              .endOf('day')
              .valueOf(),
          ],
        },
      ]
      return operator
    },
    getMills(obj) {
      let id, value
      if (obj.operatorId && obj.value) {
        id = obj.operatorId
        value = obj.value
      } else {
        id = obj.operatorId
      }
      let self = this
      let options = {}
      let time = []
      if (id === 43 || id === 22) {
        id = 22
      } else if (id === 47 || id === 31) {
        id = 31
      } else if (id === 48 || id === 28) {
        id = 28
      } else if (id === 27) {
        id = 27
      } else if (id === 46 || id === 44) {
        id = 44
      } else if (id === 42) {
        id = 42
      } else if (id === 49) {
        id = 49
      } else if (id === 50) {
        id = 50
      } else if (id === 51) {
        id = 51
      } else if (id === 25) {
        id = 25
      } else if (id === 100 || id === 101) {
        id = 100
      } else {
        id = 22
      }
      let mills1 = typeof this.operaterIdTOmills(id)
      if (mills1 !== undefined) {
        let data = self.operaterIdTOmills(id)
        if (id === 42 || id === 49 || id === 50 || id === 51) {
          time = this.getNimes(id, value)
        } else {
          time = data.timestamp
        }
        options.filter = data.trimLabel
      } else {
        time = [
          moment()
            .startOf('day')
            .valueOf(),
          moment()
            .endOf('day')
            .valueOf(),
        ]
      }
      options.time = time
      return options
    },
    operaterIdTOmills(id) {
      return this.getOperator().find(sb => sb.value === id)
    },
    operaterIdUpTOmills(id) {
      return this.getOperator().find(sb => sb.uptoNowValue === id)
    },
    getNimes(id, value) {
      let val, time
      if (value) {
        val = parseInt(value)
      }
      if (id === 42) {
        time = [
          moment()
            .tz(this.$timezone)
            .subtract(val - 1, 'hour')
            .startOf('hour')
            .valueOf(),
          moment()
            .tz(this.$timezone)
            .endOf('hour')
            .valueOf(),
        ]
      } else if (id === 49) {
        time = [
          moment()
            .tz(this.$timezone)
            .subtract(val - 1, 'day')
            .startOf('day')
            .valueOf(),
          moment()
            .tz(this.$timezone)
            .endOf('day')
            .valueOf(),
        ]
      } else if (id === 50) {
        time = [
          moment()
            .tz(this.$timezone)
            .subtract(val - 1, 'week')
            .startOf('week')
            .valueOf(),
          moment()
            .tz(this.$timezone)
            .endOf('week')
            .valueOf(),
        ]
      } else if (id === 51) {
        time = [
          moment()
            .tz(this.$timezone)
            .subtract(val - 1, 'month')
            .startOf('month')
            .valueOf(),
          moment()
            .tz(this.$timezone)
            .endOf('month')
            .valueOf(),
        ]
      } else {
        time = [
          moment()
            .tz(this.$timezone)
            .subtract(0, 'hour')
            .startOf('hour')
            .valueOf(),
          moment()
            .tz(this.$timezone)
            .endOf('hour')
            .valueOf(),
        ]
      }
      return time
    },
  },

  findModuleFromSubmodule(submoduleName) {
    if (this.PARENT_MODULE_VS_DEPENDENT_MODULE_FOR_REPORT[submoduleName]) {
      return this.PARENT_MODULE_VS_DEPENDENT_MODULE_FOR_REPORT[submoduleName]
    }
    return 'workorder'
  },
  isHiddenModule(submoduleName) {
    return this.PARENT_MODULE_VS_DEPENDENT_MODULE_FOR_REPORT[submoduleName]
  },
}
