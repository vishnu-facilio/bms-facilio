<template>
  <router-view :floorId="floorId"></router-view>
</template>

<script>
export default {
  computed: {
    floorId() {
      let { params } = this.$route
      if (params) {
        return Number(params.floorId)
      }
      return null
    },
  },
}
</script>

<style></style>
