<script>
import { isEmpty, isArray } from '@facilio/utils/validation'
import FormCreation from '@/base/FormCreation'
import { API } from '@facilio/api'
import PermitChecklistMixin from './PermitChecklistMixin'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  extends: FormCreation,
  mixins: [PermitChecklistMixin],

  computed: {
    moduleName() {
      return 'workpermit'
    },
    moduleDisplayName() {
      return 'Work Permit'
    },
    isV3Api() {
      return true
    },
    isWidgetsSupported() {
      return true
    },
  },
  methods: {
    async loadModuleData({ moduleDataId }) {
      let url = `/v3/modules/data/summary?moduleName=workpermit&id=${moduleDataId}`
      let { data, error } = await API.get(url)
      if (error) {
        let { message } = error
        this.$message.error(message)
      } else {
        let { workpermit } = data || {}
        if (!isEmpty((workpermit || {}).checklist)) {
          workpermit.checklist = this.formatPermitChecklistData(
            workpermit.checklist
          )
        }
        this.$set(this, 'moduleData', workpermit)
      }
    },
    async saveRecord(formModel) {
      let { moduleDataId, formObj } = this
      let { formId } = formModel
      let moduleData = this.$helpers.cloneObject(
        this.serializedData(formObj, formModel)
      )
      if (!isEmpty(formId)) {
        moduleData.formId = formId
      }
      if (!isEmpty(moduleData.checklist)) {
        let checklist = []
        if (
          !isEmpty(moduleData.checklist.pre) &&
          isArray(moduleData.checklist.pre)
        ) {
          moduleData.checklist.pre.forEach(category => {
            if (!isEmpty(category.checklist)) {
              checklist.push(...category.checklist)
            }
          })
        }
        if (
          !isEmpty(moduleData.checklist.post) &&
          isArray(moduleData.checklist.post)
        ) {
          moduleData.checklist.post.forEach(category => {
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
          let { message = 'Error Occured while Saving Work Permit' } = error
          this.$message.error(message)
        } else {
          this.$message.success(`Work Permit Created Successfully`)
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
          let { message = 'Error Occured while Updating Work Permit' } = error
          this.$message.error(message)
        } else {
          this.$message.success(`Work Permit Updated successfully!`)
          this.redirectToSummary(workpermit.id)
        }
        this.isSaving = false
      }
    },
    redirectToList() {
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(this.moduleName, pageTypes.LIST) || {}
        name &&
          this.$router.push({
            name,
          })
      } else {
        this.$router.push({
          name: 'workPermitList',
        })
      }
    },
    redirectToSummary(id) {
      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForModule(this.moduleName, pageTypes.OVERVIEW) || {}
        name &&
          this.$router.push({
            name,
            params: {
              id: id,
              viewname: 'all',
            },
          })
      } else {
        this.$router.push({
          name: 'workPermitSummaryV3',
          params: { id: id, viewname: 'all' },
        })
      }
    },
  },
}
</script>
