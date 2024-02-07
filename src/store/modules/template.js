import http from 'util/http'

const state = {
  currentView: null,
  templates: [],
}

// getters
const getters = {}

// actions
const actions = {
  updateRuleSearchQuery: ({ commit, dispatch, state }, payload) => {
    commit('UPDATE_SEARCH_QUERY', payload)
  },
  fetchRules: ({ commit, dispatch, state }, payload) => {
    let params = ''
    let url = '/v2/templates/' + payload.viewname + '?' + params
    console.log('view', url)
    return http.get(url).then(function(response) {
      commit('SET_RULES', response.data.result.rules)
    })
  },
  reload: ({ commit, dispatch, state }) => {
    let url = `/v2/rules/${state.currentView}?page=${state.currentPage}&perPage=${state.perPage}`
    url += `${state.search ? `&search=${state.search}` : ''}`
    url += `${
      state.includeParentFilter ? `&include=${state.includeParentFilter}` : ''
    }`
    url += `${
      state.filters
        ? `&filters=${encodeURIComponent(JSON.stringify(state.filters))}`
        : ''
    }`
    return http.get(url).then(response => {
      commit('SET_RULES', response.data.result.rules)
    })
  },
}

// mutations
const mutations = {
  SET_CURRENT_VIEW: (state, payload) => {
    state.currentView = payload.viewname
    state.currentPage = payload.page
    state.isNew = payload.isNew
  },
  SET_TEMPLATES: (state, payload) => {
    state.templates = state.perPage && state.currentPage > 1 ? payload : payload
    state.canLoadMore = payload.length === state.perPage
  },
  UPDATE_SEARCH_QUERY: (state, payload) => {
    state.quickSearchQuery = payload
  },
  UPDATE_FILTERS: (state, payload) => {
    state.filters = payload
  },
  UPDATE_INCLUDE_PARENT_FILTERS: (state, payload) => {
    state.includeParentFilter = payload
  },
}

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations,
}
