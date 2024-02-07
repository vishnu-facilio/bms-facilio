<script>
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
export default {
  props: ['details', 'resizeWidget'],
  data() {
    return {
      uomEnumMap: {},
      fieldsMap: {},
      isLoading: true,
    }
  },
  computed: {
    isSummary() {
      return this.$route.name === 'poSummary'
    },
    canShowBillToShowAddress() {
      return (
        !isEmpty(this.details.vendor) ||
        this.isAddressFieldNotEmpty(this.details.billToAddress)
      )
    },
    canShowShipToShowAddress() {
      return (
        !isEmpty(this.details.storeRoom) ||
        this.isAddressFieldNotEmpty(this.details.shipToAddress)
      )
    },
  },
  mounted() {
    this.isLoading = true
    Promise.all([
      this.loadPurchaseOrderModuleMeta(),
      this.loadLineItemsModuleMeta(),
    ])
      .then(() => {
        this.isLoading = false
      })
      .then(() => {
        this.autoResize()
      })
  },
  methods: {
    autoResize() {
      this.$nextTick(() => {
        if (!isEmpty(this.$refs['preview-container'])) {
          let height = this.$refs['preview-container'].scrollHeight + 80
          let width = this.$refs['preview-container'].scrollWidth
          if (this.resizeWidget) {
            this.resizeWidget({ height, width })
          }
        }
      })
    },
    isAddressFieldNotEmpty(value) {
      return (
        value &&
        (value.city ||
          value.state ||
          value.street ||
          value.country ||
          value.zip)
      )
    },
    async loadLineItemsModuleMeta() {
      let { data, error } = await API.get(
        '/v2/modules/meta/purchaseorderlineitems'
      )
      if (error) {
        let { message } = error
        this.$messsage.error(
          message ||
            this.$t(
              'common._common.error_occured_while_fetching_purchase_order_line_items_module_meta'
            )
        )
      } else {
        let fields = this.$getProperty(data, 'meta.fields', [])
        let uomField = fields.filter(field => field.name === 'unitOfMeasure')
        if (!isEmpty(uomField)) {
          this.uomEnumMap = this.$getProperty(uomField, [0, 'enumMap'], {})
        }
      }
    },
    async loadPurchaseOrderModuleMeta() {
      let { data, error } = await API.get('/v2/modules/meta/purchaseorder')
      if (error) {
        let { message } = error
        this.$messsage.error(
          message ||
            this.$t(
              'common._common.error_occured_while_fetching_purchase_request_module_meta'
            )
        )
      } else {
        let fields = this.$getProperty(data, 'meta.fields', [])
        fields.forEach(field => {
          this.$setProperty(this.fieldsMap, `${field.name}`, field)
        })
      }
    },
    routeToWOSummary(id) {
      if (this.isSummary) {
        this.$router.push({
          path: `/app/wo/orders/summary/${id}`,
        })
      }
    },
  },
}
</script>
