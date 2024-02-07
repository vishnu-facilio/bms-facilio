import http from 'util/http'

const state = {
  currentView: null,
  rules: [],
  count: null,
  currentPage: 1,
  perPage: 30,
  canLoadMore: false,
  quickSearchQuery: null,
  filters: null,
  includeParentFilter: null,
  metricFields: [],
  isNew: false,
}

// getters
const getters = {
  getRuleById: (state, getters) => id => {
    return state.rules.find(sroom => sroom.id === id)
  },
}

// actions
const actions = {
  updateRuleSearchQuery: ({ commit, dispatch, state }, payload) => {
    commit('UPDATE_SEARCH_QUERY', payload)
  },
  fetchRules: ({ commit, dispatch, state }, payload) => {
    let params = ''
    if (payload.page) {
      params += 'page=' + payload.page + '&perPage=' + state.perPage
      commit('SET_CURRENT_VIEW', payload)
    }
    if (payload.filters) {
      params =
        params +
        '&filters=' +
        encodeURIComponent(JSON.stringify(payload.filters))
      commit('UPDATE_FILTERS', payload.filters)
    }
    if (payload.search) {
      params = params + '&search=' + payload.search
      commit('UPDATE_SEARCH_QUERY', payload.search)
    }
    if (payload.includeParentFilter) {
      params = params + '&includeParentFilter=' + payload.includeParentFilter
    }
    if (payload.isCount) {
      params = params + '&isCount=' + payload.isCount
    }
    let url = '/v2/rules/' + payload.viewname + '?' + params
    return http.get(url).then(function(response) {
      if (payload.isCount) {
        commit('SET_RULES_COUNT', response.data.result)
        return response
      } else {
        commit('SET_RULES', response.data.result.rules)
      }
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
    state.subList = payload.subList
  },
  SET_RULES: (state, payload) => {
    if (state.subList) {
      state.rules =
        state.currentPage > 1 ? [...state.rules, ...payload] : payload
      state.canLoadMore = payload.length === state.perPage
    } else {
      state.rules = state.perPage && state.currentPage > 1 ? payload : payload
      state.canLoadMore = payload.length === state.perPage
    }
  },
  SET_RULES_COUNT: (state, payload) => {
    state.count = payload.count
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
  UPDATE_METRIC_FIELDS: (state, payload) => {
    // state.
  },
}

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations,
}
