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
          title: `${this.$t('mv.summary.actual_energy')}`,
          value: '',
          duration: 'This month',
          className: 'energy',
          unit: 'kWh',
        },
        {
          title: `${this.$t('mv.summary.baseline_energy')}`,
          value: '',
          duration: 'This month',
          className: 'energy',
          unit: 'kWh',
        },
      ],
    }
  },
  created() {
    let { details, cardArr } = this
    let { returnValue = {}, frequencyLabel } = details
    let {
      thisMonthActualConsumption,
      thisMonthBaselineConsumption,
    } = returnValue
    this.frequencyLabel = frequencyLabel

    if (thisMonthActualConsumption) {
      thisMonthActualConsumption = thisMonthActualConsumption / 1000
      cardArr[0].unit = 'MWh'
    }

    if (thisMonthBaselineConsumption) {
      thisMonthBaselineConsumption = thisMonthBaselineConsumption / 1000
      cardArr[1].unit = 'MWh'
    }

    cardArr[0].value = this.$d3.format(',.2f')(
      Math.round(thisMonthActualConsumption)
    )
    cardArr[1].value = this.$d3.format(',.2f')(
      Math.round(thisMonthBaselineConsumption)
    )
  },
}
</script>

<style></style>
