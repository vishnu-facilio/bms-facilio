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
  occurrence: [],
  events: [],
  unseen: 0,
  currentAlarm: null,
  currentOccurrence: null,
  canLoadMore: false,
  criteriaIds: null,
  includeParentFilter: null,
  isNew: false,
  subList: false,
  moduleName: null,
}

// getters
const getters = {
  getAlarmById: state => id => {
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
  updateSorting: ({ commit }, payload) => {
    commit('UPDATE_SORTING', payload)
  },
  updateSearchQuery: ({ commit }, payload) => {
    commit('UPDATE_SEARCH_QUERY', payload)
  },
  fetchAlarm: ({ commit, state }, payload) => {
    // commit('SET_CURRENT_ALARM', null)
    // commit('SET_CURRENT_OCCURRENCE', null)
    let url = '/v2/alarm/get'
    let params = {
      id: payload.id,
    }
    return http.post(url, params).then(function(response) {
      let updateCurrentAlarm = true
      if (payload.checkIfCurrentAlarm) {
        if (!state.currentAlarm || state.currentAlarm.id !== payload.id) {
          updateCurrentAlarm = false
        }
      }
      let alarm = response.data.result
        ? response.data.result.record
          ? response.data.result.record
          : null
        : null
      if (updateCurrentAlarm) {
        commit('SET_CURRENT_ALARM', alarm)
        commit(
          'SET_CURRENT_OCCURRENCE',
          response.data.result && response.data.result.latestAlarmOccurrence
            ? response.data.result.latestAlarmOccurrence
            : null
        )
      } else if (alarm) {
        commit('ADD_ALARM', alarm)
      }
    })
  },
  reload: ({ commit, state }, payload) => {
    let moduleName = ''
    if (payload && payload.moduleName) {
      moduleName = payload.moduleName
    } else {
      moduleName = 'readingalarms'
    }
    url +=
      '/view/' +
      (payload && payload.viewname ? payload.viewname : state.currentView)
    let url = `/v2/${moduleName}/view/${state.currentView}?page=${state.currentPage}&perPage=${state.perPage}`
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
      commit('SET_ALARMS', response.data.result.alarms)
    })
  },
  getOccurrenceFromId: ({ commit }, payload) => {
    if (!payload.id) {
      return
    }
    let url = 'v2/alarmOccurrence/list'
    let params = {
      id: payload.id.id,
    }
    if (payload.filters) {
      params.filters = payload.filters
    }
    return http.post(url, params).then(function(response) {
      commit(
        'SET_OCCURRENCE_LIST',
        response.data.result && response.data.result.records
          ? response.data.result.records
          : []
      )
    })
  },
  getOccurrence: ({ commit }, payload) => {
    let url = 'v2/alarmOccurrence/occurrenceList'
    let params = ''

    if (payload.filters) {
      params =
        params +
        'filters=' +
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
    if (params) {
      url += '?' + params
    }

    return http.get(url).then(function(response) {
      commit(
        'SET_OCCURRENCE_LIST',
        response.data.result && response.data.result.records
          ? response.data.result.records
          : []
      )
    })
  },
  getEvents: ({ commit }, payload) => {
    let url = 'v2/baseEvent/eventList'
    let params = ''
    if (payload.perPage) {
      params = params + 'perPage=' + payload.perPage + '&'
    }
    if (payload.page) {
      params = params + 'page=' + payload.page + '&'
    }
    if (payload.orderBy) {
      params =
        params +
        'orderBy=' +
        payload.orderBy +
        '&orderType=' +
        payload.orderType +
        '&overrideViewOrderBy=true&'
    }
    if (payload.filters) {
      params =
        params +
        'filters=' +
        encodeURIComponent(JSON.stringify(payload.filters.filters))
    }
    if (params) {
      url += '?' + params
    }
    return http.get(url).then(function(response) {
      commit(
        'EVENTS_LIST',
        response.data.result && response.data.result.records
          ? response.data.result.records
          : []
      )
    })
  },
  getEventsFromId: ({ commit }, payload) => {
    if (!payload.id) {
      return
    }
    let url = 'v2/alarmOccurrence/eventList'
    let params = {
      id: payload.id.id,
    }
    return http.post(url, params).then(function(response) {
      commit(
        'SET_EVENTS_LIST',
        response.data.result && response.data.result.records
          ? response.data.result.records
          : []
      )
    })
  },
  fetchAlarms: ({ commit, state }, payload) => {
    commit('SET_CURRENT_VIEW', payload)
    let url = '/v2/'
    url += 'newAlarms/view/' + payload.viewname
    let params = 'page=' + payload.page + '&perPage=' + state.perPage
    params = params + '&alarmModule=' + payload.moduleName
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
      commit('SET_MODULE', payload)
      commit('SET_ALARMS', response.data.result.alarms)
      commit('RESET_UNSEEN')
    })
  },
  updateAlarmStatus({ state }, updateObj) {
    return new Promise(() => {
      let data = {
        ids: [updateObj.occurrence.id],
        alarmOccurrence: {
          alarm: { id: updateObj.id },
          severity: {
            id: updateObj.severity.id,
          },
          clearedTime: updateObj.clearedTime,
        },
      }
      http.post('/v2/alarm/updateOccurrence', data).then(function() {
        let alarmIndex = state.alarms.indexOf(updateObj.alarm)
        if (alarmIndex !== -1) {
          // commit('UPDATE_ALARM_STATUS', {
          //   alarmIndex: alarmIndex,
          //   severity: updateObj.severity,
          //   clearedTime: updateObj.clearedTime,
          // })
          updateObj.alarm.severity = updateObj.severity
          updateObj.alarm.clearedTime = updateObj.clearedTime
        } else {
          updateObj.alarm.severity = updateObj.severity
          updateObj.alarm.clearedTime = updateObj.clearedTime
        }
      })
    })
  },
  acknowledgeAlarm({ commit, state }, updateObj) {
    let data = {
      ids: [updateObj.occurrence.id],
      alarmOccurrence: {
        alarm: { id: updateObj.alarm.id },
        acknowledged: true,
      },
    }
    http.post('/v2/alarm/updateOccurrence', data).then(function(response) {
      // let alarmIndex = state.alarms.indexOf(updateObj.alarm)
      // index = a.findIndex(x => x.prop2=="yutu");
      if (response) {
        // let datas = response
        let alarmIndex = state.alarms.indexOf(updateObj.alarm)
        if (alarmIndex !== -1) {
          commit('UPDATE_ALARM_ACKNOWLEDGE_STATUS', {
            alarmIndex: alarmIndex,
            acknowledged: updateObj.acknowledged,
            acknowledgedBy: updateObj.acknowledgedBy,
          })
          // return datas
        } else {
          updateObj.alarm.acknowledgedBy = updateObj.acknowledgedBy
          updateObj.alarm.acknowledged = updateObj.acknowledged
          // return datas
        }
      }
    })
  },
  createWoFromAlarm({ commit, state }, payload) {
    return new Promise((resolve, reject) => {
      payload.fields.isWoCreated = true
      // payload.fields.id = payload.id[0]
      let updateObj = {
        id: payload.id,
        workorder: payload.fields,
      }
      http
        .post('/v2/alarmOccurrence/createWO', updateObj)
        .then(function(response) {
          if (response.data.result) {
            let alarmObj = state.alarms.find(
              alarm => alarm.id === payload.id[0]
            )
            let alarmIndex = state.alarms.indexOf(alarmObj)
            let workorderId = response.data.workorder.id
            commit('UPDATE_ALARM_CREATE_WO', {
              alarmIndex: alarmIndex,
              isWoCreated: true,
              woId: workorderId,
            })
          }
          resolve(response)
        })
        .catch(function(error) {
          reject(error)
        })
    })
  },
  notifyAlarm({ commit }, data) {
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
  getRelatedWorkorderId({ commit }, ticketId) {
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
  deleteAlarm({ commit }, idList) {
    return new Promise((resolve, reject) => {
      http
        .post('/v2/alarm/delete', {
          id: idList,
        })
        .then(function(response) {
          commit('DELETE_ALARM', idList)
          resolve(response)
        })
        .catch(function(error) {
          reject(error)
        })
    })
  },
  deleteAlarmNew({ commit }, idList) {
    return new Promise((resolve, reject) => {
      http
        .post('/v2/alarm/delete', {
          id: idList[0],
        })
        .then(function(response) {
          commit('DELETE_ALARM', idList)
          resolve(response)
        })
        .catch(function(error) {
          reject(error)
        })
    })
  },
  deleteOccurrence({ commit }, idList) {
    return new Promise((resolve, reject) => {
      http
        .post('/v2/alarmOccurrence/delete', {
          id: idList,
        })
        .then(function(response) {
          commit('DELETE_OCCURRENCE', idList)
          resolve(response)
        })
        .catch(function(error) {
          reject(error)
        })
    })
  },
  refetch({ commit }, event) {
    if (event.content.sound) {
      commit('ADD_UNSEEN')
    }
    /* if (state.moduleName === 'newreadingalarm') {
      dispatch('fetchAlarm', {
        id: event.content.record.id,
        checkIfCurrentAlarm: true,
      })
    } */
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
            state.currentView +
            '&moduleName=newreadingalarm'
        )
        .then(function(response) {
          if (response.data.alarms && response.data.alarms.length) {
            let alarm = response.data.alarms[0]
            if (event.content.sound) {
              alarm.unseen = true
            }
            commit('ADD_ALARM', alarm)
          }
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
    state.subList = payload.subList
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
  SET_CURRENT_OCCURRENCE: (state, payload) => {
    state.currentOccurrence = payload
  },
  SET_OCCURRENCE_LIST: (state, payload) => {
    if (payload) {
      payload.sort((a1, a2) => a2.createdTime - a1.createdTime)
    }
    state.occurrence = payload
  },
  SET_EVENTS_LIST: (state, payload) => {
    if (payload) {
      payload.sort((a1, a2) => a2.createdTime - a1.createdTime)
    }
    state.events = payload
  },
  EVENTS_LIST: (state, payload) => {
    if (payload) {
      payload.sort((a1, a2) => a2.createdTime - a1.createdTime)
    }
    state.events = payload
    // state.events = payload
  },
  SET_MODULE: (state, payload) => {
    state.moduleName = payload.moduleName
  },
  SET_ALARMS: (state, payload) => {
    if (state.isNew) {
      if (state.subList) {
        state.canLoadMore = payload.length === state.perPage
        state.alarms =
          state.currentPage > 1 ? [...state.alarms, ...payload] : payload
      } else {
        state.alarms = state.currentPage > 1 ? payload : payload
        state.canLoadMore = payload.length === state.perPage
      }
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
    alrm.acknowledged = payload.acknowledged
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
  UPDATE_ALARM_WO(state, payload) {
    let { woId } = payload
    let { currentAlarm, currentOccurrence } = state
    currentOccurrence.woId = woId
    currentAlarm.lastWoId = woId
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
  DELETE_OCCURRENCE(state, payload) {
    for (let alarmId of payload) {
      let occurrence = state.occurrence.find(rt => rt.id === alarmId)
      let alarmIndex = state.occurrence.indexOf(occurrence)
      state.occurrence.splice(alarmIndex, 1)
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
