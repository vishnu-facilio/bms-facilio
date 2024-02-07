<template>
  <div class="p15">
    <el-row>
      <div class="flex-middle justify-content-space">
        <div class="f13 bold text-uppercase fc-black-13 text-left">
          {{ $t('common._common.details') }}
        </div>
      </div>
    </el-row>
    <el-row class="flex-middle mT30">
      <div class="display-flex-between-space width100">
        <el-col :span="7">
          <div class="pT10 bold f13 fc-black-13 text-left">
            {{ getLeaseStartDate }}
          </div>
          <div class="f13 fc-black-13 text-left mT5">
            {{ $t('common._common.lease_start_date') }}
          </div>
        </el-col>
        <div class="seperator"></div>
        <el-col :span="7">
          <div class="pT10 bold f13 fc-black-13 text-left">
            {{ getLeaseEndDate }}
          </div>
          <div class="f13 fc-black-13 text-left mT5">
            {{ $t('common._common.lease_end_date') }}
          </div>
        </el-col>
        <div class="seperator"></div>
        <el-col :span="6">
          <div class="pT10 bold f13 fc-black-13 text-left">
            {{ getTenantType }}
          </div>
          <div class="f13 fc-black-13 text-left mT5">
            {{ $t('common._common.tenant_type') }}
          </div>
        </el-col>
      </div>
    </el-row>
  </div>
</template>
<script>
export default {
  props: ['details'],
  computed: {
    getDescription() {
      let { details } = this
      let { description } = details || {}
      return description || '---'
    },
    getLeaseStartDate() {
      let { details } = this
      let { inTime } = details || {}
      return inTime ? this.$options.filters.formatDate(inTime, true) : '---'
    },
    getLeaseEndDate() {
      let { details } = this
      let { outTime } = details || {}
      return outTime ? this.$options.filters.formatDate(outTime, true) : '---'
    },
    getTenantType() {
      let { details } = this
      let { tenantType } = details || {}
      return tenantType
        ? tenantType == 1
          ? 'COMMERCIAL'
          : 'RESIDENTIAL'
        : '---'
    },
    getTenantName() {
      let { details } = this
      let { name } = details || {}
      return name || '---'
    },
  },
}
</script>
<style scoped>
.tenant-img {
  width: 100px;
  height: 100px;
  border: 1px solid #f9f9f9;
  border-radius: 50%;
}
.emtpy-tenant-container {
  width: 220px;
  height: 220px;
  border: 1px solid #cacaca;
  background-color: #ecf2f7;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}
.emtpy-tenant {
  width: 100px;
  height: 100px;
}
.flex-middle {
  display: flex;
  flex-direction: row !important;
}
.seperator {
  margin-top: 10px;
  height: 40px;
  border: solid 1px #ededed;
}
</style>
