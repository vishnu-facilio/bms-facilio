<template>
  <div class="indoor-floorplan-bookings-view height100" id="viewer">
    <div class="indoor-fp-mapbox-section relative">
      <FloorPlanLoading v-if="floorplanloading"></FloorPlanLoading>
      <base-map
        :maxZoom="22"
        :zoom="20"
        :minZoom="10"
        class="width100"
        @mapLoaded="mapLoaded"
      />
      <FloorplanTypeSwitcher
        v-if="!floorplantypeSwicthLoading && showEditLayout"
        :floorplanTypes="floorplanTypes"
        :indoorFloorplanFields="indoorFloorplanFields"
        :viewerMode="viewerMode"
      ></FloorplanTypeSwitcher>
    </div>
    <div
      class="formbuilder-fullscreen-popup floor-plan-builder height100"
      v-if="fpEditor"
    >
      <FloorPlanEditor
        @close="closeEditor"
        :id="floorplanId"
        :visibile.sync="fpEditor"
      ></FloorPlanEditor>
    </div>
    <FloorPlanBookingsForm
      v-if="showBookingsForm"
      :moduleName="bookingModuleName"
      :formData.sync="selectedForm"
      :recordData="recordData"
      @closeForm="closeForm"
      @saved="savedForm"
    ></FloorPlanBookingsForm>
    <IndoorFloorplanSearch
      v-if="!floorplanloading && !newBooking"
      :loading="employeLoading"
      :departments="departments"
      :floorId="floorId"
      viewerMode="BOOKING"
      @focus="focusMarker"
      :lockers="lockerList"
      :parkings="parkingList"
      :floorplanId="floorplanId"
    ></IndoorFloorplanSearch>

    <portal to="indoorFloorPlanLayout">
      <FloorMapBookingTimeRange
        v-if="showBooking"
        v-model="timeRange"
        @change="timeRangeChanged"
        :customTime="customTime"
      ></FloorMapBookingTimeRange>
      <BookingTimeRange
        v-else
        v-model="timeRange"
        @change="timeRangeChanged"
        :customTime="customTime"
      >
      </BookingTimeRange>
    </portal>
    <IndoorFloorPlanMarkerTooltip
      ref="markerTooltip"
      class="marker-tooltip"
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
    ></FloorplanSettings>
    <NewFloorplanSettings
      v-if="!floorplanloading && isNewSetting"
      @handleSettings="handleSettings"
      @applySettings="applySettings"
      :settingsJson="settings"
      :viewerMode="viewerMode"
      :isPortal="isPortal"
      :parkingList="parkingList"
    ></NewFloorplanSettings>
    <template v-if="newBooking">
      <SpaceBookingProperty
        :visibility.sync="showBookingsPanel"
        v-if="showBookingsPanel && showBookingOption"
        :objectId="Number(selectedFeature.objectId)"
        :currentDate="currentDate"
        :timeRange="timeRange"
        :viewerMode="viewerMode"
        @book="handleBookBtn"
      >
      </SpaceBookingProperty>
    </template>
    <template v-else>
      <BookingProperty
        :visibility.sync="showBookingsPanel"
        v-if="showBookingsPanel && showBookingOption"
        :objectId="Number(selectedFeature.objectId)"
        :currentDate="currentDate"
        :timeRange="timeRange"
        :viewerMode="viewerMode"
        @book="handleBookBtn"
      >
      </BookingProperty>
    </template>

    <BookingViewLeagends
      :deskList="deskList"
      :spaceList="spaceList"
      :departments="departments"
      :bookings="bookings"
      :isPortal="isPortal"
      :lockers="lockerList"
      :parkingList="parkingList"
      :settings="settings"
    ></BookingViewLeagends>

    <portal to="floorplan-new-search-container">
      <!-- <IndoorFloorplanNewSearch
        v-if="!floorplanloading"
        :deskList="deskList"
        :spaceList="spaceList"
        :loading="employeLoading"
        :departments="departments"
        :floorId="floorId"
        viewerMode="BOOKING"
        @focus="focusMarker"
        :lockers="lockerList"
        :parkings="parkingList"
        :floorplanId="floorplanId"
      ></IndoorFloorplanNewSearch> -->
    </portal>
  </div>
