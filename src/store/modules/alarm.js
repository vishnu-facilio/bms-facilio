import http from 'util/http'

const state = {
  currentView: null,
  currentPage: 1,
  perPage: 50,
  sorting: {
    orderBy: {
      label: 'Date modified',
      value: 'modifiedTime',
    },
    orderType: 'desc',
  },
  quickSearchQuery: null,
  filters: null,
  alarms: [],
  unseen: 0,
  currentAlarm: null,
  canLoadMore: false,
  criteriaIds: null,
  includeParentFilter: null,
  isNew: false,
}

// getters
const getters = {
  getAlarmById: (state, getters) => id => {
    console.log('alarm id ------>', id)
    let alarm = state.alarms.find(alarm => alarm.id === id)
    if (alarm) {
      alarm.unseen = false
      return alarm
    } else {
      return null
    }
  },
}

// actions
const actions = {
  updateSorting: ({ commit, dispatch, state }, payload) => {
    commit('UPDATE_SORTING', payload)
  },
  updateSearchQuery: ({ commit, dispatch, state }, payload) => {
    commit('UPDATE_SEARCH_QUERY', payload)
  },
  fetchAlarm: ({ commit, dispatch, state }, payload) => {
    let al = state.alarms.find(alarm => alarm && alarm.id === payload.id)
    console.log('*********** fetchalarm ', al)
    if (al) {
      commit('SET_CURRENT_ALARM', al)
    } else {
      let url = '/alarm/fetchAlarmSummary'
      let params = {
        id: [payload.id],
      }
      return http.post(url, params).then(function(response) {
        commit('SET_CURRENT_ALARM', response.data.alarms[0])
      })
    }
  },
  reload: ({ commit, dispatch, state }, payload) => {
    let url = `/alarm/${state.currentView}?page=${state.currentPage}&perPage=${state.perPage}`
    url += `${
      state.filters
        ? `&filters=${encodeURIComponent(JSON.stringify(state.filters))}`
        : ''
    }`
    url += `${
      state.quickSearchQuery ? `&search=${state.quickSearchQuery}` : ''
    }`
    url += `${state.criteriaIds ? `&criteriaIds=${state.criteriaIds}` : ''}`
    url += `${
      state.includeParentFilter
        ? `&includeParentFilter=${state.includeParentFilter}`
        : ''
    }`
    return http.get(url).then(response => {
      commit('SET_ALARMS', response.data.alarms)
    })
  },
  fetchAlarms: ({ commit, dispatch, state }, payload) => {
    commit('SET_CURRENT_VIEW', payload)

    let url = '/alarm/' + payload.viewname
    let params = 'page=' + payload.page + '&perPage=' + state.perPage
    if (payload.filters) {
      params =
        params +
        '&filters=' +
        encodeURIComponent(JSON.stringify(payload.filters))
      commit('SET_FILTERS', payload.filters)
    }
    if (payload.criteriaIds) {
      params = params + '&criteriaIds=' + payload.criteriaIds
      commit('SET_CRITERIA_IDS', payload.filters)
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
      commit('UPDATE_SEARCH_QUERY', payload.searchfilters)
    }
    if (payload.includeParentFilter) {
      params = params + '&includeParentFilter=' + payload.includeParentFilter
      commit('UPDATE_INCLUDE_PARENT_FILTER', payload.includeParentFilter)
    }
    if (payload.count) {
      params = params + '&isCount=' + payload.count
    }
    url = url + '?' + params
    return http.get(url).then(function(response) {
      commit('SET_ALARMS', response.data.alarms)
      commit('RESET_UNSEEN')
    })
  },
  assignAlarm({ commit, state }, payload) {
    return new Promise((resolve, reject) => {
      http
        .post('/alarm/assign', payload.data)
        .then(function(response) {
          let alarmIndex = state.alarms.indexOf(payload.alarm)
          let assignedToVal =
            payload.data.alarm.assignedTo.id === -1
              ? null
              : payload.data.alarm.assignedTo
          commit('UPDATE_ASSIGN_ALARM', {
            alarmIndex: alarmIndex,
            assignedTo: assignedToVal,
          })
        })
        .catch(function(error) {
          reject(error)
        })
    })
  },
  updateAlarmStatus({ commit, state, getters }, updateObj) {
    console.log('updateAlarmStatus called')
    let data = {
      id: [updateObj.alarm.id],
      alarm: {
        severityString: updateObj.severity.severity,
      },
    }
    console.log('ppppppppp', updateObj)
    http
      .post('/alarm/updatestatus', data)
      .then(function(response) {
        let alarmIndex = state.alarms.indexOf(updateObj.alarm)
        if (alarmIndex !== -1) {
          commit('UPDATE_ALARM_STATUS', {
            alarmIndex: alarmIndex,
            severity: updateObj.severity,
            clearedTime: updateObj.clearedTime,
          })
          updateObj.alarm.severity = updateObj.severity
          updateObj.alarm.clearedTime = updateObj.clearedTime
        } else {
          updateObj.alarm.severity = updateObj.severity
          updateObj.alarm.clearedTime = updateObj.clearedTime
        }
      })
      .catch(function(error) {
        if (error) {
          console.log(error)
        }
      })
  },
  acknowledgeAlarm({ commit, state, getters }, updateObj) {
    console.log('updateAlarmStatus called')

    let data = {
      id: [updateObj.alarm.id],
      alarm: {
        isAcknowledged: updateObj.isAcknowledged,
      },
    }
    http
      .post('/alarm/updatestatus', data)
      .then(function(response) {
        // let alarmIndex = state.alarms.indexOf(updateObj.alarm)
        // index = a.findIndex(x => x.prop2=="yutu");
        if (response) {
          // let datas = response
          let alarmIndex = state.alarms.findIndex(
            x => x.id === updateObj.alarm.id
          )
          if (alarmIndex !== -1) {
            commit('UPDATE_ALARM_ACKNOWLEDGE_STATUS', {
              alarmIndex: alarmIndex,
              isAcknowledged: updateObj.isAcknowledged,
              acknowledgedBy: updateObj.acknowledgedBy,
            })
            // return datas
          } else {
            updateObj.alarm.acknowledgedBy = updateObj.acknowledgedBy
            updateObj.alarm.isAcknowledged = payload.isAcknowledged
            // return datas
          }
        }
      })
      .catch(function(error) {
        if (error) {
          console.log(error)
        }
      })
  },
  createWoFromAlarm({ commit, state }, payload) {
    return new Promise((resolve, reject) => {
      payload.fields.isWoCreated = true
      payload.fields.id = payload.id[0]
      let updateObj = {
        id: payload.id,
        alarm: payload.fields,
      }
      http
        .post('/alarm/createWorkorder', updateObj)
        .then(function(response) {
          let alarmObj = state.alarms.find(alarm => alarm.id === payload.id[0])
          let alarmIndex = state.alarms.indexOf(alarmObj)
          let workorderId = response.data.workorder.id
          commit('UPDATE_ALARM_CREATE_WO', {
            alarmIndex: alarmIndex,
            isWoCreated: true,
            woId: workorderId,
          })
          resolve(response)
        })
        .catch(function(error) {
          reject(error)
        })
    })
  },
  notifyAlarm({ commit, state }, data) {
    return new Promise((resolve, reject) => {
      http
        .post('/alarm/notify', data)
        .then(function(response) {
          resolve(response)
        })
        .catch(function(error) {
          reject(error)
        })
    })
  },
  getRelatedWorkorderId({ commit, state }, ticketId) {
    return new Promise((resolve, reject) => {
      http
        .get('/alarm/viewWorkorder?id=' + ticketId)
        .then(function(response) {
          resolve(response)
        })
        .catch(function(error) {
          reject(error)
        })
    })
  },
  deleteAlarm({ commit, state }, idList) {
    return new Promise((resolve, reject) => {
      http
        .post('/alarm/delete', { id: idList })
        .then(function(response) {
          commit('DELETE_ALARM', idList)
          resolve(response)
        })
        .catch(function(error) {
          reject(error)
        })
    })
  },
  newAlarm({ commit, state }, event) {
    if (event.content.sound) {
      commit('ADD_UNSEEN')
    }
    if (state.currentView) {
      http
        .get(
          '/alarm/viewAlarm?id=' +
            event.content.record.id +
            '&viewName=' +
            state.currentView
        )
        .then(function(response) {
          if (response.data.alarms && response.data.alarms.length) {
            let alarm = response.data.alarms[0]
            if (event.content.sound) {
              alarm.unseen = true
            }
            commit('ADD_ALARM', alarm)
          }
          console.log(response.data)
        })
        .catch(function(error) {
          console.log(error)
        })
    }
  },
}

