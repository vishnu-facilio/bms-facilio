import Vue from 'vue'
import moment from 'moment-timezone'

export function lowerCase(str) {
  if (str) {
    return str.toLowerCase()
  }
  return str
}

export function upperCase(str) {
  if (str) {
    return str.toUpperCase()
  }
  return str
}

export function firstUpperCase(str) {
  if (str) {
    return str.charAt(0).toUpperCase() + str.slice(1)
  }
  return str
}

export function pascalCase(str) {
  if (str) {
    return str
      .replace('_', ' ')
      .replace(/\w+/g, w => w[0].toUpperCase() + w.slice(1).toLowerCase())
  }
  return str
}

export function singularize(str) {
  if (str) {
    return str.replace(/s$/, '')
  }
  return str
}

export function splitGet(str, splitKey, index) {
  if (str) {
    return str.split(splitKey)[index]
  }
  return str
}

export function reverseText(str) {
  if (str) {
    return str
      .split('')
      .reverse()
      .join('')
  }
  return str
}

export function positive(num) {
  let val = 0
  if (typeof num === 'number') {
    val = num
  } else if (typeof num === 'string') {
    try {
      val = parseInt(num)
    } catch (e) {
      return num
    }
  }
  return val < 0 ? 0 : val
}

export function fromNow(date) {
  return moment(date)
    .tz(Vue.prototype.$timezone)
    .fromNow()
}

export function formatDate(date, excludeTime, onlyTime) {
  let dateformat = Vue.prototype.$dateformat
  let timeformat = Vue.prototype.$timeformat
  // let orgformatArray = orgformat.match(/DD-MMM-YYYY (HH:mm)/)
  if (onlyTime) {
    return moment(date)
      .tz(Vue.prototype.$timezone)
      .format(timeformat)
  } else if (excludeTime) {
    return moment(date)
      .tz(Vue.prototype.$timezone)
      .format(dateformat)
  } else {
    return moment(date)
      .tz(Vue.prototype.$timezone)
      .format(dateformat + ' ' + timeformat)
  }
}

export function prevHour(date, index) {
  return moment(date)
    .tz(Vue.prototype.$timezone)
    .subtract(index || 0, 'hour')
    .startOf('hour')
    .valueOf()
}
export function nextHour(date, index) {
  return moment(date)
    .tz(Vue.prototype.$timezone)
    .add(index || 0, 'hour')
    .startOf('hour')
    .valueOf()
}
export function formatPeriod(dateRange, excludeTime, onlyTime) {
  let dateformat = Vue.prototype.$dateformat
  let timeformat = Vue.prototype.$timeformat

  let date1 = null
  let date2 = null
  if (onlyTime) {
    date1 = moment(dateRange[0])
      .tz(Vue.prototype.$timezone)
      .format(timeformat)
    date2 = moment(dateRange[1])
      .tz(Vue.prototype.$timezone)
      .format(timeformat)
  } else if (excludeTime) {
    date1 = moment(dateRange[0])
      .tz(Vue.prototype.$timezone)
      .format(dateformat)
    date2 = moment(dateRange[1])
      .tz(Vue.prototype.$timezone)
      .format(dateformat)
  } else {
    date1 = moment(dateRange[0])
      .tz(Vue.prototype.$timezone)
      .format(Vue.prototype.$dateformat)
    date2 = moment(dateRange[1])
      .tz(Vue.prototype.$timezone)
      .format(Vue.prototype.$dateformat)
  }

  if (date1 === date2) {
    return date1
  } else {
    return date1 + ' - ' + date2
  }
}

export function toDateFormat(date, format) {
  return moment(date)
    .tz(Vue.prototype.$timezone)
    .format(format)
}

export function monthEndTime(date) {
  return moment(date)
    .tz(Vue.prototype.$timezone)
    .endOf('month')
    .unix()
}

