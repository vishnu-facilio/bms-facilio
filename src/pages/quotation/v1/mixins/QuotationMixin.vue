<script>
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'
import FetchViewsMixin from '@/base/FetchViewsMixin'

export default {
  props: ['details', 'resizeWidget'],
  mixins: [FetchViewsMixin],
  data() {
    return {
      uomEnumMap: {},
      fieldsMap: {},
      isLoading: true,
      tenantUnitsFieldMap: {},
    }
  },
  computed: {
    isSummary() {
      // TEMP handling for webtabs check with harris to identify page type
      return (
        this.$route.name === 'quotationSummary' ||
        this.$route.path.includes('overview')
      )
    },
    canShowSubTotal() {
      let { details } = this
      return (
        !isEmpty(details.discountAmount) ||
        !isEmpty(details.taxSplitUp) ||
        !isEmpty(details.shippingCharges) ||
        !isEmpty(details.miscellaneousCharges) ||
        !isEmpty(details.adjustmentsCost)
      )
    },

    canShowTotalTax() {
      return (
        (this.$getProperty(this, 'details.taxSplitUp', []) || []).length > 1
      )
    },
  },
  mounted() {
    this.isLoading = true
    let promise = [this.loadQuoteModuleMeta(), this.loadLineItemsModuleMeta()]
    if (this.$org.id === 320) {
      promise.push(this.loadTenantUnitModuleMeta())
    }
    Promise.all(promise).then(() => {
      this.isLoading = false
      // if (this.isSummary && [320, 274, 559].includes(this.$org.id)) {
      //   this.autoResize()
      // }
      this.autoResize()
    })
  },
  methods: {
    async loadQuoteModuleMeta() {
      let { data, error } = await API.get('/v2/modules/meta/quote')
      if (error) {
        let { message } = error
        this.$messsage.error(
          message || 'Error Occured while fetching quote module meta'
        )
      } else {
        let fields = this.$getProperty(data, 'meta.fields', [])
        fields.forEach(field => {
          this.$setProperty(this.fieldsMap, `${field.name}`, field)
        })
      }
    },
    async loadTenantUnitModuleMeta() {
      let { data, error } = await API.get('/v2/modules/meta/tenantunit')
      if (error) {
        let { message } = error
        this.$messsage.error(
          message || 'Error Occured while fetching tenant unit meta'
        )
      } else {
        let fields = this.$getProperty(data, 'meta.fields', [])
        fields.forEach(field => {
          this.$setProperty(this.tenantUnitsFieldMap, `${field.name}`, field)
        })
      }
    },
    async routeToWOSummary(id) {
      if (this.isSummary) {
        if (isWebTabsEnabled()) {
          let viewname = await this.fetchView('workorder')
          let { name } =
            findRouteForModule('workorder', pageTypes.OVERVIEW) || {}

          name && this.$router.push({ name, params: { viewname, id } })
        } else {
          this.$router.push({
            path: `/app/wo/orders/summary/${id}`,
          })
        }
      }
    },
    async routeToSummary(details) {
      if (this.isSummary) {
        let { customerType, tenant, client, vendor } = details || {}
        let { id: tenantId } = tenant || {}
        let { id: clientId } = client || {}
        let { id: vendorId } = vendor || {}
        let routerDetails = {
          1: { moduleName: 'tenant', id: tenantId },
          2: { moduleName: 'client', id: clientId },
          4: { moduleName: 'vendors', id: vendorId },
        }

        if (isWebTabsEnabled()) {
          let { moduleName, id } = routerDetails[customerType] || {}
          let viewname = await this.fetchView(moduleName)
          if (isEmpty(viewname)) {
            viewname = 'all'
          }
          let { name } =
            findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}

          name && this.$router.push({ name, params: { viewname, id } })
        } else {
          if (customerType === 1 && tenantId) {
            this.$router.push({
              path: `/app/tm/tenants/all/${details.tenant.id}/overview`,
            })
          } else if (customerType === 2 && clientId) {
            this.$router.push({
              path: `/app/cl/client/all/${details.client.id}/overview`,
            })
          }
        }
      }
    },
    async loadLineItemsModuleMeta() {
      let { data, error } = await API.get('/v2/modules/meta/quotelineitems')
      if (error) {
        let { message } = error
        this.$messsage.error(
          message || 'Error Occured while fetching quote line items module meta'
        )
      } else {
        let fields = this.$getProperty(data, 'meta.fields', [])
        let uomField = fields.filter(field => field.name === 'unitOfMeasure')
        if (!isEmpty(uomField)) {
          this.uomEnumMap = this.$getProperty(uomField, [0, 'enumMap'], {})
        }
      }
    },
    autoResize() {
      this.$nextTick(() => {
        setTimeout(() => {
          if (!isEmpty(this.$refs['preview-container'])) {
            let height = this.$refs['preview-container'].scrollHeight + 60
            let width = this.$refs['preview-container'].scrollWidth
            if (this.resizeWidget) {
              this.resizeWidget({ height, width })
            }
          }
        }, 100)
      })
    },
  },
}
</script>
