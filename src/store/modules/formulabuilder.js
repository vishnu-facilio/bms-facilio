import util from 'util/util'
import http from 'util/http'
const state = {
  assetList: [], // Used only for edit. Selected assets in each expression
  assetReadingFields: [],
  spaceReadingFields: [],
  workorderFields: [],
  assetFields: [],
  spaceList: [],
  spaceFields: [],
  baselines: [],
  readingPromise: {},
  assetPromise: null,
  assetFieldPromise: null,
  spacePromise: null,
  spaceFieldPromise: null,
  baselinePromise: null,
}

const getters = {
  getAsset: state => id => {
    return state.assetList.find(obj => obj.id === id)
  },
  getSpace: state => id => {
    return state.spaceList.find(obj => obj.id === id)
  },

  getSpaceReadings: state => id => {
    let obj = state.spaceReadingFields.find(obj => obj.id === id)
    if (obj) {
      return obj.fields
    }
    return []
  },
  getAssetReadings: state => (id, isCategoryId) => {
    let categoryId
    if (isCategoryId) {
      categoryId = id
    } else {
      let asset = getters.getAsset(state)(id)
      categoryId = asset && asset.category ? asset.category.id : null
    }
    if (categoryId) {
      let obj = state.assetReadingFields.find(obj => obj.id === categoryId)
      if (obj) {
        return obj.fields
      }
    }
    return []
  },
}

// actions
const actions = {
  loadSpaceDetails({ commit, state }, spaceId) {
    let promise = util
      .loadSpace(null, null, [{ key: 'id', operator: '=', value: spaceId }])
      .then(response => {
        let spaceList = []
        if (response) {
          commit('ADD_SPACE', response.basespaces[0])
        }
      })
  },
  loadSpaces({ commit, state }) {
    if (!state.spaceList.length) {
      if (!state.spacePromise) {
        let promise = util
          .loadSpace()
          .then(response => {
            let spaceList = []
            if (response) {
              spaceList.push(...response.basespaces)
            }
            commit('SET_SPACE', spaceList)
          })
          .finally(_ => {
            commit('SET_SPACE_PROMISE', null)
          })
        commit('SET_SPACE_PROMISE', promise)
      }
      return state.spacePromise
    }
  },
  loadSpaceFields({ commit, state }) {
    if (!state.spaceFields.length) {
      if (!state.spaceFieldPromise) {
        let promise = util
          .loadFields('basespace')
          .then(fields => {
            commit('SET_SPACE_FIELDS', fields)
          })
          .finally(_ => {
            commit('SET_SPACE_FIELD_PROMISE', null)
          })
        commit('SET_SPACE_FIELD_PROMISE', promise)
      }
      return state.spaceFieldPromise
    } else return Promise.resolve()
  },
  loadSpaceReadings({ commit, state, getters }, spaceId) {
    if (spaceId && !getters.getSpaceReadings(spaceId).length) {
      if (!state.readingPromise[spaceId]) {
        let promise = util
          .loadSpaceReadingFields(spaceId)
          .then(fields => {
            let rFields = []
            if (this.module === 'enpi') {
              rFields.push({
                id: 'currentEnpi',
                name: '${currentField}',
                displayName: 'Current Enpi',
                module: { name: '${currentModule}' },
              }) // eslint-disable-line no-template-curly-in-string
            }
            rFields.push(...fields)
            commit('SET_SPACE_READINGS', { id: spaceId, fields: rFields })
          })
          .finally(_ => {
            commit('SET_READING_PROMISE', { id: spaceId, promise: null })
          })
        commit('SET_READING_PROMISE', { id: spaceId, promise: promise })
      }
      return state.readingPromise[spaceId]
    } else return Promise.resolve()
  },

  loadAssets({ commit, state }, { assetCategory, orgId }) {
    let tempOrgId = 6
    if (!state.assetList.length) {
      if (!state.assetPromise) {
        let paging
        if (orgId === tempOrgId) {
          paging = { page: 1, perPage: 10000 }
        } else {
          paging = { page: 1, perPage: 5000 }
        }
        let promise = util
          .loadAsset({
            withReadings: true,
            fetchPrimaryDetails: true,
            paging: paging,
          })
          .then(response => {
            let assetList = []
            if (response.assets) {
              if (assetCategory && assetCategory.id) {
                assetList.push({
                  id: 'resourceId',
                  name: 'Current Asset',
                  category: this.assetCategory,
                })
              }
              assetList.push(...response.assets)
            }
            commit('SET_ASSET', assetList)
          })
          .finally(_ => {
            commit('SET_ASSET_PROMISE', null)
          })
        commit('SET_ASSET_PROMISE', promise)
      }
      return state.assetPromise
    } else return Promise.resolve()
  },

  loadAssetFields({ commit, state }) {
    if (!state.assetFields.length) {
      if (!state.assetFieldPromise) {
        let promise = util
          .loadFields('asset')
          .then(fields => {
            commit('SET_ASSET_FIELDS', fields)
          })
          .finally(() => {
            commit('SET_ASSET_FIELD_PROMISE', null)
          })
        commit('SET_ASSET_FIELD_PROMISE', promise)
      }
      return state.assetFieldPromise
    } else return Promise.resolve()
  },
  loadWorkorderFields({ commit, state }) {
    if (!state.workorderFields.length) {
      if (!state.workorderFieldPromise) {
        let promise = util
          .loadFields('workorder', true)
          .then(fields => {
            commit('SET_WORKORDER_FIELDS', fields)
          })
          .finally(() => {
            commit('SET_WORKORDER_FIELD_PROMISE', null)
          })
        commit('SET_WORKORDER_FIELD_PROMISE', promise)
      }
      return state.workorderFieldPromise
    } else return Promise.resolve()
  },
  loadAssetReadings({ commit, state, getters }, payload) {
    if (
      !getters.getAssetReadings(payload.assetCategoryId, true).length ||
      payload.reload
    ) {
      if (!state.readingPromise[payload.assetCategoryId]) {
        let promise = util
          .loadAssetReadingFields(-1, payload.assetCategoryId)
          .then(fields => {
            commit('SET_ASSET_READINGS', {
              id: payload.assetCategoryId,
              fields: fields,
            })
          })
          .finally(() => {
            commit('SET_READING_PROMISE', {
              id: payload.assetCategoryId,
              promise: null,
            })
          })
        commit('SET_READING_PROMISE', {
          id: payload.assetCategoryId,
          promise: promise,
        })
      }
      return state.readingPromise[payload.assetCategoryId]
    } else return Promise.resolve()
  },

  loadBaselines({ commit, state }) {
    if (!state.baselines.length) {
      if (!state.baselinePromise) {
        let promise = http
          .get('/baseline/all')
          .then(function(response) {
            let baselines = []
            if (response.status === 200) {
              baselines = response.data || []
            }
            commit('SET_BASELINE', baselines)
          })
          .finally(_ => {
            commit('SET_BASELINE_PROMISE', null)
          })
        commit('SET_BASELINE_PROMISE', promise)
      }
      return state.baselinePromise
    } else return Promise.resolve()
  },

  initData({ commit, state }) {
    commit('INIT_DATA')
  },
}

