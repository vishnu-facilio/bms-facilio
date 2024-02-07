<template>
  <div class="floor-filter-con" v-if="visibility">
    <div class="position-relative floor-filter-align" style="width: 691px;">
      <div class="floor-filter-sec-building floor-filter-sec">
        <div class="floor-filter-header flex-middle justify-content-space">
          <div class="fc-black3-16 bold text-left f15">
            Buildings
          </div>
        </div>

        <div class="floor-filter-search">
          <el-input
            placeholder="Search"
            class="fc-input-full-border2"
            prefix-icon="el-icon-search"
            clearable
            v-model="floorSearchBuilding"
          ></el-input>
        </div>

        <div v-if="siteLoading">
          <spinner :show="siteLoading" size="80"></spinner>
        </div>

        <div class="floor-filter-building-list" v-else>
          <el-collapse v-model="buildingActive">
            <template v-for="(site, index) in filteredSites">
              <el-collapse-item
                :name="site.id"
                :key="index"
                v-if="filterdBuildingMap[site.id]"
              >
                <template slot="title">
                  <div class="flex-middle">
                    <div>
                      <inline-svg
                        src="svgs/spacemanagement/site"
                        iconClass="icon text-center icon-xxlll"
                      ></inline-svg>
                    </div>
                    <div class="pL10 bold">
                      {{ site.name }}
                    </div>
                  </div>
                </template>

                <div v-if="buildingLoading">
                  <spinner :show="buildingLoading" size="80"></spinner>
                </div>

                <template v-else-if="filterdBuildingMap[site.id]">
                  <div
                    class="flex-middle floor-list-bg"
                    v-for="(building, idx) in filterdBuildingMap[site.id]"
                    :key="idx"
                    @click="openBuilding(building)"
                    v-bind:class="{ active: selctedBuilding === building.id }"
                  >
                    <inline-svg
                      src="svgs/spacemanagement/building"
                      iconClass="icon text-center icon-lg"
                    ></inline-svg>
                    <div class="label-txt-black text-left pL10">
                      {{ building.name }}
                    </div>
                    <el-tooltip
                      class="item"
                      effect="dark"
                      content="Open Building view"
                      placement="left-end"
                    >
                      <!-- <div
                        class="buildingSwitch-icon"
                        @click.stop="openBuilding(building)"
                      >
                        <i class="el-icon-right"></i>
                      </div> -->
                    </el-tooltip>
                  </div>
                </template>
                <div v-else>
                  <inline-svg
                    src="svgs/spacemanagement/building"
                    iconClass="icon text-center icon-xxlll"
                  ></inline-svg>
                  <div class="label-txt-black fwbold">
                    No building
                  </div>
                </div>
              </el-collapse-item>
            </template>

            <!-- <el-collapse-item name="2">
              <template slot="title">
                <div class="flex-middle">
                  <div>
                    <inline-svg
                      src="svgs/spacemanagement/site"
                      iconClass="icon text-center icon-xxlll"
                    ></inline-svg>
                  </div>
                  <div class="pL10">
                    DEFAULT 5
                  </div>
                </div>
              </template>
              <div></div>
            </el-collapse-item> -->
          </el-collapse>
        </div>
      </div>
      <div class="floor-filter-sec-floor floor-filter-sec floor-filter-right">
        <div class="floor-filter-header flex-middle justify-content-space">
          <div class="fc-black3-16 bold text-left f15 ">
            Floors
          </div>
        </div>

        <div class="floor-filter-search">
          <el-input
            placeholder="Search"
            class="mT5 fc-input-full-border2"
            prefix-icon="el-icon-search"
            v-model="floorSearch"
          ></el-input>
        </div>

        <div class="floor-filter-con-scroll mT30 floor-filter-building-list">
          <div v-if="floorLoading">
            <spinner :show="floorLoading" size="80"></spinner>
          </div>
          <template
            v-else-if="
              buildingFloorMap[selctedBuilding] &&
                buildingFloorMap[selctedBuilding].length
            "
          >
            <div
              class="flex-middle floor-list-bg"
              :key="fidx"
              v-for="(floor, fidx) in filteredfloor"
              v-bind:class="{ active: floor.id === floorId }"
              @click="changeFloorPlan(floor)"
            >
              <inline-svg
                src="svgs/spacemanagement/floor"
                iconClass="icon text-center icon-lg"
              ></inline-svg>
              <div class="label-txt-black text-left pL10">
                {{ floor.name }}
              </div>
            </div>
          </template>
          <template v-else>
            <div class="text-center mT80">
              <inline-svg
                src="svgs/spacemanagement/floor"
                iconClass="icon text-center icon-xlg"
              ></inline-svg>
              <div class="label-txt-black fwBold pT10">
                No floors
              </div>
            </div>
          </template>
        </div>
      </div>
      <div class="floor-filter-close-ico">
        <!-- <i
              class="el-icon-back"
              v-if="floorSection"
              @click="closeFloorSection"
            ></i>
            <i class="el-icon-right" v-else @click="floorSection = true"></i> -->
        <i class="el-icon-back" @click="handleChange"></i>
      </div>
    </div>
  </div>
</template>
<script>
import floorSwitcher from 'src/pages/indoorFloorPlan/components/IndoorFloorPlanSwitcher.vue'
import { API } from '@facilio/api'
import { findRouteForTab, isWebTabsEnabled, tabTypes } from '@facilio/router'

export default {
  extends: floorSwitcher,
  methods: {
    findRoute() {
      if (isWebTabsEnabled()) {
        let tabType = tabTypes.CUSTOM
        let config = { type: 'workplace-analytics' }
        let { name } = findRouteForTab(tabType, { config }) || {}

        return name ? this.$router.resolve({ name }).href : null
      } else {
        return '/app/wp/workplacetreemap'
      }
    },
    openBuilding(building) {
      if (this.floorId) {
        this.selectBuilding(building)
        return
      }
      let parentPath = this.findRoute()

      if (parentPath) {
        this.$router.push({ path: `${parentPath}/${building.id}` })
      }
      this.$nextTick(() => {
        this.handleChange()
      })
    },
    changeFloorPlan(floor, building) {
      if (floor && floor.id) {
        this.$router.replace({
          params: { floorId: floor.id, buildingId: this.selctedBuilding },
        })
      } else if (building) {
        this.openBuilding(building)
      }
      this.$nextTick(() => {
        this.handleChange()
      })
    },
    async loadFloor(buildignId) {
      this.floorLoading = true
      let filter = {
        building: { operatorId: 36, value: [buildignId + ''] },
      }

      let url = `/v2/module/data/list`
      let params = {
        moduleName: 'floor',
        viewName: 'hidden-all',
        filters: JSON.stringify(filter),
      }
      let { data } = await API.get(url, params)
      if (data) {
        this.$set(this.buildingFloorMap, buildignId, data.moduleDatas)
      }
      this.floorLoading = false
    },
  },
}
</script>
<style>
.buildingSwitch-icon {
  right: 20px;
  position: absolute;
  font-size: 17px;
  visibility: hidden;
}
.floor-list-bg:hover .buildingSwitch-icon {
  visibility: visible;
}
</style>
