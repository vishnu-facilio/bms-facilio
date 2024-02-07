import { API } from '@facilio/api'
import getProperty from 'dlv'

const notificationsStatus = {
  UNSEEN: 1,
  UNREAD: 2,
  SEEN: 3,
}

const state = {
  notifications: [],
  notificationsForPopup: [],
  newNotification: null,
  count: 0,
  unseen: 0,
  unread: 0,
  connection: null,
  totalCount: null,
}

// actions
const actions = {
  fetchNotifications: ({ commit, dispatch, state }, payload) => {
    let page = getProperty(payload, 'page')
    let perPage = getProperty(payload, 'perPage')
    let params = {
      page: page || 1,
      perPage: perPage || 50,
      withCount: true,
    }

    return API.fetchAll('usernotification', params).then(
      ({ list, meta, error }) => {
        if (!error) {
          commit('SET_NOTIFICATIONS', list)
          commit('SET_TOTAL_COUNT', meta.pagination.totalCount)
        }
      }
    )
  },
  fetchNotificationsPopup: ({ commit, dispatch, state }) => {
    return API.fetchAll('usernotification', {
      page: 1,
      perPage: 4,
    }).then(({ list, error }) => {
      if (!error) {
        commit('SET_NOTIFICATIONS_FOR_POPUP', list)
      }
    })
  },
  updateStatus: ({ commit }, notificationItem) => {
    notificationItem.isRead = true
    return API.updateRecord('usernotification', {
      id: notificationItem.id,
      data: {
        notificationStatus: notificationsStatus.SEEN,
        readAt: new Date().getTime(),
      },
    }).then(({ data, error }) => {
      if (!error) {
        commit('UPDATE_STATUS', notificationItem)
      }
    })
  },
  fetchNotificationCount: ({ commit }, payload) => {
    return API.fetchAll('usernotification', {
      withCount: true,
      unseen: true,
      page: 1,
      perPage: 1,
    }).then(({ meta, error }) => {
      if (!error) {
        commit('SET_UNSEEN_COUNT', meta.pagination.totalCount)
      }
    })
  },
}

// mutations
const mutations = {
  SET_NOTIFICATIONS: (state, payload) => {
    payload.forEach(item => {
      item.isRead =
        item.notificationStatus == notificationsStatus.SEEN ? true : false
    })
    state.notifications = payload
  },
  SET_NOTIFICATIONS_FOR_POPUP: (state, payload) => {
    payload.forEach(item => {
      item.isRead =
        item.notificationStatus == notificationsStatus.SEEN ? true : false
    })
    let temp = payload.slice(0, 4)
    state.notificationsForPopup = temp
  },
  SET_UNSEEN_COUNT: (state, payload) => {
    state.unseen = payload
  },
  SET_UNREAD_COUNT: (state, payload) => {
    let seenNotifications = payload.filter(
      x => x.notificationStatus == notificationsStatus.SEEN
    )
    state.unread = seenNotifications.length
  },
  SET_TOTAL_COUNT: (state, payload) => {
    state.totalCount = payload
  },
  UPDATE_STATUS: (state, payload) => {
    //Replacing the notification item with the same item of updated status
    const index = state.notifications.findIndex(
      notif => notif.id === payload.id
    )
    if (index !== -1) {
      state.notifications.splice(index, 1, payload)
    }
  },
}

export default {
  namespaced: true,
  state,
  actions,
  mutations,
}
