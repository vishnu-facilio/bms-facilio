import { isEmpty } from '@facilio/utils/validation'
import http from 'util/http'

const state = {
  isAutomationModuleLoading: false,
  automationModulesList: [],
}

const getters = {
  getAutomationModulesList: state => () => {
    let { automationModulesList } = state
    if (!isEmpty(automationModulesList)) {
      return automationModulesList
    }
    return []
  },
}

const mutations = {
  UPDATE_AUTOMATION_MODULELIST(state, modulesList) {
    state.automationModulesList = !isEmpty(modulesList) ? modulesList : []
  },
}

const actions = {
  fetchAutomationModule({ commit, state }) {
    let url = `v2/automation/module`
    let { automationModulesList } = state
    if (!state.isAutomationModuleLoading && isEmpty(automationModulesList)) {
      state.isAutomationModuleLoading = new Promise((resolve, reject) => {
        http
          .get(url)
          .then(({ data: { result, responseCode, message } }) => {
            if (responseCode === 0) {
              let { modules } = result
              commit('UPDATE_AUTOMATION_MODULELIST', modules)
              state.isAutomationModuleLoading = false
              resolve()
            } else {
              throw new Error(message)
            }
          })
          .catch(({ message }) => {
            reject(message)
            state.isAutomationModuleLoading = false
          })
      })
      return state.isAutomationModuleLoading
    }
  },
}

export default {
  namespaced: true,
  state,
  getters,
  mutations,
  actions,
}
