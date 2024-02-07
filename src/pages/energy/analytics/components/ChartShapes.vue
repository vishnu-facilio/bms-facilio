<template>
  <div>
    <!-- square -->
    <div
      v-if="activeShape === 'rectangle'"
      class="fc-chart-shape-rectangle"
      :style="color"
    ></div>
    <div
      v-if="activeShape === 'circle'"
      class="fc-chart-shape-circle"
      :style="color"
    ></div>
    <div
      v-if="activeShape === 'triangle'"
      class="fc-chart-shape-triangle"
      :style="color"
    ></div>
    <div
      v-if="activeShape === 'diamond'"
      class="fc-chart-shape-diamond"
      :style="color"
    ></div>
  </div>
</template>
<script>
export default {
  props: ['activeShape', 'activeColor'],
  computed: {
    color() {
      if (this.activeColor && this.activeShape === 'diamond') {
        return {
          'background-color': this.activeColor,
        }
      } else if (this.activeColor && this.activeShape === 'triangle') {
        return {
          'border-bottom-color': this.activeColor,
        }
      } else if (this.activeColor) {
        return {
          'background-color': this.activeColor,
        }
      } else {
        return null
      }
    },
  },
}
</script>