// mutations
const mutations = {
  SET_SPACE: (state, payload) => {
    state.spaceList = payload
  },

  SET_SPACE_FIELDS: (state, payload) => {
    state.spaceFields = payload || []
  },

  SET_SPACE_READINGS: (state, payload) => {
    state.spaceReadingFields.push(payload)
  },

  SET_ASSET: (state, payload) => {
    state.assetList = payload
  },

  SET_ASSET_FIELDS: (state, payload) => {
    state.assetFields = payload || []
  },

  SET_WORKORDER_FIELDS: (state, payload) => {
    state.workorderFields = payload || []
  },

  SET_ASSET_READINGS: (state, payload) => {
    state.assetReadingFields.push(payload)
  },

  SET_READING_PROMISE: (state, payload) => {
    state.readingPromise[payload.id] = payload.promise
  },

  SET_ASSET_PROMISE: (state, payload) => {
    state.assetPromise = payload
  },

  SET_SPACE_PROMISE: (state, payload) => {
    state.spacePromise = payload
  },

  SET_ASSET_FIELD_PROMISE: (state, payload) => {
    state.assetFieldPromise = payload
  },

  SET_WORKORDER_FIELD_PROMISE: (state, payload) => {
    state.workorderFieldPromise = payload
  },

  SET_SPACE_FIELD_PROMISE: (state, payload) => {
    state.spaceFieldPromise = payload
  },

  SET_BASELINE: (state, payload) => {
    state.baselines = payload
  },

  SET_BASELINE_PROMISE: (state, payload) => {
    state.baselinePromise = payload
  },

  ADD_SPACE: (state, payload) => {
    if (!state.spaceList.some(space => space.id == payload.id)) {
      state.spaceList.push(payload)
    }
  },

  INIT_DATA: state => {
    state.assetList = []
    state.assetReadingFields = []
    state.spaceReadingFields = []
    state.assetFields = []
    state.workorderFields = []
    state.spaceList = []
    state.spaceFields = []
    state.baselines = []
    state.readingPromise = {}
    state.assetPromise = null
    state.assetFieldPromise = null
    state.workorderFieldPromise = null
    state.spacePromise = null
    state.spaceFieldPromise = null
    state.baselinePromise = null
  },
}

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations,
}
