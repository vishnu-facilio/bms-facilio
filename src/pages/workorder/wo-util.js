import { getFieldOptions } from 'util/picklist'
import { isEmpty } from '@facilio/utils/validation'

export async function getSites(props) {
  let { searchText, filters } = props || {}
  let sites = []
  let params = {
    field: { lookupModuleName: 'site' },
  }

  if (!isEmpty(searchText)) params = { ...params, searchText }
  else if (!isEmpty(filters)) params = { ...params, field: { filters } }

  sites = await getFieldOptions(params).then(({ error, options }) => {
    if (!error) {
      options = (options || []).map(option => {
        let { label, value } = option || {}
        return { id: value, name: label }
      })
      return options
    }
  })
  return sites
}

export async function getBuildings(props) {
  let { searchText, filters, siteId } = props || {}
  let buildings = []
  let params = {
    field: { lookupModuleName: 'building' },
  }

  if (!isEmpty(searchText)) params = { ...params, searchText }
  if (!isEmpty(filters)) params = { ...params, field: { filters } }
  if (!isEmpty(siteId)) params = { ...params, siteId }

  buildings = await getFieldOptions(params).then(({ error, options }) => {
    if (!error) {
      options = (options || []).map(option => {
        let { label, value } = option || {}
        return { id: value, name: label }
      })
      return options
    }
  })
  return buildings
}
