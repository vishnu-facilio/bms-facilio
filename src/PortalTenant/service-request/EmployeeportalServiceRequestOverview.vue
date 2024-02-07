<script>
import ModuleOverview from 'PortalTenant/custom-module/EmployeeportalModuleOverview'

export default {
  extends: ModuleOverview,
  data() {
    return {
      notesModuleName: 'servicerequestsnotes',
      attachmentsModuleName: 'servicerequestsattachments',
      customClass: 'service-request-summary',
    }
  },
  computed: {
    mainFieldKey() {
      return 'subject'
    },
  },
}
</script>
