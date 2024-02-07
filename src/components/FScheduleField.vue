<template>
  <div
    v-if="!$validation.isEmpty(scheduleInfo)"
    class="schedule-field-container"
  >
    <div class="d-flex">
      <f-date-picker
        v-model="startTime"
        :hideClear="true"
        :type="'date'"
        :pickerOptions="pickerOptions"
        class="fc-input-full-border2 form-date-picker start-date"
      ></f-date-picker>
      <el-select
        placeholder="--/--"
        v-model="startTime"
        class="fc-input-full-border-select2 width100 start-time"
      >
        <el-option
          v-for="(time, index) in startTimeArr"
          :key="index"
          :label="time.label"
          :value="time.value"
        ></el-option>
      </el-select>
      <el-select
        placeholder="--/--"
        v-model="endTime"
        class="fc-input-full-border-select2 width100 end-time"
      >
        <el-option-group
          v-for="(group, index) in endTimeArr"
          :key="index"
          :label="group.label"
        >
          <el-option
            v-for="(time, index) in group.options"
            :key="index"
            :label="time.label"
            :value="time.value"
          ></el-option>
        </el-option-group>
      </el-select>
    </div>
    <div>
      <el-checkbox v-model="model.isRecurring">Recurring Visit</el-checkbox>
    </div>
    <div v-if="model.isRecurring" class="mT15">
      <FRecurringSchedular
        :scheduleInfoObj="scheduleInfo"
        :pickerOptions="pickerOptions"
      ></FRecurringSchedular>
    </div>
  </div>
