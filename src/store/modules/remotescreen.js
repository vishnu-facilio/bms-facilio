const state = {
  refreshCount: 0,
}

// actions
const actions = {
  REFRESH: ({ commit, dispatch, state }, payload) => {
    commit('SET_REFRESH', 1)
  },
}

// mutations
const mutations = {
  SET_REFRESH: (state, payload) => {
    state.refreshCount = state.refreshCount + payload
  },
}

export default {
  namespaced: true,
  state,
  actions,
  mutations,
}
