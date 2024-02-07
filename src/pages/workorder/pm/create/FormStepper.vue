<template>
  <div class="steps-container">
    <div
      v-for="(step, index) in steps"
      :key="`step-${index}`"
      :class="['step', isSelectedStepPrevious(index) ? 'step--active' : '']"
    >
      <div
        class="step-circle"
        :class="isSelectedStep(index) ? 'selected-step-circle' : ''"
        @click="redirectToStep(index, step)"
      >
        <inline-svg
          v-if="isSelectedStepPrevious(index)"
          src="tick-sign"
          iconClass="icon icon-xs"
        ></inline-svg>
      </div>
      <div
        class="step-title"
        :class="isSelectedStep(index) ? 'selected-step-title' : ''"
      >
        {{ step.displayName }}
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: ['steps', 'active'],
  methods: {
    isSelectedStep(index) {
      let { active, steps } = this || {}
      let currentSelectedIndex = (steps || []).findIndex(
        step => step.name === active
      )
      return index <= currentSelectedIndex
    },
    isSelectedStepPrevious(index) {
      let { active, steps } = this || {}
      let currentSelectedIndex = (steps || []).findIndex(
        step => step.name === active
      )
      return index < currentSelectedIndex
    },
    currentActiveStep(index) {
      let { active, steps } = this || {}
      let currentSelectedIndex = (steps || []).findIndex(
        step => step.name === active
      )
      return index === currentSelectedIndex
    },
    redirectToStep(index, step) {
      if (this.isSelectedStepPrevious(index)) {
        this.$emit('onStepClick', step)
      }
    },
  },
}
</script>

<style scoped lang="scss">
.steps-container {
  display: flex;
  width: 85%;
}
.step {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
  position: relative;
  &:not(:last-child) {
    &:before,
    &:after {
      display: block;
      position: absolute;
      top: 28%;
      left: 55%;
      height: 0.3rem;
      content: '';
      transform: translateY(-50%);
      will-change: width;
      z-index: 90;
    }
  }
  &:before {
    width: 90%;
    background-color: #d1d1d1;
  }

  &:after {
    width: 0%;
    background-color: #67c23a;
  }
  &--active {
    &:after {
      width: 90% !important;
      opacity: 1;
      transition: width 0.6s ease-in-out, opacity 0.6s ease-in-out;
    }
  }
}
.step-circle {
  width: 25px;
  height: 25px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 17.5px;
  background-color: #d1d1d1;
  color: #ffffff;
  z-index: 99;
  font-weight: bolder;
  cursor: pointer;
}
.step-title {
  font-size: 12px;
  margin-top: 5px;
  color: #ccc;
  font-weight: 400;
}
.selected-step-title {
  color: #25243e;
  transition: background-color 0.8s ease-in-out;
}
.selected-step-circle {
  background-color: #67c23a;
  transition: background-color 0.8s ease-in-out;
  cursor: pointer;
  &:hover {
    box-shadow: 0 0 1px rgb(67 90 111 / 30%),
      0 5px 8px -4px rgb(67 90 111 / 47%);
  }
}
</style>
