<template>
  <ConnectedAppViewWidget
    ref="connectedAppWidgetPdf"
    :widgetId="widgetKey"
    :recordId="recordId"
    :context="widgetContext"
    :exportMode="true"
  ></ConnectedAppViewWidget>
</template>
<script>
export default {
  data() {
    return {}
  },
  components: {
    ConnectedAppViewWidget: () =>
      import('pages/connectedapps/ConnectedAppViewWidget'),
  },
  mounted() {},
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
  methods: {},
}
</script>
