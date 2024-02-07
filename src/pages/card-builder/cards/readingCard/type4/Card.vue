<template>
  <div class="dragabale-card height100">
    <shimmer-loading v-if="isLoading" class="map-shimmer"></shimmer-loading>
    <div
      v-else
      class="cb-card-container d-flex flex-direction-column"
      :style="{ backgroundColor: cardStyle.backgroundColor }"
    >
      <div
        class="card-header-block f15 bold d-flex"
        :style="{ color: cardStyle.primaryColor }"
      >
        {{ cardData.title || 'Reading Card' }}
        <span
          class="f12 mL-auto mR5"
          v-if="cardData.period"
          :style="{ color: cardStyle.secondaryColor }"
        >
          {{ cardData.period }}
        </span>
      </div>
      <div class="height100 d-flex flex-wrap mL25 mR25 justify-content-space">
        <div
          class="self-center p20 pL0"
          v-for="(value, index) in cardData.values"
          :style="{ color: cardStyle.primaryColor }"
          :key="index"
          @click="triggerAction()"
        >
          <div class="f35" :style="{ color: cardStyle.primaryColor }">
            {{ (value && formatDecimal(value.value)) || '--' }}
            <span class="f30 bold">{{ value && value.unit }}</span>
          </div>
          <div class="f12 bold" :style="{ color: cardStyle.secondaryColor }">
            {{ getAggr(value.aggregation) }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import shimmerLoading from '@/ShimmerLoading'
import Card from '../base/Card'
import { aggregateFunctions } from 'pages/card-builder/card-constants'

export default {
  extends: Card,
  components: { shimmerLoading },
  methods: {
    getAggr(aggr) {
      if (!aggr) return ''
      let aggrObj = aggregateFunctions.find(({ value }) => value === aggr)
      return aggrObj ? aggrObj.label : ''
    },
  },
}
</script>
<style scoped></style>
