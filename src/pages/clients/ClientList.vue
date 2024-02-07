<script>
import CommonModuleList from 'src/newapp/list/CommonModuleList.vue'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
export default {
  extends: CommonModuleList,
  computed: {
    parentPath() {
      return `/app/cl/client/`
    },
  },
  methods: {
    redirectToOverview(id) {
      let { moduleName, viewname } = this
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}
        let route = {
          name,
          params: {
            viewname,
            id,
          },
          query: this.$route.query,
        }
        return name && route
      } else {
        return {
          name: 'clientSummary',
          params: {
            viewname,
            id,
          },
          query: this.$route.query,
        }
      }
    },
    editModule(row) {
      let { moduleName } = this
      let { id } = row
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.EDIT) || {}

        name &&
          this.$router.push({
            name,
            params: {
              id,
            },
          })
      } else {
        this.$router.push({
          name: 'client-edit',
          params: {
            id,
          },
        })
      }
    },
    redirectToFormCreation() {
      let { moduleName } = this
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.CREATE) || {}

        name &&
          this.$router.push({
            name,
          })
      } else {
        this.$router.push({
          name: 'client-new',
        })
      }
    },
    openList() {
      let { viewname } = this

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(this.moduleName, pageTypes.LIST) || {}
        name &&
          this.$router.push({
            name,
            params: {
              viewname,
            },
            query: this.$route.query,
          })
      } else {
        this.$router.push({
          name: 'client-list',
          params: { viewname },
          query: this.$route.query,
        })
      }
    },
  },
}
</script>
