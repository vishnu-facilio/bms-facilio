<template>
  <div class="height100 pL50">
    <router-view></router-view>
  </div>
</template>

<script>
import http from 'util/http'
import { isEmpty } from '@facilio/utils/validation'

export default {
  data() {
    return {
      etisalat: {
        code: 'supp',
        label: 'Suppliers',
        path: '/app/supp',
        modules: [
          {
            label: 'Suppliers',
            path: { path: '/app/supp/vendors/all' },
            icon: '',
          },
          {
            label: 'Payment memo',
            path: { path: '/app/supp/custom_invoices/all' },
            icon: 'fa fa-bar-chart-o',
          },
          {
            label: 'Tariff',
            path: { path: '/app/supp/custom_tariffinfo/all' },
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
      let { etisalat } = this
      this.$store.dispatch('switchProduct', etisalat)
      if (this.$route.path === '/app/supp') {
        this.$router.push({
          path: '/app/supp/vendors/all',
        })
      }
    },
  },
}
</script>

<style></style>
