<template>
  <div ref="insights-container" class="asset-sum-empty-block">
    <div class="d-flex pR30">
      <div class="f16 bold mT20 mB20 mL30 inline">
        {{ $t('asset.performance.fault_reports') }}
      </div>
      <div class="mT10" style="margin-left: auto">
        <new-date-picker
          :zone="$timezone"
          class="filter-field date-filter-comp"
          style="margin-left: auto"
          :dateObj.sync="dateObj"
          :disableFutureDate="true"
          @date="changeDateFilter"
        ></new-date-picker>
      </div>
    </div>
    <table
      class="setting-list-view-table"
      style="width: 100%"
      ref="insights-list"
    >
      <tbody>
        <tr v-if="loading" class="nodata">
          <td colspan="100%" class="text-center p30imp">
            <spinner :show="loading"></spinner>
          </td>
        </tr>

        <tr v-else-if="isListEmpty" class="nodata">
          <td colspan="100%" class="text-center p30imp">
            <div class="mT40">
              <InlineSvg
                src="svgs/emptystate/alarmEmpty"
                iconClass="icon text-center icon-xxxxlg emptystate-icon-size"
              ></InlineSvg>
              <div class="fc-black-dark f18 bold">
                {{ $t('asset.performance.no_asset_fault') }}
              </div>
            </div>
          </td>
        </tr>

        <template v-else>
          <tr class="nohover" style="border: 1px solid transparent">
            <td class="header-td f15 bold" style="width: 25%; font-weight: 500">
              {{ preRequsiteName }}
            </td>
            <td class="f15 bold header-td" style="width: 15%">
              {{ totalCount }} {{ $t('asset.performance.faults') }}
            </td>
            <td class="f15 bold header-td" style="width: 23%">
              <!-- {{ $helpers.getFormattedDuration(totalDuration) }} -->
            </td>
            <td class="header-td alarm-bar-cell" style="width: 30%">
              <div
                class="text-fc-grey"
                v-if="!chartDataLoadStatus['overall'] && !loading"
              >
                <div class="mL30 mR30 animated-background"></div>
              </div>
              <f-multi-chart
                v-else-if="!loading"
                class="booleancard-chart widget-alarmbar"
                ref="overall-alarm-bar"
                key="overall-alarm-bar"
                :data="overAllChartData.data"
                :options="chartOptions"
                :alarms="overAllChartData.alarms"
                :booleanAlarmsData="overAllChartData.alarms"
                :booleanData="overAllChartData.booleanData"
                :dateRange="overAllChartData.dateRange"
              ></f-multi-chart>
            </td>
          </tr>

          <tr class="nohover">
            <th class="setting-table-th setting-th-text" style="width: 25%">
              {{ $t('asset.performance.name') }}
            </th>
            <th class="setting-table-th setting-th-text" style="width: 10%">
              {{ $t('asset.performance.faults') }}
            </th>
            <th class="setting-table-th setting-th-text" style="width: 15%">
              {{ $t('asset.performance.duration') }}
            </th>
            <th class="setting-table-th setting-th-text alarm-bar-cell">
              {{ $t('asset.performance.fault_insights') }}
            </th>
          </tr>

          <tr v-for="(alarm, index) in filteredList" :key="index">
            <td>
              <div class="text-ellipsis" @click="openSummary(alarm)">
                {{ alarm.subject ? alarm.subject : loadSubject(alarm) }}
              </div>
            </td>
            <td>
              <div class="text-ellipsis">{{ alarm.count }}</div>
            </td>
            <td>
              <div>{{ $helpers.getFormattedDuration(alarm.duration) }}</div>
            </td>
            <td class="alarm-bar-cell">
              <div
                class="text-fc-grey"
                v-if="!chartDataLoadStatus[alarm.accessKey]"
              >
                <div class="mL30 mR30 animated-background"></div>
              </div>
              <f-multi-chart
                v-else
                class="booleancard-chart widget-alarmbar"
                :ref="alarm.accessKey + '-alarmbar'"
                :key="'alarmbar' + index"
                :data="chartData[alarm.accessKey].data"
                :options="chartOptions"
                :alarms="chartData[alarm.accessKey].alarms"
                :booleanAlarmsData="chartData[alarm.accessKey].alarms"
                :booleanData="chartData[alarm.accessKey].booleanData"
                :dateRange="chartData[alarm.accessKey].dateRange"
                :hidecharttypechanger="true"
                @alarmDrilldown="openAlarmSummary(alarm)"
              ></f-multi-chart>
            </td>
          </tr>
        </template>
      </tbody>
    </table>
    <div
      class="f13 mB10 d-flex justify-content-center"
      style="margin-top: auto"
    >
      <a
        v-if="hasMoreItems && list && list.length !== 0"
        @click="toggleVisibileItems"
        class="pT10 pB10"
        >{{ showFullList ? 'Show Less' : 'Show More' }}</a
      >
    </div>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import FMultiChart from 'newcharts/components/FMultiChart'
