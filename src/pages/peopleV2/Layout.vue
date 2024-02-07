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
            label: 'Employee',
            path: { path: '/app/pl/employee' },
            icon: '',
            permission: 'people:READ',
            license: 'PEOPLE',
          },
          {
            label: 'Shift',
            path: { path: '/app/pl/shift' },
            icon: '',
            permission: 'people:READ',
            license: 'SHIFT',
          },
          {
            label: 'Shift Planner',
            path: { path: '/app/pl/shiftplanner' },
            icon: '',
            license: 'SHIFT',
          },
          {
            label: 'Break',
            path: { path: '/app/pl/break' },
            icon: '',
            license: 'ATTENDANCE',
          },
          {
            label: 'Attendance',
            path: { path: '/app/pl/attendance' },
            icon: '',
            license: 'ATTENDANCE',
          },
          {
            label: 'My Attendance',
            path: { path: '/app/pl/myattendance' },
            icon: '',
            license: 'ATTENDANCE',
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
