<template>
  <div class="formbuilder-fullscreen-popup commissioning-container">
    <div class="header">
      <div class="agent">
        <div class="label">
          {{ $t('commissioning.creation.agent') }}:
          <span class="name mL5">{{ agentName }}</span>
        </div>
      </div>
      <div class="controller mL40">
        <div class="label d-flex  ">
          {{ $t('commissioning.creation.controller') }}:

          <span display="inline" class="name mL5">{{ controllersName }}</span>

          <div v-if="hoverControllersName" class="name ml5" display="inline">
            <el-popover
              placement="bottom-end"
              trigger="hover"
              :content="hoverControllersName"
            >
              <span slot="reference" class="name mL5" display="inline">{{
                hoverController
              }}</span>
            </el-popover>
          </div>
        </div>
      </div>
      <div v-if="!isLoading" class="action-btn setting-page-btn">
        <el-button
          type="secondary"
          class="fc-btn-secondary-medium shadow-none"
          :disabled="isPasteInProgress"
          @click="showConfirmDialog()"
          >{{ redirectionButtonLabel }}</el-button
        >
        <el-button
          v-if="!isPublished"
          type="primary"
          class="fc-btn-green-medium-fill shadow-none"
          :loading="isSaving"
          :disabled="isPasteInProgress"
          @click="saveCommissioning({ redirect: true })"
          >Save</el-button
        >
      </div>
    </div>
    <div v-if="!isLoading" class="tabs-bar">
      <el-tabs
        v-model="activeTab"
        type="card"
        closable
        @tab-click="handleTabClick"
        @tab-remove="removeCommissioningTab"
      >
        <el-tab-pane
          v-for="(tab, index) in commissioningTabs"
          :key="index"
          :name="`${index}`"
          :closable="index !== 0"
        >
          <div slot="label">
            <div class="points-count">
              <div class="point"></div>
              <span class="point-text">
                {{ index === 0 ? getTabName : tab.displayName }} -
                {{ getInstanceCount(tab.searchText) }}
              </span>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
      <div :class="isSearchActive ? 'active' : ''" class="search-container">
        <div
          v-if="!isSearchActive"
          class="d-flex"
          @click="openCloseSearchBox(true)"
        >
          <inline-svg
            src="svgs/search"
            class="vertical-middle cursor-pointer"
            iconClass="icon search-icon"
          ></inline-svg>
          <div class="search-label">
            {{ $t('commissioning.sheet.search_points') }}
          </div>
        </div>
        <div v-else class="d-flex">
          <el-input
            ref="commissioning-search-input"
            class="search-input mR10"
            v-model="searchText"
            @change.native="event => onSearchChange(event)"
          >
            <div slot="suffix" @click="openCloseSearchBox(false)">
              <inline-svg
                src="svgs/close"
                class="vertical-middle cursor-pointer mT8 mR5"
                iconClass="icon search-close-icon"
              ></inline-svg>
            </div>
          </el-input>
          <inline-svg
            src="svgs/search"
            class="vertical-middle cursor-pointer self-center"
            iconClass="icon search-icon"
          ></inline-svg>
        </div>
      </div>
    </div>
    <div class="search-result-container">
      <div class="d-flex">
        <div v-if="canShowSearchResults" class="filtered-points-label">
          {{ totalFilteredPoints }} {{ $t('commissioning.sheet.pointsfound') }}
        </div>
        <div
          v-if="
            canShowSearchResults ||
              (activeTab !== '0' &&
                (commissioningTabs[Number(activeTab)] || {}).searchText)
          "
          class="d-flex map-category"
        >
          <div class="label self-center">
            {{ getMapCategoryLabel() }}
          </div>
          <el-popover
            placement="bottom"
            width="200"
            trigger="hover"
            :content="mapCategoryPopoverContent"
            :disabled="!canDisableMapCategory"
          >
            <el-select
              slot="reference"
              v-model="mappedCategory"
              class="search-input"
              :popper-append-to-body="false"
              @change="handleCategoryChange"
              :disabled="canDisableMapCategory"
              filterable
            >
              <el-option
                v-for="(category, index) in categoryList"
                :key="index"
                :label="category.label"
                :value="category.value"
              ></el-option>
            </el-select>
          </el-popover>
        </div>
      </div>
    </div>
    <div class="filters-container">
      <div class="d-flex">
        <el-dropdown @command="onViewChange">
          <el-button type="primary">
            <span class="f13">{{ currentViewLabel }}</span
            ><i class="el-icon-arrow-down el-icon--right"></i>
          </el-button>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item
              v-for="(view, index) in viewsArr"
              :key="index"
              :command="view"
            >
              {{ $t(`commissioning.sheet.filter_${view}`) }}
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </div>
    <HotTableWrapper
      v-if="!$validation.isEmpty(hotTableSettingsData)"
      ref="hotTableRef"
      :hotTableSettingsData="hotTableSettingsData"
      :getLookupOptions="getLookupOptions"
      :getSelectOptions="getSelectOptions"
      :getDynamicPlaceHolder="getDynamicPlaceHolder"
      :getSpecialFilterValue="getSpecialFilterValue"
      :getReadingOptions="getReadingOptions"
      :onPasteEventHook="onPasteEventHook"
      :afterPasteHook="afterPasteHook"
      :onAutoFillRowChange="onAutoFillRowChange"
      :afterRowPaste="setupUnitInputs"
      :preBeforePasteEventHanlder="preBeforePasteEventHanlder"
      :handleAfterDocumentKeyDown="handleAfterDocumentKeyDown"
      :showEnumInputWizard="showEnumInputWizard"
      :resetActiveRowData="resetActiveRowData"
      :showSetInputValues="showSetInputValues"
      :isPasteInProgress.sync="isPasteInProgress"
      :filterArr.sync="filterArr"
      :pasteDataMap.sync="pasteDataMap"
      :handleSorting="handleSorting"
      @onHotTableSelectChange="handleSelectOrLookupChange"
      @onHotTableLookupFieldChange="handleSelectOrLookupChange"
    ></HotTableWrapper>
    <!-- Set Input Values Wizard -->
    <CommissioningSetEnum
      v-if="canShowEnumWizard"
      :canShowEnumWizard.sync="canShowEnumWizard"
      :activeSetInputRowIndex.sync="activeSetInputRowIndex"
      :currentRowData="currentRowData"
      :selectedEnumReading="selectedEnumReading"
      :getPasteDataValue="getPasteDataValue"
      @updateEnumInputValues="updateEnumInputValues"
    ></CommissioningSetEnum>
  </div>
