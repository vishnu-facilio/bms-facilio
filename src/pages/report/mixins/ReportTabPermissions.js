import { mapGetters, mapState } from 'vuex'
import { isWebTabsEnabled } from '@facilio/router'
export default {
  computed: {
    ...mapState({
      currentTab: state => state.webtabs.selectedTab,
    }),
    ...mapGetters('webtabs', ['tabHasPermission']),
    hasCreateEditPermission() {
      if (isWebTabsEnabled()) {
        let { currentTab, tabHasPermission } = this
        return tabHasPermission('CREATE_EDIT', currentTab)
      }
      return true
    },
    hasDeletePermission() {
      if (isWebTabsEnabled()) {
        let { currentTab, tabHasPermission } = this
        return tabHasPermission('DELETE', currentTab)
      }
      return true
    },
    hasViewPermission() {
      if (isWebTabsEnabled()) {
        let { currentTab, tabHasPermission } = this
        return tabHasPermission('VIEW', currentTab)
      }
      return true
    },
    hasSharePermission() {
      if (isWebTabsEnabled()) {
        let { currentTab, tabHasPermission } = this
        return tabHasPermission('SHARE', currentTab)
      }
      return true
    },
    hasSchedulePermission() {
      if (isWebTabsEnabled()) {
        let { currentTab, tabHasPermission } = this
        return tabHasPermission('SCHEDULE', currentTab)
      }
      return true
    },
    hasExportPermission() {
      if (isWebTabsEnabled()) {
        let { currentTab, tabHasPermission } = this
        return tabHasPermission('EXPORT', currentTab)
      }
      return true
    },
  },
}
