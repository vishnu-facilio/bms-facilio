<template>
  <div
    class="p20 white-bg-block mB20"
    v-if="taskChartData && taskChartData.value && taskChartData.value > 0"
  >
    <el-row>
      <el-col
        :span="taskResources.length > 1 && taskResources.length <= 30 ? 14 : 15"
        class="mT10"
      >
        <div class="fc__wo__task__bar fc__wo__task__bar-stacked">
          <span
            class="fc__task__gradient__bar"
            :data="taskChartData"
            :style="
              'width:' +
                (taskChartData.currentValue / taskChartData.value) * 100 +
                '%'
            "
          ></span>
        </div>
      </el-col>
      <el-col :span="3" class="mT15">
        <div class="flex-middle justify-content-center">
          <div class="green-txt-12">
            {{ taskChartData.currentValue }}
          </div>
          <div class="fc-grey-text12 pL3">/ {{ taskChartData.value }}</div>
        </div>
      </el-col>
      <el-col :span="5" class="text-right">
        <el-button
          type="button"
          class="fc-pink-border-btn"
          @click="closeAllTask"
          v-if="!(taskChartData.currentValue === taskChartData.value)"
        >
          {{
            selectedTaskResource
              ? $t('maintenance._workorder.close_filtered_task')
              : $t('maintenance._workorder.close_all_task')
          }}
        </el-button>
        <div class="pT15 green-txt-13" v-else>
          {{ $t('maintenance._workorder.all_task_closed') }}
        </div>
      </el-col>
      <el-col
        :span="1"
        class="flRight mT15 pL20"
        v-if="taskResources.length > 1 && taskResources.length <= 30"
      >
        <div
          class="pointer user-select-none"
          @click="showTaskResourceFilter = !showTaskResourceFilter"
        >
          <span class="rotate90">
            <img src="~statics/icons/equalization.svg" style="width: 15px;" />
          </span>
        </div>
      </el-col>
    </el-row>
  </div>
</template>
<script>
import workorderMixin from 'pages/workorder/workorders/v1/mixins/workorderHelper'
export default {
  name: 'WorkorderProgress',
  props: ['moduleName', 'details'],
  mixins: [workorderMixin],
  data() {
    return {
      taskChartData: null,
      taskList: {},
    }
  },
  created() {
    if (this.workorder) {
      this.taskChartData = {
        value: this.workorder.noOfTasks > -1 ? this.workorder.noOfTasks : 0,
        currentValue:
          this.workorder.noOfClosedTasks > -1
            ? this.workorder.noOfClosedTasks
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
              (this.workorder.noOfTasks > -1 ? this.workorder.noOfTasks : 0) +
              ' ' +
              this.$t('maintenance._workorder.tasks'),
          },
        ],
      }
    }
  },
  computed: {
    currModuleName() {
      return 'workorder'
    },
    widgetTitle() {
      return 'Workorder Progress'
    },
    workorder() {
      return this.details.workorder
    },
  },
  methods: {
    taskResources() {
      let obj = {}
      if (this.taskList) {
        for (let idx in this.taskList) {
          for (let task of this.taskList[idx]) {
            if (task.resource && task.resource.id > 0) {
              obj[task.resource.id] = task.resource
            }
          }
        }
      }
      return Object.values(obj)
    },
  },
}
</script>
