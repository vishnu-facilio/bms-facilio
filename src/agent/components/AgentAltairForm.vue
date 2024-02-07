<template>
  <div>
    <el-dialog
      title="WattSense"
      :visible.sync="visibility"
      width="50%"
      :before-close="closeDialog"
      :append-to-body="true"
      custom-class="fc-dialog-form fc-dialog-right setup-dialog40 setup-dialog slideInRight"
    >
      <div class="new-header-container">
        <div class="new-header-text">
          <div class="fc-setup-modal-title">
            New Altair
          </div>
        </div>
      </div>

      <div class="new-body-modal"></div>

      <div class="modal-dialog-footer">
        <el-button @click="closeDialog()" class="modal-btn-cancel"
          >CANCEL</el-button
        >
        <el-button type="primary" class="modal-btn-save">Save</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
export default {
  props: ['visibility'],
  data() {
    return {}
  },
  methods: {
    closeDialog() {
      this.$emit('update:visibility', false)
    },
  },
}
</script>
