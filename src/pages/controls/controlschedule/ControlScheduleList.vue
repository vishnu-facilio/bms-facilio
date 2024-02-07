<template>
  <div v-if="isSummaryOpen">
    <div class="height100 schedule-list-container">
      <div v-if="showLoading" class="flex-middle fc-empty-white">
        <spinner :show="showLoading" size="80"></spinner>
      </div>
      <div v-else class="flex flex-row">
        <div
          class="height100vh full-layout-white fc-border-left fc-border-right approval-sidebar-list width20"
        >
          <SummarySideBar
            :list="moduleRecordList"
            :isLoading.sync="isLoading"
            :activeRecordId="selectedRecordId"
            :total="listCount"
            :currentCount="(moduleRecordList || []).length"
            class="schedule-summary-list"
          >
            <template #title>
              <div
                class="p15 pT10 pB10 align-center fc-border-bottom flex flex-row height50"
              >
                <div class="col-1 text-left pR5">
                  <i
                    class="el-icon-back fw6 pointer"
                    @click="back"
                    style="vertical-align: sub;"
                  ></i>
                </div>
                <span class="pointer f14 font-semibold">
                  {{ views[0].displayName }}
                </span>
              </div>
            </template>
            <template v-slot="{ record }">
              <div class="list-label p20" @click="openSummary(record)">
                <div class="fc-id">#{{ record.id }}</div>
                <div
                  :title="record.subject || record.name"
                  v-tippy="{
                    placement: 'top',
                    animation: 'shift-away',
                    arrow: true,
                  }"
                >
                  {{ record.subject || record.name }}
                </div>
              </div>
            </template>
          </SummarySideBar>
        </div>
        <router-view
          :moduleName="moduleName"
          :id="selectedRecordId"
        ></router-view>
      </div>
    </div>
  </div>
  <!-- eslint-disable-next-line vue/valid-template-root -->
  <CommonListLayout
    v-else
    :moduleName="moduleName"
    :showViewRearrange="true"
    :showViewEdit="true"
    :visibleViewCount="3"
    :getPageTitle="() => moduleDisplayName"
    :hideSubHeader="isEmpty(moduleRecordList)"
    :recordCount="list"
    :recordLoading="showLoading"
    pathPrefix="/app/co/schedule"
    :key="moduleName + '-list-layout'"
  >
    <template #header>
      <template>
        <AdvancedSearchWrapper
          :key="`${moduleName}-search`"
          :moduleName="moduleName"
          :moduleDisplayName="moduleDisplayName"
        ></AdvancedSearchWrapper>
        <button class="fc-create-btn" @click="redirectToForm()">
          {{ $t('common._common._new') }} {{ moduleDisplayName }}
        </button>
      </template>
    </template>

    <template #sub-header-actions>
      <template v-if="!isEmpty(moduleRecordList)">
        <pagination
          :total="listCount"
          :perPage="50"
          :skipTotalCount="true"
          class="mT2"
        ></pagination>
        <span class="separator pL0">|</span>
        <el-tooltip
          effect="dark"
          :content="$t('common._common.sort')"
          placement="right"
        >
          <sort
            :key="moduleName + '-sort'"
            :config="sortConfig"
            :sortList="sortConfigLists"
            @onchange="updateSort"
          ></sort>
        </el-tooltip>
      </template>
    </template>
    <template #content>
      <div class="fc-card-popup-list-view">
        <div
          class="fc-list-view p10 pT0 fc-list-table-container height100vh fc-table-td-height fc-table-viewchooser pB100"
          :class="[$route.query.search && 'fc-list-table-search-scroll']"
        >
          <spinner
            v-if="showLoading"
            class="mT40"
            :show="showLoading"
          ></spinner>
          <div
            v-else-if="$validation.isEmpty(moduleRecordList)"
            class="fc-list-empty-state-container"
          >
            <inline-svg
              src="svgs/emptystate/quotation"
              iconClass="icon text-center icon-xxxxlg"
            ></inline-svg>
            <div class="q-item-label nowo-label">
              {{ $t('controls.controlSchedule.no_data') }}
            </div>
          </div>
          <div v-else>
            <el-table
              :data="moduleRecordList"
              ref="tableList"
              class="fc-list-eltable width100"
              :fit="true"
              height="100%"
            >
              <el-table-column
                min-width="120"
                :label="$t('common._common.id')"
                fixed
                class="pR0 pL0"
              >
                <template v-slot="group">
                  <div class="fc-id" @click="openSummary(group.row)">
                    {{ '#' + group.row.id }}
                  </div>
                </template>
              </el-table-column>
              <el-table-column
                min-width="220"
                :label="$t('common.products.name')"
                fixed
                class="pR0 pL0"
              >
                <template v-slot="group">
                  <div @click="openSummary(group.row)">
                    {{ group.row.name }}
                  </div>
                </template>
              </el-table-column>
              <el-table-column
                min-width="220"
                :label="$t('common._common.created_time')"
                fixed
                class="pR0 pL0"
              >
                <template v-slot="group">
                  <div v-if="group.row.sysCreatedTime > 0">
                    {{ group.row.sysCreatedTime | formatDate }}
                  </div>
                  <div v-else>---</div>
                </template>
              </el-table-column>
              <el-table-column
                min-width="220"
                :label="$t('common._common.created_by')"
                fixed
                class="pR0 pL0"
              >
                <template v-slot="group">
                  <div v-if="!$validation.isEmpty(group.row.sysCreatedBy)">
                    {{ group.row.sysCreatedBy.name }}
                  </div>
                  <div v-else>---</div>
                </template>
              </el-table-column>
              <template v-for="(field, index) in viewColumns">
                <el-table-column
                  v-if="!isFixedColumn(field.name) || field.parentField"
                  :key="index"
                  :prop="field.name"
                  :label="field.displayName"
                  min-width="220"
                >
                  <template v-slot="group">
                    <div class="table-subheading">
                      {{ getColumnDisplayValue(field, group.row) || '---' }}
                    </div>
                  </template>
                </el-table-column>
              </template>
              <el-table-column
                prop
                label
                width="180"
                class="visibility-visible-actions"
                fixed="right"
              >
                <template v-slot="data">
                  <div class="text-center">
                    <i
                      class="el-icon-edit pointer edit-icon-color visibility-hide-actions mL10"
                      data-arrow="true"
                      :title="$t('common._common.edit')"
                      v-tippy
                      @click="editRecord(data.row.id)"
                    ></i>
                    <i
                      class="el-icon-delete pointer edit-icon-color visibility-hide-actions mL10"
                      data-arrow="true"
                      :title="$t('common._common.delete')"
                      v-tippy
                      @click="deleteRecord(data.row.id)"
                    ></i>
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </div>
    </template>
    <portal to="view-manager-link">
      <div @click="getViewManagerRoute" class="view-manager-btn">
        <inline-svg
          src="svgs/hamburger-menu"
          class="d-flex"
          iconClass="icon icon-sm"
        ></inline-svg>
        <span class="label mL10 text-uppercase">
          {{ $t('viewsmanager.list.views_manager') }}
        </span>
      </div>
    </portal>
  </CommonListLayout>
