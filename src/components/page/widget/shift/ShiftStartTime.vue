<template>
  <div class="box">
    <div class="label">{{ $t('common.shift.start_time') }}</div>
    <h3>{{ shiftStartTime }}</h3>
  </div>
</template>

<script>
import { getFormatedTime } from '@/mixins/TimeFormatMixin.js'
import PeopleMixin from 'src/pages/peopleV2/peopleMixin.vue'
export default {
  props: ['details'],
  name: 'ShiftStartTime',
  mixins: [PeopleMixin],

  computed: {
    widgetTitle() {
      return 'Shift Start Time'
    },
    shift() {
      return this.details
    },
    shiftStartTime() {
      return getFormatedTime(this.shift.startTime)
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
