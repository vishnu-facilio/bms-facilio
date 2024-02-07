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
        code: 'dl',
        label: 'Deliveries',
        path: '/app/dl',
        modules: [
          {
            label: this.$t('common.products.deliveries'),
            path: { path: '/app/dl/deliveries' },
          },
          {
            label: this.$t('common.products.deliveryArea'),
            path: { path: '/app/dl/deliveryArea' },
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
      if (this.$route.path === '/app/dl') {
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
