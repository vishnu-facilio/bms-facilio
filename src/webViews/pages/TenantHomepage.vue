<template>
  <div>
    <div class="fc-mobile-tenant-home-con">
      <div class="width100">
        <MobileTenantCommerical
          :tenant="tenant"
          :spaces="spaces"
          :loading="loading"
        ></MobileTenantCommerical>

        <TenantWoRequest></TenantWoRequest>

        <TenantAnnouncement></TenantAnnouncement>

        <TenantNews></TenantNews>

        <TenantContacts></TenantContacts>
      </div>
    </div>
  </div>
</template>
<script>
import TenantWoRequest from 'webViews/components/MobileTenantWoRequest'
import TenantAnnouncement from 'webViews/components/MobileTenantAnnouncement'
import TenantNews from 'webViews/components/MobileTenantNews'
import TenantContacts from 'webViews/components/MobileTenantContacts'
import MobileTenantCommerical from 'webViews/components/MobileTenantCommerical'
import { API } from '@facilio/api'
export default {
  created() {
    this.loadTenant()
  },
  data() {
    return {
      tenant: [],
      loading: true,
      spaces: [],
    }
  },
  components: {
    TenantWoRequest,
    TenantAnnouncement,
    TenantNews,
    TenantContacts,
    MobileTenantCommerical,
  },
  methods: {
    loadTenant() {
      this.loading = true
      API.get('v2/tenant/details', {
        tenantPortal: true,
      }).then(({ error, data }) => {
        if (!error) {
          this.tenant = data.tenant || []
          this.spaces = data.tenant.spaces[0] || []
        }
        this.loading = false
      })
    },
  },
}
</script>
