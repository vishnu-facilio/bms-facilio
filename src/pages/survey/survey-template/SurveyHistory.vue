<script>
import InspectionHistory from 'pages/inspection/inspection-template/InspectionHistory'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  extends: InspectionHistory,
  name: 'SurveyHistory',
  computed: {
    moduleName() {
      return 'surveyResponse'
    },
    moduleDisplayName() {
      return 'Survey'
    },
    moduleHeaderName() {
      return 'Survey'
    },
  },
  methods: {
    redirectToOverview(record) {
      let { id } = record || {}
      let { moduleName } = this
      let route
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW)
        if (name)
          route = this.$router.resolve({
            name,
            params: { id, viewname: 'all' },
          })
      } else {
        route = this.$router.resolve({
          name: 'individualSurveySummary',
          params: { id, viewname: 'all' },
        })
      }
      route && window.open(route.href, '_blank')
    },
  },
}
</script>
