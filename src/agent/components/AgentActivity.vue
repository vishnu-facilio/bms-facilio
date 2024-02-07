<template>
  <div v-bind:class="['dot-10 mT3', activityClass]"></div>
</template>
<script>
export default {
  props: ['status'],
  computed: {
    activityClass: function() {
      return {
        'color-active': this.status,
        'color-inactive': !this.status,
      }
    },
  },
}
</script>
