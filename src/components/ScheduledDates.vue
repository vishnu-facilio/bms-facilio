<template>
  <div>
    <template>
      <div
        v-if="errorMessage"
        class="fc-align-center-column height100 width250px"
      >
        <div>
          <inline-svg
            src="svgs/emptystate/data-empty"
            class="vertical-middle'"
            iconClass="icon text-center icon-xxxxlg"
          ></inline-svg>
        </div>
        <div class="fc-black3-13 self-center bold">
          {{ errorMessage }}
        </div>
      </div>
      <div
        v-else
        v-for="(i, key) in dates"
        :key="key"
        class="fc-black-13 text-left pT15 pB15 border-bottom3 bold"
      >
        <span>{{ i }}</span>
      </div>
    </template>
  </div>
</template>
<script>
import { eventBus } from './page/widget/utils/eventBus'

export default {
  props: ['trigger'],
  data() {
    return {
      dates: [],
      tmpTrigger: {},
      skip: -1,
      errorMessage: null,
      loading: false,
    }
  },
  mounted() {
    this.reinit(this.trigger)
  },
  created() {
    this.eventEmit()
  },
  destroyed() {
    eventBus.$off('changedSeasonsTrigger')
  },
  watch: {
    trigger: {
      deep: true,
      handler: function(newVal) {
        this.reinit(this.trigger)
      },
    },
  },
  methods: {
    eventEmit() {
      eventBus.$on('changedSeasonsTrigger', seasons => {
        let tempTrigger = this.$helpers.cloneObject(this.trigger)
        tempTrigger.schedule.seasons = seasons
        this.reinit(tempTrigger)
      })
    },
    reinit(trigger) {
      this.tmpTrigger = this.$helpers.cloneObject(trigger)
      if (!this.tmpTrigger.schedule.skipEvery) {
        this.tmpTrigger.schedule.skipEvery = -1
      }

      this.skip = this.tmpTrigger.schedule.skipEvery

      //this.tmpTrigger.schedule.skipEvery = -1

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

      this.fetch(this.tmpTrigger.schedule, this.tmpTrigger.startTime)
    },
    fetch(schedule, startTime) {
      if (schedule.frequencyType === 5 && schedule.yearlyDayValue === -1) {
        return
      }
      this.loading = true
      this.$http
        .get(
          `/workorder/calculateNextExecutionTimes?scheduleInfo=${encodeURIComponent(
            JSON.stringify(schedule)
          )}&startTime=${Math.floor(startTime / 1000)}`
        )
        .then(response => {
          if (response && response.data.nextExecutionTimes) {
            this.dates = []
            for (let i = 0; i < response.data.nextExecutionTimes.length; i++) {
              let day = this.$helpers.getOrgMoment(
                response.data.nextExecutionTimes[i] * 1000
              )
              this.dates.push(day.format('ddd, DD MMM YYYY LT'))
            }
            this.$emit('setTotalSchedules', this.dates.length)
          }
          this.loading = false
          this.errorMessage = null
        })
        .catch(error => {
          this.loading = false
          let { data } = error || {}
          let { message } = data || {}
          this.errorMessage = message
        })
    },
  },
}
</script>
<style>
.strike {
  text-decoration: line-through;
  color: #d0d9e2;
}
</style>
