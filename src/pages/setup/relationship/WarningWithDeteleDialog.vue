<template>
  <el-dialog
    :visible="true"
    width="30%"
    :title="$t('common.header.warning')"
    class="fieldchange-Dialog pB15 fc-dialog-center-container"
    custom-class="dialog-header-padding height200"
    :append-to-body="true"
    :before-close="closeDialog"
  >
    <div class="warning-dialog-container">
      {{ $t('setup.relationship.association_warning') }}
      <br />
      {{ $t('setup.relationship.delete_confirmation') }}
    </div>

    <div class="modal-dialog-footer">
      <el-button @click="closeDialog" class="modal-btn-cancel text-uppercase"
        >{{ $t('setup.relationship.cancle') }}
      </el-button>
      <el-button
        type="primary"
        class="modal-btn-save delete-dialog"
        @click="confirmDelete"
        :loading="saving"
      >
        {{ $t('setup.relationship.continue_deleting') }}
      </el-button>
    </div>
  </el-dialog>
</template>
<script>
export default {
  props: ['deleteRelationRecord'],
  data() {
    return {
      saving: false,
    }
  },
  methods: {
    closeDialog() {
      this.$emit('onClose')
    },
    async confirmDelete() {
      this.saving = true
      await this.deleteRelationRecord()
      this.saving = false
      this.closeDialog()
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
.warning-dialog-container {
  word-break: break-word;
  padding-bottom: 40px;
  padding-top: 10px;
  overflow-y: scroll;
}
</style>
