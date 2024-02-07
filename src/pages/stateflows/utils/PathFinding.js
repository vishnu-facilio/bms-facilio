import chunk from 'lodash/chunk'
import { isEmpty } from '@facilio/utils/validation'

import {
  snapPointToGrid as snapToGrid,
  scaleUp,
  scaleDown,
  isEqual,
  manhattanDistance,
  difference,
  theta,
  normalizeAngle,
  normalizePoint,
  getDirectionChange,
  offsetPoint,
} from './Math'
import { getStubCoords, smoothenPath } from './Common'

import SortedSet from './SortedSet'
import ObstacleMap from './ObstacleMap'

const config = {
  // size of the step to find a route (the grid of the pathfinder)
  step: 10,

  // the number of route finding loops that cause the router to abort
  // returns fallback route instead
  maximumLoops: 10000,

  // the number of decimal places to round floating point coordinates
  precision: 1,

  // maximum change of direction
  maxAllowedDirectionChange: 90,

  // should certain types of elements not be considered as obstacles?
  excludeTypes: ['connector'],

  // possible starting directions from an element
  startDirections: ['top', 'right', 'bottom', 'left'],

  // possible ending directions to an element
  endDirections: ['top', 'right', 'bottom', 'left'],

  // specify the directions used above and what they mean
  directionMap: {
    top: { x: 0, y: -1 },
    right: { x: 1, y: 0 },
    bottom: { x: 0, y: 1 },
    left: { x: -1, y: 0 },
  },

  // cost of an orthogonal step
  cost: function() {
    return this.step
  },

  // an array of directions to find next points on the route
  // different from start/end directions
  directions: function() {
    let step = this.step
    let cost = this.cost()

    return [
      { offsetX: step, offsetY: 0, cost: cost },
      { offsetX: 0, offsetY: step, cost: cost },
      { offsetX: -step, offsetY: 0, cost: cost },
      { offsetX: 0, offsetY: -step, cost: cost },
    ]
  },

  // a penalty received for direction change
  penalties: function() {
    return {
      0: 0,
      45: this.step / 2,
      90: this.step / 2,
    }
  },
}

/* Calculate the minimum cost from a point to a set of endpoints
 * using manhattan distance as the hueristic
 */
function estimateCost(from, endPoints) {
  let min = Infinity

  for (let i = 0, len = endPoints.length; i < len; i++) {
    let cost = manhattanDistance(from, endPoints[i])
    if (cost < min) min = cost
  }

  return min
}

function getKey(point) {
  return point.x + '@' + point.y
}

/* Helper function for getDirectionAngle()
 * corrects for grid deformation (if a point is one grid steps away from another in both dimensions,
 * it is considered to be 45 degrees away, even if the real angle is different)
 */
function fixAngleEnd(start, end, opt) {
  let step = opt.step

  let diffX = end.x - start.x
  let diffY = end.y - start.y

  let gridStepsX = diffX / opt.gridUnit
  let gridStepsY = diffY / opt.gridUnit

  let distanceX = gridStepsX * step
  let distanceY = gridStepsY * step

  return { x: start.x + distanceX, y: start.y + distanceY }
}

/* Returns a direction index from start point to end point
 * corrects for grid deformation between start and end
 */
function getDirectionAngle(start, end, numDirections, opt) {
  let quadrant = 360 / numDirections
  let angleTheta = theta(start, fixAngleEnd(start, end, opt))
  let normalizedAngle = normalizeAngle(angleTheta + quadrant / 2)

  return quadrant * Math.floor(normalizedAngle / quadrant)
}

function reconstructRoute(parents, points, tailPoint, from, to) {
  let route = []

  let prevDiff = normalizePoint(difference(to, tailPoint))

  // tailPoint is assumed to be aligned already
  let currentKey = getKey(tailPoint)
  let parent = parents[currentKey]

  let point
  while (parent) {
    point = points[currentKey]

    let diff = normalizePoint(difference(point, parent))

    if (!isEqual(diff, prevDiff)) {
      route.unshift(point)
      prevDiff = diff
    }

    currentKey = getKey(parent)
    parent = parents[currentKey]
  }

  // leadPoint is assumed to be aligned already
  let leadPoint = points[currentKey]

  let fromDiff = normalizePoint(difference(leadPoint, from))

  if (!isEqual(fromDiff, prevDiff)) {
    route.unshift(leadPoint)
  }

  console.log('Using A* Router')
  return route
}

