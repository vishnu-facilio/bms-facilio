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
        code: 'ps',
        label: 'Parking Stall',
        path: '/app/ps',
        modules: [
          {
            label: this.$t('common.products.parkingstall'),
            path: { path: '/app/ps/parkingstall' },
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
      if (this.$route.path === '/app/ps') {
        for (let mod of this.product.modules) {
          //   if (this.$hasPermission(mod.permission)) {
          this.$router.replace(mod.path)
          return
          //   }
        }
      }
    },
  },
}
</script>
