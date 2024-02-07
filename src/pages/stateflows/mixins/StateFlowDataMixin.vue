<script>
/* eslint-disable no-console */
import { isEmpty } from '@facilio/utils/validation'
import debounce from 'lodash/debounce'
import { API } from '@facilio/api'
import cloneDeep from 'lodash/cloneDeep'

export default {
  data() {
    return {
      stateFlow: {},
      availableStates: [],
      stateTransitions: [],
      isNew: false,
      activeStateObj: null,
      canShowStatePopup: false,
      deferAutoSave: false,
    }
  },
  computed: {
    transitionStateMap() {
      let { stateTransitions: transitions } = this
      let entries = transitions.map(t => [
        String(t.id), //key
        [t.fromStateId, t.toStateId], //value
      ])

      return Object.fromEntries(entries)
    },
  },
  methods: {
    logStatesAndTransitions() {
      if (this.isDebugMode) {
        let { log } = console
        let { availableStates: states, stateTransitions: transitions } = this

        log('\nStates\n')
        states.forEach(s => log(`${s.id} - ${s.displayName}`))
        log('\nState Transitions\n')
        transitions.forEach(t =>
          log(`${t.name} (${t.id}): ${t.fromStateId} - ${t.toStateId}`)
        )
        log('\n\n')
      }
    },

    fetchStateFlow() {
      return API.get(`/v2/stateflow/view`, {
        stateFlowId: this.stateFlowId,
      }).then(({ error, data }) => {
        if (!error) this.stateFlow = data.stateFlow
      })
    },

    fetchStates() {
      return API.get(`/v2/state/list`, {
        parentModuleName: this.moduleName,
      }).then(({ error, data }) => {
        if (!error) this.availableStates = data.status
      })
    },

    fetchTransitions() {
      return API.get('/v2/statetransition/list', {
        moduleName: this.module,
        stateFlowId: this.stateFlowId,
      }).then(({ error, data }) => {
        if (!error) this.stateTransitions = data.stateTransitionList || []
      })
    },

    serializeStateObj(obj) {
      let stateId = obj.id
      let stateHash = this.transitionStateMap
      let visitedTransitionIds = new Set()

      let anchors = obj.anchors.map(t => {
        let states = stateHash[t] || []

        if (!visitedTransitionIds.has(t)) {
          return states.includes(stateId) ? t : null
        } else {
          let isCycle = states[0] === stateId && states[1] === stateId
          return isCycle ? t : null
        }
      })

      let state = {
        stateId: stateId,
        x: obj.left,
        y: obj.top,
        anchors,
      }
      return state
    },

    serializeCanvas() {
      let { canvas, serializeStateObj } = this
      let canvasJson = JSON.stringify(canvas)
      let visitedStates = new Set()

      let data = {
        states: [],
        zoom: canvas.getZoom(),
        version: '0',
      }

      JSON.parse(canvasJson)
        .objects.filter(obj => obj.type === 'group' && obj.objType === 'state')
        .forEach(obj => {
          if (!visitedStates.has(obj.id)) {
            visitedStates.add(obj.id)
            data.states.push(serializeStateObj(obj))
          }
        })

      return data
    },

    saveDiagram() {
      this.onSaving()

      let data = this.serializeCanvas()
      return API.post(`/v2/stateflow/updateDiagram`, {
        id: this.stateFlow.id,
        stateFlowDiagram: data,
      }).then(({ error, data }) => {
        if (!error) {
          this.updateDiagram(data)
          this.onSaved()
        } else {
          this.onError()
        }
      })
    },

    autoSave: debounce(function() {
      if (this.deferAutoSave) {
        this.deferAutoSave = false
        this.$helpers.delay(3 * 1000).then(() => this.autoSave)
      } else {
        this.saveDiagram()
      }
    }, 2500),

    editState(state) {
      this.activeStateObj = cloneDeep(state)
      this.canShowTransitionPopup = false
      this.isNew = false
      this.canShowStatePopup = true
    },

    addState() {
      this.resetEditObjects()
      this.isNew = true
      this.activeStateObj = {}
      this.canShowStatePopup = true
    },

    onStateUpdate(state, message = null) {
      let index = this.availableStates.findIndex(
        s => String(s.id) === String(state.id)
      )

      if (!isEmpty(index)) {
        this.$set(this.availableStates, index, state)
      }

      this.onSaved(message)
      this.drawStateFlow()
    },

    onStateCreate(state) {
      this.availableStates.push(state)
      this.editState(state)
      this.onSaved()

      // force update ticketstatus so that state is available during
      // transition creation
      this.$store.dispatch('loadTicketStatus', {
        moduleName: this.module,
        forceUpdate: true,
      })
    },

    onSaving() {
      this.$refs['notification'].showSaving('Saving')
    },
    onSaved(message = null) {
      this.$refs['notification'].showSuccess(message || 'Saved')
    },
    onError() {
      this.$refs['notification'].showError('Error Occured')
    },

    resetEditObjects() {
      this.isNew = false
      this.selectedStateObj = null
      this.activeTransitionObj = null
      this.canShowStatePopup = false
      this.canShowTransitionPopup = false
      this.activeObj = null
    },
  },
}
</script>
