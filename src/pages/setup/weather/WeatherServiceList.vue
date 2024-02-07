<template>
  <div class="weather-service-list">
    <portal to="weather-setup-tab-actions">
      <div class="search-and-pagination-container flex-middle">
        <WeatherStationSearch
          v-model="searchData"
          :isSearchDisabled="isSearchDisabled"
          @updateSearchData="loadWeatherServices"
        />
        <div v-if="weatherServicePageCount" class="vl"></div>
        <div class="pagination-container">
          <Pagination
            :total="weatherServicePageCount"
            :perPage="weatherServicesPerPage"
            class="nowrap"
            ref="f-page"
            :pageNo="weatherServicePage"
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
    <SetupEmpty v-else-if="$validation.isEmpty(weatherServices) && !isLoading">
      <template #emptyImage>
        <inline-svg src="svgs/copy2" iconClass="icon icon-sm-md"></inline-svg>
      </template>
      <template #emptyHeading>
        {{ $t('weather.no_weather_services_available') }}
      </template>
      <template #emptyDescription> </template>
    </SetupEmpty>
    <el-table
      v-else
      :data="weatherServices"
      class="width100 fc-setup-table fc-setup-table-p0 fc-setup-table-th-borderTop"
      height="calc(100vh - 280px)"
    >
      <el-table-column
        :label="$t('common._common.display_name')"
        ref="description"
      >
        <template v-slot="weatherService">
          {{ $getProperty(weatherService, 'row.displayName', '---') }}
        </template>
      </el-table-column>
      <el-table-column
        :label="$t('setup.users_management.description')"
        ref="description"
      >
        <template v-slot="weatherService">
          {{ $getProperty(weatherService, 'row.description', '---') }}
        </template>
      </el-table-column>
      <el-table-column :label="$t('common._common.frequency')" ref="Frequency">
        <template v-slot="weatherService">
          {{ getFormattedTime(weatherService.row.dataInterval) }}
        </template>
      </el-table-column>
      <el-table-column :label="$t('setup.setupLabel.link_name')" ref="name">
        <template v-slot="weatherService">
          {{ $getProperty(weatherService, 'row.name', '---') }}
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import SetupLoader from 'pages/setup/components/SetupLoader'
import SetupEmpty from 'pages/setup/components/SetupEmptyState'
import Pagination from 'src/components/list/FPagination'
import WeatherStationSearch from './WeatherStationsearch'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
export default {
  name: 'WeatherServiceList',
  components: {
    SetupLoader,
    SetupEmpty,
    Pagination,
    WeatherStationSearch,
  },
  data() {
    return {
      data: {},
      isLoading: false,
      weatherServices: [],
      weatherServicePage: 1,
      weatherServicesPerPage: 50,
      weatherServicePageCount: null,
      searchData: null,
    }
  },
  computed: {
    isSearchDisabled() {
      let { searchData, weatherServices } = this
      return isEmpty(searchData) && isEmpty(weatherServices)
    },
  },
  created() {
    this.loadWeatherServices()
  },
  methods: {
    setPage(page) {
      this.weatherServicePage = page
    },
    getFormattedTime(totalMinutes) {
      if (isEmpty(totalMinutes)) {
        return '---'
      } else {
        if (totalMinutes > 60) {
          let hours = Math.floor(totalMinutes / 60)
          let minutes = totalMinutes % 60
          return `${hours} hr ${minutes} min`
        }
        return `${totalMinutes} min`
      }
    },
    async loadWeatherServices() {
      this.isLoading = true
      let filters
      if (!isEmpty(this.searchData)) {
        filters = JSON.stringify({
          name: { operatorId: 5, value: [this.searchData] },
        })
      }
      let { list, meta = {}, error } = await API.fetchAll('weatherservice', {
        withCount: true,
        page: this.weatherServicePage,
        perPage: this.weatherServicesPerPage,
        filters,
      })
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.weatherServices = list
        this.weatherServicePageCount = this.$getProperty(
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
.weather-service-list {
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
.weather-service-list {
}
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
