<template>
  <div class="height100" v-if="isSummaryOpen">
    <div v-if="showLoading" class="flex-middle fc-empty-white">
      <spinner :show="showLoading" size="80"></spinner>
    </div>
    <div v-else class="flex flex-row">
      <div class="cm-side-bar-container">
        <SummarySidebar
          :list="records"
          :isLoading.sync="isLoading"
          :activeRecordId="selectedRecordId"
          :total="recordCount"
          :currentCount="(records || []).length"
        >
          <template #title>
            <el-row class="cm-sidebar-header">
              <el-col :span="2">
                <span @click="openList()">
                  <inline-svg
                    src="svgs/arrow"
                    class="rotate-90 pointer"
                    iconClass="icon icon-sm"
                  ></inline-svg>
                </span>
              </el-col>

              <el-col :span="22">
                <div class="bold">{{ currentViewDetail }}</div>
              </el-col>
            </el-row>
          </template>

          <template v-slot="{ record }">
            <div
              class="cm-sidebar-list-item"
              @click="redirectToOverview(record.id)"
            >
              <div class="text-fc-grey">#{{ record['id'] }}</div>
              <div
                class="f14 truncate-text bold mT5"
                :title="record[mainField.name] || '---'"
                v-tippy="{
                  placement: 'top',
                  animation: 'shift-away',
                  arrow: true,
                }"
              >
                {{ record[mainField.name] || '---' }}
              </div>
            </div>
          </template>
        </SummarySidebar>
      </div>
      <div class="flex-1">
        <router-view></router-view>
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
    :pathPrefix="parentPath"
    :hideSubHeader="isEmpty(records)"
    :key="`${moduleName}-list-layout`"
    :recordCount="recordCount"
    :recordLoading="showLoading"
    class="custom-module-list-layout"
  >
    <template #header>
      <AdvancedSearchWrapper
        :key="`${moduleName}-search`"
        :moduleName="moduleName"
        :moduleDisplayName="moduleDisplayName"
      ></AdvancedSearchWrapper>

      <CustomButton
        :moduleName="moduleName"
        :position="POSITION.LIST_TOP"
        class="custom-button "
        @onSuccess="onCustomButtonSuccess"
        @onError="() => {}"
      ></CustomButton>
      <template v-if="hasPermission('CREATE')">
        <button class="fc-create-btn " @click="redirectToFormCreation()">
          {{ $t('custommodules.list.new') }}
          {{ moduleDisplayName ? moduleDisplayName : '' }}
        </button>
      </template>
    </template>
    <template #sub-header-actions>
      <template v-if="!isEmpty(records)">
        <pagination
          :total="recordCount"
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
            :key="`${moduleName}-sort`"
            :config="sortConfig"
            :sortList="sortConfigLists"
            @onchange="updateSort"
          ></sort>
        </el-tooltip>
        <span v-if="hasPermission('EXPORT')" class="separator">|</span>

        <el-tooltip
          v-if="hasPermission('EXPORT')"
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
      </template>
    </template>
    <template #content>
      <Spinner v-if="showLoading" class="mT40" :show="showLoading"></Spinner>

      <div
        v-if="$validation.isEmpty(records) && !showLoading"
        class="cm-empty-state-container"
      >
        <img
          class="mT20 self-center"
          src="~statics/noData-light.png"
          width="100"
          height="100"
        />
        <div class="mT10 label-txt-black f14 self-center">
          {{ emptyStateText }}
        </div>
      </div>

      <template v-if="!showLoading && !$validation.isEmpty(records)">
        <div class="cm-list-container">
          <div
            class="column-customization-icon"
            @click="showColumnSettings = true"
          >
            <img
              src="~assets/column-setting.svg"
              class="text-center position-absolute icon"
            />
          </div>
          <div
            class="pull-left table-header-actions"
            v-if="!$validation.isEmpty(selectedListItemsIds)"
          >
            <div class="action-btn-slide btn-block">
              <button
                class="btn btn--tertiary pointer"
                @click="deleteRecords(selectedListItemsIds)"
              >
                {{ $t('custommodules.list.delete') }}
              </button>
            </div>
            <CustomButton
              :key="`${moduleName}_${viewname}_${POSITION.LIST_BAR}`"
              :modelDataClass="modelDataClass"
              :moduleName="moduleName"
              :position="POSITION.LIST_BAR"
              class="custom-button margin-left-80"
              @onSuccess="onCustomButtonSuccess"
              @onError="() => {}"
              :selectedRecords="selectedListItemsObj"
            ></CustomButton>
          </div>
          <CommonList
            :viewDetail="viewDetail"
            :records="records"
            :moduleName="moduleName"
            :redirectToOverview="redirectToOverview"
            @selection-change="selectItems"
            :slotList="slotList"
            canShowCustomButton="true"
            :refreshList="onCustomButtonSuccess"
          >
            <template #[slotList[0].criteria]="{record}">
              <div class="d-flex" @click="redirectToOverview(record.id)">
                <tenant-avatar
                  :name="false"
                  size="md"
                  :tenant="record"
                ></tenant-avatar>
                <el-tooltip
                  effect="dark"
                  :content="record.name || '---'"
                  placement="top"
                  :open-delay="600"
                >
                  <div class="self-center mL5  main-field-column">
                    {{ record.name || '---' }}
                  </div>
                </el-tooltip>
              </div>
            </template>
            <template #[slotList[1].name]="{record}">
              <div class="d-flex text-center">
                <i
                  v-if="hasPermission('EDIT')"
                  class="el-icon-edit pointer edit-icon-color visibility-hide-actions mL10"
                  data-arrow="true"
                  :title="$t('common._common.edit')"
                  @click="editModule(record)"
                  v-tippy
                ></i>
                <i
                  v-if="hasPermission('DELETE')"
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
        </div>
        <column-customization
          :visible.sync="showColumnSettings"
          :moduleName="moduleName"
          :viewName="viewname"
        ></column-customization>
      </template>
    </template>
    <portal to="view-manager-link">
      <div class="view-manager-btn" @click="redirectToViewManager">
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
import CommonListLayout from 'newapp/list/CommonLayout'
import Pagination from 'src/newapp/components/ListPagination'
import FExportSettings from '@/FExportSettings'
import Sort from 'newapp/components/Sort'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import SummarySidebar from 'newapp/components/SummarySideBar'
import CommonList from 'newapp/list/CommonList'
import isEqual from 'lodash/isEqual'
import CustomButton from '@/custombutton/CustomButton'
import { POSITION_TYPE } from 'pages/setup/custombutton/CustomButtonUtil'
import { CustomModuleData } from 'pages/custom-module/CustomModuleData'
import TenantAvatar from '@/avatar/Tenant'
import CustomModuleList from 'src/pages/custom-module/CustomModuleList'
import AdvancedSearchWrapper from 'newapp/components/search/AdvancedSearchWrapper'

