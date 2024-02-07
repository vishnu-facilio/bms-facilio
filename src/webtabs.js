import { createApp, createRoutes, registerInitializer } from '@facilio/router'
import { API } from '@facilio/api'
import routes from './routes'
import router from './router'
import { constructBaseURL, getBaseURL, setBaseURL } from 'util/baseUrl'
import { isEmpty } from '@facilio/utils/validation'
import getProperty from 'dlv'

function registerInitializers() {
  // Require all files from initlaizers folder
  const requireModule = require.context('./initializers', false, /\.js$/)

  // Take only 'agent' from './agent.js' as the key and exported class
  // as the value and register it
  requireModule.keys().forEach(fileName => {
    let name = fileName.replace(/^\.\//, '').replace(/\.\w+$/, '')
    let module = requireModule(fileName)
    registerInitializer(name, module.default || module)
  })
}

function updateBaseURL(app) {
  let { linkName } = app || {}
  if (linkName) {
    setBaseURL(constructBaseURL(linkName))
  }
}

function loadData() {
  const appNameFromUrl = window.location.pathname.slice(1).split('/')[0]
  const cacheTimeout = Infinity // dont need to refetch this call untill page refresh

  let baseURL = getBaseURL()
  if (appNameFromUrl && appNameFromUrl != 'orgsetup') {
    baseURL = constructBaseURL(appNameFromUrl) // baseUrl that includes appLinkName from the address-bar
  }

  return API.get(
    `/v2/application/fetchDetails`,
    { considerRole: true },
    { cacheTimeout },
    { baseURL }
  )
}

function getAvaiableTabGroups(webTabGroups, currentUserRole) {
  let { roleId, isPrevileged } = currentUserRole || {}

  if (isEmpty(currentUserRole) || isPrevileged) {
    return webTabGroups || []
  } else {
    let availableTabGroups = []

    if (!isEmpty(webTabGroups)) {
      webTabGroups.forEach(group => {
        let { webTabs = [] } = group
        let availableTabs = []

        webTabs.forEach(tab => {
          let { permissions } = tab

          if (!isEmpty(permissions)) {
            let enabledRoleIds = permissions.map(perm => perm.roleId)
            if (enabledRoleIds.includes(roleId)) {
              availableTabs.push(tab)
            }
          }
        })

        if (!isEmpty(availableTabs)) {
          availableTabGroups.push({ ...group, webTabs: availableTabs })
        }
      })
    }

    return availableTabGroups
  }
}

function getLayoutTabGroup(data) {
  let { application, currentUserRole } = data || {}
  let { layouts } = application || {}

  if (!isEmpty(layouts)) {
    let { appLayoutType, webTabGroupList } = getProperty(layouts, '0', {})
    application = {
      ...application,
      layoutType: appLayoutType,
      webTabGroups: getAvaiableTabGroups(webTabGroupList, currentUserRole),
    }
    data = { ...data, application }
  }

  return data
}

function canSkipFetch() {
  // For apps like auth and error we dont need fetchDetails since it will return 401
  const skippedApps = ['auth', 'error', 'webview', 'link', 'pdf', 'tv']
  const appNameFromUrl = window.location.pathname.slice(1).split('/')[0]

  return skippedApps.includes(appNameFromUrl)
}
async function getLanguage() {
  let lang = ''
  let { data } = await API.get(`/v2/fetchAccount`)
  let { account } = data || {}
  let { language: userLanguage } = account?.user || {}
  let { language: orgLanguage } = account?.org || {}

  if (!isEmpty(userLanguage)) {
    lang = userLanguage
  } else if (!isEmpty(orgLanguage)) {
    lang = orgLanguage
  } else {
    lang = 'en'
  }
  return lang
}

export default function() {
  // Register all app initializers first. This has to be done before createApp()
  registerInitializers()

  return new Promise(resolve => {
    if (canSkipFetch()) return resolve()
    loadData()
      .then(({ data, error }) => {
        if (error) {
          console.warn(`Error while loading web-tabs`)
          console.error(error)
          resolve()
        } else {
          // Change the baseURL to the form "app.facilio.com/<linkName>/api"
          let { application: app } = data
          updateBaseURL(app)

          // Initialize app and genetare routes
          let currentRoleAppData = getLayoutTabGroup(data)
          createApp(currentRoleAppData)
          createRoutes(router, routes)
        }
      })
      .then(async () => {
        let lang = await getLanguage()
        resolve(lang)
      })
  })
}
