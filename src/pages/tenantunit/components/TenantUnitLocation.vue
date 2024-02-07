<template>
  <div>
    <div class="height100 flex-wrap">
      <el-row>
        <div class="flex-middle justify-content-space p20">
          <div class="f13 bold text-uppercase fc-black-13 text-left">
            {{ $t('common._common.location') }}
          </div>
          <div
            v-tippy
            :title="latLngAvailable ? 'Open in Map' : 'Location'"
            :class="['f11', 'bold', latLngAvailable ? 'pointer' : '']"
            @click="openMap()"
          >
            <img
              src="~assets/svgs/tenant/tenantunit/location.svg"
              class="mR10"
              style="width:15px;height:15px"
            />
          </div>
        </div>
      </el-row>
      <el-row>
        <el-col :span="8" class="flex justify-center">
          <div class="location-icon-container">
            <img src="~assets/svgs/tenant/tenantunit/floor.svg" class="" />
          </div>
        </el-col>
        <el-col :span="16" class="">
          <div class="f11 mT5 color-light">
            {{ $t('common._common.floor') }}
          </div>
          <div
            class="mT15 f16 pointer"
            @click="redirectToOverview(getTenantFloorId, 'floor')"
          >
            {{ getTenantFloor }}
          </div>
        </el-col>
      </el-row>
      <el-row>
        <div class="mT20 line-hr"></div>
      </el-row>
      <el-row class="mT20">
        <el-col :span="8" class="flex justify-center">
          <div class="location-icon-container">
            <img src="~assets/svgs/tenant/tenantunit/building.svg" class="" />
          </div>
        </el-col>
        <el-col :span="16" class="">
          <div class="f11 mT5 color-light">
            {{ $t('common._common.building') }}
          </div>
          <div
            class="mT15 f16 pointer"
            @click="redirectToOverview(getTenantBuildingId, 'building')"
          >
            {{ getTenantBuilding }}
          </div>
        </el-col>
      </el-row>
      <el-row>
        <div class="mT20 line-hr"></div>
      </el-row>
      <el-row class="mT20">
        <el-col :span="8" class="flex justify-center">
          <div class="location-icon-container">
            <img src="~assets/svgs/tenant/tenantunit/location.svg" class="" />
          </div>
        </el-col>
        <el-col :span="16" class="">
          <div class="f11 mT5 color-light">
            {{ $t('common.products.site') }}
          </div>
          <div
            class="mT15 f16 pointer"
            @click="redirectToOverview(getTenantSiteId, 'site')"
          >
            {{ getTenantSite }}
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>
<script>
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['details'],
  computed: {
    getTenantSite() {
      let { details } = this
      let { site } = details || {}
      let { name } = site || {}
      return name || '---'
    },
    getTenantBuilding() {
      let { details } = this
      let { building } = details || {}
      let { name } = building || {}
      return name || '---'
    },
    getTenantFloor() {
      let { details } = this
      let { floor } = details || {}
      let { name } = floor || {}
      return name || '---'
    },
    getTenantSiteId() {
      let { details } = this
      let { site } = details || {}
      let { id } = site || {}
      return id
    },
    getTenantBuildingId() {
      let { details } = this
      let { building } = details || {}
      let { id } = building || {}
      return id
    },
    getTenantFloorId() {
      let { details } = this
      let { floor } = details || {}
      let { id } = floor || {}
      return id
    },
    latLngAvailable() {
      let { details } = this
      let { location } = details || {}
      let { lat, lng } = location || {}
      if (!isEmpty(lat) && !isEmpty(lng)) {
        return true
      }
      return false
    },
  },
  methods: {
    redirectToOverview(id, moduleName) {
      if (!this.$validation.isEmpty(id)) {
        let route
        let params = {
          siteid: this.getTenantSiteId,
          buildingid: this.getTenantBuildingId,
          floorid: this.getTenantFloorId,
          viewname: 'all',
        }

        if (isWebTabsEnabled()) {
          let { name } =
            findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}

          if (name) {
            route = this.$router.resolve({
              name,
              params,
            }).href
          }
        } else {
          let routeName = null
          switch (moduleName) {
            case 'site':
              routeName = 'site-overview'
              break
            case 'building':
              routeName = 'building-overview'
              break
            case 'floor':
              routeName = 'floor-overview'
              break
          }
          route = this.$router.resolve({
            name: routeName,
            params,
          }).href
        }
        route && window.open(route, '_blank')
      }
    },
    openMap() {
      let { details } = this
      let { location } = details || {}
      let { lat, lng } = location || {}
      if (!isEmpty(lat) && !isEmpty(lng)) {
        this.$helpers.openInMap(lat, lng)
      }
    },
  },
}
</script>
<style scoped>
.line-vr {
  margin-top: 20px;
  width: 1px;
  height: 190px;
  background: #8ca1ad;
  opacity: 0.3;
}
.line-hr {
  margin-top: 25px;
  width: 100%;
  height: 1px;
  background: #8ca1ad;
  opacity: 0.3;
}
.mL55 {
  margin-left: 55px;
}
.mT100 {
  margin-top: 100px;
}
.tenant-wo-url:hover {
  cursor: pointer;
}
.f60 {
  font-size: 60px;
}
.mT65 {
  margin-top: 65px;
}
.tenant-photo {
  width: 120px;
  height: 120px;
  border-radius: 50%;
}
.email-icon {
  width: 15px;
  height: 15px;
}
.overdue-warning {
  color: #eb6a6a;
}
.location-icon-container {
  border-radius: 50%;
  border: 1px solid rgba(45, 45, 81, 0.1);
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 17px;
}
.color-light {
  color: #324056;
}
</style>
