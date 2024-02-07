<template>
  <div
    class="dragabale-card height100"
    :style="{ backgroundColor: cardStyle.backgroundColor }"
  >
    <shimmer-loading v-if="isLoading" class="map-shimmer"></shimmer-loading>
    <div v-else class="cb-card-container d-flex flex-direction-column">
      <div
        class="card-header-block f15 bold"
        :style="{ color: cardStyle.primaryColor }"
      >
        {{ cardData.title || 'Reading Card' }}
      </div>
      <div class="p25 flex-grow d-flex flex-wrap justify-content-space">
        <div
          class="mR20 mB20"
          v-for="(value, index) in cardData.values"
          :key="index"
        >
          <div class="f14 pB5" :style="{ color: cardStyle.secondaryColor }">
            {{ (value && value.name) || '' }}
          </div>
          <div class="f35" :style="{ color: cardStyle.primaryColor }">
            {{ (value && value.value) || '--' }}
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
  components: { shimmerLoading },
}
</script>
<style scoped></style>
