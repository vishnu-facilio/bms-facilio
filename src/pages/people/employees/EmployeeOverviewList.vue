<script>
import CustomModuleOverviewList from 'pages/base-module-v2/ModuleListOverview'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
export default {
  extends: CustomModuleOverviewList,
  data() {
    return {}
  },
  computed: {
    moduleName() {
      return 'employee'
    },
    moduleRouteName() {
      return 'employee'
    },
    moduleStoreName() {
      return 'employee'
    },
    customModuleList() {
      return this.$store.state.employee.employess
    },
    searchQuery() {
      return this.$store.state[this.moduleStoreName].quickSearchQuery
    },
    canLoadMore() {
      return this.$store.state[this.moduleStoreName].canLoadMore
    },
  },
  methods: {
    initial() {
      this.$store.dispatch('view/clearViews')
      this.loadData()
      let params = {
        id: this.id,
      }
      let promises = [
        this.loadViews(),
        // this.$store.dispatch(`${this.moduleStoreName}/fetchTenants`, params),
      ]
      Promise.all(promises).then(() => {
        this.loadModuleMeta(this.moduleName)
      })
    },
    getLink(id) {
      if (id === null) return
      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForModule(this.moduleName, pageTypes.OVERVIEW) || {}
        name &&
          this.$router.push({
            name,
            params: {
              id: id,
              viewname: 'all',
            },
          })
      } else {
        this.$router.push({
          path: `/app/pl/${this.moduleRouteName}/${this.currentView}/${id}/summary`,
        })
      }
    },
    switchView(view) {
      let filterIndex = this.views.findIndex(
        view => view.name === 'filteredList'
      )
      if (filterIndex !== -1) {
        this.views.splice(filterIndex, 1)
      }
      let url =
        '/app/pl/employee/' +
        view.name +
        '/' +
        this.$route.params.id +
        '/summary'
      this.loadModuleMeta('employee').then(() => {
        if (isWebTabsEnabled()) {
          let { name } =
            findRouteForModule(this.moduleName, pageTypes.OVERVIEW) || {}
          name &&
            this.$router.push({
              name,
              params: {
                id: this.$route.params.id,
                viewname: view.name,
              },
            })
        } else {
          this.$router.replace({ path: url })
        }
        this.loadData()
      })
      this.toggle = false
    },
    loadData(loadMore) {
      let { currentView, page } = this
      this.page = loadMore ? this.page : 1
      let queryObj = {
        viewname: currentView,
        page: page,
        filters: this.$route.query.search
          ? JSON.parse(this.$route.query.search)
          : '',
        // search: this.searchQuery,
        includeParentFilter: currentView === 'filteredList' ? true : false,
      }
      loadMore ? (this.fetchingMore = true) : (this.loading = true)
      this.$store
        .dispatch(`${this.moduleStoreName}/fetchEmployeesList`, queryObj)
        .then(() => {
          this.loading = false
          this.loadingLists = false
          this.fetchingMore = false
          this.page++
        })
        .catch(error => {
          if (error) {
            this.loading = false
            this.fetchingMore = false
          }
        })
    },
    quickSearch() {
      this.$store.dispatch(
        `${this.moduleStoreName}/updateSearchQuery`,
        this.quickSearchQuery
      )
    },
    back() {
      let {
        $route: { query },
        currentView,
      } = this
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(this.moduleName, pageTypes.LIST) || {}
        name &&
          this.$router.push({
            name,
            params: {
              viewname: currentView,
            },
            query,
          })
      } else {
        let url =
          '/app/pl/employee/' +
          (currentView === 'filteredList' ? 'all-employees' : currentView)
        this.$router.push({ path: url, query: query })
      }
    },
  },
}
</script>
