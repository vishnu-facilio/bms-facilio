<template>
  <spinner
    v-if="loading"
    class="mT15"
    style="margin: 0 auto"
    :show="loading"
    size="80"
  ></spinner>
  <f-chart
    v-else-if="showChartOnly"
    ref="fchart"
    :width="configData.width"
    :height="configData.height"
    :type="report.options.type"
    :data="report.data"
    :options="report.options"
    :alarms="report.alarms"
    :config="config"
  ></f-chart>
  <div
    class="fc-report"
    v-else
    v-bind:class="{
      'report-setting-class':
        report && !report.options.is_highres_data && report.data.length >= 2,
    }"
  >
    <div
      class="fc-report-section"
      v-bind:class="{ 'second-class': !showDatepicker }"
    >
      <div class="fc-report-filter row header">
        <div
          v-if="!previewReportContext"
          class="chart-select"
          v-bind:class="{ noreportdata: !report.data.length }"
        >
          <date
            class="filter-field date-filter-comp"
            @data="setDateFilter"
            v-if="report.datefilter && showDatepicker"
            :mills="
              getMills(report.datefilter.operatorId, report.datefilter).time
            "
            :filter="
              getMills(report.datefilter.operatorId, report.datefilter).filter
            "
            :dateObj="report.datefilter"
            :reportId="config.id"
          ></date>
          <div
            class="filter-field change-chart-select"
            :title="$t('home.dashboard.change_chart')"
            data-placement="top"
            data-arrow="true"
            v-tippy
            v-if="currentChartType"
          >
            <span class="q-item-label">
              <span class="chart-icon" style="position: relative"
                ><chart-icon :icon="currentChartType.ct"></chart-icon
              ></span>
              <q-popover ref="charttypepopover">
                <q-list link class="scroll" style="min-width: 150px">
                  <q-item
                    v-for="(ctype, index) in chartType.availableChartTypes"
                    :key="index"
                    @click="
                      changeChartType(ctype), $refs.charttypepopover.close()
                    "
                    v-if="!ctype.disabled"
                  >
                    <span class="chart-icon" style="margin-right: 5px">
                      <chart-icon :icon="ctype.ct"></chart-icon>
                    </span>
                    <span class="chart-label">{{ ctype.label }}</span>
                  </q-item>
                </q-list>
              </q-popover>
            </span>
          </div>
          <div
            v-if="
              !isReadOnly &&
                ((config && config.isAutoRefreshEnabled) ||
                  (report &&
                    report.data.length >= 2 &&
                    !report.options.is_highres_data))
            "
            class="report-table-setting-icon"
          >
            <span
              title="Settings"
              v-tippy
              data-arrow="true"
              class="fc-theme-color"
              style="display: inline-block; cursor: pointer; top: -3px"
            >
              <el-popover
                popper-class="report-settings-popover"
                ref="popover2"
                v-model="reportSettingsPopover"
                placement="bottom"
                title="Settings"
                width="400"
                trigger="click"
              >
                <f-report-settings
                  @close="closeSettingsPopover()"
                  @save="saveSettingsPopover()"
                  :report="report"
                  :config="config"
                ></f-report-settings>
              </el-popover>
              <i v-popover:popover2 class="fa fa-cog"></i>
            </span>
          </div>
        </div>
        <div v-if="previewReportContext">
          <div
            class="filter-field change-chart-select chat-icon-align"
            :title="$t('home.dashboard.change_chart')"
            data-placement="top"
            data-arrow="true"
            v-tippy
            v-if="currentChartType"
          >
            <span class="q-item-label">
              <span class="chart-icon" style=""
                ><chart-icon :icon="currentChartType.ct"></chart-icon
              ></span>
              <q-popover ref="charttypepopover">
                <q-list link class="scroll" style="min-width: 150px">
                  <q-item
                    v-for="(ctype, index) in chartType.availableChartTypes"
                    :key="index"
                    @click="
                      changeChartType(ctype), $refs.charttypepopover.close()
                    "
                    v-if="!ctype.disabled"
                  >
                    <span class="chart-icon" style="margin-right: 5px">
                      <chart-icon :icon="ctype.ct"></chart-icon>
                    </span>
                    <span class="chart-label">{{ ctype.label }}</span>
                  </q-item>
                </q-list>
              </q-popover>
            </span>
          </div>
        </div>
        <div style="clear: both"></div>
      </div>
      <div class="fc-underline"></div>
      <div class="fchart-overlay" v-if="filterApplying"></div>
      <div class="self-center fchart-section">
        <div v-if="!loading && failed">
          {{ $t('common._common.load_data_failed') }}
        </div>
        <div
          v-else-if="!report.data.length"
          style="
            margin-top: 50px;
            font-size: 13px;
            padding: 50px;
            line-height: 25px;
          "
        >
          <inline-svg
            src="svgs/emptystate/reportlist"
            iconClass="icon text-center icon-xxxlg"
          ></inline-svg>
          <div class="text-center nowo-label f13 line-height20">
            {{ $t('common.wo_report.empty_data') }}
            <span class="bold" v-if="time && time.length"
              >{{ time[0] | formatDate('MMM DD, YYYY') }} -
              {{ time[1] | formatDate('MMM DD, YYYY') }}</span
            >
          </div>
          <!-- <div>There isn't enough <span class="bold" v-if="time && time.length">{{ time[0] | formatDate('MMM DD, YYYY') }} - {{ time[1] | formatDate('MMM DD, YYYY') }}</span> data to power this graph.</div> -->
        </div>
        <f-timeseries
          v-else-if="
            report.options.type === 'timeseries' ||
              report.options.type === 'tabular'
          "
          ref="fTimeseriesChart"
          :width="configData.width"
          :height="configData.height"
          :data="report.data"
          :alarms="report.alarms"
        ></f-timeseries>
        <f-tabular-data
          v-else-if="report.options.type === 'matrix'"
          ref="tabulardata"
          :data="report.data"
          :options="report.options"
        ></f-tabular-data>
        <f-chart
          v-else
          ref="fchart"
          :width="configData.width"
          :height="configData.height"
          :type="report.options.type"
          :data="report.data"
          :data2="report.data1"
          :options="report.options"
          :alarms="report.alarms"
          @drilldown="handleDrillDown"
          :config="config"
        ></f-chart>
      </div>
    </div>
  </div>
