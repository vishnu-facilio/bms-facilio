<template>
  <div
    v-if="showTable"
    :style="{
      position: 'relative',
      'margin-bottom': widget ? '30px' : '150px',
    }"
    class="modular-new-report-page"
  >
    <template v-if="reportObject && isTabularReport">
      <HotTable
        :style="styleCss"
        ref="hotTable"
        :root="root"
        :settings="hotSettings"
      ></HotTable>
    </template>
    <div class="height100" v-else-if="reportObject">
      <div class="pT0 height100 scrollable">
        <div
          :ref="'fTableContainer_' + tindex"
          class="tablular-container dashboard-tablular-container row full-layout-white scrollbar-style tabular-data-border fc-modular-tabular-report"
          :style="[
            {
              'overflow-x': isOverflowing ? 'scroll' : 'scroll',
              display: 'block',
              'margin-bottom': '20px',
            },
          ]"
          v-for="(table, tindex) in multiTables"
          :key="tindex"
        >
          <table
            style="width:100%; !important"
            :ref="'fTable_' + tindex"
            class="wo-table workorder-table-list tabular-report-table fc-tabular-report-table-new"
            v-if="hotSettings.data.length"
          >
            <thead v-if="tableState.groupByColumns.length !== 0 && !isPrinting">
              <tr
                v-if="
                  reportObject.report.dataPoints[0].groupByFields &&
                    reportObject.report.dataPoints[0].groupByFields.length > 1
                "
              >
                <th></th>
                <th
                  v-for="(group, groupIdx) in tableState.groupByColumns"
                  :key="groupIdx"
                  :colspan="group.colspan"
                >
                  {{ group.label }}
                </th>
              </tr>
              <tr>
                <th
                  colspan="1"
                  v-for="(groupHeading,
                  groupHeadingIdx) in computeGroupByHeadings"
                  :key="groupHeadingIdx"
                >
                  {{ groupHeading.label }}
                </th>
              </tr>
            </thead>
            <thead v-else>
              <tr>
                <th v-if="tableState.groupByColumns.length === 0 || isPrinting">
                  {{ $t('common.products.no') }}
                </th>
                <th
                  class="text-left"
                  v-for="(header, index) in table.columns"
                  :key="index"
                >
                  <div
                    class="inline"
                    :style="!isPrinting ? { width: '89%' } : ''"
                    v-html="header"
                  ></div>
                  <div
                    class="flRight"
                    v-if="
                      hotSettings.columns[getColIdx(index, tindex)].showOptions
                    "
                  >
                    <f-popover
                      placement="right"
                      v-model="
                        hotSettings.columns[getColIdx(index, tindex)].popover
                      "
                      trigger="click"
                      width="370"
                      @save="saveOptions(index, tindex)"
                      @show="initOptions(index, tindex)"
                      confirmTitle="Save"
                    >
                      <div
                        class="customize-input-block customize-radio-block"
                        v-if="options.state && options.state.enumMode"
                      >
                        <p class="customize-label">
                          {{ $t('common._common.mode') }}
                        </p>
                        <div>
                          <el-radio-group v-model="options.state.enumMode">
                            <el-radio-button :label="enumMode.DURATION">{{
                              $t('common._common.duration')
                            }}</el-radio-button>
                            <el-radio-button :label="enumMode.PERCENT">{{
                              $t('common._common.percentage')
                            }}</el-radio-button>
                          </el-radio-group>
                        </div>
                        <el-checkbox class="pT10" v-model="options.applyAll">{{
                          $t('common.dashboard.apply_to_all_similar_columns')
                        }}</el-checkbox>
                      </div>
                      <div slot="reference" class="pointer">
                        <i class="el-icon-more rotate90deg"></i>
                      </div>
                    </f-popover>
                  </div>
                </th>
                <th class="extra-th" v-if="table.showExtraColumn"></th>
              </tr>
            </thead>
            <tbody v-if="tableState.groupByColumns.length !== 0 && !isPrinting">
              <tr
                class="tablerow"
                v-for="(data, dataIdx) in hotSettings.data"
                :key="dataIdx"
              >
                <td
                  class="tabular-data-td"
                  style="text-align: center;min-width: 49px;max-width: 49px;color: #879eb5 !important;font-size: 12px;font-weight: 500;letter-spacing: 1px;text-transform: uppercase;text-align: center;"
                  :style="!isPrinting ? 'min-width: 49px;' : ''"
                >
                  {{ dataIdx + 1 }}
                </td>
                <td
                  class="pointer tabular-data-td"
                  :style="
                    !isPrinting
                      ? {
                          'min-width': table.colWidth,
                          'max-width': table.colWidth,
                        }
                      : ''
                  "
                  v-for="(val, valIdx) in data"
                  :key="valIdx"
                  colspan="1"
                >
                  <div @click="drillDown(dataIdx, valIdx + 1)">{{ val }}</div>
                </td>
              </tr>
            </tbody>
            <tbody v-else>
              <tr
                class="tablerow"
                v-for="(data, idx) in hotSettings.data"
                :key="idx"
              >
                <td
                  class="tabular-data-td"
                  style="text-align: center;min-width: 49px;max-width: 49px;color: #879eb5 !important;font-size: 12px;font-weight: 500;letter-spacing: 1px;text-transform: uppercase;"
                  :style="!isPrinting ? 'min-width: 49px;' : ''"
                >
                  {{ idx + 1 }}
                </td>
                <td
                  :class="[
                    'tabular-data-td',
                    hotSettings.columns[getColIdx(index, tindex)].type ===
                    'numeric'
                      ? 'text-right'
                      : 'text-left',
                  ]"
                  v-for="(header, index) in table.columns"
                  :key="index"
                  :style="
                    !isPrinting
                      ? {
                          'min-width': table.colWidth,
                          'max-width': table.colWidth,
                        }
                      : ''
                  "
                >
                  <div
                    v-if="
                      hotSettings.columns[getColIdx(index, tindex)].renderer ===
                        'html'
                    "
                    v-html="data[getColIdx(index, tindex)]"
                  ></div>
                  <div class="pointer" v-else @click="drillDown(idx)">
                    {{ data[getColIdx(index, tindex)] }}
                  </div>
                </td>
                <td class="extra-td" v-if="table.showExtraColumn"></td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
