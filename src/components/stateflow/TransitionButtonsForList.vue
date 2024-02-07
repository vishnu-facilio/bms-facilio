<script>
import TransitionButtons from './TransitionButtons'

export default {
  extends: TransitionButtons,
  name: 'TransitionButtonsForList',
  props: ['stateFlowList', 'primaryBtnClass', 'secondaryBtnClass'],
  computed: {
    stateFlows() {
      return this.stateFlowList
    },
  },
  methods: {
    fetchAvailableStates() {
      let { record, stateFlows, $getProperty } = this
      let stateFlowId = $getProperty(record, 'stateFlowId')
      let moduleStateId = $getProperty(record, 'moduleState.id')
      let evaluatedTransitionIds = $getProperty(
        record,
        'evaluatedTransitionIds'
      )
      this.$emit('currentState', moduleStateId)
      let stateTran = stateFlows[`${stateFlowId}_${moduleStateId}`] || []

      this.stateTransitions = [...stateTran]
        .sort((a, b) => a.buttonType - b.buttonType)
        .filter(({ id }) => (evaluatedTransitionIds || []).includes(id))
    },
  },
}
</script>
