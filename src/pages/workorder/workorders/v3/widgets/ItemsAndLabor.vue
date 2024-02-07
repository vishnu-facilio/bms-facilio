<template>
  <Inventory-summary
    :workorder="workorder"
    :openWorkorderId="workorder.id"
    :showLabour="true"
    :canDisable="canDisable"
  ></Inventory-summary>
</template>

<script>
import InventorySummary from 'pages/workorder/workorders/v1/inventory'
import { isEmpty } from '@facilio/utils/validation'
import { mapGetters } from 'vuex'
export default {
  name: 'ItemsAndLabor',
  components: {
    InventorySummary,
  },
  props: ['moduleName', 'details'],
  computed: {
    ...mapGetters(['getApprovalStatus']),
    currModuleName() {
      return 'workorder'
    },
    widgetTitle() {
      return 'Items & Labor'
    },
    workorder() {
      return this.details.workorder
    },
    canDisable() {
      return this.isApprovalEnabled && !this.isRequestedState
    },
    isRequestedState() {
      let { workorder } = this
      let { approvalStatus } = workorder || {}

      if (isEmpty(approvalStatus)) {
        return false
      } else {
        let statusObj = this.getApprovalStatus(approvalStatus.id)
        return this.$getProperty(statusObj, 'requestedState', false)
      }
    },
    isApprovalEnabled() {
      let { workorder = {} } = this
      let { approvalFlowId, approvalStatus } = workorder || {}
      return !isEmpty(approvalFlowId) && !isEmpty(approvalStatus)
    },
  },
}
</script>
