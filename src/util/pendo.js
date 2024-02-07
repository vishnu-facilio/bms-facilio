import getProperty from 'dlv'
import { getApp } from '@facilio/router'
import dlv from 'dlv'

export const initPendo = (account, instance) => {
  let app = getApp()
  let appEnum = dlv(app, 'appDomain.appDomainTypeEnum')
  if (['SERVICE_PORTAL', 'TENANT_PORTAL'].includes(appEnum)) {
    if (instance?.$helpers?.isLicenseEnabled('PENDO_TENANT'))
      startPendoScripts(account)
  } else if (appEnum === 'VENDOR_PORTAL') {
    if (instance?.$helpers?.isLicenseEnabled('PENDO_VENDOR'))
      startPendoScripts(account)
  } else {
    if (instance?.$helpers?.isLicenseEnabled('PENDO'))
      startPendoScripts(account)
  }
}

export const startPendoScripts = account => {
  if (window?.isGoogleAnalytics) {
    ;(function(apiKey) {
      ;(function(p, e, n, d, o) {
        let v, w, x, y, z
        o = p[d] = p[d] || {}
        o._q = o._q || []
        v = ['initialize', 'identify', 'updateOptions', 'pageLoad', 'track']
        for (w = 0, x = v.length; w < x; ++w)
          (function(m) {
            o[m] =
              o[m] ||
              function() {
                o._q[m === v[0] ? 'unshift' : 'push'](
                  [m].concat([].slice.call(arguments, 0))
                )
              }
          })(v[w])
        y = e.createElement(n)
        y.async = !0
        y.src = 'https://cdn.eu.pendo.io/agent/static/' + apiKey + '/pendo.js'
        z = e.getElementsByTagName(n)[0]
        z.parentNode.insertBefore(y, z)
      })(window, document, 'script', 'pendo')
      let id = getProperty(account, 'user.id')
      let user_email = getProperty(account, 'user.email')
      let user_name = getProperty(account, 'user.name')
      let role = getProperty(account, 'user.role.name')
      let appDomain = getProperty(account, 'user.appDomain.domain')
      let orgId = getProperty(account, 'org.id')
      let domain = getProperty(account, 'org.domain')
      let org_name = getProperty(account, 'org.name')
      window.pendo.initialize({
        visitor: {
          id: user_email,
          full_name: user_name,
          role: `${domain} ${role}`,
          user_id: id,
        },
        account: {
          id: domain,
          domain: appDomain,
          full_name: org_name,
          org_id: orgId,
        },
      })
    })('a1924d14-df6b-479d-5107-6032d133fdbf')
    startListeners()
  }
}

const startListeners = () => {
  window.addEventListener('message', event => {
    let { data } = event
    if (isJSONString(data)) {
      let parsedData = JSON.parse(data)
      let { id, response, type } = parsedData || {}
      let message = 'CreateRecord'
      if (type === 'edit') message = 'EditRecord'
      if (id === 'facilio/api') {
        window.pendo.track(message, {
          response,
          type,
        })
      }
    }
  })
}

const isJSONString = str => {
  try {
    JSON.parse(str)
  } catch (e) {
    return false
  }
  return true
}
