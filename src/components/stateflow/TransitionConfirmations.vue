<template>
  <el-dialog
    :visible.sync="dialogVisible"
    :title="activeDialog.title"
    width="540px"
    style="z-index: 999999"
    custom-class="confirmation-dialog"
    append-to-body
    :close-on-click-modal="false"
    :before-close="activeDialog.cancel"
  >
    <div class="message-block">
      {{ activeDialog.message }}
    </div>
    <div class="dialog-footer">
      <el-button
        type="button"
        class="btn btn--secondary cancel-btn"
        @click="activeDialog.cancel"
      >
        Cancel
      </el-button>
      <el-button
        type="button"
        class="btn btn--primary btn--danger delete-btn"
        @click="activeDialog.proceed"
        :disabled="loadingOnConfirm"
        :loading="loadingOnConfirm"
      >
        Confirm
      </el-button>
    </div>
  </el-dialog>
</template>
<script>
export default {
  props: ['confirmations', 'onConfirm', 'onCancel'],
  data() {
    return {
      dialogVisible: false,
      activeDialog: null,
      currentIndex: 0,
      loadingOnConfirm: false,
    }
  },
  watch: {
    confirmations: {
      handler: 'startConfirmation',
      immediate: true,
    },
  },
  methods: {
    async startConfirmation() {
      let {
        confirmations,
        currentIndex,
        $helpers: { delay },
      } = this
      let dialog = confirmations[currentIndex]

      let dialogObj = {
        title: dialog.name,
        message: dialog.message,
        proceed: async () => {
          // If current dialog is the last then call the onConfirm handler and then call
          // done to keep the loading state active else proceed with the next dialog
          if (currentIndex === confirmations.length - 1) {
            this.loadingOnConfirm = true
            await this.onConfirm()
            this.loadingOnConfirm = false
            this.dialogVisible = false
            this.currentIndex = 0
          } else {
            this.currentIndex += 1
            this.dialogVisible = false
            delay(200).then(() => this.startConfirmation())
          }
        },
        cancel: async () => {
          this.dialogVisible = false
          this.currentIndex = 0
          this.onCancel()
        },
      }

      this.activeDialog = dialogObj
      this.$nextTick(() => (this.dialogVisible = true))
    },
  },
}
</script>
<style lang="scss">
.confirmation-dialog {
  .el-dialog__header {
    padding: 40px 40px 10px;
  }
  .el-dialog__body {
    padding: 0;
  }
  .message-block {
    padding: 0px 40px 50px;
    word-break: break-word;
    white-space: pre-line;
    line-height: 26px;
  }
  .cancel-btn,
  .delete-btn {
    width: 50%;
    border: none;
    border-radius: 0;
    margin-left: 0 !important;
    padding-top: 20px;
    padding-bottom: 20px;
    font-size: 13px;
    font-weight: 500;
    letter-spacing: 1.1px;
    text-align: center;
    vertical-align: bottom;
  }
  .cancel-btn {
    background-color: #f4f4f4;
    color: #8f8f8f;
  }
  .delete-btn {
    background-color: #e47676;
    color: #ffffff;
    &:hover {
      background-color: #d95f5f;
    }
  }
}
</style>
