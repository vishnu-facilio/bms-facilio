import http from 'util/http'

const state = {
  currentView: null,
  currentPage: 1,
  due: [],
  perPage: 50,
  sorting: {
    orderBy: {
      label: 'Date created',
      value: 'createdTime',
    },
    orderType: 'desc',
  },
  quickSearchQuery: null,
  filters: null,
  workorders: [],
  displayedCount: null,
  currentWorkOrder: null,
  unchangedWorkorder: null,
  canLoadMore: false,
  isNew: false,
  stateFlows: null,

  // temp - to be removed
  useV3: false,
}

// getters
const getters = {
  getWorkOrderById: (state, getters) => id => {
    return state.workorders.find(workorder => workorder.id === id)
  },
  getUseV3: state => {
    return state.useV3
  },
  getWorkOrderDueById: (state, getters) => id => {
    let url = '/workorder/hovercard' + '?userId=' + id
    return http.get(url).then(function(response) {
      // this.state.due = response.data
    })
  },
}

// actions
const actions = {
  updateUseV3: ({ commit }, payload) => {
    commit('UPDATE_USE_V3', payload)
  },

  updateSorting: ({ commit, dispatch, state }, payload) => {
    commit('UPDATE_SORTING', payload)
  },

  updateSearchQuery: ({ commit, dispatch, state }, payload) => {
    commit('UPDATE_SEARCH_QUERY', payload)
  },
  fetchVendorWorkOrder: ({ commit, dispatch, state }, payload) => {
    if (!payload.id || payload.id <= 0) {
      // Temp...needs to find from where -1 is coming in spi
      return
    }
    let wo = state.workorders.find(workorder => workorder.id === payload.id)
    if (wo && !payload.force) {
      commit('SET_CURRENT_WORKORDER', wo)
    } else {
      let url = '/workorder/summary/' + payload.id
      return http.get(url).then(function(response) {
        if (response.data.result.workorder) {
          response.data.result.workorder.loadTimer = true
        }
        response.data.result.workorder
        commit('SET_UNCHANGED_WORKORDER', response.data.result.workorder)
        commit('SET_CURRENT_WORKORDER', response.data.result.workorder)
      })
    }
  },
  fetchWorkOrder: ({ commit, dispatch, state }, payload) => {
    if (!payload.id || payload.id <= 0) {
      // Temp...needs to find from where -1 is coming in spi
      return
    }
    let wo = state.workorders.find(workorder => workorder.id === payload.id)
    if (wo && !payload.force) {
      commit('SET_CURRENT_WORKORDER', wo)
    } else {
      let url = '/workorder/summary/' + payload.id
      return http.get(url).then(function(response) {
        if (response.data.workorder) {
          response.data.workorder.loadTimer = true
        }
        let { data } = response || {}
        let { workorder } = data || {}
        let { data: woData } = workorder || {}
        workorder = { ...workorder, ...(woData || {}) }

        commit('SET_UNCHANGED_WORKORDER', workorder)
        commit('SET_CURRENT_WORKORDER', workorder)
      })
    }
  },
  fetchtenantWorkOrder: ({ commit, dispatch, state }, payload) => {
    if (!payload.id || payload.id <= 0) {
      // Temp...needs to find from where -1 is coming in spi
      return
    }
    let wo = state.workorders.find(workorder => workorder.id === payload.id)
    if (wo && !payload.force) {
      commit('SET_CURRENT_WORKORDER', wo)
    } else {
      let url = '/workorder/summary/' + payload.id
      return http.get(url).then(function(response) {
        if (response.data.result.workorder) {
          response.data.result.workorder.loadTimer = true
        }
        response.data.workorder
        commit('SET_UNCHANGED_WORKORDER', response.data.result.workorder)
        commit('SET_CURRENT_WORKORDER', response.data.result.workorder)
      })
    }
  },
  fetchWorkRequest: ({ commit, dispatch, state }, payload) => {
    let wo = state.workorders.find(workorder => workorder.id === payload.id)
    if (wo) {
      commit('SET_CURRENT_WORKORDER', wo)
    } else {
      let url = '/workorderrequest/summary/' + payload.id
      return http.get(url).then(function(response) {
        commit('SET_CURRENT_WORKORDER', response.data.workorderrequest)
        commit('SET_UNCHANGED_WORKORDER', response.data.workorderrequest)
      })
    }
  },
  fetchWorkOrders: ({ commit, dispatch, state }, payload) => {
    commit('SET_CURRENT_VIEW', payload)

    let url = '/workorder/' + payload.viewname
    let params = 'page=' + payload.page + '&perPage=' + state.perPage
    if (payload.filters) {
      params =
        params +
        '&filters=' +
        encodeURIComponent(JSON.stringify(payload.filters))
    }
    if (payload.orderBy) {
      params =
        params +
        '&orderBy=' +
        payload.orderBy +
        '&orderType=' +
        payload.orderType
    }
    if (payload.search) {
      params = params + '&search=' + payload.search
    }
    if (payload.criteriaIds) {
      params = params + '&criteriaIds=' + payload.criteriaIds
    }
    if (payload.id) {
      params =
        params + '&woIds=' + encodeURIComponent(JSON.stringify(payload.id))
    }
    if (payload.includeParentFilter) {
      params = params + '&includeParentFilter=' + payload.includeParentFilter
    }
    if (payload.fetchAllType) {
      params = params + '&fetchAllType=' + payload.fetchAllType
    }
    if (payload.count) {
      params = '&count=' + payload.count
    }
    url = url + '?' + params
    return http.get(url).then(function(response) {
      state.displayedCount = response.data.workOrders.length
      // if (payload.count) {

      // }
      // else {
      commit('SET_WORKORDERS', response.data.workOrders)
      commit('SET_DISPLAYCOUNT', response.data.workOrders.length)
      commit('UPDATE_FILTERS', payload.filters)
      // }
    })
  },
  fetchTenantWorkOrders: ({ commit, dispatch, state }, payload) => {
    commit('SET_CURRENT_VIEW', payload)

    let url = 'v2/workorders'
    let params = 'page=' + payload.page + '&perPage=' + state.perPage
    if (payload.filters) {
      params =
        params +
        '&filters=' +
        encodeURIComponent(JSON.stringify(payload.filters))
    }
    if (payload.orderBy) {
      params =
        params +
        '&orderBy=' +
        payload.orderBy +
        '&orderType=' +
        payload.orderType
    }
    if (payload.search) {
      params = params + '&search=' + payload.search
    }
    if (payload.criteriaIds) {
      params = params + '&criteriaIds=' + payload.criteriaIds
    }
    if (payload.id) {
      params =
        params + '&woIds=' + encodeURIComponent(JSON.stringify(payload.id))
    }
    if (payload.includeParentFilter) {
      params = params + '&includeParentFilter=' + payload.includeParentFilter
    }
    if (payload.count) {
      params = '&count=' + payload.count
    }
    url = url + '?' + params
    return http.get(url).then(function(response) {
      state.displayedCount = response.data.result.workorders.length
      // if (payload.count) {

      // }
      // else {
      commit('SET_WORKORDERS', response.data.result.workorders)
      commit('SET_DISPLAYCOUNT', response.data.result.workorders.length)
      commit('UPDATE_FILTERS', payload.filters)
      // }
    })
  },
  fetchVendorWorkOrders: ({ commit, dispatch, state }, payload) => {
    commit('SET_CURRENT_VIEW', payload)

    let url = '/vendorworkorders/list'
    let params = 'page=' + payload.page + '&perPage=' + state.perPage
    if (payload.filters) {
      params =
        params +
        '&filters=' +
        encodeURIComponent(JSON.stringify(payload.filters))
    }
    if (payload.orderBy) {
      params =
        params +
        '&orderBy=' +
        payload.orderBy +
        '&orderType=' +
        payload.orderType
    }
    if (payload.search) {
      params = params + '&search=' + payload.search
    }
    if (payload.criteriaIds) {
      params = params + '&criteriaIds=' + payload.criteriaIds
    }
    if (payload.id) {
      params =
        params + '&woIds=' + encodeURIComponent(JSON.stringify(payload.id))
    }
    if (payload.includeParentFilter) {
      params = params + '&includeParentFilter=' + payload.includeParentFilter
    }
    if (payload.count) {
      params = '&count=' + payload.count
    }
    url = url + '?' + params
    return http.get(url).then(function(response) {
      state.displayedCount = response.data.result.workorders.length
      // if (payload.count) {

      // }
      // else {
      commit('SET_WORKORDERS', response.data.result.workorders)
      commit('SET_DISPLAYCOUNT', response.data.result.workorders.length)
      commit('UPDATE_FILTERS', payload.filters)
      // }
    })
  },
  fetchApproval: ({ commit, dispatch, state }, payload) => {
    commit('SET_CURRENT_VIEW', payload)

    let url = '/v2/approvals/view/' + payload.viewname
    let params = 'page=' + payload.page + '&perPage=' + state.perPage
    if (payload.filters) {
      params =
        params +
        '&filters=' +
        encodeURIComponent(JSON.stringify(payload.filters))
    }
    if (payload.orderBy) {
      params =
        params +
        '&orderBy=' +
        payload.orderBy +
        '&orderType=' +
        payload.orderType
    }
    if (payload.search) {
      params = params + '&search=' + payload.search
    }
    if (payload.criteriaIds) {
      params = params + '&criteriaIds=' + payload.criteriaIds
    }
    if (payload.includeParentFilter) {
      params = params + '&includeParentFilter=' + payload.includeParentFilter
    }
    if (payload.count) {
      params = params + '&count=' + payload.count
    }
    url = url + '?' + params
    return http.get(url).then(function(response) {
      commit('SET_WORKORDERS', response.data.result.workorders)
      commit('SET_STATE_FLOWS', response.data.result.stateFlows)
      commit('UPDATE_FILTERS', payload.filters)
    })
  },
  reload: ({ commit, dispatch, state }, payload) => {
    if (state.currentView) {
      let url = `/workorder/${state.currentView}?page=${state.currentPage}&perPage=${state.perPage}`
      if (state.filters) {
        url += `&filters=${encodeURIComponent(JSON.stringify(state.filters))}`
      }
      if (state.quickSearchQuery) {
        url += `&search=${state.quickSearchQuery}`
      }
      return http.get(url).then(function(response) {
        commit('SET_WORKORDERS', response.data.workOrders)
      })
    }
  },
  reloadApproval: ({ commit, dispatch, state }, payload) => {
    let url = `/v2/approvals/view/${state.currentView}?page=${state.currentPage}&perPage=${state.perPage}`
    if (state.filters) {
      url += `&filters=${encodeURIComponent(JSON.stringify(state.filters))}`
    }
    if (state.quickSearchQuery) {
      url += `&search=${state.quickSearchQuery}`
    }
    return http.get(url).then(function(response) {
      commit('SET_WORKORDERS', response.data.result.workorders)
      commit('SET_STATE_FLOWS', response.data.result.stateFlows)
    })
  },
  fetchWorkOrderRequests: ({ commit, dispatch, state }, payload) => {
    commit('SET_CURRENT_VIEW', payload)
    let url = '/workorderrequest/' + payload.viewname
    let params = 'page=' + payload.page + '&per_page=' + state.perPage
    if (payload.filters) {
      params =
        params +
        '&filters=' +
        encodeURIComponent(JSON.stringify(payload.filters))
    }
    if (payload.orderBy) {
      params =
        params +
        '&orderBy=' +
        payload.orderBy +
        '&orderType=' +
        payload.orderType
    }
    if (payload.search) {
      params = params + '&search=' + payload.search
    }
    if (payload.count) {
      params = params + '&count=' + payload.count
    }
    url = url + '?' + params
    return http.get(url).then(function(response) {
      commit('SET_WORKORDERS', response.data.workOrderRequests)
    })
  },
  getPicklist({ commit, state }, moduleName) {
    //console.log('get picklist called')

    return new Promise((resolve, reject) => {
      http
        .get('/picklist', {
          params: {
            moduleName: moduleName,
          },
        })
        .then(function(response) {
          resolve(response)
        })
        .catch(function(error) {
          reject(error)
        })
    })
  },
  assignWorkOrder({ commit, state }, payload) {
    //console.log('assign work order called::: ', payload)
    let assignToObj = {
      id: payload.id,
      workorder: {},
    }
    if (payload.assignedTo) {
      assignToObj.workorder.assignedTo = payload.assignedTo
    }
    if (payload.assignmentGroup) {
      assignToObj.workorder.assignmentGroup = payload.assignmentGroup
    }
    return new Promise((resolve, reject) => {
      http
        .post('/workorder/assign', assignToObj)
        .then(function(response) {
          commit('ASSIGN_WORKORDER', payload)
          resolve(response)
        })
        .catch(function(error) {
          reject(error)
        })
    })
  },
  closeWorkOrder({ commit, state }, payload) {
    //console.log('close work order called::: ', payload)
    return new Promise((resolve, reject) => {
      http
        .post('/v2/workorders/close', payload)
        .then(function(response) {
          if (response.data.responseCode === 0) {
            commit('CLOSE_WORKORDER', payload)
            resolve(response.data)
          } else {
            reject(Error(response.data.message))
          }
        })
        .catch(function(error) {
          reject(error)
        })
    })
  },
  resolve({ commit, state }, payload) {
    return new Promise((resolve, reject) => {
      http
        .post('/v2/workorders/resolve', payload)
        .then(function(response) {
          if (response.data.responseCode === 0) {
            commit('UPDATE_WORKORDER', payload)
            resolve(response.data)
          } else {
            reject(Error(response.data.message))
          }
        })
        .catch(function(error) {
          reject(error)
        })
    })
  },
  deleteWorkOrder({ commit, state }, payload) {
    //console.log('delete work order called::: ', payload)
    return new Promise((resolve, reject) => {
      http
        .post('/workorder/delete', payload)
        .then(function(response) {
          commit('DELETE_WORKORDER', payload)
          resolve(response)
        })
        .catch(function(error) {
          reject(error)
        })
    })
  },
  updateWorkOrder({ commit, state }, payload) {
    //console.log('update work order called::: ', payload)
    return new Promise((resolve, reject) => {
      let updateObj = {
        id: payload.id,
        workorder: payload.fields,
        actualTimings: payload.actualTimings,
      }
      http
        .post('/workorder/update', updateObj)
        .then(function(response) {
          if (typeof response.data === 'object') {
            commit('UPDATE_WORKORDER', payload)
            resolve(response)
          } else {
            reject(Error('Workorder updation failed'))
          }
        })
        .catch(function(error) {
          reject(error)
        })
    })
  },
  approveWorkOrderRequest({ commit, state }, payload) {
    return new Promise((resolve, reject) => {
      let updateObj = {
        id: payload.id,
        workorderrequest: payload.fields,
      }
      http
        .post('/workorderrequest/approve', updateObj)
        .then(function(response) {
          commit('APPROVE_WORKORDER_REQUEST', payload)
          resolve(response)
        })
        .catch(function(error) {
          reject(error)
        })
    })
  },
  rejectWorkOrderRequest({ commit, state }, payload) {
    return new Promise((resolve, reject) => {
      http
        .post('/workorderrequest/reject', payload)
        .then(function(response) {
          commit('REJECT_WORKORDER_REQUEST', payload)
          resolve(response)
        })
        .catch(function(error) {
          reject(error)
        })
    })
  },
  updateWorkOrderRequest({ commit, state }, payload) {
    return new Promise((resolve, reject) => {
      let updateObj = {
        id: payload.id,
        workorderrequest: payload.fields,
      }
      http
        .post('/workorderrequest/update', updateObj)
        .then(function(response) {
          commit('UPDATE_WORKORDER', payload)
          resolve(response)
        })
        .catch(function(error) {
          reject(error)
        })
    })
  },
  removeCurrentWo({ commit }) {
    commit('SET_CURRENT_WORKORDER', null)
  },
  setCurrentWO({ commit }, wo) {
    commit('SET_CURRENT_WORKORDER', wo)
  },
}

