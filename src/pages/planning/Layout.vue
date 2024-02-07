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
        code: 'planning',
        label: this.$t('common.products.planning'),
        path: '/app/planning',
        modules: [
          {
            label: this.$t('common.header.routes'),
            path: { name: 'routeList' },
            license: 'ROUTES_AND_MULTI_RESOURCE',
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
      let { product, $store, $route, $router } = this

      $store.dispatch('switchProduct', product)
      if ($route.path === '/app/planning') {
        $router.push({ name: 'routeList' })
      }
    },
  },
}
</script>
