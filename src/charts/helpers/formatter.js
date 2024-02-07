import * as d3 from 'd3'
import common from '../helpers/common'
import moment from 'moment-timezone'
import Vue from 'vue'
import { isEmpty, isNumber } from '@facilio/utils/validation'
import isString from 'lodash/isString'
export default {
  getDateFormatConfig(axisObj) {
    let formatConfig = null
    if (axisObj.operation === 'minute') {
      formatConfig = {
        axis: 'HH:mm',
        tooltip: 'llll',
      }
    } else if (
      axisObj.operation === 'hoursofdayonly' ||
      axisObj.operation === 'hoursofday'
    ) {
      let minor = 'ha'
      let major = 'MMM D'
      if (axisObj.filterName === 'W') {
        minor = 'ddd'
        major = ''
      } else if (axisObj.filterName === 'M') {
        minor = 'D'
        major = 'MMM'
      }
      formatConfig = {
        period: 'hour',
        interval: 'hours',
        minor: minor,
        major: major,
        axis: 'MM/DD HH',
        tooltip: 'ddd, MMM D, YYYY h a',
      }
    } else if (
      axisObj.operation === 'daysofmonth' ||
      axisObj.operation === 'fulldate'
    ) {
      let minor = 'D'
      let major = 'MMM'
      if (axisObj.filterName === 'W') {
        minor = 'ddd'
        major = ''
      }
      formatConfig = {
        period: 'day',
        interval: 'days',
        minor: minor,
        major: major,
        axis: 'DD',
        tooltip: 'dddd, MMMM DD, YYYY',
      }
    } else if (axisObj.operation === 'weekday') {
      formatConfig = {
        period: 'day',
        interval: 'days',
        minor: 'ddd',
        major: '',
        axis: 'DD',
        tooltip: 'dddd, MMMM DD, YYYY',
      }
    } else if (
      axisObj.operation === 'dateandtime' ||
      axisObj.operation === 'actual'
    ) {
      formatConfig = {
        axis: 'HH:mm',
        tooltip: 'llll',
      }
    } else if (
      axisObj.operation === 'weekandyear' ||
      axisObj.operation === 'week'
    ) {
      formatConfig = {
        period: 'week',
        interval: 'weeks',
        minor: 'WW',
        major: 'YYYY',
        axis: 'WW YYYY',
        tooltip: 'WW YYYY',
      }
    } else if (
      axisObj.operation === 'monthandyear' ||
      axisObj.operation === 'month'
    ) {
      formatConfig = {
        period: 'month',
        interval: 'months',
        minor: 'MMM',
        major: 'YYYY',
        axis: 'MMMM YYYY',
        tooltip: 'MMMM YYYY',
      }
    } else if (axisObj.operation === 'year') {
      formatConfig = {
        period: 'year',
        interval: 'years',
        minor: 'YYYY',
        major: '',
        axis: 'YYYY',
        tooltip: 'YYYY',
      }
    } else {
      formatConfig = {
        period: 'day',
        interval: 'days',
        minor: 'D',
        major: 'MMM',
        axis: 'DD',
        tooltip: 'dddd, MMMM DD, YYYY',
      }
    }
    if (axisObj.is_highres_data) {
      formatConfig.tooltip = 'LLLL'
    }
    return formatConfig
  },
  multiFormat(date) {
    let mFormatMinute = 'hh:mm a',
      mFormatHour = 'HH a',
      mFormatDay = 'ddd DD',
      mFormatWeek = 'MMM DD',
      mFormatMonth = 'MMMM',
      mFormatYear = 'YYYY'
    let axisMultiFormat =
      d3.timeHour(date) < date
        ? mFormatMinute
        : d3.timeDay(date) < date
        ? mFormatHour
        : d3.timeMonth(date) < date
        ? d3.timeWeek(date) < date
          ? mFormatDay
          : mFormatWeek
        : d3.timeYear(date) < date
        ? mFormatMonth
        : mFormatYear
    return moment(date)
      .tz(Vue.prototype.$timezone)
      .format(axisMultiFormat)
  },
  formatAxis(d3Axis, axisObj) {
    if (axisObj.datatype === 'number') {
      d3Axis.tickFormat(this._formatNumber)
    } else if (axisObj.datatype === 'decimal') {
      d3Axis.tickFormat(this._formatDecimal)
    } else if (axisObj.datatype === 'currency') {
      d3Axis.tickFormat(this._formatCurrency)
    } else if (axisObj.datatype === 'percentage') {
      d3Axis.tickFormat(this._formatPercentage)
    } else if (
      axisObj.datatype === 'date_time' ||
      axisObj.datatype === 'date'
    ) {
      let formatConfig = this.getDateFormatConfig(axisObj)
      d3Axis.tickFormat(function(d) {
        return moment(d)
          .tz(Vue.prototype.$timezone)
          .format(formatConfig.axis)
      })
    } else {
      // by default text datatype
      // this._formatText(chartContext.getLayout().width)
    }
  },
  formatCardValue(value, unit, lUnit) {
    if (value && unit && lUnit) {
      value = Number(value)
      if (unit === Vue.prototype.$currency) {
        value = parseInt(value)
        value = d3.format(',')(value)
      } else if (lUnit.toLowerCase() === 'kwh' && value > 1000) {
        value = value / 1000
        value = value.toFixed(2)
        unit = ' MWh'
      } else if (lUnit.toLowerCase() === 'kwh') {
        value = value.toFixed(2)
      } else if (lUnit.toLowerCase() === 'kg' && value > 999) {
        value = value / 1000
        value = value.toFixed(2)
        unit = ' Tons'
      } else if (lUnit.toLowerCase() === 'kg') {
        value = value.toFixed(2)
      }
    }
    if (value !== null || value + '' !== '') {
      return {
        value: value,
        unit: unit,
      }
    } else {
      return {
        value: '---',
        unit: '',
      }
    }
  },
  formatCardTime(time, xAgg, operatorId) {
    if (time && xAgg !== null && operatorId) {
      if (xAgg === 20) {
        return moment(time)
          .tz(Vue.prototype.$timezone)
          .format('hh:mm A')
      } else if (xAgg === 12) {
        if (operatorId === 31) {
          return moment(time)
            .tz(Vue.prototype.$timezone)
            .format('ddd')
        } else if (operatorId === 30) {
          return moment(time)
            .tz(Vue.prototype.$timezone)
            .format('ddd')
        } else {
          return moment(time)
            .tz(Vue.prototype.$timezone)
            .format('MMM DD')
        }
      } else if (xAgg === 0) {
        return moment(time)
          .tz(Vue.prototype.$timezone)
          .format('hh:mm A')
      } else if (xAgg === 10) {
        return moment(time)
          .tz(Vue.prototype.$timezone)
          .format('MMM')
      } else {
        return moment(time)
          .tz(Vue.prototype.$timezone)
          .format('hh:mm A')
      }
    }
  },
  formatValue(value, axisObj, returnType = 'string') {
    if (axisObj.datatype === 'number') {
      if (axisObj.unit === 'minute') {
        return returnType === 'json'
          ? {
              value: d3.format('.2f')(value),
              unit: 'min',
            }
          : d3.format('.2f')(value) + ' min'
      } else if (axisObj.unit === 'Hrs') {
        return returnType === 'json'
          ? {
              value: d3.format('.2f')(value),
              unit: 'Hrs',
            }
          : d3.format('.2f')(value) + ' Hrs'
      } else {
        return returnType === 'json'
          ? {
              value: this._formatNumber(value),
              unit: ' ',
            }
          : this._formatNumber(value)
      }
    } else if (axisObj.datatype === 'celsius') {
      return returnType === 'json'
        ? {
            value: value,
            unit: 'C',
          }
        : value + 'C'
    } else if (axisObj.datatype === 'decimal') {
      if (axisObj.unit && axisObj.unit.toLowerCase() === 'kwh') {
        if (value > 10000) {
          return returnType === 'json'
            ? {
                value: d3.format('.2f')(value / 1000),
                unit: 'MWh',
              }
            : d3.format('.2f')(value / 1000) + ' MWh'
        } else {
          return returnType === 'json'
            ? {
                value: d3.format('.2f')(value),
                unit: 'kWh',
              }
            : d3.format('.2f')(value) + ' kWh'
        }
      } else if (axisObj.unit && axisObj.unit.toLowerCase() === 'kw') {
        return returnType === 'json'
          ? {
              value: d3.format('.2f')(value),
              unit: 'kw',
            }
          : d3.format('.2f')(value) + ' kw'
      } else if (axisObj.unit && axisObj.unit.toLowerCase() === 'eui') {
        return returnType === 'json'
          ? {
              value: d3.format('.2f')(value),
              unit: 'kw',
            }
          : d3.format('.2f')(value) +
              (Vue.prototype.$account &&
              this.formatCurrency.$account.org.id === 78
                ? 'kWh/m&sup2'
                : 'kWh/ft&sup2')
      } else if (axisObj.unit && axisObj.unit.toLowerCase() === 'aed') {
        return returnType === 'json'
          ? {
              value: d3.format(',.2f')(value),
              unit: ' AED',
            }
          : d3.format(',.2f')(value) + ' AED'
      } else {
        return returnType === 'json'
          ? {
              value: d3.format('.2f')(value),
              unit: axisObj.unit ? axisObj.unit : '',
            }
          : d3.format('.2f')(value) + (axisObj.unit ? ' ' + axisObj.unit : '')
      }
    } else if (axisObj.datatype === 'currency') {
      // return d3.format('($.2f')(value)
      return returnType === 'json'
        ? {
            value:
              Vue.prototype.$currency +
              ' ' +
              d3.format(',.2f')(Math.round(value)),
            unit: ' ',
          }
        : Vue.prototype.$currency + ' ' + d3.format(',.2f')(Math.round(value))
    } else if (axisObj.datatype === 'percentage') {
      return returnType === 'json'
        ? {
            value: this._formatPercentage(value),
            unit: ' ',
          }
        : this._formatPercentage(value)
    } else if (
      axisObj.datatype === 'date_time' ||
      axisObj.datatype === 'date'
    ) {
      let formatConfig = this.getDateFormatConfig(axisObj)
      return moment(value)
        .tz(Vue.prototype.$timezone)
        .format(formatConfig.tooltip)
    } else if (axisObj.datatype === 'string' && axisObj.unit === 'Hrs') {
      let data = Number(d3.format('.2f')(value))
      if (data > 0.0) {
        return returnType === 'json'
          ? {
              value: data,
              unit: axisObj.unit,
            }
          : data + ' ' + axisObj.unit
      } else {
        return returnType === 'json'
          ? {
              value: d3.format('.3f')(value),
              unit: axisObj.unit,
            }
          : d3.format('.3f')(value) + ' ' + axisObj.unit
      }
    } else {
      return returnType === 'json'
        ? {
            value: value,
            unit: '',
          }
        : value
      // by default text datatype
      // this._formatText(chartContext.getLayout().width)
    }
  },

  formatValueForTable(value, axisObj) {
    if (axisObj.datatype === 'number') {
      if (axisObj.unit === 'minute') {
        return d3.format('.2f')(value)
      } else {
        return this._formatNumber(value)
      }
    } else if (axisObj.datatype === 'celsius') {
      return value
    } else if (axisObj.datatype === 'decimal') {
      if (axisObj.unit && axisObj.unit.toLowerCase() === 'aed') {
        return d3.format(',.2f')(value)
      }
      return d3.format('.2f')(value)
    } else if (axisObj.datatype === 'currency') {
      return d3.format(',.2f')(value)
    } else if (axisObj.datatype === 'percentage') {
      return this._formatPercentage(value)
    } else if (
      axisObj.datatype === 'date_time' ||
      axisObj.datatype === 'date'
    ) {
      let formatConfig = this.getDateFormatConfig(axisObj)
      return moment(value)
        .tz(Vue.prototype.$timezone)
        .format(formatConfig.tooltip)
    } else if (axisObj.datatype === 'string' && axisObj.unit === 'Hrs') {
      let data = d3.format('.2f')(value)
      if (data > 0.0) {
        return data
      } else {
        return d3.format('.3f')(value)
      }
    } else {
      return value
    }
  },

  _formatUnitType(unitType, value) {
    if (!unitType.unit) {
      return value
    } else if (unitType.unit === 'hour') {
      return common.convertToMins(value)
    } else if (unitType.operation) {
      return this.formatAxis(unitType, value)
    }
  },

  _formatNumber(value) {
    let format = '.2s'
    if (value < 10) {
      format = ''
    } else if (value < 1000) {
      format = ''
    }
    return d3.format(format)(value)
  },

  _formatDecimal(value) {
    let format = '.2f'
    if (value < 10) {
      format = '.2f'
    } else if (value < 100) {
      format = '.1f'
    }
    return d3.format(format)(value)
  },

  _formatCurrency(value) {
    // return d3.format('$,')(value)
    return d3.format('.1s')(value)
  },

  _formatPercentage(value) {
    return d3.format('.0%')(value)
  },

  _formatDate(value) {
    return d3.timeFormat('%Y-%m-%dd')
  },

  _formatText() {},
}
export const formatDecimal = (value, config) => {
  let format = '.2f'
  if (value > 1000) {
    return d3.format(',')(value)
  } else {
    if (value < 10) {
      format = '.2f'
    } else if (value < 100) {
      format = '.1f'
    }
    return d3.format(format)(value)
  }
}
export const formatCardDecimal = (value, config) => {
  let format = '.2s'
  if (value > 1000) {
    return parseInt(value)
  } else {
    if (value < 10) {
      format = '.2f'
    } else if (value < 100) {
      format = '.2f'
    }
    return d3.format(format)(value)
  }
}
export const formatCurrency = (value, decimalPoints) => {
  let format = '.2f'
  if (!isEmpty(decimalPoints)) {
    format = `,.${decimalPoints}f`
    return d3.format(format)(value)
  }
  if (value > 1000) {
    return d3.format(',')(parseInt(value))
  } else {
    if (value < 10) {
      format = '.2f'
    } else if (value < 100) {
      format = '.1f'
    }
    return d3.format(format)(value)
  }
}
export const formatInterger = (value, config) => {
  if (!isEmpty(value) && isNumber(value)) {
    return parseInt(value)
  }
  return value
}

