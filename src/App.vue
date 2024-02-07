<template>
  <!-- dont remove q-app -->
  <div id="q-app">
    <router-view />
    <KBar />
  </div>
</template>

<script>
import KBar from './components/global/KBar'
import { eventBus } from './components/page/widget/utils/eventBus'
export default {
  created() {
    this.injectMSSpecificCss()
    this.eventEmit()
  },
  components: { KBar },
  methods: {
    eventEmit() {
      eventBus.$on('errorPopup', data => {
        if (data?.isVisible) {
          this.$message.error(data.message)
          return
        }
      })
    },
    injectMSSpecificCss() {
      /* HACK, please dont do this, include css normally and use with a classname instead
        TODO: Needs refactoring
      */
      if (
        window.navigator.userAgent.includes('windows') ||
        window.navigator.userAgent.includes('Windows')
      ) {
        let head = document.getElementsByTagName('head')[0]
        let link = document.createElement('link')
        link.type = 'text/css'
        link.rel = 'stylesheet'
        link.href = `${window.webpackPublicPath ||
          window.location.origin}/statics/ms-specific.css`

        head.appendChild(link)
      }
    },
  },
}
</script>

<style>
#nprogress .bar {
  z-index: 10000 !important;
}
#nprogress .spinner {
  z-index: 10000 !important;
}
</style>
