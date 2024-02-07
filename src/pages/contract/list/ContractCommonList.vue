<template>
  <CommonListLayout
    :moduleName="moduleName"
    :showViewRearrange="true"
    :showViewEdit="true"
    :visibleViewCount="3"
    :getPageTitle="() => moduleDisplayName"
    :pathPrefix="parentPath"
  >
    <template #header>
      <template v-if="!showSearch">
        <pagination
          :total="listCount"
          :perPage="50"
          class="pL15 fc-black-small-txt-12"
        ></pagination>
        <span class="separator" v-if="listCount > 0">|</span>

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
        <span class="separator">|</span>

        <f-export-settings
          :module="moduleName"
          :viewDetail="viewDetail"
          :showViewScheduler="false"
          :showMail="false"
          :filters="appliedFilters"
        ></f-export-settings>
        <span class="separator">|</span>
      </template>

      <el-tooltip
        effect="dark"
        :content="$t('common._common.search')"
        placement="left"
      >
        <search
          :key="moduleName + '-search'"
          :config="filterConfig"
          :moduleName="moduleName"
          :defaultFilter="defaultFilter"
          class="fc-black-small-txt-12"
        ></search>
      </el-tooltip>

      <template v-if="!showSearch">
        <span class="separator pL10">|</span>
        <div>
          <button
            v-if="$hasPermission('contract:CREATE')"
            class="fc-create-btn create-btn mTn10"
            @click="redirectToFormCreation()"
          >
            {{ $t('common._common._new') }}
            {{ moduleDisplayName ? moduleDisplayName : moduleName }}
          </button>
        </div>
      </template>
    </template>
    <template #content>
      <div>
        <tags></tags>
      </div>
      <template>
        <div class="height100">
          <div
            class="fc-list-view p10 pT0 mT10 fc-list-table-container height100vh fc-table-td-height fc-table-viewchooser pB100"
          >
            <div v-if="showLoading" class="flex-middle fc-empty-white">
              <spinner :show="showLoading" size="80"></spinner>
            </div>
            <div
              v-else-if="$validation.isEmpty(recordsList)"
              class="height100vh fc-empty-white flex-middle justify-content-center flex-direction-column"
            >
              <inline-svg
                src="svgs/emptystate/contracts"
                iconClass="icon text-center icon-xxxxlg"
              ></inline-svg>
              <div class="nowo-label">
                {{
                  $t('common.products.no_module_available', {
                    moduleName: moduleDisplayName
                      ? moduleDisplayName
                      : 'Records',
                  })
                }}
              </div>
            </div>
            <div v-else>
              <div
                class="view-column-chooser"
                @click="showColumnSettings = true"
              >
                <img
                  src="~assets/column-setting.svg"
                  style="text-align: center; position: absolute; top: 35%;right: 29%;"
                />
              </div>
              <div
                v-if="selectedRecords.length > 0"
                class="pull-left table-header-actions"
              >
                <div
                  v-if="$hasPermission('contract:DELETE')"
                  class="action-btn-slide btn-block"
                >
                  <button
                    class="btn btn--tertiary"
                    @click="deleteRecords(selectedRecords)"
                    :disabled="saving"
                  >
                    {{ $t('common._common.delete') }}
                  </button>
                </div>
                <div
                  v-if="
                    approveRejectVisibility() &&
                      $hasPermission('contract:APPROVE_REJECT_WORKREQUEST')
                  "
                  class="action-btn-slide btn-block"
                >
                  <button
                    :disabled="saving"
                    class="btn btn--tertiary"
                    @click="changeStatus(2)"
                  >
                    <div>{{ $t('common.header.approve') }}</div>
                  </button>
                </div>
                <div
                  v-if="
                    approveRejectVisibility() &&
                      $hasPermission('contract:APPROVE_REJECT_WORKREQUEST')
                  "
                  class="action-btn-slide btn-block"
                >
                  <button
                    :disabled="saving"
                    class="btn btn--tertiary"
                    @click="changeStatus(3)"
                  >
                    <div>{{ $t('common.header.reject') }}</div>
                  </button>
                </div>
              </div>
              <el-table
                :data="recordsList"
                style="width: 100%;"
                height="auto"
                @selection-change="selectRecords"
                :fit="true"
                :row-class-name="'no-hover'"
              >
                <el-table-column type="selection" width="60"></el-table-column>
                <el-table-column
                  fixed
                  prop
                  :label="$t('common._common.id')"
                  min-width="90"
                >
                  <template v-slot="record">
                    <div class="fc-id">{{ '#' + record.row.parentId }}</div>
                  </template>
                </el-table-column>
                <el-table-column
                  fixed
                  prop="name"
                  :label="$t('common.products.name')"
                  width="300"
                >
                  <template v-slot="record">
                    <div
                      @click="openOverview(record.row.id)"
                      v-tippy
                      small
                      :title="record.row.name"
                      class="flex-middle main-field-column"
                    >
                      <div class="fw5 ellipsis width200px">
                        {{ record.row.name }}
                      </div>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column
                  :align="
                    field.field.dataTypeEnum === 'DECIMAL' ? 'right' : 'left'
                  "
                  v-for="(field, index) in filteredViewColumns"
                  :key="index"
                  :prop="field.name"
                  :label="field.displayName"
                  min-width="200"
                >
                  <template v-slot="record">
                    <div v-if="!isFixedColumn(field.name) || field.parentField">
                      <div
                        class="table-subheading"
                        :class="{
                          'text-right': field.field.dataTypeEnum === 'DECIMAL',
                        }"
                      >
                        {{ getColumnDisplayValue(field, record.row) || '---' }}
                      </div>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column
                  width="130"
                  class="visibility-visible-actions"
                  fixed="right"
                >
                  <template v-slot="record">
                    <div class="text-center">
                      <i
                        class="el-icon-edit edit-icon-color visibility-hide-actions"
                        v-if="isContractEditable(record.row.status)"
                        :title="$t('common._common.edit')"
                        data-arrow="true"
                        v-tippy
                        @click="editData(record.row)"
                      ></i>
                      <i
                        class="el-icon-delete pointer edit-icon-color visibility-hide-actions mL10"
                        data-arrow="true"
                        v-if="isContractRemovable(record.row.status)"
                        :title="$t('common._common.delete')"
                        v-tippy
                        @click="deleteRecords([record.row.id])"
                      ></i>
                    </div>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
          <column-customization
            :visible.sync="showColumnSettings"
            :moduleName="moduleName"
            :viewName="currentView"
          ></column-customization>
        </div>
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
      </template>
    </template>
  </CommonListLayout>
