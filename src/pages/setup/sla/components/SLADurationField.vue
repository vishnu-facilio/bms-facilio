<template>
  <div class="duration-field-container d-flex">
    <template v-if="selectedUnit === 'minutes'">
      <el-select
        type="number"
        v-model="durationObj[selectedUnit]"
        class="fc-input-full-border2 input-with-select flex-grow"
        @change="handleDurationObjChange"
        :disabled="isDisabled"
        placeholder="Select duration"
        clearable
      >
        <el-option
          v-for="option in minuteOptions"
          :key="option"
          :label="option"
          :value="option"
        ></el-option>
      </el-select>
      <el-select
        v-model="selectedUnit"
        :disabled="isDisabled"
        class="fc-input-full-border2 unit-selection"
        @change="onUnitChange(...arguments)"
      >
        <el-option
          v-for="option in unitOptions"
          :key="option.value"
          :label="option.label"
          :value="option.value"
        ></el-option>
      </el-select>
    </template>
    <template v-else-if="selectedUnit === 'field'">
      <el-cascader
        v-model="durationObj[selectedUnit]"
        :options="fieldOptions"
        :props="{
          expandTrigger: 'hover',
          value: 'name',
          label: 'displayName',
          children: 'fields',
        }"
        @change="handleDurationObjChange"
        class="fc-input-full-border2 input-with-select flex-grow"
        popper-class="sla-duration-field"
      ></el-cascader>
      <el-select
        v-model="selectedUnit"
        :disabled="isDisabled"
        class="fc-input-full-border2 unit-selection"
        @change="onUnitChange(...arguments)"
      >
        <el-option
          v-for="option in unitOptions"
          :key="option.value"
          :label="option.label"
          :value="option.value"
        ></el-option>
      </el-select>
    </template>
    <el-input
      v-else
      v-model="durationObj[selectedUnit]"
      class="fc-input-full-border2 input-with-select"
      @change="handleDurationObjChange"
      :disabled="isDisabled"
      placeholder="Enter duration"
    >
      <el-select
        v-model="selectedUnit"
        slot="append"
        :disabled="isDisabled"
        @change="onUnitChange(...arguments)"
      >
        <el-option
          v-for="option in unitOptions"
          :key="option.value"
          :label="option.label"
          :value="option.value"
        ></el-option>
      </el-select>
    </el-input>
  </div>
</template>
<script>
import moment from 'moment-timezone'
import { isEmpty } from '@facilio/utils/validation'
import { durationToSeconds } from '@facilio/utils/utility-methods'
import { API } from '@facilio/api'

const regex = /[\${:\-}]+/gm

