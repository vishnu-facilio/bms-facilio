import Vuex from 'vuex'
import Vue from 'vue'
import { API, setConfig } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import clone from 'lodash/clone'
import { genericCachedGet, genericMutations } from './utils'
import { constructOptionsToMap } from '@facilio/utils/utility-methods'

import * as types from './mutation-types'
import workorder from './modules/workorder'
import reportdata from './modules/reportdata'
import device from './modules/device'
import alarm from './modules/alarm'
import system from './modules/system'
import leed from './modules/leed'
import space from './modules/space'
import purchaseditem from './modules/purchaseditem'
import itemtransaction from './modules/itemtransaction'
import tooltransaction from './modules/tooltransaction'
import event from './modules/event'
import notification from './modules/notification'
import remotescreen from './modules/remotescreen'
import view from './modules/view'
import publishdata from './modules/publishdata'
import pubsub from './modules/pubsub'
import formulabuilder from './modules/formulabuilder'
import purchaseorder from './modules/purchaseorder'
import purchasecontract from './modules/purchasecontract'
import labourcontract from './modules/labourcontract'
import rentalleasecontract from './modules/rentalleasecontract'
import warrantycontract from './modules/warrantycontract'
import metricUnits from './modules/metricunits'
import rule from './modules/rule'
import ruleTemplate from './modules/ruleTemplate'
import shiftbreak from './modules/shiftbreak'
import shift from './modules/shift'
import attendance from './modules/attendance'
import shiftrotation from './modules/shiftrotation'
import newAlarm from './modules/newAlarm'
import customModule from './modules/customModule'
import reservation from './modules/reservation'
import anomalies from './modules/anomalies'
import getProperty from 'dlv'
import devices from './modules/devices'
import workpermit from './modules/workpermit'
import automationSetup from './modules/automationSetup'
import safetyplanModule from './modules//safetyplan/safetyplan'
import hazardModule from './modules//safetyplan/hazard'
import precautionModule from './modules//safetyplan/precaution'
import clientModule from './modules/client'
import search from './modules/search'
import clientcontact from './modules/clientcontact'
import employee from './modules/employee'
import chatbot from './modules/socket/chatbot'
import webtabs from './modules/webtabs'
import dashboard from './modules/dashboard'
import glimpse from './modules/glimpse'
import $helpers from 'util/helpers'
import pagebuilder from './modules/pagebuilder'

Vue.use(Vuex)

const state = {
  isCookiesDisabled: false,
  loginRequired: false,
  account: {},
  app: {},
  features: null,
  serverNotReachable: false,
  users: [],
  spaces: [], // DO NOT Use this. Will be removed soon
  assets: {}, // DO NOT Use this. Will be removed soon
  tenant: [],
  permissionMapping: {},
  currentProduct: {
    code: null,
    label: null,
    path: null,
    modules: [],
  },
  currentMobileView: {
    name: 'Alarms',
    views: [],
  },
  socket: {
    isConnected: false,
    message: '',
    reconnectError: false,
  },
  userSites: {},
  ticketStatus: {
    workorder: null,
    asset: null,
    assetmovement: null,
  },
  approvalStatus: null,
  ticketCategory: null,
  ticketType: null,
  ticketPriority: null,
  assetCategory: null,
  kpiCategory: null,
  readingAlarmCategory: null,
  assetDepartment: null,
  assetType: null,
  alarmSeverity: null,
  spaceCategory: null,
  inventoryCategory: null,
  energyMeters: null,
  serviceList: null,
  shifts: null,
  orgs: null,
  roles: null,
  site: null,
  sites: null,
  groups: null,
  buildings: null,
  canShowSetupSideBar: true,
  activeCurrencies: [],
}

