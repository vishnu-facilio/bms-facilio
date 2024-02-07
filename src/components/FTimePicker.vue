<template>
  <div>
    <el-dropdown
      class="width100"
      trigger="click"
      v-model="modelValue"
      :disabled="disabled"
      @command="handleSelect"
    >
      <el-input
        ref="time-picker-input"
        class="time-picker-input fc-input-full-border-select2 width100 "
        :class="isError && 'time-input-error'"
        :disabled="disabled"
        v-model="inputValue"
        @input="formatTime"
      >
        <template #prefix>
          <div class="el-input__icon el-icon-time"></div>
        </template>
        <template #suffix>
          <div
            v-if="canShowSystemClear"
            class="ftime-remove-icon-alignment"
            @click="resetTimeValue"
          >
            <i class="el-icon-circle-close pointer fc-lookup-icon f13"></i>
          </div>
        </template>
      </el-input>
      <el-dropdown-menu
        slot="dropdown"
        class="height200 overflow-scroll"
        :style="dropdownStyle"
      >
        <el-dropdown-item
          v-for="row in rows"
          :key="`${row.value}`"
          :command="`${row.value}`"
          >{{ row.label }}</el-dropdown-item
        >
      </el-dropdown-menu>
    </el-dropdown>
    <div class="el-form-item-time__error">{{ errMsg }}</div>
  </div>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
import moment from 'moment-timezone'
import debounce from 'lodash/debounce'
import { durationToSeconds } from '@facilio/utils/utility-methods'

const INTERVAL_HASH = ['00', '15', '30', '45']
const TIME_FORMAT_HASH = {
  '24Hour': 1,
  '12Hour': 2,
}
export const isDateEmpty = value => {
  return isNaN(Date.parse(value)) || isEmpty(Date.parse(value))
}

