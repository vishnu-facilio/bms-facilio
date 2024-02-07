<script>
import FacilioPasswordReset from './FacilioPasswordReset'
import homeMixin from 'src/PortalTenant/auth/portalHomeMixin'

export default {
  mixins: [homeMixin],
  extends: FacilioPasswordReset,
  created() {
    this.loading = true
    this.loadPublicInfo().finally(() => (this.loading = false))
  },
}
</script>
