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
            <router-link
              tag="div"
              class="cm-sidebar-list-item label-txt-black main-field-column"
              :to="redirectToOverview(record.id)"
            >
              <div
                class="f14 truncate-text"
                :title="record[mainFieldName] || '---'"
                v-tippy="{
                  placement: 'top',
                  animation: 'shift-away',
                  arrow: true,
                }"
              >
                {{ record[mainFieldName] || '---' }}
              </div>
            </router-link>
          </template>
        </SummarySidebar>
      </div>
      <div class="flex-1">
        <router-view :viewname="viewname"></router-view>
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
    :recordCount="recordCount"
    :recordLoading="showLoading"
    :key="`${moduleName}-list-layout`"
    class="custom-module-list-layout"
  >
    <template #header>
      <AdvancedSearchWrapper
        :filters="filters"
        :moduleName="moduleName"
        :moduleDisplayName="moduleDisplayName"
      ></AdvancedSearchWrapper>
      <template v-if="canShowVisualSwitch">
        <visual-type
          @onSwitchVisualize="val => (canShowListView = val)"
        ></visual-type>
      </template>
      <CustomButton
        :moduleName="moduleName"
        :position="POSITION.LIST_TOP"
        class="custom-button"
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
        <span class="separator" v-if="recordCount > 0">|</span>

        <el-tooltip
          effect="dark"
          :content="$t('common._common.sort')"
          :open-delay="500"
          placement="top"
          :tabindex="-1"
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
          :open-delay="500"
          placement="top"
          :tabindex="-1"
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
        <div
          class="cm-list-container"
          :class="filters ? 'fc-list-table-search-scroll' : ''"
        >
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
              :moduleName="moduleName"
              :position="POSITION.LIST_BAR"
              class="custom-button"
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
              <router-link
                class="d-flex fw5 label-txt-black ellipsis main-field-column"
                :to="redirectToOverview(record.id)"
              >
                <div v-if="record[photoFieldName] > 0">
                  <img
                    :src="record.getImage(photoFieldName)"
                    class="img-container"
                  />
                </div>
                <el-tooltip
                  effect="dark"
                  :content="record.name || '---'"
                  placement="top-end"
                  :open-delay="600"
                >
                  <div class="self-center width200px">
                    <span class="list-main-field">{{
                      record.name || '---'
                    }}</span>
                  </div>
                </el-tooltip>
              </router-link>
            </template>
            <template #[slotList[1].name]="{record}">
              <div class="d-flex text-center">
                <i
                  v-if="hasPermission('EDIT,UPDATE') && record.canEdit()"
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
          </CommonList>
        </div>
      </template>
      <column-customization
        :visible.sync="showColumnSettings"
        :moduleName="moduleName"
        :viewName="viewname"
      ></column-customization>
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
import { mapState } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import ColumnCustomization from '@/ColumnCustomization'
import Spinner from '@/Spinner'
import CommonListLayout from 'newapp/list/CommonLayout'
import Pagination from 'src/newapp/components/ListPagination'
import FExportSettings from '@/FExportSettings'
import Sort from 'newapp/components/Sort'
import AdvancedSearchWrapper from 'newapp/components/search/AdvancedSearchWrapper.vue'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
  getApp,
} from '@facilio/router'
import SummarySidebar from 'newapp/components/SummarySideBar'
import CommonList from 'newapp/list/CommonList'
import isEqual from 'lodash/isEqual'
import CustomButton from '@/custombutton/CustomButton'
import { POSITION_TYPE } from 'pages/setup/custombutton/CustomButtonUtil'
import { CustomModuleData } from './CustomModuleData'
import { findRouterForModuleInApp } from 'newapp/viewmanager/routeUtil'

