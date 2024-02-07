<script>
import { isEmpty } from '@facilio/utils/validation'
import { getObjById } from '../utils/Common'
import { API } from '@facilio/api'
import cloneDeep from 'lodash/cloneDeep'

export default {
  data() {
    return {
      activeTransitionObj: null,
      canShowTransitionPopup: false,
      tempTransitionList: [],
    }
  },
  methods: {
    editTransition(transition) {
      this.activeTransitionObj = cloneDeep(transition)
      this.canShowStatePopup = false
      this.isNew = transition.isPartial
      this.canShowTransitionPopup = true
    },

    addTransition() {
      this.resetSelection()
      this.resetEditObjects()
      this.isNew = true
      this.activeTransitionObj = {}
      this.canShowTransitionPopup = true

      this.enableTransitionMode()
    },

    updateTransitionParents(transition) {
      let data = {
        stateFlowId: this.stateFlowId,
        stateTransition: {
          id: transition.id,
          fromStateId: transition.fromStateId,
          toStateId: transition.toStateId,
        },
      }
      return new Promise((resolve, reject) => {
        this.onSaving()
        API.post('/v2/statetransition/updateState', data).then(({ error }) => {
          if (!error) {
            this.onTransitionUpdate(transition)
            resolve()
          } else {
            this.onError(error)
            reject()
          }
        })
      })
    },

    onTransitionUpdate(transition, tempTransitionId) {
      let transitionId = transition.id
      let index = this.stateTransitions.findIndex(
        t => String(t.id) === String(transitionId)
      )

      if (isEmpty(index)) {
        this.stateTransitions.push(transition)
      } else {
        this.$set(this.stateTransitions, index, transition)
      }

      if (tempTransitionId) {
        this.swapTempTransition(transition, tempTransitionId)
      }

      this.updateDiagram()
      this.updateTransitionChange(transition)
        .then(() => {
          this.drawMissingStates(transition)

          this.$nextTick(() => {
            this.initTransitions([String(transitionId)])
            this.resetDragProps()
          })
        })
        .catch(error => {
          console.error(error)

          this.$nextTick(() => {
            this.drawStateFlow()
            this.resetDragProps()
          })
        })
      this.editTransition(transition)
      this.$refs['notification'].showSuccess('Saved')
    },

    onTransitionDelete(transitionId, options) {
      let { isPartial = false } = options || {}
      let { canvas, stateTransitions, tempTransitionList } = this

      let index = (isPartial ? tempTransitionList : stateTransitions).findIndex(
        t => t.id === transitionId
      )

      let transition = (isPartial ? tempTransitionList : stateTransitions)[
        index
      ]

      if (isEmpty(transition)) return

      let fromStateObj = getObjById(canvas, transition.fromStateId)
      let newStateObj = getObjById(canvas, transition.toStateId)

      this.clearAnchor(fromStateObj, transitionId)
      this.clearAnchor(newStateObj, transitionId)

      if (isPartial) {
        tempTransitionList.splice(index, 1)
      } else {
        stateTransitions.splice(index, 1)
      }

      canvas.remove(getObjById(canvas, String(transitionId)))
      canvas.requestRenderAll()

      this.autoSave()
      this.resetSelection()
    },

    updateTransitionChange(transition) {
      if (String(transition.id) === String(this.modifiedTransitionId)) {
        let prevStateObj = getObjById(this.canvas, this.modifiedStates.prev)
        let nextStateObj = getObjById(this.canvas, this.modifiedStates.current)

        let anchorId = (this.draggedConnectorProps || {}).freeAnchorId
        let preferredAnchor = anchorId
          ? {
              id: anchorId,
            }
          : {}

        this.clearAnchor(prevStateObj, transition.id)
        this.assignAnchor(nextStateObj, transition.id, preferredAnchor)

        this.resetModifiedProps()
      } else {
        this.updateDiagram()
      }

      return this.saveDiagram()
    },

    handleTransitionEdit(transition, stateId, isFromState = false) {
      let transitionId = transition && transition.id

      if (transition.isPartial) {
        let oldState = null
        let newState = getObjById(this.canvas, stateId)

        if (isFromState) {
          oldState = getObjById(this.canvas, transition.fromStateId)
          transition.fromStateId = stateId
        } else {
          oldState = getObjById(this.canvas, transition.toStateId)
          transition.toStateId = stateId
        }

        let anchorId = (this.draggedConnectorProps || {}).freeAnchorId
        let preferredAnchor = anchorId
          ? {
              id: anchorId,
            }
          : {}

        this.clearAnchor(oldState, transition.id)
        this.assignAnchor(newState, transition.id, preferredAnchor)

        this.initTransitions([String(transition.id)])

        this.$nextTick(() => this.resetDragProps())
        return Promise.resolve()
      } else {
        return new Promise((resolve, reject) => {
          if (!transitionId) {
            return reject()
          }

          let transition = cloneDeep(
            this.stateTransitions.find(({ id }) => id === transitionId)
          )

          if (isFromState) {
            transition.fromStateId = stateId
          } else {
            transition.toStateId = stateId
          }

          this.$nextTick(() => {
            this.updateTransitionParents(transition)
              .then(resolve)
              .catch(reject)
          })
        })
      }
    },

    setNewTransitionProps(transition) {
      this.$refs['transition-editor'].setProps({
        fromStateId: transition.fromStateId,
        toStateId: transition.toStateId,
        isPartialTransition: true,
        tempTransitionId: transition.id,
      })
    },

    addTempConnector(transition) {
      let {
        id,
        fromStateId,
        fromStateAnchor,
        toStateId,
        toStateAnchor,
      } = transition

      this.tempTransitionList.push({
        id,
        fromStateId: fromStateId,
        toStateId: toStateId,
        isPartial: true,
      })

      this.setAnchor(fromStateId, fromStateAnchor, id)
      this.setAnchor(toStateId, toStateAnchor, id)

      this.updateAnchorMap(fromStateId, fromStateAnchor, id)
      this.updateAnchorMap(toStateId, toStateAnchor, id)

      this.initTransitions([id])
      this.disableTransitionMode()

      this.$nextTick(() => {
        let target = getObjById(this.canvas, id)
        !isEmpty(target) && this.setActiveObject(target)
      })
    },
  },
}
</script>