// import formatter from 'charts/helpers/formatter'
import HotTable from '@handsontable/vue'
import NewDataFormatHelper from 'pages/report/mixins/NewDataFormatHelper'
import ModularAnalyticmixin from 'src/pages/energy/analytics/mixins/ModularAnalyticmixin'
import FPopover from '@/FPopover'
import * as d3 from 'd3'

const MAX_COLUMN = 5
const EnumMode = {
  DURATION: 1,
  PERCENT: 2,
  GRAPH: 3,
}

export default {
  components: {
    HotTable,
    FPopover,
  },
  mixins: [NewDataFormatHelper, ModularAnalyticmixin],
  props: [
    'reportObject',
    'reportConfig',
    'widget',
    'printReverse',
    'moduleName',
  ],
  data() {
    return {
      root: 'tabular-report-hot',
      minWidth: 225,
      maxWidth: 335,
      hotSettings: {
        data: [],
        columns: [],
        colHeaders: true,
        rowHeaders: true,
        columnHeaderHeight: 75,
        renderAllRows: true,
        colWidths: '',
        readOnly: true,
        columnSorting: true,
        manualColumnResize: true,
        manualColumnMove: true,
        fixedColumnsLeft: 1,
        stretchH: 'none',
      },
      hotTable: null,
      autoRowSize: null,
      widgetSettings: {
        manualColumnResize: false,
        manualColumnMove: false,
        fixedColumnsLeft: 0,
        stretchH: 'all',
      },
      tableState: {
        // table config to save
        columns: [],
      },
      colspan: null,
      baselineMap: {},
      dataMap: {},
      initialRendered: false,
      showExtraColumn: false,
      isOverflowing: false,
      multiTables: [],
      maxColumn: MAX_COLUMN,
      enumMode: EnumMode,
      options: {
        applyAll: false,
        state: null,
      },
    }
  },
  mounted() {
    this.init()
    this.render()
  },
  updated() {
    if (!this.isTabularReport) {
      this.setHotColWidth()
    }
  },
  computed: {
    isGroupByCrossModule() {
      if (this.reportObject.report.dataPoints[0].groupByFields !== null) {
        let groupField = this.reportObject.report.dataPoints[0].groupByFields[0]
        if (
          groupField.field.module.name ===
            this.reportObject.report.module.name ||
          (this.reportObject.report.module.extendModule &&
            groupField.field.module.name ===
              this.reportObject.report.module.extendModule.name)
        ) {
          return false
        }
        return true
      }
      return false
    },
    isXFieldCrossModule() {
      let xAxis = this.reportObject.report.dataPoints[0].xAxis
      if (
        xAxis.field.module.name === this.reportObject.report.module.name ||
        (this.reportObject.report.module.extendModule &&
          xAxis.field.module.name ===
            this.reportObject.report.module.extendModule.name)
      ) {
        return false
      }
      return true
    },
    computeGroupByHeadings() {
      let groupByHeadings = []
      // populate No and X column Heading
      if (
        typeof this.printReverse !== 'undefined' &&
        this.printReverse === true
      ) {
        let heading = {}
        heading['label'] = 'No'
        groupByHeadings.push(heading)
        let requiredGroups = this.requiredGroupValues(this.reportObject)
        for (let colHeading of this.prepareReverseColumnHeadings()) {
          heading = {}
          heading['label'] = colHeading
          heading['id'] = Object.keys(requiredGroups).filter(
            key => requiredGroups[key] === colHeading
          )[0]
          groupByHeadings.push(heading)
        }
      } else {
        let heading = {}
        heading['label'] = 'No'
        groupByHeadings.push(heading)
        let xHeading = {}
        xHeading['label'] = this.reportConfig.options.axis.x.label.text

        groupByHeadings.push(xHeading)

        if (
          this.reportObject.report.dataPoints[0].groupByFields &&
          this.reportObject.report.dataPoints[0].groupByFields.length === 1
        ) {
          let groupMap = this.requiredGroupValues(this.reportObject)
          for (let key of Object.keys(groupMap)) {
            let temp = { label: groupMap[key], id: key }
            groupByHeadings.push(temp)
          }
        }
      }
      let heading = {}
      heading['label'] = 'Total'
      groupByHeadings.push(heading)
      return groupByHeadings
    },
    showTable() {
      return (
        this.reportObject &&
        (!this.widget || this.reportConfig.options.settings.chart === false) &&
        this.reportConfig.options.settings.table
      )
    },
    isTabularReport() {
      return this.reportConfig && this.reportConfig.options.type === 'tabular'
    },
    isPrinting() {
      return this.$route.query.print || this.$route.query.daterange // daterange check temp
    },
    styleCss() {
      return 'width: 100% !important;'
    },
  },
  watch: {
    reportObject: {
      handler() {
        this.render()
      },
      deep: true,
    },
    reportConfig: {
      handler() {
        this.render()
      },
      deep: true,
    },
  },
  methods: {
    getCorrespondingDomainIndex(rowIndex) {
      let timeString = this.reportObject.reportData.data[rowIndex].X
      let period = this.reportConfig.dateRange.period
      let dateFormat = this.getDateFormat(period).format
      let formattedTime = this.getFormattedTime(timeString, dateFormat)
      for (
        let index = 0;
        index < this.reportConfig.dateRange.range.domain.length;
        index++
      ) {
        if (this.reportConfig.dateRange.range.domain[index] === formattedTime) {
          return index
        }
      }
    },
    prepareValForDrillDown(rowIndex, colIndex) {
      let val = {}
      let isTime =
        this.reportObject.report.dataPoints[0].xAxis.dataType === 5 ||
        this.reportObject.report.dataPoints[0].xAxis.dataType === 6
      if (isTime) {
        val['index'] = this.getCorrespondingDomainIndex(rowIndex)
      } else {
        val['index'] = rowIndex
      }
      if (this.tableState.groupByColumns.length !== 0) {
        // group by applied
        if (colIndex === 0 || colIndex === 1) {
          val['id'] = null
        } else {
          let groupName = this.computeGroupByHeadings[colIndex].label
          val['id'] = groupName
        }

        return val
      } else {
        return val
      }
    },
    drillDown(rowIndex, colIndex) {
      let appNameFromUrl = window.location.pathname.slice(1).split('/')[0]
      if (
        !this.isXFieldCrossModule &&
        !this.isGroupByCrossModule &&
        appNameFromUrl !== 'operations'
      ) {
        let val = this.prepareValForDrillDown(rowIndex, colIndex)
        this.handleBasicDrillDown(val, this.reportObject, this.reportConfig)
      }
    },
    init() {
      let self = this

      this.hotSettings.afterColumnMove = function() {
        self.saveCurrentState()
      }

      this.hotSettings.afterColumnResize = function() {
        self.saveCurrentState()
      }

      this.hotSettings.afterRender = function() {
        if (!self.hotTable && self.$refs['hotTable']) {
          self.hotTable = self.$refs['hotTable'].table
        }
        if (!self.initialRendered) {
          self.initialRendered = true
        }
      }
    },
    resize() {
      this.setHotColWidth()
    },
    setHotColWidth() {
      if (
        (!Array.isArray(this.hotSettings.colWidths) || !this.isTabularReport) &&
        this.showTable &&
        (this.$refs['hotTable'] ||
          (this.$refs['fTableContainer_0'] &&
            this.$refs['fTableContainer_0'][0]))
      ) {
        let width = this.isTabularReport
          ? this.$refs['hotTable'].$el.clientWidth - 47
          : this.$refs['fTableContainer_0'][0].clientWidth - 49
        if (this.multiTables.length) {
          this.multiTables.forEach(table => {
            let cols = table.columns.length
            let colWidth = width / cols
            if (colWidth < this.minWidth) {
              colWidth = this.minWidth
            } else if (colWidth > this.maxWidth) {
              colWidth = this.maxWidth
            }

            if (colWidth * cols < width) {
              table.showExtraColumn = false
            }

            table.colWidth = colWidth + 'px'
            this.hotSettings.colWidths = colWidth + 'px'
          })
        }
      }
    },
    render() {
      if (!this.showTable) {
        return
      }
      if (
        typeof this.printReverse !== 'undefined' &&
        this.printReverse === true
      ) {
        this.setTableStateConfig()
        this.prepareReverseHotConfig()
        this.setTableData()
        this.setMultipleTables()
      } else {
        this.setTableStateConfig()
        this.setHotConfig()
        this.setTableData()
        this.setMultipleTables()
      }
    },

    /**
     * Setting columns in table state..If state already available, order of columns will be maintained
     */
    prepareReverseColumnHeadings() {
      let colVals = []
      let xAlias = this.reportObject.report.xAlias
        ? this.reportObject.report.xAlias
        : 'X'
      let xDataType = this.reportObject.report.dataPoints[0].xAxis.dataType
      let isTimeSeries = xDataType === 5 || xDataType === 6
      let isXLookUp =
        this.reportObject.report.dataPoints[0].xAxis.dataTypeEnum.toLowerCase() ===
          'lookup' ||
        this.reportObject.report.dataPoints[0].xAxis.fieldName === 'siteId'
      let isXBooleanOrEnum =
        this.reportObject.report.dataPoints[0].xAxis.dataTypeEnum.toLowerCase() ===
          'boolean' ||
        this.reportObject.report.dataPoints[0].xAxis.dataTypeEnum.toLowerCase() ===
          'enum'
      if (this.reportObject.report.dataPoints[0].groupByFields) {
        colVals.push(
          this.reportObject.report.dataPoints[0].groupByFields[0].field
            .displayName
        )
      } else {
        colVals.push('Metric')
      }
      for (let data of this.reportObject.reportData.data) {
        let xValue = data[xAlias]
        if (isTimeSeries && xValue && xValue != '') {
          let period = this.reportConfig.dateRange.period
          colVals.push(this.formatDate(new Date(parseInt(xValue)), period))
        } else if (isXLookUp && xValue && xValue != '') {
          let lookupMap = this.reportObject.report.dataPoints[0].xAxis.lookupMap
          if (lookupMap[xValue]) {
            colVals.push(lookupMap[xValue])
          }
        } else if (isXBooleanOrEnum && xValue && xValue != '') {
          let enumMap = this.reportObject.report.dataPoints[0].xAxis.enumMap
          if (enumMap[xValue]) {
            colVals.push(enumMap[xValue])
          }
        } else {
          if (xValue && xValue !== '') {
            colVals.push(xValue)
          } else {
            continue
          }
        }
      }
      colVals.push('Total')
      return colVals
    },
    prepareReverseData() {
      let reversedData = []
      if (this.tableState.groupByColumns.length === 0) {
        for (let dataPoint of this.reportObject.report.dataPoints) {
          let dataPointAlias = dataPoint.aliases.actual
          let rowData = []
          rowData.push(
            dataPoint.name +
              (dataPoint.yAxis && dataPoint.yAxis.unitStr
                ? ' (' + dataPoint.yAxis.unitStr + ')'
                : '')
          )
          let total = 0
          for (let data of this.reportObject.reportData.data) {
            if (
              dataPoint.yAxis.dataTypeEnum.toLowerCase() === 'lookup' ||
              dataPoint.yAxis.dataTypeEnum.toLowerCase() === 'enum'
            ) {
              let map =
                dataPoint.yAxis.dataTypeEnum.toLowerCase() === 'lookup'
                  ? dataPoint.yAxis.lookupMap
                  : dataPoint.yAxis.enumMap
              if (data[dataPointAlias] !== null) {
                total += parseFloat(map[data[dataPointAlias]])
                rowData.push(map[data[dataPointAlias]])
              } else {
                rowData.push(null)
              }
            } else {
              total += parseFloat(data[dataPointAlias])
              rowData.push(data[dataPointAlias])
            }
          }
          rowData.push(total)
          reversedData.push(rowData)
        }
      } else {
        // supported only for one level of group by
        let requiredGroups = this.requiredGroupValues(this.reportObject)
        let xAlias = this.reportObject.report.xAlias
          ? this.reportObject.report.xAlias
          : 'X'
        let firstLevelGrouplabel = this.reportObject.report.dataPoints[0]
          .groupByFields[0].fieldName
        for (let dataPoint of this.reportObject.report.dataPoints) {
          for (let key in requiredGroups) {
            let rowData = []
            rowData.push(requiredGroups[key])
            let total = 0
            for (let data of this.reportObject.reportData.data) {
              if (
                dataPoint.xAxis.dataTypeEnum.toLowerCase() === 'lookup' ||
                dataPoint.xAxis.dataTypeEnum.toLowerCase() === 'enum'
              ) {
                let map =
                  dataPoint.xAxis.dataTypeEnum.toLowerCase() === 'lookup'
                    ? dataPoint.xAxis.lookupMap
                    : dataPoint.xAxis.enumMap
                let mapKeys = Object.keys(map)
                if (!mapKeys.includes(data[xAlias] + '')) {
                  continue
                }
              }
              let firstLevelGroupData = data[firstLevelGrouplabel]
              let groupData = firstLevelGroupData.filter(
                groupData =>
                  groupData[firstLevelGrouplabel] === parseInt(key) ||
                  groupData[firstLevelGrouplabel] === requiredGroups[key]
              )
              if (groupData && groupData.length > 0) {
                total += parseFloat(groupData[0][dataPoint.aliases.actual])
                rowData.push(groupData[0][dataPoint.aliases.actual])
              } else {
                rowData.push(0)
              }
            }
            rowData.push(total)
            reversedData.push(rowData)
          }
        }
      }

      this.hotSettings.data = reversedData
    },

    prepareReverseHotConfig() {
      this.hotSettings.colHeaders = this.prepareReverseColumnHeadings()
      this.hotSettings.columns = []
      this.hotSettings.colWidths = '335px'
      for (let column of this.prepareReverseColumnHeadings()) {
        let temp = {}
        temp['name'] = column
        temp['type'] = 'text'
        this.hotSettings.columns.push(temp)
      }
    },
    setTableStateConfig() {
      this.tableState = {
        columns: [],
        groupByColumns: [],
      }

      let dataPoints = []
      if (
        typeof this.printReverse !== 'undefined' &&
        this.printReverse === true
      ) {
        if (this.reportObject.report.dataPoints[0].groupByFields) {
          this.tableState.groupByColumns = this.prepareReverseColumnHeadings()
        } else {
          this.tableState.columns = this.prepareReverseColumnHeadings()
        }
      } else if (
        this.reportConfig.options.dataPoints.filter(
          element => element.type === 'systemgroup'
        ).length
      ) {
        // only for first level group by
        let sysGroup = this.reportConfig.options.dataPoints.filter(
          element => element.type === 'systemgroup'
        )[0]
        let firstGroupKey = sysGroup.groupBy[0].id
        let firstGroupByLabelValues = sysGroup.groupByLabelValues[firstGroupKey]
        let definedColumns = 0
        for (let column in this.reportConfig.options.axis) {
          if (
            this.reportConfig.options.axis[column] &&
            this.reportConfig.options.axis[column].label
          ) {
            if (
              this.reportConfig.options.axis[column].label.text !== null &&
              this.reportConfig.options.axis[column].label.text !== ''
            ) {
              definedColumns = definedColumns + 1
            }
          }
        }
        definedColumns = definedColumns - 1 // remove X count
        this.colspan = definedColumns
        this.tableState.groupByColumns.push({
          renderer: 'html',
          label: '',
          colspan: this.colspan,
        })

        for (let groupId in firstGroupByLabelValues) {
          let groupLabel = firstGroupByLabelValues[groupId]
          let temp = {}
          temp['renderer'] = 'html'
          temp['label'] = groupLabel
          temp['colspan'] = this.colspan
          this.tableState.groupByColumns.push(temp)
        }
      }

      this.reportConfig.options.dataPoints.forEach(dp => {
        if (dp.type === 'group') {
          dataPoints.push(...dp.children)
        } else {
          dataPoints.push(dp)
        }
      })

      let aliases = [this.reportObject.report.xAlias]
      dataPoints.forEach(dp => {
        this.dataMap[dp.alias] = dp
        if (!dp.isBaseLine || dp.pointType !== 2) {
          aliases.push(dp.alias)
        }
      })

      let setTableStateColumns = (aliases, idx) => {
        let columns = aliases.map(alias => ({ alias }))
        if (idx >= 0) {
          this.tableState.columns.splice(idx, 0, ...columns)
        } else {
          this.tableState.columns.push(...columns)
        }
      }

      if (
        this.reportObject.report.tabularState &&
        this.reportObject.report.tabularState !== 'null'
      ) {
        this.tableState =
          typeof this.reportObject.report.tabularState === 'string'
            ? JSON.parse(this.reportObject.report.tabularState)
            : this.$helpers.cloneObject(this.reportObject.report.tabularState)
        // Temp
        if (
          this.tableState.columns.length &&
          !this.tableState.columns[0].alias
        ) {
          this.tableState.columns = []
        }
      }

      if (
        typeof this.printReverse === 'undefined' ||
        this.printReverse === false
      ) {
        if (this.tableState.columns.length) {
          this.tableState.columns = this.tableState.columns.filter(column =>
            aliases.includes(column.alias)
          )
          // TODO verify
          if (this.tableState.columns[0].alias !== aliases[0]) {
            let xColIdx = this.tableState.columns.findIndex(
              col => col.alias === aliases[0]
            )
            if (xColIdx !== -1) {
              let xCols = this.tableState.columns.splice(xColIdx, 1)
              this.tableState.columns.unshift(xCols[0])
            } else {
              setTableStateColumns([aliases[0]], 0)
            }
          }
          if (this.tableState.columns.length !== aliases.length) {
            let existingHeaders = this.tableState.columns.map(
              column => column.alias
            )
            let remainingHeaders = aliases.filter(
              header => !existingHeaders.includes(header)
            )
            setTableStateColumns(remainingHeaders)
          }
        } else {
          setTableStateColumns(aliases)
        }
      }
    },

    /**
     * Setting Hot table configuration and column header names
     */
    setHotConfig() {
      let hotColHeaders = []
      let hotColWidths = []
      let hotColumns = []

      let getHotType = dataType => {
        return dataType === 2 || dataType === 3 || dataType === 11
          ? 'numeric'
          : 'text'
      }
      let label
      let type = 'text'
      if (this.reportObject.mode === 6) {
        label = 'Site'
      } else if (this.reportObject.mode === 7) {
        label = 'Building'
      } else if (this.reportObject.mode === 8) {
        label = 'Asset'
      } else {
        let xAxis = this.reportObject.report.dataPoints[0].xAxis
        label = xAxis.label
        if (xAxis.lookupMap) {
          type = 'text'
        } else {
          type = getHotType(xAxis.dataType)
        }
      }
      hotColHeaders.push(label.toUpperCase())
      let xColumn = this.tableState.columns[0]
      hotColumns.push({
        type: type,
        name: xColumn.alias,
      })
      if (xColumn.width) {
        hotColWidths.push(xColumn.width)
      }

      let noOfColumns
      let tableStateColumns
      if (
        this.reportConfig &&
        this.reportConfig.options &&
        this.reportConfig.options.isSystemGroup
      ) {
        tableStateColumns = this.computeGroupByHeadings.slice(2)

        for (let i = 0; i < tableStateColumns.length; i++) {
          let tableColumn = tableStateColumns[i]

          hotColHeaders.push(tableColumn.label.toString().toUpperCase())
          let column = {
            type: 'text',
            name: tableColumn.label,
          }
          hotColumns.push(column)
          if (tableColumn.width) {
            hotColWidths.push(tableColumn.width)
          }
        }
      } else {
        noOfColumns = this.tableState.columns.length
        tableStateColumns = this.tableState.columns

        for (let i = 1; i < noOfColumns; i++) {
          let column = tableStateColumns[i]
          let dataPoint = this.dataMap[column.alias]

          if (column.displayName) {
            hotColHeaders.push(column.displayName.toUpperCase())
          } else {
            let name =
              dataPoint.label.toUpperCase() +
              (dataPoint.unitStr ? ' (' + dataPoint.unitStr + ')' : '')
            hotColHeaders.push(name)
          }
          let col = {
            type: getHotType(dataPoint.dataTypeId),
            name: dataPoint.alias,
          }
          if (dataPoint.dataTypeId === 4 || dataPoint.dataTypeId === 8) {
            col.renderer = 'html'
            if (this.reportObject.xAggr) {
              col.showOptions = true
              if (!column.enumMode) {
                column.enumMode = EnumMode.DURATION
              } else if (column.enumMode === EnumMode.PERCENT) {
                hotColHeaders[i] = hotColHeaders[i] + ' (%)'
              }
            } else {
              column.enumMode = null
            }
          }
          hotColumns.push(col)
          if (column.width) {
            hotColWidths.push(column.width)
          }
        }
      }

      if (hotColWidths.length === hotColumns.length) {
        this.hotSettings.colWidths = hotColWidths
      }
      this.hotSettings.columns = hotColumns
      this.hotSettings.colHeaders = hotColHeaders

      if (this.widget) {
        Object.assign(this.hotSettings, this.widgetSettings)
      }
    },
    getRequiredGroupMap(groupMap, requiredIds) {
      let temp = {}
      for (let id of requiredIds) {
        let label = groupMap[parseInt(id)]
        temp[id] = label
      }
      return temp
    },
    prepareGroupByData() {
      let data = []
      let isXLookup =
        this.reportObject.report.dataPoints[0].xAxis.dataTypeEnum.toLowerCase() ===
          'lookup' ||
        this.reportObject.report.dataPoints[0].xAxis.fieldName === 'siteId' ||
        this.reportObject.report.dataPoints[0].xAxis.dataTypeEnum.toLowerCase() ===
          'multi_lookup'
      let isXBooleanOrEnum =
        this.reportObject.report.dataPoints[0].xAxis.dataTypeEnum.toLowerCase() ===
          'boolean' ||
        this.reportObject.report.dataPoints[0].xAxis.dataTypeEnum.toLowerCase() ===
          'enum' ||
        this.reportObject.report.dataPoints[0].xAxis.dataTypeEnum.toLowerCase() ===
          'multi_enum' ||
        this.reportObject.report.dataPoints[0].xAxis.dataTypeEnum.toLowerCase() ===
          'system_enum'

      let xAlias = this.reportObject.report.xAlias
        ? this.reportObject.report.xAlias
        : 'X'
      let columns = []
      let requiredGroupIds = []
      for (let data of this.reportObject.reportData.data) {
        let firstLevelGroupLabel = this.reportObject.groupIds[0].name
        let recordData = data[firstLevelGroupLabel]
        for (let record of recordData) {
          if (
            !requiredGroupIds.includes(
              parseInt(record[firstLevelGroupLabel])
            ) &&
            Number.isInteger(parseInt(record[firstLevelGroupLabel]))
          ) {
            requiredGroupIds.push(record[firstLevelGroupLabel])
          }
        }
      }

      for (let dataPoint of this.reportObject.report.dataPoints) {
        let temp = {}
        temp['alias'] = dataPoint.aliases.actual
        temp['label'] = dataPoint.name
        columns.push(temp)
      }

      for (let record of this.reportObject.reportData.data) {
        let rowData = []
        if (isXLookup) {
          let xId = record[xAlias]
          let xLabel = this.reportObject.report.dataPoints[0].xAxis.lookupMap[
            xId
          ]
          if (xLabel) {
            rowData.push(xLabel)
          } else {
            continue
          }
        } else if (isXBooleanOrEnum) {
          let enumMap = this.reportObject.report.dataPoints[0].xAxis.enumMap
          let enumId = record[xAlias]
          if (enumMap[enumId]) {
            let labelValue = enumMap[enumId]
            rowData.push(labelValue)
          } else {
            continue
          }
        } else {
          if (
            this.reportObject.report.dataPoints[0].xAxis.dataType === 5 ||
            this.reportObject.report.dataPoints[0].xAxis.dataType === 6
          ) {
            let period = this.reportConfig.dateRange.period
            if (record[xAlias]) {
              rowData.push(
                this.formatDate(new Date(parseInt(record[xAlias])), period)
              )
            } else {
              continue
            }
          } else {
            if (record[xAlias]) {
              rowData.push(record[xAlias])
            } else {
              continue
            }
          }
        }

        // to get y values
        let firstLevelGroupLabel = this.reportObject.groupIds[0].name
        let groupMap = this.reportObject.groupLabelValues[
          this.reportObject.groupIds[0].id
        ]
        groupMap = this.requiredGroupValues(this.reportObject)
        let yData = record[firstLevelGroupLabel]
        let dataOfGroup = null
        let total = 0
        for (let groupId in groupMap) {
          dataOfGroup = yData.filter(
            element =>
              element[firstLevelGroupLabel] === parseInt(groupId) ||
              element[firstLevelGroupLabel] === groupMap[groupId]
          )
          if (dataOfGroup.length === 0) {
            for (let j = 0; j < columns.length; j++) {
              rowData.push(0)
            }
          } else {
            for (let column of columns) {
              total += parseFloat(dataOfGroup[0][column.alias])
              rowData.push(dataOfGroup[0][column.alias])
            }
          }
        }
        rowData.push(total)
        data.push(rowData)
      }
      return data
    },
    setTableData() {
      let tableData = []
      let xColumn = this.tableState.columns[0]
      let xDataType = this.reportObject.report.dataPoints[0].xAxis.dataType
      let isTimeSeries = xDataType === 5 || xDataType === 6
      let isXLookUp =
        this.reportObject.report.dataPoints[0].xAxis.dataTypeEnum.toLowerCase() ===
          'lookup' ||
        this.reportObject.report.dataPoints[0].xAxis.fieldName === 'siteId' ||
        this.reportObject.report.dataPoints[0].xAxis.dataTypeEnum.toLowerCase() ===
          'multi_lookup'
      let isXBooleanOrEnum =
        this.reportObject.report.dataPoints[0].xAxis.dataTypeEnum.toLowerCase() ===
          'boolean' ||
        this.reportObject.report.dataPoints[0].xAxis.dataTypeEnum.toLowerCase() ===
          'enum' ||
        this.reportObject.report.dataPoints[0].xAxis.dataTypeEnum.toLowerCase() ===
          'multi_enum' ||
        this.reportObject.report.dataPoints[0].xAxis.dataTypeEnum.toLowerCase() ===
          'system_enum'

      let period = this.getTimePeriod(this.reportObject)

      if (
        typeof this.printReverse != 'undefined' &&
        this.printReverse === true
      ) {
        this.prepareReverseData()
      } else if (this.reportConfig.options.isSystemGroup) {
        this.hotSettings.data = this.prepareGroupByData()
      } else {
        this.reportObject.reportData.data.forEach(row => {
          let newRow = []
          if (isXLookUp) {
            if (xColumn.alias === null || xColumn.alias === undefined) {
              xColumn.alias = 'X'
            }
            let lookupId = row[xColumn.alias]
            if (
              this.reportObject.report.dataPoints[0].xAxis.lookupMap[lookupId]
            ) {
              let labelValue = this.reportObject.report.dataPoints[0].xAxis
                .lookupMap[lookupId]
              newRow.push(labelValue)
            } else {
              return
            }
          } else if (isXBooleanOrEnum) {
            if (xColumn.alias === null || xColumn.alias === undefined) {
              xColumn.alias = 'X'
            }
            let enumMap = this.reportObject.report.dataPoints[0].xAxis.enumMap
            let enumId = row[xColumn.alias]
            if (enumMap[enumId]) {
              let labelValue = enumMap[enumId]
              newRow.push(labelValue)
            } else {
              return
            }
          } else {
            if (xColumn.alias === null || xColumn.alias === undefined) {
              xColumn.alias = 'X'
            }
            let xVal = row[xColumn.alias]
            if (xVal && isTimeSeries) {
              xVal = this.formatDate(xVal, period)
              newRow.push(xVal)
            } else if (xVal) {
              newRow.push(xVal)
            } else {
              return
            }
          }
          for (let i = 1; i < this.tableState.columns.length; i++) {
            let column = this.tableState.columns[i]
            let value = row[column.alias]
            if (value) {
              let dataPoint = this.dataMap[column.alias]
              if (dataPoint.dataTypeId === 2 || dataPoint.dataTypeId === 3) {
                value = d3.format(',')(value)
              } else if (
                dataPoint.dataTypeId === 4 ||
                dataPoint.dataTypeId === 8
              ) {
                let enumVal = value
                value = null
                if (!column.enumMode) {
                  if (enumVal.timeline && enumVal.timeline[0]) {
                    value = dataPoint.enumMap[enumVal.timeline[0].value]
                  }
                } else if (column.enumMode === EnumMode.GRAPH) {
                  // TODO
                } else if (column.enumMode === EnumMode.PERCENT) {
                  if (enumVal.duration) {
                    let total = Object.keys(enumVal.duration).reduce(function(
                      previous,
                      key
                    ) {
                      return previous + enumVal.duration[key]
                    },
                    0)
                    let percent = ''
                    Object.keys(enumVal.duration)
                      .sort()
                      .forEach(key => {
                        value = (enumVal.duration[key] / total) * 100
                        value = Math.round(value * 100) / 100
                        percent +=
                          dataPoint.enumMap[key] + ': ' + value + '<br/>'
                      })
                    value = percent
                  }
                } else {
                  // DURATION
                  if (enumVal.duration) {
                    let duration = ''
                    Object.keys(enumVal.duration)
                      .sort()
                      .forEach(key => {
                        duration +=
                          dataPoint.enumMap[key] +
                          ': ' +
                          enumVal.duration[key] +
                          '<br/>'
                      })
                    value = duration
                  }
                }
              }
            }
            newRow.push(value)
          }
          tableData.push(newRow)
        })
        this.hotSettings.data = tableData
      }
    },

    /**
     *   Splitting table for print view
     */
    setMultipleTables() {
      this.multiTables = []
      this.maxColumn = this.hotSettings.colHeaders.length
      if (this.isPrinting) {
        this.maxColumn = MAX_COLUMN
        let metricIndexes = []
        this.hotSettings.colHeaders.forEach((header, idx) => {
          if (idx > 0 && idx % this.maxColumn === 0) {
            metricIndexes.push(idx)
          }
        })
        metricIndexes.forEach(idx => {
          this.hotSettings.colHeaders.splice(
            idx,
            0,
            this.hotSettings.colHeaders[0]
          )
          this.hotSettings.columns.splice(idx, 0, this.hotSettings.columns[0])
          if (this.printReverse) {
            this.hotSettings.data.forEach(data => {
              data.splice(idx, 0, data[0])
            })
          } else {
            this.hotSettings.data.splice(idx, 0, this.hotSettings.data[0])
          }
        })
      }
      for (let i = 0; i < this.hotSettings.colHeaders.length; i++) {
        let tableNo = Math.floor(i / this.maxColumn)
        let table = this.multiTables[tableNo]
        if (!table) {
          table = {
            columns: [],
            colWidth: '',
            showExtraColumn: false,
          }
          this.$set(this.multiTables, tableNo, table)
        }
        table.columns.push(this.hotSettings.colHeaders[i])
      }
    },
    saveCurrentState() {
      let stateColumns = []
      for (let i = 0; i < this.hotTable.getColHeader().length; i++) {
        let colWidth = this.hotTable.getColWidth(i)

        let idx = this.hotTable.toPhysicalColumn(i)
        let name = this.hotSettings.columns[idx].name

        let stateColumn = this.tableState.columns.find(col => col.name === name)
        stateColumn.width = colWidth
        stateColumns.push(stateColumn)
      }
      this.tableState.columns = stateColumns

      this.reportConfig.params.tabularState = JSON.stringify(this.tableState)
    },

    formatDate(date, period) {
      let dateFormat = this.getDateFormat(period)
      return this.$options.filters.toDateFormat(
        new Date(date),
        dateFormat.tooltip
      )
    },

    getColIdx(index, tindex) {
      return (
        (index % this.maxColumn) + (tindex > 0 ? tindex * this.maxColumn : 0)
      )
    },

    initOptions(index, tindex) {
      let idx = this.getColIdx(index, tindex)
      this.options.state = this.$helpers.cloneObject(
        this.tableState.columns[idx]
      )
    },

    saveOptions(index, tindex) {
      let setOption = colIdx => {
        let column = this.tableState.columns[colIdx]
        let dataPoint = this.dataMap[column.alias]
        if (
          dataPoint &&
          (dataPoint.dataTypeId === 4 || dataPoint.dataTypeId === 8)
        ) {
          this.$set(column, 'enumMode', this.options.state.enumMode)
        }
      }
      if (this.options.applyAll) {
        this.tableState.columns.forEach((column, colIdx) => {
          setOption(colIdx)
        })
        this.options.applyAll = false
      } else {
        let idx = this.getColIdx(index, tindex)
        setOption(idx)
      }
      this.setHotConfig()
      this.setTableData()
      this.setMultipleTables()

      this.reportConfig.params.tabularState = JSON.stringify(this.tableState)
    },
  },
}
</script>
<style>
@import '../../../../node_modules/handsontable/dist/handsontable.full.min.css';
</style>
<style>
.tabular-report {
  width: auto !important;
}

