<template>
  <FContainer display="flex" cursor="pointer">
    <fc-icon group="dsm" name="chevron-left" @click="prevWeek"></fc-icon>
    <FText
      marginLeft="containerMedium"
      marginRight="containerMedium"
      paddingTop="containerSmall"
      >{{ weekInfo }}</FText
    >
    <fc-icon group="dsm" name="chevron-right" @click="nextWeek"></fc-icon>
  </FContainer>
</template>

<script>
import helpers from 'src/util/helpers'
const { getOrgMoment: moment } = helpers
import { FContainer, FText } from '@facilio/design-system'
export default {
  data() {
    return {
      startDate: null,
      endDate: null,
    }
  },
  props: ['resetPicker'],
  components: {
    FContainer,
    FText,
  },
  computed: {
    weekInfo() {
      let { startDate, endDate } = this
      startDate = moment(startDate).format('MMM-DD-YYYY')
      endDate = moment(endDate).format('MMM-DD-YYYY')
      return `${startDate} to ${endDate}`
    },
  },
  watch: {
    resetPicker: {
      async handler(newVal) {
        if (newVal) {
          this.setCurrentWeek()
        }
      },
      immediate: true,
    },
  },
  methods: {
    setCurrentWeek() {
      const now = new Date()
      const firstDayOfWeek = new Date(now)
      const lastDayOfWeek = new Date(firstDayOfWeek)
      const dayOfWeek = now.getDay()
      const daysUntilMonday = dayOfWeek === 0 ? 6 : dayOfWeek - 1 // Monday Calculation

      firstDayOfWeek.setDate(now.getDate() - daysUntilMonday)
      lastDayOfWeek.setDate(firstDayOfWeek.getDate() + 6)
      firstDayOfWeek.setHours(0, 0, 0, 0)
      lastDayOfWeek.setHours(23, 59, 59, 999)

      this.startDate = firstDayOfWeek.getTime()
      this.endDate = lastDayOfWeek.getTime()

      this.$emit('goToCurrentWeek', {
        startDate: this.startDate,
        endDate: this.endDate,
      })
    },
    prevWeek() {
      const prevStartDate = new Date(this.startDate)
      const prevEndDate = new Date(this.endDate)

      prevStartDate.setDate(prevStartDate.getDate() - 7)
      prevEndDate.setDate(prevEndDate.getDate() - 7)
      prevStartDate.setHours(0, 0, 0, 0)
      prevEndDate.setHours(23, 59, 59, 999)

      this.startDate = prevStartDate.getTime()
      this.endDate = prevEndDate.getTime()
      this.$emit('goToPreviousWeek', {
        startDate: this.startDate,
        endDate: this.endDate,
      })
    },

    nextWeek() {
      const nextStartDate = new Date(this.startDate)
      const nextEndDate = new Date(this.endDate)

      nextStartDate.setDate(nextStartDate.getDate() + 7)
      nextEndDate.setDate(nextEndDate.getDate() + 7)
      nextStartDate.setHours(0, 0, 0, 0)
      nextEndDate.setHours(23, 59, 59, 999)

      this.startDate = nextStartDate.getTime()
      this.endDate = nextEndDate.getTime()
      this.$emit('goToNextWeek', {
        startDate: this.startDate,
        endDate: this.endDate,
      })
    },
  },
  mounted() {
    this.setCurrentWeek()
  },
}
</script>
