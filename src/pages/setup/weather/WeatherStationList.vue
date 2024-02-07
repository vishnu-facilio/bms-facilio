<template>
  <div class="weather-station-list">
    <portal to="weather-setup-tab-actions">
      <div class="search-and-pagination-container flex-middle">
        <WeatherStationSearch
          v-model="searchData"
          :isSearchDisabled="isSearchDisabled"
          @updateSearchData="loadWeatherStations"
        />
        <div v-if="weatherStationPageCount" class="vl"></div>
        <div class="pagination-container">
          <Pagination
            :total="weatherStationPageCount"
            :perPage="weatherStationsPerPage"
            class="nowrap"
            ref="f-page"
            :pageNo="weatherStationPage"
            @onPageChanged="setPage"
          >
          </Pagination>
        </div>
      </div>
    </portal>

    <SetupLoader v-if="isLoading">
      <template #setupLoading>
        <spinner :show="isLoading" size="80"></spinner>
      </template>
    </SetupLoader>
    <SetupEmpty v-else-if="$validation.isEmpty(weatherStations) && !isLoading">
      <template #emptyImage>
        <inline-svg src="svgs/copy2" iconClass="icon icon-sm-md"></inline-svg>
      </template>
      <template #emptyHeading>
        {{ $t('weather.no_weather_stations_available') }}
      </template>
      <template #emptyDescription> </template>
    </SetupEmpty>
    <el-table
      v-else
      :data="weatherStations"
      class="width100 fc-setup-table fc-setup-table-p0 fc-setup-table-th-borderTop"
      height="calc(100vh - 280px)"
    >
      <el-table-column label="Station Name" ref="name">
        <template v-slot="weatherstation">
          {{ weatherstation.row.name || '---' }}
        </template>
      </el-table-column>
      <el-table-column label="Description" ref="description">
        <template v-slot="weatherstation">
          {{ weatherstation.row.description || '---' }}
        </template>
      </el-table-column>
      <el-table-column label="Station Code" ref="stationCode">
        <template v-slot="weatherstation">
          {{ weatherstation.row.stationCode || '---' }}
        </template>
      </el-table-column>
      <el-table-column label="Weather Service" ref="weatherService">
        <template v-slot="weatherstation">
          {{ getServiceName(weatherstation.row.service) || '---' }}
        </template>
      </el-table-column>
      <el-table-column
        prop
        label
        width="130"
        class="visibility-visible-actions"
        fixed="right"
      >
        <template v-slot="template">
          <div class="text-center template-actions">
            <i
              class="el-icon-edit edit-icon visibility-hide-actions pR15"
              data-arrow="true"
              :title="$t('common._common.edit')"
              v-tippy
              @click="editWeatherStation(template.row)"
            ></i>
            <i
              class="el-icon-delete fc-delete-icon visibility-hide-actions"
              data-arrow="true"
              :title="$t('common._common.delete')"
              v-tippy
              @click="deleteWeatherStation([template.row.id])"
            ></i>
          </div>
        </template>
      </el-table-column>
    </el-table>
    <NewWeatherStation
      v-if="showDialog"
      :visibility="showDialog"
      :isNew="isNew"
      :selectedWeatherStation="selectedWeatherStation"
      @closeDialog="updateDialogFields"
      @weatherStationSaved="loadWeatherStations"
    >
    </NewWeatherStation>
  </div>
</template>

<script>
import SetupLoader from 'pages/setup/components/SetupLoader'
import SetupEmpty from 'pages/setup/components/SetupEmptyState'
import Pagination from 'src/components/list/FPagination'
import NewWeatherStation from './NewWeatherStation'
import { API } from '@facilio/api'
import WeatherStationSearch from './WeatherStationsearch'
import { isEmpty } from '@facilio/utils/validation'
export default {
  data() {
    return {
      isLoading: false,
      showDialog: false,
      isNew: true,
      selectedWeatherStation: null,
      weatherStations: [],
      weatherStationPage: 1,
      weatherStationsPerPage: 50,
      weatherStationPageCount: null,
      searchData: null,
      weatherServices: [],
    }
  },
  computed: {
    isSearchDisabled() {
      let { searchData, weatherStations } = this
      return isEmpty(searchData) && isEmpty(weatherStations)
    },
  },
  props: ['showAddWeatherStation'],
  components: {
    SetupLoader,
    SetupEmpty,
    Pagination,
    NewWeatherStation,
    WeatherStationSearch,
  },
  created() {
    this.loadWeatherStations()
  },
  watch: {
    showAddWeatherStation(newVal, oldVal) {
      if (newVal != oldVal) {
        this.showDialog = newVal
      }
    },
    weatherStationPage(newVal, oldVal) {
      if (newVal != oldVal) {
        this.loadWeatherStations()
      }
    },
  },
  methods: {
    setPage(page) {
      this.weatherStationPage = page
    },
    updateDialogFields() {
      this.isNew = true
      this.showDialog = false
      this.selectedWeatherStation = null
      this.$emit('updateShowAddWeatherStation', false)
    },
    editWeatherStation(selectedWeatherStation) {
      this.selectedWeatherStation = selectedWeatherStation
      this.isNew = false
      this.showDialog = true
    },
    getServiceName(service) {
      if (!isEmpty(service)) {
        return service.displayName || '---'
      }
      return '---'
    },
    async deleteWeatherStation(id) {
      let value = await this.$dialog.confirm({
        title: this.$t('common.wo_report.delete_rule_title'),
        message: this.$t('weather.are_you_sure_to_delete_weather_station'),
        rbDanger: true,
        rbLabel: 'Delete',
      })
      if (value) {
        let { error } = await API.deleteRecord('weatherstation', id)
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.$message.success(
            this.$t('weather.weather_station_deleted_successfully')
          )
          this.loadWeatherStations()
        }
      }
    },
    async loadWeatherStations() {
      this.isLoading = true
      let filters
      if (!isEmpty(this.searchData)) {
        filters = JSON.stringify({
          name: { operatorId: 5, value: [this.searchData] },
        })
      }
      let { list, meta = {}, error } = await API.fetchAll('weatherstation', {
        withCount: true,
        page: this.weatherStationPage,
        perPage: this.weatherStationsPerPage,
        filters,
      })
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.weatherStations = list
        this.weatherServices = this.$getProperty(
          meta,
          'supplements.weatherstation.service',
          null
        )
        this.weatherStationPageCount = this.$getProperty(
          meta,
          'pagination.totalCount',
          null
        )
        this.isLoading = false
      }
    },
  },
}
</script>

<style lang="scss">
.weather-station-list {
  .fc-setup-empty,
  .fc-setup-loader {
    height: calc(100vh - 300px);
  }
  .el-table__header {
    .el-table__cell {
      background: #f3f1fc;
    }
  }
}
</style>

<style lang="scss" scoped>
.weather-station-setup-page {
  .search-and-pagination-container {
    height: 40px;
  }
  .vl {
    border-left: 1.7px solid rgb(208, 211, 208);
    height: 27px;
    width: 25px;
  }
}
</style>
