<template>
  <div>
    <div v-if="isLoading" class="mT100">
      <Spinner :show="isLoading"></Spinner>
    </div>
    <div v-else-if="isTenantAvailable" class="height100 flex-wrap">
      <el-row>
        <div class="flex-middle justify-content-space p20">
          <div class="f13 bold text-uppercase fc-black-13 text-left">
            {{ $t('common._common.occupancy') }}
          </div>
          <div
            v-tippy
            :title="'View Tenant Details'"
            class="f11 bold view pointer"
            @click="redirectToOverview()"
          >
            {{ $t('common._common.view') }}
          </div>
        </div>
      </el-row>
      <el-row>
        <div class="flex justify-center mT10">
          <img v-if="getAvatarUrl" class="tenant-photo" :src="getAvatarUrl" />
          <avatar
            v-else
            :user="{ name: getTenantName }"
            :moduleName="'tenantunit'"
          ></avatar>
        </div>
      </el-row>
      <el-row>
        <div class="bold flex justify-center f16 mT20">
          {{ getTenantName }}
        </div>
      </el-row>
      <el-row>
        <div class="flex justify-center f14 mT20">
          <i
            class="el-icon-phone fwBold f13 mT2 mR7"
            style="color:rgba(121,133,150,1)"
          ></i>
          {{ getTenantPhone }}
        </div>
      </el-row>
      <el-row>
        <div class="flex justify-center f14 mT20">
          <img
            src="~assets/other-email.svg"
            class="tenant-primary-icon email-icon mR10 mT2"
          />
          {{ getTenantEmail }}
        </div>
      </el-row>
    </div>
    <div v-else class="height100 flex-wrap">
      <el-row>
        <div class="flex-middle justify-content-space p20">
          <div class="f13 bold text-uppercase fc-black-13 text-left">
            {{ $t('common._common.occupancy') }}
          </div>
        </div>
      </el-row>
      <el-row>
        <div class="flex justify-center">
          <img
            class="mT20"
            src="~assets/svgs/tenant/tenantunit/vacant_unit.svg"
            width="140"
            height="140"
          />
        </div>
      </el-row>
      <el-row>
        <div class="bold flex justify-center mT10">
          {{ $t('common._common.tenant_unit_vacant') }}
        </div>
      </el-row>
    </div>
  </div>
</template>
<script>
import Spinner from '@/Spinner'
import Avatar from 'src/pages/tenants/components/TenantAvatar.vue'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  props: ['details'],
  data() {
    return {
      lastClosedWorkorderId: null,
      lastClosedWorkorder: '---',
      mostOverdueWorkorderId: null,
      mostOverdueWorkorder: '---',
    }
  },
  components: { Spinner, Avatar },
  async created() {},
  computed: {
    getAvatarUrl() {
      let { details } = this
      let { tenant } = details || {}
      let { avatarUrl } = tenant || {}
      return avatarUrl
    },
    getTenantName() {
      let { details } = this
      let { tenant } = details || {}
      let { name } = tenant || {}
      return name || '---'
    },
    getTenantPhone() {
      let { details } = this
      let { tenant } = details || {}
      let { primaryContactPhone } = tenant || {}
      return primaryContactPhone || '---'
    },
    getTenantEmail() {
      let { details } = this
      let { tenant } = details || {}
      let { primaryContactEmail } = tenant || {}
      return primaryContactEmail || '---'
    },
    isTenantAvailable() {
      let { details } = this
      let { tenant } = details || {}
      return tenant ? true : false
    },
  },
  methods: {
    redirectToOverview() {
      let { details } = this
      let { tenant } = details || {}
      let { id } = tenant || {}
      if (!this.$validation.isEmpty(id)) {
        let route
        let params = { id: id, viewname: 'all' }

        if (isWebTabsEnabled()) {
          let { name } = findRouteForModule('tenant', pageTypes.OVERVIEW) || {}

          if (name) {
            route = this.$router.resolve({
              name,
              params,
            }).href
          }
        } else {
          route = this.$router.resolve({
            name: 'tenantSummary',
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
.line-vr {
  margin-top: 20px;
  width: 1px;
  height: 190px;
  background: #8ca1ad;
  opacity: 0.3;
}
.mL55 {
  margin-left: 55px;
}
.mT100 {
  margin-top: 100px;
}
.tenant-wo-url:hover {
  cursor: pointer;
}
.f60 {
  font-size: 60px;
}
.mT65 {
  margin-top: 65px;
}
.tenant-photo {
  width: 120px;
  height: 120px;
  border-radius: 50%;
}
.email-icon {
  width: 15px;
  height: 15px;
}
.view {
  color: #8ca1ad;
}
.view:hover {
  color: #2c9baa;
}
</style>
