<template>
  <div class="dragabale-card height100">
    <div
      :class="[
        'cb-card-container d-flex flex-direction-column p20 weather-card',
        currentWeather.class,
      ]"
    >
      <div>
        <inline-svg
          :src="currentWeather.icon"
          :class="['vertical-middle', isDataEmpty && 'op8']"
          iconClass="icon weather-icon"
        ></inline-svg>
        <div v-if="!isDataEmpty" class="mL10 degree inline vertical-middle">
          <span>{{ formatIntergerDecimal(currentTemp) }}</span>
          <!-- <span class="deg-symbol">&ordm;</span> -->
          <span v-if="unit" class="weather-unit">{{ unit }}</span>
        </div>
      </div>
      <div v-if="!isDataEmpty" class="mT10 weather-label">
        {{ currentWeather.label }}
      </div>
      <div v-else class="mT15 weather-label op8">
        No Data Found
      </div>
      <div class="mT10 weather-time" v-if="time">
        {{ time }}
      </div>
      <div v-if="cardData.building" class="mT10 weather-time text-uppercase">
        {{ cardData.building.city }}
      </div>
    </div>
  </div>
</template>
<script>
const weatherData = {
  default: {
    class: 'partly-cloudy-day',
    label: 'Clear Day',
    icon: 'svgs/weathericons/sunny-day',
  },
  1: {
    class: 'clear-day',
    label: 'Clear Day',
    icon: 'svgs/weathericons/sunny-day',
  },
  2: {
    class: 'clear-night',
    label: 'Clear Night',
    icon: 'svgs/weathericons/night',
  },
  3: { class: 'rain', label: 'Rain', icon: 'svgs/weathericons/rainy-day' },
  4: { class: 'snow', label: 'Snow', icon: 'svgs/weathericons/snow-cloud' },
  5: { class: 'sleet', label: 'Sleet', icon: 'svgs/weathericons/sleet' },
  6: { class: 'wind', label: 'Wind', icon: 'svgs/weathericons/windy-day' },
  7: { class: 'fog', label: 'Fog', icon: 'svgs/weathericons/foggy' },
  8: { class: 'cloudy', label: 'Cloudy', icon: 'svgs/weathericons/cloudy-day' },
  9: {
    class: 'partly-cloudy-day',
    label: 'Partly Cloudy Day',
    icon: 'svgs/weathericons/partialy-cloudy',
  },
  10: {
    class: 'partly-cloudy-night',
    label: 'Partly Cloudy Night',
    icon: 'svgs/weathericons/partialy-cloudy',
  },
  11: { class: 'hail', label: 'Hail', icon: 'svgs/weathericons/hail' },
  12: {
    class: 'thunderstorm',
    label: 'Thunderstorms',
    icon: 'svgs/weathericons/hail',
  },
  13: { class: 'tornado', label: 'Tornado', icon: 'svgs/weathericons/hail' },
}

import { isEmpty } from '@facilio/utils/validation'
import moment from 'moment'
import BaseCard from 'pages/card-builder/cards/common/BaseCard'

export default {
  extends: BaseCard,
  data() {
    return {}
  },
  computed: {
    subscriptionParams() {
      let { cardParams } = this

      if (isEmpty(cardParams)) return []
      return {
        readings: [
          {
            moduleName: 'weather',
            fieldName: 'temperature',
            parentId: cardParams.baseSpaceId,
          },
        ],
      }
    },
    isDataEmpty() {
      return isEmpty(this.$getProperty(this, 'cardData.value.value'))
    },
    currentWeather() {
      let { icon, summary } =
        this.$getProperty(this, 'cardData.value.value') || {}
      return isEmpty(summary)
        ? weatherData[icon || 'default']
        : { ...weatherData[icon || 'default'], label: summary }
    },
    currentTemp() {
      return this.$getProperty(this, 'cardData.value.value.temperature') || '--'
    },
    unit() {
      return this.$getProperty(this, 'cardData.value.unit') || this.orgUnit
    },
    orgUnit() {
      let { orgUnitsList } = this.$getProperty(this, '$account.appProps')
      if (orgUnitsList && orgUnitsList.length) {
        let tempObj = orgUnitsList.find(rt => rt.metricEnum === 'TEMPERATURE')
        return tempObj && tempObj.unit === 5 ? 'F' : 'C'
      }
      return 'C'
    },
    time() {
      let { actualTtime } =
        this.$getProperty(this, 'cardData.value.value') || {}
      if (actualTtime) {
        return this.$options.filters.formatDate(actualTtime, false, false)
        // moment(actualTtime)
        //   .tz(this.$timezone)
        //   .format('DD MMM YYYY hh:mm a')
      } else {
        return null
      }
    },
  },
}
</script>
<style lang="scss"></style>
