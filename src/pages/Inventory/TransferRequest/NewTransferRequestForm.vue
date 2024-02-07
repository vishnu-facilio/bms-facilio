<script>
import CustomModulesCreation from 'src/pages/custom-module/CustomModulesCreation'
import FetchViewsMixin from '@/base/FetchViewsMixin'
import { TrModuleData } from './TrModuleData'
import { isEmpty } from '@facilio/utils/validation'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  name: 'TransferRequestCreation',
  extends: CustomModulesCreation,
  props: ['viewname'],
  mixins: [FetchViewsMixin],
  computed: {
    modelDataClass() {
      return TrModuleData
    },
    moduleName() {
      return 'transferrequest'
    },
    moduleDisplayName() {
      return 'Transfer Request'
    },
  },
  methods: {
    afterSerializeHook({ data }) {
      if (isEmpty(data['isStaged'])) {
        data['isStaged'] = false
      }
      if (isEmpty(data['isShipped'])) {
        data['isShipped'] = false
      }
      if (isEmpty(data['isCompleted'])) {
        data['isCompleted'] = false
      }
      return data
    },
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
          name: 'transferrequestList',
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
          name: 'transferrequestSummary',
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
