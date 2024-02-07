<template>
  <div class="ecm-option-container">
    <div class="ecm-option f13 bold letter-spacing0_6 text-uppercase">
      {{ $t('mv.summary.ecm_option_measurement') }}
      <span class="mL15 mR15">-</span>
    </div>
    <div class="ecm-option f14">
      {{ getECMOptionLabel(mvProject.emcOptions) }}
    </div>
    <div class="mL-auto vertical-separator mR30"></div>
    <div
      class="ecm-option ecm-reporting-label f13 bold letter-spacing0_6 text-uppercase"
    >
      {{ $t('mv.summary.ecm_period') }}
      <span class="mL15 mR15">-</span>
    </div>
    <div class="ecm-option date f12 letter-spacing0_6">
      {{ mvProject.startTime | formatDate(true) }}
      <span class="mL15 mR15">-</span>
    </div>
    <div class="ecm-option date f13 letter-spacing0_6">
      {{ mvProject.endTime | formatDate(true) }}
    </div>
  </div>
</template>

<script>
import Constants from 'util/constant'

export default {
  props: ['details'],
  computed: {
    mvProject() {
      let { details } = this
      return details.mvProject
    },
  },
  methods: {
    getECMOptionLabel(value) {
      let ecmMeasurementOptions = Constants.ECM_MEASUREMENT_OPTIONS
      return ecmMeasurementOptions.find(option => option.value === value).label
    },
  },
}
</script>

<style lang="scss">
.ecm-option-container {
  padding: 10px 50px 10px 30px;
  flex-direction: row;
  .ecm-reporting-label {
    color: #4273e9;
  }
}
</style>
