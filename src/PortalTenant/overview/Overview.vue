<template>
  <div class="position-relative">
    <div class="fc-tenant-homepage-scroll">
      <TenantHeader></TenantHeader>
      <TenantAnnouncement
        v-if="$helpers.isLicenseEnabled('COMMUNITY')"
      ></TenantAnnouncement>
      <TenantServiceRequest
        v-if="$org.id === 320 || $org.id === 17"
      ></TenantServiceRequest>
      <TenantWoList v-else></TenantWoList>
      <TenantVisitor
        v-if="$helpers.isLicenseEnabled('VISITOR')"
      ></TenantVisitor>
      <TenantNewsFeed
        v-if="$helpers.isLicenseEnabled('COMMUNITY')"
      ></TenantNewsFeed>
      <TenantContact v-if="$org.id === 17"> </TenantContact>
      <div v-else>
        <TenantContact-Directory
          v-if="$helpers.isLicenseEnabled('COMMUNITY')"
        ></TenantContact-Directory>
      </div>
    </div>
  </div>
</template>
<script>
import TenantAnnouncement from 'src/PortalTenant/overview/TenantOverviewAnnouncement'
import TenantWoList from 'src/PortalTenant/overview/TenantOverviewWoList'
import TenantServiceRequest from 'src/PortalTenant/overview/TenantOverviewServiceRequest'
import TenantVisitor from 'src/PortalTenant/overview/TenantOverviewVisitor'
import TenantNewsFeed from 'src/PortalTenant/overview/TenantOverviewNewsFeed'
import TenantHeader from 'src/PortalTenant/overview/TenantOverviewHeader'
import TenantContactDirectory from 'src/PortalTenant/overview/TenantOverviewContactDirectory'
import TenantContact from 'src/PortalTenant/overview/TenantOverviewTenantContact'
export default {
  data() {
    return {}
  },
  components: {
    TenantAnnouncement,
    TenantWoList,
    TenantNewsFeed,
    TenantHeader,
    TenantContactDirectory,
    TenantVisitor,
    TenantServiceRequest,
    TenantContact,
  },
  methods: {},
}
</script>
<style lang="scss" scoped>
.overview-container {
  padding: 40px 30px 160px;
  height: calc(100vh - 100px);
  overflow-y: scroll;
}

.activeNewsWidth {
  width: calc(100% - 390px);
}

.fc-tenant-homepage-scroll {
  height: calc(100vh - 10px);
  overflow-y: scroll;
}
</style>
