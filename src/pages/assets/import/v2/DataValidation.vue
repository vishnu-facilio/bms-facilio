<script>
import DataValidation from 'src/pages/assets/import/v1/DataValidation'

export default {
  extends: DataValidation,
  methods: {
    finishValidation() {
      this.saving = true
      let url =
        this.importProcessContext.importMode === 1
          ? '/v2/import/importHandler'
          : '/import/importreading'
      this.$http
        .post(url, {
          importProcessContext: this.importProcessContext,
        })
        .then(response => {
          this.$emit(
            'update:importProcessContext',
            response.data.result.importProcessContext
          )
          this.$emit('finishValidation', true)
          this.saving = false
        })
        .catch(() => {
          this.$message.error('Error Occured')
          this.saving = false
        })
    },
  },
}
</script>