.tabular-report thead th {
  white-space: inherit !important;
}

.wtHolder tr th div {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}

.wtHolder tr td,
.wtHolder tr td,
.ht_master tr td {
  vertical-align: middle;
  color: #333 !important;
  padding: 10px;
}

#tabular-report-hot {
  border: 1px solid rgb(230, 235, 240);
  margin-bottom: 150px;
}

.handsontable tbody th.ht__highlight,
.handsontable thead th.ht__highlight {
  background-color: #f1f3f5;
}

.handsontable th {
  background-color: #ffffff;
  color: #879eb5;
  font-size: 12px;
  font-weight: 500;
  letter-spacing: 1px;
  text-transform: uppercase;
}

.handsontable th,
.handsontable td {
  border-right: 1px solid rgb(230, 235, 240) !important;
  border-bottom: 1px solid rgb(230, 235, 240) !important;
}

.handsontable .htNoFrame + td,
.handsontable .htNoFrame + th,
.handsontable.htRowHeaders thead tr th:nth-child(2),
.handsontable td:first-of-type,
.handsontable th:first-child,
.handsontable th:nth-child(2) {
  border-left: none !important;
}

.handsontable tr:first-child td,
.handsontable tr:first-child th {
  border-top: none !important;
}

.handsontable table thead th {
  white-space: pre-line;
  padding: 10px 0 !important;
}

