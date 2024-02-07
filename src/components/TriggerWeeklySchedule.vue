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
      <div class="fc-input-label-txt">Execution End Date</div>
      <el-col :span="24">
        <el-date-picker
          v-model="triggerEdit.schedule.endDate"
          :picker-options="dateOptions"
          class="form-item fc-input-full-border-select2 date-picker-trigger"
          style="width:100%"
          float-label="End Date"
          type="date"
          suffix-icon="el-icon-date"
          value-format="timestamp"
        />
      </el-col>
    </el-row>
    <el-row class="mT20">
      <div class="fc-input-label-txt text-left">Execution Days</div>
      <el-col :span="24">
        <el-select
          class="fc-input-full-border2 width100 fc-tag"
          v-model="triggerEdit.schedule.values"
          multiple
          collapse-tags
        >
          <el-option
            v-for="day in days"
            :key="day"
            :label="day"
            :value="dayValues[day]"
          ></el-option>
        </el-select>
      </el-col>
    </el-row>
    <el-row class="mT20">
      <div class="fc-input-label-txt text-left">Execution Time</div>
      <el-col :span="24">
        <el-select
          v-model="triggerEdit.schedule.times"
          class="fc-input-full-border2 width100 fc-tag"
          multiple
          collapse-tags
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
    <el-row class="mT20">
      <div class="fc-input-label-txt">Run Every</div>
      <el-col :span="24">
        <div>
          <el-select
            v-model="triggerEdit.schedule['frequency']"
            class="fc-input-full-border2 width100"
          >
            <el-option
              v-for="f in freq"
              :label="f.name"
              :value="f.value"
              :key="f.value"
            ></el-option>
          </el-select>
        </div>
      </el-col>
    </el-row>
    <el-row v-if="!(hideFields && hideFields['skipEvery'])" class="mT20">
      <div class="fc-input-label-txt">Skip Every</div>
      <el-col :span="24">
        <el-select
          v-model="triggerEdit.schedule['skipEvery']"
          class="fc-input-full-border2 width100"
        >
          <el-option
            v-for="(s, key) in skipOptions"
            :label="s.label"
            :value="s.value"
            :key="key"
          ></el-option>
        </el-select>
      </el-col>
    </el-row>
  </div>
</template>
<script>
import TriggerMixin from '@/mixins/TriggerMixin'
export default {
  props: ['triggerEdit', 'hideFields'],
  mixins: [TriggerMixin],
  data() {
    return {
      days: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
      dayValues: {
        Mon: 1,
        Tue: 2,
        Wed: 3,
        Thu: 4,
        Fri: 5,
        Sat: 6,
        Sun: 7,
      },
      freq: [
        {
          name: 'Week',
          value: 1,
        },
      ],
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
      checked: false,
      empty: null,
    }
  },
  mounted() {
    for (let i = 2; i <= 60; i++) {
      this.freq.push({
        name: `${i} Weeks`,
        value: i,
      })
    }

    for (let i = 0; i <= 23; i++) {
      let time = (i < 10 ? '0' + i : i) + ':'
      this.timesOption.push(time + '00')
      this.timesOption.push(time + '30')
    }
  },
}
</script>
