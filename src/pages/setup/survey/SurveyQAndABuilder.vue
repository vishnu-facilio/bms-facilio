<script>
import InspectionBuilder from 'pages/inspection/inspection-builder/InspectionBuilder'
import { isEmpty } from '@facilio/utils/validation'
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
      templateDisplayName: 'Survey Builder',
      responseModuleName: 'surveyResponse',
    }
  },
  computed: {
    id() {
      let { $route } = this
      let { params } = $route || {}
      let { id } = params || {}
      return Number(id)
    },
    isQuestionsEmpty() {
      let pages = this.$refs['survey-builder'].pages
      if (!isEmpty(pages)) {
        let { questions } = pages[0] || {}
        return isEmpty(questions)
      }
      return false
    },
  },
  methods: {
    redirectToSummary() {
      let { isQuestionsEmpty } = this
      if (!isQuestionsEmpty) {
        let { $route } = this
        let { params } = $route || {}
        let { id } = params || {}
        this.$emit('reloadlist')
        this.$router.push({
          name: 'survey.template',
          moduleName: this.moduleName,
        })
      } else {
        this.$message.error('You must atleast have one question in survey')
      }
    },
  },
}
</script>
