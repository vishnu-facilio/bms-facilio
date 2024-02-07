import { fabric } from 'fabric'
import { vDist } from 'vec-la-fp'
import { isEmpty } from '@facilio/utils/validation'
import { getPointsOnLine, snapToGrid, getAbsoluteCoords } from '../utils/Math'
import { getCenterCoords } from '../utils/Common'

import { commonObjProps, colors } from './Common'

const trimmedName = name => {
  return name.length > 18 ? name.slice(0, 15) + '...' : name
}

export const makeState = (
  { id, displayName, isDefaultState = false },
  canvas,
  coords = null,
  anchorList = [],
  cellSize = 10
) => {
  let textColor = colors.state.text.default
  let bgColor = colors.state.bg.default
  let rectWidth = 135
  let rectHeight = 30

  let anchorColor = colors.line.default
  let strokeColor = isDefaultState ? '#60a9a6' : textColor

  let rect = new fabric.Rect({
    ...commonObjProps,
    objType: 'rect',
    originX: 'center',
    originY: 'center',
    top: 15,
    left: 67,
    width: rectWidth,
    height: rectHeight,
    rx: 3,
    ry: 3,
    strokeWidth: 1,
    fill: bgColor,
    hoverCursor: 'move',
    stroke: strokeColor,
  })

  let textEl = new fabric.Text(trimmedName(displayName), {
    ...commonObjProps,
    objType: 'textEl',
    originX: 'center',
    originY: 'center',
    top: 15,
    left: 67,
    fontFamily: '"Aktiv-Grotesk", Helvetica, Arial, sans-serif',
    fill: textColor,
    fontSize: '13',
  })

  let connectorPoints = [
    ...getPointsOnLine({ x: 0, y: 0 }, { x: rectWidth, y: 0 }, 3),
    ...getPointsOnLine(
      { x: rectWidth, y: 0 },
      { x: rectWidth, y: rectHeight },
      1
    ),
    ...getPointsOnLine(
      { x: rectWidth, y: rectHeight },
      { x: 0, y: rectHeight },
      3
    ),
    ...getPointsOnLine({ x: 0, y: 0 }, { x: 0, y: rectHeight }, 1),
  ]

  let anchors = []
  connectorPoints.forEach(({ x, y }, index) => {
    let circle = new fabric.Circle({
      ...commonObjProps,
      objType: 'anchor',
      originX: 'center',
      originY: 'center',
      top: y,
      left: x,
      radius: 3,
      fill: anchorColor,
      stroke: anchorColor,
      objectCaching: false,
      opacity: 0,
      anchorId: index,
      transitionId: anchorList[index] || null,
      hoverCursor: 'pointer',
    })

    anchors.push(circle)
  })

  let group = new fabric.Group([rect, textEl, ...anchors], {
    ...commonObjProps,
    id,
    objType: 'state',
    originX: 'center',
    originY: 'center',
    left: coords ? coords.x : 100,
    top: coords ? coords.y : 100,
    anchors: anchors,
    excludeFromExport: false,
    subTargetCheck: true,
    cellSize: cellSize,
  })

  group.showAnchors = function() {
    let objects = this.getObjects() || []

    let rect = objects.find(obj => obj.objType === 'rect')
    rect && rect.set('hoverCursor', 'default')

    objects
      .filter(obj => obj.objType === 'anchor')
      .forEach(obj => {
        obj.animate('opacity', 1, {
          onChange: (() => {
            canvas.requestRenderAll()
          }).bind(canvas),
          duration: 200,
          easing: fabric.util.ease.easeOutSine,
        })
      })
  }.bind(group)

  group.hideAnchors = function() {
    let objects = this.getObjects() || []

    let rect = objects.find(obj => obj.objType === 'rect')
    rect && rect.set('hoverCursor', 'move')

    objects
      .filter(obj => obj.objType === 'anchor')
      .forEach(obj => {
        obj.animate('opacity', 0, {
          onChange: (() => {
            canvas.requestRenderAll()
          }).bind(canvas),
          duration: 200,
          easing: fabric.util.ease.easeOutSine,
        })
      })
  }.bind(group)

  group.setAnchor = function(anchorId, transitionId) {
    let anchor = this.getObjects()
      .filter(({ objType }) => objType === 'anchor')
      .find(a => String(a.anchorId) === String(anchorId))

    anchor.transitionId = transitionId
  }.bind(group)

  group.focusClosestAnchor = function(mouse) {
    let closestAnchorDist = Infinity
    let closestFreeAnchor = null

    let getCoords = child =>
      getAbsoluteCoords(getCenterCoords(group), getCenterCoords(child))
    let render = () => canvas.requestRenderAll()

    let anchors = this.getObjects().filter(
      ({ objType }) => objType === 'anchor'
    )

    anchors.forEach(anchor => {
      let coord = getCoords(anchor)
      let dist = vDist([mouse.x, mouse.y], [coord.x, coord.y])

      anchor.set('fill', anchorColor)
      anchor.set('stroke', anchorColor)

      if (dist < closestAnchorDist && isEmpty(anchor.transitionId)) {
        closestAnchorDist = dist
        closestFreeAnchor = anchor
      }
    })

    if (isEmpty(closestFreeAnchor)) return

    closestFreeAnchor.set('fill', colors.line.active)
    closestFreeAnchor.set('stroke', colors.line.active)
    render()

    return closestFreeAnchor.anchorId
  }.bind(group)

  group.resetAnchors = function() {
    let render = () => canvas.requestRenderAll()
    let anchors = this.getObjects().filter(
      ({ objType }) => objType === 'anchor'
    )

    anchors.forEach(anchor => {
      anchor.set('fill', anchorColor)
      anchor.set('stroke', anchorColor)
      render()
    })
  }.bind(group)

  group.onHoverStart = function() {
    let rect = this.getObjects().find(({ objType }) => objType === 'rect')
    rect && rect.set('stroke', colors.line.active)

    let text = this.getObjects().find(({ objType }) => objType === 'textEl')
    text && text.set('fill', colors.line.active)
  }.bind(group)

  group.onHoverEnd = function() {
    let rect = this.getObjects().find(({ objType }) => objType === 'rect')
    let text = this.getObjects().find(({ objType }) => objType === 'textEl')

    if (!this.isActive) {
      rect && rect.set('stroke', strokeColor)
      text && text.set('fill', textColor)
    }
  }.bind(group)

  group.makeActive = function() {
    this.isActive = true
    this.onHoverStart()
  }

  group.makeInActive = function() {
    this.isActive = false
    this.onHoverEnd()
  }

  group.snapToGrid = function() {
    this.left = snapToGrid(this.left, this.cellSize)
    this.top = snapToGrid(this.top, this.cellSize)
    this.setCoords()
  }.bind(group)

  group.updateProps = function(state) {
    let textEl = this.getObjects().find(({ objType }) => objType === 'textEl')
    textEl.set('text', trimmedName(state.displayName))
  }.bind(group)

  group.setPrevCoords = function() {
    this.prevCoords = { left: this.left, top: this.top }
  }

  group.hasPositionChanged = function() {
    let {
      prevCoords: { left, top },
    } = this
    return this.left !== left || this.top !== top
  }

  group.reInit = function(state, coords) {
    if (!isEmpty(coords)) this.set({ left: coords.x, top: coords.y })
    this.updateProps(state)
    this.snapToGrid()
  }

  group.toObject = (function(toObject) {
    return function() {
      return fabric.util.object.extend(toObject.call(this), {
        id: this.id,
        objType: this.objType,
        anchors: this.anchors.map(anchor => {
          if (String(anchor.transitionId).includes('temp')) return null
          return anchor.transitionId
        }),
      })
    }
  })(group.toObject)

  return group
}
