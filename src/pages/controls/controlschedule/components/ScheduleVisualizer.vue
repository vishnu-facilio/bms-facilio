<template>
  <div class="schedule-visualizer-container">
    <div
      v-for="(hours, indexHours) in getTimeInterval"
      :key="indexHours"
      :class="[
        'flex flex-row',
        indexHours === 0 && 'position-sticky bg-white zindex999',
      ]"
    >
      <div
        v-for="(days, indexDays) in xAxis"
        :key="indexDays"
        :class="[getCellClass(indexDays, indexHours)]"
        :style="cellDimension"
      >
        <div v-if="indexHours === 0 || indexDays === 0">
          {{ getLabel(indexDays, indexHours) }}
        </div>
        <template v-else>
          <template
            v-for="(fifteenMinTime, index) in getFifteenMinSplit(hours)"
          >
            <el-popover
              :key="index"
              v-if="getException(fifteenMinTime, days)"
              placement="right"
              :title="getSchedule(fifteenMinTime, days).date"
              width="200"
              trigger="hover"
              class="fifteenMinCell"
              :open-delay="500"
              :close-delay="500"
            >
              <div class="f13">
                <div>
                  {{
                    !getException(fifteenMinTime, days).offSchedule
                      ? 'Extended Hours'
                      : 'Reduced Hours'
                  }},
                </div>
                <div class="font-bold">
                  {{ getSchedule(fifteenMinTime, days).startTime }} -
                  {{ getSchedule(fifteenMinTime, days).endTime }}
                </div>
                <div class="d-flex flex-column pT8">
                  <div
                    class="fc-link fc-link-animation f14 pointer mT3"
                    @click="editException(fifteenMinTime, days)"
                  >
                    {{ $t('common._common.edit') }}
                  </div>
                  <span class="separator">|</span>
                  <div
                    class="fc-link fc-link-animation f14 pointer mT3"
                    @click="
                      deleteException(
                        getException(fifteenMinTime, days).id,
                        fifteenMinTime,
                        days
                      )
                    "
                  >
                    {{ $t('common._common.delete') }}
                  </div>
                </div>
              </div>
              <template slot="reference">
                <div
                  :class="[
                    'fifteenMinCell pointer',
                    getScheduleClass(fifteenMinTime, days),
                  ]"
                ></div>
              </template>
            </el-popover>
            <div
              v-else
              :key="index"
              :class="[
                'fifteenMinCell',
                getScheduleClass(fifteenMinTime, days),
              ]"
            ></div>
          </template>
        </template>
      </div>
    </div>
  </div>
</template>

<script>
import helpers from 'src/util/helpers.js'
import { isEmpty } from '@facilio/utils/validation'
import cloneDeep from 'lodash/cloneDeep'
import {
  getFifteenMinSplit,
  get15SplitFor24Hour,
  // getOneHourSplitString,
} from './ControlScheduleUtil'
import { getHour } from './ControlScheduleUtil'

const { getOrgMoment: moment } = helpers
const DAYS_IN_WEEK_FORMATTED = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat']
const DAYS = {
  SUNDAY: { value: 7 },
  MONDAY: { value: 1 },
  TUESDAY: { value: 2 },
  WEDNESDAY: { value: 3 },
  THURSDAY: { value: 4 },
  FRIDAY: { value: 5 },
  SATURDAY: { value: 6 },
}
const SCHEDULE_TYPE_ENUM = {
  SCHEDULE: 'actualSchedule',
  EXTENDED_HOURS: 'extendedHours',
  REDUCED_HOURS: 'reducedHours',
}

