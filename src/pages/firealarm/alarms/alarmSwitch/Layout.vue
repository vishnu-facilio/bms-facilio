<template>
  <component ref="alarm" v-if="layout" :is="layout"> </component>
</template>
<script>
import oldAlarm from 'pages/firealarm/alarms/alarms/v1/layout'
import newAlarm from 'pages/firealarm/alarms/v2/Layout'
export default {
  props: ['config, widget'],
  components: {
    'old-alarm': oldAlarm,
    'new-alarm': newAlarm,
  },
  computed: {
    layout() {
      if (this.$helpers.isLicenseEnabled('NEW_ALARMS')) {
        return 'new-alarm'
      }
      return 'old-alarm'
    },
  },
}
</script>

<style></style>
