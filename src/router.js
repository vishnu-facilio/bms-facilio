import Vue from 'vue'
import VueRouter from 'vue-router'
import NProgress from 'accessible-nprogress'
import store from './store'
import { isEmpty, isUndefined } from '@facilio/utils/validation'
import helpers from 'src/util/helpers.js'
import { getTabInfo, isWebTabsEnabled, getApp } from '@facilio/router'
import { isPortalDomain } from 'util/utility-methods'
import { track } from './track'
import getProperty from 'dlv'

Vue.use(VueRouter)

NProgress.configure({ showSpinner: false })

import AuthRouter from 'auth/router'
import ErrorRouter from 'error/router'
import AppRouter from 'pages/router'
import TVRouter from 'tv/router'
import DeviceRouter from 'devices/router'
import PermalinkRouter from 'permalink/router'
import WebViewRouter from 'webViews/router'
import PdfGenricRouter from 'pdf/router'
import DigestRouter from 'digest/router'
import SetupRouter from 'pages/setup/router'
import PersonalSettingsRouter from 'pages/personalsettings/router'
import PdfRouter from 'pages/pdf/router'
import ServiceCatelog from 'pages/catalog/router'
import ModuleImport from 'src/module-import/router'
import ModuleSurveysRouter from 'moduleSurveys/router'
import Developer from 'src/pages/workflow/router'
import UtilityAuthRouter from 'src/utilityApi/router'
import IFrameV2Router from 'src/iframev2/router'

const router = new VueRouter({
  linkActiveClass: 'active',
  mode: 'history',
  routes: [
    {
      path: '/app/mobilefloorplan/:floorId',
      component: () => import('pages/dashboard/MobileFloorPlanViewer'),
      meta: {
        module: 'floorplan',
        dashboardlayout: 'mobile',
        pageLoading: false,
      },
    },
    {
      path: '/app/mobiledashboard/:dashboardlink',
      component: () => import('pages/dashboard/MobileDashboard'),
      meta: {
        module: 'energydata',
        dashboardlayout: 'mobile',
        source: 'dashboard',
        pageLoading: false,
      },
    },
    {
      path: '/app/mobiledashboard/:dashboardlink/:buildingid',
      component: () => import('pages/dashboard/MobileDashboard'),
      meta: {
        module: 'energydata',
        dashboardlayout: 'mobile',
        source: 'dashboard',
        pageLoading: false,
      },
    },
    {
      path: '/app/customdashboard/:id',
      component: () => import('pages/dashboard/MobileDashboard'),
      meta: {
        module: 'energydata',
        dashboardlayout: 'mobile',
        source: 'dashboard',
        pageLoading: false,
      },
    },
    {
      path: '/app/newmobiledashboard/:dashboardlink',
      component: () => import('pages/dashboard/MobileDashboard'),
      meta: {
        module: 'energydata',
        dashboardlayout: 'mobile',
        source: 'dashboard',
        pageLoading: false,
      },
    },
    {
      path: '/app/newmobiledashboard/:dashboardlink/:buildingid',
      component: () => import('pages/dashboard/MobileDashboard'),
      meta: {
        module: 'energydata',
        dashboardlayout: 'mobile',
        source: 'dashboard',
        pageLoading: false,
      },
    },
    {
      path: '/app/newanalytics/generate',
      component: () =>
        import('pages/energy/analytics/newTools/MobileAnalytics'),
      meta: {
        module: 'energydata',
        layout: 'mobile',
        source: 'analytics',
        pageLoading: false,
      },
    },
    {
      path: '/app/mobilesummerychart',
      component: () =>
        import('pages/energy/analytics/newTools/MobileAlarmSummeryChart'),
      meta: {
        module: 'energydata',
        layout: 'mobile',
        source: 'analytics',
        pageLoading: false,
      },
    },
    {
      path: '/app/mobilealarmrca',
      component: () =>
        import('pages/energy/analytics/newTools/MobileAlarmRcaSummeryChart'),
      meta: {
        module: 'energydata',
        layout: 'mobile',
        source: 'analytics',
        pageLoading: false,
      },
    },
    {
      path: '/app/mobilereport',
      component: () => import('pages/report/FMobileReport'),
      meta: {
        module: 'energydata',
        dashboardlayout: 'mobile',
        layout: 'mobile',
        source: 'dashboard',
        pageLoading: false,
      },
    },
    {
      path: '/app/permalink/monthlyportfolio',
      component: () => import('pages/pdf/MonthlyPortfolio'),
      meta: {
        pageLoading: false,
      },
    },
    {
      path: '/app/maintenanceReport',
      component: () => import('pages/pdf/maintenanceReport'),
      meta: {
        pageLoading: false,
      },
    },
    {
      path: '/app/qr',
      component: () => import('pages/outer/Qr'),
      meta: {
        pageLoading: false,
      },
    },
    {
      path: '/app/connectedapp/:linkName',
      component: () => import('pages/connectedapps/ConnectedAppLoader'),
    },
    {
      path: '/charts/generate',
      component: () => import('minicharts/GenerateChart'),
      meta: {
        pageLoading: false,
      },
    },
    {
      path: '/app/beta/workflowbuilder',
      name: 'workflowbuilder',
      component: () => import('pages/workflowbuilder/WorkFlowBuilder'),
    },

    {
      path: '/tv',
      component: () => import('tv/Home'),
      meta: {
        requiresAuth: false,
      },
      children: TVRouter,
    },

    {
      name: 'deviceHome',
      path: '/device',
      component: () => import('devices/Home'),
      meta: {
        requiresAuth: false,
      },
      children: DeviceRouter,
    },

    ...PermalinkRouter,
    ...WebViewRouter,
    ...DigestRouter,
    ...AuthRouter,
    ...PdfGenricRouter,
    ...IFrameV2Router,
    ModuleSurveysRouter,
    UtilityAuthRouter,

    AppRouter,

    PdfRouter,
    PersonalSettingsRouter,
    SetupRouter,
    ServiceCatelog,
    ModuleImport,
    Developer,
    {
      path: '/',
      beforeEnter: (to, from, next) => next(getAppPath()),
    },

    // Always leave these intact -> Error Routes
    ...ErrorRouter,
  ],
})

