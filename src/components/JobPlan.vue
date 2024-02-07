<template>
  <el-dialog
    title="TASK TEMPLATES"
    v-if="showTaskList"
    :visible.sync="showTaskList"
    width="45%"
    :show-close="false"
    class="fc-dialog-center-container job-plan"
    :close-on-press-escape="!hasError"
    :close-on-click-modal="!hasError"
    :append-to-body="true"
  >
    <div class="mB10 field-title">Name</div>
    <el-input
      class="fc-input-full-border2 mB30"
      v-model="jobPlan.name"
    ></el-input>
    <div class="mB20 field-title job-task-title">
      <span class="job-temp-title text-uppercase">Tasks</span>
    </div>
    <TasksList
      :tasksList.sync="jobPlan.jobTasks"
      :spaceAssetResourceObj="spaceAssetResourceObj"
      :hasError.sync="error"
      :filter="filter"
    ></TasksList>
    <div class="dialog-footer pT10">
      <el-button
        class="formbuilder-secondary-btn text-uppercase"
        @click="closeJobPlan()"
        >Cancel</el-button
      >
      <el-button
        class="formbuilder-primary-btn text-uppercase"
        @click="saveRecord()"
        :loading="isSaving"
        >Save</el-button
      >
    </div>
  </el-dialog>
</template>

<script>
import TasksList from '@/TasksList'
import { deepCloneObject } from 'util/utility-methods'
import http from 'util/http'
import Tasks from '@/mixins/tasks/TasksMixin'

export default {
  mixins: [Tasks],
  props: {
    canShowTaskList: {
      type: Boolean,
      required: true,
    },
    field: {
      type: Object,
    },
    spaceAssetResourceObj: {
      type: Object,
    },
    jobPlansList: {
      type: Array,
    },
    hasError: {
      type: Boolean,
    },
    filter: {
      type: Object,
      required: true,
    },
  },
  components: {
    TasksList,
  },
  computed: {
    error: {
      get() {
        return this.hasError
      },
      set(value) {
        this.$emit('update:hasError', value)
      },
    },
    showTaskList: {
      get() {
        return this.canShowTaskList
      },
      set(value) {
        this.$emit('update:canShowTaskList', value)
      },
    },
  },
  data() {
    return {
      jobPlan: {
        name: '',
        jobTasks: [],
      },
      isSaving: false,
    }
  },
  methods: {
    saveRecord() {
      let { jobPlan, error } = this
      if (!error) {
        let { jobTasks = [], name } = jobPlan
        let tasksSerializedData = {}
        let sequenceNumber = 1
        let data = {
          jobPlan: {
            name,
            tasks: {},
          },
        }
        jobTasks.forEach(task => {
          let { tasks, section } = task
          tasksSerializedData[section] = []
          tasks.forEach(task => {
            let _task = this.serializeTaskData(
              deepCloneObject(task),
              sequenceNumber++
            )
            tasksSerializedData[section].push(_task)
          })
        })
        data.jobPlan.tasks = tasksSerializedData
        this.isSaving = true
        let promise = http
          .post('v2/jobplans/add', data)
          .then(({ data: { message, responseCode, result = {} } }) => {
            if (responseCode === 0) {
              let { jobplan } = result
              let { id } = jobplan
              let { jobPlansList, field } = this
              this.$set(field, 'value', id)
              jobPlansList.push(jobplan)
              this.closeJobPlan()
            } else {
              throw new Error(message)
            }
          })
          .catch(({ message }) => {
            this.$message.error(message)
          })
        Promise.all([promise]).finally(() => (this.isSaving = false))
      }
    },
    closeJobPlan() {
      let { hasError } = this
      if (!hasError) {
        this.resetJobPlan()
        this.showTaskList = false
      }
    },
    resetJobPlan() {
      this.jobPlan = {
        name: '',
        jobTasks: [],
      }
    },
  },
}
</script>
<style scoped>
.job-task-title {
  border-bottom: 1px solid #eeecf3;
  padding-bottom: 16px;
}
.dialog-footer {
  margin: 0 -30px;
}
.job-temp-title {
  font-size: 12px;
  font-weight: bold;
  letter-spacing: 1.5px;
  color: #324056;
}
</style>
