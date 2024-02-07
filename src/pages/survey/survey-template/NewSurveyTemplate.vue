<script>
import NewInspectionTemplate from 'pages/inspection/inspection-template/NewInspectionTemplate'
export default {
  extends: NewInspectionTemplate,
  name: 'NewSurveyTemplate',
  computed: {
    moduleName() {
      return 'surveyTemplate'
    },
  },
  methods: {
    redirectToSummary(id) {
      this.$router.push({
        name: 'surveyTemplateSummary',
        params: { id, viewname: 'all' },
      })
    },
    redirectToList() {
      this.$router.push({
        name: 'surveyTemplateList',
        params: { viewname: 'all' },
      })
    },
  },
}
</script>