</template>

<script>
import CommonListLayout from 'newapp/list/CommonLayout'
import ViewMixinHelper from '@/mixins/ViewMixin'
import { API } from '@facilio/api'
import { mapState, mapGetters } from 'vuex'
import SummarySideBar from 'newapp/components/SummarySideBar'
import { isEmpty } from '@facilio/utils/validation'
import Pagination from 'src/newapp/components/ListPagination'
import Sort from 'newapp/components/Sort'
import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
  getApp,
} from '@facilio/router'
import { findRouterForModuleInApp } from 'src/newapp/viewmanager/routeUtil.js'
import AdvancedSearchWrapper from 'newapp/components/search/AdvancedSearchWrapper.vue'

export default {
  props: ['viewname', 'id'],
  components: {
    CommonListLayout,
    SummarySideBar,
    Pagination,
    Sort,
    AdvancedSearchWrapper,
  },
  mixins: [ViewMixinHelper],
  data() {
    return {
      isEmpty,
      listCount: 0,
      sortConfig: {
        orderBy: 'id',
        orderType: 'desc',
      },
      sortConfigLists: ['subject'],
      moduleRecordList: null,
      isLoading: false,
    }
  },
  computed: {
    ...mapState({
      viewDetail: state => state.view.currentViewDetail,
      viewLoading: state => state.view.isLoading,
      groupViews: state => state.view.groupViews,
    }),
    ...mapGetters(['getApprovalStatus', 'isStatusLocked', 'getTicketStatus']),
    moduleName() {
      return 'controlSchedule'
    },
    moduleDisplayName() {
      return 'Control Schedule'
    },
    selectedRecordId() {
      return this.id
    },
    isSummaryOpen() {
      return !isEmpty(this.selectedRecordId)
    },
    views() {
      let viewsList = []
      if (!isEmpty(this.groupViews)) {
        this.groupViews.forEach(group => {
          let { views } = group
          views.forEach(view => {
            viewsList.push(view)
          })
        })
      }
      return viewsList
    },

    page() {
      return this.$route.query.page || 1
    },
    currentView() {
      let { viewname } = this
      return viewname
    },
    showLoading() {
      return this.viewLoading || this.isLoading
    },

    appliedFilters() {
      let {
        $route: { query },
      } = this
      let { search } = query || {}
      return search ? JSON.parse(search) : null
    },
  },
  watch: {
    page(newVal, oldVal) {
      if (oldVal !== newVal && !this.loading) {
        this.loadRecords()
      }
    },
    currentView: {
      handler(newVal, oldVal) {
        if (newVal && oldVal !== newVal) {
          this.loadRecords()
        }
      },
      immediate: true,
    },
    appliedFilters() {
      this.loadRecords()
    },
  },
  methods: {
    async loadRecords() {
      this.isLoading = true
      let params = {
        withCount: true,
        viewName: this.currentView,
        page: this.page,
        perPage: 50,
      }

      if (!isEmpty(this.appliedFilters)) {
        params['filters'] = JSON.stringify(this.appliedFilters)
      }

      let { list, error, meta } = await API.fetchAll(this.moduleName, params)

      if (error) {
        let {
          message = this.$t(
            'common._common.error_occured_while_fetching_control_schedule_list'
          ),
        } = error
        this.$message.error(message)
      } else {
        this.moduleRecordList = list || []
        let {
          pagination: { totalCount: count },
        } = meta
        this.listCount = count
      }
      this.isLoading = false
    },
    updateSort(sorting) {
      let { moduleName, currentView } = this
      let sortObj = {
        moduleName,
        viewName: currentView,
        orderBy: sorting.orderBy,
        orderType: sorting.orderType,
        skipDispatch: true,
      }
      this.$store
        .dispatch('view/savesorting', sortObj)
        .then(() => this.loadRecords())
    },
    loadViewMetaInfo() {
      let { moduleName } = this
      return this.$store
        .dispatch('view/loadModuleMeta', moduleName)
        .then(({ fields }) => {
          if (!isEmpty(fields)) {
            this.constructSortConfig(fields)
          }
        })
    },
    constructSortConfig(fields) {
      let configArr = this.sortConfigLists || []
      fields.forEach(field => {
        if (!field.default) {
          configArr.push(field.name)
        }
      })
      this.$set(this, 'sortConfigLists', configArr)
    },
    redirectToForm() {
      let { moduleName } = this
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.CREATE) || {}
        let route = {
          name,
        }
        this.$router.push(route)
      } else {
        this.$router.push({
          name: 'schedule-create',
        })
      }
    },
    openSummary(record) {
      let { moduleName, currentView } = this
      let { id } = record || {}
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}
        let route = {
          name,
          params: {
            viewname: currentView,
            id,
          },
        }
        this.$router.push(route)
      } else {
        this.$router
          .push({
            name: 'schedule-summary',
            params: {
              id,
              moduleName,
              viewname: currentView,
            },
            query: {
              ...this.$route.query,
            },
          })
          .catch(() => {})
      }
    },
    back() {
      let { moduleName, currentView } = this
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.LIST) || {}
        let route = {
          name,
          params: {
            viewname: currentView,
          },
        }
        this.$router.push(route)
      } else {
        this.$router.push({ path: '/app/co/schedule/all' })
      }
    },
    editRecord(id) {
      if (isWebTabsEnabled()) {
        let { moduleName } = this
        let { name } = findRouteForModule(moduleName, pageTypes.EDIT) || {}
        let route = {
          name,
          params: { id },
        }
        this.$router.push(route)
      } else {
        this.$router
          .push({
            name: 'schedule-edit',
            params: { id, moduleName: this.moduleName },
            query: {
              ...this.$route.query,
            },
          })
          .catch(() => {})
      }
    },
    async deleteRecord(id) {
      let value = await this.$dialog.confirm({
        title: this.$t(`controls.controlSchedule.delete_schedule`),
        message: this.$t(
          `controls.controlSchedule.delete_schedule_confirmation`
        ),
        rbDanger: true,
        rbLabel: this.$t('common._common.delete'),
      })
      if (value) {
        let { error } = await API.deleteRecord(this.moduleName, [id])
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.loadRecords()
        }
      }
    },
    getViewManagerRoute() {
      let appId = (getApp() || {}).id
      let { moduleName, $route } = this
      let { query } = $route

      let route = findRouterForModuleInApp(moduleName, pageTypes.VIEW_MANAGER)
      if (route)
        this.$router.push({
          ...route,
          query: { ...query, appId },
        })
    },
  },
}
</script>

<style lang="scss" scoped>
.create-btn {
  margin-top: -10px;
}
</style>
<style lang="scss">
.schedule-summary-list {
  .list-item.active {
    background-color: #f2fafb;
    border: solid 1px #d5ebed;
  }
}
</style>
