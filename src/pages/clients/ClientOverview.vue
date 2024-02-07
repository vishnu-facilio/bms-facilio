<script>
import CustomModuleSummary from 'src/pages/custom-module/CustomModuleSummary.vue'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
export default {
  extends: CustomModuleSummary,
  methods: {
    editRecord() {
      let { moduleName, id } = this

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.EDIT) || {}
        name && this.$router.push({ name, params: { id } })
      } else {
        this.$router.push({ name: 'client-edit', params: { moduleName, id } })
      }
    },
  },
}
</script>
