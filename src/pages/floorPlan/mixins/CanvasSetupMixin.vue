/* eslint-disable */
<script>
import { isEmpty } from '@facilio/utils/validation'
import { fabric } from 'fabric'
import throttle from 'lodash/throttle'
import * as PolygonUtil from 'pages/floorPlan/elements/SpacePolygonUtil'
import NewDateHelper from '@/mixins/NewDateHelper'
import {
  isNotMappedElement,
  tempProps,
  isMarkerElement,
  findShape,
  isMappedElement,
  isObjectType,
  isVariable,
  isVariableElement,
  makeBg,
  viewerProps,
  EditorProps,
  UnlockProps,
  isSpaceZoneElement,
  getCoordinatesCenter,
} from '../elements/Common'
import { isElement, getUniqueId } from '../elements/Common'
import { getBaseURL } from 'util/baseUrl'

export default {
  data() {
    return {
      animate: null,
      focusElement: null,
      selection: false,
      defaultZoomLevel: 1,
      centerPoints: null,
      touchDargEnd: false,
      currentZoomLevel: 1,
      activeAll: false,
      isCanvasPanning: false,
      mapedElementsList: [],
      allowedCanvasPanSize: 1800, // How much further the canvas extends off screen (along both axis) in px
      polygonOptions: {
        polygonMode: false,
        pointArray: new Array(),
        lineArray: new Array(),
        activeLine: null,
        activeShape: false,
      },
      trendPopup: {
        visible: false,
        analyticsConfig: {
          period: 0,
          mode: 1,
          dateFilter: NewDateHelper.getDatePickerObject(22),
          dataPoints: [],
          hidechartoptions: true,
          hidecharttypechanger: true,
        },
      },
      controlPopup: {
        visible: false,
        controlConfig: {
          title: null,
          assetId: null,
          fieldId: null,
          groupId: null,
        },
        setVal: null,
        saving: false,
      },
      clickActions: null,
    }
  },
  created() {
    this.lastPosX = null
    this.lastPosY = null
  },
  computed: {
    formattedZoomLevel() {
      return Math.round(this.currentZoomLevel * 100)
    },
  },
  methods: {
    spaceFocus() {
      if (this.focus) {
        let { spaceId } = this.focus
        // let { canvas } = this
        if (spaceId) {
          let element = this.getElementBySpaceId(spaceId)
          // let image = this.getFloorPlanImageElement()
          // let zoom = canvas.getZoom()
          // let elementCenter = element.getCenterPoint()
          this.addFocusElement(element)
          this.resetPanAndZoom()
          // let vpw = canvas.width / zoom
          // let vph = canvas.height / zoom
          // let x = elementCenter.x - vpw / 2
          // let y = elementCenter.y - vph / 2
          // canvas.absolutePan({ x: x, y: y })

          // canvas.setZoom(this.canvas.getZoom() * 1.1 * 1.1 * 1.1 * 1.1 * 1.1)
          // canvas.setZoom(this.canvas.getZoom())
        }
      }
    },
    animateFocusElement(element) {
      element.animate('radius', 100, {
        onChange: (() => {
          if (element.radius > 90) {
            element.set('opacity', 0.2)
          } else if (element.radius > 80) {
            element.set('opacity', 0.5)
          }
          if (this.canvas) {
            this.canvas.requestRenderAll()
          }
        }).bind(this.canvas),
        onComplete: () => {
          element.set('radius', 10)
          element.set('opacity', 1)
          if (this.canvas) {
            this.canvas.requestRenderAll()
          }
        },
        duration: 2500,
        easing: fabric.util.ease.easeInQuad,
      })
    },
    focusTheElement(element) {
      this.animateFocusElement(element)
      clearInterval(this.animate)
      this.animate = setInterval(() => {
        this.animateFocusElement(element)
      }, 2600)
    },
    removeFocusElement() {
      if (this.focusElement) {
        this.canvas.remove(this.focusElement)
        this.focusElement = null
      }
    },
    addFocusElement(element) {
      let { canvas } = this
      this.removeFocusElement()
      let centerPoints = element.getCenterPoint()
      this.focusElement = new fabric.Circle({
        radius: 10,
        fill: '#42acc914',
        stroke: '#42acc9',
        strokeWidth: 4,
        left: centerPoints.x,
        top: centerPoints.y,
        originX: 'center',
        originY: 'center',
        floorplan: {
          objectType: 'focus_wave',
          type: 'focus_wave',
        },
        ...viewerProps,
      })
      canvas.add(this.focusElement)
      this.focusTheElement(this.focusElement)
    },
    loadUsedVariables() {
      if (this.canvas && this.graphicUtil) {
        let usedVariables = []
        this.canvas.forEachObject(obj => {
          let uv = this.graphicUtil.getUsedVariables(obj)
          if (uv && uv.length) {
            usedVariables.push(...uv)
          }
        })
        this.usedVariables = usedVariables
      }
      // if (!this.currentAsset.record) {
      //   this.refreshLiveData()
      // }
    },
    updateVariable() {
      if (!this.graphicUtil) {
        return
      }
      let vars = this.variables
      if (this.usedVariables && this.usedVariables.length) {
        vars = vars.filter(v => this.usedVariables.indexOf(v.key) >= 0)
      } else {
        vars = vars.filter(v => v.key.indexOf('system.') === -1)
        this.canvas.requestRenderAll()
        this.loadingState = false
      }
      this.fetchGraphicsData(vars).then(() => {
        this.graphicUtil.updateAllLiveValues(this.canvas.getObjects(), {
          floorplan: this.floorplan,
          variables: vars,
          liveValues: this.liveValues,
        })
      })
    },
    fetchGraphicsData(paramList, cfShapes) {
      let parameters = []
      parameters.push(paramList)
      if (cfShapes && cfShapes.length) {
        parameters.push(cfShapes)
      }
      let url = '/v2/workflow/getDefaultWorkflowResult'
      let params = {
        defaultWorkflowId: 2,
        paramList: parameters,
      }

      return new Promise((resolve, reject) => {
        this.$http
          .post(url, params)
          .then(response => {
            if (response.data.result.workflow.returnValue) {
              let graphicsData = response.data.result.workflow.returnValue
                .conditionalFormatting
                ? response.data.result.workflow.returnValue.data
                : response.data.result.workflow.returnValue
              for (let key in graphicsData) {
                if (typeof graphicsData[key] === 'object') {
                  this.liveValues[key] = graphicsData[key]
                } else {
                  this.liveValues[key] = {
                    value:
                      graphicsData[key] && typeof graphicsData[key] === 'object'
                        ? graphicsData[key].value
                        : graphicsData[key],
                    unit: paramList.find(p => p.key === key).unit,
                  }
                }
                resolve()
              }

              if (
                this.canvas &&
                response.data.result.workflow.returnValue.conditionalFormatting
              ) {
                let cfMap = {}
                for (let cf of response.data.result.workflow.returnValue
                  .conditionalFormatting) {
                  cfMap[cf.id] = cf
                }

                this.canvas.forEachObject(obj => {
                  if (obj.fgraphic && obj.floorplan.id) {
                    let appliedTheme = obj.get('fgraphic').appliedTheme
                    if (cfMap[obj.floorplan.id]) {
                      cfMap[obj.floorplan.id].appliedTheme = appliedTheme
                      if (!obj.get('actualFgraphic')) {
                        obj.set('actualFgraphic', obj.get('fgraphic'))
                      }
                      obj.set('fgraphic', cfMap[obj.floorplan.id])
                    } else {
                      if (obj.get('actualFgraphic')) {
                        obj.set('fgraphic', obj.get('actualFgraphic'))
                      }
                    }
                  }
                })
              }
            }
          })
          .catch(err => {
            if (err) {
              reject(err)
            }
          })
      })
    },
    areaProperties() {
      if (this.viewMode && this.viewMode === 'readings') {
        this.addHeatMapScale()
      } else {
        this.removeHeatmapScale()
      }
      this.canvas.forEachObject(obj => {
        if (isSpaceZoneElement(obj)) {
          let { spaceId } = obj.floorplan
          let { areas } = this
          let focusspaceId = null
          if (this.focus) {
            focusspaceId = this.focus.spaceId
          }
          let area = areas.find(rt => rt.spaceId === spaceId)
          if (area && !isEmpty(area.icons)) {
            if (area.icons.length) {
              if (focusspaceId && area.spaceId === focusspaceId) {
                this.spaceFocus()
              }
              area.icons.forEach(icon => {
                if (icon.position) {
                  this.addMarker(icon.type, obj, icon.position, icon)
                } else {
                  this.addMarker(icon.type, obj, null, icon)
                }
              })
            }
          }
        }
      })
    },
    updatefloorplanObj(selectedobj) {
      this.canvas.forEachObject(obj => {
        if (
          selectedobj &&
          selectedobj.floorplan &&
          obj.floorplan &&
          obj.floorplan.id === selectedobj.floorplan.id
        ) {
          this.$set(obj, 'floorplan', selectedobj.floorplan)
        }
      })
    },
    updateObjectProps(selectedObj) {
      let floorplanObj = {
        floorplan: this.floorplan,
        variables: this.variables,
        liveValues: this.liveValues,
      }
      this.graphicUtil.updateLiveValue(this.selectedObject, floorplanObj)
      if (this.selectedObject.type === 'image') {
        this.graphicUtil.updateImageFilters(this.selectedObject)
      }
      this.updatefloorplanObj(selectedObj)
      this.canvas.requestRenderAll()
      this.propertiesvisible = false
      this.pointervisible = false
      // this.graphicsListForActions = null
      // this.assetListForActions = null
    },
    updateValueProps() {
      this.canvas.forEachObject(selectedObject => {
        let floorplanObj = {
          floorplan: this.floorplan,
          variables: this.variables,
          liveValues: this.liveValues,
        }
        this.graphicUtil.updateLiveValue(selectedObject, floorplanObj)
        if (selectedObject.type === 'image') {
          this.graphicUtil.updateImageFilters(selectedObject)
        }
      })
    },
    getClickActions(actions, readings) {
      let actionList = []
      if (!actions) {
        if (readings && readings.length) {
          actionList.push({
            actionName: 'Show Trend',
            actionType: 'showTrend',
            data: readings,
          })
        }
      } else {
        if (actions.showTrend.enable && readings && readings.length) {
          actionList.push({
            actionName: 'Show Trend',
            actionType: 'showTrend',
            data: readings,
          })
        }

        if (actions.controlAction.enable) {
          for (let c of actions.controlAction.control_list) {
            actionList.push({
              actionName: c.actionName,
              actionType: 'controlAction',
              data: c,
            })
          }
        }

        if (actions.hyperLink.enable) {
          for (let c of actions.hyperLink.link_list) {
            actionList.push({
              actionName: c.actionName,
              actionType: 'hyperLink',
              data: c,
            })
          }
        }
      }
      return actionList
    },
    updateObject(updatedObj) {
      this.canvas.forEachObject(object => {
        if (updatedObj && updatedObj.floorplan && object.floorplan) {
          if (object.floorplan.id === updatedObj.floorplan.id) {
            // Object.keys(object).forEach(rl => {
            //   object.set(rl, updatedObj[rl])
            // })
            if (object.type === 'group') {
              object._objects.forEach((rt, index) => {
                let { fill } = rt
                let sty = {
                  fill: fill,
                }
                this.setSpaceZoneProps(
                  object,
                  updatedObj._objects[index].type,
                  sty
                )
              })
            }
          }
        }
      })
      this.$nextTick(() => {
        this.canvas.renderAll()
      })
    },
    setImageAsbackground() {
      let { canvas } = this
      let fileURL = `${getBaseURL()}/v2/files/preview/${
        this.fileId
      }?fetchOriginal=true`
      let paddingLeft = 0
      let paddingright = 0
      fabric.Image.fromURL(fileURL, function(img) {
        canvas.setBackgroundImage(img, canvas.renderAll.bind(canvas), {
          scaleX: canvas.width / img.width,
          scaleY: canvas.height / img.height,
          left: paddingLeft,
          top: paddingright,
        })
      })
      return this.canvas.renderAll()
    },
    removeBg() {
      let { canvas } = this
      canvas.setBackgroundColor(null, canvas.renderAll.bind(canvas))
    },
    setBg() {
      return makeBg().then(bg => {
        this.canvas.backgroundColor = bg
        return this.canvas.renderAll()
      })
    },
    handleSpaceDraw(elemnet) {
      let evt = elemnet.e
      if (evt.altKey === true) {
        this.config.isDragging = true
        this.selection = false
        this.config.lastPosX = evt.clientX
        this.config.lastPosY = evt.clientY
      } else {
        if (
          elemnet.target &&
          this.polygonOptions.pointArray.length &&
          elemnet.target.id == this.polygonOptions.pointArray[0].id
        ) {
          this.generatePolygon(this.polygonOptions, this.canvas)
        }
        if (this.polygonOptions.polygonMode) {
          PolygonUtil.addPoint(this.polygonOptions, elemnet, this.canvas)
        }
      }
    },
    activeAction() {
      if (this.activeAll) {
        this.activeAll = false
        this.activeAllObjects()
      }
    },
    prepareRightClickMenu() {
      let menuList = []

      if (this._clipboard) {
        menuList.push({
          label: 'Paste',
          action: 'paste',
          order: 3,
        })
      }

      let activeObjects =
        (this.canvas && this.canvas.getActiveObjects()) || null
      if (activeObjects && activeObjects.length) {
        // menuList.push({
        //   label: 'Cut',
        //   action: 'cut',
        //   order: 1,
        // })
        menuList.push({
          label: 'Copy',
          action: 'copy',
          order: 2,
        })
        // menuList.push({
        //   label: 'Duplicate',
        //   action: 'duplicate',
        //   order: 4,
        // })
        menuList.push({
          label: 'Delete',
          action: 'delete',
          order: 5,
        })

        // if (activeObjects.length === 1) {
        //   menuList.push({
        //     label: 'Properties',
        //     action: 'showProperties',
        //     section: true,
        //     order: 20,
        //   })

        //   if (
        //     activeObjects[0].fgraphic &&
        //     activeObjects[0].fgraphic.styles &&
        //     Object.keys(activeObjects[0].fgraphic.styles).length
        //   ) {
        //     menuList.push({
        //       label: 'Copy Style',
        //       action: 'copyStyle',
        //       order: 6,
        //     })
        //   }
        //   if (
        //     activeObjects[0].fgraphic &&
        //     activeObjects[0].fgraphic.conditionalFormatting &&
        //     activeObjects[0].fgraphic.conditionalFormatting.length
        //   ) {
        //     menuList.push({
        //       label: 'Copy Conditional Formatting',
        //       action: 'copyCF',
        //       order: 8,
        //     })
        //   }
        // }

        menuList.push({
          label: 'Bring to front',
          action: 'bringToFront',
          order: 11,
          section: true,
        })
        menuList.push({
          label: 'Send to back',
          action: 'sendToBack',
          order: 12,
        })

        let typeMap = {}
        let uidMap = {}
        let themeMap = {}
        for (let activeObject of activeObjects) {
          if (activeObject.fgraphic) {
            if (activeObject.fgraphic.uid) {
              uidMap[activeObject.fgraphic.uid] = true
            }
            if (activeObject.fgraphic.type) {
              typeMap[activeObject.fgraphic.type] = true
            }
            if (activeObject.fgraphic.themeGroup) {
              themeMap[activeObject.fgraphic.themeGroup] = true
            }
          }
        }

        if (
          this._clipboardStyle &&
          this.isSameTypeObjects(this._clipboardStyle.type)
        ) {
          menuList.push({
            label: 'Paste Style',
            action: 'pasteStyle',
            order: 7,
          })
        }
        if (
          this._clipboardCF &&
          this.isSameTypeObjects(this._clipboardCF.type)
        ) {
          menuList.push({
            label: 'Paste Conditional Formatting',
            action: 'pasteCF',
            order: 9,
          })
        }

        // if (Object.keys(themeMap).length === 1) {
        //   let themeList = getColorThemes(Object.keys(themeMap)[0]).map(th => {
        //     return {
        //       label: th.label,
        //       value: th.key,
        //     }
        //   })
        //   menuList.push({
        //     label: 'Change Theme',
        //     action: 'changeTheme',
        //     order: 10,
        //     section: true,
        //     showSubMenu: false,
        //     submenu: themeList,
        //   })
        // }

        if (activeObjects.length === 1) {
          menuList.push({
            label: 'Apply to all the space',
            action: 'applytoallthespace',
            order: 10,
          })
        }
      } else {
        menuList.push({
          label: 'Select All',
          action: 'selectAll',
          order: 13,
        })
      }

      return menuList.sort((a, b) => {
        return a.order - b.order
      })
    },
    handleMouseLeftClickEvent() {},
    handleMouseMiddlelickEvent() {},
    handleMouseRightClickEvent(options) {
      let self = this
      let isCanvas = options.path.find(
        p => p.className === 'floor-plan-builder-container col-10 p10'
      )
      if (isCanvas) {
        options.preventDefault()

        self.contextmenu = {
          top: options.clientY + 10,
          left: options.clientX + 10,
          menu: self.prepareRightClickMenu(),
        }
        self.$nextTick(() => {
          if (self.contextmenu && self.$refs['floorplanContextMenu']) {
            self.contextmenu.width =
              self.$refs['floorplanContextMenu'].clientWidth - 5
            self.contextmenu.height =
              self.$refs['floorplanContextMenu'].clientHeight

            let top = options.clientY + 10
            let left = options.clientX + 10
            let bottomSpace = window.innerHeight - options.clientY
            let rightSpace = window.innerWidth - options.clientX
            let divHeight = self.contextmenu.height
            let divWidth = self.contextmenu.width
            if (divHeight > bottomSpace - 40) {
              top = options.clientY - divHeight - 10
            }
            if (divWidth > rightSpace - 20) {
              left = options.clientX - divWidth - 10
            }

            self.contextmenu.top = top
            self.contextmenu.left = left
          }
        })
      }
    },
    setupMobileEvents() {
      let { canvas } = this
      let self = this
      canvas.on('mouse:wheel', function(opt) {
        self.refresh = true
        let delta = opt.e.deltaY

        let zoom = canvas.getZoom()
        zoom = zoom + delta / 200
        if (zoom > 20) zoom = 20
        if (zoom < 0.01) zoom = 0.01
        canvas.zoomToPoint({ x: opt.e.offsetX, y: opt.e.offsetY }, zoom)
        opt.e.preventDefault()
        opt.e.stopPropagation()
      })
      canvas.on('mouse:down', function(opt) {
        let evt = opt.e
        this.isDragging = true
        this.selection = false
        self.touchDargEnd = false
        if (evt.touches && evt.touches.length) {
          let cliEvent = evt.touches[0]
          this.lastPosX = cliEvent.clientX
          this.lastPosY = cliEvent.clientY
        }
      })
      canvas.on('mouse:move', function(opt) {
        if (this.isDragging) {
          self.touchDargEnd = true
          let e = opt.e
          if (e.touches && e.touches.length) {
            let cliEvent = e.touches[0]
            self.refresh = true

            this.viewportTransform[4] += cliEvent.clientX - this.lastPosX
            this.viewportTransform[5] += cliEvent.clientY - this.lastPosY
            this.requestRenderAll()
            this.lastPosX = cliEvent.clientX
            this.lastPosY = cliEvent.clientY
          }
        }
      })
      canvas.on('mouse:up', function() {
        if (this.isDragging) {
          self.refresh = true
          self.onCanvasPanEnd()
        }
      })
      this.isDragging = false
    },
    CenterCoord() {
      let { canvas } = this
      return {
        x:
          fabric.util.invertTransform(canvas.viewportTransform)[4] +
          canvas.width / 2,
        y:
          fabric.util.invertTransform(canvas.viewportTransform)[5] +
          canvas.height / 2,
      }
    },
    setCenterCorinate() {
      this.canvas.absolutePan(new fabric.Point(0, 0))
    },
    resetPanAndZoom() {
      if (this.defaultZoomLevel) {
        this.canvas.setZoom(this.defaultZoomLevel)
      } else {
        this.canvas.setZoom(1)
      }
      this.fitToCenter()
      this.dragMode = false
      this.canvas.trigger('moved')
      this.refresh = false
      this.touchDargEnd = false
    },
    panMode() {
      const STATE_IDLE = 'idle'
      const STATE_PANNING = 'panning'
      fabric.Canvas.prototype.toggleDragMode = function(dragMode) {
        // Remember the previous X and Y coordinates for delta calculations
        let lastClientX
        let lastClientY
        // Keep track of the state
        let state = STATE_IDLE
        // We're entering dragmode
        if (dragMode && this.isDragging) {
          // Discard any active object
          this.discardActiveObject()
          // Set the cursor to 'move'
          this.defaultCursor = 'grab'
          // Loop over all objects and disable events / selectable. We remember its value in a temp variable stored on each object
          this.forEachObject(function(object) {
            object.prevEvented = object.evented
            object.prevSelectable = object.selectable
            object.evented = false
            object.selectable = false
          })
          // Remove selection ability on the canvas
          this.selection = false
          // When MouseUp fires, we set the state to idle
          this.on('mouse:up', function() {
            state = STATE_IDLE
          })
          // When MouseDown fires, we set the state to panning
          this.on('mouse:down', e => {
            state = STATE_PANNING
            lastClientX = e.e.clientX
            lastClientY = e.e.clientY
          })
          // When the mouse moves, and we're panning (mouse down), we continue
          this.on('mouse:move', e => {
            if (state === STATE_PANNING && e && e.e) {
              // let delta = new fabric.Point(e.e.movementX, e.e.movementY); // No Safari support for movementX and movementY
              // For cross-browser compatibility, I had to manually keep track of the delta

              // Calculate deltas
              let deltaX = 0
              let deltaY = 0
              if (lastClientX) {
                deltaX = e.e.clientX - lastClientX
              }
              if (lastClientY) {
                deltaY = e.e.clientY - lastClientY
              }
              // Update the last X and Y values
              lastClientX = e.e.clientX
              lastClientY = e.e.clientY

              let delta = new fabric.Point(deltaX, deltaY)
              this.relativePan(delta)
              this.trigger('moved')
            }
          })
        } else {
          // When we exit dragmode, we restore the previous values on all objects
          this.forEachObject(function(object) {
            object.evented =
              object.prevEvented !== undefined
                ? object.prevEvented
                : object.evented
            object.selectable = false
          })
          // Reset the cursor
          this.defaultCursor = 'default'
          this.trigger('moved')
          // Remove the event listeners
          // Restore selection ability on the canvas
        }
      }
    },
    openSelectedReadingInAnalytics() {
      if (
        this.trendPopup.analyticsConfig.dataPoints &&
        this.trendPopup.analyticsConfig.dataPoints.length
      ) {
        this.trendPopup.visible = false
        this.jumpReadingsToAnalytics(
          this.trendPopup.analyticsConfig.dataPoints,
          null,
          null,
          null
        )
      }
    },
    onSetControlValue() {
      if (this.$account && this.$account.org.id === 321) {
        setTimeout(() => {
          // this.refreshLiveData()
        }, 2000)
      }
      this.resetControlValue()
    },
    resetControlValue() {
      this.controlPopup = {
        visible: false,
        controlConfig: {
          title: null,
          assetId: null,
          fieldId: null,
          groupId: null,
        },
        setVal: null,
        saving: false,
      }
    },
    handleClickAction(action) {
      let selfHost = window.location.protocol + '//' + window.location.host
      if (action.actionType === 'showTrend') {
        this.trendPopup.analyticsConfig.dataPoints = action.data
        this.trendPopup.visible = true
      } else if (action.actionType === 'controlAction') {
        this.controlPopup.controlConfig.title = action.data.actionName
        if (action.data.type === 'control_group') {
          this.controlPopup.controlConfig.groupId = action.data.groupId
          this.controlPopup.controlConfig.assetId = null
          this.controlPopup.controlConfig.fieldId = null
        } else {
          if (
            action.data.assetCategoryId &&
            this.currentAsset.record &&
            this.currentAsset.record.category.id ===
              action.data.assetCategoryId &&
            this.currentAsset.id
          ) {
            this.controlPopup.controlConfig.assetId = this.currentAsset.id
          } else {
            this.controlPopup.controlConfig.assetId = action.data.assetId
          }
          this.controlPopup.controlConfig.fieldId = action.data.fieldId
          this.controlPopup.controlConfig.groupId = null
        }
        this.controlPopup.visible = true
      } else if (action.actionType === 'hyperLink') {
        if (action.data.url || action.data.id) {
          let url = this.graphicUtil.applyLiveVariables(action.data.url, true)
          if (action.data.linkType === 'graphics') {
            url = selfHost + '/app/em/graphics/view/' + action.data.id
            if (action.data.assetId) {
              url += '?assetId=' + action.data.assetId
            }
          }
          if (action.data.target === 'self') {
            if (url.startsWith(selfHost)) {
              if (
                this.$helpers.isLicenseEnabled('NEW_LAYOUT') &&
                url.startsWith(selfHost + '/app/em/newdashboard/')
              ) {
                url = url.replace(
                  selfHost + '/app/em/newdashboard/',
                  selfHost + '/app/home/dashboard/'
                )
              }
              let appUrl = url.replace(selfHost, '')
              this.$router.push({ path: appUrl })
            } else {
              window.location.href = url
            }
          } else if (action.data.target === 'popup') {
            this.$popupView.openPopup({
              type: action.data.linkType,
              url: action.data.url,
              alt: '',
              dashboardId: '',
              reportId: '',
              graphicsId: action.data.id,
              assetId: action.data.assetId,
              target: action.data.target,
            })
          } else {
            window.open(url, '_blank')
          }
        }
      }
      this.clickActions = null
    },
    openAssetGraphics(assetId, assetCategoryId) {
      this.$popupView.openPopup({
        type: 'graphics',
        url: null,
        alt: '',
        dashboardId: '',
        reportId: '',
        graphicsId: null,
        assetId: assetId,
        target: null,
        graphicsContext: {
          assetId: assetId,
          assetCategoryId: assetCategoryId,
        },
      })
    },
    handleshapesAction(e) {
      if (e && e.target) {
        if (isElement(e.target, 'button_group')) {
          let self = this
          if (e.target.floorplan.spaceMappingBindingVariable) {
            let top = e.e.offsetY + 20
            let left = e.e.offsetX + 20
            let bottomSpace = window.innerHeight - e.e.clientY
            let divHeight = 260
            if (divHeight > bottomSpace - 40) {
              top = e.e.offsetY - divHeight
            }

            self.selectedSpaceInfo = {
              top: top,
              left: left,
              space:
                self.liveValues[
                  e.target.floorplan.spaceMappingBindingVariable
                ] &&
                self.liveValues[e.target.floorplan.spaceMappingBindingVariable]
                  .value &&
                self.liveValues[e.target.floorplan.spaceMappingBindingVariable]
                  .value.length
                  ? self.liveValues[
                      e.target.floorplan.spaceMappingBindingVariable
                    ].value[0]
                  : null,
            }
            return
          }

          let readingVariables = []
          if (e.target.floorplan.formatText) {
            let formatText = e.target.floorplan.formatText
            let matched = formatText.match(/[^\\${}]+(?=\})/g)
            if (matched && matched.length) {
              readingVariables.push(...matched)
            }
          }
          if (e.target.floorplan.enableStateBinding) {
            readingVariables.push(e.target.floorplan.stateBindingVariable)
          }
          if (e.target.floorplan.enableAnimateBinding) {
            readingVariables.push(e.target.floorplan.animateBindingVariable)
          }
          let dataPoints = []
          if (readingVariables && readingVariables.length) {
            for (let readingVar of readingVariables) {
              let varObj = self.variables.find(v => v.key === readingVar)
              if (varObj) {
                let readingObj = self.currentAsset.readings
                  ? self.currentAsset.readings.find(
                      rd => rd.name === varObj.select
                    )
                  : null
                let fieldId = null
                if (readingObj) {
                  fieldId = readingObj.id
                } else {
                  if (self.liveValues[readingVar]) {
                    fieldId = self.liveValues[readingVar].fieldid
                  }
                }
                if (fieldId) {
                  dataPoints.push({
                    parentId: varObj.parentId,
                    yAxis: {
                      fieldId: fieldId,
                      aggr: 3,
                    },
                  })
                }
              }
            }
          }
          let clickActions = self.getClickActions(
            e.target.floorplan.actions,
            dataPoints
          )
          if (clickActions && clickActions.length) {
            if (clickActions.length === 1) {
              self.handleClickAction(clickActions[0])
            } else {
              self.clickActions = {
                top: e.e.offsetY + 30,
                left: e.e.offsetX + 20,
                actions: clickActions,
              }
            }
          }
        }
      }
    },
    setupViewerEvents() {
      let { canvas } = this
      let self = this
      canvas.on('mouse:down', function(opt) {
        let evt = opt.e
        // if (evt.altKey === true) {
        this.isDragging = true
        self.refresh = true
        this.selection = false
        this.lastPosX = evt.clientX
        this.lastPosY = evt.clientY
        self.handleshapesAction(opt)
        //
      })
      canvas.on('mouse:move', function(opt) {
        let e = opt.e
        let xDiff = Math.abs(this.lastPosX - e.clientX)
        let yDiff = Math.abs(this.lastPosY - e.clientY)
        let pixelDiff = xDiff > 1 && yDiff > 1 ? true : false //  make pixel diffrence morethan one for dragging
        if (this.isDragging && pixelDiff) {
          this.viewportTransform[4] += e.clientX - this.lastPosX
          this.viewportTransform[5] += e.clientY - this.lastPosY
          this.requestRenderAll()
          this.lastPosX = e.clientX
          this.lastPosY = e.clientY

          self.spaceDetailsPopup = false
          self.spaceButtonPopup = false
        }
      })
      canvas.on('mouse:up', function() {
        if (this.isDragging) {
          self.refresh = true
          self.onCanvasPanEnd()
        }
        this.isDragging = false
        this.selectedElement = null
      })
    },
    setUpEvents() {
      let { canvas } = this
      let self = this
      canvas.on('mouse:down', function(evt) {
        if (self.contextmenu && self.$refs['floorplanContextMenu']) {
          self.contextmenu = null
        }
        self.activeAction(evt)
        self.handleSpaceDraw(evt)
        // if (evt.button === 1) {
        //   self.handleMouseLeftClick(evt)
        // } else if (evt.button === 2) {
        //   self.handleMouseMiddlelick(evt)
        // } else if (evt.button === 3) {
        //   self.handleMouseRightClick(evt)
        // }
      })
      this.canvas.on('mouse:move', function(elemnet) {
        if (self.config.isDragging) {
          let e = elemnet.e
          this.viewportTransform[4] += e.clientX - self.config.lastPosX
          this.viewportTransform[5] += e.clientY - self.config.lastPosY
          this.requestRenderAll()
          self.config.lastPosX = e.clientX
          self.config.lastPosY = e.clientY
        } else {
          if (
            self.polygonOptions.activeLine &&
            self.polygonOptions.activeLine.class == 'line'
          ) {
            let pointer = self.canvas.getPointer(elemnet.e)
            self.polygonOptions.activeLine.set({
              x2: pointer.x,
              y2: pointer.y,
            })

            let points = self.polygonOptions.activeShape.get('points')
            points[self.polygonOptions.pointArray.length] = {
              x: pointer.x,
              y: pointer.y,
            }
            self.polygonOptions.activeShape.set({
              points: points,
            })
            self.canvas.renderAll()
          }
        }
      })
      this.canvas.on(
        'drop',
        function(options) {
          let pointer = this.getPointer(event)
          self.handleDrop(options, pointer)
          return false
        },
        false
      )
    },
    setUpImageViewObjEvents() {
      // view mode image events
    },
    setUpImageObjEvents(image) {
      image.on('mousedblclick', () => {
        this.activeAll = true
        this.activeAllObjects()
        if (this.polygonOptions.polygonMode) {
          this.generatePolygon(this.polygonOptions, this.canvas)
        }
      })
    },
    activeAllObjects() {
      let { canvas } = this
      let { activeAll } = this
      canvas.forEachObject(object => {
        if (object.type === 'floor_plan_image') {
          object.selectable = activeAll
        }
      })
      canvas.discardActiveObject()
      let sel = new fabric.ActiveSelection(canvas.getObjects(), {
        canvas: canvas,
      })
      canvas.setActiveObject(sel)
      canvas.requestRenderAll()
    },
    setSpaceZoneLabel(element, type = 'text', text) {
      element.forEachObject(object => {
        if (object.type === type) {
          this.$set(object, 'text', text)
        }
      })
    },
    setSpaceZoneStyles(element, type, style) {
      element.forEachObject(object => {
        if (object.type === type) {
          Object.keys(style).forEach(rl => {
            object.set(rl, style[rl])
          })
        }
      })
    },
    setSpaceZoneProps(element, type, style) {
      element.forEachObject(object => {
        if (object.type === type) {
          Object.keys(style).forEach(rl => {
            object.set(rl, style[rl])
          })
        }
      })
      this.canvas.requestRenderAll()
    },
    setSpaceZoneFloorProps(element, type, style) {
      element.forEachObject(object => {
        if (object.floorplan.type === type) {
          Object.keys(style).forEach(rl => {
            object.set(rl, style[rl])
          })
        }
      })
      this.canvas.requestRenderAll()
    },
    handleSpaceZoneHoverEvents(spaceZone) {
      spaceZone.hoverCursor = 'pointer'
      let hoverStyles = {
        opacity: 0.8,
      }
      let texthoverStyles = {
        opacity: 1,
      }
      // setInterval(() => {
      //   spaceZone.animate('opacity', spaceZone.opacity === 1 ? 0.5 : 1, {
      //     onChange: (() => {
      //       this.canvas.requestRenderAll()
      //     }).bind(this.canvas),
      //     duration: 100,
      //     easing: fabric.util.ease.easeOutSine,
      //   })
      // }, 800)

      this.setSpaceZoneProps(spaceZone, 'polygon', hoverStyles)
      this.setSpaceZoneProps(spaceZone, 'text', texthoverStyles)
      if (!isEmpty(this.selectedArea)) {
        if (this.selectedArea[0].tooltip) {
          // this.spaceDetailsPopup = false
          this.tooltipPopup = true
        }
      }
    },
    handleSpaceZoneMouseOutEvents(spaceZone) {
      let mouseOutStyles = {
        opacity: 0.4,
      }
      let texthoverStyles = {
        opacity: 1,

        fontSize: '15',
      }
      this.setSpaceZoneProps(spaceZone, 'polygon', mouseOutStyles)
      this.setSpaceZoneProps(spaceZone, 'text', texthoverStyles)
    },
    setupSpaceZoneEvents(spaceZone) {
      spaceZone.on('mouseover', elemnet => {
        if (window.orgId == 434) {
          this.getSelectedArea(elemnet)
        }
        if (!isEmpty(this.selectedArea)) {
          if (this.selectedArea[0].tooltip) {
            this.selectedElement = elemnet
          }
        }
        this.handleSpaceZoneHoverEvents(spaceZone)
      })
      spaceZone.on('mouseout', elemnet => {
        // this.tooltipPopup = false
        if (!isEmpty(this.selectedArea)) {
          if (this.selectedArea[0].tooltip) {
            this.selectedElement = elemnet
          }
        }
        this.handleSpaceZoneMouseOutEvents(spaceZone)
      })
    },
    setuppointerEvents(marker) {
      marker.on('mouseover', element => {
        element.target.set('hoverCursor', 'pointer')
        this.selectedElement = element
        this.pointerpopup = true
      })
      marker.on('mouseout', () => {
        this.selectedElement = null
        this.pointerpopup = false
      })
    },
    setelementEvents(marker) {
      marker.on('mouseover', element => {
        element.target.set('hoverCursor', 'pointer')
        this.selectedElement = element
        this.elementpopup = true
      })
      marker.on('mouseout', () => {
        this.selectedElement = null
        this.elementpopup = false
      })
    },
    setupMarkerEvents(marker) {
      marker.on('mouseover', () => {
        marker.hoverCursor = 'pointer'
      })
      marker.on('mouseout', () => {
        this.handleSpaceZoneMouseOutEvents(marker)
      })
    },
    getListofSpaceElemnts(element) {
      this.mapedElementsList = []
      let { target } = element
      if (target && target.floorplan && target.floorplan.spaceId) {
        this.mapedElementsList = this.getListOfMappedElemnet(
          'button_group',
          target.floorplan.spaceId
        )
      }
    },
    setUpViwerObjectEvents(obj) {
      if (isElement(obj, 'floor_plan_image')) {
        this.setUpImageViewObjEvents(obj)
      }
      if (isElement(obj, 'space_zone_group')) {
        this.setupSpaceZoneEvents(obj)
      }
      if (isElement(obj, 'marker')) {
        this.setupMarkerEvents(obj)
      }
      if (isElement(obj, 'pointer_group')) {
        this.applyPointerStyles(obj)
        this.setuppointerEvents(obj)
      }
      if (isElement(obj, 'element_group')) {
        // this.applyPointerStyles(obj)
        this.setelementEvents(obj)
      }

      obj.on('mousedown', element => {
        let { target } = element
        this.getSelectedArea(element)
        if (!isEmpty(target)) {
          if (
            isElement(target, 'marker') &&
            isObjectType(target, 'control') &&
            isMappedElement(target, 'marker') &&
            window.orgId !== 343
          ) {
            this.selectedElement = element
            this.selectPopUp()
          } else if (
            isElement(target, 'space_zone_group') &&
            isMappedElement(target)
          ) {
            this.selectedElement = element
            if ([343, 410, 155].includes(window.orgId)) {
              this.getListofSpaceElemnts(element)
              this.spaceButtonPopup = true
            } else {
              this.spaceDetailsPopup = true
            }
          } else if (isElement(target, 'pointer_group')) {
            this.selectedElement = element
            this.pointerDetailsPopup = true
            // this.setSpaceZoneFloorProps(target, 'pointer_active_circle', {
            //   fill: 'red',
            // })
          } else {
            this.setPointerDefaultStyles()
            this.selectedElement = null
            this.spaceDetailsPopup = false
            this.pointerDetailsPopup = false
            this.spaceButtonPopup = false
          }
        }
      })
    },
    closePointerdetailPopup() {
      this.selectedElement = null
      this.pointerDetailsPopup = false
    },
    setPointerDefaultStyles() {},
    selectPopUp() {
      if (this.viewMode) {
        let canvasMeta = {
          width: this.canvas.width,
          height: this.canvas.height,
        }
        let data = {
          floorPlan: this.floorPlan,
          element: this.selectedElement,
          spaceList: this.spaceList,
          area: this.selectedArea,
          icons: this.selectedArea[0].icons,
          canvasMeta: canvasMeta,
        }
        if (this.$refs['marker-actions']) {
          this.$refs['marker-actions'].executeAction(this.viewMode, data)
        }
        switch (this.viewMode) {
          case 'default':
            break
          case 'maintenance':
            break
          case 'control_points':
            this.floorActionVisible = true
            break
          case 'reservation':
            break
          case 'spacecategory':
            break
          case 'asset':
            break
          case 'readings':
            break
        }
      } else if (!this.isEdit) {
        this.floorActionVisible = true
      }
    },
    handleSpaceZoneObject() {},
    setUpObjectEvents(obj) {
      if (isElement(obj, 'floor_plan_image')) {
        this.setUpImageObjEvents(obj)
      } else {
        obj.on('object:selected', element => {
          let object = element.target
          this.canvas.sendToBack(object)
        })
        obj.on('moved', element => {
          this.handeleObjectMove(element)
        })
        obj.on('drop', () => {})
        obj.on('mousedown', element => {
          let { target } = element
          if (!isEmpty(target)) {
            if (
              isElement(target, 'space_zone_group') &&
              isNotMappedElement(target)
            ) {
              this.selectedElement = element
              this.floorMapVisible = true
            } else if (
              isElement(target, 'marker') &&
              isObjectType(target, 'control') &&
              isMappedElement(target, 'marker') &&
              window.orgId !== 343
            ) {
              this.selectedElement = element
              this.selectPopUp()
            }
          }
        })
        obj.on('mousedblclick', element => {
          let { target } = element
          this.selectedObject = null
          if (!isEmpty(target)) {
            if (
              isElement(target, 'space_zone_group') &&
              isMappedElement(target)
            ) {
              this.selectedObject = obj
              this.objectSettingVisible = true
            } else if (
              isElement(target, 'marker') &&
              isObjectType(target, 'control') &&
              isMappedElement(target, 'marker') &&
              [343, 410].includes(window.orgId)
            ) {
              this.selectedObject = obj
              this.propertiesvisible = true
            } else if (isElement(target, 'button_group')) {
              this.selectedObject = obj
              this.propertiesvisible = true
            } else if (isElement(target, 'pointer_group')) {
              this.selectedObject = obj
              this.pointervisible = true
            }
          }
        })
      }
    },
    keyupHandler(event) {
      if (event.keyCode === 27) {
        PolygonUtil.deletePolygonArray(this.polygonOptions, this.canvas)
        this.polygonOptions = {
          polygonMode: false,
          pointArray: new Array(),
          lineArray: new Array(),
          activeLine: null,
          activeShape: false,
        }
      }
    },
    setUpViewerProps(obj) {
      if (obj) {
        Object.keys(viewerProps).forEach(rt => {
          obj.set(rt, viewerProps[rt])
        })
      }
    },
    setUpEditorProps(obj) {
      if (
        isElement(obj, 'floor_plan_image') ||
        isElement(obj, 'space_zone_group')
      ) {
        Object.keys(EditorProps).forEach(rt => {
          obj.set(rt, EditorProps[rt])
        })
      }
    },
    unLockProps() {
      this.canvas.forEachObject(obj => {
        if (
          isElement(obj, 'floor_plan_image') ||
          isElement(obj, 'space_zone_group')
        ) {
          Object.keys(UnlockProps).forEach(rt => {
            obj.set(rt, UnlockProps[rt])
          })
        }
      })
    },
    setupCommonObjProps(obj) {
      // if (isElement(obj, 'floor_plan_image')) {
      //   obj.selectable = false
      // } else if (isElement(obj, 'space_zone_group')) {
      //   obj.selectable = false
      // }
      Object.keys(tempProps).forEach(rt => {
        obj.set(rt, tempProps[rt])
        obj.selectable = false
      })
    },
    handeleObjectMove(element) {
      let { target } = element
      if (isMarkerElement(target)) {
        // to reset the spaceId out of boundries
      }
    },
    handleRightClickAction(menu, context, subMenu) {
      if (menu.action === 'selectAll') {
        this.selectAll()
      } else if (menu.action === 'showProperties') {
        // this.showProperties()
      } else if (menu.action === 'changeTheme') {
        if (subMenu) {
          // this.changeTheme(subMenu)
        }
      } else if (menu.action === 'cut') {
        // this.cut()
      } else if (menu.action === 'copy') {
        this.copy()
      } else if (menu.action === 'paste') {
        this.paste({
          left: Math.ceil(context.left / 10) * 10,
          top: Math.ceil(context.top / 10) * 10,
        })
      } else if (menu.action === 'duplicate') {
        // this.duplicate()
      } else if (menu.action === 'delete') {
        this.deleteActiveObject()
      } else if (menu.action === 'copyStyle') {
        // this.copyStyle()
      } else if (menu.action === 'pasteStyle') {
        // this.pasteStyle()
      } else if (menu.action === 'copyCF') {
        // this.copyCF()
      } else if (menu.action === 'pasteCF') {
        // this.pasteCF()
      } else if (menu.action === 'bringToFront') {
        this.bringToFront()
      } else if (menu.action === 'sendToBack') {
        this.sendToBack()
      } else if (menu.action === 'applytoallthespace') {
        this.applyMarker()
      }
      this.contextmenu = null
    },
    handleAction(action, mode, data) {
      this.refresh = true
      this.canvas.requestRenderAll()
      if (action === 'layer') {
        this.handleLayer()
      } else if (action === 'addPolygon') {
        this.addPolygon()
      } else if (action === 'zoomIn') {
        this.zoomIn()
      } else if (action === 'zoomOut') {
        this.zoomOut()
      } else if (action === 'rotateRight') {
        this.rotateRight()
      } else if (action === 'rotateLeft') {
        this.rotateLeft()
      } else if (action === 'goRight') {
        this.goRight()
      } else if (action === 'goLeft') {
        this.goLeft()
      } else if (action === 'goUp') {
        this.goUp()
      } else if (action === 'goDown') {
        this.goDown()
      } else if (action === 'resetPanAndZoom') {
        this.resetPanAndZoom()
      } else if (action === 'enableMarkerValue') {
        this.handleMarkerValue()
      } else if (action === 'unlock') {
        this.unLockProps()
      } else if (action === 'viewMode') {
        this.$emit('view', mode, data)
      } else if (action === 'addButton') {
        this.addButton()
      } else if (action === 'addPointer') {
        this.addPointer()
      } else if (action === 'addUtilMarker') {
        this.addUtilMarker(mode)
      }
    },
    handleDrag(obj) {
      let self = this
      if (!isEmpty(obj)) {
        if (isVariable(obj)) {
          self.currentDragElement = self.getTextElement(obj)
        } else if (obj && obj.object_id) {
          let shape = findShape(obj.object_id)
          self.getShape(shape, function(obj) {
            self.currentDragElement = obj
          })
        }
      }
    },
    addViewerMarkers(target, pointer, icon) {
      let { currentDragElement } = this
      let { canvas } = this
      let x = pointer.x
      let y = pointer.y
      if (pointer) {
        x = pointer.x
        y = pointer.y
      }
      if (currentDragElement.floorplan.uid) {
        let width = currentDragElement.get('width')
        let height = currentDragElement.get('height')
        if (width) {
          x = x - width / 2 < 0 ? 0 : x - width / 2
        }
        if (height) {
          y = y - height / 2 < 0 ? 0 : y - height / 2
        }
      }
      currentDragElement.set({
        left: Math.ceil(x / 10) * 10,
        top: Math.ceil(y / 10) * 10,
        ...viewerProps,
      })
      if (!isEmpty(currentDragElement) && !isEmpty(target)) {
        let spaceId = this.getSpaceId(target)
        currentDragElement.floorplan.spaceId = spaceId
        if (icon && icon.value && icon.value.value) {
          let label = `${icon.value.value} ${icon.value.unit}`
          this.applyCustomeMarkerValue(currentDragElement, label)
        }
        canvas.add(currentDragElement)
        this.setUpObjectEvents(currentDragElement)
      }
    },
    addHeatMapScale() {
      let { canvas, leagend } = this
      let { layout } = leagend
      let rect1 = new fabric.Rect({
        left: layout.width / 4,
        top: layout.height,
        width: layout.width / 2,
        height: 10,
        fill: '#ffda4f',
        floorplan: {
          id: 'heatmapBar',
          objectType: 'heatmapBar',
          type: 'heatmapBar',
        },
        ...viewerProps,
      })
      rect1.setGradient('fill', {
        type: 'linear',
        x1: 0,
        y1: 0,
        x2: rect1.width,
        y2: 0,
        colorStops: {
          0: '#1b7f01',
          0.2: '#7fc001',
          0.4: '#ffff01',
          0.6: '#fb8002',
          0.8: '#fb8002',
          1: '#fa1000',
        },
      })
      canvas.add(rect1)
      this.setUpObjectEvents(rect1)
    },
    removeHeatmapScale() {
      this.canvas.forEachObject(() => {})
    },
    addMarkers(target, pointer) {
      let { currentDragElement } = this
      let { canvas } = this
      let x = pointer.x
      let y = pointer.y
      if (pointer) {
        x = pointer.x
        y = pointer.y
      }
      if (currentDragElement.floorplan.uid) {
        let width = currentDragElement.get('width')
        let height = currentDragElement.get('height')
        if (width) {
          x = x - width / 2 < 0 ? 0 : x - width / 2
        }
        if (height) {
          y = y - height / 2 < 0 ? 0 : y - height / 2
        }
      }
      currentDragElement.set({
        left: Math.ceil(x / 10) * 10,
        top: Math.ceil(y / 10) * 10,
      })
      if (!isEmpty(currentDragElement) && !isEmpty(target)) {
        let spaceId = this.getSpaceId(target)
        currentDragElement.floorplan.spaceId = spaceId
        canvas.add(currentDragElement)
        this.setUpObjectEvents(currentDragElement)
      }
    },
    applyMarker() {
      let object = this.canvas.getActiveObjects()
      if (object && object.length) {
        let activeObject = object[0]
        let { floorplan } = activeObject
        this.handleLayerInfo(floorplan)
      }
    },
    handleVariables(elemnet, pointer) {
      let { currentDragElement } = this
      let { canvas } = this
      let e = elemnet.e
      let x = e.layerX - canvas.viewportTransform[4]
      let y = e.layerY - canvas.viewportTransform[5]
      if (pointer) {
        x = pointer.x
        y = pointer.y
      }
      let width = currentDragElement.get('width')
      let height = currentDragElement.get('height')
      if (width) {
        x = x - width / 2 < 0 ? 0 : x - width / 2
      }
      if (height) {
        y = y - height / 2 < 0 ? 0 : y - height / 2
      }
      currentDragElement.set({
        left: Math.ceil(x / 10) * 10,
        top: Math.ceil(y / 10) * 10,
      })
      if (!isEmpty(currentDragElement)) {
        this.canvas.add(currentDragElement)
        this.canvas.requestRenderAll()
      }
    },
    handleMarkerElement(elemnet, pointer) {
      let { currentDragElement } = this
      let { canvas } = this
      let { target } = elemnet
      let e = elemnet.e
      let x = e.layerX - canvas.viewportTransform[4]
      let y = e.layerY - canvas.viewportTransform[5]
      if (pointer) {
        x = pointer.x
        y = pointer.y
      }
      if (currentDragElement.floorplan.uid) {
        let width = currentDragElement.get('width')
        let height = currentDragElement.get('height')
        if (width) {
          x = x - width / 2 < 0 ? 0 : x - width / 2
        }
        if (height) {
          y = y - height / 2 < 0 ? 0 : y - height / 2
        }
      }
      currentDragElement.set({
        left: Math.ceil(x / 10) * 10,
        top: Math.ceil(y / 10) * 10,
      })
      if (!isEmpty(currentDragElement) && !isEmpty(target)) {
        let spaceId = this.getSpaceId(target)
        currentDragElement.floorplan.spaceId = spaceId
        if ([343, 410].includes(window.orgId)) {
          this.setObjectActions(currentDragElement)
          this.setUpObjectEvents(currentDragElement)
        }
        this.canvas.add(currentDragElement)
      } else if (!isEmpty(currentDragElement)) {
        this.canvas.add(currentDragElement)
      }
    },
    getSpaceId(elemnet) {
      if (isMappedElement(elemnet)) {
        return elemnet.floorplan.spaceId
      }
      return null
    },
    handleLayerInfo(data) {
      this.layer = data.layer
      this.sublayer = data.sublayer
      this.populateLayerInfo(data)
    },
    handleDrop(elemnet, pointer) {
      let { currentDragElement } = this
      if (isMarkerElement(currentDragElement)) {
        this.handleMarkerElement(elemnet, pointer)
      } else if (isVariableElement(currentDragElement)) {
        this.handleVariables(elemnet, pointer)
      }
    },
    spaceIdMap(target) {
      let { currentDragElement } = this
      if (target.floorplan.id) {
        if (this.canvas) {
          this.canvas.forEachObject(obj => {
            let { floorplan } = obj
            if (
              !isEmpty(floorplan) &&
              floorplan.id &&
              floorplan.id === target.floorplan.id
            ) {
              floorplan.spaceId = currentDragElement.floorplan.spaceId
            }
          })
          this.canvas.renderAll()
        }
      }
    },
    getTextElement(obj) {
      let layerInfo = {
        layer: this.layer,
        sublayer: this.sublayer,
      }
      let textElm = new fabric.Text('test', {
        floorplan: {
          enableTextBinding: true,
          formatText: obj.label + ': ${' + obj.key + '}',
          blink: false,
          hide: false,
          animateEffect: 'none',
          id: getUniqueId(),
          type: obj.type,
          objectType: obj.objectType,
          variableType: obj.variableType,
          variableObj: obj,
          styles: {
            fill: '',
            textBackgroundColor: '',
            backgroundColor: '',
            fontFamily: 'Aktiv-Grotesk',
            textAlign: '',
            lineHeight: null,
            charSpacing: null,
            fontWeight: '',
            fontStyle: '',
          },
        },
        ...layerInfo,
        fill: '#0c0c0c',
        scaleX: 0.3,
        scaleY: 0.3,
      })
      return textElm
    },
    setupBackground() {
      let objects = this.canvas.getObjects()
      if (
        isEmpty(objects) ||
        objects.forEach(obj => !isElement(obj, 'floor_plan_image'))
      ) {
        let self = this
        let layerInfo = {
          layer: this.layer,
          sublayer: this.sublayer,
        }
        let fileURL = `${getBaseURL()}/v2/files/preview/${
          this.fileId
        }?fetchOriginal=true`
        fabric.Image.fromURL(fileURL, imgObj => {
          let filters = {
            brightness: null,
            contrast: null,
            saturation: null,
            hue: null,
            noise: null,
            pixelate: null,
            blur: null,
          }
          let obj = imgObj.set({
            floorplan: {
              objectType: 'image',
              type: 'floor_plan_image',
              blink: false,
              hide: false,
              animateEffect: 'none',
              filters: filters,
              ...layerInfo,
              id: getUniqueId(),
            },
            hoverCursor: 'pointer',
            left: 50,
            top: 50,
            hasRotatingPoint: false,
            lockRotation: true,
            hasControls: false,
            hasBorders: false,
            lockScalingX: true,
            lockScalingY: true,
            index: 0,
            selectable: false,
          })
          if (self.leagend.fitToscreen) {
            self.setFloorPlanDimentions(imgObj)
          }
          self.setUpImageObjEvents(obj)
          self.canvas.add(obj)
        })
      }
      this.canvas.renderAll()
    },
    setFloorPlanDimentions(imgObj) {
      let { imageLayout } = this.leagend
      let { canvas } = this
      if (imageLayout) {
        let width = canvas.width - 100
        let height = canvas.height - 100
        imgObj.scaleToHeight(height)
        imgObj.scaleToWidth(width)
      }
    },
    setDimensions() {
      let container = this.$refs['floorPlanContainer']

      if (isEmpty(container)) return
      // let { imageLayout } = this.leagend
      let width = container.offsetWidth
      let height = container.offsetHeight
      // if (!isEmpty(imageLayout)) {
      //   height = imageLayout.height
      //   width = imageLayout.width
      // }

      this.canvas.setHeight(height)
      this.canvas.setWidth(width)
      this.canvas.calcOffset()
      this.canvas.renderAll()
    },
    setFitDimensions() {
      let container = this.$refs['floorPlanContainer']

      if (isEmpty(container)) return

      this.canvas.setHeight(container.offsetHeight)
      this.canvas.setWidth(container.offsetWidth)
      this.canvas.calcOffset()
      this.canvas.renderAll()
    },
    fitToFloormapViewResolution() {},
    fitToResolution() {
      let container = this.$refs['floorPlanContainer']

      if (isEmpty(container)) return
      let width = 1200
      let height = 1000
      if (this.leagend && this.leagend.layout) {
        let { layout } = this.leagend
        width = layout.width
        height = layout.height
      }
      let widthChanges = container.offsetWidth / width
      let heightChanges = container.offsetHeight / height
      let z = widthChanges > heightChanges ? heightChanges : widthChanges
      if (z > 0.4 && z < 1) {
        this.defaultZoomLevel = z
        this.canvas.setZoom(z)
        this.setCenterCorinate()
        this.canvas.requestRenderAll()
        this.onCanvasPanEnd()
      }
    },
    fitToCenter() {
      this.setCenterCorinate()
      this.onCanvasPanEnd()
      let image = this.getFloorPlanImageElement()
      // let elementCenter = image.getCenterPoint()
      let elementCenter = getCoordinatesCenter(image.getCoords())
      let cnco = this.CenterCoord()
      let x = elementCenter.x - cnco.x
      let y = elementCenter.y - cnco.y
      if (image) {
        this.centerPoints = {
          x: x,
          y: y,
        }
        this.canvas.absolutePan(this.centerPoints)
      } else {
        this.canvas.absolutePan(new fabric.Point(0, 0))
      }
      this.canvas.requestRenderAll()
    },
    setInitialZoom() {
      let zoom = !isEmpty(this.diagramJson && this.diagramJson.zoom)
        ? this.diagramJson.zoom
        : 0.75

      this.canvas.setZoom(zoom)
      this.currentZoomLevel = zoom
    },

    adjustZoom(zoomIn = false, delta = 0.125) {
      let zoom = this.canvas.getZoom()

      if (zoomIn) {
        zoom += delta
      } else {
        zoom -= delta
      }

      if (zoom > 1.75) zoom = 1.75
      if (zoom < 0.01) zoom = 0.01

      this.canvas.setZoom(zoom)
      this.currentZoomLevel = zoom
      this.$nextTick(() => this.autoSave())
    },

    fitToScreen() {
      let delta = 0
      let edgeBuffer = 70 * 2 // Create a 70px buffer on both sides

      let canvas = this.canvas
      let zoom = canvas.getZoom()
      let canvasH = canvas.getHeight() - edgeBuffer
      let canvasW = canvas.getWidth() - edgeBuffer

      // Create a group with all objects and take the dimensions from it
      // https://github.com/fabricjs/fabric.js/issues/1745
      let objs = canvas.getObjects()
      let g = new fabric.Group(objs)
      let { width, height } = g.getBoundingRect()
      g.ungroupOnCanvas()

      width *= zoom
      height *= zoom

      let shouldScaleDown = width > canvasW || height > canvasH
      let shouldScaleUp = width < canvasW && height < canvasH

      if (shouldScaleDown || shouldScaleUp) {
        let Wx = canvasW / width
        let Hx = canvasH / height

        delta = Wx < Hx ? Wx : Hx

        if (zoom > 1.75) zoom = 1.75
        if (zoom < 0.5) zoom = 0.5

        canvas.setZoom(zoom * delta)
        this.currentZoomLevel = zoom * delta
      }
    },

    onCanvasPanStart({ e }) {
      this.isCanvasPanning = true
      this.lastPosX = e.clientX
      this.lastPosY = e.clientY

      this.canvas.defaultCursor = 'grabbing'
      this.canvas.renderAll()
    },

    onCanvasPan: throttle(function({ e }) {
      // https://stackoverflow.com/a/52504899
      if (this.isCanvasPanning) {
        let canvas = this.canvas

        let xOffset = e.clientX - this.lastPosX
        let yOffset = e.clientY - this.lastPosY
        let xDiffWidth = this.allowedCanvasPanSize - canvas.getWidth()
        let yDiffHeight = this.allowedCanvasPanSize - canvas.getHeight()

        let transformArray = canvas.viewportTransform

        let isXWithinBounds =
          transformArray[4] + xOffset >= -xDiffWidth &&
          transformArray[4] + xOffset <= 0
        let isYWithinBounds =
          transformArray[5] + yOffset >= -yDiffHeight &&
          transformArray[5] + yOffset <= 0

        if (isXWithinBounds) {
          transformArray[4] += xOffset
          this.lastPosX = e.clientX
          canvas.requestRenderAll()
        } else if (transformArray[4] + xOffset >= -xDiffWidth) {
          transformArray[4] = 0
          this.lastPosX = e.clientX
          canvas.requestRenderAll()
        }

        if (isYWithinBounds) {
          transformArray[5] += yOffset
          this.lastPosY = e.clientY
          canvas.requestRenderAll()
        } else if (transformArray[5] + yOffset >= -yDiffHeight) {
          transformArray[5] = 0
          this.lastPosY = e.clientY
          canvas.requestRenderAll()
        }
      }
    }, 75),

    onCanvasPanEnd() {
      this.isCanvasPanning = false
      this.canvas.getObjects().forEach(obj => obj.setCoords())

      this.canvas.defaultCursor = 'grab'
      this.$nextTick(() => this.canvas.renderAll())
    },
  },
}
</script>
