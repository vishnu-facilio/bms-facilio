import { isEmpty } from '@facilio/utils/validation'
import { genericCachedGet } from 'src/store/utils/index.js'

const state = {
  glimpseDetails: {},
  glimpseFields: {},
  deletedGlimpse: {},
}

const actions = {
  loadGlimpse({ commit }, params) {
    let {
      moduleName,
      id,
      lookupFieldName,
      lookupModuleName,
      recordId,
      forceUpdate = false,
    } = params

    if (!isEmpty(moduleName)) {
      return genericCachedGet({ commit, state }, 'glimpseFields', {
        url: `v3/glimpse/${moduleName}/${id}/${lookupFieldName}`,
        extraction: data => {
          return { data, lookupModuleName, recordId }
        },
        forceUpdate,
        commitName: 'ADD_QUICK_SUMMARY',
        moduleName,
      })
    }
  },
  loadGlimpseMetaFields({ commit }, params) {
    let { moduleName, forceUpdate = true } = params

    if (!isEmpty(moduleName)) {
      return genericCachedGet({ commit, state }, 'glimpseDetails', {
        url: `v1/setup/modules/${moduleName}/setting/glimpse`,
        extraction: data => {
          return { moduleName, data }
        },
        forceUpdate,
        commitName: 'ADD_META_FIELDS',
        moduleName,
      })
    }
  },
  resetGlimpse({ commit }) {
    commit('RESET_QUICK_SUMMARY')
  },
  resetGlimpseDetails({ commit }) {
    commit('RESET_GLIMPSE')
  },
}

const mutations = {
  ADD_QUICK_SUMMARY(state, { data }) {
    let { lookupModuleName, data: details, recordId } = data
    let { [lookupModuleName]: metaFields = {}, message } = details

    if (!isEmpty(message)) state.deletedGlimpse[recordId] = message
    else state.glimpseFields[metaFields.id] = metaFields
  },
  RESET_QUICK_SUMMARY(state) {
    state.glimpseFields = {}
  },
  RESET_GLIMPSE(state) {
    state.glimpseFields = {}
    state.glimpseDetails = {}
  },
  ADD_META_FIELDS(state, { data }) {
    let { moduleName, data: { result: { glimpse } = {} } = {} } = data

    if (!isEmpty(glimpse)) state.glimpseDetails[moduleName] = glimpse
  },
}

export default {
  namespaced: true,
  state,
  actions,
  mutations,
}
