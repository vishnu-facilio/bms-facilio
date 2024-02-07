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
        code: 'ac',
        label: 'Budget',
        path: '/app/ac',
        modules: [
          {
            label: this.$t('common.products.facility'),
            path: { path: '/app/bk/facility' },
            permission: 'facility:READ',
          },
          {
            label: this.$t('common.products.amenity'),
            path: { path: '/app/bk/amenity' },
            permission: 'facility:READ',
          },
          {
            label: this.$t('common.products.facilitybooking'),
            path: { path: '/app/bk/facilitybooking' },
            permission: 'facilitybooking:READ',
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
      if (this.$route.path === '/app/bk') {
        for (let mod of this.product.modules) {
          if (this.$hasPermission(mod.permission)) {
            this.$router.replace(mod.path)
            return
          }
        }
      }
    },
  },
}
</script>
