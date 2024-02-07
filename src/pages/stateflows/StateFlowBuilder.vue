<template>
  <div
    class="formbuilder-fullscreen-popup stateflow-fullscreen-popup"
    :class="isLoading || hasError ? 'noselect' : ''"
  >
    <div class="setting-header2 position-relative">
      <div class="setting-title-block">
        <div class="setting-form-title mT10 mL15">
          <img class="fc-rebrand-logo" :src="brandConfig.logoLight" />
        </div>
      </div>
    </div>
    <div class="d-flex height100 pB50">
      <div class="editor-container">
        <Toolbar
          :autoLayout="beautifyLayout"
          :adjustZoom="adjustZoom"
          :undo="undo"
          :redo="redo"
          :canShowUndo="canShowUndo"
          :canShowRedo="canShowRedo"
          :fitToScreen="fitToScreen"
          :addState="addState"
          :addTransition="addTransition"
          :goBack="redirectToStateFlowList"
        ></Toolbar>
        <div
          class="d-flex flex-direction-column stateflow-canvas-container"
          ref="stateflowContainer"
        >
          <spinner
            v-if="!$refs['stateflowCanvas']"
            class="flex-middle"
            :show="!$refs['stateflowCanvas']"
            size="80"
          ></spinner>
          <canvas id="stateflow-canvas" ref="stateflowCanvas"></canvas>
          <!-- <div class="zoom-level">{{formattedZoomLevel}}%</div> -->
          <notification ref="notification" class="notification"></notification>
        </div>
      </div>
      <div class="sidebar-container">
        <sidebar
          v-if="!canShowStatePopup && !canShowTransitionPopup"
          :stateFlow="stateFlow"
          :usedStates="usedStates"
          :unusedStates="unusedStates"
          :selectedState.sync="selectedUnusedState"
          :editAction="editState"
          :addState="addState"
        ></sidebar>
        <new-state
          v-if="canShowStatePopup"
          ref="state-editor"
          :key="activeStateObj.id"
          :stateObj="activeStateObj"
          :stateFlowId="stateFlowId"
          :stateFlowObj="stateFlow"
          :isNew="isNew"
          :module="moduleName"
          :canShowDelete="true"
          :isAutoSaveNeeded="true"
          :showProgressBar="true"
          @onClose="resetSelection"
          @onSaving="onSaving"
          @onStateUpdate="onStateUpdate"
          @onStateCreate="onStateCreate"
          @onDelete="removeState"
          @onError="onError"
        ></new-state>
        <new-state-transition
          v-if="canShowTransitionPopup"
          ref="transition-editor"
          :key="activeTransitionObj.id"
          :transitionObj="activeTransitionObj"
          :stateFlowId="stateFlowId"
          :stateFlowObj="stateFlow"
          :isNew="isNew"
          :module="moduleName"
          :isAutoSaveNeeded="true"
          :showProgressBar="true"
          @onClose="resetSelection"
          @onSaving="onSaving"
          @onTransitionUpdate="onTransitionUpdate"
          @onTransitionCreate="onTransitionUpdate"
          @onDelete="onTransitionDelete"
          @onError="onError"
        ></new-state-transition>
      </div>

      <warning-dialog ref="warning"></warning-dialog>
    </div>
  </div>
</template>
<script>
import { fabric } from 'fabric'
import throttle from 'raf-throttle'
import { isEmpty } from '@facilio/utils/validation'
import { getAbsoluteCoords } from './utils/Math'
import { visibilityChange } from '@facilio/utils/page-visibility'

import {
  getUsedStates,
  getUnusedStates,
  getCenterCoords,
  getObjById,
} from './utils/Common'

import Toolbar from './components/BuilderToolbar'
import Sidebar from './components/BuilderSidebar'

import DataMixin from './mixins/StateFlowDataMixin'
import AddEditMixin from './mixins/StateFlowAddEditMixin'
import CanvasActions from './mixins/CanvasActionsMixin'
import CanvasPanZoom from './mixins/CanvasPanZoomMixin'
import CanvasDragDrop from './mixins/CanvasDragDropMixin'
import CanvasDrawMixin from './mixins/CanvasDrawMixin'
import CanvasHistory from './mixins/CanvasHistoryMixin'
import PathHelper from './mixins/PathHelperMixin'
import AnchorMap from './mixins/AnchorMap'

