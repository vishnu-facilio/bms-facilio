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
        code: 'al',
        label: 'Bills & Alerts',
        path: '/app/al',
        modules: [
          {
            label: 'Bill Alerts',
            path: { path: '/app/al/custom_alert' },
            icon: 'fa fa-bar-chart-o',
          },
          {
            label: 'Bill Uploads',
            path: { path: '/app/al/apps/26' },
            icon: 'fa fa-bar-chart-o',
          },
          {
            label: 'Utility Bills',
            path: { path: '/app/al/custom_utilitybills' },
            icon: 'fa fa-bar-chart-o',
          },
          {
            label: 'Bill Templates',
            path: { path: '/app/al/custom_billtemplates' },
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
      if (this.$route.path === '/app/al') {
        this.$router.push({
          path: '/app/al/custom_alert/all',
        })
      }
    },
  },
}
</script>

<style></style>
