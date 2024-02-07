<template>
  <el-dialog
    :visible="true"
    :append-to-body="true"
    :before-close="cancel"
    custom-class="related-list-popup-dialog"
    class="related-list-popup"
  >
    <template #title>
      <div class="related-list-popup-title-header">
        <div class="related-list-popup-title-text">{{ title }}</div>

        <div class="related-list-popup-title-header-filter">
          <div class="related-list-popup-title-header-filter-inbuilt">
            <MainFieldSearch
              v-if="!config.canHideSearch"
              :key="`search-${wizardModuleName}`"
              :search.sync="searchText"
              :mainFieldObj="mainFieldObj"
              @onSearch="loadRecords(true)"
              class="related-list-popup-title-header-filter-inbuilt-mainField"
            ></MainFieldSearch>
            <span v-if="canShowPagination" class="related-list-popup-separator"
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
    <div class="related-list-popup-container">
      <div
        v-if="!isEmpty(appliedFilter)"
        class="related-list-popup-filter-ftags"
      >
        <div class="related-list-popup-filter-text">
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
      <div
        v-else-if="isEmpty(recordList)"
        class="related-list-popup-empty-state"
      >
        <inline-svg
          src="svgs/list-empty"
          iconClass="icon icon-130"
        ></inline-svg>
        <div class="related-list-popup-empty-state-text">
          {{
            $t('common.products.no_module_available', {
              moduleName: wizardModuleDisplayName || wizardModuleName,
            })
          }}
        </div>
      </div>
      <div v-else class="related-list-table">
        <CommonList
          ref="relatedListTable"
          :key="`related-list-table-${wizardModuleName}`"
          :viewDetail="viewDetail"
          :records="recordList"
          :moduleName="wizardModuleName"
          :slotList="slotList"
          :hideListSelect="true"
          :tableRowClick="tableRowClick"
          :hideGlimpse="true"
          @mainField="mainfieldAction"
        ></CommonList>
      </div>
    </div>
  </el-dialog>
</template>
<script>
import LineItemWizard from './LineItemWizard.vue'
import MainFieldSearch from 'src/newapp/components/search/MainFieldSearch.vue'
import { isFunction } from '@facilio/utils/validation'

export default {
  extends: LineItemWizard,
  components: { MainFieldSearch },
  data: () => ({ searchText: null }),
  computed: {
    slotList() {
      return []
    },
    title() {
      let { wizardModuleDisplayName, wizardModuleName } = this
      return wizardModuleDisplayName || wizardModuleName
    },
    mainFieldObj() {
      let { fields } = this.viewDetail || {}
      let { field: mainField } =
        (fields || []).find(viewFld => viewFld?.field?.mainField) || {}
      return mainField || {}
    },
    mainfieldAction() {
      let { mainfieldAction } = this.config || {}
      return isFunction(mainfieldAction) ? mainfieldAction : () => {}
    },
  },
}
</script>
<style lang="scss">
.related-list-popup {
  display: flex;

  .related-list-popup-dialog {
    margin: auto;
    margin-top: auto !important;
    width: 80%;
    display: flex;
    flex-direction: column;
    max-height: calc(100% - 64px);
    height: 630px;

    .el-dialog__header {
      flex-shrink: 0;

      .related-list-popup-title-header {
        .related-list-popup-title-text {
          font-size: 16px;
          font-weight: 500;
          letter-spacing: 0.37px;
          color: #324056;
          margin-bottom: 12px;
        }
        .related-list-popup-title-header-filter {
          display: flex;
          align-items: center;
          justify-content: space-between;

          .related-list-popup-title-header-filter-inbuilt {
            display: flex;
            align-items: center;
            margin-left: auto;

            .related-list-popup-title-header-filter-inbuilt-mainField {
              .fc-input-full-border2 .el-input__inner,
              .fc-input-full-border-select2 .el-input__inner,
              .fc-input-full-border-select2 .el-textarea__inner,
              .fc-full-border-select-multiple2 .el-input .el-input__inner {
                height: auto !important;
                line-height: 28px !important;
              }
            }
          }
        }
        .related-list-popup-separator {
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

      .related-list-popup-container {
        display: flex;
        flex-direction: column;
        overflow: scroll;
        height: 100%;
        border-top: 1px solid #ebeef5;
        border-bottom: 1px solid #ebeef5;

        .related-list-popup-empty-state {
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          margin: auto;

          .related-list-popup-empty-state-text {
            font-size: 16px;
            font-weight: 500;
            line-height: 1.5;
            letter-spacing: 0.27px;
            color: #324056;
            margin: 20px auto;
          }
        }

        .related-list-popup-filter-ftags {
          border-top: 1px solid #e3e6e8;
          padding: 9px 24px;

          .related-list-popup-filter-text {
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
        .related-list-table {
          position: relative;
          height: fit-content;

          .related-list-popup-column-custom-icon {
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
        .related-list-popup-radio-btn {
          padding-left: 8px;
        }
        .el-table td {
          padding: 12px;

          .cell {
            line-height: 14px;
          }
        }
        .el-table td:first-of-type {
          padding-left: 24px;
        }
        .el-table th.is-leaf {
          padding: 12px;
          background-color: #f7faff;

          &:first-of-type {
            padding-left: 24px;
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
        .main-field-column:hover {
          cursor: pointer;
          text-decoration: underline;
          text-underline-offset: 3px;
          color: #46a2bf;
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
