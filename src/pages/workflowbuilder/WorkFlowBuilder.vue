<template>
  <div class="stateflow-container mT10">
    <div class="pL10">Workflow builder</div>
    <div class="builder-container" ref="workFlowBuilderContainer">
      <div class="workflow-sidebar">
        <WorkFlowSidebar
          :selectedBlockType.sync="selectedBlockType"
          :isNewWorkFlowBlockDragged.sync="isNewWorkFlowBlockDragged"
        ></WorkFlowSidebar>
      </div>
      <canvas id="workflow-canvas" ref="workflowCanvas"></canvas>
    </div>
  </div>
</template>
<script>
import { fabric } from 'fabric'
import { isEmpty } from 'util/validation'
import throttle from 'lodash/throttle'
import {
  makeWorkFlowBlock,
  makeWorkFlowConnectors,
} from './elements/WorkFlowBlock'
import WorkFlowSidebar from './components/WorkFlowSidebar'
import CanvasPanZoom from 'pages/stateflows/mixins/CanvasPanZoomMixin'
import { getCenterCoords, getObjById } from 'pages/stateflows/utils/Common'
import { getDistBtwPoints, getLineMidPoint } from 'pages/stateflows/utils/Math'

const cursorDirectionMap = {
  bottom: 'bottom',
  right: 'right',
}

const threshold = 200

