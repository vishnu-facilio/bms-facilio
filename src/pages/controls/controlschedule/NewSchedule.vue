<template>
  <div class="flex flex-row pL5">
    <div class="schedule-form-container new-body-modal">
      <div class="section-header f20 pL30">
        {{
          isEdit
            ? $t('common.header.edit_schedule')
            : $t('common.products.new_schedule')
        }}
      </div>
      <div v-if="isLoading" class="flex-middle fc-empty-white">
        <spinner :show="isLoading" size="80"></spinner>
      </div>
      <div v-else class="pT30">
        <div class="pR30 pL30">
          <el-form
            :model="scheduleObj"
            :rules="rules"
            ref="form"
            label-position="left"
            label-width="150px"
          >
            <el-form-item
              prop="name"
              :label="$t('common.header.schedule_name')"
              class="mB25"
            >
              <el-input
                class="fc-input-full-border-select2 field-input width75"
                v-model="scheduleObj.name"
                :placeholder="$t('common._common.enter_schedule_name')"
              ></el-input>
            </el-form-item>
            <el-form-item
              prop="event"
              :label="$t('common._common.schedule_event')"
              class="mB25"
            >
              <el-select
                class="fc-input-full-border-select2 field-input width75"
                v-model="scheduleObj.event"
                :placeholder="$t('common.products.select_a_schedule_event')"
                @change="changeScheduleFormat"
              >
                <el-option
                  :label="$t('common._common.same_timing_all_day')"
                  value="SAME_TIMING_ALLDAY"
                ></el-option>
                <el-option
                  :label="$t('common._common.different_timing_all_day')"
                  value="DIFFERENT_TIMING_ALLDAY"
                ></el-option>
              </el-select>
            </el-form-item>
            <el-form-item
              prop="mode"
              :label="$t('common.products.command_only_for_schedule_change')"
              label-width="280px"
              class="mB25"
            >
              <el-radio-group v-model="scheduleObj.mode" class="mT10">
                <el-radio class="fc-radio-btn" :label="1">{{
                  $t('common.products.yes')
                }}</el-radio>
                <el-radio class="fc-radio-btn" :label="null">{{
                  $t('common.products.no')
                }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-form>
        </div>
        <div class="pL25 pR30">
          <el-table :data="scheduleDays" style="width: 100%">
            <el-table-column prop="label" :label="$t('common.date_picker.day')">
              <template v-slot="day">
                <el-checkbox v-model="day.row.isChecked">{{
                  day.row.label
                }}</el-checkbox>
              </template>
            </el-table-column>
            <el-table-column :label="$t('common.wo_report.from_time')">
              <template v-slot="day">
                <div
                  v-for="(hour, index) in day.row.hours"
                  :key="`${hour}-${index}`"
                  class="flex flex-row pB8 pR8"
                >
                  <el-select v-model="hour.from">
                    <el-option
                      v-for="(interval, intervalIndex) in getTimeInterval"
                      :key="`${interval}-${intervalIndex}`"
                      :value="interval"
                      :label="interval"
                      :disabled="
                        isOptionsDisabled(day.row.hours, interval, index, {
                          isTo: false,
                        })
                      "
                    >
                    </el-option>
                  </el-select>
                </div>
              </template>
            </el-table-column>
            <el-table-column :label="$t('common._common.to_time')">
              <template v-slot="day">
                <div
                  v-for="(hour, index) in day.row.hours"
                  :key="`${hour}-${index}`"
                  class="flex flex-row pB8 pR8"
                >
                  <el-select v-model="hour.to">
                    <el-option
                      v-for="(interval, intervalIndex) in getTimeInterval"
                      :key="`${interval}-${intervalIndex}`"
                      :value="interval"
                      :label="interval"
                      :disabled="
                        isOptionsDisabled(day.row.hours, interval, index, {
                          isTo: true,
                        })
                      "
                    >
                    </el-option>
                  </el-select>
                </div>
              </template>
            </el-table-column>
            <el-table-column
              :label="$t('common.products.split_day')"
              align="center"
            >
              <template v-slot="day">
                <img
                  src="~assets/add-icon.svg"
                  class="delete-icon pointer schedule-split-icon"
                  @click="addSplitSchedule(day.row)"
                />

                <img
                  src="~assets/remove-icon.svg"
                  v-if="$getProperty(day, 'row.hours.length') > 1"
                  class="delete-icon pointer schedule-split-icon mL10"
                  @click="deleteSplitSchedule(day.row)"
                />
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>

      <div class="flex-grow flex-shrink white-background"></div>

      <div class="schedule-btn-footer">
        <el-button
          class="modal-btn-cancel text-uppercase"
          @click="redirectToList()"
          >{{ $t('common._common.cancel') }}</el-button
        >
        <el-button class="modal-btn-save mL0" @click="save" :loading="saving">
          {{
            saving ? $t('common._common._saving') : $t('common._common._save')
          }}
        </el-button>
      </div>
    </div>
    <ScheduleVisualizer
      :schedule="slots"
      :isSameTimingForAll="isSameTimingForAll"
      class="visualizer-container"
    />
  </div>
</template>

<script>
import ScheduleVisualizer from './components/ScheduleVisualizer'

import helpers from 'src/util/helpers.js'
import cloneDeep from 'lodash/cloneDeep'
import clone from 'lodash/clone'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'

const { getOrgMoment: moment } = helpers
const DAYS_IN_WEEK = [
  'Sunday',
  'Monday',
  'Tuesday',
  'Wednesday',
  'Thursday',
  'Friday',
  'Saturday',
]
const DEFAULT_SCHEDULE = [
  {
    label: 'Sunday',
    isChecked: false,
    hours: [],
    dayOfWeek: 7,
  },
  {
    label: 'Monday',
    isChecked: false,
    hours: [],
    dayOfWeek: 1,
  },
  {
    label: 'Tuesday',
    isChecked: false,
    hours: [],
    dayOfWeek: 2,
  },
  {
    label: 'Wednesday',
    isChecked: false,
    hours: [],
    dayOfWeek: 3,
  },
  {
    label: 'Thursday',
    isChecked: false,
    hours: [],
    dayOfWeek: 4,
  },
  {
    label: 'Friday',
    isChecked: false,
    hours: [],
    dayOfWeek: 5,
  },
  {
    label: 'Saturday',
    isChecked: false,
    hours: [],
    dayOfWeek: 6,
  },
]
export default {
  props: ['id'],
  components: { ScheduleVisualizer },
  data() {
    return {
      scheduleObj: {
        name: '',
        event: '',
        mode: null,
      },
      rules: {
        name: [
          {
            required: true,
            message: this.$t('common.header.schedule_is_required'),
            trigger: 'blur',
          },
        ],
        event: [
          {
            required: true,
            message: this.$t('common.products.select_a_schedule_event_type'),
            trigger: 'change',
          },
        ],
      },
      saving: false,
      startHour: 0,
      endHour: 24,
      scheduleDays: [],
      isSameTimingForAll: false,
      isLoading: false,
      currBusinessHourId: null,
      slots: [],
    }
  },
  created() {
    this.scheduleDays = cloneDeep(DEFAULT_SCHEDULE)
    this.deserialize()
  },
  computed: {
    getTimeInterval() {
      let startTime = moment(this.startHour, 'HH:mm')
      let endTime = moment(this.endHour, 'HH:mm')

      if (endTime.isBefore(startTime)) {
        endTime.add(1, 'day')
      }

      let timeStops = []

      while (startTime <= endTime) {
        timeStops.push(new moment(startTime).format('hh:mm A'))
        startTime.add(1, 'hours')
      }
      return timeStops
    },
    defaultBusinessHour() {
      return { from: '9:00 AM', to: '6:00 PM' }
    },
    isEdit() {
      let { id } = this
      return !isEmpty(id)
    },
    moduleName() {
      return 'controlSchedule'
    },
  },
  watch: {
    scheduleDays: {
      handler(newVal) {
        let visualizerSchedule = []
        if (this.isSameTimingForAll) {
          DAYS_IN_WEEK.forEach(val => {
            let schedule = newVal[0]
            let { hours } = schedule
            hours.forEach(currHour => {
              let { from, to } = currHour
              let dayOfWeekEnum = val.toUpperCase()
              visualizerSchedule.push({
                dayOfWeekEnum,
                startTime: from,
                endTime: to,
              })
            })
          })
        } else {
          newVal.forEach(schedule => {
            let { hours, label, isChecked } = schedule
            if (isChecked) {
              hours.forEach(currHour => {
                let { from, to } = currHour
                let dayOfWeekEnum = label.toUpperCase()
                visualizerSchedule.push({
                  dayOfWeekEnum,
                  startTime: from,
                  endTime: to,
                })
              })
            }
          })
        }

        this.slots = visualizerSchedule
      },
      immediate: true,
      deep: true,
    },
  },
  methods: {
    async deserialize() {
      if (this.isEdit) {
        this.isLoading = true
        let {
          controlSchedule,
          error,
        } = await API.fetchRecord('controlSchedule', { id: this.id })
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          let { name, mode } = controlSchedule
          this.scheduleObj.mode = mode
          this.scheduleObj.name = name
          let { businessHoursContext: currBusinessHour } = controlSchedule
          let {
            customHourTypeEnum,
            singleDaybusinessHoursList,
            id: currBusinessHourId,
          } = currBusinessHour

          this.currBusinessHourId = currBusinessHourId
          this.scheduleObj.event = customHourTypeEnum
          if (customHourTypeEnum === 'SAME_TIMING_ALLDAY') {
            this.isSameTimingForAll = true
            this.scheduleDays = [
              {
                label: 'All Day',
                isChecked: true,
                hours: [],
              },
            ]
          }
          this.serializeBusinessHours(singleDaybusinessHoursList)
        }
      } else {
        this.scheduleObj.event = 'SAME_TIMING_ALLDAY'
        this.changeScheduleFormat('SAME_TIMING_ALLDAY')
      }
      this.scheduleDays.forEach(schedule => {
        if (isEmpty(schedule.hours))
          schedule.hours.push(clone(this.defaultBusinessHour))
      })
      this.isLoading = false
    },
    serializeBusinessHours(businessHours) {
      businessHours.forEach(currDaySchedule => {
        let { dayOfWeekEnum, startTime, endTime } = currDaySchedule
        let oldSchedule = this.scheduleDays.find(day => {
          if (this.isSameTimingForAll) {
            return 'MONDAY' === dayOfWeekEnum
          } else {
            return day.label.toUpperCase() === dayOfWeekEnum
          }
        })
        if (!isEmpty(oldSchedule)) {
          oldSchedule.hours.push({
            from: this.getHourString(startTime),
            to: this.getHourString(endTime),
          })
          oldSchedule.isChecked = true
        }
      })
    },
    addSplitSchedule(day) {
      let scheduledHours = day.hours
      let { to: endTime } = scheduledHours[scheduledHours.length - 1]
      let formattedEndTime = this.getHour(endTime)
      if (formattedEndTime !== 0 && formattedEndTime < this.endHour) {
        let newEndTime = formattedEndTime
        day.hours.push({
          from: this.getHourString(newEndTime),
          to: this.getHourString(this.endHour),
        })
      } else {
        this.$message.error(
          this.$t('common.dashboard.maximum_time_limit_day_reached')
        )
      }
    },
    deleteSplitSchedule(day) {
      day.hours.pop()
    },
    getHour(timeString) {
      let hour = moment(timeString, 'hh:mm A').format('H')
      return parseInt(hour)
    },
    getHourString(time) {
      return moment(time, 'HH:mm').format('hh:mm A')
    },
    isOptionsDisabled(currHour, currTimeInterval, index, { isTo }) {
      if (isTo || index > 0) {
        let { from } = currHour[index]
        let formattedFrom = this.getHour(from)
        let formattedInterval = this.getHour(currTimeInterval)
        if (isTo) {
          formattedFrom = formattedFrom - 1
        }

        if (
          formattedFrom > formattedInterval ||
          formattedFrom === formattedInterval
        ) {
          return true
        } else {
          return false
        }
      } else {
        return false
      }
    },
    changeScheduleFormat(eventType) {
      if (eventType === 'SAME_TIMING_ALLDAY') {
        this.scheduleDays = [
          {
            label: 'All Day',
            isChecked: true,
            hours: [{ from: '9:00 AM', to: '6:00 PM' }],
          },
        ]
        this.isSameTimingForAll = true
      } else {
        this.scheduleDays = clone(DEFAULT_SCHEDULE)
        this.scheduleDays.forEach(schedule => {
          if (isEmpty(schedule.hours))
            schedule.hours.push({ from: '09:00 AM', to: '06:00 PM' })
        })
        this.isSameTimingForAll = false
      }
    },
    async save() {
      let data = this.serialize()
      let { id } = this
      let currId

      this.$refs['form'].validate(async valid => {
        if (valid) {
          this.saving = true
          if (this.isEdit) {
            let { error } = await API.updateRecord('controlSchedule', {
              id,
              data,
            })
            if (!isEmpty(error)) {
              this.$message.error(error.message || 'Error Occured')
            } else {
              currId = id

              this.$message.success(
                this.$t('common.products.schedule_edited_successfully')
              )
            }
          } else {
            let { controlSchedule, error } = await API.createRecord(
              'controlSchedule',
              {
                data: data,
              }
            )
            if (!isEmpty(error)) {
              this.$message.error(error.message || 'Error Occured')
            } else {
              currId = this.$getProperty(controlSchedule, 'id')
              this.$message.success(
                this.$t('common.products.schedule_created_successfully')
              )
            }
          }
          if (currId) {
            let { moduleName } = this
            if (isWebTabsEnabled()) {
              let { name } =
                findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}
              let route = {
                name,
                params: {
                  viewname: 'all',
                  id: currId,
                },
              }
              this.$router.push(route)
            } else {
              this.$router.push({
                name: 'schedule-summary',
                params: {
                  id: currId,
                  moduleName,
                  viewname: 'all',
                },
                query: {
                  ...this.$route.query,
                },
              })
            }
          }
          this.saving = false
        }
      })
    },
    serialize() {
      let businessHours = []
      if (!this.isSameTimingForAll) {
        this.scheduleDays.forEach(schedule => {
          let { hours, isChecked, dayOfWeek } = schedule
          if (!isEmpty(hours) && isChecked) {
            hours.forEach(hour => {
              let { from, to } = hour
              if (isChecked) {
                let perDaySchedule = {
                  dayOfWeek,
                  startTime: this.getHour24(from),
                  endTime: this.getHour24(to),
                }
                businessHours.push(perDaySchedule)
              }
            })
          }
        })
      } else {
        DEFAULT_SCHEDULE.forEach(schedule => {
          let { hours, isChecked } = this.scheduleDays[0]
          let { dayOfWeek } = schedule
          if (!isEmpty(hours) && isChecked) {
            hours.forEach(hour => {
              let { from, to } = hour
              if (isChecked) {
                let perDaySchedule = {
                  dayOfWeek,
                  startTime: this.getHour24(from),
                  endTime: this.getHour24(to),
                }
                businessHours.push(perDaySchedule)
              }
            })
          }
        })
      }
      let params = {
        name: this.scheduleObj.name,
        mode: this.scheduleObj.mode,
        businessHoursContext: {
          name: this.scheduleObj.name,
          businessHourType: 'CUSTOM',
          customHourType: this.scheduleObj.event,
          id: this.currBusinessHourId,
          singleDaybusinessHoursList: businessHours,
        },
      }
      return params
    },

    getHour24(timeString) {
      let hour = moment(timeString, 'hh:mm A').format('HH:mm')
      return hour
    },
    redirectToList() {
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(this.moduleName, pageTypes.LIST)
        name && this.$router.push({ name, params: { viewname: 'all' } })
      } else {
        this.$router.push({
          name: 'schedule-list',
          params: { viewname: 'all' },
        })
      }
    },
  },
}
</script>

<style lang="scss" scoped>
.schedule-form-container,
.visualizer-container {
  height: calc(100vh - 70px);
}
.schedule-form-container {
  width: 40%;
  max-width: 1000px;
  margin: 5px;
  background-color: white;
  padding: 28px 0px 0px;
  display: flex;
  flex-direction: column;
}
.width75 {
  width: 75%;
}
.schedule-btn-footer {
  bottom: 0px;
  display: flex;
}
.schedule-split-icon {
  height: 18px;
  width: 18px;
}
.visualizer-container {
  flex-grow: 1;
  padding: 10px 20px 0px;
}
.field-input {
  width: auto;
  flex-grow: 1;
}
.field-label {
  width: 120px;
}
</style>
<style lang="scss">
.schedule-form-container {
  .el-table td {
    border-bottom: solid 1px white;
    padding-left: 0px;
    padding-right: 0px;
  }

  .el-table th.is-leaf {
    border-bottom: solid 1px #dee7ef;
    border-top: solid 1px #dee7ef;
    padding-left: 0px;
    padding-right: 0px;
  }
  .el-table .cell {
    padding-left: 0px;
    padding-right: 0px;
  }
  .el-table th > .cell {
    padding-left: 0px;
    padding-right: 0px;
  }
}
</style>
