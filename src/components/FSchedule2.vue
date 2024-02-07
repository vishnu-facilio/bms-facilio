<template>
  <el-row :gutter="80" align="middle">
    <el-col :span="24">
      <div class="fc-input-label-txt">
        {{ simple ? 'Frequency' : 'Repetition' }}
      </div>
      <el-select style="width:100%" v-model="schedule.frequencyType">
        <el-option
          v-for="(ftype, index) in frequencyTypes"
          :key="index"
          :label="ftype.label"
          :value="ftype.value"
        ></el-option>
      </el-select>
    </el-col>
    <el-col :span="24" v-if="schedule.frequencyType === -1">
      <div class="fc-input-label-txt mT8">Frequency</div>
      <el-cascader
        :options="generateFrequencyList()"
        v-model="schedule.frequency"
        style="width:100%;"
        class="fcascader mT10"
      >
      </el-cascader>
      <div v-if="schedule.frequency[0] !== 'day'" class="fc-input-label-txt">
        Choose reminder time
      </div>
      <div v-if="schedule.frequency[0] === 'week'">
        <el-select v-model="schedule.executeOnDays" multiple>
          <el-option
            v-for="day in days"
            :key="day"
            :label="day"
            :value="day"
          ></el-option>
        </el-select>
      </div>
      <div v-if="schedule.frequency[0] === 'month'">
        <el-select v-model="schedule.executeOnType">
          <el-option label="Day" value="day"></el-option>
          <el-option label="Week" value="week"></el-option>
        </el-select>
        <el-select
          v-model="schedule.executeOnDates"
          multiple
          v-if="schedule.executeOnType === 'day'"
        >
          <el-option
            v-for="date in getNumberRange(31)"
            :key="date.value"
            :label="date.label"
            :value="date.value"
          ></el-option>
        </el-select>
        <el-cascader
          :options="getWeekList()"
          v-model="schedule.executeOnWeek"
          v-if="schedule.executeOnType === 'week'"
        >
          >
        </el-cascader>
      </div>
      <div v-else-if="schedule.frequency[0] === 'year'">
        <el-select v-model="schedule.executeOnMonth" multiple>
          <el-option
            v-for="month in months"
            :key="month"
            :label="month"
            :value="month"
          ></el-option>
        </el-select>
      </div>
    </el-col>
  </el-row>
