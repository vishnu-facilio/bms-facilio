<template>
  <div>
    <div class="new-header-container">
      <div class="new-header-text">
        <div class="fc-setup-modal-title">
          {{ $t('common.wo_report.export_alarm_summary') }}
        </div>
      </div>
    </div>
    <div class="export-body-dailog">
      <div>
        <el-checkbox
          checked
          style="padding-bottom: 10px;padding-left: 40px;padding-top: 10px;"
          disabled
          >{{ $t('common.events.all_events') }}</el-checkbox
        >
      </div>
      <el-checkbox-group v-model="selectedFields" class="check-padding-remove">
        <div class="row">
          <el-checkbox
            v-for="field in assetFields"
            :key="field.id"
            :label="field.id"
            class="check-width"
            >{{ field ? field.displayName : '' }}</el-checkbox
          >
        </div>
      </el-checkbox-group>
    </div>

    <div class="modal-dialog-footer">
      <el-button @click="closeExportDialog()" class="modal-btn-cancel">{{
        $t('common._common.cancel')
      }}</el-button>
      <el-button
        type="primary"
        @click="submitExportDialogue()"
        class="modal-btn-save"
        >{{ $t('common.wo_report.export_data') }}</el-button
      >
    </div>
  </div>
</template>

<script>
export default {
  props: ['assetFields'],
  data() {
    return {
      selectedFields: [],
    }
  },
  methods: {
    submitExportDialogue() {
      this.$emit('submit', this.selectedFields)
    },
    closeExportDialog() {
      this.$emit('close')
    },
  },
}
</script>

<style scoped>
.export-body-dailog {
  height: 50vh;
  overflow-x: hidden;
  overflow-y: scroll;
  padding-bottom: 40px;
}
.check-padding-remove .el-checkbox + .el-checkbox {
  margin-left: 0;
}
.check-width {
  width: 100%;
  padding-bottom: 7px;
  padding-left: 40px;
  padding-top: 10px;
  padding-bottom: 10px;
}
</style>
