<template>
  <CommonListLayout
    :moduleName="moduleName"
    :showViewRearrange="true"
    :showViewEdit="true"
    :visibleViewCount="3"
    :getPageTitle="() => moduleDisplayName"
    :hideSubHeader="isEmpty(moduleRecordList)"
    :recordCount="listCount"
    :recordLoading="showLoading"
    pathPrefix="/app/cy/audience/"
  >
    <template #header>
      <AdvancedSearchWrapper
        :key="`${moduleName}-search`"
        :moduleName="moduleName"
        :moduleDisplayName="moduleDisplayName"
      ></AdvancedSearchWrapper>

      <template v-if="!showSearch">
        <button
          v-if="$hasPermission(`${moduleName}:CREATE`)"
          class="fc-create-btn "
          @click="addRecord()"
        >
          {{ $t('common.products.new_audience') }}
        </button>
      </template>
    </template>

    <template #sub-header-actions>
      <template v-if="!isEmpty(moduleRecordList)">
        <pagination
          :total="listCount"
          :perPage="50"
          :skipTotalCount="true"
          class="pL15 fc-black-small-txt-12"
        ></pagination>
        <span class="separator">|</span>

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
          :class="[appliedFilters && 'fc-list-table-search-scroll']"
        >
          <Spinner
            v-if="showLoading"
            class="mT40"
            :show="showLoading"
          ></Spinner>
          <div
            v-else-if="$validation.isEmpty(moduleRecordList)"
            class="fc-list-empty-state-container"
          >
            <inline-svg
              src="svgs/community-empty-state/audience"
              iconClass="icon text-center icon-xxxxlg"
            ></inline-svg>
            <div class="q-item-label nowo-label">
              {{ $t('tenant.audience.no_data') }}
            </div>
          </div>
          <template v-else>
            <div class="view-column-chooser" @click="showColumnSettings = true">
              <img
                src="~assets/column-setting.svg"
                style="text-align: center; position: absolute; top: 35%;right: 29%;"
              />
            </div>
            <div
              v-if="!$validation.isEmpty(selectedRecordsObj)"
              class="pull-left table-header-actions"
            >
              <div class="action-btn-slide btn-block">
                <button
                  class="btn btn--tertiary"
                  @click="deleteRecords(selectedRecords)"
                  :class="{ disabled: loading }"
                >
                  <i class="fa fa-trash-o b-icon" v-if="!loading"></i>
                  <i
                    class="fa fa-circle-o-notch b-icon fa-spin"
                    aria-hidden="true"
                    v-if="loading"
                  ></i>
                  {{ $t('common._common.delete') }}
                </button>
              </div>
            </div>
            <CommonList
              :viewDetail="viewDetail"
              :records="moduleRecordList"
              :moduleName="moduleName"
              :redirectToOverview="openRecordSummary"
              @selection-change="selectRecords"
              :slotList="slotList"
            >
              <template #[slotList[0].criteria]="{record}">
                <div
                  class="d-flex main-field-column"
                  @click="openRecordSummary(record.id)"
                >
                  <el-tooltip
                    effect="dark"
                    :content="record.name || '---'"
                    placement="top"
                    :open-delay="600"
                  >
                    <div class="self-center mL5">
                      {{ record.name || '---' }}
                    </div>
                  </el-tooltip>
                </div>
              </template>

              <template #[slotList[1].name]="{record}">
                <div class="text-center">
                  <i
                    v-if="canShowActions(record, 'UPDATE')"
                    class="el-icon-edit pointer edit-icon-color visibility-hide-actions mL10"
                    data-arrow="true"
                    :title="$t('common._common.edit')"
                    @click="editRecord(record)"
                    v-tippy
                  ></i>
                  <i
                    v-if="canShowActions(record, 'DELETE')"
                    class="el-icon-delete pointer edit-icon-color visibility-hide-actions mL10"
                    data-arrow="true"
                    :title="$t('common._common.delete')"
                    @click="deleteRecords([record.id])"
                    v-tippy
                  ></i>
                </div>
              </template>
              <template #[slotList[2].name]="{record}">
                <div class="fc-id">
                  {{ '#' + record.id }}
                </div>
              </template>
            </CommonList>
          </template>
        </div>
        <column-customization
          :visible.sync="showColumnSettings"
          :moduleName="moduleName"
          :viewName="currentView"
        ></column-customization>
      </div>
      <portal to="view-manager-link">
        <router-link
          tag="div"
          :to="`/app/cy/audience/viewmanager`"
          class="view-manager-btn"
        >
          <inline-svg
            src="svgs/hamburger-menu"
            class="d-flex"
            iconClass="icon icon-sm"
          ></inline-svg>
          <span class="label mL10 text-uppercase">
            {{ $t('viewsmanager.list.views_manager') }}
          </span>
        </router-link>
      </portal>
      <router-view @refreshList="loadRecords(true)" />
    </template>
  </CommonListLayout>
