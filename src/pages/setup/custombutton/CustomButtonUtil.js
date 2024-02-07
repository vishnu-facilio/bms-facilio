import getProperty from 'dlv'
import { isEmpty } from '@facilio/utils/validation'

export function getActionType(button) {
  let formModuleName = getProperty(button, 'formModuleName')
  let buttonType = getProperty(button, 'buttonType')
  if (buttonType === 1) {
    if (!isEmpty(formModuleName) && !isEmpty(button.formId)) {
      return ACTION_TYPES.CREATE_RECORD
    } else if (isEmpty(formModuleName) && !isEmpty(button.formId)) {
      return ACTION_TYPES.UPDATE_RECORD
    } else if (isEmpty(button.formId)) {
      return ACTION_TYPES.OTHER_ACTIONS
    }
  } else if (buttonType === 2 && !isEmpty(button.config)) {
    if (button.config.actionType === ACTION_TYPES.REDIRECT_URL) {
      return ACTION_TYPES.REDIRECT_URL
    } else if (button.config.navigateTo === 'Form') {
      return ACTION_TYPES.OPEN_FORM
    } else if (button.config.navigateTo === 'Summary') {
      return ACTION_TYPES.OPEN_SUMMARY
    } else if (button.config.actionType === ACTION_TYPES.CONNECTED_APPS) {
      return ACTION_TYPES.CONNECTED_APPS
    }
  }
}
export const ACTION_TYPES = {
  CREATE_RECORD: 'Create Record',
  UPDATE_RECORD: 'Update Record',
  REDIRECT_URL: 'Redirect URL',
  CONNECTED_APPS: 'Connected App',
  OPEN_FORM: 'Open Form',
  OPEN_SUMMARY: 'Open Summary',
  OTHER_ACTIONS: 'Other Actions',
}

export const POSITION_TYPE = {
  SUMMARY: 1,
  LIST_ITEM: 2,
  LIST_BAR: 3,
  LIST_TOP: 4,
}

export const POSITION_LABELS = {
  [POSITION_TYPE.SUMMARY]: 'Summary Page',
  [POSITION_TYPE.LIST_ITEM]: 'List Page - Each Record',
  [POSITION_TYPE.LIST_BAR]: 'List Page - Bulk Actions',
  [POSITION_TYPE.LIST_TOP]: 'List Page - Top Bar',
}
