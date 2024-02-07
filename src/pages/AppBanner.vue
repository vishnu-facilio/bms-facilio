<template>
  <portal to="app-banner">
    <div class="app-banner" :style="appBannerStyle">
      <slot></slot>
    </div>
  </portal>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
export default {
  props: ['backgroundColor', 'textColor'],
  computed: {
    appBannerStyle() {
      let { backgroundColor, textColor } = this
      let style = { background: '#ff9933', color: '#fff' }

      if (!isEmpty(backgroundColor)) {
        style.background = `${backgroundColor}`
      }
      if (!isEmpty(textColor)) {
        style.color = `${textColor}`
      }

      return style
    },
  },
}
</script>
<style lang="scss">
.app-banner {
  text-align: center;
  padding: 5px;
}
</style>
