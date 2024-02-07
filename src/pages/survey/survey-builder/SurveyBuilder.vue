<script>
import InspectionBuilder from 'pages/inspection/inspection-builder/InspectionBuilder'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
export default {
  extends: InspectionBuilder,
  data() {
    return {
      templateModuleName: 'surveyTemplate',
      templateDisplayName: 'Survey',
      responseModuleName: 'surveyResponse',
    }
  },
  methods: {
    redirectToSummary() {
      let { $route } = this
      let { params } = $route || {}
      let { id } = params || {}
      this.$emit('reloadlist')
      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForModule('surveyTemplate', pageTypes.OVERVIEW) || {}
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
          name: 'surveyTemplate',
          moduleName: this.moduleName,
        })
      }
    },
  },
}
</script>
