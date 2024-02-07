import { API } from '@facilio/api'

const getVisitorSetting = async (visitorTypeId, query = {}) => {
  let url = 'v2/visitorSettings/get'
  let payLoad = {
    visitorType: {
      id: visitorTypeId,
    },
    ...query,
  }
  let { error, data } = await API.post(url, payLoad)

  if (!error) {
    return data
  }
}

const updateVisitorSettings = async visitorSettings => {
  let url = 'v2/visitorSettings/update'
  let payLoad = {
    visitorSettings,
  }
  let { error, data } = await API.post(url, payLoad)

  if (!error) {
    return data
  }
}

export { getVisitorSetting, updateVisitorSettings }
