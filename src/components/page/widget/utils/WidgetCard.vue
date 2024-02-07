<template>
  <el-card
    shadow="never"
    :class="['page-widget', hideBoxShadow && 'transparent-bg']"
    :body-style="cardBodyStyle"
  >
    <slot></slot>
  </el-card>
</template>
<script>
export default {
  props: ['hideTitleSection', 'needsFooter', 'hideBg', 'isSidebarView'],
  data() {
    return {
      cardBodyStyle: {
        padding: '0px',
        textAlign: 'left',
        height: '100%',
      },
    }
  },
  computed: {
    hideBoxShadow() {
      return this.isSidebarView || this.hideBg
    },
  },
}
</script>
