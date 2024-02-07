<template>
  <CommonListLayout
    :moduleName="moduleName"
    :showViewRearrange="true"
    :showViewEdit="true"
    :visibleViewCount="3"
    :getPageTitle="() => moduleDisplayName"
    :hideSubHeader="isEmpty(recordList)"
    :recordCount="listCount"
    :recordLoading="showLoading"
    pathPrefix="/app/vi/watchlist/"
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
          class="fc-create-btn"
          @click="openNewForm()"
        >
          {{ $t('common.products.new_watchlist') }}
        </button>
      </template>
    </template>

    <template #sub-header-actions>
      <template v-if="!isEmpty(recordList)">
        <pagination
          :total="listCount"
          :perPage="50"
          :skipTotalCount="true"
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
        <template v-if="!isAltayerNonPrivilagedUser">
          <f-export-settings
            :module="moduleName"
            :viewDetail="viewDetail"
            :showViewScheduler="false"
            :showMail="false"
            :filters="appliedFilters"
          ></f-export-settings>
        </template>
      </template>
    </template>
    <template #content>
      <template>
        <div class="fc-card-popup-list-view">
          <div
            class="fc-list-view p10 pT0  fc-list-table-container height100vh fc-table-td-height fc-table-viewchooser pB100"
          >
            <Spinner
              v-if="showLoading"
              class="mT40"
              :show="showLoading"
            ></Spinner>
            <div
              v-else-if="$validation.isEmpty(recordList)"
              class="fc-list-empty-state-container"
            >
              <inline-svg
                src="svgs/list-empty"
                iconClass="icon text-center icon-xxxxlg"
              ></inline-svg>
              <div class="q-item-label nowo-label">
                {{ $t('home.visitor.visitor_no_data') }}
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
                v-if="!$validation.isEmpty(selectedRecordIds)"
                class="pull-left table-header-actions"
              >
                <div class="action-btn-slide btn-block">
                  <button
                    class="btn btn--tertiary"
                    @click="deleteRecords(selectedRecordIds)"
                    :disabled="saving"
                  >
                    {{ $t('common._common.delete') }}
                  </button>
                </div>
              </div>
              <el-table
                :data="recordList"
                ref="tableList"
                class="fc-list-eltable width100"
                height="auto"
                :fit="true"
                @selection-change="selectRecords"
              >
                <el-table-column
                  v-if="$hasPermission(`${moduleName}:DELETE`)"
                  type="selection"
                  width="60"
                ></el-table-column>
                <el-table-column
                  fixed
                  prop
                  label="ID"
                  min-width="90"
                  class="pR0 pL0"
                >
                  <template v-slot="data" class="pL0">
                    <div class="fc-id">{{ '#' + data.row.id }}</div>
                  </template>
                </el-table-column>
                <el-table-column
                  v-if="!$validation.isEmpty(nameColumn)"
                  :label="nameColumn.displayName"
                  :prop="nameColumn.name"
                  fixed
                  min-width="250"
                >
                  <template v-slot="item">
                    <div
                      @click="goToOverview(item.row)"
                      class="table-subheading"
                    >
                      <div class="d-flex">
                        <VisitorAvatar
                          module="visitor"
                          :name="false"
                          size="lg"
                          :recordData="item.row"
                        ></VisitorAvatar>
                        <div class="self-center mL10">
                          {{
                            getColumnDisplayValue(nameColumn, item.row) || '---'
                          }}
                        </div>
                      </div>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column
                  :align="
                    field.field && field.field.dataTypeEnum === 'DECIMAL'
                      ? 'right'
                      : 'left'
                  "
                  v-for="(field, index) in filteredViewColumns"
                  :key="index"
                  :prop="field.name"
                  :label="field.displayName"
                  min-width="200"
                >
                  <template v-slot="data">
                    <div>
                      <div
                        class="table-subheading"
                        :class="{
                          'text-right':
                            (field.field || {}).dataTypeEnum === 'DECIMAL',
                        }"
                      >
                        {{ getColumnDisplayValue(field, data.row) || '---' }}
                      </div>
                    </div>
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
                        v-if="$hasPermission(`${moduleName}:UPDATE`)"
                        class="el-icon-edit pointer edit-icon-color visibility-hide-actions mL10"
                        data-arrow="true"
                        :title="$t('common._common.edit')"
                        v-tippy
                        @click="editRecord(data.row)"
                      ></i>
                      <i
                        v-if="$hasPermission(`${moduleName}:DELETE`)"
                        class="el-icon-delete pointer edit-icon-color visibility-hide-actions mL10"
                        data-arrow="true"
                        :title="$t('common._common.delete')"
                        v-tippy
                        @click="deleteRecords([data.row.id])"
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
          <WatchListForm
            v-if="formVisibility"
            :visibility.sync="formVisibility"
            :editId="editId"
            :moduleName="moduleName"
            @saved="refreshList()"
          >
          </WatchListForm>
        </div>
      </template>
    </template>
    <portal to="view-manager-link">
      <router-link
        tag="div"
        :to="{ name: 'vi-viewmanager', params: { moduleName } }"
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
    <router-view></router-view>
  </CommonListLayout>
</template>

