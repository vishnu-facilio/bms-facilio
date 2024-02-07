<template>
  <div class="hot-table-wrapper-container">
    <div
      class="relative"
      v-if="!$validation.isEmpty(hotTableSettingsData)"
      ref="hotTableContainer"
    >
      <div
        v-if="isPasteInProgress"
        v-loading="isPasteInProgress"
        class="loading-container"
      ></div>
      <hot-table
        :settings="hotTableSettingsData"
        ref="hotTableInstance"
      ></hot-table>
    </div>
    <!-- Editor Components -->
    <el-input
      ref="inputComponent"
      v-if="canShowInput"
      v-model="proxyDataSetter"
      class="fc-handson-input hide"
    ></el-input>
    <el-select
      ref="selectComponent"
      v-if="canShowSelect"
      :key="`unit-dropdown`"
      v-model="proxyDataSetter"
      class="fc-handson-input hide"
      @mousedown.native.stop="$event => onMouseDownEvent($event)"
      @keydown.native.stop="$event => onKeyDownEvent($event)"
      @change="handleSelectChange"
      :automatic-dropdown="true"
      :popper-append-to-body="false"
      :placeholder="selectPlaceHolder"
      :loading="(activeSelectField || {}).isSelectOptionsLoading || false"
      :loading-text="$t('common._common.searching')"
      filterable
    >
      <el-option
        v-for="(option, index) in selectOptions"
        :key="index"
        :label="option.label"
        :value="option.value"
      ></el-option>
    </el-select>

    <f-date-picker
      v-if="canShowPicker"
      ref="pickerComponent"
      v-model="proxyDataSetter"
      :type="hotTableSettingsData.columns[activeColumnIndex].pickerType"
      class="fc-input-full-border2 form-date-picker fc-handson-picker"
      :format="EL_PICKER_DISPLAY_FORMAT"
      @pickerChange="activeEditorInstance.finishEditing()"
      v-bind:appendToBody="false"
      @mousedown.native.stop="$event => onMouseDownEvent($event)"
      v-bind:arrowControl="true"
    ></f-date-picker>

    <!--
        TO Implementation of site ID filter and lookupwizard (formmodel setting value)
        should  per row and not per form like FWebForm
    -->
    <FLookupField
      v-if="canShowLookup"
      class="fc-handson-lookup"
      ref="lookUpComponent"
      :model.sync="proxyDataSetter"
      :field="activeLookupField"
      :popperAppendToBody="false"
      :skipLoading="true"
      :fetchOptionsMethod="getLookupOptions ? getLookupOptionsForColumn : null"
      :hideDropDown="
        $fieldUtils.isChooserTypeField(activeLookupField) &&
          !(activeLookupField.config || {}).isFiltersEnabled
      "
      @recordSelected="handleRecordSelected"
      @mousedown.native.stop="$event => onMouseDownEvent($event)"
      @keydown.native.stop="$event => onKeyDownEvent($event)"
      @showLookupWizard="showLookupWizard"
    ></FLookupField>
    <div v-if="canShowLookupWizard">
      <FLookupFieldWizard
        :canShowLookupWizard.sync="canShowLookupWizard"
        :selectedLookupField="selectedLookupField"
        :specialFilterValue="specialFilterValue"
        @setLookupFieldValue="setLookupFieldValue"
        @closeWizard="handleCloseWizard"
      ></FLookupFieldWizard>
    </div>
    <div v-if="canShowFilterDialog">
      <HandsonColumnFilter
        :canShowFilterDialog.sync="canShowFilterDialog"
        :currentFilterColumn.sync="currentFilterColumn"
        :filteredCondition.sync="filteredCondition"
      >
      </HandsonColumnFilter>
    </div>
  </div>
</template>
<script>
import Constants from 'util/constant'
import { isEmpty, isFunction } from '@facilio/utils/validation'
import { HotTable } from '@handsontable/vue'
import FDatePicker from 'pages/assets/overview/FDatePicker'
import FLookupField from 'src/components/forms/FLookupField'
import FLookupFieldWizard from '@/FLookupFieldWizard'
import HandsonColumnFilter from '@/handsontable/HandsonColumnFilter'
import hottableEvents from './hottable/hottableEvents'
import hottableGetterSetter from './hottable/hottableGetterSetter'
import hottableSetup from './hottable/hottableSetup'