</template>
<script>
import FChart from 'charts/FChart'
import tooltip from '@/graph/mixins/tooltip'
import FTimeseries from 'charts/FTimeseries'
import ChartIcon from 'charts/components/chartIcon'
import moment from 'moment-timezone'
import formatter from 'charts/helpers/formatter'
import FTabularData from './FTabularData'
import date from '@/DatePicker'
import colors from 'charts/helpers/colors'
import ReportUtil from 'pages/report/mixins/ReportUtil'
import ChartType from 'pages/report/mixins/ChartType'
import FReportSettings from 'pages/report/components/FReportSettings'
import JumpToHelper from '@/mixins/JumpToHelper'
import { QPopover, QList, QItem } from 'quasar'
export default {
  props: [
    'config',
    'currentDateFilter',
    'print',
    'showChartOnly',
    'currentEMFilter',
    'previewReportContext',
    'getReportUtil',
  ],
  mixins: [ReportUtil, ChartType, JumpToHelper],
  components: {
    FChart,
    QPopover,
    QList,
    QItem,
    ChartIcon,
    FTimeseries,
    FTabularData,
    date,
    FReportSettings,
  },
  filters: {
    subMeterName(meterId, subMeters) {
      if (subMeters) {
        let meter = subMeters.find(sb => sb.id === meterId)
        if (meter) {
          return meter.name
        }
      }
      return ''
    },
  },
  data() {
    return {
      filterApplying: false,
      loading: true,
      mounted: true,
      failed: false,
      time: null,
      filterName: 'D',
      reportContext: null,
      reportFieldLabelMap: null,
      report: null,
      chartType: null,
      reportContextModel: null,
      availableBaseLines: [],
      comparisions: [],
      energyMeterFilter: {
        buildingId: '',
        serviceId: '',
        subMeterId: ',',
        readingField: '',
      },
      subMeters: [],
      meterList: null,
      dateOperators2: [
        {
          index: 0,
          label: 'Today',
          trimLabel: 'D',
          value: 22,
          uptoNowValue: 43,
          timestamp: [
            moment()
              .startOf('day')
              .valueOf(),
            moment()
              .endOf('day')
              .valueOf(),
          ],
        },
        {
          index: 1,
          label: 'This Week',
          trimLabel: 'W',
          value: 31,
          uptoNowValue: 47,
          timestamp: [
            moment()
              .startOf('week')
              .valueOf(),
            moment()
              .endOf('week')
              .valueOf(),
          ],
        },
        {
          index: 2,
          label: 'This Month',
          trimLabel: 'M',
          value: 28,
          uptoNowValue: 48,
          timestamp: [
            moment()
              .startOf('month')
              .valueOf(),
            moment()
              .endOf('month')
              .valueOf(),
          ],
        },
        {
          index: 3,
          label: 'This Year',
          trimLabel: 'Y',
          value: 44,
          uptoNowValue: 46,
          timestamp: [
            moment()
              .startOf('year')
              .valueOf(),
            moment()
              .endOf('year')
              .valueOf(),
          ],
        },
        {
          index: 4,
          label: 'Last N displayUnkownValueAs',
          trimLabel: 'C',
          value: 49,
          uptoNowValue: 49,
          timestamp: [
            moment()
              .startOf('day')
              .valueOf(),
            moment()
              .endOf('day')
              .valueOf(),
          ],
        },
        {
          index: 5,
          label: 'Last N displayUnkownValueAs',
          trimLabel: 'C',
          value: 42,
          uptoNowValue: 42,
          timestamp: [
            moment()
              .startOf('day')
              .valueOf(),
            moment()
              .endOf('day')
              .valueOf(),
          ],
        },
        {
          index: 6,
          label: 'Last N displayUnkownValueAs',
          trimLabel: 'C',
          value: 50,
          uptoNowValue: 50,
          timestamp: [
            moment()
              .startOf('day')
              .valueOf(),
            moment()
              .endOf('day')
              .valueOf(),
          ],
        },
        {
          index: 7,
          label: 'Last N displayUnkownValueAs',
          trimLabel: 'C',
          value: 51,
          uptoNowValue: 51,
          timestamp: [
            moment()
              .startOf('day')
              .valueOf(),
            moment()
              .endOf('day')
              .valueOf(),
          ],
        },
        {
          index: 8,
          label: 'Range',
          trimLabel: 'R',
          value: 100,
          uptoNowValue: 100,
          timestamp: [
            moment()
              .startOf('year')
              .valueOf(),
            moment()
              .endOf('year')
              .valueOf(),
          ],
        },
        {
          index: 9,
          label: 'Yesterday',
          option: 'D',
          value: 25,
          days: '30',
          uptoNowValue: 25,
          timestamp: [
            moment()
              .subtract(1, 'day')
              .startOf('day')
              .valueOf(),
            moment()
              .subtract(1, 'day')
              .endOf('day')
              .valueOf(),
          ],
        },
        {
          index: 10,
          label: 'Last month',
          trimLabel: 'M',
          value: 27,
          uptoNowValue: 48,
          timestamp: [
            moment()
              .subtract(1, 'month')
              .startOf('month')
              .valueOf(),
            moment()
              .subtract(1, 'month')
              .endOf('month')
              .valueOf(),
          ],
        },
        {
          index: 11,
          label: 'Last Week',
          trimLabel: 'W',
          value: 30,
          uptoNowValue: 47,
          timestamp: [
            moment()
              .subtract(1, 'week')
              .startOf('eek')
              .valueOf(),
            moment()
              .subtract(1, 'week')
              .endOf('week')
              .valueOf(),
          ],
        },
        {
          index: 12,
          label: 'yesterday',
          option: 'D',
          value: 25,
          uptoNowValue: 43,
          timestamp: [
            moment()
              .subtract(1, 'day')
              .startOf('day')
              .valueOf(),
            moment()
              .subtract(1, 'day')
              .endOf('day')
              .valueOf(),
          ],
        },
      ],
      value: '',
      previewOnly: false,
      reportSettingsPopover: false,
    }
  },
  created() {
    this.$store.dispatch('loadEnergyMeters')
  },
  mounted() {
    this.mounted = true
    if (this.previewReportContext) {
      this.previewOnly = true
    }
    this.initData()
  },
  computed: {
    isReadOnly() {
      if (
        this.config &&
        this.config.currentDashboard &&
        this.config.currentDashboard.readOnly
      ) {
        return true
      }
      return false
    },
    showDatepicker() {
      if (
        this.config &&
        this.config.layoutConfig &&
        this.config.layoutConfig.hideleagend
      ) {
        return false
      } else {
        return true
      }
    },
    appliedDateFilter() {
      if (this.$route.query.daterange) {
        return JSON.parse(this.$route.query.daterange)
      }
      return null
    },
    currentChartType() {
      if (this.isReadOnly) {
        return null
      }
      if (this.chartType) {
        return this.chartType.chartType
      }
      return null
    },
    configData() {
      return this.config
    },
    styleAObj() {
      return (
        'padding-left:' +
        this.config.width / 20 +
        'px;' +
        'padding-right:' +
        this.config.width / 20 +
        'px;'
      )
    },
    reportFilters() {
      if (this.$route.query.filters) {
        return this.$route.query.filters
      }
      return null
    },
    buildingId() {
      if (this.config && this.config.currentDashboard) {
        return this.config.currentDashboard.buildingId
      } else {
        if (
          this.$route.params &&
          (this.$route.params.dashboardlink !== 'chilleroverview' ||
            this.$route.params.dashboardlink !== 'chillerplant')
        ) {
          return this.$route.params.buildingid
        } else {
          return null
        }
      }
    },
    siteId() {
      if (this.config && this.config.currentDashboard) {
        return this.config.currentDashboard.siteId
      } else {
        if (
          this.$route.params &&
          this.$route.params.dashboardlink === 'sitedashboard'
        ) {
          return this.$route.params.buildingid
        } else {
          return null
        }
      }
    },
    chillerId() {
      if (this.config && this.config.currentDashboard) {
        return this.config.currentDashboard.chillerId
      } else {
        if (
          this.$route.params &&
          (this.$route.params.dashboardlink === 'chillers' ||
            this.$route.params.dashboardlink === 'chillerplant')
        ) {
          return this.$route.params.buildingid
        } else {
          return null
        }
      }
    },
  },
  watch: {
    config: {
      handler(newData, oldData) {
        if (newData && oldData) {
          if (newData.id !== oldData.id) {
            this.initData()
          }
        } else {
          this.initData()
        }
      },
      deep: true,
    },
    time: {
      handler(newData, oldData) {
        this.$emit('update:currentDateFilter', newData)
      },
      immediate: true,
    },
    energyMeterFilter: {
      handler(newData, oldData) {
        this.$emit('update:currentEMFilter', newData)
      },
      immediate: true,
    },
    print: {
      handler(newData, oldData) {
        this.printing()
      },
    },
    reportFilters: {
      handler(newData, oldData) {
        if (this.reportFilters) {
          this.initData()
        }
      },
      immediate: true,
    },
  },
  methods: {
    getMills(id, datefilter) {
      let self = this
      let options = {}
      let time = []
      if (id === 43 || id === 22) {
        id = 22
      } else if (id === 47 || id === 31) {
        id = 31
      } else if (id === 48 || id === 28) {
        id = 28
      } else if (id === 46 || id === 44) {
        id = 44
      } else if (id === 42) {
        id = 42
      } else if (id === 49) {
        id = 49
      } else if (id === 50) {
        id = 50
      } else if (id === 51) {
        id = 51
      } else if (id === 25) {
        id = 25
      } else if (id === 27) {
        id = 27
      } else if (id === 25) {
        id = 25
      } else if (id === 30) {
        id = 30
      } else if (
        datefilter.operatorId === null &&
        datefilter.endTime &&
        datefilter.startTime
      ) {
        id = 100
      } else {
        id = 22
      }
      let mills1 = typeof this.operaterIdTOmills(id)
      if (mills1 !== undefined) {
        let data = self.operaterIdTOmills(id)
        if (id === 42 || id === 49 || id === 50 || id === 51) {
          time = this.time
        } else {
          time = data.timestamp
        }
        options.filter = data.trimLabel
      } else {
        time = [
          moment()
            .startOf('day')
            .valueOf(),
          moment()
            .endOf('day')
            .valueOf(),
        ]
      }
      if (id === 100 && datefilter.startTime && datefilter.endTime) {
        time = [
          moment(datefilter.startTime)
            .tz(this.$timezone)
            .valueOf(),
          moment(datefilter.endTime)
            .tz(this.$timezone)
            .valueOf(),
        ]
      }
      options.time = time
      return options
    },
    operaterIdTOmills(id) {
      return this.dateOperators2.find(sb => sb.value === id)
    },
    operaterIdUpTOmills(id) {
      return this.dateOperators2.find(sb => sb.uptoNowValue === id)
    },
    refresh() {
      this.initData()
    },
    initData() {
      let self = this
      self.loading = true
      self.comparisions = []
      self.getParameter()
      self.$emit('onload', null)
      let url, params
      if (self.previewOnly) {
        url =
          'dashboard/getData?moduleName=' + self.previewReportContext.moduleName
        params = { reportContext: self.previewReportContext.reportContext }
      } else {
        url = '/dashboard/getData'
        params = {
          reportId: self.config.id,
        }
        if (
          self.$route.params &&
          self.$route.params.reportid &&
          self.$route.query.reportSpaceFilterContext
        ) {
          let querry = JSON.parse(self.$route.query.reportSpaceFilterContext)
          if (querry.buildingid) {
            params.reportSpaceFilterContext = {
              buildingId: parseInt(querry.buildingid),
            }
          }
        }
        if (
          self.$route.params &&
          self.$route.params.dashboardlink === 'buildingdashboard'
        ) {
          params.reportSpaceFilterContext = {
            buildingId: parseInt(self.buildingId),
          }
        } else if (
          self.$route.query &&
          self.$route.query.reportSpaceFilterContext &&
          self.$route.query.reportSpaceFilterContext
        ) {
          let querry = JSON.parse(self.$route.query.reportSpaceFilterContext)
          if (querry.buildingId) {
            params.reportSpaceFilterContext = {
              buildingId: parseInt(querry.buildingid),
            }
          } else if (querry.chillerId) {
            params.reportSpaceFilterContext = {
              chillerId: parseInt(querry.chillerId),
            }
          }
        }
        if (this.appliedDateFilter) {
          params.dateFilter = this.appliedDateFilter
        }
        if (self.buildingId) {
          params.reportSpaceFilterContext = {
            buildingId: parseInt(self.buildingId),
          }
        }
        if (self.siteId) {
          params.reportSpaceFilterContext = {
            siteId: parseInt(self.siteId),
          }
        }
        if (self.chillerId) {
          params.reportSpaceFilterContext = {
            chillerId: parseInt(self.chillerId),
          }
        }
        if (self.reportFilters) {
          params.filters = self.reportFilters
        }
      }
      self.$http
        .post(url, params)
        .then(function(response) {
          let reportObj = {}
          self.meterList = response.data.meterIds
          self.filterName = self.dateOperatorSetFilter(
            response.data.reportContext
              ? response.data.reportContext.dateFilter
                ? response.data.reportContext.dateFilter.operatorId
                : 22
              : 22,
            self.dateOperators2
          )
          if (response.data.reportContext.dateFilter) {
            self.setTime(
              response.data.reportContext.dateFilter.operatorId,
              true,
              response.data.reportContext.dateFilter.value,
              response.data.reportContext
            )
          }
          reportObj.options = self.prepareReportOptions(
            response.data,
            self.time,
            self.filterName
          )
          reportObj.alarms = self.prepareRelatedAlarms(
            response.data.readingAlarms,
            response.data.reportContext,
            reportObj.options
          )
          reportObj.datefilter = self.prepareDateFilter(
            response.data.reportContext
          )
          if (
            response.data.reportContext.reportSpaceFilterContext ||
            response.data.reportContext.energyMeter
          ) {
            let meterFilter = {
              buildingId: '',
              serviceId: '',
              subMeterId: ',',
              readingField: '',
            }
            if (response.data.reportContext.energyMeter) {
              meterFilter = response.data.reportContext.energyMeter
            }
            if (response.data.reportContext.reportSpaceFilterContext) {
              meterFilter.buildingId =
                response.data.reportContext.reportSpaceFilterContext.buildingId
            }
            self.energyMeterFilter = meterFilter
            self.loadSubMeters()
          } else {
            self.energyMeterFilter = null
          }
          if (
            response.data &&
            response.data.booleanResultGrouping &&
            response.data.booleanResultGrouping.length
          ) {
            reportObj.data2 = self.prepareBooleanReportData(
              response.data.booleanResultGrouping,
              response.data.reportContext,
              reportObj.options,
              'y1axis',
              response.data.baseLineComparisionDiff
            )
            reportObj.options.booleanchart = true
            reportObj.options.booleanKey = self.prepareBooleanKey(
              response.data.booleanResultOptions,
              response.data.reportContext,
              reportObj.options,
              'y1axis',
              response.data.baseLineComparisionDiff
            )
            reportObj.options.booleanKey1 = response.data.booleanResultOptions
            reportObj.data = self.prepareReportData(
              response.data.reportData,
              response.data.reportContext,
              reportObj.options,
              'y1axis',
              response.data.baseLineComparisionDiff
            )
          } else {
            reportObj.data = self.prepareReportData(
              response.data.reportData,
              response.data.reportContext,
              reportObj.options,
              'y1axis',
              response.data.baseLineComparisionDiff
            )
          }
          reportObj.baselines = response.data.reportContext.baseLineContexts
            ? response.data.reportContext.baseLineContexts
            : []

          let dataList = []
          let colorMap = {}
          colorMap[response.data.reportContext.id] =
            reportObj.options.color || colors.default[0]
          reportObj.options.color = colorMap[response.data.reportContext.id]

          if (reportObj.options.type === 'tabular') {
            self.$emit('onload', reportObj, response.data)
            return
          }

          let isLine =
            response.data.reportContext.baseLineContexts &&
            response.data.reportContext.baseLineContexts.length
          let lineOrArea = isLine ? 'line' : 'area'

          dataList.push({
            title: reportObj.options.entityName
              ? reportObj.options.entityName
              : reportObj.options.y1axis.title,
            type: lineOrArea,
            options: reportObj.options,
            data: reportObj.data,
            data2: reportObj.data2,
          })

          if (
            (response.data.reportContext.comparingReportContexts &&
              response.data.reportContext.comparingReportContexts.length) ||
            (response.data.reportContext.baseLineContexts &&
              response.data.reportContext.baseLineContexts.length)
          ) {
            let reportParamList = []

            if (response.data.reportContext.baseLineContexts) {
              for (let baseline of response.data.reportContext
                .baseLineContexts) {
                let prms = {
                  reportId: response.data.reportContext.id,
                  baseLineId: baseline.id,
                  baseLineName: baseline.name,
                }
                if (self.buildingId) {
                  prms.reportSpaceFilterContext = {
                    buildingId: parseInt(self.buildingId),
                  }
                }
                if (self.siteId) {
                  prms.reportSpaceFilterContext = {
                    siteId: parseInt(self.siteId),
                  }
                }
                if (self.chillerId) {
                  params.reportSpaceFilterContext = {
                    chillerId: parseInt(self.chillerId),
                  }
                }
                if (self.reportFilters) {
                  prms.filters = self.reportFilters
                }
                if (reportObj.options.type !== 'tabular') {
                  reportParamList.push(prms)
                }
                self.comparisions.push({
                  type: 'baseline',
                  name: baseline.name,
                  id: baseline.id,
                })

                if (response.data.reportContext.comparingReportContexts) {
                  for (let compReport of response.data.reportContext
                    .comparingReportContexts) {
                    if (compReport.excludeBaseline) {
                      continue
                    }
                    reportParamList.push({
                      reportId: compReport.id,
                      legendMode: compReport.legendMode
                        ? compReport.legendMode._name
                        : '',
                      baseLineId: baseline.id,
                      baseLineName: baseline.name,
                    })
                  }
                }
              }
            }

            if (response.data.reportContext.comparingReportContexts) {
              for (let compReport of response.data.reportContext
                .comparingReportContexts) {
                reportParamList.push({
                  reportId: compReport.id,
                  legendMode: compReport.legendMode
                    ? compReport.legendMode._name
                    : '',
                })
                self.comparisions.push({
                  type: 'comparision',
                  name: compReport.name,
                  id: compReport.id,
                })
              }
            }

            let index = 0
            self
              .loadData(reportParamList, index, dataList, colorMap, lineOrArea)
              .then(function(response) {
                reportObj.data = dataList
                reportObj.multi = true

                self.reportContext = response.data.reportContext
                self.reportFieldLabelMap = response.data.reportFieldLabelMap
                if (self.getWidgetChartType()) {
                  reportObj.options.type = self.getWidgetChartType()
                }
                self.report = self.combineReportData(reportObj)
                self.chartType = self.getChartType(self.report)
                if (self.report.options.type === 'regression') {
                  self.$emit('onload', reportObj)
                } else {
                  self.$emit('onload', self.report)
                }
                self.failed = false
                self.loading = false
              })
          } else {
            reportObj.data = dataList
            reportObj.multi = true

            self.reportContext = response.data.reportContext
            self.reportFieldLabelMap = response.data.reportFieldLabelMap
            if (self.getWidgetChartType()) {
              reportObj.options.type = self.getWidgetChartType()
            }
            self.report = self.combineReportData(reportObj)
            self.chartType = self.getChartType(self.report)
            if (self.report.options.type === 'regression') {
              self.$emit('onload', reportObj)
            } else {
              self.$emit('onload', self.report)
            }
            self.failed = false
            self.loading = false
          }
        })
        .catch(function(error) {
          console.log('error ', error)
          if (error) {
            self.failed = true
            self.loading = false
          }
        })
      self.sendReportutil()
    },
    getParameter() {},
    sendReportutil() {
      let self = this
      let data = {
        previewReportContext: self.previewReportContext,
        chartType: self.chartType,
      }
      self.$emit('reportUtil', data)
    },
    loadSubMeters() {},
    getWidgetChartType() {
      if (
        this.config &&
        this.config.widget &&
        this.config.widget.dataOptions.chartType
      ) {
        return this.config.widget.dataOptions.chartType
      }
      return null
    },
    applyDateFilter(isHeatMap, isCombo) {
      let dateFilterQuery = ''
      console.log('********** applyfilter called', this.report)
      if (this.report.datefilter) {
        if (this.report.datefilter.operatorId === 20) {
          if (
            this.report.datefilter.dateRange &&
            this.report.datefilter.dateRange.length === 2
          ) {
            dateFilterQuery =
              this.report.datefilter.dateRange[0].getTime() +
              ',' +
              this.report.datefilter.dateRange[1].getTime()
          }
        } else {
          dateFilterQuery = this.report.datefilter.operatorId
        }
      }
      if (dateFilterQuery === '' && !this.energyMeterFilter) {
        return
      }

      let self = this
      let url = '/dashboard/getData'
      let params = {}
      params.reportId = self.config.id
      if (dateFilterQuery) {
        params.dateFilter = this.time
      }
      if (isHeatMap) {
        params.isHeatMap = true
      }

      if (self.energyMeterFilter !== null) {
        self.energyMeterFilter.buildingId = self.energyMeterFilter.buildingId
          ? parseInt(self.energyMeterFilter.buildingId)
          : null
        self.energyMeterFilter.serviceId = self.energyMeterFilter.serviceId
          ? parseInt(self.energyMeterFilter.serviceId)
          : null
        self.energyMeterFilter.subMeterId = self.energyMeterFilter.subMeterId
          ? parseInt(self.energyMeterFilter.subMeterId)
          : null

        params.energyMeterFilter = self.energyMeterFilter
        if (self.energyMeterFilter && self.energyMeterFilter.buildingId) {
          params.reportSpaceFilterContext = {
            buildingId: parseInt(self.energyMeterFilter.buildingId),
          }
        }
      }
      if (self.buildingId) {
        let buildingId
        if (self.energyMeterFilter && self.energyMeterFilter.buildingId) {
          buildingId = parseInt(self.energyMeterFilter.buildingId)
        } else {
          buildingId = parseInt(self.buildingId)
        }
        params.reportSpaceFilterContext = {
          buildingId: parseInt(buildingId),
        }
      } else if (self.siteId) {
        params.reportSpaceFilterContext = {
          siteId: parseInt(self.siteId),
        }
      } else if (self.chillerId) {
        params.reportSpaceFilterContext = {
          chillerId: parseInt(self.chillerId),
        }
      }
      if (self.reportFilters) {
        params.filters = self.reportFilters
      }
      // + '?' + qs.stringify(params)
      self.filterApplying = true
      self.$http
        .post(url, params)
        .then(function(response) {
          self.meterList = response.data.meterIds
          let reportObj = {}
          reportObj.options = self.prepareReportOptions(
            response.data,
            self.time,
            self.filterName
          )
          if (isCombo) {
            reportObj.options.is_combo = isCombo
          }
          reportObj.alarms = self.prepareRelatedAlarms(
            response.data.readingAlarms,
            response.data.reportContext,
            reportObj.options
          )
          reportObj.options.type = self.report.options.type
          reportObj.datefilter = self.report.datefilter
          if (
            response.data &&
            response.data.booleanResultGrouping &&
            response.data.booleanResultGrouping.length
          ) {
            reportObj.data = self.prepareReportData(
              response.data.reportData,
              response.data.reportContext,
              reportObj.options,
              'y1axis',
              response.data.baseLineComparisionDiff
            )
            reportObj.options.booleanchart = true
            reportObj.options.booleanKey = self.prepareBooleanKey(
              response.data.booleanResultOptions,
              response.data.reportContext,
              reportObj.options,
              'y1axis',
              response.data.baseLineComparisionDiff
            )
            reportObj.options.booleanKey1 = response.data.booleanResultOptions
            reportObj.data2 = self.prepareBooleanReportData(
              response.data.booleanResultGrouping,
              response.data.reportContext,
              reportObj.options,
              'y1axis',
              response.data.baseLineComparisionDiff
            )
          } else {
            reportObj.data = self.prepareReportData(
              response.data.reportData,
              response.data.reportContext,
              reportObj.options,
              'y1axis',
              response.data.baseLineComparisionDiff
            )
          }
          let dataList = []
          let colorMap = {}
          colorMap[response.data.reportContext.id] =
            reportObj.options.color || colors.default[0]
          reportObj.options.color = colorMap[response.data.reportContext.id]

          let isLine =
            response.data.reportContext.baseLineContexts &&
            response.data.reportContext.baseLineContexts.length
          let lineOrArea = isLine ? 'line' : 'area'

          dataList.push({
            title: reportObj.options.entityName
              ? reportObj.options.entityName
              : reportObj.options.y1axis.title,
            type: lineOrArea,
            options: reportObj.options,
            data: reportObj.data,
            data2: reportObj.data2,
          })

          if (
            (response.data.reportContext.comparingReportContexts &&
              response.data.reportContext.comparingReportContexts.length) ||
            (response.data.reportContext.baseLineContexts &&
              response.data.reportContext.baseLineContexts.length)
          ) {
            let reportParamList = []

            if (response.data.reportContext.baseLineContexts) {
              for (let baseline of response.data.reportContext
                .baseLineContexts) {
                if (response.data.reportContext.chartType !== 'tabular') {
                  reportParamList.push({
                    reportId: response.data.reportContext.id,
                    baseLineId: baseline.id,
                    baseLineName: baseline.name,
                    energyMeterFilter: self.energyMeterFilter
                      ? self.energyMeterFilter
                      : null,
                    reportSpaceFilterContext: {
                      buildingId:
                        self.energyMeterFilter &&
                        self.energyMeterFilter.buildingId
                          ? self.energyMeterFilter.buildingId
                          : null,
                    },
                  })
                }
                if (response.data.reportContext.comparingReportContexts) {
                  for (let compReport of response.data.reportContext
                    .comparingReportContexts) {
                    if (compReport.excludeBaseline) {
                      continue
                    }
                    reportParamList.push({
                      reportId: compReport.id,
                      legendMode: compReport.legendMode
                        ? compReport.legendMode._name
                        : '',
                      baseLineId: baseline.id,
                      baseLineName: baseline.name,
                      energyMeterFilter: self.energyMeterFilter
                        ? self.energyMeterFilter
                        : null,
                      reportSpaceFilterContext: {
                        buildingId:
                          self.energyMeterFilter &&
                          self.energyMeterFilter.buildingId
                            ? self.energyMeterFilter.buildingId
                            : null,
                      },
                    })
                  }
                }
              }
            }

            if (response.data.reportContext.comparingReportContexts) {
              for (let compReport of response.data.reportContext
                .comparingReportContexts) {
                reportParamList.push({
                  reportId: compReport.id,
                  legendMode: compReport.legendMode
                    ? compReport.legendMode._name
                    : '',
                })
              }
            }

            let index = 0
            self
              .loadData(reportParamList, index, dataList, colorMap, lineOrArea)
              .then(function(response) {
                reportObj.data = dataList
                reportObj.multi = true

                self.reportContext = response.data.reportContext
                self.reportFieldLabelMap = response.data.reportFieldLabelMap
                self.report = self.combineReportData(reportObj)
                self.$emit('onload', self.report)
                self.filterApplying = false
                self.failed = false
                self.loading = false
              })
          } else {
            reportObj.data = dataList
            reportObj.multi = true

            self.reportContext = response.data.reportContext
            self.reportFieldLabelMap = response.data.reportFieldLabelMap
            self.report = self.combineReportData(reportObj)
            self.$emit('onload', self.report)
            self.filterApplying = false
            self.failed = false
            self.loading = false
          }
        })
        .catch(function(error) {
          console.log('error ', error)
          if (error) {
            self.failed = true
            self.loading = false
          }
        })
    },

    loadData(reportParamList, index, dataList, colorMap, lineOrArea) {
      let self = this
      let params = reportParamList[index]
      params.energyMeterFilter = self.energyMeterFilter
        ? self.energyMeterFilter
        : null
      params.reportSpaceFilterContext = {
        buildingId:
          self.energyMeterFilter && self.energyMeterFilter.buildingId
            ? self.energyMeterFilter.buildingId
            : null,
      }
      params.dateFilter = self.time
      return new Promise((resolve, reject) => {
        self.$http
          .post('/dashboard/getData', params)
          .then(function(response) {
            let options = self.prepareReportOptions(
              response.data,
              self.time,
              self.filterName
            )
            let data = []
            let data2 = []
            if (
              response.data &&
              response.data.booleanResultGrouping &&
              response.data.booleanResultGrouping.length
            ) {
              data = self.prepareReportData(
                response.data.reportData,
                response.data.reportContext,
                options,
                'y1axis',
                response.data.baseLineComparisionDiff
              )
              options.booleanchart = true
              options.booleanKey = self.prepareBooleanKey(
                response.data.booleanResultOptions,
                response.data.reportContext,
                options,
                'y1axis',
                response.data.baseLineComparisionDiff
              )
              options.booleanKey1 = response.data.booleanResultOptions
              data2 = self.prepareBooleanReportData(
                response.data.booleanResultGrouping,
                response.data.reportContext,
                options,
                'y1axis',
                response.data.baseLineComparisionDiff
              )
            } else {
              data = self.prepareReportData(
                response.data.reportData,
                response.data.reportContext,
                options,
                'y1axis',
                response.data.baseLineComparisionDiff
              )
            }
            let colorKey = response.data.reportContext.id
            if (params.baseLineId) {
              options.dashed = true
              colorKey += colorKey + '_' + params.baseLineId
            }
            if (!colorMap[colorKey]) {
              colorMap[colorKey] = options.color || colors.default[index + 1]
            }
            options.color = colorMap[colorKey]

            let title = response.data.reportContext.name
            if (params.baseLineName) {
              title = params.baseLineName
            }

            let chartType = response.data.reportContext.reportChartType
              ? response.data.reportContext.reportChartType.name
              : null
            if (!chartType || (chartType !== 'line' && chartType !== 'area')) {
              chartType = lineOrArea
            }

            dataList.push({
              title: response.data.entityName
                ? response.data.entityName
                : title,
              type: chartType,
              options: options,
              data: data,
              data2: data2,
            })
            console.log(reportParamList)
            if (index + 1 < reportParamList.length) {
              self
                .loadData(
                  reportParamList,
                  index + 1,
                  dataList,
                  colorMap,
                  lineOrArea
                )
                .then(function(response) {
                  resolve(response)
                })
            } else {
              resolve(response)
            }
          })
          .catch(function(error) {
            reject(error)
          })
      })
    },

    changeChartType(ctype) {
      let needToReload =
        this.report.options.type === 'heatMap' ||
        this.report.options.type === 'timeseries' ||
        this.report.options.is_combo
      if (!ctype.disabled) {
        this.report.options.type = ctype.ct
        if (ctype.sct) {
          this.report.options.type1 = ctype.sct
        }

        let self = this
        let formdata = {}
        formdata.chartType = this.report.options.type
        if (this.report.options.type1) {
          formdata.secChartType = this.report.options.type1
        }
        let params = {}
        params.reportId = self.report.options.id
        params.chartType = this.report.options.type
        if (this.config && this.config.widget) {
          params.widgetId = this.config.widget.id
          params.isReportUpdateFromDashboard = this.config
            .isReportUpdateFromDashboard
            ? this.config.isReportUpdateFromDashboard
            : false
        }
        if (this.report.options.type1) {
          params.secChartType = this.report.options.type1
        }
        self.$http.post('dashboard/updateChartType', params)
        if (this.report.options.type === 'heatMap') {
          self.applyDateFilter(true)
        } else if (this.report.options.type === 'timeseries') {
          self.applyDateFilter()
        } else if (this.report.options.type === 'combo') {
          self.applyDateFilter(false, true)
        } else if (needToReload) {
          self.applyDateFilter()
        }
      }
    },
    selectBuilding(building) {
      this.energyMeterFilter.buildingId = building
      this.energyMeterFilter.serviceId = null
      this.energyMeterFilter.subMeterId = null
      this.loadSubMeters()
    },
    selectMeter(meter) {
      this.energyMeterFilter.subMeterId = meter
      // this.energyMeterFilter.serviceId = null
    },
    selectService(service) {
      this.energyMeterFilter.serviceId = service
      this.energyMeterFilter.subMeterId = null
      this.loadSubMeters()
    },
    selectDateOperator(operator, data) {
      if (data === 'D') {
        this.filterName = 'D'
        console.log('day')
      } else if (data === 'W') {
        this.filterName = 'W'
        console.log('week')
      } else if (data === 'M') {
        this.filterName = 'M'
        console.log('month')
      } else if (data === 'Y') {
        this.filterName = 'Y'
        console.log('year')
      } else {
        this.filterName = 'D'
        console.log('day')
      }
      this.report.datefilter.operatorId = operator
      this.setTime()
    },
    setTime(operatorId, doNotApply, value, reportContext) {
      let dontapply = false
      if (!operatorId) {
        // dontapply = true
        // operatorId = this.report.datefilter.operatorId
        operatorId = 100
      }
      let self = this
      let startTime = null
      let endTime = null
      self.timeCounter = 0
      this.filterName =
        operatorId === 45
          ? 'Y'
          : this.dateOperatorId(operatorId, this.dateOperators2)
      if (this.filterName === 'D') {
        if (operatorId === 25) {
          startTime = moment()
            .tz(this.$timezone)
            .add(self.timeCounter - 1, 'day')
            .startOf('day')
          endTime = moment()
            .tz(this.$timezone)
            .add(self.timeCounter - 1, 'day')
            .endOf('day')
        } else {
          startTime = moment()
            .tz(this.$timezone)
            .add(self.timeCounter, 'day')
            .startOf('day')
          endTime = moment()
            .tz(this.$timezone)
            .add(self.timeCounter, 'day')
            .endOf('day')
        }
      } else if (this.filterName === 'W') {
        if (operatorId === 30) {
          startTime = moment()
            .tz(this.$timezone)
            .add(self.timeCounter - 1, 'week')
            .startOf('week')
          endTime = moment()
            .tz(this.$timezone)
            .add(self.timeCounter - 1, 'week')
            .endOf('week')
        } else {
          startTime = moment()
            .tz(this.$timezone)
            .add(self.timeCounter, 'week')
            .startOf('week')
          endTime = moment()
            .tz(this.$timezone)
            .add(self.timeCounter, 'week')
            .endOf('week')
        }
      } else if (this.filterName === 'M') {
        if (operatorId === 27) {
          startTime = moment()
            .tz(this.$timezone)
            .add(self.timeCounter - 1, 'month')
            .startOf('month')
          endTime = moment()
            .tz(this.$timezone)
            .add(self.timeCounter - 1, 'month')
            .endOf('month')
        } else {
          startTime = moment()
            .tz(this.$timezone)
            .add(self.timeCounter, 'month')
            .startOf('month')
          endTime = moment()
            .tz(this.$timezone)
            .add(self.timeCounter, 'month')
            .endOf('month')
        }
      } else if (this.filterName === 'Y') {
        if (operatorId === 45) {
          self.timeCounter = self.timeCounter - 1
        }
        startTime = moment()
          .tz(this.$timezone)
          .add(self.timeCounter, 'year')
          .startOf('year')
        endTime = moment()
          .tz(this.$timezone)
          .add(self.timeCounter, 'year')
          .endOf('year')
      } else if (this.filterName === 'C') {
        let time = this.getNimes(operatorId, value)
        startTime = time[0]
        endTime = time[1]
      } else if (this.filterName === 'R') {
        startTime = reportContext.dateFilter.startTime
        endTime = reportContext.dateFilter.endTime
      } else {
        startTime = moment()
          .tz(this.$timezone)
          .add(self.timeCounter, 'day')
          .startOf('day')
        endTime = moment()
          .tz(this.$timezone)
          .add(self.timeCounter, 'day')
          .endOf('day')
      }
      if (this.filterName === 'R') {
        this.time = [startTime, endTime]
      } else {
        this.time = [startTime.valueOf(), endTime.valueOf()]
      }
      if (dontapply) {
        this.applyDateFilter()
      }
      // console.log('self ***************', this.time)
    },
    dateOperatorId(id, operators) {
      if (operators) {
        let oprter = operators.find(
          op => op.value === id || op.uptoNowValue === id
        )
        if (oprter) {
          return oprter.trimLabel
        }
      }
      return '---'
    },
    getNimes(id, value) {
      let val, time
      if (value) {
        val = parseInt(value)
      }
      if (id === 42) {
        time = [
          moment()
            .tz(this.$timezone)
            .subtract(val - 1, 'hour')
            .startOf('hour')
            .valueOf(),
          moment()
            .tz(this.$timezone)
            .endOf('hour')
            .valueOf(),
        ]
      } else if (id === 49) {
        time = [
          moment()
            .tz(this.$timezone)
            .subtract(val - 1, 'day')
            .startOf('day')
            .valueOf(),
          moment()
            .tz(this.$timezone)
            .endOf('day')
            .valueOf(),
        ]
      } else if (id === 50) {
        time = [
          moment()
            .tz(this.$timezone)
            .subtract(val - 1, 'week')
            .startOf('week')
            .valueOf(),
          moment()
            .tz(this.$timezone)
            .endOf('week')
            .valueOf(),
        ]
      } else if (id === 51) {
        time = [
          moment()
            .tz(this.$timezone)
            .subtract(val - 1, 'month')
            .startOf('month')
            .valueOf(),
          moment()
            .tz(this.$timezone)
            .endOf('month')
            .valueOf(),
        ]
      } else {
        time = [
          moment()
            .tz(this.$timezone)
            .subtract(0, 'hour')
            .startOf('hour')
            .valueOf(),
          moment()
            .tz(this.$timezone)
            .endOf('hour')
            .valueOf(),
        ]
      }
      return time
    },
    dateOperatorSetFilter(id, operators) {
      if (operators) {
        let oprter = operators.find(op => op.value === id)
        let oprter2 = operators.find(op => op.uptoNowValue === id)
        if (oprter || oprter2) {
          if (oprter) {
            return oprter.trimLabel
          } else {
            return oprter2.trimLabel
          }
        } else {
          return 'D'
        }
      }
      return 'D'
    },
    printing() {
      setTimeout(function() {
        window.print()
      }, 1000)
    },
    removeComparision(comp) {
      let self = this
      if (comp.type === 'baseline') {
        self.updateBaseLine(null)
      } else {
        self.$http
          .post('/dashboard/deleteReport', { reportId: comp.id })
          .then(function(response) {
            self.initData()
          })
      }
    },
    updateBaseLine(baseline) {
      let self = this
      let reportId = this.config.id
      let formdata = {
        reportId: reportId,
      }
      if (baseline) {
        formdata.baseLines = [
          {
            id: baseline.id,
          },
        ]
      } else {
        formdata.baseLines = []
      }
      self.$http
        .post('/baseline/updateReportBaseLines', formdata)
        .then(function(response) {
          self.$message({
            message: 'Baseline updated successfully!',
            type: 'success',
          })
          self.initData()
        })
    },
    handleDrillDown(data) {
      let self = this
      if (this.isReadOnly) {
        return
      }
      console.log('****** handleDrillDown called', data)
      if (
        this.reportContext.xAxisField.field.module.name === 'workorder' ||
        this.reportContext.xAxisField.field.module.name === 'alarm'
      ) {
        console.log('******** loop 1')
        self.handleTicketsDrilldown(
          self.reportContext.xAxisField.field.module.name,
          data
        )
      } else {
        console.log('******** loop 2')
        self.handleReadingsDrilldown(
          self.reportContext.xAxisField.field.module.name,
          data
        )
      }
      tooltip.hideTooltip()
    },
    handleTicketsDrilldown(moduleName, data) {
      let self = this
      let filters = {}
      let dateFilterField = null

      for (let d of data) {
        let formatConfig = formatter.getDateFormatConfig(d.axis)
        if (!d.axis.field.fieldId || d.axis.field.fieldId < 0) {
          return
        }

        let fieldName =
          self.reportFieldLabelMap[d.axis.field.name] || d.axis.field.name
        if (d.axis.datatype === 'date' || d.axis.datatype === 'date_time') {
          dateFilterField = d.axis.field.fieldId
          filters[fieldName] = {
            operatorId: 20,
            value: [
              moment(d.data.orgLabel)
                .tz(this.$timezone)
                .startOf(formatConfig.period)
                .valueOf() + '',
              moment(d.data.orgLabel)
                .tz(this.$timezone)
                .endOf(formatConfig.period)
                .valueOf() + '',
            ],
          }
        } else {
          let operatorId = 36
          if (d.axis.datatype === 'string') {
            operatorId = 3
          }

          if (d.data.orig_label) {
            filters[fieldName] = {
              operatorId: operatorId,
              value: [d.data.orig_label + ''],
            }
          }
        }
      }
      if (
        this.reportContext.dateFilter &&
        this.reportContext.dateFilter.fieldId !== dateFilterField
      ) {
        filters[this.reportContext.dateFilter.field.name] = {
          operatorId: 20,
          value: [this.time[0] + '', this.time[1] + ''],
        }
      }
      if (this.$route.query.filters) {
        let dashboardFilters = JSON.parse(this.$route.query.filters)
        filters = this.$helpers.extend(filters, dashboardFilters)
      }

      if (Object.keys(filters).length) {
        let url = '/app/wo/orders/open'
        if (moduleName === 'alarm') {
          url = '/app/fa/faults/active'
        }

        let query = { includeParentFilter: true }
        query.search = JSON.stringify(filters)
        if (this.reportContext && this.reportContext.reportCriteriaContexts) {
          let criteriaId = []
          for (let criteria of this.reportContext.reportCriteriaContexts) {
            criteriaId.push(criteria.criteriaId)
          }
          query.criteriaIds = criteriaId.join(',')
        }
        this.$router.push({ path: url, query: query })
      }
    },
    handleReadingsDrilldown(moduleName, data) {
      if (this.reportContext.reportChartType.name === 'heatMap') {
        let parentId = parseInt(this.meterList[0])

        this.jumpReadingToAnalytics(
          parentId,
          this.reportContext.y1AxisField.field.id,
          { operatorId: 62, value: data.chartData.orgLabel + '' },
          20,
          3
        )
      }
    },
    setDateFilter(fulldate) {
      this.$emit('dateFilterUpdated', fulldate)
      if (fulldate.filterName === 'W') {
        this.time[0] = moment(fulldate.time[0])
          .tz(this.$timezone)
          .valueOf()
        this.time[1] = moment(fulldate.time[1])
          .tz(this.$timezone)
          .valueOf()
      } else {
        this.time = fulldate.time
      }
      this.filterName = fulldate.filterName
      let value = this.getOperatorId(fulldate.filterName)
      this.report.datefilter.operatorId = value.value
      console.log('******** apply filter mounted', this.mounted)
      this.applyDateFilter()
    },
    getOperatorId(name) {
      if (name) {
        if (name === 'R' || name === 'C') {
          return this.dateOperators2.find(n => n.trimLabel === 'Y')
        } else {
          return this.dateOperators2.find(n => n.trimLabel === name)
        }
      } else {
        return this.dateOperators2.find(n => n.trimLabel === name)
      }
    },
    closeSettingsPopover() {
      this.reportSettingsPopover = false
    },
    saveSettingsPopover() {
      if (this.$refs['fchart']) {
        this.$refs['fchart'].rerender()
      }
      this.reportSettingsPopover = false
    },
  },
}
</script>
<style>
.charttype-options {
  /* padding: 4px 10px;
 margin: 0;
 list-style: none;
 float: left; */
  border-bottom: 1px solid #6666660d;
  padding: 8px 13px;
}

