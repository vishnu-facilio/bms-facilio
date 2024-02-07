<script>
import CustomModuleList from 'pages/base-module-v2/ModuleList'
import { isEmpty } from '@facilio/utils/validation'
import { findRouteForModule, pageTypes } from '@facilio/router'

export default {
  extends: CustomModuleList,
  props: ['id'],
  watch: {
    id: {
      handler(newVal) {
        if (!isEmpty(newVal)) {
          this.selectedRow = { id: newVal }
          this.showBillSummary = true
        }
      },
      immediate: true,
    },
  },
  methods: {
    redirectToOverview(row) {
      let { moduleName, viewname } = this
      let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}

      name &&
        this.$router.push({
          name,
          params: {
            viewname,
            id: row.id,
          },
        })
    },
  },
}
</script>
