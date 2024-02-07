<template>
  <div
    v-if="workorder"
    class="wo-timer fc__wo__sum__timer"
    style="min-height: 45px;"
  >
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
      <timer class="alarm-timer p10 wip" :time="time"></timer>
    </template>
    <template
      v-else-if="
        !(workorder.moduleState && workorder.moduleState.id) &&
          workorder.status.status === 'Work in Progress'
      "
    >
      <timer vclass="alarm-timer p10 wip" :time="time"></timer>
    </template>
    <template v-else-if="time">
      <static-timer
        v-if="actualWorkDurationField"
        class="alarm-timer p10 static"
        :time="time"
        :static="true"
        twoDigits="true"
        :duration="duration"
      ></static-timer>
    </template>
    <template v-else>
      <div class="fc-timer p10 nothing static">
        <div class="row">
          <div class="t-hours">
            <div class="t-label">{{ '00' }}</div>
            <div class="t-sublabel uppercase">
              {{ $t('maintenance._workorder.hour') }}
            </div>
          </div>
          <span class="t-separate">:</span>
          <div class="t-mins">
            <div class="t-label">{{ '00' }}</div>
            <div class="t-sublabel uppercase">
              {{ $t('maintenance._workorder.min') }}
            </div>
          </div>
          <span class="t-separate pL13">:</span>
          <div class="t-secs">
            <div class="t-label">{{ '00' }}</div>
            <div class="t-sublabel uppercase">
              {{ $t('maintenance._workorder.sec') }}
            </div>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>
<script>
import Timer from '@/Timer'
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
.wo-timer .fc-timer div.t-label {
  font-size: 20px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: 30px;
  letter-spacing: 0.2px;
  color: #ff3184;
}
.wo-timer .fc-timer div.t-sublabel {
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
.fc__wo__sum__timer .wo-timer .fc-timer div.t-label {
  text-align: left;
}
.fc__wo__sum__timer .alarm-timer {
  padding-left: 10px;
}
.fc__wo__sum__timer .fc-timer div.t-mins,
.fc-timer div.t-secs,
.fc-timer div.t-hours,
.fc-timer div.t-days {
  flex: inherit;
}
.fc__wo__sum__timer .fc-timer span.t-separate {
  padding-left: 8px;
  padding-right: 14px;
}
</style>
