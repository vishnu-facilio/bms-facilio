import { fabric } from 'fabric'
import { commonObjProps, colors } from './Common'
import { getArrowCoords } from 'pages/stateflows/elements/Common'
import { getLineMidPoint } from 'pages/stateflows/utils/Math'

const trimmedName = name => {
  return name.length > 18 ? name.slice(0, 15) + '...' : name
}

const actionSvg = require(`!html-loader!svgo-loader?externalConfig=svgo.config.yml!./../../../assets/svgs/workflowbuilder/action.svg`)
const eventSvg = require(`!html-loader!svgo-loader?externalConfig=svgo.config.yml!./../../../assets/svgs/workflowbuilder/event.svg`)
const conditionSvg = require(`!html-loader!svgo-loader?externalConfig=svgo.config.yml!./../../../assets/svgs/workflowbuilder/condition.svg`)

const blockHash = {
  event: {
    svg: eventSvg,
  },
  action: {
    svg: actionSvg,
  },
  condition: {
    svg: conditionSvg,
  },
}

export const makeWorkFlowBlock = (selectedBlock, canvas, coords) => {
  let { label, type } = selectedBlock
  let textColor = colors.workflowBlock.text.default
  let bgColor = colors.workflowBlock.bg.default
  let strokeColor = colors.workflowBlock.stroke.default
  let rectWidth = 170
  let rectHeight = 70
  let selectedBlockSvg = blockHash[type].svg

  let rect = new fabric.Rect({
    ...commonObjProps,
    objType: 'rect',
    originX: 'center',
    originY: 'center',
    width: rectWidth,
    height: rectHeight,
    rx: 3,
    ry: 3,
    strokeWidth: 1,
    fill: bgColor,
    hoverCursor: 'move',
    stroke: strokeColor,
  })

  let blockSvg
  fabric.loadSVGFromString(selectedBlockSvg, function(objects, options) {
    let groupedObject = fabric.util.groupSVGElements(objects, options)
    groupedObject.set({
      ...commonObjProps,
      originX: 'center',
      originY: 'center',
      left: -85,
    })
    blockSvg = groupedObject
  })

  let textDesEl = new fabric.Text(trimmedName(label), {
    ...commonObjProps,
    objType: 'textEl',
    originX: 'center',
    left: -30,
    top: -10,
    fontFamily: '"Aktiv-Grotesk", Helvetica, Arial, sans-serif',
    fill: textColor,
    fontSize: '14',
  })

  let group = new fabric.Group([rect, blockSvg, textDesEl], {
    ...commonObjProps,
    workFlowType: type,
    connectorsArr: [],
    objType: 'workFlowBlock',
    originX: 'center',
    originY: 'center',
    left: coords ? coords.x : 100,
    top: coords ? coords.y : 100,
    excludeFromExport: false,
    subTargetCheck: true,
    cellSize: 10,
  })

  return group
}

export const makeWorkFlowConnectors = props => {
  let { arrowStartEndCoords, cursorDirection, id } = props
  let arrowPointerDirection =
    cursorDirection === 'right' ? 'left' : cursorDirection
  let [startPos, endPos] = arrowStartEndCoords
  let [lineCenterCoords] = getLineMidPoint(startPos, endPos)
  let linePath = arrowStartEndCoords.reduce((acc, c) => {
    acc += ` ${c.x} ${c.y}`
    return acc
  }, 'M')
  let line = new fabric.Path(linePath, {
    ...commonObjProps,
    objType: 'line',
    fill: '',
    stroke: '#808080',
    objectCaching: false,
  })
  let circle = new fabric.Circle({
    ...commonObjProps,
    originX: 'center',
    originY: 'center',
    top: lineCenterCoords.y,
    left: lineCenterCoords.x,
    radius: 5.5,
    fill: '#fff',
    stroke: '#808080',
    objectCaching: false,
  })

  let arrowCoords = getArrowCoords(endPos, arrowPointerDirection)

  let arrowPath = arrowCoords.reduce((acc, c) => {
    acc += ` ${c.x} ${c.y}`
    return acc
  }, 'M')

  let arrow = new fabric.Path(`${arrowPath} Z`, {
    ...commonObjProps,
    objType: 'arrow',
    fill: '#808080',
    stroke: '#808080',
    objectCaching: false,
    hoverCursor: 'pointer',
  })

  let group = new fabric.Group([line, arrow, circle], {
    ...commonObjProps,
    id,
    originX: 'center',
    originY: 'center',
  })
  return group
}
