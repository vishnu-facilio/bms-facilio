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
        code: 'induction',
        label: 'Induction',
        path: '/app/induction',
        modules: [
          {
            label: 'Induction Template',
            path: {
              path: '/app/induction/template',
            },
            permission: 'inductionTemplate:READ',
            icon: '',
          },
          {
            label: 'Induction',
            path: {
              path: '/app/induction/individual',
            },
            permission: 'inductionResponse:READ',
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
      if (this.$route.path === '/app/induction') {
        this.$router.replace({
          name: 'inductionTemplateList',
          params: { viewname: 'all' },
        })
      }
      if (this.$route.path === '/app/induction') {
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
