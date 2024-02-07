const Handlebars = require('handlebars')
const isDev = process.env.NODE_ENV === 'development'

function stringify(json) {
  // To make handlebars unescape the data. Used only in dev
  return new Handlebars.SafeString(JSON.stringify(json))
}

function isPortal() {
  if (isDev) {
    const servicePortalDomainsList = [
      'fazilio.com',
      'stageportal.facilio.in',
      'stagetenant.facilio.in',
      'stagevendor.facilio.in',
      'stage2portal.facilio.in',
      'stage2tenant.facilio.in',
      'stage2vendor.facilio.in',
    ]
    const { VUE_APP_FACILIO_BASE_URL = '' } = process.env

    return servicePortalDomainsList.some(domainName =>
      (VUE_APP_FACILIO_BASE_URL || '').includes(domainName)
    )
  } else {
    return false
  }
}

module.exports = {
  development: {
    staticURL: '',
    webpackPublicPath: '',
    title: 'Facilio',
    favicon: '/statics/favicon.png',
    faviconURL: 'https://static.facilio.com/common/favicon.png',
    rebrandInfo: stringify({
      brandName: 'Facilio',
      name: 'facilio',
      copyright: { name: 'Facilio Inc', year: '2022' },
      domain: '.facilio.com',
      servername: 'localhost:8080',
    }),
    brandConfig: stringify({
      name: 'Facilio',
      legalName: 'Facilio Inc',
      logo: 'https://static.facilio.com/common/facilio-dark-logo.svg',
      logoLight: 'https://static.facilio.com/common/facilio-light-logo.svg',
      favicon: 'https://static.facilio.com/common/favicon.png',
      website: 'www.facilio.com',
      copyright: 'Facilio Inc &copy; 2023',
    }),
    domainInfo: stringify({
      isCustomDomain: false,
      isSSOEnabled: false,
      ssoEndPoint: null,
      isPortal: isPortal(),
    }),
    servicePortalDomain: isPortal(),
    googleAuthEnable: true,
    googleAuthClientId:
      '480704980623-mefe6uf2v66pn5mk3tm7ti42cnqtkbj2.apps.googleusercontent.com',
    dataDogClientId: 'pubb065ce5aa6a0cf51468ac84be3072f15',
    // identityServerURL: 'https://stage.facilio.in/identity',
    isGoogleAnalytics: false,
  },
  production: {
    staticURL: '{{staticURL}}',
    webpackPublicPath: '{{webpackPublicPath}}',
    title: '{{title}}',
    favicon: '{{favicon}}',
    faviconURL: '{{faviconURL}}',
    brandConfig: '{{brandConfig}}',
    rebrandInfo: '{{rebrandInfo}}',
    domainInfo: '{{domainInfo}}',
    servicePortalDomain: '{{servicePortalDomain}}',
    googleAuthEnable: '{{googleAuthEnable}}',
    googleAuthClientId: '{{googleAuthClientId}}',
    dataDogClientId: '{{dataDogClientId}}',
    identityServerURL: '{{identityServerURL}}',
    isGoogleAnalytics: '{{isGoogleAnalytics}}',
  },
}
