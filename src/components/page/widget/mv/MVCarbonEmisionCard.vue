<template>
  <div>
    <MVCommonCard
      :cardArr="cardArr"
      :frequencyLabel="frequencyLabel"
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
          title: `${this.$t('mv.summary.carbon_emissions')}`,
          value: '',
          duration: 'This month',
          className: 'energy',
          unit: 'Tons',
        },
      ],
    }
  },
  created() {
    let { details, cardArr } = this
    let { returnValue = {}, frequencyLabel } = details
    this.frequencyLabel = frequencyLabel
    cardArr[0].value = this.$d3.format(',.2f')(
      Math.round(returnValue.carbonEmissionSaved) / 1000
    )
  },
}
</script>

<style></style>
