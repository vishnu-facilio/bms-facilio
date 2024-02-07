<template>
  <div class="line-item-list">
    <template v-if="!canHideHeader">
      <div v-if="canShowSelection" class="line-item-selected-action-container">
        <div class="line-item-selected-items">
          <span class="line-item-selection-count">{{
            selectedRecordCount
          }}</span>
          <span class="line-item-selection-text">
            {{ $t('common.line_item.items_selected') }}</span
          >
        </div>
        <div>
          <slot name="bulk-actions"></slot>
        </div>
        <div class="line-item-selection-close-btn">
          <fc-icon
            group="default"
            name="close"
            size="18"
            class="pointer"
            @click="clearSelection()"
          ></fc-icon>
        </div>
      </div>
      <template v-else>
        <div class="line-item-list-header-container">
          <div
            v-if="!isEmpty(appliedFilter)"
            class="line-item-wizard-filter-ftags"
          >
            <div class="line-item-wizard-filter-text">
              {{ $t('commissioning.sheet.filter') }}
            </div>
            <FTags
              :key="`ftag-${moduleName}`"
              :moduleName="moduleName"
              :filters="appliedFilter"
              :hideQuery="true"
              :hideSaveAs="true"
              @updateFilters="setAppliedfilter"
              @clearFilters="setAppliedfilter({})"
            />
          </div>
          <div v-else class="line-item-list-header-title">
            {{ title }}
          </div>

          <slot name="header-additional-actions"></slot>
          <div class="line-item-list-header-action">
            <template v-if="canShowSearchAndFilter">
              <AdvancedSearch
                v-show="canShowFilter"
                :key="`filter-${moduleName}`"
                :moduleName="moduleName"
                :moduleDisplayName="moduleDisplayName"
                :hideQuery="true"
                :filterList="appliedFilter"
                @applyFilters="setAppliedfilter"
              ></AdvancedSearch>
              <span v-if="canShowPagination" class="separator mL10 mR10"
                >|</span
              >
            </template>
            <Pagination
              v-if="canShowPagination"
              :key="`pagination-${moduleName}`"
              :total="recordCount"
              :currentPage.sync="page"
              :currentPageCount="(recordList || []).length"
              :perPage="perPage"
            ></Pagination>
            <slot name="header-additional-action-right"></slot>
          </div>
        </div>
      </template>
    </template>
    <spinner v-if="loading" :show="loading" size="80"></spinner>
    <template v-else>
      <template v-if="isEmpty(recordList)">
        <div v-if="isFilterEnabled" class="line-item-list-empty-state">
          <InlineSvg
            src="svgs/list-empty"
            iconClass="icon icon-130"
          ></InlineSvg>
          <div class="line-item-list-empty-state-text">
            {{ $t('common.line_item.no_records_found') }}
          </div>
        </div>
        <slot v-else name="empty-state">
          <div class="line-item-list-empty-state">
            <slot name="empty-state-icon-text">
              <InlineSvg
                src="svgs/list-empty"
                iconClass="icon icon-130"
              ></InlineSvg>
              <div class="line-item-list-empty-state-text">
                {{ emptyStateText }}
              </div>
            </slot>
            <slot name="empty-state-btn">
              <el-button
                v-if="config.canShowAddBtn && isEmpty(actionButtonList)"
                type="primary"
                @click="openForm"
                class="line-item-list-empty-state-action-btn"
                :disabled="disableActionableUIElements"
              >
                {{ addBtnName }}
              </el-button>
              <template v-if="!isEmpty(actionButtonList)">
                <el-button
                  v-if="actionButtonList.length === 1"
                  type="primary"
                  @click="handleClickActionBtn(actionButtonList[0].value)"
                  class="line-item-list-empty-state-action-btn"
                  :disabled="disableActionableUIElements"
                >
                  {{ actionButtonList[0].label }}
                </el-button>
                <el-dropdown
                  v-else
                  split-button
                  type="primary"
                  @command="handleClickActionBtn"
                  @click="handleClickActionBtn(actionButtonList[0].value)"
                  class="line-item-list-empty-state-action-btn-list"
                  :disabled="disableActionableUIElements"
                >
                  {{ actionButtonList[0].label }}
                  <el-dropdown-menu
                    slot="dropdown"
                    class="line-item-list-empty-state-action-btn-list-menu"
                  >
                    <el-dropdown-item
                      v-for="(btnObj, index) in actionButtonList.slice(1)"
                      :key="`${btnObj.value}-${index}}`"
                      :command="btnObj.value"
                      class="line-item-list-empty-state-action-btn-list-menu-item"
                    >
                      {{ btnObj.label }}
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
              </template>
            </slot>
          </div>
        </slot>
      </template>
      <template v-else>
        <div class="line-item-list-table">
          <div
            v-if="!config.canHideColumnConfig"
            class="line-item-list-column-custom-icon"
            @click="showColumnSettings = true"
          >
            <fc-icon
              group="default"
              name="column-customisation"
              color="#969696"
              size="16"
              class="margin-auto"
            ></fc-icon>
          </div>
          <CommonList
            ref="lineItemTable"
            :key="`line-item-table-${moduleName}`"
            :viewDetail="viewDetail"
            :records="recordList"
            :moduleName="moduleName"
            :slotList="slotList"
            :canShowCustomButton="canShowCustomButton"
            :hideListSelect="hideListSelect"
            :modelDataClass="modelDataClass"
            :refreshList="onCustomButtonSuccess"
            :checkSelection="checkSelection"
            @selection-change="handleSelection"
            @mainField="mainfieldAction"
            style="height: fit-content;"
          >
            <template #[slotList[0].name]="{record}">
              <div class="line-item-list-action-btns">
                <div class="line-item-list-edit-delete-btn">
                  <fc-icon
                    v-if="canShowSummaryWidget"
                    group="dsm"
                    name="info"
                    size="16"
                    color="#1d384e"
                    v-tippy
                    data-arrow="true"
                    :title="$t('maintenance.calender.more_details')"
                    @click="toggleSummaryDetailsDialog(record)"
                    class="visibility-hide-actions pointer"
                  ></fc-icon>
                  <span
                    :class="{
                      'cursor-not-allowed disabled': disableActionableUIElements,
                    }"
                  >
                    <fc-icon
                      v-if="record.canShowEdit"
                      group="default"
                      name="edit-text"
                      size="16"
                      color="#1d384e"
                      v-tippy
                      data-arrow="true"
                      :title="$t('common._common.edit')"
                      @click="openForm(record)"
                      class="visibility-hide-actions pointer"
                      :class="{
                        'pointer-events-none': disableActionableUIElements,
                      }"
                    ></fc-icon>
                  </span>
                  <span
                    :class="{
                      'cursor-not-allowed disabled': disableActionableUIElements,
                    }"
                  >
                    <fc-icon
                      v-if="record.canShowDelete"
                      group="dsm"
                      name="delete"
                      size="16"
                      color="#1d384e"
                      v-tippy
                      data-arrow="true"
                      :title="$t('common._common.delete')"
                      @click="deleteRecord([record.id])"
                      class="visibility-hide-actions pointer"
                      :class="{
                        'pointer-events-none': disableActionableUIElements,
                      }"
                    ></fc-icon>
                  </span>
                </div>
                <slot name="additional-action-btns" :record="record"></slot>
              </div>
            </template>
            <template
              v-for="(x, slotName) in tableListScopedSlots"
              v-slot:[slotName]="context"
            >
              <slot :name="slotName" v-bind="context" />
            </template>
          </CommonList>
        </div>
        <ColumnCustomization
          :key="`column-customization-${moduleName}`"
          :visible.sync="showColumnSettings"
          :moduleName="moduleName"
          :viewName="viewname"
          :relatedViewDetail="viewDetail"
          :relatedMetaInfo="metaInfo"
          @refreshRelatedList="refreshView"
        ></ColumnCustomization>
        <slot v-if="!config.canHideFooter" name="footer">
          <div class="line-item-list-footer">
            <el-button
              v-if="config.canShowAddBtn && isEmpty(actionButtonList)"
              type="primary"
              class="line-item-list-footer-add-btn"
              @click="openForm"
              :disabled="disableActionableUIElements"
            >
              <div class="d-flex">
                <fc-icon
                  group="sign-symbols"
                  name="addition"
                  size="14"
                  color="currentColor"
                  class="mR8"
                ></fc-icon>
                {{ addBtnName }}
              </div>
            </el-button>
            <el-button
              v-else
              v-for="(btnObj, index) in actionButtonList"
              :key="`footer-btn-${index}`"
              type="primary"
              @click="handleClickActionBtn(btnObj.value)"
              class="line-item-list-footer-add-btn"
              :disabled="disableActionableUIElements"
            >
              <div class="d-flex" style="height: 14px;">
                <InlineSvg
                  v-if="isObject(btnObj.value)"
                  src="svgs/plans/list-alt-black-24-dp"
                  iconClass="icon icon-sm"
                  class="mR8"
                ></InlineSvg>
                <fc-icon
                  v-else
                  group="sign-symbols"
                  name="addition"
                  size="14"
                  color="currentColor"
                  class="mR8"
                ></fc-icon>
                {{ btnObj.label }}
              </div>
            </el-button>
            <slot name="footer-btns"></slot>
          </div>
        </slot>
      </template>
    </template>
    <el-dialog
      v-if="canShowSummaryDetailsDialog"
      :visible.sync="canShowSummaryDetailsDialog"
      :title="$t('maintenance.calender.more_details')"
      :before-close="toggleSummaryDetailsDialog"
      :fullscreen="true"
      :append-to-body="true"
      class="line-item-summary-details-dialog"
    >
      <SummaryFieldsWidget
        ref="summaryFieldsWidget"
        v-bind="$attrs"
        :key="`summary-field-widget-${moduleName}`"
        :details="currentRecord"
        :moduleName="moduleName"
        :disableAutoResize="true"
        :detailsLayoutProp="detailsLayoutProp"
        :siteList="siteList"
        class="pB20"
      ></SummaryFieldsWidget>
    </el-dialog>
    <slot
      v-if="showPopUpFormDialog"
      name="popup-form"
      :record="currentRecord"
      :recordId="currentRecordId"
    >
      <PopupLineItemForm
        :key="`popup-form-${moduleName}`"
        :config="formConfig"
        :moduleName="moduleName"
        :moduleDisplayName="moduleDisplayName"
        :dataId="currentRecordId"
        :dataObj="currentRecord"
        :recordConversionDetails="lineItemLookupDetails"
        :additionalParams="additionalParams"
        :selectedFormDetails="selectedFormDetails"
        :hideNotification="hideNotification"
        :hasPreviousDialog="!isEmpty(currentBtnDetails)"
        @openPrevious="openPreviousDialog"
        @onSave="refreshList"
        @onClose="closeForm"
      ></PopupLineItemForm>
    </slot>
    <slot v-if="showWizard" name="lineitem-wizard">
      <LineItemWizard
        :config="currentBtnDetails"
        :selectedLookupModuleId="selectedLookupModuleId"
        @onSave="handleSelectedLookup"
        @onCancel="closeWizard"
      ></LineItemWizard>
    </slot>
    <slot></slot>
  </div>
