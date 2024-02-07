<script>
import ModuleListView from 'src/newapp/viewmanager/ModuleListView'
import { isWebTabsEnabled } from '@facilio/router'

export default {
  extends: ModuleListView,
  methods: {
    openViewCreation(viewname = null) {
      let { moduleName, appId, viewCloneAppId } = this
      let name = isWebTabsEnabled()
        ? 'portfolio-view-creation-form-webtab'
        : 'portfolio-view-creation-form'
      if (viewname && viewCloneAppId) {
        this.$router.push({
          name,
          params: { moduleName },
          query: { appId, viewCloneAppId, viewname },
        })
      } else if (viewname) {
        this.$router.push({
          name,
          params: { moduleName, viewname },
          query: { appId },
        })
      } else {
        this.$router.push({
          name,
          params: { moduleName },
          query: { appId },
        })
      }
    },
  },
}
</script>
