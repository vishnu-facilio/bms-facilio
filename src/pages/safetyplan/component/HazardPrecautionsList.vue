<template>
  <div class="related-list-container" ref="relatedListContainer">
    <div v-if="isLoading" class="flex-middle fc-empty-white">
      <spinner :show="isLoading" size="80"></spinner>
    </div>
    <div v-else class="related-list-header">
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
              v-if="!hideSearchFieldList.includes(moduleName)"
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
        <span class="self-center">
          <el-button
            @click="addRelatedRecordsToggle"
            class="fc-create-btn mR10"
          >
            <i class="el-icon-plus"></i>
          </el-button>
        </span>
      </div>
    </div>
    <div>
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
          v-if="!hideColumnCustomizationList.includes(moduleName)"
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
            :label="mainFieldColumn.displayName"
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
            :label="field.displayName"
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
                  @click="deleteHazardPrecaution(item.row)"
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

    <V3LookupFieldWizard
      v-if="canShowWizard"
      :canShowLookupWizard.sync="canShowWizard"
      :selectedLookupField="hazardPrecautionFilter"
      @setLookupFieldValue="closeLookUpWizard"
    ></V3LookupFieldWizard>

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
import RelatedListWidget from '@/page/widget/common/RelatedListWidget'
import Constants from 'util/constant'
import { isEmpty, isNullOrUndefined } from '@facilio/utils/validation'
import { isFileTypeField } from '@facilio/utils/field'
import { API } from '@facilio/api'
import V3LookupFieldWizard from 'src/newapp/components/V3LookupFieldWizard.vue'
import { eventBus } from '@/page/widget/utils/eventBus'

export default {
  name: 'HazardPrecautionList',
  extends: RelatedListWidget,
  components: {
    SafetyPlanParentUpdateViewer: () =>
      import('pages/safetyplan/component/SafetyPlanParentUpdateViewer'),
    V3LookupFieldWizard,
  },
  data() {
    return {
      addRelatedRecordsVisibility: false,
      canShowWizard: false,
      passRecordFilter: null,
    }
  },
  created() {
    this.isFileTypeField = isFileTypeField
  },
  computed: {
    moduleName() {
      return 'hazardPrecaution'
    },
    isCustomModule() {
      return false
    },
    moduleDisplayName() {
      return 'Associated Precautions'
    },
    filters() {
      let { mainField, searchText, $route, details } = this
      let { params } = $route
      let { id } = params || details

      let filterObj = {}
      // search filter
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

      // special filter

      filterObj.hazard = {
        operatorId: 36,
        value: [`${id}`],
      }

      return filterObj
    },
    hazardPrecautionFilter() {
      let field = {
        isDataLoading: false,
        options: [],
        lookupModuleName: 'precaution',
        field: {
          lookupModule: {
            name: 'precaution',
            displayName: 'Precaution',
          },
        },
        multiple: true,
        forceFetchAlways: true,
      }
      let value = this.modulesList.map(record => {
        let { precaution } = record || {}
        let { id } = precaution || {}
        return id + ''
      })
      if (value.length) {
        let filter = {
          id: {
            operatorId: 37,
            value,
          },
        }
        field['filters'] = filter
      }
      return field
    },
    parentId() {
      let { $route } = this
      let { params } = $route || {}
      let { id } = params || {}
      return id || -1
    },
  },
  methods: {
    async closeLookUpWizard(precautionData) {
      this.isLoading = true
      let { parentId } = this
      let url = 'v3/modules/data/bulkCreate'
      let records = []
      let field = this.$getProperty(precautionData, 'field')
      let selectedData = this.$getProperty(field, 'selectedItems')
      records = selectedData.map(record => {
        return {
          hazard: { id: parentId },
          precaution: { id: record.value },
        }
      })
      let params = {
        data: {
          hazardPrecaution: records,
        },
        moduleName: 'hazardPrecaution',
        params: {
          return: true,
        },
      }
      let { error } = await API.post(url, params)
      if (!error) {
        this.loadData()
        eventBus.$emit('refesh-parent')
        this.canShowWizard = false
      }
      this.isLoading = false
      this.canShowWizard = false
    },
    async loadData() {
      let { moduleName, filters, viewDetailsExcludedModules } = this
      let viewDetailUrl = `v2/views/associatedprecautions?moduleName=${moduleName}`

      let params = {
        viewName: 'hidden-all',
        withCount: true,
        filters: JSON.stringify(filters),
      }

      let promiseHash = []
      this.isLoading = true
      promiseHash.push(this.$http.get(viewDetailUrl))

      Promise.all(promiseHash)
        .then(([viewDetailsData]) => {
          if (
            !isEmpty(viewDetailsData) &&
            !viewDetailsExcludedModules.includes(moduleName)
          ) {
            let {
              data: { message, responseCode, result = {} },
            } = viewDetailsData
            if (responseCode === 0) {
              let { viewDetail } = result
              if (!isEmpty(viewDetail)) {
                this.$set(this, 'viewDetail', viewDetail)
              }
            } else {
              throw new Error(message)
            }
          }
        })
        .catch(({ message }) => {
          this.$message.error(message)
        })

      let { list, error, meta } = await API.fetchAll(moduleName, params, {
        force: true,
      })
      if (!error) {
        let { pagination } = meta
        if (!isNullOrUndefined(list)) {
          this.$set(this, 'modulesList', list)
          this.$set(this, 'totalCount', pagination?.totalCount)
        }
      }
      this.isLoading = false
    },
    async deleteHazardPrecaution(item) {
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
          this.$message.success(
            `${this.moduleDisplayName} deleted successfully`
          )
          this.loadData()
        }
      }
    },
    addRelatedRecordsToggle(filter) {
      this.canShowWizard = true
      this.$set(
        this,
        'addRelatedRecordsVisibility',
        !this.addRelatedRecordsVisibility
      )
      this.$set(this, 'passRecordFilter', filter)
    },
  },
}
</script>
