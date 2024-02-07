<template>
  <div class="layout container maxwidth100">
    <subheader
      v-if="!$route.path.includes('summary')"
      :menu="subheaderMenu"
      newbtn="false"
      type="rules"
      parent="/app/fa/rules"
      :listCount="listCount"
      @rearrange="showViewSettings = true"
      :showCurrentViewOnly="showQuickSearch"
    >
      <template slot="prefix">
        <div class="fL fc-subheader-left">
          <div class="pR15 pointer">
            <img
              src="~assets/three-line-menu.svg"
              width="14"
              height="14"
              class="wo-three-line"
              @click.stop="showSideView = true"
            />
            <el-dropdown
              class="fc-dropdown-menu pL10"
              @command="openChild"
              v-if="selectedList !== 'System Views' && views"
              trigger="click"
            >
              <span class="el-dropdown-link">
                {{ selectedList }}
                <i
                  class="el-icon-arrow-down el-icon--right"
                  v-if="!showQuickSearch"
                ></i>
              </span>
              <el-dropdown-menu
                slot="dropdown"
                v-if="views && !showQuickSearch"
              >
                <el-dropdown-item
                  v-for="(view, key) in views"
                  :key="key"
                  :command="view.displayName"
                  >{{ view.displayName }}</el-dropdown-item
                >
              </el-dropdown-menu>
            </el-dropdown>
          </div>
          <div
            v-if="views && selectedList !== 'System Views'"
            class="fc-separator-lg mL10 mR10"
          ></div>
        </div>
      </template>
      <div class="fR fc-subheader-right">
        <template v-if="!showQuickSearch">
          <div class="pL15 fc-black-small-txt-12">
            <pagination :total="listCount" :perPage="30"></pagination>
            <div class="block"></div>
          </div>
          <span class="separator" v-if="listCount > 0">|</span>
          <div>
            <i
              class="el-icon-search fc-black-2 f16 pointer fw-bold"
              @click.stop="toggleQuickSearch()"
            ></i>
          </div>
          <div class="pL10" v-show="!showQuickSearch">
            <el-dropdown @command="handleNewRule">
              <button
                v-if="$hasPermission('alarmrules:CREATE')"
                class="fc-create-btn create-btn mL20"
              >
                {{ $t('common.products.new_rule') }}
                <i class="el-icon-arrow-down pL10 fc-white-14 bold"></i>
              </button>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="library">{{
                  $t('common.products.add_from_library')
                }}</el-dropdown-item>
                <el-dropdown-item command="custom">{{
                  $t('common.products.new_custom_rule')
                }}</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </template>
        <!-- View When SearchBox Is Acessed -->
        <div
          class="fc-black-small-txt-12 fc-subheader-right-search"
          v-show="showQuickSearch"
        >
          <new-search
            :config="filterConfig"
            :isAssetsCategory="true"
            :loadViews="true"
            @loadView="loadViews()"
            @hideSearch="showQuickSearch = false"
            :quickSearchQuery="quickSearchQuery"
            :save="save"
            :saveAs="saveAs"
            :resetFilters="resetFilters"
            :showSearch="showQuickSearch"
            :defaultFilter="defaultFilter"
            :moduleName="'readingrule'"
          ></new-search>
          <div class="filter-search-close"></div>
        </div>
        <!-- <div class="row" style="margin-right: 20px" v-if="showQuickSearch">
        <div class="col-12 fc-list-search" style="margin-top: -18px; width: 300px;">
          <div class="fc-list-search-wrapper relative">
            <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 32 32" class="search-icon"><title>{{$t("common._common.search")}}</title><path d="M21.487 22.927l7.037 7.037c.473.473 1.238.475 1.71.003s.47-1.237-.003-1.71l-7.037-7.037c3.96-4.82 3.675-11.967-.846-16.487C17.539-.076 9.757-.092 4.966 4.699S.191 17.272 5 22.081c4.52 4.52 11.667 4.805 16.487.846zM6.679 6.413c3.848-3.848 10.099-3.836 13.962.027s3.875 10.114.027 13.962c-3.848 3.848-10.099 3.836-13.962-.027S2.831 10.261 6.679 6.413z"></path></svg>
            <input ref="quickSearchQuery" autofocus type="text" v-model="quickSearchQuery" @keyup.enter="quickSearch" :placeholder="$t('common._common.search')" class="quick-search-input">
            <svg @click="closeSearch" xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 32 32" class="close-icon" aria-hidden="true"><title>{{$t("common._common.close")}}</title><path d="M17.992 16l8.796-8.796a1.409 1.409 0 0 0-1.992-1.992L16 14.008 7.204 5.212a1.409 1.409 0 0 0-1.992 1.992L14.008 16l-8.796 8.796a1.409 1.409 0 0 0 1.992 1.992L16 17.992l8.796 8.796a1.409 1.409 0 0 0 1.992-1.992L17.992 16z"></path></svg>
          </div>
        </div>
        </div>-->
      </div>
    </subheader>
    <div class="height100 row">
      <div v-if="appliedFilters !== null" class="width100">
        <div class="fL" style="width: 84%;">
          <new-tag
            :config="filterConfig"
            :isAssetsCategory="true"
            :filters="appliedFilters"
            :showFilterAdd="showAddFilter"
            :showCloseIcon="true"
            class="layout-new-tag"
          ></new-tag>
        </div>
        <div class="save-btn-section">
          <div v-if="appliedFilters">
            <!-- <div v-if="!viewDetail.isDefault">
          <el-dropdown @command="savingView">
          <el-button type="primary" class="subheader-saveas-btn">
                {{$t('common._common.save_filters')}}<i class="el-icon-arrow-down el-icon--right"></i>
                </el-button>
          <el-dropdown-menu slot="dropdown">
          <el-dropdown-item command="Save">{{$t('common._common._save')}}</el-dropdown-item>
          <el-dropdown-item command="Save As">{{$t('common._common.save_as')}}</el-dropdown-item>
          </el-dropdown-menu>
          </el-dropdown>
          <div class="clear-filter" v-if="Object.keys(appliedFilters).length > '0'"  @click="resetFilters = !resetFilters">{{$t('common._common.clear_all_filters')}}</div>
            </div>-->
            <div>
              <!-- <el-button class="subheader-saveas-btn saveas" v-if="Object.keys(appliedFilters).length > '0'"  @click="saveAs = !saveAs">{{$t('common._common.save_filters')}}</el-button> -->
              <div
                class="clear-filter"
                v-if="Object.keys(appliedFilters).length > '0'"
                @click="resetFilters = !resetFilters"
              >
                {{ $t('common._common.clear_all_filters') }}
              </div>
            </div>
          </div>
        </div>
      </div>
      <div v-show="showSideView">
        <views-list
          class="rule-views-list"
          :config="filterConfig"
          :newGroup="true"
          :preparingViews.sync="preparingViews"
          :views.sync="views"
          :showEditIcon="showEditIcon"
          :showListWorkorder.sync="showSideView"
          moduleName="readingRule"
          :mainViewsList="mainViewsList"
        ></views-list>
      </div>
      <div class="height100 f-list-view" v-bind:class="{ '': showFilter }">
        <router-view
          @syncCount="callbackMethod"
          ref="rulesLayout"
        ></router-view>
      </div>
    </div>
    <!-- // <view-customization :visible.sync="showViewSettings" :reload="true" @onchange="callMethod()" :menu="subheaderMenu" moduleName="readingrule"></view-customization> -->
  </div>