const getters = {
  currentAccount: state => state.account,
  currentApp: state => state.app,
  getActiveCurrencies: state => state.activeCurrencies,
  getCurrentProduct: state => state.currentProduct,
  getUser: state => id => {
    let user = state.users.find(user => user.id === id)
    if (user) {
      return clone(user)
    }
    return {
      id: id,
      name: 'Unknown',
    }
  },
  getGroup: state => id => {
    if (!isEmpty(state.groups)) {
      return clone(state.groups.find(group => group.id === id))
    }
  },
  // DO NOT Use this. Will be removed soon
  getSpace: state => id => {
    return clone(state.spaces.find(space => space.id === id))
  },
  getSite: state => id => {
    if (!isEmpty(state.site)) {
      return clone(state.site.find(site => site.id === id))
    }
    return {}
  },
  getApprovalStatus: state => id => {
    if (!isEmpty(state.approvalStatus)) {
      let status = state.approvalStatus.find(
        ticketStatus => ticketStatus.id === id
      )

      return status ? clone(status) : null
    }
  },
  getTicketStatus: state => (id, moduleName) => {
    if (!isEmpty(state.ticketStatus[moduleName])) {
      return clone(
        state.ticketStatus[moduleName].find(
          ticketStatus => ticketStatus.id === id
        )
      )
    }
  },
  getTicketStatusByLabel: state => (label, moduleName) => {
    if (!isEmpty(state.ticketStatus[moduleName])) {
      return clone(
        state.ticketStatus[moduleName].find(
          ticketStatus => ticketStatus.status === label
        )
      )
    }
  },
  getTicketStatusPickList: state => moduleName => {
    if (!isEmpty(state.ticketStatus[moduleName])) {
      return state.ticketStatus[moduleName].reduce((acc, ticketStatusObj) => {
        acc[ticketStatusObj.id] = ticketStatusObj.displayName
        return acc
      }, {})
    } else return {}
  },
  getTicketPriority: state => id => {
    if (!isEmpty(state.ticketPriority)) {
      return state.ticketPriority.find(
        ticketPriority => ticketPriority.id === id
      )
    } else return {}
  },
  getTicketPriorityByLabel: state => label => {
    if (!isEmpty(state.ticketPriority)) {
      return state.ticketPriority.find(
        ticketPriority => ticketPriority.priority === label
      )
    } else return {}
  },
  getTicketPriorityPickList: state => () => {
    if (!isEmpty(state.ticketPriority)) {
      return state.ticketPriority.reduce((acc, ticketPriorityObj) => {
        acc[ticketPriorityObj.id] = ticketPriorityObj.displayName
        return acc
      }, {})
    } else return {}
  },
  getTicketCategory: state => id => {
    return (state.ticketCategory || []).find(
      ticketCategory => ticketCategory.id === id
    )
  },
  getTicketCategoryByLabel: state => label => {
    return state.ticketCategory.find(
      ticketCategory => ticketCategory.name === label
    )
  },
  getTicketCategoryPickList: state => () => {
    if (!isEmpty(state.ticketCategory)) {
      return state.ticketCategory.reduce((acc, ticketCategoryObj) => {
        acc[ticketCategoryObj.id] = ticketCategoryObj.name
        return acc
      }, {})
    } else return {}
  },
  getTicketType: state => id => {
    if (!isEmpty(state.ticketType)) {
      return state.ticketType.find(ticketType => ticketType.id === id)
    } else return {}
  },
  getTicketTypePickList: state => () => {
    if (!isEmpty(state.ticketType)) {
      return state.ticketType.reduce((acc, ticketTypeObj) => {
        acc[ticketTypeObj.id] = ticketTypeObj.name
        return acc
      }, {})
    } else return {}
  },
  getTicketTypePickListById: (state, getters) => id => {
    return getters.getTicketTypePickList.find(
      ticketType => ticketType.id === id
    )
  },
  // DO NOT Use this. Will be removed soon
  getBuildings: state => {
    return state.spaces.filter(space => {
      if (space.spaceTypeVal === 'Building') {
        return space
      }
      return false
    })
  },
  getAssetCategory: state => id => {
    return (state.assetCategory || []).find(
      assetCategory => assetCategory.id === id
    )
  },
  getAssetCategoryByType: state => type => {
    return state.assetCategory.find(category => category.type === type)
  },
  getUtilityAssetCategoryByType: state => type => {
    let categoryId = []
    state.assetCategory.forEach(category => {
      if (category.type === type) {
        categoryId.push(category.id)
      }
      return categoryId
    })
  },
  getAssetCategoryByName: state => name => {
    return state.assetCategory.find(category => category.name === name)
  },
  getAssetCategoryByModule: state => name => {
    return state.assetCategory.find(category => category.moduleName === name)
  },
  getAssetCategoryPickList: state => () => {
    if (!isEmpty(state.assetCategory)) {
      return state.assetCategory.reduce((acc, assetCategoryObj) => {
        acc[assetCategoryObj.id] = assetCategoryObj.displayName
        return acc
      }, {})
    } else return {}
  },
  getSpaceCategoryPickList: state => () => {
    if (!isEmpty(state.spaceCategory)) {
      return state.spaceCategory.reduce((acc, spaceCategoryObj) => {
        acc[spaceCategoryObj.id] = spaceCategoryObj.name
        return acc
      }, {})
    } else return {}
  },
  getAssetDepartment: state => id => {
    return state.assetDepartment.find(
      assetDepartment => assetDepartment.id === id
    )
  },
  getAssetType: state => id => {
    return (state.assetType || []).find(assetType => assetType.id === id)
  },
  isStatusLocked: state => (id, moduleName) => {
    if (isEmpty(state.ticketStatus[moduleName])) {
      return false
    }
    let status = state.ticketStatus[moduleName].find(
      ticketStatus => ticketStatus.id === id
    )
    return status ? status.recordLocked : false
  },
  getServiceList: state => () => {
    return state.serviceList
  },
  getBuildingsPickList: state => () => {
    if (!isEmpty(state.buildings)) {
      return state.buildings.reduce((acc, buildingsObj) => {
        acc[buildingsObj.id] = buildingsObj.name
        return acc
      }, {})
    } else return {}
  },
  getBuildingNameById: state => id => {
    if (!isEmpty(state.buildings)) {
      let buildingObj = state.buildings.find(
        buildingsObj => buildingsObj.id === id
      )
      if (!isEmpty(buildingObj)) {
        return clone(buildingObj.name)
      }
    } else return {}
  },
  getReadingAlarmCategory: state => id => {
    return state.readingAlarmCategory.find(
      readingAlarmCategory => readingAlarmCategory.id === id
    )
  },
  getAlarmSeverity: state => id => {
    return state.alarmSeverity
      ? state.alarmSeverity.find(alarmSeverity => alarmSeverity.id === id)
      : null
  },
  getAlarmSeverityByName: state => severity => {
    return state.alarmSeverity.find(
      alarmSeverity => alarmSeverity.severity === severity
    )
  },
  getAlarmSeverityByDisplayName: state => severity => {
    // handled with display name to get the servertiy from field matcher in rules
    return state.alarmSeverity.find(
      alarmSeverity => alarmSeverity.displayName === severity
    )
  },
  getKpiCategoryById: state => id => {
    let category = state.kpiCategory.find(c => c.id === id)
    if (category) {
      return category
    } else {
      return null
    }
  },
  getCurrentUser: state => () => {
    return state.account.user
  },
  getCurrentSiteId: () => () => {
    let curr = Vue.cookie.get('fc.currentSite')
    curr = Number(curr)
    if (curr > 0) {
      return curr
    }
    return -1
  },
  getOperatorById: state => id => {
    return clone(state.operatorsValue[id])
  },
  getOperatorsList: state => () => {
    let { operatorsList } = state
    return clone(operatorsList)
  },
  getRoleNameById: state => id => {
    let { roles } = state
    let selectedRole = (roles || []).find(role => role.id === id) || {}
    return selectedRole.name
  },
}

