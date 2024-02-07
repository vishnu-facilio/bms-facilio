<template>
  <FContainer v-if="workorder">
    <template v-if="!workorder.loadTimer || loading">
      <spinner :show="true" size="40"></spinner>
    </template>
    <template
      v-else-if="
        workorder.moduleState &&
          $store.getters.getTicketStatus(workorder.moduleState.id, 'workorder')
            .timerEnabled
      "
    >
      <timer :time="time" twoDigits="true"></timer>
    </template>
    <template
      v-else-if="
        !(workorder.moduleState && workorder.moduleState.id) &&
          workorder.status.status === 'Work in Progress'
      "
    >
      <timer :time="time"></timer>
    </template>
    <template v-else-if="time">
      <static-timer
        v-if="actualWorkDurationField"
        :time="time"
        :static="true"
        twoDigits="true"
        :duration="duration"
      ></static-timer>
    </template>
    <template v-else>
      <FContainer class="fc-timer p10 nothing static">
        <FContainer class="row">
          <FContainer class="t-hours">
            <FContainer class="t-label">{{ '00' }}</FContainer>
            <FContainer class="t-sublabel uppercase">
              {{ $t('maintenance._workorder.hour') }}
            </FContainer>
          </FContainer>
          <span class="t-separate">:</span>
          <FContainer class="t-mins">
            <FContainer class="t-label">{{ '00' }}</FContainer>
            <FContainer class="t-sublabel uppercase">
              {{ $t('maintenance._workorder.min') }}
            </FContainer>
          </FContainer>
          <span class="t-separate pL13">:</span>
          <FContainer class="t-secs">
            <FContainer class="t-label">{{ '00' }}</FContainer>
            <FContainer class="t-sublabel uppercase">
              {{ $t('maintenance._workorder.sec') }}
            </FContainer>
          </FContainer>
        </FContainer>
      </FContainer>
    </template>
  </FContainer>
</template>
<script>
// import Timer from '@/Timer'
import Timer from 'src/beta/summary/widgets/WorkorderWidget/NewTimer.vue'

import moment from 'moment-timezone'
export default {
  props: ['actualWorkDurationField', 'loading'],
  components: {
    Timer: Timer,
    StaticTimer: Timer,
  },
  watch: {
    workorder: {
      handler() {},
      deep: true,
    },
  },
  created() {
    this.$store.dispatch('loadTicketStatus', 'workorder')
  },
  computed: {
    time() {
      if (this.workorder.loadTimer) {
        let time =
          this.workorder.actualWorkStart > this.workorder.resumedWorkStart
            ? this.workorder.actualWorkStart
            : this.workorder.resumedWorkStart
        let duration = this.workorder.actualWorkDuration
        time =
          time > -1
            ? time
            : moment()
                .tz(this.$timezone)
                .valueOf()
        duration = (duration > -1 ? duration : 0) * 1000
        return time - duration
      } else {
        return moment()
          .tz(this.$timezone)
          .valueOf()
      }
    },
    duration() {
      let { actualWorkDurationField } = this
      if (this.workorder.loadTimer) {
        let duration = this.workorder.actualWorkDuration
        duration = duration > -1 ? duration : 0
        return this.$helpers.getDurationInSecs(
          duration,
          actualWorkDurationField?.unit || 's'
        )
      } else {
        return 0
      }
    },
    workorder() {
      return this.$store.state.workorder.currentWorkOrder
    },
  },
}
</script>
<style>
.wo-timer .fc-timer FContainer.t-label {
  font-size: 20px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: 30px;
  letter-spacing: 0.2px;
  color: #ff3184;
}
.wo-timer .fc-timer FContainer.t-sublabel {
  text-align: center;
  font-size: 11px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.4px;
  color: #324056;
  text-transform: uppercase !important;
}
.wo-timer .fc-timer {
  padding-left: 0;
}
.fc-timer.static {
  padding-left: 10px;
}
.fc__wo__sum__timer .wo-timer .fc-timer FContainer.t-label {
  text-align: left;
}
.fc__wo__sum__timer .alarm-timer {
  padding-left: 10px;
}
.fc__wo__sum__timer .fc-timer FContainer.t-mins,
.fc-timer FContainer.t-secs,
.fc-timer FContainer.t-hours,
.fc-timer FContainer.t-days {
  flex: inherit;
}
.fc__wo__sum__timer .fc-timer span.t-separate {
  padding-left: 8px;
  padding-right: 14px;
}
</style>
