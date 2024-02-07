import axios from 'axios'
import Vue from 'vue'
import store from 'src/store/index'
import router from 'src/router'
import helpers from 'src/util/helpers.js'
import { isEmpty } from '@facilio/utils/validation'
import getProperty from 'dlv'
import { isWebTabsEnabled } from '@facilio/router'
import { constructBaseURL } from './baseUrl'
import { getApp } from '@facilio/router'
import { eventBus } from '../components/page/widget/utils/eventBus'

let dialogueShown = false

const appVersion = window.webpackPublicPath
  ? window.webpackPublicPath.substring(
      window.webpackPublicPath.lastIndexOf('/') + 1,
      window.webpackPublicPath.length
    )
  : 'None'

const isMobile =
  window.location.pathname.indexOf('newmobiledashboard') > -1 ||
  window.location.pathname.indexOf('/webview/mobiledashboard') > -1 // temp fix it will be removed after this method works

const exportUrls = [
  '/v2/report/export',
  '/exportModule',
  '/v2/report/exportModuleReport',
  '/import/',
  '/v2/integ/pdf/create',
]

axios.defaults.baseURL = constructBaseURL()

const http = axios.create({
  withCredentials: true,
  headers: {
    'X-Device-Type': isMobile ? 'android' : 'Web',
    'X-App-Version': appVersion,
  },
})

http.interceptors.request.use(
  function(config) {
    if (window.loggerLevel) {
      config.url +=
        (!config.url.includes('?') ? '?' : '&') +
        `loggerLevel=${window.loggerLevel}`
    }
    if (window.fetchTrace) {
      config.url +=
        (!config.url.includes('?') ? '?' : '&') +
        `fetchStackTrace=${window.fetchTrace}`
    }
    if (!config.headers) {
      config.headers = {}
    }

    let currLocation = window.location
    let { pathname } = currLocation || {}

    config.headers['X-current-site'] = Vue.cookie.get('fc.currentSite') || -1

    let cookieValue = Vue.cookie.get('fc.switchValue') || null

    if (!isEmpty(cookieValue)) {
      let { linkName } = getApp() || {}

      if (!isEmpty(linkName)) {
        let switchValue = JSON.parse(atob(cookieValue))
        let { [linkName]: encodedSwitchValue } = switchValue || {}

        config.headers['X-switch-value'] = encodedSwitchValue || ''
      }
    }

    if (currLocation && pathname && pathname.startsWith('/app/setup')) {
      config.headers['X-current-site'] = -1
      config.headers['X-switch-value'] = ''
    }

    if (currLocation && pathname && pathname.startsWith('/link/')) {
      let tokenProps = /token=(\S*)&?/.exec(window.location.href)
      let token = tokenProps ? tokenProps[1] : null
      if (token) config.headers['X-Permalink-Token'] = token
    }

    let { webtabs: { selectedTab } = {}, account = null } = store && store.state

    if (isWebTabsEnabled() && selectedTab) {
      if (selectedTab) config.headers['X-Tab-Id'] = selectedTab.id
    }

    if (account && account.org) {
      config.headers['X-Org-Id'] = account.org.id
      if (account.org.groupName) {
        config.headers['X-Org-Group'] = account.org.groupName
      }
    }

    let csrf = Vue.cookie.get('fc.csrfToken')
    if (!isEmpty(csrf)) {
      config.headers['X-CSRF-Token'] = csrf
    }

    let isExportUrl = exportUrls.some(url => config.url.startsWith(url))
    if (isExportUrl) {
      config.headers['X-Is-Export'] = true
    }

    if (config?.removeCurrentSiteFilter) {
      config.headers['X-current-site'] = -1
    }

    if (config?.removeSwitchFilter) {
      config.headers['X-switch-value'] = ''
    }

    return config
  },
  function(error) {
    return Promise.reject(error)
  }
)

export const isTvRoute = api => {
  let { hostname, pathname } = window.location

  if (hostname.startsWith('tv.') || pathname.startsWith('/tv')) {
    return true
  }
  return false
}

export const skipLoginRedirection = api => {
  let { hostname, pathname } = window.location

  if (hostname.startsWith('tv.') || pathname.startsWith('/tv')) {
    return true
  }

  const skippedRoutes = [
    '/auth/login',
    '/app/login',
    '/app/logout',
    '/app/signup',
    '/app/entry',
    '/app/mobile/entry',
    '/app/mobile/login',
    '/app/mobile/service/login',
  ]
  if (skippedRoutes.some(url => pathname.includes(url))) {
    return true
  }

  let excludedAPIs = []
  if (excludedAPIs.some(url => api.includes(url))) {
    return true
  }
}
export const forbiddenApi = api => {
  // we have to check unauthorize only for some base apis like fetchDetails,fetchAccount
  if (api.includes('fetchDetails')) {
    return true
  }
  return false
}

