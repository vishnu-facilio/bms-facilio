import { datadogRum } from '@datadog/browser-rum'
import getProperty from 'dlv'
import { isEmpty } from './validation'

export const initDataDog = account => {
  // works only in prod, for local dev remove the is prod check for this method,
  let { dataDogClientId } = window || {}
  let email = getProperty(account, 'user.email', '')
  if (!isEmpty(dataDogClientId) && !email.includes('@facilio.com')) {
    let id = getProperty(account, 'user.uid')
    let org_id = getProperty(account, 'org.id')
    let org_domain = getProperty(account, 'org.domain')

    let email_domain

    if (email.includes('@')) {
      email_domain = email.split('@').pop()
    } else {
      email_domain = ''
    }

    datadogRum.setUser({
      id,
      org_id,
      org_domain,
      email_domain,
    })

    const isProd = process.env.NODE_ENV === 'production'
    const applicationId = isProd
      ? '2ce7642f-81f2-4407-9b1d-6ce272e57429'
      : '9f5446f9-e303-4090-9cc2-b245a4b1e91a'
    const service = isProd ? 'vue-client' : 'vue-client-dev'

    let version = window.webpackPublicPath
      ? window.webpackPublicPath.substring(
          window.webpackPublicPath.lastIndexOf('/') + 1,
          window.webpackPublicPath.length
        )
      : ''

    datadogRum.init({
      applicationId,
      clientToken: dataDogClientId,
      site: 'datadoghq.com',
      service,
      version: version,
      sampleRate: 100,
      trackInteractions: true,
      defaultPrivacyLevel: 'mask-user-input',
      enableExperimentalFeatures: ['feature_flags'],
    })
    datadogRum.startSessionReplayRecording()
  }
}
