<template>
  <div class="newsfields-container">
    <div class="record-id bold">#{{ details.id }}</div>
    <div class="f16 bold mT5 mB20">
      {{ details.title }}
    </div>
    <div class="rich-text-preview">
      <div
        v-html="sanitize(details[fieldKey])"
        ref="description-container"
      ></div>
    </div>
  </div>
</template>
<script>
import { sanitize } from '@facilio/utils/sanitize'
export default {
  name: 'NewsFields',
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
.newsfields-container {
  display: flex;
  flex-direction: column;
  padding: 25px 30px 15px;
}
.record-id {
  font-size: 14px;
  color: #39b2c2;
}
.rich-text-preview {
  display: flex;
  flex-direction: column;
  white-space: pre-line;
  word-break: break-word;
}
</style>
