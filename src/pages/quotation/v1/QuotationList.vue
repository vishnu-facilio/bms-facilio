<template>
  <CommonListLayout
    :moduleName="moduleName"
    :showViewRearrange="true"
    :showViewEdit="true"
    :visibleViewCount="3"
    :getPageTitle="() => moduleDisplayName"
    :hideSubHeader="isEmpty(moduleRecordList)"
    pathPrefix="/app/tm/quotation/"
    :recordCount="listCount"
    :recordLoading="showLoading"
  >
    <template #header>
      <AdvancedSearchWrapper
        :key="`${moduleName}-search`"
        :moduleName="moduleName"
        :moduleDisplayName="moduleDisplayName"
      ></AdvancedSearchWrapper>

      <template v-if="!showSearch">
        <div v-if="$hasPermission(`${moduleName}:CREATE`)">
          <button class="fc-create-btn" @click="redirectToFormCreation()">
            New Quote
          </button>
        </div>
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
      </template>
    </template>
    <template #content>
      <div class="fc-card-popup-list-view">
        <div
          class="fc-list-view p10 pT0  fc-list-table-container fc-table-td-height fc-table-viewchooser pB100"
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
              src="svgs/emptystate/quotation"
              iconClass="icon text-center icon-xxxxlg"
            ></inline-svg>
            <div class="q-item-label nowo-label">
              {{ $t('quotation.list.no_data') }}
            </div>
          </div>
          <div v-else>
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
              <div class="action-btn-slide btn-block d-flex">
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
                <CustomButton
                  :key="`${moduleName}_${viewname}_${POSITION.LIST_BAR}`"
                  :moduleName="moduleName"
                  :position="POSITION.LIST_BAR"
                  class="custom-button mL10"
                  @onSuccess="onCustomButtonSuccess"
                  @onError="() => {}"
                  :selectedRecords="selectedRecordsObj"
                ></CustomButton>
              </div>
            </div>
            <el-table
              :data="moduleRecordList"
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
                  <div class="fc-id">{{ '#' + data.row.parentId }}</div>
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
                    @click="openRecordSummary(item.row.id)"
                    class="table-subheading"
                  >
                    <div class="d-flex">
                      <div class="self-center mL5">
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
                  $getProperty(field, 'field.dataTypeEnum') === 'DECIMAL'
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
                  <div
                    v-if="
                      $getProperty(field, 'field.displayType') === 'SIGNATURE'
                    "
                  >
                    <SignatureField
                      :field="(field || {}).field"
                      :record="data.row"
                      :isV3Api="isV3Api"
                    />
                  </div>
                  <div
                    v-else-if="
                      $getProperty(field, 'field.displayType') ===
                        'MULTI_CURRENCY'
                    "
                    placement="right"
                  >
                    <CurrencyPopOver
                      :field="{
                        ...(field || {}),
                        displayValue: data.row[field.name],
                      }"
                      :details="data.row"
                    />
                  </div>
                  <div
                    v-else-if="
                      $getProperty(field, 'field.displayType') === 'FILE'
                    "
                  >
                    <div
                      v-if="
                        !$validation.isEmpty(data.row[`${field.name}FileName`])
                      "
                      @click="openAttachment(field, data.row)"
                      class="d-flex file-column"
                    >
                      <a class="truncate-text">
                        {{ getColumnDisplayValue(field, data.row) }}
                      </a>
                    </div>
                    <div v-else>---</div>
                  </div>
                  <div
                    v-else
                    class="table-subheading"
                    :class="{
                      'text-right':
                        $getProperty(field, 'field.dataTypeEnum') === 'DECIMAL',
                    }"
                  >
                    {{ getColumnDisplayValue(field, data.row) || '---' }}
                  </div>
                </template>
              </el-table-column>
              <el-table-column min-width="200" align="right">
                <template v-slot="data">
                  <CustomButton
                    :moduleName="moduleName"
                    :record="data.row"
                    :position="POSITION.LIST_ITEM"
                    class="custom-button"
                    @onSuccess="loadRecords"
                    @onError="() => {}"
                  ></CustomButton>
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
                          showEditDelete(data.row)
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
                          showEditDelete(data.row)
                      "
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
      </div>
      <preview-file
        :visibility.sync="showPreview"
        v-if="showPreview && selectedFile"
        :previewFile="selectedFile"
        :files="[selectedFile]"
      ></preview-file>
    </template>
    <portal to="view-manager-link">
      <router-link
        tag="div"
        :to="`/app/tm/quote/viewmanager`"
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
import Pagination from 'src/newapp/components/ListPagination'
import FExportSettings from '@/FExportSettings'
import Sort from 'newapp/components/Sort'
import SignatureField from '@/list/SignatureColumn'
import PreviewFile from '@/PreviewFile'
import CustomButton from '@/custombutton/CustomButton'
import { POSITION_TYPE } from 'pages/setup/custombutton/CustomButtonUtil'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import { getColumnConfig } from 'src/newapp/components/column-customization/columnConfigUtil.js'
import AdvancedSearchWrapper from 'newapp/components/search/AdvancedSearchWrapper'
import CurrencyPopOver from 'src/pages/setup/organizationSetting/currency/CurrencyPopOver.vue'

