<template>
  <div
    class="pivot-table-comp-root height100 width100"
    :class="isPivot ? 'pR25' : ''"
  >
    <div
      class="date-picker-boxed-style"
      v-if="showTimelineFilter && datePickerObj && !isBuilder"
      :style="
        showTimelineFilter && datePickerObj && !isBuilder && !isPivot
          ? 'display:none !important;'
          : ''
      "
    >
      <portal
        :to="'widget-datepicker' + config.widget.id"
        slim
        class="tabulardata-datepicker"
        v-if="config"
      >
        <new-date-picker
          :key="pickerCompKey"
          ref="datePickerRef"
          :zone="$timezone"
          class="filter-field date-filter-comp-new-report inline"
          :class="{ 'move-left': isInfoIconVisible }"
          style="text-align:right"
          :dateObj="datePickerObj"
          @date="dateChanged"
        ></new-date-picker>
      </portal>
      <new-date-picker
        v-else
        class="mB10"
        :key="pickerCompKey"
        ref="datePickerRef"
        :zone="$timezone"
        :dateObj="datePickerObj"
        style="text-align:left"
        @date="dateChanged"
      ></new-date-picker>
    </div>
    <div class="pivot-tool-bar pivot-table-builder-root" v-if="isBuilder">
      <slot name="tools-section"></slot>
      <div class="date-picker-slot" v-if="showTimelineFilter && datePickerObj">
        <new-date-picker
          :key="pickerCompKey"
          ref="datePickerRef"
          :zone="$timezone"
          :dateObj="datePickerObj"
          style="text-align:left"
          @date="dateChanged"
        ></new-date-picker>
      </div>
    </div>
    <div
      ref="pivotTableContainer"
      v-if="showTable"
      v-click-outside="outsideClick"
      class="calculate-table-height h100 w100"
    >
      <div v-if="isBuilderLoaded">
        <skeletonLoader
          v-if="loading"
          :show="loading"
          preset="Table"
          :columns="4"
          :rows="4"
        ></skeletonLoader>
      </div>
      <div
        class="no-config-pivot width100 flex-middle flex-direction-column justify-content-center"
        v-else-if="isNoConfig || fetchPivotDataError"
        :class="[`pivot-theme-${pivotThemeName}`]"
      >
        <div v-if="fetchPivotDataError" class="pivot-error-container">
          <inline-svg
            src="svgs/emptystate/reportlist"
            iconClass="icon text-center icon-100"
          ></inline-svg>
          <!--centered but lift up by 150px -->
          <div class="nowo-label mB150">
            {{ $t('pivot.fetchError') }}
          </div>
        </div>
        <div
          v-if="isNoConfig && spinnerStat.measures && spinnerStat.dimensions"
          style="width:500px;"
        >
          <img
            src="~statics/pivot/loading.gif"
            style="margin-top: -480px;
              transform: scale(0.3);
              margin-left: -700px;"
          />
          <!--centered but lift up by 150px -->
          <!-- <div class="nowo-label mB150">
            {{ noDataState ? $t('pivot.noData') : $t('pivot.noConfig') }}
          </div> -->
        </div>
        <el-table
          :data="[]"
          border
          :max-height="500"
          width="100"
          :cell-style="cellStyle"
          :header-cell-style="headerStyle"
          row-class-name="pivot-table-row"
          :span-method="pivotSpanMethod"
          @header-dragend="columnResizeEvent"
          v-else-if="!fetchPivotDataError && !isDimensionsAndMeasuresLoaded"
        >
          <!-- <template slot="empty">
          Abey kuch nhi hai
        </template> -->
          <el-table-column
            resizable
            v-for="(prop, index) in tableProps"
            :fixed="isColumnFixed(index, prop)"
            :min-width="getColumnWidth(prop)"
            :key="index"
            :prop="prop"
            :label="tableHeaders[index]"
          >
            <template slot="header" slot-scope="scope">
              <div
                :class="
                  prop == activeColumn
                    ? 'selected-row pivot-table-header'
                    : 'pivot-table-header'
                "
              >
                <span
                  :style="prop == activeColumn ? 'color:white !important' : ''"
                >
                  {{ scope.column.label }}
                </span>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- pivot table content  -->
      <div v-else :class="{ 'pivot-el-table-section': enableColumnFormat }">
        <div
          class="height100 width100"
          :class="[`pivot-theme-${pivotThemeName}`]"
        >
          <div
            class="height100 width100"
            :class="[
              `pivot-grid-line-${pivotTableGrid}`,
              `pivot-stripe-${pivotTableStripe}`,
              `pivot-font-${pivotFontSize}`,
            ]"
          >
            <el-table
              :data="orderedPivotRecords"
              border
              :max-height="pivotTableHeight"
              max-width="100"
              :cell-style="cellStyle"
              :header-cell-style="headerStyle"
              @header-dragend="columnResizeEvent"
              @header-click="enableTitleEdit"
              @cell-click="handleDrillDown"
              row-class-name="pivot-table-row"
              ref="pivotTable"
              v-if="!isDimensionsAndMeasuresLoaded"
            >
              <el-table-column
                resizable
                v-for="(prop, index) in pivotFilteredColumnProps"
                :fixed="isColumnFixed(index, prop)"
                :key="index"
                :prop="prop"
                :label="pivotTable.pivotReconstructedData.headers[prop]"
                :min-width="getColumnWidth(prop)"
                :sort-method="(a, b) => sortMethod(a, b, prop)"
              >
                <template slot="header" slot-scope="scope">
                  <div v-if="prop != 'number'">
                    <div
                      :class="
                        prop == activeColumn
                          ? 'selected-row pivot-table-header'
                          : 'pivot-table-header'
                      "
                    >
                      <div
                        class="header-dropdown-container pointer"
                        style="width:100%"
                      >
                        <i
                          v-if="isColumnFixed(index, prop)"
                          class="fa fa-thumb-tack mL5"
                          :id="`pin-${prop}`"
                          style="opacity: 0.5; font-size: 10px;padding-right:5px;"
                        ></i>
                        <div v-if="showTitleEditBox == prop">
                          <input
                            class="el-input fc-input-full-border2"
                            @keydown.enter="headerLabelChanged"
                            v-model="titleEditModel"
                            style="border:none;"
                            v-click-outside="headerLabelChanged"
                          />
                        </div>

                        <span
                          class="el-dropdown-tooltip"
                          :style="
                            prop == activeColumn ? 'color:white !important' : ''
                          "
                          v-else
                        >
                          <el-tooltip placement="top">
                            <div slot="content">
                              <span>{{ scope.column.label }}</span>
                            </div>
                            <!-- <div class="header-label" :ref="`column${prop}`" > -->
                            <div class="header-label" :id="`column-${prop}`">
                              <span> {{ scope.column.label }} </span>
                            </div>
                          </el-tooltip>
                          <el-tooltip placement="top">
                            <div slot="content">
                              <span>{{
                                tableSort.order == 3
                                  ? 'Ascending'
                                  : 'Descending'
                              }}</span>
                            </div>
                            <i
                              class="header-icon"
                              :id="`sort-${prop}`"
                              :class="[...getSortIconClass(prop)]"
                              @click="toggleSortColumn(prop)"
                            ></i>
                          </el-tooltip>
                        </span>
                      </div>
                      <div class="header-icons">
                        <header-dropdown
                          class="header-dropdown-icon"
                          v-if="enableColumnFormat"
                          :contextmenu="getContextMenu(prop)"
                          :prop="prop"
                          @contextMenuSelectEvent="contextMenuSelectEvent"
                        ></header-dropdown>
                      </div>
                    </div>
                  </div>
                  <div v-else>
                    <span>
                      {{ scope.column.label }}
                    </span>
                  </div>
                </template>
                <template slot-scope="scope">
                  <table-cell-wrapper
                    :textBackgroundStyle="
                      getTextBackground(scope.row[prop].style, prop)
                    "
                    :prop="prop"
                    :pivotTable="pivotTable"
                    :visualConfig="getVisualConfig(prop)"
                    :referenceValue="getReferenceValue(prop, scope.row)"
                    :alignment="getTextAlignment(scope.row[prop])"
                    :value="scope.row[prop].formattedValue"
                  ></table-cell-wrapper>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
        <!-- </div> -->
      </div>
      <!-- *** -->
    </div>
  </div>
