<template>
  <div class="rich-text-preview">
    <div v-html="sanitize(details[fieldKey])" ref="description-container"></div>
  </div>
</template>
<script>
import { sanitize } from '@facilio/utils/sanitize'
export default {
  name: 'RichTextPreview',
  props: ['details', 'widget'],
  computed: {
    fieldKey() {
      return (
        this.$getProperty(
          this.widget,
          'widgetParams.fieldKey',
          'description'
        ) || 'description'
      )
    },
  },
  created() {
    this.sanitize = sanitize
  },
}
</script>
<style lang="scss" scoped>
.rich-text-preview {
  display: flex;
  flex-direction: column;
  padding: 25px 30px 15px;
  white-space: pre-line;
  word-break: break-word;
}
</style>
