<template>
  <div class="height100 d-flex">
    <div style="flex: 0 0 300px; max-width: 300px;">
      <div
        class="height100vh full-layout-white fc-border-left fc-border-right approval-sidebar-list"
      >
        <SummarySideBar
          :list="tenantList"
          :isLoading.sync="isLoading"
          :activeRecordId="currentId"
          :total="listCount"
          :currentCount="(tenantList || []).length"
          :currentPage="page - 1"
          @nextPage="loadTenants"
        >
          <template #title>
            <div class="row p15 fc-border-bottom pointer">
              <div class="col-1 text-left">
                <i
                  class="el-icon-back fw6"
                  @click="back"
                  style="vertical-align: sub;"
                ></i>
              </div>
              <span class="line-height20">
                {{ currentViewDetail.displayName }}
              </span>
            </div>
          </template>

          <template v-slot="{ record }">
            <div class="list-label p20" @click="getLink(record.id)">
              <span
                :title="record.name"
                v-tippy="{
                  placement: 'top',
                  animation: 'shift-away',
                  arrow: true,
                }"
              >
                {{ record.name }}
              </span>
            </div>
          </template>
        </SummarySideBar>
      </div>
    </div>

    <TenantOverviewPage class="width100"></TenantOverviewPage>
  </div>
</template>
<script>
import SummarySideBar from 'newapp/components/SummarySideBar'
import TenantOverviewPage from './TenantOverviewPage'
import { mapState } from 'vuex'
import { API } from '@facilio/api'
import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'

export default {
  props: ['id', 'moduleName', 'viewname'],
  data() {
    return {
      tenantList: [],
      isLoading: false,
      listCount: 0,
      page: 1,
    }
  },
  created() {
    let { moduleName, viewname } = this

    this.loadTenantCount()
    this.loadTenants()
    this.$store.dispatch('view/loadModuleMeta', this.moduleName)
    this.$store.dispatch('view/loadViewDetail', {
      viewName: viewname,
      moduleName,
    })
  },
  components: {
    SummarySideBar,
    TenantOverviewPage,
  },
  computed: {
    ...mapState({
      viewDetail: state => state.view.currentViewDetail,
    }),
    filters() {
      let { query } = this.$route
      let { search } = query || {}

      return search ? JSON.parse(search) : null
    },
    currentId() {
      return this.id ? parseInt(this.id) : null
    },
    currentViewDetail() {
      let { filters, viewDetail } = this

      if (filters) {
        return { displayName: 'Filtered List', name: 'filteredList' }
      }
      return viewDetail
    },
  },
  methods: {
    loadTenants() {
      let { viewname, filters, page } = this
      let url = `/v2/tenant/${viewname}`
      let params = {
        page,
        perPage: 50,
        filters: filters ? JSON.stringify(filters) : null,
      }

      this.isLoading = true
      API.get(url, params).then(({ data, error }) => {
        if (error) {
          this.$message.error(error || 'Error Occured')
        } else {
          this.tenantList = [...this.tenantList, ...data.tenants]
          this.page++
        }
        this.isLoading = false
      })
    },
    loadTenantCount() {
      let { filters, viewname } = this
      let params = {
        viewName: viewname,
        count: true,
        filters: filters ? JSON.stringify(filters) : null,
      }

      API.get('/tenant/tenantCount', params).then(({ data, error }) => {
        if (!error) this.listCount = data.tenantCount || 0
      })
    },
    back() {
      let { viewname, $route, moduleName } = this

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.LIST) || {}
        if (name) {
          this.$router.push({ name, params: { viewname } })
        }
      } else {
        let url = `/app/tm/tenants/${viewname}`
        this.$router.push({ path: url, query: $route.query })
      }
    },
    getLink(id) {
      let { viewname, moduleName, $route } = this

      if (id === null) return
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}

        if (name) {
          this.$router.replace({
            name,
            params: { viewname, id },
            query: $route.query,
          })
        }
      } else {
        this.$router.replace({
          path: `/app/tm/tenants/${viewname}/${id}/overview`,
          query: $route.query,
        })
      }
    },
  },
}
</script>
