<script>
import CustomModulesCreation from 'src/pages/custom-module/CustomModulesCreation'
import FetchViewsMixin from '@/base/FetchViewsMixin'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  extends: CustomModulesCreation,
  mixins: [FetchViewsMixin],
  computed: {
    isEdit() {
      return this.moduleDataId ? true : false
    },
    title() {
      let { moduleDisplayName } = this
      return this.isEdit
        ? `Edit ${moduleDisplayName}`
        : `Create ${moduleDisplayName}`
    },
    moduleName() {
      return 'itemTypes'
    },
    moduleDisplayName() {
      return 'Item Types'
    },
  },
  methods: {
    async redirectToList() {
      if (this.isEdit) {
        await this.redirectToSummary(this.moduleDataId)
      } else {
        if (isWebTabsEnabled()) {
          let { name } =
            findRouteForModule(this.moduleName, pageTypes.LIST) || {}
          name &&
            this.$router.push({
              name,
              query: this.$route.query,
            })
        } else {
          this.$router.push({
            name: 'itemtypes',
          })
        }
      }
    },
    async redirectToSummary(id) {
      let currentView = await this.fetchView(this.moduleName)
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
          name: 'itemtypeSummary',
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
