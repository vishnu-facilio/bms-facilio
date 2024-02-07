<template>
  <div class="related-list-container" ref="relatedListContainer">
    <div v-if="isLoading" class="flex-middle fc-empty-white">
      <spinner :show="isLoading" size="80"></spinner>
    </div>
    <div v-else>
      <div class="related-list-header pB25">
        <div class="header justify-content-space">
          <div class="widget-title d-flex flex-direction-column justify-center">
            {{ moduleDisplayName ? moduleDisplayName : moduleName }}
          </div>
          <template v-if="!$validation.isEmpty(modulesList) || showFilter">
            <AdvancedSearch
              v-if="showFilterWizard"
              ref="precaution-search"
              :key="`${moduleName}-search`"
              :moduleName="moduleName"
              :moduleDisplayName="moduleDisplayName"
              :hideQuery="true"
              :onSave="applyFilters"
              :filterList="precautionFilters"
            >
              <template #icon>
                <div></div>
              </template>
            </AdvancedSearch>
            <div class="ml-auto flex items-center">
              <div class="resource-filter-icon">
                <template>
                  <el-badge
                    is-dot
                    :hidden="$validation.isEmpty(precautionFilters)"
                  >
                    <div class="resource-icons" @click="checkShowFitler">
                      <InlineSvg
                        src="svgs/search"
                        class="d-flex cursor-pointer"
                        iconClass="icon icon-sm self-center"
                      ></InlineSvg>
                    </div>
                  </el-badge>
                </template>
              </div>

              <div v-if="!$validation.isEmpty(precautionFilters) || showFilter">
                <span class="separator">|</span>
                <div
                  @click="clearSearch"
                  class="el-dialog__close el-icon el-icon-close close-icon cursor-pointer resource-icons  mR5"
                ></div>
              </div>
            </div>
            <span
              v-if="
                !$validation.isEmpty(totalCount) &&
                  !$validation.isEmpty(modulesList)
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
              v-if="showAddButton"
              @click="showDialog"
              class="fc-create-btn mR10"
            >
              <i class="el-icon-plus"></i>
            </el-button>
          </span>
        </div>
      </div>
      <div>
        <div
          v-if="$validation.isEmpty(modulesList)"
          class="flex-middle justify-content-center wo-flex-col flex-direction-column"
          style="margin-top:4%"
        >
          <inline-svg
            :src="`svgs/emptystate/readings-empty`"
            iconClass="icon text-center icon-xxxlg"
          ></inline-svg>
          <div class="pT10 fc-black-dark f18 bold pB50">
            {{
              $t('common.products.no_module_available', {
                moduleName: moduleDisplayName ? moduleDisplayName : moduleName,
              })
            }}
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
                          avatarMap[moduleName]
                            ? avatarMap[moduleName]
                            : 'Avatar'
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
                        getColumnDisplayValue(mainFieldColumn, item.row) ||
                          '---'
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
                (field.field || {}).dataTypeEnum === 'DECIMAL'
                  ? 'right'
                  : 'left'
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
                    'text-right':
                      (field.field || {}).dataTypeEnum === 'DECIMAL',
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
                  <span @click="deleteRecord(item.row.id)">
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
        <el-form :model="ruleForm" :rules="rules" ref="ruleForm">
          <el-dialog
            v-if="showMsgPopup"
            title="Hazard"
            :visible.sync="showMsgPopup"
            width="40%"
            :append-to-body="true"
            style="z-index: 9999999999;"
            class="agents-dialog fc-dialog-center-container dialog-padding pointer"
          >
            <div
              class="label-txt-black line-height24 height200 overflow-y-scroll pB50"
            >
              <el-form-item prop="hazard" label="Hazard">
                <FLookupField
                  v-model="selectedHazard"
                  :model.sync="selectedHazard"
                  :field="wohazardField"
                  @showLookupWizard="() => (canShowHazardWizard = true)"
                  @setLookupFieldValue="
                    validateAndSave('ruleForm', selectedProblem)
                  "
                />
              </el-form-item>
            </div>
            <div class="modal-dialog-footer">
              <el-button class="modal-btn-cancel f13" @click="closeDialog">{{
                $t('agent.agent.cancel')
              }}</el-button>
              <el-button
                class="modal-btn-save f13"
                @click="validateAndSave('ruleForm', selectedHazard)"
                >{{ $t('common.failure_class.save') }}</el-button
              >
            </div>
          </el-dialog>
        </el-form>
      </div>
      <QuickCreateRecord
        v-if="quickAddDialogVisibility"
        :dialogVisibility.sync="quickAddDialogVisibility"
        :moduleName="moduleName"
        :parentId="getParentId"
        @saved="refreshRelatedList"
      ></QuickCreateRecord>

      <V3LookupFieldWizard
        v-if="canShowHazardWizard"
        :canShowLookupWizard.sync="canShowHazardWizard"
        :selectedLookupField="wohazardField"
        @setLookupFieldValue="setHazardData"
      ></V3LookupFieldWizard>

      <V3LookupFieldWizard
        v-if="canShowWizard"
        :canShowLookupWizard.sync="canShowWizard"
        :selectedLookupField="precautionField"
        @setLookupFieldValue="saveRecord"
      ></V3LookupFieldWizard>

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
  </div>
