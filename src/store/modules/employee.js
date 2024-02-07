import http from 'util/http'
import { API } from '@facilio/api'
import getProperty from 'dlv'
import { isEmpty } from '@facilio/utils/validation'

const state = {
  currentView: null,
  employess: [],
  currentPage: 1,
  perPage: 50,
  canLoadMore: false,
  quickSearchQuery: null,
  filters: null,
  includeParentFilter: null,
  isNew: false,
  employeeDetail: null,
}

// actions
const actions = {
  updateSearchQuery: ({ commit, dispatch, state }, payload) => {
    commit('UPDATE_SEARCH_QUERY', payload)
  },
  fetchEmployeeDetails({ commit, dispatch, state }, payload) {
    return http
      .post('/v2/employee/details', { recordId: payload.id })
      .then(function(response) {
        commit('SET_CONTACT_DATA', response.data.result.employee)
      })
  },
  fetchEmployeesList: async ({ commit }, payload) => {
    let moduleName = 'employee'
    let { search, page, viewname, filters, perPage, force = false } =
      payload || {}
    let params = {
      fetchOnlyViewGroupColumn: true,
      ...(payload || {}),
      viewName: viewname,
      filters: !isEmpty(filters) ? JSON.stringify(filters) : null,
      perPage: perPage || 50,
      withoutCustomButtons: true,
      force: null,
    }
    if (page) commit('SET_CURRENT_VIEW', payload)
    if (filters) commit('UPDATE_FILTERS', filters)
    if (search) commit('UPDATE_SEARCH_QUERY', search)
    let config = { force, uniqueKey: `${moduleName}_LIST` }
    let { list, error } = await API.fetchAll(moduleName, params, config)

    if (error) {
      console.warn(`${moduleName} fetchRecord API failed or cancelled`)
      let { isCancelled } = error || {}
      if (!isCancelled) throw error
    } else {
      commit('SET_CONTACTS', list || [])
    }
  },
  reload: ({ commit, dispatch, state }) => {
    let url = `/v2/employee/views/${state.currentView}?page=${state.currentPage}&perPage=${state.perPage}`
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
      commit('SET_CONTACTS', response.data.result.employees)
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
    state.employeeDetail = payload
  },
  SET_CONTACTS: (state, payload) => {
    if (state.isNew) {
      state.employess =
        state.perPage && state.currentPage > 1 ? payload : payload
      state.canLoadMore = payload.length === state.perPage
    } else {
      state.employess =
        state.perPage && state.currentPage > 1
          ? [...state.employess, ...payload]
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
