<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import { getBaseURL } from 'util/baseUrl'
import mapboxgl from 'mapbox-gl'
import MapboxDraw from '@mapbox/mapbox-gl-draw'
// import { extendDrawBar } from './CustomDraw.js'
import styles from 'pages/indoorFloorPlan/components/drawStyles.json'
export default {
  props: ['id', 'visibile'],
  data() {
    return {
      geoJson: {},
      indoorFloorPlan: null,
      markers: {},
      zones: {},
      indoorFloorPlanObjects: null,
      loading: true,
      ready: false,
      draw: null,
      selectedMarkerType: null,
      deskMarkerSelected: false,
      saveButn: true,
      selectedElementIndex: -1,
      selectedElement: null,
      canMove: true,
      propertieVisibile: false,
      markerTypes: [],
      selectedZone: null,
      spaceMapDialog: false,
      spaces: [],
      numbering: [],
      selectedNumbering: null,
      deletedMarkersList: [],
      deletedZonesList: [],
      editorProperties: false,
      spaceCategories: [],
      departments: [],
      floorDetails: null,
      imageBounds: {},
      rawMarkers: [],
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
        Parking: 'Parking.png',
        Restroom: 'Restroom.png',
        Handicap: 'Handicap.png',
        Limited: 'Limited.png',
      },
      rawDesks: {},
    }
  },
  computed: {
    iconSize() {
      if (this.indoorFloorPlan && this.indoorFloorPlan.iconSize) {
        return this.indoorFloorPlan.iconSize
      }
      return 1
    },
    fontSize() {
      if (this.indoorFloorPlan && this.indoorFloorPlan.fontSize) {
        return this.indoorFloorPlan.fontSize
      }
      return 10
    },
    minZoom() {
      if (this.indoorFloorPlan && this.indoorFloorPlan.minZoom) {
        return this.indoorFloorPlan.minZoom
      }
      return 16
    },
    maxZoom() {
      if (this.indoorFloorPlan && this.indoorFloorPlan.maxZoom) {
        return this.indoorFloorPlan.maxZoom
      }
      return 22
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
    defaultMarkerIdVsName() {
      let data = {}
      this.defaultMarkerTypes.forEach(rt => {
        this.$set(data, rt.id, rt.name)
      })
      return data
    },
    defaultMarkerNameVsId() {
      let data = {}
      this.defaultMarkerTypes.forEach(rt => {
        this.$set(data, rt.name, rt.id)
      })
      return data
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
    spaceCategoryIdVsNameMap() {
      let data = {}
      this.spaceCategories.forEach(rt => {
        this.$set(data, rt.id, rt.name)
      })
      return data
    },
    deskCategoryId() {
      let deskCategory = this.spaceCategories.find(rt => rt.name === 'Desk')
      if (deskCategory) {
        return deskCategory.id
      }
      return null
    },
    floorplanId() {
      if (this.id) {
        return this.id
      } else if (this.$route && this.$route.params.floorplanid) {
        return parseInt(this.$route.params.floorplanid)
      }
      return null
    },
  },
  methods: {
    updateZone(zone) {
      if (zone.properties) {
        Object.keys(zone.properties).forEach(key => {
          this.draw.setFeatureProperty(zone.id, key, zone.properties[key])
        })
        this.syncData()
      }
    },
    closeSpacePropertieDialog() {
      this.spaceMapDialog = false
      this.selectedZone = null
    },
    move() {
      this.canMove = !this.canMove
    },
    getPreviewUrl(fileId) {
      return `${getBaseURL()}/v2/files/preview/${fileId}?fetchOriginal=true`
    },
    deleteFeature() {
      let { selectedElementIndex, selectedElement, markers } = this
      if (selectedElementIndex > -1 && selectedElement) {
        markers.features.splice(selectedElementIndex, 1)
        this.refrshMarkerJson()
      }
    },
    updateMarker(properties) {
      let { selectedElementIndex, markers } = this
      if (selectedElementIndex > -1 && properties) {
        markers.features[selectedElementIndex].properties = properties
        this.refrshMarkerJson()
      }
    },
    refrshMarkerJson() {
      this.map.getSource('marker').setData(this.markers)
      this.selectedElementIndex = -1
      this.selectedElement = null
    },
    addControlLayer(map) {
      let self = this
      this.draw = new MapboxDraw({
        userProperties: false,
        displayControlsDefault: false,
        styles: styles,
        controls: {},
      })
      let { draw } = this

      // let drawBar = new extendDrawBar({
      //   draw: draw,
      //   buttons: [
      //     {
      //       on: 'click',
      //       action: e => {
      //         this.move(e)
      //       },
      //       classes: ['fa', 'fa-arrows'],
      //     },
      //   ],
      // })
      map.addControl(draw)
      this.draw.add(this.zones)
      map.on('draw.create', e => {
        this.selectedZone = null
        this.selectedZone = this.getFirstFeature(e)
        this.spaceMapDialog = true
      })
      map.on('draw.delete', deleteObject)
      map.on('draw.update', update)
      map.on('draw.selectionchange', e => {
        if (e.features && e.features.length) {
          let feat = e.features[0]
          this.selectedZone = feat
          this.spaceMapDialog = true
          this.draw.changeMode('direct_select', { featureId: feat.id })
          map.setLayoutProperty('space-text', 'visibility', 'none')
        } else {
          this.closeSpacePropertieDialog()
          map.setLayoutProperty('space-text', 'visibility', 'visible')
        }
      })
      function update() {
        self.objectUpdate(draw.getAll())
      }
      function deleteObject() {
        self.objectDelete(draw.getAll())
      }
    },
    // getSelectedZone() {
    //   let {draw, selectedZone} = this
    //   let selectedzone = draw.getSelected()
    //   if (selectedZone !== null) {
    //     let selectedFeatureId = this.getFirstFeatureId(selectedzone)
    //     if (selectedFeatureId === selectedZone.id) {
    //     }
    //   }
    //   else {
    //     this.objectSelected(this.getFirstFeature(selectedzone))
    //   }
    // },
    getFirstFeatureId(geoJson) {
      if (geoJson.features.length && geoJson.features[0]) {
        return geoJson.features[0].id
      }
      return null
    },
    getFirstFeature(geoJson) {
      return geoJson.features[0]
    },
    objectSelected(feature) {
      this.selectedZone = feature
    },
    objectCreate(data) {
      this.syncData(data)
    },
    objectUpdate(data) {
      this.syncData(data)
    },
    objectDelete(data) {
      this.syncData(data)
    },
    updateAndSaveNumbering() {
      let moduleName = 'floorplanmarkertypes'
      this.numbering.forEach(rt => {
        if (rt.id) {
          let params = {
            id: rt.id,
            data: rt,
          }
          API.updateRecord(moduleName, params)
        } else {
          API.createRecord(moduleName, { data: rt })
        }
      })
    },
    mergeDeleteMarkerList() {
      let { indoorFloorPlan, deletedMarkersList } = this
      if (indoorFloorPlan && indoorFloorPlan.markers) {
        indoorFloorPlan.markers = [
          ...indoorFloorPlan.markers,
          ...deletedMarkersList,
        ]
      }
    },
    async saveFloorPlan() {
      this.serializeGeoJson()
      this.deletedMarkersList = this.formatDeleteMarker()
      this.deletedZonesList = this.formatDeleteZone()
      // eslint-disable-next-line no-console
      console.log('delete', this.deletedMarkersList, this.deletedZonesList)
      this.mergeDeleteMarkerList()
      let params = {
        moduleName: 'indoorfloorplan',
        id: this.floorplanId,
        data: this.indoorFloorPlan,
      }
      let { error } = await API.post(`v3/modules/data/update`, params)
      if (error) {
        this.$message.error(error.message)
      } else {
        this.deleteMarkers()
        this.deleteZones()
        this.refreshData()
        this.updateAndSaveNumbering()
        this.$message.success('updated')
        this.closeFlooplanEditor()
      }
    },
    deleteMarkers() {
      if (this.deletedMarkersList.length) {
        let ids = this.deletedMarkersList.map(rt => rt.id)
        // let params = {
        //     data: {
        //       floorplanmarker: ids
        //     },
        //   }
        API.deleteRecord('floorplanmarker', ids)
      }
    },
    deleteZones() {
      if (this.deletedZonesList.length) {
        let ids = this.deletedZonesList.map(rt => rt.id)
        // let params = {
        //     data: {
        //       floorplanmarkedzone: ids
        //     },
        //   }
        API.deleteRecord('floorplanmarkedzone', ids)
      }
    },
    async getNumberingList() {
      let params = {
        moduleName: 'floorplanmarkertypes',
      }
      return await API.get(`v3/modules/data/list`, params)
    },
    async getSpaceCatgory() {
      let params = {
        page: 1,
        perPage: 200,
        includeParentFilter: true,
        moduleName: 'spacecategory',
      }
      let { data } = await API.get(`v3/modules/data/list`, params)
      if (data) {
        this.spaceCategories = data.spacecategory
      }
    },
    refreshData() {
      this.getFloorplan().then(({ data, error }) => {
        if (error) {
          this.$message.error(error.message)
        }
        this.indoorFloorPlan = data.indoorfloorplan
        this.deSerializeGeoJson(this.indoorFloorPlan)
      })
    },
    async getFloorplan() {
      let params = {
        moduleName: 'indoorfloorplan',
        id: this.floorplanId,
      }
      return await API.post(`v3/modules/data/summary`, params)
    },
    loadFloorplanImage(map) {
      let imageUrl = ''
      let { fileId } = this.indoorFloorPlan
      imageUrl = this.isDev
        ? `${window.location.origin}/statics/floorplan.png`
        : `${getBaseURL()}/v2/files/preview/${fileId}?fetchOriginal=true`
      const img = new Image()
      let self = this
      img.onload = function() {
        let w = this.width,
          h = this.height,
          cUL = map.unproject([0, 0]).toArray(),
          cUR = map.unproject([w, 0]).toArray(),
          cLR = map.unproject([w, h]).toArray(),
          cLL = map.unproject([0, h]).toArray()
        let coordinates = [cUL, cUR, cLR, cLL]

        if (self.indoorFloorPlan.geometry) {
          let geometry = JSON.parse(self.indoorFloorPlan.geometry)
          if (geometry.coordinates) {
            coordinates = geometry.coordinates
          }
        } else {
          self.indoorFloorPlan.geometry = JSON.stringify({
            coordinates: coordinates,
          })
        }

        self.setImageBounds(coordinates)

        map.addSource(
          'ImageSource',
          {
            type: 'image',
            url: imageUrl,
            coordinates: coordinates,
          },
          { pixelRatio: 2 }
        )

        map.addLayer({
          id: 'ImageLayer',
          source: 'ImageSource',
          type: 'raster',
          paint: {
            'raster-opacity': 0.7,
          },
        })

        let bd = coordinates.reduce(function(bid, coord) {
          return bid.extend(coord)
        }, new mapboxgl.LngLatBounds(coordinates[0], coordinates[0]))

        // need to set the mapbox fitbounds zoom level

        // let minZoom = map.getZoom() - 2
        // let maxZoom = map.getZoom() + 2
        // self.minZoom = minZoom
        // self.maxZoom = maxZoom
        // map.setMinZoom(minZoom)
        // map.setMaxZoom(maxZoom)

        map.fitBounds(bd, {
          padding: 100,
        })

        self.loadObjects(map)
      }
      img.src = imageUrl
    },
    setImageBounds(coordinates) {
      let { imageBounds } = this
      this.$set(imageBounds, 'blLng', coordinates[3][0]) // bottom-left lng
      this.$set(imageBounds, 'blLat', coordinates[3][1]) // bottom-left lat
      this.$set(imageBounds, 'trLng', coordinates[1][0]) // top-right lng
      this.$set(imageBounds, 'trLat', coordinates[1][1]) // top-right lat
    },
    getNumbering(marker) {
      return this.numbering.find(rt => {
        if (rt.markerType && rt.markerType.id === marker.id) {
          return rt
        }
        return null
      })
    },
    getNumberingCode(numbering, selectedMarkerType) {
      if (numbering) {
        let { prefix, suffix, currentNumber } = numbering
        let code = ''
        if (prefix) {
          code += prefix
        }
        if (currentNumber) {
          code += currentNumber
        }
        if (suffix) {
          code += suffix
        }

        return code
      } else {
        return selectedMarkerType ? selectedMarkerType.name : ''
      }
    },
    getSpaceCatgoryfromSpace(space) {
      if (space && space.spaceCategory && space.spaceCategory.id) {
        if (this.spaceCategoryIdVsNameMap[space.spaceCategory.id]) {
          return this.spaceCategoryIdVsNameMap[space.spaceCategory.id]
        }
      }
      return null
    },
    deSerializeGeoJson(floorplanObj) {
      let { markedZones, markers } = floorplanObj
      let deskMarkerType = this.getDeskMarkerType()
      let features = []
      if (!isEmpty(markedZones)) {
        markedZones.forEach(zone => {
          let feature = {}
          feature['geometry'] = JSON.parse(zone.geometry)
          feature['properties'] = JSON.parse(zone.properties)
          feature.type = zone.type
          feature.geoId = zone.geoId
          feature.id = zone.id
          feature.space = zone.space
          feature.label = zone.label
          if (zone.markerType) {
            feature.markerType = zone.markerType
            this.$set(feature.properties, 'markerId', zone.markerType.id)
          }
          if (zone.space && zone.space.id) {
            this.$set(feature, 'space', zone.space)
            feature.space = zone.space
            this.$set(feature.properties, 'isReservable', zone.isReservable)

            // let reservable = zone.space.reservable || false
            // this.$set(feature.properties, 'isReservable', reservable)
            this.$set(feature.properties, 'spaceId', zone.space.id)
            this.$set(
              feature.properties,
              'spaceCategory',
              this.getSpaceCatgoryfromSpace(zone.space)
            )
          }
          if (zone.zoneModuleId) {
            this.$set(feature.properties, 'zoneModuleId', zone.zoneModuleId)
          }
          if (zone.recordId) {
            this.$set(feature.properties, 'recordId', zone.recordId)
          }
          if (zone.label) {
            this.$set(feature.properties, 'label', zone.label)
          }
          this.$set(feature.properties, 'id', zone.id)
          features.push(feature)
        })
        this.zones = this.convertGeoJson(features)
      } else {
        this.zones = this.convertGeoJson([])
      }
      if (!isEmpty(markers)) {
        features = []
        markers.forEach(marker => {
          let feature = {}
          feature['geometry'] = JSON.parse(marker.geometry)
          feature['properties'] = JSON.parse(marker.properties)
          feature.type = marker.type
          feature.geoId = marker.geoId
          feature.label = marker.label
          feature.id = marker.id
          if (marker.desk) {
            feature.desk = marker.desk
            this.$set(this.rawDesks, marker.desk.id, marker.desk)
            this.$set(feature.properties, 'deskCode', marker.desk.deskCode)
            this.$set(feature.properties, 'deskId', marker.desk.id)
            if (marker.desk && marker.desk.employee) {
              this.$set(
                feature.properties,
                'employeeId',
                marker.desk.employee.id
              )
            }
            if (marker.desk && marker.desk.deskType) {
              this.$set(feature.properties, 'deskType', marker.desk.deskType)
            }
            if (marker.desk && marker.desk.department) {
              this.$set(
                feature.properties,
                'departmentId',
                marker.desk.department.id
              )
            }
          }
          if (marker.markerType) {
            feature.markerType = marker.markerType
            if (marker.markerType.id === deskMarkerType.id) {
              this.$set(feature.properties, 'normalClass', 'desk')
              this.$set(feature.properties, 'markerId', 'desk')
              this.$set(feature.properties, 'activeClass', 'desk_active')

              this.$set(feature.properties, 'isCustom', true)
            } else if (this.defaultMarkerIdVsName[marker.markerType.id]) {
              let name = this.defaultMarkerIdVsName[marker.markerType.id]
              this.$set(feature.properties, 'normalClass', name)
              this.$set(feature.properties, 'markerId', name)
              this.$set(feature.properties, 'activeClass', `${name}`)

              this.$set(feature.properties, 'isCustom', true)
            } else {
              this.$set(feature.properties, 'normalClass', marker.markerType.id)
              this.$set(feature.properties, 'markerId', marker.markerType.id)
              this.$set(feature.properties, 'activeClass', marker.markerType.id)
            }
          }
          if (marker.markerModuleId) {
            this.$set(
              feature.properties,
              'markerModuleId',
              marker.markerModuleId
            )
          }
          if (marker.recordId) {
            this.$set(feature.properties, 'recordId', marker.recordId)
          }
          if (marker.label) {
            this.$set(feature.properties, 'label', marker.label)
          }
          this.$set(feature.properties, 'id', marker.id)

          features.push(feature)
        })
        this.markers = this.convertGeoJson(features)
      } else {
        this.markers = this.convertGeoJson([])
      }
    },
    convertGeoJson(data) {
      return {
        type: 'FeatureCollection',
        features: data,
      }
    },
    getDeskMarkerType() {
      return this.markerTypes.find(rt => rt.name === 'desk')
    },
    formatDeleteZone() {
      let markedZones = []
      if (this.deletedZonesList && this.deletedZonesList.length) {
        this.deletedZonesList.forEach(feature => {
          markedZones.push(this.formatedZones(feature))
        })
      }
      return markedZones
    },
    formatDeleteMarker() {
      let markers = []
      if (this.deletedMarkersList && this.deletedMarkersList.length) {
        this.deletedMarkersList.forEach(feature => {
          markers.push(this.formatedMarkers(feature))
        })
      }
      return markers
    },
    formatedZones(feature) {
      let localObj = {}
      localObj['geometry'] = JSON.stringify(feature.geometry)
      localObj['properties'] = JSON.stringify({})
      localObj.type = feature.type
      localObj.indoorfloorplan = {
        id: this.indoorFloorPlan.id,
      }
      if (typeof feature.id === 'number') {
        localObj.id = feature.id
      } else if (typeof feature.id === 'string') {
        localObj.geoId = feature.id
      }
      if (feature.properties.geoId) {
        localObj.geoId = feature.properties.geoId
      }
      if (feature.properties.spaceId) {
        localObj.space = {
          id: feature.properties.spaceId,
          reservable: feature.properties.isReservable || false,
        }
      }
      localObj.isReservable = feature.properties.isReservable || false

      if (feature.properties.markerId) {
        localObj.markerType = {
          id: feature.properties.markerId,
        }
      }
      if (feature.properties.zoneModuleId) {
        localObj.zoneModuleId = feature.properties.zoneModuleId
      }
      //      else if (feature.properties.markerModuleId) {
      //   localObj.markerModuleId = feature.properties.markerModuleId
      // }
      if (feature.properties.recordId) {
        localObj.recordId = feature.properties.recordId
      }
      if (feature.properties.label) {
        localObj.label = feature.properties.label
      }

      return localObj
    },
    formatedMarkers(feature) {
      let deskMarkerType = this.getDeskMarkerType()
      let localObj = {}
      localObj['geometry'] = JSON.stringify(feature.geometry)
      localObj['properties'] = JSON.stringify({})
      localObj.type = feature.type
      localObj.indoorfloorplan = {
        id: this.indoorFloorPlan.id,
      }
      if (typeof feature.id === 'number') {
        localObj.id = feature.id
      } else if (typeof feature.id === 'string') {
        localObj.geoId = feature.id
      }
      if (feature.properties.geoId) {
        localObj.geoId = feature.properties.geoId
      }
      if (
        feature.properties &&
        feature.properties.isCustom &&
        feature.properties.markerId &&
        (feature.properties.markerId === 'desk' ||
          feature.properties.markerId === 'desk_active')
      ) {
        localObj.desk = this.propertiesTODesk(feature.properties)
      }
      if (
        (feature.properties.markerId === 'desk' ||
          feature.properties.markerId === 'desk_active') &&
        feature.properties.isCustom &&
        deskMarkerType
      ) {
        localObj.markerType = {
          id: deskMarkerType.id,
        }
      } else if (this.defaultMarkerNameVsId[feature.properties.markerId]) {
        localObj.markerType = {
          id: this.defaultMarkerNameVsId[feature.properties.markerId],
        }
      } else if (feature.properties.markerId && !feature.properties.isCustom) {
        localObj.markerType = {
          id: feature.properties.markerId,
        }
      }

      if (feature.properties.recordModuleId) {
        localObj.markerModuleId = feature.properties.recordModuleId
      } else if (feature.properties.markerModuleId) {
        localObj.markerModuleId = feature.properties.markerModuleId
      }
      if (feature.properties.recordId) {
        localObj.recordId = feature.properties.recordId
      }
      if (feature.properties.label) {
        localObj.label = feature.properties.label
      }
      return localObj
    },
    serializeGeoJson() {
      let markedZones = []
      let markers = []
      if (this.zones.features && this.zones.features.length) {
        this.zones.features.forEach(feature => {
          markedZones.push(this.formatedZones(feature))
        })
      }
      if (this.markers.features && this.markers.features.length) {
        this.markers.features.forEach(feature => {
          markers.push(this.formatedMarkers(feature))
        })
      }
      this.$set(this.indoorFloorPlan, 'markedZones', markedZones)
      this.$set(this.indoorFloorPlan, 'markers', markers)
    },
    propertiesTODesk(properites) {
      if (properites) {
        let desk = {
          deskCode: properites.deskCode,
          deskType: properites.deskType,
          name: properites.label,
          spaceType: properites.spaceType,
          floor: {
            id: this.floorId,
          },
          spaceCategory: {
            id: this.deskCategoryId,
          },
          siteId: this.siteId,
          resourceType: 1,
          floorId: this.floorId,
          reservable: properites.deskType !== 1 ? true : false,
          isArchived: properites.isArchived || false,
        }

        if (properites.deskId) {
          // delete desk.name
          this.$set(desk, 'id', Number(properites.deskId))
        }
        if (properites.employeeId) {
          this.$set(desk, 'employee', { id: Number(properites.employeeId) })
        }
        if (properites.departmentId) {
          this.$set(desk, 'department', { id: Number(properites.departmentId) })
        }

        if (this.buildingId) {
          this.$set(desk, 'building', { id: this.buildingId })
        }

        if (desk?.id && this.rawDesks && this.rawDesks[desk.id]) {
          let rawDesk = this.rawDesks[desk.id]
          return { ...rawDesk, ...desk }
        } else {
          return desk
        }
      }
    },
    async fetchMarkerTypes() {
      let params = {
        moduleName: 'markertype',
      }
      return await API.get(`v3/modules/data/list`, params).then(({ data }) => {
        if (data) {
          this.markerTypes = data.markertype
        }
      })
    },
    async getDepartments() {
      let params = {
        moduleName: 'department',
      }
      return await API.get(`v2/module/data/list`, params).then(({ data }) => {
        if (data) {
          this.departments = data.moduleDatas
        }
      })
    },
    async fetchFloorDetails() {
      let params = { floorId: this.floorId }

      return await API.get(`v2/floor/details`, params).then(({ data }) => {
        if (data) {
          this.floorDetails = data.floor
        }
      })
    },
    addDeskIcon() {
      let { map } = this

      let size = 80
      let outerRadius = (size - 20) / 2
      let radius = (size / 3) * 0.5
      let active = size / 2 - 5

      // implementation of CustomLayerInterface to draw a pulsing dot icon on the map
      // see https://docs.mapbox.com/mapbox-gl-js/api/#customlayerinterface for more info
      let desk = {
        width: size,
        height: size,
        data: new Uint8Array(size * size * 4),

        // get rendering context for the map canvas when layer is added to the map
        onAdd: function() {
          let canvas = document.createElement('canvas')
          canvas.width = this.width
          canvas.height = this.height
          this.context = canvas.getContext('2d')
        },

        // called once before every frame where the icon will be used
        render: function() {
          let context = this.context

          // draw inner circle
          context.beginPath()
          context.arc(this.width / 2, this.height / 2, active, 0, Math.PI * 2)
          context.fillStyle = 'transparent'
          context.strokeStyle = 'transparent'
          context.lineWidth = 5
          context.fill()
          context.stroke()

          // draw inner circle
          context.beginPath()
          context.arc(
            this.width / 2,
            this.height / 2,
            outerRadius,
            0,
            Math.PI * 2
          )
          context.fillStyle = '#000'
          context.strokeStyle = 'white'
          context.lineWidth = 2
          context.fill()
          context.stroke()

          // draw inner circle
          context.beginPath()
          context.arc(this.width / 2, this.height / 2, radius, 0, Math.PI * 2)
          context.fillStyle = '#fff'
          context.fill()

          // update this image's data with data from the canvas
          this.data = context.getImageData(0, 0, this.width, this.height).data

          // continuously repaint the map, resulting in the smooth animation of the dot
          // map.triggerRepaint();

          // return `true` to let the map know that the image was updated
          return true
        },
      }

      let deskactive = {
        width: size,
        height: size,
        data: new Uint8Array(size * size * 4),

        // get rendering context for the map canvas when layer is added to the map
        onAdd: function() {
          let canvas = document.createElement('canvas')
          canvas.width = this.width
          canvas.height = this.height
          this.context = canvas.getContext('2d')
        },

        // called once before every frame where the icon will be used
        render: function() {
          let context = this.context

          // draw inner circle
          context.beginPath()
          context.arc(this.width / 2, this.height / 2, active, 0, Math.PI * 2)
          context.fillStyle = 'transparent'
          context.strokeStyle = 'green'
          context.lineWidth = 5
          context.fill()
          context.stroke()

          // draw outer circle
          context.beginPath()
          context.arc(
            this.width / 2,
            this.height / 2,
            outerRadius,
            0,
            Math.PI * 2
          )
          context.fillStyle = '#000'
          context.strokeStyle = 'white'
          context.lineWidth = 2
          context.fill()
          context.stroke()

          // draw inner circle
          context.beginPath()
          context.arc(this.width / 2, this.height / 2, radius, 0, Math.PI * 2)
          context.fillStyle = '#fff'
          context.fill()

          // update this image's data with data from the canvas
          this.data = context.getImageData(0, 0, this.width, this.height).data

          // continuously repaint the map, resulting in the smooth animation of the dot
          // map.triggerRepaint();

          // return `true` to let the map know that the image was updated
          return true
        },
      }

      map.addImage('desk_active', deskactive, { pixelRatio: 2 })
      map.addImage('desk', desk, { pixelRatio: 2 })
    },
  },
}
</script>