export function yearEndTime(date) {
  return moment(date)
    .tz(Vue.prototype.$timezone)
    .endOf('year')
    .unix()
}

export function getTime(date) {
  return moment(date)
    .tz(Vue.prototype.$timezone)
    .unix()
}

export function getTimeInmillis(date) {
  return moment(date)
    .tz(Vue.prototype.$timezone)
    .valueOf()
}

export function now() {
  return moment()
    .tz(Vue.prototype.$timezone)
    .valueOf()
}

export function startOfHour(date) {
  return moment(date)
    .tz(Vue.prototype.$timezone)
    .startOf('hour')
    .valueOf()
}
export function startOfDay(date) {
  return moment(date)
    .startOf('day')
    .valueOf()
}

export function endOfDay(date) {
  return moment(date)
    .endOf('day')
    .valueOf()
}
export function startOfDayIntimezone(date) {
  return moment(date)
    .tz(Vue.prototype.$timezone)
    .startOf('day')
    .valueOf()
}

export function endOfDayIntimezone(date) {
  return moment(date)
    .tz(Vue.prototype.$timezone)
    .endOf('day')
    .valueOf()
}
export function addDays(date, days, asDate) {
  let formattedDate = moment(date)
    .tz(Vue.prototype.$timezone)
    .add(days, 'days')
    .format('DD-MMM-YYYY')

  if (asDate) {
    return formattedDate
  }
  return formattedDate.valueOf()
}

export function monthName(num) {
  return moment(num, 'M').format('MMMM')
}

export function options(jsonobj, labelKey, valueKey) {
  let array = []
  if (labelKey && valueKey) {
    for (let key in jsonobj) {
      array.push({
        label: jsonobj[key][labelKey],
        value: jsonobj[key][valueKey],
      })
    }
  } else {
    for (let jsonkey in jsonobj) {
      let val = jsonobj[jsonkey]
      array.push({ label: val, value: parseInt(jsonkey) })
    }
  }
  return array
}

export function round(value, decimals = 2) {
  return value || value === 0
    ? Number(Math.round(value + 'e' + decimals) + 'e-' + decimals).toFixed(
        decimals
      )
    : ''
}

export function prettyAddress(addressObj) {
  let addressStr = ''
  if (addressObj) {
    if (addressObj.name) {
      addressStr += addressObj.name + ', '
    }
    if (addressObj.street) {
      addressStr += addressObj.street + ', '
    }
    if (addressObj.city) {
      addressStr += addressObj.city + ', '
    }
    if (addressObj.state) {
      addressStr += addressObj.state + ', '
    }
    if (addressObj.country) {
      addressStr += addressObj.country
    }
    if (addressObj.zip) {
      addressStr += ' - ' + addressObj.zip
    }
  } else {
    addressStr = '---'
  }
  return addressStr
}
export function weekly(period) {
  switch (period) {
    case 'month': {
      let year = moment().year()
      return moment(new Date()).format('MMMM') + ' ' + year
    }
    case 'week': {
      let thisWeekStart = moment().startOf('week')
      let thisWeekEnd = moment(new Date())
      return (
        moment(thisWeekStart).format('DD') +
        ' - ' +
        moment(thisWeekEnd).format('DD MMM YYYY')
      )
    }
    case 'day':
      return moment(new Date()).format('DD MMM YYYY')
  }
}

export function prettyBytes(bytes) {
  const UNITS = ['B', 'kB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB']

  if (!Number.isFinite(bytes)) {
    return null
  }

  const neg = bytes < 0

  if (neg) {
    bytes = -bytes
  }

  if (bytes < 1) {
    return (neg ? '-' : '') + bytes + ' B'
  }

  const exponent = Math.min(Math.floor(Math.log10(bytes) / 3), UNITS.length - 1)
  const numStr = Number((bytes / Math.pow(1000, exponent)).toPrecision(3))
  const unit = UNITS[exponent]

  return (neg ? '-' : '') + numStr + ' ' + unit
}
