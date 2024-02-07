<template>
  <div class="hourly-trend-widget">
    <div
      v-if="isLoading"
      class="loading-container d-flex justify-content-center height100"
    >
      <spinner :show="isLoading"></spinner>
    </div>
    <template v-else-if="!canLoadWeatherChart"
      ><div class="height100 d-flex flex-col justify-center align-center">
        <div>
          <inline-svg
            src="svgs/cardNodata"
            class="vertical-middle'"
            iconClass="icon icon-80"
          ></inline-svg>
        </div>
        <div class="fc-black3-16 self-center bold">
          {{ $t('space.sites.weather.no_weather_station') }}
        </div>
      </div>
    </template>
    <template v-else>
      <div class="bold pT20 pL20 f18">
        {{ $t('space.sites.weather.hourly_trend') }}
      </div>
      <el-tabs v-model="activeTab">
        <el-tab-pane
          v-for="(tab, index) in chartTabs"
          :name="tab.name"
          :label="tab.label"
          :key="`${tab.name}-${index}`"
          :lazy="true"
        >
          <template v-if="activeTab === tab.name">
            <FNewAnalyticReport
              v-if="$getProperty(analyticsConfig, 'params')"
              :key="`${tab.name}-${index}`"
              ref="siteSummaryChart"
              :config="analyticsConfig"
              :moduleName="moduleName"
            ></FNewAnalyticReport>
          </template>
        </el-tab-pane> </el-tabs
    ></template>
  </div>
</template>

<script>
import FNewAnalyticReport from 'pages/energy/analytics/components/FNewAnalyticReport'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'

export default {
  components: { FNewAnalyticReport },
  props: ['details', 'moduleName'],
  data() {
    return {
      isLoading: false,
      stationId: -1,
      isWeatherStationAssociated: true,
      weatherModule: {},
      weatherReadingFields: {},
      activeTab: 'temperature',
      chartTabs: [
        { name: 'temperature', label: 'Temperature' },
        // { name: 'precipitationProbability', label: 'Precipitation' },
        { name: 'windSpeed', label: 'Wind' },
        { name: 'humidity', label: 'Humidity' },
      ],
      analyticsConfigDefault: {
        analyticsType: 6,
        hidechartoptions: true,
        hidetabular: true,
        hidecharttypechanger: true,
        hidedatepicker: true,
        fixedChartHeight: 250,
        isFromAlarmSummary: false,
        isFromSiteSummary: true,
        hideAlarmSubject: true,
        showAlarms: false,
        groupByMetrics: true,
        showWidgetLegends: false,
        customizeChartOptions: {
          dataPoints: [],
          axis: {
            x: { label: { text: '' } },
            y: { label: { text: '' } },
          },
          legend: { show: false },
        },
      },
    }
  },
  computed: {
    canLoadWeatherChart() {
      let params = this.$getProperty(this, 'analyticsConfig.params')
      return this.isWeatherStationAssociated && !isEmpty(params)
    },
    analyticsConfig() {
      let { stationId } = this
      let {
        weatherReadingFields = [],
        activeTab,
        analyticsConfigDefault = {},
        startTime,
        endTime,
      } = this
      let canSetConfig =
        stationId && !isEmpty(activeTab) && !isEmpty(weatherReadingFields)
      if (canSetConfig) {
        let fields = []
        let {
          displayName: name,
          dataType,
          fieldId,
          name: actual,
          weatherModule,
        } = weatherReadingFields[activeTab] || {}
        let field = {
          parentId: [stationId],
          name,
          yAxis: { dataType, fieldId, aggr: 2 },
          aliases: { actual },
          type: 1,
          duplicateDataPoint: false,
        }
        fields.push(field)
        fields = JSON.stringify(fields)
        let params = {
          mode: 1,
          startTime,
          endTime,
          fields,
          xAggr: 20,
          showSafeLimit: true,
          analyticsType: 6,
          newFormat: true,
        }
        analyticsConfigDefault.params = params
        let value = []
        value[0] = startTime
        value[1] = endTime
        analyticsConfigDefault.dateFilter = {
          operatorId: 62,
          operationOnId: 1,
          operationOn: 'day',
          value,
          offset: -1,
        }
        let dataPoints = [
          {
            aggr: 2,
            safelimit: false,
            visible: true,
            type: 'datapoint',
            axes: 'y',
            chartType: '',
            pointType: 1,
            dataType: 'DECIMAL',
            dataTypeId: 3,
            xDataPoint: false,
            duplicateDataPoint: false,
            parentId: 1,
            label: name,
            name: name,
            key: actual,
            alias: actual,
            fieldName: actual,
            color: '#3ab2c1',
            fieldId: fieldId,
            moduleName: weatherModule?.name,
            moduleId: weatherModule?.moduleName,
          },
        ]
        this.$setProperty(
          analyticsConfigDefault,
          'customizeChartOptions.dataPoints',
          dataPoints
        )
        if (
          activeTab === 'precipitationProbability' ||
          activeTab === 'humidity'
        ) {
          this.$setProperty(
            analyticsConfigDefault,
            'customizeChartOptions.type',
            'area-step'
          )
        } else {
          this.$setProperty(
            analyticsConfigDefault,
            'customizeChartOptions.type',
            'area'
          )
        }
        return analyticsConfigDefault
      }
      return {}
    },
    startTime() {
      return this.$helpers.getOrgMoment().valueOf()
    },
    endTime() {
      return this.$helpers.getOrgMoment().valueOf() + 24 * 60 * 60 * 1000
    },
  },
  watch: {},
  created() {
    this.getStationAndLoadMeta('newWeather')
  },
  methods: {
    async getStationAndLoadMeta() {
      this.isLoading = true
      let siteId = this.$getProperty(this, 'details.id')
      try {
        let { data, error } = await API.get('v3/weather/sitestationid', {
          siteId,
        })
        if (error) {
          this.stationId = -1
        } else {
          this.stationId = this.$getProperty(data, 'stationId', -1)
        }
      } catch (e) {
        this.stationId = -1
      }
      if (!isEmpty(this.stationId)) {
        await this.loadModuleMeta('newWeather')
      } else {
        this.isWeatherStationAssociated = false
      }
      this.isLoading = false
    },
    async loadModuleMeta(moduleName) {
      let { data, error } = await API.get('/module/meta', { moduleName })
      if (error) {
        this.weatherModule = {}
        this.weatherReadingFields = {}
      } else {
        this.weatherModule = {}
        this.weatherReadingFields = {}
        if (!isEmpty(data)) {
          let { meta } = data || {}
          let { fields = [], module } = meta || {}
          let fieldsMap = {}
          fields.forEach(field => {
            fieldsMap[field.name] = field
          })
          this.weatherReadingFields = fieldsMap
          this.weatherModule = module
        }
      }
    },
  },
}
</script>

<style lang="scss">
.hourly-trend-widget {
  .analytics-report-empty-state {
    padding: 0px !important;
  }
  .el-tabs__nav-wrap {
    padding-left: 20px !important;
  }
}
</style>
