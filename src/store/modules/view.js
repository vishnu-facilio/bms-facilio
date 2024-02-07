import { API } from '@facilio/api'
import http from 'util/http'
import { isEmpty } from '@facilio/utils/validation'

const state = {
  views: [],
  groupViews: [],
  currentViewDetail: {},
  metaInfo: {},
  isLoading: false,
  detailLoading: false,
  promiseCache: {
    metaInfo: {},
    currentViewDetail: {},
  },
}

const getters = {
  getViewsFolderList: state => () => {
    let { groupViews } = state
    let foldersList = []
    if (!isEmpty(groupViews)) {
      foldersList = groupViews.map(group => {
        if (!isEmpty(group.views)) {
          group.views.forEach(view => {
            view.criteria = null
          })
        }
        return group
      })
    }
    return foldersList
  },
  getCurrentViewDetail: state => () => {
    let { currentViewDetail } = state
    return currentViewDetail
  },
}

const actions = {
  clearViews({ commit }) {
    commit('INIT_VIEW')
  },
  loadViews({ commit }, moduleName) {
    if (!moduleName) {
      return Promise.resolve()
    }
    return new Promise((resolve, reject) => {
      API.get('/view', { moduleName }).then(({ data, error }) => {
        if (!error) {
          commit('VIEW_LIST', data.views || [])
          resolve({ data, error })
        } else {
          reject({ error })
        }
      })
    }).catch(() => {})
  },
  loadGroupViews({ commit }, payload) {
    if (!payload.moduleName) {
      return Promise.resolve({
        data: null,
        error: { message: 'ModuleName is not defined' },
      })
    }
    return new Promise((resolve, reject) => {
      commit('SET_LOADING', true)
      let {
        moduleName,
        status = null,
        appId,
        restrictPermissions,
        groupType = 1,
        viewType = 1,
        fromBuilder,
      } = payload
      let params = { moduleName, groupStatus: status, groupType, viewType }
      if (!isEmpty(appId)) {
        params.appId = appId
      }
      if (!isEmpty(fromBuilder)) {
        params.fromBuilder = true
      }
      if (!isEmpty(restrictPermissions)) {
        params.restrictPermissions = true
      }
      API.get('/v2/views/viewList', params, { uniqueKey: 'GROUP_VIEW_LIST' })
        .then(({ data, error }) => {
          if (!error) {
            commit('GROUP_VIEW_LIST', data.groupViews || data.views)
            resolve({ data, error })
            commit('SET_LOADING', false)
          } else {
            let { isCancelled } = error || {}
            !isCancelled && reject({ error })
          }
        })
        .catch(() => {
          console.warn(
            `Fetching /v2/views/viewList cancelled for ${moduleName}`
          )
          commit('SET_LOADING', false)
        })
    }).catch(() => {})
  },
  loadViewDetail({ commit, state }, payload) {
    if (!payload.viewName || !payload.moduleName) {
      return
    }

    let { moduleName, viewName, appId, parentView } = payload
    let { fetch } = payload || false
    let cache = state.promiseCache.currentViewDetail
    let {
      promise,
      moduleName: existingModuleName,
      viewName: existingViewName,
    } = cache
    if (
      existingModuleName === moduleName &&
      existingViewName === viewName &&
      promise
    ) {
      return promise
    } else if (
      existingModuleName === moduleName &&
      existingViewName === viewName &&
      !isEmpty(state.currentViewDetail) &&
      !fetch
    ) {
      return Promise.resolve(state.currentViewDetail)
    } else {
      if (!isEmpty(existingModuleName) && existingModuleName !== moduleName) {
        try {
          API.cancel({ uniqueKey: `${existingModuleName}_VIEW_DETAIL` })
        } catch (error) {
          console.warn(
            `Failed to cancel previous module (${existingModuleName}) view details fetch api`
          )
        }
      }

      cache = {
        moduleName,
        viewName,
        promise: new Promise((resolve, reject) => {
          commit('SET_VIEW_LOADING', true)

          let url = `/v2/views/${viewName}`
          let params = {
            moduleName,
            appId: !isEmpty(appId) ? appId : null,
            parentView: !isEmpty(parentView) ? parentView : null,
          }

          // Passing moduleName as uniqueKey to cancel any view pending requests

          API.get(url, params, { uniqueKey: `${moduleName}_VIEW_DETAIL` })
            .then(({ error, data }) => {
              if (!error) {
                commit('CURRENT_VIEW_INFO', data.viewDetail)
                resolve(data.viewDetail)
                commit('SET_VIEW_LOADING', false)
              } else {
                let { isCancelled } = error || {}
                !isCancelled && reject(error)
              }
            })
            .catch(error => {
              console.warn(
                `Fetching view details failed for ${moduleName} - ${viewName}`
              )
              commit('SET_VIEW_LOADING', false)
              reject(error)
            })
            .finally(() => {
              cache.promise = null
            })
        }).catch(() =>
          console.warn(
            `Fetching /v2/views/${viewName} cancelled for ${moduleName}`
          )
        ),
      }
      state.promiseCache.currentViewDetail = cache

      return cache.promise
    }
  },

  savesorting({ commit, dispatch }, payload) {
    return new Promise(resolve => {
      let url = '/view/customizeSortColumns'
      return http.post(url, payload).then(function(response) {
        commit('SET_CURRENT_SORT_FIELDS', response.data.sortFields)
        if (!payload.skipDispatch) {
          // DO NOT ADD ANY MORE MODULES HERE, TO RUN SOMETHING AFTER SORT IS
          // SAVED USE dispatch(view / savesorting).then(() => {...dosomething}) in your component
          if (payload.moduleName === 'workorder') {
            dispatch('workorder/updateSorting', payload, {
              root: true,
            })
          } else if (payload.moduleName === 'alarm') {
            dispatch('alarm/reload', null, {
              root: true,
            })
          } else if (payload.moduleName === 'newreadingalarm') {
            dispatch('newAlarm/reload', null, {
              root: true,
            })
          } else if (payload.moduleName === 'itemTransactions') {
            dispatch('itemtransaction/reload', null, {
              root: true,
            })
          } else if (payload.moduleName === 'toolTransactions') {
            dispatch('tooltransaction/reload', null, {
              root: true,
            })
          } else if (payload.moduleName === 'receivable') {
            dispatch('receivable/reload', null, {
              root: true,
            })
          } else if (payload.moduleName === 'purchasecontracts') {
            dispatch('purchasecontract/reload', null, {
              root: true,
            })
          } else if (payload.moduleName === 'labourcontracts') {
            dispatch('labourcontract/reload', null, {
              root: true,
            })
          } else if (payload.moduleName === 'purchasedItem') {
            dispatch('purchaseditem/reload', null, {
              root: true,
            })
          } else if (payload.moduleName === 'inventoryrequest') {
            dispatch('inventoryrequest/reload', null, {
              root: true,
            })
          } else if (payload.moduleName === 'shift') {
            dispatch('shift/reload', null, {
              root: true,
            })
          } else if (payload.moduleName === 'break') {
            dispatch('shiftbreak/reload', null, {
              root: true,
            })
          } else if (payload.moduleName === 'attendance') {
            dispatch('attendance/reload', null, {
              root: true,
            })
          } else if (payload.moduleName === 'warrantycontracts') {
            dispatch('warrantycontract/reload', null, {
              root: true,
            })
          } else if (payload.moduleName === 'rentalleasecontracts') {
            dispatch('rentalleasecontract/reload', null, {
              root: true,
            })
          } else if (payload.moduleName === 'workpermit') {
            dispatch('workpermit/reload', null, {
              root: true,
            })
          } else if (payload.moduleName === 'serviceRequest') {
            dispatch('servicerequest/reload', null, {
              root: true,
            })
          } else if (payload.moduleName === 'safetyPlan') {
            dispatch('safetyplanModule/reload', null, {
              root: true,
            })
          } else if (payload.moduleName === 'hazard') {
            dispatch('hazardModule/reload', null, {
              root: true,
            })
          } else if (payload.moduleName === 'precaution') {
            dispatch('precautionModule/reload', null, {
              root: true,
            })
          } else if (payload.moduleName === 'client') {
            dispatch('client/reload', null, {
              root: true,
            })
          }
          // DO NOT ADD ANY MORE MODULES HERE, TO RUN SOMETHING AFTER SORT IS
          // SAVED USE dispatch(view / savesorting).then(() => {...dosomething}) in your component
        }
        resolve()
      })
    })
  },

  loadModuleMeta({ commit, state }, moduleName) {
    let cache = state.promiseCache.metaInfo
    let { promise, moduleName: existingModuleName } = cache

    if (existingModuleName === moduleName && promise) {
      return promise
    } else if (existingModuleName === moduleName && !isEmpty(state.metaInfo)) {
      return Promise.resolve(state.metaInfo)
    } else if (moduleName) {
      if (existingModuleName) {
        // Cancel any pending requests
        try {
          API.cancel({ uniqueKey: `${existingModuleName}_META_INFO` })
        } catch (error) {
          console.warn(
            `Failed to cancel previous module (${existingModuleName}) meta fetch api`
          )
        }
      }

      commit('SET_META_INFO', null)
      cache = {
        moduleName,
        promise: new Promise((resolve, reject) => {
          let url = '/module/meta'
          let params = { moduleName }

          // Passing moduleName as uniqueKey to cancel any metaInfo pending requests
          API.get(url, params, { uniqueKey: `${moduleName}_META_INFO` })
            .then(({ error, data }) => {
              if (!error) {
                commit('SET_META_INFO', data.meta)
                resolve(data.meta)
              } else {
                reject()
              }
            })
            .catch(error => {
              console.warn(`Fetching /module/meta failed for ${moduleName}`)
              reject(error)
            })
            .finally(() => {
              cache.promise = null
            })
        }).catch(() =>
          console.warn(`Fetching /module/meta cancelled for ${moduleName}`)
        ),
      }
      state.promiseCache.metaInfo = cache

      return cache.promise
    }
  },
  loadModuleMetaForControlPoints({ commit }, moduleName) {
    return API.get('/controlAction/metaData', { moduleName }).then(
      ({ error, data }) => {
        if (error) {
          console.warn(
            `Fetching /controlAction/metaData failed for ${moduleName}`
          )
        } else if (data.meta) {
          commit('SET_META_INFO', data.meta)
          return data.meta
        }
      }
    )
  },
  customizeViews({ commit }, payload) {
    return new Promise((resolve, reject) => {
      API.post('/v2/views/customizeView', payload).then(({ error, data }) => {
        if (!error) {
          commit('GROUP_VIEW_LIST', data.groupViews || data.views)
          resolve({ error, data })
        } else {
          reject({ error })
        }
      })
    })
  },
  deleteView({ commit }, payload) {
    return new Promise((resolve, reject) => {
      let { viewData, index, viewIndex } = payload
      API.post('/view/delete', viewData).then(({ error, data }) => {
        if (!error) {
          let { groupViews } = state
          groupViews[index].views.splice(viewIndex, 1)
          commit('GROUP_VIEW_LIST', groupViews)
          resolve({ error, data })
        } else {
          reject({ error })
        }
      })
    })
  },
  customizeFolders({ commit }, payload) {
    return new Promise((resolve, reject) => {
      API.post('/v2/views/customizeViewGroups', payload).then(
        ({ error, data }) => {
          if (!error) {
            commit('GROUP_VIEW_LIST', data.groupViews || data.views)
            resolve({ error, data })
          } else {
            reject({ error })
          }
        }
      )
    })
  },
  addFolder({ commit }, payload) {
    return new Promise((resolve, reject) => {
      API.post('/v2/views/addGroup', payload).then(({ error, data }) => {
        if (!error) {
          let { viewGroup } = data
          let { groupViews } = state
          groupViews.push(viewGroup)
          commit('GROUP_VIEW_LIST', groupViews)
          resolve({ error, data })
        } else {
          reject({ error })
        }
      })
    })
  },
  updateFolder({ commit }, payload) {
    return new Promise((resolve, reject) => {
      API.post('/v2/views/updateGroup', payload).then(({ error, data }) => {
        if (!error) {
          let { viewGroup } = data
          let { groupViews } = state
          let index = groupViews.findIndex(view => view.name === viewGroup.name)

          if (index !== -1) {
            groupViews.splice(index, 1, viewGroup)
          }
          commit('GROUP_VIEW_LIST', groupViews)
          resolve({ error, data })
        } else {
          reject({ error })
        }
      })
    })
  },
  deleteFolder({ commit }, payload) {
    return new Promise((resolve, reject) => {
      let { viewGroupData, index } = payload
      API.post('/v2/views/deleteGroup', viewGroupData).then(
        ({ error, data }) => {
          if (!error) {
            let { groupViews } = state
            groupViews.splice(index, 1)
            commit('GROUP_VIEW_LIST', groupViews)
            resolve({ error, data })
          } else {
            reject({ error })
          }
        }
      )
    })
  },
  saveNewView({ commit }, view) {
    return new Promise((resolve, reject) => {
      API.post('/view/add', view).then(({ error, data }) => {
        if (!error && data) {
          commit('ADD_VIEW', data)
          commit('ADD_GROUP_VIEW', data)
          resolve(data)
        } else {
          reject(error)
        }
      })
    })
  },
  editView({ commit }, view) {
    return new Promise((resolve, reject) => {
      API.post('/v2/views/edit', view).then(({ error, data }) => {
        if (!error) {
          if (!isEmpty(data.view)) {
            commit('EDIT_VIEW', data.view)
            commit('CURRENT_VIEW_INFO', data.view)
            commit('UPDATE_GROUP_VIEW', data.view)
            commit('UPDATE_COLUMNS', data.view.fields)
          }
          resolve(data)
        } else {
          reject(error)
        }
      })
    })
  },
  customizeView({ commit }, payload) {
    return new Promise((resolve, reject) => {
      API.post('/view/customize', payload).then(({ error, data }) => {
        if (!error) {
          commit('VIEW_LIST', data.views)
          resolve({ error, data })
        } else {
          reject({ error })
        }
      })
    })
  },
  customizeColumns({ commit }, payload) {
    return new Promise((resolve, reject) => {
      API.post('/view/customizeColumns', payload).then(({ error, data }) => {
        if (!error && data) {
          commit('UPDATE_COLUMNS', data.fields)
          resolve(data)
        } else {
          reject(error)
        }
      })
    })
  },
}

