import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import { getUnRelatedModuleList } from 'src/util/relatedFieldUtil.js'

//TODO : To remove all util methods without parent module name and its usage !!!

export async function getFilteredItemList(filters) {
  let params = { filters: !isEmpty(filters) ? JSON.stringify(filters) : null }
  let { error, list } = await API.fetchAll('item', params)

  if (!error) {
    return list
  } else {
    return []
  }
}

export async function getFilteredItemListWithParentModule(
  parentModuleName,
  filters
) {
  let params = {
    filters: !isEmpty(filters) ? JSON.stringify(filters) : null,
  }
  let { error, list } = await getUnRelatedModuleList(
    parentModuleName,
    'item',
    params
  )

  if (!error) {
    return list
  } else {
    return []
  }
}

export async function getFilteredItemTypeList(filters) {
  let params = { filters: !isEmpty(filters) ? JSON.stringify(filters) : null }
  let { list, error } = await API.fetchAll('itemTypes', params)
  if (!error) {
    return list
  } else {
    return []
  }
}

export async function getFilteredToolList(filters) {
  let params = { filters: !isEmpty(filters) ? JSON.stringify(filters) : null }
  let { list, error } = await API.fetchAll('tool', params)

  if (!error) {
    return list
  } else {
    return []
  }
}

export async function getFilteredToolListWithParentModule(
  parentModuleName,
  filters
) {
  let params = {
    filters: !isEmpty(filters) ? JSON.stringify(filters) : null,
  }
  let { error, list } = await getUnRelatedModuleList(
    parentModuleName,
    'tool',
    params
  )

  if (!error) {
    return list
  } else {
    return []
  }
}

export async function getFilteredToolTypeList(filters) {
  let params = { filters: !isEmpty(filters) ? JSON.stringify(filters) : null }
  let { list, error } = await API.fetchAll('toolTypes', params)
  if (!error) {
    return list
  } else {
    return []
  }
}

export async function getStoreRoomById(id) {
  let { error, data } = await API.get(`/v2/storeRoom/${id}`)

  if (!error) {
    return data.storeRoom
  } else {
    return {}
  }
}

export async function getFilteredStoreRoomList(filters) {
  let params = { filters: !isEmpty(filters) ? JSON.stringify(filters) : null }
  let { list, error } = await API.fetchAll('storeRoom', params)
  if (!error) {
    return list
  } else {
    return []
  }
}
export async function loadNonIndTrackedInventory(moduleName) {
  let filters = {
    isRotating: {
      operatorId: 15,
      value: ['false'],
    },
  }
  let params = {
    filters: JSON.stringify(filters),
  }
  let { list, error } = await API.fetchAll(moduleName, params)
  if (!error) {
    return list
  } else {
    return []
  }
}
