<template>
  <div>
    <qriously :value="qrVal" :size="size || 200" :level="'H'" />
    <!-- {{name}} -->
  </div>
</template>
<script>
export default {
  computed: {
    qrVal() {
      return this.$route.query.code
    },
    size() {
      return this.$route.query.size
    },
  },
}
</script>
