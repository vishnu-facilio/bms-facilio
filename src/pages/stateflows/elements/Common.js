import { fabric } from 'fabric'

export const commonObjProps = {
  hasRotatingPoint: false,
  lockRotation: true,
  hasControls: false,
  hasBorders: false,
  lockScalingX: true,
  lockScalingY: true,
  perPixelTargetFind: true,
  excludeFromExport: true,
}

export const colors = {
  state: {
    bg: {
      default: '#fff',
      active: '#FF6660',
    },
    text: {
      default: '#3995e2',
      active: '#2d2d52',
    },
  },
  transition: {
    bg: {
      default: '#6071db',
      active: '#FF6660',
    },
    condition: {
      default: '#45bec4',
      active: '#FF6660',
    },
    time: {
      default: '#4eb871',
      active: '#FF6660',
    },
    text: {
      default: '#fff',
      active: '#fff',
    },
  },
  line: {
    default: '#3995e2',
    active: '#FF6660',
  },
}

export const makeBg = () => {
  return new Promise(resolve => {
    let img = new Image()
    img.src =
      'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAoAAAAKCAMAAAC67D+PAAAABGdBTUEAALGPC/xhBQAAACBjSFJNAAB6JgAAgIQAAPoAAACA6AAAdTAAAOpgAAA6mAAAF3CculE8AAAAVFBMVEXr6/8AAAD29/3r6//r6//r6//r6//r6//r6//u7v7y8v7r6//29/329/3r6//u7v73+P329/329/329/329/3y8v729/329/329/329/3r6/////+v4k9qAAAAG3RSTlMAAAAAAK9ucG+CqG4hZHCCHh89eB6oYmOgYnDbLnXkAAAAAWJLR0QbAmDUpAAAAAd0SU1FB+MJBQoVBFuF9hQAAABASURBVAjXY2BlY+fg5OJmZ2dnYGdgZOLhZWJkZERm8jHDmfwCPIJCwiI8PDwMomK84sISkry8vAxSLNi0YWUCAKQaAtqfMEELAAAAJXRFWHRkYXRlOmNyZWF0ZQAyMDE5LTA5LTA1VDEwOjIxOjA0KzAwOjAwruZKXgAAACV0RVh0ZGF0ZTptb2RpZnkAMjAxOS0wOS0wNVQxMDoyMTowNCswMDowMN+78uIAAAAASUVORK5CYII='

    img.onload = () =>
      resolve(
        new fabric.Pattern({
          source: img,
          offsetY: 4,
          offsetX: 1,
        })
      )
  })
}

export const makePlusSign = ({ bgColor, color } = {}) => {
  if (!bgColor) bgColor = colors.line.active
  if (!color) color = '#fff'

  let circle = new fabric.Circle({
    ...commonObjProps,
    objType: 'bg',
    originX: 'center',
    originY: 'center',
    radius: 10,
    fill: bgColor,
    stroke: bgColor,
    objectCaching: false,
  })
  let plus = new fabric.Path('M 0, -6 V 6 M -6, 0 H 6', {
    ...commonObjProps,
    objType: 'sign',
    originX: 'center',
    originY: 'center',
    stroke: color,
    strokeWidth: 3,
  })

  let group = new fabric.Group([circle, plus], {
    ...commonObjProps,
    objType: 'plus',
    originX: 'center',
    originY: 'center',
  })

  group.onHoverStart = function() {
    this.set('fill', colors.line.active)
    this.set('stroke', colors.line.active)
  }.bind(circle)

  group.onHoverEnd = function() {
    this.set('fill', bgColor)
    this.set('stroke', bgColor)
  }.bind(circle)

  return group
}

/**
 * @param  {x, y} pt
 * @param  {String} direction (bottom, left, right, top)
 * @param {Boolean} needsEdgeShift The origin point is usually shifted backwards along the primary axis
 * since we need the arrow to meet at the given point exactly. This can be skipped while dragging as then the
 * arrow will reflect the mouse cursor more accurately
 * @return {Array} Required points to plot an arrow head based on the direction it meets the point.
 */
export const getArrowCoords = (
  pt,
  direction = 'bottom',
  needsEdgeShift = true
) => {
  let priAxisOffset = 4
  let secAxisOffset = 3

  switch (direction) {
    case 'bottom':
      pt = needsEdgeShift
        ? { ...pt, y: pt.y - (priAxisOffset + secAxisOffset) }
        : pt

      return [
        pt,
        { x: pt.x + priAxisOffset, y: pt.y - secAxisOffset },
        { x: pt.x, y: pt.y + (priAxisOffset + secAxisOffset) },
        { x: pt.x - priAxisOffset, y: pt.y - secAxisOffset },
      ]
    case 'top':
      pt = needsEdgeShift
        ? { ...pt, y: pt.y + (priAxisOffset + secAxisOffset) }
        : pt

      return [
        pt,
        { x: pt.x + priAxisOffset, y: pt.y + secAxisOffset },
        { x: pt.x, y: pt.y - (priAxisOffset + secAxisOffset) },
        { x: pt.x - priAxisOffset, y: pt.y + secAxisOffset },
      ]
    case 'left':
      pt = needsEdgeShift
        ? { ...pt, x: pt.x - (priAxisOffset + secAxisOffset) }
        : pt

      return [
        pt,
        { x: pt.x - secAxisOffset, y: pt.y - priAxisOffset },
        { x: pt.x + (priAxisOffset + secAxisOffset), y: pt.y },
        { x: pt.x - secAxisOffset, y: pt.y + priAxisOffset },
      ]
    case 'right':
      pt = needsEdgeShift
        ? { ...pt, x: pt.x + (priAxisOffset + secAxisOffset) }
        : pt

      return [
        pt,
        { x: pt.x + secAxisOffset, y: pt.y - priAxisOffset },
        { x: pt.x - (priAxisOffset + secAxisOffset), y: pt.y },
        { x: pt.x + secAxisOffset, y: pt.y + priAxisOffset },
      ]
  }
}
