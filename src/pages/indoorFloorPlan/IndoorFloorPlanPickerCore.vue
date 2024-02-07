// eslint-disable-next-line vue/no-bare-strings-in-template
<template>
  <div class="h100 flex flex-col">
    <div
      class="fp-picker-header width100 row flex-middle justify-content-space pT0"
    >
      <portal to="indoorFloorPlanPickerSearch">
        <div class="text-right">
          <IndoorFloorPlanPickerSearch
            v-if="!floorplanloading"
            :deskList="deskList"
            :spaceList="spaceList"
            :loading="employeLoading"
            :departments="departments"
            :viewerMode="viewerMode"
            :lockers="lockerList"
            :parkings="parkingList"
            :floorId="floorId"
            @focus="focusMarker"
            @unselectFeature="clearFeatureData"
            @resetZoom="resetZoomLevel"
            :floorplanId="floorplanId"
          >
          </IndoorFloorPlanPickerSearch>
        </div>
      </portal>
    </div>
    <div class="indoor-floorplan-editor height80" id="viewer">
      <div class="indoor-fp-mapbox-section relative">
        <FloorPlanLoading v-if="floorplanloading"></FloorPlanLoading>
        <base-map
          :maxZoom="maxZoom"
          :zoom="zoom"
          :minZoom="minZoom"
          style="width: 100%;"
          @mapLoaded="mapLoaded"
        />
      </div>

      <IndoorFloorPlanMarkerTooltip
        ref="markerTooltip"
        :tooltipData="tooltipData"
      >
      </IndoorFloorPlanMarkerTooltip>
    </div>
  </div>
</template>
<script>
import actionMixin from 'pages/indoorFloorPlan/mixins/ViewerActions.vue'
import viewer from 'pages/indoorFloorPlan/IndoorFloorPlanViewer'
import FloorPlanLoading from 'pages/indoorFloorPlan/components/FloorPlanLoading.vue'
import mapboxgl from 'mapbox-gl'
import IndoorFloorPlanMarkerTooltip from './IndoorFloorPlanMarkerToolipNew'