import NewDataFormatHelper from 'pages/report/mixins/NewDataFormatHelper'
import NewDateHelper from '@/mixins/NewDateHelper'
import NewDatePicker from '@/NewDatePicker'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  props: [
    'details',
    'layoutParams',
    'resizeWidget',
    'moduleName',
    'hideTitleSection',
    'groupKey',
    'widget',
    'isRCA',
  ],
  mixins: [NewDataFormatHelper, NewDateHelper],
  components: {
    FMultiChart,
    NewDatePicker,
  },
  async created() {
    await this.$store.dispatch('loadAlarmSeverity')
  },
  data() {
    return {
      list: [],
      totalCount: null,
      totalDuration: null,
      loading: false,
      showFullList: false,
      defaultListLength: 4,
      initialHeight: null,
      defaultWidgetHeight: this.layoutParams ? this.layoutParams.h : null,
      overAllChartData: {
        data: {},
      },
      chartDataLoadStatus: {
        overall: false,
      },
      chartData: {},
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
      localDateFormat: [22, 25, 31, 30, 28, 27, 44, 45],
      configData: {
        height: 40,
        width: 100,
        dateFilter: NewDateHelper.getDatePickerObject(22),
      },
      dateObj: NewDateHelper.getDatePickerObject(31, null),
      dateOperator: 31,
      dateValue: null,
      resourceDetails: [],
    }
  },
  computed: {
    isListEmpty() {
      let { list } = this
      return isEmpty(list)
    },
    filteredList() {
      if (this.showFullList) return this.list
      else return (this.list || []).slice(0, this.defaultListLength)
    },
    hasMoreItems() {
      return this.list && this.list.length
        ? this.defaultListLength < this.list.length
        : false
    },

    ruleId() {
      return this.preRequsiteId
    },
    preRequsiteId() {
      let { details } = this
      return this.$getProperty(details, 'id', null)
    },
    preRequsiteName() {
      let { details } = this
      let { moduleName, alarmRule } = details || {}
      let name = ''
      if (moduleName === 'newreadingrules') {
        name = this.$getProperty(details, 'name', null)
      } else {
        name = this.$getProperty(alarmRule, 'preRequsite.name', null)
      }
      return name
    },
  },

  watch: {
    ruleId: {
      handler(newVal, oldVal) {
        if (newVal !== oldVal) this.loadAlarmInsights()
      },
      immediate: true,
    },
    preRequsiteId: {
      handler(newVal, oldVal) {
        if (newVal !== oldVal) this.loadAlarmInsights()
      },
      immediate: true,
    },
    filteredList() {
      this.$nextTick(() => this.initializeCharts())
    },
  },

  methods: {
    toggleVisibileItems() {
      this.showFullList = !this.showFullList
      this.resize()
    },
    autoResize(height) {
      let { isListEmpty } = this
      if (!isListEmpty) {
        if (!height) {
          height = this.$refs['insights-container'].scrollHeight + 15
        }
        let width = this.$refs['insights-container'].scrollWidth
        this.resizeWidget({
          height,
          width,
        })
      }
    },
    resize() {
      this.$nextTick(() => {
        if (this.showFullList) {
          this.autoResize()
        } else {
          if (!this.initialHeight) {
            this.initialHeight = this.$refs['insights-list'].scrollHeight + 120
          }
          this.autoResize(this.initialHeight)
        }
      })
    },

    changeDateFilter(dateFilter) {
      this.dateObj = dateFilter

      if (this.localDateFormat.includes(dateFilter.operatorId)) {
        this.dateOperator = dateFilter.operatorId
        this.dateValue = null
      } else {
        this.dateOperator = 20
        this.dateValue = dateFilter.value.join()
      }
      this.loadAlarmInsights()
      this.chartDataLoadStatus = {}
      this.chartData = {}
    },

    async loadAlarmInsights() {
      this.loading = true
      let url = `/v2/alarms/insights`
      let ruleId = this.details.alarmRule ? this.preRequsiteId : this.ruleId

      if (this.moduleName === 'newreadingrules') {
        url += `?ruleId=${ruleId}`
      } else {
        url += `?assetId=${ruleId}`
      }

      if (this.dateObj && this.dateObj.operatorId) {
        let operatorId = this.dateOperator

        url += NewDateHelper.isValueRequired(operatorId)
          ? `&dateOperator=${operatorId}&dateOperatorValue=${this.dateValue}`
          : `&dateOperator=${operatorId}`
      }
      if (this.isRCA) {
        url += `&isRca=${this.isRCA}`
      }
      let { error, data } = await API.get(url)
      if (error) {
        this.$message.error('Error Occured')
      } else {
        this.list = data.alarms || []
        this.list.forEach(alarm => {
          alarm.accessKey = this.$constants.SPECIAL_MODULE.includes(
            this.moduleName
          )
            ? alarm.resource.id
            : alarm.ruleId
        })
        this.calculateTotals()
      }
      this.loading = false
      this.initialHeight = null
      this.resize()
    },

    initializeCharts() {
      if (isEmpty(this.chartData['overall']))
        this.getChartData(
          this.ruleId ? (this.isRCA ? this.ruleId : null) : this.preRequsiteId,
          this.ruleId ? (this.isRCA ? null : this.ruleId) : null,
          this.ruleId ? null : this.preRequsiteId
        )
      if (this.isRCA) {
        let resourceIds = this.filteredList.map(d => d.resource.id)
        this.$util
          .loadResource(resourceIds)
          .then(
            resouece =>
              (this.resourceDetails = [...resouece, ...this.resourceDetails])
          )
      }

      this.filteredList.forEach(
        ({ ruleId, resource, accessKey, subRuleId, resourceId }) => {
          if (isEmpty(this.chartData[ruleId]))
            this.getChartData(
              ruleId ? ruleId : subRuleId,
              this.ruleId
                ? this.isRCA
                  ? resourceId
                  : this.ruleId
                : resource.id,
              accessKey
            )
        }
      )
    },

    calculateTotals() {
      this.totalCount = this.list.reduce((acc, { count }) => {
        return acc + Number(count)
      }, 0)

      this.totalDuration = this.list.reduce((acc, { duration }) => {
        return acc + duration
      }, 0)
    },

    loadSubject(alarm) {
      return this.resourceDetails.filter(d => d.id == alarm.resource.id)[0].name
    },

    prepareGraph(result, showTimeOnly) {
      let reportObj = {
        data: {},
      }

      reportObj.alarms = this.prepareBooleanReport(result)
      reportObj.alarms.barSize = 'medium'
      reportObj.alarms.showTimeOnly = showTimeOnly

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

    async getChartData(ruleId = null, resourceId = null, accessKey = null) {
      let params = {
        paramsJson: {
          parentId: resourceId,
          dateOperator: this.dateOperator,
          dateValue: this.dateValue,
        },
        staticKey: 'resourceAlarmBar',
        fetchAlarmInfo: false,
      }

      if (ruleId) {
        params.paramsJson.ruleId = ruleId
      }

      if (this.isRCA) {
        params.isRca = true
      }
      let { error, data } = await API.post('dashboard/getCardData', params)
      if (!error) {
        let { cardResult } = data || {}
        if ((this.ruleId && ruleId) || (ruleId && resourceId)) {
          this.chartData[accessKey] = this.chartData[accessKey] || {}
          this.$set(
            this.chartData,
            accessKey,
            this.prepareGraph(cardResult, this.moduleName === 'readingrule')
          )
          this.$set(this.chartDataLoadStatus, accessKey, true)
        } else {
          this.overAllChartData = this.prepareGraph(cardResult)
          this.$set(this.chartDataLoadStatus, 'overall', true)
        }
      }
    },
    openSummary(details) {
      if (details) {
        let url
        if (
          this.$route.name === 'assetsummary' ||
          this.moduleName === 'asset'
        ) {
          if (isWebTabsEnabled()) {
            let { name } =
              findRouteForModule('newreadingalarm', pageTypes.OVERVIEW) || {}
            name &&
              this.$router.push({
                name,
                params: { id: details.alarm.id, viewname: 'active' },
              })
          } else {
            url = '/app/fa/faults/all/newsummary/' + details.alarm.id
          }
        } else if (
          this.$route.name === 'ruleOverview' ||
          this.$route.name === 'newRulesSummary' ||
          this.moduleName === 'newreadingrules'
        ) {
          if (isWebTabsEnabled()) {
            let { name } = findRouteForModule('asset', pageTypes.OVERVIEW) || {}
            name &&
              this.$router.push({
                name,
                params: { id: details.resource.id, viewname: 'all' },
              })
          } else {
            url = '/app/at/assets/all/' + details.resource.id + '/overview'
          }
        }
        if (!isEmpty(url)) {
          this.$router.replace({ path: url })
        }
      }
    },
    openAlarmSummary(details) {
      if (details && details.alarm) {
        if (isWebTabsEnabled()) {
          let { name } =
            findRouteForModule('newreadingalarm', pageTypes.OVERVIEW) || {}
          name &&
            this.$router.push({
              name,
              params: { id: details.alarm.id, viewname: 'active' },
            })
        } else if (
          this.$route.name === 'ruleOverview' ||
          this.$route.name === 'newRulesSummary'
        ) {
          let url = '/app/fa/faults/all/newsummary/' + details.alarm.id
          window.open(url, '_blank')
        }
      }
    },
  },
}
</script>
<style scoped>
.setting-list-view-table tbody tr td.header-td {
  border-bottom: 1px solid rgba(233, 233, 226, 0.5);
}

.setting-list-view-table tbody tr:hover td.header-td {
  border-top: 1px solid transparent !important;
}

.alarm-bar-cell {
  width: 45%;
  height: 60px;
  padding: 0;
  overflow: hidden;
}

@keyframes placeHolderShimmer {
  0% {
    background-position: -468px 0;
  }

  100% {
    background-position: 468px 0;
  }
}

.animated-background {
  animation-duration: 1.4s;
  animation-fill-mode: forwards;
  animation-iteration-count: infinite;
  animation-timing-function: linear;
  animation-name: placeHolderShimmer;
  background: #f6f7f8;
  background: linear-gradient(to right, #fafafa 8%, #f1f1f1 33%, #fafafa 45%);
  background-size: 800px 104px;
  height: 33px;
  position: relative;
}
</style>
