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
        code: 'ro',
        label: 'Rooms',
        path: '/app/ro',
        modules: [
          {
            label: this.$t('common.products.rooms'),
            path: { path: '/app/ro/rooms' },
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
      if (this.$route.path === '/app/ro') {
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
