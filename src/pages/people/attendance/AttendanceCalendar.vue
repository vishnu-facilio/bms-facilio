<template>
  <div class="attendance-calendar">
    <facilio-calendar
      v-bind:showTopbar="false"
      :views="['MONTH']"
      v-bind:isDragDropAllowed="false"
      @viewChanged="handleViewChange"
      ref="calendar"
      :startRange="startTs"
      v-bind:allowUserSettings="false"
      :defaultSettings="getCalendarSettings()"
    >
      <template v-slot:taskContent="eventProps">
        <div
          class="fc-att-legend-icon mb5"
          :class="[
            {
              ['attendance-status-' + eventProps.event['status'] + '-bg']: true,
            },
          ]"
        >
          {{ eventProps.event['status'] }}
        </div>
        <div class="working-hrs" v-if="eventProps.event['workingHours'] != -1">
          {{ $helpers.get12HrTime(eventProps.event['workingHours'], 'HH:mm') }}
          hr
        </div>
        <!-- to do write a proper layout to center this -->
        <div class="working-hrs-dummy" v-else></div>
      </template>
    </facilio-calendar>
  </div>
</template>

<script>
import FacilioCalendar from 'src/pages/workorder/FacilioCalendar'
import { getDisplayValue } from 'src/util/field-utils'
import { calendarSettings } from 'src/pages/workorder/CalendarConstants'
import { mapState } from 'vuex'
export default {
  components: {
    FacilioCalendar,
  },

  computed: {
    ...mapState({
      userAttendance: state => state.attendance.attendanceList,
      metaInfo: state => state.view.metaInfo,
    }),
  },
  created() {
    console.log('created fired in att cal')
    this.startTs = this.$route.query.startTime
    this.events = this.userAttendance.map(e => {
      return {
        id: e.id,
        start: e.day,
        status: this.getStatusDisplayVal(e.status)
          .substring(0, 1)
          .toUpperCase(),
        workingHours: e.workingHours,
      }
    })
    this.events.sort((e1, e2) => e1.start - e2.start)
  },
  data() {
    return {
      events: [],
      startTs: null,
    }
  },
  methods: {
    handleViewChange(e) {
      console.log('rendering calendar from parent', this.events)
      this.$refs['calendar'].renderCalendar(this.events)
    },
    getCalendarSettings() {
      // TO DO FIX LATER , dont pass as prop each time

      return calendarSettings
    },
    getStatusDisplayVal(enumVal) {
      return getDisplayValue(
        this.metaInfo.fields.find(e => e.name == 'status'),
        enumVal
      )
    },
  },
}
</script>

<style lang="scss">
.attendance-calendar {
  .facilio-calendar {
    height: calc(100vh - 275px);
  }

  .fc-cal-cell-body {
    display: flex;
  }
  .fc-task {
    flex-grow: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
  }

  .working-hrs {
    text-align: center;
    border-radius: 2px;
    width: 64px;
    height: 22px;
    background: #f3f3f8;
    color: #324056;
    font-size: 12px;
    font-weight: 500;
    border-radius: 2px;
    letter-spacing: 0.3px;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .working-hrs-dummy {
    width: 64px;
    height: 22px;
  }
}
</style>
