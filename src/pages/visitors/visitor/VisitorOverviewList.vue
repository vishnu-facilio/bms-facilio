<template>
  <el-row class="height100">
    <el-col :span="4">
      <div
        class="height100vh full-layout-white fc-border-left fc-border-right approval-sidebar-list"
      >
        <SummarySideBar
          :list="visitorsList"
          :isLoading.sync="loading"
          :activeRecordId="currentId"
          :total="listCount"
          :currentCount="(visitorsList || []).length"
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
    </el-col>
    <el-col :span="20">
      <VisitorSummary
        v-if="currentId"
        :id="currentId"
        @redirectToList="back"
      ></VisitorSummary>
    </el-col>
  </el-row>
</template>
<script>
import VisitorSummary from './VisitorSummary'
import { API } from '@facilio/api'
import SummarySideBar from 'newapp/components/SummarySideBar'
import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'
import { mapState } from 'vuex'

export default {
  props: ['viewname', 'id', 'moduleName'],

  components: {
    VisitorSummary,
    SummarySideBar,
  },
  data() {
    return {
      visitorsList: [],
      loading: false,
      listCount: 0,
      page: 1,
    }
  },
  created() {
    let { moduleName, viewname } = this
    this.loadData()
    this.$store.dispatch('view/loadModuleMeta', moduleName)
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
            path: `/app/vi/visitor/${viewname}/${id}/summary`,
            query: $route.query,
          })
        }
      }
    },
    async loadData() {
      let { viewname, filters, page } = this
      let params = {
        withCount: true,
        viewName: viewname,
        includeParentFilter: filters ? true : false,
        page,
        perPage: 50,
        filters: filters ? JSON.stringify(filters) : null,
      }
      this.loading = true
      let {
        list,
        meta: { pagination = {} },
        error,
      } = await API.fetchAll('visitor', params)

      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.listCount = this.$getProperty(pagination, 'totalCount', null)
        this.visitorsList = list || []
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
        this.$router.push({
          path: `/app/vi/visitor/${viewname}`,
          query: $route.query,
        })
      }
    },
  },
}
</script>
