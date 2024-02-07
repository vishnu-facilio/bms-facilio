<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import NewFailureCode from 'src/pages/assets/failureclass/forms/NewFailureCode.vue'

export default {
  extends: NewFailureCode,
  props: ['canShowFormCreation'],
  methods: {
    closeDialog(code) {
      this.$emit('update:canShowFormCreation', false)
      this.$emit('closeDialog', code)
    },
    async saveRecord(data) {
      let { formObj, moduleDataId, moduleDisplayName, moduleName } = this
      let formId = formObj?.id
      let successMsg = ''
      let failureclassToSave = this.$helpers.cloneObject(data)

      if (isEmpty(moduleDataId)) {
        successMsg = `${moduleDisplayName} created successfully!`
      } else {
        successMsg = `${moduleDisplayName} updated successfully!`
        failureclassToSave.id = moduleDataId
      }
      this.isSaving = true
      if (!isEmpty(formId)) {
        failureclassToSave.formId = formId
      }
      let response = {}
      if (isEmpty(moduleDataId)) {
        response = await API.createRecord(moduleName, {
          data: failureclassToSave,
        })
      } else {
        response = await API.updateRecord(moduleName, {
          id: moduleDataId,
          data: failureclassToSave,
        })
      }
      let { error } = response || {}
      this.isSaving = false
      if (error) {
        this.$message.error(
          error?.message || this.$t('common.failure_class.error_occured')
        )
      } else {
        this.$message.success(successMsg)
        if (response.failureclass) this.closeDialog(response.failureclass.id)
        else this.closeDialog()
      }
    },
  },
}
</script>
