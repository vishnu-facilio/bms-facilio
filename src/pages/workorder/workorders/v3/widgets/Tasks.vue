<template>
  <!-- <div>
    <div
      v-for="(section, sectionIx) in workorder.taskSections"
      :key="sectionIx"
      class="white-bg-block mB20 pB40"
    >
      <div class="label-txt-black p20 border-bottom4 fw-bold">
        {{ section.name }}
      </div>

      <div v-for="(task, taskIx) in workorder.tasks[section.id]" :key="taskIx">
        {{ task.subject }}
      </div>
    </div>
  </div> -->

  <div>
    <div
      v-for="(woSection, woSectionIx) in workorder.taskSections"
      :key="woSectionIx"
      class="white-bg-block mB20 pB40"
    >
      <div class="label-txt-black p20 border-bottom4 fw-bold">
        {{ woSection.name }}
      </div>

      <div
        v-bind:class="{
          selected: selectedTask === task,
          closed: task.statusNewEnum !== 'OPEN',
        }"
        v-for="(task, taskIx) in workorder.tasks[woSection.id]"
        :key="taskIx"
        class="fc__sum__task__list"
      >
        <div style="cursor: pointer; padding-left: 5px;" class="width40px">
          <i
            v-if="task.statusNewEnum === 'OPEN'"
            class="fa fa-circle-thin"
            style="color: #ff3184; font-size: 28px;"
          ></i>
          <i
            v-else
            class="el-icon-circle-check"
            style="color: #ff3184; font-size: 28px;"
          ></i>
        </div>

        <div
          class="task-item width50"
          style="padding-left: 10px; position: relative;"
          @click="showTaskDetails(task)"
        >
          <el-input
            v-if="task.id === -1"
            style="width: 100%;"
            v-model="task.subject"
            type="text"
            :placeholder="$t('maintenance._workorder.write_a_task_name')"
          ></el-input>
          <div
            v-else
            :class="[
              { closedTask: task.statusNewEnum !== 'OPEN' },
              'label-txt-black pointer',
            ]"
            style="width: 100%; word-wrap: break-word;"
            type="text"
            placeholder
          >
            <span class="fc-id">#{{ task.uniqueId }}</span>
            {{ task.subject }}
          </div>
          <div
            v-if="task.id === -1 || (task.resource && task.resource.name)"
            class="pT5 inline-flex"
          ></div>
          <div></div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  name: 'Tasks',
  props: ['moduleName', 'details'],
  data() {
    return {
      showAddTask: false,
      selectedTask: {
        inputValidationRuleId: -1,
        inputValidation: null,
        safeLimitRuleId: -1,
        minSafeLimit: null,
        maxSafeLimit: null,
        options: [],
        taskType: -1,
        enableInput: false,
        inputType: 1,
        attachmentRequired: false,
        subject: '',
        selectedResourceName: '',
        inputValues: [],
      },
    }
  },
  computed: {
    currModuleName() {
      return 'workorder'
    },
    widgetTitle() {
      return 'Tasks'
    },
    workorder() {
      return this.details.workorder
    },
  },
  methods: {
    closeAddTask() {
      this.showAddTask = false
    },
    getSortedTaskList() {
      // let sorted = []
      // if (this.workorder.tasks) {
      //   let list = this.workorder.tasks
      //   if (this.selectedTaskResource > 0) {
      //     list = list.filter(
      //       task =>
      //         task.resource && task.resource.id === this.selectedTaskResource
      //     )
      //   }
      //   sorted.push({ sectionId: -1, taskList: list })
      // }
      // if (this.sections) {
      //   let seqList = Object.keys(this.sections).map(sectionId => ({
      //     secId: sectionId,
      //     sequence: this.taskList[sectionId][0].sequence,
      //   }))
      //   sorted.push(
      //     ...seqList
      //       .sort((a, b) => a.sequence - b.sequence)
      //       .map(obj => ({
      //         sectionId: obj.secId,
      //         taskList:
      //           this.selectedTaskResource > 0
      //             ? this.taskList[obj.secId].filter(
      //                 task =>
      //                   task.resource &&
      //                   task.resource.id === this.selectedTaskResource
      //               )
      //             : this.taskList[obj.secId],
      //       }))
      //   )
      // }
      // return sorted.filter(obj => obj.taskList.length > 0)
      return this.workorder.tasks
    },
  },
}
</script>
