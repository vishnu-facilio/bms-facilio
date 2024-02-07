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
        code: 'purchase',
        label: 'Purchase',
        path: '/app/purchase',
        modules: [
          {
            label: this.$t('common.products.purchase_request'),
            path: { name: 'purchaserequest' },
            icon: '',
            permission: 'purchaserequest:READ',
            license: 'PURCHASE',
          },
          {
            label: this.$t('common.products.rfq'),
            path: { name: 'requestForQuotation' },
            icon: '',
            permission: 'requestForQuotation:READ',
            license: 'REQUEST_FOR_QUOTATION',
          },
          {
            label: this.$t('common.products.vendor_quotes'),
            path: { name: 'vendorQuotes' },
            icon: '',
            permission: 'requestForQuotation:READ',
            license: 'REQUEST_FOR_QUOTATION',
          },
          {
            label: this.$t('common.products.purchase_order'),
            path: { name: 'purchaseorder' },
            icon: '',
            permission: 'purchaseorder:READ',
            license: 'PURCHASE',
          },

          {
            label: this.$t('common.products.receivables'),
            path: { name: 'receivable' },
            icon: '',
            permission: 'purchaseorder:READ',
            license: 'PURCHASE',
          },
          {
            label: this.$t('common._common.terms_conditions'),
            path: { name: 'tandclist' },
            icon: '',
            permission: 'termsandconditions:READ',
            license: 'PURCHASE',
          },
          {
            label: this.$t('common.header.reports'),
            path: { name: 'purchaseorderreports' },
            icon: '',
            permission: 'purchaserequest:READ',
            license: 'PURCHASE',
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
      if (this.$route.path === '/app/purchase') {
        for (let mod of this.product.modules) {
          if (this.$hasPermission(mod.permission)) {
            this.$router.replace({
              name: mod.path.name,
              params: { viewname: 'all' },
            })
            return
          }
        }
      }
    },
  },
}
</script>
