<template>
  <el-popover
    placement="right"
    width="300"
    trigger="manual"
    popper-class="scheduler-color-picker"
    v-model="visibility"
    v-click-outside="close"
  >
    <div class="popup-container">
      <template v-for="(color, index) in colorPalette">
        <div
          v-if="color !== selectedColor"
          :key="index"
          @click="saveColor(color)"
          class="color-shade"
          :style="{ 'background-color': `${color}` }"
        ></div>
        <InlineSvg
          v-else
          :key="`dropdown-${index}`"
          src="tick-sign"
          class="color-shade"
          :style="{ 'background-color': `${color}` }"
          iconClass="icon icon-xxs margin-auto"
          iconStyle="fill: #fff"
        ></InlineSvg>
      </template>
    </div>
    <template #reference>
      <div @click="visibility = true">
        <slot name="reference"></slot>
      </div>
    </template>
  </el-popover>
</template>
<script>
import { colorPalette } from '../schedulerViewUtil'

export default {
  props: ['selectedColor'],
  data() {
    return {
      visibility: false,
      currentColor: null,
      colorPalette,
    }
  },
  created() {
    this.currentColor = this.selectedColor
  },
  methods: {
    saveColor(color) {
      this.currentColor = color
      this.$emit('currentColor', {
        eventColor: color,
      })
      this.close()
    },
    close() {
      this.visibility = false
    },
  },
}
</script>
<style lang="scss">
.scheduler-color-picker {
  .popup-container {
    display: flex;
    flex-wrap: wrap;
    flex-direction: column;
    height: 100px;
  }
  .color-shade {
    height: 28px;
    width: 28px;
    display: flex;
    border-radius: 5px;
    border: 1px solid rgb(0, 0, 0, 0.2);
    margin: 0 5px 5px 0;
    justify-content: flex-end;
  }
}
</style>
