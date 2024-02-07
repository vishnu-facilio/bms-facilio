import { API } from '@facilio/api'

export const getHomepageData = async () => {
  return await API.get('/v3/homepage/list')
}

export const getHomePageWidgetData = async widgetName => {
  return await API.get(`/v3/homepage/widgetData?linkName=${widgetName}`)
}
