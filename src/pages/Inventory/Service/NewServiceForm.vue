<script>
import FormCreation from '@/base/FormCreation'
import FetchViewsMixin from '@/base/FetchViewsMixin'
import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'

export default {
  extends: FormCreation,
  mixins: [FetchViewsMixin],
  computed: {
    isV3Api() {
      return true
    },
    moduleName() {
      return 'service'
    },
    moduleDisplayName() {
      return 'Service'
    },
    isWidgetsSupported() {
      return true
    },
  },
  methods: {
    afterSaveHook(response) {
      let { moduleName } = this
      let { [moduleName]: data } = response
      let { id } = data || {}
      this.redirectToSummary(id)
    },
    async redirectToSummary(id) {
      let viewname = await this.fetchView(this.moduleName)

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule('service', pageTypes.OVERVIEW) || {}
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
          path: `/app/inventory/service/${viewname}/${id}/summary`,
        })
      }
    },
    async redirectToList() {
      let viewname = await this.fetchView(this.moduleName)

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule('service', pageTypes.LIST) || {}
        name &&
          this.$router.push({
            name,
            params: {
              viewname,
            },
          })
      } else {
        this.$router.push({
          name: 'service',
          params: {
            viewname,
          },
        })
      }
    },
  },
}
</script>
