<template>
  <component
    ref="alarm"
    v-if="layout"
    :is="layout"
    @syncCount="callbackMethod"
    @showTag="showTagInList"
  >
  </component>
</template>
<script>
import oldAlarm from 'pages/firealarm/alarms/alarms/v1/AlarmsList'
import newAlarm from 'pages/firealarm/alarms/v2/AlarmList'
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
  methods: {
    callbackMethod(val) {
      this.$emit('syncCount', val)
    },
    showTagInList(val) {
      this.$emit('showTag', val)
    },
  },
}
</script>

<style></style>
