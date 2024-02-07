<template>
  <el-dialog
    title="Preview Template"
    class="fc-dialog-center-container"
    :visible="true"
    width="60%"
    :before-close="handleClose"
  >
    <div class="height50vh">
      <div v-html="sanitize(previewData)"></div>
    </div>
  </el-dialog>
</template>
<script>
export default {
  props: ['previewData'],
  methods: {
    closeDialog() {
      this.$emit('onClose')
    },
  },
}
</script>
