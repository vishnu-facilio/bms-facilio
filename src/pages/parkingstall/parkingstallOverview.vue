<script>
import Overview from 'PortalTenant/components/ModuleSummary'

export default {
  extends: Overview,
  data() {
    return {}
  },
  computed: {
    moduleName() {
      return 'parkingstall'
    },
    mainFieldKey() {
      return 'name'
    },
    isV3Api() {
      return true
    },
  },
}
</script>
