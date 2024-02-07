<template>
  <div class="pivot-builder-new-ui">
    <el-dialog
      custom-class="new-pivot-builder-dialog-box"
      :visible="true"
      :append-to-body="true"
      :show-close="false"
      width="100%"
      height="100vh"
    >
      <div
        class="pivot-builder-new  height-100  d-flex flex-col white-background"
      >
        <div
          class="pivot-header-main-content"
          :style="{
            width: '100%',
          }"
        >
          <header
            class="fc-report-header flex-shrink-0 header-pivot show-edit-icon"
            :style="{
              'justify-content': 'space-between',
            }"
          >
            <div class="title-and-icon-section">
              <div class="pull-left report-title-section">
                <el-breadcrumb>
                  <el-breadcrumb-item
                    class="bread-crumb-text"
                    :to="{ path: getBreadCrumbPath(1) }"
                    >Analytics</el-breadcrumb-item
                  >
                  <el-breadcrumb-item
                    class="bread-crumb-text"
                    :to="{ path: getBreadCrumbPath(2) }"
                    >Pivot</el-breadcrumb-item
                  >
                  <el-breadcrumb-item
                    class="bread-crumb-text"
                    :to="{
                      path: getBreadCrumbPath(3),
                    }"
                    >{{ reportFolderName }}</el-breadcrumb-item
                  >
                </el-breadcrumb>
              </div>
              <div class="icon-and-title">
                <div
                  :class="
                    `left-pane-icon-section ${
                      !leftPaneVisibility ? 'left-pane-active' : ''
                    }`
                  "
                  style="padding-top:1px;"
                >
                  <img
                    src="~statics/pivot/menu.svg"
                    width="18px"
                    height="18px"
                    :class="
                      `hamburger-menu-builder ${
                        !leftPaneVisibility ? 'ham-menu-builder-active' : ''
                      }`
                    "
                    @click="leftPaneVisibility = !leftPaneVisibility"
                  />
                </div>
                <div class="newreport-title-input">
                  {{ reportObject.name }}
                </div>
                <el-button
                  size="mini"
                  class="edit-btn"
                  @click="
                    () => {
                      this.editMode = true
                      this.moduleSlectVisibility = true
                    }
                  "
                  style="margin-left:10px;padding:5px !important;"
                  circle
                >
                  <InlineSvg
                    src="svgs/pivot/edit"
                    iconClass="icon-sm flex icon"
                  ></InlineSvg>
                </el-button>
              </div>
            </div>
            <div class="pull-right report-btn-section">
              <SavePivotReportComponent
                :report="reportObject"
                :config="config"
                @reportSaved="reportSaved"
                @savingStarted="spinnerStatToggle(3)"
                @savingEnded="spinnerStatToggle(4)"
              ></SavePivotReportComponent>
            </div>
          </header>
          <div class="pivot-builder-new-body">
            <div
              class="d-flex pivot-builder-body items-stretch"
              v-show="leftPaneVisibility"
              :style="{
                display: leftPaneVisibility ? 'flex' : 'none',
                border: '1px solid #ebedf4',
                height: 'calc(100vh - 60px)',
              }"
            >
              <div
                class="overflow-hidden flex-shrink-0 pivot-left-pane  pL25 pT10"
              >
                <div>
                  <div class="config-section">
                    <div class="PivotDimentions">
                      <PivotDimentions
                        :pivotBaseModuleName="config.moduleName"
                        :builderConfig="config"
                        :pivotResponse="pivotTable"
                        :initaialRows="config.rows"
                        :activeColumn="activeColumn"
                        :criteria="config.criteria"
                        :dateFieldId="config.dateFieldId"
                        @deleteRowValue="deleteRowValue"
                        @pivotDimentionUpdated="pivotDimentionUpdated"
                        @openUserFilter="openAddCriteriaDialog"
                        @dimensionsLoaded="spinnerStatToggle(1)"
                        @activeColumnChanged="activeColumnChanged"
                      >
                      </PivotDimentions>
                    </div>
                    <div class="pivot-measures">
                      <PivotMeasures
                        :pivotBaseModuleName="config.moduleName"
                        :builderConfig="config"
                        :pivotResponse="pivotTable"
                        :initaialValues="config.values"
                        :activeColumn="activeColumn"
                        @deleteRowValue="deleteRowValue"
                        @pivotMeasureUpdated="pivotMeasureUpdated"
                        @measuresLoaded="spinnerStatToggle(2)"
                        @activeColumnChanged="activeColumnChanged"
                      >
                      </PivotMeasures>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div
              class="pivot-table-section"
              :style="{
                width: leftPaneVisibility ? 'calc( 100% - 434px )' : '100%',
              }"
            >
              <!-- <el-dialog
                :visible="!spinnerStat.measures || !spinnerStat.dimensions"
                :show-close="false"
                :append-to-body="true"
                :close-on-click-modal="false"
                width="0"
              >
                <img src="~statics/pivot/circle-loading.svg" />
              </el-dialog> -->
              <PivotTable
                :sortBy="config.sortBy"
                ref="pivotTable"
                :showTimelineFilter="config.showTimelineFilter"
                :dateOperator="config.dateOperator"
                :dateValue="config.dateValue"
                :activeColumn="activeColumn"
                :spinnerStat="spinnerStat"
                :pivotTable="pivotTable"
                :loading.sync="tableLoading"
                :isNoConfig="isNoConfig"
                :theme="config.templateJSON.theme"
                :enableColumnFormat="true"
                :columnFormatting="config.templateJSON.columnFormatting"
                :fetchPivotDataError="fetchPivotDataError"
                :diable_userfilter_drillown="
                  config.templateJSON.diable_userfilter_drillown
                "
                @sortChanged="handleTableSortIconClick"
                @pickerChanged="handlePickerChange"
                @columnResizeEvent="columnResizeEvent"
                @openColumnFormatDialog="openColumnFormatDialog"
                @openConditionalFormatDialog="openConditionalFormatDialog"
                @openCellVisualizationDialog="openCellVisualizationDialog"
                @columnPinningEvent="columnPinningEvent"
                @columnTextWarpEvent="columnTextWarpEvent"
                @headerLabelChanged="headerLabelChanged"
                @activeColumnChanged="activeColumnChanged"
                @recordCountUpdated="setRecordCount"
              >
                <div slot="tools-section" class="pivot-tool-bar-container">
                  <div
                    class="filter-section pivot-tab-hover"
                    @click="openAddCriteriaDialog"
                    style="margin-left:16px;padding:6.5px 8px;width:80px;"
                  >
                    <img
                      v-if="config.dateFieldId < 0 && config.criteria == null"
                      src="~statics/pivot/FilterIcon1.svg"
                      height="15px"
                      width="15px"
                    />
                    <img
                      v-else
                      src="~statics/pivot/filter-applied-icon.svg"
                      height="15px"
                      width="15px"
                    />
                    <div class="filter-tilte">
                      {{ $t('pivot.filters') }}
                    </div>
                  </div>
                  <PivotThemeCustomization
                    :initialTheme="config.templateJSON.theme"
                    :initialRowHieght="config.templateJSON.theme.density"
                    @rowHeightChanged="rowHeightChanged"
                    @themeConfigChanged="themeConfigChanged"
                  ></PivotThemeCustomization>
                  <HideColumnsCustomization
                    :columnConfig="config.templateJSON.columnFormatting"
                  ></HideColumnsCustomization>
                  <RowLimit
                    :recordCount="recordCount"
                    :config="config"
                    @rowsCountUpdated="configChanged"
                  >
                  </RowLimit>
                </div>
              </PivotTable>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
    <ColumnFormatDialog
      v-if="columnFormatConfig.visibility"
      :visibility="columnFormatConfig.visibility"
      :field="columnFormatConfig.field"
      :alias="columnFormatConfig.alias"
      :editConfig="columnFormatConfig.editConfig"
      :isDataColumn="columnFormatConfig.isDataColumn"
      :fieldName="columnFormatConfig.fieldName"
      @close="columnFormatConfig.visibility = false"
      @columnFormatConfig="columnFormatConfigAdded"
    >
    </ColumnFormatDialog>
    <ModuleSelectDialogBox
      :v-if="moduleSlectVisibility"
      :visibility.sync="moduleSlectVisibility"
      :moduleName.sync="config.moduleName"
      :editMode="editMode"
      :report.sync="reportObject"
      @closeDialog="moduleSlectVisibility = false"
      @reportSaved="reportSaved"
      @moduleSelected="spinnerStatToggle(3)"
    ></ModuleSelectDialogBox>
    <ConditionalFormatDialog
      v-if="conditionalFormatConfig.visibility"
      :visibility.sync="conditionalFormatConfig.visibility"
      :alias="conditionalFormatConfig.alias"
      :variables="conditionalFormatConfig.fields"
      :editConfig="conditionalFormatConfig.editConfig"
      @closeDialog="
        conditionalFormatConfig.visibility = false
        conditionalFormatConfig.editConfig = {}
      "
      @conditionFormatAdded="conditionFormatAdded"
    ></ConditionalFormatDialog>
    <cell-visualization-dialog
      v-if="cellVisualizationConfig.visibility"
      :visibility.sync="cellVisualizationConfig.visibility"
      :alias="cellVisualizationConfig.alias"
      :editConfig="cellVisualizationConfig.editConfig"
      :referenceDataColumnOptions="referenceDataColumnOptions"
      @closeDialog="
        cellVisualizationConfig.visibility = false
        cellVisualizationConfig.editConfig = {}
      "
      @cellVisualizationConfigAdded="cellVisualizationConfigAdded"
    ></cell-visualization-dialog>
    <PivotUserFilterDialogBox
      v-if="showAddCriteriaDialog"
      :visibility.sync="showAddCriteriaDialog"
      :criteria="config.criteria"
      :moduleName="config.moduleName"
      :dateFields="dateFields"
      :dateFieldId="config.dateFieldId"
      :disableUserFilterInDrilldown="
        config.templateJSON.diable_userfilter_drillown
      "
      @save="criteriaSaved"
      @disableFilterInDrilldown="disableFilterInDrilldown"
    ></PivotUserFilterDialogBox>
  </div>
