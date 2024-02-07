<template>
  <div>
    <div :class="labelClass">{{ label ? label : 'Duration' }}</div>
    <el-row :gutter="10">
      <slot name="prefix"></slot>
      <el-col :span="size == 'l' ? 8 : 5">
        <el-input
          type="number"
          v-model="duration.day"
          min="0"
          style="width:37%"
          @change="updateDuration('manual-entry')"
        ></el-input>
        <span class="textcolor pL10">Day(s)</span>
      </el-col>
      <el-col :span="11">
        <el-select
          v-model="duration.hour"
          style="width: 50%"
          @change="updateDuration('manual-entry')"
        >
          <el-option
            v-for="hour in getNumberRange(23)"
            :key="hour.value"
            :label="hour.label"
            :value="hour.value"
          ></el-option>
        </el-select>
        <span class="textcolor pL10">Hour(s)</span>
      </el-col>
    </el-row>
    <el-row v-for="(v, i) in workTimings" :key="i">
      <div class="creteria-delete-new">
        <el-date-picker
          v-model="workTimings[i]"
          type="datetimerange"
          start-placeholder="Start Time"
          end-placeholder="End Time"
          format="yyyy-MM-dd HH"
          value-format="timestamp"
          range-separator="To"
          @change="updateDuration('split')"
        >
        </el-date-picker>
        <img
          src="~assets/add-icon.svg"
          v-if="workTimings.length === parseInt(i) + 1"
          style="height:18px;width:37px;"
          class="delete-icon"
          @click="addTiming"
        />
        <img
          src="~assets/remove-icon.svg"
          v-if="i !== 0"
          style="height:18px;width:18px;margin-right: 3px;"
          class="delete-icon"
          @click="deleteTiming(i)"
        />
      </div>
    </el-row>
  </div>
</template>
<script>
import moment from 'moment-timezone'
export default {
  props: ['value', 'label', 'labelClass', 'format', 'size'],
  data() {
    return {
      duration: {
        day: 0,
        hour: 0,
      },
      workTimings: [],
      now: moment(),
    }
  },
  watch: {
    duration: {
      handler: function() {
        this.onValueChange()
      },
      deep: true,
    },
  },
  mounted() {
    this.workTimings.push([
      Number(this.now.format('x')),
      Number(this.now.format('x')),
    ])
    let unwatch = this.$watch(
      'value',
      function(value) {
        if (value && value.duration !== -1) {
          let duration = value.duration
          let factor = 1
          if (this.format === 'h') {
            factor = 1
          } else {
            factor = 3600 // In seconds
          }
          let days = Math.floor(duration / (24 * factor))
          duration -= days * 24 * factor
          let hours = Math.floor(duration / factor)
          this.duration.day = days
          this.duration.hour = hours
          unwatch()
        }
      },
      { immediate: true }
    )
  },
  methods: {
    updateDuration(val) {
      if (val === 'manual-entry') {
        let start = this.now
          .clone()
          .subtract(this.duration.day, 'days')
          .subtract(this.duration.hour, 'hours')
        this.workTimings = [
          [Number(start.format('x')), Number(this.now.format('x'))],
        ]
        this.onValueChange()
      } else if (val === 'split') {
        this.setDuration(this.workTimings)
      }
    },
    setDuration(val) {
      let totalDuration = moment.duration(0)
      val.forEach(e => {
        if (e.length !== 0) {
          let duration = moment.duration(
            moment(e[1], 'x').diff(moment(e[0], 'x'))
          )
          totalDuration.add(duration)
        }
      })
      this.$set(this.duration, 'day', totalDuration.get('days'))
      this.$set(this.duration, 'hour', totalDuration.get('hours'))
    },
    addTiming() {
      this.workTimings.push([])
    },
    deleteTiming(i) {
      this.workTimings.splice(i, 1)
    },
    getNumberRange(no) {
      let list = []
      for (let i = 0; i <= no; i++) {
        list.push({
          label: i + '',
          value: i,
        })
      }
      return list
    },
    onValueChange() {
      let factor = 1
      if (this.format === 'h') {
        factor = 1
      } else {
        factor = 3600 // In seconds
      }
      let duration = this.duration.day * 24 * factor
      duration += this.duration.hour * factor
      duration = duration || -1
      this.$emit('input', { workTimings: this.workTimings, duration })
    },
  },
}
</script>
<style>
.creteria-delete-new {
  padding-bottom: 2px;
  height: 20px;
}
</style>
