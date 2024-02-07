// eslint-disable-next-line vue/no-bare-strings-in-template
<template>
  <div
    class="indoor-floorplan-editor relative"
    id="viewer"
    @mousemove="mousemove"
    @mouseup="mouseup"
  >
    <div
      id="dragItem"
      style="display: none"
      class="dragElement"
      @mouseup="dropElement"
    ></div>
    <div class="indoor-fp-mapbox-section relative">
      <FloorPlanLoading v-if="floorplanloading"></FloorPlanLoading>
      <template v-if="showAssignmentsidebar">
        <IndoorFloorplanSearch
          v-if="!isNewSetting"
          @dragElement="mouseDownElement"
          :deskList="deskList"
          :spaceList="spaceList"
          :loading="employeLoading"
          :departments="departments"
          :viewerMode="viewerMode"
          :lockers="lockerList"
          :parkings="parkingList"
          :floorId="floorId"
          :floorplanId="floorplanId"
          @focus="focusMarker"
        ></IndoorFloorplanSearch>
        <portal to="ASSIGNMENT_SIDEBAR" v-else>
          <IndoorFloorplanSearch3
            @dragElement="mouseDownElement"
            :deskList="deskList"
            :spaceList="spaceList"
            :loading="employeLoading"
            :departments="departments"
            :viewerMode="viewerMode"
            :lockers="lockerList"
            :parkings="parkingList"
            :floorId="floorId"
            :floorplanId="floorplanId"
            @focus="focusMarker"
          ></IndoorFloorplanSearch3>
        </portal>
      </template>

      <base-map
        :maxZoom="maxZoom"
        :zoom="zoom"
        :minZoom="minZoom"
        style="width: 100%;"
        @mapLoaded="mapLoaded"
      />
      <!-- <ViewerPropertyDialog
        v-if="propertiesFeatureVisibile"
        :properties="selectedFeature.properties"
        :selectedFeature="selectedFeature"
        :visible.sync="propertiesFeatureVisibile"
        :employeeList="employeeList"
        :departments="departments"
        :defaultMarkerIdVsName="defaultMarkerIdVsName"
        @update="updateData"
        @updatemarker="updateMarkerData"
        @move="moveData"
        @unassignDesk="unassignDeskfromProperty"
        @updatelocker="updateLocker"
        @close="closePropertyDialog"
        :building="building"
        :floor="floor"
        :isPortal="isPortal"
      ></ViewerPropertyDialog> -->
      <ViewerPropertyDialogWrapper
        v-if="propertiesFeatureVisibile && showAssignOption"
        :objectId="Number(selectedFeature.objectId)"
        :visible.sync="propertiesFeatureVisibile"
        :isPortal="isPortal"
        :siteId="siteId"
        @refresh="loadViewerData"
        @close="closePropertyDialog"
      ></ViewerPropertyDialogWrapper>
      <AssignmentViewSpaceDialog
        v-if="spaceMapDialog"
        :visible.sync="spaceMapDialog"
        :isPortal="isPortal"
        :siteId="siteId"
        :objectId="Number(selectedFeature.objectId)"
        @refresh="loadViewerData"
        @close="closeSpaceDialog"
      ></AssignmentViewSpaceDialog>
      <LockerViewerPropertydialog
        v-if="lockerDialog"
        :visible.sync="lockerDialog"
        @close="closeSpaceDialog"
        :isPortal="isPortal"
        :siteId="siteId"
        @refresh="loadViewerData"
        :objectId="Number(selectedFeature.objectId)"
        @updateLocker="updateLockerProperty"
      >
      </LockerViewerPropertydialog>
      <ParkingViewerPropertydialog
        v-if="parkingdialog"
        :visible.sync="parkingdialog"
        :selectedZone="selectedZone"
        @close="closeSpaceDialog"
        :isPortal="isPortal"
        :siteId="siteId"
        @refresh="loadViewerData"
        :objectId="Number(selectedFeature.objectId)"
      >
      </ParkingViewerPropertydialog>
      <FloorplanTypeSwitcher
        v-if="!floorplantypeSwicthLoading"
        :floorplanTypes="floorplanTypes"
        :indoorFloorplanFields="indoorFloorplanFields"
        :viewerMode="viewerMode"
      ></FloorplanTypeSwitcher>
    </div>
    <AssignmentViewLeagends
      :deskList="deskList"
      :spaceList="spaceList"
      :departments="departments"
      :lockers="lockerList"
      :parkingList="parkingList"
      :settings="settings"
    ></AssignmentViewLeagends>
    <IndoorFloorPlanMarkerTooltip
      ref="markerTooltip"
      :tooltipData="tooltipData"
    >
    </IndoorFloorPlanMarkerTooltip>
    <FloorplanSettings
      v-if="!floorplanloading && !isNewSetting"
      @handleSettings="handleSettings"
      @applySettings="applySettings"
      :settingsJson="settings"
      :viewerMode="viewerMode"
      :isPortal="isPortal"
      :parkingList="parkingList"
    ></FloorplanSettings>
    <portal to="floorplan-new-search-container">
      <IndoorFloorplanSearch2
        v-if="showViewOption"
        @dragElement="mouseDownElement"
        :deskList="deskList"
        :spaceList="spaceList"
        :loading="employeLoading"
        :departments="departments"
        :viewerMode="viewerMode"
        :lockers="lockerList"
        :parkings="parkingList"
        :floorId="floorId"
        :floorplanId="floorplanId"
        @focus="focusMarker"
      ></IndoorFloorplanSearch2>
    </portal>
    <NewFloorplanSettings
      v-if="!floorplanloading && isNewSetting"
      @handleSettings="handleSettings"
      @applySettings="applySettings"
      :settingsJson="settings"
      :viewerMode="viewerMode"
      :isPortal="isPortal"
      :parkingList="parkingList"
    ></NewFloorplanSettings>
  </div>
