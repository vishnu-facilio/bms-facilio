import { fabric } from 'fabric'
import { getPathMidPt } from '../utils/Math'
import { commonObjProps, colors, makePlusSign, getArrowCoords } from './Common'
import { isEmpty } from '@facilio/utils/validation'

export const getTransitionColors = type => {
  if (Number(type) === 3) {
    return {
      bg: colors.transition.condition,
      text: colors.transition.text,
      line: colors.line,
    }
  } else if ([2, 4].includes(Number(type))) {
    return {
      bg: colors.transition.time,
      text: colors.transition.text,
      line: colors.line,
    }
  } else {
    return {
      bg: colors.transition.bg,
      text: colors.transition.text,
      line: colors.line,
    }
  }
}

const getTransitionIcon = type => {
  let paths = []
  let color = colors.transition.text.default

  if (Number(type) === 3) {
    paths = [
      'M307.6 21.2l182.2 182.2a73.6 73.6 0 0 1 0 104.2L307.6 489.8a73.6 73.6 0 0 1-104.2 0L21.2 307.6a73.6 73.6 0 0 1 0-104.2L203.4 21.2a73.6 73.6 0 0 1 104.2 0zm-26 26a36.8 36.8 0 0 0-52.1 0L47.2 229.5a36.8 36.8 0 0 0 0 52l182.3 182.3a36.8 36.8 0 0 0 52 0l182.3-182.3a36.8 36.8 0 0 0 0-52L281.5 47.2z',
    ]
  } else if ([2, 4].includes(Number(type))) {
    paths = [
      'M256 511a255.5 255.5 0 0 0 255.4-255.4A255.5 255.5 0 0 0 256 .2 255.5 255.5 0 0 0 .6 255.6 255.5 255.5 0 0 0 256 510.9zm0-475a219.7 219.7 0 1 1 0 439.2A219.7 219.7 0 0 1 256 36z',
      'M244 285.5h85.2c10.2 0 18-7.9 18-18 0-10.2-7.8-18.1-18-18.1h-67v-105c0-10.1-8-18-18.1-18-10.2 0-18 7.9-18 18v123c0 10.2 7.8 18.1 18 18.1z',
    ]
  } else {
    return null
  }

  paths = paths.map(
    path =>
      new fabric.Path(path, {
        ...commonObjProps,
        fill: '',
        strokeWidth: 35,
        stroke: color,
        objectCaching: false,
      })
  )

  return new fabric.Group(paths, {
    ...commonObjProps,
    objType: 'icon',
    originX: 'right',
    originY: 'bottom',
  }).scaleToHeight(11)
}

export const arrowDirectionHash = [
  'bottom',
  'bottom',
  'bottom',
  'right',
  'top',
  'top',
  'top',
  'left',
]

export const makeConnector = (transition, coords, canvas, anchors) => {
  if (isEmpty(coords)) return

  let id = transition.id
  let {
    text: { default: textColor },
    bg: { default: bgColor },
    line: { default: lineColor },
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
    fill: colors.line.default,
    stroke: colors.line.default,
    objectCaching: false,
  })

  // Draw arrow on line end point
  let arrowCoords = getArrowCoords(
    coords[coords.length - 1],
    arrowDirectionHash[anchors[1]]
  )

  let arrowPath = arrowCoords.reduce((acc, c) => {
    acc += ` ${c.x} ${c.y}`
    return acc
  }, 'M')

  let arrow = new fabric.Path(`${arrowPath} Z`, {
    objType: 'arrow',
    fill: lineColor,
    stroke: lineColor,
    objectCaching: false,
    hoverCursor: 'pointer',
  })

  let elementArray = [line, arrow, circle]

  let pathMidPt = getPathMidPt(coords) || {
    x: 0,
    y: 0,
  }

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

    let { name, type } = transition
    let icon = getTransitionIcon(type)

    let allowedNameLength = icon ? 14 : 16

    let trimmedName =
      name.length > allowedNameLength
        ? `${name.slice(0, allowedNameLength - 4)}...`
        : name

    let textEl = new fabric.Text(trimmedName, {
      ...commonObjProps,
      objType: 'text',
      originX: 'center',
      originY: 'center',
      fontFamily: '"Aktiv-Grotesk", Helvetica, Arial, sans-serif',
      fill: textColor,
      fontSize: '13',
    })

    if (icon) {
      // http://fabricjs.com/test/misc/origin.html
      icon.set({ left: 0, top: -3 })
      textEl.set({ originX: 'left', originY: 'bottom', left: 5, top: 0 })
      let iconText = new fabric.Group([icon, textEl])
      iconText.set({
        originX: 'center',
        originY: 'center',
        left: pathMidPt.x,
        top: pathMidPt.y,
      })

      elementArray.push(iconText)
    } else {
      textEl.set({
        left: pathMidPt.x,
        top: pathMidPt.y,
      })
      elementArray.push(textEl)
    }
  } else {
    let plusSign = makePlusSign({ bgColor: lineColor })
    plusSign.left = pathMidPt.x
    plusSign.top = pathMidPt.y

    elementArray.push(plusSign)
  }

  let group = new fabric.Group(elementArray, {
    ...commonObjProps,
    id,
    objType: 'connector',
    pathPoints: coords,
    transition,
    lockMovementX: true,
    lockMovementY: true,
    hoverCursor: 'pointer',
  })

  group.on('mouseover', event => {
    event.target && event.target.onHoverStart()
    canvas.requestRenderAll()
  })

  group.on('mouseout', event => {
    event.target && event.target.onHoverEnd()
    canvas.requestRenderAll()
  })

  group.toggleActiveState = function(isActive) {
    let propName = isActive ? 'active' : 'default'
    let objects = this.getObjects()

    let {
      text: textColors,
      bg: bgColors,
      line: lineColors,
    } = getTransitionColors(this.transition.type || 1)

    objects.forEach(obj => {
      if (['line', 'dot', 'arrow'].includes(obj.objType))
        obj.set('stroke', lineColors[propName])

      obj.objType !== 'line' && obj.set('fill', lineColors[propName])
      obj.objType === 'text' && obj.set('fill', textColors[propName])
      obj.objType === 'rect' && obj.set('fill', bgColors[propName])
      obj.objType === 'rect' && obj.set('stroke', bgColors[propName])
      obj.objType === 'plus' &&
        (isActive ? obj.onHoverStart() : obj.onHoverEnd())
    })
  }.bind(group)

  group.onHoverStart = function() {
    this.toggleActiveState(true)
  }.bind(group)

  group.onHoverEnd = function() {
    if (!this.isActive) this.toggleActiveState(false)
  }.bind(group)

  group.makeActive = function() {
    this.isActive = true

    this.onHoverStart()
  }

  group.makeInActive = function() {
    this.isActive = false

    this.onHoverEnd()
  }

  return group
}
