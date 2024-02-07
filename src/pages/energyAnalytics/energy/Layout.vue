<template>
  <div class="layout container">
    <router-view
      @syncCount="callbackMethod"
      class=""
      ref="energylist"
    ></router-view>
  </div>
</template>
<script>
export default {
  data() {
    return {}
  },
  watch: {},
  components: {},
  mounted() {
    this.$store.dispatch('view/loadModuleMeta', 'workflowrule')
  },
  methods: {},
}
</script>