</template>

<script>
import { isWebTabsEnabled, pageTypes, findRouteForTab } from '@facilio/router'
import { API } from '@facilio/api'
import NewDateHelper from '@/mixins/NewDateHelper'
import { isDateTypeField } from '@facilio/utils/field'
import { getUniqueCharAlias } from 'util/utility-methods'
import { isEmpty } from '@facilio/utils/validation'

import {
  defaultTheme,
  datePeriodOptions,
  pivotThemeOptions,
} from './PivotDefaults'
import './pivot.scss'
import PivotTable from './PivotTable'
import { deepCloneObject } from 'util/utility-methods'
import { serializeProps } from '@facilio/utils/utility-methods'
// import ConditionalFormatDialog from './ConditionalFormatDialog.vue'
// import CellVisualizationDialog from './CellVisualizationDialog.vue'

import PivotDimentions from './Components/PivotDimentions.vue'
import PivotMeasures from './Components/PivotMeasures.vue'
import SavePivotReportComponent from './Components/SavePivotReportComponent.vue'
import ConditionalFormatDialog from './Components/ConditionalFormatDialog.vue'
import ColumnFormatDialog from './Components/ColumnFormatDialog.vue'
import ModuleSelectDialogBox from './Components/ModuleSelectDialogBox.vue'
import CellVisualizationDialog from './Components/CellVisualizationDialog.vue'
import PivotUserFilterDialogBox from './Components/PivotUserFilterDialogBox.vue'
import PivotThemeCustomization from './Components/PivotThemeCustomization.vue'
import PivotRowHeightCustomization from './Components/PivotRowHeightCustomization.vue'
import HideColumnsCustomization from './Components/HideColumnsCustomization.vue'
import RowLimit from './Components/RowLimit.vue'

