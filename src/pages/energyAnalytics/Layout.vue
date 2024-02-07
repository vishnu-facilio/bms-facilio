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
        code: 'en',
        label: 'Energy Analytics',
        path: '/app/en',
        modules: [
          {
            label: this.$t('common.header.energy_meter'),
            path: { path: '/app/en/energymeter' },
            icon: 'fa fa-bar-chart-o',
            permission: 'asset:READ',
            license: 'ENERGY_STAR_INTEG',
          },
          {
            label: this.$t('common.header.m&v'),
            path: { path: '/app/en/mv' },
            icon: 'fa fa-bar-chart-o',
            license: 'M_AND_V',
          },
          {
            label: this.$t('common.header.energyStar'),
            path: { path: '/app/en/energy' },
            icon: 'fa fa-bar-chart-o',
            license: 'ENERGY_STAR_INTEG',
          },
        ],
      },
    }
  },
  mounted() {
    this.initProduct()
  },
  watch: {
    $route(from, to) {
      this.initProduct()
    },
  },
  methods: {
    initProduct() {
      // TODO get the custom modules from server for assets and show the list
      console.log('initProduct')
      this.$store.dispatch('switchProduct', this.product)
      if (this.$route.path === '/app/en') {
        if (this.$helpers.isLicenseEnabled('ENERGY_STAR_INTEG')) {
          this.$router.replace({ path: '/app/en/energymeter' })
        }
      }
    },
  },
}
</script>
