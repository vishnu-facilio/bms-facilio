import { isPortalDomain } from 'util/utility-methods'

export default {
  computed: {
    isCustomDomain() {
      return window.domainInfo && window.domainInfo.isCustomDomain
    },
    copyrightFooter() {
      return ''
    },
    rebrandInfo() {
      return window.rebrandInfo
    },
    brandConfig() {
      return window.brandConfig
    },
    orgLogoUrl() {
      return window.domainInfo.logo_url
    },
    isWebServicePortal() {
      return isPortalDomain()
    },
    isWebView() {
      return this.$cookie.get('fc.isWebView')
    },
    isMobilePortal() {
      return (
        this.isMobileServicePortal ||
        this.isMobileTenantPortal ||
        this.isMobileVendorPortal ||
        this.isMobileWorkQ
      )
    },
    // Remove urls with app/mobile/ in JAN 2021
    isMobileServicePortal() {
      let { pathname } = window.location
      return (
        pathname.startsWith('/app/mobile/service/login') ||
        pathname.startsWith('/auth/mobile/service/login')
      )
    },
    isMobileVendorPortal() {
      let { pathname } = window.location
      return (
        pathname.startsWith('/app/mobile/vendor/login') ||
        pathname.startsWith('/auth/mobile/vendor/login')
      )
    },
    isMobileTenantPortal() {
      let { pathname } = window.location
      return (
        pathname.startsWith('/app/mobile/tenant/login') ||
        pathname.startsWith('/auth/mobile/tenant/login')
      )
    },
    isMobileWorkQ() {
      let { pathname } = window.location
      return pathname.startsWith('/auth/mobile/workq/login')
    },
  },
}
