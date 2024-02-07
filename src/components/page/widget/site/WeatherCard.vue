<template>
  <div v-if="loading">
    <spinner :show="true"></spinner>
  </div>
  <div
    v-else-if="$validation.isEmpty(weatherCardData)"
    class="d-flex flex-direction-column justify-content-even site-weather-card p30"
  >
    <div class="d-flex flex-direction-column align-center">
      <div>
        <inline-svg
          src="svgs/cardNodata"
          class="vertical-middle'"
          iconClass="icon icon-80"
        ></inline-svg>
      </div>
      <div class="fc-black3-16 self-center bold">
        {{ $t('space.sites.no_weather_data') }}
      </div>
    </div>
  </div>
  <div
    v-else
    class="d-flex flex-direction-column justify-content-even site-weather-card p30"
  >
    <div class="d-flex flex-direction-row">
      <div class="">
        <inline-svg
          :src="currentWeather.icon"
          class="vertical-middle'"
          iconClass="icon icon-xxlg"
        ></inline-svg>
      </div>
      <div class="temperature-text pL20">{{ temperature }}&ordm;{{ unit }}</div>
      <div class="weather-text self-center pL10">
        {{ currentWeather.label }}
      </div>
    </div>
    <div class="d-flex flex-direction-row">
      <div>{{ $options.filters.toDateFormat(time, 'h:mm a') }}</div>
      <div class="mL10 mR10 border-right"></div>
      <div>
        {{ $options.filters.toDateFormat(time, 'dddd, DD MMMM YYYY') }}
      </div>
    </div>
  </div>
</template>
<script>
const weatherData = {
  default: {
    class: 'partly-cloudy-day',
    label: 'Clear Day',
    icon: 'svgs/cloudy',
  },
  1: {
    class: 'clear-day',
    label: 'Clear Day',
    icon: 'svgs/spacemanagement/weathericons/sunny-day',
  },
  2: {
    class: 'clear-night',
    label: 'Clear Night',
    icon: 'svgs/spacemanagement/weathericons/clear-night',
  },
  3: {
    class: 'rain',
    label: 'Rain',
    icon: 'svgs/spacemanagement/weathericons/rainy-day',
  },
  4: {
    class: 'snow',
    label: 'Snow',
    icon: 'svgs/spacemanagement/weathericons/snow-cloud',
  },
  5: {
    class: 'sleet',
    label: 'Sleet',
    icon: 'svgs/spacemanagement/weathericons/sleet',
  },
  6: {
    class: 'wind',
    label: 'Wind',
    icon: 'svgs/spacemanagement/weathericons/windy-day',
  },
  7: {
    class: 'fog',
    label: 'Fog',
    icon: 'svgs/spacemanagement/weathericons/foggy',
  },
  8: {
    class: 'cloudy',
    label: 'Cloudy',
    icon: 'svgs/spacemanagement/weathericons/cloudy-day',
  },
  9: {
    class: 'partly-cloudy-day',
    label: 'Partly Cloudy Day',
    icon: 'svgs/spacemanagement/weathericons/cloudy-day',
  },
  10: {
    class: 'partly-cloudy-night',
    label: 'Partly Cloudy Night',
    icon: 'svgs/spacemanagement/weathericons/cloudy-day',
  },
  11: {
    class: 'hail',
    label: 'Hail',
    icon: 'svgs/spacemanagement/weathericons/hail',
  },
  12: {
    class: 'thunderstorm',
    label: 'Thunderstorms',
    icon: 'svgs/spacemanagement/weathericons/hail',
  },
  13: {
    class: 'tornado',
    label: 'Tornado',
    icon: 'svgs/spacemanagement/weathericons/hail',
  },
}
export default {
  props: ['details'],
  data() {
    return {
      readingUnit: null,
      temperature: null,
      time: null,
      weatherCardData: {},
      loading: false,
    }
  },
  created() {
    this.loadWeatherData()
  },
  computed: {
    unit() {
      return this.readingUnit || this.orgUnit
    },
    orgUnit() {
      let { orgUnitsList } = this.$getProperty(this, '$account.appProps')
      if (orgUnitsList && orgUnitsList.length) {
        let tempObj = orgUnitsList.find(rt => rt.metricEnum === 'TEMPERATURE')
        return tempObj && tempObj.unit === 5 ? 'F' : 'C'
      }
      return 'C'
    },
    currentWeather() {
      let { weatherCardData = {} } = this
      let { icon } = weatherCardData || {}
      return weatherData[icon || 'default']
    },
  },
  methods: {
    loadWeatherData() {
      let { details = {} } = this
      let { id } = details
      let params = {
        staticKey: 'weathermini',
        baseSpaceId: id,
      }
      this.loading = true
      this.$http
        .post('/dashboard/getCardData', params)
        .then(({ data: { cardResult = {} }, status }) => {
          if (status === 200) {
            let { weather = {} } = cardResult
            let { actualTtime, temperature, unit } = weather
            this.$set(this, 'weatherCardData', weather)
            this.$set(this, 'time', actualTtime)
            this.$set(this, 'temperature', Math.round(temperature))
            this.$set(this, 'readingUnit', unit)
          }
          this.loading = false
        })
        .catch(
          ({ message = 'Error Occurred while fetching weather card data' }) => {
            this.$message.error(message)
            this.loading = false
          }
        )
    },
  },
}
</script>
