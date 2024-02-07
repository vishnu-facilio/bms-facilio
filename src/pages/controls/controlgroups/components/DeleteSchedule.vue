<template>
  <el-dialog
    :title="$t('common.header.delete_schedule')"
    :visible="true"
    :width="isSlotDeletion ? '540px' : '618px'"
    :append-to-body="true"
    style="z-index: 9999999999;"
    class="agents-dialog fc-dialog-center-container mT10rem"
    :before-close="() => closeDialog()"
    :show-close="false"
  >
    <div class="d-flex flex-row pR30 pL30 pB30" v-if="!isSlotDeletion">
      <div class="pR20 mT3 ">{{ $t('common._common.delete') }}</div>
      <el-radio-group
        v-model="isThisEvent"
        :disabled="$validation.isEmpty(slotData)"
      >
        <el-radio :label="true" class="fc-radio-btn mR10">{{
          $t('common.events.this_event')
        }}</el-radio>
        <el-radio :label="false" class="fc-radio-btn">{{
          $t('common.wo_report.recurring')
        }}</el-radio>
      </el-radio-group>
    </div>
    <div v-else class="pR30 pL30 pB30">
      <div class="delete-slot-content">
        {{ $t('common.header.are_you_sure_you_want_to_delete_this_slot') }}
      </div>
    </div>
    <div class="schedule-btn-footer">
      <el-button
        class="modal-btn-cancel text-uppercase"
        @click="() => closeDialog()"
        >{{ $t('common._common.cancel') }}</el-button
      >
      <el-button
        class="modal-btn-save delete-dialog mL0"
        @click="deleteRecord"
        :loading="deleting"
      >
        {{
          !deleting
            ? $t('common._common.delete')
            : $t('common._common.deleting')
        }}
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['closeDialog', 'recordId', 'moduleName', 'slotData'],
  data() {
    return {
      isThisEvent: true,
      deleting: false,
    }
  },
  created() {
    let { slotData } = this
    if (isEmpty(slotData)) this.isThisEvent = false
  },
  computed: {
    getModuleDetails() {
      let { slotData, isThisEvent, recordId, moduleName } = this
      let { edited, id, exception } = slotData || {}
      let { startSchedule } = exception || {}
      let moduleDetails = {}

      if (edited) {
        moduleDetails = { moduleName: 'controlScheduleSlots', id }
      } else if (isEmpty(startSchedule) || !isThisEvent) {
        moduleDetails = { moduleName: moduleName, id: recordId }
      } else {
        moduleDetails = { moduleName: 'controlScheduleSlots', id }
      }
      return moduleDetails
    },
    isSlotDeletion() {
      let { slotData } = this
      if (isEmpty(slotData)) {
        return true
      } else {
        let { edited, exception } = slotData || {}
        let { startSchedule } = exception || {}
        return edited || isEmpty(startSchedule)
      }
    },
  },
  methods: {
    async deleteRecord() {
      this.deleting = true
      let { moduleName, id } = this.getModuleDetails
      let { error } = await API.deleteRecord(moduleName, id)
      if (error) {
        this.$message.error(error.message || 'Error Occured')
        this.closeDialog()
      } else {
        this.$message.success(
          this.$t('common.products.exception_deleted_successfully')
        )
        this.closeDialog(true)
      }
      this.deleting = false
    },
  },
}
</script>

<style scoped>
.modal-btn-save.delete-dialog {
  background-color: #ec7c7c;
  font-weight: 600;
}
.modal-btn-save:hover.delete-dialog {
  background-color: #ec7c7c !important;
}
.mT10rem {
  margin-top: 10rem;
}
.delete-slot-content {
  font-size: 14px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: 1.29;
  letter-spacing: 0.9px;
  text-align: left;
  color: #333333;
}
</style>
