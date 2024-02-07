<script>
import Summary from '../visits/VisitsSummary'
export default {
  extends: Summary,
  data() {
    return {
      primaryFields: [
        'host',
        'expectedCheckInTime',
        'expectedCheckOutTime',
        'purposeOfVisit',
        'visitorType',
        'visitorName',
      ],
    }
  },
  computed: {
    moduleName() {
      return 'invitevisitor'
    },
  },
}
</script>
