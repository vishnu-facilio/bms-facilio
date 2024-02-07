import { isWebTabsEnabled, findRouteForTab, tabTypes } from '@facilio/router'
import { isEmpty } from '@facilio/utils/validation'

export default {
  methods: {
    findRoute() {
      if (isWebTabsEnabled()) {
        let { $router } = this
        let tabType = tabTypes.CUSTOM
        let config = { type: 'portfolio' }
        let route = findRouteForTab(tabType, { config }) || {}

        if (!isEmpty(route)) {
          return $router.resolve({ name: route.name }).href
        } else {
          return null
        }
      } else {
        return '/app/home/portfolio'
      }
    },
    getSiteLink(siteId) {
      let parentPath = this.findRoute()

      if (parentPath) {
        return this.$router.resolve({
          path: `${parentPath}/site/${siteId}/overview`,
        }).href
      } else {
        return null
      }
    },
    openSiteLink(siteId) {
      let siteLink = this.getSiteLink(siteId)
      if (siteLink) this.$router.push({ path: siteLink })
    },
    getBuildingLink(siteId, buildingId) {
      let parentPath = this.findRoute()

      if (parentPath) {
        return this.$router.resolve({
          path: `${parentPath}/site/${siteId}/building/${buildingId}`,
        }).href
      } else {
        return null
      }
    },
    getSiteZoneLink(zoneId) {
      let parentPath = this.findRoute()

      if (parentPath) {
        return this.$router.resolve({
          path: `${parentPath}/zone/${zoneId}/overview`,
        }).href
      } else {
        return null
      }
    },
    openBuildingLink(siteId, buildingId) {
      let buildingLink = this.getBuildingLink(siteId, buildingId)
      if (buildingLink) this.$router.push({ path: buildingLink })
    },
    getFloorLink(siteId, floorId) {
      let parentPath = this.findRoute()

      if (parentPath) {
        return this.$router.resolve({
          path: `${parentPath}/site/${siteId}/floor/${floorId}`,
        }).href
      } else {
        return null
      }
    },
    openFloorLink(siteId, floorId) {
      let floorLink = this.getFloorLink(siteId, floorId)
      if (floorLink) this.$router.push({ path: floorLink })
    },
    getSpaceLink(siteId, spaceId) {
      let parentPath = this.findRoute()

      if (parentPath) {
        return this.$router.resolve({
          path: `${parentPath}/site/${siteId}/space/${spaceId}`,
        }).href
      } else {
        return null
      }
    },
    openSpaceLink(siteId, spaceId) {
      let spaceLink = this.getSpaceLink(siteId, spaceId)
      if (spaceLink) this.$router.push({ path: spaceLink })
    },
    getZoneLink(zoneId) {
      let parentPath = this.findRoute()

      if (parentPath) {
        return this.$router.resolve({
          path: `${parentPath}/zone/${zoneId}/overview`,
        }).href
      } else {
        return null
      }
    },
    openZoneLink(zoneId) {
      let zoneLink = this.getZoneLink(zoneId)
      if (zoneLink) this.$router.push({ path: zoneLink })
    },
    deleteSpace(id, spaces) {
      let promptObj = {
        title: 'Delete space',
        message: 'Are you sure you want to delete this space?',
        rbDanger: true,
        rbLabel: 'Delete',
      }
      this.$dialog.confirm(promptObj).then(value => {
        if (value) {
          this.$http.post('/campus/delete', { id: id }).then(response => {
            if (response.data && !response.data.error && response.data.id) {
              let doSpace = spaces.find(r => r.id === response.data.id)
              if (doSpace) {
                spaces.splice(spaces.indexOf(doSpace), 1)
              }
              // this.visibility = false
              this.$message.success('Deleted Successfully')
            } else if (response.data && response.data.error) {
              this.$message.error(response.data.error)
            } else {
              this.$message.error('Deletion operation failed')
            }
          })
        }
      })
    },
  },
}
