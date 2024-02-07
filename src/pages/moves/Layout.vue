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
        code: 'mv',
        label: 'Moves',
        path: '/app/mv',
        modules: [
          {
            label: this.$t('common.products.moves'),
            path: { path: '/app/mv/moves' },
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
      if (this.$route.path === '/app/mv') {
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
