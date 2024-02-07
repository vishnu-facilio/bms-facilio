<template>
  <div class="height100 pL50">
    <router-view></router-view>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'

export default {
  data() {
    return {
      newproduct: {
        code: 'home',
        label: this.$t('common.header.spacemanagement'),
        path: '/app',
        modules: [
          {
            label: this.$t('common.header.dashboard'),
            path: { path: '/app/home/dashboard' },
            icon: '',
            permission: 'dashboard:READ',
          },
          {
            label: this.$t('common.header.portfolio'),
            path: { path: '/app/home/portfolio' },
            icon: '',
            permission: 'space:READ',
          },
          {
            label: this.$t('common.header.reservations'),
            path: { path: '/app/home/reservation' },
            icon: '',
            license: 'RESOURCE_BOOKING',
          },
        ],
      },
      etisalat: {
        code: 'home',
        label: this.$t('common.header.spacemanagement'),
        path: '/app',
        modules: [
          {
            label: this.$t('common.header.dashboard'),
            path: { path: '/app/home/dashboard' },
            icon: '',
            permission: 'dashboard:READ',
          },
          {
            label: 'Utility Accounts',
            path: { path: '/app/home/custom_utilityaccounts_1/all' },
            icon: 'fa fa-bar-chart-o',
          },
          {
            label: 'Meters',
            path: { path: '/app/home/assets' },
            icon: 'fa fa-bar-chart-o',
          },
        ],
      },
    }
  },
  mounted() {
    this.initProduct()
  },
  watch: {
    $route(from, to) {
      this.initProduct()
    },
  },
  methods: {
    initProduct() {
      let product = []

      if (this.$account.user.email === 'jasonsmith@sutherland.com') {
        this.newproduct.modules = this.newproduct.modules.filter(
          product => product.path.path !== '/app/home/reservation'
        )
        product = this.newproduct
      }
      if (this.$account.user.email === 'moro@facilio.com') {
        this.newproduct.modules = this.newproduct.modules.filter(
          m => !['Reservations'].includes(m.label)
        )
        product = this.newproduct
      } else if (this.$helpers.isEtisalat()) {
        product = this.etisalat
      } else {
        product = this.newproduct
      }

      this.$store.dispatch('switchProduct', product)

      if (this.$route.path === '/app/home') {
        if (isEmpty(product.modules)) {
          return
        } else if (this.$hasPermission(product.modules[0].permission)) {
          this.$router.push(product.modules[0].path)
        } else if (this.$hasPermission(product.modules[1].permission)) {
          this.$router.push(product.modules[1].path)
        } else {
          this.$router.push({ path: '/app/home/portfolio/sites' })
        }
      }
    },
  },
}
</script>
