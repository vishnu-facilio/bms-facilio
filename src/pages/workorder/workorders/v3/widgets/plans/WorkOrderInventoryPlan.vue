<template>
  <el-tabs v-model="moduleName">
    <el-tab-pane
      :label="$t('common.header.items')"
      name="workOrderPlannedItems"
    >
      <WorkOrderPlannedItems
        v-if="moduleName == 'workOrderPlannedItems'"
        v-bind="$attrs"
        :workOrderId="details.id"
        :workorder="workorder"
        :widget="widget"
        :disableActionableUIElements="disableActionableUIElements"
        class="height-100"
      >
      </WorkOrderPlannedItems>
    </el-tab-pane>
    <el-tab-pane
      :label="$t('common.header.tools')"
      name="workOrderPlannedTools"
    >
      <WorkOrderPlannedTools
        v-bind="$attrs"
        v-if="moduleName == 'workOrderPlannedTools'"
        :workOrderId="details.id"
        :workorder="workorder"
        :widget="widget"
        :disableActionableUIElements="disableActionableUIElements"
        class="height-100"
      >
      </WorkOrderPlannedTools>
    </el-tab-pane>
    <el-tab-pane
      :label="$t('common.products.services')"
      name="workOrderPlannedServices"
    >
      <WorkOrderPlannedServices
        v-bind="$attrs"
        v-if="moduleName == 'workOrderPlannedServices'"
        :workOrderId="details.id"
        :workorder="workorder"
        :widget="widget"
        :disableActionableUIElements="disableActionableUIElements"
        class="height-100"
      >
      </WorkOrderPlannedServices>
    </el-tab-pane>
    <el-tab-pane
      :label="$t('common.header.labours')"
      name="workOrderPlannedLabours"
    >
      <WorkOrderPlannedLabours
        v-if="moduleName == 'workOrderPlannedLabours'"
        v-bind="$attrs"
        :workOrderId="details.id"
        :resizeWidget="resizeWidget"
        :workorder="workorder"
        :widget="widget"
        :disableActionableUIElements="disableActionableUIElements"
        class="height-100"
      >
      </WorkOrderPlannedLabours>
    </el-tab-pane>
  </el-tabs>
</template>
<script>
import WorkOrderPlannedItems from './WorkOrderPlannedItems'
import WorkOrderPlannedTools from './WorkOrderPlannedTools'
import WorkOrderPlannedServices from './WorkOrderPlannedServices'
import WorkOrderPlannedLabours from './WorkOrderPlannedLabours.vue'
export default {
  props: ['details', 'widget'],
  components: {
    WorkOrderPlannedLabours,
    WorkOrderPlannedItems,
    WorkOrderPlannedTools,
    WorkOrderPlannedServices,
  },
  data() {
    return {
      moduleName: 'workOrderPlannedItems',
    }
  },
  computed: {
    workOrderSettings() {
      let { details } = this
      let { workOrderSettings = {} } = details || {}
      return workOrderSettings
    },
    disableActionableUIElements() {
      let { workOrderSettings } = this
      let { inventoryPlaning } = workOrderSettings || {}
      let { allowed: canDoActionsOnPlans = true } = inventoryPlaning || {}
      return !canDoActionsOnPlans
    },
    workorder() {
      let { workorder } = this.details || {}
      return workorder
    },
  },
}
</script>
