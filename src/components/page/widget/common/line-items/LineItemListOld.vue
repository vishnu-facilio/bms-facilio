<template>
  <div class="line-item-list">
    <div v-if="canShowSelection" class="line-item-selected-action-container">
      <div class="line-item-selected-items">
        <span class="line-item-selection-count">{{ selectedRecordCount }}</span>
        <span class="line-item-selection-text">
          {{ $t('common.line_item.items_selected') }}</span
        >
      </div>
      <div>
        <slot name="bulk-actions"></slot>
      </div>
      <div class="line-item-selection-close-btn">
        <i
          class="el-icon-close pointer close-icon"
          @click="clearSelection()"
        ></i>
      </div>
    </div>
    <template v-else>
      <div class="line-item-list-header-container">
        <el-radio-group
          v-if="!isEmpty(unSavedRecords)"
          v-model="currentList"
          @change="resetFilter"
          class="line-item-list-unsaved-switch"
        >
          <el-radio-button label="Draft"></el-radio-button>
          <el-radio-button label="Saved"></el-radio-button>
        </el-radio-group>
        <div v-else class="line-item-list-header-title">
          {{ title }}
        </div>
        <slot name="header-additional-actions"></slot>
        <div class="line-item-list-header-action">
          <template v-if="canShowSearchAndFilter">
            <MainFieldSearch
              v-if="!config.canHideSearch"
              :key="`search-${moduleName}`"
              :search.sync="searchText"
              :mainFieldObj="mainFieldObj"
              @onSearch="loadSearchList"
              :class="[canShowFilter && 'mR20']"
            ></MainFieldSearch>
            <AdvancedSearch
              v-show="canShowFilter"
              :key="`filter-${moduleName}`"
              :moduleName="moduleName"
              :moduleDisplayName="moduleDisplayName"
              :hideQuery="true"
              :onSave="setAppliedfilter"
              :filterList="appliedFilter"
            >
              <template #icon>
                <div class="line-item-list-header-filter">
                  <InlineSvg
                    src="svgs/filter"
                    class="d-flex cursor-pointer"
                    iconClass="icon icon-sm"
                  ></InlineSvg>
                  <div
                    v-if="!isEmpty(appliedFilter)"
                    class="dot-active-pink"
                  ></div>
                </div>
              </template>
            </AdvancedSearch>
            <span v-if="!isUnSavedList" class="separator mL10 mR10">|</span>
          </template>
          <Pagination
            v-if="!isUnSavedList"
            :key="`pagination-${moduleName}`"
            :total="recordCount"
            :currentPage.sync="page"
            :currentPageCount="(currentRecordList || []).length"
            :perPage="perPage"
          ></Pagination>
        </div>
      </div>
    </template>
    <spinner v-if="loading" :show="loading" size="80"></spinner>
    <template v-else>
      <template v-if="isEmpty(currentRecordList)">
        <div v-if="isFilterEnabled" class="line-item-list-empty-state">
          <inline-svg
            src="svgs/list-empty"
            iconClass="icon icon-130"
          ></inline-svg>
          <div class="line-item-list-empty-state-text">
            {{ $t('common.line_item.no_records_found') }}
          </div>
        </div>
        <slot v-else name="empty-state">
          <div class="line-item-list-empty-state">
            <inline-svg
              src="svgs/list-empty"
              iconClass="icon icon-130"
            ></inline-svg>
            <div class="line-item-list-empty-state-text">
              {{ emptyStateText }}
            </div>
            <slot name="empty-state-btn">
              <el-button
                v-if="!config.canHideAddBtn && isEmpty(emptyStateBtnList)"
                type="primary"
                @click="openForm"
                class="line-item-list-empty-state-action-btn"
              >
                {{ addBtnName }}
              </el-button>
              <template v-else-if="!isEmpty(emptyStateBtnList)">
                <el-button
                  v-if="emptyStateBtnList.length === 1"
                  type="primary"
                  @click="handleClickEmptyStateBtn(emptyStateBtnList[0].value)"
                  class="line-item-list-empty-state-action-btn"
                >
                  {{ emptyStateBtnList[0].label }}
                </el-button>
                <el-dropdown
                  v-else
                  split-button
                  type="primary"
                  @command="handleClickEmptyStateBtn"
                  @click="handleClickEmptyStateBtn(emptyStateBtnList[0].value)"
                  class="line-item-list-empty-state-action-btn-list"
                >
                  {{ emptyStateBtnList[0].label }}
                  <el-dropdown-menu
                    slot="dropdown"
                    class="line-item-list-empty-state-action-btn-list-menu"
                  >
                    <el-dropdown-item
                      v-for="(btnObj, index) in emptyStateBtnList.slice(1)"
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
        <div class="line-item-list-table" :style="tableHeight">
          <div
            v-if="!config.canHideColumnConfig"
            class="line-item-list-column-custom-icon"
            @click="showColumnSettings = true"
          >
            <InlineSvg
              src="svgs/column-customization"
              class="icon margin-auto"
              iconStyle="fill: #969696;"
            />
          </div>
          <div
            v-if="hasContentInLeft"
            class="line-item-list-table-shadow-left"
          ></div>
          <div
            v-if="hasContentInRight"
            class="line-item-list-table-shadow-right"
          ></div>
          <LineItemTable
            ref="lineItemTable"
            :key="`line-item-table-${moduleName}`"
            :records="currentRecordList"
            :viewFields="customizedViewFields"
            :config="lineItemTableConfig"
            :siteList="siteList"
            @selection-change="handleSelection"
            @toggleAccordion="autoResize"
            @mainField="mainfieldAction"
          >
            <template #action-buttons="{row}">
              <slot name="action-buttons" :row="row">
                <div class="line-item-list-action-btns">
                  <div class="line-item-list-edit-delete-btn">
                    <span
                      v-if="!row.record.id || row.record.canShowEdit"
                      @click="openForm(row.record)"
                    >
                      <inline-svg
                        src="svgs/edit"
                        class="pointer edit-icon-color visibility-hide-actions"
                        iconClass="icon icon-sm mR20"
                      ></inline-svg>
                    </span>
                    <span
                      v-if="!row.record.id || row.record.canShowDelete"
                      @click="
                        deleteOrRemoveRecord(row.record.id || row.record.$uuid$)
                      "
                    >
                      <inline-svg
                        src="svgs/delete"
                        class="pointer edit-icon-color visibility-hide-actions"
                        iconClass="icon icon-sm"
                      ></inline-svg>
                    </span>
                  </div>
                  <slot
                    name="additional-action-btns"
                    :record="row.record"
                  ></slot>
                </div>
              </slot>
            </template>
            <template #accordion="{row}">
              <slot name="accordion" :row="row">
                <div :style="{ width: getOffSetWidth, height: maxHeight }">
                  <slot
                    v-if="canShowInLineForm && showInLineFormDialog"
                    name="in-line-form"
                    :record="row.record"
                    :recordId="currentRecordId"
                    :recordIndex="currentUnSavedRecordIndex"
                  >
                    <PopupLineItemForm
                      :key="`inline-form-${moduleName}`"
                      :config="formConfig"
                      :moduleName="moduleName"
                      :moduleDisplayName="moduleDisplayName"
                      :dataId="currentRecordId"
                      :dataObj="currentRecord"
                      :additionalParams="additionalParams"
                      :selectedFormDetails="selectedFormDetails"
                      :hideNotification="hideNotification"
                      @onSave="refreshList"
                      @onClose="closeForm"
                    ></PopupLineItemForm>
                  </slot>
                  <slot
                    v-else
                    name="summary-field-widget"
                    :record="row.record"
                    :detailsLayoutProp="detailsLayoutProp"
                    :siteList="siteList"
                    :resizeListWidget="resizeListWidget"
                  >
                    <SummaryFieldsWidget
                      ref="summaryFieldsWidget"
                      v-bind="$attrs"
                      :key="`summary-field-widget-${moduleName}`"
                      :details="row.record"
                      :moduleName="moduleName"
                      :disableAutoResize="true"
                      :detailsLayoutProp="detailsLayoutProp"
                      :siteList="siteList"
                      @autoResize="resizeListWidget"
                      class="line-item-list-summary-details"
                    ></SummaryFieldsWidget>
                  </slot>
                </div>
              </slot>
            </template>
          </LineItemTable>
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
        <slot name="footer">
          <div v-if="!config.canHideFooter" class="line-item-list-footer">
            <el-button
              v-if="!config.canHideAddBtn"
              type="primary"
              class="line-item-list-footer-add-btn"
              @click="openForm"
            >
              <i class="el-icon-plus plus-icon"></i>
              {{ addBtnName }}
            </el-button>
            <slot name="footer-btns"></slot>
          </div>
        </slot>
      </template>
    </template>
    <slot
      v-if="showPopUpFormDialog"
      name="popup-form"
      :record="currentRecord"
      :recordId="currentRecordId"
      :recordIndex="currentUnSavedRecordIndex"
    >
      <PopupLineItemForm
        :key="`popup-form-${moduleName}`"
        :config="formConfig"
        :moduleName="moduleName"
        :moduleDisplayName="moduleDisplayName"
        :dataId="currentRecordId"
        :dataObj="currentRecord"
        :additionalParams="additionalParams"
        :selectedFormDetails="selectedFormDetails"
        :hideNotification="hideNotification"
        @onSave="refreshList"
        @onClose="closeForm"
      ></PopupLineItemForm>
    </slot>
    <slot></slot>
  </div>
