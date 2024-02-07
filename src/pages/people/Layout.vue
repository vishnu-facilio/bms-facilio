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
        code: 'pl',
        label: 'People',
        path: '/app/pl',
        modules: [
          {
            label: 'Employees',
            path: { path: '/app/pl/employee' },
            icon: '',
            permission: 'employee:READ',
            license: 'PEOPLE_CONTACTS',
          },
          {
            label: 'Attendance',
            path: { path: '/app/pl/attendance' },
            icon: '',
            permission: 'employee:READ',
            license: 'PEOPLE',
          },
          {
            label: 'Shift',
            path: { path: '/app/pl/sft' },
            icon: '',
            permission: 'employee:READ',
            license: 'PEOPLE',
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
      // TODO get the custom modules from server for assets and show the list
      this.$store.dispatch('switchProduct', this.product)
      if (this.$route.path === '/app/pl') {
        if (this.$helpers.isLicenseEnabled('PEOPLE_CONTACTS')) {
          this.$router.push({ path: '/app/pl/employee' })
        } else {
          this.$router.push({ path: '/app/pl/attendance' })
        }
      }
    },
  },
}
</script>
