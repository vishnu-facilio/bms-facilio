<template>
  <div class="height100 d-flex">
    <div style="flex: 0 0 300px; max-width: 300px;">
      <div
        class="height100vh full-layout-white fc-border-left fc-border-right approval-sidebar-list"
      >
        <SummarySideBar
          :list="tenantUnit"
          :isLoading.sync="loading"
          :activeRecordId="currentId"
          :total="listCount"
          :currentCount="(tenantUnit || []).length"
          :currentPage="page - 1"
          @nextPage="loadData"
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
            <div class="cm-sidebar-list-item" @click="getLink(record.id)">
              <div class="text-fc-grey">#{{ record['id'] }}</div>
              <div
                class="f14 truncate-text bold mT5"
                :title="record.name || '---'"
                v-tippy="{
                  placement: 'top',
                  animation: 'shift-away',
                  arrow: true,
                }"
              >
                {{ record.name || '---' }}
              </div>
            </div>
          </template>
        </SummarySideBar>
      </div>
    </div>
    <TenantUnitOverviewPage class="width100"></TenantUnitOverviewPage>
  </div>
</template>
<script>
import SummarySideBar from 'newapp/components/SummarySideBar'
import TenantUnitOverviewPage from './TenantUnitOverviewPage'
import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'
import { API } from '@facilio/api'
import { mapState } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['viewname', 'id', 'moduleName'],
  components: { SummarySideBar, TenantUnitOverviewPage },
  data() {
    return {
      tenantUnit: [],
      loading: false,
      listCount: 0,
      page: 1,
    }
  },
  created() {
    let { moduleName, viewname } = this

    this.loadRecordsCount()
    this.loadData()
    this.$store.dispatch('view/loadModuleMeta', this.moduleName)
    this.$store.dispatch('view/loadViewDetail', {
      viewName: viewname,
      moduleName,
    })
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
    currentViewDetail() {
      let { filters, viewDetail } = this

      if (filters) {
        return { displayName: 'Filtered List', name: 'filteredList' }
      }
      return viewDetail
    },
    currentId() {
      return this.id ? parseInt(this.id) : null
    },
  },
  methods: {
    getLink(id) {
      if (id === null) return
      else {
        let { viewname, moduleName, $route } = this

        if (isWebTabsEnabled()) {
          let { name } =
            findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}

          if (name) {
            this.$router.replace({
              name,
              params: { viewname, id },
              query: $route.query,
            })
          }
        } else {
          this.$router.replace({
            path: `/app/tm/tenantunit/${viewname}/${id}/overview`,
            query: $route.query,
          })
        }
      }
    },
    async loadRecordsCount() {
      let { viewname, filters, moduleName } = this
      let params = {
        filters: filters ? JSON.stringify(filters) : null,
        moduleName,
        fetchCount: true,
        viewName: viewname,
        includeParentFilter: !isEmpty(filters),
      }

      let { data, error } = await API.get(`/v2/module/data/list`, params)

      if (!error) {
        this.listCount = data.count || 0
      }
    },
    async loadData() {
      let { viewname, filters, page, moduleName } = this
      let params = {
        page,
        perPage: 50,
        filters: filters ? JSON.stringify(filters) : null,
        includeParentFilter: !isEmpty(filters),
        moduleName,
      }

      this.loading = true
      let { data, error } = await API.get(
        `/v2/module/data/view/${viewname}`,
        params
      )

      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.tenantUnit = [...this.tenantUnit, ...data.moduleDatas]
        this.page++
      }
      this.loading = false
    },
    back() {
      let { viewname, moduleName, $route } = this

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.LIST) || {}

        if (name) {
          this.$router.push({ name, params: { viewname }, query: $route.query })
        }
      } else {
        let url = `/app/tm/tenantunit/${viewname}`
        this.$router.push({ path: url, query: $route.query })
      }
    },
  },
}
</script>
<style scoped>
.cm-sidebar-list-item {
  text-overflow: ellipsis;
  white-space: nowrap;
  overflow: hidden;
  padding: 20px;
  font-size: 12px;
  cursor: pointer;
}
</style>
