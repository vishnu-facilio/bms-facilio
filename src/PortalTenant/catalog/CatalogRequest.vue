<script>
import CatalogRequest from 'pages/catalog/CatalogRequest'
import { isObject } from '@facilio/utils/validation'
import { mapState } from 'vuex'

import {
  findRouteForTab,
  findRouteForModule,
  pageTypes,
  getApp,
} from '@facilio/router'

const { LIST, OVERVIEW, CATALOG_LIST } = pageTypes

const pageHash = {
  workorder: OVERVIEW,
  vendors: OVERVIEW,
  visitorlog: LIST,
  workpermit: OVERVIEW,
  serviceRequest: OVERVIEW,
}

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
      let appName = getApp().linkName || 'tenant'
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
    redirectToRecord(record) {
      let pageType = this.$getProperty(pageHash, this.module)

      let id = Array.isArray(record)
        ? record[0].id
        : isObject(record)
        ? record.id
        : null

      let route = findRouteForModule(this.module, pageType)

      if (route) {
        this.$router.push({
          name: route.name,
          params: {
            viewname: 'all',
            id,
          },
        })
      } else {
        this.redirectToCatalogList()
      }
    },
  },
}
</script>
<style lang="scss" scoped>
.service-catalog-form .section-container {
  padding-top: 0 !important;
  margin-top: 0 !important;
}
</style>
