<template>
  <div>
    <div class="table-header" v-if="report">
      <div class="pull-left">{{ $t('common.wo_report.detailed_report') }}</div>
      <div class="pull-right"></div>
    </div>
    <div class="fc-underlyingdata" v-if="report">
      <div v-if="!report.data || !report.data.length" class="nounderlinedata">
        <table class="fc-list-view-table fc-chart-table">
          <thead>
            <tr>
              <th class="text-left uppercase fc-chart-table-header">
                {{ report.options.xaxis.title }}
              </th>
              <th
                class="text-left uppercase fc-chart-table-header"
                v-if="!legends || legends.length <= 1"
              >
                {{ report.options.y1axis.title }}
              </th>
              <th
                class="text-left uppercase fc-chart-table-header"
                v-else
                v-for="(legend, idx) in legends"
                :key="idx"
              >
                {{ legend }}
              </th>
            </tr>
          </thead>
        </table>
        <div class="content uppercase">
          {{ $t('common.wo_report.no_data') }}
        </div>
      </div>
      <div
        class="row fc-border-1"
        style="overflow-x: scroll;border: solid 1px #e6ebf0;"
        v-else
      >
        <table class="fc-list-view-table fc-chart-table" v-if="!report.multi">
          <thead>
            <tr>
              <th class="text-left uppercase fc-chart-table-header">
                <div class="f-tabler-report-title">
                  {{ report.options.xaxis.title }}
                </div>
              </th>
              <th
                class="text-left uppercase fc-chart-table-header"
                v-if="!legends || legends.length <= 1"
              >
                <div class="f-tabler-report-title">
                  {{ report.options.y1axis.title }}
                  <span
                    v-if="report.options.y1axis.unit"
                    v-html="getUnit(report.options.y1axis.unit)"
                  ></span>
                </div>
              </th>
              <template v-else>
                <th
                  class="text-left uppercase fc-chart-table-header"
                  v-for="(legend, idx) in legends"
                  :key="idx"
                >
                  <div class="f-tabler-report-title">
                    {{ legend }}
                    <span
                      v-if="report.options.y1axis.unit"
                      v-html="getUnit(report.options.y1axis.unit)"
                    ></span>
                  </div>
                </th>
                <th
                  class="text-left uppercase fc-chart-table-header"
                  v-if="report.options.type === 'stackedbar'"
                >
                  <div class="f-tabler-report-title">
                    Total
                    <span
                      v-if="report.options.y1axis.unit"
                      v-html="getUnit(report.options.y1axis.unit)"
                    ></span>
                  </div>
                </th>
              </template>
            </tr>
          </thead>
          <tbody>
            <tr
              class="tablerow"
              v-for="(row, index) in report.data[0].data"
              :key="index"
            >
              <td class="fc-chart-table-body">
                <span class="f-tabler-report-content">{{
                  row.label | formatValue(report.options.xaxis)
                }}</span>
              </td>
              <td
                class="fc-chart-table-body"
                v-if="
                  (!legends || legends.length <= 1) && !Array.isArray(row.value)
                "
              >
                <span
                  class="f-tabler-report-content"
                  v-html="
                    valueformt(row.value, report.options.y1axis, true) !== 'NaN'
                      ? valueformt(row.value, report.options.y1axis, true)
                      : '---'
                  "
                ></span>
              </td>
              <template v-else>
                <td
                  class="fc-chart-table-body"
                  v-for="(legend, idx) in legends"
                  :key="idx"
                >
                  <span
                    class="f-tabler-report-content"
                    v-if="getSubValue(row.value, idx, legend)"
                    >{{
                      getSubValue(row.value, idx, legend)
                        | formatValue1(report.options.y1axis)
                    }}</span
                  >
                  <span class="f-tabler-report-content" v-else>---</span>
                </td>
                <td
                  class="fc-chart-table-body"
                  v-if="report.options.type === 'stackedbar'"
                >
                  <span class="f-tabler-report-content">{{
                    row.value.map(function(d) {
                      return d.value
                    }) | sum
                  }}</span>
                </td>
              </template>
            </tr>
          </tbody>
        </table>
        <table class="fc-list-view-table fc-chart-table" v-else>
          <thead>
            <tr>
              <th
                class="text-left uppercase fc-chart-table-header"
                v-for="(header, index) in groupedData.headings"
                :key="index"
              >
                <div
                  class="f-tabler-report-title"
                  v-html="
                    header === 'label' ? report.options.xaxis.title : header
                  "
                ></div>
              </th>
            </tr>
          </thead>
          <tbody v-if="report.options.type === 'boolean'">
            <tr
              class="tablerow"
              v-for="(row, index) in groupedData.data"
              :key="index"
            >
              <td
                class="fc-chart-table-body"
                v-for="(header, index) in groupedData.headings"
                :key="index"
              >
                <span class="f-tabler-report-content">{{
                  row[header] | getFormatedValue()
                }}</span>
              </td>
            </tr>
          </tbody>
          <tbody v-else>
            <tr
              class="tablerow"
              v-for="(row, index) in groupedData.data"
              :key="index"
            >
              <td
                class="fc-chart-table-body"
                v-for="(header, index) in groupedData.headings"
                :key="index"
              >
                <span class="f-tabler-report-content">{{
                  row[header] || row[header] === 0 ? row[header] : '---'
                }}</span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>
