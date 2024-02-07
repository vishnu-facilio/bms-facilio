<script>
import InspectionTemplateList from 'pages/inspection/inspection-template/InspectionTemplateList'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  extends: InspectionTemplateList,
  name: 'SurveyTemplateList',
  computed: {
    createText() {
      return this.$t('qanda.template.new_survey')
    },
    moduleDisplayName() {
      return 'Survey Template'
    },
    pathPrefix() {
      return '/app/survey/template'
    },
    viewManagerRoute() {
      return `/app/survey/${this.moduleName}/viewmanager`
    },
  },
  methods: {
    openSummary(id) {
      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForModule(this.moduleName, pageTypes.OVERVIEW) || {}
        name &&
          this.$router.push({
            name,
            params: {
              viewname: this.currentView,
              id,
            },
            query: this.$route.query,
          })
      } else {
        this.$router.push({
          name: 'surveyTemplateSummary',

          params: { id, viewname: this.currentView },
          query: this.$route.query,
        })
      }
    },
    openList() {
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(this.moduleName, pageTypes.LIST) || {}
        name &&
          this.$router.push({
            name,
            params: {
              viewname: this.currentView,
            },
            query: this.$route.query,
          })
      } else {
        this.$router.push({
          name: 'surveyTemplateList',

          params: { viewname: this.currentView },
          query: this.$route.query,
        })
      }
    },
    openCreate() {
      if (isWebTabsEnabled()) {
        let { currentView } = this
        let { name } =
          findRouteForModule(this.moduleName, pageTypes.CREATE) || {}
        name &&
          this.$router.push({
            name,
            params: {
              viewname: currentView,
            },
            query: this.$route.query,
          })
      } else {
        this.$router.push({
          name: 'surveyTemplateCreate',
          params: { viewname: this.currentView },
          query: this.$route.query,
        })
      }
    },
    editRecord(record) {
      let { id } = record || {}
      let { moduleName } = this
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.EDIT)
        name && this.$router.push({ name, params: { id } })
      } else {
        this.$router.push({
          name: 'surveyTemplateEdit',
          params: { id },
        })
      }
    },
  },
}
</script>

<style></style>
