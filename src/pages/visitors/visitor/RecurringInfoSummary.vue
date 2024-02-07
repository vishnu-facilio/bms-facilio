<template>
  <div>
    <div class="fc__white__bg__info">
      <el-row class="border-bottom6 pB20">
        <el-col :span="12">
          <el-col :span="12">
            <div class="field-label">Start Date</div>
          </el-col>
          <el-col :span="12">{{ startDate ? startDate : '---' }}</el-col>
        </el-col>
        <el-col :span="12">
          <el-col :span="12">
            <div class="field-label">End Date</div>
          </el-col>
          <el-col :span="12">{{ endDate ? endDate : '---' }}</el-col>
        </el-col>
      </el-row>
      <el-row class="pT20 border-bottom6 pB20">
        <el-col :span="12">
          <el-col :span="12">
            <div class="field-label">Start Time</div>
          </el-col>
          <el-col :span="12">{{ startTime ? startTime : '---' }}</el-col>
        </el-col>
        <el-col :span="12">
          <el-col :span="12">
            <div class="field-label">End Time</div>
          </el-col>
          <el-col :span="12">{{ endTime ? endTime : '---' }}</el-col>
        </el-col>
      </el-row>
      <el-row class="pT20 border-bottom6 pB20">
        <el-col :span="24">
          <el-col :span="6">
            <div class="field-label">Allowed Days</div>
          </el-col>
          <el-col :span="18">
            <span v-for="(week, wIndex) in weeks" :key="wIndex" class="mR20">
              <el-checkbox disabled v-model="week.value"></el-checkbox
              ><span class="mL10 label-txt-black">{{ week.displayName }}</span>
            </span>
          </el-col>
        </el-col>
      </el-row>
    </div>
  </div>
</template>
<script>
import { isArray, isObject } from '@facilio/utils/validation'
export default {
  props: ['details'],
  data() {
    return {
      startTime: null,
      endTime: null,
      startDate: null,
      endDate: null,
      recurringKey: 'recurringInfo',
      weeks: [
        { dayOfWeek: 1, displayName: 'Monday', value: false },
        { dayOfWeek: 2, displayName: 'Tuesday', value: false },
        { dayOfWeek: 3, displayName: 'Wednesday', value: false },
        { dayOfWeek: 4, displayName: 'Thrusday', value: false },
        { dayOfWeek: 5, displayName: 'Friday', value: false },
        { dayOfWeek: 6, displayName: 'Saturday', value: false },
        { dayOfWeek: 7, displayName: 'Sunday', value: false },
      ],
    }
  },
  computed: {
    weeksToDisplay() {
      return this.weeks.filter(week => week.value)
    },
  },
  created() {
    this.formatData(this.details)
  },
  methods: {
    formatData(data) {
      if (data.isRecurring) {
        let recurringVisitTime = data[this.recurringKey]
        if (isObject(recurringVisitTime)) {
          if (isArray(recurringVisitTime.singleDaybusinessHoursList)) {
            recurringVisitTime.singleDaybusinessHoursList.forEach(day => {
              this.startTime = day.startTime
              this.endTime = day.endTime
              let week = this.weeks.find(i => i.dayOfWeek === day.dayOfWeek)
              if (week) {
                week.value = true
              }
            })
          }
          this.startDate =
            data.expectedStartTime && data.expectedStartTime > 0
              ? this.$options.filters.formatDate(data.expectedStartTime, true)
              : null
          this.endDate =
            data.expectedStartTime && data.expectedEndTime > 0
              ? this.$options.filters.formatDate(data.expectedEndTime, true)
              : null
        }
      }
    },
  },
}
</script>
<style scoped>
.field-label {
  color: #324056;
  font-weight: 500;
  font-size: 13px;
  font-weight: 500;
  letter-spacing: 0.5px;
}
.field-value {
  padding-left: 10px;
  font-size: 13px;
  font-weight: normal;
  letter-spacing: 0.5px;
  color: #324056;
}
</style>
