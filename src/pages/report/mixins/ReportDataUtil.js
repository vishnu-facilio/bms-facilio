import * as d3 from 'd3'
import analyticsModels from 'src/pages/report/mixins/FNewAnalyticsModelHelper'
import NewDataFormatHelper from 'src/pages/report/mixins/NewDataFormatHelper'
import moment, { min } from 'moment'
import Vue from 'vue'

const MomentRange = require('moment-range')

export default {
  mixins: [NewDataFormatHelper],
  methods: {
    transformData(reportObj, data) {
      let finalData = new Array(data[Object.keys(data)[0]].length)
      let dataPoints = this.getAllDataPoints(reportObj.options)
      for (let point of dataPoints) {
        if (
          point.type &&
          point.pointType &&
          point.type === this.dataTypes().DATAPOINT &&
          point.pointType === this.contextNames().REGRESSION
        ) {
          let regressionGroup = reportObj.options.dataPoints.find(
            dp => dp.key === point.alias
          )
          let xDataPoint = null
          if (regressionGroup) {
            xDataPoint = regressionGroup.children.find(d => d.axes === 'x')
          }
          if (xDataPoint) {
            for (
              let index2 = 0;
              index2 < data[xDataPoint.alias].length;
              index2++
            ) {
              let d = data[xDataPoint.alias][index2]
              let value = d3.format(',.4f')(
                point.evaluator(
                  point.coefficients,
                  d,
                  this.contextNames().LINEAR
                )
              )
              if (typeof finalData[index2] === 'object') {
                finalData[index2][point.alias] = value
              } else {
                let temp = {}
                temp[point.alias] = value
                finalData[index2] = temp
              }
            }
          }
        } else {
          for (let index = 0; index < data[point.alias].length; index++) {
            let d = data[point.alias][index]
            if (typeof finalData[index] === 'object') {
              finalData[index][point.alias] = d
            } else {
              let temp = {}
              temp[point.alias] = d
              finalData[index] = temp
            }
          }
        }
      }
      // console.log('TRANSFORMED DATA')
      // console.log(finalData)
      return finalData
    },

    cleanArray(...args) {
      // args is a list of arrays
      let maxArraySize = args[0].length
      let removeIndex = new Set()
      let finalArray = []

      for (let j = 0; j < args.length; j++) {
        let currentArray = args[j]
        for (let i = 0; i < maxArraySize; i++) {
          if (
            typeof currentArray[i] === 'undefined' ||
            currentArray[i] === null
          ) {
            removeIndex.add(i)
          } else {
            currentArray[i] = parseFloat(currentArray[i])
          }
        }
      }
      // console.log('Remove indexes')
      // console.log(removeIndex)
      for (let k = 0; k < args.length; k++) {
        let currentArray = args[k]
        let newArray = []
        for (let index = 0; index < currentArray.length; index++) {
          if (!removeIndex.has(index)) {
            newArray.push(currentArray[index])
          }
        }
        finalArray.push(newArray)
      }
      // console.log('Arrays cleaned')
      // console.log(finalArray)
      return finalArray
    },
    sumOfSquares(data, yPoint, modelPoint) {
      let sqauresOfDifference = []
      let xLength = data['x'].length
      for (let index = 0; index < xLength; index++) {
        let yData =
          data[yPoint.alias ? yPoint.alias : yPoint.aliases.actual][index]
        let y2Data = modelPoint.evaluator(
          modelPoint.coefficients,
          data['x'][index],
          this.contextNames().LINEAR
        )
        if (yData && y2Data) {
          sqauresOfDifference.push(Math.pow(yData - y2Data, 2))
        }
      }

      let sumOfSquares = this.getSum(sqauresOfDifference)
      return sumOfSquares
    },
    computeMeanSquaredError(data, yPoint, modelPoint) {
      let sumOfSquares = this.sumOfSquares(data, yPoint, modelPoint)
      let average = sumOfSquares / data.x.length

      return average
    },
    computeRsquared(data, yPoint, modelPoint) {
      let meanSquaredError = this.sumOfSquares(data, yPoint, modelPoint)
      let yAlias = yPoint.alias ? yPoint.alias : yPoint.aliases.actual
      let yMean = this.getArithematicMean(data[yAlias])
      let SSyy = []
      for (let index = 0; index < data[yAlias].length; index++) {
        SSyy.push(Math.pow(data[yAlias][index] - yMean, 2))
      }
      let ssyy = this.getSum(SSyy)
      return 1 - meanSquaredError / ssyy
    },

    getLinearFit(idp, dep) {
      // idp x
      // dep y
      // console.log('ARRAYS')
      // console.log(idp)
      // console.log(dep)
      let linearCoefficient = {}
      let idpSum = this.getSum(idp)
      let depSum = this.getSum(dep)
      let idpMean = this.getArithematicMean(idp)
      let depMean = this.getArithematicMean(dep)
      let sum_of_products = this.getSumOfProducts(idp, dep)
      let idp_variance = this.getVariance(idp)
      linearCoefficient['a'] =
        (sum_of_products - (idpSum * depSum) / idp.length) / idp_variance
      linearCoefficient['b'] = depMean - linearCoefficient.a * idpMean

      console.log('LINEAR COEFFICIENTS')
      console.log(linearCoefficient)
      return linearCoefficient
    },
    getVariance(data) {
      let squares = []
      for (let i = 0, l = data.length; i < l; i++) {
        squares[i] = Math.pow(data[i], 2)
      }

      let sum_of_squares = this.getSum(squares)
      let mean = this.getSum(data)
      let square_of_mean = Math.pow(mean, 2)
      let variance = sum_of_squares - square_of_mean / data.length

      return variance
    },
    getSum(data) {
      let sum = 0
      for (let d of data) {
        sum = sum + d
      }
      return sum
    },
    getSumOfProducts(data1, data2) {
      let total = 0
      try {
        if (data1.length === data2.length) {
          for (let i = 0; i < data1.length; i++) {
            total = total + data1[i] * data2[i]
          }
          return total
        } else {
          throw new Error('Error!! Mean of products cannot be computed')
        }
      } catch (e) {
        throw new Error('Error!! Mean of products cannot be computed')
      }
    },
    getArithematicMean(data) {
      let total = data.reduce((acc, current) => acc + current)
      return total / data.length
    },
    sortDataByX(options, data) {
      let finalData = {}
      let dataStructure = {}
      let xDataPoint = analyticsModels
        .getAllDataPoints(options)
        .find(dataPoint => dataPoint.axes === 'x')
      let finalDataPoints = JSON.parse(
        JSON.stringify(analyticsModels.getAllDataPoints(options))
      )
      let xDataPointIndex = analyticsModels
        .getAllDataPoints(options)
        .indexOf(xDataPoint)
      let xCount = {}
      let count = 0
      if (xDataPoint) {
        finalDataPoints.splice(xDataPointIndex, 1)
        for (let i = 0; i < data[xDataPoint.alias].length; i++) {
          let d = data[xDataPoint.alias][i]
          if (options.timeValues) {
            if (dataStructure[d]) {
              dataStructure[d].timeValues.push(options.timeValues[i])
            } else {
              dataStructure[d] = {}
              dataStructure[d].timeValues = []
              dataStructure[d].timeValues.push(options.timeValues[i])
            }
          }
          if (d) {
            if (xCount[d]) {
              xCount[d]++
              count++
            } else {
              xCount[d] = 1
              count !== 0 ? count++ : (count = 1)
            }
            for (let dataPoint of finalDataPoints) {
              if (data[dataPoint.alias]) {
                if (dataStructure[d]) {
                  if (dataStructure[d][dataPoint.alias]) {
                    dataStructure[d][dataPoint.alias].push(
                      data[dataPoint.alias][i]
                    )
                  } else {
                    dataStructure[d][dataPoint.alias] = []
                    dataStructure[d][dataPoint.alias].push(
                      data[dataPoint.alias][i]
                    )
                  }
                } else {
                  dataStructure[d] = {}
                  dataStructure[d][dataPoint.alias] = []
                  dataStructure[d][dataPoint.alias].push(
                    data[dataPoint.alias][i]
                  )
                }
              }
            }
          }
        }
        let orderedX = Object.keys(dataStructure).sort((a, b) => a - b)
        let timeValues = []

        finalData[xDataPoint.alias] = []
        for (let oX of orderedX) {
          // finalData[xDataPoint.alias].push(oX)
          let length = xCount[oX]
          for (let j = 0; j < length; j++) {
            finalData[xDataPoint.alias].push(parseFloat(oX))
          }
          if (options.timeValues) {
            let tValues = dataStructure[oX].timeValues
            timeValues.push(...tValues)
          }

          // finalData['timeValues'].push(... tValues)

          for (let key in dataStructure[oX]) {
            if (key !== 'timeValues') {
              let temp = dataStructure[oX][key].map(p => {
                if (p) {
                  return parseFloat(p)
                } else {
                  return null
                }
              })
              if (finalData[key]) {
                finalData[key].push(...temp)
              } else {
                finalData[key] = []
                finalData[key].push(...temp)
              }
            }
          }
        }
        options.timeValues = timeValues
        return finalData
      }
    },
    computeRegressionValues(reportObj, options, data) {
      let tabularData = {}
      let allDataPoints = []
      if (reportObj.filterRegression) {
        data = this.sortDataByX(options, data)
      }

      let xList = new Set()
      for (let dataPoint of options.dataPoints) {
        if (dataPoint.type === 'group') {
          allDataPoints.push(...dataPoint.children)
        } else {
          allDataPoints.push(dataPoint)
        }
      }

      for (let rConfig of reportObj.regressionConfig) {
        let xAxis = reportObj.report.dataPoints.filter(
          dP =>
            dP.yAxis.fieldId === rConfig.xAxis.readingId &&
            dP.metaData.parentIds[0] === rConfig.xAxis.parentId
        )

        let lr_coefficients = null
        let yAxis = reportObj.report.dataPoints.filter(
          dP =>
            dP.yAxis.fieldId === rConfig.yAxis.readingId &&
            dP.metaData.parentIds[0] === rConfig.yAxis.parentId
        )

        let actualDataPoint = allDataPoints.filter(
          dP =>
            dP.alias ===
            xAxis[0].aliases.actual + '_' + yAxis[0].aliases.actual + 'regr'
        )

        if (xAxis.length !== 0 && yAxis.length !== 0) {
          try {
            tabularData[xAxis[0].aliases.actual] = data[xAxis[0].aliases.actual]
            tabularData[yAxis[0].aliases.actual] = data[yAxis[0].aliases.actual]

            xList.add(xAxis[0].aliases.actual)
            let cleanedArrays = null
            if (reportObj.filterRegression) {
              cleanedArrays = analyticsModels.cleanArray(
                data[xAxis[0].aliases.actual],
                data[yAxis[0].aliases.actual],
                options.timeValues
              )
            } else {
              cleanedArrays = analyticsModels.cleanArray(
                data[xAxis[0].aliases.actual],
                data[yAxis[0].aliases.actual]
              )
            }
            data[xAxis[0].aliases.actual] = cleanedArrays[0]
            data[yAxis[0].aliases.actual] = cleanedArrays[1]
            if (reportObj.filterRegression) {
              options.timeValues = cleanedArrays[2]
            }
            lr_coefficients = analyticsModels.getLinearFit(
              data[xAxis[0].aliases.actual],
              data[yAxis[0].aliases.actual]
            )
            let modelData = data[xAxis[0].aliases.actual]
            let regressionData = analyticsModels.evaluateLinearData(
              lr_coefficients,
              modelData,
              'linear'
            )
            data['x'] = data[xAxis[0].aliases.actual]
            data[
              xAxis[0].aliases.actual + '_' + yAxis[0].aliases.actual + 'regr'
            ] = regressionData
            if (actualDataPoint.length !== 0) {
              actualDataPoint[0].evaluator =
                analyticsModels['evaluateLinearData']
              actualDataPoint[0].coefficients = lr_coefficients
              actualDataPoint[0][
                'MSR'
              ] = analyticsModels.computeMeanSquaredError(
                data,
                yAxis[0],
                actualDataPoint[0]
              )
              actualDataPoint[0]['rSquared'] = analyticsModels.computeRsquared(
                data,
                yAxis[0],
                actualDataPoint[0]
              )
              actualDataPoint[0].label = analyticsModels.getEquationString(
                lr_coefficients,
                actualDataPoint[0]['rSquared'],
                analyticsModels.contextNames().LINEAR
              )
            }
          } catch (e) {
            console.log(e)
            return
          }
        }
      }

      if (Object.keys(tabularData).length !== 0) {
        reportObj['tabularData'] = tabularData
      }

      for (let alias of xList) {
        delete data[alias]
      }
      options.dataPoints = this.sortBasedOnRSqr(options.dataPoints)
      return data
    },

    getMomentFormatter(operationOn) {
      switch (operationOn) {
        case 'hour':
          return 'kk'
        case 'day':
          return 'ddd'
        case 'month':
          return 'MM'
        case 'week':
          return 'ww'
        case 'quarter':
          return 'MM'
        case 'year':
          return 'YYYY'
      }
    },

    getxAliasFormatter(period, operationOn, xformat) {
      switch (period) {
        case 'hourly':
          return 'hh a'
        case 'daily':
          if (operationOn === 'week') {
            return 'ddd'
          } else if (
            operationOn === 'month' ||
            operationOn === 'year' ||
            operationOn === 'quarter'
          ) {
            return xformat === 'weeks' ? 'ddd' : 'DD'
          } else {
            if (xformat === 'weeks') {
              return 'ddd'
            } else if (xformat === 'days') {
              return 'DD'
            } else {
              return 'ddd'
            }
          }
        case 'monthly':
          return 'MMM YYYY'
        case 'quarterly':
          return '[Q]Q YYYY'
        case 'yearly':
          return 'YYYY'
      }
    },

    DateFormatter(period) {
      switch (period) {
        case 'hourly':
          return 'MMM DD YYYY, hh a'
        case 'daily':
          return 'MMM DD YYYY'
        case 'weekly':
          return '[W]ww gggg'
        case 'monthly':
          return 'MMM YYYY'
        case 'quarterly':
          return '[Q]Q YYYY'
        case 'yearly':
          return 'YYYY'
      }
    },

    getyAliasFormatter(period, operationOn, xformat) {
      switch (period) {
        case 'hourly':
          return 'MMM DD'
        case 'daily':
          if (operationOn === 'week') {
            return '[W]ww'
          } else if (operationOn === 'month' || operationOn === 'quarter') {
            return xformat === 'weeks' ? '[W]ww' : 'MMM'
          } else if (operationOn === 'year') {
            return xformat === 'weeks' ? '[W]ww gggg' : 'MMM'
          } else {
            if (xformat === 'weeks') {
              return '[W]ww gggg'
            } else if (xformat === 'days') {
              return 'MMM'
            } else {
              return 'MMM DD'
            }
          }
      }
    },
    getxAxisData(xformat, operationOn) {
      switch (xformat) {
        case 'hours':
          return [
            '12 am',
            '01 am',
            '02 am',
            '03 am',
            '04 am',
            '05 am',
            '06 am',
            '07 am',
            '08 am',
            '09 am',
            '10 am',
            '11 am',
            '12 pm',
            '01 pm',
            '02 pm',
            '03 pm',
            '04 pm',
            '05 pm',
            '06 pm',
            '07 pm',
            '08 pm',
            '09 pm',
            '10 pm',
            '11 pm',
          ]
        case 'days':
          return operationOn === 'week'
            ? ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat']
            : [
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
        case 'weeks':
          return ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat']
      }
    },

    getyAxisData(period, operationOn, range, xformat, offset) {
      const currentTimezone = Vue.prototype.$timezone
      switch (period) {
        case 'hourly':
          if (
            operationOn === 'month' ||
            operationOn === 'year' ||
            operationOn === 'quarter' ||
            (operationOn === 'day' && offset >= 32)
          ) {
            let domain = []
            domain.push(
              Array.from(range.by('days')).map(r =>
                moment(r.valueOf())
                  .tz(currentTimezone)
                  .format('MMM DD')
              )
            )
            domain.push(
              Array.from(range.by('months')).map(r =>
                moment(r.valueOf())
                  .tz(currentTimezone)
                  .startOf('month')
                  .format('MMM')
              )
            )
            return domain
          } else {
            let domain = Array.from(range.by('days')).map(r =>
              moment(r.valueOf())
                .tz(currentTimezone)
                .format('MMM DD')
            )
            return domain
          }
        case 'daily':
          if (operationOn === 'week') {
            let domain = Array.from(range.by('weeks')).map(r =>
              moment(r.valueOf())
                .tz(currentTimezone)
                .startOf('week')
                .format('[W]ww')
            )
            return domain
          } else if (operationOn === 'month' || operationOn === 'quarter') {
            if (xformat === 'weeks') {
              let domain = []
              domain.push(
                Array.from(range.by('weeks')).map(r =>
                  moment(r.valueOf())
                    .tz(currentTimezone)
                    .startOf('week')
                    .format('[W]ww')
                )
              )
              domain.push(
                Array.from(range.by('months')).map(r =>
                  moment(r.valueOf())
                    .tz(currentTimezone)
                    .startOf('month')
                    .format('MMM')
                )
              )
              return domain
            } else {
              let domain = Array.from(range.by('months')).map(r =>
                moment(r.valueOf())
                  .tz(currentTimezone)
                  .startOf('month')
                  .format('MMM')
              )
              return domain
            }
          } else if (operationOn === 'year') {
            if (xformat === 'weeks') {
              let domain = []
              domain.push(
                Array.from(range.by('weeks')).map(r =>
                  moment(r.valueOf())
                    .tz(currentTimezone)
                    .format('[W]ww gggg')
                )
              )

              domain.push(
                Array.from(range.by('months')).map(r =>
                  moment(r.valueOf())
                    .tz(currentTimezone)
                    .startOf('month')
                    .format('MMM')
                )
              )
              return domain
            } else {
              let domain = Array.from(range.by('months')).map(r =>
                moment(r.valueOf())
                  .tz(currentTimezone)
                  .startOf('month')
                  .format('MMM')
              )
              return domain
            }
          } else {
            let domain = []
            if (xformat === 'weeks') {
              domain = Array.from(range.by('days')).map(r =>
                moment(r.valueOf())
                  .tz(currentTimezone)
                  .format('[W]ww gggg')
              )
              domain = domain.filter(
                (month, index) => domain.indexOf(month) === index
              )
            } else if (xformat === 'days') {
              domain = Array.from(range.by('days')).map(r =>
                moment(r.valueOf())
                  .tz(currentTimezone)
                  .format('MMM')
              )
              domain = domain.filter(
                (month, index) => domain.indexOf(month) === index
              )
            } else {
              domain = Array.from(range.by('days')).map(r =>
                moment(r.valueOf())
                  .tz(currentTimezone)
                  .format('MMM DD')
              )
            }
            return domain
          }
      }
    },

    transformDataForHeatMap(
      reportObj,
      data,
      xAlias,
      yAlias,
      isContinous,
      xformat
    ) {
      let transformedData = []
      let dataWithAxis = []
      const currentTimezone = Vue.prototype.$timezone
      const momentRange = MomentRange.extendMoment(moment)
      let range = momentRange.range(
        new Date(reportObj.dateRange.time[0]),
        new Date(reportObj.dateRange.time[1])
      )
      if (data && data.length != 0) {
        if (isContinous && reportObj.dateRange && reportObj.dateRange.period) {
          let yFormatter = this.getyAliasFormatter(
            reportObj.dateRange.period,
            reportObj.dateRange.operationOn,
            xformat
          )
          let xFormatter = this.getxAliasFormatter(
            reportObj.dateRange.period,
            reportObj.dateRange.operationOn,
            xformat
          )
          let dateFormat = this.getDateFormat(reportObj.dateRange.period)
          console.log('reportObj', reportObj)
          if (dateFormat) {
            for (let record of data) {
              if (record[xAlias]) {
                let groupedData = {}
                let formattedDate = moment(record[xAlias])
                  .tz(currentTimezone)
                  .format(yFormatter)
                groupedData['Y'] = formattedDate
                groupedData[xAlias] = moment(record[xAlias])
                  .tz(currentTimezone)
                  .format(xFormatter)
                groupedData['value'] = record[yAlias]
                  ? parseFloat(record[yAlias])
                  : null
                groupedData['xTitle'] = reportObj.options.axis.x.label.text
                groupedData['yTitle'] = reportObj.options.dataPoints[0].label
                groupedData['originalValue'] = record[xAlias]
                groupedData['violated_value'] = record['violated_value']
                  ? record['violated_value']
                  : null
                transformedData.push(groupedData)
              }
            }
          }
          let xAxisData = this.getxAxisData(
            xformat,
            reportObj.dateRange.operationOn
          )
          let yAxisData = this.getyAxisData(
            reportObj.dateRange.period,
            reportObj.dateRange.operationOn,
            range,
            xformat,
            reportObj.dateRange.offset
          )
          dataWithAxis.push(transformedData, xAxisData, yAxisData)
        }
        return dataWithAxis
      }
    },
    transformDataforTreeMap(
      reportObj,
      resultObject,
      data,
      xAlias,
      yAlias,
      colorAlias,
      sizePoint,
      colorPoint,
      mode
    ) {
      let transformedData = []
      const currentTimezone = Vue.prototype.$timezone
      if (data && data.length != 0) {
        for (let record of data) {
          if (record[xAlias]) {
            let temp = {}
            temp['mode'] = mode
            temp['name'] =
              resultObject.mode === 4
                ? moment(record[xAlias])
                    .tz(currentTimezone)
                    .format(this.DateFormatter(reportObj.dateRange.period))
                : record[xAlias]
            temp['sizePoint'] = sizePoint.name
            temp['colorPoint'] =
              sizePoint.name !== colorPoint.name ? colorPoint.name : null
            temp['colorUnit'] = colorPoint.yAxis.unitStr
              ? ' ' + colorPoint.yAxis.unitStr
              : ''
            temp['value'] = record[yAlias]
              ? this.DecimalRoundOff(parseFloat(record[yAlias]))
              : null
            temp['colorValue'] = record[colorAlias]
              ? this.DecimalRoundOff(parseFloat(record[colorAlias]))
              : null
            temp['unit'] = resultObject.report.dataPoints[0].yAxis.unitStr
              ? ' ' + resultObject.report.dataPoints[0].yAxis.unitStr
              : ''
            transformedData.push(temp)
          }
        }
      }
      if (transformedData.length > 1) {
        transformedData.sort(function(a, b) {
          return b['value'] - a['value']
        })
      }
      return {
        name: resultObject.report.dataPoints[0].name,
        children: transformedData,
        unit: resultObject.report.dataPoints[0].yAxis.unitStr,
      }
    },

    getWorkpplaceTreeMapData({
      resultObject,
      data,
      xAlias,
      yAlias,
      colorAlias,
      sizePoint,
      colorPoint,
      mode,
      colorMap,
    }) {
      let lookupMap = this.getLookupMap(resultObject)
      let transformedData = []
      if (data && data.length != 0) {
        data.forEach((record, index) => {
          let d = this.getTreemapData({
            record,
            sizePoint,
            colorPoint,
            colorAlias,
            lookupMap,
            yAlias,
            xAlias,
            colorMap,
          })
          transformedData.push(d)
        })
      }
      return {
        name: resultObject.report.dataPoints[0].name,
        children: transformedData,
        unit: resultObject.report.dataPoints[0].yAxis.unitStr,
      }
    },

    transformWorkorderDataforTreeMap(
      reportObj,
      resultObject,
      data,
      xAlias,
      yAlias,
      colorAlias,
      sizePoint,
      colorPoint,
      mode
    ) {
      let lookupMap = this.getLookupMap(resultObject)
      let transformedData = []
      if (data && data.length != 0) {
        data.forEach((record, index) => {
          console.log('index')
          if (
            (reportObj.report && reportObj.report.id === 4170) ||
            reportObj.report.id === 4158
          ) {
            // to be removed
            if (record[xAlias]) {
              let temp = {}
              temp['mode'] = 'Building'
              temp['name'] =
                reportObj.data && reportObj.data.x
                  ? reportObj.data.x[index]
                  : record[xAlias]
              temp['sizePoint'] = sizePoint.name
              temp['colorPoint'] =
                sizePoint.name !== colorPoint.name ? colorPoint.name : null
              temp['value'] = record[yAlias]
              temp['colorValue'] = record[colorAlias]
              temp['colorUnit'] = 'kWh/ft²'
              temp['unit'] = resultObject.report.dataPoints[0].yAxis.unitStr
              transformedData.push(temp)
            }
          } else {
            if (this.isContainsArray(record)) {
              let d = this.getTreemapData({
                record,
                sizePoint,
                colorPoint,
                colorAlias,
                lookupMap,
                yAlias,
                xAlias,
              })
              transformedData.push(d)
            } else if (record[xAlias]) {
              let temp = {}
              temp['mode'] = 'Building'
              temp['name'] = record[xAlias]
              temp['sizePoint'] = sizePoint.name
              temp['colorPoint'] =
                sizePoint.name !== colorPoint.name ? colorPoint.name : null
              temp['value'] = record[yAlias]
              temp['colorValue'] = record[colorAlias]
              temp['unit'] = resultObject.report.dataPoints[0].yAxis.unitStr
              transformedData.push(temp)
            }
          }
        })
      }
      return {
        name: resultObject.report.dataPoints[0].name,
        children: transformedData,
        unit: resultObject.report.dataPoints[0].yAxis.unitStr,
      }
    },
    getLookupMap(resultObject) {
      let result = {}
      if (
        resultObject?.report?.dataPoints &&
        resultObject.report.dataPoints.length
      ) {
        let dataPoint = resultObject.report.dataPoints[0]
        if (dataPoint?.xAxis?.lookupMap) {
          result['X'] = dataPoint.xAxis.lookupMap
        }

        if (dataPoint?.groupByFields && dataPoint.groupByFields.length) {
          dataPoint.groupByFields.forEach(rt => {
            if (rt.lookupMap) {
              result[rt.alias] = rt.lookupMap
            }
          })
        }
      }
      return result
    },
    getTreemapData({
      colorPoint,
      record,
      sizePoint,
      colorAlias,
      lookupMap,
      yAlias,
      xAlias,
      colorMap,
    }) {
      let temp = {}
      if (Array.isArray(record)) {
        let dataArray = []
        record.forEach(rt => {
          let data = this.getTreemapData({
            ...{ record: rt },
            sizePoint,
            colorAlias,
            lookupMap,
            yAlias,
            xAlias,
            colorMap,
          })
          dataArray.push(data)
        })
        return dataArray
      } else {
        Object.keys(record).forEach(key => {
          if (Array.isArray(record[key])) {
            let value = record[key]
            temp['mode'] = 'Status'
            temp['name'] = this.getNameFromLookup(lookupMap, record, xAlias)
            temp['sizePoint'] = sizePoint.name
            temp['children'] = this.getTreemapData({
              ...{ record: value },
              sizePoint,
              ...{ colorAlias: key },
              lookupMap,
              yAlias,
              xAlias,
              colorMap,
            })
          } else if (key === xAlias) {
            temp['mode'] = 'Status'
            temp['name'] = this.getNameFromLookup(lookupMap, record, key)
            temp['key'] = record[key] || -1
            temp['sizePoint'] = sizePoint.name

            // temp['value'] = 0
            temp['color'] = 'transparent'
          } else {
            temp['name'] = this.getNameFromLookup(lookupMap, record, key)
            temp['sizePoint'] = sizePoint.name
            temp['mode'] = 'Status'
            temp['key'] = record[key] || -1
            temp['value'] = record[yAlias]
            temp['color'] = this.getColorFromMap({
              colorAlias,
              colorMap,
              xAlias,
              yAlias,
              record,
            })
          }
        })
      }

      return temp
    },
    getColorFromMap({ colorAlias, colorMap, xAlias, yAlias, record }) {
      if (![xAlias, yAlias].includes(colorAlias)) {
        let value = record[colorAlias]
        if (value) {
          if (value === '') {
            return '#d9d9d9'
          }
          return colorMap[value]
        }
      }
      return null
    },
    getNameFromLookup(lookupMap, record, key) {
      let value = record[key]
      let lookupMapValues = lookupMap[key]
      if (value && lookupMapValues) {
        if (lookupMapValues[value]) {
          return lookupMapValues[value]
        }
      }
      return 'Empty'
    },
    isContainsArray(data) {
      let isArray = false
      Object.values(data).forEach(value => {
        if (Array.isArray(value)) {
          isArray = true
        }
      })
      return isArray
    },
    transformWorkorderDataForHeatMap(
      resultObject,
      reportObject,
      label,
      isContinous,
      xformat
    ) {
      let transformedData = []
      let dataWithAxis = []
      const currentTimezone = Vue.prototype.$timezone
      const momentRange = MomentRange.extendMoment(moment)
      let range = momentRange.range(
        new Date(reportObj.dateRange.time[0]),
        new Date(reportObj.dateRange.time[1])
      )
    },
    fillRemainingXValues(xData, transformedData, alias) {
      let finalData = []
      let dateFormat = this.getDateFormat(this.reportObject.dateRange.period)
      let yFormatter = this.reportObject.dateRange
        ? this.reportObject.dateRange.period
        : null

      for (let rec of xData) {
        let existingRecord = transformedData.find(x => x['X'] === rec)
        if (existingRecord) {
          finalData.push(existingRecord)
        } else {
          let temp = {}
          temp['X'] = moment(rec, dateFormat.format).format('MM-DD-YYYY')
          temp[alias] = rec
          temp['value'] = 0 + ''
          finalData.push(temp)
        }
      }
      return finalData
    },
    getheatMapData(xData, yData, transformedData, reportObj) {
      let finalData = []
      for (let x of xData) {
        for (let y of yData) {
          let existingRecord = transformedData.find(
            r => r['X'] === x && r['Y'] === y
          )
          if (existingRecord) {
            finalData.push(existingRecord)
          } else {
            let temp = {}
            temp['Y'] = y
            temp['X'] = x
            temp['value'] = null
            temp['xTitle'] = reportObj.options.axis.x.label.text
            temp['yTitle'] = reportObj.options.axis.y.label.text
            temp['originalValue'] = moment(y)
              .tz(Vue.prototype.$timezone)
              .format('x')
            finalData.push(temp)
          }
        }
      }
      return finalData
    },
    getRoundedNumber(number, round) {
      number = parseFloat(number)
      let length = (Math.log(number) * Math.LOG10E + 1) | 0
      let pow = Math.pow(10, length - 1)
      switch (round) {
        case 'ceil': {
          let max = Math.ceil(number / pow) * pow
          return max - Math.floor(max) !== 0 ? max.toFixed(2) : max
        }
        case 'floor': {
          let min = Math.floor(number / pow) * pow
          return min - Math.floor(min) !== 0 ? min.toFixed(2) : min
        }
        default: {
          let common_num = Math.ceil(number / pow) * pow
          return common_num - Math.floor(common_num) !== 0
            ? common_num.toFixed(2)
            : common_num
        }
      }
    },
    sampletreemapdata() {
      return {
        name: 'flare',
        children: [
          {
            name: 'analytics',
            children: [
              {
                name: 'cluster',
                children: [
                  {
                    name: 'AgglomerativeCluster',
                    value: 3938,
                    colorValue: 3938,
                  },
                  { name: 'CommunityStructure', value: 3812, colorValue: 3812 },
                  {
                    name: 'HierarchicalCluster',
                    value: 6714,
                    colorValue: 6714,
                  },
                  { name: 'MergeEdge', value: 743, colorValue: 743 },
                ],
              },
              {
                name: 'graph',
                children: [
                  {
                    name: 'BetweennessCentrality',
                    value: 3534,
                    colorValue: 3534,
                  },
                  { name: 'LinkDistance', value: 5731, colorValue: 5731 },
                  { name: 'MaxFlowMinCut', value: 7840, colorValue: 7840 },
                  { name: 'ShortestPaths', value: 5914, colorValue: 5914 },
                  { name: 'SpanningTree', value: 3416, colorValue: 3416 },
                ],
              },
              {
                name: 'optimization',
                children: [
                  { name: 'AspectRatioBanker', value: 7074, colorValue: 7074 },
                ],
              },
            ],
          },
          {
            name: 'animate',
            children: [
              { name: 'Easing', value: 17010 },
              { name: 'FunctionSequence', value: 5842 },
              {
                name: 'interpolate',
                children: [
                  { name: 'ArrayInterpolator', value: 1983 },
                  { name: 'ColorInterpolator', value: 2047 },
                  { name: 'DateInterpolator', value: 1375 },
                  { name: 'Interpolator', value: 8746 },
                  { name: 'MatrixInterpolator', value: 2202 },
                  { name: 'NumberInterpolator', value: 1382 },
                  { name: 'ObjectInterpolator', value: 1629 },
                  { name: 'PointInterpolator', value: 1675 },
                  { name: 'RectangleInterpolator', value: 2042 },
                ],
              },
              { name: 'ISchedulable', value: 1041 },
              { name: 'Parallel', value: 5176 },
              { name: 'Pause', value: 449 },
              { name: 'Scheduler', value: 5593 },
              { name: 'Sequence', value: 5534 },
              { name: 'Transition', value: 9201 },
              { name: 'Transitioner', value: 19975 },
              { name: 'TransitionEvent', value: 1116 },
              { name: 'Tween', value: 6006 },
            ],
          },
          {
            name: 'data',
            children: [
              {
                name: 'converters',
                children: [
                  { name: 'Converters', value: 721 },
                  { name: 'DelimitedTextConverter', value: 4294 },
                  { name: 'GraphMLConverter', value: 9800 },
                  { name: 'IDataConverter', value: 1314 },
                  { name: 'JSONConverter', value: 2220 },
                ],
              },
              { name: 'DataField', value: 1759 },
              { name: 'DataSchema', value: 2165 },
              { name: 'DataSet', value: 586 },
              { name: 'DataSource', value: 3331 },
              { name: 'DataTable', value: 772 },
              { name: 'DataUtil', value: 3322 },
            ],
          },
          {
            name: 'display',
            children: [
              { name: 'DirtySprite', value: 8833 },
              { name: 'LineSprite', value: 1732 },
              { name: 'RectSprite', value: 3623 },
              { name: 'TextSprite', value: 10066 },
            ],
          },
          {
            name: 'flex',
            children: [{ name: 'FlareVis', value: 4116 }],
          },
          {
            name: 'physics',
            children: [
              { name: 'DragForce', value: 1082 },
              { name: 'GravityForce', value: 1336 },
              { name: 'IForce', value: 319 },
              { name: 'NBodyForce', value: 10498 },
              { name: 'Particle', value: 2822 },
              { name: 'Simulation', value: 9983 },
              { name: 'Spring', value: 2213 },
              { name: 'SpringForce', value: 1681 },
            ],
          },
          {
            name: 'query',
            children: [
              { name: 'AggregateExpression', value: 1616 },
              { name: 'And', value: 1027 },
              { name: 'Arithmetic', value: 3891 },
              { name: 'Average', value: 891 },
              { name: 'BinaryExpression', value: 2893 },
              { name: 'Comparison', value: 5103 },
              { name: 'CompositeExpression', value: 3677 },
              { name: 'Count', value: 781 },
              { name: 'DateUtil', value: 4141 },
              { name: 'Distinct', value: 933 },
              { name: 'Expression', value: 5130 },
              { name: 'ExpressionIterator', value: 3617 },
              { name: 'Fn', value: 3240 },
              { name: 'If', value: 2732 },
              { name: 'IsA', value: 2039 },
              { name: 'Literal', value: 1214 },
              { name: 'Match', value: 3748 },
              { name: 'Maximum', value: 843 },
              {
                name: 'methods',
                children: [
                  { name: 'add', value: 593 },
                  { name: 'and', value: 330 },
                  { name: 'average', value: 287 },
                  { name: 'count', value: 277 },
                  { name: 'distinct', value: 292 },
                  { name: 'div', value: 595 },
                  { name: 'eq', value: 594 },
                  { name: 'fn', value: 460 },
                  { name: 'gt', value: 603 },
                  { name: 'gte', value: 625 },
                  { name: 'iff', value: 748 },
                  { name: 'isa', value: 461 },
                  { name: 'lt', value: 597 },
                  { name: 'lte', value: 619 },
                  { name: 'max', value: 283 },
                  { name: 'min', value: 283 },
                  { name: 'mod', value: 591 },
                  { name: 'mul', value: 603 },
                  { name: 'neq', value: 599 },
                  { name: 'not', value: 386 },
                  { name: 'or', value: 323 },
                  { name: 'orderby', value: 307 },
                  { name: 'range', value: 772 },
                  { name: 'select', value: 296 },
                  { name: 'stddev', value: 363 },
                  { name: 'sub', value: 600 },
                  { name: 'sum', value: 280 },
                  { name: 'update', value: 307 },
                  { name: 'variance', value: 335 },
                  { name: 'where', value: 299 },
                  { name: 'xor', value: 354 },
                  { name: '_', value: 264 },
                ],
              },
              { name: 'Minimum', value: 843 },
              { name: 'Not', value: 1554 },
              { name: 'Or', value: 970 },
              { name: 'Query', value: 13896 },
              { name: 'Range', value: 1594 },
              { name: 'StringUtil', value: 4130 },
              { name: 'Sum', value: 791 },
              { name: 'Variable', value: 1124 },
              { name: 'Variance', value: 1876 },
              { name: 'Xor', value: 1101 },
            ],
          },
          {
            name: 'scale',
            children: [
              { name: 'IScaleMap', value: 2105 },
              { name: 'LinearScale', value: 1316 },
              { name: 'LogScale', value: 3151 },
              { name: 'OrdinalScale', value: 3770 },
              { name: 'QuantileScale', value: 2435 },
              { name: 'QuantitativeScale', value: 4839 },
              { name: 'RootScale', value: 1756 },
              { name: 'Scale', value: 4268 },
              { name: 'ScaleType', value: 1821 },
              { name: 'TimeScale', value: 5833 },
            ],
          },
          {
            name: 'util',
            children: [
              { name: 'Arrays', value: 8258 },
              { name: 'Colors', value: 10001 },
              { name: 'Dates', value: 8217 },
              { name: 'Displays', value: 12555 },
              { name: 'Filter', value: 2324 },
              { name: 'Geometry', value: 10993 },
              {
                name: 'heap',
                children: [
                  { name: 'FibonacciHeap', value: 9354 },
                  { name: 'HeapNode', value: 1233 },
                ],
              },
              { name: 'IEvaluable', value: 335 },
              { name: 'IPredicate', value: 383 },
              { name: 'IValueProxy', value: 874 },
              {
                name: 'math',
                children: [
                  { name: 'DenseMatrix', value: 3165 },
                  { name: 'IMatrix', value: 2815 },
                  { name: 'SparseMatrix', value: 3366 },
                ],
              },
              { name: 'Maths', value: 17705 },
              { name: 'Orientation', value: 1486 },
              {
                name: 'palette',
                children: [
                  { name: 'ColorPalette', value: 6367 },
                  { name: 'Palette', value: 1229 },
                  { name: 'ShapePalette', value: 2059 },
                  { name: 'SizePalette', value: 2291 },
                ],
              },
              { name: 'Property', value: 5559 },
              { name: 'Shapes', value: 19118 },
              { name: 'Sort', value: 6887 },
              { name: 'Stats', value: 6557 },
              { name: 'Strings', value: 22026 },
            ],
          },
          {
            name: 'vis',
            children: [
              {
                name: 'axis',
                children: [
                  { name: 'Axes', value: 1302 },
                  { name: 'Axis', value: 24593 },
                  { name: 'AxisGridLine', value: 652 },
                  { name: 'AxisLabel', value: 636 },
                  { name: 'CartesianAxes', value: 6703 },
                ],
              },
              {
                name: 'controls',
                children: [
                  { name: 'AnchorControl', value: 2138 },
                  { name: 'ClickControl', value: 3824 },
                  { name: 'Control', value: 1353 },
                  { name: 'ControlList', value: 4665 },
                  { name: 'DragControl', value: 2649 },
                  { name: 'ExpandControl', value: 2832 },
                  { name: 'HoverControl', value: 4896 },
                  { name: 'IControl', value: 763 },
                  { name: 'PanZoomControl', value: 5222 },
                  { name: 'SelectionControl', value: 7862 },
                  { name: 'TooltipControl', value: 8435 },
                ],
              },
              {
                name: 'data',
                children: [
                  { name: 'Data', value: 20544 },
                  { name: 'DataList', value: 19788 },
                  { name: 'DataSprite', value: 10349 },
                  { name: 'EdgeSprite', value: 3301 },
                  { name: 'NodeSprite', value: 19382 },
                  {
                    name: 'render',
                    children: [
                      { name: 'ArrowType', value: 698 },
                      { name: 'EdgeRenderer', value: 5569 },
                      { name: 'IRenderer', value: 353 },
                      { name: 'ShapeRenderer', value: 2247 },
                    ],
                  },
                  { name: 'ScaleBinding', value: 11275 },
                  { name: 'Tree', value: 7147 },
                  { name: 'TreeBuilder', value: 9930 },
                ],
              },
              {
                name: 'events',
                children: [
                  { name: 'DataEvent', value: 2313 },
                  { name: 'SelectionEvent', value: 1880 },
                  { name: 'TooltipEvent', value: 1701 },
                  { name: 'VisualizationEvent', value: 1117 },
                ],
              },
              {
                name: 'legend',
                children: [
                  { name: 'Legend', value: 20859 },
                  { name: 'LegendItem', value: 4614 },
                  { name: 'LegendRange', value: 10530 },
                ],
              },
              {
                name: 'operator',
                children: [
                  {
                    name: 'distortion',
                    children: [
                      { name: 'BifocalDistortion', value: 4461 },
                      { name: 'Distortion', value: 6314 },
                      { name: 'FisheyeDistortion', value: 3444 },
                    ],
                  },
                  {
                    name: 'encoder',
                    children: [
                      { name: 'ColorEncoder', value: 3179 },
                      { name: 'Encoder', value: 4060 },
                      { name: 'PropertyEncoder', value: 4138 },
                      { name: 'ShapeEncoder', value: 1690 },
                      { name: 'SizeEncoder', value: 1830 },
                    ],
                  },
                  {
                    name: 'filter',
                    children: [
                      { name: 'FisheyeTreeFilter', value: 5219 },
                      { name: 'GraphDistanceFilter', value: 3165 },
                      { name: 'VisibilityFilter', value: 3509 },
                    ],
                  },
                  { name: 'IOperator', value: 1286 },
                  {
                    name: 'label',
                    children: [
                      { name: 'Labeler', value: 9956 },
                      { name: 'RadialLabeler', value: 3899 },
                      { name: 'StackedAreaLabeler', value: 3202 },
                    ],
                  },
                  {
                    name: 'layout',
                    children: [
                      { name: 'AxisLayout', value: 6725 },
                      { name: 'BundledEdgeRouter', value: 3727 },
                      { name: 'CircleLayout', value: 9317 },
                      { name: 'CirclePackingLayout', value: 12003 },
                      { name: 'DendrogramLayout', value: 4853 },
                      { name: 'ForceDirectedLayout', value: 8411 },
                      { name: 'IcicleTreeLayout', value: 4864 },
                      { name: 'IndentedTreeLayout', value: 3174 },
                      { name: 'Layout', value: 7881 },
                      { name: 'NodeLinkTreeLayout', value: 12870 },
                      { name: 'PieLayout', value: 2728 },
                      { name: 'RadialTreeLayout', value: 12348 },
                      { name: 'RandomLayout', value: 870 },
                      { name: 'StackedAreaLayout', value: 9121 },
                      { name: 'TreeMapLayout', value: 9191 },
                    ],
                  },
                  { name: 'Operator', value: 2490 },
                  { name: 'OperatorList', value: 5248 },
                  { name: 'OperatorSequence', value: 4190 },
                  { name: 'OperatorSwitch', value: 2581 },
                  { name: 'SortOperator', value: 2023 },
                ],
              },
              { name: 'Visualization', value: 16540 },
            ],
          },
        ],
      }
    },
    getAllTreeMapDataValues(data, valueArray) {
      if (data.value) {
        valueArray.push(parseFloat(data.value))
      }
      if (!data.children) {
        return
      }
      data.children.forEach(child =>
        this.getAllTreeMapDataValues(child, valueArray)
      )
      return valueArray
    },
    DecimalRoundOff(number) {
      return parseFloat(parseInt(number * 100) / 100)
    },
    formatZeroesInData(data) {
      let formattedData = {}
      for (let column of Object.keys(data)) {
        if (column !== 'x') {
          formattedData[column] = data[column].map(val => {
            return val === 0 ? null : val
          })
        } else {
          formattedData[column] = data[column]
        }
      }
      return formattedData
    },
  },
}
