<template>
  <div>
    <MVExpressions
      :expressionTitle="expressionTitle"
      :expressionBtnLabel="expressionBtnLabel"
      :sectionTitle="sectionTitle"
      :isAdjustments="true"
      :sharedData="sharedData"
      @appendData="appendData"
    ></MVExpressions>
  </div>
</template>

<script>
import MVExpressions from 'pages/energy/mv/MVExpressions'

export default {
  components: {
    MVExpressions,
  },
  props: {
    sharedData: {
      type: Object,
    },
  },
  data() {
    return {
      expressionTitle: 'Non-Routine Adjustments',
      expressionBtnLabel: 'Add Non-Routine Adjustments',
      sectionTitle: 'Non-Routine Adjustments',
    }
  },
  methods: {
    appendData({ expressionArr, proceed }) {
      let baseLineExp = {
        adjustments: expressionArr,
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
