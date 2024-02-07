<script>
import { API } from '@facilio/api'
import Transition from './Transition'
import { isEmpty } from '@facilio/utils/validation'

export default {
  extends: Transition,
  props: ['moduleName', 'recordId', 'transitionId'],
  created() {
    if (this.hasProps) {
      this.loadOrgInfo()
      this.loadData()
    } else {
      this.isTransitionInProgress = false
      this.isTransitionComplete = false
    }
  },
  computed: {
    hasProps() {
      return this.moduleName && this.recordId && this.transitionId
    },
    canShowTransitionBlock() {
      return this.hasProps && !this.hasTransitionFailed
    },
  },
  methods: {
    loadData() {
      this.details = {
        recordId: this.recordId,
        moduleName: this.moduleName,
      }

      this.loadTransition()
    },

    loadTransition() {
      return API.post('v2/statetransition/view', {
        stateTransitionId: this.transitionId,
      }).then(({ error, data }) => {
        if (error) {
          this.setFailureState('Link Expired', 'TRANSITION_FETCH_FAILED')
        } else {
          this.transition = !isEmpty(data && data.transition)
            ? data.transition
            : null
          return this.loadModule().then(() => {
            return !isEmpty(this.transition.formId)
              ? this.showTransitionForm()
              : this.transitionToState()
          })
        }
      })
    },
  },
}
</script>
