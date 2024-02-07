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
        code: 'tm',
        label: 'Tenants',
        path: '/app/tm',
        modules: [
          {
            label: this.$t('common.products.tenants'),
            path: { path: '/app/tm/tenants' },
            license: '',
            permission: 'tenant:READ',
          },
          {
            label: this.$t('common.header.tenantContact'),
            path: { path: '/app/tm/tenantcontact' },
            license: 'PEOPLE_CONTACTS',
            permission: 'tenantcontact:READ',
          },

          {
            label: this.$t('common.header.tenantUnit'),
            path: { path: '/app/tm/tenantunit' },
            license: '',
            permission: 'tenantunit:READ',
          },
          {
            label: this.$t('common.products.quotation'),
            path: { path: '/app/tm/quotation' },
            license: 'QUOTATION',
            permission: 'quote:READ',
          },
          {
            label: this.$t('common.header.reports'),
            path: { path: '/app/tm/reports' },
            icon: 'fa fa-bar-chart-o',
            license: 'TENANTS',
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
      if (this.$route.path === '/app/tm') {
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
