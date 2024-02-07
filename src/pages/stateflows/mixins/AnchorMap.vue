<script>
/*
 * Each state has 8 anchors, from which transitions can start, or end at. 3 on the top and bottom and 1 on each side.
 *
 *         ---0---1---2---    Anchors are referred based on their index along the boundary of the shape in clockwise
 *        |               |   direction. Each anchor will have the transition's id as `transitionId` inside it.
 *        7               3
 *        |               |   While assigning anchors automatically, we take the prominent direction between the two states
 *         ---6---5---4---    and then assign an angle from anchorDirectionHash based on it.
 */

import isString from 'lodash/isString'
import isInteger from 'lodash/isInteger'
import { isEmpty } from '@facilio/utils/validation'
import { getCenterCoords, isValidAnchor } from '../utils/Common'
import { getAbsoluteCoords } from '../utils/Math'
import cloneDeep from 'lodash/cloneDeep'

const startAnchorDirectionHash = {
  top: [7, 3, 4, 6, 5],
  bottom: [5, 6, 4, 7, 3],
  left: [7, 6, 0, 5, 1],
  right: [3, 4, 2, 5, 1],
  bottomright: [3, 4, 5, 2],
  bottomleft: [7, 6, 5, 0],
  topleft: [7, 6, 5, 0],
  topright: [3, 4, 5, 2],
}

const endAnchorDirectionHash = {
  top: [1, 0, 2, 7, 3],
  bottom: [7, 3, 5, 6, 4],
  left: [7, 0, 6, 5, 1],
  right: [3, 2, 4, 1, 5],
  bottomright: [3, 4, 5, 2, 1],
  bottomleft: [7, 6, 5, 0, 1],
  topleft: [7, 0, 5, 6, 1],
  topright: [3, 2, 4, 5],
}

export default {
  computed: {
    anchorMap() {
      let { diagramJson, usedStates } = this

      if (!isEmpty(diagramJson)) {
        return diagramJson.states.reduce((anchorMap, state) => {
          anchorMap[state.stateId] = state.anchors
          return anchorMap
        }, {})
      } else {
        return usedStates.reduce((anchorMap, state) => {
          anchorMap[state.id] = [null, null, null, null, null, null, null, null]
          return anchorMap
        }, {})
      }
    },
  },
  methods: {
    isAnchorFree(stateId, anchorId) {
      return isEmpty(this.anchorMap[stateId][anchorId])
    },

    setAnchor(state, anchorId, transitionId) {
      let isValidAnchorId = anchorId >= 0 && anchorId < 8

      if (isString(state) || isInteger(state)) {
        state = this.canvas.getObjects().find(obj => {
          return obj.objType === 'state' && String(obj.id) === String(state)
        })
      }

      if (!isEmpty(state) && isValidAnchorId) {
        state.setAnchor(anchorId, transitionId)
      }
    },

    updateAnchorMap(stateId, anchorId, transitionId = null) {
      let diagramJson = cloneDeep(this.diagramJson || {})
      let state = null

      if (diagramJson.states) {
        state = diagramJson.states.find(state => state.stateId === stateId)
      }

      if (isEmpty(state)) {
        state = {
          stateId,
          anchors: null,
        }
        diagramJson.states = diagramJson.states || []
        diagramJson.states.push(state)
      }

      state.anchors = !isEmpty(state.anchors)
        ? state.anchors
        : [null, null, null, null, null, null, null, null]

      if (transitionId) {
        state.anchors[anchorId] = transitionId
      } else {
        state.anchors[anchorId] = null
      }

      this.diagramJson = diagramJson
    },

    clearAnchor(stateObj, transitionId) {
      let anchorId = stateObj.anchors.findIndex(
        anchor => String(anchor.transitionId) === String(transitionId)
      )

      if (anchorId >= 0 && anchorId < 8) {
        this.setAnchor(stateObj, anchorId, null)
        this.updateAnchorMap(stateObj.id, anchorId)
      }
    },

    getPreferredAnchor(stateId, { id, direction, isStartAnchor }) {
      if (id && this.isAnchorFree(stateId, id)) {
        // If a preferred anchor is specified, check if it is empty and return it
        return id
      } else if (direction) {
        // Pick an anchor along the direction ie.. if 'top' is preferred we can choose either 0, 1 or 2
        // if available. If not try for the anchors on sides and then fallback to whatever is empty
        let anchorHash = isStartAnchor
          ? startAnchorDirectionHash
          : endAnchorDirectionHash
        let preferredAnchors = anchorHash[direction] || []

        return preferredAnchors.find(id => this.isAnchorFree(stateId, id))
      }
    },

    assignAnchor(stateObj, transitionId, preferredAnchor = {}) {
      let groupCoords = getCenterCoords(stateObj)

      let freeAnchorId = this.getPreferredAnchor(stateObj.id, preferredAnchor)

      // Preferred anchors were not free or preference was not specified, pick whatever is available
      if (!isValidAnchor(freeAnchorId)) {
        let anchorMap = this.anchorMap[stateObj.id] || []
        if (isEmpty(anchorMap)) {
          freeAnchorId = 0
        } else {
          freeAnchorId = anchorMap.findIndex(id => isEmpty(id))
        }
      }

      // In case anchor is not found use center pt of state obj
      let anchorCoords = { x: 0, y: 0 }

      // Update anchor details in map and stateObj
      if (isValidAnchor(freeAnchorId)) {
        this.setAnchor(stateObj, freeAnchorId, transitionId)
        this.updateAnchorMap(stateObj.id, freeAnchorId, transitionId)

        anchorCoords = getCenterCoords(stateObj.anchors[freeAnchorId])
      }

      return [getAbsoluteCoords(groupCoords, anchorCoords), freeAnchorId]
    },

    findAnchor(stateObj, transitionId, isCyclicTransition = false) {
      let groupCoords = getCenterCoords(stateObj)
      let anchorCoords = { x: 0, y: 0 }

      let anchorMap = [...(this.anchorMap[stateObj.id] || [])]

      let anchorId = anchorMap.findIndex(
        id => String(id) === String(transitionId)
      )

      if (isCyclicTransition) {
        // To find the second anchor index since if cyclic this anchor has already been
        // found during the first run
        let offset = anchorMap
          .slice(anchorId + 1)
          .findIndex(id => String(id) === String(transitionId))

        anchorId += offset + 1
      }

      if (anchorId >= 0 && anchorId < 8) {
        anchorCoords = getCenterCoords(stateObj.anchors[anchorId])
      }

      return [
        getAbsoluteCoords(groupCoords, anchorCoords),
        !isEmpty(anchorId) ? anchorId : null,
      ]
    },
  },
}
</script>
