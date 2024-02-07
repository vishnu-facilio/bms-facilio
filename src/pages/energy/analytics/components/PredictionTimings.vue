<template>
  <div v-if="timings && timings.length" class="prediction-filter mL20 fc-tag">
    <div class="fc-grey-text12 pB10">Prediction Timings:</div>
    <el-tag
      v-for="(time, index) in timings"
      :key="index"
      :closable="index !== 0 || timings.length > 1"
      @close="removeTime(index)"
      @click="$refs['prediction-time-picker' + index].focus()"
      class="pointer"
    >
      {{ time | formatDate }}
      <div class="flLeft date-picker">
        <el-date-picker
          :ref="'prediction-time-picker' + index"
          prefix-icon=" "
          clear-icon=" "
          v-model="timings[index]"
          align="right"
          type="datetime"
          :picker-options="pickerOptions"
          value-format="timestamp"
          @change="changeTime(index)"
        >
        </el-date-picker>
      </div>
    </el-tag>
    <el-button
      size="small"
      @click="$refs['new-prediction-time-picker'].focus()"
      class="plain p10"
      >+ Add Time</el-button
    >
    <div class="flLeft date-picker">
      <el-date-picker
        :ref="'new-prediction-time-picker'"
        prefix-icon=" "
        clear-icon=" "
        v-model="newTime"
        align="right"
        type="datetime"
        class="date-picker flLeft"
        value-format="timestamp"
        @change="addTime"
        :picker-options="pickerOptions"
      >
      </el-date-picker>
    </div>
  </div>
</template>
<script>
import AnalyticsMixin from 'pages/energy/analytics/mixins/AnalyticsMixin'
export default {
  props: ['config'],
  mixins: [AnalyticsMixin],
  data() {
    return {
      showPicker: false,
      newTime: null,
      timings: [],
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() > Date.now()
        },
      },
    }
  },
  mounted() {
    this.timings = this.$helpers.cloneObject(this.config.predictionTimings)
  },
  methods: {
    removeTime(index) {
      this.removePrediction(index)
      this.config.predictionTimings.splice(index, 1)
      this.timings.splice(index, 1)
    },
    changeTime(index) {
      let time = this.getTime(this.timings[index])
      this.changePredictionTime(index, time)
      this.$set(this.config.predictionTimings, index, time)
      this.$set(this.timings, index, time)
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
  },
}
</script>

<style>
.prediction-filter .date-picker input[type='text'] {
  border: none;
  background-color: transparent;
  text-shadow: 0 0 0 #25243e;
  text-align: center;
  cursor: pointer;
  font-size: 0;
  width: 120px;
}

.prediction-filter .date-picker .el-date-editor {
  width: 0px;
}
.prediction-filter .date-picker .el-icon-date,
.prediction-filter .date-picker .el-icon-circle-close {
  display: none;
}

.prediction-filter .el-tag__close {
  display: none;
}

.prediction-filter .el-tag:hover .el-tag__close {
  display: inline-block;
}
</style>
