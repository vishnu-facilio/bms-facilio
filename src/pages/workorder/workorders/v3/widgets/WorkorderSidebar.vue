<template>
  <div>
    <WorkDuration :moduleName="moduleName" />

    <Resource :moduleName="moduleName" :details="details" />

    <Responsibility :moduleName="moduleName" :details="details" />

    <WorkorderDetails :moduleName="moduleName" :details="details" />
  </div>
</template>
<script>
import WorkDuration from 'src/pages/workorder/workorders/v3/widgets/WorkDuration.vue'
import Resource from 'src/pages/workorder/workorders/v3/widgets/Resource.vue'
import Responsibility from 'src/pages/workorder/workorders/v3/widgets/Responsibility.vue'
import WorkorderDetails from 'src/pages/workorder/workorders/v3/widgets/WorkorderDetails.vue'

export default {
  name: 'Sidebar',
  components: {
    WorkDuration,
    Resource,
    Responsibility,
    WorkorderDetails,
  },
  props: ['moduleName', 'details'],
  computed: {
    currModuleName() {
      return 'workorder'
    },
    widgetTitle() {
      return 'Sidebar'
    },
    workorder() {
      return this.details.workorder
    },
  },
}
</script>
