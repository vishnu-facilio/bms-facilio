import moment from 'moment-timezone'
import Vue from 'vue'
import cloneDeep from 'lodash/cloneDeep'
import getProperty from 'dlv'
let DateType = {
  DAY: 1,
  WEEK: 2,
  MONTH: 3,
  YEAR: 4,
  QUARTER: 5,
  RANGE: 6,
  properties: {
    1: { name: 'Day', value: 1, code: 'D' },
    2: { name: 'Week', value: 2, code: 'W' },
    3: { name: 'Month', value: 3, code: 'M' },
    4: { name: 'Year', value: 4, code: 'Y' },
    5: { name: 'Quarter', value: 5, code: 'Q' },
    6: { name: 'Range', value: 6, code: 'R' },
  },
}

export default {
  dateOperators() {
    return cloneDeep({
      Days: [
        {
          id: 0,
          operatorId: 22,
          label: 'Today',
          value: null,
          offset: 0,
          operationOn: 'day',
          operationOnId: 1,
        },
        {
          id: 1,
          operatorId: 25,
          label: 'Yesterday',
          value: null,
          offset: -1,
          operationOn: 'day',
          operationOnId: 1,
        },
        {
          id: 2,
          operatorId: 43,
          label: 'Today upto Now',
          value: null,
          offset: 0,
          operationOn: 'day',
          operationOnId: 1,
        },
        {
          id: 6,
          operatorId: 49,
          label: 'Last 2 days',
          value: null,
          offset: -2,
          operationOn: 'day',
          operationOnId: 1,
        },
        {
          id: 7,
          operatorId: 49,
          label: 'Last 7 days',
          value: null,
          offset: -7,
          operationOn: 'day',
          operationOnId: 1,
        },
        {
          id: 8,
          operatorId: 49,
          label: 'Last 15 days',
          value: null,
          offset: -15,
          operationOn: 'day',
          operationOnId: 1,
        },
        {
          id: 9,
          operatorId: 49,
          label: 'Last 30 days',
          value: null,
          offset: -30,
          operationOn: 'day',
          operationOnId: 1,
        },
      ],
      Weeks: [
        {
          id: 10,
          operatorId: 31,
          label: 'This week',
          value: null,
          offset: 0,
          operationOn: 'week',
          operationOnId: 2,
        },
        {
          id: 11,
          operatorId: 30,
          label: 'Last week',
          value: null,
          offset: -1,
          operationOn: 'week',
          operationOnId: 2,
        },
        {
          id: 13,
          operatorId: 50,
          label: 'Last 2 weeks',
          value: null,
          offset: -2,
          operationOn: 'week',
          operationOnId: 2,
        },
        {
          id: 14,
          operatorId: 50,
          label: 'Last 4 weeks',
          value: null,
          offset: -4,
          operationOn: 'week',
          operationOnId: 2,
        },
        {
          id: 15,
          operatorId: 50,
          label: 'Last 8 weeks',
          value: null,
          offset: -8,
          operationOn: 'week',
          operationOnId: 2,
        },
        {
          operatorId: 47,
          label: 'This Week Until Now',
          offset: 0,
          value: null,
          operationOn: 'week',
          operationOnId: 2,
        },
      ],
      Months: [
        {
          id: 16,
          operatorId: 28,
          label: 'This month',
          value: null,
          offset: 0,
          operationOn: 'month',
          operationOnId: 3,
        },
        {
          id: 17,
          operatorId: 66,
          label: 'This Month till yesterday',
          value: null,
          operationOn: 'month',
          operationOnId: 3,
        },
        {
          id: 18,
          operatorId: 27,
          label: 'Last month',
          value: null,
          offset: -1,
          operationOn: 'month',
          operationOnId: 3,
        },
        {
          id: 19,
          operatorId: 51,
          label: 'Last 2 months',
          value: null,
          offset: -2,
          operationOn: 'month',
          operationOnId: 3,
        },
        {
          id: 20,
          operatorId: 51,
          label: 'Last 4 months',
          offset: -4,
          value: null,
          operationOn: 'month',
          operationOnId: 3,
        },
        {
          operatorId: 48,
          label: 'This Month Until Now',
          offset: 0,
          value: null,
          operationOn: 'month',
          operationOnId: 3,
        },
      ],
      Year: [
        {
          id: 21,
          operatorId: 44,
          label: 'This year',
          value: null,
          offset: 0,
          operationOn: 'year',
          operationOnId: 4,
        },
        {
          id: 23,
          operatorId: 45,
          label: 'Last year',
          value: null,
          offset: -1,
          operationOn: 'year',
          operationOnId: 4,
        },
        {
          id: 24,
          operatorId: 46,
          label: 'This Year upto Now',
          value: null,
          offset: 0,
          operationOn: 'year',
          operationOnId: 4,
        },
        {
          id: 25,
          operatorId: 72,
          label: 'Till Now',
          value: null,
          offset: 0,
          operationOn: 'year',
          operationOnId: 4,
        },
      ],
      Quarter: [
        {
          operatorId: 68,
          label: 'This Quarter',
          value: null,
          offset: 0,
          operationOn: 'quarter',
          operationOnId: 5,
        },
        {
          operatorId: 69,
          label: 'Last Quarter',
          value: null,
          offset: -1,
          operationOn: 'quarter',
          operationOnId: 5,
        },
        {
          operatorId: 70,
          label: '',
          value: null,
          offset: -1,
          operationOn: 'quarter',
          operationOnId: 5,
        },
      ],
      Custom: [
        {
          operatorId: 62,
          label: '',
          value: null,
          offset: 0,
          operationOn: 'day',
          operationOnId: 1,
        },
        {
          operatorId: 63,
          label: '',
          value: null,
          offset: 0,
          operationOn: 'week',
          operationOnId: 2,
        },
        {
          operatorId: 64,
          label: '',
          value: null,
          offset: 0,
          operationOn: 'month',
          operationOnId: 3,
        },
        {
          operatorId: 65,
          label: '',
          value: null,
          offset: 0,
          operationOn: 'year',
          operationOnId: 4,
        },
        {
          operatorId: 20,
          label: '',
          value: null,
          offset: null,
          operationOn: 'hour',
          operationOnId: 6,
        },
        {
          operatorId: 67,
          label: '',
          value: null,
          offset: 0,
          operationOn: 'quarter',
          operationOnId: 5,
        },
      ],
    })
  },
  computeMoment(additionalInfo, timeZone) {
    let momentNew = moment.tz(timeZone)
    let finalValue = null
    if (additionalInfo.period) {
      switch (additionalInfo.period) {
        case 'start': {
          momentNew.startOf(additionalInfo.label)
          break
        }
        case 'end': {
          momentNew.endOf(additionalInfo.label)
          break
        }
      }
    }

    if (additionalInfo.operation) {
      switch (additionalInfo.operation) {
        case 'add': {
          momentNew.add(additionalInfo.label, additionalInfo.value)
          break
        }
        case 'subtract': {
          momentNew.subtract(additionalInfo.label, additionalInfo.value)
          break
        }
      }
    }

    if (additionalInfo.label) {
      switch (additionalInfo.label) {
        case 'day': {
          finalValue = momentNew.day()
          break
        }
        case 'week': {
          finalValue = momentNew.week()
          break
        }
        case 'month': {
          finalValue = momentNew.month()
          break
        }
        case 'year': {
          finalValue = momentNew.year()
          break
        }
      }
    }
    return finalValue
  },
  DateType,
  tabContextNames() {
    let contextNames = {}
    contextNames['DAY'] = 'D'
    contextNames['WEEK'] = 'W'
    contextNames['MONTH'] = 'M'
    contextNames['YEAR'] = 'Y'
    contextNames['QUARTER'] = 'Q'
  },
  getDateOperatorsHashMap() {
    // Returns a {operatorId: operator} hashMap.
    const dateOperators = this.dateOperators() ?? {}
    const operatorsHashMap = {}
    Object.keys(dateOperators).forEach(operatorKey => {
      const operators = dateOperators[operatorKey]
      operators.forEach(operator => {
        const { operatorId } = operator ?? {}
        operatorsHashMap[operatorId] = operator
      })
    })
    return cloneDeep(operatorsHashMap)
  },
  updateStartOfWeekInMoment(org) {
    if (org && org?.country === 'GB') {
      moment.updateLocale('en', {
        week: {
          dow: 1, // Monday is the first day of the week.
        },
      })
    }
  },
  calculateCustomCaseValue(customCaseSection, timeZone, org) {
    this.updateStartOfWeekInMoment(org)
    if (customCaseSection.operatorId === 72 || customCaseSection.id === 25) {
      // till now
      let data = getProperty(Vue, 'prototype.$org.createdTime', 1483209000000)
      customCaseSection.value = [data]
      customCaseSection.value.push(moment.tz(timeZone).valueOf())
    } else if (customCaseSection.operatorId === 48) {
      // this month till now
      customCaseSection.value = []
      customCaseSection.value.push(
        moment
          .tz(timeZone)
          .startOf('month')
          .valueOf()
      )
      customCaseSection.value.push(moment.tz(timeZone).valueOf())
    } else if (customCaseSection.operatorId === 46) {
      // this year till now
      customCaseSection.value = []
      customCaseSection.value.push(
        moment
          .tz(timeZone)
          .startOf('year')
          .valueOf()
      )
      customCaseSection.value.push(moment.tz(timeZone).valueOf())
    } else if (customCaseSection.operatorId === 43) {
      // today till now
      customCaseSection.value = []
      customCaseSection.value.push(
        moment
          .tz(timeZone)
          .startOf('day')
          .valueOf()
      )
      customCaseSection.value.push(moment.tz(timeZone).valueOf())
    } else if (customCaseSection.operatorId === 47) {
      // this week till now
      customCaseSection.value = []
      customCaseSection.value.push(
        moment
          .tz(timeZone)
          .startOf('week')
          .valueOf()
      )
      customCaseSection.value.push(moment.tz(timeZone).valueOf())
    } else if (customCaseSection.operatorId === 66) {
      customCaseSection.value = []
      if (
        moment
          .tz(timeZone)
          .startOf('day')
          .valueOf() ===
        moment
          .tz(timeZone)
          .startOf('month')
          .valueOf()
      ) {
        customCaseSection.value.push(
          moment
            .tz(timeZone)
            .subtract(1, 'month')
            .startOf('month')
            .valueOf()
        )
      } else {
        customCaseSection.value.push(
          moment
            .tz(timeZone)
            .startOf('month')
            .valueOf()
        )
      }
      customCaseSection.value.push(
        moment
          .tz(timeZone)
          .subtract(1, 'day')
          .endOf('day')
          .valueOf()
      )
    } else if (
      customCaseSection.operatorId === 51 ||
      customCaseSection.operatorId === 50 ||
      (customCaseSection.operatorId === 49 && customCaseSection.offset < -1)
    ) {
      customCaseSection.value = []
      let tempMoment = moment
        .tz(timeZone)
        .add(customCaseSection.offset, customCaseSection.operationOn)
        .startOf(customCaseSection.operationOn)
        .valueOf()
      customCaseSection.value.push(tempMoment)
      customCaseSection.value.push(
        moment
          .tz(tempMoment, timeZone)
          .add(
            Math.abs(customCaseSection.offset + 1),
            customCaseSection.operationOn
          )
          .endOf(customCaseSection.operationOn)
          .valueOf()
      )
    } else {
      if (customCaseSection.offset === 0 || customCaseSection.offset === -1) {
        customCaseSection.value = []
        let tempMoment = moment
          .tz(timeZone)
          .add(customCaseSection.offset, customCaseSection.operationOn)
          .startOf(customCaseSection.operationOn)
          .valueOf()
        customCaseSection.value.push(tempMoment)
        customCaseSection.value.push(
          moment
            .tz(tempMoment, timeZone)
            .endOf(customCaseSection.operationOn)
            .valueOf()
        )
      } else {
        customCaseSection.value = []
        let tempMoment = moment
          .tz(timeZone)
          .add(customCaseSection.offset + 1, customCaseSection.operationOn)
          .startOf(customCaseSection.operationOn)
          .valueOf()
        customCaseSection.value.push(tempMoment)
        customCaseSection.value.push(
          moment
            .tz(tempMoment, timeZone)
            .add(
              Math.abs(customCaseSection.offset + 1),
              customCaseSection.operationOn
            )
            .endOf(customCaseSection.operationOn)
            .valueOf()
        )
      }
    }
    return customCaseSection
  },

  isValueRequired(operatorId) {
    if (
      operatorId === 42 ||
      operatorId === 49 ||
      operatorId === 50 ||
      operatorId === 51 ||
      operatorId === 70
    ) {
      // last N days/weeks/months/quarters/years
      return true
    } else if (
      operatorId === 62 ||
      operatorId === 63 ||
      operatorId === 64 ||
      operatorId === 65 ||
      operatorId === 67
    ) {
      // custom date/week/month/quarter/year
      return true
    } else if (operatorId === 20) {
      return true
    }
    return false
  },

  getDatePickerObject(id, value, timeZone) {
    if (!timeZone) {
      timeZone = Vue.prototype.$timezone
    }
    if (!this.isValueRequired(id)) {
      value = null
    }
    if (value && typeof value === 'string') {
      if (value.split(',').length > 1) {
        value = value.split(',').map(function(d) {
          return parseInt(d)
        })
      } else {
        if (parseInt(value) > 1000) {
          // millis
          value = [parseInt(value)]
        } else {
          value = parseInt(value)
        }
      }
    }
    let dateObj = {}
    let fromMoment = null
    let toMoment = null
    const dateOperatorsHashMap = this.getDateOperatorsHashMap()
    const { operationOnId, operationOn, operatorId, label } =
      dateOperatorsHashMap[id] ?? {}
    dateObj['operatorId'] = operatorId
    dateObj['operationOnId'] = operationOnId
    dateObj['operationOn'] = operationOn
    dateObj['label'] = label
    if (Array.isArray(value)) {
      if (value.length === 1) {
        fromMoment = moment(value[0])
          .tz(timeZone)
          .startOf(dateObj['operationOn'])
        toMoment = moment(value[0])
          .tz(timeZone)
          .endOf(dateObj['operationOn'])
        dateObj['value'] = [fromMoment.valueOf(), toMoment.valueOf()]
      } else {
        fromMoment = moment(value[0])
        toMoment = moment(value[1])
        dateObj['value'] = value
      }
      let offset = -toMoment.diff(fromMoment, operationOn) - 1
      dateObj['offset'] = offset
    } else if (value === null) {
      if (id === 22 || id === 31 || id === 28 || id === 44 || id === 68) {
        dateObj['offset'] = 0
        dateObj = this.calculateCustomCaseValue(
          dateObj,
          timeZone,
          Vue.prototype.$org
        )
      } else {
        dateObj['offset'] = -1
        dateObj = this.calculateCustomCaseValue(
          dateObj,
          timeZone,
          Vue.prototype.$org
        )
      }
    } else {
      dateObj['offset'] = value * -1
      dateObj = this.calculateCustomCaseValue(
        dateObj,
        timeZone,
        Vue.prototype.$org
      )
    }
    return dateObj
  },
  getDatePickerObjectFromDateAggregation(
    dateAggregation,
    startEndTimeStampString,
    timeZone
  ) {
    let dateOperator = null
    switch (dateAggregation) {
      //Map BMSAggregateOperators to  DateOperators

      case 25: //QUARTERLY
        dateOperator = 67
        break

      case 10: //MONTHLY
        dateOperator = 64
        break
      case 11: //WEEKLY
        dateOperator = 63
        break
      case 12: //DAILY
        dateOperator = 62
        break
      case 20: //HOURLY
        dateOperator = 20
        break

      default:
        throw new Error('invalid DATE AGGREGATION')
    }
    return this.getDatePickerObject(
      dateOperator,
      startEndTimeStampString,
      timeZone
    )
  },
  returnOperator(id) {
    const dateOperators = this.dateOperators() ?? {}
    for (let key in Object.keys(dateOperators)) {
      if (dateOperators[Object.keys(dateOperators)[key]] !== 'Custom') {
        let operationsList = dateOperators[Object.keys(dateOperators)[key]]
        for (let operation in operationsList) {
          let temp = operationsList[operation]
          if (temp.operatorId === id) {
            return temp
          } else {
            continue
          }
        }
      }
    }
    return null
  },
  getOperationFromLabel(search) {
    const dateOperators = this.dateOperators() ?? {}
    for (let key in Object.keys(dateOperators)) {
      let operations = dateOperators[Object.keys(dateOperators)[key]]
      for (let operationIndex in operations) {
        if (
          operations[operationIndex].label
            .split(' ')
            .join('')
            .toLowerCase() === search
        ) {
          return operations[operationIndex]
        } else {
          continue
        }
      }
    }
    return null
  },
  getDate(operatorId, timeZone) {
    let temp = []
    if (this.customCaseSection.id === 48) {
      // this month till now
      temp = []
      temp.push(
        moment
          .tz(timeZone)
          .startOf('month')
          .valueOf()
      )
      temp.push(moment.tz(timeZone).valueOf())
    } else if (this.customCaseSection.id === 46) {
      // this year till now
      temp = []
      temp.push(
        moment
          .tz(timeZone)
          .startOf('year')
          .valueOf()
      )
      temp.push(moment.tz(timeZone).valueOf())
    } else if (this.customCaseSection.id === 43) {
      // today till now
      temp = []
      temp.push(
        moment
          .tz(timeZone)
          .startOf('day')
          .valueOf()
      )
      temp.push(moment.tz(timeZone).valueOf())
    } else if (this.customCaseSection.id === 47) {
      // this week till now
      temp = []
      temp.push(
        moment
          .tz(timeZone)
          .startOf('week')
          .valueOf()
      )
      temp.push(moment.tz(timeZone).valueOf())
    } else {
      temp = []
      let operation = this.returnOperator(operatorId)
      let tempMoment = null
      if (operation.offset < 0) {
        tempMoment = moment
          .tz(timeZone)
          .subtract(Math.abs(operation.offset), operation.operationOn)
          .startOf(operation.operationOn)
          .valueOf()
        temp.push(tempMoment)
      } else {
        let tempMoment = moment
          .tz(timeZone)
          .add(Math.abs(operation.offset), operation.operationOn)
          .valueOf()
        temp.push(tempMoment)
      }
      temp.push(
        moment(tempMoment)
          .endOf(operation.operationOn)
          .valueOf()
      )
    }
    return temp
  },
  getTabFromOperation(operationOnId, offset) {
    if (offset < -1) {
      return 'R'
    } else {
      if (operationOnId === 1) {
        return 'D'
      } else if (operationOnId === 2) {
        return 'W'
      } else if (operationOnId === 3) {
        return 'M'
      } else if (operationOnId === 4) {
        return 'Y'
      } else if (operationOnId === 5) {
        return 'Q'
      } else if (operationOnId === 6) {
        return 'R'
      }
    }
  },
  getLastNMonthsTimeStamp(n) {
    let startTime = moment()
      .subtract(n - 1, 'months')
      .startOf('month')
      .valueOf()
    let endTime = moment()
      .endOf('month')
      .valueOf()
    return {
      value: [startTime, endTime],
    }
  },
}
