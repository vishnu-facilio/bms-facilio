<template>
  <div class="booking-time-range d-flex mT5">
    <FDatePicker
      v-bind:hideClear="true"
      v-model="date"
      :type="'date'"
      class="fc-input-full-border2 "
      @change="dateChanged"
    ></FDatePicker>
    <TimeRangePicker
      v-model="timeRange"
      @change="dateChanged"
      :customTime="customTime"
    >
    </TimeRangePicker>
    <!-- to do handle Parent->child init data in v model , currently only emitted data is correct -->
  </div>
</template>

<script>
import FDatePicker from 'pages/assets/overview/FDatePicker'
import moment from 'moment'
import TimeRangePicker from './TimeRangePicker'
export default {
  components: {
    FDatePicker,
    TimeRangePicker,
  },
  props: ['value', 'customTime'],
  mounted() {
    if (this.customTime.date) {
      this.date = parseInt(this.customTime.date)
    }
  },
  data() {
    return {
      timeRange: {
        timeMillis: [32400000, 61200000], //default 9AM-5PM
        timeStrings: ['09:00 : AM', '05:00 : PM'],
      },

      date: moment()
        .startOf('day')
        .valueOf(),
    }
  },
  methods: {
    dateChanged() {
      this.$emit('input', {
        startTime: this.date + parseInt(this.timeRange.timeMillis[0]),
        endTime: this.date + parseInt(this.timeRange.timeMillis[1]),
      })
      this.$emit('change', {
        startTime: this.date + parseInt(this.timeRange.timeMillis[0]),
        endTime: this.date + parseInt(this.timeRange.timeMillis[1]),
      })
    },
  },
}
</script>

<style lang="scss">
.booking-time-range {
  .el-date-editor {
    width: 200px;
    cursor: pointer;
    margin-right: 20px;
    .el-input__inner {
      height: 34px !important;
      line-height: 34px !important;
      padding-left: 35px !important;
      border: 1px solid #efefef !important;
      color: #324056;
      font-size: 15px;
      border-radius: 5px !important;
      &:hover {
        background: rgba(57, 179, 194, 0.1);
        -webkit-transition: 0.6s all;
        transition: 0.6s all;
      }
    }
    .el-input__prefix {
      cursor: pointer;
      position: none;
      color: #605e88;
      font-size: 16px;
      top: -3px;
    }
  }
}
</style>
