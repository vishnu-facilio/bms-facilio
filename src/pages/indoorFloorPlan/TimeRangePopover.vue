<template>
  <div
    class="time-picker-popover d-flex flex-col align-center pT20 pL15 pR15 pB20"
    v-click-outside="outsideClick"
  >
    <div class="popper-arrow">
      <i class="el-icon-caret-top"></i>
    </div>
    <div class="time-picker-popover-body d-flex align-center">
      <div class="start-time-slots-container">
        <div class="time-slots-container-title mB15">
          Start time
        </div>
        <div class="time-slot-list">
          <div
            @click="timeSlotClick('START_TIME', index)"
            v-for="(slot, index) in slotStrings"
            :key="index"
            class="time-slot"
            :class="[{ active: index == startTimeSlotIndex }]"
          >
            {{ slot }}
          </div>
        </div>
        <el-radio-group v-model="startTimeSlotPeriod" class="period-switch">
          <el-radio-button label="AM">
            AM
          </el-radio-button>
          <el-radio-button label="PM">
            PM
          </el-radio-button>
        </el-radio-group>
      </div>
      <div class="end-time-slots-container">
        <div class="time-slots-container-title mB10">
          End time
        </div>
        <div class="time-slot-list">
          <div
            @click="timeSlotClick('END_TIME', index)"
            v-for="(slot, index) in slotStrings"
            :key="index"
            class="time-slot"
            :class="[{ active: index == endTimeSlotIndex }]"
          >
            {{ slot }}
          </div>
        </div>
        <el-radio-group v-model="endTimeSlotPeriod" class="period-switch">
          <el-radio-button label="AM">
            AM
          </el-radio-button>
          <el-radio-button label="PM">
            PM
          </el-radio-button>
        </el-radio-group>
      </div>
    </div>
    <div class="time-picker-popover-footer mT15 pointer " @click.stop="save">
      Done
    </div>
  </div>
</template>

<script>
const slots = [
  '12:00',
  '12:30',
  '01:00',
  '01:30',
  '02:00',
  '02:30',
  '03:00',
  '03:30',
  '04:00',
  '04:30',
  '05:00',
  '05:30',
  '06:00',
  '06:30',
  '07:00',
  '07:30',
  '08:00',
  '08:30',
  '09:00',
  '09:30',
  '10:00',
  '10:30',
  '11:00',
  '11:30',
]
export default {
  created() {
    console.log('im in created')
    if (this.popoverState) {
      this.startTimeSlotIndex = this.popoverState.start.index
      this.endTimeSlotIndex = this.popoverState.end.index
      this.startTimeSlotPeriod = this.popoverState.start.period
      this.endTimeSlotPeriod = this.popoverState.end.period
    }
  },

  props: ['popoverState', 'customIndex'],
  mounted() {
    console.log('ct popover : ', this.customIndex)
    if (this.customIndex.start) {
      this.startTimeSlotIndex = slots.indexOf(this.customIndex.start)
      this.endTimeSlotIndex = slots.indexOf(this.customIndex.end)
      this.startTimeSlotPeriod = this.customIndex.startAmPm
      this.endTimeSlotPeriod = this.customIndex.endAmPm
    }
  },
  data() {
    return {
      clickOutsideFirstTime: true,
      slotStrings: [
        '12:00',
        '12:30',
        '01:00',
        '01:30',
        '02:00',
        '02:30',
        '03:00',
        '03:30',
        '04:00',
        '04:30',
        '05:00',
        '05:30',
        '06:00',
        '06:30',
        '07:00',
        '07:30',
        '08:00',
        '08:30',
        '09:00',
        '09:30',
        '10:00',
        '10:30',
        '11:00',
        '11:30',
      ],
      startTimeSlotIndex: 18,
      startTimeSlotPeriod: 'AM',
      endTimeSlotIndex: 10,
      endTimeSlotPeriod: 'PM',
    }
  },
  methods: {
    timeSlotClick(slotType, slotIndex) {
      if (slotType == 'START_TIME') {
        this.startTimeSlotIndex = slotIndex
      } else if (slotType == 'END_TIME') {
        this.endTimeSlotIndex = slotIndex
      }
    },
    outsideClick() {
      if (this.clickOutsideFirstTime) {
        // console.log('IGNORING FIRST TIME ') //bad hack
        this.clickOutsideFirstTime = false
        return
      }
      // console.log('outsideclick')
      this.$emit('update:visibility', false)
    },
    save() {
      this.$emit('save', {
        start: {
          index: this.startTimeSlotIndex,
          period: this.startTimeSlotPeriod,
          slotString: this.slotStrings[this.startTimeSlotIndex],
        },
        end: {
          index: this.endTimeSlotIndex,
          slotString: this.slotStrings[this.endTimeSlotIndex],
          period: this.endTimeSlotPeriod,
        },
      })
    },
  },
}
</script>

<style lang="scss">
.time-picker-popover {
  width: 350px;
  position: absolute;
  left: -40px;
  background-color: #ffffff;
  top: 55px;
  display: flex;
  border: 1px solid #e4eaed;
  border-radius: 6px;
  box-shadow: 0 1px 6px 0 rgba(0, 0, 0, 0.15);
  &:hover {
    box-shadow: 0 1px 6px 0 rgba(0, 0, 0, 0.15);
  }
  .time-slots-container-title {
    font-size: 13px;
    letter-spacing: 0.5px;
    color: #324056;
    font-weight: 600;
    text-align: center;
  }

  .time-slot-list {
    display: grid;
    grid-template-columns: auto auto;
    grid-column-gap: 20px;
    grid-row-gap: 10px;
  }
  .time-slot {
    cursor: pointer;
    border-radius: 5px;
    background-color: #ffffff;
    letter-spacing: 0.5px;
    color: #000000;
    font-size: 12px;
    padding: 5px 10px;
    border: 1px solid #e4eaed;
    &.active {
      background-color: #39b3c2;
      color: #ffffff;
      font-weight: 500;
    }
    &:hover {
      background-color: #39b3c2;
      color: #ffffff;
      box-shadow: 0 1px 4px 0 rgb(0 0 0 / 10%);
    }
  }

  .start-time-slots-container,
  .end-time-slots-container {
    width: 170px;
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  .el-radio-button__inner {
    padding: 5px 8px !important;
    font-size: 10px !important;
  }
  .period-switch {
    margin-top: 20px;
  }
  .time-picker-popover-footer {
    text-align: center;
    font-size: 12px;
    letter-spacing: 0.7px;
    color: #ffffff;
    background-color: #ff3184;
    border-radius: 3px;
    padding: 8px 25px;
    width: 90px;
    font-weight: bold;
    text-transform: uppercase;
  }
  .popper-arrow {
    position: absolute;
    top: -26px;
    .el-icon-caret-top {
      color: #fff;
      font-size: 38px;
    }
  }
}
</style>
