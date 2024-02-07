import { fabric } from 'fabric'
import { isEmpty, isNumber, isFloat } from '@facilio/utils/validation'
import getProperty from 'dlv'
import { formatCurrency, formatInterger } from 'charts/helpers/formatter'

import markerJson from 'pages/floorPlan/elements/markers.json'
export const getCoordinatesCenter = coordinates => {
  if (coordinates) {
    let points1 = coordinates[0]
    let points2 = coordinates[2]
    return {
      x: (points1.x + points2.x) / 2,
      y: (points1.y + points2.y) / 2,
    }
  }
  return {
    x: 0,
    y: 0,
  }
}
export const getcoordinates = (element, position) => {
  if (!isEmpty(element)) {
    if (position === 'top-left') {
      return {
        x: element.left + 30,
        y: element.top + 30,
      }
    } else if (position === 'top-right') {
      return {
        x: element.left + element.width,
        y: element.top + 30,
      }
    } else if (position === 'bottom-left') {
      return {
        x: element.left + 30,
        y: element.top + (element.height - 15),
      }
    } else if (position === 'bottom-right') {
      return {
        x: element.left + element.width,
        y: element.top + (element.height - 15),
      }
    } else {
      return element._findCenterFromElement()
    }
  }
  return null
}
export const isVariable = obj => {
  // it will check the varibale type
  if (isEmpty(obj)) {
    return false
  } else if (obj.objectType && obj.objectType === 'variable') {
    return true
  } else {
    return false
  }
}
export const isVariableElement = element => {
  // it will check the varibale type
  if (isEmpty(element)) {
    return false
  } else if (isObjectType(element, 'variable')) {
    return true
  } else {
    return false
  }
}
export const isElement = (element, type) => {
  if (isEmpty(element)) {
    return false
  } else {
    let { floorplan } = element
    if (isEmpty(floorplan)) {
      return false
    }
    if (floorplan.type && floorplan.type === type) {
      return true
    }
    return false
  }
}
export const isSpaceId = (element, spaceId) => {
  if (isEmpty(element)) {
    return false
  } else {
    let { floorplan } = element
    if (isEmpty(floorplan)) {
      return false
    }
    if (floorplan.spaceId && floorplan.spaceId === spaceId) {
      return true
    }
    return false
  }
}
export const isObjectType = (element, objectType) => {
  if (isEmpty(element)) {
    return false
  } else {
    let { floorplan } = element
    if (isEmpty(floorplan)) {
      return false
    } else if (floorplan.objectType && floorplan.objectType === objectType) {
      return true
    }
    return false
  }
}
export const isNotMappedElement = (element, type) => {
  let { floorplan } = element
  if (
    !isEmpty(floorplan) &&
    getProperty(floorplan, 'type', type) &&
    floorplan.spaceId === null
  ) {
    return true
  }
  return false
}
export const isMappedElement = (element, type) => {
  let { floorplan } = element
  if (type) {
    if (
      !isEmpty(floorplan) &&
      getProperty(floorplan, 'type', type) &&
      floorplan.spaceId
    ) {
      return true
    }
  } else {
    if (
      !isEmpty(floorplan) &&
      floorplan.type === 'space_zone_group' &&
      floorplan.spaceId
    ) {
      return true
    }
  }

  return false
}
export const findShape = uid => {
  for (let group of markerJson.shapes) {
    let shape = group.children.find(s => s.uid === uid)
    if (shape) {
      return shape
    }
  }
  return null
}
export const findShapeByName = _name => {
  for (let group of markerJson.shapes) {
    let shape = group.children.find(s => s._name === _name)
    if (shape) {
      return shape
    }
  }
  return null
}
export const isMarkerElement = element => {
  if (isElement(element, 'marker')) {
    return true
  }
  return false
}
export const isSpaceZoneElement = element => {
  if (isElement(element, 'space_zone_group')) {
    return true
  }
  return false
}
export const isNotMappedMarkerElement = element => {
  if (isElement(element, 'marker') && element.floorplan.spaceId === null) {
    return true
  }
  return false
}
export const isMappedMarkerElement = element => {
  if (isElement(element, 'marker') && element.floorplan.spaceId !== null) {
    return true
  }
  return false
}
export const getUniqueId = () => {
  return (
    Date.now().toString(36) +
    Math.random()
      .toString(36)
      .substr(2, 5)
  ).toLowerCase()
}
export const getColor = () => {
  return (
    '#' +
    Math.random()
      .toString(16)
      .substr(2, 6)
  )
}
export const commonObjProps = {
  hasRotatingPoint: false,
  lockRotation: true,
  hasControls: false,
  hasBorders: false,
  lockScalingX: true,
  lockScalingY: true,
  perPixelTargetFind: true,
}

export const tempProps = {
  // hasRotatingPoint: false,
  // lockRotation: true,
  // hasControls: false,
  // hasBorders: false,
  // lockScalingX: true,
  // lockScalingY: true,
}
export const viewerProps = {
  hasRotatingPoint: false,
  lockRotation: true,
  hasControls: false,
  hasBorders: false,
  lockScalingX: true,
  lockScalingY: true,
  selectable: false,
  hoverCursor: 'default',
}
export const EditorProps = {
  hasRotatingPoint: false,
  lockRotation: true,
  hasControls: false,
  hasBorders: false,
  lockScalingX: true,
  lockScalingY: true,
  selectable: false,
  hoverCursor: 'default',
}
export const UnlockProps = {
  hasRotatingPoint: true,
  lockRotation: false,
  hasControls: true,
  hasBorders: false,
  lockScalingX: false,
  lockScalingY: false,
  selectable: true,
  hoverCursor: 'default',
}
export const getElement = element => {
  if (isEmpty(element)) {
    return false
  } else if (element.floorplan) {
    return element.floorplan
  } else {
    return false
  }
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

export const formatDecimal = (value, decimalPoints = 2) => {
  if (!isEmpty(value) && isNumber(value) && isFloat(value)) {
    if (decimalPoints === 0) {
      return formatInterger(value)
    }
    return formatCurrency(Number(value))
  }
  return typeof value === 'boolean' ? String(value) : value
}
