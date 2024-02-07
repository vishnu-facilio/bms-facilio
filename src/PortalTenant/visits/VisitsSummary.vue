<script>
import Overview from '../components/ModuleSummary'

export default {
  extends: Overview,
  data() {
    return {
      notesModuleName: 'newvisitorlognotes',
      attachmentsModuleName: 'newvisitorlogattachments',
      primaryFields: [
        'host',
        'checkInTime',
        'checkOutTime',
        'purposeOfVisit',
        'visitorType',
        'visitorName',
        'avatar',
      ],
    }
  },
  computed: {
    moduleName() {
      return 'visitorlog'
    },
    mainFieldKey() {
      return 'visitorName'
    },
    photoFieldName() {
      return 'avatarId'
    },
  },
}
</script>