const getAppPath = () => {
  let nextPath = { path: '/app' }

  if (isWebTabsEnabled()) {
    nextPath = { path: `/${getApp().linkName}` }
  }

  return nextPath
}

const handleBuildRefresh = to => {
  if (store.state.system && store.state.system.reload) {
    let urlToLoad = new URL(
      window.location.protocol + '//' + window.location.host + to.path
    )

    if (!isEmpty(to.query)) {
      Object.entries(to.query).forEach(([key, value]) =>
        urlToLoad.searchParams.set(key, value)
      )
    }

    window.location.href = urlToLoad
  }
}

const needsRedirect = to => {
  const domain = document.domain

  if (isPortalDomain() && to.path.startsWith(`/app`)) {
    return {
      path: '/',
    }
  } else if (domain.includes('tv.facilio.com') && !to.path.startsWith('/tv')) {
    return {
      path: '/tv',
    }
  }
}

const loadAccount = (to, from, next) => {
  let siteId = Vue.cookie.get('fc.currentSite')

  if (siteId > 0) {
    let { state } = store || {}
    let { account } = state || {}

    Vue.cookie.delete('fc.currentSite')
    store.dispatch('getCurrentAccount').then(() => {
      Vue.cookie.set('fc.currentSite', siteId, {
        expires: '10Y',
        path: '/',
      })
      track(to?.path, account)
      next()
    })
  } else {
    next()
  }
}

const updateLastVisited = to => {
  Vue.cookie.set('lastvisit', to.path, {
    path: '/',
  })
}

