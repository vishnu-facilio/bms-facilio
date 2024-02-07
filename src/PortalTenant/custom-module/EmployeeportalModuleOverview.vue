<script>
import Overview from 'src/PortalTenant/components/EmployeeportalModuleSummary.vue'
export default {
  extends: Overview,
  data() {
    return {
      notesModuleName: 'cmdnotes',
      attachmentsModuleName: 'cmdattachments',
    }
  },
  computed: {
    moduleName() {
      return this.$attrs.moduleName
    },
    mainFieldKey() {
      return 'name'
    },
  },
}
</script>
