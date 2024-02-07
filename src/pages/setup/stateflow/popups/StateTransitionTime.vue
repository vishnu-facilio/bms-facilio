<template>
  <div>
    <el-dialog
      :visible="true"
      width="50%"
      title="Configure Time"
      class="fieldchange-Dialog pB15 fc-dialog-center-container"
      custom-class="dialog-header-padding"
      :append-to-body="true"
      :before-close="close"
    >
      <error-banner
        :error.sync="error"
        :errorMessage="errorMessage"
      ></error-banner>

      <div class="height380 overflow-y-scroll pB50">
        <el-radio-group
          v-model="transitionDateObj.type"
          @change="clearData"
          class="criteria-radio-label mB20"
        >
          <el-radio class="fc-radio-btn" :label="2">
            On Time
          </el-radio>
          <el-radio class="fc-radio-btn" :label="4">
            On Date
          </el-radio>
        </el-radio-group>

        <el-row
          v-if="transitionDateObj.type !== $constants.TRANSITION_TYPE.time"
          class="mB20"
        >
          <el-col :span="15" class="mB20">
            <p class="fc-input-label-txt">
              {{ $t('setup.setupLabel.dateField') }}
            </p>
            <el-select
              v-model="transitionDateObj.dateFieldId"
              placeholder="Select field"
              class="fc-input-full-border-select2 width100"
            >
              <el-option
                v-for="(fld, index) in filteredDateFields"
                :key="`${fld.id}_${index}`"
                :label="fld.displayName"
                :value="fld.id"
              >
              </el-option>
            </el-select>
          </el-col>

          <el-col :span="18">
            <p class="fc-input-label-txt mB10">
              {{ $t('setup.setupLabel.schedule_type') }}
            </p>
            <el-radio-group
              v-model="transitionDateObj.scheduleType"
              class="criteria-radio-label"
            >
              <el-radio
                class="fc-radio-btn"
                :label="scheduleTypes.BEFORE"
                :key="1"
                v-if="canShowBefore"
              >
                Before
              </el-radio>
              <el-radio class="fc-radio-btn" :label="scheduleTypes.ON" :key="2">
                On
              </el-radio>
              <el-radio
                class="fc-radio-btn"
                :label="scheduleTypes.AFTER"
                :key="3"
              >
                After
              </el-radio>
            </el-radio-group>
          </el-col>
        </el-row>

        <el-row v-if="canShowTime">
          <el-col :span="isDateTimeField ? 8 : 12" class="pR10">
            <p class="fc-input-label-txt f13 pB5">Days</p>
            <el-select
              v-model="transitionDateObj.date.days"
              clearable
              placeholder="Select"
              class="fc-input-full-border-select2 width100"
            >
              <el-option
                v-for="index in 30"
                :label="index"
                :key="index + 1"
                :value="index"
              ></el-option>
            </el-select>
          </el-col>

          <template v-if="isDateTimeField">
            <el-col :span="8" class="pL10 pR10">
              <p class="fc-input-label-txt f13 pB5">Hours</p>
              <el-select
                v-model="transitionDateObj.date.hours"
                clearable
                class="fc-input-full-border-select2 width100"
              >
                <el-option
                  v-for="index in $constants.HOURS"
                  :label="index"
                  :key="index + 1"
                  :value="index"
                ></el-option>
              </el-select>
            </el-col>

            <el-col :span="8" class="pL10">
              <p class="fc-input-label-txt f13 pB5">Mins</p>
              <el-select
                v-model="transitionDateObj.date.minute"
                placeholder="Select"
                class="fc-input-full-border-select2 width100"
              >
                <el-option
                  v-for="index in $constants.MINUTES"
                  :label="index"
                  :key="index + 1"
                  :value="index"
                ></el-option>
              </el-select>
            </el-col>
          </template>

          <el-col :span="12" v-else class="pL10">
            <p class="fc-input-label-txt f13 pB5">Time</p>
            <el-select
              v-model="transitionDateObj.time"
              collapse-tags
              class="fc-input-full-border-select2 width100 fc-tag"
            >
              <el-option
                v-for="time in timeOption"
                :label="time"
                :value="time"
                :key="time"
              ></el-option>
            </el-select>
          </el-col>
        </el-row>
      </div>

      <div class="modal-dialog-footer">
        <el-button @click="close()" class="modal-btn-cancel">CANCEL</el-button>
        <el-button type="primary" class="modal-btn-save" @click="saveTime()">
          Save
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
import ErrorBanner from '@/ErrorBanner'
import {
  isDateTimeField as checkDateTimeField,
  isDateField as checkDateField,
} from '@facilio/utils/field'

