<script>
import debounce from 'lodash/debounce'
import canvasAction from 'pages/stateflows/mixins/CanvasActionsMixin'
import * as PolygonUtil from 'pages/floorPlan/elements/SpacePolygonUtil'
import { fabric } from 'fabric'
import { isEmpty } from '@facilio/utils/validation'
import {
  isNotMappedElement,
  isMappedElement,
  isElement,
  findShapeByName,
  getcoordinates,
  isSpaceId,
} from '../elements/Common'
export default {
  extends: canvasAction,
  methods: {
    onWindowResize: debounce(function() {
      // this.updateDiagram()
      this.setupEditor()
    }, 2 * 1000),
    mapTheMouseEvents() {
      let self = this
      this.canvas.on('mouse:down', function(event) {
        self.onNewConnectionMouseDown(event)
      })
      this.canvas.on('mouse:up', function(event) {
        self.onNewConnectionMouseUp(event)
      })
    },
    handleMarkerValue() {
      if (this.leagend && this.leagend.showValue) {
        this.$set(this.leagend, 'showValue', false)
      } else {
        this.$set(this.leagend, 'showValue', true)
      }
      this.canvas.forEachObject(obj => {
        this.applyValue(obj, this.leagend.showValue)
      })
      this.canvas.renderAll()
    },
    setMarkerValue() {
      this.canvas.forEachObject(obj => {
        this.applyValue(obj, this.leagend.showValue)
      })
      this.canvas.renderAll()
    },
    populateLayerInfo(data) {
      this.populateMarkers(data)
    },
    zoomIn() {
      this.canvas.setZoom(this.canvas.getZoom() * 1.1)
    },
    zoomOut() {
      this.canvas.setZoom(this.canvas.getZoom() / 1.1)
    },
    sendToBack() {
      let activeObject = this.canvas.getActiveObject()
      if (activeObject) {
        this.canvas.sendToBack(activeObject)
      }
    },
    bringToFront() {
      let activeObject = this.canvas.getActiveObject()
      if (activeObject) {
        this.canvas.bringToFront(activeObject)
      }
    },
    rotate(degrees) {
      let { canvas } = this
      let canvasCenter = new fabric.Point(
        canvas.getWidth() / 2,
        canvas.getHeight() / 2
      ) // center of canvas
      let radians = fabric.util.degreesToRadians(degrees)

      canvas.getObjects().forEach(obj => {
        let objectOrigin = new fabric.Point(obj.left, obj.top)
        let new_loc = fabric.util.rotatePoint(
          objectOrigin,
          canvasCenter,
          radians
        )
        obj.top = new_loc.y
        obj.left = new_loc.x
        obj.angle += degrees //rotate each object buy the same angle
        obj.setCoords()
      })
      canvas.renderAll()
    },
    rotateLeft() {
      this.rotate(-90)
    },
    rotateRight() {
      this.rotate(90)
    },
    goRight() {
      let units = 10
      let { canvas } = this
      let delta = new fabric.Point(units, 0)
      canvas.relativePan(delta)
    },
    goLeft() {
      let units = 10
      let { canvas } = this
      let delta = new fabric.Point(-units, 0)
      canvas.relativePan(delta)
    },
    goUp() {
      let units = 10
      let { canvas } = this
      let delta = new fabric.Point(0, -units)
      canvas.relativePan(delta)
    },
    goDown() {
      let units = 10
      let { canvas } = this
      let delta = new fabric.Point(0, units)
      canvas.relativePan(delta)
    },
    selectAll() {
      this.activeAllObjects()
    },
    deleteActiveObject() {
      let activeObjects = this.canvas.getActiveObjects()
      this.canvas.discardActiveObject()
      if (activeObjects.length) {
        this.canvas.remove.apply(this.canvas, activeObjects)
      }
    },
    copy() {
      if (this.canvas.getActiveObject()) {
        this.canvas.getActiveObject().clone(
          cloned => {
            this._clipboard = cloned
          },
          ['floorplan']
        )
      }
    },
    paste(position) {
      this.pasteInternal(this._clipboard, position)
    },
    pasteInternal(clipboard, position) {
      let self = this
      if (clipboard) {
        clipboard.clone(
          clonedObj => {
            clonedObj.set({
              left: position ? position.left : clonedObj.left + 10,
              top: position ? position.top : clonedObj.top + 10,
              evented: true,
            })
            if (clonedObj.type === 'activeSelection') {
              // active selection needs a reference to the canvas.
              clonedObj.canvas = this.canvas
              clonedObj.forEachObject(function(obj) {
                if (obj.floorplan && obj.floorplan.id) {
                  obj.floorplan.id = self.graphicUtil.getUniqueId()
                }
                self.canvas.add(obj)
              })
              // this should solve the unselectability
              clonedObj.setCoords()
            } else {
              if (clonedObj.floorplan && clonedObj.floorplan.id) {
                clonedObj.floorplan.id = self.graphicUtil.getUniqueId()
              }
              self.canvas.add(clonedObj)
            }
            clipboard.top += 10
            clipboard.left += 10
            self.selectedObject = clonedObj
            self.setUpObjectEvents(clonedObj)
            self.canvas.setActiveObject(clonedObj)
            self.canvas.requestRenderAll()
          },
          ['floorplan']
        )
      }
    },
    populateMarkers(data) {
      let { markerType } = data
      let self = this
      let list = this.getListOfMappedElemnet('space_zone_group').filter(rt => {
        let spaceId =
          rt.floorplan && rt.floorplan.spaceId ? rt.floorplan.spaceId : null
        if (spaceId && !this.getElementBySpaceId(spaceId, markerType)) {
          return rt
        }
      })

      list.forEach(rt => {
        let shape = findShapeByName(markerType)
        self.getShape(shape, function(obj) {
          self.currentDragElement = obj
        })
        this.addMarkers(rt, rt._findCenterFromElement())
      })
    },
    addMarker(markerType, space, location, icon) {
      let shape = findShapeByName(markerType)
      this.getShape(shape, obj => {
        this.currentDragElement = obj
      })
      this.addViewerMarkers(space, getcoordinates(space, location), icon)
    },
    addPolygon() {
      let { canvas } = this
      let { polygonOptions } = this
      PolygonUtil.drawPolygon(polygonOptions, canvas)
    },
    handleLayer() {
      this.layerVisible = true
    },
    configSpaceRegion() {},
    bindSpace(space) {
      if (!isEmpty(space) && this.selectedElement) {
        let { target } = this.selectedElement
        let { floorplan } = target
        if (isNotMappedElement(target, 'space_zone_group')) {
          if (target && target._objects) {
            target.getObjects().forEach(rt => {
              if (
                rt.floorplan &&
                rt.floorplan.objectType === 'space_zone_label'
              ) {
                rt.set('text', space.name)
              }
            })
          }
          floorplan.spaceId = space.id
          this.setElementDataById(this.selectedElement.id, floorplan)
          this.canvas.renderAll()
          this.showSuccess('Space Mapped successfully')
        } else if (!isNotMappedElement(target, 'space_zone_group')) {
          this.showError('Space Mapped already')
        }
      } else {
        this.showWarning('No Space Selected')
      }
    },
    getElementBySpaceId(spaceId, markerType) {
      if (markerType) {
        return this.canvas.getObjects().find(el => {
          let { floorplan } = el
          if (
            floorplan &&
            floorplan.spaceId === spaceId &&
            floorplan.markerType &&
            floorplan.markerType === markerType
          ) {
            return el
          }
        })
      }
      return this.canvas.getObjects().find(el => {
        if (el.floorplan && el.floorplan.spaceId === spaceId) {
          return el
        }
      })
    },
    getElementById(id) {
      return this.canvas.getObjects().find(el => {
        if (el.floorplan && el.floorplan.id === id) {
          return el
        }
      })
    },
    getFloorPlanImageElement() {
      return this.canvas.getObjects().find(el => {
        if (isElement(el, 'floor_plan_image')) {
          return el
        }
      })
    },
    getListOfNotMappedElemnet(type) {
      if (type) {
        return this.canvas.getObjects().filter(el => {
          if (!isElement(el, type)) {
            return el
          }
        })
      }
      return this.canvas.getObjects().filter(el => {
        if (isNotMappedElement(el, type)) {
          return el
        }
      })
    },
    getListOfMappedElemnet(type, spaceId) {
      if (type && spaceId) {
        return this.canvas.getObjects().filter(el => {
          if (isElement(el, type) && isSpaceId(el, spaceId)) {
            return el
          }
        })
      } else if (type) {
        return this.canvas.getObjects().filter(el => {
          if (isElement(el, type)) {
            return el
          }
        })
      }
      return this.canvas.getObjects().filter(el => {
        if (isMappedElement(el)) {
          return el
        }
      })
    },
    getListOfSpaceId(element) {
      if (isMappedElement(element)) {
        this.mappedSpaceId.push(element.floorplan.spaceId)
      }
    },
    getListOfMappedSpaceId() {
      return this.canvas
        .getObjects()
        .filter(el => {
          if (isMappedElement(el)) {
            return el
          }
        })
        .map(rt => rt.floorplan.spaceId)
    },
    updateActionEvents() {
      setTimeout(() => {
        this.loadfloorPlanData()
      }, 1000)
    },
    setElementDataById(id, data) {
      this.canvas.getObjects().forEach(el => {
        if (el.floorplan && el.floorplan.id === id) {
          this.$set(el, 'floorplan', data)
        }
      })
    },
    resetSpaceFocus() {
      this.canvas.getObjects().forEach(el => {
        if (el.floorplan && isElement('space_zone_group')) {
          this.$set(el, 'opacity', 0.6)
        }
      })
    },
    getSpaceIdFromElement(element) {
      if (
        isMappedElement(element) &&
        element.floorplan &&
        element.floorplan.spaceId
      ) {
        return element.floorplan.spaceId
      }
      return null
    },
    getSelectedArea(element) {
      if (this.hasOwnProperty('selectedArea') && element) {
        let spaceId = this.getSpaceIdFromElement(element.target)
        if (spaceId) {
          this.selectedArea = this.areas.filter(rt => rt.spaceId === spaceId)
        }
      }
      return null
    },
  },
}
</script>
