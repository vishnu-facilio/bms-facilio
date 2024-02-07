<template>
  <el-row class="schedule-layout" :gutter="80" align="middle">
    <el-col :span="24">
      <div class="fc-input-label-txt pB10">
        {{ $t('maintenance._workorder.frequency') }}
      </div>
      <el-select
        :style="{
          width:
            !schedule.custom &&
            schedule.facilioFrequency !== 0 &&
            schedule.facilioFrequency !== 1
              ? '50%'
              : '100%',
        }"
        v-model="schedule.facilioFrequency"
        class="fc-input-full-border-select2"
      >
        <el-option
          v-for="(ftype, index) in frequencyTypes"
          :key="index"
          :label="ftype.label"
          :value="parseInt(ftype.value)"
        ></el-option>
      </el-select>
      <template v-if="!schedule.custom && schedule.facilioFrequency !== 0">
        <el-select
          v-if="dayTypes[schedule.facilioFrequency] === 'week'"
          v-model="schedule.executeOnDay"
          style="width:49%"
          class="pL10 fc-input-full-border-select2"
        >
          <el-option
            v-for="day in days"
            :key="day"
            :label="dayNames[day]"
            :value="day"
          ></el-option>
        </el-select>
        <el-select
          v-else-if="dayTypes[schedule.facilioFrequency] === 'month'"
          v-model="schedule.executeOnDate"
          style="width:49%"
          class="pL10 fc-input-full-border-select2"
        >
          <el-option
            v-for="date in getNumberRange(31)"
            :key="date.value"
            :label="date.label"
            :value="date.value"
          ></el-option>
        </el-select>
        <template v-else-if="dayTypes[schedule.facilioFrequency] === 'year'">
          <el-select
            v-model="schedule.executeOnMonth"
            style="width:30%"
            class="pL10 fc-input-full-border-select2"
          >
            <el-option
              v-for="(month, index) in months"
              :key="month"
              :label="(index + 1) | monthName"
              :value="month"
            ></el-option>
          </el-select>
          <el-select
            v-model="schedule.yearlyDayValue"
            style="width:18% fc-input-full-border-select2"
          >
            <el-option
              v-for="date in getNumberRange(31)"
              :key="date.value"
              :label="date.label"
              :value="date.value"
            ></el-option>
          </el-select>
        </template>
      </template>
    </el-col>
    <el-col :span="24" v-if="schedule.facilioFrequency !== 0">
      <el-checkbox v-model="schedule.custom"
        ><div class="textcolor">
          {{ $t('maintenance._workorder.custom') }}
        </div></el-checkbox
      >
    </el-col>
    <el-col
      :span="24"
      v-if="schedule.custom && schedule.facilioFrequency !== 0"
      class="custom-frequency"
    >
      <template
        v-if="
          schedule.facilioFrequency !== 4 && schedule.facilioFrequency !== 5
        "
      >
        <div class="fc-input-label-txt pB10">
          {{ $t('maintenance._workorder.run_every') }}
        </div>
        <el-select
          style="width:100%"
          v-model="schedule.frequency"
          class="fc-input-full-border-select2"
        >
          <el-option
            v-for="(ftype, index) in repeatList"
            :key="index"
            :label="ftype.label"
            :value="ftype.value"
          ></el-option>
        </el-select>
      </template>
      <div class="fc-input-label-txt pB10">
        {{ $t('maintenance._workorder.execute_on') }}
      </div>
      <div
        v-if="
          dayTypes[schedule.facilioFrequency] === 'day' ||
            dayTypes[schedule.facilioFrequency] === 'week'
        "
        class="fc-input-full-border-select2"
      >
        <el-select
          v-model="schedule.executeOnDays"
          multiple
          style="width:100%"
          class="fc-tag"
        >
          <el-option
            v-for="day in days"
            :key="day"
            :label="dayNames[day]"
            :value="day"
          ></el-option>
        </el-select>
      </div>
      <div
        v-else-if="dayTypes[schedule.facilioFrequency] === 'month'"
        style="width:100%"
      >
        <el-col :span="8">
          <el-select
            v-model="schedule.executeOnType"
            key="typeday"
            class="fc-input-full-border-select2"
          >
            <el-option label="Day" value="day"></el-option>
            <el-option label="Week" value="week"></el-option>
          </el-select>
        </el-col>
        <el-col :span="16">
          <el-select
            v-model="schedule.executeOnDates"
            multiple
            v-if="schedule.executeOnType === 'day'"
            class="pL10 fc-input-full-border-select2"
            style="width:100%"
            key="datevalue"
          >
            <el-option
              v-for="date in getNumberRange(31)"
              :key="date.value + 'day'"
              :label="date.label"
              :value="date.value"
            ></el-option>
          </el-select>
          <el-cascader
            :options="getWeekList()"
            v-model="schedule.executeOnWeek"
            v-if="schedule.executeOnType === 'week'"
            class="pL10 fcascader"
            style="width:100%"
          >
          </el-cascader>
        </el-col>
      </div>
      <div
        v-else-if="dayTypes[schedule.facilioFrequency] === 'year'"
        class="flLeft"
        style="width:100%"
      >
        <el-col :span="16">
          <el-select
            v-model="schedule.executeOnMonths"
            multiple
            style="width:100%"
            key="months"
            class="fc-input-full-border-select2"
          >
            <el-option
              v-for="(month, index) in months"
              :key="month + 'month'"
              :label="(index + 1) | monthName"
              :value="month"
            ></el-option>
          </el-select>
        </el-col>
        <el-col :span="8">
          <el-select
            v-model="schedule.yearlyDayValue"
            class="pL10 fc-input-full-border-select2"
            style="width:100%"
          >
            <el-option
              v-for="date in getNumberRange(31)"
              :key="date.value + 'yearday'"
              :label="date.label"
              :value="date.value"
            ></el-option>
          </el-select>
        </el-col>
      </div>
    </el-col>
  </el-row>
