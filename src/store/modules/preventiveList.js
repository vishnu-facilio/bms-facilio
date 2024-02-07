import http from 'util/http'

const state = {
  preventiveList: [],
  upcomingPreventiveList: [],
  InactivePreventiveMaintenance: [],
}

// getters
const getters = {
  getPreventiveById: (state, getters) => id => {
    return null
  },
}

// actions
const actions = {
  getActivePreventiveMaintenance: ({ commit, dispatch, state }, payload) => {
    let url = 'workorder/getActivePreventiveMaintenance'
    return http.get(url).then(function(response) {
      commit('SET_PREVENTIVELIST', response.data)
    })
  },
  getUpcomingPreventiveMaintenance: ({ commit, dispatch, state }, payload) => {
    let url = 'workorder/getUpcomingPreventiveMaintenance'
    return http.get(url).then(function(response) {
      commit('SET_UPCOMMING_PREVENTIVELIST', response.data)
    })
  },
  getInactivePreventiveMaintenance: ({ commit, dispatch, state }, payload) => {
    let url = 'workorder/getInactivePreventiveMaintenance'
    return http.get(url).then(function(response) {
      commit('SET_INACTIVE_PREVENTIVELIST', response.data)
    })
  },
}

// mutations
const mutations = {
  SET_PREVENTIVELIST: (state, payload) => {
    state.preventiveList = payload
  },
  SET_UPCOMMING_PREVENTIVELIST: (state, payload) => {
    state.upcomingPreventiveList = payload
  },
  SET_INACTIVE_PREVENTIVELIST: (state, payload) => {
    state.InactivePreventiveMaintenance = payload
  },
}

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations,
}
