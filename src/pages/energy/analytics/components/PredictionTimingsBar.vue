<template>
  <div
    v-if="localtimings && localtimings.length"
    class="prediction-component row"
  >
    <div class="col-12 pr-date-picker">
      <div class="pr-label">
        <i class="el-icon-time"></i> Predicted Timing :
      </div>
      <el-date-picker
        :ref="'new-prediction-time-picker'"
        prefix-icon=" "
        clear-icon=" "
        v-model="newTime"
        align="right"
        type="date"
        class="date-picker flLeft"
        value-format="timestamp"
        :picker-options="pickerOptions"
        @change="changedate"
      >
      </el-date-picker>
    </div>
    <div class="date-picker col-12">
      <div
        v-for="(time, index) in localtimings"
        :key="index"
        class="prediction-container"
        :class="{ active: getActive(time), disabled: time >= currentTime }"
      >
        <div
          class="prediction-cube"
          @click="addNewTime(time)"
          :class="{ active: getActive(time) }"
        >
          <div class="prediction-time">
            {{ formatTime(time, 'h a', false) }}
          </div>
          <div :class="{ active: getActive(time) }"></div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import AnalyticsMixin from 'pages/energy/analytics/mixins/AnalyticsMixin'
import moment from 'moment-timezone'
import newDateHelper from '@/mixins/NewDateHelper'
export default {
  props: ['config', 'reportObj'],
  mixins: [AnalyticsMixin, newDateHelper],
  data() {
    let time = moment(new Date().valueOf())
      .tz(this.$timezone)
      .startOf('day')
      .valueOf()
    return {
      showPicker: false,
      localtimings: [],
      splitRatio: 10,
      newTime: time,
      timings: [],
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() > new Date().valueOf()
        },
      },
    }
  },
  watch: {
    'config.dataPoints': {
      handler(newData, oldData) {
        this.reloadBar()
      },
      deep: true,
    },
  },
  computed: {
    currentTime() {
      return moment()
        .tz(this.$timezone)
        .startOf('hour')
        .valueOf()
    },
    today() {
      return moment()
        .tz(this.$timezone)
        .startOf('day')
        .valueOf()
    },
  },
  mounted() {
    this.timings = this.$helpers.cloneObject(this.config.predictionTimings)
    this.getFullDayTimes(false)
  },
  methods: {
    removeTime(index) {
      this.removePrediction(index)
      this.config.predictionTimings.splice(index, 1)
      this.timings.splice(index, 1)
    },
    reloadBar() {
      this.timings = this.$helpers.cloneObject(this.config.predictionTimings)
    },
    formatTime(date, period, enableFormating) {
      let dateFormat = period || 'DD-MM-YYY'
      let today = moment()
        .tz(this.$timezone)
        .startOf('day')
        .valueOf()
      let yesterday = moment()
        .tz(this.$timezone)
        .subtract(1, 'day')
        .startOf('day')
        .valueOf()
      if (date > yesterday && enableFormating && date <= today) {
        return 'YESTERDAY'
      } else if (date > today && enableFormating) {
        return 'TODAY'
      } else {
        return moment(date)
          .tz(this.$timezone)
          .format(dateFormat)
      }
    },
    changeTime(index) {
      let time = this.getTime(this.timings[index])
      this.changePredictionTime(index, time)
      this.$set(this.config.predictionTimings, index, time)
      this.$set(this.timings, index, time)
    },
    addNewTime(time) {
      if (time <= this.currentTime) {
        if (this.timings.findIndex(rt => rt === time) < 0) {
          this.timings.push(time)
          // this.setDateFilter(this.timings)
          this.config.predictionTimings.push(time)
          this.addPredictionFields(this.config)
        } else {
          this.removeTime(this.timings.findIndex(rt => rt === time))
        }
      }
    },
    // setDateFilter (time) {
    //   if (time && time.length) {
    //     let dateFilter = {}
    //     time = time.sort()
    //     if (time.length === 1 && time[0] < this.today) {
    //       dateFilter = newDateHelper.getDatePickerObject(20, [this.$options.filters.startOfDay(time[0]), this.$options.filters.endOfDay(time[0])])
    //        this.config.dateFilter = dateFilter
    //     }
    //     else if (time[0] < this.today) {
    //       dateFilter = newDateHelper.getDatePickerObject(20, [this.$options.filters.startOfDay(time[0]), this.$options.filters.endOfDay(time[time.length - 1])])
    //        this.config.dateFilter = dateFilter
    //        this.config.period = 20
    //     }
    //   }
    // },
    getActive(time) {
      if (this.timings.findIndex(rt => rt === time) < 0) {
        return false
      } else if (this.timings.findIndex(rt => rt === time) > -1) {
        return true
      }
    },
    addTime() {
      let time = this.getTime(this.newTime)
      this.config.predictionTimings.push(time)
      this.timings.push(time)
      this.newTime = null
      this.addPredictionFields(this.config)
    },
    getTime(time) {
      return this.$options.filters.startOfHour(this.$helpers.getTimeInOrg(time))
    },
    getDpIndexes(predictionIndex) {
      let oldTime = this.config.predictionTimings[predictionIndex]
      let indexes = []
      this.config.dataPoints.forEach((point, idx) => {
        if (point.prediction && point.predictedTime === oldTime) {
          indexes.push(idx)
        }
      })
      return indexes
    },
    selectPredictedTime() {
      this.config.predictionTimings = this.timings
    },
    removePrediction(index) {
      let indexes = this.getDpIndexes(index)
      indexes.forEach(idx => {
        this.config.dataPoints.splice(idx, 1)
      })
    },

    changePredictionTime(index, newTime) {
      let indexes = this.getDpIndexes(index)
      indexes.forEach(idx => {
        this.$set(this.config.dataPoints[idx], 'predictedTime', newTime)
      })
    },
    changedate() {
      console.log('****** timings', this.newTime)
      this.getFullDayTimes()
    },
    getFullDayTimes(mounted) {
      let timings = []
      let time = moment(this.newTime)
        .tz(this.$timezone)
        .valueOf()
      for (let i = 0; i < 24; i++) {
        timings.push(
          this.$options.filters.nextHour(this.$helpers.getTimeInOrg(time), i)
        )
      }
      this.localtimings = this.$helpers.cloneObject(timings)
    },
  },
}
</script>

