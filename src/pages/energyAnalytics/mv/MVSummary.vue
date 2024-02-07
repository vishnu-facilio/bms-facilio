<script>
import mvSummary from 'src/pages/energy/mv/MVSummary.vue'
import { isWebTabsEnabled } from '@facilio/router'
export default {
  extends: mvSummary,
  methods: {
    redirectToMvList() {
      if (isWebTabsEnabled()) {
        let { parentPath } = this
        let path = `${parentPath}/open`
        this.$router.push({
          path,
        })
      } else {
        this.$router.push({
          path: '/app/en/mv/open',
        })
      }
    },
    editMVProject() {
      if (isWebTabsEnabled()) {
        let { mvId, parentPath } = this
        let path = `${parentPath}/edit/${mvId}`
        this.$router.push({
          path,
        })
      } else {
        this.$router.push({
          name: 'mv-energy-project-edit',
          params: {
            id: this.mvId,
          },
        })
      }
    },
  },
}
</script>
