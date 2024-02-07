<template>
  <div class="height100">
    <router-view :isOldKpi="isOldKpi"></router-view>
  </div>
</template>
<script>
import { eventBus } from 'src/components/page/widget/utils/eventBus.js'
export default {
  data() {
    return {
      isOldKpi: null,
    }
  },
  mounted() {
    eventBus.$on('oldkpi', () => {
      this.isOldKpi = true
    })
  },
}
</script>
