<script>
import { getApp } from '@facilio/router'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import FetchViewsMixin from '@/base/FetchViewsMixin'
import ModuleForm from '../custom-module/ModuleForm'

export default {
  extends: ModuleForm,
  mixins: [FetchViewsMixin],
  title() {
    return 'Neighbourhood'
  },
  data() {
    return {
      currentTenant: null,
    }
  },
  async created() {
    await this.loadCurrentTenant()
  },
  computed: {
    moduleDisplayName() {
      return 'Neighbourhood'
    },
  },
  methods: {
    async loadCurrentTenant() {
      let { error, data } = await API.get('v2/tenant/details', {
        tenantPortal: true,
      })
      if (!error) {
        this.currentTenant = data.tenant
      }
    },
    afterSerializeHook({ data }) {
      if (getApp().linkName === 'tenant') {
        let currentSiteId = this.currentTenant.siteId
        if (isEmpty(this.moduleDataId) && currentSiteId) {
          data['neighbourhoodsharing'] = [
            { sharedToSpace: { id: currentSiteId }, sharingType: 2 },
          ]
        }
      }
      return data
    },
  },
}
</script>
