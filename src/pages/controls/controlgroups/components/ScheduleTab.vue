<template>
  <div>
    <div v-if="isLoading" class="mL10 mT70">
      <spinner :show="isLoading" size="80"></spinner>
    </div>
    <div v-else-if="currBusinessHour">
      <div class="d-flex justify-center flex-col">
        <div class="d-flex justify-between legend-box-schedule">
          <div v-if="scheduleLoading" class="mL10">
            <spinner :show="scheduleLoading" size="40"></spinner>
          </div>
          <div v-else class="font-medium mL10">{{ currentWeek }}</div>
          <div class="text-fc-pink font-medium f16">
            <new-date-picker
              :zone="$timezone"
              class="filter-field date-filter-comp"
              :dateObj.sync="dateObj"
              @date="changeDateFilter"
              :hidePopover="true"
              :isDateFixed="false"
            ></new-date-picker>
          </div>
        </div>
        <ScheduleVisualizer
          v-if="!$validation.isEmpty(currBusinessHour)"
          :schedule="currBusinessHour"
          :isSameTimingForAll="false"
          @deleteException="deleteException"
          @editException="editException"
        />
        <div class="legend-box-schedule pT20 pB20 d-flex">
          <div class="green-dot legend-dot"></div>
          {{ $t('common.operational_analytics.scheduled_hours') }}
          <template v-if="showHourExtension">
            <div class="schedule-extension-dot legend-dot"></div>
            {{ $t('common._common.hours_extension') }}
          </template>
          <template v-if="showHourReduction">
            <div class="schedule-reduction-dot legend-dot"></div>
            {{ $t('common._common.hours_reduction') }}
          </template>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import NewDatePicker from '@/NewDatePicker'
import ScheduleVisualizer from 'pages/controls/controlschedule/components/ScheduleVisualizer'
import NewDateHelper from 'src/components/mixins/NewDateHelper'
import clone from 'lodash/clone'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import helpers from 'src/util/helpers.js'

const { getOrgMoment: moment } = helpers

export default {
  components: { ScheduleVisualizer, NewDatePicker },
  props: ['group', 'isLoading', 'moduleName'],
  data() {
    return {
      schedule: null,
      currBusinessHour: null,
      dateObj: clone(NewDateHelper.getDatePickerObject(31, null)),
      startWeek: null,
      endWeek: null,
      showHourReduction: false,
      showHourExtension: false,
      scheduleLoading: false,
    }
  },
  computed: {
    currentWeek() {
      let { startWeek, endWeek } = this
      if (startWeek && endWeek) {
        let start = moment(startWeek).format('DD, MMM-YYYY')
        let end = moment(endWeek).format('DD, MMM-YYYY')
        return `${start} - ${end}`
      } else {
        let currentDate = moment()
        let start = currentDate.clone().startOf('isoWeek')

        let weekEnd = moment(start)
          .add(5, 'days')
          .format('DD, MMM-YYYY')
        let weekStart = moment(start).format('DD, MMM-YYYY')
        return `${weekStart} - ${weekEnd}`
      }
    },
  },
  watch: {
    group: {
      handler() {
        this.init()
      },
      immediate: true,
    },
  },
  methods: {
    init() {
      this.loadData()
      let {
        dateObj: { value },
        group,
      } = this
      if (!isEmpty(group)) this.loadSlots(value)
    },
    loadData() {
      if (!isEmpty(this.group)) {
        let {
          group: { controlSchedule },
        } = this
        this.schedule = controlSchedule
      }
    },
    changeDateFilter(dateFilter) {
      let { value } = dateFilter
      this.startWeek = value[0]
      this.endWeek = value[1]
      this.loadSlots(value)
      this.dateObj = dateFilter
    },
    async loadSlots(value) {
      this.scheduleLoading = true
      let endTime = moment(value[1]).format('x')
      let {
        group: { id },
      } = this
      let param = {
        group: {
          id,
        },
        startTime: value[0],
        endTime: parseInt(endTime),
        moduleName: this.moduleName,
      }

      let { data, error } = await API.post('/v3/control/getSlot', param)
      if (!error) {
        let { slots } = data
        let schedule = slots.map(currSlot => {
          let { startTime, endTime, exception, offSchedule } = currSlot

          if (!isEmpty(exception)) {
            if (!offSchedule) this.showHourExtension = true
            else this.showHourReduction = true
          }

          let start = this.getHour(startTime)
          let end = this.getHour(endTime)
          let dayOfWeekEnum = moment(startTime).format('dddd')
          let startTimeMilli = startTime

          return {
            ...currSlot,
            startTime: start,
            endTime: end,
            dayOfWeekEnum: dayOfWeekEnum.toUpperCase(),
            startTimeMilli,
          }
        })
        this.currBusinessHour = schedule
        this.scheduleLoading = false
      }
    },
    getHour(time) {
      return moment(time).format('HH:mm')
    },
    editException(slot) {
      this.$emit('editException', slot)
    },
    deleteException(dataObj) {
      this.$emit('deleteException', dataObj)
    },
  },
}
</script>

<style scoped>
.green-dot {
  background-color: #97ce9e;
}
.legend-dot {
  width: 10px;
  height: 10px;
  border-radius: 5px;
  margin-right: 10px;
  margin-left: 10px;
}
.schedule-extension-dot {
  background-color: #fad94d;
}

.schedule-reduction-dot {
  background-color: #d0d0d2;
}

.legend-box-schedule {
  display: flex;
  padding: 5px;
  background-color: #ffffff;
  align-items: center;
}
</style>
