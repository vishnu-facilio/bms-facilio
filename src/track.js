import axios from 'axios'
import { isEmpty } from '@facilio/utils/validation'
import { getApp, isWebTabsEnabled } from '@facilio/router'
import Vue from 'vue'
import store from './store'
import getProperty from 'dlv'
import { datadogRum } from '@datadog/browser-rum'
import { addRoutesTracker } from 'vue-gtag'

export const PATH_VS_FEATURE_MAP = {
  dashboard: 'dashboard',
  '/home/portfolio': 'portfolio',
  '/at/assets': 'asset',
  reports: 'report',
  '/inventory': 'inventory',
  '/wo/orders': 'workorder',
  '/wo/workpermit': 'workpermit',
  '/wo/planned': 'ppm',
  '/wo/calendar': 'calendar',
  'wo/approvals': 'approval',
  'wo/newapprovals': 'approval',
  timeline: 'timeline',
  '/inspection/template': 'inspectionTemplate',
  '/inspection/individual': 'inspectionResponse',
  '/induction': 'induction',
  '/em/analytics': 'analytics',
  '/app/em/pivot': 'pivot',
  '/em/kpi': 'kpi',
  '/fa/faults': 'faults',
  '/fa/rules': 'rules',
  '/tm/tenants': 'tenant',
  '/purchase/pr': 'purchaserequest',
  '/purchase/po': 'purchaseorder',
  '/purchase/rv': 'receivable',
  '/purchase/tandc': 'termsandconditions',
  '/vendor': 'vendors',
  '/bk/facility': 'facility',
  '/bk/amenity': 'facilityamenity',
  '/bk/facilitybooking': 'facilitybooking',
  '/ca/modules/': 'custom_module',
  '/ca/apps': 'connected_app',
  '/setup': 'setup',
  '/co/graphics': 'graphics',
  '/en/mv': 'm_and_v',
  '/en/energy': 'energystar',
  '/vi/visits': 'visitorlog',
  '/vi/invites': 'invitevisitor',
  '/vi/visitor': 'visitor',
  '/vi/watchlist': 'watchlist',
  '/webview/tenant': 'tenant',
  '/webview/occupant': 'occupant',
  '/webview/employee': 'employee',
  '/webview/vendor': 'vendor',
  '/webview/client': 'client',
  '/webview/qanda': 'qanda',
  '/webview/sc': 'SmartControl',
}

export const track = (path, account) => {
  let { user, org } = account || {}
  let { id, orgId, role, appDomain, email } = user || {}
  let { name } = role || {}
  let { domain } = appDomain || {}
  let { domain: orgDomain } = org || {}
  let { linkName } = getApp() || {}
  let isSystemUser = !isEmpty(email) ? email.endsWith('@facilio.com') : false
  let feature = detectFeature(window.location.pathname, account)
  let canCallTracter =
    !isEmpty(account) &&
    !isEmpty(path) &&
    path !== '/' &&
    !path.includes('login')

  if (window?.isGoogleAnalytics && canCallTracter) {
    axios.get(`https://track.facilio.com/blank.gif`, {
      params: {
        url: path,
        appName: linkName,
        userId: id,
        orgId,
        domain,
        role: name,
        orgDomain: orgDomain,
        isSystemUser,
        device: 'Web',
        feature,
      },
    })

    // set feature dimension for google analytics
    if (Vue.prototype.$gtag) {
      let feature = detectFeature(window.location.pathname, account)
      if (feature) {
        Vue.prototype.$gtag.set('Feature', feature)
      }
    }
  }
}

export const detectFeature = (path, account) => {
  let feature = null
  let { dataDogClientId } = window || {}
  let email = getProperty(account, 'user.email', '')
  let shouldAddFeatureFlag =
    !isEmpty(dataDogClientId) && !email.includes('@facilio.com')
  if (isWebTabsEnabled()) {
    let currentTab = store?.state?.webtabs?.selectedTab
    if (currentTab?.name) {
      let { modules } = currentTab
      if (modules && modules.length) {
        let moduleObject = modules[0]
        feature = moduleObject?.name
        if (feature?.startsWith('custom_')) {
          feature = 'custom_module'
        }
      }
      if (!feature) {
        feature = `${currentTab.typeEnum}`
        feature = feature.replaceAll(' ', '_').toLowerCase()
        if (shouldAddFeatureFlag) {
          datadogRum.addFeatureFlagEvaluation(feature, true)
        }
      }
    }
  } else {
    Object.keys(PATH_VS_FEATURE_MAP).forEach(routerKey => {
      if (path.indexOf(routerKey) > -1) {
        feature = PATH_VS_FEATURE_MAP[routerKey]
        if (shouldAddFeatureFlag) {
          datadogRum.addFeatureFlagEvaluation(feature, true)
        }
      }
    })
  }
  return feature
}

export const initGoogleAnalytics = account => {
  if (Vue.prototype.$gtag && window?.isGoogleAnalytics) {
    let { user, org } = account
    let appNameFromUrl = window.location.pathname.slice(1).split('/')[0]
    // let feature = detectFeature(window.location.pathname)

    let CustomerOrFacilioUser =
      user?.email?.includes('@facilio.com') ||
      user?.email?.includes('qatestfac@gmail.com')
        ? 'Facilio'
        : 'Customer'

    let userProperties = {
      userId: user.iamOrgUserId,
      orgDomain: org.domain,
      role: user?.role?.name,
      userType: CustomerOrFacilioUser,
      appType: appNameFromUrl,
    }

    bootstrapGA4(userProperties)
  }
}

export const bootstrapGA4 = userProperties => {
  const configId = 'G-DRM4R5SBLQ'
  const customResourceURL = 'https://www.googletagmanager.com/gtag/js'
  const customPreconnectOrigin = 'https://www.googletagmanager.com'
  const globalObjectName = 'gtag'
  const globalDataLayerName = 'dataLayer'
  const deferScriptLoad = false

  // registering gtag global variables (window.gtag, window.dataLayer)
  if (window[globalObjectName] == null) {
    window[globalDataLayerName] = window[globalDataLayerName] || []
    window[globalObjectName] = function() {
      window[globalDataLayerName].push(arguments)
    }
  }
  window[globalObjectName]('js', new Date())

  // setting user properties in dataLayer
  window[globalObjectName]('set', 'user_properties', userProperties)

  // adding router to auto tracking
  addRoutesTracker()

  // loading the gtag.js script
  return loadScript(
    `${customResourceURL}?id=${configId}&l=${globalDataLayerName}`,
    {
      preconnectOrigin: customPreconnectOrigin,
      defer: deferScriptLoad,
    }
  )
}

export const loadScript = (url, options = {}) => {
  return new Promise((resolve, reject) => {
    if (typeof document === 'undefined') {
      return
    }

    const head = document.head || document.getElementsByTagName('head')[0]
    const script = document.createElement('script')

    script.async = true
    script.src = url
    script.defer = options.defer

    if (options.preconnectOrigin) {
      const link = document.createElement('link')

      link.href = options.preconnectOrigin
      link.rel = 'preconnect'

      head.appendChild(link)
    }

    head.appendChild(script)

    script.onload = resolve
    script.onerror = reject
  })
}
