<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import { getBaseURL } from 'util/baseUrl'
import mapboxgl from 'mapbox-gl'
import { eventBus } from '@/page/widget/utils/eventBus'
import moment from 'moment'

export default {
  props: ['id'],
  data() {
    return {
      floorplanMappedmodules: [],
      floorplanloading: true,
      movedEmployeeDesks: [],
      moveText: null,
      indoorFloorPlan: null,
      markers: {},
      zones: {},
      selectedElementIndex: -1,
      selectedElement: null,
      canMove: false,
      spaces: [],
      markerTypes: [],
      employeeList: [],
      employeSideBar: true,
      selectedFeature: null,
      modules: [],
      departments: [],
      leagends: [],
      desks: [],
      imageList: [],
      amenities: [],
      deskTypes: [
        { name: 'Assigned Desk', value: 1, color: 'red' },
        { name: 'Hotel Desk', value: 2, color: 'green' },
        { name: 'HOT Desk', value: 3, color: 'blue' },
        { name: 'Assignable Desk', value: 4, color: 'red' },
      ],
      spaceCategories: [],
      employeLoading: false,
      unassignedemployee: true,
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
      floorplanTypes: {},
      floorplantypeSwicthLoading: true,
      indoorFloorplanFields: [],
      imageBounds: [],
      markerMap: {},
      markerList: [],
      zoneList: [],
      zoneMap: {},
      facilities: {},
      bookings: {},
    }
  },
  computed: {
    defaulttimeRange() {
      // '09:00 : AM', '05:00 : PM'
      let date = moment()
        .startOf('day')
        .valueOf()
      const d = new Date()
      let ms = d.getHours()
      let currentTime = ms * 3600000
      let time = date + 10800000
      if (currentTime <= time) {
        return {
          startTime: date + 54000000,
          endTime: date + 79200000,
        }
      } else {
        return {
          startTime: date + 32400000,
          endTime: date + 61200000,
        }
      }
    },
    floorplanMappedModulesObject() {
      let data = {}
      this.floorplanMappedmodules.forEach(rt => {
        if (!data[rt.moduleId]) {
          this.$set(data, rt.moduleId, rt.name)
        }
      })
      return data
    },
    newBooking() {
      return this.$helpers.isLicenseEnabled('NEW_BOOKING') ? true : false
    },
    newFloorPlan() {
      return this.$helpers.isLicenseEnabled('NEW_FLOORPLAN') ? true : false
    },
    spaceCategoryIdVsNameMap() {
      let data = {}
      this.spaceCategories.forEach(rt => {
        this.$set(data, rt.id, rt.name)
      })
      return data
    },
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
    defaultMarkerIdVsIcon() {
      let data = {}
      this.defaultMarkerTypes.forEach(rt => {
        this.$set(data, rt.id, rt.icon)
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
    deskCategoryId() {
      let deskCategory = this.spaceCategories.find(rt => rt.name === 'Desk')
      if (deskCategory) {
        return deskCategory.id
      }
      return null
    },
    siteId() {
      if (this.indoorFloorPlan && this.indoorFloorPlan.site) {
        return this.indoorFloorPlan.site.id
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
    customization() {
      return this.indoorFloorPlan?.customization
        ? this.indoorFloorPlan.customization
        : null
    },
    customizationBooking() {
      return this.indoorFloorPlan?.customizationBooking
        ? this.indoorFloorPlan.customizationBooking
        : null
    },
  },
  methods: {
    async patchUpdateRecord(settings) {
      let { id } = this.indoorFloorPlan || {}
      let param = {
        id,
        data: {
          customizationJSON: JSON.stringify(settings),
          customization: settings,
        },
      }

      let { error } = await API.updateRecord('indoorfloorplan', param)

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      }
    },
    async patchUpdateBookingRecord(settings) {
      let { id } = this.indoorFloorPlan || {}
      let param = {
        id,
        data: {
          customizationBookingJSON: JSON.stringify(settings),
          customizationBooking: settings,
        },
      }

      let { error } = await API.updateRecord('indoorfloorplan', param)

      if (error) {
        this.$message.error(error.message || 'Error Occured')
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
    async loadModulesList() {
      let { data } = await API.get('/v3/modules/list/all')
      if (data) {
        let customModules = data['customModules'] || []
        let systemModules = data.systemModules || []
        this.modules = [...systemModules, ...customModules]
      }
    },
    async fetchEmployeeFulldetails(id) {
      if (id) {
        let { data } = await API.get(
          `v2/servicePortalHome?fetchOnlyDesk=true&count=1&recordId=${id}`
        )

        if (data && data.desks) {
          this.movedEmployeeDesks = data.desks
        }
      }
    },
    loadMoveDetails(fromDesk, toDesk) {
      this.moveText = ''
      if (fromDesk) {
        let floorName =
          fromDesk.floor && fromDesk.floor.name ? fromDesk.floor.name : null
        this.moveText = `Employee moved from ${fromDesk.name} ${
          floorName ? ', ' + floorName : ''
        }`

        if (toDesk && toDesk.name) {
          this.moveText += ` to ${toDesk.name}`
        }
      } else if (toDesk && toDesk.name) {
        this.moveText += `Employee moved to ${toDesk.name}`
      }
      this.$message.success(this.moveText)
      eventBus.$emit('REFRESH_EMPLOYEE_LIST')
    },
    generateIcons() {
      // this.addSecondaryLabelIcon()
      this.addDeskIcon('desk', null, null) // no assignmnet desk types should be #000 in this color
      this.deskTypes.forEach(type => {
        let name = ''
        name = `desk_${type.name}`
        this.addDeskIcon(name.replace(/\s+/g, ''), '#73706f', '#73706f')
        this.departments.forEach(department => {
          name = `desk_${type.name}`
          name += `_${department.name}`
          this.addDeskIcon(
            name.replace(/\s+/g, ''),
            department.color,
            department.color
          )
        })
      })
    },
    addSecondaryLabelIcon() {
      let { map } = this

      let secondaryLabel = {
        width: 50,
        height: 10,
        data: new Uint8Array(50 * 10 * 4),

        // get rendering context for the map canvas when layer is added to the map
        onAdd: function() {
          let canvas = document.createElement('canvas')
          canvas.width = this.width
          canvas.height = this.height
          this.context = canvas.getContext('2d')
        },

        // called once before every frame where the icon will be used
        render: function() {
          let ctx = this.context

          ctx.beginPath()
          ctx.rect(0, 0, 50, 10)
          ctx.fillStyle = 'blue'
          ctx.stroke()
          ctx.fill()

          return true
        },
      }

      map.addImage('secondaryLabel', secondaryLabel, {
        pixelRatio: 2,
      })
    },
    addDeskIcon(name, innercolor, outerColor) {
      let { map } = this
      if (!name) {
        name = 'desk'
      }
      let size = 80
      let outerRadius = (size - 20) / 2
      let radius = (size / 3) * 0.5
      let active = size / 2 - 5
      let activeOuter = size / 2 - 3

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
          context.fillStyle = outerColor || '#000'
          context.strokeStyle = 'white'
          context.lineWidth = 5
          context.fill()
          context.stroke()

          // draw inner circle
          context.beginPath()
          context.arc(this.width / 2, this.height / 2, radius, 0, Math.PI * 2)
          context.fillStyle = innercolor || '#fff'
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

          // draw active green circle
          context.beginPath()
          context.arc(
            this.width / 2,
            this.height / 2,
            activeOuter,
            0,
            Math.PI * 2
          )
          context.fillStyle = 'transparent'
          context.strokeStyle = '#fff'
          context.lineWidth = 10
          context.fill()
          context.stroke()

          // draw active green circle
          context.beginPath()
          context.arc(this.width / 2, this.height / 2, active, 0, Math.PI * 2)
          context.fillStyle = 'transparent'
          context.strokeStyle = 'green'
          context.lineWidth = 10
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
          context.fillStyle = outerColor || '#000'
          context.strokeStyle = 'white'
          context.lineWidth = 5
          context.fill()
          context.stroke()

          // draw inner circle
          context.beginPath()
          context.arc(this.width / 2, this.height / 2, radius, 0, Math.PI * 2)
          context.fillStyle = innercolor || '#fff'
          context.fill()

          // update this image's data with data from the canvas
          this.data = context.getImageData(0, 0, this.width, this.height).data

          // continuously repaint the map, resulting in the smooth animation of the dot
          // map.triggerRepaint();

          // return `true` to let the map know that the image was updated
          return true
        },
      }

      let hoverdesksize = 100
      let hoverOuterRadius = (hoverdesksize - 20) / 2
      let hoverRadius = (hoverdesksize / 3) * 0.5
      let hoverActive = hoverdesksize / 2 - 5
      let deskHover = {
        width: hoverdesksize,
        height: hoverdesksize,
        data: new Uint8Array(hoverdesksize * hoverdesksize * 4),

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
          context.arc(
            this.width / 2,
            this.height / 2,
            hoverActive,
            0,
            Math.PI * 2
          )
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
            hoverOuterRadius,
            0,
            Math.PI * 2
          )
          context.fillStyle = outerColor || '#000'
          context.strokeStyle = 'white'
          context.lineWidth = 5
          context.fill()
          context.stroke()

          // draw inner circle
          context.beginPath()
          context.arc(
            this.width / 2,
            this.height / 2,
            hoverRadius,
            0,
            Math.PI * 2
          )
          context.fillStyle = innercolor || '#fff'
          context.fill()

          // update this image's data with data from the canvas
          this.data = context.getImageData(0, 0, this.width, this.height).data

          // continuously repaint the map, resulting in the smooth animation of the dot
          // map.triggerRepaint();

          // return `true` to let the map know that the image was updated
          return true
        },
      }

      map.addImage(`${name}_active`, deskactive, { pixelRatio: 2 })
      this.imageList.push(`${name}_active`)

      map.addImage(`${name}_hover`, deskHover, { pixelRatio: 2 })
      this.imageList.push(`${name}_hover`)

      map.addImage(name, desk, { pixelRatio: 2 })
      this.imageList.push(name)
    },
    removeImage() {
      let { map, imageList } = this
      imageList.forEach(name => {
        map.removeImage(name)
      })
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
        map.addImage(type.name, pulsingDot, {
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

        if (marker.name === 'Locker') {
          let InActiveIcons = {
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
                url = self.inActivepathIcon(marker.icon)
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
              this.data = context.getImageData(
                0,
                0,
                this.width,
                this.height
              ).data

              // return `true` to let the map know that the image was updated
              return true
            },
          }
          map.addImage(`${marker.name}_inactive`, InActiveIcons, {
            pixelRatio: 2,
          })
        }

        map.addImage(marker.name, defaultIcons, {
          pixelRatio: 2,
        })
      })
    },
    pathIcon(icon) {
      return require(`statics/floorplan/${icon}`)
    },
    inActivepathIcon(icon) {
      return require(`statics/floorplan/inactive/${icon}`)
    },
    getPreviewUrl(fileId) {
      return `${getBaseURL()}/v2/files/preview/${fileId}?fetchOriginal=true`
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

        self.imageBounds = bd

        map.fitBounds(bd, {
          padding: 100,
        })

        self.loadObjects(map)
        self.afterFloorPlanRender && self.afterFloorPlanRender()
      }
      img.src = imageUrl
    },
    fitBounds(coordinates, padding = 10) {
      let bd = coordinates.reduce(function(bid, coord) {
        return bid.extend(coord)
      }, new mapboxgl.LngLatBounds(coordinates[0], coordinates[0]))

      this.map.fitBounds(bd, {
        padding: padding,
      })
    },
    getDeskMarkerType() {
      return this.markerTypes.find(rt => rt.name === 'desk')
    },
    getIconName(desk) {
      let name = 'desk'
      if (desk && desk.employee && desk.employee.id) {
        if (desk.department && desk.department.id) {
          let department = this.departments.find(
            rt => rt.id === desk.department.id
          )
          if (department) {
            name += `_${desk.deskTypeVal}_${department.name}`
          } else {
            name += `_${desk.deskTypeVal}`
          }
        } else {
          name += `_${desk.deskTypeVal}`
        }
      }
      let result = name.replace(/\s+/g, '')
      return result
    },
    centerLabel(name) {
      if (name) {
        let spliteName = name.split(' ')
        if (spliteName.length > 1) {
          return (
            spliteName[0].charAt(0).toUpperCase() +
            spliteName[1].charAt(0).toUpperCase()
          )
        } else if (spliteName.length === 1 && spliteName[0].length > 1) {
          return (
            spliteName[0].charAt(0).toUpperCase() +
            spliteName[0].charAt(1).toUpperCase()
          )
        } else {
          spliteName[0].charAt(0).toUpperCase()
        }
      }
      return ''
    },
    deSerializeGeoJson() {
      // if (this.zones?.features?.length) {
      //   this.zones.features.forEach(f => {
      //     console.log('--->', f.properties.zoneOpacity)
      //   })
      // }
    },
    getDeskSecondaryLabel(feature) {
      if (!feature.properties.employeeId) {
        return ''
      }
      if (feature?.desk?.employee?.name) {
        return feature.desk.employee.name
      }
      return ''
    },
    async loadFloorplanTypes() {
      this.floorplantypeSwicthLoading = true
      let { data } = await API.get(
        `v3/floorplan/getFloorplanDetailsByType?floorId=${this.floorId}`
      )
      if (data?.indoorFloorPlans) {
        this.floorplanTypes = data.indoorFloorPlans
      }
      this.floorplantypeSwicthLoading = false
    },
    setFeatureStateClass(feature, name) {
      this.$set(feature.properties, 'normalClass', name)
      this.$set(feature.properties, 'markerId', name)
      this.$set(feature.properties, 'activeClass', `${name}_active`)
      this.$set(feature.properties, 'hoverClass', `${name}_hover`)
    },
    async updateDesk(desk) {
      let params = {
        moduleName: 'desks',
        data: desk,
        id: desk.id,
      }
      let { data, error } = await API.post(`v3/modules/data/update`, params)
      if (data) {
        this.$message.success('Desk updated')
      }
      if (error) {
        this.$message.error('Failed')
      }
    },
    async updateMarker(marker) {
      let params = {
        moduleName: 'floorplanmarker',
        data: marker,
        id: marker.id,
      }
      let { data, error } = await API.post(`v3/modules/data/update`, params)
      if (data) {
        this.$message.success('Updated')
      }
      if (error) {
        this.$message.error('Failed')
      }
    },
    async updateMoveData(move) {
      let params = {
        data: move,
        moduleName: 'moves',
      }
      await API.post(`v3/modules/data/create`, params)
    },
    async getFloorplan() {
      let params = {
        moduleName: 'indoorfloorplan',
        id: this.floorplanId,
      }
      let { data, error } = await API.post(`v3/modules/data/summary`, params)
      if (error) {
        this.$message.error('Failed')
        throw new Error('getFloorplan failed', error)
      } else {
        this.$emit('floorPlanLoaded', data.indoorfloorplan)
        return data
      }
    },
    async getViewerApi() {
      //  temp method need to change this
      let filters = {}
      let params = {
        floorplanId: this.floorplanId,
        viewMode: this.viewerMode,
        newBooking: this.newBooking,
      }
      if (this.viewerMode === 'BOOKING') {
        if (this.timeRange) {
          params['endTime'] =
            this.timeRange.endTime || this.defaulttimeRange.endTime
          params['startTime'] =
            this.timeRange.startTime || this.defaulttimeRange.startTime
        } else {
          params['endTime'] = this.defaulttimeRange.endTime
          params['startTime'] = this.defaulttimeRange.startTime
        }

        if (this.amenities && this.amenities.length) {
          this.$set(filters, 'amenities', this.amenities)
        } else {
          this.$set(filters, 'amenities', [])
        }
        params['floorplanFilters'] = filters
      }

      let { data, error } = await API.post(`v3/floorplan/viewerData`, params)
      if (error) {
        this.$message.error('Failed')
        throw new Error('floorplan viewer data failed', error)
      } else {
        this.markers = data.marker
        this.zones = data.spaceZone
        this.markerMap = this.convertToMap(data.marker)
        this.zoneMap = this.convertToMap(data.spaceZone)
        this.indoorFloorPlan = data.indoorfloorplan
        this.floorplanMappedmodules = data.floorplanMappedmodules
        this.$emit('floorPlanLoaded', data.indoorfloorplan)
        eventBus.$emit('VIEWER_API_LOADED', {
          markers: this.markers,
          zones: this.zones,
        })

        if (data?.facility) {
          this.facilities = data.facility
        }
        if (data?.facilitybooking) {
          this.bookings = data.facilitybooking
        }
        return data
      }
    },
    convertToMap(data) {
      let { features } = data
      let d = {}
      if (features && features.length) {
        features.forEach(f => {
          if (f?.properties?.objectId) {
            this.$set(d, f.properties.objectId, f)
          }
        })
        return d
      } else {
        return {}
      }
    },
    convertGeoJson(data) {
      return {
        type: 'FeatureCollection',
        features: data,
      }
    },
    async fetchSpaces() {
      let filters = {
        floor: { operatorId: 36, value: [`${this.floorId}`] },
      }
      let params = {
        moduleName: 'space',
        filters: JSON.stringify(filters),
        includeParentFilter: true,
        page: 1,
        perPage: 100,
      }
      return await API.get(`v2/module/data/list`, params).then(({ data }) => {
        if (data) {
          this.spaces = data.moduleDatas
        }
      })
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
      let { data } = await API.get(`v2/module/data/list`, params)
      this.departments = data.moduleDatas
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
    async loadFloorplanFields() {
      let params = {
        moduleName: 'indoorfloorplan',
      }
      let { data } = await API.get(`v2/modules/fields/fields`, params)
      if (data?.fields) {
        this.indoorFloorplanFields = data.fields
      }
    },
  },
}
</script>
