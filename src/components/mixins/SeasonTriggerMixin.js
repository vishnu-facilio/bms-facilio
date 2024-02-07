import { isEmpty } from '@facilio/utils/validation'

export default {
  methods: {
    async canSaveTrigger() {
      let { triggerEdit } = this
      let { schedule } = triggerEdit || {}
      let { seasons } = schedule || {}
      let valid = true
      if (seasons != null) {
        valid = true
        seasons.map(season => {
          if (isEmpty(season.name)) {
            valid = false
          } else if (isEmpty(season.startMonth) || season.startMonth <= 0) {
            valid = false
          } else if (isEmpty(season.endMonth) || season.endMonth <= 0) {
            valid = false
          } else if (isEmpty(season.startDate) || season.startDate <= 0) {
            valid = false
          } else if (isEmpty(season.endDate) || season.endDate <= 0) {
            valid = false
          }
        })
        if(!valid) {
          this.$message.error(this.$t('common.trigger.mandatory_message'))
        } else {
          valid = await this.getSchedules(triggerEdit)
          if(!valid) {
            this.$message.error("No next execution time found for the given frequency type and season")
          }
        }
      }
      return valid
    },

    async getSchedules(trigger) {
      this.tmpTrigger = this.$helpers.cloneObject(trigger)
      if (!this.tmpTrigger.schedule.skipEvery) {
        this.tmpTrigger.schedule.skipEvery = -1
      }
      this.skip = this.tmpTrigger.schedule.skipEvery

      let freq = this.tmpTrigger.schedule.frequencyType

      if (freq === 3) {
        if (this.tmpTrigger.basedOn === 'Week') {
          this.tmpTrigger.schedule.frequencyType = 4
        }
      } else if (freq === 4) {
        if (this.tmpTrigger.basedOn === 'Week') {
          this.tmpTrigger.schedule.frequencyType = 8
        } else {
          this.tmpTrigger.schedule.frequencyType = 7
        }
      } else if (freq === 5) {
        if (this.tmpTrigger.basedOn === 'Week') {
          this.tmpTrigger.schedule.frequencyType = 10
        } else {
          this.tmpTrigger.schedule.frequencyType = 9
        }
      } else if (freq === 6) {
        if (this.tmpTrigger.basedOn === 'Week') {
          this.tmpTrigger.schedule.frequencyType = 6
        } else {
          this.tmpTrigger.schedule.frequencyType = 5
        }
      }
      return await this.fetch(this.tmpTrigger.schedule, this.tmpTrigger.startTime)
    },
    async fetch(schedule, startTime) {
      if (schedule.frequencyType === 5 && schedule.yearlyDayValue === -1) {
        return true
      }
      let isValid = true
      await this.$http
        .get(
          `/workorder/calculateNextExecutionTimes?scheduleInfo=${encodeURIComponent(
            JSON.stringify(schedule)
          )}&startTime=${Math.floor(startTime / 1000)}`
        )
        .catch(error => {
          if(!isEmpty(error)) {
            isValid = false
          }
        })
      return isValid
    },
  },
}
