<template>
  <div>
    <div class="height100 d-flex">
      <div style="flex: 0 0 300px; max-width: 300px;">
        <div
          class="height100vh full-layout-white fc-border-left fc-border-right"
        >
          <div class="row p15 fc-border-bottom pointer">
            <div class="col-1 text-left">
              <i
                class="el-icon-back fw6"
                @click="back"
                style="vertical-align: sub;"
              ></i>
            </div>
            <el-popover
              placement="bottom"
              width="250"
              v-model="toggle"
              popper-class="popover-height asset-popover "
              trigger="click"
              visible-arrow="true"
            >
              <ul>
                <li
                  @click="switchView(view)"
                  v-for="(view, index) in views"
                  :key="index"
                  :class="{ active: currentView === view.name }"
                >
                  {{ view.displayName }}
                </li>
              </ul>
              <span slot="reference" class="line-height20"
                >{{ currentViewDetail.displayName
                }}<i
                  class="el-icon-arrow-down el-icon-arrow-down-tv"
                  style="padding-left:8px"
                ></i
              ></span>
            </el-popover>
            <div class="pointer" @click="toggleQuickSearch">
              <i
                class="fa fa-search fa-search-asste-icon"
                aria-hidden="true"
              ></i>
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
                    ></path>
                  </svg>
                  <input
                    ref="quickSearchQuery"
                    autofocus
                    type="text"
                    v-model="quickSearchQuery"
                    @keyup.enter="loadAssetsList({ forceFetch: true })"
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
                    ></path>
                  </svg>
                </div>
              </div>
            </div>
          </div>

          <div class="row sp-navbar2">
            <ul class="sp-ul">
              <v-infinite-scroll
                :loading="loading"
                @bottom="nextPage"
                :offset="20"
                style="height: 100vh; padding-bottom: 100px;overflow-y: scroll;"
              >
                <div
                  class="menu-item space-secondary-color sp-li ellipsis f12 pointer asset-item p20"
                  @click="getAssetLink(asset.id)"
                  v-for="asset in assets"
                  :key="asset.id"
                  v-bind:class="{ active: currentassetId === asset.id }"
                >
                  <span
                    class="label"
                    :title="asset.name"
                    v-tippy="{
                      placement: 'top',
                      animation: 'shift-away',
                      arrow: true,
                    }"
                    >{{ asset.name }}</span
                  >
                </div>
              </v-infinite-scroll>
            </ul>
            <ul v-if="loading" class="sp-ul">
              <spinner :show="loading" size="80"></spinner>
            </ul>
            <ul v-if="loadingLists" class="sp-ul">
              <spinner :show="loadingLists" size="60"></spinner>
            </ul>
          </div>
        </div>
      </div>
      <div style="flex: 1;">
        <router-view :key="currentassetId"></router-view>
      </div>
    </div>
  </div>
</template>