.handsontable thead th .relative {
  display: flex;
  align-items: center;
  justify-content: center;
}

.handsontable .cornerHeader::after {
  content: 'No';
}
.table-overlay {
  position: absolute;
  width: 100%;
  height: 100%;
  background: transparent;
  z-index: 9999;
}

.tabular-report-table {
  border-collapse: collapse;
  border: 1px solid rgb(236, 236, 236);
}

.tabular-report-table th {
  height: 56px;
  background-color: #ffffff !important;
  color: #879eb5 !important;
  font-size: 11px !important;
  font-weight: 500 !important;
  letter-spacing: 1px !important;
  /* text-transform: uppercase !important; */
  padding: 0 10px !important;
  text-align: center !important;
  border: 1px solid rgb(230, 235, 240);
  white-space: normal !important;
  padding-left: 0 !important;
  padding-right: 0 !important;
}
.modular-new-report-page .tabular-report-table th.extra-th {
  border-bottom: 0 !important;
}
.modular-new-report-page .tabular-report-table .tabular-data-td {
  border: 1px solid rgb(230, 235, 240);
  color: #333 !important;
  padding: 13px 10px !important;
}

.modular-new-report-page .tabular-report-table .extra-td {
  border-top: 0 !important;
  width: 100%;
}
/* dashboard widget */