const actions = {
  getCurrentAccount({ commit }) {
    return API.get('/v2/fetchAccount').then(({ error, data }) => {
      if (error) {
        console.warn('Unable to fetch account details')
        commit('LOGIN_REQUIRED', true)
      } else {
        let { account: currentAccount } = data || {}
        if (currentAccount) {
          commit('LOGIN_REQUIRED', false)
          commit(types.CURRENT_ACCOUNT, currentAccount)
          // For error logs handling
          window.orgId = getProperty(currentAccount, 'org.id', null)
          window.userId = getProperty(currentAccount, 'user.id', null)
        } else {
          commit('LOGIN_REQUIRED', true)
        }
      }
    })
  },
  getActiveCurrencyList({ commit, state }, forceUpdate = false) {
    if (
      $helpers.isLicenseEnabled('MULTI_CURRENCY') &&
      isEmpty(state.activeCurrencies)
    ) {
      return genericCachedGet({ commit, state }, 'multicurrency', {
        url: 'v2/multicurrency/list',
        commitName: 'ACTIVE_CURRENCIES',
        extraction: data => data.currencies,
        forceUpdate,
      })
    }
    return state.activeCurrencies
  },
  getCurrentApp({ commit }) {
    return API.get('v2/application/fetchDetails?considerRole=true').then(
      ({ error, data }) => {
        if (error) {
          console.warn('Unable to fetch app details')
        } else {
          let { application: currentApp } = data || {}
          if (currentApp) {
            commit(types.CURRENT_APP, currentApp)
          }
        }
      }
    )
  },
  getFeatureLicenses({ commit }) {
    return genericCachedGet({ commit, state }, 'features', {
      url: 'v2/features',
      extraction: data => data.features || {},
    }).then(() => {
      if ($helpers.isLicenseEnabled('NEW_V3API')) {
        setConfig({ _newV3: true })
      }
    })
  },

  loadTicketCategory({ commit, state }, forceUpdate = false) {
    return genericCachedGet({ commit, state }, 'ticketCategory', {
      url: 'v2/module/data/list?moduleName=ticketcategory',
      extraction: data => data.moduleDatas,
      forceUpdate,
    })
  },
  loadTicketPriority({ commit, state }, forceUpdate = false) {
    return genericCachedGet({ commit, state }, 'ticketPriority', {
      url: 'v2/module/data/list?moduleName=ticketpriority',
      extraction: data => data.moduleDatas,
      forceUpdate,
    })
  },
  loadTicketStatus({ commit, state }, moduleName) {
    let forceUpdate = false
    if (typeof moduleName === 'object') {
      forceUpdate = moduleName.forceUpdate
      moduleName = moduleName.moduleName
    }

    return genericCachedGet({ commit, state }, 'ticketStatus', {
      url: `v2/state/list?parentModuleName=${moduleName}`,
      extraction: data => data.status,
      forceUpdate,
      moduleName,
    })
  },
  loadApprovalStatus({ commit, state }, moduleName) {
    let forceUpdate = false
    if (typeof moduleName === 'object') {
      forceUpdate = moduleName.forceUpdate
      moduleName = moduleName.moduleName
    }

    return genericCachedGet({ commit, state }, 'approvalStatus', {
      url: `v2/state/list?approvalStatus=true`,
      extraction: data => data.status,
      forceUpdate,
      moduleName,
    })
  },
  loadTicketType({ commit, state }, forceUpdate = false) {
    return genericCachedGet({ commit, state }, 'ticketType', {
      url: 'v2/module/data/list?moduleName=tickettype',
      extraction: data => data.moduleDatas,
      forceUpdate,
    })
  },
  loadAssetCategory({ commit, state }, forceUpdate = false) {
    return genericCachedGet({ commit, state }, 'assetCategory', {
      url: 'v2/module/data/list?moduleName=assetcategory',
      extraction: data => data.moduleDatas,
      forceUpdate,
    })
  },
  loadAssetDepartment({ commit, state }, forceUpdate = false) {
    return genericCachedGet({ commit, state }, 'assetDepartment', {
      url: 'v2/module/data/list?moduleName=assetdepartment',
      extraction: data => data.moduleDatas,
      forceUpdate,
    })
  },
  loadAssetType({ commit, state }, forceUpdate = false) {
    return genericCachedGet({ commit, state }, 'assetType', {
      url: 'v2/module/data/list?moduleName=assettype',
      extraction: data => data.moduleDatas,
      forceUpdate,
    })
  },
  loadAlarmSeverity({ commit, state }, forceUpdate = false) {
    return genericCachedGet({ commit, state }, 'alarmSeverity', {
      url: 'v2/module/data/list?moduleName=alarmseverity',
      extraction: data => data.moduleDatas,
      forceUpdate,
    })
  },
  loadReadingAlarmCategory({ commit, state }, forceUpdate = false) {
    return genericCachedGet({ commit, state }, 'readingAlarmCategory', {
      url: 'v2/module/data/list?moduleName=readingAlarmCategory',
      extraction: data => data.moduleDatas,
      forceUpdate,
    })
  },
  loadSpaceCategory({ commit, state }, forceUpdate = false) {
    return genericCachedGet({ commit, state }, 'spaceCategory', {
      url: 'v2/module/data/list?moduleName=spacecategory',
      extraction: data => data.moduleDatas,
      forceUpdate,
    })
  },
  loadInventoryCategory({ commit, state }, forceUpdate = false) {
    return genericCachedGet({ commit, state }, 'inventoryCategory', {
      url: 'v2/module/data/list?moduleName=inventorycategory',
      extraction: data => data.moduleDatas,
      forceUpdate,
    })
  },
  loadKpiCategories: ({ commit, state }, forceUpdate = false) => {
    return genericCachedGet({ commit, state }, 'kpiCategory', {
      url: 'v2/kpi/getAllKPICategories',
      extraction: data => data.kpiCategoryContexts,
      forceUpdate,
    })
  },
  loadEnergyMeters({ commit, state }, forceUpdate = false) {
    return genericCachedGet({ commit, state }, 'energyMeters', {
      url: 'v2/getMainEnergyMeters',
      extraction: data => data.energyMeters,
      forceUpdate,
    })
  },
  loadServiceList({ commit, state }, forceUpdate = false) {
    return genericCachedGet({ commit, state }, 'serviceList', {
      url: '/v3/picklist/energymeterpurpose',
      extraction: data => {
        let { pickList: options } = data || {}
        let optionsObj = constructOptionsToMap(options || [])

        if (optionsObj) return optionsObj
        else return null
      },
      forceUpdate,
      canFreeze: true,
    })
  },
  loadShifts({ commit, state }, payload, forceUpdate = false) {
    return genericCachedGet({ commit, state }, 'shifts', {
      url: 'v2/shift/list',
      extraction: data => data.shifts,
      forceUpdate,
    })
  },
  loadOrgs({ commit, state }, forceUpdate = false) {
    return genericCachedGet({ commit, state }, 'orgs', {
      url: 'v2/getOrgs',
      extraction: data => data.Orgs,
      forceUpdate,
    })
  },
  loadRoles({ commit, state }, forceUpdate = false) {
    return genericCachedGet({ commit, state }, 'roles', {
      url: 'v2/getRoles',
      extraction: data => data.Roles,
      forceUpdate,
    })
  },
  loadSites({ commit, state }, forceUpdate = false) {
    return genericCachedGet({ commit, state }, 'sites', {
      url: 'v2/getSites',
      extraction: data => data.sites,
      forceUpdate,
    })
  },
  loadSite({ commit, state }, forceUpdate = false) {
    return genericCachedGet({ commit, state }, 'site', {
      url: 'v2/getSite',
      extraction: data => data.site,
      forceUpdate,
      canFreeze: true,
    })
  },
  loadGroups({ commit, state }, forceUpdate = false) {
    return genericCachedGet({ commit, state }, 'groups', {
      url: 'v2/getGroups',
      extraction: data => data.groups,
      forceUpdate,
      canFreeze: true,
    })
  },
  loadOperators({ commit, state }, forceUpdate = false) {
    return genericCachedGet({ commit, state }, 'operatorsList', {
      url: 'v2/filter/advanced/operators',
      extraction: data => data.operators,
      forceUpdate,
      canFreeze: true,
    })
  },
  loadBuildings({ commit, state }, forceUpdate = false) {
    return genericCachedGet({ commit, state }, 'buildings', {
      url: 'v2/getAllBuildings',
      extraction: data => data.buildings,
      forceUpdate,
    })
  },
  loadUsers({ commit, state }, forceUpdate = false) {
    return genericCachedGet({ commit, state }, 'users', {
      url: '/users',
      extraction: data => data.users,
      forceUpdate,
    })
  },
  loadUtilityDetails({ commit }) {
    API.get('/tenantuser').then(({ error, data }) => {
      if (!error && data) {
        commit(types.REQUESTER_LIST, data)
      }
    })
  },
  loadSpaces({ commit }) {
    API.get('/basespace').then(({ error, data }) => {
      if (!error && data) {
        commit(types.SPACE_LIST, data.basespaces)
      }
    })
  },
  updateCalendarColor({ commit }, payload) {
    API.post('/settings/updateCalendarColor', {
      calendarColor: {
        basedOn: payload,
      },
    }).then(({ data, error }) => {
      if (!error && data) {
        commit('UPDATE_CALENDAR_COLOR', payload)
      }
    })
  },

  updateServiceList({ commit }, payload) {
    commit('UPDATE_SERVICE_LIST', payload)
  },
  updateSetupSideBar({ commit }, payload) {
    commit('UPDATE_SETUP_SIDEBAR', payload)
  },
  switchMobileView({ commit }, payload) {
    commit('CURRENT_MOBILE_VIEW', payload)
  },
  updateMobileViews({ commit }, payload) {
    commit('UPDATE_MOBILE_VIEW_LIST', payload)
  },
  switchProduct({ commit }, payload) {
    commit('CURRENT_PRODUCT', payload)
  },
  setCurrentAccount({ commit }, account) {
    commit(types.CURRENT_ACCOUNT, account)
  },
  setCurrentApp({ commit }, app) {
    commit(types.CURRENT_APP, app)
  },
  setTenantUsers({ commit }, tenant) {
    commit(types.REQUESTER_LIST, tenant)
  },
  setServicePortalAccount({ commit }, account) {
    commit('SERVICE_PORTAL_ACCOUNT', account)
  },
  updateUserAvatarUrl({ commit }, payload) {
    commit('CURRENT_ACCOUNT_AVATAR_URL', payload)
  },
}