</template>
<script>
import HotTableWrapper from './HotTableWrapper'
import CommissioningEventMixin from './mixins/CommissioningEventMixin'
import CommissioningSetupMixin from './mixins/CommissioningSetupMixin'
import CommissioningGetterSetterMixin from './mixins/CommissioningGetterSetterMixin'
import { findRouteForTab, tabTypes } from '@facilio/router'
import CommissioningSetEnum from './CommissioningSetEnum'
import { isNumber, isEmpty } from '@facilio/utils/validation'
import cloneDeep from 'lodash/cloneDeep'
import { API } from '@facilio/api'

export default {
  mixins: [
    CommissioningEventMixin,
    CommissioningSetupMixin,
    CommissioningGetterSetterMixin,
  ],
  components: {
    HotTableWrapper,
    CommissioningSetEnum,
  },
  data() {
    return {
      agentName: '',
      controllersName: '',
      hoverControllersName: '',
      hoverControllerCount: 0,
      instances: [],
      filteredInstances: [],
      isLoading: false,
      hotTableSettingsData: {},
      isSearchActive: false,
      searchText: null,
      mappedCategory: null,
      canShowSearchResults: false,
      commissioningTabs: [
        {
          name: 'allpoints',
          displayName: this.$t('commissioning.creation.allpoints'),
          searchText: null,
          mappedCategoryId: null,
        },
      ],
      activeTab: '0',
      isSaving: false,
      isPasteInProgress: false,
      filterArr: [],
      viewsArr: ['all_points', 'mapped', 'unmapped'],
      appliedView: 'all_points',
      canShowEnumWizard: false,
      activeSetInputRowIndex: null,
      isPublished: false,
      pasteDataMap: {},
      selectedEnumReading: {},
    }
  },
  computed: {
    logId() {
      let { $route } = this
      let { params } = $route || {}
      let { logId } = params || {}
      return logId
    },
    hoverController() {
      let { hoverControllerCount } = this || {}
      return `, +${hoverControllerCount} more`
    },
    categoryList() {
      let categoryList = this.$store.state.assetCategory || []
      categoryList = categoryList.map(option => {
        let { displayName, id } = option
        return {
          label: displayName,
          value: id,
        }
      })
      return categoryList || []
    },
    totalInstanceCount() {
      let { filteredInstances, instances } = this
      let instancesLen = (instances || []).length
      let filteredInstancesLen = (filteredInstances || []).length
      let finalLen = instancesLen
      if (finalLen !== filteredInstancesLen) {
        finalLen = instancesLen - filteredInstancesLen
      }
      return finalLen
    },
    totalFilteredPoints() {
      let { filteredInstances } = this
      return (filteredInstances || []).length
    },
    getTabName() {
      let { commissioningTabs } = this
      return commissioningTabs.length > 1
        ? `${this.$t('commissioning.sheet.remaining_points')}`
        : `${this.$t('commissioning.creation.allpoints')}`
    },
    currentViewLabel() {
      let { appliedView } = this
      return this.$t(`commissioning.sheet.filter_${appliedView}`)
    },
    canDisableMapCategory() {
      let { filterArr, appliedView } = this
      return !isEmpty(filterArr) || appliedView !== 'all_points'
    },
    mapCategoryPopoverContent() {
      let { appliedView, filterArr } = this
      let content = this.$t('commissioning.sheet.removeview')
      if (appliedView !== 'all_points' && !isEmpty(filterArr)) {
        content = this.$t('commissioning.sheet.remove_filter_view')
      } else if (!isEmpty(filterArr)) {
        content = this.$t('commissioning.sheet.removefilter')
      }
      return content
    },
    hotTableData() {
      let { hotTableSettingsData } = this
      let { data } = hotTableSettingsData
      return data || []
    },
    currentRowData() {
      let { hotTableData, activeSetInputRowIndex } = this
      return hotTableData[activeSetInputRowIndex] || {}
    },
    redirectionButtonLabel() {
      let { isPublished } = this
      return isPublished ? 'Back' : 'Cancel'
    },
  },
  watch: {
    categoryList: {
      handler(newVal) {
        this.setColumnOptions(newVal, 'categoryId')
      },
    },
    filterArr: {
      handler(newVal) {
        let {
          activeTab,
          commissioningTabs,
          hotTableSettingsData,
          filteredInstances,
          isSearchActive,
          searchText: globalSearchText,
        } = this
        let searchText = ''
        let { columns } = hotTableSettingsData
        if (!isEmpty(activeTab)) {
          searchText =
            (commissioningTabs[Number(activeTab)] || {}).searchText || ''
        } else if (isSearchActive) {
          searchText = globalSearchText
        }
        filteredInstances = this.getFilteredInstances({
          searchText,
          allInstances: true,
        })
        if (!isEmpty(newVal)) {
          filteredInstances = (filteredInstances || []).filter(point => {
            return newVal.every(filter => {
              let { columnIndex, searchText } = filter
              let currentColumn = columns[columnIndex] || {}
              let { data } = currentColumn
              return String(point[data])
                .toLowerCase()
                .trim()
                .includes(searchText.toLowerCase().trim())
            })
          })
        }
        this.setFilteredInstances(filteredInstances)
      },
      deep: true,
    },
    filteredInstances: {
      handler(newVal) {
        this.$set(this.hotTableSettingsData, 'data', newVal)
      },
    },
    activeTab: {
      handler(newVal) {
        // Disable category column for the tabs already created
        let { hotTableSettingsData } = this
        let { columns } = hotTableSettingsData
        let categoryColumn = columns.find(
          column => column.data === 'categoryId'
        )
        if (!isEmpty(categoryColumn)) {
          if (!isEmpty(newVal)) {
            categoryColumn.readOnly = newVal !== '0'
          }
        }
      },
    },
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
    this.loadDefaultMetricUnits()
    this.initializeConstants()
    this.init()
  },
  methods: {
    showEnumInputWizard(row) {
      let { hotTableData } = this
      let physicalRow = this.getPhysicalRowIndex(row)
      let activeRowData = hotTableData[physicalRow]
      let fieldId = this.getFieldId({ rowData: activeRowData })
      let categoryId = this.getCategoryId({ rowData: activeRowData })
      this.getReadingOptions({ categoryId, row }).then(options => {
        let selectedOption = options.find(option => option.value === fieldId)
        if (!isEmpty(selectedOption)) {
          this.$set(this, 'selectedEnumReading', selectedOption)
          this.$set(this, 'activeSetInputRowIndex', physicalRow)
          this.$set(this, 'canShowEnumWizard', true)
        }
      })
    },
    openCloseSearchBox(value) {
      let { searchText } = this
      this.$set(this, 'isSearchActive', value)
      this.$set(this, 'mappedCategory', null)
      this.$set(this, 'filterArr', [])
      if (value) {
        this.$nextTick(() => {
          let { $refs } = this
          this.$set(this, 'activeTab', null)
          if (
            !isEmpty($refs) &&
            !isEmpty($refs['commissioning-search-input'])
          ) {
            $refs['commissioning-search-input'].focus()
          }
        })
      } else if (!isEmpty(searchText)) {
        let firstTabInstances = this.getFilteredInstances({
          searchText: '',
        })
        this.setFilteredInstances(firstTabInstances)
        this.$set(this, 'activeTab', '0')
        this.$set(this, 'searchText', '')
        this.$set(this, 'canShowSearchResults', false)
      } else if (isEmpty(searchText)) {
        this.$set(this, 'activeTab', '0')
      }
    },
    addNewCommissioningTab(value, instances) {
      let { commissioningTabs, searchText } = this
      let newTab = {
        name: `${searchText} ${value}`,
        displayName: searchText,
        searchText,
        mappedCategoryId: value,
      }
      commissioningTabs.push(newTab)
      this.setFilteredInstances(instances)
      this.$set(this, 'isSearchActive', false)
      this.$set(this, 'canShowSearchResults', false)
      this.$set(this, 'activeTab', `${commissioningTabs.length - 1}`)
      this.$set(this, 'searchText', '')
    },
    updateEnumInputValues(props) {
      let { hotTableData, instances } = this
      let { valuesArr, applySameValues, activeSetInputRowIndex } = props
      let physicalRow = this.getPhysicalRowIndex(activeSetInputRowIndex)
      let selectedRow = hotTableData[physicalRow]
      let selectedReadingId = this.getFieldId({ rowData: selectedRow })
      let selectedResourceId = this.getResourceId({ rowData: selectedRow })
      if (
        applySameValues &&
        !isEmpty(instances) &&
        !isEmpty(selectedResourceId)
      ) {
        instances.forEach(instance => {
          let { resourceId } = instance
          let fieldId = this.getFieldId({ rowData: instance })
          if (fieldId === selectedReadingId && !isEmpty(resourceId)) {
            instance.inputValues = valuesArr
          }
        })
      } else {
        selectedRow.inputValues = valuesArr
      }
    },
    removeCommissioningTab(index) {
      let { commissioningTabs, activeTab } = this
      if (index !== '0') {
        let isActiveTab = index === activeTab
        let tabInstances = []
        let activeTabSearchText =
          (commissioningTabs[Number(activeTab)] || {}).searchText || ''
        commissioningTabs.splice(Number(index), 1)
        this.$set(this, 'commissioningTabs', commissioningTabs)
        if (isActiveTab) {
          tabInstances = this.getFilteredInstances({
            searchText: '',
          })
          this.setFilteredInstances(tabInstances)
          this.$set(this, 'activeTab', '0')
        } else {
          // have to reset activetab index if tab removed before active tab
          let currentIndexOfActiveTab = commissioningTabs.findIndex(
            tab => tab.searchText === activeTabSearchText
          )
          if (activeTab !== `${currentIndexOfActiveTab}`) {
            this.$set(this, 'activeTab', `${currentIndexOfActiveTab}`)
          }
          tabInstances = this.getFilteredInstances({
            searchText: activeTabSearchText,
            allInstances: true,
          })
          this.setFilteredInstances(tabInstances)
        }
        this.$set(this, 'filterArr', [])
      }
    },
    async saveCommissioning() {
      let { $refs, logId, commissioningTabs, instances } = this
      let cellsErrorMap = $refs['hotTableRef'].cellsErrorMaps()
      if (!isEmpty(cellsErrorMap)) {
        this.$message.error(
          `${this.$t('commissioning.sheet.commissioning_save_error')}`
        )
        return
      }
      let url = `v2/commissioning/update`
      let prop = {
        log: {
          id: logId,
          points: this.serializePoints(instances),
          clientMeta: {
            commissioningTabs,
          },
        },
      }
      this.isSaving = true
      let { data, error } = await API.post(url, prop)
      if (!isEmpty(error)) {
        let { message } = error || {}
        this.$message.error(message)
      } else {
        let { result } = data || {}
        if (result == 'success') {
          this.$message.success(
            `${this.$t('commissioning.sheet.commissioning_save_success')}`
          )
          this.redirectToCommissioningList()
        }
      }
      this.isSaving = false
    },
    serializePoints(data) {
      // Remove empty row
      let filteredData = data.filter(datum => !isEmpty(datum.name))
      let clonedData = cloneDeep(filteredData)
      if (!isEmpty(clonedData)) {
        clonedData.forEach((data, index) => {
          Object.entries(data).forEach(([key, value]) => {
            if (
              ['categoryId', 'resourceId', 'fieldId', 'unit'].includes(key) &&
              !isNumber(value)
            ) {
              let pasteValue = this.getPasteDataValue(key, {
                rowData: clonedData[index],
              })
              clonedData[index][key] = pasteValue
            }
            if (isEmpty(value)) {
              clonedData[index][key] = null
            }
          })
          if (!isEmpty(data.instanceTypeLabel)) {
            delete data.instanceTypeLabel
          }
          if (!isEmpty(data.dataType)) {
            delete data.dataType
          }
          if (!isEmpty(data.canShowSetInputIcon)) {
            delete data.canShowSetInputIcon
          }
        })
      }
      return clonedData
    },
    showConfirmDialog() {
      let { isPublished } = this
      if (isPublished) {
        this.redirectToCommissioningList()
      } else {
        let dialogObj = {
          title: 'You have not saved your changes',
          htmlMessage: 'Are you sure want to exit commissioning?',
          rbDanger: true,
          rbLabel: 'Confirm',
        }
        this.$dialog.confirm(dialogObj).then(value => {
          if (value) {
            this.redirectToCommissioningList()
          }
        })
      }
    },
    redirectToCommissioningList() {
      let { name } = findRouteForTab(tabTypes.CUSTOM, {
        config: {
          type: 'commissioning',
        },
      })
      name && this.$router.push({ name })
    },
  },
}
</script>
