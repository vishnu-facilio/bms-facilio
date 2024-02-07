<template>
  <div class="pivot-builder  height-100   d-flex flex-col white-background">
    <header class="fc-report-header flex-shrink-0">
      <div class="pull-left report-title-section">
        <el-input
          :autofocus="true"
          class="fc-report-input-title newreport-title-input pL10"
          v-model="reportObject.name"
          :placeholder="$t('common.wo_report.new_report')"
          prop="reportName"
          type="text"
        ></el-input>
      </div>
      <div class="pull-right report-btn-section">
        <!-- fsave as report -->
        <f-report-options
          :isActionsDisabled="isNoConfig"
          :optionsToEnable="[5]"
          :reportObject="reportObject"
          :config="config"
          class="pL10"
          :pdf="false"
        ></f-report-options>
      </div>
    </header>
    <div
      class="d-flex flex-grow pivot-builder-body items-stretch overflow-hidden"
    >
      <transition name="fc-scale-panel">
        <div
          class="width45  overflow-x-hidden flex-shrink-0 pivot-left-pane  pL20 pR20 pT10 pB40 overflow-y-scroll"
          v-show="leftPaneVisibility"
        >
          <div v-show="leftPaneActiveTab == 'CONFIG'">
            <!-- MODULE SELECT -->
            <div class="fc-text-pink fw6 text-uppercase">
              {{ $t('pivot.build') }}
            </div>

            <div class="fc-grey7-12 f14 text-left line-height25 mT10">
              {{ $t('pivot.module') }}
            </div>

            <el-select
              v-model="config.moduleName"
              filterable
              placeholder="Select"
              class="fc-input-full-border-select2 width100 mT5"
              popper-class="fc-group-select"
              :loading="moduleLoading"
              :loading-text="$t('pivot.loading')"
              :disabled="isModuleAvailable"
            >
              <el-option-group :label="$t('pivot.systemModules')">
                <el-option
                  v-for="(module, index) in moduleOptions.systemModules"
                  :key="index"
                  :label="module.displayName"
                  :value="module.name"
                ></el-option>
              </el-option-group>
              <el-option-group
                :label="$t('pivot.customModules')"
                v-if="
                  moduleOptions.customModules &&
                    moduleOptions.customModules.length > 0
                "
              >
                <el-option
                  v-for="(module, index) in moduleOptions.customModules"
                  :key="index"
                  :label="module.displayName"
                  :value="module.name"
                ></el-option>
              </el-option-group>
            </el-select>

            <!-- ******** -->
            <!-- Pivot criteria -->
            <div
              class="pivot-pane-criteria mT20 d-flex justify-content-space align-center"
            >
              <div class="pivot-criteria-config-text">
                <div class="fc-grey7-12 f14 text-left ">
                  {{ $t('pivot.criteria') }}
                </div>
                <div
                  class="fc-grey2 f11 text-left pT5 pB5"
                  :class="[
                    { fwBold: config.criteria },
                    { pointer: config.criteria },
                  ]"
                  @click="resetCriteria"
                >
                  {{
                    config.criteria
                      ? $t('pivot.criteriaReset')
                      : $t('pivot.criteriaDefault')
                  }}
                </div>
              </div>
              <i
                @click="openAddCriteriaDialog"
                class="el-icon-edit report-edit-icon pointer"
                :class="[
                  config.moduleName ? ['pointer'] : ['cursor-not-allowed'],
                ]"
              ></i>
            </div>
            <!-- **** -->
            <!--  TIMELINE FILTER -->
            <div class="timeline-filter-section">
              <el-checkbox
                class="mT15  p5 fc-grey7-12 f14 text-left line-height25"
                style="border: none;"
                v-model="config.showTimelineFilter"
                :label="$t('pivot.timelineFilter')"
                @change="toggleTimelineFilter"
              ></el-checkbox>

              <!-- <div class="period-section" v-if="showTimelineFilter">
                <div class="fc-grey7-12 f14 text-left line-height25 mT10">
                  {{ $t('pivot.defaultPeriod') }}
                </div>

                <el-select
                  @change="configChanged(true)"
                  v-if="showTimelineFilter"
                  v-model="config.datePeriod"
                  filterable
                  placeholder="Select"
                  class="fc-input-full-border-select2 width100  mT5 date-period-select "
                >
                  <el-option
                    v-for="(datePeriod, index) in datePeriodOptions"
                    :key="'module-date-period' + index"
                    :label="datePeriod.label"
                    :value="datePeriod.dateOperator"
                  ></el-option>
                </el-select>
              </div> -->
              <div class="date-field-section" v-if="config.showTimelineFilter">
                <div class="fc-grey7-12 f14 text-left line-height25 mT10">
                  {{ $t('pivot.rowDateField') }}
                </div>

                <el-select
                  @change="configChanged"
                  v-model="config.dateFieldId"
                  filterable
                  placeholder="Select"
                  class="fc-input-full-border-select2 width100 mT5 date-field-select"
                >
                  <el-option
                    v-for="(dateField, index) in dateFieldOptions"
                    :key="'date-field-option' + index"
                    :label="dateField.displayName"
                    :value="dateField.id"
                  ></el-option>
                </el-select>
              </div>
            </div>

            <!-- **** -->
            <!-- PIVOT ROWS -->
            <div class="fc-grey7-12 f14 text-left line-height25 mT20 d-flex">
              {{ $t('pivot.rows') }}
              <div
                class="fc-dark-blue2-12  mL-auto"
                :class="[
                  config.moduleName ? ['pointer'] : ['cursor-not-allowed'],
                ]"
                @click="openAddRowDialog"
              >
                {{ $t('pivot.add') }}
              </div>
            </div>
            <draggable
              :list="config.rows"
              :options="draggableOptions"
              @change="handleReorder"
            >
              <div
                class="field-row mT10 "
                v-for="(row, index) in config.rows"
                :style="isColumnHidden(row.alias)"
                :key="index"
              >
                <div class="task-handle mR10 pointer">
                  <img src="~assets/drag-grey.svg" />
                </div>

                <div class="mT-auto mB-auto">
                  {{ config.templateJSON.columnFormatting[row.alias].label }}
                </div>

                <div class="mL-auto mT-auto mB-auto">
                  <div class="mR10 inline">
                    <i
                      class="el-icon-view pointer mR10"
                      @click="hideShowRow(row)"
                    ></i>
                    <i
                      class="el-icon-edit pointer mR10"
                      @click="editRow(row, index)"
                    ></i>
                    <i
                      class="el-icon-delete pointer trash-icon"
                      @click="removeRow(row, index)"
                    ></i>
                  </div>
                </div>
              </div>
            </draggable>
            <!-- ******** -->
            <!-- PIVOT DATA -->
            <div class="fc-grey7-12 f14 text-left line-height25 mT20 d-flex">
              {{ $t('pivot.data') }}
              <div
                class="fc-dark-blue2-12  mL-auto"
                :class="[
                  config.moduleName ? ['pointer'] : ['cursor-not-allowed'],
                ]"
                @click="openAddDataDialog"
              >
                {{ $t('pivot.add') }}
              </div>
            </div>
            <draggable
              :list="config.data"
              :options="draggableOptions"
              @change="handleReorder"
            >
              <div
                class="field-row mT10"
                v-for="(data, index) in config.data"
                :style="isColumnHidden(data.alias)"
                :key="index"
              >
                <div class="task-handle mR10 pointer">
                  <img src="~assets/drag-grey.svg" />
                </div>

                <div class="mT-auto mB-auto">
                  {{ config.templateJSON.columnFormatting[data.alias].label }}
                </div>

                <div class="mL-auto mT-auto mB-auto">
                  <div class="mR10 inline">
                    <i
                      class="el-icon-view pointer mR10"
                      @click="hideShowData(data)"
                    ></i>
                    <i
                      class="el-icon-edit pointer mR10"
                      @click="editData(data, index)"
                    ></i>
                    <i
                      class="el-icon-delete pointer trash-icon"
                      @click="removeData(data, index)"
                    ></i>
                  </div>
                </div>
              </div>
            </draggable>
            <!-- ******** -->
            <!-- PIVOT FORMULA  -->
            <div class="fc-grey7-12 f14 text-left line-height25 mT20 d-flex">
              {{ $t('pivot.formula') }}
              <div
                class="fc-dark-blue2-12  mL-auto"
                :class="[
                  config.moduleName ? ['pointer'] : ['cursor-not-allowed'],
                ]"
                @click="openAddFormulaDialog"
              >
                {{ $t('pivot.add') }}
              </div>
            </div>
            <draggable
              :list="config.formula"
              :options="draggableOptions"
              @change="handleReorder"
            >
              <div
                class="field-row mT10"
                v-for="(formula, index) in config.formula"
                :style="isColumnHidden(formula.alias)"
                :key="index"
              >
                <div class="task-handle mR10 pointer">
                  <img src="~assets/drag-grey.svg" />
                </div>

                <div class="mT-auto mB-auto">
                  {{
                    config.templateJSON.columnFormatting[formula.alias].label
                  }}
                </div>

                <div class="mL-auto mT-auto mB-auto">
                  <div class="mR10 inline">
                    <i
                      class="el-icon-view pointer mR10"
                      @click="hideShowFormula(formula)"
                    ></i>
                    <i
                      class="el-icon-edit pointer mR10"
                      @click="editFormula(formula, index)"
                    ></i>
                    <i
                      class="el-icon-delete pointer trash-icon"
                      @click="removeFormula(formula, index)"
                    ></i>
                  </div>
                </div>
              </div>
            </draggable>
            <!-- ******** -->
            <!-- PIVOT SORT  -->
            <div
              class="pivot-sort-section"
              v-if="
                config.moduleName && config.rows.length && config.data.length
              "
            >
              <div class="fc-grey7-12 f14 text-left line-height25 mT20">
                {{ $t('pivot.sortColumn') }}
              </div>

              <el-select
                @change="configChanged"
                v-model="config.sortBy.alias"
                filterable
                placeholder="Select sort column"
                class="fc-input-full-border-select2 width100 mT5"
                popper-class="fc-group-select"
              >
                <el-option-group :label="$t('pivot.rows')">
                  <el-option
                    v-for="(row, index) in config.rows"
                    :key="index"
                    :label="
                      config.templateJSON.columnFormatting[row.alias].label ||
                        row.field.displayName
                    "
                    :value="row.alias"
                  ></el-option>
                </el-option-group>
                <el-option-group :label="$t('pivot.data')">
                  <el-option
                    v-for="(data, index) in config.data"
                    :key="index"
                    :label="
                      config.templateJSON.columnFormatting[data.alias].label ||
                        (data.field || data.readingField).displayName
                    "
                    :value="data.alias"
                  ></el-option>
                </el-option-group>
              </el-select>

              <div class="fc-grey7-12 f14 text-left line-height25 mT20">
                {{ $t('pivot.sortOrder') }}
              </div>

              <el-select
                @change="configChanged"
                v-model="config.sortBy.order"
                placeholder="Ascending/Descending"
                class="fc-input-full-border-select2 width100 mT5"
              >
                <el-option :label="$t('pivot.asc')" :value="2"></el-option>
                <el-option :label="$t('pivot.desc')" :value="3"></el-option>
              </el-select>
              <!-- ****** -->
              <!-- LIMIT  -->

              <div class="fc-grey7-12 f14 text-left line-height25 mT10">
                {{ $t('pivot.limit') }}
              </div>

              <el-input
                @change="configChanged"
                controls-position="right"
                v-model.number="config.sortBy.limit"
                type="number"
                :step="20"
                :min="1"
                :max="400"
                placeholder="Ascending/Descending"
                class="fc-input-full-border2 width100 mT5"
              >
              </el-input>
              <!-- ***** -->
            </div>
            <AddRowDialog
              v-if="showAddRowDialog"
              :visibility.sync="showAddRowDialog"
              :pivotBaseModuleName="config.moduleName"
              :editConfig="editConfig"
              :formatConfig="formatConfig"
              :index="editIndex"
              @save="rowAdded"
              @update="pivotRowUpdated"
              @updateCancel="pivotUpdateReset"
            ></AddRowDialog>
            <AddCriteriaDialog
              v-if="showAddCriteriaDialog"
              :visibility.sync="showAddCriteriaDialog"
              :criteria="config.criteria"
              :moduleName="config.moduleName"
              @save="criteriaSaved"
            ></AddCriteriaDialog>
            <AddDataDialog
              v-if="showAddDataDialog"
              :visibility.sync="showAddDataDialog"
              :pivotBaseModuleName="config.moduleName"
              :editConfig="editConfig"
              :formatConfig="formatConfig"
              :index="editIndex"
              @save="pivotDataAdded"
              @update="pivotDataUpdated"
              @updateCancel="pivotUpdateReset"
            ></AddDataDialog>
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
            <add-formula-dialog
              v-if="showAddFormulaDialog"
              :visibility.sync="showAddFormulaDialog"
              :moduleName="config.moduleName"
              :formatConfig="formatConfig"
              :editConfig="editConfig"
              :pivotFieldMap="pivotFieldMap"
              :index="editIndex"
              @save="pivotFormulaAdded"
              @update="pivotFormulaUpdated"
              @updateCancel="pivotUpdateReset"
            >
            </add-formula-dialog>
          </div>
          <div v-show="leftPaneActiveTab == 'SETTINGS'">
            <div class="fc-text-pink fw6 text-uppercase">
              {{ $t('pivot.theme') }}
            </div>
            <div class="fc-grey7-12 f14 text-left line-height25 mT10">
              {{ $t('pivot.rowSpacing') }}
            </div>

            <el-select
              v-model="config.templateJSON.theme.density"
              filterable
              placeholder="Select"
              class="fc-input-full-border-select2 width100 mT5"
            >
              <el-option :label="$t('pivot.small')" value="SMALL"> </el-option>
              <el-option :label="$t('pivot.medium')" value="MEDIUM">
              </el-option>
              <el-option :label="$t('pivot.large')" value="LARGE"> </el-option>
            </el-select>
            <div class="fc-grey7-12 f14 text-left line-height25 mT10">
              {{ $t('pivot.fontSize') }}
            </div>

            <el-select
              v-model="config.templateJSON.theme.fontSize"
              filterable
              placeholder="Select"
              class="fc-input-full-border-select2 width100 mT5"
            >
              <el-option :label="$t('pivot.small')" value="small"> </el-option>
              <el-option :label="$t('pivot.medium')" value="medium">
              </el-option>
              <el-option :label="$t('pivot.large')" value="large"> </el-option>
            </el-select>
            <div class="fc-grey7-12 f14 text-left line-height25 mT10">
              {{ $t('pivot.themeColor') }}
            </div>
            <el-select
              v-model="config.templateJSON.theme.class"
              filterable
              placeholder="Select"
              class="fc-input-full-border-select2 width100 mT5"
            >
              <el-option
                v-for="theme in pivotThemeOptions"
                :key="'pivotTheme-' + theme.name"
                :label="theme.name"
                :value="theme.class"
              >
              </el-option>
            </el-select>
            <div class="fc-grey7-12 f14 text-left line-height25 mT10">
              {{ $t('pivot.grid') }}
            </div>
            <el-select
              v-model="config.templateJSON.theme.grid"
              filterable
              placeholder="Select"
              class="fc-input-full-border-select2 width100 mT5"
            >
              <el-option :label="$t('pivot.both')" value="both"> </el-option>
              <el-option :label="$t('pivot.horizontal')" value="horizontal">
              </el-option>
              <el-option :label="$t('pivot.vertical')" value="vertical">
              </el-option>
              <el-option :label="$t('pivot.none')" value="none"> </el-option>
            </el-select>
            <el-checkbox
              class="mT15"
              v-model="config.templateJSON.theme.number"
              >{{ $t('pivot.showRowNumbers') }}</el-checkbox
            >
            <el-checkbox
              class="mT15"
              v-model="config.templateJSON.theme.stripe"
              >{{ $t('pivot.stripes') }}</el-checkbox
            >
          </div>
        </div>
      </transition>

      <div class="pivot-main relative width75 flex-grow  pL75 pT25 pB25 pR25">
        <PaneSwitch
          :isPaneExpanded.sync="leftPaneVisibility"
          :activeTab.sync="leftPaneActiveTab"
        ></PaneSwitch>
        <PivotTable
          :sortBy="config.sortBy"
          ref="pivotTable"
          :showTimelineFilter="config.showTimelineFilter"
          :dateOperator="config.dateOperator"
          :dateValue="config.dateValue"
          :pivotTable="pivotTable"
          :loading.sync="tableLoading"
          :isNoConfig="isNoConfig"
          :theme="config.templateJSON.theme"
          :enableColumnFormat="true"
          :columnFormatting="config.templateJSON.columnFormatting"
          :fetchPivotDataError="fetchPivotDataError"
          @sortChanged="handleTableSortIconClick"
          @pickerChanged="handlePickerChange"
          @columnResizeEvent="columnResizeEvent"
          @openColumnFormatDialog="openColumnFormatDialog"
          @openConditionalFormatDialog="openConditionalFormatDialog"
          @openCellVisualizationDialog="openCellVisualizationDialog"
          @columnPinningEvent="columnPinningEvent"
          @columnTextWarpEvent="columnTextWarpEvent"
        >
        </PivotTable>
      </div>
    </div>
  </div>
