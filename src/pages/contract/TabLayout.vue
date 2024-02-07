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
        code: 'ct',
        label: 'Contracts',
        path: '/app/ct',
        modules: [
          {
            label: this.$t('common.header.purchase_contract'),
            path: { path: '/app/ct/purchasecontracts' },
            icon: '',
            permission: 'contract:READ',
            license: 'CONTRACT',
          },
          {
            label: this.$t('common.header.labour_contract'),
            path: { path: '/app/ct/labourcontracts' },
            icon: '',
            permission: 'contract:READ',
            license: 'CONTRACT',
          },
          {
            label: this.$t('common.header.rental_lease_contract'),
            path: { path: '/app/ct/rentalleasecontracts' },
            icon: '',
            permission: 'contract:READ',
            license: 'CONTRACT',
          },
          {
            label: this.$t('common.header.warranty_contract'),
            path: { path: '/app/ct/warrantycontracts' },
            icon: '',
            permission: 'contract:READ',
            license: 'CONTRACT',
          },
          {
            label: this.$t('common.header.reports'),
            path: { path: '/app/ct/reports' },
            icon: 'fa fa-bar-chart-o',
            license: 'CONTRACT',
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
      if (this.$route.path === '/app/ct') {
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
