<template>
  <div>
    <component
      v-if="componentName"
      :is="componentName"
      :key="recordId"
      v-bind="$attrs"
    ></component>
  </div>
</template>
<script>
export default {
  components: {
    NewNotes: () => import('src/components/widget/NewNotes.vue'),
    OldNotes: () => import('src/components/widget/OldNotes.vue'),
  },
  computed: {
    componentName() {
      return this.$helpers.isLicenseEnabled('NEW_COMMENTS')
        ? 'NewNotes'
        : 'OldNotes'
    },
    recordId() {
      return this.$getProperty(this.$attrs, 'record.id')
    },
  },
}
</script>
