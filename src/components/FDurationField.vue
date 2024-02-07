<template>
  <div class="d-flex">
    <el-input
      type="number"
      v-model="durationObj[field.selectedUnit]"
      class="fc-input-full-border2 input-with-select"
      @change="handleDurationObjChange()"
      :disabled="isDisabled"
    >
      <el-select
        v-model="field.selectedUnit"
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
    <el-input
      type="number"
      v-model="durationObj[secondaryUnitObj.value]"
      class="fc-input-full-border2 secondary-field"
      @change="handleDurationObjChange()"
      :disabled="isDisabled"
    >
      <span slot="suffix" class="pR10 fc-grey6 f12 line-height40">{{
        secondaryUnitObj.label
      }}</span>
    </el-input>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import { durationToSeconds } from '@facilio/utils/utility-methods'

export default {
  props: ['field', 'isDisabled', 'value'],
  data() {
    return {
      durationObj: {
        days: 0,
        hours: 0,
        minutes: 0,
        seconds: 0,
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
      ],
    }
  },
  computed: {
    secondaryUnitObj() {
      let { field } = this
      let { selectedUnit } = field
      return selectedUnit === 'days'
        ? { label: 'Hours', value: 'hours' }
        : { label: 'Minutes', value: 'minutes' }
    },
  },
  watch: {
    field: {
      handler() {
        let { field } = this
        let { value } = field
        this.deserializeDurationField(value)
      },
      immediate: true,
      deep: true,
    },
    value: {
      handler(val) {
        this.deserializeDurationField(val)
      },
      immediate: true,
    },
  },
  methods: {
    deserializeDurationField(value) {
      if (!isEmpty(value)) {
        let durationObj = this.$helpers.getDuration(value, 'seconds', 1)
        let { Days = 0, Hrs = 0, Mins = 0, Secs = 0 } = durationObj
        let selectedUnit = Days === 0 ? 'hours' : 'days'
        this.$set(this, 'durationObj', {
          days: Days,
          hours: Hrs,
          minutes: Mins,
          seconds: Secs,
        })
        this.$set(this.field, 'selectedUnit', selectedUnit)
      }
    },
    onUnitChange(value) {
      let { durationObj } = this
      let { days, hours, minutes, seconds } = durationObj
      if (value === 'days') {
        minutes = 0
        seconds = 0
      } else if (value === 'hours') {
        days = 0
        seconds = 0
      }
      this.$set(this, 'durationObj', {
        days,
        hours,
        minutes,
        seconds,
      })
    },
    handleDurationObjChange() {
      let { durationObj } = this
      let { days, hours, minutes, seconds } = durationObj
      let formatedDuration = durationToSeconds({
        seconds,
        minutes,
        hours,
        days,
      })
      this.setFieldValue(formatedDuration)
    },
    setFieldValue(formatedDuration) {
      this.$set(this.field, 'value', formatedDuration)
      this.$emit('updateDurationValue', this.field, formatedDuration)
      this.$emit('input', formatedDuration)
    },
  },
}
</script>
<style lang="scss">
.formbuilder-main-comp-right {
  .secondary-field {
    margin-top: 10px;
  }
}
.duration-container {
  .fc-input-full-border2 {
    @media screen and (min-width: 1000px) {
      width: 49%;
      .fc-input-full-border2:last-of-type {
        margin-left: 15px;
      }
    }
    width: 48%;
    line-height: normal;
    &.input-with-select {
      .el-input-group__append {
        width: 40%;
      }
    }
  }
  .fc-input-full-border2:last-of-type {
    margin-left: 9px;
  }
}
.fc-input-full-border2 {
  &.input-with-select {
    .el-input__inner {
      border-top-right-radius: 0 !important;
      border-bottom-right-radius: 0 !important;
    }
    .el-input-group__append {
      width: 30%;
      background: #fff;
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
}
</style>
