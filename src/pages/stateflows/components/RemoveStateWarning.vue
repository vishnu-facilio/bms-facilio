<template>
  <div>
    <el-dialog
      :visible="canShowDialog"
      width="30%"
      title="Warning"
      class="fieldchange-Dialog pB15 fc-dialog-center-container"
      custom-class="dialog-header-padding"
      :append-to-body="true"
      :before-close="
        () => {
          canShowDialog = false
        }
      "
    >
      <div class="overflow-y-scroll pB40 pT10" style="word-break: break-word;">
        There are transitions associated to this state.
        <br />
        <br />Please remove all associated transitions before removing the state
        from the stateflow
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      canShowDialog: false,
    }
  },

  methods: {
    show() {
      this.canShowDialog = true
    },
  },
}
</script>
<style scoped>
.modal-btn-save.delete-dialog {
  background-color: #ec7c7c;
  font-weight: 600;
  width: 100%;
}
.modal-btn-save:hover.delete-dialog {
  background-color: #ec7c7c !important;
}
</style>
