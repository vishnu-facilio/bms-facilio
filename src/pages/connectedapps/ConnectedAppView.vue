<template>
  <connected-app-view-widget
    :ref="`ref-${widgetid}`"
    :widgetId="widgetid"
    :handlers="handlers"
  ></connected-app-view-widget>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
export default {
  props: ['widgetid'],
  components: {
    ConnectedAppViewWidget: () =>
      import('pages/connectedapps/ConnectedAppViewWidget'),
  },
  data() {
    return {
      handlers: {},
    }
  },
  created() {
    this.initWidgetHandlers()
  },
  methods: {
    initWidgetHandlers() {
      // live data subscribe
      let subscribe = ({ topic }) => {
        if (topic) {
          this.$wms.subscribe(topic, this.onLiveData)
        }
      }
      let unsubscribe = ({ topic }) => {
        if (topic) {
          this.$wms.unsubscribe(topic, this.onLiveData)
        }
      }

      this.handlers = {
        subscribe,
        unsubscribe,
      }
    },
    onLiveData(eventData) {
      this.sendEvent('live.data.received', eventData)
    },
    sendEvent(eventName, eventData) {
      let id = this.widgetid
      let refElement = this.$refs[`ref-${id}`]
      if (!isEmpty(refElement)) {
        refElement.sendEvent(eventName, eventData)
      }
    },
  },
}
</script>
