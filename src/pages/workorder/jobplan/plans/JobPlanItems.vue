<template>
  <div>
    <LineItemList
      v-bind="$attrs"
      ref="lineItemList"
      :config="listConfiguration"
      :moduleName="moduleName"
      :moduleDisplayName="moduleDisplayName"
      :additionalParams="additionalParams"
      :widgetDetails="widgetDetails"
      viewname="all"
      class="height-100"
    >
    </LineItemList>
  </div>
</template>
<script>
import LineItemList from 'src/components/page/widget/common/line-items/LineItemList.vue'
import JobPlanPlansCommon from 'src/pages/workorder/jobplan/plans/JobPlanPlansCommon'
export default {
  components: {
    LineItemList,
  },
  extends: JobPlanPlansCommon,
  props: ['details', 'widget'],
  computed: {
    moduleName() {
      return 'jobPlanItems'
    },
    moduleDisplayName() {
      return 'Job Plan Item'
    },
  },
}
</script>
