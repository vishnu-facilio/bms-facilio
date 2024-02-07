<template>
  <div>
    <div class="height100 d-flex fc-quotation-summary-list">
      <div style="flex: 0 0 320px; max-width: 320px;">
        <div
          class="height100vh full-layout-white fc-border-left fc-border-right"
        >
          <div class="row p15 fc-border-bottom pointer">
            <div class="flex items-center">
              <i
                class="el-icon-back fw6"
                @click="back"
                style="vertical-align: sub;"
              ></i>
              <span slot="reference" class="line-height20 bold mL10">
                {{ currentViewDetail.displayName }}
              </span>
            </div>
            <div class="row" v-if="showQuickSearch">
              <div class="col-12 fc-list-search">
                <div
                  class="fc-list-search-wrapper fc-list-search-wrapper-asset"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="32"
                    height="32"
                    viewBox="0 0 32 32"
                    class="search-icon-asset"
                  >
                    <title>search</title>
                    <path
                      d="M21.487 22.927l7.037 7.037c.473.473 1.238.475 1.71.003s.47-1.237-.003-1.71l-7.037-7.037c3.96-4.82 3.675-11.967-.846-16.487C17.539-.076 9.757-.092 4.966 4.699S.191 17.272 5 22.081c4.52 4.52 11.667 4.805 16.487.846zM6.679 6.413c3.848-3.848 10.099-3.836 13.962.027s3.875 10.114.027 13.962c-3.848 3.848-10.099 3.836-13.962-.027S2.831 10.261 6.679 6.413z"
                    />
                  </svg>
                  <input
                    ref="quickSearchQuery"
                    autofocus
                    type="text"
                    v-model="quickSearchQuery"
                    @keyup.enter="quickSearch"
                    placeholder="Search"
                    class="quick-search-input-asset"
                  />
                  <svg
                    @click="closeSearch"
                    xmlns="http://www.w3.org/2000/svg"
                    width="32"
                    height="32"
                    viewBox="0 0 32 32"
                    class="close-icon-asset"
                    aria-hidden="true"
                  >
                    <title>close</title>
                    <path
                      d="M17.992 16l8.796-8.796a1.409 1.409 0 0 0-1.992-1.992L16 14.008 7.204 5.212a1.409 1.409 0 0 0-1.992 1.992L14.008 16l-8.796 8.796a1.409 1.409 0 0 0 1.992 1.992L16 17.992l8.796 8.796a1.409 1.409 0 0 0 1.992-1.992L17.992 16z"
                    />
                  </svg>
                </div>
              </div>
            </div>
          </div>

          <div class="row sp-navbar2">
            <ul v-if="loading" class="sp-ul">
              <spinner :show="true" size="80"></spinner>
            </ul>
            <ul class="sp-ul mT0">
              <v-infinite-scroll
                :loading="loading"
                @bottom="nextPage"
                :offset="20"
                style="height: 100vh; padding-bottom: 100px;overflow-y: scroll;"
              >
                <div
                  class="menu-item space-secondary-color sp-li ellipsis f12 pointer asset-item p20"
                  @click="getLink(row.id)"
                  v-for="(row, index) in customModuleList"
                  :key="index"
                  v-bind:class="{ active: id === row.id }"
                >
                  <el-row>
                    <el-col :span="24">
                      <div
                        class="label truncate-text"
                        :title="row[mainFieldKey]"
                        v-tippy="{
                          placement: 'top',
                          animation: 'shift-away',
                          arrow: true,
                        }"
                      >
                        {{ row[mainFieldKey] }}
                      </div>
                      <div
                        class="flex-middle justify-content-space width100 pT10"
                      >
                        <div class="d-flex">
                          <div class="fc-id border-right7 pR10">
                            #{{ row.parentId }}
                          </div>

                          <div
                            class="uppercase f11 font-medium letter-spacing1 pL10 truncate-text width130px"
                            :class="[
                              getTicketStatusDisplayName(row) === 'Sent'
                                ? 'fc-green-color4'
                                : 'fc-grey7',
                            ]"
                          >
                            {{ getTicketStatusDisplayName(row) }}
                          </div>
                        </div>
                        <div class="fc-grey2-text12">
                          <i class="el-icon-time pR5"></i>
                          {{ row.sysCreatedTime | fromNow }}
                        </div>
                      </div>
                    </el-col>
                  </el-row>
                </div>
              </v-infinite-scroll>
            </ul>
            <ul v-if="loadingLists" class="sp-ul">
              <spinner :show="true" size="80"></spinner>
            </ul>
          </div>
        </div>
      </div>
      <div style="flex: 1;">
        <router-view
          @refreshSummaryList="
            () => {
              this.loadData(false, true)
            }
          "
          :key="id"
          :viewname="currentView"
        ></router-view>
      </div>
    </div>
  </div>