<script>
import { mapState } from 'vuex'
import ViewMixinHelper from '@/mixins/ViewMixin'
import { isEmpty } from '@facilio/utils/validation'
import ColumnCustomization from '@/ColumnCustomization'
import Spinner from '@/Spinner'
import CommonListLayout from 'newapp/list/CommonLayout'
import Pagination from 'src/newapp/components/ListPagination'
import FExportSettings from '@/FExportSettings'
import Sort from 'newapp/components/Sort'
import WatchListForm from './WatchListForm'
import VisitorAvatar from '@/avatar/VisitorAvatar'
import { API } from '@facilio/api'
import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'
import { getColumnConfig } from 'src/newapp/components/column-customization/columnConfigUtil.js'
import OtherMixin from '@/mixins/OtherMixin'
import AdvancedSearchWrapper from 'newapp/components/search/AdvancedSearchWrapper'

export default {
  name: 'WatchListRecords',
  props: ['viewname', 'moduleName'],
  mixins: [ViewMixinHelper, OtherMixin],
  components: {
    ColumnCustomization,
    Spinner,
    CommonListLayout,
    Pagination,
    FExportSettings,
    Sort,
    WatchListForm,
    VisitorAvatar,
    AdvancedSearchWrapper,
  },
  created() {
    this.loadViewMetaInfo()
  },
  data() {
    return {
      sortConfig: {
        orderBy: 'name',
        orderType: 'asc',
      },
      sortConfigLists: ['name'],
      listCount: '',
      loading: true,
      recordList: [],
      selectedRecordsObj: [],
      showColumnSettings: false,
      tableLoading: false,
      saving: false,
      formVisibility: false,
      editId: null,
      isEmpty,
    }
  },
  computed: {
    ...mapState({
      viewDetail: state => state.view.currentViewDetail,
      metaInfo: state => state.view.metaInfo,
      showSearch: state => state.search.active,
      detailLoading: state => state.view.detailLoading,
      viewLoading: state => state.view.isLoading,
    }),
    nameColumn() {
      let { viewColumns } = this
      if (!isEmpty(viewColumns)) {
        return viewColumns.find(column => column.name === 'name')
      }
      return {}
    },
    moduleDisplayName() {
      return this.metaInfo?.displayName
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
      return (
        this.viewLoading ||
        this.loading ||
        this.detailLoading ||
        this.tableLoading
      )
    },
    currentViewFields() {
      let { fields } = this.viewDetail || {}
      return fields
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
    selectedRecordIds() {
      let { selectedRecordsObj } = this
      return selectedRecordsObj.map(value => value.id)
    },
  },
  watch: {
    currentViewFields() {
      this.tableLoading = true
      this.$nextTick(() => {
        this.$refs.tableList ? this.$refs.tableList.doLayout() : null
        this.tableLoading = false
      })
    },
    currentView: {
      handler(newVal, oldVal) {
        if (oldVal !== newVal && !isEmpty(newVal)) {
          this.loadRecords()
        }
      },
      immediate: true,
    },
    appliedFilters() {
      this.loadRecords()
    },
    page(newVal, oldVal) {
      if (oldVal !== newVal) {
        this.loadRecords()
      }
    },
    viewDetail() {
      let { sortFields = [] } = this.viewDetail || {}

      if (!isEmpty(sortFields)) {
        let { name } = this.$getProperty(sortFields[0], 'sortField', {})
        let { isAscending } = this.$getProperty(sortFields, '0', {})

        this.sortConfig = {
          orderType: isAscending ? 'asc' : 'desc',
          orderBy: name ? name : '',
        }
      }
    },
  },
  methods: {
    loadViewMetaInfo() {
      let { moduleName } = this
      return this.$store
        .dispatch('view/loadModuleMeta', moduleName)
        .then(({ fields }) => {
          if (!isEmpty(fields)) {
            this.sortConfigLists = fields.map(fld => fld.name)
          }
        })
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
        .then(() => this.refreshList())
    },
    refreshList() {
      this.loadRecords()
    },
    async getRecordDetails() {
      let { currentView, appliedFilters, page } = this
      let params = {
        withCount: true,
        viewName: currentView,
        includeParentFilter: this.includeParentFilter,
        page,
        perPage: 50,
        filters: appliedFilters ? JSON.stringify(appliedFilters) : null,
      }
      let {
        list,
        meta: { pagination = {} },
        error,
      } = await API.fetchAll(this.moduleName, params)

      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.listCount = this.$getProperty(pagination, 'totalCount', null)
        this.recordList = list || []
      }
    },
    async loadRecords() {
      this.loading = true
      await this.getRecordDetails()
      this.loading = false
    },
    deleteRecords(id) {
      this.$dialog
        .confirm({
          title: this.$t('home.visitor.delete_watch_list'),
          message: this.$t('home.visitor.delete_watch_list_confirmation'),
          rbDanger: true,
          rbLabel: this.$t('common._common.delete'),
        })
        .then(value => {
          if (value) {
            API.deleteRecord(this.moduleName, id).then(({ error }) => {
              if (error) {
                this.$message.error(error.messsage || 'Error Occured')
              } else {
                this.$message.success(
                  this.$t('home.visitor.watch_list_delete_success')
                )
                this.loadRecords()
              }
            })
          }
        })
        .catch(({ message }) => {
          this.$message.error(message)
          this.saving = false
        })
    },
    goToOverview({ id }) {
      let { moduleName, currentView, $route } = this
      let params = { viewname: currentView, id }
      let { query } = $route || {}

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}

        if (name) {
          this.$router.replace({ name, params, query })
        }
      } else {
        this.$router.replace({
          name: 'watchlist-overview',
          params,
          query,
        })
      }
    },
    selectRecords(selectedRecordsObj) {
      this.selectedRecordsObj = selectedRecordsObj
    },
    openNewForm() {
      this.editId = null
      this.formVisibility = true
    },
    editRecord(data) {
      this.editId = data.id
      this.formVisibility = true
    },
  },
}
</script>
<style scoped></style>
