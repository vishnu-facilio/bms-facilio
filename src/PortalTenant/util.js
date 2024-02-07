import { isEmpty } from '@facilio/utils/validation'
import store from 'src/store/index'
import getProperty from 'dlv'

export function isRecordLocked(record, moduleName) {
  let hasState = getProperty(record, 'moduleState.id') ? true : false

  if (!hasState) {
    return false
  } else {
    let moduleState = getProperty(record, 'moduleState.id')
    let isLocked = store.getters.isStatusLocked(moduleState, moduleName)

    return moduleState && isLocked
  }
}
export function isRequestedState(record) {
  let { approvalStatus } = record || {}
  if (isEmpty(approvalStatus)) {
    return false
  } else {
    let statusObj = store.getters.getApprovalStatus(approvalStatus.id)
    return getProperty(statusObj, 'requestedState', false)
  }
}
