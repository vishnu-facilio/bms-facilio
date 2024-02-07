<template>
  <div class="indoor-fp-sidebar" @mouseup="handlemouseup()">
    <!-- space section -->
    <div class="mT15">
      <div>
        <!-- header section -->
        <div
          class="flex-middle width100 justify-content-space space-search-block p15 pT0"
        >
          <div class="fc-pink-floorplan flex-middle" v-if="showHidesearchIcon">
            <div>
              Spaces
            </div>
            <!-- <div>
              <img
                src="~assets/add-icon.svg"
                width="14"
                height="14"
                class="pointer fc-space-add-icon"
              />
            </div> -->
          </div>
          <div class="width100 text-right pointer">
            <div @click="clickToShowSearchBox()">
              <i
                class="el-icon-search fc-black-14 fw-bold pointer"
                v-if="showHidesearchIcon"
              ></i>
            </div>
            <el-input
              size="medium"
              v-model="spaceSearch"
              class="width100 fc-input-full-border-h35"
              v-if="showSearchbox"
              @change="fetchSpaces()"
              placeholder="Search"
            >
              <i
                slot="suffix"
                class="el-input__icon pointer fc-black-14 fwBold el-icon-close"
                @click="hideSearchBox"
              ></i>
            </el-input>
          </div>
        </div>
        <!-- body section -->

        <template v-if="spaceLoading">
          <spinner :show="spaceLoading" size="40"></spinner>
        </template>
        <div v-else class="db-user-filter-manager-dialog pL15 pR15">
          <div
            class="fc-black-14 text-left line-height25 break-word"
            v-if="filterdSpaceList.length"
          >
            <div
              class="field-row mB15 cursor-drag"
              v-for="(space, index) in filterdSpaceList"
              :key="index"
              @mousedown="handleMouseDown($event, space)"
            >
              <div class="task-handle mR10 pointer">
                <i class="fa fa-cube" aria-hidden="true"></i>
              </div>
              <div class="fc-black-14 bold text-left pT5">
                {{ space.name }}
              </div>
            </div>
          </div>
          <div class="pB10 text-center" v-else>
            <div
              class="fc-black-12 line-height25 break-word letter-spacing-normal text-left pL15"
            >
              No spaces available to add.
            </div>
          </div>
        </div>
      </div>

      <div>
        <!-- header section -->
        <div
          class="flex-middle width100 justify-content-space space-search-block p15 pT0"
        >
          <div class="fc-pink-floorplan flex-middle" v-if="showHidesearchIcon1">
            <div class="ellipsis">
              Parking spaces
            </div>
            <!-- <div>
              <img
                src="~assets/add-icon.svg"
                width="14"
                height="14"
                class="pointer fc-space-add-icon"
              />
            </div> -->
          </div>
          <div class="width100 text-right pointer">
            <div @click="clickToShowSearchBox()">
              <i
                class="el-icon-search fc-black-14 fw-bold pointer"
                v-if="showHidesearchIcon1"
              ></i>
            </div>
            <el-input
              size="medium"
              v-model="spaceSearch"
              class="width100 fc-input-full-border-h35"
              v-if="showSearchbox1"
              @change="fetchSpaces()"
              placeholder="Search"
            >
              <i
                slot="suffix"
                class="el-input__icon pointer fc-black-14 fwBold el-icon-close"
                @click="hideSearchBox"
              ></i>
            </el-input>
          </div>
        </div>
        <!-- body section -->

        <template v-if="spaceLoading">
          <spinner :show="spaceLoading" size="40"></spinner>
        </template>
        <div v-else class="db-user-filter-manager-dialog pL15 pR15">
          <div
            class="fc-black-14 text-left line-height25 break-word"
            v-if="parkingSpaceList.length"
          >
            <div
              class="field-row mB15 cursor-drag"
              v-for="(space, index) in parkingSpaceList"
              :key="index"
              @mousedown="handleMouseDown($event, space)"
            >
              <div class="task-handle mR10 pointer">
                <i class="fa fa-cube" aria-hidden="true"></i>
              </div>
              <div class="fc-black-14 bold text-left pT5">
                {{ space.name }}
              </div>
            </div>
          </div>
          <div class="pB10 text-center" v-else>
            <div
              class="fc-black-12 line-height25 break-word letter-spacing-normal text-left pL15"
            >
              No parking spaces available to add.
            </div>
          </div>
        </div>
      </div>

      <div>
        <!-- header section -->
        <div
          class="flex-middle width100 justify-content-space space-search-block p15 pT0"
        >
          <div class="fc-pink-floorplan flex-middle" v-if="showHidesearchIcon2">
            <div class="ellipsis">
              Lockers
            </div>
            <!-- <div>
              <img
                src="~assets/add-icon.svg"
                width="14"
                height="14"
                class="pointer fc-space-add-icon"
              />
            </div> -->
          </div>
          <div class="width100 text-right pointer">
            <div @click="clickToShowLockerSearchBox()">
              <i
                class="el-icon-search fc-black-14 fw-bold pointer"
                v-if="showHidesearchIcon2"
              ></i>
            </div>
            <el-input
              size="medium"
              v-model="lockerSearch"
              class="width100 fc-input-full-border-h35"
              v-if="showSearchbox2"
              @change="fetchLockers()"
              placeholder="Search"
            >
              <i
                slot="suffix"
                class="el-input__icon pointer fc-black-14 fwBold el-icon-close"
                @click="hidelocker"
              ></i>
            </el-input>
          </div>
        </div>
        <!-- body section -->

        <template v-if="lockerloading">
          <spinner :show="lockerloading" size="40"></spinner>
        </template>
        <div v-else class="db-user-filter-manager-dialog pL15 pR15">
          <div
            class="fc-black-14 text-left line-height25 break-word"
            v-if="filteredLockerList.length"
          >
            <div
              class="field-row mB15 cursor-drag"
              v-for="(space, index) in filteredLockerList"
              :key="index"
              @mousedown="handleMouseDown($event, space)"
            >
              <div class="task-handle mR10 pointer">
                <i class="fa fa-cube" aria-hidden="true"></i>
              </div>
              <div class="fc-black-14 bold text-left pT5">
                {{ space.name }}
              </div>
            </div>
          </div>
          <div class="pB10 text-center" v-else>
            <div
              class="fc-black-12 line-height25 break-word letter-spacing-normal text-left pL15"
            >
              No lockers available to add.
            </div>
          </div>
        </div>
      </div>

      <!-- <div v-else>
        <div class="emptyIcon text-center">
          <div class="fc-black-24 fwBold pT20 f14 text-center">
            No Spaces
          </div>
        </div>
      </div> -->
    </div>

    <!-- desk section -->

    <div>
      <!-- header section -->
      <div
        class="flex-middle width100 justify-content-space space-search-block p15"
      >
        <div
          class="fc-pink-floorplan flex-middle"
          v-if="showDiskHidesearchIcon"
        >
          <div>
            Desks
          </div>
          <!-- <div @click="upadteMarker({ ...deskmarker, type: 'custom' })">
            <img
              src="~assets/add-icon.svg"
              width="14"
              height="14"
              class="pointer fc-space-add-icon"
            />
          </div> -->
        </div>
        <div class="width100 text-right pointer">
          <div @click="clickToDiskShowSearchBox()">
            <i
              class="el-icon-search fc-black-14 fw-bold"
              v-if="showDiskHidesearchIcon"
            ></i>
          </div>
          <el-input
            size="medium"
            v-model="deskSearch"
            @change="fetchDesks()"
            class="width100 fc-input-full-border-h35"
            v-if="showDiskSearchbox"
            placeholder="Search"
          >
            <i
              slot="suffix"
              class="el-input__icon pointer fc-black-14 fwBold el-icon-close"
              @click="hideDiskSearchBox"
            ></i>
          </el-input>
        </div>
      </div>

      <div
        class="flex p15 f13 pointer bold"
        :style="{ color: '#289cab' }"
        @click="upadteMarker({ ...deskmarker, type: 'custom' })"
      >
        <i class="el-icon-plus" />
        <span class="pL5">New Desk</span>
      </div>
      <!-- body section -->
      <template v-if="deskLoading">
        <spinner :show="deskLoading" size="40"></spinner>
      </template>
      <template v-else>
        <div class="db-user-filter-manager-dialog">
          <div class="fc-black-14 text-left line-height25 break-word ">
            <div
              class="field-row mB10 mT15"
              v-for="(desk, index) in desklist"
              :key="index"
              @mousedown="handleDeskMouseDown($event, desk)"
            >
              <div class="task-handle mR10 pointer mT5">
                <inline-svg
                  src="svgs/office_desk"
                  iconClass="icon text-center icon-sm-md icon-fill-desk"
                ></inline-svg>
              </div>
              <div class="fc-black-14 bold text-left pT5">
                {{ desk.name }}
              </div>
            </div>
          </div>
        </div>
      </template>
    </div>

    <!-- marker section -->

    <div class="width100 p15">
      <div class="fc-pink-floorplan f14 text-left bold ">
        Markers
      </div>
      <div
        class="fc-black-14 text-left line-height25 break-word flex flex-wrap pT15 justify-content-space"
      >
        <div
          v-bind:class="{
            active:
              selectedMarkerType && selectedMarkerType.name === marker.name,
          }"
          class=" marker-conatiner pointer"
          v-for="(marker, index) in defaultMarkerTypes"
          :key="'custom' + index"
          v-tippy
          data-arrow="true"
          :title="marker.name"
        >
          <div
            class="pointer"
            @click="upadteMarker({ ...marker, ...{ type: 'custom' } })"
          >
            <img
              style="width: 20px; height: 20px"
              :src="pathIcon(marker.icon)"
              class="vertical-middle"
            />
          </div>
        </div>
        <div
          v-bind:class="{
            active:
              selectedMarkerType && selectedMarkerType.name === marker.name,
          }"
          class=" marker-conatiner pointer"
          v-for="(marker, index) in filterdMarkertypes"
          :key="index"
        >
          <div
            v-if="marker.name === 'desk'"
            class="pointer"
            @click="upadteMarker({ ...marker, type: 'custom' })"
          >
            <img
              style="width: 20px; height: 20px"
              src="~/statics/desk.png"
              class="vertical-middle"
            />
          </div>
          <el-image
            v-else
            :src="getPreviewUrl(marker.fileId)"
            @click="upadteMarker(marker)"
          >
            <div slot="error" class="image-slot">
              <i class="el-icon-picture-outline"></i>
            </div>
          </el-image>
        </div>
      </div>
      <!-- <div class="mT20 text-center">
        <el-button
          @click="uploadDialog = true"
          class="fc-btn-green-medium-fill"
          :size="'small'"
        >
          Add Custom Marker
        </el-button>
      </div> -->
      <div
        class=" mT0 flex p15 f13 pointer bold pL0"
        :style="{ color: '#289cab' }"
        @click="uploadDialog = true"
        v-if="isNewFloorPlan"
      >
        <i class="el-icon-plus" />
        <span class="pL5">Marker</span>
      </div>
    </div>
    <markerUploader
      @close="close"
      :visibility.sync="uploadDialog"
    ></markerUploader>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import markerUploader from 'pages/indoorFloorPlan/components/FloorPlanMarkerUploader.vue'