</template>
<script>
import { NewLineItemTable as LineItemTable } from '@facilio/ui/app'
import {
  isEmpty,
  isArray,
  isFunction,
  isNullOrUndefined,
} from '@facilio/utils/validation'
import { CustomModuleData } from 'src/pages/custom-module/CustomModuleData'
import { API } from '@facilio/api'
import { getApp } from '@facilio/router'
import { getFieldOptions } from 'util/picklist'
import PopupLineItemForm from './PopupLineItemForm.vue'
import SummaryFieldsWidget from '../field-details/SummaryFieldsWidget.vue'
import ColumnCustomization from '@/ColumnCustomization'
import MainFieldSearch from 'src/newapp/components/search/MainFieldSearch.vue'
import AdvancedSearch from 'newapp/components/search/AdvancedSearch'
import Pagination from 'src/newapp/components/ListPagination'
import { v4 as uuid } from 'uuid'

export default {
  props: [
    'config',
    'moduleName',
    'moduleDisplayName',
    'viewname',
    'widgetDetails',
    'additionalParams',
    'unSavedRecords',
    'layoutParams',
    'resizeWidget',
    'calculateDimensions',
    'hideNotification',
  ],
  components: {
    LineItemTable,
    PopupLineItemForm,
    SummaryFieldsWidget,
    ColumnCustomization,
    MainFieldSearch,
    AdvancedSearch,
    Pagination,
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
      showInLineFormDialog: false,
      showPopUpFormDialog: false,
      currentRecordId: null,
      currentRecord: null,
      currentUnSavedRecordId: null,
      currentUnSavedRecordIndex: -1,
      summaryDetailsLoading: false,
      detailsLayoutProp: null,
      hasExpanded: false,
      currentList: 'Saved',
      showColumnSettings: false,
      metaInfo: {},
      hasContentInLeft: null,
      hasContentInRight: null,
      isEmpty,
    }
  },
  async created() {
    await this.init()
  },
  beforeDestroy() {
    let tableElement = document.querySelector('.el-table__body-wrapper')
    tableElement &&
      tableElement.removeEventListener('scroll', this.handleTableShadow)
  },
  computed: {
    modelDataClass() {
      return CustomModuleData
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
    emptyStateText() {
      let { moduleDisplayName, moduleName, widgetDetails } = this
      return (
        widgetDetails?.emptyStateText ||
        this.$t('common.products.no_module_available', {
          moduleName: moduleDisplayName || moduleName,
        })
      )
    },
    emptyStateBtnList() {
      let { emptyStateBtnList } = this.widgetDetails || {}

      return (emptyStateBtnList || []).map(btn => {
        let { value, label } = btn || {}
        let btnObj =
          value === 'default_add' && isEmpty(label)
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
    canShowInLineForm() {
      let { expand, formType } = this.lineItemTableConfig || {}
      return expand && formType === 'IN_LINE_FORM'
    },
    lineItemTableConfig() {
      let { config, indexMethod, widgetDetails } = this
      let { summaryWidgetName } = widgetDetails || {}
      let { expand } = config || {}

      return {
        ...(config || {}),
        expand: expand && !isEmpty(summaryWidgetName) ? true : false,
        indexMethod,
        dateformat: this.$dateformat,
        timezone: this.$timezone,
        timeformat: this.$timeformat,
      }
    },
    loading() {
      return this.isLoading || this.viewLoading || this.summaryDetailsLoading
    },
    formConfig() {
      let { currentRecordId, config } = this
      let {
        formType,
        formTitle,
        modifyFieldPropsHook = () => {},
        onBlurHook = () => {},
        onWidgetChange = () => {},
      } = config || {}

      formType = isEmpty(currentRecordId) ? 'POP_UP_FORM' : formType
      return {
        formType,
        formTitle,
        modifyFieldPropsHook,
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
    getOffSetWidth() {
      return !isEmpty(this.$refs?.lineItemTable)
        ? `${this.$refs?.lineItemTable.$el.offsetWidth}px`
        : null
    },
    maxHeight() {
      return this.config?.accordionMaxHeight || null
    },
    viewFields() {
      let { fields } = this.viewDetail || {}
      return fields || []
    },
    mainFieldObj() {
      let { field: mainField } =
        this.viewFields.find(viewFld => viewFld?.field?.mainField) || {}
      return mainField || {}
    },
    customizedViewFields() {
      let { viewFields } = this
      let mainFieldObjArray = (viewFields || []).filter(
        fld => fld?.field?.mainField
      )
      let otherViewFields = (viewFields || []).filter(
        fld => !fld?.field?.mainField
      )
      let customizedViewFields = [
        ...mainFieldObjArray,
        ...otherViewFields,
      ].filter(fld => !isEmpty(fld))

      return customizedViewFields
    },
    canShowSearchAndFilter() {
      let { recordList, isUnSavedList, searchText, appliedFilter } = this

      return (
        isUnSavedList ||
        (!isUnSavedList && !isEmpty(recordList)) ||
        !isEmpty(searchText) ||
        !isEmpty(appliedFilter)
      )
    },
    canShowFilter() {
      let { isUnSavedList, config } = this
      return !(isUnSavedList || config.canHideFilter)
    },
    isUnSavedList() {
      return this.currentList === 'Draft'
    },
    currentRecordList() {
      let { isUnSavedList, recordList, filteredUnSavedRecords } = this
      return !isUnSavedList ? recordList : filteredUnSavedRecords
    },
    filteredUnSavedRecords() {
      let { unSavedRecordsWithUUId, searchText, mainFieldObj } = this

      if (!isEmpty(searchText)) {
        let { name } = mainFieldObj || {}
        return (unSavedRecordsWithUUId || []).filter(record =>
          (record[name]?.toLowerCase() || '').includes(searchText.toLowerCase())
        )
      }
      return unSavedRecordsWithUUId || []
    },
    unSavedRecordsWithUUId() {
      return (this.unSavedRecords || []).map(record => ({
        ...record,
        $uuid$: uuid(),
      }))
    },
    tableHeight() {
      return {
        height: isEmpty(this.unSavedRecords)
          ? 'calc(100% - 134px)'
          : 'calc(100% - 184px)',
      }
    },
    mainfieldAction() {
      let { mainfieldAction } = this.config || {}
      return isFunction(mainfieldAction) ? mainfieldAction : () => {}
    },
    isFilterEnabled() {
      let { page, searchText, appliedFilter } = this
      return page !== 1 || !isEmpty(searchText) || !isEmpty(appliedFilter)
    },
  },
  watch: {
    moduleName: {
      handler: 'init',
      immediate: true,
    },
    page() {
      if (!this.isUnSavedList) this.loadRecords(true)
    },
    unSavedRecords(newVal) {
      if (isEmpty(newVal)) {
        this.currentList = 'Saved'
        this.resetFilter()
      } else if (this.isUnSavedList) {
        this.clearSelection()
      }
    },
  },
  methods: {
    init() {
      this.loadViewDetails()
      this.refreshRecordList()
      this.loadSummaryDetailLayout()
      this.loadMetaInfo()
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
          this.recordList = (recordList || []).map(record => ({
            ...record,
            canShowEdit: canShowEdit(record),
            canShowDelete: canShowDelete(record),
          }))
          await this.loadSiteList()
        }
      } catch (errorMsg) {
        this.$message.error(errorMsg || 'Unable to fetch moduleList')
      }
      this.isLoading = false
      this.addScrollEventListener()
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
    async loadSummaryDetailLayout() {
      let { moduleName, widgetDetails, config } = this
      let { summaryWidgetName } = widgetDetails || {}
      let { expand } = config || {}

      if (!expand || isEmpty(summaryWidgetName)) return
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
    async loadMetaInfo() {
      if (this.config?.canHideColumnConfig) return

      let { moduleName } = this
      let { data, error } = await API.get('/module/meta', { moduleName })

      if (!error) {
        this.metaInfo = data?.meta || {}
      }
    },
    async refreshView() {
      this.showColumnSettings = false
      await this.loadViewDetails()
      this.addScrollEventListener()
    },
    setAppliedfilter({ filters }) {
      this.appliedFilter = filters || {}
      this.refreshRecordList(true)
    },
    handleSelection(selectedList) {
      let selectedRecordIndices = []
      let selectedRecordList = (selectedList || []).map(list => list.record)

      if (this.isUnSavedList) {
        let selectedRecordUUId = selectedRecordList.map(record => record.$uuid$)

        selectedRecordIndices = this.unSavedRecordsWithUUId
          .map((record, index) => {
            if (selectedRecordUUId.includes(record.$uuid$)) return index
            else return null
          })
          .filter(index => !isNullOrUndefined(index))
        selectedRecordList = (selectedRecordList || []).map(record => {
          delete record.$uuid$
          return record
        })
      }
      this.$emit('handleSelection', {
        recordList: selectedRecordList,
        currentTab: this.currentList,
        selectedRecordIndices,
      })
      this.selectedRecordCount = (selectedList || []).length
    },
    openForm(record) {
      let { id, $uuid$ } = record || {}

      this.currentRecordId = id || null
      this.currentUnSavedRecordId = $uuid$ || null
      if (isEmpty(id) && !isEmpty(record)) {
        let recordObj = { ...record }

        delete recordObj.$uuid$
        this.currentRecord = recordObj
        this.currentUnSavedRecordIndex = (
          this.unSavedRecordsWithUUId || []
        ).findIndex(record => record.$uuid$ === $uuid$)
      }
      if (this.canShowInLineForm && !isEmpty(record))
        this.showInLineFormDialog = true
      else this.showPopUpFormDialog = true
    },
    closeForm() {
      this.currentRecordId = null
      this.currentRecord = null
      this.currentUnSavedRecordId = null
      this.currentUnSavedRecordIndex = -1
      this.showInLineFormDialog = false
      this.showPopUpFormDialog = false
      this.clearSelection()
    },
    handleClickEmptyStateBtn(btnValue) {
      if (btnValue === 'default_add') this.openForm()
      else this.$emit('emptyStateBtn', btnValue)
    },
    canShowEdit(record) {
      let { moduleName, config } = this
      let { isRecordEditable, canHideEdit } = config || {}
      let hasPermission = this.$hasPermission(`${moduleName}:EDIT,UPDATE`)
      let canEdit = record.canEdit() || false
      let customcanShowEditCallBack = isFunction(isRecordEditable)
        ? isRecordEditable(record)
        : true
      let canShowRecordEdit =
        hasPermission && canEdit && customcanShowEditCallBack && !canHideEdit

      return canShowRecordEdit
    },
    canShowDelete(record) {
      let { moduleName, config } = this
      let { isRecordDeletable, canHideDelete } = config || {}
      let hasPermission = this.$hasPermission(`${moduleName}:DELETE`)
      let customcanShowDeleteCallBack = isFunction(isRecordDeletable)
        ? isRecordDeletable(record)
        : true
      let canShowRecordDelete =
        hasPermission && customcanShowDeleteCallBack && !canHideDelete

      return canShowRecordDelete
    },
    deleteOrRemoveRecord(id) {
      if (!this.isUnSavedList) this.deleteRecord([id])
      else {
        let { unSavedRecordsWithUUId, unSavedRecords } = this
        let index = (unSavedRecordsWithUUId || []).findIndex(
          record => record.$uuid$ === id
        )

        if (!isNullOrUndefined(index) && index !== -1) {
          unSavedRecords.splice(index, 1)
          this.$emit('update:unSavedRecords', unSavedRecords)
        }
      }
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
    refreshList() {
      this.$emit('onCreateOrUpdate')
      if (this.isUnSavedList) {
        this.deleteOrRemoveRecord(this.currentUnSavedRecordId)
      }
      if (!this.isUnSavedList || isEmpty(this.unSavedRecords)) {
        this.refreshRecordList(true)
      }
    },
    resetFilter() {
      let hasSearchText = !isEmpty(this.searchText)

      this.page = 1
      this.appliedFilter = {}
      this.searchText = null
      this.clearSelection(true)
      if (!(this.isUnSavedList || hasSearchText)) this.refreshRecordList(true)
    },
    clearSelection(disableClearSelectionHandler) {
      this.selectedRecordCount = null
      this.$emit('handleSelection', {
        recordList: [],
        currentTab: this.currentList,
        selectedRecordIndices: [],
      })
      if (!disableClearSelectionHandler) {
        this.$refs?.lineItemTable?.clearSelectionHandler()
      }
    },
    indexMethod(index) {
      let { page, perPage } = this
      return page * perPage + index - perPage + 1
    },
    resizeListWidget(container) {
      let dimensions = { h: 0 }

      if (container) {
        let height = container.$el.scrollHeight + 60
        let width = container.$el.scrollWidth

        dimensions = this.calculateDimensions({ height, width })
      }

      let { h } = dimensions || {}
      let params = {}
      let defaultWidgetHeight = (this.layoutParams || {}).h || 7
      let totalHeight = h + defaultWidgetHeight

      if (totalHeight <= defaultWidgetHeight) {
        params = { height: defaultWidgetHeight }
      } else {
        params = { h: totalHeight }
      }
      this.resizeWidget(params)
    },
    autoResize(expandedRows) {
      let hasExpanded = !isEmpty(expandedRows)
      if (this.hasExpanded === hasExpanded) return

      this.hasExpanded = hasExpanded
      if (!hasExpanded) this.resizeListWidget()
    },
    addScrollEventListener() {
      this.$nextTick(() => {
        this.hasContentInLeft = null
        this.hasContentInRight = null

        // Since this block called before table rendered, used setTimeOut with 0sec
        setTimeout(() => {
          let tableElement = document.querySelector('.el-table__body-wrapper')

          if (tableElement) {
            tableElement.addEventListener('scroll', this.handleTableShadow, {
              passive: true,
            })
          }
        }, 0)
      })
    },
    handleTableShadow() {
      let tableElement = this.$refs?.lineItemTable?.$el || null

      if (!tableElement) {
        this.hasContentInLeft = null
        this.hasContentInRight = null
        return
      }

      let lineItemTableElementDetails = tableElement.getBoundingClientRect()
      let tableBodyElementArray = tableElement.getElementsByClassName(
        'el-table__body'
      )
      let tableBodyElement = !isEmpty(tableBodyElementArray)
        ? tableBodyElementArray[0]
        : null
      let actualTableBodyElementDetails = tableBodyElement
        ? tableBodyElement.getBoundingClientRect()
        : null

      let { left: tableLeft, right: tableRight } =
        lineItemTableElementDetails || {}
      let { left: tableBodyLeft, right: tableBodyRight } =
        actualTableBodyElementDetails || {}

      this.hasContentInLeft =
        tableBodyLeft && tableLeft && tableBodyLeft < tableLeft
      this.hasContentInRight =
        tableBodyRight && tableRight && tableBodyRight > tableRight
    },
    loadSearchList() {
      if (!this.isUnSavedList) this.refreshRecordList(true)
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

      .line-item-list-header-filter {
        position: relative;

        .dot-active-pink {
          position: absolute;
          top: -4px;
          right: -8px;
        }
      }
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
      }
    }
  }
  .line-item-list-table {
    flex-grow: 1;
    position: relative;

    .line-item-list-column-custom-icon {
      position: absolute;
      top: 0;
      right: 0px;
      width: 45px;
      height: 43px;
      cursor: pointer;
      text-align: center;
      border-left: 1px solid #f2f5f6;
      z-index: 20;
      display: flex;
      background: #f8fafe;
    }
    .line-item-list-table-shadow-left,
    .line-item-list-table-shadow-right {
      position: absolute;
      z-index: 1;
    }
    .line-item-list-table-shadow-left {
      height: 100%;
      width: 50px;
      left: 0;
      background-image: linear-gradient(to right, white, transparent);
    }
    .line-item-list-table-shadow-right {
      height: 100%;
      width: 50px;
      right: 0;
      background-image: linear-gradient(to left, white, transparent);
    }

    .line-item-list-action-btns {
      display: flex;
      align-items: center;

      .line-item-list-edit-delete-btn {
        display: flex;
        align-items: center;
        margin-right: 10px;
      }
    }
    .line-item-list-summary-details {
      min-height: 200px;
      overflow: scroll;
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
      border: solid 1px #38b2c2;
      background-color: #fff;
      font-size: 14px;
      font-weight: 500;
      color: #324056;

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
  .line-item-list-header-container {
    .line-item-list-unsaved-switch.el-radio-group {
      .el-radio-button.is-active {
        .el-radio-button__inner {
          letter-spacing: 0.2px;
          border-color: #3ab2c1;
          background-color: #f7feff;
          color: #324056;
        }
      }
      .el-radio-button__inner {
        padding: 10px 26px;

        &:hover {
          color: #324056;
        }
      }
    }
  }
  .line-item-list-empty-state {
    .line-item-list-empty-state-action-btn-list {
      .el-button {
        padding: 12px 16px;
        border-color: transparent;
        background-color: #3ab2c2;
        letter-spacing: 0.5px;

        &:hover {
          z-index: auto;
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
</style>
