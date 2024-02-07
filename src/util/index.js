import http from './http'
import * as filters from './filters'
import directives from './directives'
import helpers from './helpers'
import style from './style'
import convert from './convert'
import time from './time'
import { isEmpty, isBoolean, isObject } from '@facilio/utils/validation'
import sanitize from './sanitize'
import { prependBaseUrl } from './utility-methods'
import {
  isBooleanField,
  isNumberField,
  isDecimalField,
  isEnumField,
  isDateField,
  isDateTimeField,
  isDateTypeField,
  isLookupField,
  getDisplayValue,
  isLookupDropDownField,
  isChooserTypeField,
  isSiteField,
  isSiteLookup,
  isLookupSimple,
  isLocationField,
  isIdField,
  isFileTypeField,
  isGeoLocationField,
} from './field-utils'
import Spinner from '@/Spinner'
import currency from '@/currency'
import FDialog from '@/FDialog'
import FSound from '@/FSound'
import ValueFormatter from '@/ValueFormatter'
import { Alert } from 'quasar'
import constants from 'util/constant'
import util from 'util/util'
import common from 'util/common'
import moment from 'moment-timezone'
import * as d3 from 'd3'
import delve from 'dlv'
import dset from 'dset'
import ConnectedAppEventChannel from 'pages/connectedapps/event-channel/ConnectedAppEventChannel'
import { isWebTabsEnabled } from '@facilio/router'
import store from '../store/index'
import { htmlToText } from '@facilio/utils/filters'

let FacilioUtil = {}

let CONNECTED_APP_EVENT_CHANNEL = null