</template>
<script>
import Subheader from '@/Subheader'
import ViewsList from '@/ViewsList'
import { mapActions, mapState } from 'vuex'
import Pagination from '@/list/FPagination'
import NewSearch from '@/NewSearch'
import NewTag from '@/NewTag'
import { isWebTabsEnabled, findRouteForTab, pageTypes } from '@facilio/router'
import { isEmpty } from '@facilio/utils/validation'

export default {
  data() {
    return {
      listCount: '',
      quickSearchQuery: null,
      showQuickSearch: false,
      showSideView: false,
      showAddFilter: true,
      selectedList: null,
      filterSelected: false,
      showMail: true,
      defaultFilter: 'name',
      showEditIcon: false,
      save: false,
      saveAs: false,
      resetFilters: false,
      showViewSettings: false,
      mainViewsList: {
        systemviews: 'System Views',
        customviews: 'Custom Views',
      },
      filterConfig: {
        moduleName: 'readingrule',
        path: '/app/fa/rules/',
        data: {
          name: {
            label: this.$t('common.products.name'),
            displayType: 'string',
            value: [],
          },
          assetCategory: {
            label: this.$t('maintenance.wr_list.category'),
            displayType: 'select',
            options: {},
            value: [],
            key: 'assetCategoryId',
          },
          severity: {
            label: this.$t('alarm.alarm.severity'),
            displayType: 'select',
            options: {},
            value: [],
            key: 'alarmSeverityId',
          },
        },
        availableColumns: [],
        saveView: true,
        includeParentCriteria: true, // Temporary
        disableColumnCustomization: true,
      },
    }
  },
  created() {
    this.$store.dispatch('loadAlarmSeverity')
    this.$store.dispatch('view/clearViews')
  },
  watch: {
    filters: function(val) {
      if (val != null && !this.filterSelected) {
        this.toggleViewFilter()
      }
    },
    currentView(newVal, oldVal) {
      if (newVal && oldVal !== newVal) {
        this.getViewDetail()
      }
    },
  },
  components: {
    Subheader,
    NewSearch,
    Pagination,
    ViewsList,
    NewTag,
  },
  computed: {
    ...mapActions({
      loadModuleMeta: 'view/loadModuleMeta',
      loadViewDetail: 'view/loadViewDetail',
      savesorting: 'view/savesorting',
    }),
    ...mapState({
      viewDetail: state => state.view.currentViewDetail,
    }),
    showFilter() {
      return this.filterSelected && this.$route.path.indexOf('/summary/') === -1
    },
    filters() {
      return this.$route.query.search
        ? JSON.parse(this.$route.query.search)
        : null
    },
    currentView() {
      if (this.$route.params.viewname) {
        return this.$route.params.viewname
      }
      return null
    },
    views() {
      return this.$store.state.view.groupViews
    },
    preparingViews() {
      return this.views
    },
    subheaderMenu() {
      return this.getSubHeaderMenu()
    },
    showSubheader() {
      return this.$route.path.indexOf('/rules/new') === -1
    },
    appliedFilters() {
      if (
        this.$route.query.search &&
        !this.$route.fullPath.includes('newsummary')
      ) {
        return JSON.parse(this.$route.query.search)
      }
      return null
    },
  },
  mounted() {
    this.loadViews()
    this.$store.dispatch('view/loadModuleMeta', 'readingrule')
    this.getViewDetail()
    if (!this.filterSelected && this.filters) {
      this.toggleViewFilter()
    }
  },
  methods: {
    callMethod() {
      this.loadViews()
    },
    loadViews() {
      let param = {
        moduleName: 'readingrule',
      }
      this.$store.dispatch('view/loadGroupViews', param).then(() => {
        this.defaultViews()
      })
    },
    defaultViews() {
      if (!this.$route?.params?.viewname && this.views.length) {
        let firstFolder = this.views[0]
        let firstView = this.$getProperty(firstFolder, 'views.0.name')
        if (firstFolder.views && !isEmpty(firstView)) {
          if (isWebTabsEnabled()) {
            let { path } = findRouteForTab(pageTypes.RULES_LIST) || {}
            this.$router.push({
              path:
                '/maintenance/' + path.replace(':viewname?', firstView.name),
            })
          } else {
            this.$router.push({
              path: '/app/fa/rules/' + firstView.name,
            })
          }
        }
      }
    },
    openChild(command) {
      let self = this
      let a2
      self.selectedList = null
      self.selectedList = command
      let key1 = self.views.find(rt => rt.displayName === command)
      if (key1.displayName === command) {
        a2 = key1.views
      }
      if (a2) {
        if (isWebTabsEnabled()) {
          let { path } = findRouteForTab(pageTypes.RULES_LIST) || {}
          self.$router.push({
            path: '/maintenance/' + path.replace(':viewname?', a2[0].name),
          })
        } else {
          self.$router.push({
            path: '/app/fa/rules/' + a2[0].name,
          })
        }
      }
    },
    openRulesSummary() {
      // this.$refs.rulesLayout.showDialog = true
      let url = '/app/fa/rules/templates'
      this.$router.push({ path: url })
    },
    callbackMethod(newVal) {
      this.listCount = newVal
    },
    getViewDetail() {
      this.$store.dispatch('view/loadViewDetail', {
        viewName: this.currentView,
        moduleName: 'readingrule',
      })
    },
    closeSearch() {
      this.toggleQuickSearch()
      this.quickSearchQuery = null
      this.quickSearch()
    },
    toggleQuickSearch() {
      this.showQuickSearch = !this.showQuickSearch
    },
    quickSearch() {
      // let queryParam = this.$route.query
      // queryParam.searchText
      // queryParam.searchText = this.quickSearchQuery
      // this.$router.push({path: this.$route.path, query: queryParam})
      this.$router.replace({ query: { searchText: this.quickSearchQuery } })
    },
    toggleViewFilter() {
      this.filterSelected = !this.filterSelected
    },
    getSubHeaderMenu() {
      let a1 = []
      let self = this
      if (self.currentView) {
        for (let i = 0; i < self.views.length; i++) {
          let lists = self.views[i].views
          if (lists) {
            let key1 = lists.find(rt => rt.name === self.currentView)
            if (key1) {
              self.selectedList = self.views[i].displayName
              a1 = self.views[i].views
            }
            self.mainViewsList[self.views[i].name] = self.views[i].displayName
          }
        }
      } else {
        for (let i = 0; i < self.views.length; i++) {
          let lists = self.views[i].views
          if (lists) {
            let primaryView = lists.find(view => view.primary)
            if (primaryView) {
              self.selectedList = self.views[i].displayName
              a1 = self.views[i].views
            }
            self.mainViewsList[self.views[i].name] = self.views[i].displayName
          }
        }
        if (!self.selectedList && self.views.length) {
          let a = 'systemviews'
          let key1 = self.views.find(rt => rt.name === a)
          if (key1) {
            self.selectedList = key1.displayName
            a1 = key1.views
          }
        }
      }
      if (!self.selectedList) {
        return []
      }
      if (a1.length > 0) {
        let routePath = {}
        let { path } = {}
        if (isWebTabsEnabled()) {
          path = findRouteForTab(pageTypes.RULES_LIST) || {}
        } else {
          routePath = '/app/fa/rules/'
        }
        return a1.map(view => ({
          label: view.displayName,
          path: {
            path: isWebTabsEnabled()
              ? '/maintenance/' + path.path.replace(':viewname?', view.name)
              : routePath + view.name,
          },
          permission: 'alarm:READ,READ_TEAM,READ_OWN',
          id: view.id,
          name: view.name,
          isCustom: !view.isDefault,
          primary: view.primary,
        }))
      }
      return []
    },
    handleNewRule(option) {
      if (option == 'library') {
        this.openRulesSummary()
      }
      if (option == 'custom') {
        if (isWebTabsEnabled()) {
          let { name } = findRouteForTab(pageTypes.RULES_CREATE) || {}
          name &&
            this.$router.push({
              name,
              query: this.$route.query,
            })
        } else {
          this.$router.push({ path: `/app/fa/rule/new` })
        }
      }
    },
  },
}
</script>
<style>
.rule-views-list .workorder-list-container {
  top: 0;
}
</style>
