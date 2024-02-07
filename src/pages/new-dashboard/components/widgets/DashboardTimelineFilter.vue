<template>
  <div class="dashboard-timeline-filter" v-if="datePickerObj">
    <new-date-picker
      ref="datePickerRef"
      :zone="$timezone"
      :dateObj="datePickerObj"
      @date="dateChanged"
    ></new-date-picker>
  </div>
</template>

<script>
import NewDateHelper from 'src/pages/new-dashboard/components/date-picker/NewDateHelper.js'
import NewDatePicker from 'src/pages/new-dashboard/components/date-picker/NewDatePicker.vue'
export default {
  components: {
    NewDatePicker,
  },
  props: ['dbTimelineFilterInitialState'],
  data() {
    return {
      datePickerObj: null, //picker state
    }
  },
  created() {
    this.datePickerObj = NewDateHelper.getDatePickerObject(
      this.dbTimelineFilterInitialState.operatorId,
      this.dbTimelineFilterInitialState.dateValueString
    )
  },
  computed: {},
  methods: {
    dateChanged(pickerObj) {
      //set initial state of filters
      let timelineFilter = {}
      timelineFilter.startTime = pickerObj.value[0]
      timelineFilter.endTime = pickerObj.value[1]
      timelineFilter.operatorId = pickerObj.operatorId
      timelineFilter.dateLabel = pickerObj.label
      timelineFilter.dateValueString =
        pickerObj.value[0] + ',' + pickerObj.value[1]

      this.$emit('timelineFilterChanged', timelineFilter)
    },
  },
}
</script>
