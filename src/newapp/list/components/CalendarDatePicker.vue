<template>
  <div class="calendar-date-picker">
    <DatePicker
      v-if="showDatePicker"
      :dateObj="datePickerObj"
      :zone="$timezone"
      :tabs="datePickerTabs"
      :iconDisabled="isLoading"
      @date="dateObj => (timeStamp = dateObj)"
      @today-clicked="todayclicked = true"
      class="calendar-date-picker-btn"
    ></DatePicker>
  </div>
</template>
<script>
import DatePicker from 'src/newapp/list/components/NewDatePicker'
import NewDateHelper from 'src/components/mixins/NewDateHelper'
import moment from 'moment-timezone'
import { isEmpty } from '@facilio/utils/validation'
import { eventBus } from '@/page/widget/utils/eventBus'

const datePickerTabs = {
  enableByOperationOnId: true,
  disableDefaultLabels: true,
  enabledTabs: ['M'],
}

export default {
  components: { DatePicker },
  data() {
    return {
      datePickerTabs,
      datePickerObj: null,
      timeStamp: null,
      showDatePicker: true,
      todayclicked: true,
      isLoading: false,
    }
  },
  async mounted() {
    await this.setDateObj(this.startTime)
    eventBus.$on('custom-event', payload => {
      this.isLoading = payload
    })
  },
  beforeDestroy() {
    eventBus.$off('custom-event')
  },
  computed: {
    startTime() {
      let { query } = this.$route || {}
      let { startTime } = query || {}

      return startTime ? parseInt(startTime) : null
    },
  },
  watch: {
    timeStamp(newVal) {
      this.setQuery(newVal)
    },
    startTime(newVal) {
      this.setDateObj(newVal)
    },
  },
  methods: {
    setDateObj(newVal) {
      this.showDatePicker = false

      let startOfMonth = moment
        .tz(newVal, this.$timezone)
        .startOf('month')
        .valueOf()

      this.datePickerObj = NewDateHelper.getDatePickerObject(
        64, //for now its only month view 64 defines operatorId for month
        `${startOfMonth}`
      )
      if (isEmpty(newVal)) this.setQuery(this.datePickerObj)
      this.$nextTick(() => {
        this.showDatePicker = true
      })
    },
    setQuery(dateObj) {
      let { operationOn, value } = dateObj || {}
      let [startTime, endTime] = value || []
      let currentView = operationOn.toUpperCase()
      let { query } = this.$route || {}

      let todayDate = this.todayclicked
        ? moment
            .tz(this.$timezone)
            .startOf('day')
            .valueOf()
        : null

      query = {
        ...query,
        currentView,
        startTime,
        endTime,
        refDate: todayDate,
        onChange: true,
        today: this.todayclicked,
      }

      this.$router.replace({ query }).catch(() => {})
      this.todayclicked = false
    },
  },
}
</script>
<style lang="scss" scoped>
.calendar-date-picker {
  display: flex;
  gap: 10px;
  align-items: center;
  background: #fff;
  margin: 0px 10px;
  padding: 0px 16px;
  border-radius: 4px 4px 0px 0px;
  border-bottom: 1px solid #f1f2f4;
  min-height: 40px;
}
.calendar-date-picker-btn {
  width: 100%;
}
</style>