export default {
  name: 'CustomModuleList',
  props: ['moduleName', 'viewname'],
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
  },
  created() {
    this.init()
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

      recordCount: null,
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
      isViewDetailLoading: state => state.view.detailLoading,
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
      ]
    },
    moduleDisplayName() {
      return this.metaInfo?.displayName
    },
    parentPath() {
      let { moduleName, modulePath } = this
      return `/app/${modulePath}/${moduleName}/`
    },
    modulePath() {
      return 'ca/modules'
    },
    showLoading() {
      return this.isLoading || this.isViewLoading || this.isViewDetailLoading
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
    isPermissionsRequired() {
      let currentOrgId = this.$getProperty(this, '$org.id')
      return this.$constants.isCustomModulePermissionsEnabled(currentOrgId)
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
    mainFieldName() {
      return 'name'
    },
    photoFieldName() {
      return 'photoId'
    },
    modelDataClass() {
      return CustomModuleData
    },
    viewDetailFields() {
      let { viewDetail } = this || {}
      let { fields } = viewDetail || {}
      return fields
    },
  },
  watch: {
    viewname: {
      handler(newVal, oldVal) {
        if (!isEmpty(newVal) && !isEqual(newVal, oldVal)) {
          this.onViewChange()
        }
      },
      immediate: true,
    },
    metaInfo: {
      handler(value) {
        let { fields } = value || {}
        this.sortConfigLists = (fields || []).map(fld => fld.name)
      },
      immediate: true,
    },
    moduleName(newVal, oldVal) {
      if (!isEqual(newVal, oldVal)) {
        this.init()
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
    page: { handler: 'onPageChange' },
    sortConfig(newVal, oldVal) {
      if (!isEqual(newVal, oldVal)) {
        this.loadRecords()
      }
    },
    viewDetailFields: {
      handler(newVal, oldVal) {
        if (!isEqual(newVal, oldVal)) {
          if (!this.isLoading) this.loadRecords(true)
        }
      },
      deep: true,
    },
  },
  methods: {
    onPageChange() {
      this.loadRecords()
    },
    onViewChange() {
      this.loadRecords()
      this.getViewDetail()
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
    init() {
      this.$store
        .dispatch('loadTicketStatus', this.moduleName || '')
        .catch(() => {})
      this.$store
        .dispatch('view/loadModuleMeta', this.moduleName)
        .catch(() => {})
    },
    getViewDetail() {
      let { viewname, moduleName } = this
      this.$store
        .dispatch('view/loadViewDetail', {
          viewName: viewname,
          moduleName: moduleName,
        })
        .catch(() => {})
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
        this.isLoading = true
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

        this.records = await this.modelDataClass.fetchAll(params)
        this.recordCount = this.modelDataClass.recordListCount
      } catch (errorMsg) {
        this.recordCount = null
        this.records = null
        this.$message.error(errorMsg)
      } finally {
        this.isLoading = false
      }
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
          await this.modelDataClass.delete(moduleName, idList)
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
      let { isPermissionsRequired } = this
      if (isPermissionsRequired || isWebTabsEnabled()) {
        return this.$hasPermission(`${this.moduleName}:${action}`)
      }
      return true
    },
    refresh() {
      this.loadRecords()
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
          name: 'custommodules-list',
          params: { viewname },
          query: this.$route.query,
        })
      }
    },
    redirectToOverview(id) {
      let { moduleName, viewname } = this

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}
        let route = {
          name,
          params: {
            viewname,
            id,
          },
          query: this.$route.query,
        }

        return name && route
      } else {
        return {
          name: 'custommodules-summary',
          params: { moduleName, viewname, id },
          query: this.$route.query,
        }
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
          name: 'custommodules-edit',
          params: {
            moduleName,
            id,
          },
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
          name: 'custommodules-new',
          params: {
            moduleName,
          },
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
<style lang="scss">
.cm-side-bar-container {
  flex: 0 0 320px;
  max-width: 320px;
  background: white;
  position: relative;
  height: 100vh;
  border-right: 1px solid #ececec;
  border-left: 1px solid #ececec;
}
.cm-sidebar-header {
  padding: 20px 15px;
  border-bottom: 1px solid #f2f2f2;
}
.cm-sidebar-list-item {
  text-overflow: ellipsis;
  white-space: nowrap;
  overflow: hidden;
  padding: 20px;
  font-size: 12px;
  cursor: pointer;
}
.custom-module-list-layout {
  .create-btn {
    margin-top: -10px;
  }
  .cm-empty-state-container {
    background-color: #fff;
    flex-grow: 1;
    display: flex;
    flex-direction: column;
    justify-content: center;
    margin: 0px 10px 10px;
  }
  .cm-list-container {
    border-width: 0px !important;
    border-style: solid;
    padding: 0px 10px 10px;
    height: calc(100vh - 155px);
  }
  .img-container {
    width: 37px;
    height: 37px;
    border: 1px solid #f9f9f9;
    border-radius: 50%;
  }
  .column-customization-icon {
    position: absolute;
    right: 11px;
    display: block;
    width: 45px;
    height: 50px;
    cursor: pointer;
    text-align: center;
    background-color: #ffffff;
    border-left: 1px solid #f2f5f6;
    z-index: 20;
    .icon {
      top: 35%;
      right: 29%;
    }
    margin-top: 2px;
  }
}
.cm-list-container {
  .el-table td {
    padding: 10px 20px;
  }
  .el-table th.is-leaf {
    padding: 15px 20px;
  }
  .el-table th > .cell {
    font-size: 11px;
    font-weight: 700;
    text-transform: uppercase;
    letter-spacing: 0.5px;
    color: #333;
    white-space: nowrap;
    padding-left: 0;
    padding-right: 0;
  }
  .hover-actions {
    visibility: hidden;
  }
  .el-table__body tr.hover-row > td .hover-actions {
    visibility: visible;
  }
}
</style>
