<script>
import { isEmpty, isArray } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import FetchViewsMixin from '@/base/FetchViewsMixin'
import ModuleForm from '../custom-module/ModuleForm'

export default {
  extends: ModuleForm,
  mixins: [FetchViewsMixin],
  computed: {
    moduleDisplayName() {
      return 'Work Permit'
    },
  },
  methods: {
    async saveRecord(formModel) {
      let { moduleDataId, formObj } = this
      let { formId } = formModel
      let moduleData = this.$helpers.cloneObject(
        this.serializedData(formObj, formModel)
      )
      let { checklist } = moduleData || {}
      let { pre = [], post = [] } = checklist || {}

      if (!isEmpty(formId)) {
        moduleData.formId = formId
      }
      if (!isEmpty(moduleData.checklist)) {
        let checklist = []

        if (!isEmpty(pre) && isArray(pre)) {
          pre.forEach(category => {
            if (!isEmpty(category.checklist)) {
              checklist.push(...category.checklist)
            }
          })
        }
        if (!isEmpty(post) && isArray(post)) {
          post.forEach(category => {
            if (!isEmpty(category.checklist)) {
              checklist.push(...category.checklist)
            }
          })
        }
        let { fieldId, deleteIds } = moduleData.checklist
        this.$setProperty(moduleData, 'relations.workpermitchecklist', [
          { fieldId, data: checklist, deleteIds },
        ])
        delete moduleData.checklist
      }

      if (isEmpty(moduleDataId)) {
        this.isSaving = true
        let { workpermit, error } = await API.createRecord(this.moduleName, {
          data: moduleData,
        })
        if (error) {
          let {
            message = this.$t(
              'common.products.error_occured_while_saving_work_permit'
            ),
          } = error
          this.$message.error(message)
        } else {
          this.$message.success(
            this.$t('common.products.work_permit_created_successfully')
          )
          this.redirectToSummary(workpermit.id)
        }
        this.isSaving = false
      } else {
        this.isSaving = true
        let { workpermit, error } = await API.updateRecord(this.moduleName, {
          id: moduleDataId,
          data: moduleData,
        })
        if (error) {
          let {
            message = this.$t(
              'common.products.error_occured_while_updating_work_permit'
            ),
          } = error
          this.$message.error(message)
        } else {
          this.$message.success(
            this.$t('common.products.work_permit_updated_successfully')
          )
          this.redirectToSummary(workpermit.id)
        }
        this.isSaving = false
      }
    },
  },
}
</script>
