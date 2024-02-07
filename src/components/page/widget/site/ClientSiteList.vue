<template>
  <div class="related-list-container" ref="relatedListContainer">
    <div class="related-list-header">
      <div class="header justify-content-space">
        <div class="widget-title d-flex flex-direction-column justify-center">
          {{ moduleDisplayName ? moduleDisplayName : moduleName }}
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
          >
            |
          </span>
          <pagination
            v-if="!hidePaginationSearch"
            :currentPage.sync="page"
            :total="totalCount"
            :perPage="perPage"
            class="self-center"
          ></pagination>
        </template>

        <span class="self-center">
          <el-button
            @click="addRelatedRecordsToggle"
            class="fc-create-btn mL10"
          >
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
          No
          {{ moduleDisplayName ? moduleDisplayName : moduleName }} available.
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
            prop
            label
            fixed="right"
            width="130"
            class="visibility-visible-actions"
          >
            <template v-slot="item">
              <div class="text-center">
                <span @click="invokeDeleteDialog(item.row)">
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
    <ColumnCustomization
      :visible.sync="canShowColumnCustomization"
      :moduleName="moduleName"
      :columnConfig="columnCustomizationConfig"
      :relatedViewDetail="viewDetail"
      :relatedMetaInfo="currentMetaInfo"
      :viewName="'hidden-all'"
      @refreshRelatedList="refreshRelatedList"
    />

    <V3LookupFieldWizard
      v-if="associateRecordPopup"
      :canShowLookupWizard.sync="associateRecordPopup"
      :selectedLookupField="siteFieldObj"
      @setLookupFieldValue="setLookupFieldValue"
    >
    </V3LookupFieldWizard>
  </div>
</template>
<script>
import RelatedListWidget from '@/page/widget/common/RelatedListWidget'
import Constants from 'util/constant'
import { isEmpty } from '@facilio/utils/validation'
import V3LookupFieldWizard from '../../../../newapp/components/V3LookupFieldWizard.vue'
import { API } from '@facilio/api'
import { isFileTypeField } from '@facilio/utils/field'

let siteFieldObj = {
  isDataLoading: false,
  options: [],
  lookupModuleName: 'site',
  field: {
    lookupModule: {
      name: 'site',
      displayName: 'Sites',
    },
  },
  forceFetchAlways: true,
  filters: {},
  multiple: true,
  isDisabled: false,
}

export default {
  name: 'SiteListWidget',
  extends: RelatedListWidget,
  components: { V3LookupFieldWizard },
  data() {
    return { siteFieldObj, associateRecordPopup: false }
  },
  created() {
    this.isFileTypeField = isFileTypeField
  },
  computed: {
    moduleName() {
      return 'site'
    },
    isCustomModule() {
      return false
    },
    moduleDisplayName() {
      return 'Sites'
    },
    filters() {
      let { mainField, searchText, details } = this

      let filterObj = {}

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

      let { id } = details
      filterObj.client = {
        operatorId: 36,
        value: [`${id}`],
      }

      return filterObj
    },
    hidePaginationSearch() {
      return false
    },
  },
  methods: {
    addRelatedRecordsToggle() {
      this.associateRecordPopup = !this.associateRecordPopup
    },
    async setLookupFieldValue(value) {
      let { field } = value
      let { selectedItems } = field
      let siteIds = selectedItems.map(item => item.value)
      let { id } = this.details

      let { error } = await API.updateRecord('client', {
        id,
        data: { siteIds },
        params: { isAssociateSites: true },
      })
      if (!error) {
        this.$message.success(this.$t('common._common.siteAssociation'))
        await this.refreshRelatedList()
      } else {
        this.$message.error(error.message || 'Error Occured')
      }
      this.siteFieldObj.selectedItems = []
      this.showLookupFieldWizard = false
    },
    async invokeDeleteDialog(item) {
      let { id } = this.details
      let { error } = await API.updateRecord('client', {
        id,
        data: { siteIds: [item.id] },
        params: { isDisassociateSites: true },
      })
      if (!error) {
        this.$message.success(this.$t('common._common.siteDisassociation'))
        await this.refreshRelatedList()
      } else {
        this.$message.error(error.message || 'Error Occured')
      }
    },
  },
}
</script>