import NewStateTransition from 'pages/setup/stateflow/NewStateTransition'
import NewState from 'pages/setup/stateflow/NewState'
import Notification from './components/BuilderNotification'
import WarningDialog from './components/RemoveStateWarning'

/* For better performance, we internally represent grid by dividing the dimensions by cellSize.
 * Each point (or cell) in the grid is a 10px x 10px square area in the actual canvas.
 *
 * All elements will have a snapToGrid() fn that re-aligns the coordinates to this grid by
 * rounding points after dividing them by cellSize, and then scaling it back up to cellSize
 * More in Math.js
 *
 * Pathfinding will similarly be done in a scaled matrix and then points multiplied by 10 at the end.
 * More in PathHelperMixin.js
 */

export default {
  name: 'StateflowBuilder',
  title() {
    return 'Edit Stateflow'
  },
  mixins: [
    DataMixin,
    AddEditMixin,
    CanvasActions,
    CanvasPanZoom,
    CanvasDragDrop,
    CanvasHistory,
    PathHelper,
    AnchorMap,
    CanvasDrawMixin,
  ],
  components: {
    Toolbar,
    Sidebar,
    NewStateTransition,
    NewState,
    Notification,
    WarningDialog,
  },
  props: ['moduleName', 'stateFlowId', 'isCustomModule'],

  data() {
    return {
      isLoading: false,
      hasError: false,
      isDebugMode: false,
      activeObj: null,
    }
  },
  created() {
    this.initData()

    if (process.env.NODE_ENV !== 'production') this.isDebugMode = true

    this.gridUnit = 10
  },

  mounted() {
    visibilityChange(this, 1000 * 60 * 10).$on('refresh', this.initData)
  },

  beforeDestroy() {
    this.removeNativeEvents()
  },

  computed: {
    usedStates() {
      let states = this.availableStates
      let transitions = this.stateTransitions
      return getUsedStates(states, transitions, this.stateFlow.defaultStateId)
    },
    unusedStates() {
      let states = this.availableStates
      let transitions = this.stateTransitions
      let detachedStatesOnCanvas = this.draggedStateMap || []

      let unusedStates = getUnusedStates(
        states,
        transitions,
        this.stateFlow.defaultStateId
      )

      return unusedStates.filter(
        state => !detachedStatesOnCanvas.includes(state.id)
      )
    },
    detachedStates() {
      let detachedStatesOnCanvas = this.draggedStateMap || []

      return this.availableStates.filter(state =>
        detachedStatesOnCanvas.includes(state.id)
      )
    },
    brandConfig()
    {
      return window.brandConfig
    }
  },
  methods: {
    initData() {
      this.$store.dispatch('loadTicketStatus', this.module)

      Promise.all([
        this.fetchStateFlow(),
        this.fetchStates(),
        this.fetchTransitions(),
      ]).then(() => this.initCanvas())
    },

    initCanvas() {
      this.isDebugMode && this.logStatesAndTransitions() // DEBUGGING ONLY

      this.canvas = new fabric.Canvas(this.$refs['stateflowCanvas'], {
        selection: false,
        defaultCursor: 'grab',
      })

      this.setInitialZoom()
      this.$nextTick(() => {
        this.setupEditor()

        this.$nextTick(() => this.registerNativeEvents())
      })
    },

    setupEditor() {
      this.setDimensions('stateflowContainer')
      this.setBg().then(() => {
        this.drawStateFlow()

        this.$nextTick(() => {
          this.resetHistory()
          this.addToHistory()
          this.registerEvents()
        })
      })
    },

    drawStateFlow() {
      this.initStates([...this.usedStates, ...this.detachedStates])
      this.initRouter()

      this.$nextTick(() => {
        this.initTransitions()
      })
    },

    initStates(states = []) {
      if (this.isDebugMode) window.fabric = fabric

      let diagramJson = this.diagramJson

      if (!isEmpty(diagramJson)) {
        let anchorMap = this.anchorMap
        let coords = diagramJson.states.reduce((acc, state) => {
          acc[state.stateId] = { x: state.x, y: state.y }
          return acc
        }, {})

        states.map(state => {
          let coord = coords[state.id]
          let anchorList = anchorMap[state.id]
          this.drawState(state, coord, anchorList)
        })
      } else {
        this.autoLayout(states)
      }
    },

    handleStateMove(target) {
      this.reinitRouter()

      let associatedTransitions = target.anchors.reduce((acc, anchor) => {
        if (!isEmpty(anchor.transitionId)) {
          acc.push(String(anchor.transitionId))
        }
        return acc
      }, [])

      this.initTransitions(associatedTransitions)
    },

    initTransitions(selectedTransitions = []) {
      let canvas = this.canvas
      let draw = this.drawConnector
      let stateTransitions = [
        ...this.stateTransitions,
        ...this.tempTransitionList,
      ]

      if (!isEmpty(selectedTransitions)) {
        stateTransitions = [...stateTransitions].filter(t =>
          selectedTransitions.includes(String(t.id))
        )
      }

      stateTransitions.forEach(transition => {
        let isCycle = transition.fromStateId === transition.toStateId

        let fromStateObj = getObjById(canvas, transition.fromStateId)
        let toStateObj = getObjById(canvas, transition.toStateId)

        let [src, srcAnchor] = this.findAnchor(fromStateObj, transition.id)
        let [dest, destAnchor] = this.findAnchor(
          toStateObj,
          transition.id,
          isCycle
        )

        if (isEmpty(srcAnchor)) {
          let [coord, anchorId] = this.assignAnchor(fromStateObj, transition.id)
          src = coord
          srcAnchor = anchorId
        }

        if (isEmpty(destAnchor)) {
          let [coord, anchorId] = this.assignAnchor(toStateObj, transition.id)
          dest = coord
          destAnchor = anchorId
        }

        let path = this.getPathCoords(
          transition,
          { point: src, anchor: srcAnchor, element: fromStateObj },
          { point: dest, anchor: destAnchor, element: toStateObj }
        )

        draw(transition, path, [srcAnchor, destAnchor])
      })
    },

    moveConnectors: throttle(function(element) {
      let canvas = this.canvas
      let draw = this.drawConnector
      let transitions = this.stateTransitions
      let tempTransitionList = this.tempTransitionList
      let findAnchor = this.findAnchor

      let stateObjCoords = getCenterCoords(element)
      let visitedTransitions = []

      element.anchors.forEach((anchor, index) => {
        let transitionId = anchor.transitionId

        if (transitionId) {
          if (visitedTransitions.includes(transitionId)) {
            return // this is a cyclic transition and is already drawn
          } else {
            visitedTransitions.push(transitionId)
          }

          let transition
          let src, dest, srcAnchor, destAnchor, srcElement, destElement
          let connector = getObjById(canvas, transitionId)

          if (connector) {
            transition = connector.transition
          } else {
            transition = transitions.find(t => t.id === transitionId)

            if (isEmpty(transition)) {
              transition = tempTransitionList.find(t => t.id === transitionId)
            }
          }

          if (isEmpty(transition)) {
            // Clear anchor because transition does not exist in transition list
            this.$nextTick(() => this.clearAnchor(element, transitionId))
            return
          }

          let isCycle = transition.fromStateId === transition.toStateId

          if (String(transition.fromStateId) === String(element.id)) {
            src = getAbsoluteCoords(stateObjCoords, getCenterCoords(anchor))
            srcAnchor = index
            srcElement = element

            let targetEl = getObjById(canvas, transition.toStateId)
            let [coord, anchorId] = findAnchor(targetEl, transitionId, isCycle)
            dest = coord
            destAnchor = anchorId
            destElement = element
          } else if (String(transition.toStateId) === String(element.id)) {
            dest = getAbsoluteCoords(stateObjCoords, getCenterCoords(anchor))
            destAnchor = index
            destElement = element

            let targetEl = getObjById(canvas, transition.fromStateId)
            let [coord, anchorId] = findAnchor(targetEl, transitionId, isCycle)
            src = coord
            srcAnchor = anchorId
            srcElement = element
          }

          if (
            !isEmpty(src) &&
            !isEmpty(dest) &&
            !isEmpty(srcAnchor) &&
            !isEmpty(destAnchor)
          ) {
            let path = this.getPathCoords(
              transition,
              { point: src, anchor: srcAnchor, element: srcElement },
              { point: dest, anchor: destAnchor, element: destElement },
              { isDragging: true, draggedObject: element }
            )

            draw(transition, path, [srcAnchor, destAnchor])
          }
        }
      })
    }),

    registerEvents() {
      let canvas = this.canvas

      canvas.on('mouse:move', event => {
        let { target } = event
        if (target && target.isDragMode) {
          if (target.objType === 'connector' && !this.isNewConnectionMode) {
            this.dragConnector(event)
          } else if (target.objType === 'state' && this.isNewConnectionMode) {
            this.dragConnector(event)
          }
        } else if (target && target.objType === 'state') {
          let mouseCoords = this.canvas.getPointer(event.e)
          target.focusClosestAnchor(mouseCoords)
        } else if (isEmpty(event.target) && this.isCanvasPanning) {
          this.onCanvasPan(event)
        }
      })

      canvas.on('mouse:down', event => {
        if (isEmpty(event.target)) this.onCanvasPanStart(event)
      })

      canvas.on('mouse:up', event => {
        this.onCanvasPanEnd(event)
        this.isConnectorDragging = false

        if (isEmpty(event.target)) {
          this.removePseudoConnector()
          this.resetSelection()
          this.disableTransitionMode()
        }
      })

      canvas.on('drop', event => {
        this.addStateToCanvas(event)
      })

      canvas.on('object:moving', () => {
        this.deferAutoSave = true
      })

      // Whenever any state is moved, push that to history for undo/redo
      canvas.on('object:moved', () => {
        this.deferAutoSave = false
        this.$nextTick(() => {
          this.addToHistory()
          this.autoSave()
        })
      })
    },

    registerNativeEvents() {
      window.addEventListener('keydown', this.onKeyPress, {
        passive: true,
      })

      window.addEventListener('resize', this.onWindowResize, {
        passive: true,
      })

      !this.isDebugMode &&
        this.$refs['stateflowContainer'].addEventListener('contextmenu', e => {
          if (e.button == 2) e.preventDefault()
        })
    },

    removeNativeEvents() {
      window.removeEventListener('keydown', this.onKeyPress)
      window.removeEventListener('resize', this.onWindowResize)
    },

    redirectToStateFlowList() {
      let { moduleName } = this
      this.$router.push({ name: 'stateflow.list', params: { moduleName } })
    },
  },
}
</script>
<style lang="scss" scoped>
.formbuilder-fullscreen-popup {
  bottom: 0;
}
.saving-container {
  font-size: 11px;
  font-weight: 500;
  font-style: italic;
  letter-spacing: 0.4px;
  color: #39b2c2;
  margin-left: 15px;
}
.setting-header2 {
  height: 50px;
  padding: 0;
  box-shadow: 0 2px 12px 0 rgba(155, 157, 180, 0.1);
  background-color: var(--fc-toolbar-bg);
  border-color: #e6ecf3;
  border-width: 0 0 1px 0px;
  border-style: solid;
}
.setting-status-block {
  color: #fff;
}
.editor-container {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
}
.stateflow-canvas-container {
  flex-grow: 1;
  position: relative;
}
.sidebar-container {
  width: 30%;
  min-width: 500px;
  background-color: #fff;
  border-left: 1px solid #ededed;
  box-shadow: -5px 0px 4px 0 rgba(218, 218, 218, 0.32);
}
.zoom-level {
  position: absolute;
  left: 10px;
  bottom: 10px;
  color: #666;
}
.notification {
  position: absolute;
  bottom: 10px;
  right: 20px;
  font-size: 14px;
  min-width: 180px;
  line-height: 19px;
}
</style>
