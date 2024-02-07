<template>
  <div>
    <el-row prop="workflowRule">
      <el-col :span="12">
        <p class="fc-input-label-txt">
          {{ $t('setup.setupLabel.dateField') }}
        </p>
        <el-select
          v-model="scheduleObj.dateFieldId"
          placeholder="Select"
          class="width300px"
        >
          <el-option
            v-for="(fld, index) in filteredDateFields"
            :key="index"
            :label="fld.displayName"
            :value="fld.id"
          >
          </el-option>
        </el-select>
      </el-col>
      <el-col :span="12" class="pL40">
        <p class="fc-input-label-txt mB10">
          {{ $t('setup.setupLabel.scheduled_type') }}
        </p>
        <el-radio-group
          v-model="scheduleObj.scheduleType"
          class="criteria-radio-label"
        >
          <el-radio
            class="fc-radio-btn"
            :label="1"
            :key="1"
            v-if="!beforeFieldIds.includes(scheduleObj.dateFieldId)"
            >Before</el-radio
          >
          <el-radio class="fc-radio-btn" :label="2" :key="2">On </el-radio>
          <el-radio class="fc-radio-btn" :label="3" :key="3">After </el-radio>
        </el-radio-group>
      </el-col>
    </el-row>
    <el-row class="mT30">
      <template v-if="[1, 3].includes(parseInt(scheduleObj.scheduleType))">
        <el-col :span="8">
          <p class="fc-input-label-txt">Days</p>
          <el-select
            v-model="scheduleObj.dateObject.days"
            clearable
            placeholder="Select"
            class="fc-input-full-border-select2"
          >
            <el-option
              v-for="index in 60"
              :label="index"
              :key="`days-${index}`"
              :value="index"
            ></el-option>
          </el-select>
        </el-col>
        <template v-if="checkDateTimeField">
          <el-col :span="8">
            <p class="fc-input-label-txt">Hours</p>
            <el-select
              v-model="scheduleObj.dateObject.hours"
              clearable
              class="fc-input-full-border-select2"
            >
              <el-option
                v-for="index in $constants.HOURS"
                :label="index"
                :key="index + 1"
                :value="index"
              ></el-option>
            </el-select>
          </el-col>
          <el-col :span="8">
            <p class="fc-input-label-txt">Mins</p>
            <el-select
              v-model="scheduleObj.dateObject.minute"
              placeholder="Select"
              class="fc-input-full-border-select2"
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
      </template>
      <el-col :span="8" v-if="!checkDateTimeField">
        <p class="fc-input-label-txt">Time</p>
        <el-select
          v-model="scheduleObj.time"
          collapse-tags
          class="fc-input-full-border-select2 width100 fc-tag"
        >
          <el-option
            v-for="time in timesOption"
            :label="time"
            :value="time"
            :key="time"
          ></el-option>
        </el-select>
      </el-col>
    </el-row>
  </div>
</template>
<script>
import { isDateTimeField } from '@facilio/utils/field'
import { isEmpty } from '@facilio/utils/validation'
export default {
  props: ['moduleFields', 'workflowRule', 'isEdit'],
  data() {
    return {
      dateFieldToExclude: [
        'sysCreatedTime',
        'sysModifiedTime',
        'sysDeletedTime',
      ],
      beforeDateFieldsToExclude: ['createdTime', 'modifiedTime'],
      dateFieldName: ['DATE', 'DATE_TIME'],
      metaInfo: {},
      scheduleObj: {
        time: null,
        scheduleType: 3,
        dateFieldId: null,
        dateObject: {
          days: null,
          minute: 0,
          hours: 0,
          minu: null,
        },
      },
    }
  },
  watch: {
    scheduleObj: {
      handler: function() {
        this.dateField()
      },
      deep: true,
    },
  },
  created() {
    let { isEdit } = this
    if (isEdit) {
      let { workflowRule } = this
      let { interval } = workflowRule || {}
      let dateFieldId = this.dateFieldsCheck(workflowRule, interval)
      this.$setProperty(this, 'scheduleObj.dateFieldId', dateFieldId)
      this.$setProperty(
        this,
        'scheduleObj.scheduleType',
        workflowRule.scheduleType
      )
      this.dateField()
    }
  },
  computed: {
    checkDateTimeField() {
      let { dateFields, scheduleObj } = this
      let { dateFieldId } = scheduleObj || {}
      let field =
        (dateFields || []).find(fld => fld.fieldId === dateFieldId) || {}

      return !isEmpty(field) ? isDateTimeField(field) : false
    },

    filteredDateFields() {
      let fields = [...this.dateFieldToExclude]
      if (this.scheduleType === 1) {
        fields = [...fields, ...this.beforeDateFieldsToExclude]
      }
      return (this.dateFields || []).filter(
        field => !fields.includes(field.name)
      )
    },
    dateFields() {
      let { moduleFields, dateFieldName } = this
      return (moduleFields || []).filter(d =>
        (dateFieldName || []).includes(d.dataTypeEnum._name)
      )
    },
    beforeFieldIds() {
      return (this.dateFields || [])
        .filter(field =>
          (this.beforeDateFieldsToExclude || []).includes(field.name)
        )
        .map(field => field.id)
    },
  },
  methods: {
    dateFieldsCheck(rule, dateObj) {
      let { event, dateFieldId, scheduleType } = rule || {}
      let { activityType } = event || {}
      if (activityType === 524288 && !isEmpty(dateFieldId)) {
        if (dateObj && [1, 3].includes(parseInt(scheduleType))) {
          let dateObject = this.$helpers.secTodaysHoursMinu(dateObj)
          this.scheduleObj.dateObject = {
            ...dateObject,
          }
        }
        return rule.dateFieldId
      }
      this.dateObject = {
        days: null,
        minute: 0,
        hours: 0,
        minu: null,
      }
      return null
    },
    dateField() {
      let { scheduleObj } = this
      this.$emit('schedulerFields', scheduleObj)
    },
  },
}
</script>
