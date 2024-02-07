<template>
  <div class="stepper-container">
    <div class="stepper-title">
      <slot name="title"></slot>
    </div>
    <div class="stepper-tabs">
      <div class="stepper-tabs-list">
        <ul v-for="(step, index) in steps" :key="step.title">
          <li
            :class="[
              'tab-item f13 letter-spacing0_5',
              isEdit && !disableGoto && 'pointer',
            ]"
            @click="goToStep(index)"
          >
            <div
              :class="[
                'form-dot-align',
                isActiveStep(step) ? 'dot-active-pink' : 'dot-inactive-grey',
              ]"
            ></div>
            {{ step.title }}
          </li>
        </ul>
      </div>
      <div class="stepper-tabs-content">
        <keep-alive v-if="keepAliveData">
          <component
            :is="activeStep.component"
            :sharedData="sharedData"
            @nextStep="nextStep"
            @goToPreviousStep="goToPreviousStep"
            @generateFinalSharedData="generateFinalSharedData"
          ></component>
        </keep-alive>
        <component
          v-else
          :is="activeStep.component"
          :sharedData="sharedData"
          @nextStep="nextStep"
          @goToPreviousStep="goToPreviousStep"
          @generateFinalSharedData="generateFinalSharedData"
        ></component>
      </div>
    </div>
  </div>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
export default {
  props: {
    steps: {
      type: Array,
      required: true,
    },
    initialStep: {
      type: Object,
      required: true,
    },
    keepAliveData: {
      type: Boolean,
    },
    isEdit: {
      type: Boolean,
    },
    disableGoto: {
      type: Boolean,
    },
  },
  data() {
    return {
      activeStep: this.initialStep,
      previousStep: null,
      sharedData: {},
    }
  },
  computed: {
    activeStepIndex() {
      let { steps, activeStep } = this
      return steps.findIndex(step => step.title === activeStep.title)
    },
  },
  methods: {
    isActiveStep(step) {
      let { activeStep } = this
      return activeStep.title === step.title
    },
    nextStep(formModel) {
      let { steps, sharedData, activeStepIndex } = this
      this.previousStep =
        activeStepIndex > 0 ? steps[activeStepIndex - 1] : null
      this.activeStep = steps[activeStepIndex + 1]
      this.sharedData = Object.assign(sharedData, formModel)
    },
    goToPreviousStep(formModel) {
      let { steps, activeStepIndex, sharedData } = this
      this.activeStep = steps[activeStepIndex - 1]
      this.previousStep =
        activeStepIndex > 0 ? steps[activeStepIndex - 1] : null
      this.sharedData = Object.assign(sharedData, formModel)
    },
    goToStep(index) {
      let { isEdit, activeStepIndex, steps, disableGoto } = this
      if (isEdit && !disableGoto) {
        activeStepIndex = index
        this.activeStep = steps[activeStepIndex]
      }
    },
    generateFinalSharedData(formModel) {
      let { sharedData } = this
      let { saveGoalType } = formModel
      this.sharedData = Object.assign(sharedData, formModel)
      // TODO: Have to remove this once rules feature is done
      if (!isEmpty(saveGoalType)) {
        if (saveGoalType === 0) {
          delete sharedData.saveGoal
        } else {
          delete sharedData.saveGoalFormulaField
        }
        delete sharedData.saveGoalType
      }
      this.$emit('finalStep', sharedData)
    },
  },
}
</script>

<style lang="scss">
.stepper-container {
  height: 100vh;
  .stepper-title {
    margin: 0 0 0 21%;
  }
  .stepper-tabs {
    margin: 10px;
    display: flex;
    .stepper-tabs-list {
      margin: 20px 30px 0px 0px;
      ul {
        list-style-type: none;
        padding: 0;
        .tab-item {
          display: flex;
          color: #324056;
        }
      }
    }
    .stepper-tabs-content {
      width: 75%;
      background-color: #fff;
    }
  }
}
</style>
