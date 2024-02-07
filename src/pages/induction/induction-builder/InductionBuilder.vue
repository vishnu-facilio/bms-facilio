<script>
import InspectionBuilder from 'pages/inspection/inspection-builder/InspectionBuilder'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
export default {
  extends: InspectionBuilder,
  name: 'InductionBuilder',
  props: {
    isV2: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      templateModuleName: 'inductionTemplate',
      templateDisplayName: 'Induction',
      responseModuleName: 'inductionResponse',
    }
  },
  methods: {
    redirectToSummary() {
      let { viewname } = this.$route.params
      if (this.isV2) {
        this.$emit('ruleEvent', {
          moduleName: this.templateModuleName,
          id: this.id,
          viewname,
        })
      } else {
        let { $route } = this
        let { params } = $route || {}
        let { id } = params || {}
        this.$emit('reloadlist')
        if (isWebTabsEnabled()) {
          let { name } =
            findRouteForModule('inductionTemplate', pageTypes.OVERVIEW) || {}
          name &&
            this.$router.push({
              name,
              params: {
                viewname: 'all',
                id,
              },
            })
        } else {
          this.$router
            .push({
              name: 'inductionTemplateSummary',
              params: {
                id,
              },
            })
            .catch(() => {})
        }
      }
    },
  },
}
</script>
