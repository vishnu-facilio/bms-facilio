<script>
import ImportSetting from 'src/pages/assets/import/v1/ImportSetting'

export default {
  extends: ImportSetting,
  methods: {
    addAttachment() {
      const formData = new FormData()
      formData.append('moduleName', this.module)
      formData.append('fileUpload', this.uploadFile)
      formData.append('importMode', this.importMode)
      this.loading = true
      this.$http
        .post('/v2/import/uploadHandler', formData)
        .then(response => {
          if (response.data) {
            this.loading = false
            if (response.data.responseCode !== 0) {
              this.$message.error(response.data.message)
              this.deleteAttachment()
            } else {
              this.$emit(
                'update:importProcessContext',
                response.data.result.importProcessContext
              )
              this.$message.success('File Uploaded Successfully')
            }
          }
        })
        .catch(error => {
          this.loading = false
          this.$message.error(error)
          this.deleteAttachment()
        })
    },
  },
}
</script>
