<template>
  <div class="related-list-container" ref="relatedListContainer">
    <div class="related-list-header">
      <div class="header justify-content-space">
        <div class="widget-title d-flex flex-direction-column justify-center">
          {{ relatedRecordDisplayName }}
        </div>
        <template v-if="!$validation.isEmpty(modulesList)">
          <el-input
            ref="mainFieldSearchInput"
            v-if="showMainFieldSearch"
            class="fc-input-full-border2 width-auto mL-auto"
            suffix-icon="el-icon-search"
            v-model="searchText"
            @blur="hideMainFieldSearch"
          ></el-input>
          <span v-else class="self-center mL-auto" @click="openMainFieldSearch">
            <inline-svg
              v-if="
                !widgetParams.hideSearchField ||
                  !hideSearchFieldList.includes(moduleName)
              "
              src="svgs/search"
              class="vertical-middle cursor-pointer"
              iconClass="icon icon-sm mT5 mR5 search-icon"
            ></inline-svg>
          </span>
          <span
            v-if="
              !$validation.isEmpty(totalCount) &&
                !$validation.isEmpty(modulesList) &&
                !hideSearchFieldList.includes(moduleName)
            "
            class="separator self-center"
            >|</span
          >
          <pagination
            :currentPage.sync="page"
            :total="totalCount"
            :perPage="perPage"
            class="self-center"
          ></pagination>
        </template>

        <template v-if="canShowCreate">
          <el-button
            @click="openFormCreation()"
            icon="el-icon-plus"
            class="el-button bR3 f12 fc-sites-btn el-button--text sh-button sh-button-add button-add sp-sh-btn-sm"
            type="text"
            v-tippy
            data-size="small"
          ></el-button>
        </template>
        <span v-else-if="canShowQuickCreate" class="self-center">
          <el-button @click="quickAddRecordToggle" class="fc-create-btn mR10">
            <i class="el-icon-plus"></i>
          </el-button>
        </span>
      </div>
    </div>
    <div class="position-relative">
      <div
        v-if="isLoading"
        class="loading-container d-flex justify-content-center"
      >
        <spinner :show="isLoading"></spinner>
      </div>

      <div
        v-else-if="$validation.isEmpty(modulesList)"
        class="flex-middle justify-content-center wo-flex-col flex-direction-column"
        style="margin-top:4%"
      >
        <inline-svg
          :src="`svgs/emptystate/readings-empty`"
          iconClass="icon text-center icon-xxxlg"
        ></inline-svg>
        <div class="pT10 fc-black-dark f18 bold pB50">
          {{ $t('common.products.no_mod_available') }}
          {{ moduleDisplayName ? moduleDisplayName : moduleName }}
          {{ $t('common._common.available') }}
        </div>
      </div>
      <div v-else class="fc-list-view fc-table-td-height">
        <div
          v-if="canShowColumnIcon"
          class="view-column-chooser"
          @click="showColumnCustomization"
        >
          <img
            src="~assets/column-setting.svg"
            class="column-customization-icon"
          />
        </div>

        <el-table
          :data="modulesList"
          style="width: 100%;"
          :fit="true"
          :height="tableHeight"
          class="related-list-widget-table"
        >
          <el-table-column
            v-if="!$validation.isEmpty(mainFieldColumn)"
            :label="getColumnHeaderLabel(mainFieldColumn)"
            :prop="mainFieldColumn.name"
            fixed
            min-width="200"
          >
            <template v-slot="item">
              <div
                class="table-subheading"
                @click="redirectToOverview(item.row)"
              >
                <div class="d-flex">
                  <div v-if="item.row.photoId">
                    <img
                      v-if="item.row.photoId > 0"
                      :src="getImage(item.row.photoId)"
                      class="img-container mR10"
                    />
                    <component
                      v-else
                      :is="
                        avatarMap[moduleName] ? avatarMap[moduleName] : 'Avatar'
                      "
                      size="lg"
                      :name="item.row.name"
                      :asset="item.row"
                      color="#4273e9"
                      class="mR10"
                    ></component>
                  </div>
                  <div class="self-center name bold">
                    {{
                      getColumnDisplayValue(mainFieldColumn, item.row) || '---'
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
            :label="getColumnHeaderLabel(field)"
            :align="
              (field.field || {}).dataTypeEnum === 'DECIMAL' ? 'right' : 'left'
            "
            min-width="200"
          >
            <template v-slot="scope">
              <keep-alive v-if="isSpecialHandlingField(field)">
                <component
                  :is="(listComponentsMap[moduleName] || {}).componentName"
                  :field="field"
                  :moduleData="scope.row"
                  @refreshList="refreshRelatedList"
                ></component>
              </keep-alive>

              <div v-else-if="isFileTypeField((field || {}).field)">
                <FilePreviewColumn
                  :field="field"
                  :record="scope.row"
                  :isV2="true"
                />
              </div>
              <div v-else-if="isMultiCurrencyField((field || {}).field)">
                <CurrencyPopOver
                  :field="field"
                  :details="scope.row"
                  :showInfo="false"
                />
              </div>
              <div
                v-else
                class="table-subheading"
                :class="{
                  'text-right': (field.field || {}).dataTypeEnum === 'DECIMAL',
                }"
              >
                {{ getColumnDisplayValue(field, scope.row) || '---' }}
              </div>
            </template>
          </el-table-column>
          <el-table-column
            width="210"
            v-if="
              ['vendorDocuments'].includes(moduleName) ||
                ($org.id === 155 && moduleName === 'workorder')
            "
          >
            <template v-slot="record">
              <div class="text-center">
                <el-button
                  @click="openStateTransitionComponent(record.row)"
                  class="fc-wo-border-btn visibility-hide-actions mR20"
                  >{{ $t('common.profile.change_status') }}</el-button
                >
              </div>
            </template>
          </el-table-column>
          <el-table-column
            v-if="canShowDelete || canShowEdit"
            prop
            label
            width="130"
            class="visibility-visible-actions"
          >
            <template v-slot="item">
              <div class="text-center">
                <span v-if="canShowEdit" @click="editItem(item.row.id)">
                  <inline-svg
                    src="svgs/edit"
                    class="edit-icon-color visibility-hide-actions"
                    iconClass="icon icon-sm mR5 icon-edit"
                  ></inline-svg>
                </span>
                <span
                  v-if="canShowDelete"
                  @click="invokeDeleteDialog(item.row)"
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
    </div>
    <QuickCreateRecord
      v-if="quickAddDialogVisibility"
      :dialogVisibility.sync="quickAddDialogVisibility"
      :moduleName="moduleName"
      :parentId="getParentId"
      @saved="refreshRelatedList"
    ></QuickCreateRecord>

    <StateTransitionComponent
      v-if="selectedRecord && showStateTransitionComponent"
      :moduleName="moduleName"
      :record="selectedRecord"
      :updateUrl="
        $getProperty(
          $constants,
          `moduleStateUpdateMap.${moduleName}.updateUrl`,
          'v2/module/data/update'
        )
      "
      :transformFn="transformFormData"
      @currentState="() => {}"
      @transitionSuccess="
        () => {
          ;(showStateTransitionComponent = false), refreshRelatedList()
        }
      "
      @transitionFailure="() => {}"
      @onClose="showStateTransitionComponent = false"
    ></StateTransitionComponent>
    <ColumnCustomization
      :visible.sync="canShowColumnCustomization"
      :moduleName="moduleName"
      :relatedViewDetail="viewDetail"
      :relatedMetaInfo="currentMetaInfo"
      :viewName="'hidden-all'"
      @refreshRelatedList="refreshRelatedList"
    />
  </div>
</template>
<script>
import Pagination from 'pageWidgets/utils/WidgetPagination'
import ViewMixinHelper from '@/mixins/ViewMixin'
import isEqual from 'lodash/isEqual'
import WorkOrderSpecialFieldsList from '@/list/WorkOrderSpecialFieldsList'
import InsuranceSpecialFieldsList from '@/list/InsuranceSpecialFieldsList'
import VendorDocumentSpecialFieldsList from '@/list/VendorDocumentSpecialFieldsList'
import TermsSpecialFieldsList from '@/list/TermsSpecialFieldsList'
import { eventBus } from '@/page/widget/utils/eventBus'
import { isEmpty, isNullOrUndefined } from '@facilio/utils/validation'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import { getBaseURL } from 'util/baseUrl'
import Constants from 'util/constant'
import { API } from '@facilio/api'
import ColumnCustomization from '@/ColumnCustomization'
import FilePreviewColumn from '@/list/FilePreviewColumn'
import { isFileTypeField } from '@facilio/utils/field'
import CurrencyPopOver from 'src/pages/setup/organizationSetting/currency/CurrencyPopOver.vue'

const skipSiteIdModules = [
  'vendors',
  'safetyPlan',
  'people',
  'vendorcontact',
  'service',
  'client',
]

const columnConfigMap = {
  workorder: {
    fixedColumns: ['subject'],
    fixedSelectableColumns: ['noOfNotes', 'noOfTasks', 'noOfAttachments'],
  },
}

const deleteUrlMap = {
  hazardPrecaution: {
    url: `v2/hazardPrecaution/delete`,
    key: 'hazardPrecautionIds',
    successMsg: 'Associated Preacaution Deleted Successfully',
  },
  workorderHazard: {
    url: `v2/workorderHazard/delete`,
    key: 'workOrderHazardIds',
    successMsg: 'Hazard Deleted Successfully',
  },
  assetHazard: {
    url: `v2/assetHazard/delete`,
    key: 'assetHazardIds',
    successMsg: 'Hazard Deleted Successfully',
  },
  quoteterms: {
    url: `v2/quotation/disassociateTerms`,
    key: 'recordIds',
    successMsg: 'Terms Disassociated Successfully',
  },
  poterms: {
    url: `v2/purchaseorder/disassociateTerms`,
    key: 'recordIds',
    successMsg: 'Terms Disassociated Successfully',
  },
  prterms: {
    url: `v2/purchaserequest/disassociateTerms`,
    key: 'recordIds',
    successMsg: 'Terms Disassociated Successfully',
  },
}

const customModulesColumnConfig = {
  fixedColumns: ['name'],
  fixedSelectableColumns: ['photo'],
}

export default {
  name: 'RelatedListWidget',
  props: ['widget', 'details', 'staticWidgetHeight'],
  mixins: [ViewMixinHelper],
  components: {
    Pagination,
    // eslint-disable-next-line vue/no-unused-components
    Avatar: () => import('@/Avatar'),
    QuickCreateRecord: () => import('@/forms/FQuickCreateRecord'),
    StateTransitionComponent: () =>
      import('@/stateflow/ExecuteTransitionPopup'),
    FilePreviewColumn,
    ColumnCustomization,
    CurrencyPopOver,
  },
  data() {
    return {
      relatedList: [],
      deleteItem: {},
      quickSearchQueries: null,
      quickAddDialogVisibility: false,
      showColumnSettings: false,
      defaultHideColumns: ['photo', 'stateFlowId'],
      columnConfig: {
        fixedColumns: ['name'],
        fixedSelectableColumns: ['photo'],
      },
      listComponentsMap: {
        workorder: {
          componentName: WorkOrderSpecialFieldsList,
          specialHandlingFields: [
            'resource',
            'assignedTo',
            'noOfNotes',
            'noOfTasks',
            'noOfAttachments',
          ],
        },
        insurance: {
          componentName: InsuranceSpecialFieldsList,
          specialHandlingFields: ['insurance'],
        },
        vendorDocuments: {
          componentName: VendorDocumentSpecialFieldsList,
          specialHandlingFields: ['document'],
        },
        quoteterms: {
          componentName: () => import('@/list/TermsSpecialFieldsList'),
          specialHandlingFields: ['longDesc'],
        },
        poterms: {
          componentName: TermsSpecialFieldsList,
          specialHandlingFields: ['longDesc'],
        },
        prterms: {
          componentName: TermsSpecialFieldsList,
          specialHandlingFields: ['longDesc'],
        },
      },
      hideEditModulesList: [
        'purchaseorder',
        'purchaserequest',
        'contracts',
        'vendorDocuments',
        'insurance',
        'contact',
        'workorder',
        'safetyPlanHazard',
        'hazardPrecaution',
        'workorderHazard',
        'assetHazard',
        'workpermit',
        'tenantcontact',
        'vendorcontact',
        'clientcontact',
        'tenantspaces',
        'site',
        'quoteterms',
        'poterms',
        'prterms',
      ],
      hideDeleteModulesList: [
        'purchaseorder',
        'purchaserequest',
        'contracts',
        'vendorDocuments',
        'insurance',
        'contact',
        'workorder',
        'workpermit',
        'tenantcontact',
        'vendorcontact',
        'tenantspaces',
        'clientcontact',
        'site',
      ],
      showDeleteInPortalModules: ['workorderHazard', 'hazardPrecaution'],
      hideSearchFieldList: [
        'safetyPlanHazard',
        'hazardPrecaution',
        'workorderHazard',
        'assetHazard',
        'quoteterms',
      ],
      hideColumnCustomizationList: [
        'purchaserequest',
        'safetyPlanHazard',
        'workorderHazardPrecaution',
        'hazardPrecaution',
        'workorderHazard',
        'assetHazard',
        'quoteterms',
        'workpermit',
      ],
      showStateTransitionComponent: false,
      selectedRecord: null,
      avatarMap: {
        asset: () => import('@/avatar/Asset'),
      },
      page: 1,
      perPage: 10,
      totalCount: null,
      showMainFieldSearch: false,
      isSearchDataLoading: false,
      debounceDelay: 2000,
      viewDetailsExcludedModules: ['users', 'building', 'space', 'basespace'],
      searchText: null,
      viewDetail: null,
      isLoading: false,
      modulesList: [],
      canShowColumnCustomization: false,
      currentMetaInfo: null,
    }
  },

  created() {
    this.init()
    this.isFileTypeField = isFileTypeField
    let { moduleName } = this
    this.$store.dispatch('loadTicketStatus', moduleName)

    if (['vendorDocuments'].includes(moduleName)) {
      this.defaultHideColumns.push('parentId')
    }
  },

  mounted() {
    eventBus.$on('refresh-related-list', () => {
      this.refreshRelatedList()
    })
    eventBus.$on('delete-record', () => {
      this.deleteItemFromList()
    })
    eventBus.$on('dissociate', () => {
      this.dissociateItem()
    })
  },

  watch: {
    filters(newVal, oldVal) {
      if (!isEqual(newVal, oldVal)) {
        this.$set(this, 'isSearchDataLoading', true)
        this.debounceMainFieldSearch()
      }
    },
    page(newVal, oldVal) {
      if (!isEqual(newVal, oldVal)) {
        let { isSearchDataLoading } = this
        let promise = []
        if (!isSearchDataLoading) {
          promise.push(this.fetchModulesData(true))
          promise.push(this.loadDataCount(true))
          this.isLoading = true
          Promise.all(promise).finally(() => {
            this.isLoading = false
          })
        }
      }
    },
  },

  computed: {
    relatedRecordDisplayName() {
      let { moduleDisplayName, widget } = this
      let { relatedList } = widget || {}
      let { field = {} } = relatedList || {}
      let { relatedListDisplayName } = field || {}
      return relatedListDisplayName || moduleDisplayName || '---'
    },
    moduleName() {
      let { widget } = this
      let { relatedList } = widget || {}
      let { module } = relatedList || {}
      let { name } = module || {}
      return name || ''
    },
    mainField() {
      let { viewDetail } = this
      let mainField = null
      if (!isEmpty(viewDetail)) {
        let { fields } = viewDetail
        mainField = (fields || []).find(field => {
          let { field: fieldObj } = field
          return (fieldObj || {}).mainField
        })
      } else {
        mainField = {
          name: 'name',
          displayName: 'Name',
          field: {
            name: 'name',
            dataTypeEnum: 'STRING',
          },
        }
      }
      return mainField
    },
    filters() {
      let { mainField, searchText, details, widget, siteId, moduleName } = this

      let { id } = details
      let { relatedList } = widget || {}
      let { field } = relatedList || {}
      let fieldName = (field || {}).name
      let filterObj = {}

      if (!isEmpty(fieldName) && id) {
        filterObj[fieldName] = {
          operatorId: 36,
          value: [`${id}`],
        }
      }
      if (!isEmpty(siteId) && !skipSiteIdModules.includes(moduleName)) {
        filterObj.siteId = {
          operatorId: 36,
          value: [`${siteId}`],
        }
      }

      if (
        !isEmpty(mainField) &&
        !isEmpty(searchText) &&
        searchText.length > 0
      ) {
        let { name, field } = mainField
        let { dataTypeEnum } = field
        let value = [searchText]
        let operatorId = Constants.FILTER_OPERATORID_HASH[dataTypeEnum]
        filterObj[name] = {
          operatorId,
          value,
        }
      }

      return filterObj
    },
    filteredViewColumns() {
      let { viewColumns, hideColumns } = this
      if (!isEmpty(viewColumns)) {
        return viewColumns.filter(column => {
          return !hideColumns.includes(column.name)
        })
      }
      return []
    },
    routeName() {
      let { $route } = this
      let { name } = $route || {}
      return name || ''
    },
    getParentId() {
      let { id } = this.details || {}
      return id
    },
    isCustomModule() {
      let { widget } = this
      let { relatedList } = widget || {}
      let { module } = relatedList || {}
      let { custom } = module || {}
      return custom
    },
    moduleDetails() {
      let { widget } = this
      let { relatedList } = widget || {}
      let { module } = relatedList || {}
      return module
    },
    canShowEdit() {
      let { moduleName, $account, hideEditModulesList, moduleDetails } = this
      let { user } = $account
      let { appType } = user || {}
      let { type: moduleType } = moduleDetails || {}

      // temporary module type check
      let moduleTypeCheck =
        [1, 26, 27].includes(moduleType) &&
        this.$helpers.isLicenseEnabled('NEW_V3API')
      let hasPermission =
        !moduleTypeCheck && this.$hasPermission(`${moduleName}: UPDATE`)

      // existing check
      let hardCodedCheck = !(
        (appType && appType > 0) ||
        hideEditModulesList.includes(moduleName)
      )

      return !isWebTabsEnabled()
        ? hasPermission && hardCodedCheck
        : hardCodedCheck
    },
    canShowDelete() {
      let {
        moduleName,
        $account,
        hideDeleteModulesList,
        showDeleteInPortalModules,
        moduleDetails,
      } = this
      let { user } = $account
      let { appType } = user || {}

      let tAndCEabledModulesHash = {
        quoteterms: 'quote',
        prterms: 'purchaserequest',
        poterms: 'purchaseorder',
      }
      let canShowTAndCDelete = !isEmpty(tAndCEabledModulesHash[moduleName])
        ? this.$hasPermission(`${tAndCEabledModulesHash[moduleName]}: UPDATE`)
        : true

      let { type: moduleType } = moduleDetails || {}

      // temporary module type check
      let moduleTypeCheck =
        [1, 26, 27].includes(moduleType) &&
        this.$helpers.isLicenseEnabled('NEW_V3API')
      let hasPermission =
        !moduleTypeCheck && this.$hasPermission(`${moduleName}: DELETE`)

      // existing check
      let hardCodedCheck =
        canShowTAndCDelete &&
        !(
          (appType &&
            appType > 0 &&
            !showDeleteInPortalModules.includes(moduleName)) ||
          hideDeleteModulesList.includes(moduleName)
        )

      return !isWebTabsEnabled()
        ? hasPermission && hardCodedCheck
        : hardCodedCheck
    },
    canShowColumnIcon() {
      return (
        !this.widgetParams.hideColumnCustomization ||
        !this.hideColumnCustomizationList.includes(this.moduleName)
      )
    },
    moduleDisplayName() {
      let { widget } = this
      let { relatedList } = widget || {}
      let { module = {} } = relatedList || {}
      let { displayName } = module || {}
      return displayName || ''
    },
    tableHeight() {
      let { $refs, staticWidgetHeight } = this
      let height = '250px'
      let relatedListContainer = $refs['relatedListContainer']
      if (!isEmpty(relatedListContainer)) {
        let containerHeight = (relatedListContainer || {}).scrollHeight - 90
        height = `${containerHeight}px`
      }
      // temp handling
      if (staticWidgetHeight) {
        return staticWidgetHeight
      }
      return height
    },
    mainFieldColumn() {
      let { viewColumns, mainField } = this
      let mainFieldName =
        this.$getProperty(mainField, 'field.name', '') || 'name'
      if (!isEmpty(viewColumns)) {
        return viewColumns.find(column => column.name === mainFieldName)
      }
      return {}
    },
    hideColumns() {
      let { defaultHideColumns = [], mainField } = this
      let mainFieldName =
        this.$getProperty(mainField, 'field.name', '') || 'name'
      return defaultHideColumns.concat(mainFieldName)
    },
    hidePaginationSearch() {
      let { moduleName } = this
      return ['users', 'site'].includes(moduleName)
    },
    isUserLookupModule() {
      let { moduleName } = this
      return moduleName === 'users'
    },
    columnCustomizationConfig() {
      let { moduleName } = this
      return columnConfigMap[moduleName] || customModulesColumnConfig
    },
    widgetParams() {
      let { widget } = this
      let { widgetParams } = widget || {}
      return widgetParams || {}
    },
    pageModuleName() {
      return this.$attrs.moduleName
    },
    canShowCreate() {
      let { moduleName, pageModuleName, widgetParams } = this
      let { showCreate } = widgetParams || {}
      let isWorkorderTenant =
        moduleName === 'workorder' &&
        ['tenant', 'tenantunit'].includes(pageModuleName)
      let isWorkpermit =
        moduleName === 'workpermit' && pageModuleName === 'workorder'

      return isWorkorderTenant || isWorkpermit || showCreate
    },
    canShowQuickCreate() {
      let { moduleName, widgetParams } = this
      let { showQuickCreate } = widgetParams || {}

      return (
        [
          'vendorDocuments',
          'vendorcontact',
          'clientcontact',
          'custom_tenantutility',
        ].includes(moduleName) || showQuickCreate
      )
    },
    routeToFormHash() {
      return {
        workorder: this.getTenantWorkorderRoute,
        workpermit: this.getWorkPermitRoute,
      }
    },
    getTenantWorkorderRoute() {
      let { details, $getProperty, pageModuleName } = this
      let query = {}
      if (pageModuleName === 'tenant') {
        query = {
          tenant: details.id,
          tenantLabel: details.name,
        }
      } else if (pageModuleName === 'tenantunit') {
        query = {
          siteId: details.siteId,
        }
        if (details.tenant) {
          this.$setProperty(query, 'tenant', $getProperty(details, 'tenant.id'))
          this.$setProperty(
            query,
            'tenantLabel',
            $getProperty(details, 'tenant.name')
          )
        }
        if (this.$org.id === 320) {
          this.$setProperty(
            query,
            'resource',
            $getProperty(details, 'buildingId')
          )
          this.$setProperty(
            query,
            'resourceLabel',
            $getProperty(details, 'building.name')
          )
          this.$setProperty(query, 'unit', details.id)
          this.$setProperty(query, 'unitLabel', details.name)
        }
      }
      return { path: '/app/wo/create', query }
    },
    getWorkPermitRoute() {
      let { details = {} } = this
      let query = {
        ticket: details.id,
        ticketLabel: details.subject,
      }

      if (!isEmpty(details.vendor)) {
        this.$setProperty(query, 'vendor', details.vendor.id)
        this.$setProperty(query, 'vendorLabel', details.vendor.name)
      }
      if (!isEmpty(details.siteId)) {
        this.$setProperty(query, 'siteId', details.siteId)
      }
      if (!isEmpty(details.subject)) {
        this.$setProperty(query, 'name', details.subject)
      }
      if (!isEmpty(details.resource)) {
        // 1 => Space 2 => Asset
        if (details.resource.resourceType === 1) {
          this.$setProperty(query, 'space', details.resource.id)
          this.$setProperty(query, 'spaceLabel', details.resource.name)
        } else if (details.resource.resourceType === 2) {
          this.$setProperty(
            query,
            'space',
            this.$getProperty(details, 'resource.space.id')
          )
          this.$setProperty(
            query,
            'spaceLabel',
            this.$getProperty(details, 'resource.space.name')
          )
        }
      }
      return { path: `/app/wo/workpermit/new`, query }
    },
  },
  methods: {
    init() {
      this.debounceMainFieldSearch = this.$helpers.debounce(() => {
        this.fetchModulesData().finally(() => {
          this.onDataLoad && this.onDataLoad()
        })
        this.loadDataCount()
      }, this.debounceDelay)
      this.loadData()
      this.loadDataCount()
    },

    loadData() {
      let promise = []

      if (this.moduleName) {
        promise.push(this.fetchViewDetail())
        promise.push(this.fetchModulesData())

        this.isLoading = true
        Promise.all(promise).finally(() => {
          this.isLoading = false
          this.onDataLoad && this.onDataLoad()
        })
      }
    },

    fetchViewDetail() {
      let { moduleName, viewDetailsExcludedModules } = this
      let viewDetailUrl = `v2/views/hidden-all?moduleName=${moduleName}`

      return API.get(viewDetailUrl).then(({ data, error }) => {
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          if (
            !isEmpty(data) &&
            !viewDetailsExcludedModules.includes(moduleName)
          ) {
            let { viewDetail } = data
            if (!isEmpty(viewDetail)) {
              this.$set(this, 'viewDetail', viewDetail)
            }
          }
        }
      })
    },

    fetchModulesData(skipResetPage = false) {
      let {
        moduleName,
        filters,
        perPage,
        viewDetailsExcludedModules,
        hidePaginationSearch,
        searchText,
      } = this
      let modulesListDataUrl
      modulesListDataUrl = `v2/module/data/list?moduleName=${moduleName}`
      // Reset page to 1 on search
      if (!isEmpty(searchText) && !skipResetPage) {
        this.$set(this, 'page', 1)
      }
      if (modulesListDataUrl.includes('?')) {
        modulesListDataUrl += '&'
      } else {
        modulesListDataUrl += '?'
      }
      if (!hidePaginationSearch) {
        modulesListDataUrl += `page=${this.page}&perPage=${perPage}`
      }
      if (!viewDetailsExcludedModules.includes(moduleName)) {
        modulesListDataUrl += `&viewName=hidden-all`
      }
      if (!isEmpty(filters)) {
        let encodedFilters = encodeURIComponent(JSON.stringify(filters))
        modulesListDataUrl = `${modulesListDataUrl}&filters=${encodedFilters}`
      }

      return API.get(modulesListDataUrl)
        .then(({ data, error }) => {
          if (error) {
            this.$message.error(error.message)
          } else {
            if (!isEmpty(data)) {
              let moduleDatas = data.moduleDatas
              if (!isNullOrUndefined(moduleDatas)) {
                this.$set(this, 'modulesList', moduleDatas)
              }
            }
          }
        })
        .catch(({ message }) => {
          this.$message.error(message)
        })
        .finally(() => {
          this.isSearchDataLoading = false
        })
    },

    loadDataCount() {
      let { moduleName, filters, hidePaginationSearch } = this

      if (!hidePaginationSearch && moduleName) {
        let url = `v2/module/data/list?moduleName=${moduleName}&fetchCount=true`

        if (!isEmpty(filters)) {
          let encodedFilters = encodeURIComponent(JSON.stringify(filters))
          url = `${url}&filters=${encodedFilters}`
        }

        return API.get(url).then(({ data, error }) => {
          if (error) {
            this.$message.error(error.message || 'Error Occured')
          } else {
            let { count } = data
            this.$set(this, 'totalCount', count)
          }
        })
      }
    },

    isSpecialHandlingField(field) {
      let { name } = field
      let { moduleName, listComponentsMap = {} } = this
      let specialFields = (listComponentsMap[moduleName] || {})
        .specialHandlingFields
      return (specialFields || []).includes(name)
    },

    hideMainFieldSearch() {
      let { searchText } = this
      if (isEmpty(searchText)) {
        this.$set(this, 'showMainFieldSearch', false)
      }
    },

    openMainFieldSearch() {
      this.$set(this, 'showMainFieldSearch', true)
      this.$nextTick(() => {
        let mainFieldSearchInput = this.$refs['mainFieldSearchInput']
        if (!isEmpty(mainFieldSearchInput)) {
          mainFieldSearchInput.focus()
        }
      })
    },

    refreshRelatedList() {
      this.loadData()
      this.loadDataCount()
    },

    quickAddRecordToggle() {
      this.$set(
        this,
        'quickAddDialogVisibility',
        !this.quickAddDialogVisibility
      )
    },
    openFormCreation() {
      let { path, query } = this.routeToFormHash[this.moduleName]
      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForModule(this.moduleName, pageTypes.CREATE) || {}
        name && this.$router.push({ name, query })
      } else {
        this.$router.push({ path, query })
      }
    },
    editItem(id) {
      let { moduleName } = this

      if (isWebTabsEnabled()) {
        let pageType
        let params = { id }

        if (moduleName === 'workorder') {
          pageType = pageTypes.OVERVIEW
          params.viewname = 'all'
        } else {
          pageType = pageTypes.EDIT
        }

        let { name } = findRouteForModule(moduleName, pageType) || {}

        if (name) {
          this.$router.push({ name, params })
        }
      } else {
        if (moduleName === 'workorder') {
          this.$router.push({
            name: 'wosummarynew',
            params: { id },
          })
        } else {
          this.$router.push({
            name: 'custommodules-edit',
            params: { moduleName, id },
          })
        }
      }
    },
    deleteItemFromList() {
      let { deleteItem, widget } = this
      let { relatedList } = widget || {}
      let { module } = relatedList || {}
      let { name } = module || {}
      let url = `/v2/module/data/delete`
      let item = this.modulesList.find(
        listItem => listItem.id === deleteItem.id
      )
      let data = {
        moduleName: name,
        ids: [deleteItem.id],
      }
      API.post(url, data)
        .then(({ error }) => {
          if (error) {
            this.$message.error(error.message || 'Error Occured')
          } else {
            let idx = this.modulesList.indexOf(item)
            this.modulesList.splice(idx, 1)
            eventBus.$emit('show-delete-dialog', {
              showDeleteDialog: false,
            })
            this.$message.success('Deleted Successfully')
          }
        })
        .catch(({ message }) => {
          this.$message.error(message)
        })
    },
    dissociateItem() {
      let { deleteItem, widget } = this
      let { relatedList } = widget || {}
      let { field, module } = relatedList || {}
      let fieldName = field.name
      let { name } = module || {}
      let formData = ''
      let { data } = deleteItem || {}
      delete data[fieldName]
      let finalData = {
        data: {
          [fieldName]: {
            id: -99,
          },
        },
        id: deleteItem.id,
      }
      formData = {
        moduleName: name,
        withLocalId: false,
        moduleData: finalData,
      }
      let url = `/v2/module/data/update`
      let item = this.modulesList.find(
        listItem => listItem.id === deleteItem.id
      )

      API.post(url, formData).then(({ error }) => {
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          let idx = this.modulesList.indexOf(item)
          this.modulesList.splice(idx, 1)
          eventBus.$emit('show-delete-dialog', {
            showDeleteDialog: false,
          })
          this.$message.success('Dissociated Successfully')
        }
      })
    },
    redirectToListView() {
      let { moduleName } = this
      let lookupFilter = {
        lookup: [
          {
            operatorId: 36,
            value: [this.getParentId ? this.getParentId : ''],
            selectedLabel: this.$attrs.moduleName ? this.$attrs.moduleName : '',
          },
        ],
      }
      let query = {
        search: JSON.stringify(lookupFilter),
        includeParentFilter: true,
      }

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.LIST)

        if (name) {
          this.$router.push({
            name,
            params: {
              viewname: 'all',
            },
            query,
          })
        }
      } else {
        this.$router.push({
          path: `/app/ca/modules/${moduleName}/all`,
          query,
        })
      }
    },
    redirectToOverview(row) {
      let { moduleName, isCustomModule } = this
      let route

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}

        if (name) {
          route = this.$router.resolve({
            name,
            params: {
              viewname: 'all',
              id: row.id,
            },
          }).href
        }
      } else {
        let hasPermission = this.$hasPermission(`${moduleName}: READ`)
        if (!hasPermission) {
          return
        }
        let routerMap = {
          workorder: {
            name: 'wosummarynew',
            params: { id: row.id },
          },
          workpermit: {
            name: 'workPermitSummary',
            params: { id: row.id, viewname: 'all' },
          },
          purchaserequest: {
            name: 'prSummary',
            params: { id: row.id, viewname: 'all' },
          },
          purchaseorder: {
            name: 'poSummary',
            params: { id: row.id, viewname: 'all' },
          },
          tenantcontact: {
            name: 'tenantcontact',
            params: { id: row.id, viewname: 'all' },
          },
          tenantunit: {
            name: 'tenantUnitSummary',
            params: { id: row.id, viewname: 'all' },
          },
          vendors: {
            name: 'vendorsSummary',
            params: { id: row.id, viewname: 'all' },
          },
          asset: {
            name: 'assetsummary',
            params: { assetid: row.id, viewname: 'all' },
          },
          tenantspaces: {
            name: 'tenant',
            params: { id: (row.tenant || {}).id, viewname: 'all' },
          },
          serviceRequest: {
            name: 'serviceRequestSummary',
            params: { id: row.id, viewname: 'all' },
          },
          client: {
            name: 'clientSummary',
            params: { id: row.id, viewname: 'all' },
          },
          vendorcontact: {
            name: 'vendorContactsSummary',
            params: { id: row.id, viewname: 'all' },
          },
          insurance: {
            name: 'insurancesSummary',
            params: { id: row.id, viewname: 'all' },
          },
          quote: {
            path: `/app/tm/quotation/all/${row.id}/overview`,
          },
          item: {
            path: `/app/inventory/item/all/${row.id}/summary`,
          },
          tool: {
            path: `/app/inventory/tool/all/${row.id}/summary`,
          },
          inspectionResponse: {
            path: `/app/inspection/individual/all/summary/${row.id}`,
          },
        }

        if (isCustomModule) {
          route = this.$router.resolve({
            path: `/app/ca/modules/${moduleName}/all/${row.id}/summary`,
          }).href
        } else {
          route = this.$router.resolve(routerMap[moduleName]).href
        }
      }
      route && window.open(route, '_blank')
    },
    async invokeDeleteDialog(item) {
      this.deleteItem = item
      let { moduleName, $attrs, moduleDisplayName } = this
      let { isV3Api } = $attrs || {}
      if (deleteUrlMap[moduleName]) {
        let url = (deleteUrlMap[moduleName] || {}).url
        let successMsg = (deleteUrlMap[moduleName] || {}).successMsg
        let param = {
          [(deleteUrlMap[moduleName] || {}).key]: [String(item.id)],
        }

        API.post(url, param)
          .then(({ error }) => {
            if (error) {
              this.$message.error(error.message || 'Error Occured')
            } else {
              this.$message.success(successMsg)
              this.refreshRelatedList()
              eventBus.$emit('refesh-parent')
            }
          })
          .catch(({ message }) => {
            this.$message.error(message)
          })
      } else if (isV3Api) {
        let value = await this.$dialog.confirm({
          title: this.$t(`common.header.delete_record`),
          message: this.$t(
            `common._common.are_you_sure_want_to_delete_this_record`
          ),
          rbDanger: true,
          rbLabel: this.$t('common._common.delete'),
        })
        if (value) {
          let { error } = await API.deleteRecord(this.moduleName, [item.id])
          if (error) {
            this.$message.error(error.message || 'Error Occured')
          } else {
            this.$message.success(`${moduleDisplayName} deleted successfully`)
            this.refreshRelatedList()
            eventBus.$emit('refesh-parent')
          }
        }
      } else {
        eventBus.$emit('show-delete-dialog', {
          showDeleteDialog: true,
        })
      }
    },
    showColumnCustomization() {
      let { moduleName } = this
      let url = `/module/meta?moduleName=${moduleName}`
      API.get(url)
        .then(({ data, error }) => {
          if (error) {
            this.$message.error(error.message || 'Error occured')
          } else {
            if (!isEmpty(data)) {
              let { meta: metaInfo } = data
              this.currentMetaInfo = metaInfo
              this.canShowColumnCustomization = true
            }
          }
        })
        .catch(errMsg => {
          this.$message.error(errMsg)
        })
    },
    getImage(photoId) {
      return `${getBaseURL()}/v2/files/preview/${photoId}`
    },
    openStateTransitionComponent(record) {
      this.selectedRecord = record
      this.showStateTransitionComponent = true
    },
    transformFormData(returnObj, formData) {
      let { moduleName } = this
      let paramKey = this.$getProperty(
        this,
        `$constants.moduleStateUpdateMap.${moduleName}.key`,
        'moduleData'
      )
      returnObj[`${paramKey}`] = {
        id: returnObj.id,
      }
      if (formData) {
        if (formData.comment) {
          returnObj.comment = formData.comment
          delete formData.comment
        }
        returnObj[`${paramKey}`] = { ...returnObj[`${paramKey}`], ...formData }
      }
      if (moduleName === 'vendorDocuments') {
        returnObj[`${paramKey}`] = [returnObj[`${paramKey}`]]
        returnObj['parentAttachmentModuleName'] = moduleName
      }
      return returnObj
    },
  },
}
</script>
<style scoped>
.view-column-chooser {
  position: absolute;
  top: 0;
  right: 0px;
  display: block;
  width: 45px;
  height: 50px;
  cursor: pointer;
  text-align: center;
  background-color: #f3f1fc;
  border-left: 1px solid #f2f5f6;
  z-index: 20;
}
.img-container {
  width: 40px;
  height: 40px;
  border: 1px solid #f9f9f9;
  border-radius: 50%;
}
</style>
<style lang="scss">
.related-list-container {
  .el-table th.el-table__cell > .cell {
    padding-left: 0px;
    padding-right: 0px;
  }
  .widget-title {
    margin-left: 4px;
  }
  .related-list-widget-table.el-table {
    th.el-table__cell {
      background-color: #f3f1fc;
    }
  }
}
</style>
