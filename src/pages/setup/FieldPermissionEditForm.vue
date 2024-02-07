<template>
  <el-dialog
    :title="$t('setup.setupLabel.field_permission')"
    :visible="true"
    :fullscreen="false"
    :append-to-body="true"
    :before-close="closeDialog"
    custom-class="fc-dialog-center-container module-field-permission"
    class="d-flex"
  >
    <el-form ref="fieldPermissionForm" :model="fieldObj">
      <el-form-item class="d-flex" :label="$t('setup.setupLabel.export')">
        <el-radio-group v-model="fieldObj.exportable" class="pL30">
          <el-radio
            v-for="(value, label) in buttonOptions"
            :label="value"
            :key="value"
            class="fc-radio-btn"
          >
            {{ label }}
          </el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <div class="modal-dialog-footer">
      <el-button
        @click="closeDialog()"
        class="modal-btn-cancel border-bottom-left-radius"
      >
        {{ $t('agent.agent.cancel') }}</el-button
      >
      <el-button
        type="primary"
        :loading="saving"
        @click="updateCustomField"
        class="modal-btn-save border-bottom-right-radius"
        >{{ handleSaving }}</el-button
      >
    </div>
  </el-dialog>
</template>
<script>
import { API } from '@facilio/api'
export default {
  props: ['selectedField'],
  data() {
    return {
      buttonOptions: { Allow: true, "Don't Allow": false },
      saving: false,
      fieldObj: {
        exportable: null,
        dataType: null,
      },
    }
  },
  computed: {
    handleSaving() {
      return this.saving
        ? this.$t('common._common._saving')
        : this.$t('common._common._save')
    },
  },
  created() {
    let { exportable, dataType, id: fieldId } = this.selectedField || {}
    this.fieldObj = { exportable, dataType, fieldId }
  },
  methods: {
    closeDialog() {
      this.$emit('close')
    },
    async updateCustomField() {
      this.saving = true
      let params = { fieldJson: { ...(this.fieldObj || {}) } }
      let url = 'v2/modules/fields/update'
      let { error } = await API.post(url, params)

      if (error) {
        this.$message.error(
          error.message || this.$t('common._common.error_occured')
        )
      } else {
        this.$message.success(this.$t('forms.field_permission.update_success'))
        this.$emit('save')
        this.closeDialog()
      }

      this.saving = false
    },
  },
}
</script>
<style scoped>
.border-bottom-left-radius {
  border-bottom-left-radius: 4px;
}
.border-bottom-right-radius {
  border-bottom-right-radius: 4px;
}
</style>
<style lang="scss">
.module-field-permission.el-dialog {
  margin: auto !important;
  border-radius: 4px;
  width: 35%;
  height: 200px;
}
</style>
