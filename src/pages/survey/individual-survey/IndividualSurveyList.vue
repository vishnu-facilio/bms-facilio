<script>
import IndividualInspectionList from 'pages/inspection/individual-inspection/IndividualInspectionList'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import { API } from '@facilio/api'

export default {
  extends: IndividualInspectionList,
  name: 'IndividualSurveyList',
  computed: {
    moduleDisplayName() {
      return 'Survey Response'
    },
    pathPrefix() {
      return '/app/survey/individual'
    },
    viewManagerRoute() {
      let { moduleName } = this
      return `/app/survey/${moduleName}/viewmanager`
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
          name: 'individualSurveySummary',

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
          name: 'individualSurveyList',

          params: { viewname: this.currentView },
          query: this.$route.query,
        })
      }
    },
    async deleteRecords(idList) {
      let value = await this.$dialog.confirm({
        title: this.$t(`qanda.response.delete_induction`),
        message: this.$t(`qanda.response.delete_induction_confirmation`),
        rbDanger: true,
        rbLabel: this.$t('common._common.delete'),
      })
      if (value) {
        let { error } = await API.deleteRecord(this.moduleName, idList)
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.loadResponseList()
          this.selectedRecordsObj = []
          this.selectedRecords = []
        }
      }
    },
  },
}
</script>
