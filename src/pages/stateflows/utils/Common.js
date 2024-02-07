/**
 * @param  [] states
 * @param  [] transitions
 * @param  Id defaultStateId
 * @returns [] states that have transitions mapped to them
 */
export const getUsedStates = (states, transitions, defaultStateId) => {
  let usedStates = new Set()

  transitions.forEach(({ toStateId, fromStateId }) => {
    !usedStates.has(toStateId) && usedStates.add(toStateId)
    !usedStates.has(fromStateId) && usedStates.add(fromStateId)
  })

  return states
    .filter(state => {
      return usedStates.has(state.id) || state.id === defaultStateId
    })
    .map(state => {
      if (state.id === defaultStateId) state.isDefaultState = true
      return state
    })
}

/**
 * @param  [] states
 * @param  [] transitions
 * @param  Id defaultStateId
 * @returns [] states that have transitions mapped to them
 */
export const getUnusedStates = (states, transitions, defaultStateId) => {
  let usedStates = new Set()

  transitions.forEach(({ toStateId, fromStateId }) => {
    !usedStates.has(toStateId) && usedStates.add(toStateId)
    !usedStates.has(fromStateId) && usedStates.add(fromStateId)
  })

  return states.filter(
    state =>
      !usedStates.has(state.id) && String(state.id) !== String(defaultStateId)
  )
}

/**
 * @param  [] states
 * @param  [] transitions
 * @returns Graph - a dagre graph object that has nodes() and edges() with respective coordinates
 */
const createGraph = (states, transitions) => {
  let g = new dagre.graphlib.Graph()

  g.setGraph({ nodesep: 120, ranksep: 100, marginx: 100, marginy: 50 })
  g.setDefaultEdgeLabel(() => ({}))

  // Add states and transitions and makes edges between them
  states.forEach(({ id }) => g.setNode(id, { width: 140, height: 36 }))
  transitions.forEach(({ fromStateId, toStateId }) =>
    g.setEdge(fromStateId, toStateId)
  )
  // Calculate layout coords
  dagre.layout(g)

  return g
}

/**
 * @param  [] states
 * @param  [] transitions
 * @returns {} Map of stateId and their coordinates based on the dagre graph
 */
export const getLayout = (states, transitions) => {
  let graph = createGraph(states, transitions)

  let stateCoords = graph.nodes().reduce((res, id) => {
    res[id] = graph.node(id)
    return res
  }, {})

  return stateCoords
}

/**
 * @param  {canvas} Canvas
 * @param  Id id of the object to be found
 * @returns {obj} the fabric.js object with the given id
 */
export const getObjById = (canvas, id) => {
  let object = null
  let objects = canvas.getObjects()

  for (let i = 0, len = canvas.size(); i < len; i++) {
    if (objects[i].id && String(objects[i].id) === String(id)) {
      object = objects[i]
      break
    }
  }

  return object
}

/**
 * @param  {} obj
 * @returns {x, y} the centre of the object relative to the canvas
 */
export const getCenterCoords = obj => {
  let { x, y } = obj.getCenterPoint()
  return { x, y }
}

export const stubLength = 20
const portOffsetHash = [
  { x: 0, y: -stubLength },
  { x: 0, y: -stubLength },
  { x: 0, y: -stubLength },
  { x: stubLength, y: 0 },
  { x: 0, y: stubLength },
  { x: 0, y: stubLength },
  { x: 0, y: stubLength },
  { x: -stubLength, y: 0 },
]

/**
 * @param  {x, y} coord Coords of the anchor relative to canvas
 * @param  Number anchorId - index of the anchor in the portArray
 * @returns {x, y} of a pt at dist `stubLength` in a direction appropriate
 * for the anchor
 */
export const getStubCoords = (coord, anchorId) => {
  let portOffset =
    anchorId > -1 && anchorId < 8 ? portOffsetHash[anchorId] : { x: 0, y: 0 }

  return {
    x: Math.round(coord.x + portOffset.x),
    y: Math.round(coord.y + portOffset.y),
  }
}

/**
 * @param  {} point
 * @param  {} canvasObj
 * @returns {Boolean} Checks if mouse coords lie inside the given canvas object
 */
export const isObjIntesecting = (canvasObj, point) => {
  let isIntersecting = canvasObj.containsPoint(point, null, true, true)

  return isIntersecting
}

/**
 * @param  [{x,y}] path
 * @return [{x,y}] A smoothened path accounting for deformations due to scaling
 *
 * We know that the lines are orthogonal and so the difference between each point on the path will
 * always be on one axis - either x-axis value changes or the y-axis value changes
 *
 * Keeping this in mind, we pick the lowest differing axis value and swap it with the previous axis value
 * so that the difference is only along the other axis
 */
export const smoothenPath = path => {
  const reducer = (res, pt, index) => {
    if (index === 0 || index === path.length - 1) {
      res.push(pt)
    } else {
      let newPt = { ...pt }
      let prevPt = res[res.length - 1]

      let xAxisDiff = Math.abs(pt.x - prevPt.x)
      let yAxisDiff = Math.abs(pt.y - prevPt.y)

      if (xAxisDiff > yAxisDiff) {
        newPt = { x: pt.x, y: prevPt.y }
      } else if (xAxisDiff < yAxisDiff) {
        newPt = { x: prevPt.x, y: pt.y }
      }
      res.push(newPt)
    }
    return res
  }

  let newPath = path.reduce(reducer, [])

  if (newPath.length > 3) newPath = newPath.reduceRight(reducer, []).reverse()

  return newPath
}

/**
 * @param  {x,y} pt
 * @return {x,y} pt that is offsetted to adjust for mouse position so that the arrow
 * end is visible to the user dragging it
 */
export const getMouseAdjustedEndPoint = pt => {
  return {
    x: pt.x,
    y: pt.y,
  }
}

export const isValidAnchor = anchorId => anchorId >= 0 && anchorId < 8
