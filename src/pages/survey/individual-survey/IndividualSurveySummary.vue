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
        `/app/pdf/surveyPdf?id=${this.id}`
      )
    },
    liveFormBtnText() {
      return 'Attend Survey'
    },
    canShowLiveForm() {
      let { record, $account } = this
      let peopleId = this.$getProperty(record, 'people.id')
      let currUserPeopleId = this.$getProperty($account, 'user.peopleId')
      let responseStatus = this.$getProperty(record, 'responseStatus')
      return (
        record &&
        ![1, 4].includes(responseStatus) &&
        !this.$getProperty(record, 'parent.deleted') &&
        peopleId === currUserPeopleId
      )
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
          name: 'survey-live-form',
          params: { id },
        })
      }
    },
  },
}
</script>
