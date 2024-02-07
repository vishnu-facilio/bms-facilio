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
  name: 'IndividualInductionList',
  computed: {
    moduleDisplayName() {
      return 'Induction Response'
    },
    pathPrefix() {
      return '/app/induction/individual'
    },
    viewManagerRoute() {
      let { moduleName } = this
      return `/app/induction/${moduleName}/viewmanager`
    },
  },
  methods: {
    openSummary(id) {
      let { viewname } = this
      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForModule(this.moduleName, pageTypes.OVERVIEW) || {}
        name &&
          this.$router.push({
            name,
            params: {
              viewname,
              id,
            },
            query: this.$route.query,
          })
      } else {
        this.$router.push({
          name: 'individualInductionSummary',

          params: { id, viewname },
          query: this.$route.query,
        })
      }
    },
    openList() {
      let { viewname } = this
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(this.moduleName, pageTypes.LIST) || {}
        name &&
          this.$router.push({
            name,
            params: {
              viewname,
            },
            query: this.$route.query,
          })
      } else {
        this.$router.push({
          name: 'individualInductionList',

          params: { viewname },
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
          await this.refreshRecordDetails(true)
          this.$message.success(
            this.$t(`qanda.response.induction_multiple_success_delete`)
          )
          this.selectedRecordsObj = []
          this.selectedRecords = []
        }
      }
    },
  },
}
</script>
