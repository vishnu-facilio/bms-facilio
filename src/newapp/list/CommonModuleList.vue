<template>
  <div class="height100" v-if="isSummaryOpen">
    <div v-if="showLoading" class="flex-middle fc-empty-white">
      <Spinner :show="showLoading" size="80"></Spinner>
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
    :key="`${moduleName}-list-layout`"
    :hideSubHeader="canHideSubHeader"
    :recordCount="recordCount"
    :recordLoading="showLoading"
    class="custom-module-list-layout"
  >
    <template #header>
      <template v-if="!isEmpty(viewname)">
        <AdvancedSearchWrapper
          v-if="!canHideFilter"
          :filters="filters"
          :moduleName="moduleName"
          :moduleDisplayName="moduleDisplayName"
        ></AdvancedSearchWrapper>
        <visual-type
          v-if="canShowVisualSwitch"
          @onSwitchVisualize="val => (canShowListView = val)"
        ></visual-type>

        <CustomButton
          :key="`${moduleName}_${viewname}_${POSITION.LIST_TOP}`"
          :moduleName="moduleName"
          :position="POSITION.LIST_TOP"
          :modelDataClass="modelDataClass"
          class="custom-button"
          @onSuccess="onCustomButtonSuccess"
          @onError="() => {}"
        ></CustomButton>
      </template>
      <template v-if="hasPermission('CREATE')">
        <button class="fc-create-btn" @click="redirectToFormCreation()">
          {{ createBtnText }}
        </button>
      </template>
    </template>

    <template v-if="canShowCalendarHeader" #sub-header>
      <CalendarDateWrapper v-if="!showListView" />
    </template>

    <template #sub-header-actions>
      <template v-if="!isEmpty(records) && showListView">
        <pagination
          :total="recordCount"
          :currentPageCount="currentPageCount"
          :perPage="perPage"
          :skipTotalCount="true"
          class="pL15 fc-black-small-txt-12"
        ></pagination>
        <span class="separator">|</span>

        <el-tooltip
          effect="dark"
          :content="$t('common._common.sort')"
          :open-delay="500"
          placement="top"
          :tabindex="-1"
        >
          <Sort
            :key="`${moduleName}-sort`"
            :moduleName="moduleName"
            @onSortChange="updateSort"
          ></Sort>
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
      <div v-else-if="isEmpty(viewname)" class="cm-view-empty-state-container">
        <inline-svg
          src="svgs/no-configuration"
          class="d-flex module-view-empty-state"
          iconClass="icon"
        ></inline-svg>
        <div class="mB20 label-txt-black f14 self-center">
          {{ $t('viewsmanager.list.no_view_config') }}
        </div>
        <el-button
          type="primary"
          class="add-view-btn"
          @click="openViewCreation"
        >
          <span class="btn-label">{{ $t('viewsmanager.list.add_view') }}</span>
        </el-button>
      </div>
      <template v-else-if="showListView">
        <div v-if="isEmpty(records)" class="cm-empty-state-container">
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
        <div v-else class="cm-list-container">
          <div
            v-if="showListView"
            class="column-customization-icon"
            :disabled="!isColumnCustomizable"
            @click="toShowColumnSettings"
          >
            <el-tooltip
              :disabled="isColumnCustomizable"
              placement="top"
              :content="$t('common._common.you_dont_have_permission')"
            >
              <inline-svg
                src="column-setting"
                class="text-center position-absolute icon"
              />
            </el-tooltip>
          </div>

          <div
            class="pull-left table-header-actions"
            v-if="!isEmpty(selectedListItemsIds)"
          >
            <div
              class="action-btn-slide btn-block"
              v-if="hasPermission('DELETE')"
            >
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
              :modelDataClass="modelDataClass"
              class="custom-button"
              @onSuccess="onCustomButtonSuccess"
              @onError="() => {}"
              :selectedRecords="selectedListItemsObj"
            ></CustomButton>
          </div>
          <CommonList
            :key="`viewname-${viewname}`"
            :viewDetail="viewDetail"
            :records="records"
            :moduleName="moduleName"
            :redirectToOverview="redirectToOverview"
            @selection-change="selectItems"
            :slotList="slotList"
            canShowCustomButton="true"
            :refreshList="onCustomButtonSuccess"
            :modelDataClass="modelDataClass"
          >
            <template #[slotList[0].name]="{record}">
              <div class="d-flex">
                <div class="fc-id">{{ '#' + record[slotList[0].name] }}</div>
              </div>
            </template>
            <template #[slotList[1].criteria]="{record}">
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
            <template #[slotList[2].name]="{record}">
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
      <CalendarView
        v-else-if="!showListView"
        ref="calendar"
        :moduleName="moduleName"
        :record="records"
        :viewDetail="viewDetail"
        :viewname="viewname"
        :filters="filters"
      ></CalendarView>
      <ColumnCustomization
        :visible.sync="showColumnSettings"
        :moduleName="moduleName"
        :viewName="viewname"
      ></ColumnCustomization>
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
import Pagination from 'src/newapp/components/ListPagination'
import isEqual from 'lodash/isEqual'
import debounce from 'lodash/debounce'
import Sort from 'src/newapp/components/NewSort.vue'
import ColumnCustomization from '@/ColumnCustomization'
import Spinner from '@/Spinner'
import CommonListLayout from 'newapp/list/CommonLayout'
import FExportSettings from '@/FExportSettings'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
  getApp,
} from '@facilio/router'
import SummarySidebar from 'newapp/components/SummarySideBar'
import CommonList from 'newapp/list/CommonList'
import CustomButton from '@/custombutton/CustomButton'
import { POSITION_TYPE } from 'pages/setup/custombutton/CustomButtonUtil'
import { CustomModuleData } from 'src/pages/custom-module/CustomModuleData'
import { findRouterForModuleInApp } from 'newapp/viewmanager/routeUtil'
import { isEmpty, isArray } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import { mapState } from 'vuex'
import AdvancedSearchWrapper from 'newapp/components/search/AdvancedSearchWrapper.vue'
import VisualType from 'src/newapp/components/VisualType.vue'
import CalendarView from 'src/newapp/list/CalendarView.vue'
import CalendarDateWrapper from 'src/newapp/list/components/CalendarDatePicker.vue'

