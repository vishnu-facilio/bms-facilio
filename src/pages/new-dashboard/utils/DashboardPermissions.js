import { mapGetters, mapState } from 'vuex'
import { isWebTabsEnabled } from '@facilio/router'
export default {
  computed: {
    ...mapState({
      currentTab: state => state.webtabs.selectedTab,
    }),
    ...mapGetters('webtabs', ['tabHasPermission']),

    hasViewPermission() {
      if (isWebTabsEnabled()) {
        let { currentTab, tabHasPermission } = this
        return tabHasPermission('VIEW', currentTab)
      }
      return this.$hasPermission('dashboard:READ')
    },
    hasCreatePermission() {
      if (isWebTabsEnabled()) {
        let { currentTab, tabHasPermission } = this
        return tabHasPermission('CREATE', currentTab)
      }
      return this.$hasPermission(`dashboard:CREATE`)
    },
    hasDeletePermission() {
      if (isWebTabsEnabled()) {
        let { currentTab, tabHasPermission } = this
        return tabHasPermission('DELETE', currentTab)
      }
      return this.$hasPermission(`dashboard:DELETE`)
    },
    hasDashboardEditPermission() {
      if (isWebTabsEnabled()) {
        let { currentTab, tabHasPermission } = this
        return tabHasPermission('EDIT', currentTab)
      }
      return this.$hasPermission(`dashboard:UPDATE`)
    },
    hasSharePermission() {
      if (isWebTabsEnabled()) {
        let { currentTab, tabHasPermission } = this
        return tabHasPermission('SHARE', currentTab)
      }
      return this.$hasPermission(`dashboard:SHARE_DASHBOARD`)
    },
  },
}
