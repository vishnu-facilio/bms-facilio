<template>
  <CommonListLayout
    :moduleName="moduleName"
    :showViewRearrange="true"
    :showViewEdit="true"
    :visibleViewCount="3"
    :getPageTitle="() => moduleDisplayName"
    :pathPrefix="parentPath"
    :key="moduleName + '-list-layout'"
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
        <span v-if="canShowExport" class="separator">|</span>

        <el-tooltip
          v-if="canShowExport"
          effect="dark"
          :content="$t('common._common.export')"
          placement="left"
        >
          <f-export-settings
            :module="moduleName"
            :viewDetail="viewDetail"
            :showViewScheduler="false"
            :showMail="false"
            :filters="filters"
          ></f-export-settings>
        </el-tooltip>
        <span class="separator">|</span>
      </template>

      <el-tooltip
        effect="dark"
        :content="$t('common._common.search')"
        placement="left"
      >
        <AdvancedSearch
          :key="`${moduleName}-search`"
          :moduleName="moduleName"
          :moduleDisplayName="moduleDisplayName"
        >
        </AdvancedSearch>
      </el-tooltip>

      <template v-if="showNewButton">
        <span class="separator pL10">|</span>
        <div>
          <button
            class="fc-create-btn create-btn"
            @click="redirectToFormCreation()"
          >
            {{
              $t('common.products.new_module', {
                moduleName: moduleDisplayName ? moduleDisplayName : '',
              })
            }}
          </button>
        </div>
      </template>
    </template>
    <template #content>
      <div>
        <FTags :key="`ftags-list-${moduleName}`"></FTags>
      </div>
      <Spinner v-if="showLoading" class="mT40" :show="showLoading"></Spinner>

      <div
        v-if="$validation.isEmpty(records) && !showLoading"
        class="d-flex flex-direction-column m10 list-empty-state justify-content-center"
      >
        <img
          class="mT20 self-center"
          src="~statics/noData-light.png"
          width="100"
          height="100"
        />
        <div class="mT10 label-txt-black f14 self-center">
          {{
            $t('common.products.no_module_available', {
              moduleName: moduleDisplayName
                ? moduleDisplayName.toLowerCase()
                : moduleName,
            })
          }}
        </div>
      </div>

      <template v-if="!showLoading && !$validation.isEmpty(records)">
        <div
          class="fc-list-view p10 pT0 mT10 fc-list-table-container height100vh fc-table-td-height fc-table-viewchooser pB100"
          :class="$route.query.search ? 'fc-list-table-search-scroll' : ''"
        >
          <div class="view-column-chooser" @click="showColumnSettings = true">
            <img
              src="~assets/column-setting.svg"
              class="text-center position-absolute"
              style="top: 35%;right: 29%;"
            />
          </div>
          <div
            class="pull-left table-header-actions"
            v-if="!$validation.isEmpty(selectedListItems)"
          >
            <div class="action-btn-slide btn-block">
              <button
                class="btn btn--tertiary pointer"
                @click="showConfirmDelete()"
              >
                {{ $t('common._common.delete') }}
              </button>
            </div>
          </div>
          <el-table
            :data="records"
            style="width: 100%;"
            height="100%"
            :fit="true"
            @selection-change="selectItems"
            :row-class-name="'no-hover'"
          >
            <el-table-column
              type="selection"
              width="60"
              fixed
              v-model="selectAll"
            ></el-table-column>
            <el-table-column
              v-if="!$validation.isEmpty(nameColumn)"
              :label="nameColumn.displayName"
              :prop="nameColumn.name"
              fixed
              min-width="250"
            >
              <template v-slot="item">
                <div class="table-subheading main-field-column">
                  <div class="d-flex" @click="redirectToOverview(item.row)">
                    <div v-if="item.row.photoId > 0">
                      <img
                        :src="getImage(item.row.photoId)"
                        class="img-container"
                      />
                    </div>
                    <el-tooltip
                      effect="dark"
                      :content="
                        getColumnDisplayValue(nameColumn, item.row) || ''
                      "
                      placement="top"
                      :open-delay="600"
                    >
                      <div class="self-center mL5">
                        {{
                          getColumnDisplayValue(nameColumn, item.row) || '---'
                        }}
                      </div>
                    </el-tooltip>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column
              v-for="(field, index) in filteredViewColumns"
              :key="index"
              :prop="field.name"
              :label="getColumnHeaderLabel(field)"
              :align="
                field.field && field.field.dataTypeEnum === 'DECIMAL'
                  ? 'right'
                  : 'left'
              "
              min-width="230"
            >
              <template v-slot="scope">
                <div
                  v-if="
                    $getProperty(field, 'field.displayType') === 'SIGNATURE'
                  "
                >
                  <SignatureField
                    :field="(field || {}).field"
                    :record="scope.row"
                  />
                </div>
                <div v-else-if="isFileTypeField($getProperty(field, 'field'))">
                  <FilePreviewColumn
                    :field="field"
                    :record="scope.row"
                    :isV2="true"
                  />
                </div>
                <div
                  v-else
                  class="table-subheading"
                  :class="{
                    'text-right':
                      field.field && field.field.dataTypeEnum === 'DECIMAL',
                  }"
                >
                  {{ getColumnDisplayValue(field, scope.row) || '---' }}
                </div>
              </template>
            </el-table-column>
            <el-table-column
              prop
              label
              width="130"
              class="visibility-visible-actions"
            >
              <template v-slot="item">
                <div class="text-center">
                  <span
                    v-if="
                      $org.id === 321 && moduleName === 'custom_tenantbilling'
                    "
                    @click="downloadTenantBilling(item.row)"
                  >
                    <inline-svg
                      src="svgs/download2"
                      class="download-icon visibility-hide-actions"
                      iconClass="icon icon-sm mR20 pointer"
                    ></inline-svg>
                    <iframe
                      v-if="downloadUrl"
                      :src="downloadUrl"
                      style="display: none;"
                    ></iframe>
                  </span>
                  <span v-if="canShowEdit" @click="editModule(item.row)">
                    <inline-svg
                      src="svgs/edit"
                      class="edit-icon-color visibility-hide-actions"
                      iconClass="icon icon-sm mR5 icon-edit"
                    ></inline-svg>
                  </span>
                  <span
                    v-if="canShowDelete"
                    @click="showConfirmDelete(item.row)"
                  >
                    <inline-svg
                      src="svgs/delete"
                      class="pointer edit-icon-color visibility-hide-actions mL10"
                      iconClass="icon icon-sm icon-remove"
                    ></inline-svg>
                  </span>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <column-customization
          :visible.sync="showColumnSettings"
          :moduleName="moduleName"
          :viewName="view"
          :columnConfig="columnConfig"
        ></column-customization>
      </template>
    </template>
    <BillSummaryDialog
      v-if="showBillSummary"
      :summary="selectedRow"
      :viewname="viewname"
      @onClose="showBillSummary = false"
    ></BillSummaryDialog>
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
import { mapState } from 'vuex'
import ViewMixinHelper from '@/mixins/ViewMixin'
import { isEmpty } from '@facilio/utils/validation'
import ColumnCustomization from '@/ColumnCustomization'
import Spinner from '@/Spinner'
import { API } from '@facilio/api'
import CommonListLayout from 'newapp/list/DeprecatedCommonLayout'
import Pagination from '@/list/FPagination'
import FExportSettings from '@/FExportSettings'
import Sort from 'newapp/components/Sort'
import SignatureField from '@/list/SignatureColumn'
import BillSummaryDialog from 'src/pages/etisalat/UtilityBills/UtilityBillsSummaryDialog'
import AdvancedSearch from 'newapp/components/search/AdvancedSearch'
import FTags from 'newapp/components/search/FTags'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
  getApp,
} from '@facilio/router'
import { getBaseURL } from 'util/baseUrl'
import FilePreviewColumn from '@/list/FilePreviewColumn'
import { isFileTypeField } from '@facilio/utils/field'
import isEqual from 'lodash/isEqual'
import { findRouterForModuleInApp } from 'newapp/viewmanager/routeUtil'