const mutations = {
  VIEW_LIST(state, payload) {
    state.views = payload
  },
  GROUP_VIEW_LIST(state, payload) {
    state.groupViews = payload
  },
  ADD_VIEW(state, view) {
    state.views.push(view)
  },
  EDIT_VIEW(state, view) {
    let { id } = view
    let index = state.views.findIndex(view => view.id === id)
    if (index) {
      state.views.splice(index, 1, view)
    }
  },
  ADD_GROUP_VIEW(state, payload) {
    let { groupId } = payload
    let { groupViews = [] } = state
    let selectedGroupIndex = groupViews.findIndex(group => group.id === groupId)
    let selectedGroup = groupViews[selectedGroupIndex] || {}
    if (!isEmpty(selectedGroup)) {
      if (isEmpty(groupViews[selectedGroupIndex].views)) {
        groupViews[selectedGroupIndex].views = []
      }
      groupViews[selectedGroupIndex].views.push(payload)
      state.groupViews = groupViews
    }
  },
  UPDATE_GROUP_VIEW(state, payload) {
    let { groupId, id } = payload
    let { groupViews = [] } = state
    let selectedGroupIndex = groupViews.findIndex(group => group.id === groupId)
    let selectedGroup = groupViews[selectedGroupIndex] || {}
    let { views = [] } = selectedGroup || []

    if (!isEmpty(views)) {
      let viewIndex = views.findIndex(view => view.id === id)

      if (!isEmpty(viewIndex)) {
        groupViews[selectedGroupIndex].views.splice(viewIndex, 1, payload)
      } else {
        groupViews[selectedGroupIndex].views.push(payload)
      }
    } else {
      groupViews[selectedGroupIndex].views = []
      groupViews[selectedGroupIndex].views.push(payload)
    }
    state.groupViews = groupViews
  },
  CURRENT_VIEW_INFO: (state, payload) => {
    state.currentViewDetail = payload
  },
  UPDATE_COLUMNS: (state, payload) => {
    state.currentViewDetail.fields = payload
  },
  SET_META_INFO: (state, payload) => {
    state.metaInfo = payload
  },
  SET_CURRENT_SORT_FIELDS: (state, payload) => {
    state.currentViewDetail.sortFields = payload
  },
  SET_LOADING: (state, payload) => {
    state.isLoading = payload
  },
  SET_VIEW_LOADING: (state, payload) => {
    state.detailLoading = payload
  },
  INIT_VIEW() {
    state.views = []
    state.currentViewDetail = {}
    state.metaInfo = {}
    state.groupViews = []
    state.detailLoading = false
  },
}

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations,
}