</template>

<script>
import { mapState } from 'vuex'
import cloneDeep from 'lodash/cloneDeep'
import ViewMixinHelper from '@/mixins/ViewMixin'
import { isEmpty, isArray } from '@facilio/utils/validation'
import ColumnCustomization from '@/ColumnCustomization'
import Spinner from '@/Spinner'
import CommonListLayout from 'newapp/list/DeprecatedCommonLayout'
import Pagination from '@/list/FPagination'
import Search from 'newapp/components/Search'
import Tags from 'newapp/components/Tags'
import FExportSettings from '@/FExportSettings'
import Sort from 'newapp/components/Sort'
import contractMixin from 'pages/contract/mixin/contractHelper'
import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'
import isEqual from 'lodash/isEqual'
import { findRouterForModuleInApp } from 'newapp/viewmanager/routeUtil'
import { getColumnConfig } from 'src/newapp/components/column-customization/columnConfigUtil.js'

const moduleInfoMap = {
  purchasecontracts: {
    storeName: 'purchasecontract',
    fetchListMethod: 'fetchPurchaseContracts',
    fetchUrl: 'purchasecontract',
    recordsState: 'purchaseContracts',
    displayName: 'Purchase Contract',
  },
  labourcontracts: {
    storeName: 'labourcontract',
    fetchListMethod: 'fetchLabourContracts',
    fetchUrl: 'labourcontract',
    recordsState: 'labourContracts',
    displayName: 'Labour Contract',
  },
  rentalleasecontracts: {
    storeName: 'rentalleasecontract',
    fetchListMethod: 'fetchRentalLeaseContracts',
    fetchUrl: 'rentalleasecontract',
    recordsState: 'rentalLeaseContracts',
    displayName: 'Lease/Rental Contract',
  },
  warrantycontracts: {
    storeName: 'warrantycontract',
    fetchListMethod: 'fetchWarrantyContracts',
    fetchUrl: 'warrantycontract',
    recordsState: 'warrantyContracts',
    displayName: 'Warranty Contract',
  },
}

