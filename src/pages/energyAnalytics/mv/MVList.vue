<script>
import mvList from 'src/pages/energy/mv/MVList.vue'
import { isWebTabsEnabled } from '@facilio/router'
export default {
  extends: mvList,
  data() {
    return {
      views: [
        {
          name: 'open',
          label: 'Open Projects',
          path: '/app/en/mv/open',
        },
        {
          name: 'closed',
          label: 'Closed Projects',
          path: '/app/en/mv/closed',
        },
      ],
    }
  },
  methods: {
    redirectToFormCreation() {
      if (isWebTabsEnabled()) {
        let { parentPath } = this
        let path = `${parentPath}/new`
        this.$router.push({
          path,
        })
      } else {
        this.$router.push({
          path: '/app/en/mv/project/new',
        })
      }
    },
    redirectToSummary(projectId) {
      if (isWebTabsEnabled()) {
        let { currentView = 'open', parentPath } = this
        let path = `${parentPath}/${currentView}/${projectId}/overview`
        this.$router.push({
          path,
        })
      } else {
        this.$router.push({
          name: 'mv-energy-project-summary',
          params: {
            id: projectId,
          },
        })
      }
    },
    editMVProject(projectId) {
      if (isWebTabsEnabled()) {
        let { parentPath } = this
        let path = `${parentPath}/edit/${projectId}`
        this.$router.push({
          path,
        })
      } else {
        this.$router.push({
          name: 'mv-energy-project-edit',
          params: {
            id: projectId,
          },
        })
      }
    },
  },
}
</script>
