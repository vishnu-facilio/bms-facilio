<script>
import { CustomModuleData } from 'pages/custom-module/CustomModuleData'
import WorkorderPlannedInventory from 'src/pages/workorder/workorders/v3/widgets/plans/WorkorderPlannedInventory.vue'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
export default {
  extends: WorkorderPlannedInventory,
  computed: {
    moduleName() {
      return 'workOrderPlannedItems'
    },
    moduleDisplayName() {
      return this.$t('common.inventory._items')
    },
    getReservationQuantityMsg() {
      let { reservableLineItems, reservingLineItems } = this
      let msg = `${reservableLineItems.length} of ${reservingLineItems.length} items reservable`
      return msg
    },

    formConfig() {
      return {
        formType: 'POP_UP_FORM',
        modifyFieldPropsHook: field => {
          let { name } = field || {}
          if (name === 'itemType') {
            return { ...field, isDisabled: true }
          }
        },
      }
    },

    emptyStateBtnList() {
      return [
        {
          label: this.$t('common.inventory.select_item'),
          value: {
            lookupModuleName: 'itemTypes',
            lookupModuleDisplayName: this.$t('common.inventory._item_types'),
            getRecordDetails: async payload => {
              let { id, moduleName } = payload || {}
              let workOrderPlannedItem = {
                itemType: {
                  id: id,
                },
              }
              return new CustomModuleData({
                ...workOrderPlannedItem,
                moduleName,
              })
            },
          },
        },
      ]
    },
  },
  methods: {
    getNonRotatingItemTypes(itemTypes) {
      let nonRotatingItemTypes = (itemTypes || {}).filter(itemType => {
        let { rotating } = itemType || {}
        return !rotating
      })
      return nonRotatingItemTypes
    },
    inventoryName(val) {
      return this.$getProperty(val, 'itemType.name', '---')
    },

    async reservePlannedItems() {
      let { reservableLineItems } = this
      let plannedItems = (reservableLineItems || []).map(lineItem => ({
        id: this.$getProperty(lineItem, 'id'),
      }))
      let url = `v3/modules/bulkPatch/workOrderPlannedItems`
      let params = {
        data: {
          workOrderPlannedItems: plannedItems,
        },
        moduleName: 'workOrderPlannedItems',
        params: {
          reserve: true,
        },
      }
      if (!isEmpty(plannedItems)) {
        let { error } = await API.post(url, params)
        if (error) {
          this.$message.error(
            error.message || this.$t('common._common.error_occured')
          )
        } else {
          this.$message.success(this.$t('common._common.reserve_success'))
          this.showReservationSummary = false
          this.$refs['lineItemTable']?.refreshRecordList(true)
          this.$refs['lineItemTable']?.clearSelection()
        }
      }
    },

    async reserveLineItem(record) {
      this.recordIds = []
      this.recordIds.push(record.id)
      let queryParam = {
        recordIds: this.recordIds,
      }
      let { data, error } = await API.get(
        '/v3/workOrderPlannedItems/reserve',
        queryParam
      )
      if (error) {
        let { message } = error
        this.$message.error(message || this.$t('common._common.error_occured'))
        return {}
      } else {
        this.reservingLineItems = this.$getProperty(
          data,
          'workOrderPlannedItems'
        )
        this.setReservableLineItems(this.reservingLineItems)
        this.showReservationSummary = true
      }
    },

    async reserveMultipleLineItems() {
      let { selectedLineItems } = this
      this.recordIds = []
      for (let lineItem of selectedLineItems) {
        let id = this.$getProperty(lineItem, 'id')
        this.recordIds.push(id)
      }
      let queryParam = {
        recordIds: this.recordIds,
      }
      let { data, error } = await API.get(
        '/v3/workOrderPlannedItems/reserve',
        queryParam
      )
      if (error) {
        let { message } = error
        this.$message.error(message || this.$t('common._common.error_occured'))
        return {}
      } else {
        this.reservingLineItems = this.$getProperty(
          data,
          'workOrderPlannedItems'
        )
        this.setReservableLineItems(this.reservingLineItems)
        this.showReservationSummary = true
      }
    },
  },
}
</script>
