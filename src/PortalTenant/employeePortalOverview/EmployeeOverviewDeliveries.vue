<script>
import Overview from '../components/ModuleSummary'
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
      return this.$attrs.moduleName
    },
    mainFieldKey() {
      return 'name'
    },
  },
}
</script>
