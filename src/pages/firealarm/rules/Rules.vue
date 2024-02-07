<template>
  <div>
    <div
      class="height100 fc-list-table-container fc-table-td-height rules-list-table rules-list-page"
      :class="$route.query.search ? 'fc-list-table-search-scroll' : ''"
    >
      <div v-if="openRuleId === -1">
        <div v-if="loading" class="flex-middle fc-empty-white p10 mT10 mL10">
          <spinner :show="loading" size="80"></spinner>
        </div>
        <div
          v-if="$validation.isEmpty(rules) && !loading"
          class="height100vh fc-empty-white flex-middle justify-content-center flex-direction-column"
        >
          <inline-svg
            src="svgs/list-empty"
            iconClass="icon text-center icon-xxxxlg"
          ></inline-svg>
          <div class="mT10 fc-black-dark f16 fw6">
            <div class="mT10 label-txt-black f14">
              {{ $t('setup.users_management.no_rules_available') }}
            </div>
          </div>
        </div>
        <div
          class="fc-full-table-wrap"
          v-if="!loading && !$validation.isEmpty(rules)"
        >
          <div class="pT0 height100 fc-list-table-container">
            <el-table :data="rules" style="width: 100%" height="100%">
              <el-table-column
                fixed
                prop="id"
                :label="$t('common._common.id')"
                width="110"
                class="pL0"
              >
                <template v-slot="rule">
                  <div class="fc-id">{{ '#' + rule.row.id }}</div>
                </template>
              </el-table-column>
              <el-table-column
                fixed
                prop="id"
                :label="$t('common._common.rule_id')"
                width="350"
                class="pL0"
              >
                <template v-slot="rule">
                  <div @click="openSummaryRules(rule.row.id)">
                    <div
                      class="table-subject-heading fw5 ellipsis textoverflow-ellipsis width280px"
                      v-tippy
                      small
                      :title="rule.row.name"
                    >
                      {{ rule.row.name }}
                    </div>
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="assetCategoryId" label="Type" width="100">
                <template v-slot="rule">
                  <div class="table-subheading">
                    {{ rule.row.assetCategoryId > 0 ? 'Asset' : 'Space' }}
                  </div>
                </template>
              </el-table-column>
              <el-table-column
                prop="assetCategoryId"
                :label="$t('common._common.category')"
                width="150"
              >
                <template v-slot="rule">
                  <div
                    @click="openSummaryRules(rule.row.id)"
                    class="table-subheading"
                  >
                    {{
                      rule.row.assetCategoryId > 0
                        ? getCategoryName(rule.row.assetCategoryId)
                        : 'Space'
                    }}
                  </div>
                </template>
              </el-table-column>
              <el-table-column
                :label="$t('common._common.space/asset')"
                width="200"
              >
                <template v-slot="rule">
                  <div class="table-subheading">
                    {{ getResourceName(rule.row) }}
                  </div>
                </template>
              </el-table-column>
              <el-table-column
                prop="alarmSeverityId"
                :label="$t('common._common.severity')"
                width="180"
              >
                <template v-slot="rule">
                  <div class="table-subheading">
                    <i
                      class="fa fa-circle prioritytag"
                      v-if="rule.row.alarmSeverityId"
                      v-bind:style="{
                        color: getAlarmSeverity(rule.row.alarmSeverityId).color,
                      }"
                      aria-hidden="true"
                    ></i>
                    {{
                      rule.row.alarmSeverityId
                        ? getAlarmSeverity(rule.row.alarmSeverityId).displayName
                        : '--'
                    }}
                  </div>
                </template>
              </el-table-column>
              <el-table-column
                prop="status"
                :label="$t('common._common.status')"
                width="100"
              >
                <template v-slot="rule">
                  <el-switch
                    v-model="rule.row.status"
                    @change="changeRuleStatus(rule.row)"
                    class="Notification-toggle"
                    active-color="rgba(57, 178, 194, 0.8)"
                    inactive-color="#e5e5e5"
                  ></el-switch>
                </template>
              </el-table-column>
              <el-table-column
                prop
                label
                width="100"
                class="visibility-visible-actions"
                fixed="right"
              >
                <template v-slot="rule">
                  <div class="text-center">
                    <i
                      v-if="
                        $hasPermission(
                          'alarmrules:UPDATE,UPDATE_TEAM,UPDATE_OWN'
                        )
                      "
                      class="el-icon-edit pointer visibility-hide-actions"
                      @click="editThresholdRule(rule.row)"
                    ></i>
                    <i
                      v-if="$hasPermission('alarmrules:DELETE')"
                      class="el-icon-delete pointer visibility-hide-actions mL10"
                      @click="deleteRule(rule.row)"
                    ></i>
                  </div>
                </template>
              </el-table-column>
              <el-table-column>
                <template></template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </div>

      <div class="fc-column-view height100" v-else>
        <div class="row fc-column-view-title height100">
          <div class="rules-left-side">
            <div class="fc__layout__has__row fc__layout__box">
              <!--sub header -->
              <div
                class="fc__layout__has__row fc__submenu__left fc__layout__box"
              >
                <div
                  class="fc__layout_media_center fc__submenu__header fc__layout__has__columns pointer"
                >
                  <i class="el-icon-back fw6" @click="back"></i>
                  <div class="label-txt-black pL10">
                    <el-popover
                      placement="bottom"
                      width="200"
                      v-model="toggle"
                      popper-class="popover-height inventory-list-popover"
                      trigger="click"
                    >
                      <!-- hiding drop down tempeory -->
                      <ul>
                        <li
                          @click="switchCategory(view)"
                          v-for="(view, index) in views"
                          :key="index"
                          :class="{
                            active:
                              currentViewDetail.displayName ===
                              view.displayName,
                          }"
                        >
                          {{ view.displayName }}
                        </li>
                      </ul>
                      <span slot="reference">
                        {{ currentViewDetail.displayName }}
                        <i
                          class="el-icon-arrow-down el-icon-arrow-down-tv"
                          style="padding-left:8px"
                        ></i>
                      </span>
                    </el-popover>
                  </div>
                  <div class="pointer" @click="toggleQuickSearch">
                    <i
                      class="fa fa-search asset__search__icon"
                      aria-hidden="true"
                    ></i>
                  </div>
                  <div class="row" v-if="showQuickSearch">
                    <div class="col-12 fc-list-search">
                      <div
                        class="fc-list-search-wrapper asset__inventory_search__con"
                      >
                        <svg
                          xmlns="http://www.w3.org/2000/svg"
                          width="32"
                          height="32"
                          viewBox="0 0 32 32"
                          class="search-icon-asset hide"
                        >
                          <title>{{ $t('common._common.search') }}</title>
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
                          :placeholder="$t('common._common.search')"
                          class="quick-search-input-asset asset__inventory__search"
                        />
                        <svg
                          @click="closeSearch"
                          xmlns="http://www.w3.org/2000/svg"
                          width="32"
                          height="32"
                          viewBox="0 0 32 32"
                          class="asset__inventory__close"
                          aria-hidden="true"
                        >
                          <title>{{ $t('common._common.close') }}</title>
                          <path
                            d="M17.992 16l8.796-8.796a1.409 1.409 0 0 0-1.992-1.992L16 14.008 7.204 5.212a1.409 1.409 0 0 0-1.992 1.992L14.008 16l-8.796 8.796a1.409 1.409 0 0 0 1.992 1.992L16 17.992l8.796 8.796a1.409 1.409 0 0 0 1.992-1.992L17.992 16z"
                          />
                        </svg>
                      </div>
                    </div>
                  </div>
                </div>
                <v-infinite-scroll
                  :loading="subListLoading"
                  @bottom="nextPage"
                  :offset="20"
                  style="height: 100vh; padding-bottom: 150px;overflow-y: scroll;"
                >
                  <div
                    class="menu-item space-secondary-color sp-li ellipsis f12 pointer rule-item p20"
                    @click="openSummaryRules(rule.id)"
                    v-for="rule in rules"
                    :key="rule.id"
                    v-bind:class="{ fcactivelist: openRuleId === rule.id }"
                  >
                    <span
                      class="label"
                      :title="rule.name"
                      v-tippy="{
                        placement: 'top',
                        animation: 'shift-away',
                        arrow: true,
                      }"
                      >{{ rule.name }}</span
                    >
                  </div>
                  <div
                    v-if="subListLoading"
                    class="menu-item space-secondary-color sp-li ellipsis f12 pointer rule-item p20"
                  >
                    <spinner :show="subListLoading" size="80"></spinner>
                  </div>
                </v-infinite-scroll>
              </div>
            </div>
          </div>
          <div class="fc-column-view-right-approval">
            <div slot="right">
              <router-view name="summary"></router-view>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import VInfiniteScroll from 'v-infinite-scroll'
