import { vDist, vAdd, vSub, vScale, vNorm } from 'vec-la-fp'

/**
 * @param  {x, y} pt1
 * @param  {x, y} pt2
 * To calculate the distance btw two points
 */
export const getDistBtwPoints = (pt1, pt2) => {
  let distance = vDist([pt1.x, pt1.y], [pt2.x, pt2.y])
  return distance
}

/**
 * @param  {x,y} pt1
 * @param  {x,y} pt2
 * @param  {Number} d
 *
 * Formula for point along a line between two points is at dist d from first point is
 * (p,q) = (𝑥0,𝑦0) + 𝑑𝐮 where u is 𝐮 = 𝐯 / ||𝐯||,
 *
 * v is vector along the two points 𝐯 =(𝑥1,𝑦1)−(𝑥0,𝑦0) and ||v|| is its magnitude
 * https://math.stackexchange.com/a/175906
 */
const getPointAtDistOnLine = (pt1, pt2, d) => {
  let v = vSub([pt2.x, pt2.y], [pt1.x, pt1.y])
  let [x, y] = vAdd([pt1.x, pt1.y], vScale(d, vNorm(v)))

  return { x, y }
}

/**
 * @param  {x, y} pt1
 * @param  {x, y} pt2
 * @param  {Number} pointCount
 * @return {Array} Required points [{x,y}, ...] on a line that are evenly spaced
 */
export const getPointsOnLine = (pt1, pt2, pointCount) => {
  let dist = vDist([pt1.x, pt1.y], [pt2.x, pt2.y])
  let partDist = dist / (pointCount + 1)
  let points = []

  for (let i = 1; i <= pointCount; i++) {
    let point = getPointAtDistOnLine(pt1, pt2, partDist * i)
    points.push(point)
  }
  return points
}

export const getLineMidPoint = (from, to) => {
  return getPointsOnLine(from, to, 1)
}

/**
 * @param  {Array} coords
 * @return  {Object} Required point on the polyline that is equidistant from both the src
 * and dest points
 */
export const getPathMidPt = coords => {
  let segmentDist = []
  let totalDist = 0

  for (let i = 0; i < coords.length - 1; i++) {
    let pt1 = coords[i]
    let pt2 = coords[i + 1]
    let dist = vDist([pt1.x, pt1.y], [pt2.x, pt2.y])

    segmentDist.push(dist)
    totalDist += dist
  }

  let midLength = totalDist / 2
  let midPoint

  for (let i = 0; i < segmentDist.length; i++) {
    let dist = segmentDist[i]

    if (midLength >= dist) {
      midLength -= dist
    } else {
      let pt1 = coords[i]
      let pt2 = coords[i + 1]

      midPoint = getPointAtDistOnLine(pt1, pt2, midLength)
      break
    }
  }

  return midPoint
}

/**
 * @param  {x, y} parent
 * @param  {x, y} child
 * @return {x, y} Absolute coords of a child shape relative to the canvas
 */
export const getAbsoluteCoords = (parent, child) => {
  return {
    x: parent.x + child.x,
    y: parent.y + child.y,
  }
}

/**
 * @param  {Array} path
 * @param  {x,y} mouseCoord
 * @return {Boolean} indicating if the mouse coordinate is closer to
 * the starting point (true) or ending point (false) of the path
 */
export const findClosestPathEnd = (path, mouseCoord) => {
  let src = path[0]
  let dest = path[path.length - 1]

  let distFromSrc = vDist([src.x, src.y], [mouseCoord.x, mouseCoord.y])
  let distToDest = vDist([mouseCoord.x, mouseCoord.y], [dest.x, dest.y])

  return distFromSrc < distToDest
}

export const scaleDown = (value, cellSize = 10) => {
  return Math.round(value / cellSize)
}

export const scaleUp = (value, cellSize = 10) => {
  return Math.round(value * cellSize)
}

