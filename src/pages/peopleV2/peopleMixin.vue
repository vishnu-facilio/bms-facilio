<script>
import moment from 'moment-timezone'
export default {
  methods: {
    getTodayDate() {
      return new Date().setHours(0, 0, 0, 0).valueOf()
    },
    computeSlotEndDay(epoch, span) {
      return moment(epoch)
        .add(span - 1, 'days')
        .valueOf()
    },
    readableTime(secondsOfDay) {
      if (!secondsOfDay) {
        secondsOfDay = 0
      }
      const millisecondsOfDay = secondsOfDay

      return this.formatDateWithOnlyTime(
        new Date().setHours(0, 0, 0, 0) + millisecondsOfDay
      )
    },
    prettifyEmpty(value) {
      return value ? value : '---'
    },
    formatTime(t) {
      return t.toString().padStart(2, '0')
    },
    secondsToHoursAndMinutes(seconds) {
      if (!seconds) {
        return '---'
      }
      const h = Math.floor(seconds / 3600)
      const m = Math.floor((seconds % 3600) / 60)
      return `${h}h : ${m}m`
    },
    msToHoursAndMinutes(ms) {
      if (!ms) {
        return '---'
      }
      return this.secondsToHoursAndMinutes(ms / 1000)
    },
    formatDate(date, exclTime, onlyTime) {
      if (!date) {
        return '---'
      }
      return this.$options.filters.formatDate(date, exclTime, onlyTime)
    },
    formatDateWithoutTime(date) {
      return this.formatDate(date, true, false)
    },
    formatDateWithOnlyTime(date) {
      return this.formatDate(date, false, true)
    },
    getDisplayNameForTransition(name) {
      switch (name) {
        case 'CHECK_IN':
          return 'Check-In'
        case 'CHECK_OUT':
          return 'Check-Out'
        case 'RESUME_WORK':
          return 'Resume Work'
        case 'BREAK':
          return 'Break'
        default:
          return '---'
      }
    },
    getDisplayNameForStatus(name) {
      switch (name) {
        case 'ABSENT':
          return 'Absent'
        case 'PRESENT':
          return 'Present'
        case 'WEEKLY_OFF':
          return 'Weekly Off'
        case 'LEAVE':
          return 'Leave'
        default:
          return '---'
      }
    },
  },
}
</script>
