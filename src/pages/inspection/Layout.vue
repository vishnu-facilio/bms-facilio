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
        code: 'inspection',
        label: 'Inspection',
        path: '/app/inspection',
        modules: [
          {
            label: 'Inspection Template',
            path: {
              path: '/app/inspection/template',
            },
            permission: 'inspectionTemplate:READ',
            icon: '',
          },
          {
            label: 'Inspection',
            path: {
              path: '/app/inspection/individual',
            },
            permission: 'inspectionResponse:READ',
            icon: '',
          },
          {
            label: 'Reports',
            path: {
              path: '/app/inspection/reports',
            },
            permission: 'inspectionTemplate:READ',
            icon: '',
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
      if (this.$route.path === '/app/inspection') {
        this.$router.replace({
          name: 'inspectionTemplateList',
          params: { viewname: 'all' },
        })
      }
      if (this.$route.path === '/app/inspection') {
        for (let mod of this.product.modules) {
          if (this.$hasPermission(mod.permission)) {
            this.$router.push(mod.path)
            return
          }
        }
      }
    },
  },
}
</script>

<style></style>
