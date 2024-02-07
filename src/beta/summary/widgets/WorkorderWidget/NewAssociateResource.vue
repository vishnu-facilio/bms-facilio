<template>
  <FContainer>
    <spinner v-if="loading" :show="loading" size="80"></spinner>
    <template>
      <template v-if="isEmpty(currentRecordList)">
        <FContainer
          marginTop="containerXxLarge"
          padding="containerNone 268.5px"
          display="flex"
          justify-content="center"
          align-items="center"
        >
          <FContainer
            borderRadius="medium"
            border="1px"
            padding="containerMedium containerLarge"
            display="flex"
            flexDirection="column"
            align-items="center"
            gap="containerLarge"
          >
            <FButton
              justifyContent="space-around"
              appearance="secondary"
              size="large"
              @click="openForm"
            >
              <FText color="textMain" font-weight="bold">Add space/Asset</FText>
            </FButton>

            <FText appearance="captionReg12" color="textMain">
              Add one or multiple secondary assets and spaces to be resolved in
              this work order.</FText
            >
          </FContainer>
        </FContainer>
      </template>
      <template v-else>
        <portal
          :to="`title-${widget.id}-${widget.name}`"
          class="portal-alignment-ws"
        >
          <FContainer padding="containerXLarge">
            <FText appearance="headingMed14">Space & Asset</FText>
          </FContainer></portal
        >
        <portal
          :to="`action-${widget.id}-${widget.name}`"
          class="portal-alignment-ws action-ws"
        >
          <FContainer
            padding="containerXLarge"
            display="flex"
            justifyContent="space-between"
            alignItems="center"
          >
            <FContainer
              display="flex"
              alignItems="center"
              gap="sectionLarge"
              justifyContent="space-between"
            >
              <FContainer
                display="flex"
                alignItems="center"
                gap="containerXLarge"
              ></FContainer>
              <FContainer
                display="flex"
                justifyContent="flex-end"
                alignItems="center"
                gap="containerXLarge"
              >
                <FContainer>
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
                      <InlineSvg
                        src="svgs/dsm-filter"
                        class="d-flex cursor-pointer"
                        iconClass="icon icon-sm"
                      ></InlineSvg>
                      <div
                        v-if="!isEmpty(appliedFilter)"
                        class="dot-active-pink"
                      ></div>
                    </template>
                  </AdvancedSearch>
                </FContainer>
                <FDivider height="24px" width="0px" />

                <FContainer>
                  <FButton
                    justifyContent="space-around"
                    appearance="secondary"
                    size="large"
                    @click="openForm"
                  >
                    <FText color="textMain" font-weight="bold"
                      >Add space/Asset</FText
                    >
                  </FButton>
                </FContainer>
              </FContainer>
            </FContainer>
          </FContainer>
        </portal>
        <portal
          paddingTop="sectionLarge"
          class="portal-alignment-ws"
          ref="footer-portal"
          :class="{ height2px: !showFooterDivider }"
          :to="`footer-${widget.id}-${widget.name}`"
          @change="footerChange"
        >
          <FContainer v-if="!isEmpty(currentRecordList)">
            <FPagination
              v-if="!isEmpty(currentRecordList)"
              :total="recordCount"
              pageCount="5"
              :currentPage.sync="page"
              pageSize="5"
            ></FPagination>
          </FContainer>
        </portal>

        <!--commonlist-->

        <template>
          <CommonList
            v-if="!isEmpty(recordList)"
            :viewDetail="viewDetail"
            :records="recordList"
            :hideBorder="false"
            :slotList="slotList"
            selectType=""
          >
            <template #[slotList[0].name]="{record}">
              <div v-if="!record.id || record.canShowEdit">
                <FButton
                  appearance="tertiary"
                  size="small"
                  @click="openForm(record)"
                >
                  <InlineSvg
                    src="svgs/dsm-edit"
                    class="d-flex cursor-pointer"
                    iconClass="icon icon-sm"
                  ></InlineSvg>
                </FButton>
              </div>
              <div v-if="!record.id || record.canShowDelete">
                <FButton
                  appearance="tertiary"
                  size="small"
                  @click="deleteOrRemoveRecord(record.id || record.$uuid$)"
                  ><InlineSvg
                    src="svgs/dsm-delete"
                    class="d-flex cursor-pointer"
                    iconClass="icon icon-sm"
                  ></InlineSvg
                ></FButton>
              </div>
              <div>
                <FButton
                  appearance="tertiary"
                  size="small"
                  @click="openOverview(record)"
                >
                  <InlineSvg
                    src="svgs/dsm-info"
                    class="d-flex cursor-pointer"
                    iconClass="icon icon-sm"
                  ></InlineSvg
                ></FButton>
              </div>
            </template>
          </CommonList>
        </template>
      </template>

      <FModal
        title="Glimpse"
        :visible="isOpenOverview"
        :confirmLoading="loading"
        size="S"
        @ok="handleOk"
        @cancel="closeOverview"
        :hideFooter="true"
        centered="true"
      >
        <FContainer
          paddingBottom="containerSmall"
          paddingLeft="sectionSmall"
          paddingTop="containerXLarge"
        >
          <FText>More Details</FText>
        </FContainer>
        <FContainer
          display="flex"
          flex-direction="column"
          gap="12px"
          alignItems="flex-start"
          paddingTop="containerXxLarge"
          paddingLeft="sectionSmall"
          paddingBottom="containerXxLarge"
        >
          <FContainer display="flex" alignItems="flex-start" gap="16px">
            <FContainer
              display="flex"
              width="200px"
              flex-direction="column"
              justifyContent="center"
              gap="4px"
              alignItems="flex-start"
            >
              <FContainer
                display="flex"
                alignItems="center"
                gap="8px"
                align-self="stretch"
              >
                <FText
                  color="textMain"
                  line-height="130%"
                  appearance="captionMed12"
                  >Created by</FText
                >
              </FContainer>
              <FContainer
                display="flex"
                alignItems="center"
                paddingTop="containerMedium"
                align-self="stretch"
              >
                <FText
                  color="textDescription"
                  apperance="bodyReg14"
                  line-height="130%"
                  >{{ details.createdBy }}</FText
                >
              </FContainer>
            </FContainer>

            <FContainer
              display="flex"
              width="200px"
              flex-direction="column"
              justifyContent="center"
              gap="4px"
              alignItems="flex-start"
            >
              <FContainer
                display="flex"
                alignItems="center"
                gap="8px"
                align-self="stretch"
              >
                <FText
                  color="textMain"
                  line-height="130%"
                  appearance="captionMed12"
                  >Created Time</FText
                >
              </FContainer>
              <FContainer
                display="flex"
                alignItems="center"
                paddingTop="containerMedium"
                align-self="stretch"
              >
                <FText
                  color="textDescription"
                  apperance="bodyReg14"
                  line-height="130%"
                >
                  {{ details.sysCreatedTime | formatDate('DD-MM-YYYY HH:mm') }}
                </FText>
              </FContainer>
            </FContainer>
          </FContainer>
          <FContainer display="flex" alignItems="flex-start" padding-top="16px">
            <FContainer
              display="flex"
              width="200px"
              flex-direction="column"
              justifyContent="center"
              alignItems="flex-start"
            >
              <FContainer
                display="flex"
                alignItems="center"
                gap="8px"
                align-self="stretch"
              >
                <FText
                  color="textMain"
                  line-height="130%"
                  appearance="captionMed12"
                  >Modified by</FText
                >
              </FContainer>
              <FContainer
                display="flex"
                alignItems="center"
                paddingTop="containerMedium"
                align-self="stretch"
              >
                <FText
                  color="textDescription"
                  apperance="bodyReg14"
                  line-height="130%"
                  >{{ details.modifiedBy }}</FText
                >
              </FContainer>
            </FContainer>

            <FContainer
              display="flex"
              width="200px"
              flex-direction="column"
              justifyContent="center"
              gap="4px"
              alignItems="flex-start"
            >
              <FContainer
                display="flex"
                alignItems="center"
                gap="8px"
                align-self="stretch"
              >
                <FText
                  color="textMain"
                  line-height="130%"
                  appearance="captionMed12"
                  >Modified Time</FText
                >
              </FContainer>
              <FContainer
                display="flex"
                alignItems="center"
                paddingTop="containerMedium"
                align-self="stretch"
              >
                <FText
                  color="textDescription"
                  apperance="bodyReg14"
                  line-height="130%"
                  >{{
                    $options.filters.formatDate(details.sysModifiedTime, true)
                  }}</FText
                >
              </FContainer>
            </FContainer>
          </FContainer>
        </FContainer>
      </FModal>

      <ColumnCustomization
        :key="`column-customization-${moduleName}`"
        :visible.sync="showColumnSettings"
        :moduleName="moduleName"
        :viewName="viewname"
        :relatedViewDetail="viewDetail"
        :relatedMetaInfo="metaInfo"
        @refreshRelatedList="refreshView"
      ></ColumnCustomization>
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
  </FContainer>