</template>

<script>
import RelatedListWidget from '@/page/widget/common/RelatedListWidget'
import { isFileTypeField } from '@facilio/utils/field'
import { API } from '@facilio/api'
import { isEmpty, isNullOrUndefined } from '@facilio/utils/validation'
import V3LookupFieldWizard from 'src/newapp/components/V3LookupFieldWizard.vue'
import FLookupField from '@/forms/FLookupField'
import isEqual from 'lodash/isEqual'
import { eventBus } from '@/page/widget/utils/eventBus'
import AdvancedSearch from 'newapp/components/search/AdvancedSearch'

export default {
  name: 'WorkorderPrecautions',
  extends: RelatedListWidget,
  components: {
    V3LookupFieldWizard,
    FLookupField,
    AdvancedSearch,
  },
  data() {
    return {
      addRelatedRecordsVisibility: false,
      passRecordFilter: null,
      canShowWizard: false,
      showMsgPopup: false,
      selectedHazard: null,
      showFilter: false,
      showFilterWizard: false,
      precautionFilters: null,
      showAddButton: false,
      canShowHazardWizard: false,
      ruleForm: {
        hazard: '',
      },
      rules: {
        hazard: [
          {
            required: true,
            message: 'Please select a hazard',
            trigger: 'blur',
          },
        ],
      },
    }
  },
  created() {
    this.isFileTypeField = isFileTypeField
  },
  mounted() {
    eventBus.$on('refresh-workorderPrecaution', showAddButton => {
      this.showAddButton = showAddButton
      this.fetchModulesData()
    })
  },
  computed: {
    moduleName() {
      return 'workorderHazardPrecaution'
    },
    moduleDisplayName() {
      return 'Precautions'
    },
    canShowColumnCustomization() {
      return false
    },
    wohazardField() {
      let workOrderId = this.$getProperty(this, 'details.id', 0)
      let field = {
        isDataLoading: false,
        options: [],
        lookupModuleName: 'workorderHazard',
        field: {
          lookupModule: {
            name: 'workorderHazard',
            displayName: 'Hazards',
          },
        },
        forceFetchAlways: true,
        selectedItems: [],
        filters: {
          workorder: {
            operatorId: 9,
            value: [`${workOrderId}`],
          },
        },
      }
      return field
    },
    precautionField() {
      let { selectedHazard } = this
      let field = {
        isDataLoading: false,
        options: [],
        lookupModuleName: 'precaution',
        field: {
          lookupModule: {
            name: 'precaution',
            displayName: 'Precautions',
          },
        },
        multiple: true,
        forceFetchAlways: true,
        selectedItems: [],
        additionalParams: {
          excludeAvailableWorkOrderHazardPrecautions: selectedHazard,
        },
      }
      return field
    },
  },
  watch: {
    selectedHazard: {
      handler() {
        this.$refs['ruleForm'].clearValidate()
      },
    },
    filters(newVal, oldVal) {
      if (!isEqual(newVal, oldVal)) {
        this.$set(this, 'isSearchDataLoading', true)
        this.debounceMainFieldSearch()
      }
    },
    page(newVal, oldVal) {
      if (!isEqual(newVal, oldVal)) {
        let { isSearchDataLoading } = this
        if (!isSearchDataLoading) {
          this.isLoading = true
          this.fetchModulesData(true).finally(() => {
            this.isLoading = false
            this.onDataLoad && this.onDataLoad()
          })
          this.loadDataCount(true)
        }
      }
    },
  },
  methods: {
    validateAndSave(formName, selectedHazard) {
      this.$refs[formName].validate(valid => {
        if (selectedHazard) {
          this.closeDialog()
          this.showPrecautionWizard()
        } else {
          return false
        }
      })
    },
    setHazardData(selectedHazard) {
      let field = this.$getProperty(selectedHazard, 'field')
      let selectedData = this.$getProperty(field, 'selectedItems')
      let data = selectedData[0]
      this.selectedHazard = data?.value
      this.canShowHazardWizard = false
    },
    showPrecautionWizard() {
      this.canShowWizard = true
    },
    setQueryParams() {
      this.precautionField['additionalParams'] = {
        excludeAvailableParentAssociatedHazards: this.selectedHazard,
      }
    },
    showDialog() {
      let { precautionField } = this
      this.$set(precautionField, 'selectedItems', [])
      this.$set(this, 'precautionField', precautionField)
      this.selectedHazard = null
      this.showMsgPopup = true
    },
    async deleteRecord(id) {
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

        let { error } = await API.deleteRecord(moduleName, id)
        if (isEmpty(error)) {
          this.$message.success(
            `${moduleDisplayName} ${this.$t(
              'custommodules.list.delete_success'
            )}`
          )
          await this.loadData()
        } else {
          this.$message.error(error.message)
        }
      }

      this.isLoading = false
    },
    checkShowFitler() {
      this.showFilterWizard = false
      this.$nextTick(() => {
        this.openSearchFilter()
      })
    },
    openSearchFilter() {
      this.showFilterWizard = true
      this.$nextTick(() => {
        this.$refs['precaution-search'].openCloseSearch(true)
      })
    },
    closeDialog() {
      this.showMsgPopup = false
    },
    closeWizard() {
      this.canShowWizard = false
    },
    async applyFilters({ filters }) {
      this.isLoading = true
      this.showFilterWizard = false
      this.precautionFilters = filters
      this.showFilter = true
      await this.fetchModulesData({ filters })

      this.isLoading = false
    },
    async clearSearch() {
      this.showFilterWizard = false
      this.precautionFilters = null
      this.showFilter = false
      await this.fetchModulesData()
    },
    async saveRecord(precautionData) {
      let { moduleName, selectedHazard } = this
      this.isLoading = true
      let parentId = this.$getProperty(this, 'details.id', null)
      let url = 'v3/modules/data/bulkCreate'
      let records = []
      let field = this.$getProperty(precautionData, 'field')
      let selectedData = this.$getProperty(field, 'selectedItems')
      records = selectedData.map(record => {
        return {
          workorderHazard: { id: selectedHazard },
          workorder: { id: parentId },
          precaution: { id: record.value },
        }
      })
      let params = {
        data: {
          workorderHazardPrecaution: records,
        },
        moduleName: moduleName,
        params: {
          return: true,
        },
      }
      let { error } = await API.post(url, params)
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.$message.success('Hazards added successfully')
        this.closeWizard()
        this.loadData()
      }
      this.isLoading = false
      this.closeWizard()
    },
    async fetchModulesData(props) {
      let { moduleName, page } = this
      this.isLoading = true
      let { filters = {} } = props || {}
      let filter = {
        ...filters,
        workorder: {
          operatorId: 9,
          value: [`${this.details?.id}`],
        },
      }
      let params = {
        viewName: 'hidden-all',
        withCount: true,
        filters: JSON.stringify(filter),
        page,
        perPage: 10,
      }
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
    loadDataCount() {},
  },
}
</script>

<style scoped lang="scss">
.resource-filter-icon {
  cursor: pointer;
}
.resource-icons {
  padding: 5px;
  border: solid 1px transparent;
  border-radius: 3px;
  &:hover {
    color: #615e88;
    background: #f5f6f8;
    border: 1px solid #dae0e8;
  }
}
.separator {
  font-weight: 300;
  color: #d8d8d8 !important;
  padding-right: 15px;
  padding-left: 15px;
  padding-bottom: 2px;
}
</style>
