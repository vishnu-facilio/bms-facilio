<script>
import { getLayout } from '../utils/AutoLayout'
import { getObjById, getCenterCoords } from '../utils/Common'
import { getDirectionBetweenPoints, getCombinedDirection } from '../utils/Math'
import { makeBg } from '../elements/Common'
import debounce from 'lodash/debounce'
import cloneDeep from 'lodash/cloneDeep'
import { isEmpty } from '@facilio/utils/validation'

export default {
  computed: {
    diagramJson: {
      get() {
        let diagramJson = this.stateFlow.diagramJson
        return diagramJson ? JSON.parse(diagramJson) : null
      },
      set(json) {
        this.stateFlow.diagramJson = JSON.stringify(json)
      },
    },
  },

  methods: {
    autoLayout() {
      let {
        stateTransitions,
        usedStates,
        stateFlow: { defaultStateId },
      } = this

      let coords = getLayout(usedStates, stateTransitions, defaultStateId)
      usedStates.map(state => this.drawState(state, coords[state.id]))
    },

    beautifyAnchors() {
      // Clear existing anchors
      let diagramJson = cloneDeep(this.diagramJson)
      diagramJson.states.forEach(
        s => (s.anchors = [null, null, null, null, null, null, null, null])
      )
      this.diagramJson = diagramJson

      this.$nextTick(() => {
        // Assign new anchors to all transitions based on direction
        let { canvas } = this
        let stateTransitions = [
          ...this.stateTransitions,
          ...this.tempTransitionList,
        ].filter(t => !isEmpty(t.id))

        stateTransitions.forEach(transition => {
          let fromStateObj = getObjById(canvas, transition.fromStateId)
          let toStateObj = getObjById(canvas, transition.toStateId)
          let fromCoords = getCenterCoords(fromStateObj)
          let toCoords = getCenterCoords(toStateObj)

          let dirForFromState = getCombinedDirection(
            getDirectionBetweenPoints(fromCoords, toCoords)
          )
          let anchorForFromState = this.getPreferredAnchor(
            transition.fromStateId,
            { direction: dirForFromState, isStartAnchor: true }
          )
          this.assignAnchor(fromStateObj, transition.id, {
            id: anchorForFromState,
          })

          let dirForToState = getCombinedDirection(
            getDirectionBetweenPoints(toCoords, fromCoords)
          )
          let anchorForToState = this.getPreferredAnchor(transition.toStateId, {
            direction: dirForToState,
          })
          this.assignAnchor(toStateObj, transition.id, {
            id: anchorForToState,
          })
        })
      })
    },

    beautifyLayout() {
      this.autoLayout()
      this.beautifyAnchors()
      this.$nextTick(() => {
        this.initTransitions()
        this.$nextTick(() => this.addToHistory())
      })
    },

    setBg() {
      return makeBg().then(bg => {
        this.canvas.backgroundColor = bg
        return this.canvas.renderAll()
      })
    },

    setActiveObject(activeObject) {
      this.activeObj && this.activeObj.makeInActive()
      this.activeObj = activeObject
      this.canvas.requestRenderAll()
    },

    resetSelection() {
      let canvas = this.canvas

      this.activeObj && this.activeObj.makeInActive()
      this.resetEditObjects()

      canvas.discardActiveObject()
      canvas.defaultCursor = 'grab'
      canvas.requestRenderAll()
    },

    showAllAnchors() {
      this.canvas
        .getObjects()
        .filter(obj => ['state'].includes(obj.objType))
        .forEach(obj => {
          obj.lockMovementY = true
          obj.lockMovementX = true
          obj.resetAnchors()
          obj.showAnchors()
          this.canvas.requestRenderAll()
        })
    },

    hideAllAnchors() {
      this.canvas
        .getObjects()
        .filter(obj => ['state'].includes(obj.objType))
        .forEach(obj => {
          obj.lockMovementY = false
          obj.lockMovementX = false
          obj.resetAnchors()
          obj.hideAnchors()
          this.canvas.requestRenderAll()
        })
    },

    enableTransitionMode() {
      this.isNewConnectionMode = true
      this.showAllAnchors()
    },

    disableTransitionMode() {
      this.isNewConnectionMode = false
      this.hideAllAnchors()
    },

    clearDragDrop() {
      let obj = this.canvas.getActiveObject()
      if (obj.objType === 'connector') {
        obj.isDragMode = false
      }
      this.canvas.discardActiveObject()

      this.isConnectorDragging = false
      this.disableTransitionMode()
      this.removePseudoConnector()
      this.resetDragProps()
      this.resetModifiedProps()
      this.resetNewConnectorProps()
    },

    onKeyPress({ keyCode }) {
      let isEsc = keyCode == 27

      if (isEsc && (this.isConnectorDragging || this.isNewConnectionMode)) {
        this.clearDragDrop()
      }
    },

    onWindowResize: debounce(function() {
      this.updateDiagram()
      this.setupEditor()
    }, 2 * 1000),

    addToDiagram(stateId, { x, y }) {
      let stateObj = {
        stateId,
        x,
        y,
        anchors: [null, null, null, null, null, null, null, null],
      }

      let diagramJson = cloneDeep(this.diagramJson || {})
      diagramJson.states = diagramJson.states || []

      let stateMap = diagramJson.states.map(state => state.stateId)

      if (stateMap.includes(stateId)) {
        let index = stateMap.findIndex(key => String(key) === String(stateId))
        diagramJson.states.splice(index, 1, stateObj)
      } else {
        diagramJson.states.push(stateObj)
      }

      this.$set(this, 'diagramJson', diagramJson)
    },

    removeTempTransition(transitionId) {
      let index = this.tempTransitionList.findIndex(t => t === transitionId)
      this.tempTransitionList.splice(index, 1)

      let connector = getObjById(this.canvas, String(transitionId))
      this.canvas.remove(connector)
      this.canvas.requestRenderAll()
    },

    swapTempTransition(transition, tempTransitionId) {
      let { anchorMap } = this

      let fromAnchor = (anchorMap[transition.fromStateId] || []).findIndex(
        id => String(id) === String(tempTransitionId)
      )
      let toAnchor = (anchorMap[transition.toStateId] || []).findIndex(
        id => String(id) === String(tempTransitionId)
      )

      this.setAnchor(transition.fromStateId, fromAnchor, transition.id)
      this.setAnchor(transition.toStateId, toAnchor, transition.id)

      this.updateAnchorMap(transition.fromStateId, fromAnchor, transition.id)
      this.updateAnchorMap(transition.toStateId, toAnchor, transition.id)

      this.removeTempTransition(tempTransitionId)
    },
  },
}
</script>
