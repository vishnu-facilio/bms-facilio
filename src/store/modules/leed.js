import http from 'util/http'

const state = {
  leeds: [],
  isLoginRequired: false,
  viewlayout: {},
}

// getters
const getters = {
  getLeedById: (state, getters) => id => {
    return state.leeds.find(leed => leed.id === id)
  },
}

// actions
const actions = {
  fetchLeeds: ({ commit, dispatch, state }) => {
    let url = '/leed/leedlist'
    return http.get(url).then(function(response) {
      commit('SET_LEEDS', response.data)
    })
  },
}

// mutations
const mutations = {
  SET_LEEDS: (state, payload) => {
    state.isLoginRequired = payload.isLoginRequired
    state.leeds = payload.leedList ? payload.leedList : []
    state.viewlayout = payload.viewlayout
  },
}

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations,
}
