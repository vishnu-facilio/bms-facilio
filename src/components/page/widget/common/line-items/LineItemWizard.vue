<template>
  <el-dialog
    :visible="true"
    :append-to-body="true"
    :close-on-click-modal="false"
    :before-close="cancel"
    :show-close="false"
    custom-class="line-item-wizard-dialog"
    class="line-item-wizard"
  >
    <template #title>
      <div class="line-item-wizard-title-header">
        <div class="line-item-wizard-title-text">{{ title }}</div>

        <div class="line-item-wizard-title-header-filter">
          <slot name="quick-filter"></slot>
          <div class="line-item-wizard-title-header-filter-inbuilt">
            <AdvancedSearch
              :key="`filter-${wizardModuleName}`"
              :moduleName="wizardModuleName"
              :moduleDisplayName="wizardModuleDisplayName"
              :hideQuery="true"
              :filterList="appliedFilter"
              @applyFilters="setAppliedfilter"
            ></AdvancedSearch>
            <span v-if="canShowPagination" class="line-item-wizard-separator"
              >|</span
            >
            <Pagination
              v-if="canShowPagination"
              :key="`pagination-${wizardModuleName}`"
              :total="recordCount"
              :currentPage.sync="page"
              :currentPageCount="(recordList || []).length"
              :perPage="perPage"
            ></Pagination>
          </div>
        </div>
      </div>
    </template>
    <div class="line-item-wizard-container">
      <div v-if="!isEmpty(appliedFilter)" class="line-item-wizard-filter-ftags">
        <div class="line-item-wizard-filter-text">
          {{ $t('commissioning.sheet.filter') }}
        </div>
        <FTags
          :key="`ftag-${wizardModuleName}`"
          :moduleName="wizardModuleName"
          :filters="appliedFilter"
          :hideQuery="true"
          :hideSaveAs="true"
          @updateFilters="setAppliedfilter"
          @clearFilters="setAppliedfilter({})"
          class="flex-shrink-0"
        />
      </div>
      <spinner v-if="showLoading" :show="showLoading" size="80"></spinner>
      <div v-else-if="isEmpty(recordList)" class="line-item-wizard-empty-state">
        <inline-svg
          src="svgs/list-empty"
          iconClass="icon icon-130"
        ></inline-svg>
        <div class="line-item-wizard-empty-state-text">
          {{
            $t('common.products.no_module_available', {
              moduleName: wizardModuleDisplayName || wizardModuleName,
            })
          }}
        </div>
      </div>
      <div v-else class="line-item-list-wizard">
        <div
          v-if="canShowColumnConfig"
          class="line-item-wizard-column-custom-icon"
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
          :key="`line-item-table-${wizardModuleName}`"
          :viewDetail="viewDetail"
          :records="recordList"
          :moduleName="wizardModuleName"
          :slotList="slotList"
          :hideListSelect="true"
          :tableRowClick="tableRowClick"
          :hideGlimpse="true"
        >
          <template #[slotList[0].name]="{record}">
            <el-radio
              :label="record.id"
              v-model="selectedRecordId"
              class="fc-radio-btn line-item-wizard-radio-btn"
            >
            </el-radio>
          </template>
        </CommonList>
      </div>
      <ColumnCustomization
        :key="`column-customization-${wizardModuleName}`"
        :visible.sync="showColumnSettings"
        :moduleName="wizardModuleName"
        :viewName="viewname"
        :relatedViewDetail="viewDetail"
        :relatedMetaInfo="metaInfo"
        @refreshRelatedList="refreshView"
      ></ColumnCustomization>
    </div>
    <template #footer>
      <el-button @click="cancel">{{ $t('common._common.cancel') }}</el-button>
      <el-button
        type="primary"
        :disabled="isEmpty(selectedRecordId)"
        @click="updateSelectedRecord"
        >{{ $t('common._common.next') }}</el-button
      >
    </template>
  </el-dialog>
</template>
<script>
import Pagination from 'src/newapp/components/ListPagination'
import CommonList from 'newapp/list/CommonList'
import ColumnCustomization from '@/ColumnCustomization'
import AdvancedSearch from 'newapp/components/search/AdvancedSearchUI'
import { CustomModuleData } from 'src/pages/custom-module/CustomModuleData'
import { FTags } from '@facilio/criteria'
import { API } from '@facilio/api'
import { isEmpty, isArray, isFunction } from '@facilio/utils/validation'

