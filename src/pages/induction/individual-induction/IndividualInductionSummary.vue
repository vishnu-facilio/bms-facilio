<script>
import IndividualInspectionList from 'pages/inspection/individual-inspection/IndividualInspectionSummary'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  extends: IndividualInspectionList,
  computed: {
    pdfUrl() {
      return (
        window.location.protocol +
        '//' +
        window.location.host +
        `/app/pdf/inductionPdf?id=${this.id}`
      )
    },
    liveFormBtnText() {
      return 'Attend Induction'
    },
    canShowLiveForm() {
      let { record, $account } = this
      let peopleId = this.$getProperty(record, 'people.id')
      let currUserPeopleId = this.$getProperty($account, 'user.peopleId')
      let responseStatus = this.$getProperty(record, 'responseStatus')
      let canEditRecord = this.$getProperty(this, 'canEditRecord', true)
      let returnVal =
        record &&
        ![1, 4].includes(responseStatus) &&
        !this.$getProperty(record, 'parent.deleted') &&
        peopleId === currUserPeopleId &&
        canEditRecord

      return returnVal
    },
  },
  methods: {
    openLiveForm() {
      let { record, moduleName } = this
      let id = this.$getProperty(record, 'id', '')
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.EDIT) || {}
        name &&
          this.$router.push({
            name,
            params: {
              id,
            },
          })
      } else {
        this.$router.push({
          name: 'induction-live-form',
          params: { id },
        })
      }
    },
  },
}
</script>