// mutations
const mutations = {
  UPDATE_USE_V3: (state, payload) => {
    state.useV3 = payload.useV3
  },

  SET_CURRENT_VIEW: (state, payload) => {
    state.currentView = payload.viewname
    state.currentPage = payload.page
    state.isNew = payload.isNew
  },
  UPDATE_SORTING: (state, payload) => {
    state.sorting = payload
  },
  UPDATE_SEARCH_QUERY: (state, payload) => {
    state.quickSearchQuery = payload
  },
  UPDATE_FILTERS: (state, payload) => {
    state.filters = payload
  },
  SET_WORKORDERS: (state, payload) => {
    if (state.isNew) {
      state.workorders = state.currentPage > 1 ? payload : payload
      state.canLoadMore = payload.length === state.perPage
    } else {
      state.workorders =
        state.currentPage > 1 ? [...state.workorders, ...payload] : payload
      state.canLoadMore = payload.length === state.perPage
    }
  },
  SET_DISPLAYCOUNT: (state, payload) => {
    state.displayedCount = payload
  },
  SET_STATE_FLOWS: (state, payload) => {
    state.stateFlows = payload
  },
  SET_CURRENT_WORKORDER: (state, payload) => {
    state.currentWorkOrder = payload
    if (state.currentWorkOrder) {
      state.currentWorkOrder.loadTimer = true
    }
  },
  SET_UNCHANGED_WORKORDER: (state, payload) => {
    state.unchangedWorkorder = payload
  },
  ASSIGN_WORKORDER_DUE: (state, payload) => {
    state.dueToday = payload
  },
  ASSIGN_WORKORDER: (state, payload) => {
    for (let idx in payload.id) {
      let woId = payload.id[idx]
      let wo = state.workorders.find(workorder => workorder.id === woId)
      if (wo === null || wo === undefined) {
        wo = state.currentWorkOrder
      }
      if (!wo) {
        return
      }
      if (payload.assignedTo) {
        if (payload.assignedTo.id === -1) {
          wo.assignedTo = null
        } else {
          wo.assignedTo = payload.assignedTo
        }
      }
      if (payload.assignmentGroup) {
        if (payload.assignmentGroup.id === -1) {
          wo.assignmentGroup = null
        } else {
          wo.assignmentGroup = payload.assignmentGroup
        }
      }
      if (wo.status.status === 'Submitted' && payload.status) {
        wo.status = payload.status
      }
    }
  },
  CLOSE_WORKORDER: (state, payload) => {
    for (let idx in payload.id) {
      let woId = payload.id[idx]
      let wo = state.workorders.find(workorder => workorder.id === woId)
      if (!wo) {
        return
      }
      let woIdx = state.workorders.indexOf(wo)
      state.workorders.splice(woIdx, 1)
    }
  },
  DELETE_WORKORDER: (state, payload) => {
    for (let idx in payload.id) {
      let woId = payload.id[idx]
      let wo = state.workorders.find(workorder => workorder.id === woId)
      let woIdx = state.workorders.indexOf(wo)
      state.workorders.splice(woIdx, 1)
    }
  },
  UPDATE_WORKORDER: (state, payload) => {
    for (let idx in payload.id) {
      let woId = payload.id[idx]
      let wo = state.workorders.find(workorder => workorder.id === woId)
      if (!wo) {
        return
      }
      for (let key in payload.fields) {
        wo[key] = payload.fields[key]
      }
    }
  },
  APPROVE_WORKORDER_REQUEST: (state, payload) => {
    for (let idx in payload.id) {
      let woId = payload.id[idx]
      let wo = state.workorders.find(workorder => workorder.id === woId)
      let woIdx = state.workorders.indexOf(wo)
      state.workorders.splice(woIdx, 1)
    }
  },
  REJECT_WORKORDER_REQUEST: (state, payload) => {
    for (let idx in payload.id) {
      let woId = payload.id[idx]
      let wo = state.workorders.find(workorder => workorder.id === woId)
      let woIdx = state.workorders.indexOf(wo)
      state.workorders.splice(woIdx, 1)
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
