import http from 'util/http'

const state = {
  currentView: null,
  contacts: [],
  currentPage: 1,
  perPage: 50,
  canLoadMore: false,
  quickSearchQuery: null,
  filters: null,
  includeParentFilter: null,
  isNew: false,
  contactDetail: null,
}

// actions
const actions = {
  updateSearchQuery: ({ commit, dispatch, state }, payload) => {
    commit('UPDATE_SEARCH_QUERY', payload)
  },
  fetchContactDetails({ commit, dispatch, state }, payload) {
    return http
      .post('/v2/clientcontact/details', { recordId: payload.id })
      .then(function(response) {
        commit('SET_CONTACT_DATA', response.data.result.clientcontact)
      })
  },
  fetchContacts: ({ commit, dispatch, state }, payload) => {
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
    let url = '/v2/clientcontact/views/' + payload.viewname + '?' + params
    return http.get(url).then(function(response) {
      commit('SET_CONTACTS', response.data.result.clientcontacts)
    })
  },
  reload: ({ commit, dispatch, state }) => {
    let url = `/v2/clientcontact/views/${state.currentView}?page=${state.currentPage}&perPage=${state.perPage}`
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
      commit('SET_CONTACTS', response.data.result.clientcontacts)
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
  SET_CONTACT_DATA: (state, payload) => {
    state.contactDetail = payload
  },
  SET_CONTACTS: (state, payload) => {
    if (state.isNew) {
      state.contacts =
        state.perPage && state.currentPage > 1 ? payload : payload
      state.canLoadMore = payload.length === state.perPage
    } else {
      state.contacts =
        state.perPage && state.currentPage > 1
          ? [...state.contacts, ...payload]
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
