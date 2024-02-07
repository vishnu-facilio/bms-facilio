<template>
  <el-popover
    placement="bottom"
    width="240"
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
        <div v-else @click="saveColor(color)" :key="`dropdown-${index}`">
          <InlineSvg
            src="tick-sign"
            class="color-shade"
            :style="{ 'background-color': `${color}` }"
            iconClass="icon icon-xxs margin-auto"
            iconStyle="fill: #fff"
          ></InlineSvg>
        </div>
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
const COLOR_HASH = [
  '#000',
  '#E60001',
  '#FF9900',
  '#FFFF00',
  '#008A00',
  '#0065CC',
  '#9833FF',
  '#FFFFFF',
  '#FACCCC',
  '#FFEBCB',
  '#FFFFCC',
  '#CBE8CC',
  '#CCDFF5',
  '#EAD6FF',
  '#BBBBBB',
  '#EF6565',
  '#FFC166',
  '#FFFF65',
  '#65B966',
  '#66A2E0',
  '#C284FF',
  '#888888',
  '#A00000',
  '#B26A01',
  '#B2B200',
  '#016000',
  '#0047B1',
  '#6B24B1',
]

export default {
  props: ['selectedColor', 'additionalColors', 'customColorPalette'],
  data() {
    return {
      visibility: false,
      currentColor: null,
    }
  },
  created() {
    this.currentColor = this.selectedColor
  },
  computed: {
    colorPalette() {
      let { additionalColors = [], customColorPalette } = this || {}
      if (customColorPalette) {
        return customColorPalette
      } else {
        return [...additionalColors, ...COLOR_HASH]
      }
    },
  },
  methods: {
    saveColor(color) {
      this.currentColor = color
      this.$emit('currentColor', {
        color,
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
    flex-direction: row;
    height: 120px;
    align-items: center;
    justify-content: center;
  }
  .color-shade {
    height: 25px;
    width: 25px;
    display: flex;
    margin: 0 5px 5px 0;
    justify-content: flex-end;
  }
}
</style>
