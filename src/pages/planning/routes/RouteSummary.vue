<script>
import CustomModuleSummary from 'pages/custom-module/CustomModuleSummary'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import { isEmpty } from '@facilio/utils/validation'

export default {
  extends: CustomModuleSummary,
  computed: {
    moduleName() {
      return 'routes'
    },
  },
  methods: {
    async loadRecord(force = false) {
      try {
        let { moduleName, id } = this
        this.isLoading = true
        this.record = await this.modelDataClass.fetch({ moduleName, id, force })
        await this.getFormMeta()
      } catch (errorMsg) {
        this.$message.error(errorMsg)
      }
      this.isLoading = false
    },
    editRecord() {
      let { id, moduleName, $router } = this
      let editRoute = { name: 'edit-route', params: { id } }

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.EDIT) || {}
        if (!isEmpty(name)) {
          editRoute.name = name
        }
      }
      $router.push(editRoute)
    },
  },
}
</script>
