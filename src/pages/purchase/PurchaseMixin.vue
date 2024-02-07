<script>
import FormMixin from '@/mixins/FormMixin'
import { isEmpty, isArray } from '@facilio/utils/validation'
import { API } from '@facilio/api'
export default {
  mixins: [FormMixin],
  data() {
    return {
      storeRoomCheck: false,
      displayNameMap: {
        pr: 'Purchase Request',
        po: 'Purchase Order',
      },
      moduleNameMap: {
        pr: 'purchaserequest',
        po: 'purchaseorder',
      },
    }
  },
  methods: {
    async submitForm(data) {
      let paramData = this.$helpers.cloneObject(data)
      let param = {
        data: paramData,
      }
      if (
        !isEmpty(this.conversionId) &&
        !isArray(JSON.parse(this.conversionId)) &&
        !this.vendorQuote
      ) {
        param['id'] = this.conversionId
      } else if (this.prIds) {
        param['prIds'] = JSON.parse(this.conversionId)
        param.data['prIds'] = JSON.parse(this.conversionId)
      } else if (this.vendorQuote && this.requestForQuotation) {
        param.data['vendorQuote'] = { id: Number(this.conversionId) }
        param.data['requestForQuotation'] = {
          id: Number(this.requestForQuotation),
        }
      }
      let id
      if (isEmpty(param.id)) {
        this.isSaving = true
        let { purchaseorder, purchaserequest, error } = await API.createRecord(
          this.moduleName,
          param
        )
        if (error) {
          let { message = 'Error Occured while Saving' } = error
          this.$message.error(message)
        } else {
          id = (purchaseorder || {}).id || (purchaserequest || {}).id
          this.$message.success(`Record Created Successfully`)
          this.isFormSaved = true
          this.redirectToSummary(id)
        }
        this.isSaving = false
      } else {
        this.isSaving = true
        let { purchaseorder, purchaserequest, error } = await API.updateRecord(
          this.moduleName,
          param
        )
        if (error) {
          let { message = 'Error Occured while Updating' } = error
          this.$message.error(message)
        } else {
          id = (purchaseorder || {}).id || (purchaserequest || {}).id
          this.$message.success(`Record Updated successfully`)
          this.isFormSaved = true
          this.redirectToSummary(id)
        }
        this.isSaving = false
      }
    },
  },
}
</script>
