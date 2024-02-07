<script>
import FormCreation from '@/base/FormCreation'
import PurchaseMixin from 'pages/purchase/PurchaseMixin'
import {
  isNullOrUndefined,
  isEmpty,
  isArray,
  areValuesEmpty,
  isNumber,
} from '@facilio/utils/validation'
import { API } from '@facilio/api'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  extends: FormCreation,
  props: ['viewname'],
  mixins: [PurchaseMixin],
  data() {
    return {}
  },
  computed: {
    isEdit() {
      let { conversionId } = this
      return !isEmpty(conversionId)
    },
    conversionId() {
      let { $route } = this
      let {
        query: { recordIds, vendorQuote },
        params: { id },
      } = $route
      return Number(id) || recordIds || vendorQuote
    },
    prIds() {
      let { $route } = this
      let {
        query: { recordIds },
      } = $route
      return recordIds
    },
    requestForQuotation() {
      let { $route } = this
      let {
        query: { requestForQuotation },
      } = $route
      return requestForQuotation
    },
    vendorQuote() {
      let { $route } = this
      let {
        query: { vendorQuote },
      } = $route
      return vendorQuote
    },
    title() {
      let { moduleDisplayName, moduleDataId } = this
      return !isNumber(moduleDataId)
        ? `Create ${moduleDisplayName}`
        : `Edit ${moduleDisplayName}`
    },
    isWidgetsSupported() {
      return true
    },
    isV3Api() {
      return true
    },
    moduleName() {
      return 'purchaseorder'
    },
    moduleDisplayName() {
      return 'Purchase Order'
    },
  },
  methods: {
    async init() {
      let { conversionId, moduleName } = this
      this.isLoading = true
      await this.loadFormsList(moduleName)
      if (!isEmpty(conversionId)) {
        await this.loadModuleData({
          moduleName,
          conversionId,
        })
      }
      this.setInitialForm()

      this.isLoading = false
    },
    async loadModuleData({ conversionId, moduleName }) {
      let url = `/v2/module/data/${conversionId}?moduleName=${moduleName}`
      let param = { id: conversionId }
      if (!isEmpty(this.prIds) && isArray(JSON.parse(this.prIds))) {
        url = `/v2/purchaseorder/convertPrToPo`
        param = { recordIds: JSON.parse(this.prIds) }
      } else if (!isEmpty(this.vendorQuote)) {
        url = `/v3/requestForQuotation/convertVendorQuoteToPo`
        param = {
          vendorQuote: this.vendorQuote,
        }
      } else {
        url = `v3/modules/data/summary?moduleName=purchaseorder`
      }
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

        if (!isEmpty(summaryData.lineItems)) {
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
        this.moduleData = summaryData
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
            this.storeRoomCheck = true
            if (areValuesEmpty(lineItem.itemType)) {
              lineItem.itemType = null
            }
          } else if (lineItem.inventoryType === 2) {
            lineItem.itemType = null
            lineItem.service = null
            this.storeRoomCheck = true
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
      if (this.storeRoomCheck) {
        if (isNullOrUndefined((serializedData.storeRoom || {}).id)) {
          this.$message.error('Please Enter Storeroom')
          return
        }
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
          name: 'purchaseorder',
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
        let url = `/app/purchase/po/all/summary/${id}`
        this.$router.push({ path: url })
      }
    },
  },
}
</script>
