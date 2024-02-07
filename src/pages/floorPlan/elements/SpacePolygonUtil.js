import fabric from 'fabric'
const min = 99
const max = 999999

export const drawPolygon = (polygonOptions, canvas) => {
  canvas.selection = false
  canvas.forEachObject(function(object) {
    object.set('selectable', false)
  })

  polygonOptions.polygonMode = true
  polygonOptions.pointArray = new Array()
  polygonOptions.lineArray = new Array()
  polygonOptions.activeLine = null
}

export const addPoint = (polygonOptions, eventOptions, canvas) => {
  let random = Math.floor(Math.random() * (max - min + 1)) + min
  let id = new Date().getTime() + random
  let circle = new fabric.fabric.Circle({
    radius: 5,
    fill: '#ffffff',
    stroke: '#333333',
    strokeWidth: 0.5,
    left: eventOptions.e.layerX / canvas.getZoom(),
    top: eventOptions.e.layerY / canvas.getZoom(),
    selectable: false,
    hasBorders: false,
    hasControls: false,
    originX: 'center',
    originY: 'center',
    id: id,
    objectCaching: false,
  })
  if (polygonOptions.pointArray.length == 0) {
    circle.set({
      fill: 'red',
    })
  }
  let points = [
    eventOptions.e.layerX / canvas.getZoom(),
    eventOptions.e.layerY / canvas.getZoom(),
    eventOptions.e.layerX / canvas.getZoom(),
    eventOptions.e.layerY / canvas.getZoom(),
  ]
  let line = new fabric.fabric.Line(points, {
    strokeWidth: 2,
    fill: '#999999',
    stroke: '#999999',
    class: 'line',
    originX: 'center',
    originY: 'center',
    selectable: false,
    hasBorders: false,
    hasControls: false,
    evented: false,
    objectCaching: false,
  })
  let polygon
  if (polygonOptions.activeShape) {
    let pos = canvas.getPointer(eventOptions.e)
    points = polygonOptions.activeShape.get('points')
    points.push({
      x: pos.x,
      y: pos.y,
    })
    polygon = new fabric.fabric.Polygon(points, {
      stroke: '#333333',
      strokeWidth: 1,
      fill: '#cccccc',
      opacity: 0.3,
      selectable: false,
      hasBorders: false,
      hasControls: false,
      evented: false,
      objectCaching: false,
      perPixelTargetFind: true,
    })
    canvas.remove(polygonOptions.activeShape)
    canvas.add(polygon)
    polygonOptions.activeShape = polygon
    canvas.renderAll()
  } else {
    let polyPoint = [
      {
        x: eventOptions.e.layerX / canvas.getZoom(),
        y: eventOptions.e.layerY / canvas.getZoom(),
      },
    ]
    polygon = new fabric.fabric.Polygon(polyPoint, {
      stroke: '#333333',
      strokeWidth: 1,
      fill: '#cccccc',
      opacity: 0.3,
      selectable: false,
      hasBorders: false,
      hasControls: false,
      evented: false,
      objectCaching: false,
      perPixelTargetFind: true,
    })
    polygonOptions.activeShape = polygon
    canvas.add(polygon)
  }
  polygonOptions.activeLine = line

  polygonOptions.pointArray.push(circle)
  polygonOptions.lineArray.push(line)
  canvas.add(line)
  canvas.add(circle)
  canvas.selection = false
}
export const deletePolygonArray = (polygonOptions, canvas) => {
  for (let point of polygonOptions.pointArray) {
    canvas.remove(point)
  }
  for (let line of polygonOptions.lineArray) {
    canvas.remove(line)
  }
  canvas.remove(polygonOptions.activeShape).remove(polygonOptions.activeLine)
}

export const generatePolygon = (polygonOptions, canvas) => {
  let points = new Array()
  for (let point of polygonOptions.pointArray) {
    points.push({
      x: point.left,
      y: point.top,
    })
    canvas.remove(point)
  }
  for (let line of polygonOptions.lineArray) {
    canvas.remove(line)
  }
  canvas.remove(polygonOptions.activeShape).remove(polygonOptions.activeLine)
  let polygon = new fabric.fabric.Polygon(points, {
    stroke: '#333333',
    strokeWidth: 0.8,
    fill: 'red',
    opacity: 0.2,
    perPixelTargetFind: true,
    floorplan: {
      id: (
        Date.now().toString(36) +
        Math.random()
          .toString(36)
          .substr(2, 5)
      ).toLowerCase(),
      type: 'space_zone_group',
      spaceId: null,
      enableSpaceMapping: true,
      styles: {
        fill: '#ff0000',
      },
      actions: {},
    },
  })
  canvas.add(polygon)

  polygonOptions.activeLine = null
  polygonOptions.activeShape = null
  polygonOptions.polygonMode = false

  canvas.forEachObject(function(object) {
    object.set('selectable', true)
  })
  canvas.selection = true
}
