import App from './newapp/routes.js'
import BetaApp from './beta/routes.js'
import Agent from './agent/routes.js'
import PortalApps from './PortalTenant/routes.js'
import Operations from './OperationalVisibility/routes.js'
import dlv from 'dlv'

const isNewLayout = () => {
  let isNew = dlv(window, 'localStorage.dsm_layout')
  try {
    return JSON.parse(isNew)
  } catch (e) {
    return false
  }
}

const AppRoutes = isNewLayout() ? BetaApp : App

export default [...AppRoutes, Operations, Agent, ...PortalApps]
