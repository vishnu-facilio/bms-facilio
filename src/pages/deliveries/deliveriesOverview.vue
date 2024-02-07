<script>
import Overview from 'PortalTenant/components/ModuleSummary'

export default {
  extends: Overview,
  data() {
    return {
      notesModuleName: 'deliverynotes',
      attachmentsModuleName: 'deliveryattachments',
    }
  },
  computed: {
    moduleName() {
      return 'deliveries'
    },
    mainFieldKey() {
      return 'name'
    },
  },
}
</script>
