<template>
  <component ref="alarm" v-if="layout" :is="layout" :moduleName="moduleName">
  </component>
</template>
<script>
import oldAlarm from 'pages/firealarm/alarms/v1/NewAlarmSummary'
import newAlarm from 'pages/firealarm/alarms/v2/AlarmSummary'
import newSummary from 'pages/firealarm/alarms/v1/AlarmOverview'
export default {
  props: ['config, widget'],
  components: {
    'old-alarm': oldAlarm,
    'new-alarm': newAlarm,
    'new-summary': newSummary,
  },
  computed: {
    layout() {
      console.log(this.$store.state.view.currentViewDetail)

      if (this.$helpers.isLicenseEnabled('NEW_ALARMS')) {
        //  if  (this.$org.id === 78) {
        if (
          this.$store.state.view.currentViewDetail &&
          this.$store.state.view.currentViewDetail.moduleName === 'bmsalarm'
        ) {
          return 'new-alarm'
        } else {
          return 'new-summary'
        }
        // }
        // return 'new-alarm'
      }
      return 'old-alarm'
    },
    moduleName() {
      return this.$store.state.view.currentViewDetail.moduleName
    },
  },
}
</script>

<style></style>
