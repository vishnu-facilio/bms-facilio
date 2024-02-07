<template>
  <div class="height100 d-flex">
    <div style="flex: 0 0 300px; max-width: 300px;">
      <div
        class="height100vh full-layout-white fc-border-left fc-border-right approval-sidebar-list"
      >
        <SummarySideBar
          :list="contacts"
          :isLoading.sync="loading"
          :activeRecordId="currentId"
          :total="listCount"
          :currentCount="(contacts || []).length"
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

    <TenantContactOverviewPage class="width100"></TenantContactOverviewPage>
  </div>
</template>
<script>
import TenantContactOverviewPage from './TenantContactOverviewPage'
import { API } from '@facilio/api'
import SummarySideBar from 'newapp/components/SummarySideBar'
import { mapState } from 'vuex'
import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'

export default {
  props: ['viewname', 'id', 'moduleName'],
  data() {
    return {
      loading: false,
      contacts: [],
      listCount: 0,
      page: 1,
    }
  },
  components: {
    SummarySideBar,
    TenantContactOverviewPage,
  },
  created() {
    let { moduleName, viewname } = this

    this.loadCount()
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
        let { moduleName, viewname, $route } = this

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
            path: `/app/tm/${moduleName}/${viewname}/${id}/overview`,
            query: $route.query,
          })
        }
      }
    },
    async loadCount() {
      let { viewname, filters } = this
      let params = {
        filters: filters ? JSON.stringify(filters) : null,
        includeParentFilter: filters ? true : false,
        fetchCount: true,
      }
      let { data, error } = await API.get(
        `/v2/tenantcontact/views/${viewname}`,
        params
      )

      if (!error) {
        this.listCount = data.recordCount || 0
      }
    },
    loadData() {
      let { viewname, filters, page } = this
      let url = `/v2/tenantcontact/views/${viewname}`
      let params = {
        page,
        perPage: 50,
        filters: filters ? JSON.stringify(filters) : null,
        includeParentFilter: filters ? true : false,
      }

      this.loading = true
      API.get(url, params).then(({ data, error }) => {
        if (error) {
          this.$message.error(error || 'Error Occured')
        } else {
          this.contacts = [...this.contacts, ...data.tenantcontacts]
          this.page++
        }
        this.loading = false
      })
    },
    back() {
      let { viewname, moduleName } = this

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.LIST) || {}

        if (name) {
          this.$router.push({ name, params: { viewname } })
        }
      } else {
        let url = `/app/tm/tenantcontact/${viewname}`
        this.$router.push({ path: url, query: this.$route.query })
      }
    },
  },
}
</script>
