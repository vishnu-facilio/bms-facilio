<template>
  <div>
    <div class="circle-view-wrapper">
      <div class="circle-view-pivot">
        <svg height="25" width="25" viewBox="0 0 20 20">
          <circle r="10" cx="10" cy="10" :fill="fillColor" opacity="0.1" />
          <circle
            r="5"
            cx="10"
            cy="10"
            fill="transparent"
            :stroke="fillColor"
            stroke-width="10"
            :stroke-dasharray="svgDashArray"
            transform="rotate(-90) translate(-20)"
          />
        </svg>
      </div>
      <div v-if="showValue" class="circle-value-container">
        {{ value }}
      </div>
    </div>
  </div>
</template>

<script>
import BaseView from './BaseView.vue'
export default {
  computed: {
    fillColor() {
      return this.visualConfig.fillColor ? this.visualConfig.fillColor : 'red'
    },
    svgDashArray() {
      return `${(this.absValueInPercentage * 31.4) / 100} 31.4`
    },
    showValue() {
      return this.visualConfig.showValue
    },
  },
  extends: BaseView,
  methods: {
    format() {
      return this.value
    },
  },
}
</script>

<style scoped>
.circle-view-wrapper {
  display: flex;
  justify-content: space-around;
  width: fit-content;
  align-items: stretch;
}
.circle-value-container {
  padding-left: 25px;
}
</style>
