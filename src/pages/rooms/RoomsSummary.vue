<script>
  import Overview from 'PortalTenant/components/ModuleSummary'

  export default {
    extends: Overview,
    data() {
      return {}
    },
    computed: {
      moduleName() {
        return 'rooms'
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
