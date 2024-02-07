<template>
  <div>
    <div class="mR25">
      <el-row class="mT20">
        <div class="fc-input-label-txt">
          {{ $t('maintenance._workorder.frequency') }}
        </div>
        <el-col :span="24">
          <div>
            <el-select
              @change="resetTrigger"
              v-model="trigger.schedule['frequencyType']"
              class="fc-input-full-border2 width100"
            >
              <el-option
                v-for="(ftype, index) in frequencyTypes"
                :key="index"
                :label="ftype.label"
                :value="parseInt(ftype.value)"
              ></el-option>
            </el-select>
          </div>
        </el-col>
      </el-row>
      <daily-schedule
        v-if="
          this.$constants.FACILIO_FREQUENCY[
            trigger.schedule['frequencyType']
          ] === 'Daily'
        "
        :hideFields="hideFields"
        :triggerEdit="trigger"
      ></daily-schedule>
      <weekly-schedule
        v-if="
          this.$constants.FACILIO_FREQUENCY[
            trigger.schedule['frequencyType']
          ] === 'Weekly'
        "
        :hideFields="hideFields"
        :triggerEdit="trigger"
      ></weekly-schedule>
      <monthly-schedule
        v-if="
          this.$constants.FACILIO_FREQUENCY[
            trigger.schedule['frequencyType']
          ] === 'Monthly'
        "
        :hideFields="hideFields"
        :triggerEdit="trigger"
      ></monthly-schedule>
      <quarterly-schedule
        v-if="
          this.$constants.FACILIO_FREQUENCY[
            trigger.schedule['frequencyType']
          ] === 'Quarterly'
        "
        :hideFields="hideFields"
        :triggerEdit="trigger"
      ></quarterly-schedule>
      <half-yearly-schedule
        v-if="
          this.$constants.FACILIO_FREQUENCY[
            trigger.schedule['frequencyType']
          ] === 'Half Yearly'
        "
        :hideFields="hideFields"
        :triggerEdit="trigger"
      ></half-yearly-schedule>
      <yearly-schedule
        v-if="
          this.$constants.FACILIO_FREQUENCY[
            trigger.schedule['frequencyType']
          ] === 'Annually'
        "
        :hideFields="hideFields"
        :triggerEdit="trigger"
      ></yearly-schedule>
      <do-not-repeat-schedule
        v-if="trigger.schedule['frequencyType'] === 0"
        :triggerEdit="trigger"
      ></do-not-repeat-schedule>
    </div>
    <TriggerSeason
      :triggerEdit="trigger"
      v-if="
        !$validation.isEmpty(fromPage) &&
          fromPage === 'pmtrigger' &&
          this.$constants.FACILIO_FREQUENCY[
            trigger.schedule['frequencyType']
          ] !== 'Half Yearly' &&
          this.$constants.FACILIO_FREQUENCY[
            trigger.schedule['frequencyType']
          ] !== 'Annually'
      "
    >
    </TriggerSeason>
  </div>
</template>
<script>
import DailySchedule from '@/TriggerDailySchedule'
import WeeklySchedule from '@/TriggerWeeklySchedule'
import MonthlySchedule from '@/TriggerMonthlySchedule'
import QuarterlySchedule from '@/TriggerQuarterlySchedule'
import HalfYearlySchedule from '@/TriggerHalfYearlySchedule'
import YearlySchedule from '@/TriggerYearlySchedule'
import DoNotRepeatSchedule from '@/TriggerDoNotRepeat'
import PMMixin from '@/mixins/PMMixin'
import { deepCloneObject } from 'util/utility-methods'
import TriggerSeason from 'src/components/TriggerSeason.vue'

export default {
  props: [
    'trigger',
    'hideFields',
    'availableFrequency',
    'canShowCustomFrequency',
    'fromPage',
  ],
  components: {
    DailySchedule,
    WeeklySchedule,
    MonthlySchedule,
    QuarterlySchedule,
    HalfYearlySchedule,
    YearlySchedule,
    DoNotRepeatSchedule,
    TriggerSeason,
  },
  mounted() {
    // Will enable shortly hence commenting it now.
    // let { availableFrequency, canShowCustomFrequency } = this
    // if (canShowCustomFrequency)
    //   this.$set(this, 'frequencyTypes', availableFrequency)
    // else {
    for (let key in this.$constants.FACILIO_FREQUENCY) {
      if (this.$constants.FACILIO_FREQUENCY.hasOwnProperty(key)) {
        this.frequencyTypes.push({
          label: this.$constants.FACILIO_FREQUENCY[key],
          value: key,
        })
      }
    }
    //}
  },
  data() {
    return {
      frequencyTypes: [],
    }
  },
  watch: {
    trigger: function() {},
  },
  methods: {
    resetTrigger() {
      let schedule = deepCloneObject(PMMixin.INITIAL_SCHEDULE)
      schedule.frequencyType = this.trigger.schedule['frequencyType']

      if (schedule.frequencyType === 4 || schedule.frequencyType === 5) {
        schedule.monthValue = 1
      }

      if (schedule.frequencyType === 1) {
        schedule.values = []
      }

      if (
        schedule.frequencyType === 5 ||
        schedule.frequencyType === 6 ||
        schedule.frequencyType === 4
      ) {
        this.trigger.basedOn = 'Date'
      }

      if (schedule.frequencyType === 6) {
        schedule.yearlyDayValue = 1
      }

      this.trigger.schedule = schedule
    },
  },
}
</script>
<style lang="scss">
.pm-tigger-dialog-form {
  .pm-tigger-dialog-form-left {
    width: 70%;
    float: left;
  }
  .pm-tigger-dialog-form-right {
    width: 25%;
    float: right;
    overflow-y: scroll;
    max-height: 700px;
    padding-bottom: 50px;
  }
  .pm-tigger-dialog-form-separator-con {
    width: 5%;
    height: 100%;
    .pm-tigger-dialog-form-separator {
      margin-left: 20px;
      margin-right: 20px;
      background: #ebedf4;
      width: 1px;
      height: 100%;
    }
  }
}
</style>
