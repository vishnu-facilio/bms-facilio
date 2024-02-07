<template>
  <div
    v-if="workorder.tenant && workorder.tenant.id > 0"
    class="white-bg-block p20 wo-tenant-card text-left"
  >
    <div class="fc-id11 text-uppercase text-left">
      {{ $t('maintenance.wr_list.tenant') }}
    </div>
    <div class="pointer pT5 text-left" @click="openTenant(workorder.tenant.id)">
      <el-row class="flex-middle">
        <el-col :span="2">
          <tenant-avatar
            :name="false"
            size="md"
            :tenant="workorder.tenant"
          ></tenant-avatar>
        </el-col>
        <el-col :span="22" class="pT3 pL10">
          <div class="label-txt-black bold line-height20">
            {{ workorder.tenant.name }}
          </div>
          <div class="fc-black-13 text-left line-height20">
            {{ workorder.tenant.primaryContactName }}
          </div>
          <div class="fc-black-13 text-left line-height20">
            {{ workorder.tenant.primaryContactEmail }}
          </div>
          <div class="pT5 fc-black-13 text-left">
            {{ workorder.tenant.primaryContactPhone }}
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>
<script>
import TenantAvatar from '@/avatar/Tenant'
export default {
  name: 'Tenant',
  props: ['moduleName', 'details'],
  components: {
    TenantAvatar,
  },
  computed: {
    currModuleName() {
      return 'workorder'
    },
    widgetTitle() {
      return 'Tenant'
    },
    workorder() {
      return this.details.workorder
    },
  },
}
</script>
