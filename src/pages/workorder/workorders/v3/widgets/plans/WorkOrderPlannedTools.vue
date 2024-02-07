<script>
import { CustomModuleData } from 'pages/custom-module/CustomModuleData'
import WorkorderPlannedInventory from 'src/pages/workorder/workorders/v3/widgets/plans/WorkorderPlannedInventory.vue'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
export default {
  extends: WorkorderPlannedInventory,
  computed: {
    moduleName() {
      return 'workOrderPlannedTools'
    },
    moduleDisplayName() {
      return this.$t('common.inventory._tools')
    },
    getReservationQuantityMsg() {
      let { reservableLineItems, reservingLineItems } = this
      let msg = `${reservableLineItems.length} of ${reservingLineItems.length} tools reservable`
      return msg
    },

    formConfig() {
      return {
        formType: 'POP_UP_FORM',
        modifyFieldPropsHook: field => {
          let { name } = field || {}
          if (name === 'toolType') {
            return { ...field, isDisabled: true }
          }
        },
      }
    },

    emptyStateBtnList() {
      return [
        {
          label: this.$t('common.inventory.select_tool'),
          value: {
            lookupModuleName: 'toolTypes',
            lookupModuleDisplayName: this.$t('common.inventory._tool_types'),
            getRecordDetails: async payload => {
              let { id, moduleName } = payload || {}
              let workOrderPlannedTool = {
                toolType: {
                  id: id,
                },
              }

              return new CustomModuleData({
                ...workOrderPlannedTool,
                moduleName,
              })
            },
          },
        },
      ]
    },
  },
  methods: {
    // getNonRotatingItemTypes() {
    //   return
    // },
    inventoryName(val) {
      return this.$getProperty(val, 'toolType.name', '---')
    },

    async reservePlannedItems() {
      let { reservableLineItems } = this
      let plannedTools = (reservableLineItems || []).map(lineItem => ({
        id: this.$getProperty(lineItem, 'id'),
      }))
      let url = `v3/modules/bulkPatch/workOrderPlannedTools`
      let params = {
        data: {
          workOrderPlannedTools: plannedTools,
        },
        moduleName: 'workOrderPlannedTools',
        params: {
          reserve: true,
        },
      }
      if (!isEmpty(plannedTools)) {
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
        '/v3/workOrderPlannedTools/reserve',
        queryParam
      )
      if (error) {
        let { message } = error
        this.$message.error(message || this.$t('common._common.error_occured'))
        return {}
      } else {
        this.reservingLineItems = this.$getProperty(
          data,
          'workOrderPlannedTools'
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
        '/v3/workOrderPlannedTools/reserve',
        queryParam
      )
      if (error) {
        let { message } = error
        this.$message.error(message || this.$t('common._common.error_occured'))
        return {}
      } else {
        this.reservingLineItems = this.$getProperty(
          data,
          'workOrderPlannedTools'
        )
        this.setReservableLineItems(this.reservingLineItems)
        this.showReservationSummary = true
      }
    },
  },
}
</script>
