import { API } from '@facilio/api'
import $helpers from 'util/helpers'
import Vue from 'vue'

export const defaultAppList = {
  iot: { license: 'ENERGY' },
  tenant: { license: 'TENANTS' },
  vendor: { license: 'VENDOR' },
  client: { license: 'CLIENT_PORTAL' },
}

export const getFilteredApps = apps => {
  return apps.filter(app => {
    {
      let { linkName } = app
      let { license } = defaultAppList[linkName] || {}

      if (license) {
        return (
          $helpers.isLicenseEnabled(license) ||
          // Requested by Vandhana for investa org
          (linkName === 'vendor' && Vue.prototype?.$org?.id === 17)
        )
      } else {
        return true
      }
    }
  })
}

export const loadApps = async () => {
  let { data, error } = await API.get('/v2/application/list')
  if (error) {
    return { error }
  } else {
    return { data: data.application }
  }
}
