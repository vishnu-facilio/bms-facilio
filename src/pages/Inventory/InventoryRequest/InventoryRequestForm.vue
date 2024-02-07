<script>
import FormCreation from '@/base/FormCreation'
import FetchViewsMixin from '@/base/FetchViewsMixin'
import { isEmpty, isArray, isNumber } from '@facilio/utils/validation'
import { mapState } from 'vuex'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  extends: FormCreation,
  mixins: [FetchViewsMixin],
  data() {
    return {}
  },
  computed: {
    ...mapState({
      account: state => state.account,
    }),
    moduleDataId() {
      let { $route } = this
      let {
        query: { recordIds },
        params: { id },
      } = $route
      return Number(id) || recordIds
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
      return 'inventoryrequest'
    },
    moduleDisplayName() {
      return 'Inventory Request'
    },
  },
  methods: {
    modifyFieldPropsHook(field) {
      let { $route } = this
      let {
        query: { requestedFromWorkOrder, woId, woLocalId: localId },
      } = $route
      woId = !isEmpty(woId) ? parseInt(woId) : null
      let { moduleDataId, account } = this
      if (requestedFromWorkOrder && !isEmpty(woId) && !isEmpty(localId)) {
        let { user } = account || {}
        let { id: userId } = user || {}
        let fieldValueObj = {
          name: {
            ...field,
            value: `${this.$t(
              'common.inventory.required_inventory_items_for_wo'
            )}${localId}`,
          },
          requestedBy: { ...field, value: userId, isDisabled: true },
          requestedFor: { ...field, value: userId },
          workorder: { ...field, value: woId, isDisabled: true },
        }
        if (isEmpty(moduleDataId)) {
          let { name } = field || {}
          return fieldValueObj[name] || field
        }
      } else {
        let fieldValueObj = {
          requestedBy: { ...field, isDisabled: true },
          workorder: { ...field, isDisabled: true },
        }
        if (!isEmpty(moduleDataId)) {
          let { name } = field || {}
          return fieldValueObj[name] || field
        }
      }
    },
    afterSerializeHook({ data }) {
      let { inventoryrequestlineitems: lineItems } = data || []

      if (!isEmpty(lineItems) && isArray(lineItems)) {
        lineItems = lineItems
          .filter(
            item =>
              !isEmpty(item.quantity) &&
              ((item.inventoryType === 1 && item.itemType?.id) ||
                (item.inventoryType === 2 && item.toolType?.id))
          )
          .map(item => {
            let { inventoryType } = item || {}
            if (inventoryType === 1) {
              return { ...item, toolType: null }
            } else {
              return { ...item, itemType: null }
            }
          })
        this.$setProperty(
          data,
          'inventoryrequestlineitems',
          !isEmpty(lineItems) ? lineItems : null
        )
      }
      return data
    },
    afterSaveHook(response) {
      let { moduleName } = this
      let { [moduleName]: data } = response
      let { id } = data || {}

      if (!isEmpty(id)) {
        this.redirectToSummary(id)
      }
    },
    async redirectToList() {
      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForModule('inventoryrequest', pageTypes.LIST) || {}
        name &&
          this.$router.push({
            name,
          })
      } else {
        this.$router.push({
          path: '/app/inventory/inventoryrequest',
        })
      }
    },
    async redirectToSummary(id) {
      let viewname = await this.fetchView(this.moduleName)
      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForModule(this.moduleName, pageTypes.OVERVIEW) || {}
        name &&
          this.$router.push({
            name,
            params: {
              viewname,
              id,
            },
          })
      } else {
        this.$router.push({
          path: `/app/inventory/inventoryrequest/${viewname}/${id}/overview`,
        })
      }
    },
  },
}
</script>
