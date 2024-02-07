<script>
import CustomModulesCreation from 'src/pages/custom-module/CustomModulesCreation'
import FetchViewsMixin from '@/base/FetchViewsMixin'
import { isEmpty } from '@facilio/utils/validation'
import { RfqModuleData } from './RfqModuleData'
import { isNumber } from '@facilio/utils/validation'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
export default {
  name: 'RequestForQuotationForm',
  extends: CustomModulesCreation,
  props: ['viewname'],
  mixins: [FetchViewsMixin],
  computed: {
    modelDataClass() {
      return RfqModuleData
    },
    moduleName() {
      return 'requestForQuotation'
    },
    moduleDisplayName() {
      return 'Request For Quotation'
    },
    prIds() {
      let { $route } = this
      let {
        query: { recordIds },
      } = $route
      return recordIds
    },
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
    loadDataManually() {
      let { prIds } = this || {}
      return !isEmpty(prIds)
    },
    customProps() {
      let { prIds } = this || {}
      return { prIds }
    },
  },
  methods: {
    afterSerializeHook({ data }) {
      if (!isEmpty(this.prIds)) {
        data['recordIds'] = JSON.parse(this.prIds)
      }
      return data
    },
    afterSaveHook(response) {
      let { moduleName } = this
      let { error, [moduleName]: record } = response || {}

      if (isEmpty(error)) this.redirectToSummary(record)
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
          name: 'requestForQuotation',
        })
      }
    },
    async redirectToSummary({ id }) {
      let { moduleName } = this
      let currentView = await this.fetchView(moduleName)

      if (isWebTabsEnabled()) {
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
        this.$router.push({
          name: 'requestForQuotationSummary',
          params: {
            viewname: currentView,
            id,
          },
        })
      }
    },
  },
}
</script>
