import http from 'util/http'

const state = {
  anomaliesMetrics: null,
  isLoading: false,
}

// getters
const getters = {
  getNoOfAnomalies: (state, getters) => {
    return state.anomaliesMetrics ? state.anomaliesMetrics.noofanomalies : null
  },
  getEnergyByCdd: (state, getters) => {
    return state.anomaliesMetrics ? state.anomaliesMetrics.energyByCdd : null
  },
  getDeviation: (state, getters) => {
    return state.anomaliesMetrics ? state.anomaliesMetrics.deviation : null
  },
  getEnergyWastage: (state, getters) => {
    return state.anomaliesMetrics ? state.anomaliesMetrics.wastage : null
  },
  getMttc: (state, getters) => {
    return state.anomaliesMetrics ? state.anomaliesMetrics.mttc : null
  },
}

// actions
const actions = {
  getMetricsDetails: ({ commit, dispatch, state }, payload) => {
    // commit('SET_METRICS', null)
    commit('SET_LOADING', true)
    let url = `/v2/mlAnomalyAlarm/metrics`
    let paramJson = {}
    paramJson.alarmId = payload.alarmId
    paramJson.resourceId = payload.resourceId
    paramJson.siteId = payload.siteId
    if (payload.dateRange != null) {
      paramJson.dateRange = payload.dateRange
    }
    return http.post(url, paramJson).then(function(response) {
      if (response.data && response.data.result) {
        commit('SET_METRICS', response.data.result.metrics)
        commit('SET_LOADING', false)
      } else {
        commit('SET_METRICS', {})
        commit('SET_LOADING', false)
      }
    })
  },
}

// mutations
const mutations = {
  SET_METRICS: (state, payload) => {
    state.anomaliesMetrics = payload
  },
  SET_LOADING: (state, payload) => {
    state.isLoading = payload
  },
}

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations,
}
