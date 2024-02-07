<template>
  <div class="date-time-slot">
    <!-- DATE PAGE -->
    <div class="date-slot" v-if="!showTimeSlots">
      <div class="date-header">
        Select a day
      </div>
      <div class="date-container">
        <div
          v-for="(date, index) in sortedDates"
          class="date-item"
          :key="index"
          @click="selectDate(date)"
        >
          {{ formatDate(date) }}
        </div>
      </div>
      <div class="date-more" @click="openPicker">
        <div class="date-more-icon">
          <div class="date-more-icon-dot"></div>
          <div class="date-more-icon-dot"></div>
          <div class="date-more-icon-dot"></div>
        </div>
      </div>
    </div>
    <!-- TIME PAGE -->
    <div class="time-slot" v-if="showTimeSlots">
      <div class="time-header-date">
        <div class="left-arrow" @click="goBack"></div>
        {{ formatDate(selectedDateString) }}
      </div>
      <div class="time-header-title">
        Select a time
      </div>

      <div class="time-container">
        <div
          v-for="(timeString, index) in timeSlots"
          class="time-item"
          :key="index"
          @click="selectTime(timeString)"
        >
          {{ formatDuration(timeString) }}
        </div>
      </div>
    </div>
    <!-- PICKER FLOATING ON TOP -->
    <div class="more-picker">
      <f-date-picker
        ref="datePicker"
        v-model="pickerDate"
        :appendToBody="false"
        type="date"
        class="fc-input-full"
        @change="pickerDateSelected"
      ></f-date-picker>
    </div>
  </div>
</template>

<script>
import Vue from 'vue'
import moment from 'moment-timezone'
import FDatePicker from 'pages/assets/overview/FDatePicker'

export default {
  props: ['dateSlots'],
  components: { FDatePicker },
  data() {
    return {
      selectedDateString: null,
      showTimeSlots: false,
      pickerDate: null,
      defaultTimeSlots: [
        '10:00',
        '10:30',
        '12:00',
        '12:30',
        '14:00',
        '14:30',
        '16:00',
        '16:30',
      ],
    }
  },
  methods: {
    openPicker() {
      this.$refs['datePicker'].focus()
    },
    pickerDateSelected() {
      this.selectedDateString = moment
        .tz(this.pickerDate, this.$timezone)
        .format('DD-MM-YYYY')
      this.showTimeSlots = true
    },
    goBack() {
      this.showTimeSlots = false
      this.selectedDateString = null
    },
    selectDate(dateString) {
      this.selectedDateString = dateString
      this.showTimeSlots = true
    },
    selectTime(durationString) {
      let dateMillis = moment.tz(
        this.selectedDateString,
        'DD-MM-YYYY',
        Vue.prototype.$timezone
      )
      let durationMillis = moment.duration(durationString).valueOf()
      let dateTimeMillis = dateMillis + durationMillis

      let formattedDate = moment(dateTimeMillis)
        .tz(this.$timezone)
        .format('MMM DD hh:mm A')

      let dateTimeMessage = { id: dateTimeMillis, label: formattedDate }
      this.$emit('change', dateTimeMessage)
    },
    formatDuration(durationString) {
      //duration object doesn't support formatting to '2:45P PM', so covert to momentDateTime instance only to format
      let startOfDayMillis = moment()
        .startOf('day')
        .valueOf()
      let durationMilli = moment.duration(durationString).valueOf()

      let durationInDateTime = moment(startOfDayMillis + durationMilli)

      return durationInDateTime.format('hh:mm A')
    },
    formatDate(dateString) {
      return moment
        .tz(dateString, 'DD-MM-YYYY', Vue.prototype.$timezone)
        .format('dddd MMMM DD,YYYY')
    },
  },

  computed: {
    timeSlots() {
      if (!this.selectedDateString) {
        return null
      } else if (this.dateSlots[this.selectedDateString]) {
        return this.dateSlots[this.selectedDateString]
      } else {
        return this.defaultTimeSlots
      }
    },
    sortedDates() {
      return Object.keys(this.dateSlots).sort((dateString1, dateString2) => {
        return (
          moment
            .tz(dateString1, 'DD-MM-YYYY', Vue.prototype.$timezone)
            .valueOf() -
          moment
            .tz(dateString2, 'DD-MM-YYYY', Vue.prototype.$timezone)
            .valueOf()
        )
      })
    },
  },
}
</script>

<style lang="scss">
@import './style/_variables.scss';
.date-time-slot {
  align-self: flex-end;
  background: $bot-background;
  width: 282px;
  border: solid 1.5px $bot-base-color;
  border-radius: 15px;
  overflow: hidden;

  .time-header-date {
    display: flex;
    align-items: center;
  }

  .date-header,
  .time-header-date {
    padding: 20px 0px;
    background: $bot-light-blue;
    color: $bot-base-color;

    font-size: 15px;
    letter-spacing: 0.5px;
    font-weight: 500;
    text-align: center;
  }

  .time-header-title {
    padding-bottom: 20px;
    background: $bot-light-blue;
    color: $bot-base-color;
    font-size: 14px;
    letter-spacing: 0.5px;

    text-align: center;
  }
  .date-item {
    border-bottom: solid 1px $bot-border-color;
  }
  .date-item,
  .time-item {
    transition: background-color 0.5s ease;
    cursor: pointer;
    padding: 20px 0px;
    font-size: 13px;
    letter-spacing: 0.5px;

    text-align: center;
    color: $bot-base-color;
    &:hover {
      background: $bot-light-blue;
    }
  }
  .time-item {
    width: 50%;
    border-bottom: solid 1px $bot-border-color;
    border-right: solid 1px $bot-border-color;
  }
  .time-container {
    display: flex;
    justify-content: flex-start;
    flex-wrap: wrap;
  }
  .date-more {
    display: flex;
    cursor: pointer;
  }
  .date-more-icon {
    cursor: pointer;
    padding: 15px 5px;
    margin-left: auto;
    margin-right: auto;
    display: flex;
  }
  .date-more-icon-dot {
    height: 4px;
    width: 4px;
    margin-right: 3px;
    border-radius: 50%;
    background: $bot-base-color;
  }
  .left-arrow {
    cursor: pointer;
    border: solid #3e2a8c;
    border-width: 1.5px 0px 0px 1.5px;
    transform: rotate(-45deg);
    display: inline-block;
    padding: 4px;
    margin-left: 20px;
    margin-top: 4px;
    margin-right: 25px;
  }
  //position picker to open near bot header center
  .more-picker {
    position: absolute;
    width: 0;
    height: 0;
    // overflow:hidden;
    left: 40px;
    top: -8px;

    .el-date-editor,
    .el-input__inner {
      width: 1px !important;
      height: 1px !important;
      border: none !important;
    }
    .el-input__inner {
      opacity: 0;
    }

    .el-input__prefix {
      display: none;
    }
  }
}
</style>
