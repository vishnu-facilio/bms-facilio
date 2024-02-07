import * as d3 from 'd3'

export default {
  computeRegressionAlias(conf) {
    let groupAlias = ''

    for (let x of conf['xAxis']) {
      groupAlias = groupAlias + x.alias
      groupAlias = groupAlias + '_'
    }

    groupAlias = groupAlias + conf['yAxis'].alias
    groupAlias = groupAlias + 'regr'
    return groupAlias
  },
  getAllDataPoints(option) {
    let dataPoints = []
    for (let point of option.dataPoints) {
      if (point.type === 'datapoint') {
        let isAlreadyExisting = dataPoints.find(dP => dP.alias === point.alias)
        if (!isAlreadyExisting) {
          dataPoints.push(point)
        }
      } else if (point.type === 'group') {
        for (let child of point.children) {
          let isAlreadyExisting = dataPoints.find(
            dP => dP.alias === child.alias
          )
          if (!isAlreadyExisting) {
            dataPoints.push(child)
          }
        }
      }
    }
    return dataPoints
  },
  getDataPoint(fieldId, parentId, options) {
    for (let dp of options.dataPoints) {
      if (dp.type === 'dataPoint') {
        if (dp.fieldId === fieldId && dp.parentId === parentId) {
          return dp
        }
      } else if (dp.type === 'group') {
        for (let child of dp.children) {
          if (child.fieldId === fieldId && child.parentId === parentId) {
            return child
          }
        }
      }
    }
  },
  pointTypes() {
    let types = {}
    types['5'] = 'regressionPoint'
    return types
  },
  contextNames() {
    let contextNames = {}
    contextNames['REGRESSION'] = 'regression'
    contextNames['LINEAR'] = 'linear'
    contextNames['SINGLE'] = 'single'
    contextNames['MULTIPLE'] = 'multi'
    return contextNames
  },
  dataTypes() {
    let dataTypes = {}
    dataTypes['DATAPOINT'] = 'datapoint'
    dataTypes['GROUP'] = 'group'
    dataTypes['RANGEGROUP'] = 'rangeGroup'
    dataTypes['SYSTEMGROUP'] = 'systemGroup'
    dataTypes['REGRESSION'] = 'regression'
    return dataTypes
  },

  regressionTypes() {
    let regressionTypes = {}
    regressionTypes['SINGLE'] = 'single'
    regressionTypes['MULTIPLE'] = 'multiple'
    return regressionTypes
  },
  regressionMetrics() {
    let regressionMetricKeys = [
      {
        name: 'rsquared',
        displayName: 'R Square',
      },
      {
        name: 'adjustedrSquared',
        displayName: 'Adjusted R Square',
      },
      {
        name: 'standardError',
        displayName: 'Standard Error',
      },
      {
        name: 'observations',
        displayName: 'Observations',
      },
    ]
    return regressionMetricKeys
  },

  anovaResultKeys() {
    let anovaResultKeys = [
      {
        name: 'degreeOfFreedom',
        displayName: 'DF',
      },
      {
        name: 'sumOfSquare',
        displayName: 'SS',
      },
      {
        name: 'meanSumOfSquare',
        displayName: 'MS',
      },
      {
        name: 'fStat',
        displayName: 'F',
      },
    ]
    return anovaResultKeys
  },
  regressionStatistics() {
    let regressionStatKeys = [
      {
        name: 'coefficient',
        displayName: 'COEFFICIENTS',
      },
      {
        name: 'standardError',
        displayName: 'STANDARD ERROR',
      },
      {
        name: 'tValue',
        displayName: 'T VALUE',
      },
      {
        name: 'pValue',
        displayName: 'P VALUE',
      },
    ]
    return regressionStatKeys
  },

  evaluateLinearData(coefficients, modelData, type) {
    if (type === 'linear') {
      let data = []
      // let newXSet = new Set()
      if (Array.isArray(modelData)) {
        for (let d of modelData) {
          let y = coefficients.a * d + coefficients.b
          data.push(y)
          // if(!newXSet.has(d)){
          //   newXSet.add(d)

          // }
        }
        return data
      } else {
        data = coefficients.a * modelData + coefficients.b
        return data
      }
    }
  },

  getEquationString(coefficients, rsquared, type) {
    switch (type) {
      case 'linear': {
        return (
          'y=' +
          d3.format(',g')(coefficients.a) +
          'x+' +
          d3.format(',g')(coefficients.b) +
          ' ' +
          '(R2' +
          '= ' +
          d3.format(',g')(rsquared) +
          ')'
        )
      }
    }
  },
  getPeriod(period) {
    switch (period) {
      case 'hourly': {
        return 8
      }
      case 'daily': {
        return 1
      }
      case 'weekly': {
        return 2
      }
      case 'monthly': {
        return 3
      }
      default: {
        return null
      }
    }
  },
  getReadingFieldForEnpi() {
    let readingField = {
      safeLimitId: -1,
      raiseSafeLimitAlarm: false,
      displayName: '',
      dataType: 1,
      lesserThan: null,
      greaterThan: null,
      betweenTo: null,
      betweenFrom: null,
      safeLimitPattern: 'none',
      safeLimitSeverity: 'Minor',
    }
    return readingField
  },
  getAggregateString(aggr) {
    if (aggr) {
      switch (aggr) {
        case 2: {
          return 'avg'
        }
        case 3: {
          return 'sum'
        }
        case 4: {
          return 'min'
        }
        case 5: {
          return 'max'
        }
      }
    }
  },
  getStaticCriteria(parentId) {
    let criteria = {}
    let conditions = {
      1: {
        fieldName: 'parentId',
        operatorId: 9,
        sequence: '1',
        value: parentId,
      },
      2: {
        fieldName: 'ttime',
        operatorId: 20,
        sequence: '2',
        value: '${startTime}, ${endTime}',
      },
    }

    criteria['pattern'] = '(1 and 2)'
    criteria['conditions'] = conditions
    return criteria
  },
  getWorkFlowForSingleRegression(reportObject, regressionObject) {
    let workFlow = {}
    let expressions = []
    let parameters = []
    if (regressionObject.regressionType === this.contextNames.LINEAR) {
      let xDataPoint = regressionObject.children.find(dp => dp.axes === 'x')
      let regressionPoint = regressionObject.children.find(
        d => d.alias === regressionObject.key
      )
      let coefficients = regressionPoint.coefficients

      expressions.push({
        name: 'A',
        constant: coefficients[xDataPoint.alias] + '',
      })
      expressions.push({
        name: 'B',
        aggregateString: this.getAggregateString(xDataPoint.aggr),
        fieldName: xDataPoint.fieldName,
        moduleName: xDataPoint.moduleName,
        criteria: this.getStaticCriteria(xDataPoint.parentId),
        conditionSeqVsBaselineId: {},
      })
      expressions.push({
        name: 'C',
        constant: coefficients.constant + '',
      })

      parameters = [
        {
          name: 'startTime',
          typeString: 'Number',
        },
        {
          name: 'endTime',
          typeString: 'Number',
        },
      ]
      workFlow['resultEvaluator'] = '(A*B+C)'
    }

    workFlow['expressions'] = expressions
    workFlow['parameters'] = parameters

    return workFlow
  },
  getNewWorkFlowForEnpi(resultObject, regressionObject) {
    if (
      resultObject.options ||
      (resultObject.regressionConfig &&
        resultObject.regressionType === this.regressionTypes().SINGLE)
    ) {
      return this.getWorkFlowForSingleRegression(resultObject, regressionObject)
    } else {
      return this.getWorkFlowForRegression(
        resultObject,
        regressionObject.aliases.actual
      )
    }
  },
  provideStringOperators() {
    let operators = {
      SUM: '+',
      DIFF: '-',
      MUL: '*',
      DIV: '/',
    }
    return operators
  },
  getWorkFlowForRegression(resultObject, choosenAlias) {
    let workFlow = {}
    let expressions = []
    let expressionPattern = ''
    let regressionConfig = resultObject.regressionConfig.filter(
      conf => conf.groupAlias === choosenAlias
    )
    let coefficientMap =
      resultObject.reportData.regressionResult[choosenAlias].coefficientMap
    let currentCharacterCode = 65
    let parameters = [
      {
        name: 'startTime',
        typeString: 'Number',
      },
      {
        name: 'endTime',
        typeString: 'Number',
      },
    ]

    workFlow['parameters'] = parameters

    if (regressionConfig.length !== 0) {
      expressionPattern = ''
      for (let key in coefficientMap) {
        if (key != 'constant') {
          let constant = {
            constant: coefficientMap[key] + '',
            name: String.fromCharCode(currentCharacterCode),
          }

          expressions.push(constant)

          currentCharacterCode = currentCharacterCode + 1

          let dataPoint = resultObject.report.dataPoints.find(
            dp => dp.aliases.actual === key
          )
          if (dataPoint) {
            let temp = {}
            temp['name'] = String.fromCharCode(currentCharacterCode)

            currentCharacterCode = currentCharacterCode + 1
            temp['fieldName'] = dataPoint.yAxis.field.name
            temp['moduleName'] = dataPoint.yAxis.field.module.name
            ;(temp['criteria'] = this.getStaticCriteria(
              dataPoint.metaData.parentIds[0]
            )),
              (temp['aggregateString'] = this.getAggregateString(
                dataPoint.yAxis.aggr
              ))
            temp['conditionSeqVsBaselineId'] = {}

            expressions.push(temp)
            let overOperation =
              coefficientMap[key] < 0
                ? this.provideStringOperators().DIFF
                : this.provideStringOperators().SUM
            expressionPattern = this.populateResultEvaluator(
              expressionPattern,
              [constant, temp],
              overOperation,
              [this.provideStringOperators().MUL]
            )
          }
        } else {
          let overOperation =
            coefficientMap[key] < 0
              ? this.provideStringOperators().DIFF
              : this.provideStringOperators().SUM
          let constant = {
            constant: coefficientMap[key] + '',
            name: String.fromCharCode(currentCharacterCode),
          }
          expressions.push(constant)
          currentCharacterCode = currentCharacterCode + 1
          expressionPattern = this.populateResultEvaluator(
            expressionPattern,
            [constant],
            overOperation,
            null
          )
        }
      }

      // console.log('Expressions')
      // console.log(expressions)

      // console.log('Expression Pattern')
      // console.log(expressionPattern)

      workFlow['expressions'] = expressions

      workFlow['resultEvaluator'] = expressionPattern

      return workFlow
    }
  },
  populateResultEvaluator(
    expressionPattern,
    variables,
    overOperation,
    inOperation
  ) {
    if (variables.length === 1) {
      if (expressionPattern !== '') {
        expressionPattern =
          expressionPattern + overOperation + '(' + variables[0].name + ')'
      } else {
        expressionPattern = expressionPattern + '(' + variables[0].name + ')'
      }
    } else {
      let subExpression = ''
      for (let i = 0; i < variables.length - 1; i++) {
        subExpression = subExpression + '(' + variables[i].name + inOperation[i]
      }
      subExpression = subExpression + variables[variables.length - 1].name + ')'

      if (expressionPattern !== '') {
        expressionPattern = expressionPattern + overOperation + subExpression
      } else {
        expressionPattern = expressionPattern + subExpression
      }
    }
    return expressionPattern
  },
  prepareNewEnpiObject(reportObject, choosenAlias, commons, datePeriod) {
    let newEnpi = {}
    for (let alias of choosenAlias) {
      let regressionObject = reportObject.options
        ? reportObject.options.dataPoints.filter(
            dp => dp.type === 'group' && dp.key && dp.key === alias
          )
        : reportObject.report.dataPoints.filter(
            dp => dp.aliases.actual === alias
          )
      if (regressionObject.length) {
        regressionObject = regressionObject[0]
        // let xDataPoint = regressionObject.children.find((dp) => dp.axes === 'x')
        newEnpi['name'] = regressionObject.label
          ? regressionObject.label
          : regressionObject.name
        newEnpi['description'] = ''
        newEnpi['triggerType'] = 1
        newEnpi['formulaFieldType'] = 1
        newEnpi['resourceType'] = 1
        newEnpi['resultDataType'] = 3
        newEnpi['readingField'] = this.getReadingFieldForEnpi()
        newEnpi['interval'] = 60

        if (reportObject.options) {
          newEnpi['matchedResources'] = [
            {
              name: reportObject.options.common.buildingName,
            },
          ]
          newEnpi['resourceId'] = reportObject.options.common.buildingIds[0]
        } else {
          newEnpi['matchedResources'] = [
            {
              name: commons.buildingName,
            },
          ]
          newEnpi['resourceId'] = parseInt(commons.buildingIds[0])
        }

        newEnpi['workflow'] = this.getNewWorkFlowForEnpi(
          reportObject,
          regressionObject
        )

        let period = null
        if (reportObject.dateRange && reportObject.dateRange.period) {
          period = this.getPeriod(reportObject.dateRange.period)
        } else {
          period = this.getPeriod(datePeriod)
        }

        if (period) {
          newEnpi['frequency'] = period
        } else {
          newEnpi['frequency'] = 8
        }
      }
    }

    // console.log('Constructed New Enpi config')
    // console.log(newEnpi)

    return newEnpi
  },
}
