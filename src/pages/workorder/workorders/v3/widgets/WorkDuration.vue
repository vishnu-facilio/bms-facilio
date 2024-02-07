<template>
  <div class="white-bg-block mb fc__wo__sum__timer pB20 mT10 pT20">
    <div class="fc-id11 text-uppercase pL15">
      {{ $t('maintenance._workorder.work_duration') }}
    </div>
    <wo-timer
      class="alarm-timer wo-duration"
      :actualWorkDurationField="actualWorkDurationField"
    ></wo-timer>
  </div>
</template>
<script>
import WoTimer from '@/WorkorderTimer'
import workorderMixin from 'pages/workorder/workorders/v1/mixins/workorderHelper'
export default {
  name: 'WorkDuration',
  mixins: [workorderMixin],
  components: {
    WoTimer,
  },
  mounted() {
    this.loadFields()
  },
  data() {
    return {
      loading: true,
      fields: [],
    }
  },
  computed: {
    actualWorkDurationField() {
      let { fields } = this
      return fields.find(field => field.name === 'actualWorkDuration')
    },
  },
}
</script>
