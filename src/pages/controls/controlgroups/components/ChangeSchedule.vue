<template>
  <el-dialog
    title="Schedule Change"
    :visible="true"
    width="618px"
    :append-to-body="true"
    style="z-index: 9999999999;"
    class="agents-dialog fc-dialog-center-container change-schedule-container"
    :before-close="() => closeDialog()"
    :show-close="false"
  >
    <div v-if="isEdit && showLoading" class="p20">
      <spinner :show="showLoading" size="80"></spinner>
    </div>
    <el-form
      v-else
      ref="form"
      :model="schedule"
      :rules="rules"
      label-width="120px"
      label-position="left"
      class="pR30 pL30 pB30"
    >
      <el-form-item label="Group" v-if="isGroupEmpty" prop="selectedGroup">
        <FLookupField
          class="flookup-field-groups width100"
          :model.sync="(schedule.selectedGroup || {}).label"
          :hideDropDown="true"
          :fetchOptionsOnLoad="true"
          :canShowLookupWizard="showLookupFieldWizard"
          :field="fieldObj"
          @showLookupWizard="showLookupWizard"
          @setLookupFieldValue="setLookupFieldValue"
        ></FLookupField>
      </el-form-item>
      <el-form-item label="Name" v-if="!isEdit" prop="name">
        <el-input
          v-model="schedule.name"
          placeholder="Enter a name for schedule"
          class="el-input-textbox-full-border"
        ></el-input>
      </el-form-item>
      <el-form-item label="Frequency" v-if="!isEdit">
        <el-radio-group v-model="schedule.isDay" @change="clearValidation">
          <el-radio :label="true" class="fc-radio-btn mR10">Daily</el-radio>
          <el-radio :label="false" class="fc-radio-btn">Specific Date</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="Apply For" v-if="isEdit">
        <el-radio-group
          :disabled="schedule.isEdited || !schedule.isDay || isEditTypeDisabled"
          v-model="schedule.isThisEvent"
        >
          <el-radio :label="true" class="fc-radio-btn mR10"
            >This Event</el-radio
          >
          <el-radio :label="false" class="fc-radio-btn"
            >Recurring Event</el-radio
          >
        </el-radio-group>
      </el-form-item>

      <template v-if="isEdit ? !schedule.isThisEvent : schedule.isDay">
        <el-form-item label="Select Days" prop="selectedDays">
          <el-checkbox
            @change="checkAllDay"
            v-model="allDays"
            label="All days"
          ></el-checkbox>
          <el-checkbox-group v-model="schedule.selectedDays">
            <el-checkbox
              v-for="(day, index) in daysInWeek"
              :key="index"
              :label="day"
            ></el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </template>
      <el-form-item label="Date" v-if="!schedule.isDay" prop="date">
        <FDatePicker
          v-model="schedule.date"
          :type="'date'"
          class="fc-input-full-border2 form-date-picker"
          placeholder="Select Date"
          :pickerOptions="pickerOptions"
        ></FDatePicker>
      </el-form-item>
      <div class="flex flex-row">
        <el-form-item label="Time" prop="startTime">
          <el-select
            class="fc-input-full-border-select2 mR10"
            v-model="schedule.startTime"
            placeholder="From time"
          >
            <el-option
              v-for="interval in getTimeInterval(startHour, endHour)"
              :key="interval"
              :value="interval"
              :label="interval"
            ></el-option>
          </el-select>
        </el-form-item>
        <span class="mT10">-</span>
        <el-form-item prop="endTime" label-width="0px">
          <el-select
            class="fc-input-full-border-select2 mL10"
            v-model="schedule.endTime"
            placeholder="To time"
          >
            <el-option
              v-for="interval in getTimeInterval(startHour, endHour)"
              :key="interval"
              :value="interval"
              :label="interval"
              :disabled="isOptionsDisabled(interval)"
            ></el-option>
          </el-select>
        </el-form-item>
      </div>
      <el-form-item label="Select Type">
        <el-radio-group v-model="schedule.isScheduleExtension">
          <el-radio :label="false" class="fc-radio-btn mR10"
            >Extend Hours</el-radio
          >
          <el-radio :label="true" class="fc-radio-btn">Reduce Hours</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <div class="schedule-btn-footer">
      <el-button
        class="modal-btn-cancel text-uppercase"
        @click="() => closeDialog()"
        >{{ $t('common._common.cancel') }}</el-button
      >
      <el-button class="modal-btn-save mL0" @click="save" :loading="saving">
        {{ saving ? $t('common._common._saving') : $t('common._common._save') }}
      </el-button>
    </div>
    <FLookupFieldWizard
      :canShowLookupWizard.sync="showLookupFieldWizard"
      :selectedLookupField="fieldObj"
      :withReadings="true"
      @setLookupFieldValue="setLookupFieldValue"
    ></FLookupFieldWizard>
  </el-dialog>
</template>

<script>
import FDatePicker from 'pages/assets/overview/FDatePicker'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import helpers from 'src/util/helpers.js'
import { getHour } from '../../controlschedule/components/ControlScheduleUtil'
import FLookupFieldWizard from '@/FLookupFieldWizard'
import FLookupField from '@/forms/FLookupField'