</template>
<script>
import actionMixin from 'pages/indoorFloorPlan/mixins/ViewerActions.vue'
import IndoorFloorplanSearch from 'pages/indoorFloorPlan/components/IndoorFloorplanSearch.vue'
import IndoorFloorplanSearch3 from 'pages/indoorFloorPlan/components/IndoorFloorplanSearch3.vue'

import IndoorFloorplanSearch2 from 'pages/indoorFloorPlan/components/IndoorFloorplanSearch2.vue'

import ViewerPropertyDialogWrapper from 'pages/indoorFloorPlan/components/ViewerPropertyDialogWrapper'
import viewer from 'pages/indoorFloorPlan/IndoorFloorPlanViewer'
import AssignmentViewSpaceDialog from 'pages/indoorFloorPlan/components/AssignmentViewSpaceDialog.vue'
import LockerViewerPropertydialog from 'pages/indoorFloorPlan/components/LockerViewerPropertydialog.vue'
import ParkingViewerPropertydialog from 'pages/indoorFloorPlan/components/ParkingViewerPropertydialog.vue'
import AssignmentViewLeagends from 'pages/indoorFloorPlan/components/AssignmentViewLeagends.vue'
import FloorplanTypeSwitcher from 'pages/indoorFloorPlan/components/FloorplanTypeSwitcher.vue'
import FloorPlanLoading from 'pages/indoorFloorPlan/components/FloorPlanLoading.vue'
import mapboxgl from 'mapbox-gl'
import IndoorFloorPlanMarkerTooltip from './IndoorFloorPlanMarkerToolipNew'
import FloorplanSettings from 'src/pages/indoorFloorPlan/components/FloorplanSettings.vue'
import NewFloorplanSettings from 'src/pages/indoorFloorPlan/components/NewFloorPlanSettings.vue'
import { mapState, mapGetters } from 'vuex'

