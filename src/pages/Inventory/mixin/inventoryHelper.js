import { isWebTabsEnabled, findRouteForTab, tabTypes } from '@facilio/router'
import { isEmpty } from '@facilio/utils/validation'

export default {
  methods: {
    findRoute() {
      if (isWebTabsEnabled()) {
        let tabType = tabTypes.CUSTOM
        let config = { type: 'portfolio' }
        let route = findRouteForTab(tabType, { config }) || {}

        if (!isEmpty(route)) {
          return this.$router.resolve({ name: route.name }).href
        } else {
          return null
        }
      } else {
        return '/app/home/portfolio'
      }
    },
    getSpaceRouteLink(resource) {
      let parentPath = this.findRoute()

      if (resource.siteId && parentPath) {
        let url = `${parentPath}/site/${resource.siteId}`
        if (resource.spaceType === 1) {
          url += '/overview'
        } else if (resource.spaceType === 2) {
          url += '/building/' + resource.spaceId
        } else if (resource.spaceType === 3) {
          url += '/floor/' + resource.spaceId
        } else if (resource.spaceType === 4) {
          url += '/space/' + resource.spaceId
        }
        return url
      }
    },
  },
}
