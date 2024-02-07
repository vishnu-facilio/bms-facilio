import { findRouteForTab, isWebTabsEnabled, tabTypes } from '@facilio/router'
import { isEmpty } from '@facilio/utils/validation'
export default {
  methods: {
    findRoute() {
      if (isWebTabsEnabled()) {
        let tabType = tabTypes.CUSTOM
        let config = { type: 'portfolio' }
        let { name } = findRouteForTab(tabType, { config }) || {}

        return name ? this.$router.resolve({ name }).href : null
      } else {
        return '/app/home/portfolio'
      }
    },
    initHierarchy(record) {
      let spaceType = this.$getProperty(record, 'spaceType', null)
      let siteId = this.$getProperty(record, 'siteId', null)
      let building = this.$getProperty(record, 'building', null)
      let floor = this.$getProperty(record, 'floor', null)
      let space1 = this.$getProperty(record, 'space1', null)
      let space2 = this.$getProperty(record, 'space2', null)
      let space3 = this.$getProperty(record, 'space3', null)
      let space4 = this.$getProperty(record, 'space4', null)
      let space5 = this.$getProperty(record, 'space5', null)
      let space = this.$getProperty(record, 'space', null)
      let breadCrumbObj = []
      let recordName = this.$getProperty(record, 'name', null)
      let parentPath = this.findRoute()
      if (!isEmpty(siteId)) {
        let site = this.$store.getters.getSite(siteId)

        breadCrumbObj.push({
          displayName: site.name,
          route: isEmpty(parentPath)
            ? null
            : `${parentPath}/site/${siteId}/overview`,
        })
        if (!isEmpty(building)) {
          let { name, id } = building
          breadCrumbObj.push({
            displayName: spaceType === 2 ? recordName : name,
            route: isEmpty(parentPath)
              ? null
              : `${parentPath}/site/${siteId}/building/${id}`,
          })
        }
        if (!isEmpty(floor)) {
          let { name, id } = floor
          breadCrumbObj.push({
            displayName: spaceType === 3 ? recordName : name,
            route: isEmpty(parentPath)
              ? null
              : `${parentPath}/site/${siteId}/floor/${id}`,
          })
        }
        if (!isEmpty(space1)) {
          let { name, id } = space1
          breadCrumbObj.push({
            displayName: name,
            route: isEmpty(parentPath)
              ? null
              : `${parentPath}/site/${siteId}/space/${id}`,
          })
        }
        if (!isEmpty(space2)) {
          let { name, id } = space2
          breadCrumbObj.push({
            displayName: name,
            route: isEmpty(parentPath)
              ? null
              : `${parentPath}/site/${siteId}/space/${id}`,
          })
        }
        if (!isEmpty(space3)) {
          let { name, id } = space3
          breadCrumbObj.push({
            displayName: name,
            route: isEmpty(parentPath)
              ? null
              : `${parentPath}/site/${siteId}/space/${id}`,
          })
        }
        if (!isEmpty(space4)) {
          let { name, id } = space4
          breadCrumbObj.push({
            displayName: name,
            route: isEmpty(parentPath)
              ? null
              : `${parentPath}/site/${siteId}/space/${id}`,
          })
        }
        if (!isEmpty(space5)) {
          let { name, id } = space5
          breadCrumbObj.push({
            displayName: name,
            route: isEmpty(parentPath)
              ? null
              : `${parentPath}/site/${siteId}/space/${id}`,
          })
        }
        if (!isEmpty(space) && spaceType === 4) {
          let { id } = space
          breadCrumbObj.push({
            displayName: recordName,
            route: isEmpty(parentPath)
              ? null
              : `${parentPath}/site/${siteId}/space/${id}`,
          })
        }
      }
      return breadCrumbObj
    },
    redirect(route) {
      this.$router.push({ path: route })
    },
  },
}
