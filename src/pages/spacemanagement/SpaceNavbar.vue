<template>
  <div v-if="site">
    <el-popover
      ref="spaceSwitcher"
      placement="right"
      width="280"
      trigger="click"
      v-model="showSpaceSwitcher"
      @show="switcherOpened"
      popper-class="popover-height asset-popover"
    >
      <space-switcher
        ref="spaceSwitcherContent"
        @onselect="switchSite"
      ></space-switcher>
    </el-popover>

    <div class="row p15 fc-border-bottom  f12" @click="back">
      <div class="col-1 text-center"><i class="el-icon-back fw6"></i></div>
      <div class="col-7 pL5">
        <a class="space-secondary-color">{{}}</a>
      </div>
    </div>
    <div class="row p10 fc-border-bottom">
      <div class="col-3 text-center">
        <space-avatar name="false" size="xlg" :space="site"></space-avatar>
      </div>
      <div class="col-9  self-center">
        <div
          class="fw5 pointer fc-site-name"
          data-switcher-type="site"
          :data-current-value="site.id"
          v-popover:spaceSwitcher
        >
          {{ site.name }}
          <i class="el-icon-d-caret pull-right"></i>
        </div>
        <div class="space-secondary-color f12" style="padding: 2px 5px;">
          Site ID:
          <span class="fc-identify-color-1 f12 fw5 uppercase"
            >#{{ site.id }}</span
          >
        </div>
      </div>
    </div>
    <div class="row sp-navbar">
      <ul class="sp-ul">
        <router-link
          tag="li"
          :to="getSiteLink(site.id)"
          v-on:click.native="currentBuildingId = -1"
          class="uppercase space-secondary-color sp-li p10 ellipsis f12"
        >
          <div class="menu-item">
            <i class="fa fa-th-large f18" aria-hidden="true"></i
            ><span class="label">{{ $t('space.sites.site_overview') }}</span>
          </div>
        </router-link>
        <ul class="sp-ul">
          <router-link
            tag="li"
            v-for="(building, index) in buildings"
            :key="index"
            v-on:click.native="expandBuilding(building)"
            :to="navigate(siteId, building.id, building.spaceType)"
            class="uppercase space-secondary-color sp-li p10 ellipsis f12"
          >
            <div class="menu-item">
              <img
                v-if="building.spaceType === 5"
                height="19px"
                src="~assets/spaces.svg"
              />
              <img v-else height="19px" src="~assets/layers.svg" />
              <span class="label">{{ building.name }}</span>
            </div>
            <f-simple-tree
              v-if="showTree"
              :class="building.id === currentBuildingId ? '' : 'hide'"
              :model="bindModel(building)"
              :load="loadSubTree"
              class="space-left-tree uppercase space-secondary-color sp-li p10 ellipsis f12"
            >
            </f-simple-tree>
          </router-link>
        </ul>
      </ul>
    </div>
  </div>
