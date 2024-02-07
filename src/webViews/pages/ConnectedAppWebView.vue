<template>
  <ConnectedAppViewWidget
    class="connectedapp-webview"
    ref="connectedAppWidgetPdf"
    :widgetId="widgetKey"
    :recordId="recordId"
    :context="widgetContext"
    :exportMode="true"
  ></ConnectedAppViewWidget>
</template>
<script>
import Vue from 'vue'
import ConnectedAppEventChannel from 'pages/connectedapps/event-channel/ConnectedAppEventChannel'
export default {
  data() {
    return {}
  },
  components: {
    ConnectedAppViewWidget: () =>
      import('pages/connectedapps/ConnectedAppViewWidget'),
  },
  created() {
    this.appInit()
  },
  mounted() {
    let self = this
    window.widgetReference = this.$refs['connectedAppWidgetPdf']
    window.sendEventToConnectedApp = (eventKey, eventData) => {
      console.log(
        'callback from native',
        eventKey,
        eventData,
        self.$refs['connectedAppWidgetPdf']
      )
      if (self.$refs['connectedAppWidgetPdf']) {
        self.$refs['connectedAppWidgetPdf'].sendEvent(eventKey, eventData)
      }
    }
  },
  computed: {
    widgetKey() {
      return this.$route.params.appname + '_' + this.$route.params.widgetname
    },
    recordId() {
      return window.recordId || null
    },
    widgetContext() {
      return window.widgetContext || {}
    },
  },
  methods: {
    appInit() {
      // appInit
      let CONNECTED_APP_EVENT_CHANNEL = null
      CONNECTED_APP_EVENT_CHANNEL = new ConnectedAppEventChannel({
        user: null,
        org: null,
        app: null,
      })

      Vue.prototype.$connectedAppEventChannel =
        CONNECTED_APP_EVENT_CHANNEL || null
    },
  },
}
</script>
<style>
.connectedapp-webview {
  height: 100vh !important;
}
</style>
