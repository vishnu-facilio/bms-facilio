<template>
  <el-dialog
    :visible="true"
    :before-close="closeDialog"
    :title="`${$t('common._common.edit')} ${$t('maintenance.pm.planner')}`"
    class="planner-edit-dialog"
  >
    <div class="position-relative height160">
      <div class="planner-dialog-body">
        <div class="planner-name-label">
          {{ $t('maintenance.pm.planner_name') }}
        </div>
        <el-input
          class="fc-input-full-border2"
          v-model="plannerName"
        ></el-input>
      </div>
      <div class="modal-dialog-footer">
        <el-button class="modal-btn-cancel" @click="closeDialog">{{
          $t('maintenance._workorder.cancel')
        }}</el-button>
        <el-button
          type="primary"
          class="modal-btn-save"
          @click="savePlanner"
          :loading="isSaving"
          >{{ $t('maintenance._workorder.save') }}</el-button
        >
      </div>
    </div>
  </el-dialog>
</template>

<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['closeDialog', 'planner', 'onSave'],
  data: () => ({ plannerName: '', moduleName: 'pmPlanner', isSaving: false }),
  created() {
    let { planner } = this || {}
    let { name } = planner || {}
    this.plannerName = name || ''
  },
  methods: {
    async savePlanner() {
      this.isSaving = true
      let { moduleName, plannerName: name, planner } = this || {}
      let { id } = planner || {}
      let params = {
        data: { name },
        id,
      }
      let { error } = await API.updateRecord(moduleName, params)

      if (isEmpty(error)) {
        this.closeDialog()
        this.onSave()
      } else {
        this.$message.error(error.message || 'Error Occured')
      }
      this.isSaving = false
    },
  },
}
</script>

<style lang="scss">
.planner-edit-dialog {
  .height160 {
    height: 160px;
  }
  .planner-dialog-body {
    padding: 15px 20px 30px;
  }
  .el-dialog__body {
    padding: 0px;
  }
  .planner-name-label {
    font-size: 13px;
    line-height: normal;
    display: flex;
    word-break: break-word;
    margin-bottom: 15px;
  }
}
</style>