export const formatDisplayValue = (value, config) => {
  if (!isEmpty(value) && isNumber(value)) {
    if (config && config.formatter) {
      return formatCurrency(value, config)
    }
    return formatDecimal(value, config)
  }
  return value
}

export const formatDuration = (
  value,
  format = 'milliseconds',
  toFormat,
  length = 3,
  actual_unit,
  isDefautlDuration
) => {
  if (actual_unit && isDefautlDuration === undefined) {
    if (!isEmpty(value) && isString(value)) {
      value = value.replace(',', '')
      return (
        Math.floor(parseFloat(value)) + ' ' + getSymbolsForTimeUnit(actual_unit)
      )
    }
  }
  let duration
  let count = 0
  let durationLabel = ''
  if (!isEmpty(value) && isString(value)) {
    let validString = value.replace(',', '')
    duration = moment.duration(parseFloat(validString), format)
  } else {
    duration = moment.duration(parseFloat(value), format)
  }
  if (toFormat) {
    switch (toFormat) {
      case 'Milliseconds':
        durationLabel = d3.format('.2f')(parseFloat(duration.asMilliseconds()))
        return durationLabel <= 1
          ? `${durationLabel} millisecond `
          : `${durationLabel} milliseconds `
      case 'Seconds':
        durationLabel = d3.format('.2f')(parseFloat(duration.asSeconds()))
        return durationLabel <= 1
          ? `${durationLabel} second `
          : `${durationLabel} seconds `
      case 'Minutes':
        durationLabel = d3.format('.2f')(parseFloat(duration.asMinutes()))
        return durationLabel <= 1
          ? `${durationLabel} minute `
          : `${durationLabel} minutes `
      case 'Hours':
        durationLabel = d3.format('.2f')(parseFloat(duration.asHours()))
        return durationLabel <= 1
          ? `${durationLabel} hour `
          : `${durationLabel} hours `
      case 'Days':
        durationLabel = d3.format('.2f')(parseFloat(duration.asDays()))
        return durationLabel <= 1
          ? `${durationLabel} day `
          : `${durationLabel} days `
    }
  }

  let years = parseInt(duration.years())
  let months = parseInt(duration.months())
  let days = parseInt(duration.days())
  let hours = parseInt(duration.hours())
  let minutes = parseInt(duration.minutes())
  let seconds = parseInt(duration.seconds())
  let milliseconds = parseInt(duration.milliseconds())

  if (years > 0 && count < length) {
    durationLabel += years === 1 ? `${years} year ` : `${years} years `
    count++
  }
  if (months > 0 && count < length) {
    durationLabel += months === 1 ? `${months} month ` : `${months} months `
    count++
  }
  if (days > 0 && count < length) {
    durationLabel += days === 1 ? `${days} day ` : `${days} days `
    count++
  }
  if (hours > 0 && count < length) {
    durationLabel += hours === 1 ? `${hours} hour ` : `${hours} hours `
    count++
  }
  if (minutes > 0 && count < length) {
    durationLabel +=
      minutes === 1 ? `${minutes} minute ` : `${minutes} minutes `
    count++
  }
  if (seconds > 0 && count < length) {
    durationLabel +=
      seconds === 1 ? `${seconds} second ` : `${seconds} seconds `
    count++
  }
  if (milliseconds > 0 && count < length) {
    durationLabel +=
      milliseconds === 1
        ? `${milliseconds} millisecond `
        : `${milliseconds} milliseconds `
    count++
  }
  return durationLabel === '' ? '0 seconds' : durationLabel
}

export const getSymbolsForTimeUnit = unit => {
  const symbalsmap = {
    hour: 'h',
    day: 'days',
    minute: 'm',
    week: 'W',
    year: 'Y',
    milisecond: 'ms',
    second: 's',
  }
  const symbalsmaps = {
    hours: 'h',
    days: 'days',
    minutes: 'm',
    weeks: 'W',
    years: 'Y',
    miliseconds: 'ms',
    seconds: 's',
  }
  if (symbalsmap[unit]) {
    return symbalsmap[unit]
  } else if (symbalsmaps[unit]) {
    return symbalsmaps[unit]
  }
}
