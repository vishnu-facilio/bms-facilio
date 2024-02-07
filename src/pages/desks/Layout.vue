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
        code: 'de',
        label: 'Desks',
        path: '/app/de',
        modules: [
          {
            label: this.$t('common.products.desks'),
            path: { path: '/app/de/desks' },
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
      if (this.$route.path === '/app/de') {
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
