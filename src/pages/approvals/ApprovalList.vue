<template>
  <div class="layout new-approval-list">
    <div v-if="isSummaryOpen" class="d-flex height100">
      <!-- HALF VIEW WHEN SUMMARY OPEN-->

      <div
        class="height100vh full-layout-white fc-border-left fc-border-right approval-sidebar-list"
      >
        <summary-side-bar
          :list="list"
          :isLoading.sync="loading"
          :activeRecordId="selectedRecordId"
          :total="listCount"
          :currentCount="(list || []).length"
          :currentPage="page - 1"
          @nextPage="loadApprovals"
        >
          <template #title>
            <el-row class="p15 pT20 pB20 fc-border-bottom">
              <el-col :span="2">
                <span @click="openList()">
                  <inline-svg
                    @click="openList()"
                    src="svgs/arrow"
                    class="rotate-90 pointer"
                    iconClass="icon icon-sm"
                  ></inline-svg>
                </span>
              </el-col>

              <el-col :span="22">
                <div class="bold">{{ getViewName(currentView) }}</div>
              </el-col>
            </el-row>
          </template>

          <template v-slot="{ record }">
            <div class="list-label p20" @click="openSummary(record)">
              <span
                :title="record.subject || record.name"
                v-tippy="{
                  placement: 'top',
                  animation: 'shift-away',
                  arrow: true,
                }"
              >
                {{ record.subject || record.name }}
              </span>
            </div>
          </template>
        </summary-side-bar>
      </div>

      <div class="flex-grow">
        <ApprovalSummary
          :key="selectedRecordId"
          :id="selectedRecordId"
          :moduleName="moduleName"
          :details="list.find(({ id }) => id === selectedRecordId)"
          :updateUrl="moduleUpdateUrl"
          :transformFn="moduleTransformFn"
          @onTransitionSuccesss="handleTransitionSuccess"
        ></ApprovalSummary>
      </div>
    </div>
    <div v-else>
      <!-- LIST VIEW -->
      <ListLayout
        :moduleName="moduleName"
        :showViewRearrange="true"
        :visibleViewCount="2"
        :showViewEdit="false"
        :getPageTitle="getViewName"
        :pathPrefix="pathPrefix"
      >
        <template #views-list>
          <ApprovalViewSidePanel
            v-if="canShowViewsSidePanel"
            :moduleName="moduleName"
            :moduleList="modules"
            :canShowViewsSidePanel.sync="canShowViewsSidePanel"
            :pathPrefix="pathPrefix"
          ></ApprovalViewSidePanel>
        </template>
        <template #views>
          <ApprovalViews
            :moduleName="moduleName"
            :moduleList="modules"
            :maxVisibleMenu="3"
            :canShowViewsSidePanel.sync="canShowViewsSidePanel"
            :pathPrefix="pathPrefix"
          ></ApprovalViews>
        </template>

        <template #header>
          <template v-if="sortConfigList">
            <pagination
              v-if="listcount"
              :total="listcount"
              :perPage="perPage"
              class="pL15 fc-black-small-txt-12"
            ></pagination>

            <!-- portal to insert elements in header from child components-->
            <portal-target name="approval-list-top"></portal-target>

            <span class="separator" v-if="listcount > 0">|</span>
            <sort
              :sortList="sortConfigList"
              :config="sortConfig"
              :excludeFields="excludedSortFields"
              @onchange="updateSort"
            ></sort>
            <span class="separator">|</span>
          </template>
          <AdvancedSearch
            :key="`${moduleName}-search`"
            :moduleName="moduleName"
            :moduleDisplayName="moduleDisplayName"
          >
          </AdvancedSearch>
        </template>

        <div class="width100">
          <FTags :key="`ftags-list-${moduleName}`" :hideSaveView="true"></FTags>
        </div>

        <div :class="['height100 f-list-view height100vh', { m10: loading }]">
          <div v-if="loading" class="full-layout-white height100 text-center">
            <spinner :show="loading" size="80"></spinner>
          </div>

          <component
            v-else
            :is="listComponent"
            :moduleName="moduleName"
            :list="list"
            :setSortConfig="setSortConfig"
            :setTransitionConfig="setTransitionConfig"
            :getApprovalStates="getApprovalStates"
            :refreshAction="reload"
            :openSummary="openSummary"
            :currentView="currentView"
          ></component>
        </div>
      </ListLayout>
    </div>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import { mapState } from 'vuex'
import ListLayout from 'newapp/list/DeprecatedCommonLayout'
import Pagination from '@/list/FPagination'
import AdvancedSearch from 'newapp/components/search/AdvancedSearch'
import FTags from 'newapp/components/search/FTags'
import Sort from 'newapp/components/Sort'
import ApprovalViews from './components/ApprovalHeader'
import ApprovalViewSidePanel from './components/ApprovalViewSidePanel'
import SummarySideBar from 'newapp/components/SummarySideBar'
import ApprovalSummary from './ApprovalSummary'
import { API } from '@facilio/api'
import { isWebTabsEnabled } from '@facilio/router'

