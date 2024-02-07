import getProperty from 'dlv'
import { API } from '@facilio/api'

/**
 * Example:
 * parentModuleName:{
 *     relatedModuleName: relatedFieldName
 * }
 *
 */
export const relatedFieldHash = {
  workorder: {
    quote: 'workorder',
    inventoryrequest: 'parentId',
    workorderHazard: 'workorder',
    multiResource: 'parentRecordId',
    workorderCost: 'parentId',
  },
  jobplan: {
    jobplansection: 'jobPlan',
    jobplantask: 'jobPlan',
  },
  jobplantask: {
    jobPlanTaskInputOptions: 'jobPlanTask',
  },
  jobplansection: {
    jobPlanSectionInputOptions: 'jobPlanSection',
    jobplantask: 'taskSection',
  },
  plannedmaintenance: {
    pmPlanner: 'pmId',
    pmResourcePlanner: 'pmId',
  },
  pmPlanner: {
    pmResourcePlanner: 'planner',
  },
}

export function getRelatedFieldName(parentModuleName, relatedModuleName) {
  return getProperty(
    relatedFieldHash,
    `${parentModuleName}.${relatedModuleName}`,
    ''
  )
}

export async function getUnRelatedModuleList(
  moduleName,
  unRelatedModuleName,
  params
) {
  let { force = false } = params || {}
  let url = `v3/unrelated/${moduleName}/fetchAll/${unRelatedModuleName}`
  let { error, data, meta } = await API.get(url, params, { force })
  let { [unRelatedModuleName]: list } = data || {}

  return { error, list, meta }
}

export async function getUnRelatedModuleSummary(
  moduleName,
  unRelatedModuleName,
  id,
  params
) {
  let { force = false } = params || {}
  let url = `v3/unrelated/${moduleName}/fetch/${unRelatedModuleName}/${id}`
  let { error, data } = await API.get(url, params, { force })

  return { error, ...data }
}
