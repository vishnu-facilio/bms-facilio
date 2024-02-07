import { isEmpty } from '@facilio/utils/validation'
import { isObjIntesecting } from './Common'
const { floor, ceil } = Math

/* Map of obstacles
 * Helper structure to identify whether a point lies inside an obstacle.
 *
 * Points are first scaled down by a factor of `gridUnit` and then added to
 * the hashmap during build()
 */
function ObstacleMap(opt) {
  this.map = {}
  this.options = opt
  this.options.gridUnit = opt.gridUnit || 10

  // Id of object that the user is moving around. The points of this object are not treated as obstacles
  this.movingObject = null
}

/* Builds a map of all elements for quicker obstacle queries.
 *
 * The canvas is divided into smaller cells, where each holds information about which
 * elements are on it. When we query whether a point lies inside an obstacle we
 * don't need to go through all obstacles, we check the cell directly.
 */
ObstacleMap.prototype.build = function(canvas) {
  let opt = this.options
  let gridUnit = opt.gridUnit
  let padding = opt.padding || 10

  let validObjects = canvas
    .getObjects()
    .filter(({ objType }) => !opt.excludeTypes.includes(objType))

  validObjects.reduce(function(map, obj) {
    let { tl, br } = obj.aCoords

    // Round points to lower whole number
    let origin = { x: floor(tl.x), y: floor(tl.y) }
    let corner = { x: floor(br.x), y: floor(br.y) }

    // Snap to grid but towards the lower grid point
    origin = {
      x: floor(origin.x / gridUnit) * gridUnit,
      y: floor(origin.y / gridUnit) * gridUnit,
    }
    corner = {
      x: ceil(corner.x / gridUnit) * gridUnit,
      y: ceil(corner.y / gridUnit) * gridUnit,
    }

    // Add a padding to ensure lines arent close to obj
    let [x1, x2] = [origin.x - padding, corner.x + padding]
    let [y1, y2] = [origin.y - padding, corner.y + padding]

    for (let x = x1; x <= x2; x += gridUnit) {
      for (let y = y1; y <= y2; y += gridUnit) {
        let gridKey = x + '@' + y
        map[gridKey] = map[gridKey] || []
        map[gridKey].push(obj)
      }
    }
    return map
  }, this.map)

  return this
}

ObstacleMap.prototype.isPointAccessible = function({ x, y }) {
  let { movingObject } = this
  let gridKey = x + '@' + y
  let canvasObj = this.map[gridKey]

  let isPointMappedToMovingObject =
    !isEmpty(canvasObj) &&
    !isEmpty(movingObject) &&
    String(movingObject.id) === String(canvasObj.id)

  let isPointInMovingObject =
    !isEmpty(movingObject) && isObjIntesecting(movingObject, { x, y })

  /* If an obj is being moved, treat it's points as empty/accessible and instead include a check
   * to find if point is inside moving object
   */
  if (isPointInMovingObject) {
    return false
  } else if (isPointMappedToMovingObject) {
    return true
  } else {
    return isEmpty(canvasObj)
  }
}

export default ObstacleMap