<script>
import VInfiniteScroll from 'v-infinite-scroll'
import { mapState } from 'vuex'
import { isEmpty, isArray } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  data() {
    return {
      toggle: false,
      loading: true,
      loadingLists: false,
      page: 1,
      showQuickSearch: false,
      quickSearchQuery: null,
      fetchingMore: false,
      canLoadMore: true,
      assets: [],
    }
  },
  components: {
    VInfiniteScroll,
  },
  computed: {
    ...mapState({
      groupViews: state => state.view.groupViews,
      metaInfo: state => state.view.metaInfo,
    }),
    views() {
      let views = []

      if (this.filters) {
        views.push({
          displayName: 'Filtered Assets',
          name: 'filteredassets',
        })
      }
      if (this.groupViews && isArray(this.groupViews)) {
        this.groupViews.forEach(group => {
          views.push(...group.views)
        })
      }
      return views
    },
    currentassetId() {
      let assetId = this.$attrs.id || this.$route.params.assetid

      if (!isEmpty(assetId)) {
        return parseInt(assetId)
      }

      return null
    },
    scrollDisabled() {
      return this.loading || !this.canLoadMore || this.fetchingMore
    },
    currentView() {
      if (this.filters) {
        return 'filteredassets'
      }

      let viewName = this.$attrs.viewname || this.$route.params.viewname

      return viewName || 'all'
    },
    currentViewDetail() {
      if (this.filters) {
        return { displayName: 'Filtered Assets', name: 'filteredassets' }
      }
      return this.views.find(view => view.name === this.currentView) || {}
    },
    currentModuleName() {
      return (this.metaInfo || {}).name || 'asset'
    },
    filters() {
      let {
        query: { search },
      } = this.$route
      if (search) {
        return JSON.parse(search)
      }
      return null
    },
  },
  async created() {
    this.$store.dispatch('view/clearViews')
    await this.loadViews()

    let { moduleName } =
      this.assets.find(asset => asset.id === this.currentassetId) || {}

    this.loadModuleMeta(moduleName)
    this.loadAssetsList({ forceFetch: true })
  },
  watch: {
    assets(newVal) {
      if (!this.currentassetId) {
        this.getAssetLink(newVal[0].id)
      }
    },
    currentView(newVal, oldVal) {
      if (newVal && oldVal !== newVal) {
        this.loadAssetsList({ forceFetch: true })
      }
    },
  },
  methods: {
    async loadViews() {
      let param = {
        moduleName: this.currentModuleName || 'asset',
      }
      return await this.$store.dispatch('view/loadGroupViews', param)
    },
    loadModuleMeta(moduleName) {
      return this.$store.dispatch('view/loadModuleMeta', moduleName)
    },
    async switchView(view) {
      this.toggle = false

      let filterIndex = this.views.findIndex(
        view => view.name === 'filteredassets'
      )
      if (filterIndex !== -1) {
        this.views.splice(filterIndex, 1)
      }

      await this.loadModuleMeta(view.moduleName || 'asset')
      await this.loadAssetsList({ viewName: view.name })

      let { currentassetId, assets } = this

      let record = assets.find(({ id }) => id === currentassetId)
      if (isEmpty(record)) {
        record = !isEmpty(assets) ? assets[0] : {}
      }

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule('asset', pageTypes.OVERVIEW)
        name &&
          this.$router.replace({
            name,
            params: {
              viewname: view.name,
              id: record.id || currentassetId,
            },
            query: this.$route.query,
          })
      } else if (this.$helpers.isEtisalat()) {
        this.$router.replace({
          name: 'metersummary',
          params: {
            viewname: view.name,
            assetid: record.id || currentassetId,
          },
          query: this.$route.query,
        })
      } else {
        this.$router.replace({
          name: 'assetsummary',
          params: {
            viewname: view.name,
            assetid: record.id || currentassetId,
          },
          query: this.$route.query,
        })
      }
    },
    async loadAssetsList({ loadMore, viewName, forceFetch } = {}) {
      if (loadMore) {
        this.fetchingMore = true
        this.page++
      } else {
        this.loading = true
        this.page = 1
      }

      let { currentView, page } = this
      let queryObj = {
        viewName: viewName || currentView,
        page,
        filters: this.$route.query.search
          ? JSON.stringify(JSON.parse(this.$route.query.search))
          : '',
        search: this.quickSearchQuery,
        includeParentFilter: currentView === 'filteredList' ? true : false,
        perPage: 50,
        withCount: true,
      }

      let { list, error, meta } = await API.fetchAll(
        this.currentModuleName,
        queryObj,
        {
          force: forceFetch,
        }
      )

      if (error) {
        let { message = 'Error Occured while fetching Asset list' } = error
        this.$message.error(message)
      } else {
        if (forceFetch) {
          this.assets = list
        } else {
          if (loadMore) {
            this.assets = [...(this.assets || []), ...list]
          }
        }
        let listCount = this.$getProperty(meta, 'pagination.totalCount', null)
        this.canLoadMore = listCount > (this.assets || []).length
      }
      this.loading = false
      this.loadingLists = false
      this.fetchingMore = false
    },
    nextPage() {
      if (!this.scrollDisabled) {
        this.loadingLists = true
        this.fetchingMore = true
        this.loadAssetsList({ loadMore: true })
      }
    },
    closeSearch() {
      this.toggleQuickSearch()
      this.quickSearchQuery = null
      this.loadAssetsList({ forceFetch: true })
    },
    toggleQuickSearch() {
      this.showQuickSearch = !this.showQuickSearch
    },
    getAssetLink(id) {
      if (id === null) return
      let { currentView } = this

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule('asset', pageTypes.OVERVIEW)
        name &&
          this.$router.push({
            name,
            params: {
              viewname: currentView,
              id,
            },
            query: this.$route.query,
          })
      } else if (this.$helpers.isEtisalat()) {
        this.$router.push({
          name: 'metersummary',
          params: {
            viewname: currentView,
            assetid: id,
          },
          query: this.$route.query,
        })
      } else {
        this.$router.push({
          name: 'assetsummary',
          params: {
            viewname: currentView,
            assetid: id,
          },
          query: this.$route.query,
        })
      }
    },
    back() {
      let { currentView } = this

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule('asset', pageTypes.LIST)
        name &&
          this.$router.push({
            name,
            params: {
              viewname: currentView,
            },
            query: this.$route.query,
          })
      } else {
        let { params, query } = this.$route
        let { viewname } = params
        let viewName = viewname === 'filteredassets' ? 'all' : viewname
        let path

        if (this.$helpers.isEtisalat()) {
          path = `/app/home/assets/${viewName}`
        } else {
          path = `/app/at/assets/${viewName}`
        }

        this.$router.push({ path, query })
      }
    },
  },
}
</script>
<style>
.sp-navbar {
  padding: 0 15px;
}
.sp-navbar ul li {
  list-style: none;
  padding: 0px;
}
.sp-navbar ul li .menu-item,
.sp-navbar ul li .node-label {
  display: flex;
  align-items: center;
  padding: 10px;
  cursor: pointer;
}
.sp-navbar ul li .menu-item .label {
  margin-left: 8px;
}
.sp-navbar ul li.active .menu-item {
  background: #eef5f7;
}
.sp-ul {
  width: 100%;
  padding: 0px;
}
.sp-navbar .sp-ul li:hover .menu-item,
.sp-navbar .sp-ul li.active .menu-item .asset-item,
.menu-item:hover,
.asset-item.active {
  background: #eef5f7;
  cursor: pointer;
  color: #9666cf;
}

