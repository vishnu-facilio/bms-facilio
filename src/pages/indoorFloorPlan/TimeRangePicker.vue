<template>
  <div
    class="booking-time-range-picker d-flex align-center pointer relative"
    @click="openPopover"
  >
    <i class="el-icon-time time-icon pointer mR10"></i>
    <div class="selected-time mR10">
      {{ startTimeString }}
    </div>
    <i class="el-icon-right bookings-between mR10"></i>

    <div class="selected-time">
      {{ endTimeString }}
    </div>
    <TimeRangePopover
      :popoverState="popoverState"
      v-if="popoverOpen"
      :visibility.sync="popoverOpen"
      :customIndex="indexof"
      @save="saved"
    >
    </TimeRangePopover>
  </div>
</template>

<script>
import TimeRangePopover from './TimeRangePopover'
import moment from 'moment'

export default {
  components: {
    TimeRangePopover,
  },
  props: ['customTime'],
  mounted() {
    if (this.customTime?.startTime) {
      this.customTimeEmit()
    }
  },
  data() {
    return {
      popoverOpen: false,
      startTimeString: '9:00 AM',
      endTimeString: '5:00 PM',
      popoverState: null,
      indexof: {
        start: '09:00',
        end: '05:00',
        startAmPm: 'AM',
        endAmPm: 'PM',
      },
    }
  },

  methods: {
    openPopover() {
      this.popoverOpen = true
    },
    customTimeEmit() {
      let startTs = this.customTime.startTime
      let endTs = this.customTime.endTime
      const st = moment()
        .startOf('day')
        .add(startTs, 'milliseconds')
      const et = moment()
        .startOf('day')
        .add(endTs, 'milliseconds')
      let sthour = st.format('hh')
      let stminute = st.format('mm')
      let stamPm = st.format('A')
      let ethour = et.format('hh')
      let etminute = et.format('mm')
      let etamPm = et.format('A')
      this.indexof.start = sthour + ':' + stminute
      this.indexof.end = ethour + ':' + etminute
      this.indexof.startAmPm = stamPm
      this.indexof.endAmPm = etamPm
      this.startTimeString = sthour + ':' + stminute + ' : ' + stamPm
      this.endTimeString = ethour + ':' + etminute + ' : ' + etamPm
      this.$emit('input', {
        timeMillis: [startTs, endTs],
        timeStrings: [
          sthour + ':' + stminute + ' : ' + stamPm,
          ethour + ':' + etminute + ' : ' + etamPm,
        ],
      })
      this.$emit(
        'change',
        this.$emit('input', {
          timeMillis: [startTs, endTs],
          timeStrings: [
            sthour + ':' + stminute + ' : ' + stamPm,
            ethour + ':' + etminute + ' : ' + etamPm,
          ],
        })
      )
    },

    saved(e) {
      this.popoverState = e
      //calculate start and end timestamps
      let slotMillis = 30 * 60 * 1000 //30 min slot in millis
      let pmMillis = 12 * 60 * 60 * 1000 //12 hours in millis

      let startTs = e.start.index * slotMillis
      let endTs = e.end.index * slotMillis

      if (e.start.period == 'PM') {
        startTs += pmMillis
      }

      if (e.end.period == 'PM') {
        endTs += pmMillis
      }

      this.startTimeString = e.start.slotString + ' : ' + e.start.period
      this.endTimeString = e.end.slotString + ' : ' + e.end.period

      this.$emit('input', {
        timeMillis: [startTs, endTs],
        timeStrings: [
          e.start.slotString + ' : ' + e.start.period,
          e.end.slotString + ' : ' + e.end.period,
        ],
      })
      this.$emit(
        'change',
        this.$emit('input', {
          timeMillis: [startTs, endTs],
          timeStrings: [
            e.start.slotString + ' : ' + e.start.period,
            e.end.slotString + ' : ' + e.end.period,
          ],
        })
      )
      this.popoverOpen = false
    },
  },
}
</script>

<style lang="scss">
.booking-time-range-picker {
  padding: 3px 12px;
  border-radius: 5px;
  border: 1px solid #efefef;
  background-color: #ffffff;
  &:hover {
    background: rgba(57, 179, 194, 0.1);
    -webkit-transition: 0.6s all;
    transition: 0.6s all;
  }

  .selected-time {
    cursor: pointer;
    padding: 5px 12px;
    border-radius: 10px;
    border: solid 0.5px #39b2c2;
    background-color: #f5feff;
    font-size: 11px;
    font-weight: 500;
    letter-spacing: 0.5px;
    color: #27a0b0;
  }
  .bookings-between {
    color: #605e88;
    font-weight: 600;
  }
  .time-icon {
    color: #605e88;
    font-size: 16px;
  }
}
</style>
