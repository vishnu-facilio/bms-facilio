<script>
import InspectionTemplateSummary from 'pages/inspection/inspection-template/InspectionTemplateSummary'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  extends: InspectionTemplateSummary,
  name: 'InductionTemplateSummary',
  computed: {
    moduleDisplayName() {
      return 'Induction'
    },
  },
  methods: {
    openQAndABuilder() {
      if (isWebTabsEnabled()) {
        this.$router.push({
          path: 'builder',
          query: {
            pageNo: 1,
          },
        })
      } else {
        this.$router.push({
          name: 'induction-builder',
          query: {
            pageNo: 1,
          },
        })
      }
    },
    editRecord() {
      let { record } = this || {}
      let { id } = record || {}
      let { moduleName } = this
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.EDIT)
        name && this.$router.push({ name, params: { id } })
      } else {
        this.$router.push({
          name: 'inductionTemplateEdit',
          params: { id },
        })
      }
    },
    async executeTrigger() {
      this.canShowExecuteWizard = true
    },
  },
}
</script>
