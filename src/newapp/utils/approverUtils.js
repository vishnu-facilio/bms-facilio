import { isEmpty } from '@facilio/utils/validation'
import getProperty from 'dlv'
import { API } from '@facilio/api'

const approverType = {
  USER: 1,
  ROLE: 2,
  TEAM: 3,
  FIELD: 4,
  TENANT: 6,
  VENDOR: 7,
}

export const approverTypes = [
  {
    id: approverType.USER,
    name: 'User',
  },
  {
    id: approverType.ROLE,
    name: 'Role',
  },
  {
    id: approverType.TEAM,
    name: 'Team',
  },
  {
    id: approverType.FIELD,
    name: 'Field',
  },
  {
    id: approverType.TENANT,
    name: 'Tenant',
  },
  {
    id: approverType.VENDOR,
    name: 'Vendor',
  },
]

async function getSupportedFields(fields) {
  let peopleModuleId = null
  let { data, error } = await API.get('/module/meta', { moduleName: 'people' })
  if (!error) {
    let { meta: { module = {} } = {} } = data
    if (module) {
      peopleModuleId = module['moduleId']
    }
  }
  return fields.filter(field => {
    let isPeopleField = false
    if (peopleModuleId) {
      let extendedModuleIds = getProperty(
        field,
        'lookupModule.extendedModuleIds',
        []
      )
      let moduleId = getProperty(field, 'lookupModule.moduleId', null)
      isPeopleField =
        peopleModuleId === moduleId ||
        extendedModuleIds.some(id => id === peopleModuleId)
    }

    let isUserField = getProperty(field, 'lookupModule.name', '') === 'users'

    return [7, 13].includes(field.dataType) && (isUserField || isPeopleField)
  })
}

function getTenantFields(fields) {
  return fields.filter(
    field =>
      field.dataType === 7 &&
      getProperty(field, 'lookupModule.name', '') === 'tenant'
  )
}

function getVendorFields(fields) {
  return fields.filter(
    field =>
      field.dataType === 7 &&
      getProperty(field, 'lookupModule.name', '') === 'vendors'
  )
}

export async function getApproverOptions(options, supportedTypes = null) {
  let types = !isEmpty(supportedTypes)
    ? supportedTypes
    : Object.values(approverType)

  let supportedFields = await getSupportedFields(options.fields)

  return types.reduce((res, type) => {
    if (type === approverType.USER) {
      res[type] = options.users
    } else if (type === approverType.ROLE) {
      res[type] = options.roles
    } else if (type === approverType.TEAM) {
      res[type] = options.teams
    } else if (type === approverType.VENDOR) {
      res[type] = getVendorFields(options.fields)
    } else if (type === approverType.TENANT) {
      res[type] = getTenantFields(options.fields)
    } else if (type === approverType.FIELD) {
      res[type] = supportedFields
    }
    return res
  }, {})
}

export function deserializeApprover(approver) {
  let { type, roleId, userId, fieldId, groupId } = approver
  let approverObj = { type: null, approverId: null }

  approverObj.type = type

  if (type === approverType.USER) {
    if (userId > 0) {
      approverObj.approverId = userId
    } else if (fieldId > 0) {
      approverObj.approverId = 'requester'
    }
  } else if (type === approverType.ROLE) {
    approverObj.approverId = roleId
  } else if (type === approverType.TEAM) {
    approverObj.approverId = groupId
  } else if (
    [approverType.FIELD, approverType.TENANT, approverType.VENDOR].includes(
      type
    )
  ) {
    approverObj.approverId = fieldId
  }
  return approverObj
}

export function serializeApprover(approver, { fields = [] }) {
  let approverKeys = {
    1: 'userId',
    2: 'roleId',
    3: 'groupId',
    4: 'fieldId',
    6: 'fieldId',
    7: 'fieldId',
  }

  if (isEmpty(approver.type) || isEmpty(approver.approverId)) {
    return null
  }

  if (approver.approverId === 'requester') {
    approver.approverId = (fields.find(f => f.name === 'requester') || {}).id

    return {
      fieldId: approver.approverId,
      type: approver.type,
      actions: approver.actions,
    }
  } else {
    return {
      [approverKeys[approver.type]]: approver.approverId,
      type: approver.type,
      actions: approver.actions,
    }
  }
}