</template>
<script>
import moment from 'moment'
export default {
  props: ['from', 'value', 'initialSchedule', 'simple'],
  data() {
    return {
      frequencyTypes: [],
      scheduleInfo: {
        times: ['23:59'],
        frequencyType: 3,
        values: [13],
      },
      schedule: {
        frequencyType: 0,
        frequency: ['day', 1],
        executeOnDays: [],
        executeOnType: 'day',
        executeOnDates: [],
        executeOnWeek: [],
        executeOnMonth: [],
      },
      days: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
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
    }
  },
  computed: {
    startTime() {
      return this.from ? this.from : new Date()
    },
  },
  mounted() {
    let self = this
    this.loadScheduleInfo()
    this.setInitialValue()

    this.$watch(
      'schedule',
      function(newVal) {
        self.$emit('input', this.constructScheduleObj(this.schedule))
        self.$emit(
          'input2',
          this.frequencyTypes.find(
            rt => rt.value === this.schedule.frequencyType
          )
        )
      },
      {
        deep: true,
      }
    )
  },
  methods: {
    getDayIndex(dayName) {
      if (!dayName) {
        let date = moment(this.startTime)
        dayName = date.format('ddd')
      }
      let dayIndex = this.days.indexOf(dayName)
      return dayIndex + 1
    },
    getMonthIndex(monthName) {
      if (!monthName) {
        let date = moment(this.startTime)
        monthName = date.format('MMM')
      }
      let monthIndex = this.months.indexOf(monthName)
      return monthIndex + 1
    },
    getWeekName(weekIndex) {
      if (!weekIndex) {
        let date = moment(this.startTime)
        weekIndex = Math.ceil(date.date() / 7)
      }
      let weekName = this.weeks[weekIndex - 1]
      if (!weekName) {
        weekName = this.weeks[this.weeks.length - 1]
      }
      return weekName
    },
    getWeekIndex(weekName) {
      if (!weekName) {
        let date = moment(this.startTime)
        return Math.ceil(date.date() / 7)
      }
      let weekIndex = this.weeks.indexOf(weekName)
      return weekIndex + 1
    },
    loadScheduleInfo() {
      let date = moment(this.startTime)

      this.frequencyTypes = []

      if (!this.simple) {
        this.frequencyTypes.push({
          label: 'Do not repeat',
          value: 0,
        })
      } else {
        this.schedule.frequencyType = 1
      }

      this.frequencyTypes.push({
        label: 'Daily',
        value: 1,
      })

      let dayName = date.format('dddd')
      this.frequencyTypes.push({
        label: 'Weekly (every ' + dayName + ')',
        value: 2,
      })

      this.frequencyTypes.push({
        label: 'Monthly (on day ' + date.format('DD') + ')',
        value: 3,
      })

      let dayShortName = date.format('ddd')
      let weekOfMonthStr = this.getWeekName()
      if (!this.simple) {
        this.frequencyTypes.push({
          label: 'Monthly (every ' + weekOfMonthStr + ' ' + dayShortName + ')',
          value: 4,
        })
      }

      this.frequencyTypes.push({
        label: 'Yearly (on ' + date.format('DD MMMM') + ')',
        value: 5,
      })

      if (!this.simple) {
        this.frequencyTypes.push({
          label: 'Custom',
          value: -1,
        })
      }

      if (!this.schedule.executeOnDays.length) {
        this.schedule.executeOnDays.push(dayShortName)
      }
      if (!this.schedule.executeOnDates.length) {
        this.schedule.executeOnDates.push(parseInt(date.format('DD')))
      }
      if (!this.schedule.executeOnWeek.length) {
        this.schedule.executeOnWeek.push(weekOfMonthStr)
        this.schedule.executeOnWeek.push('Whole_Week')
      }
      if (!this.schedule.executeOnMonth.length) {
        this.schedule.executeOnMonth.push(date.format('MMM'))
      }
    },
    constructScheduleObj(scheduleObj) {
      let date = moment(this.startTime)

      let newSchedule = {
        frequencyType: 0,
        frequency: 1,
        values: [],
        times: [],
      }
      newSchedule.times.push(date.format('HH:mm'))
      if (scheduleObj.frequencyType >= 0) {
        newSchedule.frequencyType = scheduleObj.frequencyType

        if (newSchedule.frequencyType === 2) {
          let dayIndex = this.getDayIndex()
          newSchedule.values.push(dayIndex)
        } else if (newSchedule.frequencyType === 3) {
          let dateNo = date.format('DD')
          newSchedule.values.push(parseInt(dateNo))
        } else if (newSchedule.frequencyType === 4) {
          let weekOfMonth = Math.ceil(date.date() / 7)
          newSchedule.weekFrequency = weekOfMonth
          let dayIndex = this.getDayIndex()
          newSchedule.values.push(dayIndex)
        } else if (newSchedule.frequencyType === 5) {
          let monthIndex = this.getMonthIndex()
          newSchedule.values.push(monthIndex)
        }
      } else {
        // custom scheduling
        if (scheduleObj.frequency[0] === 'day') {
          newSchedule.frequencyType = 1
        } else if (scheduleObj.frequency[0] === 'week') {
          newSchedule.frequencyType = 2
          newSchedule.values = []
          for (let dayName of scheduleObj.executeOnDays) {
            newSchedule.values.push(this.getDayIndex(dayName))
          }
        } else if (scheduleObj.frequency[0] === 'month') {
          if (scheduleObj.executeOnType === 'day') {
            newSchedule.frequencyType = 3
            newSchedule.values = scheduleObj.executeOnDates
          } else {
            newSchedule.frequencyType = 4
            newSchedule.weekFrequency = this.getWeekIndex(
              scheduleObj.executeOnWeek[0]
            )
            newSchedule.values = []
            let weekValue = scheduleObj.executeOnWeek[1]
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
          }
        } else if (scheduleObj.frequency[0] === 'year') {
          newSchedule.frequencyType = 5
          newSchedule.values = []
          for (let monthName of scheduleObj.executeOnMonth) {
            newSchedule.values.push(this.getMonthIndex(monthName))
          }
        }
        newSchedule.frequency = scheduleObj.frequency[1]
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
      for (let i = 0; i < no; i++) {
        if (i === 0) {
          list.push({
            label: 'Every ' + type,
            value: 1,
          })
        } else {
          list.push({
            label: 'Every ' + (i + 1) + ' ' + type + 's',
            value: i + 1,
          })
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
          label: 'Mon',
          value: 'Mon',
        },
        {
          label: 'Tue',
          value: 'Tue',
        },
        {
          label: 'Wed',
          value: 'Wed',
        },
        {
          label: 'Thu',
          value: 'Thu',
        },
        {
          label: 'Fri',
          value: 'Fri',
        },
        {
          label: 'Sat',
          value: 'Sat',
        },
        {
          label: 'Sun',
          value: 'Sun',
        },
        {
          label: 'Mon - Fri',
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
        if (this.initialSchedule.frequency > 1) {
          let val = 'day'
          if (this.initialSchedule.frequencyType === 2) {
            val = 'week'
            this.schedule.executeOnDays = this.initialSchedule.values.map(
              day => this.days[day - 1]
            )
          } else if (this.initialSchedule.frequencyType === 3) {
            this.schedule.executeOnType = 'day'
            this.schedule.executeOnDates = this.initialSchedule.values
            val = 'month'
          } else if (this.initialSchedule.frequencyType === 4) {
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
            val = 'month'
          } else if (this.initialSchedule.frequencyType === 5) {
            this.schedule.executeOnMonth = this.initialSchedule.values.map(
              month => this.months[month - 1]
            )
            val = 'year'
          }
          this.schedule.frequency = [val, this.initialSchedule.frequency]
          this.schedule.frequencyType = -1
        } else {
          this.schedule.frequencyType = this.initialSchedule.frequencyType
        }
      } else {
        this.$emit('input', this.constructScheduleObj(this.schedule))
        this.$emit(
          'input2',
          this.frequencyTypes.find(
            rt => rt.value === this.schedule.frequencyType
          )
        )
      }
    },
  },
  watch: {
    startTime: function() {
      this.loadScheduleInfo()
      this.$emit('input', this.constructScheduleObj(this.schedule))
      this.$emit(
        'input2',
        this.frequencyTypes.find(rt => rt.value === this.schedule.frequencyType)
      )
    },
    initialSchedule: function() {
      this.setInitialValue()
    },
  },
}
</script>
<style>
.fcascader .el-cascader__label {
  padding: 0px;
}
</style>
