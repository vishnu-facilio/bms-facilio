<template>
  <div class="weather-station-setup-page fc-setup-page height100">
    <SetupHeader>
      <template #heading>
        {{ $t('weather.weather_station') }}
      </template>
      <template #description>
        {{ $t('weather.list_of_all_weather_station_and_service') }}
      </template>
      <template #actions>
        <div v-if="$route.name == 'weatherstation.list'" class="flex-middle">
          <div class="action-btn setting-page-btn mL20">
            <el-button
              type="primary"
              @click="addNewWeatherStation"
              class="setup-el-btn"
            >
              {{ $t('common._common.new') }} {{ currentTabName }}
            </el-button>
          </div>
        </div>
      </template>
      <template #tabs>
        <el-tabs class="fc-setup-list-tab" v-model="currentTab">
          <el-tab-pane
            :label="$t('weather.weather_station')"
            name="weatherstation.list"
          >
            <div class="mT10 mB20">
              <WeatherStationList
                :showAddWeatherStation="showAddWeatherStation"
                v-if="currentTabName === 'Weather Station'"
                @updateShowAddWeatherStation="
                  newVal => (showAddWeatherStation = newVal)
                "
              />
            </div>
          </el-tab-pane>
          <el-tab-pane
            :label="$t('weather.weather_service')"
            name="weatherservice.list"
          >
            <div class="mT10 mB20">
              <WeatherServiceList v-if="currentTabName === 'Weather Service'" />
            </div>
          </el-tab-pane>
        </el-tabs>
        <div class="weather-setup-tab-actions">
          <portal-target name="weather-setup-tab-actions"></portal-target>
        </div>
      </template>
    </SetupHeader>
  </div>
</template>

<script>
import SetupHeader from 'pages/setup/components/SetupHeaderTabs'
import WeatherStationList from './WeatherStationList'
import WeatherServiceList from './WeatherServiceList'

export default {
  name: 'WeatherStationSetup',
  components: {
    SetupHeader,
    WeatherStationList,
    WeatherServiceList,
  },
  data() {
    return {
      showAddWeatherStation: false,
    }
  },
  computed: {
    currentTab: {
      get() {
        return this.$route.name
      },
      set(selectedTabName) {
        this.handleTabChange(selectedTabName)
      },
    },
    currentTabName() {
      if (this.$route.name == 'weatherstation.list') {
        return this.$t('weather.weather_station')
      }
      return this.$t('weather.weather_service')
    },
  },
  methods: {
    changeRouteForSelectedTab(selectedTabName) {
      if (this.$route.name !== selectedTabName)
        this.$router.push({ name: selectedTabName })
    },
    handleTabChange(selectedTabName) {
      this.changeRouteForSelectedTab(selectedTabName)
    },
    addNewWeatherStation() {
      this.showAddWeatherStation = true
    },
  },
}
</script>

<style lang="scss" scoped>
.weather-station-setup-page {
  .weather-setup-tab-actions {
    display: flex;
    align-items: center;
    justify-content: right;
    position: absolute;
    top: 90px;
    right: 35px;
    height: 55px;
    width: 35%;
  }
}
</style>
