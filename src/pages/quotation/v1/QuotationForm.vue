<script>
import { isEmpty, isArray, areValuesEmpty } from '@facilio/utils/validation'
import FormCreation from '@/base/FormCreation'
import { API } from '@facilio/api'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  extends: FormCreation,
  computed: {
    moduleName() {
      return 'quote'
    },
    customClassForContainer() {
      return 'quotation-form'
    },
    moduleDisplayName() {
      if (this.formObj && this.formObj.module) {
        return this.formObj.module.displayName
      }
      return ''
    },
    isV3Api() {
      return true
    },
    isRevise() {
      return this.$route.query.revise
    },
    isWidgetsSupported() {
      return true
    },
    title() {
      let { moduleDisplayName, moduleDataId } = this
      let title = ``
      if (this.isRevise) {
        title = `Revision - ${this.revisionNumber}`
      } else if (!isEmpty(moduleDataId)) {
        title = `Edit ${moduleDisplayName}`
      } else {
        title = `Create ${moduleDisplayName}`
      }
      return title
    },
    revisionNumber() {
      let revisionNumber = this.$getProperty(
        this,
        'moduleData.revisionNumber',
        0
      )
      return Number(revisionNumber || 0) + 1
    },
  },
  methods: {
    /* Overrided hooks */
    async loadModuleData({ moduleDataId }) {
      let url = `/v3/modules/data/summary?moduleName=quote&id=${moduleDataId}`
      let { data, error } = await API.get(url)
      if (error) {
        let { message } = error
        this.$message.error(
          message || 'Error Occured while fetching quote details'
        )
      } else {
        let { quote } = data || {}
        if (!isEmpty(quote.lineItems)) {
          quote.lineItems.forEach(lineItem => {
            if (isEmpty(lineItem.tax)) {
              this.$set(lineItem, 'tax', { id: null })
            }
            if (isEmpty(lineItem.toolType)) {
              this.$set(lineItem, 'toolType', { id: null })
            }
            if (isEmpty(lineItem.itemType)) {
              this.$set(lineItem, 'itemType', { id: null })
            }
            if (isEmpty(lineItem.service)) {
              this.$set(lineItem, 'service', { id: null })
            }
            if (isEmpty(lineItem.labour)) {
              this.$set(lineItem, 'labour', { id: null })
            }
            if (isEmpty(lineItem.description)) {
              this.$set(lineItem, 'description', '')
            }
            if (isEmpty(lineItem.unitOfMeasure)) {
              this.$set(lineItem, 'unitOfMeasure', null)
            }
          })
        }
        this.$set(this, 'moduleData', quote)
      }
    },
    /* Event related methods */
    async saveRecord(formModel) {
      let { moduleDataId, formObj } = this
      let { formId } = formModel
      let moduleData = this.$helpers.cloneObject(
        this.serializedData(formObj, formModel)
      )
      if (!isEmpty(formId)) {
        moduleData.formId = formId
      }
      // TODO @Aashiq remove validations
      if (!isEmpty(moduleData.lineItems) && isArray(moduleData.lineItems)) {
        moduleData.lineItems.forEach(lineItem => {
          if (lineItem.type === 1) {
            lineItem.toolType = null
            lineItem.labour = null
            lineItem.service = null
            if (areValuesEmpty(lineItem.itemType)) {
              lineItem.itemType = null
            }
          } else if (lineItem.type === 2) {
            lineItem.itemType = null
            lineItem.labour = null
            lineItem.service = null
            if (areValuesEmpty(lineItem.toolType)) {
              lineItem.toolType = null
            }
          } else if (lineItem.type === 3) {
            lineItem.itemType = null
            lineItem.toolType = null
            lineItem.labour = null
            if (areValuesEmpty(lineItem.service)) {
              lineItem.service = null
            }
          } else if (lineItem.type === 4) {
            lineItem.itemType = null
            lineItem.toolType = null
            lineItem.service = null
            if (areValuesEmpty(lineItem.labour)) {
              lineItem.labour = null
            }
          } else if (lineItem.type === 5) {
            lineItem.itemType = null
            lineItem.toolType = null
            lineItem.service = null
            lineItem.labour = null
          }
          if (isEmpty(lineItem.tax) || isEmpty(lineItem.tax.id)) {
            lineItem.tax = null
          }
        })
      }
      if (this.isRevise) {
        moduleData['id'] = moduleDataId
        moduleData['termsAssociated'] = (this.moduleData || {}).termsAssociated
        moduleData['revisionNumber'] = (this.moduleData || {}).revisionNumber
        this.isSaving = true
        let { quote, error } = await API.createRecord(`quote`, {
          data: moduleData,
          params: { revise: true },
        })
        if (error) {
          let { message = 'Error Occured while Revising Quote' } = error
          this.$message.error(message)
        } else {
          this.$message.success(`Quote revised successfully!`)
          this.redirectToSummary(quote.id)
        }
        this.isSaving = false
      } else if (isEmpty(moduleDataId)) {
        this.isSaving = true
        let { quote, error } = await API.createRecord(`quote`, {
          data: moduleData,
        })
        if (error) {
          let { message = 'Error Occured while Saving Quote' } = error
          this.$message.error(message)
        } else {
          this.$message.success(`Quote Created Successfully`)
          this.redirectToSummary(quote.id)
        }
        this.isSaving = false
      } else {
        this.isSaving = true
        let { quote, error } = await API.updateRecord(`quote`, {
          id: moduleDataId,
          data: moduleData,
        })
        if (error) {
          let { message = 'Error Occured while Updating Quote' } = error
          this.$message.error(message)
        } else {
          this.$message.success(`Quote Updated successfully`)
          this.redirectToSummary(quote.id)
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
            query: this.$route.query,
            viewname: 'all',
          })
      } else {
        this.$router.push({
          path: '/app/tm/quotation/all',
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
              viewname: 'all',
              id,
            },
          })
      } else {
        this.$router.push({
          path: `/app/tm/quotation/all/${id}/overview`,
        })
      }
    },
  },
}
</script>
<style scoped>
.quotation-form {
  max-width: 1200px;
}
</style>
