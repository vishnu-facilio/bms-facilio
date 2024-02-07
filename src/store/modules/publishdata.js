import Vue from 'vue'
import { Message } from 'element-ui'
import util from 'util/util'

const state = {
  data: {},
  info: {}, // for discover count
  setValue: {},
}

// actions
const actions = {
  publish({ commit, state }, message) {
    if (message.content.data) {
      if (
        message.content.data.commandEnum === 'SET' &&
        state.data[message.content.data.id]
      ) {
        let data = state.data[message.content.data.id]
        if (data.additionalInfo && data.additionalInfo.assetName) {
          // commit('SET_VALUE', data.additionalInfo)
          setTimeout(() => {
            util
              .loadLatestReading(
                data.additionalInfo.assetId,
                false,
                false,
                null,
                data.additionalInfo.fieldId
              )
              .then(fields => {
                if (fields && fields.length) {
                  commit('SET_VALUE', {
                    data: data.additionalInfo,
                    value: fields[0].value,
                  })
                }
              })
          }, 3000)
        } else {
          Message.success('Reading set successfully')
        }
      } else if (
        [
          'CONFIGURE',
          'UNCONFIGURE',
          'SUBSCRIBE',
          'UNSUBSCRIBE',
          'DISCOVER',
        ].includes(message.content.data.commandEnum)
      ) {
        commit('SET_DATA', message.content.data)
      }
    } else if (message.content.info) {
      // For adding the count of discovered points
      commit('SET_INFO', message.content.info)
    }
  },

  publishFailure({ commit, state }, message) {
    if (message.content.data) {
      commit('HANDLE_FAILURE', message.content.data)
    }
  },

  listen({ commit }, payload) {
    commit('LISTEN_DATA', payload)
  },
}

// mutations
const mutations = {
  SET_DATA: (state, payload) => {
    if (state.data[payload.id]) {
      Vue.set(state.data[payload.id], 'response', payload)
    }
  },

  SET_INFO: (state, payload) => {
    if (state.info[payload.controllerId]) {
      Vue.set(state.info, payload.controllerId, payload)
    }
  },

  SET_VALUE: (state, payload) => {
    let { fieldName, assetName, assetId, fieldId } = payload.data
    Message.success(
      'Value for ' +
        fieldName +
        ' reading of ' +
        assetName +
        ' set successfully'
    )
    if (!state.setValue[assetId]) {
      Vue.set(state.setValue, assetId, {})
    }
    Vue.set(state.setValue[assetId], fieldId, payload.value)
  },

  HANDLE_FAILURE: (state, payload) => {
    if (payload.commandEnum === 'PING' || payload.messages[0].data.isPing) {
      // get agent from additional info
      Message.error(
        'Agent ' +
          payload.messages[0].data.pingAgent +
          ' is not active. Please try after some time'
      )
    }
    if (state.data[payload.id]) {
      Vue.set(state.data[payload.id], 'failure', payload)
      Vue.set(state.data[payload.id], 'response', 'failed')
    }
  },

  LISTEN_DATA: (state, payload) => {
    Vue.set(state.data, payload.id, {
      createdTime: payload.createdTime,
      additionalInfo: payload.additionalInfo,
      controllerId: payload.controllerId,
      command: payload.commandEnum,
    })

    Vue.set(state.info, payload.controllerId, {})
  },
}

export default {
  namespaced: true,
  state,
  actions,
  mutations,
}
