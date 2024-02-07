<script>
import {
  isEmpty,
  isArray,
  isObject,
  isBoolean,
} from '@facilio/utils/validation'
import { API } from '@facilio/api'
export default {
  data() {
    return {
      checklistTypeTab: 'pre',
      tabModes: [
        {
          displayName: this.$t('home.workpermit.pre_checklist'),
          name: 'pre',
          list: 'preChecklist',
          value: 1,
        },
        {
          displayName: this.$t('home.workpermit.post_checklist'),
          name: 'post',
          list: 'postChecklist',
          value: 2,
        },
      ],
      isLoading: false,
      workPermitFieldId: null,
    }
  },
  computed: {
    workPermitTypeId() {
      return this.$getProperty(this.model, 'workPermitType.id')
    },
  },
  watch: {
    workPermitTypeId: {
      handler() {
        let deleteIds = this.$getProperty(
          this,
          'model.checklist.permitChecklistIds'
        )
        if (!isEmpty(deleteIds)) {
          this.$setProperty(this, 'model.checklist.deleteIds', deleteIds)
          delete this.model.checklist.permitChecklistIds
        }
        if (!isEmpty(this.workPermitTypeId)) {
          this.loadChecklist(true)
        } else {
          this.formatCategoryData([])
        }
      },
      immediate: true,
    },
  },
  methods: {
    getCategoriesForForm(mode) {
      return (
        this.$getProperty(this.model, `checklist.${mode.name}`, []).filter(
          category => !isEmpty(category.checklist)
        ) || []
      )
    },
    async loadChecklist(force) {
      let filters = {
        workPermitType: {
          operatorId: 36,
          value: [String(this.workPermitTypeId)],
        },
      }
      let params = {
        viewName: 'all',
        filters: JSON.stringify(filters),
      }
      this.isLoading = true
      let { list, error } = await API.fetchAll(
        `workpermittypechecklistcategory`,
        params,
        {
          force,
        }
      )
      if (error) {
        let { message = 'Error Occured while fetching Category list' } = error
        this.$message.error(message)
      } else {
        this.formatCategoryData(list)
      }
      this.isLoading = false
    },
    formatPermitChecklistData(list = []) {
      let preCategories = []
      let postCategories = []
      let permitChecklistIds = []
      list.forEach(permitChecklist => {
        let checklist = this.$getProperty(permitChecklist, 'checklist', {})
        let checklistCategory = this.$getProperty(checklist, 'category', {})
        if (!isEmpty(permitChecklist.id)) {
          permitChecklistIds.push(permitChecklist.id)
        }
        if (checklist.validationType === 1 && !isEmpty(checklistCategory)) {
          let category = preCategories.find(c => c.id === checklistCategory.id)
          if (!isEmpty(category) && isObject(category)) {
            category.checklist.push(permitChecklist)
          } else {
            preCategories.push({
              ...checklistCategory,
              checklist: [permitChecklist],
            })
          }
        } else if (
          checklist.validationType === 2 &&
          !isEmpty(checklistCategory)
        ) {
          let category = postCategories.find(c => c.id === checklistCategory.id)
          if (!isEmpty(category) && isObject(category)) {
            category.checklist.push(permitChecklist)
          } else {
            postCategories.push({
              ...checklistCategory,
              checklist: [permitChecklist],
            })
          }
        }
      })
      return { pre: preCategories, post: postCategories, permitChecklistIds }
    },
    getChecklistforValidation(list = [], validationType) {
      let categories = []
      list.forEach(permitChecklist => {
        let checklist = this.$getProperty(permitChecklist, 'checklist', {})
        let checklistCategory = this.$getProperty(checklist, 'category', {})
        if (
          checklist.validationType === validationType &&
          !isEmpty(checklistCategory) &&
          permitChecklist.required === 1
        ) {
          this.$set(
            permitChecklist,
            'isReviewed',
            !isBoolean(permitChecklist.isReviewed) || true
          )
          let category = categories.find(c => c.id === checklistCategory.id)
          if (!isEmpty(category) && isObject(category)) {
            category.checklist.push(permitChecklist)
          } else {
            categories.push({
              ...checklistCategory,
              checklist: [permitChecklist],
            })
          }
        }
      })
      return categories || []
    },
    formatCategoryData(list = []) {
      let pre = []
      let post = []
      list.forEach(category => {
        let categoryChecklist = this.$getProperty(category, 'checklist', [])
        let workPermitChecklist = []
        if (isArray(categoryChecklist) && !isEmpty(categoryChecklist)) {
          categoryChecklist.forEach(checklist => {
            workPermitChecklist.push({
              checklist,
              remarks: null,
              required: 1,
            })
          })
          this.$set(category, 'checklist', workPermitChecklist)
        }
        if (category.validationType === 1) {
          pre.push(category)
        } else if (category.validationType === 2) {
          post.push(category)
        }
      })
      if (this.$getProperty(this, 'model.checklist')) {
        this.$set(this.model.checklist, 'pre', pre)
        this.$set(this.model.checklist, 'post', post)
      }
    },
    async loadWorkPermitChecklistModuleMeta() {
      let { data, error } = await API.get(
        '/v2/modules/meta/workpermitchecklist'
      )
      if (error) {
        let { message } = error
        this.$messsage.error(
          message ||
            'Error Occured while fetching Work permit checklist module meta'
        )
      } else {
        let fields = this.$getProperty(data, 'meta.fields', [])
        let workpermitField = fields.find(field => field.name === 'workPermit')
        if (!isEmpty(workpermitField)) {
          this.workPermitFieldId = workpermitField.id
          if (!isEmpty(this.model)) {
            this.$setProperty(
              this,
              'model.checklist.fieldId',
              this.workPermitFieldId
            )
          }
        } else {
          console.warn('Work Permit field Not Found')
        }
      }
    },
  },
}
</script>