<style>
.prediction-cube {
  width: 53px;
  text-align: center;
  border: 1px solid #d0d9e2;
  position: relative;
  border-left: 0;
  cursor: pointer;
  height: 30px;
  padding-top: 5px;
  white-space: nowrap;
}
.prediction-day {
  font-size: 10px;
  line-height: 20px;
  padding: 15px;
  font-size: 9px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  letter-spacing: 0.4px;
  color: #8ca1ad;
  padding-top: 2px;
  padding-bottom: 0px;
}

.prediction-time {
  font-size: 14px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: normal;
  color: #324056;
  opacity: 0.5;
}
/* .prediction-cube .active {
  position: absolute;
  bottom: 0;
  width: 100%;
  height: 2px;
  background: #ff3e90;
} */
.prediction-cube.active .prediction-time {
  color: #324056;
  opacity: 1;
}
.prediction-component .el-carousel__item {
  display: flex;
}
.prediction-component .el-carousel__arrow {
  height: 30px;
  width: 30px;
  border-radius: 0;
  right: 0;
}
.prediction-component button.el-carousel__arrow.el-carousel__arrow--left {
  left: 0px;
}
.prediction-component button.el-carousel__arrow.el-carousel__arrow--right {
  right: 0px;
}
.prediction-component .el-carousel__indicators {
  display: none;
}
.prediction-container:first-child .prediction-cube {
  border-left: 1px solid #d0d9e2;
}
.prediction-cube.active {
  box-shadow: 0 2px 10px 0 rgba(47, 50, 51, 0.1);
  border: solid 1px #9ed7de;
  background-color: #f9feff;
  border-left: 0;
  border-bottom: 2px solid #ff3e90;
}
.prediction-container.active:first-child .prediction-cube.active {
  border-left: 2px solid #9ed7de;
}
.prediction-timing-bar {
  padding: 10px;
  padding-right: 0;
  padding-bottom: 20px;
}
.predictive-header {
  float: right;
  font-size: 14px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.5px;
  text-align: right;
  color: #324056;
  cursor: pointer;
}
img.predictive-arrow {
  right: 150px;
  float: right;
}
.date-picker.col-12 {
  width: 100%;
  overflow-x: scroll;
  padding-left: 50px;
  padding-right: 30px;
  display: inline-flex;
}
.prediction-container {
  width: 90px;
  height: 50px;
}
.pr-date-picker {
  position: relative;
  right: 30px;
  top: -5px;
}
.pr-date-picker .date-picker {
  position: absolute;
  bottom: 20px;
  width: 90px;
  right: 50px;
}
.pr-date-picker .date-picker .el-input__inner {
  cursor: pointer;
  text-align: right;
  padding-left: 0 !important;
  border-bottom: 0;
  font-size: 14px;
  font-weight: 500;
}
.pr-date-picker .pr-label {
  position: absolute;
  bottom: 27px;
  right: 140px;
  float: right;
  font-size: 13px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.5px;
  text-align: right;
  color: #324056;
  cursor: pointer;
}
</style>
