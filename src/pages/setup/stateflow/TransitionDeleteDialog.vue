<template>
  <div>
    <el-dialog
      :visible="true"
      width="30%"
      title="Confirm Delete"
      class="fieldchange-Dialog pB15 fc-dialog-center-container"
      custom-class="dialog-header-padding"
      :append-to-body="true"
      :before-close="close"
    >
      <div class="height140 overflow-y-scroll pB50 pT10">
        Are you sure you want to delete this transition?
      </div>
      <div class="modal-dialog-footer">
        <el-button @click="close" class="modal-btn-cancel">Cancel</el-button>
        <el-button
          type="primary"
          class="modal-btn-save delete-dialog"
          @click="save"
          >Confirm</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  methods: {
    save() {
      this.$emit('onSave')
      this.close()
    },

    close() {
      this.$emit('onClose')
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
</style>
