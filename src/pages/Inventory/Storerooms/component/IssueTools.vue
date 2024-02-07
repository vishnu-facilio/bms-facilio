<script>
import IssuesAndReturnsBase from 'src/pages/Inventory/Storerooms/component/IssuesAndReturnsBase.vue'
import { API } from '@facilio/api'
import { CustomModuleData } from 'pages/custom-module/CustomModuleData'
import { isEmpty } from '@facilio/utils/validation'
export default {
  name: 'IssueTools',
  extends: IssuesAndReturnsBase,
  computed: {
    moduleDisplayName() {
      return this.$t('common.workorder_actuals.work_order_tool')
    },
    moduleName() {
      return 'workorderTools'
    },
    additionalBtnList() {
      if (this.checkStoreRoomPermission()) {
        return [
          {
            label: this.$t('common.products.issue_to_person'),
            action: () => {
              this.showToolsIssue = true
            },
          },
          {
            label: this.$t('common._common.return_tools'),
            action: () => {
              this.showToolsReturn = true
            },
          },
        ]
      }
      return []
    },

    emptyStateBtnList() {
      if (this.checkStoreRoomPermission()) {
        return [
          {
            label: this.$t('common.inventory.issue_available_tools'),
            value: {
              lookupModuleName: 'tool',
              lookupModuleDisplayName: this.$t('common.header.tool'),
              getRecordDetails: async payload => {
                let { id, moduleName } = payload || {}
                let { workorder } = this
                let workOrderId = this.$getProperty(workorder, 'id')
                let queryParam = {
                  toolId: id,
                  workOrderId,
                }
                let { data, error } = await API.get(
                  '/v3/workorderTools/getWorkorderTool',
                  queryParam
                )
                if (error) {
                  this.$message.error(
                    error.message || this.$t('common._common.error_occured')
                  )
                } else {
                  let { workorderTools } = data || {}
                  let { toolType } = workorderTools || {}
                  let { isRotating } = toolType || {}
                  this.isRotatingType = isRotating
                  return new CustomModuleData({
                    ...workorderTools,
                    moduleName,
                  })
                }
              },
              getRecordList: async listParams => {
                let { filters } = listParams || {}
                let toolModuleName = 'tool'
                let { siteId } = this
                let params = {
                  siteId,
                  includeServingSite: true,
                  filters: JSON.stringify(filters),
                }
                let { list, error } = await API.fetchAll(toolModuleName, params)
                if (!error) {
                  let { length } = list
                  this.recordsCount = length
                  return list
                }
              },
              getRecordCount: () => {
                return this.recordsCount
              },
            },
          },
          {
            label: this.$t('common.inventory.issue_reserved_tools'),
            value: {
              lookupModuleName: 'inventoryReservation',
              viewname: 'all-tools',
              lookupModuleDisplayName: this.$t(
                'common.inventory.reserved_tools'
              ),
              getRecordList: async listParams => {
                let { filters } = listParams || {}
                let additionalFilters = {
                  ...(filters || {}),
                  storeRoom: {
                    operatorId: 36,
                    value: [`${this.details.id}`],
                  },
                  reservationStatus: {
                    operatorId: 10,
                    value: [`${3}`],
                  },
                  toolType: {
                    operatorId: 2,
                  },
                }
                let params = {
                  filters: JSON.stringify(additionalFilters),
                }
                let { list, error } = await API.fetchAll(
                  'inventoryReservation',
                  params
                )
                if (!error) {
                  let { length } = list
                  this.recordsCount = length
                  return list
                }
              },
              getRecordCount: () => {
                return this.recordsCount
              },
              getRecordDetails: async payload => {
                let { id, moduleName } = payload || {}
                this.inventoryReservationId = !isEmpty(id) ? id : null
                let queryParam = {
                  reservationId: id,
                }
                let { data, error } = await API.get(
                  '/v3/workorderTools/getWorkorderToolFromReservation',
                  queryParam
                )
                let { workorderTools } = data || {}
                let { toolType } = workorderTools || {}
                let { isRotating } = toolType || {}
                this.isRotatingType = isRotating
                if (!error) {
                  return new CustomModuleData({
                    ...workorderTools,
                    moduleName,
                  })
                }
              },
            },
          },
        ]
      }
      return []
    },
    typeName() {
      return this.$getProperty(
        this.issuedInventory,
        'itemType.name',
        this.$t('common.inventory.item')
      )
    },
  },
  methods: {},
}
</script>
