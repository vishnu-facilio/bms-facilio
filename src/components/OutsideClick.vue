<template>
  <div v-if="visibility" v-click-outside="onOutsideClick">
    <slot></slot>
  </div>
</template>
<script>
export default {
  props: ['visibility'],
  methods: {
    onOutsideClick() {
      this.$emit('onOutsideClick')
      this.$emit('update:visibility', false)
    },
  },
}
</script>
