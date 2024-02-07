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
          float-label="End Month"
          type="month"
          suffix-icon="el-icon-date"
          value-format="timestamp"
        />
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
            v-for="(f, key) in basedOn"
            :label="f"
            :value="f"
            :key="key"
            class="fc-radio-btn"
            >{{ f }}</el-radio
          >
        </el-radio-group>
      </el-col>
    </el-row>
    <el-row v-if="triggerEdit['basedOn'] === 'Date'" class="mT20">
      <div class="fc-input-label-txt text-left">Execution Date</div>
      <el-col :span="24">
        <el-select
          class="fc-input-full-border2 width100 fc-tag"
          @change="changeMonthlyDate(triggerEdit.schedule)"
          v-model="monthlyDate"
          multiple
          collapse-tags
        >
          <el-option
            v-for="day in getMonthlyDateOptions()"
            :key="day"
            :label="day.label"
            :value="day.value"
          ></el-option>
        </el-select>
      </el-col>
    </el-row>
    <el-row v-if="triggerEdit['basedOn'] === 'Date'" class="mT20">
      <div class="fc-input-label-txt text-left">Execution Times</div>
      <el-col :span="24">
        <el-select
          v-model="triggerEdit.schedule.times"
          multiple
          collapse-tags
          class="fc-input-full-border2 width100 fc-tag"
        >
          <el-option
            v-for="(time, key) in timesOption"
            :label="time"
            :value="time"
            :key="key"
          ></el-option>
        </el-select>
      </el-col>
    </el-row>
    <el-row v-if="triggerEdit['basedOn'] === 'Week'" class="mT20">
      <div class="fc-input-label-txt text-left">Execute On</div>
      <el-col :span="24">
        <el-col :span="5" class="pR10">
          <el-select
            class="fc-input-full-border2 width100"
            v-model="weekFrequency"
            @change="changeWeekFrequency(triggerEdit.schedule)"
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
    <el-row v-if="triggerEdit['basedOn'] === 'Week'" class="mT20">
      <div class="fc-input-label-txt text-left">Execution Time</div>
      <el-col :span="24">
        <el-select
          v-model="triggerEdit.schedule.times"
          class="fc-input-full-border2 width100 fc-tag"
          collapse-tags
          multiple
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
      weekFrequency: -1,
      monthlyDate: [],
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
      freq: [
        {
          name: 'Month',
          value: 1,
        },
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
    for (let i = 0; i <= 23; i++) {
      let time = (i < 10 ? '0' + i : i) + ':'
      this.timesOption.push(time + '00')
      this.timesOption.push(time + '30')
    }

    for (let i = 2; i <= 60; i++) {
      this.freq.push({
        name: `${i} Months`,
        value: i,
      })
    }
  },
  created() {
    let { triggerEdit } = this
    let { schedule } = triggerEdit || {}
    let { variableEnum, weekFrequency, values } = schedule || {}
    if (variableEnum === 'LAST_WEEK') {
      this.weekFrequency = 'LASTWEEK'
    } else {
      this.weekFrequency = weekFrequency
    }
    this.monthlyDate = this.$helpers.cloneObject(values)
    if (variableEnum === 'LAST_DAY') {
      this.monthlyDate.push('LASTDAY')
    }
  },
  methods: {
    changeMonthlyDate(schedule) {
      let { monthlyDate } = this
      let newDateValues = []
      delete schedule.variableEnum
      delete schedule.variable
      if (monthlyDate) {
        monthlyDate.forEach(function(item) {
          if (item === 'LASTDAY') {
            schedule.variableEnum = 'LAST_DAY'
            schedule.variable = 0
          } else {
            newDateValues.push(item)
          }
        })
      }
      schedule.values = newDateValues
    },
    changeWeekFrequency(schedule) {
      let { weekFrequency } = this
      if (weekFrequency === 'LASTWEEK') {
        schedule.variableEnum = 'LAST_WEEK'
        schedule.variable = 1
        schedule.weekFrequency = -1
      } else {
        schedule.weekFrequency = weekFrequency
        delete schedule.variableEnum
        delete schedule.variable
      }
    },
    getMonthlyDateOptions() {
      let rangeList = this.getNumberRange(31)
      rangeList.push({
        label: 'Last Day',
        value: 'LASTDAY',
      })
      return rangeList
    },
    getWeekList() {
      let options = [
        {
          label: 'First',
          value: 1,
          children: this.getWeekDays(),
        },
        {
          label: 'Second',
          value: 2,
          children: this.getWeekDays(),
        },
        {
          label: 'Third',
          value: 3,
          children: this.getWeekDays(),
        },
        {
          label: 'Fourth',
          value: 4,
          children: this.getWeekDays(),
        },
        {
          label: 'Fifth',
          value: 5,
          children: this.getWeekDays(),
        },
        {
          label: 'Last Week',
          value: 'LASTWEEK',
          children: this.getWeekDays(),
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
        this.weekFrequency = 1
        this.triggerEdit.schedule.weekFrequency = 1
      } else {
        this.weekFrequency = -1
        this.triggerEdit.schedule.weekFrequency = -1
      }
      this.triggerEdit.schedule.values = [1]
    },
  },
  watch: {},
}
</script>
