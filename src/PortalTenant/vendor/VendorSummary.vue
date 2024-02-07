<script>
import Overview from '../components/ModuleSummary'

export default {
  extends: Overview,
  data() {
    return {
      notesModuleName: 'vendorsNotes',
      attachmentsModuleName: 'vendorsAttachments',
      customClass: 'fc-portal-vendor-summary ',
    }
  },
  computed: {
    moduleName() {
      return 'vendors'
    },
  },
}
</script>
<style lang="scss">
.fc-portal-vendor-summary .fpage-tabs > .el-tabs__header .el-tabs__nav-wrap {
  padding-left: 50px;
}
</style>
