<template>
  <div v-bind:class="['dot-10 mT3', activityClass]"></div>
</template>
<script>
export default {
  props: ['state', 'status'],
  computed: {
    activityClass: function() {
      return {
        'color-active': !!this.state && this.status,
        'color-warning': !!this.state && !this.status,
        'color-inactive': !this.state,
      }
    },
  },
}
</script>