export default {
  props: ['value', 'isDisabled'],
  data() {
    return {
      selectedUnit: 'hours',
      durationObj: {
        days: null,
        hours: null,
        minutes: null,
        field: null,
      },
      unitOptions: [
        {
          label: 'Days',
          value: 'days',
        },
        {
          label: 'Hours',
          value: 'hours',
        },
        {
          label: 'Minutes',
          value: 'minutes',
        },
        {
          label: 'Field',
          value: 'field',
        },
      ],
      minuteOptions: [15, 30, 45],
      fieldOptions: [],
    }
  },
  async created() {
    await this.loadPlaceholderFieldOptions()
    this.deserializeDurationField()
  },
  computed: {
    moduleName() {
      return this.$route.params.moduleName
    },
  },
  watch: {
    value: {
      handler: 'deserializeDurationField',
    },
  },
  methods: {
    async loadPlaceholderFieldOptions() {
      let { data, error } = await API.get(`v3/placeholders/${this.moduleName}`)

      if (!error) {
        let { placeholders } = data || {}
        let { fields, moduleFields } = placeholders || {}

        if (!isEmpty(fields)) {
          let fieldTypeFilter = fld =>
            ['NUMBER'].includes(fld.dataType) &&
            ['DURATION'].includes(fld.displayType)

          if (!isEmpty(moduleFields)) {
            Object.keys(moduleFields).forEach(fieldName => {
              let lookupField = fields.find(fld => fld.module === fieldName)

              lookupField.fields = moduleFields[fieldName].filter(
                fieldTypeFilter
              )
            })
          }
          this.fieldOptions = fields.filter(
            fld => fieldTypeFilter(fld) || !isEmpty(fld.fields)
          )
        }
      }
    },
    deserializeDurationField() {
      let { value, selectedUnit } = this
      if (!isEmpty(value)) {
        let durationObj = {
          days: null,
          hours: null,
          minutes: null,
          field: null,
        }

        //return true if value string has `${}`
        if (!regex.test(value)) {
          let duration = moment.duration(parseInt(value, 10), 'seconds')
          let Days = parseInt(duration.asDays(), 10)
          let Hrs = parseInt(duration.hours(), 10)
          let Mins = parseInt(duration.minutes(), 10)

          if (Days !== 0) {
            if (Hrs !== 0) {
              Hrs = parseInt(duration.asHours(), 10)
              Days = 0
              selectedUnit = 'hours'
            } else {
              selectedUnit = 'days'
            }
          } else if (Hrs !== 0) {
            if (Mins !== 0) {
              selectedUnit = 'minutes'
              Hrs = 0
              Mins = parseInt(duration.asMinutes(), 10)
            } else {
              selectedUnit = 'hours'
            }
          } else if (Mins !== 0) {
            selectedUnit = 'minutes'
          } else {
            selectedUnit = 'hours'
          }

          durationObj = {
            days: Days,
            hours: Hrs,
            minutes: Mins,
            field: null,
          }

          // Set null for values that are 0. This is to allow users to clear
          // values in the field (without this value will always be 0)
          Object.keys(durationObj).forEach(key => {
            if (durationObj[key] === 0) durationObj[key] = null
          })
        } else {
          durationObj.field = this.getFieldFromPlaceholder(value)
          selectedUnit = 'field'
        }

        this.$set(this, 'durationObj', durationObj)
      }
      this.$set(this, 'selectedUnit', selectedUnit)
    },
    getFieldFromPlaceholder(value) {
      let placeholderArray = value.replace(regex, '').split('.')
      let [, fieldName, primaryField] = placeholderArray
      let field = !isEmpty(fieldName)
        ? this.fieldOptions.find(f => f.name === fieldName)
        : null

      if (!isEmpty(field)) {
        if (primaryField) {
          // Checking if primary field is a lookup field
          let lookupField =
            field.fields?.find(f => f.name === primaryField) || null

          if (!isEmpty(lookupField)) {
            return [fieldName, primaryField]
          } else {
            return [fieldName]
          }
        } else {
          return [fieldName]
        }
      }
      return null
    },
    onUnitChange(unit) {
      let { durationObj } = this
      let existingDuration = Object.values(durationObj).find(
        value => value != 0
      )

      Object.keys(durationObj).forEach(key => {
        if (key !== unit) {
          durationObj[key] = 0
        } else {
          durationObj[key] = unit === 'minutes' ? 30 : existingDuration
        }
        durationObj['field'] = null
      })
      this.$set(this, 'selectedUnit', unit)
      this.$set(this, 'durationObj', durationObj)
      this.handleDurationObjChange()
    },
    getPlaceholderFromField(placeholderField) {
      let placeholderString = null

      if (!isEmpty(placeholderField)) {
        let [fieldName, lookupFieldName] = placeholderField
        let { primaryField, fields } = this.fieldOptions.find(
          f => f.name === fieldName
        )

        if (!isEmpty(lookupFieldName)) {
          let { primaryField: lookupPrimaryField } = fields.find(
            f => f.name === lookupFieldName
          )
          !isEmpty(lookupPrimaryField) &&
            placeholderField.push(lookupPrimaryField)
        } else {
          !isEmpty(primaryField) && placeholderField.push(primaryField)
        }
        placeholderString = `\${${this.moduleName}.${placeholderField.join(
          '.'
        )}:-}`
      }

      return placeholderString
    },
    handleDurationObjChange() {
      let { durationObj } = this
      let { days, hours, minutes = 0, seconds = 0, field } = durationObj
      let value = null

      if (this.selectedUnit !== 'field') {
        let formatedDuration = durationToSeconds({
          seconds,
          minutes,
          hours,
          days,
        })
        value = isEmpty(formatedDuration) ? null : formatedDuration
      } else {
        value = this.getPlaceholderFromField(field)
      }
      this.$emit('change', value)
    },
  },
}
</script>
<style lang="scss">
.duration-field-container {
  height: 40px;
  .fc-input-full-border2 {
    line-height: normal;

    &.input-with-select {
      .el-input__inner {
        border-top-right-radius: 0 !important;
        border-bottom-right-radius: 0 !important;
      }
      &.is-disabled {
        .el-input-group__append {
          background: unset;
        }
      }
      .el-select {
        .el-input__inner {
          border: none !important;
          font-size: 12px;
        }
        .el-input {
          &.is-disabled {
            .el-input__inner {
              background: #f5f7fa;
            }
          }
        }
      }
    }
    &.input-with-select .el-input-group__append,
    &.unit-selection {
      width: 100px;
      background-color: #f1f8fa;
      color: #324056;
    }
    &.unit-selection {
      .el-input__inner {
        background-color: #f1f8fa;
        color: #324056;
        font-size: 12px;
        border-left: none !important;
        border-top-left-radius: 0 !important;
        border-bottom-left-radius: 0 !important;
      }
    }
  }
}
.sla-duration-field .el-cascader-panel .el-cascader-menu__list {
  .el-cascader-node.is-active,
  .el-cascader-node.in-active-path {
    color: #f94f83;
    font-weight: normal;
    background-color: rgba(248, 249, 250, 0.7803921568627451);
  }
}
</style>
