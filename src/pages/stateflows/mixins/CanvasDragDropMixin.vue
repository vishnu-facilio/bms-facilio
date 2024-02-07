<script>
import throttle from 'raf-throttle'
import { isEmpty } from '@facilio/utils/validation'
import { findClosestPathEnd, getAbsoluteCoords } from '../utils/Math'
import { isObjIntesecting, getCenterCoords } from '../utils/Common'
import { v4 as uuid } from 'uuid'

export default {
  data() {
    return {
      // Props for transition edit
      draggedConnector: null,
      draggedConnectorProps: null,
      modifiedTransitionId: null,
      modifiedStates: {
        prev: null,
        current: null,
      },
      // Props for transition creation
      isNewConnectionMode: false,
      newConnector: null,
      newConnectorProps: null,
      // Props for unused states
      selectedUnusedState: null,
      draggedStateMap: [],
    }
  },
  methods: {
    /*
     * State Drag & Drop
     */
    addStateToCanvas({ e }) {
      let pos = this.canvas.getPointer(e)
      let activeStateObj = { ...this.selectedUnusedState }

      if (pos && !isEmpty(activeStateObj)) {
        this.drawState(activeStateObj, pos)
        this.draggedStateMap.push(activeStateObj.id)
        this.selectedUnusedState = null

        this.addToDiagram(activeStateObj.id, pos)

        this.$nextTick(() => {
          this.addToHistory(true)
          this.autoSave()
          this.reinitRouter()
        })
      }
    },

    /*
     * Existing Connector Drag & Drop
     */
    onConnectorMouseDown(event) {
      event.target.isDragMode = true
      let mouseCoord = this.canvas.getPointer(event.e)
      let pathPoints = event.target.pathPoints

      this.draggedConnector = event.target

      if (!isEmpty(event.target.pathPoints)) {
        this.draggedConnectorProps = {
          src: event.target.pathPoints[0],
          dest: event.target.pathPoints[event.target.pathPoints.length - 1],
          isCloserToSrc: findClosestPathEnd(pathPoints, mouseCoord),
        }
      }
    },

    onConnectorMouseUp(event) {
      let canvas = this.canvas
      let mouseCoords = this.canvas.getPointer(event.e)

      let { isCloserToSrc } = this.draggedConnectorProps
      let { transition } = this.draggedConnector

      if (!isEmpty(transition)) {
        this.canvas
          .getObjects()
          .filter(obj => obj.objType === 'state')
          .forEach(obj => {
            obj.onHoverEnd()

            let existingStateId = isCloserToSrc
              ? transition['fromStateId']
              : transition['toStateId']

            let isCycle =
              obj.id ===
              (isCloserToSrc
                ? transition['toStateId']
                : transition['fromStateId'])

            let hasStateChanged = obj.id !== existingStateId && !isCycle

            if (isObjIntesecting(obj, mouseCoords) && hasStateChanged) {
              let stateId = obj.id
              let freeAnchorId = obj.focusClosestAnchor(mouseCoords)

              this.modifiedTransitionId = transition.id
              this.modifiedStates = {
                current: stateId,
                prev: isCloserToSrc
                  ? transition.fromStateId
                  : transition.toStateId,
              }
              this.draggedConnectorProps = {
                ...this.draggedConnectorProps,
                freeAnchorId,
              }

              this.handleTransitionEdit(
                transition,
                stateId,
                isCloserToSrc
              ).catch(() => {
                this.$nextTick(
                  () => this.resetDragProps(),
                  this.removePseudoConnector()
                )
              })
            } else if (isObjIntesecting(obj, mouseCoords) && !isCycle) {
              let newAnchorId = obj.focusClosestAnchor(mouseCoords)
              let [, existingAnchorId] = this.findAnchor(obj, transition.id)

              if (newAnchorId !== existingAnchorId) {
                this.clearAnchor(obj, transition.id)
                this.assignAnchor(obj, transition.id, { id: newAnchorId })

                this.$nextTick(() => {
                  this.autoSave()
                  this.initTransitions([String(transition.id)])
                })
              }

              this.$nextTick(() => this.setActiveObject(event.target))
            }
          })
      }

      event.target.isDragMode = false
      this.removePseudoConnector()
      canvas.requestRenderAll()
    },

    /*
     * New Connector Drag & Drop
     */
    onNewConnectionMouseDown(event) {
      let state = event.target
      let mouseCoords = this.canvas.getPointer(event.e)
      let freeAnchorId = state.focusClosestAnchor(mouseCoords)

      let groupCoords = getCenterCoords(state)
      let anchorCoords = getCenterCoords(state.anchors[freeAnchorId]) || {
        x: 0,
        y: 0,
      }

      this.newConnectorProps = {
        fromStateId: Number(state.id),
        fromStateAnchor: freeAnchorId,
        src: getAbsoluteCoords(groupCoords, anchorCoords),
      }
      this.newConnector = { transition: { isPartial: true } }
    },

    onNewConnectionMouseUp(event) {
      let mouseCoords = this.canvas.getPointer(event.e)

      this.canvas
        .getObjects()
        .filter(obj => obj.objType === 'state')
        .forEach(obj => {
          let isCycle =
            String(this.newConnectorProps.fromStateId) === String(obj.id)

          if (isObjIntesecting(obj, mouseCoords) && !isCycle) {
            let freeAnchorId = obj.focusClosestAnchor(mouseCoords)

            let groupCoords = getCenterCoords(obj)
            let anchorCoords = getCenterCoords(obj.anchors[freeAnchorId]) || {
              x: 0,
              y: 0,
            }

            this.newConnectorProps = {
              ...this.newConnectorProps,
              toStateId: obj.id,
              toStateAnchor: freeAnchorId,
              dest: getAbsoluteCoords(groupCoords, anchorCoords),
            }

            let tempId = `temp${uuid()}`

            this.setNewTransitionProps({
              ...this.newConnectorProps,
              id: tempId,
            })
            this.addTempConnector({ ...this.newConnectorProps, id: tempId })
          }
          obj.resetAnchors()
          obj.onHoverEnd()
        })

      this.removePseudoConnector()
      this.canvas.requestRenderAll()

      this.$nextTick(() => {
        this.resetNewConnectorProps()
      })
    },

    /*
     * Pesudo Connector Handling
     */
    dragConnector: throttle(function(event) {
      let mouseCoords = this.canvas.getPointer(event.e)
      let isExisitingTransition = !this.isNewConnectionMode

      this.drawPseudoConnector(mouseCoords, isExisitingTransition)

      this.canvas.getObjects().forEach(obj => {
        if (obj.objType === 'state') {
          if (isObjIntesecting(obj, mouseCoords)) {
            obj.focusClosestAnchor(mouseCoords)
            obj.onHoverStart()
          } else {
            obj.resetAnchors()
            obj.onHoverEnd()
          }
        }
      })
    }),

    /*
     * Reset fns
     */
    resetDragProps() {
      this.draggedConnector = null
      this.draggedConnectorProps = null
    },

    resetModifiedProps() {
      this.modifiedTransitionId = null
      this.modifiedStates = {
        prev: null,
        current: null,
      }
    },

    resetNewConnectorProps() {
      this.newConnector = null
      this.newConnectorProps = null
    },

    /*
     * Reset at edge and overlapping
     */
    handleBounds(stateObj) {
      let {
        aCoords: { tl, br },
      } = stateObj
      let { allowedCanvasPanSize } = this
      let offSet = 50

      if (tl.x < 2) {
        let { width } = stateObj.getBoundingRect()

        stateObj.left = width / 2 + offSet
      } else if (allowedCanvasPanSize <= br.x) {
        let xDiffWidth = br.x - allowedCanvasPanSize

        stateObj.left -= xDiffWidth + offSet
      }

      if (tl.y < 2) {
        let { height } = stateObj.getBoundingRect()

        stateObj.top = height / 2 + offSet
      } else if (allowedCanvasPanSize <= br.y) {
        let yDiffHeight = br.y - allowedCanvasPanSize

        stateObj.top -= yDiffHeight + offSet
      }
      return stateObj
    },

    handleIntersections(stateObj) {
      let hasIntersection = this.canvas
        .getObjects()
        .filter(
          obj =>
            obj.objType === 'state' && String(obj.id) !== String(stateObj.id)
        )
        .some(state => stateObj.intersectsWithObject(state, true, true))

      if (hasIntersection) {
        this.skipHistoryPush = true

        let json = this.canvasStates[this.currentCanvasState]
        let { states } = JSON.parse(json)
        let { x, y } = states.find(
          state => String(state.stateId) === String(stateObj.id)
        )

        stateObj.left = x
        stateObj.top = y
      }
    },
  },
}
</script>