export const snapToGrid = (value, cellSize = 10) => {
  return cellSize * Math.round(value / cellSize)
}

/**
 * @param  {x,y} Point
 * @param  {} gridUnit
 * @returns {x,y} a point snapped (aligned) to the grid
 */
export const snapPointToGrid = ({ x, y }, gridUnit) => ({
  x: snapToGrid(x, gridUnit),
  y: snapToGrid(y, gridUnit),
})

export const isEqual = (p1, p2) => {
  return p1.x === p2.x && p1.y === p2.y
}

export const manhattanDistance = (p1, p2) => {
  return Math.abs(p2.x - p1.x) + Math.abs(p2.y - p1.y)
}

export const difference = (p1, p2) => {
  return { x: p1.x - (p2.x || 0), y: p1.y - (p2.y || 0) }
}

/**
 * @param  {x, y} p1
 * @param  {x, y} p2
 * @returns The angle between the two points
 */
export const theta = (p1, p2) => {
  // Invert the y-axis
  let y = -(p2.y - p1.y)
  let x = p2.x - p1.x
  let rad = Math.atan2(y, x) // defined for all 0 corner cases

  // Correction for III and IV quadrants
  if (rad < 0) {
    rad = 2 * Math.PI + rad
  }

  return (180 * rad) / Math.PI
}

/**
 * @param  {Number} angle
 * @returns The equivalent positive angle if angle is negative or the equalent angle
 * if it exceeds 360`
 */
export const normalizeAngle = angle => {
  return (angle % 360) + (angle < 0 ? 360 : 0)
}

/**
 * @param  {x,y} point
 * @returns {x,y} Return a normalized version of the point
 * Used to determine the direction of a difference between two points
 */
export const normalizePoint = point => {
  return {
    x: point.x === 0 ? 0 : Math.abs(point.x) / point.x,
    y: point.y === 0 ? 0 : Math.abs(point.y) / point.y,
  }
}

/* Calculate the direction change between two angles
 */
export const getDirectionChange = (angle1, angle2) => {
  let directionChange = Math.abs(angle1 - angle2)
  return directionChange > 180 ? 360 - directionChange : directionChange
}

/**
 * @param  {x,y} point
 * @param  {x,y} or {Number} dx
 * @param  {Number} dy
 * @return {x, y} Offsets a point by a value (dx or dy) along its axes
 * Used for computing neighbor points on grid for current point
 */
export const offsetPoint = (point, dx, dy) => {
  if (Object(dx) === dx) {
    dy = dx.y
    dx = dx.x
  }

  return {
    x: point.x + (dx || 0),
    y: point.y + (dy || 0),
  }
}

export const toDeg = rad => {
  return ((180 * rad) / Math.PI) % 360
}

export const toRad = deg => {
  deg = deg % 360
  return (deg * Math.PI) / 180
}

export const getRectCenter = ({ origin, corner }) => {
  return {
    x: getLineMidPoint(origin.x, corner.x),
    y: getLineMidPoint(origin.y, corner.x),
  }
}

export const isPointInRect = ({ origin, corner }, p) => {
  let isXWithinBounds = origin.x <= p.x && p.x <= corner.x
  let isYWithinBounds = origin.y <= p.x && p.x <= corner.y

  return isXWithinBounds && isYWithinBounds
}

export const getDirectionBetweenPoints = (p1, p2) => {
  let xDir = null
  let yDir = null

  let deltaX = p2.x - p1.x
  let deltaY = p2.y - p1.y

  if (deltaX > 0) {
    xDir = 'right'
  } else if (deltaX < 0) {
    xDir = 'left'
  }

  if (deltaY > 0) {
    yDir = 'bottom'
  } else if (deltaY < 0) {
    yDir = 'top'
  }

  return { x: xDir, y: yDir }
}

export const getCombinedDirection = ({ x, y }) => {
  return y ? y + (x ? x : '') : x
}
