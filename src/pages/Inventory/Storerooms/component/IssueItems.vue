<script>
import IssuesAndReturnsBase from 'src/pages/Inventory/Storerooms/component/IssuesAndReturnsBase.vue'
import { API } from '@facilio/api'
import { CustomModuleData } from 'pages/custom-module/CustomModuleData'
import { isEmpty } from '@facilio/utils/validation'
export default {
  name: 'IssueItems',
  extends: IssuesAndReturnsBase,
  computed: {
    moduleDisplayName() {
      return this.$t('common.workorder_actuals.work_order_item')
    },
    moduleName() {
      return 'workorderItem'
    },

    emptyStateBtnList() {
      if (this.checkStoreRoomPermission()) {
        return [
          {
            label: this.$t('common.inventory.issue_available_items'),
            value: {
              lookupModuleName: 'item',
              lookupModuleDisplayName: this.$t('common.inventory.item'),
              getRecordDetails: async payload => {
                let { id, moduleName } = payload || {}
                let { workorder } = this
                let workOrderId = this.$getProperty(workorder, 'id')
                let queryParam = {
                  itemId: id,
                  workOrderId,
                }
                let { data, error } = await API.get(
                  '/v3/workOrderItem/getWorkorderItem',
                  queryParam
                )
                if (error) {
                  this.$message.error(
                    error.message || this.$t('common._common.error_occured')
                  )
                } else {
                  let { workorderItem } = data || {}
                  let { itemType } = workorderItem || {}
                  let { isRotating } = itemType || {}
                  this.isRotatingType = isRotating
                  return new CustomModuleData({
                    ...workorderItem,
                    moduleName,
                  })
                }
              },
              getRecordList: async listParams => {
                let { filters } = listParams || {}
                let itemModuleName = 'item'
                let { siteId } = this
                let params = {
                  siteId,
                  includeServingSite: true,
                  filters: JSON.stringify(filters),
                }
                let { list, error } = await API.fetchAll(itemModuleName, params)
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
            label: this.$t('common.inventory.issue_reserved_items'),
            value: {
              lookupModuleName: 'inventoryReservation',
              lookupModuleDisplayName: this.$t(
                'common.inventory.reserved_items'
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
                  itemType: {
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
                  '/v3/workOrderItem/getWorkorderItemFromReservation',
                  queryParam
                )
                let { workorderItem } = data || {}
                let { itemType } = workorderItem || {}
                let { isRotating } = itemType || {}
                this.isRotatingType = isRotating
                if (!error) {
                  return new CustomModuleData({
                    ...workorderItem,
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
