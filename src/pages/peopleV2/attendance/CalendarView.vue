<template>
  <div class="attendance-list-container">
    <div class="attendance-list-head">
      <div>
        {{ currentEmployeeName }}
      </div>
      <div>
        <TransitionButtons
          v-if="showTransitionButtons"
          :employeeID="employeeID"
          :allowDateTimeSelector="allowDateTimeSelector"
          :showAllButtons="true"
        />
      </div>
    </div>
    <div class="attendance-list-body">
      <vue-cal
        ref="vue-cal-component"
        activeView="month"
        :disable-views="['week', 'year', 'years', 'day']"
        :hideViewSelector="true"
        :hideTitleBar="true"
        :minDate="new Date(this.timelineRange.from)"
        :maxDate="new Date(this.timelineRange.to)"
        :startWeekOnSunday="true"
        :small="true"
        events-on-month-view="short"
        :events="getAttendanceforCal()"
      >
        <template #event="{ event }">
          <div
            class="vuecal__event-title"
            :style="{ 'background-color': event.background }"
          >
            <div>{{ event.title }}</div>
            <div>
              <fc-icon
                group="dsm"
                name="clock"
                size="8"
                color="#324056"
              ></fc-icon>
              <span style="font-size: 0.8em; margin-left: 3px">{{
                event.workingHours
              }}</span>
            </div>
          </div>
        </template>
      </vue-cal>
    </div>
  </div>
</template>
<script>
import AttendanceView from 'src/pages/peopleV2/attendance/AttendanceView.vue'
import VueCal from 'vue-cal'
import 'vue-cal/dist/vuecal.css'
export default {
  name: 'CalendarView',
  extends: AttendanceView,
  components: {
    VueCal,
  },
  data() {
    return {}
  },
  watch: {
    timelineRange: {
      handler() {
        this.fetchCurrentEmployeeAttendance()
        this.$refs['vue-cal-component'].switchView(
          'day',
          new Date(this.timelineRange.from)
        )
      },
      deep: true,
    },
  },
  methods: {
    getAttendanceforCal() {
      return this.attendance.list.map(a => ({
        title: this.getDisplayNameForStatus(a.status),
        start: this.formatDate(a.day),
        end: this.formatDate(a.day),
        background: a.shift.colorCode,
        workingHours: this.msToHoursAndMinutes(a.workingHours),
        class: 'cal-entry-pill',
      }))
    },
    formatDate(epoch) {
      const date = new Date(epoch)

      const datestring =
        date.getFullYear() +
        '-' +
        ('0' + (date.getMonth() + 1)).slice(-2) +
        '-' +
        ('0' + date.getDate()).slice(-2) +
        ' 12:00'
      return datestring
    },
  },
}
</script>

<style>
.vuecal--month-view .vuecal__cell {
  height: 80px;
}

.vuecal--month-view .vuecal__cell-content {
  justify-content: flex-start;
  height: 100%;
  align-items: flex-end;
}

.vuecal--month-view .vuecal__cell-date {
  padding: 4px;
  font-weight: bold;
}
.vuecal--month-view .vuecal__no-event {
  display: none;
}

.vuecal--month-view .vuecal__event {
  width: 95%;
  margin: 5px;
  border-radius: 5px;
}

.vuecal--month-view .vuecal__event-title {
  border-radius: 5px;
  padding: 3px;
  font-weight: bold;
}
</style>
<style scoped>
.cal-entry {
  margin: 4px;
}

.sort-icon {
  cursor: pointer;
}
.close-icon {
  cursor: pointer;
  margin: 10px;
}
.search-icon {
  cursor: pointer;
  border-right: 1px solid #e0e0e0;
  padding-right: 7px;
  margin-right: 7px;
}
.list-view-container {
  display: flex;
  padding: 5px;
  box-sizing: border-box;
  padding: 10px;
}
.employee-list-container {
  flex-basis: 20%;
  display: flex;
  flex-direction: column;
  border: solid 1px #e0e0e0;
  background-color: white;
  height: calc(100vh - 150px);
}
.employee-list-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-basis: 20%;
  font-weight: bold;
  padding: 10px;
  align-content: center;
  margin-top: 10px;
  margin-left: 5px;
  margin-bottom: 8px;
}
.employee-list-head {
  flex-basis: 10%;
}
.employee-list-body {
  flex-basis: 80%;
  overflow: scroll;
}
.employee-list-item {
  padding: 15px 15px 35px 15px;
  border-top: solid 1px #e0e0e0;
  font-weight: bold;
}
.employee-list-item:hover {
  cursor: pointer;
}
.active {
  border-left: 5px solid #f53085;
  padding-left: 10px;
  background-color: #f6f8fc;
}
.employee-list-foot {
  flex-basis: 10%;
  display: flex;
  justify-content: center;
  align-items: center;
  border-top: solid 1px #e0e0e0;
}
.attendance-list-container {
  flex-basis: 80%;
  background-color: white;
}
.attendance-list-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: solid 1px #e0e0e0;
  padding: 15px 15px 15px 15px;
  font-weight: bold;
}
</style>
