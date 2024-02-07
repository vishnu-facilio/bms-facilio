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
    pathPrefix="/app/bk/facility/"
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
          {{ $t('tenant.booking.new_facility') }}
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
          :class="[$route.query.search && 'fc-list-table-search-scroll']"
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
              src="svgs/emptystate/commonempty"
              iconClass="icon text-center icon-xxxxlg"
            ></inline-svg>
            <div class="q-item-label nowo-label">
              {{ $t('tenant.booking.no_data_facility') }}
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
            <el-table
              :data="moduleRecordList"
              ref="tableList"
              class="fc-list-eltable width100"
              height="auto"
              :fit="true"
              stripe
              @selection-change="selectRecords"
            >
              <el-table-column
                v-if="$hasPermission(`${moduleName}:DELETE`)"
                type="selection"
                width="60"
                fixed
              ></el-table-column>
              <el-table-column
                fixed
                prop
                label="ID"
                min-width="90"
                class="pR0 pL0"
              >
                <template v-slot="data" class="pL0">
                  <div class="fc-id">{{ '#' + data.row.localId }}</div>
                </template>
              </el-table-column>
              <el-table-column
                :label="titleColumn.displayName"
                :prop="titleColumn.name"
                fixed
                min-width="250"
              >
                <template v-slot="item">
                  <div
                    @click="openRecordSummary(item.row.id)"
                    class="table-subheading"
                  >
                    <div class="d-flex">
                      <div class="self-center bold">
                        {{
                          getColumnDisplayValue(titleColumn, item.row) || '---'
                        }}
                      </div>
                    </div>
                  </div>
                </template>
              </el-table-column>
              <el-table-column
                v-for="(field, index) in filteredViewColumns"
                :key="index"
                :prop="field.name"
                :label="field.displayName"
                min-width="200"
                :align="
                  field.field && field.field.dataTypeEnum === 'DECIMAL'
                    ? 'right'
                    : 'left'
                "
              >
                <template v-slot="data">
                  <template>
                    <div
                      class="table-subheading"
                      :class="{
                        'text-right':
                          $getProperty(field, 'field.dataTypeEnum') ===
                          'DECIMAL',
                      }"
                    >
                      {{ getColumnDisplayValue(field, data.row) || '---' }}
                    </div>
                  </template>
                </template>
              </el-table-column>
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
                      v-if="
                        $hasPermission(`${moduleName}:UPDATE`) &&
                          canShowActions(data.row)
                      "
                      class="el-icon-edit pointer edit-icon-color visibility-hide-actions mL10"
                      data-arrow="true"
                      :title="$t('common._common.edit')"
                      v-tippy
                      @click="editRecord(data.row)"
                    ></i>
                    <i
                      class="el-icon-delete pointer edit-icon-color visibility-hide-actions mL10"
                      data-arrow="true"
                      v-if="
                        $hasPermission(`${moduleName}:DELETE`) &&
                          canShowActions(data.row)
                      "
                      :title="$t('common._common.delete')"
                      v-tippy
                      @click="deleteRecords([data.row.id])"
                    ></i>
                  </div>
                </template>
              </el-table-column>
            </el-table>
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
          :to="`/app/bk/facility/viewmanager`"
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
import ViewMixinHelper from '@/mixins/ViewMixin'
import ColumnCustomization from '@/ColumnCustomization'
import Spinner from '@/Spinner'
import CommonListLayout from 'newapp/list/CommonLayout'
import Pagination from '@/list/FPagination'
import Sort from 'newapp/components/Sort'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import { getColumnConfig } from 'src/newapp/components/column-customization/columnConfigUtil.js'
import AdvancedSearchWrapper from 'newapp/components/search/AdvancedSearchWrapper.vue'

export default {
  props: ['viewname'],
  mixins: [ViewMixinHelper],
  components: {
    ColumnCustomization,
    Spinner,
    CommonListLayout,
    Pagination,
    AdvancedSearchWrapper,
    Sort,
  },
  data() {
    return {
      sortConfig: {
        orderBy: 'id',
        orderType: 'desc',
      },
      sortConfigLists: ['name'],
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
    this.$store.dispatch('loadApprovalStatus')
    this.$store.dispatch('loadTicketStatus', this.moduleName)
    this.$store.dispatch('view/loadModuleMeta', this.moduleName)
  },
  computed: {
    ...mapState({
      viewDetail: state => state.view.currentViewDetail,
      viewLoading: state => state.view.isLoading,
      metaInfo: state => state.view.metaInfo,
      showSearch: state => state.search.active,
    }),

    ...mapGetters(['getApprovalStatus', 'isStatusLocked']),
    moduleDisplayName() {
      return this.metaInfo?.displayName || 'Facility'
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
    titleColumn() {
      let { viewColumns } = this
      if (!isEmpty(viewColumns)) {
        return viewColumns.find(column => column.name === 'name')
      }
      return {}
    },
    moduleName() {
      return 'facility'
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
        this.$message.error(
          message || 'Error Occured while fetching facility list'
        )
      } else {
        this.listCount = this.$getProperty(meta, 'pagination.totalCount', null)
        this.moduleRecordList = list || []
      }
      this.loading = false
    },
    async deleteRecords(idList) {
      let value = await this.$dialog.confirm({
        title: this.$t(`tenant.booking.delete_facility`),
        message: this.$t(`tenant.booking.delete_confirmation_facility`),
        rbDanger: true,
        rbLabel: this.$t('common._common.delete'),
      })

      if (value) {
        let { moduleName } = this
        let { error } = await API.deleteRecord(moduleName, idList)
        if (error) {
          let { message } = error
          this.$message.error(
            message || 'Error Occured while deleting facility'
          )
          this.loading = false
        } else {
          this.$message.success(
            this.$t(`tenant.booking.delete_success_facility`)
          )
          this.loadRecords(true)
          this.selectedRecords = []
          this.selectedRecordsObj = []
        }
      }
    },
    openRecordSummary(id) {
      let { viewname } = this
      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForModule(this.moduleName, pageTypes.OVERVIEW) || {}

        if (name) {
          this.$router.push({
            name,
            params: {
              viewname,
              id,
            },
          })
        }
      } else {
        this.$router.push({
          name: 'facilitySummary',
          params: {
            viewname,
            id,
          },
        })
      }
    },
    addRecord() {
      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForModule(this.moduleName, pageTypes.CREATE) || {}

        if (name) {
          this.$router.push({
            name,
          })
        }
      } else {
        this.$router.push({ name: 'new-facility' })
      }
    },
    editRecord({ id = null } = {}) {
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(this.moduleName, pageTypes.EDIT) || {}

        if (id && name) {
          this.$router.push({
            name,
            params: { id },
          })
        }
      } else if (id) {
        this.$router.push({ name: 'edit-facility', params: { id } })
      }
    },
    selectRecords(selectedRecordsObj) {
      this.selectedRecordsObj = selectedRecordsObj
      this.selectedRecords = selectedRecordsObj.map(value => value.id)
    },
    canShowActions(record) {
      return this.hasModuleState(record)
        ? !this.isRecordLocked(record) && !this.isRequestedState(record)
        : true
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
cm-sub-header-txt {
  margin: 12px 16px;
}
</style>