import { isDecimalField, isNumberField } from '../../../util/field-utils'

export default {
  name: 'PivotBuilder',
  props: ['id'],
  components: {
    PivotTable,
    ColumnFormatDialog,
    SavePivotReportComponent,
    ConditionalFormatDialog,
    CellVisualizationDialog,
    PivotDimentions,
    ModuleSelectDialogBox,
    PivotMeasures,
    PivotUserFilterDialogBox,
    PivotThemeCustomization,
    HideColumnsCustomization,
    RowLimit,
  },
  created() {
    if (this.$route.query.module && !this.$route.query.reportId) {
      API.get(`v2/modules/meta/${this.$route.query.module}`).then(
        ({ error }) => {
          if (!error) {
            this.isModuleAvailable = true
            this.config.moduleName = this.$route.query.module
          }
        }
      )
    }
    this.loadModuleOptions().then(() => {
      let reportId = this.$route.query.reportId
      if (reportId) {
        this.loadReportFolders()
        API.post('v2/report/executePivotReport', { reportId: reportId }).then(
          ({ data }) => {
            this.config.moduleName = data.report.module.name
            this.config.templateJSON = data.pivotTemplateJSON
            this.config.sortBy = data.sorting
            this.loadDateFields()
            this.reportObject.name = data.report.name

            this.config.rows = data.rows
            this.config.data = data.data
            this.config.values = data.values

            this.config.formula = data.formula
            this.config.criteria = data.criteria
            this.config.startTime = data.startTime
            this.config.endTime = data.endTime
            // if (data.startTime != -1 || data.endTime != -1) {
            //   this.config.showTimelineFilter = true
            // }
            this.reportObject = data.report
            this.reportObject.reportType = 5
            this.config.showTimelineFilter = data.dateField > 0 ? true : false
            if (this.config.showTimelineFilter) {
              this.config.dateOperator = data.dateOperator
              this.config.dateValue = data.dateOperatorValue
              this.config.dateFieldId = data.dateField
              if (!isEmpty(data.dateOffset)) {
                this.config.dateOffset = data.dateOffset
              }
            }
            this.tableLoading = true
            this.isModuleAvailable = true
            this.pivotTable = data
            this.tableLoading = false
            // this.configChanged()
            if (data?.showLimitBubble) {
              this.$set(this.config, 'showLimitBubble', data.showLimitBubble)
            } else {
              this.$set(this.config, 'showLimitBubble', false)
            }
          }
        )
      }
    })
  },
  mounted() {
    if (this.$route.query.module || this.$route.query.reportId) {
      this.moduleSlectVisibility = false
      this.spinnerStatToggle(3)
    }
  },
  watch: {
    'config.moduleName': {
      immediate: true,
      handler: function() {
        //clear all options when module changes
        if (!this.$route.query.reportId) {
          this.config.rows = []
          this.config.data = []
          this.config.formula = []
          this.loadDateFields()
          this.loadReportFolders()
        }
      },
    },
    'config.templateJSON.theme.number': {
      immediate: true,
      handler: function() {
        this.configChanged()
      },
    },
  },
  computed: {
    isNewPivotReport() {
      return !this.reportId
    },
    reportFolderName() {
      if (
        this.reportObject &&
        this.reportObject.reportFolderId > 0 &&
        !isNaN(this.reportObject.reportFolderId) &&
        this.reportFolders
      )
        return this.reportFolders.find(
          rep => rep.id === this.reportObject.reportFolderId
        )?.name
      else if (this.reportObject.reportFolderId)
        return this.reportObject.reportFolderId
      return 'Default'
    },
    reportId() {
      let reportId = this.id
      if (reportId && !isNaN(reportId)) {
        return parseInt(reportId)
      } else {
        return null
      }
    },
    isEditView() {
      if (this.$route.query.reportId) return true
      return false
    },
    dateFieldOptions() {
      let options = []
      options.push({ id: -99, displayName: 'None' })
      let dateFields = this.dateFields || []
      options.push(...dateFields)
      return options
    },
    isNoConfig() {
      return !(this.config.rows.length > 0 && this.config.values.length > 0)
    },
    referenceDataColumnOptions() {
      if (!this.pivotTable?.pivotAliasVsField) {
        return []
      }
      let fielsAliases = Object.keys(this.pivotTable?.pivotAliasVsField)
      let formulaValidFieldAliases = []
      let pivotFieldMapLocal = []
      fielsAliases.forEach(key => {
        if (key.startsWith('data')) {
          formulaValidFieldAliases.push(key)
        } else {
          let field = this.pivotTable?.pivotAliasVsField[key]
          if (isNumberField(field) || isDecimalField(field))
            formulaValidFieldAliases.push(key)
        }
      })
      formulaValidFieldAliases.forEach(alias => {
        pivotFieldMapLocal.push({
          alias: alias,
          label: this.config.templateJSON.columnFormatting[alias].label,
        })
      })
      return pivotFieldMapLocal
    },
    pivotFieldMap() {
      let fielsAliases = Object.keys(this.pivotTable?.pivotAliasVsField)
      let formulaValidFieldAliases = []
      let pivotFieldMapLocal = []
      fielsAliases.forEach(key => {
        if (!key.startsWith('formula')) {
          formulaValidFieldAliases.push(key)
        }
      })
      formulaValidFieldAliases.forEach(alias => {
        pivotFieldMapLocal.push({
          alias: alias,
          paramAlias: this.getCharAlias(alias),
          label: this.config.templateJSON.columnFormatting[alias].label,
        })
      })

      pivotFieldMapLocal.sort((a, b) => {
        if (a.paramAlias > b.paramAlias) {
          return 1
        }
        return -1
      })
      return pivotFieldMapLocal
    },
  },
  data() {
    return {
      datePeriodOptions,
      editMode: false,
      recordCount: 0,
      spinnerStat: {
        measures: true,
        dimensions: true,
      },
      draggableOptions: {
        animation: 150,
        easing: 'cubic-bezier(1, 0, 0, 1)',
        group: 'tasksection',
        sort: true,
      },
      isModuleAvailable: false,
      editConfig: null, // edit config
      editIndex: -1, // edit index
      formatConfig: null,
      moduleLoading: true,
      moduleOptions: { systemModules: [], customModules: [] },
      rowOptions: [],
      /**  PIVOT MODULE,ROW,DATA AND COLUMNS*/

      showAddCriteriaDialog: false,
      dateFields: null,
      showSidePanel: true,

      /** *****************/
      showAddRowDialog: false,
      showAddDataDialog: false,
      showAddFormulaDialog: false,
      moduleSlectVisibility: true,

      fetchPivotDataError: false,

      leftPaneVisibility: true,
      leftPaneActiveTab: 'CONFIG', //CONFIG || SETTINGS
      tableLoading: false,
      reportObject: {
        name: '',
        reportType: 5,
      },
      reportFolders: [],
      activeColumn: null,
      pivotTable: null,
      pivotThemeOptions,
      charAliasMap: {},
      config: {
        showTimelineFilter: false,
        templateJSON: {
          theme: defaultTheme,
          columnFormatting: {},
          diable_userfilter_drillown: false,
        },
        builderV2: true,
        moduleName: null,
        criteria: null,
        rows: [],
        values: [],
        formula: [],
        drillDown: 2,
        dateOperator: 28,
        dateValue: null,
        dateFieldId: -99,
        startTime: -1,
        endTime: -1,
        sortBy: {
          alias: null,
          order: 3,
          limit: 50,
        },
        showLimitBubble: false,
      },
      columnFormatConfig: {
        field: null,
        visibility: false,
        isDataColumn: false,
      },
      conditionalFormatConfig: {
        variables: [],
        visibility: false,
        prop: null,
        editConfig: {},
      },
      cellVisualizationConfig: {
        alias: null,
        visibility: false,
        editConfig: {},
      },
    }
  },

  methods: {
    disableFilterInDrilldown(prop) {
      this.config.templateJSON.diable_userfilter_drillown = prop
    },
    getBreadCrumbPath(type) {
      if (isWebTabsEnabled()) {
        let { name } = findRouteForTab(pageTypes.PIVOT_VIEW)
        if (type == 2) {
          return this.$router.resolve({ name }).href
        }
        if (type == 3) {
          return this.$router.resolve({
            name,
            query: { folderId: this.reportObject.reportFolderId },
          }).href
        }
        return this.$route.fullPath
      } else {
        if (type == 1) {
          return '/app/em/analytics'
        }
        if (type == 2) {
          return '/app/em/pivot/view'
        }
        if (type == 3) {
          return `/app/em/pivot/view?folderId=${this.reportObject.reportFolderId}`
        }
      }
    },
    handleTableSortIconClick(newSortBy) {
      this.config.sortBy = newSortBy
      this.configChanged()
    },
    setRecordCount(val) {
      this.recordCount = val
    },
    spinnerStatToggle(val) {
      if (val == 1) {
        this.spinnerStat.dimensions = true
      } else if (val == 2) {
        this.spinnerStat.measures = true
      } else if (val == 3) {
        this.spinnerStat.dimensions = false
        this.spinnerStat.measures = false
      } else if (val == 4) {
        this.spinnerStat.dimensions = true
        this.spinnerStat.measures = true
      }
    },
    headerLabelChanged({ prop, label }) {
      this.config.templateJSON.columnFormatting[prop].label = label
      this.pivotTable.pivotReconstructedData.headers[prop] = label
      // this.configChanged()
    },
    toggleTimelineFilter(val) {
      //on enabling filter pivottable doesn't emit pickerChange ,only on subsequent changes so set initial start and endtimes here
      if (val) {
        let [startTime, endTime] = NewDateHelper.getDatePickerObject(
          this.config.dateOperator,
          this.config.dateValue
        ).value
        this.config.startTime = startTime
        this.config.endTime = endTime
        this.config.dateValue = startTime + ',' + endTime
      } else {
        this.config.startTime = -1
        this.config.endTime = -1
      }
      this.configChanged()
    },
    handlePickerChange(datePickerObj) {
      //set current state of date picker in config object
      let [startTime, endTime] = datePickerObj.value
      this.config.startTime = startTime

      this.config.endTime = endTime

      this.config.dateOperator = datePickerObj.operatorId
      this.config.dateValue = startTime + ',' + endTime
      if (!isEmpty(datePickerObj.offset)) {
        this.config.dateOffset = Math.abs(datePickerObj.offset)
      }
      this.configChanged()
    },
    async loadDateFields() {
      if (!this.config.moduleName) {
        return
      }

      let { data, error } = await API.get(
        `v2/modules/meta/${this.config.moduleName}`
      )

      if (error) {
        this.$message.error('Error loading date fields for module')
      } else {
        this.dateFields = data.meta.fields.filter(field => {
          return isDateTypeField(field)
        })
      }
    },
    criteriaSaved(criteria, dateFieldId) {
      this.showAddCriteriaDialog = false
      this.config.criteria = criteria
      if (dateFieldId > 0) this.config.showTimelineFilter = true
      else this.config.showTimelineFilter = false
      this.config.dateFieldId = dateFieldId
      this.configChanged()
    },
    async loadModuleOptions() {
      let resp = await API.get('/v2/automation/module')
      let { data, error } = resp
      if (error) {
        this.$message.error('Error loading module list')
      } else {
        this.moduleOptions.systemModules = data.modules.filter(e => !e.custom)
        this.moduleOptions.customModules = data.modules.filter(e => e.custom)
        this.moduleLoading = false
      }
    },
    activeColumnChanged(activeColumn) {
      this.activeColumn = activeColumn
    },
    openAddCriteriaDialog() {
      if (this.config.moduleName) {
        this.showAddCriteriaDialog = true
      }
    },
    columnResizeEvent({ width, column }) {
      this.config.templateJSON.columnFormatting[column].width = width
      this.config.templateJSON.columnFormatting[column].autoWidth = false
    },
    columnPinningEvent({ prop, value }) {
      this.config.templateJSON.columnFormatting[prop].isFixed = value
      this.configChanged()
    },
    openCellVisualizationDialog(alias) {
      this.cellVisualizationConfig.alias = alias
      if (alias.startsWith('data')) {
        let index = this.config.values.findIndex(dt => dt.alias == alias)
        if (
          index != -1 &&
          this.config.values[index].moduleMeasure.visualConfig
        ) {
          let editConfig = {
            ...this.config.values[index].moduleMeasure.visualConfig,
          }
          this.cellVisualizationConfig.editConfig = editConfig
        }
      } else {
        let index = this.config.formula.findIndex(dt => dt.alias == alias)

        if (
          index != -1 &&
          this.config.formula[index].customMeasure.visualConfig
        ) {
          let editConfig = {
            ...this.config.formula[index].customMeasure.visualConfig,
          }
          this.cellVisualizationConfig.editConfig = editConfig
        }
      }

      this.cellVisualizationConfig.visibility = true
    },
    cellVisualizationConfigAdded({ alias, visualConfig }) {
      if (alias.startsWith('data')) {
        let index = this.config.values.findIndex(dt => dt.alias == alias)
        if (index != -1)
          this.config.values[index].moduleMeasure.visualConfig = visualConfig
      } else {
        let index = this.config.formula.findIndex(dt => dt.alias == alias)
        if (index != -1)
          this.config.formula[index].customMeasure.visualConfig = visualConfig
      }

      this.cellVisualizationConfig = {
        alias: null,
        visibility: false,
        editConfig: {},
      }
      this.configChanged()
    },
    themeConfigChanged(themeConfig) {
      this.config.templateJSON.theme = {
        ...this.config.templateJSON.theme,
        ...themeConfig,
      }
    },
    rowHeightChanged(rowHeight) {
      this.config.templateJSON.theme.density = rowHeight
    },
    openColumnFormatDialog(prop) {
      if (this.pivotTable && !prop?.startsWith('formula')) {
        this.columnFormatConfig.field = this.pivotTable.pivotAliasVsField[prop]
      } else {
        let field
        if (prop && prop.startsWith('data')) {
          field = this.config.values.find(dt => dt.alias == prop)
        } else if (prop && prop.startsWith('row')) {
          field = this.config.rows.find(row => row.alias == prop)
        } else if (prop && prop.startsWith('formula')) {
          field = this.config.formula.find(formula => formula.alias == prop)
        }
        if (field && !prop?.startsWith('formula')) {
          this.columnFormatConfig.field = field.field
        } else {
          this.columnFormatConfig.field = field
        }
      }

      // if (prop.startsWith('data')) {
      //   this.columnFormatConfig.isDataColumn = true
      // }

      if (this.config.templateJSON.columnFormatting[prop].format) {
        this.columnFormatConfig.editConfig = {
          ...this.config.templateJSON.columnFormatting[prop].format,
        }
      }
      if (this.config.templateJSON.columnFormatting[prop].label) {
        this.columnFormatConfig.fieldName = this.config.templateJSON.columnFormatting[
          prop
        ].label
      }

      this.columnFormatConfig.alias = prop
      this.columnFormatConfig.visibility = true
    },

    async openConditionalFormatDialog(prop) {
      let columnConfig = this.config.templateJSON.columnFormatting[prop]
      this.conditionalFormatConfig.alias = prop
      this.conditionalFormatConfig.fields = await this.getConditionalFormattingFields()
      if (columnConfig?.conditionalFormat) {
        this.conditionalFormatConfig.editConfig = columnConfig.conditionalFormat
      }
      this.conditionalFormatConfig.visibility = true
    },

    async getConditionalFormattingFields() {
      let { data } = await this.$http.post('v2/report/pivotMetaFields', {
        rows: this.config.rows,
        data: this.config.data,
        formula: this.config.formula,
        columnFormatting: this.config.templateJSON.columnFormatting,
      })
      return data.meta
    },
    conditionFormatAdded({ prop, formatting }) {
      this.config.templateJSON.columnFormatting[
        prop
      ].conditionalFormat = formatting
      this.conditionalFormatConfig.editConfig = []
      this.conditionalFormatConfig.visibility = false
      this.configChanged()
    },

    columnFormatConfigAdded({ alias, config }) {
      this.config.templateJSON.columnFormatting[alias].format = config
      this.columnFormatConfig.visibility = false
      this.configChanged()
    },
    columnTextWarpEvent({ prop, value }) {
      this.config.templateJSON.columnFormatting[prop].textWarp = value
      this.configChanged()
    },
    hideShowRow(row) {
      let prop = row.alias
      let currentValue = this.config.templateJSON.columnFormatting[prop]
        .hideColumn
      this.config.templateJSON.columnFormatting[prop].hideColumn = !currentValue
      this.configChanged()
    },
    hideShowData(data) {
      let prop = data.alias
      let currentValue = this.config.templateJSON.columnFormatting[prop]
        .hideColumn
      this.config.templateJSON.columnFormatting[prop].hideColumn = !currentValue
      this.configChanged()
    },
    hideShowFormula(formula) {
      let prop = formula.alias
      let currentValue = this.config.templateJSON.columnFormatting[prop]
        .hideColumn
      this.config.templateJSON.columnFormatting[prop].hideColumn = !currentValue
      this.configChanged()
    },
    deleteRowValue(alias) {
      if (alias == this.activeColumn) {
        this.activeColumn = null
      }
      let formulasUsed = this.getFormulasUsed(alias)
      if (this.config?.templateJSON?.columnFormatting) {
        this.$delete(this.config.templateJSON.columnFormatting, alias)
      }
      if (formulasUsed.length > 0) {
        let messageString = `This column used in the the formula ${
          formulasUsed.length == 1 ? 'column' : 'columns'
        } ( ${formulasUsed.join(', ')} ), please try again after removing it.`
        let promptObj = {
          title: 'Delete Row',
          message: messageString,
          rbHide: true,
        }
        this.$dialog.confirm(promptObj)
        return
      }
      if (alias && alias.startsWith('row')) {
        this.config.rows = this.config.rows.filter(row => row.alias !== alias)
        this.configChanged()
        return
      }
      this.config.values = this.config.values.filter(row => row.alias !== alias)

      this.$delete(this.config.templateJSON.columnFormatting, alias)
      this.resetSortByColumn(alias)
      this.configChanged()
    },
    isColumnHidden(prop) {
      let style = {}
      if (this.config.templateJSON.columnFormatting[prop]?.hideColumn) {
        style['opacity'] = 0.5
      }

      return style
    },
    async loadReportFolders() {
      // no module name case is energy data reports, DON't send moduleName prop as energydata.;leave null for energreports
      let url = '/v3/report/folders?moduleName=energydata'
      let self = this
      //pivot
      url += '&isPivot=true'
      API.get(url).then(response => {
        if (!response.error) {
          self.reportFolders = response.data.reportFolders
        }
      })
    },
    getFormulasUsed(alias) {
      let result = []
      this.config.values.forEach(value => {
        if (value.alias.startsWith('data')) return

        let variablesUsedInExp = Object.keys(value.customMeasure.variableMap)
        result = variablesUsedInExp
          .filter(val => val === alias)
          .map(() => this.config.templateJSON.columnFormatting[alias].label)
      })

      return result
    },
    reportSaved(reportObj) {
      this.reportObject = reportObj
    },
    resetSortByColumn(alias) {
      //when sort by column is deleted  , set first row column as sort column
      if (this.config.sortBy.alias == alias) {
        this.config.sortBy.order = 2
        if (this.config.rows.length) {
          this.config.sortBy.alias = this.config.rows[0].alias
        } else {
          this.config.sortBy.alias = null
        }
      }
    },
    pivotDimentionUpdated({ dimentions, formats }) {
      this.config.rows = [...dimentions]
      this.config.templateJSON.columnFormatting = {
        ...this.config.templateJSON.columnFormatting,
        ...formats,
      }

      this.configChanged()
    },
    pivotMeasureUpdated({ measures, formats }) {
      this.config.values = [...measures]
      this.config.templateJSON.columnFormatting = {
        ...this.config.templateJSON.columnFormatting,
        ...formats,
      }

      this.configChanged()
    },
    getCharAlias(alias) {
      let index = Object.keys(this.charAliasMap).findIndex(
        e => this.charAliasMap[e] == alias
      )
      let keys = Object.keys(this.charAliasMap)
      if (index != -1) return keys[index]
      keys = keys.map(e => e.toLowerCase())
      let varAlias = getUniqueCharAlias(keys)
      let result = varAlias.toUpperCase()
      this.charAliasMap[result] = alias
      return result
    },
    async configChanged() {
      if (this.config.rows.length && this.config.values.length) {
        //first time rows and data added ,set first row as sort column
        if (!this.config.sortBy.alias) {
          this.config.sortBy.alias = this.config.rows[0].alias
        }

        this.tableLoading = true
        this.fetchPivotData().then(data => {
          this.pivotTable = data
          this.tableLoading = false
        })
      }
    },
    getConfig() {
      let config = { ...this.config }
      if (
        config.dateFieldId == null ||
        config.dateFieldId == -1 ||
        config.dateFieldId == -99 ||
        (this.config.startTime == -1 &&
          this.config.endTime == -1 &&
          (config.dateFieldId == null ||
            config.dateFieldId == -1 ||
            config.dateFieldId == -99))
      ) {
        delete config.startTime
        delete config.endTime
        delete config.dateValue
        delete config.dateOperator
        delete config.dateFieldId
      }
      return config
    },
    async fetchPivotData() {
      let serverConfig = this.getSerializedParams(this.getConfig())
      let { data, error } = await API.post('v2/report/fetchTabularReportData', {
        ...serverConfig,
      })

      if (error) {
        this.fetchPivotDataError = true
      } else {
        this.fetchPivotDataError = false
        this.config.data = data.data
        if (data?.showLimitBubble) {
          this.$set(this.config, 'showLimitBubble', data.showLimitBubble)
        } else {
          this.$set(this.config, 'showLimitBubble', false)
        }
        Object.freeze(data.pivotTableData)
        return data
      }
    },
    getSerializedParams(config) {
      let serializedConfig = deepCloneObject(config)
      serializedConfig.rows.forEach(row => {
        row.field = serializeProps(row.field, [
          'id',
          'name',
          'moduleId',
          'tableName',
          'displayName',
          'aggr',
        ])
      })
      serializedConfig.values.forEach(({ moduleMeasure }) => {
        if (moduleMeasure) {
          if (moduleMeasure.field) {
            moduleMeasure.field = serializeProps(moduleMeasure.field, [
              'id',
              'name',
              'moduleId',
              'tableName',
              'displayName',
              'aggr',
            ])
          } else if (moduleMeasure.readingField) {
            moduleMeasure.readingField = serializeProps(
              moduleMeasure.readingField,
              ['id', 'name', 'moduleId', 'tableName', 'aggr']
            )
          }
        }
      })

      return serializedConfig
    },
  },
}
</script>