</template>

<script>
import NewDateHelper from '@/mixins/NewDateHelper'
import NewDatePicker from '@/NewDatePicker'
import { isEmpty } from '@facilio/utils/validation'
import { defaultTheme, timeAggregation } from './PivotDefaults'
import {
  isNumberField,
  isDecimalField,
  isIdField,
  isDateTimeField,
  isDateField,
  isLookupField,
} from '@facilio/utils/field'
import HeaderDropdown from './HeaderDropdown'
import TableCellWrapper from './TableCellWrapper.vue'
import JumpToHelper from '@/mixins/JumpToHelper.js'
import { pageTypes, isWebTabsEnabled, findRouteForTab } from '@facilio/router'
import skeletonLoader from './Components/skeletonLoader.vue'

export default {
  components: {
    NewDatePicker,
    HeaderDropdown,
    TableCellWrapper,
    skeletonLoader,
  },
  watch: {
    showTimelineFilter(newVal) {
      //if picker enabled from edit pane
      if (newVal && this.dateOperator && this.dateValue) {
        this.setDatePicker()
      }
    },
    dateValue(val) {
      if (val) {
        this.setDatePicker()
      }
    },
    recordsCount(newCount) {
      this.$emit('recordCountUpdated', newCount)
    },
    isNoConfig: {
      handler(newState) {
        if (newState == true) {
          this.tableProps = null
        }
      },
    },
    pivotTable() {
      this.constructTableData(this.pivotTable)
      this.setDatePicker()
      this.$nextTick().then(() => {
        this.$emit('tableReady', false)
        this.pivotTableHeight = this.calculateTableHeight()
        this.rerenderTable()
        if (this.$refs.pivotTable) {
          this.$refs?.pivotTable?.doLayout()
        }
        this.tableSort = JSON.parse(JSON.stringify(this.sortBy))
        this.currentSortAlias = this.tableSort.alias
      })
    },
  },
  mixins: [JumpToHelper],
  props: {
    isInfoIconVisible: {
      type: Boolean,
      required: false,
      default: false,
    },
    sortBy: {
      type: Object,
      default: null,
    },
    showTimelineFilter: {
      type: Boolean,
      default: false,
    },
    activeColumn: {
      type: String,
      default: null,
    },
    dateOperator: {
      type: Number,
      default: 28,
    },
    dateValue: {
      type: String,
      default: null,
    },
    pivotTable: {
      type: Object,
      default() {
        return null
      },
    },
    theme: {
      type: Object,
      default() {
        return defaultTheme
      },
    },
    columnFormatting: {
      type: Object,
      default() {
        return {}
      },
    },
    isNoConfig: {
      default: false,
    },
    spinnerStat: {
      type: Object,
    },
    tableFormatting: {
      type: Object,
      default() {
        return { isStickyColumns: false, noWrap: false }
        //setting no wrap property is like auto width for ALL columns ,styles are set to whitespace no wrap and no width property is set
      },
    },

    loading: {
      type: Boolean,
      default: true,
    },
    dashbord: { type: Boolean },
    config: { type: Object },
    widgetDatePicker: { type: Object },
    enableColumnFormat: { type: Boolean },
    isMobileDashboard: { type: Boolean },
    fetchPivotDataError: { type: Boolean },
    diable_userfilter_drillown: { type: Boolean },
  },
  mounted() {
    this.$nextTick().then(() => {
      this.pivotTableHeight = this.calculateTableHeight()
      this.setDatePicker()
      this.$refs?.pivotTable?.doLayout()
    })
    // window.addEventListener('click',(e)=>{
    //   if(!(e.target == document.querySelector('.calculate-table-height')) ){
    //     this.$emit('activeColumnChanged',null)
    //   }
    // })
  },
  data() {
    return {
      stickyColumnWidths: [], //rendered row widths in pixel
      dragging: false,
      renameModel: null,
      datePickerObj: null,
      pickerCompKey: 0,
      tableData: [],
      tableHeaders: [],
      tableProps: [],
      showSortIcon: '',
      borderColor: '',
      hoverColor: '',
      showTable: true,
      tableSort: {},
      editTitleProp: null,
      currentSortAlias: null,
      pivotTableHeight: 700,
      showTitleEditBox: null,
      headerDBClick: 0,
      dBClickCounter: null,
      dBTimerObj: null,
      titleEditModel: '',
      contextMenu: {
        menu: [
          {
            label: 'Pin',
            activeLabel: 'Unpin',
            isActive: false,
          },
          { label: 'Sort' },
          { label: 'Auto size' },
          {
            label: 'Text wrap',
            activeLabel: 'Clip text',
            isActive: true,
          },
          {
            label: 'Cell visualization',
          },
          { label: 'Column formatting' },
          {
            label: 'Conditional formatting',
          },
          { label: 'Rename' },
        ],
      },
      timeAggregation,
    }
  },
  computed: {
    noDataState() {
      return !(this.pivotTable?.pivotReconstructedData?.records.length != 0)
    },
    isBuilderLoaded() {
      if (this.$route.path.includes('pivotbuilder')) {
        return (
          this.loading &&
          this.spinnerStat.dimensions &&
          this.spinnerStat.measures
        )
      } else {
        return this.loading
      }
    },
    isDimensionsAndMeasuresLoaded() {
      if (this.$route.path.includes('pivotbuilder')) {
        return !(this.spinnerStat.dimensions && this.spinnerStat.measures)
      } else {
        return this.loading
      }
    },
    recordsCount() {
      return this.pivotTable?.pivotReconstructedData?.records.length
    },
    pivotThemeName() {
      return this.theme?.class ? this.theme?.class : 'default'
    },
    isBuilder() {
      return this.enableColumnFormat
    },
    isPivot() {
      if (isWebTabsEnabled()) {
        let { path } = findRouteForTab(pageTypes.PIVOT_VIEW) ?? {}
        if (this.$route.path.includes(path)) {
          return true
        }
        let route_obj = findRouteForTab(pageTypes.PIVOT_FORM) ?? {}
        if (this.$route.path.includes(route_obj.path)) {
          return true
        }
        route_obj = findRouteForTab(pageTypes.PIVOT_VIEW_MANAGER) ?? {}
        if (this.$route.path.includes(route_obj.path)) {
          return true
        }
        return false
      }
      return this.$route.path.includes('em/pivot')
    },
    pivotTableGrid() {
      return this.theme?.grid ? this.theme?.grid : 'both'
    },
    pivotTableStripe() {
      return this.theme?.stripe ? this.theme?.stripe : false
    },
    pivotFontSize() {
      return this.theme?.fontSize ? this.theme?.fontSize : 'medium'
    },
    pivotShowRowNumber() {
      return this.theme?.number ? this.theme.number : false
    },
    pivotFilteredColumnProps() {
      if (!this.columnFormatting || !this.tableProps) return this.tableProps
      let props = this.tableProps.filter(
        prop => !this.columnFormatting[prop]?.hideColumn
      )
      return props
    },
    orderedPivotRecords() {
      let sort_alias = this.sortBy?.alias
      let sort_order = this.sortBy.order
      if (
        !isEmpty(sort_alias) &&
        !isEmpty(this.pivotTable.pivotReconstructedData.records) &&
        this.pivotTable.pivotReconstructedData.records.length > 1
      ) {
        let self = this
        let field = this.pivotTable.pivotAliasVsField[sort_alias]
        if (!isEmpty(field) && isLookupField(field)) {
          this.pivotTable.pivotReconstructedData.records.sort(function(
            objA,
            objB
          ) {
            return sort_order == 3
              ? self.sortMethod(objA, objB, sort_alias)
              : self.sortMethod(objB, objA, sort_alias)
          })
        }
      }
      return this.pivotTable.pivotReconstructedData.records
    },
  },

  methods: {
    isEmpty,
    outsideClick() {
      this.$emit('activeColumnChanged', null)
    },

    enableTitleEdit(column) {
      // this.showTitleEditBox = column.property
      // if (this.showTitleEditBox === column.property) return

      // if (this.dBTimerObj) {
      //   clearTimeout(this.dBTimerObj)
      //   this.dBClickCounter = 2
      //   this.dBTimerObj = null
      // } else {
      //   this.dBClickCounter = 1
      //   this.dBTimerObj = setTimeout(() => {
      //     this.showTitleEditBox = null
      //     this.dBClickCounter = 0
      //     this.dBTimerObj = null
      //   }, 200)
      // }

      // if (this.dBClickCounter == 2) {
      //   this.dBClickCounter = 0
      //   this.showTitleEditBox = column.property
      //   this.titleEditModel = column.label
      this.$emit('activeColumnChanged', column.property)
      // }
    },
    headerLabelChanged() {
      this.$emit('headerLabelChanged', {
        label: this.titleEditModel,
        prop: this.showTitleEditBox,
      })
      this.$emit('activeColumnChanged', null)
      this.showTitleEditBox = null
      this.titleEditModel = ''
    },
    getFormattedCellValue(value, prop) {
      if (prop.startsWith('number')) return value
      if (prop.startsWith('formula') || isEmpty(field)) {
        return value ? value : '--'
      }
      let { dataAlias, rowAlias } = this.pivotTable || {}
      let field = dataAlias[prop] ? dataAlias[prop].field : rowAlias[prop].field
      if (isEmpty(value)) {
        return '0'
      } else if (isIdField(field) && prop.startsWith('row')) {
        return '#' + value
      } else if (isDateTimeField(field) || isDateField(field)) {
        let aggr = 0
        this.pivotTable?.rows?.forEach(e => {
          if (e.alias === prop) {
            aggr = e.field.aggr > 0 ? e.field.aggr : 0
          }
        })
        let timeAggr
        this.timeAggregation.forEach(obj => {
          if (obj.value === aggr) {
            timeAggr = obj.enumValue
          }
        })
        return this.formatDate(value, timeAggr)
      } else {
        return value
      }
    },
    formatDate(date, period) {
      let dateFormat = this.getDateFormat(period)
      return this.$options.filters.toDateFormat(
        new Date(date),
        dateFormat.tooltip
      )
    },
    checkNumberField(prop) {
      let { dataAlias, rowAlias } = this.pivotTable || {}
      let field = dataAlias[prop] ? dataAlias[prop].field : rowAlias[prop].field

      return !isEmpty(field)
        ? isNumberField(field) || isDecimalField(field) || isIdField(field)
        : false
    },
    sortMethod(obj1, obj2, column) {
      let at = obj1[column].value
      let bt = obj2[column].value
      let field = this.pivotTable.pivotAliasVsField[column]
      if (
        !isEmpty(field) &&
        isLookupField(field) &&
        typeof obj1[column].value == 'number' &&
        typeof obj1[column].formattedValue == 'string'
      ) {
        at = obj1[column].formattedValue
        bt = obj2[column].formattedValue
      }
      if (typeof at == 'string') return at.localeCompare(bt)

      if (at == null) {
        return 1
      } else if (bt == null) {
        return -1
      } else if (at > bt) {
        return -1
      } else {
        return 1
      }
    },
    handleDrillDown(rows, column) {
      let filters = {}
      let pattern = ''

      let count = 0

      // global criteria search query
      let global_pattern = ''

      let glocalCriteria = this.pivotTable.criteria
      if (this.pivotTable && this.pivotTable.criteria) {
        global_pattern = glocalCriteria.pattern
        let result = this.criteriaToSearchQuery(glocalCriteria, count, filters)

        let globalCriteriaQuery = result[0]
        global_pattern = result[1]

        if (globalCriteriaQuery) {
          count = Object.keys(globalCriteriaQuery).length
        }
        if (globalCriteriaQuery) filters = { ...globalCriteriaQuery }
      }

      // data criteria search query
      let data_pattern = ''

      if (column.property.startsWith('data')) {
        let dataCriteria = this.getDataCriteria(column.property)
        if (dataCriteria) {
          data_pattern = dataCriteria.pattern
          let result = this.criteriaToSearchQuery(dataCriteria, count, filters)

          let dataCriteriaQuery = result[0]
          data_pattern = result[1]

          filters = { ...filters, ...dataCriteriaQuery }
          count = Object.keys(filters).length
        }
      }

      // timefilter search query
      let time_filter_pattern = ''

      if (
        !this.diable_userfilter_drillown &&
        this.pivotTable.pivotDrillDownFields['timeFilter']
      ) {
        let timefilterQuery = {}
        let timefilterField = this.pivotTable.pivotDrillDownFields['timeFilter']
        let fieldName = this.generateUniqueFieldName(
          filters,
          timefilterField.name
        )
        timefilterQuery[fieldName] = {
          operatorId: this.pivotTable.pivotDrillDownOperators['timeFilter'],
          value: [
            `${this.datePickerObj.value[0]}`,
            `${this.datePickerObj.value[1]}`,
          ],
          id: ++count,
        }

        time_filter_pattern = `(${count})`
        filters = { ...filters, ...timefilterQuery }

        count = Object.keys(filters).length
      }
      let rowVsRowAggr = null
      try {
        rowVsRowAggr = this?.pivotTable?.rows?.reduce(function(map, obj) {
          map[obj.alias] = obj.selectedTimeAggr
          return map
        }, {})
      } catch (e) {
        console.log('error while constructing rowVsRowAggr map')
      }
      // row search query
      let row_pattern = ''
      let rowCriteriaQuery = {}
      let rowAliases = Object.keys(rows)
      if (rowAliases?.length > 0) {
        row_pattern = '('
        let filtersClone = { ...filters }
        rowAliases = rowAliases.filter(row => row.startsWith('row'))
        rowAliases.forEach(row => {
          let facilioField = this.pivotTable.pivotAliasVsField[row]
          if (
            !isEmpty(facilioField) &&
            facilioField.columnName == 'SITE_ID' &&
            facilioField.tableName == 'BaseSpace'
          ) {
            let queryObj = {
              operatorId: 36,
              value: this.getValueArray(rows[row].value),
              id: ++count,
            }
            rowCriteriaQuery['siteId'] = queryObj
            filtersClone['siteId'] = queryObj
            if (row_pattern == '(') row_pattern = `${row_pattern} ${count}`
            else row_pattern = `${row_pattern}  and ${count}`
          } else if (this.isDrillDownFieldValid(row) && rows[row].value) {
            const row_field = this.pivotTable.pivotDrillDownFields[row]
            let fieldName = this.generateUniqueFieldName(
              filtersClone,
              row_field.name
            )
            let rowValue = this.getRowValue(row, rows[row].value)
            let dateObj = {}
            if (
              (this.pivotTable.pivotDrillDownFields[row].dataTypeEnum ===
                'DATE_TIME' ||
                this.pivotTable.pivotDrillDownFields[row].dataTypeEnum ===
                  'DATE') &&
              !isEmpty(rowVsRowAggr[row]) &&
              rowVsRowAggr[row] > 0
            ) {
              dateObj = NewDateHelper.getDatePickerObjectFromDateAggregation(
                rowVsRowAggr[row],
                [rowValue]
              )
              if (!isEmpty(dateObj)) {
                let queryObj = {
                  operatorId: this.pivotTable.pivotDrillDownOperators[row],
                  value: this.getValueArray(dateObj.value),
                  id: ++count,
                }
                rowCriteriaQuery[fieldName] = queryObj
                filtersClone[fieldName] = queryObj
              }
            } else {
              let queryObj = {
                operatorId:
                  this.pivotTable.pivotDrillDownFields[row].dataTypeEnum ===
                    'DATE_TIME' ||
                  this.pivotTable.pivotDrillDownFields[row].dataTypeEnum ===
                    'DATE'
                    ? 16
                    : this.pivotTable.pivotDrillDownOperators[row],
                value:
                  row_field.dataTypeEnum === 'STRING'
                    ? [rowValue]
                    : this.getValueArray(rowValue),
                id: ++count,
              }

              rowCriteriaQuery[fieldName] = queryObj
              filtersClone[fieldName] = queryObj
            }
            if (row_pattern == '(') row_pattern = `${row_pattern} ${count}`
            else row_pattern = `${row_pattern}  and ${count}`
          }
        })
        row_pattern += ')'

        filters = { ...filters, ...rowCriteriaQuery }
      }
      pattern = '('
      if (global_pattern !== '') {
        pattern = `${pattern} ${global_pattern} and`
      }
      if (data_pattern !== '') {
        pattern = `${pattern} ${data_pattern} and`
      }
      if (time_filter_pattern !== '') {
        pattern = `${pattern} ${time_filter_pattern} and`
      }
      if (row_pattern !== '') {
        pattern = `${pattern} ${row_pattern})`
      }

      filters['drillDownPattern'] = pattern

      if (this?.$mobile) {
        if (this.pivotTable?.moduleName) {
          let drillDowndata = {
            moduleName: this.pivotTable.moduleName,
            viewName: 'all',
            viewDisplayName: 'All',
            type: 'view',
            query: {
              filter: this.$helpers.formatFilter(filters),
            },
          }
          this.$helpers.sendToMobile(drillDowndata)
        }
      } else {
        this.jumpToViewList(filters, this.pivotTable.moduleName, true)
      }
    },
    isDrillDownFieldValid(alias) {
      let field = this.pivotTable.pivotDrillDownFields[alias]
      let facilioField = this.pivotTable.pivotAliasVsField[alias]
      let pivotExtendedModuleIds = this.pivotTable.pivotExtendedModuleIds

      if (
        facilioField &&
        pivotExtendedModuleIds.includes(facilioField.moduleId)
      ) {
        return true
      }

      if (field && isLookupField(field) && facilioField.mainField) {
        return true
      }

      return false
    },
    getValueArray(value) {
      if (!value) return null
      if (typeof value != 'object') return `${value}`.split(',')
      return value.map(val => `${val}`)
    },
    getRowValue(alias, value) {
      let facilioField = this.pivotTable.pivotAliasVsField[alias]
      let lookupField = this.pivotTable.pivotDrillDownFields[alias]
      if (isLookupField(lookupField) && !isLookupField(facilioField)) {
        return this.pivotTable.pivotLookupMap[alias][value]
      }
      return value
    },
    criteriaToSearchQuery(criteria, count, filters) {
      let searchQuery = {}
      let pattern
      let filtersClone = { ...filters }
      if (criteria?.conditions) {
        let conditionKeys = Object.keys(criteria.conditions)
        pattern = criteria.pattern

        conditionKeys.forEach((key, index) => {
          let condition = criteria.conditions[key]
          let newFieldName = this.getQueryFieldName(condition.fieldName)
          let fieldName = this.generateUniqueFieldName(
            filtersClone,
            newFieldName
          )
          let queryObj = {
            operatorId: condition.operatorId,
            value: this.getValueArray(condition.value),
            id: ++count,
          }
          searchQuery[fieldName] = queryObj
          filtersClone[fieldName] = queryObj
          pattern = pattern.replace(index + 1, count)
        })
      }
      return [searchQuery, pattern]
    },
    getQueryFieldName(fieldName) {
      let fieldNameArray = fieldName.split('.')
      if (fieldNameArray.length > 1)
        return fieldNameArray[fieldNameArray.length - 1]
      return fieldName
    },
    generateUniqueFieldName(filters, fieldName) {
      if (filters && !filters[fieldName]) return fieldName
      let count = 1
      while (filters[fieldName]) {
        fieldName = `${fieldName}----${count}`
      }
      return fieldName
    },
    getDataCriteria(alias) {
      let criteria
      if (!this.pivotTable?.data) return
      this.pivotTable.data.forEach(data => {
        if (data.alias == alias) criteria = data.criteria
      })
      return criteria
    },
    toggleSortColumn(alias) {
      //if clicked col already sort col , flip asc/desc 3/2 only

      this.tableSort = {
        limit: this.tableSort.limit,
        order: this.tableSort.order == 3 ? 2 : 3,
        alias,
      }

      if (!this.isBuilder) {
        let sortOrder = this.tableSort.order == 3 ? 'ascending' : 'descending'
        this.currentSortAlias = alias
        this.$refs.pivotTable.sort(alias, sortOrder)
        return
      }

      this.$emit('sortChanged', this.tableSort)
    },
    constructTableData(data) {
      this.tableData = []
      this.tableProps = []
      this.tableHeaders = []

      data = JSON.parse(JSON.stringify(data))
      this.tableData = data.pivotReconstructedData

      if (this.pivotShowRowNumber) {
        this.tableProps.push('number')
      }

      this.tableProps.push(...data.rowHeaders)
      let valueProps = data.values.map(v => v.alias)

      this.tableProps.push(...valueProps)
      this.tableHeaders = this.tableProps.map(
        prop => data.pivotReconstructedData.headers[prop]
      )
    },
    getTextAlignment(scope) {
      if (scope?.style?.textAlign) {
        return scope.style.textAlign
      }
      return 'left'
    },
    getVisualConfig(prop) {
      if (prop.startsWith('data')) {
        let data = this.pivotTable?.values?.find(data => data.alias == prop)
        if (data?.moduleMeasure.visualConfig) {
          return data.moduleMeasure.visualConfig
        }
      } else {
        let formula = this.pivotTable?.values?.find(
          formula => formula.alias == prop
        )
        if (formula?.customMeasure.visualConfig) {
          return formula.customMeasure.visualConfig
        }
      }
      return {}
    },
    getReferenceValue(prop, row) {
      let visualConfig = this.getVisualConfig(prop)
      let referenceAlias = visualConfig.referenceAlias
      return row[referenceAlias] ? row[referenceAlias].formattedValue : null
    },
    getSortIconClass(alias) {
      let elIconclass =
        this.tableSort?.order === 2 ? ['el-icon-bottom'] : ['el-icon-top']
      if (this.tableSort?.alias === alias) {
        elIconclass =
          this.tableSort?.order === 3 ? ['el-icon-bottom'] : ['el-icon-top']
      }

      elIconclass.push('sort-column')

      if (this.currentSortAlias == alias) {
        elIconclass.push('current-sort-column')
      }
      return elIconclass
    },
    getColumnWidth(prop) {
      if (prop.startsWith('number')) return 50
      else if (this.columnFormatting[prop]?.autoWidth)
        return (
          this.getTextWidth(
            this.pivotTable.pivotReconstructedData.headers[prop]
          ) + 25
        )
      else return this.columnFormatting[prop].width
    },

    dateChanged(dateObj) {
      this.datePickerObj = dateObj
      this.$emit('pickerChanged', dateObj)
    },
    setDatePicker() {
      if (this.showTimelineFilter) {
        this.datePickerObj = NewDateHelper.getDatePickerObject(
          this.dateOperator,
          this.dateValue
        )
        this.pickerCompKey++
      }
    },
    contextMenuSelectEvent({ menu, prop }) {
      this.contextMenu.showContextMenu = false
      if (menu.label == 'Pin') {
        this.$emit('columnPinningEvent', { prop: prop, value: true })
      } else if (menu.label == 'Unpin') {
        this.$emit('columnPinningEvent', { prop: prop, value: false })
      } else if (menu.label == 'Sort') {
        this.toggleSortColumn(prop)
      } else if (menu.label == 'Rename') {
        this.showTitleEditBox = prop
        this.titleEditModel = this.pivotTable.pivotReconstructedData.headers[
          prop
        ]
      } else if (menu.label == 'Auto size') {
        this.textAutoSize(prop)
      } else if (menu.label == 'Text wrap' && menu.isActive) {
        this.$emit('columnTextWarpEvent', { prop: prop, value: true })
      } else if (menu.label == 'Text wrap' && !menu.isActive) {
        this.$emit('columnTextWarpEvent', { prop: prop, value: false })
      } else if (menu.label == 'Column formatting') {
        this.$emit('openColumnFormatDialog', prop)
      } else if (menu.label == 'Conditional formatting') {
        this.$emit('openConditionalFormatDialog', prop)
      } else if (menu.label == 'Cell visualization') {
        this.$emit('openCellVisualizationDialog', prop)
      }
    },
    isColumnFixed(index, prop) {
      // if (index == 0 && prop) return true
      // else if (this.pivotShowRowNumber && index == 1) return true
      if (this.columnFormatting[prop]?.isFixed && !this.isMobileDashboard) {
        return true
      }
      return false
    },
    getContextMenu(prop) {
      let contextMenu = JSON.parse(JSON.stringify(this.contextMenu))
      let columnFormat = JSON.parse(JSON.stringify(this.columnFormatting[prop]))
      if (columnFormat.isFixed) {
        contextMenu.menu[0].isActive = true
        contextMenu.menu[0].label = 'Unpin'
      } else {
        contextMenu.menu[0].isActive = false
        contextMenu.menu[0].label = 'Pin'
      }
      if (!columnFormat.textWarp) {
        contextMenu.menu[3].isActive = true
      } else {
        contextMenu.menu[3].isActive = false
      }

      let includeCellVisual = false
      if (prop.startsWith('data')) includeCellVisual = true
      let { pivotAliasVsField } = this.pivotTable
      let field = pivotAliasVsField[prop]
      if (isDecimalField(field) || isNumberField(field))
        includeCellVisual = true

      if (!includeCellVisual) {
        contextMenu.menu = contextMenu.menu.filter(
          menu => menu.label != 'Cell visualization'
        )
      }

      return contextMenu
    },
    getTextWidth(word, fontSize) {
      let text = document.createElement('span')
      document.body.appendChild(text)

      text.style.fontFamily = "'Aktiv-Grotesk', Helvetica, Arial, sans-serif"
      text.style.fontSize = fontSize ? fontSize + 'px' : 16 + 'px'
      text.style.height = 'auto'
      text.style.width = 'auto'
      text.style.position = 'absolute'
      text.style.whiteSpace = 'no-wrap'
      text.innerHTML = word
      let width = Math.ceil(text.clientWidth)
      document.body.removeChild(text)
      return width + 50
    },

    rerenderTable() {
      // force re-render table...
      setTimeout(() => {
        this.showTable = false
        this.$nextTick(() => (this.showTable = true))
      }, 250)
    },

    pivotSpanMethod() {
      return {
        rowspan: 0,
        colspan: this.tableHeaders.length,
      }
    },
    textAutoSize(prop) {
      let records = []
      this.pivotTable.pivotReconstructedData.records.forEach(ob => {
        records.push(ob[prop].formattedValue)
      })
      let longestWord = records.reduce((a, b) => {
        return a && b && a.length > b.length ? a : b
      })
      let width = this.getTextWidth(longestWord, 13)
      width = width < 500 ? width : 500
      this.$emit('columnResizeEvent', {
        width: width,
        column: prop,
      })
    },
    calculateTableHeight() {
      if (this.isMobileDashboard) {
        return this.config?.height
      }
      let table = this.$refs.pivotTableContainer
      if (this.isMobileDashboard === false) {
        return table.clientHeight - 55
      }
      return table.clientHeight < 600 ? 600 : table.clientHeight
    },

    cellStyle(data) {
      let style = {}

      let columnFormat = JSON.parse(JSON.stringify(this.columnFormatting))

      if (this.theme.density == 'LARGE') {
        style['padding'] = `15px 10px 15px 10px`
      } else if (this.theme.density == 'MEDIUM') {
        style['padding'] = `10px 10px 10px 10px`
      } else {
        style['padding'] = `5px 10px 5px 10px`
      }

      if (data.column.property != 'number') {
        let columnStyle = data.row[data.column.property]?.style
        style['text-align'] = columnStyle.textAlign

        if (columnFormat[data.column.property].textWarp) {
          style['white-space'] = 'nowrap'
          style['overflow'] = 'hidden'
        } else {
          style['white-space'] = 'inherit'
        }

        // conditional format

        if (columnStyle?.blink)
          style['animation'] = 'blinkerCell 1s linear infinite !important'

        if (columnStyle?.bgColor)
          style['backgroundColor'] = `${columnStyle.bgColor} !important`

        if (columnStyle?.textColor)
          style['color'] = `${columnStyle.textColor} !important`
      } else style['text-align'] = 'left'

      return style
    },
    getTextBackground(cellStyleObj, prop) {
      let style = {}
      if (cellStyleObj?.textBgColor) {
        style['backgroundColor'] = cellStyleObj.textBgColor
        style['padding'] = '0px 10px'
        style['border-radius'] = '5px'
      }

      if (cellStyleObj?.textStyle) {
        cellStyleObj.textStyle.forEach(textStyle => {
          let fontStyle = this.getFontStyle(textStyle)
          style = { ...style, ...fontStyle }
        })
      }

      let visualConfig = this.getVisualConfig(prop)
      if (visualConfig?.visualType != 'data-bar') {
        style['width'] = 'fit-content'
      }

      return style
    },
    getFontStyle(value) {
      if (value == 'normal') return {}
      else if (value == 'italic') return { 'font-style': 'italic' }
      else if (value == 'bold') return { 'font-weight': 'bold' }
      else if (value == 'strike-through')
        return { 'text-decoration': 'line-through' }

      return {}
    },
    columnResizeEvent(newWidth, oldWidth, column) {
      let label = document.querySelectorAll(`#column-${column.property}`)
      let pin = document.querySelectorAll(`#pin-${column.property}`)
      let sort = document.querySelectorAll(`#sort-${column.property}`)
      let text = label.textContent
      let textWidth = this.getTextWidth(text, 11)
      if (newWidth < textWidth + 30) {
        label.forEach(l => (l.style.display = 'none'))
      } else if (newWidth > textWidth - 50) {
        label.forEach(l => (l.style.display = 'inline'))
      }
      if (newWidth < 84) {
        sort.forEach(s => (s.style.display = 'none'))
      } else {
        sort.forEach(s => (s.style.display = 'inline'))
      }
      if (newWidth < 50) {
        pin.forEach(p => (p.style.display = 'none'))
      } else {
        pin.forEach(p => (p.style.display = 'inline'))
      }
      this.$emit('columnResizeEvent', {
        width: newWidth,
        column: column.property,
      })
    },
    headerStyle() {
      let style = {}
      style['text-align'] = 'left'
      style['white-space'] = 'nowrap'

      return style
    },
  },
}
</script>

