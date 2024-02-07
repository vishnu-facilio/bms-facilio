import NewDateHelper from '@/mixins/NewDateHelper'
export default {
  mounted() {
    this.loadMobileQueryData()
  },
  data() {
    return {
      analyticsLoading: true,
    }
  },
  methods: {
    loadMobileQueryData() {
      this.analyticsLoading = true
      let querryData = this.$route.query
      if (querryData.dataPoints) {
        this.analyticsConfig.dataPoints = JSON.parse(querryData.dataPoints)
      }
      if (querryData.period) {
        this.analyticsConfig.period = parseInt(querryData.period)
      }
      if (querryData.mode) {
        this.analyticsConfig.mode = parseInt(querryData.mode)
      }
      if (querryData.dateFilter) {
        let date = null
        date = JSON.parse(querryData.dateFilter)
        let timezone = JSON.parse(querryData.timezone)
          ? JSON.parse(querryData.timezone)
          : null
        if (date.operatorId && date.value) {
          this.analyticsConfig.dateFilter = NewDateHelper.getDatePickerObject(
            date.operatorId,
            date.value,
            timezone
          )
        } else if (date.operatorId) {
          this.analyticsConfig.dateFilter = NewDateHelper.getDatePickerObject(
            date.operatorId,
            null,
            timezone
          )
        } else {
          this.analyticsConfig.dateFilter = NewDateHelper.getDatePickerObject(
            22,
            null,
            timezone
          )
        }
      }
      if (querryData.api) {
        this.analyticsConfig.api = querryData.api
      }
      if (querryData.name) {
        this.analyticsConfig.name = querryData.name
      }
      if (querryData.chartType) {
        this.analyticsConfig.chartType = querryData.chartType
      }
      if (querryData.hidechart === 'true' || querryData.hidechart === true) {
        this.analyticsConfig.hidechart = true
      }
      if (
        querryData.hidetabular === 'true' ||
        querryData.hidetabular === true
      ) {
        this.analyticsConfig.hidetabular = true
      }
      if (
        querryData.hidecharttypechanger === 'true' ||
        querryData.hidecharttypechanger === true
      ) {
        this.analyticsConfig.hidecharttypechanger = true
      }
      this.analyticsLoading = false
    },
  },
}
