<template>
  <div class="related-list-container" ref="relatedListContainer">
    <div class="related-list-header">
      <div class="header justify-content-space">
        <template>
          <FLookupFieldWrapper
            ref="mainFieldSearchInput"
            v-if="showMainFieldSearch"
            v-model="searchText"
            :field="lineItemLookupData(lookupModuleName)"
            class="fc-input-full-border2 width-auto mL-auto"
            suffix-icon="el-icon-search"
            @recordSelected="refreshRelatedList()"
          ></FLookupFieldWrapper>
          <span v-else class="self-center mL-auto" @click="openMainFieldSearch">
            <inline-svg
              v-if="hideSearchField"
              src="svgs/search"
              class="vertical-middle cursor-pointer"
              iconClass="icon icon-sm mT5 mR5 search-icon"
            ></inline-svg>
          </span>
          <span v-if="showSeparator" class="separator self-center">|</span>
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
          {{
            `${$t('common.products.no_mod_available')} ${moduleName} ${$t(
              'common._common.available'
            )}`
          }}
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
          style="width: 100%"
          :fit="true"
          height="400px"
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
              <div
                v-else-if="(field.field || {}).displayType == 'MULTI_CURRENCY'"
              >
                <CurrencyPopOver
                  class="d-flex flex-row-reverse"
                  :field="{
                    displayValue: getCurrencyInDecimalValue(
                      scope.row[field.name],
                      { decimalPlaces: 2 }
                    ),
                  }"
                  :details="scope.row"
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
      :visibility.sync="showStateTransitionComponent"
      :moduleName="moduleName"
      :record="selectedRecord"
      :updateUrl="getUpdateUrl"
      :transformFn="transformFormData"
      @currentState="() => {}"
      @transitionSuccess="
        () => {
          ;(showStateTransitionComponent = false), refreshRelatedList()
        }
      "
      @transitionFailure="() => {}"
    ></StateTransitionComponent>
    <ColumnCustomization
      :visible.sync="canShowColumnCustomization"
      :moduleName="moduleName"
      :columnConfig="columnCustomizationConfig"
      :relatedViewDetail="viewDetail"
      :relatedMetaInfo="currentMetaInfo"
      :viewName="'hidden-all'"
      @refreshRelatedList="refreshRelatedList"
    />
  </div>
</template>
<script>
import RelatedListWidget from '@/page/widget/common/RelatedListWidget'
import FLookupFieldWrapper from '@/FLookupFieldWrapper'
import { isEmpty } from '@facilio/utils/validation'
import Constants from 'util/constant'
import CurrencyPopOver from 'src/pages/setup/organizationSetting/currency/CurrencyPopOver.vue'
import { getCurrencyInDecimalValue } from 'src/pages/setup/organizationSetting/currency/CurrencyUtil.js'

export default {
  name: 'InventoryRelatedListWidget',
  extends: RelatedListWidget,
  components: {
    FLookupFieldWrapper,
    CurrencyPopOver,
  },
  data() {
    return {
      getCurrencyInDecimalValue,
    }
  },
  computed: {
    getUpdateUrl() {
      return this.$getProperty(
        this.$constants,
        `moduleStateUpdateMap.${this.moduleName}.updateUrl`,
        'v2/module/data/update'
      )
    },
    hideSearchField() {
      let { widgetParams, moduleName, hideSearchFieldList } = this
      return (
        !widgetParams.hideSearchField ||
        !hideSearchFieldList.includes(moduleName)
      )
    },
    showSeparator() {
      let { totalCount, modulesList, moduleName, hideSearchFieldList } = this
      return (
        !isEmpty(totalCount) &&
        !isEmpty(modulesList) &&
        !hideSearchFieldList.includes(moduleName)
      )
    },
    moduleList() {
      return this.$getProperty(this.widget, 'relatedList.module.name')
    },
    lookupModuleName() {
      return this.moduleList === 'item' ? 'itemTypes' : 'toolTypes'
    },
    filters() {
      let { mainField, searchText, details, widget, siteId } = this

      let { id } = details || {}
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
      if (!isEmpty(siteId)) {
        filterObj.siteId = {
          operatorId: 36,
          value: [`${siteId}`],
        }
      }

      if (!isEmpty(mainField) && !isEmpty(searchText) && !isEmpty(searchText)) {
        let { name, field } = mainField || {}
        let { dataTypeEnum } = field || {}
        let value = [`${searchText}`]
        let operatorId = Constants.FILTER_OPERATORID_HASH[dataTypeEnum]
        filterObj[name] = {
          operatorId,
          value,
        }
      }

      return filterObj
    },
  },
  methods: {
    lineItemLookupData(type) {
      return {
        lookupModule: { name: type },
        multiple: false,
      }
    },
    openMainFieldSearch() {
      this.$set(this, 'showMainFieldSearch', true)
    },
  },
}
</script>
