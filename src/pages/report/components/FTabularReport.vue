<template>
  <div>
    <HotTable
      class="f-tabular-report-hot"
      :style="styleCss"
      v-if="reportObject"
      ref="hotTable"
      :root="root"
      :settings="hotSettings"
    ></HotTable>
  </div>
</template>
<script>
import formatter from 'charts/helpers/formatter'
import HotTable from '@handsontable/vue'
import * as d3 from 'd3'
export default {
  components: {
    HotTable,
  },
  props: ['reportObject', 'config'],
  data() {
    return {
      root: 'tabular-report-hot',
      hotTableWidth: 1000,
      hotSettings: {
        data: [],
        columns: [],
        colHeaders: true,
        rowHeaders: true,
        readOnly: true,
        columnSorting: true,
        manualColumnResize: true,
        manualColumnMove: true,
        fixedColumnsLeft: 1,
      },
    }
  },
  mounted() {
    this.setHotTableWidth()
    this.init()
    this.render()

    let self = this
    window.addEventListener('resize', function() {
      self.setHotTableWidth()
    })
  },
  computed: {
    styleCss() {
      if (this.hotTableWidth) {
        return 'width: ' + this.hotTableWidth + 'px !important;'
      }
      return 'width: 1000px !important;'
    },
  },
  watch: {
    reportObject: {
      handler(newData, oldData) {
        this.render()
      },
      deep: true,
    },
  },
  methods: {
    init() {
      let self = this

      this.hotSettings.afterColumnMove = function(from, to) {
        self.saveCurrentState()
      }

      this.hotSettings.afterColumnResize = function(col, width) {
        self.saveCurrentState()
      }
    },
    setHotTableWidth() {
      this.hotTableWidth = parseInt(d3.select(this.$el).style('width'))
    },
    render() {
      let colHeaders = []
      let colWidth = []
      let columns = []
      let validWidth = true
      for (let column of this.reportObject.reportColumns) {
        if (column.report.id === this.reportObject.reportContext.id) {
          column.isMainReport = true
        }
        if (column.report.id === 525) {
          continue
        }

        if (column.isMainReport) {
          colHeaders.push(column.report.xAxisLabel)

          columns.push({
            type: 'text',
          })
        } else {
          colHeaders.push(column.report.name)

          columns.push({
            type: 'numeric',
          })
        }

        colWidth.push(column.width)
        if (column.width <= 0) {
          validWidth = false
        }
      }
      this.hotSettings.columns = columns
      this.hotSettings.colHeaders = colHeaders
      if (validWidth) {
        this.hotSettings.colWidths = null
        this.hotSettings.colWidths = colWidth
      }

      let formattedData = []
      for (let row of this.reportObject.reportData) {
        let newRow = []
        for (let i = 0; i < row.length; i++) {
          let column = this.reportObject.reportColumns[i]
          if (!column) {
            break
          }

          if (row[i] === null || row[i] === 'null') {
            newRow.push('')
          } else {
            let field = column.isMainReport
              ? column.report.xAxisField
              : column.report.y1AxisField
            let operator = column.isMainReport
              ? column.report.XAxisAggregateOpperator
              : column.report.y1AxisAggregateOpperator
            let axisColumn = {
              datatype:
                field && field.field.dataTypeEnum
                  ? field.field.dataTypeEnum._name.toLowerCase()
                  : 'string',
              operation: operator ? operator._name.toLowerCase() : '',
            }

            newRow.push(formatter.formatValue(row[i], axisColumn))
          }
        }
        formattedData.push(newRow)
      }
      this.hotSettings.data = formattedData
    },
    saveCurrentState() {
      let self = this
      let colHeaders = self.hotSettings.colHeaders

      let modifiedColumns = []
      let colIdMap = {}
      for (let i = 0; i < colHeaders.length; i++) {
        let colHeader = self.$refs['hotTable'].table.getColHeader(i)
        let colWidth = self.$refs['hotTable'].table.getColWidth(i)

        let reportColumn = this.reportObject.reportColumns.find(
          rc =>
            rc.report.name === colHeader || rc.report.xAxisLabel === colHeader
        )
        if (colIdMap[reportColumn.id + '']) {
          continue
        } else {
          colIdMap[reportColumn.id + ''] = true
        }

        let modifiedColumn = {
          id: reportColumn.id,
          width: colWidth,
        }
        modifiedColumns.push(modifiedColumn)
      }

      self.$http
        .post('/dashboard/updateTabularSequence', {
          reportColumns: modifiedColumns,
        })
        .then(function(response) {})
    },
  },
}
</script>
<style>
@import '../../../../node_modules/handsontable/dist/handsontable.full.min.css';
</style>
<style lang="scss">
.f-tabular-report-hot {
  .tabular-report {
    width: auto !important;
  }

  .tabular-report thead th {
    line-height: 2 !important;
    max-width: 150px;
    white-space: inherit !important;
  }

  .wtHolder tr td,
  .ht_master tr td {
    padding: 10px;
    color: #333 !important;
  }

  .wtHolder tr td,
  .ht_master tr td {
    padding: 10px;
    color: #333 !important;
  }

  #tabular-report-hot {
    overflow: hidden;
    height: 70vh;
    position: absolute;
    box-shadow: 0 4px 10px 0 hsla(0, 0%, 70%, 0.18);
    border: 1px solid rgb(230, 235, 240);
  }

  .handsontable tbody th.ht__highlight,
  .handsontable thead th.ht__highlight {
    background-color: #f1f3f5;
  }

  .handsontable thead th {
    padding: 10px !important;
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
} //applying scoping for HOT
</style>
