<script>
// used to customize the notification
export default {
  methods: {
    showError(data = 'Error occured') {
      this.$message.error(data)
    },
    showSaving(data = 'Saving...') {
      this.$message({
        message: data,
        type: 'success',
      })
    },
    showSuccess(data = 'success') {
      this.$message({
        message: data,
        type: 'success',
      })
    },
    showWarning(data = 'warning') {
      this.$message({
        message: data,
        type: 'warning',
      })
    },
  },
}
</script>