export default {
  name: 'CustomModuleList',
  props: ['moduleName', 'viewname'],
  mixins: [ViewMixinHelper],
  components: {
    ColumnCustomization,
    Spinner,
    CommonListLayout,
    Pagination,
    FExportSettings,
    Sort,
    SignatureField,
    BillSummaryDialog,
    AdvancedSearch,
    FTags,
    FilePreviewColumn,
  },
  created() {
    this.init()
    this.isFileTypeField = isFileTypeField
  },
  data() {
    return {
      showBillSummary: false,
      selectAll: false,
      selectedListItems: [],
      showColumnSettings: false,
      columnConfig: {
        fixedColumns: ['name'],
        fixedSelectableColumns: ['photo'],
      },
      sortConfig: {
        orderBy: {
          label: 'System Created Time',
          value: 'sysCreatedTime',
        },
        orderType: 'desc',
      },
      sortConfigLists: [],

      listCount: '',
      isLoading: false,
      selectedRow: null,
      // temp for tenant billing
      downloadUrl: null,
    }
  },
  computed: {
    ...mapState({
      viewDetail: state => state.view.currentViewDetail,
      isViewLoading: state => state.view.isLoading,
      metaInfo: state => state.view.metaInfo,
      showSearch: state => state.search.active,
    }),
    view() {
      let { viewname } = this
      return isEmpty(viewname) ? 'all' : viewname
    },
    moduleDisplayName() {
      return this.metaInfo?.displayName
    },
    parentPath() {
      let { moduleName, modulePath } = this
      return `/app/${modulePath}/${moduleName}/`
    },
    modulePath() {
      if (this.$helpers.isEtisalat()) {
        return this.getEtisalatRouterName().modulePath
      } else {
        return 'ca/modules'
      }
    },
    showLoading() {
      return this.isLoading || this.isViewLoading
    },
    records() {
      return this.$store.state.customModule.customModuleList
    },
    page() {
      let { $route } = this
      let {
        query: { page },
      } = $route || {}
      return page || 1
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
    filteredViewColumns() {
      let { viewColumns, columnConfig } = this
      let { fixedColumns, fixedSelectableColumns } = columnConfig
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
    showNewButton() {
      if (!this.showSearch) {
        if (this.moduleName === 'custom_alert') {
          return false
        }
        if (this.$constants.isCustomModulePermissionsEnabled(this.$org.id)) {
          return this.$hasPermission(`${this.moduleName}:CREATE`)
        }
        return true
      }
      return false
    },
    canShowEdit() {
      if (this.$constants.isCustomModulePermissionsEnabled(this.$org.id)) {
        return this.$hasPermission(`${this.moduleName}:UPDATE`)
      }
      return true
    },
    canShowExport() {
      if (this.$constants.isCustomModulePermissionsEnabled(this.$org.id)) {
        return this.$hasPermission(`${this.moduleName}:EXPORT`)
      }
      return true
    },
    canShowDelete() {
      if (this.$constants.isCustomModulePermissionsEnabled(this.$org.id)) {
        return this.$hasPermission(`${this.moduleName}:DELETE`)
      }
      return true
    },
  },
  watch: {
    moduleName(newVal, oldVal) {
      if (newVal !== oldVal) {
        this.init()
      }
    },
    filters(newVal, oldVal) {
      if (newVal !== oldVal) {
        this.loadCustomModulesList()
        this.loadListCount()
      }
    },
    view(newVal, oldVal) {
      if (newVal !== oldVal) {
        this.loadCustomModulesList()
        this.loadListCount()
      }
    },
    viewDetail(newVal, oldVal) {
      if (!isEqual(newVal, oldVal)) {
        let { sortFields = [] } = newVal || {}

        if (!isEmpty(sortFields)) {
          let name = this.$getProperty(sortFields[0], 'sortField.name', null)
          let isAscending = this.$getProperty(
            sortFields[0],
            'isAscending',
            null
          )
          if (!isEmpty(name) && !isEmpty(isAscending))
            this.sortConfig = {
              orderType: isAscending ? 'asc' : 'desc',
              orderBy: name ? name : '',
            }
        }
      }
    },
    metaInfo: {
      handler(value) {
        let { fields } = value
        if (!isEmpty(fields)) {
          this.constructSortConfig(fields)
        }
      },
      immediate: true,
    },
    page(newVal, oldVal) {
      if (oldVal !== newVal) {
        this.loadCustomModulesList()
        this.loadListCount()
      }
    },
    sortConfig(newVal, oldVal) {
      if (oldVal !== newVal) {
        this.loadCustomModulesList()
      }
    },
    selectAll(val) {
      if (val) {
        this.selectedRecords = []
        this.records.filter(module => {
          this.selectedRecords.push(module.id)
        })
      } else {
        if (this.selectedRecords.length === this.records.length) {
          this.selectedRecords = []
        }
      }
    },
  },
  methods: {
    init() {
      this.$store.dispatch('loadTicketStatus', this.moduleName || '')
      this.loadCustomModulesList()
      this.loadListCount()
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
    constructSortConfig(fields) {
      let configArr = []
      fields.forEach(field => {
        configArr.push(field.name)
      })
      this.$set(this, 'sortConfigLists', configArr)
    },
    updateSort(sorting) {
      let { moduleName, view } = this
      let sortObj = {
        moduleName,
        viewName: view,
        orderBy: sorting.orderBy,
        orderType: sorting.orderType,
        skipDispatch: true,
      }
      this.$store
        .dispatch('view/savesorting', sortObj)
        .then(() => this.loadCustomModulesList())
    },
    selectItems(selectedItem) {
      this.selectedListItems = selectedItem.map(value => value.id)
    },
    async loadListCount() {
      let { moduleName, view, filters, page } = this
      let url = 'v2/module/data/list'
      let params = {
        moduleName,
        viewName: view,
        perPage: 50,
        page,
        fetchCount: true,
        includeParentFilter: true,
        filters: !isEmpty(filters) ? JSON.stringify(filters) : null,
      }
      let { data, error } = await API.get(url, params)

      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.listCount = !isEmpty(data.count) ? data.count : 0
      }
    },
    loadCustomModulesList() {
      this.isLoading = true

      let { moduleName, view, filters, page } = this
      let queryObj = {
        viewname: view,
        page: page,
        filters: filters ? filters : '',
        includeParentFilter: !isEmpty(filters),
        moduleName,
        isNew: true,
      }

      this.$store
        .dispatch('customModule/fetchCustomModuleList', queryObj)
        .catch(() => {})
        .finally(() => {
          this.isLoading = false
        })
    },
    redirectToOverview(row) {
      let { moduleName, view, modulePath } = this
      if (moduleName === 'custom_utilitybills') {
        this.selectedRow = null
        this.selectedRow = row
        this.openEtisalatPopupView()
      } else if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}

        name &&
          this.$router.push({
            name,
            params: {
              viewname: view,
              id: row.id,
            },
          })
      } else {
        this.$router.push({
          path: `/app/${modulePath}/${moduleName}/${view}/${row.id}/summary`,
          query: this.$route.query,
        })
      }
    },
    openEtisalatPopupView() {
      this.showBillSummary = true
    },
    editModule(row) {
      let { moduleName } = this
      let { id } = row
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.EDIT) || {}

        name &&
          this.$router.push({
            name,
            params: {
              id,
            },
          })
      } else {
        let creatioName = this.$helpers.isEtisalat()
          ? this.getEtisalatRouterName().edit
          : 'custommodules-edit'

        this.$router.push({
          name: creatioName,
          params: {
            moduleName,
            id,
          },
        })
      }
    },
    showConfirmDelete(module) {
      let { moduleDisplayName } = this
      let dialogObj = {
        title: `Delete ${moduleDisplayName}`,
        message: `Are you sure you want to delete this ${moduleDisplayName}?`,
        rbDanger: true,
        rbLabel: 'Delete',
      }
      this.$dialog.confirm(dialogObj).then(value => {
        if (value) {
          this.deleteModule(module)
        }
      })
    },
    redirectToFormCreation() {
      let { moduleName } = this
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.CREATE) || {}

        name &&
          this.$router.push({
            name,
          })
      } else {
        let creatioName = this.$helpers.isEtisalat()
          ? this.getEtisalatRouterName().new
          : 'custommodules-new'

        this.$router.push({
          name: creatioName,
          params: {
            moduleName,
          },
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
    async deleteModule(selectedModule) {
      let { records, moduleName, selectedListItems } = this
      let url = '/v2/module/data/delete'
      let params = {
        moduleName,
        ids: !isEmpty(selectedListItems)
          ? selectedListItems
          : [selectedModule.id],
      }
      let { error } = await API.post(url, params)

      if (error) {
        this.$message.error(error.message)
      } else {
        if (selectedListItems) {
          this.loadCustomModulesList()
        } else {
          let selectedModuleIndex = records.findIndex(
            module => module.id === selectedModule.id
          )
          records.splice(selectedModuleIndex, 1)
        }
        this.$message.success('Deleted Successfully')
      }
    },
    getImage(photoId) {
      return `${getBaseURL()}/v2/files/preview/${photoId}`
    },

    // Temp adding here for demo. Copied from summaryDownload in overview
    // Please remove after checking if still required
    downloadTenantBilling(record) {
      this.$message({
        message: 'Downloading...',
        showClose: true,
        duration: 0,
      })

      this.downloadUrl = null

      let additionalInfo = {
        showFooter: false,
        footerStyle: 'p {font-size:12px; margin-left:500px}',
        footerHtml:
          '<p>Page  <span class="pageNumber"></span> / <span class="totalPages"></span></p>',
      }
      let url = `${window.location.protocol}//${window.location.host}/app/pdf/tenantbilling?id=${record.id}`

      API.post(`/v2/integ/pdf/create`, {
        url,
        additionalInfo,
      }).then(({ data, error }) => {
        this.$message.closeAll()

        if (error) {
          let { message = 'Unable to fetch quote download link' } = error
          this.$message.error(message)
        } else {
          let { fileUrl } = data || {}
          this.downloadUrl = fileUrl
        }
      })
    },
  },
}
</script>
<style lang="scss" scoped>
.create-btn {
  margin-top: -10px;
}
.img-container {
  width: 37px;
  height: 37px;
  border: 1px solid #f9f9f9;
  border-radius: 50%;
}
</style>
