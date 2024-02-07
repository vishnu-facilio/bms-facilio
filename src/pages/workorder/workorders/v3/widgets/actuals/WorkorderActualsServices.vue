<template>
  <div>
    <LineItemList
      v-bind="$attrs"
      ref="lineItemList"
      :config="listConfiguration"
      :moduleName="moduleName"
      :moduleDisplayName="moduleDisplayName"
      :widgetDetails="widgetDetails"
      :additionalParams="additionalParams"
      @onCreateOrUpdate="refreshData"
      @clickedActionBtn="btnValue"
      viewname="all"
    >
    </LineItemList>
  </div>
</template>
<script>
import LineItemList from 'src/components/page/widget/common/line-items/LineItemList.vue'
import WorkorderActualsCommon from './WorkorderActualsCommon'

export default {
  props: ['widget', 'details', 'resizeWidget', 'module'],
  extends: WorkorderActualsCommon,
  components: { LineItemList },
  computed: {
    moduleDisplayName() {
      return this.$t('common.workorder_actuals.work_order_service')
    },
    moduleName() {
      return 'workorderService'
    },
  },
}
</script>
