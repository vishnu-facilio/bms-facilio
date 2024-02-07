<template>
  <div v-if="!loading" class="height100 customize-scroll-hidden">
    <form
      :action="actionUrl"
      :method="samlResponse ? 'POST' : 'GET'"
      ref="formSubmit"
      :target="`iframeWidget-${widgetId}`"
    >
      <input type="hidden" name="origin" :value="origin" />
      <input type="hidden" name="capp_id" :value="instanceUid" />

      <template v-if="urlParams">
        <input
          v-for="(param, index) in urlParams"
          :key="index"
          type="hidden"
          :name="param.name"
          :value="param.value"
        />
      </template>

      <!-- SAML params -->
      <input
        v-if="samlResponse"
        type="hidden"
        name="SAMLResponse"
        :value="samlResponse"
      />
      <input v-if="relay" type="hidden" name="RelayState" :value="relay" />
    </form>
    <iframe
      ref="cappFrame"
      style="width: 100%; height: 100%; border: 0px;"
      class="fc-connected-app-iframe"
      :name="`iframeWidget-${widgetId}`"
      allow="geolocation *; microphone *;  camera *;"
    >
      The URL cannot be loaded in iframe.
    </iframe>
  </div>
</template>

<script>
import { API } from '@facilio/api'
export default {
  props: ['widgetId', 'recordId', 'context', 'handlers', 'exportMode'],
  data() {
    return {
      loading: true,
      origin: null,
      instanceUid: null,
      urlParams: null,
      actionUrl: null,
      samlResponse: null,
      relay: null,
    }
  },
  computed: {
    widgetKey() {
      let key = this.widgetId + ''
      if (this.recordId) {
        key = key + '_' + this.recordId
      }
      return key
    },
  },
  mounted() {
    this.loadConnectedAppWidget()
  },
  watch: {
    widgetKey: function() {
      this.loadConnectedAppWidget()
    },
  },
  beforeDestroy() {
    this.destroyCurrentInstance()
  },
  methods: {
    getActions() {
      return {
        fullscreen: () => {
          if (
            document.fullscreenEnabled ||
            document.webkitFullscreenEnabled ||
            document.mozFullScreenEnabled ||
            document.msFullscreenEnabled
          ) {
            if (this.$refs.cappFrame.requestFullscreen) {
              this.$refs.cappFrame.requestFullscreen()
            } else if (this.$refs.cappFrame.webkitRequestFullscreen) {
              this.$refs.cappFrame.webkitRequestFullscreen()
            } else if (this.$refs.cappFrame.mozRequestFullScreen) {
              this.$refs.cappFrame.mozRequestFullScreen()
            } else if (this.$refs.cappFrame.msRequestFullscreen) {
              this.$refs.cappFrame.msRequestFullscreen()
            }
            return true
          } else {
            return false
          }
        },
        trigger: (namespace, options) => {
          if (
            this.handlers &&
            this.handlers[namespace] &&
            typeof this.handlers[namespace] === 'function'
          ) {
            let returnValue = this.handlers[namespace](options)
            if (typeof returnValue !== 'undefined') {
              return returnValue
            } else {
              return true
            }
          } else {
            return false
          }
        },
      }
    },
    getWidgetLinkName(key) {
      if (key) {
        key = key + ''
        if (key.indexOf('_') > 0 && key.split('_').length === 2) {
          let cappLink = key.split('_')[0]
          let widgetLink = key.split('_')[1]

          if (cappLink.length && widgetLink.length) {
            return cappLink + '.' + widgetLink
          }
        }
      }
      return null
    },
    loadConnectedAppWidget() {
      this.destroyCurrentInstance()

      this.loading = true

      let url = '/v2/connectedApps/viewWidget?'
      if (this.getWidgetLinkName(this.widgetId)) {
        url += '&widgetLinkName=' + this.getWidgetLinkName(this.widgetId)
      } else {
        url += '&widgetId=' + this.widgetId
      }
      if (this.recordId) {
        url += '&recordId=' + this.recordId
      }
      API.get(url).then(response => {
        let { data } = response
        this.loading = false
        if (data.viewUrl) {
          this.samlResponse = data.samlResponse
          this.relay = data.relay
          this.actionUrl = this.samlResponse ? data.acsURL : data.viewUrl

          let connectedAppWidget = data.connectedAppWidget || {}

          this.$nextTick(() => {
            let frame = this.$refs.cappFrame.contentWindow
            let widgetOrigin =
              new URL(data.viewUrl).protocol + '//' + new URL(data.viewUrl).host
            this.instanceUid = this.$connectedAppEventChannel.attachWidget(
              connectedAppWidget,
              this.context,
              {
                frame: frame,
                origin: widgetOrigin,
              },
              this.getActions(),
              this.exportMode || false
            )
            this.origin = this.$connectedAppEventChannel.getOrigin()

            let urlParamsList = []
            let urlSearchParams = new URL(data.viewUrl).searchParams
            for (const [key, value] of urlSearchParams.entries()) {
              urlParamsList.push({
                name: key,
                value: value,
              })
            }
            this.urlParams = urlParamsList

            this.$nextTick(() => {
              this.$refs.formSubmit.submit()
            })
          })
        }
      })
    },
    destroyCurrentInstance() {
      if (this.instanceUid) {
        this.$connectedAppEventChannel.deattachWidget(this.instanceUid)
        this.instanceUid = null
      }
    },
    sendEvent(key, data) {
      if (this.instanceUid) {
        this.$connectedAppEventChannel.sendEventToInstance(
          this.instanceUid,
          key,
          data
        )
      }
    },
  },
}
</script>

<style>
.fc-connected-app-iframe body {
  overflow: hidden;
}
</style>