import { getBaseURL } from 'util/baseUrl'
export default {
  props: [
    'selectedMarkerType',
    'isDraging',
    'deskDraging',
    'markerMappedDesk',
    'floorId',
    'zoneMapedSpaceId',
  ],
  data() {
    return {
      spaceLoading: false,
      lockerloading: false,
      markerTypes: [],
      uploadDialog: false,
      spaceSearch: null,
      parkingSearch: null,
      deskSearch: null,
      showSearchbox: false,
      showSearchbox1: false,
      showHidesearchIcon: true,
      showHidesearchIcon1: true,
      showHidesearchIcon2: true,
      showSearchbox2: false,
      lockerSearch: null,
      closeSearchBox: false,
      showDiskHidesearchIcon: true,
      showDiskSearchbox: false,
      closeDeskSearchBox: false,
      deskSpaces: [],
      deskLoading: false,
      spaces: [],
      lcokerList: [],
      activeNames: ['space', 'marker', 'markers'],
      spaceCategories: [],
      modulesList: [],
      markerIconMap: {
        Camera: 'Camera.png',
        CCTV: 'CCTV.png',
        Elevator: 'Elevator.png',
        Escalator: 'Escalator.png',
        'Women’s restroom': 'FemaleRestroom.png',
        'Fire extinguisher': 'FireExtingus.png',
        Kitchen: 'Kitchen1.png',
        Microwave: 'Kitchen2.png',
        Locker: 'Locker.png',
        'Men’s restroom': 'MaleRestroom.png',
        'Womens restroom': 'FemaleRestroom.png',
        'Mens restroom': 'MaleRestroom.png',
        Parking: 'Parking.png',
        Restroom: 'Restroom.png',
        Handicap: 'Handicap.png',
        Limited: 'Limited.png',
      },
    }
  },
  computed: {
    parkingSpaceCategory() {
      let parking = this.spaceCategories.find(rt => rt.name === 'Parking Stall')
      return parking || null
    },
    parkingSpaceCategoryId() {
      if (this.parkingSpaceCategory) {
        return this.parkingSpaceCategory.id
      }
      return null
    },
    defaultMarkerTypes() {
      let markers = this.markerTypes.filter(rt => {
        if (rt.fileId === -1 && rt.name !== 'desk') {
          return rt
        }
        return null
      })
      markers.forEach(rt => {
        if (this.markerIconMap[rt.name]) {
          this.$set(rt, 'icon', this.markerIconMap[rt.name])
        }
      })
      return markers
    },
    filterdSpaceList() {
      let spaceIds = this.zoneMapedSpaceId
      return this.spaces.filter(space => {
        if (
          spaceIds.indexOf(space.id) === -1 &&
          space.spaceCategory &&
          space.spaceCategory.id !== this.parkingSpaceCategoryId
        ) {
          return space
        }
        return null
      })
    },
    parkingSpaceList() {
      let spaceIds = this.zoneMapedSpaceId
      return this.spaces.filter(space => {
        if (
          spaceIds.indexOf(space.id) === -1 &&
          space.spaceCategory &&
          space.spaceCategory.id === this.parkingSpaceCategoryId
        ) {
          return space
        }
        return null
      })
    },
    filteredLockerList() {
      let spaceIds = this.zoneMapedSpaceId
      return this.lcokerList.filter(space => {
        if (spaceIds.indexOf(space.id) === -1) {
          return space
        }
        return null
      })
    },
    deskCategoryId() {
      let deskCategory = this.spaceCategories.find(rt => rt.name === 'Desk')
      if (deskCategory) {
        return deskCategory.id
      }
      return null
    },
    filterdMarkertypes() {
      return this.markerTypes.filter(rt => {
        if (rt.name !== 'desk' && rt.fileId !== -1) {
          return rt
        }
        return null
      })
    },
    deskmarker() {
      let find = this.markers.find(rt => rt.name === 'desk')
      return find || null
    },
    desklist() {
      let deskIds = this.markerMappedDesk
      return this.deskSpaces.filter(desk => {
        if (deskIds.indexOf(desk.id) === -1) {
          return desk
        }
        return null
      })
    },
    getParkingModule() {
      if (this.modulesList.length) {
        let parkingModule = this.modulesList.find(
          rt => rt.name === 'parkingstall'
        )
        return parkingModule || null
      }
      return null
    },
    isNewFloorPlan() {
      return this.$helpers.isLicenseEnabled('NEW_FLOORPLAN')
    },
  },
  components: {
    markerUploader,
  },
  created() {
    let promise = [
      this.loadModules(),
      this.getMarkerTypes(),
      this.fetchDesks(),
      this.fetchLockers(),
      this.getSpaceCatgory(),
    ]
    Promise.all(promise).finally(() => {
      this.fetchSpaces()
    })
  },
  methods: {
    async loadModules() {
      let { data } = await API.get('/v3/modules/list/all')

      if (data?.systemModules.length) {
        this.modulesList = data.systemModules
      }
    },
    pathIcon(icon) {
      return require(`statics/floorplan/${icon}`)
    },
    async getSpaceCatgory() {
      let params = {
        page: 1,
        perPage: 200,
        includeParentFilter: true,
        moduleName: 'spacecategory',
      }
      return await API.get(`v3/modules/data/list`, params).then(({ data }) => {
        if (data) {
          this.spaceCategories = data.spacecategory
        }
      })
    },
    async fetchSpaces() {
      this.spaceLoading = true
      let filters = {
        floor: { operatorId: 36, value: [`${this.floorId}`] },
        spaceCategory: { operatorId: 37, value: [`${this.deskCategoryId}`] },
      }
      if (this.spaceSearch) {
        filters['name'] = { operatorId: 5, value: [`${this.spaceSearch}`] }
      }
      let params = {
        moduleName: 'space',
        page: 1,
        perPage: 200,
        includeParentFilter: true,
        filters: JSON.stringify(filters),
      }
      await API.get(`v2/module/data/list`, params).then(({ data }) => {
        if (data) {
          this.spaces = data.moduleDatas
          this.spaces.forEach(space => {
            if (this.isParkingSpace(space) && this.getParkingModule) {
              space.moduleId = this.getParkingModule.id
              this.$set(space, 'moduleId', this.getParkingModule.moduleId)
            }
          })
        }
      })
      this.spaceLoading = false
    },
    async fetchLockers() {
      this.lockerloading = true
      let filters = {
        name: { operatorId: 5, value: [this.lockerSearch] },
      }
      let params = {
        moduleName: 'lockers',
        page: 1,
        perPage: 50,
        includeParentFilter: true,
      }
      if (this.lockerSearch) {
        params = {
          moduleName: 'lockers',
          page: 1,
          perPage: 50,
          includeParentFilter: true,
          filters: JSON.stringify(filters),
        }
      }
      await API.get(`v2/module/data/list`, params).then(({ data }) => {
        if (data) {
          this.lcokerList = data.moduleDatas
        }
      })
      this.lockerloading = false
    },
    async fetchDesks() {
      this.deskLoading = true
      let filters = {
        floor: { operatorId: 36, value: [`${this.floorId}`] },
      }
      let params = {
        moduleName: 'desks',
        page: 1,
        perPage: 200,
        includeParentFilter: true,
        filters: JSON.stringify(filters),
      }
      if (this.deskSearch) {
        filters = {
          name: { operatorId: 5, value: [this.deskSearch] },
          floor: { operatorId: 36, value: [`${this.floorId}`] },
        }
        params = {
          moduleName: 'desks',
          filters: JSON.stringify(filters),
          page: 1,
          perPage: 200,
          includeParentFilter: true,
        }
      }
      await API.get(`v3/modules/data/list`, params).then(({ data }) => {
        if (data) {
          this.deskSpaces = data.desks
        }
      })
      this.deskLoading = false
    },
    handleDeskMouseDown($event, desk) {
      this.$emit('deskDrag', $event, desk)
      this.$emit('update:deskDraging', true)
    },
    isParkingSpace(space) {
      if (
        space?.spaceCategory?.id &&
        space.spaceCategory.id === this.parkingSpaceCategoryId
      ) {
        return true
      }
      return false
    },
    handleMouseDown($event, space) {
      this.$emit('dragElement', $event, space)
      this.$emit('update:isDraging', true)
    },
    handlemouseup() {
      if (this.isDraging) {
        this.$emit('update:isDraging', false)
        this.$emit('closeElement', false)
      }
      if (this.deskDraging) {
        this.$emit('update:deskDraging', false)
        this.$emit('closeElement', false)
      }
    },
    upadteMarker(marker) {
      let data = marker
      if (marker?.fileId > 0) {
        data = { ...marker, ...{ type: 'custom' } }
      }
      console.log('marker type clicked', data)
      this.$emit('selectedMarkerType:update', data)
      this.$emit('markerType', data)
    },
    getPreviewUrl(fileId) {
      return `${getBaseURL()}/v2/files/preview/${fileId}?fetchOriginal=true`
    },
    close() {
      this.getMarkerTypes()
    },
    async getMarkerTypes() {
      let params = {
        moduleName: 'markertype',
      }
      let { data } = await API.get(`v3/modules/data/list`, params)
      if (data) {
        this.markerTypes = data.markertype
        this.markers = data.markertype
      }
    },
    clickToShowSearchBox() {
      ;(this.showSearchbox = true), (this.showHidesearchIcon = false)
      this.showHidesearchIcon1 = false
      this.showSearchbox1 = true
    },
    clickToShowLockerSearchBox() {
      this.showHidesearchIcon2 = false
      this.showSearchbox2 = true
    },
    hidelocker() {
      this.showSearchbox2 = false
      this.showHidesearchIcon2 = true
    },
    hideSearchBox() {
      this.showSearchbox = false
      this.showHidesearchIcon = true
      this.showHidesearchIcon1 = true
      this.showSearchbox1 = false
    },
    clickToDiskShowSearchBox() {
      ;(this.showDiskSearchbox = true), (this.showDiskHidesearchIcon = false)
    },
    hideDiskSearchBox() {
      ;(this.showDiskSearchbox = false), (this.showDiskHidesearchIcon = true)
    },
  },
}
</script>

