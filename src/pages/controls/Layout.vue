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
        code: 'co',
        label: this.$t('common.products.controls'),
        path: '/app/co',
        modules: [
          {
            label: this.$t('common.header.graphics'),
            path: { path: '/app/co/graphics' },
            icon: '',
            license: 'GRAPHICS',
          },
          {
            label: this.$t('common.header.control_group'),
            path: { name: 'group-list' },
            icon: '',
            license: 'CONTROL_ACTIONS',
          },
          {
            label: this.$t('common.header.controlpoints'),
            path: { path: '/app/co/cp/controlpoints' },
            icon: '',
            license: 'CONTROL_ACTIONS',
          },
          {
            label: this.$t('common.header.control_schedule'),
            path: { name: 'schedule-list' },
            icon: '',
            license: 'CONTROL_ACTIONS',
          },
          {
            label: this.$t('common.header.Commands'),
            path: { path: '/app/co/cc/commands' },
            icon: '',
            license: 'CONTROL_ACTIONS',
          },
          // Remove route and components in June 2021
          // {
          //   label: this.$t('common.header.controlLogic'),
          //   path: { path: '/app/co/cl/controllogic' },
          //   icon: '',
          //   license: 'CONTROL_ACTIONS',
          // },
        ],
      },
    }
  },
  created() {
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
      if (this.$route.path === '/app/co') {
        if (this.$helpers.isLicenseEnabled('GRAPHICS')) {
          this.$router.replace({ path: '/app/co/graphics' })
        } else if (this.$helpers.isLicenseEnabled('CONTROL_ACTIONS')) {
          this.$router.replace({ name: 'group-list' })
        }
      }
    },
  },
}
</script>
