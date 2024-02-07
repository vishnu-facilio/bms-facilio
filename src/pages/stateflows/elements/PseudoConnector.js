import { fabric } from 'fabric'
import { getPathMidPt } from '../utils/Math'
import { commonObjProps, colors, makePlusSign, getArrowCoords } from './Common'
import { getTransitionColors } from './Transition.js'
import { isEmpty } from '@facilio/utils/validation'
import { vDist } from 'vec-la-fp'

const getAdjustedEndPt = coords => {
  let direction = 'bottom'

  let end = coords[coords.length - 1]
  end = {
    x: Math.round(end.x),
    y: Math.round(end.y),
  }

  let startIndex = 2
  let start = coords[coords.length - startIndex]
  start = {
    x: Math.round(start.x),
    y: Math.round(start.y),
  }

  // Pick two points on the line that are atleast 20px away from each other to get the direction
  // the arrow is moving in. Should prob switch to angle based computation like in pathfinding.js
  while (
    vDist([start.x, start.y], [end.x, end.y]) < 20 &&
    coords.length - 1 > startIndex
  ) {
    end = start
    startIndex += 1
    start = coords[coords.length - startIndex]
    start = {
      x: Math.round(start.x),
      y: Math.round(start.y),
    }
  }

  if (start.y < end.y) {
    direction = 'bottom'
  } else if (start.y > end.y) {
    direction = 'top'
  } else if (start.x < end.x) {
    direction = 'left'
  } else {
    direction = 'right'
  }

  let endPt = coords[coords.length - 1]

  return {
    endPt: { x: Math.round(endPt.x), y: Math.round(endPt.y) },
    direction,
  }
}

export const makePseudoConnector = (id, transition, coords) => {
  if (isEmpty(coords)) return

  let elementArray = []

  let {
    text: { active: textColor },
    bg: { active: bgColor },
    line: { active: lineColor },
  } = getTransitionColors(transition.type || 1)

  // Draw Line along the points
  let linePath = coords.reduce((acc, c) => {
    acc += ` ${c.x} ${c.y}`
    return acc
  }, 'M')

  let line = new fabric.Path(linePath, {
    ...commonObjProps,
    objType: 'line',
    fill: '',
    stroke: lineColor,
    objectCaching: false,
  })

  elementArray.push(line)

  // Draw arrow on line end point
  let { endPt, direction } = getAdjustedEndPt(coords)

  let arrowPath = getArrowCoords(endPt, direction, false).reduce((acc, c) => {
    acc += ` ${c.x} ${c.y}`
    return acc
  }, 'M')

  let arrow = new fabric.Path(`${arrowPath} Z`, {
    objType: 'arrow',
    fill: lineColor,
    stroke: lineColor,
    objectCaching: false,
  })
  elementArray.push(arrow)

  //  Draw circle on line start point
  let centre = coords[0]
  let circle = new fabric.Circle({
    ...commonObjProps,
    objType: 'dot',
    originX: 'center',
    originY: 'center',
    top: centre.y,
    left: centre.x,
    radius: 2.5,
    fill: colors.line.active,
    stroke: colors.line.active,
    objectCaching: false,
  })
  elementArray.push(circle)

  // Draw transition box
  let pathMidPt = getPathMidPt(coords) || { x: 0, y: 0 }

  if (!transition.isPartial) {
    let rect = new fabric.Rect({
      ...commonObjProps,
      objType: 'rect',
      left: pathMidPt.x,
      top: pathMidPt.y,
      originX: 'center',
      originY: 'center',
      width: 110,
      height: 26,
      rx: 12.5,
      ry: 12.5,
      stroke: bgColor,
      strokeWidth: 1,
      fill: bgColor,
    })
    elementArray.push(rect)

    let textEl = new fabric.Text(transition.name, {
      ...commonObjProps,
      objType: 'text',
      left: pathMidPt.x,
      top: pathMidPt.y,
      originX: 'center',
      originY: 'center',
      fontFamily: '"Aktiv-Grotesk", Helvetica, Arial, sans-serif',
      fill: textColor,
      fontSize: '13',
    })
    elementArray.push(textEl)
  } else {
    let plusSign = makePlusSign()
    plusSign.left = pathMidPt.x
    plusSign.top = pathMidPt.y

    elementArray.push(plusSign)
  }

  let group = new fabric.Group(elementArray, {
    ...commonObjProps,
    id,
    objType: 'pesudo-connector',
    lockMovementX: true,
    lockMovementY: true,
    hoverCursor: 'pointer',
    opacity: 0.6,
  })

  return group
}