export default {
  mixins: [CanvasPanZoom],
  components: {
    WorkFlowSidebar,
  },
  data() {
    return {
      selectedBlockType: null,
      isNewWorkFlowBlockDragged: null,
      pseudoConnectorProps: null,
    }
  },
  created() {
    this.initData()
  },
  methods: {
    initData() {
      this.initCanvas()
    },
    initCanvas() {
      this.$nextTick(() => {
        let { $refs } = this
        if (!isEmpty($refs)) {
          this.canvas = new fabric.Canvas(this.$refs['workflowCanvas'], {
            selection: false,
            defaultCursor: 'grab',
          })
          this.$nextTick(() => {
            this.setupEditor()
            this.$nextTick(() => {
              this.registerNativeEvents()
            })
          })
        }
      })
    },
    setupEditor() {
      this.setDimensions('workFlowBuilderContainer')
      this.registerEvents()
    },
    registerEvents() {
      let canvas = this.canvas
      canvas.on('mouse:move', event => {
        let { target } = event
        if (isEmpty(target)) {
          if (this.isCanvasPanning) {
            this.onCanvasPan(event)
          }
        }
      })
      canvas.on('dragover', event => {
        let { isNewWorkFlowBlockDragged } = this
        if (isNewWorkFlowBlockDragged) {
          this.findClosestWorkFlowBlow(event)
        }
      })
      canvas.on('mouse:down', event => {
        if (isEmpty(event.target)) this.onCanvasPanStart(event)
      })
      canvas.on('mouse:up', event => {
        this.onCanvasPanEnd(event)
      })
      canvas.on('drop', event => {
        let { pseudoConnectorProps, selectedBlockType } = this
        let { type } = selectedBlockType || {}
        if (!isEmpty(pseudoConnectorProps) || type === 'event') {
          this.drawWorkFlowBlock(event)
          this.drawWorkFlowConnector({
            pseudoConnectorProps,
          })
        }
        this.$set(this, 'pseudoConnectorProps', null)
        this.$set(this, 'isNewWorkFlowBlockDragged', false)
      })
    },
    registerNativeEvents() {
      // Setup native events here
    },
    findClosestWorkFlowBlow: throttle(function({ e }) {
      let { canvas, isNewWorkFlowBlockDragged } = this
      let pos = canvas.getPointer(e)
      let canvasObjects = canvas.getObjects()
      let workFlowObjects = canvasObjects.filter(
        obj => !isEmpty(obj.workFlowType)
      )
      if (!isEmpty(workFlowObjects) && isNewWorkFlowBlockDragged) {
        for (let index = 0; index < workFlowObjects.length; index++) {
          let obj = workFlowObjects[index]
          let { aCoords } = obj
          let { br, bl, tr } = aCoords
          let { x: pointerX, y: pointerY } = pos
          let rectCenterCoords = getCenterCoords(obj)
          let { x: centerX, y: centerY } = rectCenterCoords
          let cursorDirection = null
          let distBtwCenterAndPos = null
          let { x: bottomRightX, y: bottomRightY } = br
          let { x: topRightX, y: topRightY } = tr
          let { x: bottomLeftX, y: bottomLeftY } = bl
          let midPointBottom = getLineMidPoint(
            {
              x: bottomLeftX,
              y: bottomLeftY,
            },
            {
              x: bottomRightX,
              y: bottomRightY,
            }
          )
          let midPointRight = getLineMidPoint(
            {
              x: topRightX,
              y: topRightY,
            },
            {
              x: bottomRightX,
              y: bottomRightY,
            }
          )
          let startPos = {}
          let endPos = {}
          let isAroundBottomSide =
            pointerX > bottomLeftX &&
            pointerX < bottomRightX &&
            bottomLeftY < pointerY
          let isAroundRightSide =
            pointerY < bottomRightY &&
            pointerY > topRightY &&
            pointerX > bottomRightX
          if (isAroundBottomSide) {
            cursorDirection = cursorDirectionMap.bottom
            startPos = midPointBottom[0]
            endPos = {
              x: startPos.x,
              y: startPos.y + 100,
            }
          } else if (isAroundRightSide) {
            cursorDirection = cursorDirectionMap.right
            startPos = midPointRight[0]
            endPos = {
              x: startPos.x + 100,
              y: startPos.y,
            }
          }
          if (!isEmpty(cursorDirection)) {
            if (cursorDirection === cursorDirectionMap.right) {
              distBtwCenterAndPos = getDistBtwPoints(
                { x: centerX, y: centerY },
                { x: pointerX, y: pointerY }
              )
            } else {
              distBtwCenterAndPos = getDistBtwPoints(
                { x: centerX, y: centerY },
                { x: pointerX, y: pointerY }
              )
            }
          }
          if (distBtwCenterAndPos && distBtwCenterAndPos < threshold) {
            let arrowStartEndCoords = [startPos, endPos]
            this.drawPseudoConnector({
              e,
              arrowStartEndCoords,
              cursorDirection,
            })
            break
          } else {
            this.removePseudoConnector()
          }
        }
      }
    }, 40),
    drawWorkFlowBlock({ e }) {
      let { canvas, selectedBlockType, pseudoConnectorProps } = this
      let pos = canvas.getPointer(e)
      let workflowBlockObj = makeWorkFlowBlock(selectedBlockType, canvas, pos)
      let { aCoords } = workflowBlockObj
      let { br, bl, tr } = aCoords
      let width = getDistBtwPoints(br, bl) / 2
      let height = getDistBtwPoints(tr, br) / 2
      if (!isEmpty(pseudoConnectorProps)) {
        let { arrowStartEndCoords, cursorDirection } = pseudoConnectorProps
        let endPos = arrowStartEndCoords[1]
        if (cursorDirection === cursorDirectionMap.right) {
          workflowBlockObj.set({
            top: endPos.y,
            left: endPos.x + width,
          })
        } else if (cursorDirection === cursorDirectionMap.bottom) {
          workflowBlockObj.set({
            top: endPos.y + height,
            left: endPos.x,
          })
        }
      }
      canvas.add(workflowBlockObj)
    },
    drawWorkFlowConnector(props) {
      let { pseudoConnectorProps } = props
      if (!isEmpty(pseudoConnectorProps)) {
        this.removePseudoConnector()
        let { canvas } = this
        let connector = makeWorkFlowConnectors({
          ...pseudoConnectorProps,
        })
        canvas.add(connector)
        this.$set(this, 'pseudoConnectorProps', null)
      }
    },
    drawPseudoConnector(props) {
      this.removePseudoConnector()
      let { canvas } = this
      let pseudoConnector = makeWorkFlowConnectors({
        ...props,
        id: 'pseudo-connector',
      })
      canvas.add(pseudoConnector)
      this.$set(this, 'pseudoConnectorProps', props)
    },
    removePseudoConnector() {
      let { canvas } = this
      this.$set(this, 'pseudoConnectorProps', null)
      let existingConnector = getObjById(canvas, 'pseudo-connector')
      canvas.remove(existingConnector)
      canvas.requestRenderAll()
    },
  },
}
</script>
<style lang="scss">
.stateflow-container {
  .builder-container {
    display: flex;
    margin: 10px;
    width: 1200px;
    height: 700px;
    border: 1px solid;
    .workflow-sidebar {
      border-right: 1px solid;
    }
  }
}
</style>
