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
        code: 'survey',
        label: 'Survey',
        path: '/app/survey',
        modules: [
          {
            label: 'Survey Template',
            path: {
              path: '/app/survey/template',
            },
            icon: '',
          },
          {
            label: 'Survey',
            path: {
              path: '/app/survey/individual',
            },
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
      if (this.$route.path === '/app/survey') {
        this.$router.replace({
          name: 'surveyTemplateList',
          params: { viewname: 'all' },
        })
      }
    },
  },
}
</script>

<style></style>
