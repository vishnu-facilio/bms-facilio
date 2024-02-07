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
              <span slot="reference" class="line-height20">
                {{ currentViewDetail.displayName }}
                <i
                  class="el-icon-arrow-down el-icon-arrow-down-tv"
                  style="padding-left:8px"
                ></i>
              </span>
            </el-popover>
            <!-- <div class="pointer" @click="toggleQuickSearch">
              <i
                class="fa fa-search fa-search-asste-icon"
                aria-hidden="true"
              ></i>
            </div>-->
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
            <ul class="sp-ul">
              <v-infinite-scroll
                :loading="loading"
                @bottom="nextPage"
                :offset="20"
                style="height: 100vh; padding-bottom: 100px;overflow-y: scroll;"
              >
                <div
                  class="menu-item space-secondary-color sp-li ellipsis f12 pointer asset-item p20"
                  @click="getLink(row.id)"
                  v-for="row in customModuleList"
                  :key="row.id"
                  v-bind:class="{ active: id === row.id }"
                >
                  <span
                    class="label"
                    :title="row[mainFieldKey]"
                    v-tippy="{
                      placement: 'top',
                      animation: 'shift-away',
                      arrow: true,
                    }"
                    >{{ row[mainFieldKey] }}</span
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
        <router-view :key="id"></router-view>
      </div>
    </div>
  </div>
</template>
<script>
import VInfiniteScroll from 'v-infinite-scroll'
import { isEmpty } from '@facilio/utils/validation'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import { mapState } from 'vuex'

