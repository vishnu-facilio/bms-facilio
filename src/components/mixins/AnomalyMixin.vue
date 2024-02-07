<script>
import moment from 'moment-timezone'
import NewDateHelper from '@/mixins/NewDateHelper'

export default {
  computed: {
    lastMonth() {
      return moment()
        .tz(this.$timezone)
        .subtract(1, 'months')
        .format('MMMM')
    },
    currentMonth() {
      return moment()
        .tz(this.$timezone)
        .startOf('month')
        .format('MMMM')
    },
    isMetricsApiLoading() {
      return this.$store.state.anomalies.isLoading
    },
    currMntNLastMntRanges() {
      let thisMonthdateRange = NewDateHelper.getDatePickerObject(
        28,
        this.$timezone
      )
      let lastMonthDateRange = NewDateHelper.getDatePickerObject(
        27,
        this.$timezone
      )
      let dateRanges = {}
      dateRanges.startTime = lastMonthDateRange.value[0]
      dateRanges.endTime = thisMonthdateRange.value[1]
      return dateRanges
    },
  },
  methods: {
    loadEnrgyByCdd(resourcesIds) {
      return this.$util.getWorkFlowResult(49, [
        resourcesIds,
        this.details.alarm.resource.siteId,
      ])
    },
    loadEnergyWastage(resourceIds) {
      return this.$util.getWorkFlowResult(99, [
        resourceIds,
        this.details.alarm.id,
      ])
    },
    loadAnomaliesCount(resourceIds, isRCA) {
      return this.$util.getWorkFlowResult(100, [
        resourceIds,
        this.details.alarm.id,
        isRCA || false,
      ])
    },
    loadDeviation(resourceIds) {
      return this.$util.getWorkFlowResult(46, [
        resourceIds,
        this.details.alarm.id,
      ])
    },
    loadMetrics(resourceId, alarmId, siteId, dateRange, rca) {
      let url = `/v2/mlAnomalyAlarm/metrics`
      let paramJson = {}
      paramJson.alarmId = alarmId
      paramJson.resourceId = resourceId
      paramJson.siteId = siteId
      if (dateRange != null) {
        paramJson.dateRange = dateRange
      }
      if (rca) {
        paramJson.rca = rca
      }
      this.loading = true
      return new Promise((resolve, reject) => {
        this.$http
          .post(url, paramJson)
          .then(response => {
            resolve(response.data.result.metrics)
          })
          .catch(function(error) {
            reject(error)
          })
      })
    },
    loadMeanMetrics() {
      let workflowID
      if (this.moduleName === 'readingalarm') {
        workflowID = 8
      } else if (this.moduleName === 'newreadingalarm') {
        workflowID = 108
      } else if (
        this.moduleName === 'agentAlarm' ||
        this.moduleName === 'operationalarm'
      ) {
        workflowID = 11
      } else {
        workflowID = 47
      }
      // if (!this.details.metricsPromise) {
      let params = [this.details.alarm.id]
      if (this.moduleName === 'newreadingalarm') {
        params.push(this.cardId)
      }
      let promise = this.$util
        .getWorkFlowResult(workflowID, params)
        .finally(_ => {
          this.$set(this.details, 'metricsPromise', null)
        })
      this.$set(this.details, 'metricsPromise', promise)
      // }
      return this.details.metricsPromise
    },
  },
}
</script>
