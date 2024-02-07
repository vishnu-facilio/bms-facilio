<template>
  <div>
    <div class="alarm-bar-cell position-relative">
      <div class="text-fc-grey pL0 width100" v-if="chartDataLoadStatus">
        <div
          class="mL30 mR30 animated-background booleancard-chart widget-alarmbar"
          style="height: 26px;"
        ></div>
      </div>
      <f-multi-chart
        ref="barRefrs"
        v-else-if="chartData && chartData.data"
        class="booleancard-chart widget-alarmbar"
        :key="'alarmbar' + parentId"
        :data="chartData.data"
        :options="chartOptions"
        :alarms="chartData.alarms"
        :booleanAlarmsData="chartData.alarms"
        :booleanData="chartData.booleanData"
        :dateRange="chartData.dateRange"
        :hidecharttypechanger="true"
      ></f-multi-chart>
      <div v-if="isNoDropButtom" class="empty-div-cell"></div>
    </div>
  </div>
</template>
<script>
import NewDataFormatHelper from 'pages/report/mixins/NewDataFormatHelper'
import NewDateHelper from '@/mixins/NewDateHelper'
import { isEmpty } from '@facilio/utils/validation'
import FMultiChart from 'newcharts/components/FMultiChart'
export default {
  props: [
    'parentId',
    'sourceKey',
    'dateOperator',
    'dateValue',
    'isResize',
    'isRca',
    'resourceId',
    'parentAlarmId',
    'rca',
    'isOpen',
    'isNoDropButtom',
  ],
  mixins: [NewDataFormatHelper, NewDateHelper],
  components: {
    FMultiChart,
  },
  data() {
    return {
      localDateFormat: [22, 25, 31, 30, 28, 27, 44, 45],
      configData: {
        height: 40,
        width: 100,
        dateFilter: NewDateHelper.getDatePickerObject(22),
      },
      dateObj: NewDateHelper.getDatePickerObject(28, null),
      chartDataLoadStatus: false,
      chartData: null,
      chartOptions: {
        common: {
          mode: 1,
          buildingIds: [],
          type: 1,
        },
        hideAlarmTitle: true,
        general: {
          grid: {
            y: false,
            x: false,
          },
          point: {
            show: false,
          },
          labels: false,
          normalizeStack: false,
          dataOrder: null,
          hideZeroes: false,
        },
        settings: {
          chartMode: 'multi',
          alarm: true,
          chart: true,
          table: false,
          safelimit: false,
          enableAlarm: true,
          autoGroup: false,
          booleanAlarms: true,
        },
        tooltip: {
          grouped: false,
          sortOrder: 'none',
          showNullValues: false,
        },
        donut: {
          labelType: 'percentage',
          centerText: {
            primaryText: null,
            secondaryText: null,
          },
        },
        area: {
          above: false,
          linearGradient: true,
        },
        axis: {
          rotated: false,
          showy2axis: true,
          x: {
            show: false,
            label: {
              text: 'Timestamp',
              position: 'outer-center',
            },
            tick: {
              direction: 'auto',
            },
            range: {
              min: null,
              max: null,
            },
            datatype: 'date_time',
            time: {
              period: null,
              format: {
                period: 'minute',
                interval: 'minutes',
                format: 'MM-DD-YYYY HH:mm',
                d3Format: '%m-%d-%Y %H:%M',
                tooltip: 'LLL',
              },
            },
          },
          y: {
            show: false,
            label: {
              text: 'PRE FILTER ALARM',
              position: 'outer-middle',
              type: 'auto',
            },
            unit: null,
            scale: 'linear',
            range: {
              min: null,
              max: null,
            },
            ticks: {
              count: 5,
            },
            format: {
              decimals: 0,
            },
            padding: {
              bottom: 0,
            },
            datatype: null,
          },
          y2: {
            show: false,
            label: {
              text: null,
              position: 'outer-middle',
            },
            unit: null,
            scale: 'linear',
            range: {
              min: null,
              max: null,
            },
            ticks: {
              count: 5,
            },
            format: {
              decimals: 0,
            },
            padding: {
              bottom: 0,
            },
          },
        },
        legend: {
          show: true,
          position: 'top',
          width: 180,
        },
        widgetLegend: {
          show: false,
        },
        colorPalette: 'auto',
        style: {
          pie: {
            label: {
              show: true,
            },
          },
          donut: {
            width: null,
            label: {
              show: true,
            },
          },
          gauge: {
            width: null,
            label: {
              show: true,
            },
            min: 0,
            max: 100,
            unit: ' %',
          },
          line: {
            point: {
              show: true,
              radius: 5,
            },
            lineMode: 'default',
            stepType: 'step',
            stroke: {
              width: 1,
              opacity: 1,
              dashed: {
                length: 2,
                space: 2,
              },
            },
            connectNull: false,
          },
          area: {
            point: {
              show: true,
              radius: 5,
            },
            lineMode: 'default',
            stepType: 'step',
            fillOpacity: null,
            stroke: {
              width: 1,
              opacity: 1,
              dashed: {
                length: 2,
                space: 2,
              },
            },
            connectNull: false,
          },
          bar: {
            width: null,
          },
          scatter: {
            point: {
              radius: 5,
            },
          },
        },
        type: 'area',
        multichart: {},
        isSystemGroup: false,
        dataPoints: [
          {
            label: 'Status',
            children: [],
            type: 'group',
            chartType: '',
            groupKey: 1,
            unit: null,
            dataType: 'BOOLEAN',
          },
        ],
        safeLimit: [],
      },
    }
  },
  watch: {
    parentId: function(newVal) {
      this.initializeCharts()
    },
    dateOperator: function(newVal) {
      this.initializeCharts()
    },
    dateValue: function(newVal) {
      this.initializeCharts()
    },
    isResize: function(newVal) {
      // if (newVal) {
      // this.$refs
      if (this.$refs['barRefrs']) {
        this.$refs['barRefrs'].resize()
      }
      // }
    },
  },
  mounted() {
    this.initializeCharts()
  },
  methods: {
    prepareGraph(result) {
      this.chartDataLoadStatus = false
      let reportObj = {
        data: {},
      }

      reportObj.alarms = this.prepareBooleanReport(result)
      reportObj.alarms.barSize = 'small'

      this.configData.dateFilter = NewDateHelper.getDatePickerObject(
        this.dateOperator,
        this.dateValue
      )

      reportObj.dateRange = NewDateHelper.getDatePickerObject(
        this.dateOperator,
        this.dateValue
      )

      reportObj.xAggr = 0
      reportObj.report = {
        xAggr: 0,
      }
      reportObj.dateRange = this.prepareHighResDateRange(reportObj)

      reportObj.data.x = reportObj.dateRange.range.domain

      this.chartOptions.axis.x.range = reportObj.dateRange.range

      return reportObj
    },

    getChartData() {
      this.chartDataLoadStatus = true
      let params = {
        paramsJson: {
          isRca: this.isRca,
          [this.sourceKey]: this.parentId,
          dateOperator: this.dateOperator,
          dateValue: this.dateValue,
        },
        staticKey: 'resourceAlarmBar',
      }
      if (this.rca) {
        params.paramsJson['isRca'] = true
      }
      if (this.resourceId) {
        params.paramsJson['parentId'] = this.resourceId
      }
      if (this.parentAlarmId) {
        params.paramsJson['parentAlarmId'] = this.parentAlarmId
      }
      this.$http.post('dashboard/getCardData', params).then(response => {
        this.chartData = this.chartData || {}
        this.chartData = this.prepareGraph(response.data.cardResult)
        this.$nextTick(() => {
          if (this.$refs['barRefrs']) {
            this.$refs['barRefrs'].resize()
          }
        })
      })
    },
    initializeCharts() {
      this.getChartData()
    },
  },
}
</script>

<style>
.animated-background {
  animation-duration: 1.5s;
  animation-fill-mode: forwards;
  animation-iteration-count: infinite;
  animation-name: placeHolderShimmer;
  animation-timing-function: linear;
  background: #f7f8f9;
  background: linear-gradient(to right, #f7f8f9 8%, #e9e9e9 18%, #f6f7f8 33%);
  background-size: 800px 104px;
  height: 10px;
  position: relative;
}

.empty-div-cell {
  width: 25px;
  height: 26px;
  visibility: visible;
  float: right;
  position: relative;
  right: 11px;
  top: -46px;
  background: #fff;
}
.booleancard-chart .fc-alarms-chart-title {
  right: 0;
  display: none;
}
</style>
