const state = {
  sharedData: null,
}

// getters
const getters = {}

// actions
const actions = {
  saveSharedData: ({ commit, dispatch, state }, payload) => {
    commit('SET_SHARED_DATA', payload)
  },
}

// mutations
const mutations = {
  SET_SHARED_DATA: (state, payload) => {
    state.sharedData = payload
  },
}

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations,
}