</template>
<script>
import CommonList from 'newapp/list/CommonList'
import PopupLineItemForm from './PopupLineItemForm.vue'
import LineItemWizard from './LineItemWizard.vue'
import SummaryFieldsWidget from '../field-details/SummaryFieldsWidget.vue'
import ColumnCustomization from '@/ColumnCustomization'
import AdvancedSearch from 'newapp/components/search/AdvancedSearchUI'
import Pagination from 'src/newapp/components/ListPagination'
import { CustomModuleData } from 'src/pages/custom-module/CustomModuleData'
import { getFieldOptions } from 'util/picklist'
import { FTags } from '@facilio/criteria'
import { API } from '@facilio/api'
import { getApp } from '@facilio/router'
import {
  isEmpty,
  isArray,
  isFunction,
  isNullOrUndefined,
  isObject,
} from '@facilio/utils/validation'

export default {
  props: [
    'config',
    'moduleName',
    'moduleDisplayName',
    'viewname',
    'widgetDetails',
    'additionalParams',
    'hideNotification',
  ],
  components: {
    CommonList,
    PopupLineItemForm,
    LineItemWizard,
    SummaryFieldsWidget,
    ColumnCustomization,
    AdvancedSearch,
    Pagination,
    FTags,
  },
  data() {
    return {
      recordList: [],
      recordCount: null,
      page: 1,
      isLoading: false,
      appliedFilter: {},
      viewDetail: {},
      viewLoading: false,
      searchText: null,
      selectedRecordCount: null,
      siteList: {},
      showPopUpFormDialog: false,
      currentRecordId: null,
      currentRecord: null,
      summaryDetailsLoading: false,
      detailsLayoutProp: null,
      showColumnSettings: false,
      metaInfo: {},
      canShowSummaryDetailsDialog: false,
      showWizard: false,
      currentBtnDetails: {},
      selectedLookupModuleId: null,
      isEmpty,
      isObject,
    }
  },
  async created() {
    await this.init()
  },
  computed: {
    modelDataClass() {
      let { modelDataClass } = this.config || {}
      return modelDataClass || CustomModuleData
    },
    canHideHeader() {
      return !!this.config?.canHideHeader
    },
    title() {
      let { moduleDisplayName, moduleName, widgetDetails } = this
      let { title } = widgetDetails || {}

      return (
        title ||
        this.$t('common.header.list_of_module_display_name', {
          moduleDisplayName: moduleDisplayName || moduleName,
        })
      )
    },
    perPage() {
      return this.widgetDetails?.perPage || 10
    },
    canShowPagination() {
      let { recordCount, recordList, appliedFilter } = this
      return (
        !isEmpty(recordCount) || !isEmpty(recordList) || !isEmpty(appliedFilter)
      )
    },
    emptyStateText() {
      let { moduleDisplayName, moduleName, widgetDetails } = this
      return (
        widgetDetails?.emptyStateText ||
        this.$t('common.products.no_module_available', {
          moduleName: moduleDisplayName || moduleName,
        })
      )
    },
    actionButtonList() {
      let { actionButtonList } = this.widgetDetails || {}

      return (actionButtonList || []).map(btn => {
        let { value, label } = btn || {}
        let btnObj =
          !isObject(value) && value === 'default_add' && isEmpty(label)
            ? { value, label: this.addBtnName }
            : btn

        return btnObj
      })
    },
    currentFilter() {
      let { appliedFilter, config } = this
      let { filters } = config || {}
      return { ...(filters || {}), ...(appliedFilter || {}) }
    },
    canShowSelection() {
      return this.selectedRecordCount && !isEmpty(this.selectedRecordCount)
    },
    loading() {
      return this.isLoading || this.viewLoading
    },
    formConfig() {
      let { currentRecordId, currentRecord, config } = this
      let {
        formTitle,
        modifyFieldPropsHook = () => {},
        onBlurHook = () => {},
        onWidgetChange = () => {},
      } = config || {}
      let modifyFieldProps = field =>
        modifyFieldPropsHook(field, { currentRecordId, currentRecord })

      return {
        formTitle,
        modifyFieldPropsHook: modifyFieldProps,
        onBlurHook,
        onWidgetChange,
      }
    },
    selectedFormDetails() {
      let { currentRecordId } = this
      let { formDetails } = this.config || {}
      let { defaultForm, addForm, editForm } = formDetails || {}

      if (!isEmpty(currentRecordId) && !isEmpty(editForm)) {
        return editForm || {}
      } else if (isEmpty(currentRecordId) && !isEmpty(addForm)) {
        return addForm || {}
      } else if (!isEmpty(defaultForm)) {
        return defaultForm || {}
      }
      return null
    },
    addBtnName() {
      let { moduleDisplayName, moduleName, config } = this
      let { addBtnText } = config || {}
      return addBtnText || `Add ${moduleDisplayName || moduleName}`
    },
    mainFieldObj() {
      let { fields } = this.viewDetail || {}
      let { field: mainField } =
        (fields || []).find(viewFld => viewFld?.field?.mainField) || {}
      return mainField || {}
    },
    canShowSearchAndFilter() {
      let { recordList, searchText, appliedFilter } = this

      return (
        !isEmpty(recordList) || !isEmpty(searchText) || !isEmpty(appliedFilter)
      )
    },
    canShowFilter() {
      return !this.config?.canHideFilter
    },
    canShowCustomButton() {
      return !!this.config?.canShowCustomButton
    },
    hideListSelect() {
      return this.disableActionableUIElements || !!this.config?.hideListSelect
    },
    checkSelection() {
      return this.config?.checkSelection
    },
    skipEditPermission() {
      let { skipModulePermission } = this.config || {}
      let { editPermission } = skipModulePermission || {}
      return !!editPermission
    },
    skipDeletePermission() {
      let { skipModulePermission } = this.config || {}
      let { deletePermission } = skipModulePermission || {}
      return !!deletePermission
    },
    canShowSummaryWidget() {
      let { summaryWidgetName } = this.widgetDetails || {}
      let { canHideSummaryWidget } = this.config || {}
      return !canHideSummaryWidget && summaryWidgetName
    },
    mainfieldAction() {
      let { mainfieldAction } = this.config || {}
      return isFunction(mainfieldAction) ? mainfieldAction : () => {}
    },
    isFilterEnabled() {
      let { page, searchText, appliedFilter } = this
      return page !== 1 || !isEmpty(searchText) || !isEmpty(appliedFilter)
    },
    slotList() {
      let { tableSlotList } = this.config || {}
      let defaultSlotList = [
        {
          name: 'additionalActions',
          isActionColumn: true,
          columnAttrs: {
            width: 130,
            class: 'visibility-visible-actions',
            fixed: 'right',
          },
        },
        ...(tableSlotList || []),
      ]
      return defaultSlotList
    },
    tableListScopedSlots() {
      let { $scopedSlots } = this
      let otherSlots = [
        'bulk-actions',
        'header-additional-actions',
        'header-additional-action-right',
        'empty-state',
        'empty-state-btn',
        'additional-action-btns',
        'footer',
        'footer-btns',
        'popup-form',
        'lineitem-wizard',
        'default',
      ]

      return Object.entries($scopedSlots).reduce(
        (tableListScopedSlots, [slotName]) => {
          if (!otherSlots.includes(slotName)) {
            tableListScopedSlots[slotName] = $scopedSlots[slotName]
          }
          return tableListScopedSlots
        },
        {}
      )
    },
    lineItemLookupDetails() {
      let { selectedLookupModuleId, currentBtnDetails } = this
      return { selectedLookupModuleId, currentBtnDetails }
    },
    disableActionableUIElements() {
      let { disableActionableUIElements = false } = this.config
      if (
        isEmpty(disableActionableUIElements) ||
        !disableActionableUIElements
      ) {
        return false
      } else {
        return true
      }
    },
  },
  watch: {
    moduleName: {
      handler: 'init',
      immediate: true,
    },
    page() {
      this.loadRecords(true)
    },
    searchText() {
      this.refreshRecordList(true)
    },
  },
  methods: {
    init() {
      this.loadViewDetails()
      this.refreshRecordList()
      this.loadMetaInfo()
      this.loadSummaryDetailLayout()
    },
    async refreshRecordList(force = false) {
      await this.loadRecords(force)
      await this.loadRecordCount(force)
    },
    async loadRecords(force = false) {
      let {
        moduleName,
        viewname,
        page,
        currentFilter,
        searchText,
        perPage,
      } = this
      let { getRecordList } = this.config || {}
      let { canShowEdit, canShowDelete } = this
      let recordList = []

      this.isLoading = true
      try {
        if (isFunction(getRecordList)) {
          recordList = await getRecordList({
            page,
            filters: currentFilter,
            search: searchText,
            force,
          })
        } else {
          let params = {
            moduleName,
            viewname,
            filters: currentFilter,
            page,
            perPage,
            force,
            search: searchText,
          }

          recordList = await this.modelDataClass.fetchAll(params)
        }
        if (isArray(recordList)) {
          this.recordList = (recordList || []).map(record => {
            return new this.modelDataClass({
              ...record,
              canShowEdit: canShowEdit(record),
              canShowDelete: canShowDelete(record),
            })
          })
          await this.loadSiteList()
        }
      } catch (errorMsg) {
        this.$message.error(errorMsg || 'Unable to fetch moduleList')
      }
      this.isLoading = false
    },
    async loadRecordCount(force = false) {
      let { moduleName, viewname, currentFilter, searchText } = this
      let { getRecordCount } = this.config || {}
      let recordCount = null

      try {
        if (isFunction(getRecordCount)) {
          recordCount = await getRecordCount({
            filters: currentFilter,
            search: searchText,
            force,
          })
        } else {
          let params = {
            moduleName,
            viewname,
            filters: currentFilter,
            force,
            search: searchText,
          }

          recordCount = await this.modelDataClass.fetchRecordsCount(params)
        }

        this.recordCount = recordCount
      } catch (errorMsg) {
        this.$message.error(errorMsg || 'Unable to fetch count')
      }
    },
    async loadViewDetails() {
      let { moduleName, viewname } = this
      let { getViewDetails } = this.config || {}
      let response = {}

      this.viewLoading = true
      try {
        if (isFunction(getViewDetails)) {
          response = await getViewDetails()
        } else {
          response = await API.get(`v2/views/${viewname}`, {
            moduleName,
          })
        }
        let { error, data } = response || {}

        if (error) {
          throw error
        } else {
          this.viewDetail = data?.viewDetail || {}
        }
      } catch (errorMsg) {
        this.$message.error(errorMsg || 'Cannot fetch view details')
      }
      this.viewLoading = false
    },
    async loadSiteList() {
      let { recordList } = this
      let siteIds = (recordList || []).map(record => record.siteId)
      let defaultIds = [...new Set(siteIds)]
      let perPage = defaultIds.length

      if (perPage === 0) return

      let { error, options } = await getFieldOptions({
        field: { lookupModuleName: 'site', skipDeserialize: true },
        defaultIds,
        perPage,
      })

      if (!error) {
        this.siteList = options || {}
      }
    },
    async loadMetaInfo() {
      if (this.config?.canHideColumnConfig) return

      let { moduleName } = this
      let { data, error } = await API.get('/module/meta', { moduleName })

      if (!error) {
        this.metaInfo = data?.meta || {}
      }
    },
    toggleSummaryDetailsDialog(record = null) {
      this.currentRecord = record
      this.canShowSummaryDetailsDialog = !this.canShowSummaryDetailsDialog
    },
    async loadSummaryDetailLayout() {
      let { moduleName, widgetDetails } = this
      let { summaryWidgetName } = widgetDetails || {}

      if (isEmpty(summaryWidgetName)) return
      this.summaryDetailsLoading = true

      let appId = (getApp() || {}).id
      let params = { moduleName, appId, widgetName: summaryWidgetName }
      let { data, error } = await API.get(
        'v2/customPage/summaryFieldWidget',
        params
      )

      if (!error) {
        this.detailsLayoutProp = data
      }
      this.summaryDetailsLoading = false
    },
    onCustomButtonSuccess() {
      this.loadRecords()
      this.clearSelection()
    },
    async refreshView() {
      this.showColumnSettings = false
      await this.loadViewDetails()
    },
    setAppliedfilter(filters) {
      this.appliedFilter = filters || {}
      this.refreshRecordList(true)
    },
    handleSelection(selectedList) {
      this.$emit('handleSelection', selectedList)
      this.selectedRecordCount = (selectedList || []).length
    },
    handleSelectedLookup(record) {
      let { id } = record || {}
      this.selectedLookupModuleId = id
      this.$emit('selectedLookupModuleRecord', record)
      this.openForm()
    },
    openForm(record) {
      let { id } = record || {}

      this.currentRecordId = id || null
      if (isEmpty(id) && !isEmpty(record)) {
        this.currentRecord = record
      }
      this.showPopUpFormDialog = true
      this.$nextTick(() => {
        this.showWizard = false
      })
    },
    closeForm() {
      this.currentRecordId = null
      this.currentRecord = null
      this.showPopUpFormDialog = false
      this.selectedLookupModuleId = null
      this.currentBtnDetails = {}
      this.clearSelection()
      this.$emit('selectedLookupModuleRecord', null)
    },
    closeWizard() {
      this.showWizard = false
      this.closeForm()
    },
    openPreviousDialog() {
      this.showWizard = true
      this.$nextTick(() => {
        this.showPopUpFormDialog = false
      })
    },
    handleClickActionBtn(btnValue) {
      this.$emit('clickedActionBtn', btnValue)

      let { lookupModuleName } = btnValue || {}

      if (!isEmpty(lookupModuleName)) {
        this.currentBtnDetails = btnValue
        this.showWizard = true
      } else this.showPopUpFormDialog = true
    },
    canShowEdit(record) {
      let { moduleName, config, skipEditPermission } = this
      let { isRecordEditable, canHideEdit } = config || {}
      let hasPermission = !skipEditPermission
        ? this.$hasPermission(`${moduleName}:EDIT,UPDATE`)
        : true
      let canEdit = record.canEdit() || false
      let customcanShowEditCallBack = isFunction(isRecordEditable)
        ? isRecordEditable(record)
        : true
      let canShowRecordEdit =
        hasPermission && canEdit && customcanShowEditCallBack && !canHideEdit

      return canShowRecordEdit
    },
    canShowDelete(record) {
      let { moduleName, config, skipDeletePermission } = this
      let { isRecordDeletable, canHideDelete } = config || {}
      let hasPermission = !skipDeletePermission
        ? this.$hasPermission(`${moduleName}:DELETE`)
        : true
      let customcanShowDeleteCallBack = isFunction(isRecordDeletable)
        ? isRecordDeletable(record)
        : true
      let canShowRecordDelete =
        hasPermission && customcanShowDeleteCallBack && !canHideDelete

      return canShowRecordDelete
    },
    async deleteRecord(idList) {
      let { moduleDisplayName, moduleName, config } = this
      let { deletePopupDetails } = config || {}
      let { title, message, rbDanger, rbLabel } = deletePopupDetails || {}
      let customTitle = !isNullOrUndefined(title)
        ? title
        : `${this.$t('custommodules.list.delete')} ${moduleDisplayName}`
      let customMessage = !isNullOrUndefined(message)
        ? message
        : `${this.$t(
            'custommodules.list.delete_confirmation'
          )} ${moduleDisplayName}?`
      let customBtnLabel = !isNullOrUndefined(rbLabel)
        ? rbLabel
        : this.$t('custommodules.list.delete')
      let value = await this.$dialog.confirm({
        title: customTitle,
        message: customMessage,
        rbDanger: !isNullOrUndefined(rbDanger) ? rbDanger : true,
        rbLabel: customBtnLabel,
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
          this.clearSelection()
          this.$emit('onDelete')
          await this.refreshRecordList(true)
        } catch (errorMsg) {
          this.$message.error(errorMsg)
          this.isLoading = false
        }
      }
    },
    refreshList(response) {
      this.$emit('onCreateOrUpdate', response)

      let { error } = response || {}
      !error && this.refreshRecordList(true)
    },
    clearSelection() {
      this.selectedRecordCount = null
      this.$emit('handleSelection', [])
      let lineItemListRef = this.$refs?.lineItemTable
      let tableRef = lineItemListRef?.$refs[`common-list-${this.moduleName}`]

      tableRef?.clearSelection()
    },
  },
}
</script>
<style lang="scss" scoped>
.line-item-list {
  display: flex;
  flex-direction: column;

  .line-item-selected-action-container {
    display: flex;
    align-items: center;
    justify-content: space-between;
    min-height: 50px;
    flex-shrink: 0;
    border-bottom: 1px solid #f0f0f0;

    .line-item-selected-items {
      display: flex;
      flex-direction: column;
      justify-content: center;
      border-left: 5px solid #3ab2c1;
      padding: 10px 16px;

      .line-item-selection-count {
        color: #324056;
        font-size: 18px;
        font-weight: 500;
        line-height: 18px;
      }
      .line-item-selection-text {
        color: #989faa;
        font-size: 14px;
        line-height: 14px;
        margin-top: 4px;
      }
    }
    .line-item-selection-close-btn {
      width: 20px;
      height: 20px;
      display: flex;
      margin-right: 24px;

      .close-icon {
        margin: auto;
        font-size: 18px;
        font-weight: bold;
      }
    }
  }
  .line-item-list-header-container {
    display: flex;
    align-items: center;
    min-height: 52px;
    flex-shrink: 0;
    border-bottom: 1px solid #f0f0f0;
    padding: 8px 24px;

    .line-item-wizard-filter-ftags {
      flex-shrink: 0;

      .line-item-wizard-filter-text {
        font-size: 10px;
        font-weight: 500;
        letter-spacing: 0.19px;
        color: #1d384e;
        line-height: 10px;
      }
      .f-filter-tags-container {
        margin: 0px;
        gap: 8px;
        padding: 4px 0px 0px;

        .btn-container .tag-btn {
          margin: 0px;
        }
      }
    }

    .line-item-list-header-title {
      font-size: 18px;
      font-weight: 500;
      color: #324056;
    }
    .line-item-list-header-action {
      display: flex;
      align-items: center;
      margin-left: auto;
      height: 40px;
    }
  }
  .line-item-list-empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    margin: auto;

    .line-item-list-empty-state-text {
      font-size: 16px;
      font-weight: 500;
      line-height: 1.5;
      letter-spacing: 0.27px;
      color: #324056;
      margin: 20px auto;
    }

    .line-item-list-empty-state-action-btn {
      padding: 13px 40px;
      border-radius: 4px;
      border-color: transparent;
      background-color: #3ab2c2;
      font-size: 14px;
      font-weight: 500;
      letter-spacing: 0.5px;
      color: #fff;

      &:hover {
        cursor: pointer;
        background-color: #3cbfd0;
      }
    }
  }
  .line-item-list-table {
    position: relative;
    height: fit-content;

    .line-item-list-column-custom-icon {
      position: absolute;
      top: 0;
      right: 0px;
      width: 45px;
      height: 40px;
      cursor: pointer;
      text-align: center;
      border-left: 1px solid #f2f5f6;
      z-index: 20;
      display: flex;
      background: #f8fafe;
    }
    .line-item-list-action-btns {
      display: flex;
      align-items: center;

      .line-item-list-edit-delete-btn {
        display: flex;
        flex-grow: 1;
        justify-content: space-between;
        align-items: center;
        margin-right: 10px;
      }
    }
  }
  .line-item-list-footer {
    display: flex;
    align-items: center;
    flex-shrink: 0;
    padding: 16px 24px;
    overflow: scroll;

    .line-item-list-footer-add-btn {
      border-radius: 4px;
      border: solid 1px #3ab2c2;
      background-color: #fff;
      font-size: 14px;
      font-weight: 500;
      color: #324056;
      padding: 10px 12px;

      &:hover {
        color: #fff;
        background-color: #3cbfd0;
      }

      .plus-icon {
        margin-right: 5px;
        font-weight: bold;
      }
    }
    .el-button + .el-button {
      margin-left: 16px;
    }
  }
}
</style>
<style lang="scss">
.line-item-list {
  .line-item-list-empty-state {
    .line-item-list-empty-state-action-btn-list {
      .el-button {
        padding: 12px 16px;
        border-color: transparent;
        background-color: #3ab2c2;
        letter-spacing: 0.5px;

        &:hover {
          z-index: auto;
          background-color: #3cbfd0;
        }
        &.el-dropdown__caret-button {
          padding: 12px;

          .el-dropdown__icon {
            margin: 0px;
          }
        }
      }
      &.el-dropdown .el-dropdown__caret-button::before {
        top: 0;
        bottom: 0;
      }
    }
  }
  .line-item-list-table {
    .el-table td {
      padding: 12px;

      &:first-of-type {
        padding: 9px 12px 9px 24px;
      }
      &:nth-last-child(-n + 2) {
        padding: 9px 12px;
      }
      .cell {
        line-height: 14px;
      }
    }
    .el-table th.is-leaf {
      padding: 12px;
      background-color: #f7faff;

      &:first-of-type {
        padding: 9px 12px 9px 24px;
      }
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
      line-height: 14px;
    }
    .el-table .el-table__row {
      min-height: auto;
      height: auto;
    }
    .hover-actions {
      visibility: hidden;
    }
    .el-table__body tr.hover-row > td .hover-actions {
      visibility: visible;
    }
    .custombtn-container .more-icon {
      padding: 0px;
    }
    .el-table_body tr.hover-row > td,
    .el-table--striped .el-table__body tr.el-table__row--striped.current-row td,
    .el-table__body tr.current-row > td,
    .el-table__body tr.hover-row.current-row > td,
    .el-table__body tr.hover-row.el-table__row--striped.current-row > td,
    .el-table__body tr.hover-row.el-table__row--striped > td,
    .el-table__body tr.hover-row > td {
      background-color: #fff !important;
    }
    .hover-row {
      box-shadow: 0 4px 8px -2px rgba(29, 56, 78, 0.11),
        0 0 1px 0 rgba(29, 56, 78, 0.31);
      background-color: #fff !important;
    }
    .main-field-column:hover {
      cursor: pointer;
      text-decoration: underline;
      text-underline-offset: 3px;
      color: #46a2bf;
    }
  }
}
.line-item-list-empty-state-action-btn-list-menu {
  padding: 0px;
  margin: 0px;

  .line-item-list-empty-state-action-btn-list-menu-item {
    width: 200px;
    height: 44px;
    padding: 16px;
    display: flex;
    align-items: center;
    letter-spacing: 0.2px;
    color: #324056;

    .el-dropdown-menu__item:focus,
    .el-dropdown-menu__item:not(.is-disabled):hover {
      background-color: #fafafa;
    }
  }
}
.line-item-summary-details-dialog {
  height: 100%;
  display: flex;

  .el-dialog {
    height: max-content;
    max-height: calc(100% - 64px);
    width: 80%;
    margin: auto;
    display: flex;
    flex-direction: column;

    .el-dialog__header {
      border-bottom: 1px solid #eee;
      height: 56px;
      padding: 20px 24px;
      flex-shrink: 0;

      .el-dialog__title {
        font-size: 16px;
        font-weight: 500;
        line-height: 16px;
        letter-spacing: 0.37px;
        color: #324056;
        text-transform: capitalize;
      }
    }
    .el-dialog__body {
      padding: 0px;
      overflow: scroll;
    }
  }
}
</style>