</template>
<script>
import CustomModuleOverviewList from 'pages/base-module-v2/ModuleListOverview'
import { API } from '@facilio/api'
import { mapGetters } from 'vuex'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
export default {
  extends: CustomModuleOverviewList,
  data() {
    return {
      quotationList: [],
      quickSearchQuery: '',
      canLoadMoreList: false,
    }
  },
  computed: {
    ...mapGetters(['getTicketStatus']),
    moduleName() {
      return 'quote'
    },
    mainFieldKey() {
      return 'subject'
    },
    customModuleList() {
      return this.quotationList
    },
    searchQuery() {
      return this.quickSearchQuery
    },
    canLoadMore() {
      return this.canLoadMoreList
    },
  },
  watch: {
    searchQuery() {
      this.loadData()
    },
  },
  components: {},
  methods: {
    initial() {
      let promises = [
        this.loadViews(),
        this.$store.dispatch('view/clearViews'),
        this.loadData(false, true),
      ]
      Promise.all(promises).then(() => {
        this.loadModuleMeta(this.moduleName)
      })
    },
    getLink(id) {
      if (id === null) return
      let { currentView, moduleName } = this
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}
        name &&
          this.$router.push({
            name,
            params: {
              viewname: currentView,
              id,
            },
            query: this.$route.query,
          })
      } else {
        this.$router.push({
          path: `/app/tm/quotation/${currentView}/${id}/overview`,
          query: this.$route.query,
        })
      }
    },
    nextPage() {
      if (!this.scrollDisabled && this.canLoadMoreList) {
        this.loadingLists = true
        this.fetchingMore = true
        this.page++
        this.loadData(true)
      }
    },
    async loadData(loadMore, forceFetch) {
      let { currentView, page } = this
      let queryObj = {
        viewName: currentView,
        page: page,
        filters: this.$route.query.search
          ? JSON.stringify(JSON.parse(this.$route.query.search))
          : '',
        search: this.searchQuery,
        includeParentFilter: currentView === 'filteredList' ? true : false,
        perPage: 50,
        withCount: true,
      }
      loadMore ? (this.fetchingMore = true) : (this.loading = true)
      let { list, error, meta } = await API.fetchAll(`quote`, queryObj, {
        force: forceFetch,
      })

      if (error) {
        let { message = 'Error Occured while fetching Quote list' } = error
        this.$message.error(message)
      } else {
        if (forceFetch) {
          this.quotationList = list
        } else {
          if (loadMore) {
            this.quotationList = [...(this.quotationList || []), ...list]
          }
        }
        let listCount = this.$getProperty(meta, 'pagination.totalCount', null)
        this.canLoadMoreList = listCount > (this.quotationList || []).length
      }
      this.loading = false
      this.loadingLists = false
      this.fetchingMore = false
    },
    back() {
      let { currentView, moduleName } = this
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.LIST) || {}
        name &&
          this.$router.push({
            name,
            params: {
              viewname: currentView,
            },
            query: this.$route.query,
          })
      } else {
        this.$router.push({
          path: `/app/tm/quotation/${currentView}`,
          query: this.$route.query,
        })
      }
    },
    getTicketStatusDisplayName(record) {
      return (
        this.getTicketStatus(
          this.$getProperty(record, 'moduleState.id', -1),
          this.moduleName
        ) || {}
      ).displayName
    },
  },
}
</script>