import IndoorFloorPlanPickerSearch from 'src/pages/indoorFloorPlan/components/IndoorFloorPlanPickerSearch.vue'
export default {
  props: [
    'building',
    'floor',
    'selectedModuleName',
    'selectedFeatureId',
    'id',
    'selectedModuleId',
  ],
  extends: viewer,
  mixins: [actionMixin],
  components: {
    FloorPlanLoading,
    IndoorFloorPlanMarkerTooltip,
    IndoorFloorPlanPickerSearch,
  },
  watch: {
    id: {
      immediate: true,
      handler() {
        // this.init()
      },
    },
  },
  computed: {
    buildingId() {
      if (this.building?.id) {
        return this.building.id
      }
      return null
    },
    floorId() {
      if (this.floor?.id) {
        return this.floor.id
      }
      return null
    },
  },
  data() {
    return {
      zoom: 20,
      propertiesFeatureVisibile: false,
      visibleFilter: false,
      filterDialogOpen: true,
      floorSearchBuilding: '',
      buildingActive: '',
      moveElement: false,
      mouseMouseEnter: false,
      spaceMapDialog: false,
      lockerDialog: false,
      parkingdialog: false,
      selectedZone: {},
      viewerMode: 'ASSIGNMENT',
      zonesModules: ['space', 'lockers', 'parkingstall'],
    }
  },
  methods: {
    async addSelectedMarker(feature) {
      let asyncCalls = [this.onMapLoad]
      await Promise.all(asyncCalls)
      this.$nextTick(() => {
        this.removeYourMarker()
        const mapMarker = new mapboxgl.Marker({ color: '#f53b5d' })
          .setLngLat(feature.geometry.coordinates)
          .addTo(this.map)
        this.$nextTick(() => {
          this.mapMarker = mapMarker
        })
      })
      // this.addYourMarker(feature, 'selectedMarker')
    },
    removeYourMarker() {
      if (this.mapMarker) {
        this.mapMarker.remove()
      }
    },
    removeLayer(layerName) {
      this.map.removeLayer(layerName)
    },
    onFeatureSelect(feature, node) {
      if (node === 'marker') {
        this.addSelectedMarker(feature)
      }
      this.$emit('onFeatureSelect', feature, node)
    },
    resetZoomLevel() {
      if (this.imageBounds && this.map) {
        this.map.fitBounds(this.imageBounds, {
          padding: 100,
        })
      }
    },
    clearFeatureData() {
      // this method used in the search clear
      this.selectedFeature = null
      this.selectedZone = {}
    },
    focusAndActiveMarker(feature) {
      if (this.selectedFeature) {
        this.unSelectFeature(this.selectedFeature)
      }
      if (feature.active) {
        this.selectFeature(feature)
        this.selectedFeature = feature
      }

      this.map.flyTo({
        center: feature.geometry.coordinates,
        zoom: this.maxZoom - 1,
        essential: true,
      })
      this.$nextTick(() => {
        this.onFeatureSelect(feature, 'marker')
      })
    },
    focusAndActivezone(feature) {
      if (this.selectedFeature) {
        this.unSelectFeature(this.selectedFeature)
      }
      if (feature.active) {
        this.selectFeature(feature)
        this.selectedFeature = feature
        this.selectedZone = feature
        this.$nextTick(() => {
          this.onFeatureSelect(feature, 'zone')
        })
      }
      let { coordinates } = feature.geometry
      this.fitBounds(coordinates[0], 100)
    },
    zoneClick(e, f) {
      if (!f.active) return //zone active state set from child comps  , based on IS_RESERVABLE Flag
      e.preventDefault()

      if (this.selectedFeature) {
        this.unSelectFeature(this.selectedFeature)
      }
      this.selectFeature(f)
      this.selectedFeature = f
      this.onFeatureSelect(f, 'zone')

      this.handleZoneClick && this.handleZoneClick(e, f) //fire hook if children have registered
    },
    changeFloor(value) {
      this.$emit('switchFloor', value)
    },
    afterFloorPlanRender() {
      try {
        let { selectedModuleName, selectedFeatureId } = this
        if (selectedModuleName && selectedFeatureId) {
          if (this.zonesModules.includes(selectedModuleName)) {
            this.focusMarker('space', { id: selectedFeatureId })
          } else {
            this.focusMarker(selectedModuleName, { id: selectedFeatureId })
          }
        }
      } catch (e) {
        console.error('error zooming into current employee')
      }
    },
    getZoneFeatureFromRecordId(recordId) {
      let data = this.zones.features.find(rt => {
        if (rt.space?.id && rt.space.id === recordId) {
          return rt
        }
        return null
      })
      return data || null
    },
    getMarkerFeatureFromRecordId(recordId) {
      let data = this.markers.features.find(rt => rt.recordId === recordId)
      return data || null
    },
    updateLockerProperty(feature) {
      let data = feature.moduleData
      if (data) {
        let dataIndex = this.moduleVsData[this.lockerModuleId].findIndex(
          rt => rt.id === data.id
        )
        if (dataIndex > -1) {
          this.moduleVsData[this.lockerModuleId].splice(dataIndex, 1, data)
        }
        this.$set(feature, 'moduleData', data)
        if (data.employee && data.employee.id) {
          //feature.properties.zoneBackgroundColor = 'green'
        } else {
          //feature.properties.zoneBackgroundColor = 'red'
        }
      }
      this.refreshData()
    },
    dropElement() {
      this.isDraging = false
      let div = document.getElementById('dragItem')
      div.style.display = 'none'
      this.dragElement = null
      this.employeSideBar = true
    },
    mousemove(e) {
      if (this.isDraging && this.dragElement) {
        this.ghostElemntHandle(e)
      }
    },
    mouseup(e) {
      this.dropElement(e)
    },
    beforeDestroy() {
      this.map = null
    },
    markerMouseLeave(e, f) {
      if (this.popup) {
        this.popup.remove()
      }
      this.popup = null
      e.preventDefault()
      let { map } = this
      let canvas = map.getCanvasContainer()

      canvas.style.cursor = ''
      if (this.isDraging && f.active) {
        f.properties.markerId = f.properties.normalClass
        // map.setLayoutProperty(id, 'icon-image', `${f.properties.markerId}`)
      }

      if (
        f &&
        f.properties.employeeId &&
        this.moveElement &&
        !this.mouseMouseEnter
      ) {
        map['dragPan'].disable()
        f.properties.markerId = 'desk'
        // map.setLayoutProperty(id, 'icon-image', 'desk')
        if (f.desk && f.desk.employee && f.desk.employee.id) {
          this.mouseDownElement(e, f.desk.employee, f)
        }
        f.properties.employeeId = null
        this.$set(f.properties, 'secondaryLabel', this.getDeskSecondaryLabel(f))
        this.unassignDesk(f)
        this.updateProperty(f)
      }
      this.refreshData()
      this.mouseMouseEnter = false
    },
    closeSpaceDialog() {
      if (this.selectedZone) {
        this.unSelectFeature(this.selectedZone)
      }
      this.spaceMapDialog = false
      this.lockerDialog = false
      this.parkingdialog = false
      this.selectedZone = {}
    },
    handleZoneClick(e, f) {
      e.preventDefault()
      if (f.properties && f.properties.spaceCategory) {
        if (f.properties.spaceCategory === 'Lockers') {
          this.lockerDialog = true
        } else if (f.properties.spaceCategory === 'Parking Stall') {
          this.parkingdialog = true
        } else {
          this.spaceMapDialog = true
        }
      } else {
        this.spaceMapDialog = true
      }
      this.selectedZone = f
    },
    markerMouseEnter(e, f) {
      e.preventDefault()
      let { map } = this
      this.mouseMouseEnter = true
      let canvas = map.getCanvasContainer()
      canvas.style.cursor = 'pointer'

      if (this.isDraging && f.active) {
        this.hoveredMarker = f
        f.properties.markerId = f.properties.hoverClass
        // map.setLayoutProperty(
        //   id,
        //   'icon-image',
        //   `${f.properties.markerId}_hover`
        // )
      } else {
        if (this.popup) {
          this.popup.remove()
          this.popup = null
        }
        this.popup = new mapboxgl.Popup({
          closeButton: false,
          closeOnClick: false,
        })

        this.popup
          //elngLat
          .setLngLat(f.geometry.coordinates)
          .setDOMContent(this.$refs['markerTooltip'].$el)
          .addTo(map)
      }
      this.refreshData()
    },

    zoneMouseEnter(e, f) {
      e.preventDefault()
      let { map } = this
      this.mouseMouseEnter = true
      let canvas = map.getCanvasContainer()
      canvas.style.cursor = 'pointer'

      if (this.popup) {
        this.popup.remove()
        this.popup = null
      }
      this.popup = new mapboxgl.Popup({
        closeButton: false,
        closeOnClick: false,
      })

      this.popup
        //elngLat
        .setLngLat(this.getPolygonCenter([f.geometry.coordinates[0]]))
        .setDOMContent(this.$refs['markerTooltip'].$el)
        .addTo(map)
    },
    zoneMouseLeave(e) {
      if (this.popup) {
        this.popup.remove()
      }
      this.popup = null
      e.preventDefault()
      let { map } = this
      let canvas = map.getCanvasContainer()
      canvas.style.cursor = ''
    },

    // eslint-disable-next-line no-unused-vars
    markerMouseDown(e, f, id) {
      if (!f.active) return

      let { map } = this
      map['dragPan'].disable()
      this.moveElement = true
      this.mouseMouseEnter = false
      this.refreshData()
    },
    markerMouseUp(e, f) {
      if (!f.active) return

      e.preventDefault()
      let { map } = this
      let canvas = map.getCanvasContainer()
      canvas.style.cursor = 'pointer'
      if (this.isDraging && this.dragElement) {
        this.$set(f.properties, 'employeeId', Number(this.dragElement.id))
        let { data } = this.dragElement
        this.$set(f, 'employee', this.dragElement)
        if (data && data.department && data.department.id) {
          this.$set(f.properties, 'departmentId', Number(data.department.id))
          this.$set(f, 'department', data.department)
        } else {
          if (f.properties.departmentId) {
            delete f.properties.departmentId
            delete f.department
          }
        }
        this.updateProperty(f)
        this.updateMove(f)

        this.dropElement()
      }
      this.dragElement = null
      this.moveElement = false
      map['dragPan'].enable()
    },
    handleMarkerClick() {
      this.propertiesFeatureVisibile = true
    },

    // Do not move this to mixin, mixin methods wont be properly overridden from children
    //this methods makes sense to be in children
    setDeskActiveState(feature) {
      if (
        feature?.properties?.markerModuleName &&
        this.selectedModuleName === feature.properties.markerModuleName
      ) {
        if (feature.properties?.deskId && feature.properties?.deskType) {
          let { deskType } = feature.properties
          if (deskType === 1) {
            this.$set(feature, 'active', true)
            this.$set(feature.properties, 'active', true)
          } else {
            this.$set(feature, 'active', false)
            this.$set(feature.properties, 'active', false)
          }
        } else {
          this.$set(feature, 'active', true)
          this.$set(feature.properties, 'active', true)
        }
      } else {
        this.$set(feature, 'active', false)
        this.$set(feature.properties, 'active', false)
      }
      return feature
    },
    setActive(feature) {
      this.$set(feature, 'active', true)
      if (feature?.properties) {
        this.$set(feature.properties, 'active', true)
      }
      return feature
    },
    setZoneActiveState(feature) {
      // used to set the active state
      if (this.selectedModuleName === 'space') {
        this.setActive(feature)
      } else if (
        this.selectedModuleName === 'lockers' &&
        feature?.space?.spaceCategory?.id &&
        feature.space.spaceCategory.id === this.lockerSpaceCatgegoryId
      ) {
        this.setActive(feature)
      } else if (
        this.selectedModuleName === 'parkingstall' &&
        feature?.space?.spaceCategory?.id &&
        feature.space.spaceCategory.id === this.parkingSpaceCategoryId
      ) {
        this.setActive(feature)
      } else {
        this.$set(feature, 'active', false)
        if (feature?.properties) {
          this.$set(feature.properties, 'active', false)
        }
      }
      return feature
    },
  },
}
</script>
<style lang="scss" scopped>
.fp-chooser-new {
  height: 34px;
  margin-top: 5px;
  padding: 5px 10px;
  border: 1px solid #efefef;
  border-radius: 5px;
  cursor: pointer;
  display: flex;
  align-items: center;

  &:hover {
    background: rgb(57 179 194 / 10%);
    transition: 0.6s all;
  }
}

.fc-filter-icon {
  font-size: 20px;
  color: #605e88;
  border-radius: 3px;
  padding: 6px 6px 4px;
  border: 1px solid #efefef;
  cursor: pointer;
}

.fc-actions-floor {
  color: #605e88;
  font-size: 18px;
  margin-left: 10px;
  padding: 6px 6px 6px;
  border-radius: 3px;
  border: 1px solid #efefef;
}

.floor-filter-sec-building {
  float: left;
}

.floor-filter-sec-floor {
  position: absolute;
  left: 333px;
  box-shadow: 1px 0 2px 0 rgb(0 0 0 / 10%) !important;
}
.height80 {
  height: 80% !important;
}
.selelectedValue {
  font-size: 14px;
  font-weight: 700;
  color: rgba(50, 64, 86, 0.87);
  margin-left: 10px;
  border: 1px solid #d0d9e2;
  padding: 10px;
  border-radius: 4px;
  height: 40px;
  position: relative;
  top: 1px;
}
.align-center {
  align-items: center;
}
</style>
