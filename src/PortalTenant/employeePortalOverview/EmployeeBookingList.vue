<script>
import EmployeeModuleList from 'PortalTenant/employeePortalOverview/EmployeeModuleList'
import { isEmpty } from '@facilio/utils/validation'

export default {
  extends: EmployeeModuleList,
  data() {
    return {
      defaultColumnConfig: {
        fixedColumns: ['facility'],
        availableColumns: [],
      },
    }
  },
  computed: {
    nameColumn() {
      let { viewColumns } = this
      if (!isEmpty(viewColumns)) {
        return viewColumns.find(column => column.name === 'facility')
      }
      return null
    },
    idField() {
      return 'localId'
    },
  },
}
</script>

<style lang="scss">
.employee-portal-homepage {
  .el-table {
    height: calc(100vh - 130px);
    padding-bottom: 50px;
  }
}
</style>
