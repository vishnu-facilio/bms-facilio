<script>
import OverviewLayout from 'pages/assets/asset/v1/AssetOverviewLayout.vue'
export default {
  extends: OverviewLayout,
  methods: {
    back() {
      let url =
        '/app/en/energymeter/' +
        (this.$route.params.viewname === 'filteredassets'
          ? 'all'
          : this.$route.params.viewname)
      this.$router.push({ path: url, query: this.$route.query })
    },
    getAssetLink(id) {
      if (id === null) return
      this.$router.push({
        path: `/app/en/energymeter/${this.currentView}/${id}/overview`,
        query: this.$route.query,
      })
    },
    loadViews() {
      let param = {
        moduleName: 'energymeter',
      }
      return this.$store.dispatch('view/loadGroupViews', param)
    },
  },
}
</script>