@media print {
  html,
  body {
    height: 100%;
    width: 100%;
    margin: 0;
    padding: 0;
  }
  @page {
    size: A4 portrait;
    max-height: 100%;
    max-width: 100%;
    margin: 0.7cm;
  }
  .tablular-container {
    /* page-break-before:always;
    page-break-after:always; */
    margin-right: 0;
    padding-right: 0;
  }
  table {
    page-break-after: auto;
    page-break-inside: auto;
    border: 1px solid rgb(230, 235, 240);
  }
  tr {
    page-break-inside: avoid;
    page-break-after: auto;
  }
  td {
    page-break-inside: avoid;
    page-break-after: auto;
  }
  thead {
    display: table-header-group;
  }
  tfoot {
    display: table-footer-group;
  }
  .tabular-data-border,
  .tabular-report-table {
    overflow: scroll !important;
    width: 100%;
    /* height: 100%; */
  }
  .fc-modular-tabular-report .tabular-report-table th {
    height: auto !important;
    color: #000 !important;
    font-weight: 600 !important;
    font-size: 0.65rem !important;
    background: #f6fbff !important;
    border: 1px solid #000000 !important;
    text-align: center !important;
  }
  .tabular-report-table th .inline {
    padding-top: 10px !important;
    padding-bottom: 10px !important;
  }
}
</style>