http.interceptors.response.use(
  function(response) {
    if (
      response?.status === 202 &&
      window.location.href.indexOf('orgsetup') == -1
    ) {
      // org signup still in progress
      return new Promise((_, reject) => {
        router
          .replace({
            name: 'orgsetup',
          })
          .finally(() => {
            return reject(response)
          })
      })
    }
    return response
  },
  function(error) {
    let app = window.location.pathname.slice(1).split('/')[0]
    let response = getProperty(error, 'response')
    let config = getProperty(response, 'config') || {}
    let api = getProperty(config, 'url', null)
    let headers = getProperty(response, 'headers')
    let status = getProperty(response, 'status')
    let currentRoute = getProperty(window, 'location.href', '')
    let isForbiddenApi = false
    // if no response, directly send error back to callee
    if (isEmpty(response)) response = error
    if (!isEmpty(api)) isForbiddenApi = forbiddenApi(api)

    let dialogue = getProperty(Vue, 'prototype.$dialog', null)
    let isDialogueAvailable = !isEmpty(dialogue)
    if (
      headers &&
      headers['x-redirect-to'] &&
      headers['x-redirect-to'] !== window.location.href &&
      window.location.pathname !== '/app/samllogout'
    ) {
      window.location.href = headers['x-redirect-to']
    }

    let errMessage = null
    if (!isEmpty(response)) {
      let { data } = response || {}
      let { errorMessage } = data || {}
      errMessage = errorMessage
    }
    if (app === 'webview' && status > 400) {
      console.warn('Webview Error.')
      return new Promise((_, reject) => {
        router
          .replace({
          name: 'webviewerror',
          query: {
            app,
            errMessage,
            prevRoute: currentRoute,
            errorCode: status,
          },
        })
      })
    }
    if (status === 401 && !skipLoginRedirection(api)) {
      return new Promise(() => {
        store.commit('LOGIN_REQUIRED', true)
        helpers.logout(window.location.pathname, true)
      })
    } else if (status === 440 || status === 408) {
      let htmlMessage = 'Your Session has expired.'
      if (status === 408) {
        htmlMessage =
          'You have been logged out due to inactivity. Please login again.'
      }
      let dialogObj = {
        title: 'Session Expired',
        htmlMessage: htmlMessage,
        wide: true,
      }
      if (!dialogueShown) {
        if (isDialogueAvailable) {
          dialogueShown = true
          return helpers.alert(dialogObj).then(() => {
            store.commit('LOGIN_REQUIRED', true)
            helpers.logout(window.location.pathname, true)
          })
        } else {
          return new Promise(() => {
            store.commit('LOGIN_REQUIRED', true)
            helpers.logout(window.location.pathname, true)
          })
        }
      }
    } else if (status === 503) {
      console.warn('Scheduled Maintenance. Server is down.')
      return new Promise((_, reject) => {
        router
          .replace({
            name: 'scheduledmaintenance',
            params: { app, errMessage },
          })
          .finally(() => {
            return reject(response)
          })
      })
    } else if (status === 403 || status == 409) {
      console.warn(
        'Unauthorized. Current user does not have permission for any tab.'
      )

      return new Promise((_, reject) => {
        router
          .replace({
            name: 'nopermission',
            query: { app, errMessage, prevRoute: currentRoute },
          })
          .finally(() => {
            return reject(response)
          })
      })
    } else if (status === 404) {
      console.warn('Page not found.')

      return new Promise((_, reject) => {
        router
          .replace({
            name: 'pagenotfound',
            query: { app, errMessage, prevRoute: currentRoute },
          })
          .finally(() => {
            return reject(response)
          })
      })
    } else if (status === 429) {
      console.warn('To many Requests')

      return new Promise((_, reject) => {
        router
          .replace({
            name: 'toManyRequests',
            query: { app, errMessage, prevRoute: currentRoute },
          })
          .finally(() => {
            return reject(response)
          })
      })
    } else if (isForbiddenApi && !isTvRoute) {
      console.warn('Error Occured in fetch details API.')
      return new Promise((_, reject) => {
        router
          .replace({
            name: 'fetchdetailserror',
            query: {
              app,
              errMessage,
              errorCode: status,
              prevRoute: currentRoute,
            },
          })
          .finally(() => {
            return reject(response)
          })
      })
    } else {
      if (response?.data?.isErrorGeneralized) {
        eventBus.$emit('errorPopup', response.data)
      }
      return Promise.reject(response)
    }
  }
)

export default http