export default {
  name: 'ApprovalList',
  props: ['moduleName', 'modules'],
  components: {
    ListLayout,
    ApprovalViews,
    ApprovalViewSidePanel,
    Pagination,
    AdvancedSearch,
    FTags,
    Sort,
    SummarySideBar,
    ApprovalSummary,
  },
  data() {
    return {
      list: [],
      listcount: null,
      stateflows: [],
      page: 1,
      perPage: 50,
      isLoading: true,
      isMetaLoading: false,
      sortConfig: {},
      sortConfigList: null,
      excludedSortFields: null,
      moduleUpdateUrl: null,
      moduleTransformFn: null,
      canShowViewsSidePanel:
        JSON.parse(localStorage.getItem('fc-view-sidepanel')) || false,
    }
  },
  created() {
    if (isEmpty(this.moduleName)) {
      let moduleName = this.$getProperty(this.modules, '0.name') || null
      moduleName && this.$router.replace({ params: { moduleName } })
    }

    this.isMetaLoading = true
    Promise.all([
      this.$store.dispatch('loadTicketStatus', this.moduleName),
      this.$store.dispatch('loadTicketCategory'),
      this.$store.dispatch('loadTicketPriority'),
    ]).finally(() => {
      this.isMetaLoading = false
    })
  },
  computed: {
    ...mapState({
      isViewLoading: state => state.view.isLoading,
      moduleMeta: state => state.view.metaInfo,
    }),
    moduleDisplayName() {
      let { moduleMeta } = this
      let { displayName = '' } = moduleMeta || {}
      return displayName
    },
    listComponent() {
      let componentHash = {
        workorder: () => import('./components/WorkRequestList'),
        workpermit: () => import('./components/WorkPermitList'),
        inventoryrequest: () => import('./components/InventoryList'),
        defaultModule: () => import('./components/DefaultModuleList'),
      }
      return componentHash[this.moduleName] || componentHash['defaultModule']
    },
    selectedRecordId() {
      if (this.$route.params.id) {
        return parseInt(this.$route.params.id)
      }
      return -1
    },
    isSummaryOpen() {
      return !isEmpty(this.selectedRecordId)
    },
    currentView() {
      return this.$route.params.viewname
    },
    loading: {
      get() {
        return this.isLoading || this.isMetaLoading || this.isViewLoading
      },
      set(value) {
        this.isLoading = value
      },
    },
    filters() {
      let { $route } = this
      let {
        query: { search },
      } = $route || {}

      if (!isEmpty(search)) {
        return JSON.parse(this.$route.query.search)
      }
      return null
    },
    includeParentFilter() {
      let {
        $route: { query },
      } = this
      let { includeParentFilter } = query

      return (
        includeParentFilter &&
        (includeParentFilter === 'true' || includeParentFilter === true)
      )
    },
    pathPrefix() {
      return '/app/wo/newapprovals/'
    },
  },
  watch: {
    moduleName: {
      handler(newVal, oldVal) {
        if (!isEmpty(newVal) && newVal !== oldVal) {
          this.$store.dispatch('view/loadModuleMeta', newVal).catch(() => {})
          this.reload()
        }
      },
      immediate: true,
    },
    filters() {
      this.reload()
    },
    isSummaryOpen(isOpen) {
      if (!isOpen) {
        this.reload()
      }
    },
    canShowViewsSidePanel(value) {
      localStorage.setItem('fc-view-sidepanel', value)
    },
  },
  methods: {
    getViewName() {
      return `Pending Approvals`
    },
    openSummary({ id }) {
      let { moduleName } = this

      this.$router.push({
        params: { id, moduleName },
        query: {
          ...this.$route.query,
        },
      })
    },
    openList() {
      let { moduleName, currentView: viewname } = this

      if (isWebTabsEnabled()) {
        this.$router.push({
          params: { moduleName, viewname, id: null },
          query: {
            ...this.$route.query,
          },
        })
      } else {
        this.$router.push({
          name: 'ApprovalList',
          params: {
            moduleName,
            viewname,
          },
          query: {
            ...this.$route.query,
          },
        })
      }
    },
    reload(force = false) {
      this.page = 1
      this.loadApprovals({ force })
      this.loadApprovalCounts({ force })
    },
    handleTransitionSuccess(currentRecord) {
      let { list } = this
      let record, index
      let skipLoading = false

      if (this.isSummaryOpen) {
        if (list.length === 1) {
          index = 0
          this.openList()
        } else {
          // Find the next record and open it's summary
          index = list.findIndex(({ id }) => currentRecord.id === id)

          record = index < list.length - 1 ? list[index + 1] : list[0]
          if (!isEmpty(record)) {
            this.openSummary(record)
          }
          skipLoading = true
        }
      }
      this.loadApprovals({ skipLoading })
      this.loadApprovalCounts()
    },
    loadApprovals({ skipLoading = false, force = false } = {}) {
      this.page = skipLoading ? 1 : this.page

      let {
        moduleName,
        page,
        perPage,
        includeParentFilter,
        filters,
        currentView,
      } = this
      let { criteriaIds } = this.$route.query

      if (isEmpty(moduleName) || isEmpty(currentView)) return

      let params = {
        moduleName,
        viewName: currentView,
        page,
        perPage,
        filters: !isEmpty(filters) ? JSON.stringify(filters) : null,
        criteriaIds: !isEmpty(criteriaIds) ? criteriaIds : null,
        includeParentFilter,
      }

      if (!skipLoading) this.isLoading = true

      return API.get(`/v2/approval/moduleList`, params, { force }).then(
        ({ data, error }) => {
          if (error) {
            this.$message.error(
              error.message || 'Could not load approval requests'
            )
          } else {
            this.list = data.records
            this.stateflows = data.stateFlows
            this.page += 1 // move this to a separate function and then remove this from reload()
          }
          if (!this.isSummaryOpen) this.isLoading = false
        }
      )
    },
    loadApprovalCounts({ force = false } = {}) {
      let { moduleName, includeParentFilter, filters, currentView } = this
      let { criteriaIds } = this.$route.query
      let params = {
        moduleName,
        viewName: currentView,
        filters: !isEmpty(filters) ? JSON.stringify(filters) : null,
        criteriaIds: !isEmpty(criteriaIds) ? criteriaIds : null,
        includeParentFilter,
        fetchCount: true,
      }

      if (isEmpty(moduleName) || isEmpty(currentView)) return

      API.get(`/v2/approval/moduleList`, params, { force }).then(
        ({ data, error }) => {
          if (error) {
            this.$message.error(error.message || 'Error Occured')
            this.listcount = null
          } else {
            this.listcount = data.recordCount
          }
        }
      )
    },
    getApprovalStates(record) {
      let { approvalStatus, approvalFlowId } = record || {}
      let { id: approvalStatusId } = approvalStatus || {}
      if (isEmpty(approvalStatus) || isEmpty(approvalFlowId)) {
        return []
      }
      let key = `${approvalFlowId}_${approvalStatusId}`
      let transitions = this.stateflows[key]
      let { evaluatedTransitionIds = [] } = record

      if (transitions) {
        transitions = transitions.filter(({ id }) =>
          (evaluatedTransitionIds || []).includes(id)
        )
        return transitions.slice(0, 2)
      }
      return []
    },
    setSortConfig(sortConfig, sortConfigList, excludedSortFields) {
      this.sortConfig = sortConfig
      this.sortConfigList = sortConfigList
      this.excludedSortFields = excludedSortFields
    },
    setTransitionConfig(updateUrl, transformFn) {
      this.moduleUpdateUrl = updateUrl || null
      this.moduleTransformFn = transformFn || null
    },
    updateSort(sorting) {
      this.$store
        .dispatch('view/savesorting', {
          viewName: this.currentView,
          orderBy: sorting.orderBy,
          orderType: sorting.orderType,
          moduleName: this.moduleName,
        })
        .then(() => this.reload({ force: true }))
    },
  },
}
</script>
<style lang="scss">
.new-approval-list {
  .page-sort-popover {
    right: 10px;
  }
  .list-label {
    font-size: 14px;
    color: #324056;
    letter-spacing: 0.5px;
    font-weight: 400;
  }
  .approval-sidebar-list {
    flex: 0 0 400px;
    max-width: 300px;
  }
  .approval-list-action-container {
    min-height: 25px;
  }
  .approval-list-actions {
    display: none;
  }
  .el-table__body tr.hover-row > td .approval-list-actions {
    display: flex;
  }
  .el-table thead tr > th:last-of-type {
    opacity: 0;
  }
  .action-btn-slide {
    float: none;
    display: inline-block;
    margin-left: 20px;
  }
  .bulk-approval-button {
    &.el-button {
      cursor: pointer;
      text-transform: uppercase;
      text-align: center;
      display: inline-block;
      letter-spacing: 0.7px;
      border-radius: 3px;
    }

    &.el-button--primary {
      box-shadow: 0 2px 4px 0 rgba(230, 230, 230, 0.5) !important;
      border: solid 1px #39b2c2;
      background-color: #39b2c2;
      color: #fff;
      &:hover {
        background: #3cbfd0 !important;
        color: #fff;
      }
    }

    &.el-button:not(.el-button--primary) {
      border: solid 1px #dc7171;
      background-color: #dc7171;
      color: #ffffff;
      &:hover {
        color: #fff;
        background-color: #ef6d6d !important;
        border-color: #ef6d6d !important;
      }
    }
  }
}
</style>
