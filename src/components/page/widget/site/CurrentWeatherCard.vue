<template>
  <div v-if="loading">
    <spinner :show="true"></spinner>
  </div>
  <div v-else-if="$validation.isEmpty(currentWeatherData)" class="height100">
    <div class="height100 d-flex flex-col justify-center align-center">
      <div>
        <inline-svg
          src="svgs/cardNodata"
          class="vertical-middle'"
          iconClass="icon icon-80"
        ></inline-svg>
      </div>
      <div class="fc-black3-16 self-center bold">
        {{ emptyStateMsg }}
      </div>
    </div>
  </div>
  <div v-else class="height100 current-weather-widget">
    <div class="current-weather-header p20 pT15 pB10 border-bottom14 ">
      <p class="f18 bold mB10">
        {{ $t('space.sites.weather.current_weather') }}
      </p>
      <div class="d-flex flex-direction-row bold mT15 mB10">
        <div>{{ $t('common._common.today') }},</div>
        <div>{{ getFormatedTime(currentWeatherData) }}</div>
      </div>
    </div>
    <div
      class="current-weather-body height100 p30 flex-center-column justify-around"
    >
      <div class="weather-icon">
        <div class="">
          <fc-icon
            group="default"
            :name="iconSrc"
            size="50"
            color="#3ab2c2"
          ></fc-icon>
        </div>
      </div>
      <div class="weather-temperature">
        <div class="temperature-text">
          {{ temperature }}
        </div>
      </div>
      <div class="weather-label">
        <div class="weather-text self-center f18 fw4">
          {{ $getProperty(currentWeatherData, 'summary', '---') }}
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import { getTimeInOrgFormat } from 'src/util/helpers'
import { getIconSrc } from './utils/WeatherUtils'

export default {
  props: ['details'],
  data() {
    return {
      currentWeatherData: {},
      loading: false,
      stationAssociated: true,
      temperatureUnit: '',
    }
  },
  created() {
    this.init()
  },
  computed: {
    emptyStateMsg() {
      let { stationAssociated } = this
      return stationAssociated
        ? this.$t('space.sites.no_weather_data')
        : this.$t('space.sites.weather.no_weather_station')
    },
    iconSrc() {
      let { icon } = this.currentWeatherData || {}
      return getIconSrc(icon)
    },
    temperature() {
      let { currentWeatherData } = this || {}
      let currentTemperature = this.$getProperty(
        currentWeatherData,
        'temperature',
        '---'
      )
      let unit = this.$getProperty(currentWeatherData, 'unit', '\u00B0C')
      return `${currentTemperature}${unit}`
    },
  },
  methods: {
    async init() {
      await this.setUnitForTemperature('newWeather')
      await this.loadWeatherData()
    },
    async setUnitForTemperature(moduleName) {
      try {
        let { data, error } = await API.get('/module/meta', { moduleName })
        if (!error) {
          if (!isEmpty(data)) {
            let { meta } = data || {}
            let { fields = [] } = meta || {}
            let fieldsMap = {}
            fields.forEach(field => {
              fieldsMap[field.name] = field
            })
            this.temperatureUnit = fieldsMap['temperature']?.unit
          }
        }
      } catch (e) {
        this.temperatureUnit = '\u00B0C'
      }
    },
    async loadWeatherData() {
      let { details = {}, temperatureUnit = '\u00B0C' } = this
      let { id: siteId } = details || {}
      let params = {
        siteId,
      }
      this.loading = true
      try {
        let { data, error } = await API.get('v3/weather/currentdata', params)
        if (!isEmpty(error)) {
          this.currentWeatherData = {}
          if (error.code === -1) {
            this.stationAssociated = false
          }
        } else {
          this.currentWeatherData = {}
          if (!isEmpty(data)) {
            let {
              actualTtime: time,
              temperature,
              unit = temperatureUnit,
              icon = 'default',
              summary = '---',
            } = data || {}
            if (!isEmpty(temperature)) {
              temperature = Math.round(temperature)
              this.currentWeatherData = {
                time,
                temperature,
                unit,
                icon,
                summary,
              }
            }
          }
        }
      } catch (e) {
        this.currentWeatherData = {}
      }
      this.loading = false
    },
    getFormatedTime({ time }) {
      return getTimeInOrgFormat(time)
    },
  },
}
</script>

<style lang="scss" scoped>
.current-weather-widget {
  .weather-temperature {
    .temperature-text {
      font-size: 48px;
      font-weight: 500;
    }
  }
}
</style>
