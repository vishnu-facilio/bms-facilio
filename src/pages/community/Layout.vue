<template>
  <div class="height100 pL50">
    <router-view></router-view>
  </div>
</template>
<script>
export default {
  computed: {
    product() {
      let product = {
        code: 'cy',
        label: 'Community',
        path: '/app/cy',
        modules: [
          {
            label: this.$t('common.products.announcements'),
            path: { path: '/app/cy/announcements' },
            permission: 'announcement:READ',
          },
          {
            label: this.$t('common.products.neighbourhood'),
            path: { path: '/app/cy/neighbourhood' },
            permission: 'neighbourhood:READ',
          },
          {
            label: this.$t('common.products.deals'),
            path: { path: '/app/cy/deals' },
            permission: 'dealsandoffers:READ',
          },
          {
            label: this.$t('common.products.news'),
            path: { path: '/app/cy/news' },
            permission: 'newsandinformation:READ',
          },
          {
            label: this.$t('common.products.audience'),
            path: { path: '/app/cy/audience' },
            permission: 'audience:READ',
          },
        ],
      }
      return product
    },
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
      if (this.$route.path === '/app/cy') {
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
