<template>
  <el-dialog
    :visible.sync="canShowWizard"
    width="70%"
    top="5vh"
    class="fc-dialog-center-container f-lookup-wizard scale-up-center fc-wizard-table-width100"
    :append-to-body="true"
    :show-close="false"
    :before-close="closeWizard"
  >
    <template slot="title">
      <div class="header d-flex">
        <div v-if="!resourceModule" class="el-dialog__title self-center">
          {{ title }}
        </div>
        <div v-else class="el-dialog__title self-center pointer d-flex">
          <div
            :class="[
              'mR20',
              { 'font-hairline': resourceSubModuleName !== 'space' },
            ]"
            @click="onTabSwitch('space')"
          >
            {{ $t('common.space_asset_chooser.space') }}
          </div>
          <div
            :class="{
              'font-hairline': resourceSubModuleName !== 'asset',
              disabled: disableTabSwitch,
            }"
            @click="onTabSwitch('asset')"
          >
            {{ $t('common._common.asset') }}
          </div>
        </div>

        <div class="mL-auto d-flex">
          <template v-if="showSearch">
            <el-tooltip
              effect="dark"
              :content="$t('common._common.search')"
              placement="left"
            >
              <SearchComponent
                :key="moduleName + '-search'"
                class="self-center mL-auto lookup-wizard-search"
                :moduleName="moduleName"
                :searchMetaInfo="moduleMetaInfo"
                :hideOperatorDialog="true"
                :singleFieldSearch="true"
                :clearFilters="clearAllFilters"
                @onChange="applyFilter"
                @clearedFilters="clearAllFilters = false"
              >
              </SearchComponent>
            </el-tooltip>
            <el-tooltip
              effect="dark"
              content="Clear Filters"
              placement="right"
              v-if="!$validation.isEmpty(wizardFilterValue)"
            >
              <span @click="clearFilter()" class="self-center"
                ><img
                  src="~assets/filter-remove.svg"
                  width="20px"
                  height="20px"
                  style="position: relative;top: 4px;left: 10px;"
              /></span>
            </el-tooltip>
            <span class="separator self-center">|</span>
          </template>
          <template v-if="!hidePaginationSearch && totalCount > 0">
            <pagination
              :currentPage.sync="page"
              :total="totalCount"
              :perPage="perPage"
              class="self-center"
              style="margin-right: -10px;"
            ></pagination>
            <span class="separator self-center">|</span>
          </template>
          <template
            v-if="(selectedLookupField || {}).allowCreate && !isLoading"
          >
            <span class="self-center">
              <el-button @click="quickAddRecordToggle" class="fc-create-btn">
                <i class="el-icon-plus"></i>
              </el-button>
            </span>
            <span class="separator self-center">|</span>
          </template>
          <div
            class="close-btn self-center cursor-pointer"
            v-bind:class="hidePaginationSearch ? 'mL-auto pL10' : ''"
            @click="closeWizard"
          >
            <i class="el-dialog__close el-icon el-icon-close"></i>
          </div>
        </div>
      </div>
    </template>
    <div>
      <div
        v-if="isLoading"
        class="loading-container d-flex justify-content-center height550px"
      >
        <Spinner :show="isLoading"></Spinner>
      </div>
      <div v-else class="height550 fc__layout__has__row">
        <div class="lookup-wizard-scroll">
          <div v-if="resourceModule || showFilters" class="d-flex pT20 pB20">
            <template v-for="(field, index) in quickFilters">
              <FLookupField
                :key="index"
                :model.sync="field.value"
                :hideLookupIcon="true"
                :field="field"
                :disabled="field.disabled"
                :fetchOptionsOnLoad="true"
                :preHookFilterConstruction="resourceFilterConstruction"
                @recordSelected="value => setLookUpFilter(field, value)"
                class="quick-filter"
              ></FLookupField>
            </template>
          </div>
          <div
            v-if="!$validation.isEmpty(selectedItem)"
            class="pT20 pL20 pB10 fc-black-color letter-spacing0_5 f13 bold d-flex items-baseline flex-direction-row flex-wrap"
          >
            <template>
              <div class="mR10 mB10 mT5 flex-shrink-0">{{ selectedLabel }}</div>
              <div
                v-for="(name, index) in selectedItemName"
                :key="index"
                class="action-badge mR10 mb5"
              >
                {{ name }}
                <i
                  v-if="multiSelect"
                  class="el-icon-close pointer"
                  @click="handleClose(name)"
                ></i>
              </div>
            </template>
          </div>
          <div
            class="fc-list-view fc-table-td-height fc-list-table-container fc-table-viewchooser overflow-scroll pB30"
          >
            <div
              v-if="isSearchDataLoading"
              class="loading-container d-flex justify-content-center height550px"
            >
              <Spinner :show="isSearchDataLoading"></Spinner>
            </div>
            <template v-else>
              <el-table
                ref="listTable"
                :data="modulesList"
                style="width: 100%;"
                :fit="true"
                height="100%"
                @selection-change="handleSelection"
              >
                <template slot="empty">
                  <img
                    class="mT20"
                    src="~statics/noData-light.png"
                    width="100"
                    height="100"
                  />
                  <div class="mT10 label-txt-black f14">
                    {{
                      $t('common.products.no_module_available', {
                        moduleName: moduleDisplayName
                          ? moduleDisplayName
                          : moduleName,
                      })
                    }}
                  </div>
                </template>
                <el-table-column
                  v-if="multiSelect"
                  type="selection"
                  width="60"
                  fixed
                ></el-table-column>
                <el-table-column v-if="!multiSelect" fixed width="60">
                  <template v-slot="item">
                    <div>
                      <el-radio
                        :label="item.row.id"
                        v-model="selectedItemId"
                        class="fc-radio-btn"
                        @change="setSelectedItem"
                      ></el-radio>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column
                  v-if="!$validation.isEmpty(mainFieldColumn)"
                  :label="mainFieldColumn.displayName"
                  :prop="mainFieldColumn.name"
                  fixed
                  min-width="200"
                >
                  <template v-slot="item">
                    <div class="table-subheading">
                      <div class="d-flex">
                        <div
                          class="self-center name bold main-field-column"
                          :title="
                            getColumnDisplayValue(mainFieldColumn, item.row)
                          "
                          v-tippy
                        >
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
                  :align="getColumnAlignment(field)"
                  min-width="200"
                >
                  <template v-slot="scope">
                    <keep-alive v-if="isSpecialHandlingField(field)">
                      <!-- eslint-disable-next-line vue/require-component-is -->
                      <component
                        :is="
                          (listComponentsMap[moduleName] || {}).componentName
                        "
                        :field="field"
                        :moduleData="scope.row"
                      ></component>
                    </keep-alive>
                    <div
                      v-else-if="
                        ((field || {}).field || {}).displayType ===
                          'MULTI_CURRENCY'
                      "
                    >
                      <CurrencyPopOver
                        :key="`${field.fieldId}-${field.name}`"
                        :field="field"
                        :details="scope.row"
                      />
                    </div>
                    <div
                      v-else
                      class="table-subheading"
                      :class="[
                        getColumnAlignment(field) === 'right'
                          ? 'text-right'
                          : '',
                      ]"
                    >
                      {{ getColumnDisplayValue(field, scope.row) || '---' }}
                    </div>
                  </template>
                </el-table-column>
              </el-table>
            </template>
          </div>
        </div>
        <div v-if="multiSelect && !isLoading" class="d-flex mT-auto">
          <el-button @click="canShowWizard = false" class="modal-btn-cancel">
            {{ $t('common._common.cancel') }}
          </el-button>
          <el-button
            class="modal-btn-save m0"
            type="primary"
            @click="setSelectedItem()"
          >
            {{ $t('common._common._save') }}
          </el-button>
        </div>
      </div>
      <QuickCreateData
        v-if="canShowQuickCreate"
        :canShowDialog.sync="canShowQuickCreate"
        :quickCreateField="selectedLookupField"
        :setAddedRecord="setAddedRecord"
      ></QuickCreateData>
    </div>
  </el-dialog>
