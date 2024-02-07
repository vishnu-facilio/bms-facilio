<template>
  <div class="pointer p15" @click="redirectToOverview()">
    <div>
      <el-row>
        <div class="flex-middle justify-content-space">
          <div class="f13 bold text-uppercase fc-black-13 text-left">
            {{ $t('common._common.primaty_contact') }}
          </div>
        </div>
      </el-row>
      <el-row>
        <el-col :span="3">
          <div class="tenant-avatar mL5 mT25 flex-middle justify-content-left">
            <avatar :user="{ name: getTenantPrimaryName }"></avatar>
          </div>
        </el-col>
        <el-col :span="19" class="mT25 mL20">
          <el-row>
            <el-col :span="24">
              <div class="fc-black-13 bold flex text-left">
                {{ getTenantPrimaryName }}
              </div>
            </el-col>
          </el-row>
          <el-row>
            <div class="fc-black-13 flex text-left mT10">
              <img src="~assets/other-email.svg" class="tenant-primary-icon" />
              {{ getTenantPrimaryEmail }}
            </div>
          </el-row>
          <el-row>
            <div class="fc-black-13 flex text-left mT10">
              <i
                class="el-icon-phone fwBold f13 mT2 mR7"
                style="color:rgba(121,133,150,1)"
              ></i>
              {{ getTenantPrimaryPhone }}
            </div>
          </el-row>
        </el-col>
      </el-row>
    </div>
  </div>
</template>
<script>
import Avatar from 'src/pages/tenants/components/TenantAvatar.vue'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
export default {
  props: ['details'],
  components: { Avatar },
  computed: {
    getTenantPrimaryName() {
      let { details } = this
      let { primaryContactName } = details || {}
      return primaryContactName || '---'
    },
    getTenantPrimaryPhone() {
      let { details } = this
      let { primaryContactPhone } = details || {}
      return primaryContactPhone || '---'
    },
    getTenantPrimaryEmail() {
      let { details } = this
      let { primaryContactEmail } = details || {}
      return primaryContactEmail || '---'
    },
    getTenantPrimaryContactId() {
      let { details } = this
      let { peopleTenantContacts } = details || []
      let primaryContactId = null
      peopleTenantContacts.forEach(tenantcontact => {
        let { isPrimaryContact } = tenantcontact || false
        if (isPrimaryContact) {
          primaryContactId = tenantcontact.id
        }
      })
      return primaryContactId
    },
  },
  methods: {
    redirectToOverview() {
      let tenantcontactId = this.getTenantPrimaryContactId
      if (tenantcontactId) {
        let route
        let params = { id: tenantcontactId, viewname: 'all' }

        if (isWebTabsEnabled()) {
          let { name } =
            findRouteForModule('tenantcontact', pageTypes.OVERVIEW) || {}

          if (name) {
            route = this.$router.resolve({
              name,
              params,
            }).href
          }
        } else {
          route = this.$router.resolve({
            name: 'tenantcontact',
            params,
          }).href
        }
        route && window.open(route, '_blank')
      }
    },
  },
}
</script>
<style scoped>
.tenant-avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
}
.tenant-primary-icon {
  height: 10px;
  margin-top: 3px;
  margin-right: 8px;
}
</style>
