import Vue from 'vue'
import moment from 'moment-timezone'
import store from '../store'
import { isFileObject, isEmpty } from '@facilio/utils/validation'
import { makeObjectNonReactive } from 'util/utility-methods'
import { htmlToText } from '@facilio/utils/filters'
import router from 'src/router'
import countries from 'util/data/countries'
import getProperty from 'dlv'
import setProperty from 'dset'
import { getBaseURL } from 'util/baseUrl'
import { emitEvent } from 'src/webViews/utils/mobileapps'
import { getApp } from '@facilio/router'

export default {
  sendToMobile(data) {
    emitEvent('sendData', JSON.stringify(data))
    if (window.JSReceiver) {
      // window.JSReceiver.sendData(JSON.stringify(data))
      //  emitEvent('sendData', JSON.stringify(data))
    } else {
      document.location.href = 'drilldown://' + JSON.stringify(data)
    }
  },
  nonReactive(object, deep = true) {
    return makeObjectNonReactive(object, deep)
  },
  cloneFromSchema(schema, obj) {
    let value = {}
    Object.keys(schema).forEach(key => {
      if (this.isObject(schema[key]) && this.isObject(obj[key])) {
        value[key] = this.cloneFromSchema(schema[key], obj[key])
      } else {
        if (this.typeOf(obj[key]) !== 'undefined') {
          if (obj[key]) {
            value[key] = obj[key]
          } else {
            value[key] = schema[key]
          }
        }
      }
    })
    let schemaKeys = Object.keys(schema)
    let valKeys = Object.keys(value)

    schemaKeys
      .filter(i => !valKeys.includes(i))
      .forEach(i => {
        value[i] = schema[i]
      })
    return value
  },
  // https://stackoverflow.com/a/46663081/1157445
  isObject(o) {
    return o !== null && typeof o === 'object' && Array.isArray(o) === false
  },
  capitalize(str) {
    return [...str]
      .map((c, i) => (i === 0 ? c.toUpperCase() : c.toLowerCase()))
      .join('')
  },
  pluralize(n, label) {
    return n + ' ' + (n === 1 ? label : label + 's')
  },
  isObjectEqual(a, b) {
    return JSON.stringify(a) === JSON.stringify(b)
  },
  cloneObject(obj) {
    return JSON.parse(JSON.stringify(obj))
  },
  extend(target) {
    let sources = [].slice.call(arguments, 1)
    sources.forEach(function(source) {
      if (source) {
        for (let prop in source) {
          target[prop] = source[prop]
        }
      }
    })
    return target
  },
  compareObject(changedObj, sourceObj) {
    // Simple compare..Doesnt check for nested
    if (sourceObj == null || changedObj == null) {
      return
    }
    let obj = {}
    Object.keys(changedObj).forEach(key => {
      if (sourceObj.hasOwnProperty(key)) {
        if (typeof changedObj[key] === 'object') {
          if (
            JSON.stringify(sourceObj[key]) !== JSON.stringify(changedObj[key])
          ) {
            obj[key] = changedObj[key]
          }
        } else if (sourceObj[key] !== changedObj[key]) {
          obj[key] = changedObj[key]
        }
      } else {
        obj[key] = changedObj[key]
      }
    })
    return obj
  },
  convertToMins(millis) {
    if (millis) {
      let formatted =
        millis / 1000 / 60 > 99
          ? (millis / 1000 / 60 / 60).toFixed(2)
          : (millis / 1000 / 60).toFixed(2)
      let hours = Math.floor(Math.abs(formatted))
      let min = Math.floor((Math.abs(formatted) * 60) % 60)
      let val = (hours > 0 ? hours + 'h ' : '') + min
      return val + 'min'
    } else {
      return '--'
    }
  },
  addTime(date, timeInHoursAndMinutes) {
    let time = timeInHoursAndMinutes.split(':')
    return this.getDateInOrg(date)
      .add(parseInt(time[0]), 'h')
      .add(parseInt(time[1]), 'm')
      .valueOf()
  },
  addPeriod(date, period, value) {
    return this.getDateInOrg(date)
      .add(value, period)
      .valueOf()
  },
  getDateRange(period, num = 0) {
    return [
      moment()
        .subtract(num, period)
        .startOf(period)
        .valueOf(),
      moment()
        .subtract(num, period)
        .endOf(period)
        .valueOf(),
    ]
  },
  /**
   * This method should be used when selecting a date from picker.
   * This returns the date as if its selected in the org.
   * eg: '01-12-2013 08:30 GMT+0530' in india will be returned as '01-12-2013 08:30 GMT+0400' in Dubai Org
   *
   * @param {Date} date
   * @returns {Long} time in millis/seconds
   */
  getTimeInOrg(date, inseconds) {
    if (inseconds) {
      return this.getDateInOrg(date).unix()
    }
    return this.getDateInOrg(date).valueOf()
  },
  /**
   * @param {Date} date
   * @returns Moment date wrapper
   */
  getDateInOrg(date) {
    return moment.getDateInOrg(date)
  },

  getPlaceholders(str) {
    return this.execRegex(str, /(\$\{([^\\:}]*))/g, 2)
  },

  execRegex(str, regex, group = null) {
    let result = []
    let match = regex.exec(str)
    while (match != null) {
      if (group) {
        result.push(match[group])
      } else {
        result.push(match)
      }
      match = regex.exec(str)
    }
    return result
  },
  // TODO needs to test properly
  setFormData(prop, object, formdata) {
    if (typeof object === 'string' || object || object === false) {
      if (Array.isArray(object)) {
        object.forEach((element, index) => {
          this.setFormData(prop + '[' + index + ']', element, formdata)
        })
      } else if (typeof object === 'object') {
        if (isFileObject(object)) {
          if (object instanceof File) {
            formdata.append(prop + 'FileName', object.name)
          }
          formdata.append(prop + 'ContentType', object.type)
          formdata.append(prop, object, object.name)
        } else {
          for (let key in object) {
            if (
              typeof object[key] === 'string' ||
              object[key] ||
              object[key] === false
            ) {
              this.setFormData(prop + '.' + key, object[key], formdata)
            }
          }
        }
      } else {
        formdata.append(prop, object)
      }
    }
  },
  andOperatorOnLong(permVal, totalPermVal) {
    let hi = 0x80000000
    let low = 0x7fffffff
    let hi1 = ~~(permVal / hi)
    let hi2 = ~~(totalPermVal / hi)
    let low1 = permVal & low
    let low2 = totalPermVal & low
    let h = hi1 & hi2
    let l = low1 & low2
    return h * hi + l
  },

  copy(target, source) {
    for (let key in source) {
      if (
        source.hasOwnProperty(key) &&
        target.hasOwnProperty(key) &&
        source[key] &&
        source[key] !== -1
      ) {
        target[key] = source[key]
      }
    }
    return target
  },

  getCurrentModule() {
    let routeObj = this.$route
    let module = null
    let rootPath = null
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
    return {
      module: module,
      rootPath: rootPath,
    }
  },
  setActionText(actions) {
    if (actions) {
      let typeString = []
      actions.forEach(d => {
        if (d.actionType === 3) {
          if (typeString.indexOf('Email') === -1) {
            typeString.push('Email')
          }
        } else if (d.actionType === 4) {
          if (typeString.indexOf('SMS') === -1) {
            typeString.push('SMS')
          }
        } else if (d.actionType === 7) {
          if (typeString.indexOf('Mobile') === -1) {
            typeString.push('Mobile')
          }
        } else if (d.actionType === 11) {
          if (typeString.indexOf('Create WorkOrder') === -1) {
            typeString.push('Create WorkOrder')
          }
        } else if (d.actionType === 17) {
          if (typeString.indexOf('Severity Change') === -1) {
            typeString.push('Severity Change')
          }
        } else if (d.actionType === 12) {
          if (typeString.indexOf('Close WorkOrder') === -1) {
            typeString.push('Close WorkOrder')
          }
        } else if (d.actionType === 13) {
          if (typeString.indexOf('Field Change') === -1) {
            typeString.push('Field Change')
          }
        } else if (d.actionType === 18) {
          if (typeString.indexOf('Control') === -1) {
            typeString.push('Control')
          }
        } else if (d.actionType === 21) {
          if (typeString.indexOf('Script') === -1) {
            typeString.push('Script')
          }
        }
      })
      return typeString.sort()
    } else {
      return '---'
    }
  },

  percent(total, value) {
    value = value < 0 ? 0 : value
    total = total < 0 ? 0 : total
    let percentage = (value / total) * 100
    if (isNaN(percentage)) {
      return 0
    } else {
      return percentage
    }
  },
  openInMap(lat, lng) {
    window.open(
      'https://www.google.com/maps/search/?api=1&query=' + lat + ',' + lng,
      '_blank'
    )
  },
  openInMapForLatLngStr(latlng) {
    window.open(
      'https://www.google.com/maps/search/?api=1&query=' + latlng,
      '_blank'
    )
  },
  weekly(period) {
    period = period.toLowerCase()
    switch (period) {
      case 'month': {
        let thismonth1 = moment().year()
        let weekly = moment(new Date()).format('MMMM') + ' ' + thismonth1
        return weekly
      }
      case 'fullmonth': {
        let thismonth = moment().year()
        let lastmonth = moment(new Date())
          .subtract(1, 'year')
          .format('YYYY')
        let weekly1 =
          moment(new Date()).format('MMMM') +
          ' ' +
          lastmonth +
          ' vs ' +
          moment(new Date()).format('MMMM') +
          ' ' +
          thismonth
        return weekly1
      }
      case 'week': {
        let thisWeekStart = moment().startOf('week')
        let thisWeekEnd = moment().endOf('week')
        return (
          moment(thisWeekStart).format('DD') +
          ' - ' +
          moment(thisWeekEnd).format('DD MMM YYYY')
        )
      }
      case 'day':
        return moment(new Date()).format('DD MMM YYYY')
      case 'yearcompare': {
        let thisyear = moment(new Date()).format('YYYY')
        let lastyear = moment(new Date())
          .subtract(1, 'year')
          .format('YYYY')
        return lastyear + ' vs ' + thisyear
      }
      case 'year':
        return moment(new Date()).format('YYYY')
      case 'lastyear':
        return moment(new Date())
          .subtract(1, 'year')
          .format('YYYY')
      case 'current week': {
        let firstDay = moment().startOf('week')
        let endDay = moment().endOf('week')
        return (
          moment(firstDay).format('DD') +
          ' - ' +
          moment(endDay).format('DD MMM YYYY')
        )
      }
      case 'last week': {
        let lastWeekStart = moment()
          .subtract(1, 'weeks')
          .startOf('week')
        let lastWeekEnd = moment()
          .subtract(1, 'weeks')
          .endOf('week')
        return (
          moment(lastWeekStart).format('DD') +
          ' - ' +
          moment(lastWeekEnd).format('DD MMM YYYY')
        )
      }
      case 'today':
        return moment(new Date()).format('DD MMM YYYY')
      case 'current_week': {
        let firstDaya = moment().startOf('week')
        let endDaya = moment().endOf('week')
        return (
          moment(firstDaya).format('DD') +
          ' - ' +
          moment(endDaya).format('DD MMM YYYY')
        )
      }
      case 'last_week': {
        let lastWeekStarts = moment()
          .subtract(1, 'weeks')
          .startOf('week')
        let lastWeekEnds = moment()
          .subtract(1, 'weeks')
          .endOf('week')
        return (
          moment(lastWeekStarts).format('DD') +
          ' - ' +
          moment(lastWeekEnds).format('DD MMM YYYY')
        )
      }
    }
    return period
  },
  weeklys(period) {
    switch (period) {
      case 'current_week': {
        let firstDaya = moment().startOf('week')
        let endDaya = moment().endOf('week')
        return (
          moment(firstDaya).format('DD') +
          ' - ' +
          moment(endDaya).format('DD MMM YYYY')
        )
      }
      case 'last_week': {
        let lastWeekStarts = moment()
          .subtract(1, 'weeks')
          .startOf('week')
        let lastWeekEnds = moment()
          .subtract(1, 'weeks')
          .endOf('week')
        return (
          moment(lastWeekStarts).format('DD') +
          ' - ' +
          moment(lastWeekEnds).format('DD MMM YYYY')
        )
      }
    }
    return period
  },
  debounce(callback, wait, immediate = false) {
    let timeout = null

    return function() {
      const callNow = immediate && !timeout
      const next = () => callback.apply(this, arguments)

      clearTimeout(timeout)
      timeout = setTimeout(next, wait)

      if (callNow) {
        next()
      }
    }
  },

  delay(ms) {
    return new Promise(resolve => setTimeout(resolve, ms))
  },

  arrowHandle: function(varience) {
    if (varience < 0) {
      return {
        color: '#75bd2b',
        direction: require('statics/energy/arrow-pointing-down.svg'),
      }
    } else if (varience > 0) {
      return {
        color: '#d66c6c',
        direction: require('statics/energy/arrow-up.svg'),
      }
    } else if (varience === 0) {
      return {
        color: '#d66c6c',
        direction: require('statics/energy/arrow-up.svg'),
      }
    }
  },
  arrowHandleClass: function(varience) {
    if (varience < 0) {
      return 'fa fa-arrow-down'
    } else if (varience > 0) {
      return 'fa fa-arrow-up'
    } else if (varience === 0) {
      return 'fa fa-arrow-up'
    }
  },
  daysHoursMinuToSec(dateObject) {
    let interval = null
    if (dateObject.days) {
      interval = dateObject.days * 86400
    }
    if (dateObject.minute) {
      interval += dateObject.minute * 60
    }
    if (dateObject.hours) {
      interval += dateObject.hours * 3600
    }
    return interval
  },
  secTodaysHoursMinu(second) {
    let dateObject = {}

    let seconds = parseInt(second, 10)
    let days = Math.floor(seconds / 86400)
    seconds -= days * 86400
    let hrs = Math.floor(seconds / 3600)
    seconds -= hrs * 3600
    let mnts = Math.floor(seconds / 60)
    seconds -= mnts * 60

    dateObject.days = days > 0 ? days : null
    dateObject.hours = hrs
    dateObject.minute = mnts
    dateObject.seconds = seconds > 0 ? seconds : 0

    return dateObject
  },

  secToHoursMinu(second) {
    let dateObject = {}
    let seconds = parseInt(second, 10)
    let hrs = Math.floor(seconds / 3600)
    seconds -= hrs * 3600
    let mnts = Math.floor(seconds / 60)
    seconds -= mnts * 60

    dateObject.hours = hrs
    dateObject.minute = mnts
    dateObject.seconds = seconds > 0 ? seconds : 0

    return dateObject
  },

  getFormattedDuration(
    value,
    format = 'milliseconds',
    skipDurationCreation = false
  ) {
    if (!value) return '00:00 Hrs'
    let duration

    if (skipDurationCreation) {
      duration = value
    } else {
      duration = moment.duration(parseInt(value, 10), format)
    }

    let days = parseInt(duration.asDays(), 10)
    let hours = duration.hours()
    let minutes = duration.minutes()
    let seconds = duration.seconds()

    if (days > 0) {
      if (days === 1) {
        return hours ? `${days} Day ${hours} Hrs` : `${days} Day`
      }
      return hours ? `${days} Days ${hours} Hrs` : `${days} Days`
    } else if (hours > 0) {
      return minutes ? `${hours} Hrs ${minutes} Mins` : `${hours} Hrs`
    } else if (minutes > 0) {
      return seconds ? `${minutes} Mins ${seconds} Secs` : `${minutes} Mins`
    } else {
      return `${seconds} Secs`
    }
  },
  getFormattedDurationSeconds(
    value,
    format = 'seconds',
    skipDurationCreation = false
  ) {
    if (!value) return '00:00 Hrs'
    let duration

    if (skipDurationCreation) {
      duration = value
    } else {
      duration = moment.duration(parseInt(value, 10), format)
    }

    let days = parseInt(duration.asDays(), 10)
    let hours = duration.hours()
    let minutes = duration.minutes()
    let seconds = duration.seconds()

    if (days > 0) {
      if (days === 1) {
        return hours ? `${days} Day ${hours} Hrs` : `${days} Day`
      }
      return hours ? `${days} Days ${hours} Hrs` : `${days} Days`
    } else if (hours > 0) {
      return minutes ? `${hours} Hrs ${minutes} Mins` : `${hours} Hrs`
    } else if (minutes > 0) {
      return seconds ? `${minutes} Mins ${seconds} Secs` : `${minutes} Mins`
    } else {
      return `${seconds} Secs`
    }
  },

  sortData(prop, data) {
    if (prop && data && data.length) {
      data.sort(this.dynamicSort(prop))
    }
    return data
  },
  dynamicSort(property) {
    let sortOrder = 1

    if (property[0] === '-') {
      sortOrder = -1
      property = property.substr(1)
    }

    return function(a, b) {
      if (sortOrder == -1) {
        return b[property].localeCompare(a[property])
      } else {
        return a[property].localeCompare(b[property])
      }
    }
  },
  objectOfArrayToArray(obj, groupBy) {
    if (obj) {
      let data = []
      if (groupBy && groupBy === 'key') {
        Object.keys(obj).forEach(rt => {
          data.push(rt)
        })
      } else {
        Object.values(obj).forEach(rt => {
          if (Array.isArray(rt)) {
            rt.forEach(rl => {
              if (data.findIndex(rx => rx === rl) === -1) {
                data.push(rl)
              }
            })
          } else {
            Object.values(rt).forEach(rl => {
              if (data.findIndex(rx => rx === rl) === -1) {
                data.push(rl)
              }
            })
          }
        })
      }
      return data
    }
    return []
  },
  criteriaToFilters(criteria) {
    let filters = {}
    let { conditions } = criteria || {}
    Object.values(conditions || []).forEach(condition => {
      if (!isEmpty(condition)) {
        let { operatorId, criteriaValue } = condition
        if (operatorId === 35 && !isEmpty(criteriaValue)) {
          this.constructOneLevelFilterObject(filters, condition)
        } else {
          this.constructFilterObject(filters, condition)
        }
      }
    })
    return filters
  },
  constructFilterObject(filters, condition) {
    let { value, valueArray, fieldName, operatorId: id } = condition
    if (!isEmpty(fieldName)) {
      filters[fieldName] = {
        operatorId: id,
      }
      if (value || valueArray) {
        filters[fieldName].value = valueArray
          ? valueArray
          : this.separatedToArray(value)
      }
    }
  },
  constructOneLevelFilterObject(filters, condition) {
    let { oneLevelLookup } = filters
    let lookUpFilterObject = oneLevelLookup || {}
    let { fieldName, criteriaValue } = condition || {}
    if (!isEmpty(fieldName)) {
      let fieldNameList = Object.keys(lookUpFilterObject)
      let filterObject = {
        operatorId: 35,
      }
      filterObject.criteriaValue = this.criteriaToFilters(criteriaValue)
      if (fieldNameList.includes(fieldName)) {
        let filterArray =
          getProperty(filters, `oneLevelLookup.${fieldName}`) || []
        filterArray.push(filterObject)
        setProperty(filters, `oneLevelLookup.${fieldName}`, filterArray)
      } else {
        lookUpFilterObject[fieldName] = []
        lookUpFilterObject[fieldName].push(filterObject)
        filters.oneLevelLookup = lookUpFilterObject
      }
    }
  },
  formatFilter(filters) {
    let filter = {}
    if (filters) {
      Object.keys(filters).forEach(ftr => {
        if (Array.isArray(filters[ftr])) {
          if (filters[ftr].length) {
            filter[ftr] = {
              operatorId: filters[ftr][0].operatorId,
            }
            if (
              filters[ftr]
                .map(rt => rt.value + '')
                .filter(rt => rt !== 'undefined')
            ) {
              filter[ftr].value = filters[ftr]
                .map(rt => rt.value + '')
                .filter(rt => rt !== 'undefined')
            }
          }
        } else if (ftr === 'drillDownPattern') {
          filter[ftr] = filters[ftr]
        } else {
          filter[ftr] = {
            operatorId: filters[ftr].operatorId,
          }
          if (filters[ftr].value) {
            filter[ftr].value = filters[ftr].value
          }
        }
      })
    }
    return filter
  },
  isValueSeparated(value) {
    if (value.indexOf(',') === -1) {
      return false
    }
    return true
  },
  separatedToArray(value) {
    if (this.isValueSeparated(value)) {
      return value.split(',').filter(rl => rl)
    }
    return [value]
  },
  expressionsToFilters(expressions) {
    if (expressions) {
      let filters = this.criteriaToFilters(expressions[0].criteria)
      return {
        filters: filters,
        moduleName: expressions[0].moduleName,
      }
    }
    return {}
  },
  isEtisalat() {
    return this.isLicenseEnabled('ETISALAT')
  },
  isLicenseEnabled(module, mobileLicense) {
    if (module.split(',').length > 1) {
      let moduleList = module.split(',')
      for (let mod of moduleList) {
        if (this.isLicenseEnabled(mod, mobileLicense)) {
          return true
        }
      }
      return false
    }

    if (Vue.prototype.$mobile && !mobileLicense) {
      return true
    } else {
      let { features } = store.state
      return (features || {})[module.toUpperCase()]
    }
  },
  unitValueString(field, value) {
    if (field.unitEnum && field.unit) {
      if (field.unitEnum.isLeft) {
        return field.unit + ' ' + value
      } else {
        return value + ' ' + field.unit
      }
    }
    return value
  },
  getAlarmSeverity(id, newColor) {
    if (id && typeof id === 'string') {
      id = parseInt(id)
    }
    let colorMap = {}
    if (newColor) {
      colorMap = {
        critical: '#f33333',
        major: '#e79958',
        minor: '#e3c920',
        warning: '#a5a5a5',
        clear: '#6cbd85',
        info: '#7daeec',
      }
    } else {
      colorMap = {
        critical: '#ff5959',
        major: '#e79958',
        minor: '#e3c920',
        info: '#a5a5a5',
        warning: '#7daeec',
        fire: '#ca0a0a',
      }
    }
    if (store.state.alarmSeverity) {
      let alarmSeverityList = store.state.alarmSeverity
      let alarmSeverity = alarmSeverityList.find(alarm => alarm.id === id)
      if (alarmSeverity) {
        let color = alarmSeverity.color
          ? alarmSeverity.color
          : colorMap[alarmSeverity.severity.toLowerCase()]
        if (!color) {
          color = colorMap.info
        }

        return {
          cardinality: alarmSeverity.cardinality,
          severity: alarmSeverity.severity,
          color: color,
        }
      }
    }
    return null
  },
  logout(redirect, forceLogin) {
    Vue.cookie.delete('fc.currentSite')
    Vue.cookie.delete('fc.currentOrg')

    const { domainInfo = {} } = window
    const isSAML =
      getProperty(domainInfo, 'isCustomDomain') &&
      getProperty(domainInfo, 'isSSOEnabled')

    let loginURL = '/auth/login'
    if (
      window.identityServerURL &&
      window.identityServerURL.indexOf('http') >= 0
    ) {
      if (window.location.host == 'localhost') {
        let logoutURL = window.identityServerURL + '/identity/logout'
        if (redirect) {
          logoutURL =
            logoutURL +
            '?redirect=' +
            encodeURIComponent(window.location.origin + redirect)
        }
        window.location.href = logoutURL
      } else {
        let logoutURL = '/identity/logout'
        if (redirect) {
          logoutURL = logoutURL + '?redirect=' + encodeURIComponent(redirect)
        }
        window.location.href = logoutURL
      }
      return
    }

    if (isSAML && !forceLogin) {
      if (!domainInfo.isSLOEnabled) {
        loginURL = '/auth/entry'
      } else {
        return
      }
    } else if (redirect && redirect !== loginURL) {
      loginURL += '?redirect=' + redirect
    }

    // TODO Use route names instead of urls
    //let route = router.resolve({ path: loginURL })

    // Can't use route.push or repace here because we need the store to be cleared
    window.location.href = loginURL
  },

  parseLocation(str) {
    if (str) {
      let val = str.split(',')
      return parseFloat(val[0]).toFixed(6) + ',' + parseFloat(val[1]).toFixed(6)
    }
  },

  // https://stackoverflow.com/a/28475133/1157445
  typeOf(obj) {
    return {}.toString
      .call(obj)
      .split(' ')[1]
      .slice(0, -1)
      .toLowerCase()
  },
  getOrgMoment() {
    //return a new moment with the org timezone and specified params
    return moment.tz(...arguments, Vue.prototype.$timezone)
  },
  getOrgTimeMoment() {
    //return a new moment with the only org timezone and specified params for budget module as it needs a specific time zone
    return moment.tz(...arguments, Vue.prototype.$orgTimezone)
  },

  //milliseconds in duration
  //DONT SEND TIMESTAMP Millis here .
  get12HrTime(milliseconds, formatString) {
    return moment.utc(milliseconds).format(formatString)
  },

  weekOfMonth(ts) {
    let dayOfMonth = this.getOrgMoment(ts).date()
    let weekNo = parseInt(dayOfMonth / 7) + (dayOfMonth % 7 == 0 ? 0 : 1)
    return weekNo
  },

  formatDateFull(timeStamp) {
    return this.getOrgMoment(timeStamp).format('DD MMM YYYY')
  },
  formatTimeFull(timeStamp) {
    return this.getOrgMoment(timeStamp).format('hh:mm A')
  },
  formatMillistoHHMM(timeStamp) {
    return this.getOrgMoment(timeStamp).format('HH:mm')
  },
  getFormattedValueForMillis(milliseconds, formatString) {
    return this.getOrgMoment(milliseconds).format(formatString)
  },
  formatTimeStamp(time) {
    return moment.duration(time).asMilliseconds()
  },
  isEmpty(obj) {
    return Object.keys(obj).length == 0
  },
  getDurationInSecs(value, unit = 's') {
    return moment.duration(parseInt(value), unit).asSeconds()
  },

  getDuration(value, format, padValue = 2) {
    if (!value) {
      return {
        Hrs: 0,
      }
    }

    let duration = moment.duration(parseInt(value, 10), format)
    let days = parseInt(duration.asDays(), 10)
    let hours = duration.hours()
    let minutes = duration.minutes()
    let seconds = duration.seconds()

    const pad = val => String(val).padStart(padValue, '0')

    if (days > 0) {
      return hours
        ? {
            Days: pad(days),
            Hrs: pad(hours),
          }
        : {
            Days: pad(days),
          }
    } else if (hours > 0) {
      return minutes
        ? {
            Hrs: pad(hours),
            Mins: pad(minutes),
          }
        : {
            Hrs: pad(hours),
          }
    } else if (minutes > 0) {
      return minutes
        ? {
            Mins: pad(minutes),
          }
        : {
            Secs: pad(seconds),
          }
    } else {
      return {
        Secs: pad(seconds),
      }
    }
  },
  dataURLtoFile(dataurl, filename) {
    let arr = dataurl.split(','),
      mime = arr[0].match(/:(.*?);/)[1],
      bstr = atob(arr[1]),
      n = bstr.length,
      u8arr = new Uint8Array(n)
    while (n--) {
      u8arr[n] = bstr.charCodeAt(n)
    }
    return new File([u8arr], filename, {
      type: mime,
    })
  },
  toBase64(file) {
    return new Promise((resolve, reject) => {
      const reader = new FileReader()
      reader.readAsDataURL(file)
      reader.onload = () => resolve(reader.result)
      reader.onerror = error => reject(error)
    })
  },
  getDataPoints(dataPoints, pointType, includeGroupPoints) {
    if (includeGroupPoints === true) {
      return dataPoints.filter(
        data =>
          pointType.includes(data.pointType || data.type) ||
          data.type === 'group'
      )
    }
    return dataPoints.filter(data => pointType.includes(data.pointType))
  },
  getNextAlias(dpAlias) {
    let { lastAlias, lastNum = 0 } = dpAlias
    if (lastNum || lastAlias == 122) {
      return 'A' + ++dpAlias.lastNum
    }
    if ([68, 87, 100].includes(lastAlias)) {
      // excluding reserved E,X,e
      dpAlias.lastAlias++
    } else if (lastAlias == 90) {
      dpAlias.lastAlias = 96
    }
    return String.fromCharCode(++dpAlias.lastAlias)
  },
  getFileObjectFromRecord(recordData, field) {
    return {
      fileUrl:
        field &&
        ((field.hasOwnProperty('default') && field.default) ||
          (field.hasOwnProperty('isDefault') && field.isDefault))
          ? recordData[field.name + 'Url']
          : recordData.data
          ? recordData.data[field.name + 'Url']
          : null,
      fileContentType:
        field &&
        ((field.hasOwnProperty('default') && field.default) ||
          (field.hasOwnProperty('isDefault') && field.isDefault))
          ? recordData[field.name + 'ContentType']
          : recordData.data
          ? recordData.data[field.name + 'ContentType']
          : null,
    }
  },
  getImagePreviewUrl(id, width, height) {
    let url = `${getBaseURL()}/v2/files/preview/${id}`
    if (!isEmpty(width)) {
      url += `?width=${width}`
      if (!isEmpty(height)) {
        url += `&height=${height}`
      }
    }
    return url
  },
  getFileDownloadUrl(id) {
    let url = `${getBaseURL()}/v2/files/download/${id}`
    return url
  },
  formatedCurrencyCost(val) {
    return Vue.prototype.$currency === '$' || Vue.prototype.$currency === '₹'
      ? Vue.prototype.$currency + Vue.prototype.$d3.format(',.2f')(val)
      : Vue.prototype.$d3.format(',.2f')(val) + ' ' + Vue.prototype.$currency
  },
  htmlToText,
  getDiplayNameforCountryISOCode(iso) {
    let country = countries.find(c => c.value === iso)
    return (country || {}).label
  },
  discountMode() {
    // 1 => Before Tax 2 => After Tax
    let formDataString =
      getProperty(
        Vue.prototype.$account,
        'data.orgEnabledPrefs.discountApplication.formData',
        '{}'
      ) || '{}'
    let formData = JSON.parse(formDataString)
    return getProperty(formData, 'discountApplication', 1) || 1
  },
  taxMode() {
    // 1 => Line Item Level 2 => Transaction Level
    let formDataString =
      getProperty(
        Vue.prototype.$account,
        'data.orgEnabledPrefs.taxApplication.formData',
        '{}'
      ) || '{}'
    let formData = JSON.parse(formDataString)
    return getProperty(formData, 'taxApplication', 1) || 1
  },
  isPortalUser() {
    return getProperty(Vue.prototype.$account, 'user.appType') > 0
  },
  alert(args = {}) {
    return Vue.prototype.$dialog.alert(args)
  },
  getNameAndEMail(value) {
    let mailRegEx = /(?:"?([^"]*)"?\s)?(?:<?(.+@[^>]+)>?)/g
    let values = mailRegEx.exec(value)
    // If name is empty set email as name
    return { name: values[1] || values[2] || '', email: values[2] || '' }
  },
}

export const getTimeInOrgFormat = function(timeStamp) {
  let { $timezone: timezone, $timeformat: timeformat } = Vue.prototype || {
    $timeformat: 'HH:mm',
  }
  if (!isEmpty(timezone)) {
    return moment(timeStamp)
      .tz(Vue.prototype.$timezone)
      .format(timeformat)
  }
  return '---'
}

export const isValidPhoneNumber = function(number) {
  let phoneno = /^[+]*[(]{0,1}[0-9]{1,3}[)]{0,1}[-\s\./0-9]*$/g
  if (number.match(phoneno)) {
    return true
  } else {
    return false
  }
}

export const getAppDomain = function() {
  let {
    location: { protocol },
  } = window
  let app = getApp()
  let domain = getProperty(app, 'appDomain.domain', null)
  return `${protocol}//${domain}`
}
