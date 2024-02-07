<script>
import ViewSidePanel from 'src/pages/views/ViewsSidePanel.vue'
import { findRouteForTab, pageTypes, isWebTabsEnabled } from '@facilio/router'

export default {
  extends: ViewSidePanel,
  methods: {
    loadViewManager() {
      let { moduleName, $route } = this
      let { query } = $route

      if (isWebTabsEnabled()) {
        let { name } = findRouteForTab(pageTypes.TIMELINE_VIEW_MANAGER, {
          moduleName,
        })

        name && this.$router.push({ name, query })
      } else {
        this.$router.push({ name: 'resource-scheduler-viewmanager', query })
      }
    },
  },
}
</script>
