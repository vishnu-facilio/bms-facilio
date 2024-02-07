<template>
  <div ref="hourly-weather-table" class="site-weather-table">
    <div class="site-weather-table-header p20 pT15 pB15 border-bottom14">
      <p class="f18 bold mB0">
        {{ $t('space.sites.weather.hourly_forecast') }}
      </p>
    </div>
    <div class="site-weather-table-body height100">
      <div
        v-if="isLoading"
        class="loading-container d-flex justify-content-center height100"
      >
        <spinner :show="isLoading"></spinner>
      </div>
      <div
        v-else-if="$validation.isEmpty(weatherDataTimeList)"
        class="analytics-report-empty-state height100 fc-align-center-column"
      >
        <inline-svg
          src="svgs/emptystate/reportlist"
          iconClass="icon text-center icon-200"
        ></inline-svg>
        <div class="nowo-label f13">
          {{ $t('space.sites.weather.no_table_data') }}
          <!-- <span class="bold" v-if="resultObj && resultObj.dateRange">{{
          {{period}}
        }}</span> -->
        </div>
      </div>
      <template v-else
        ><el-collapse
          v-if="!$validation.isEmpty(weatherDataTimeList)"
          v-model="activeRow"
          @change="handleRowClick"
          accordion
          ref="el-collapse-container"
        >
          <el-collapse-item
            v-for="(time, index) in weatherDataTimeList"
            :key="index"
            :name="index"
          >
            <template slot="title">
              <div class="site-weather-table-row-header height100 width100">
                <el-row
                  class="flex-middle"
                  v-if="activeRow !== index"
                  :gutter="40"
                >
                  <el-col :span="3">
                    <div class="time">{{ getFormatedTime(time) }}</div>
                  </el-col>
                  <el-col :span="6">
                    <div class="weather-summary flex-middle">
                      <fc-icon
                        group="default"
                        :name="getCurrentWeatherData(time, 'icon')"
                        size="20"
                      ></fc-icon>
                      <p class="inline-block f14 fw4 m0 pL10 truncate-text">
                        {{ getCurrentWeatherData(time, 'summary') }}
                      </p>
                    </div>
                  </el-col>
                  <el-col :span="5">
                    <div class="temperature flex-middle">
                      <fc-icon group="default" name="temp" size="20"></fc-icon>
                      <p class="inline-block f14 fw4 m0 pL10 truncate-text">
                        {{ getCurrentWeatherData(time, 'temperature') }}
                      </p>
                    </div>
                  </el-col>
                  <!-- <el-col :span="3">
                    <div class="precipitation flex-middle">
                      <fc-icon
                        group="default"
                        name="precipitation"
                        size="20"
                      ></fc-icon>
                      <p class="inline-block f14 fw4 m0 pL10 truncate-text">
                        {{ getCurrentWeatherData(time, 'precipitation') }}
                      </p>
                    </div>
                  </el-col> -->
                  <el-col :span="5">
                    <div class="wind-speed flex-middle">
                      <fc-icon group="default" name="wind" size="20"></fc-icon>
                      <p class="inline-block f14 fw4 m0 pL10 truncate-text">
                        {{ getCurrentWeatherData(time, 'windSpeed') }}
                      </p>
                    </div>
                  </el-col>
                  <el-col :span="5">
                    <div class="humidity flex-middle">
                      <fc-icon
                        group="default"
                        name="humidity"
                        size="20"
                      ></fc-icon>
                      <p class="inline-block f14 fw4 m0 pL10 truncate-text">
                        {{ getCurrentWeatherData(time, 'humidity') }}
                      </p>
                    </div>
                  </el-col>
                </el-row>
                <template v-else>
                  <div class="height100 width100 flex">
                    <div>
                      <fc-icon
                        group="default"
                        :name="getCurrentWeatherData(time, 'icon')"
                        size="40"
                        class="mT10"
                        color="#3ab2c2"
                      ></fc-icon>
                    </div>
                    <div class="mL20 flex-center-vH">
                      <p class="m0 f14 fw4 primary-font">
                        {{ getFormatedTime(time) }}
                      </p>
                      <p class="m0 f16 bold primary-font">
                        {{ getCurrentWeatherData(time, 'summary') }}
                      </p>
                    </div>
                  </div>
                </template>
              </div>
            </template>
            <div class="site-weather-table-row-expand">
              <div
                v-for="(reading, index) in requiredReadingFields"
                class="temperature weather-content-box"
                :key="index"
              >
                <div class="reading-name-row">
                  <fc-icon
                    group="default"
                    :name="reading.icon"
                    size="18"
                    class="inline-block height20 facilio-icon-cont"
                  ></fc-icon>
                  <p class="inline-block f14 fw4 m0 pL10">
                    {{ reading.label }}
                  </p>
                </div>
                <p class="pL30 m0 f18 bold">
                  {{ getCurrentWeatherData(time, reading.key) }}
                </p>
              </div>
            </div>
          </el-collapse-item>
        </el-collapse>
      </template>
    </div>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import { getTimeInOrgFormat } from 'src/util/helpers'
