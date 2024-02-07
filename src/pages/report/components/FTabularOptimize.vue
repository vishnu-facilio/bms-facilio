<template>
  <div>
    <div
      v-if="showTable"
      class="mT50 f-tabular-page"
      :style="{
        position: 'relative',
        'margin-bottom': widget ? '30px' : '150px',
      }"
    >
      <template v-if="reportObject && isTabularReport">
        <!-- <div class="table-overlay"></div> -->
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
            class="tablular-container row full-layout-white scrollbar-style tabular-data-border"
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
            <pagination
              :total="reportObject.reportData.data.length"
              :perPage="100"
              @fromto="fromto"
            ></pagination>
            <table
              :ref="'fTable_' + tindex"
              class="wo-table workorder-table-list tabular-report-table"
              v-if="hotSettings.data.length"
            >
              <thead>
                <tr>
                  <th v-if="!tableConfig.hideIndex">NO</th>
                  <th v-if="tindex > 0">{{ multiTables[0].columns[0] }}</th>
                  <th
                    :colspan="hotSettings.columns[index].columnSpan"
                    :class="['table-col-' + index, 'pL0', 'pR0']"
                    style="padding-left: 0 !important;padding-right: 0 !important;"
                    v-for="(header, index) in table.columns"
                    :key="index"
                  >
                    <div class="inline pT20 pB20" v-html="header"></div>
                    <table
                      v-if="
                        hotSettings.columns[index].hasOwnProperty(
                          'subColumnHeaders'
                        )
                      "
                      class="width100 tabular-inner-table"
                    >
                      <thead>
                        <tr>
                          <th
                            class="text-left tabular-inner-table-th"
                            v-for="(sColumnHeader, sIdx) in hotSettings.columns[
                              index
                            ].subColumnHeaders"
                            :key="sIdx + index"
                            style="bordee-bottom: 0 !important;"
                          >
                            {{ sColumnHeader }}
                          </th>
                        </tr>
                      </thead>
                    </table>
                    <div
                      class="flRight"
                      v-else-if="
                        hotSettings.columns[getColIdx(index, tindex)]
                          .showOptions &&
                          !hotSettings.columns[index].hasOwnProperty(
                            'subColumnHeaders'
                          )
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
                          <p class="customize-label">Mode</p>
                          <div>
                            <el-radio-group v-model="options.state.enumMode">
                              <el-radio-button :label="enumMode.DURATION"
                                >Duration</el-radio-button
                              >
                              <el-radio-button :label="enumMode.PERCENT"
                                >Percent</el-radio-button
                              >
                              <!-- <el-radio-button :label="enumMode.GRAPH">Graph</el-radio-button> -->
                            </el-radio-group>
                          </div>
                          <el-checkbox class="pT10" v-model="options.applyAll"
                            >Apply to all similar columns</el-checkbox
                          >
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
              <tbody class="scroll-y">
                <tr
                  class="tablerow"
                  v-for="(data, idx) in hotSettings.data.slice(start - 1, end)"
                  :key="idx"
                >
                  <td
                    v-if="!tableConfig.hideIndex"
                    class="tabular-data-td"
                    style="text-align: center;min-width: 49px;max-width: 49px;text-align:center;color: #000 !important;font-size: 12px;font-weight: 500;letter-spacing: 1px;text-transform: uppercase;text-align: center;"
                    :style="!isPrinting ? 'min-width: 49px;' : ''"
                  >
                    {{ idx + start }}
                  </td>
                  <td class="tabular-data-td" v-if="tindex > 0">
                    <div>{{ data[0] }}</div>
                  </td>

                  <template v-else v-for="(header, index) in table.columns">
                    <td
                      :key="index"
                      :style="
                        !isPrinting
                          ? {
                              'min-width': table.colWidth,
                              'max-width': table.colWidth,
                            }
                          : ''
                      "
                      :class="[
                        'tabular-data-td',
                        'table-col-' + index,
                        hotSettings.columns[getColIdx(index, tindex)].type ===
                        'numeric'
                          ? 'text-right'
                          : 'text-left',
                      ]"
                      v-if="
                        !hotSettings.columns[index].columnSpan ||
                          (hotSettings.columns[index].columnSpan &&
                            hotSettings.columns[index].columnSpan === 0)
                      "
                    >
                      <div
                        v-if="
                          hotSettings.columns[getColIdx(index, tindex)]
                            .renderer === 'html'
                        "
                        v-html="data[getColIdx(index, tindex)]"
                      ></div>
                      <div v-else>{{ data[getColIdx(index, tindex)] }}</div>
                    </td>
                    <template
                      v-else
                      v-for="(i, didx) in data[getColIdx(index, tindex)]"
                    >
                      <td
                        :key="didx + index"
                        :style="
                          !isPrinting
                            ? {
                                'min-width': table.colWidth,
                                'max-width': table.colWidth,
                              }
                            : ''
                        "
                        :class="[
                          'tabular-data-td',
                          hotSettings.columns[getColIdx(index, tindex)].type ===
                            'numeric' || 'array'
                            ? 'text-right'
                            : 'text-left',
                        ]"
                      >
                        {{ i }}
                      </td>
                    </template>
                  </template>

                  <td class="extra-td" v-if="table.showExtraColumn"></td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import HotTable from '@handsontable/vue'
