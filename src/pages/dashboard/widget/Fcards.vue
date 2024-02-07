<template>
  <component
    v-if="staticKey"
    :is="staticKey"
    :widget="config ? config.widget : null"
    class="f-cards"
    :currentDashboard="config ? config.currentDashboard : null"
  >
  </component>
</template>
<script>
import WeatherMiniCard from './cards/WeatherMiniCard'
import CarbonMiniCard from './cards/CarbonMini'
import ProfileMiniCard from './cards/Profilemini'
import EnergyMinicard from './cards/Energymini'
import Readingcard from './cards/Readingcard'

export default {
  props: ['config'],
  data() {
    return {}
  },
  components: {
    weathermini: WeatherMiniCard,
    carbonmini: CarbonMiniCard,
    energycostmini: EnergyMinicard,
    profilemini: ProfileMiniCard,
    readingcard: Readingcard,
  },
  computed: {
    staticKey() {
      if (this.config && this.config.widget.dataOptions) {
        return this.config.widget.dataOptions.staticKey
      }
      return null
    },
  },
}
</script>
<style>
/* .f-cards .vue-resizable-handle {
  display: none;
} */
</style>
