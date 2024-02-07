import http from 'util/http'

const state = {
  events: [],
  currentevent: '',
}

// getters
const getters = {
  getEventById: (state, getters) => id => {
    return state.events.find(event => event.id === id)
  },
}

// actions
const actions = {
  fetchEvents: ({ commit, dispatch, state }, payload) => {
    let url = '/event/' + payload.viewname
    if (payload.isCount) {
      url = url + '?isCount=' + payload.isCount
    }
    return http.get(url).then(function(response) {
      console.log('event payload ------>', response.data.events)
      commit('SET_EVENTS', response.data.events)
    })
  },
  summaryEvents: ({ commit, dispatch, state }, payload) => {
    let url = '/event/summary/' + payload.id
    return http.get(url).then(function(response) {
      console.log('event payload ------>', response.data.event)
      commit('SET_CURRENT_EVENT', response.data)
    })
  },
}

// mutations
const mutations = {
  SET_EVENTS: (state, payload) => {
    state.events = payload
  },
  SET_CURRENT_EVENT: (state, payload) => {
    state.currentevent = payload
    let existingEvent = state.events.find(event => event.id === payload.id)
    if (existingEvent) {
      let existingEventIdx = state.events.indexOf(existingEvent)
      state.events.splice(existingEventIdx, 1, payload)
    } else {
      state.events.splice(0, 0, payload)
    }
  },
}

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations,
}
