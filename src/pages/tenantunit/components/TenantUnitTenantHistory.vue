<template>
  <div>
    <div v-if="isLoading" class="mT100">
      <Spinner :show="isLoading"></Spinner>
    </div>
    <div v-else class="height100 flex-wrap">
      <el-row>
        <div class="flex-middle justify-content-space p20">
          <div class="f13 bold text-uppercase fc-black-13 text-left">
            {{ $t('common._common.tenant_history') }}
          </div>
        </div>
      </el-row>
      <el-table :data="tenantspaces" class="width100" height="330" :fit="true">
        <template slot="empty">
          <img
            class="mT100"
            src="~statics/noData-light.png"
            width="100"
            height="100"
          />
          <div class="mT10 label-txt-black f14">
            {{ $t('common._common.no_units_available') }}
          </div>
        </template>
        <el-table-column fixed prop label="Name" width="220">
          <template v-slot="item">
            <div class="pointer mL30" @click="redirectToOverview(item.row)">
              {{ getFieldValue(item.row.tenant || {}, 'name') }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop label="Primary Contact Name" width="240">
          <template v-slot="item">
            <div class="pointer mL30">
              {{ getFieldValue(item.row.tenant || {}, 'primaryContactName') }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="Primary Contact Phone" width="240">
          <template v-slot="item">
            <div class="pointer mL30">
              {{ getFieldValue(item.row.tenant || {}, 'primaryContactPhone') }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="Lease Start Date" width="180">
          <template v-slot="item">
            <div class="pointer mL30">
              {{ getFieldValue(item.row.tenant || {}, 'inTime') }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="Lease End Date" width="180">
          <template v-slot="item">
            <div class="pointer mL30">
              {{ getFieldValue(item.row.tenant || {}, 'outTime') }}
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
import { API } from '@facilio/api'
export default {
  props: ['details'],
  data() {
    return {
      activeModuleName: 'spaces',
      tenantspaces: [],
      isLoading: false,
    }
  },
  async created() {
    await this.loadTenantSpaces()
  },
  computed: {},
  methods: {
    async loadTenantSpaces() {
      let { details } = this
      let { id } = details || {}
      let params = {
        viewname: 'all',
        page: 1,
        orderBy: 'associatedTime',
        orderType: 'desc',
        filters: JSON.stringify({
          space: { operatorId: 54, value: [id.toString()] },
        }),
        moduleName: 'tenantspaces',
      }
      let { data, error } = await API.get('v3/modules/data/list', params)
      if (!error) {
        let { tenantspaces } = data || {}
        this.tenantspaces = tenantspaces || []
      }
    },
    getFieldValue(valueObject, key) {
      let value = (valueObject || {})[key]
      if (key === 'inTime' || key === 'outTime') {
        return value ? this.$options.filters.formatDate(value, true) : '---'
      }
      return value || '---'
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
.line-hr {
  margin-top: 25px;
  width: 100%;
  height: 1px;
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
.overdue-warning {
  color: #eb6a6a;
}
</style>
