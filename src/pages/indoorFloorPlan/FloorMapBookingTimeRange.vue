<template>
  <div class="floormap-time-booking d-flex ">
    <FloorMapDatePicker
      v-bind:hideClear="true"
      v-model="date"
      :type="'date'"
      class="floormap-datepicker mR15 fc-input-full-border2"
      @change="dateChanged"
    />
    <FloorMapTImeRangePicker
      v-model="timeRange"
      @change="timeChanged"
      :customTime="customTime"
      class="mR15"
    />
    <el-button class="find-space-btn" type="primary" @click="findSpace">{{
      'Find Space'
    }}</el-button>
  </div>
</template>
<script>
import FloorMapTImeRangePicker from 'src/pages/indoorFloorPlan/FloorMapTImeRangePicker.vue'
import FloorMapDatePicker from 'src/pages/indoorFloorPlan/FloorMapDatePicker.vue'
import moment from 'moment'

export default {
  props: ['customTime'],
  components: { FloorMapDatePicker, FloorMapTImeRangePicker },
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
    timeChanged(data) {
      this.timeRange = data
    },
    dateChanged() {
      // this method should be empty to prevent the date change api calls
    },
    findSpace() {
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
<style>
.floormap-datepicker .el-input__inner {
  height: 36px !important;
  font-size: 14px;
  font-weight: 500;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: normal;
  text-align: justify;
  color: #452d88;
  padding-left: 47px !important;
  border-radius: 4px !important;
}
.floormap-datepicker .el-input__inner:hover {
  border-color: #0053cc !important;
}
.floormap-datepicker .el-input__inner:focus {
  border-color: #0053cc !important;
}
.floormap-datepicker span.el-input__prefix {
  border-right: 0.5px solid #e0e2e4;
  height: 36px;
  width: 36px;
  left: 0;
  color: #324056;
}
.floormap-datepicker {
  width: 250px !important;
}
button.el-button.find-space-btn.el-button--primary {
  border-radius: 4px;
  background-color: #0053cc;
  border-color: #0053cc;
  font-size: 14px;
  font-weight: 500;
  font-stretch: normal;
  font-style: normal;
  line-height: 14px;
  letter-spacing: normal;
  text-align: center;
  color: #fff;
  padding: 10px 15px;
}
</style>
