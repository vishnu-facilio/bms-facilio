<template>
  <el-dialog
    :visible="true"
    title="Delete this group?"
    height="100px"
    class="fc-dialog-center-container "
    custom-class="action-configure-dialog delete-popup"
    :close-on-press-escape="true"
    :append-to-body="true"
    :show-close="false"
  >
    <div>
      Do you want to delete <b>{{ groupName }}</b> and all its contents?
    </div>
    <span slot="footer" class="modal-dialog-footer row">
      <el-button class="col-6 modal-btn-cancel" @click="$emit('no')"
        >No</el-button
      >
      <el-button
        type="primary"
        class="col-6 modal-btn-save"
        @click="$emit('yes')"
        >Yes</el-button
      >
    </span>
  </el-dialog>
</template>

<script>
export default {
  props: {
    groupName: {
      type: String,
      required: true,
    },
  },
}
</script>
<style lang="scss">
.delete-popup {
  height: 180px;
}
</style>
<style lang="scss" scoped></style>
