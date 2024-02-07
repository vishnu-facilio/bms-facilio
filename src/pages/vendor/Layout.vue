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
        code: 'vendor',
        label: 'Vendor',
        path: '/app/vendor',
        modules: [
          {
            label: 'Vendor',
            path: { name: 'vendorList' },
            icon: '',
            permission: 'vendors:READ',
            license: 'VENDOR,INVENTORY,CONTRACT',
          },
          {
            label: 'Vendor Contacts',
            path: {
              name: 'vendorContactsList',
            },
            icon: '',
            permission: this.getVendorContactPermission(),
            license: 'VENDOR',
          },
          {
            label: 'Insurance',
            path: { name: 'insurancesList' },
            icon: '',
            permission: 'vendors:READ',
            license: 'VENDOR',
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
      if (this.$route.path === '/app/vendor') {
        this.$router.replace({
          name: 'vendorList',
        })
      }
    },
    getVendorContactPermission() {
      if (this.$hasPermission('vendorcontact:READ')) {
        return 'vendorcontact:READ'
      } else {
        return 'vendorContact:READ'
      }
    },
  },
}
</script>
