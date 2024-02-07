<script>
import ModuleListView from 'src/newapp/viewmanager/ModuleListView.vue'
import FolderCreation from './FolderCreation'
import { findRouteForTab, pageTypes, isWebTabsEnabled } from '@facilio/router'

export default {
  extends: ModuleListView,
  components: { FolderCreation },
  methods: {
    async loadGroupViewsList() {
      let { moduleName, appId, currentSelectedApp } = this
      let data = {
        moduleName,
        appId: appId || currentSelectedApp,
        restrictPermissions: true,
        groupType: 2,
        viewType: 2,
        fromBuilder: true,
      }

      this.isLoading = true
      await this.$store.dispatch('view/loadGroupViews', data)
      this.isLoading = false
    },
    openViewCreation(viewname = null) {
      let { moduleName, appId, viewCloneAppId } = this

      let routeName
      if (isWebTabsEnabled()) {
        let { name } = findRouteForTab(pageTypes.TIMELINE_CREATE, {
          moduleName,
        })
        routeName = name
      }

      if (viewname && viewCloneAppId) {
        if (!isWebTabsEnabled()) routeName = 'timeline-view-create'

        routeName &&
          this.$router.push({
            name: routeName,
            params: { moduleName },
            query: { appId, viewCloneAppId, viewname },
          })
      } else if (viewname) {
        if (!isWebTabsEnabled()) routeName = 'timeline-view-edit'

        routeName &&
          this.$router.push({
            name: routeName,
            params: { viewname },
            query: { appId },
          })
      } else {
        if (!isWebTabsEnabled()) routeName = 'timeline-view-create'
        routeName && this.$router.push({ name: routeName, query: { appId } })
      }
    },
  },
}
</script>