.charttype-options ul.fchart-icon li {
  float: left;
  cursor: pointer;
  width: 45px;
  height: 40px;
  padding: 20px 10px 10px 10px;
}

.charttype-options ul li svg {
  width: 18px;
  height: 18px;
  opacity: 0.3;
}

.charttype-options ul li svg:hover,
.charttype-options ul li.active svg {
  opacity: 1;
}

.charttype-options-select {
  padding-top: 0px;
  padding-left: 10px;
  padding-right: 10px;
}
.chart-category-dropdown {
  font-size: 12px;
  /* padding-top: 15px; */
  /* padding-right: 23px; */
}
.datefilter-name {
  padding-right: 8px;
  padding-top: 7px !important;
}
.chart-created-info {
  text-align: left;
  line-height: 1.5;
  color: #333333;
  font-size: 12px;
  justify-content: center;
}
.chart-category-dropdown input.el-input__inner {
  font-size: 12px;
}
.datefilter-name {
  white-space: nowrap;
  align-items: center;
  justify-content: center;
  padding-top: 10px;
  padding-right: 10px;
}
.charttype-options-select {
  font-size: 12px;
}
.building-filter {
  text-align: left;
}
.building-filter .filter-entry {
  display: inline-block;
  font-size: 12px;
  color: #666;
  padding-left: 10px;
}

