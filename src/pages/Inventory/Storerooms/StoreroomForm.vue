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
  extends: CustomModulesCreation,
  props: ['viewname'],
  mixins: [FetchViewsMixin],
  computed: {
    isEdit() {
      return this.getId ? true : false
    },
    getId() {
      let { $route } = this
      let { params } = $route || {}
      let { id } = params || {}
      return id || null
    },
    title() {
      let { moduleDisplayName } = this
      return this.isEdit
        ? `Edit ${moduleDisplayName}`
        : `Create ${moduleDisplayName}`
    },
    isWidgetsSupported() {
      return true
    },
    isV3Api() {
      return true
    },
    moduleName() {
      return 'storeRoom'
    },
    moduleDisplayName() {
      return 'Store Room'
    },
  },
  methods: {
    afterSaveHook(response) {
      let { error, [this.moduleName]: record } = response || {}

      if (isEmpty(error)) this.redirectToSummary(record.id)
    },
    async redirectToList() {
      if (this.isEdit) {
        await this.redirectToSummary(this.getId)
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
            name: 'storerooms',
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
          name: 'storeroomSummary',
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