</template>
<script>
import actionMixin from 'pages/indoorFloorPlan/mixins/ViewerActions.vue'
import FloorPlanBookingsForm from './FloorPlanBookingsForm'
import BookingTimeRange from './BookingTimeRange'
import viewer from 'pages/indoorFloorPlan/IndoorFloorPlanViewer'
import mapboxgl from 'mapbox-gl'
import IndoorFloorPlanMarkerTooltip from './IndoorFloorPlanMarkerToolipNew'
import IndoorFloorplanSearch from 'pages/indoorFloorPlan/components/IndoorFloorplanSearch.vue'
import IndoorFloorplanNewSearch from 'pages/indoorFloorPlan/components/IndoorFloorplanSearch2.vue'
import moment from 'moment'
import BookingViewLeagends from 'src/pages/indoorFloorPlan/components/BookingLeagendSelect.vue'
import FloorPlanLoading from 'pages/indoorFloorPlan/components/FloorPlanLoading.vue'
import FloorplanTypeSwitcher from 'pages/indoorFloorPlan/components/FloorplanTypeSwitcher.vue'
import FloorplanSettings from 'src/pages/indoorFloorPlan/components/FloorplanSettings.vue'
import { mapState, mapGetters } from 'vuex'
import { API } from '@facilio/api'
import FloorMapBookingTimeRange from 'src/pages/indoorFloorPlan/FloorMapBookingTimeRange.vue'
import NewFloorplanSettings from 'src/pages/indoorFloorPlan/components/NewFloorPlanSettings.vue'
import { getApp } from '@facilio/router'
import { eventBus } from 'src/components/page/widget/utils/eventBus.js'
import Vue from 'vue'
import momenttz from 'moment-timezone'

