<template>
  <f-mini-chart :type="chartType" :data="chartData"></f-mini-chart>
</template>

<script>
import FMiniChart from './components/FMiniChart'
export default {
  components: { FMiniChart },
  computed: {
    chartType() {
      if (this.$route.query.type) {
        return this.$route.query.type
      }
      return null
    },
    chartData() {
      if (this.$route.query.data) {
        return JSON.parse(this.$route.query.data)
      }
      return null
    },
  },
}
</script>