const scheduleTypes = {
  BEFORE: 1,
  ON: 2,
  AFTER: 3,
}

export default {
  components: { ErrorBanner },
  props: ['timeObj', 'dataObj', 'moduleFields'],

  data() {
    return {
      transitionDateObj: {
        type: 2,
        date: null,
      },
      error: false,
      errorMessage: '',
      scheduleTypes,
    }
  },

  computed: {
    canShowTime() {
      let { TRANSITION_TYPE } = this.$constants
      let { scheduleType, type } = this.transitionDateObj
      let { BEFORE, AFTER } = scheduleTypes

      return (
        TRANSITION_TYPE.time === type || [BEFORE, AFTER].includes(scheduleType)
      )
    },
    canShowBefore() {
      let { dateFieldId } = this.transitionDateObj
      let beforeDateFieldsToExclude = ['createdTime', 'modifiedTime']
      let beforeFieldIds = this.moduleFields
        .filter(field => beforeDateFieldsToExclude.includes(field.name))
        .map(field => field.id)

      return !beforeFieldIds.includes(dateFieldId)
    },
    filteredDateFields() {
      let dateFieldToExclude = [
        'sysCreatedTime',
        'sysModifiedTime',
        'sysDeletedTime',
      ]
      let beforeDateFieldsToExclude = ['createdTime', 'modifiedTime']
      let { transitionDateObj, moduleFields } = this
      let { scheduleType } = transitionDateObj
      let excludedfields = [...dateFieldToExclude]

      if (scheduleType === scheduleTypes.BEFORE) {
        excludedfields = [...excludedfields, ...beforeDateFieldsToExclude]
      }

      return (moduleFields || []).filter(
        field =>
          !excludedfields.includes(field.name) &&
          (checkDateTimeField(field) || checkDateField(field))
      )
    },
    isDateTimeField() {
      let { TRANSITION_TYPE } = this.$constants
      let { dateFieldId, type } = this.transitionDateObj
      let field = this.moduleFields.find(d => d.fieldId === dateFieldId) || {}

      return (
        TRANSITION_TYPE.time === type ||
        (!isEmpty(field) ? checkDateTimeField(field) : false)
      )
    },
    timeOption() {
      let timeOption = []
      this.$constants.HOURS.forEach(t => {
        let time = (t < 10 ? `0${t}` : t) + ':'
        timeOption.push(time + '00')
        timeOption.push(time + '30')
      })
      return timeOption
    },
  },

  watch: {
    timeObj: {
      handler(newVal) {
        this.transitionDateObj.date = newVal
      },
      immediate: true,
    },
    dataObj: {
      handler(newVal) {
        this.transitionDateObj = { ...this.transitionDateObj, ...newVal }
      },
      immediate: true,
    },
    isDateTimeField() {
      let { isDateTimeField, transitionDateObj } = this
      let time = !isDateTimeField ? '00:00' : null

      this.transitionDateObj = {
        ...transitionDateObj,
        time,
        date: {
          days: null,
          minute: 0,
          hours: 0,
        },
      }
    },
  },

  methods: {
    saveTime() {
      let {
        type,
        date,
        scheduleType,
        dateFieldId,
        time,
      } = this.transitionDateObj
      let { TRANSITION_TYPE } = this.$constants
      let { days, minute, hours } = date
      let timeField = !isEmpty(days) || minute !== 0 || hours !== 0
      let dateFieldTimeValidation =
        scheduleType === scheduleTypes.ON || timeField || !isEmpty(time)
      let dateFieldValidation =
        type === TRANSITION_TYPE.datefield &&
        !isEmpty(dateFieldId) &&
        dateFieldTimeValidation

      if ((type === TRANSITION_TYPE.time && timeField) || dateFieldValidation) {
        this.$emit('save', this.transitionDateObj)
      } else {
        this.error = true
        this.errorMessage = ' Please select fields'
        return
      }
    },

    close() {
      this.$emit('close')
    },
    clearData() {
      let { type } = this.transitionDateObj
      let time = !this.isDateTimeField ? '00:00' : null

      this.transitionDateObj = {
        scheduleType: 3,
        dateFieldId: null,
        time,
        type,
        date: {
          days: null,
          minute: 0,
          hours: 0,
        },
      }
    },
  },
}
</script>
