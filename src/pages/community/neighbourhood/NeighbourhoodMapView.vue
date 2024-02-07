<template>
  <GmapMap
    :zoom="14"
    :center="center"
    :options="{
      zoomControl: false,
      mapTypeControl: false,
      scaleControl: false,
      streetViewControl: false,
      rotateControl: false,
      fullscreenControl: false,
      disableDefaultUI: true,
    }"
    map-type-id="terrain"
    class="width100 height100"
  >
    <GmapMarker
      :key="index"
      v-for="(m, index) in markers"
      :position="m.position"
      @click="center = m.position"
    />
  </GmapMap>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
export default {
  props: ['location'],
  data() {
    return {
      markers: [],
      center: {},
    }
  },
  mounted() {
    let { location } = this
    if (!isEmpty(location)) {
      let { lat, lng } = location
      this.markers.push({ position: { lat, lng } })
      this.center = { lat, lng }
    }
  },
}
</script>
