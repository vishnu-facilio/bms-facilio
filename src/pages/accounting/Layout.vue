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
            label: this.$t('common.products.budget'),
            path: { path: '/app/ac/budget' },
            permission: 'budget:READ',
          },
          {
            label: this.$t('tenant.accounting.chart_of_account'),
            path: { path: '/app/ac/chartofaccount' },
            permission: 'budget:READ',
          },
          {
            label: this.$t('tenant.accounting.account_type'),
            path: { path: '/app/ac/accounttype' },
            permission: 'budget:READ',
          },
          {
            label: this.$t('common.header.reports'),
            path: { name: 'budgetreports' },
            permission: 'budget:READ',
            icon: '',
            license: 'BUDGET_MONITORING',
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
      if (this.$route.path === '/app/ac') {
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
