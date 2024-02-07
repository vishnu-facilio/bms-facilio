<template>
  <div class="related-list-container" ref="relatedListContainer">
    <div v-if="isLoading" class="flex-middle fc-empty-white">
      <spinner :show="isLoading" size="80"></spinner>
    </div>
    <div v-else>
      <div class="related-list-header">
        <div class="header justify-content-space">
          <div class="widget-title d-flex flex-direction-column justify-center">
            {{ moduleDisplayName ? moduleDisplayName : moduleName }}
          </div>
          <template v-if="!$validation.isEmpty(modulesList) || showFilter">
            <AdvancedSearch
              v-if="showFilterWizard"
              ref="hazard-search"
              :key="`${moduleName}-search`"
              :moduleName="moduleName"
              :moduleDisplayName="moduleDisplayName"
              :hideQuery="true"
              :onSave="applyFilters"
              :filterList="hazardFilters"
            >
              <template #icon>
                <div></div>
              </template>
            </AdvancedSearch>
            <div class="ml-auto flex items-center">
              <div class="resource-filter-icon">
                <template>
                  <el-badge is-dot :hidden="$validation.isEmpty(hazardFilters)">
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

              <div v-if="!$validation.isEmpty(hazardFilters) || showFilter">
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
              :align="fieldtype(field)"
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
        :selectedLookupField="hazardField"
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
import { eventBus } from '@/page/widget/utils/eventBus'
import AdvancedSearch from 'newapp/components/search/AdvancedSearch'

export default {
  name: 'CommonHazardList',
  extends: RelatedListWidget,
  components: {
    V3LookupFieldWizard,
    AdvancedSearch,
  },
  data() {
    return {
      canShowWizard: false,
      showFilter: false,
      showFilterWizard: false,
      hazardFilters: null,
      hazardField: {
        isDataLoading: false,
        options: [],
        lookupModuleName: 'hazard',
        field: {
          lookupModule: {
            name: 'hazard',
            displayName: 'Hazards',
          },
        },
        multiple: true,
        forceFetchAlways: true,
        selectedItems: [],
      },
      canShowColumnCustomization: false,
    }
  },
  created() {
    this.isFileTypeField = isFileTypeField
    this.setQueryParams()
  },
  computed: {
    fieldtype() {
      return field => {
        if ((field.field || {}).dataTypeEnum === 'DECIMAL') {
          return 'right'
        } else {
          return 'left'
        }
      }
    },
  },
  watch: {
    hazardFilters: {
      handler(oldVal, newVal) {
        if (oldVal != newVal) {
          this.showFilterWizard = false
        }
      },
    },
  },
  methods: {
    init() {
      this.loadData()
    },
    addRelatedRecordsToggle() {
      let { hazardField } = this
      this.$set(hazardField, 'selectedItems', [])
      this.$set(this, 'hazardField', hazardField)
      this.canShowWizard = true
    },
    setQueryParams() {
      this.hazardField['additionalParams'] = {
        excludeAvailableParentAssociatedHazards: this.details?.id,
        parentModuleName: this.moduleName,
      }
    },
    closeWizard() {
      this.canShowWizard = false
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
        this.$refs['hazard-search'].openCloseSearch(true)
      })
    },
    async applyFilters({ filters }) {
      this.isLoading = true
      this.showFilterWizard = false
      this.hazardFilters = filters
      this.showFilter = true
      await this.fetchModulesData({ filters })

      this.isLoading = false
    },
    async clearSearch() {
      this.showFilterWizard = false
      this.hazardFilters = null
      this.showFilter = false
      await this.fetchModulesData()
    },
    getParentModuleName() {
      let { pageModuleName } = this
      let baseSpaces = ['site', 'building', 'floor', 'space']
      if (baseSpaces.includes(pageModuleName)) {
        return 'space'
      } else {
        return pageModuleName
      }
    },
    async saveRecord(HazardData) {
      this.isLoading = true
      let { moduleName } = this
      let hazardIds = this.$getProperty(this, 'details.hazardIds', [])
      let parentId = this.$getProperty(this, 'details.id', null)
      let parentModuleName = this.getParentModuleName()
      let url = 'v3/modules/data/bulkCreate'
      let records = []
      let selectedHazardIds = []
      let field = this.$getProperty(HazardData, 'field')
      let selectedData = this.$getProperty(field, 'selectedItems')
      records = selectedData.map(record => {
        let { value } = record
        selectedHazardIds.push(value)
        let obj = { hazard: { id: value } }
        obj[`${parentModuleName}`] = { id: parentId }
        return obj
      })
      selectedHazardIds = [...selectedHazardIds, ...hazardIds]
      this.$set(this, 'details.hazardIds', selectedHazardIds)
      let params = {
        data: {
          workorderHazard: records,
        },
        moduleName: moduleName,
        params: {
          return: true,
        },
      }
      let data = {}
      data[`${parentModuleName}Hazard`] = records
      params['data'] = data
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
      let parentModuleName = this.getParentModuleName()
      let { filters = {} } = props || {}
      let filter = { ...filters }
      filter[`${parentModuleName}`] = {
        operatorId: 9,
        value: [`${this.details?.id}`],
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
        eventBus.$emit('refresh-precautions')
        if (this.totalCount > 0)
          eventBus.$emit('refresh-workorderPrecaution', true)
        else eventBus.$emit('refresh-workorderPrecaution', false)
      }
      this.isLoading = false
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

        try {
          await API.deleteRecord(moduleName, id)
          this.$message.success(
            `${moduleDisplayName} ${this.$t(
              'custommodules.list.delete_success'
            )}`
          )
          await this.loadData()
          eventBus.$emit('refresh-workorderPrecaution')
        } catch (errorMsg) {
          this.$message.error(errorMsg)
        }

        this.isLoading = false
      }
    },
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