<style lang="scss">
.empty-table {
  padding: 10px;
}
.hide-label {
  display: none !important;
}
.header-icons {
  padding: 0px 5px;
  display: flex;
  align-items: center;
  position: absolute;
  right: 0;
}
.selected-row {
  background: #647faa !important;
  color: #fff !important;
  .el-dropdown-tooltip {
    color: white !important;
    display: flex;
    align-items: center;
  }
}

.el-dropdown-tooltip {
  display: flex;
  align-items: center;
}

.header-icon {
  padding: 0px 5px;
}

.pivot-tool-bar {
  height: 46px;
  width: 100%;
  // background: #fff;
  display: flex;
  padding: 20px 0px;
  align-content: space-between;
  justify-content: space-between;
  margin-bottom: 20px;
  background: #fff;
  border: solid 1px #e2e6f1;
  border-radius: 3px;

  .date-picker-slot {
    margin-top: -20px;
  }
}

.pivot-el-table-section {
  .is-leaf {
    font-weight: 500 !important;
  }
  .el-dropdown-tooltip {
    cursor: pointer;
    display: flex;
    align-items: center;
  }
  .header-label {
    overflow: hidden;
    text-overflow: ellipsis;
    max-width: 100px;
  }
}
.pivot-table-comp-root {
  .el-table__header {
    .el-table__cell {
      padding: 0 !important;
    }
  }
}