import { WEATHER_ICONS } from './utils/WeatherUtils'
export default {
  props: ['details'],
  data() {
    return {
      WEATHER_ICONS: WEATHER_ICONS,
      isLoading: false,
      rowExpanded: false,
      activeRow: '',
      stationId: -1,
      weatherData: {},
      weatherReadingFields: {},
      weatherModule: {},
      weatherDataTimeList: [],
      requiredReadingFields: [
        {
          key: 'temperature',
          label: 'Temperature',
          icon: 'temp',
        },
        {
          key: 'dewPoint',
          label: 'Dew Point',
          icon: 'dew-point',
        },
        {
          key: 'windSpeed',
          label: 'Wind Speed',
          icon: 'wind',
        },
        {
          key: 'visibility',
          label: 'Visibility',
          icon: 'visibility',
        },
        {
          key: 'uvIndex',
          label: 'UV Index',
          icon: 'uv-index',
        },
        {
          key: 'pressure',
          label: 'Pressure',
          icon: 'pressure',
        },
        {
          key: 'humidity',
          label: 'Humidity',
          icon: 'humidity',
        },
        // {
        //   key: 'precipitationProbability',
        //   label: 'Precipitation',
        //   icon: 'precipitation',
        // },
      ],
    }
  },
  computed: {},
  async created() {
    await this.getStationAndLoadData()
    this.$nextTick(() => {
      this.resizeTableHeight({})
    })
  },
  methods: {
    async getStationAndLoadData() {
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
        await this.loadWeatherData()
      }
      this.isLoading = false
    },
    async loadModuleMeta(moduleName) {
      try {
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
      } catch (e) {
        this.weatherModule = {}
        this.weatherReadingFields = {}
      }
    },

    async loadWeatherData() {
      let { weatherReadingFields = {}, stationId } = this
      let startTime = this.$helpers.getOrgMoment().valueOf()
      let endTime = this.$helpers.getOrgMoment().valueOf() + 12 * 60 * 60 * 1000
      let fetchFields = [
        'temperature',
        'dewPoint',
        'windSpeed',
        'visibility',
        'uvIndex',
        'pressure',
        'humidity',
        'precipitationProbability',
        'icon',
        'summary',
      ]
      if (!isEmpty(weatherReadingFields)) {
        let fields = []
        fetchFields.forEach(fetchField => {
          let {
            displayName: name,
            dataType,
            fieldId,
            name: actual,
          } = weatherReadingFields[fetchField]
          if (fetchField === 'icon') dataType = 2
          let aggr = fetchField === 'summary' ? 4 : 2
          let field = {
            parentId: [stationId],
            name,
            yAxis: { dataType, fieldId, aggr },
            aliases: { actual },
            type: 1,
            duplicateDataPoint: false,
          }
          fields.push(field)
        })
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
        let { error, data } = await API.get('v2/report/readingReport', params)
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          if (!isEmpty(data)) {
            let { weatherData = {}, weatherDataTimeList = [] } = this
            let dataList = this.$getProperty(data, 'reportData.data', [])
            dataList.forEach(dataPoint => {
              if (!isEmpty(dataPoint?.X)) {
                weatherDataTimeList.push(dataPoint.X)
                weatherData[dataPoint.X] = dataPoint
              }
            })
          }
        }
      }
    },
    getCurrentWeatherData(time, key) {
      let { WEATHER_ICONS } = this
      let { unit = '' } = this.weatherReadingFields[key] || {}
      unit = isEmpty(unit) ? '' : unit
      let { weatherData = {} } = this
      let { [time]: currentWeatherGroup = {} } = weatherData
      if (!isEmpty(currentWeatherGroup[key])) {
        if (key === 'icon') {
          let IconKey = currentWeatherGroup[key] || 'default'
          IconKey = Math.floor(IconKey)
          return WEATHER_ICONS[IconKey]
        }
        return `${currentWeatherGroup[key]} ${unit}`
      }
      return key === 'icon' ? WEATHER_ICONS['default'] : '---'
    },
    getFormatedTime(timeStamp) {
      return getTimeInOrgFormat(timeStamp)
    },
    handleRowClick(activeName) {
      if (isEmpty(activeName)) {
        this.resizeTableHeight({ showMore: false })
        this.rowExpanded = false
      } else {
        this.resizeTableHeight({ showMore: true })
        this.rowExpanded = true
      }
    },
    resizeTableHeight({ showMore }) {
      let container = this.$refs['el-collapse-container']
      let containerElement = container?.$el
      if (!containerElement) return
      let height = containerElement.scrollHeight + 65
      let width = containerElement.scrollWidth
      if (!this.rowExpanded && !isEmpty(showMore) && showMore) {
        height += 245
      }
      if (!isEmpty(showMore) && !showMore) {
        height -= 245
      }
      height = height <= 370 ? 370 : height
      this.$attrs.resizeWidget({ height, width })
    },
  },
}
</script>

<style lang="scss">
.site-weather-table {
  .site-weather-table-body {
    .site-weather-table-row-expand {
      .reading-name-row {
        display: flex;
        align-items: center;
        .facilio-icon-cont {
          height: 20px;
          .fc-icon-svg-container {
            height: 20px;
          }
        }
      }
    }
    .el-collapse {
      border: 0px;
      .el-collapse-item {
        .el-collapse-item__header {
          &:not(.is-active):hover {
            background: #f0f8fa;
          }
          padding: 4px 20px;
          padding-left: 30px;
          height: 60px;
          &.is-active {
            padding: 20px;
            padding-left: 30px;
            height: 100px;
          }
        }
        .el-collapse-item__content {
          height: 200px;
          padding-left: 20px;
        }
      }
    }
  }
}
</style>

<style lang="scss" scoped>
.site-weather-table {
  .site-weather-table-body {
    overflow: scroll;
    .site-weather-table-row-expand {
      display: flex;
      flex-wrap: wrap;
      .weather-content-box {
        margin: 18px;
        width: 16%;
      }
    }
  }
}
</style>
