<script>
import { API } from '@facilio/api'
import { eventBus } from '@/page/widget/utils/eventBus'
export default {
  name: 'WorkOrderPlans',
  computed: {
    additionalParams() {
      let { currencyCode, exchangeRate } = this.workorder || {}
      return {
        workOrder: {
          id: this.workOrderId,
        },
        currencyCode,
        exchangeRate,
      }
    },
    filters() {
      return {
        workOrder: {
          operatorId: 36,
          value: [this.workOrderId + ''],
        },
      }
    },
    getLabel() {
      let { moduleName } = this
      if (moduleName === 'workOrderPlannedItems') {
        return this.$t('common.inventory.select_item')
      } else if (moduleName === 'workOrderPlannedTools') {
        return this.$t('common.inventory.select_tool')
      } else if (moduleName === 'workOrderPlannedServices') {
        return this.$t('common.inventory.select_service')
      }
      return ''
    },
  },
  methods: {
    reloadCost() {
      eventBus.$emit('loadCost')
    },
    selectedRecords(selectedList) {
      this.selectedLineItems = selectedList
    },

    async deleteLineItems() {
      let { selectedLineItems } = this

      this.recordIds = []
      selectedLineItems.forEach(lineItem => {
        let id = this.$getProperty(lineItem, 'id')
        this.recordIds.push(id)
      })
      let { error } = await API.deleteRecord(this.moduleName, this.recordIds)
      if (error) {
        let { message } = error
        this.$message.error(message || this.$t('common._common.error_occured'))
        return {}
      } else {
        let { moduleDisplayName } = this
        this.$message.success(
          `${moduleDisplayName} ${this.$t('custommodules.list.delete_success')}`
        )
        this.$refs['lineItemTable']?.refreshRecordList(true)
        this.$refs['lineItemTable']?.clearSelection()
      }
    },
  },
}
</script>
