<template>
  <div class="width100 height-100">
    <iframe
      ref="iframe"
      :src="url"
      class="dispatcher-console-container"
      id="dispatcher-console"
    />
  </div>
</template>

<script>
export default {
  name: 'DispatcherConsoleBoard',
  computed: {
    url() {
      let { NODE_ENV, VUE_APP_DISPATCHER_URL } = process.env || {}

      if (NODE_ENV === 'development') {
        return `${VUE_APP_DISPATCHER_URL}`
      } else {
        return `/dispatcher`
      }
    },
  },
  // mounted() {
  //   // Synchronize the parent URL with the child iframe URL. Whenever there is a route change in the iframe,
  //   // the parent will be notified via a post message, and it will update its own URL accordingly.
  //   window.addEventListener('message', ev => {
  //     if (ev.origin === 'http://localhost:8080') {
  //       const appName = window.location.pathname.slice(1).split('/')[0]
  //       const path = `/${appName}/dispatcher${ev.data}`
  //       this.$router.push({ path })
  //     }
  //   })
  //   // When a user copies and pastes a URL into a new tab or reloads the page, we need to handle the iframe URL to match the current URL shown in the parent window's address bar. For example, if the parent window displays "app/dispatcher/console/9/gant/", the iframe should also be updated to match the same URL.
  //   const path = this.$route.path.replace('/app', '')
  //   this.iframeSrc = `http://localhost:8080${path}`
  // },
}
</script>

<style>
.dispatcher-console-container {
  width: 100%;
  height: 100%;
  border: none;
}
</style>
