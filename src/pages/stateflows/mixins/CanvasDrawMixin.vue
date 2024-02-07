<script>
import { isEmpty } from '@facilio/utils/validation'
import { makePseudoConnector } from '../elements/PseudoConnector'
import { getObjById, isValidAnchor } from '../utils/Common'
import { makeState } from '../elements/State'
import { makeConnector } from '../elements/Transition'

export default {
  methods: {
    drawPseudoConnector(mouseCoords, isExisitingTransition) {
      this.removePseudoConnector()

      let canvas = this.canvas
      let src, dest, isCloserToSrc, transition
      let srcAnchor = -1
      let destAnchor = -1

      if (isExisitingTransition) {
        if (isEmpty(this.draggedConnector)) return

        src = this.draggedConnectorProps.src
        dest = this.draggedConnectorProps.dest
        isCloserToSrc = this.draggedConnectorProps.isCloserToSrc

        let { freeAnchorId } = this.draggedConnectorProps

        if (isCloserToSrc) {
          srcAnchor = isValidAnchor(freeAnchorId) ? freeAnchorId : -1
        } else {
          destAnchor = isValidAnchor(freeAnchorId) ? freeAnchorId : -1
        }
        transition = this.draggedConnector.transition
      } else {
        if (isEmpty(this.newConnectorProps)) return
        let { fromStateAnchor } = this.newConnectorProps

        src = this.newConnectorProps.src
        srcAnchor = isValidAnchor(fromStateAnchor) ? fromStateAnchor : -1
        dest = null
        isCloserToSrc = false
        transition = this.newConnector.transition
      }

      if (isCloserToSrc) {
        src = mouseCoords
      } else {
        dest = mouseCoords
      }

      if (!isEmpty(src) && !isEmpty(dest)) {
        let path = this.getPathCoords(
          null,
          { point: src, anchor: srcAnchor },
          { point: dest, anchor: destAnchor },
          { isDragging: true }
        )

        let connector = makePseudoConnector(
          'pseudo-connector',
          transition,
          path
        )
        canvas.add(connector)
      }

      canvas.requestRenderAll()
    },

    removePseudoConnector() {
      let canvas = this.canvas

      let existingConnector = getObjById(canvas, 'pseudo-connector')
      canvas.remove(existingConnector)
      canvas.requestRenderAll()
    },

    createState(state, coords, anchors) {
      let { canvas } = this
      let stateObj = makeState(state, canvas, coords, anchors)

      stateObj.snapToGrid()
      stateObj.setPrevCoords()

      canvas.add(stateObj)

      return stateObj
    },

    drawState(state, coords, anchors) {
      let {
        canvas,
        createState,
        moveConnectors,
        handleStateMove,
        handleIntersections,
        handleBounds,
      } = this
      let stateObj = getObjById(canvas, state.id)

      if (stateObj) {
        stateObj.reInit(state, coords)
        canvas.requestRenderAll()

        if (stateObj.hasPositionChanged()) {
          moveConnectors(stateObj)
          stateObj.setPrevCoords()
        }
      } else {
        stateObj = createState(state, coords, anchors)
        stateObj.on('mouseover', event => {
          let { target } = event
          let mouseCoords = canvas.getPointer(event.e)

          if (target) {
            if (this.isNewConnectionMode) {
              target.focusClosestAnchor(mouseCoords)
            } else {
              target.onHoverStart()
            }

            canvas.requestRenderAll()
          }
        })

        stateObj.on('mouseout', ({ target }) => {
          if (target) {
            if (this.isNewConnectionMode) target.resetAnchors()

            target.onHoverEnd()
            canvas.requestRenderAll()
          }
        })

        stateObj.on('mousedown', event => {
          let { target } = event

          if (this.isNewConnectionMode) {
            target.isDragMode = true
            this.onNewConnectionMouseDown(event)
          } else {
            target.oldCoords = target.calcCoords()

            let state = this.availableStates.find(
              state => state.id === target.id
            )
            this.setActiveObject(target)
            target.makeActive()
            this.editState(state)
          }
        })

        stateObj.on('mouseup', event => {
          if (this.isNewConnectionMode) {
            event.target.isDragMode = false
            event.target.resetAnchors()

            this.onNewConnectionMouseUp(event)
          }
        })

        stateObj.on('moving', ({ target }) => {
          target.snapToGrid()

          if (target.hasPositionChanged()) {
            moveConnectors(target)
            target.setPrevCoords()
          }
        })

        stateObj.on('moved', ({ target }) => {
          target.snapToGrid()
          handleBounds(target)
          handleIntersections(target)
          this.$nextTick(() => handleStateMove(target))
        })
      }
    },

    removeState(id) {
      let stateObj = getObjById(this.canvas, String(id))
      let isInDiagram = !isEmpty(stateObj)
      let isUsed = !isEmpty(
        this.usedStates.find(s => String(s.id) === String(id))
      )

      if (isInDiagram) {
        if (isUsed) {
          this.$refs['warning'].show()
        } else {
          this.canvas.remove(stateObj)
          let index = this.draggedStateMap.findIndex(
            stateId => String(id) === String(stateId)
          )

          this.draggedStateMap.splice(index, 1)
          this.resetEditObjects()
          this.canvas.requestRenderAll()
        }
      } else {
        return
      }
    },

    drawMissingStates(transition) {
      let canvas = this.canvas
      let { fromStateId, toStateId } = transition

      let missingStates = [fromStateId, toStateId].reduce((acc, id) => {
        let obj = getObjById(canvas, String(id))
        if (isEmpty(obj)) {
          let state = this.availableStates.find(
            s => String(s.id) === String(id)
          )
          if (state) acc.push()
        }
      }, [])

      !isEmpty(missingStates) && this.initStates(missingStates)
    },

    drawConnector(transition, coords, anchors) {
      let {
        canvas,
        editTransition,
        onConnectorMouseDown,
        onConnectorMouseUp,
        showAllAnchors,
        hideAllAnchors,
      } = this

      let existingConnector = getObjById(canvas, transition.id)
      canvas.remove(existingConnector)

      let connector = makeConnector(transition, coords, canvas, anchors)

      if (isEmpty(connector)) {
        return
      }
      canvas.add(connector)

      canvas.sendToBack(connector)
      canvas.requestRenderAll()

      connector.on('mousedown', event => {
        if (event.target) {
          this.isConnectorDragging = true

          this.setActiveObject(event.target)
          event.target.makeActive()
          onConnectorMouseDown(event)
          editTransition(event.target.transition)
          showAllAnchors()
        }
      })
      connector.on('mouseup', event => {
        if (event.target) {
          this.isConnectorDragging = false

          if (event.target.isDragMode) {
            onConnectorMouseUp(event)
            hideAllAnchors()
          }
          canvas.requestRenderAll()
        }
      })
    },
  },
}
</script>