export default {
  name: 'QuotationList',
  props: ['viewname'],
  mixins: [ViewMixinHelper],
  components: {
    ColumnCustomization,
    Spinner,
    CommonListLayout,
    Pagination,
    FExportSettings,
    Sort,
    SignatureField,
    AdvancedSearchWrapper,
    PreviewFile,
    CustomButton,
    CurrencyPopOver,
  },
  title() {
    'Quote'
  },
  created() {
    this.init()
    this.$store.dispatch('getActiveCurrencyList')
  },
  data() {
    return {
      isEmpty,
      sortConfig: {
        orderBy: 'id',
        orderType: 'desc',
      },
      sortConfigLists: [
        'subject',
        'description',
        'billDate',
        'expiryDate',
        'subTotal',
        'totalTaxAmount',
        'discountAmount',
        'shippingCharges',
        'adjustmentsCost',
        'miscellaneousCharges',
        'totalCost',
      ],
      listCount: '',
      loading: true,
      selectedRecords: [],
      selectedRecordsObj: [],
      showColumnSettings: false,
      tableLoading: false,
      searchQuery: null,
      moduleRecordList: [],
      selectedFile: null,
      showPreview: false,
      POSITION: POSITION_TYPE,
    }
  },
  computed: {
    ...mapState({
      viewDetail: state => state.view.currentViewDetail,
      metaInfo: state => state.view.metaInfo,
      showSearch: state => state.search.active,
      account: state => state.account,
      currencyList: state => state.activeCurrencies,
    }),
    ...mapGetters(['getApprovalStatus', 'isStatusLocked', 'getTicketStatus']),
    moduleName() {
      return 'quote'
    },
    nameColumn() {
      let { viewColumns } = this
      if (!isEmpty(viewColumns)) {
        return viewColumns.find(column => column.name === 'subject')
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
      let { params } = this.$route || {}
      let { viewname } = params || {}

      return viewname
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
    isV3Api() {
      return true
    },
    multiCurrency() {
      let { displaySymbol, currencyCode, multiCurrencyEnabled } =
        this.account.data.currencyInfo || {}
      return { displaySymbol, currencyCode, multiCurrencyEnabled }
    },
  },
  watch: {
    moduleName(newVal, oldVal) {
      if (newVal !== oldVal) {
        this.init()
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
        }
      },
      immediate: true,
    },

    appliedFilters() {
      this.loadRecords()
    },
    page(newVal, oldVal) {
      if (oldVal !== newVal && !this.loading) {
        this.loadRecords()
      }
    },
    searchQuery() {
      this.loadRecords()
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
              'id'
            ),
          }
        }
      }
    },
  },
  methods: {
    init() {
      this.loadViewMetaInfo()
      this.$store.dispatch('loadTicketStatus', this.moduleName)
      this.selectedRecords = []
      this.selectedRecordsObj = []
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
        .then(() => this.loadRecords())
    },
    onCustomButtonSuccess() {
      this.loadRecords()
      this.selectedRecords = []
      this.selectedRecordsObj = []
    },
    async loadRecords(forceFetch) {
      let params = {
        withCount: true,
        viewName: this.currentView,
        includeParentFilter: this.includeParentFilter,
      }
      if (!isEmpty(this.page)) {
        params['page'] = this.page
        params['perPage'] = 50
      }
      if (!isEmpty(this.appliedFilters)) {
        params['filters'] = JSON.stringify(this.appliedFilters)
      }
      if (!isEmpty(this.searchQuery)) {
        params['search'] = this.searchQuery
      }
      this.loading = true

      let { list, meta, error } = await API.fetchAll(`quote`, params, {
        force: forceFetch,
      })

      if (error) {
        let { message = 'Error Occured while fetching Quote list' } = error
        this.$message.error(message)
      } else {
        this.listCount = this.$getProperty(meta, 'pagination.totalCount', null)
        this.moduleRecordList = list || []
      }
      this.loading = false
    },
    async deleteRecords(idList) {
      let value = await this.$dialog.confirm({
        title: this.$t(`quotation.list.delete`),
        message: this.$t(`quotation.list.delete_confirmation`),
        rbDanger: true,
        rbLabel: this.$t('common._common.delete'),
      })

      if (value) {
        let { moduleName } = this
        this.loading = true
        // Setting loading as false in loadRecords

        let { error } = await API.deleteRecord(moduleName, idList)
        if (error) {
          let { message } = error
          this.$message.error(message || 'Error Occured while deleting quote')
          this.loading = false
        } else {
          this.$message.success(this.$t(`quotation.list.delete_success`))
          this.loadRecords(true)
          this.selectedRecords = []
          this.selectedRecordsObj = []
        }
      }
    },
    openRecordSummary(id) {
      let { currentView, moduleName } = this
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}
        name &&
          this.$router.push({
            name,
            params: {
              viewname: currentView,
              id,
            },
            query: this.$route.query,
          })
      } else {
        this.$router.push({
          path: `/app/tm/quotation/${currentView}/${id}/overview`,
          query: this.$route.query,
        })
      }
    },
    openAttachment(field, record) {
      this.selectedFile = {
        contentType: record[`${field.name}ContentType`],
        fileName: record[`${field.name}FileName`],
        downloadUrl: record[`${field.name}DownloadUrl`],
        previewUrl: record[`${field.name}Url`],
      }
      this.showPreview = true
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
        this.$router.push({ path: `/app/tm/quotation/new` })
      }
    },
    selectRecords(selectedRecordsObj) {
      this.selectedRecordsObj = selectedRecordsObj
      this.selectedRecords = selectedRecordsObj.map(value => value.id)
    },
    editRecord(data) {
      let { moduleName } = this
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.EDIT) || {}
        name &&
          this.$router.push({
            name,
            params: {
              id: data.id,
            },
          })
      } else {
        this.$router.push({
          path: `/app/tm/quotation/edit/${data.id}`,
        })
      }
    },
    hasModuleState(record) {
      let hasState = this.$getProperty(record, 'moduleState.id')
      return hasState ? true : false
    },
    showEditDelete(record) {
      return this.hasModuleState(record)
        ? !this.isRecordLocked(record) && !this.isRequestedState(record)
        : true
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
  },
}
</script>
<style scoped></style>
