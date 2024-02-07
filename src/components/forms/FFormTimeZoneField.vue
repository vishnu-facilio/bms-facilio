<template>
  <el-select
    v-model="valueBinder"
    @change="updateValue"
    filterable
    clearable
    class="fc-input-full-border-select2 width100"
  >
    <el-option
      v-for="(timeZone, index) in timeZonesList"
      :key="index"
      :label="timeZone.label"
      :value="timeZone.value"
      :disabled="field.isDisabled"
    >
    </el-option>
  </el-select>
</template>
<script>
import timezones from 'util/data/timezones'
export default {
  props: ['value', 'field'],
  data() {
    return {
      valueBinder: this.value,
    }
  },
  computed: {
    timeZonesList() {
      return timezones
    },
  },
  methods: {
    updateValue(value) {
      this.$emit('input', value)
    },
  },
}
</script>