// import { API } from '@facilio/api'
export default {
  props: ['building', 'floor'],
  extends: viewer,
  mixins: [actionMixin],
  components: {
    IndoorFloorplanSearch,
    IndoorFloorplanSearch2,
    AssignmentViewSpaceDialog,
    AssignmentViewLeagends,
    FloorPlanLoading,
    IndoorFloorPlanMarkerTooltip,
    LockerViewerPropertydialog,
    ParkingViewerPropertydialog,
    FloorplanTypeSwitcher,
    ViewerPropertyDialogWrapper,
    FloorplanSettings,
    IndoorFloorplanSearch3,
    NewFloorplanSettings,
  },
  mounted() {},
  beforeDestroy() {
    //Avoid drag element remaining in DOM after component has been destroyed
    let dragEle = document.querySelector('#dragItem')
    if (dragEle) {
      document.body.removeChild(dragEle)
    }
  },

  data() {
    return {
      zoom: 20,
      propertiesFeatureVisibile: false,
      visibleFilter: false,
      filterDialogOpen: false,
      floorSearchBuilding: '',
      buildingActive: '',
      moveElement: false,
      mouseMouseEnter: false,
      spaceMapDialog: false,
      lockerDialog: false,
      parkingdialog: false,
      selectedZone: {},
      viewerMode: 'ASSIGNMENT',
    }
  },
  computed: {
    ...mapState('webtabs', {
      currentTab: state => state.selectedTab,
    }),
    ...mapGetters('webtabs', ['tabHasPermission']),
    hasViewPermission() {
      let { currentTab, tabHasPermission } = this
      return tabHasPermission('VIEW', currentTab)
    },
    hasAssignPermission() {
      let { currentTab, tabHasPermission } = this
      return tabHasPermission('ASSIGN', currentTab)
    },
    settings() {
      return this.customization ? this.customization : null
    },
    showAssignmentsidebar() {
      let showAssignment = true
      return this.showAssignOption && showAssignment
    },
    showAssignOption() {
      if (this.hasViewPermission) {
        if (this.hasAssignPermission) {
          return true
        }
      }
      return false
    },
    showViewOption() {
      if (this.hasViewPermission) {
        return true
      }
      return false
    },
    isNewSetting() {
      return this.$helpers.isLicenseEnabled('NEW_FLOORPLAN')
    },
  },

  methods: {
    async handleSettings(settings) {
      this.applySettings(settings)
      this.patchUpdateFloorplan(settings)
    },
    afterFloorplanLoadHook() {
      // console.log('after floorplan hook called')
      this.applySettings(this.settings)
    },
    applySettings(settings) {
      if (settings) {
        let {
          spacePrimaryLabel,
          deskPrimaryLabel,
          deskSecondaryLabel,
          spaceBookingState,
          spaceSecondaryLabel,
          compactView,
          textHalo,
          allowTextOverlap,
          imageOpacity,
        } = settings
        let compactview = compactView
        let texthalo = textHalo
        let textoverlap = allowTextOverlap
        let deskfontColor = deskPrimaryLabel.color
        let deskNameColor = deskSecondaryLabel.color
        let spacePrimaryColor = spacePrimaryLabel.color
        let spaceSecondaryColor = spaceSecondaryLabel.color
        let spaceColor = spaceBookingState.nonReservableColor
        let deskPrimarySize = deskPrimaryLabel.fontSize
        let deskSecondarySize = deskSecondaryLabel.fontSize
        let spacePrimarySize = spacePrimaryLabel.fontSize
        let spaceSecondarySize = spaceSecondaryLabel.fontSize
        let alpha = spaceColor.replace(/^.*,(.+)\)/, '$1')
        if (alpha === '#000000') {
          alpha = 0.3
        }
        let opacity = Number(alpha ? alpha : 0.3)
        if (!deskfontColor) {
          deskfontColor = 'black'
        }
        if (compactview) {
          textoverlap = true
        }
        let deskSecondaryText = 'secondary-label-marker'
        let spaceSecondaryText = 'space-secondary-text-zone'
        let spacePrimaryText = 'space-text-zone'
        let deskCenterLabel = 'formated-secondary-label-marker'
        let deskLabel = 'marker'
        if (this.isLayerAvailable(deskSecondaryText)) {
          this.map.setPaintProperty(
            deskSecondaryText,
            'text-color',
            deskfontColor
          )
        }

        if (this.isLayerAvailable(deskLabel)) {
          this.map.setPaintProperty(deskLabel, 'text-color', deskNameColor)
        }
        if (this.isLayerAvailable(spaceSecondaryText)) {
          this.map.setPaintProperty(
            spaceSecondaryText,
            'text-color',
            spaceSecondaryColor
          )
        }
        if (this.isLayerAvailable(spacePrimaryText)) {
          this.map.setPaintProperty(
            spacePrimaryText,
            'text-color',
            spacePrimaryColor
          )
        }

        // this.map.setPaintProperty('zone', 'fill-extrusion-opacity', opacity)
        // this.map.setPaintProperty('zone', 'fill-extrusion-color', spaceColor)
        this.map.setPaintProperty('ImageLayer', 'raster-opacity', imageOpacity)
        if (compactview) {
          this.map.setLayoutProperty(deskSecondaryText, 'text-size', [
            'interpolate',
            ['linear'],
            ['zoom'],
            this.minZoom,
            0,
            this.maxZoom,
            this.fontSize + 1,
          ])
          this.map.setLayoutProperty(deskLabel, 'text-size', [
            'interpolate',
            ['linear'],
            ['zoom'],
            this.minZoom,
            0,
            this.maxZoom,
            this.fontSize,
          ])
          this.map.setLayoutProperty(spacePrimaryText, 'text-size', [
            'interpolate',
            ['linear'],
            ['zoom'],
            this.minZoom,
            0,
            this.maxZoom,
            this.fontSize + 1,
          ])
          this.map.setLayoutProperty(spaceSecondaryText, 'text-size', [
            'interpolate',
            ['linear'],
            ['zoom'],
            this.minZoom,
            0,
            this.maxZoom,
            this.fontSize + 1,
          ])
        } else {
          this.map.setLayoutProperty(deskSecondaryText, 'text-size', [
            'interpolate',
            ['linear'],
            ['zoom'],
            17,
            2,
            20,
            deskPrimarySize,
          ])
          this.map.setLayoutProperty(deskLabel, 'text-size', [
            'interpolate',
            ['linear'],
            ['zoom'],
            17,
            0,
            20,
            deskSecondarySize - 2,
          ])
          this.map.setLayoutProperty(spacePrimaryText, 'text-size', [
            'interpolate',
            ['linear'],
            ['zoom'],
            17,
            2,
            20,
            spacePrimarySize,
          ])
          this.map.setLayoutProperty(spaceSecondaryText, 'text-size', [
            'interpolate',
            ['linear'],
            ['zoom'],
            17,
            2,
            20,
            spaceSecondarySize,
          ])
        }

        if (texthalo) {
          this.map.setPaintProperty(deskCenterLabel, 'text-halo-width', 1)
          this.map.setPaintProperty(spacePrimaryText, 'text-halo-width', 1)
          this.map.setPaintProperty(spaceSecondaryText, 'text-halo-width', 1)
          this.map.setPaintProperty(deskSecondaryText, 'text-halo-width', 0.2)
        } else {
          this.map.setPaintProperty(deskCenterLabel, 'text-halo-width', 0)
          this.map.setPaintProperty(spacePrimaryText, 'text-halo-width', 0)
          this.map.setPaintProperty(spaceSecondaryText, 'text-halo-width', 0)
          this.map.setPaintProperty(deskSecondaryText, 'text-halo-width', 0)
        }
        if (textoverlap) {
          this.map.setLayoutProperty(
            deskSecondaryText,
            'text-allow-overlap',
            true
          )
          this.map.setLayoutProperty(deskLabel, 'text-allow-overlap', true)
          this.map.setLayoutProperty(
            spacePrimaryText,
            'text-allow-overlap',
            true
          )
          this.map.setLayoutProperty(
            spaceSecondaryText,
            'text-allow-overlap',
            true
          )
        } else {
          this.map.setLayoutProperty(
            deskSecondaryText,
            'text-allow-overlap',
            false
          )
          this.map.setLayoutProperty(deskLabel, 'text-allow-overlap', false)
          this.map.setLayoutProperty(
            spacePrimaryText,
            'text-allow-overlap',
            false
          )
          this.map.setLayoutProperty(
            spaceSecondaryText,
            'text-allow-overlap',
            false
          )
        }
      }
    },
    async patchUpdateFloorplan(settings) {
      let customization = settings
      await this.patchUpdateRecord(customization)
      await this.loadViewerData()
      this.$message.success('Updated!')
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
          feature.properties.zoneBackgroundColor = 'green'
        } else {
          feature.properties.zoneBackgroundColor = 'red'
        }
      }
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
      this.resetDialogs()
      e.preventDefault()
      if (f?.properties?.spaceCategory) {
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

      if (f?.geometry) {
        this.popup
          //elngLat
          .setLngLat(this.getPolygonCenter([f.geometry.coordinates[0]]))
          .setDOMContent(this.$refs['markerTooltip'].$el)
          .addTo(map)
      }
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
        this.$set(f.properties, 'secondaryLabel', this.dragElement.name)
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
    specialHandelCheck(feature) {
      if (feature?.properties?.openDialog) {
        this.resetDialogs()
        this.specialHandelforMarkerClick(feature)
      } else {
        this.propertiesFeatureVisibile = false
      }

      let { properties } = feature
      if (properties?.deskType > 0) {
        return true
      }
      return false
    },
    resetDialogs() {
      this.spaceMapDialog = false
      this.propertiesFeatureVisibile = false
    },
    specialHandelforMarkerClick({ markerType, properties }) {
      // need to remove this method
      if (
        markerType?.fileId > 0 &&
        markerType?.recordModuleId > 0 &&
        properties?.recordId
      ) {
        this.redirectToSummary(markerType.recordModuleId, properties.recordId)
      } else {
        this.propertiesFeatureVisibile = true
      }
    },
    handleMarkerClick() {
      // if (feature?.properties?.openDialog) {
      //   this.resetDialogs()
      //   this.specialHandelforMarkerClick(feature)
      // } else {
      //   this.propertiesFeatureVisibile = false
      // }
    },

    // Do not move this to mixin, mixin methods wont be properly overridden from children
    //this methods makes sense to be in children
    setDeskActiveState(feature) {
      // used to set desk active state
      return feature
    },
  },
}
</script>
<style lang="scss">
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
</style>