.building-filter .filter-entry .q-select {
  font-size: 12px;
  margin-top: 0px;
  padding-bottom: 0px;
  margin-right: 10px;
}

.building-filter .filter-entry .q-select i {
  font-size: 12px;
  opacity: 0.5;
  padding-right: 4px;
}

.building-filter .filter-entry .q-select:before {
  height: 0px;
}

.building-filter .filter-entry .q-select .q-if-control[slot='after'] {
  display: none;
}

.fc-report-filter {
  padding: 12px 24px;
}
.reports-summary .fc-report-filter {
  padding: 25px 25px;
}

.fc-report-filter .pull-left {
  padding-top: 4px;
}

.fc-report-filter .filter-field {
  font-size: 12px;
  padding-bottom: 0px;
  margin-right: 15px;
}

.fc-report-filter .filter-field i {
  opacity: 1;
  padding-right: 4px;
  font-size: 1.2em;
  padding-top: 5px;
  color: var(--fc-theme-color);
  font-weight: 600;
  padding-right: 0;
  margin-right: -1px;
}

.fc-report-filter .filter-field .plholder {
  opacity: 0.5;
  font-size: 11px;
}

.chart-icon svg {
  width: 18px;
  height: 18px;
}
.chart-label {
  margin-top: -4px;
  margin-left: 6px;
}
.fchart-overlay {
  position: absolute;
  width: 100%;
  height: 100%;
  background: #ffffff91;
  cursor: progress;
  z-index: 100;
}
.report-col {
  padding: 5px;
}
.report-col.active {
  color: red !important;
}
.date-icon-right,
.date-icon-left {
  font-size: 15px;
  position: relative;
  top: 2px;
}
.emptyLegends {
  padding-top: 45px;
}
.fc-report-building {
  top: 25px;
  padding: 5px;
  border-radius: 3px;
  background-color: #fff;
  border: solid 1px #39b2c2;
  padding-bottom: 5px !important;
  position: absolute;
}
.fc-report-building .q-item-label {
  color: #39b2c2 !important;
  font-size: 12px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.3px;
  text-align: left;
}
.fc-report-building .q-item-label > span,
.fc-report-building .q-item-label > i,
.fc-compare-options .q-item-label > span {
  color: #39b2c2 !important;
}
.fc-report-font-icon {
  font-weight: 900;
  font-size: 14px;
  color: #39b2c2;
}
.fc-compare-options .q-item-label {
  padding: 5px;
  border-radius: 3px;
  background-color: transparent;
  border: solid 1px #39b2c2;
  padding-bottom: 5px !important;
  font-size: 12px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.3px;
  text-align: left;
  color: #39b2c2;
}
.building-title {
  margin-bottom: 10px;
  font-size: 11px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.7px;
  text-align: left;
  color: #609298;
}
.fc-underline {
  border-top: solid 1px #f2f5f7;
  margin-left: 10px;
  margin-right: 10px;
}
.fc-compare-button {
  padding: 10px;
  border-radius: 5px;
  background-color: #ffffff;
  box-shadow: 0 2px 4px 0 rgba(230, 230, 230, 0.5);
  border: solid 1px #d9e0e7;
  font-size: 12px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 1.2px;
  text-align: left;
  color: #615f89;
  padding-top: 12px;
  padding-bottom: 12px;
  padding-right: 21px;
  padding-left: 21px;
}
.baselineoption {
  position: fixed;
  top: 86px;
  right: 215px;
}
.report-dropDown {
  width: 100%;
}
.fc-report .el-popover {
  padding: 0px !important;
}
.fc-el-report-pop {
  padding: 0px !important;
}
.fc-el-btn {
  text-align: center;
  padding: 10px;
  font-size: 12px;
  align-items: center;
  text-transform: uppercase;
  padding-bottom: 15px;
  cursor: pointer;
  font-weight: 500;
  padding-top: 15px;
}
.el-report-cancel-btn {
  background-color: #f4f4f4;
  color: #5f5f5f;
}
.el-report-save-btn {
  color: white;
  background-color: #39b2c2;
}
.report-dropDown input.el-input__inner {
  font-size: 14px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.5px;
  text-align: left;
  color: #333333;
}
.fc-report-popover-conetnt .header {
  font-size: 12px;
  opacity: 0.8;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.4px;
  text-align: left;
  color: #6b7e91;
}
.fc-report-pop-btn-row {
  border-color: #39b2c2;
  padding: 7px;
  color: #39b2c2;
  font-weight: 400;
  font-size: 12px;
}
.fc-report-pop-btn-row i {
  padding-left: 10px;
  font-size: 12px;
}
.popupcontainer {
  padding-bottom: 12px;
}
.fc-report-popover-conetnt .title {
  padding: 5px;
  font-size: 12px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: 2.25;
  letter-spacing: 1.1px;
  text-align: left;
  color: #000000;
}
.comparepopover {
  margin-top: 18px;
  border-radius: 3px;
  background-color: #ffffff;
  box-shadow: 0 2px 9px 0 rgba(178, 178, 178, 0.5);
}
.comparepopover .q-list {
  padding: 0px !important;
}
.comparepopover .chart-label {
  font-size: 13px;
  line-height: 2.79;
  letter-spacing: 0.3px;
}
.comparepopover .q-item-separator {
  border-top: solid 1px #e6ebf0;
}
.compare-dialog .el-dialog__body {
  padding: 0px;
}
.no-chart-data {
  width: 100px;
  height: 100px;
}
.date-filter-comp button {
  right: 35px;
  top: 0px;
  font-size: 11px;
  padding: 8px;
  border: none;
  color: var(--fc-theme-color);
}
.date-filter-comp button:hover {
  /*color: #ef508f;*/
  color: var(--fc-theme-color);
  background: #ef508f50;
}
.edit-preview-block {
  display: block;
}
.report-chart-align {
  float: right;
  margin-top: -22px;
}
.chat-icon-align {
  float: right;
}
.reports-summary .button-row .p5,
.reports-summary.day-row .p5 {
  padding: 5px;
  padding-left: 0px;
  padding-right: 0px;
}
.reports-summary .cal-left-btn .date-arrow {
  padding-right: 0px;
  margin-right: -1px;
}
.second-class .fc-report-filter {
  display: none;
}
/* .second-class {
  border-top: 1px solid #eae8e8;
} */

@media print {
  .fc-underline {
    display: none;
  }
}
</style>
