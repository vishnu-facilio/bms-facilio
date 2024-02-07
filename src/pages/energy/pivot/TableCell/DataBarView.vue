<template>
  <div>
    <div class="progress-bar-wrapper">
      <div class="progress-bar" :style="{ background: `${fillColor}0f` }">
        <div
          class="progress-bar-base"
          :style="{
            width: `${absValueInPercentage}%`,
            opacity: 1,
            background: fillColor,
          }"
        >
          <div
            v-if="showValue"
            class="data-bar-value-container"
            :style="{ color: textColor }"
          >
            {{ value }}
          </div>
        </div>
      </div>
      <div v-if="false" class="progress-percentage mL5">
        <div class="icons-container mL5">
          <i
            v-if="valueInPercentage < 0"
            class="el-icon-bottom"
            :style="{ color: 'red' }"
          ></i>
          <i v-else class="el-icon-top" :style="{ color: 'green' }"></i>
        </div>
        <div class="data-bar-value-container">
          {{ absValueInPercentage + '%' }}
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import BaseView from './BaseView.vue'
export default {
  computed: {
    showIcon() {
      if (this.visualConfig.compareWith != 'maxValue') return true
      return false
    },
    fillColor() {
      return this.visualConfig.fillColor ? this.visualConfig.fillColor : 'green'
    },
    textColor() {
      return this.visualConfig.textColor ? this.visualConfig.textColor : 'black'
    },
    showValue() {
      return this.visualConfig.showValue
    },
  },
  extends: BaseView,
}
</script>

<style scoped>
.progress-bar {
  width: 100%;
  display: flex;
  height: 25px;
  background: rgb(190, 190, 190);
  align-items: center;
}
.progress-bar-wrapper {
  width: 100%;
  display: flex;
}
.progress-bar-base {
  height: 25px;
  display: flex;
  flex-wrap: wrap;
  align-content: space-around;
  justify-content: flex-end;
  align-items: flex-end;
}
.data-bar-value-container {
  padding-left: 15px;
}
.progress-percentage {
  display: flex;
  width: 100px;
  white-space: nowrap;
}
.data-bar-value-container {
  padding-right: 5px;
}
</style>