function findRoute(from, to, isDragging = false) {
  let opt = this.options
  let map = this.map

  let { gridUnit, maxAllowedDirectionChange } = opt

  // The set of tentative points to be evaluated, initially containing the start points.
  // Rounded to nearest integer for simplicity.
  let openSet = new SortedSet()
  // Keeps reference to actual points for given elements of the open set.
  let points = {}
  // Keeps reference to a point that is immediate predecessor of given element.
  let parents = {}
  // Cost from start to a point along best known path.
  let costs = {}

  let start, end
  let endPoints

  // set of points we start pathfinding from
  start = snapToGrid(from, gridUnit)

  end = to
  endPoints = [end]

  let startPointKey = getKey(start)
  openSet.add(startPointKey, estimateCost(start, [end]))
  points[startPointKey] = start
  costs[startPointKey] = 0

  let previousRouteDirectionAngle = opt.previousDirectionAngle // undefined for first route
  let isPathBeginning = previousRouteDirectionAngle === undefined

  // directions
  let directions = opt.directions
  let numDirections = directions.length
  let direction
  let directionChange

  let loopsRemaining = isDragging ? opt.maximumLoops / 2 : opt.maximumLoops

  // We can assume path to be found if the path reaches any neighbor point of the actual
  // end point. We find all neighbors of end point along all directions and add it to list of
  // points
  for (let i = 0; i < numDirections; i++) {
    let direction = directions[i]
    let offsetedEndPoint = offsetPoint(
      snapToGrid(end, gridUnit),
      direction.offsetX,
      direction.offsetY
    )
    if (map.isPointAccessible(offsetedEndPoint)) {
      endPoints.push(offsetedEndPoint)
    }
  }
  let endPointsKeys = endPoints.map(getKey)

  // main route finding loop
  while (!openSet.isEmpty() && loopsRemaining > 0) {
    // remove current from the open list
    let currentKey = openSet.pop()

    let currentPoint = points[currentKey]
    let currentParent = parents[currentKey]
    let currentCost = costs[currentKey]

    let isRouteBeginning = currentParent === undefined // undefined for route starts
    let isStart = isEqual(currentPoint, start) // (is source anchor or `from` point) = can leave in any direction

    let previousDirectionAngle

    if (!isRouteBeginning) {
      previousDirectionAngle = getDirectionAngle(
        currentParent,
        currentPoint,
        numDirections,
        opt
      )
    } else if (!isPathBeginning) {
      // a vertex on the route
      previousDirectionAngle = previousRouteDirectionAngle
    } else if (!isStart) {
      // beginning of route on the path
      previousDirectionAngle = getDirectionAngle(
        start,
        currentPoint,
        numDirections,
        opt
      )
    } else {
      // beginning of path, source anchor or `from` point
      previousDirectionAngle = null
    }

    // check if we reached any endpoint
    let samePoint = isEqual(start, end)
    let skipEndCheck = isRouteBeginning && samePoint

    if (!skipEndCheck && endPointsKeys.includes(currentKey)) {
      opt.previousDirectionAngle = previousDirectionAngle
      return reconstructRoute(parents, points, currentPoint, start, end)
    }

    // go over all possible directions and find neighbors
    for (let i = 0; i < numDirections; i++) {
      direction = directions[i]

      let directionAngle = direction.angle

      directionChange = getDirectionChange(
        previousDirectionAngle,
        directionAngle
      )

      // if the direction changed rapidly, don't use this point
      // any direction is allowed for starting points
      let isRouteStart = !(isPathBeginning && isStart)

      let isDirectionChangeAllowed =
        directionChange <= maxAllowedDirectionChange

      if (isRouteStart && !isDirectionChangeAllowed) {
        continue
      }

      let directionOffsetedPt = offsetPoint(
        currentPoint,
        direction.offsetX,
        direction.offsetY
      )

      let neighborPoint = snapToGrid(directionOffsetedPt, gridUnit)

      let neighborKey = getKey(neighborPoint)

      // Closed points from the openSet were already evaluated.
      let canSkipPoint =
        !endPointsKeys.includes(neighborKey) &&
        (openSet.isClose(neighborKey) || !map.isPointAccessible(neighborPoint))

      if (canSkipPoint) {
        continue
      }

      if (endPointsKeys.includes(neighborKey)) {
        // If neighbor is an end point it can be entered in any direction
        // If not we have to check if direction change is safe
        let isNeighborEnd = isEqual(neighborPoint, end)

        if (!isNeighborEnd) {
          let endDirectionAngle = getDirectionAngle(
            neighborPoint,
            end,
            numDirections,
            opt
          )
          let endDirectionChange = getDirectionChange(
            directionAngle,
            endDirectionAngle
          )

          if (endDirectionChange > maxAllowedDirectionChange) continue
        }
      }

      let neighborCost = direction.cost
      let neighborPenalty = isStart ? 0 : opt.penalties[directionChange] // no penalties for start point
      let costFromStart = currentCost + neighborCost + neighborPenalty

      if (!openSet.isOpen(neighborKey) || costFromStart < costs[neighborKey]) {
        // If neighbor point has not been processed yet or the cost of the path
        // from start is lower than previously calculated neighbor needs to be explored
        points[neighborKey] = neighborPoint
        parents[neighborKey] = currentPoint
        costs[neighborKey] = costFromStart
        openSet.add(
          neighborKey,
          costFromStart + estimateCost(neighborPoint, endPoints)
        )
      }
    }

    loopsRemaining--
  }
}

