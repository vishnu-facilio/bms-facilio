<template>
  <el-date-picker
    ref="date-picker"
    v-model="ttime"
    :appendToBody="appendToBody"
    :clearable="!hideClear"
    :type="type"
    :value-format="valueFormat"
    :format="pickerFormat"
    :placeholder="placeholder"
    :disabled="disabled"
    :editable="editable"
    :default-value="defaultValue"
    :picker-options="pickerOptions"
    :prefix-icon="prefixIcon"
    :clear-icon="clearIcon"
    :start-placeholder="startPlaceholder"
    :end-placeholder="endPlaceholder"
    :time-arrow-control="arrowControl"
    :default-time="defaultTime"
    @change="pickerChange"
  ></el-date-picker>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import Constants from 'util/constant'
import moment from 'moment-timezone'

export default {
  props: [
    'appendToBody',
    'value',
    'value-format',
    'format',
    'default-value',
    'placeholder',
    'disabled',
    'editable',
    'picker-options',
    'type',
    'prefix-icon',
    'clear-icon',
    'start-placeholder',
    'end-placeholder',
    'hideClear',
    'arrowControl',
    'default-time',
  ],
  mounted() {
    this.init()
  },
  data() {
    return {
      timeinorg: [],
    }
  },
  computed: {
    isRangeType() {
      let { type } = this || {}
      return ['daterange', 'datetimerange'].includes(type)
    },
    defaultFormats() {
      let { $timeformat, $dateformat } = this
      let dateformat = ($dateformat || '').replaceAll('Y', 'y')
      dateformat = (dateformat || '').replaceAll('D', 'd')
      return {
        date: dateformat,
        month: 'yyyy-MM',
        datetime: `${dateformat} ${$timeformat}`,
        time: `${$timeformat}`,
        week: 'yyyywWW',
        timerange: `${$timeformat}`,
        daterange: `${dateformat}`,
        monthrange: 'yyyy-MM',
        datetimerange: `${dateformat} ${$timeformat}`,
        year: 'yyyy',
      }
    },
    pickerFormat() {
      let { defaultFormats, type, format } = this || {}
      if (isEmpty(format) && !isEmpty(defaultFormats[type])) {
        return defaultFormats[type]
      } else {
        return format
      }
    },
    ttime: {
      get() {
        let { value, isRangeType } = this || {}
        if (!isEmpty(value)) {
          if (isRangeType) {
            return value.map(val => this.getTimeInSystemZone(val))
          } else {
            return this.getTimeInSystemZone(value)
          }
        } else {
          return isRangeType ? [] : ''
        }
      },
      set(value) {
        let { isRangeType } = this
        if (isRangeType) {
          if (isEmpty(value)) {
            this.timeinorg = []
          } else {
            for (let time in value) {
              this.timeinorg[time] = this.$helpers.getTimeInOrg(value[time])
            }
          }
          this.$emit('input', this.timeinorg)
          this.$emit('change', this.timeinorg)
        } else {
          let modifiedTimeFormat = this.$helpers.getTimeInOrg(value)
          this.$emit('input', modifiedTimeFormat)
          this.$emit('change', modifiedTimeFormat)
        }
      },
    },
  },
  methods: {
    pickerChange() {
      let { isRangeType, value } = this
      if (isRangeType) {
        if (isEmpty(value)) {
          this.timeinorg = []
        } else {
          for (let time in value) {
            this.timeinorg[time] = this.$helpers.getTimeInOrg(value[time])
          }
        }
        this.$emit('pickerChange', this.timeinorg)
      } else {
        let modifiedTimeFormat = this.$helpers.getTimeInOrg(value)
        this.$emit('pickerChange', modifiedTimeFormat)
      }
    },
    init() {
      let { isRangeType, value } = this
      if (isRangeType) {
        if (isEmpty(value)) {
          this.timeinorg = []
        } else {
          for (let time in value) {
            let timeVal = parseInt(value[time])
            this.timeinorg[time] = timeVal
          }
        }
        this.$emit('input', this.timeinorg)
      } else {
        if (!isEmpty(value)) {
          let isPlaceHoldersEnabled =
            Constants.FIELD_PLACEHOLDERS.includes(value) || false
          if (!isPlaceHoldersEnabled) {
            this.$emit('input', value)
          }
        }
      }
      this.$emit('initialized')
    },
    focus() {
      this.$refs['date-picker'].focus()
    },
    getTimeInSystemZone(value) {
      return new Date(
        moment.tz(value, this.$timezone).format('YYYY-MM-DD HH:mm:ss')
      )
    },
  },
}
</script>
