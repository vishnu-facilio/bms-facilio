<script>
import CustomModulesCreation from 'src/pages/custom-module/CustomModulesCreation'
import { TrModuleData } from '../TrModuleData'
import FetchViewsMixin from '@/base/FetchViewsMixin'
import { isEmpty } from '@facilio/utils/validation'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  extends: CustomModulesCreation,
  props: ['viewname'],
  mixins: [FetchViewsMixin],
  data() {
    return {}
  },
  computed: {
    modelDataClass() {
      return TrModuleData
    },
    title() {
      let { moduleDisplayName } = this
      return `Edit ${moduleDisplayName}`
    },
    moduleName() {
      return 'transferrequestshipment'
    },
    moduleDisplayName() {
      return 'Transfer Request Shipment'
    },
  },
  methods: {
    afterSaveHook(response) {
      let { error, [this.moduleName]: record } = response || {}

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
          name: 'trShipmentList',
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
          name: 'trShipmentSummary',
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
