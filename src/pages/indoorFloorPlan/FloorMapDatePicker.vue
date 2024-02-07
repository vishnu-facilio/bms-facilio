<template>
  <el-date-picker
    ref="date-picker"
    v-model="ttime"
    :appendToBody="appendToBody"
    :clearable="!hideClear"
    :type="type"
    :value-format="valueFormat"
    :format="pickerFormat"
    :placeholder="placeholder"
    :disabled="disabled"
    :editable="editable"
    :default-value="defaultValue"
    :picker-options="pickerOptions"
    :prefix-icon="prefixIcon"
    :clear-icon="clearIcon"
    :start-placeholder="startPlaceholder"
    :end-placeholder="endPlaceholder"
    :time-arrow-control="arrowControl"
    @change="pickerChange"
    popper-class="date-picker-panel"
  ></el-date-picker>
</template>
<script>
import FDatePicker from 'src/pages/assets/overview/FDatePicker.vue'
export default {
  extends: FDatePicker,
}
</script>
<style lang="scss">
.date-picker-panel {
  .el-date-table td.current:not(.disabled) span {
    border-radius: 7.2px;
    background-color: #0053cc;
    color: #fff;
  }
  table.el-date-table {
    color: #483db6;
  }
  .el-date-table th {
    font-size: 10px;
    font-weight: 500;
    font-stretch: normal;
    font-style: normal;
    line-height: normal;
    letter-spacing: normal;
    text-align: center;
    color: #334056;
    opacity: 0.5;
    border-bottom: 0px;
  }
  .el-button--text,
  .el-date-table td.available:hover,
  .el-date-table td.today span {
    color: #483db6;
  }
  .el-date-picker__header-label {
    font-size: 14px;
    font-weight: bold;
    font-stretch: normal;
    font-style: normal;
    line-height: normal;
    letter-spacing: normal;
    color: #483db6;
  }
  td.available {
    font-size: 10px;
  }
  td.prev-month {
    font-size: 10px;
  }
  .el-date-table td.current {
    border-radius: 7.2px;
    background-color: #0053cc;
  }
  .el-date-table td.today {
    color: inherit;
  }
  .el-date-table td:hover {
    border-radius: 7.2px;
    background-color: #0053cc;
    color: #fff !important;
  }
}
</style>