const FILTER_CONFIG_OBJ = {
  includeParentCriteria: true,
  data: {
    name: {
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
  name: 'ContractCommonList',
  props: ['moduleName', 'viewname'],
  mixins: [ViewMixinHelper, contractMixin],
  components: {
    ColumnCustomization,
    Spinner,
    CommonListLayout,
    Pagination,
    Search,
    Tags,
    FExportSettings,
    Sort,
  },
  created() {
    this.init()
  },
  data() {
    return {
      sortConfig: {
        orderBy: 'localId',
        orderType: 'desc',
      },
      sortConfigLists: ['name', 'endDate', 'fromDate', 'totalCost', 'localId'],
      filterConfig: null,
      defaultFilter: 'name',
      listCount: '',
      loading: true,
      selectedRecords: [],
      selectedRecordsObj: [],
      showColumnSettings: false,
      tableLoading: false,
      saving: false,
    }
  },
  computed: {
    ...mapState({
      viewDetail: state => state.view.currentViewDetail,
      metaInfo: state => state.view.metaInfo,
      showSearch: state => state.search.active,
    }),
    moduleInfo() {
      return moduleInfoMap[this.moduleName] || {}
    },
    moduleDisplayName() {
      return this.metaInfo?.displayName
    },
    parentPath() {
      let { moduleName } = this
      return `/app/ct/${moduleName}/`
    },
    appliedFilters() {
      let {
        $route: { query },
      } = this
      let { search } = query || {}
      return search ? JSON.parse(search) : null
    },
    recordsList() {
      let { storeName, recordsState } = this.moduleInfo
      return this.$store.state[storeName][recordsState]
    },
    currentView() {
      let {
        params: { viewname },
      } = this.$route
      return viewname
    },
    searchQuery() {
      let { storeName } = this.moduleInfo
      return this.$store.state[storeName].quickSearchQuery
    },
    page() {
      return this.$route.query.page || 1
    },
    showLoading() {
      return (
        this.loading ||
        this.$store.state.view.detailLoading ||
        this.tableLoading
      )
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
  },
  watch: {
    moduleName(newVal, oldVal) {
      if (newVal !== oldVal) {
        this.init()
        this.loadRecords()
        this.loadRecordsCount()
      }
    },
    currentViewFields() {
      this.tableLoading = true
      this.$nextTick(() => {
        this.$refs.tableList ? this.$refs.tableList.doLayout() : null
        this.tableLoading = false
      })
    },
    currentView: {
      handler(newVal, oldVal) {
        if (newVal && oldVal !== newVal) {
          this.loadRecords()
          this.loadRecordsCount()
        }
      },
      immediate: true,
    },
    appliedFilters(newVal, oldVal) {
      if (!isEqual(newVal, oldVal)) {
        this.loadRecords()
        this.loadRecordsCount()
      }
    },
    page(newVal, oldVal) {
      if (oldVal !== newVal && !this.loading) {
        this.loadRecords()
      }
    },
    searchQuery() {
      this.loadRecords()
      this.loadRecordsCount()
    },
    viewDetail(newVal, oldVal) {
      if (newVal && oldVal !== newVal) {
        if (
          !isEmpty(this.viewDetail.sortFields) &&
          isArray(this.viewDetail.sortFields)
        ) {
          this.sortConfig = {
            orderType: this.$getProperty(
              this.viewDetail,
              ['sortFields', 0, 'isAscending'],
              false
            )
              ? 'asc'
              : 'desc',
            orderBy: this.$getProperty(
              this.viewDetail,
              ['sortFields', 0, 'sortField', 'name'],
              'localId'
            ),
          }
        }
      }
    },
  },
  methods: {
    init() {
      this.loadViewMetaInfo()
      this.initFilterConfig()
      this.selectedRecords = []
      this.selectedRecordsObj = []
    },
    getViewManagerRoute() {
      let { moduleName, $route } = this
      let { query } = $route

      let route = findRouterForModuleInApp(moduleName, pageTypes.VIEW_MANAGER)
      if (route)
        this.$router.push({
          ...route,
          params: { moduleName },
          query: { ...query },
        })
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
        .then(() => this.refreshList(false))
    },
    initFilterConfig() {
      let { moduleName } = this
      let configObj = {
        ...cloneDeep(FILTER_CONFIG_OBJ),
        moduleName,
        path: `/app/ct/${moduleName}/`,
      }
      this.$set(this, 'filterConfig', configObj)
    },
    redirectToFormCreation() {
      let { moduleName } = this

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.CREATE) || {}
        if (name) {
          this.$router.push({ name })
        }
      } else {
        this.$router.push({ path: `/app/ct/${moduleName}/new` })
      }
    },
    selectRecords(selectedRecordsObj) {
      this.selectedRecordsObj = selectedRecordsObj
      this.selectedRecords = selectedRecordsObj.map(value => value.id)
    },
    editData({ id }) {
      let { moduleName } = this

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.EDIT) || {}
        if (name) {
          this.$router.push({ name, params: { id } })
        }
      } else {
        this.$router.push({
          path: `/app/ct/${moduleName}/edit/${id}`,
        })
      }
    },
    loadRecords() {
      let queryObj = {
        viewname: this.currentView,
        page: this.page,
        filters: this.appliedFilters,
        search: this.searchQuery,
        isNew: true,
        includeParentFilter: this.includeParentFilter,
      }
      this.loading = true
      let { storeName, fetchListMethod } = this.moduleInfo
      this.$store
        .dispatch(`${storeName}/${fetchListMethod}`, queryObj)
        .catch(({ message }) => {
          if (message) {
            this.$message.error(message)
          }
        })
        .finally(() => {
          this.loading = false
        })
    },
    loadRecordsCount() {
      let queryObj = {
        viewname: this.currentView,
        filters: this.appliedFilters,
        search: this.quickSearchQuery,
        criteriaIds: this.$route.query.criteriaIds,
        includeParentFilter: this.includeParentFilter,
        count: true,
      }
      let { fetchUrl } = this.moduleInfo
      let url = `/v2/${fetchUrl}/list?viewName=` + queryObj.viewname
      let params
      params = 'fetchCount=' + queryObj.count
      if (queryObj.filters) {
        params =
          params +
          '&filters=' +
          encodeURIComponent(JSON.stringify(queryObj.filters))
      }
      if (queryObj.search) {
        params = params + '&search=' + queryObj.search
      }
      if (queryObj.criteriaIds) {
        params = params + '&criteriaIds=' + queryObj.criteriaIds
      }
      if (queryObj.includeParentFilter) {
        params = params + '&includeParentFilter=' + queryObj.includeParentFilter
      }
      url = url + '&' + params
      this.$http
        .get(url)
        .then(({ data: { responseCode, message, result } }) => {
          if (responseCode === 0) {
            let { recordCount } = result || {}
            this.listCount = recordCount
          } else {
            throw new Error(message)
          }
        })
        .catch(({ message }) => {
          this.$message.error(message)
        })
    },
    deleteRecords(idList) {
      let { fetchUrl, displayName } = this.moduleInfo
      this.$dialog
        .confirm({
          title: this.$t('common.header.delete_displayname', { displayName }),
          message: this.$t(
            'common.header.are_you_sure_you_want_to_delete_this_display_name',
            { displayName }
          ),

          rbDanger: true,
          rbLabel: this.$t('common._common.delete'),
        })
        .then(value => {
          if (value) {
            this.saving = true
            this.$http
              .post(`/v2/${fetchUrl}/delete`, { recordIds: idList })
              .then(response => {
                if (response.data.responseCode === 0) {
                  response.data.result.recordIds.forEach(element => {
                    let record = this.recordsList.find(r => r.id === element)
                    if (record) {
                      this.recordsList.splice(
                        this.recordsList.indexOf(record),
                        1
                      )
                      this.$message.success(
                        this.$t(
                          'common._common.displayname_deleted_successfully',
                          { displayName }
                        )
                      )
                    }
                  })
                  this.saving = false
                  this.selectedRecords = []
                  this.selectedRecordsObj = []
                } else {
                  throw new Error(response.data.message)
                }
              })
              .catch(({ message }) => {
                this.$message.error(message)
                this.saving = false
              })
          }
        })
    },
    openOverview(id) {
      let {
        moduleName,
        currentView,
        $route: { query },
      } = this

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}
        if (name) {
          this.$router.push({
            name,
            params: { id, viewname: currentView },
            query,
          })
        }
      } else {
        this.$router.push({
          path: `/app/ct/${moduleName}/${currentView}/summary/${id}`,
          query,
        })
      }
    },
    approveRejectVisibility() {
      if (this.selectedRecordsObj.length === 0) {
        return false
      }
      for (let i in this.selectedRecordsObj) {
        if (parseInt(this.selectedRecordsObj[i].status) !== 1) {
          return false
        }
      }
      return true
    },
    changeStatus(id) {
      let param = {
        recordIds: this.selectedRecords,
        status: id,
      }
      let { fetchUrl, displayName } = this.moduleInfo
      this.saving = true
      this.$http
        .post(`/v2/${fetchUrl}/changeStatus`, param)
        .then(response => {
          if (response.data.responseCode === 0) {
            if (id === 2) {
              this.$message.success(
                this.$t('common._common.displayname_approved_successfully', {
                  displayName,
                })
              )
            } else if (id === 3) {
              this.$message.success(
                this.$t('common._common.displayname_rejected_successfully', {
                  displayName,
                })
              )
            }
            this.loadRecords()
            this.selectedRecords = []
            this.selectedRecordsObj = []
            this.saving = false
          } else {
            throw new Error(response.data.message)
          }
        })
        .catch(({ message }) => {
          this.$message.error(message)
          this.saving = false
        })
    },
    refreshList(loadCount) {
      this.loadRecords()
      if (loadCount) {
        this.loadRecordsCount()
      }
    },
  },
}
</script>
