<script>
import Overview from 'PortalTenant/components/ModuleSummary'

export default {
  extends: Overview,
  data() {
    return {}
  },
  computed: {
    moduleName() {
      return 'deliveryArea'
    },
    mainFieldKey() {
      return 'name'
    },
  },
}
</script>
