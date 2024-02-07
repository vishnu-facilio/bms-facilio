<template>
  <!-- step components emit next(data), sharedData prop is accessible across steps -->
  <div class="fc-kiosk-form">
    <div class="fc-kiosk-form-header fc-scale-in-center">
      <div class="fc-kiosk-form-header-icon" @click="goToPreviousStep({})">
        <img src="~assets/kiosk-back-arrow.svg" width="16" height="16" />
      </div>
      <div class="fc-kiosk-f18">{{ currentAccount.org.name }}</div>
      <portal-target
        name="kiosk-top-right"
        class="kiosk-top-right"
      ></portal-target>
    </div>
    <div class="full-page-stepper-content">
      <keep-alive v-if="keepAliveData" class="fc-animated slideInLeft">
        <component
          :key="activeStep.title"
          :is="activeStep.component"
          v-bind:sharedData="sharedData"
          v-bind="activeStep.props"
          @nextStep="nextStep"
          @goToPreviousStep="goToPreviousStep"
          @generateFinalSharedData="generateFinalSharedData"
        ></component>
      </keep-alive>
      <component
        v-else
        :key="activeStep.title"
        :is="activeStep.component"
        v-bind:sharedData="sharedData"
        v-bind="activeStep.props"
        @nextStep="nextStep"
        @goToPreviousStep="goToPreviousStep"
        @generateFinalSharedData="generateFinalSharedData"
      ></component>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  props: {
    initialShared: {
      type: Object,
    },
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
      default: true,
    },
  },
  created() {
    this.sharedData = { ...this.sharedData, ...this.initialShared }
  },
  data() {
    return {
      activeStep: this.initialStep,
      previousStep: null,
      sharedData: {},
    }
  },
  computed: {
    ...mapGetters(['currentAccount']),
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
      if (activeStepIndex + 1 == steps.length) {
        this.generateFinalSharedData(formModel)

        return
      }

      this.previousStep =
        activeStepIndex > 0 ? steps[activeStepIndex - 1] : null

      this.activeStep = steps[activeStepIndex + 1]
      this.sharedData = Object.assign(sharedData, formModel)
    },
    goToPreviousStep(formModel) {
      let { steps, activeStepIndex, sharedData } = this
      if (activeStepIndex == 0) {
        //exit the stepper
        this.$emit('exit', sharedData)
        return
      }
      this.activeStep = steps[activeStepIndex - 1]
      this.previousStep =
        activeStepIndex > 0 ? steps[activeStepIndex - 1] : null
      this.sharedData = Object.assign(sharedData, formModel)
    },
    generateFinalSharedData(formModel) {
      let { sharedData } = this
      this.sharedData = Object.assign(sharedData, formModel)
      //   // TODO: Have to remove this once rules feature is done
      this.$emit('finalStep', sharedData)
    },
  },
}
</script>
