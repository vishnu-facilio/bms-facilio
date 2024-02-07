<template>
  <div>
    <el-row class="mT20">
      <div class="fc-input-label-txt">Execution Start Date</div>
      <el-col :span="24">
        <el-date-picker
          v-model="triggerEdit.startTime"
          :picker-options="dateOptions"
          class="form-item fc-input-full-border-select2 date-picker-trigger"
          style="width:100%"
          float-label="Start Date"
          type="date"
          suffix-icon="el-icon-date"
          value-format="timestamp"
        />
      </el-col>
    </el-row>
    <el-row class="mT20">
      <div class="fc-input-label-txt">Execution On</div>
      <el-col :span="24">
        <el-select
          v-model="triggerEdit.schedule.times[0]"
          class="fc-input-full-border2 width100 pB10"
        >
          <el-option
            v-for="time in timesOption"
            :label="time"
            :value="time"
            :key="time"
          ></el-option>
        </el-select>
      </el-col>
    </el-row>
  </div>
</template>
<script>
export default {
  props: ['triggerEdit'],
  data() {
    return {
      timesOption: [],
      dateOptions: {
        disabledDate(time) {
          let today = new Date()
          today.setHours(0, 0, 0, 0)
          let fiveYear = new Date()
          fiveYear.setFullYear(today.getFullYear() + 5)
          return (
            time.getTime() < today.getTime() ||
            time.getTime() > fiveYear.getTime()
          )
        },
      },
    }
  },
  mounted() {
    for (let i = 0; i <= 23; i++) {
      let time = (i < 10 ? '0' + i : i) + ':'
      this.timesOption.push(time + '00')
      this.timesOption.push(time + '30')
    }
  },
  methods: {
    handleChange(val) {
      if (val) {
        this.triggerEdit.schedule.skipEvery = 1
      } else {
        this.triggerEdit.schedule.skipEvery = this.empty
      }
    },
    addTimes() {
      this.triggerEdit.schedule.times.push('00:00')
    },
    removeTimes(k) {
      this.triggerEdit.schedule.times.splice(k, 1)
    },
  },
}
</script>
