<template>
  <CommonPrecautionList
    :details="workorder"
    moduleName="workorder"
    :staticWidgetHeight="'270'"
  ></CommonPrecautionList>
</template>
<script>
import CommonPrecautionList from 'src/components/page/widget/common/CommonPrecautionList.vue'

export default {
  name: 'Resource',
  components: {
    CommonPrecautionList,
  },
  props: ['moduleName', 'details'],
  computed: {
    currModuleName() {
      return 'workorder'
    },
    widgetTitle() {
      return 'Resource'
    },
    workorder() {
      return this.details.workorder
    },
  },
}
</script>
