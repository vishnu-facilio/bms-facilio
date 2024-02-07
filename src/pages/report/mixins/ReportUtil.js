import moment from 'moment-timezone'
import formatter from 'charts/helpers/formatter'
import DateHelper from '@/mixins/DateHelper'
import colors from 'charts/helpers/colors'
import { API} from '@facilio/api'
import { mapGetters } from 'vuex'
import { getFieldOptions } from 'util/picklist'

export default {
  mixins: [DateHelper],
  data() {
    return {
      assets: [],
      folderState: {},
    }
  },
  created() {
    this.loadAssetPickListData()
    this.$store.dispatch('loadTicketCategory')
    this.$store.dispatch('loadTicketPriority')
    this.$store.dispatch('loadTicketStatus', 'workorder')
    this.$store.dispatch('loadTicketType')
    this.$store.dispatch('loadAssetCategory')
    this.$store.dispatch('loadAssetDepartment')
    this.$store.dispatch('loadAssetType')
    this.$store.dispatch('loadAlarmSeverity')
    this.$store.dispatch('loadServiceList')
    this.$store.dispatch('loadGroups')
    this.$store.dispatch('loadBuildings')
  },
  computed: {
    ...mapGetters(['getTicketPriority', 'getTicketCategory']),
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
    async deleteReport(id) {
      let promptObj = {
        title: 'Delete Report',
        message: 'Are you sure you want to delete this Report?',
        rbDanger: true,
        rbLabel: 'Delete',
      }
      let deleteConfirmation = await this.$dialog.confirm(promptObj)
      if (deleteConfirmation) {
        let { data, error } = await API.delete(`/v3/report/delete`, {
          reportId: id,
        })
        if (error) {
          this.$message.error(
            'Failed to delete report, please try again.',
            error
          )
        } else {
          if (data && data.errorString) {
            let confirmObj = {
              title: 'Delete Report',
              message:
                'This report is associated to a dashboard widget. Deleting this report will remove the widget from the dashboard. Are you sure you want to continue?',
              rbLabel: 'Yes, delete',
              lbLabel: 'No, cancel',
            }
            let widgetDeleteConfirm = await this.$dialog.confirm(confirmObj)
            if (widgetDeleteConfirm) {
              let resp = await API.delete('/v3/report/delete', {
                reportId: id,
                deleteWithWidget: true,
              })
              if (resp.error) {
                this.$message.error(
                  'Failed to delete report, please try again.',
                  error
                )
              } else {
                this.$emit('reportDeleted', { type: 'new', reportId: id })
                this.$message.success('Report deleted successfully')
              }
            }
          } else {
            this.$emit('reportDeleted', { type: 'new', reportId: id })
            this.$message.success('Report deleted successfully')
          }
        }
        this.handleDelete(id)
      }
    },
    handleDelete(id) {
      this.reportlist.forEach((report, index) => {
        if (report.id === id) {
          this.reportlist.splice(index, 1)
        }
      })
    },
    prepareDateFilter(reportContext) {
      let dateFilter = null
      if (reportContext.dateFilter) {
        dateFilter = {}
        dateFilter.operatorId = reportContext.dateFilter.operatorId
        if (reportContext.dateFilter.value) {
          dateFilter.value = reportContext.dateFilter.value
        }
        dateFilter.field = reportContext.dateFilter.field
        if (dateFilter.operatorId === 20) {
          dateFilter.dateRange = []
          dateFilter.dateRange.push(
            new Date(reportContext.dateFilter.val.split(',')[0])
          )
          dateFilter.dateRange.push(
            new Date(reportContext.dateFilter.val.split(',')[1])
          )
        }
        if (
          reportContext.dateFilter.startTime &&
          reportContext.dateFilter.endTime
        ) {
          dateFilter.startTime = reportContext.dateFilter.startTime
          dateFilter.endTime = reportContext.dateFilter.endTime
        }
      }
      return dateFilter
    },

    prepareReportOptions(responseData, time, filterName) {
      let reportContext = responseData.reportContext
      let entityName = responseData.entityName
      let variance = responseData.variance
      if (!entityName) {
        entityName = reportContext.name
      }
      let chartOptions = {}
      chartOptions.id = reportContext.id
      chartOptions.name = reportContext.name
      chartOptions.entityName = entityName
      chartOptions.description = reportContext.description
      chartOptions.type = reportContext.reportChartType
        ? reportContext.reportChartType.name
        : 'line'
      chartOptions.is_highres_data = reportContext.isHighResolutionReport
      chartOptions.is_combo =
        reportContext.isCombinationReport || chartOptions.type === 'combo'
      chartOptions.date_range = time
      chartOptions.color = reportContext.reportColor
      if (responseData.heatMapRange) {
        chartOptions.heatMapRange = responseData.heatMapRange
      }
      chartOptions.xaxis = {
        title: reportContext.xAxisLabel ? reportContext.xAxisLabel : '',
        unit: reportContext.xAxisUnit ? reportContext.xAxisUnit : '',
        field: reportContext.xAxisField ? reportContext.xAxisField.field : null,
        datatype:
          reportContext.xAxisField &&
          reportContext.xAxisField.field.dataTypeEnum
            ? reportContext.xAxisField.field.dataTypeEnum._name.toLowerCase()
            : 'string',
        operation: reportContext.XAxisAggregateOpperator
          ? reportContext.XAxisAggregateOpperator._name.toLowerCase()
          : '',
        isTitle: reportContext.xAxisTitleEnable
          ? reportContext.xAxisTitleEnable
          : true,
        is_highres_data: reportContext.isHighResolutionReport,
      }
      if (filterName) {
        chartOptions.xaxis.filterName = filterName
      }

      if (reportContext.y1AxisField) {
        chartOptions.y1axis = {
          title: reportContext.y1AxisLabel ? reportContext.y1AxisLabel : '',
          title1: reportContext.y1AxisField
            ? reportContext.y1AxisField.fieldLabel
            : null,
          unit: reportContext.y1AxisUnit
            ? reportContext.y1AxisUnit
            : reportContext.y1AxisField.field.unit
            ? reportContext.y1AxisField.field.unit === 'AED'
              ? reportContext.y1AxisField.field.unit
              : reportContext.y1AxisField.field.unit.toLowerCase()
            : '',
          field: reportContext.y1AxisField
            ? reportContext.y1AxisField.field
            : null,
          datatype:
            reportContext.y1AxisUnit && reportContext.y1AxisUnit === 'cost'
              ? 'currency'
              : reportContext.y1AxisField &&
                reportContext.y1AxisField.field.dataTypeEnum
              ? reportContext.y1AxisField.field.dataTypeEnum._name.toLowerCase()
              : 'number',
          operation: reportContext.y1AxisAggregateOpperator
            ? reportContext.y1AxisAggregateOpperator._name.toLowerCase()
            : '',
          isTitle: reportContext.yAxisTitleEnable
            ? reportContext.yAxisTitleEnable
            : true,
        }
      }

      if (reportContext.y2AxisField) {
        chartOptions.y2axis = {
          title: reportContext.y2AxisLabel ? reportContext.y2AxisLabel : '',
          unit: reportContext.y2AxisUnit
            ? reportContext.y2AxisUnit
            : reportContext.y2AxisField.field.unit
            ? reportContext.y2AxisField.field.unit.toLowerCase()
            : '',
          field: reportContext.y2AxisField
            ? reportContext.y2AxisField.field
            : null,
          datatype:
            reportContext.y2AxisUnit && reportContext.y2AxisUnit === 'cost'
              ? 'currency'
              : reportContext.y2AxisField &&
                reportContext.y2AxisField.field.dataTypeEnum
              ? reportContext.y2AxisField.field.dataTypeEnum._name.toLowerCase()
              : 'number',
          operation: reportContext.y2AxisAggregateOpperator
            ? reportContext.y2AxisAggregateOpperator._name.toLowerCase()
            : '',
        }
      }
      if (reportContext.y3AxisField) {
        chartOptions.y3axis = {
          title: reportContext.y3AxisLabel ? reportContext.y3AxisLabel : '',
          unit: reportContext.y3AxisField
            ? reportContext.y3AxisUnit
            : reportContext.y3AxisField.field.unit
            ? reportContext.y3AxisField.field.unit.toLowerCase()
            : '',
          field: reportContext.y3AxisField
            ? reportContext.y3AxisField.field
            : null,
          datatype:
            reportContext.y3AxisUnit && reportContext.y3AxisUnit === 'cost'
              ? 'currency'
              : reportContext.y3AxisField &&
                reportContext.y3AxisField.field.dataTypeEnum
              ? reportContext.y3AxisField.field.dataTypeEnum._name.toLowerCase()
              : 'number',
          operation: reportContext.y3AxisAggregateOpperator
            ? reportContext.y3AxisAggregateOpperator._name.toLowerCase()
            : '',
        }
      }
      if (reportContext.groupByField) {
        if (reportContext.groupByField.field !== null) {
          chartOptions.groupby = {
            title: reportContext.groupByLabel ? reportContext.groupByLabel : '',
            unit: reportContext.groupByUnit ? reportContext.groupByUnit : '',
            field: reportContext.groupByField
              ? reportContext.groupByField.field
              : null,
            datatype:
              reportContext.groupByField &&
              reportContext.groupByField.field.dataTypeEnum
                ? reportContext.groupByField.field.dataTypeEnum._name.toLowerCase()
                : 'string',
            operation: reportContext.groupByAggregateOpperator
              ? reportContext.groupByAggregateOpperator._name.toLowerCase()
              : '',
          }
        }
      }
      if (reportContext.isComparisionReport) {
        chartOptions.groupby = {
          title: reportContext.groupByLabel ? reportContext.groupByLabel : '',
          unit: reportContext.groupByUnit ? reportContext.groupByUnit : '',
          datatype:
            reportContext.groupByField &&
            reportContext.groupByField.field.dataTypeEnum
              ? reportContext.groupByField.field.dataTypeEnum._name.toLowerCase()
              : 'string',
          operation: reportContext.groupByAggregateOpperator
            ? reportContext.groupByAggregateOpperator._name.toLowerCase()
            : '',
        }
      }
      if (reportContext.metaJson) {
        chartOptions.metaJson = reportContext.metaJson
      }
      if (reportContext.colorSchema) {
        chartOptions.colorSchema = reportContext.colorSchema
      }
      chartOptions.timeObject = {
        time: time,
        field: this.filterName,
      }

      if (
        (variance && variance.sum) ||
        ([3944, 4067, 4063, 4066, 3940].includes(reportContext.id) &&
          responseData.reportData)
      ) {
        let sum = 0
        if (!variance || !variance.sum) {
          responseData.reportData.forEach(data => {
            sum += Number(data.value)
          })
        } else {
          sum = variance.sum
        }
        chartOptions.innerText = true
        chartOptions.centerText = this.formatValue(sum, chartOptions.y1axis)
        if (chartOptions.y1axis.datatype === 'currency' && sum > 10000) {
          chartOptions.centerValue = this.formatValue(
            parseInt(sum / 1000),
            chartOptions.y1axis,
            'json'
          ).value
          chartOptions.centerValue =
            chartOptions.centerValue.substring(
              0,
              chartOptions.centerValue.indexOf('.')
            ) + 'K'
        } else {
          chartOptions.centerValue = this.formatValue(
            sum,
            chartOptions.y1axis,
            'json'
          ).value
        }

        if (!chartOptions.y1axis.unit) {
          chartOptions.centerText += ' ' + chartOptions.y1axis.title
          chartOptions.centerLabel =
            (chartOptions.y1axis && chartOptions.y1axis.unit !== ''
              ? chartOptions.y1axis.unit
              : chartOptions.y1axis.title) + ''
        } else {
          chartOptions.centerLabel =
            (chartOptions.y1axis && chartOptions.y1axis.unit !== ''
              ? this.formatValue(sum, chartOptions.y1axis, 'json').unit
              : chartOptions.y1axis.title) + ''
        }
        if (chartOptions.innerText === true) {
          chartOptions.arcSliceEnable = false
          chartOptions.arcSliceValueFormat = 'percent'
        }
      }

      if (
        reportContext.baseLineContexts &&
        reportContext.baseLineContexts.length
      ) {
        chartOptions.isBaseLine = true
        chartOptions.baseLineDiff = responseData.baseLineComparisionDiff
        chartOptions.criteria = reportContext.criteria
        chartOptions.baseLineContexts = reportContext.baseLineContexts
      }

      let legendTypes = ['number', 'celsius', 'decimal', 'percentage']
      chartOptions.showWidgetLegends =
        !!variance &&
        legendTypes.includes(chartOptions.y1axis.datatype) &&
        (this.$org.id !== 88 || reportContext.id !== 3992) &&
        (this.$org.id !== 78 ||
          ['portfolio', 'buildingdashboard'].includes(
            this.$route.params.dashboardlink
          ))
      if (chartOptions.showWidgetLegends) {
        chartOptions.variance = variance
        chartOptions.widgetLegends = {}
        let labels = {
          min: 'MIN',
          max: 'MAX',
          sum: 'TOTAL',
          avg: 'AVG',
          eui: 'EUI',
          total_cost: 'COST',
          total_kwh: 'ENERGY',
          yesterday_cost: 'COST',
          yesterday_kwh: 'ENERGY',
        }
        let setWidgetLegends = key => {
          if (
            !variance ||
            !variance.hasOwnProperty(key) ||
            variance[key] === null
          ) {
            return
          }
          let yaxis = this.$helpers.cloneObject(chartOptions.y1axis)
          if (['total_kwh', 'yesterday_kwh'].includes(key)) {
            yaxis.unit = 'kwh'
          }
          let formattedValue = this.formatValue(variance[key], yaxis, 'json')
          let yest = new Date()
          yest.setDate(yest.getDate() - 1)
          let varianceLabel
          if (key === 'yesterday_cost' || key === 'yesterday_kwh') {
            varianceLabel = this.$options.filters.formatDate(yest, true)
          } else if (
            (key === 'total_cost' || key === 'total_kwh') &&
            reportContext.dateFilter
          ) {
            let operator = this.getDateOperatorFromId(
              reportContext.dateFilter.operatorId,
              reportContext.dateFilter.value
            )
            if (operator) {
              varianceLabel = operator.label
            }
          }

          chartOptions.widgetLegends[key] = {
            label: labels[key],
            value: formattedValue.value || 0,
            varianceLabel: varianceLabel || '',
            unit:
              key === 'eui'
                ? formattedValue.unit +
                  (this.$account.org.id === 78
                    ? '/m<sup>2</sup>'
                    : '/ft<sup>2</sup>' +
                      (this.$account.org.id === 88 && reportContext.id === 1012
                        ? '/hr'
                        : ''))
                : formattedValue.unit,
          }
        }
        if (variance.hasOwnProperty('total_cost')) {
          setWidgetLegends('total_cost')
          setWidgetLegends('total_kwh')
          setWidgetLegends('yesterday_cost')
          setWidgetLegends('yesterday_kwh')
        } else {
          if (responseData.reportData && responseData.reportData.length) {
            setWidgetLegends('min')
            setWidgetLegends('max')
            setWidgetLegends('sum')
            if (variance.hasOwnProperty('eui')) {
              setWidgetLegends('eui')
            } else if (chartOptions.type !== 'doughnut') {
              setWidgetLegends('avg')
            }
          }
        }
      }
      if (responseData.safelimit) {
        let safelimit = []
        safelimit = responseData.safelimit
        chartOptions.safelimit = []
        for (let i = 0; i < safelimit.length; i++) {
          if (safelimit[i] !== null) {
            let data = {
              header: 'SafeLimit',
              title:
                chartOptions.y1axis.field &&
                chartOptions.y1axis.field.displayName
                  ? chartOptions.y1axis.field.displayName
                  : '',
              safelimitName: i === 0 ? 'Min' : i === 1 ? 'Max' : '',
              formated_value: this.formatValue(
                safelimit[i],
                chartOptions.y1axis
              ),
              safelimitValue: safelimit[i],
              lineStyle: i === 0 ? 'dashed' : 'line',
              color: chartOptions.color,
              index: 0,
            }
            chartOptions.safelimit.push(data)
          }
        }
      }
      if (responseData.benchmarks) {
        if (!chartOptions.safelimit) {
          chartOptions.safelimit = []
        } else {
          let benchmarks = []
          benchmarks = responseData.benchmarks
          for (let i = 0; i < benchmarks.length; i++) {
            if (benchmarks[i] !== null) {
              let data = {
                header: 'benchmark',
                title: benchmarks[i].name,
                safelimitName: 'Value',
                formated_value: this.formatValue(
                  benchmarks[i].value,
                  chartOptions.y1axis
                ),
                safelimitValue: benchmarks[i].value,
                lineStyle: 'line',
                color: chartOptions.color,
                index: 19,
              }
              chartOptions.safelimit.push(data)
            }
          }
        }
      }
      return chartOptions
    },

    prepareMultiwidgetLegends(report) {
      let dataList = report.data
      if (report.multi && dataList.length !== 1) {
        let unit = report.options.y1axis.unit
          ? report.options.y1axis.unit.toLowerCase()
          : ''
        let aggr = ['currency', 'kwh', 'co2', 'kg', 'mwh'].includes(unit.trim())
          ? 'sum'
          : 'avg'
        if (report.options.isBaseLine && dataList.length <= 2) {
          let total
          let baseLineTotal
          let label
          if (
            report.options.variance &&
            report.options.variance.hasOwnProperty('total_cost')
          ) {
            total = report.options.widgetLegends['total_cost']
            baseLineTotal = report.data[1].options.widgetLegends['total_cost']
            label = 'COST'
          } else {
            total = report.options.widgetLegends[aggr]
            baseLineTotal = report.data[1].options.widgetLegends[aggr]
            label = aggr === 'sum' ? 'TOTAL' : 'AVG'
          }
          if (!total) {
            return
          }
          report.options.widgetLegends = {
            current: {
              value: total.value,
              unit: total.unit,
              type: 'baseLine',
              legendColor: report.options.color,
              numValue: Number(total.value),
              convertedValue:
                total.unit.toLowerCase() === 'mwh'
                  ? Number(total.value) * 1000
                  : Number(total.value),
              label: label,
              index: 0,
              name: report.data[0].options.entityName,
              stack: true,
            },
            baseLine: {
              value: baseLineTotal ? baseLineTotal.value : 0,
              unit: baseLineTotal ? baseLineTotal.unit : total.unit,
              type: 'baseLine',
              baseLineDiff: report.data[1].options.baseLineDiff,
              legendColor: report.data[1].options.color,
              numValue: Number(total.value),
              convertedValue:
                total.unit.toLowerCase() === 'mwh'
                  ? Number(total.value) * 1000
                  : Number(total.value),
              label: label,
              index: 1,
              name: report.data[1].options.entityName,
              stack: true,
            },
          }
        } else {
          let widgetLegends = {}
          if (
            report.options.variance &&
            report.options.variance.hasOwnProperty('total_cost')
          ) {
            dataList.forEach((data, index) => {
              if (!widgetLegends['total_cost']) {
                widgetLegends['total_cost'] =
                  data.options.widgetLegends['total_cost']
                widgetLegends['total_kwh'] =
                  data.options.widgetLegends['total_kwh']
                widgetLegends['yesterday_cost'] =
                  data.options.widgetLegends['yesterday_cost']
                widgetLegends['yesterday_kwh'] =
                  data.options.widgetLegends['yesterday_kwh']
              } else {
                widgetLegends['total_cost'].value = (
                  Number(widgetLegends['total_cost'].value) +
                  Number(data.options.widgetLegends['total_cost'].value)
                ).toFixed(2)
                widgetLegends['yesterday_cost'].value = (
                  Number(widgetLegends['yesterday_cost'].value) +
                  Number(data.options.widgetLegends['yesterday_cost'].value)
                ).toFixed(2)
              }
            })
          } else {
            dataList.forEach((data, index) => {
              if (!data.options.widgetLegends) {
                return
              }
              let total = data.options.widgetLegends[aggr]
              if (total) {
                data.options.widgetLegends = total
                widgetLegends[aggr + '_' + index] = {
                  value: total.value,
                  numValue: Number(total.value),
                  convertedValue:
                    total.unit.toLowerCase() === 'mwh'
                      ? Number(total.value) * 1000
                      : Number(total.value),
                  unit: total.unit,
                  label: aggr === 'sum' ? 'TOTAL' : 'AVG',
                  legendColor: data.options.color,
                  type: aggr,
                  index: index,
                  name: data.options.entityName,
                }
              }
            })
          }
          report.options.widgetLegends = widgetLegends
        }
      }
    },

    setMinMaxVarianceLabel(options, label, value) {
      if (options.variance && options.widgetLegends.min) {
        let variance
        if (value === options.variance.min) {
          variance = 'min'
        } else if (value === options.variance.max) {
          variance = 'max'
        }

        if (variance) {
          let formattedLabel
          if (
            options.is_highres_data &&
            (options.xaxis.datatype === 'date_time' ||
              options.xaxis.datatype === 'date')
          ) {
            formattedLabel = this.$options.filters.toDateFormat(
              label,
              'DD MMM YYYY HH:mm A'
            )
          } else {
            options.widgetLegends[variance].legendColor = options.color
            formattedLabel = this.formatValue(label, options.xaxis)
          }
          options.widgetLegends[variance].varianceLabel = formattedLabel
          if (options.variance.min === options.variance.max) {
            options.widgetLegends['max'].varianceLabel = formattedLabel
          }
        }
      }
    },
    prepareBooleanReportData(
      reportData,
      reportContext,
      reportOptions,
      yAxis,
      diff
    ) {
      let lb = reportData.find(r => r.label === null)
      if (lb) {
        reportData.splice(reportData.indexOf(lb), 1)
      }
      reportData = reportData.filter(rd => !rd.marked)
      let sublabels = []
      if (Array.isArray(reportData)) {
        for (let i = 0; i < reportData.length; i++) {
          let row = reportData[i]
          row.orig_label = row.label
          row.name = reportOptions.entityName
          row.title = reportOptions.entityName
          if (
            diff &&
            reportOptions.xaxis.datatype.indexOf('date') !== -1 &&
            typeof row.label === 'number'
          ) {
            row.label = row.label + diff
          }
          row.orgLabel = row.label
          let operationIdDate
          if (reportContext.dateFilter) {
            operationIdDate = reportContext.dateFilter.operatorId
          }
          row.label = this.formatLabel(
            row.label,
            reportContext.xAxisField,
            reportOptions.xaxis,
            operationIdDate
          )
          if (Array.isArray(row.value)) {
            for (let j = 0; j < row.value.length; j++) {
              let col = row.value[j]
              col.orig_label = col.label
              col.name = reportOptions.entityName
              col.title = reportOptions.name
              col.label = this.formatLabel(
                col.label,
                reportContext.groupByField,
                reportOptions.groupby,
                operationIdDate
              )
              col.formatted_value = this.formatValue(
                col.value,
                reportOptions[yAxis]
              )
              if (sublabels.indexOf(col.label) === -1) {
                sublabels.push(col.label)
              }
            }
          } else {
            this.setMinMaxVarianceLabel(reportOptions, row.label, row.value)
            row.formatted_value = this.formatValue(
              row.value,
              reportOptions[yAxis]
            )
          }
        }
      }
      if (sublabels.length) {
        for (let i = 0; i < reportData.length; i++) {
          let row = reportData[i]

          if (Array.isArray(row.value)) {
            for (let sublabel of sublabels) {
              let sv = row.value.find(v => v.label === sublabel)
              if (!sv) {
                row.value.push({
                  label: sublabel,
                  value: 0,
                })
              }
            }
          }
        }
      }
      return reportData
    },
    prepareReportData(
      reportData,
      reportContext,
      reportOptions,
      yAxis,
      diff,
      call
    ) {
      let lb = reportData.find(r => r.label === null)
      if (lb) {
        reportData.splice(reportData.indexOf(lb), 1)
      }
      reportData = reportData.filter(rd => !rd.marked)
      let sublabels = []
      /* temp fix only */
      let ignoreDip = true
      let reptId = reportContext.id
      if (
        reptId &&
        (reptId === 3990 ||
          reptId === 3991 ||
          reptId === 3992 ||
          reptId === 3993)
      ) {
        ignoreDip = false
      }
      if (Array.isArray(reportData)) {
        for (let i = 0; i < reportData.length; i++) {
          if (!ignoreDip) {
            let row = reportData[i]
            row.orig_label = row.label
            if (
              diff &&
              reportOptions.xaxis.datatype.indexOf('date') !== -1 &&
              typeof row.label === 'number'
            ) {
              row.label = row.label + diff
            }
            row.orgLabel = row.label
            let operationIdDate
            if (reportContext.dateFilter) {
              operationIdDate = reportContext.dateFilter.operatorId
            }
            row.label = this.formatLabel(
              row.label,
              reportContext.xAxisField,
              reportOptions.xaxis,
              operationIdDate
            )
            if (Array.isArray(row.value)) {
              for (let j = 0; j < row.value.length; j++) {
                let col = row.value[j]
                col.orig_label = col.label
                col.label = this.formatLabel(
                  col.label,
                  reportContext.groupByField,
                  reportOptions.groupby,
                  operationIdDate
                )
                col.formatted_value = this.formatValue(
                  col.value,
                  reportOptions[yAxis]
                )
                if (sublabels.indexOf(col.label) === -1) {
                  sublabels.push(col.label)
                }
              }
            } else {
              this.setMinMaxVarianceLabel(reportOptions, row.label, row.value)
              row.formatted_value = this.formatValue(
                row.value,
                reportOptions[yAxis]
              )
            }
          } else {
            let row = reportData[i]
            row.orig_label = row.label
            if (
              diff &&
              reportOptions.xaxis.datatype.indexOf('date') !== -1 &&
              typeof row.label === 'number'
            ) {
              row.label = row.label + diff
            }
            row.orgLabel = row.label
            let operationIdDate
            if (reportContext.dateFilter) {
              operationIdDate = reportContext.dateFilter.operatorId
            }
            row.label = this.formatLabel(
              row.label,
              reportContext.xAxisField,
              reportOptions.xaxis,
              operationIdDate
            )
            if (Array.isArray(row.value)) {
              for (let j = 0; j < row.value.length; j++) {
                let col = row.value[j]
                col.orig_label = col.label
                col.label = this.formatLabel(
                  col.label,
                  reportContext.groupByField,
                  reportOptions.groupby,
                  operationIdDate
                )
                col.formatted_value = this.formatValue(
                  col.value,
                  reportOptions[yAxis]
                )
                if (sublabels.indexOf(col.label) === -1) {
                  sublabels.push(col.label)
                }
              }
            } else {
              this.setMinMaxVarianceLabel(reportOptions, row.label, row.value)
              row.formatted_value = this.formatValue(
                row.value,
                reportOptions[yAxis]
              )
            }
          }
        }
      }
      if (sublabels.length) {
        for (let i = 0; i < reportData.length; i++) {
          let row = reportData[i]

          if (Array.isArray(row.value)) {
            for (let sublabel of sublabels) {
              let sv = row.value.find(v => v.label === sublabel)
              if (!sv) {
                row.value.push({
                  label: sublabel,
                  value: 0,
                })
              }
            }
          }
        }
      }
      return reportData
    },
    repoveChartDip(report) {
      if (
        report &&
        report.options &&
        (report.options.xaxis.datatype === 'date_time' ||
          report.options.xaxis.datatype === 'date') &&
        report.options.is_highres_data === false &&
        report.options.xaxis.operation === 'hoursofday'
      ) {
        let currentHour = moment(new Date())
          .tz(this.$timezone)
          .startOf('hour')
          .valueOf()
        if (report.data && report.data.length) {
          for (let i = 0; i < report.data.length; i++) {
            if (report.data[i].data && report.data[i].data.length) {
              for (let j = 0; j < report.data[i].data.length; j++) {
                if (report.data[i].data[j].orig_label >= currentHour) {
                  report.data[i].data.splice(j, 1)
                }
              }
            }
          }
        }
        if (report.alarms && report.alarms.length) {
          for (let i = 0; i < report.alarms.length; i++) {
            let mills = moment(report.alarms[i].from)
              .tz(this.$timezone)
              .valueOf()
            if (mills > currentHour) {
              report.alarms.splice(i, 1)
            }
          }
        }
      }
    },
    prepareBooleanKey(key, reportContext, reportOptions, yAxis, diff, call) {
      let keys = []
      let col = reportOptions.color && reportOptions.color.split(',')
      if (key && key.length) {
        let color = null
        for (let i = 0; i < key.length; i++) {
          color = reportOptions.color ? reportOptions.color : colors.default[i]
          if (
            key[i] === '1' ||
            key[i] === 1 ||
            key[i] === true ||
            key[i] === 'true' ||
            key[i] === 'on' ||
            key[i] === 'ON'
          ) {
            color = col && col.length === 2 ? col[0] : '#74d2c8'
            key[i] = 'ON'
          }
          if (
            key[i] === '0' ||
            key[i] === false ||
            key[i] === 'false' ||
            key[i] === 'off' ||
            key[i] === 'OFF'
          ) {
            key[i] = col && col.length === 2 ? col[1] : '#f3fafa'
            color = '#74d2c8'
          }
          keys.push({
            value: key[i],
            color: color,
          })
        }
      }
      return [
        { value: 'ON', color: col && col.length === 2 ? col[0] : '#74d2c8' },
        { value: 'OFF', color: col && col.length === 2 ? col[1] : '#f3fafa' },
      ]
    },
    setBooleanKey(data) {
      let key = []
      if (data && data.length) {
        let options = data.map(rt => rt.options.booleanKey1)
        if (options && options.length) {
          for (let i = 0; i < options.length; i++) {
            let color = options.color ? options.color : colors.default[i]
            let op = options[i]
            for (let j = 0; j < op.length; j++) {
              if (
                op[j] === '1' ||
                op[j] === 1 ||
                op[j] === 'ON' ||
                op[j] === true ||
                key[i] === 'true' ||
                key[i] === 'on' ||
                key[i] === 'ON'
              ) {
                color = '#74d2c8'
                op[j] = 'ON'
              }
              if (
                op[j] === '0' ||
                op[j] === 0 ||
                op[j] === 'OFF' ||
                op[j] === false ||
                key[i] === 'false' ||
                key[i] === 'off' ||
                key[i] === 'OFF'
              ) {
                op[j] = 'OFF'
                color = '#f3fafa'
              }
              if (key.filter(rt => rt.value === op[j]).length < 1) {
                key.push({
                  value: op[j],
                  color: color,
                })
              }
            }
          }
        }
      }
      return key
    },
    combineReportData(report) {
      this.repoveChartDip(report)
      if (report.options.id === 1015) {
        report.options.is_combo = true
      }
      let dataList = report.data
      if (!dataList || !dataList.length) {
        return report
      } else if (
        report.options.id === 3354 ||
        report.options.id === 3357 ||
        report.options.id === 3578 ||
        report.options.id === 3580 ||
        report.options.id === 3582 ||
        report.options.id === 3584 ||
        report.options.id === 3586
      ) {
        let tmpReport = {}
        tmpReport.multi = true
        tmpReport.title = report.title
        tmpReport.options = report.data[1].options
        tmpReport.options.id = report.data[0].options.id
        tmpReport.data = [report.data[1]]
        tmpReport.options.type = 'bar'
        tmpReport.datefilter = report.datefilter
        if (report.data[0].options.color) {
          tmpReport.options.color = report.data[0].options.color
        }
        tmpReport.tabledata = report.data
        if (
          tmpReport.tabledata[0].data.length >
          tmpReport.tabledata[1].data.length
        ) {
          tmpReport.tabledata[0].data.splice(-1, 1)
        }
        return tmpReport
      } else if (dataList.length === 1 || !report.multi) {
        if (!dataList[0].data.length) {
          report.data = []
        }
        if (dataList.length === 1) {
          report.multi = false
        }
        return report
      }
      if (report && report.options.showWidgetLegends) {
        this.prepareMultiwidgetLegends(report)
      }

      if (report.options.type === 'regression' && dataList.length === 2) {
        return this.prepareRegressionData(report)
      } else if (dataList.length > 1 && report.options.is_highres_data) {
        if (report.options.booleanchart || report.options.enumChart) {
          report.options.type = 'boolean'
        } else {
          report.options.type = 'timeseries'
        }
        return report
      } else if (report.options.type === 'timeseries') {
        return report
      } else if (report.options.is_combo) {
        let y2Axis = false
        let unitList = {}
        for (let i = 0; i < dataList.length; i++) {
          let serie = dataList[i]

          if (i === 0) {
            if (
              ['bar', 'stackedbar', 'line', 'area'].indexOf(
                serie.options.type
              ) < 0
            ) {
              serie.options.type = 'bar'
            }
          } else {
            if (['line', 'area'].indexOf(serie.options.type) < 0) {
              serie.options.type = 'line'
            }
          }
          serie.options.y1axis.title = serie.title
          serie.options.withPoints = true
          if (!unitList[serie.options.y1axis.unit] && !y2Axis) {
            unitList[serie.options.y1axis.unit] = true
            if (Object.keys(unitList).length > 1) {
              serie.options.y2axis = serie.options.y1axis
              serie.options.y1axis = null
              y2Axis = true
            }
          }
        }
        return report
      } else {
        if (
          dataList.length === 2 &&
          dataList[0].options.baseLineContexts &&
          dataList[0].options.baseLineContexts.length > 0 &&
          dataList[1].options.baseLineContexts &&
          dataList[1].options.baseLineContexts.length > 0
        ) {
          let a = moment(report.options.timeObject.time[0])
            .tz(this.$timezone)
            .format('MM')
          let b = moment(
            report.options.timeObject.time[0] - dataList[1].options.baseLineDiff
          )
            .tz(this.$timezone)
            .format('MM')
          if (report.options.timeObject.field === 'D') {
            if (
              moment()
                .tz(this.$timezone)
                .diff(report.options.timeObject.time[0], 'days') === 0
            ) {
              dataList[0].options.entityName = 'Today'
              dataList[1].options.entityName = 'Yesterday'
            } else {
              dataList[0].options.entityName = this.$options.filters.formatDate(
                report.options.timeObject.time[0],
                true,
                false
              )
              dataList[1].options.entityName = this.$options.filters.formatDate(
                report.options.timeObject.time[0] -
                  dataList[1].options.baseLineDiff,
                true,
                false
              )
            }
          } else if (report.options.timeObject.field === 'M') {
            if (parseInt(a) - parseInt(b) === 1) {
              dataList[0].options.entityName = 'This month'
              dataList[1].options.entityName = 'Last month'
            } else {
              dataList[0].options.entityName = moment(
                report.options.timeObject.time[0]
              )
                .tz(this.$timezone)
                .format('MMM YYYY')
              dataList[1].options.entityName = moment(
                report.options.timeObject.time[0] -
                  dataList[1].options.baseLineDiff
              )
                .tz(this.$timezone)
                .format('MMM YYYY')
            }
          } else {
            dataList[0].options.entityName =
              this.$options.filters.formatDate(
                report.options.timeObject.time[0],
                true,
                false
              ) +
              '  -  ' +
              this.$options.filters.formatDate(
                report.options.timeObject.time[1],
                true,
                false
              )

            dataList[1].options.entityName =
              this.$options.filters.formatDate(
                dataList[1].options.date_range[0] -
                  dataList[1].options.baseLineDiff,
                true,
                false
              ) +
              '  -  ' +
              this.$options.filters.formatDate(
                dataList[0].options.date_range[1] -
                  dataList[1].options.baseLineDiff,
                true,
                false
              )
          }
        }
        if (report.options.id === 3481 || report.options.id === 3943) {
          if (report.options.timeObject.field === 'D') {
            if (
              moment()
                .tz(this.$timezone)
                .diff(report.options.timeObject.time[0], 'days') === 0
            ) {
              dataList[0].options.entityName = 'Today'
              dataList[1].options.entityName = 'Yesterday'
            } else {
              dataList[0].options.entityName = this.$options.filters.formatDate(
                report.options.timeObject.time[0],
                true,
                false
              )

              dataList[1].options.entityName = this.$options.filters.formatDate(
                report.options.timeObject.time[0] -
                  dataList[1].options.baseLineDiff,
                true,
                false
              )
            }
          } else {
            dataList[0].options.entityName =
              this.$options.filters.formatDate(
                report.options.timeObject.time[0],
                true,
                false
              ) +
              '  -  ' +
              this.$options.filters.formatDate(
                report.options.timeObject.time[1],
                true,
                false
              )

            dataList[1].options.entityName =
              this.$options.filters.formatDate(
                dataList[1].options.date_range[0] -
                  dataList[1].options.baseLineDiff,
                true,
                false
              ) +
              '  -  ' +
              this.$options.filters.formatDate(
                dataList[0].options.date_range[1] -
                  dataList[1].options.baseLineDiff,
                true,
                false
              )
          }
        }

        if (report.options.id === 3664 || report.options.id === 3663) {
          report.options.xaxis.datatype = 'string'
          report.data[0].options.xaxis.datatype = 'string'
          report.data[1].options.xaxis.datatype = 'string'
          if (!report.data[0].data.length) {
            let newData = []
            for (let d of report.data[1].data) {
              newData.push({
                label: d.label,
                orig_label: d.orig_label,
                orgLabel: d.orgLabel,
                value: 0,
                formatted_value: '0',
              })
            }
            report.data[0].data = newData
          }
        }

        let mapping = {}
        let keyList = []
        let sublabels = []
        let formatConfig =
          dataList[0].options.xaxis.datatype.indexOf('date') !== -1
            ? formatter.getDateFormatConfig(dataList[0].options.xaxis)
            : null
        for (let i = 0; i < dataList.length; i++) {
          let comp = dataList[i].data
          for (let j = 0; j < comp.length; j++) {
            let key = formatConfig
              ? moment(comp[j].orgLabel)
                  .tz(this.$timezone)
                  .startOf(formatConfig.period)
                  .format(formatConfig.tooltip)
              : comp[j].label

            let d = comp[j]
            d.key = key

            let tooltipLabel = formatConfig
              ? moment(comp[j].orig_label)
                  .tz(this.$timezone)
                  .startOf(formatConfig.period)
                  .format(formatConfig.tooltip)
              : comp[j].label
            let label = dataList[i].options.entityName

            let newd = {
              key: key,
              label: label,
              tooltip_label: tooltipLabel,
              orgLabel: d.orgLabel,
              value: d.value,
              formatted_value: d.formatted_value,
            }

            let val = null
            if (mapping[key]) {
              val = mapping[key]
            } else {
              val = []
              mapping[key] = val
              keyList.push([key, comp[j].label, comp[j].orgLabel])
            }
            val.push(newd)

            if (sublabels.indexOf(label) === -1) {
              sublabels.push(label)
            }
          }
        }

        let cdata = []
        for (let i = 0; i < keyList.length; i++) {
          let d = keyList[i]

          cdata.push({
            label: d[1],
            orgLabel: d[2],
            tooltip_label: 'Energy Consumption',
            value: mapping[d[0]] ? mapping[d[0]] : [],
          })
        }

        if (sublabels.length) {
          for (let i = 0; i < cdata.length; i++) {
            let row = cdata[i]

            if (Array.isArray(row.value)) {
              for (let sublabel of sublabels) {
                let sv = row.value.find(v => v.label === sublabel)
                if (!sv) {
                  row.value.push({
                    label: sublabel,
                    tooltip_label: null,
                    value: 0,
                  })
                }
              }
            }
          }
        }

        let options = dataList[0].options
        if (!options.groupby) {
          options.groupby = {
            title: 'Baseline',
            unit: '',
            datatype: 'string',
            operation: '',
          }
        }

        return {
          datefilter: report.datefilter,
          options: options,
          combined: true,
          data: [
            {
              data: cdata,
              options: options,
            },
          ],
        }
      }
    },

    prepareRegressionData(report) {
      let dataList = report.data
      let data = []
      let dateMapping = {}

      let formatConfig = formatter.getDateFormatConfig(dataList[0].options)
      for (let entry of dataList[0].data) {
        let date = moment(entry.label)
          .tz(this.$timezone)
          .startOf(formatConfig.period)
          .format(formatConfig.tooltip)

        data.push({
          label: entry.value,
          formatted_date: moment(entry.label)
            .tz(this.$timezone)
            .startOf(formatConfig.period)
            .format(formatConfig.tooltip),
          value: null,
        })
        dateMapping[date] = data.length - 1
      }

      for (let entry of dataList[1].data) {
        let date = moment(entry.label)
          .tz(this.$timezone)
          .startOf(formatConfig.period)
          .format(formatConfig.tooltip)

        let idx = dateMapping[date]
        if (idx) {
          data[idx].value = entry.value
        }
      }

      let options = this.$helpers.cloneObject(dataList[0].options)
      options.trendline = true
      options.xaxis = dataList[0].options.y1axis
      options.y1axis = dataList[1].options.y1axis

      let cdata = {
        options: options,
        datefilter: report.datefilter,
        data: [
          {
            data: data.filter(d => d.value !== null),
            options: options,
          },
        ],
      }
      if (!cdata.data[0].data || !cdata.data[0].data.length) {
        cdata.data = null
      }
      return cdata
    },

    prepareRelatedAlarms(alarmsData, reportContext, reportOptions) {
      let newAlarms = []
      let operationIdDate
      if (reportContext.dateFilter) {
        operationIdDate = reportContext.dateFilter.operatorId
      }
      if (alarmsData) {
        for (let row of alarmsData) {
          newAlarms.push({
            id: row.id,
            from: this.formatLabel(
              row.startTime,
              reportContext.xAxisField,
              reportOptions.xaxis,
              operationIdDate
            ),
            to: this.formatLabel(
              row.endTime,
              reportContext.xAxisField,
              reportOptions.xaxis,
              operationIdDate
            ),
            message: row.readingMessage,
          })
        }
        return newAlarms
      }
    },

    formatLabel(label, axis, axisOptions, operatorId) {
      let displayUnkownValueAs = 'Unknown'
      if (label === null) {
        label = displayUnkownValueAs
        return label
      }

      if (!axis) {
        return label
      }
      if (
        axis.field.dataTypeEnum._name === 'DATE' ||
        axis.field.dataTypeEnum._name === 'DATE_TIME'
      ) {
        let formatConfig = formatter.getDateFormatConfig(axisOptions)
        if (
          !axisOptions.is_highres_data &&
          axisOptions.operation !== 'dateandtime' &&
          axisOptions.operation !== 'actual'
        ) {
          return new Date(
            moment(label)
              .tz(this.$timezone)
              .startOf(formatConfig.period)
              .valueOf()
          )
        }
        return new Date(label)
      } else if (axis.field.dataTypeEnum._name === 'STRING') {
        return label
      } else if (
        axis.field.columnName === 'PARENT_METER_ID' ||
        axis.field.columnName === 'BUILDING_ID' ||
        axis.field.columnName === 'RESOURCE_ID' ||
        axis.field.columnName === 'PARENT_ID'
      ) {
        if (
          axis.field.displayName === 'Building' ||
          axis.field.displayName === 'Parent'
        ) {
          axisOptions.datatype = 'lookup'
          let building = this.$store.getters.getBuildingsPickList()[label]
          if (!building) {
            building = displayUnkownValueAs
          }
          return building
        } else if (axis.field.displayName === 'Service') {
          let service = this.$store.state.serviceList[label]
          axisOptions.datatype = 'lookup'
          if (!service) {
            service = label
          }
          return service
        }
      } else if (axis.field.dataTypeEnum._name === 'LOOKUP') {
        if (axis.field.lookupModule === null) {
          if (axis.field.specialType === 'users') {
            let newLabel = null
            newLabel = this.$store.getters.getUser(label).name
            return newLabel || label
          } else if (axis.field.specialType === 'groups') {
            let newLabel = null
            newLabel = this.$store.getters.getGroup(label).name
            return newLabel || label
          }
        } else {
          let lookupModule = axis.field.lookupModule.name
          let newLabel = null
          if (lookupModule === 'ticketstatus') {
            newLabel = this.$store.getters.getTicketStatus(label, 'workorder')
              .displayName
          } else if (lookupModule === 'ticketpriority') {
            newLabel = this.getTicketPriority(label).displayName
          } else if (lookupModule === 'ticketcategory') {
            newLabel = this.getTicketCategory(label).displayName
          } else if (lookupModule === 'tickettype') {
            newLabel = this.$store.getters.getTicketTypePickList()[label]
          } else if (lookupModule === 'asset') {
            newLabel = this.assets[label]
          } else if (lookupModule === 'basespace') {
            newLabel = this.$store.getters.getSpace(label).name
          } else if (lookupModule === 'alarmseverity') {
            let sev = this.$store.state.alarmSeverity.find(
              as => as.id === label
            )
            if (sev) {
              newLabel = sev.severity
            }
          } else if (lookupModule === 'assetcategory') {
            let category = this.$store.state.assetCategory.find(
              as => as.id === label
            )
            if (category) {
              newLabel = category.name
            }
          } else if (lookupModule === 'assetdepartment') {
            let dept = this.$store.state.assetDepartment.find(
              as => as.id === label
            )
            if (dept) {
              newLabel = dept.name
            }
          } else if (lookupModule === 'assettype') {
            let type = this.$store.state.assetType.find(as => as.id === label)
            if (type) {
              newLabel = type.name
            }
          }
          return newLabel || label
        }
      }
      return label
    },

    formatValue(value, axis, returnType = 'string') {
      return formatter.formatValue(value, axis, returnType)
    },

    getCurrentModule() {
      if (this.config && this.config.currentDashboard) {
        return {
          module: this.config.currentDashboard.moduleName,
          rootPath: '',
        }
      }
      let routeObj = this.$route
      let module = null
      let rootPath = null
      if (this.$helpers.isLicenseEnabled('NEW_LAYOUT')) {
        if (routeObj.params.moduleName) {
          module = routeObj.params.moduleName
          rootPath = routeObj.path
        } else {
          if (routeObj.matched) {
            for (let matchedRoute of routeObj.matched) {
              if (matchedRoute.meta.module) {
                module = matchedRoute.meta.module
                rootPath = matchedRoute.path
                break
              }
            }
          }
        }
      } else {
        if (routeObj.meta.module) {
          module = routeObj.meta.module
          rootPath = routeObj.path
        } else {
          if (routeObj.matched) {
            for (let matchedRoute of routeObj.matched) {
              if (matchedRoute.meta.module) {
                module = matchedRoute.meta.module
                rootPath = matchedRoute.path
                break
              }
            }
          }
        }
      }
      rootPath = rootPath.replace('/new', '')
      rootPath = rootPath.replace('/newmatrix', '')
      rootPath = rootPath.replace('/newtabular', '')
      rootPath = rootPath.replace('/scheduled', '')
      return {
        module: module,
        rootPath: rootPath,
      }
    },
  },
}
