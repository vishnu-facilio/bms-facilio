import * as d3 from 'd3'
import defaults from 'src/newcharts/model/format-model'
export default {
  unitValueOfFormat(labelId) {
    let value = null
    switch (labelId) {
      case 0: {
        return value
      }
      case 1: {
        return value
      }
      case 2: {
        value = 1000
        return value
      }
      case 3: {
        value = 1000000
        return value
      }
      case 4: {
        value = 1000000000
        return value
      }
    }
  },
  getFormattedValue(rawValue, dataType, format) {
    if (format !== null && typeof format !== 'undefined') {
      let val = this.unitValueOfFormat(format.unit.labelId)
      format.unit['value'] = val
      let formatString = this.getFormatString(format)
      return this.loadFromObject(formatString, format, rawValue)
    } else {
      let defaultFormat = this.fetchDefaultFormat(dataType)
      let formatString = this.getFormatString(defaultFormat)
      return this.loadFromObject(formatString, defaultFormat, rawValue)
    }
  },
  fetchDefaultFormat(dataType) {
    let defaultFormats = defaults.getDefaultFormat()
    return defaultFormats[dataType.trim().toLowerCase()]
  },
  getFormatString(formatObject) {
    let formatString = ''
    formatString = formatString + formatObject.negation // (mandatory field)
    if ('currency' in formatObject) {
      formatString = formatString + formatObject.currency.value
    }
    formatString = formatString + ',' // thousand separator (mandatory field)
    if (
      formatObject.unit.labelId === 0 ||
      formatObject.unit.labelId === 2 ||
      formatObject.unit.labelId === 3 ||
      formatObject.unit.labelId === 4
    ) {
      formatString = formatString + '.' + formatObject.decimal + 'f'
    } else {
      if (formatObject.decimal === 0) {
        formatString = formatString + '.' + formatObject.decimal + 's'
      } else {
        formatString = formatString + '.' + (formatObject.decimal + 1) + 's'
      }
      // else if (formatObject.decimal % 2 === 1) {
      //   formatString = formatString + '.' + (formatObject.decimal + 1) + 's'
      // }
      // else {
      //   formatString = formatString + '.' + (formatObject.decimal * 2) + 's'
      // }
    }
    return formatString
  },
  getCurrencyList(currencyObj) {
    let temp = []
    if (currencyObj.position) {
      temp.push('')
      temp.push(currencyObj.value)
    } else {
      temp.push(currencyObj.value)
      temp.push('')
    }
    return temp
  },
  loadFromObject(formatString, format, value) {
    let result = null
    let locale = d3.formatLocale({
      decimal:
        typeof format.separator.decimal === 'undefined'
          ? ''
          : format.separator.decimal,
      thousands: format.separator.thousand,
      grouping: [3], // default grouping
      currency:
        typeof format.currency === 'undefined'
          ? [this.$currency, '']
          : this.getCurrencyList(format.currency),
    })
    if (format.unit.labelId === 0) {
      return locale.format(formatString)(value)
    } else if (format.unit.labelId === 1) {
      result = locale.format(formatString)(value)
      if (result.includes('G')) {
        result = result.slice(0, result.length - 2) // d3-format specifies billions with a 'G'
        result = result + 'B'
      }
      return result
    } else {
      let formatFunction = locale.formatPrefix(formatString, format.unit.value)
      result = formatFunction(value)
      if (format.unit.value === 1000000000) {
        result = result.slice(0, result.length - 2) // d3-format specifies billions with a 'G'
        result = result + 'B'
      }
      return result
    }
  },
  formatTime(time, forTick) {
    let value = ''
    let currentValue = time
    if (!currentValue) {
      value = forTick ? '0' : '0 Hour'
    }
    if (currentValue >= 86400000) {
      let days = Math.floor(currentValue / 86400000)
      value = Math.round(days * 100) / 100
      value = forTick ? value : value + ' Day' + (days > 1 ? 's' : '')
      currentValue -= days * 86400000
    }
    if (!forTick) {
      if (currentValue >= 3600000) {
        let hours = Math.floor(currentValue / 3600000)
        if (hours) {
          value += ' '
        }
        value +=
          Math.round(hours * 100) / 100 + ' Hour' + (hours > 1 ? 's' : '')
        currentValue -= hours * 3600000
      }
      if (currentValue >= 60000) {
        let minutes = Math.floor(currentValue / 60000)
        if (minutes) {
          value += ' '
        }
        value +=
          Math.round(minutes * 100) / 100 + ' Minute' + (minutes > 1 ? 's' : '')
      }
    }
    return value
  },
}
