import { isNullOrUndefined, isEmpty, isNumber } from '@facilio/utils/validation'
import findIndex from 'lodash/findIndex'
import clone from 'lodash/clone'
import { API } from '@facilio/api'

export const genericCachedGet = (
  { commit, state },
  param,
  { url, extraction, forceUpdate, moduleName, canFreeze, commitName }
) => {
  let dataObj, promise

  if (!isEmpty(moduleName)) {
    dataObj = state[param][moduleName]
  } else {
    dataObj = state[param]
  }

  if (isNullOrUndefined(dataObj) || forceUpdate) {
    promise = new Promise((resolve, reject) => {
      API.get(url, null, { force: forceUpdate }).then(({ error, data }) => {
        promise = null
        if (error) {
          promise = null
          reject()
        } else {
          let responseData = extraction(data)
          if (!isNullOrUndefined(responseData)) {
            commitName = commitName ? commitName : 'GENERIC_SET'
            commit(commitName, {
              type: param,
              data: responseData,
              moduleName,
              canFreeze,
            })
            resolve()
          } else {
            reject()
          }
        }
      })
    })
    return promise
  } else return new Promise(resolve => resolve())
}

export const genericMutations = {
  GENERIC_SET(state, payload) {
    if (isEmpty(payload.moduleName)) {
      state[payload.type] = payload.data
    } else {
      state[payload.type] = {
        ...state[payload.type],
        [payload.moduleName]: payload.data,
      }
    }
  },
  GENERIC_ADD_OR_UPDATE(state, { type, data, matches }) {
    let list = clone(state[type])

    if (isEmpty(matches)) matches = ['id', data.id]

    let index = findIndex(list, matches)

    if (!isEmpty(index)) list.splice(index, 1, data)
    else list.push(data)

    state[type] = list
  },
  GENERIC_DELETE(state, { type, matches }) {
    let list = clone(state[type])
    let index

    if (isNumber(matches)) index = matches
    else index = findIndex(list, matches)

    list.splice(index, 1)
    state[type] = list
  },
}
