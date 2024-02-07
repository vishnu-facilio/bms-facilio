<template>
  <el-dialog
    :visible="true"
    :before-close="() => {}"
    :append-to-body="true"
    :show-close="false"
    width="36%"
    class="pm-bulk-save-dialog"
    top="30vh"
  >
    <div class="bulk-save-dialog-heading">
      {{ $t('maintenance.pm.all_records_selected') }}
    </div>
    <div class="bulk-save-dialog-sub-heading">
      {{
        `${$t('maintenance.pm.save_prompt_1')} ${totalCount} ${$t(
          'maintenance.pm.save_prompt_2'
        )}`
      }}
    </div>
    <div class="flex">
      <el-button @click="undoSelection" class="progress-cancel">{{
        $t('maintenance.pm.cancel_progress')
      }}</el-button>
      <el-button @click="saveRecord" class="progress-save">{{
        `${$t('maintenance.pm.save_all')} (${totalCount})`
      }}</el-button>
    </div>
  </el-dialog>
</template>

<script>
export default {
  props: ['closeDialog', 'totalCount', 'saveRecord'],
  methods: {
    undoSelection() {
      this.$emit('cancel')
    },
  },
}
</script>

<style scoped lang="scss">
.bulk-save-dialog-heading {
  font-size: 24px;
  color: #324056;
  font-weight: 600;
}
.bulk-save-dialog-sub-heading {
  font-size: 14px;
  color: #324056;
  margin: 14px 0px 24px;
}
.progress-save {
  background-color: #3ab2c2;
  color: #ffffff;
  border-color: transparent;
}
.progress-cancel {
  background-color: #f5f5f5;
  color: #324056;
  border-color: transparent;
}
.progress-save,
.progress-cancel {
  font-size: 13px;
  font-weight: 400;
}
</style>

<style lang="scss">
.pm-bulk-save-dialog {
  .el-dialog {
    .el-dialog__header {
      padding: 0px;
    }
    .el-dialog__body {
      display: flex;
      flex-direction: column;
      align-items: center;
    }
  }
}
</style>
