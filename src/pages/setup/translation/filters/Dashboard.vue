<template>
  <div>
    <div
      v-if="!isEmpty(activeDashboardFolderId) && !isEmpty(activeDashboardId)"
      class="filter-list"
    >
      <div class="filter-start">
        <FilterDropdown
          v-if="!loadingDashboards"
          v-model="activeDashboardFolderId"
          :options="folderOptions"
        >
        </FilterDropdown>
      </div>

      <div class="filter">
        <FilterDropdown
          v-if="!isEmpty(dashboardOptions)"
          v-model="activeDashboardId"
          :options="dashboardOptions"
        >
        </FilterDropdown>
      </div>
      <div class="filter">
        <FilterDropdown
          v-if="!isEmpty(tabOptions)"
          v-model="activeTabId"
          :options="tabOptions"
        >
        </FilterDropdown>
      </div>
    </div>
    <div
      v-else
      class="filter-start loading-shimmer width120px height40 bR5"
    ></div>
  </div>
</template>

<script>
import { API } from '@facilio/api'
import FilterDropdown from './FilterDropdown.vue'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['moduleName', 'setFilter'],
  components: {
    FilterDropdown,
  },
  data() {
    return {
      dashboardFolders: [],
      folderOptions: [],
      dashboardOptions: [],
      tabOptions: [],
      activeDashboardFolderId: null,
      activeDashboardId: null,
      activeTabId: null,
      loadingDashboards: true,
    }
  },
  created() {
    this.loadDashboardFolders()
  },
  watch: {
    activeDashboardFolderId: function() {
      const currentFolder = this.dashboardFolders.find(folder => {
        return folder.id === this.activeDashboardFolderId
      })
      let { dashboards } = currentFolder || {}
      dashboards = dashboards || []
      dashboards = dashboards.map(dashboard => {
        const { id, dashboardName } = dashboard
        return {
          id,
          label: dashboardName,
        }
      })
      const [firstDashboard] = dashboards
      const { id } = firstDashboard || {}
      this.activeDashboardId = id
      this.dashboardOptions = dashboards
    },

    activeDashboardId: function() {
      const { activeDashboardFolderId, activeDashboardId } = this
      const currentFolder = this.dashboardFolders.find(folder => {
        return folder.id === activeDashboardFolderId
      })
      const { dashboards } = currentFolder || {}
      const currentDashboard = dashboards
        ? dashboards.find(dashboard => {
            return dashboard.id === activeDashboardId
          })
        : {}
      const { dashboardTabContexts } = currentDashboard || {}
      if (isEmpty(dashboardTabContexts) && !isEmpty(activeDashboardId)) {
        this.setFilter({
          loading: false,
          query: {
            dashboardId: activeDashboardId,
          },
        })
      }
      let tabs = []
      dashboardTabContexts &&
        dashboardTabContexts.forEach(tab => {
          const { id, name, childTabs } = tab
          childTabs.forEach(childTab => {
            const { id, name } = childTab
            tabs = [...tabs, { id, label: name }]
          })
          tabs = [...tabs, { id, label: name }]
        })
      const [firstTab] = tabs
      const { id } = firstTab || {}
      this.activeTabId = id
      this.tabOptions = tabs
    },

    activeTabId: function() {
      const { activeTabId: id } = this
      if (!isEmpty(id)) {
        this.setFilter({
          loading: false,
          query: {
            dashboardTabId: id,
          },
        })
      }
    },
  },
  computed: {},
  methods: {
    isEmpty,
    async loadDashboardFolders() {
      this.loadingStateFlows = true
      const { error, data } = await API.get('/dashboardWithFolder')
      if (error) {
        this.$message.error(error.message || 'Error Occured')
        this.setFilter({
          loading: false,
          query: null,
        })
      } else {
        let { dashboardFolders } = data || {}
        dashboardFolders = dashboardFolders || []
        this.dashboardFolders = dashboardFolders

        const [firstFolder] = dashboardFolders
        const { id } = firstFolder || {}
        this.activeDashboardFolderId = id
        this.folderOptions = dashboardFolders.map(folder => {
          const { id, moduleName } = folder
          return {
            id,
            label: moduleName,
          }
        })
        if (isEmpty(dashboardFolders)) {
          this.setFilter({
            loading: false,
            query: null,
          })
        }
      }
      this.loadingDashboards = false
    },
  },
}
</script>

<style scoped>
.filter-list {
  display: flex;
}
.filter-start {
  margin: 0 5px 0 43px;
  padding: 10px 0;
}

.filter {
  margin: 0 5px;
  padding: 10px 0;
}
</style>