function findBackupRoute(from, to, opt) {
  let gridUnit = opt.gridUnit

  let path = []
  let su = value => scaleUp(value, gridUnit)
  let sd = value => scaleDown(value, gridUnit)

  let sFrom = { x: sd(from.x), y: sd(from.y) }
  let sTo = { x: sd(to.x), y: sd(to.y) }

  this.l1PathFinder.search(sFrom.x, sFrom.y, sTo.x, sTo.y, path)
  console.log('Using L1 Router')
  path = chunk(path, 2)
    .map(([x, y]) => ({ x: su(x), y: su(y) })) // Split into (x,y) and scale up to actual coords
    .map((point, index) => {
      // Swap starting and ending points from path with stub coords
      if (index === 0) return from
      else if (index === path.length - 1) return to
      else return point
    })

  return path
}

function resolveOptions(opt) {
  opt.directions = opt.directions()
  opt.penalties = opt.penalties()

  opt.directions.forEach(function(direction) {
    let point1 = { x: 0, y: 0 }
    let point2 = { x: direction.offsetX, y: direction.offsetY }

    direction.angle = normalizeAngle(theta(point1, point2))
  })

  return opt
}

function Router(canvas, opt, l1PathFinder) {
  this.options = resolveOptions(Object.assign({}, config, opt))

  this.map = new ObstacleMap(opt).build(canvas)
  this.l1PathFinder = l1PathFinder

  this.findRoute = findRoute.bind(this)
  this.findBackupRoute = findBackupRoute.bind(this)

  this.search = function(
    from,
    to,
    { isDragging = false, draggedObject = null }
  ) {
    if (isDragging && !isEmpty(draggedObject)) {
      this.map.movingObject = draggedObject
    }

    let { fromPt, fromAnchor } = from
    let { toPt, toAnchor } = to

    let sourceAnchor = getStubCoords(fromPt, fromAnchor)
    let targetAnchor = getStubCoords(toPt, toAnchor)

    let path = this.findRoute(sourceAnchor, targetAnchor, isDragging)

    this.map.movingObject = null

    // no route found (targetAnchor wasn't accessible or finding a route took too much calculation)
    if (isEmpty(path))
      path = this.findBackupRoute(sourceAnchor, targetAnchor, opt)

    return smoothenPath([fromPt, ...path, toPt])
  }.bind(this)
}

export default Router
