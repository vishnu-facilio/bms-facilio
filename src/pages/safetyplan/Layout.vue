<template>
  <div class="height100 pL50">
    <router-view></router-view>
  </div>
</template>
<script>
export default {
  data() {
    return {
      product: {
        code: 'sf',
        label: 'Safety Plan',
        path: '/app/sf',
        modules: [
          {
            label: 'Safety Plans',
            path: { name: 'safetyplan' },
            license: '',
          },
          {
            label: 'Hazards',
            path: { name: 'hazard' },
            license: '',
          },
          {
            label: 'Precautions',
            path: { name: 'precaution' },
            license: '',
          },
        ],
      },
    }
  },
  mounted() {
    this.initProduct()
  },
  watch: {
    $route() {
      this.initProduct()
    },
  },
  methods: {
    initProduct() {
      this.$store.dispatch('switchProduct', this.product)
      if (this.$route.path === '/app/sf') {
        this.$router.replace({ name: 'safetyplan' })
      }
    },
  },
}
</script>
