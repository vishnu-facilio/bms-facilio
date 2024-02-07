<template>
  <div class="dragabale-card booleancard">
    <div v-if="loading" class="shimmer-frame">
      <div class="assetcard-shimmer shine"></div>
    </div>
    <div v-else>
      <div v-if="cardData" class="header">{{ cardData.name }}</div>
      <new-date-picker
        ref="newDatePicker"
        :zone="$timezone"
        class="filter-field date-filter-comp inline"
        :dateObj="configData.dateFilter"
        @date="setDateFilter"
      ></new-date-picker>
      <div v-if="chartloading" class="no-data-center2">
        <spinner :show="true" size="80"></spinner>
      </div>
      <div class="no-data-center" v-else-if="nodata">
        {{ $t('panel.tyre.no_data') }}
      </div>
      <f-multi-chart
        v-else
        class="booleancard-chart"
        ref="multiChart"
        :data="reportObj.data"
        :options="reportObj.options"
        :alarms="reportObj.alarms"
        :booleanAlarmsData="reportObj.alarms"
        :booleanData="reportObj.booleanData"
        :dateRange="reportObj.dateRange"
      ></f-multi-chart>
    </div>
  </div>
</template>
<script>
import FMultiChart from 'newcharts/components/FMultiChart'
import NewDataFormatHelper from 'pages/report/mixins/NewDataFormatHelper'
import AnalyticsMixin from 'pages/energy/analytics/mixins/AnalyticsMixin'
import NewDateHelper from '@/mixins/NewDateHelper'
import NewDatePicker from '@/NewDatePicker'
export default {
  mixins: [NewDataFormatHelper, AnalyticsMixin, NewDateHelper],
  props: ['widget', 'config'],
  components: {
    FMultiChart,
    NewDatePicker,
  },
  data() {
    return {
      data: null,
      cardData: null,
      loading: false,
      nodata: false,
      chartloading: false,
      localDateFormat: [22, 25, 31, 30, 28, 27, 44, 45],
      configData: {
        height: 100,
        width: 100,
        dateFilter: NewDateHelper.getDatePickerObject(22),
      },
      reportObj: {
        data: {},
        options: {
          common: {
            mode: 1,
            buildingIds: [],
            type: 1,
          },
          hideAlarmTitle: true,
          general: {
            grid: {
              y: true,
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
              show: true,
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
              show: true,
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
          predictionTimings: '__vue_devtool_undefined__',
        },
        booleanData: {},
        analyticsType: -1,
        dateRange: {},
        params: {
          alarmId: 813353,
          mode: 1,
          xAggr: 0,
          startTime: 1554408000000,
          endTime: 1554494399999,
          showAlarms: true,
          isWithPrerequsite: true,
          newFormat: true,
        },
        alarms: {},
      },
    }
  },
  watch: {
    'config.height': {
      handler(newData, oldData) {
        let self = this
        setTimeout(function() {
          self.$refs['multiChart'].render()
        }, 100)
      },
    },
  },
  mounted() {
    this.loadCardData()
  },
  methods: {
    loadCardData(data) {
      let self = this
      let params = null
      if (this.widget.id > -1) {
        // let meta = JSON.parse(this.widget.dataOptions.metaJson)
        // this.cardData = meta.cardData
        params = {
          widgetId: self.widget.id,
        }
      } else {
        params = {
          paramsJson: {
            parentId: this.widget.dataOptions.cardData.selectedObj.id,
            dateOperator: this.widget.dataOptions.cardData.carddata.periodId,
          },
          staticKey: 'resourceAlarmBar',
        }
        let data = {
          params: params.paramsJson,
          cardata: this.widget.dataOptions.cardData,
        }
        this.widget.dataOptions.metaJson = JSON.stringify(data)
      }
      this.chartloading = true
      if (!data) {
        this.loading = true
      }
      self.$http
        .post('dashboard/getCardData', data ? data : params)
        .then(function(response) {
          self.updateMetadata()
          if (data) {
            self.updateCall(response.data.cardResult, data)
          }
          self.getData(response.data.cardResult)
          self.loading = false
          self.chartloading = false
          self.nodata = false
        })
        .catch(function(error) {
          console.log('******** error', error)
          if (error) {
            self.loading = false
            self.chartloading = false
            self.nodata = false
          }
        })
    },
    updateCall(result, params) {
      let data = params.paramsJson
      this.reportObj.alarms = this.prepareBooleanReport(result)
      this.reportObj.alarms.barSize = 'medium'
      this.configData.dateFilter = NewDateHelper.getDatePickerObject(
        data.dateOperator,
        data.dateValue
      )
      this.reportObj.dateRange = NewDateHelper.getDatePickerObject(
        data.dateOperator,
        data.dateValue
      )
      this.reportObj.xAggr = 0
      this.reportObj.report = {
        xAggr: 0,
      }
      this.reportObj.dateRange = this.prepareHighResDateRange(this.reportObj)
      this.reportObj.options.axis.x.range = this.reportObj.dateRange.range
      this.reportObj.data.x = this.reportObj.dateRange.range.domain
      // this.reportObj.booleanData['A'] = this.reportObj.alarms.regions
      this.$refs['multiChart'].render()
    },
    getData(result) {
      let data = JSON.parse(this.widget.dataOptions.metaJson)
      this.reportObj.alarms = this.prepareBooleanReport(result)
      this.reportObj.alarms.barSize = 'medium'
      this.configData.dateFilter = NewDateHelper.getDatePickerObject(
        data.cardata.carddata.periodId
      )
      this.reportObj.dateRange = NewDateHelper.getDatePickerObject(
        data.cardata.carddata.periodId
      )
      this.reportObj.xAggr = 0
      this.reportObj.report = {
        xAggr: 0,
      }
      this.reportObj.dateRange = this.prepareHighResDateRange(this.reportObj)
      this.reportObj.options.axis.x.range = this.reportObj.dateRange.range
      this.reportObj.data.x = this.reportObj.dateRange.range.domain
      // this.reportObj.booleanData['A'] = this.reportObj.alarms.regions
    },
    updateMetadata() {
      this.cardData = JSON.parse(
        this.widget.dataOptions.metaJson
      ).cardata.carddata
    },
    setDateFilter(dateFilter) {
      let paramsJson = {}
      if (this.widget.id > -1) {
        paramsJson.parentId = this.widget.dataOptions.paramsJson.parentId
        if (
          this.localDateFormat.findIndex(rt => rt === dateFilter.operatorId) >
          -1
        ) {
          paramsJson.dateOperator = dateFilter.operatorId
        } else {
          paramsJson.dateOperator = 20
          paramsJson.dateValue = dateFilter.value[0] + ',' + dateFilter.value[1]
        }
      } else {
        paramsJson.parentId = JSON.parse(
          this.widget.dataOptions.metaJson
        ).params.parentId
        paramsJson.dateOperator = dateFilter.operatorId
      }
      let params = {
        paramsJson: paramsJson,
        staticKey: 'resourceAlarmBar',
      }
      this.loadCardData(params)
    },
  },
}
</script>
<style>
.booleancard .f-multichart-print {
  display: none;
}
.booleancard .bb-axis-x-label {
  display: none;
}
.booleancard .date-filter-comp {
  position: unset;
  float: right;
  padding-top: 7px;
  padding-right: 20px;
}
.booleancard-chart {
  position: relative;
  top: 50px;
  border-top: 1px solid #eae8e8;
}
.booleancard .header {
  float: left;
  padding: 20px;
  position: relative;
  left: 1px;
  cursor: pointer;
  top: -5px;
  padding-left: 30px;
  font-size: 16px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.5px;
  color: #324056;
}
.alarm-spereator {
  padding: 20px;
}
.tooltip-alarm-header {
  display: inline-flex;
  padding: 10px;
}
.tooltip-alarm-newtitle {
  font-size: 13px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: 1.15;
  letter-spacing: 0.4px;
  color: #324056;
}

.tooltip-alarm-subtitle {
  font-size: 11px;
  color: #8ca1ad;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: 1.15;
  letter-spacing: 0.4px;
  padding-top: 5px;
}
.alarm-spereator {
  padding: 20px;
  padding-top: 0;
  font-size: 25px;
  font-weight: 300;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.7px;
  color: #c0c2c6;
}
.booleancard-chart .fc-alarms-chart-title {
  right: 55px;
  display: none;
}
.booleancard-chart .bb .bb-axis-x .tick text,
.booleancard-chart .bb .bb-axis-y .tick text,
.booleancard-chart .bb .bb-axis-y2 .tick text {
  font-size: 10px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.3px;
  fill: #324056;
}
.fc-widget .booleancard-chart .f-multichart {
  padding: 0px;
}
.booleancard-chart .fc-alarms-chart.pdf-chart.bb {
  position: absolute !important;
  padding-top: 20px;
}
.no-data-center {
  position: absolute;
  top: 50%;
  left: 45%;
}
.no-data-center2 {
  position: absolute;
  top: 30%;
  left: 45%;
}
.booleancard .mobile-new-date-filter {
  position: relative;
  top: 10px;
  bottom: 73px;
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  font-weight: 500;
  opacity: 0.8;
  white-space: nowrap;
}
</style>