import { mapState, mapGetters } from 'vuex'
import { isWebTabsEnabled, findRouteForTab, pageTypes } from '@facilio/router'

export default {
  created() {
    this.$store.dispatch('loadAssetCategory')
    this.$store.dispatch('loadAlarmSeverity')
  },
  data() {
    return {
      showDialog: false,
      selectAll: false,
      loading: false,
      subListLoading: false,
      thresholddialog: false,
      selectedRules: null,
      selectedRule: null,
      showQuickSearch: false,
      quickSearchQuery: null,
      toggle: false,
      fetchingMore: false,
      perPage: 30,
    }
  },
  title() {},
  computed: {
    ...mapState({
      currentViewDetails: state => state.view.currentViewDetail,
      rules: state => state.rule.rules,
      count: state => state.rule.count,
      assetCategoryList: state => state.assetCategory,
      views: state => state.view.views,
      searchQuery: state => state.rule.quickSearchQuery,
    }),
    ...mapGetters(['getAlarmSeverity']),
    filters() {
      if (this.$route.query.search) {
        return JSON.parse(this.$route.query.search)
      }
      return null
    },
    canLoadMore() {
      return this.$store.state.rule.canLoadMore
    },
    scrollDisabled() {
      return this.loading || !this.canLoadMore || this.fetchingMore
    },
    openRuleId() {
      if (this.$route.params.id) {
        return parseInt(this.$route.params.id)
      }
      return -1
    },
    currentView() {
      // if (this.$route.query.search && this.openRuleId > -1) {
      //   this.views.push({
      //     displayName: 'Filtered Rules',
      //     name: 'filteredRules',
      //   })
      //   return 'Filtered Rules Types'
      // }
      if (this.$route.params.viewname) {
        return this.$route.params.viewname
      }
      return 'all'
    },
    page() {
      return this.$route.query.page || 1
    },
    includeParentFilter() {
      return (
        this.$route.query.includeParentFilter &&
        (this.$route.query.includeParentFilter === 'true' ||
          this.$route.query.includeParentFilter === true)
      )
    },
    currentViewDetail() {
      if (this.$route.query.search) {
        return {
          displayName: 'Filtered Rules',
          name: 'filteredrules',
        }
      }
      return this.$store.state.view.currentViewDetail
    },
    moduleName() {
      return 'readingrule'
    },
  },
  mounted() {
    this.$store.dispatch('view/loadViews', 'readingrule')
    this.$store.dispatch('view/loadModuleMeta', 'readingrule')
    this.getViewDetail()
    this.loadNewPageData()
  },
  methods: {
    getViewDetail() {
      this.$store.dispatch('view/loadViewDetail', {
        viewName: this.currentView,
        moduleName: 'readingrule',
      })
    },
    switchCategory(index) {
      let indexs
      this.views.filter((d, i) => {
        if (d.name === 'filteredRules') {
          indexs = i
        }
      })
      if (indexs && indexs !== -1) {
        this.views.splice(indexs, 1)
      }
      if (isWebTabsEnabled()) {
        let { moduleName } = this
        let { name } = findRouteForTab(pageTypes.RULES_SUMMARY) || {}
        name &&
          this.$router.push({
            name,
            params: { id: this.$route.params.id, viewname: index.name },
            query: this.$route.query,
          })
      } else {
        this.$router.push({
          name: 'ruleOverview',
          params: { viewname: index.name, id: this.$route.params.id },
        })
      }
      this.toggle = false
    },
    back() {
      if (isWebTabsEnabled()) {
        let { moduleName } = this
        let { name } = findRouteForTab(pageTypes.RULES_LIST) || {}
        name &&
          this.$router.push({
            name,
            params: { viewname: this.$route.params.viewname },
            query: this.$route.query,
          })
      } else {
        let url = '/app/fa/rules/' + this.$route.params.viewname
        this.$router.push({
          path: url,
          query: this.$route.query,
        })
      }
    },
    handleSelectionChange(val) {
      console.log('val' + JSON.stringify(val))
      this.multipleSelection = val
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
      this.$store.dispatch('rule/updateRuleSearchQuery', this.quickSearchQuery)
    },
    editThresholdRule(rule) {
      let self = this
      if (isWebTabsEnabled()) {
        let { name } = findRouteForTab(pageTypes.RULES_EDIT) || {}
        name &&
          this.$router.push({
            name,
            params: { id: rule.id },
            query: this.$route.query,
          })
      } else {
        self.$util.fetchRules('alarm', rule.id).then(function(rules) {
          let url = '/app/fa/rule/edit/' + rule.id
          self.$router.replace({
            path: url,
          })
          self.$router.push()
          self.selectedRules = rules
          self.selectedRule = rule.id
        })
      }
    },
    openSummaryRules(id) {
      if (isWebTabsEnabled()) {
        let { moduleName } = this
        let { name } = findRouteForTab(pageTypes.RULES_SUMMARY) || {}
        name &&
          this.$router.push({
            name,
            params: { id, viewname: this.$route.params.viewname },
            query: this.$route.query,
          })
      } else {
        this.$router.push({
          name: 'ruleOverview',
          params: { viewname: this.$route.params.viewname, id },
          query: this.$route.query,
        })
      }
    },
    getCategoryName(categoryId) {
      if (categoryId > 0 && this.assetCategoryList) {
        let category = this.assetCategoryList.find(
          category => category.id === categoryId
        )
        if (category) {
          return category.name
        }
      }
    },
    deleteRule(rule) {
      this.$dialog
        .confirm({
          title: this.$t('common.wo_report.delete_rule_title'),
          message: this.$t('common.wo_report.delete_this_rule'),
          rbDanger: true,
          rbLabel: 'Delete',
        })
        .then(value => {
          if (value) {
            this.$util.deleteRule('alarm', rule.id).then(response => {
              this.loadNewPageData()
            })
          }
        })
    },
    getResourceName(rule) {
      if (rule.assetCategoryId > 0) {
        let message
        let isIncluded = rule.includedResources && rule.includedResources.length
        let selectedCount
        if (isIncluded) {
          selectedCount = rule.includedResources.length
        } else if (rule.excludedResources && rule.excludedResources.length) {
          selectedCount = rule.excludedResources.length
        }
        let categoryName = this.getCategoryName(rule.assetCategoryId)
        if (selectedCount) {
          message =
            (isIncluded ? selectedCount : 'Some') +
            ' ' +
            categoryName +
            (!isIncluded || selectedCount > 1 ? 's' : '')
        } else {
          message = 'All ' + categoryName + 's'
        }
        return message
      } else if (rule.resourceId > 0) {
        return rule.matchedResources
          ? rule.matchedResources[Object.keys(rule.matchedResources)[0]].name
          : '--- '
      }
      return '---'
    },
    changeRuleStatus(rule) {
      this.$util
        .changeRuleStatus('alarm', rule.id, rule.status)
        .then(response => {
          this.$message.success(
            (rule.status ? 'Enabled' : 'Disabled') + ' rule successfully.'
          )
        })
        .catch(function(error) {
          console.log(error)
        })
    },
    nextPage() {
      console.log('Nectxpad')
      if (!this.scrollDisabled) {
        this.subListLoading = true
        this.fetchingMore = true
        this.loadRuleList(true, this.state)
      }
    },
    loadRuleList(subList) {
      this.setTitle(this.currentViewDetail.displayName + ' - Rules')
      let self = this
      let queryObj = {
        subList: subList,
        viewname: this.currentView,
        page: subList ? this.$store.state.rule.currentPage + 1 : this.page,
        filters: this.filters,
        search: this.searchQuery,
        isNew: true,
        includeParentFilter: this.includeParentFilter,
      }
      self.$store
        .dispatch('rule/fetchRules', queryObj)
        .then(function(response) {
          self.loading = false
          self.subListLoading = false
          self.fetchingMore = false
        })
        .catch(function(error) {
          if (error) {
            self.loading = false
            self.subListLoading = false
            self.fetchingMore = false
          }
        })
    },
    loadRulesCount() {
      let self = this
      let queryObj = {
        viewname: this.currentView,
        filters: this.filters,
        search: this.searchQuery,
        isNew: true,
        isCount: true,
        includeParentFilter: this.includeParentFilter,
      }
      self.$store
        .dispatch('rule/fetchRules', queryObj)
        .then(function(response) {
          self.fetchingMore = false
          self.$emit('syncCount', self.count)
        })
        .catch(function(error) {
          if (error) {
            self.fetchingMore = false
          }
        })
    },
    loadNewPageData() {
      if (this.openRuleId > 0) {
        this.subListLoading = true
      } else {
        this.loading = true
      }
      this.resetPagination()
      this.loadRuleList()
      this.loadRulesCount()
    },
    resetPagination() {
      this.fetchingMore = false
      this.hasMorePages = false
      Object.assign([], this.rules)
    },
    opensummary(id) {
      this.$router.push({
        path: '/app/fa/rules/' + this.$route.params.viewname + '/summary/' + id,
      })
    },
  },
  watch: {
    currentView: function(newVal, oldVal) {
      if (oldVal !== newVal) {
        this.loadNewPageData()
      }
    },
    currentViewDetails: function(newVal, oldVal) {
      if (newVal != oldVal) {
        this.setTitle(
          this.currentViewDetails
            ? this.currentViewDetails.displayName
            : '' + ' - Rules'
        )
      }
    },
    filters: function(newVal) {
      this.loadNewPageData()
    },
    page: function(newVal, oldVal) {
      if (oldVal !== newVal && !this.loading) {
        this.loading = true
        this.loadRuleList()
      }
    },
    searchQuery() {
      this.loadRuleList()
    },
    search: function(newVal) {
      this.loadNewPageData()
    },
  },
  components: {
    VInfiniteScroll,
  },
}
</script>
<style lang="scss">
.rules-list-table {
  .el-table td {
    padding: 17px 20px;
  }
}

.rules-list-page {
  .fc-column-view-right-approval {
    width: 78%;
    flex: 0 0 78% !important;
    max-width: 78% !important;
  }

  .rules-left-side {
    width: 22%;
  }

  .fc__submenu__left {
    width: inherit;
  }

  .menu-item .label {
    font-size: 14px;
    color: #324056;
    letter-spacing: 0.5px;
    font-weight: 400;
  }

  .rule-item {
    border-bottom: 1px solid #f4f6f8;
  }
}
</style>
