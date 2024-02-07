// To do  , once WMS channels implemented ,check and move to common code
const state = {
  botSocketHandler: null,
}
const actions = {
  botMessage({ dispatch }, socketMessage) {
    dispatch('publishMessageToBotHandler', {
      messageType: 'BOT_MESSAGE',
      message: socketMessage,
    })
  },
  userMessage({ dispatch }, socketMessage) {
    dispatch('publishMessageToBotHandler', {
      messageType: 'USER_MESSAGE',
      message: socketMessage,
    })
  },

  subscribeToChatBotChannel({ commit }, handler) {
    console.log('subscribing to bot', handler)
    commit('addSubscriber', handler)
  },
  unsubscribeFromBotChannel({ commit }) {
    commit('removeSubscriber')
  },
  publishMessageToBotHandler({ state }, payload) {
    if (state.botSocketHandler) {
      payload.message = JSON.parse(payload.message.content.result)
      state.botSocketHandler(payload)
    }
  },
}
const mutations = {
  addSubscriber(state, handler) {
    state.botSocketHandler = handler
  },

  removeSubscriber(state) {
    state.botSocketHandler = null
  },
}

export default {
  namespaced: true,
  actions,
  mutations,
  state,
}
