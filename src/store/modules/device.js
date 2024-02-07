import http from 'util/http'

const actions = {
  getAllDevice({ commit, state }) {
    return new Promise((resolve, reject) => {
      http
        .get('/device/all')
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
