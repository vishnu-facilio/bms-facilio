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
        code: 'sr',
        label: 'Service Request',
        path: '/app/sr',
        modules: [
          {
            label: this.$t('common.products.service_request'),
            path: { path: '/app/sr/serviceRequest' },
            license: 'SERVICE_REQUEST',
            permission: 'serviceRequest:READ',
          },
          {
            label: 'Contact Directory',
            path: { path: '/app/sr/contactdir' },
            license: 'COMMUNITY',
            permission: 'contactdirectory:READ',
          },
          {
            label: 'Documents',
            path: { path: '/app/sr/admindocs' },
            license: 'COMMUNITY',
            permission: 'admindocuments:READ',
          },
          {
            label: this.$t('common.header.reports'),
            path: { path: '/app/sr/reports' },
            license: 'SERVICE_REQUEST',
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
      if (this.$route.path === '/app/sr') {
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
