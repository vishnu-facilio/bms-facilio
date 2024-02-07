const state = {
  ruleTemplate: null,
  selectedTemplate: null,
  selectedTemplateNew: null,
}
const actions = {
  setSelectedTemplate: ({ commit, dispatch, state }, payload) => {
    commit('SET_SELECTED_TEMPLATE', payload)
  },
  setSelectedTemplateNew: ({ commit, dispatch, state }, payload) => {
    commit('SET_SELECTED_TEMPLATE_NEW', payload)
  },
  setRuleContext: ({ commit, dispatch, state }, payload) => {
    commit('SET_RULE_CONTEXT', payload)
  },
}
const mutations = {
  SET_SELECTED_TEMPLATE: (state, payload) => {
    state.selectedTemplate = payload
  },
  SET_SELECTED_TEMPLATE_NEW: (state, payload) => {
    state.selectedTemplateNew = payload
  },
  SET_RULE_CONTEXT: (state, payload) => {
    state.ruleContext = payload
  },
}

export default {
  namespaced: true,
  state,
  actions,
  mutations,
}
