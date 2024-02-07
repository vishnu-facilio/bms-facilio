<template>
  <connected-app-view-widget
    style="height: 800px;"
    :widgetId="config.widgetId"
    :recordId="config.recordId"
    :context="details"
  ></connected-app-view-widget>
</template>
<script>
export default {
  props: ['tab', 'moduleName', 'details', 'layoutParams', 'fields', 'widget'],
  components: {
    ConnectedAppViewWidget: () =>
      import('pages/connectedapps/ConnectedAppViewWidget'),
  },
  created() {},
  computed: {
    config() {
      return {
        widgetId: this.tab ? this.tab.name : this.widget.widgetParams.widgetId,
        recordId: this.details.id,
      }
    },
  },
}
</script>
