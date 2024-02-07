import { isEmpty } from '@facilio/utils/validation'

export function validateRule(rule, type) {
  let mandatoryFields = {}
  if (type === 'readingRule') {
    let { data } = rule || {}
    let { name, assetCategory, ns, ruleInterval, alarmDetails } = data || {}
    let { message, severity } = alarmDetails || {}
    let { workflowContext } = ns || {}
    let { workflowV2String } = workflowContext || {}

    mandatoryFields = {
      name,
      assetCategory,
      workflowV2String,
      ruleInterval,
      message,
      severity,
    }
  } else {
    let { name, assetCategory, workflow } = rule || {}
    let { id } = assetCategory || {}
    let { workflowV2String } = workflow || {}

    mandatoryFields = {
      name,
      id,
      workflowV2String,
    }
  }
  let validRule = Object.values(mandatoryFields).every(value => !isEmpty(value))

  return validRule
}

export function validateFields(rule, type) {
  let variables = []
  let isRelatedReadingPresent = false
  let isCurrentAssetAvailable = false
  if (type === 'readingRule') {
    let { data } = rule || {}
    let { ns } = data || {}
    let { fields } = ns || {}

    variables = fields
  } else {
    let { fields } = rule
    variables = fields
  }

  const uniqueVariables = [
    ...new Set(variables.map(variable => variable.varName)),
  ]
  const finalReadings = []

  variables.forEach(variable => {
    let { fieldId, resourceId, relMapContext, nsFieldType } = variable || {}
    let { mappingLinkName } = relMapContext || {}
    if (!isEmpty(fieldId)) {
      finalReadings.push(fieldId)
    }
    let assetId = resourceId
    if (type === 'faultImpact') {
      let { resource } = variable || {}
      let { id } = resource || {}
      assetId = id
    }
    if (
      (assetId === -1 && nsFieldType === 'ASSET_READING') ||
      !isEmpty(mappingLinkName)
    ) {
      isCurrentAssetAvailable = isCurrentAssetAvailable || true
    }
    if (nsFieldType === 'RELATED_READING') {
      isRelatedReadingPresent = true
    }
  })

  if (
    uniqueVariables.length === variables.length &&
    finalReadings.length === variables.length &&
    (isCurrentAssetAvailable || isRelatedReadingPresent)
  ) {
    return true
  }
  if (type === 'faultImpact' && variables.length === 0) {
    return true
  }
  return false
}

export function validateRca(rule) {
  let mandatoryFields = {}

  let { data } = rule || {}
  let { rca } = data || {}
  if (isEmpty(rca)) {
    return true
  }
  let { groups } = rca || {}
  let validGroups = Object.values(groups).every(group => validateGroup(group))
  let validRca = Object.values(mandatoryFields).every(value => !isEmpty(value))
  return validRca && validGroups
}

export function validateGroup(group) {
  let { criteria, conditions } = group || {}
  let validConditions = conditions.every(condition => {
    return validateCondition(condition)
  })

  let mandatoryFields = {
    criteria,
  }
  let validGroup = Object.values(mandatoryFields).every(
    value => !isEmpty(value)
  )
  return validGroup && validConditions
}

export function validateCondition(condition) {
  let { criteria, score } = condition || {}
  let { conditions } = criteria || {}

  let { fieldName, operatorId } = conditions['1'] || {}
  let mandatoryFields = {
    fieldName,
    operatorId,
    criteria,
    score,
  }
  return Object.values(mandatoryFields).every(value => !isEmpty(value))
}
