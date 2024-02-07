<template>
  <div>
    <ShiftPlanTopBar
      @timelineRangeUpdate="updateTimelineRange"
      @viewModeUpdate="updateViewMode"
    />
    <CalendarView v-if="isCalendarViewSelected" :timelineRange="timelineRange">
    </CalendarView>
    <ListView v-else :timelineRange="timelineRange" />
  </div>
</template>
<script>
import ShiftPlanTopBar from 'src/components/shiftPlanner/ShiftPlanTopBar'
import ListView from 'src/components/shiftPlanner/ListView'
import CalendarView from 'src/components/shiftPlanner/CalendarView'

export default {
  name: 'PlannerOverview',
  components: {
    ShiftPlanTopBar,
    ListView,
    CalendarView,
  },
  created() {
    this.initDefaults()
  },
  data() {
    return {
      timelineRange: null,
      viewMode: null,
    }
  },
  computed: {
    isCalendarViewSelected() {
      return this.viewMode && this.viewMode === 'Calendar View'
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
  },
}
</script>
