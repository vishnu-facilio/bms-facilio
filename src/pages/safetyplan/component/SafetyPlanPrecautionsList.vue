<template>
  <div class="related-list-container" ref="relatedListContainer">
    <div v-if="isLoading" class="flex-middle fc-empty-white">
      <spinner :show="isLoading" size="80"></spinner>
    </div>
    <div v-else class="related-list-header pB25">
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
          <div v-if="showSearchField()" class="ml-auto flex items-center">
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
          :header-cell-style="{ background: '#f3f1fc' }"
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
import { API } from '@facilio/api'
import { isEmpty, isNullOrUndefined } from '@facilio/utils/validation'
import { eventBus } from '@/page/widget/utils/eventBus'
import AdvancedSearch from 'newapp/components/search/AdvancedSearch'

const EQUALS_OPERATOR = 9
const SHOW_SEARCH_FIELD = ['asset', 'site', 'building', 'floor', 'space']

export default {
  name: 'SafetyPlanPrecautionsList',
  extends: RelatedListWidget,
  props: ['details'],
  components: {
    AdvancedSearch,
  },
  data() {
    return {
      showFilter: false,
      showFilterWizard: false,
      hazardIds: [],
      precautionFilters: null,
      canShowColumnCustomization: false,
    }
  },
  computed: {
    moduleName() {
      return 'hazardPrecaution'
    },
    moduleDisplayName() {
      return 'Precautions'
    },
    canShowDelete() {
      return false
    },
  },
  created() {
    this.fetchSafetyPlanHazards()
  },
  mounted() {
    eventBus.$on('refesh-parent', this.fetchSafetyPlanHazards)
    eventBus.$on('refresh-precautions', this.fetchSafetyPlanHazards)
  },
  methods: {
    showSearchField() {
      return SHOW_SEARCH_FIELD.includes(this.pageModuleName)
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
    getParentModuleName() {
      let { pageModuleName } = this
      let baseSpaces = ['site', 'building', 'floor', 'space']
      if (baseSpaces.includes(pageModuleName)) {
        return 'space'
      } else {
        return pageModuleName
      }
    },
    async fetchSafetyPlanHazards() {
      this.hazardIds = []
      this.isLoading = true
      let parentModuleName = this.getParentModuleName()
      let filter = {}
      filter[`${parentModuleName}`] = {
        operatorId: EQUALS_OPERATOR,
        value: [`${this.details?.id}`],
      }
      let params = {
        withCount: true,
      }
      params['filters'] = JSON.stringify(filter)
      let { list, error } = await API.fetchAll(
        `${parentModuleName}Hazard`,
        params,
        { force: true }
      )
      if (!error) {
        if (list?.length > 0) {
          this.hazardIds = list.map(data => {
            let hazard = this.$getProperty(data, 'hazard', {})
            let hazardId = this.$getProperty(hazard, 'id', null)
            return hazardId + ''
          })
        }
      }
      this.fetchModulesData()
      this.isLoading = false
    },
    async fetchModulesData(props) {
      let { moduleName } = this
      let { hazardIds } = this
      let { filters = {} } = props || {}
      if (!isEmpty(hazardIds)) {
        let filter = {
          ...filters,
          hazard: {
            operatorId: 9,
            value: hazardIds,
          },
        }
        let params = {
          withCount: true,
          filters: JSON.stringify(filter),
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
      } else {
        this.$set(this, 'modulesList', [])
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
}
.separator {
  font-weight: 300;
  color: #d8d8d8 !important;
  padding-right: 15px;
  padding-left: 15px;
  padding-bottom: 2px;
}
</style>