// mutations
const mutations = {
  UPDATE_INCLUDE_PARENT_FILTER: (state, payload) => {
    state.includeParentFilter = payload
  },
  SET_CRITERIA_IDS: (state, payload) => {
    state.criteriaIds = payload
  },
  SET_FILTERS: (state, payload) => {
    state.filters = payload
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
  SET_CURRENT_ALARM: (state, payload) => {
    state.currentAlarm = payload
  },
  SET_ALARMS: (state, payload) => {
    if (state.isNew) {
      state.alarms = state.currentPage > 1 ? payload : payload
      state.canLoadMore = payload.length === state.perPage
    } else {
      state.alarms =
        state.currentPage > 1 ? [...state.alarms, ...payload] : payload
      state.canLoadMore = payload.length === state.perPage
    }
  },
  RESET_UNSEEN: state => {
    state.unseen = 0
  },
  ADD_UNSEEN: state => {
    state.unseen += 1
  },
  ADD_ALARM: (state, payload) => {
    let existingAlarm = state.alarms.find(alarm => alarm.id === payload.id)
    if (existingAlarm) {
      // if the alarm entry already exists in the current view listing
      let existingAlarmIndex = state.alarms.indexOf(existingAlarm)
      if (payload.unseen) {
        state.alarms.splice(existingAlarmIndex, 1)
        state.alarms.splice(0, 0, payload)
      } else {
        state.alarms.splice(existingAlarmIndex, 1, payload)
      }
    } else {
      // if the alarm entry
      state.alarms.splice(0, 0, payload)
    }
  },
  UPDATE_ALARM_STATUS(state, payload) {
    state.alarms.splice(payload.alarmIndex, 1)
  },
  UPDATE_ALARM_ACKNOWLEDGE_STATUS(state, payload) {
    let alrm = state.alarms[payload.alarmIndex]
    alrm.isAcknowledged = payload.isAcknowledged
    alrm.acknowledgedBy = payload.acknowledgedBy
    state.alarms.splice(payload.alarmIndex, 1, alrm)
  },
  UPDATE_ALARM_CREATE_WO(state, payload) {
    let alrm = state.alarms[payload.alarmIndex]
    alrm.woId = payload.woId
    alrm.isWoCreated = payload.isWoCreated
    alrm.lastWoId = payload.woId
    state.alarms.splice(payload.alarmIndex, 1, alrm)
  },
  UPDATE_ASSIGN_ALARM(state, payload) {
    let alrm = state.alarms[payload.alarmIndex]
    alrm.assignedTo = payload.assignedTo
    state.alarms.splice(payload.alarmIndex, 1, alrm)
  },
  DELETE_ALARM(state, payload) {
    for (let alarmId of payload) {
      let alarm = state.alarms.find(alarm => alarm.id === alarmId)
      let alarmIndex = state.alarms.indexOf(alarm)
      state.alarms.splice(alarmIndex, 1)
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
