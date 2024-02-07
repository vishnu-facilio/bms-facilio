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
                    @click="selectBuilding(building)"
                    v-bind:class="{ active: selctedBuilding === building.id }"
                  >
                    <inline-svg
                      src="svgs/spacemanagement/building"
                      iconClass="icon text-center icon-lg"
                    ></inline-svg>
                    <div class="label-txt-black text-left pL10">
                      {{ building.name }}
                    </div>
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
      <div
        class="floor-filter-sec-floor floor-filter-sec floor-filter-right"
        v-if="floorSection"
      >
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
      <div class="floor-filter-close-ico" v-if="floorSection">
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
import { mapState } from 'vuex'
import { API } from '@facilio/api'
export default {
  props: ['visibility', 'floorplanId', 'floorId', 'buildingId'],
  data() {
    return {
      floorSearchBuilding: null,
      buildingActive: [],
      floorSection: false,
      siteBuildingMap: {},
      buildingLoading: false,
      selctedBuilding: null,
      floorLoading: false,
      buildingFloorMap: {},
      floorSearch: null,
      siteLoading: false,
    }
  },
  watch: {
    fpType: {
      immediate: true,
      handler() {
        // change floorplan here
      },
    },
  },
  created() {
    this.$store.dispatch('loadSite')
    this.loadSearchBuilding()
  },
  computed: {
    ...mapState({
      sites: state => state.site,
    }),
    fpType() {
      if (this.$route.query && this.$route.query.type) {
        return this.$route.query.type
      }
      return 1
    },
    filteredSites() {
      // return this.sites.filter(data => {
      //   if (this.floorSearchBuilding) {
      //     if (
      //       data.name
      //         .toLowerCase()
      //         .indexOf(this.floorSearchBuilding.toLowerCase()) > -1
      //     ) {
      //       return data
      //     }
      //     return null
      //   }
      //   return data
      // })
      return this.sites
    },
    filterdBuildingMap() {
      let { siteBuildingMap } = this
      let buildingandSiteMap = {}
      if (siteBuildingMap) {
        Object.keys(siteBuildingMap).forEach(siteId => {
          let buildings = siteBuildingMap[siteId].filter(building => {
            if (this.floorSearchBuilding) {
              if (
                building.name
                  .toLowerCase()
                  .indexOf(this.floorSearchBuilding.toLowerCase()) > -1
              ) {
                return building
              }
              return null
            }
            return building
          })
          if (buildings.length) {
            this.$set(buildingandSiteMap, siteId, buildings)
          }
        })
      }
      return buildingandSiteMap
    },
    filteredfloor() {
      let floors = this.buildingFloorMap[this.selctedBuilding]
      return floors.filter(data => {
        if (this.floorSearch) {
          if (
            data.name.toLowerCase().indexOf(this.floorSearch.toLowerCase()) > -1
          ) {
            return data
          }
          return null
        }
        return data
      })
    },
  },
  methods: {
    changeFloorPlan(floor) {
      if (floor && floor.indoorFloorPlanId) {
        if (floor.indoorFloorPlanId !== this.floorplanId) {
          this.$router.replace({
            params: { floorId: floor.id, floorplanid: floor.indoorFloorPlanId },
          })
        }
      }
      this.afterFloorplanChangeHook(floor)
      this.handleChange()
    },
    selectBuilding(building) {
      this.selctedBuilding = building.id
      this.floorSection = true
      if (!this.buildingFloorMap[building.id]) {
        this.loadFloor(building.id)
      }
    },
    afterFloorplanChangeHook() {
      // after floorplan change hook
    },
    // changeSite(id) {
    //   if (!this.siteBuildingMap[id]) {
    //     this.loadBuilding(id)
    //   }
    // },
    closeFloorSection() {
      this.floorSection = false
    },
    handleChange() {
      this.$emit('update:visibility', false)
    },
    async loadSearchBuilding() {
      this.siteLoading = true
      this.siteBuildingMap = {}
      this.buildingActive = []
      let url = `/v2/building/list?viewName=all`

      if (this.floorSearchBuilding) {
        url = `/v2/building/list?viewName=all&search=${this.floorSearchBuilding}`
      }
      let { data } = await API.get(url)
      if (data && data.buildings && data.buildings.length) {
        data.buildings.forEach(building => {
          if (this.selectBuilding) {
            if (building.id === this.buildingId) {
              this.selectBuilding(building)
            }
          }
          if (building.siteId) {
            this.buildingActive.push(building.siteId)
            if (this.siteBuildingMap[building.siteId]) {
              this.siteBuildingMap[building.siteId].push(building)
            } else {
              this.$set(this.siteBuildingMap, building.siteId, [building])
            }
          }
        })
      }
      this.afterBuildingDataHook()
      this.siteLoading = false
    },
    afterBuildingDataHook() {
      // after building load hook
    },
    async loadBuilding(siteId) {
      this.buildingLoading = true
      let url = `/v2/basespace/getBaseSpaceChildren?baseSpaceId=${siteId}`
      let { data } = await API.get(url)
      if (data) {
        this.$set(this.siteBuildingMap, siteId, data.basespaces)
      }
      this.buildingLoading = false
    },
    async loadFloor(buildignId) {
      this.floorLoading = true
      let filter = {
        building: { operatorId: 36, value: [buildignId + ''] },
        indoorFloorPlanId: { operatorId: 2 },
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
<style lang="scss">
.floor-filter-sec-floor {
  position: absolute;
  left: 333px;
  box-shadow: 1px 0 2px 0 rgb(0 0 0 / 10%) !important;
  top: 0;
}
.floor-filter-con {
  width: 100%;
  height: 100vh;
  z-index: 900;
  position: absolute;
  top: 0;
  left: 61px;

  .floor-filter-sec {
    width: 334px;
    height: 100vh;
    background: #fff;
    // box-shadow: inset 0 1px 3px 0 rgba(0, 0, 0, 0.1);
    box-shadow: none;
    border-right: 1px solid #eee;
  }

  .floor-filter-right {
    box-shadow: -1px 0px 4px 0 rgb(0 0 0 / 10%) !important;
  }

  .floor-filter-header {
    padding: 20px 0 20px 20px;
  }

  .floor-filter-close-ico {
    width: 25px;
    position: absolute;
    right: 0px;
    padding: 3px;
    border: 1px solid #eee;
    background: #fff;
    border-radius: 3px;
    cursor: pointer;
    box-shadow: 0px 0px 1px 0 rgb(0 0 0 / 10%);
    border-top-left-radius: 0;
    border-bottom-left-radius: 0;
    top: 0;

    &:hover {
      background: #39b3c2;
      border: 1px solid #39b3c2;
      border-right: none;

      .el-icon-back {
        color: #fff;
      }
    }

    .el-icon-back {
      font-size: 16px;
      font-weight: 500;
    }
  }

  .floor-filter-search {
    padding-left: 20px;
    padding-right: 20px;

    .el-input__inner {
      padding-left: 30px !important;
    }

    .el-input__prefix {
      left: 5px;

      .el-icon-search {
        font-size: 14px;
        color: #c0c3cc;
        font-weight: bold;
      }
    }
  }

  .floor-filter-building-list {
    height: calc(100vh - 200px);
    overflow-y: scroll;
    padding-bottom: 30px;
    margin-top: 20px;
    padding: 2px 0;

    .el-collapse {
      border-top: none;
      border-bottom: none;

      .el-collapse-item__header {
        font-size: 14px;
        font-weight: bold;
        line-height: normal;
        letter-spacing: 0.86px;
        text-align: left;
        color: #fd3084;
        padding-left: 20px;
        padding-right: 20px;
        border-bottom: none;
      }

      .el-collapse-item__wrap {
        border-bottom: none;
      }

      .el-collapse-item__arrow {
        font-weight: 800;
        transform: rotate(90deg);
        font-size: 16px;
        color: rgb(0 0 0 / 40%);
      }
      .el-collapse-item__arrow.is-active {
        transform: rotate(-90deg);
      }
    }
  }

  .floor-list-bg {
    padding: 10px 20px;
    border-left: 3px solid transparent;
    position: relative;
    cursor: pointer;

    .active {
      background-color: #fcf7f9;
      border-left: 3px solid #fd3084;
    }

    &:hover {
      background-color: #fcf7f9;
      border-left: 3px solid #fd3084;
    }

    &:after {
      content: '';
      width: 0;
      height: 0;
      border-top: 10px solid transparent;
      border-bottom: 10px solid transparent;
      border-right: 10px solid #fff;
      position: absolute;
      right: 0;
      top: 15px;
    }
  }
  .floor-list-bg.active {
    background-color: #fcf7f9;
    border-left: 3px solid #fd3084;
  }
}
</style>