<script>
import * as d3 from 'd3'
import formatter from 'charts/helpers/formatter'
import common from 'charts/helpers/common'
export default {
  props: ['data', 'options', 'multi'],
  data() {
    return {
      report: null,
      groupedData: null,
    }
  },
  watch: {
    data: {
      handler: function(newVal, oldVal) {
        if (this.data) {
          let reportObj = {
            data: this.data,
            options: this.options,
            multi: this.multi,
          }
          this.initData(reportObj)
        }
      },
      deep: true,
    },
  },
  created() {
    this.$store.dispatch('loadGroups')
  },
  mounted() {
    if (this.data) {
      let reportObj = {
        data: this.data,
        options: this.options,
        multi: this.multi,
      }
      this.initData(reportObj)
    }
  },
  computed: {
    legends() {
      if (this.report && common.isValueArray(this.report.data)) {
        return common.getGroup(this.report.data)
      } else if (this.report && common.isValueArray(this.report.data[0].data)) {
        return common.getGroup(this.report.data[0].data)
      }
      return []
    },
    tableStyle() {
      if (this.report && this.report.type === 'tabular') {
        return {
          width: 'auto',
        }
      }
      return {}
    },
  },
  filters: {
    formatValue(value, axis) {
      return formatter.formatValue(value, axis)
    },
    getFormatedValue(value) {
      if (
        value === 0 ||
        value === '0' ||
        value === false ||
        value === 'false'
      ) {
        return 'OFF'
      } else if (
        value === 1 ||
        value === '1' ||
        value === true ||
        value === 'true'
      ) {
        return 'ON'
      } else {
        return value
      }
    },
    formatValue1(value, axis) {
      return formatter.formatValueForTable(value, axis)
    },
    sum(array) {
      return d3.format('.2f')(d3.sum(array))
    },
  },
  methods: {
    initData(reportObj) {
      console.log('init tabular data callled... ', reportObj)
      this.report = null
      if (reportObj && reportObj.data.length) {
        this.report = reportObj
        if (this.report.options.id === 1015) {
          this.groupData1()
        } else {
          this.groupData()
        }
      }
    },
    getUnit(unit) {
      let unitLabel = unit
      if (unit.toLowerCase() === 'cost') {
        unitLabel = this.$currency.toUpperCase()
      } else if (unit.toLowerCase() === 'eui') {
        unitLabel =
          this.$account && this.$account.org.id === 78
            ? 'kWh/m&sup2'
            : 'kWh/ft&sup2'
      }
      return '(' + unitLabel + ')'
    },
    getSubValue(subList, index, legend) {
      if (subList && subList.length) {
        let l = subList.find(sl => sl.label === legend)
        if (l) {
          return l.value
        } else {
          return subList[index].value
        }
      }
      return null
    },
    valueformt(value, axis, table) {
      if (table) {
        return formatter.formatValueForTable(value, axis)
      }
      return formatter.formatValue(value, axis)
    },
    groupData() {
      if (this.report && this.report.multi) {
        // let groupedData = []
        let headings = []
        let data = []
        // let indexMapping = {}
        let mapping = {}
        let dataOrder = []
        let dataTimeOrder = []
        let dateVsData = {}
        headings.push('label')
        let mainAxis = this.report.data[0].options.xaxis
        let tabularData = this.report.tabledata
          ? this.report.tabledata
          : this.report.data
        for (let i = 0; i < tabularData.length; i++) {
          let d = tabularData[i]
          let serieTitle = d.title
          if (d.options.y1axis.unit) {
            serieTitle = d.title + ' ' + this.getUnit(d.options.y1axis.unit)
          }
          headings.push(serieTitle)

          for (let v of d.data) {
            // let formatedLabel = moment(v.label).tz(this.$timezone).format('MMM DD, YYYY HH')
            let formatedLabel = formatter.formatValue(v.label, mainAxis)

            if (dataOrder.indexOf(formatedLabel) === -1) {
              dataOrder.push(formatedLabel)
              dataTimeOrder.push(v.label)
              dateVsData[v.label] = formatedLabel
            }
            if (!mapping[formatedLabel]) {
              mapping[formatedLabel] = {}
            }
            mapping[formatedLabel][serieTitle] = formatter.formatValueForTable(
              v.value,
              d.options.y1axis
            )

            // let idx = indexMapping[formatedLabel]
            // console.log('################## idx : ' + idx + '  #  ' + formatedLabel)
            // if (!idx) {
            //   idx = data.length
            //   indexMapping[formatedLabel] = idx
            // }

            // if (!data[idx]) {
            //   data.push({
            //     label: formatedLabel
            //   })
            // }
            // let obj = data[idx]
            // obj[d.title] = v.formatted_value
          }
        }
        dataTimeOrder.sort(function(a, b) {
          return a - b
        })
        for (let dt of dataTimeOrder) {
          let f = dateVsData[dt]
          let d = mapping[f]
          d.label = f
          data.push(d)
        }
        this.groupedData = {
          headings: headings,
          data: data,
        }
      }
    },
    groupData1() {
      if (this.report && this.report.multi) {
        console.log('######### groupData1::: ', this.report)
        let headings = []
        headings.push('label')
        let sublabels = common.getGroup(this.report.data[0].data)
        for (let sb of sublabels) {
          headings.push(sb)
        }
        headings.push(this.report.data[1].title)

        let mapping = {}
        let dataOrder = []
        let dataTimeOrder = []
        let dateVsData = {}

        for (let v of this.report.data[0].data) {
          let formatedLabel = formatter.formatValue(
            v.label,
            this.report.data[0].options.xaxis
          )

          if (dataOrder.indexOf(formatedLabel) === -1) {
            dataOrder.push(formatedLabel)
            dataTimeOrder.push(v.label)
            dateVsData[v.label] = formatedLabel
          }
          if (!mapping[formatedLabel]) {
            mapping[formatedLabel] = {}
          }

          for (let r of v.value) {
            mapping[formatedLabel][r.label] = formatter.formatValueForTable(
              r.value,
              this.report.data[0].options.y1axis
            )
          }
        }

        for (let v of this.report.data[1].data) {
          let formatedLabel = formatter.formatValue(
            v.label,
            this.report.data[1].options.xaxis
          )

          if (dataOrder.indexOf(formatedLabel) === -1) {
            dataOrder.push(formatedLabel)
            dataTimeOrder.push(v.label)
            dateVsData[v.label] = formatedLabel
          }
          if (!mapping[formatedLabel]) {
            mapping[formatedLabel] = {}
          }
          mapping[formatedLabel][
            this.report.data[1].title
          ] = formatter.formatValueForTable(
            v.value,
            this.report.data[1].options.y1axis
              ? this.report.data[1].options.y1axis
              : this.report.data[1].options.y2axis
          )
        }
        dataTimeOrder.sort(function(a, b) {
          return a - b
        })

        let data = []
        for (let dt of dataTimeOrder) {
          let f = dateVsData[dt]
          let d = mapping[f]
          d.label = f
          data.push(d)
        }

        this.groupedData = {
          headings: headings,
          data: data,
        }
      }
    },
  },
}
</script>
<style>
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

.handsontable tbody th.ht__highlight,
.handsontable thead th.ht__highlight {
  background-color: #f1f3f5;
}

.handsontable thead th {
  padding: 10px !important;
}

.handsontable th {
  background-color: #f1f3f5;
  color: #717b85;
}

.handsontable th,
.handsontable td {
  border-right: 1px solid #f0f0f0 !important;
  border-bottom: 1px solid #f0f0f0 !important;
}
.f-tabler-report-title {
  min-width: 180px;
  white-space: pre-line;
}
.f-tabler-report-content {
  white-space: nowrap;
}
.analytic-summary .analystics-layout .fchart-section {
  height: 80vh !important;
}
</style>
