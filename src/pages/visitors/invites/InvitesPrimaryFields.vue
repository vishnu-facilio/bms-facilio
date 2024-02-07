<script>
import VisitsPrimaryFields from 'pages/visitors/visits/VisitsPrimaryFields.vue'
export default {
  extends: VisitsPrimaryFields,
  computed: {
    primaryFields() {
      return {
        host: 'Host',
        visitorType: 'Type',
        expectedCheckInTime: 'Expected Check In',
        expectedCheckOutTime: 'Expected Check Out',
      }
    },
  },
}
</script>