</template>
<script>
import { API } from '@facilio/api'
import { mapState, mapGetters } from 'vuex'
import { isEmpty, isArray } from '@facilio/utils/validation'
import cloneDeep from 'lodash/cloneDeep'
import ColumnCustomization from '@/ColumnCustomization'
import Spinner from '@/Spinner'
import CommonListLayout from 'newapp/list/CommonLayout'
import Pagination from 'src/newapp/components/ListPagination'
import Sort from 'newapp/components/Sort'
import CommonList from 'newapp/list/CommonList'
import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'
import { getColumnConfig } from 'src/newapp/components/column-customization/columnConfigUtil.js'
import AdvancedSearchWrapper from 'newapp/components/search/AdvancedSearchWrapper.vue'

const FILTER_CONFIG_OBJ = {
  includeParentCriteria: true,
  data: {
    title: {
      label: 'Name',
      displayType: 'string',
      value: '',
    },
  },
  availableColumns: [],
  fixedCols: ['name'],
  saveView: true,
}

export default {
  props: ['viewname', 'moduleName', 'attachmentsModuleName'],
  components: {
    ColumnCustomization,
    Spinner,
    CommonListLayout,
    Pagination,
    AdvancedSearchWrapper,
    Sort,
    CommonList,
  },
  data() {
    return {
      sortConfig: {
        orderBy: 'id',
        orderType: 'desc',
      },
      sortConfigLists: ['name'],
      filterConfig: null,
      defaultFilter: 'name',
      loading: true,
      selectedRecords: [],
      selectedRecordsObj: [],
      showColumnSettings: false,
      tableLoading: false,
      moduleRecordList: [],
      listCount: null,
      isEmpty,
    }
  },
  created() {
    this.$store.dispatch('loadTicketStatus', this.moduleName)
    this.$store.dispatch('view/loadModuleMeta', this.moduleName)
    this.initFilterConfig()
  },
  computed: {
    slotList() {
      return [
        {
          criteria: JSON.stringify({ name: 'name' }),
        },
        {
          name: 'editDelete',
          isActionColumn: true,
          columnAttrs: {
            width: 130,
            class: 'visibility-visible-actions',
            fixed: 'right',
          },
        },
        {
          name: 'id',
          isHardcodedColumn: true,
          columnAttrs: {
            'min-width': 90,
            label: 'ID',
            fixed: 'left',
          },
        },
      ]
    },
    ...mapState({
      viewDetail: state => state.view.currentViewDetail,
      viewLoading: state => state.view.isLoading,
      metaInfo: state => state.view.metaInfo,
      showSearch: state => state.search.active,
    }),

    ...mapGetters(['getApprovalStatus', 'isStatusLocked']),

    moduleDisplayName() {
      let { displayName = null } = this.metaInfo || {}
      return displayName || 'Audiences'
    },
    appliedFilters() {
      let {
        $route: { query },
      } = this
      let { search } = query || {}
      return search ? JSON.parse(search) : null
    },
    currentView() {
      return this.viewname
    },
    page() {
      return this.$route.query.page || 1
    },
    showLoading() {
      return this.loading || this.viewLoading || this.tableLoading
    },
    currentViewFields() {
      return this.$store.state.view.currentViewDetail['fields']
    },
    filteredViewColumns() {
      let { viewColumns } = this
      let { fixedColumns, fixedSelectableColumns } = getColumnConfig(
        this.moduleName
      )
      let finalFixedColumns = fixedColumns.concat(fixedSelectableColumns)

      if (!isEmpty(viewColumns)) {
        return viewColumns.filter(column => {
          return !finalFixedColumns.includes(column.name)
        })
      }

      return []
    },
    nameColumn() {
      let { viewColumns } = this
      if (!isEmpty(viewColumns)) {
        return viewColumns.find(column => column.name === 'name')
      }
      return {}
    },
  },
  watch: {
    currentView: {
      handler(newVal, oldVal) {
        if (newVal && oldVal !== newVal) {
          this.loadRecords()
        }
      },
      immediate: true,
    },
    currentViewFields() {
      this.tableLoading = true
      this.$nextTick(() => {
        this.$refs.tableList ? this.$refs.tableList.doLayout() : null
        this.tableLoading = false
      })
    },
    appliedFilters() {
      this.loadRecords()
    },
    page(newVal, oldVal) {
      if (oldVal !== newVal && !this.loading) {
        this.loadRecords()
      }
    },
    viewDetail(newVal, oldVal) {
      if (newVal && oldVal !== newVal) {
        this.updateSortConfig()
      }
    },
    metaInfo: {
      handler(value) {
        let { fields } = value || {}
        if (!isEmpty(fields)) this.constructSortConfig(fields)
      },
      immediate: true,
    },
  },
  methods: {
    async loadRecords(force = null) {
      let config = force ? { force } : {}

      let params = {
        viewName: this.currentView,
        page: this.page,
        perPage: 50,
        withCount: true,
        includeParentFilter: this.includeParentFilter,
      }
      if (!isEmpty(this.appliedFilters)) {
        params['filters'] = JSON.stringify(this.appliedFilters)
      }

      this.loading = true

      let { list, meta, error } = await API.fetchAll(
        this.moduleName,
        params,
        config
      )

      if (error) {
        let { message } = error
        this.$message.error(message || 'Error Occured while fetching audiences')
      } else {
        this.listCount = this.$getProperty(meta, 'pagination.totalCount', null)
        this.moduleRecordList = list || []
      }
      this.loading = false
    },
    async deleteRecords(idList) {
      let value = await this.$dialog.confirm({
        title: this.$t(`tenant.audience.delete`),
        message: this.$t(`tenant.audience.delete_confirmation`),
        rbDanger: true,
        rbLabel: this.$t('common._common.delete'),
      })

      if (value) {
        let { moduleName } = this
        let { error } = await API.deleteRecord(moduleName, idList)
        if (error) {
          let { message } = error
          this.$message.error(
            message || 'Error Occured while deleting audiences'
          )
          this.loading = false
        } else {
          this.$message.success(this.$t(`tenant.audience.delete_success`))
          this.loadRecords(true)
          this.selectedRecords = []
          this.selectedRecordsObj = []
        }
      }
    },
    openRecordSummary(id) {
      let { viewname, moduleName } = this

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}

        if (name) {
          this.$router.push({ name, params: { viewname, id } })
        }
      } else {
        this.$router.push({
          name: 'audienceSummary',
          params: { viewname, id },
        })
      }
    },
    addRecord() {
      let { moduleName } = this

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.CREATE) || {}

        if (name) {
          this.$router.push({ name })
        }
      } else {
        this.$router.push({ name: 'new-audience' })
      }
    },
    editRecord({ id = null } = {}) {
      if (!id) return
      else {
        if (isWebTabsEnabled()) {
          let { name } =
            findRouteForModule(this.moduleName, pageTypes.CREATE) || {}

          if (name) {
            this.$router.push({ name, params: { id } })
          }
        } else {
          this.$router.push({
            name: 'edit-audience',
            params: { id },
          })
        }
      }
    },
    selectRecords(selectedRecordsObj) {
      this.selectedRecordsObj = selectedRecordsObj
      this.selectedRecords = selectedRecordsObj.map(value => value.id)
    },
    canShowActions(record, action) {
      if (this.$hasPermission(`${this.moduleName}:${action}`)) {
        return this.hasModuleState(record)
          ? !this.isRecordLocked(record) && !this.isRequestedState(record)
          : true
      }
    },
    hasModuleState(record) {
      let hasState = this.$getProperty(record, 'moduleState.id')
      return hasState ? true : false
    },
    isRecordLocked(record) {
      let { moduleName } = this
      if (!this.hasModuleState(record)) {
        return false
      } else {
        let moduleState = this.$getProperty(record, 'moduleState.id')
        return moduleState && this.isStatusLocked(moduleState, moduleName)
      }
    },
    isRequestedState(record) {
      let { approvalStatus } = record || {}
      if (isEmpty(approvalStatus)) {
        return false
      } else {
        let statusObj = this.getApprovalStatus(approvalStatus.id)
        return this.$getProperty(statusObj, 'requestedState', false)
      }
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
    initFilterConfig() {
      let { moduleName } = this
      let configObj = {
        ...cloneDeep(FILTER_CONFIG_OBJ),
        moduleName,
        path: `/app/cy/audience/`,
      }
      this.$set(this, 'filterConfig', configObj)
    },
    constructSortConfig(fields) {
      let configArr = this.sortConfigLists || []
      fields.forEach(field => {
        configArr.push(field.name)
      })
      this.$set(this, 'sortConfigLists', configArr)
    },
    updateSortConfig() {
      let { viewDetail } = this
      if (!isEmpty(viewDetail.sortFields) && isArray(viewDetail.sortFields)) {
        this.sortConfig = {
          orderType: this.$getProperty(
            viewDetail,
            'sortFields.0.isAscending',
            false
          )
            ? 'asc'
            : 'desc',
          orderBy: this.$getProperty(
            viewDetail,
            'sortFields.0.sortField.name',
            'id'
          ),
        }
      }
    },
  },
}
</script>
<style scoped>
.empty-state-container {
  display: flex;
  flex-direction: column;
  text-align: center;
  margin-bottom: 200px;
}
</style>
