<template>
  <div
    v-if="ready"
    class="indoor-floorplan-editor fc-indoor-floorplan-editor-view"
    id="editor"
  >
    <div id="dragMarkerItem" style="display: none" class="dragElement"></div>
    <div id="dragSpaceItem" style="display: none" class="dragElement"></div>
    <div class="dragElemntClose" v-if="selectedMarkerType">
      <div class="width100 flex">
        <div class="fc-dragelement-txt pR5">
          {{ `You are adding ${selectedMarkerType.name}, click to ` }}
        </div>
        <div class="">
          <el-button
            size="medium"
            class="pull-right fc-dragelement-btn"
            @click="closeDrgaMarker"
            >Stop</el-button
          >
        </div>
      </div>
    </div>
    <top-bar
      @refresh="refresh"
      @save="saveFloorPlan"
      @addNumber="setNumber()"
      :saveButn="saveButn"
      :indoorFloorPlan="indoorFloorPlan"
      :floor="floor"
      :building="building"
      @close="closeFlooplanEditor"
    ></top-bar>
    <div class="indoor-fp-mapbox-section">
      <side-bar
        @markerType="getMarker"
        :selectedMarkerType.sync="selectedMarkerType"
        :markers="markerTypes"
        :markerMappedDesk="markerMappedDesk"
        :isDraging.sync="isDraging"
        :deskDraging.sync="deskDraging"
        :floorId="floorId"
        @dragElement="handlemouseDownElement"
        @deskDrag="handleDeskDrag"
        @closeElement="destroyDragEement"
        :zoneMapedSpaceId="zoneMapedSpaceId"
        style="width: 16%;"
      ></side-bar>
      <div
        id="drop1"
        v-if="dropZone"
        v-on:dragover="allowDrop"
        v-on:drop="drop"
      ></div>
      <base-map
        :maxZoom="22"
        :zoom="20"
        :minZoom="10"
        style="width: 84%;"
        @mapLoaded="mapLoaded"
      />
      <properties-dialog
        v-if="propertieVisibile"
        :visible.sync="propertieVisibile"
        :properties="selectedElement.properties"
        :selectedElement="selectedElement"
        :departments="departments"
        :markers="rawMarkers"
        @close="closeDialog"
        @update="updateMarker"
        @delete="deleteMarker()"
      ></properties-dialog>
      <SpaceMapDialog
        v-if="spaceMapDialog"
        :visible.sync="spaceMapDialog"
        :selectedZone="selectedZone"
        @update="updateZone"
        @close="closeSpacePropertieDialog"
        @delete="deleteZone"
      ></SpaceMapDialog>
    </div>
  </div>
</template>

<script>
import actionMixin from 'pages/indoorFloorPlan/mixins/EditorActions.vue'
import topBar from 'pages/indoorFloorPlan/components/IndoorFloorPlanTopBar.vue'
import SideBar from 'pages/indoorFloorPlan/components/IndoorFloorPlanSideBar.vue'
import BaseMap from 'pages/indoorFloorPlan/components/MapBoxBase.vue'
import PropertiesDialog from 'pages/indoorFloorPlan/components/PropertiesDialog.vue'
import SpaceMapDialog from 'pages/indoorFloorPlan/components/SpaceMapDialog.vue'
import { getBaseURL } from 'util/baseUrl'

