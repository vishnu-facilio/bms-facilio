<template>
  <div class="box">
    <div class="label">{{ $t('common.shift.end_time') }}</div>
    <h3>{{ shiftEndTime }}</h3>
  </div>
</template>

<script>
import PeopleMixin from 'src/pages/peopleV2/peopleMixin.vue'
import { getFormatedTime } from '@/mixins/TimeFormatMixin.js'
export default {
  props: ['details'],
  name: 'ShiftEndTime',
  mixins: [PeopleMixin],

  computed: {
    widgetTitle() {
      return 'Shift Start Time'
    },
    shift() {
      return this.details
    },
    shiftEndTime() {
      return getFormatedTime(this.shift.endTime)
    },
  },
  methods: {
    formatDate(date, exclTime, onlyTime) {
      return this.$options.filters.formatDate(date, exclTime, onlyTime)
    },
    formatDateWithOnlyTime(date) {
      return this.formatDate(date, false, true)
    },
  },
}
</script>

<style scoped>
.box {
  padding: 20px;
}
.label {
  font-size: 1.1em;
}
</style>
