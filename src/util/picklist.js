import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import getProperty from 'dlv'
import { constructOptionsToMap } from '@facilio/utils/utility-methods'

export const getFieldOptions = async props => {
  let {
    field,
    siteId,
    searchText,
    page,
    perPage,
    defaultIds,
    apiConfig = {},
    apiOptions = {},
  } = props
  let {
    filters = {},
    field: fieldObj,
    clientCriteria = {},
    lookupModule,
    resourceLookupModuleName,
    lookupModuleName,
    skipDeserialize,
    operatorLookupModule,
    additionalParams = {},
  } = field || {}
  let { name } = lookupModule || {}
  let { name: operatorLookupModuleName } = operatorLookupModule || {}
  let moduleName = resourceLookupModuleName || lookupModuleName || name
  if (!isEmpty(operatorLookupModuleName)) {
    moduleName = operatorLookupModuleName
  }
  let { filters: lookupModuleFilters } = lookupModule || {}
  if (!isEmpty(lookupModuleFilters)) {
    filters = {
      ...filters,
      ...lookupModuleFilters,
    }
  }
  if (!isEmpty(siteId)) {
    filters = {
      ...filters,
      siteId: {
        operatorId: 36,
        value: [`${siteId}`],
      },
    }
  }
  if (
    getProperty(fieldObj, 'module.name') === 'quote' &&
    moduleName === 'workorder'
  ) {
    filters = {
      ...filters,
      // isQuotationNeeded: {
      //   operatorId: 15,
      //   value: [String(true)],
      // },
    }
  }
  let defaultIdsStr = !isEmpty(defaultIds) ? defaultIds.toString() : ''

  let payload = {
    moduleName,
    searchText,
    page,
    perPage,
    filters: !isEmpty(filters) ? JSON.stringify(filters) : null,
    clientCriteria: !isEmpty(clientCriteria)
      ? JSON.stringify(clientCriteria)
      : null,
    defaultIds: defaultIdsStr || null,
    additionalParams,
    apiConfig,
    apiOptions,
  }
  let { error, options = [], meta } = (await fetchFieldOptions(payload)) || {}
  if (error) {
    return { error }
  } else {
    if (skipDeserialize) {
      let optionsObj = constructOptionsToMap(options || [])
      return { options: optionsObj, meta }
    } else {
      return { options, meta }
    }
  }
}

export const fetchFieldOptions = async payload => {
  let {
    moduleName,
    searchText,
    page,
    perPage,
    filters,
    clientCriteria,
    defaultIds,
    additionalParams,
    apiOptions,
    apiConfig,
  } = payload
  let url = `/v3/picklist/${moduleName}`
  let params = {
    page,
    perPage,
    clientCriteria,
    filters,
    search: searchText,
    default: defaultIds,
    viewName: 'hidden-all',
    ...additionalParams,
  }
  let { error, data, meta = {} } =
    (await API.get(url, params, apiConfig, apiOptions)) || {}
  if (error) {
    return { error }
  } else {
    let { pickList: options } = data || {}
    return { options: options || [], meta }
  }
}

export const getFieldValue = async props => {
  let { lookupModuleName, selectedOptionId } = props
  let labelMeta = {}

  labelMeta[lookupModuleName] = selectedOptionId

  let { data, error } = await API.post(`/v2/picklist/label`, { labelMeta })

  if (error) {
    return { error }
  } else {
    let { label } = data || {}
    return { data: label[lookupModuleName] } || { data: null }
  }
}

export const getIsSiteDecommissioned = data => {
  let { fourthLabel } = data || {}
  return isEmpty(fourthLabel) ? false : JSON.parse(fourthLabel)
}
