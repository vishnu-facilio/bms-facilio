<script>
import TenantLayout from 'src/webViews/pages/TenantLayout'
export default {
  extends: TenantLayout,
  name: 'QandaFormLayout',
  data() {
    return {
      linkName: null,
    }
  },
  created() {
    let { $route } = this
    let { query } = $route || {}

    this.linkName = this.$getProperty(query, 'appName', 'maintenance')
  },
}
</script>