export default {
  extends: viewer,
  mixins: [actionMixin],
  components: {
    FloorMapBookingTimeRange,
    BookingTimeRange,
    FloorPlanBookingsForm,
    IndoorFloorPlanMarkerTooltip,
    IndoorFloorplanSearch,
    IndoorFloorplanNewSearch,
    BookingViewLeagends,
    FloorPlanLoading,
    FloorplanTypeSwitcher,
    FloorplanSettings,
    NewFloorplanSettings,
    BookingProperty: () =>
      import('pages/indoorFloorPlan/components/FloorPlanBookingPropertyNew'),
    SpaceBookingProperty: () =>
      import('pages/indoorFloorPlan/components/FloorPlanSpaceBookingProperty'),
    FloorPlanEditor: () =>
      import('pages/indoorFloorPlan/IndoorFloorPlanEditor'),
  },
  props: ['customTime'],
  // mounted() {
  //   console.log('CT: ', this.cutsomTime)
  //   console.log('TR: ', this.timeRange)
  // },
  data() {
    return {
      viewerMode: 'BOOKING',
      timeRange: {
        startTime: null,
        endTime: null,
      },
      floorDetails: null,

      //form prefils
      showBookingsForm: false,
      selectedForm: null,
      recordData: null,
      showBookingsPanel: false,
    }
  },
  watch: {
    querryFeatureId: {
      async handler() {
        if (this.querryFeatureId) {
          this.focusMarkerFromFeature(Number(this.querryFeatureId))
        }
      },
      immediate: true,
    },
  },
  async created() {
    // this.getViewerApi()
    await this.initializeTimeRange()
  },
  mounted() {
    this.loadBookingForms()
    eventBus.$on('focusMarker', (modulesName, data) => {
      this.focusMarker(modulesName, data)
    })
  },

  computed: {
    ...mapState('webtabs', {
      currentTab: state => state.selectedTab,
    }),
    ...mapGetters('webtabs', ['tabHasPermission']),
    hasEditPermission() {
      let { currentTab, tabHasPermission } = this
      return tabHasPermission('EDIT', currentTab)
    },
    hasViewAssignmentPermission() {
      let { currentTab, tabHasPermission } = this
      return tabHasPermission('VIEW_ASSIGNMENT', currentTab)
    },

    hasAssignmentDepartmentPermission() {
      let { currentTab, tabHasPermission } = this
      return tabHasPermission('VIEW_ASSIGNMENT_DEPARTMENT', currentTab)
    },
    hasAssignmentOwnPermission() {
      let { currentTab, tabHasPermission } = this
      return tabHasPermission('VIEW_ASSIGNMENT_OWN', currentTab)
    },
    showEditLayout() {
      if (this.hasViewPermission) {
        if (
          this.hasEditPermission ||
          this.hasViewAssignmentPermission ||
          this.hasAssignmentDepartmentPermission ||
          this.hasAssignmentOwnPermission
        ) {
          return true
        }
      }
      return false
    },
    recordIdVsFeature() {
      let data = {}
      if (this.zones?.features) {
        this.zones.features.forEach(zone => {
          if (zone?.properties?.recordId) {
            this.$set(data, zone.properties.recordId, zone)
          }
        })
      }
      if (this.markers?.features) {
        this.markers.features.forEach(marker => {
          if (marker?.properties?.recordId) {
            this.$set(data, marker.properties.recordId, marker)
          }
        })
      }
      return data
    },
    bookingModuleName() {
      return this.newBooking ? 'spacebooking' : 'facilitybooking'
    },
    querryFeatureId() {
      return this.$route.query.featureId || null
    },
    querryFeatureRecordId() {
      return this.$route.query.featureRecordId || null
    },
    hasBookingPermission() {
      let { currentTab, tabHasPermission } = this
      return tabHasPermission('BOOKING', currentTab)
    },
    currentDate() {
      if (!this.timeRange?.startTime) {
        return null
      } else {
        return this.$helpers
          .getOrgMoment(this.timeRange.startTime)
          .format('DD-MM-YYYY')
      }
    },
    building() {
      let { floorDetails } = this
      if (floorDetails && floorDetails.building) {
        return floorDetails.building
      }
      return null
    },
    floor() {
      return this.floorDetails || null
    },
    settings() {
      return this.customizationBooking ? this.customizationBooking : null
    },
    hasViewPermission() {
      let { currentTab, tabHasPermission } = this
      return tabHasPermission('VIEW', currentTab)
    },
    showBookingOption() {
      if (this.hasViewPermission) {
        if (this.hasBookingPermission) {
          return true
        }
      }
      return false
    },
    showBooking() {
      let linkname = getApp().linkName
      if (linkname === 'employee') {
        return true
      } else {
        return false
      }
    },
    isNewSetting() {
      return this.$helpers.isLicenseEnabled('NEW_FLOORPLAN')
    },
  },

  methods: {
    setQuerrData() {
      if (
        this.querryFeatureRecordId &&
        this.recordIdVsFeature[this.querryFeatureRecordId]
      ) {
        let feature = this.recordIdVsFeature[this.querryFeatureRecordId]
        if (feature) {
          this.setHoverEffect(feature)
          this.focusAndActiveMarker(feature)
        }
      }
    },
    async handleSettings(settings) {
      this.applySettings(settings)
      this.patchUpdateFloorplan(settings)
    },
    afterFloorplanLoadHook() {
      // console.log('after floorplan hook called')
      this.applySettings(this.settings)
      if (this.querryDeskId) {
        let data = { id: Number(this.querryDeskId) }
        this.focusMarker('desks', data)
      }
    },
    applySettings(settings) {
      if (settings) {
        let {
          spacePrimaryLabel,
          deskPrimaryLabel,
          deskSecondaryLabel,
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
        let deskPrimarySize = deskPrimaryLabel.fontSize
        let deskSecondarySize = deskSecondaryLabel.fontSize
        let spacePrimarySize = spacePrimaryLabel.fontSize
        let spaceSecondarySize = spaceSecondaryLabel.fontSize
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
        if (this.isLayerAvailable(deskLabel)) {
          this.map.setPaintProperty(deskLabel, 'text-color', deskNameColor)
        }

        if (this.isLayerAvailable('ImageLayer')) {
          this.map.setPaintProperty(
            'ImageLayer',
            'raster-opacity',
            imageOpacity
          )
        }

        if (compactview) {
          if (this.isLayerAvailable(deskSecondaryText)) {
            this.map.setLayoutProperty(deskSecondaryText, 'text-size', [
              'interpolate',
              ['linear'],
              ['zoom'],
              this.minZoom,
              0,
              this.maxZoom,
              this.fontSize + 1,
            ])
          }

          this.map.setLayoutProperty(deskLabel, 'text-size', [
            'interpolate',
            ['linear'],
            ['zoom'],
            this.minZoom,
            0,
            this.maxZoom,
            this.fontSize,
          ])
          if (this.isLayerAvailable(spacePrimaryText)) {
            this.map.setLayoutProperty(spacePrimaryText, 'text-size', [
              'interpolate',
              ['linear'],
              ['zoom'],
              this.minZoom,
              0,
              this.maxZoom,
              this.fontSize + 1,
            ])
          }

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
          if (this.isLayerAvailable(spacePrimaryText)) {
            this.map.setLayoutProperty(spacePrimaryText, 'text-size', [
              'interpolate',
              ['linear'],
              ['zoom'],
              17,
              2,
              20,
              spacePrimarySize,
            ])
          }

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
      this.removeImage()
      this.generateIcons()
    },
    async patchUpdateFloorplan(settings) {
      let customizationBooking = settings
      await this.patchUpdateBookingRecord(customizationBooking)
      await this.loadViewerData()
      this.removeImage()
      this.generateIcons()
      this.refreshData()
      this.$message.success('Updated!')
    },
    async loadViewerData() {
      this.floorplanloading = true
      let { data, error } = await this.getViewerApi()
      if (error) {
        this.$message.error('error loading bookings for floor plan')
      } else {
        if (data?.facility) {
          this.facilities = data.facility
        }
        if (data?.facilitybooking) {
          this.bookings = data.facilitybooking
        }
      }

      this.floorplanloading = false
      this.deSerializeGeoJson()
      this.refreshData()
    },
    loadBookingData() {
      this.$message('updating....')
      this.getViewerApi().then(() => {
        this.deSerializeGeoJson()
        this.refreshData()
        this.$message.close()
      })
    },
    timeRangeChanged() {
      this.floorplanloading = true
      this.loadViewerData()
      // this.loadBookingsAndFacilities().then(() => {
      //   this.floorplanloading = false
      // })
    },
    refreshDeskMarkers() {
      //regenerate iconname(based on current availabilty status etc) and update desk markers
      this.markers.features.forEach(feature => {
        if (feature.desk) {
          this.map.setLayoutProperty(
            feature.id,
            'icon-image',
            this.getIconName(feature.desk)
          )
        }
      })
    },
    markerMouseEnter(e, f, id) {
      e.preventDefault()
      let { map } = this
      this.mouseMouseEnter = true
      let canvas = map.getCanvasContainer()
      canvas.style.cursor = 'pointer'

      if (this.isDraging && f.active) {
        map.setLayoutProperty(
          id,
          'icon-image',
          `${f.properties.markerId}_hover`
        )
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
    },
    markerMouseLeave(e, f, id) {
      if (this.popup) {
        this.popup.remove()
      }
      this.popup = null
      e.preventDefault()
      let { map } = this
      let canvas = map.getCanvasContainer()

      canvas.style.cursor = ''
      if (this.isDraging && f.active) {
        map.setLayoutProperty(id, 'icon-image', `${f.properties.markerId}`)
      }

      if (
        f &&
        f.properties.employeeId &&
        this.moveElement &&
        !this.mouseMouseEnter
      ) {
        map['dragPan'].disable()
        map.setLayoutProperty(id, 'icon-image', 'desk')
        if (f.desk && f.desk.employee && f.desk.employee.id) {
          this.mouseDownElement(e, f.desk.employee, f)
        }
        f.properties.employeeId = null
        this.$set(f.properties, 'secondaryLabel', this.getDeskSecondaryLabel(f))
        this.unassignDesk(f)
        this.updateProperty(f)
      }
      this.mouseMouseEnter = false
    },
    initializeTimeRange() {
      if (this.customTime?.date) {
        let orgTzDate = momenttz.tz(
          moment.utc(parseInt(this.customTime.date)),
          Vue.prototype.$timezone
        )
        this.timeRange.startTime =
          parseInt(orgTzDate.valueOf()) + parseInt(this.customTime.startTime)
        this.timeRange.endTime =
          parseInt(orgTzDate.valueOf()) + parseInt(this.customTime.endTime)
      } else if (this.customTime?.startTime) {
        let startMoment = this.$helpers.getOrgMoment()
        startMoment.startOf('day')
        // startMoment.add(8, 'hour')
        this.timeRange.startTime =
          startMoment.valueOf() + parseInt(this.customTime.startTime)

        let endMoment = this.$helpers.getOrgMoment()
        endMoment.startOf('day')
        // endMoment.add(20, 'hour')
        this.timeRange.endTime =
          endMoment.valueOf() + parseInt(this.customTime.endTime)
      } else {
        let startMoment = this.$helpers.getOrgMoment()
        startMoment.startOf('day')
        startMoment.add(9, 'hour')
        this.timeRange.startTime = startMoment.valueOf()

        let endMoment = this.$helpers.getOrgMoment()
        endMoment.startOf('day')
        endMoment.add(17, 'hour')
        this.timeRange.endTime = endMoment.valueOf()
      }
    },
    afterDeserialize() {
      //set zone colors based on reservable and availabilty status
      this.zones.features.forEach(feature => {
        if (feature?.isReservable) {
          // feature.properties.zoneBackgroundColor = '#0D5BE1'
        }
        if (feature?.properties?.isBooked) {
          //feature.properties.zoneBackgroundColor = '#dc4a4c'
        }
      })
    },
    setHoverEffect(feature) {
      if (feature) {
        if (this.selectedHoverZone) {
          this.removeHoverEffect(this.selectedHoverZone)
        }
        this.selectedHoverZone = this.$helpers.cloneObject(feature)
        let newFeature = this.$helpers.cloneObject(feature)
        if (newFeature?.properties?.zoneBackgroundColor) {
          this.$set(newFeature.properties, 'zoneBackgroundColor', '#00bfff')
          this.$set(newFeature.properties, 'fill-outline-color', '#0073cf')
        }
        this.addZoneStrokeLayer(newFeature)
        this.replaceFeature(newFeature)
      }
    },
    removeHoverEffect(f) {
      if (f) {
        this.removeZoneStrokeLayer('zone-outline')
        this.selectedHoverZone = this.$helpers.cloneObject(f)
        this.replaceFeature(this.selectedHoverZone)
      } else {
        this.removeZoneStrokeLayer('zone-outline')
        this.replaceFeature(this.selectedHoverZone)
      }
      this.selectedHoverZone = null
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

    handleZoneClick() {
      this.initBookingsPanel()
    },
    async loadBookingForms() {
      let { data, error } = await API.get('/v2/forms', {
        moduleName: this.bookingModuleName,
      })
      if (error) {
        this.$message.error('error loading facility booking ')
      } else {
        this.formList = data.forms
      }
    },
    closeForm() {
      this.showBookingsForm = false
    },
    savedForm() {
      // console.log('form saved')
      this.showBookingsForm = false
      this.showBookingsPanel = false
      this.$nextTick(async () => {
        await this.loadViewerData()
      })
    },
    //   @hook implemented
    async loadBookingsAndFacilities() {
      let url = 'v3/floorplan/getFacilityDetails'

      let deskIds = this.indoorFloorPlan.markers
        .filter(marker => marker?.desk?.deskType == 3)
        .map(deskMarker => {
          return deskMarker?.desk?.id
        })
      let bookableSpaceIds = this.indoorFloorPlan.markedZones
        .filter(markedZone => markedZone.isReservable)
        .map(markedZone => {
          return markedZone?.space?.id
        })

      let allIds = [...bookableSpaceIds, ...deskIds]
      //collect reservable space and hot desk Ids and send to api

      let params = {
        spaceIds: allIds,
        startTime: this.timeRange.startTime,
        endTime: this.timeRange.endTime,
      }
      let { data, error } = await API.post(url, params)

      if (error) {
        this.$message.error('error loading bookings for floor plan')
      } else {
        this.facilities = data.facility
        this.bookings = data.facilitybooking
      }
    },

    async afterDataLoadHook() {
      //this.loadViewerData()
    },

    initBookingsPanel() {
      let { properties } = this.selectedFeature
      let basespaeceId = properties.deskId || properties.spaceId
      this.clickedFacility = this.facilities[basespaeceId]
      if (!basespaeceId) {
        //marker has no desk or space
        return
      } else {
        this.clickedFacilityBookings = this.bookings[basespaeceId]
        this.showBookingsPanel = true
      }
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
        this.initBookingsPanel()
      }
    },
    handleMarkerClick() {
      this.specialHandelforMarkerClick(this.selectedFeature)
    },

    handleBookBtn(slots) {
      this.showBookingsPanel = false
      //clicked from popup for both space and desk
      if (this.newBooking) {
        this.triggerBookingForm(slots)
      } else {
        if (this.selectedFeature?.properties?.deskId) {
          this.triggerForm(
            [
              'hot_desk_facilitybooking_web',
              'hot_desk_facilitybooking_workplace',
              'hot_desk_facilitybooking_portal',
            ],
            this.selectedFeature.properties.deskId,
            slots
          )
        } else if (this.selectedFeature.isReservable) {
          this.triggerForm(
            //web and app forms cannot have same name
            [
              'Space_facilitybooking_web',
              'space_facilitybooking_web',
              'space_facilitybooking_workplace',
              'Space_facilitybooking_portal',
            ],
            this.selectedFeature.properties.spaceId,
            slots
          )
        }
      }
    },
    //both marked zone ->space and desk extend SPACE->Basespace . so facilty map has facilities for all these
    triggerForm(formNames, baseSpaceId, slots) {
      this.selectedForm = null
      if (this.selectedFeature?.properties?.bookingFormId) {
        let { bookingFormId } = this.selectedFeature.properties
        this.selectedForm = this.formList.find(
          form => form.id === bookingFormId
        )
      } else {
        this.selectedForm = this.formList.find(form =>
          formNames.includes(form.name)
        )
      }

      let facility = { id: this.facilities[baseSpaceId].id }

      //prefill today.,issue in picker needs converting back to UTC
      let selectedDateOrgTime = this.$helpers
        .getOrgMoment(this.timeRange.startTime)
        .startOf('day')
        .format('YYYY-MM-DD HH:mm:ss')
      //console.log('date in org',selectedDateOrgTime)
      // let selectedDateUTCTime=moment.utc(selectedDateOrgTime)
      this.recordData = {
        facility,
        bookingDate: moment(selectedDateOrgTime).valueOf(),
        slotList: slots.map(e => {
          return { slot: e }
        }), //needs to be in this format for prefill
      }

      this.showBookingsForm = true
    },

    triggerBookingForm(slots) {
      let { bookingStartTime, bookingEndTime } = slots
      this.selectedForm = null
      if (this.selectedFeature?.properties?.bookingFormId) {
        let {
          bookingFormId,
          markerModuleName,
          recordId,
        } = this.selectedFeature.properties
        this.selectedForm = this.formList.find(
          form => form.id === bookingFormId
        )
        if (bookingStartTime && bookingEndTime) {
          this.recordData = {
            bookingStartTime,
            bookingEndTime,
          }
        } else {
          this.recordData = {
            bookingStartTime: moment(this.timeRange.startTime).valueOf(),
            bookingEndTime: moment(this.timeRange.endTime).valueOf(),
          }
        }

        if (markerModuleName === 'desks') {
          this.$set(this.recordData, 'desk', { id: recordId })
        }
        if (markerModuleName === 'parkingstall') {
          this.$set(this.recordData, 'parkingStall', { id: recordId })
        } else {
          this.$set(this.recordData, 'space', { id: recordId })
        }

        this.showBookingsForm = true
      } else {
        this.$message.error('No booking form found')
      }

      // let facility = { id: this.facilities[baseSpaceId].id }

      //prefill today.,issue in picker needs converting back to UTC
      // let selectedDateOrgTime = this.$helpers
      //   .getOrgMoment(this.timeRange.startTime)
      //   .startOf('day')
      //   .format('YYYY-MM-DD HH:mm:ss')
      //console.log('date in org',selectedDateOrgTime)
      // let selectedDateUTCTime=moment.utc(selectedDateOrgTime)
      // this.recordData = {
      //   bookingDate: moment(selectedDateOrgTime).valueOf(),

      // }
    },

    //   @overridden
    setDeskActiveState(feature) {
      // used to set desk active state
      return feature
    },
    //Generate icon Key for a given desk ,name generated based on TYPE+BOOKING STATE
    //   @overridden
    getIconName(desk) {
      let name = 'desk'

      // if (desk && desk.department) {
      //   name += `_${desk.department.name}`
      // }

      if (desk.deskType == 3) {
        if (this.bookings[desk.id] && this.bookings[desk.id].length) {
          name += '_hot_booked'
        } else {
          name += '_hot_available'
        }
      }
      if (desk.deskType == 2) {
        if (this.bookings[desk.id] && this.bookings[desk.id].length) {
          name += '_hotel_booked'
        } else {
          name += '_hotel_available'
        }
      }

      return name
    },

    //for each  desk_type generate an icon key:'desk_type and value->icon canvas object
    //   @overridden
    generateIcons() {
      // NO department cases
      let availableColor = this.settings.bookingState.availableColor
      let bookedColor = this.settings.bookingState.notAvailableColor
      let nonReservableColor = this.settings.bookingState.nonReservableColor
      this.addDeskIcon('desk', null, null) //for no desk type, rare, usually desk has type

      this.addDeskIcon('desk_hot_available', availableColor, availableColor)
      this.addDeskIcon('desk_hot_booked', bookedColor, bookedColor)
      // this.addDeskIcon('desk_hotel_available', '#22ae5c', '#22ae5c')
      // this.addDeskIcon('desk_hotel_booked', '#dc4a4c', '#dc4a4c')

      this.addDeskIcon('desk_hotel_available', null, null) // now hotel desk is non reserable
      this.addDeskIcon('desk_hotel_booked', null, null)
      //*** */

      // this.departments.forEach(department => {
      //   let name = ''
      //   name = `desk_${department.name}`

      //   //generate icons for each type where outercolor =type.color
      //   this.addDeskIcon(name, null, department.color)
      //   //for HOT desk ,generate two icons , with inner color  indicating booking state

      //   this.addDeskIcon(name + '_available', '#0cff00', department.color)

      //   this.addDeskIcon(name + '_booked', '#ff1900', department.color)
      // })

      // this is for assignable desks
      this.deskTypes.forEach(type => {
        let name = ''
        name = `desk_${type.name}`
        this.addDeskIcon(
          name.replace(/\s+/g, ''),
          nonReservableColor,
          nonReservableColor
        )
        this.departments.forEach(department => {
          name = `desk_${type.name}`
          name += `_${department.name}`
          this.addDeskIcon(
            name.replace(/\s+/g, ''),
            nonReservableColor,
            nonReservableColor
          )
        })
      })
    },
    loadMetaData() {
      this.fetchFloorDetails()
    },
    async fetchFloorDetails() {
      let params = { floorId: this.floorId }

      return await API.get(`v2/floor/details`, params).then(({ data }) => {
        if (data) {
          this.floorDetails = data.floor
        }
      })
    },
  },
}
</script>

<style lang="scss">
.fp-bookings-popup {
  border: 1px solid #efefef;
  position: absolute;
  z-index: 5;
  display: none;

  &.active {
    display: block;
  }
  // left:450px;
  // top:250px;
  max-width: 400px;
  padding: 20px;
  background: white;
  box-shadow: 0 7px 7px 0 rgba(0, 0, 0, 0.15);
  min-width: 350px;

  .facility-name {
    color: #39b2c2;
    font-size: 17px;
    font-weight: 500;
  }
  .facility-type {
    color: #000000;
    font-size: 12px;
    letter-spacing: 0.6px;
  }

  .el-icon-star-on {
    color: #ffb70e;
    font-weight: 800;
  }
  .amenity {
    letter-spacing: 0.5px;
    color: #000000;
    font-size: 12px;
    margin-right: 5px;
  }
  .seperator {
    height: 1px;
    border: solid 0.5px #ededed;
  }

  .booked-slots-title {
    letter-spacing: 0.5px;
    color: #b3b3b3;
    font-size: 15px;
    font-weight: 500;
  }

  .slot-date {
    letter-spacing: 0.4px;
    color: #1a1a1a;
    font-weight: 500;
    font-size: 14px;
  }
  .slot-time {
    padding: 5px;
    margin-right: 3px;
    margin-bottom: 5px;
    border-radius: 3px;
    background-color: #39b3c2;
    font-size: 11px;
    font-weight: 500;
    color: #ffffff;
  }
  .reserved-by {
    color: #000000;
    font-weight: 14px;
  }
  .book-now-btn {
    padding: 10px;
    border-radius: 5px;
    background: #ff3184;
    font-size: 15px;
    text-align: center;
    font-weight: 500;
    letter-spacing: 0.6px;
    color: #ffffff;
  }
}
</style>
