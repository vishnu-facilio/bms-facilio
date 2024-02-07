<template>
  <div>
    <el-button @click="dialogVisible">
      Export
    </el-button>
      <el-dialog
    :title="'title'"
    :visible="true"
    custom-class="fc-dialog-center-container"
    :append-to-body="true"
    :before-close="closeDialog"
    width="90%"
    >
    <div>

    </div>
    <span slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">Cancel</el-button>
      <el-button type="primary" @click="dialogVisible = false">Export</el-button>
    </span>
  </el-dialog>
  </div>
</template>
<script>
  export default {
    data() {
      return {
        title: 'Export'
      }
    },
  }
</script>