export default {
  name: 'tenant',
  props: ['moduleName', 'viewname'],
  mixins: [ViewMixinHelper],
  extends: { CustomModuleList },
  components: {
    ColumnCustomization,
    Spinner,
    CommonListLayout,
    Pagination,
    FExportSettings,
    Sort,
    AdvancedSearchWrapper,
    SummarySidebar,
    CommonList,
    CustomButton,
    TenantAvatar,
  },
  data() {
    return {
      selectedListItemsIds: [],
      selectedListItemsObj: [],
      showColumnSettings: false,
      sortConfig: {
        orderBy: {
          label: 'System Created Time',
          value: 'sysCreatedTime',
        },
        orderType: 'desc',
      },
      sortConfigLists: [],
      recordCount: '',
      isLoading: false,
      records: null,
      POSITION: POSITION_TYPE,
      canShowListView: true,
      isEmpty,
    }
  },
  computed: {
    ...mapState({
      viewDetail: state => state.view.currentViewDetail,
      isViewLoading: state => state.view.isLoading,
      metaInfo: state => state.view.metaInfo,
    }),

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
    moduleDisplayName() {
      return this.metaInfo?.displayName
    },
    parentPath() {
      return `/app/tm/tenants/`
    },
    showLoading() {
      return this.isLoading || this.isViewLoading
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

    isV3Api() {
      return true
    },
    selectedRecordId() {
      let id = this.$getProperty(this, '$route.params.id')
      if (id) {
        return parseInt(id)
      }
      return -1
    },
    isSummaryOpen() {
      return !isEmpty(this.selectedRecordId)
    },
    currentViewDetail() {
      let { viewDetail, filters } = this
      if (filters) {
        return this.$t('custommodules.list.filtered_view')
      } else {
        return viewDetail.displayName
      }
    },
    emptyStateText() {
      let { moduleDisplayName, moduleName } = this
      moduleDisplayName = moduleDisplayName ? moduleDisplayName : moduleName
      return `${this.$t(
        'custommodules.list.no'
      )} ${moduleDisplayName} ${this.$t('custommodules.list.available')}.`
    },
    mainField() {
      let { viewColumns } = this
      if (!isEmpty(viewColumns)) {
        return viewColumns.find(column => column.name === 'name')
      }
      return {}
    },
  },
  watch: {
    viewname: {
      handler(newVal, oldVal) {
        if (!isEmpty(newVal) && !isEqual(newVal, oldVal)) {
          this.loadRecords()
          this.getViewDetail()
        }
      },
      immediate: true,
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
    moduleName(newVal, oldVal) {
      if (!isEqual(newVal, oldVal)) {
        this.loadRecords()
      }
    },
    filters(newVal, oldVal) {
      if (!isEqual(newVal, oldVal)) {
        this.loadRecords()
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
    page(newVal, oldVal) {
      if (!isEqual(newVal, oldVal)) {
        this.loadRecords()
      }
    },
    sortConfig(newVal, oldVal) {
      if (!isEqual(newVal, oldVal)) {
        this.loadRecords()
      }
    },
  },
  methods: {
    getViewDetail() {
      let { viewname, moduleName } = this
      this.$store.dispatch('view/loadViewDetail', {
        viewName: viewname,
        moduleName: moduleName,
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
      let { moduleName, viewname } = this
      let sortObj = {
        moduleName,
        viewName: viewname,
        orderBy: sorting.orderBy,
        orderType: sorting.orderType,
        skipDispatch: true,
      }
      this.$store
        .dispatch('view/savesorting', sortObj)
        .then(() => this.loadRecords(true))
    },
    selectItems(selectedItem) {
      this.selectedListItemsObj = selectedItem
      this.selectedListItemsIds = selectedItem.map(value => value.id)
    },
    async loadRecords(force = false) {
      try {
        let { moduleName, viewname, filters, page } = this
        let params = {
          moduleName,
          viewname,
          filters,
          page,
          force,
          withCount: true,
          fetchOnlyViewGroupColumn: false,
        }

        this.isLoading = true
        this.records = await CustomModuleData.fetchAll(params)
        this.recordCount = CustomModuleData.recordListCount
      } catch (errorMsg) {
        this.$message.error(errorMsg)
      }

      this.isLoading = false
    },
    async deleteRecords(idList) {
      let { moduleDisplayName, moduleName } = this
      let value = await this.$dialog.confirm({
        title: `${this.$t('custommodules.list.delete')} ${moduleDisplayName}`,
        message: `${this.$t(
          'custommodules.list.delete_confirmation'
        )} ${moduleDisplayName}?`,
        rbDanger: true,
        rbLabel: this.$t('custommodules.list.delete'),
      })

      if (value) {
        this.isLoading = true

        try {
          await CustomModuleData.delete(moduleName, idList)
          this.$message.success(
            `${moduleDisplayName} ${this.$t(
              'custommodules.list.delete_success'
            )}`
          )
          await this.loadRecords()
          this.selectedListItemsIds = []
          this.selectedListItemsObj = []
        } catch (errorMsg) {
          this.$message.error(errorMsg)
        }

        this.isLoading = false
      }
    },
    hasPermission(action) {
      return this.$hasPermission(`${this.moduleName}:${action}`)
    },
    // route redirection handling
    openList() {
      let { viewname } = this

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(this.moduleName, pageTypes.LIST) || {}
        name &&
          this.$router.push({
            name,
            params: {
              viewname,
            },
            query: this.$route.query,
          })
      } else {
        this.$router.push({
          name: 'tenant-list',
          params: { viewname },
          query: this.$route.query,
        })
      }
    },
    redirectToOverview(id) {
      let { moduleName, viewname } = this
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}
        name &&
          this.$router.push({
            name,
            params: {
              viewname,
              id,
            },
            query: this.$route.query,
          })
      } else {
        this.$router.push({
          path: `/app/tm/tenants/${viewname}/${id}/overview`,
          query: this.$route.query,
        })
      }
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
        this.$router.push({
          name: 'tenant-edit',
          params: {
            id,
          },
        })
      }
    },
    redirectToViewManager() {
      let { moduleName } = this
      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForModule(moduleName, pageTypes.VIEW_MANAGER) || {}
        name &&
          this.$router.push({
            name,
            params: { moduleName },
          })
      } else {
        this.$router.push({
          name: 'tm-viewmanager',
          params: { moduleName },
        })
      }
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
        this.$router.push({
          name: 'tenant-new',
        })
      }
    },
    onCustomButtonSuccess() {
      this.loadRecords()
      this.selectedListItemsIds = []
      this.selectedListItemsObj = []
    },
  },
}
</script>
<style scoped></style>
