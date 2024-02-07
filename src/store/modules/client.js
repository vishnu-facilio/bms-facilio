import http from 'util/http'

const state = {
  currentView: null,
  clients: [],
  currentPage: 1,
  perPage: 50,
  canLoadMore: false,
  quickSearchQuery: null,
  filters: null,
  includeParentFilter: null,
  isNew: false,
  clientDetail: null,
}

// actions
const actions = {
  updateSearchQuery: ({ commit, dispatch, state }, payload) => {
    commit('UPDATE_SEARCH_QUERY', payload)
  },
  fetchClientDetails({ commit, dispatch, state }, payload) {
    return http.get('v2/client/' + payload.id).then(function(response) {
      commit('SET_CLIENT_DATA', response.data.result.client)
    })
  },
  fetchAppClientDetails({ commit, dispatch, state }, payload) {
    return http.get('v2/client/' + payload.id).then(function(response) {
      commit('SET_CLIENT_DATA', response.data.result.client)
    })
  },
  fetchClients: ({ commit, dispatch, state }, payload) => {
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
    let url = '/v2/clients/view/' + payload.viewname + '?' + params
    return http.get(url).then(function(response) {
      commit('SET_CLIENTS', response.data.result.clients)
    })
  },
  reload: ({ commit, dispatch, state }) => {
    let url = `/v2/client/view/${state.currentView}?page=${state.currentPage}&perPage=${state.perPage}`
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
      commit('SET_CLIENTS', response.data.result.clients)
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
  SET_CLIENT_DATA: (state, payload) => {
    state.clientDetail = payload
  },
  SET_CLIENTS: (state, payload) => {
    if (state.isNew) {
      state.clients = state.perPage && state.currentPage > 1 ? payload : payload
      state.canLoadMore = payload.length === state.perPage
    } else {
      state.clients =
        state.perPage && state.currentPage > 1
          ? [...state.clients, ...payload]
          : payload
      state.canLoadMore = payload.length === state.perPage
    }
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
  actions,
  mutations,
}
