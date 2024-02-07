import http from 'util/http'

const state = {
  sites: [],
  currentSite: {
    data: null,
    buildings: [],
  },
  zones: [],
}

// getters
const getters = {
  getSiteById: (state, getters) => id => {
    return state.sites.find(site => site.id === id)
  },
  getBuildingById: (state, getters) => id => {
    return state.currentSite.buildings.find(building => building.id === id)
  },
  getZoneById: (state, getters) => id => {
    return state.zones.find(zone => zone.id === id)
  },
}

// actions
const actions = {
  fetchSites: ({ commit, dispatch, state }) => {
    let url = '/campus'
    let promise = http.get(url).then(function(response) {
      commit('SET_SITES', response.data.records)
    })
    return promise
  },
  switchSite: ({ commit, dispatch, state }, payload) => {
    let site = state.sites.find(site => site.id === payload.id)
    if (site && !(payload || {}).forceFetch) {
      return commit('SET_CURRENT_SITE', site)
    } else {
      let url = '/campus/' + payload.id
      return http.get(url).then(function(response) {
        commit('SET_CURRENT_SITE', response.data.record)
      })
    }
  },
  fetchBuildings: ({ commit, dispatch, state }, payload) => {
    let url = '/building?siteId=' + payload.id
    http.get(url).then(function(response) {
      commit('SET_SITE_BUILDINGS', response.data.records)
    })
  },
  fetchZones: ({ commit, dispatch, state }) => {
    let url = '/zone'
    return http.get(url).then(function(response) {
      commit('SET_ZONES', response.data.records)
    })
  },
}

// mutations
const mutations = {
  SET_SITES: (state, payload) => {
    state.sites = payload
  },
  SET_CURRENT_SITE: (state, payload) => {
    state.currentSite.data = payload
  },
  SET_SITE_BUILDINGS: (state, payload) => {
    state.currentSite.buildings = payload
  },
  SET_ZONES: (state, payload) => {
    state.zones = payload
  },
}

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations,
}