export default {
  props: ['scheduleDays', 'isSameTimingForAll', 'schedule', 'cellHeight'],
  data() {
    return {
      startHour: 0,
      endHour: 23,
      scheduleLookup: {
        SUNDAY: { value: 7 },
        MONDAY: { value: 1 },
        TUESDAY: { value: 2 },
        WEDNESDAY: { value: 3 },
        THURSDAY: { value: 4 },
        FRIDAY: { value: 5 },
        SATURDAY: { value: 6 },
      },
    }
  },
  computed: {
    getTimeInterval() {
      let startTime = moment(this.startHour, 'HH:mm')
      let endTime = moment(this.endHour, 'HH:mm')

      if (endTime.isBefore(startTime)) {
        endTime.add(1, 'day')
      }

      let timeStops = []
      timeStops.push(null)

      while (startTime <= endTime) {
        timeStops.push(new moment(startTime).format('hh:mm A'))
        startTime.add(1, 'hours')
      }
      return timeStops
    },
    cellDimension() {
      return `height: ${this.cellHeight || '40px'}`
    },
    xAxis() {
      return ['', ...Object.keys(this.scheduleLookup)]
    },
  },
  watch: {
    schedule: {
      handler: function(newVal) {
        if (!isEmpty(newVal)) this.serializeSchedule()
        else this.scheduleLookup = cloneDeep(DAYS)
      },
      deep: true,
      immediate: true,
    },
  },
  methods: {
    getFifteenMinSplit: hours => getFifteenMinSplit(hours),
    serializeSchedule() {
      let { schedule } = this
      this.scheduleLookup = cloneDeep(DAYS)
      schedule.forEach(currSchedule => {
        let {
          dayOfWeekEnum,
          startTime,
          endTime,
          exception,
          offSchedule,
          startTimeMilli,
        } = currSchedule

        let currDay = Object.keys(this.scheduleLookup).find(
          week => week === dayOfWeekEnum
        )

        get15SplitFor24Hour(
          getHour(startTime, 'HH:mm'),
          getHour(endTime, 'HH:mm'),
          true
        ).forEach(hour => {
          this.scheduleLookup[currDay][hour] = {
            offSchedule: !isEmpty(exception) ? offSchedule : false,
            dayOfWeekEnum,
            scheduleType: this.getScheduleType(exception, offSchedule),
            isException: !isEmpty(exception),
            exception,
            date: this.getDate(startTimeMilli),
            startTime: getHour(startTime, 'hh:mm a'),
            endTime: getHour(endTime, 'hh:mm a'),
            startTimeMilli,
            ...currSchedule,
          }
        })
      })
      this.$forceUpdate()
    },
    getLabel(indexDays, indexHours) {
      if (indexDays === 0 && indexHours === 0) {
        return `GMT${moment().format('Z')}`
      } else if (indexDays === 0) {
        return this.getTimeInterval[indexHours] === '12:00 AM'
          ? ''
          : this.getTimeInterval[indexHours]
      } else if (indexHours === 0) {
        return DAYS_IN_WEEK_FORMATTED[indexDays - 1]
      } else {
        return ''
      }
    },
    getScheduleType(exception, offSchedule) {
      return isEmpty(exception)
        ? SCHEDULE_TYPE_ENUM['SCHEDULE']
        : !offSchedule
        ? SCHEDULE_TYPE_ENUM['EXTENDED_HOURS']
        : SCHEDULE_TYPE_ENUM['REDUCED_HOURS']
    },
    getScheduleClass(time, days) {
      if (
        !isEmpty(this.scheduleLookup) &&
        !isEmpty(this.scheduleLookup[days][time])
      ) {
        let { scheduleType } = this.scheduleLookup[days][time]
        return `${scheduleType} ${time}`
      } else {
        return 'noSchedule'
      }
    },
    getException(time, days) {
      let scheduleObj = this.getSchedule(time, days)
      if (!isEmpty(scheduleObj)) {
        let { exception } = scheduleObj
        return exception
      }
    },
    getSchedule(time, days) {
      if (
        !isEmpty(this.scheduleLookup) &&
        !isEmpty(this.scheduleLookup[days][time])
      ) {
        return this.scheduleLookup[days][time]
      }
    },
    getCellClass(day, hour) {
      if (day === 0 && hour == 0) return 'schedule-container hour-header-cell'
      else if (day === 0) return 'hour-axis-cell'
      else return 'schedule-container cell'
    },
    getDate(time) {
      return moment(time).format('ddd - DD MMM')
    },
    deleteException(id, hour, day) {
      this.$emit('deleteException', {
        id,
        slot: this.scheduleLookup[day][hour],
      })
    },

    editException(hour, day) {
      this.$emit('editException', this.scheduleLookup[day][hour])
    },
  },
}
</script>

<style scoped>
.schedule-container {
  background-color: #ffffff;
  height: 40px;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #324056;
  font-size: 11px;
  text-transform: uppercase;
  font-weight: 500;
  border-left: 1px solid #eceef4;
  border-top: 1px solid #eceef4;
}
.actualSchedule {
  background-color: #97ce9e;
  border-bottom: 11px solid #97ce9e;
}
.extendedHours {
  background-color: #fad94d;
  border-bottom: 11px solid #fad94d;
}
.reducedHours {
  background-color: #eceffb;
  border-bottom: 11px solid #eceffb;
}
.no-schedule {
  background-color: #ffffff;
}
.schedule-visualizer-container {
  overflow: scroll;
}
.bgWhite {
  background-color: white;
}
.hour-axis-cell,
.hour-header-cell {
  width: 115px;
  display: flex;
  align-items: center;
  color: #324056;
  font-size: 11px;
  text-transform: uppercase;
  font-weight: 500;
  background-color: #ffffff;
}
.hour-header-cell {
  border-left: none;
}
.hour-axis-cell {
  padding-bottom: 38px;

  padding-left: 25px;
  justify-content: space-between;
}
.hour-axis-cell::after {
  content: ' ';
  height: 1px;
  width: 20px;
  margin-top: -1px;
  display: inline-block;
  zoom: 1;
  background-color: #eceef4;
  margin-bottom: 1px;
}
.zindex999 {
  z-index: 999;
}
.cell {
  flex-grow: 1;
  flex-shrink: 1;
  flex-basis: 0;
  flex-direction: column;
}
.position-sticky {
  position: sticky;
  top: 0;
}
.fifteenMinCell {
  flex-grow: 1;
  width: 100%;
  flex-basis: 0;
}
</style>
