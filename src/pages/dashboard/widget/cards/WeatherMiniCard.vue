<template>
  <shimmer-loading v-if="wloading" class="card-shimmer"> </shimmer-loading>
  <div class="dragabale-card h100 user-layout weather-mini-card" v-else>
    <div
      class="col-12 h100 weather text-left"
      :class="weatherEnumMap[data.weatherBg] || 'default'"
    >
      <div class="pL20 weather-layou3 weather-layou3-mini pT15">
        <span>
          <img
            v-if="data.icon === 1"
            class="svg-icon sun-icon"
            src="~statics/weathericons/sunny-day.svg"
          />
          <img
            v-else-if="data.icon === 2"
            class="svg-icon sun-icon"
            src="~statics/weathericons/night.svg"
          />
          <img
            v-else-if="data.icon === 3"
            class="svg-icon sun-icon"
            src="~statics/weathericons/rainy-day.svg"
          />
          <img
            v-else-if="data.icon === 4"
            class="svg-icon sun-icon"
            src="~statics/weathericons/snow-cloud.svg"
          />
          <img
            v-else-if="data.icon === 5"
            class="svg-icon sun-icon"
            src="~statics/weathericons/sleet.svg"
          />
          <img
            v-else-if="data.icon === 6"
            class="svg-icon sun-icon"
            src="~statics/weathericons/windy-day.svg"
          />
          <img
            v-else-if="data.icon === 7"
            class="svg-icon sun-icon"
            src="~statics/weathericons/foggy.svg"
          />
          <img
            v-else-if="data.icon === 8"
            class="svg-icon sun-icon"
            src="~statics/weathericons/cloudy-day.svg"
          />
          <img
            v-else-if="data.icon === 9"
            class="svg-icon sun-icon"
            src="~statics/weathericons/partialy-cloudy.svg"
          />
          <img
            v-else-if="data.icon === 10"
            class="svg-icon sun-icon"
            src="~statics/weathericons/partialy-cloudy.svg"
          />
          <img
            v-else-if="data.icon === 11"
            class="svg-icon sun-icon"
            src="~statics/weathericons/hail.svg"
          />
          <img
            v-else
            class="svg-icon sun-icon"
            src="~statics/weathericons/sunny-day.svg"
          />

          <span class="degree"
            ><span class="mini-weather">{{ data.temperature }}</span
            ><span class="deg-mini">&ordm;</span
            ><span class="weather-mini-unit">{{ data.unit }}</span></span
          >
          <div
            class="sub-text mT10"
            style="font-weight: 500;font-size: 1vw;margin-left: -5px;"
          >
            &nbsp;{{ summaryBuild(data.icon) }}
          </div>
        </span>
        <!-- <div class="area uppercase weather-mini-title">weather</div> -->
        <div class="month mT10 line-height20 weather-time">
          {{ getTime(time) }}
        </div>
        <div class="area pT10 uppercase bold">
          {{ buildingDetails.data.city }}
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import moment from 'moment'
import shimmerLoading from '@/ShimmerLoading'
export default {
  props: ['widget', 'currentDashboard'],
  data() {
    return {
      loading: false,
      data: [],
      weatherBg: 2,
      demoMode: false,
      building: null,
      time: 0,
      weatherEnumMap: {
        1: 'clear-day',
        2: 'clear-night',
        3: 'rain',
        4: 'snow',
        5: 'sleet',
        6: 'wind',
        7: 'fog',
        8: 'cloudy',
        9: 'partly-cloudy-day',
        10: 'partly-cloudy-night',
        11: 'hail',
        12: 'thunderstorm',
        13: 'tornado',
      },
      buildingDetails: {
        loading: true,
        data: [],
      },
      wloading: true,
    }
  },
  components: {
    shimmerLoading,
  },
  mounted() {
    this.initData()
  },
  computed: {
    today() {
      return moment()
        .tz(this.$timezone)
        .format('DD MMM YYYY hh:mm a')
    },
    buildingId() {
      return this.building ? this.building : null
    },
  },
  methods: {
    weekly(period) {
      return this.$helpers.weekly(period)
    },
    getTime(time) {
      if (time) {
        return this.$options.filters.formatDate(time, false, false)
      } else {
        return ''
      }
    },
    initData() {
      let self = this
      self.loading = true
      self.wloading = true
      let params = null
      if (this.widget.dataOptions.building) {
        this.building = this.widget.dataOptions.building
        params = {
          staticKey: 'weathermini',
          baseSpaceId: this.building.id,
        }
      } else {
        params = {
          widgetId: self.widget.id,
        }
      }
      self.$http
        .post('dashboard/getCardData', params)
        .then(function(response) {
          if (
            response.data &&
            response.data.cardResult &&
            response.data.cardResult.weather
          ) {
            self.time = response.data.cardResult.weather.actualTtime
            self.data = self.prepareData(response.data.cardResult.weather)
          } else {
            self.prepareDemoData()
          }
          self.loading = false
          self.demoMode = false
          self.wloading = false
        })
        .catch(function(error) {
          self.wloading = false
        })
      self.loading = false
    },
    prepareDemoData() {
      let self = this
      self.data.date = moment()
        .tz(this.$timezone)
        .format('DD MMM YYYY hh:mm a')
      self.data.month = 'This Month'
      self.weatherBg = 1
      self.data.icon = 1
      self.data.temperature = '32'
      self.data.summary = 'clear day'
      self.data.carbon = {
        value: 9.68,
        unit: 'Tons',
      }
      self.demoMode = true
    },
    prepareData(data) {
      let time = data
        ? data.ttime
        : moment()
            .tz(this.$timezone)
            .valueOf()
      data.date = moment(time)
        .tz(this.$timezone)
        .format('dddd, DD MMMM YYYY hh:mm')
      let month = moment(time)
        .tz(this.$timezone)
        .format('MMMM')
      let localmonth = moment()
        .tz(this.$timezone)
        .format('MMMM')
      let lastmonth = moment()
        .tz(this.$timezone)
        .add(-1, 'month')
        .format('MMMM')
      if (month === localmonth) {
        data.month = 'This Month'
      } else if (month === lastmonth) {
        data.month = 'Last Month'
      } else {
        data.month = month
      }
      this.weatherBg = data.icon ? data.icon : 1
      data.weatherBg = data.icon ? data.icon : 1
      data.location = this.$store.getters['space/getSiteById'](data.parentId)
      data.unit = data.unit ? data.unit : this.getUnit()
      return data
    },
    getUnit() {
      let { appProps } = this.$account
      if (appProps) {
        let { orgUnitsList } = appProps
        if (orgUnitsList && orgUnitsList.length) {
          let tempObj = orgUnitsList.find(rt => rt.metricEnum === 'TEMPERATURE')
          return tempObj && tempObj.unit === 5 ? 'F' : 'C'
        }
      }
      return 'C'
    },
    summaryBuild(data) {
      if (data && this.weatherEnumMap[data]) {
        data = this.weatherEnumMap[data].split('-').join(' ')
        data = data.replace(/(^|\s)[a-z]/g, function(f) {
          return f.toUpperCase()
        })
        return data
      } else {
        return 'clear-day'
      }
    },
  },
}
</script>
<style>
.user-layout .el-icon-close:before {
  content: '';
}
.setting-header .add-btn {
  position: fixed;
  right: 0;
  z-index: 111;
}
.user-layout .setting-page-btn {
  top: 7.5rem !important;
}
.user-layout .setting-Rlayout {
  padding: 1rem 1.7rem !important;
}
.add-btn {
  position: relative;
  right: 21px;
  top: -47px;
  z-index: 1111;
}
.h100 {
  height: 100% !important;
}
.weather-time {
  font-size: 1vw;
  font-weight: 500;
}
.weather-layou3-mini img {
  width: 3vw;
  position: relative;
  top: 9px;
}
.weather-layou3-mini .degree {
  margin-left: 10px;
}
.mini-weather {
  font-size: 2.5vw;
  padding-right: 10px;
  font-weight: 300;
}
.deg-mini {
  position: relative;
  bottom: 3px;
  left: -2px;
  font-weight: 300;
  font-size: 2.2vw;
}
.weather-mini-card .default {
  background: linear-gradient(217deg, #e6e0a5, #53bec8);
}
.weather-mini-card .clear-day {
  background-image: linear-gradient(to bottom, #f3bf5f, #e79827);
}
.weather-mini-card .clear-night {
  background-image: linear-gradient(to bottom, #10022d, #1d1759);
}
.weather-mini-card .rain {
  background-image: linear-gradient(to bottom, #1d2f4d, #0c4c69);
}
.weather-mini-card .snow {
  background-image: linear-gradient(to bottom, #56c1d8, #00829c);
}
.weather-mini-card .sleet {
  background-image: linear-gradient(to bottom, #057a93, #3b7591);
}
.weather-mini-card .wind {
  background-image: linear-gradient(to bottom, #134065, #125b89);
}
.weather-mini-card .fog {
  background-image: linear-gradient(to bottom, #005482, #13849c);
}
.weather-mini-card .cloudy {
  background-image: linear-gradient(to bottom, #8abead, #008fab);
}
.weather-mini-card .partly-cloudy-day {
  background-image: linear-gradient(to left, #8abead, #008fab);
}
.weather-mini-card .partly-cloudy-night {
  background-image: linear-gradient(to left, #8abead, #008fab);
}
.weather-mini-card .hail {
  background-image: linear-gradient(to left, #8abead, #008fab);
}
.weather-mini-title {
  position: absolute;
  top: 18%;
  left: 35%;
  font-weight: 600;
  letter-spacing: 1px;
}
.weather-mini-unit {
  font-weight: 400;
  font-size: 1.8vw;
}
@media only screen and (min-width: 1800px) {
  .weather-layou3-mini {
    padding-top: 20px;
    text-align: center;
    padding-left: 0;
  }
}
</style>