export default {
  data() {
    return {
      toggle: false,
      selectindex: false,
      loading: true,
      loadingLists: false,
      page: 1,
      showQuickSearch: false,
      quickSearchQuery: null,
      fetchingMore: false,
    }
  },
  components: {
    VInfiniteScroll,
  },
  watch: {
    currentView(newVal, oldVal) {
      if (newVal && oldVal !== newVal) {
        this.init()
      }
    },
    $route: {
      handler(newVal, oldVal) {
        if (newVal && oldVal !== newVal) {
          this.init()
        }
      },
    },
    moduleName(newVal, oldVal) {
      if (newVal && oldVal !== newVal) {
        this.loadData()
      }
    },
    customModuleList(newVal) {
      if (!this.id) {
        this.getLink(newVal[0].id)
      }
    },
  },
  created() {
    this.initial()
  },
  computed: {
    ...mapState({
      canLoadMore: state => state.customModule.canLoadMore,
      groupViews: state => state.view.groupViews,
      customModuleList: state => state.customModule.customModuleList,
      searchQuery: state => state.customModule.quickSearchQuery,
    }),
    parentPath() {
      if (this.$helpers.isEtisalat()) {
        return `/app/${this.getEtisalatRouterName().modulePath}`
      } else {
        return '/app/ca/modules'
      }
    },
    scrollDisabled() {
      return this.loading || !this.canLoadMore || this.fetchingMore
    },
    id() {
      let paramId = this.$attrs.id || this.$route.params.id
      let id =
        paramId && paramId !== 'null' ? parseInt(this.$route.params.id) : ''

      return id
    },
    views() {
      let views = []
      if (this.groupViews && Array.isArray(this.groupViews)) {
        this.groupViews.forEach(group => {
          views.push(...group.views)
        })
      }
      return views
    },
    moduleName() {
      return this.$route.params.moduleName
        ? this.$route.params.moduleName
        : this.$attrs.moduleName || ''
    },
    currentView() {
      let filteredListView = this.views.find(
        view => view.name === 'filteredList'
      )
      if (this.$route.query.search && isEmpty(filteredListView)) {
        this.views.push({ displayName: 'Filtered List', name: 'filteredList' })
      }
      if (this.$route.query.search) {
        return 'filteredList'
      }
      if (!isEmpty(this.$attrs.viewname)) {
        return this.$attrs.viewname
      }
      if (
        !isEmpty(this.$route.params.viewname) ||
        !isEmpty(this.$route.params.viewName)
      ) {
        return this.$route.params.viewname || this.$route.params.viewName
      }

      return 'all'
    },
    currentViewDetail() {
      if (this.$route.query.search) {
        return { displayName: 'Filtered List', name: 'filteredList' }
      }
      return this.views.find(view => view.name === this.currentView) || {}
    },
    mainFieldKey() {
      return 'name'
    },
  },
  methods: {
    getLink(id) {
      if (id === null) return
      let view

      if (this.currentView === 'filteredList') {
        view =
          this.$attrs.viewname ||
          this.$route.params.viewname ||
          this.$route.params.viewName
      } else {
        view = this.currentView
      }

      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForModule(this.moduleName, pageTypes.OVERVIEW) || {}
        name &&
          this.$router.push({
            name,
            params: { id, viewname: view },
            query: this.$route.query,
          })
      } else {
        this.$router.push({
          path: `${this.parentPath}/${this.moduleName}/${view}/${id}/summary`,
          query: this.$route.query,
        })
      }
    },
    init() {
      this.loadData()
    },
    initial() {
      this.$store.dispatch('view/clearViews')
      this.loadData()
      let params = {
        id: this.id,
        moduleName: this.moduleName,
      }
      let promises = [
        this.loadViews(),
        this.$store.dispatch('customModule/fetch', params),
      ]
      Promise.all(promises).then(() => {
        this.loadModuleMeta(this.moduleName)
      })
    },
    loadModuleMeta(moduleName) {
      return this.$store.dispatch('view/loadModuleMeta', moduleName)
    },
    switchView(view) {
      let { id, parentPath, moduleName } = this
      let filterIndex = this.views.findIndex(
        view => view.name === 'filteredList'
      )

      if (filterIndex !== -1) {
        this.views.splice(filterIndex, 1)
      }

      this.loadModuleMeta(view.moduleName).then(() => {
        if (isWebTabsEnabled()) {
          let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW)
          name &&
            this.$router.replace({
              name,
              params: {
                viewname: view.name,
                id: null,
              },
            })
        } else {
          this.$router.replace({
            path: `${parentPath}/${moduleName}/${view.name}/${id}/summary`,
          })
        }
      })
      this.page = 1
      this.toggle = false
    },
    loadViews() {
      let { moduleName } = this
      let param = {
        moduleName: moduleName,
      }
      return this.$store.dispatch('view/loadGroupViews', param)
    },
    nextPage() {
      if (!this.scrollDisabled) {
        this.loadingLists = true
        this.fetchingMore = true
        this.loadData(true)
      }
    },
    loadData(loadMore) {
      let { moduleName, currentView, page } = this
      let viewName
      this.page = loadMore ? this.page : 1
      if (currentView === 'filteredList') {
        viewName = this.$route.params.viewname || this.$route.params.viewName
      } else {
        viewName = currentView
      }
      let queryObj = {
        viewname: viewName,
        page: page,
        filters: this.$route.query.search
          ? JSON.parse(this.$route.query.search)
          : '',
        includeParentFilter: true,
        moduleName: moduleName,
      }
      loadMore ? (this.fetchingMore = true) : (this.loading = true)
      this.$store
        .dispatch('customModule/fetchCustomModuleList', queryObj)
        .then(response => {
          this.loading = false
          this.loadingLists = false
          this.fetchingMore = false
          this.page++
        })
        .catch(error => {
          if (error) {
            this.loading = false
            this.fetchingMore = false
          }
        })
    },
    toggleQuickSearch() {
      this.showQuickSearch = !this.showQuickSearch
    },
    closeSearch() {
      this.toggleQuickSearch()
      this.quickSearchQuery = null
      this.quickSearch()
    },
    quickSearch() {
      this.$store.dispatch(
        'customModule/updateSearchQuery',
        this.quickSearchQuery
      )
    },
    back() {
      let { moduleName, currentView, $route } = this

      if (currentView === 'filteredList') {
        currentView =
          this.$attrs.viewname ||
          this.$route.params.viewname ||
          this.$route.params.viewName
      }

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.LIST)
        name &&
          this.$router.push({
            name,
            params: {
              viewname: currentView,
            },
            query: $route.query,
          })
      } else {
        this.$router.push({
          path: `${this.parentPath}/${moduleName}/${currentView}`,
        })
      }
    },
    getEtisalatRouterName() {
      let data = {
        new: 'custommodules-new',
        edit: 'custommodules-edit',
        list: 'custommodules-list',
        summary: 'custommodules-summary',
      }
      if (this.$route && this.$route.name) {
        let { name } = this.$route
        if (
          name === 'et1custommodules-list' ||
          name === 'et1custommodules-new' ||
          name === 'et1custommodules-edit' ||
          name === 'et1custommodules-summary'
        ) {
          ;(data['new'] = 'et1custommodules-new'),
            (data['edit'] = 'et1custommodules-edit')
          data['list'] = 'et1custommodules-edit'
          data['summary'] = 'et1custommodules-summary'

          data['modulePath'] = 'supp'
        } else if (
          name === 'et-custommodules-list' ||
          name === 'et-custommodules-edit' ||
          name === 'et-custommodules-new' ||
          name === 'et-custommodules-summary'
        ) {
          ;(data['new'] = 'et-custommodules-new'),
            (data['edit'] = 'et-custommodules-edit')
          data['list'] = 'et-custommodules-list'
          data['summary'] = 'et-custommodules-summary'
          data['modulePath'] = 'al'
        } else if (
          name === 'et2-custommodules-list' ||
          name === 'et2-custommodules-edit' ||
          name === 'et2-custommodules-new' ||
          name === 'et2-custommodules-summary'
        ) {
          ;(data['new'] = 'et2-custommodules-new'),
            (data['edit'] = 'et2-custommodules-edit')
          data['list'] = 'et2-custommodules-list'
          data['summary'] = 'et2-custommodules-summary'
          data['modulePath'] = 'home'
        }
      }
      return data
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
