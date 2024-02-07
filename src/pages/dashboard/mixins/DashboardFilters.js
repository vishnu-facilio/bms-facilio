// DEPRECATED , DO NOT IMPORT THIS. SEE dashboardFilterContainer for new db filter impl
// NOT removed yet as in use in OLD CARD/Widget components such
// KPITargetMeter,ReadingCard.vue etc.

import NewDateHelper from 'src/components/mixins/NewDateHelper'
export default {
  computed: {
    dashboardDateFilter() {
      if (this.$route.query.dashboardFilters) {
        return JSON.parse(this.$route.query.dashboardFilters).dateFilter
      } else {
        return null
      }
    },
  },
  methods: {
    getDateFilterStartTime() {
      return Number.parseInt(this.dashboardDateFilter.value[0])
    },
    getDateFilterEndTime() {
      return Number.parseInt(this.dashboardDateFilter.value[1])
    },
    getDashboardPickerObj() {
      return NewDateHelper.getDatePickerObject(
        this.dashboardDateFilter
          ? this.dashboardDateFilter.operatorId
          : this.dateOperator,
        this.dashboardDateFilter
          ? this.dashboardDateFilter.value
          : this.dateValue
      )
    },
  },
}
