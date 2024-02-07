<template>
  <div class="fdd-historical-date-picker">
    <FDatePicker
      @change="setDateRange"
      type="datetimerange"
      :value="dateRange"
      value-format="timestamp"
      format="dd/MM/yyyy  hh:mm a"
      range-separator="-"
      :start-placeholder="$t('kpi.historical.select_start_date')"
      :end-placeholder="$t('kpi.historical.select_end_date')"
      unlink-panels
      :picker-options="pickerOptions"
      :default-time="['00:00:00', '23:59:59']"
    />
  </div>
</template>

<script>
import FDatePicker from 'pages/assets/overview/FDatePicker'

export default {
  components: {
    FDatePicker,
  },
  data() {
    return {
      dateRange: null,
    }
  },
  computed: {
    pickerOptions() {
      let { $helpers } = this
      let todayDate = $helpers
        .getOrgMoment()
        .startOf('hour')
        .valueOf()
      return {
        disabledDate(time) {
          return $helpers.getDateInOrg(time.getTime()) > todayDate
        },
      }
    },
  },
  methods: {
    setDateRange(ttime) {
      this.dateRange = ttime
      this.$emit('rangeChange', ttime)
    },
  },
}
</script>

<style lang="scss">
.fdd-historical-date-picker {
  .el-date-editor--datetimerange.el-input__inner {
    width: 100% !important;
    background: none !important;
    border-radius: 0 !important;
    height: 40px;
    line-height: 40px;
    padding-left: 15px;
    padding-right: 15px;
    font-size: 14px;
    letter-spacing: 0.4px;
    color: #333333;
    text-overflow: ellipsis;
    font-weight: 400;
    white-space: nowrap;
  }
}
</style>