const setWebTab = async to => {
  if (isWebTabsEnabled()) {
    let meta = getTabInfo(to) || {}

    if (meta.groupId) {
      await store.dispatch('webtabs/setTabGroup', meta.groupId)
    } else {
      await store.dispatch('webtabs/setTabGroup', null)
    }

    if (meta.tabId) {
      await store.dispatch('webtabs/setTab', meta.tabId)
    }
  } else {
    return Promise.resolve()
  }
}

const hasAppChanged = (to, from) => {
  // if App changed ie. from /agent to /app, then reload whole page
  let isLogout = to.name === 'logout'

  let appNameRegex = /\/([a-zA-Z]*)\/?/
  let fromApp = new RegExp(appNameRegex).exec(from.fullPath)[1]
  let toApp = new RegExp(appNameRegex).exec(to.fullPath)[1]

  return !isLogout && !isEmpty(fromApp) && !isEmpty(toApp) && fromApp !== toApp
}

const cookieDisabled = to => {
  return !to.path.includes('/notsupported') && store.state.isCookiesDisabled
}

router.beforeEach(async (to, from, next) => {
  let toUrl = to?.fullPath || '/'
  let isErrorPage = toUrl.startsWith('/error')
  let shouldReload = hasAppChanged(to, from) && !isErrorPage
  let { state } = store || {}
  let { account } = state || {}

  if (shouldReload) {
    console.warn('Reloading page since app was changed')
    window.location.href = window.location.origin + to.fullPath
  }

  let nextPath = needsRedirect(to, from)

  if (cookieDisabled(to)) {
    nextPath = { name: 'notsupported' }
  }
  if (!isEmpty(nextPath)) {
    let { href } = router.resolve(nextPath)
    window.location.href = href
    return
  }

  handleBuildRefresh(to)

  await setWebTab(to)

  const isLoginRequired = store.state.loginRequired
  const hasPathChanged = to.path !== from.path
  const showProgress =
    !to.meta || isUndefined(to.meta.pageLoading) || to.meta.pageLoading

  track(to?.path, account)

  if (hasPathChanged && showProgress) {
    NProgress.start()
  }

  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (isLoginRequired) {
      helpers.logout(to.fullPath)
    } else {
      if ((to.path === '/app' || to.path === '/app/') && nextPath) {
        next(nextPath)
      } else {
        if (to.path === '/app/setup' || to.path === '/app/setup/') {
          loadAccount(to, from, next)
        } else if (to.matched.some(record => record.meta.remember)) {
          updateLastVisited(to)
          next()
        } else {
          next()
        }
      }
    }
  } else {
    if ((to.path === '/app' || to.path === '/app/') && nextPath) {
      next(nextPath)
    } else {
      next()
    }
  }
})

router.afterEach((to, from) => {
  let setupRegex = new RegExp(/\/setup\/?/)
  let isFromSetup = setupRegex.test(from.path)
  let isNextRouteNotSetup = to.path && !setupRegex.test(to.path)
  let { state } = store || {}
  let { account, features } = state || {}

  if (isFromSetup && isNextRouteNotSetup) {
    let siteId = Vue.cookie.get('fc.currentSite')
    if (siteId > 0) {
      store.dispatch('getCurrentAccount').then(() => {
        track(to?.path, account)
      })
    }
  }
  if (!features && Vue.cookie.get('fc.loggedIn') === 'true') {
    store.dispatch('getFeatureLicenses').then(() => {
      let isMaintenanceAppSignup = getProperty(
        store,
        'state.features.MAINTENANCE_APP_SIGNUP'
      )
      let pathName = getProperty(window, 'location.pathname', '')
      let isOldApp =
        pathName.startsWith('/app') || pathName.startsWith('/newapp')
      let isNotAppRedirectionPage = pathName.indexOf('app/redirection') == -1
      if (isMaintenanceAppSignup && isOldApp && isNotAppRedirectionPage) {
        let currentRoute = getProperty(window, 'location.href', '')
        let searchQuery = getProperty(window, 'location.search', '')
        router.replace({
          name: 'appredirection',
          params: { searchQuery, currentRoute },
        })
      }
    })
  }
  NProgress.done()
})

export default router