<style lang="scss">
.indoor-fp-sidebar {
  background: #fff;
  border-right: 1px solid #eee;
  overflow-y: auto;
  padding-bottom: 50px;
}

.dislplay-flex {
  display: flex;
  /* or inline-flex */
  flex-wrap: wrap;
}

.marker-conatiner {
  width: 40px;
  height: 40px;
  border: 2px solid #e6e6e6;
  padding: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  margin-bottom: 10px;
}

.marker-conatiner.active {
  box-shadow: inset 0 0 5px 0 rgb(57 178 194 / 50%);
  border: solid 1px rgba(57, 178, 194, 0.5);
}

.fp-editor-sidebar {
  overflow: auto;
  height: calc(100vh - 150px);
}

.indoor-fp-sidebar {
  width: 250px;
  .el-collapse-item__header {
    padding-left: 15px !important;
    padding-right: 15px !important;
  }

  .el-collapse-item__arrow {
    margin: 0 -15px 0 auto !important;
  }

  .el-collapse-item__arrow {
    padding-left: 15px !important;
    padding-right: 15px !important;
  }

  .db-user-filter-manager-dialog .field-row {
    padding: 7px 15px;
    box-shadow: none;
    border: 0px;
    margin-top: 0;
    margin-bottom: 0;
    cursor: pointer;
    border-bottom: 1px solid rgb(222 231 239 / 50%);
  }
}

.db-user-filter-manager-dialog {
  max-height: 200px;
  overflow-y: scroll;
  padding-left: 0 !important;
  padding-right: 0 !important;
  padding-top: 0;
}

.space-search-block {
  height: 35px;
  padding: 20px 15px;
  margin-top: 10px;
}

.el-image__inner {
  width: 25px;
  height: 25px;
}
.fc-space-add-icon {
  margin-left: 5px;
  margin-top: 5px;
}
.mapboxgl-ctrl-logo {
  display: none !important;
}
.icon-fill-desk #shape,
.icon-fill-desk #Rectangle-path {
  fill: #a9aacb;
}
.fc-pink-floorplan {
  font-size: 11px !important;
  font-weight: 600 !important;
  text-transform: uppercase;
  letter-spacing: 1px !important;
  color: #ff3184;
}
</style>
