import getProperty from 'dlv'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'

export const EXCLUDE_OPERATORS = [74, 75, 76, 77, 78, 79, 35, 81, 82]
export const vmDataModel = {
  name: '',
  description: '',
  scope: null,
  bulkSelectOption: 'all',
  sites: [],
  siteIds: [],
  buildings: [],
  buildingIds: [],
  assetCategory: null,
  spaceCategory: null,
  assets: null,
  spaces: null,
  meterName: '',
  meterDescription: '',
  utilityType: null,
  readings: [],
  relationShipId: null,
}

export const readingModel = {
  readingFieldId: null,
  frequency: null,
  ns: {
    fields: [],
    expressionType: '',
    includedAssetIds: null,
    workflowContext: {
      workflowV2String: '',
      workflowUIMode: null,
    },
  },
}

export const vmScopeHash = {
  1: 'floor',
  2: 'space',
  3: 'spaceCategory',
  4: 'assetCategory',
  7: 'building',
  8: 'site',
}

export function validateReadings(readings = []) {
  let validations = []

  validations = (readings || []).map(reading => {
    let { readingFieldId, uuid, frequency, ns } = reading || {}
    let { workflowContext, fields } = ns || {}
    let workflowV2String = getProperty(workflowContext, 'workflowV2String', '')

    if (isEmpty(readingFieldId)) {
      return { valid: false, message: 'Please select reading name', uuid }
    }
    if (isEmpty(frequency)) {
      return { valid: false, message: 'Please select frequency', uuid }
    }
    let variableError = {}
    ;(fields || []).forEach(variable => {
      if (isEmpty(variableError)) {
        let { varName, fieldId } = variable || {}
        if (isEmpty(fieldId)) {
          variableError = {
            valid: false,
            message: `${varName} is not a valid Variable`,
            uuid,
          }
        }
      }
    })
    if (!isEmpty(variableError)) {
      return variableError
    }

    workflowV2String = workflowV2String
      .replace('Number test(){\n', '')
      .replace('\nreturn', '')
      .replace(';  \n}', '')
      .trim()

    if (isEmpty(workflowV2String)) {
      return { valid: false, message: 'Expression cannot be empty', uuid }
    }
    return { valid: true, message: '', uuid }
  })

  return validations
}

export function serializeVM(virtualMeter = {}) {
  let {
    sites,
    siteIds,
    buildings,
    buildingIds,
    scope,
    assets,
    spaces,
    bulkSelectOption,
    readings,
    assetCategory,
    spaceCategory,
    utilityType,
  } = virtualMeter || {}

  assetCategory = { id: assetCategory }
  spaceCategory = { id: spaceCategory }
  utilityType = { id: utilityType }

  // Serializing lookup fields
  sites = (siteIds || []).map(siteId => {
    return { id: siteId }
  })
  buildings = (buildingIds || []).map(buildingId => {
    return { id: buildingId }
  })
  assets = (assets || []).map(assetId => {
    return { id: assetId }
  })
  spaces = (spaces || []).map(spaceId => {
    return { id: spaceId }
  })

  readings = (readings || []).map(reading => {
    delete reading['uuid']
    return reading
  })

  virtualMeter = {
    ...virtualMeter,
    sites,
    buildings,
    assets,
    spaces,
    readings,
    assetCategory,
    spaceCategory,
    utilityType,
  }

  // Removing unwanted fields from model
  let excludeFields = ['siteIds', 'buildingIds', 'bulkSelectOption']
  let selectedScope = getProperty(vmScopeHash, `${scope}`, '')

  if (selectedScope !== 'spaceCategory') {
    excludeFields.push('spaceCategory')
  }
  if (selectedScope !== 'assetCategory') {
    excludeFields.push('assetCategory')
  }
  if (
    (selectedScope === 'assetCategory' && bulkSelectOption === 'all') ||
    selectedScope !== 'assetCategory'
  ) {
    excludeFields.push('assets')
  }
  if (
    (selectedScope === 'spaceCategory' && bulkSelectOption === 'all') ||
    selectedScope !== 'spaceCategory'
  ) {
    excludeFields.push('spaces')
  }

  excludeFields.forEach(field => {
    delete virtualMeter[`${field}`]
  })

  return virtualMeter
}

export function deserializeVM(virtualMeter = {}) {
  let {
    sites,
    siteIds,
    buildings,
    buildingIds,
    assetCategory,
    spaceCategory,
    utilityType,
  } = virtualMeter || {}

  assetCategory = getProperty(assetCategory, 'id', null)
  spaceCategory = getProperty(spaceCategory, 'id', null)
  utilityType = getProperty(utilityType, 'id', null)
  siteIds = (sites || []).map(site => {
    let { id } = site || {}
    return id
  })
  buildingIds = (buildings || []).map(building => {
    let { id } = building || {}
    return id
  })

  virtualMeter = {
    ...virtualMeter,
    siteIds,
    buildingIds,
    assetCategory,
    spaceCategory,
    utilityType,
  }
  return virtualMeter
}

export const loadMeterReadingFields = async (
  meterId,
  categoryId,
  excludeEmptyFields,
  type,
  fetchValidationRules
) => {
  if (excludeEmptyFields !== false) {
    excludeEmptyFields = true
  }
  let url =
    meterId > 0
      ? 'v2/reading/getmeterspecificreadings?parentId=' + meterId
      : 'v2/readings/utilitytype?id=' + categoryId
  url += '&excludeEmptyFields=' + excludeEmptyFields
  if (type) {
    url += '&readingType=' + type
  }
  if (fetchValidationRules) {
    url += '&fetchValidationRules=' + fetchValidationRules
  }
  let fields = []
  if (meterId > 0) {
    // API for meter specific readings not added yet
    //promise = get(url)
  } else {
    let { data } = await API.get(url)
    if (data) {
      if (meterId > 0) {
        data.forEach(reading => {
          reading.fields.forEach(field => {
            field.module = { name: reading.name }
            fields.push(field)
          })
        })
      } else {
        data.readings.forEach(field => {
          field.module = { name: field.module.name }
          if (fetchValidationRules && data.fieldVsRules) {
            field.readingRules = data.fieldVsRules[field.fieldId]
          }
          let operators = getProperty(field, 'dataTypeEnum.operators')
          if (operators) {
            for (let operatorKey in operators) {
              let operator = operators[operatorKey]
              if (EXCLUDE_OPERATORS.includes(operator.operatorId)) {
                delete operators[operatorKey]
              }
            }
          }
          fields.push(field)
        })
      }
    }
    return fields
  }
}