const { getOrgMoment: moment } = helpers

export default {
  props: [
    'closeDialog',
    'group',
    'moduleName',
    'tenantId',
    'isTenantGroup',
    'record',
    'isEditTypeDisabled',
  ],
  components: { FDatePicker, FLookupFieldWizard, FLookupField },
  data() {
    return {
      startHour: 0,
      endHour: 23,
      daysInWeek: ['Mon', 'Tue', 'Wed', 'Thurs', 'Fri', 'Sat', 'Sun'],
      showLoading: false,
      allDays: false,
      saving: false,
      currentGroup: null,
      showLookupFieldWizard: false,
      fieldObj: {
        isDataLoading: false,
        options: [],
        lookupModuleName: 'controlGroupv2TenantSharing',
        field: {
          lookupModule: {
            name: 'controlGroupv2TenantSharing',
            displayName: 'Control Group',
          },
        },
        forceFetchAlways: true,
        filters: {},
        placeHolderText: 'Select Group',
        isDisabled: false,
      },
      schedule: {
        selectedGroup: null,
        isDay: true,
        isScheduleExtension: true,
        selectedDays: [],
        startTime: '',
        endTime: '',
        date: null,
        name: '',
        isThisEvent: true,
        isEdited: false,
      },
      rules: {
        selectedGroup: [
          {
            required: true,
            message: 'Please select a group',
            trigger: 'change',
          },
        ],
        name: [
          {
            required: true,
            message: 'Please enter a name for the schedule change',
            trigger: 'blur',
          },
        ],
        selectedDays: [
          {
            required: true,
            message:
              'Please select days for which you want the schedule changes to be applied',
            trigger: 'change',
          },
        ],
        date: [
          {
            required: true,
            message: 'Please select date',
            trigger: 'change',
          },
        ],
        startTime: [
          {
            required: true,
            message: 'Please select a start Time',
            trigger: 'change',
          },
        ],
        endTime: [
          {
            required: true,
            message: 'Please select a end Time',
            trigger: 'change',
          },
        ],
      },
    }
  },
  computed: {
    isEdit() {
      return !isEmpty(this.record)
    },
    pickerOptions() {
      let todayDate = moment()
        .startOf('date')
        .valueOf()

      return {
        disabledDate(time) {
          return time.getTime() < todayDate
        },
      }
    },
    isGroupEmpty() {
      let { group } = this
      return isEmpty(group)
    },
    getModuleDetails() {
      let {
        record,
        schedule: { isThisEvent },
        moduleName,
      } = this
      let { edited, id, exception } = record || {}
      let { startSchedule, id: recordId } = exception || {}
      let moduleDetails = {}

      if (edited) {
        moduleDetails = { moduleName: 'controlScheduleSlots', id }
      } else if (isEmpty(startSchedule) || !isThisEvent) {
        moduleDetails = { moduleName: moduleName, id: recordId }
      } else {
        moduleDetails = { moduleName: 'controlScheduleSlots', id }
      }
      return moduleDetails
    },
  },
  created() {
    this.deserialize()
  },
  methods: {
    showLookupWizard(field, canShow) {
      this.selectedLookupField = field
      this.showLookupFieldWizard = canShow
    },
    setLookupFieldValue(value) {
      let { field } = value
      let { selectedItems } = field
      this.schedule.selectedGroup = selectedItems[0]
      this.showLookupFieldWizard = false
      this.fetchGroupDetails()
    },
    async fetchGroupDetails() {
      let {
        schedule: { selectedGroup },
      } = this
      let { value } = selectedGroup || {}
      let groupModuleName = 'controlGroupv2TenantSharing'
      if (value) {
        let { [groupModuleName]: record } = await API.fetchRecord(
          groupModuleName,
          {
            id: value,
          }
        )
        this.currentGroup = record
      }
    },
    async deserialize() {
      this.currentGroup = this.group
      if (!isEmpty(this.record)) {
        let {
          record: { exception, startTime, endTime, startTimeMilli, edited },
        } = this
        let { startSchedule, typeEnum } = exception

        if (!edited && typeEnum === 'RECURING')
          this.schedule.isThisEvent = false
        if (!isEmpty(startSchedule)) {
          let { startSchedule, offSchedule, name } = exception
          let { values } = JSON.parse(startSchedule)
          let days
          if (values) {
            days = this.daysInWeek.filter((day, index) => {
              return values.includes(index + 1)
            })
            if (values.length === 7) this.allDays = true
          }
          this.schedule = {
            ...this.schedule,
            selectedDays: days,
            isDay: true,
            startTime: moment(startTime, 'HH:mm').format('hh:mm A'),
            endTime: moment(endTime, 'HH:mm').format('hh:mm A'),
            name,
            date: startTimeMilli,
            isScheduleExtension: offSchedule ? offSchedule : false,
            isEdited: edited ? edited : false,
          }
        } else {
          let {
            startTime,
            endTime,
            offSchedule,
            exception: { name },
          } = this.record
          this.schedule = {
            ...this.schedule,
            isDay: false,
            startTime: moment(startTime, 'HH:mm').format('hh:mm A'),
            endTime: moment(endTime, 'HH:mm').format('hh:mm A'),
            date: startTimeMilli,
            name,
            isScheduleExtension: offSchedule ? offSchedule : false,
            isEdited: edited ? edited : false,
          }
        }
      }
    },
    checkAllDay(selectedVal) {
      if (selectedVal) {
        this.schedule.selectedDays = this.daysInWeek
      } else {
        this.schedule.selectedDays = []
      }
    },
    getTimeInterval(startHour, endHour) {
      let startTime = moment(startHour, 'HH:mm')
      let endTime = moment(endHour, 'HH:mm')

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
    async save() {
      this.$refs['form'].validate(async valid => {
        if (valid) {
          this.saving = true

          let data = this.serialize()
          let promise

          if (this.isEdit) {
            let { moduleName, id } = this.getModuleDetails
            promise = API.updateRecord(moduleName, {
              data,
              id,
            })
          } else {
            promise = API.createRecord(this.moduleName, {
              data,
            })
          }
          let { error } = await promise

          if (error) {
            this.$message.error(error.message || 'Error Occured')
          } else {
            this.$message.success(
              `Exception ${this.isEdit ? 'Edited' : 'Created'} successfully`
            )
            this.closeDialog(true)
          }
          this.saving = false
        }
      })
    },
    serialize() {
      let { schedule, daysInWeek } = this
      let {
        name,
        startTime,
        endTime,
        isScheduleExtension,
        isDay,
        selectedDays,
        date,
        isThisEvent,
      } = schedule

      if (this.isEdit && isThisEvent) {
        let {
          controlSchedule: { id: scheduleId },
          id: groupId,
        } = this.group

        let {
          id,
          exception: { id: exceptionId },
          startTimeMilli,
        } = this.record

        let date = moment(startTimeMilli).format('DD MM')
        let actualDate = moment(date, 'DD MM').format('x')
        let data = {
          controlGroupModuleName: 'controlGroupv2TenantSharing',
          startTime: this.getDateWithTime(actualDate, startTime),
          endTime: this.getDateWithTime(actualDate, endTime),
          exception: { id: exceptionId },
          group: { id: groupId },
          schedule: { id: scheduleId },
          id: id,
          offSchedule: isScheduleExtension,
        }

        return data
      } else {
        let {
          controlSchedule: { id: scheduleId },
          parentGroup: { id: parentGroupId },
        } = this.currentGroup

        let data = {
          name,
          type: isDay ? 1 : 2,
          offSchedule: isScheduleExtension,
          schedule: { id: scheduleId },
        }

        if (isDay) {
          let scheduleParam = {
            frequency: 1,
            frequencyType: 1,
            values: [],
            times: [getHour(startTime, 'HH:mm')],
            custom: true,
          }

          let { values } = scheduleParam

          selectedDays.forEach(currDay => {
            let index = daysInWeek.findIndex(day => day === currDay)

            values.push(index + 1)
            values.sort()
          })
          data = {
            ...data,
            startScheduleJson: scheduleParam,
            endScheduleTime: getHour(endTime, 'HH:mm'),
          }
        } else {
          data = {
            ...data,
            startTime: this.getDateWithTime(date, startTime),
            endTime: this.getDateWithTime(date, endTime),
          }
        }

        if (this.isTenantGroup) {
          let tenantSpecificData = {
            tenant: { id: parseInt(this.tenantId) },
            parentGroup: { id: parentGroupId },
          }

          data = { ...data, ...tenantSpecificData }
        }
        return data
      }
    },
    getDateWithTime(date, time) {
      let milli = moment(date, 'x')
        .add(getHour(time, 'HH:mm'), 'hours')
        .format('x')
      return parseInt(milli)
    },
    isOptionsDisabled(currTimeInterval) {
      let {
        schedule: { startTime },
      } = this
      if (!isEmpty(startTime)) {
        let formattedStartTime = getHour(startTime, 'H')
        let formattedInterval = getHour(currTimeInterval, 'H')
        if (formattedInterval > formattedStartTime) {
          return false
        } else {
          return true
        }
      } else {
        return true
      }
    },
    clearValidation() {
      this.$refs['form'].clearValidate()
    },
  },
}
</script>

<style scoped>
.schedule-btn-footer {
  display: flex;
  width: 618px;
}
</style>
<style lang="scss">
.agents-dialog .el-dialog__body {
  padding: 30px 0px 0px 0px;
}
.change-schedule-container {
  .el-date-editor.el-input,
  .el-date-editor.el-input__inner {
    width: 100%;
  }
  .flookup-field-groups {
    .el-input {
      .el-input__prefix {
        right: 5px;
        left: 95%;
        z-index: 10;
        .fc-lookup-icon {
          margin-top: 12px;
        }
      }
      .el-input__suffix {
        .el-icon-circle-close {
          padding-left: 30px;
        }
      }
    }
  }
  .f-lookup-chooser .remove-icon {
    top: 0px;
  }
}
</style>
