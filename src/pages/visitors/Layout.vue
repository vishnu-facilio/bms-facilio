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
        code: 'vi',
        label: 'Visitor',
        path: '/app/vi',
        modules: [
          {
            label: this.$t('common.header.visitors_logs'),
            path: { path: '/app/vi/visits' },
            license: '',
            permission: 'visitorlog:READ',
          },
          {
            label: this.$t('common.header.visitor_invites'),
            path: { path: '/app/vi/invites' },
            license: '',
            permission: 'invitevisitor:READ',
          },
          {
            label: this.$t('common.header.group_invite'),
            path: { path: '/app/vi/groupinvite' },
            license: 'GROUP_INVITES',
            permission: 'groupinvite:READ',
          },
          {
            label: this.$t('common.header.visitors'),
            path: { path: '/app/vi/visitor' },
            license: '',
            permission: 'visitor:READ',
          },
          {
            label: this.$t('common.header.watch_list'),
            path: { path: '/app/vi/watchlist' },
            license: '',
            permission: 'watchlist:READ',
          },
          {
            label: this.$t('common.header.reports'),
            path: { path: '/app/vi/reports' },
            icon: 'fa fa-bar-chart-o',
            license: '',
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
      if (this.$route.path === '/app/vi') {
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