</template>
<script>
import SpaceSwitcher from './SpaceSwitcher'
import SpaceAvatar from '@/avatar/Space'
import SpaceMixin from '@/mixins/SpaceMixin'
import FSimpleTree from '@/FSimpleTree'
import { findRouteForTab, isWebTabsEnabled, tabTypes } from '@facilio/router'
import { isEmpty } from '@facilio/utils/validation'
export default {
  mixins: [SpaceMixin],
  components: {
    SpaceAvatar,
    SpaceSwitcher,
    FSimpleTree,
  },
  data() {
    return {
      isZone: false,
      buildings: null,
      showSpaceSwitcher: false,
      currentBuildingId: null,
      currentZoneId: null,
      showTree: false,
    }
  },
  created() {
    this.$store.dispatch('loadBuildings')
  },
  mounted() {
    this.initData()
  },
  computed: {
    zones() {
      let filtersZones = this.$store.state.space.zones
      let filteredZones = []
      filtersZones.forEach(zone => {
        if (zone.floorId > 0) {
          filteredZones.push(zone)
        } else if (zone.siteId > 0) {
          filteredZones.push(zone)
        } else if (zone.buildingId > 0) {
          filteredZones.push(zone)
        } else {
          return '--'
        }
      })
      return filteredZones
    },
    siteId() {
      return parseInt(this.$route.params.siteid)
    },
    site() {
      return this.$store.state.space.currentSite.data
    },
    zonesChildren() {
      return this.$store.state.space.currentSite.zones
    },
  },
  methods: {
    bindModel(obj) {
      return {
        name: obj.name,
        path: this.getBuildingLink(this.siteId, obj.id),
        data: obj,
      }
    },
    updateTree() {
      this.initData()
    },
    initData() {
      this.buildings = []
      this.showTree = false
      this.$store.dispatch('space/switchSite', {
        id: parseInt(this.$route.params.siteid),
      })
      if (this.$route.params.zoneid) {
        this.currentBuildingId = parseInt(this.$route.params.buildingid)
        this.currentZoneId = parseInt(this.$route.params.zoneid)
      }
      if (this.$route.params.buildingid) {
        this.currentBuildingId = parseInt(this.$route.params.buildingid)
      }
      if (this.$route.params.floorId) {
        this.currentBuildingId = parseInt(this.$route.params.floorId)
      }
      if (
        this.$account.org.orgId === 210 ||
        this.$account.org.orgId === 237 ||
        (this.$account.org.orgId === 183 && this.siteId === 1299354)
      ) {
        // Temp Handling added for Alfajer Pointe mall
        let subparams = [
          { key: 'site', value: this.$route.params.siteid },
          { key: 'floorId', operator: 'is empty' },
          { key: 'buildingId', operator: 'is empty' },
          { key: 'space1', operator: 'is empty' },
          { key: 'space2', operator: 'is empty' },
          { key: 'space3', operator: 'is empty' },
        ]
        this.$util.loadSpace([2, 4], null, subparams).then(response => {
          if (response.basespaces) {
            self.buildings.push(...response.basespaces)
          }
        })
      }
      let url =
        '/basespace/children?spaceId=' +
        parseInt(this.$route.params.siteid) +
        '&spaceType=site&isZone=true'
      let self = this
      self.$http.get(url).then(function(response) {
        self.showTree = true
        self.buildings.unshift(...response.data.basespaces)
      })
    },
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
    back() {
      let parentPath = this.findRoute()
      if (parentPath) {
        //this.$router.push({ path: `${parentPath}/sites` })
      }
    },
    switcherOpened() {
      this.$refs.spaceSwitcherContent.load(
        this.$refs.spaceSwitcher.referenceElm.dataset
      )
    },
    navigate(site, id, type) {
      let parentPath = this.findRoute()

      if (parentPath) {
        if (parseInt(type) === 5) {
          return `${parentPath}/site/${site}/zone/${id}`
        } else if (parseInt(type) === 4) {
          return `${parentPath}/site/${site}/space/${id}`
        } else {
          return `${parentPath}/site/${site}/building/${id}`
        }
      }
    },
    switchSite(site) {
      let siteUrl = this.getSiteLink(site.id)
      this.$router.push({ path: siteUrl })
      this.initData()
      this.showSpaceSwitcher = false
    },
    loadSubTree(item, resolve) {
      let self = this
      let spaceKey
      let subparams
      if (item.data.spaceType === 1) {
        spaceKey = 'site'
      } else if (item.data.spaceType === 2) {
        spaceKey = 'building'
      } else if (item.data.spaceType === 3) {
        spaceKey = 'floor'
        subparams = [
          { key: spaceKey, value: item.data.id },
          {
            key: 'space1',
            operator: item.data.spaceId1 > 0 ? 'is' : 'is empty',
            value: item.data.spaceId1,
          },
          {
            key: 'space2',
            operator: item.data.spaceId2 > 0 ? 'is' : 'is empty',
            value: item.data.spaceId2,
          },
          {
            key: 'space3',
            operator: item.data.spaceId3 > 0 ? 'is' : 'is empty',
            value: item.data.spaceId3,
          },
        ]
      } else if (item.data.spaceType === 4) {
        spaceKey = 'space'
        if (item.data.spaceId1 < 0) {
          subparams = [
            { key: 'space1', operator: 'is', value: item.data.spaceId },
            { key: 'space2', operator: 'is empty' },
            { key: 'space3', operator: 'is empty' },
          ]
        } else if (item.data.spaceId2 < 0) {
          subparams = [
            { key: 'space2', operator: 'is', value: item.data.spaceId },
            { key: 'space3', operator: 'is empty' },
          ]
        } else {
          subparams = [
            { key: 'space3', operator: 'is', value: item.data.spaceId },
          ]
        }
      } else {
        return
      }
      if (item.data.spaceType === 2) {
        let url = '/floor?buildingId=' + item.data.id
        self.$http.get(url).then(function(response) {
          let basespaces
          basespaces = response.data.records
          if (basespaces) {
            let children = []
            for (let bs of basespaces) {
              let path = ''
              if (bs.spaceTypeVal === 'Site') {
                path = self.getSiteLink(bs.id)
              } else if (bs.spaceTypeVal === 'Building') {
                path = self.getBuildingLink(self.siteId, bs.id)
              } else if (bs.spaceTypeVal === 'Floor') {
                path = self.getFloorLink(self.siteId, bs.id)
              } else if (bs.spaceTypeVal === 'Space') {
                path = self.getSpaceLink(self.siteId, bs.id)
              } else if (bs.spaceTypeVal === 'Zone') {
                let parentPath = self.findRoute()
                if (parentPath) {
                  path = `${parentPath}/zone/${bs.id}/overview`
                }
              }
              children.push({
                key: bs.id + '_' + bs.spaceTypeVal,
                name: bs.name,
                path: path,
                data: bs,
                leaf: bs.spaceTypeVal === 'Space',
              })
            }
            resolve(children)
          } else {
            resolve([])
          }
        })
      } else {
        let spaceType =
          item.data.spaceType <= 3
            ? item.data.spaceType + 1
            : item.data.spaceType
        // default load zone
        this.$util.loadSpace([spaceType, 5], null, subparams).then(response => {
          let basespaces

          basespaces = response.basespaces
          if (basespaces) {
            let children = []
            for (let bs of basespaces) {
              let path = ''
              if (bs.spaceTypeVal === 'Site') {
                path = self.getSiteLink(bs.id)
              } else if (bs.spaceTypeVal === 'Building') {
                path = self.getBuildingLink(self.siteId, bs.id)
              } else if (bs.spaceTypeVal === 'Floor') {
                path = self.getFloorLink(self.siteId, bs.id)
              } else if (bs.spaceTypeVal === 'Space') {
                path = self.getSpaceLink(self.siteId, bs.id)
              } else if (bs.spaceTypeVal === 'Zone') {
                let parentPath = self.findRoute()
                if (parentPath) {
                  path = `${parentPath}/zone/${bs.id}/overview`
                }
              }
              children.push({
                key: bs.id + '_' + bs.name,
                name: bs.name,
                path: path,
                data: bs,
                leaf: false,
              })
            }
            resolve(children)
          } else {
            resolve([])
          }
        })
      }
    },
    expandBuilding(building) {
      this.currentBuildingId = building.id
    },
    expandZone(zone) {
      this.currentZoneId = zone.id
    },
  },
}
</script>
<style>
.fc-site-name {
  padding: 5px;
  border-radius: 2px;
}
.fc-site-name:hover {
  background: #f7f8f9;
}
.sp-navbar {
  padding: 0 15px;
}
.sp-navbar ul li {
  list-style: none;
  padding: 0px;
}
.sp-navbar ul li .menu-item,
.sp-navbar ul li .node-label {
  display: flex;
  align-items: center;
  padding: 10px;
  cursor: pointer;
}
.sp-navbar ul li .menu-item .label {
  margin-left: 15px;
}
.sp-navbar ul li.active .menu-item {
  background: #f7f8f9;
}
.space-left-tree {
  padding: 0px 24px;
}
.space-left-tree .el-tree-node {
  padding: 2px;
}
.space-left-tree .el-tree__empty-block {
  display: none;
}
.sp-ul {
  width: 100%;
  padding: 0px;
}
.sp-navbar .sp-ul li .menu-item:hover,
.sp-navbar .sp-ul li.active .menu-item {
  background: #f7f8f9;
  cursor: pointer;
  color: #9666cf;
}
.sp-navbar li:hover .node-label,
.sp-navbar li.active .node-label {
  color: #9666cf;
}
.sp-navbar li .sp-sub-ul {
  display: none;
}
.sp-navbar li .sp-sub-ul li {
  padding-left: 0px;
}
.sp-navbar li.active .sp-sub-ul {
  display: block;
}
.sp-sub-ul li:hover .sub-menu-item,
.sp-sub-ul li.active .sub-menu-item {
  cursor: pointer;
  color: #9666cf;
}
.sp-sub-ul li .sub-menu-item {
  padding: 10px 0px;
}
</style>
