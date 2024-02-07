import http from 'util/http'

const actions = {
  getReportData({ commit, state }, obj) {
    return new Promise((resolve, reject) => {
      http
        .post('/report/all', obj)
        .then(function(response) {
          resolve(response)
        })
        .catch(function(error) {
          reject(error)
        })
    })
  },
  getDefaultData({ commit, state }) {
    return new Promise((resolve, reject) => {
      http
        .get('/report/default', 13)
        .then(function(response) {
          resolve(response)
        })
        .catch(function(error) {
          reject(error)
        })
    })
  },
  getDashBoardDataFirstRow({ commit, state }) {
    return new Promise((resolve, reject) => {
      http
        .get('/report/dashboard1', 13)
        .then(function(response) {
          resolve(response)
        })
        .catch(function(error) {
          reject(error)
        })
    })
  },
}
export default {
  namespaced: true,
  actions,
}
