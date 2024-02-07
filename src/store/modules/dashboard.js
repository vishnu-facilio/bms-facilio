import { isEmpty } from '@facilio/utils/validation'

// sync dashboard filter current state here in store . can be used by print/export and other features
const state = {
  dbTimelineFilter: null,
  dbUserFilters: null,
}

const getters = {
  dbFilterQueryParam(state) {
    if (state.dbTimelineFilter || state.dbUserFilters) {
      let queryParam = {}
      if (!isEmpty(state.dbTimelineFilter)) {
        queryParam.timelineFilter = state.dbTimelineFilter
      }
      if (!isEmpty(state.dbUserFilters)) {
        queryParam.userFilters = state.dbUserFilters
      }
      return encodeURIComponent(JSON.stringify(queryParam))
    } else {
      return null
    }
  },
}

const mutations = {
  SET_DB_TIME_LINE_FILTER(state, payload) {
    state.dbTimelineFilter = payload
  },
  SET_DB_USER_FILTERS(state, payload) {
    state.dbUserFilters = payload
  },
}
const actions = {
  persistDbTimelineFilter({ state, commit }, payload) {
    commit('SET_DB_TIME_LINE_FILTER', payload)
  },
  persistDbUserFilters({ state, commit }, payload) {
    commit('SET_DB_USER_FILTERS', payload)
  },
}

export default {
  namespaced: true,
  state,
  actions,
  mutations,
  getters,
}
