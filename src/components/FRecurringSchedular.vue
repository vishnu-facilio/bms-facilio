<template>
  <div class="reccuring-schedular-container">
    <div class="recurring-type">
      <div class="label">Recurrence Type</div>
      <el-select
        v-model="scheduleInfoObj.frequencyType"
        class="fc-input-full-border-select2 width100"
      >
        <el-option
          v-for="(type, index) in frequencyTypes"
          :key="index"
          :label="type.label"
          :value="type.value"
        ></el-option>
      </el-select>
    </div>
    <div v-if="canShowRecurringBasedOn" class="recurring-monthly-based-on mT15">
      <div class="label">Based On</div>
      <el-radio-group v-model="scheduleInfoObj.monthlyBasedOn">
        <el-radio class="fc-radio-btn" :label="`basedOnDate`">Date</el-radio>
        <el-radio class="fc-radio-btn" :label="`basedOnWeek`">Week</el-radio>
      </el-radio-group>
    </div>
    <div class="d-flex flex-wrap">
      <div class="recurring-every mT15">
        <div class="label">Every</div>
        <el-select
          v-model="scheduleInfoObj.frequency"
          class="fc-input-full-border-select2 width100"
        >
          <el-option
            v-for="(type, index) in everyTypes"
            :key="index"
            :label="type.label"
            :value="type.value"
          ></el-option>
        </el-select>
      </div>
      <div
        v-if="canShowRecurringOn"
        class="recurring-on mT15"
        :class="
          canShowWeekFrequency || canShowYearly ? 'form-one-column' : 'pL10'
        "
      >
        <div class="label">On</div>
        <div
          class="d-flex"
          :class="canShowYearlyWeekFrequency ? 'flex-wrap' : ''"
        >
          <el-select
            v-if="canShowWeekFrequency"
            v-model="scheduleInfoObj.weekFrequency"
            class="weeks-frequency fc-input-full-border-select2 width100"
          >
            <el-option
              v-for="(week, index) in weeksFrequencyHash"
              :key="index"
              :label="week.label"
              :value="week.value"
            ></el-option>
          </el-select>
          <el-select
            v-model="scheduleInfoObj.values"
            class="fc-input-full-border-select2 width100 fc-tag"
            multiple
            collapse-tags
          >
            <el-option
              v-for="(day, index) in recurringOnTypeOptions"
              :key="index"
              :label="day.label"
              :value="day.value"
            ></el-option>
          </el-select>
          <div
            v-if="canShowYearlyWeekFrequency"
            class="d-flex mT10 form-one-column"
          >
            <el-select
              v-if="canShowYearlyWeekFrequency"
              v-model="scheduleInfoObj.weekFrequency"
              class="weeks-frequency fc-input-full-border-select2 width100"
            >
              <el-option
                v-for="(week, index) in weeksFrequencyHash"
                :key="index"
                :label="week.label"
                :value="week.value"
              ></el-option>
            </el-select>
            <el-select
              v-model="scheduleInfoObj.yearlyDayOfWeekValues"
              class="fc-input-full-border-select2 width100 fc-tag"
              multiple
              collapse-tags
            >
              <el-option
                v-for="(day, index) in weekDaysArr"
                :key="index"
                :label="day.label"
                :value="day.value"
              ></el-option>
            </el-select>
          </div>
          <div v-else-if="canShowYearly" class="yearly-date">
            <el-select
              v-model="scheduleInfoObj.yearlyDayValue"
              class="fc-input-full-border-select2 width100"
            >
              <el-option
                v-for="(day, index) in monthDatesArr"
                :key="index"
                :label="day.label"
                :value="day.value"
              ></el-option>
            </el-select>
          </div>
        </div>
      </div>
      <div class="d-flex form-one-column mT15">
        <div class="recurring-end">
          <div class="label">End</div>
          <f-date-picker
            v-model="scheduleInfoObj.endTime"
            :hideClear="true"
            :type="'date'"
            :pickerOptions="pickerOptions"
            class="fc-input-full-border2 form-date-picker start-date"
          ></f-date-picker>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { constructFieldOptions } from '@facilio/utils/utility-methods'
import Constants from 'util/constant'
import FDatePicker from 'pages/assets/overview/FDatePicker'

const frequencyTypesHash = {
  1: 'Daily',
  2: 'Weekly',
  3: 'Monthly',
  5: 'Annually',
}
const weeksHash = {
  1: 'First',
  2: 'Second',
  3: 'Third',
  4: 'Fourth',
  5: 'Last',
}
const frequencyHash = ['Day', 'Week', 'Month', 'Quater', 'Year']
const recurringOnHash = ['Weekly', 'Monthly', 'Annually']
const recurringBasedOnHash = ['Monthly', 'Annually']

