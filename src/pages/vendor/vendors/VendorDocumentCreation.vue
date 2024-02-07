<script>
import { isEmpty } from '@facilio/utils/validation'
import FormCreation from '@/base/FormCreation'
import { API } from '@facilio/api'

export default {
  extends: FormCreation,
  computed: {
    moduleName() {
      return 'vendorDocuments'
    },
    moduleDisplayName() {
      if (this.formObj && this.formObj.module) {
        return this.formObj.module.displayName
      }
      return ''
    },
    moduleDataId() {
      return this.$route.query.id
    },
    isWidgetsSupported() {
      return true
    },
  },
  methods: {
    async saveRecord(formModel) {
      let { moduleName, moduleDataId, formObj } = this
      let { formId, document } = formModel
      let url = ''
      let successMsg = ''
      let finalData = ''
      let data = {
        moduleName,
        withLocalId: false,
        moduleData: this.serializedData(formObj, formModel),
      }
      if (document) {
        data.moduleData['documents'] = document
      }
      if (isEmpty(moduleDataId)) {
        url = `/v2/documents/add`
        successMsg = `${this.moduleDisplayName} created successfully!`
      } else {
        url = `/v2/documents/update`
        successMsg = `${this.moduleDisplayName} updated successfully!`
        data.moduleData.id = moduleDataId
      }
      if (!isEmpty(formId)) {
        data.moduleData.formId = formId
      }
      let formData = new FormData()
      data.moduleData.parentId.id = this.$route.query.vendorId
      this.$helpers.setFormData('documents[0]', data.moduleData, formData)
      finalData = formData
      this.isSaving = true

      let { error } = await API.post(url, finalData)

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.$message.success(successMsg)
      }
    },
  },
}
</script>