</template>

<script>
import CommonRelatedList from '@/mixins/list/CommonRelatedList'
import WorkOrderSpecialFieldsList from '@/list/WorkOrderSpecialFieldsList'
import { isEmpty } from '@facilio/utils/validation'
import Spinner from '@/Spinner'
import AlarmSpecialFieldsList from '@/list/AlarmSpecialFieldsList'
import SearchComponent from 'newapp/components/WizardSearch'
import AssetSpecialFieldsList from '@/list/AssetSpecialFieldsList'
import Constants from 'util/constant'
import FLookupField from '@/forms/FLookupField'
import WizardFilterMixin from 'newapp/components/WizardQuickFilterAndSearchMixin'
import isEqual from 'lodash/isEqual'
import { getFieldValue } from 'util/picklist'
import CurrencyPopOver from 'src/pages/setup/organizationSetting/currency/CurrencyPopOver.vue'

export default {
  name: 'f-lookup-field-wizard',
  components: {
    Spinner,
    SearchComponent,
    FLookupField,
    CurrencyPopOver,
    QuickCreateData: () => import('@/forms/QuickCreateData'),
  },
  mixins: [CommonRelatedList, WizardFilterMixin],
  props: [
    'canShowLookupWizard',
    'selectedLookupField',
    'siteId',
    'withReadings',
    'specialFilterValue',
    'categoryId',
    'disableTabSwitch',
    'showFilters',
    'skipDecommission',
  ],

  async created() {
    await this.initSelectedItems()
    this.constructQuickFilters()
    this.init()
    this.fetchModuleMetaInfo()
  },

  computed: {
    canShowWizard: {
      get() {
        return this.canShowLookupWizard
      },
      set(value) {
        this.$emit('update:canShowLookupWizard', value)
      },
    },

    moduleDisplayName() {
      let { selectedLookupField } = this
      let { config, operatorLookupModule } = selectedLookupField
      let {
        isFiltersEnabled,
        filterValue,
        lookupModuleName: configLookupModuleName,
      } = config || {}
      if (isFiltersEnabled) {
        let selectedFilter = Constants.LOOKUP_FILTERS_MAP[filterValue]
        return (
          configLookupModuleName ||
          this.$t(`fields.properties.show_only_${selectedFilter}`)
        )
      }
      let displayName =
        this.$getProperty(
          selectedLookupField,
          'field.lookupModule.displayName',
          ''
        ) ||
        this.$getProperty(selectedLookupField, 'lookupModule.displayName', '')
      if (!isEmpty(operatorLookupModule)) {
        let operatorLookupModuleDisplayName = this.$getProperty(
          operatorLookupModule,
          'displayName',
          ''
        )
        if (!isEmpty(operatorLookupModuleDisplayName)) {
          displayName = operatorLookupModuleDisplayName
        }
      }
      return displayName
    },

    moduleName() {
      let {
        selectedLookupField,
        resourceSubModuleName,
        resourceModule,
        resourceFilterObj,
      } = this
      let { config } = selectedLookupField
      let {
        isFiltersEnabled,
        filterValue,
        lookupModuleName: configLookupModuleName,
      } = config || {}
      if (isFiltersEnabled) {
        return (
          configLookupModuleName || Constants.LOOKUP_FILTERS_MAP[filterValue]
        )
      }
      let name =
        this.$getProperty(
          selectedLookupField,
          'operatorLookupModule.name',
          ''
        ) ||
        this.$getProperty(selectedLookupField, 'lookupModuleName', '') ||
        this.$getProperty(selectedLookupField, 'field.lookupModule.name', '') ||
        this.$getProperty(selectedLookupField, 'lookupModule.name', '')
      let categorySelected =
        !isEmpty(resourceFilterObj['spaceCategory']) ||
        !isEmpty(resourceFilterObj['category'])
      let subModuleName =
        !categorySelected && resourceSubModuleName === 'space'
          ? 'basespace'
          : resourceSubModuleName

      name = resourceModule ? subModuleName : name

      return name
    },

    title() {
      let { moduleDisplayName } = this
      return `${this.$t('common.text.choose')} ${moduleDisplayName}`
    },

    selectedLabel() {
      let { moduleDisplayName } = this
      return `Selected ${moduleDisplayName} `
    },

    multiSelect() {
      let { selectedLookupField } = this
      let { multiple } = selectedLookupField || {}

      return !!multiple
    },

    selectedItemName() {
      let selectedItemNames = this.selectedItem.map(list => list.label)
      return !isEmpty(selectedItemNames) ? selectedItemNames : null
    },
    skipSiteFilter() {
      let { selectedLookupField } = this
      let { config } = selectedLookupField || {}
      let { skipSiteFilter = false } = config || {}
      return skipSiteFilter
    },
  },
  data() {
    return {
      debounceDelay: 0,
      modelValue: null,
      fieldObj: {},
      selectedItemId: [],
      defaultHideColumns: ['photo', 'moduleState', 'stateFlowId'],
      listComponentsMap: {
        workorder: {
          componentName: WorkOrderSpecialFieldsList,
          specialHandlingFields: [
            'resource',
            'assignedTo',
            'noOfNotes',
            'noOfTasks',
            'noOfAttachments',
          ],
        },
        alarm: {
          componentName: AlarmSpecialFieldsList,
          specialHandlingFields: [
            'severity',
            'noOfEvents',
            'acknowledgedBy',
            'modifiedTime',
            'previousSeverity',
            'resource',
            'alarmType',
          ],
        },
        asset: {
          componentName: AssetSpecialFieldsList,
          specialHandlingFields: [
            'movable',
            'geoLocation',
            'currentLocation',
            'designatedLocation',
            'distanceMoved',
            'boundaryRadius',
          ],
        },
      },
      canShowQuickCreate: false,
      selectedItem: [],
      selectedCategoryName: '',
    }
  },
  watch: {
    categoryId: {
      async handler(newVal, oldVal) {
        if (!isEqual(newVal, oldVal) && !isEmpty(newVal)) {
          let { data } = await getFieldValue({
            lookupModuleName: 'assetCategory',
            selectedOptionId: [newVal],
          })
          let [categoryObj] = data || []
          let { label = '' } = categoryObj || {}
          this.$set(this, 'selectedCategoryName', label)
        }
      },
      immediate: true,
    },
  },
  methods: {
    closeWizard(data) {
      this.canShowWizard = false
      if (data?.field?.selectedItems && data.field.selectedItems.length) {
        this.$emit('closeWizard', data)
      }
    },
    loadInitialData() {
      this.loadData()
      this.loadDataCount()
    },
    setSelectedItem() {
      let {
        selectedItem,
        mainField,
        selectedItemId,
        multiSelect,
        selectedLookupField,
      } = this
      if (!multiSelect) {
        selectedItem = [
          this.modulesList.find(list => list.id === selectedItemId),
        ]
        selectedItem = selectedItem.map(list => {
          let { id } = list
          let { name } = this.mainField || { name: 'name' }
          let selectedItem = {
            label: list[name],
            value: id,
          }
          return selectedItem
        })
      }
      if (isEmpty(mainField)) {
        mainField = {
          name: 'name',
        }
      }
      selectedLookupField.selectedItems = selectedItem
      let data = {
        field: selectedLookupField,
      }
      this.$emit('setLookupFieldValue', data)
      this.$nextTick(() => {
        this.closeWizard(data)
      })
    },
    async initSelectedItems() {
      let { selectedItems = [] } = this.selectedLookupField || {}

      if (!isEmpty(selectedItems)) {
        let selectedIds = selectedItems.map(item => item.value)
        this.selectedItemId = this.multiSelect ? selectedIds : selectedIds[0]

        if (this.resourceModule) {
          this.isLoading = true
          await this.getResourceDetails(selectedIds)
        } else {
          this.selectedItem = [...selectedItems]
        }
      } else {
        this.resourceSubModuleName = 'space'
      }
    },
    onDataLoad() {
      let { isLoading, isSearchDataLoading, multiSelect, selectedItemId } = this
      let toggleSelection =
        !isLoading &&
        !isSearchDataLoading &&
        multiSelect &&
        !isEmpty(selectedItemId)

      if (toggleSelection) {
        this.$nextTick(() => {
          let currentSelectedList = this.modulesList.filter(
            list => selectedItemId.findIndex(item => item === list.id) !== -1
          )
          let ref = this.$refs.listTable

          if (!isEmpty(ref)) {
            currentSelectedList.forEach(item =>
              ref.toggleRowSelection(item, true)
            )
          }
        })
      }
    },
    getNonCurrentList(selectedList) {
      let { resourceSubModuleName, modulesList, resourceModule } = this
      let currentModuleList = selectedList.filter(
        item => modulesList.findIndex(list => list.id === item.value) === -1
      )

      if (resourceModule) {
        return currentModuleList.filter(
          list => list.subModule === resourceSubModuleName
        )
      }

      return currentModuleList
    },
    getColumnAlignment(field) {
      let { field: fieldObj } = field
      let { dataTypeEnum } = fieldObj || {}
      return dataTypeEnum === 'DECIMAL' ? 'right' : 'left'
    },
    handleSelection(selectedList) {
      let nonCurrentItems = this.getNonCurrentList(this.selectedItem)
      let currentItems = selectedList.map(list => {
        let { id, resourceTypeEnum } = list
        let { name } = this.mainField || { name: 'name' }
        let selectedItem = {
          label: list[name],
          value: id,
        }

        if (this.resourceModule) {
          selectedItem['subModule'] = resourceTypeEnum.toLowerCase()
        }

        return selectedItem
      })

      this.selectedItem = [...nonCurrentItems, ...currentItems]
      this.selectedItemId = this.selectedItem.map(list => list.value)
    },
    handleClose(item) {
      let index = this.selectedItem.findIndex(list => list.label === item)

      this.selectedItem.splice(index, 1)
      this.selectedItemId = this.selectedItem.map(list => list.value)
      this.onDataLoad()
    },
    quickAddRecordToggle() {
      this.$set(this, 'canShowQuickCreate', !this.canShowQuickCreate)
    },
    setAddedRecord(props) {
      let { record } = props
      this.selectedItemId = (record || {}).id
      this.modulesList.push(record)
      this.setSelectedItem()
    },
  },
}
</script>

