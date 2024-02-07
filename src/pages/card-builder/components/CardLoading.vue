<template>
  <div class="height100 width100 newcard-shimmer">
    <shimmer-loading class="map-shimmer height100"></shimmer-loading>
  </div>
</template>

<script>
import shimmerLoading from '@/ShimmerLoading'
export default {
  components: { shimmerLoading },
}
</script>
<style>
.newcard-shimmer .shimmer section {
  height: 100%;
}
.newcard-shimmer .sh-background.img {
  background-size: 100% 100%;
  height: 100%;
}
</style>
