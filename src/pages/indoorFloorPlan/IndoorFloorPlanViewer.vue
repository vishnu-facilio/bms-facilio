<template>
  <base-map
    :maxZoom="22"
    :zoom="20"
    :minZoom="10"
    class="width100"
    @mapLoaded="mapLoaded"
  />
</template>

<script>
import centroid from '@turf/centroid'
import { polygon } from '@turf/helpers'

import { API } from '@facilio/api'
import actionMixin from 'pages/indoorFloorPlan/mixins/ViewerActions.vue'
import BaseMap from 'pages/indoorFloorPlan/components/MapBoxBase.vue'
import mapboxgl from 'mapbox-gl'
import { mapGetters } from 'vuex'
import { getApp } from '@facilio/router'
import { isEmpty, isUndefined } from '@facilio/utils/validation'
import { eventBus } from '@/page/widget/utils/eventBus'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
export default {
  mixins: [actionMixin],
  data() {
    return {
      mouseDownClicked: false,
      selectedHoverZone: null,
      isDraging: false,
      prevId: null,
      fpEditor: false,
      move: {},
      floorplanSwitch: false,
      popup: null,
      clickPopup: null,
      isClickPopupActive: false,
      tooltipData: {
        employee: {},
        desk: {},
        label: null,
        markerType: null,
      },
      deskTypeEnumMap: {},
      moduleVsData: {},
      hoveredMarker: null,
      secondaryMarkers: {},
      viewerMode: 'ASSIGNMENT',
    }
  },
  computed: {
    ...mapGetters(['getCurrentUser', 'getTicketStatus']),
    floorplanTypesLength() {
      // have to remove
      if (!isEmpty(this.floorplanTypes) || !isUndefined(this.floorplanTypes)) {
        return Object.keys(this.floorplanTypes).length
      }
      return 0
    },
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
    lockerCatgegory() {
      let locker = this.spaceCategories.find(rt => rt.name === 'Lockers')
      return locker || null
    },
    lockerSpaceCatgegoryId() {
      if (this.lockerCatgegory) {
        return this.lockerCatgegory.id
      }
      return null
    },
    markerModuleVsId() {
      let { markers } = this.indoorFloorPlan
      let map = {}
      markers.forEach(marker => {
        if (
          marker &&
          marker.markerModuleId &&
          marker.recordId &&
          !marker.desk
        ) {
          if (map[marker.markerModuleId] && map[marker.markerModuleId].length) {
            let d = map[marker.markerModuleId]
            map[marker.markerModuleId] = [...d, ...[String(marker.recordId)]]
          } else {
            map[marker.markerModuleId] = [String(marker.recordId)]
          }
        }
      })
      return map
    },
    parkingModule() {
      let { modules } = this
      if (modules) {
        let mod = modules.find(rt => rt.name === 'parkingstall')
        if (mod) {
          return mod
        }
      }
      return null
    },
    lockerModule() {
      let { modules } = this
      if (modules) {
        let mod = modules.find(rt => rt.name === 'lockers')
        if (mod) {
          return mod
        }
      }
      return null
    },
    lockerList() {
      let { zones } = this

      let { features } = zones

      let data = []
      if (features && features.length) {
        features.forEach(feature => {
          if (
            feature?.properties?.markerModuleId &&
            feature.properties.markerModuleId === this.lockerModuleId
          ) {
            let { properties } = feature
            let desk = {
              id: properties.recordId,
              name: properties.label,
              isBooked: properties.isBooked,
              isOccupied: properties.isOccupied,
            }
            if (properties.employeeId) {
              desk['employee'] = { id: properties.employeeId }
            }
            if (properties.departmentId) {
              desk['department'] = { id: properties.departmentId }
            }
            data.push(desk)
          }
        })
      }
      return data
    },
    parkingList() {
      let { zones } = this

      let { features } = zones

      let data = []
      if (features && features.length) {
        features.forEach(feature => {
          if (
            feature?.properties?.markerModuleId &&
            feature.properties.markerModuleId === this.parkingModuleId
          ) {
            let { properties } = feature
            let parking = {
              id: properties.recordId,
              name: properties.label,
              isBooked: properties.isBooked,
              isOccupied: properties.isOccupied,
              isReservable: feature.isReservable,
            }
            if (feature.isReservable) {
              delete parking.isOccupied
            }
            if (properties.employeeId) {
              parking['employee'] = { id: properties.employeeId }
            }
            if (properties.departmentId) {
              parking['department'] = { id: properties.departmentId }
            }
            data.push(parking)
          }
        })
      }
      return data
    },
    lockerModuleId() {
      if (this.lockerModule) {
        return this.lockerModule.moduleId
      }
      return null
    },
    parkingModuleId() {
      if (this.parkingModule) {
        return this.parkingModule.moduleId
      }
      return null
    },
    parkingRecordIds() {
      let ids = []
      this.spaceList.forEach(space => {
        if (
          space.spaceCategory &&
          space.spaceCategory.id === this.parkingSpaceCategoryId
        ) {
          ids.push(String(space.id))
        }
      })
      return ids
    },
    LockerRecordIds() {
      let ids = []
      this.spaceList.forEach(space => {
        if (
          space.spaceCategory &&
          space.spaceCategory.id === this.lockerSpaceCatgegoryId
        ) {
          ids.push(String(space.id))
        }
      })
      return ids
    },
    moduleIdvsModuleName() {
      let { modules } = this
      let data = {}
      modules.forEach(m => {
        this.$set(data, m.moduleId, m.name)
      })
      return data
    },
    isPortal() {
      let {
        appCategory: { PORTALS },
      } = this.$constants
      if (getApp() && getApp().appCategory === PORTALS) {
        return true
      }
      return false
    },
    spaceList() {
      let { zones } = this
      let data = []

      if (zones.features && zones.features.length) {
        zones.features.forEach(rt => {
          if (rt.properties) {
            this.$set(rt.properties, 'isReservable', rt.isReservable)
            data.push(rt.properties)
          }
        })
      }
      return data
    },
    deskList() {
      let { markers } = this

      let { features } = markers

      let desks = []
      if (features && features.length) {
        features.forEach(feature => {
          if (
            feature?.properties?.markerModuleName &&
            feature.properties.markerModuleName === 'desks'
          ) {
            let { properties } = feature
            let desk = {
              id: properties.deskId,
              name: properties.label,
              deskType: properties.deskType,
              isBooked: properties.isBooked,
              isOccupied: properties.isOccupied,
              openDialog: properties.openDialog,
            }
            if (properties.employeeId) {
              desk['employee'] = { id: properties.employeeId }
            }
            if (properties.departmentId) {
              desk['department'] = { id: properties.departmentId }
            }
            desks.push(desk)
          }
        })
      }
      return desks
    },
    isDev() {
      const isDevMode = process.env.NODE_ENV === 'development'
      return isDevMode
    },
    floorId() {
      if (
        this.indoorFloorPlan &&
        this.indoorFloorPlan.floor &&
        this.indoorFloorPlan.floor.id
      ) {
        return this.indoorFloorPlan.floor.id
      }
      return null
    },
    siteId() {
      if (
        this.indoorFloorPlan &&
        this.indoorFloorPlan.site &&
        this.indoorFloorPlan.site.id
      ) {
        return this.indoorFloorPlan.site.id
      }
      return null
    },
    querryDeskId() {
      if (this.$route.query && this.$route.query.deskId) {
        return this.$route.query.deskId
      }
      return null
    },
  },
  components: {
    BaseMap,
  },
  async mounted() {
    this.getSpaceCatgory()
    // this.afterFloorplanLoad
    const floorPlandataLoaded = new Promise(resolve => {
      eventBus.$on('floorplandataloaded', data => {
        resolve(data)
      })
    })

    const objectLoad = new Promise(resolve => {
      eventBus.$on('floorplanObjectLoaded', data => {
        resolve(data)
      })
    })
    let asyncCalls = [floorPlandataLoaded, objectLoad]
    await Promise.all(asyncCalls)
    this.afterFloorplanLoadHook()
    eventBus.$on('amenity-filter', array => {
      this.handleAmenityFilter(array)
    })
    eventBus.$on('reset-amenity', () => {
      this.resetAmenityFilter()
    })
  },
  destroyed() {
    eventBus.$off('floorplandataloaded', {})
    eventBus.$off('floorplanObjectLoaded', {})
    eventBus.$off('amenity-filter', [])
    eventBus.$off('reset-amenity')
  },
  methods: {
    redirectToSummary(moduleId, recordId) {
      if (moduleId && recordId) {
        let moduleName = this.floorplanMappedModulesObject[moduleId] || null
        if (isWebTabsEnabled() && moduleName !== null) {
          let { name } =
            findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}

          if (name) {
            this.$router.push({
              name,
              params: {
                viewname: 'all',
                id: recordId,
              },
            })
          }
        } else {
          this.$message.info('No summary tab found!!!')
        }
      }
    },
    isLayerAvailable(layerName) {
      let mapLayer = this.map.getLayer(layerName)

      if (typeof mapLayer !== 'undefined') {
        // Remove map layer & source.
        return true
      }
      return false
    },
    afterFloorplanLoadHook() {},
    setZoneActiveState(feature) {
      // used to set the active state
      this.$set(feature, 'active', true)
      if (feature?.properties) {
        this.$set(feature.properties, 'active', true)
      }
      return feature
    },
    loadViewerData() {
      this.getViewerApi().then(() => {
        this.deSerializeGeoJson()
        this.refreshData()
      })
    },
    loadParkingData() {
      if (this.parkingModuleId && this.parkingRecordIds.length) {
        this.getModuleData(Number(this.parkingModuleId), this.parkingRecordIds)
      }
    },
    loadLockerData() {
      if (this.lockerModuleId && this.LockerRecordIds.length) {
        this.getModuleData(Number(this.lockerModuleId), this.LockerRecordIds)
      }
    },
    loadMarkerModuleData() {
      // need to remove
      if (Object.keys(this.markerModuleVsId).length) {
        this.moduleVsData = {}
        Object.keys(this.markerModuleVsId).forEach(moduleId => {
          let idsList = this.markerModuleVsId[moduleId]
          this.getModuleData(Number(moduleId), idsList)
        })
      }
    },
    async getModuleData(moduleId, idsList) {
      // need to remove

      if (idsList && idsList.length && moduleId) {
        let moduleName = this.moduleIdvsModuleName[moduleId]
        if (moduleName) {
          let filters = {
            id: { operatorId: 9, value: idsList },
          }
          let params = {
            moduleName: moduleName,
            includeParentFilter: true,
            page: 1,
            perPage: 200,
            filters: JSON.stringify(filters),
          }
          let { data } = await API.get(`v3/modules/data/list`, params)
          if (data && data[moduleName]) {
            this.$set(this.moduleVsData, moduleId, data[moduleName])
            if (moduleId === this.parkingModuleId) {
              this.refreshParkingZone()
            } else if (moduleId === this.lockerModuleId) {
              this.refreshLockerZone()
            }
          }
          this.refreshData()
        }
      }
    },
    refreshParkingZone() {
      let { zones } = this
      zones.features.forEach(feature => {
        let dataList = this.moduleVsData[this.parkingModuleId]
        if (dataList && dataList.length) {
          if (feature.space && feature.space.id) {
            let data = dataList.find(rt => rt.id === feature.space.id)
            if (data) {
              this.$set(feature, 'moduleData', data)
              let moduleStateId =
                data.moduleState && data.moduleState.id
                  ? data.moduleState.id
                  : null
              if (moduleStateId) {
                let statusMap = this.getTicketStatus(
                  moduleStateId,
                  'parkingstall'
                )
                let status =
                  statusMap && statusMap.displayName
                    ? statusMap.displayName
                    : null

                // need to remove this after demo
                if (moduleStateId === 28477) {
                  status = 'Vacant'
                } else if (moduleStateId === 28478) {
                  status = 'Occupied'
                } else {
                  status = null
                }
                //
                this.$set(feature.moduleData, 'statusDisplayName', status)
                this.$set(feature.properties, 'status', status)
                if (status === 'Occupied') {
                  // feature.properties.zoneBackgroundColor = 'green'
                } else if (status === 'Vacant') {
                  // feature.properties.zoneBackgroundColor = 'red'
                }
              } else {
                this.$set(feature.moduleData, 'statusDisplayName', null)

                this.$set(feature.properties, 'status', null)
              }
              this.updateProperty(feature)
              this.refreshData()
            }
          }
        }
      })
    },
    refreshLockerZone() {
      let { zones } = this
      zones.features.forEach(feature => {
        let dataList = this.moduleVsData[this.lockerModuleId]
        if (dataList && dataList.length) {
          if (feature.space && feature.space.id) {
            let data = dataList.find(rt => rt.id === feature.space.id)
            if (data) {
              this.$set(feature, 'moduleData', data)
              if (data.employee && data.employee.id) {
                this.$set(feature.properties, 'employeeId', data.employee.id)
                //feature.properties.zoneBackgroundColor = 'green'
              } else {
                this.$set(feature.properties, 'employeeId', null)
                //feature.properties.zoneBackgroundColor = 'red'
              }
            }
          }
        }
      })
      this.refreshData()
    },
    updateMarker() {
      // need to optimize this method
      let { markers } = this

      markers.features.forEach(feature => {
        if (feature.properties.markerModuleId && feature.properties.recordId) {
          if (this.moduleVsData[feature.properties.markerModuleId]) {
            let dataList = this.moduleVsData[feature.properties.markerModuleId]
            let data = dataList.find(
              rt => rt.id === feature.properties.recordId
            )
            if (data) {
              feature.moduleData = data
            }
          }
        }
      })
    },
    focusMarkerFromFeature(featureId) {
      let { markers = {}, zones = {} } = this

      let feature = null

      if (markers?.features) {
        feature = markers.features.find(
          f => f.properties.objectId === featureId
        )
        if (feature != null) {
          this.focusAndActiveMarker(feature)
        }
      }

      feature = null
      if (zones?.features) {
        feature = zones.features.find(f => f.properties.objectId === featureId)
        if (feature != null) {
          if (this.selectedHoverZone) {
            this.removeHoverEffect(this.selectedHoverZone)
          }
          this.setHoverEffect(feature)
          this.focusAndActivezone(feature)
          this.selectedHoverZone = feature
        }
      }
    },
    focusMarker(moduleName, data) {
      if (moduleName === 'desks' && data && data.id) {
        let { markers } = this
        if (markers.features && markers.features.length) {
          let feature = markers.features.find(f => {
            if (f?.properties?.recordId && f.properties.recordId === data.id) {
              return f
            } else if (f?.recordId && f.recordId === data.id) {
              return f
            } else {
              return null
            }
          })

          if (feature) {
            this.focusAndActiveMarker(feature)
          }
        }
      } else if (moduleName === 'space' && data && data.id) {
        let { zones } = this
        if (zones.features && zones.features.length) {
          let feature = zones.features.find(f => {
            if (f.properties?.spaceId && f.properties.spaceId === data.id) {
              return f
            } else {
              return null
            }
          })
          if (feature) {
            this.focusAndActivezone(feature)
          }
        }
      }
    },
    getPolygonCenter(polyGonCoordinates) {
      let centroidPoint = centroid(polygon(polyGonCoordinates))

      return centroidPoint.geometry.coordinates
    },
    async getDeskTypeEnumMap() {
      let url = '/module/meta'
      let { data, error } = await API.get(url, { moduleName: 'desks' })
      if (error) {
        console.error('error fetching module meta for desks', error)
      } else {
        let deskTypeField = data.meta.fields.find(
          field => field.name == 'deskType'
        )
        this.deskTypeEnumMap = deskTypeField.enumMap
      }
    },
    setToolTipData(f) {
      if (this.isDraging && !f.tooltipData) {
        return
      }

      if (f?.tooltipData) {
        this.$set(this, 'tooltipData', f.tooltipData)
      }
    },

    //load the map image only after floorplan data   and
    //mapbox component are loaded .but both can be loaded in parallel
    async init() {
      //console.log('init fired')
      // wait till all these async actions are completed. But all can be made in parallel
      this.floorplanloading = true
      this.loadFloorplanFields()
      let asyncCalls = [
        this.onMapLoad,
        this.getViewerApi(),
        this.loadModulesList(),
        this.fetchMarkerTypes(), //to do . marker type data needed for booking and assign view, other api calls can be moved to optional data load hooks
        this.getDepartments(),
        this.getDeskTypeEnumMap(),
      ]

      let resps = await Promise.all(asyncCalls)

      let [, floorplanResp] = resps

      //transform data from floorplanResp

      this.indoorFloorPlan = floorplanResp.indoorfloorplan

      this.deSerializeGeoJson()
      this.loadFloorplanTypes()
      // this.loadMarkerModuleData()
      // this.loadParkingData() // have to remove this method
      // this.loadLockerData() // have to remove this method
      this.fetchSpaces()
      this.afterDeserialize && this.afterDeserialize()
      //floor plan loades after mapbox and all required apis are done
      this.loadFloorplanImage(this.map)
      this.floorplanloading = false
      // console.log('floorplan loaded')
      eventBus.$emit('floorplandataloaded')
      if (this.afterDataLoadHook) {
        //extend and write after load hook in children
        await this.afterDataLoadHook()
      }
    },
    afterFloorPlanRender() {
      try {
        // if currently logged in user is assigned a desk in current floor plan , focus on desk
        let feature = this.getFeatureAssisgnedToCurrentUser()
        if (feature && !this.querryDeskId) {
          this.map.flyTo({
            center: feature.geometry.coordinates,
            zoom: 20,
            essential: true, // this animation is considered essential with respect to prefers-reduced-motion
          })
          this.yourMarker = []
          new mapboxgl.Marker({ color: 'red' })
            .setLngLat(feature.geometry.coordinates)
            .addTo(this.map)
          this.addYourMarker(feature)
          this.loadFloorplanSettings()
          // this.yourMarker.push(yourMarker)
        }
      } catch (e) {
        console.error('error zooming into current employee')
      }
    },
    setQuerrData() {
      // methods used to set the querry data
    },
    loadFloorplanSettings() {},
    refreshData() {
      //this.preFormatMarkers()
      this.map.getSource('spaceZone').setData(this.zones)
      this.map.getSource('marker').setData(this.markers)
    },

    addYourMarker(f, layerName = 'yourmarker') {
      this.map.addLayer({
        id: layerName,
        type: 'symbol',
        source: {
          type: 'geojson',
          data: {
            type: 'FeatureCollection',
            features: [f],
          },
        },
        layout: {
          'icon-allow-overlap': true,
          'text-field': `Your desk`,
          'text-font': ['Open Sans Bold', 'Arial Unicode MS Bold'],
          'text-size': 14,
          'text-letter-spacing': 0.03,
          'text-offset': [-4, -1],
        },
        paint: {
          'text-color': 'red',
          'text-halo-color': '#fff',
          'text-halo-width': 1,
        },
      })
    },
    getFeatureAssisgnedToCurrentUser() {
      let feature = null
      let user = this.getCurrentUser()
      let currentEmployeeId = user.peopleId

      if (currentEmployeeId) {
        this.markers.features.forEach(f => {
          if (f?.properties?.employeeId == currentEmployeeId) {
            feature = f
          }
        })
      }
      return feature
    },
    loadMetaData() {
      // hooks for meta data like space and floor desks etc
    },
    focusAndActiveMarker(feature) {
      if (this.selectedFeature) {
        this.unSelectFeature(this.selectedFeature)
      }
      this.selectFeature(feature)
      this.selectedFeature = feature
      this.handleMarkerClick(feature)
      this.map.flyTo({
        center: feature.geometry.coordinates,
        zoom: this.maxZoom - 1,
        essential: true, // this animation is considered essential with respect to prefers-reduced-motion
      })
      this.$nextTick(() => {
        this.onFeatureSelect(feature, 'marker')
      })
    },
    onFeatureSelect(feature, node) {
      this.$emit('onFeatureSelect', feature, node)
    },
    focusAndActivezone(feature) {
      if (this.selectedFeature) {
        this.unSelectFeature(this.selectedFeature)
      }
      this.selectFeature(feature)
      this.selectedFeature = feature
      this.onFeatureSelect(feature, 'zone')
      this.spaceMapDialog = true
      this.selectedZone = feature
      let { coordinates } = feature.geometry
      this.fitBounds(coordinates[0], 100)
    },
    addSelectedMarker() {
      // used to add the location marker for mapbox
    },
    closePropertyDialog() {
      this.unSelectFeature(this.selectedFeature)
      this.selectedFeature = null
      this.onFeatureSelect(null, 'marker')
      this.propertiesFeatureVisibile = false
    },
    updateData() {
      // this.updateProperty(this.selectedFeature)
      // let desk = this.deskDataFromFeature(this.selectedFeature)
      // this.updateDesk(desk)
    },
    updateMarkerData() {
      this.updateProperty(this.selectedFeature)
      let marker = this.markerFromFeature(this.selectedFeature)
      this.updateMarker(marker)
    },
    updateLocker() {
      this.updateProperty(this.selectedFeature)
    },
    markerFromFeature(feature) {
      let { markers } = this.indoorFloorPlan
      if (feature.properties && feature.properties.objectId && markers.length) {
        let marker = markers.find(
          rt => rt.id === String(feature.properties.objectId)
        )
        if (marker && feature.properties && feature.properties.label) {
          marker.label = feature.properties.label
          marker.id = Number(feature.properties.objectId)
          marker.geometry = JSON.stringify(marker.geometry)
          marker.properties = JSON.stringify(marker.properties)
          return marker
        }
      }
      return null
    },
    moveData() {
      this.updateProperty(this.selectedFeature)
      this.updateMove(this.selectedFeature)
    },
    updateProperty(feature) {
      if (feature.desk) {
        let desk = this.deskDataFromFeature(feature)
        let name = this.getIconName(desk)
        feature.properties.normalClass = name
        this.$set(feature.properties, 'markerId', name)
      }
      this.refreshData()
    },
    unSelectFeature(feature) {
      if (feature.space) {
        //need to remove border when zone unselected
        // {
        //   this.map.setPaintProperty(
        //     feature.id,
        //     'fill-extrusion-color',
        //     '#000000'
        //   )
        // }
      } else {
        //   this.map.setLayoutProperty(
        //     feature.id,
        //     'icon-image',
        //     feature.properties.markerId
        //   )
        if (feature.properties.normalClass) {
          feature.properties.markerId = feature.properties.normalClass
        }
      }
      this.refreshData()
    },
    selectFeature(feature) {
      if (!feature) {
        return
      }
      if (feature.objectType === 2) {
        // this.map.setPaintProperty(feature.id, 'fill-extrusion-color', '#0D5BE1')
        // need to highlight borders when space selected
      } else {
        if (
          feature.properties.markerId &&
          feature.properties.markerId.indexOf('desk') > -1
        ) {
          let iconName = feature.properties.activeClass
          feature.properties.markerId = iconName
        } else {
          let iconName = `${feature.properties.markerId}`
          feature.properties.markerId = iconName
        }
      }
      this.refreshData()
    },

    getDragElement(dragElement) {
      let data = null
      if (dragElement && dragElement.employee) {
        data = dragElement.employee
        if (dragElement.department) {
          this.$set(data, 'data', { department: dragElement.department })
        }
      }

      return data
    },
    mouseDownElement(e, data, feature) {
      this.isDraging = true
      this.dragElement = feature ? this.getDragElement(feature.desk) : data
      if (e.pageX && e.pageY) {
        this.createElemnt(e)
      } else {
        this.createMouseDownElemnt(e)
      }
      if (this.selectedFeature) {
        this.propertiesFeatureVisibile = false
        //this.unSelect(this.selectedFeature)
      }
    },
    getDepartmentColor(dragElement) {
      if (
        dragElement &&
        dragElement.data &&
        dragElement.data.department &&
        dragElement.data.department.id
      ) {
        let department = this.departments.find(
          rt => rt.id === dragElement.data.department.id
        )
        if (department && department.color) {
          return department.color
        }
      }
      return '#000'
    },
    createMouseDownElemnt(e) {
      if (!e.point.x && !e.point.y) return
      let color = this.getDepartmentColor(this.dragElement)
      let div = document.getElementById('dragItem')
      div.style.position = 'absolute'
      div.style.display = 'block'
      div.style.zIndex = '10'
      div.style.top = '0px'
      ;(div.innerHTML = `<div class="dragelemnt-innerContainer">
      <div class="drag-avatar">${this.dragElement.name.charAt(
        0
      )}${this.dragElement.name.charAt(1)}</div>
      <div class="drag-label">${this.dragElement.name}</div>
      </div>`),
        (div.style.transform = `translate(${e.point.x + 10}px,${e.point.y}px)`) // Insert text
      div.style.background = color
      document.body.appendChild(div)
    },
    createElemnt(e) {
      if (!e.pageX && !e.pageY) return
      let color = this.getDepartmentColor(this.dragElement)
      let div = document.getElementById('dragItem')
      div.style.position = 'absolute'
      div.style.display = 'block'
      div.style.zIndex = '10'
      div.style.top = '0px'
      ;(div.innerHTML = `<div class="dragelemnt-innerContainer">
      <div class="drag-avatar">${this.dragElement.name.charAt(
        0
      )}${this.dragElement.name.charAt(1)}</div>
      <div class="drag-label">${this.dragElement.name}</div>
      </div>`),
        (div.style.transform = `translate(${e.pageX + 10}px,${e.pageY}px)`) // Insert text
      div.style.background = color
      document.body.appendChild(div)
    },
    ghostElemntHandle(e) {
      if (!e.pageX && !e.pageY) return
      let div = document.getElementById('dragItem')
      div.style.display = 'block'
      div.style.transform = `translate(${e.pageX + 10}px,${e.pageY}px)`
    },
    addMarkedZones(map) {
      map.addSource('spaceZone', {
        type: 'geojson',
        data: this.zones,
      })

      let id = 'zone'
      map.addLayer({
        id: id,
        type: 'fill',
        source: 'spaceZone',
        paint: {
          'fill-color': ['get', 'zoneBackgroundColor'],
          'fill-opacity': ['get', 'zoneOpacity'],
        },
        filter: ['all', ['in', '$type', 'Polygon']],
      })
      map.addLayer({
        id: `space-text-${id}`,
        type: 'symbol',
        source: 'spaceZone',
        layout: {
          'icon-allow-overlap': true,
          'text-field': `{label}`,
          'text-font': ['Open Sans Bold', 'Arial Unicode MS Bold'],
          'text-max-width': 15,
          'text-size': [
            'interpolate',
            ['linear'],
            ['zoom'],
            this.minZoom,
            0,
            this.maxZoom,
            this.fontSize + 1,
          ],
          'text-transform': 'uppercase',
          'text-letter-spacing': 0.03,
          'text-offset': [0, 0],
        },
        paint: {
          'text-color': '#324056',
          'text-halo-color': '#ffffff',
          'text-halo-width': 0.5,
        },
        filter: ['all', ['in', '$type', 'Polygon']],
      })
      map.addLayer({
        id: `space-secondary-text-${id}`,
        type: 'symbol',
        source: 'spaceZone',
        layout: {
          'icon-allow-overlap': true,
          'text-field': `{secondaryLabel}`,
          'text-font': ['Open Sans Semibold', 'Arial Unicode MS Bold'],
          'text-max-width': 15,
          'text-size': [
            'interpolate',
            ['linear'],
            ['zoom'],
            this.minZoom,
            0,
            this.maxZoom,
            this.fontSize + 1,
          ],
          'text-transform': 'uppercase',
          'text-letter-spacing': 0.03,
          'text-offset': [
            'interpolate',
            ['exponential', 0.5],
            ['zoom'],
            17,
            ['literal', [0, 1.2]],
            18,
            ['literal', [0, 1.2]],
            19,
            ['literal', [0, 1.3]],
            20,
            ['literal', [0, 1.5]],
          ],
        },
        paint: {
          'text-color': '#324056',
          'text-halo-color': '#ffffff',
          'text-halo-width': 0.5,
        },
        filter: ['all', ['in', '$type', 'Polygon']],
      })
      map.on('click', id, e => {
        let f = this.getFirstFeatureFromZonePoints(e)
        this.zoneClick(e, f)
      })
      map.on('mousemove', id, e => {
        e.preventDefault()
        let f = this.getFirstFeatureFromZonePoints(e)
        // console.log('=====> f', f)
        //  this.unSelectHoverItems(f)
        this.setToolTipData(f)
        this.setHoverEffect(f)
        this.zoneMouseEnter && this.zoneMouseEnter(e, f, id)
        this.prevId = f
      })
      map.on('mouseleave', id, e => {
        let f = this.getFirstFeatureFromZonePoints(e)
        this.removeHoverEffect(f)
        this.zoneMouseLeave && this.zoneMouseLeave(e, f, id)
      })
    },
    setHoverEffect() {
      // prototype
    },
    // unSelectHoverItems(f) {
    //   if (f == null) {
    //   } else if (
    //     f != null &&
    //     f.properties.spaceId != f.properties.spaceId
    //   ) {
    //     this.removeHoverEffect(this.prevId)
    //   }
    // },
    addZoneStrokeLayer(newFeature) {
      if (
        this.map.getLayer('zone-outline') &&
        this.map.getSource('zone-outline')
      ) {
        return
      }
      this.map.addLayer({
        id: 'zone-outline',
        type: 'line',
        source: {
          type: 'geojson',
          data: newFeature,
        },
        paint: {
          'line-color': '#0073cf',
          'line-width': 2,
        },
      })
    },
    removeZoneStrokeLayer(id) {
      if (this.map.getLayer(id) && this.map.getSource(id)) {
        this.map.removeLayer(id)
        this.map.removeSource(id)
      }
      return
    },
    replaceFeature(newFeature) {
      if (this.zones?.features) {
        let index = this.zones.features.findIndex(
          f => f.properties.objectId === newFeature.properties.objectId
        )
        if (index > -1) {
          this.zones.features[index] = newFeature
        }
      }
      this.refreshData()
    },
    removeHoverEffect() {
      // prototype
    },
    getFirstFeatureFromZonePoints(e) {
      let point = this.map.queryRenderedFeatures(e.point)
      let feature = point ? point[0] : null
      return this.getZoneFromObjectId(feature) || null
    },
    getFirstFeatureFromMarkerPoints(e) {
      let point = this.map.queryRenderedFeatures(e.point)
      let feature = point ? point[0] : null
      return this.getMarkerFromObjectId(feature) || null
    },
    getZoneFromObjectId(f) {
      if (f && f.properties && f.properties.objectId) {
        return this.zoneMap[f.properties.objectId]
      }
      return null
    },
    getMarkerFromObjectId(f) {
      if (f && f.properties && f.properties.objectId) {
        return this.markerMap[f.properties.objectId]
      }
      return null
    },
    preFormatMarkers() {
      this.markers.features.forEach(marker => {
        let { properties } = marker
        this.$set(
          properties,
          'centerLabel',
          this.centerLabel(properties.secondaryLabel)
        )
      })
    },
    addMarkerLayer(map) {
      this.mapAddImages()
      //this.preFormatMarkers()
      map.addSource('marker', {
        type: 'geojson',
        data: this.markers,
      })
      let id = 'marker'
      map.addLayer({
        id: id,
        type: 'symbol',
        source: 'marker',
        layout: {
          'icon-image': '{markerId}',
          'text-field': '{label}',
          'text-allow-overlap': true,
          'text-font': ['Open Sans Semibold', 'Arial Unicode MS Bold'],
          'text-offset': [0, 2],
          'text-anchor': 'top',
          'icon-allow-overlap': true,
          'icon-size': [
            'interpolate',
            ['linear'],
            ['zoom'],
            this.minZoom,
            0,
            this.maxZoom,
            this.iconSize,
          ],
          'text-size': [
            'interpolate',
            ['linear'],
            ['zoom'],
            this.minZoom,
            0,
            this.maxZoom,
            this.fontSize,
          ],
        },
        paint: {
          'icon-opacity': [
            'case',
            ['boolean', ['get', 'active'], false],
            1,
            0.3,
          ],
        },
        filter: ['all', ['in', '$type', 'Point']],
      })

      map.addLayer({
        id: `secondary-label-${id}`,
        type: 'symbol',
        source: 'marker',
        layout: {
          'icon-allow-overlap': true,
          'text-field': `{secondaryLabel}`,
          'text-font': ['Open Sans Semibold', 'Arial Unicode MS Bold'],
          'text-size': [
            'interpolate',
            ['linear'],
            ['zoom'],
            8,
            0,
            this.maxZoom,
            this.fontSize + 3,
          ],
          'text-letter-spacing': 0.03,
          'text-offset': [0, -2],
        },
        paint: {
          'text-color': '#25243e',
          'text-halo-width': 0.5,
          'text-halo-color': '#ffffff',
        },
        filter: ['all', ['in', '$type', 'Point']],
      })

      map.addLayer({
        id: `formated-secondary-label-${id}`,
        type: 'symbol',
        source: 'marker',
        layout: {
          'icon-allow-overlap': true,
          'text-field': `{centerLabel}`,
          'text-font': ['Open Sans Bold', 'Arial Unicode MS Bold'],
          'text-size': [
            'interpolate',
            ['linear'],
            ['zoom'],
            this.minZoom,
            0,
            this.maxZoom,
            this.fontSize + 1,
          ],
          'text-letter-spacing': 0.05,
          'text-offset': [0, 0],
        },
        paint: {
          'text-color': '#fafafa',
          'text-halo-color': '#25243e',
          'text-halo-width': 1,
        },
        filter: ['all', ['in', '$type', 'Point']],
      })

      map.on('click', id, e => {
        e.preventDefault()
        let f = this.getFirstFeatureFromMarkerPoints(e)

        this.markerClick(e, f)
      })
      map.on('mousemove', id, e => {
        // recent impplementaion due to breck school zone top marker issue

        //  console.log('****** f', e)
        e.preventDefault()
        let f = this.getFirstFeatureFromMarkerPoints(e)
        this.setToolTipData(f)
        this.markerMouseEnter && this.markerMouseEnter(e, f, id)
      })

      map.on('mouseenter', id, e => {
        e.preventDefault()
        let f = this.getFirstFeatureFromMarkerPoints(e)
        this.setToolTipData(f)
        this.markerMouseEnter && this.markerMouseEnter(e, f, id)
      })

      map.on('mouseleave', id, e => {
        let f = this.getFirstFeatureFromMarkerPoints(e)
        if (this.hoveredMarker) {
          f = this.hoveredMarker
        }
        this.markerMouseLeave && this.markerMouseLeave(e, f, id)
      })

      map.on('mousedown', id, e => {
        let f = this.getFirstFeatureFromMarkerPoints(e)
        this.mouseDownClicked = true
        this.markerMouseDown && this.markerMouseDown(e, f, id)
      })

      map.on('mouseup', id, e => {
        let f = this.getFirstFeatureFromMarkerPoints(e)
        if (this.mouseDownClicked) {
          this.markerClick(e, f)
        }
        this.mouseDownClicked = false

        this.markerMouseUp && this.markerMouseUp(e, f, id)
      })

      // map.on('render', id, e => {
      //   if (!map.isSourceLoaded(`marker`)) return
      //   this.updateMarkers(id, e)
      // })
    },

    //pass click,mouse enter or any event , and dom ref of popup template
    displayClickPopUp(coordinates, popupEle) {
      this.isClickPopupActive = true

      this.clickPopup = new mapboxgl.Popup({ closeButton: false })
      this.clickPopup
        .setLngLat(coordinates)
        .setDOMContent(popupEle)
        .addTo(this.map)
      this.clickPopup.on('close', () => {
        // console.log('popup was closed',e);
        this.isClickPopupActive = false
      })
    },
    updateMarkers() {
      {
        let { map } = this
        let features = map.querySourceFeatures(`marker`)
        document.getElementsByClassName('secondaryMarker').forEach(el => {
          el.remove()
        })
        features.forEach(feature => {
          let coords = feature.geometry.coordinates
          let { id } = feature
          let el = document.createElement('div')
          el.className = 'secondaryMarker'
          el.innerHTML = `<div>hii</div>`
          this.secondaryMarkers[id] = new mapboxgl.Marker(el)
            .setLngLat(coords)
            .addTo(this.map)
        })
      }
    },
    specialHandelCheck() {
      // overwride this function for special Handel
      return true
    },
    markerClick(e, f) {
      e.preventDefault()

      if (!this.specialHandelCheck(f) && !f.active) {
        return
      }
      if (this.selectedFeature) {
        this.unSelectFeature(this.selectedFeature)
      }
      this.selectFeature(f)
      this.selectedFeature = f
      this.onFeatureSelect(f, 'marker')
      this.handleMarkerClick(f)
    },

    zoneClick(e, f) {
      //if (!f.active) return //zone active state set from child comps  , based on IS_RESERVABLE Flag
      e.preventDefault()

      if (this.selectedFeature) {
        this.unSelectFeature(this.selectedFeature)
      }
      this.selectFeature(f)
      this.selectedFeature = f
      this.onFeatureSelect(f, 'zone')

      this.handleZoneClick && this.handleZoneClick(e, f) //fire hook if children have registered
    },

    loadObjects(map) {
      this.generateIcons()
      this.addMarkedZones(map)
      this.addMarkerLayer(map)
      this.loadDefaultDesk(map)
      this.setQuerrData(map)
      eventBus.$emit('floorplanObjectLoaded')
      // console.log('floorplan object s loaded')
    },
    loadDefaultDesk() {
      if (this.querryDeskId) {
        let feature = this.markers.features.find(f => {
          if (f?.desk?.id == this.querryDeskId) {
            return f
          }
          return null
        })
        if (feature) {
          this.setHoverEffect(feature)
          this.focusAndActiveMarker(feature)
        }
      }
    },
    deskDataFromFeatureNew(feature) {
      if (feature?.properties?.deskId) {
        let { properties } = feature
        let desk = {
          id: feature.properties.deskId,
          name: properties.label || '',
        }
        if (properties.employeeId) {
          desk['employee'] = { id: properties.employeeId }
        }
        if (properties.departmentId) {
          desk['department'] = { id: properties.departmentId }
        }
        return desk
      }
      return null
    },
    // to be removed bcs moving to new viewer api
    deskDataFromFeature(feature) {
      if (feature && feature.properties && feature.properties.desk) {
        let { properties } = feature
        let { desk } = properties
        if (properties.deskId) {
          this.$set(desk, 'id', properties.deskId)
        }
        if (properties.deskType) {
          this.$set(desk, 'deskType', properties.deskType)
        }
        if (properties.deskCode) {
          this.$set(desk, 'deskCode', properties.deskCode)
        }
        if (properties.employeeId) {
          if (
            feature.employee &&
            feature.employee.id &&
            feature.employee.id === properties.employeeId
          ) {
            this.$set(desk, 'employee', feature.employee)
          } else {
            if (
              desk.employee?.value &&
              desk.employee?.value === properties.employeeId
            ) {
              this.$set(desk, 'employee', {
                id: properties.employeeId,
                name: desk.employee.label,
              })
            } else if (desk.employee?.id !== properties.employeeId) {
              this.$set(desk, 'employee', { id: properties.employeeId })
            }
          }
          if (properties.departmentId) {
            if (
              desk.department &&
              desk.department.id &&
              desk.department.id === properties.departmentId
            ) {
              this.$set(desk, 'department', desk.department)
            } else {
              this.$set(desk, 'department', { id: properties.departmentId })
            }
          } else {
            delete desk.department
          }
        } else {
          delete desk.employee
        }

        if (properties.departmentId) {
          if (
            desk.department &&
            desk.department.id &&
            desk.department.id === properties.departmentId
          ) {
            this.$set(desk, 'department', desk.department)
          } else {
            this.$set(desk, 'department', { id: properties.departmentId })
          }
        }
        return desk
      }
    },
    //
    unassignDesk(feature) {
      if (feature && feature.properties) {
        let desk = this.deskDataFromFeature(feature)
        if (desk) {
          //this.updateDesk(desk)
          this.$set(this.move, 'fromFetaure', feature)
          this.$set(this.move, 'fromDesk', desk)
        }
      }
    },
    getDepartmentFromEmployee(employeeId) {
      let employee = this.employeeList.find(rt => rt.id === employeeId)
      if (employee && employee.data && employee.data.department) {
        return employee.data.department
      } else {
        return null
      }
    },
    unassignDeskfromProperty() {
      let feature = this.selectedFeature
      this.updateProperty(feature)
      let desk = feature.desk || null

      if (desk) {
        this.$set(this.move, 'fromFetaure', feature)
        this.$set(this.move, 'fromDesk', desk)
        let msg = `${desk.employee.name} is unassigned from ${desk.name}`
        this.updateMoveApi(msg)
      }
    },
    async updateMove(feature) {
      if (feature && feature.properties) {
        let desk = this.deskDataFromFeatureNew(feature)
        if (desk) {
          this.$set(this.move, 'toFetaure', feature)
          this.$set(this.move, 'toDesk', desk)
          let employeId =
            desk.employee && desk.employee.id ? desk.employee.id : null
          await Promise.all([
            this.updateMoveApi(),
            this.fetchEmployeeFulldetails(employeId, desk),
          ])

          let fromDesk = this.movedEmployeeDesks.length
            ? this.movedEmployeeDesks[0]
            : null
          this.loadMoveDetails(fromDesk, desk)
        }
      }
    },
    updateMoveApi(msg) {
      let { move } = this
      this.movedEmployeeDesks = []
      if (move && Object.keys(move).length) {
        let moveAPI = this.prepareMoveData(move)
        this.updateMoveData(moveAPI).then(() => {
          if (msg) {
            this.$message.success(msg)
          }
          this.loadViewerData()
        })
      }
      this.move = {}
    },
    prepareMoveData(move) {
      let timeOfMove = new Date().valueOf()
      let moveParmas = {}
      if (move.fromDesk && move.toDesk) {
        this.$set(moveParmas, 'to', move.toDesk)
        this.$set(moveParmas, 'from', move.fromDesk)
        this.$set(moveParmas, 'timeOfMove', timeOfMove)
        if (move.toDesk.employee) {
          let department = this.getDepartmentFromEmployee(
            move.toDesk.employee.id
          )
          this.$set(moveParmas, 'employee', move.toDesk.employee)
          if (department) {
            this.$set(moveParmas, 'department', department)
          }
        }
      } else if (move.fromDesk && !move.toDesk) {
        this.$set(moveParmas, 'from', move.fromDesk)
        this.$set(moveParmas, 'timeOfMove', timeOfMove)
        if (move.fromDesk.employee) {
          let department = this.getDepartmentFromEmployee(
            move.fromDesk.employee.id
          )
          this.$set(moveParmas, 'employee', move.fromDesk.employee)
          if (department) {
            this.$set(moveParmas, 'department', department)
          }
        }
      } else if (move.toDesk && !move.fromDesk) {
        this.$set(moveParmas, 'to', move.toDesk)
        this.$set(moveParmas, 'timeOfMove', timeOfMove)
        if (move.toDesk.employee) {
          let department = this.getDepartmentFromEmployee(
            move.toDesk.employee.id
          )
          this.$set(moveParmas, 'employee', move.toDesk.employee)
          if (department) {
            this.$set(moveParmas, 'department', department)
          }
        }
      }
      this.$set(moveParmas, 'scheduledTime', null)
      this.$set(moveParmas, 'moveType', 1)
      this.$set(moveParmas, 'siteId', this.siteId)
      return moveParmas
    },
    mapLoaded(event) {
      //console.log('mapload fired')
      this.init() //to do . avoid consuming this.map reference before mappromise resolves(ie mapbpox comp loads)

      this.map = event.map //map object is referred evenb before mapload //to check
      this.onMapLoad = event.onMapLoad //this is a promise which will resolve when BaseMap component emits load event
    },

    getRandomColor() {
      let letters = '0123456789ABCDEF'
      let color = '#'
      for (let i = 0; i < 6; i++) {
        color += letters[Math.floor(Math.random() * 16)]
      }
      return color
    },
    handleAmenityFilter(array) {
      this.amenities = array
      this.loadViewerData()
    },
    resetAmenityFilter() {
      this.amenities = []
      this.loadViewerData()
    },
  },
}
</script>
<style scoped lang="scss">
.indoor-fp-mapbox-section {
  display: flex;
  height: 100%;
}
.indoor-floorplan-editor {
  height: 100%;
  display: flex;
  flex-direction: column;
}
</style>
<style>
.mapboxgl-ctrl-logo {
  display: none !important;
}
.indoor-fp-topbar {
  padding: 10px 15px;
  background: #fff;
  border-bottom: 1px solid #eee;
}
.dragElement {
  padding: 10px;
  border-radius: 20px;
  font-size: 12px;
  color: #fff;
}
.dragelemnt-innerContainer {
  display: inline-flex;
}
.drag-avatar {
  background: #545454ad;
  padding: 5px;
  border-radius: 20px;
  text-transform: uppercase;
}
.drag-label {
  padding: 5px;
  padding-left: 10px;
  font-size: 13px;
}
.mapboxgl-popup-content {
  /* background: #464444;
  color: #fff; */
  padding: 0px;
  /* overflow: hidden  !important;  */
  border-radius: 10px;
  /* DONT CHANGE PADDING HERE, set in content container if needed */
}

.mapboxgl-popup-tip {
  /* border-top-color: #464444 !important;
  border-bottom-color: #464444 !important; */
  /* box-shadow: 0 1px 7px 0 rgba(0, 0, 0, 0.15); */
}
</style>