</template>
<script>
import FDatePicker from 'pages/assets/overview/FDatePicker'
import FRecurringSchedular from '@/FRecurringSchedular'
import isEqual from 'lodash/isEqual'
import { deepCloneObject } from 'util/utility-methods'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: {
    model: {
      type: Object,
      required: true,
    },
    config: {
      type: Object,
      required: true,
    },
  },
  components: {
    FDatePicker,
    FRecurringSchedular,
  },
  data() {
    return {
      timeInterval: 15,
      scheduleInfo: null,
      initScheduleInfo: {
        endTime: this.$helpers
          .getOrgMoment()
          .startOf('date')
          .add(1, 'd')
          .valueOf(),
        frequencyType: 1,
        frequency: 1,
        values: [1],
        weekFrequency: 1,
        yearlyDayValue: 1,
        yearlyDayOfWeekValues: [1],
        monthlyBasedOn: 'basedOnDate',
      },
    }
  },
  computed: {
    startTime: {
      get() {
        let { config, model } = this
        let { startFieldName } = config
        return model[startFieldName]
      },
      set(value) {
        let { config } = this
        let { startFieldName } = config
        this.$set(this.model, startFieldName, value)
      },
    },
    endTime: {
      get() {
        let { config, model } = this
        let { endFieldName } = config
        return model[endFieldName]
      },
      set(value) {
        let { config } = this
        let { endFieldName } = config
        this.$set(this.model, endFieldName, value)
      },
    },
    pickerOptions() {
      let todayDate = this.$helpers
        .getOrgMoment()
        .startOf('date')
        .valueOf()
      return {
        disabledDate(time) {
          return time.getTime() < todayDate
        },
      }
    },
    startTimeArr() {
      return this.constructTimeArr({
        startHour: 0,
        endHour: 24,
        startMinute: 0,
        endMinute: 60,
        isNextDay: false,
      })
    },
    endTimeArr() {
      let { startTime } = this
      let timesArr = [
        {
          label: '',
          options: [],
        },
        {
          label: '',
          options: [],
        },
      ]
      let startHour = this.$helpers.getOrgMoment(startTime).hours()
      let startMinute = this.$helpers.getOrgMoment(startTime).minutes()
      let endHour = startHour
      let endMinute = startMinute
      timesArr[0].options = this.constructTimeArr({
        startHour,
        endHour: 24,
        startMinute,
        endMinute: 60,
        isNextDay: false,
      })
      timesArr[1].options = this.constructTimeArr({
        startHour: 0,
        endHour,
        startMinute: 0,
        endMinute,
        isNextDay: true,
      })
      return timesArr
    },
  },
  watch: {
    startTime: {
      handler(newVal, oldVal) {
        if (!isEqual(newVal, oldVal)) {
          let endTime = this.$helpers
            .getOrgMoment(newVal)
            .add(2, 'hours')
            .valueOf()
          this.$set(this, 'endTime', endTime)
        }
      },
    },
    scheduleInfo: {
      handler(newVal) {
        let { config } = this
        let { scheduleJsonName } = config
        let value = deepCloneObject(newVal)
        let serializedValue = this.serializeScheduleInfo(value)
        this.$set(this.model, scheduleJsonName, serializedValue)
      },
      deep: true,
    },
  },
  created() {
    let { model, config } = this
    let { scheduleJsonName } = config
    let scheduleInfoObj = model[scheduleJsonName]
    if (!isEmpty(scheduleInfoObj)) {
      scheduleInfoObj = this.deserializeScheduleInfo(model[scheduleJsonName])
      this.$set(this, 'scheduleInfo', scheduleInfoObj)
    } else {
      this.init()
    }
  },
  methods: {
    init() {
      let { timeInterval, initScheduleInfo } = this
      let currentHour = this.$helpers.getOrgMoment().hours()
      let currentMinute = this.$helpers.getOrgMoment().minutes()
      let nearestMinute = Math.ceil(currentMinute / timeInterval) * timeInterval
      let startTime = this.$helpers
        .getOrgMoment({ hour: currentHour })
        .add(nearestMinute, 'minutes')
        .valueOf()
      let endTime = this.$helpers
        .getOrgMoment(startTime)
        .add(2, 'hours')
        .valueOf()
      this.$set(this, 'startTime', startTime)
      this.$set(this, 'endTime', endTime)
      this.$set(this, 'scheduleInfo', deepCloneObject(initScheduleInfo))
    },
    constructTimeArr(timeObj = {}) {
      let { startHour, endHour, startMinute, endMinute, isNextDay } = timeObj
      let { timeInterval, startTime } = this
      let timesArr = []
      let timeArrLength = endHour - startHour
      if (isNextDay) {
        timeArrLength += 1
      }
      let lastIndex = timeArrLength - 1
      new Array(timeArrLength).fill().forEach((_, index) => {
        let currentHour = startHour + index
        let currentMinute = startMinute
        let lastMin = lastIndex === index ? endMinute : 60
        while (currentMinute < lastMin) {
          let label = this.$helpers
            .getOrgMoment({ hour: currentHour, minute: currentMinute })
            .format('h:mm A')
          let value = this.$helpers
            .getOrgMoment({
              hour: currentHour,
              minute: currentMinute,
            })
            .valueOf()
          let diffInDays = this.findDiffInDays(startTime, value)
          if (diffInDays > 0) {
            value = this.$helpers
              .getOrgMoment(value)
              .add(diffInDays, 'days')
              .valueOf()
          }
          if (isNextDay) {
            label = `${label} +1`
            value = this.$helpers
              .getOrgMoment(value)
              .add(1, 'days')
              .valueOf()
          }
          timesArr.push({
            label,
            value,
          })
          currentMinute += timeInterval
        }
        currentMinute = 0
      })
      return timesArr
    },
    findDiffInDays(startTime, currentTime) {
      let startTimeMoment = this.$helpers.getOrgMoment(startTime)
      let currentTimeMoment = this.$helpers.getOrgMoment(currentTime)
      return startTimeMoment.diff(currentTimeMoment, 'days')
    },
    deserializeScheduleInfo(scheduleInfo) {
      let { schedule, endTime } = scheduleInfo
      let { frequencyType } = schedule
      let deserializedScheduleObj = {}
      if ([3, 4].includes(frequencyType)) {
        this.$set(
          schedule,
          'monthlyBasedOn',
          frequencyType === 3 ? 'basedOnDate' : 'basedOnWeek'
        )
        schedule.frequencyType = 3
      } else if ([5, 6].includes(frequencyType)) {
        this.$set(
          schedule,
          'monthlyBasedOn',
          frequencyType === 5 ? 'basedOnDate' : 'basedOnWeek'
        )
        schedule.frequencyType = 5
      }
      schedule.endTime = endTime
      deserializedScheduleObj = {
        ...schedule,
      }
      return deserializedScheduleObj
    },
    /*
      when `basedOnWeek` is selected for monthly frequency, then frequency type should be 4
      when `basedOnDate` is selected for monthly frequency, then frequency type should be 3
      when `basedOnWeek` is selected for yearly frequency, then frequency type should be 6
      when `basedOnDate` is selected for yearly frequency, then frequency type should be 5
    */
    serializeScheduleInfo(scheduleInfo) {
      let { startTime } = this
      let { monthlyBasedOn, frequencyType, endTime } = scheduleInfo
      let finalScheduleObj = {}
      if ([3, 5].includes(frequencyType) && monthlyBasedOn === 'basedOnWeek') {
        scheduleInfo.frequencyType = frequencyType === 3 ? 4 : 6
      }
      delete scheduleInfo.monthlyBasedOn
      delete scheduleInfo.endTime
      finalScheduleObj = {
        startTime,
        endTime,
        schedule: scheduleInfo,
      }
      return finalScheduleObj
    },
  },
}
</script>
<style lang="scss">
.schedule-field-container {
  border: solid 1px #eff3f7;
  background-color: #fbfcfd;
  padding: 40px 25px;
  .start-date {
    flex: 1 0 44%;
  }
  .start-time,
  .end-time {
    flex: 1 0 23%;
    padding-left: 10px;
  }
}
</style>
