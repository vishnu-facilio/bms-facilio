<script>
import AssetList from 'pages/assets/asset/v1/AssetList'
export default {
  extends: AssetList,
  data() {
    return {
      pathPrefix: '/app/en/energymeter',
    }
  },
  computed: {
    parentPath() {
      return '/app/en'
    },
    modulePrefix() {
      return 'energymeter'
    },
  },
  methods: {
    openAssetOverview(id) {
      return {
        path: `${this.parentPath}/${this.modulePrefix}/${this.viewname}/${id}/overview`,
        query: this.$route.query,
      }
    },
  },
}
</script>
