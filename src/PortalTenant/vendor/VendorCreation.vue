<script>
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import FetchViewsMixin from '@/base/FetchViewsMixin'
import ModuleForm from '../custom-module/ModuleForm'

export default {
  extends: ModuleForm,
  mixins: [FetchViewsMixin],
  computed: {
    title() {
      return isEmpty(this.$route.params.id)
        ? this.$t('common.header.register_vendor')
        : this.$t('common.header.edit_vendor')
    },
    moduleDataId() {
      return this.$route.params.id
    },
  },
  methods: {
    saveRecord(formModel) {
      let { moduleName, moduleDataId, formObj } = this
      let { formId } = formModel
      let data = {
        ...this.serializedData(formObj, formModel),
      }

      if (formId) {
        data.formId = formId
      }

      this.isSaving = true

      if (isEmpty(moduleDataId)) {
        data['registeredBy'] = { id: this.$portaluser.ouid }

        API.createRecord(moduleName, {
          data,
        }).then(response => {
          let { error } = response || {}
          if (!error) {
            this.$message.success(
              `${this.moduleDisplayName} created successfully`
            )
            this.afterSaveHook(response)
          } else {
            this.$message.error(error.message)
          }
          this.isSaving = false
        })
      } else {
        data.id = moduleDataId

        API.updateRecord(moduleName, {
          id: moduleDataId,
          data,
        }).then(response => {
          let { error } = response || {}
          if (!error) {
            this.$message.success(
              `${this.moduleDisplayName} updated successfully`
            )
            this.afterSaveHook(response)
          } else {
            this.$message.error(error.message)
          }
          this.isSaving = false
        })
      }
    },
  },
}
</script>
