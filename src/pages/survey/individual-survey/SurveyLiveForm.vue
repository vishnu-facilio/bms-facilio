<script>
import InspectionLiveForm from 'pages/inspection/individual-inspection/InspectionLiveForm'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
export default {
  extends: InspectionLiveForm,
  data() {
    return {
      templateModuleName: 'surveyTemplate',
      templateDisplayName: 'Survey',
      responseModuleName: 'surveyResponse',
    }
  },
  methods: {
    redirectToSummary(data) {
      let { id } = data || {}
      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForModule(this.responseModuleName, pageTypes.OVERVIEW) || {}
        name &&
          this.$router.push({
            name,
            params: {
              viewname: 'all',
              id,
            },
          })
      } else {
        this.$router.push({
          name: 'individualSurveySummary',
          params: { id, viewname: 'all' },
        })
      }
    },
  },
}
</script>

<style></style>
