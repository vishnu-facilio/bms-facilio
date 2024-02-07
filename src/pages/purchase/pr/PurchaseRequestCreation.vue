<script>
import FormCreation from '@/base/FormCreation'
import PurchaseMixin from 'pages/purchase/PurchaseMixin'
import FetchViewsMixin from 'src/components/base/FetchViewsMixin'
import { isEmpty, isArray, areValuesEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  extends: FormCreation,
  props: ['viewname'],
  mixins: [PurchaseMixin, FetchViewsMixin],
  data() {
    return {}
  },
  computed: {
    isWidgetsSupported() {
      return true
    },
    isV3Api() {
      return true
    },
    moduleName() {
      return 'purchaserequest'
    },
    moduleDisplayName() {
      return 'Purchase Request'
    },
  },
  methods: {
    /* Overrided hooks */
    async loadModuleData({ moduleDataId }) {
      let param = { recordId: moduleDataId }
      let url = `v3/modules/data/summary?moduleName=purchaserequest&id=${moduleDataId}`
      let { data, error } = await API.get(url, param)
      if (error) {
        let { message } = error
        this.$message.error(message)
      } else {
        let { purchaseorder, purchaserequest } = data || {}
        let summaryData = purchaseorder || purchaserequest
        if ((summaryData || {}).data) {
          summaryData = { ...summaryData, ...summaryData.data }
        }

        if (!isEmpty(summaryData) && !isEmpty(summaryData.lineItems)) {
          summaryData.lineItems.forEach(lineItem => {
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
            if (isEmpty(lineItem.description)) {
              this.$set(lineItem, 'description', '')
            }
            if (isEmpty(lineItem.unitOfMeasure)) {
              this.$set(lineItem, 'unitOfMeasure', null)
            }
          })
        }
        this.$set(this, 'moduleData', summaryData || {})
      }
    },
    /* Event related methods */
    saveRecord(formModel) {
      let { formObj } = this
      let serializedData = this.serializedData(formObj, formModel)

      if (
        !isEmpty(serializedData.lineItems) &&
        isArray(serializedData.lineItems)
      ) {
        serializedData.lineItems.forEach(lineItem => {
          if (lineItem.inventoryType === 1) {
            lineItem.toolType = null
            lineItem.service = null
            if (areValuesEmpty(lineItem.itemType)) {
              lineItem.itemType = null
            }
          } else if (lineItem.inventoryType === 2) {
            lineItem.itemType = null
            lineItem.service = null
            if (areValuesEmpty(lineItem.toolType)) {
              lineItem.toolType = null
            }
          } else if (lineItem.inventoryType === 3) {
            lineItem.itemType = null
            lineItem.toolType = null
            if (areValuesEmpty(lineItem.service)) {
              lineItem.service = null
            }
          } else if (lineItem.inventoryType === 4) {
            lineItem.itemType = null
            lineItem.toolType = null
            lineItem.service = null
          }
          if (isEmpty(lineItem.tax) || isEmpty(lineItem.tax.id)) {
            lineItem.tax = null
          }
        })
      }
      this.submitForm(serializedData)
    },
    redirectToList() {
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(this.moduleName, pageTypes.LIST) || {}
        name &&
          this.$router.push({
            name,
            query: this.$route.query,
          })
      } else {
        this.$router.push({
          name: 'purchaserequest',
        })
      }
    },

    async redirectToSummary(id) {
      let { moduleName } = this || {}
      if (isWebTabsEnabled()) {
        let currentView = await this.fetchView(moduleName)
        let { name } =
          findRouteForModule(this.moduleName, pageTypes.OVERVIEW) || {}
        name &&
          this.$router.push({
            name,
            params: {
              viewname: currentView,
              id,
            },
          })
      } else {
        let url = `/app/purchase/pr/all/summary/${id}`
        this.$router.push({ path: url })
      }
    },
  },
}
</script>
