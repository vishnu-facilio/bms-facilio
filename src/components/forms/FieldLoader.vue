<template>
  <div class="field-loading loading-shimmer" v-if="isLoading"></div>
</template>
<script>
export default {
  props: {
    isLoading: {
      type: Boolean,
      required: true,
    },
  },
}
</script>
<style lang="scss">
.field-loading {
  height: 40px;
  display: flex;
  flex: 1;
  border-radius: 3px;
}
</style>
