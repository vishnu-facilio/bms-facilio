// import http from 'util/http'

const state = {
  subscribers: {},
}

// actions
const actions = {
  subscribe: ({ commit, dispatch, state }, payload) => {
    commit('SUBSCRIBE', payload)
  },

  unsubscribe: ({ commit, dispatch, state }, payload) => {
    commit('UNSUBSCRIBE', payload)
  },

  publish({ commit, state }, data) {
    if (state.subscribers[data.content.uniqueKey]) {
      state.subscribers[data.content.uniqueKey](data)
    }
  },
}

// mutations
const mutations = {
  SUBSCRIBE: (state, payload) => {
    state.subscribers[payload.key] = payload.callback
  },

  UNSUBSCRIBE: (state, payload) => {
    delete state.subscribers[payload.key]
  },
}

export default {
  namespaced: true,
  state,
  actions,
  mutations,
}
