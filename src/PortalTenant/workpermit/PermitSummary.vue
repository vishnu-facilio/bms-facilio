<script>
import Overview from '../components/ModuleSummary'

export default {
  extends: Overview,
  data() {
    return {
      notesModuleName: 'workpermitnotes',
      attachmentsModuleName: 'workpermitattachments',
    }
  },
  computed: {
    moduleName() {
      return 'workpermit'
    },
    mainFieldKey() {
      return 'name'
    },
    idFieldKey() {
      return 'localId'
    },
  },
}
</script>
