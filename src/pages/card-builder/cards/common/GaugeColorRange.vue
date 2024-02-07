<template>
  <div class="gauge-color-range">
    <el-row
      v-for="(range, index) in value"
      :key="'range-' + range.id"
      class="mB10"
    >
      <el-col :span="10" class="d-flex range-inputs">
        <el-input
          v-if="index === 0"
          :disabled="true"
          v-model="dummy"
          class="fc-input-full-border-select2"
        ></el-input>
        <el-input
          v-else
          :disabled="true"
          v-model="value[index - 1].limit"
          class="fc-input-full-border-select2"
        ></el-input>
        <div class="range-prefix">
          to
        </div>
        <el-input
          type="number"
          v-model="range.limit"
          :min="index === 0 ? 0 : value[index - 1].limit + 1"
          :max="index === value.length - 1 ? 100 : value[index + 1].limit - 1"
          :disabled="index === value.length - 1"
          class="fc-input-full-border-select2 range-input pR10"
        >
        </el-input>
      </el-col>
      <el-col :span="10">
        <el-input
          v-model="range.label"
          type="text"
          class="fc-input-full-border-select2"
        >
        </el-input>
      </el-col>
      <el-col :span="4" class="pL10">
        <div class="mT5 card-color-container">
          <div class="mR10 fc-color-picker card-color-container">
            <el-color-picker
              v-model="range.color"
              :key="'' + range.id + range.color"
              :predefine="predefinedColors"
              size="small"
              :popper-class="'chart-custom-color-picker'"
            ></el-color-picker>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>
<script>
import { predefinedColors } from 'pages/card-builder/card-constants'
export default {
  props: ['value'],
  data() {
    return {
      predefinedColors,
      dummy: 0,
    }
  },
}
</script>
<style lang="scss" scoped>
.range-prefix {
  padding: 0 20px 0 10px;
  display: inline-block;
  white-space: nowrap;
  padding: 0 10px 0 10px;
  text-align: center;
  align-self: center;
  font-size: 15px;
}
</style>
<style lang="scss">
.gauge-color-range {
  .el-input.is-disabled .el-input__inner {
    color: #324056;
  }
  .range-inputs .fc-input-full-border-select2 {
    max-width: 100px;
  }
}
</style>
