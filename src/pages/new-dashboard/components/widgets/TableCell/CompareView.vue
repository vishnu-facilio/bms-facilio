<template>
  <div>
    <div class="compare-view-wrapper">
      <el-row style="display: flex;min-width: 200px;">
        <el-col :span="24">
            <div class="compare-value-container">
              {{ value }}
            </div>
        </el-col>
        <el-col :span="8">
          <div class="icons-container mR5">
              <i
                v-if="valueInPercentage < 0"
                class="el-icon-bottom"
                :style="{ color: downArrowColor }"
              ></i>
              <i v-else class="el-icon-top" :style="{ color: upArrowColor }"></i>
          </div>
        </el-col>
        <el-col :span="24">
          <div :style="getCellStyle"><span> {{ absValueInPercentage + ' %' }}</span></div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import BaseView from './BaseView.vue'
export default {
  props: ['visualConfig', 'referenceValue', 'alignment'],
  computed: {
    downArrowColor() {
      return this.visualConfig.downArrowColor
        ? this.visualConfig.downArrowColor
        : 'red'
    },
    upArrowColor() {
      return this.visualConfig.upArrowColor
        ? this.visualConfig.upArrowColor
        : 'green'
    },
    getCellStyle() {
      let style = {}
      style['display'] = 'flex'
      if (this.alignment == 'right') {
        style['justify-content'] = 'flex-end'
      } else if (this.alignment == 'left' || this.alignment == 'center') {
        style['justify-content'] = 'flex-start'
      } else  {
        style['justify-content'] = 'space-around'
      }
      style['align-items'] = 'center'
      return style
    },
  },
  extends: BaseView,
}
</script>

<style>
.compare-view-wrapper {
  display: flex;
  justify-content: space-around;
  width: fit-content;
}
.compare-icon-container {
  display: flex;
  padding-left: 25px;
  white-space: nowrap;
}
</style>