FacilioUtil.install = function(Vue, options) {
  // register global utility variables and functions
  Object.defineProperties(Vue.prototype, {
    $account: {
      get() {
        return options.account
      },
    },
    $app: {
      get() {
        return options.app
      },
    },
    $org: {
      get() {
        return options.account.org
      },
    },
    $mobile: {
      get() {
        return false
      },
    },
    $timezone: {
      get() {
        let timezone
        if (!options.account.timezone) {
          timezone = options.account.org.timezone
            ? options.account.org.timezone
            : 'Etc/UTC'
        } else {
          timezone = options.account.timezone
            ? options.account.timezone
            : 'Etc/UTC'
        }
        return timezone
      },
    },
    $orgTimezone: {
      get() {
        let timezone = ''
        if (options?.account?.org?.timezone) {
          timezone = options.account.org.timezone
        }
        return timezone
      },
    },
    $currency: {
      get() {
        let { currencyInfo, orgInfo } = options.account.data || {}
        let currencyDisplaySymbol = delve(currencyInfo, 'displaySymbol', null)
        let orgCurrency = delve(orgInfo, 'currency', null)

        if (currencyDisplaySymbol) return currencyDisplaySymbol
        else if (orgCurrency) return orgCurrency === 'rs' ? '₹' : orgCurrency
        else return '$'
      },
    },
    $dateformat: {
      get() {
        return options.account.org.dateFormat || 'DD-MMM-YYYY'
      },
    },
    $timeformat: {
      get() {
        return options.account.org.timeFormat === 2 ? 'hh:mm A' : 'HH:mm'
      },
    },
    $hasPermission: {
      get() {
        return (permissionListStr, tabId) => {
          let permissionValues = permissionListStr.split(':')
          let permModuleName = permissionValues[0]
          let permissionList = permissionValues[1].split(',')

          if (isWebTabsEnabled() && permModuleName !== 'setup') {
            let tab = null

            if (!isEmpty(tabId)) {
              tab = store.getters['webtabs/getTabByTabId'](tabId)
            }

            return permissionList.some(permName =>
              store.getters['webtabs/tabHasPermission'](permName, tab)
            )
          } else {
            let permissionMapping = {}
            let checkPermission = function(moduleName, permission) {
              let permissionValue =
                options.account.appProps.permissions[permission]
              return helpers.andOperatorOnLong(
                permissionValue,
                permissionMapping[moduleName]
              )
            }

            let permissionModuleList = []
            // Temp fix to load dashboard in tenant portal
            if (
              options.account.user &&
              options.account.user.role &&
              options.account.user.role.permissions &&
              options.account.user.role.permissions.length
            ) {
              for (
                let i = 0;
                i < options.account.user.role.permissions.length;
                i++
              ) {
                let perm = options.account.user.role.permissions[i]
                permissionModuleList[i] = perm.moduleName
                permissionMapping[perm.moduleName] = perm.permission
              }
            } else {
              // 0 means full permission
              return true
            }
            let hasAccess = false
            for (let moduleName in permissionModuleList) {
              if (permissionValues[0] === permissionModuleList[moduleName]) {
                for (let x in permissionList) {
                  let perm = permissionList[x].trim()
                  if (
                    typeof options.account.appProps.permissions[perm] !==
                    'undefined'
                  ) {
                    hasAccess = checkPermission(
                      permissionModuleList[moduleName],
                      perm
                    )
                    if (hasAccess) {
                      return true
                    }
                  }
                }
              }
            }
            return false
          }
        }
      },
    },
    $http: {
      get() {
        return http
      },
    },
    $d3: {
      get() {
        return d3
      },
    },
    $helpers: {
      get() {
        return helpers
      },
    },
    $style: {
      get() {
        return style
      },
    },
    $time: {
      get() {
        return time
      },
    },
    $validation: {
      get() {
        return {
          isEmpty,
          isBoolean,
          isObject,
        }
      },
    },
    $fieldUtils: {
      get() {
        return {
          isBooleanField,
          isNumberField,
          isDecimalField,
          isEnumField,
          isDateField,
          isDateTimeField,
          isDateTypeField,
          isLookupField,
          getDisplayValue,
          isLookupDropDownField,
          isChooserTypeField,
          isSiteField,
          isSiteLookup,
          isLookupSimple,
          isLocationField,
          isIdField,
          isFileTypeField,
          isGeoLocationField,
        }
      },
    },
    $sound: {
      get() {
        return {
          play(name) {
            return fsound(name).play(name)
          },
          stop(name) {
            return fsound(name).stop(name)
          },
        }
      },
    },
    $dialog: {
      get() {
        return {
          alert(args = {}) {
            return fdialog('alert', args)
          },
          confirm(args = {}) {
            return fdialog('confirm', args)
          },
          prompt(args = {}) {
            return fdialog('prompt', args)
          },
          notify(args = {}) {
            let alertObj = {
              color: 'positive',
              position: 'top-center',
            }
            let autoclose = 1500
            if (typeof args === 'string') {
              alertObj.html = args
            } else {
              alertObj.html = args.message
              if (args.color) {
                alertObj.color = args.color
              }
              if (args.position) {
                alertObj.position = args.position
              }
              if (args.autoclose) {
                autoclose = args.autoclose
              }
            }
            let alertInstance = Alert.create(alertObj)
            if (autoclose > 0) {
              setTimeout(function() {
                alertInstance.dismiss()
              }, autoclose)
            }
            return alertInstance
          },
        }
      },
    },
    $constants: {
      get() {
        return constants
      },
    },
    $util: {
      get() {
        return util
      },
    },
    $common: {
      get() {
        return common
      },
    },
    $getProperty: {
      get() {
        return delve
      },
    },
    $setProperty: {
      get() {
        return dset
      },
    },
    $convert: {
      get() {
        return convert
      },
    },
    $connectedAppEventChannel: {
      get() {
        if (!CONNECTED_APP_EVENT_CHANNEL) {
          CONNECTED_APP_EVENT_CHANNEL = new ConnectedAppEventChannel({
            user: options.account.user,
            org: options.account.org,
            app: options.account.app,
          })
        }
        return CONNECTED_APP_EVENT_CHANNEL
      },
    },
  })

  Vue.prototype.$sanitize = sanitize
  Vue.prototype.$prependBaseUrl = prependBaseUrl

  // setting moment timezone globally
  // moment.tz.setDefault(options.account.org.timezone ? options.account.org.timezone : 'Etc/UTC')

  /**
   * This returns the date as if its selected in the org.
   * eg: '01-12-2013 08:30 GMT+0530' in india will be returned as '01-12-2013 08:30 GMT+0400' in Dubai
   *
   * @param {Date} date
   * @returns Moment Date Wrapper
   */
  moment.getDateInOrg = function(date) {
    // let timezone = (options.account.org.timezone ? options.account.org.timezone : 'Etc/UTC')

    let timezone
    if (!options.account.timezone) {
      timezone = options.account.org.timezone
        ? options.account.org.timezone
        : 'Etc/UTC'
    } else {
      timezone = options.account.timezone ? options.account.timezone : 'Etc/UTC'
    }
    return moment.tz(moment(date).format('YYYY-MM-DD HH:mm:ss'), timezone)
  }

  // register global utility components.
  Vue.component('spinner', Spinner)
  Vue.component('currency', currency)
  Vue.component('ValueFormatter', ValueFormatter)
  // register global utility filters.
  let filterList = filters
  Object.keys(filterList).forEach(key => {
    Vue.filter(key, filterList[key])
  })
  Vue.filter('htmlToText', htmlToText)

  // register global utility directives.
  let directiveList = directives
  Object.keys(directiveList).forEach(key => {
    Vue.directive(key, directiveList[key])
  })

  // register FDialog component globally
  const CONSTRUCTOR = Vue.extend(FDialog)
  const CACHE = {}
  const mergedOptions = Object.assign(
    FDialog.OPTIONS_TEMPLATE
    // options.dialog ? options.dialog : {}
  )
  const fdialog = (mode, args) => {
    if (typeof args === 'string') {
      let title = args
      args = {
        title,
      }
    }
    args.mode = mode || 'alert'
    switch (mode) {
      case 'alert':
        args.lbHide = true
        break
      case 'confirm':
        break
      case 'prompt':
        break
      default:
        args.lbHide = true
        break
    }
    let dialog =
      CACHE[mergedOptions.id] || (CACHE[mergedOptions.id] = new CONSTRUCTOR())
    if (!dialog.$el) {
      let vm = dialog.$mount()
      document.querySelector(args.parent || 'body').appendChild(vm.$el)
    }
    return dialog.enqueue(args)
  }

  // register FSound component globally
  const SOUND_CONSTRUCTOR = Vue.extend(FSound)
  const SOUND_CACHE = {}
  const fsound = name => {
    let sound =
      SOUND_CACHE[name] || (SOUND_CACHE[name] = new SOUND_CONSTRUCTOR())
    if (!sound.$el) {
      let vm = sound.$mount()
      document.querySelector('body').appendChild(vm.$el)
    }
    return sound
  }
}

export default FacilioUtil
