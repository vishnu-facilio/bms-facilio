<script>
import VisitsSummary from 'pages/visitors/visits/VisitsSummary.vue'

export default {
  extends: VisitsSummary,
  computed: {
    listRouteName() {
      return 'invites-list'
    },
    primaryFields() {
      return [
        'host',
        'expectedCheckInTime',
        'expectedCheckOutTime',
        'visitorType',
        'visitorName',
      ]
    },
  },
}
</script>