import NewDataFormatHelper from 'pages/report/mixins/NewDataFormatHelper'
import AnalyticsModelHelper from 'src/pages/report/mixins/FNewAnalyticsModelHelper'
import Pagination from 'pages/report/components/FTabularPagination'
import NumberFormatHelper from 'src/components/mixins/NumberFormatHelper'
import FPopover from '@/FPopover'
import * as d3 from 'd3'
import { formatDuration } from 'charts/helpers/formatter'
import { getFieldOptions } from 'util/picklist'

const MAX_COLUMN = 6
const EnumMode = {
  DURATION: 1,
  PERCENT: 2,
  GRAPH: 3,
}

export default {
  components: {
    HotTable,
    FPopover,
    Pagination,
  },
  mixins: [NewDataFormatHelper],
  props: [
    'reportObject',
    'reportConfig',
    'widget',
    'showWidgetTable',
    'hideColumn',
  ],
  data() {
    return {
      assets: [],
      start: null,
      end: null,
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
      aggrMap: {
        2: 'AVG',
        3: 'SUM',
        4: 'MIN',
        5: 'MAX',
      },
    }
  },
  created() {
    this.loadAssetPickListData()
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
    showTable() {
      return (
        this.reportObject &&
        (!this.widget ||
          this.reportConfig.options.settings.chart === false ||
          this.showWidgetTable) &&
        this.reportConfig.options.settings.table
      )
    },
    isTreeMap() {
      return this.reportConfig && this.reportConfig.options.type === 'treemap'
    },
    isTabularReport() {
      return this.reportConfig && this.reportConfig.options.type === 'tabular'
    },
    tableConfig() {
      return this.reportConfig && this.reportConfig.tableConfig
        ? this.reportConfig.tableConfig
        : this.reportConfig &&
          this.reportConfig.options &&
          this.reportConfig.options.table
        ? this.reportConfig.options.table
        : {}
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
      handler(newData, oldData) {
        this.render()
      },
      deep: true,
    },
    reportConfig: {
      handler(newData, oldData) {
        this.render()
      },
      deep: true,
    },
  },
  methods: {
    async loadAssetPickListData() {
      let { error, options } = await getFieldOptions({
        field: { lookupModuleName: 'asset', skipDeserialize: true },
      })

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.assets = options
      }
    },
    fromto(start, end) {
      this.start = start
      this.end = end
    },
    init() {
      let self = this

      this.hotSettings.afterColumnMove = function(from, to) {
        self.saveCurrentState()
      }

      this.hotSettings.afterColumnResize = function(col, width) {
        self.saveCurrentState()
        self.setTableHeight()
      }

      this.hotSettings.afterRender = function() {
        if (!self.hotTable && self.$refs['hotTable']) {
          self.hotTable = self.$refs['hotTable'].table
          self.setTableHeight()
        }
        if (!self.initialRendered) {
          self.initialRendered = true
          self.setHotColWidth()
        }
      }
    },
    resize() {
      this.setHotColWidth()
    },
    setTableHeight() {
      /* this.$nextTick(() => {
        this.hotSettings.height = this.$refs['hotTable'].$el.querySelector('.ht_master .htCore').clientHeight
      }) */
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
              if (this.hideColumn) {
                table.showExtraColumn = !this.hideColumn
              } else {
                table.showExtraColumn = !this.isPrinting
              }
            }

            // this.isOverflowing = width < this.$refs['fTable'].clientWidth - 50
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
      this.setTableStateConfig()
      this.setHotConfig()
      this.setTableData()
      this.setMultipleTables()
    },

    /**
     * Setting columns in table state..If state already available, order of columns will be maintained
     */
    setTableStateConfig() {
      this.tableState = {
        columns: [],
      }

      let dataPoints = []
      let FieldsIncluded = []
      if (this.isTreeMap) {
        this.$helpers
          .getDataPoints(
            this.reportConfig.options.dataPoints,
            [1, 2, 4, 6],
            true
          )
          .forEach(dp => {
            if (dp.type === 'group') {
              dp.children.forEach(children => {
                if (
                  !FieldsIncluded.includes(children.fieldId) &&
                  children.visible
                ) {
                  FieldsIncluded.push(children.fieldId)
                  dataPoints.push(children)
                }
              })
            } else {
              if (!FieldsIncluded.includes(dp.fieldId) && dp.visible) {
                FieldsIncluded.push(dp.fieldId)
                dataPoints.push(dp)
              }
            }
          })
      } else {
        this.$helpers
          .getDataPoints(
            this.reportConfig.options.dataPoints,
            [1, 2, 4, 6],
            true
          )
          .forEach(dp => {
            if (dp.type === 'group') {
              dp.children.forEach(children => {
                if (children.visible) {
                  dataPoints.push(children)
                }
              })
            } else {
              if (dp.visible) {
                dataPoints.push(dp)
              }
            }
          })
      }

      let aliases = []
      if (
        this.reportConfig &&
        this.reportConfig.data &&
        (this.reportConfig.data.x ||
          this.reportConfig.data.X ||
          this.reportConfig.scatterConfig)
      ) {
        aliases.push(this.reportObject.report.xAlias || 'X')
      }

      // if(this.reportConfig.options.regressionConfig){
      //   let xDataPoint = this.reportObject.report.dataPoints.filter((dp) => dp.yAxis.fieldId === this.reportConfig.options.regressionConfig[0].xAxis.readingId
      //     && dp.metaData.parentIds[0] === this.reportConfig.options.regressionConfig[0].xAxis.parentId
      //   )
      //   if(xDataPoint.length !== 0){
      //     aliases.push(xDataPoint[0].aliases.actual)
      //   }
      // }
      // else{

      // }

      dataPoints.forEach(dp => {
        if (
          dp.pointType &&
          dp.pointType !== AnalyticsModelHelper.dataTypes().REGRESSION
        ) {
          // if (!this.dataMap[dp.alias]) {
          this.dataMap[dp.alias] = dp
          if (!dp.isBaseLine || dp.pointType !== 2) {
            aliases.push(dp.alias)
          }
          // }
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

      if (this.reportObject.report.tabularState) {
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
    },

    /**
     * Setting Hot table configuration and column header names
     */
    setHotConfig() {
      let hotColHeaders = []
      let hotColWidths = []
      let hotColumns = []

      let allDataPoints = AnalyticsModelHelper.getAllDataPoints(
        this.reportConfig.options
      )
      let xDataPoint = allDataPoints.find(dp => dp.axes === 'x')
      let getHotType = dataType => {
        return dataType === 2 || dataType === 3 ? 'numeric' : 'text'
      }
      let label
      let type = 'text'
      if (
        this.reportObject.report &&
        this.reportObject.report.reportState &&
        this.reportObject.report.reportState.reportResourceAliases &&
        this.reportObject.report.reportState.reportResourceAliases.X ===
          'mvproject'
      ) {
        label = 'M&Vs'
      } else if (this.reportObject.mode === 6) {
        label = 'Site'
      } else if (this.reportObject.mode === 7) {
        label = 'Building'
      } else if (this.reportObject.mode === 8) {
        label = 'Asset'
      } else if (this.reportObject.mode === 2) {
        label = 'Asset'
      } else {
        //  else if (xDataPoint && !this.reportConfig.options.regressionConfig) {
        //   label = xDataPoint.label
        //   type = getHotType(xDataPoint.dataTypeId)
        // }
        let xAxis = this.reportObject.report.dataPoints[0].xAxis
        label = xAxis.label
        type = getHotType(xAxis.dataType)
      }
      hotColHeaders.push(label.toUpperCase())
      let xColumn = this.tableState.columns[0]
      hotColumns.push({
        type: type,
        name: xColumn.alias,
        columnSpan: 0,
      })
      if (xColumn.width) {
        hotColWidths.push(xColumn.width)
      }

      let noOfColumns = this.tableState.columns.length
      for (let i = 1; i < noOfColumns; i++) {
        let column = this.tableState.columns[i]
        let dataPoint = this.dataMap[column.alias]
        if (column.displayName) {
          hotColHeaders.push(column.displayName.toUpperCase())
        } else {
          let name =
            (dataPoint.duplicateDataPoint === true &&
            this.reportObject &&
            ((this.reportObject.report &&
              this.reportObject.report.xAggr !== 0) ||
              ![1, 4, 'reading'].includes(this.reportObject.mode))
              ? this.aggrMap[dataPoint.aggr] + ' OF '
              : '') +
            dataPoint.label.toUpperCase() +
            (dataPoint.unitStr ? ' (' + dataPoint.unitStr + ')' : '')
          hotColHeaders.push(name)
        }

        let columnSpan = 0
        let subColumnHeaders = []
        let columnSpanDp = this.reportConfig.options.dataPoints.filter(
          dp => dp.alias === column.alias
        )

        if (
          columnSpanDp.length !== 0 &&
          columnSpanDp[0].type === 'rangeGroup'
        ) {
          columnSpan = dataPoint.children.length
        } else if (this.reportConfig.options.isGroupedByTime) {
          let keys = Object.keys(this.reportConfig.data)
          subColumnHeaders = keys.filter(
            key => key !== 'x' && key.split('_')[0] === column.alias
          )
          subColumnHeaders = subColumnHeaders.map(header => {
            return header.split('_')[1]
          })
          columnSpan = subColumnHeaders.length
        } else {
          columnSpan = 0
        }

        let type = null
        if (
          !dataPoint.dataTypeId &&
          columnSpanDp.length !== 0 &&
          columnSpanDp[0].type === 'rangeGroup'
        ) {
          type = 'array'
        } else {
          type = getHotType(dataPoint.dataTypeId)
        }

        let col = {
          type: type,
          name: dataPoint.alias,
          columnSpan: columnSpan,
        }

        if (col.columnSpan !== 0) {
          if (subColumnHeaders.length > 0) {
            col['subColumnHeaders'] = subColumnHeaders
          } else {
            col['subColumnHeaders'] = ['min', 'avg', 'max']
          }
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
        if (this.reportObject.mode === 11 && dataPoint.dataTypeId === 6) {
          hotColHeaders[i] = 'Duration'
        }
        hotColumns.push(col)
        if (column.width) {
          hotColWidths.push(column.width)
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

    setTableData() {
      let tableData = []
      let xColumn = this.tableState.columns[0]
      let xDataType = null
      let xDataPoint = AnalyticsModelHelper.getAllDataPoints(
        this.reportConfig.options
      ).find(dP => dP.axes === 'x')
      // if (xDataPoint) {
      //   xDataType = xDataPoint.dataTypeId
      // } else {
      xDataType = this.reportObject.report.dataPoints[0].xAxis.dataType
      // }

      let enumMap = this.reportObject.report.dataPoints[0].xAxis.enumMap
      let isTimeSeries = xDataType === 5 || xDataType === 6

      let period = this.getTimePeriod(this.reportObject)
      let data = null
      data = this.reportObject.reportData.data
      if (!this.reportConfig.options.isGroupedByTime) {
        data.forEach(row => {
          let newRow = []
          let xVal
          let index = data.indexOf(row)
          if (this.reportConfig.options.regressionConfig) {
            // xVal = row['x']
            if (this.reportConfig.options.timeValues && period) {
              // xVal = xVal  + ' (' + this.formatDate(this.reportConfig.options.timeValues[index], period) + ')'
              xVal = this.formatDate(
                this.reportConfig.options.timeValues[index],
                period
              )
            } else {
              xVal = this.reportConfig.options.timeValues[index]
            }
          } else {
            if (this.reportObject.mode === 2) {
              xVal = this.assets[row[xColumn.alias]]
            } else {
              xVal = row[xColumn.alias]
            }
          }
          if (isTimeSeries) {
            xVal = this.formatDate(xVal, period)
          }
          if (this.reportObject.mode === 11) {
            xVal = enumMap[xVal]
          }
          /* else if (xDataType === 2 || xDataType === 3) {
          xVal = d3.format(',')(xVal)
        } */
          newRow.push(xVal)
          for (let i = 1; i < this.tableState.columns.length; i++) {
            let column = this.tableState.columns[i]
            let value = null
            if (this.dataMap[column.alias].type === 'rangeGroup') {
              let range = ['min', 'avg', 'max']
              value = []
              for (let r of range) {
                value.push(row[column.alias + '.' + r])
              }
            } else {
              value = row[column.alias]
            }
            if (value && !this.reportConfig.options.isGroupedByTime) {
              let dataPoint = this.dataMap[column.alias]
              if (dataPoint.type === 'rangeGroup') {
                value.forEach(val => d3.format(',')(val))
              } else if (
                dataPoint.dataTypeId === 2 ||
                dataPoint.dataTypeId === 3
              ) {
                value =
                  this.tableConfig &&
                  this.tableConfig.removeNegativeValue &&
                  value < 0
                    ? 0
                    : d3.format(',')(value)
              } else if (
                dataPoint.dataTypeId === 4 ||
                dataPoint.dataTypeId === 8
              ) {
                let enumVal = value
                value = null
                if (!column.enumMode) {
                  if (enumVal.timeline && enumVal.timeline[0]) {
                    value = dataPoint.enumMap[enumVal.timeline[0].value]
                    if (enumVal.timeline[1]) {
                      value = dataPoint.enumMap[enumVal.timeline[1].value]
                    }
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
                          formatDuration(
                            enumVal.duration[key],
                            'milliseconds',
                            null,
                            7
                          ) +
                          '<br/>'
                      })
                    value = duration
                  }
                  /* value = dataPoint.enumMap[0] + ': ' + d3.format(',')(value.duration[0]) + '<br/>' +
                  dataPoint.enumMap[1] + ': ' + d3.format(',')(value.duration[1]) */
                }
              } else if (dataPoint.dataTypeId === 5) {
                value = this.formatDate(value, true)
              } else if (dataPoint.dataTypeId === 6) {
                value = this.formatDate(value)
              } else if (this.reportObject.mode === 11) {
                value = NumberFormatHelper.formatTime(value)
              }
            }
            newRow.push(value)
          }
          tableData.push(newRow)
        })
      } else if (this.reportConfig.options.isGroupedByTime) {
        let timeMap = {}
        data.forEach(row => {
          let xVal
          let index = data.indexOf(row)
          if (this.reportConfig.options.regressionConfig) {
            if (this.reportConfig.options.timeValues && period) {
              xVal = this.formatDate(
                this.reportConfig.options.timeValues[index],
                period
              )
            } else {
              xVal = this.reportConfig.options.timeValues[index]
            }
          } else {
            if (this.reportObject.mode === 2) {
              xVal = this.assets[row[xColumn.alias]]
            } else {
              xVal = row[xColumn.alias]
            }
          }
          if (isTimeSeries) {
            xVal = this.formatDate(xVal, period)
          }
          if (this.reportObject.mode === 11) {
            xVal = enumMap[xVal]
          }
          if (!timeMap[xVal]) {
            timeMap[xVal] = {}
          }
          let groupedData = row.group
          let groupByTimeAggr = parseInt(
            this.reportObject.report.reportState.groupByTimeAggr
          )
          if (groupedData) {
            for (let gData of groupedData) {
              if (gData[xColumn.alias]) {
                let period = this.getPeriodFromAggr(groupByTimeAggr)
                let formattedTime = this.convertToDateFormat(
                  new Date(gData[xColumn.alias]),
                  period
                )
                if (['hourofday', 'dayofmonth'].includes(period)) {
                  formattedTime = this.convertToTooltipFormat(
                    new Date(gData[xColumn.alias]),
                    period
                  )
                }
                for (let column of this.tableState.columns) {
                  if (column.alias !== xColumn.alias) {
                    timeMap[xVal][column.alias + '_' + formattedTime] = gData[
                      column.alias
                    ]
                      ? gData[column.alias]
                      : null
                  }
                }
              }
            }
          }
        })
        let keys = Object.keys(this.reportConfig.data)
        for (let xVal of Object.keys(timeMap)) {
          let newRow = []
          newRow.push(xVal)
          for (let column of this.tableState.columns) {
            if (column.alias !== xColumn.alias) {
              let value = []
              let subColumnHeaders = keys
                .filter(
                  key => key !== 'x' && key.split('_')[0] === column.alias
                )
                .map(header => {
                  return header.split('_')[1]
                })
              for (let key of subColumnHeaders) {
                if (timeMap[xVal][column.alias + '_' + key]) {
                  value.push(timeMap[xVal][column.alias + '_' + key])
                } else {
                  value.push(null)
                }
              }
              newRow.push(value)
            }
          }
          tableData.push(newRow)
        }
      }
      if (isTimeSeries) {
        if ([17, 18, 19].includes(this.reportObject.xAggr)) {
          tableData = this.getSortedData(this.reportObject.xAggr, tableData)
        }
      }
      this.hotSettings.data = tableData
    },

    /**
     *   Splitting table for print view
     */
    setMultipleTables() {
      this.multiTables = []
      this.maxColumn = this.isPrinting
        ? MAX_COLUMN
        : this.hotSettings.colHeaders.length
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

    convertToDateFormat(date, period) {
      let dateFormat = this.getDateFormat(period)
      return this.$options.filters.toDateFormat(
        new Date(date),
        dateFormat.format
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

    getSortedData(xAggr, data) {
      let referenceData = []
      let temp = []
      if (parseInt(xAggr) === 17) {
        referenceData = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
      } else if (parseInt(xAggr) === 18) {
        referenceData = [
          '1st',
          '2nd',
          '3rd',
          '4th',
          '5th',
          '6th',
          '7th',
          '8th',
          '9th',
          '10th',
          '11th',
          '12th',
          '13th',
          '14th',
          '15th',
          '16th',
          '17th',
          '18th',
          '19th',
          '20th',
          '21st',
          '22nd',
          '23rd',
          '24th',
          '25th',
          '26th',
          '27th',
          '28th',
          '29th',
          '30th',
          '31st',
        ]
      } else if (parseInt(xAggr) === 19) {
        referenceData = [
          '12 am',
          '1 am',
          '2 am',
          '3 am',
          '4 am',
          '5 am',
          '6 am',
          '7 am',
          '8 am',
          '9 am',
          '10 am',
          '11 am',
          '12 pm',
          '1 pm',
          '2 pm',
          '3 pm',
          '4 pm',
          '5 pm',
          '6 pm',
          '7 pm',
          '8 pm',
          '9 pm',
          '10 pm',
          '11 pm',
        ]
      }
      for (let rdata of referenceData) {
        for (let record of data) {
          if (record[0] === rdata) {
            temp.push(record)
          }
        }
      }
      return temp
    },
  },
}
</script>
<style>
.tabular-inner-table .tabular-inner-table-th {
  border-left: 0 !important;
  border-bottom: 0 !important;
  width: 33.3%;
}
.tabular-inner-table .tabular-inner-table-th:last-child {
  border-right: 0 !important;
}
.tabular-report {
  width: auto !important;
}

.tabular-report thead th {
  /* line-height: 2 !important; */
  /* max-width: 150px; */
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
  /* overflow: hidden; */
  /* height: 100vh; */
  /* position: absolute; */
  /* box-shadow: 0 4px 10px 0 hsla(0,0%,70%,.18); */
  border: 1px solid rgb(230, 235, 240);
  /* padding-bottom: 30px; */
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
/* .tabular-data-border{
  border: 1px solid rgb(230, 235, 240) !important;
} */
.tabular-report-table {
  border-collapse: collapse;
  border: 1px solid rgb(236, 236, 236);
}
/* .tabular-report-table .tabular-data-td:last-child, .tabular-report-table th:last-child{
  border-border-bottom: none !important;
} */
.f-tabular-page .tabular-report-table th {
  background-color: #ffffff !important;
  color: #879eb5 !important;
  font-size: 11px !important;
  font-weight: 500 !important;
  letter-spacing: 1px !important;
  /* text-transform: uppercase !important; */
  padding: 0 10px !important;
  text-align: center !important;
  border: 1px solid rgb(230, 235, 240) !important;
  white-space: normal !important;
  height: 96px;
}

.tabular-report-table th.extra-th {
  border-bottom: 0 !important;
}
.tabular-report-table .tabular-data-td {
  max-width: 120px !important;
  font-size: 12px;
  border: 1px solid rgb(230, 235, 240) !important;
  color: #333 !important;
  padding: 13px 10px !important;
  word-break: break-word;
  /* font-size: 1vw; */
}

.tabular-report-table .extra-td {
  border-top: 0 !important;
  width: 100%;
}
@media print {
  * {
    background: #ffffff;
  }
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
    page-break-before: always;
    page-break-after: always;
    margin-right: 0;
    padding-right: 0;
  }
  table {
    page-break-after: auto;
    page-break-inside: auto;
    border: 1px solid rgb(230, 235, 240) !important;
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
    height: 100%;
  }
  .scroll-y {
    overflow-y: scroll;
  }
  /* .tabular-report-table th, .tabular-report-table td{
border: 1px solid rgb(230, 235, 240) !important;
}
.tabular-report-table th, .tabular-report-table td{
  border-right: 1px solid rgb(230, 235, 240) !important;
} */
  /* .tabular-data-border{
  border: none !important;
} */
}
</style>
<style>
@import '../../../../node_modules/handsontable/dist/handsontable.full.min.css';
</style>
