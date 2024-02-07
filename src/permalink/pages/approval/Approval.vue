<script>
import Transition from 'permalink/pages/transition/Transition.vue'

export default {
  extends: Transition,
  methods: {
    getTransitionParam(transition) {
      return { approvalTransitionId: transition.id }
    },
  },
}
</script>
