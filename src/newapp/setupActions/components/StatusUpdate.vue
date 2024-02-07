<template>
  <el-dialog
    :visible="true"
    width="40%"
    :title="$t('setup.setup.change_status')"
    class="fieldchange-Dialog pB15 fc-dialog-center-container"
    custom-class="dialog-header-padding"
    :append-to-body="true"
    :close-on-click-modal="false"
    :show-close="false"
  >
    <div v-if="isLoading" class="height120">
      <span class="rectangle loading-shimmer"></span>
    </div>
    <div v-else class="height120 overflow-y-scroll">
      <el-select
        v-model="statusUpdateObj.new_state"
        class="fc-input-full-border-select2 width100 mT5"
        filterable
      >
        <el-option
          v-for="(label, value) in statusList"
          :key="value"
          :label="label"
          :value="value"
        ></el-option>
      </el-select>
    </div>

    <div class="modal-dialog-footer">
      <el-button @click="closeDialog" class="modal-btn-cancel">{{
        $t('common._common.cancel')
      }}</el-button>
      <el-button type="primary" class="modal-btn-save" @click="actionSave">
        {{ $t('common._common.save') }}</el-button
      >
    </div>
  </el-dialog>
</template>
<script>
import { ChangeStatusModel } from '../models/ChangeStatusModel'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['actionObj', 'moduleName', 'moduleId'],
  data() {
    return {
      statusUpdateObj: {},
      statusList: {},
      isLoading: false,
    }
  },

  async created() {
    this.isLoading = true
    this.statusUpdateObj = new ChangeStatusModel(this.actionObj || {})

    let metaModuleId = null

    if (isEmpty(this.moduleId)) {
      metaModuleId = await this.fetchModuleId()
    }

    try {
      this.statusList = await ChangeStatusModel.getTicketStatus(
        this.moduleId || metaModuleId
      )
    } catch (error) {
      this.$message.error(error.message)
    }

    this.isLoading = false
  },
  methods: {
    async fetchModuleId() {
      try {
        return await ChangeStatusModel.fetchMetaModuleId(this.moduleName)
      } catch (error) {
        this.$message.error(error.message)
      }
    },
    closeDialog() {
      this.$emit('onClose')
    },
    actionSave() {
      this.$emit('onSave', this.statusUpdateObj.serialize())
      this.closeDialog()
    },
  },
}
</script>
<style scoped>
.height120 {
  height: 120px;
}
.rectangle {
  height: 30px;
  width: 100%;
}
</style>