</template>
<script>
import moment from 'moment-timezone'
import { isEmpty } from '@facilio/utils/validation'
export default {
  props: ['from', 'times', 'value', 'initialSchedule', 'simple'],
  data() {
    return {
      frequencyTypes: [],
      schedule: {
        frequencyType: 1,
        facilioFrequency: 1,
        frequency: 1,
        executeOnDay: '',
        executeOnDays: [],
        executeOnType: 'day',
        executeOnDate: '',
        executeOnDates: [],
        executeOnWeek: [],
        executeOnMonth: '',
        executeOnMonths: [],
        yearlyDayValue: '',
        custom: false,
      },
      days: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
      dayNames: {
        Mon: 'Monday',
        Tue: 'Tuesday',
        Wed: 'Wednesday',
        Thu: 'Thursday',
        Fri: 'Friday',
        Sat: 'Saturday',
        Sun: 'Sunday',
      },
      weeks: ['First', 'Second', 'Third', 'Fourth', 'Last'],
      months: [
        'Jan',
        'Feb',
        'Mar',
        'Apr',
        'May',
        'Jun',
        'Jul',
        'Aug',
        'Sep',
        'Oct',
        'Nov',
        'Dec',
      ],
      dayTypes: {
        // based on facilio frequency
        1: 'day',
        2: 'week',
        3: 'month',
        4: 'month',
        5: 'month',
        6: 'year',
      },
    }
  },
  computed: {
    startTime() {
      return this.from ? this.from : new Date()
    },
    repeatList() {
      let { dayTypes, schedule } = this
      let { facilioFrequency } = schedule

      return this.generateFrequencyTimes(dayTypes[facilioFrequency], 60)
    },
    localStartTime() {
      return moment(this.startTime).tz(moment.tz.guess())
    },
  },
  mounted() {
    let self = this
    this.loadScheduleInfo()
    this.setInitialValue()

    this.$watch(
      'schedule',
      function() {
        this.$emit('input', self.constructScheduleObj())
      },
      {
        deep: true,
      }
    )
  },
  methods: {
    getDayIndex(dayName) {
      if (!dayName) {
        dayName = this.localStartTime.format('ddd')
      }
      let dayIndex = this.days.indexOf(dayName)
      return dayIndex + 1
    },
    getMonthIndex(monthName) {
      if (!monthName) {
        monthName = this.localStartTime.format('MMM')
      }
      let monthIndex = this.months.indexOf(monthName)
      return monthIndex + 1
    },
    getWeekName(weekIndex) {
      if (!weekIndex) {
        weekIndex = Math.ceil(this.localStartTime.date() / 7)
      }
      let weekName = this.weeks[weekIndex - 1]
      if (!weekName) {
        weekName = this.weeks[this.weeks.length - 1]
      }
      return weekName
    },
    getWeekIndex(weekName) {
      if (!weekName) {
        return Math.ceil(this.localStartTime.date() / 7)
      }
      let weekIndex = this.weeks.indexOf(weekName)
      return weekIndex + 1
    },
    loadScheduleInfo() {
      this.frequencyTypes = []
      if (!this.simple) {
        this.frequencyTypes.push({
          label: 'Do not repeat',
          value: 0,
        })
      } else {
        this.facilioFrequency = 1
      }

      for (let key in this.$constants.FACILIO_FREQUENCY) {
        if (this.$constants.FACILIO_FREQUENCY.hasOwnProperty(key)) {
          this.frequencyTypes.push({
            label: this.$constants.FACILIO_FREQUENCY[key],
            value: key,
          })
        }
      }

      let dayShortName = this.localStartTime.format('ddd')
      if (!this.schedule.executeOnDays.length) {
        this.schedule.executeOnDay = dayShortName
        if (this.schedule.facilioFrequency === 1) {
          this.schedule.executeOnDays = this.days
        } else if (this.schedule.facilioFrequency === 2) {
          this.schedule.executeOnDays.push(dayShortName)
        }
      }

      let selectedDate = parseInt(this.localStartTime.format('DD'))
      if (!this.schedule.executeOnDates.length) {
        this.schedule.executeOnDate = selectedDate
        this.schedule.executeOnDates.push(selectedDate)
      }

      if (!this.schedule.executeOnWeek.length) {
        let weekOfMonthStr = this.getWeekName()
        this.schedule.executeOnWeek.push(weekOfMonthStr)
        this.schedule.executeOnWeek.push(dayShortName)
      }

      if (!this.schedule.executeOnMonths.length) {
        let month = this.localStartTime.format('MMM')
        this.schedule.executeOnMonth = month
        this.schedule.executeOnMonths = [month]
        this.schedule.yearlyDayValue = parseInt(selectedDate)
      }
    },
    constructScheduleObj() {
      let newSchedule = {
        frequencyType: 0,
        facilioFrequency: this.schedule.facilioFrequency,
        frequency: 1,
        values: [],
        times: [],
        custom: this.schedule.custom,
      }
      this.times
        ? (newSchedule.times = this.times)
        : newSchedule.times.push('00:00')

      if (this.schedule.custom) {
        newSchedule.frequency = this.schedule.frequency
      }

      if (this.schedule.facilioFrequency === 1) {
        newSchedule.frequencyType = this.schedule.facilioFrequency
        if (this.schedule.custom && this.schedule.executeOnDays.length !== 7) {
          for (let dayName of this.schedule.executeOnDays) {
            newSchedule.values.push(this.getDayIndex(dayName))
          }
        }
      } else if (this.schedule.facilioFrequency === 2) {
        newSchedule.frequencyType = this.schedule.facilioFrequency
        let days = this.schedule.custom
          ? this.schedule.executeOnDays
          : [this.schedule.executeOnDay]
        for (let dayName of days) {
          newSchedule.values.push(this.getDayIndex(dayName))
        }
      } else if ([3, 4, 5].includes(this.schedule.facilioFrequency)) {
        if (this.schedule.custom && this.schedule.executeOnType === 'week') {
          newSchedule.frequencyType = 4
          newSchedule.weekFrequency = this.getWeekIndex(
            this.schedule.executeOnWeek[0]
          )
          newSchedule.values = []
          let weekValue = this.schedule.executeOnWeek[1]
          if (weekValue === 'Whole_Week') {
            newSchedule.values = [1, 2, 3, 4, 5, 6, 7]
          } else if (weekValue === 'Mon_Fri') {
            newSchedule.values = [1, 2, 3, 4, 5]
          } else if (weekValue === 'Weekend') {
            newSchedule.values = [6, 7]
          } else {
            let dayIndex = this.getDayIndex(weekValue)
            if (dayIndex > 0) {
              newSchedule.values = [dayIndex]
            }
          }
        } else {
          newSchedule.frequencyType = 3
          let dates = this.schedule.custom
            ? this.schedule.executeOnDates
            : [this.schedule.executeOnDate]
          newSchedule.values = dates
        }

        if (this.schedule.facilioFrequency === 4) {
          newSchedule.frequency = 3
        } else if (this.schedule.facilioFrequency === 5) {
          newSchedule.frequency = 6
        }
      } else if (this.schedule.facilioFrequency === 6) {
        newSchedule.frequencyType = 5
        newSchedule.yearlyDayValue = this.schedule.yearlyDayValue
        let months = this.schedule.custom
          ? this.schedule.executeOnMonths
          : [this.schedule.executeOnMonth]
        for (let monthName of months) {
          newSchedule.values.push(this.getMonthIndex(monthName))
        }
      }

      return newSchedule
    },
    generateFrequencyList() {
      let newObj = [
        {
          value: 'day',
          label: 'Day',
          children: this.generateFrequencyTimes('day', 50),
        },
        {
          value: 'week',
          label: 'Week',
          children: this.generateFrequencyTimes('week', 50),
        },
        {
          value: 'month',
          label: 'Month',
          children: this.generateFrequencyTimes('month', 50),
        },
        {
          value: 'year',
          label: 'Year',
          children: this.generateFrequencyTimes('year', 50),
        },
      ]
      return newObj
    },
    generateFrequencyTimes(type, no) {
      let list = []

      if (!isEmpty(type)) {
        for (let i = 0; i < no; i++) {
          if (i === 0) {
            list.push({
              label: type.charAt(0).toUpperCase() + type.slice(1),
              value: 1,
            })
          } else {
            list.push({
              label: i + 1 + ' ' + type + 's',
              value: i + 1,
            })
          }
        }
      }
      return list
    },
    getWeekList() {
      let options = [
        {
          label: 'First',
          value: 'First',
          children: this.getWeekDays(),
        },
        {
          label: 'Second',
          value: 'Second',
          children: this.getWeekDays(),
        },
        {
          label: 'Third',
          value: 'Third',
          children: this.getWeekDays(),
        },
        {
          label: 'Fourth',
          value: 'Fourth',
          children: this.getWeekDays(),
        },
        {
          label: 'Last',
          value: 'Last',
          children: this.getWeekDays(),
        },
      ]
      return options
    },
    getNumberRange(no) {
      let list = []
      for (let i = 1; i <= no; i++) {
        list.push({
          label: i + '',
          value: i,
        })
      }
      return list
    },
    getWeekDays() {
      let list = [
        {
          label: 'Whole week',
          value: 'Whole_Week',
        },
        {
          label: this.dayNames['Mon'],
          value: 'Mon',
        },
        {
          label: this.dayNames['Tue'],
          value: 'Tue',
        },
        {
          label: this.dayNames['Wed'],
          value: 'Wed',
        },
        {
          label: this.dayNames['Thu'],
          value: 'Thu',
        },
        {
          label: this.dayNames['Fri'],
          value: 'Fri',
        },
        {
          label: this.dayNames['Sat'],
          value: 'Sat',
        },
        {
          label: this.dayNames['Sun'],
          value: 'Sun',
        },
        {
          label: 'Monday - Friday',
          value: 'Mon_Fri',
        },
        {
          label: 'Weekend',
          value: 'Weekend',
        },
      ]
      return list
    },
    setInitialValue() {
      if (this.initialSchedule) {
        this.schedule.facilioFrequency =
          this.initialSchedule.facilioFrequency !== -1
            ? this.initialSchedule.facilioFrequency
            : 0
        this.schedule.frequency = this.initialSchedule.frequency
        this.schedule.custom = this.initialSchedule.custom
        if (
          this.initialSchedule.facilioFrequency === 1 ||
          this.initialSchedule.facilioFrequency === 2
        ) {
          this.schedule.executeOnDays = this.initialSchedule.values.map(
            day => this.days[day - 1]
          )
          this.schedule.executeOnDay = this.schedule.executeOnDays[0]
        } else if ([3, 4, 5].includes(this.schedule.facilioFrequency)) {
          if (this.initialSchedule.weekFrequency > 0) {
            this.schedule.executeOnType = 'week'
            this.schedule.executeOnWeek[0] = this.getWeekName(
              this.initialSchedule.weekFrequency
            )
            let value
            let length = this.initialSchedule.values.length
            if (length === 7) {
              value = 'Whole_Week'
            } else if (length === 5) {
              value = 'Mon_Fri'
            } else if (length === 2) {
              value = 'Weekend'
            } else {
              value = this.days[this.initialSchedule.values[0] - 1]
            }
            this.schedule.executeOnWeek[1] = value
          } else {
            this.schedule.executeOnType = 'day'
            this.schedule.executeOnDates = this.initialSchedule.values
            this.schedule.executeOnDate = this.initialSchedule.values[0]
          }
        } else if (this.initialSchedule.facilioFrequency === 6) {
          this.schedule.executeOnMonths = this.initialSchedule.values.map(
            month => this.months[month - 1]
          )
          this.schedule.executeOnMonth = this.schedule.executeOnMonths[0]
          this.schedule.yearlyDayValue =
            this.initialSchedule.yearlyDayValue > 0
              ? this.initialSchedule.yearlyDayValue
              : ''
        }
      } else {
        this.$emit('input', this.constructScheduleObj())
      }
    },
  },
  watch: {
    startTime: function() {
      this.loadScheduleInfo()
      this.$emit('input', this.constructScheduleObj())
    },
    initialSchedule: function() {
      this.setInitialValue()
    },
    times: function() {
      this.$emit('input', this.constructScheduleObj())
    },
    'schedule.facilioFrequency'(val) {
      if (val === 1) {
        this.schedule.executeOnDays = this.days
      } else if (val === 2) {
        this.schedule.executeOnDays = [
          this.schedule.executeOnDay
            ? this.schedule.executeOnDay
            : this.localStartTime.format('ddd'),
        ]
      }
    },
  },
}
</script>
<style>
.fcascader.el-cascader {
  padding: 0px;
  line-height: 37px !important;
}
.schedule-layout .el-input__inner {
  min-height: 40px;
}

.custom-frequency .el-col {
  padding: 0 !important;
}
</style>
