<script>
import CatalogRequest from 'src/PortalTenant/catalog/CatalogRequest'
import { findRouteForTab, pageTypes, getApp } from '@facilio/router'
import { mapState } from 'vuex'

const { CATALOG_LIST } = pageTypes

export default {
  extends: CatalogRequest,
  computed: {
    ...mapState({
      currentTab: state => state.webtabs.selectedTab,
    }),
  },
  methods: {
    redirectToCatalogList(groupId) {
      let { currentTab } = this
      let appName = getApp().linkName || 'employee'
      let route =
        findRouteForTab(currentTab.id, {
          pageType: CATALOG_LIST,
        }) || {}

      route &&
        this.$router.push({
          path: `/${appName}/${route.path}`,
          meta: {
            groupId,
          },
        })
    },
  },
}
</script>