export default {
  name: 'CommonModuleList',
  props: ['moduleName', 'viewname', 'isCustomModule'],
  components: {
    ColumnCustomization,
    Spinner,
    CommonListLayout,
    Pagination,
    FExportSettings,
    Sort,
    SummarySidebar,
    CommonList,
    CustomButton,
    VisualType,
    AdvancedSearchWrapper,
    CalendarView,
    CalendarDateWrapper,
  },
  data() {
    return {
      canShowListView: true,
      showType: false,
      recordCount: null,
      currentPageCount: null,
      perPage: 50,
      sortObj: {},
      selectedListItemsIds: [],
      selectedListItemsObj: [],
      showColumnSettings: false,
      isLoading: true,
      records: [],
      POSITION: POSITION_TYPE,
      isEmpty,

      searchText: '',
    }
  },
  created() {
    this.init()
  },
  computed: {
    ...mapState({
      viewDetail: state => state.view.currentViewDetail,
      isViewLoading: state => state.view.isLoading,
      isViewDetailLoading: state => state.view.detailLoading,
      metaInfo: state => state.view.metaInfo,
      groupViews: state => {
        let { view } = state || {}
        let { groupViews } = view || {}
        return !isEmpty(groupViews) ? groupViews : []
      },
    }),
    canHideSubHeader() {
      return isEmpty(this.records)
    },
    canShowCalendarHeader() {
      return !this.showListView && !this.showLoading && !isEmpty(this.viewname)
    },
    moduleDisplayName() {
      let { module: moduleObj, name } = this.metaInfo || {}
      let { displayName } = moduleObj || {}
      return displayName || name || this.moduleName
    },
    parentPath() {
      let { moduleName } = this
      return `/app/ca/modules/${moduleName}/`
    },
    viewLoading() {
      return this.isViewLoading || this.isViewDetailLoading
    },
    showLoading() {
      return this.isLoading || this.viewLoading
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

      return !isEmpty(search) ? JSON.parse(search) : null
    },
    canHideFilter() {
      let { $route, records } = this
      let {
        query: { search, page },
      } = $route || {}

      return isEmpty(records) && isEmpty(search) && isEmpty(page)
    },
    isPermissionsRequired() {
      let currentOrgId = this.$getProperty(this, '$org.id')
      return this.$constants.isCustomModulePermissionsEnabled(currentOrgId)
    },
    selectedRecordId() {
      let id = this.$getProperty(this.$route, 'params.id')
      return id ? parseInt(id) : -1
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
      let { moduleDisplayName } = this
      return this.$t('common.products.no_module_available', {
        moduleName: moduleDisplayName,
      })
    },
    createBtnText() {
      let { moduleDisplayName } = this
      return this.$t('common.products.new_module', {
        moduleName: moduleDisplayName,
      })
    },
    photoFieldName() {
      return 'photoId'
    },
    modelDataClass() {
      return CustomModuleData
    },
    mainFieldName() {
      let { fields } = this.metaInfo || {}
      let mainFieldObj = (fields || []).find(field => field.mainField)
      return mainFieldObj?.name || 'name'
    },
    isColumnCustomizable() {
      let { viewDetail } = this
      let { isEditable } = viewDetail || {}
      return isEditable
    },
    // will be uncommented after calendar configuration

    canShowVisualSwitch() {
      let { listView = false, calendarView = false } = this.viewDetail || {}
      return (
        listView &&
        calendarView &&
        this.$helpers.isLicenseEnabled('CALENDAR_VIEW') &&
        this.showType
      )
    },
    showListView() {
      let { viewDetail, canShowVisualSwitch, canShowListView = true } = this
      let { listView = false, calendarView = false, calendarViewContext } =
        viewDetail || {}

      return (
        (canShowVisualSwitch && canShowListView) ||
        (!listView && !calendarView) ||
        (listView && !calendarView) ||
        isEmpty(calendarViewContext)
      )
    },
    slotList() {
      return [
        {
          name: 'id',
          isHardcodedColumn: true,
          columnAttrs: {
            'min-width': 110,
            label: 'ID',
            fixed: 'left',
          },
        },
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
    hasViewList() {
      return (this.groupViews || []).some(grp => !isEmpty(grp.views))
    },
    viewDetailFields() {
      let { viewDetail } = this || {}
      let { fields } = viewDetail || {}
      return fields
    },
  },
  watch: {
    page() {
      this.currentPageCount = null
      this.refreshRecordDetails(false, false)
    },
    filters(newVal, oldVal) {
      if (!isEqual(newVal, oldVal)) {
        this.refreshRecordDetails()
      }
    },
    moduleName: {
      handler(newVal, oldVal) {
        if (!isEmpty(newVal) && !isEqual(newVal, oldVal)) {
          this.resetData()
          this.init()
          if (!isEmpty(this.viewname)) {
            this.refreshRecordDetails()
          }
        }
      },
      immediate: true,
    },
    viewname: {
      handler(newVal, oldVal) {
        if (!isEmpty(newVal) && !isEqual(newVal, oldVal)) {
          this.resetData()
          this.getViewDetail()
          this.refreshRecordDetails()
        }
      },
      immediate: true,
    },
    sortObj(newVal, oldVal) {
      if (!isEqual(newVal, oldVal)) this.refreshRecordDetails()
    },
    viewLoading(newVal) {
      if (!newVal && !this.hasViewList && isEmpty(this.viewname)) {
        this.isLoading = false
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
    async loadRecords(force = false) {
      let {
        moduleName,
        viewname,
        filters,
        page,
        perPage,
        sortObj,
        searchText,
      } = this

      await API.cancel({ uniqueKey: `${moduleName}_LIST` })
      await API.cancel({ uniqueKey: `${moduleName}_CUSTOM_BUTTON` })

      try {
        this.isLoading = true
        this.currentPageCount = null

        let params = {
          moduleName,
          viewname,
          filters,
          page,
          perPage,
          force,
          search: searchText,
          ...sortObj,
        }

        this.records = await this.modelDataClass.fetchAll(params)
        if (isArray(this.records)) {
          this.currentPageCount = this.records.length
          this.isLoading = false
        }
      } catch (error) {
        this.showErrorToastMessage(
          error,
          this.$t('custommodules.list.list_error')
        )
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
          this.selectedListItemsIds = []
          this.selectedListItemsObj = []
          await this.refreshRecordDetails(true)
        } catch (error) {
          this.showErrorToastMessage(
            error,
            this.$t('custommodules.list.delete_error')
          )
        }
        this.isLoading = false
      }
    },
    async loadCount(force = false) {
      let { moduleName, viewname, filters } = this

      API.cancel({ uniqueKey: `${moduleName}_LIST_COUNT` })

      try {
        this.recordCount = null
        let params = { moduleName, viewname, filters, force }
        this.recordCount = await this.modelDataClass.fetchRecordsCount(params)
      } catch (error) {
        this.showErrorToastMessage(
          error,
          this.$t('custommodules.list.unable_to_fetch_count')
        )
        this.recordCount = null
      }
    },
    toShowColumnSettings() {
      if (this.isColumnCustomizable) this.showColumnSettings = true
    },
    refreshRecordDetails: debounce(async function(force = false, count = true) {
      if (count) this.loadCount(true)
      this.loadRecords(force)
    }, 100),
    updateSort(sorting) {
      this.sortObj = { ...(sorting || {}) }
    },
    selectItems(selectedItem) {
      this.selectedListItemsObj = selectedItem
      this.selectedListItemsIds = selectedItem.map(value => value.id)
    },
    hasPermission(action) {
      let { isPermissionsRequired, moduleName } = this
      let currentOrgId = this.$getProperty(this, '$org.id') || null
      let isHideCreateButton =
        moduleName === 'custom_invoice' &&
        currentOrgId === 17 &&
        action === 'CREATE' //Investa special check
      if (isHideCreateButton) {
        return false
      }
      if (isPermissionsRequired || isWebTabsEnabled()) {
        return this.$hasPermission(`${this.moduleName}:${action}`)
      }
      return true
    },
    getViewManagerRoute() {
      let appId = (getApp() || {}).id
      let { moduleName, $route } = this
      let { query } = $route

      let route = findRouterForModuleInApp(moduleName, pageTypes.VIEW_MANAGER)
      if (route)
        this.$router.push({
          ...route,
          params: { moduleName },
          query: { ...query, appId },
        })
    },
    openList() {
      let { viewname, $route } = this
      let { query } = $route

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(this.moduleName, pageTypes.LIST) || {}
        name && this.$router.push({ name, params: { viewname }, query })
      } else {
        this.$router.push({
          name: 'custommodules-list',
          params: { viewname },
          query,
        })
      }
    },
    redirectToOverview(id) {
      let { moduleName, viewname, $route } = this
      let { query } = $route

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}
        let route = { name, params: { viewname, id }, query }

        return name && route
      } else {
        return {
          name: 'custommodules-summary',
          params: { moduleName, viewname, id },
          query,
        }
      }
    },
    editModule(row) {
      let { moduleName } = this
      let { id } = row
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.EDIT) || {}

        name && this.$router.push({ name, params: { id } })
      } else {
        this.$router.push({
          name: 'custommodules-edit',
          params: { moduleName, id },
        })
      }
    },
    redirectToFormCreation() {
      let { moduleName } = this
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.CREATE) || {}

        name && this.$router.push({ name })
      } else {
        this.$router.push({ name: 'custommodules-new', params: { moduleName } })
      }
    },
    openViewCreation() {
      let { moduleName } = this
      let appId = (getApp() || {}).id

      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForModule(moduleName, pageTypes.VIEW_CREATION) || {}

        if (name) {
          this.$router.push({ name, query: { appId } })
        }
      } else {
        let { name } =
          findRouterForModuleInApp(moduleName, pageTypes.VIEW_CREATION) || {}

        if (name) {
          this.$router.push({ name, params: { moduleName }, query: { appId } })
        }
      }
    },
    onCustomButtonSuccess() {
      this.loadRecords()
      this.selectedListItemsIds = []
      this.selectedListItemsObj = []
    },
    resetData() {
      this.showType = false
      this.isLoading = true
      this.recordCount = null
      this.currentPageCount = null
      this.records = null
      this.selectedListItemsIds = []
      this.selectedListItemsObj = []
      this.sortObj = {}
      this.$nextTick(() => {
        this.showType = true
      })
    },
    showErrorToastMessage(error, customMsg) {
      let message = error?.message || customMsg
      this.$message.error(message)
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
  .cm-view-empty-state-container {
    align-items: center;
    background-color: #fff;
    display: flex;
    flex-direction: column;
    justify-content: center;
    margin: 10px;
    flex-grow: 1;
    overflow: auto;

    .module-view-empty-state svg.icon {
      width: 150px;
      height: 120px;
    }
    .add-view-btn {
      background-color: #39b2c2;
      line-height: normal;
      padding: 11px 17px;
      border: solid 1px rgba(0, 0, 0, 0);
      margin-bottom: 30px;
    }
  }
  .cm-empty-state-container {
    background-color: #fff;
    flex-grow: 1;
    display: flex;
    flex-direction: column;
    justify-content: center;
    margin: 0px 10px 10px;
    height: calc(100vh - 125px);
  }
  .cm-list-container {
    border-width: 0px !important;
    border-style: solid;
    padding: 0px 10px 10px;
    height: calc(100vh - 155px) !important;
  }
  .cm-list-container-empty {
    height: calc(100vh - 250px);
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

.cm-empty-bg-white {
  background-color: #ffff;
  height: calc(100vh - 175px);
}

.custom-module-list-layout {
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
}
.scheduler-calender-view {
  height: 32px;
  width: 100px;

  .el-input__inner {
    padding: 5px;
    padding-left: 9px;
    border-radius: 4px;
    border: solid 1px #e3eaed;
    font-size: 14px;
    color: #324056;
    height: 32px;
    letter-spacing: 0.5px;
  }
  .el-input__suffix {
    height: 32px;
  }
  &.el-select .el-input.is-focus .el-input__inner,
  &.el-select:hover .el-input__inner {
    border-color: #4d95ff;
  }
  .el-input__inner:hover,
  .el-input__inner:focus {
    border-color: #4d95ff !important;
  }
}
</style>
