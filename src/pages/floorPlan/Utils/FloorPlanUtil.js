import fabric from 'fabric'
import shapes from 'pages/floorPlan/elements/marker.js'
import { applyColorTheme } from 'pages/assets/graphics/ColorThemes'
import * as d3 from 'd3'

export default {
  Floorplan(canvas, readonly, graphicsObj) {
    this.canvas = canvas
    this.readonly = readonly
    let destroyed = false

    let reg = new RegExp('^1|true|open|on$', 'i')

    let timeUpdate = 0
    let timerObj = null
    let animate = function() {
      if (canvas && graphicsObj && !destroyed) {
        let liveValueMap = graphicsObj.liveValues

        timeUpdate += 100
        if (timeUpdate >= 1000) {
          timeUpdate = 0
        }

        let modified = false
        canvas.forEachObject(function(object) {
          if (object.floorplan && object.floorplan.enableAnimateBinding) {
            if (object.floorplan.animate) {
              let animateUID = object.floorplan.animate.uid
              let animateElm =
                object.floorplan.uid === animateUID
                  ? object
                  : object
                      .getObjects()
                      .find(
                        oe => oe.floorplan && oe.floorplan.uid === animateUID
                      )
              if (animateElm) {
                let liveVal =
                  liveValueMap[object.floorplan.animateBindingVariable]

                if (liveVal && reg.test(liveVal.value + '')) {
                  if (object.floorplan.animate.type === 'rotate') {
                    let angle = animateElm.get('angle')

                    if (
                      object.floorplan.animate.direction === 'anti-clockwise'
                    ) {
                      if (!angle) {
                        angle = 360
                      }
                      angle -= 30
                      if (angle <= 0) {
                        angle = 360
                      }
                    } else {
                      if (!angle) {
                        angle = 0
                      }
                      angle += 30
                      if (angle > 360) {
                        angle = 0
                      }
                    }

                    animateElm.set('angle', angle)
                    animateElm.dirty = true
                    modified = true
                  }
                }
              }
            } else {
              // old shapes
              let animateElm =
                object.getObjects().length === 1
                  ? object.getObjects()[0]
                  : object.getObjects()[1]
              let liveVal =
                liveValueMap[object.floorplan.animateBindingVariable]

              if (
                liveVal &&
                (reg.test(liveVal.value + '') || liveVal.value > 1)
              ) {
                let rotateBy = object.floorplan.animateRotateBy
                  ? object.floorplan.animateRotateBy
                  : 'clockwise'
                let angle = animateElm.get('angle')

                if (rotateBy === 'anti-clockwise') {
                  if (!angle) {
                    angle = 360
                  }
                  angle -= 30
                  if (angle <= 0) {
                    angle = 360
                  }
                } else {
                  if (!angle) {
                    angle = 0
                  }
                  angle += 30
                  if (angle > 360) {
                    angle = 0
                  }
                }
                animateElm.set('angle', angle)
                animateElm.dirty = true
                modified = true
              }
            }
          } else if (
            object.floorplan &&
            (typeof object.floorplan.blink !== 'undefined' ||
              typeof object.floorplan.animateEffect !== 'undefined') &&
            !object.floorplan.hide
          ) {
            if (timeUpdate === 0 || timeUpdate === 500) {
              if (
                object.floorplan.animateEffect &&
                object.floorplan.animateEffect !== 'none'
              ) {
                let posKey = ''
                let actualPosKey = ''
                let step = 0
                if (object.floorplan.animateEffect === 'move_right_effect') {
                  posKey = 'left'
                  actualPosKey = 'actualLeft'
                  step = 15
                } else if (
                  object.floorplan.animateEffect === 'move_left_effect'
                ) {
                  posKey = 'left'
                  actualPosKey = 'actualLeft'
                  step = -15
                } else if (
                  object.floorplan.animateEffect === 'move_bottom_effect'
                ) {
                  posKey = 'top'
                  actualPosKey = 'actualTop'
                  step = -15
                } else if (
                  object.floorplan.animateEffect === 'move_top_effect'
                ) {
                  posKey = 'top'
                  actualPosKey = 'actualTop'
                  step = 15
                }
                if (posKey && actualPosKey) {
                  let pos = object.get(posKey)
                  let actualPos = object.get(actualPosKey)
                  if (!actualPos) {
                    object.set(actualPosKey, pos)
                    actualPos = pos
                  }
                  if (pos !== actualPos) {
                    object.set('opacity', 1)
                  } else {
                    object.set('opacity', 0.1)
                  }
                  object.animate(
                    posKey,
                    pos === actualPos ? pos + step : actualPos,
                    {
                      onChange: canvas.renderAll.bind(canvas),
                      duration: 100,
                    }
                  )
                }
              } else {
                let existOpacity = object.get('opacity') || 0
                if (object.floorplan.blink) {
                  object.set('opacity', existOpacity === 1 ? 0 : 1)
                  object.dirty = true
                  modified = true
                } else {
                  if (!existOpacity) {
                    object.set('opacity', existOpacity === 1 ? 0 : 1)
                    object.dirty = true
                    modified = true
                  }
                }
              }
            }
          } else if (
            object.floorplan &&
            object.floorplan.formatText &&
            object.floorplan.formatText.indexOf('${system.currentTime}') >= 0
          ) {
            let ftext = object.floorplan.formatText
            if (timeUpdate === 0) {
              ftext = ftext.replace(
                '${system.currentTime}',
                liveValueMap['system.currentTime'].value()
              )
              object.set({
                text: ftext,
              })
              modified = true
            }
          }
        })

        if (modified) {
          canvas.requestRenderAll()
        }
        if (timerObj) {
          clearTimeout(timerObj)
          timerObj = null
        }
        timerObj = setTimeout(animate, 100)
      }
    }
    animate()

    this.destroy = function() {
      destroyed = true
    }
    ;(this.addShape = function(uid, options) {
      options || (options = {})

      let shape = shapes[uid]

      fabric.fabric.loadSVGFromString(shape.svg, function(objects) {
        let groupedObject = fabric.fabric.util.groupSVGElements(objects)

        let extendedOptions = fabric.fabric.util.object.extend(
          shape.options,
          options
        )

        groupedObject.set(extendedOptions)

        canvas.add(groupedObject)
      })
    }),
      (this.updateShape = function(object, svgTemplate) {
        let shape = this.findShape(object.floorplan.uid)
        fabric.fabric.loadSVGFromString(svgTemplate, function(objects) {
          let parentObject = fabric.fabric.util.groupSVGElements(objects)
          let groupedObject = null
          if (shape && shape.children && shape.children.length) {
            let index = 0
            let childList = []
            let getAllChild = () => {
              let currentShape = shape.children[index]
              if (!currentShape) {
                groupedObject = new fabric.fabric.Group([
                  parentObject,
                  ...childList,
                ])
                return
              }
              fabric.fabric.loadSVGFromString(
                currentShape.template.svg,
                function(objects) {
                  let childObj = fabric.fabric.util.groupSVGElements(objects)

                  let extendedOptions = fabric.fabric.util.object.extend(
                    currentShape.options,
                    {
                      floorplan: {
                        uid: currentShape.uid,
                      },
                    }
                  )
                  childObj.set(extendedOptions)
                  childList.push(childObj)

                  index++
                  getAllChild()
                }
              )
            }
            getAllChild()
          } else {
            groupedObject = parentObject
          }

          let positionScaleX = object.get('scaleX')
          let positionScaleY = object.get('scaleY')
          let positionLeft = object.get('left')
          let positionTop = object.get('top')
          let positionAngle = object.get('angle')
          let width = object.get('width')
          let height = object.get('height')

          groupedObject.set({
            scaleX: positionScaleX,
            scaleY: positionScaleY,
            top: positionTop,
            left: positionLeft,
            angle: positionAngle,
            floorplan: object.floorplan,
            width: width,
            height: height,
          })

          if (object.actualfloorplan) {
            groupedObject.set('actualfloorplan', object.actualfloorplan)
          }

          if (readonly) {
            groupedObject.set('selectable', false)
            if (
              object.floorplan.actions &&
              (object.floorplan.actions.showTrend.enable ||
                object.floorplan.actions.controlAction.enable ||
                object.floorplan.actions.hyperLink.enable)
            ) {
              groupedObject.set('hoverCursor', 'pointer')
            } else {
              groupedObject.set('hoverCursor', 'default')
            }
          }

          let idx = canvas.getObjects().indexOf(object)
          canvas.remove(object)
          canvas.add(groupedObject)
          object = groupedObject
          groupedObject.moveTo(idx)
        })
      }),
      (this.getShape = function(shape, theme, callback) {
        let options = {
          floorplan: {
            uid: shape.uid,
            type: 'shape',
            blink: false,
            hide: false,
            animateEffect: 'none',
            id: this.getUniqueId(),
          },
        }
        let shapeSvg = shape.template.svg
        if (shape.template.themeGroup) {
          options.floorplan.theme = theme || 'default'
          options.floorplan.appliedTheme = theme || 'default'
          options.floorplan.themeGroup = shape.template.themeGroup
          shapeSvg = applyColorTheme(
            options.floorplan.themeGroup,
            options.floorplan.theme,
            shape.template.svg
          )
        }

        if (shape.states) {
          options.floorplan.enableStateBinding = true
          options.floorplan.stateBindingVariable = null
        }
        if (shape.animate) {
          options.floorplan.enableAnimateBinding = true
          options.floorplan.animateBindingVariable = null
          options.floorplan.animate = shape.animate
        }

        fabric.fabric.loadSVGFromString(shapeSvg, function(objects) {
          let parentObject = fabric.fabric.util.groupSVGElements(objects)

          if (shape.children && shape.children.length) {
            let index = 0
            let childList = []
            let getAllChild = () => {
              let currentShape = shape.children[index]
              if (!currentShape) {
                let groupedObject = new fabric.fabric.Group([
                  parentObject,
                  ...childList,
                ])
                let extendedOptions = fabric.fabric.util.object.extend(
                  shape.options,
                  options
                )
                groupedObject.set(extendedOptions)

                if (
                  shape.uid === 'com.facilio.graphics.shape.pipe.horizontal'
                ) {
                  groupedObject.setControlsVisibility({
                    bl: false,
                    br: false,
                    tl: false,
                    tr: false,
                    mb: false,
                    mt: false,
                  })
                } else if (
                  shape.uid === 'com.facilio.graphics.shape.pipe.vertical'
                ) {
                  groupedObject.setControlsVisibility({
                    bl: false,
                    br: false,
                    tl: false,
                    tr: false,
                    ml: false,
                    mr: false,
                  })
                }

                callback(groupedObject)
                return
              }
              fabric.fabric.loadSVGFromString(
                currentShape.template.svg,
                function(objects) {
                  let childObj = fabric.fabric.util.groupSVGElements(objects)

                  let extendedOptions = fabric.fabric.util.object.extend(
                    currentShape.options,
                    {
                      floorplan: {
                        uid: currentShape.uid,
                      },
                    }
                  )
                  childObj.set(extendedOptions)
                  childList.push(childObj)

                  index++
                  getAllChild()
                }
              )
            }
            getAllChild()
          } else {
            let extendedOptions = fabric.fabric.util.object.extend(
              shape.options,
              options
            )
            parentObject.set(extendedOptions)

            if (shape.uid === 'com.facilio.graphics.shape.pipe.horizontal') {
              parentObject.setControlsVisibility({
                bl: false,
                br: false,
                tl: false,
                tr: false,
                mb: false,
                mt: false,
              })
            } else if (
              shape.uid === 'com.facilio.graphics.shape.pipe.vertical'
            ) {
              parentObject.setControlsVisibility({
                bl: false,
                br: false,
                tl: false,
                tr: false,
                ml: false,
                mr: false,
              })
            }

            callback(parentObject)
          }
        })
      }),
      (this.applyLiveVariables = function(str, applyRawValue) {
        if (!str) {
          return str
        }
        let readingVariables = []
        let matched = str.match(/[^\\${}]+(?=\})/g)
        if (matched && matched.length) {
          readingVariables.push(...matched)
        }

        if (readingVariables && readingVariables.length) {
          for (let rv of readingVariables) {
            let liveVal = this.getLiveValue(graphicsObj.liveValues[rv])
            let replaceVal = ''
            if (liveVal) {
              if (applyRawValue) {
                replaceVal = liveVal.value
              } else {
                replaceVal = liveVal.label
              }
            }
            if (!replaceVal) {
              replaceVal = ''
            }
            str = str.replace('${' + rv + '}', replaceVal)
          }
        }
        return str
      }),
      (this.getUsedVariables = function(fobj) {
        let readingVariables = []
        if (!fobj.floorplan) {
          return readingVariables
        }
        if (fobj.floorplan.formatText) {
          let formatText = fobj.floorplan.formatText
          let matched = formatText.match(/[^\\${}]+(?=\})/g)
          if (matched && matched.length) {
            readingVariables.push(...matched)
          }
        }
        if (
          fobj.floorplan.enableStateBinding &&
          fobj.floorplan.stateBindingVariable
        ) {
          readingVariables.push(fobj.floorplan.stateBindingVariable)
        }
        if (
          fobj.floorplan.enableAnimateBinding &&
          fobj.floorplan.animateBindingVariable
        ) {
          readingVariables.push(fobj.floorplan.animateBindingVariable)
        }
        if (
          fobj.floorplan.enableSpaceMapping &&
          fobj.floorplan.spaceMappingBindingVariable
        ) {
          readingVariables.push(fobj.floorplan.spaceMappingBindingVariable)
        }
        if (
          fobj.floorplan.conditionalFormatting &&
          fobj.floorplan.conditionalFormatting.length
        ) {
          for (let cf of fobj.floorplan.conditionalFormatting) {
            if (cf.criteria && cf.criteria.conditions) {
              for (let cond in cf.criteria.conditions) {
                readingVariables.push(cf.criteria.conditions[cond].fieldName)
              }
            }
          }
        }
        return readingVariables
      }),
      (this.updateImageFilters = function(object) {
        if (object.floorplan.filters.brightness !== null) {
          object.filters[0] = new fabric.fabric.Image.filters.Brightness({
            brightness: object.floorplan.filters.brightness * 0.01,
          })
        }
        if (object.floorplan.filters.contrast !== null) {
          object.filters[1] = new fabric.fabric.Image.filters.Contrast({
            contrast: object.floorplan.filters.contrast * 0.01,
          })
        }
        if (object.floorplan.filters.saturation !== null) {
          object.filters[2] = new fabric.fabric.Image.filters.Saturation({
            saturation: object.floorplan.filters.saturation * 0.01,
          })
        }
        if (object.floorplan.filters.hue != null) {
          object.filters[3] = new fabric.fabric.Image.filters.HueRotation({
            rotation: object.floorplan.filters.hue * 0.01,
          })
        }
        if (object.floorplan.filters.blur != null) {
          object.filters[4] = new fabric.fabric.Image.filters.Blur({
            blur: object.floorplan.filters.blur * 0.01,
          })
        }
        object.applyFilters()
      }),
      (this.getUniqueId = function(liveVal) {
        return (
          Date.now().toString(36) +
          Math.random()
            .toString(36)
            .substr(2, 5)
        ).toLowerCase()
      }),
      (this.getLiveValue = function(liveVal) {
        if (!liveVal) {
          return {
            label: '##',
            value: null,
          }
        } else {
          let formattedVal = liveVal.value
          if (typeof liveVal.value === 'object') {
            if (liveVal.value) {
              if (liveVal.value.length) {
                formattedVal = liveVal.value[0].name
              } else {
                formattedVal = liveVal.value.name
              }
            }
          }
          if (
            formattedVal === null ||
            formattedVal === -1 ||
            typeof formattedVal === 'undefined'
          ) {
            formattedVal = '--'
          }
          if (liveVal.enumMap) {
            if (formattedVal === true) {
              formattedVal = liveVal.enumMap[formattedVal + '']
                ? liveVal.enumMap[formattedVal + '']
                : liveVal.enumMap[1]
            } else if (formattedVal === false) {
              formattedVal = liveVal.enumMap[formattedVal + '']
                ? liveVal.enumMap[formattedVal + '']
                : liveVal.enumMap[0]
            } else {
              formattedVal = liveVal.enumMap[formattedVal + '']
                ? liveVal.enumMap[formattedVal + '']
                : formattedVal
            }
          }

          if (typeof formattedVal === 'number') {
            if ((formattedVal + '').indexOf('.') >= 0) {
              // decimal numbers
              formattedVal = d3.format(',.2f')(formattedVal)
            }
          }
          if (liveVal.unit && formattedVal !== '--') {
            formattedVal = formattedVal + ' ' + liveVal.unit
          }
          return {
            label: formattedVal,
            value: liveVal.value,
          }
        }
      }),
      (this.updateLiveValue = function(object, graphicsObj) {
        let usedVariables = this.getUsedVariables(object)
        let liveValueMap = graphicsObj.liveValues
        let hoverCursor = false
        if (object) {
          if (object.floorplan && object.floorplan.formatText) {
            let ftext = object.floorplan.formatText
            if (usedVariables && usedVariables.length) {
              hoverCursor = true
              for (let usedVar of usedVariables) {
                let formattedVal = '--'
                if (liveValueMap[usedVar]) {
                  if (typeof liveValueMap[usedVar].value === 'function') {
                    formattedVal = liveValueMap[usedVar].value()
                  } else {
                    formattedVal = this.getLiveValue(liveValueMap[usedVar])
                      .label
                  }
                }
                ftext = ftext.replace('${' + usedVar + '}', formattedVal)
              }
            }

            if (object.floorplan.type === 'button_group') {
              hoverCursor = true
              let txt = object
                .getObjects()
                .find(o => o.floorplan.objectType === 'button_text')
              if (txt) {
                let text1 = new fabric.fabric.Text(ftext, {
                  fontSize: 15,
                  originX: 'center',
                  originY: 'center',
                  fill: '#000000',
                  floorplan: {
                    objectType: 'button_text',
                  },
                })

                txt.set({
                  text: ftext,
                  width: text1.get('width'),
                  height: text1.get('height'),
                })
              }
            } else {
              object.set({
                text: ftext,
              })
            }
          } else if (
            object.floorplan &&
            object.floorplan.stateBindingVariable
          ) {
            let liveVal = null
            if (liveValueMap[object.floorplan.stateBindingVariable]) {
              liveVal =
                typeof liveValueMap[object.floorplan.stateBindingVariable]
                  .value === 'function'
                  ? liveValueMap[object.floorplan.stateBindingVariable].value()
                  : liveValueMap[object.floorplan.stateBindingVariable].value
            }

            if (typeof object.floorplan.blink !== 'undefined') {
              let shape = this.findShape(object.floorplan.uid)
              if (shape.states) {
                let trueRegx = new RegExp('^1|true|open$', 'i')
                let falseRegx = new RegExp('^0|false|close$', 'i')

                let currentState = 'close'
                if (trueRegx.test(liveVal + '') || liveVal > 0) {
                  currentState = 'open'
                } else if (falseRegx.test(liveVal + '')) {
                  currentState = 'close'
                }

                let currentStateObj = shape.states.find(
                  s => s.state === currentState
                )
                if (currentStateObj) {
                  object.floorplan.appliedTheme = object.floorplan.theme
                  let appliedThemeSvg = applyColorTheme(
                    currentStateObj.template.themeGroup,
                    object.floorplan.theme,
                    currentStateObj.template.svg
                  )
                  this.updateShape(object, appliedThemeSvg)
                }
              }
            } else {
              let shape = shapes[object.floorplan.uid]
              if (shape.state) {
                for (let key in shape.state) {
                  let regx = new RegExp('^' + key + '$', 'i')
                  if (liveVal === null) {
                    liveVal = 0
                  }
                  if (regx.test(liveVal + '')) {
                    let stateSvg = shape.state[key]
                    this.updateShape(object, stateSvg)
                  }
                }
              }
            }
          } else if (
            object.floorplan &&
            object.floorplan.spaceMappingBindingVariable
          ) {
            if (graphicsObj.occupancyColorScale) {
              let liveValObj =
                liveValueMap[object.floorplan.spaceMappingBindingVariable]
              if (liveValObj) {
                let spaceId = liveValObj.value[0].id
                let occupancyCount =
                  graphicsObj.occupancyData.spaceOccupancyData[spaceId + '']
                occupancyCount = occupancyCount ? occupancyCount : 0
                if (occupancyCount) {
                  let occupancyColor = graphicsObj.occupancyColorScale(
                    occupancyCount
                  )
                  object.set('fill', occupancyColor)
                } else {
                  object.set('fill', '#9c9c9b')
                }
                object.set('opacity', 0.2)
              }
            } else if (graphicsObj.fireAlarm) {
              let liveValObj =
                liveValueMap[object.floorplan.spaceMappingBindingVariable]
              if (liveValObj) {
                let spaceId = liveValObj.value[0].id
                if (graphicsObj.fireAlarm[spaceId]) {
                  object.set('fill', '#FF0000')
                  object.set('opacity', 0.2)
                }
              }
            } else {
              let liveValObj = this.getLiveValue(
                liveValueMap[object.floorplan.spaceMappingBindingVariable]
              )
              if (
                liveValObj &&
                liveValObj.value &&
                liveValObj.value[0].reservable
              ) {
                object.set('fill', '#75c5d9')
              } else {
                object.set('fill', '#9c9c9b')
              }
            }
          }

          if (object.floorplan && object.floorplan.theme) {
            if (
              object.floorplan.theme &&
              object.floorplan.appliedTheme &&
              object.floorplan.theme !== object.floorplan.appliedTheme
            ) {
              object.floorplan.appliedTheme = object.floorplan.theme
              let shape = this.findShape(object.floorplan.uid)

              let appliedThemeSvg = applyColorTheme(
                object.floorplan.themeGroup,
                object.floorplan.theme,
                shape.template.svg
              )
              this.updateShape(object, appliedThemeSvg)
            }
          }

          if (object.floorplan.styles) {
            if (object.floorplan.type === 'button_group') {
              let txt = object
                .getObjects()
                .find(o => o.floorplan.objectType === 'button_text')
              let rct = object
                .getObjects()
                .find(o => o.floorplan.objectType === 'button_rect')

              if (txt) {
                txt.set('fill', object.floorplan.styles.fontColor)
              }
              if (rct) {
                let textWidth = txt.get('width')
                let textHeight = txt.get('height')

                rct.set('fill', object.floorplan.styles.backgroundColor)
                rct.set('rx', object.floorplan.styles.radius)
                rct.set('ry', object.floorplan.styles.radius)
                rct.set(
                  'width',
                  textWidth + object.floorplan.styles.padding * 2
                )
                rct.set(
                  'height',
                  textHeight + object.floorplan.styles.padding * 2
                )

                object.set(
                  'width',
                  textWidth + object.floorplan.styles.padding * 2
                )
              }

              object.setCoords()
            } else {
              for (let stl in object.floorplan.styles) {
                if (object.floorplan.styles[stl]) {
                  object.set(stl, object.floorplan.styles[stl])
                }
              }
            }
          }

          if (readonly) {
            if (object.floorplan.hide) {
              object.set('opacity', 0)
            } else if (object.floorplan.type !== 'space_zone') {
              object.set('opacity', 1)
            }

            if (
              object.floorplan &&
              (hoverCursor ||
                object.floorplan.type === 'button_group' ||
                (object.floorplan.actions &&
                  (object.floorplan.actions.showTrend.enable ||
                    object.floorplan.actions.controlAction.enable ||
                    object.floorplan.actions.hyperLink.enable)) ||
                object.floorplan.stateBindingVariable ||
                object.floorplan.enableButtonBinding ||
                object.floorplan.animateBindingVariable)
            ) {
              object.set({
                hoverCursor: 'pointer',
              })
            } else {
              object.set({
                hoverCursor: 'default',
              })
            }
          }
        }
      }),
      (this.updateAllLiveValues = function(objects, graphicsObj) {
        if (objects && objects.length) {
          for (let obj of objects) {
            this.updateLiveValue(obj, graphicsObj)
          }
        }
      })
  },
}
