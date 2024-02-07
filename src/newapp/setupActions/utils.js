import { isEmpty } from '@facilio/utils/validation'
import $common from 'src/util/common.js'
import $getProperty from 'dlv'
import { API } from '@facilio/api'

export const actionHash = {
  mail: 'Send Email',
  sms: 'Send SMS',
  mobile: 'Send Notification',
  fieldUpdate: 'Field Update',
  script: 'Execute Script',
  changeStatus: 'Change Status',
}

export const actionType = {
  3: 'EMAIL',
  4: 'SMS',
  7: 'NOTIFICATION',
  13: 'FIELD_UPDATE',
  19: 'CHANGE_STATUS',
  21: 'EXECUTE_SCRIPT',
}

export const Modules = {
  WORKORDER: 'workorder',
  WORKREQUEST: 'workorderrequest',
  ALARM: 'alarm',
}

export const Fields = {
  REQUESTER: 'requester',
  REQUESTED_BY: 'requestedBy',
  ASSIGNED_BY: 'assignedBy',
  ASSIGNED_TO: 'assignedTo',
  ASSIGNED_GROUP: 'assignmentGroup',
  VENDOR: 'vendor',
  TENANT: 'tenant',
  SYS_CREATED_BY: 'sysCreatedBy',
}

export const sysCreatedByUserOption = {
  label: 'Created By',
  field: Fields.SYS_CREATED_BY,
}

export const notifConfig = {
  'Approve Workrequest_11': {
    options: [Fields.REQUESTER],
    module: Modules.WORKREQUEST,
    isUsersEditable: true,
  },
  'Reject Workrequest_11': {
    options: [Fields.REQUESTER],
    module: Modules.WORKREQUEST,
    isUsersEditable: true,
  },
  'Notify Requester on Closing Workorder_11': {
    options: [Fields.REQUESTER],
    module: Modules.WORKORDER,
    isUsersEditable: true,
  },
  'Technician Closes Workorder_11': {
    options: [Fields.ASSIGNED_TO, Fields.ASSIGNED_BY],
    module: Modules.WORKORDER,
  },
  'Assign Tech_11': {
    options: [Fields.ASSIGNED_TO],
    module: Modules.WORKORDER,
    isUsersEditable: true,
  },
  'Assign Team_11': {
    options: [Fields.ASSIGNED_GROUP],
    module: Modules.WORKORDER,
    isUsersEditable: true,
  },
  'Add Comment_11': {
    options: [Fields.ASSIGNED_TO, Fields.ASSIGNED_BY],
    module: Modules.WORKORDER,
  },
  'Technician Resolves Workorder_11': {
    options: [Fields.ASSIGNED_TO, Fields.ASSIGNED_BY],
    module: Modules.WORKORDER,
  },
  'Workorder on hold_11': {
    options: [Fields.ASSIGNED_BY],
    module: Modules.WORKORDER,
  },
  Approval: {
    options: [Fields.REQUESTED_BY, Fields.REQUESTER],
    module: Modules.WORKORDER,
  },
}
export const handleAddrChange = function(
  receiverType,
  userOptionList,
  isPlaceHolder,
  isEmail
) {
  let selectedReceivers = this.templateJson[receiverType] || []
  userOptionList = {
    ...(userOptionList || {}),
    ...(isPlaceHolder ? { PlaceHolders: [] } : {}),
    ...(isEmail ? { EmailIds: [] } : {}),
  }
  let valuedOptions = Object.values(userOptionList)
  let receiverValuesArr = valuedOptions.reduce((prev, arr) => {
    if (isNaN(arr[0]?.value)) {
      prev.push(...(arr || []).map(obj => obj.value))
    }
    return prev
  }, [])
  selectedReceivers = (selectedReceivers || []).filter(value => {
    return isNaN(value) && !receiverValuesArr.includes(value)
  })
  selectedReceivers.forEach(receiver => {
    let receiverObj = { label: receiver, value: receiver }
    if (isEmail && $common.validateEmail(receiver)) {
      userOptionList.EmailIds.push(receiverObj)
    } else if (isPlaceHolder && receiver.startsWith('${cs.')) {
      userOptionList.PlaceHolders.push(receiverObj)
    } else {
      let valueIndex = this.templateJson[receiverType].findIndex(
        value => value === receiver
      )

      if (valueIndex !== -1)
        this.templateJson[receiverType].splice(valueIndex, 1)
    }
  })
  return userOptionList
}
export const quillEditorConfig = {
  toolbar: [
    [
      'bold',
      'italic',
      'underline',
      'link',
      {
        list: 'ordered',
      },
      {
        list: 'bullet',
      },
      {
        indent: '-1',
      },
      {
        indent: '+1',
      },
      {
        size: ['small', false, 'large', 'huge'],
      },
      {
        align: ['', 'right', 'center', 'justify'],
      },
      {
        header: [1, 2, 3, 4, 5, 6, false],
      },
    ],
  ],
}
export const fetchUsers = async function(
  search = '',
  appIdProp = null,
  receiverTypeList = ['to']
) {
  let { application: appId } = this.templateJson || {}
  let userList = [],
    defaultIds = []
  let params = {
    page: 1,
    perPage: 20,
    inviteAcceptStatus: true,
    appId: !isEmpty(appIdProp) ? appIdProp : appId,
    search: !isEmpty(search) ? search : null,
  }
  ;(receiverTypeList || []).forEach(type => {
    let endList = $getProperty(this.templateJson, type, null)
    if (!isEmpty(endList)) {
      let userIds = (endList || []).filter(id => typeof id === 'number')
      defaultIds = [...defaultIds, ...userIds]
    }
  })
  defaultIds = [...new Set(defaultIds)]
  if (!isEmpty(defaultIds) && isEmpty(search)) {
    params.defaultIds = defaultIds
    params.perPage = defaultIds.length > 20 ? defaultIds.length : 20
  }

  let { data, error } = await API.get('/setup/newUserList', params)
  if (!error) {
    userList = data = (data?.users || []).map(user => ({
      value: user.id,
      label: user.name + ' (' + user.email + ')',
    }))
  }
  return { data: userList, error }
}
export const addModuleParam = function(templateJson, moduleName) {
  let workflowParameters = $getProperty(templateJson, 'workflow.parameters')
  let ftl = templateJson.ftl
  let isModuleInWorkflow =
    ftl &&
    !isEmpty(workflowParameters) &&
    workflowParameters.some(param => param.name === moduleName)
  if (!isModuleInWorkflow) {
    templateJson.workflow.parameters.push({
      name: moduleName,
      typeString: 'String',
    })
  }
}
