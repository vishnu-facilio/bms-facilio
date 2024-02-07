<script>
import EmployeeModuleList from 'src/PortalTenant/employeePortalOverview/EmployeeModuleList.vue'
import { findRouteForModule, pageTypes } from '@facilio/router'

export default {
  extends: EmployeeModuleList,
  methods: {
    editRecord(record) {
      let { moduleName } = this
      let route = findRouteForModule(moduleName, pageTypes.EDIT)
      if (route) {
        let { id, visitorType, formId } = record
        let { id: visitorTypeId } = visitorType || {}
        this.$router.push({
          name: route.name,
          params: { id },
          query: { id, visitorTypeId, formId },
        })
      }
    },
  },
}
</script>
