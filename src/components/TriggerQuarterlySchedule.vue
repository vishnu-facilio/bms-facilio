<template>
  <div>
    <el-row class="mT20">
      <div class="fc-input-label-txt">Execution Start Month</div>
      <el-col :span="24">
        <el-date-picker
          v-model="triggerEdit.startTime"
          :picker-options="dateOptions"
          class="form-item fc-input-full-border-select2 date-picker-trigger"
          style="width:100%"
          float-label="Start Date"
          type="month"
          suffix-icon="el-icon-date"
          value-format="timestamp"
        />
      </el-col>
    </el-row>
    <el-row class="mT20">
      <div class="fc-input-label-txt">Execution End Month</div>
      <el-col :span="24">
        <el-date-picker
          v-model="triggerEdit.schedule.endDate"
          :picker-options="dateOptions"
          class="form-item fc-input-full-border-select2 date-picker-trigger"
          style="width:100%"
          float-label="End Date"
          type="month"
          suffix-icon="el-icon-date"
          value-format="timestamp"
        />
      </el-col>
    </el-row>
    <el-row class="mT20">
      <div class="fc-input-label-txt">Months</div>
      <el-col :span="24">
        <el-select
          v-model="triggerEdit.schedule['monthValue']"
          class="fc-input-full-border2 width100"
        >
          <el-option
            v-for="f in monthOfQuarter"
            :label="f.label"
            :value="f.value"
            :key="f.value"
          ></el-option>
        </el-select>
      </el-col>
    </el-row>
    <el-row class="mT20">
      <div class="fc-input-label-txt">Based On</div>
      <el-col :span="24">
        <el-radio-group
          v-model="triggerEdit['basedOn']"
          @change="onBasedOnChange"
        >
          <el-radio
            v-for="f in basedOn"
            :label="f"
            :value="f"
            :key="f"
            class="fc-radio-btn"
            >{{ f }}</el-radio
          >
        </el-radio-group>
      </el-col>
    </el-row>
    <el-row v-if="triggerEdit['basedOn'] === 'Date'" class="mT20">
      <div class="fc-input-label-txt text-left">Execution Dates</div>
      <el-col :span="24">
        <el-select
          class="fc-input-full-border2 width100 fc-tag"
          v-model="triggerEdit.schedule.values"
          multiple
          collapse-tags
        >
          <el-option
            v-for="day in getNumberRange(31)"
            :key="day"
            :label="day.label"
            :value="day.value"
          ></el-option>
        </el-select>
      </el-col>
    </el-row>
    <el-row v-if="triggerEdit['basedOn'] === 'Week'" class="mT20">
      <div class="fc-input-label-txt text-left">Scheduled Dates</div>
      <el-col :span="24">
        <el-col :span="5" class="pR10">
          <el-select
            class="fc-input-full-border2 width100"
            v-model="triggerEdit.schedule.weekFrequency"
          >
            <el-option
              v-for="(val, k) in getWeekList()"
              :key="k"
              :label="val.label"
              :value="val.value"
            ></el-option>
          </el-select>
        </el-col>
        <el-col :span="19" class="pL10">
          <el-select
            class="fc-input-full-border2 width100 fc-tag"
            v-model="triggerEdit.schedule.values"
            multiple
            collapse-tags
          >
            <el-option
              v-for="(val, k) in getWeekDays()"
              :key="k"
              :label="val.label"
              :value="val.value"
            ></el-option>
          </el-select>
        </el-col>
      </el-col>
    </el-row>
    <el-row class="mT20">
      <div class="fc-input-label-txt">Execute Time</div>
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
    <el-row v-if="!(hideFields && hideFields['skipEvery'])" class="mT20">
      <div class="fc-input-label-txt">Skip</div>
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
      executeOnWeek: [],
      dayNames: {
        Mon: 'Monday',
        Tue: 'Tuesday',
        Wed: 'Wednesday',
        Thu: 'Thursday',
        Fri: 'Friday',
        Sat: 'Saturday',
        Sun: 'Sunday',
      },
      monthOfQuarter: [
        { label: 'January / April / July / October', value: 1 },
        { label: 'February / May / August / November', value: 2 },
        { label: 'March / June / September / December', value: 3 },
      ],
      checked: false,
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
      timesOption: [],
    }
  },
  mounted() {
    if (this.triggerEdit.schedule.skipEvery > 0) {
      this.checked = true
    }

    for (let i = 0; i <= 23; i++) {
      let time = (i < 10 ? '0' + i : i) + ':'
      this.timesOption.push(time + '00')
      this.timesOption.push(time + '30')
    }
  },
  methods: {
    getWeekList() {
      let options = [
        {
          label: 'First',
          value: 1,
        },
        {
          label: 'Second',
          value: 2,
        },
        {
          label: 'Third',
          value: 3,
        },
        {
          label: 'Fourth',
          value: 4,
        },
        {
          label: 'Last',
          value: 5,
        },
      ]
      return options
    },
    getWeekDays() {
      let list = [
        {
          label: this.dayNames['Mon'],
          value: 1,
        },
        {
          label: this.dayNames['Tue'],
          value: 2,
        },
        {
          label: this.dayNames['Wed'],
          value: 3,
        },
        {
          label: this.dayNames['Thu'],
          value: 4,
        },
        {
          label: this.dayNames['Fri'],
          value: 5,
        },
        {
          label: this.dayNames['Sat'],
          value: 6,
        },
        {
          label: this.dayNames['Sun'],
          value: 7,
        },
      ]
      return list
    },
    onBasedOnChange(val) {
      if (this.triggerEdit.basedOn === 'Week') {
        this.triggerEdit.schedule.weekFrequency = 1
      } else {
        this.triggerEdit.schedule.weekFrequency = -1
      }
      this.triggerEdit.schedule.values = [1]
    },
  },
}
</script>
