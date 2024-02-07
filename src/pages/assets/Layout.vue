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
        code: 'at',
        label: 'Asset Management',
        path: '/app/at',
        modules: [
          {
            label: this.$t('common.header.assets'),
            path: { path: '/app/at/assets' },
            icon: '',
            permission: 'asset:READ',
            license: 'SPACE_ASSET',
          },
          {
            label: this.$t('common.header.failure_class'),
            path: { path: '/app/at/failureclass' },
            icon: '',
            permission: 'asset:READ',
            license: 'FAILURE_CODES',
          },
          {
            label: this.$t('common.header.reports'),
            path: { path: '/app/at/reports' },
            icon: '',
            permission: 'asset:READ',
            license: 'SPACE_ASSET',
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
      if (!this.$helpers.isLicenseEnabled('PEOPLE_CONTACTS')) {
        {
          this.product.modules = this.product.modules.filter(
            pro => pro.path.path !== '/app/vendor/vendorcontact'
          )
        }
      }
      if (this.$route.path === '/app/at') {
        for (let mod of this.product.modules) {
          if (this.$hasPermission(mod.permission)) {
            this.$router.push(mod.path)
            return
          }
        }
      }
    },
  },
}
</script>