// import { eventBus } from '@/page/widget/utils/eventBus'
export default {
  mixins: [actionMixin],
  data() {
    return {
      dropZone: false,
      isDraging: false,
      deskDraging: false,
      dragSpaceElement: null,
      drgaDeskElement: null,
      clickEvents: {},
    }
  },
  computed: {
    zoneMapedSpaceId() {
      let { zones } = this
      let spaceIds = []
      if (zones && zones.features.length) {
        zones.features.forEach(feature => {
          if (feature.properties && feature.properties.spaceId) {
            spaceIds.push(feature.properties.spaceId)
          }
        })
      }
      return spaceIds
    },
    markerMappedDesk() {
      let { markers } = this
      let deskIds = []
      if (markers && markers.features.length) {
        markers.features.forEach(feature => {
          let { desk } = feature
          if (desk) {
            deskIds.push(desk.id)
          }
        })
      }
      return deskIds
    },
    isDev() {
      const isDevMode = process.env.NODE_ENV === 'development'
      return isDevMode
    },
    floorId() {
      if (this.indoorFloorPlan.floor && this.indoorFloorPlan.floor.id) {
        return this.indoorFloorPlan.floor.id
      }
      return null
    },
    buildingId() {
      if (this.indoorFloorPlan.building && this.indoorFloorPlan.building.id) {
        return this.indoorFloorPlan.building.id
      }
      return null
    },
    siteId() {
      if (this.indoorFloorPlan.site && this.indoorFloorPlan.site.id) {
        return this.indoorFloorPlan.site.id
      }
      return null
    },
  },
  components: {
    topBar,
    BaseMap,
    SideBar,
    PropertiesDialog,
    SpaceMapDialog,
  },
  mounted() {
    this.getSpaceCatgory()
    Promise.all([this.fetchMarkerTypes()]).then(() => {
      this.getFloorplan().then(({ data, error }) => {
        if (error) {
          this.$message.error(error.message)
        }
        this.indoorFloorPlan = data.indoorfloorplan

        this.deSerializeGeoJson(this.indoorFloorPlan)
        this.rawMarkers = this.indoorFloorPlan.markers || []
        this.fetchFloorDetails()
        this.getDepartments()
        this.ready = true
        this.$nextTick(() => {
          this.addEventListenerevents()
        })
        this.getNumberingList().then(response => {
          if (response.data) {
            this.numbering = response.data.floorplanmarkertypes.filter(
              rt => rt.indoorfloorplan.id === this.floorplanId
            )
          }
        })
      })
    })
  },
  methods: {
    closeFlooplanEditor() {
      this.$emit('update:visibile', false)
      this.$emit('close')
    },
    deleteZone() {
      let { selectedZone, zones, deletedZonesList } = this
      if (selectedZone.id) {
        deletedZonesList.push(selectedZone)
        let index = zones.features.findIndex(rt => rt.id === selectedZone.id)
        zones.features.splice(index, 1)
        this.draw.deleteAll()
        this.draw.add(zones)
        this.syncData()
      }
      this.closeSpacePropertieDialog()
    },
    deleteMarker() {
      let {
        selectedElementIndex,
        selectedElement,
        markers,
        deletedMarkersList,
      } = this
      if (selectedElementIndex > -1 && selectedElement) {
        if (selectedElement.id) {
          this.$set(selectedElement.properties, 'isArchived', true)
          deletedMarkersList.push(selectedElement)
        }
        markers.features.splice(selectedElementIndex, 1)
        this.refrshMarkerJson()
      }
      this.closeDialog()
    },
    addZoneLabelLayer(map) {
      map.addSource('zoneLabel', {
        type: 'geojson',
        data: this.zones,
      })

      map.addLayer({
        id: 'space-text',
        type: 'symbol',
        source: 'zoneLabel',
        layout: {
          'icon-allow-overlap': true,
          'text-field': `{label}`,
          'text-font': ['Open Sans Bold', 'Arial Unicode MS Bold'],
          'text-size': ['interpolate', ['linear'], ['zoom'], 16, 1, 20, 14],
          'text-transform': 'uppercase',
          'text-letter-spacing': 0.05,
          'text-offset': [0, 1.5],
        },
        paint: {
          'text-color': '#202',
          'text-halo-color': '#fff',
          'text-halo-width': 2,
        },
        filter: ['all', ['in', '$type', 'Polygon']],
      })
    },
    destroyDragEement() {
      this.isDraging = false
      this.deskDraging = false
      this.dragSpaceElement = null
      this.drgaDeskElement = null
      this.hideElement()
      this.onselectEnable()
    },
    onselectPrevent() {
      document.onselectstart = () => {
        return false // cancel selection
      }
    },
    onselectEnable() {
      document.onselectstart = () => {
        return true // cancel selection
      }
    },
    handlemouseDownElement(e, data) {
      this.isDraging = true
      this.dragSpaceElement = data
      this.createElemnt(e)
      this.onselectPrevent()
    },
    handleDeskDrag(e, desk) {
      this.deskDraging = true
      this.drgaDeskElement = desk
      this.displayDragElement(e)
      this.onselectPrevent()
    },
    createElemnt(e) {
      if (!e.pageX && !e.pageY) return
      let color = '#fff'
      let div = document.getElementById('dragSpaceItem')
      div.style.position = 'absolute'
      div.style.zIndex = '20'
      div.style.top = '0px'
      div.style.color = '#000'
      div.style.display = 'block'
      ;(div.innerHTML = `<div class="dragelemnt-innerContainer">
      <div class="drag-avatar">${this.dragSpaceElement.name.charAt(
        0
      )}${this.dragSpaceElement.name.charAt(1)}</div>
      <div class="drag-label">${this.dragSpaceElement.name}</div>
      </div>`),
        (div.style.transform = `translate(${e.pageX + 10}px,${e.pageY}px)`) // Insert text
      div.style.background = color
      document.body.appendChild(div)
    },
    ghostElemntHandle(e) {
      if (!e.pageX && !e.pageY) return
      let div = document.getElementById('dragSpaceItem')
      div.style.display = 'block'
      div.style.transform = `translate(${e.pageX + 10}px,${e.pageY}px)`
    },
    closeDrgaMarker() {
      this.selectedMarkerType = null
      this.selectedNumbering = null
      this.hideElement()
    },
    setNumber() {},
    hideElement() {
      let marker = document.getElementById('dragMarkerItem')
      let space = document.getElementById('dragSpaceItem')
      marker.innerHTML = ''
      space.innerHTML = ''
      marker.style.display = 'none'
      space.style.display = 'none'
    },
    mapAddImages() {
      let map = this.map
      let size = 100
      let self = this
      this.markerTypes.forEach(type => {
        let pulsingDot = {
          width: size,
          height: size,
          data: new Uint8Array(size * size * 4),

          // get rendering context for the map canvas when layer is added to the map
          onAdd: function() {
            let imageCanvas = document.createElement('canvas')
            imageCanvas.width = this.width
            imageCanvas.height = this.height
            this.context = imageCanvas.getContext('2d')
          },

          // called once before every frame where the icon will be used
          render: function() {
            let context = this.context

            let img = new Image() // Create new img element
            let url = ''
            if (type.fileId) {
              url = self.getPreviewUrl(type.fileId)
            }

            if (self.isDev) {
              url =
                'https://upload.wikimedia.org/wikipedia/commons/7/7c/201408_cat.png'
            }

            img.src = url
            img.setAttribute('crossOrigin', '')

            img.onload = function() {
              //https://www.nashvail.me/blog/canvas-image refre this link for more details
              context.drawImage(
                this,
                0,
                0,
                this.width,
                this.height,
                25,
                25,
                50,
                50
              )
            }

            // update this image's data with data from the canvas
            this.data = context.getImageData(0, 0, this.width, this.height).data

            // return `true` to let the map know that the image was updated
            return true
          },
        }
        map.addImage(type.id, pulsingDot, {
          pixelRatio: 2,
        })
      })

      this.defaultMarkerTypes.forEach(marker => {
        let defaultIcons = {
          width: size,
          height: size,
          data: new Uint8Array(size * size * 4),

          // get rendering context for the map canvas when layer is added to the map
          onAdd: function() {
            let imageCanvas = document.createElement('canvas')
            imageCanvas.width = this.width
            imageCanvas.height = this.height
            this.context = imageCanvas.getContext('2d')
          },

          // called once before every frame where the icon will be used
          render: function() {
            let context = this.context

            let img = new Image() // Create new img element
            let url = ''
            if (marker.name) {
              url = self.pathIcon(marker.icon)
            }

            // if (self.isDev) {
            //   url =
            //     'https://upload.wikimedia.org/wikipedia/commons/7/7c/201408_cat.png'
            // }
            img.src = url
            img.setAttribute('crossOrigin', '')

            img.onload = function() {
              //https://www.nashvail.me/blog/canvas-image refre this link for more details
              context.drawImage(
                this,
                0,
                0,
                this.width,
                this.height,
                25,
                25,
                50,
                50
              )
            }

            // update this image's data with data from the canvas
            this.data = context.getImageData(0, 0, this.width, this.height).data

            // return `true` to let the map know that the image was updated
            return true
          },
        }
        map.addImage(marker.name, defaultIcons, {
          pixelRatio: 2,
        })
      })
    },
    closeDialog() {
      this.propertieVisibile = false
      this.selectedElement = null
      this.selectedElementIndex = null
      this.deActiveSelectedElement()
    },
    addMarkerLayer(map) {
      this.mapAddImages()
      this.addDeskIcon()

      map.addSource('marker', {
        type: 'geojson',
        data: this.markers,
      })
      // this.geojson.features.forEach(ft => {
      let id = `deskMarker`
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
            0.2,
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
        filter: ['all', ['in', '$type', 'Point']],
      })

      let canvas = map.getCanvasContainer()
      let self = this

      function onMove(e) {
        let coords = e.lngLat
        // Set a UI indicator for dragging.
        canvas.style.cursor = 'grabbing'

        if (this.selectedElement && self.canMove) {
          let { markers } = self
          if (this.selectedElementIndex > -1) {
            markers.features[this.selectedElementIndex].geometry.coordinates = [
              coords.lng,
              coords.lat,
            ]
            map.getSource('marker').setData(markers)
          }
        }
      }

      function onUp() {
        if (this.selectedElement) {
          map.off('mousemove', onMove)
          map.off('touchmove', onMove)
        }
      }

      // When the cursor enters a feature in the point layer, prepare for dragging.
      map.on('mouseenter', id, function(e) {
        let point = map.queryRenderedFeatures(e.point)
        let element = point ? point[0] : null
        if (element && self.selectedElement) {
          if (element.id === self.selectedElement.id) {
            canvas.style.cursor = 'move'
          } else {
            canvas.style.cursor = 'pointer'
          }
        } else {
          canvas.style.cursor = 'pointer'
        }
      })

      map.on('mouseleave', id, function() {
        // map.setPaintProperty(id, 'circle-color', '#3887be')
        canvas.style.cursor = ''
      })

      map.on('mouseup', () => {
        this.$set(this.clickEvents, 'mouseup', true)
      })

      map.on('mousedown', id, function(e) {
        self.$set(self.clickEvents, 'mousedown', true)

        // Prevent the default map drag behavior.
        self.deActiveSelectedElement()
        self.closeDrgaMarker()
        e.preventDefault()

        let inside = true
        inside = self.findPoint(e.lngLat)
        if (!inside) {
          self.closeDrgaMarker()
          return
        }

        let point = map.queryRenderedFeatures(e.point)
        if (self.canMove) {
          this.selectedElement = point ? point[0] : null
        }
        self.deActiveSelectedElement()
        self.selectedElement = point ? point[0] : null
        if (this.selectedElement) {
          self.selectedElement = this.selectedElement
          let { markers } = self
          let layerId = this.selectedElement.properties.id
          // eslint-disable-next-line array-callback-return
          this.selectedElementIndex = markers.features.findIndex(rt => {
            if (rt.properties.id) {
              if (rt.properties.id === layerId) {
                return rt
              }
            }
          })
        }

        if (self.selectedElement) {
          let { markers } = self
          let layerId = self.selectedElement.properties.id
          // eslint-disable-next-line array-callback-return
          self.selectedElementIndex = markers.features.findIndex(rt => {
            if (rt.properties.id) {
              if (rt.properties.id === layerId) {
                return rt
              }
            }
          })
          self.propertieVisibile = true
          self.activeSelectedElement()
        }

        canvas.style.cursor = 'grab'

        map.on('mousemove', onMove)
        map.once('mouseup', onUp)
      })

      map.on('touchstart', id, function(e) {
        if (e.points.length !== 1) return

        // Prevent the default map drag behavior.
        e.preventDefault()

        map.on('touchmove', onMove)
        map.once('touchend', onUp)
      })
      // })
    },
    activeSelectedElement() {
      let { selectedElement, markers, selectedElementIndex } = this
      if (
        selectedElement.properties &&
        selectedElement.properties.activeClass
      ) {
        selectedElement.properties.markerId = this.$helpers.cloneObject(
          selectedElement.properties.activeClass
        )
      }

      markers.features[selectedElementIndex] = selectedElement
      this.refrshMarkerJson()
    },
    deActiveSelectedElement() {
      let { markers } = this
      markers.features.forEach(feature => {
        let { properties } = feature
        if (properties.normalClass) {
          properties['markerId'] = properties.normalClass
        }
      })

      this.refrshMarkerJson()
    },
    addPolygon(e) {
      let id = Math.random()
        .toString(36)
        .substring(10)

      let map = this.map

      let point = e.point
      let x = point.x
      let y = point.y

      let w = 100,
        h = 100,
        cUL = map.unproject([x - w / 2, y - h / 2]).toArray(),
        cUR = map.unproject([x + w / 2, y - h / 2]).toArray(),
        cLR = map.unproject([x + w / 2, y + h / 2]).toArray(),
        cLL = map.unproject([x - w / 2, y + h / 2]).toArray()
      let coordinates = [cUL, cUR, cLR, cLL, cUL]

      let poly = {
        id: id,
        type: 'Feature',
        properties: {
          label: this.dragSpaceElement.name,
          spaceId: this.dragSpaceElement.id,
          zoneModuleId: this.dragSpaceElement.moduleId,
          recordId: this.dragSpaceElement.id,
        },
        geometry: {
          coordinates: [coordinates],
          type: 'Polygon',
        },
      }
      this.draw.add(poly)
      this.draw.changeMode('direct_select', {
        featureId: id,
      })
      this.syncData()
    },
    findPoint(lngLat) {
      let { lat, lng } = lngLat
      let { imageBounds } = this
      let { blLat, blLng, trLat, trLng } = imageBounds
      if (Object.keys(imageBounds).length) {
        if (lat > blLat && lat < trLat && lng > blLng && lng < trLng) {
          return true
        }
      }
      return false
    },
    mapboxEvents(map) {
      let self = this
      map.on('mouseup', e => {
        this.$set(this.clickEvents, 'mouseup', true)

        let inside = true
        inside = this.findPoint(e.lngLat)
        if (!inside) {
          this.destroyDragEement()
          this.closeDrgaMarker()
          this.$message.error('You clicked outside of the Floor Plan')
          return
        }
        if (this.isDraging) {
          this.addPolygon(e)
          this.destroyDragEement()
        }
        if (this.deskDraging) {
          this.addDeskMarker(e)
          this.destroyDragEement()
        }
      })
      map.on('click', onClick)

      function onClick(e) {
        self.clickHandle(e)
      }
    },
    clickHandle(e) {
      if (!this.clickEvents.mousedown) {
        this.closeDialog()
      }
      if (this.selectedElement === null) {
        this.deActiveSelectedElement()
      }
      let inside = true
      inside = this.findPoint(e.lngLat)
      if (!inside) {
        this.destroyDragEement()
        // this.$message.error('You clicked outside of the floorplan')
        return
      }

      let id = `desk-marker-${String(new Date().getTime())}`
      let marker = {
        type: 'Feature',
        geometry: {
          type: 'Point',
          coordinates: [e.lngLat.lng, e.lngLat.lat],
        },
        properties: {
          id: id,
        },
        id: id,
      }

      if (this.selectedMarkerType) {
        let { name, type } = this.selectedMarkerType
        if (type && type === 'custom') {
          if (name === 'desk') {
            let desk = {
              deskCode: '',
              departmentId: -1,
              deskType: 1,
              name: this.getNumberingCode(this.selectedNumbering),
              spaceType: 4,
              floor: {
                id: this.floorId,
              },
              siteId: this.siteId,
              resourceType: 1,
              floorId: this.floorId,
            }
            if (this.buildingId) {
              this.$set(desk, 'building', { id: this.buildingId })
            }

            marker.properties = this.$helpers.cloneObject({
              ...desk,
              ...this.selectedMarkerType,
              ...marker.properties,
              ...{
                markerId: 'desk',
                isCustom: true,
                label: this.getNumberingCode(this.selectedNumbering),
              },
              ...{
                normalClass: 'desk',
                markerId: 'desk',
                activeClass: 'desk_active',
              },
            })
            this.selectedNumbering.currentNumber += 1
            this.markers.features.push(marker)
            this.refrshMarkerJson()
          } else if (name) {
            console.log('marker clicked', this.selectedMarkerType)
            let { fileId, id } = this.selectedMarkerType

            let markerId =
              fileId && fileId > 0 ? id : this.selectedMarkerType.name
            marker.properties = this.$helpers.cloneObject({
              ...this.selectedMarkerType,
              ...marker.properties,
              ...{
                markerId: markerId,
                isCustom: false,
                label: this.selectedMarkerType.name,
              },
              // ...{normalClass: markerId, markerId: markerId, activeClass: `${markerId}-active`} // to be done for other markers
            })
            this.markers.features.push(marker)
            this.refrshMarkerJson()
          }
        } else {
          let markerId = this.selectedMarkerType.id
          marker.properties = this.$helpers.cloneObject({
            ...this.selectedMarkerType,
            ...marker.properties,
            ...{
              markerId: markerId,
              isCustom: false,
              label: this.selectedMarkerType.name,
            },
            // ...{normalClass: markerId, markerId: markerId, activeClass: `${markerId}-active`} // to be done for other markers
          })
          this.markers.features.push(marker)
          this.refrshMarkerJson()
        }
      }
      this.clickEvents = {}
    },
    addDeskMarker(e) {
      if (!this.drgaDeskElement) {
        return
      }
      let desk = this.drgaDeskElement
      let id = `desk-marker-${String(new Date().getTime())}`
      let marker = {
        type: 'Feature',
        geometry: {
          type: 'Point',
          coordinates: [e.lngLat.lng, e.lngLat.lat],
        },
        properties: {
          id: id,
        },
        id: id,
        markerModuleId: desk.moduleId,
        recordId: desk.id,
        desk: desk,
      }

      marker.properties = this.$helpers.cloneObject({
        ...desk,
        ...this.selectedMarkerType,
        ...marker.properties,
        ...{
          markerId: 'desk',
          isCustom: true,
          label: desk.name,
          recordModuleId: desk.moduleId,
          recordId: desk.id,
        },
        ...{
          normalClass: 'desk',
          markerId: 'desk',
          activeClass: 'desk_active',
        },
      })
      if (desk.employee) {
        this.$set(marker.properties, 'employeeId', Number(desk.employee.id))
      }
      if (desk.department) {
        this.$set(marker.properties, 'departmentId', Number(desk.department.id))
      }
      if (desk.id) {
        this.$set(marker.properties, 'deskId', Number(desk.id))
      }
      this.$set(marker, 'desk', desk)
      this.markers.features.push(marker)
      this.refrshMarkerJson()
    },
    updateSpaceElement(e) {
      if (!e.pageX && !e.pageY) return
      let color = '#fff'
      let div = document.getElementById('dragSpaceItem')

      div.style.transform = `translate(${e.pageX + 10}px,${e.pageY}px)` // Insert text
      div.style.background = color
      div.style.padding = '4px'
      div.innerHTML = `<div>${this.dragSpaceElement.name}</div>`
      document.body.appendChild(div)
    },
    updateDeskElement(e) {
      if (!this.drgaDeskElement) return
      if (!e.pageX && !e.pageY) return
      let color = 'transparent'
      let div = document.getElementById('dragMarkerItem')

      div.style.transform = `translate(${e.pageX + 10}px,${e.pageY}px)` // Insert text
      div.style.background = color
      div.style.width = '30px'
      div.style.height = '30px'
      div.style.padding = '4px'
      let url = ''
      url = '/img/desk.cd45f094.png'
      div.innerHTML = `<div data-v-0045ef1c="" class="el-image"><img src="${url}" class="el-image__inner"></div>
       <div  style="text-align: center;white-space: nowrap;font-size: 10px;">${this.drgaDeskElement.name}</div>
      `
      document.body.appendChild(div)
    },
    updateElement(e) {
      if (!e.pageX && !e.pageY) return
      let color = 'transparent'
      let div = document.getElementById('dragMarkerItem')

      div.style.transform = `translate(${e.pageX + 10}px,${e.pageY}px)` // Insert text
      div.style.background = color
      div.style.width = '30px'
      div.style.height = '30px'
      div.style.padding = '4px'
      let url = ''
      if (this.selectedMarkerType.name === 'desk') {
        url = '/img/desk.cd45f094.png'
      } else if (this.selectedMarkerType.type === 'custom') {
        url = this.pathIcon(this.selectedMarkerType.icon)
      } else {
        url = `${getBaseURL()}/v2/files/preview/${
          this.selectedMarkerType.fileId
        }?fetchOriginal=true`
      }
      div.innerHTML = `<div data-v-0045ef1c="" class="el-image"><img src="${url}" class="el-image__inner"></div>
       <div  style="text-align: center;white-space: nowrap;font-size: 10px;">${this.getNumberingCode(
         this.selectedNumbering,
         this.selectedMarkerType
       )}</div>
      `
      document.body.appendChild(div)
    },
    pathIcon(icon) {
      return require(`statics/floorplan/${icon}`)
    },
    addEventListenerevents() {
      document
        .getElementById('editor')
        .addEventListener('mousemove', this.mousemove)
    },
    mousemove(e) {
      if (this.selectedMarkerType) {
        this.updateElement(e)
      }
      if (this.isDraging) {
        this.updateSpaceElement(e)
      }
      if (this.deskDraging) {
        this.updateDeskElement(e)
      }
    },
    displayDragElement() {
      let div = document.getElementById('dragMarkerItem')
      div.style.display = 'block'
      div.style.position = 'absolute'
      div.style.zIndex = '20'
      div.style.opacity = '0.5'
      div.style.width = '0px'
      div.style.height = '0px'
      div.style.top = '0px'
      div.style.color = '#000'
    },
    removeEventListenerevents() {
      let editor = document.getElementById('editor')
      if (editor) {
        editor.removeEventListener('mousemove', this.mousemove)
      }
    },
    refrshMarkerJson() {
      this.map.getSource('marker').setData(this.markers)
      // this.selectedElementIndex = -1
      // this.selectedElement = null
    },
    checkNumbering(marker) {
      if (marker && marker.id && this.numbering && this.numbering.length) {
        let index = this.numbering.findIndex(rt => {
          if (rt.markerType && rt.markerType.id === marker.id) {
            return rt
          }
          return null
        })
        if (index > -1) {
          return true
        }
      }
      return false
    },
    getMarker(marker) {
      this.selectedMarkerType = marker
      if (marker.name === 'desk') {
        if (!this.checkNumbering(marker)) {
          let numbering = {
            startNumber: 1,
            currentNumber: 1,
            prefix: 'd',
            suffix: '',
            previewcard: '',
            indoorfloorplan: {
              id: this.indoorFloorPlan.id,
            },
            markerType: {
              id: marker.id,
            },
          }
          this.numbering.push(numbering)
        }
        this.selectedNumbering = this.getNumbering(marker)
      }

      this.displayDragElement()
    },
    syncData() {
      this.zones = this.draw.getAll()
      this.map.getSource('zoneLabel').setData(this.zones)
    },
    refresh() {
      this.ready = false
      setTimeout(() => {
        this.ready = true
      }, 100)
    },
    loadObjects(map) {
      this.addControlLayer(map)
      this.addMarkerLayer(map)
      this.addZoneLabelLayer(map)
    },
    mapLoaded(event) {
      this.map = event.map
      this.loading = false

      event.onMapLoad.then(() => {
        this.loadFloorplanImage(this.map)
        this.mapboxEvents(this.map)
      })
    },
  },
  destroyed() {
    this.draw = null
    this.removeEventListenerevents()
  },
}
</script>
<style scoped lang="scss">
.indoor-fp-mapbox-section {
  display: flex;
  height: 100%;
}

.fc-indoor-floorplan-editor-view {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.marker-subtext {
  font-size: 10px;
  white-space: nowrap;
  display: center;
}

.dragElemntClose {
  z-index: 100;
  position: absolute;
  max-width: 400px;
  display: flex;
  border-radius: 2px;
  left: 50%;
  top: 65px;
  border: 1px solid #faecd8;
  border-radius: 4px;
  background: #fdf6ec;
  padding: 6px 10px;
}

#drop1 {
  width: 80%;
  height: 100%;
  position: absolute;
  background: transparent;
  margin-left: 20%;
  z-index: 2;
}

.mapboxgl-ctrl-logo {
  display: none;
}

.fc-dragelement-txt {
  font-weight: 600;
  color: #e6a23c;
  padding-top: 6px;
}

.fc-dragelement-btn {
  padding: 6px 8px;
  background: #e6a23c;
  border: 1px solid #e6a23c;
  color: #fff;
  font-weight: 600;
  font-size: 12px;
}
</style>
