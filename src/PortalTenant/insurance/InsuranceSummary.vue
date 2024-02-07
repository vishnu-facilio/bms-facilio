<script>
import Overview from '../components/ModuleSummary'

export default {
  extends: Overview,
  data() {
    return {
      notesModuleName: 'insurancenotes',
      attachmentsModuleName: 'insuranceattachments',
    }
  },
  computed: {
    moduleName() {
      return 'insurance'
    },
    mainFieldKey() {
      return 'companyName'
    },
  },
}
</script>
