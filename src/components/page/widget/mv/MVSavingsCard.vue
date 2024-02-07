<template>
  <div>
    <MVCommonCard
      :cardArr="cardArr"
      :frequencyLabel="frequencyLabel"
      :evenlySpaced="true"
    ></MVCommonCard>
  </div>
</template>

<script>
import MVCommonCard from './MVCommonCard'

export default {
  components: {
    MVCommonCard,
  },
  props: ['details'],
  data() {
    return {
      cardArr: [
        {
          title: `${this.$t('mv.summary.savings')}`,
          value: '',
          className: 'saving-amount',
          isCurrenyCard: true,
        },
        {
          title: `${this.$t('mv.summary.target_savings')}`,
          value: '',
          className: 'target-saving-amount',
          isCurrenyCard: true,
        },
      ],
    }
  },
  created() {
    let { details, cardArr } = this
    let { returnValue = {}, frequencyLabel } = details
    this.frequencyLabel = frequencyLabel
    cardArr[0].value = Math.round(returnValue.costSaved)
    cardArr[1].value = Math.round(returnValue.targetCost)
  },
}
</script>

<style></style>
