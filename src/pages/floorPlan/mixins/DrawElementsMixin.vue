<script>
import { fabric } from 'fabric'
import { isElement, commonObjProps, getUniqueId } from '../elements/Common'
const CURRENT_MARKER_FIELD_PREFIX = key => {
  return 'currentMarker.' + key
}
export default {
  methods: {
    getlayerInfo() {
      let { layer } = this
      let { sublayer } = this
      let layerInfo = {
        layer: layer,
        sublayer: sublayer,
      }
      return layerInfo
    },
    addUtilMarker(mode) {
      let canvas = this.canvas
      let svg = ''
      let uid = 1
      let options = {
        floorplan: {
          uid: uid,
          name: '',
          markerType: null,
          blink: false,
          hide: false,
          animateEffect: 'none',
          id: getUniqueId(),
          spaceId: null,
          objectType: 'element_group',
          type: 'element_group',
        },
        hoverCursor: 'pointer',
      }
      if (mode === '1') {
        uid = 1 + '_1'
        options.floorplan.name = 'CCTV'
        svg =
          '<svg xmlns="http://www.w3.org/2000/svg" width="512" height="512"><rect width="512" height="512" rx="20" ry="20" fill="#5b616b" transform="matrix(.1 0 0 .1 230.4 230.4)"/><path d="M261.47 261.31l7.325-9.737-19.118-7.64-4.364 10.919 7.49 2.994-.92 2.296a2.886 2.886 0 00-2.961 1.975h-2.86v-3.976h-5.422v9.926h5.422v-4.156h2.86a2.886 2.886 0 002.737 1.985 2.885 2.885 0 002.882-2.882c0-.871-.39-1.652-1.003-2.182l.931-2.32 7.001 2.798zm-1.51-5.567l1.667.665-.667 1.666-1.665-.665.666-1.666zM269.326 253.851l-4.935 6.56 4.029 1.61 2.94-7.357z" fill="#fff" data-original="#000000" xmlns="http://www.w3.org/2000/svg"/></svg>'
      } else if (mode === '2') {
        options.floorplan.name = 'LIFT'
        uid = 1 + '_2'

        svg =
          '<svg xmlns="http://www.w3.org/2000/svg" width="512" height="512"><rect width="512" height="512" rx="20" ry="20" fill="#5b616b"/><path d="M341.2 142.4H136.72a5.675 5.675 0 00-5.68 5.68v284c0 3.141 2.538 5.68 5.68 5.68s5.68-2.539 5.68-5.68V153.76h193.12v278.32c0 3.141 2.538 5.68 5.68 5.68s5.68-2.539 5.68-5.68v-284a5.675 5.675 0 00-5.68-5.68z" fill="#fffefe" data-original="#000000" xmlns="http://www.w3.org/2000/svg"/><path d="M324.16 159.44h-170.4a5.675 5.675 0 00-5.68 5.68v266.96c0 3.141 2.538 5.68 5.68 5.68s5.68-2.539 5.68-5.68V170.8h159.04v261.28c0 3.141 2.538 5.68 5.68 5.68s5.68-2.539 5.68-5.68V165.12a5.675 5.675 0 00-5.68-5.68zM379.295 257.664a5.674 5.674 0 00-8.031 0l-11.36 11.36a5.674 5.674 0 000 8.032 5.664 5.664 0 004.015 1.664 5.664 5.664 0 004.016-1.664l11.36-11.36a5.674 5.674 0 000-8.032z" fill="#fffefe" data-original="#000000" xmlns="http://www.w3.org/2000/svg"/><path d="M390.655 269.024l-11.36-11.36a5.674 5.674 0 00-8.031 0 5.674 5.674 0 000 8.032l11.36 11.36a5.664 5.664 0 004.015 1.664 5.664 5.664 0 004.016-1.664 5.674 5.674 0 000-8.032zM390.655 303.104a5.674 5.674 0 00-8.031 0l-11.36 11.36a5.674 5.674 0 000 8.032 5.664 5.664 0 004.015 1.664 5.664 5.664 0 004.016-1.664l11.36-11.36a5.674 5.674 0 000-8.032z" fill="#fffefe" data-original="#000000" xmlns="http://www.w3.org/2000/svg"/><path d="M379.295 314.464l-11.36-11.36a5.674 5.674 0 00-8.031 0 5.674 5.674 0 000 8.032l11.36 11.36a5.664 5.664 0 004.015 1.664 5.664 5.664 0 004.016-1.664 5.674 5.674 0 000-8.032zM238.96 159.44a5.675 5.675 0 00-5.68 5.68v266.96c0 3.141 2.538 5.68 5.68 5.68s5.68-2.539 5.68-5.68V165.12a5.675 5.675 0 00-5.68-5.68z" fill="#fffefe" data-original="#000000" xmlns="http://www.w3.org/2000/svg"/><path d="M352.56 426.4h-227.2c-3.142 0-5.68 2.539-5.68 5.68s2.538 5.68 5.68 5.68h227.2c3.14 0 5.68-2.539 5.68-5.68s-2.54-5.68-5.68-5.68zM238.96 74.24c-28.19 0-51.12 22.93-51.12 51.12 0 3.141 2.538 5.68 5.68 5.68s5.68-2.539 5.68-5.68c0-21.92 17.84-39.76 39.76-39.76s39.76 17.84 39.76 39.76c0 3.141 2.538 5.68 5.68 5.68s5.68-2.539 5.68-5.68c0-28.19-22.93-51.12-51.12-51.12z" fill="#fffefe" data-original="#000000" xmlns="http://www.w3.org/2000/svg"/><path d="M284.4 119.68h-90.88c-3.142 0-5.68 2.539-5.68 5.68s2.538 5.68 5.68 5.68h90.88c3.14 0 5.68-2.539 5.68-5.68s-2.54-5.68-5.68-5.68z" fill="#fffefe" data-original="#000000" xmlns="http://www.w3.org/2000/svg"/><path d="M238.96 96.96a5.675 5.675 0 00-5.68 5.68v22.72c0 3.141 2.538 5.68 5.68 5.68s5.68-2.539 5.68-5.68v-22.72a5.675 5.675 0 00-5.68-5.68z" fill="#fffefe" data-original="#000000" xmlns="http://www.w3.org/2000/svg"/></svg>'
      } else if (mode === '3') {
        options.floorplan.name = 'WIFI'

        uid = 1 + '_3'

        svg =
          '<svg xmlns="http://www.w3.org/2000/svg" width="512" height="512" viewBox="0 0 493.746 493.746"><rect width="493.746" height="493.746" rx="20" ry="20" fill="#5b616b"/><g xmlns="http://www.w3.org/2000/svg"><path d="M219.394 334.911c-15.173 15.175-15.173 39.79 0 54.965 15.18 15.172 39.792 15.172 54.972 0 15.172-15.175 15.172-39.79 0-54.965-15.18-15.175-39.791-15.175-54.972 0zM246.88 242.481c-32.021 0-62.146 12.473-84.777 35.126-8.89 8.886-8.89 23.29 0 32.17 8.884 8.886 23.28 8.886 32.17 0 14.038-14.057 32.726-21.795 52.607-21.795s38.57 7.738 52.607 21.795a22.721 22.721 0 0016.09 6.664 22.64 22.64 0 0016.08-6.664c8.891-8.88 8.891-23.283 0-32.17-22.63-22.653-52.755-35.126-84.777-35.126z" fill="#fff" data-original="#000000"/><path d="M246.88 167.57c-52.042 0-100.967 20.267-137.759 57.064-8.883 8.886-8.883 23.291 0 32.17 8.89 8.887 23.294 8.887 32.17 0 28.198-28.206 65.703-43.737 105.588-43.737 39.87 0 77.375 15.53 105.574 43.737a22.69 22.69 0 0016.082 6.666 22.654 22.654 0 0016.088-6.666c8.884-8.879 8.884-23.284 0-32.17-36.792-36.797-85.7-57.065-137.743-57.065z" fill="#fff" data-original="#000000"/><path d="M437.723 171.551c-50.968-50.986-118.74-79.06-190.843-79.06s-139.874 28.074-190.843 79.06c-8.883 8.888-8.883 23.292 0 32.172a22.694 22.694 0 0016.089 6.663c5.82 0 11.656-2.22 16.081-6.663 42.376-42.392 98.736-65.733 158.673-65.733 59.939 0 116.281 23.341 158.659 65.733 8.881 8.886 23.278 8.878 32.17.008 8.882-8.888 8.882-23.292.014-32.18z" fill="#fff" data-original="#000000"/></g></svg>'
      } else if (mode === '4') {
        options.floorplan.name = 'FIRE EXTINGUISHER'
        uid = 1 + '_4'

        svg =
          '<svg xmlns="http://www.w3.org/2000/svg" width="512" height="512" viewBox="0 0 128 128"><rect width="128" height="128" rx="20" ry="20" fill="#5b616b"/><path xmlns="http://www.w3.org/2000/svg" d="M100.3 20.313A4.317 4.317 0 0095.985 16H71.91a8.827 8.827 0 00-8.502 6.518h-2.94A28.345 28.345 0 0032.155 50.83v23.312a4.546 4.546 0 00-2.617 5.92l1.88 4.769-3.703 25.669a1.312 1.312 0 001.3 1.5H43.92a1.312 1.312 0 001.3-1.5l-3.703-25.67 1.88-4.769a4.545 4.545 0 00-2.617-5.919V50.83a19.71 19.71 0 0119.688-19.687h2.625v4.5a27.118 27.118 0 00-12.2 22.532v49.513A4.317 4.317 0 0055.206 112h33.41a4.317 4.317 0 004.312-4.312V58.175a26.458 26.459 0 00-.269-3.712 1.322 1.322 0 00-.023-.145A27.15 27.15 0 0080.73 35.643v-5.722l5.526 3.191a7.318 7.318 0 009.989-2.677 1.312 1.313 0 00-.48-1.794l-6.956-4.016h7.176a4.317 4.317 0 004.315-4.312zm-2.625 0A1.69 1.69 0 0195.985 22H80.26a8.836 8.836 0 00-2.08-3.375h17.806a1.69 1.69 0 011.69 1.688zm-25.764-1.688a6.2 6.2 0 016.194 6.194v10.215H65.718V24.819c0-.15.012-.3.023-.45a8.04 8.04 0 01.038-.366v-.008a6.199 6.199 0 016.132-5.37zm-41.381 90.75l3.375-23.389h5.127l3.375 23.389zm10.425-30.276l-1.682 4.262h-5.612L31.98 79.1a1.922 1.922 0 011.788-2.625h5.4a1.922 1.922 0 011.787 2.625zm19.513-50.581A22.337 22.337 0 0038.155 50.83v23.016H34.78V50.83a25.716 25.716 0 0125.688-25.687h2.625v3.375zM90.303 98.69H53.518V94.47h36.785zm-1.687 10.685h-33.41a1.69 1.69 0 01-1.688-1.687v-6.375h36.785v6.375a1.69 1.69 0 01-1.687 1.687zm1.687-51.2v22.7H79.317V55.94h10.87c.07.74.116 1.485.116 2.235zm-.507-4.86H78.005a1.312 1.312 0 00-1.313 1.312V82.19a1.312 1.312 0 001.313 1.31h12.3v8.344H53.518v-33.67A24.481 24.482 0 0164.782 37.66h14.259A24.505 24.505 0 0189.8 53.315zm3.357-23.151a4.694 4.694 0 01-5.585.675L80.73 26.89v-2.07c0-.065-.01-.13-.01-.194h2.84z" fill="#fff" data-original="#000000"/></svg>'
      }

      fabric.loadSVGFromString(svg, function(objects) {
        let groupedObject = fabric.util.groupSVGElements(objects)
        let extendedOptions = fabric.util.object.extend(options)
        groupedObject.set(extendedOptions)
        canvas.add(groupedObject)
      })
      this.canvas.requestRenderAll()
    },
    addPointer() {
      let fgraphic = {
        enableButtonBinding: true,
        formatText: '',
        blink: false,
        hide: false,
        animateEffect: 'none',
        id: getUniqueId(),
        employeeId: null,
        styles: {
          fill: '',
          fontFamily: 'Aktiv-Grotesk',
          textAlign: '',
          lineHeight: null,
          charSpacing: null,
          fontWeight: '',
          fontStyle: '',
          fontColor: '#FFFFFF',
          backgroundColor: '#C95DB4',
          padding: 20,
          radius: 5,
        },
        hoverCursor: 'pointer',
        objectType: 'pointer_group',
        type: 'pointer_group',
        ...this.getlayerInfo(),
      }

      let bg = new fabric.Circle({
        fill: '#000',
        originX: 'center',
        originY: 'center',
        radius: 6,
        opacity: 1,
        floorplan: {
          objectType: 'pointer_bg_circle',
          type: 'pointer_bg_circle',
          id: getUniqueId(),
          ...this.getlayerInfo(),
        },
      })

      let center = new fabric.Circle({
        fill: '#fff',
        originX: 'center',
        originY: 'center',
        radius: 2,
        opacity: 1,
        floorplan: {
          objectType: 'pointer_center_circle',
          type: 'pointer_center_circle',
          id: getUniqueId(),
          ...this.getlayerInfo(),
        },
      })

      let activeT = new fabric.Circle({
        fill: 'transparent',
        originX: 'center',
        originY: 'center',
        radius: 8,
        opacity: 1,
        floorplan: {
          objectType: 'pointer_activeT_circle',
          type: 'pointer_activeT_circle',
          id: getUniqueId(),
          ...this.getlayerInfo(),
        },
      })

      let active = new fabric.Circle({
        fill: 'transparent',
        originX: 'center',
        originY: 'center',
        radius: 10,
        opacity: 1,
        floorplan: {
          objectType: 'pointer_active_circle',
          type: 'pointer_active_circle',
          id: getUniqueId(),
          ...this.getlayerInfo(),
        },
      })

      let group = new fabric.Group([active, activeT, bg, center], {
        left: 50,
        top: 100,
        hoverCursor: 'pointer',
      })

      group.set({
        floorplan: fgraphic,
      })

      this.canvas.add(group)
      this.setObjectActions(group)
      this.setUpObjectEvents(group)
    },
    addButton() {
      let fgraphic = {
        enableButtonBinding: true,
        formatText: 'Button',
        blink: false,
        hide: false,
        animateEffect: 'none',
        id: getUniqueId(),
        styles: {
          fill: '',
          fontFamily: 'Aktiv-Grotesk',
          textAlign: '',
          lineHeight: null,
          charSpacing: null,
          fontWeight: '',
          fontStyle: '',
          fontColor: '#FFFFFF',
          backgroundColor: '#C95DB4',
          padding: 20,
          radius: 5,
        },
        objectType: 'button_group',
        type: 'button_group',
        ...this.getlayerInfo(),
      }
      let text = new fabric.Text('Button', {
        fontSize: 15,
        originX: 'center',
        originY: 'center',
        fill: fgraphic.styles.fontColor,
        floorplan: {
          objectType: 'button_text',
          type: 'button',
          id: getUniqueId(),
          ...this.getlayerInfo(),
        },
      })

      let textWidth = text.get('width')
      let textHeight = text.get('height')

      let bg = new fabric.Rect({
        fill: fgraphic.styles.backgroundColor,
        scaleY: 0.5,
        originX: 'center',
        originY: 'center',
        rx: fgraphic.styles.radius,
        ry: fgraphic.styles.radius,
        width: textWidth + fgraphic.styles.padding * 2,
        height: textHeight + fgraphic.styles.padding * 2,
        floorplan: {
          objectType: 'button_rect',
          type: 'button_rect',
          id: getUniqueId(),
          ...this.getlayerInfo(),
        },
      })

      let group = new fabric.Group([bg, text], {
        left: 50,
        top: 100,
      })

      group.set({
        floorplan: fgraphic,
      })

      this.canvas.add(group)
      this.setObjectActions(group)
      this.setUpObjectEvents(group)
    },
    setObjectActions(object) {
      if (!object || !object.floorplan) {
        return
      }

      this.controlPoints = []
      this.controlGroups = []

      let showTrend = true
      let controlAction = false
      let controlActionList = [
        {
          type: 'control_point',
          actionName: null,
          assetId: null,
          assetCategoryId: null,
          fieldId: null,
          pointId: null,
          groupId: null,
        },
      ]
      let hyperLink = false
      let hyperLinkList = [
        {
          actionName: null,
          linkType: null,
          assetId: null,
          id: null,
          url: null,
          target: '_blank',
        },
      ]
      if (object.floorplan.actions) {
        if (object.floorplan.actions.showTrend.enable) {
          showTrend = true
        } else {
          showTrend = false
        }

        if (object.floorplan.actions.controlAction.enable) {
          controlAction = true
          if (object.floorplan.actions.controlAction.control_list) {
            controlActionList =
              object.floorplan.actions.controlAction.control_list
          }
        } else {
          controlAction = false
        }

        if (object.floorplan.actions.hyperLink.enable) {
          hyperLink = true
          if (object.floorplan.actions.hyperLink.link_list) {
            hyperLinkList = object.floorplan.actions.hyperLink.link_list
            hyperLinkList.forEach(rt => {
              if (!rt.hasOwnProperty('assetId')) {
                this.$set(rt, 'assetId', null)
              }
            })
          }
        } else {
          hyperLink = false
        }
      }
      this.objectActions = {
        showTrend: {
          enable: showTrend,
        },
        controlAction: {
          enable: controlAction,
          control_list: controlActionList,
        },
        hyperLink: {
          enable: hyperLink,
          link_list: hyperLinkList,
        },
        invokeFunction: {
          enable: false,
          function_list: [],
        },
      }
    },
    generatePolygon(polygonOptions, canvas) {
      let points = new Array()
      let { layer } = this
      let { sublayer } = this
      let layerInfo = {
        layer: layer,
        sublayer: sublayer,
      }
      for (let point of polygonOptions.pointArray) {
        points.push({
          x: point.left,
          y: point.top,
        })
        canvas.remove(point)
      }
      for (let line of polygonOptions.lineArray) {
        canvas.remove(line)
      }
      canvas
        .remove(polygonOptions.activeShape)
        .remove(polygonOptions.activeLine)

      let elementArray = []
      let polygon = new fabric.Polygon(points, {
        stroke: '#333333',
        strokeWidth: 0.8,
        fill: '#b0b0b0',
        opacity: 0.5,
        perPixelTargetFind: true,
        floorplan: {
          objectType: 'space_poly',
          type: 'polygon',
          id: getUniqueId(),
          ...layerInfo,
        },
      })
      elementArray.push(polygon)
      let center = polygon._findCenterFromElement()
      let textEl = new fabric.Text('', {
        left: center.x,
        top: center.y,
        originX: 'center',
        originY: 'center',
        fontFamily: '"Aktiv-Grotesk", Helvetica, Arial, sans-serif',
        fill: '#25243e',
        setStroke: '#fff',
        fontSize: '15',
        perPixelTargetFind: true,
        floorplan: {
          ...layerInfo,
          id: getUniqueId(),
          type: 'text',
          objectType: 'space_zone_label',
        },
        ...commonObjProps,
      })
      elementArray.push(textEl)

      let group = new fabric.Group(elementArray, {
        lockMovementX: true,
        lockMovementY: true,
        hoverCursor: 'pointer',
        opacity: 0.6,
        perPixelTargetFind: true,
        ...commonObjProps,
        floorplan: {
          spaceId: null,
          enableSpaceMapping: true,
          styles: {},
          ...layerInfo,
          id: getUniqueId(),
          objectType: 'space_zone',
          type: 'space_zone_group',
          fill: '#ff0000',
          actions: {},
        },
      })
      canvas.add(group)

      this.setUpObjectEvents(group)
      polygonOptions.activeLine = null
      polygonOptions.activeShape = null
      polygonOptions.polygonMode = false

      canvas.forEachObject(function(object) {
        object.set('selectable', true)
      })
      canvas.selection = true
    },
    getShape(shape, callback) {
      let { layer } = this
      let { sublayer } = this
      let layerInfo = {
        layer: layer,
        sublayer: sublayer,
      }
      let options = {
        floorplan: {
          uid: shape.uid,
          type: 'marker',
          objectType: 'control',
          markerType: shape._name || null,
          blink: false,
          hide: false,
          animateEffect: 'none',
          id: getUniqueId(),
          ...layerInfo,
          spaceId: null,
        },
        hoverCursor: 'pointer',
      }
      let shapeSvg = shape.template.svg
      let { floorplan } = options
      if (shape.states) {
        floorplan.enableStateBinding = true
        floorplan.stateBindingVariable = null
      }
      if (shape.animate) {
        floorplan.enableAnimateBinding = true
        floorplan.animateBindingVariable = null
        floorplan.animate = shape.animate
      }
      let rectOption = shape.rectOption
      let svgObject = null
      let rect = new fabric.Rect({
        width: 40,
        height: 40,
        left: 0,
        top: 0,
        strokeWidth: 1,
        ...rectOption,
      })
      // let center = rect._findCenterFromElement()
      let textOption = shape.textOption
      let textEl = new fabric.Text('', {
        formatText: '${' + CURRENT_MARKER_FIELD_PREFIX['LABEL'] + '}',
        floorplan: {
          ...layerInfo,
          id: getUniqueId(),
          type: 'text',
          objectType: 'marker_label',
        },
        ...textOption,
        ...commonObjProps,
      })
      let aditionalOptions = {
        left: 0,
        top: 0,
      }
      fabric.loadSVGFromString(shapeSvg, function(objects) {
        svgObject = fabric.util.groupSVGElements(objects)
        let extendedOptions = fabric.util.object.extend(
          shape.options,
          options,
          aditionalOptions
        )
        svgObject.set(extendedOptions)
      })
      let groupedObject = new fabric.Group([rect, svgObject, textEl])
      let groupedOtions = fabric.util.object.extend(shape.options, options)
      groupedObject.set(groupedOtions)
      callback(groupedObject)
    },
  },
}
</script>
