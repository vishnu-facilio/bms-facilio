import { isEmpty, isNull } from '@facilio/utils/validation'
import { getApp, isWebTabsEnabled } from '@facilio/router'
import find from 'lodash/find'
import helpers from 'src/util/helpers'
import getProperty from 'dlv'

const state = {
  selectedTabGroup: null,
  selectedTab: null,
}

const getters = {
  getTabGroups: () => () => {
    let { webTabGroups } = getApp()

    return !isEmpty(webTabGroups) ? webTabGroups : []
  },
  getTabGroupById: () => groupId => {
    let { webTabGroups } = getApp()

    let group = find(webTabGroups, ['id', groupId])
    return !isEmpty(group) ? group : null
  },
  getTabs: () => groupId => {
    let { webTabGroups } = getApp()

    let group = find(webTabGroups, ['id', groupId])
    return !isEmpty(group) ? group.webTabs : []
  },
  getTabByTabId: () => tabId => {
    let { webTabGroups } = getApp()
    let selectedTab = null

    webTabGroups.forEach(group => {
      let { webTabs = [] } = group
      let tab = webTabs.find(tab => tab.id === tabId)

      if (!isEmpty(tab)) selectedTab = tab
    })

    return selectedTab
  },
  isAppPrefEnabled: () => preference => {
    let { configJSON } = getApp()
    let config = getProperty(configJSON, preference, false)
    return config
  },
  tabHasPermission: (state, getters, rootState) => (permissionName, tab) => {
    let { account } = rootState
    let { role } = (account || {}).user || {}
    let { roleId, isPrevileged } = role || {}
    if (isPrevileged) {
      return true
    } else {
      if (helpers.isLicenseEnabled('NEW_TAB_PERMISSIONS')) {
        if (isEmpty(tab)) {
          tab = state.selectedTab
        }
        if (isWebTabsEnabled() && !isEmpty(tab)) {
          let { permission: permissionList } = tab
          return permissionList.some(perm => {
            let { enabled, actionName, permissions: childPermissions } = perm
            if (!isEmpty(actionName)) {
              let hasPerm = permissionName === actionName && enabled
              return hasPerm
            } else {
              if (!isEmpty(childPermissions)) {
                let { enabled } =
                  childPermissions.find(p => p.actionName === permissionName) ||
                  {}
                return enabled
              } else {
                return false
              }
            }
          })
        } else {
          return true
        }
      } else {
        if (isEmpty(tab)) {
          tab = state.selectedTab
        }
        if (isWebTabsEnabled() && !isEmpty(tab)) {
          let {
            permission: permissionList,
            permissions: permissionsForRole,
          } = tab
          let permissionVal = 0

          if (!isEmpty(permissionsForRole)) {
            permissionVal =
              (permissionsForRole.find(p => p.roleId === roleId) || {})
                .permission || 0
          }

          return permissionList.some(perm => {
            let { actionName, permissions: subPermissions } = perm

            if (!isEmpty(actionName)) {
              let hasPerm =
                permissionName === actionName && perm.enabled

              return hasPerm
            } else {
              if (!isEmpty(subPermissions)) {
                let { enabled = false } =
                  subPermissions.find(p => p.actionName === permissionName) ||
                  {}

                return enabled
              } else {
                return false
              }
            }
          })
        } else {
          return true
        }
      }
    }
  },
}

const actions = {
  setTabGroup({ commit, dispatch, getters, state }, groupId) {
    let tabGroups = getters.getTabGroups() || []
    let { selectedTabGroup } = state

    if (isEmpty(groupId) && !isNull(groupId)) {
      // Check if groupId is intentionally left out and not null
      groupId = !isEmpty(tabGroups[0]) ? tabGroups[0].id : null
    }

    let group = groupId ? tabGroups.find(({ id }) => id === groupId) : null

    if (isEmpty(group)) {
      dispatch('clearTab')
    } else {
      let selectedGroupId = selectedTabGroup && selectedTabGroup.id

      if (!selectedGroupId || selectedGroupId !== groupId) {
        commit('SET_TAB_GROUP', group)
      }
    }
  },

  setTab({ commit, state }, tabId) {
    let { webTabs: tabs } = state.selectedTabGroup || {}

    if (!isEmpty(tabs)) {
      if (isEmpty(tabId) && !isNull(tabId)) {
        tabId = !isEmpty(tabs[0]) ? tabs[0].id : null
      }

      let tab = tabId ? tabs.find(({ id }) => id === tabId) : null

      commit('SET_TAB', tab)
    }
  },

  clearTab({ commit }) {
    commit('SET_TAB', null)
    commit('SET_TAB_GROUP', null)
  },
}

const mutations = {
  SET_TAB_GROUP(state, payload) {
    state.selectedTabGroup = Object.freeze(payload)
  },
  SET_TAB(state, payload) {
    state.selectedTab = Object.freeze(payload)
  },
}

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations,
}
