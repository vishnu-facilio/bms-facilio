<template>
  <shimmer-loading v-if="wloading"> </shimmer-loading>
  <div v-else>
    <div class="dragabale-card h100 row" v-if="!loading">
      <div
        class="col-12 h50 weather text-left"
        :class="weatherEnumMap[data.weatherBg]"
      >
        <div class="pL20 weather-layou1">
          <span>
            <img
              v-if="data.icon === 2"
              class="svg-icon sun-icon"
              src="~statics/weathericons/night.svg"
            />
            <img
              v-else-if="data.icon === 1"
              class="svg-icon sun-icon"
              src="~statics/weathericons/sunny-day.svg"
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
              v-else-if="data.icon === 2"
              class="svg-icon sun-icon"
              src="~statics/weathericons/night.svg"
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
              >{{ data.temperature }}<span>&ordm;</span
              ><span style="font-weight: 400;font-size: 20px;">{{
                data.unit
              }}</span
              ><span class="sub-text"
                >&nbsp;{{ summaryBuild(data.icon) }}</span
              ></span
            >
          </span>
          <div class="month">{{ getTime(time) }}</div>
          <div class="area pT5 uppercase">{{ buildingDetails.data.city }}</div>
        </div>
      </div>
      <div class="col-12 h50 carbon-weather text-left" style="height:52%;">
        <div class="pL20 weather-layout">
          <div>
            <img class="svg-icon sun-icon carbon-icon" src="~statics/co2.svg" />
            <span class="degree"
              >{{ data.carbon.value
              }}<span class="sub-text">{{ data.carbon.unit }}</span></span
            >
          </div>
          <div class="month">{{ data.month }}</div>
          <div class="area uppercase">{{ $t('panel.layout.carbon') }}</div>
        </div>
      </div>
    </div>
    <div
      class="dragabale-card h100 row"
      :class="weatherEnumMap[data.weatherBg]"
      v-else
    ></div>
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
      time: 0,
      demoMode: false,
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
    this.loadBuildingDetails()
  },
  computed: {
    today() {
      return this.$options.filters.formatDate(new Date(), true, false)
    },
    buildingId() {
      if (this.currentDashboard) {
        if (this.currentDashboard.linkName === 'dsoem1dailyconsumption') {
          return 616227
        }
        return this.currentDashboard.buildingId
      }
      if (this.$route.params.dashboardlink === 'dsoem1dailyconsumption') {
        return 616227
      }
      if (this.$route.params.buildingid) {
        return parseInt(this.$route.params.buildingid)
      } else {
        return false
      }
    },
  },
  methods: {
    weekly(period) {
      return this.$helpers.weekly(period)
    },
    initData() {
      let self = this
      self.loading = true
      self.wloading = true
      let params = {
        widgetId: self.widget.id,
        reportSpaceFilterContext: { buildingId: self.buildingId },
      }
      self.prepareDemoData()
      self.$http
        .post('dashboard/getCardData', params)
        .then(function(response) {
          self.data = self.prepareData(response.data.cardResult.weather)
          self.time = response.data.cardResult.weather.actualTtime
          self.data.carbon = self.prepareEmission(
            response.data.cardResult.carbonEmission
          )
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
        .format('dddd, DD MMMM YYYY hh:mm')
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
    getTime(time) {
      if (time) {
        return this.$options.filters.formatDate(time, false, false)
      } else {
        return ''
      }
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
      data.unit = data.unit ? data.unit : 'C'
      return data
    },
    prepareEmission(data) {
      if (data) {
        if (data > 1000) {
          let d = {}
          d.value = (data / 1000).toFixed(2)
          d.unit = 'Tons'
          return d
        } else {
          let d = {}
          d.value = data.toFixed(2)
          d.unit = 'Kg'
          return d
        }
      } else {
        return {
          value: '',
          unit: '',
        }
      }
    },
    loadBuildingDetails() {
      let self = this
      self.buildingDetails.loading = true
      self.wloading = true
      const formData = new FormData()
      formData.append('buildingId', self.buildingId)
      self.$http
        .post('/report/energy/building/getBuildingDetails', formData)
        .then(function(response) {
          let reportData = response.data.reportData
          self.buildingDetails.data = reportData
          self.buildingDetails.arrow = self.$helpers.arrowHandleClass(
            reportData.variance
          )
          self.purpose = reportData.purpose
          self.buildingDetails.loading = false
          self.wloading = false
        })
        .catch(function(error) {
          self.buildingDetails.loading = false
          self.wloading = false
        })
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
.dashboard-container .weather {
  background-image: linear-gradient(to bottom, #10022d, #1d1759);
}
.h100 {
  height: 100%;
}
.h50 {
  height: 50%;
}
.weather .area {
  font-size: 1vw;
  font-weight: bold;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 2.3px;
  text-align: left;
  color: #ffffff;
}
.weather .degree {
  font-size: 2.5vw;
  font-weight: 300;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.2px;
  text-align: center;
  color: #ffffff;
}
.weather .month,
.carbon-weather .month {
  font-size: 1vw;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.3px;
  text-align: left;
  color: #ffffff;
  padding-top: 10px;
  padding-bottom: 3px;
}
.weather .sub-text,
.carbon-weather .sub-text {
  font-size: 1.1vw;
  font-weight: 500;
}
.weather-layout {
  padding-left: 30px;
  position: absolute;
  top: 9%;
  width: 100%;
}
.sun-icon {
  text-align: center;
  width: 35px;
  padding-top: 0px;
  position: relative;
  top: 7px;
}
.carbon-icon {
  text-align: center;
  width: 40px;
  padding-top: 0px;
  position: relative;
  top: 7px;
}
.carbon-weather .degree {
  font-size: 2.5vw;
  font-weight: 300;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: -0.8px;
  text-align: left;
  color: #ffffff;
}
.carbon-weather .area {
  font-size: 1vw;
  font-weight: bold;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 1.5px;
  text-align: left;
  color: #ffffff;
  padding-top: 6px;
}
.carbon-weather .sub-text {
  font-size: 0.8vw;
  padding-left: 10px;
}
.carbon-weather {
  height: 52%;
  position: absolute;
  width: 100%;
  top: 48%;
}
.weather {
  position: absolute;
  width: 100%;
}
.weather-layou1 {
  padding-left: 30px;
  padding-top: 5px;
  position: absolute;
  top: 5%;
  width: 100%;
}
.dashboard-container .clear-day {
  background-image: linear-gradient(to bottom, #f3bf5f, #e79827);
}
.dashboard-container .clear-night {
  background-image: linear-gradient(to bottom, #10022d, #1d1759);
}
.dashboard-container .rain {
  background-image: linear-gradient(to bottom, #1d2f4d, #0c4c69);
}
.dashboard-container .snow {
  background-image: linear-gradient(to bottom, #56c1d8, #00829c);
}
.dashboard-container .sleet {
  background-image: linear-gradient(to bottom, #057a93, #3b7591);
}
.dashboard-container .wind {
  background-image: linear-gradient(to bottom, #134065, #125b89);
}
.dashboard-container .fog {
  background-image: linear-gradient(to bottom, #005482, #13849c);
}
.dashboard-container .cloudy {
  background-image: linear-gradient(to bottom, #8abead, #008fab);
}
.dashboard-container .partly-cloudy-day {
  background-image: linear-gradient(to left, #8abead, #008fab);
}
.dashboard-container .partly-cloudy-night {
  background-image: linear-gradient(to left, #8abead, #008fab);
}
.dashboard-container .hail {
  background-image: linear-gradient(to left, #8abead, #008fab);
}
</style>
