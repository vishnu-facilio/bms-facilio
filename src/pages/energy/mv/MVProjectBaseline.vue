<template>
  <div>
    <MVExpressions
      :expressionTitle="expressionTitle"
      :expressionBtnLabel="expressionBtnLabel"
      :sectionTitle="sectionTitle"
      :isAdjustments="false"
      :sharedData="sharedData"
      @appendData="appendData"
    ></MVExpressions>
  </div>
</template>

<script>
import MVExpressions from 'pages/energy/mv/MVExpressions'

export default {
  props: {
    sharedData: {
      type: Object,
    },
  },
  components: {
    MVExpressions,
  },
  data() {
    return {
      expressionTitle: `${this.$t('mv.creation.define_baseline')}`,
      expressionBtnLabel: '',
      sectionTitle: `${this.$t('mv.creation.baseline_expression')}`,
    }
  },
  methods: {
    appendData({ expressionArr, proceed }) {
      let baseLineExp = {
        baselines: expressionArr,
      }
      if (proceed) {
        this.$emit('nextStep', baseLineExp)
      } else {
        this.$emit('goToPreviousStep', baseLineExp)
      }
    },
  },
}
</script>

<style></style>
