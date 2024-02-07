<template>
  <div>
    <el-input
      class="fc-input-full-border2 location-field"
      v-model="inputModel"
      readonly
      :disabled="disabled"
      @change="handleChange"
    >
      <div slot="suffix" :class="['flRight d-flex', !disabled && 'pointer']">
        <div
          v-if="canShowSystemClear"
          class="flocation-remove-icon-alignment  flookup-remove-icon"
          @click="resetLocationValue"
        >
          <i class="el-icon-circle-close pointer fc-lookup-icon f13"></i>
        </div>
        <div @click="dialogVisibility = !disabled" slot="suffix">
          <inline-svg
            src="location-icon"
            class="vertical-middle cursor-pointer mT10 mR5"
            iconClass="icon icon-md location-icon"
          ></inline-svg>
        </div>
      </div>
    </el-input>
    <FLocationPickerDialog
      :model="inputValue"
      @updateLocation="updateLocation"
      :dialogVisibility.sync="dialogVisibility"
    ></FLocationPickerDialog>
  </div>
</template>
<script>
import FLocationPickerDialog from '@/FLocationPickerDialog'
import { isEmpty, isObject } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import isEqual from 'lodash/isEqual'

export default {
  props: ['value', 'field', 'disabled', 'clearable'],
  components: {
    FLocationPickerDialog,
  },
  data() {
    return {
      dialogVisibility: false,
      inputModel: null,
      inputValue: null,
    }
  },
  computed: {
    canShowSystemClear() {
      let { disabled, value } = this
      if (!disabled && !isEmpty(value)) {
        let { locationId } = value || {}
        return !isEmpty(locationId) || !this.isLocationValueEmpty(value)
      } else {
        return false
      }
    },
  },
  watch: {
    inputValue: {
      async handler(newVal, oldVal) {
        if (!isEmpty(newVal)) {
          if (
            !isEmpty(newVal.locationId) &&
            this.isLocationValueEmpty(newVal)
          ) {
            let { locationId } = newVal || {}
            let { data, error } = await API.get(`v2/location/details`, {
              locationId,
            })

            if (!error) {
              let { location } = data || {}
              let { id, lat, lng } = location || {}
              newVal = { ...newVal, lat, lng }
              this.$emit('input', { locationId: id, lat, lng })
            } else {
              this.$message.error(error.message || 'Error Occured')
            }
          }
          if (!this.isLocationValueEmpty(newVal)) {
            this.inputModel = `${newVal.lat}, ${newVal.lng}`
          }
        }
        if (newVal?.lat && newVal?.lng) {
          this.$emit('locationUpdated', newVal)
          this.$emit('input', newVal)
        } else {
          this.inputModel = newVal
        }
        if (!isEqual(oldVal, newVal)) {
          this.handleChange()
        }
      },
      deep: true,
    },
    value: {
      async handler(newVal) {
        if (!isObject(newVal)) {
          newVal = JSON.parse(newVal)
        }
        this.inputValue = newVal
      },
    },
  },
  mounted() {
    let { value } = this
    let { locationId } = value || {}
    if (
      (!isEmpty(value) && !isEmpty(locationId)) ||
      !this.isLocationValueEmpty(value)
    ) {
      this.inputValue = this.$helpers.cloneObject(value)
    }
  },
  methods: {
    async resetLocationValue() {
      this.inputModel = null
      let { field, clearable } = this || {}
      let { value } = field || {}

      if (!isEmpty(value) && clearable) {
        if (!isObject(value)) {
          value = JSON.parse(value)
        }
        let locationIds = [value?.locationId]
        let { error } = await API.post(`location/delete`, {
          locationIds,
        })
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        }
      }
      this.handleChange()
      this.inputValue = null
      this.$emit('input', null)
    },
    handleChange() {
      let { value } = this || {}
      if (!isEmpty(value)) {
        if (!isObject(value)) {
          value = JSON.parse(value)
        }
        this.inputValue = value
      }
      !isEmpty(value) && this.$emit('change', value)
    },
    updateLocation(location) {
      this.$emit('input', location)
      this.$emit('value', location)
    },
    isLocationValueEmpty(value) {
      return isEmpty(value?.lat) && isEmpty(value?.lng)
    },
  },
}
</script>
<style scoped>
.flocation-remove-icon-alignment {
  height: 100%;
  align-items: center;
  justify-content: center;
  margin: 10px 5px;
  visibility: hidden;
}
.icon-remove {
  padding-right: 50px !important;
}
</style>
<style lang="scss">
.location-field {
  .el-input__inner {
    padding-right: 50px !important;
  }
  &:hover {
    .flocation-remove-icon-alignment {
      visibility: visible;
    }
    .el-input__suffix {
      height: 36px;
    }
  }
}
</style>