export default {
  props: ['config', 'selectedLookupModuleId'],
  components: {
    AdvancedSearch,
    Pagination,
    CommonList,
    FTags,
    ColumnCustomization,
  },
  data: () => ({
    appliedFilter: {},
    recordList: [],
    recordCount: null,
    page: 1,
    isListLoading: false,
    viewDetail: {},
    viewLoading: false,
    selectedRecordId: null,
    showColumnSettings: false,
    metaInfo: {},
    isEmpty,
  }),
  created() {
    this.loadViewDetails()
    this.refreshRecordList()
    this.loadMetaInfo()

    if (!isEmpty(this.selectedLookupModuleId))
      this.selectedRecordId = this.selectedLookupModuleId
  },
  computed: {
    title() {
      let { wizardModuleDisplayName, wizardModuleName } = this
      return `Select ${wizardModuleDisplayName || wizardModuleName}`
    },
    wizardModuleName() {
      return this.config?.lookupModuleName
    },
    wizardModuleDisplayName() {
      return this.config?.lookupModuleDisplayName
    },
    perPage() {
      return this.config?.perPage || 10
    },
    slotList() {
      return [
        {
          name: 'selection',
          isHardcodedColumn: true,
          columnAttrs: {
            width: 60,
            fixed: 'left',
          },
        },
      ]
    },
    modelDataClass() {
      return CustomModuleData
    },
    showLoading() {
      return this.viewLoading || this.isListLoading
    },
    canShowPagination() {
      let { recordCount, recordList, appliedFilter } = this
      return (
        !isEmpty(recordCount) || !isEmpty(recordList) || !isEmpty(appliedFilter)
      )
    },
    canShowColumnConfig() {
      return !!this.config?.canShowColumnConfig
    },
    viewname() {
      return this.config?.viewname || 'hidden-all'
    },
  },
  watch: {
    page() {
      this.loadRecords(true)
    },
  },
  methods: {
    async refreshRecordList(force = false) {
      await this.loadRecords(force)
      await this.loadRecordCount(force)
    },
    async loadRecords(force = false) {
      let {
        wizardModuleName: moduleName,
        viewname,
        page,
        appliedFilter,
        searchText,
        perPage,
      } = this
      let { getRecordList, additionalParams, additionalFilters } =
        this.config || {}
      let recordList = []
      let filters = { ...(appliedFilter || {}), ...(additionalFilters || {}) }

      this.isListLoading = true
      try {
        if (isFunction(getRecordList)) {
          recordList = await getRecordList({
            page,
            filters,
            search: searchText || null,
            force,
          })
        } else {
          let params = {
            moduleName,
            viewname,
            filters,
            page,
            perPage,
            force,
            search: searchText || null,
            ...(additionalParams || {}),
          }

          recordList = await this.modelDataClass.fetchAll(params)
        }
        this.recordList = isArray(recordList) ? recordList : []
      } catch (errorMsg) {
        this.$message.error(errorMsg || 'Unable to fetch moduleList')
      }
      this.isListLoading = false
    },
    async loadRecordCount(force = false) {
      let {
        wizardModuleName: moduleName,
        viewname,
        appliedFilter,
        searchText,
      } = this
      let { getRecordCount, additionalParams, additionalFilters } =
        this.config || {}
      let recordCount = null
      let filters = { ...(appliedFilter || {}), ...(additionalFilters || {}) }

      this.recordCount = null

      try {
        if (isFunction(getRecordCount)) {
          recordCount = await getRecordCount({
            filters,
            search: searchText,
            force,
          })
        } else {
          let params = {
            moduleName,
            viewname,
            filters,
            force,
            search: searchText,
            additionalParams,
          }

          recordCount = await this.modelDataClass.fetchRecordsCount(params)
        }

        this.recordCount = recordCount
      } catch (errorMsg) {
        this.$message.error(errorMsg || 'Unable to fetch count')
      }
    },
    async loadViewDetails() {
      let { wizardModuleName: moduleName, viewname } = this
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
    async loadMetaInfo() {
      if (!this.canShowColumnConfig) return

      let params = { moduleName: this.wizardModuleName }
      let { data, error } = await API.get('/module/meta', params)

      if (!error) {
        this.metaInfo = data?.meta || {}
      }
    },
    setAppliedfilter(filters) {
      this.appliedFilter = filters || {}
      this.page = 1
      this.refreshRecordList(true)
    },
    async refreshView() {
      this.showColumnSettings = false
      await this.loadViewDetails()
    },
    tableRowClick(record) {
      let { id } = record || {}
      this.selectedRecordId = id
    },
    updateSelectedRecord() {
      let { recordList, selectedRecordId } = this
      let record = recordList.find(record => record.id === selectedRecordId)

      this.$emit('onSave', record)
      this.$emit('onClose')
    },
    cancel() {
      this.$emit('onCancel')
    },
  },
}
</script>
<style lang="scss">
.line-item-wizard {
  display: flex;

  .line-item-wizard-dialog {
    margin: auto;
    margin-top: auto !important;
    width: 80%;
    display: flex;
    flex-direction: column;
    max-height: calc(100% - 64px);
    height: 630px;

    .el-dialog__header {
      flex-shrink: 0;

      .line-item-wizard-title-header {
        .line-item-wizard-title-text {
          font-size: 16px;
          font-weight: 500;
          letter-spacing: 0.37px;
          color: #324056;
          margin-bottom: 12px;
        }
        .line-item-wizard-title-header-filter {
          display: flex;
          align-items: center;
          justify-content: space-between;

          .line-item-wizard-title-header-filter-inbuilt {
            display: flex;
            align-items: center;
            margin-left: auto;
          }
        }
        .line-item-wizard-separator {
          padding: 0px 16px;
          opacity: 0.35;
          color: #324056;
          width: 1px;
        }
      }
    }
    .el-dialog__body {
      padding: 0px;
      flex-grow: 1;
      display: flex;
      flex-direction: column;
      overflow: auto;

      .line-item-wizard-container {
        display: flex;
        flex-direction: column;
        overflow: scroll;
        height: 100%;
        border-top: 1px solid #ebeef5;
        border-bottom: 1px solid #ebeef5;

        .line-item-wizard-empty-state {
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          margin: auto;

          .line-item-wizard-empty-state-text {
            font-size: 16px;
            font-weight: 500;
            line-height: 1.5;
            letter-spacing: 0.27px;
            color: #324056;
            margin: 20px auto;
          }
        }

        .line-item-wizard-filter-ftags {
          border-top: 1px solid #e3e6e8;
          padding: 9px 24px;

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
        .line-item-list-wizard {
          position: relative;
          height: fit-content;

          .line-item-wizard-column-custom-icon {
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
        }
        .common-list-container {
          overflow: scroll;
          flex-grow: 1;
          height: inherit;
        }
        .line-item-wizard-radio-btn {
          padding-left: 8px;
        }
        .el-table td {
          padding: 12px;

          .cell {
            line-height: 14px;
          }
        }
        .el-table td:first-of-type {
          padding: 9px 12px;
        }
        .el-table th.is-leaf {
          padding: 12px;
          background-color: #f7faff;
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
        .el-table--striped
          .el-table__body
          tr.el-table__row--striped.current-row
          td,
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
      }
    }
    .el-dialog__footer {
      flex-shrink: 0;
      padding: 16px;
    }
  }

  //Overriding existing css class style to new color

  .el-button {
    border-color: #3ab2c2;
    padding: 12px 16px;
    min-width: 96px;
  }
  .el-button--primary,
  .el-button--primary.is-disabled,
  .el-button--primary.is-disabled:active {
    background-color: #3ab2c2;
    border-color: #3ab2c2;
  }
  .el-button:hover,
  .el-button:focus,
  .el-button--primary:hover,
  .el-button--primary:focus,
  .el-button--primary.is-disabled:hover,
  .el-button--primary.is-disabled:focus {
    color: #fff;
    background-color: #3cbfd0;
    border-color: #3cbfd0;
  }

  .fc-radio-btn .el-radio__inner:hover {
    border-color: #3ab2c2;
  }
  .fc-radio-btn .el-radio__input.is-checked .el-radio__inner {
    border-color: #3ab2c2;
    background: #3ab2c2;
  }
}
</style>