</template>
<script>
import {
  FContainer,
  FButton,
  FText,
  FTable,
  FPagination,
  FIcon,
  FDivider,
  FModal,
} from '@facilio/design-system'
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
import PopupLineItemForm from 'src/components/page/widget/common/line-items/PopupLineItemForm.vue'
import SummaryFieldsWidget from 'src/components/page/widget/common/field-details/SummaryFieldsWidget.vue'
import ColumnCustomization from '@/ColumnCustomization'
import MainFieldSearch from 'src/newapp/components/search/MainFieldSearch.vue'
import AdvancedSearch from 'newapp/components/search/AdvancedSearch'
import Pagination from 'src/newapp/components/ListPagination'
import { v4 as uuid } from 'uuid'
import { CommonList } from '@facilio/ui/new-app'
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
    'widget',
  ],
  components: {
    FContainer,
    FButton,
    FText,
    LineItemTable,
    PopupLineItemForm,
    SummaryFieldsWidget,
    ColumnCustomization,
    MainFieldSearch,
    AdvancedSearch,
    Pagination,
    FTable,
    FPagination,
    FIcon,
    FDivider,
    CommonList,
    FModal,
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
      isOpenOverview: false,
      details: {},
      isEmpty,
      column: [
        { displayName: 'Sequence', name: 'sequence', id: 1 },
        { displayName: 'Asset', name: 'assetname', id: 2 },
        { displayName: 'Space', name: 'spacename', id: 3 },
        { displayName: 'Description', name: 'description', id: 4 },
      ],
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
    slotList() {
      return [
        {
          name: 'action',
          isActionColumn: true,
          columnAttrs: {
            width: 130,
            class: 'visibility-visible-actions',
            fixed: 'right',
          },
        },
      ]
    },

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
          if (isEmpty(recordList)) {
            this.$emit('resize-Widget')
          }
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
    getTableRecord() {
      let { recordList } = this

      recordList.forEach(item => {
        if (!isEmpty(item.asset)) {
          this.$set(item, 'assetname', item.asset.name)
        } else if (!isEmpty(item.space)) {
          this.$set(item, 'spacename', item.space.name)
        }
      })
      return recordList
    },
    openOverview(record) {
      this.details = record
      this.$set(
        this.details,
        'createdBy',
        this.details.sysCreatedBy?.name ? this.details.sysCreatedBy.name : '___'
      )
      this.$set(
        this.details,
        'modifiedBy',
        this.details.sysModifiedBy?.name
          ? this.details.sysModifiedBy.name
          : '___'
      )
      console.log(this.details.createdBy)
      console.log(this.details.modifiedBy)
      this.isOpenOverview = true
    },
    closeOverview() {
      this.isOpenOverview = false
      this.details = {}
    },
  },
}
</script>