const mutations = {
  SOCKET_ONOPEN(state) {
    state.socket.isConnected = true
  },
  SOCKET_ONCLOSE(state) {
    state.socket.isConnected = false
  },
  SOCKET_ONERROR(state, event) {
    console.error(event)
  },
  // default handler called for all methods
  SOCKET_ONMESSAGE(state, message) {
    state.socket.message = message
  },
  // mutations for reconnect methods
  SOCKET_RECONNECT(state, count) {
    console.warn('Reconnect', count)
  },
  SOCKET_RECONNECT_ERROR(state) {
    state.socket.reconnectError = true
  },
  [types.SERVER_NOT_REACHABLE](state, serverNotReachable) {
    state.serverNotReachable = serverNotReachable
  },
  LOGIN_REQUIRED(state, loginRequired) {
    Vue.cookie.set('fc.loggedIn', !loginRequired)
    state.loginRequired = loginRequired
  },
  ACTIVE_CURRENCIES(state, currencies) {
    state.activeCurrencies = currencies.data
  },
  CURRENT_APP(state, app) {
    state.app = app
  },
  SERVICE_PORTAL_ACCOUNT(state, account) {
    state.account = account

    if (account.data.users) {
      state.users = account.data.users
    }
    if (account.data.groups) {
      state.groups = account.data.groups
    }
    if (account.data.buildingList) {
      state.buildingList = account.data.buildingList
    }
  },
  [types.CURRENT_ACCOUNT](state, account) {
    state.account = account

    if (account.user && account.user.role && account.user.role.permissions) {
      for (let i = 0; i < account.user.role.permissions.length; i++) {
        let perm = account.user.role.permissions[i]
        state.permissionMapping[perm.moduleName] = perm.permission
      }
    }
    if (account.data.users) {
      state.users = account.data.users
    }
    if (account.data.tenant) {
      state.tenant = account.data.tenant
    }

    if (account.data.userSites) {
      state.userSites = account.data.userSites
    }

    if (account.appProps && account.appProps.operators) {
      state.operators = account.appProps.operators
    }
    if (account.appProps && account.appProps.operators) {
      let opera = Object.keys(account.appProps.operators)
      let lstOperator = []
      opera.forEach(dk => {
        if (dk) {
          if (account.appProps.operators[dk]) {
            let operator = Object.keys(account.appProps.operators[dk])
            operator.forEach(key => {
              let obj = account.appProps.operators[dk][key]
              lstOperator[obj.operatorId] = key
            })
          }
        }
      })
      state.operatorsValue = lstOperator
    }
  },
  FEATURES(state, features) {
    state.features = features
  },
  CURRENT_MOBILE_VIEW(state, view) {
    state.currentMobileView.name = view
  },
  UPDATE_MOBILE_VIEW_LIST(state, views) {
    state.currentMobileView.views = views
  },
  CURRENT_PRODUCT(state, product) {
    state.currentProduct = product
  },
  CURRENT_ACCOUNT_AVATAR_URL(state, payload) {
    state.account.user.avatarUrl = payload.avatarUrl
    let userObj = state.users.find(user => user.id === payload.id)
    let userIndex = state.users.indexOf(userObj)
    userObj.avatarUrl = payload.avatarUrl
    state.users.splice(userIndex, 1, userObj)
  },

  [types.SPACE_LIST](state, spaces) {
    state.spaces = Object.freeze(spaces)
  },
  // /To be removed

  [types.REQUESTER_LIST](state, tenant) {
    state.tenant = tenant
  },
  UPDATE_SERVICE_LIST(state, payload) {
    state.serviceList[payload.id] = payload.name
  },
  UPDATE_CALENDAR_COLOR(state, basedOn) {
    state.calendarColor = basedOn
  },
  UPDATE_SETUP_SIDEBAR(state, payload) {
    state.canShowSetupSideBar = payload
  },
  ...genericMutations,
}

export default new Vuex.Store({
  state,
  getters,
  actions,
  mutations,
  modules: {
    workorder,
    reportdata,
    device,
    alarm,
    system,
    leed,
    space,
    purchaseditem,
    itemtransaction,
    tooltransaction,
    event,
    notification,
    remotescreen,
    view,
    publishdata,
    pubsub,
    formulabuilder,
    purchaseorder,
    purchasecontract,
    labourcontract,
    metricUnits,
    rule,
    ruleTemplate,
    shift,
    shiftbreak,
    attendance,
    shiftrotation,
    rentalleasecontract,
    warrantycontract,
    newAlarm,
    customModule,
    anomalies,
    reservation,
    devices,
    workpermit,
    automationSetup,
    safetyplanModule,
    precautionModule,
    hazardModule,
    clientModule,
    search,
    clientcontact,
    employee,
    chatbot,
    webtabs,
    dashboard,
    glimpse,
    pagebuilder,
  },
  strict: false,
})
