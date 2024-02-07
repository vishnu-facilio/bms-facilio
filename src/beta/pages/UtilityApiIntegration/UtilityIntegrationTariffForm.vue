<script>
import DsmForm from 'src/pages/custom-module/DSMFormCreation.vue'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
export default {
  extends: DsmForm,
  methods: {
    afterSaveHook(response) {
      let { moduleName } = this
      let { [moduleName]: data } = response
      this.redirectToList()
    },
    redirectToList() {
      let { moduleName } = this

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.LIST) || {}

        if (name) {
          this.$router.push({ name })
        }
      } else {
        this.$router
          .push({ name: 'utilityTariffCreate', params: { moduleName } })
          .catch(() => {})
      }
    },
  },
}
</script>
