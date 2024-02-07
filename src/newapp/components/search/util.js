import { API } from '@facilio/api'

export const fetchSearchableFields = async moduleName => {
  let url = `/v2/fields/advancedFilter/${moduleName}`
  let response = await API.get(url)
  return response
}
