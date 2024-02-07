<template>
  <div>
    <AttendanceTopBar
      v-if="selectedEmployeeIsSet"
      :selectedEmployee="selectedEmployee"
      :showTransitionButtons="myAttendance"
      @timelineRangeUpdate="updateTimelineRange"
      @viewModeUpdate="updateViewMode"
    />
    <EmployeeSnippetView
      :myAttendance="myAttendance"
      @setEmployee="setEmployee"
    >
      <CalendarView
        v-if="isCalendarViewSelected"
        :timelineRange="timelineRange"
        :selectedEmployee="selectedEmployee"
        :myAttendance="myAttendance"
      />
      <ListView
        v-else
        :timelineRange="timelineRange"
        :selectedEmployee="selectedEmployee"
        :myAttendance="myAttendance"
      />
    </EmployeeSnippetView>
  </div>
</template>
<script>
import AttendanceTopBar from 'src/pages/peopleV2/attendance/AttendanceTopBar'
import ListView from 'src/pages/peopleV2/attendance/ListView'
import CalendarView from 'src/pages/peopleV2/attendance/CalendarView.vue'
import EmployeeSnippetView from 'src/pages/peopleV2/attendance/EmployeeSnippetView.vue'
export default {
  name: 'AttendanceList',
  props: {
    myAttendance: {
      type: Boolean,
      default: false,
    },
  },
  components: {
    AttendanceTopBar,
    ListView,
    CalendarView,
    EmployeeSnippetView,
  },
  created() {
    this.initDefaults()
  },
  data() {
    return {
      timelineRange: null,
      viewMode: null,
      selectedEmployee: null,
    }
  },
  computed: {
    isCalendarViewSelected() {
      return Boolean(this.viewMode && this.viewMode === 'Calendar View')
    },
    selectedEmployeeIsSet() {
      return Boolean(this.selectedEmployee != null)
    },
  },
  watch: {
    myAttendance: {
      handler() {
        this.selectedEmployee = null
      },
    },
  },
  methods: {
    initDefaults() {
      this.viewMode = 'Calendar View'
      const date = new Date()
      const from = new Date(date.getFullYear(), date.getMonth(), 1).valueOf()
      const to = new Date(date.getFullYear(), date.getMonth() + 1, 0).valueOf()
      this.timelineRange = { from, to }
    },
    updateTimelineRange(value) {
      this.timelineRange = value
    },
    updateViewMode(value) {
      this.viewMode = value
    },
    setEmployee(value) {
      this.selectedEmployee = value
    },
  },
}
</script>
