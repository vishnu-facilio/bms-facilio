<template>
  <component :is="componentName" v-bind="$attrs" />
</template>

<script>
/* eslint-disable vue/no-unused-components */
export default {
  name: 'ModuleForm',
  components: {
    dsmForm: () => import('./DSMFormCreation.vue'),
    oldForm: () => import('./ModuleForm.vue'),
  },
  computed: {
    componentName() {
      return this.isLicenseEnabled ? 'dsmForm' : 'oldForm'
    },
    isLicenseEnabled() {
      return this.$helpers.isLicenseEnabled('FORM_DSM')
    },
  },
}
</script>
