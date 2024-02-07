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
        code: 'cl',
        label: 'Client',
        path: '/app/cl',
        modules: [
          {
            label: this.$t('common.products.clients'),
            path: { path: '/app/cl/client' },
            license: '',
          },
          {
            label: this.$t('common.header.clientContact'),
            path: { path: '/app/cl/clientcontact' },
            icon: '',
            license: 'PEOPLE_CONTACTS',
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
      if (this.$route.path === '/app/cl') {
        this.$router.push({ path: '/app/cl/client' })
      }
    },
  },
}
</script>
