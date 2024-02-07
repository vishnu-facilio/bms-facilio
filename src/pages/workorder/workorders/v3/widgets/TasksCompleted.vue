<template>
  <div
    class="p30 white-bg-block nws-chart-container height212px width100"
    v-if="taskChartData"
  >
    <f-mini-chart
      class="tasks-completed-dial"
      :type="'semidoughnut'"
      :data="taskChartData"
    ></f-mini-chart>
  </div>
</template>
<script>
import FMiniChart from 'minicharts/components/FMiniChart'
export default {
  name: 'TasksCompleted',
  props: ['moduleName', 'details'],
  components: {
    FMiniChart,
  },
  mounted() {
    this.taskChartData = {
      value:
        this.details.workorder.noOfTasks > -1
          ? this.details.workorder.noOfTasks
          : 0,
      currentValue:
        this.details.workorder.noOfClosedTasks > -1
          ? this.details.workorder.noOfClosedTasks
          : 0,
      color: '#9c5fb8,#f87a60',
      unit: '',
      centerText: [
        {
          label: this.$t('maintenance._workorder.completed'),
        },
        {
          label:
            this.$t('maintenance._workorder.of') +
            ' ' +
            (this.details.workorder.noOfTasks > -1
              ? this.details.workorder.noOfTasks
              : 0) +
            ' ' +
            this.$t('maintenance._workorder.tasks'),
        },
      ],
    }
  },
  data() {
    return {
      taskChartData: null,
    }
  },

  computed: {
    currModuleName() {
      return 'workorder'
    },
    widgetTitle() {
      return 'Tasks Completed'
    },
  },
}
</script>

<style>
.tasks-completed-dial {
  margin: auto;
  height: 80%;
}
</style>
