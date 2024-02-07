<template>
  <div class="tenant-container">
    <div v-if="loading" class="text-center width100 pT50">
      <spinner :show="loading" size="80"></spinner>
    </div>
    <div
      v-else-if="$validation.isEmpty(tenants)"
      class="flex-middle justify-content-center flex-direction-column"
    >
      <inline-svg
        src="svgs/emptystate/tenant"
        iconClass="icon text-center icon-xxxxlg"
      ></inline-svg>
      <div class="nowo-label">
        No Tenant Available
      </div>
    </div>
    <template v-else>
      <el-table :data="tenants" height="calc(100vh - 195px)">
        <el-table-column
          label="ID"
          header-align="center"
          align="center"
          min-width="30px"
        >
          <template v-slot="tenant">
            <div class="fc-id">
              {{ '#' + tenant.row.id }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="NAME" header-align="center" align="center">
          <template v-slot="tenant">
            {{ tenant.row.name }}
          </template>
        </el-table-column>

        <el-table-column label="PUBLISHED" header-align="center" align="center">
          <template v-slot="tenant">
            <AsyncButton
              v-if="isTenantPublished(tenant.row)"
              :clickAction="publishTenant"
              :actionParams="[tenant.row]"
              class="publish-btn"
              size="mini"
              >PUBLISH</AsyncButton
            >
            <AsyncButton
              v-else
              :clickAction="publishTenant"
              :actionParams="[tenant.row]"
              class="unpublish-btn"
              size="mini"
              >UNPUBLISH</AsyncButton
            >
          </template>
        </el-table-column>
        <el-table-column label="SUMMARY" header-align="center" align="center">
          <template v-slot="tenant">
            <a
              @click="openTenantSummary(tenant.row)"
              :class="
                !isTenantPublished(tenant.row) ? '' : 'disabled-view-summary'
              "
              >View Summary
              <InlineSvg
                src="svgs/new-tab"
                iconClass="icon vertical-middle icon-xs mL3"
              ></InlineSvg
            ></a>
          </template>
        </el-table-column>
      </el-table>
    </template>
  </div>
</template>

<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import AsyncButton from '@/AsyncButton'
export default {
  components: { AsyncButton },
  props: ['group'],
  data() {
    return {
      loading: false,
      tenants: [],
      publishedTenants: [],
      selectedTenantIds: [],
      isPublishLoading: false,
    }
  },
  watch: {
    group: {
      handler(value) {
        if (!isEmpty(value)) this.loadRecords()
      },
      immediate: true,
    },
  },
  methods: {
    async loadRecords() {
      let {
        group: { id, space },
      } = this
      if (!isEmpty(space)) {
        let url = `/v3/control/getTenantsToBePublished`
        let params = {
          group: { id: id },
        }
        this.loading = true

        let { data, error } = await API.post(url, params)
        if (!isEmpty(error)) {
          this.tenants = []
          this.$message.error(error.message || 'Error occured')
        } else {
          let { tenants, alreadySharedTenantList } = data
          let publishedTenants = []
          if (!isEmpty(alreadySharedTenantList))
            publishedTenants = alreadySharedTenantList.map(group => {
              let { id, tenant } = group
              return { ...tenant, groupId: id }
            })
          this.tenants = [...tenants, ...publishedTenants]
          this.publishedTenants = publishedTenants
        }

        this.loading = false
      } else {
        this.tenants = []
      }
    },
    changeTenantsSelection(tenants) {
      if (!isEmpty(tenants)) {
        this.selectedTenantIds = tenants.map(currTenant => currTenant.id)
        this.showTenantOverride = true
      } else {
        this.showTenantOverride = false
      }
    },
    allowOverride() {
      this.isOverrideLoading = true
      this.isOverrideLoading = false
    },
    isTenantPublished(tenant) {
      let { id } = tenant
      let publishedTenant = this.publishedTenants.find(
        currTenant => currTenant.id === id
      )
      return isEmpty(publishedTenant)
    },
    async publishTenant(tenant) {
      this.isPublishLoading = true
      let {
        group: { id },
      } = this
      let { id: tenantId } = tenant
      let url = `/v3/control/${
        this.isTenantPublished(tenant) ? 'publishToTenant' : 'unPublishToTenant'
      }`
      let params = {
        group: { id: id },
        tenant: { id: tenantId },
      }
      let { error } = await API.post(url, params)
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.$message.success(
          `Tenant Group ${
            this.isTenantPublished(tenant) ? 'Published' : 'Unpublished'
          } successfully`
        )
        this.loadRecords()
      }
      this.isPublishLoading = false
    },
    openTenantSummary(tenant) {
      let { groupId } = tenant
      if (!this.isTenantPublished(tenant)) {
        let route = {
          name: 'tenant-group-summary',
          params: { id: groupId },
          query: {
            ...this.$route.query,
          },
        }

        let { href } = this.$router.resolve(route)

        window.open(href, '_blank', 'noopener,noreferrer')
      }
    },
  },
}
</script>

<style lang="scss" scoped>
.table-header-group {
  position: absolute;
  z-index: 1343;
  left: 70px;
  background: #fff;
  width: 90.9%;
  height: 40px;
  padding-top: 8px;
}

.publish-btn {
  background-color: rgb(57, 178, 194) !important;
  border-color: rgb(57, 178, 194) !important;
  padding-bottom: 5px;
  padding-top: 5px;
  color: #ffffff;
  &:hover {
    background-color: #33a6b5 !important;
    color: #ffffff;
  }
}
.unpublish-btn {
  border-color: rgb(57, 178, 194) !important;
  padding-bottom: 5px;
  padding-top: 5px;
  color: rgb(57, 178, 194) !important;
  &:hover {
    background-color: rgb(57, 178, 194) !important;
    color: #ffffff !important;
  }
}

.disabled-view-summary {
  cursor: not-allowed;
  color: #717b85;
}
.tenant-container {
  height: calc(100vh - 195px);
  background-color: #ffffff;
}
</style>
