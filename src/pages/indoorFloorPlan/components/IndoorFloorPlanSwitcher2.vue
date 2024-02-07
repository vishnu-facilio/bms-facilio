<template>
  <div class="floor-filter-con floor-swicther-2">
    <div class="fp-sw-mask" @click="handleChange"></div>
    <div class="floor-sw-mask-top-layer">
      <div class="floor-filter-search p0">
        <el-input
          placeholder="Search"
          class="fc-input-full-border2 emp-fc-input-full-border2"
          prefix-icon="el-icon-search"
          clearable
          v-model="search"
        ></el-input>
      </div>
      <div class="recent-search p15" v-if="recentlySwitchedFloors.length">
        <div class="pB10">
          Recent searches
        </div>
        <div class="em-tag">
          <span
            class="recent-name pointer el-tag el-tag--light m5"
            v-for="(floor, index) in recentlySwitchedFloors"
            :key="index"
            @click="changeFloorPlan(floor)"
          >
            <div class="recent-icon-con">
              <InlineSvg
                iconClass="icon icon-md recent-icon"
                :src="'svgs/employeePortal/recent-floor'"
              ></InlineSvg>
            </div>
            {{ floor.name }}
          </span>
        </div>
      </div>
      <div>
        <el-collapse v-model="activeName" accordion>
          <el-collapse-item
            class="em-sw-building"
            :title="building.name"
            :name="String(index)"
            v-for="(building, index) in filterdData"
            :key="index"
            v-if="building.children && building.children.length"
          >
            <div
              class="emp-sw-floor"
              v-for="(floor, floorIndex) in building.children"
              :key="floorIndex"
              @click="changeFloorPlan(floor)"
            >
              {{ floor.name }}
            </div>
          </el-collapse-item>
        </el-collapse>
      </div>
    </div>
  </div>
</template>
<script>
import IndoorFloorPlanSwitcher from 'pages/indoorFloorPlan/components/IndoorFloorPlanSwitcher'
import { API } from '@facilio/api'

export default {
  extends: IndoorFloorPlanSwitcher,
  data() {
    return {
      activeName: ['0'],
      floors: [],
      search: null,
      recentlySwitchedFloors: [],
    }
  },
  mounted() {
    this.getRecentFloors()
  },
  computed: {
    filteredBuildings() {
      let { siteBuildingMap } = this
      let buildingandSiteMap = {}
      let buildingsList = []
      if (siteBuildingMap) {
        Object.keys(siteBuildingMap).forEach(siteId => {
          let buildings = siteBuildingMap[siteId].filter(building => {
            if (this.search) {
              if (
                building.name.toLowerCase().indexOf(this.search.toLowerCase()) >
                -1
              ) {
                return building
              }
              return null
            }
            return building
          })
          if (buildings.length) {
            buildingsList.push(...buildings)
            this.$set(buildingandSiteMap, siteId, buildings)
          }
        })
      }
      return buildingsList
    },
    filteredfloor() {
      return this.floors.filter(data => {
        if (this.search) {
          if (data.name.toLowerCase().indexOf(this.search.toLowerCase()) > -1) {
            return data
          }
          return null
        }
        return data
      })
    },
    filterdData() {
      let data = []
      this.filteredBuildings.forEach(building => {
        let d = null
        d = building
        let floors = this.filteredfloor.filter(
          rt => rt.buildingId === building.id
        )
        this.$set(d, 'children', floors)
        data.push(d)
      })
      return data
    },
  },
  methods: {
    getRecentFloors() {
      let floorList = JSON.parse(
        window.localStorage.getItem('RECENTLY_SWITCHED_FLOORS')
      )
      if (floorList !== null) {
        this.recentlySwitchedFloors = floorList
      } else {
        this.recentlySwitchedFloors = []
      }
    },
    afterFloorplanChangeHook(floor) {
      if (
        floor?.id &&
        this.recentlySwitchedFloors.findIndex(rt => rt.id === floor.id) === -1
      ) {
        this.setLocalitem(floor)
      }
    },
    setLocalitem(floor) {
      let data = {
        id: floor.id,
        indoorFloorPlanId: floor.indoorFloorPlanId,
        name: floor.name,
      }
      this.recentlySwitchedFloors.push(data)
      if (this.recentlySwitchedFloors.length > 3) {
        this.recentlySwitchedFloors.pop()
      }
      window.localStorage.setItem(
        'RECENTLY_SWITCHED_FLOORS',
        JSON.stringify(this.recentlySwitchedFloors)
      )
    },
    afterBuildingDataHook() {
      this.loadFloors()
    },
    async loadFloors() {
      let buildings = this.filteredBuildings.map(rt => rt.id)
      if (buildings.length) {
        let filter = {
          building: { operatorId: 36, value: buildings.map(rt => String(rt)) },
          indoorFloorPlanId: { operatorId: 2 },
        }

        let url = `/v2/module/data/list`
        let params = {
          moduleName: 'floor',
          viewName: 'hidden-all',
          filters: JSON.stringify(filter),
        }
        let { data } = await API.get(url, params)
        if (data?.moduleDatas) {
          this.floors = data.moduleDatas
        }
      }
    },
  },
}
</script>
<style>
.recent-name.el-tag {
  font-size: 14px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  letter-spacing: normal;
  text-align: justify;
  color: #0053cc;
  border-radius: 17px;
  border: solid 1px #0053cc;
  background-color: #fff;
  display: inline-flex;
}
.recent-name.el-tag:hover {
  font-size: 14px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  letter-spacing: normal;
  text-align: justify;
  color: #fff;
  border-radius: 17px;
  border: solid 1px #0053cc;
  background-color: #0053cc;
}
.recent-icon-con {
  margin-right: 4px;
  margin-top: 4px;
}
.recent-icon {
  top: 3px;
}
.m5 {
  margin: 5px;
}
.em-tag {
  overflow: hidden;
  display: flex;
  flex-wrap: wrap;
}
.fp-sw-mask {
  width: 100%;
  height: 100%;
  background: rgb(0 0 0 / 80%);
  z-index: 1;
  position: absolute;
  top: 0;
}
.portal-home-layout .floor-swicther-2 {
  left: 0px !important;
}
.building-title {
  font-size: 16px;
  font-weight: bold;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: 0.27px;
  color: #324056;
  padding: 18px 20px;
  height: 56px;
}
.floor-swicther-2 {
  width: 100%;
  background: transparent;
}
.floor-sw-mask-top-layer {
  width: 320px;
  background: #fff;
  height: 100%;
  z-index: 10;
  position: absolute;
}
.emp-fc-input-full-border2 .el-input__inner {
  border-radius: 0px !important;
  border-left: 0px !important;
  border-right: 0px !important;
  border-top: 0px !important;
}
.em-sw-building {
  font-size: 14px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: normal;
  color: #324056;
  cursor: pointer;
}
.em-sw-building .el-collapse-item__header {
  letter-spacing: inherit;
  height: 44px;
  padding: 0px 15px;
  cursor: pointer;
}
.em-sw-building .el-collapse-item__header.is-active {
  border-bottom-color: inherit !important;
  background-color: #fafaff;
}
.em-sw-building .el-collapse-item__content {
  padding: 0px !important;
}
.emp-sw-floor {
  font-size: 13px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: normal;
  color: #324056;
  letter-spacing: inherit;
  padding: 14px 15px;
  cursor: pointer;
}
.emp-sw-floor:hover {
  background-color: #fafaff;
}
</style>