<style lang="scss">
.el-dialog.new-pivot-builder-dialog-box {
  margin: 0px !important;

  height: 100vh;
  overflow: hidden;
  .filter-section {
    display: flex;
    justify-content: space-between;
    width: 65px;
  }
  .ham-menu-builder-active {
    background-color: #ebedf4 !important;
    border-radius: 15%;
  }
  .el-dialog__header {
    padding: 0px;
    display: none;
  }

  .el-dialog__body {
    padding: 0px;
  }

  .el-breadcrumb {
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .bread-crumb-text {
    font-size: 13px;
    font-weight: normal;
    font-stretch: normal;
    font-style: normal;
    line-height: normal;
    letter-spacing: 0.31px;
    color: #a0acc0;
    cursor: pointer;
  }
  .el-breadcrumb__inner {
    color: #a0acc0 !important;
    cursor: pointer !important;
    font-weight: 400 !important;
    &:hover {
      color: #ff3184 !important;
    }
  }
  .is-link {
    cursor: pointer;
  }
}

.save-button {
  position: absolute;
  z-index: 400;
  top: 4px;
  right: 20px;
}
.left-pane-icon-section {
  padding-top: 1px;
  width: 25px;
  position: relative;
  &:hover {
    background-color: #ebedf4 !important;
    border-radius: 15%;
  }
}
.left-pane-active {
  background-color: #ebedf4 !important;
  border-radius: 15%;
  height: 25.5px;
}
.hamburger-menu-builder {
  position: absolute;
  top: 4px;
  left: 3px;
}

.default-colors-div {
  display: flex;
  align-content: space-between;
  justify-content: space-between;
}
.default-colors {
  height: 40px;
  width: 40px;
  padding: 4px;
  border: 1px solid #e6e6e6;
  border-radius: 2px;
}

.inner-color-div {
  height: 100%;
  width: 100%;
  border-radius: 2px;
}
.selected-color {
  border: 2px solid #1f1f1f;
}

.pivot-builder-new {
  // height:calc(100vh - 50px);//the parent container of this comp has height of TOTAL-50, but setting height 100 here takes up TOTAL height,hence temp fix
  display: flex;
  flex-direction: row-reverse;

  .el-checkbox__label {
    color: #6b7e91;
  }

  .report-title-section {
    padding-left: 5px;
    align-self: flex-start;
  }

  .pivot-builder-new-body {
    display: flex;
  }

  .pivot-left-pane {
    border-left: 1px solid #dedfe0;
    border-right: 1px solid #dedfe0;
  }

  .field-row {
    padding: 10px 10px;
    border: 1px solid #ebeef5;
    box-shadow: 0px 3px 5px #ebeef5;
    display: flex;
    font-size: 13px;
    font-weight: normal;
    letter-spacing: normal;
  }

  .field-row:hover {
    background-color: #f1f8fa;
  }

  .config-section {
    display: flex;
    flex-direction: column;
    padding-right: 14px;
  }

  .pivot-header-main-content {
    display: flex;
    flex-direction: column;

    .pivot-table-section {
      padding-left: 20px;
      padding-top: 21px;
      padding-bottom: 10px;
      height: calc(100vh - 70px);
      background: #f7f9f8;
    }
  }

  .function-icon-button {
    height: inherit !important;
    padding: 3px 10px !important;
    font-size: 13px;
    font-weight: normal;
    text-transform: none;
  }

  .pivot-measures {
    padding-top: 10px;
  }

  .title-and-icon-section {
    display: flex;
    align-items: center;
    align-content: space-between;
    padding-left: 15px;
    flex-direction: column;
    height: 100%;
    margin-bottom: 10px;
    .newreport-title-input {
      margin-top: 2px;
      border: none !important;
      border-bottom: none Im !important;
    }
  }
  .icon-and-title {
    display: flex;
    align-self: flex-start;
    margin-top: 8px;
    .cls-1 {
      fill: none;
    }
    .cls-2 {
      fill: black;
    }
    &:hover {
      .edit-btn {
        display: inline;
      }
      .cls-2 {
        fill: black;
      }
    }
  }
  .show-edit-icon:hover {
    .edit-btn {
      display: inline;
    }
  }
  .edit-btn {
    border: none;
    display: none;
    &:hover {
      background-color: #f1f4f8;
    }
  }

  .newreport-title-input {
    font-size: 16px !important;
    font-weight: 500;
    font-stretch: normal;
    font-style: normal;
    line-height: normal;
    letter-spacing: normal;
    color: #324056;
    margin-left: 12px;
  }

  .pivot-tool-bar-container {
    display: flex;
    align-items: center;
  }

  .fc-report-header {
    width: 100%;
    display: flex;
    height: 64px;
    -webkit-box-sizing: border-box;
    box-sizing: border-box;
    -webkit-box-shadow: 0 3px 4px 0 rgba(218, 218, 218, 0.32);
    box-shadow: 0 3px 4px 0 rgba(218, 218, 218, 0.32);
    background-color: #ffffff;
    border-bottom: 1px solid #e6e6e6;
    position: sticky;
    z-index: 1;
    justify-content: space-between;
    align-items: center;
  }
  .fc-report-input-title .el-input__inner {
    width: 300px;
    height: 35px !important;
    line-height: 35px !important;
    font-size: 13px;
    font-weight: 500;
    letter-spacing: 0.5px;
    color: #324056;
    text-align: left;
    padding-left: 0px;
    padding-right: 0px;
  }
  .aggr-select-option {
    .is-reverse::before {
      top: -8px;
      //transform:none !important;
    }
    .is-reverse {
      left: -25px;
    }
    .el-select__caret {
      font-size: 10px !important;
    }
    .el-icon-arrow-up {
      height: 29px;
      top: -9px;
      position: absolute;
      left: -24px;
    }
    .el-icon-arrow-up::before {
      // transform:none !important;
      position: absolute;
      top: 0px;
      left: 8px;
    }
  }
}
</style>
