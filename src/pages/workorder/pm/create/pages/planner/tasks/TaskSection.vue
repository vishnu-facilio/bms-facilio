<template>
  <div>
    <template v-if="!isJobPlanEmpty">
      <div class="mT5 planner-trigger-section">
        <div class="section-sub-heading">
          {{ $t('maintenance.pm.ad_hoc_task') }}
        </div>
        <div class="flex flex-no-wrap mT10 justify-between align-center">
          <div class="section-description width60">
            {{ $t('maintenance.pm.ad_hoc_desc') }}
          </div>
          <el-button class="edit-task-btn" @click="editTask">{{
            $t('maintenance.pm.edit_task')
          }}</el-button>
        </div>
      </div>
    </template>
    <el-dialog
      :visible="showTaskDialog"
      :before-close="closeDialog"
      width="70%"
      class="planner-task-dialog"
      top="2vh"
    >
      <template #title>
        <div>
          <div class="section-sub-heading mB10 f16">
            {{ $t('maintenance.pm.ad_hoc_task') }}
          </div>
          <div class="section-description width60">
            {{ $t('maintenance.pm.ad_hoc_desc') }}
          </div>
        </div>
      </template>
      <div>
        <div v-if="isLoading" class="flex-middle m10">
          <spinner :show="isLoading" size="80"></spinner>
        </div>
        <template v-else>
          <div class="planner-task-section">
            <TaskCreation
              v-model="tasksList"
              :isPreRequisite="false"
              :isJobPlan="false"
            />
          </div>
          <div class="modal-dialog-footer">
            <el-button class="modal-btn-cancel" @click="closeDialog">{{
              $t('maintenance._workorder.cancel')
            }}</el-button>
            <el-button
              type="primary"
              class="modal-btn-save"
              @click="saveTask"
              :loading="isSaving"
              >{{ $t('maintenance._workorder.save') }}</el-button
            >
          </div>
        </template>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import TaskCreation from './TaskCreation'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'

const SCOPE_OPTIONS = {
  SPACES: 2,
  SPACECATEGORY: 3,
  ASSETCATEGORY: 4,
  ASSETS: 6,
  BUILDINGS: 7,
  SITES: 8,
}

export default {
  props: [
    'closeDialog',
    'reloadPlanner',
    'pmRecord',
    'showTaskDialog',
    'planner',
  ],
  components: { TaskCreation },
  data: () => ({
    tasksList: [],
    isSaving: false,
    isLoading: false,
  }),
  computed: {
    moduleName() {
      return 'jobplan'
    },
    isJobPlanEmpty() {
      let { jobPlanId } = this || {}
      return isEmpty(jobPlanId)
    },
    jobPlanId() {
      let { planner } = this || {}
      let { jobPlan } = planner || {}
      let { id } = jobPlan || {}
      return id
    },
  },
  methods: {
    async saveTask() {
      this.isSaving = true
      let { tasksList, moduleName, pmRecord } = this || {}
      let { assignmentTypeEnum } = pmRecord || {}
      assignmentTypeEnum = SCOPE_OPTIONS[assignmentTypeEnum]
      let params = {
        data: {
          jobPlanCategory: assignmentTypeEnum,
          jobplansection: tasksList,
        },
      }
      let { [moduleName]: data, error } = await API.createRecord(
        moduleName,
        params
      )
      if (!isEmpty(error)) {
        let { id } = data || {}
        await this.associateJobPlan(id)
      } else {
        this.$message.error(error.message || 'Error occured')
      }
      this.isSaving = false
    },
    editTask() {
      this.$emit('update:showTaskDialog', true)
      this.loadJobPlan()
    },
    async loadJobPlan() {
      this.isLoading = true
      let { moduleName, jobPlanId } = this || {}
      let params = {
        id: jobPlanId,
      }
      let { [moduleName]: data, error } = await API.fetchRecord(
        moduleName,
        params
      )
      if (!isEmpty(error)) {
        this.$message.error(error.message || 'Error occured')
      } else {
        let { jobplansection } = data || {}
        this.tasksList = jobplansection
      }
      this.isLoading = false
    },
    async associateJobPlan(id) {
      let params = {
        jobPlan: { id },
      }
      let { error } = await API.updateRecord('pmPlanner', params)
      if (!isEmpty(error)) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.$message.success(this.$t('maintenance.pm.planner_update'))
        this.reloadPlanner()
      }
    },
  },
}
</script>

<style lang="scss">
.planner-task-dialog {
  .el-dialog__body {
    padding: 0px;
  }
}
</style>

<style lang="scss" scoped>
.planner-task-section {
  height: 400px;
  padding: 0px 20px 50px;
  position: relative;
  overflow: scroll;
}
.edit-task-btn {
  height: 40px;
  &:hover {
    border: solid 1px #39b2c2;
    background: #fff;
    color: #39b2c2;
  }
}
</style>
