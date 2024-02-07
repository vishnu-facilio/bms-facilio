<template>
  <el-dialog
    :title="
      isNew
        ? $t('common.wo_report.create_trigger')
        : $t('common.wo_report.edit_trigger')
    "
    :visible="true"
    width="30%"
    class="fc-dialog-center-container"
    :append-to-body="true"
    :before-close="closeDialog"
  >
    <div class="height200">
      <el-form ref="triggerForm" :rules="rules" :model="triggerData">
        <el-form-item label="Name" prop="name" :required="true">
          <el-input
            class="width100 fc-input-full-border2"
            autofocus
            v-model="triggerData.name"
            type="text"
            :placeholder="$t('common._common.enter_trigger_name')"
          />
        </el-form-item>
      </el-form>

      <div class="modal-dialog-footer">
        <el-button @click="closeDialog" class="modal-btn-cancel">
          {{ $t('setup.users_management.cancel') }}
        </el-button>

        <el-button
          type="primary"
          :loading="saving"
          class="modal-btn-save"
          @click="saveTrigger()"
        >
          {{ $t('panel.dashboard.confirm') }}
        </el-button>
      </div>
    </div>
  </el-dialog>
</template>
<script>
import { Trigger } from './TriggerModel'
import cloneDeep from 'lodash/cloneDeep'

export default {
  props: ['trigger', 'isNew', 'moduleName'],

  data() {
    return {
      saving: false,
      triggerData: {},
      rules: {
        name: {
          required: true,
          message: this.$t('common._common.please_enter_a_name'),
          trigger: 'change',
        },
      },
    }
  },

  created() {
    if (this.isNew) {
      let { moduleName } = this
      this.triggerData = new Trigger({ moduleName }) //same as `Trigger.create()` to create instance
    } else {
      this.triggerData = cloneDeep(this.trigger)
    }
  },

  methods: {
    closeDialog() {
      this.$emit('onClose')
    },
    saveTrigger() {
      this.$refs['triggerForm'].validate(async valid => {
        if (!valid) return false

        this.saving = true
        try {
          await this.triggerData.save()
          this.$message.success(
            this.$t('common._common.trigger_saved_successfully')
          )
          this.$emit('onSave')
          this.closeDialog()
        } catch (errorMsg) {
          this.$message.error(errorMsg)
        }
        this.saving = false
      })
    },
  },
}
</script>