.sp-navbar li:hover .node-label,
.sp-navbar li.active .node-label {
  color: #9666cf;
}
.sp-navbar li .sp-sub-ul {
  display: none;
}
.sp-navbar li .sp-sub-ul li {
  padding-left: 0px;
}
.sp-navbar li.active .sp-sub-ul {
  display: block;
}
.sp-sub-ul li:hover .sub-menu-item,
.sp-sub-ul li.active .sub-menu-item {
  cursor: pointer;
  color: #9666cf;
}
.sp-sub-ul li .sub-menu-item {
  padding: 10px 0px;
}
.fc-list-search-wrapper .search-icon-asset {
  width: 14px;
  fill: #6f7c87;
  height: 20px;
  top: 12px;
  left: 6px;
  position: absolute;
  opacity: 0.8;
}
.fc-list-search-wrapper .quick-search-input-asset {
  transition: 0.2s linear;
  padding: 10px 40px 8px 38px !important;
  line-height: 1.8;
  width: 100%;
  border: none;
  outline: none;
  background: #fff;
  border-bottom: 1px solid #6f7c87 !important;
  border-radius: none !important;
  height: 47px;
}

.fc-list-search-wrapper .close-icon-asset {
  width: 14px;
  fill: #6f7c87;
  height: 20px;
  position: absolute;
  right: 10px;
  top: 14px;
  cursor: pointer;
}
.fc-list-search-wrapper-asset {
  position: absolute;
  left: 0;
  width: 100%;
  top: 1px;
}
.fa-search-asste-icon {
  position: absolute;
  right: 10px;
  top: 16px;
}
.fc-list-search-wrapper-asset
  input[type='text']:not(.q-input-target):not(.quick-search-input):not(.el-input__inner):not(.el-select__input) {
  border: none;
  border-radius: 0;
}
.sp-navbar2 {
  height: 100%;
  padding: 0 0 100px !important;
  overflow-x: hidden;
  overflow-y: scroll !important;
}
.asset-popover {
  padding: 0;
}
.menu-item .label {
  font-size: 14px;
  color: #324056;
  letter-spacing: 0.5px;
  font-weight: 400;
}
.asset-item {
  border-bottom: 1px solid #f4f6f8;
}
</style>