.pivot-table-header {
  display: flex;
  padding-left: 20px;
  position: relative;
  justify-content: space-between;
  flex-direction: row;
  height: 33px;
  align-items: center;
  .header-dropdown-container {
    display: flex;
    align-items: center;
  }
  &:hover {
    .sort-column {
      opacity: 0.5 !important;
      cursor: pointer;
    }
  }
}

.pivot-error-container {
  display: flex;
  align-content: space-between;
  flex-direction: column;
  justify-content: flex-end;
  align-items: center;
}

.el-table .cell {
  white-space: inherit;
}

.sort-column {
  opacity: 0;
  cursor: pointer;
}

.current-sort-column {
  font-style: bold;
  opacity: 1;
}

.el-table .cell,
.el-table--border .el-table__cell:first-child .cell {
  padding: 0px !important;
}

@keyframes blinkerCell {
  50% {
    opacity: 0.45;
  }
}
.pivot-table-builder-root {
  .button-row {
    padding: 7px 21px 8px 8px !important;
    font-size: 10px !important;
    letter-spacing: 0.64px;
    .p5 {
      padding: 0px;
    }
    .date-arrow {
      padding-top: 8px !important;
    }
  }
  .button-row:not(.picker-disabled) .el-button {
    font-size: 13px !important;
  }
  .p5 .cal-right-btn {
    padding: 0px !important;
  }
  .button-row .p5 {
    padding: 0px !important;
  }
}
</style>
<style lang="scss" scoped>
.move-left {
  right: 85px !important;
}
</style>
