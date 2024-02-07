import moment from 'moment-timezone'
import NewDateHelper from '@/mixins/NewDateHelper'
import { isEmpty } from '@facilio/utils/validation'
import * as d3 from 'd3'

export default {
  computed: {
    isActionsDisabled() {
      if (
        this.analyticsConfig &&
        this.analyticsConfig.dataPoints &&
        this.analyticsConfig.dataPoints.length
      ) {
        return false
      }
      return true
    },
    derivations() {
      let d = []
      if (this.$route.query.derivations) {
        d = JSON.parse(this.$route.query.derivations)
      }
      d = d.map(d => {
        d.type = 'derivation'
        return d
      })
      return d
    },

    reportId() {
      if (this.$route.query && this.$route.query.reportId) {
        return parseInt(this.$route.query.reportId)
      }
      return null
    },

    alarmId() {
      if (this.$route.query && this.$route.query.alarmId) {
        return parseInt(this.$route.query.alarmId)
      }
      return null
    },
    isWithPrerequsite() {
      if (this.$route.query && this.$route.query.isWithPrerequsite) {
        return this.$route.query.isWithPrerequsite
      }
      return null
    },
    shouldIncludeMarked() {
      if (this.$route.query && this.$route.query.shouldIncludeMarked) {
        return this.$route.query.shouldIncludeMarked
      }
      return null
    },
    cardId() {
      if (this.$route.query && this.$route.query.cardId) {
        return parseInt(this.$route.query.cardId)
      }
      return null
    },
    readingRuleId() {
      if (this.$route.query && this.$route.query.readingRuleId) {
        return parseInt(this.$route.query.readingRuleId)
      }
      return null
    },
    today() {
      return moment()
        .tz(this.$timezone)
        .startOf('day')
        .valueOf()
    },
  },

  methods: {
    enableForRangeMode() {
      if (this.reportObject) {
        let rangeGroup = this.reportObject.options.dataPoints.filter(
          dp => dp.type === 'rangeGroup'
        )
        if (rangeGroup.length !== 0) {
          return false
        } else {
          return true
        }
      } else {
        return true
      }
    },
    getXAggr() {
      console.log('xAggr')
      let dateFilter = this.config.dateFilter
      let fromMoment = moment(parseInt(dateFilter.value[0]))
      let toMoment = moment(parseInt(dateFilter.value[1]))
      if (toMoment.diff(fromMoment, 'hour') > 1) {
        return 20
      } else if (toMoment.diff(fromMoment, 'days') > 1) {
        return 12
      } else if (toMoment.diff(fromMoment, 'week') > 1) {
        return 11
      } else if (toMoment.diff(fromMoment, 'month') > 1) {
        return 10
      }
    },
    getDefaultDateFilter(filter) {
      if (!filter) {
        filter = 'D'
      }
      let period = 'day'
      let operatorId = 22
      if (filter === 'D') {
        period = 'day'
        operatorId = 22
      } else if (filter === 'W') {
        period = 'week'
        operatorId = 31
      } else if (filter === 'M') {
        period = 'month'
        operatorId = 28
      } else if (filter === 'Y') {
        period = 'year'
        operatorId = 44
      }

      let actualTimeRange = [
        moment()
          .startOf(period)
          .valueOf(),
        moment()
          .endOf(period)
          .valueOf(),
      ]
      let timeRange = [
        moment()
          .tz(this.$timezone)
          .startOf(period)
          .valueOf(),
        moment()
          .tz(this.$timezone)
          .endOf(period)
          .valueOf(),
      ]
      return {
        filter: filter,
        filterName: filter,
        operatorId: operatorId,
        time: timeRange,
        actual_time: actualTimeRange,
      }
    },

    loadAnalyticsConfig() {
      this.loadBaseLines()
      let config
      if (this.$route.query.filters) {
        let filters = this.$route.query.filters
        let filtersJSON = JSON.parse(filters)
        // if (Object.keys(filtersJSON).length) {
        //   config = filtersJSON
        // }
        config = filtersJSON
      }
      if (this.derivations.length) {
        if (!config) {
          config = {}
        }
        config.dataPoints = this.derivations
      }
      return config
    },

    loadBaseLines() {
      let self = this
      if (!self.baseLineList) {
        self.$http.get('/baseline/all').then(function(response) {
          if (response.status === 200) {
            self.baseLineList = response.data ? response.data : []
          }
        })
      }
    },

    selectBaseLine() {
      if (this.selectedBaseLineId) {
        if (this.selectedBaseLineId === -1) {
          this.analyticsConfig.baseLine = null
        } else {
          this.analyticsConfig.baseLine = this.baseLineList.find(
            bl => bl.id === this.selectedBaseLineId
          )
        }
      }
    },

    printReport() {},

    exportData(type, analyticsConfig, calback) {
      let param = this.getExportParam(analyticsConfig)
      param.type = type
      this.$message('Exporting as ' + this.$constants.FILE_FORMAT[type])
      this.$http.post('/dashboard/exportAnalytics', param).then(response => {
        this.$message.close()
        if (calback) {
          calback(response.data.fileUrl)
        } else {
          window.open(response.data.fileUrl, '_blank')
        }
      })
    },
    getExportParam(analyticsConfig) {
      let getParam = dataPoint => {
        let data = {
          parentId: dataPoint.parentId || -1,
          readingFieldId: dataPoint.readingFieldId || -1,
          xAggr: analyticsConfig.period,
          yAggr: dataPoint.yAggr || 0,
          title: dataPoint.name,
        }

        if (analyticsConfig.period !== 0 && dataPoint.yAggr === 0) {
          // setting sum
          data.yAggr = 3
        } else if (analyticsConfig.period === 0 && dataPoint.yAggr !== 0) {
          // setting actual
          data.yAggr = 0
        }

        if (dataPoint.workflowId > 0) {
          data.derivation = dataPoint
        }
        return data
      }

      let dataList = []
      for (let dataPoint of analyticsConfig.dataPoints) {
        let data = getParam(dataPoint)
        dataList.push(data)

        if (analyticsConfig.baseLine) {
          let data2 = getParam(dataPoint)
          data2.baseLineId = analyticsConfig.baseLine.id
          if (analyticsConfig.baseLine.name) {
            data2.title += ' (' + analyticsConfig.baseLine.name + ')'
          }
          dataList.push(data2)
        }
      }

      let config = this.$helpers.cloneObject(analyticsConfig)
      config.path = this.$route.path
      let param = {
        analyticsDataList: dataList,
        analyticsConfig: config,
      }
      if (analyticsConfig.dateFilter) {
        param.dateFilter = analyticsConfig.dateFilter.time
          ? analyticsConfig.dateFilter.time
          : analyticsConfig.dateFilter.value
      }
      return param
    },

    getTime(operatorId, dateValue) {
      let value = 0
      try {
        value = parseInt(dateValue)
        if (isNaN(value)) {
          value = 0
        }
      } catch (err) {
        console.log(err)
      }
      let startTime = null
      let endTime = null
      let filterName = null
      if (operatorId === 22 || operatorId === 43) {
        filterName = 'D'
        startTime = moment()
          .tz(this.$timezone)
          .startOf('day')
        endTime = moment()
          .tz(this.$timezone)
          .endOf('day')
      } else if (operatorId === 25) {
        filterName = 'D'
        startTime = moment()
          .tz(this.$timezone)
          .add(-1, 'day')
          .startOf('day')
        endTime = moment()
          .tz(this.$timezone)
          .add(-1, 'day')
          .endOf('day')
      } else if (operatorId === 49) {
        filterName = 'C'
        startTime = moment()
          .tz(this.$timezone)
          .add(-value, 'day')
          .startOf('day')
        endTime = moment()
          .tz(this.$timezone)
          .endOf('day')
      } else if (operatorId === 31 || operatorId === 47) {
        filterName = 'W'
        startTime = moment()
          .tz(this.$timezone)
          .startOf('week')
        endTime = moment()
          .tz(this.$timezone)
          .endOf('week')
      } else if (operatorId === 30) {
        filterName = 'W'
        startTime = moment()
          .tz(this.$timezone)
          .add(-1, 'week')
          .startOf('week')
        endTime = moment()
          .tz(this.$timezone)
          .add(-1, 'week')
          .endOf('week')
      } else if (operatorId === 50) {
        filterName = 'C'
        startTime = moment()
          .tz(this.$timezone)
          .add(-value, 'week')
          .startOf('week')
        endTime = moment()
          .tz(this.$timezone)
          .endOf('week')
      } else if (operatorId === 28 || operatorId === 48) {
        filterName = 'M'
        startTime = moment()
          .tz(this.$timezone)
          .startOf('month')
        endTime = moment()
          .tz(this.$timezone)
          .endOf('month')
      } else if (operatorId === 27) {
        filterName = 'M'
        startTime = moment()
          .tz(this.$timezone)
          .add(-1, 'month')
          .startOf('month')
        endTime = moment()
          .tz(this.$timezone)
          .add(-1, 'month')
          .endOf('month')
      } else if (operatorId === 51) {
        filterName = 'C'
        startTime = moment()
          .tz(this.$timezone)
          .add(-value, 'month')
          .startOf('month')
        endTime = moment()
          .tz(this.$timezone)
          .endOf('month')
      } else if (operatorId === 44 || operatorId === 46) {
        filterName = 'Y'
        startTime = moment()
          .tz(this.$timezone)
          .startOf('year')
        endTime = moment()
          .tz(this.$timezone)
          .endOf('year')
      } else if (operatorId === 45) {
        filterName = 'Y'
        startTime = moment()
          .tz(this.$timezone)
          .add(-1, 'year')
          .startOf('year')
        endTime = moment()
          .tz(this.$timezone)
          .add(-1, 'year')
          .endOf('year')
      }

      return {
        filter: filterName,
        filterName: filterName,
        time: [startTime.valueOf(), endTime.valueOf()],
        // value: value
      }
    },

    loadReport(mobileConfig) {
      let self = this
      let url = null
      let reportParams = null
      if (self.reportId) {
        url = '/v2/report/fetchReport'
        reportParams = {
          reportId: self.reportId,
          newFormat: true,
        }
      } else if (self.alarmId) {
        url = '/v2/report/fetchReadingsFromAlarm'
        reportParams = {
          alarmId: self.alarmId,
          newFormat: true,
        }
        if (self.isWithPrerequsite) {
          reportParams.isWithPrerequsite = self.isWithPrerequsite
        }
        if (self.shouldIncludeMarked) {
          reportParams.shouldIncludeMarked = self.shouldIncludeMarked
        }
        if (self.readingRuleId) {
          reportParams.readingRuleId = self.readingRuleId
        }
      } else if (self.cardId) {
        url = '/v2/report/fetchReadingsFromCard'
        reportParams = {
          cardWidgetId: self.cardId,
        }
      }
      if (reportParams) {
        self.$http
          .get(url, {
            params: reportParams,
          })
          .then(function(response) {
            let result = response.data.result
            if (result) {
              let dataPoints = []
              let mode = 1
              let filters = null
              let sorting = {
                sortByField: null,
                orderByFunc: 3,
                limit: 10,
              }
              if (result.report.chartState) {
                let chartStateObj = JSON.parse(result.report.chartState)
                if (chartStateObj) {
                  mode = chartStateObj.common.mode
                  filters = chartStateObj.common.filters
                  sorting = chartStateObj.common.sorting
                  result.report.showAlarms =
                    chartStateObj.settings && chartStateObj.settings.alarm
                  result.report.showSafeLimit =
                    chartStateObj.settings && chartStateObj.settings.safelimit
                  self.analyticsConfig.predictionTimings =
                    chartStateObj.predictionTimings || []
                  if (chartStateObj.heatMapOptions) {
                    if (result.report.analyticsType) {
                      self.analyticsConfig.analyticsType =
                        result.report.analyticsType
                    }
                    if (!self.heatMapOptions) {
                      self.heatMapOptions = {}
                      if (self.Colors) {
                        self.heatMapOptions.Colors = self.Colors
                      }
                    }
                    self.heatMapOptions.chosenColors =
                      chartStateObj.heatMapOptions.chosenColors
                    self.heatMapOptions.minValue =
                      chartStateObj.heatMapOptions.minValue
                    self.heatMapOptions.maxValue =
                      chartStateObj.heatMapOptions.maxValue
                    if (
                      typeof chartStateObj.heatMapOptions.showGrandParent !==
                      'undefined'
                    ) {
                      self.heatMapOptions.showGrandParent =
                        chartStateObj.heatMapOptions.showGrandParent
                    }
                    if (
                      typeof chartStateObj.heatMapOptions.reversePallete !==
                      'undefined'
                    ) {
                      self.heatMapOptions.reversePallete =
                        chartStateObj.heatMapOptions.reversePallete
                    }
                    if (
                      typeof chartStateObj.heatMapOptions.colorCriteria !==
                      'undefined'
                    ) {
                      self.heatMapOptions.colorCriteria =
                        chartStateObj.heatMapOptions.colorCriteria
                    }
                    if (
                      typeof chartStateObj.heatMapOptions.showColorScale !==
                      'undefined'
                    ) {
                      self.heatMapOptions.showColorScale =
                        chartStateObj.heatMapOptions.showColorScale
                    }
                    if (
                      typeof chartStateObj.heatMapOptions.treemapTextMode !==
                      'undefined'
                    ) {
                      self.heatMapOptions.treemapTextMode =
                        chartStateObj.heatMapOptions.treemapTextMode
                    }
                    if (
                      typeof chartStateObj.heatMapOptions.showWidgetLegends !==
                      'undefined'
                    ) {
                      self.heatMapOptions.showWidgetLegends =
                        chartStateObj.heatMapOptions.showWidgetLegends
                    }
                  }
                  if (chartStateObj?.tooltip?.compare_indicator) {
                    self.compare_indicator =
                      chartStateObj?.tooltip?.compare_indicator
                  }
                }
              }
              let xAggr = result.report.xAggr >= 0 ? result.report.xAggr : 0
              let dpList = result.report.dataPoints
              let baseLineNames
              if (result.report.baseLines) {
                baseLineNames = result.report.baseLines.map(
                  bl => bl.baseLine.name
                )
              }
              if (result.report.analyticsType === 7) {
                self.sizeDataPoint = dpList[0]
                self.colorDataPoint = dpList[1]
              }
              self.setAlias(dpList, baseLineNames)
              if (mode === 3) {
                for (let dp of dpList) {
                  let readingFieldId = dp.yAxis.fieldId
                  let yAggr = dp.yAxis.aggr
                  let parentList = dp.criteria.conditions['1'].value.split(',')
                  for (let parent of parentList) {
                    let parentId = parseInt(parent.trim())

                    dataPoints.push({
                      yAxis: {
                        fieldId: readingFieldId,
                        aggr: yAggr,
                      },
                      parentId: parentId,
                      type: 1,
                      aliases: dp.aliases,
                    })
                  }
                }
              } else {
                for (let dp of dpList) {
                  let readingFieldId = dp.yAxis.fieldId
                  let yAggr = dp.yAxis.aggr
                  let parentId = dp.criteria
                    ? parseInt(dp.criteria.conditions['1'].value.trim())
                    : null
                  let dynamicKpiId = null

                  if (!isEmpty(dp.dynamicKpi)) {
                    self.hideFilterDynamic = true
                    parentId = dp.dynamicKpi.parentId[0]
                    dynamicKpiId = dp.dynamicKpi.dynamicKpi
                  }
                  let categoryId = null
                  if (dp.metaData && dp.metaData.categoryId) {
                    categoryId = dp.metaData.categoryId
                  }

                  dataPoints.push({
                    yAxis: {
                      fieldId: readingFieldId,
                      aggr: yAggr,
                      unitStr: dp.yAxis.unitStr,
                      dataType: dp.yAxis.dataType,
                      label: dp.name,
                      dynamicKpi: dynamicKpiId,
                    },
                    buildingId:
                      dp.buildingId && dp.buildingId !== -1
                        ? dp.buildingId
                        : -1,
                    parentId: parentId,
                    kpiType: dp.kpiType,
                    moduleName: dp.moduleName,
                    categoryId: categoryId,
                    type: dp.type > 0 ? dp.type : 1,
                    aliases: dp.aliases,
                    name: dp.name,
                    prediction: dp.yAxis.predicted,
                    xDataPoint: dp.xDataPoint,
                    duplicateDataPoint: dp.duplicateDataPoint,
                    rightInclusive: dp.rightInclusive,
                  })
                }
              }

              if (
                result.report &&
                result.report.reportState &&
                result.report.reportState.reportResourceAliases &&
                result.report.reportState.reportResourceAliases.X ===
                  'mvproject'
              ) {
                mode = 'mv'
              }

              if (self.analyticsMode) {
                self.analyticsMode = mode
              }
              self.analyticsConfig.mode = mode
              if (filters) {
                self.analyticsConfig.filters = filters
                if (filters.filterState && self.reportFilterForm) {
                  self.setfilter = 1
                  self.reportFilterForm = self.$helpers.cloneObject(
                    filters.filterState
                  )
                }
              }
              if (sorting) {
                self.analyticsConfig.sorting = sorting
              }
              self.analyticsConfig.dateFilter =
                mobileConfig && mobileConfig.dateFilter
                  ? mobileConfig.dateFilter
                  : NewDateHelper.getDatePickerObject(
                      result.report.dateOperator,
                      result.report.dateValue
                    )
              self.analyticsConfig.period =
                mobileConfig && mobileConfig.period
                  ? mobileConfig.period
                  : xAggr
              if (result.report.baseLines && result.report.baseLines.length) {
                if (
                  result.report.baseLines[0].baseLine.rangeTypeEnum ===
                    'PREVIOUS_MONTH' ||
                  result.report.baseLines[0].baseLine.rangeTypeEnum ===
                    'ANY_MONTH' ||
                  result.report.baseLines[0].baseLine.rangeTypeEnum ===
                    'PREVIOUS_YEAR' ||
                  result.report.baseLines[0].baseLine.rangeTypeEnum ===
                    'ANY_YEAR'
                ) {
                  self.analyticsConfig.baseLine = [
                    result.report.baseLines[0].baseLineId,
                    result.report.baseLines[0].adjustType,
                  ]
                } else {
                  self.analyticsConfig.baseLine = [
                    result.report.baseLines[0].baseLineId,
                  ]
                }
                self.isShowCompareIndicator = true
              }
              self.analyticsConfig.savedReport = result.report
              if (self.analyticsConfig && self.analyticsConfig.format) {
                self.analyticsConfig.format =
                  result && result.report && result.report.reportState
                    ? result.report.reportState.hmAggr
                    : 'hours'
              }
              self.analyticsConfig.dataPoints = dataPoints

              for (let dp of result.report.dataPoints) {
                if (dp.xDataPoint === true) {
                  result.report['Readingdp'] = dp
                  break
                }
              }

              if (
                self.analyticsConfig &&
                result &&
                result.report &&
                result.report.Readingdp
              ) {
                self.analyticsMode = 'reading'
                self.analyticsConfig.dimensionDataPoint = parseInt(
                  Object.keys(self.analyticsConfig.dataPoints).find(
                    key =>
                      self.analyticsConfig.dataPoints[key].xDataPoint === true
                  )
                )
              }

              if (self.analyticsConfig.mode === 11) {
                self.analyticsMode = self.analyticsConfig.mode
                self.analyticsConfig.dimensionDataPoint = 0
              }

              if (result.report.transformWorkflow) {
                self.analyticsConfig.transformWorkflow = self.filterWorkflow(
                  result.report.transformWorkflow
                )
              }
              if (
                result.report.reportState &&
                result.report.reportState.regressionConfig &&
                result.report.reportState.regressionConfig.length !== 0
              ) {
                self.analyticsConfig['regressionConfig'] =
                  result.report.reportState.regressionConfig
                self.analyticsConfig['regressionType'] =
                  result.report.reportState.regressionType
              }
              if (
                result.report.reportState &&
                result.report.reportState.groupByTimeAggr
              ) {
                self.analyticsConfig.groupByTimeAggr =
                  result.report.reportState.groupByTimeAggr
              }
              if (
                result.report.reportState &&
                result.report.reportState.scatterConfig
              ) {
                let savedconfig = JSON.parse(
                  result.report.reportState.scatterConfig
                )
                self.analyticsConfig.scatter = savedconfig.properties
                self.analyticsConfig.scatterConfig = savedconfig.datapoints
                if (self.$refs['leftpanel']) {
                  self.$refs['leftpanel'].loadFromReport()
                }
              }

              if (result.report.type === 4) {
                let template = null
                if (result.report.template) {
                  template = JSON.parse(result.report.template)
                } else {
                  template = JSON.parse(
                    JSON.stringify(result.report.reportTemplate)
                  )
                }
                if (!isEmpty(template.criteria)) {
                  delete template.criteria.regEx
                  for (let key in template.criteria.conditions) {
                    delete template.criteria.conditions[key].operator
                  }
                }
                template.chooseValues = []
                self.$set(self.analyticsConfig, 'template', template)
              }
              if (result.report.timeFilter) {
                if (typeof self.analyticsConfig.reportFilter !== 'undefined') {
                  self.analyticsConfig.reportFilter.timeFilter = JSON.parse(
                    result.report.timeFilter
                  )
                }
              }
              if (result.report.dataFilter) {
                if (typeof self.analyticsConfig.reportFilter !== 'undefined') {
                  self.analyticsConfig.reportFilter.dataFilter = JSON.parse(
                    result.report.dataFilter
                  )
                }
              }
            }
          })
      }
    },
    filterWorkflow(transformWorkflow) {
      let expressions = transformWorkflow.expressions
      let parameters = transformWorkflow.parameters
      let keys = ['expr', 'name', 'typeString', 'defaultFunctionContext']
      for (let expr of expressions) {
        for (let subExpr of expr.expressions) {
          for (let key in subExpr) {
            if (!keys.includes(key)) {
              delete subExpr[key]
            }
          }
        }
      }

      for (let p of parameters) {
        for (let key in p) {
          if (!keys.includes(key)) {
            delete p[key]
          }
        }
      }
      let temp = {}
      temp['expressions'] = expressions
      temp['parameters'] = parameters
      return temp
    },
    setAlias(dataPoints, baselines) {
      let dpAlias = { lastAlias: 64, lastNum: 0 }
      let regex = /[a-z]+|\d+/gi
      dataPoints.forEach(dp => {
        if (dp.aliases) {
          for (let key in dp.aliases) {
            let alias = dp.aliases[key]
            if (alias.length > 1) {
              let num = alias.match(regex)[1]
              dpAlias.lastNum = num > dpAlias.lastNum ? num : dpAlias.lastNum
            } else {
              dpAlias.lastAlias =
                alias.charCodeAt(0) > dpAlias.lastAlias
                  ? alias.charCodeAt(0)
                  : dpAlias.lastAlias
            }
          }
        } else {
          dp.aliases = {}
        }
      })

      dataPoints.forEach(dp => {
        if (!dp.aliases.actual) {
          dp.aliases.actual = this.$helpers.getNextAlias(dpAlias)
        }
        if (baselines) {
          baselines.forEach(baseLineName => {
            if (!dp.aliases[baseLineName] && (!dp.type || dp.type !== 2)) {
              dp.aliases[baseLineName] = this.$helpers.getNextAlias(dpAlias)
            }
          })
        }
      })
    },

    getConfigDataPointFromOptionDP(config, optionDataPoint, fetchIndex) {
      let idx = config.dataPoints.findIndex(dp =>
        Object.keys(dp.aliases).some(
          key => dp.aliases[key] === optionDataPoint.alias
        )
      )
      return fetchIndex ? idx : config.dataPoints[idx]
    },

    checkAndDeletePointsRel(options, alias, confirmDelete) {
      let canDelete = true
      if (options.derivations) {
        for (let deralias in options.derivations) {
          if (deralias !== alias) {
            let der = options.derivations[deralias]
            if (der.usedAliases.includes(alias)) {
              if (!confirmDelete) {
                canDelete = false
                return
              }
            }
          }
        }
      }

      if (canDelete) {
        // Delete everything based on alias
        if (options.derivations) {
          delete options.derivations[alias]
        }
        if (options.widgetLegend.variances) {
          delete options.widgetLegend.variances[alias]
        }
      }
      return canDelete
    },

    removeOptionPoints(options, pointsToRemove) {
      if (pointsToRemove.length) {
        options.dataPoints = options.dataPoints.filter(dp => {
          if (dp.type === 'group') {
            dp.children = dp.children.filter(
              chdp => !pointsToRemove.includes(chdp.alias)
            )
            return dp.children.length > 0
          } else return !pointsToRemove.includes(dp.alias)
        })
      }
    },

    removeDataPoint(options, config, parent, parentKey, child, childKey) {
      if (parentKey === 'TimeFilter' || parentKey.includes('Cri')) {
        this.$emit('removeFilter', parentKey)
      } else {
        if (parent.pointType === 3) {
          //trendLine
          let data = options.dataPoints.find(
            dataPoint => dataPoint.alias === parent.alias
          )
          options.dataPoints.splice(options.dataPoints.indexOf(data), 1)
        } else if (config) {
          let dataPoints = this.$helpers.cloneObject(config.dataPoints)
          if (parent && child && childKey) {
            let data = dataPoints.find(function(rt) {
              if (
                rt.parentId === child.parentId &&
                rt.yAxis.fieldId === child.fieldId &&
                rt.aliases.actual === child.alias
              ) {
                return rt
              }
            })
            if (
              config &&
              config.predictionTimings &&
              data &&
              data.predictedTime
            ) {
              config.predictionTimings.splice(
                config.predictionTimings.indexOf(data.predictedTime),
                1
              )
            }
            if (options && options.derivations) {
              let values = []
              for (let alias in options.derivations) {
                let aConfig = options.derivations[alias]
                if (
                  aConfig.usedAliases.includes(childKey) &&
                  (typeof aConfig.isDeleted === 'undefined' ||
                    aConfig.isDeleted === false)
                ) {
                  values.push(aConfig)
                }
              }
              if (values.length !== 0) {
                let singleOrMultipleText =
                  values.length === 1
                    ? ' a derivation '
                    : ' multiple derivations '
                this.$message.error(
                  'Selected point is used in' +
                    singleOrMultipleText +
                    '. Delete appropriate derivations first'
                )
              } else {
                if (
                  options &&
                  options.derivations &&
                  typeof options.derivations[parentKey] !== 'undefined'
                ) {
                  options.derivations[parentKey]['isDeleted'] = true
                }
                config.dataPoints.splice(dataPoints.indexOf(data), 1)
              }
            } else {
              config.dataPoints.splice(dataPoints.indexOf(data), 1)
            }

            // this.setDateFilter(config.predictionTimings)
          } else if (parent && parentKey) {
            if (parent.children && parent.children.length !== 0) {
              let allDataPoints = [...config.dataPoints]
              let toBeDeletedIndexes = []
              for (let child of parent.children) {
                let configDp = allDataPoints.filter(
                  dp => dp.aliases.actual === child.alias
                )
                if (configDp.length !== 0) {
                  if (options && options.derivations) {
                    let dAlias = Object.keys(options.derivations).filter(a =>
                      options.derivations[a].usedAliases.includes(child.alias)
                    )
                    if (dAlias.length !== 0) {
                      this.$message.error(
                        'This range point is used in a derivation. Delete appropriate derivations first'
                      )
                      return false
                    } else {
                      let index = allDataPoints.indexOf(configDp[0])
                      allDataPoints.splice(index, 1)
                      // toBeDeletedIndexes.push(config.dataPoints.indexOf(configDp[0]))
                    }
                  } else {
                    let index = allDataPoints.indexOf(configDp[0])
                    allDataPoints.splice(index, 1)
                    // toBeDeletedIndexes.push(config.dataPoints.indexOf(configDp[0]))
                  }
                }
              }
              config.dataPoints = allDataPoints
            } else {
              let data = dataPoints.find(function(rt) {
                if (
                  rt.parentId === parent.parentId &&
                  rt.yAxis.fieldId === parent.fieldId &&
                  rt.aliases.actual === parent.alias
                ) {
                  return rt
                }
              })
              if (
                config &&
                config.predictionTimings &&
                data &&
                data.predictedTime
              ) {
                config.predictionTimings.splice(
                  config.predictionTimings.indexOf(data.predictedTime),
                  1
                )
              }
              if (options && options.derivations) {
                let values = []
                for (let alias in options.derivations) {
                  let aConfig = options.derivations[alias]
                  if (
                    aConfig.usedAliases.includes(parentKey) &&
                    (typeof aConfig.isDeleted === 'undefined' ||
                      aConfig.isDeleted === false)
                  ) {
                    values.push(aConfig)
                  }
                }
                if (values.length !== 0) {
                  let singleOrMultipleText =
                    values.length === 1
                      ? ' a derivation '
                      : ' multiple derivations '
                  this.$message.error(
                    'Selected point is used in' +
                      singleOrMultipleText +
                      '. Delete appropriate derivations first'
                  )
                } else {
                  if (
                    options &&
                    options.derivations &&
                    typeof options.derivations[parentKey] !== 'undefined'
                  ) {
                    options.derivations[parentKey]['isDeleted'] = true
                  }
                  config.dataPoints.splice(dataPoints.indexOf(data), 1)
                }
              } else {
                config.dataPoints.splice(dataPoints.indexOf(data), 1)
              }
            }
          }
        }
      }
    },
    // setDateFilter (time) {
    //   if (time && time.length) {
    //     let dateFilter = {}
    //     time = time.sort()
    //     if (time.length === 1 && time[0] < this.today) {
    //       dateFilter = NewDateHelper.getDatePickerObject(20, [this.$options.filters.startOfDay(time[0]), this.$options.filters.endOfDay(time[0])])
    //        this.config.dateFilter = dateFilter
    //     }
    //     else if (time[0] < this.today) {
    //       dateFilter = NewDateHelper.getDatePickerObject(20, [this.$options.filters.startOfDay(time[0]), this.$options.filters.endOfDay(time[time.length - 1])])
    //        this.config.dateFilter = dateFilter
    //        this.config.period = 20
    //     }
    //   }
    // },
    addPredictionFields(config) {
      // this.setDateFilter(config.predictionTimings)
      let predictionPoints = config.dataPoints.filter(dp => dp.prediction)
      if (!predictionPoints.length) {
        config.predictionTimings = []
        if (config.dataPoints.length <= 1 && this.analyticsMode === 'reading') {
          this.noChartState = true
          config.dimensionDataPoint = null
        }
        return
      }
      if (!config.predictionTimings.length) {
        config.predictionTimings = [
          this.$options.filters.startOfHour(new Date()),
        ]
      }
      let newPoints = []
      predictionPoints.forEach(point => {
        config.predictionTimings.forEach(time => {
          if (!point.predictedTime) {
            point.predictedTime = time
          } else if (
            !predictionPoints.some(
              dp =>
                point.parentId === dp.parentId &&
                point.yAxis.fieldId === dp.yAxis.fieldId &&
                dp.predictedTime === time
            )
          ) {
            let newPoint = {
              parentId: point.parentId,
              yAxis: {
                fieldId: point.yAxis.fieldId,
                aggr: point.yAxis.aggr,
              },
              prediction: true,
              predictedTime: time,
            }
            if (
              !newPoints.some(
                dp =>
                  dp.parentId === newPoint.parentId &&
                  dp.yAxis.fieldId === newPoint.yAxis.fieldId &&
                  dp.predictedTime === newPoint.predictedTime
              )
            ) {
              newPoints.push(newPoint)
            }
          }
        })
      })
      if (
        config.dataPoints.length <= 1 &&
        this.analyticsMode === 'reading' &&
        !newPoints.length
      ) {
        this.noChartState = true
        config.dimensionDataPoint = null
      }
      if (newPoints.length) {
        config.dataPoints.push(...newPoints)
      }
    },

    resetPredictionTimings(config) {
      if (
        config &&
        config.predictionTimings &&
        config.predictionTimings.length
      ) {
        let predictionFields = config.dataPoints.filter(dp => dp.prediction)
        if (!predictionFields.length) {
          config.predictionTimings = []
        }
      }
    },
    pointShowRule(dateRange, rule) {
      if (dateRange && rule) {
        if (
          dateRange.offset <= rule.offset &&
          dateRange.operationOn === rule.operationOn &&
          dateRange.period === rule.period
        ) {
          return {
            show: rule.show,
          }
        } else {
          return {
            show: !rule.show,
          }
        }
      } else {
        return {
          show: false,
        }
      }
    },
    toggleDonutLabel(xValueMode) {
      let totalValList = []
      if (this.options && this.options.xColorMap) {
        for (let datakey in this.options.xColorMap) {
          if (!this.hideLegend.includes(datakey)) {
            let dataList = this.chart.data(datakey)
            let val = dataList[0].values[0].value
            if (val) {
              totalValList.push(val)
            }
          }
        }
      }

      let unit =
        xValueMode && this.options && this.options.dataPoints.length
          ? this.options.dataPoints[0].unitStr
          : null

      let fieldMap = {
        _sum: d3.sum(totalValList),
        _min: d3.min(totalValList),
        _max: d3.max(totalValList),
        _avg: d3.mean(totalValList),
      }
      if (
        this.options &&
        Object.keys(fieldMap).includes(
          this.options.donut.centerText.primaryText
        )
      ) {
        let fieldVal = fieldMap[this.options.donut.centerText.primaryText]
        let primaryUnit = this.options.donut.centerText.primaryUnit
          ? this.options.donut.centerText.primaryUnit
          : unit
        if (this.options.donut.centerText.primaryRoundOff)
          fieldVal = parseInt(fieldVal)

        this.chart.$.main
          .select('.bb-chart-arcs-title tspan:nth-child(1)')
          .text(`${this.formatDonutLabel(fieldVal, primaryUnit)}`)
      }
      if (
        this.options &&
        Object.keys(fieldMap).includes(
          this.options.donut.centerText.secondaryText
        )
      ) {
        let fieldVal = fieldMap[this.options.donut.centerText.secondaryText]
        let secondaryUnit = this.options.donut.centerText.primaryUnit
          ? this.options.donut.centerText.secondaryUnit
          : unit
        if (this.options.donut.centerText.secondaryRoundOff)
          fieldVal = parseInt(fieldVal)

        this.chart.$.main
          .select('.bb-chart-arcs-title tspan:nth-child(4)')
          .text(`${this.formatDonutLabel(fieldVal, secondaryUnit)}`)
      }
    },
    formatDonutLabel(val, unit) {
      if (unit) {
        val = this.formatDuration(val, unit)
      } else if (Number.isInteger(val)) {
        val = d3.format(',')(val)
      } else {
        val = d3.format(',.2f')(val)
      }
      return val
    },
    formatDuration(val, unit) {
      if (unit === 'hours') {
        if (val > 24) {
          return `${this.formatNumber(val / 24)} days`
        } else {
          return `${this.formatNumber(val)} hours`
        }
      } else if (unit === 'minutes') {
        if (val > 1440) {
          return `${this.formatNumber(val / 1440)} days`
        } else if (val > 60) {
          return `${this.formatNumber(val / 60)} hours`
        } else {
          return `${val} minutes`
        }
      } else if (unit === 'seconds') {
        if (val > 86400) {
          return `${this.formatNumber(val / 86400)} days`
        } else if (val > 3600) {
          return `${this.formatNumber(val / 3600)} hours`
        } else if (val > 60) {
          return `${this.formatNumber(val / 60)} minutes`
        } else {
          return `${this.formatNumber(val)} seconds`
        }
      } else if (['$', '€', '₹'].includes(unit)) {
        return `${unit} ${this.formatNumber(val)}`
      } else {
        return `${this.formatNumber(val)} ${unit}`
      }
    },
    formatNumber(fieldVal) {
      if (Number.isInteger(fieldVal)) {
        fieldVal = d3.format(',')(fieldVal)
      } else {
        fieldVal = d3.format(',.2f')(fieldVal)
      }
      return fieldVal
    },
  },
}