<style lang="scss">
.fc-dialog-center-container {
  &.f-lookup-wizard {
    overflow: hidden;

    > .el-dialog {
      max-width: 998px;
    }
    .header {
      min-height: 35px;
    }
    .el-dialog__header {
      padding: 15px 20px;
    }
    .el-dialog__body {
      padding: 0;
      .fc-table-td-height {
        .el-table {
          tr {
            td:first-child + td,
            th:first-child + th {
              padding-left: 0px !important;
            }
          }
        }
      }
      .search-filter-tag {
        font-size: 13px;
        display: flex;
        letter-spacing: 0.5px;
        color: #748893;
        border-radius: 3px;
        background-color: #eaeef0;
        border: none;
        padding-top: 2px;
        margin-right: 10px;
        margin-bottom: 10px;
      }

      .search-filter-tag:hover {
        border-radius: 3px;
        background-color: #ddf1f4;
        font-size: 13px;
        letter-spacing: 0.5px;
        color: #38a1ae;
        border: none;
        padding-top: 2px;
        cursor: pointer;
      }
    }
    .fc-input-full-border2 {
      .el-input__inner {
        height: 35px !important;
      }
    }
    .separator {
      font-size: 18px;
    }
    .cell {
      .name {
        white-space: nowrap;
        text-overflow: ellipsis;
        overflow: hidden;
      }
    }
    .search-icon {
      fill: #91969d;
    }

    .lookup-wizard-search {
      .fc-subheader-right-search {
        bottom: 0px;
      }
      .el-input__suffix {
        display: none;
      }
      .el-input__icon.el-icon-search.filter-space-search-icon3 {
        right: 5px;
        cursor: pointer;
      }
    }
  }
  .task-icon {
    fill: #dddddd;
  }
  .fc-create-btn {
    padding: 5px 7px;
  }

  .quick-filter {
    margin-left: 20px;
    &:last-of-type {
      margin-right: 20px;
    }
  }
}
.lookup-wizard-scroll {
  max-height: 500px;
  overflow-y: scroll;
  padding-bottom: 100px;
}
</style>
<style lang="scss">
.action-badge {
  border-radius: 12px;
  border: solid 1px #39b2c2;
  color: #39b2c2;
  font-size: 11px;
  padding: 5px 10px;
}
.fc-wizard-table-width100 {
  .el-table__header,
  .el-table__body {
    width: 100% !important;
  }
}
</style>
