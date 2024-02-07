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
          :key="index"
          :style="{ color: cardStyle.primaryColor }"
        >
          <div class="f12" :style="{ color: cardStyle.secondaryColor }">
            {{ (value && value.name) || '' }}
          </div>
          <div class="f35 bold" @click="triggerAction()">
            {{ (value && formatDecimal(value.value)) || '' }}
            <span class="f30 bold">{{ value && value.unit }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import shimmerLoading from '@/ShimmerLoading'
import Card from '../base/Card'

export default {
  extends: Card,
  components: {
    shimmerLoading,
  },
}
</script>

<style scoped></style>
