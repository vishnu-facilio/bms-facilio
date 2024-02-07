<script>
import CustomModulesCreation from 'src/pages/custom-module/CustomModulesCreation'
import FetchViewsMixin from '@/base/FetchViewsMixin'
import { isEmpty } from '@facilio/utils/validation'
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
    moduleName() {
      return 'vendorQuotes'
    },
    moduleDisplayName() {
      return 'Vendor Quotes'
    },
  },
  methods: {
    afterSaveHook(response) {
      let { moduleName } = this || {}
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
          name: 'vendorQuotes',
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
          name: 'vendorQuotesSummary',
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
