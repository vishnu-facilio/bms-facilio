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
      templateModuleName: 'inductionTemplate',
      templateDisplayName: 'Induction',
      responseModuleName: 'inductionResponse',
    }
  },
  methods: {
    redirectToSummary(data) {
      let { viewname } = this.$route.params
      if (this.isV2) {
        //check in extended component for this variable
        this.$emit('ruleEvent', {
          moduleName: this.responseModuleName,
          id: this.responseId,
          viewname,
        })
      } else {
        let { id } = data || {}
        if (isWebTabsEnabled()) {
          let { name } =
            findRouteForModule(this.responseModuleName, pageTypes.OVERVIEW) ||
            {}
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
            name: 'individualInductionSummary',
            params: { id, viewname: 'all' },
          })
        }
      }
    },
  },
}
</script>

<style></style>
