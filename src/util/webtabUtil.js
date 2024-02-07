import { API } from '@facilio/api'
import icons from 'newapp/webtab-icons.js'

export const loadAppTabs = (appId, additionalParams = {}) => {
  return API.get('/v2/tab/listForApp', { appId, ...additionalParams }).then(
    ({ data, error }) => {
      if (error) {
        return { error }
      } else {
        return { data: data.webTabs || [] }
      }
    }
  )
}

export const loadLayouts = (appId, force = false, additionalParams = {}) => {
  return API.get(
    '/v2/application/fetchDetails',
    { fetchAllLayouts: true, appId, ...additionalParams },
    { force }
  ).then(({ data, error }) => {
    if (error) {
      return { error }
    } else {
      return { data: data.application || {} }
    }
  })
}

export const getGroupIcon = type => {
  let iconType = icons[type] || {}
  return iconType.icon || icons[0].icon
}

export const getGroupIconClass = type => {
  let iconType = icons[type] || {}
  return iconType.class || ''
}