</template>

<script>
import { API } from '@facilio/api'
import NewDateHelper from '@/mixins/NewDateHelper'
import { isDateTypeField } from '@facilio/utils/field'
import draggable from 'vuedraggable'
import AddRowDialog from './AddRowDialog'
import AddDataDialog from './AddDataDialog'
import ColumnFormatDialog from './ColumnFormatDialog'
import AddCriteriaDialog from './AddCriteriaDialog'
import { getUniqueCharAlias } from 'util/utility-methods'

import {
  defaultTheme,
  datePeriodOptions,
  pivotThemeOptions,
} from './PivotDefaults'
import './pivot.scss'
import PivotTable from './PivotTable'
import PaneSwitch from './PaneSwitch'
import { deepCloneObject } from 'util/utility-methods'
import { serializeProps } from '@facilio/utils/utility-methods'
import FReportOptions from 'pages/report/components/FReportOptions'
import AddFormulaDialog from './AddFormulaDialog.vue'
import ConditionalFormatDialog from './ConditionalFormatDialog.vue'
import CellVisualizationDialog from './CellVisualizationDialog.vue'
// import { isDecimalField } from '../../../util/field-utils'
import { isDecimalField, isNumberField } from '../../../util/field-utils'

export default {
  name: 'PivotBuilder',
  props: ['id'],
  components: {
    FReportOptions,
    draggable,
    AddRowDialog,
    AddDataDialog,
    AddCriteriaDialog,
    PivotTable,
    PaneSwitch,
    ColumnFormatDialog,
    AddFormulaDialog,
    ConditionalFormatDialog,
    CellVisualizationDialog,
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
        API.post('v2/report/executePivotReport', { reportId: reportId }).then(
          ({ data }) => {
            this.config.moduleName = data.report.module.name
            this.config.templateJSON = data.pivotTemplateJSON
            this.config.sortBy = data.sorting
            this.loadDateFields()
            this.reportObject.name = data.report.name
            this.config.rows = data.rows
            this.config.data = data.data
            this.config.formula = data.formula
            this.config.criteria = data.criteria
            this.config.startTime = data.startTime
            this.config.endTime = data.endTime
            if (data.startTime != -1 || data.endTime != -1) {
              this.config.showTimelineFilter = true
            }
            this.reportObject = data.report
            this.reportObject.reportType = 5
            if (this.config.showTimelineFilter) {
              this.config.dateOperator = data.dateOperator
              this.config.dateValue = data.dateValue
              this.config.dateFieldId = data.dateField
            }
            this.isModuleAvailable = true
            this.configChanged()
            this.pivotUpdateReset()
          }
        )
      }
    })
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
    reportId() {
      let reportId = this.id
      if (reportId && !isNaN(reportId)) {
        return parseInt(reportId)
      } else {
        return null
      }
    },
    dateFieldOptions() {
      let options = []
      options.push({ id: -99, displayName: 'NONE' })
      let dateFields = this.dateFields || []
      options.push(...dateFields)
      return options
    },
    isNoConfig() {
      return !(this.config.rows.length > 0 && this.config.data.length > 0)
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

      /** *****************/
      showAddRowDialog: false,
      showAddDataDialog: false,
      showAddFormulaDialog: false,

      fetchPivotDataError: false,

      leftPaneVisibility: true,
      leftPaneActiveTab: 'CONFIG', //CONFIG || SETTINGS
      tableLoading: false,
      reportObject: {
        name: 'New Pivot table',
        reportType: 5,
      },
      pivotTable: null,
      pivotThemeOptions,
      charAliasMap: {},
      config: {
        showTimelineFilter: false,
        templateJSON: {
          theme: defaultTheme,
          columnFormatting: {},
        },
        moduleName: null,
        criteria: null,
        rows: [],
        data: [],
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
          limit: 40,
        },
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
    handleTableSortIconClick(newSortBy) {
      this.config.sortBy = newSortBy
      this.configChanged()
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
    resetCriteria() {
      this.config.criteria = null
      this.configChanged()
    },
    criteriaSaved(criteria) {
      this.showAddCriteriaDialog = false
      this.config.criteria = criteria
      this.configChanged()
    },
    handleReorder() {
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

    openAddRowDialog() {
      if (this.config.moduleName) {
        this.showAddRowDialog = true
      }
    },
    openAddDataDialog() {
      if (this.config.moduleName) {
        this.showAddDataDialog = true
      }
    },
    openAddFormulaDialog() {
      if (this.config.moduleName) {
        this.showAddFormulaDialog = true
      }
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
        let index = this.config.data.findIndex(dt => dt.alias == alias)

        if (index != -1 && this.config.data[index].visualConfig) {
          let editConfig = { ...this.config.data[index].visualConfig }
          this.cellVisualizationConfig.editConfig = editConfig
        }
      } else {
        let index = this.config.formula.findIndex(dt => dt.alias == alias)

        if (index != -1 && this.config.formula[index].visualConfig) {
          let editConfig = { ...this.config.formula[index].visualConfig }
          this.cellVisualizationConfig.editConfig = editConfig
        }
      }

      this.cellVisualizationConfig.visibility = true
    },
    cellVisualizationConfigAdded({ alias, visualConfig }) {
      if (alias.startsWith('data')) {
        let index = this.config.data.findIndex(dt => dt.alias == alias)
        if (index != -1) this.config.data[index].visualConfig = visualConfig
      } else {
        let index = this.config.formula.findIndex(dt => dt.alias == alias)
        if (index != -1) this.config.formula[index].visualConfig = visualConfig
      }

      this.cellVisualizationConfig = {
        alias: null,
        visibility: false,
        editConfig: {},
      }
      this.configChanged()
    },
    openColumnFormatDialog(prop) {
      if (this.pivotTable && !prop?.startsWith('formula')) {
        this.columnFormatConfig.field = this.pivotTable.pivotAliasVsField[prop]
      } else {
        let field
        if (prop && prop.startsWith('data')) {
          field = this.config.data.find(dt => dt.alias == prop)
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

      if (prop.startsWith('data')) {
        this.columnFormatConfig.isDataColumn = true
      } else {
        this.columnFormatConfig.isDataColumn = false
      }

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
    rowAdded({ row, formatting }) {
      //remove preffix and send aliases of exiting rows
      row.alias =
        'row_' +
        getUniqueCharAlias(this.config.rows.map(e => e.alias.split('_')[1]))
      this.config.templateJSON.columnFormatting[row.alias] = formatting
      this.config.rows.push(row)
      this.showAddRowDialog = false
      this.configChanged()
    },
    isColumnHidden(prop) {
      let style = {}
      if (this.config.templateJSON.columnFormatting[prop].hideColumn) {
        style['opacity'] = 0.5
      }

      return style
    },
    removeRow(row, rowIndex) {
      let formulasUsed = this.getFormulasUsed(row.alias)
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
      this.config.rows.splice(rowIndex, 1)
      this.$delete(this.config.templateJSON.columnFormatting, row.alias)
      this.resetSortByColumn(row.alias)
      this.configChanged()
    },
    getFormulasUsed(alias) {
      let result = []
      this.config.formula.forEach(formula => {
        let variablesUsedInExp = Object.keys(formula.variableMap)

        variablesUsedInExp.forEach(val => {
          if (val == alias) {
            result.push(
              this.config.templateJSON.columnFormatting[formula.alias].label
            )
          }
        })
      })
      return result
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
    pivotDataAdded({ data, formatting }) {
      data.alias =
        'data_' +
        getUniqueCharAlias(this.config.data.map(e => e.alias.split('_')[1]))

      this.config.templateJSON.columnFormatting[data.alias] = formatting
      this.config.data.push(data)
      this.showAddDataDialog = false
      this.configChanged()
    },
    removeData(data, dataIndex) {
      let formulasUsed = this.getFormulasUsed(data.alias)
      if (formulasUsed.length > 0) {
        let messageString = `This column used in the the formula ${
          formulasUsed.length == 1 ? 'column' : 'columns'
        } ( ${formulasUsed.join(', ')} ), please try again after removing it.`
        let promptObj = {
          title: 'Delete Data',
          message: messageString,
          rbHide: true,
        }
        this.$dialog.confirm(promptObj)
        return
      }
      this.config.data.splice(dataIndex, 1)
      this.$delete(this.config.templateJSON.columnFormatting, data.alias)
      this.resetSortByColumn(data.alias)
      this.configChanged()
    },
    pivotFormulaAdded({ data, formatting }) {
      data.alias =
        'formula_' +
        getUniqueCharAlias(this.config.formula.map(e => e.alias.split('_')[1]))

      this.config.templateJSON.columnFormatting[data.alias] = formatting
      this.config.formula.push(data)
      this.showAddFormulaDialog = false
      this.configChanged()
    },
    pivotFormulaUpdated({ params, index }) {
      let { data, formatting } = params
      // data.alias =
      //   'formula_' +
      //   getUniqueCharAlias(this.config.formula.map(e => e.alias.split('_')[1]))

      this.config.templateJSON.columnFormatting[data.alias] = formatting
      this.config.formula[index] = data
      this.showAddFormulaDialog = false
      this.configChanged()
      this.pivotUpdateReset()
    },
    removeFormula(formula, formulaIndex) {
      this.config.formula.splice(formulaIndex, 1)
      this.$delete(this.config.templateJSON.columnFormatting, formula.alias)
      this.resetSortByColumn(formula.alias)
      this.configChanged()
    },
    editData(data, index) {
      // to remove observer
      this.editConfig = JSON.parse(JSON.stringify(data))
      this.formatConfig = this.config.templateJSON.columnFormatting[
        this.editConfig.alias
      ]
      this.editIndex = index
      this.showAddDataDialog = true
    },
    editRow(row, index) {
      this.editConfig = JSON.parse(JSON.stringify(row))
      this.formatConfig = this.config.templateJSON.columnFormatting[
        this.editConfig.alias
      ]
      this.editIndex = index
      this.showAddRowDialog = true
    },
    editFormula(formula, index) {
      this.editConfig = JSON.parse(JSON.stringify(formula))
      this.formatConfig = this.config.templateJSON.columnFormatting[
        this.editConfig.alias
      ]
      this.editIndex = index
      this.showAddFormulaDialog = true
    },
    pivotDataUpdated({ params, index }) {
      let { data, formatting } = params
      // data.alias =
      //   'data_' +
      //   getUniqueCharAlias(this.config.data.map(e => e.alias.split('_')[1]))

      this.config.templateJSON.columnFormatting[data.alias] = formatting
      this.config.data[index] = data
      this.showAddDataDialog = false
      this.configChanged()
      this.pivotUpdateReset()
    },
    pivotUpdateReset() {
      this.editConfig = null
      this.formatConfig = null
      this.editIndex = -1
    },
    pivotRowUpdated({ params, index }) {
      // let { row, index } = params
      let { row, formatting } = params
      // row.alias =
      //   'row_' +
      //   getUniqueCharAlias(this.config.rows.map(e => e.alias.split('_')[1]))
      this.config.templateJSON.columnFormatting[row.alias] = formatting
      this.config.rows[index] = row
      this.showAddRowDialog = false
      this.configChanged()
      this.pivotUpdateReset()
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
      if (this.config.rows.length && this.config.data.length) {
        //first time rows and data added ,set first row as sort column
        if (!this.config.sortBy.alias) {
          this.config.sortBy.alias = this.config.rows[0].alias
        }

        this.tableLoading = true
        this.fetchPivotData().then(data => {
          this.pivotTable = data
          this.tableLoading = false
        })
        // .catch(error => {
        //   console.error(
        //     'error fetching pivot data ' + error + 'for config' + this.config
        //   )
        // })
      }
    },
    getConfig() {
      let config = { ...this.config }
      if (this.config.startTime == -1 && this.config.endTime == -1) {
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
          'aggr',
        ])
      })

      serializedConfig.data.forEach(data => {
        if (data.field) {
          data.field = serializeProps(data.field, [
            'id',
            'name',
            'moduleId',
            'tableName',
            'aggr',
          ])
        } else if (data.readingField) {
          data.readingField = serializeProps(data.readingField, [
            'id',
            'name',
            'moduleId',
            'tableName',
            'aggr',
          ])
        }
      })

      return serializedConfig
    },
  },
}
</script>

<style lang="scss" scoped>
.save-button {
  position: absolute;
  z-index: 400;
  top: 4px;
  right: 20px;
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

.pivot-builder {
  // height:calc(100vh - 50px);//the parent container of this comp has height of TOTAL-50, but setting height 100 here takes up TOTAL height,hence temp fix
  .el-checkbox__label {
    color: #6b7e91;
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
  }

  .field-row:hover {
    background-color: #f1f8fa;
  }
}
</style>
