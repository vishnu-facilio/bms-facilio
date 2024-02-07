<template>
  <div class="newanalytics-generater mobile-rca" v-if="!loading">
    <rca-chart
      id="mobile-summary-rca"
      :details="analyticsConfig"
      v-if="analyticsConfig"
      :width="width"
      class="newanalytics-generater-container"
    ></rca-chart>
  </div>
</template>
<script>
import rcaChart from 'src/components/page/widget/alarms/MobileAlarmRca'
import mobilesummaryChart from 'pages/energy/analytics/newTools/MobileAlarmSummeryChart'
export default {
  extends: mobilesummaryChart,
  components: {
    rcaChart,
  },
  computed: {
    width() {
      if (
        document.getElementById('q-app') &&
        document.getElementById('q-app')
      ) {
        return document.getElementById('q-app').offsetWidth - 30
      }
      return 300
    },
  },
}
</script>
<style lang="scss">
.mobile-chart {
  border: none;
  box-shadow: none;
}
.empty-multi-chart {
  display: none;
}
.mobile-rca .fc-alarms-chart-title {
  display: none;
}
.mobile-rca {
  background: #fff;
}
</style>