export default {
  components: {
    HotTable,
    FDatePicker,
    FLookupField,
    FLookupFieldWizard,
    HandsonColumnFilter,
  },
  mixins: [hottableEvents, hottableGetterSetter, hottableSetup],
  props: {
    hotTableSettingsData: {
      type: Object,
      required: true,
    },
    isPasteInProgress: {
      type: Boolean,
    },
    filterArr: {
      type: Array,
    },
    getLookupOptions: {
      type: [Function, null],
    },
    getSelectOptions: {
      type: Function,
      default: () => {},
    },
    getDynamicPlaceHolder: {
      type: Function,
      default: () => {},
    },
    getSpecialFilterValue: {
      type: Function,
      default: () => {},
    },
    getReadingOptions: {
      type: Function,
      default: () => {},
    },
    onPasteEventHook: {
      type: Function,
      default: () => {},
    },
    afterPasteHook: {
      type: Function,
      default: () => {},
    },
    afterRowPaste: {
      type: Function,
      default: () => {},
    },
    handleAfterDocumentKeyDown: {
      type: Function,
      default: () => {},
    },
    preBeforePasteEventHanlder: {
      type: Function,
      default: () => {},
    },
    showEnumInputWizard: {
      type: Function,
      default: () => {},
    },
    resetActiveRowData: {
      type: Function,
      default: () => {},
    },
    showSetInputValues: {
      type: Function,
      default: () => {},
    },
    pasteDataMap: {
      type: Object,
    },
    onAutoFillRowChange: {
      type: Function,
      default: () => {},
    },
    onRowChange: {
      type: Function,
      default: () => {},
    },
    showCustomFilter: {
      type: Boolean,
      default: false,
    },
    handleSorting:{
      type: Function,
      default: () => {},
    }
  },
  data() {
    return {
      EL_PICKER_DISPLAY_FORMAT: Constants.EL_PICKER_DISPLAY_FORMAT,
      canShowInput: false,
      canShowSelect: false,
      canShowPicker: false,
      canShowLookup: false,
      activePropName: null,
      activeRowIndex: null,
      activeColumnIndex: null,
      activeEditorInstance: null,
      isLoading: true,
      canShowLookupWizard: false,
      selectedLookupField: null,
      cellsErrorMap: {},
      canShowFilterDialog: false,
      currentFilterColumn: null,
      specialFilterValue: {},
      physicalRowArr: [],
      previousData: null,
    }
  },
  computed: {
    hotTableData() {
      let { hotTableSettingsData } = this
      let { data } = hotTableSettingsData
      return data || []
    },
    proxyDataSetter: {
      get() {
        let { activeRowIndex, hotTableData, activePropName } = this
        let physicalRow = this.getPhysicalRowIndex(activeRowIndex)
        let currentData = hotTableData[physicalRow] || {}
        return this.$getProperty(currentData, `${activePropName}`, null)
      },
      set(value) {
        let { activeRowIndex, activePropName, hotTableData } = this
        let physicalRow = this.getPhysicalRowIndex(activeRowIndex)
        let currentData = hotTableData[physicalRow] || {}
        this.$setProperty(currentData, `${activePropName}`, value)
        /*
          set property doesnot triggers the change, hence to rerender we're
          setting using Vue setter.
        */
        this.$set(hotTableData, physicalRow, currentData)
      },
    },
    activeColumn() {
      let { hotTableSettingsData, activeColumnIndex } = this
      let { columns } = hotTableSettingsData
      if (!isEmpty(columns)) {
        return columns[activeColumnIndex] || {}
      }
      return {}
    },
    // This field only exists if column of select type having different options for every row
    activeSelectField() {
      let { activeColumn } = this
      if (!isEmpty(activeColumn)) {
        let { field } = activeColumn
        return field || {}
      }
      return {}
    },
    activeLookupField() {
      let { activeColumn } = this
      return activeColumn.field || null
    },
    selectOptions: {
      get() {
        let { activeColumn, activeSelectField } = this
        if (!isEmpty(activeSelectField)) {
          return activeSelectField.options || []
        }
        return activeColumn.options || []
      },
      set(value) {
        let { activeColumn, activeSelectField } = this
        if (!isEmpty(activeSelectField)) {
          this.$set(activeSelectField, 'options', value || [])
        } else {
          this.$set(activeColumn, 'options', value || [])
        }
      },
    },
    selectPlaceHolder() {
      let { activeColumn, activeRowIndex } = this
      if (!isEmpty(activeColumn)) {
        let { isDynamicPlaceHolderText } = activeColumn
        if (isDynamicPlaceHolderText) {
          let text = this.getDynamicPlaceHolder({ row: activeRowIndex })
          return text || ''
        } else {
          let { placeHolderText } = activeColumn
          return placeHolderText || ''
        }
      }
      return ''
    },
    filteredCondition: {
      get() {
        return this.filterArr || []
      },
      set(value) {
        this.$emit('update:filterArr', value)
      },
    },
  },
  created() {
    this.$set(this, 'isLoading', true)
    this.setupHotTableSettingsData()
    this.$set(this, 'isLoading', false)
  },
  methods: {
    /*
      Caching labels for selected options for both lookup and select fields
      Custom Renderer needs ID -> labels map to render
    */
    cacheLookUpLabels(option) {
      if (!this.activeLookupField.optionsCache) {
        this.$set(this.activeLookupField, 'optionsCache', {})
      }
      this.$set(this.activeLookupField.optionsCache, option.value, option.label)
    },
    handleCloseWizard(data) {
      let { hotInstance } = this.getHotTableInstance()
      if (data.field.selectedItems[0]?.value) {
        this.previousData.isAlreadyInvalid = true
        this.previousData.isValidEntry = true
        this.pushOrRemoveError(this.previousData)
      }
      hotInstance.render()
    },
    cacheSelectLabels(value) {
      let { activeSelectField } = this
      if (!isEmpty(activeSelectField) && !isEmpty(value)) {
        let { options, optionsCache = {} } = activeSelectField
        let selectedOption =
          options.find(option => option.value === value) || {}
        let selectedOptionLabel = selectedOption.label || ''
        this.$set(optionsCache, value, selectedOptionLabel)
      }
    },
    setLookupFieldValue(props) {
      let { field } = props
      let { selectedItems } = field || {}
      let [selectedItem] = selectedItems
      let options = []
      let { label, value } = selectedItem
      options = [{ label, value }]
      this.$set(this.activeLookupField, 'options', options)
      this.$set(this, 'specialFilterValue', {})
      this.proxyDataSetter = value
      this.handleRecordSelected(selectedItem)
    },
    /* Open dialog related methods */
    showLookupWizard(field, canShow) {
      let { activeColumnIndex, activeRowIndex } = this
      this.$set(this, 'selectedLookupField', field)
      this.$set(this, 'canShowLookupWizard', canShow)
      if (isFunction(this.getSpecialFilterValue)) {
        let filters = this.getSpecialFilterValue({
          row: activeRowIndex,
          column: activeColumnIndex,
        })
        this.$set(this, 'specialFilterValue', filters)
      }
    },

    openFilterDialog(column) {
      let { showCustomFilter } = this || {}
      if (showCustomFilter) {
        this.$emit('openCustomFilter', { column })
      } else {
        this.$set(this, 'canShowFilterDialog', true)
        this.$set(this, 'currentFilterColumn', column)
      }
    },
    cellsErrorMaps(){
      let { cellsErrorMap} = this
      return cellsErrorMap
    },
    pushOrRemoveError(props) {
      this.previousData = props
      let { isValidEntry, isAlreadyInvalid, id, propName } = props
      let { cellsErrorMap } = this

      if (!id) return

      if (!isValidEntry) {
        if (!isEmpty(cellsErrorMap[id])) {
          // Don't have to push, if the entry is invalid again and again
          if (!isAlreadyInvalid) {
            cellsErrorMap[id].push(propName)
          }
        } else {
          cellsErrorMap[id] = [propName]
        }
      } else if (isAlreadyInvalid) {
        cellsErrorMap[id].splice(cellsErrorMap[id].indexOf(propName), 1)
        let isAllErrorsCleared = Object.entries(cellsErrorMap).every(
          ([, value]) => {
            return isEmpty(value)
          }
        )
          if (isAllErrorsCleared) {
          this.cellsErrorMap = {}
        }
      }
    },
    clearRowError(id) {
      let { cellsErrorMap } = this
      if (!isEmpty(id)) {
        cellsErrorMap[id] = []
        delete cellsErrorMap[id]
      }
    },
    triggerValidate(props, callback) {
      let { rows } = props
      let {
        hotTableSettingsData,
        hotTableData,
        cellsErrorMap,
        pasteDataMap,
      } = this
      let { columns, uniqueDataKey } = hotTableSettingsData || {}
      let { hotInstance } = this.getHotTableInstance() || {}
      for (let row of rows) {
        for (let column of columns) {
          let { required, validateFn, data, cellType } = column
          let rowData = hotTableData[row] || {}
          let id = !isEmpty(rowData) ? rowData[uniqueDataKey] : null
          let dataFromPasteMap = pasteDataMap[id]
            ? pasteDataMap[id][data]
            : null
          let cellValue =
            dataFromPasteMap || this.$getProperty(rowData, data, null) || {}

          let isValidEntry = true
          if (required && !isEmpty(validateFn)) {
            isValidEntry = validateFn({
              cellValue,
              cellType,
              skipRowValidation: true,
            })
          }
          let isAlreadyInvalid = (cellsErrorMap[id] || []).includes(data)
          if (!isAlreadyInvalid) {
            this.pushOrRemoveError({
              isValidEntry,
              isAlreadyInvalid,
              id,
              propName: data,
            })
          }
        }
      }
      if (!isEmpty(cellsErrorMap)) {
        hotInstance.render()
      }
      callback(isEmpty(cellsErrorMap))
    },
    onEnumIconClick(rowIndex) {
      this.showEnumInputWizard && this.showEnumInputWizard(rowIndex)
    },
    _showSetInputValues(prop) {
      return this.showSetInputValues(prop)
    },
  },
}
</script>
<style lang="scss">
.hot-table-wrapper-container {
  .el-icon-circle-close {
    margin-right: 25px;
  }
  .handsontableInput {
    box-shadow: inset 0 0 0 2px #39b2c2;
  }

  .el-select {
    height: inherit;
  }
  .fc-handson-input,
  .fc-handson-lookup,
  .fc-handson-picker {
    position: fixed;
    z-index: 20;

    .el-input {
      height: inherit;
      .el-input__inner {
        padding-left: 15px;
        padding-right: 15px;
        background-color: #ffffff;
        height: inherit !important;
        letter-spacing: 0.4px;
        color: #324056;
        text-overflow: ellipsis;
        font-weight: 400;
        white-space: nowrap;
      }
    }
  }

  .fc-handson-lookup {
    .el-input__prefix {
      right: 5px;
      left: 95%;
      z-index: 10;
    }
  }
  .fc-handson-picker {
    .el-input__prefix {
      right: 10px;
      left: unset;
    }
  }

  .fc-lookup-icon {
    color: #c0c4cc;
    cursor: pointer;
  }

  .handsontable td.htInvalid {
    background: #f9d3d3 !important;
  }

  .handsontable td.suggestion {
    background: #f9f3d3;
  }

  .handsontable {
    .colHeader {
      display: flex;
      .filter-icon {
        fill: #879eb5;
        padding-left: 5px;
      }
    }
    .ht__active_highlight {
      .colHeader {
        .filter-icon {
          fill: #000;
        }
      }
    }
  }
}
</style>
