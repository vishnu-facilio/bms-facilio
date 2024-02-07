import http from 'util/http'

const state = {
  currentView: null,
  customModuleList: [],
  customModuledata: null,
  currentPage: 1,
  perPage: 50,
  canLoadMore: false,
  quickSearchQuery: null,
  filters: null,
  includeParentFilter: null,
  isNew: false,
}

const getters = {
  getCustomModuleListById: state => id => {
    return state.customModuleList.find(data => data.id === id)
  },
}

const actions = {
  updateSearchQuery: ({ commit }, payload) => {
    commit('UPDATE_SEARCH_QUERY', payload)
  },
  fetchCustomModuleList: ({ commit, state }, payload) => {
    let params = ''
    if (payload.moduleName) {
      params += params + 'moduleName=' + payload.moduleName
    }
    if (payload.page) {
      params = params + '&page=' + payload.page + '&perPage=' + state.perPage
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
    if (payload.viewname) {
      params = params + '&viewName=' + payload.viewname
    }
    if (payload.includeParentFilter) {
      params = params + '&includeParentFilter=' + payload.includeParentFilter
    }
    let url = 'v2/module/data/list' + '?' + params
    return http.get(url).then(function(response) {
      commit('SET_CUSTOM_MODULES', response.data.result.moduleDatas)
    })
  },
  fetch({ commit }, payload) {
    return http
      .get('v2/module/data/' + payload.id + '?moduleName=' + payload.moduleName)
      .then(function(response) {
        commit('SET_CUSTOM_MODULE', response.data.result.moduleData)
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
  SET_CUSTOM_MODULES: (state, payload) => {
    if (state.isNew) {
      state.customModuleList = []
      state.customModuleList =
        state.perPage && state.currentPage > 1 ? payload : payload
      state.canLoadMore = payload.length === state.perPage
    } else {
      state.customModuleList =
        state.perPage && state.currentPage > 1
          ? [...state.customModuleList, ...payload]
          : payload
      state.canLoadMore = payload.length === state.perPage
    }
  },
  SET_CUSTOM_MODULE: (state, payload) => {
    state.customModuledata = null
    let idx = state.customModuleList.findIndex(rt => rt.id === payload.id)
    if (idx === -1) {
      state.customModuleList.push(payload)
    }
    state.customModuledata = payload
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
