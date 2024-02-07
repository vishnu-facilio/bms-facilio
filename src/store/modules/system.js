const state = {
  reload: false,
  canLoadMore: false,
}

// getters
const getters = {}

// actions
const actions = {
  reload({ commit, state }, event) {
    commit('SET_RELOAD')
  },
}

// mutations
const mutations = {
  SET_RELOAD(state, payload) {
    state.reload = true
  },
}

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations,
}
