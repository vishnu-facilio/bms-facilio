<template>
  <div class="weather-station-location">
    <el-input
      v-if="!$validation.isEmpty(value)"
      class="fc-input-full-border2 font-lite mB15"
      v-model="locationPlaceHolder"
      readonly
      :disabled="!isNew"
    >
    </el-input>
    <el-button
      class="btn-blue-fill location-select-btn"
      @click="dialogVisibility = true"
      :class="{ disabled: !isNew }"
    >
      {{ locationBtnPlaceHolder }}
    </el-button>
    <FLocationPickerDialog
      :model="value"
      :dialogVisibility="dialogVisibility"
      @update:dialogVisibility="dialogVisibility = false"
      @updateLocation="updateLocation"
      class="weather-location-select-dialog"
    ></FLocationPickerDialog>
  </div>
</template>

<script>
import FLocationPickerDialog from '@/FLocationPickerDialog'
import { isEmpty } from '@facilio/utils/validation'
export default {
  name: 'WeatherStationLocation',
  components: {
    FLocationPickerDialog,
  },
  props: ['value', 'isNew'],
  data() {
    return {
      dialogVisibility: false,
      locationPlaceHolder: '',
    }
  },
  computed: {
    locationBtnPlaceHolder() {
      if (isEmpty(this.value)) {
        return this.$t('weather.create.choose_your_locaiton_in_map')
      }
      return this.$t('weather.create.change_your_location_in_map')
    },
  },
  watch: {
    value: {
      async handler(newVal, oldVal) {
        if (newVal != oldVal) {
          this.locationPlaceHolder = `${newVal?.lat}, ${newVal?.lng}`
        }
      },
      immediate: true,
    },
  },
  methods: {
    updateLocation(location) {
      this.$emit('input', location)
    },
  },
}
</script>

<style lang="scss">
.weather-station-location {
  .font-lite {
    .el-input__inner {
      color: #969ba0;
    }
  }
}
.weather-location-select-dialog {
  .gm-style {
    .gm-style-iw {
      padding: 20px !important;
    }
  }
}
</style>

<style lang="scss" scoped>
.weather-station-location {
  .disabled {
    pointer-events: none;
  }
  .location-select-btn {
    width: 100%;
  }
}
</style>
