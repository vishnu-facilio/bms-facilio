<template>
  <div v-if="unitConfig">
    <el-row class="">
      <el-col :span="12">
        <el-form-item prop="unit" class="">
          <p class="fc-input-label-txt pB5">Unit</p>
          <div class="d-flex  card-color-container fc-color-picker">
            <el-input
              v-model="unitConfig.unit"
              class="width100 fc-input-full-border2"
              clearable
            ></el-input>
          </div>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item prop="position" class="">
          <p class="fc-input-label-txt pB5">Position</p>
          <div class="d-flex  card-color-container fc-color-picker">
            <el-select
              v-model="unitConfig.position"
              placeholder="Please select a period"
              class="width100 el-input-textbox-full-border"
            >
              <template v-for="(value, key) in unitPosition">
                <el-option :label="key" :value="value" :key="key"></el-option>
              </template>
            </el-select>
          </div>
        </el-form-item>
      </el-col>
    </el-row>
  </div>
</template>
<script>
import { unitPosition } from 'pages/card-builder/card-constants'
export default {
  props: ['unitConfig'],
  data() {
    return {
      unitPosition,
    }
  },
}
</script>
