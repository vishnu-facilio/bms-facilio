export const actionStatusFlag = {
  NOT_CONFIGURED: -1,
  YET_TO_START: 0,
  IN_PROCESS: 1,
  FAILED: 2,
  SUCCESS: 3,
}

export const transitionBtnActionsOrder = ['FORM', 'CONFIRMATION']

export const transitionBtnActions = {
  FORM: 'formId',
  CONFIRMATION: 'confirmationDialogs',
}
