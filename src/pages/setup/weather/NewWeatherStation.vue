<template>
  <el-dialog
    :visible="visibility"
    :fullscreen="true"
    :append-to-body="true"
    custom-class="new-weather-station-form fc-dialog-form setup-dialog"
    :before-close="closeDialog"
  >
    <div class="new-header-container pL30">
      <div class="new-header-text">
        <div class="fc-setup-modal-title">
          {{ weatherStationFormHeader }}
        </div>
      </div>
    </div>
    <div v-if="isLoading" class="mT20">
      <spinner :show="isLoading" size="80"></spinner>
    </div>
    <template v-else>
      <el-form
        ref="weatherStationForm"
        :model="weatherStationDetails"
        label-width="150px"
        label-position="top"
        class="p30 pT10 pB30"
        :rules="weatherStationRules"
      >
        <div class="section-container flex-container">
          <div class="form-one-column">
            <el-form-item
              :label="$t('weather.create.station_name')"
              prop="name"
            >
              <el-input
                v-model="weatherStationDetails.name"
                class="fc-input-full-border2"
                :placeholder="$t('weather.create.station_name')"
                :autofocus="true"
              ></el-input>
            </el-form-item>
          </div>
          <div class="form-one-column">
            <el-form-item
              :label="$t('common.roles.description')"
              prop="description"
            >
              <el-input
                v-model="weatherStationDetails.description"
                type="textarea"
                class="mT3 fc-input-full-border-textarea"
                :min-rows="2"
                :autosize="{ minRows: 3, maxRows: 4 }"
                :placeholder="$t('common.roles.description')"
              ></el-input>
            </el-form-item>
          </div>
          <div class="form-one-column">
            <el-form-item
              :label="$t('weather.weather_service')"
              prop="serviceId"
            >
              <FLookupField
                :key="`weatherStation-service`"
                :model.sync="serviceId"
                :field="weatherServiceField"
                :isRemoteField="false"
                :disabled="!isNew"
                :hideLookupIcon="true"
              ></FLookupField>
            </el-form-item>
          </div>
          <div class="form-one-column">
            <el-form-item :label="$t('setup.bim.bim_location')" prop="location">
              <WeatherStationLocation
                v-model="location"
                :isNew="isNew"
              ></WeatherStationLocation>
            </el-form-item>
          </div>
          <div class="form-one-column">
            <el-form-item
              :label="$t('weather.create.weather_station_code')"
              prob="stationCode"
            >
              <el-input
                :placeholder="
                  $t('weather.create.auto_generated_based_on_location')
                "
                v-model="weatherStationDetails.stationCode"
                :disabled="true"
                class="fc-input-full-border2"
              >
              </el-input>
            </el-form-item>
          </div>
        </div>
      </el-form>
    </template>
    <div class="modal-dialog-footer">
      <el-button @click="closeDialog" class="modal-btn-cancel">
        {{ $t('common._common.cancel') }}
      </el-button>
      <el-button
        type="primary"
        @click="saveWeatherStation"
        :loading="weatherStationSaving"
        class="modal-btn-save"
      >
        {{ weatherStationFormSaveTxt }}
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
import FLookupField from '@/forms/FLookupField'
import { isEmpty } from '@facilio/utils/validation'
import WeatherStationLocation from './WeatherStationLocation.vue'
import { API } from '@facilio/api'
import cloneDeep from 'lodash/cloneDeep'
export default {
  name: 'NewWeatherStation',
  components: {
    FLookupField,
    WeatherStationLocation,
  },
  props: ['isNew', 'visibility', 'selectedWeatherStation'],
  data() {
    return {
      weatherStationDetails: { service: { id: null } },
      field: {},
      location: {},
      weatherServiceField: {
        lookupModuleName: 'weatherservice',
      },
      isLoading: false,
      weatherStationSaving: false,
      weatherStationRules: {
        name: {
          validator: (rule, name, callback) => {
            if (isEmpty(name)) {
              callback(
                new Error(this.$t('weather.create.please_enter_station_name'))
              )
            } else {
              callback()
            }
          },
          trigger: 'blur',
        },
        serviceId: {
          validator: (rule, serviceId, callback) => {
            if (isEmpty(serviceId)) {
              callback(
                new Error(
                  this.$t('weather.create.please_select_weather_service')
                )
              )
            } else {
              callback()
            }
          },
          trigger: 'blur',
        },
        location: {
          validator: (rule, location, callback) => {
            let lat = this.$getProperty(this, 'weatherStationDetails.lat')
            let lng = this.$getProperty(this, 'weatherStationDetails.lng')
            if (isEmpty(lat) || isEmpty(lng)) {
              callback(
                new Error(this.$t('weather.create.please_choose_location'))
              )
            } else {
              callback()
            }
          },
          trigger: 'blur',
        },
      },
    }
  },
  computed: {
    serviceId: {
      get() {
        let service = this.$getProperty(this, 'weatherStationDetails.service')
        if (!isEmpty(service)) {
          return service.id
        }
        return service
      },
      set(newVal) {
        this.weatherStationDetails.service.id = newVal
      },
    },
    weatherStationFormHeader() {
      return this.isNew
        ? this.$t('weather.create.create_weather_station')
        : this.$t('weather.create.edit_weather_station')
    },
    weatherStationFormSaveTxt() {
      return this.weatherStationSaving
        ? this.$t('common._common._saving')
        : this.$t('common._common._save')
    },
  },
  watch: {
    location: {
      async handler(newVal, oldVal) {
        if (newVal != oldVal && !isEmpty(newVal)) {
          this.getStationCode(newVal)
        }
      },
      immediate: true,
    },
    serviceId: function(newVal, oldVal) {
      if (newVal != oldVal) {
        this.weatherStationDetails.serviceId = newVal
      }
    },
  },
  created() {
    if (!this.isNew && !isEmpty(this.selectedWeatherStation)) {
      this.preFillStationDetails(this.selectedWeatherStation)
    }
  },
  methods: {
    closeDialog() {
      this.$emit('closeDialog')
    },
    preFillStationDetails(selectedWeatherStation) {
      this.weatherStationDetails = cloneDeep(selectedWeatherStation, true)
      this.location.lat = selectedWeatherStation?.lat
      this.location.lng = selectedWeatherStation?.lng
    },
    async saveWeatherStation() {
      this.$refs['weatherStationForm'].validate(async valid => {
        if (!valid) return false
        this.weatherStationSaving = true
        let data = { data: this.weatherStationDetails }
        let promise
        if (this.isNew) {
          promise = await API.createRecord('weatherstation', data)
        } else {
          let updatedData = {
            name: this.weatherStationDetails?.name,
            description: this.weatherStationDetails?.description,
          }
          data = { id: this.weatherStationDetails?.id, data: updatedData }
          promise = await API.updateRecord('weatherstation', data)
        }
        let { error } = await promise
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.closeDialog()
          if (this.isNew) {
            this.$message.success(
              this.$t('weather.create.weather_station_created_successfully')
            )
          } else {
            this.$message.success(
              this.$t('weather.create.weather_station_edited_successfully')
            )
          }
          this.$emit('weatherStationSaved')
        }
        this.weatherStationSaving = false
      })
    },
    async getStationCode(newVal) {
      let url = 'v3/weather/stationcode'
      this.weatherStationDetails.lat = newVal?.lat
      this.weatherStationDetails.lng = newVal?.lng
      let { error, data } = await API.get(url, {
        lat: newVal?.lat,
        lng: newVal?.lng,
      })
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        let { lat, lng, stationCode } = data || {}
        this.$set(this.weatherStationDetails, 'stationCode', stationCode)
        this.weatherStationDetails.lat = lat
        this.weatherStationDetails.lng = lng
      }
    },
  },
}
</script>

<style lang="scss" scoped>
.new-weather-station-form {
  .default {
    color: #6b6666;
  }
  .fS10 {
    font-size: 15px;
  }
}
</style>
<style lang="scss">
.new-weather-station-form {
  .default {
    color: #6b6666;
  }
  .fS10 {
    font-size: 15px;
  }
  &.setup-dialog {
    width: 40% !important;
    .el-dialog__header {
      display: none;
    }
    .el-dialog__body {
      height: calc(100vh - 100px);
      overflow-y: scroll;
      padding-bottom: 50px;
    }
  }
}
</style>