export default {
  props: ['scheduleInfoObj', 'pickerOptions'],
  components: {
    FDatePicker,
  },
  computed: {
    everyTypes() {
      let { scheduleInfoObj } = this
      let { frequencyType } = scheduleInfoObj
      let everyTypesArr = []
      new Array(60).fill().forEach((_, index) => {
        let label = `${frequencyHash[frequencyType - 1]}`
        label = `${label}`
        if (index !== 0) {
          label = `${index + 1} ${label}s`
        }
        everyTypesArr.push({
          label,
          value: index + 1,
        })
      })
      return everyTypesArr
    },
    frequencyTypes() {
      return constructFieldOptions(frequencyTypesHash)
    },
    weekDaysArr() {
      return constructFieldOptions(Constants.WEEK_DAYS)
    },
    weeksFrequencyHash() {
      return constructFieldOptions(weeksHash)
    },
    monthDatesArr() {
      let date = this.$helpers.getOrgMoment('2012-01-01')
      let monthDatesArr = []
      new Array(31).fill().forEach((_, index) => {
        monthDatesArr.push({
          label: date.format('DDDo'),
          value: index + 1,
        })
        date.add(1, 'd')
      })
      return monthDatesArr
    },
    monthsArr() {
      return Constants.MONTHS
    },
    selectedFrequencyObj() {
      let { scheduleInfoObj, frequencyTypes } = this
      let { frequencyType } = scheduleInfoObj
      let frequencyObj =
        frequencyTypes.find(frequency => frequency.value === frequencyType) ||
        {}
      return frequencyObj
    },
    canShowRecurringBasedOn() {
      let { selectedFrequencyObj } = this
      let { label } = selectedFrequencyObj
      return recurringBasedOnHash.includes(label)
    },
    canShowRecurringOn() {
      let { selectedFrequencyObj } = this
      let { label } = selectedFrequencyObj
      return recurringOnHash.includes(label)
    },
    canShowWeekFrequency() {
      let { selectedFrequencyObj, scheduleInfoObj } = this
      let { label } = selectedFrequencyObj
      let { monthlyBasedOn } = scheduleInfoObj
      if (label === 'Monthly') {
        return monthlyBasedOn !== 'basedOnDate'
      }
      return false
    },
    canShowYearlyWeekFrequency() {
      let { selectedFrequencyObj, scheduleInfoObj } = this
      let { label } = selectedFrequencyObj
      let { monthlyBasedOn } = scheduleInfoObj
      if (label === 'Annually') {
        return monthlyBasedOn !== 'basedOnDate'
      }
      return false
    },
    canShowYearly() {
      let { selectedFrequencyObj } = this
      let { label } = selectedFrequencyObj
      return label === 'Annually'
    },
    recurringOnTypeOptions() {
      let {
        selectedFrequencyObj,
        monthDatesArr,
        weekDaysArr,
        monthsArr,
        scheduleInfoObj,
      } = this
      let { value, label } = selectedFrequencyObj
      let { monthlyBasedOn } = scheduleInfoObj
      if (label === 'Monthly') {
        return monthlyBasedOn !== 'basedOnDate' ? weekDaysArr : monthDatesArr
      }
      if (value === 2) {
        return weekDaysArr
      }
      return value === 6 ? monthsArr : monthDatesArr
    },
  },
  watch: {
    'scheduleInfoObj.frequencyType': {
      handler() {
        this.resetToDefaultValues()
      },
    },
  },
  methods: {
    resetToDefaultValues() {
      this.$set(this.scheduleInfoObj, 'values', [1])
      this.$set(this.scheduleInfoObj, 'weekFrequency', 1)
      this.$set(this.scheduleInfoObj, 'yearlyDayValue', 1)
      this.$set(this.scheduleInfoObj, 'yearlyDayOfWeekValues', [1])
    },
  },
}
</script>
<style lang="scss">
.reccuring-schedular-container {
  .recurring-type,
  .recurring-every,
  .recurring-monthly-based-on,
  .recurring-on,
  .recurring-start,
  .recurring-end {
    flex: 1 1 50%;
    .label {
      font-size: 14px;
      letter-spacing: 0.32px;
      color: #324056;
    }
  }
  .recurring-on {
    .weeks-frequency {
      flex: 1 1 25%;
      padding-right: 10px;
    }
    &.form-one-column {
      flex: 1 1 100%;
    }
    .yearly-date {
      flex: 0 0 50%;
      padding-left: 10px;
    }
  }
}
</style>
