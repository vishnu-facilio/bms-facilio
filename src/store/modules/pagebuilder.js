import { genericCachedGet } from '../utils'

const state = {
  newPageEnabledModules: null,
}

const actions = {
  loadNewPageEnabledModules({ commit, state }) {
    try {
      return genericCachedGet({ commit, state }, 'newPageEnabledModules', {
        url: 'v2/moduleSetting/pageBuilder/modules',
        extraction: data => data.modules,
        commitName: 'PAGE_MODULE_LIST',
      })
    } catch (e) {
      //API error handling
      commit('PAGE_MODULE_LIST', {})
    }
  },
}
const mutations = {
  PAGE_MODULE_LIST(state, payload) {
    let { data } = payload || {}
    state.newPageEnabledModules = data || []
  },
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
}