export default {
  props: {
    value: {
      default: () => new Date(),
    },
    field: {
      type: Object,
      default: () => ({}),
    },
    disabled: {
      type: Boolean,
      default: false,
    },
  },

  data() {
    return {
      time: '',
      inputValue: null,
      debounce: debounce,
      errMsg: '',
      isError: null,
      interval: 30,
      dropdownStyle: '',
    }
  },
  mounted() {
    let element = this.$refs['time-picker-input']
    if (!isEmpty(element)) {
      let width = this.$getProperty(element, '$el.clientWidth')
      this.dropdownStyle = `width:${width}px`
    }
  },
  computed: {
    is12Hour() {
      let timeFormat = this.$getProperty(this.$account, 'org.timeFormat')
      return timeFormat === TIME_FORMAT_HASH['12Hour']
    },
    modelValue: {
      get() {
        return this.value
      },
      set(value) {
        this.$emit('input', this.getFormattedTime(value))
      },
    },
    rows() {
      let rows = []
      let { is12Hour, interval } = this
      rows = this.generateRows(is12Hour, interval)

      return rows
    },
    canShowSystemClear() {
      let { disabled, modelValue } = this
      return !disabled && !isEmpty(modelValue)
    },
  },
  watch: {
    value: {
      handler(val) {
        if (!isEmpty(val)) {
          this.handleValue(val)
        } else {
          this.resetTimeValue()
        }
      },
      immediate: true,
    },
    inputValue: {
      handler() {
        this.errMsg = ''
        this.isError = false
      },
      immediate: true,
    },
  },
  methods: {
    handleValue(val) {
      let selectedTime = this.rows.find(row => {
        return row.label == this.getFormattedTime(val)
      })
      if (!isEmpty(selectedTime)) {
        this.inputValue = selectedTime.label
      } else {
        this.inputValue = this.getFormattedTime(val)
      }
      this.handleChange()
    },
    generateRows(is12Hour, interval) {
      let rows = []
      // mintuesInterval helps to decide the interval hash interval
      let minutesInterval = interval === 30 ? 2 : 4
      let startHour = is12Hour ? '12' : '0'
      let meridian = is12Hour ? 'AM' : ''
      for (let i = 0; i < 24; i++) {
        for (let j = 0; j < minutesInterval; j++) {
          let minutes =
            interval === 30 ? INTERVAL_HASH[j * 2] : INTERVAL_HASH[j]
          rows.push({
            label: `${
              startHour < 10 ? '0' : ''
            }${startHour}:${minutes} ${meridian}`,
            value: this.getUnixTime(startHour, minutes, meridian),
          })
        }
        if (is12Hour) {
          startHour = (parseInt(startHour) + 1) % 12
          if (startHour === 0) {
            startHour = 12
            meridian = 'PM'
          }
        } else {
          startHour = parseInt(startHour) + 1
        }
      }
      return rows
    },
    getUnixTime(hours, minutes, meridian) {
      let setHour = hours
      if (!isEmpty(meridian)) setHour = meridian === 'AM' ? hours : hours + 12
      if (meridian === 'AM' && hours == 12) {
        setHour = 0
      } else if (meridian === 'PM' && hours == 12) {
        setHour = 12
      }
      let time = this.getTimeAsMills(setHour, minutes)
      return time
    },
    handleSelect(val) {
      this.$emit('input', val)
      let selectedTime = this.rows.find(row => row.value === parseInt(val))
      this.inputValue = selectedTime.label
      this.handleChange()
    },
    handleChange() {
      let { value } = this || {}
      !isEmpty(value) && this.$emit('change', value)
    },
    resetTimeValue() {
      this.modelValue = null
      this.inputValue = null
      this.handleChange()
      this.$emit('input', null)
    },
    formatTime: debounce(function(value) {
      if (!isEmpty(value)) {
        // regex for 01:34 PM, 11:59 am
        if (!this.is12Hour) {
          value = value.replace(/\s/g, '')
        }
        let meridianRegex = /^(0?[0-9]|1[0-9]|2[0-4]):([0-5][0-9]) ([AaPp][Mm])$/
        let meridianValues = value.match(meridianRegex)
        if (!isEmpty(meridianValues)) {
          let [, hours, mins, meridian] = meridianValues || []
          // handling for cases : 16:45 PM ---> 04:45 PM , 14:22 AM ---> 02:22 PM
          if (hours > 12 && hours <= 24) {
            hours -= 12
            meridian = 'PM'
          }
          if (hours === '0' || hours === '00') {
            hours = '00'
            meridian = 'AM'
          }
          // we need to convert 12 AM to 24 hrs format
          if (hours === '12' && meridian.toUpperCase() == 'AM') {
            hours = '00'
          } else {
            /* we need to add 12 to prime meridian hours
                to convert to 24 hrs.
                12 PM is an exception here.
                */
            hours =
              meridian.toUpperCase() === 'PM' && hours != 12
                ? parseInt(hours) + 12
                : hours
          }
          let updatedTime = this.getTimeAsMills(hours, mins)
          updatedTime = moment(updatedTime).format('x')
          this.$emit('input', updatedTime)
        } else {
          // regex for 00:46, 22:30, 01:56
          let nonMeridianRegex = /^(0?[0-9]|1[0-9]|2[0-3]):([0-5][0-9])$/
          let nonMeridianValues = value.match(nonMeridianRegex)
          if (!isEmpty(nonMeridianValues)) {
            let [, hours, mins] = nonMeridianValues || []
            let updatedTime = this.getTimeAsMills(hours, mins)
            updatedTime = moment(updatedTime).format('x')
            this.$emit('input', updatedTime)
          } else if (isEmpty(nonMeridianValues)) {
            let numberRegex = /^(0?[0-9]|1[0-9]|2[0-3])$/
            let colonRegex = /^(0?[0-9]|1[0-9]|2[0-3]):$/
            let numberPatternRegex = /^(0?[0-9]|1[0-9]|2[0-3]):([0-5])$/
            let numberValues = value.match(numberRegex)
            let colonValues = value.match(colonRegex)
            let numberPatternValues = value.match(numberPatternRegex)
            if (!isEmpty(numberPatternValues)) {
              let [, hours, mins] = numberPatternValues || []
              let min = mins * 10
              let updatedTime = this.getTimeAsMills(hours, min)
              updatedTime = moment(updatedTime).format('x')
              this.$emit('input', updatedTime)
            } else if (!isEmpty(colonValues)) {
              let [, hours] = colonValues || []
              let mins = 0
              let updatedTime = this.getTimeAsMills(hours, mins)
              updatedTime = moment(updatedTime).format('x')
              this.$emit('input', updatedTime)
            } else if (!isEmpty(numberValues)) {
              let [, hours] = numberValues || []
              let mins = 0
              let updatedTime = this.getTimeAsMills(hours, mins)
              updatedTime = moment(updatedTime).format('x')
              this.$emit('input', updatedTime)
            } else {
              this.isError = true
              this.errMsg = this.$t('forms.field_permission.valid_time')
            }
          } else {
            this.isError = true
            this.errMsg = this.$t('forms.field_permission.valid_time')
          }
        }
        this.isDropdownVisible = false
      }
    }, 1000),

    getTimeAsMills(hours, minutes) {
      let seconds = 0
      let days = 0
      let formatedDuration = durationToSeconds({
        seconds,
        minutes,
        hours,
        days,
      })
      return formatedDuration * 1000
    },
    getFormattedTime(value) {
      let format = this.is12Hour ? 'hh:mm A' : '  HH:mm'
      return this.convertMilliSecondsToTimeHHMM(value, format)
    },

    convertMilliSecondsToTimeHHMM(timeValue, format) {
      let timeFieldValue = timeValue || {}
      return moment()
        .startOf('day')
        .milliseconds(timeFieldValue)
        .format(format)
    },
  },
}
</script>
<style lang="scss">
.time-icon {
  height: 37px !important;
  width: 16px !important;
}
.el-form-item-time__error {
  color: #f56c6c;
  font-size: 12px;
  line-height: 1;
  padding-top: 4px;
  position: absolute;
  top: 100%;
  left: 0;
}
.time-picker-input {
  .el-input__inner {
    padding-left: 30px !important;
  }
}
.time-input-error {
  .el-input__inner {
    border-color: #f56c6c !important;
  }
}
.ftime-remove-icon-alignment {
  color: #809aae !important;
  width: 20px;
  line-height: 40px;
}
</style>
